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
//     File:     INGwSpSipStackIntfLayer.C
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipProvider/INGwSpSipStackIntfLayer.h>

#include <INGwSipProvider/INGwSpSipContext.h>

#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwSipProvider/INGwSpSipProviderConfig.h>
#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipCall.h>
#include <INGwSipProvider/INGwSpStackConfigMgr.h>

#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>

#include <Util/SchedulerMsg.h>
#include <stdlib.h>

#include <INGwSipProvider/INGwSpBufferFactory.h>
#include <INGwSipProvider/INGwSpTcpConnMgr.h>

#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>

#include <INGwInfraMsrMgr/MsrMgr.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>


extern BpGenUtil::INGwIfrSmAppStreamer *sipMsgStream;

int INGwSpSipStackIntfLayer::miRetransTime = 0;
unsigned long long* INGwSpSipStackIntfLayer::mplSipRetransCount = NULL;

#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>
#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>

#include <INGwSipProvider/INGwSpSipCallController.h>

bool simSTHack = false;

#include <string>

long gllowThreadId = 0;
long glHiThreadId  = 0;

//const std::string glbDummyCallie("dummy_callie");
//INGwSpSipStackIntfLayer* INGwSpSipStackIntfLayer::mpSelf = NULL;
//
//extern Sdf_ty_bool glbProfileSupportedTimer;

const std::string glbDummyCallie("dummy_callie");
INGwSpSipStackIntfLayer* INGwSpSipStackIntfLayer::mpSelf = NULL;

extern Sdf_ty_bool glbProfileSupportedTimer;

void handleTimeout(INGwSipTimerType *aMsg, unsigned int aTimerid);

void indicateTimeoutToCall(INGwSipTimerContext *aContext, unsigned int aTimerid);

Sdf_ty_retVal handleNotifyRequest(Sdf_st_callObject **ppCallObj,
																	Sdf_st_eventContext *pEventContext,
																	Sdf_st_overlapTransInfo *pOverlapTxn,
																	Sdf_st_error *pErr);

Sdf_ty_retVal handleOptionsRequest(Sdf_st_callObject **ppCallObj,
																	Sdf_st_eventContext *pEventContext,
																	Sdf_st_overlapTransInfo *pOverlapTxn,
																	Sdf_st_error *pErr);

INGwSpSipStackIntfLayer& INGwSpSipStackIntfLayer::instance()
{
  if(!mpSelf)
    mpSelf = new INGwSpSipStackIntfLayer;
  return *mpSelf;
} // end of instance

INGwSpSipStackIntfLayer::INGwSpSipStackIntfLayer()
{
  LogINGwTrace(false, 0, "IN INGwSpSipStackIntfLayer");

  glbProfileSupportedTimer = Sdf_co_false;

  LogINGwTrace(false, 0, "OUT INGwSpSipStackIntfLayer");
} // end of constructor

void
INGwSpSipStackIntfLayer::initialize(void)
{
#ifndef DO_NOT_USE_BUFFER_POOL
  INGwIfrMgrThreadMgr& l_bpCcmTm = INGwIfrMgrThreadMgr::getInstance();
  int l_thrCnt = l_bpCcmTm.getThreadCount();

  INGwSpBufferFactory::initialize(1000, MAX_SIPBUF_SIZE + 1);
#endif

  if(getenv("SIM_SESSION_REFRESHER_HACK"))
    simSTHack = true;

  miRetransTime = 500;
  char *lpcRetransTime = getenv("INGW_SIP_RETRANS_TIME");

  if(NULL != lpcRetransTime)
  {
    miRetransTime = atoi(lpcRetransTime);

    if(miRetransTime < 500)
    {
      miRetransTime = 500;
    }
  }

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"INGW_SIP_RETRANS_TIME <%d> "
              ,miRetransTime); 


}

char*
INGwSpSipStackIntfLayer::getBuffer()
{
#ifdef DO_NOT_USE_BUFFER_POOL
  return new char[MAX_SIPBUF_SIZE + 1];
#else
  return INGwSpBufferFactory::getInstance().getNewObject();
#endif
}

void
INGwSpSipStackIntfLayer::reuseBuffer(char* apBuffer)
{
#ifdef DO_NOT_USE_BUFFER_POOL
  delete [] apBuffer;
#else
  INGwSpBufferFactory::getInstance().reuseObject(apBuffer);
#endif
}


////////////////////////////////////////////////////////////////////////////////
// Method: handleWorkerClbk
// Description:
//
//
////////////////////////////////////////////////////////////////////////////////
int
INGwSpSipStackIntfLayer::handleWorkerClbk(INGwIfrMgrWorkUnit* apWork) {
  LogINGwTrace(false, 0, "IN handleWorkerClbk");
 
  int l_retVal = OP_FAIL;

  if(0 == apWork) {
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
                  " INGwIfrMgrWorkUnit* apWork == NULL \n");
    LogINGwTrace(false, 0, "OUT handleWorkerClbk");
    return OP_FAIL;
  }

  switch(apWork->meWorkType)
  {
    case INGwIfrMgrWorkUnit::SIP_CALL_MSG:
    {
#ifdef INGW_TRACE_CALL_THREAD
      logger.logINGwMsg(false, VERBOSE_FLAG, 0,"+THREAD+ Sip SIL got <%s>",
                                               apWork->mpcCallId);
#endif
      l_retVal = recvSipMsg((char*)apWork->mpMsg,
                            apWork->mulMsgSize,
                            apWork->mpContextData);
      break;
    }
    
    case INGwIfrMgrWorkUnit::SEND_NOTIFY_TO_NW:
    {
#ifdef INGW_TRACE_CALL_THREAD
      logger.logINGwMsg(false,VERBOSE_FLAG,0,"+THREAD+ Sip SiL got Msg");
#endif
      sendSipMsgToNetwork(apWork); 
      break;
    }
    case INGwIfrMgrWorkUnit::TIMER_MSG:
    {
      logger.logINGwMsg(false, TRACE_FLAG, 0, 
												"INGwSpSipStackIntfLayer::handleWorkerClbk: "
												"Timerid <%u> fired, workunit <%p>", 
												apWork->muiTimerId, apWork);
      // Get the context and call the handleTimeout method.
      INGwSipTimerType *timercontext = (INGwSipTimerType *)(apWork->mpContextData);
      if(!timercontext)
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0, 
													"INGwSpSipStackIntfLayer::handleWorkerClbk: "
													"Found NULL timer context in WorkUnit");
        l_retVal = OP_FAIL;
      }
      else
      {
        // If the timer had already been stopped, its status will be false.
        // Check this and delete the timer if required.
        SchedulerMsg* sm = dynamic_cast<SchedulerMsg*>(apWork);
        if(!sm)
        {
          logger.logINGwMsg(false, ERROR_FLAG, 0, 
														"INGwSpSipStackIntfLayer::handleWorkerClbk: "
														"Error casting workunit into scheduler msg");
          l_retVal = OP_FAIL;
        }
        else if(sm->getStatus())
        {
          logger.logINGwMsg(false, TRACE_FLAG, 0, 
														"INGwSpSipStackIntfLayer::handleWorkerClbk: "
														"Calling handleTimeout");

          handleTimeout(timercontext, apWork->muiTimerId);
          l_retVal = OP_SUCCESS;
        }
        else
        {
          logger.logINGwMsg(false, TRACE_FLAG, 0, 
														"INGwSpSipStackIntfLayer::handleWorkerClbk: "
														"Not calling handleTimeout, since timer status is false");
          delete timercontext;
          l_retVal = OP_SUCCESS;
        }
      }

      break;
    }
    case INGwIfrMgrWorkUnit::CONFIG_UPDATE:
    {
      // Get the config context from workunit, and handle the update.
      INGwSipConfigContext* context =(INGwSipConfigContext*)(apWork->mpContextData);
      if(!context)
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0, 
												 "INGwSpSipStackIntfLayer::handleWorkerClbk: "
												 "Found NULL config context in WorkUnit");
        l_retVal = OP_FAIL;
      }

      handleConfigUpdate(context);
      delete context;

      break;
    }
    
    case INGwIfrMgrWorkUnit::PENDING_SLEERESPONSE_INFO:
    {
      Sdf_st_error sdferror;

      // Find the connection corresponding to this workunig
      Sdf_st_callObject *callobj = NULL;
      if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().
          getCallTable().get(apWork->mpcCallId, &callobj))
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "INGwSpSipStackIntfLayer::handleWorkerClbk: "
												 "Could not find callobject for callid <%s> for "
												 "msgtype PENDING_SLEERESPONSE_INFO", 
                         apWork->mpcCallId);
      }
      else
      {
        // Get the app data from the permanent call object and the connection
        // context from there.
        INGwSpSipConnection* connection = NULL;
        Sdf_st_appData *appdata = Sdf_co_null;

        sdf_ivk_uaGetAppDataFromCallObject(callobj, &appdata, &sdferror);
        if(appdata == NULL)
        {
          logger.logINGwMsg(false, ERROR_FLAG, 0, 
                            "INGwSpSipStackIntfLayer::handleWorkerClbk: "
														"Could not find appdata for callid <%s> for "
														"msgtype PENDING_SLEERESPONSE_INFO", 
                            apWork->mpcCallId);
        }
        else
        {
          if(appdata->pData)
            connection=((INGwConnectionContext *)(appdata->pData))->mSipConnection;

          sdf_ivk_uaFreeAppData(appdata);

          if(connection)
          {
            logger.logINGwMsg(false, TRACE_FLAG, 0, 
                             "INGwSpSipStackIntfLayer::handleWorkerClbk: "
														 "Calling sendPendingSleeResponse with callid <%s>", 
                             apWork->mpcCallId);

            //connection->sendPendingSleeResponse(INGwIfrMgrWorkUnit::PENDING_SLEERESPONSE_INFO, apWork->mpContextData);
          }
          else
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
                              "INGwSpSipStackIntfLayer::handleWorkerClbk: "
															"Could not find connection for callid <%s> for"
															" msgtype PENDING_SLEERESPONSE_INFO", 
                              apWork->mpcCallId);
        }
      }

      break;
    }

    default:
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
											  "INGwSpSipStackIntfLayer::handleWorkerClbk: "
												"Unrecognized work type <%d>", 
												apWork->meWorkType);
      l_retVal = OP_FAIL;
      break;
  } // End of switch

  // Set the context to NULL, so that in case the pointer is lingering around
  // no one will be able to use it.
  apWork->mpContextData = NULL;
  // apWork->muiTimerId    = 0;

  LogINGwTrace(false, 0, "OUT handleWorkerClbk");
  return l_retVal;
}

////////////////////////////////////////////////////////////////////////////////
// Method: recvSipMsg
// Description: This method is called by the worker thread in the sip provider
//              when a sip message is received from the network.  This method
//              creates a temporary call object, and decodes the message.
//              It then tries to find an existing call object corresponding to
//              this call leg.  If an existing call object is found, the
//              call object is updated with the temporary object.  Else, the
//              temporary object itself is asked to handle the message.  After
//              this, the control will go to some stack callback, which will
//              do the needful.
// IN  - aSipMsg: Contains the raw SIP message buffer.
// IN  - aMaxLength: Specifies the maximum length to parse in the raw SIP
//       buffer.  Not used at present.
// IN  - aContext: This is the INGwSipTranspInfo structure, which contains the
//       address and transport mechanism on which the raw SIP buffer was
//       received.  This is filled in by the network listening thread and is
//       passed to the worker thread as a message unit context.
//
// Return Value: NONE
////////////////////////////////////////////////////////////////////////////////
int INGwSpSipStackIntfLayer::recvSipMsg(char *aSipMsg,
                                     unsigned long         aMaxLength,
                                     void       *aContext)
{
   LogINGwTrace(false, 0, "IN recvSipMsg");

   if((aSipMsg == NULL) || (aContext == NULL))
   {
      LogINGwTrace(false, 0, "OUT recvSipMsg");
      return OP_FAIL;
   }

	 //
	 // If the received Sip Msg doesn't pass screening..We'll just drop it.

	 if( INGwSpSipProviderConfig::isIncomingMessageScreeningEnabled() )
	 {
		 if( ! (INGwSpSipUtil::screenSipMessageForCorruption(aSipMsg, aMaxLength) ) )
		 {
			 logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "Corrupted Sip Msg in recvSipMsg: %s", aSipMsg);
			 LogINGwTrace(false, 0, "OUT recvSipMsg");
			 return OP_FAIL;
		 }
	 }

   INGwSipTranspInfo *pTranspInfo = (INGwSipTranspInfo *)aContext;

   // Create a temporary call object and initialize it.
   Sdf_st_callObject *tmpCallObj = NULL;
   Sdf_st_error       tmpErr;

   Sdf_ty_retVal      status ;


   Sdf_st_eventContext *eventContext = NULL;
   sdf_ivk_uaInitEventContext(&eventContext, &tmpErr);

   Sdf_ty_s8bit* currentBufPtr = (Sdf_ty_s8bit*)aSipMsg;
   Sdf_ty_s8bit* nextBufPtr = NULL;
   bool lIsNextMsgPresent = false;

   do
   {
      sdf_ivk_uaInitCall(&tmpCallObj, &tmpErr);

      tmpCallObj->pAppData->pData = 0;

      nextBufPtr = NULL;
      eventContext->pTranspAddr =
                  (SipTranspAddr*)sdf_memget(0, sizeof(SipTranspAddr), &tmpErr);
      eventContext->dNextMessageLength = 0;
      eventContext->pData              = NULL;
      eventContext->pTranspAddr->pHost = pTranspInfo->pTranspAddr;
      eventContext->dProtocol          = pTranspInfo->mTranspType;

      eventContext->dSdfDisabledChecks |= Sdf_mc_disableRequireCheck;
   

      SipOptions decodeOptions;
      decodeOptions.dOption  = 0;

      if(!INGwSpSipProviderConfig::isSdpParsed())
      {
         decodeOptions.dOption |= (SIP_OPT_NOPARSEBODY | SIP_OPT_NOPARSESDP);
      }


      LogINGwTrace(false, 0, "Invoking sdf_ivk_uaDecodeMessage");

      Sdf_ty_matchMessageType msgtype = sdf_ivk_uaDecodeMessage(
          tmpCallObj,              // The call object
          currentBufPtr,           // The raw sip buffer
          &decodeOptions,          // Options for decoding
          aMaxLength,              // Max length of the raw buffer
          &nextBufPtr,             // Pointer to next sip msg in buf (not used)
          eventContext,            // The event context
          &tmpErr);                // Any error (in case)

      if(! INGwSpSipProviderConfig::isTransportUdp() )
      {
	   	   if (tmpCallObj->pUacTransaction->pSipMsg != Sdf_co_null)
			   {
				    //tmpCallObj->pUacTransaction->dSockfd = pTranspInfo->mSockfd;
				    tmpCallObj->pUacTransaction->dSockfd =
											 INGwSpTcpConnMgr::instance().getSelfSocketId();
			   }
		     else if (tmpCallObj->pUasTransaction->pSipMsg != Sdf_co_null)
			   {
				    //tmpCallObj->pUasTransaction->dSockfd = pTranspInfo->mSockfd;
				    tmpCallObj->pUasTransaction->dSockfd = 
											 INGwSpTcpConnMgr::instance().getSelfSocketId();
			   }
      }

      switch(msgtype)
      {
         case Sdf_en_success:
         {
            logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
                            "Sip Message successfully decoded");

            // The msg was good.  Now get the call id, and check in the hash table
            // for an existing call object.
   
            sdf_ivk_uaSetInitDataInCallObject(tmpCallObj, pGlbProfile, &tmpErr);

            if(tmpCallObj->pCommonInfo->pCallid == NULL)
            {
               logger.logINGwMsg(false, ERROR_FLAG, 0, 
                               "Could not find callid in the decoded call object");
               break;
            }
 
            Sdf_st_callObject *permCallObj = NULL;
            
            if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().
               getCallTable().get(tmpCallObj->pCommonInfo->pCallid, &permCallObj))
            {
               permCallObj = NULL;
            }

            Sdf_ty_retVal status = Sdf_co_success;

            if(permCallObj == NULL)
            {
               logger.logINGwMsg(false, TRACE_FLAG, 0, 
                               "recvSipMsg: permCallObj is NULL, so calling "
                               "HandleCall");
               tmpCallObj->pAppData->pData = 0;

               INGwSpThreadSpecificSipData &thrData = 
                         INGwSpSipProvider::getInstance().getThreadSpecificSipData();
               thrData.msgBuf       = aSipMsg;
               thrData.msgTransport = pTranspInfo;

               status = sdf_ivk_uaHandleCall(&tmpCallObj, eventContext, &tmpErr);

               thrData.msgBuf = NULL;
               thrData.msgTransport = NULL;

               if(status != Sdf_co_success)
               {
                  logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
                                  "recvSipMsg: Error handling call");
                  INGwSpSipUtil::checkError(status, tmpErr);
               }
               else
               {
                  logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
                                  "recvSipMsg: Success handling call");
               }
            }
            else
            {
               logger.logINGwMsg(false, TRACE_FLAG, 0, 
                               "recvSipMsg: permCallObj is not NULL, calling "
                               "UpdateCallDetails");
               tmpErr.ErrMsg[0] = 0;
               HSS_LOCKEDINCREF(permCallObj->dRefCount);

               INGwSpSipCall *bpCall = NULL;
               INGwSpSipConnection *bpConn = 
                                INGwSpSipUtil::getINGwSipConnFromHssCall(permCallObj);

               if(bpConn == NULL)
               {
                  logger.logMsg(ERROR_FLAG, 0, 
                                "Conn is NULL, may lead to crash.");
               }
               else
               {
                  bpConn->getRef();
    
                  bpCall = &(bpConn->getCall());
               }

               INGwIfrUtlRefCount_var bpConn_var(bpConn);

               if(bpCall == NULL)
               {
                  logger.logMsg(ERROR_FLAG, 0, 
                             "INGwSpSipCall is NULL, may lead to crash.");
               }
               else
               {
                  bpCall->getRef();

                  if(INGwSpSipProviderConfig::isHoldMsg())
                  {
                     struct timeval tim;
	                   char buf[26];
	                   gettimeofday( &tim, NULL);
	                   time_t tt;
   
                     #ifdef LINUX
	                    char *t = ctime_r(&tim.tv_sec, buf);
                     #else
                        char *t = ctime_r(&tim.tv_sec, buf, sizeof(buf));
                     #endif
   
	                   int len = strlen(buf);
	                   buf[len-6] = '\0';
                     char tempBuff[7];
                     sprintf(tempBuff,"%d",tim.tv_usec);
                     std::string timeStamp=string(buf)+':'+string(tempBuff);
   
	   		            char nwAddrBuf[26];
                     sprintf(nwAddrBuf,"<%s:%d>",pTranspInfo->pTranspAddr,pTranspInfo->mPort);
                     std::string msgBuf=timeStamp+
                                        " MSG RECEIVED FROM "+string(nwAddrBuf)+
                                        '\n'+string(aSipMsg);
                     bpCall->msgList.push_back(msgBuf);
                  }
               }

               INGwIfrUtlRefCount_var bpCall_var(bpCall);

               Sdf_ty_messageValidity updatestat = 
                      sdf_ivk_uaUpdateCallDetails(&permCallObj, tmpCallObj, 
                                                  eventContext,  &tmpErr);
               if(updatestat != Sdf_en_valid)
               {
                  logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
                                  "recvSipMsg: UpdateCallDetails returned %d", 
                                  updatestat);
                  status = Sdf_co_fail;
               }
   
               if(status != Sdf_co_success)
               {
                  logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
                                  "recvSipMsg: Error updating call");
                  INGwSpSipUtil::checkError(status, tmpErr);
               }

               sdf_ivk_uaFreeCallObject(&permCallObj);
            }
   
            if(status != Sdf_co_success)
            {
               INGwSpSipUtil::checkError(status, tmpErr);
               logger.logINGwMsg(false, VERBOSE_FLAG, 0, "recvSipMsg: %s", aSipMsg);
            }
         }
         break;

         case Sdf_en_remoteRetransmission:
         {
         }
         break;

         case Sdf_en_fail:
         {
            logger.logMsg(ERROR_FLAG, 0, "Decode of [%s] failed.", aSipMsg);
            INGwSpSipUtil::checkError(Sdf_co_fail, tmpErr);
         }
         break;

         default:
         {
            logger.logMsg(ERROR_FLAG, 0, "Unknown return from decode [%d] [%s]",
                          msgtype, aSipMsg);
         }
         break;
      } 

		  /* 
  		 * The current message is processed. Parse the next Sip Message
  		 * in the buffer (if any)
  		 */
		
      // For the first time flag will be off
      // so only on second loop this currentBufPtr will be deleted
	  	if (lIsNextMsgPresent)
	  		 sdf_memfree(0, (Sdf_ty_pvoid *)&currentBufPtr, Sdf_co_null);
	
	  	currentBufPtr = Sdf_co_null;
      int lNextLen = 0;

	  	if (nextBufPtr != Sdf_co_null)
	  	   lNextLen =eventContext->dNextMessageLength;
   
	  	if (nextBufPtr != Sdf_co_null)
	  	{
         Sdf_st_error 	error;
			   currentBufPtr = (Sdf_ty_s8bit *)sdf_memget(0, 
                                 lNextLen * sizeof(Sdf_ty_s8bit), &error);
			   lIsNextMsgPresent = true;
			   Sdf_mc_memcpy(currentBufPtr, nextBufPtr, lNextLen);
			   sdf_memfree(0, (Sdf_ty_pvoid *)&nextBufPtr, Sdf_co_null);
	  	}	
   

      if(eventContext->pTranspAddr)
      {
         sdf_memfree(0, (Sdf_ty_pvoid *)&(eventContext->pTranspAddr), &tmpErr);
      }

      logger.logINGwMsg(0, TRACE_FLAG, 0,
                    "HSS Call Object Ref-Count [ %d ].",
                    (tmpCallObj->dRefCount).ref);

      sdf_ivk_uaFreeCallObject(&tmpCallObj);

   }
   while(currentBufPtr != NULL);

   sdf_ivk_uaFreeEventContext(eventContext);
   delete pTranspInfo;

#ifdef DO_NOT_USE_BUFFER_POOL
   delete[] aSipMsg;
#else
   INGwSpBufferFactory::getInstance().reuseObject((char*)aSipMsg);
#endif

   LogINGwTrace(false, 0, "OUT recvSipMsg");
   return OP_SUCCESS;
} 

////////////////////////////////////////////////////////////////////////////////
// Function: sdf_cbk_uaNewCallReceived
// Description: This function is called by the stack when a received message is
//              an INVITE for a new call.  This is a callback function, and is
//              called from within the sdf_ivk_uaHandleCall function.
// IN  - ppCallObj: The call object for which the message is received.
// IN  - pEventContext: The event context which was passed to the handle call.
// OUT - pErr: The application can return an error if something is not right.
//       This parameter and the return value will both be returned to the
//       handle call caller.
////////////////////////////////////////////////////////////////////////////////

Sdf_ty_retVal sdf_cbk_uaNewCallReceived(Sdf_st_callObject **ppCallObj, 
                                        Sdf_st_eventContext *pEventContext, 
                                        Sdf_st_error *pErr)
{
   LogINGwTrace(false, 0, "IN sdf_cbk_uaNewCallReceived");

   int lCurVal = 0;
   INGwIfrSmStatMgr::instance().increment(
							INGwSpSipProvider::miStatParamId_NumInvRecvd, lCurVal, 1);

   if((*ppCallObj)->pCommonInfo->pCallid == NULL)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "Could not find call id from call object.");
      pErr->errCode = Sdf_en_callObjectAccessError;
      strcpy(pErr->ErrMsg, "Call id is NULL");
      LogINGwTrace(false, 0, "OUT sdf_cbk_uaNewCallReceived");
      return Sdf_co_fail;
   }

   // check if Call Gapping needs to drop this call
   INGwSpSipProvider& l_pdr = INGwSpSipProvider::getInstance();
   if(!l_pdr.getNewCallsStatus()) 
   {

      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "getNewCallsStatus returned false, dropping call:<%s>",
                      (*ppCallObj)->pCommonInfo->pCallid);

      Sdf_ty_retVal status = Sdf_co_fail;
      Sdf_st_error  pErr;

      int lCurVal = 0;
      INGwIfrSmStatMgr::instance().increment(
							INGwSpSipProvider::miStatParamId_NumInvRejected, lCurVal, 1);

      sdf_ivk_uaFormResponse(500, "INVITE", *ppCallObj, Sdf_co_null, 
                             Sdf_co_false, &pErr);

      INGwSpSipUtil::sendCallToPeer(*ppCallObj, 
                                (*ppCallObj)->pUasTransaction->pSipMsg, 
                                INGW_SIP_METHOD_TYPE_INVITE, &pErr);

      LogINGwTrace(false, 0, "OUT sdf_cbk_uaNewCallReceived");
      return Sdf_co_success;
   }

   LogINGwTrace(false, 0, "getNewCallsStatus TRUE ");

   // Get the call-id from the call object.
   const char *callidstr = (*ppCallObj)->pCommonInfo->pCallid;

#if 0
	// Fetch Sequence ID coming in Notify
		SipList &locUnknownHdrList = 
				(*ppCallObj)->pUasTransaction->pSipMsg->pGeneralHdr->slBadHdr;
				//(*ppCallObj)->pUasTransaction->pSipMsg->pGeneralHdr->slUnknownHdr;

		for(SipListElement *curr = locUnknownHdrList.head; curr != SIP_NULL;
			 curr = curr->next)
		{
			SipBadHeader	 *currHeader = (SipBadHeader *)(curr->pData);
			//SipUnknownHeader *currHeader = (SipUnknownHeader *)(curr->pData);
			string hdrName = currHeader->pName;

			if(string::npos != hdrName.find("Dialogue"))
			{
				logger.logMsg(ERROR_FLAG, 0, "Found Dialogue-id: name:%s, body:%s", 
				currHeader->pName, currHeader->pBody);
			}
			logger.logMsg(ERROR_FLAG, 0, "Unknown Header:name:%s body:%s", 
				currHeader->pName, currHeader->pBody);
		}
#endif

  // Create a call and a connection object, and set up the links between the
  // call, connection, and the HSS call leg.

   INGwSpSipCall* tmpSipCall = 
         dynamic_cast<INGwSpSipCall*>(INGwSpSipProvider::getInstance().getNewCall(callidstr,
                                                                      true));
   INGwIfrUtlRefCount_var call_var(tmpSipCall);
   INGwSpSipConnection *tmpSipConnection = dynamic_cast<INGwSpSipConnection*>(
                       INGwSpSipProvider::getInstance().getNewConnection(*tmpSipCall));
   tmpSipConnection->setLocalCallId(callidstr);

   INGwIfrUtlRefCount_var con_var(tmpSipConnection);
   tmpSipCall->addConnection((INGwSpSipConnection *)tmpSipConnection);
   //Now Call has Conn Ref and Conn has Call Ref.


   tmpSipConnection->setHssCallObject(*ppCallObj);
   HSS_LOCKEDINCREF((*ppCallObj)->dRefCount); 
   //Now Conn had HssCallLeg Ref.

   // Attach the callid and the INGwSpSipConnection as application data in the hss
   // call leg.

   Sdf_st_appData *pAppSpecificData = Sdf_co_null;
   sdf_ivk_uaGetAppDataFromCallObject((*ppCallObj), &pAppSpecificData, pErr);
 
   INGwConnectionContext *tmpConnectionContext = new INGwConnectionContext;
   tmpConnectionContext->mSipConnection = tmpSipConnection;
   strcpy(tmpConnectionContext->mCallId, (*ppCallObj)->pCommonInfo->pCallid);

   pAppSpecificData->pData = (void *)tmpConnectionContext;
   tmpSipConnection->getRef();  
   //Now HSS call leg has a reference of connection

   sdf_ivk_uaFreeAppData(pAppSpecificData);
   logger.logINGwMsg(false, TRACE_FLAG, 0, 
                   "Successfully added app data into call object");
  
   INGwSpSipProvider& l_bpSipProvider = INGwSpSipProvider::getInstance();


   // Add the call object into the call table in the thread specific data.  This
   // also warrants an increase in the reference count of the HSS call leg.

   if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().
                   getCallTable().put(callidstr, strlen(callidstr), *ppCallObj))
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "Error adding call object in CCM call table");
      INGwSpSipUtil::releaseConnection(tmpSipConnection);
      tmpSipCall->removeConnection(tmpSipConnection->getSelfConnectionId());

      pErr->errCode = Sdf_en_callObjectAccessError;
      strcpy(pErr->ErrMsg, "Error adding call object in CCM call table");
      LogINGwTrace(false, 0, "OUT sdf_cbk_uaNewCallReceived");
      return Sdf_co_fail;
   }

   HSS_LOCKEDINCREF((*ppCallObj)->dRefCount); 
   //For placing call leg in leg map.

   // XXX: Here call method on the provider to add the call object into the CCM
   // call table.


   INGwSpSipCallController* l_callCntrl = l_bpSipProvider.getCallController(); 
   if(0 == l_callCntrl) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "Error adding call object in CCM call table");
      LogINGwTrace(false, 0, "OUT sdf_cbk_uaNewCallReceived");
      return Sdf_co_fail;
   }

   std::string l_internalCallId = tmpSipCall->getCallId();

   int retVal = l_callCntrl->addCall(l_internalCallId, tmpSipCall);

   if(-1 == retVal)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "sdf_cbk_uaNewCallReceived: error in adding call object "
                      "in CallCntrl");

      Sdf_st_overlapTransInfo* dummy = NULL;
      Sdf_ty_retVal status = sdf_ivk_uaRejectRequest(*ppCallObj, pGlbProfile, 
                                                     500, &dummy, pErr);
      if(status != Sdf_co_success)
      {
         logger.logINGwMsg(false, WARNING_FLAG, 0, 
                         "sdf_cbk_uaNewCallReceived: Error rejecting request "
                         "with 5xx response");
         INGwSpSipUtil::checkError(status, *pErr);
      }
      else
      {
         logger.logINGwMsg(false, TRACE_FLAG, 0, 
                         "sdf_cbk_uaNewCallReceived: Successfully formed 5xx "
                         "response to reject the request");

         status = INGwSpSipUtil::sendCallToPeer(*ppCallObj, 
                                         (*ppCallObj)->pUasTransaction->pSipMsg,
                                            INGW_SIP_METHOD_TYPE_INVITE, pErr);
         if(status != Sdf_co_success)
         {
            logger.logINGwMsg(false, WARNING_FLAG, 0, 
                            "sdf_cbk_uaNewCallReceived: Error sending call to "
                            "peer");
            INGwSpSipUtil::checkError(status, *pErr);
         }
         else
         {
            logger.logINGwMsg(false, TRACE_FLAG, 0, 
                            "sdf_cbk_uaNewCallReceived: Successfully sent 5xx "
                            "rejection to peer");
         }
      }
    
      // Release the connection.  This should in turn release the INGwSpSipCall and
      // hssCallObj
      INGwSpSipUtil::releaseConnection(tmpSipConnection);
      tmpSipCall->removeConnection(tmpSipConnection->getSelfConnectionId());

      LogINGwTrace(false, 0, "OUT sdf_cbk_uaNewCallReceived");
      return Sdf_co_fail;
   }

   INGwConnectionContext *conncontext = 
                         (INGwConnectionContext *)((*ppCallObj)->pAppData->pData);
   INGwSpSipConnection *bpSipConnection = NULL;

   if(conncontext)
   {
      bpSipConnection = conncontext->mSipConnection;
   }
  
   if(bpSipConnection == NULL)
   {
      pErr->errCode = Sdf_en_callObjectAccessError;
      strcpy(pErr->ErrMsg, 
             "Error retrieving Connection from stack call leg");
      LogINGwTrace(false, 0, "OUT sdf_cbk_uaNewCallReceived");
      return Sdf_co_fail;
   }

   bpSipConnection->getRef();
   INGwIfrUtlRefCount_var sipConnVar(bpSipConnection);

   // Set sender active port where response will be sent in TCP conn mode

   INGwSpThreadSpecificSipData &thrData = 
                           INGwSpSipProvider::getInstance().getThreadSpecificSipData();
   INGwSipTranspInfo* lTranspInfo = thrData.msgTransport;

   bpSipConnection->setActiveEPPort(lTranspInfo->mPort);

   logger.logINGwMsg(false, TRACE_FLAG, 0, 
                   "Calling request callback on the INGwSpSipConnection\n");
   Sdf_ty_retVal retval = bpSipConnection->stackCallbackRequest(
                           INGW_SIP_METHOD_TYPE_INVITE,
                           ppCallObj,
                           pEventContext,
                           pErr, NULL);

   LogINGwTrace(false, 0, "OUT sdf_cbk_uaNewCallReceived");
   return retval;
} 



////////////////////////////////////////////////////////////////////////////////
// Function: sdf_cbk_uaInProgress
// Description: This function is called by the stack when a provisional
//              response is received.
// IN  - ppCallObj: The call object for which the message is received.
// IN  - pEventContext: The event context which was passed to the handle call.
// OUT - pErr: The application can return an error if something is not right.
//       This parameter and the return value will both be returned to the
//       handle call caller.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal sdf_cbk_uaInProgress
  (Sdf_st_callObject        **ppCallObj        ,
   Sdf_st_overlapTransInfo   *pOverlapTransInfo,
   Sdf_st_eventContext       *pEventContext    ,
   Sdf_st_error              *pErr             )
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaInProgress");

  // Check the call state.  If it is reinvite sent or reinvite prov response
  // received, then this is a 1xx response for reinvite.
  bool isReinvite = false;
  Sdf_st_callInfo *pCallInfo = Sdf_co_null;
  sdf_ivk_uaGetCallInfoFromCallObject((*ppCallObj), &pCallInfo, pErr);
  Sdf_ty_state callstate = pCallInfo->dState;
  logger.logINGwMsg(false, VERBOSE_FLAG, 0, "sdf_cbk_uaInProgress: callstate <%d>", callstate);
  if((callstate == Sdf_en_reInviteSent    ) ||
     (callstate == Sdf_en_reInviteprovRespReceived))
  {
    isReinvite = true;
  }
  sdf_ivk_uaFreeCallInfo(pCallInfo);
  
  Sdf_ty_retVal ret = responseCallback
    (ppCallObj, pEventContext, pErr, isReinvite, pOverlapTransInfo);

  LogINGwTrace(false, 0, "OUT sdf_cbk_uaInProgress");
  return ret;
} // End of sdf_cbk_uaInProgresk

////////////////////////////////////////////////////////////////////////////////
// Function: sdf_cbk_uaCallFailed
// Description: This function is called by the stack when an error response
//              is received for a request.
// IN  - ppCallObj: The call object for which the message is received.
// IN  - pEventContext: The event context which was passed to the handle call.
// OUT - pErr: The application can return an error if something is not right.
//       This parameter and the return value will both be returned to the
//       handle call caller.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal sdf_cbk_uaCallFailed
  (Sdf_st_callObject   **ppCallObj,
  Sdf_st_eventContext   *pEventContext,
  Sdf_st_error          *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaCallFailed");
  Sdf_ty_retVal ret = responseCallback(ppCallObj, pEventContext, pErr, false);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaCallFailed");
  return ret;
}

////////////////////////////////////////////////////////////////////////////////
// Function: sdf_cbk_uaCallAccepted
// Description: This function is called by the stack when a final success
//              response is received for a request.
// IN  - ppCallObj: The call object for which the message is received.
// IN  - pEventContext: The event context which was passed to the handle call.
// OUT - pErr: The application can return an error if something is not right.
//       This parameter and the return value will both be returned to the
//       handle call caller.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal sdf_cbk_uaCallAccepted
  (Sdf_st_callObject   **ppCallObj,
  Sdf_st_eventContext   *pEventContext,
  Sdf_st_error          *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaCallAccepted");
  Sdf_ty_retVal ret = responseCallback(ppCallObj, pEventContext, pErr, false);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaCallAccepted");
  return ret;
}

////////////////////////////////////////////////////////////////////////////////
// Function: sdf_cbk_uaCallRedirected
// Description: This function is called by the stack when a 3xx response is
//              received for a request.
// IN  - ppCallObj: The call object for which the message is received.
// IN  - pEventContext: The event context which was passed to the handle call.
// OUT - pErr: The application can return an error if something is not right.
//       This parameter and the return value will both be returned to the
//       handle call caller.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal sdf_cbk_uaCallRedirected
  (Sdf_st_callObject   **ppCallObj,
  Sdf_st_eventContext   *pEventContext,
  Sdf_st_error          *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaCallRedirected");
  Sdf_ty_retVal ret = responseCallback(ppCallObj, pEventContext, pErr, false);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaCallRedirected");
  return ret;
}

////////////////////////////////////////////////////////////////////////////////
// Function: responseCallback
// Description: This function is called by the various response callbacks of
//              the stack.  The stack has many response callbacks, but most of
//              them do the same thing.  So this function is used instead of
//              implementing repeated functionality.
//              This method gets the bp sip connection from the hss call leg,
//              finds out the response code and the method name, and calls the
//              response callback on the bp sip connection.
// IN  - ppCallObj: The call object for which the message is received.
// IN  - pEventContext: The event context which was passed to the handle call.
// OUT - pErr: The application can return an error if something is not right.
//       This parameter and the return value will both be returned to the
//       handle call caller.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal responseCallback
  (Sdf_st_callObject    **ppCallObj,
   Sdf_st_eventContext   *pEventContext,
   Sdf_st_error          *pErr,
   bool                   aIsReinvite,
   Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
  LogINGwTrace(false, 0, "IN responseCallback");
 
  Sdf_ty_retVal status;

  logger.logINGwMsg(false, VERBOSE_FLAG, 0, "responseCallback: Getting app data from call object");

  // Get the associated bp sip connection from the hss call object.
  Sdf_st_appData *pAppSpecificData = Sdf_co_null;
  if(sdf_ivk_uaGetAppDataFromCallObject((*ppCallObj),
                                         &pAppSpecificData,
                                         pErr)
                                       != Sdf_co_success)
  {
    // Should never happen.
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "Error retrieving app data from call object.");

    logger.logINGwMsg(false, ERROR_FLAG, 0, "Error XXX");
    logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT responseCallback");
    LogINGwTrace(false, 0, "OUT responseCallback");
    return Sdf_co_fail;
  }

  logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
										"responseCallback: Getting method name from call object."
										" OverlapTransInfo <%p>", pOverlapTransInfo);

  // Get the method name and response code from the response.
  Sdf_ty_u16bit respcode;
  if(pOverlapTransInfo)
    status = sdf_ivk_uaGetRespCodeFromSipMessage(
               pOverlapTransInfo->pSipMsg,
               &respcode,
               pErr);
  else
    status = sdf_ivk_uaGetRespCodeFromSipMessage(
               (*ppCallObj)->pUacTransaction->pSipMsg,
               &respcode,
               pErr);

  logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
                             "Response Code:<%d>", respcode);

  INGwSipMethodType methodType;
  if(pOverlapTransInfo)
    methodType = INGwSpSipUtil::getMethodFromSipMessage
      (pOverlapTransInfo->pSipMsg);
  else
    methodType = INGwSpSipUtil::getMethodFromSipMessage
      ((*ppCallObj)->pUacTransaction->pSipMsg);

  INGwConnectionContext *connectionContext =
                         (INGwConnectionContext *)(pAppSpecificData->pData);
  INGwSpSipConnection     *bpSipConnection = connectionContext->mSipConnection;
  sdf_ivk_uaFreeAppData (pAppSpecificData);

  if(!bpSipConnection)
  {
		if(methodType == INGW_SIP_METHOD_TYPE_INVITE)
		{
      logger.logINGwMsg(false, ERROR_FLAG, 0, "responseCallback: Error getting INGwSpSipConnection");
      LogINGwTrace(false, 0, "OUT responseCallback");
      return Sdf_co_fail;
		}
		else
		{
      logger.logINGwMsg(false, TRACE_FLAG, 0, 
											 "responseCallback: Response for Notify. Not doing anything");
      LogINGwTrace(false, 0, "OUT responseCallback");
      return Sdf_co_success;
		}
  }

  bpSipConnection->getRef();
  INGwIfrUtlRefCount_var sipConnVar(bpSipConnection);

  // Call the response call back on the connection.  If the connection is
  // CONNECTED and this is an INVITE, then it is actually a re-INVITE.
  if(aIsReinvite && (methodType == INGW_SIP_METHOD_TYPE_INVITE))
  {
    logger.logINGwMsg(false, TRACE_FLAG, 0, 
											"responseCallback: since major state is CONNECTED"
											" method is INITE, method is RE-INVITE.  Connection <%s>", 
											bpSipConnection->getLocalCallId().c_str());
    methodType = INGW_SIP_METHOD_TYPE_REINVITE;
  }

  logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
										"responseCallback: Calling response callback "
										"on connection with method <%d>", methodType);

  status = bpSipConnection->stackCallbackResponse(
                              methodType   ,
                              respcode     ,
                              ppCallObj    ,
                              pEventContext,
                              pErr, pOverlapTransInfo);

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT responseCallback");
  LogINGwTrace(false, 0, "OUT responseCallback");
  return status;
} // End of responseCallback

////////////////////////////////////////////////////////////////////////////////
// Function: sdf_cbk_uaCallAcknowledged
// Description: This function is called by the stack when a successful INVITE
//              is acknowledged.
// IN  - ppCallObj: The call object for which the message is received.
// IN  - pEventContext: The event context which was passed to the handle call.
// OUT - pErr: The application can return an error if something is not right.
//       This parameter and the return value will both be returned to the
//       handle call caller.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal sdf_cbk_uaCallAcknowledged
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaCallAcknowledged");
  Sdf_ty_retVal ret = ackCallback(ppCallObj, pEventContext, pErr, false);
  LogINGwTrace(false, 0, "IN sdf_cbk_uaCallAcknowledged");
  return ret;
} // End of sdf_cbk_uaCallAcknowledged

////////////////////////////////////////////////////////////////////////////////
// Function: sdf_cbk_uaFailedCallAcknowledged
// Description: This function is called by the stack when a failed INVITE is
//              acknowledged.
// IN  - ppCallObj: The call object for which the message is received.
// IN  - pEventContext: The event context which was passed to the handle call.
// OUT - pErr: The application can return an error if something is not right.
//       This parameter and the return value will both be returned to the
//       handle call caller.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal sdf_cbk_uaFailedCallAcknowledged
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaFailedCallAcknowledged");
  Sdf_ty_retVal ret = ackCallback(ppCallObj, pEventContext, pErr, false);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaFailedCallAcknowledged");
  return ret;
} // End of sdf_cbk_uaFailedCallAcknowledged

Sdf_ty_retVal sdf_cbk_uaReInviteAcknowledged
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaReInviteAcknowledged");
  Sdf_ty_retVal ret = ackCallback(ppCallObj, pEventContext, pErr, true);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaReInviteAcknowledged");
  return ret;
} // End of sdf_cbk_uaReInviteAcknowledged


////////////////////////////////////////////////////////////////////////////////
// Function: ackCallback
// Description: This function is called by ack callbacks of the stack.  Stack
//              provides different ack callbacks under different conditions,
//              but they have the same processing logic.  This function
//              implements that logic.
// IN  - ppCallObj: The call object for which the message is received.
// IN  - pEventContext: The event context which was passed to the handle call.
// OUT - pErr: The application can return an error if something is not right.
//       This parameter and the return value will both be returned to the
//       handle call caller.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal ackCallback
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr,
    bool aIsReinvite)
{
  LogINGwTrace(false, 0, "IN ackCallback");
  Sdf_ty_retVal status = Sdf_co_fail;

  // Get the associated bp sip connection from the hss call object.
  Sdf_st_appData *pAppSpecificData = Sdf_co_null;
  if(sdf_ivk_uaGetAppDataFromCallObject((*ppCallObj),
                                         &pAppSpecificData,
                                         pErr)
                                       != Sdf_co_success)
  {
    // Should never happen.
    logger.logINGwMsg(false, ERROR_FLAG, 0, "ackCallback: Error retrieving app data from call object\n");
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "Error retrieving app data from call object.");
    LogINGwTrace(false, 0, "OUT ackCallback");
    return Sdf_co_fail;
  }

  INGwConnectionContext *connectionContext =
                         (INGwConnectionContext *)(pAppSpecificData->pData);
  INGwSpSipConnection     *bpSipConnection = connectionContext->mSipConnection;
  bpSipConnection->getRef();
  INGwIfrUtlRefCount_var sipConnVar(bpSipConnection);

  sdf_ivk_uaFreeAppData (pAppSpecificData);

  // Get the method name from the ack
  INGwSipMethodType methodType = INGwSpSipUtil::getMethodFromSipMessage(
                    (*ppCallObj)->pUasTransaction->pSipMsg);
  if(methodType == INGW_SIP_METHOD_TYPE_ACK)
    methodType = INGW_SIP_METHOD_TYPE_INVITE;

  logger.logINGwMsg(false, TRACE_FLAG, 0, " ackCallback:Calling stack callback ack on the connection\n");

  // Call the ack call back on the connection.  If the connection is
  // CONNECTED and this is an INVITE, then it is actually a re-INVITE.
  if(aIsReinvite && (methodType == INGW_SIP_METHOD_TYPE_INVITE))
    methodType = INGW_SIP_METHOD_TYPE_REINVITE;
  status = bpSipConnection->stackCallbackAck(methodType   , ppCallObj,
                                             pEventContext, pErr     );

  logger.logINGwMsg(false, TRACE_FLAG, 0, " OUT ackCallback\n");
  LogINGwTrace(false, 0, "OUT ackCallback");
  return status;
} // End of ackCallback

Sdf_ty_retVal requestCallback
    (Sdf_st_callObject       **ppCallObj        ,
    Sdf_st_eventContext       *pEventContext    ,
    Sdf_st_overlapTransInfo   *pOverlapTransInfo,
    Sdf_st_error              *pErr             ,
    bool                      aIsReinvite       )
{
  LogINGwTrace(false, 0, "IN requestCallback");
  Sdf_ty_retVal status = Sdf_co_success;

  // Get the associated bp sip connection from the hss call object.
  Sdf_st_appData *pAppSpecificData = Sdf_co_null;
  if(sdf_ivk_uaGetAppDataFromCallObject((*ppCallObj),
                                         &pAppSpecificData,
                                         pErr)
                                       != Sdf_co_success)
  {
    // Should never happen.
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "Error retrieving app data from call object.");
    logger.logINGwMsg(false, TRACE_FLAG, 0, " OUT requestCallback\n");
    LogINGwTrace(false, 0, "OUT requestCallback");
    return Sdf_co_fail;
  }

  INGwConnectionContext *connectionContext = NULL;
  Sdf_st_callObject   *callobj           = NULL;

  INGwSipMethodType methodType = INGW_METHOD_TYPE_NONE;
  if(pOverlapTransInfo)
    methodType = INGwSpSipUtil::getMethodFromSipMessage
      (pOverlapTransInfo->pSipMsg);
  else
    methodType = INGwSpSipUtil::getMethodFromSipMessage
      ((*ppCallObj)->pUasTransaction->pSipMsg);

  if(!pAppSpecificData->pData)
  {
    // This can happen in three cases
    // 1. This is an OPTIONS request, in which case we will not have any
    //    associated call object.
    // 2. This is a CANCEL or mid-transaction BYE request, in which case the
    //    call object is actually some temporary call object.  In this case,
    //    we need to find the permanent call object that we have stored.
		// 3. Notify is recv, in that case we may not have call object
    if(methodType == INGW_SIP_METHOD_TYPE_OPTIONS)
    {

      // Handle options here.  
      logger.logINGwMsg(false, TRACE_FLAG, 0, 
												"requestCallback: Received an OPTIONS request.");
      callobj = *ppCallObj;
      sdf_ivk_uaFreeAppData (pAppSpecificData);
      status = handleOptionsRequest(&callobj,
                                   pEventContext,
                                   pOverlapTransInfo,
                                   pErr);
      //END here
/*
      status = sdf_ivk_uaFormResponse
        (200, "OPTIONS", *ppCallObj, pOverlapTransInfo, Sdf_co_false, pErr);

      if(status != Sdf_co_success)
      {
        INGwSpSipUtil::checkError(status, *pErr);
        logger.logINGwMsg(false, ERROR_FLAG, 0, 
													"requestCallback: "
													"Error forming 200 OK response for OPTIONS");
      }
      else
        logger.logINGwMsg(false, TRACE_FLAG, 0, 
													"requestCallback: "
													"Successfully formed a 200 OK response to OPTIONS");

      if(status == Sdf_co_success)
      {
        status = INGwSpSipUtil::sendCallToPeer(
                   (*ppCallObj), pOverlapTransInfo->pSipMsg,
                   INGW_SIP_METHOD_TYPE_OPTIONS, pErr);
        if(status != Sdf_co_success)
        {
          INGwSpSipUtil::checkError(status, *pErr);
          logger.logINGwMsg(false, ERROR_FLAG, 0, 
														"requestCallback: "
														"Error sending 200 OK response for OPTIONS");
        }
        else
          logger.logINGwMsg(false, TRACE_FLAG, 0, 
														"requestCallback: "
														"Successfully sent a 200 OK response to OPTIONS");
      }
*/

    } // end of if(methodType == OPTIONS)
    else if((methodType == INGW_SIP_METHOD_TYPE_CANCEL) ||
            (methodType == INGW_SIP_METHOD_TYPE_BYE   ))
    {
      // Extract the call id again from the request, and find the permanent
      // call object from the call leg map.
      Sdf_st_callObject *permCallObj = NULL;
      if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().
          getCallTable().get((*ppCallObj)->pCommonInfo->pCallid, &permCallObj))
        permCallObj = NULL;

      if(!permCallObj)
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0, 
													"requestCallback: Could not find a permanent "
													"call object for method <%d>, and the app data is NULL", 
													methodType);
        LogINGwTrace(false, 0, "OUT requestCallback");
        return Sdf_co_fail;
      }
      else
      {
        // Get the app data from the permanent call object and the connection
        // context from there.
        Sdf_st_appData *appdata = Sdf_co_null;
        sdf_ivk_uaGetAppDataFromCallObject(permCallObj, &appdata, pErr);
        connectionContext = (INGwConnectionContext *)(appdata->pData);
        sdf_ivk_uaFreeAppData(appdata);

        // Place the temporary call object in the event context's app data so
        // that the handlers can access it.
        pEventContext->pData = (void *)(*ppCallObj);
        callobj = permCallObj;
      }
    } // end of if(BYE || CANCEL)
    else if(methodType == INGW_SIP_METHOD_TYPE_NOTIFY)
		{

      callobj = *ppCallObj;
      sdf_ivk_uaFreeAppData (pAppSpecificData);
      status = handleNotifyRequest(&callobj,
                                   pEventContext,
                                   pOverlapTransInfo,
                                   pErr);
		} //end of if( NOTIFY )
    else
    {
      // This cannot happen!!!
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
												"requestCallback: "
												"Found NULL App data for a message with method <%d>", 
												methodType);
      LogINGwTrace(false, 0, "OUT requestCallback");
      return Sdf_co_fail;
    }
  } // end of if(!pAppSpecificData->pData)
  else
  {
    callobj = *ppCallObj;
    connectionContext = (INGwConnectionContext *)(pAppSpecificData->pData);
    sdf_ivk_uaFreeAppData (pAppSpecificData);
  }

  if(connectionContext)
  {
    // Get the connection from the app data.
    INGwSpSipConnection *bpSipConnection = connectionContext->mSipConnection;
    bpSipConnection->getRef();
    INGwIfrUtlRefCount_var sipConnVar(bpSipConnection);

    // Call the request call back on the connection.  If the connection is
    // CONNECTED and this is an INVITE, then it is actually a re-INVITE.
    if(aIsReinvite && (methodType == INGW_SIP_METHOD_TYPE_INVITE))
      methodType = INGW_SIP_METHOD_TYPE_REINVITE;
  
    status = bpSipConnection->stackCallbackRequest
               (methodType,
                &callobj,
                pEventContext,
                pErr, pOverlapTransInfo);
  } // end of if(connectionContext...)

  logger.logINGwMsg(false, TRACE_FLAG, 0, " OUT requestCallback\n");
  logger.logINGwMsg(false, TRACE_FLAG, 0, "requestCallback: Returning <%d>", status);

  LogINGwTrace(false, 0, "OUT requestCallback");
  return status;
} // end of requestCallback

////////////////////////////////////////////////////////////////////////////////
// Method: callidGeneratorFunction
// Description: This method is called by the stack whenever it wants a new
//              call id (like when creating a new HSS call leg.
//              Since we have a requirement of all call legs of a call to be
//              processed by the same thread (otherwise, the call object will
//              have to be thread safe), we need to have call ids of different
//              legs in a call related to each other in such a way that all of
//              them hash on to the same thread id.
//              To accomplish this, the call id of the anchor leg is set in
//              the app data in the new call leg, and when this method is
//              called, the app data already has a call id in it.  In this case,
//              a small string like BP_ is prepended to the existsing call id
//              and used as the call id of the new call leg.
////////////////////////////////////////////////////////////////////////////////

Sdf_ty_s8bit* callidGeneratorFunction(Sdf_st_appData *pAppData)
{

  LogINGwTrace(false, 0, "IN callidGeneratorFunction");
  Sdf_ty_s8bit *retval = NULL;
  Sdf_ty_s8bit  dTemp[MAX_CALLID_LEN];

  // Here check whether the app data is NULL or not.  If it is not NULL, then
  // it will contain a connection context, which will contain the call id of
  // the peer connection.  In that case, we need to generate a callid related
  // to the peer (so that both this connection and its peer will get processed
  // by the same thread, and since thread id is determined by call id hash).
  // In that case, we can simply prepend a known string (like BP_) to the
  // call id, and that will distinguish it from the peer's call id.

  if(pAppData)
  {
    INGwConnectionContext *bpconncontext =
          static_cast<INGwConnectionContext*>(pAppData->pData);
    
    if(0 == bpconncontext) {
      logger.logINGwMsg(false, TRACE_FLAG, 0, " static_cast failed...\n");
      logger.logINGwMsg(false, TRACE_FLAG, 0, " OUT callidGeneratorFunction\n");
      LogINGwTrace(false, 0, "OUT callidGeneratorFunction");
      return retval;
    }

    INGwSpSipConnection* l_conn = bpconncontext->mSipConnection;
    if(0 == l_conn) {
      logger.logINGwMsg(false, TRACE_FLAG, 0, " null conn ptr ...\n");
      logger.logINGwMsg(false, TRACE_FLAG, 0, " OUT callidGeneratorFunction\n");
      LogINGwTrace(false, 0, "OUT callidGeneratorFunction");
      return retval;
    }
    
    logger.logINGwMsg(false,VERBOSE_FLAG,0,"callidGeneratorFunction <%s> ",bpconncontext->mCallId);
    short l_connId =0;
	// linux : porting
#ifdef linux
    unsigned long l_hash = elf_hash((const char*)(bpconncontext->mCallId));
#else
    unsigned long l_hash = elf_hash(bpconncontext->mCallId);
#endif

    // No need to append BP_  blah blah to hbcall_ callids, since they are
    // heartbeat callids, which will happen on only one connection, so there is
    // no need to distinguish that call id from any other connection.
    // Also if the connection context says that the supplied callid itself
    // should be used, then simply copy the supplied callid.
    if((strncmp(bpconncontext->mCallId, "hbcall_", 7)) &&
       (!bpconncontext->mbUseGivenCallid))
      sprintf(dTemp, "BP_%d_%d_%d_%s", l_hash, l_connId, bpconncontext->mUniqId,
            bpconncontext->mCallId);
    else
      strcpy(dTemp, bpconncontext->mCallId);
    retval = Sdf_mc_strdupCallHandler(dTemp);
  }
  // There was no app data, which means that this connection is not a peer of
  // some other connection.  In this case, we need to create a call id for this
  // connection which hashes to this thread id.  Hence we ask the provider to
  // give us a correctly hashed call id.

  else
  {
    if(INGwSpSipProvider::generateCallid(dTemp))
      retval = Sdf_mc_strdupCallHandler(dTemp);
  }

  logger.logINGwMsg(false, VERBOSE_FLAG, 0, " Generated call id <%s>\n", retval);
  logger.logINGwMsg(false, TRACE_FLAG, 0, " OUT callidGeneratorFunction\n");
  LogINGwTrace(false, 0, "OUT callidGeneratorFunction");
  return retval;
} // end of callidGeneratorFunction

////////////////////////////////////////////////////////////////////////////////
// Method: fast_startTimer
// Description: This method is called by the stack when it wants a timer to be
//              started.  In this method, a stack timer context is created with
//              the timer key and the buffer.  These are stored in a map in the
//              provider.  When the timer needs to be stopped, the timer key is
//              the ONLY matching criterion, and the buffer needs to be returned
//              to the stack.  By storing it in the timer context, this can be
//              done by simply retrieving the context from the map.
// IN  - duration: The timer duration in milliseconds
// IN  - restart : unused
// IN  - timeoutfunc: Pointer to a function to be called if this timer expires.
// IN  - buffer  : stack's context data to be returned in stopTimer.
// IN  - key     : timer key, unique to this timer.
// IN  - err     : If the timer cannot be started, this error needs to be set.
//
// Return Value: SipBool: 1 if timer addition succeeds, 0 otherwise.
////////////////////////////////////////////////////////////////////////////////
SipBool fast_startTimer
    (SIP_U32bit         duration,
     SIP_S8bit          restart,
     sip_timeoutFuncPtr timeoutfunc,
     SIP_Pvoid          buffer,
     SipTimerKey       *key,
     SipError          *err)
{

  LogINGwTrace(false, 0, "IN fast_startTimer");

  // To count the number of retransmissions, assume that any message started
  // with a duration of more than 500 millisecs is a retransmission
  // Nah, the above logic doesn't work with HSS 2.0 stack onwards.  Have to
  // explicitly get the retrans count.  This is a quick fix for demo.  Later
  // enable retrans callbacks and count from there.  This one may not be safe.



  long llThreadId = pthread_self();
  logger.logINGwMsg(false,TRACE_FLAG,0,"fast_startTimer threadId",llThreadId);

  if(buffer && (((SipTimerBuffer *)buffer)->dRetransCount > 0))
  {
    ++(INGwSpSipStackIntfLayer::mplSipRetransCount[llThreadId - gllowThreadId]);
  }
  //if((duration == 500)) 
  else
  {
    if(NULL == buffer)
    {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,"fast_startTimer buffer NULL");
    }

    if(INGwSpSipStackIntfLayer::miRetransTime > 500)
    {
      duration = INGwSpSipStackIntfLayer::miRetransTime;
    }
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0, "fast_startTimer: duration <%u>", duration);

  if(buffer)
    logger.logINGwMsg(false, TRACE_FLAG, 0, "fast_startTimer: retrans count <%u>", (int)(((SipTimerBuffer *)buffer)->dRetransCount));
  
  // Store the timer key and the buffer in the timer map for this thread.
  // The buffer will need to be returned when the timer is stopped by the stack.
  if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().
      getStackTimer().startTimer(key, buffer, duration, timeoutfunc))
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "fast_startTimer: startTimer failed...");
    *err = E_TIMER_DUPLICATE;
    LogINGwTrace(false, 0, "OUT fast_startTimer");
    return SipFail;
  }

  LogINGwTrace(false, 0, "OUT fast_startTimer");
  return SipSuccess;
} // end of fast_startTimer

////////////////////////////////////////////////////////////////////////////////
// Method: fast_stopTimer
// Description: This method is called by the stack when it wants a timer to be
//              stopped, because an expected SIP message was received.  This
//              method searches in the timer map the entry whose key matches
//              the given key, and returns the buffer found in that entry.
// IN  - inKey  : key of the timer which needs to be stopped.
// OUT - outkey : the key structure which was given when startTimer was called
// OUT - buffer : the buffer which was given when startTimer was called
////////////////////////////////////////////////////////////////////////////////
SipBool fast_stopTimer
    (SipTimerKey    *inkey,
     SipTimerKey   **outkey,
     SIP_Pvoid      *buffer,
     SipError       *err)
{

  LogINGwTrace(false, 0, "IN fast_stopTimer");
  if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().
    getStackTimer().stopTimer(inkey, outkey, buffer))
  {
    *err = E_TIMER_NO_EXIST;
    logger.logINGwMsg(false, WARNING_FLAG, 0,"stopTimer Failed...");
    LogINGwTrace(false, 0, "OUT fast_stopTimer");
    return SipFail;
  }

  LogINGwTrace(false, 0, "OUT fast_stopTimer");
  return SipSuccess;
} // end of fast_stopTimer

////////////////////////////////////////////////////////////////////////////////
// Method: sip_indicateTimeOut
// Description: This method is called by the stack when a timer times out after
//              retransmissions.  This method extracts the timer context from
//              the provided context, and gets the call id from there.  From the
//              callid, the HSS call leg is retrieved, and the INGwSpSipConnection
//              is got from its application data context.  Then the timeout
//              method is invoked on the INGwSpSipConnection.
// IN  - context: The timer context which was set in the call to SendCallToPeer.
////////////////////////////////////////////////////////////////////////////////
void sip_indicateTimeOut(SipEventContext *context)
{
  LogINGwTrace(false, 0, "IN sip_indicateTimeOut");

  // If the context data in this event context is not NULL, then it must be
  // INGwSipTimerContext structure.  Extract the call id and get the connection
  // from it. When event data is freed context also gets freed.

  if((context != NULL) && (context->pData  != NULL)) 
  {
     Sdf_st_appData *appdata = (Sdf_st_appData *)(context->pData);
     if(appdata->pData != NULL)
     {
        INGwSipTimerContext *timerCtxt = (INGwSipTimerContext *)appdata->pData;

        if(timerCtxt != NULL)
        {
           indicateTimeoutToCall(timerCtxt, 0);
        }
     }
  }

  sip_freeEventContext(context);

  LogINGwTrace(false, 0, "OUT sip_indicateTimeOut");
} // end of sip_indicateTimeOut

void indicateTimeoutToCall(INGwSipTimerContext *aContext, unsigned int aTimerid)
{
  LogINGwTrace(false, 0, "IN indicateTimeoutToCall");
  INGwSipTimerContext *timercontext = aContext;
  const char *callidstr    = timercontext->mCallId;
  Sdf_st_callObject *callobj      = NULL;
  INGwSpSipConnection   *connection   = NULL;
  bool               connfromcall = false;

/*
	// Special Handling for Notify as we don't store call/conn for it
	if (timercontext->mMethodType == INGW_SIP_METHOD_TYPE_NOTIFY)
	{
		int lResponseCode = 408;
		std::string lCallIdStr = std::string(callidstr);
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
											"indicateTimeoutToCall: "
											"Notify TIMEOUT for callid <%s>", 
											callidstr);

		INGwSpSipProvider& l_bpSipProvider = INGwSpSipProvider::getInstance();
	  INGwSpSipCallController* l_callCntrl = l_bpSipProvider.getCallController();	
		l_callCntrl->handleInboundMsgFailure(lResponseCode, lCallIdStr);
    LogINGwTrace(false, 0, "OUT indicateTimeoutToCall");
    return;
	}
*/
  // The no answer timer delivery cannot be simply done on the call leg.
  // The callid in case of no-ans timer is actually the call id (rather than
  // call leg id).  The connection has to be retrieved from the call and the
  // timeout given to that connection.  This is necessary because no-answer
  // timer will not necessarily be delivered to the connection object which
  // started it.  The connection which started it may be dead by now, and
  // replaced with a new active connection.
  if(timercontext->mType != INGwSipTimerType::NOANSWER_TIMER)
  {
    if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().
         getCallTable().get(callidstr, &callobj))
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
											 "indicateTimeoutToCall: Error getting call-leg for callid <%s>", 
											 callidstr);
      LogINGwTrace(false, 0, "OUT indicateTimeoutToCall");
      return;
    }

    INGwConnectionContext *connectioncontext = 
      (INGwConnectionContext *)(callobj->pAppData->pData);
    if(connectioncontext)
    {
      connection = (INGwSpSipConnection *)(connectioncontext->mSipConnection);
    }
    else
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
												"indicateTimeoutToCall: "
												"Error getting connection-context for callid <%s>", 
												callidstr);
    }
  } // end of if(timertype != NOANSWER
  else
  {
    // The timer IS no answer.  From the callid and connection id, get the
    // connection.
    INGwSpSipProvider& l_bpSipProvider = INGwSpSipProvider::getInstance();

    INGwSpSipCallController* l_callCntrl = l_bpSipProvider.getCallController(); 
    if(0 == l_callCntrl)
    {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
											 "indicateTimeoutToCall: Error getting call controller from sip provider.");
      LogINGwTrace(false, 0, "OUT indicateTimeoutToCall");
      return;
    }

    INGwSpSipConnection* conn = NULL;
		string lCallIdStr = callidstr;
    INGwSpSipCall* call = l_callCntrl->getCall(lCallIdStr);
    INGwIfrUtlRefCount_var callMonitor(call);

    // Do not create the RefCount_var for the below getConnection.  The scope
    // ends before we actually use the connection.  Do an explicit releaseRef
    // after we are done using the connection.
    if(call)
    {
      conn = call->getConnection(timercontext->mConnId);
      connection = dynamic_cast<INGwSpSipConnection*>(conn);
      connfromcall = true;
    }
  }

  if(connection)
  {
    // STATISTICS: Number of stack timeouts (check for timer type), with method
    // name: Increment
   
    connection->getRef();
    INGwIfrUtlRefCount_var sipConnVar(connection);

    connection->indicateTimeout(timercontext->mMethodType,
                                timercontext->mType, aTimerid);
    if(connfromcall)
      connection->releaseRef();
  }
  else
  {
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, "indicateTimeOutToCall: Could not find connection");
  }

  LogINGwTrace(false, 0, "OUT indicateTimeoutToCall");
} // end of indicateTimeOutToCall

void handleTimeout(INGwSipTimerType *aMsg, unsigned int aTimerid)
{
  INGwSipTimerType *timertype = aMsg;

  if(!timertype)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "handleTimeout: NULL timer type in the TimerMsg context: %p", aMsg);

    // Recycle the timer msg.

    LogINGwTrace(false, 0, "OUT handleTimeout");
  }
  else
  {
    switch(timertype->mType)
    {
      case INGwSipTimerType::STACK_TIMER   :
      {
        INGwSpStackTimerContext *timercontext = 
          dynamic_cast<INGwSpStackTimerContext *>(timertype);
        if(!timercontext)
        {
          logger.logINGwMsg(false, ERROR_FLAG, 0, "handleTimeout: error casting to INGwSpStackTimerContext");
          LogINGwTrace(false, 0, "OUT handleTimeout");
          return;
        }
        else
        {
          // Inform the stack timer class.  It will then call the timeout
          // function of the stack
          logger.logINGwMsg(false, TRACE_FLAG, 0, "handleTimeout: XXXXXX : timerid <%u>", aTimerid);
          INGwSpSipProvider::getInstance().getThreadSpecificSipData().
             getStackTimer().indicateTimeout(timercontext);
        }
        delete timertype;
        break;
      } // end of INGwSipTimerType::STACK_TIMER
      case INGwSipTimerType::NOANSWER_TIMER:
      case INGwSipTimerType::INITRESP_TIMER:
      case INGwSipTimerType::TRANSCOMPL_TIMER:
      case INGwSipTimerType::SIPIVR_TIMER:
      {
        INGwSipTimerContext *timercontext =
          dynamic_cast<INGwSipTimerContext *>(timertype);
        if(!timercontext)
        {
          logger.logINGwMsg(false, ERROR_FLAG, 0, "handleTimeout: error casting to INGwSipTimerContext");
          LogINGwTrace(false, 0, "OUT handleTimeout");
          return;
        }
        else
        {
          indicateTimeoutToCall(timercontext, aTimerid);
        }
        delete timertype;
        break;
      }

      case INGwSipTimerType::SESSION_TIMER: {

        INGwSpSessionTimerContext* l_sessTmrCtx =
               dynamic_cast<INGwSpSessionTimerContext*>(timertype);
        if(0 == l_sessTmrCtx) {
          logger.logINGwMsg(false, ERROR_FLAG, 0,  
            "cast error from INGwSipTimerType To INGwSpSessionTimerContext");
          LogINGwTrace(false, 0, "OUT handleTimeout");
          return;
        }

        //check timer ID stored is the same as expired.

        Sdf_st_sessionTimerContextInfo* l_hssSessTmrCtx = 
          reinterpret_cast<Sdf_st_sessionTimerContextInfo*>(
          (l_sessTmrCtx->mStackTmrHandle).pContextInfo);

        if(0 == l_hssSessTmrCtx) {
          logger.logINGwMsg(false, ERROR_FLAG, 0,  
            "Sdf_st_sessionTimerContextInfo Null in timer context");
          LogINGwTrace(false, 0, "OUT handleTimeout");
          return;
        }

        Sdf_st_callObject* l_hssCallObject = 
                                  l_hssSessTmrCtx->pCallObject;

        if(0 == l_hssCallObject) {
          logger.logINGwMsg(false, ERROR_FLAG, 0,  
            "l_hssCallObject Null in Sdf_st_sessionTimerContextInfo");
          LogINGwTrace(false, 0, "OUT handleTimeout");
          return;
        }

        unsigned int* l_hssSessTmrId = reinterpret_cast<unsigned int*>(
          l_hssCallObject->pSessionTimer->pTimerId);

        if(0 == l_hssSessTmrId) {
					logger.logINGwMsg(false, ERROR_FLAG, 0,
							"l_hssCallObject->pSessionTimer->pTimerId  Null for call-id: %s", 
							l_hssCallObject->pCommonInfo->pCallid );
          LogINGwTrace(false, 0, "OUT handleTimeout");
          return;
        }

        //get bp connection from hss call object
        INGwSpSipConnection* l_bpSipConnection =
                INGwSpSipUtil::getINGwSipConnFromHssCall(l_hssCallObject);

        if(0 == l_bpSipConnection) {
          logger.logINGwMsg(false, ERROR_FLAG, 0,
                       "INGwSpSipConnection Null in INGwConnectionContext for call-id: <%d>", 
											 l_hssCallObject->pCommonInfo->pCallid);
          logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT handleTimeout");
          LogINGwTrace(false, 0, "OUT handleTimeout");
          return;
        }

        l_bpSipConnection->getRef();
        INGwIfrUtlRefCount_var sipConnVar(l_bpSipConnection);

        INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn = 
                                                              l_bpSipConnection;

        Sdf_st_error pErr;
        if((*l_hssSessTmrId) == l_bpSipConnection->mActiveSessionTimerId) {

          l_bpSipConnection->mActiveSessionTimerId = 0;

          if(l_sessTmrCtx->mpTimeoutFunc(l_sessTmrCtx->mTimertype,
            (l_sessTmrCtx->mStackTmrHandle).pContextInfo, 
             &pErr) != Sdf_co_success) {

						logger.logINGwMsg(false, ERROR_FLAG, 0,
								"Stack Session-timer-timeout function failed for call-id: %s",
								l_hssCallObject->pCommonInfo->pCallid );
					}

					delete l_sessTmrCtx;
				}
				else {
          logger.logINGwMsg(false, ERROR_FLAG, 0,
            "Session-Timer Id's do not match. hss:<%x>, INGw:<%x>, timer:<%x> timeout ignored for call-id: <%s>", 
            *l_hssSessTmrId, l_bpSipConnection->mActiveSessionTimerId, aTimerid, 
						l_hssCallObject->pCommonInfo->pCallid);
        }

        INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn = NULL;
      }
      break;

      case INGwSipTimerType::REMOTE_RETRANS_PURGE_TIMER:
      {
        logger.logINGwMsg(false, TRACE_FLAG, 0, "handleTimeout: Calling remote retransmission entry purge functions on stack");
        Sdf_st_error sdferror;
        Sdf_ty_retVal status = Sdf_co_fail;
        Sdf_st_hash* remotehash = NULL;

        status =
          sdf_cbk_uaGetRemoteRetransHashTable(&remotehash, NULL, &sdferror);
        if(status != Sdf_co_success)
        {
          logger.logINGwMsg(false, ERROR_FLAG, 0, "handleTimeout: Error getting remote retrans hash table");
        }

        if(status == Sdf_co_success)
        {
          status = sdf_ivk_uaPurgeExpiredRemoteRetransEntries
            (Sdf_en_remoteRetransTimer, remotehash, &sdferror);
          if(status != Sdf_co_success)
          {
            INGwSpSipUtil::checkError(status, sdferror);
            logger.logINGwMsg(false, ERROR_FLAG, 0, "handleTimeout: Error calling remote retrans entry purging function on stack");
          }
          else
          {
            logger.logINGwMsg(false, TRACE_FLAG, 0, "handleTimeout: Successfully called remote retrans entry purging function on stack");
          }
        }

        // Start another remote retrans timer
        if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().getStackTimer()
           .startRemoteRetransPurgeTimer(REMOTE_RETRANS_PURGE_INTERVAL))
        {
          logger.logINGwMsg(false, ERROR_FLAG, 0, "handleTimeout: Error starting remote retrans purge timer");
        }
        else
          logger.logINGwMsg(false, TRACE_FLAG, 0, "handleTimeout: Successfully started remote retrans purge timer");

        delete timertype;
        break;
      }
      default:
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0, "handleTimeout: Unknown timer type");
        break;
      }
    } // end of switch
  } // end of else
  LogINGwTrace(false, 0, "OUT handleTimeout");
} // end of handleTimeout

Sdf_ty_retVal sdf_cbk_uaCancelReceived
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaCancelReceived");
  Sdf_ty_retVal ret =
    requestCallback(ppCallObj, pEventContext, Sdf_co_null, pErr, false);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaCancelReceived");
  return ret;
}

Sdf_ty_retVal sdf_cbk_uaCancelCompleted
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaCancelCompleted");
  Sdf_ty_retVal ret = responseCallback(ppCallObj, pEventContext, pErr, false);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaCancelCompleted");
  return ret;
}

Sdf_ty_retVal sdf_cbk_uaCallTerminateRequest
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaCallTerminateRequest");
  Sdf_ty_retVal ret =
    requestCallback(ppCallObj, pEventContext, Sdf_co_null, pErr, false);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaCallTerminateRequest");
  return ret;

} // end of sdf_cbk_uaCallTerminateRequest

////////////////////////////////////////////////////////////////////////////////
// Function: sdf_cbk_uaCallTerminated
// Description: This function is called by the stack when a final success
//              response is received for a call termination request like BYE.
// IN  - ppCallObj: The call object for which the message is received.
// IN  - pEventContext: The event context which was passed to the handle call.
// OUT - pErr: The application can return an error if something is not right.
//       This parameter and the return value will both be returned to the
//       handle call caller.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal sdf_cbk_uaCallTerminated
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaCallTerminated");
  Sdf_ty_retVal ret = responseCallback(ppCallObj, pEventContext, pErr, false);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaCallTerminated");
  return ret;
}

Sdf_ty_retVal sdf_cbk_uaTransactionCompleted
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_overlapTransInfo *pOverlapTransInfo,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaTransactionCompleted");
  Sdf_ty_retVal ret = responseCallback(ppCallObj, pEventContext, pErr, false, pOverlapTransInfo);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaTransactionCompleted");
  return ret;
}

Sdf_ty_retVal sdf_cbk_uaTransactionReceived
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_overlapTransInfo *pOverlapTransInfo,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaTransactionReceived");
  Sdf_ty_retVal ret =  requestCallback(ppCallObj, pEventContext, pOverlapTransInfo, pErr, false);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaTransactionReceived");
  return ret;
}

Sdf_ty_retVal sdf_cbk_uaReInviteReceived
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaReInviteReceived");
  Sdf_ty_retVal ret =
    requestCallback(ppCallObj, pEventContext, Sdf_co_null, pErr, true);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaReInviteReceived");
  return ret;
}

Sdf_ty_retVal sdf_cbk_uaReInviteFailed
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaReInviteFailed");
  Sdf_ty_retVal ret = responseCallback(ppCallObj, pEventContext, pErr, true);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaReInviteFailed");
  return ret;
}

Sdf_ty_retVal sdf_cbk_uaReInviteAccepted
    (Sdf_st_callObject **ppCallObj,
    Sdf_st_eventContext *pEventContext,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaReInviteAccepted");
  Sdf_ty_retVal ret = responseCallback(ppCallObj, pEventContext, pErr, true);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaReInviteAccepted");
  return ret;
}

Sdf_ty_authResult sdf_cbk_uaAuthenticateUser
    (Sdf_st_callObject **ppCallObj,
    SipAddrSpec *pAddrSpec,
    Sdf_st_uasAuthInfo *pUasAuthInfo,
    Sdf_ty_pvoid *ppContext,
    Sdf_st_error *pErr)
{
  printf("\n****sdf_cbk_uaAuthenticateUser ****\n");
  return Sdf_en_error;
}

Sdf_ty_authResult sdf_cbk_uaChallengeUser
    (Sdf_st_callObject **ppCallObj,
    SipAddrSpec *pAddrSpec,
    Sdf_st_authenticationParams *pAuthParams,
    Sdf_ty_pvoid *ppContext,
    Sdf_st_error *pErr)
{
  printf("\n****sdf_cbk_uaChallengeUser ****\n");
  return Sdf_en_error;
}

//////////////////////////////////////////////////////////////
//
// sdf_cbk_uaUnknownMethodReceived
//
//////////////////////////////////////////////////////////////
Sdf_ty_retVal
sdf_cbk_uaUnknownMethodReceived (Sdf_st_callObject** ppCallObj,
Sdf_st_eventContext* pEventContext,
Sdf_st_overlapTransInfo *pOverlapTransInfo,
Sdf_st_error* pErr) {
  LogINGwTrace(false, 0, "IN sdf_cbk_uaUnknownMethodReceived");
  Sdf_ty_retVal ret = requestCallback(ppCallObj, pEventContext, Sdf_co_null, pErr, false);
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaUnknownMethodReceived");
  return ret;
}

//////////////////////////////////////////////////////////////
//
// sdf_cbk_uaRPRReceived
//
//////////////////////////////////////////////////////////////
Sdf_ty_retVal
sdf_cbk_uaRPRReceived (Sdf_st_callObject** ppCallObj,
  Sdf_st_overlapTransInfo* pOverlapTransInfo,
  Sdf_st_eventContext* pEventContext,
  Sdf_st_error* pErr) {

  LogINGwTrace(false, 0, "IN sdf_cbk_uaRPRReceived");
  Sdf_ty_retVal   l_retVal   = Sdf_co_success;
  Sdf_st_appData* l_pAppData = Sdf_co_null;

  if(sdf_ivk_uaGetAppDataFromCallObject((*ppCallObj), &l_pAppData,
                                         pErr) != Sdf_co_success) {
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
          "sdf_ivk_uaGetAppDataFromCallObject failed ... \n");
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "sdf_ivk_uaGetAppDataFromCallObject failed ...");
    LogINGwTrace(false, 0, "OUT sdf_cbk_uaRPRReceived");
    return Sdf_co_fail;
  }

  INGwConnectionContext* l_connCtx =
                         (INGwConnectionContext*)(l_pAppData->pData);
  if(0 == l_connCtx) {
    sdf_ivk_uaFreeAppData(l_pAppData);
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "l_pAppData->pData null ... \n");
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "l_pAppData->pData null ...");
    LogINGwTrace(false, 0, "OUT sdf_cbk_uaRPRReceived");
    return Sdf_co_fail;
  }

  INGwSpSipConnection* l_bpSipConn = l_connCtx->mSipConnection;
  if(0 == l_bpSipConn) {
    sdf_ivk_uaFreeAppData(l_pAppData);
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "l_pAppData->pData->mSipConnection null ... \n");
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "l_pAppData->pData->mSipConnection null ...");
    LogINGwTrace(false, 0, "OUT sdf_cbk_uaRPRReceived");
    return Sdf_co_fail;
  }

  l_bpSipConn->getRef();
  INGwIfrUtlRefCount_var sipConnVar(l_bpSipConn);
  
  Sdf_ty_u16bit l_respCode = 0;
  l_retVal = sdf_ivk_uaGetRespCodeFromSipMessage(
             //(*ppCallObj)->pUacTransaction->pSipMsg,
             pOverlapTransInfo->pSipMsg,
             &l_respCode, pErr);
  
  if(Sdf_co_fail == l_retVal) {
    sdf_ivk_uaFreeAppData(l_pAppData);
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "sdf_ivk_uaGetRespCodeFromSipMessage failed ... \n");
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "sdf_ivk_uaGetRespCodeFromSipMessage failed ...");
    LogINGwTrace(false, 0, "OUT sdf_cbk_uaRPRReceived");
    return Sdf_co_fail;
  }

  INGwSipMethodType methodType = 
         INGwSpSipUtil::getMethodFromSipMessage(pOverlapTransInfo->pSipMsg);

  if((l_bpSipConn->getMajorState() == CONNECTION_CONNECTED) &&
     (methodType == INGW_SIP_METHOD_TYPE_INVITE))
  {
    methodType = INGW_SIP_METHOD_TYPE_REINVITE;
  }

  sdf_ivk_uaFreeAppData(l_pAppData);

  l_retVal = l_bpSipConn->stackCallbackResponse(methodType, l_respCode,
                                             ppCallObj,
                                             pEventContext, pErr, 
                                             pOverlapTransInfo);

  LogINGwTrace(false, 0, "OUT sdf_cbk_uaRPRReceived");
  return l_retVal;
}

//////////////////////////////////////////////////////////////
//
// sdf_cbk_uaOverlapTransactionReceived
//
//////////////////////////////////////////////////////////////
Sdf_ty_retVal 
sdf_cbk_uaOverlapTransactionReceived (Sdf_st_callObject** ppCallObj,
  Sdf_st_overlapTransInfo* pOverlapTransInfo,
  Sdf_st_eventContext* pEventContext,
  Sdf_st_error* pErr) {

  LogINGwTrace(false, 0, "IN sdf_cbk_uaOverlapTransactionReceived");
  Sdf_ty_retVal   l_retVal   = Sdf_co_success;
  Sdf_st_appData* l_pAppData = Sdf_co_null;

  if(sdf_ivk_uaGetAppDataFromCallObject((*ppCallObj), &l_pAppData,
                                         pErr) != Sdf_co_success) {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
         "sdf_ivk_uaGetAppDataFromCallObject failed ... \n");
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "sdf_ivk_uaGetAppDataFromCallObject failed ...");
    logger.logINGwMsg(false, TRACE_FLAG, 0,
                      "OUT sdf_cbk_uaOverlapTransactionReceived \n");
    return Sdf_co_fail;
  }

  INGwConnectionContext* l_connCtx =
                         (INGwConnectionContext*)(l_pAppData->pData);
  if(0 == l_connCtx) {
    sdf_ivk_uaFreeAppData(l_pAppData);
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "l_pAppData->pData null ... \n");
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "l_pAppData->pData null ...");
    logger.logINGwMsg(false, TRACE_FLAG, 0, 
                        "OUT sdf_cbk_uaOverlapTransactionReceived \n");
    return Sdf_co_fail;
  }

  INGwSpSipConnection* l_bpSipConn = l_connCtx->mSipConnection;
  if(0 == l_bpSipConn) {
    sdf_ivk_uaFreeAppData(l_pAppData);
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "l_pAppData->pData->mSipConnection null ... \n");
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "l_pAppData->pData->mSipConnection null ...");
    logger.logINGwMsg(false, TRACE_FLAG, 0, 
                       "OUT sdf_cbk_uaOverlapTransactionReceived \n");
    return Sdf_co_fail;
  }
  
  l_bpSipConn->getRef();
  INGwIfrUtlRefCount_var sipConnVar(l_bpSipConn);
  
  sdf_ivk_uaFreeAppData(l_pAppData);

  INGwSipMethodType methodType = INGwSpSipUtil::getMethodFromSipMessage(
                      pOverlapTransInfo->pSipMsg);

  l_retVal = l_bpSipConn->stackCallbackRequest(methodType, ppCallObj,
                                             pEventContext, pErr, 
                                             pOverlapTransInfo);

  logger.logINGwMsg(false, TRACE_FLAG, 0, 
                        "OUT sdf_cbk_uaOverlapTransactionReceived \n");

  return l_retVal;
}

/////////////////////////////////////////////////////////
//
// sdf_cbk_uaOverlapTransactionCompleted
//
/////////////////////////////////////////////////////////
Sdf_ty_retVal 
sdf_cbk_uaOverlapTransactionCompleted (Sdf_st_callObject** ppCallObj,
    Sdf_st_overlapTransInfo* pOverlapTransInfo,
    Sdf_st_eventContext* pEventContext,
    Sdf_st_error* pErr) {

  LogINGwTrace(false, 0, "IN sdf_cbk_uaOverlapTransactionCompleted");
  Sdf_ty_retVal   l_retVal   = Sdf_co_success;
  Sdf_st_appData* l_pAppData = Sdf_co_null;

  if(sdf_ivk_uaGetAppDataFromCallObject((*ppCallObj), &l_pAppData,
                                         pErr) != Sdf_co_success) {
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
          "sdf_ivk_uaGetAppDataFromCallObject failed ... \n");
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "sdf_ivk_uaGetAppDataFromCallObject failed ...");
    logger.logINGwMsg(false, TRACE_FLAG, 0, 
          "OUT INGwSpSipStackIntfLayer::sdf_cbk_uaOverlapTransactionCompleted \n");
    return Sdf_co_fail;
  }

  INGwConnectionContext* l_connCtx =
                         (INGwConnectionContext*)(l_pAppData->pData);
  if(0 == l_connCtx) {
    sdf_ivk_uaFreeAppData(l_pAppData);
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "l_pAppData->pData null ... \n");
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "l_pAppData->pData null ...");
    logger.logINGwMsg(false, TRACE_FLAG, 0, 
               "OUT sdf_cbk_uaOverlapTransactionCompleted \n");
    return Sdf_co_fail;
  }

  INGwSpSipConnection* l_bpSipConn = l_connCtx->mSipConnection;
  if(0 == l_bpSipConn) {
    sdf_ivk_uaFreeAppData(l_pAppData);
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "l_pAppData->pData->mSipConnection null ... ");
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "l_pAppData->pData->mSipConnection null ...");
    logger.logINGwMsg(false, TRACE_FLAG, 0, 
                    "OUT sdf_cbk_uaOverlapTransactionCompleted");
    return Sdf_co_fail;
  }

  l_bpSipConn->getRef();
  INGwIfrUtlRefCount_var sipConnVar(l_bpSipConn);
  
  Sdf_ty_u16bit l_respCode = 0;
  l_retVal = sdf_ivk_uaGetRespCodeFromSipMessage(
             pOverlapTransInfo->pSipMsg,
             &l_respCode, pErr);
  
  if(Sdf_co_fail == l_retVal) {
    sdf_ivk_uaFreeAppData(l_pAppData);
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "sdf_ivk_uaGetRespCodeFromSipMessage failed ... \n");
    pErr->errCode = Sdf_en_callObjectAccessError;
    strcpy(pErr->ErrMsg, "sdf_ivk_uaGetRespCodeFromSipMessage failed ...");
    logger.logINGwMsg(false, TRACE_FLAG, 0, 
          "OUT INGwSpSipStackIntfLayer::sdf_cbk_uaOverlapTransactionCompleted \n");
    return Sdf_co_fail;
  }
  
  sdf_ivk_uaFreeAppData(l_pAppData);

  INGwSipMethodType methodType = 
             INGwSpSipUtil::getMethodFromSipMessage(pOverlapTransInfo->pSipMsg);
                     
  l_retVal = l_bpSipConn->stackCallbackResponse(methodType, l_respCode,
                                             ppCallObj, pEventContext, 
                                             pErr, pOverlapTransInfo);

  logger.logINGwMsg(false, TRACE_FLAG, 0, 
          "OUT INGwSpSipStackIntfLayer::sdf_cbk_uaOverlapTransactionCompleted \n");

  return l_retVal;
}

//////////////////////////////////////////////////////////////
//
// sdf_cbk_uaFreeApplicationData
//
//////////////////////////////////////////////////////////////
Sdf_ty_retVal sdf_cbk_uaFreeApplicationData
    (Sdf_ty_pvoid *ppData)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaFreeApplicationData");
  // Check here whether the pData is empty or not.  If not empty, it should
  // contain the connection context.  Delete the context.

  if(*ppData)
  {
    delete (INGwConnectionContext *)(*ppData);
    *ppData = NULL;
  }

  LogINGwTrace(false, 0, "OUT sdf_cbk_uaFreeApplicationData");
  return Sdf_co_success;
}

Sdf_ty_retVal sdf_cbk_uaCloneAppData
    (Sdf_st_appData *pDestInfo,
    Sdf_st_appData *pSrcInfo,
    Sdf_st_error *pErr)
{
  printf("\n****sdf_cbk_uaCloneAppData ****\n");
  return Sdf_co_success;
}

Sdf_ty_retVal sdf_cbk_uaFreeEventContextData
    (Sdf_ty_pvoid *ppData)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaFreeEventContextData");
  // If the pData is not NULL, it should contain the app data.  Cast and
  // delete.
  if(*ppData)
  {
    LogINGwTrace(false, 0, "calling sdf_ivk_uaFreeAppData");

    // The context data in the app specific data should be the timer
    // context.  Cast it here and delete.
    // printf("RRR: sdf_cbk_uaFreeEventContextData appdata<%p>, timercont<%p>\n", *ppData, ((Sdf_st_appData*)(*ppData))->pData);
    if(((Sdf_st_appData*)(*ppData))->pData)
      delete (INGwSipTimerContext *)(((Sdf_st_appData*)(*ppData))->pData);
    ((Sdf_st_appData*)(*ppData))->pData = NULL;

    sdf_ivk_uaFreeAppData((Sdf_st_appData *)(*ppData));
    *ppData = NULL;
  }

  LogINGwTrace(false, 0, "OUT sdf_cbk_uaFreeEventContextData");
  return Sdf_co_success;
}

/*****************************************************************
** FUNCTION: sdf_cbk_uaStartTimer
**
** // The DESSRIPTION no longer applies.
** // The role of this function has expanded.
**
** DESCRIPTION: This callback is issued by the toolkit to the application
**              if the toolkit is compiled without the
**              SDF_USE_INTERNALTHREADS. The application should provide
**              the timer implementation whereby it invokes the timeoutfunc
**              after expiry of the "duration"
**
** PARAMETERS:
**      duration(IN)    : The duration in milliseconds after which the
**                          application must call timeoutfunc
**      restart(IN)     : Currently not used. 0 to be passed.
**      timeoutfunc(IN) : A function supplied by the toolkit. The
**                          application must invoke this function when
**                          the timer expires.
**      pErr(OUT): Error code may be returned in this if the call fails.
**
*******************************************************************/
extern Sdf_ty_retVal sdf_cbk_uaStartTimer _ARGS_((
    Sdf_ty_u32bit           dDuration,
    Sdf_ty_timerType        dTimerType,
    Sdf_ty_pvoid            pContextInfo,
    Sdf_ty_pvoid            pAppData,
    Sdf_ty_TimertimeOutFunc timeoutFunc,
    Sdf_ty_pvoid            *ppTimerHandle,
    Sdf_st_error            *pErr)) {

  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN sdf_cbk_uaStartTimer");

  if(dTimerType == Sdf_en_sessionTimer) {
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
      "dTimerType:<Sdf_en_sessionTimer>, dDuration:<%d>", dDuration);

    //start Session timer....................
    bool retval = INGwSpSipProvider::getInstance().getThreadSpecificSipData().
      getStackTimer().startSessionTimer(dDuration, dTimerType,
                                      pContextInfo, ppTimerHandle,
                                      timeoutFunc);
  }
  else {
    logger.logINGwMsg(false, WARNING_FLAG, 0, 
      "Timer Type:<%d>, no processing to do", dTimerType);
  }
  
  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT sdf_cbk_uaStartTimer");
  return Sdf_co_success;
}


/***************************************************************************
 ** FUNCTION:       sdf_cbk_uaStopTimer
 **
 ** DESCRIPTION:    Stop the specified timer. pTimerHandle must be the handle
 **                 that was returned by sdf_cbk_uaStartTimer. pContextInfo arg
 **                 that was passed to sdf_cbk_uaStartTimer will be returned in
 **                 ppContextInfo.
 **
 ***************************************************************************/
extern Sdf_ty_retVal sdf_cbk_uaStopTimer _ARGS_((
    Sdf_ty_pvoid    pTimerHandle,
    Sdf_ty_pvoid    *ppContextInfo,
    Sdf_st_error    *pErr)) {

  LogINGwTrace(false, 0, "IN sdf_cbk_uaStopTimer");

  bool retval = INGwSpSipProvider::getInstance().getThreadSpecificSipData().
      getStackTimer().stopSessionTimer(pTimerHandle, ppContextInfo);

  LogINGwTrace(false, 0, "OUT sdf_cbk_uaStopTimer");

  if(retval)
  {
     return Sdf_co_success;
  }

  return Sdf_co_fail;
}

SipBool sip_sendToNetwork
    (SIP_S8bit *buffer,
    SIP_U32bit buflen,
    SipTranspAddr *transpaddr,
    SIP_S8bit transptype,
    SipError *err)
{
	LogINGwTrace(false, 0, "IN sip_sendToNetwork");

	logger.logMsg(TRACE_FLAG, 0, "To send [%x] len [%d]", buffer, buflen);
	//
	// If the received Sip Msg doesn't pass screening..We'll just drop it.

	if( INGwSpSipProviderConfig::isOutgoingMessageScreeningEnabled() )
	{
		if( ! (INGwSpSipUtil::screenSipMessageForCorruption(buffer, buflen) ) )
		{
			// dump as much call info here as much safely possible
			logger.logMsg(ERROR_FLAG, 0, " Corrupted Sip Msg to send [%s] len [%d]", buffer, buflen);
			LogINGwTrace(false, 0, "OUT sip_sendToNetwork");
			return SipSuccess;
		}
	}

	char locBuffer[MAX_SIPBUF_SIZE + 50];

	char *outmsg = locBuffer + 50;
	memcpy(outmsg, buffer, buflen);
	outmsg[buflen] = 0;
  
  //INGwSpData* rawBuffObj = INGwSpDataFactory::getInstance().getNewObject();
  //rawBuffObj->setBody((const char *)buffer, buflen);
  //rawBuffObj->setBodyLength(buflen);
  
	// The dysoft stack in the sip sim crashes if the Refresher parameter
	// begins with an 'R' instead of an 'r'.  Correct that here.
	if(simSTHack)
	{
		char* check = strstr(outmsg, "Refresher=ua");
		if(check)
			check[0] = 'r';
	}

	// Check if Firewall Proxy has been configured..
	char *destIpAddr = transpaddr->dIpv4;
	int destPort = transpaddr->dPort;
  int lSockFd = transpaddr->dSockFd;
	int retVal = 0;
#ifdef QUEUE_CHANGES  
  RawSipData * rawSipData = new RawSipData;
  rawSipData->mBuf    = new char[buflen+1];
  rawSipData->mBufLen = buflen;
  memcpy(rawSipData->mBuf, buffer, buflen);
  rawSipData->mBuf[buflen]=0;
  
  rawSipData->mPort   =  transpaddr->dPort;
  rawSipData->IpAddr  = new char[strlen(destIpAddr)+1];
  strcpy(rawSipData->IpAddr, destIpAddr);
  INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
   
  lWorkUnit->meWorkType    = INGwIfrMgrWorkUnit::SEND_NOTIFY_TO_NW;
  lWorkUnit->mpWorkerClbk  = &(INGwSpSipStackIntfLayer::instance());
  lWorkUnit->mpcCallId     = NULL;
  rawSipData->mSipConn = 
  INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn;

  lWorkUnit->mpContextData = (void*)rawSipData;
#endif  
 
	const GwInfo *fwProxyInfo = INGwSpSipProviderConfig::getFirewallProxyInfo();
	if( NULL != fwProxyInfo) {
		destIpAddr = const_cast <char *> ((fwProxyInfo->mpHost).c_str() );
		destPort = fwProxyInfo->miPort;
#ifdef QUEUE_CHANGES 
    strcpy(rawSipData->IpAddr, destIpAddr);
    rawSipData->mPort = destPort;
#endif
	}

#ifdef QUEUE_CHANGES
  rawSipData->mTransptype = transptype;
   
  bool ret = 
  INGwIfrMgrThreadMgr::getInstance().postMsgForSipReq(lWorkUnit);
  //SipSuccess:SipFail;
  if(ret) {
    return SipSuccess; 
  } 
  else {
    delete lWorkUnit;
    delete [] rawSipData->IpAddr;
    delete [] rawSipData->mBuf;
    delete rawSipData;
    return SipFail;
  }
#endif

	INGwSpSipConnection *conn = 
		INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn;

  if(transptype == SIP_UDP)
  {  
	  INGwSpStackConfigMgr::getInstance()->sendToNetwork((void *)outmsg, buflen, 
			                                         destIpAddr, destPort);
  }
  else
  {
		bool lIsSasFailed = false;

    int lRetryCount = 0;

		while (! lIsSasFailed)
		{
      if(++lRetryCount >= 5 )
      {
				lIsSasFailed = true;
			  logger.logMsg(ERROR_FLAG, 0, 
											"Failed to send msg to [%s] after <%d> retries ", 
											destIpAddr, lRetryCount);
			  LogINGwTrace(false, 0, "OUT sip_sendToNetwork");
			  return SipSuccess;
      }

      // check if conn is available
      if(conn != NULL)
        destPort = conn->getActiveEPPort();

		  bool lIsConnPresent = INGwSpTcpConnMgr::instance().isConnectionPresent(destIpAddr, destPort);
      if(lIsConnPresent == false)
		  {
				// get the EP listener port and try to connect to it.
        if(conn != NULL)
				  destPort = conn->getGwPort();

				INGwSpSipCallController* callCtlr =
									INGwSpSipProvider::getInstance().getCallController();

        std::string destIpStr = destIpAddr;

        if(conn != NULL)
				  conn->setActiveEPPort(destPort);

			  retVal = INGwSpTcpConnMgr::instance().addRemoteConnection(destIpAddr, destPort);
			  if(retVal != -1)
			  {
					 //Connection is successfull
					 //Set active port in end point information
					unsigned short lClientport = destPort;
          if(conn != NULL)
				  	callCtlr->updateEndPoint(destIpStr, lClientport);
			  }
				else
				{
					// New conn creation has failed
					//if entry not found in call controller map than
					// SAS has failed so break from loop 

					INGwSipEPInfo lEPInfo;
					retVal = callCtlr->getEndPoint(destIpStr, lEPInfo);
					if(retVal == -1)
					{
						lIsSasFailed = true;
			      logger.logMsg(ERROR_FLAG, 0, 
												 " Failed to send msg to [%s] before Endpoint timeout", 
												 destIpAddr);
			      LogINGwTrace(false, 0, "OUT sip_sendToNetwork");
			      return SipSuccess;
					}

					//will try again to connect
          logger.logMsg(ALWAYS_FLAG, 0, "sip_sendToNetwork():: sleeping 1 sec...");
          sleep(1);
					continue;
				} // if connect failed

		  } // end of if lIsConnPresent == false

			if(-1 != INGwSpTcpConnMgr::instance().sendMsgToNetwork(destIpAddr, destPort,
																														 outmsg, buflen))
      {
				// send success
				break;
			}

		} // end of while ! lIsSasFailed
  }


  // Time stamp - Start
  struct timeval tim;
  char buf[26];
  gettimeofday( &tim, NULL);
  time_t tt;

  #ifdef LINUX
	  char *t = ctime_r(&tim.tv_sec, buf);
  #else
    char *t = ctime_r(&tim.tv_sec, buf, sizeof(buf));
  #endif

  int len = strlen(buf);
  buf[len-6] = '\0';
  char tempBuff[7];
  sprintf(tempBuff,"%d",tim.tv_usec);
  string timeStamp=string(buf)+':'+string(tempBuff);
  // Time stamp - End

  int lCurVal = 0;
  INGwIfrSmStatMgr::instance().increment(
							INGwSpSipProvider::miStatParamId_SipMsgSent, lCurVal, 1);

	if(sipMsgStream->isLoggable())
	{
    bool lbIsSuppressOptLog = false;
    bool lIsOptionMsg = false;

    if(getenv("SUPPRESS_OPTIONS_LOGGING"))
    {
      lbIsSuppressOptLog = true;
      if(conn != NULL)
      {
				if(conn->getLastMethod() == INGW_SIP_METHOD_TYPE_OPTIONS )
				{
					lIsOptionMsg = true;
				}
      }
    }

		if(lIsOptionMsg && lbIsSuppressOptLog)
		{
  		logger.logINGwMsg(false, TRACE_FLAG, 0, "sendToNetwork: SENT RAWMSG <%s:%d> "
			  	              ":\n%s\n", 
			  	              destIpAddr, destPort, "OPTIONS HEARTBEAT RESPONSE");


	  	sipMsgStream->log("%s:%d %d Sending RAWMSG <%s:%d> :\n%s\n", 
                        buf, tim.tv_usec, (int)pthread_self(),
		  		              destIpAddr, destPort, "OPTIONS HEARTBEAT RESPONSE");
		}
		else
		{
  		logger.logINGwMsg(false, TRACE_FLAG, 0, "sendToNetwork: SENT RAWMSG <%s:%d> "
			  	              ":\n%s\n", 
			  	              destIpAddr, destPort, outmsg);


	  	sipMsgStream->log("%s:%d %d Sending RAWMSG <%s:%d> :\n%s\n", 
                        buf, tim.tv_usec, (int)pthread_self(),
		  		              destIpAddr, destPort, outmsg);
		}
	}

	LogINGwTrace(false, 0, "OUT sip_sendToNetwork");
	return SipSuccess;
}

SIP_S8bit sip_willParseSdpLine
    (SIP_S8bit *in,
    SIP_S8bit **out)
{
  printf("\n****sip_willParseSdpLine ****\n");
  *out = SIP_NULL;
  return 1;
}

SipBool sip_acceptIncorrectSdpLine
    (SIP_S8bit *in)
{
  printf("\n****sip_acceptIncorrectSdpLine ****\n");
  return SipSuccess;
}

SIP_S8bit sip_willParseHeader
    (en_HeaderType type,
    SipUnknownHeader *hdr)
{
  return 1;
}

SipBool sip_acceptIncorrectHeader
    (en_HeaderType type,
    SipUnknownHeader *hdr)
{
  printf("\n****sip_acceptIncorrectHeader ****\n");
  return SipSuccess;
}

SipBool sip_decryptBuffer
    (SipMessage *s,
    SIP_S8bit *encinbuffer,
    SIP_U32bit clen,
    SIP_S8bit **encoutbuffer,
    SIP_U32bit *outlen)
{
  printf("\n****sip_decryptBuffer ****\n");
  return SipSuccess;
}


Sdf_ty_retVal sdf_cbk_uaInitRemoteRetransTables
    (Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaInitRemoteRetransTables");
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaInitRemoteRetransTables");
  return Sdf_co_success;
}

Sdf_ty_retVal sdf_cbk_uaFreeRemoteRetransTables
    (Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaFreeRemoteRetransTables");

  // TBD
  
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaFreeRemoteRetransTables");
  return Sdf_co_success;
} // end of sdf_cbk_uaFreeRemoteRetransTables

Sdf_ty_retVal sdf_cbk_uaGetRemoteRetransHashTable
    (Sdf_st_hash **ppRemoteRetransHash,
    Sdf_st_eventContext *pContext,
    Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaGetRemoteRetransHashTable");
  Sdf_ty_retVal retval = Sdf_co_fail;
  Sdf_st_error  sdferror;

  *ppRemoteRetransHash =
    INGwSpSipProvider::getInstance().getThreadSpecificSipData().getRemoteRetransHash();
  if(!(*ppRemoteRetransHash))
  {
    // Create a new hash table and set in the thread specific data.
    Sdf_st_hash* remotehash =
      (Sdf_st_hash*)sdf_memget(0, sizeof(Sdf_st_hash), 0);
    Sdf_ty_retVal status = 
      sdf_ivk_uaInitRemoteRetransTable(remotehash, &sdferror);

    if(status != Sdf_co_success)
    {
      INGwSpSipUtil::checkError(status, sdferror);
      logger.logINGwMsg(false, ERROR_FLAG, 0, "sdf_cbk_uaGetRemoteRetransHashTable: Error creating a new remote retrans hash table");
      *ppRemoteRetransHash = NULL;
    }
    else
    {
      INGwSpSipProvider::getInstance().getThreadSpecificSipData().
        setRemoteRetransHash(remotehash);
      *ppRemoteRetransHash = remotehash; 
      logger.logINGwMsg(false, TRACE_FLAG, 0, "sdf_cbk_uaGetRemoteRetransHashTable: Got NULL remote retrans hash, created a new one.  Starting the first purge timer");

      // Start a remote retrans timer
      if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().getStackTimer()
         .startRemoteRetransPurgeTimer(REMOTE_RETRANS_PURGE_INTERVAL))
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0, "sdf_cbk_uaGetRemoteRetransHashTable: Error starting remote retrans purge timer");
        retval = Sdf_co_fail;
      }
      else
        logger.logINGwMsg(false, TRACE_FLAG, 0, "sdf_cbk_uaGetRemoteRetransHashTable: Successfully started remote retrans purge timer");

      retval = Sdf_co_success;
    }
  }
  else
    retval = Sdf_co_success;

  LogINGwTrace(false, 0, "OUT sdf_cbk_uaGetRemoteRetransHashTable");
  return retval;
} // end of sdf_cbk_uaGetRemoteRetransHashTable


Sdf_ty_retVal sdf_cbk_uaGetIpFromTelUrl
   (Sdf_st_callObject *pCallobj,
    TelUrl *pTelUrl,
    Sdf_st_transportInfo *pDestInfo,
    Sdf_st_error *pErr)
{
  INGwSpSipConnection *conn = INGwSpSipUtil::getINGwSipConnFromHssCall(pCallobj);

  if((conn != NULL) && (pDestInfo != NULL))
  {
     SipError *err = (SipError *)&(pErr->errCode); 
     pDestInfo->dPort = conn->getGwPort();
     FREE(pDestInfo->pIp);
     FREE(pDestInfo->pScheme);

     pDestInfo->pScheme = (Sdf_ty_s8bit *)fast_memget(0, 4, err);
     strcpy(pDestInfo->pScheme, "udp");

     const char *ip = conn->getGwIPAddress();
     pDestInfo->pIp = (Sdf_ty_s8bit *)fast_memget(0, strlen(ip) + 1, err);
     strcpy(pDestInfo->pIp, ip);

     logger.logMsg(TRACE_FLAG, 0, "Destination provided.");
  }
  else
  {
     logger.logMsg(ERROR_FLAG, 0, "IP from TEL reqd.");
  }
  return Sdf_co_success;
}

/*
Sdf_ty_retVal sdf_cbk_uaStopTimer
  (Sdf_ty_pvoid pTimerHandle,
  Sdf_ty_pvoid *info,
  Sdf_st_error *pErr)
{
  printf("\n**** sdf_cbk_uaStopTimer ****\n");
  return SipSuccess;
}
*/

Sdf_ty_retVal sdf_cbk_uaTransactionCancel
  (Sdf_st_callObject **ppCallObj,
  Sdf_st_eventContext *pEventContext,
  Sdf_st_overlapTransInfo *pOverlapTransInfo,
  Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaTransactionCancel");
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaTransactionCancel");
  return Sdf_co_success;
}

/*---------------------------------------------------------------
 |
 | sdf_cbk_uaHandleSTTimeout
 |
 |
 |
 |----------------------------------------------------------------
*/ 
 
Sdf_ty_retVal bp_handleSessionTimeout(Sdf_st_callObject *, 
                                      Sdf_ty_handleEvent *,Sdf_st_error  *);

Sdf_ty_retVal sdf_cbk_uaHandleSTTimeout(Sdf_st_callObject *pCallObj,
                                        Sdf_ty_handleEvent *pRefresh,
                                     Sdf_ty_sessionRefreshRequest *pRefreshReq, 
                                        Sdf_st_error  *pErr)
{
   //Refresh request is used to sent either invite/update for refreshing.
   //Since we are never a refresher, the value in this field wont matter.

   *pRefreshReq = Sdf_en_refresherInvite;

   return bp_handleSessionTimeout(pCallObj, pRefresh, pErr);
}

Sdf_ty_retVal bp_handleSessionTimeout
  (Sdf_st_callObject *pCallObj,
  Sdf_ty_handleEvent *pRefresh,
  Sdf_st_error  *pErr) {

  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN bp_handleSessionTimeout");

  logger.logINGwMsg(false, WARNING_FLAG, 0,
    "Session Timer TIMEOUT CallId:<%s>", pCallObj->pCommonInfo->pCallid);

  INGwSpSipConnection* l_bpSipConnection = 
                   INGwSpSipUtil::getINGwSipConnFromHssCall(pCallObj);

  if(0 == l_bpSipConnection) {
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
      "bp_handleSessionTimeout: Null INGwSpSipConnection from INGwConnectionContext for CallId:<%s>", 
			pCallObj->pCommonInfo->pCallid);
    logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT bp_handleSessionTimeout");
    return Sdf_co_fail;
  }

  l_bpSipConnection->getRef();
  INGwIfrUtlRefCount_var sipConnVar(l_bpSipConnection);
  
  //get BYE handler - send Bye.

  INGwSpMsgByeHandler* l_byeHandler = dynamic_cast<INGwSpMsgByeHandler*> (
    l_bpSipConnection->getSipHandler(INGW_SIP_METHOD_TYPE_BYE));

  // need to unlock the call object
  l_bpSipConnection->unlockResource();

  l_byeHandler->disconnect(l_bpSipConnection, 0);

  // lock the call object
  l_bpSipConnection->lockResource();

  *pRefresh = Sdf_en_applicaton;

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT bp_handleSessionTimeout");

  return Sdf_co_success;
}

int INGwSpSipStackIntfLayer::addHBConnection(INGwSpSipConnection* aSipConnection)
{
  LogINGwTrace(false, 0, "IN addHBConnection");

#if 0 //not ot be done
  // Create a new HSS call object, and set up links between HSS call object and
  // connection.
  Sdf_st_callObject* hssCallObj = Sdf_co_null;
  Sdf_st_error sdferror;
  Sdf_ty_retVal status = sdf_ivk_uaInitCall(&hssCallObj, &sdferror);
  sdf_ivk_uaSetInitDataInCallObject(hssCallObj, pGlbProfile, &sdferror);

  // Copy the local call id of the connection as the callid in the connection
  // context.
  INGwConnectionContext *bpconncontext = new INGwConnectionContext;
  const std::string &callid = aSipConnection->getLocalCallId();
  strcpy(bpconncontext->mCallId, callid.c_str());

  // Add to and fro references between call object and connection.
  hssCallObj->pAppData->pData = (void *)bpconncontext;
  bpconncontext->mSipConnection   = aSipConnection;
  bpconncontext->mbUseGivenCallid = true;
  logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwSpSipStackIntfLayer::addHBConnection: Setting the hss callobj <%p> into the hb connection <%p>", hssCallObj, aSipConnection);
  aSipConnection->setHssCallObject(hssCallObj);
  aSipConnection->getRef();             // hsscallobj's ref of sipconn
  HSS_LOCKEDINCREF(hssCallObj->dRefCount);// sipconn's ref of hsscallobj

  // Add the call object into the call table in the thread specific data.  This
  // also warrants an increase in the reference count of the HSS call leg.
  if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().
    getCallTable().put(callid.c_str(), callid.size(), hssCallObj))
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "Error adding call object in CCM call table");
    sdferror.errCode = Sdf_en_callObjectAccessError;
    strcpy(sdferror.ErrMsg, "Error adding call object in CCM call table");
    LogINGwTrace(false, 0, "OUT addHBConnection");
    return -1;
  }

  HSS_LOCKEDINCREF(hssCallObj->dRefCount); // For placing call leg in leg map.

  // We are done using HSS call object
  sdf_ivk_uaFreeCallObject(&hssCallObj);
#endif
  LogINGwTrace(false, 0, "OUT addHBConnection");
  return 0;
} // end of addHBConnection

void INGwSpSipStackIntfLayer::handleConfigUpdate(INGwSipConfigContext* aContext)
{
  LogINGwTrace(false, 0, "IN handleConfigUpdate");
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
										"Received config update with oid <%s>, value <%s>", 
										aContext->mOid.c_str(), aContext->mVal.c_str());

  if(!strcmp(ingwSIP_STACK_DEBUG_LEVEL, aContext->mOid.c_str()))
  {
    bool debug = false;
    if(atoi(aContext->mVal.c_str()))
      debug = true;

    INGwSpSipProvider::getInstance().getThreadSpecificSipData().
															getConfigRepository().setStackDebugLevel(debug);
    logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
											"INGwSpSipStackIntfLayer::handleConfigUpdate: "
											"Changed ingwSIP_STACK_DEBUG_LEVEL to <%d>", 
											INGwSpSipProvider::getInstance().
											getThreadSpecificSipData().getConfigRepository().
											getStackDebugLevel());
  }
  else
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
											"INGwSpSipStackIntfLayer::handleConfigUpdate: "
											"Received unrecognized oid <%s> in config update", 
											aContext->mOid.c_str());
  }
  LogINGwTrace(false, 0, "OUT handleConfigUpdate");
}

Sdf_ty_u8bit* sdf_cbk_uaHAmemget
   (Sdf_ty_criticalDataUpdateMode  dUpdateType      ,
    Sdf_ty_uaObjectType            dObjectType      ,
    Sdf_ty_pvoid                   pObjId           ,
    Sdf_ty_pvoid                   pObj             ,
    Sdf_ty_u16bit                  dNumBytesRequired,
    Sdf_ty_u16bit                  *pOffset         )
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaHAmemget");
  logger.logINGwMsg(false, VERBOSE_FLAG, 0, "sdf_cbk_uaHAmemget: number of bytes required <%d>", (int)dNumBytesRequired);

  // Return the buffer from thread specific data.
  unsigned char* buf    =  0;
  int            offset = -1;
  int            maxlen = -1;

  INGwSpSipProvider::getInstance().getThreadSpecificSipData().
    getStackSerializationContext(buf, offset, maxlen);
  *pOffset = offset;
  LogINGwTrace(false, 0, "OUT sdf_cbk_uaHAmemget");
  return buf;
} // end of sdf_cbk_uaHAmemget

void sdf_cbk_uaFreeTimerHandle (Sdf_ty_pvoid pTimerHandle)
{
  MARK_BLOCK("sdf_cbk_uaFreeTimerHandle", TRACE_FLAG);
}

Sdf_ty_retVal sdf_cbk_uaUnexpectedRequestReceived
  (Sdf_st_callObject  **ppCallObj, Sdf_st_eventContext *pEventContext,
   Sdf_st_overlapTransInfo *pOverlapTransInfo, Sdf_st_error *pErr)
{
  LogINGwTrace(false, 0, "IN sdf_cbk_uaUnexpectedRequestReceived");

  SipMessage* sipmsg = (*ppCallObj)->pUasTransaction->pSipMsg;
	if(pOverlapTransInfo){

		LogINGwTrace(false, 0, "IN sdf_cbk_uaUnexpectedRequestReceived. Getting message from Overlap Transaction");
		sipmsg = pOverlapTransInfo->pSipMsg;
	}

  INGwSipMethodType lMethod = INGwSpSipUtil::getMethodFromSipMessage(sipmsg);
  bool lbRejectRequest = true;

  if (lMethod == INGW_SIP_METHOD_TYPE_NOTIFY)
  {
    string lstrValue;
    lbRejectRequest = INGwIfrPrParamRepository::getInstance().
																			 getValue (SIP_OOD_FAILURE, lstrValue);
    if (!lstrValue.empty())
      lbRejectRequest = atoi (lstrValue.c_str());
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                        "sdf_cbk_uaUnexpectedRequestReceived: "
												"Received NOTIFY");
  }

  if (!lbRejectRequest)
  {

    char            *pMethod = NULL;
    Sdf_st_error     sdferror;
    Sdf_ty_retVal status = sdf_ivk_uaGetMethodFromSipMessage(sipmsg, 
																														 &pMethod, 
																														 &sdferror);

    // Form a response with the given response code.
    status = sdf_ivk_uaFormResponse(200, pMethod, *ppCallObj, 
																		pOverlapTransInfo , Sdf_co_false, 
																		&sdferror);

    if(status != Sdf_co_success)
    {
      logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
												"sdf_cbk_uaUnexpectedRequestReceived: "
												"Error forming response");
      return status;
    }
    else
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
												"sdf_cbk_uaUnexpectedRequestReceived: "
												"Successfully forming response");
  
    status = INGwSpSipUtil::sendCallToPeer
      (*ppCallObj, sipmsg, lMethod, pErr);

    if(status != Sdf_co_success)
    {
      logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
                        "sdf_cbk_uaUnexpectedRequestReceived: "
												"Error sending request response to peer");
      INGwSpSipUtil::checkError(status, *pErr);
      return status;
    }
    else
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
                        "sdf_cbk_uaUnexpectedRequestReceived: "
												"Successfully sent request response to peer ");
  
    return Sdf_co_success;
  }

  // Reject the request
  Sdf_ty_retVal status = sdf_ivk_uaRejectRequest
    (*ppCallObj, pGlbProfile, 481, &pOverlapTransInfo, pErr);

  if(status != Sdf_co_success)
  {
    logger.logINGwMsg(false, WARNING_FLAG, imERR_NONE, 
											"sdf_cbk_uaUnexpectedRequestReceived: "
											"Error rejecting request");
		INGwSpSipUtil::checkError(status, *pErr);
    return status;
  }
  else
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"sdf_cbk_uaUnexpectedRequestReceived: "
											"Successfully rejected request");

	// Need to point to the newly created SipMessage
	if (lMethod == INGW_SIP_METHOD_TYPE_INVITE)
	{
		sipmsg = (*ppCallObj)->pUasTransaction->pSipMsg;

	} else {

		if(pOverlapTransInfo){
			LogINGwTrace(false, 0, "IN sdf_cbk_uaUnexpectedRequestReceived. Getting new message from Overlap Transaction");
			sipmsg = pOverlapTransInfo->pSipMsg;
		}
	}

	status = INGwSpSipUtil::sendCallToPeer
		(*ppCallObj, sipmsg, lMethod, pErr);

	if(status != Sdf_co_success)
	{
    logger.logINGwMsg(false, WARNING_FLAG, 0, "sdf_cbk_uaUnexpectedRequestReceived: Error sending request rejection to peer");
    INGwSpSipUtil::checkError(status, *pErr);
    return status;
  }
  else
    logger.logINGwMsg(false, TRACE_FLAG, 0, "sdf_cbk_uaUnexpectedRequestReceived: Successfully sent request rejection to peer");

  return Sdf_co_success;
} // end of sdf_cbk_uaUnexpectedRequestReceived

Sdf_ty_retVal sdf_cbk_uaByeRejected(Sdf_st_callObject **ppCallObj, 
                                    Sdf_st_eventContext *pEventContext, 
                                    Sdf_st_error *pErr)
{
   LogINGwTrace(false, 0, "IN sdf_cbk_uaByeRejected");
   Sdf_ty_retVal ret = responseCallback(ppCallObj, pEventContext, pErr, false);
   LogINGwTrace(false, 0, "OUT sdf_cbk_uaByeRejected");
   return ret;
}

Sdf_ty_retVal sdf_cbk_uaResendMessage(Sdf_st_callObject *pCallObj, 
                                      Sdf_st_transaction *pUacUasTxn, 
                                      Sdf_st_overlapTransInfo *pOverlapTxn, 
                                      Sdf_ty_appActionExpected dActionExpected, 
                                      Sdf_st_error *pError)
{
   logger.logMsg(ERROR_FLAG, 0, "UnImplemented API called.");
   return Sdf_co_success;
}

Sdf_ty_retVal handleNotifyRequest(Sdf_st_callObject **ppCallObj,
																	Sdf_st_eventContext *pEventContext,
																	Sdf_st_overlapTransInfo *pOverlapTxn,
																	Sdf_st_error *pErr)
{
   LogINGwTrace(false, 0, "IN handleNotifyRequest");

   int lCurVal = 0;
   INGwIfrSmStatMgr::instance().increment(
							INGwSpSipProvider::miStatParamId_NumNotifyRecvd, lCurVal, 1);

   int lSeqNum = -1;
   if((*ppCallObj)->pCommonInfo->pCallid == NULL)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "Could not find call id from call object.");
      pErr->errCode = Sdf_en_callObjectAccessError;
      strcpy(pErr->ErrMsg, "Call id is NULL");
      LogINGwTrace(false, 0, "OUT handleNotifyRequest");
      return Sdf_co_fail;
   }

   // check if Call Gapping needs to drop this call
   INGwSpSipProvider& l_pdr = INGwSpSipProvider::getInstance();
   if(!l_pdr.getNewCallsStatus()) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "getNewCallsStatus returned false, dropping call:<%s>",
                      (*ppCallObj)->pCommonInfo->pCallid);

      Sdf_st_error  pErr;

      int lCurVal = 0;
      INGwIfrSmStatMgr::instance().increment(
							INGwSpSipProvider::miStatParamId_NumNotifyRecvd, lCurVal, 1);

      sdf_ivk_uaFormResponse(500, "NOTIFY", *ppCallObj, 
                             pOverlapTxn, Sdf_co_false, &pErr);

      INGwSpSipUtil::sendCallToPeer(*ppCallObj, pOverlapTxn->pSipMsg,
                                    INGW_SIP_METHOD_TYPE_NOTIFY, &pErr);

      LogINGwTrace(false, 0, "OUT handleNotifyRequest");
      return Sdf_co_success;
   }

#if 1
	// Fetch Sequence ID coming in Notify
		SipList &locUnknownHdrList = 
				(*ppCallObj)->pUasTransaction->pSipMsg->pGeneralHdr->slUnknownHdr;

		for(SipListElement *curr = locUnknownHdrList.head; curr != SIP_NULL;
			 curr = curr->next)
		{
			SipUnknownHeader *currHeader = (SipUnknownHeader *)(curr->pData);
			string hdrName = currHeader->pName;

			if(hdrName.find("TC-Seq"))
			{
				logger.logMsg(VERBOSE_FLAG, 0, "TC-Seq: body:%s", currHeader->pBody);
			}

			logger.logMsg(VERBOSE_FLAG, 0, "Unknown Header:name:%s body:%s", 
				currHeader->pName, currHeader->pBody);
      if(currHeader->pName[0] == 'T') {
        lSeqNum = atoi(currHeader->pBody);
			  logger.logMsg(VERBOSE_FLAG, 0, "Unknown Header:name:%s body:%s", 
			  	currHeader->pName, currHeader->pBody);
      }
		}
#endif

   LogINGwTrace(false, 0, "getNewCallsStatus TRUE ");

   // Get the call-id from the call object.
   const char *callidstr = (*ppCallObj)->pCommonInfo->pCallid;

   // Create a call and a connection object, and set up the links between the
   // call, connection, and the HSS call leg.

   INGwSpSipCall* tmpSipCall = 
         dynamic_cast<INGwSpSipCall*>(INGwSpSipProvider::getInstance().getNewCall(callidstr,
                                                                      true));
   INGwIfrUtlRefCount_var call_var(tmpSipCall);

   INGwSpSipConnection *tmpSipConnection = dynamic_cast<INGwSpSipConnection*>(
                       INGwSpSipProvider::getInstance().getNewConnection(*tmpSipCall));
   tmpSipConnection->setLocalCallId(callidstr);
   INGwIfrUtlRefCount_var con_var(tmpSipConnection);
   
   int lConnId = tmpSipCall->addConnection((INGwSpSipConnection *)tmpSipConnection);

   //Now Call has Conn Ref and Conn has Call Ref.

   tmpSipConnection->setHssCallObject(*ppCallObj);
   //HSS_LOCKEDINCREF((*ppCallObj)->dRefCount); 
   //Now Conn had HssCallLeg Ref.

#if 0
//We won't need any app data and thread specific data or entry in call map
//as all the proceessing will be finished in this thread

   // Attach the callid and the INGwSpSipConnection as application data in the hss
   // call leg.

   Sdf_st_appData *pAppSpecificData = Sdf_co_null;
   sdf_ivk_uaGetAppDataFromCallObject((*ppCallObj), &pAppSpecificData, pErr);
 
   INGwConnectionContext *tmpConnectionContext = new INGwConnectionContext;
   tmpConnectionContext->mSipConnection = tmpSipConnection;
   strcpy(tmpConnectionContext->mCallId, (*ppCallObj)->pCommonInfo->pCallid);
   pAppSpecificData->pData = (void *)tmpConnectionContext;
   tmpSipConnection->getRef();  
   //Now HSS call leg has a reference of connection

   sdf_ivk_uaFreeAppData(pAppSpecificData);
   logger.logINGwMsg(false, TRACE_FLAG, 0, 
                   "Successfully added app data into call object");
  
   INGwSpSipProvider& l_bpSipProvider = INGwSpSipProvider::getInstance();


   // Add the call object into the call table in the thread specific data.  This
   // also warrants an increase in the reference count of the HSS call leg.

   if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().
                   getCallTable().put(callidstr, strlen(callidstr), *ppCallObj))
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "Error adding call object in CCM call table");
      INGwSpSipUtil::releaseConnection(tmpSipConnection);
      tmpSipCall->removeConnection(tmpSipConnection->getSelfConnectionId());

      pErr->errCode = Sdf_en_callObjectAccessError;
      strcpy(pErr->ErrMsg, "Error adding call object in CCM call table");
      LogINGwTrace(false, 0, "OUT sdf_cbk_uaNewCallReceived");
      return Sdf_co_fail;
   }

   HSS_LOCKEDINCREF((*ppCallObj)->dRefCount); 
   //For placing call leg in leg map.

   // XXX: Here call method on the provider to add the call object into the CCM
   // call table.


   INGwSpSipCallController* l_callCntrl = l_bpSipProvider.getCallController(); 
   if(0 == l_callCntrl) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "Error adding call object in CCM call table");
      LogINGwTrace(false, 0, "OUT sdf_cbk_uaNewCallReceived");
      return Sdf_co_fail;
   }

   std::string l_internalCallId = tmpSipCall->getCallId();

   int retVal = l_callCntrl->addCall(l_internalCallId, tmpSipCall);

   if(-1 == retVal)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "sdf_cbk_uaNewCallReceived: error in adding call object "
                      "in CallCntrl");

      Sdf_st_overlapTransInfo* dummy = NULL;
      Sdf_ty_retVal status = sdf_ivk_uaRejectRequest(*ppCallObj, pGlbProfile, 
                                                     500, &dummy, pErr);
      if(status != Sdf_co_success)
      {
         logger.logINGwMsg(false, WARNING_FLAG, 0, 
                         "sdf_cbk_uaNewCallReceived: Error rejecting request "
                         "with 5xx response");
         INGwSpSipUtil::checkError(status, *pErr);
      }
      else
      {
         logger.logINGwMsg(false, TRACE_FLAG, 0, 
                         "sdf_cbk_uaNewCallReceived: Successfully formed 5xx "
                         "response to reject the request");

         status = INGwSpSipUtil::sendCallToPeer(*ppCallObj, 
                                         (*ppCallObj)->pUasTransaction->pSipMsg,
                                            INGW_SIP_METHOD_TYPE_INVITE, pErr);
         if(status != Sdf_co_success)
         {
            logger.logINGwMsg(false, WARNING_FLAG, 0, 
                            "sdf_cbk_uaNewCallReceived: Error sending call to "
                            "peer");
            INGwSpSipUtil::checkError(status, *pErr);
         }
         else
         {
            logger.logINGwMsg(false, TRACE_FLAG, 0, 
                            "sdf_cbk_uaNewCallReceived: Successfully sent 5xx "
                            "rejection to peer");
         }
      }
    
      // Release the connection.  This should in turn release the INGwSpSipCall and
      // hssCallObj
      INGwSpSipUtil::releaseConnection(tmpSipConnection);
      tmpSipCall->removeConnection(tmpSipConnection->getSelfConnectionId());

      LogINGwTrace(false, 0, "OUT sdf_cbk_uaNewCallReceived");
      return Sdf_co_fail;
   }

   INGwConnectionContext *conncontext = 
                         (INGwConnectionContext *)((*ppCallObj)->pAppData->pData);
   INGwSpSipConnection *bpSipConnection = NULL;

   if(conncontext)
   {
      bpSipConnection = conncontext->mSipConnection;
   }
  
   if(bpSipConnection == NULL)
   {
      pErr->errCode = Sdf_en_callObjectAccessError;
      strcpy(pErr->ErrMsg, 
             "Error retrieving Connection from stack call leg");
      LogINGwTrace(false, 0, "OUT sdf_cbk_uaNewCallReceived");
      return Sdf_co_fail;
   }

   bpSipConnection->getRef();
   INGwIfrUtlRefCount_var sipConnVar(bpSipConnection);

#endif

   // Set sender IP and Port
   INGwSpThreadSpecificSipData &thrData = 
                           INGwSpSipProvider::getInstance().getThreadSpecificSipData();
   INGwSipTranspInfo* lTranspInfo = thrData.msgTransport;

   tmpSipConnection->setGwIPAddress(lTranspInfo->pTranspAddr, 
                                    strlen(lTranspInfo->pTranspAddr));
   tmpSipConnection->setGwPort(lTranspInfo->mPort);
   tmpSipConnection->setActiveEPPort(lTranspInfo->mPort);

   logger.logINGwMsg(false, TRACE_FLAG, 0, 
                   "Calling +rem+ request callback on the INGwSpSipConnection seqNum<%d>\n",
                    lSeqNum);
   tmpSipConnection->setNotifyTcSeqNum(lSeqNum);

   int retval = tmpSipConnection->stackCallbackRequest(
                           INGW_SIP_METHOD_TYPE_NOTIFY,
                           ppCallObj,
                           pEventContext,
                           pErr, pOverlapTxn);
 
   tmpSipCall->removeConnection(lConnId);

   LogINGwTrace(false, 0, "OUT handleNotifyRequest");

   if(retval == -1)
     return Sdf_co_fail;
   else
     return Sdf_co_success;
}

int INGwSpSipStackIntfLayer::initInboundMsgConn(INGwSpSipConnection* aSipConnection)
{
  LogINGwTrace(false, 0, "IN initInboundMsgConn");

  // Create a new HSS call object, and set up links between HSS call object and
  // connection.
  Sdf_st_callObject* hssCallObj = Sdf_co_null;
  Sdf_st_error sdferror;
  Sdf_ty_retVal status = sdf_ivk_uaInitCall(&hssCallObj, &sdferror);

  if(status != Sdf_co_success)
  {
    INGwSpSipUtil::checkError(status, sdferror);
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "initInboundMsgConn: Error init HSS call object.");
    return -1;
  }
  status = sdf_ivk_uaSetInitDataInCallObject(hssCallObj, pGlbProfile, &sdferror);

  if(status != Sdf_co_success)
  {
    sdf_ivk_uaFreeCallObject(&hssCallObj);
    INGwSpSipUtil::checkError(status, sdferror);
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "initInboundMsgConn: Error setting profile in HSS call object.");
    return -1;
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0, 
                    "INGwSpSipStackIntfLayer::initInboundMsgConn : "
                    " Setting the hss callobj <%p> into the Notify connection <%p>", 
                    hssCallObj, aSipConnection);

   Sdf_st_appData *pAppSpecificData = Sdf_co_null;
   sdf_ivk_uaGetAppDataFromCallObject(hssCallObj, &pAppSpecificData, &sdferror);
 
   INGwConnectionContext *tmpConnectionContext = new INGwConnectionContext;
   tmpConnectionContext->mSipConnection = aSipConnection;

   INGwSpSipProvider& l_bpSipProvider = INGwSpSipProvider::getInstance();

   std::string lCallIdStr = aSipConnection->getLocalCallId();

   strcpy(tmpConnectionContext->mCallId, lCallIdStr.c_str());
	 tmpConnectionContext->mbUseGivenCallid = true;

   pAppSpecificData->pData = (void *)tmpConnectionContext;
   aSipConnection->getRef();  
   //Now HSS call leg has a reference of connection

   sdf_ivk_uaFreeAppData(pAppSpecificData);

   logger.logINGwMsg(false, TRACE_FLAG, 0, 
                   "Successfully added app data into call object");
  
   // Add the call object into the call table in the thread specific data.  This
   // also warrants an increase in the reference count of the HSS call leg.

   if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().
                   getCallTable().put(lCallIdStr.c_str(), lCallIdStr.length(), hssCallObj))
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "Error adding call object in Thread Specific call table");
      LogINGwTrace(false, 0, "OUT initInboundMsgConn");
      return -1;
   }
  HSS_LOCKEDINCREF(hssCallObj->dRefCount);// call table has ref of hsscallobj

  aSipConnection->setHssCallObject(hssCallObj);
  HSS_LOCKEDINCREF(hssCallObj->dRefCount);// sipconn's ref of hsscallobj

  // We are done using HSS call object
  sdf_ivk_uaFreeCallObject(&hssCallObj);
  LogINGwTrace(false, 0, "OUT initInboundMsgConn");
  return 0;
} //


Sdf_ty_retVal handleOptionsRequest(Sdf_st_callObject **ppCallObj,
																	 Sdf_st_eventContext *pEventContext,
																	 Sdf_st_overlapTransInfo *pOverlapTxn,
																	 Sdf_st_error *pErr)
{
   LogINGwTrace(false, 0, "IN handleOptionsRequest");



   if((*ppCallObj)->pCommonInfo->pCallid == NULL)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "Could not find call id from call object.");
      pErr->errCode = Sdf_en_callObjectAccessError;
      strcpy(pErr->ErrMsg, "Call id is NULL");
      LogINGwTrace(false, 0, "OUT handleOptionsRequest");
      return Sdf_co_fail;
   }

   INGwSpSipProvider& l_pdr = INGwSpSipProvider::getInstance();

   // Get the call-id from the call object.
   const char *callidstr = (*ppCallObj)->pCommonInfo->pCallid;

   // Create a call and a connection object, and set up the links between the
   // call, connection, and the HSS call leg.

   INGwSpSipCall* tmpSipCall = 
         dynamic_cast<INGwSpSipCall*>(INGwSpSipProvider::getInstance().getNewCall(callidstr,
                                                                      true));
   INGwIfrUtlRefCount_var call_var(tmpSipCall);

   INGwSpSipConnection *tmpSipConnection = dynamic_cast<INGwSpSipConnection*>(
                       INGwSpSipProvider::getInstance().getNewConnection(*tmpSipCall));
   tmpSipConnection->setLocalCallId(callidstr);
   INGwIfrUtlRefCount_var con_var(tmpSipConnection);

   int lConnId = tmpSipCall->addConnection((INGwSpSipConnection *)tmpSipConnection);

   //Now Call has Conn Ref and Conn has Call Ref.

   tmpSipConnection->setHssCallObject(*ppCallObj);
   // HSS_LOCKEDINCREF((*ppCallObj)->dRefCount); 
   //Now Conn had HssCallLeg Ref.

   // Set sender IP and Port
   INGwSpThreadSpecificSipData &thrData = 
                           INGwSpSipProvider::getInstance().getThreadSpecificSipData();
   INGwSipTranspInfo* lTranspInfo = thrData.msgTransport;

   tmpSipConnection->setGwIPAddress(lTranspInfo->pTranspAddr, 
                                    strlen(lTranspInfo->pTranspAddr));
   tmpSipConnection->setGwPort(lTranspInfo->mPort);
   tmpSipConnection->setActiveEPPort(lTranspInfo->mPort);

   logger.logINGwMsg(false, TRACE_FLAG, 0, 
                   "Calling request callback on the INGwSpSipConnection");

   int retval = tmpSipConnection->stackCallbackRequest(
                           INGW_SIP_METHOD_TYPE_OPTIONS,
                           ppCallObj,
                           pEventContext,
                           pErr, pOverlapTxn);

   tmpSipCall->removeConnection(lConnId);

   LogINGwTrace(false, 0, "OUT handleOptionsRequest");

   if(retval == -1)
     return Sdf_co_fail;
   else
     return Sdf_co_success;
}

int
INGwSpSipStackIntfLayer::sendSipMsgToNetwork(INGwIfrMgrWorkUnit* apWork) {
  int retVal = G_SUCCESS;
  LogINGwTrace(false, 0, "In  sendSipMsgToNetwork");
//
		
  RawSipData* lpRawSipData = static_cast<RawSipData*>(apWork->mpContextData);
	INGwSpSipConnection *conn =  lpRawSipData->mSipConn;
  char* destIpAddr = lpRawSipData->IpAddr;

  int destPort = lpRawSipData->mPort; 
  if(lpRawSipData->mTransptype == SIP_UDP)
  {  
	  INGwSpStackConfigMgr::getInstance()->
      sendToNetwork((void *)lpRawSipData->mBuf, 
                     lpRawSipData->mBufLen, 
			               lpRawSipData->IpAddr, 
                     lpRawSipData->mPort);
  }
  else
  {
		bool lIsSasFailed = false;

    int lRetryCount = 0;

		while (! lIsSasFailed)
		{
      if(++lRetryCount >= 5 )
      {
				lIsSasFailed = true;
			  logger.logMsg(ERROR_FLAG, 0, 
											"Failed to send msg to [%s] after <%d> retries ", 
											lpRawSipData->IpAddr, lRetryCount);
			  LogINGwTrace(false, 0, "OUT sip_sendToNetwork");
			  return SipSuccess;
      }

      // check if conn is available
      if(conn != NULL)
        destPort = conn->getActiveEPPort();

		  bool lIsConnPresent = INGwSpTcpConnMgr::instance().
            isConnectionPresent(lpRawSipData->IpAddr, lpRawSipData->mPort);

      if(lIsConnPresent == false)
		  {
				// get the EP listener port and try to connect to it.
        if(conn != NULL)
				  destPort = conn->getGwPort();

				INGwSpSipCallController* callCtlr =
									INGwSpSipProvider::getInstance().getCallController();

        std::string destIpStr = lpRawSipData->IpAddr;

        if(conn != NULL)
				  conn->setActiveEPPort(destPort);

			  retVal = INGwSpTcpConnMgr::instance().addRemoteConnection(destIpAddr, destPort);
			  if(retVal != -1)
			  {
					 //Connection is successfull
					 //Set active port in end point information
					unsigned short lClientport = destPort;
          if(conn != NULL)
				  	callCtlr->updateEndPoint(destIpStr, lClientport);
			  }
				else
				{
					//New conn creation has failed
					//if entry not found in call controller map than
					//SAS has failed so break from loop 

					INGwSipEPInfo lEPInfo;
					retVal = callCtlr->getEndPoint(destIpStr, lEPInfo);

					if(retVal == -1)
					{
						lIsSasFailed = true;
			      logger.logMsg(ERROR_FLAG, 0, 
												 " Failed to send msg to [%s] before Endpoint timeout", 
												 destIpAddr);

			      LogINGwTrace(false, 0, "OUT sip_sendToNetwork");
			      return SipSuccess;
					}

					//will try again to connect
          logger.logMsg(ALWAYS_FLAG, 0, "sip_sendToNetwork():: sleeping 1 sec...");
          sleep(1);
					continue;
				} // if connect failed

		  } // end of if lIsConnPresent == false

			if(-1 != INGwSpTcpConnMgr::instance().sendMsgToNetwork(destIpAddr, destPort,
																														 lpRawSipData->mBuf, 
                                                             lpRawSipData->mBufLen))
      {
				// send success
				break;
			}

		} // end of while ! lIsSasFailed
  }


  // Time stamp - Start
  struct timeval tim;
  char buf[26];
  gettimeofday( &tim, NULL);
  time_t tt;

  #ifdef LINUX
	  char *t = ctime_r(&tim.tv_sec, buf);
  #else
    char *t = ctime_r(&tim.tv_sec, buf, sizeof(buf));
  #endif

  int len = strlen(buf);
  buf[len-6] = '\0';
  char tempBuff[7];
  sprintf(tempBuff,"%d",tim.tv_usec);
  string timeStamp=string(buf)+':'+string(tempBuff);
  // Time stamp - End

  int lCurVal = 0;
  INGwIfrSmStatMgr::instance().increment(
							INGwSpSipProvider::miStatParamId_SipMsgSent, lCurVal, 1);

	if(sipMsgStream->isLoggable())
	{
    bool lbIsSuppressOptLog = false;
    bool lIsOptionMsg = false;

    if(getenv("SUPPRESS_OPTIONS_LOGGING"))
    {
      lbIsSuppressOptLog = true;
      if(conn != NULL)
      {
				if(conn->getLastMethod() == INGW_SIP_METHOD_TYPE_OPTIONS )
				{
					lIsOptionMsg = true;
				}
      }
    }

		if(lIsOptionMsg && lbIsSuppressOptLog)
		{
  		logger.logINGwMsg(false, TRACE_FLAG, 0, "sendToNetwork: SENT RAWMSG <%s:%d> "
			  	              ":\n%s\n", 
			  	              destIpAddr, destPort, "OPTIONS HEARTBEAT RESPONSE");


	  	sipMsgStream->log("%s:%d %d Sending RAWMSG <%s:%d> :\n%s\n", 
                        buf, tim.tv_usec, (int)pthread_self(),
		  		              destIpAddr, destPort, "OPTIONS HEARTBEAT RESPONSE");
		}
		else
		{
  		logger.logINGwMsg(false, TRACE_FLAG, 0, "sendToNetwork: SENT RAWMSG <%s:%d> "
			  	              ":\n%s\n", 
			  	              destIpAddr, destPort, lpRawSipData->mBuf);


	  	sipMsgStream->log("%s:%d %d Sending RAWMSG <%s:%d> :\n%s\n", 
                        buf, tim.tv_usec, (int)pthread_self(),
		  		              destIpAddr, destPort, lpRawSipData->mBuf);
		}
	}

//
  conn = NULL;
  LogINGwTrace(false, 0, "OUT sendSipMsgToNetwork");

    delete [] lpRawSipData->IpAddr;
    delete [] lpRawSipData->mBuf;
    delete lpRawSipData;
  return retVal;
}

int
INGwSpSipStackIntfLayer::initRetransCntArray()
{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In initRetransCntArray");

  const long* lplThreadId   = INGwIfrMgrThreadMgr::getInstance().getWorkerThreadId();

  long  llThreadCount = INGwIfrMgrThreadMgr::getInstance().getThreadCount();

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"threadCnt <%d>", llThreadCount); 

  if (NULL == lplThreadId){
    return -1;
  }

  gllowThreadId = lplThreadId[0];
  glHiThreadId  = lplThreadId[0];

  for(int i=0; i< llThreadCount; ++i) 
  {
    if(gllowThreadId > lplThreadId[i])
    {
      gllowThreadId = lplThreadId[i];
    }

    if(glHiThreadId < lplThreadId[i])
    {
      glHiThreadId = lplThreadId[i];
    }    

    logger.logINGwMsg(false,ALWAYS_FLAG,0,"rem thread id <%d>",lplThreadId[i]); 
  }

  mllowThreadId = gllowThreadId;
  mlHiThreadId  = glHiThreadId;

  mplSipRetransCount = new (nothrow) 
                      unsigned long long[glHiThreadId - gllowThreadId + 1];

  memset(mplSipRetransCount,0,
            (glHiThreadId - gllowThreadId + 1)*sizeof(unsigned long long));

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out initRetransCntArray");

  return 0;
}
