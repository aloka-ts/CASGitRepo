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
//     File:     INGwFtMsnPktDispatcher.C
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtMessenger");

#include <Util/ObjectId.h>
#include <unistd.h>

#include <EmsCommon/EmsConfigurationManager.h>
#include <INGwFtTalk/INGwFtTkInterface.h>

#include <INGwFtMessenger/INGwFtMsnPktDispatcher.h>
#include <INGwFtMessenger/INGwFtMsnCommMgr.h>
#include <INGwFtMessenger/INGwFtMsnMessenger.h>

#include <INGwFtPacket/INGwFtPktMsg.h>

#if 0
#include <VersionMgr/VersionMgr.h>
#endif

#include <INGwInfraManager/INGwIfrMgrFtIface.h>
#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>

//using namespace RSI_NSP_CCM;

extern BpGenUtil::INGwIfrSmAppStreamer *ingwMsgStream;

INGwFtMsnPktDispatcher::INGwFtMsnPktDispatcher(INGwFtMsnCommMgr *ingwCommMgr, 
																							 INGwIfrMgrFtIface *ingwFtInf,
													                     int aPeerId)
{
   _peerId = aPeerId;
   _ingwFtInf = ingwFtInf;
   _ingwCommMgr = ingwCommMgr;
   _connHandle = NULL;
   _errorFlag = false;
   _sendVersion = 0;

   msgQueue.registerCleanupFunc(cleanupUnprocessedMsg);

   char data[100];
   sprintf(data, "PeerINGW[%d]-Dispatcher", _peerId);
   msgQueue.setName(data);
}

INGwFtMsnPktDispatcher::~INGwFtMsnPktDispatcher()
{
   if(_connHandle)
   {
      delete _connHandle;
   }
}

void INGwFtMsnPktDispatcher::initConnection()
{
   EmsManager::EmsConfigurationManager &cfgMgr = 
                             EmsManager::EmsConfigurationManager::getInstance();

   ObjectId peer(_peerId, BT_CCM_CCM_MESSENGER);
   ObjectId self(atoi(cfgMgr.getParameter("-a").c_str()), BT_CCM_CCM_MESSENGER);

   int errCode = 0;
   _connHandle = INGwFtTkInterface::instance()->openConnection(self, peer);

   if(_connHandle)
   {
      _connHandle->setConnectionHandler(this);
      _ingwFtInf->ingwConnected(_peerId);
			_sendVersion = 1;

#if 0
      _sendVersion = VersionMgr::getInstance().getVersionHolder(
                        VersionMgr::VER_CCM_MESSAGES_SER, 
                        VersionMgr::CCM_SUBSYSTEM)->getSendVersion(_peerId);
#endif
   }

   start();
}

void INGwFtMsnPktDispatcher::execute()
{
   if((_connHandle == NULL) ||
      (_connHandle->getConnState().getLinkState() == 
                                                  INGwFtTkConnState::DISCONNECTED) ||
      (_connHandle->getConnState().getLinkState() == 
                                                   INGwFtTkConnState::DISCONNECTING))
   {
      logger.logMsg(ERROR_FLAG, 0, "Connection not established to INGW [%d]",
                    _peerId);
      connectionUpdate(0, INGwFtTkConnState::DISCONNECTED);
   }


   QueueData  *data = new QueueData[5];
   INGwFtTkMessageBuf *messages = new INGwFtTkMessageBuf[5];

   INGwFtTkInterface *ingwFtTalk = INGwFtTkInterface::instance();

   int sleepTime = 10;
   int dequeueCount = 5;

   while(!msgQueue.isQueueStopped())
   {
      int ret = msgQueue.eventDequeueBlk(data, dequeueCount, sleepTime, true);

      if(ret == 0)
      {
         dequeueCount = 1;
         sleepTime = 0;

         continue;
      }

      dequeueCount = 5;
      sleepTime = 10;

      unsigned char opCode = 1;
      unsigned int dataStructureId = 1;
      int no_messages = ret;

      for(int idx = 0; idx < no_messages; idx++)
      {
         messages[idx].msg = data[idx].data;
         messages[idx].len = data[idx].length;
      }

      if(-1 == ingwFtTalk->sendMessageMultiMsg(_connHandle, opCode, dataStructureId,
                                          messages, no_messages, _sendVersion))
      {
         logger.logMsg(ERROR_FLAG, 0, "Error sending message over BTalk.");
      }

      for(int idx = 0; idx < no_messages; idx++)
      {
         delete [] ((char *) messages[idx].msg);
      }
   }

   delete []messages;
   delete []data;

   if(_connHandle != NULL)
   {
      ingwFtTalk->closeConnection(_connHandle);
   }

   if(_errorFlag)
   {
      //reports error.
      _ingwFtInf->handleINGwFailure(_peerId);
   }

   return;
}

void INGwFtMsnPktDispatcher::sendMsg(INGwFtPktMsg *msg)
{
   int numTries = 0;
   while((_sendVersion == 0) && (numTries <= 100))
   {
      logger.logMsg(TRACE_FLAG, 0, "No negotiated version to send msg. [%s]",
                    msg->toLog().c_str());

      logger.logMsg(TRACE_FLAG, 0, "Going to negotiate a sending version .. %d",
                                                                      numTries);
      _sendVersion = 1;

#if 0
      _sendVersion = VersionMgr::getInstance().getVersionHolder(
                        VersionMgr::VER_CCM_MESSAGES_SER, 
                        VersionMgr::CCM_SUBSYSTEM)->getSendVersion(_peerId);
#endif
      sleep(1);
      ++numTries;
   }

   // BPInd11133 - STARTS.
   // There is a scenario where the network delay is so large that version 
   // has still not been negotiated even after 100 tries. So set the version 1.
   if (100 == numTries) {
      logger.logMsg(TRACE_FLAG, 0, "Version could not be negotiated even after "
                    "100 tries. Hardcoding the version to 1.");
      _sendVersion = 1;
   }
   // BPInd11133 - ENDS.

   QueueData inData;
   inData.data = NULL;

   switch(msg->getMsgType())
   {
      case MSG_UPDATE_TCAP_SESSION:
      case MSG_CREATE_TCAP_SESSION:
      case MSG_TCAP_CALL_SEQ_ACK:
      case MSG_CLOSE_TCAP_SESSION:
      case MSG_HANDLE_SAS_HB_FAILURE:
      case MSG_HANDLE_SYNCH_UP:
      case MSG_CALL_BACKUP:
      case MSG_CHANGE_SLEE:
      case MSG_FT_DELETE_CALL:
      case MSG_CCM_DYN_FT:
      case MSG_FT_ROLE_NEGOTIATION:
			case MSG_LOAD_DIST_MSG:
			case MSG_TCAP_DLG_INFO:
      case MSG_TCAP_CALL_DATA:
			case MSG_STACK_CONFIG:
			case MSG_ADD_LINK:
			case MSG_DEL_LINK:
			case MSG_ADD_LINKSET:
			case MSG_DEL_LINKSET:
			case MSG_ADD_NW:
			case MSG_DEL_NW:
			case MSG_ADD_ROUTE:
			case MSG_DEL_ROUTE:
			case MSG_ADD_LOCAL_SSN:
			case MSG_DEL_LOCAL_SSN:
			case MSG_ADD_USER_PART:
			case MSG_DEL_USER_PART:
			case MSG_M3UA_ASSOC_DOWN:
			case MSG_M3UA_ASSOC_UP:
			case MSG_ADD_PS:
			case MSG_DEL_PS:
			case MSG_ADD_ENDPOINT:
			case MSG_DEL_ENDPOINT:
			case MSG_ADD_PSP:
			case MSG_DEL_PSP:
			case MSG_ADD_ADDRMAP:
			case MSG_ADD_RULE:
			case MSG_DEL_ADDRMAP:
			case MSG_DEL_RULE:
			case MSG_MOD_LINK:
			case MSG_MOD_LINKSET:
			case MSG_MOD_PS:
			case MSG_ASP_ACTV:
			case MSG_ASP_INACTV:
			case MSG_CONFIG_STATUS:
      {
         inData.length = msg->packetize((char **)(&inData.data), _sendVersion);

         if(inData.data == NULL)
         {
            logger.logMsg(ERROR_FLAG, 0, "No packetization done. [%s]",
                          msg->toLog().c_str());
            return;
         }
      }
      break;
      default:
      {
         msg->markMsgSendRecvTime();
         logger.logINGwMsg(false, ERROR_FLAG, 0, "Unexpected Msg [%s] for INGW.", 
                         msg->toLog().c_str());
         return;
      }
   }

   int queueSize = msgQueue.queueSize();

   if(queueSize > 1000)
   {
      logger.logMsg(ERROR_FLAG, 0, "INGW Queue [%d] Slow response.", queueSize);
   }

   if(msgQueue.eventEnqueueBlk(&inData, 1) != 1)
   {
      cleanupUnprocessedMsg(inData);
      return;
   }

   if(ingwMsgStream->isLoggable())
   {
      msg->markMsgSendRecvTime();
      ingwMsgStream->log("<- %s\n", msg->toLog().c_str());
   }
}

void INGwFtMsnPktDispatcher::connectionUpdate(unsigned int peerID,
                                             INGwFtTkConnState::LinkState connState)
{
   if((connState == INGwFtTkConnState::DISCONNECTED) ||
      (connState == INGwFtTkConnState::DISCONNECTING))
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Connection to INGW [%d] is down",
                      _peerId);

      INGwFtTkInterface::instance()->closeConnection(_connHandle);
      _errorFlag = true;

      _ingwCommMgr->remove(_peerId);
   }
}

void INGwFtMsnPktDispatcher::cleanupUnprocessedMsg(QueueData data)
{
   delete [] ((char *) data.data);
}

