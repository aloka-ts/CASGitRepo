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
//     File:     INGwSpMsgInviteHandler.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   08/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipMsgHandler");

#include <INGwSipMsgHandler/INGwSpMsgInviteHandler.h>

#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipContext.h>
#include <INGwSipProvider/INGwSpSipProviderConfig.h>
#include <INGwSipProvider/INGwSpSipHeaderPolicy.h>
#include <INGwSipProvider/INGwSpThreadSpecificSipData.h>

#include <INGwSipMsgHandler/INGwSpMsgSipSpecificAttr.h>

#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwInfraUtil/INGwIfrUtlStrUtil.h>

#include <INGwSipProvider/INGwSpSipProviderConfig.h>
#include <INGwSipProvider/INGwSpTcpConnMgr.h>

#if 0
#include <Gw/BpGwCall.h>
#include <ccm/INGwSpSipCallTracer.h>
#include <ccm/BpGenEventRetValue.h>
#endif

#include <INGwSipProvider/INGwSpData.h>
#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwInfraManager/INGwIfrMgrWorkerThread.h>
#include <INGwSipProvider/INGwSpDataFactory.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <iterator>
#include <strings.h>
#include <INGwInfraMsrMgr/MsrMgr.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>

using namespace std;
using namespace RSI_NSP_SIP;

const char *DEFAULT_ADDRESS = "X_NO";

void INGwSpMsgInviteHandler::changeRequestLine(Sdf_st_callObject* aCallObj, 
                                           const char* aUser, const char* aHost,
                                           const int aPort)
{
   Sdf_st_error sdferror;

   if(!aCallObj || !aUser || !aHost || !aPort)
   {
      logger.logINGwMsg(false, WARNING_FLAG, 0, 
                      "invalid parameters: callobj<%p>, user<%p>, host<%p>, "
                      "port<%d>", aCallObj, aUser, aHost, aPort);
      return;
   }

   logger.logINGwMsg(false, TRACE_FLAG, 0, "Change ReqLine User[%s] Host[%s] "
                                         "port[%d]", aUser, aHost, aPort);

   Sdf_st_commonInfo *pCommonInfo = Sdf_co_null;

   if(sdf_ivk_uaGetCommonInfoFromCallObject(aCallObj, &pCommonInfo, &sdferror) 
      == Sdf_co_fail)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Error getting common info from "
                                            "the call object");
      return;
   }

   if(pCommonInfo->pRequestUri != Sdf_co_null)
   {
      SipError siperror;
      SipUrl* psipurl  = 0;

      if((sip_getUrlFromAddrSpec(pCommonInfo->pRequestUri, &psipurl, &siperror) 
          != SipSuccess) || (!psipurl))
      {
         logger.logINGwMsg(false, WARNING_FLAG, 0, "could not get uri from "
                                                 "reqline addr spec");
      }
      else
      {
         char* existingUser = NULL;
         char* existingHost = NULL;
         char* newUser = NULL;
         char* newHost = NULL;
         unsigned short newPort = 0;
         unsigned short existingPort = 0;

         newUser = (char *)fast_memget(0, strlen(aUser) + 1, &siperror);
         newHost = (char *)fast_memget(0, strlen(aHost) + 1, &siperror);

         strcpy(newUser, aUser);
         strcpy(newHost, aHost);

         logger.logINGwMsg(false, TRACE_FLAG, 0, 
													 "Before replace Host [%s] Port [%d]", 
													 psipurl->pHost, *(psipurl->dPort));

         sip_setUserInUrl(psipurl, newUser, &siperror);
         sip_setHostInUrl(psipurl, newHost, &siperror);
         sip_setPortInUrl(psipurl, aPort  , &siperror);

         logger.logINGwMsg(false, TRACE_FLAG, 0, 
													 "After replace Host [%s] Port [%d]", 
													 psipurl->pHost, *(psipurl->dPort));

         sip_freeSipUrl(psipurl);
      }
   }
   else
   {
      logger.logINGwMsg(false, TRACE_FLAG, 0, "NULL ReqURI");
   }

   sdf_ivk_uaFreeCommonInfo(pCommonInfo);
} 

INGwSpMsgInviteHandler::INGwSpMsgInviteHandler()
{
   m_oInvReqLineUrl = 0;
   m_overlapTxInfo  = 0;

   m_oFromTel = NULL;
   m_oToTel = NULL;
   m_oReqTel = NULL;

   sipAttr = NULL;

	 mOfferAnswerState = OA_NONE;
}

INGwSpMsgInviteHandler::~INGwSpMsgInviteHandler()
{
   if(m_oInvReqLineUrl)
   {
      sip_freeSipUrl(m_oInvReqLineUrl);
      m_oInvReqLineUrl = NULL;
   }

   if(m_oFromTel)
   {
      sip_freeTelUrl(m_oFromTel);
      m_oFromTel = NULL;
   }

   if(m_oToTel)
   {
      sip_freeTelUrl(m_oToTel);
      m_oToTel = NULL;
   }

   if(m_oReqTel)
   {
      sip_freeTelUrl(m_oReqTel);
      m_oReqTel = NULL;
   }

   if(sipAttr)
   {
      sipAttr->releaseRef();
      sipAttr = NULL;
   }
}

void INGwSpMsgInviteHandler::reset(void)
{
   mInviteStateContext.reset();

   if(m_oInvReqLineUrl)
   {
      sip_freeSipUrl(m_oInvReqLineUrl);
      m_oInvReqLineUrl = NULL;
   }

   if(m_oFromTel)
   {
      sip_freeTelUrl(m_oFromTel);
      m_oFromTel = NULL;
   }

   if(m_oToTel)
   {
      sip_freeTelUrl(m_oToTel);
      m_oToTel = NULL;
   }

   if(m_oReqTel)
   {
      sip_freeTelUrl(m_oReqTel);
      m_oReqTel = NULL;
   }

   if(sipAttr)
   {
      sipAttr->releaseRef();
      sipAttr = NULL;
   }

   m_overlapTxInfo  = 0;

	 mOfferAnswerState = OA_NONE;
}

const INGwSpMsgInviteHandler & INGwSpMsgInviteHandler::operator = (
                                               const INGwSpMsgInviteHandler &inData)
{
   reset();

   m_overlapTxInfo = inData.m_overlapTxInfo;
   mInviteStateContext = inData.mInviteStateContext;

   if(inData.m_oInvReqLineUrl)
   {
      m_oInvReqLineUrl = inData.m_oInvReqLineUrl;
      HSS_INCREF(m_oInvReqLineUrl->dRefCount);
   }

   if(inData.m_oFromTel)
   {
      m_oFromTel = inData.m_oFromTel;
      HSS_INCREF(m_oFromTel->dRefCount);
   }

   if(inData.m_oToTel)
   {
      m_oToTel = inData.m_oToTel;
      HSS_INCREF(m_oToTel->dRefCount);
   }

   if(inData.m_oReqTel)
   {
      m_oReqTel = inData.m_oReqTel;
      HSS_INCREF(m_oReqTel->dRefCount);
   }

   if(inData.sipAttr)
   {
      sipAttr = inData.sipAttr;
      sipAttr->getRef();
   }

   return *this;
}

void INGwSpMsgInviteHandler::setSipSpecificAttr(INGwSpMsgSipSpecificAttr *inSipAttr)
{
   if(sipAttr)
   {
      sipAttr->releaseRef();
      sipAttr = NULL;
   }

   if(inSipAttr)
   {
      sipAttr = inSipAttr;
      sipAttr->getRef();
   }
}

////////////////////////////////////////////////////////////////////////////////
// Method: stackCallbackRequest
// Description: This method is called when the stack receives an INVITE message.
//              In this method, various addresses are extracted from the HSS
//              call leg and set in the connection.
//              Message body (if present) is extracted and initial ip data is
//              created and stored in the connection.
//              A 100 Trying provisional response is sent for the INVITE.
//              The ADDRESS_ANALYZE event is generated.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal INGwSpMsgInviteHandler::stackCallbackRequest(
    INGwSpSipConnection *aSipConnection, INGwSipMethodType aMethodType, 
    Sdf_st_callObject **ppCallObj, Sdf_st_eventContext *pEventContext, 
    Sdf_st_error *pErr, Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
   LogINGwTrace(aSipConnection->mLogFlag, 0, "IN stackCallbackRequest");

   mInviteStateContext.callSetupStartTime = gethrtime();
   logger.logINGwMsg(aSipConnection->mLogFlag, VERBOSE_FLAG, 0, 
                   "Call setup time <%lld>", 
                   mInviteStateContext.callSetupStartTime);

   Sdf_ty_retVal        retval         = Sdf_co_fail;
   INGwSipConnMinorState  connMinorState = aSipConnection->getMinorState();
   int                  connMajorState = (int)aSipConnection->getMajorState();

   logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, 
                   "stackCallbackRequest: connminorstate <%d>, connmajorstate "
                   "<%d>", connMinorState, connMajorState);

   if((*ppCallObj)->pCommonInfo->pCallid)
   {
      logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, 
                      "stackCallbackRequet: callid <%s>", 
                      (*ppCallObj)->pCommonInfo->pCallid);
   }
  
   // Check whether the connection is in created state.  This is the only time
   // we actually expect to receive an INVITE.  Otherwise simply return.  This
   // case will not happen unless the stack goes for a toss.  Only re-invite can
   // come mid call or the initial INVITE retransmission can happen.  Both of
   // these should not come to this method.

   if((connMinorState != CONN_MINSTATE_IDLE) ||
      (connMajorState != CONNECTION_CREATED))
   {
      logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                      "iNVite received in wrong connection state: <%d,%d>\n", 
                      connMajorState, connMinorState);
      pErr->errCode = Sdf_en_callStateError;
      sprintf(pErr->ErrMsg, "Invite rcvd in wrong connection state: <%d,%d>", 
              connMajorState, connMinorState);
      LogINGwTrace(false, 0, "OUT stackCallbackRequest");
      return Sdf_co_fail;
   }

   // Extract the various addresses from the INVITE and store them in the
   // connection.

   INGwSpAddress &origAddrStr = 
             aSipConnection->getAddress(INGwSpSipConnection::ORIGINATING_ADDRESS);
   INGwSpAddress &termAddrStr = 
             aSipConnection->getAddress(INGwSpSipConnection::TARGET_ADDRESS);
   INGwSpAddress &dialAddrStr = 
             aSipConnection->getAddress(INGwSpSipConnection::DIALED_ADDRESS);

   SipList &remotePartyIDHdrList = 
     (*ppCallObj)->pUasTransaction->pSipMsg->pGeneralHdr->slDcsRemotePartyIdHdr;

   char useraddr[100];
   char larDname[100];


   _parseFromHdr(aSipConnection, (*ppCallObj)->pCommonInfo->pFrom,
                 origAddrStr);

   if(remotePartyIDHdrList.size != 0)
   {
      //Checking only the first header.
      SipHeader remotePartyHdr;
      remotePartyHdr.dType   = SipHdrTypeDcsRemotePartyId;
      remotePartyHdr.pHeader = remotePartyIDHdrList.head->pData;

      INGwSpSipUtil::processRemotePartyID(aSipConnection, origAddrStr, 
                                      remotePartyHdr);
   }

   _parseToHdr(aSipConnection, (*ppCallObj)->pCommonInfo->pTo, dialAddrStr);

   SipReqLine  *pReqLine       = NULL;
   SipAddrSpec *termAddrSpec   = NULL;

   SipError siperr;
   SipBool sipstatus;

   sipstatus = sip_getReqLine((*ppCallObj)->pUasTransaction->pSipMsg,
                              &pReqLine, &siperr);

   if(pReqLine && (SipSuccess == sipstatus))
   {
     sipstatus = sip_getAddrSpecFromReqLine(pReqLine, &termAddrSpec, &siperr);
     sip_freeSipReqLine(pReqLine);
   }
   else
   {
      logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                      "Error getting reqline: pReqLine <%p>, sipstatus <%d>", 
                      pReqLine, sipstatus);
   }

   if(termAddrSpec && (SipSuccess == sipstatus))
   {
      _parseReqURI(aSipConnection, termAddrSpec, termAddrStr);
      sip_freeSipAddrSpec(termAddrSpec);
   }
   else
   {
      logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                      "Error getting addrspec: termAddrSpec <%p>, status <%d>", 
                      termAddrSpec, sipstatus);
   }

   if(SipSuccess != sipstatus)
   {
      logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                      "Error retrieving reqline/or a reqline parameter.");
      LogINGwTrace(false, 0, "OUT stackCallbackRequest");
      return Sdf_co_fail;
   }

   _processIncomingViaHeader(aSipConnection, *ppCallObj);


   unsigned int cause = 0;

   //To change the bitmask for RoutingAddress.
   aSipConnection->setAddress(INGwSpSipConnection::TARGET_ADDRESS, termAddrStr);

   INGwSpSipCall &ccmCall = aSipConnection->getCall();

   {
      INGwSpThreadSpecificSipData &tsd = 
                           INGwSpSipProvider::getInstance().getThreadSpecificSipData();

      if(INGwSpSipProviderConfig::isHoldMsg())
      {
         ccmCall.msgList.push_back(std::string(tsd.msgBuf));
      }
   }

   logger.logMsg(TRACE_FLAG, 0, "Getting contact info.");

   _parseContact((*ppCallObj)->pUasTransaction->pSipMsg, aSipConnection);

   logger.logMsg(TRACE_FLAG, 0, "Getting contact info Done");

	 logger.logMsg(TRACE_FLAG, 0, "ORIGADDR %s ", origAddrStr.toLog().c_str());
	 logger.logMsg(TRACE_FLAG, 0, "TARGETADDR %s ", termAddrStr.toLog().c_str());
	 logger.logMsg(TRACE_FLAG, 0, "DIALEDADDR %s ", dialAddrStr.toLog().c_str());

/*
   // NOT using incoming SDP

   // Clone the INVITE message and store it as initial ip data of the
   // associated connection.

   INGwSpData* sipData = INGwSpDataFactory::getInstance().getNewObject();

   if(INGwSpSipUtil::createIpDataFromSipMessage(
        (*ppCallObj)->pUasTransaction->pSipMsg,
        &((*ppCallObj)->pUasTransaction->slMsgBodyList),
        SipMessageRequest, sipData, pErr, 
        ATTRIB_MSGBODY + ATTRIB_SIPMESG, aSipConnection) == Sdf_co_success)
   {
      //aSipConnection->setInitialIpData(sipData);
   }
   else
   {
      logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                      "Error creating IP data from sip message");
      LogINGwTrace(false, 0, "OUT stackCallbackRequest");
      return Sdf_co_fail;
   }

*/

   // So the call object has atleast basic sanity.  Send a 100 Trying here.

   Sdf_ty_retVal status = Sdf_co_fail;
   status = sdf_ivk_uaFormResponse(100,           // response code
                                   "INVITE",      // method name
                                   *ppCallObj,    // call object
                                   Sdf_co_null,   // overlap transaction info
                                   Sdf_co_false,  // is reliable response
                                   pErr);

   if(status != Sdf_co_success)
   {
      logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                      "Error Forming 100 Trying response");
      INGwSpSipUtil::checkError(status, *pErr);
   }
   else
   {
      status = INGwSpSipUtil::sendCallToPeer(*ppCallObj,
                                         (*ppCallObj)->pUasTransaction->pSipMsg,
                                         INGW_SIP_METHOD_TYPE_INVITE, pErr, 
                                         aSipConnection);

      if(Sdf_co_success == status)
      {
         logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, 
                         "Sent a 100 Trying response");
      }
      else
      {
         logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                         "Error sending 100 Trying to peer");
         INGwSpSipUtil::checkError(status, *pErr);
      }
   }

   // Update the connection states.
   aSipConnection->setMinorState(CONN_MINSTATE_MID_3_MSGRX);
   aSipConnection->setLastMethod(INGW_SIP_METHOD_TYPE_INVITE);

   mInviteStateContext.mbIsincoming = true;

   logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, 
                   "state-change  connminorstate <%d>, "
                   "lastmethod <%d>, invitecontext.isincoming <%d>", 
                   CONN_MINSTATE_MID_3_MSGRX, INGW_SIP_METHOD_TYPE_INVITE, true);

   //Now INGW specific handling
   // Controller will send on response on sip conn

	 INGwSpSipCallController* callCtlr =
														INGwSpSipProvider::getInstance().getCallController();
  
	 int retVal = callCtlr->processSasServerRegistration(aSipConnection);

   aSipConnection->transactionStarted(this);

   LogINGwTrace(false, 0, "OUT stackCallbackRequest");
   return Sdf_co_success;
} 

////////////////////////////////////////////////////////////////////////////////
//
// connect
//
////////////////////////////////////////////////////////////////////////////////

short INGwSpMsgInviteHandler::connect(INGwSpSipConnection *aSipConnection,
                                      int                  aNoAnsTimer)
{
	 //CONNECT is not supported in INGW

   logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                   "INGwSpMsgInviteHandler::connect is NOT SUPPORTED.");
   return -1;
} 

////////////////////////////////////////////////////////////////////////////////
// Method: sendInvite
// Description: This method is called when SLEE requests a connect, or locally
//              continue call happens.  This method does different
//              types of connections, as specifid, like normal INVITE, REFER,
//              DIALOUT, etc.
//              This method verifies that the connection is in a state to
//              invoke this method, forms necessary SIP request, and sends it
//              out.  Once the request is send, the INVITE handler state context
//              is updated to reflect the sendout.
////////////////////////////////////////////////////////////////////////////////

short INGwSpMsgInviteHandler::sendInvite(INGwSpSipConnection* aSipConnection)
{
	 // INGW will not send INVITE but only receive them
   logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                   "INGwSpMsgInviteHandler::sendInvite is NOT SUPPORTED.");
   return -1;
} 

////////////////////////////////////////////////////////////////////////////////
// Method: stackCallbackResponse
// Description: This method is called when the stack receives a response.  Both
//              provisional and final responses end up in this method.
////////////////////////////////////////////////////////////////////////////////

Sdf_ty_retVal INGwSpMsgInviteHandler::stackCallbackResponse(
                   INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   int                      aRespCode        ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             ,
                   Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
	 // INGW will not send INVITE so will never receive any response
   logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                   "INGwSpMsgInviteHandler::stackCallbackResponse is NOT SUPPORTED.");

  Sdf_ty_retVal status = Sdf_co_fail;
	return status;
} // End of stackCallbackResponse



short INGwSpMsgInviteHandler::continueProcessing(INGwSpSipConnection *aSipConnection)
{
	 // INGW will not send INVITE so will never receive any response so no continue processing
   logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                   "INGwSpMsgInviteHandler::continueProcessing is NOT SUPPORTED.");
   return -1;
} 

////////////////////////////////////////////////////////////////////////////////
// Method: mSendResponse
// Description: This method is called by when a response needs to be
//              sent for the request
//              This method makes connection state validation to ensure that
//              a response can be sent out.
//              Then a response message is formed, and message body (if present
//              from peer) is inserted into the message.  The response message
//              is then sent out.
////////////////////////////////////////////////////////////////////////////////

int INGwSpMsgInviteHandler::mSendResponse(INGwSpSipConnection *aSipConnection,
                                      INGwSipMethodType     aMethodtype   ,
                                      INGwSpData        *aIpData       ,
                                      int              aRespCode)
{
  logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, "IN mSendResponse");
  logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, "MethodType:<%d>, RespCode:<%d>,",
    aMethodtype, aRespCode);

  short               retval     = 0;
  short               majorState = aSipConnection->getMajorState();
  INGwSipConnMinorState minorState = aSipConnection->getMinorState();
  Sdf_st_callObject  *hssCallObj = aSipConnection->getHssCallObject();
  INGwSipMethodType        methodType = aSipConnection->getLastMethod();
  Sdf_ty_retVal       status     = Sdf_co_fail;
  Sdf_st_error        sdferror;

  // Validate that response can be sent in this connection state.
  if((minorState != CONN_MINSTATE_MID_3_MSGRX) ||
     (methodType != INGW_SIP_METHOD_TYPE_INVITE))
  {
    // This is not really an error, since this can happen when this handler
    // has already sent a final response to the INVITE because of CANCEL or
    // something.
    logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
      "Minor State not CONN_MINSTATE_MID_3_MSGRX OR Method Type not INVITE");
    logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT mSendResponse");
    return -1;
  }

  // Set the ip data as the peer's initial ip data in the connection.
  // The false is to indicate that this is peer's ip data.


  if( (0 != aIpData) && ((INGwSpData *)aIpData)->getBody())
  {
#if 0
    INGwSpData *peerINGwSpData =
      dynamic_cast<INGwSpData *>(aSipConnection->getInitialIpData(false));
    if(!peerSipData)
      peerINGwSpData =INGwSpDataFactory::getInstance().getNewObject();


        // hack for MgcpData for now - Gaurav. To be removed.
    peerSipData->copyMsgBody(*aIpData);
    //peerSipData->copy(dynamic_cast<INGwSpData &>(*aIpData), ATTRIB_MSGBODY);
    aSipConnection->setInitialIpData(peerSipData, false);
#endif

  }

  // Form a response with the given response code.
  status = sdf_ivk_uaFormResponse(aRespCode   ,
                                  "INVITE"    ,
                                  hssCallObj  ,
                                  Sdf_co_null ,
                                  Sdf_co_false,
                                  &sdferror   );


  if(Sdf_co_fail == status) {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "sdf_ivk_uaFormResponse failed ... ");
    logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT mSendResponse");
    return -1;
  }

  if(ISSUCCESSRESP(aRespCode))
  {
    if(!INGwSpSipUtil::setBodyFromAttr(hssCallObj->pUasTransaction->pSipMsg, 
                                   aSipConnection))
    {
       if(aIpData)
       {
         char *msgbody    = (char *)(((INGwSpData *)(aIpData))->getBody());
         int   msgbodylen = ((INGwSpData *)aIpData)->getBodyLength();
         char* bodyType   = (char *)(((INGwSpData *)aIpData)->getBodyType());
         if(msgbody)
         {
           status = INGwSpSipUtil::copyMsgBodyIntoSipMessage
                    (hssCallObj->pUasTransaction->pSipMsg, msgbody, msgbodylen);
           if(Sdf_co_fail == status) {
             logger.logINGwMsg(aSipConnection->mLogFlag, WARNING_FLAG, 0, 
                          "INGwSpSipUtil::copyMsgBodyIntoSipMessage failed");
           }
           else {
             status = INGwSpSipUtil::setContentTypeInMessage
                   (hssCallObj->pUasTransaction->pSipMsg, bodyType, msgbodylen);
             if(Sdf_co_fail == status) {
               logger.logINGwMsg(aSipConnection->mLogFlag, WARNING_FLAG, 0, 
                          "INGwSpSipUtil::setContentTypeInMessage failed");
             }
           }
         }
         else
         {
           logger.logINGwMsg(aSipConnection->mLogFlag, WARNING_FLAG, 0, 
                           "No msg body");
           // XXX: Error
         }
       }
    }
  }


  status = INGwSpSipUtil::sendCallToPeer(hssCallObj,
                                     hssCallObj->pUasTransaction->pSipMsg,
                                     INGW_SIP_METHOD_TYPE_INVITE,
                                     &sdferror,
                                     aSipConnection);

  if(Sdf_co_fail == status)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, 
      "INGwSpSipUtil::sendCallToPeer failed ... ");
    INGwSpSipUtil::checkError(status, sdferror);
    logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT mSendResponse");
    return -1;
  }

  mInviteStateContext.miLatestRespCode = aRespCode;

  // Update the connection state.
  if(ISSUCCESSRESP(aRespCode) || ISREDIRRESP(aRespCode))
  {
    aSipConnection->setMinorState(CONN_MINSTATE_MID_3_RSPTX);
  }

  aSipConnection->setMinorState(CONN_MINSTATE_MID_3_RSPTX);

  if(aRespCode >=400 )
  {
    int lCurVal = 0;
    INGwIfrSmStatMgr::instance().increment(
                      INGwSpSipProvider::miStatParamId_NumInvRejected, lCurVal, 1);
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT mSendResponse");
  return retval;
} // End of mSendResponse

////////////////////////////////////////////////////////////////////////////////
// Method: stackCallbackAck
// Description: This method is called when stack receives an ACK.
//              This method validates the connection that ACK can be actually
//              received
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal INGwSpMsgInviteHandler::stackCallbackAck
                (INGwSpSipConnection *aSipConnection           ,
                 INGwSipMethodType             aMethodType      ,
                 Sdf_st_callObject      **ppCallObj        ,
                 Sdf_st_eventContext     *pEventContext    ,
                 Sdf_st_error            *pErr             )
{
  logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, "IN stackCallbackAck");

  short               majorState = aSipConnection->getMajorState();
  INGwSipConnMinorState minorState = aSipConnection->getMinorState();
  INGwSipMethodType        methodType = aSipConnection->getLastMethod();
  Sdf_ty_retVal       status     = Sdf_co_success;

  // Validate that ack can be received in this state.
  if((minorState != CONN_MINSTATE_MID_3_RSPTX) ||
     (methodType != INGW_SIP_METHOD_TYPE_INVITE))
  {
    
    logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
											"INGwSpMsgInviteHandler::stackCallbackAck: "
											"Ack callback received in wrong connection state <XX:%d>\n", 
											minorState);
    sprintf(pErr->ErrMsg, "Ack callback received in wrong connection state <XX:%d>\n", minorState);
    pErr->errCode = Sdf_en_callStateError;
    return Sdf_co_fail;
  }

  // Update the connection state.
  aSipConnection->setMinorState(CONN_MINSTATE_IDLE);
  aSipConnection->setLastMethod(INGW_METHOD_TYPE_NONE);

  // If there was an ongoing CANCEL for the last INVITE, then further processing
  // should not be done until that CANCEL is complete.  Find this out from
  // the CANCEL handler.
#if 0
  if(mInviteStateContext.isCancelPending)
  {
    mInviteStateContext.isCancelPending = false;
    BpSipCancelHandler *cancelhandler = dynamic_cast<BpSipCancelHandler *>
      (aSipConnection->getSipHandler(INGW_SIP_METHOD_TYPE_CANCEL));

    // If the CANCEL is complete, then proceed to further process the ACK.
    // If not, immdiately return from here so that when the CANCEL completes,
    // the cancel handler can do the needful.
    if(!cancelhandler->isTransactionComplete())
    {
      // If the last INVITE has got a final success response, then it has
      // established a dialog, which has to be torn down.  Mark the cancel
      // handler to be pending with BYE, so that it can tear down the dialog
      // when the CANCEL completes.
      // Do this only if the bye is already not pending.  If it is pending, it
      // would have already been marked as pending in cancel handler also.
      // ?????? There can be a problem here, where a Re-INVITE was being
      // ?????? cancelled, the Re-INVITE got a success response, but we do not
      // ?????? want to tear the dialog.  How to handle this?  But can this
      // ?????? happen?  When can a Re-INVITE get cancelled?  Actually if we
      // ?????? get a success response to Re-INVITE, then the cancellation has
      // ?????? really failed.  Doesn't seem to be any case like this in BP
      // ?????? though.
      if(!mInviteStateContext.isFailedByePending)
        cancelhandler->setByePending(true);
      return Sdf_co_success;
    }
  }
  // Check here whether there is a pending BYE for this connection.  If there
  // is, no more processing should be done, and simply invoke disconnect on
  // BYE handler.  That will send out BYE and do the needful.
  if(mInviteStateContext.isFailedByePending)
  {
    BpSipByeHandler *byehandler = dynamic_cast<BpSipByeHandler *>
      (aSipConnection->getSipHandler(INGW_SIP_METHOD_TYPE_BYE));
    if(byehandler)
      byehandler->disconnect(aSipConnection, 0);
 
    return Sdf_co_success;
  }

#endif

  // If the last operation got an error response, or if the connection is in a
  // FAILED state (because CANCEL happened).  
  if((majorState == CONNECTION_FAILED) ||
     (mInviteStateContext.miLatestRespCode >= 400  ))
  {
   // If tcp mode of transport, we need to clean it now
   if(! INGwSpSipProviderConfig::isTransportUdp())
   {
     INGwSpTcpConnMgr::instance().removeClientConn(aSipConnection->getGwIPAddress());
   }
    //clear the Sipcall and Sipconnections
    INGwSipEPInfo lSipEPInfoTmp;
    lSipEPInfoTmp.msLocalCallID = aSipConnection->getLocalCallId();
    INGwSpSipCallController* callCtlr =
                            INGwSpSipProvider::getInstance().getCallController();
    callCtlr->cleanSipCall(lSipEPInfoTmp);
 
    return Sdf_co_success;
  }

  //Now INGW specific handling
  //1 - Make a SAS structure

  INGwSipEPInfo lSipEPInfo;
  lSipEPInfo.port = aSipConnection->getGwPort();
  lSipEPInfo.clientport = aSipConnection->getActiveEPPort();
  lSipEPInfo.initalClientPort = aSipConnection->getActiveEPPort();
  lSipEPInfo.msLocalCallID = aSipConnection->getLocalCallId();
  strncpy((char*)lSipEPInfo.mEpHost, aSipConnection->getGwIPAddress(),MAX_EP_HOST_LENGTH);
  strncpy((char*)lSipEPInfo.mContactHdr, aSipConnection->getContactHdr(), MAX_HEADER_LEN  );

	lSipEPInfo.mContactAddSpec = aSipConnection->getContactAddSpec();
	lSipEPInfo.mFromAddSpec = aSipConnection->getFromAddSpec();
  lSipEPInfo.mEPTimer = new INGWSipEPTimer();

	//Update User, host and port in addr specs
  SipUrl* psipurl  = 0;
	SipError siperror;

	if(lSipEPInfo.mFromAddSpec != NULL)
	{
		if((sip_getUrlFromAddrSpec(lSipEPInfo.mFromAddSpec, &psipurl, &siperror) != SipSuccess) || 
																																								(!psipurl))
    {
      logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
											"INGwSpMsgInviteHandler::stackCallbackAck: "
											"Count not get UrlFromAddrSpec so will use existing user and host");
		}
		else
		{
			if(psipurl->pHost != NULL && psipurl->dPort != NULL)
			{
        logger.logINGwMsg(false, TRACE_FLAG, 0, 
			  									"Before replace Host [%s] Port [%d]", 
			  									psipurl->pHost, *(psipurl->dPort));
      }
      std::string lUser = "ingw";
      //char* newUser = (char *)fast_memget(0, lUser.length()+ 1, &siperror);
      char* newHost = (char *)fast_memget(0, strlen(aSipConnection->getGwIPAddress()) + 1, &siperror);

      //strcpy(newUser, lUser.c_str());
      strcpy(newHost, aSipConnection->getGwIPAddress());
      //sip_setUserInUrl(psipurl, newUser, &siperror);
      sip_setHostInUrl(psipurl, newHost, &siperror);
      sip_setPortInUrl(psipurl, aSipConnection->getGwPort(), &siperror);

      logger.logINGwMsg(false, TRACE_FLAG, 0, 
                      "After replace Host [%s] Port [%d]", 
                      psipurl->pHost, *(psipurl->dPort));
      sip_freeSipUrl(psipurl);
		}
	}

  SipUrl* psipurlContact  = 0;
	if(lSipEPInfo.mContactAddSpec != NULL)
	{
		if((sip_getUrlFromAddrSpec(lSipEPInfo.mContactAddSpec, &psipurlContact, &siperror) != SipSuccess) || 
																																								(!psipurlContact))
    {
      logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
											"INGwSpMsgInviteHandler::stackCallbackAck: "
											"Count not get UrlFromAddrSpec so will use existing user and host");
		}
		else
		{
			if(psipurlContact->pHost != NULL && psipurlContact->dPort != NULL)
			{
        logger.logINGwMsg(false, TRACE_FLAG, 0, 
			  									"Before replace Host [%s] Port [%d]", 
			  									psipurlContact->pHost, *(psipurlContact->dPort));
    }
      std::string lUser = "ingw";
      //char* newUser = (char *)fast_memget(0, lUser.length()+ 1, &siperror);
      char* newHost = (char *)fast_memget(0, strlen(aSipConnection->getGwIPAddress()) + 1, &siperror);

      //strcpy(newUser, lUser.c_str());
      strcpy(newHost, aSipConnection->getGwIPAddress());
      //sip_setUserInUrl(psipurlContact, newUser, &siperror);
      sip_setHostInUrl(psipurlContact, newHost, &siperror);
      sip_setPortInUrl(psipurlContact, aSipConnection->getGwPort(), &siperror);

      logger.logINGwMsg(false, TRACE_FLAG, 0, 
                      "After replace Host [%s] Port [%d]", 
                      psipurlContact->pHost, *(psipurlContact->dPort));
      sip_freeSipUrl(psipurlContact);
		}
	}

  //2 - Save it in a map

  INGwSpSipCallController* callCtlr =
                            INGwSpSipProvider::getInstance().getCallController();
  callCtlr->addEndPoint(lSipEPInfo);

  // Update the connection state.
  aSipConnection->setMajorState(CONNECTION_CONNECTED);
  mInviteStateContext.miLatestRespCode = -1;

	// 3 -Need to send FT data to peer end
  aSipConnection->transactionEnded(this, INVITE_HANDLER);

  
  // This is the end of callsetup.  Get the call setup start time from the
  // state context and update the call setup statistics parameter.

  if(mInviteStateContext.callSetupStartTime > 0)
  {
    int callsetupduration = (int)
                    (((hrtime_t)(gethrtime() - mInviteStateContext.callSetupStartTime))/1000000);
    mInviteStateContext.callSetupStartTime = -1;
    logger.logINGwMsg(aSipConnection->mLogFlag, VERBOSE_FLAG, 0, 
											"Call setup time <%d>", callsetupduration);
  }
  else
    logger.logINGwMsg(aSipConnection->mLogFlag, VERBOSE_FLAG, 0, 
											"Call setup time <%d>", mInviteStateContext.callSetupStartTime);
  return status;
} // End of stackCallbackAck

////////////////////////////////////////////////////////////////////////////////
// Method: mSendAck
////////////////////////////////////////////////////////////////////////////////
int INGwSpMsgInviteHandler::mSendAck(INGwSpSipConnection *aSipConnection,
                                 INGwSipMethodType     aMethodType,
                                 INGwSpData        *aSipData)
{
	 // INGW will not send INVITE so will never send ACK
   logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                   "INGwSpMsgInviteHandler::mSendAck is NOT SUPPORTED.");
   return -1;
} // end of mSendAck

int INGwSpMsgInviteHandler::mSendRequest
      (INGwSpSipConnection         *aSipConnection   ,
       INGwSipMethodType             aMethodType      ,
       INGwSpData                *aGwData          )
{
  return -1;
}

void INGwSpMsgInviteHandler::generateEvent(INGwSpSipConnection *aSipConnection)
{
	//INGW will not generate any event
} // end of generateEvent

void INGwSpMsgInviteHandler::indicateTimeout
       (INGwSpSipConnection          *aSipConnection   ,
        INGwSipMethodType              aMethodType      ,
        INGwSipTimerType::TimerType aType            ,
        unsigned int              aTimerid         )
{
  logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, "IN INGwSpMsgInviteHandler::indicateTimeout");
  INGwSipConnMinorState connMinorState = aSipConnection->getMinorState();
  INGwSipMethodType        methodType     = aSipConnection->getLastMethod();

  // Validate that the ongoing method is INVITE and that the transaction has
  // already not completed.
  if((methodType     != INGW_SIP_METHOD_TYPE_INVITE) ||
     (connMinorState == CONN_MINSTATE_IDLE       )  )
  {
    // The method type is not invite (which is an error) or the timeout was
    // received AFTER the transaction completed (which is race condition so
    // the timer is useless now).
    logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT INGwSpMsgInviteHandler::indicateTimeout");
    return;
  }

  // If the timeout is because of the stack, then this INVITE has failed.
  // Mark the connection as FAILED, and generate a suitable event.
  if(aType == INGwSipTimerType::STACK_TIMER)
	{
		logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, 
			"IN INGwSpMsgInviteHandler::indicateTimeout: STACK_TIMER timed out: %x", aTimerid );

		// We should stop the IRT and NAT both if they are still running.

		if(mInviteStateContext.mInitRespTimerid)
		{
			INGwSpSipProvider::getInstance().getThreadSpecificSipData().getStackTimer().
				stopInitRespTimer(mInviteStateContext.mInitRespTimerid);
			mInviteStateContext.mInitRespTimerid = 0;
		}

		if(mInviteStateContext.mNoAnsTimerid) {
			INGwSpSipProvider::getInstance().getThreadSpecificSipData().getStackTimer()
				.stopNoAnswerTimer(mInviteStateContext.mNoAnsTimerid);
			mInviteStateContext.mNoAnsTimerid = 0;
		}

    int connMajorPriorState = (int)aSipConnection->getMajorState();
    aSipConnection->setMajorState(CONNECTION_FAILED);
    mInviteStateContext.miFailureCause = FAILCAUSE_TIMEOUT;
    aSipConnection->setLastMethod(INGW_METHOD_TYPE_NONE);
    aSipConnection->setMinorState(CONN_MINSTATE_IDLE);

    bool bClearTrans = true;

    // Before raising an event, check whether there is a pending CANCEL.  If
    // there is, the event will be generated once the CANCEL completes.
#if 0
    if(mInviteStateContext.isCancelPending)
    {
      mInviteStateContext.isCancelPending = false;
      BpSipCancelHandler *cancelhandler = dynamic_cast<BpSipCancelHandler *>
        (aSipConnection->getSipHandler(INGW_SIP_METHOD_TYPE_CANCEL));

      // If the CANCEL is complete, then proceed to clear transaction
      // If not, immdiately return from here so that when the CANCEL completes,
      // the cancel handler can do the needful.
      if(!cancelhandler->isTransactionComplete())
      {
         bClearTrans = false;
      }
    }
#endif

    if(bClearTrans)
    {
       //Have to cleanup the transaction obj.
       Sdf_st_callObject* hsscall = aSipConnection->getHssCallObject();

       if(hsscall != NULL)
       {
          sdf_ivk_uaClearTransaction(hsscall, NULL, "INVITE", 
                                     Sdf_en_uacTransaction);
          sdf_ivk_uaClearTransaction(hsscall, NULL, "INVITE", 
                                     Sdf_en_uasTransaction);
       }
    }
  } // end of if(STACK_TIMER)
  else if(aType == INGwSipTimerType::NOANSWER_TIMER)
  {
    // If there is a pending no answer timer whose id is different from the one
    // we have got now, ignore this timer.  This is a race condition, where the
    // timer fired before it was stopped.
    if(aTimerid != mInviteStateContext.mNoAnsTimerid)
    {
      logger.logINGwMsg(aSipConnection->mLogFlag, VERBOSE_FLAG, 0, 
												"INGwSpMsgInviteHandler::indicateTimeout: Ignoring timerid  %u", 
												aTimerid);
      return;
    }

		logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, 
			                "NOANSWER_TIMER (NoAnswerTimer) timed out: %x", aTimerid );

    mInviteStateContext.mNoAnsTimerid = 0;

    // STATISTICS: Total number of NA timeouts: increment
    logger.logINGwMsg(aSipConnection->mLogFlag, VERBOSE_FLAG, 0, "Received a NO-ANSWER timeout");

    handleTimeout(aSipConnection, aType);
  } // end of if(NOANSWER_TIMER)
  else if(aType == INGwSipTimerType::INITRESP_TIMER)
  {
    // If there is a pending init resp timer whose id is different from the one
    // we have got now, ignore this timer.  This is a race condition, where the
    // timer fired before it was stopped.
    if(aTimerid != mInviteStateContext.mInitRespTimerid)
    {
      logger.logINGwMsg(aSipConnection->mLogFlag, VERBOSE_FLAG, 0, 
												"INITRESP_TIMER Race. rxId:<%x>, storedId:<%x>", 
												aTimerid, mInviteStateContext.mInitRespTimerid);

      logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT indicateTimeout");
      return;
    }

		logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, 
			                "INITRESP_TIMER (InitRespTimer) timed out: %x", aTimerid );

    mInviteStateContext.mInitRespTimerid = 0;

    // STATISTICS: Total number of IR timeouts: Increment
  }
	// PANAKJ
	// We need to cleanup the call and connection object as done by continue call

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT indicateTimeout");
} // end of indicateTimeout

bool INGwSpMsgInviteHandler::terminateTransaction(INGwSpSipConnection *aSipConnection,
                                              int              aErrcode      ,
                                              TerminateType    aTermType     )
{
  LogINGwTrace(aSipConnection->mLogFlag, 0, "IN terminateTransaction");
  Sdf_st_callObject  *hssCallObj = aSipConnection->getHssCallObject();
  INGwSipConnMinorState minorState = aSipConnection->getMinorState();
  Sdf_ty_retVal status = Sdf_co_fail;
  Sdf_st_error  sdferror;

  logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, 
										"INGwSpMsgInviteHandler::terminateTransaction: connmajorstate <%d>,"
										" connminorstate <%d>, connection <%s>", 
										aSipConnection->getMajorState(), minorState, 
										aSipConnection->getLocalCallId().c_str());

  // Check the state of the INVITE.  Based on that, either send a failure
  // response or send an ACK.
  if(minorState == CONN_MINSTATE_MID_3_MSGRX)
  {
    // Invite has been received.  Send the response.  The transaction will not
    // terminate until ACK is received, or the response times out.
    sdf_ivk_uaFormResponse(aErrcode    ,
                           "INVITE"    ,
                           hssCallObj  ,
                           Sdf_co_null ,  // overlap transaction info
                           Sdf_co_false,  // is reliable response
                           &sdferror   );

    INGwSpSipUtil::setBodyFromAttr(hssCallObj->pUasTransaction->pSipMsg, 
                               aSipConnection);
    INGwSpSipUtil::addHeadersFromAttr(hssCallObj->pUasTransaction->pSipMsg, 
                                  aSipConnection);

    INGwSpSipUtil::sendCallToPeer(hssCallObj,
                              hssCallObj->pUasTransaction->pSipMsg,
                              INGW_SIP_METHOD_TYPE_INVITE,
                              &sdferror,
                              aSipConnection);
    mInviteStateContext.miLatestRespCode = aErrcode;

    aSipConnection->setMinorState(CONN_MINSTATE_MID_3_RSPTX);

    // Mark here to send bye when transaction completes.
    if(aTermType & TERMINATE_BYE)
      mInviteStateContext.isFailedByePending = true;

    LogINGwTrace(false, 0, "OUT terminateTransaction");
    return false;
  }
  else if(minorState == CONN_MINSTATE_MID_3_RSPRX)
  {
    // Response has been received.  Send an ACK.  This transaction is now
    // complete.
    status = sdf_ivk_uaFormRequest("ACK",
                                   hssCallObj,
                                   Sdf_co_null, // Not an overlap transaction
                                   &sdferror);
#if 0
    if(!aSipConnection->mstrContactUser.empty())
    {
      INGwSpSipUtil::setContactUser(hssCallObj->pUacTransaction->pSipMsg,
                                aSipConnection->mstrContactUser.c_str());
    }
#endif

    INGwSpSipUtil::sendCallToPeer(hssCallObj,
                              hssCallObj->pUacTransaction->pSipMsg,
                              INGW_SIP_METHOD_TYPE_INVITE,
                              &sdferror,
                              aSipConnection);

    // Update the connection state and reset the INVITE state context.
    // XXX: Also reset the last operation here.  This applies to re-inv too.
    aSipConnection->setMajorState(CONNECTION_FAILED);
    aSipConnection->setMinorState(CONN_MINSTATE_IDLE);
    aSipConnection->setLastMethod(INGW_METHOD_TYPE_NONE);
    mInviteStateContext.reset();

    LogINGwTrace(false, 0, "OUT terminateTransaction");
    return true;
  }
  else if(minorState == CONN_MINSTATE_MID_3_MSGTX)
  {
    // Invite has been sent.  If CANCEL needs to be sent, send it here.
    if(aTermType & TERMINATE_CANCEL)
    {
      // Check whether the latest response (if any) is provisional.  If not,
      // CANCEL cannot be sent now.  If no response has yet been received, the
      // CANCEL sending should be postponed till the time a provisional
      // response is received.
      if(ISPROVRESP(mInviteStateContext.miLatestRespCode))
      {
#if 0
        BpSipCancelHandler *cancelhandler = (BpSipCancelHandler *)
          aSipConnection->getSipHandler(INGW_SIP_METHOD_TYPE_CANCEL);

        // If a BYE needs to be sent after the transaction completion, inform
        // this to the CANCEL handler also, since we have no means to ensure
        // that either INVITE or CANCEL will complete first.  Whoever completes
        // last is responsible for sending the BYE.
        mInviteStateContext.isCancelPending = true;
        cancelhandler->sendCancel(aSipConnection,
                                  INGW_SIP_METHOD_TYPE_CANCEL,
                                  aTermType & TERMINATE_BYE);
#endif
      }
      else if(mInviteStateContext.miLatestRespCode <= 0)
      {
        // No response has been received yet.  Mark so that when a provisional
        // response is received, the CANCEL will be sent.
        mInviteStateContext.isCancelPending = true;
      }
      else
      {
        // Some final response has been received.  CANCEL cannot be done now.
      }
    } // end of if(aTermType & TERMINATE_CANCEL)

    if(aTermType & TERMINATE_BYE)
      mInviteStateContext.isFailedByePending = true;

    LogINGwTrace(false, 0, "OUT terminateTransaction");
    return false;
  }
  else if(minorState == CONN_MINSTATE_MID_3_RSPTX)
  {
    // Response has already been sent.  The transaction will complete when
    // ACK is received or the response times out.
    // Mark here to call BYE handler when the transaction completes.
    if(aTermType & TERMINATE_BYE)
      mInviteStateContext.isFailedByePending = true;

    LogINGwTrace(false, 0, "OUT terminateTransaction");
    return false;
  }
  LogINGwTrace(false, 0, "OUT terminateTransaction");
  return true;
} // end of terminateTransaction

////////////////////////////////////////
//
// mSendRPR
//
////////////////////////////////////////
int
INGwSpMsgInviteHandler::mSendRPR(INGwSpSipConnection* aSipConnection,
  INGwSipMethodType aMethodType,
  INGwSpData* aGwData,
  int aCode) 
{
	 // INGW will not send reliable response
   logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                    "INGwSpMsgInviteHandler::mSendRPR is NOT SUPPORTED.");
   return -1;
}

////////////////////////////////////////
//
// processUnProcessedHeaders
//
////////////////////////////////////////
Sdf_ty_retVal 
INGwSpMsgInviteHandler::processUnProcessedHeaders(Sdf_st_callObject* hssCallObj,
                                                  INGwSpSipConnection* bpConn, 
                                                  Sdf_st_error* pErr, SIP_U32bit & causeCode)
{
	 // INGW will not parse extra headers
   logger.logINGwMsg(bpConn->mLogFlag, ERROR_FLAG, 0, 
                    "INGwSpMsgInviteHandler::processUnProcessedHeaders is NOT SUPPORTED.");
   Sdf_ty_retVal status = Sdf_co_fail;
   return status;
}

Sdf_ty_retVal INGwSpMsgInviteHandler::sendAckToPeer(INGwSpSipConnection *aSipConnection,
                                                Sdf_st_callObject *aCallObj)
{
  Sdf_ty_retVal status = Sdf_co_fail;
  Sdf_st_error  sdferror;

  status = sdf_ivk_uaFormRequest((Sdf_ty_s8bit *)"ACK",
                                 aCallObj,
                                 Sdf_co_null,
                                 &sdferror);
#if 0
  if(!aSipConnection->mstrContactUser.empty())
  {
    INGwSpSipUtil::setContactUser(aCallObj->pUacTransaction->pSipMsg,
                              aSipConnection->mstrContactUser.c_str());
  }
#endif
  status = INGwSpSipUtil::sendCallToPeer(aCallObj,
                                     aCallObj->pUacTransaction->pSipMsg,
                                     INGW_SIP_METHOD_TYPE_INVITE,
                                     &sdferror,
                                     aSipConnection);

  return status;
} // end of sendAckToPeer

bool INGwSpMsgInviteHandler::searchContact(INGwSpSipConnection *aSipConnection)
{
	 // INGW will not support redirection
   logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                    "INGwSpMsgInviteHandler::searchContact is NOT SUPPORTED.");
  return false;
} // end of searchContact

void INGwSpMsgInviteHandler::handleTimeout(INGwSpSipConnection* aSipConnection,
                                       INGwSipTimerType::TimerType aType)
{
  LogINGwTrace(aSipConnection->mLogFlag, 0, "IN handleTimeout");
  INGwSipConnMinorState connMinorState = aSipConnection->getMinorState();
  INGwSipMethodType        methodType     = aSipConnection->getLastMethod();
  int                 lastresp       = mInviteStateContext.miLatestRespCode;

  logger.logINGwMsg(aSipConnection->mLogFlag, TRACE_FLAG, 0, 
										"handleTimeout: connminorstate <%d>, methodtype <%d>, lastresp <%d>, connection <%s>", 
										connMinorState, methodType, lastresp, aSipConnection->getLocalCallId().c_str());

  mInviteStateContext.mNoAnsTimerid    = 0;

  // If initial response timer is running, stop it
  if(mInviteStateContext.mInitRespTimerid) {
    INGwSpSipProvider::getInstance().getThreadSpecificSipData().
      getStackTimer().stopInitRespTimer(mInviteStateContext.mInitRespTimerid);
    mInviteStateContext.mInitRespTimerid = 0;
  }

  // Entertain this only if the state is having sent out INVITE.
  if(connMinorState != CONN_MINSTATE_MID_3_MSGTX)
  {
    LogINGwTrace(false, 0, "OUT handleTimeout");
    return;
  }

  // Nothing more can be done on this connection.  Reset states.
  // XXX: Actually this may be required, since we need to hang around till the
  // XXX: signaling completes.
  aSipConnection->setMajorState(CONNECTION_FAILED);
  // aSipConnection->setDead(true);
  // aSipConnection->setMinorState(CONN_MINSTATE_IDLE);
  // aSipConnection->setLastMethod(INGW_METHOD_TYPE_NONE);

  mInviteStateContext.reset();

  // If a provisional response has been received, send out a CANCEL.
  if(ISPROVRESP(lastresp))
  {
    // The last response Code is updated with 408 as No Answer timer 
    // Expires.
#if 0 
    mInviteStateContext.miLatestRespCode = 408;
    aSipConnection->miFailureRespCode = 408;
    BpSipCancelHandler *cancelhandler = dynamic_cast<BpSipCancelHandler *>
    (aSipConnection->getSipHandler(INGW_SIP_METHOD_TYPE_CANCEL));
    cancelhandler->sendCancel(aSipConnection, INGW_SIP_METHOD_TYPE_CANCEL, 0);
#endif
  }
  else
  {

    if(CONNECTION_FAILED == aSipConnection->getMajorState()) 
    {
      //reset call min state to idle
      //aSipConnection->mSendGwResponse(INGW_SIP_METHOD_TYPE_INVITE, 0, 408,
      //                           mInviteStateContext.m_isRpr, 0,
      //                           aSipConnection->getChargeStatus());
    }
  }

  LogINGwTrace(false, 0, "OUT handleTimeout");
} // end of handleTimeout

bool INGwSpMsgInviteHandler::isIncoming()
{
  return mInviteStateContext.mbIsincoming;
}

void INGwSpMsgInviteHandler::processPrivacy(INGwSpSipConnection* aSipConnection,
                                        Sdf_st_callObject* aCallObj)
{
	 // INGW will not support privacy header
   logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                    "INGwSpMsgInviteHandler::processPrivacy is NOT SUPPORTED.");
} // end of processPrivacy

bool
INGwSpMsgInviteHandler::searchOutboundGw(INGwSpSipConnection *aSipConnection) 
{
   logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                    "INGwSpMsgInviteHandler::searchOutboundGw is NOT SUPPORTED.");
   return false;
} // end of searchOutboundGw

bool INGwSpMsgInviteHandler::getRedirectionInfo
  (INGwSpSipConnection* aSipConnection,
   INGwSipEPInfo& aSourceInfo,
   INGwSipMethodType aHandlerType)
{
   logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                    "INGwSpMsgInviteHandler::getRedirectionInfo is NOT SUPPORTED.");
   return false;
} // end of getRedirectionInfo

Sdf_ty_retVal INGwSpMsgInviteHandler::stackCallbackResponse_Redir(
                   INGwSpSipConnection         *aSipConnection   ,
                   INGwSipMethodType             aMethodType      ,
                   int                      aRespCode        ,
                   Sdf_st_callObject      **ppCallObj        ,
                   Sdf_st_eventContext     *pEventContext    ,
                   Sdf_st_error            *pErr             ,
                   Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
   logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                    "INGwSpMsgInviteHandler::stackCallbackResponse_Redir is NOT SUPPORTED.");
   Sdf_ty_retVal        retval         = Sdf_co_fail;
   return retval;
} // end of redirect

void INGwSpMsgInviteHandler::releaseTempRedirCallObj
  (INGwSpSipConnection* aSipConnection,
   Sdf_st_callObject** ppCallObj)
{
   logger.logINGwMsg(aSipConnection->mLogFlag, ERROR_FLAG, 0, 
                    "INGwSpMsgInviteHandler::releaseTempRedirCallObj is NOT SUPPORTED.");
}

std::string INGwSpMsgInviteHandler::toLog() const
{
   std::string ret = "\nInvite Handler\n";

   ret += mInviteStateContext.toLog();

   return ret;
}

int INGwSpMsgInviteHandler::getLatestRespCode()
{
  return mInviteStateContext.miLatestRespCode;
}

bool INGwSpMsgInviteHandler::_processIncomingViaHeader(INGwSpSipConnection *conn,
                                                   Sdf_st_callObject *hssObj)
{
   if(!INGwSpSipProviderConfig::isProcessIncomingVia())
   {
      return false;
   }

   SipList &viaHdrs = hssObj->pUasTransaction->pSipMsg->pGeneralHdr->slViaHdr;

   if(viaHdrs.size == 0)
   {
      logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, "No via headers");
      return false;
   }

   SipViaHeader *firstVia;
   
   switch(INGwIfrPrParamRepository::getInstance().getOperationMode())
   {
      case NPlusZero:
      case NPlusOne:
      {
         //We have to get the GW from second via.
         if(viaHdrs.size < 2)
         {
            logger.logINGwMsg(conn->mLogFlag, TRACE_FLAG, 0, "No GW Via headers");
            return false;
         }

         firstVia = (SipViaHeader *) viaHdrs.head->next->pData;
      }
      break;
      default:
      {
         firstVia = (SipViaHeader *) viaHdrs.head->pData;
      }
   }

   if(firstVia == NULL)
   {
      logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, "NULL via in list");
      return false;
   }

   char *peerInfo = firstVia->pSentBy;

   if(peerInfo == NULL)
   {
      logger.logINGwMsg(conn->mLogFlag, ERROR_FLAG, 0, "No sentby in Via");
      return false;
   }

   //PeerInfo syntax. host[:port]
   //Lets check whether port is there or not.

   while(*peerInfo == ' ') peerInfo++;

   char *colonStart = strchr(peerInfo, ':');

   if(colonStart)
   {
      char *portStart = colonStart + 1;
      while(*portStart == ' ') portStart++;
      conn->setGwPort(atoi(portStart));

      char *peerEnd = colonStart - 1;
      while(*peerEnd == ' ' && peerEnd > peerInfo) peerEnd--;
      conn->setGwIPAddress(peerInfo, peerEnd - peerInfo + 1);
   }
   else
   {
      char *peerEnd = peerInfo;

      while(*peerEnd != ' ' && *peerEnd != '\0') peerEnd++;
      conn->setGwIPAddress(peerInfo, peerEnd - peerInfo);
      conn->setGwPort(5060);
   }

   return true;
}

