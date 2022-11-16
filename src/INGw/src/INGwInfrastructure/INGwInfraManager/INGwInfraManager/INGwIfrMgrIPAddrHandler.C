//*********************************************************************
//   GENBAND, Inc. Confidential and Proprietary
//
// This work contains valuable confidential and proprietary
// information.
// Disclosure, use or reproduction without the written authorization of
// GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
// is protected by the laws of the United States and other countries.
// If publication of the work should occur the following notice shall
// apply:
//
// "Copyright 2007 GENBAND, Inc.  All rights reserved."
//*********************************************************************
//********************************************************************
//
//     File:     INGwIfrMgrIPAddrHandler.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraManager");

using namespace std;

#include <Util/BayPing.h>
#include <Util/imAlarmCodes.h>
#include <Util/BpTcpClient.h>

#ifndef INGW_SET_FLOAT_IP_FAILED
#define INGW_SET_FLOAT_IP_FAILED        10005
#endif

#ifndef INGW_UNSET_FLOAT_IP_FAILED
#define INGW_UNSET_FLOAT_IP_FAILED        10006
#endif

#include <INGwTcapProvider/INGwTcapProvider.h>
#include <INGwStackManager/INGwSmWrapper.h>
#include <INGwStackManager/INGwSmBlkConfig.h>
#include <INGwInfraManager/INGwIfrMgrIPAddrHandler.h>

#include "INGwInfraParamRepository/INGwIfrPrParamRepository.h"
#include "INGwInfraManager/INGwIfrMgrAlarmMgr.h"

#include <sstream>

using namespace RSI_NSP_CCM;

INGwIfrMgrIPAddrHandler::INGwIfrMgrIPAddrHandler(void) : 
   mbIsPrimary(false), 
   miAgentPort(0)
{
   LogINGwTrace(false, 0, "IN INGwIfrMgrIPAddrHandler()");
   LogINGwTrace(false, 0, "OUT INGwIfrMgrIPAddrHandler()");
}

INGwIfrMgrIPAddrHandler::~INGwIfrMgrIPAddrHandler()
{
   LogINGwTrace(false, 0, "IN ~INGwIfrMgrIPAddrHandler()");
   LogINGwTrace(false, 0, "OUT ~INGwIfrMgrIPAddrHandler()");
}

void INGwIfrMgrIPAddrHandler::initialize(bool abIsPrimary) throw (INGwIfrUtlReject)
{
   LogINGwTrace(false, 0, "IN initialize");

   INGwIfrPrParamRepository& paramRep = INGwIfrPrParamRepository::getInstance();
   string mPeerIPAddr     = paramRep.getPeerIPAddr();

   try 
   {
      mSelfIPAddr     = paramRep.getSelfIPAddr();
      miAgentPort     = paramRep.getAgentPort();
      mRefIPAddr      = paramRep.getValue(INGW_NW_REF_IP_ADDRESS);

      if(!mPeerIPAddr.empty())
      {
         _refIP  = inet_addr(mRefIPAddr.c_str());

         if(_refIP == -1)
         {
            logger.logMsg(ALWAYS_FLAG, 0, "Invalid IP. PeerIP [%s] Ref IP[%s].",
                          mPeerIPAddr.c_str(), mRefIPAddr.c_str());
            exit(1);
         }

         _refIP = ntohl(_refIP);

         _refMachStatus  = INGwIfrUtlMachPing::getInstance().getStatus(_refIP,  2000);
      }
      else
      {
         _refIP = 0;
         _refMachStatus  = INGwIfrUtlMachPing::NOT_AVAILABLE;
      }
   }
   catch(...) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "Parameters for INGwIfrMgrIPAddrHandler not avl");

      LogINGwTrace(false, 0, "OUT initialize");
      _THROW_REJECT("INIT failed")
   }

   logger.logMsg(ALWAYS_FLAG, 0, "INGwIfrMgrIPAddrHandler initialized. Self [%s] peer "
                                 "[%s] floatingIP [%s] RefIP [%s] SysMon port "
                                 "[%d]", mSelfIPAddr.c_str(), 
                 mPeerIPAddr.c_str(), getFloatingIPAddr().c_str(), 
                 mRefIPAddr.c_str(), miAgentPort);
                                  
   int result = setSelfMode(abIsPrimary);

   if(0 != result) 
   {
      LogINGwTrace(false, 0, "OUT initialize");
      _THROW_REJECT("INIT failed")
   }

   LogINGwTrace(false, 0, "OUT initialize");
}

bool INGwIfrMgrIPAddrHandler::getSelfMode()
{
   return mbIsPrimary;
}

string INGwIfrMgrIPAddrHandler::getFloatingIPAddr()
{
   INGwIfrPrParamRepository& paramRep = INGwIfrPrParamRepository::getInstance();
   return paramRep.getValue(ingwFLOATING_IP_ADDR);
}

bool INGwIfrMgrIPAddrHandler::isNetworkAvailable()
{
   INGwIfrPrParamRepository& paramRep = INGwIfrPrParamRepository::getInstance();
   string locPeerAddr = paramRep.getPeerIPAddr();

   if(locPeerAddr.empty())
   {
      //In non FT setup, no need to check for mach isolation.
      // If localPeer is empty then check with the referenceIp. In N+1
      // Standby will not have the local peer so we need to check
      // with the reference ip.
      if(paramRep.getOperationMode() == NPlusOne) {
        locPeerAddr = mRefIPAddr;
        logger.logMsg(ALWAYS_FLAG, 0, "PeerIP is set to Ref IP[%s].",
                      locPeerAddr.c_str());
      } else {
        return true;
      }
   }

   int locPeer = 0;
   int _locPeerStatus = 0;

   locPeer = ntohl(inet_addr(locPeerAddr.c_str()));
   _refIP  = ntohl(inet_addr(mRefIPAddr.c_str()));

   if(locPeer == -1 || _refIP == -1)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Invalid IP. PeerIP [%s] Ref IP[%s].",
                    locPeerAddr.c_str(), mRefIPAddr.c_str());
      exit(1);
   }

   _locPeerStatus = INGwIfrUtlMachPing::getInstance().getStatus(locPeer, 100);
   _refMachStatus = INGwIfrUtlMachPing::getInstance().getStatus(_refIP,  100);

   logger.logMsg(ALWAYS_FLAG, 0, "Ping status. PeerIP[%s] status[%d], "
                 "RefIP[%s] status[%d]",
                 locPeerAddr.c_str(), _locPeerStatus,
                 mRefIPAddr.c_str(), _refMachStatus);

   if((_locPeerStatus == INGwIfrUtlMachPing::AVAILABLE) ||
      (_refMachStatus == INGwIfrUtlMachPing::AVAILABLE))
   {
      logger.logMsg(ALWAYS_FLAG, 0, "INGW machine is within network. "
                                    "Can reach other machines.");
      return true;
   }

   logger.logMsg(ERROR_FLAG, 0, "Machine [%s] isolated.", mSelfIPAddr.c_str());

   _changeExtIPAddr(false, mSelfIPAddr);

   logger.logMsg(ALWAYS_FLAG, 0, "INGW isolated. Quitting deliberately.");
   printf("INGW isolated. Quitting deliberately.\n");
   exit(1);

   return false;
}

int INGwIfrMgrIPAddrHandler::setSelfMode(bool abIsPrimary)
{
   LogINGwTrace(false, 0, "IN setSelfMode");

   int result = 0;

   mbIsPrimary = abIsPrimary;

   INGwIfrPrParamRepository& paramRep = INGwIfrPrParamRepository::getInstance();
   string mPeerIPAddr     = paramRep.getPeerIPAddr();

   if(mbIsPrimary) 
   {
      if(!mPeerIPAddr.empty())
      {
				result = _changeExtIPAddr(false, mPeerIPAddr);

        bool isIngwFtMode = INGwSmBlkConfig::getInstance().getMode();
				if((result != 0) && (isIngwFtMode))
        {
          INGwSmWrapper *wrapper = INGwTcapProvider::getInstance().getSmWrapperPtr();
          int relayChannelRole = INGwSmBlkConfig::getInstance().getRelayChannelRole();
          std::string lstrPeerIp = "";
          if (relayChannelRole == 1) {
            lstrPeerIp = wrapper->relayClientIp;
          }
          else if (relayChannelRole == 2) {
            lstrPeerIp = wrapper->relayServerIp;
          }

          if ( (!lstrPeerIp.empty()) && ((lstrPeerIp != mPeerIPAddr))) {
				    result = _changeExtIPAddr(false, lstrPeerIp);
				    if(result != 0) {
              logger.logMsg(ALWAYS_FLAG, 0,
                     "setSelfMode(): relayChannelRole<%d> UnSetting of "
                     "floating IP on peer failed through "
                     "Relay Interface<%s> also.", relayChannelRole, lstrPeerIp.c_str());
            }
          }
          else {
            logger.logMsg(ALWAYS_FLAG, 0, "Not sending UNSET cmd on Relay Intf "
                          "either it is same or not known. RelayPeerIp<%s> PeerIp<%s>",
                          lstrPeerIp.c_str(), mPeerIPAddr.c_str());
          }
        }

				// This alarm is being commented for following reasons:
				// 1. If the UNSET would fail because of Peer host isolation,
				//     the peer INGW would itself unset FIP from its host and go down.
				// 2. If the Peer host gets isolated just after the INGW on that host 
				//     crashed, the FIP on that host shall get UNSET when the INGW
				//     on that host would be restarted by SysMon. Pleae note that 
				//     there might be a window of time between the peer host being
				//     available on the Network AND the INGW on that host getting 
				//     restarted by SysMon. Within that time, it is possible that 
				//     FIP might be available in more than one INGW hosts.
				/*
				if(result != 0)
         {
            INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                                                  __FILE__, __LINE__, 
                        INGW_UNSET_FLOAT_IP_FAILED, "INGW", 
                        INGwIfrPrParamRepository::getInstance().getSelfId(), 
                        "UNSET on %s failed", mPeerIPAddr.c_str());
         }
				 */
      }

      result = _changeExtIPAddr(true, mSelfIPAddr);

      if(result != 0)
      {
         INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                                               __FILE__, __LINE__, 
                     INGW_SET_FLOAT_IP_FAILED, "INGW", 
                     INGwIfrPrParamRepository::getInstance().getSelfId(), 
                     "SET on %s failed", mSelfIPAddr.c_str());

         logger.logMsg(ALWAYS_FLAG, 0, "Setting of floating IP failed "
                                       "quitting.");
         printf("Setting of floating IP failed quitting.\n");
         sleep(1);
         exit(1);
      }
   }
   else
   {
      if(_changeExtIPAddr(false, mSelfIPAddr) != 0)
      {
         INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                                               __FILE__, __LINE__, 
                     INGW_UNSET_FLOAT_IP_FAILED, "INGW", 
                     INGwIfrPrParamRepository::getInstance().getSelfId(), 
                     "UNSET on %s failed", mSelfIPAddr.c_str());
      }
   }

   LogINGwTrace(false, 0, "OUT setSelfMode");
   return result;
}

int INGwIfrMgrIPAddrHandler::shutdown(void)
{
   logger.logMsg(ALWAYS_FLAG, 0, "IN shutdown()");

   _changeExtIPAddr(false, mSelfIPAddr);

   logger.logMsg(ALWAYS_FLAG, 0, "OUT shutdown()");
   return 0;
}

int INGwIfrMgrIPAddrHandler::_changeExtIPAddr(bool abMode, const string& arIPAddr)
{
   LogINGwTrace(false, 0, "IN _changeExtIPAddr");

   int result = 0;
   ostringstream cmd;

   string floatingIP = getFloatingIPAddr();

   if(floatingIP.empty())
   {
      if(abMode)
      {
         return -1;
      }

      return 0;
   }

   if(true == abMode) 
   {
      cmd << "SET " << getFloatingIPAddr();
   }
   else 
   {
      cmd << "UNSET " << getFloatingIPAddr();
   }

   string command = cmd.str();

   result = _sendCommand(arIPAddr, miAgentPort, command);

   //sleep(2); //Dont know why to sleep. -Suriya.

   logger.logMsg(VERBOSE_FLAG, 0, "Result of command [%s] on machine [%s] is "
                                  "[%d]", 
                 command.c_str(), arIPAddr.c_str(), result);

   LogINGwTrace(false, 0, "OUT _changeExtIPAddr");
   return result;
}

int INGwIfrMgrIPAddrHandler::changeExtIPAddr(bool abMode, const string &floatingIP, 
                                     const string& arIPAddr)
{
   LogINGwTrace(false, 0, "IN changeExtIPAddr");

   int result = 0;
   ostringstream cmd;

   if(true == abMode) 
   {
      cmd << "SET " << floatingIP;
   }
   else 
   {
      cmd << "UNSET " << floatingIP;
   }

   string command = cmd.str();

   INGwIfrPrParamRepository& paramRep = INGwIfrPrParamRepository::getInstance();
   int miAgentPort     = paramRep.getAgentPort();

   result = _sendCommand(arIPAddr, miAgentPort, command);

   logger.logMsg(VERBOSE_FLAG, 0, "Result of command [%s] on machine [%s] is "
                                  "[%d]", 
                 command.c_str(), arIPAddr.c_str(), result);

   LogINGwTrace(false, 0, "OUT changeExtIPAddr");
   return result;
}

int INGwIfrMgrIPAddrHandler::_sendCommand(const string& arPeerIP, int aiPeerPort, 
                                  const string& arCmd)
{
   LogINGwTrace(false, 0, "IN _sendCommand");
   logger.logMsg(ALWAYS_FLAG, 0, "Sending command [%s] to [%s:%d]", 
                 arCmd.c_str(), arPeerIP.c_str(), aiPeerPort);

   char lpcTime[64];
   memset(lpcTime, 0, sizeof(lpcTime));
   lpcTime[0] = '1';
   g_getCurrentTime(lpcTime);
   printf("[+INC+] INGwIfrMgrIPAddrHandler::_sendCommand(): %s "
          "Sending command [%s] to [%s:%d]\n",
          lpcTime, arCmd.c_str(), arPeerIP.c_str(), aiPeerPort); fflush(stdout);

   int retry_count = 0;

   int readWait = 5;
   if(arCmd.find(arPeerIP) != -1)
   {
      //Case of unsetting IP using the IP. In this case we wont get the 
      //response. -Suriya.
      readWait = 1;
   }

   while(true)
   {
      BpTcpClient peerMachSysMon(arPeerIP.c_str(), aiPeerPort);

      if(peerMachSysMon.sendBuf(arCmd.c_str(), arCmd.length() + 1, 5) != 0)
      {
         logger.logMsg(ERROR_FLAG, 0, "Error sending command [%s] to [%s:%d]",
                       arCmd.c_str(), arPeerIP.c_str(), aiPeerPort);

         memset(lpcTime, 0, sizeof(lpcTime));
         lpcTime[0] = '1';
         g_getCurrentTime(lpcTime);
         printf("[+INC+] INGwIfrMgrIPAddrHandler::_sendCommand(): %s "
                "Error sending command [%s] to [%s:%d]\n",
                lpcTime, arCmd.c_str(), arPeerIP.c_str(),
                aiPeerPort); fflush(stdout);
         

         LogINGwTrace(false, 0, "OUT _sendCommand");
         return -1;
      }

      char result[200];
      int resultLen = 0;
      memset(result, 0, 200);

      if(peerMachSysMon.recvBuf(result, 199, resultLen, readWait) != 0)
      {
         logger.logMsg(ERROR_FLAG, 0, "Error receiving response for [%s] from "
                                      "[%s:%d]",
                       arCmd.c_str(), arPeerIP.c_str(), aiPeerPort);

         memset(lpcTime, 0, sizeof(lpcTime));
         lpcTime[0] = '1';
         g_getCurrentTime(lpcTime);
         printf("[+INC+] INGwIfrMgrIPAddrHandler::_sendCommand(): %s "
                "Error receiving response for [%s] from [%s:%d]\n",
                lpcTime, arCmd.c_str(), arPeerIP.c_str(),
                aiPeerPort); fflush(stdout);
         

         LogINGwTrace(false, 0, "OUT _sendCommand");
         return -1;
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Received response [%s] [%d] for [%s] from "
                                    "[%s:%d]", result, resultLen, arCmd.c_str(),
                    arPeerIP.c_str(), aiPeerPort);

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] INGwIfrMgrIPAddrHandler::_sendCommand(): %s "
             "Received response [%s] [%d] for [%s] from [%s:%d]\n",
             lpcTime, result, resultLen, arCmd.c_str(), arPeerIP.c_str(),
             aiPeerPort); fflush(stdout);
      

      retry_count++;

      if(result[0] == '1')
      {
         break;
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Sysmon receives request but failed "
                                    "processing it. will retry [%d]", 
                    retry_count);

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] INGwIfrMgrIPAddrHandler::_sendCommand(): %s "
             "Sysmon receives request but failed processing it. "
             "will retry [%d]\n",
             lpcTime, retry_count); fflush(stdout);
      

      if((*(arCmd.c_str()) == 'U') && 
         (strstr(arCmd.c_str(), arPeerIP.c_str()) != NULL))
      {
         logger.logMsg(ALWAYS_FLAG, 0, 
                       "Sysmon failed to UNSET IP using same IP[%s]",
                       arCmd.c_str());
         return -1;
      }

      if(retry_count < 5)
      {
         sleep(1);
         continue;
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Retry exhausted. Returning failure.");

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] INGwIfrMgrIPAddrHandler::_sendCommand(): %s "
             "Retry exhausted. Returning failure.\n", lpcTime); fflush(stdout);
      
      LogINGwTrace(false, 0, "OUT _sendCommand");
      return -1;
   }

   LogINGwTrace(false, 0, "OUT _sendCommand");
   return 0;
}
