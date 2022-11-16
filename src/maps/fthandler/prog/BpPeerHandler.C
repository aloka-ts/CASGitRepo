#include <Util/Logger.h>
LOG("LoadBalancer");

#include <iostream.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/uio.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <net/if.h>
#include <arpa/inet.h>
#include <inttypes.h>
#include <netdb.h>
#include <string.h>
#include <pthread.h>
#include <errno.h>
#include <unistd.h>
#include <stdlib.h>
#include <limits.h>
#include <fcntl.h>
#include <time.h>

#include "BpIPAddrHandler.h"
#include "BpFtHandler.h"
#include "BpPeerHandler.h"

const char *getRole = "getrole";
const char *roleCheck = "rolecheck";
const char *heartBeat = "heartbeat";

void *
_listener(void *args) {

	BpFtHandler::getInstance().setSignal();
	pthread_detach(pthread_self());

	BpPeerHandler *hdlr = (BpPeerHandler *)(args);
	hdlr->listener();

	return NULL;
}

void *
_receiver(void *args) {

	BpFtHandler::getInstance().setSignal();
	pthread_detach(pthread_self());

	BpPeerHandler *hdlr = (BpPeerHandler *)(args);
	hdlr->receiver();

	return NULL;
}

void *
_heartbeat(void *args) {

	BpFtHandler::getInstance().setSignal();
	pthread_detach(pthread_self());

	BpPeerHandler *hdlr = (BpPeerHandler *)(args);
	hdlr->heartbeat();

	return NULL;
}

void *
_peerConnection(void *args) {

	BpFtHandler::getInstance().setSignal();
	pthread_detach(pthread_self());

	BpPeerHandler *hdlr = (BpPeerHandler *)(args);
	hdlr->_connectWithPeer();

	return NULL;
}

void *
_roleResolution(void *args) {

	BpFtHandler::getInstance().setSignal();
	pthread_detach(pthread_self());

	BpPeerHandler *hdlr = (BpPeerHandler *)(args);
	hdlr->_resolveRole();

	return NULL;
}

void *
_initializer(void *args) {

	BpFtHandler::getInstance().setSignal();
	pthread_detach(pthread_self());

	BpPeerHandler *hdlr = (BpPeerHandler *)(args);
	hdlr->_init();

	return NULL;
}

int
BpPeerHandler::init()
{
	LogTrace(0, "Entering init");

	LogTrace(0, "Creating a thread for initialization");

	pthread_t tmpThread;
	if (-1 == pthread_create(&(tmpThread), NULL,
		_initializer, (void *)this)) {
		LogError(0, "Failed to create create connection thread");
		this->shutdown();
		//LogTrace(0, "Exiting");
		//exit(1);
		LogError(0, "Going to dead state");
		BpFtHandler::getInstance().
			updateState(BpFtHandler::DEAD);
		return -1;
	}

	LogTrace(0, "Leaving init");
	return 0;
}

int
BpPeerHandler::_init()
{
	LogTrace(0, "Entering _init");

	// Create a socket
	LogTrace(0, "Create a listener socket");
	
	int sock = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (sock < 0) {
		logger.logMsg(ERROR_FLAG, 0, "Socket creation failed. [%s]",
       	strerror(errno));
		//LogError(0, "Exiting");
		//exit(1);
      LogError(0, "Going to dead state");
      BpFtHandler::getInstance().
         updateState(BpFtHandler::DEAD);
      return -1;
	}

	LogTrace(0, "Setting socket option SO_REUSEADDR");
	const int optval = 1;
   if (setsockopt(sock, SOL_SOCKET, SO_REUSEADDR, &optval, 4) < 0 ) {
		logger.logMsg(ERROR_FLAG, 0, "Socket option setting failed. [%s]",
       	strerror(errno));
		this->shutdown();
		//LogError(0, "Exiting");
		//exit(1);
      LogError(0, "Going to dead state");
      BpFtHandler::getInstance().
         updateState(BpFtHandler::DEAD);
      return -1;
   }

	std::string ip = BpFtHandler::getInstance().getSelfIp();

	sockaddr_in myDetails;
	memset((void *)(&myDetails), 0, sizeof(sockaddr_in));
	myDetails.sin_family = AF_INET;
 	myDetails.sin_addr.s_addr = inet_addr(ip.c_str());
	myDetails.sin_port = htons(BpFtHandler::getInstance().getSelfPort());

	LogTrace(0, "Bind the listener socket");
	logger.logMsg(TRACE_FLAG, 0, "IP = [%s], Port = [%d]",
					  ip.c_str(), BpFtHandler::getInstance().getSelfPort());

	errno = 0;
	
	if (bind(sock, (struct sockaddr *)&myDetails, sizeof(sockaddr_in)) != 0) {
      logger.logMsg(ERROR_FLAG, 0, "Unable to bind to IP. [%s]",
			strerror(errno));
		this->shutdown();
      //LogError(0, "Exiting");
		//exit(1);
      LogError(0, "Going to dead state");
      BpFtHandler::getInstance().
         updateState(BpFtHandler::DEAD);
      return -1;
   }

	LogTrace(0, "Call listen on the listener socket");
	if (listen(sock, 5) == -1) {
      logger.logMsg(ERROR_FLAG, 0, "Call to listen failed. [%s]",
			strerror(errno));

		this->shutdown();
      //LogError(0, "Exiting");
		//exit(1);
      LogError(0, "Going to dead state");
      BpFtHandler::getInstance().
         updateState(BpFtHandler::DEAD);
      return -1;
   }

	this->listenerFd = sock;

	// launch the listener
	LogTrace(0, "Creating the listener thread");
	doListen = true;
	
	if (-1 == pthread_create(&(this->listenerThread), NULL,
		_listener, (void *)this)) {
		LogError(0, "Failed to create listener thread");

		this->shutdown();
		//LogTrace(0, "Exiting");
		//exit(1);
      LogError(0, "Going to dead state");
      BpFtHandler::getInstance().
         updateState(BpFtHandler::DEAD);
      return -1;
	}

	// Give the listener a chance to go active
	sleep(1);
	
	LogTrace(0, "Initilization complete. Changing state to INIT");
	BpFtHandler::getInstance().updateState(BpFtHandler::INIT);
	
	LogTrace(0, "Leaving init");
	return 0;
}

void
BpPeerHandler::resolveRole()
{
	LogTrace(0, "Entering resolveRole");

	LogTrace(0, "Creating a thread for role resolution");

	pthread_t tmpThread;
	if (-1 == pthread_create(&(tmpThread), NULL,
		_roleResolution, (void *)this)) {
		LogError(0, "Failed to create role resolution thread");
		this->shutdown();
		//LogTrace(0, "Exiting");
		//exit(1);
      LogError(0, "Going to dead state");
      BpFtHandler::getInstance().
         updateState(BpFtHandler::DEAD);
      return;

	}

	LogTrace(0, "Leaving resolveRole");
	return;
}

// Resolve role with peer
// Any TCP error, then peer is down

void
BpPeerHandler::_resolveRole()
{
	LogTrace(0, "Entering _resolveRole");

	while (1) {
		// Send a getRole request
		if (-1 == send(this->selfSockFd, (const void *)(getRole), 7, 0)) {
			logger.logMsg(ERROR_FLAG, 0, "Error sending getRole message. [%s]",
							  strerror(errno));

			LogError(0, "socket send call failed");
			//_cleanupSelf();
			//_cleanupPeer();
			
			if (BpIPAddrHandler::getInstance().isNetworkAvailable()) {
				LogError(0, "Network available... Go to primary state");
			   LogTrace(0, "Updating self role as primary");
			  	BpFtHandler::getInstance().updateRole(BpFtHandler::A_PRIMARY);
				LogTrace(0, "Changing state to RUNNING");
				BpFtHandler::getInstance().updateState(BpFtHandler::RUNNING);
				LogTrace(0, "Leaving _resolveRole");
				return;
			} 
		}

		// Wait for a response for 5 seconds else declare failure
		int retVal = 0;
		fd_set read_fds;
		
		FD_ZERO(&read_fds);
		FD_SET(this->selfSockFd, &read_fds);
		
		struct timeval time_out;
		time_out.tv_sec = 5;
		time_out.tv_usec = 0;
		
		LogTrace(0, "Call to select waiting for response");
		retVal = select(this->selfSockFd + 1, &read_fds, NULL, NULL, &time_out);
		
		if ((retVal == -1) && (errno == EINTR)) {
			logger.logMsg(WARNING_FLAG, 0, "Select interrupted. [%s]",
							  strerror(errno));
			continue;
		}

		if (retVal == -1) {
			cout << "Select Error" << endl;
			logger.logMsg(WARNING_FLAG, 0, "Select Error. [%s]",
							  strerror(errno));
			continue;
		}
		
		if (retVal == 0) {
			logger.logMsg(ERROR_FLAG, 0, "Timedout. [%s]",
							  strerror(errno));
			LogError(0, "Timeout waiting for getRole response from peer");
			_cleanupSelf();
			_cleanupPeer();
			
			LogTrace(0, "Updating self role as primary");
			BpFtHandler::getInstance().updateRole(BpFtHandler::A_PRIMARY);
			LogTrace(0, "Changing state to RUNNING");
			BpFtHandler::getInstance().updateState(BpFtHandler::RUNNING);
			
			LogTrace(0, "Leaving _resolveRole");
			return;
		}
		
		char buf[4096];
		int numBytes = recv(this->selfSockFd, (void *)(buf), 4096, 0);
		if (numBytes <= 0) {
			logger.logMsg(ERROR_FLAG, 0, "Error in recv. [%s]",
							  strerror(errno));
			
			LogError(0, "Error in recv call");
			_cleanupSelf();
			_cleanupPeer();
			
			LogTrace(0, "Updating self role as primary");
			BpFtHandler::getInstance().updateRole(BpFtHandler::A_PRIMARY);
			LogTrace(0, "Changing state to RUNNING");
			BpFtHandler::getInstance().updateState(BpFtHandler::RUNNING);
			
			LogTrace(0, "Leaving _resolveRole");
			return;
		}
		
		LogTrace(0, "Response received from peer for getRole");
		buf[numBytes] = '\0';
		
		if (0 == strcmp(roleCheck, buf)) {
			LogAlways(0, "Peer in ROLE_CHECK mode. Sleep and try again");
			if (BpFtHandler::C_PRIMARY ==
				 BpFtHandler::getInstance().getConfiguredRole()) {
				sleep(2);
				LogTrace(0, "Changing state to SLEEP_ROLE_CHECK");
				BpFtHandler::getInstance().
					updateState(BpFtHandler::SLEEP_ROLE_CHECK);
				LogTrace(0, "Leaving _resolveRole");
				return;
			}
			else {
				sleep(5);
				LogTrace(0, "Changing state to SLEEP_ROLE_CHECK");
				BpFtHandler::getInstance().
					updateState(BpFtHandler::SLEEP_ROLE_CHECK);
				LogTrace(0, "Leaving _resolveRole");
				return;
			}
		}
		else if (0 == strcmp("10", buf)) {
			LogAlways(0, "Peer in configured primary. Become secondary");

			LogTrace(0, "Updating self role as secondary");
			BpFtHandler::getInstance().updateRole(BpFtHandler::A_SECONDARY);
			LogTrace(0, "Changing state of peer to UP");
			BpFtHandler::getInstance().updatePeerStatus(BpFtHandler::UP);
			LogTrace(0, "Changing state to RUNNING");
			BpFtHandler::getInstance().updateState(BpFtHandler::RUNNING);
			
			LogTrace(0, "Leaving _resolveRole");
			return;
		}
		else if (0 == strcmp("11", buf)) {
			LogAlways(0, "Peer in actual primary. Become secondary");

			LogTrace(0, "Updating self role as secondary");
			BpFtHandler::getInstance().updateRole(BpFtHandler::A_SECONDARY);
			LogTrace(0, "Changing state of peer to UP");
			BpFtHandler::getInstance().updatePeerStatus(BpFtHandler::UP);
			LogTrace(0, "Changing state to RUNNING");
			BpFtHandler::getInstance().updateState(BpFtHandler::RUNNING);
			
			LogTrace(0, "Leaving _resolveRole");
			return;
		}
		else if (0 == strcmp("20", buf)) {
			LogAlways(0, "Peer in configured secondary. Become primary");

			LogTrace(0, "Updating self role as primary");
			BpFtHandler::getInstance().updateRole(BpFtHandler::A_PRIMARY);
			LogTrace(0, "Changing state of peer to UP");
			BpFtHandler::getInstance().updatePeerStatus(BpFtHandler::UP);
			LogTrace(0, "Changing state to RUNNING");
			BpFtHandler::getInstance().updateState(BpFtHandler::RUNNING);
			
			LogTrace(0, "Leaving _resolveRole");
			return;
		}
		else if (0 == strcmp("21", buf)) {
			LogAlways(0, "Peer in actual secondary. Become primary");

			LogTrace(0, "Updating self role as primary");
			BpFtHandler::getInstance().updateRole(BpFtHandler::A_PRIMARY);
			LogTrace(0, "Changing state of peer to UP");
			BpFtHandler::getInstance().updatePeerStatus(BpFtHandler::UP);
			LogTrace(0, "Changing state to RUNNING");
			BpFtHandler::getInstance().updateState(BpFtHandler::RUNNING);
			
			LogTrace(0, "Leaving _resolveRole");
			return;
		}
		else {
			LogAlways(0, "Invalid response from peer. Continue");
			continue;
		}
	}

	LogTrace(0, "Leaving _resolveRole");
}
	
int
BpPeerHandler::connectWithPeer()
{
	LogTrace(0, "Entering connectWithPeer");

	LogTrace(0, "Creating a thread to for connecting to peer");

	pthread_t tmpThread;
	if (-1 == pthread_create(&(tmpThread), NULL,
		_peerConnection, (void *)this)) {
		LogError(0, "Failed to create create connection thread");
		this->shutdown();
		//LogTrace(0, "Exiting");
		//exit(1);
		LogError(0, "Going to dead state");
		BpFtHandler::getInstance().
			updateState(BpFtHandler::DEAD);
		return -1;
	}

	LogTrace(0, "Leaving connectWithPeer");
	return 0;
}

void
BpPeerHandler::startHeartbeat()
{
	LogTrace(0, "Entering startHeartbeat");

	LogTrace(0, "Creating a heartbeat thread");

	pthread_t tmpThread;
	if (-1 == pthread_create(&(tmpThread), NULL,
		_heartbeat, (void *)this)) {
		LogError(0, "Failed to create heartbeat thread");
		this->shutdown();
		//LogTrace(0, "Exiting");
		//exit(1);
      LogError(0, "Going to dead state");
      BpFtHandler::getInstance().
         updateState(BpFtHandler::DEAD);
      return;
	}

	LogTrace(0, "Leaving startHeartbeat");
	return;
}

void
BpPeerHandler::_connectWithPeer()
{
	LogTrace(0, "Entering _connectWithPeer");
	
	// Ping the peer machine, if not reachable become primary
	if (BpMachPing::NOT_AVAILABLE ==
		BpIPAddrHandler::getInstance().getPeerMachStatus()) {
		LogTrace(0, "Peer Machine not reachable. Updating role to primary");
		BpFtHandler::getInstance().updateRole(BpFtHandler::A_PRIMARY);
		LogTrace(0, "Changing state to RUNNING");
		BpFtHandler::getInstance().updateState(BpFtHandler::RUNNING);
		LogTrace(0, "Leaving _connectWithPeer");
		return;
	}

	// Try the connection 5 times at an interval of 5 seconds
	int count = 0;
	while (count < 5) {
		if (-1 == _connect()) {
			count ++;
			sleep(5);
			continue;
		}

		LogTrace(0, "Successfully created connection with peer");
		LogTrace(0, "Changing state to ROLE_CHECK");
		BpFtHandler::getInstance().updateState(BpFtHandler::ROLE_CHECK);
		LogTrace(0, "Leaving _connectWithPeer");
		return;
	}

	if (BpIPAddrHandler::getInstance().isNetworkAvailable()) {
		LogError(0, "Network available... Go to primary state");
   
		LogAlways(0, "Failed to establish connection with Peer");
		LogTrace(0, "Connection with peer failed. Updating role to primary");
		BpFtHandler::getInstance().updateRole(BpFtHandler::A_PRIMARY);
		LogTrace(0, "Changing state to RUNNING");
		BpFtHandler::getInstance().updateState(BpFtHandler::RUNNING);
		LogTrace(0, "Leaving _connectWithPeer");  

		return;
	} 

	LogAlways(0, "Failed to establish connection with Peer");
	LogTrace(0, "Connection with peer failed. Updating role to primary");
	BpFtHandler::getInstance().updateRole(BpFtHandler::A_PRIMARY);
	LogTrace(0, "Changing state to RUNNING");
	BpFtHandler::getInstance().updateState(BpFtHandler::RUNNING);
	LogTrace(0, "Leaving _connectWithPeer");
	return;
}

int
BpPeerHandler::_connect()
{
	LogTrace(0, "Entering connect");

	int timeout = 10; //10 Secs.

	// Create a socket
	LogTrace(0, "Create a sender socket");
	int sock = socket(PF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (-1 == sock) {
		logger.logMsg(ERROR_FLAG, 0, "Socket creation failed. [%s]",
       	strerror(errno));
		this->shutdown();
		//LogError(0, "Exiting");
		//exit(1);
		LogError(0, "Going to dead state");
		BpFtHandler::getInstance().
			updateState(BpFtHandler::DEAD);
		return -1;
	}

	this->selfSockFd = sock;
	
	std::string ip = BpFtHandler::getInstance().getPeerIp();
   struct sockaddr_in srvAddr;
   memset(&srvAddr, 0, sizeof(srvAddr));
   srvAddr.sin_family = AF_INET;
   srvAddr.sin_port =
		htons(BpFtHandler::getInstance().getPeerPort());
	srvAddr.sin_addr.s_addr = inet_addr(ip.c_str());

	LogTrace(0, "Call connect");
	logger.logMsg(TRACE_FLAG, 0, "IP = [%s], Port = [%d]", ip.c_str(),
					  BpFtHandler::getInstance().getPeerPort());

   errno = 0;

   int retVal = 0;
   fd_set read_fds;
   fd_set write_fds;

   if ((retVal =
		  connect(this->selfSockFd, (struct sockaddr *)&srvAddr,
					 sizeof(srvAddr))) < 0) {
		
      if (errno != EINPROGRESS) {
         logger.logMsg(ERROR_FLAG, 0, "Error connecting server. [%s]",
                       strerror(errno));
			_cleanupSelf();
			
			LogTrace(0, "Leaving connect");
         return -1;
      }

      while (true) {
         errno = 0;

         FD_ZERO(&read_fds);
			FD_SET(sock, &read_fds);
         FD_ZERO(&write_fds);
			FD_SET(this->selfSockFd, &write_fds);

         struct timeval time_out;
         time_out.tv_sec = timeout;
         time_out.tv_usec = 0;

         retVal = select(this->selfSockFd + 1, &read_fds, &write_fds,
								 NULL, &time_out);
			
         if ((retVal == -1) && (errno == EINTR)) {
            logger.logMsg(WARNING_FLAG, 0, "Select interrupted. [%s]",
                          strerror(errno));
            continue;
         }
			if (retVal == -1) {
				cout << "Select Error" << endl;
				logger.logMsg(WARNING_FLAG, 0, "Select Error. [%s]",
								  strerror(errno));
				continue;
			}

         break;
      }

      if (retVal == 0) {
         logger.logMsg(ERROR_FLAG, 0, "Connect failed, Timedout. [%s]",
                       strerror(errno));
			_cleanupSelf();
			
			LogTrace(0, "Leaving connect");
         return -1;
      }

      if (retVal == -1) {
         logger.logMsg(ERROR_FLAG, 0, "Select failed. [%s]", strerror(errno));
			_cleanupSelf();
			
			LogTrace(0, "Leaving connect");
         return -1;
      }
      if (FD_ISSET(sock, &read_fds) || FD_ISSET(sock, &write_fds)) {
         int err;
         socklen_t errlen;

         errlen = sizeof(err);

         if (getsockopt(this->selfSockFd, SOL_SOCKET, SO_ERROR,
								&err, &errlen) < 0) {
            logger.logMsg(ERROR_FLAG, 0, "Error reading sock property. [%s]",
                          strerror(errno));
				_cleanupSelf();
				
				LogTrace(0, "Leaving connect");
				return -1;
         }

         if (err != 0) {
            logger.logMsg(ERROR_FLAG, 0, "Error in connect. [%d] [%s]",
                          err, strerror(err));
				_cleanupSelf();
				LogTrace(0, "Leaving connect");
				return -1;
         }

			LogAlways(0, "Connection with peer established");
      }
   }

	LogTrace(0, "Leaving connect");
	return 0;
}


void
BpPeerHandler::shutdown()
{
	LogTrace(0, "Entering shutdown");

	doListen = false;

	if (-1 != listenerFd) {
		//close(listenerFd);
		::shutdown(listenerFd,SHUT_RDWR);
		//BPInd17019 (Mayank)
		//On some solaris systems it was observed 
		//that the socket does not get closed properly
		//until the close() API was invoked. 
		//Hence this explicit call was added, otherwise ::shutdown 
		//should have done the job
		close(listenerFd) ; 
		listenerFd = -1;
	}
	
	_cleanupPeer();
	_cleanupSelf();
	
	LogTrace(0, "Leaving shutdown");
}

void
BpPeerHandler::_cleanupSelf()
{
	LogTrace(0, "Entering _cleanupSelf");

	if (-1 != selfSockFd) {
		//close(selfSockFd);
		::shutdown(selfSockFd,SHUT_RDWR);
		//BPInd17019(Mayank)
		close(selfSockFd) ; 
		selfSockFd = -1;
	}

	LogTrace(0, "Leaving _cleanupSelf");
}

void
BpPeerHandler::_cleanupPeer()
{
	LogTrace(0, "Entering _cleanupPeer");

	if (-1 != peerSockFd) {
		//close(peerSockFd);
		::shutdown(peerSockFd,SHUT_RDWR);
		//BPInd17019(Mayank)
		close(peerSockFd) ; 
		peerSockFd = -1;
	}

	LogTrace(0, "Leaving _cleanupPeer");
}

void
BpPeerHandler::heartbeat()
{
	LogTrace(0, "Entering heartbeat");

	int retVal = 0;
	fd_set read_fds;
	
	FD_ZERO(&read_fds);
	FD_SET(this->selfSockFd, &read_fds);

	// select timeout
	struct timeval time_out;
	time_out.tv_sec = 5;
	time_out.tv_usec = 0;

	// heartbeat time 250 milli seconds
	timespec ts;
	ts.tv_sec = 0;
	ts.tv_nsec = 250000000;

	while (1) {
		LogTrace(0, "Sending Heartbeat");
		
		if (-1 == send(this->selfSockFd, (const void *)(heartBeat), 9, 0)) {
			logger.logMsg(ERROR_FLAG, 0,
							  "Error sending heartbeat. [%s]",
							  strerror(errno));
			
			LogError(0, "socket send call failed");
			_cleanupSelf();
			_cleanupPeer();
			
			LogTrace(0, "Updating Peer Load Balancer status as DOWN");
			BpFtHandler::getInstance().
				updatePeerStatus(BpFtHandler::DOWN);
			break;
		}

		retVal = select(this->selfSockFd + 1, &read_fds, NULL, NULL, &time_out);
		
		if ((retVal == -1) && (errno == EINTR)) {
			logger.logMsg(WARNING_FLAG, 0, "Select interrupted. [%s]",
							  strerror(errno));
			continue;
		}
		if (retVal == -1) {
			logger.logMsg(WARNING_FLAG, 0, "Select Error. [%s]",
							  strerror(errno));
			continue;
		}
		
		if (retVal == 0) {
			logger.logMsg(ERROR_FLAG, 0, "Timedout. [%s]",
							  strerror(errno));
			LogError(0, "Timeout waiting for heartbeat response");
			_cleanupSelf();
			_cleanupPeer();
			
			LogTrace(0, "Updating Peer Load Balancer status as DOWN");
			BpFtHandler::getInstance().
				updatePeerStatus(BpFtHandler::DOWN);
			break;
		}
		
		char buf[4096];
		int numBytes = recv(this->selfSockFd, (void *)(buf), 4096, 0);
		if (numBytes <= 0) {
			logger.logMsg(ERROR_FLAG, 0, "Error in recv. [%s]",
							  strerror(errno));
			
			LogError(0, "Error in recv call");
			_cleanupSelf();
			_cleanupPeer();

			LogTrace(0, "Updating Peer Load Balancer status as DOWN");
			BpFtHandler::getInstance().
				updatePeerStatus(BpFtHandler::DOWN);
			break;
		}

		LogTrace(0, "Heartbeat response received");
		nanosleep(&ts, NULL);
	}
	LogTrace(0, "Leaving heartbeat");
}
		
void
BpPeerHandler::receiver()
{
	LogTrace(0, "Entering receiver");

	int retVal = 0;
	fd_set read_fds;
	
	FD_ZERO(&read_fds);
	FD_SET(this->peerSockFd, &read_fds);

	while (1) {
		LogTrace(0, "Call to select waiting for data");
		retVal = select(this->peerSockFd + 1, &read_fds, NULL, NULL, NULL);
		logger.logMsg(TRACE_FLAG, 0, "Select Returned. RetVal = [%d] [%s]",
						  retVal, strerror(errno));
		
		if ((retVal == -1) && (errno == EINTR)) {
			logger.logMsg(WARNING_FLAG, 0, "Select interrupted. [%s]",
							  strerror(errno));
			continue;
		}
		if (retVal == -1) {
			logger.logMsg(WARNING_FLAG, 0, "Select Error. [%s]",
							  strerror(errno));
			continue;
		}

		char buf[4096];
		int numBytes = recv(this->peerSockFd, (void *)(buf), 4096, 0);
		if (numBytes <= 0) {
			logger.logMsg(ERROR_FLAG, 0, "Error in recv. [%s]",
				strerror(errno));
			LogError(0, "Closing socket");
			_cleanupPeer();
			
            LogTrace(0, "Updating Peer Load Balancer status as DOWN");
            BpFtHandler::getInstance().
                updatePeerStatus(BpFtHandler::DOWN);

			break;
		}

		buf[numBytes] = '\0';

		if (0 == strcmp(getRole, buf)) {
			LogAlways(0, "get Role request");
			int state = BpFtHandler::getInstance().getState();
			if (BpFtHandler::ROLE_CHECK == state) {
				LogAlways(0, "In ROLE_CHECK state");
				if (-1 == send(this->peerSockFd, (const void *)(roleCheck),
									9, 0)) {
					logger.logMsg(ERROR_FLAG, 0,
									  "Error sending rolecheck message. [%s]",
									  strerror(errno));
					LogError(0, "socket send call failed. Closing");
					_cleanupPeer();

					LogTrace(0, "Updating Peer Load Balancer status as DOWN");
					BpFtHandler::getInstance().
						updatePeerStatus(BpFtHandler::DOWN);					
					break;
				}
			}
			else {
				int aRole = BpFtHandler::getInstance().getActualRole();
				int cRole = BpFtHandler::getInstance().getConfiguredRole();

				char buf[50];

				// Fasih: If the peer is in defunct mode.... or init then send configured role...
				LogTrace(0, "If role is defunct or init... send configured role..");
				if (-1 == aRole || 7 == aRole) {
					logger.logMsg(TRACE_FLAG, 0, "Sending configured role = [%d]",
									  cRole);
					sprintf(buf, "%d", cRole);
				}
				else {
					logger.logMsg(TRACE_FLAG, 0, "Sending actual role = [%d]",
									  aRole);
					sprintf(buf, "%d", aRole);
				}

				if (-1 == send(this->peerSockFd, (const void *)(buf),
									strlen(buf), 0)) {
					logger.logMsg(ERROR_FLAG, 0,
									  "Error sending role. [%s]",
									  strerror(errno));
					LogError(0, "socket send call failed. Closing");
					_cleanupPeer();
					
					LogTrace(0, "Updating Peer Load Balancer status as DOWN");
					BpFtHandler::getInstance().
						updatePeerStatus(BpFtHandler::DOWN);
					break;
				}
			}
		}

		else if (0 == strcmp(heartBeat, buf)) {
			LogTrace(0, "Received Heartbeat");
			if (-1 == send(this->peerSockFd, (const void *)(heartBeat),
								9, 0)) {
				logger.logMsg(ERROR_FLAG, 0,
								  "Error sending heartbeat response. [%s]",
								  strerror(errno));
				LogError(0, "socket send call failed. Closing");
				_cleanupPeer();
			
				LogTrace(0, "Updating Peer Load Balancer status as DOWN");
				BpFtHandler::getInstance().
					updatePeerStatus(BpFtHandler::DOWN);	
				break;
			}
			LogTrace(0, "Sent Heartbeat Response");
		}
	}
	
	LogTrace(0, "Leaving receiver");
}

void
BpPeerHandler::listener()
{
	LogTrace(0, "Entering listener");
	
	while(doListen) {
		int retVal = 0;
		fd_set read_fds;
		
		FD_ZERO(&read_fds);
		FD_SET(this->listenerFd, &read_fds);
		
		LogTrace(0, "Call to select waiting for response");
		retVal = select(this->listenerFd + 1, &read_fds, NULL, NULL, NULL);

		if (retVal != 1) {
         logger.logMsg(ERROR_FLAG, 0, "Select returned [%d] [%s]",
                       retVal, strerror(errno));
         continue;
      }

		struct sockaddr_in peerInfo;
		memset((void *)(&peerInfo), 0, sizeof(sockaddr_in));
		socklen_t len = sizeof(sockaddr_in);

		LogTrace(0, "Calling accept");
		int sock = accept(this->listenerFd, (sockaddr *)(&peerInfo), &len);
		LogTrace(0, "After accept");

		if (sock <= 1) {
			logger.logMsg(ERROR_FLAG, 0, "Error in accept. [%s]",
				strerror(errno));
         continue;
		}

		// Received connection request from peer
		// If we already have a receiver connection,
		// then something has gone wrong
		// In this case shutdown
		LogAlways(0, "Received connection request from Peer");
		
		if (-1 != this->peerSockFd) {
			LogError(0, "Connection with peer already exists. Exiting");
			this->shutdown();
			//exit(1);
			LogError(0, "Going to dead state");
			BpFtHandler::getInstance().
				updateState(BpFtHandler::DEAD);
			return;
		}
		
		this->peerSockFd = sock;

		// If I am in RUNNING stage then indicate peer up
		if (BpFtHandler::RUNNING ==
			 BpFtHandler::getInstance().getState()) {

			// Check the peer status. If already up then do nothing
			BpFtHandler::Status curStatus;
			BpFtHandler::Status oldStatus;
			BpFtHandler::getInstance().getPeerStatus(oldStatus, curStatus);

			if (BpFtHandler::DOWN == curStatus ||
				 BpFtHandler::NO_STATE == curStatus) {
				// Try and setup a connection with the peer
				LogTrace(0, "Setup connection with peer");
				if (-1 == _connect()) {
					LogError(0, "Failed to setup peer connection. Exiting");
					this->shutdown();
					//exit(1);
					LogError(0, "Going to dead state");
					BpFtHandler::getInstance().
						updateState(BpFtHandler::DEAD);
					return;
				}
				
				LogTrace(0, "Successfullt setup connection with peer");
				LogTrace(0, "Updating Peer Load Balancer status as UP");
				BpFtHandler::getInstance().
					updatePeerStatus(BpFtHandler::UP);
			}
		}
		
		// launch the receiver
		if (-1 == pthread_create(&(this->receiverThread), NULL,
			_receiver, (void *)this)) {
			LogError(0, "Failed to create receiver thread");
			this->shutdown();
			//LogTrace(0, "Exiting");
			//exit(1);
         LogError(0, "Going to dead state");
         BpFtHandler::getInstance().
            updateState(BpFtHandler::DEAD);
         return;
		}
	}
}

BpPeerHandler::BpPeerHandler() :
	doListen(false),
	listenerFd(-1),
	peerSockFd(-1),
	selfSockFd(-1)
{}

BpPeerHandler::~BpPeerHandler()
{}
