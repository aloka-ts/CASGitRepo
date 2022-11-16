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
//     File:     INGwSpSipConnection.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <INGwSipProvider/INGwSpSipConnection.h>

#include <INGwSipProvider/INGwSpSipCall.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipProviderConfig.h>

#include <INGwSipProvider/INGwSpSipUtil.h>

#include <INGwSipProvider/INGwSpSipConnectionFactory.h>

#include <INGwSipProvider/INGwSpDataFactory.h>

#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwSipProvider/INGwSpSipListenerThread.h>

#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>
#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>
#include <INGwSipProvider/INGwSpSipCall.h>

#if 0
using namespace RSI_NSP_CCM;

VersionHolder * INGwSpSipConnection::_verHolder = NULL;
#endif

int INGwSpSipConnection::miStCount = 0;
pthread_mutex_t  INGwSpSipConnection::mStMutex;


void INGwSpSipConnection::setCounters()
{
   pthread_mutex_lock(&mStMutex);
   miStCount++;
   pthread_mutex_unlock(&mStMutex);
}

int
INGwSpSipConnection::initStaticCount(void)
{
   return pthread_mutex_init(&mStMutex, NULL);
}

int
INGwSpSipConnection::getCount(void) { return miStCount; }


INGwSpSipConnection::INGwSpSipConnection()
{
   INGwSpSipConnection::initObject(true);
} 

void INGwSpSipConnection::initObject(bool consFlag)
{
   if(! consFlag)
   {
      INGwIfrUtlRefCount::initObject(consFlag);
      INGwIfrUtlSerializable::initObject(consFlag);

      pthread_mutex_lock(&mStMutex);
      miStCount--;
      pthread_mutex_unlock(&mStMutex);
			
   }
	 else
	 {
      memset(&mChangeData, 0, sizeof(ChangeableConnectionData));
      memset(&mConstData, 0, sizeof(ConstConnectionData));
	 }


   mActiveSessionTimerId = 0;

   m_MinSessionInterval    = 0;
   m_MaxSessionInterval    = 0;

   meConnMinorState      = CONN_MINSTATE_IDLE;
   meLastMethod          = INGW_METHOD_TYPE_NONE;
   mbStackSerReqdFlag    = true;
   mHssCallLeg           = NULL;
   miIncomingGwPort      = -1;
   miFailureRespCode     = 0;
   mLogFlag              = false;
   sipAttr               = NULL;
	 mContactAddSpec       = NULL;

   mSipInviteHandler.reset();
   mSipByeHandler.reset();
   mSipInfoHandler.reset();
   mSipCancelHandler.reset();
   mSipNotifyHandler.reset();
   mSipOptionsHandler.reset();

   miPrivacyMask = 0;

   memset(paiTelUri, '\0', 300);
   memset(paiSipUri, '\0', 300);

	 mpCall = NULL;
   mbIsDead = false;
   msSelfConnId = 0;
	 msLocalCallID.clear(); 

   mChangeData.meMajorState = CONNECTION_CREATED;
   mChangeData.mGwIPAddress[0] = '\0';
   mChangeData.miGwPort = -1;
	 mChangeData.mContactHdr[0] = '\0';

   mConstData.mOriginatingAddr.initObject(false);
   mConstData.mDialedAddr.initObject(false);
   mConstData.mTargetAddr.initObject(false);

	 mTcapTransInfo.reset();
	 
}

void INGwSpSipConnection::reset(void) 
{
   logger.logINGwMsg(false, TRACE_FLAG, 0, "reset local callid <%s>", 
                   msLocalCallID.c_str());

   mActiveSessionTimerId = 0;
   miFailureRespCode = 0;

   if(mpCall != NULL)
   {
      mpCall->releaseRef();
   }

   mpCall = NULL;

   if(NULL != sipAttr)
   {
      delete sipAttr;
      sipAttr = NULL;
   }
}

INGwSpSipConnection::~INGwSpSipConnection()
{
	 reset();
}

void INGwSpSipConnection::releaseRef(void)
{

#ifdef USE_LOCK_FOR_REF_COUNT
   pthread_mutex_lock(&mRefMutex);
#endif

   msRefCount--;

   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "releaseRef conn: Id [%x][%s], "
                   "RefCount [%d]",
                   (INGwIfrUtlRefCount *)this, mId.c_str(), msRefCount);

   if(1 == msRefCount) 
   {
#ifdef USE_LOCK_FOR_REF_COUNT
      pthread_mutex_unlock(&mRefMutex);
#endif
      INGwSpSipConnectionFactory::getInstance().reuseObject(this);
      return;
   }
   else if(0 >= msRefCount) 
   {

#ifdef USE_LOCK_FOR_REF_COUNT
      pthread_mutex_unlock(&mRefMutex);
#endif

      delete this;
      return;
   }

#ifdef USE_LOCK_FOR_REF_COUNT
   pthread_mutex_unlock(&mRefMutex);
#endif

   return;
}

////////////////////////////////////////////////////////////////////////////////
// Method: stackCallbackRequest
// Description: This method is called by the stack interface layer.  This
//              method acts as a demultiplexer, handing over the received
//              request to the appropriate sip handler.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal INGwSpSipConnection::stackCallbackRequest
                (INGwSipMethodType             aMethodType      ,
                 Sdf_st_callObject      **ppCallObj        ,
                 Sdf_st_eventContext     *pEventContext    ,
                 Sdf_st_error            *pErr             ,
                 Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
   MARK_BLOCK("INGwSpSipConnection::stackCallbackRequest", TRACE_FLAG);

   Sdf_ty_retVal retval = Sdf_co_fail;
   INGwSipConnMinorState  connMinorState = getMinorState();

   logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "connminorstate [%d], methodtype "
                     "[%d]", 
                     connMinorState, aMethodType);

/*
 *   If this is a parallel transaction while another client transaction is going
 *   on, reject this request.  However, if the request is BYE or CANCEL, we
 *   still need to process it. 
 */

   if(connMinorState != CONN_MINSTATE_IDLE)
   {
      switch(aMethodType)
      {
         case INGW_SIP_METHOD_TYPE_INVITE   :
         // PR 50787
         // Will handle OPTIONS request if any client tranaction is in progress
/*
         case INGW_SIP_METHOD_TYPE_OPTIONS  :
*/
         case INGW_SIP_METHOD_TYPE_NOTIFY   :
         case INGW_SIP_METHOD_TYPE_INFO     :

         logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
                         "Terminating server transaction with method [%d] "
                         "since a transaction [%d] in progress.", aMethodType,
                         connMinorState);

         INGwSpMsgBaseHandler* siphandler = getSipHandler(aMethodType);
         siphandler->terminateServerTransaction(this, aMethodType, pOverlapTransInfo);
         return Sdf_co_success;
      } 
   }

   switch(aMethodType)
   {
      case INGW_SIP_METHOD_TYPE_INVITE   :
      {
         retval = mSipInviteHandler.stackCallbackRequest(this, aMethodType, 
                             ppCallObj, pEventContext, pErr, pOverlapTransInfo);
      }
      break;

      case INGW_SIP_METHOD_TYPE_CANCEL   :
      {
         retval = mSipCancelHandler.stackCallbackRequest(this, aMethodType, 
                             ppCallObj, pEventContext, pErr, pOverlapTransInfo);
      }
      break;

      case INGW_SIP_METHOD_TYPE_BYE      :
      {
         retval = mSipByeHandler.stackCallbackRequest(this, aMethodType, 
                             ppCallObj, pEventContext, pErr, pOverlapTransInfo);
      }
      break;

      case INGW_SIP_METHOD_TYPE_OPTIONS  :
      {
         retval = mSipOptionsHandler.stackCallbackRequest(this, aMethodType, 
                             ppCallObj, pEventContext, pErr, pOverlapTransInfo);
      }
			break;

      case INGW_SIP_METHOD_TYPE_NOTIFY   :
      {
         retval = mSipNotifyHandler.stackCallbackRequest(this, aMethodType, 
                             ppCallObj, pEventContext, pErr, pOverlapTransInfo);
         logger.logINGwMsg(false,TRACE_FLAG,0, "+rem+ INGwSipConnection seqNum<%d>", 
                                              mSipNotifyHandler.mSeqNum);
      }
      break;

      case INGW_SIP_METHOD_TYPE_INFO     :
      {
         retval = mSipInfoHandler.stackCallbackRequest(this, aMethodType, 
                             ppCallObj, pEventContext, pErr, pOverlapTransInfo);
      }
      break;

      default:
      {
         logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
                         "Unrecognized method type [%d]", aMethodType);
         pErr->errCode = Sdf_en_invalidTypeError;
         sprintf(pErr->ErrMsg, "Unrecognized method type [%d]", aMethodType);
      }
      break;
   } 

   logger.logINGwMsg(false, TRACE_FLAG, 0, "Returning [%d]", retval);
   return retval;
} 

////////////////////////////////////////////////////////////////////////////////
// Method: stackCallbackResponse
// Description: This method is called by the stack interface layer, when it
//              receives a response.  This method acts like a demultiplexer,
//              distributing the received response to the appropriate sip
//              handler.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal INGwSpSipConnection::stackCallbackResponse
                (INGwSipMethodType        aMethodType      ,
                 int                      aRespCode        ,
                 Sdf_st_callObject      **ppCallObj        ,
                 Sdf_st_eventContext     *pEventContext    ,
                 Sdf_st_error            *pErr             ,
                 Sdf_st_overlapTransInfo *pOverlapTransInfo)
{
   MARK_BLOCK("INGwSpSipConnection::stackCallbackResponse", TRACE_FLAG);

   logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
									 "INGwSpSipConnection::stackCallbackResponse: respcode <%d>, methodtype <%d>", 
									 aRespCode, aMethodType);

  Sdf_ty_retVal retval = Sdf_co_fail;

  switch(aMethodType)
  {
    case INGW_SIP_METHOD_TYPE_INVITE   :
      retval = mSipInviteHandler.stackCallbackResponse
                 (this , aMethodType, aRespCode, ppCallObj,
                  pEventContext, pErr, pOverlapTransInfo);
      break;
    case INGW_SIP_METHOD_TYPE_CANCEL   :
      retval = mSipCancelHandler.stackCallbackResponse
                 (this, aMethodType, aRespCode, ppCallObj, pEventContext,
                  pErr       , pOverlapTransInfo);
      break;
    case INGW_SIP_METHOD_TYPE_BYE      :
      retval = mSipByeHandler.stackCallbackResponse
                 (this, aMethodType, aRespCode, ppCallObj, pEventContext,
                  pErr       , pOverlapTransInfo);
      break;
    case INGW_SIP_METHOD_TYPE_OPTIONS  :
      retval = mSipOptionsHandler.stackCallbackResponse
                 (this, aMethodType, aRespCode, ppCallObj, pEventContext,
                  pErr       , pOverlapTransInfo);
			break;
    case INGW_SIP_METHOD_TYPE_NOTIFY   :
      retval = mSipNotifyHandler.stackCallbackResponse
                 (this, aMethodType, aRespCode, ppCallObj, pEventContext,
                  pErr       , pOverlapTransInfo);
      break;
    case INGW_SIP_METHOD_TYPE_INFO     :
      retval = mSipInfoHandler.stackCallbackResponse
                 (this, aMethodType, aRespCode, ppCallObj, pEventContext,
                  pErr       , pOverlapTransInfo);
      break;
    default:
      pErr->errCode = Sdf_en_invalidTypeError;
      sprintf(pErr->ErrMsg, "Unrecognized method type <%d>", aMethodType);
      break;
  } // End of switch

  return retval;
} // End of stackCallbackResponse

////////////////////////////////////////////////////////////////////////////////
// Method: stackCallbackAck
// Description: This method is called by the stack interface layer when an ACK
//              is received.  This method hands over the ACK to the INVITE
//              handler.
////////////////////////////////////////////////////////////////////////////////
Sdf_ty_retVal INGwSpSipConnection::stackCallbackAck
                (INGwSipMethodType        aMethodType      ,
                 Sdf_st_callObject      **ppCallObj        ,
                 Sdf_st_eventContext     *pEventContext    ,
                 Sdf_st_error            *pErr             )
{
  MARK_BLOCK("INGwSpSipConnection::stackCallbackAck", TRACE_FLAG);
  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
									"INGwSpSipConnection::stackCallbackAck: methodtype <%d>", aMethodType);
  Sdf_ty_retVal retval = Sdf_co_fail;

  switch(aMethodType)
  {
    case INGW_SIP_METHOD_TYPE_INVITE:
      logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "INGwSpSipConnection::stackCallbackAck: "
											"calling ack callback on INVITE handler\n");
      retval = mSipInviteHandler.stackCallbackAck
                 (this, aMethodType, ppCallObj, pEventContext, pErr);
      break;
    default:
      logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "INGwSpSipConnection::stackCallbackAck: "
												"Unrecognized method type <%d>\n", aMethodType);
      pErr->errCode = Sdf_en_invalidTypeError;
      sprintf(pErr->ErrMsg, "Unrecognized method type <%d>", aMethodType);
      break;
  } // End of switch

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT INGwSpSipConnection::stackCallbackAck\n");
  return retval;
} // End of stackCallbackAck

void INGwSpSipConnection::handleConnectionFailure()
{
}

////////////////////////////////////////////////////////////////////////////////
// Method: mSendResponse
// Description: This method is called when a response is to be sent
//              to the appropriate handler.
// IN  - aIpData: Contains the IP data (message body and an optional message).
//                The message body is needed to form the response.
//
// Return Value: int - returns a 0 if success, -1 otherwise.
////////////////////////////////////////////////////////////////////////////////
int INGwSpSipConnection::mSendResponse(INGwSipMethodType      aMethodType,
                                   INGwSpData         *aIpData,
                                   int               aRespCode)
{
  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "IN mSendResponse");
  logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
									 "INGwSpSipConnection::mSendResponse: "
									 "methodtype <%d>, respcode <%d>,  connmode <%d>", 
									 aMethodType, aRespCode, 0);

  int retval = 0;

  switch(aMethodType)
  {
    case INGW_SIP_METHOD_TYPE_INVITE   :
      retval = mSipInviteHandler.mSendResponse
                 (this, aMethodType, aIpData, aRespCode);
      break;
    case INGW_SIP_METHOD_TYPE_CANCEL   :
      retval = mSipCancelHandler.mSendResponse
                 (this, aMethodType, aIpData, aRespCode);
      break;
    case INGW_SIP_METHOD_TYPE_BYE      :
      retval = mSipByeHandler.mSendResponse
                 (this, aMethodType, aIpData, aRespCode);
      break;
    case INGW_SIP_METHOD_TYPE_OPTIONS  :
      retval = mSipOptionsHandler.mSendResponse
                 (this, aMethodType, aIpData, aRespCode);
			break;
    case INGW_SIP_METHOD_TYPE_NOTIFY   :
      retval = mSipNotifyHandler.mSendResponse
                 (this, aMethodType, aIpData, aRespCode);
      break;
		case INGW_SIP_METHOD_TYPE_INFO     :
			retval = mSipInfoHandler.mSendResponse
									(this, aMethodType, aIpData, aRespCode);
      break;
    default:
      retval = -1;
      break;
  } // End of switch

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT mSendResponse");
  return retval;
} // End of mSendResponse

////////////////////////////////////////////////////////////////////////////////
// Method: mSendAck
// Description: This method is called by a peer connection  when a ACK is
//              received at its side.  This method hands over the information
//              to the INVITE handler.
// IN  - aIpData: Contains the IP data (optional message body and an optional
//                message).  The message body may be needed to form the ACK.
//
// Return Value: int - returns a 0 if success, -1 otherwise.
////////////////////////////////////////////////////////////////////////////////
int INGwSpSipConnection::mSendAck(INGwSipMethodType aMethodType, INGwSpData *aIpData)
{
  MARK_BLOCK("INGwSpSipConnection::mSendAck", TRACE_FLAG);
  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
										"INGwSpSipConnection::mSendAck: methodtype <%d>", aMethodType);


  if(aMethodType == INGW_SIP_METHOD_TYPE_INVITE)
    return mSipInviteHandler.mSendAck(this,
                                        INGW_SIP_METHOD_TYPE_INVITE,
                                        aIpData);

  return -1;
} // end of mSendAck

////////////////////////////////////////////////////////////////////////////////
// Method: mSendRequest
// Description: The appropriate handler is informed of a request reception.
//
// IN  - aGwData: Contains the IP data of the peer, with an options message and
//                an optional message body.
//
// Return Value: int - returns 0 if success, -1 otherwise.
////////////////////////////////////////////////////////////////////////////////
int INGwSpSipConnection::mSendRequest(INGwSipMethodType aMethodType, 
  INGwSpData *aIpData,
  Sdf_st_overlapTransInfo* peerOverlapTxInfo)
{
  MARK_BLOCK("INGwSpSipConnection::mSendRequest", TRACE_FLAG);
  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
										"INGwSpSipConnection::mSendRequest: methodtype <%d>", aMethodType);
  int retval = 0;

  switch(aMethodType)
  {
    case INGW_SIP_METHOD_TYPE_INVITE   :
      retval = mSipInviteHandler.mSendRequest
                 (this, aMethodType, aIpData);
      break;
    case INGW_SIP_METHOD_TYPE_CANCEL   :
      retval = mSipCancelHandler.mSendRequest
                 (this, aMethodType, aIpData);
      break;
    case INGW_SIP_METHOD_TYPE_BYE      :
      retval = mSipByeHandler.mSendRequest
                 (this, aMethodType, aIpData);
      break;
    case INGW_SIP_METHOD_TYPE_OPTIONS  :
      retval = mSipOptionsHandler.mSendRequest
                 (this, aMethodType, aIpData);
			break;
    case INGW_SIP_METHOD_TYPE_NOTIFY   :
      retval = mSipNotifyHandler.mSendRequest
                 (this, aMethodType, aIpData);
      break;
    case INGW_SIP_METHOD_TYPE_INFO     :
      mSipInfoHandler.setLastOperation(LASTOP_PASSTHROUGH_INFO);
      retval = mSipInfoHandler.mSendRequest
                 (this, aMethodType, aIpData);
      break;
    default:
      logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
												"INGwSpSipConnection::mSendRequest: Unrecognized methodtype <%d>", 
												aMethodType);
      retval = -1;
      break;
  } // end of switch

  return retval;
}
int INGwSpSipConnection::mSendRPR(INGwSipMethodType      aMethodType,
                              INGwSpData         *aIpData    ,
                              int               aRespCode)
{
	// Not supported
  MARK_BLOCK("INGwSpSipConnection::mSendRPR", TRACE_FLAG);
  int l_retVal = 0;
  return l_retVal;
}

void INGwSpSipConnection::indicateTimeout
  (INGwSipMethodType              aMethodType,
   INGwSipTimerType::TimerType aType      ,
   unsigned int              aTimerid   )
{
  MARK_BLOCK("INGwSpSipConnection::indicateTimeout", TRACE_FLAG);
  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
									"INGwSpSipConnection::indicateTimeout: methodtype <%d>, connection <%s>", 
									aMethodType, getLocalCallId().c_str());
  switch(aMethodType)
  {
    case INGW_SIP_METHOD_TYPE_INVITE   :
      mSipInviteHandler.indicateTimeout(this, aMethodType, aType, aTimerid);
      break;
    case INGW_SIP_METHOD_TYPE_CANCEL   :
      mSipCancelHandler.indicateTimeout(this, aMethodType, aType, aTimerid);
      break;
    case INGW_SIP_METHOD_TYPE_BYE      :
      mSipByeHandler.indicateTimeout(this, aMethodType, aType, aTimerid);
      break;
    case INGW_SIP_METHOD_TYPE_OPTIONS  :
      mSipOptionsHandler.indicateTimeout(this, aMethodType, aType, aTimerid);
      break;
    case INGW_SIP_METHOD_TYPE_INFO     :
      mSipInfoHandler.indicateTimeout(this, aMethodType, aType, aTimerid);
      break;
    case INGW_SIP_METHOD_TYPE_NOTIFY:
      mSipNotifyHandler.indicateTimeout(this, aMethodType, aType, aTimerid); 
      break;
    default:
      logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
												"INGwSpSipConnection::indicateTimeout: unrecognized method <%d>", 
												aMethodType);
      break;
  } // end of switch


} // end of indicateTimeout

INGwSipMethodType INGwSpSipConnection::getLastMethod() const
{
  return meLastMethod;
}

void INGwSpSipConnection::setLastMethod(INGwSipMethodType aMethodType)
{
  meLastMethod = aMethodType;
}

Sdf_st_callObject*  INGwSpSipConnection::getHssCallObject() const
{
  return mHssCallLeg;
}

void INGwSpSipConnection::setHssCallObject(Sdf_st_callObject *aConnection)
{
  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "IN setHssCallObject");
  mHssCallLeg = aConnection;
  if (NULL == mHssCallLeg) {
    logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "mHssCallLeg has been set as NULL.");
  } else {
    logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "mHssCallLeg has been set as <%x>", mHssCallLeg);
  }
  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "OUT setHssCallObject");
}

void INGwSpSipConnection::setMinorState(INGwSipConnMinorState aNewState)
{
  logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
									"INGwSpSipConnection::setMinorState: state-change : %d, connection <%s>", 
									(int)aNewState, getLocalCallId().c_str());
  meConnMinorState = aNewState;
}

short INGwSpSipConnection::disconnect(int errCode)
{
  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "IN disconnect");

  logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0,
    "Self ConnId:<%d> ", msSelfConnId);



  return mSipByeHandler.disconnect(this, errCode);

	return 0;

}

std::string INGwSpSipConnection::toLog(void) const
{
   std::string  ret="";
   ret += "\nSipConnection part:\nMessages.\n";

   char *local = new char[500];
   
   sprintf(local, "Active session TimerID:[%d] RPR supported:[%d]\n"
                  "Minor state:[%d] lastMethod:[%d] SerializationReqd:[%d]\n"
                  "Incoming GWPort[%d]\n", 
           mActiveSessionTimerId, 0, meConnMinorState, 
           meLastMethod, mbStackSerReqdFlag, miIncomingGwPort);

   ret += local;
   delete []local;
   ret += mSipInviteHandler.toLog();
   ret += mSipByeHandler.toLog();
   ret += mSipInfoHandler.toLog();
   ret += mSipCancelHandler.toLog();
   ret += mSipNotifyHandler.toLog();
   ret += mSipOptionsHandler.toLog();

   return ret;
}

short INGwSpSipConnection::sendOptions(const char *aIpAddr, const int aPort)
{
  return -1;
}

short INGwSpSipConnection::sendInfo(INGwSpData &aIpData)
{
  return -1;
}

short INGwSpSipConnection::continueProcessing(short originator)
{
  MARK_BLOCK("INGwSpSipConnection::continueProcessing", TRACE_FLAG);
  logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, "Op orig <%d>", (int)originator);
  short retval = 0;

  INGwSipConnMajorState majorstate = getMajorState();
  bool isdead = isDead();
  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
									"INGwSpSipConnection::continueProcessing: "
									"connmajorstate <%d>, isdead <%d>, connection <%s>", 
									majorstate, isdead, getLocalCallId().c_str());


  switch(majorstate)
  {

    case CONNECTION_DISCONNECTED:
    {
      int causecode = 0;
      if(ISFAILRESP(miFailureRespCode))
        causecode = miFailureRespCode;

      if(originator == OP_ORG_SERVICE)
      {
        retval = mSipByeHandler.disconnect(this, causecode);
      }
      else
      {
         if(1)
         {
            if(!isDead())
            {
               // Disconnected will be raised on receiving Bye or 200Ok for Bye.
               // In case of bye received with CP we have to clean the call.
               // In case of 200 for bye with CP we have to disconnect. 
               // Which internally cleans the call.

               if(mSipByeHandler.mByeStateContext.mbByeCompleted)
               {
                  retval = mSipByeHandler.disconnect(this, causecode);
               }
               else
               {
                  getCall().release(causecode);
               }
            }
            else
            {
               getCall().removeConnection(getSelfConnectionId());
               retval = mSipByeHandler.disconnect(this, causecode);
            }
         }
      }
    }
    break;

    case CONNECTION_FAILED:
    {
      int causecode = 0;
      if(ISFAILRESP(miFailureRespCode))
        causecode = miFailureRespCode;

      logger.logINGwMsg(mLogFlag, TRACE_FLAG, imERR_NONE, 
												"continueProcessing: causecode <%d>", causecode);

      // If the connection failed, then this connection has to be removed.
      // If the continue processing happened from the provider, then the rest
      // of the connections and call should also go.
      if(!isDead())
      {
        INGwSpSipCall &call = getCall();
        logger.logINGwMsg(call.mLogFlag, TRACE_FLAG, 0, 
												 "continueProcessing: removing connection from call");

        int numconn = call.removeConnectionOnly(getSelfConnectionId());

        logger.logINGwMsg(call.mLogFlag, TRACE_FLAG, 0, 
												 "continueProcessing: originator <%d>, FROM_SELF <%d>, OP_ORG_SLEE <%d>",
												 originator, FROM_SELF, OP_ORG_SLEE);

        if((originator == FROM_SELF) || (originator == OP_ORG_SLEE))
        {
           logger.logINGwMsg(call.mLogFlag, TRACE_FLAG, 0, 
														 "continueProcessing: In the release if condition");
           call.release(causecode);
        }
        INGwSpSipUtil::releaseConnection(this);
      }
      else
      {
         //Continue processing for the dead connection.
         INGwSpSipCall &call = getCall();
         logger.logINGwMsg(call.mLogFlag, TRACE_FLAG, 0, 
                         "continueProcessing on deadConnection. Clearing up.");
         INGwSpSipUtil::releaseConnection(this);
      }
      break;
    }
    case CONNECTION_CONNECTED:
      break;

    default:
      logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
												"continueProcessing: Unknown state <%d>\n", getMajorState());

      break;
  } // end of switch


  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT INGwSpSipConnection::continueProcessing\n");

  return retval;
} // end of continueProcessing

INGwSipConnMinorState INGwSpSipConnection::getMinorState() const
{
  return meConnMinorState;
}

int INGwSpSipConnection::getProviderSpecificMinorState() const
{
  return (int)meConnMinorState;
}

SipUrl*
INGwSpSipConnection::getInvReqLine() {

  return mSipInviteHandler.m_oInvReqLineUrl;
}

INGwSpMsgBaseHandler* INGwSpSipConnection::getSipHandler(INGwSipMethodType aMethodType)
{

				
  INGwSpMsgBaseHandler *handler = NULL;
  switch(aMethodType)
  {
    case INGW_SIP_METHOD_TYPE_INVITE   :
      handler = (INGwSpMsgBaseHandler *)&mSipInviteHandler ; break;
    case INGW_SIP_METHOD_TYPE_CANCEL   :
      handler = (INGwSpMsgBaseHandler *)&mSipCancelHandler ; break;
    case INGW_SIP_METHOD_TYPE_BYE      :
      handler = (INGwSpMsgBaseHandler *)&mSipByeHandler    ; break;
    case INGW_SIP_METHOD_TYPE_OPTIONS  :
      handler = (INGwSpMsgBaseHandler *)&mSipOptionsHandler; break;
    case INGW_SIP_METHOD_TYPE_NOTIFY   :
      handler = (INGwSpMsgBaseHandler *)&mSipNotifyHandler ; break;
    case INGW_SIP_METHOD_TYPE_INFO     :
      handler = (INGwSpMsgBaseHandler *)&mSipInfoHandler   ; break;
  }
  return handler;
} // end of getSipHandler


////////////////////////////////////////////////////////////////////////////////
// This method is used to clone a sip connection if the previous connection
// got timed out mid-transaction because of an app-timer.  That connection (and
// its associated call leg) will not be usable, since the stack timeout is
// necessary for that.
// So here we make a copy of the connection, with most fields (including the
// connection id) borrowed from the previous connection.
////////////////////////////////////////////////////////////////////////////////
INGwSpSipConnection* INGwSpSipConnection::clone()
{
  MARK_BLOCK("INGwSpSipConnection::clone", TRACE_FLAG);
  if(isDead())
  {
    logger.logINGwMsg(false, TRACE_FLAG, 0, "connection is dead.  Cannot clone.");
    logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT clone");
    return NULL;
  }

  INGwSpSipConnection* clone = dynamic_cast<INGwSpSipConnection*>(
                     INGwSpSipProvider::getInstance().getNewConnection(getCall()));

  // Copy ALL the states and handlers to the new connection.


	clone->msSelfConnId = msSelfConnId;
	clone->mbIsDead     = mbIsDead;

	clone->mChangeData = mChangeData;
	clone->mConstData  = mConstData;

	clone->mSipInviteHandler.mInviteStateContext   = mSipInviteHandler.mInviteStateContext   ;

	mSipInviteHandler.mInviteStateContext.mNoAnsTimerid = 0;

  clone->mHssCallLeg      = NULL;
  clone->meConnMinorState = CONN_MINSTATE_IDLE;

  return clone;
} // end of clone

void
INGwSpSipConnection::unlockResource() {

  Sdf_st_error l_err;

  logger.logINGwMsg (false, VERBOSE_FLAG, 0,
   "UnLocking the HSS Call Object Resource");

  sdf_ivk_uaUnlockCallObject(mHssCallLeg, &l_err);
}

void
INGwSpSipConnection::lockResource() {

 Sdf_st_error l_err;

 logger.logINGwMsg (false, VERBOSE_FLAG, 0,
   "Locking the HSS Call Object Resource");

 if (mHssCallLeg) {
   sdf_ivk_uaLockCallObject(mHssCallLeg, &l_err);
 } else {
   logger.logINGwMsg (false, VERBOSE_FLAG, 0,
   "mHssCallLeg - the HSS Call Object Resource - is NULL");
 }
}

/*----------------------------------------------------------------
 | Function: setSessionTimerInfo
 | Desc:
 |
 |
 |----------------------------------------------------------------
*/

int INGwSpSipConnection::setSessionTimerInfo(Sdf_st_callObject* pCallObject, 
                                         bool isResponse) 
{
   logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "IN setSessionTimerInfo");

   Sdf_st_error l_err;

   if(!isResponse) 
   {
      if(0 != pCallObject) 
      {
         INGwSpSipUtil::addSupportedHdr((SIP_S8bit*)"Supported: timer", 
                                    pCallObject);
      }

      // BPInd08203 - Start
			m_MinSessionInterval = INGwSpSipProviderConfig::getMinSeValue();
			m_MaxSessionInterval = INGwSpSipProviderConfig::getMaxSeValue();
      sdf_ivk_uaSetSessionInterval(pCallObject, m_MaxSessionInterval, &l_err);
			// BPInd08203 - End

      sdf_ivk_uaSetRefresher(pCallObject, Sdf_en_refresherRemote, &l_err);
      sdf_ivk_uaSetMinSe(pCallObject, m_MinSessionInterval, &l_err);

   	  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
				"Session timer set for [%d] and Min-SE set for [%d]. IsIvrConn[%d]", 
				m_MaxSessionInterval, m_MinSessionInterval, 0);
   }
   else 
   {
    //This is a response, copy stuff from pSessionTimerInfo struct. 
    //Done by UA-Toolkit.
   }

   logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT setSessionTimerInfo");
   return true;
}

void INGwSpSipConnection::setVersionDetail()
{
#if 0

   _verHolder = VersionMgr::getInstance().getVersionHolder(
                       VersionMgr::VER_SIP_PROV_SER, VersionMgr::CCM_SUBSYSTEM);
   _verHolder->addSupportedVersion(1);
#endif
}

bool INGwSpSipConnection::serialize
  (unsigned char*       apcData,
   int                  aiOffset,
   int&                 aiNewOffset,
   int                  aiMaxSize,
   bool                 abForceFullSerialization)
{
   MARK_BLOCK("INGwSpSipConnection::serialize", TRACE_FLAG)

#if 0
   if(_verHolder->getSendVersion() != 1)
   {
      return false;
   }
#endif

   aiNewOffset = aiOffset;

   if(operType == REP_DELETE)
   {
      if(aiMaxSize <= (aiNewOffset + 1))
      {
         logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "Buffer size not enough.");
         return false;
      }

      apcData[aiNewOffset] = (unsigned char)operType;
      aiNewOffset++;
      return true;
   }

   if(aiMaxSize <= (aiNewOffset + 2))  //char + char
   {
      logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "Buffer size not enough.");
      return false;
   }

	 // BPInd12721 : [
	 if( (abForceFullSerialization) )// || (SERIALIZATION_PENDING == getSerializationPendingFlag() ) )
	 // : ]
   {
      apcData[aiNewOffset] = (unsigned char)INGwIfrUtlSerializable::REP_CREATE;
      aiNewOffset++;
      apcData[aiNewOffset] = (unsigned char)INGwIfrUtlSerializable::FULL_SER_REQD;
      aiNewOffset++;
   }
   else
   {
      apcData[aiNewOffset] = (unsigned char)operType;
      aiNewOffset++;
      apcData[aiNewOffset] = (unsigned char)meSerType;
      aiNewOffset++;
   }

	 if(meSerType == SER_NOT_REQD)
   {
      apcData[aiNewOffset - 1] = (unsigned char)INGwIfrUtlSerializable::SER_NOT_REQD;
      return true;
   }

   SerializationType locSerType = meSerType;

	 if(aiMaxSize <= (aiNewOffset + sizeof(ChangeableConnectionData) ) )
	 {
			logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "Buffer size not enough.");
			LogTrace(0, "OUT serialize");
			return false;
	 }

	 logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
										"Serializing Changeable data Offset is <%d>.",
										aiNewOffset);

	 aiNewOffset += INGwIfrUtlSerializable::serializeStruct(&mChangeData, 
																													sizeof(ChangeableConnectionData),
																													(char *)(apcData + aiNewOffset));



	 if(aiMaxSize <= (aiNewOffset + sizeof(ConstConnectionData) ) )
	 {
			logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "Buffer size not enough.");
			LogTrace(0, "OUT serialize");
			return false;
	 }

	 logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
										"Serializing Const Conn data Offset is <%d>.",
										aiNewOffset);

	 aiNewOffset += INGwIfrUtlSerializable::serializeStruct(&mConstData, 
																													sizeof(ConstConnectionData),
																													(char *)(apcData + aiNewOffset));

	 logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
										"Serializing local call Id Offset is <%d>.",
										aiNewOffset);

	 if(aiMaxSize <= (aiNewOffset + 4 + msLocalCallID.length() ) )
	 {
			logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "Buffer size not enough.");
			LogTrace(0, "OUT serialize");
			return false;
	 }

	 aiNewOffset += INGwIfrUtlSerializable::serializeVLString(msLocalCallID, 
																														(char *)(apcData + aiNewOffset));
  // SDP serialization not required
/*
   if (INGwSpSipProviderConfig::isSdpSerializationReqd ())
   {
     if(mSelfData)
     {
        apcData [aiNewOffset] = '1';
        aiNewOffset++;
        if (mSelfData->serialize (apcData, aiNewOffset, aiNewOffset, aiMaxSize,
                                abForceFullSerialization) == false)
        {
          logger.logINGwMsg(false, WARNING_FLAG, 0,
            "SDP serialization failed");
        }
     }
     else
     {
        apcData [aiNewOffset] = '0';
        aiNewOffset++;
     }
  
     if(mPeerData)
     {
        apcData [aiNewOffset] = '1';
        aiNewOffset++;
        if (mPeerData->serialize (apcData, aiNewOffset, aiNewOffset, aiMaxSize,
                                abForceFullSerialization) == false)
        {
          logger.logINGwMsg(false, WARNING_FLAG, 0,
            "SDP serialization failed");
        }
     }
     else
     {
        apcData [aiNewOffset] = '0';
        aiNewOffset++;
     }
   }
*/
   //Check for mbStackReqdFlag and avoid serialize stack obj.
   // Encode the hss call object

	 logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
										"Serializing HSS Call Obj. Offset is <%d>.",
										aiNewOffset);
   Sdf_st_callObject* tempCallObj = getHssCallObject();
   Sdf_st_error sdferror;

   if(NULL == tempCallObj)
   {
      logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "Serialization Failed: " 
                      "SIP Stack Call Object is NULL.");
      return false;
   }
   // Moving offset 2 byte forward so hss obj size can be inserted afterwards 
   aiNewOffset += 2;
   unsigned short outLen = 0;

   if(mbStackSerReqdFlag)
   {
      if(abForceFullSerialization || (locSerType == FULL_SER_REQD))
      {
         // Serialize the hss call object. Place the buffer in thread specific 
         // data, so that HAmemget function can access it.

         INGwSpSipProvider::getInstance().getThreadSpecificSipData().
                  setStackSerializationContext(apcData, aiNewOffset, aiMaxSize);
 
         unsigned char* outBuf = NULL;
         unsigned short outOff = 0;

         Sdf_ty_retVal status = 
               sdf_ivk_uaSerializeEntireObject(Sdf_en_callObject, tempCallObj,
                                               Sdf_co_false, &outBuf, &outLen,
                                               &outOff, &sdferror);

         // Reset the serialization context
         INGwSpSipProvider::getInstance().getThreadSpecificSipData().
                                     setStackSerializationContext(NULL, -1, -1);

         if(status != Sdf_co_success)
         {
            logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "INGwSpSipConnection::serialize: Error "
                                         "serializing the hss call object");
            INGwSpSipUtil::checkError(status, sdferror);
            return false;
         }
         else
         {
            logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
                       "INGwSpSipConnection::serialize: Successfully serialized the"
                       " call object.  Offset <%d>, length <%d>", 
                       (int)outOff, (int)outLen);

         }
 
      }
      else //Serialization type is INC.
      {
         INGwSpSipProvider::getInstance().getThreadSpecificSipData().
                  setStackSerializationContext(apcData, aiNewOffset, aiMaxSize);
 
         //Serialize incrementally. To be done.
         Sdf_ty_callObjectFieldId fields[9];

         fields[0] = Sdf_en_coCmnInfo_remoteCseqId;
         fields[1] = Sdf_en_coCmnInfo_localCseqId;
         fields[2] = Sdf_en_coCmnInfo_localRseqId;
         fields[3] = Sdf_en_coCmnInfo_remoteRseqId;
         fields[4] = Sdf_en_coCmnInfo_localRPRCseqId;
         fields[5] = Sdf_en_coCmnInfo_remoteRPRCseqId;
         fields[6] = Sdf_en_coOLTxn_rseqId;
         fields[7] = Sdf_en_coOLTxn_localCseqId;
         fields[8] = Sdf_en_coOLTxn_remoteCseqId;

         unsigned char* outBuf = NULL;
         unsigned short outOff = 0;
         unsigned short dummy  = 0;
         Sdf_ty_retVal status = 
               sdf_ivk_uaSendPartialCriticalData(Sdf_en_callObject, tempCallObj,
                                                 fields, 9, Sdf_en_sendNow, 
                                                 Sdf_co_false, &outBuf, &outLen,
                                                 &outOff, &sdferror);

         // Reset the serialization context
         INGwSpSipProvider::getInstance().getThreadSpecificSipData().
                                     setStackSerializationContext(NULL, -1, -1);

         if(status != Sdf_co_success)
         {
            logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "INGwSpSipConnection::serialize: Error "
                                         "serializing the hss call object");
            INGwSpSipUtil::checkError(status, sdferror);
            return false;
         }
         else
         {
            logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, 
                       "INGwSpSipConnection::serialize: Successfully serialized the"
                       " call object Incrementally.  Offset <%d>, length <%d>", 
                       (int)outOff, (int)outLen);
         }
      }
   }

   // Encode the length of serialized stack data
   aiNewOffset -= 2;
   aiNewOffset += INGwIfrUtlSerializable::serializeShort(outLen, 
                                               (char *)(apcData + aiNewOffset));
   aiNewOffset += outLen;

	 logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
										"Serialized HSS Call Obj.Stack data <%d> Current Offset is <%d>.",
										outLen, aiNewOffset);
   return true;
}

bool INGwSpSipConnection::deserialize
  (const unsigned char* apcData,
   int                  aiOffset,
   int&                 aiNewOffset,
   int                  aiMaxSize)
{
   MARK_BLOCK("INGwSpSipConnection::deserialize", TRACE_FLAG)
#if 0
   if(_verHolder->getRecvVersion() != 1)
   {
      return false;
   }
#endif

   aiNewOffset = aiOffset;

   ReplicationOperationType reptype = 
                                (ReplicationOperationType) apcData[aiNewOffset];
   aiNewOffset++;  

   SerializationType serType = (SerializationType) apcData[aiNewOffset];
   aiNewOffset++;

	 if(aiMaxSize <= (aiNewOffset + sizeof(ChangeableConnectionData) ) )
	 {
			logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "Buffer size not enough.");
			LogTrace(0, "OUT deserialize");
			return false;
	 }

	 logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
										"Deserializing Changeable data Offset is <%d>.",
										aiNewOffset);

	 aiNewOffset += INGwIfrUtlSerializable::deserializeStruct((char *)(apcData + aiNewOffset), 
																													  &mChangeData, 
																													  sizeof(ChangeableConnectionData));


	 if(aiMaxSize <= (aiNewOffset + sizeof(ConstConnectionData) ) )
	 {
			logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "Buffer size not enough.");
			LogTrace(0, "OUT deserialize");
			return false;
	 }

	 logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
										"Deserializing Const Conn data Offset is <%d>.",
										aiNewOffset);

	 aiNewOffset += INGwIfrUtlSerializable::deserializeStruct((char *)(apcData + aiNewOffset), 
																														&mConstData, 
																													  sizeof(ConstConnectionData));

	 logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
										"deserializing local call Id Offset is <%d>.",
										aiNewOffset);

	 aiNewOffset += INGwIfrUtlSerializable::deserializeVLString((char *)(apcData + aiNewOffset), 
																															msLocalCallID);

	 if(aiMaxSize <= aiNewOffset )
	 {
			logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "Buffer size not enough.");
			LogTrace(0, "OUT deserialize");
			return false;
	 }

  // SDP serialization not required
/*
   if (INGwSpSipProviderConfig::isSdpSerializationReqd ())
   {
     if(apcData[aiNewOffset] == '1')
     {
       aiNewOffset++;
 
       if(mSelfData == NULL)
       {
          mSelfData = INGwSpDataFactory::getInstance().getNewObject();
       }
 
       if (!mSelfData->deserialize (apcData, aiNewOffset, aiNewOffset, aiMaxSize))
       {
         logger.logINGwMsg(false, WARNING_FLAG, 0,
           "SDP de-serialization failed");

         return false;
       }
     }
     else
     {
        aiNewOffset++;
        logger.logINGwMsg(false, VERBOSE_FLAG, 0,
           "Self SDP not serialized");
     }
 
     if(apcData[aiNewOffset] == '1')
     {
       aiNewOffset++;

       if(mPeerData == NULL)
       {
          mPeerData = INGwSpDataFactory::getInstance().getNewObject();
       }

       if (!mPeerData->deserialize (apcData, aiNewOffset, aiNewOffset, aiMaxSize))
       {
         logger.logINGwMsg(false, WARNING_FLAG, 0,
           "SDP de-serialization failed");

         return false;
       }
     }
     else
     {
       aiNewOffset++;
       logger.logINGwMsg(false, VERBOSE_FLAG, 0,
         "Peer SDP not serialized");
     }

   }
*/
	 logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
										"Deserializing HSS Object. Offset is <%d>.",
										aiNewOffset);

   // Decode the stack serialization length;
   short stacklen = -1;
   aiNewOffset += INGwIfrUtlSerializable::deserializeShort(
                                     (char *)(apcData + aiNewOffset), stacklen);

   logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, "INGwSpSipConnection::deserialize: stack length "
                                  "<%d>", (int)stacklen);

   if(stacklen == 0)
   {
      logger.logINGwMsg(mLogFlag, VERBOSE_FLAG, 0, "Deserialization of the stack obj not "
                                     "reqd.");
      return true;
   }

   // Deserialize the hss call object
   Sdf_st_error sdferror;
   Sdf_ty_retVal status;

   Sdf_st_callObject* callobj = NULL;

   if(reptype == REP_CREATE)
   {
      sdf_ivk_uaInitCall(&callobj, &sdferror);
      sdf_ivk_uaSetInitDataInCallObject(callobj, pGlbProfile, &sdferror);
   }
   else
   {
      callobj = getHssCallObject();
   }

   sdf_ivk_uaDeserializeObject(Sdf_en_callObject, callobj,
                               (unsigned char *)(apcData + aiNewOffset),
                               &sdferror);

#if 0
   if(serType == FULL_SER_REQD)
   {
      sdf_ivk_uaReconstructObject(Sdf_en_callObject, callobj, &sdferror);
   }
#endif

   aiNewOffset += stacklen;
	 logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
										"Deserialized HSS Object. Offset is <%d>.",
										aiNewOffset);

   // Set the new call object as the hss call object in connection
   setHssCallObject(callobj);

   // Increment the local cseq number by a large number, so that if there
   // are mid-call unreplicated transactions and ft happens, the backup
   // ccm will be send out the next request with a wrong cseq number.
   if(callobj && callobj->pCommonInfo)
   {
     logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
											"Deserialization: local cseq before increment <%u>", 
											callobj->pCommonInfo->dLocalCseq);

     callobj->pCommonInfo->dLocalCseq += 10000;

     logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
											 "Deserialization: local cseq after increment <%u>", 
											 callobj->pCommonInfo->dLocalCseq);
   }

   if(callobj)
   {
     Sdf_st_overlapTransInfo *pOverlapTransInfo =
         INGwSpSipUtil::getLastOverlapTransInfo(callobj);
     if(pOverlapTransInfo)
     {
       logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
												"Deserialization: local overlap cseq before increment <%u>", 
												pOverlapTransInfo->dLocalCseq);

       pOverlapTransInfo->dLocalCseq += 10000;

       logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, 
												"Deserialization: local overlap cseq after increment <%u>", 
												pOverlapTransInfo->dLocalCseq);
     }
     else
     {
       logger.logINGwMsg(mLogFlag, WARNING_FLAG, 0, 
												"Deserialization: Error incrementing overlap local cseq.  "
												"Overlaptrans is absent");
     }

     if(!callobj->pCommonInfo)
       logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, 
												 "Deserialization: Error incrementing local cseq.  commoninfo absent");
   }

   // If this is a connection creation, re-create the connection context and
   // add the hss call object to the call object map.
   if(reptype == REP_CREATE)

   // If this is a connection creation, re-create the connection context and
   // add the hss call object to the call object map.
   if(reptype == REP_CREATE)
   {
      // Copy the local call id of the connection as the callid in the 
      // connection context.
			INGwConnectionContext* lConnectionContext = new INGwConnectionContext;

      const std::string &callid = getLocalCallId();

      strcpy(lConnectionContext->mCallId, callid.c_str());

      // Add to and fro references between call object and connection.
      callobj->pAppData->pData = (void *)lConnectionContext;
      lConnectionContext->mSipConnection   = this;
      lConnectionContext->mbUseGivenCallid = true;

      setHssCallObject(callobj);
      getRef();             // hsscallobj's ref of sipconn
      HSS_LOCKEDINCREF(callobj->dRefCount);// sipconn's ref of hsscallobj

      // Add the call object into the call table in the thread specific data.
      // This also warrants an increase in the reference count of the HSS 
      // call leg.
      if(!INGwSpSipProvider::getInstance().getThreadSpecificSipData().
                                         getCallTable().put(callid.c_str(), callid.size(), callobj))
      {
         logger.logINGwMsg(mLogFlag, ERROR_FLAG, 0, "INGwSpSipConnection::deserialize: Error "
                                      "adding call obj in call table");
         sdferror.errCode = Sdf_en_callObjectAccessError;
         strcpy(sdferror.ErrMsg, "Error adding call object in call tbl");
         return -1;
      }

      HSS_LOCKEDINCREF(callobj->dRefCount); // For placing call leg in map.

      // We are done using HSS call object
      sdf_ivk_uaFreeCallObject(&callobj);
   }
   return true;
}

void INGwSpSipConnection::cleanup(void)
{
   INGwSpSipUtil::releaseConnection(this);
   mpCall->removeConnection(msSelfConnId, true);
}

void
INGwSpSipConnection::setPrivacyMask (int aiMask)
{
  miPrivacyMask = aiMask;
}

int
INGwSpSipConnection::getPrivacyMask ()
{
  return miPrivacyMask;
}

INGwSpSipConnection* INGwSpSipConnection::getPeerSipConnection()
{
	return NULL;
}

const char * INGwSpSipConnection::getGwIPAddress(void) 
{ 
  return mChangeData.mGwIPAddress; 
}

void INGwSpSipConnection::setGwIPAddress(const char* apcIPAddress, int len) 
{
   if(NULL != apcIPAddress) 
   {
      if(len == -1)
      {
         strncpy(mChangeData.mGwIPAddress, apcIPAddress, SIZE_OF_IPADDR);
         mChangeData.mGwIPAddress[SIZE_OF_IPADDR] = '\0';
      }
      else
      {
         if(len > SIZE_OF_IPADDR)
         {
            logger.logMsg(ERROR_FLAG, 0, "Call[%s] GwIPAddress exceeds storage "
                                         " len[%d] max[%d]", 
                          getCallId().c_str(), len, SIZE_OF_IPADDR);
                          
            return;
         }

         strncpy(mChangeData.mGwIPAddress, apcIPAddress, len);
         mChangeData.mGwIPAddress[len] = '\0';
      }
   }

   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "CallID[%s] GwIP address [%s]", 
                   getCallId().c_str(), mChangeData.mGwIPAddress);
}

void INGwSpSipConnection::setGwPort(int aPort)
{
   logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "GwPort [%d]", aPort);
   mChangeData.miGwPort = aPort;
}

int INGwSpSipConnection::getGwPort() 
{
  return mChangeData.miGwPort;
}

const char* INGwSpSipConnection::getContactHdr(void) 
{
  return mChangeData.mContactHdr;
}

void INGwSpSipConnection::setContactHdr(const char* apcContactHdr, int len)
{
   if(NULL != apcContactHdr)
   {
      if(len == -1)
      {
         strncpy(mChangeData.mContactHdr, apcContactHdr, SIZE_OF_IPADDR);
         mChangeData.mContactHdr[SIZE_OF_IPADDR] = '\0';
      }
      else
      {
         if(len > SIZE_OF_IPADDR)
         {
            logger.logMsg(ERROR_FLAG, 0, "Call[%s] GwIPAddress exceeds storage "
                                         " len[%d] max[%d]",
                          getCallId().c_str(), len, SIZE_OF_IPADDR);

            return;
         }

         strncpy(mChangeData.mContactHdr, apcContactHdr, len);
         mChangeData.mContactHdr[len] = '\0';
      }
   }

   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "CallID[%s] Gw CONTACT [%s]",
                   getCallId().c_str(), mChangeData.mContactHdr);
}

const string& INGwSpSipConnection::getLocalCallId(void) { return msLocalCallID; }

void INGwSpSipConnection::setLocalCallId(const char *aStr)
{
	msLocalCallID = aStr;
	char localID[512];
	sprintf(localID, "%s<%d>", msLocalCallID.c_str(), msSelfConnId);
  INGwIfrUtlRefCount::mId = localID;
}

const std::string& INGwSpSipConnection::getCallId() const
{
	return mpCall->getCallId();
}

void INGwSpSipConnection::setCall(INGwSpSipCall* p_SipCall)
{
	if(mpCall != NULL)
	{
		 mpCall->releaseRef();
		 mpCall == NULL;
	}
	mpCall = p_SipCall;
	mpCall->getRef();
}

void 
INGwSpSipConnection::setMajorState(INGwSipConnMajorState p_NewMajorState) 
{
   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "Major State change for CallID[%s] "
										 "Old State is %d New State is %d",
                      getCallId().c_str(), (int)mChangeData.meMajorState, 
									    int(p_NewMajorState));

   mChangeData.meMajorState = p_NewMajorState;

}
INGwSipConnMajorState INGwSpSipConnection::getMajorState()const
{
	 return mChangeData.meMajorState;
}

INGwSpAddress&
INGwSpSipConnection::getAddress(INGwSpSipConnection::AddressType aeAddrType)
{
   switch(aeAddrType)
   {
      case ORIGINATING_ADDRESS:
      {
         return mConstData.mOriginatingAddr;
      }
      case DIALED_ADDRESS:
      {
         return mConstData.mDialedAddr;
      }
      case TARGET_ADDRESS:
      {
         return mConstData.mTargetAddr;
      }
      default:
      {
         logger.logINGwMsg(false, ERROR_FLAG, 0, "Invalid AddrType [%d]",
                         aeAddrType);
      }
   }

   return mConstData.mOriginatingAddr;
}

void 
INGwSpSipConnection::setAddress(INGwSpSipConnection::AddressType aeAddrType,
                                const INGwSpAddress& arAddr)
{
   if(INGwSpSipConnection::ORIGINATING_ADDRESS == aeAddrType) 
   {
      mConstData.mOriginatingAddr = arAddr;
   }
   else if(INGwSpSipConnection::DIALED_ADDRESS == aeAddrType) 
   {
      mConstData.mDialedAddr = arAddr;
   }
   else if(INGwSpSipConnection::TARGET_ADDRESS == aeAddrType) 
   {
      mConstData.mTargetAddr  = arAddr;
   }
   else
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Invalid AddrType [%d]",
                      aeAddrType);
   }
}
INGwSpSipCall& 
INGwSpSipConnection::getCall()
{
	 return *mpCall;
}

// Function that control serialization of data to PEER
void INGwSpSipConnection::transactionStarted(INGwSpMsgBaseHandler *p_Handler)
{
  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "IN transactionStarted");

	logger.logINGwMsg(false, TRACE_FLAG, 0, "Transaction Started on Connection "
									"<%d>.", getSelfConnectionId());
	serializationReadyFlag = COMP_SERI_NOT_READY;

  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "OUT transactionStarted");
}

void INGwSpSipConnection::transactionEnded(INGwSpMsgBaseHandler *p_Handler, 
																					 INGwSipHandlerType type)
{
  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "IN transactionEnded");
	logger.logINGwMsg(false, TRACE_FLAG, 0, "Transaction Ended on Connection "
									"<%d>.", getSelfConnectionId());

  switch (type)
	{
		case INVITE_HANDLER :
		{
			serializationReadyFlag = COMP_SERI_READY;
			getCall().startSerialize(REP_CREATE);
		}
		break;

		case BYE_HANDLER :
		{
			if(getCall().isCallReleased())
			{
				return;
			}
			operType = REP_DELETE;
			serializationReadyFlag = COMP_SERI_READY;
			getCall().startSerialize(REP_DELETE);
		}
		break;

	}
	serializationReadyFlag = COMP_SERI_READY;
  logger.logINGwMsg(mLogFlag, TRACE_FLAG, 0, "OUT transactionEnded");
}

void
INGwSpSipConnection::setNotifyTcSeqNum(int aSeqNum){
  mSipNotifyHandler.mSeqNum =  aSeqNum;
}
