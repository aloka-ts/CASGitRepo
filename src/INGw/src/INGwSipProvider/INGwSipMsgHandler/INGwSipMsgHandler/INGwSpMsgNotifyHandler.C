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
//     File:     INGwSpMsgNotifyHandler.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   08/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipMsgHandler");

#include <INGwSipMsgHandler/INGwSpMsgNotifyHandler.h>
#include <INGwSipProvider/INGwSpDataFactory.h>

#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpData.h>
#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwSipProvider/INGwSpSipProviderConfig.h>
#include <INGwSipProvider/INGwSpTcpConnMgr.h>

#include <INGwInfraMsrMgr/MsrMgr.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>
#include <INGwFtPacket/INGwFtPktTcapCallSeqAck.h>
#include <INGwIwf/INGwIwfProvider.h>
using namespace std;

#define INGW_MSG_BODYTYPE_TCAP  "application/tcap"

INGwSpMsgNotifyHandler::INGwSpMsgNotifyHandler()
{
  reset();
}

INGwSpMsgNotifyHandler::~INGwSpMsgNotifyHandler()
{
}

void INGwSpMsgNotifyHandler::reset()
{
  mLastop = LASTOP_UNKNOWN;
  muiIvrTimeout = 0;
}

Sdf_ty_retVal INGwSpMsgNotifyHandler::stackCallbackRequest(
                    INGwSpSipConnection         *aSipConnection   ,
                    INGwSipMethodType             aMethodType      ,
                    Sdf_st_callObject      **ppCallObj        ,
                    Sdf_st_eventContext     *pEventContext    ,
                    Sdf_st_error            *pErr             ,
                    Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
  MARK_BLOCK("INGwSpMsgNotifyHandler::stackCallbackRequest", TRACE_FLAG);

  INGwSipConnMinorState  connMinorState = aSipConnection->getMinorState();
  int                  connMajorState = (int)aSipConnection->getMajorState();
  Sdf_st_error         sdferror;

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgNotifyHandler::stackCallbackRequest: "
										"connminorstate <%d>, connmajorstate <%d>", 
										connMinorState, connMajorState);

  aSipConnection->setLastMethod(INGW_SIP_METHOD_TYPE_NOTIFY);

  // If the connection is not in created state, we cannot process this notify.
  if(connMajorState != CONNECTION_CREATED)
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgNotifyHandler::stackCallbackRequest: "
											"notify request received in wrong connection state");
    return Sdf_co_success;
  }

  // Process via header to get GW port
  INGwSpSipUtil::processIncomingViaHeader(aSipConnection, *ppCallObj);

////////////////////////////////////////////////////////////////////////////////
//PANKAJ - we need to change terminateTransaction to acco notify termination
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
												"INGwSpMsgNotifyHandler::stackCallbackRequest:"
												" Error getting message body from the notify message.");
      CLEANUP_SEQUENCE(487)
      return Sdf_co_success;
    }

    if(!strlen(infoData->getBodyType()))
    {
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, WARNING_FLAG, imERR_NONE, 
												"INGwSpMsgNotifyHandler::stackCallbackRequest: "
												"Error getting message body from the notify message.");
      CLEANUP_SEQUENCE(487)
      return Sdf_co_success;
    }

    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, VERBOSE_FLAG, imERR_NONE, 
											"INGwSpMsgNotifyHandler::stackCallbackRequest: Found msgbody type <%s>", 
											infoData->getBodyType());

    // If the msgbody type corresponds to the TCAP application , pass on the message body
    // to the call controller
    if(!strcasecmp(INGW_MSG_BODYTYPE_TCAP, infoData->getBodyType()))
    {
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgNotifyHandler::stackCallbackRequest: "
												" Passing NOTIFY sdp to SIP Call controller seqNum<%d>",mSeqNum);

			// INGW Handling 
			// PANKAJ NEED TO test here

			INGwSpSipCallController* callCtlr =
														INGwSpSipProvider::getInstance().getCallController();
      // Ask call controller to handle it
			callCtlr->processOutboundMsg(aSipConnection, 
																	 infoData->getBody(), 
																	 infoData->getBodyLength(),mSeqNum);

    }
    else 
    {
      // Terminate the NOTIFY with a 404 response
			// Not supporting any other body type
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
												"INGwSpMsgNotifyHandler::stackCallbackRequest: "
												"terminating NOTIFY with a 400 .");
      CLEANUP_SEQUENCE(400);
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

Sdf_ty_retVal INGwSpMsgNotifyHandler::stackCallbackResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   int                      aRespCode        ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             ,
                   Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
  MARK_BLOCK("INGwSpMsgNotifyHandler::stackCallbackResponse", TRACE_FLAG);
  INGwSipConnMinorState  connMinorState = aSipConnection->getMinorState();
  int                  connMajorState = (int)aSipConnection->getMajorState();
  INGwSipMethodType         methodType     = aSipConnection->getLastMethod();

  // If this is a 1xx or 3xx response, do not handle it.
  if((aRespCode/100 == 1) || (aRespCode/100 == 3))
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, WARNING_FLAG, imERR_NONE, 
											"stackCallbackResponse: "
											"Got 1xx or 3xx response for NOTIFY.  Not handling.");
    return Sdf_co_success;
  }

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"stackCallbackResponse: connminstate <%d>, "
										"connmajorstate <%d>, methodtype <%d>, respcode <%d>, lastop <%d>", 
										connMinorState, connMajorState, aMethodType, aRespCode, mLastop);
  if((aRespCode/100) == 2) {
    //got the success response so clear the data at standBy

#ifdef INGW_TRACE_CALL_THREAD
      logger.logINGwMsg(false, VERBOSE_FLAG, 0,"+THREAD+ Sip NotifyHandler 200 OK");
#endif

    /*if(INGwIfrPrParamRepository::getInstance().getPeerStatus())*/{
      INGwSpSipConnTcapTransInfo lTcapTransInfo;
      aSipConnection->getTcapTransInfo(lTcapTransInfo);
      logger.logINGwMsg(false, VERBOSE_FLAG,0,"dialogueId %d suId "
      "%d spId %d seqNum %d", lTcapTransInfo.m_stackDlgId, 
      lTcapTransInfo.m_suId, lTcapTransInfo.m_spId, lTcapTransInfo.m_seqNum);

      INGwIwfBaseIface *lIface = INGwIwfProvider::getInstance().getInterface(); 
      INGwIwfIface* l_IwfIface = static_cast<INGwIwfIface*>(lIface);

      //Yogesh to be tested for 200 Ok's may be lost in this synchronous design
      //second approach post in the FtMessenger's queue directly from here
      //change this flow 
#ifdef INGW_TRACE_CALL_THREAD
      logger.logINGwMsg(false, VERBOSE_FLAG, 0,"+THREAD+ Sip NotifyHandler 200 OK <%d>",
                                               lTcapTransInfo.m_stackDlgId);
#endif
        l_IwfIface->processSeqNumAck(INGW_CLEAN_INBOUND_MESSAGE,
                                     lTcapTransInfo.m_stackDlgId,
                                     lTcapTransInfo.m_seqNum, 
                                     lTcapTransInfo.m_isDialogueComplete);
    }
  }

  // HACK so that stack will not close this FD
  if(pOverlapTransInfo)
  {
    pOverlapTransInfo->dSockfd = -1;
  } 
  // If the last operation is not notify or if the connection minor state is not
  // msg transmitted, this response cannot be processed.
  
  if( methodType != INGW_SIP_METHOD_TYPE_NOTIFY)
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"stackCallbackResponse: "
											"Last method is not notify.  "
											"This response cannot be processed.");
    return Sdf_co_success;
  }


  // The last operation should be either a passthrough notify or a media server
  // info.  Handle both cases here.
  LastOperation tmpLastop = mLastop;
  mLastop = LASTOP_UNKNOWN;
  if(methodType == INGW_SIP_METHOD_TYPE_NOTIFY)
  {
  }
  else
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"stackCallbackResponse: "
											"Response received when lastop is not valid <%d>", tmpLastop);
  }

  // Update the connection state.
  aSipConnection->setMinorState(CONN_MINSTATE_IDLE);
  aSipConnection->setLastMethod(INGW_METHOD_TYPE_NONE);

  if(aRespCode >= 400 && aRespCode != 499)
	{
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
										"stackCallbackResponse: Err resp rcvd for Notify Req" 
										" respcode<%d>, callid<%s>",
										aRespCode, aSipConnection->getCallId().c_str());

    int lCurVal = 0;
	  INGwIfrSmStatMgr::instance().increment(
	  									INGwSpSipProvider::miStatParamId_NumNotifySentRejected, 
											lCurVal, 1);

		INGwSpSipCallController* callCtlr =
														INGwSpSipProvider::getInstance().getCallController();

    //Ask call controller to handle it
		callCtlr->handleInboundMsgFailure(aRespCode, aSipConnection);

  }

  if( ! (aRespCode >= 400 && aRespCode != 499))
  {
    if(!aSipConnection->isDead())
	  		aSipConnection->getCall().removeConnection(aSipConnection->getSelfConnectionId());

	  INGwSpSipUtil::releaseConnection(aSipConnection);
  }

  return Sdf_co_success;
} // stackCallbackResponse

Sdf_ty_retVal INGwSpMsgNotifyHandler::stackCallbackAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             )
{
  MARK_BLOCK("INGwSpMsgNotifyHandler::stackCallbackAck", TRACE_FLAG);
  return Sdf_co_fail;
}

void INGwSpMsgNotifyHandler::indicateTimeout
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSipTimerType::TimerType aType           ,
                   unsigned int              aTimerid        )
{
  MARK_BLOCK("INGwSpMsgNotifyHandler::indicateTimeout", TRACE_FLAG);
  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"indicateTimeout: timerid <%u>, timertype <%d>", 
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
                                   INGW_SIP_METHOD_TYPE_NOTIFY,
                                   408,
                                   &hssCallObj,
                                   NULL,
                                   &sdferror,
                                   pOverlapTransInfo);

    if(status != Sdf_co_success)
		{
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
												"indicateTimeout: "
												"Error calling response callback on NOTIFY handler");
    }
    else
    {
      // Reset the client transaction.
      status = sdf_ivk_uaClearRegularTransaction(hssCallObj, "NOTIFY", 
                                                 pOverlapTransInfo);

      if(status != Sdf_co_success)
      {
        INGwSpSipUtil::checkError(status, sdferror);
          logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
														"indicateTimeout: error clearing the NOTIFY transaction");
      }

      if(!aSipConnection->isDead())
         aSipConnection->getCall().removeConnection(aSipConnection->getSelfConnectionId());

      INGwSpSipUtil::releaseConnection(aSipConnection);
    }
  }
  else if(aType == INGwSipTimerType::SIPIVR_TIMER)
  {

    // If this timer is not the one we have, ignore it.
    if(muiIvrTimeout != aTimerid)
    {
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
												"indicateTimeout: timerid <%u> is different "
												"from the one in stock <%u>.  Ignoring the received timer", 
												muiIvrTimeout, aTimerid);
    }
    else
    {
       muiIvrTimeout = 0;
      // Inform the msobject of the timeout.
			//PANKAJ - handling
#if 0
      SIPIvrMSObject* sipmsobj = 
							 dynamic_cast<SIPIvrMSObject*>(aSipConnection->getMSObject());
      if(!sipmsobj)
        logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
													"INGwSpMsgNotifyHandler::indicateTimeout: "
													"Error getting the ms object from connection");
      else
        sipmsobj->handleResponse("TIMEOUT", 7);
#endif
    }
  }
  else
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
		                  "INGwSpMsgNotifyHandler::indicateTimeout: "
											"Unrecognized timer type <%d>", aType);
} // end of indicateTimeout

int INGwSpMsgNotifyHandler::mSendRequest
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          )
{

  MARK_BLOCK("INGwSpMsgNotifyHandler::mSendRequest", TRACE_FLAG);
  INGwSipConnMinorState  connMinorState = aSipConnection->getMinorState();
  int                  connMajorState = (int)aSipConnection->getMajorState();
  INGwSipMethodType         methodType     = aSipConnection->getLastMethod();
  Sdf_ty_retVal        status         = Sdf_co_fail;
  Sdf_st_callObject*   hssCallObj     = aSipConnection->getHssCallObject();
  Sdf_st_error         sdferror;

	SipAddrSpec* lContactAddSpec = aSipConnection->getContactAddSpec();
	SipAddrSpec* lFromAddSpec = aSipConnection->getFromAddSpec();

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgNotifyHandler::mSendRequest: "
										"connminstate <%d>, connmajorstate <%d>, methodtype <%d>",
										connMinorState, connMajorState, aMethodType);


  // If the connection is not connected or if the minor state is not idle, or
  // if the last method is not NONE, then this notify cannot be sent out.

  if((connMinorState != CONN_MINSTATE_IDLE) ||
     (connMajorState != CONNECTION_CREATED))
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgNotifyHandler::mSendRequest: "
											"The send request has been received in wrong connection state.  "
											"Request cannot be sent out.");
    return -1;
  }

  Sdf_st_overlapTransInfo *pOverlapTransInfo = Sdf_co_null;

  status = sdf_ivk_uaStartRegularTransaction
                  (hssCallObj, &pOverlapTransInfo, "NOTIFY", &sdferror);
  if(status != Sdf_co_success)
  {
    INGwSpSipUtil::checkError(status, sdferror);
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgNotifyHandler::mSendRequest: "
											"error creating a new NOTIFY transaction");
    return -1;
  }
	else
	{
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
											"INGwSpMsgNotifyHandler::mSendRequest: "
											"Successfully started a new NOTIFY transaction");
	}
#define CLEANUP \
    sdf_ivk_uaClearRegularTransaction(hssCallObj, "NOTIFY", pOverlapTransInfo);

  string l_strHost = string(aSipConnection->getGwIPAddress());
	int l_port = aSipConnection->getGwPort();
	//int l_port = aSipConnection->getActiveEPPort();

  if(status == Sdf_co_success)
  {
    string lstrUser = "tcap@";
    lstrUser += l_strHost;
    status = sdf_ivk_uaSetTo(hssCallObj, "INGW", (char *)lstrUser.c_str(),
                             l_port, (char *)"sip", &sdferror);
    if(status != Sdf_co_success)
    {
      INGwSpSipUtil::checkError(status, sdferror);
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
                      "INGwSpMsgNotifyHandler::mSendRequest: "
                      "error setting To hdr in NOTIFY transaction");
      CLEANUP
      return -1;
    }
    else
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
                      "INGwSpMsgNotifyHandler::mSendRequest: "
                      "successfully set TO hdr in NOTIFY transaction");
  }

  bool lIsUseContact = INGwSpSipProviderConfig::isUseContactForReqUri();

  if(lIsUseContact)
	{
  if(status = Sdf_co_success)
  {
		if(lContactAddSpec == NULL)
		{
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
                      "INGwSpMsgNotifyHandler::mSendRequest: "
                      "ContactAddSpec is NULL . Not setting from Orig");
		}
		else
		{
			HSS_LOCKEDINCREF(lContactAddSpec->dRefCount);
      status = sdf_ivk_uaChangeRequestURI
        (hssCallObj, lContactAddSpec, &sdferror);

      if(status != Sdf_co_success)
      {
        INGwSpSipUtil::checkError(status, sdferror);
        logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
                       "INGwSpMsgNotifyHandler::mSendRequest: "
                       "error  changing RequestURI in NOTIFY transaction");
        CLEANUP
        return -1;
      }
      else
        logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
                        "INGwSpMsgNotifyHandler::mSendRequest: "
                        "successfully changed RequestURI in NOTIFY transaction");
    }
    } // end of if status = Sdf_co_success
  } //end of if lIsUseContact
	else
	{
    if(status = Sdf_co_success)
    {
	  	if(lFromAddSpec == NULL)
	  	{
        logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
                        "INGwSpMsgNotifyHandler::mSendRequest: "
                        "FromAddSpec is NULL . Not setting from Orig");
  		}
  		else
  		{
  			HSS_LOCKEDINCREF(lFromAddSpec->dRefCount);
        status = sdf_ivk_uaChangeRequestURI
          (hssCallObj, lFromAddSpec, &sdferror);
  
        if(status != Sdf_co_success)
        {
          INGwSpSipUtil::checkError(status, sdferror);
          logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
                         "INGwSpMsgNotifyHandler::mSendRequest: "
                         "error  changing RequestURI in NOTIFY transaction");
          CLEANUP
          return -1;
        }
        else
          logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
                          "INGwSpMsgNotifyHandler::mSendRequest: "
                          "successfully changed RequestURI in NOTIFY transaction");
      }
    } // end of if status = Sdf_co_success
	} //end of else lIsUseContact

  if(status = Sdf_co_success)
  {
    std::string lTransportStr;
    if(INGwSpSipProviderConfig::isTransportUdp() )
    {
      lTransportStr = "UDP";
    }
    else
    {
      lTransportStr = "TCP";
    }
    status = sdf_ivk_uaSetDestTransportInTransaction
      (Sdf_co_null, pOverlapTransInfo, (char *)l_strHost.c_str(),
       l_port, (char*) lTransportStr.c_str(), &sdferror);

    if(status != Sdf_co_success)
    {
      INGwSpSipUtil::checkError(status, sdferror);
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
                     "INGwSpMsgNotifyHandler::mSendRequest: "
                     "error setting dest transport in NOTIFY transaction");
      CLEANUP
      return -1;
    }
    else
    {
      if(! INGwSpSipProviderConfig::isTransportUdp() )
      {

         pOverlapTransInfo->dSockfd = 
                 INGwSpTcpConnMgr::instance().getSelfSocketId();
      }

      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
                      "INGwSpMsgNotifyHandler::mSendRequest: "
                      "successfully set dest transport in NOTIFY transaction");
  }
  }

/*
	//Add Event and subscription header
	std::string lEventHeader = "Event: tcap-event";
	std::string lSubscriptionHdr = "Subscription-State: active";
	en_HeaderType lHdrTypeEvt = SipHdrTypeEvent;
	en_HeaderType lHdrTypeSubs = SipHdrTypeSubscriptionState;
//	Sdf_ty_transactionType lTrxType = Sdf_en_overlapTransaction;
	Sdf_ty_transactionType lTrxType = Sdf_en_uacTransaction;
	Sdf_st_error lHdrErr;

	if(-1 == INGwSpSipUtil::createAndAddHeader(hssCallObj, 
																		lTrxType, lHdrTypeEvt, 
																		(Sdf_ty_s8bit*)lEventHeader.c_str(), &lHdrErr))
  {
    INGwSpSipUtil::checkError(Sdf_co_fail, lHdrErr);
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
                     "INGwSpMsgNotifyHandler::mSendRequest: Failed to set <Event> header");
	}

	if(-1 == INGwSpSipUtil::createAndAddHeader(hssCallObj, 
																		lTrxType, lHdrTypeSubs, 
																		(Sdf_ty_s8bit*)lSubscriptionHdr.c_str(), &lHdrErr))
  {
    INGwSpSipUtil::checkError(Sdf_co_fail, lHdrErr);
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
                     "INGwSpMsgNotifyHandler::mSendRequest: Failed to set <Subscription-State> header");
	}
*/
  // Form an NOTIFY request
  if(status == Sdf_co_success)
  {
/*
    status = sdf_ivk_uaFormRequest((Sdf_ty_s8bit *)"NOTIFY",
                                 hssCallObj,
                                 pOverlapTransInfo,
                                 &sdferror);
*/
    //PANKAJ
    //sdf_ivk_uaMakeInfo call this api
    // which in turn calls sdf_ivk_uaFormRequest
    // so if above call does not work we will try this
    status = sdf_ivk_uaMakeTransaction((Sdf_ty_s8bit *)"NOTIFY",
                                 hssCallObj,
                                 pOverlapTransInfo,
                                 &sdferror);

    if(status != Sdf_co_success)
    {
      INGwSpSipUtil::checkError(status, sdferror);
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
                     "INGwSpMsgNotifyHandler::mSendRequest: error making a new NOTIFY request");
      CLEANUP
      return -1;
    }
    else
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
                     "INGwSpMsgNotifyHandler::mSendRequest: successfully made an NOTIFY request");
  }

  // If the GwData has some msgbody, then set it into the message.

  char lDialogueIdHeader[32];
  char lBillingIdHeader[32];
  char lSeqNumIdHeader[24];
  if(aGwData && (aGwData->getBody()))
  {
    char *msgBody  = (char *)(((INGwSpData*)aGwData)->getBody()    );
    char *bodyType = (char *)(((INGwSpData*)aGwData)->getBodyType());
      
    int   msgbodylen = ((INGwSpData *)aGwData)->getBodyLength();

    /*Yogesh extracting dialogue Id from byte array*/
    /*DLGTYPE LEN VAL DLGID VAL array Index 4,5,6,7*/
    /*It can be optimized by passing dlgId directly*/ 
    if(NULL != msgBody)
    {
#ifdef INGW_EXTRA_LOGS
      logger.logINGwMsg(false,VERBOSE_FLAG,0,
                      "+REM+ INGwSpMsgNotifyHandler::mSendRequest %s",msgBody);
#endif 

      //int  lDialogueId = getDialogueId((unsigned char*)msgBody);
      sprintf(lDialogueIdHeader,"%s: %d","Dialogue-id",((INGwSpData*)aGwData)->getDialogueId()); 
      sprintf(lSeqNumIdHeader,"%s: %d","TC-Seq",((INGwSpData*)aGwData)->getSeqNum()); 
      sprintf(lBillingIdHeader,"%s: %d","TC-Corr-id",((INGwSpData*)aGwData)->getBillingId()); 
    }

    status = INGwSpSipUtil::copyMsgBodyIntoSipMessage
                       (pOverlapTransInfo->pSipMsg,
                        msgBody, msgbodylen);

    status = INGwSpSipUtil::setContentTypeInMessage
                      (pOverlapTransInfo->pSipMsg, bodyType, msgbodylen);

    if(status != Sdf_co_success)
    {
      INGwSpSipUtil::checkError(status, sdferror);
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
                      "INGwSpMsgNotifyHandler::mSendRequest: error adding SDP in NOTIFY msg");
      CLEANUP
      return -1;
    }
    else
    {
      logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
		  									"INGwSpMsgNotifyHandler::mSendRequest: "
		  									"Message body copied in the NOTIFY message");
    }
  }

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										 "INGwSpMsgNotifyHandler::mSendRequest: Sending NOTIFY to peer");

  SipError siperror;

#ifdef RES_PRIORITY_HDR
  if(sip_insertHeaderFromStringAtIndex(pOverlapTransInfo->pSipMsg,
                                       SipHdrTypeUnknown,
                                       (SIP_S8bit*)"Resource-Priority: ets.0", 0, &siperror) != SipSuccess)
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, 0,
                    "Error in adding Resource-Priority header");
  }
#endif
  
  if(sip_insertHeaderFromStringAtIndex(pOverlapTransInfo->pSipMsg,
                                       SipHdrTypeEvent,
                                       (SIP_S8bit*)"Event: tcap-event", 0, &siperror) != SipSuccess)
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, 0,
                    "Error in adding Event header");
  }
  
  if(sip_insertHeaderFromStringAtIndex(pOverlapTransInfo->pSipMsg,
                                       SipHdrTypeSubscriptionState,
                                       (SIP_S8bit*)"Subscription-State: active", 0, &siperror) != SipSuccess)
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, 0,
                    "Error in adding Subscription-State header");
  }
  
  if(sip_insertHeaderFromStringAtIndex(pOverlapTransInfo->pSipMsg,
                                       SipHdrTypeUnknown,
                                       (SIP_S8bit*)lDialogueIdHeader, 0, &siperror) != SipSuccess)
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, 0,
                    "Error in adding Dialogue-id header yogeshTest");
  }

  if(sip_insertHeaderFromStringAtIndex(pOverlapTransInfo->pSipMsg,
                                       SipHdrTypeUnknown,
                                       (SIP_S8bit*)lSeqNumIdHeader, 0, &siperror) != SipSuccess)
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, 0,
                    "Error in adding TC-Seq header yogeshTest");
  }

  if(sip_insertHeaderFromStringAtIndex(pOverlapTransInfo->pSipMsg,
                                       SipHdrTypeUnknown,
                                       (SIP_S8bit*)lBillingIdHeader, 0, &siperror) != SipSuccess)
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, 0,
                    "Error in adding Billing-Id header");
  }

#ifdef INGW_EXTRA_LOGS
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"+rem+ before sendCallToPeer +conn+ <%x>",
                    INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);
#endif

  status = INGwSpSipUtil::sendCallToPeer(hssCallObj,
                                     pOverlapTransInfo->pSipMsg,
                                     INGW_SIP_METHOD_TYPE_NOTIFY,
                                     &sdferror,
                                     aSipConnection);

  
  logger.logINGwMsg(false,TRACE_FLAG,0,"did<%s>TCQ<%s>",lDialogueIdHeader,lSeqNumIdHeader);
#ifdef INGW_EXTRA_LOGS
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"+rem+ after sendCallToPeer +conn+ <%x>",
                    INGwSpSipProvider::getInstance().getThreadSpecificSipData().conn);
#endif

  status = sdf_ivk_uaEndRegularTransaction
                 (hssCallObj, pOverlapTransInfo, "NOTIFY", &sdferror);

  int lCurVal = 0;
	INGwIfrSmStatMgr::instance().increment(
										INGwSpSipProvider::miStatParamId_NumNotifySent, lCurVal, 1);

  // Update the connection state.
  if(status == Sdf_co_success)
  {
    aSipConnection->setLastMethod(INGW_SIP_METHOD_TYPE_NOTIFY);
  }
  
  if(status == Sdf_co_success) return 0;
  else                         return 1;
} // end of mSendRequest

int INGwSpMsgNotifyHandler::mSendResponse
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          ,
                   int                      aCode)
{
  MARK_BLOCK("INGwSpMsgNotifyHandler::mSendResponse", TRACE_FLAG);
  INGwSipConnMinorState connMinorState = aSipConnection->getMinorState();
  int                  connMajorState = (int)aSipConnection->getMajorState();
  INGwSipMethodType    methodType     = aSipConnection->getLastMethod();
  Sdf_st_callObject*   hssCallObj     = aSipConnection->getHssCallObject();
  Sdf_ty_retVal        status         = Sdf_co_fail;
  Sdf_st_error         sdferror;

  logger.logINGwMsg(aSipConnection->getCall().mLogFlag, TRACE_FLAG, imERR_NONE, 
										"INGwSpMsgNotifyHandler::mSendResponse: "
										"connminstate <%d>, connmajorstate <%d>, methodtype <%d>, ", 
										connMinorState, connMajorState, aMethodType);

  // If the connection is not connected or if the minor state is not msgrecd, or
  // if the last method is not NOTIFY, then this response cannot be sent out.

  if((methodType     != INGW_SIP_METHOD_TYPE_NOTIFY) ||
     (connMajorState != CONNECTION_CREATED))
  {
    logger.logINGwMsg(aSipConnection->getCall().mLogFlag, ERROR_FLAG, imERR_NONE, 
											"INGwSpMsgNotifyHandler::mSendResponse: The send response has been "
											"called in wrong connection state.  Response cannot be sent out.");
    return -1;
  }

  // Form a response
  Sdf_st_overlapTransInfo *pOverlapTransInfo =
              INGwSpSipUtil::getLastOverlapTransInfo(hssCallObj);
  status = sdf_ivk_uaFormResponse(aCode, "NOTIFY" , hssCallObj,
                                  pOverlapTransInfo, Sdf_co_false, &sdferror);

  status = INGwSpSipUtil::sendCallToPeer(hssCallObj,
                                     pOverlapTransInfo->pSipMsg,
                                     INGW_SIP_METHOD_TYPE_NOTIFY,
                                     &sdferror,
                                     aSipConnection);
  if(aCode >=400 )
	{
    int lCurVal = 0;
	  INGwIfrSmStatMgr::instance().increment(
	  									INGwSpSipProvider::miStatParamId_NumNotifyRejected, lCurVal, 1);
  }

  // Update the connection state.
  aSipConnection->setLastMethod(INGW_METHOD_TYPE_NONE);
  aSipConnection->setMinorState(CONN_MINSTATE_IDLE);
  mLastop = LASTOP_UNKNOWN;

  
  if(status == Sdf_co_success) return 0;
  else                         return -1;
} // end of mSendResponse

int INGwSpMsgNotifyHandler::mSendAck
                  (INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   INGwSpData                *aGwData          )
{
  MARK_BLOCK("INGwSpMsgNotifyHandler::mSendAck", TRACE_FLAG)
  return -1;
}

void INGwSpMsgNotifyHandler::setLastOperation(LastOperation aLastop)
{
  mLastop = aLastop;
}

std::string INGwSpMsgNotifyHandler::toLog()const
{
   std::string ret = "\nnotify handler\n";

   char local[500];

   sprintf(local, "LastOperation:[%d] \n", 
           mLastop);
   ret += local;
   return ret;
}

int INGwSpMsgNotifyHandler :: getDialogueId(unsigned char* pMsgBody)
{
  int lDlgId = 0;
  int lLenOffset = 0;
  if(0x00 != (pMsgBody[1] & 0x80)){
    lLenOffset = (pMsgBody[1]) - 128;
  }

  lDlgId = (lDlgId | pMsgBody[4 + lLenOffset]) << 8;
  lDlgId = (lDlgId | pMsgBody[5 + lLenOffset]) << 8;
  lDlgId = (lDlgId | pMsgBody[6 + lLenOffset]) << 8;
  lDlgId = lDlgId | pMsgBody[7 + lLenOffset];
  return lDlgId; 
}


