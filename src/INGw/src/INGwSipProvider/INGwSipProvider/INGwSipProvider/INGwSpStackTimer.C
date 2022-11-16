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
//     File:     INGwSpStackTimer.C
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwSipProvider/INGwSpSipStackIntfLayer.h>
#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwSipProvider/INGwSpStackTimer.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipUtil.h>

#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>
#include <INGwInfraManager/INGwIfrMgrTimer.h>

int imERR_NONE = 0;

using namespace std;

#define POSITIVE_MATCH 0
#define NEGATIVE_MATCH 1 

extern "C" {

  SIP_U8bit
  timerMapKeyCompare(void* pKey1,
    void *pKey2) {

    LogINGwTrace(false, 0, "IN timerMapKeyCompare");

    if(0 == pKey1 || 0 == pKey2) {

      LogINGwTrace(false, 0, "OUT timerMapKeyCompare");
      return NEGATIVE_MATCH;
    }

    SipError error;

    SipTimerKey* l_timerKey1 =
      static_cast<SipTimerKey*>(pKey1);

    SipTimerKey* l_timerKey2 =
      static_cast<SipTimerKey*>(pKey2);

    // In case of a timeout the SipTimerKey pointers
    // will be the same.
    if(l_timerKey1 == l_timerKey2) {

      LogINGwTrace(false, 0, "OUT TimerKey Pointers match ... ");
      LogINGwTrace(false, 0, "OUT timerMapKeyCompare");
      return POSITIVE_MATCH;
    }

    if(SipSuccess == sip_compareTimerKeys(l_timerKey2,
        l_timerKey1, &error)) {

      LogINGwTrace(false, 0, "using sip_compareTimerkeys ... ");
      LogINGwTrace(false, 0, "OUT timerMapKeyCompare");
      return POSITIVE_MATCH;
    }

    LogINGwVerbose(false, 0, " SipTimerKey compare failed ");

    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
      "t2:<%s> t1:<%s>\n, t2:<%s> t1:<%s>\n, \
       t2:<%d> t1:<%d>\n, t2:<%d> t1:<%d>\n, \
       t2:<%d> t1:<%d>",
      l_timerKey2->dCallid,
      l_timerKey1->dCallid,
      l_timerKey2->pMethod,
      l_timerKey1->pMethod,
      l_timerKey2->dCseq,
      l_timerKey1->dCseq,
      l_timerKey2->dRseq,
      l_timerKey1->dRseq,
      l_timerKey2->dMatchType,
      l_timerKey1->dMatchType);

    LogINGwTrace(false, 0, "OUT timerMapKeyCompare");
    return NEGATIVE_MATCH;
  }

  void
  timerMapFreeElement(void* pElement)  {
   LogINGwTrace(false, 0, "IN timerMapFreeElement");

   //nothing to do

   LogINGwTrace(false, 0, "OUT timerMapFreeElement");
  }

  void
  timerMapFreeKey(void* pTimerKey) {
    LogINGwTrace(false, 0, "IN timerMapFreeKey");

    SipError error;

    SipTimerKey* l_timerKey =
      static_cast<SipTimerKey*>(pTimerKey);

    HSS_DECREF(l_timerKey->dRefCount);
    
    LogINGwTrace(false, 0, "OUT timerMapFreeKey");
  }
}

const string glbEmptyString("");

INGwSpStackTimer::INGwSpStackTimer() {
   LogINGwTrace(false, 0, "IN INGwSpStackTimer");

   Sdf_st_error error;

   //get memory for hash table.
   mTimerMap = (Sdf_st_hash *)
     sdf_memget (0, sizeof(Sdf_st_hash), &error);

     //Initialize the hash table.
   Sdf_ty_retVal l_retVal =
     bp_ivk_uaHashInit(mTimerMap,
       bp_ivk_uaElfHashTmr,
       timerMapKeyCompare,
       timerMapFreeElement,
       timerMapFreeKey,
       BP_SIP_NO_OF_HASH_BUCKETS_STACKTIMERMAP,
       BP_SIP_MAX_HSS_TIMEROBJECTS,
       &error);

   if(Sdf_co_fail == l_retVal) {
     LogINGwError(false, 0, "bp_ivk_uaHashInit FAILED");
   }

   LogINGwTrace(false, 0, "OUT INGwSpStackTimer");
}

INGwSpStackTimer::~INGwSpStackTimer() {
  // call bp_ivk_uaHashFree_ADI
}

bool INGwSpStackTimer::startInitRespTimer
                             (unsigned int &arTimerid  ,
                              int           aDuration  ,
                              const string       &arCallid   ,
                              INGwSipMethodType  aMethodType)
{
  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "INGwSpStackTimer::startInitRespTimer");

  return startAppTimer(arTimerid, aDuration, arCallid,
                       aMethodType, INGwSipTimerType::INITRESP_TIMER);
} // end of startInitRespTimer

bool INGwSpStackTimer::startTransComplTimer
                             (unsigned int &arTimerid  ,
                              int           aDuration  ,
                              const string       &arCallid   ,
                              INGwSipMethodType  aMethodType)
{
  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "INGwSpStackTimer::startTransComplTimer");

  return startAppTimer(arTimerid, aDuration, arCallid,
                       aMethodType, INGwSipTimerType::TRANSCOMPL_TIMER);
} // end of startTransComplTimer

bool INGwSpStackTimer::startNoAnswerTimer
                             (unsigned int &arTimerid  ,
                              int           aDuration  ,
                              const string       &arCallid   ,
                              INGwSipMethodType  aMethodType,
                              short         aConnId    )
{
  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "INGwSpStackTimer::startNoAnswerTimer");

  return startAppTimer(arTimerid, aDuration, arCallid,
                       aMethodType, INGwSipTimerType::NOANSWER_TIMER, aConnId);
} // end of startNoAnswerTimer

bool INGwSpStackTimer::startAppTimer
                             (unsigned int &arTimerid  ,
                              int           aDuration  ,
                              const string       &arCallid   ,
                              INGwSipMethodType  aMethodType,
                              INGwSipTimerType::TimerType aType,
                              short         aConnId    )
{
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "INGwSpStackTimer::startAppTimer: Starting app timer: duration <%d>, methodtype <%d>, timertype <%d>, connid <%d>, callid <%s>", aDuration, aMethodType, aType, aConnId, arCallid.c_str());

  INGwSipTimerContext *timercontext = new INGwSipTimerContext;
  timercontext->mType = aType;
  strncpy(timercontext->mCallId, arCallid.c_str(), MAX_CALLID_LEN - 1);
  timercontext->mCallId[MAX_CALLID_LEN - 1] = 0;
  timercontext->mMethodType = aMethodType;
  timercontext->mConnId     = aConnId;

  if(INGwIfrMgrThreadMgr::getInstance().startTimer(aDuration, (void *)timercontext,
     &INGwSpSipStackIntfLayer::instance(), arTimerid) < 0)
  {
    // The timer could not be started.  Release the context and return error.
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "INGwSpStackTimer::startAppTimer: Could not start timer");
    delete timercontext;
    return false;
  }

  return true;
} // end of startAppTimer

bool INGwSpStackTimer::stopInitRespTimer(unsigned int &aTimerid)
{
  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "INGwSpStackTimer::stopInitRespTimer: Stopping init-resp timer id %u", aTimerid);
  return stopAppTimer(aTimerid);
} // end of stopInitRespTimer

bool INGwSpStackTimer::stopNoAnswerTimer(unsigned int &aTimerid)
{
  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "INGwSpStackTimer::stopNoAnswerTimer: Stopping no-answer timer id %u", aTimerid);
  return stopAppTimer(aTimerid);
} // end of stopNoAnswerTimer

bool INGwSpStackTimer::stopTransComplTimer(unsigned int &aTimerid)
{
  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "INGwSpStackTimer::stopTransComplTimer: Stopping trans-compl timer id %u", aTimerid);
  return stopAppTimer(aTimerid);
} // end of stopTransComplTimer

bool INGwSpStackTimer::stopAppTimer(unsigned int &aTimerid)
{
  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "INGwSpStackTimer::stopAppTimer: Stopping timer id %u", aTimerid);
  int status = INGwIfrMgrThreadMgr::getInstance().stopTimer(aTimerid);
  if(status < 0) return false;
  else           return true;
} // end of stopAppTimer

bool
INGwSpStackTimer::put(INGwSpStackTimerContext* aContext) {

  LogINGwTrace(false, 0, "IN put");

  if(0 == aContext->mpKey) {
    LogINGwError(false, 0, "SipTimerKey null in StackTimerCtx");
    LogINGwTrace(false, 0, "OUT put");
    return false;
  }

  SipError error;
  HSS_INCREF(aContext->mpKey->dRefCount);

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
    "Inserting:<%s>", aContext->mpKey->dCallid);

  if(bp_ivk_uaHashAdd(mTimerMap,
    (void*)aContext,
    (void*)aContext->mpKey) == Sdf_co_success) {

    // key & context will be deleted when the element is
    // removed from hash map.
    LogINGwTrace(false, 0, "OUT put");
    return true;
  }
  else {
    HSS_DECREF(aContext->mpKey->dRefCount);
    LogINGwTrace(false, 0, "OUT put");
    return false;
  }
}

bool
INGwSpStackTimer::remove(SipTimerKey* aKey, 
  INGwSpStackTimerContext** pObject) {
  LogINGwTrace(false, 0, "IN remove");

  if(bp_ivk_uaHashRemove(mTimerMap,
    (void*)aKey, (void**)pObject) == Sdf_co_success) {

    LogINGwTrace(false, 0, "OUT remove");
    return true;
  }
  else {

    LogINGwTrace(false, 0, "OUT remove");
    return false;
  }

  LogINGwTrace(false, 0, "OUT remove");
}

bool
INGwSpStackTimer::startTimer(SipTimerKey* aKey,
  void* aBuffer,
  int   aDuration,
  sip_timeoutFuncPtr  aTimeoutFunc) {

  LogINGwTrace(false, 0, "IN startTimer");

  if(0 == aKey->dCallid) {
    LogINGwError(false, 0, "callid null in SipTimerKey");
    LogINGwTrace(false, 0, "OUT startTimer");
    return false;
  }

  // Directly do a put, if it fails delete the 
  // timer context
  INGwSpStackTimerContext* l_tmrCtx =
    new INGwSpStackTimerContext(aKey, aBuffer, 0, aTimeoutFunc);
  int l_timerStatus = 0;

  if(put(l_tmrCtx)) {

    l_timerStatus = INGwIfrMgrThreadMgr::getInstance().startTimer(
                      aDuration, (void *)l_tmrCtx, 
                      &INGwSpSipStackIntfLayer::instance(),
                      l_tmrCtx->mTimerId);
  }
  else {
    delete l_tmrCtx;
    LogINGwTrace(false, 0, "OUT startTimer");
    return false;
  }

  if(l_timerStatus < 0) {
    remove(aKey, 0);
    delete l_tmrCtx;
    LogINGwTrace(false, 0, "OUT startTimer");
    return false;
  }

  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
    "started stack timer with id:<%x> and callid <%s>", 
    l_tmrCtx->mTimerId, aKey->dCallid);

  LogINGwTrace(false, 0, "OUT startTimer");
  return true;
}

bool
INGwSpStackTimer::stopTimer(SipTimerKey* aInKey,
  SipTimerKey** aOutKey,
  void** aOutBuf) {

  LogINGwTrace(false, 0, "IN stopTimer");

  if(0 == aInKey->dCallid) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                       "Error getting callid from the InKey");
    LogINGwTrace(false, 0, "OUT stopTimer");
    return false;
  }

  INGwSpStackTimerContext* l_tmrCtx = 0;
  if(remove(aInKey, &l_tmrCtx)) {

    if(0 == l_tmrCtx) {
      LogINGwError(false, 0, 
        "remove returned null INGwSpStackTimerContext");
      LogINGwTrace(false, 0, "OUT stopTimer");
      return false;
    }

    unsigned int &timerid = l_tmrCtx->mTimerId;
    *aOutKey = l_tmrCtx->mpKey;
    *aOutBuf = l_tmrCtx->mBuf;

    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
      "Erasing timer for callid:<%s>, timerid:<%x>", 
      aInKey->dCallid, timerid);

    INGwIfrMgrThreadMgr::getInstance().stopTimer(timerid);

    LogINGwTrace(false, 0, "OUT stopTimer");
    return true;
  }
  else {
    LogINGwWarning(false, 0, "remove failed");
    LogINGwTrace(false, 0, "OUT stopTimer");
    return false;
  }
}

bool 
INGwSpStackTimer::indicateTimeout(INGwSpStackTimerContext *aMsg) {
  LogINGwTrace(false, 0, "IN indicateTimeout");

  INGwSpStackTimerContext *timercontext = aMsg;
  if(0 == timercontext) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                "indicateTimeout: NULL timer context");
    LogINGwTrace(false, 0, "OUT indicateTimeout");
    return false;
  }


  if(0 == timercontext->mpKey ||
     0 == timercontext->mpKey->dCallid) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
                    "error extracting SipTimerKey/CallId");
    LogINGwTrace(false, 0, "OUT indicateTimeout");
    return false;
  }

  if(false == remove(timercontext->mpKey, 0)) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
      "could not find entry, callId:<%s> for timeout message", 
      timercontext->mpKey->dCallid);
    LogINGwTrace(false, 0, "OUT indicateTimeout");
    return false;
  }

  INGwSpThreadSpecificSipData &tsd = 
                          INGwSpSipProvider::getInstance().getThreadSpecificSipData();

  // Mriganka BPInd12961 - Start.
  Sdf_st_callObject *callobj = NULL;
  const char *callid = timercontext->mpKey->dCallid;
  if(!tsd.getCallTable().get(callid, &callobj))
  {
     callobj = NULL;
  }
  // Mriganka BPInd12961 - End.

  if(INGwSpSipProviderConfig::isHoldMsg())
  {
     // Mriganka BPInd12961 - Start.
     // This has been moved outside the if check
     /*
     Sdf_st_callObject *callobj = NULL;
     const char *callid = timercontext->mpKey->dCallid;
     if(!tsd.getCallTable().get(callid, &callobj))
     {
        callobj = NULL;
     }
     */
     // Mriganka BPInd12961 - End.

     if(callobj)
     {
        INGwSpSipConnection *bpConn = INGwSpSipUtil::getINGwSipConnFromHssCall(callobj);
        tsd.conn = bpConn;
     }
  }

  if(INGwSpSipProviderConfig::getDontRetrans()) 
  {
    if(!callobj) 
    {
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
                      "indicateTimeout: Not calling TimeoutFunc since Call object not present in map");
      SipEventContext *tempcontext;
      tempcontext = ((SipTimerBuffer *)timercontext->mBuf)->pContext;
      ((SipTimerBuffer *)timercontext->mBuf)->pContext = SIP_NULL;
      if(tempcontext != SIP_NULL)
      {
        if((tempcontext->dOptions.dOption & SIP_OPT_DIRECTBUFFER) == \
            SIP_OPT_DIRECTBUFFER)
        {
          ((SipTimerBuffer *)timercontext->mBuf)->pBuffer= SIP_NULL;
        }
      }
      sip_freeSipTimerBuffer(((SipTimerBuffer *)timercontext->mBuf));
      sip_freeSipTimerKey(timercontext->mpKey);

      sip_freeEventContext(tempcontext);
    }
    else {
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE,
                      "indicateTimeout: Call object present in map, Calling TimeoutFunc");
      (*(timercontext->mTimeoutFunc))(timercontext->mpKey, timercontext->mBuf);
    }
  }
  else {
    // STATISTICS: Total number of retransmissions sent: Increment
    (*(timercontext->mTimeoutFunc))(timercontext->mpKey, timercontext->mBuf);
  }
  // Mriganka BPInd12961 - End.

  tsd.conn = NULL;

  LogINGwTrace(false, 0, "OUT indicateTimeout");
  return true;

} // end of indicateTimeout

/*
 |-----------------------------------------------------------
 | stopSessionTimer
 |
 |
 |-----------------------------------------------------------
*/
bool
INGwSpStackTimer::stopSessionTimer(
  Sdf_ty_pvoid pTimerHandle,
  Sdf_ty_pvoid* ppContextInfo) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN stopSessionTimer");

  if( pTimerHandle == Sdf_co_null){
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, "pTimerHandle is NULL in stopSessionTimer");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT stopSessionTimer");
    return false;
  }

  INGwSpSessionTmrStackHandle* l_bpSessTmrStackHandle = 
     reinterpret_cast<INGwSpSessionTmrStackHandle*>(pTimerHandle);

  if(0 == l_bpSessTmrStackHandle) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
                   "cast failed from void* to INGwSpSessionTmrStackHandle* in stopSessionTimer");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT stopSessionTimer");
    return false;
  }

  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE,
              "Timer to stop id:<%x>", l_bpSessTmrStackHandle->mTimerId);

  INGwSpSipConnection* l_bpSipConnection = 0;
  Sdf_st_error pErr;
  Sdf_st_sessionTimerContextInfo* l_hssSessTmrCtx =
    (Sdf_st_sessionTimerContextInfo*)(l_bpSessTmrStackHandle->pContextInfo);

  if(0 == l_hssSessTmrCtx) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
        "ConnectionContext from SessionTmrStackHandle is Null in stopSessionTimer");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT stopSessionTimer");
    return false;
  }

  Sdf_st_callObject* l_hssCallObj = l_hssSessTmrCtx->pCallObject;

  if(0 == l_hssCallObj) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
        "pCallObject from SessionTmrStackHandle is Null in stopSessionTimer");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT stopSessionTimer");
    return false;
  }

  l_bpSipConnection =
    INGwSpSipUtil::getINGwSipConnFromHssCall(l_hssCallObj);

  if(0 == l_bpSipConnection) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
        "stopSessionTimer: INGwSpSipConnection from ConnectionContext is Null for call-id: <%s>", l_hssCallObj->pCommonInfo->pCallid);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT stopSessionTimer");
    return false;
  }

  // Compare if we are trying to stop our own timer...
  if( l_bpSessTmrStackHandle->mTimerId ==
      l_bpSipConnection->mActiveSessionTimerId ) {

    // Reset our timerid to 0 also.
    l_bpSipConnection->mActiveSessionTimerId = 0;

    int l_retVal = 
      INGwIfrMgrThreadMgr::getInstance().stopTimer(l_bpSessTmrStackHandle->mTimerId);
    if(0 > l_retVal) {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
        "stopSessionTimer: stop failed for timer id:<%x> for call-id: <%s>", 
				l_bpSessTmrStackHandle->mTimerId, l_hssCallObj->pCommonInfo->pCallid);

      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT stopSessionTimer");
      return false;
    }

  } else {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
        "stopSessionTimer called for timer id:< %x >," \
        "while the actual timer id stored with connection objects is < %x > for call-id <%s>",
        l_bpSessTmrStackHandle->mTimerId, l_bpSipConnection->mActiveSessionTimerId,
				l_hssCallObj->pCommonInfo->pCallid);

    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT stopSessionTimer");
    return false;
  }

  //pass the structure to stack to free up.
  *ppContextInfo = l_bpSessTmrStackHandle->pContextInfo;

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT stopSessionTimer");
  return true;
}

/*
 |-----------------------------------------------------------
 | startSessionTimer
 |
 |
 |-----------------------------------------------------------
*/
bool
INGwSpStackTimer::startSessionTimer(
  unsigned int aDuration,
  Sdf_ty_timerType aTimertype,
  Sdf_ty_pvoid     aContextInfo,
  Sdf_ty_pvoid*    ppTimerHandle,
  Sdf_ty_TimertimeOutFunc aTimeoutFunc) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN startSessionTimer");

  // If major state of the connection is not connected
  // the session timer cannot be started.
  // Also check to see that the mActiveSessionTimerId is null.
  // Two session timers cannot be active at one time

  // BPInd16293 : Initialize this object so that in case we do not start timer here
  // for some reason, we either do not get the stopSessionTimer callback
  // or we can at least check before trying to stop the same.

  *ppTimerHandle = Sdf_co_null;

  INGwSpSipConnection* l_bpSipConnection = 0;
  Sdf_st_appData* l_pAppSpecificData = Sdf_co_null;
  Sdf_st_error pErr;
  Sdf_st_sessionTimerContextInfo* l_hssSessTmrCtx =
                       (Sdf_st_sessionTimerContextInfo*)aContextInfo;
  Sdf_st_callObject* l_hssCallObj = l_hssSessTmrCtx->pCallObject;

  if(0 == l_hssCallObj) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
        "startSessionTimer: pCallObject from SessionTmrStackHandle is Null ....");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT startSessionTimer");
    return false;
  }

  l_bpSipConnection =
        INGwSpSipUtil::getINGwSipConnFromHssCall(l_hssCallObj);

  if(0 == l_bpSipConnection) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
       "startSessionTimer: INGwSpSipConnection from ConnectionContext is Null for call-id <%s>", 
			 l_hssCallObj->pCommonInfo->pCallid);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT startSessionTimer");
    return false;
  }
  else {
    if(CONNECTION_FAILED == l_bpSipConnection->getMajorState() ||
       CONNECTION_DISCONNECTED ==
                                   l_bpSipConnection->getMajorState()) {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
        "startSessionTimer: Connection Major State is FAIELD || DISCONNECTED, cannot start" \
				"sess timer for call-id <%s>", l_hssCallObj->pCommonInfo->pCallid);
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT startSessionTimer");
      return false;
    }
    if(0 != l_bpSipConnection->mActiveSessionTimerId) {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
        "startSessionTimer: Session timer Id:<%x>, expected null before starting new timer for call-id <%s>",
         l_bpSipConnection->mActiveSessionTimerId, l_hssCallObj->pCommonInfo->pCallid);
      logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT startSessionTimer");
      return false;
    }
  }
   
  INGwSpSessionTimerContext* l_sessTmrCtx = new INGwSpSessionTimerContext;
  l_sessTmrCtx->mTimertype    = aTimertype;
  l_sessTmrCtx->mpTimeoutFunc = aTimeoutFunc;
  (l_sessTmrCtx->mStackTmrHandle).pContextInfo = aContextInfo;

  // This timer id needs to be stores in the SessionTimerInfo
  // structure in the HSS call object
  unsigned int l_timerid = 0;

  if(INGwIfrMgrThreadMgr::getInstance().startTimer((aDuration + (aDuration >> 2)),
    (void*)l_sessTmrCtx, &INGwSpSipStackIntfLayer::instance(), 
		l_timerid) < 0) {

		logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
				"startSessionTimer FAILED for call-id <%s>", l_hssCallObj->pCommonInfo->pCallid);

		delete l_sessTmrCtx;

		logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT startSessionTimer");
		return false;
	}
  else {
    logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
      "sessionTimer Started:<%x>, duration:<%d msec>", l_timerid, aDuration);
   
    l_bpSipConnection->mActiveSessionTimerId = l_timerid;
    (l_sessTmrCtx->mStackTmrHandle).mTimerId = l_timerid;
    (l_sessTmrCtx->mStackTmrHandle).pSessionTmrCtx = l_sessTmrCtx;

    *ppTimerHandle = (void*) &(l_sessTmrCtx->mStackTmrHandle);
  }

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT startSessionTimer");
  return true;
}

bool INGwSpStackTimer::startRemoteRetransPurgeTimer(unsigned int aDuration)
{
  LogINGwTrace(false, 0, "IN startRemoteRetransPurgeTimer");

  unsigned int timerid = 0;
  bool ret = startAppTimer(timerid, aDuration,
                       glbEmptyString, INGW_METHOD_TYPE_NONE,
                       INGwSipTimerType::REMOTE_RETRANS_PURGE_TIMER);
  LogINGwTrace(false, 0, "OUT startRemoteRetransPurgeTimer");

  return ret;
}

bool INGwSpStackTimer::startSipIvrTimer
  (unsigned int &arTimerid  ,
   int           aDuration  ,
   const string       &arCallid   )
{
  LogINGwTrace(false, 0, "IN startSipIvrTimer");
  bool result =
    startAppTimer(arTimerid,
                  aDuration,
                  arCallid,
                  INGW_SIP_METHOD_TYPE_INFO,
                  INGwSipTimerType::SIPIVR_TIMER);

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "INGwSpStackTimer::startSipIvrTimer: Started sip ivr timerid <%u>", arTimerid);

  LogINGwTrace(false, 0, "OUT startSipIvrTimer");
  return result;
}

bool INGwSpStackTimer::stopSipIvrTimer
  (unsigned int &aTimerid)
{
  LogINGwTrace(false, 0, "IN stopSipIvrTimer");
  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "INGwSpStackTimer::stopSipIvrTimer: Stopping sip-ivr timer id %u", aTimerid);
  bool ret = stopAppTimer(aTimerid);
  LogINGwTrace(false, 0, "OUT stopSipIvrTimer");
  return ret;
}

int INGwSpStackTimer::getCount()
{
   return mTimerMap->numberOfElements;
}
