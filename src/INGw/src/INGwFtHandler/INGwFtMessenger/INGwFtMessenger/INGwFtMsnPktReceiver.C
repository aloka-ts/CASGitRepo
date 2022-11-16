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
//     File:     INGwFtMsnPktReceiver.C
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

#include <netinet/in.h>

#include <EmsCommon/EmsConfigurationManager.h>

#include <INGwFtTalk/INGwFtTkInterface.h>

#include <INGwFtMessenger/INGwFtMsnPktReceiver.h>
#include <INGwFtMessenger/INGwFtMsnMessenger.h>

#include <INGwInfraManager/INGwIfrMgrFtIface.h>

#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>

#define CCM_MSG_BULK_COUNT 100

extern BpGenUtil::INGwIfrSmAppStreamer *ingwMsgStream;

//using namespace RSI_NSP_CCM;

INGwFtMsnPktReceiver::INGwFtMsnPktReceiver(INGwFtMsnCommMgr *ingwCommMgr, 
																					 INGwIfrMgrFtIface *ingwFtInf)
{
   msgQueue.setName("INGWReceiverUnUsedQueue");
   logger.logINGwMsg(false, ALWAYS_FLAG, 0, "INGW-INGW message receiver created.");

   _ingwCommMgr = ingwCommMgr;
   _ingwFtInf = ingwFtInf;

   EmsManager::EmsConfigurationManager &cfgMgr =
                             EmsManager::EmsConfigurationManager::getInstance();

   ObjectId selfId(atoi(cfgMgr.getParameter("-a").c_str()), 
                   BT_CCM_CCM_MESSENGER);

   _recvQueue = INGwFtTkInterface::instance()->registerMessageReceiver(selfId);

#if 0
   _verHolder = VersionMgr::getInstance().getVersionHolder(
                   VersionMgr::VER_CCM_MESSAGES_SER, VersionMgr::CCM_SUBSYSTEM);
   _verHolder->addSupportedVersion(1);
#endif

   start();
}

INGwFtMsnPktReceiver::~INGwFtMsnPktReceiver()
{
   INGwFtTkInterface::instance()->deregisterMessageReceiver(_recvQueue);
   stop(true);
}

void INGwFtMsnPktReceiver::execute()
{
   logger.logINGwMsg(false, ALWAYS_FLAG, 0, "INGW-INGW message receiver started.");

   INGwFtTkOutData *outData = new INGwFtTkOutData[CCM_MSG_BULK_COUNT];
   INGwFtTkInterface *bTalk = INGwFtTkInterface::instance();

   while(isRunnable())
   {
      int ret = 0;
      if((ret = _recvQueue->receive(outData, CCM_MSG_BULK_COUNT)) == -1)
      {
         logger.logINGwMsg(false, ERROR_FLAG, 0, "INGwTalk receive error.");
         break;
      }

      for(int idx = 0; idx < ret; idx++)
      {
         INGwFtTkOutData &currMsg = outData[idx];
         _processRecvMsg(currMsg.msg, currMsg.msgLen, currMsg.senderID, 
                         currMsg.version);
      }
   }

   delete []outData;
}

void INGwFtMsnPktReceiver::_processRecvMsg(char *msgBuffer, int len, 
                                          ObjectId &senderID, int version)
{
#if 0
   if(version != _verHolder->getRecvVersion(senderID.getComponentId()))
   {
      logger.logMsg(ERROR_FLAG, 0, "Version mismatch. Received [%d] "
                                   "Negotiated [%d]", version, 
                    _verHolder->getRecvVersion(senderID.getComponentId()));
      delete []msgBuffer;
      return;
   }
#endif

#if 0
//PANKAJ We will move this algo in INGW Mgr IMPT
   switch(version)
   {
      case 1:
      {
         int locVersion = 1;

         short msgType = 0;
         memcpy(&msgType, msgBuffer, sizeof(short));
         msgType = ntohs(msgType);
      
         INGwFtPktMsg *msg = NULL;
      
         switch(msgType)
         {
            case MSG_CALL_BACKUP:
            {
               msg = new BpCallDataMsg();
               if(msg->depacketize(msgBuffer, len, locVersion) == 0)
               {
                  delete []msgBuffer;
                  delete msg;

                  logger.logMsg(ERROR_FLAG, 0, 
                                "Depacketize failed. version [%d]", locVersion);
                  return;
               }
      
               //Call Data msg internally holds the ref of buffer. 
            }
            break;
      
            case MSG_FT_DELETE_CALL:
            {
               msg = new BpDeleteCallMsg();
               if(msg->depacketize(msgBuffer, len, locVersion) == 0)
               {
                  delete []msgBuffer;
                  delete msg;

                  logger.logMsg(ERROR_FLAG, 0, 
                                "Depacketize failed. version [%d]", locVersion);
                  return;
               }
      
               //Delete call internally holds the ref of buffer.
            }
            break;
      
            case MSG_CHANGE_SLEE:
            {
               msg = new BpChangeSLEEMsg();
               if(msg->depacketize(msgBuffer, len, locVersion) == 0)
               {
                  delete []msgBuffer;
                  delete msg;

                  logger.logMsg(ERROR_FLAG, 0, 
                                "Depacketize failed. version [%d]", locVersion);
                  return;
               }

               delete []msgBuffer;
            }
            break;

            case MSG_CCM_DYN_FT:
            {
               msg = new BpCCMDynFTMsg();
               if(msg->depacketize(msgBuffer, len, locVersion) == 0)
               {
                  delete []msgBuffer;
                  delete msg;

                  logger.logMsg(ERROR_FLAG, 0, 
                                "Depacketize failed. version [%d]", locVersion);
                  return;
               }

               delete []msgBuffer;
            }
            break;
      
            default:
            {
               logger.logINGwMsg(false, ERROR_FLAG, 0, "Unexpected msg type [%d] "
                                                     "from peerCCM [%d-%d].", 
                               msgType, senderID.getComponentId(), 
                               senderID.getSubComponentId());
               delete []msgBuffer;
               return;
            }
         }
      
         if(ingwMsgStream->isLoggable())
         {
            msg->markMsgSendRecvTime();
            ingwMsgStream->log("-> %s\n", msg->toLog().c_str());
         }
      
         _ingwFtInf->recvMsgFromCCM(msg);
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unsupported version [%d]", version);
         delete []msgBuffer;
         return;
      }
      break;
   };
#endif

   int locVersion = 1;
   _ingwFtInf->recvMsgFromPeerINGw(msgBuffer, len, locVersion);
   return;
}
