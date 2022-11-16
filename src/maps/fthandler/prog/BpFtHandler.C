#include <Util/Logger.h>
LOG("LoadBalancer");

#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <iostream.h>
#include <fstream.h>
#include <strings.h>

#include <unistd.h>
#include <netdb.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <sys/wait.h>

#include "BpApacheHandler.h"
#include "BpPeerHandler.h"
#include "BpFtHandler.h"

// Exteranl Function which searches for primaryIp and secondaryIp in the
// network interfaces. Returns 1 is primary ip is found, 0 if secondary
// ip is found. If neither is found, the program exits.
// Uses ioctl commands to get the network interfaces
extern int
determineConfiguredRole(std::string &primaryIp, std::string &secondaryIp);

BpFtHandler *BpFtHandler::me;

BpFtHandler &
BpFtHandler::getInstance()
{
   if (NULL == me)
      me = new BpFtHandler();

   return *me;
}

int
BpFtHandler::init(const char *cfgFile)
{
   LogTrace(0, "Entering init");

	// Check for validity of config file
	if ((NULL == cfgFile) || (0 == strlen(cfgFile))) {
      LogError(0, "Invalid config file");
   	LogTrace(0, "Leaving init");
		return -1;
	}

	logger.logMsg(ALWAYS_FLAG, 0, "Configuration File = [%s]", cfgFile);
	
   // Read the configuration
	int ret = readConfig(cfgFile);
	if (-1 == ret) {
      LogError(0, "Failed to read Configuration");
   	LogTrace(0, "Leaving init");
		return -1;
	}

	// Determine Configured Role
	setConfiguredRole();
	
   LogTrace(0, "Configuration successfully Read");
		
	// Initialize the Locks
	pthread_rwlock_init(&statusLock, 0);
	pthread_rwlock_init(&stateLock, 0);
	pthread_rwlock_init(&roleLock, 0);

	// Initialize the stausCondition
	pthread_mutex_init(&mainLock, 0);
	pthread_cond_init(&mainCond, NULL);
	// Initialize configured Role
	int myRole = 0;
	if (true == this->isPrimary)
		myRole = C_PRIMARY;
	else
		myRole = C_SECONDARY;
	
	this->updateConfiguredRole(myRole);
	
	// Create an instance of the IPAddrHandler
   LogTrace(0, "Initializing BpIPAddrHandler");
	BpIPAddrHandler::getInstance().initialize();

	// Create an instance of the Apache Handler
   LogTrace(0, "Initializing Apache Handler");
	this->apacheHdlr = new BpApacheHandler();
	this->apacheHdlr->init();

   LogTrace(0, "Starting Apache");
	if (-1 == this->apacheHdlr->startApache()) {
		LogError(0, "Failed to launch Apache");
   	LogTrace(0, "Leaving init");
		return -1;
	}

	// Now go to sleep
	while (1) {
		LogTrace(0, "mainLock mutex pe Lock lagaa liyaaa");
		pthread_mutex_lock(&mainLock);
		//BPInd17020 (Mayank) (Predicate Flag checking Added
		//to ensure that the signal is not lost and a spurious signal
		//doesn't cause the wait condition to change)
		
/*  If the child thread runs first then it may signal the condvar 
 before the main thread waits on it. Condition variables do *not* 
 remember signals, so we can  end up in a situation with 
 the thread stuck in cond_wait for ever.
 (Added by Mayank)

So if the predicate flag is false (0) that means that the signal 
has not been sent as yet and the cond_wait needs to wait for signal to come
Alternatively if the signal has been sent and the main thread is not waiting 
then it should skip the while loop and not wait at all  
*/		
		while(isPredicateFalse())
		{
		pthread_cond_wait(&mainCond, &mainLock);
		}
		//Resetting the predicate flag back to false or 0 
		setPredicateFalse() ; 
		LogError(0, "Koi uthaa diyaa Woken up from condtion wait. Check state change");

		// this method checks the state change and takes appropriate action
		action();
		pthread_mutex_unlock(&mainLock);
		LogTrace(0, "mainLock mutex unlocked");
		continue;
	}

   LogTrace(0, "Leaving init");
	return 0;
}

int
BpFtHandler::action()
{
   LogTrace(0, "Entering action");

	// Perform check to see what has changed in the configuration and
	// take appropriate action

	// Check for Apache Change
	if (isApacheChange()) {
		BpFtHandler::Status curStatus, oldStatus;
		this->getApacheStatus(oldStatus, curStatus);

		// Apache UP
		if (BpFtHandler::UP == curStatus) {
			LogAlways(0, "Apache started up.");
		
			// Start the Peer Handler
			LogTrace(0, "Initializing Peer Handler");
			if ( NULL == this->peerHdlr)
				this->peerHdlr = new BpPeerHandler();
			this->peerHdlr->init();
		}

	/*	// My process is down
		else if (BpFtHandler::DOWN == curStatus) {
			LogError(0, "The program I had started is down. Exit and let peer take over");
			
			if (NULL != this->peerHdlr) 
				this->peerHdlr->shutdown();
			LogError(0, "Exiting");
			exit(1);
		}
		
		// Fasih: The program which I had to monitor is defunct... So shutting down the communication with peer
		// till the state is changed again....
		else if (BpFtHandler::DEFUNCT == curStatus) {
			LogError(0, "I was expected to monitor this process.... It is defunct... shutting down peer");
			
			if (NULL != this->peerHdlr) {
				// Give up primary....
				// Unset the floating ip as well...
				changeRole(false);
				this->peerHdlr->shutdown();
			}
			LogError(0, "Wait till it is alive...");
		}
	*/	
	}

	// Role Change
	if (isRoleChange()) {
		int role = getActualRole();
        //Testing 
		if (BpFtHandler::A_PRIMARY == role) {
			LogTrace(0, "Become Primary. Take over floating IP");
			changeRole(true);
		}
		else if (BpFtHandler::A_SECONDARY == role) {
			LogTrace(0, "Become Secondary. Release floating IP");
			changeRole(false);
		}
	}

 	// Peer Change
	if (isPeerChange()) {
		BpFtHandler::Status curStatus;
		BpFtHandler::Status oldStatus;

		BpFtHandler::getInstance().getPeerStatus(oldStatus, curStatus);

		// Peer UP then start heartbeat thread
		if (BpFtHandler::UP == curStatus) {
			LogTrace(0, "Peer status UP, start heartbeat");
			this->peerHdlr->startHeartbeat();
		}
		else if (BpFtHandler::DOWN == curStatus) {
			LogTrace(0, "Peer status DOWN. Check if ROLE change is required");
			checkRole();
		}
	}
	
	// State Change
	if (isStateChange()) {
		BpFtHandler::State st = this->getState();

		// STARTUP State Nothing to do
		if (BpFtHandler::STARTUP == st) {
			LogTrace(0, "STARTUP state. Nothing to do.");
		}
		// INIT state. Setup connection with peer
		else if (BpFtHandler::INIT == st) {
			LogTrace(0, "INIT state. Setup connection with peer");
			this->peerHdlr->connectWithPeer();
		}
		// ROLE_CHECK state. Setup connection with peer
		else if (BpFtHandler::ROLE_CHECK == st ||
					BpFtHandler::SLEEP_ROLE_CHECK == st) {
			LogTrace(0, "ROLE_CHECK state. Resolve Role with peer");
			this->peerHdlr->resolveRole();
		}
		// Fasih: Five new states...
	   // 	Primary state: 
		//		Unset floating ip on peer
		//		Set floating ip on self
		else if (BpFtHandler::PRIMARY == st ) {
			LogTrace(0, "Entering PRIMARY  state...UNSET FIP on peer & SET FIP on self");
			BpIPAddrHandler::getInstance().setSelfMode(true);
			LogTrace(0, "UNSET FIP on peer & SET FIP on self done");
		}

		// Secondary state:
		//		Unset floating ip on self
		else if (BpFtHandler::SECONDARY == st ) {
			LogTrace(0, "Entering SECONDARY state...UNSET FIP on self");
			BpIPAddrHandler::getInstance().setSelfMode(false);
			LogTrace(0, "SECONDARY state...UNSET FIP on self done");
		}

		// Defunct state:
		// 	Increment isDefunct counter
		//		Stop the listener peer
		//		Stop the sender peer
		//  	Unset floating ip on self
		// 	Update role to defunct
		//		Raise alarm
		else if (BpFtHandler::DEFUNCT == st ) {
			LogTrace(0, "Entering DEFUNCT state");
			makeDefunct();
			raiseAlarm("610","FTHANDLER going to defunct state..");
                        //  raiseAlarm("3604","FTHANDLER going to defunct state..");
			LogTrace(0, "Leaving DEFUNCT state");
                        LogTrace(0, "3604 sending");
                        LogError(0, "3604 going");
		}

		// Active state:
		// 	Decrement counter
		//		Start the listener peer
		//  	Start the sender peer
		//		Do Role resolution
		else if (BpFtHandler::ACTIVE == st ) {
			LogTrace(0, "Entering ACTIVE state");
			if (NULL == this->peerHdlr)
				this->peerHdlr = new BpPeerHandler();
			LogTrace(0, "Do role resolution");
			this->peerHdlr->init();
			LogTrace(0, "Leaving UP state");
		}

		// Dead state:
		//		Stop the listener peer
		//		Stop the sender peer
		//		Unset floating ip on peer
		//		Kill the process started
		//		Raise alarm
		//		Exit
		else if (BpFtHandler::DEAD == st ) {
			LogTrace(0, "Entering DEAD state");
			LogTrace(0, "Stop the listener comm");
			if (NULL != this->peerHdlr)
				this->peerHdlr->shutdown();
			LogTrace(0, "UNSET FIP on self");
			BpIPAddrHandler::getInstance().setSelfMode(false);
			LogTrace(0, "Kill the process started");
			this->apacheHdlr->shutdown();
			LogTrace(0, "Leaving DEAD state.. Exiting...");
			raiseAlarm("610","FtHandler down...");
			exit(1);
		}
	}
	LogTrace(0, "Leaving action");
	return 0;
}

void
BpFtHandler::checkRole()
{
	LogTrace(0, "Entering checkRole");

	
	logger.logMsg(TRACE_FLAG, 0, "Current Role = [%d]", this->aRole);

	if (this->aRole == BpFtHandler::A_PRIMARY) {
		LogAlways(0, "Current role is primary. No change required");
		LogTrace(0, "Leaving checkRole");
		return;
	}

	LogAlways(0, "Current role is secondary. Change to primary");
	this->aRole = BpFtHandler::A_PRIMARY;
	
	BpIPAddrHandler::getInstance().setSelfMode(true);
	
	LogTrace(0, "Leaving checkRole");
}
	
BpFtHandler::State
BpFtHandler::getState()
{
	LogTrace(0, "Getting R_LOCK on state");
	pthread_rwlock_rdlock(&stateLock);
	BpFtHandler::State aState = this->state;
	LogTrace(0, "Leaving R_LOCK on state");
	pthread_rwlock_unlock(&stateLock);
	return aState;
}

// Fasih: If the main thread is in defunct state..
// There can be no transition other than going to active state or dead state
// Or some other thread going into defunct state...
void
BpFtHandler::updateState(BpFtHandler::State aState)
{
   LogTrace(0, "Entering updateState");
	logger.logMsg(TRACE_FLAG, 0, "Old State = [%d] New State = [%d]",
	      this->state, aState);


	if(isDefunct()) {
		logger.logMsg(TRACE_FLAG, 0, "The thread is defunct due to %d reason(s)",_isDefunct);
		if(aState != BpFtHandler::DEFUNCT && aState != BpFtHandler::ACTIVE && aState != BpFtHandler::DEAD)  {
			LogTrace(0, "The new state is neither DEFUNCT nor ACTIVE.. Make sure we are still in defunct state...");
			makeDefunct();
			return;
		} else if (aState == BpFtHandler::ACTIVE) {
			--_isDefunct;
			logger.logMsg(TRACE_FLAG, 0, "Now there are %d defunct reasons..",_isDefunct);
			if(isDefunct()) {
				LogTrace(0, "This is still defunct.... Wait till all the reasons clear up...Leaving updateState");
				return;
			}
		}    
										
	}

	// Only in case of defunct state.. can the thread reenter the same state
	if (aState == BpFtHandler::DEFUNCT) {
		_isDefunct++;
		logger.logMsg(TRACE_FLAG, 0, "This is reason %d to go to the defunct state..",_isDefunct);
	} else if (this->state == aState) {
		LogTrace(0, "No state change required..");
		LogTrace(0, "Leaving updateState");
		return;
	}
	
   LogTrace(0, "Getting RW_LOCK on state");
	pthread_rwlock_wrlock(&stateLock);
	this->state = aState;
   LogTrace(0, "Leaving RW_LOCK on state");
	pthread_rwlock_unlock(&stateLock);

	// Signal the main condition varibale
	updateMainChange(BpFtHandler::STATE_CHANGE);
	
   LogTrace(0, "Leaving updateState");
}

int
BpFtHandler::getConfiguredRole()
{
	pthread_rwlock_rdlock(&roleLock);
	int role = this->cRole;
	pthread_rwlock_unlock(&roleLock);
	return role;
}

int
BpFtHandler::getActualRole()
{
	pthread_rwlock_rdlock(&roleLock);
	int role = this->aRole;
	pthread_rwlock_unlock(&roleLock);
	return role;
}

void
BpFtHandler::updateConfiguredRole(int role)
{
	pthread_rwlock_wrlock(&roleLock);
	this->cRole = role;
	pthread_rwlock_unlock(&roleLock);
}

void
BpFtHandler::updateRole(int role)
{
   LogTrace(0, "Entering updateRole");

	LogTrace(0, "Check the network...");
	BpIPAddrHandler::getInstance().isNetworkAvailable();

	logger.logMsg(TRACE_FLAG, 0, "Old Role = [%d] New Role = [%d]",
		this->aRole, role);

    if(isDefunct()) {
   	LogTrace(0, "The thread is defunct...Cannot update the role");
		makeDefunct();
		return;
    }

	if (this->aRole == role) {
		LogTrace(0, "No role change required");
		LogTrace(0, "Leaving updateRole");
		return;
	}
	
	pthread_rwlock_wrlock(&roleLock);
	this->aRole = role;
	pthread_rwlock_unlock(&roleLock);

	// Signal the main condition varibale
	updateMainChange(BpFtHandler::ROLE_CHANGE);
	
   LogTrace(0, "Leaving updateRole");
}

void
BpFtHandler::getPeerStatus(BpFtHandler::Status &oldStatus,
										BpFtHandler::Status &curStatus)
{
	pthread_rwlock_rdlock(&statusLock);
	oldStatus = this->oldPeerStatus;
	curStatus = this->peerStatus;
	pthread_rwlock_unlock(&statusLock);
}

void
BpFtHandler::updatePeerStatus(BpFtHandler::Status status)
{
   LogTrace(0, "Entering updatePeerStatus");

	LogTrace(0, "Check the network...");
	BpIPAddrHandler::getInstance().isNetworkAvailable();

	logger.logMsg(TRACE_FLAG, 0, "Old Status = [%d] New Status = [%d]",
		this->peerStatus, status);

    if(isDefunct()) {
     	LogTrace(0, "The thread is defunct...Cannot update peerStatus");
		makeDefunct();
		return;
    }

	if (this->peerStatus == status) {
		LogTrace(0, "No peer status change required");
		LogTrace(0, "Leaving updatePeerStatus");
		return;
	}
	
	pthread_rwlock_wrlock(&statusLock);
	this->oldPeerStatus = this->peerStatus;
	this->peerStatus = status;
	pthread_rwlock_unlock(&statusLock);

	// Signal the main condition varibale
	updateMainChange(BpFtHandler::PEER_CHANGE);

   LogTrace(0, "Leaving updatePeerStatus");
}

void
BpFtHandler::getApacheStatus(BpFtHandler::Status &oldStatus,
										  BpFtHandler::Status &curStatus)
{
	pthread_rwlock_rdlock(&statusLock);
	oldStatus = this->oldApacheStatus;
	curStatus = this->apacheStatus;
	pthread_rwlock_unlock(&statusLock);
}

void
BpFtHandler::updateApacheStatus(BpFtHandler::Status status)
{
   LogTrace(0, "Entering updateApacheStatus");

	logger.logMsg(TRACE_FLAG, 0, "Old Status = [%d] New Status = [%d]",
		this->apacheStatus, status);

 	if(isDefunct()) {
  		LogTrace(0, "The thread is defunct... Cannot update the apache status");
		makeDefunct();
		return;
  	}

	if (this->apacheStatus == status) {
		LogTrace(0, "Np Apache status change required");
		LogTrace(0, "Leaving updateApacheStatus");
		return;
	}
	
	pthread_rwlock_wrlock(&statusLock);
	this->oldApacheStatus = this->apacheStatus;
	this->apacheStatus = status;
	pthread_rwlock_unlock(&statusLock);

	// Signal the main condition varibale
	updateMainChange(BpFtHandler::APACHE_CHANGE);
	
   LogTrace(0, "Leaving updateApacheStatus");
}

void
BpFtHandler::updateMainChange(int change)
{
	LogTrace(0, "Acquiring mutex LOCK on main");
	pthread_mutex_lock(&mainLock);
	mainChange |= change;
   LogTrace(0, "Signalling the main thread to act on change and also setting the predicate flag");
	setPredicateTrue() ; //BPInd17020 (mayank)
	pthread_cond_signal(&mainCond);
	pthread_mutex_unlock(&mainLock);
	LogTrace(0, "Mutex LOCK released on main");
}

//BPInd17020 Starts (Mayank)
void 
BpFtHandler::setPredicateTrue()
{
	this->predicateFlag = 1 ; 
}

void
BpFtHandler::setPredicateFalse()
{
	this->predicateFlag = 0 ; 
}

bool
BpFtHandler::isPredicateFalse()
{
if(this->predicateFlag == 0) 
   return true ; 
else
   return false ; 
}

//BPInd17020 (Ends)

// This thread is always called by the main thread which already holds
// the mainLock
bool
BpFtHandler::isApacheChange()
{
   LogTrace(0, "Entering isApacheChange");
	bool ret;
	
	if (BpFtHandler::APACHE_CHANGE & mainChange)
		ret = true;
	else
		ret = false;
	mainChange &= ~(BpFtHandler::APACHE_CHANGE);

	logger.logMsg(TRACE_FLAG, 0, "isApacheChange. Return = [%d]", (int)ret);
   LogTrace(0, "Leaving isApacheChange");
	return ret;
}

// This thread is always called by the main thread which already holds
// the mainLock
bool
BpFtHandler::isStateChange()
{
   LogTrace(0, "Entering isStateChange");
	bool ret;
	
	if (BpFtHandler::STATE_CHANGE & mainChange)
		ret = true;
	else
		ret = false;
	mainChange &= ~(BpFtHandler::STATE_CHANGE);

	logger.logMsg(TRACE_FLAG, 0, "isStateChange. Return = [%d]", (int)ret);
   LogTrace(0, "Leaving isStateChange");
	return ret;
}

// This thread is always called by the main thread which already holds
// the mainLock
bool
BpFtHandler::isPeerChange()
{
   LogTrace(0, "Entering isPeerChange");
	bool ret;
	
	if (BpFtHandler::PEER_CHANGE & mainChange)
		ret = true;
	else
		ret = false;
	mainChange &= ~(BpFtHandler::PEER_CHANGE);

	logger.logMsg(TRACE_FLAG, 0, "isPeerChange. Return = [%d]", (int)ret);
   LogTrace(0, "Leaving isPeerChange");
	return ret;
}

// This thread is always called by the main thread which already holds
// the mainLock
bool
BpFtHandler::isRoleChange()
{
   LogTrace(0, "Entering isRoleCHange");
   LogTrace(0, "Inside isRoleChange ");
	bool ret;
	
	if (BpFtHandler::ROLE_CHANGE & mainChange)
		ret = true;
	else
		ret = false;
	mainChange &= ~(BpFtHandler::ROLE_CHANGE);

	logger.logMsg(TRACE_FLAG, 0, "isRoleCHange. Return = [%d]", (int)ret);
   LogTrace(0, "Leaving isRoleCHange");
   LogTrace(0, "Exiting isRoleChange ");
	return ret;
}

void
BpFtHandler::setConfiguredRole()
{
   LogTrace(0, "Entering setConfiguredRole");

// 	char host[100];
	
//    if (0 != gethostname(host, sizeof(host))) {
// 		logger.logMsg(ERROR_FLAG, 0, "gethostname call failure. [%s]",
// 						  strerror(errno));
// 		LogError(0, "Exiting");
// 		exit(1);
//    }

// 	logger.logMsg(TRACE_FLAG, 0, "Hostname = [%s]", host);

// 	struct hostent *phe;
//    if (NULL == (phe = gethostbyname(host))) {
// 		logger.logMsg(ERROR_FLAG, 0, "gethostbyname call failure. [%s]",
// 						  strerror(errno));
// 		LogError(0, "Exiting");
// 		exit(1);
//    }
	
// 	// Now loop thru all the IP addresses and see which of
// 	// primaryIp or secondaryIp are set
// 	bool match = false;
	
//    for (int i = 0; phe->h_addr_list[i] != 0; ++i) {
//       struct in_addr addr;
//       memcpy(&addr, phe->h_addr_list[i], sizeof(struct in_addr));
// 		char *ip = inet_ntoa(addr);

// 		if (0 == strcmp(ip, primaryIp.c_str())) {
// 			match = true;
// 			isPrimary = true;
// 			break;
// 		}
// 		else if (0 == strcmp(ip, secondaryIp.c_str())) {
// 			match = true;
// 			isPrimary = false;
// 			break;
// 		}
//    }

// 	if (false == match) {
// 		logger.logMsg(ERROR_FLAG, 0, "Neither of [%s] and [%s] [%s]",
// 						  primaryIp.c_str(), secondaryIp.c_str(),
// 						  "are configured in the network interfaces");
// 		LogError(0, "Exiting");
// 		exit(1);
//    }

	int ret = determineConfiguredRole(primaryIp, secondaryIp);

	if (1 == ret) {
		selfIpAddr = primaryIp;
		selfAgentIpAddr = primaryAgentIp;
		selfPort = primaryPort;

		peerIpAddr = secondaryIp;
		peerAgentIpAddr = secondaryAgentIp;
		peerPort = secondaryPort;
		LogAlways(0, "Configured Role = PRIMARY");
	}
	else {
		selfIpAddr = secondaryIp;
		selfPort = secondaryPort;
		selfAgentIpAddr = secondaryAgentIp;
		
		peerIpAddr = primaryIp;
		peerPort = primaryPort;
		peerAgentIpAddr = primaryAgentIp;
		LogAlways(0, "Configured Role = SECONDARY");
	}
	
	logger.logMsg(ALWAYS_FLAG, 0, "Self IP = [%s]", selfIpAddr.c_str());
	logger.logMsg(ALWAYS_FLAG, 0, "Self Agent IP = [%s]",
					  selfAgentIpAddr.c_str());
	logger.logMsg(ALWAYS_FLAG, 0, "Self Port = [%d]", selfPort);

	logger.logMsg(ALWAYS_FLAG, 0, "Peer IP = [%s]", peerIpAddr.c_str());
	logger.logMsg(ALWAYS_FLAG, 0, "Peer Agent IP = [%s]",
					  peerAgentIpAddr.c_str());
	logger.logMsg(ALWAYS_FLAG, 0, "Peer Port = [%d]", peerPort);
	
   LogTrace(0, "Leaving setConfiguredRole");
}
	
int
BpFtHandler::readConfig(const char *cfgFile)
{
   LogTrace(0, "Entering readConfig");

	ifstream inFile;

	inFile.open(cfgFile, ios::in);
	if (!inFile) {
		LogError(0, "Failed to open config file for reading");
		LogTrace(0, "Leaving readConfig");
		return -1;
	}

	char buf[500];
	
	while (inFile >> buf) {
		if (isComment(buf))
			continue;
		
		logger.logMsg(ALWAYS_FLAG, 0, "Read Line = [%s]", buf);

		char *param = NULL;
		char *value = NULL;
		char tmp[500];
		const char *sep = "=";
//		parseLine(buf, param, value);

		strcpy(tmp, buf);
		param = strtok(tmp, sep);
		value = strtok(NULL, sep);
		
		if (0 == strcmp("primarymachineip", param)) {
			this->primaryIp = value;
			logger.logMsg(ALWAYS_FLAG, 0, "Primary Machine IP = [%s]",
							  this->primaryIp.c_str());
		}
		else if (0 == strcmp("secondarymachineip", param)) {
			this->secondaryIp = value;
			logger.logMsg(ALWAYS_FLAG, 0, "Secondary Machine IP = [%s]",
							  this->secondaryIp.c_str());
		}
		else if (0 == strcmp("primaryport", param)) {
			this->primaryPort = atoi(value);
			logger.logMsg(ALWAYS_FLAG, 0, "Primary Port = [%d]",
							  this->primaryPort);
		}
		else if (0 == strcmp("secondaryport", param)) {
			this->secondaryPort = atoi(value);
			logger.logMsg(ALWAYS_FLAG, 0, "Secondary Port = [%d]",
							  this->secondaryPort);
		}
		else if (0 == strcmp("floatingip", param)) {
			this->floatingIpAddr = value;
			logger.logMsg(ALWAYS_FLAG, 0, "Floating IpAddress = [%s]",
							  this->floatingIpAddr.c_str());
		}
		else if (0 == strcmp("referenceip", param)) {
			this->refIpAddrs[numRefIps++] = value;
			logger.logMsg(ALWAYS_FLAG, 0, "Reference IpAddress(%d) = [%s]",
							  numRefIps, this->refIpAddrs[numRefIps-1].c_str());
		}
		else if (0 == strcmp("primaryagentip", param)) {
			this->primaryAgentIp = value;
			logger.logMsg(ALWAYS_FLAG, 0, "Primary Agent IpAddress = [%s]",
							  this->primaryAgentIp.c_str());
		}
		else if (0 == strcmp("secondaryagentip", param)) {
			this->secondaryAgentIp = value;
			logger.logMsg(ALWAYS_FLAG, 0, "Secondary Agent IpAddress = [%s]",
							  this->secondaryAgentIp.c_str());
		}
		else if (0 == strcmp("agentport", param)) {
			this->agentPort = atoi(value);
			logger.logMsg(ALWAYS_FLAG, 0, "Agent Port = [%d]",
							  this->agentPort);
		}

		else if (0 == strcmp("cmdtorun", param)) {
			while (0 == strcmp("cmdtorun", param)) {
				logger.logMsg(ALWAYS_FLAG, 0, "Command To Run = [%s]",
								  value);
				struct cmdNode *node = &(cmdNodes[numberCommands++]);
				node->cmdToRun = value;
				node->cmdArgs[node->cmdArgCount] = value;
				node->cmdArgCount ++;

				while (inFile >> buf) {
					if (isComment(buf))
						continue;
					//parseLine(buf, param, value);
					strcpy(tmp, buf);
					param = strtok(tmp, sep);
					value = strtok(NULL, sep);

					if (0 == strcmp("cmdarg", param)) {
						logger.logMsg(ALWAYS_FLAG, 0, "Command Arg = [%s]",
										  value);
						node->cmdArgs[node->cmdArgCount] = value;
						node->cmdArgCount ++;
					}
					else if (0 == strcmp("pidfile", param)) {
						logger.logMsg(ALWAYS_FLAG, 0, "PID File = [%s]",
										  value);
						node->pidFile = value;
					}
					else {
						break;
					}
					
				}
			}
		}
		 
		else if (0 == strcmp("pidToMonitor", param)) {
			logger.logMsg(ALWAYS_FLAG, 0, "PID to Monitor = [%s]",
         	value);
			struct cmdNode *node = &(cmdNodes[numberCommands++]);
			node->pidFile = value;

		}

		else if (0 == strcmp("installRoot", param)) {
         logger.logMsg(ALWAYS_FLAG, 0, "Install Root = [%s]",
            value);
         installRoot = value;

      }


		else {
			logger.logMsg(ALWAYS_FLAG, 0, "Kuch bakar hai... %s = %s !!!",
				param,value);
		}
		
// 		logger.logMsg(ALWAYS_FLAG, 0, "Read Line = [%s]", buf);
				

// 		else if (0 == strcmp("cmdtorun", param)) {
// 			this->cmdToRun = value;
// 			cmdArgs[cmdArgCount++] = value;
// 			logger.logMsg(ALWAYS_FLAG, 0, "Run Command = [%s]",
// 							  this->cmdToRun.c_str());
// 		}
// 		else if (0 == strcmp("cmdarg", param)) {
// 			cmdArgs[cmdArgCount++] = value;
// 			logger.logMsg(ALWAYS_FLAG, 0, "Run Command Arg = [%s]",
// 							  (cmdArgs[cmdArgCount-1]).c_str());
// 		}
// 		else if (0 == strcmp("pidfile", param)) {
// 			this->pidFile = value;
// 			logger.logMsg(ALWAYS_FLAG, 0, "PID File = [%s]",
// 							  this->pidFile.c_str());
// 		}
	}
			
   LogTrace(0, "Leaving readConfig");
	return 0;
}

bool
BpFtHandler::isComment(const char *buf)
{
	if (buf[0] == '#')
		return true;
	return false;
}

void
BpFtHandler::parseLine(const char *buf, char *&param, char *&value)
{
	if (buf[0] == ' ' || buf[0] == '\t') {
		logger.logMsg(ERROR_FLAG, 0, "Illegal line in config file = [%s]",
						  buf);
		LogError(0, "Exiting");
		exit(0);
	}

	char tmp[500];
	const char *sep = "=";

	strcpy(tmp, buf);
	param = strtok(tmp, sep);
   value = strtok(NULL, sep);

	if ((NULL == param) || (0 == strcmp("", param)) ||
       (NULL == value) || (0 == strcmp("", value))) {
		logger.logMsg(ERROR_FLAG, 0, "Illegal line in config file = [%s]",
						  buf);
		LogError(0, "Exiting");
		exit(0);
	}
}


void
BpFtHandler::setSignal()
{
	LogTrace(0, "Entering setSignal");
   sigignore(SIGPIPE);
   sigignore(SIGALRM);
	LogTrace(0, "Leaving setSignal");
}
	
// Fasih: Called to change the role...
// 
void 
BpFtHandler::changeRole(bool isPrimary)
{
	logger.logMsg(TRACE_FLAG, 0, "Updating the state. Setting myself as %d",isPrimary);
	BpIPAddrHandler::getInstance().setSelfMode(isPrimary);
	LogTrace(0, "Leaving changeRole");
}

// Used to make sure that nothing else happens when the main thread is in 
// Defunct state...
void
BpFtHandler::makeDefunct(){
	BpIPAddrHandler::getInstance().setSelfMode(false);
	LogTrace(0, "Shutdown the peer communications");
	if (NULL != this->peerHdlr)
		this->peerHdlr->shutdown();
	// Did not call updateRole as the update method is designed to be called by 
	// children... these methods acquire a lock on main which this thread is already holding....
	LogTrace(0, "Updating role to defunct");
	pthread_rwlock_wrlock(&roleLock);
	this->aRole = BpFtHandler::DEFUNCT;
	pthread_rwlock_unlock(&roleLock);

	LogTrace(0, "Updating peerStatus to DEFUNCT(NO_STATE)");
    pthread_rwlock_wrlock(&statusLock);
    this->oldPeerStatus = this->peerStatus;
    this->peerStatus = BpFtHandler::NO_STATE;
    pthread_rwlock_unlock(&statusLock);

	LogTrace(0, "Updating apache status to DEFUNCT(NO_STATE)");
    pthread_rwlock_wrlock(&statusLock);
    this->oldApacheStatus = this->apacheStatus;
    this->apacheStatus = BpFtHandler::NO_STATE;
    pthread_rwlock_unlock(&statusLock);
	
	LogTrace(0, "Role now defunct");
}

// Used to find out if the thread is in defunct state
bool
BpFtHandler::isDefunct() {
	return (_isDefunct != 0);
}

// Raise Alarm when required...
void
BpFtHandler::raiseAlarm(const char* errorCode,const char* content) {
	
	LogTrace(0, "Raise alarm");
	char cmd[200];
	
	sprintf(cmd,"%s/scripts/raiseAlarm -e %s -c \"%s\"",installRoot.c_str(),errorCode,content);
	system(cmd);
	logger.logMsg(TRACE_FLAG, 0, "%s returned with : %s",cmd, strerror(errno));
}


//BPInd17020 (Mayank)  - Added a predicate flag with initialised value as
// as true so that the first time it does not wait for the signal to come and 
// goes to action() and from next time onwards it is reset to 0  
BpFtHandler::BpFtHandler() :
        predicateFlag(1),	
	isPrimary(false),
	agentPort(0),
	numberCommands(0),
	apacheHdlr(NULL),
	peerHdlr(NULL),
	oldApacheStatus(BpFtHandler::NO_STATE),
	apacheStatus(BpFtHandler::NO_STATE),
	oldPeerStatus(BpFtHandler::NO_STATE),
	peerStatus(BpFtHandler::NO_STATE),
	state(BpFtHandler::STARTUP),
	aRole(-1),
	numRefIps(0),
	mainChange(0),
	_isDefunct(0)
{
	for (int i = 0; i < 100; i++) {
		cmdNodes[i].cmdArgCount = 0;
	}
}

BpFtHandler::~BpFtHandler()
{}
