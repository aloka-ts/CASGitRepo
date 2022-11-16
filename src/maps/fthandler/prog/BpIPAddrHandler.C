#include <Util/Logger.h>
LOG("LoadBalancer");

#include <sstream>

#include <Util/BayPing.h>
#include <Util/imAlarmCodes.h>
#include <Util/BpTcpClient.h>

#include "BpFtHandler.h"
#include "BpIPAddrHandler.h"

BpIPAddrHandler *BpIPAddrHandler::me;

BpIPAddrHandler &
BpIPAddrHandler::getInstance()
{
	if (NULL == me)
		me = new BpIPAddrHandler();

	return *me;
}

BpIPAddrHandler::BpIPAddrHandler(void) : 
   mbIsPrimary(false), 
   miAgentPort(0)
{
	LogTrace(0, "Entering BpIPAddrHandler()");
	LogTrace(0, "Leaving BpIPAddrHandler()");
}

BpIPAddrHandler::~BpIPAddrHandler()
{
	LogTrace(0, "Entering ~BpIPAddrHandler()");
	LogTrace(0, "Leaving ~BpIPAddrHandler()");
}

int
BpIPAddrHandler::initialize()
{
	LogTrace(0, "Entering initialize");

	BpFtHandler &lb = BpFtHandler::getInstance();

	mSelfIPAddr = lb.getSelfIp();
	mSelfAgentIPAddr = lb.getSelfAgentIp();
	
	mPeerIPAddr = lb.getPeerIp();
	mPeerAgentIPAddr = lb.getPeerAgentIp();
	
	mFloatingIPAddr = lb.getFloatingIp();
	miAgentPort = lb.getAgentPort();
	// Fasih: Multiple interface support
	mRefIPAddrs = lb.getRefIps();
	mNumRefIps  = lb.getNumRefIps();
	_refIPs = new uint32_t[mNumRefIps];

	_peerIP = inet_addr(mPeerIPAddr.c_str());
	if (_peerIP == -1 ) {
      logger.logMsg(ERROR_FLAG, 0, "Invalid PeerIP = [%s]", 
		mPeerIPAddr.c_str());
      exit(1);
   }
	_peerIP = ntohl(_peerIP);
        //Changed the time out from 200ms to 1000ms (Mayank BPInd16695)	
	_peerMachStatus = BpMachPing::getInstance().getStatus(_peerIP, 1000);
	if (BpMachPing::NOT_AVAILABLE == _peerMachStatus) {
      LogError(0, "Peer IP not pingable. Ok.. I am the king ");
      //exit(1);
   }

	// Checking each interface by way of different ref ips....
	for (int i = 0; i < mNumRefIps; i++) {
		logger.logMsg(TRACE_FLAG, 0, "Trying to parse Reference IP(%d) = %s",i,mRefIPAddrs[i].c_str());
		_refIPs[i] = inet_addr(mRefIPAddrs[i].c_str());

		if (_refIPs[i] == -1) {
			logger.logMsg(ERROR_FLAG, 0, "Invalid RefIP(%d) = [%s]",
				i, mRefIPAddrs[i].c_str());
			exit(1);
		}

		_refIPs[i] = ntohl(_refIPs[i]);

	}
	isNetworkAvailable();

	pthread_rwlock_init(&alarm.lock, 0);
	alarm.isRaised = false;
	LogTrace(0,"Leaving Initialise");
	return 0;
}

BpMachPing::BpMachStatus
BpIPAddrHandler:: getPeerMachStatus()
{
//Changed the time out from 200ms to 1000ms (Mayank BPInd16695)
	_peerMachStatus = BpMachPing::getInstance().getStatus(_peerIP, 1000);
	return _peerMachStatus;
}

bool
BpIPAddrHandler::getSelfMode()
{
	return mbIsPrimary;
}

std::string
BpIPAddrHandler::getFloatingIPAddr()
{
	return mFloatingIPAddr;
}

// A loss of peer heartbeat tells us that there is a problem in the network..
// We ping the peer machine...followed by ref ip.. if we detect that the ref ip
// is not pingable we realise that the network is out.. in which case we start
// waiting on the network...
// we wait till the machine comes back again.... i.e. till (!isIsolated())
// now... if the peer is not pingable... we have to wait on the network again...
// this time we have to wait till the network goes down or the peer comes back..
// we do this as... we dont have a way to figure out the subnet outage ( the heartbeat
// which acted as a trigger is no more there... )... 
bool
BpIPAddrHandler::isNetworkAvailable()
{
	LogTrace(0, "Entering isNetworkAvailable");

	if (mPeerIPAddr.empty()) {
		//In non FT setup, no need to check for mach isolation.
		LogTrace(0, "Leaving isNetworkAvailable. Return TRUE");
		return true;
	}
	
	LogTrace(0, "Check if I am in network");
	if(isIsolated()) {
		LogTrace(0, "I am isolated.... raise an alarm and mark the thread as defunct");
		pthread_rwlock_wrlock(&alarm.lock);
		bool isRaised = alarm.isRaised;
		alarm.isRaised = true;
		pthread_rwlock_unlock(&alarm.lock);

		if ( !isRaised ) {
			LogTrace(0, "Raise the alarm");
			BpFtHandler::getInstance().raiseAlarm("610","Network is not available");
		} else {
			LogTrace(0, "No need to raise the alarm");
		}

		BpFtHandler::getInstance().updateState(BpFtHandler::DEFUNCT);	
		LogTrace(0, "Start monitoring for network up");
		waitOnNetwork();
		LogTrace(0, "Thread spawned...");
		LogTrace(0, "Leaving isNetworkAvailable. Return FALSE");
		return false;
	}
//Changed the time out from 100ms to 1000ms (Mayank BPInd16695)
   _peerMachStatus = BpMachPing::getInstance().getStatus(_peerIP, 1000);
	if (BpMachPing::NOT_AVAILABLE == _peerMachStatus) {
		LogError(0, "Peer IP not pingable. Take over the FIP");
		_changeExtIPAddr(true, mSelfAgentIPAddr);
		LogTrace(0, "Start pinging for network down");
		pingReferenceIPs();
		LogTrace(0, "Thread spawned...");
		LogTrace(0, "Leaving isNetworkAvailable. Return TRUE");
		return true;
	}

   if ((_peerMachStatus == BpMachPing::AVAILABLE) ||
       (_refMachStatus  == BpMachPing::AVAILABLE)) {
		logger.logMsg(TRACE_FLAG, 0, "Machine is within network. "
			"Can reach other machines.");
		LogTrace(0, "Leaving isNetworkAvailable. Return TRUE");
      return true;
   }

}

int BpIPAddrHandler::setSelfMode(bool abIsPrimary)
{
	LogTrace(0, "Entering setSelfMode");

   logger.logMsg(TRACE_FLAG, 0, "New Mode = [%d]", abIsPrimary);
   int result = 0;

   mbIsPrimary = abIsPrimary;

	if (mbIsPrimary) {
// 		if (!mPeerIPAddr.empty()) {
		if (!mPeerAgentIPAddr.empty()) {
         if (_peerMachStatus == BpMachPing::AVAILABLE) {
            result = _changeExtIPAddr(false, mPeerAgentIPAddr);
//             result = _changeExtIPAddr(false, mPeerIPAddr);
         }
         else {
            result = 1;
   			logger.logMsg(TRACE_FLAG, 0,
					"Peer machine not available to unset floating IP");
         }

         if (result != 0) {
   			logger.logMsg(ERROR_FLAG, 0,
					"UNSET on %s Failed", mPeerAgentIPAddr.c_str());
//    			logger.logMsg(ERROR_FLAG, 0,
// 					"UNSET on %s Failed", mPeerIPAddr.c_str());
			}
      }

      result = _changeExtIPAddr(true, mSelfAgentIPAddr);
//       result = _changeExtIPAddr(true, mSelfIPAddr);

      if (result != 0) {
			logger.logMsg(ERROR_FLAG, 0,
				"SET on %s Failed", mSelfAgentIPAddr.c_str());
// 			logger.logMsg(ERROR_FLAG, 0,
// 				"SET on %s Failed", mSelfIPAddr.c_str());

         logger.logMsg(ERROR_FLAG, 0, "Setting of floating IP failed "
         	"quitting.");
         sleep(1);
         exit(1);
      }
   }
   else {
      if (_changeExtIPAddr(false, mSelfAgentIPAddr) != 0) {
			logger.logMsg(ERROR_FLAG, 0,
				"UNSET on %s Failed", mSelfAgentIPAddr.c_str());
		}
//       if (_changeExtIPAddr(false, mSelfIPAddr) != 0) {
// 			logger.logMsg(ERROR_FLAG, 0,
// 				"UNSET on %s Failed", mSelfIPAddr.c_str());
// 		}
   }

	LogTrace(0, "Leaving setSelfMode");
   return result;
}

int
BpIPAddrHandler::shutdown(void)
{
	LogTrace(0, "Entering shutdown");

   _changeExtIPAddr(false, mSelfAgentIPAddr);
//    _changeExtIPAddr(false, mSelfIPAddr);

	LogTrace(0, "Leaving shutdown");
   return 0;
}

int
BpIPAddrHandler::_changeExtIPAddr(bool abMode, const std::string& arIPAddr)
{
	LogTrace(0, "Entering _changeExtIPAddr");

	if ((arIPAddr == mSelfIPAddr) && abMode) {
		LogTrace (0, "About to set FIP on the m/c");
		BpFtHandler::getInstance().raiseAlarm("611","fthandler taking over the FIP");
		BpFtHandler::getInstance().raiseAlarm("3604","Fthandler Setting Floating IP");
		LogTrace (0, "sending 3604 ");
                //Fthandler
	}	

   int result = 0;
   std::ostringstream cmd;

   if (true == abMode) {
      cmd << "SET " << mFloatingIPAddr;
   }
   else {
      cmd << "UNSET " << mFloatingIPAddr;
   }

   std::string command = cmd.str();

   result = _sendCommand(arIPAddr, miAgentPort, command);

   logger.logMsg(TRACE_FLAG, 0, "Result of command [%s] on machine [%s] is "
		"[%d]", command.c_str(), arIPAddr.c_str(), result);

	LogTrace(0, "Leaving _changeExtIPAddr");
   return result;
}

int
BpIPAddrHandler::_sendCommand(const std::string& arPeerIP, int aiPeerPort, 
                    				const std::string& arCmd)
{
	LogTrace(0, "Entering _sendCommand");

   logger.logMsg(ALWAYS_FLAG, 0, "Sending command [%s] to [%s:%d]", 
		arCmd.c_str(), arPeerIP.c_str(), aiPeerPort);

   int retry_count = 0;

   while (true) {
      BpTcpClient peerMachSysMon(arPeerIP.c_str(), aiPeerPort);

      if (peerMachSysMon.sendBuf(arCmd.c_str(), arCmd.length() + 1, 5) != 0) {
         logger.logMsg(ERROR_FLAG, 0, "Error sending command [%s] to [%s:%d]",
                       arCmd.c_str(), arPeerIP.c_str(), aiPeerPort);

			LogTrace(0, "Leaving _sendCommand");
         return -1;
      }

      char result[200];
      int resultLen = 0;
      memset(result, 0, 200);

      if (peerMachSysMon.recvBuf(result, 199, resultLen, 5) != 0) {
         logger.logMsg(ERROR_FLAG, 0, "Error receiving response for [%s] from "
				"[%s:%d]", arCmd.c_str(), arPeerIP.c_str(), aiPeerPort);

			LogTrace(0, "Leaving _sendCommand");
         return -1;
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Rcvd response [%s] [%d] for [%s] from "
			"[%s:%d]", result, resultLen, arCmd.c_str(),
			arPeerIP.c_str(), aiPeerPort);

      retry_count++;

      if (result[0] == '1') {
         break;
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Sysmon receives request but failed "
			"processing it. will retry [%d]", retry_count);

      if (retry_count < 5) {
         sleep(1);
         continue;
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Retry exhausted. Returning failure.");
		LogTrace(0, "Leaving _sendCommand");
      return -1;
   }

	LogTrace(0, "Leaving _sendCommand");
   return 0;
}


void *
_monitorNetwork(void *args) {

    BpFtHandler::getInstance().setSignal();
    pthread_detach(pthread_self());

    BpIPAddrHandler *hdlr = (BpIPAddrHandler*)(args);
    hdlr->_waitOnNetwork();

    return NULL;
}


void *
_monitorReferenceIPs(void *args) {
            
    BpFtHandler::getInstance().setSignal();
    pthread_detach(pthread_self());
        
    BpIPAddrHandler *hdlr = (BpIPAddrHandler*)(args);
    hdlr->_pingReferenceIPs(); 
    
    return NULL;
}   


int
BpIPAddrHandler::waitOnNetwork() {
   LogTrace(0, "Creating a thread to monitor for network....");

   pthread_t tmpThread;
   if (-1 == pthread_create(&(tmpThread), NULL,
      _monitorNetwork, (void *)this)) {
      LogError(0, "Failed to create monitor network thread");
      LogError(0, "Going to dead state");
      BpFtHandler::getInstance().
         updateState(BpFtHandler::DEAD);
      return -1;
   }

   LogTrace(0, "Leaving waitOnNetwork");
}

int     
BpIPAddrHandler::pingReferenceIPs() {
   LogTrace(0, "Creating a thread to ping reference ips....");
    
   pthread_t tmpThread;
   if (-1 == pthread_create(&(tmpThread), NULL,
      _monitorReferenceIPs, (void *)this)) {
      LogError(0, "Failed to create ping reference ip thread");
      LogError(0, "Going to dead state");
      BpFtHandler::getInstance().
         updateState(BpFtHandler::DEAD);
      return -1;
   }

   LogTrace(0, "Leaving pingReferenceIPs");
}


void
BpIPAddrHandler::_waitOnNetwork() {
	LogTrace(0, "Start monitoring network...");

	while ( 1 ) {
		LogTrace(0, "Checking network availablity");
		if(!isIsolated()) {
			LogTrace(0, "Network up...Changing the state to ACTIVE");
			BpFtHandler::getInstance().updateState(BpFtHandler::ACTIVE);
			pthread_rwlock_wrlock(&alarm.lock);
			alarm.isRaised = false;
			pthread_rwlock_unlock(&alarm.lock);
			BpFtHandler::getInstance().raiseAlarm("611","Network is available now");
			break;
		} 
		sleep(5);
	}

	LogTrace(0, "Leaving _waitOnNetwork");
}

void
BpIPAddrHandler::_pingReferenceIPs() {
    LogTrace(0, "Start pinging reference ips...");
	BpFtHandler::Status curPeerStatus;
	BpFtHandler::Status oldPeerStatus;

    while ( 1 ) {
        LogTrace(0, "Checking network availablity");
        if(isIsolated()) {
            LogTrace(0, "Network down...Changing the state to DEFUNCT");
			
			pthread_rwlock_wrlock(&alarm.lock);
			bool isRaised = alarm.isRaised;
			alarm.isRaised = true;
			pthread_rwlock_unlock(&alarm.lock);

			if ( !isRaised ) {
				LogTrace(0, "Raise the alarm");
				BpFtHandler::getInstance().raiseAlarm("610","Network is not available");
			} else {
				LogTrace(0, "Could not raise the alarm");
			}

            BpFtHandler::getInstance().updateState(BpFtHandler::DEFUNCT);
			LogTrace(0, "Start monitoring for network up");
			waitOnNetwork();
			LogTrace(0, "Thread spawned...");
			LogTrace(0, "Leaving _pingReferenceIPs...");
            break;
        } else {
			BpFtHandler::getInstance().getPeerStatus(oldPeerStatus, curPeerStatus);
			if(curPeerStatus == BpFtHandler::UP) {
				LogTrace(0, "The peer is up.... got to quit this loop");
				break;
			}
        }
        sleep(15);
    }

    LogTrace(0, "Leaving _pingReferenceIPs");
}


bool
BpIPAddrHandler::isIsolated() {
   // Fasih:Checking each interface by way of different ref ips....
   for (int i = 0; i < mNumRefIps; i++) {

		logger.logMsg(TRACE_FLAG, 0, "Trying to ping Reference IP(%d) = %s",i,mRefIPAddrs[i].c_str());
      //Changed the time out from 100ms to 1000ms (Mayank BPInd16695)
      _refMachStatus  = BpMachPing::getInstance().getStatus(_refIPs[i],  1000);

      if (BpMachPing::NOT_AVAILABLE == _refMachStatus) {
			logger.logMsg(ERROR_FLAG, 0, "Reference IP(%d) not pingable.",i);
			return true;
      }
   }
	return false;
}
