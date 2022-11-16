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
//     File:     INGwSpMsgInfoHandler.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   08/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipMsgHandler");

#include <INGwSipMsgHandler/INGwSpMsgInfoHandler.h>
#include <INGwSipProvider/INGwSpDataFactory.h>

#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpData.h>
#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipProvider/INGwSpSipProvider.h>

#include <INGwInfraMsrMgr/MsrMgr.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>

using namespace std;

#define INGW_MSG_BODYTYPE_TCAP  "application/tcap"

INGwSpMsgInfoHandler::INGwSpMsgInfoHandler()
{
  reset();
}

INGwSpMsgInfoHandler::~INGwSpMsgInfoHandler()
{
}

void INGwSpMsgInfoHandler::reset()
{
  mLastop = LASTOP_UNKNOWN;
  muiIvrTimeout = 0;
}

Sdf_ty_retVal INGwSpMsgInfoHandler::stackCallbackRequest(
                    INGwSpSipConnection         *aSipConnection   ,
                    INGwSipMethodType             aMethodType      ,
                    Sdf_st_callObject      **ppCallObj        ,
                    Sdf_st_eventContext     *pEventContext    ,
                    Sdf_st_error            *pErr             ,
                    Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
  MARK_BLOCK("INGwSpMsgInfoHandler::stackCallbackRequest", TRACE_FLAG);
  INGwSipConnMinorState  connMinorState = aSipConnection->getMinorState();
  int                  connMajorState = (int)aSipConnection->getMajorState();
  Sdf_st_error         sdferror;

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgInfoHandler::stackCallbackRequest: "
										"connminorstate <%d>, connmajorstate <%d>", 
										connMinorState, connMajorState);

  aSipConnection->setLastMethod(INGW_SIP_METHOD_TYPE_INFO);

  int lCurVal = 0;
  INGwIfrSmStatMgr::instance().increment(
                      INGwSpSipProvider::miStatParamId_NumInfoRecvd,
                      lCurVal, 1);

  // If the connection is not in connected state, we cannot process this info.
  if(connMajorState != CONNECTION_CONNECTED)
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgInfoHandler::stackCallbackRequest: "
											"Info request received in wrong connection state");
    return Sdf_co_success;
  }

  // Update connection minor state to message received
  aSipConnection->setMinorState(CONN_MINSTATE_MID_2_MSGRX);

////////////////////////////////////////////////////////////////////////////////
#ifdef CLEANUP_SEQUENCE
#undef CLEANUP_SEQUENCE
#endif
#define CLEANUP_SEQUENCE(respcode) \
    sdf_ivk_uaUnlockCallObject(*ppCallObj, &sdferror);                       \
    terminateTransaction(aSipConnection, respcode, TERMINATE_CANCEL);        \
    sdf_ivk_uaLockCallObject(*ppCallObj, &sdferror);                         \
    aSipConnection->setLastMethod(INGW_METHOD_TYPE_NONE);                      \
    aSipConnection->setMinorState(CONN_MINSTATE_IDLE);                       \
    mLastop = LASTOP_UNKNOWN;                                                \
    INGwSpDataFactory::getInstance().reuseObject(infoData);
////////////////////////////////////////////////////////////////////////////////
  
  // Extract the info msg and msgbody from the received info
  INGwSpData* infoData =
    INGwSpDataFactory::getInstance().getNewObject();
  if(INGwSpSipUtil::createIpDataFromSipMessage(pOverlapTransInfo->pSipMsg,
                                           &(pOverlapTransInfo->slMsgBodyList),
                                           SipMessageRequest,
                                           infoData,
                                           pErr,
                                           ATTRIB_MSGBODY + ATTRIB_SIPMESG)
                                             == Sdf_co_success)
  {
    if(!infoData->getBodyLength())
    {
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, WARNING_FLAG, imERR_NONE, 
												"INGwSpMsgInfoHandler::stackCallbackRequest:"
												" Error getting message body from the info message.");
      CLEANUP_SEQUENCE(487)
      return Sdf_co_success;
    }

    if(!strlen(infoData->getBodyType()))
    {
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, WARNING_FLAG, imERR_NONE, 
												"INGwSpMsgInfoHandler::stackCallbackRequest: "
												"Error getting message body from the info message.");
      CLEANUP_SEQUENCE(487)
      return Sdf_co_success;
    }

    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, VERBOSE_FLAG, imERR_NONE, 
											"INGwSpMsgInfoHandler::stackCallbackRequest: Found msgbody type <%s>", 
											infoData->getBodyType());

    // If the msgbody type corresponds to the SIP ivr, pass on the message body
    // to the ms object.
    if(!strcasecmp(INGW_MSG_BODYTYPE_TCAP, infoData->getBodyType()))
    {
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgInfoHandler::stackCallbackRequest: "
												" Passing INFO sdp to SIPIvrMSObject");
      mLastop = LASTOP_MSERV_INFO;

			// INGW Handling 

			INGwSpSipCallController* callCtlr =
														INGwSpSipProvider::getInstance().getCallController();

      // Ask call controller to handle it

      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgInfoHandler::stackCallbackRequest:"
												" Passed INFO body to Sip call Controller.");

			callCtlr->processSasAppMgmtRequest(aSipConnection, 
																				 infoData->getBody(), 
																				 infoData->getBodyLength());


    }

    else 
    {
      // Terminate the INFO with a 200 response
			// Not supporting any other body type
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgInfoHandler::stackCallbackRequest: "
												"terminating INFO with a 200 OK.");
      CLEANUP_SEQUENCE(200);
      return Sdf_co_success;
    }

    // Reclaim the sipdata memory
    INGwSpDataFactory::getInstance().reuseObject(infoData);
  }
  else
  {
     CLEANUP_SEQUENCE(487)
  }

  return Sdf_co_success;
} // end of stackCallbackRequest

Sdf_ty_retVal INGwSpMsgInfoHandler::stackCallbackResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   int                      aRespCode        ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             ,
                   Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
  MARK_BLOCK("INGwSpMsgInfoHandler::stackCallbackResponse", TRACE_FLAG);
  INGwSipConnMinorState  connMinorState = aSipConnection->getMinorState();
  int                  connMajorState = (int)aSipConnection->getMajorState();
  INGwSipMethodType         methodType     = aSipConnection->getLastMethod();

  // If this is a 1xx or 3xx response, do not handle it.
  if((aRespCode/100 == 1) || (aRespCode/100 == 3))
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, WARNING_FLAG, imERR_NONE, 
											"INGwSpMsgInfoHandler::stackCallbackResponse: "
											"Got 1xx or 3xx response for INFO.  Not handling.");
    return Sdf_co_success;
  }

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgInfoHandler::stackCallbackResponse: connminstate <%d>, "
										"connmajorstate <%d>, methodtype <%d>, respcode <%d>, lastop <%d>", 
										connMinorState, connMajorState, aMethodType, aRespCode, mLastop);
  // HACK so that stack will not close this FD
  if(pOverlapTransInfo)
  {
    pOverlapTransInfo->dSockfd = -1;
  }
  // If the last operation is not info or if the connection minor state is not
  // msg transmitted, this response cannot be processed.
  if((connMinorState != CONN_MINSTATE_MID_2_MSGTX) ||
     (methodType     != INGW_SIP_METHOD_TYPE_INFO))
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgInfoHandler::stackCallbackResponse: "
											"The connminorstate is not MID_2_MSGTX or last method is not info.  "
											"This response cannot be processed.");
    return Sdf_co_success;
  }

  // The last operation should be either a passthrough info or a media server
  // info.  Handle both cases here.
  LastOperation tmpLastop = mLastop;
  mLastop = LASTOP_UNKNOWN;
  if(tmpLastop == LASTOP_MSERV_INFO)
  {
    // Pass on the response to the ms object if this is a failure or timeout
    // response.
		//PANKAJ to test it
		// INGW Handling 

		INGwSpSipCallController* callCtlr =
														INGwSpSipProvider::getInstance().getCallController();

    // Ask call controller to handle it

    if(!callCtlr)
    {
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
												"INGwSpMsgInfoHandler::stackCallbackResponse: "
												"Error getting the Call Controller");
    }
    else
    {
      // If this is a failure or timeout response, inform the call handler
      if(aRespCode/100 >= 4)
			{
        logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
													"INGwSpMsgInfoHandler::stackCallbackResponse: "
													"Error response <%d> recv for INFO for call id <%s>",
													aRespCode, aSipConnection->getLocalCallId().c_str());
        std::string lCallIdStr = aSipConnection->getLocalCallId();
        callCtlr->handleSasAppMgmtMsgFailure(aRespCode, lCallIdStr);
      }
      else if(aRespCode/100 == 2)
      {
        logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
													"INGwSpMsgInfoHandler::stackCallbackResponse: "
													"Success final response recv for INFO for call id <%s>",
													aSipConnection->getLocalCallId().c_str());
      }
    }
  }
  else
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgInfoHandler::stackCallbackResponse: "
											"Response received when lastop is not valid <%d>", tmpLastop);
  }

  // Update the connection state.
  aSipConnection->setMinorState(CONN_MINSTATE_IDLE);
  aSipConnection->setLastMethod(INGW_METHOD_TYPE_NONE);

  if(aRespCode >=400 )
  {
    int lCurVal = 0;
    INGwIfrSmStatMgr::instance().increment(
                      INGwSpSipProvider::miStatParamId_NumInfoSentRejected,
                      lCurVal, 1);
  }

  return Sdf_co_success;
} // stackCallbackResponse

Sdf_ty_retVal INGwSpMsgInfoHandler::stackCallbackAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             )
{
  MARK_BLOCK("INGwSpMsgInfoHandler::stackCallbackAck", TRACE_FLAG);
  return Sdf_co_fail;
}

void INGwSpMsgInfoHandler::indicateTimeout
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSipTimerType::TimerType aType           ,
                   unsigned int              aTimerid        )
{
  MARK_BLOCK("INGwSpMsgInfoHandler::indicateTimeout", TRACE_FLAG);
  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgInfoHandler::indicateTimeout: timerid <%u>, timertype <%d>", 
										aTimerid, aType);

  if(aType == INGwSipTimerType::STACK_TIMER)
  {
    // This is equivalent to a Request-Timeout(408) error.
    Sdf_ty_retVal      status     = Sdf_co_fail;
    Sdf_st_callObject *hssCallObj = aSipConnection->getHssCallObject();
    Sdf_st_error       sdferror;

    // Get the last overlap transaction info
    Sdf_st_overlapTransInfo *pOverlapTransInfo =
       INGwSpSipUtil::getLastOverlapTransInfo(hssCallObj);
    

    status = stackCallbackResponse(aSipConnection,
                                   INGW_SIP_METHOD_TYPE_INFO,
                                   408,
                                   &hssCallObj,
                                   NULL,
                                   &sdferror,
                                   pOverlapTransInfo);

    if(status != Sdf_co_success)
		{
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
												"INGwSpMsgInfoHandler::indicateTimeout: "
												"Error calling response callback on INFO handler");
    }
    else
    {
      // Reset the client transaction.
      status = sdf_ivk_uaClearRegularTransaction(hssCallObj, "INFO", 
                                                 pOverlapTransInfo);

      if(status != Sdf_co_success)
      {
        INGwSpSipUtil::checkError(status, sdferror);
          logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
														"INGwSpMsgInfoHandler::indicateTimeout: "
														"error clearing the INFO transaction");
      }
    }
  }
  else
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
		                  "INGwSpMsgInfoHandler::indicateTimeout: "
											"Unrecognized timer type <%d>", aType);
} // end of indicateTimeout

int INGwSpMsgInfoHandler::mSendRequest
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          )
{

  MARK_BLOCK("INGwSpMsgInfoHandler::mSendRequest", TRACE_FLAG);
  INGwSipConnMinorState  connMinorState = aSipConnection->getMinorState();
  int                  connMajorState = (int)aSipConnection->getMajorState();
  INGwSipMethodType         methodType     = aSipConnection->getLastMethod();
  Sdf_ty_retVal        status         = Sdf_co_fail;
  Sdf_st_callObject*   hssCallObj     = aSipConnection->getHssCallObject();
  Sdf_st_error         sdferror;

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"mSendRequest(): "
										"connminstate <%d>, connmajorstate <%d>, methodtype <%d>",
										connMinorState, connMajorState, aMethodType);


  // If the connection is not connected or if the minor state is not idle, or
  // if the last method is not NONE, then this info cannot be sent out.

  if((connMinorState != CONN_MINSTATE_IDLE) ||
     (methodType     != INGW_METHOD_TYPE_NONE) ||
     (connMajorState != CONNECTION_CONNECTED))
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"mSendRequest():Req rcvd in wrong ConnState");

    if(connMajorState == CONNECTION_CONNECTED)
    {
      return INVALID_STATE_RETRY;
    }
		else
		{
      return -1;
		}
  }

  Sdf_st_overlapTransInfo *pOverlapTransInfo = Sdf_co_null;

  status = sdf_ivk_uaStartRegularTransaction
                  (hssCallObj, &pOverlapTransInfo, "INFO", &sdferror);
  if(status != Sdf_co_success)
  {
    INGwSpSipUtil::checkError(status, sdferror);
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgInfoHandler::mSendRequest: "
											"error creating a new INFO transaction");
    return -1;
  }

  // Form an INFO request.
  status = sdf_ivk_uaMakeInfo(hssCallObj, pOverlapTransInfo, &sdferror);
  
  // If the GwData has some msgbody, then set it into the message.

  if(aGwData && (aGwData->getBody()))
  {
    char *msgBody  = (char *)(((INGwSpData*)aGwData)->getBody()    );
    char *bodyType = (char *)(((INGwSpData*)aGwData)->getBodyType());
    int   msgbodylen = ((INGwSpData *)aGwData)->getBodyLength();

    status = INGwSpSipUtil::copyMsgBodyIntoSipMessage
                       (pOverlapTransInfo->pSipMsg,
                        msgBody, msgbodylen);
    status = INGwSpSipUtil::setContentTypeInMessage
                      (pOverlapTransInfo->pSipMsg, bodyType, msgbodylen);

    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgInfoHandler::mSendRequest: Message body copied in the INFO message");
  }

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										 "INGwSpMsgInfoHandler::mSendRequest: Sending INFO to peer");

  status = INGwSpSipUtil::sendCallToPeer(hssCallObj,
                                     pOverlapTransInfo->pSipMsg,
                                     INGW_SIP_METHOD_TYPE_INFO,
                                     &sdferror,
                                     aSipConnection);

  status = sdf_ivk_uaEndRegularTransaction
                 (hssCallObj, pOverlapTransInfo, "INFO", &sdferror);

  int lCurVal = 0;
  INGwIfrSmStatMgr::instance().increment(
                      INGwSpSipProvider::miStatParamId_NumInfoSent,
                      lCurVal, 1);

  // Update the connection state.
  if(status == Sdf_co_success)
  {
    aSipConnection->setMinorState(CONN_MINSTATE_MID_2_MSGTX);
    aSipConnection->setLastMethod(INGW_SIP_METHOD_TYPE_INFO);
    mLastop = LASTOP_MSERV_INFO;
  }

  
  if(status == Sdf_co_success) return 0;
  else                         return 1;
} // end of mSendRequest

int INGwSpMsgInfoHandler::mSendResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          ,
                   int                      aCode)
{
  MARK_BLOCK("INGwSpMsgInfoHandler::mSendResponse", TRACE_FLAG);
  INGwSipConnMinorState  connMinorState = aSipConnection->getMinorState();
  int                  connMajorState = (int)aSipConnection->getMajorState();
  INGwSipMethodType         methodType     = aSipConnection->getLastMethod();
  Sdf_st_callObject*   hssCallObj     = aSipConnection->getHssCallObject();
  Sdf_ty_retVal        status         = Sdf_co_fail;
  Sdf_st_error         sdferror;

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgInfoHandler::mSendResponse: "
										"connminstate <%d>, connmajorstate <%d>, methodtype <%d>, ", 
										connMinorState, connMajorState, aMethodType);

  // If the connection is not connected or if the minor state is not msgrecd, or
  // if the last method is not INFO, then this info response cannot be sent out.

  if((connMinorState != CONN_MINSTATE_MID_2_MSGRX) ||
     (methodType     != INGW_SIP_METHOD_TYPE_INFO) ||
     (connMajorState != CONNECTION_CONNECTED))
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgInfoHandler::mSendResponse: The send response has been "
											"called in wrong connection state.  Response cannot be sent out.");
    return -1;
  }

  // Form a response
  Sdf_st_overlapTransInfo *pOverlapTransInfo =
              INGwSpSipUtil::getLastOverlapTransInfo(hssCallObj);
  status = sdf_ivk_uaFormResponse(aCode, "INFO" , hssCallObj,
                                  pOverlapTransInfo, Sdf_co_false, &sdferror);

  status = INGwSpSipUtil::sendCallToPeer(hssCallObj,
                                     pOverlapTransInfo->pSipMsg,
                                     INGW_SIP_METHOD_TYPE_INFO,
                                     &sdferror,
                                     aSipConnection);

  // Update the connection state.
  aSipConnection->setLastMethod(INGW_METHOD_TYPE_NONE);
  aSipConnection->setMinorState(CONN_MINSTATE_IDLE);
  mLastop = LASTOP_UNKNOWN;

  if(aCode >=400 )
  {
    int lCurVal = 0;
    INGwIfrSmStatMgr::instance().increment(
                      INGwSpSipProvider::miStatParamId_NumInfoRejected,
                      lCurVal, 1);
  }

  if(status == Sdf_co_success) return 0;
  else                         return -1;
} // end of mSendResponse

int INGwSpMsgInfoHandler::mSendAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          )
{
  MARK_BLOCK("INGwSpMsgInfoHandler::mSendAck", TRACE_FLAG)
  return -1;
}

void INGwSpMsgInfoHandler::setLastOperation(LastOperation aLastop)
{
  mLastop = aLastop;
}

std::string INGwSpMsgInfoHandler::toLog()const
{
   std::string ret = "\nInfo handler\n";

   char local[500];

   sprintf(local, "LastOperation:[%d] IvrTimeout:[%d]\n", 
           mLastop, muiIvrTimeout);
   ret += local;
   return ret;
}



