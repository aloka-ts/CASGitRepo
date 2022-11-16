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
//     File:     INGwSpMsgByeHandler.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   08/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipMsgHandler");

#include <INGwSipMsgHandler/INGwSpMsgByeHandler.h>
#include <INGwSipMsgHandler/INGwSpMsgCancelHandler.h>
#include <INGwSipMsgHandler/INGwSpMsgInviteHandler.h>
#include <INGwSipProvider/INGwSpDataFactory.h>

#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpData.h>
#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <INGwSipProvider/INGwSpSipProviderConfig.h>
#include <INGwSipProvider/INGwSpTcpConnMgr.h>


#include <INGwInfraMsrMgr/MsrMgr.h>

#include <errno.h>

using namespace std;


INGwSpMsgByeHandler::INGwSpMsgByeHandler()
{
  mTempCallObj = NULL;
}

INGwSpMsgByeHandler::~INGwSpMsgByeHandler()
{
}

void INGwSpMsgByeHandler::reset(void)
{
    mByeStateContext.reset();
    mTempCallObj = NULL;
}

Sdf_ty_retVal INGwSpMsgByeHandler::stackCallbackRequest(
                    INGwSpSipConnection         *aSipConnection   ,
                    INGwSipMethodType             aMethodType      ,
                    Sdf_st_callObject      **ppCallObj        ,
                    Sdf_st_eventContext     *pEventContext    ,
                    Sdf_st_error            *pErr             ,
                    Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "IN stackCallbackRequest");

  INGwSipConnMinorState  connMinorState = aSipConnection->getMinorState();
  int                  connMajorState = (int)aSipConnection->getMajorState();
  INGwSipMethodType         lastMethod     = aSipConnection->getLastMethod();
  Sdf_ty_retVal        status         = Sdf_co_fail;

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgByeHandler::stackCallbackRequest: "
										"Bye received with majorstate <%d>, minorstate <%d>, "
										"lastmethod <%d>, connection <%s>",
										connMajorState, connMinorState, lastMethod, 
										aSipConnection->getLocalCallId().c_str());

  // Check here that we are in CONNECTED state.  
  // Also, in early dialog, BYE can be received.  This happens
  // before INVITE gets completed.
  // Bye can recv after 200 ok for invite is done.
  // we should entertain such Bye request

  if((connMajorState != CONNECTION_CONNECTED) &&
       (connMinorState != CONN_MINSTATE_MID_3_RSPRX)&&
			 (connMinorState != CONN_MINSTATE_MID_3_RSPTX))
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgByeHandler::stackCallbackRequest: "
											"Bye received in wrong connection state: <%d,XX>\n", 
											connMajorState);
    pErr->errCode = Sdf_en_callStateError;
    sprintf(pErr->ErrMsg, 
						"Bye received in wrong connection state: <%d,XX>", connMajorState);
    LogINGwTrace(false, 0, "OUT stackCallbackRequest");
    return Sdf_co_fail;
  }

  Sdf_st_callObject *callobj = *ppCallObj;

  // Check whether there is an ongoing transaction.  If so, request that
  // handler to terminate the transaction.  Also, in this case we should have
  // got a temporary call object, which should be used to send BYE responses.

  if((connMinorState != CONN_MINSTATE_IDLE) && 
    (connMinorState != CONN_MINSTATE_MID_3_RSPRX) &&
     (connMinorState != CONN_MINSTATE_MID_3_RSPTX))
  {
    Sdf_st_callObject *tempCallObj = (Sdf_st_callObject*)(pEventContext->pData);
    if(!tempCallObj)
    {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
												"INGwSpMsgByeHandler::stackCallbackRequest: "
												"Found NULL temporary call object.");
      LogINGwTrace(false, 0, "OUT stackCallbackRequest");
      return Sdf_co_fail;
    }

    pEventContext->pData = NULL;

    // Now store the temporary object in the BYE handler so that when
    // continue processing happens, we can send a 200 OK on it.  Also increment
    // its reference count since we are holding one.
    mTempCallObj = tempCallObj;
    HSS_LOCKEDINCREF(tempCallObj->dRefCount);
    callobj = tempCallObj;
  }

  // Form a 200 OK response.
  status = sdf_ivk_uaFormResponse(200, "BYE" , callobj,
                                  Sdf_co_null, Sdf_co_false, pErr);


  // Here again, if there is an ongoing transaction, request the handler of
  // that transaction to terminate the transaction.

  if((connMinorState != CONN_MINSTATE_IDLE)&&
     (connMinorState != CONN_MINSTATE_MID_3_RSPRX) &&
     (connMinorState != CONN_MINSTATE_MID_3_RSPTX))
  {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgByeHandler::stackCallbackRequest: "
											"Terminating the ongoing transaction method <%d>", lastMethod);

    // Set the connection state to FAILED, since we received a BYE during an
    // operation.
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgByeHandler::stackCallbackRequest: state-change : "
											"ConnMajorState from <%d> to <%d>, "
											"ByeContext.ByeRecd from <%d> to <%d>", 
											connMajorState, CONNECTION_FAILED,
											mByeStateContext.mbByeReceived, true);

    aSipConnection->setMajorState(CONNECTION_FAILED);
    mByeStateContext.mbByeReceived = true;

    // Also inform the handler that a BYE is pending, so that when the
    // transaction completes, we can send a BYE.
    INGwSpMsgBaseHandler *siphandler = aSipConnection->getSipHandler(lastMethod);
    bool transTerminated = false;
    if(lastMethod == INGW_SIP_METHOD_TYPE_INVITE)
    {
      INGwSpMsgInviteHandler* invhandler = (INGwSpMsgInviteHandler *)siphandler;
      transTerminated = invhandler->terminateTransaction
        (aSipConnection, 487, TERMINATE_CANCEL+TERMINATE_BYE);
    }
    else if(lastMethod == INGW_SIP_METHOD_TYPE_REINVITE)
    {
#if 0
//PANKAJ - May be no re-invite
      BpSipReinviteHandler* reinvhandler = (BpSipReinviteHandler *)siphandler;
      transTerminated = reinvhandler->terminateTransaction
        (aSipConnection, 487, TERMINATE_CANCEL+TERMINATE_BYE);
#endif
    }
    else
    {
      // Any non-invite and non-reinvite server transactions will terminate now.
      transTerminated = true;
      siphandler->terminateServerTransaction(aSipConnection, lastMethod);
    }

    // If the ongoing transaction did not terminate, return now.  That handler
    // will call us again when transaction terminates.
    if(!transTerminated)
    {
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgByeHandler::stackCallbackRequest: "
												"Ongoing transaction not terminated yet.  "
												"200 OK to BYE will be sent later.");
      mByeStateContext.isFailedByePending = true;
      LogINGwTrace(false, 0, "OUT stackCallbackRequest");
      return Sdf_co_success;
    }
  }
  else
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgByeHandler::stackCallbackRequest: "
											"Not terminating ongoing transaction.");
    // Update the connection state

    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgByeHandler::stackCallbackRequest: state-change : "
											"ConnMajorState from <%d> to <%d>, "
											"ByeContext.ByeRecd from <%d> to <%d>", 
											connMajorState, CONNECTION_DISCONNECTED, 
											mByeStateContext.mbByeReceived, true);

    aSipConnection->setMajorState(CONNECTION_DISCONNECTED);
    mByeStateContext.mbByeReceived = true;
  }

  // We will send 200 ok for BYE
  aSipConnection->setMinorState(CONN_MINSTATE_IDLE);

  //Now INGW specific handling

  // 1 - Remove Sas info from map

  INGwSpSipCallController* callCtlr =
                            INGwSpSipProvider::getInstance().getCallController();

  std::string lCallIdStr = aSipConnection->getLocalCallId();

	std::string lHost = aSipConnection->getGwIPAddress();
  int ret = callCtlr->removeEndPoint(lHost, lCallIdStr);

  // 2 - Ask call controller to handle it
  // Call controller will send response for Bye

  callCtlr->processSasServerDeregistration(aSipConnection);

	// 3 - Send replication Message

	aSipConnection->transactionEnded(this, BYE_HANDLER);

  // 4 - Clean Up call and connection

  // PANKAJ To Do
#if 0 
  bool bContProcessing = false;
  if(bContProcessing == false)
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgByeHandler::stackCallbackRequest: "
											"Calling continue processing from self on connection <%s>", 
											aSipConnection->getLocalCallId().c_str());
    aSipConnection->continueProcessing(FROM_SELF);
  }
  else
	{
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgByeHandler::stackCallbackRequest: "
											"Not calling continue processing on connection <%s>", 
											aSipConnection->getLocalCallId().c_str());
  }
#endif
  LogINGwTrace(false, 0, "OUT stackCallbackRequest");
  return status;
} // end of stackCallbackRequest

Sdf_ty_retVal INGwSpMsgByeHandler::stackCallbackResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   int                      aRespCode        ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             ,
                   Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "IN stackCallbackResponse");
  short               majorState = aSipConnection->getMajorState();
  INGwSipConnMinorState minorState = aSipConnection->getMinorState();
  INGwSipMethodType        methodType = aSipConnection->getLastMethod();
  Sdf_ty_retVal       status     = Sdf_co_fail;

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgByeHandler::stackCallbackResponse: "
										"majorstate <%d>, minorstate <%d>, lastmethod <%d>, "
										"respcode <%d>, connection <%s>", 
										majorState, minorState, methodType, aRespCode, 
										aSipConnection->getLocalCallId().c_str());

  // Validate that this is ok to be received in this state.
  if((majorState != CONNECTION_DISCONNECTED) &&
     (majorState != CONNECTION_FAILED      )) 
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgByeHandler::stackCallbackResponse: "
											"response callback received in wrong connection state <%d,XX>\n", 
											(int)majorState, minorState);

    pErr->errCode = Sdf_en_callStateError;
    LogINGwTrace(false, 0, "OUT stackCallbackResponse");
    return Sdf_co_fail;
  }

  // Handle based on the response category.
  if(ISPROVRESP(aRespCode))
  {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"provisional response received for BYE.  Ignoring.\n");
  }
  else
  {
    // Apart from provisional response, it does not matter what response we
    // receive.  Time to cleanup now.  Update the connection state.

    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, VERBOSE_FLAG, imERR_NONE, 
													"INGwSpMsgByeHandler::stackCallbackResponse: "
													"state-change : ConnMajorState from <%d> to <%d>, "
													"ByeContext.byeCompleted from <%d> to <%d>", 
													majorState, CONNECTION_DISCONNECTED,
													mByeStateContext.mbByeCompleted, true);
    aSipConnection->setMajorState(CONNECTION_DISCONNECTED);

    mByeStateContext.mbByeCompleted = true;
    bool bContinueProcessing = false;

    // If tcp mode of transport, we need to clean it now
    if( (aRespCode == 200 ) && (! INGwSpSipProviderConfig::isTransportUdp()) )
    {
      INGwSpTcpConnMgr::instance().removeClientConn(aSipConnection->getGwIPAddress());
    }

    if(bContinueProcessing == false)
    {
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgByeHandler::stackCallbackResponse: "
												"Calling continue processing on connection <%s>", 
												aSipConnection->getLocalCallId().c_str());
      aSipConnection->continueProcessing(FROM_SELF);
    }
    else
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgByeHandler::stackCallbackResponse: "
												"Not calling continue processing on connection <%s>", 
												aSipConnection->getLocalCallId().c_str());
  }
       
  LogINGwTrace(false, 0, "OUT stackCallbackResponse");
  return Sdf_co_success;
} // end of stackCallbackResponse

Sdf_ty_retVal INGwSpMsgByeHandler::stackCallbackAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             )
{
  return Sdf_co_fail;
}

void INGwSpMsgByeHandler::indicateTimeout
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSipTimerType::TimerType aType           ,
                   unsigned int              aTimerid        )
{
   LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "IN indicateTimeout");
   // This is equivalent to a Request-Timeout(408) error.
   Sdf_ty_retVal      status     = Sdf_co_fail;
   Sdf_st_callObject *hssCallObj = aSipConnection->getHssCallObject();
   Sdf_st_error       sdferror;

   status = stackCallbackResponse(aSipConnection, INGW_SIP_METHOD_TYPE_BYE,
                                  408, &hssCallObj, NULL, &sdferror, NULL);

   hssCallObj = aSipConnection->getHssCallObject();

   if(status != Sdf_co_success)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "INGwSpMsgByeHandler::indicateTimeout: "
                                            "Error calling response callback "
                                            "on BYE handler");
   }
   else if(hssCallObj == NULL)
   {
      logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwSpMsgByeHandler::indicateTimeout: "
                                            "CallObj already cleared.");
   }
   else if(aType == INGwSipTimerType::STACK_TIMER)
   {
      // Reset the client transaction.
      status = sdf_ivk_uaClearTransaction(hssCallObj, NULL, "BYE", 
                                          Sdf_en_uacTransaction);
      if(status != Sdf_co_success)
      {
         INGwSpSipUtil::checkError(status, sdferror);
         logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, 0, 
                         "INGwSpMsgByeHandler::indicateTimeout: error clearing the "
                         "BYE transaction");
      }
      else
      {
         logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, 0, 
                         "INGwSpMsgByeHandler::indicateTimeout: successfully "
                         "cleared the BYE transaction");
      }
  }

  LogINGwTrace(false, 0, "OUT indicateTimeout");
} // end of indicateTimeout

int INGwSpMsgByeHandler::mSendRequest
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData)
{
  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "IN INGwSpMsgByeHandler::mSendRequest");
	int retCode =  disconnect(aSipConnection, 408);
  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "OUT INGwSpMsgByeHandler::mSendRequest");
	return retCode;
}

int INGwSpMsgByeHandler::mSendResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          ,
                   int                      aCode)
                   
{
  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "IN INGwSpMsgByeHandler::mSendResponse");
	int retCode =  disconnect(aSipConnection, aCode);

  // If tcp mode of transport, we need to clean it now
  if(! INGwSpSipProviderConfig::isTransportUdp())
  {
    INGwSpTcpConnMgr::instance().removeClientConn(aSipConnection->getGwIPAddress());
  }

  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "OUT INGwSpMsgByeHandler::mSendResponse");
	return retCode;
}

// Will never happen.
int INGwSpMsgByeHandler::mSendAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          )
{
  return -1;
}

short INGwSpMsgByeHandler::disconnect(INGwSpSipConnection *aSipConnection, int aErrCode)
{
  LogINGwTrace(aSipConnection->getCall().mLogFlag, 0, "IN disconnect");

  if(aErrCode < 400)
  {
    if(aSipConnection->miFailureRespCode >= 400)
      aErrCode = aSipConnection->miFailureRespCode;
    else
      aErrCode = 603;
  }

  Sdf_ty_retVal        status         = Sdf_co_fail;
  short                retval         = -1;
  Sdf_st_callObject   *hssCallObj     = aSipConnection->getHssCallObject();
  short                majorState     = aSipConnection->getMajorState();
  INGwSipConnMinorState  connMinorState = aSipConnection->getMinorState();
  Sdf_st_error         sdferror;

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgByeHandler::disconnect: "
										"majorstate <%d>, minorstate <%d>, connection <%s>", 
										 majorState, connMinorState, aSipConnection->getLocalCallId().c_str());

  // Check whether BYE is complete.  If so, we just need to cleanup this
  // connection.  The cleanup should also be done when the connection is in
  // FAILED state and there is no BYE pending for it, or when this connection
  // did not establish any dialog.
  if((mByeStateContext.mbByeCompleted            )    || 
     (majorState == CONNECTION_CREATED) ||
     ((majorState == CONNECTION_FAILED)   &&
      (!mByeStateContext.isFailedByePending          ) ) )
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgByeHandler::disconnect: Cleaning up the connection");

    // If the connection is dead, it would have already been removed from the
    // call.  Should not do it again.
    if(!aSipConnection->isDead())
      aSipConnection->getCall().
        removeConnection(aSipConnection->getSelfConnectionId());
    INGwSpSipUtil::releaseConnection(aSipConnection);

    LogINGwTrace(false, 0, "OUT disconnect");
    return 0;
  }

  // Check whether a BYE has been received.  If so, then send an ok response
  // to the BYE.  If there is a temporary call object on which the BYE was
  // delivered, the response should be sent on that object, not the one stored
  // in the connection.
  else if(mByeStateContext.mbByeReceived)
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgByeHandler::disconnect: Bye has already been received. "
											" Sending a 200 OK for BYE\n");

    Sdf_st_callObject *callobj = hssCallObj  ;
    if(mTempCallObj)   callobj = mTempCallObj;

    if(true)
    {
       INGwSpSipUtil::setBodyFromAttr(callobj->pUasTransaction->pSipMsg, 
                                  aSipConnection);
       INGwSpSipUtil::addHeadersFromAttr(callobj->pUasTransaction->pSipMsg,
                                     aSipConnection);
    }

    status = INGwSpSipUtil::sendCallToPeer(callobj                    ,
                                       callobj->pUasTransaction->pSipMsg,
                                       INGW_SIP_METHOD_TYPE_BYE     ,
                                       &sdferror                  ,
                                       aSipConnection);

    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgByeHandler::disconnect: state-change : "
											"ByeContext.isFailedByePending from <%d> to <%d>,"
											" ByeContext.byeCompleted from <%d> to <%d>", 
											mByeStateContext.isFailedByePending, false, 
											mByeStateContext.mbByeCompleted, true);

    mByeStateContext.isFailedByePending = false;
    mByeStateContext.mbByeCompleted     = true ;

    if(mTempCallObj)
    {
      // Our usage of the temporary object ends here.  Release a reference
      // so that if no one else is holding the temporary object, it can get
      // destroyed.
      HSS_LOCKEDDECREF(mTempCallObj->dRefCount);
      mTempCallObj = NULL;
    }

    if(status == Sdf_co_success)
    {
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgByeHandler::disconnect: Sent a 200 OK for BYE\n");
      retval = 0;
    }
    else
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
												"INGwSpMsgByeHandler::disconnect: Error sending 200 OK for BYE");

    // Check here whether all ongoing transactions are complete.  If so, release
    // the connection.
    if(connMinorState == CONN_MINSTATE_IDLE)
    {
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgByeHandler::disconnect: "
												"All ongoing transactions are complete.  "
												"Calling disconnect again to release the connection");
      retval = disconnect(aSipConnection, 0);
    }
  }
  else
  {
    // No BYE was received.  So BYE needs to be sent out here.
    Sdf_st_callObject  *hssCallObj = aSipConnection->getHssCallObject();
    Sdf_ty_retVal       status     = Sdf_co_fail;
    INGwSipConnMinorState minorState = aSipConnection->getMinorState();
    INGwSipMethodType        methodType = aSipConnection->getLastMethod();
    Sdf_st_error        sdferror;

    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, VERBOSE_FLAG, imERR_NONE, 
											"INGwSpMsgByeHandler::disconnect: Sending BYE");

    // Check whether there is an ongoing transaction.  If so, request the
    // concerned handler to terminate that transaction.

    bool transactionTerminated = true;
    if(minorState != CONN_MINSTATE_IDLE)
    {
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, VERBOSE_FLAG, imERR_NONE, 
												"INGwSpMsgByeHandler::disconnect: Found an ongoing "
												"transaction with method <%d>", methodType);

      // Don't get into the business of cancelling a cancel or BYE transaction.

      INGwSpMsgBaseHandler *siphandler = aSipConnection->getSipHandler(methodType);
      if((methodType != INGW_SIP_METHOD_TYPE_CANCEL) &&
         (methodType != INGW_SIP_METHOD_TYPE_BYE   ))
      {
        // Mark the connection as failed, so that no more requests on this
        // connection will be entertained.
        logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
													"INGwSpMsgByeHandler::disconnect: state-change : "
													"ConnMajorState from <%d> to <%d>", 
													majorState, CONNECTION_FAILED);

        aSipConnection->setMajorState(CONNECTION_FAILED);

        // If the connection is in connected state, a BYE will be required
        // after the ongoing transaction terminates.

        TerminateType termtype = TERMINATE_CANCEL;

        if(majorState == CONNECTION_CONNECTED)
          termtype |= TERMINATE_BYE;

        transactionTerminated    =
          siphandler->terminateTransaction(aSipConnection, aErrCode, termtype);
        if(methodType == INGW_SIP_METHOD_TYPE_INVITE )
        {
           if((minorState == CONN_MINSTATE_MID_3_MSGRX) &&
              (methodType == INGW_SIP_METHOD_TYPE_INVITE))
           {
           }
           else
           {
             mByeStateContext.isFailedByePending = true;
           }
        }
      }
      else
      {
        if(methodType ==INGW_SIP_METHOD_TYPE_CANCEL)
        {
          logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
														"INGwSpMsgByeHandler::disconnect: Ongoing method is CANCEL."
														"  Setting a BYE pending in CANCEL handler.");

          INGwSpMsgCancelHandler* cancelhandler =
            dynamic_cast<INGwSpMsgCancelHandler*>(siphandler);
          cancelhandler->setByePending(true);
        }
        else
        {
          logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
														"INGwSpMsgByeHandler::disconnect: Ongoing method is BYE.");
        }
      }

      // Check whether the ongoing transaction got completed or not.  If not,
      // simply return, since the respective method handler will call us again
      // to send BYE, once the transaction completes.

      if(!transactionTerminated)
      {
        // BYE will be required only if the connection is already connected.
        // Otherwise, the transaction's completion can trigger the connection's
        // destruction without a BYE being sent.
        if(majorState == CONNECTION_CONNECTED)
        {
          logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
														"INGwSpMsgByeHandler::disconnect: state-change: "
														"ByeContext.failedByePending from <%d> to <%d>. "
														" BYE will be sent when the ongoing transaction completes", 
														mByeStateContext.isFailedByePending, true);
          mByeStateContext.isFailedByePending = true;
        }

        LogINGwTrace(false, 0, "OUT disconnect");
        return 0;
      }
    }

    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, VERBOSE_FLAG, imERR_NONE, 
											"disconnect: Sending BYE, no ongoing transaction.");

      // If the ongoing transaction was terminated, or there was no ongoing
      // transaction, then check whether the connection was in CONNECTED or
      // FAILED state and failed BYE pending is set.
      // If so, we need to send out a BYE.
      if((majorState == CONNECTION_CONNECTED) ||
         ((aSipConnection->getMajorState() == CONNECTION_FAILED) &&
          (mByeStateContext.isFailedByePending          ) ) ||
          majorState == CONNECTION_DISCONNECTED ) 
      {
         Sdf_st_overlapTransInfo *tempObj = NULL;
         status = sdf_ivk_uaStartTransaction(hssCallObj, &tempObj, "BYE", 
                                             &sdferror);
         if(status != Sdf_co_success)
         {
            INGwSpSipUtil::checkError(status, sdferror);
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
															"INGwSpMsgByeHandler::disconnect: error "
                              "creating a new BYE transaction for "
                              "terminating BYE\n");

            LogINGwTrace(false, 0, "OUT disconnect");
            return -1;
         }

         // Form a BYE request.
         status = sdf_ivk_uaTerminateCall(hssCallObj, &sdferror);

         // Update the call state.
         logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, 0, 
                              "INGwSpMsgByeHandler::disconnect: state-change: "
															"ConnMajorState from "
                              "<%d> to <%d>", 
                              majorState, CONNECTION_DISCONNECTED);

         aSipConnection->setMajorState(CONNECTION_DISCONNECTED);

  
         // Send the request out.
         if(true)
         {
            INGwSpSipUtil::setBodyFromAttr(hssCallObj->pUacTransaction->pSipMsg,
                                       aSipConnection);
            INGwSpSipUtil::addHeadersFromAttr(hssCallObj->pUacTransaction->pSipMsg,
                                          aSipConnection);
         }

         INGwSpSipUtil::sendCallToPeer(hssCallObj, 
                                   hssCallObj->pUacTransaction->pSipMsg,
                                   INGW_SIP_METHOD_TYPE_BYE, &sdferror, 
                                   aSipConnection);

         logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, 0, 
                         "INGwSpMsgByeHandler::disconnect: Sent out BYE");

         status = sdf_ivk_uaEndTransaction(hssCallObj, NULL, "BYE", &sdferror);


       	// Send replication Message

       	aSipConnection->transactionEnded(this, BYE_HANDLER);


         if(status == Sdf_co_success) retval =  0;
         else                         retval = -1;
      } 
      else
         retval = 0;
  } // end of else

  LogINGwTrace(false, 0, "OUT disconnect");
  return retval;
} // end of disconnect

void INGwSpMsgByeHandler::setFailedByePending(bool aPending)
{
  LogINGwTrace(false, 0, "IN setFailedByePending");
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgByeHandler::setFailedByePending: "
										"Setting pending to <%d>", aPending);

  mByeStateContext.isFailedByePending = aPending;
  LogINGwTrace(false, 0, "OUT setFailedByePending");
}

short INGwSpMsgByeHandler::disconnectWithAlso
  (INGwSpSipConnection *aSipConnection, const char * aAccountCode)
{
// NOT SUPPORTED
	return -1;
} 

void INGwSpMsgByeHandler::receiveRedirectionInfo
  (INGwSpSipConnection* aSipConnection,
   int              aContactCount,
   INGwSipEPInfo*        aContactList)
{
// NOT SUPPORTED
}

std::string INGwSpMsgByeHandler::toLog()const
{
   std::string ret = "\nBye handler\n";

   ret += mByeStateContext.toLog();

   return ret;
}
