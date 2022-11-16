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
//     File:     INGwSpMsgCancelHandler.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   08/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipMsgHandler");

#include <INGwSipMsgHandler/INGwSpMsgCancelHandler.h>
#include <INGwSipMsgHandler/INGwSpMsgInviteHandler.h>
#include <INGwSipMsgHandler/INGwSpMsgByeHandler.h>
#include <INGwSipProvider/INGwSpDataFactory.h>

#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpData.h>
#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipProvider/INGwSpSipProvider.h>


#include <INGwSipProvider/INGwSpSipProvider.h>

#include <INGwInfraMsrMgr/MsrMgr.h>

using namespace std;


INGwSpMsgCancelHandler::INGwSpMsgCancelHandler()
{
  isCancelStarted = false;
  isByePending = false;
  muTransComplTimerid = 0;
}

INGwSpMsgCancelHandler::~INGwSpMsgCancelHandler()
{
}

Sdf_ty_retVal INGwSpMsgCancelHandler::stackCallbackRequest(
                    INGwSpSipConnection     *aSipConnection   ,
                    INGwSipMethodType        aMethodType      ,
                    Sdf_st_callObject      **ppCallObj        ,
                    Sdf_st_eventContext     *pEventContext    ,
                    Sdf_st_error            *pErr             ,
                    Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
  LogINGwTrace(false, 0, "IN stackCallbackRequest");

  short               majorState = aSipConnection->getMajorState();
  INGwSipConnMinorState minorState = aSipConnection->getMinorState();
  INGwSipMethodType        methodType = aSipConnection->getLastMethod();
  Sdf_ty_retVal status = Sdf_co_success;

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgCancelHandler::stackCallbackRequest: "
										"majorstate <%d>, minorstate <%d>, lasmethod <%d>, "
										"connection <%s>", majorState, minorState, 
										methodType, aSipConnection->getLocalCallId().c_str());

  // The CANCEL will arrive in a temporary call object, which should be in
  // the event context by now.  Extract the temporary object and send a 200 OK
  // for the CANCEL.

  Sdf_st_callObject *tempCallObj = (Sdf_st_callObject *)(pEventContext->pData);
  if(!tempCallObj)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::stackCallbackRequest: "
											"Received an event context with NULL temporary call object.");

    LogINGwTrace(false, 0, "OUT stackCallbackRequest");
    return Sdf_co_success;
  }
  else
  {
    status = sdf_ivk_uaFormResponse(200, "CANCEL", tempCallObj,
                                    Sdf_co_null, Sdf_co_false, pErr);

    status = INGwSpSipUtil::sendCallToPeer
                                   (tempCallObj,
                                    tempCallObj->pUasTransaction->pSipMsg,
                                    INGW_SIP_METHOD_TYPE_CANCEL, pErr,
                                    aSipConnection);

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::stackCallbackRequest: "
											"Sent a 200 OK for cancel.");
    pEventContext->pData = NULL;
  }

  // Check whether there is any ongoing transaction and whether the ongoing
  // transaction is an INVITE or Re-INVITE.  If not, do not do anything.

  if(minorState == CONN_MINSTATE_IDLE)
  {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::stackCallbackRequest:"
											" No ongoing transaction.  Returning");

    LogINGwTrace(false, 0, "OUT stackCallbackRequest");
    return Sdf_co_success;
  }

  if((methodType != INGW_SIP_METHOD_TYPE_INVITE) &&
     (methodType != INGW_SIP_METHOD_TYPE_REINVITE))
  {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::stackCallbackRequest: "
											"Ongoing method is neither invite nor re-invite.  Returning");
    LogINGwTrace(false, 0, "OUT stackCallbackRequest");
    return Sdf_co_success;
  }

  // XXX: With INVITE and Re-INVITE also, check should be made that they are
  // XXX: server transactions, and not INVITE generated from BP.

  bool isIncoming = false;
  INGwSpMsgBaseHandler* siphandler = aSipConnection->getSipHandler(methodType);
  if(methodType == INGW_SIP_METHOD_TYPE_INVITE)
  {
    INGwSpMsgInviteHandler* invhandler =
            dynamic_cast<INGwSpMsgInviteHandler*>(siphandler);
    if(invhandler) isIncoming = invhandler->isIncoming();
  }
  else if(methodType == INGW_SIP_METHOD_TYPE_REINVITE)
  {
#if 0
//PANKAJ See if reinvite is needed

    BpSipReinviteHandler* reinvhandler =
            dynamic_cast<BpSipReinviteHandler*>(siphandler);
    if(reinvhandler) isIncoming = reinvhandler->isIncoming();
#endif
  }

  if(!isIncoming)
  {
    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::stackCallbackRequest: "
											"Current transaction is not a server transaction.  Ignoring CANCEL");
    LogINGwTrace(false, 0, "OUT stackCallbackRequest");
    return Sdf_co_success;
  }
  
  if(methodType == INGW_SIP_METHOD_TYPE_INVITE)
  {
    // This is a failure condition.  Terminate INVITE with 487.  
    // Also there is no need to
    // check whether the transaction terminated after we asked it to terminate,
    // because if it is an incoming INVITE AND is still going on, then atleast
    // an ACK is still due from the UAS.  So we can rest assured that the
    // INVITE will not complete right away.
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::stackCallbackRequest: "
											"state-change : Ongoing transaction is invite.  "
											"Marking connection as failed, and terminating invite.");
    aSipConnection->setMajorState(CONNECTION_FAILED);
    INGwSpMsgBaseHandler *siphandler =
          aSipConnection->getSipHandler(INGW_SIP_METHOD_TYPE_INVITE);
    ((INGwSpMsgInviteHandler *)siphandler)->
                            terminateTransaction(aSipConnection, 487, 0);
  } // end of if(INVITE)
  else if(methodType == INGW_SIP_METHOD_TYPE_REINVITE)
  {
    // This is a pass through, so just request the peer (if any) to cancel the
    // re-invite.  Whatever happens afterwards (whether re-invite got cancelled
    // or it succeeded before cancel came) is the headache of the orig/term
    // party.  We need not do anything more here.
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::stackCallbackRequest: "
											"Ongoing transaction is re-invite. "
											" Requesing re-invite handler to handle cancel.");

#if 0
//PANKAJ See if reinvite is needed
    BpSipReinviteHandler* reinvhandler =
              dynamic_cast<BpSipReinviteHandler*>(siphandler);
    if(reinvhandler)
      reinvhandler->handleCancel(aSipConnection);
#endif
  }
 
  LogINGwTrace(false, 0, "OUT stackCallbackRequest");
  return Sdf_co_success;
} // end of stackCallbackRequest

Sdf_ty_retVal INGwSpMsgCancelHandler::stackCallbackResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   int                      aRespCode        ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             ,
                   Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
  LogINGwTrace(false, 0, "IN stackCallbackResponse");

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgCancelHandler::stackCallbackResponse: "
										"respcode <%d>, connection <%s>", 
										aRespCode, aSipConnection->getLocalCallId().c_str());

  // Check whether there is a CANCEL started.  If not, this is some error.
  if(!isCancelStarted)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::stackCallbackResponse: "
											"Received a CANCEL response with code <%d> when "
											"no CANCEL has been sent out", aRespCode);

    LogINGwTrace(false, 0, "OUT stackCallbackResponse");
    return Sdf_co_success;
  }
  else
  {
    // If this is a provisional response, ignore it.
    if(ISPROVRESP(aRespCode))
    {
      logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
												"INGwSpMsgCancelHandler::stackCallbackResponse: "
												"Received a provisional response with code <%d>. "
												" Ignorinng...", aRespCode);
      LogINGwTrace(false, 0, "OUT stackCallbackResponse");
      return Sdf_co_success;
    }
    else
      isCancelStarted = false;
  }

  // If the response was 408 (CANCEL timed out), then it is probably bcoz
  // something is seriously wrong (like peer is dead/network dead) and so
  // it is unlikely that ongoing INVITE/Re-INVITE will also get a response.
  // In this case, reset all states and generate a FAILED event.  When
  // continue processing happens, this connection will be blown off.
  if(aRespCode == 408)
  {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::stackCallbackResponse: "
											"state-change : Cancel timed out.  ConnMajorState to <%d>, "
											"ConnMinorState to <%d>.  Connection FAILED on "
											"connection <%s>", 
											CONNECTION_FAILED, CONN_MINSTATE_IDLE, 
											aSipConnection->getLocalCallId().c_str());

    aSipConnection->setMajorState(CONNECTION_FAILED);
    aSipConnection->setMinorState(CONN_MINSTATE_IDLE);

    //reset call min state to idle
#if 0
//PANKAJ TO DO
    aSipConnection->mSendGwResponse(INGW_SIP_METHOD_TYPE_INVITE, 0, 408,
                                 false, 0,
                                 aSipConnection->getChargeStatus());
#endif
    LogINGwTrace(false, 0, "OUT stackCallbackResponse");
    return Sdf_co_success;
  }

  // If the connection is in FAILED state, the request that was cancelled has
  // completed its transaction, and there is no BYE to be sent
  // If BYE is pending, the BYE handler will so cleanup.  No need to
  // do it here.
  if(!isByePending &&
     (aSipConnection->getMinorState() == CONN_MINSTATE_IDLE) &&
     (aSipConnection->getMajorState() == CONNECTION_FAILED))
  {
#if 0
//PANKAJ TO DO
    //reset call min state to idle
    aSipConnection->mSendGwResponse(INGW_SIP_METHOD_TYPE_INVITE, 0, 487,
                               false, 0,
                               aSipConnection->getChargeStatus());

#endif
    LogINGwTrace(false, 0, "OUT stackCallbackResponse");
    return Sdf_co_success;
  }

  // If this is a provisional response, ignore it.
  if(ISPROVRESP(aRespCode))
  {
    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::stackCallbackResponse: "
											"Received a provisional response with code <%d>.  "
											"Ignorinng...", aRespCode);
    LogINGwTrace(false, 0, "OUT stackCallbackResponse");
    return Sdf_co_success;
  }
  else
  {
    // Does not matter what the final response was.  All the same.  Now check
    // whether there was a pending BYE to be done.
    if(isByePending)
    {
      // Bye should not be sent unless the INVITE (which was CANCELled by this
      // CANCEL) transaction is complete.
      if(aSipConnection->getMinorState() != CONN_MINSTATE_IDLE)
      {
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
													"INGwSpMsgCancelHandler::stackCallbackResponse: "
													"pending bye: REINV/INV transaction still ongoing.  "
													"Not sending BYE.");
      }
      else
      {
        logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
				                  "INGwSpMsgCancelHandler::stackCallbackResponse: "
													"pending bye: Ongoing transaction completed.  "
													"Calling DISCONNECT on bye handler.");

        aSipConnection->unlockResource();
        INGwSpMsgByeHandler *byehandler = (INGwSpMsgByeHandler *)
               aSipConnection->getSipHandler(INGW_SIP_METHOD_TYPE_BYE);
        byehandler->disconnect(aSipConnection, 0);
        aSipConnection->lockResource();
      }

      isByePending = false;
    } // end of if(byepending)
    else
    {
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgCancelHandler::stackCallbackResponse: "
												"There is no pending BYE for connection <%s>", 
												aSipConnection->getLocalCallId().c_str());

      // This is a non-408 response.  If there is some ongoing transaction, then
      // start a timer so that if the ongoing transaction does not receive any
      // final response, the connection state can be reset 

      if(aSipConnection->getMinorState() != CONN_MINSTATE_IDLE)
      {
        if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().
                 getStackTimer().startTransComplTimer
                                       (muTransComplTimerid, 32000, 
																			  aSipConnection->getLocalCallId(),
                                        INGW_SIP_METHOD_TYPE_CANCEL))
        {
          logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
														"Error starting TRANS_COMPL timer.  "
														"Calling indicateTimeout anyway.");

          indicateTimeout(aSipConnection, INGW_SIP_METHOD_TYPE_CANCEL,
                          INGwSipTimerType::TRANSCOMPL_TIMER, 0);
        }
        else
        {
          logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "Started TRANS_COMPL timer"
														" with id <%u>", muTransComplTimerid);
        }
      } // end of if(minstate !idle)
    }
  } // end of else


  LogINGwTrace(false, 0, "OUT stackCallbackResponse");
  return Sdf_co_success;
} // end of stackCallbackResponse

Sdf_ty_retVal INGwSpMsgCancelHandler::stackCallbackAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             )
{
  return Sdf_co_fail;
}

void INGwSpMsgCancelHandler::indicateTimeout
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSipTimerType::TimerType aType           ,
                   unsigned int              aTimerid        )
{
  LogINGwTrace(false, 0, "IN indicateTimeout");

  switch(aType)
  {
    case INGwSipTimerType::STACK_TIMER:
    {
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgCancelHandler::indicateTimeout: Received STACK_TIMER");
      // This is equivalent to Request-Timeout(408) response code.
      Sdf_ty_retVal      status     = Sdf_co_fail;
      Sdf_st_callObject *hssCallObj = aSipConnection->getHssCallObject();
      Sdf_st_error       sdferror;

      status = stackCallbackResponse(aSipConnection,
                                     INGW_SIP_METHOD_TYPE_BYE,
                                     408,
                                     &hssCallObj,
                                     NULL,
                                     &sdferror,
                                     NULL);

      break;
    }
    case INGwSipTimerType::TRANSCOMPL_TIMER:
    {
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgCancelHandler::indicateTimeout: "
												"Received TRANSCOMPL_TIMER");

      // Reset all connection states and generate a FAILED event. 
      aSipConnection->setMinorState(CONN_MINSTATE_IDLE);
      aSipConnection->setLastMethod(INGW_METHOD_TYPE_NONE);
      aSipConnection->setMajorState((CONNECTION_FAILED));

#if 0
//PANKAJ TO DO
      //reset call min state to idle
      aSipConnection->mSendGwResponse(INGW_SIP_METHOD_TYPE_INVITE, 0, 408,
                                 false, 0,0);
#endif                                 
      muTransComplTimerid = 0;

      break;
    }
    default:
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
												"INGwSpMsgCancelHandler::indicateTimeout: "
												"Unrecognized timer type <%d>", aType);
      break;
  } // end of switch

  LogINGwTrace(false, 0, "OUT indicateTimeout");
} // end of stackCallbackTimeout

////////////////////////////////////////////////////////////////////////////////
// This method will only be called in two cases.
// 1. An INVITE that got cancelled on peer.
// 2. A Re-INVITE that got cancelled on peer.
// In both cases, a BYE is not required to be sent, as the INVITE responses
// and requests which will pass through between two peers will sort themselves
// out and the originating/terminating party will separately send a BYE if
// needed.  So there is no need to worry about whether BYE should be sent after
// this CANCEL completes.
////////////////////////////////////////////////////////////////////////////////
int INGwSpMsgCancelHandler::mSendRequest
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          )
{
  LogINGwTrace(false, 0, "IN mSendRequest");

  aMethodType = aSipConnection->getLastMethod();
  // Check whether the method type is either INVITE or Re-INVITE.  If not,
  // CANCEL cannot be sent.
  if((aMethodType != INGW_SIP_METHOD_TYPE_INVITE  ) &&
     (aMethodType != INGW_SIP_METHOD_TYPE_REINVITE))
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::mSendRequest: "
											"Method called with invalid method type <%d>", aMethodType);
    LogINGwTrace(false, 0, "OUT mSendRequest");
    return -1;
  }

  // Check whether there is an ongoing transaction.  If not, CANCEL cannot be
  // sent.
  if(aSipConnection->getMinorState() == CONN_MINSTATE_IDLE)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::mSendRequest: "
											"There is no ongoing transaction and "
											"the connection is in minor state <%d>.  "
											"Nothing to CANCEL", 
											CONN_MINSTATE_IDLE);
    LogINGwTrace(false, 0, "OUT mSendRequest");
    return -1;
  }

  // Okay, now we are safe atleast to try to CANCEL.  Get hold of the correct
  // handler and request it to terminate the transaction.  If and when the
  // transaction gets into terminatable state, sendCancel will be called on this
  // class.
  INGwSpMsgBaseHandler *siphandler = aSipConnection->getSipHandler(aMethodType);
  if(aMethodType == INGW_SIP_METHOD_TYPE_INVITE)
  {
    // If this is an INVITE, set the call state to FAILED, since we are
    // cancelling the INVITE.
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::mSendRequest: "
											"Ongoing transaction found to be INVITE.  Terminating the INVITE.");
    aSipConnection->setMajorState(CONNECTION_FAILED);
    ((INGwSpMsgInviteHandler *)(siphandler))->
                    terminateTransaction(aSipConnection, 487, TERMINATE_CANCEL);
  }
  else if(aMethodType == INGW_SIP_METHOD_TYPE_REINVITE)
  {
#if 0
//PANKAJ May be we won't support Re-invite

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::mSendRequest: "
											"Ongoing transaction found to be Re-INVITE.  "
											"Terminating the Re-INVITE.");
    ((BpSipReinviteHandler *)(siphandler))->
                    terminateTransaction(aSipConnection, 487, TERMINATE_CANCEL);
#endif

  }
  LogINGwTrace(false, 0, "OUT mSendRequest");
  return 0;
} // end of mSendRequest

// CANCEL responses are never sent pass through (through the gateway).  Thus,
// this method is dummy.
int INGwSpMsgCancelHandler::mSendResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          ,
                   int                      aCode)
{
  return -1;
}

// No ACK for Cancel.  Dummy method
int INGwSpMsgCancelHandler::mSendAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          )
{
  return -1;
}

int INGwSpMsgCancelHandler::sendCancel
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   bool                     aIsByePending    )
{
  LogINGwTrace(false, 0, "IN sendCancel");
  setByePending(aIsByePending);
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "INGwSpMsgCancelHandler::sendCancel: isbyepending <%d> for connection <%s>", aIsByePending, aSipConnection->getLocalCallId().c_str());

  Sdf_st_callObject  *hssCallObj = aSipConnection->getHssCallObject();
  Sdf_ty_retVal       status     = Sdf_co_fail;
  Sdf_st_error        sdferror;
  int                 retval     = -1;

  Sdf_st_overlapTransInfo *tempObj = NULL;
  status = sdf_ivk_uaStartTransaction(hssCallObj, &tempObj, "CANCEL", 
                                      &sdferror);
  if(status != Sdf_co_success)
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::sendCancel: "
											"error creating a new CANCEL transaction for CANCEL\n");
    LogINGwTrace(false, 0, "OUT sendCancel");
    return -1;
  }

  // Form a CANCEL request
  status = sdf_ivk_uaCancelCall(hssCallObj, &sdferror);

  // Send the request out.
  status = INGwSpSipUtil::sendCallToPeer(hssCallObj                 ,
                                     hssCallObj->pUacTransaction->pSipMsg,
                                     INGW_SIP_METHOD_TYPE_CANCEL,
                                     &sdferror                  ,
                                     aSipConnection);

  if(status == Sdf_co_success)
  {
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::sendCancel: Sent CANCEL");
    isCancelStarted = true;
    retval =  0;
    status = sdf_ivk_uaEndTransaction (hssCallObj, NULL, "CANCEL",
                                       &sdferror);
  }
  else
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgCancelHandler::sendCancel: "
											"Error creating CANCEL transaction");
    INGwSpSipUtil::checkError(status, sdferror);
    retval = -1;
  }

  LogINGwTrace(false, 0, "OUT sendCancel");
  return retval;
} // end of sendCancel

bool INGwSpMsgCancelHandler::isTransactionComplete()
{
  return !isCancelStarted;
}

void INGwSpMsgCancelHandler::setByePending(bool aPending)
{
  LogINGwTrace(false, 0, "IN setByePending");
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgCancelHandler::setByePending: byepending <%d>", 
										aPending);
  isByePending = aPending;
  LogINGwTrace(false, 0, "OUT setByePending");
}

bool INGwSpMsgCancelHandler::stopTransComplTimer()
{
  LogINGwTrace(false, 0, "IN stopTransComplTimer");
  if(!muTransComplTimerid) 
  {
     LogINGwTrace(false, 0, "OUT stopTransComplTimer");
     return false;
  }

  bool ret = INGwSpSipProvider::getInstance().getThreadSpecificSipData().
           getStackTimer().stopTransComplTimer(muTransComplTimerid);
  muTransComplTimerid = 0;
  LogINGwTrace(false, 0, "OUT stopTransComplTimer");
  return ret;
} // end of stopTransComplTimer

std::string INGwSpMsgCancelHandler::toLog()const
{
   std::string ret = "\nCancelHandler\n";

   char local[500];

   sprintf(local, "CancelStarted[%d] ByePending[%d] TransComplTimerID[%d]\n",
           isCancelStarted, isByePending, muTransComplTimerid);

   ret += local;
   return ret;
};
