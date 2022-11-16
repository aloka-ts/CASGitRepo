/************************************************************************
     Name:     Stack Manager Adaptor 
 
     Type:     Implementation
 
     Desc:     Implementation of the SM Adaptor

     File:     INGwSmAdaptor.C

     Sid:      INGwSmAdaptor.C 0  -  04/08/03 

     Prg:      gs,bd

************************************************************************/
//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

#include "INGwSmAdaptor.h"

/**
 * definition for static class member variable.
 */
INGwSmDistributor* INGwSmAdaptor::mpDist = 0;
INGwSmTrcHdlr*     INGwSmAdaptor::mpTrcHdlr = 0;
INGwSmAdaptor*     INGwSmAdaptor::mpSelf = 0;
INGwSmAdaptorState INGwSmAdaptor::mState;


class INGwSmRepository;

/*
 * ss7 stack impl specific includes
 */
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
  #include "ssi.h"
#ifndef __CCPU_CPLUSPLUS
}
#endif

using namespace std;

// c'tor
INGwSmAdaptor::INGwSmAdaptor()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAdaptor::INGwSmAdaptor");

  mState = BP_AIN_SM_STARTING_UP;

  mpTrcHdlr = new INGwSmTrcHdlr;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAdaptor::INGwSmAdaptor");
}


// d'tor
INGwSmAdaptor::~INGwSmAdaptor()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAdaptor::~INGwSmAdaptor");

  mState = BP_AIN_SM_SHUTTING_DOWN;

  if (mpTrcHdlr) 
    delete mpTrcHdlr;

  mpTrcHdlr = 0;


  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAdaptor::~INGwSmAdaptor");
}


// singleton accessor/creator
INGwSmAdaptor* 
INGwSmAdaptor::getInstance()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAdaptor::getInstance");

  if (mpSelf == 0) {
    mpSelf = new INGwSmAdaptor();
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAdaptor::getInstance");

  return mpSelf;
}


// save the Distributor ptr for later use. this guy's created
// by the Distributor itself.
int 
INGwSmAdaptor::initialize(INGwSmDistributor *apDist)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAdaptor::initialize");

  mpDist = apDist;

  INGwSmRepository * repos = mpDist->getSmRepository();
  string fn = repos->getSmLogFile();
  char *filename = const_cast<char *> ( fn.c_str() );
  if (filename == 0) {
    logger.logMsg (TRACE_FLAG, 0,
      "INGwSmAdaptor::initialize -- need valid log file.");
  }

  // max trace file size currently 0 (don't care)
  if (mpTrcHdlr->initialize(0, filename) != BP_AIN_SM_OK) {
    logger.logMsg (TRACE_FLAG, 0,
      "INGwSmAdaptor::initialize -- INGwSmTrcHdlr initialization failed.");
  }

  mState = BP_AIN_SM_UP;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAdaptor::initialize");

  return (BP_AIN_SM_OK);
}
   

// get distributor reference
INGwSmDistributor* 
INGwSmAdaptor::getDistributor()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAdaptor::getDistributor");

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAdaptor::getDistributor");

  return mpDist;
}


// get trace handler reference
INGwSmTrcHdlr* 
INGwSmAdaptor::getTrcHdlr()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAdaptor::getTrcHdlr");

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAdaptor::getTrcHdlr");

  return mpTrcHdlr;
}


// the Initialization TAPA Task routine
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
short iNGwSmActvInit (Ent aEnt, Inst aInst, 
                 Region aRegion, Reason aReason)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering iNGwSmActvInit");

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving iNGwSmActvInit");

  // no op
  return (ROK);   // ie. if anybody cares!
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// the activate function for the TAPA Task
// this will receive all the messages for stack manager and then
// distribute them according to the Source Entity
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
short iNGwSmActvTsk(Pst *aPost, Buffer *apBuf)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering iNGwSmActvTsk");

#ifdef _SM_BUFFER_DUMP_
  SPrntMsg(apBuf, aPost->srcEnt, aPost->dstEnt);
#endif

  /* process event based on source entitiy */
  switch(aPost->srcEnt)
  {
#if 0
    // INAP
    case ENTIE:
      iNGwSmIeActvTsk(aPost, apBuf);
      break;
#endif
    // TCAP
    case ENTST:
      iNGwSmStActvTsk(aPost, apBuf);
      break;

    // SCCP
    case ENTSP:
      iNGwSmSpActvTsk(aPost, apBuf);
      break;

    // M3UA
    case ENTIT:
      iNGwSmItActvTsk(aPost, apBuf);
      break;

    // LDF M3UA
    case ENTDV:
      iNGwSmDvActvTsk(aPost, apBuf);
      break;

    // SCTP
    case ENTSB:
      iNGwSmSbActvTsk(aPost, apBuf);
      break;

    // TUCL
    case ENTHI:
      iNGwSmHiActvTsk(aPost, apBuf);
      break;

    // MTP3
    case ENTSN:
      iNGwSmSnActvTsk(aPost, apBuf);
      break;

    // LDF MTP3
    case ENTDN:
      iNGwSmDnActvTsk(aPost, apBuf);
      break;

    // MTP2
    case ENTSD:
      iNGwSmSdActvTsk(aPost, apBuf);
      break;
    
    case ENTSH:
      iNGwSmShActvTsk(aPost, apBuf);
      break;

    case ENTSG:
      iNGwSmSgActvTsk(aPost, apBuf);
      break;

    case ENTMR:
      iNGwSmMrActvTsk(aPost, apBuf);
      break;

    case ENTRY:
      iNGwSmRyActvTsk(aPost, apBuf);
      break;

    // discard this message
    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected mesg recv'd by AinStackMgr from layer: %d",
                    (int)aPost->srcEnt);    
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving iNGwSmActvTsk");

  return (ROK);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


/*
 * handler function for MR 
 */

// activation function for MR Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmMrActvTsk (Pst *aPost, Buffer *apBuf)
{
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmMrActvTsk");

  switch(aPost->event)
  {
    case EVTLMRCFGCFM:             /* Config Confirm */
      ret = cmUnpkMiLmrCfgCfm(iNGwSmMrCfgCfm, aPost, apBuf);
      break;

    case EVTLMRCNTRLCFM:             /* Control Confirm */
      ret = cmUnpkMiLmrCntrlCfm(iNGwSmMrCtlCfm, aPost, apBuf);
      break;

    case EVTLMRSTACFM:             /* Status Confirmn */
      ret = cmUnpkMiLmrStaCfm(iNGwSmMrStaCfm, aPost, apBuf);
      break;

    case EVTLMRSTAIND:             /* Status Indication */
      ret = cmUnpkMiLmrStaInd(iNGwSmMrStaInd, aPost, apBuf);
      break;

    case EVTLMRSTSCFM:             /* Statistics Confirm */
      ret = cmUnpkMiLmrStsCfm(iNGwSmMrStsCfm, aPost, apBuf);
      break;

    case EVTLMRTRCIND:           /* Trace Confirm */
      ret = cmUnpkMiLmrTrcInd(iNGwSmMrTrcInd, aPost, apBuf);
      break;

    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected MR mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmMrActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Configuration Confirm Handler of MR Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmMrCfgCfm (Pst *aPost, MrMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmMrCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTMR, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmMrCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Control Confirm handler of MR Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmMrCtlCfm (Pst *aPost, MrMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmMrCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTMR, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmMrCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Status  Confirm handler of MR Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmMrStaCfm (Pst *aPost, MrMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmMrStaCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(ENTMR, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmMrStaCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Status Indication handler of MR Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmMrStaInd (Pst *aPost, MrMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmMrStaInd");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(ENTMR, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmMrStaInd");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Statics Confirm handler of MR Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmMrStsCfm (Pst *aPost, MrMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmMrStsCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStsCfmQueMsg(ENTMR, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmMrStsCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Trace Indication handler of MR Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmMrTrcInd (Pst *aPost, MrMngmt *aTrc)
{
#ifdef INCTCBD
  INGwSmTrcInfo *trcInfo = 0;
  int allocLen = 0;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmMrTrcInd");

  // since INGwSmTrcInfo is a variable length structure, so
  // compute it's size in bytes, and then allocate that...
  allocLen = sizeof(INGwSmTrcInfo) + aTrc->t.trc.len - 1;

  trcInfo = (INGwSmTrcInfo *) (new char [allocLen]);

  if (trcInfo == 0) {
    return (RFAILED);
  }

  trcInfo->miLayerId = BP_AIN_SM_SH_LAYER;
  trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_NA;

  if (aTrc->t.trc.trcEvent == LST_MSG_RECVD) {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
  }
  else {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
  }

  trcInfo->hour  = aTrc->t.trc.dt.hour;
  trcInfo->min   = aTrc->t.trc.dt.min;
  trcInfo->sec   = aTrc->t.trc.dt.sec;
  trcInfo->tenth = aTrc->t.trc.dt.tenths;

  trcInfo->miTrcLen = aTrc->t.trc.len;
  memcpy((void *)(trcInfo->maTrcBuf),
      (void *)(aTrc->t.trc.evntParm),
      trcInfo->miTrcLen);

  // invoke TraceHandler to handle this trace info
  INGwSmTrcHdlr *lpTrcHdlr = INGwSmAdaptor::getTrcHdlr();

  if (lpTrcHdlr)
    lpTrcHdlr->handleTrace(trcInfo);

  // since we are done with it, deallocate trcInfo
  delete [] ((char *)trcInfo);

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmMrTrcInd");
#endif
  return (ROK);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

/*
 * handler function for RY 
 */


// activation function for RY Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmRyActvTsk (Pst *aPost, Buffer *apBuf)
{
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmRyActvTsk");

  switch(aPost->event)
  {
    case EVTLRYCFGCFM:             /* Config Confirm */
      ret = cmUnpkMiLryCfgCfm(iNGwSmRyCfgCfm, aPost, apBuf);
      break;

    case EVTLRYCNTRLCFM:             /* Control Confirm */
      ret = cmUnpkMiLryCntrlCfm(iNGwSmRyCtlCfm, aPost, apBuf);
      break;

    case EVTLRYSTACFM:             /* Status Confirmn */
      ret = cmUnpkMiLryStaCfm(iNGwSmRyStaCfm, aPost, apBuf);
      break;

    case EVTLRYSTAIND:             /* Status Indication */
      ret = cmUnpkMiLryStaInd(iNGwSmRyStaInd, aPost, apBuf);
      break;

#if 0
    case EVTLRYSTSCFM:             /* Statistics Confirm */
      ret = cmUnpkMiLryStsCfm(iNGwSmRyStsCfm, aPost, apBuf);
      break;
    case EVTLRYTRCIND:           /* Trace Confirm */
      ret = cmUnpkMiLryTrcInd(iNGwSmRyTrcInd, aPost, apBuf);
      break;
#endif
    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected RY mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmRyActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// Configuration Confirm Handler of RY Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
S16
iNGwSmRyCfgCfm (Pst *aPost, RyMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmRyCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTRY, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmRyCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif



// Control Confirm handler of RY Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmRyCtlCfm (Pst *aPost, RyMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmRyCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTRY, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmRyCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// Status Indication handler of RY Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmRyStaInd (Pst *aPost, RyMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmRyStaInd");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(ENTRY, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmRyStaInd");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// Status  Confirm handler of RY Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmRyStaCfm (Pst *aPost, RyMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmRyStaCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(ENTRY, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmRyStaCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Statics Confirm handler of RY Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmRyStsCfm (Pst *aPost, RyMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmRyStsCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStsCfmQueMsg(ENTRY, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmRyStsCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif




// the timeout handler function of the Stack
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmActvStsTmr ()
{
  INGwSmQueueMsg *qMsg = 0;

  logger.logMsg (TRACE_FLAG, 0,
    "Entering iNGwSmActvStsTmr");

  if ((qMsg = new INGwSmQueueMsg) == 0) {
    // memory alloc failure -- alarm    
    return (RFAILED);
  }

  memset (qMsg, 0, sizeof (INGwSmQueueMsg));

  qMsg->mSrc = BP_AIN_SM_SRC_STACK;                     // msg from stack
  qMsg->t.stackMsg.stkMsgTyp = BP_AIN_SM_STKOP_TIMEOUT; // it's a status confirm
  qMsg->t.stackMsg.stkMsg.miTransId = 0;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving iNGwSmActvStsTmr");

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    delete qMsg;
    return (RFAILED);
  }
  else
    return (ROK);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


#if 0
// activation function for INAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmIeActvTsk (Pst *aPost, Buffer *apBuf)
{
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
    "Entering iNGwSmIeActvTsk");

  switch(aPost->event)
  {
    case LIE_EVTSTACFM:             /* Status Confirm */
      ret = cmUnpkLieStaCfm(iNGwSmIeStaCfm, aPost, apBuf);
      break;

    case LIE_EVTSTSCFM:             /* Statistics Confirm */
      ret = cmUnpkLieStsCfm(iNGwSmIeStsCfm, aPost, apBuf);
      break;

    case LIE_EVTSTAIND:             /* Status Indication */
      ret = cmUnpkLieStaInd(iNGwSmIeStaInd, aPost, apBuf);
      break;

    case LIE_EVTTRCIND:             /* Trace Indication */
      ret = cmUnpkLieTrcInd(iNGwSmIeTrcInd, aPost, apBuf);
      break;

    case LIE_EVTCFGCFM:             /* Configuration Confirm */
      ret = cmUnpkLieCfgCfm(iNGwSmIeCfgCfm, aPost, apBuf);
      break;

    case LIE_EVTCNTRLCFM:           /* Control Confirm */
      ret = cmUnpkLieCntrlCfm(iNGwSmIeCtlCfm, aPost, apBuf);
      break;

    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected INAP mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving iNGwSmIeActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Configuration Confirm Handler of INAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmIeCfgCfm (Pst *aPost, IeMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmIeCfgCfm");
     
  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTIE, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmIeCfgCfm");
     
  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Control Confirm handler of INAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmIeCtlCfm (Pst *aPost, IeMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmIeCtlCfm");
     
  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTIE, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmIeCtlCfm");
     
  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Status Confirm handler of INAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmIeStaCfm (Pst *aPost, IeMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmIeStaCfm");
     
  if ((qMsg = INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(ENTIE, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmIeStaCfm");
     
  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Statistics Confirm handler of INAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmIeStsCfm (Pst *aPost, IeMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmIeStsCfm");
     
  if ((qMsg = INGwSmAdaptor::iNGwSmMkStsCfmQueMsg(ENTIE, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmIeStsCfm");
     
  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Trace Indication handler of INAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmIeTrcInd (Pst *aPost, IeMngmt *aTrc)
{
  INGwSmTrcInfo *trcInfo = 0;
  int allocLen = 0;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmIeTrcInd");
     
  // since INGwSmTrcInfo is a variable length structure, so
  // compute it's size in bytes, and then allocate that...
  allocLen = sizeof(INGwSmTrcInfo) + aTrc->t.trc.diag.len - 1;

  trcInfo = (INGwSmTrcInfo *) (new char [allocLen]);

  if (trcInfo == 0) {
    return (RFAILED);
  }

  trcInfo->miLayerId = BP_AIN_SM_AIN_LAYER;
  trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_NA;

  if (aTrc->t.trc.evnt == LIE_CMP_RXED) {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
  }
  else {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
  }

  trcInfo->hour  = aTrc->t.trc.dt.hour;
  trcInfo->min   = aTrc->t.trc.dt.min;
  trcInfo->sec   = aTrc->t.trc.dt.sec;
  trcInfo->tenth = aTrc->t.trc.dt.tenths;

  trcInfo->miTrcLen = aTrc->t.trc.diag.len;
  memcpy((void *)(trcInfo->maTrcBuf),
         (void *)(aTrc->t.trc.diag.val),
         trcInfo->miTrcLen);

  // invoke TraceHandler to handle this trace info
  INGwSmTrcHdlr *lpTrcHdlr = INGwSmAdaptor::getTrcHdlr();

  if (lpTrcHdlr)
    lpTrcHdlr->handleTrace(trcInfo);

  // since we are done with it, deallocate trcInfo
  delete [] ((char *)trcInfo);

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmIeTrcInd");

  return (ROK);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Unsolicited Status Indication of INAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmIeStaInd (Pst *aPost, IeMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmIeStaInd");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(ENTIE, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmIeStaInd");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

#endif


/*
 * handler function for SG
 */

// activation function for SH Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSgActvTsk (Pst *aPost, Buffer *apBuf)
{
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSgActvTsk");

  switch(aPost->event)
  {
    case EVTLSGCFGCFM:             /* Config Confirm */
      ret = cmUnpkMiLsgCfgCfm(iNGwSmSgCfgCfm, aPost, apBuf);
      break;

    case EVTLSGCNTRLCFM:             /* Control Confirm */
      ret = cmUnpkMiLsgCntrlCfm(iNGwSmSgCtlCfm, aPost, apBuf);
      break;

    case EVTLSGSTACFM:             /* Status Confirmn */
      ret = cmUnpkMiLsgStaCfm(iNGwSmSgStaCfm, aPost, apBuf);
      break;

    case EVTLSGSTAIND:             /* Status Indication */
      ret = cmUnpkMiLsgStaInd(iNGwSmSgStaInd, aPost, apBuf);
      break;

    case EVTLSGSTSCFM:             /* Statistics Confirm */
      ret = cmUnpkMiLsgStsCfm(iNGwSmSgStsCfm, aPost, apBuf);
      break;

    case EVTLSGTRCIND:           /* Trace Confirm */
      ret = cmUnpkMiLsgTrcInd(iNGwSmSgTrcInd, aPost, apBuf);
      break;

    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected SH mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSgActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Configuration Confirm Handler of SG Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSgCfgCfm (Pst *aPost, SgMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSgCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTSG, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSgCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Control Confirm handler of SG Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSgCtlCfm (Pst *aPost, SgMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSgCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTSG, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSgCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Status  Confirm handler of SG Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSgStaCfm (Pst *aPost, SgMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSgStaCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(ENTSG, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSgStaCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Status Indication handler of SG Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSgStaInd (Pst *aPost, SgMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSgStaInd");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(ENTSG, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSgStaInd");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// Statics Confirm handler of SG Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSgStsCfm (Pst *aPost, SgMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSgStsCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStsCfmQueMsg(ENTSG, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSgStsCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Trace Indication handler of SG Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSgTrcInd (Pst *aPost, SgMngmt *aTrc)
{
#ifdef INCTCBD
  INGwSmTrcInfo *trcInfo = 0;
  int allocLen = 0;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSgTrcInd");

  // since INGwSmTrcInfo is a variable length structure, so
  // compute it's size in bytes, and then allocate that...
  allocLen = sizeof(INGwSmTrcInfo) + aTrc->t.trc.len - 1;

  trcInfo = (INGwSmTrcInfo *) (new char [allocLen]);

  if (trcInfo == 0) {
    return (RFAILED);
  }

  trcInfo->miLayerId = BP_AIN_SM_SG_LAYER;
  trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_NA;

  if (aTrc->t.trc.trcEvent == LST_MSG_RECVD) {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
  }
  else {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
  }

  trcInfo->hour  = aTrc->t.trc.dt.hour;
  trcInfo->min   = aTrc->t.trc.dt.min;
  trcInfo->sec   = aTrc->t.trc.dt.sec;
  trcInfo->tenth = aTrc->t.trc.dt.tenths;

  trcInfo->miTrcLen = aTrc->t.trc.len;
  memcpy((void *)(trcInfo->maTrcBuf),
      (void *)(aTrc->t.trc.evntParm),
      trcInfo->miTrcLen);

  // invoke TraceHandler to handle this trace info
  INGwSmTrcHdlr *lpTrcHdlr = INGwSmAdaptor::getTrcHdlr();

  if (lpTrcHdlr)
    lpTrcHdlr->handleTrace(trcInfo);

  // since we are done with it, deallocate trcInfo
  delete [] ((char *)trcInfo);

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSgTrcInd");
#endif
  return (ROK);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif





/*
 * handler function for SH
 */

// activation function for SH Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmShActvTsk (Pst *aPost, Buffer *apBuf)
{
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmShActvTsk");

  switch(aPost->event)
  {
    case EVTLSHCFGCFM:             /* Config Confirm */
      ret = cmUnpkMiLshCfgCfm(iNGwSmShCfgCfm, aPost, apBuf);
      break;

    case EVTLSHCNTRLCFM:             /* Control Confirm */
      ret = cmUnpkMiLshCntrlCfm(iNGwSmShCtlCfm, aPost, apBuf);
      break;

    case EVTLSHSTACFM:             /* Status Confirmn */
      ret = cmUnpkMiLshStaCfm(iNGwSmShStaCfm, aPost, apBuf);
      break;

    case EVTLSHSTAIND:             /* Status Indication */
      ret = cmUnpkMiLshStaInd(iNGwSmShStaInd, aPost, apBuf);
      break;

    case EVTLSHSTSCFM:             /* Statistics Confirm */
      ret = cmUnpkMiLshStsCfm(iNGwSmShStsCfm, aPost, apBuf);
      break;

    case EVTLSHTRCIND:           /* Trace Confirm */
      ret = cmUnpkMiLshTrcInd(iNGwSmShTrcInd, aPost, apBuf);
      break;

    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected SH mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmShActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// Configuration Confirm Handler of SH Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmShCfgCfm (Pst *aPost, ShMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmShCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTSH, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSgCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Control Confirm handler of SH Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmShCtlCfm (Pst *aPost, ShMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmShCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTSH, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmShCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Status  Confirm handler of SH Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmShStaCfm (Pst *aPost, ShMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmShStaCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(ENTSH, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmShStaCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Status Indication handler of SH Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmShStaInd (Pst *aPost, ShMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmShStaInd");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(ENTSH, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmShStaInd");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// Statics Confirm handler of SH Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmShStsCfm (Pst *aPost, ShMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmShStsCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStsCfmQueMsg(ENTSH, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmShStsCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Trace Indication handler of SH Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmShTrcInd (Pst *aPost, ShMngmt *aTrc)
{
#ifdef INCTCBD
  INGwSmTrcInfo *trcInfo = 0;
  int allocLen = 0;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmShTrcInd");

  // since INGwSmTrcInfo is a variable length structure, so
  // compute it's size in bytes, and then allocate that...
  allocLen = sizeof(INGwSmTrcInfo) + aTrc->t.trc.len - 1;

  trcInfo = (INGwSmTrcInfo *) (new char [allocLen]);

  if (trcInfo == 0) {
    return (RFAILED);
  }

  trcInfo->miLayerId = BP_AIN_SM_SH_LAYER;
  trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_NA;

  if (aTrc->t.trc.trcEvent == LST_MSG_RECVD) {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
  }
  else {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
  }

  trcInfo->hour  = aTrc->t.trc.dt.hour;
  trcInfo->min   = aTrc->t.trc.dt.min;
  trcInfo->sec   = aTrc->t.trc.dt.sec;
  trcInfo->tenth = aTrc->t.trc.dt.tenths;

  trcInfo->miTrcLen = aTrc->t.trc.len;
  memcpy((void *)(trcInfo->maTrcBuf),
      (void *)(aTrc->t.trc.evntParm),
      trcInfo->miTrcLen);

  // invoke TraceHandler to handle this trace info
  INGwSmTrcHdlr *lpTrcHdlr = INGwSmAdaptor::getTrcHdlr();

  if (lpTrcHdlr)
    lpTrcHdlr->handleTrace(trcInfo);

  // since we are done with it, deallocate trcInfo
  delete [] ((char *)trcInfo);

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmStTrcInd");
#endif
  return (ROK);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif






//Configuration Confirm Handler of PSF TCAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmZtCfgCfm (Pst *aPost, ZtMngmt *aCfm) 
{
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmZtCfgCfm");

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTZT, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmZtCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif



/*
 * handler function for TCAP
 */

// activation function for TCAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmStActvTsk (Pst *aPost, Buffer *apBuf)
{
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmStActvTsk");

  switch(aPost->event)
  {
    case EVTLSTSTACFM:             /* Status Confirm */
      ret = cmUnpkLstStaCfm(iNGwSmStStaCfm, aPost, apBuf);
      break;

    case EVTLSTSTSCFM:             /* Statistics Confirm */
      ret = cmUnpkLstStsCfm(iNGwSmStStsCfm, aPost, apBuf);
      break;

    case EVTLSTSTAIND:             /* Status Indication */
      ret = cmUnpkLstStaInd(iNGwSmStStaInd, aPost, apBuf);
      break;

    case EVTLSTTRCIND:             /* Trace Indication */
      ret = cmUnpkLstTrcInd(iNGwSmStTrcInd, aPost, apBuf);
      break;

    case EVTLSTCFGCFM:             /* Config Confirm */
      ret = cmUnpkLstCfgCfm(iNGwSmStCfgCfm, aPost, apBuf);
      break;

    case EVTLSTCNTRLCFM:           /* Control Confirm */
      ret = cmUnpkLstCntrlCfm(iNGwSmStCtlCfm, aPost, apBuf);
      break;

    case EVTZTMILZTCFGCFM:           /* Control Confirm */
      ret = cmUnpkLztCfgCfm(iNGwSmZtCfgCfm, aPost, apBuf);
      break;

    case EVTZTMILZTCNTRLCFM:           /* Control Confirm */
      ret = cmUnpkLztCntrlCfm(iNGwSmZtCtlCfm, aPost, apBuf);
      break;

    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected TCAP mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmStActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Configuration Confirm Handler of TCAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmStCfgCfm (Pst *aPost, StMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmStCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTST, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmStCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// Control Confirm handler of TCAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmZtCtlCfm (Pst *aPost, ZtMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmZtCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTZT, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmZtCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif



// Control Confirm handler of TCAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmStCtlCfm (Pst *aPost, StMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmStCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTST, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmStCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Status Confirm handler of TCAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmStStaCfm (Pst *aPost, StMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmStStaCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(ENTST, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmStStaCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Statistics Confirm handler of TCAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmStStsCfm (Pst *aPost, StMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmStStsCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStsCfmQueMsg(ENTST, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmStStsCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Trace Indication handler of TCAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmStTrcInd (Pst *aPost, StMngmt *aTrc)
{
  INGwSmTrcInfo *trcInfo = 0;
  int allocLen = 0;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmStTrcInd");

  // since INGwSmTrcInfo is a variable length structure, so
  // compute it's size in bytes, and then allocate that...
  allocLen = sizeof(INGwSmTrcInfo) + aTrc->t.trc.len - 1;

  trcInfo = (INGwSmTrcInfo *) (new char [allocLen]);

  if (trcInfo == 0) {
    return (RFAILED);
  }

  trcInfo->miLayerId = BP_AIN_SM_TCA_LAYER;
  trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_NA;

  if (aTrc->t.trc.evnt == LST_MSG_RECVD) {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
  }
  else {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
  }

  trcInfo->hour  = aTrc->t.trc.dt.hour;
  trcInfo->min   = aTrc->t.trc.dt.min;
  trcInfo->sec   = aTrc->t.trc.dt.sec;
  trcInfo->tenth = aTrc->t.trc.dt.tenths;

  trcInfo->miTrcLen = aTrc->t.trc.len;
  memcpy((void *)(trcInfo->maTrcBuf),
         (void *)(aTrc->t.trc.evntParm),
         trcInfo->miTrcLen);

  // invoke TraceHandler to handle this trace info
  INGwSmTrcHdlr *lpTrcHdlr = INGwSmAdaptor::getTrcHdlr();

  if (lpTrcHdlr)
    lpTrcHdlr->handleTrace(trcInfo);

  // since we are done with it, deallocate trcInfo
  delete [] ((char *)trcInfo);

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmStTrcInd");

  return (ROK);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Unsolicited Status Indication of TCAP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmStStaInd (Pst *aPost, StMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmStStaInd");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(ENTST, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmStStaInd");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif



/*
 * handler function for SCCP
 */

// activation function for SCCP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSpActvTsk (Pst *aPost, Buffer *apBuf)
{
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSpActvTsk");

  switch(aPost->event)
  {
    case EVTLSPSTSCFM:             /* Statistics Confirm */
      ret = cmUnpkLspStsCfm(iNGwSmSpStsCfm, aPost, apBuf);
      break;

    case EVTLSPSTAIND:             /* Status Indication */
      ret = cmUnpkLspStaInd(iNGwSmSpStaInd, aPost, apBuf);
      break;

    case EVTLSPSTACFM:             /* Status Confirm */
      ret = cmUnpkLspStaCfm(iNGwSmSpStaCfm, aPost, apBuf);
      break;

    case EVTLSPTRCIND:             /* Trace Indication */
      ret = cmUnpkLspTrcInd(iNGwSmSpTrcInd, aPost, apBuf);
      break;

    case EVTLSPCFGCFM:             /* Config Confirm */
      ret = cmUnpkLspCfgCfm(iNGwSmSpCfgCfm, aPost, apBuf);
      break;

    case EVTLSPCNTRLCFM:           /* control Confirm */
      ret = cmUnpkLspCntrlCfm(iNGwSmSpCtlCfm, aPost, apBuf);
      break;

    case EVTZPMILZPCFGCFM:             /* PSF Configuration Confirm */
      if ((ret = cmUnpkLzpCfgCfm(iNGwSmZpCfgCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTZPMILZPCNTRLCFM:             /* PSF Configuration Confirm */
      if ((ret = cmUnpkLzpCntrlCfm(iNGwSmZpCtlCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected SCCP mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSpActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Configuration Confirm Handler of SCCP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSpCfgCfm (Pst *aPost, SpMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSpCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTSP, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSpCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// Control Confirm handler of SCCP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmZpCtlCfm (Pst *aPost, ZpMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmZpCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTZP, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmZpCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// Control Confirm handler of SCCP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSpCtlCfm (Pst *aPost, SpMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSpCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTSP, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSpCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Status Confirm handler of SCCP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSpStaCfm (Pst *aPost, SpMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSpStaCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(ENTSP, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSpStaCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Statistics Confirm handler of SCCP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSpStsCfm (Pst *aPost, Action aAct, SpMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSpStsCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStsCfmQueMsg(ENTSP, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSpStsCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Trace Indication handler of SCCP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSpTrcInd (Pst *aPost, SpMngmt *aTrc)
{
  INGwSmTrcInfo *trcInfo = 0;
  int allocLen = 0;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSpTrcInd");

  // since INGwSmTrcInfo is a variable length structure, so
  // compute it's size in bytes, and then allocate that...
  allocLen = sizeof(INGwSmTrcInfo) + aTrc->t.trc.len - 1;

  trcInfo = (INGwSmTrcInfo *) (new char [allocLen]);

  if (trcInfo == 0) {
    return (RFAILED);
  }

  trcInfo->miLayerId = BP_AIN_SM_SCC_LAYER;
  trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_NA;

  if (aTrc->t.trc.evnt == SP_MSG_RECVD) {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
  }
  else {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
  }

  trcInfo->hour  = aTrc->t.trc.dt.hour;
  trcInfo->min   = aTrc->t.trc.dt.min;
  trcInfo->sec   = aTrc->t.trc.dt.sec;
  trcInfo->tenth = aTrc->t.trc.dt.tenths;

  trcInfo->miTrcLen = aTrc->t.trc.len;
  memcpy((void *)(trcInfo->maTrcBuf),
         (void *)(aTrc->t.trc.evntParm),
         trcInfo->miTrcLen);

  // invoke TraceHandler to handle this trace info
  INGwSmTrcHdlr *lpTrcHdlr = INGwSmAdaptor::getTrcHdlr();

  if (lpTrcHdlr)
    lpTrcHdlr->handleTrace(trcInfo);


  // since we are done with it, deallocate trcInfo
  delete [] ((char *)trcInfo);

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSpTrcInd");

  return (ROK);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Unsolicited Status Indication of SCCP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSpStaInd (Pst *aPost, SpMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSpStaInd");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(ENTSP, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSpStaInd");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


/*
 * handler function for M3UA
 */

// activation function for M3UA
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmItActvTsk (Pst *aPost, Buffer *apBuf)
{
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmItActvTsk");

  switch(aPost->event)
  {
    case EVTLITCFGCFM:             /* Config confirm */
      ret = cmUnpkLitCfgCfm(iNGwSmItCfgCfm, aPost, apBuf);
      break;

    case EVTLITCNTRLCFM:           /* Control confirm */
      ret = cmUnpkLitCntrlCfm(iNGwSmItCtlCfm, aPost, apBuf);
      break;

    case EVTLITSTACFM:             /* Status Confirm */
      ret = cmUnpkLitStaCfm(iNGwSmItStaCfm, aPost, apBuf);
      break;

    case EVTLITSTSCFM:             /* Statistics Confirm */
      ret = cmUnpkLitStsCfm(iNGwSmItStsCfm, aPost, apBuf);
      break;

    case EVTLITSTAIND:             /* Status Indication */
      ret = cmUnpkLitStaInd(iNGwSmItStaInd, aPost, apBuf);
      break;

    case EVTLITTRCIND:             /* Trace Indication */
      ret = cmUnpkLitTrcInd(iNGwSmItTrcInd, aPost, apBuf);
      break;

    case EVTZVMILZVCFGCFM:             /* PSF M3UA Config confirm */
      ret = cmUnpkLzvCfgCfm(iNGwSmZvCfgCfm, aPost, apBuf);
      break;

    case EVTZVMILZVCNTRLCFM:             /* PSF M3UA Config confirm */
      ret = cmUnpkLzvCntrlCfm(iNGwSmZvCntrlCfm, aPost, apBuf);
      break;


    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected M3UA mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmItActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


/////////////////////////////////////////////////////
//
// DV layer (M3UA)
//
/////////////////////////////////////////////////////

//activation function for M3UA Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmDvActvTsk (Pst *aPost, Buffer *apBuf) {
  short  ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
        "Entering iNGwSmDvActvTsk");

  switch(aPost->event)
  {
    case EVTLDVCFGCFM:             /* Configuration Confirm */
      if ((ret = cmUnpkLdvCfgCfm(iNGwSmDvCfgCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;
    case EVTLDVCNTRLCFM:           /* Conntrol Confirm */
      if ((ret = cmUnpkLdvCntrlCfm(iNGwSmDvCtlCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

   #ifdef INCTBD
/* need to similarly define for cntrl confirm and all */ 
    case EVTLITSTSCFM:             /* Statistics Confirm */
      if ((ret = cmUnpkLitStsCfm(iNGwSmItStsCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLITCNTRLCFM:           /* Conntrol Confirm */
      if ((ret = cmUnpkLitCntrlCfm(iNGwSmItCtlCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLITSTACFM:             /* Status Confirm */
      if ((ret = cmUnpkLitStaCfm(iNGwSmItStaCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLITSTAIND:             /* Status Indication */
      if ((ret = cmUnpkLitStaInd(iNGwSmItStaInd, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLITTRCIND:             /* Trace Indication */
      if ((ret = cmUnpkLitTrcInd(iNGwSmItTrcInd, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;
#endif
    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected MTP3 mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
             "Leaving iNGwSmDvActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

//Configuration Confirm Handler of LDF M3UA Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmDvCfgCfm (Pst *aPost, LdvMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmDvCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTDV, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmDvCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


//Control Confirm Handler of LDF M3UA Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmDvCtlCfm (Pst *aPost, LdvMngmt *aCfm) {
      
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
      
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmDvCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTDV, aCfm)) == 0) {
    return (RFAILED);
  }   
      
  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }   

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmDvCtlCfm");
      
  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// Configuration Confirm Handler of M3UA Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmItCfgCfm (Pst *aPost, ItMgmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmItCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTIT, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmItCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


//Configuration Confirm Handler of PSF M3UA Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmZvCfgCfm (Pst *aPost, ZvMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmZvCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTZV, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmZvCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif



//Control Confirm Handler of PSF M3UA Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmZvCntrlCfm (Pst *aPost, ZvMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmZvCntrlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTZV, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmZvCntrlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif



// Control Confirm handler of M3UA Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmItCtlCfm (Pst *aPost, ItMgmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmItCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTIT, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmItCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Status Confirm handler of M3UA Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmItStaCfm (Pst *aPost, ItMgmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmItStaCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(ENTIT, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmItStaCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Statistics Confirm handler of M3UA Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmItStsCfm (Pst *aPost, ItMgmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmItStsCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStsCfmQueMsg(ENTIT, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmItStsCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Trace Indication handler of M3UA Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmItTrcInd (Pst *aPost, ItMgmt *aTrc)
{
  INGwSmTrcInfo *trcInfo = 0;
  int allocLen = 0;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmItTrcInd");

  // since INGwSmTrcInfo is a variable length structure, so
  // compute it's size in bytes, and then allocate that...
  allocLen = sizeof(INGwSmTrcInfo) + aTrc->t.trc.len - 1;

  trcInfo = (INGwSmTrcInfo *) (new char [allocLen]);

  if (trcInfo == 0) {
    return (RFAILED);
  }

  trcInfo->miLayerId = BP_AIN_SM_M3U_LAYER;

  // well here i use it for msg-type.
  //trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_MSG;
  
  switch (aTrc->t.trc.evnt) {

    /* Trace for the message received */             
    case LIT_MSG_RX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_MSG;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
      break;

    /* Trace for the message transmitted */          
    case LIT_MSG_TX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_MSG;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
      break;

    /* Trace for the MGMT msg received */            
    case LIT_MGMT_RX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_MGMT;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
      break;

    /* Trace for the M3UA DATA msg received */       
    case LIT_DATA_RX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_DATA;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
      break;

    /* Trace for the M3UA SSNM msg received */       
    case LIT_SSNM_RX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_SSNM;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
      break;

    /* Trace for the M3UA ASPSM msg received */      
    case LIT_ASPSM_RX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_ASPSM;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
      break;

    /* Trace for the M3UA ASPTM msg received */      
    case LIT_ASPTM_RX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_ASPTM;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
      break;

    /* Trace for the M3UA RKM msg received */        
    case LIT_RKM_RX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_RKM;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
      break;

    /* Trace for Management */                        
    case LIT_MGMT_TX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_MGMT;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
      break;

    /* Trace for M3UA data transfer message class */  
    case LIT_DATA_TX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_DATA;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
      break;

    /* Trace for SS7 network management msg class */  
    case LIT_SSNM_TX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_SSNM;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
      break;

    /* Trace for ASP state maintenance msg class */   
    case LIT_ASPSM_TX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_ASPSM;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
      break;

    /* Trace for ASP traffic maintenance msg class */ 
    case LIT_ASPTM_TX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_ASPTM;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
      break;

    /* Trace for Routing Key Mgmt message class */ 
    case LIT_RKM_TX :
      trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_M3U_RKM;
      trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
      break;

    default:
      // since we are done with it, deallocate trcInfo
      delete [] ((char *)trcInfo);

      logger.logMsg (TRACE_FLAG, 0,
          "iNGwSmItTrcInd - Invlid eventType:%d", aTrc->t.trc.evnt);

      return (RFAILED);
  }

  trcInfo->hour  = aTrc->t.trc.dt.hour;
  trcInfo->min   = aTrc->t.trc.dt.min;
  trcInfo->sec   = aTrc->t.trc.dt.sec;
  trcInfo->tenth = aTrc->t.trc.dt.tenths;

  trcInfo->miTrcLen = aTrc->t.trc.len;
  memcpy((void *)(trcInfo->maTrcBuf),
         (void *)(aTrc->t.trc.evntParm),
         trcInfo->miTrcLen);

  // invoke TraceHandler to handle this trace info
  INGwSmTrcHdlr *lpTrcHdlr = INGwSmAdaptor::getTrcHdlr();

  if (lpTrcHdlr)
    lpTrcHdlr->handleTrace(trcInfo);


  // since we are done with it, deallocate trcInfo
  delete [] ((char *)trcInfo);

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmItTrcInd");

  return (ROK);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Unsolicited Status Indication of M3UA Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmItStaInd (Pst *aPost, ItMgmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmItStaInd");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(ENTIT, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmItStaInd");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


/*
 * handler function for SCTP
 */

// activation function for SCTP
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSbActvTsk (Pst *aPost, Buffer *apBuf)
{
  short ret;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSbActvTsk");

  ret = ROK;

  switch(aPost->event)
  {
    case LSB_EVTCFGCFM:             /* Config confirm */
      ret = cmUnpkLsbCfgCfm(iNGwSmSbCfgCfm, aPost, apBuf);
      break;

    case LSB_EVTCNTRLCFM:           /* Control confirm */
      ret = cmUnpkLsbCntrlCfm(iNGwSmSbCtlCfm, aPost, apBuf);
      break;

    case LSB_EVTSTACFM:             /* Status Confirm */
      ret = cmUnpkLsbStaCfm(iNGwSmSbStaCfm, aPost, apBuf);
      break;

    case LSB_EVTSTSCFM:             /* Statistics Confirm */
      ret = cmUnpkLsbStsCfm(iNGwSmSbStsCfm, aPost, apBuf);
      break;

    case LSB_EVTSTAIND:             /* Status Indication */
      ret = cmUnpkLsbStaInd(iNGwSmSbStaInd, aPost, apBuf);
      break;

    case LSB_EVTTRCIND:             /* Trace Indication */
      ret = cmUnpkLsbTrcInd(iNGwSmSbTrcInd, aPost, apBuf);
      break;

    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected SCTP mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSbActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Configuration Confirm Handler of SCTP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSbCfgCfm (Pst *aPost, SbMgmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSbCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTSB, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSbCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Control Confirm handler of SCTP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSbCtlCfm (Pst *aPost, SbMgmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSbCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTSB, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSbCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Status Confirm handler of SCTP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSbStaCfm (Pst *aPost, SbMgmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSbStaCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(ENTSB, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSbStaCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Statistics Confirm handler of SCTP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSbStsCfm (Pst *aPost, SbMgmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSbStsCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStsCfmQueMsg(ENTSB, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSbStsCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Trace Indication handler of SCTP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSbTrcInd (Pst *aPost, SbMgmt *aTrc)
{
  INGwSmTrcInfo *trcInfo = 0;
  int allocLen = 0;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSbTrcInd");

  // since INGwSmTrcInfo is a variable length structure, so
  // compute it's size in bytes, and then allocate that...
  allocLen = sizeof(INGwSmTrcInfo) + aTrc->t.trc.len - 1;

  trcInfo = (INGwSmTrcInfo *) (new char [allocLen]);

  if (trcInfo == 0) {
    return (RFAILED);
  }

  trcInfo->miLayerId = BP_AIN_SM_SCT_LAYER;
  trcInfo->miSubLayerId = BP_AIN_SM_SUBLYR_NA;

  if (aTrc->t.trc.evnt == LSB_MSG_RECVD) {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_RX;
  }
  else {
    trcInfo->miDir = BP_AIN_SM_TRCDIR_TX;
  }

  trcInfo->hour  = aTrc->t.trc.dt.hour;
  trcInfo->min   = aTrc->t.trc.dt.min;
  trcInfo->sec   = aTrc->t.trc.dt.sec;
  trcInfo->tenth = aTrc->t.trc.dt.tenths;

  trcInfo->miTrcLen = aTrc->t.trc.len;
  memcpy((void *)(trcInfo->maTrcBuf),
         (void *)(aTrc->t.trc.evntParm),
         trcInfo->miTrcLen);

  // invoke TraceHandler to handle this trace info
  INGwSmTrcHdlr *lpTrcHdlr = INGwSmAdaptor::getTrcHdlr();

  if (lpTrcHdlr)
    lpTrcHdlr->handleTrace(trcInfo);


  // since we are done with it, deallocate trcInfo
  delete [] ((char *)trcInfo);

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSbTrcInd");

  return (ROK);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Unsolicited Status Indication of SCTP Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmSbStaInd (Pst *aPost, SbMgmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSbStaInd");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(ENTSB, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSbStaInd");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


/*
 * handler function for TUCL
 */

// activation function for TUCL Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmHiActvTsk (Pst *aPost, Buffer *apBuf)
{
  short  ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmHiActvTsk");

  switch(aPost->event)
  {
    case EVTLHICFGCFM:             /* Configuration Confirm */
      if ((ret = cmUnpkLhiCfgCfm(iNGwSmHiCfgCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;
    
    case EVTLHISTSCFM:             /* Statistics Confirm */
      if ((ret = cmUnpkLhiStsCfm(iNGwSmHiStsCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLHICNTRLCFM:           /* Conntrol Confirm */
      if ((ret = cmUnpkLhiCntrlCfm(iNGwSmHiCtlCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLHISTACFM:             /* Status Confirm */
      if ((ret = cmUnpkLhiStaCfm(iNGwSmHiStaCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLHISTAIND:             /* Status Indication */
      if ((ret = cmUnpkLhiStaInd(iNGwSmHiStaInd, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLHITRCIND:             /* Trace Indication */
      if ((ret = cmUnpkLhiTrcInd(iNGwSmHiTrcInd, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected TUCL mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmHiActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Configuration Confirm Handler of TUCL Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmHiCfgCfm (Pst *aPost, HiMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmHiCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTHI, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmHiCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Control Confirm handler of TUCL Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmHiCtlCfm (Pst *aPost, HiMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmHiCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTHI, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmHiCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Status Confirm handler of TUCL Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmHiStaCfm (Pst *aPost, HiMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmHiStaCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(ENTHI, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmHiStaCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

// Statistics Confirm handler of TUCL Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmHiStsCfm (Pst *aPost, HiMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmHiStsCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStsCfmQueMsg(ENTHI, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmHiStsCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// Trace Indication handler of TUCL Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmHiTrcInd (Pst *aPost, HiMngmt *aTrc, Buffer *apBuf)
{
  INGwSmTrcInfo trcInfo;
  int allocLen = 0;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmHiTrcInd");

  // although INGwSmTrcInfo is a variable length structure,
  // TUCL trace doesn't have any real data, just direction
  // indication, we don't need dynamic alloc unlike other
  // layers.
  trcInfo.miTrcLen = 0;

  trcInfo.miLayerId = BP_AIN_SM_TUC_LAYER;

  switch (aTrc->t.trc.evnt) {

    case LHI_TCP_RXED:
      trcInfo.miSubLayerId = BP_AIN_SM_SUBLYR_TCP;
      trcInfo.miDir = BP_AIN_SM_TRCDIR_RX;
      break;

    case LHI_TCP_TXED:
      trcInfo.miSubLayerId = BP_AIN_SM_SUBLYR_TCP;
      trcInfo.miDir = BP_AIN_SM_TRCDIR_TX;
      break;

    case LHI_UDP_RXED:
      trcInfo.miSubLayerId = BP_AIN_SM_SUBLYR_UDP;
      trcInfo.miDir = BP_AIN_SM_TRCDIR_RX;
      break;

    case LHI_UDP_TXED:
      trcInfo.miSubLayerId = BP_AIN_SM_SUBLYR_UDP;
      trcInfo.miDir = BP_AIN_SM_TRCDIR_TX;
      break;

    case LHI_RAW_RXED:
      trcInfo.miSubLayerId = BP_AIN_SM_SUBLYR_RAW;
      trcInfo.miDir = BP_AIN_SM_TRCDIR_RX;
      break;

    case LHI_RAW_TXED:
      trcInfo.miSubLayerId = BP_AIN_SM_SUBLYR_RAW;
      trcInfo.miDir = BP_AIN_SM_TRCDIR_TX;
      break;

    default:
      return (RFAILED);
  }

  trcInfo.hour  = aTrc->t.trc.dt.hour;
  trcInfo.min   = aTrc->t.trc.dt.min;
  trcInfo.sec   = aTrc->t.trc.dt.sec;
  trcInfo.tenth = aTrc->t.trc.dt.tenths;

  // invoke TraceHandler to handle this trace info
  INGwSmTrcHdlr *lpTrcHdlr = INGwSmAdaptor::getTrcHdlr();

  if (lpTrcHdlr)
    lpTrcHdlr->handleTrace(&trcInfo);


  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmHiTrcInd");

  return (ROK);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


// Unsolicited Status Indication of TUCL Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short
iNGwSmHiStaInd (Pst *aPost, HiMngmt *aCfm)
{
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmHiStaInd");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(ENTHI, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmHiStaInd");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

/////////////////////////////////////////////////////
//
// DN layer (MTP3)
//
/////////////////////////////////////////////////////

//activation function for MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmDnActvTsk (Pst *aPost, Buffer *apBuf) {
  short  ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
        "Entering iNGwSmDnActvTsk");

  switch(aPost->event)
  {
    case EVTLDNCFGCFM:             /* Configuration Confirm */
      if ((ret = cmUnpkLdnCfgCfm(iNGwSmDnCfgCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;
    case EVTLDNCNTRLCFM:           /* Conntrol Confirm */
      if ((ret = cmUnpkLdnCntrlCfm(iNGwSmDnCtlCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

   #ifdef INCTBD
/* need to similarly define for cntrl confirm and all */ 
    case EVTLSNSTSCFM:             /* Statistics Confirm */
      if ((ret = cmUnpkLsnStsCfm(iNGwSmSnStsCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLSNSTACFM:             /* Status Confirm */
      if ((ret = cmUnpkLsnStaCfm(iNGwSmSnStaCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLSNSTAIND:             /* Status Indication */
      if ((ret = cmUnpkLsnStaInd(iNGwSmSnStaInd, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLSNTRCIND:             /* Trace Indication */
      if ((ret = cmUnpkLsnTrcInd(iNGwSmSnTrcInd, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;
#endif
    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected MTP3 mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
             "Leaving iNGwSmDnActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

//Configuration Confirm Handler of LDF MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmDnCfgCfm (Pst *aPost, LdnMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmDnCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTDN, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmDnCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

/////////////////////////////////////////////////////
//
// SN layer (MTP3)
//
/////////////////////////////////////////////////////

//activation function for MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmSnActvTsk (Pst *aPost, Buffer *apBuf) {
  short  ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
        "Entering iNGwSmSnActvTsk");

  switch(aPost->event)
  {
    case EVTLSNCFGCFM:             /* Configuration Confirm */
      if ((ret = cmUnpkLsnCfgCfm(iNGwSmSnCfgCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;
    
    case EVTLSNSTSCFM:             /* Statistics Confirm */
      if ((ret = cmUnpkLsnStsCfm(iNGwSmSnStsCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLSNCNTRLCFM:           /* Conntrol Confirm */
      if ((ret = cmUnpkLsnCntrlCfm(iNGwSmSnCtlCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLSNSTACFM:             /* Status Confirm */
      if ((ret = cmUnpkLsnStaCfm(iNGwSmSnStaCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLSNSTAIND:             /* Status Indication */
      if ((ret = cmUnpkLsnStaInd(iNGwSmSnStaInd, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLSNTRCIND:             /* Trace Indication */
      if ((ret = cmUnpkLsnTrcInd(iNGwSmSnTrcInd, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTZNMILZNCFGCFM:             /* PSF Configuration Confirm */
      if ((ret = cmUnpkLznCfgCfm(iNGwSmZnCfgCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;
    case EVTZNMILZNCNTRLCFM:             /* PSF Configuration Confirm */
      if ((ret = cmUnpkLznCntrlCfm(iNGwSmZnCtlCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected MTP3 mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
             "Leaving iNGwSmSnActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif





//Configuration Confirm Handler of PSF MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmZpCfgCfm (Pst *aPost, ZpMngmt *aCfm) 
{
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmZpCfgCfm");

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTZP, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmZpCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


//Configuration Confirm Handler of PSF MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmZnCfgCfm (Pst *aPost, ZnMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmZnCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTZN, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmZnCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


//Configuration Confirm Handler of MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmSnCfgCfm (Pst *aPost, SnMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmSnCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTSN, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmSnCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


//Control Confirm handler of MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmDnCtlCfm (Pst *aPost, LdnMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmDnCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTDN, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmDnCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


//Control Confirm handler of MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmZnCtlCfm (Pst *aPost, ZnMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmZnCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTZN, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmZnCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif



//Control Confirm handler of MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmSnCtlCfm (Pst *aPost, SnMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmSnCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTSN, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmSnCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

//Status Confirm handler of MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmSnStaCfm (Pst *aPost, SnMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSnStaCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(ENTSN, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK)
    ret = RFAILED;

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSnStaCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

//Statistics Confirm handler of MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmSnStsCfm (Pst *aPost, Action action, SnMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmSnStsCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStsCfmQueMsg(ENTSN, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmSnStsCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

//Trace Indication handler of MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmSnTrcInd (Pst *aPost, SnMngmt *aTrc){

  return ROK;
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

//Unsolicited Status Indication of MTP3 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmSnStaInd (Pst *aPost, SnMngmt *aCfm){
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmSnStaInd");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(ENTSN, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmSnStaInd");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


/////////////////////////////////////////////////////
//
// SD layer (MTP2)
//
/////////////////////////////////////////////////////
//activation function for MTP2 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C"  {
#endif
short 
iNGwSmSdActvTsk (Pst *aPost, Buffer *apBuf) {
  short  ret = ROK;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering iNGwSmSdActvTsk");

  switch(aPost->event)
  {
    case EVTLSDCFGCFM:             /* Configuration Confirm */
      if ((ret = cmUnpkLsdCfgCfm(iNGwSmSdCfgCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;
    
    case EVTLSDSTSCFM:             /* Statistics Confirm */
      if ((ret = cmUnpkLsdStsCfm(iNGwSmSdStsCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLSDCNTRLCFM:           /* Conntrol Confirm */
      if ((ret = cmUnpkLsdCntrlCfm(iNGwSmSdCtlCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLSDSTACFM:             /* Status Confirm */
      if ((ret = cmUnpkLsdStaCfm(iNGwSmSdStaCfm, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLSDSTAIND:             /* Status Indication */
      if ((ret = cmUnpkLsdStaInd(iNGwSmSdStaInd, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    case EVTLSDTRCIND:             /* Trace Indication */
      if ((ret = cmUnpkLsdTrcInd(iNGwSmSdTrcInd, aPost, apBuf)) != ROK)
      {
         return (ret);
      }
      break;

    default:
      SPutMsg(apBuf);
      logger.logMsg(ERROR_FLAG, 0,
                    "Unexpected MTP2 mesg recv'd by AinStackMgr with event: %d",
                    (int)aPost->event);
      ret = RFAILED;
      break;
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving iNGwSmSdActvTsk");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

//Configuration Confirm Handler of MTP2 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
short iNGwSmSdCfgCfm (Pst *aPost, SdMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmSdCfgCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(ENTSD, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmSdCfgCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

//Control Confirm handler of MTP2 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
short iNGwSmSdCtlCfm (Pst *aPost, SdMngmt *aCfm) {

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;
   
  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmSdCtlCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(ENTSD, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmSdCtlCfm");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

//Status Confirm handler of MTP2 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
short iNGwSmSdStaCfm (Pst *aPost, SdMngmt *aCfm){

  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmSdStaCfm");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(ENTSD, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmSdStaCfm");

  return (ret);

}
#ifndef __CCPU_CPLUSPLUS
}
#endif

//Statistics Confirm handler of MTP2 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
short iNGwSmSdStsCfm (Pst *aPost, Action action, SdMngmt *aCfm){

  return ROK;
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

//Trace Indication handler of MTP2 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
short iNGwSmSdTrcInd (Pst *aPost, SdMngmt *aTrc){

  return ROK;
}
#ifndef __CCPU_CPLUSPLUS
}
#endif

//Unsolicited Status Indication of MTP2 Layer
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
short iNGwSmSdStaInd (Pst *aPost, SdMngmt *aCfm){
  INGwSmQueueMsg *qMsg = 0;
  short ret = ROK;

  logger.logMsg (TRACE_FLAG, 0, "Entering iNGwSmSdStaInd");

  if ((qMsg = INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(ENTSD, aCfm)) == 0) {
    return (RFAILED);
  }

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) != BP_AIN_SM_OK) {
    ret = RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving iNGwSmSdStaInd");

  return (ret);
}
#ifndef __CCPU_CPLUSPLUS
}
#endif


/////////////////////////////////////////////////////
//
// Adaptor functions
//
/////////////////////////////////////////////////////

// prepare the config confirm msg
INGwSmQueueMsg*
INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg(int aLayerId,
                                      void* apMgmt)
{
  INGwSmQueueMsg *ret=0;

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg for layerId:%d",
    aLayerId);

  ret = INGwSmAdaptor::iNGwSmMkCfmQueMsg(aLayerId, apMgmt,
                                          BP_AIN_SM_STKOP_CFGCFM);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAdaptor::iNGwSmMkCfgCfmQueMsg");

  return (ret);
}

// prepare the control confirm msg
INGwSmQueueMsg*
INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg(int aLayerId,
                                      void* apMgmt)
{
  INGwSmQueueMsg *ret=0;

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg for layerId:%d",
    aLayerId);

  ret = INGwSmAdaptor::iNGwSmMkCfmQueMsg(aLayerId, apMgmt,
                                          BP_AIN_SM_STKOP_CTLCFM);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAdaptor::iNGwSmMkCtlCfmQueMsg");

  return (ret);
}


// prepare the statistics confirm msg
INGwSmQueueMsg*
INGwSmAdaptor::iNGwSmMkStsCfmQueMsg(int aLayerId,
                                      void* apMgmt)
{
  INGwSmQueueMsg *ret=0;

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAdaptor::iNGwSmMkStsCfmQueMsg for layerId:%d",
    aLayerId);

  ret = INGwSmAdaptor::iNGwSmMkCfmQueMsg(aLayerId, apMgmt,
                                          BP_AIN_SM_STKOP_STSCFM);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAdaptor::iNGwSmMkStsCfmQueMsg");

  return (ret);
}


// creates a INGwSmQueueMsg containing a Status Confirmation 
// indication from the stack. This is handled separately as
// it involves copying a "char *" embedded in the SystemID.
INGwSmQueueMsg*
INGwSmAdaptor::iNGwSmMkStaCfmQueMsg(int aLayerId,
                                      void* apMgmt)
{
  INGwSmQueueMsg *qMsg = 0;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering INGwSmAdaptor::iNGwSmMkStaCfmQueMsg - for layedId:%d",
      aLayerId);

  if (apMgmt == 0) {
    logger.logMsg (WARNING_FLAG, 0,
        "INGwSmAdaptor::iNGwSmMkStaCfmQueMsg - Bad Parameter.");
    return (0);
  }

  if ((qMsg = new INGwSmQueueMsg) == 0) {
    logger.logMsg (ERROR_FLAG, 0,
        "INGwSmAdaptor::iNGwSmMkStaCfmQueMsg - Memory allocation failed.");
    return (0);
  }

  memset (qMsg, 0, sizeof (INGwSmQueueMsg));

  qMsg->mSrc = BP_AIN_SM_SRC_STACK;             // msg from stack
  qMsg->t.stackMsg.stkMsgTyp = BP_AIN_SM_STKOP_STACFM; // status cfm

  switch (aLayerId) {
#if 0
    case ENTIE:       // INAP layer
    {
      IeMngmt&  mgt = *(IeMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_AIN_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.ie = mgt;
      if (mgt.hdr.elmId.elmnt == STSID) {
        qMsg->t.stackMsg.stkMsg.lyr.ie.t.ssta.s.sysId.ptNmb = 
            new char [ strlen(mgt.t.ssta.s.sysId.ptNmb) + 1 ]; // to be freed by user
        strcpy(qMsg->t.stackMsg.stkMsg.lyr.ie.t.ssta.s.sysId.ptNmb,
               mgt.t.ssta.s.sysId.ptNmb);
      }
      qMsg->t.stackMsg.stkMsg.miTransId = mgt.hdr.transId;
      break;
    }
#endif
    case ENTSG:       // SG layer
    {
      SgMngmt&  mgt = *(SgMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_SG_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.sg = mgt;
      if (mgt.hdr.elmId.elmnt == STSID) {
        qMsg->t.stackMsg.stkMsg.lyr.sg.t.hi.ssta.u.sid.ptNmb =
            new char [ strlen(mgt.t.hi.ssta.u.sid.ptNmb) + 1 ]; // to be freed by user
        strcpy(qMsg->t.stackMsg.stkMsg.lyr.sg.t.hi.ssta.u.sid.ptNmb,
               mgt.t.hi.ssta.u.sid.ptNmb);
      }
      qMsg->t.stackMsg.stkMsg.miTransId = mgt.hdr.transId;
      break;
    }



    case ENTST:       // TCAP layer
    {
      StMngmt&  mgt = *(StMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_TCA_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.st = mgt;
      if (mgt.hdr.elmId.elmnt == STSID) {
        qMsg->t.stackMsg.stkMsg.lyr.st.t.ssta.s.sysId.ptNmb = 
            new char [ strlen(mgt.t.ssta.s.sysId.ptNmb) + 1 ]; // to be freed by user
        strcpy(qMsg->t.stackMsg.stkMsg.lyr.st.t.ssta.s.sysId.ptNmb,
               mgt.t.ssta.s.sysId.ptNmb);
      }
      qMsg->t.stackMsg.stkMsg.miTransId = mgt.hdr.transId;
      break;
    }

    case ENTSP:       // SCCP layer
    {
      SpMngmt&  mgt = *(SpMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_SCC_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.sp = mgt;
      if (mgt.hdr.elmId.elmnt == STSID) {
        qMsg->t.stackMsg.stkMsg.lyr.sp.t.ssta.s.sysId.ptNmb = 
            new char [ strlen(mgt.t.ssta.s.sysId.ptNmb) + 1 ]; // to be freed by user
        strcpy(qMsg->t.stackMsg.stkMsg.lyr.sp.t.ssta.s.sysId.ptNmb,
               mgt.t.ssta.s.sysId.ptNmb);
      }
      else if(mgt.hdr.elmId.elmnt == STROUT)
      {
	// Possible Values
	// SP_ACC, SP_INACC, SP_CONG
	      qMsg->t.stackMsg.stkMsg.lyr.sp.hdr.elmId.elmnt = STROUT;
	      qMsg->t.stackMsg.stkMsg.lyr.sp.t.ssta.s.spRteSta.pcSta.status = 
			      mgt.t.ssta.s.spRteSta.pcSta.status;
        logger.logMsg (TRACE_FLAG, 0,
            "INGwSmAdaptor::iNGwSmMkStaCfmQueMsg - State of DPC <%d>",
           mgt.t.ssta.s.spRteSta.pcSta.status );
      }


      qMsg->t.stackMsg.stkMsg.miTransId = mgt.hdr.transId;
      break;
    }

    case ENTIT:       // M3UA layer
    {
      ItMgmt&   mgt = *(ItMgmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_M3U_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.it = mgt;
      if (mgt.hdr.elmId.elmnt == STITSID) {
        qMsg->t.stackMsg.stkMsg.lyr.it.t.ssta.s.sysId.ptNmb = 
            new char [ strlen(mgt.t.ssta.s.sysId.ptNmb) + 1 ]; // to be freed by user
        strcpy(qMsg->t.stackMsg.stkMsg.lyr.it.t.ssta.s.sysId.ptNmb,
               mgt.t.ssta.s.sysId.ptNmb);
      }

      else if(mgt.hdr.elmId.elmnt == STITPS)
      {
	// Possible Values
	// LIT_AS_UNKNOWN,LIT_AS_DOWN,LIT_AS_INACTIVE,LIT_AS_ACTIVE,LIT_AS_PENDING
	      qMsg->t.stackMsg.stkMsg.lyr.it.hdr.elmId.elmnt = STITPS;
	      qMsg->t.stackMsg.stkMsg.lyr.it.t.ssta.s.psSta.asSt = 
			      mgt.t.ssta.s.psSta.asSt;
        logger.logMsg (TRACE_FLAG, 0,
            "INGwSmAdaptor::iNGwSmMkStaCfmQueMsg - State of AS <%d>",
           mgt.t.ssta.s.psSta.asSt);
      }
      else if(mgt.hdr.elmId.elmnt == STITPSP)
      {
	// Possible Values
	// LIT_ASP_UNSUPP, LIT_ASP_DOWN, LIT_ASP_INACTIVE, LIT_ASP_ACTIVE
	      qMsg->t.stackMsg.stkMsg.lyr.it.hdr.elmId.elmnt = STITPSP;

	      memcpy(&(qMsg->t.stackMsg.stkMsg.lyr.it.t.ssta.s.pspSta), 
			      &(mgt.t.ssta.s.pspSta), sizeof(ItPspSta));
      }

      qMsg->t.stackMsg.stkMsg.miTransId = mgt.hdr.transId;
      break;
    }

    case ENTSB:       // SCTP layer
    {
      SbMgmt&   mgt = *(SbMgmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_SCT_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.sb = mgt;
      if (mgt.hdr.elmId.elmnt == STSBSID) {
        qMsg->t.stackMsg.stkMsg.lyr.sb.t.ssta.s.sysId.ptNmb = 
            new char [ strlen(mgt.t.ssta.s.sysId.ptNmb) + 1 ]; // to be freed by user
        strcpy(qMsg->t.stackMsg.stkMsg.lyr.sb.t.ssta.s.sysId.ptNmb,
               mgt.t.ssta.s.sysId.ptNmb);
      }
      qMsg->t.stackMsg.stkMsg.miTransId = mgt.hdr.transId;
      break;
    }

    case ENTHI:       // TUCL layer
    {
      HiMngmt&  mgt = *(HiMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_TUC_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.hi = mgt;
      if (mgt.hdr.elmId.elmnt == STSID) {
        qMsg->t.stackMsg.stkMsg.lyr.hi.t.ssta.s.sysId.ptNmb = 
            new char [ strlen(mgt.t.ssta.s.sysId.ptNmb) + 1 ]; // to be freed by user
        strcpy(qMsg->t.stackMsg.stkMsg.lyr.hi.t.ssta.s.sysId.ptNmb,
               mgt.t.ssta.s.sysId.ptNmb);
      }
      qMsg->t.stackMsg.stkMsg.miTransId = mgt.hdr.transId;
      break;
    }

    case ENTSN:      //MTP3 layer
    {
      SnMngmt&  mgt = *(SnMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_MTP3_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.sn = mgt;
      if (mgt.hdr.elmId.elmnt == STSID) {
        qMsg->t.stackMsg.stkMsg.lyr.sn.t.ssta.s.sysId.ptNmb = 
            new char [ strlen(mgt.t.ssta.s.sysId.ptNmb) + 1 ]; // to be freed by user
        strcpy(qMsg->t.stackMsg.stkMsg.lyr.sn.t.ssta.s.sysId.ptNmb,
               mgt.t.ssta.s.sysId.ptNmb);
      }
      else if(mgt.hdr.elmId.elmnt == STDLSAP)
      {
	// Possible Values
	// LSN_LST_INACTIVE, LSN_LST_CON, LSN_LST_ACTIVE
	// LSN_LST_FAILED, LSN_LST_WAITCON, LSN_LST_SUSPEND
	      qMsg->t.stackMsg.stkMsg.lyr.sn.hdr.elmId.elmnt = STDLSAP;
	      qMsg->t.stackMsg.stkMsg.lyr.sn.t.ssta.s.snDLSAP.state = 
			      mgt.t.ssta.s.snDLSAP.state;
	      //qMsg->t.stackMsg.stkMsg.lyr.sn.t.ssta.s.snDLSAP.lnkSetId = 
			  //    mgt.t.ssta.s.snDLSAP.lnkSetId;
        logger.logMsg (TRACE_FLAG, 0,
            "INGwSmAdaptor::iNGwSmMkStaCfmQueMsg - State of Link <%d>",
           mgt.t.ssta.s.snDLSAP.state );
      }
      else if(mgt.hdr.elmId.elmnt == STLNKSET)
      {
	// Possible Values
	// LSN_SET_ACTIVE, LSN_SET_INACTIVE
	      qMsg->t.stackMsg.stkMsg.lyr.sn.hdr.elmId.elmnt = STLNKSET;
	      qMsg->t.stackMsg.stkMsg.lyr.sn.t.ssta.s.snLnkSet.state = 
			      mgt.t.ssta.s.snLnkSet.state;
        logger.logMsg (TRACE_FLAG, 0,
            "INGwSmAdaptor::iNGwSmMkStaCfmQueMsg - State of LinkSet <%d>",
           mgt.t.ssta.s.snLnkSet.state );
      }

      qMsg->t.stackMsg.stkMsg.miTransId = mgt.hdr.transId;
      break;
    }

    case ENTSD:      //MTP2 layer
    {
      SdMngmt&  mgt = *(SdMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_MTP2_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.sd = mgt;
      if (mgt.hdr.elmId.elmnt == STSID) {
        qMsg->t.stackMsg.stkMsg.lyr.sd.t.ssta.s.sysId.ptNmb = 
            new char [ strlen(mgt.t.ssta.s.sysId.ptNmb) + 1 ]; // to be freed by user
        strcpy(qMsg->t.stackMsg.stkMsg.lyr.sd.t.ssta.s.sysId.ptNmb,
               mgt.t.ssta.s.sysId.ptNmb);
      }
      qMsg->t.stackMsg.stkMsg.miTransId = mgt.hdr.transId;

      break;
    }

    default:
      logger.logMsg (TRACE_FLAG, 0,
        "INGwSmAdaptor::iNGwSmMkStaCfmQueMsg - For unrecognized layer:%d. Discard.",
        aLayerId);
      delete qMsg;

      return (0);
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving INGwSmAdaptor::iNGwSmMkStaCfmQueMsg");

  return (qMsg);
}


// creates a INGwSmQueueMsg containing a Confirmation indication
// from the stack.
INGwSmQueueMsg*
INGwSmAdaptor::iNGwSmMkCfmQueMsg(int aLayerId,
                                   void* apMgmt,
                                   INGwSmStackMsgType aStkMsgTyp)
{
  INGwSmQueueMsg *qMsg = 0;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering INGwSmAdaptor::iNGwSmMkCfmQueMsg layerId:%d type:%d",
      aLayerId, aStkMsgTyp);

  if (apMgmt == 0) {
    logger.logMsg (WARNING_FLAG, 0,
        "INGwSmAdaptor::iNGwSmMkCfmQueMsg - Bad Parameter.");
    return (0);
  }

  if ((qMsg = new INGwSmQueueMsg) == 0) {
    logger.logMsg (ERROR_FLAG, 0,
        "INGwSmAdaptor::iNGwSmMkCfmQueMsg - Memory allocation failed.");
    return (0);
  }

  memset (qMsg, 0, sizeof (INGwSmQueueMsg));

  qMsg->mSrc = BP_AIN_SM_SRC_STACK;             // msg from stack
  qMsg->t.stackMsg.stkMsgTyp = aStkMsgTyp;

  switch (aLayerId) {
#if 0
    case ENTIE:       // INAP layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_AIN_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.ie = *(IeMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((IeMngmt *)apMgmt)->hdr.transId;
      break;
#endif
    case ENTSH:       // SH layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_SH_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.sh = *(ShMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((ShMngmt *)apMgmt)->hdr.transId;
      break;
    case ENTSG:       // SG   layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_SG_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.sg = *(SgMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((SgMngmt *)apMgmt)->hdr.transId;
      break;
    case ENTRY:       // RY   layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_RELAY_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.ry = *(RyMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((RyMngmt *)apMgmt)->hdr.transId;
      break;
    case ENTMR:       // MR layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_MR_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.mr = *(MrMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((MrMngmt *)apMgmt)->hdr.transId;
      break;
    case ENTST:       // TCAP layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_TCA_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.st = *(StMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((StMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTZT:       // PSF SCCP layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_PSF_TCAP_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.zt = *(ZtMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((ZtMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTSP:       // SCCP layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_SCC_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.sp = *(SpMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((SpMngmt *)apMgmt)->hdr.transId;
      break;
    case ENTZP:       // PSF SCCP layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_PSF_SCCP_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.zp = *(ZpMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((ZpMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTIT:       // M3UA layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_M3U_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.it = *(ItMgmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((ItMgmt *)apMgmt)->hdr.transId;
      break;

    case ENTDV:       // LDF M3UA layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_LDF_M3UA_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.dv = *(LdvMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((LdvMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTZV:       // PSF M3UA layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_PSF_M3UA_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.zv = *(ZvMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((ZvMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTSB:       // SCTP layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_SCT_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.sb = *(SbMgmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((SbMgmt *)apMgmt)->hdr.transId;
      break;

    case ENTHI:       // TUCL layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_TUC_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.hi = *(HiMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((HiMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTSN:       // MTP3 layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_MTP3_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.sn = *(SnMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((SnMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTZN:       // PSF MTP3 layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_PSF_MTP3_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.zn = *(ZnMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((ZnMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTDN:       // LDF MTP3 layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_LDF_MTP3_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.dn = *(LdnMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((LdnMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTSD:       // MTP2 layer
      qMsg->t.stackMsg.stkMsg.miLayerId = BP_AIN_SM_MTP2_LAYER;
      qMsg->t.stackMsg.stkMsg.lyr.sd = *(SdMngmt *)apMgmt;
      qMsg->t.stackMsg.stkMsg.miTransId = ((SdMngmt *)apMgmt)->hdr.transId;
      break;

    default:
      logger.logMsg (TRACE_FLAG, 0,
          "INGwSmAdaptor::iNGwSmMkCfmQueMsg - Unrecognized layerId:%d. type:%d. Discard.",
          aLayerId,
          aStkMsgTyp);
      delete qMsg;

      return (0);
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving INGwSmAdaptor::iNGwSmMkCfmQueMsg");

  return (qMsg);
}


// creates a INGwSmQueueMsg containing a Alarm indication and
// information required for handling that alarm.
INGwSmQueueMsg*
INGwSmAdaptor::iNGwSmMkStkAlmQueMsg(int aLayerId, void* apMgmt)
{
  INGwSmQueueMsg *qMsg = 0;

  logger.logMsg (TRACE_FLAG, 0,
      "Entering INGwSmAdaptor::iNGwSmMkStkAlmQueMsg for layerId:%d",
      aLayerId);

  if (apMgmt == 0) {
    // log error - bad parameter
    return (0);
  }

  if ((qMsg = new INGwSmQueueMsg) == 0) {
    // memory alloc failure -- alarm    
    return (0);
  }

  memset (qMsg, 0, sizeof (INGwSmQueueMsg));

  qMsg->mSrc = BP_AIN_SM_SRC_STACK_ALM;                 // alarm msg from stack

  switch (aLayerId) {
#if 0
    case ENTIE:       // INAP layer
      qMsg->t.alarmMsg.almInfo.miLayerId = BP_AIN_SM_AIN_LAYER;
      qMsg->t.alarmMsg.almInfo.lyr.ie = *(IeMngmt *)apMgmt;
      qMsg->t.alarmMsg.almInfo.miTransId = ((IeMngmt *)apMgmt)->hdr.transId;
      break;
#endif
    case ENTMR:       // SG layer
      qMsg->t.alarmMsg.almInfo.miLayerId = BP_AIN_SM_MR_LAYER;
      qMsg->t.alarmMsg.almInfo.lyr.mr = *(MrMngmt *)apMgmt;
      qMsg->t.alarmMsg.almInfo.miTransId = ((MrMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTSG:       // SG layer
      qMsg->t.alarmMsg.almInfo.miLayerId = BP_AIN_SM_SG_LAYER;
      qMsg->t.alarmMsg.almInfo.lyr.sg = *(SgMngmt *)apMgmt;
      qMsg->t.alarmMsg.almInfo.miTransId = ((SgMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTSH:       // SH layer
      qMsg->t.alarmMsg.almInfo.miLayerId = BP_AIN_SM_SH_LAYER;
      qMsg->t.alarmMsg.almInfo.lyr.sh = *(ShMngmt *)apMgmt;
      qMsg->t.alarmMsg.almInfo.miTransId = ((ShMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTST:       // TCAP layer
      qMsg->t.alarmMsg.almInfo.miLayerId = BP_AIN_SM_TCA_LAYER;
      qMsg->t.alarmMsg.almInfo.lyr.st = *(StMngmt *)apMgmt;
      qMsg->t.alarmMsg.almInfo.miTransId = ((StMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTSP:       // SCCP layer
      qMsg->t.alarmMsg.almInfo.miLayerId = BP_AIN_SM_SCC_LAYER;
      qMsg->t.alarmMsg.almInfo.lyr.sp = *(SpMngmt *)apMgmt;
      qMsg->t.alarmMsg.almInfo.miTransId = ((SpMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTIT:       // M3UA layer
      qMsg->t.alarmMsg.almInfo.miLayerId = BP_AIN_SM_M3U_LAYER;
      qMsg->t.alarmMsg.almInfo.lyr.it = *(ItMgmt *)apMgmt;
      qMsg->t.alarmMsg.almInfo.miTransId = ((ItMgmt *)apMgmt)->hdr.transId;
      break;

    case ENTSB:       // SCTP layer
      qMsg->t.alarmMsg.almInfo.miLayerId = BP_AIN_SM_SCT_LAYER;
      qMsg->t.alarmMsg.almInfo.lyr.sb = *(SbMgmt *)apMgmt;
      qMsg->t.alarmMsg.almInfo.miTransId = ((SbMgmt *)apMgmt)->hdr.transId;
      break;

    case ENTHI:       // TUCL layer
      qMsg->t.alarmMsg.almInfo.miLayerId = BP_AIN_SM_TUC_LAYER;
      qMsg->t.alarmMsg.almInfo.lyr.hi = *(HiMngmt *)apMgmt;
      qMsg->t.alarmMsg.almInfo.miTransId = ((HiMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTSN:       // MTP3 layer
      qMsg->t.alarmMsg.almInfo.miLayerId = BP_AIN_SM_MTP3_LAYER;
      qMsg->t.alarmMsg.almInfo.lyr.sn = *(SnMngmt *)apMgmt;
      qMsg->t.alarmMsg.almInfo.miTransId = ((SnMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTSD:       // MTP2 layer
      qMsg->t.alarmMsg.almInfo.miLayerId = BP_AIN_SM_MTP2_LAYER;
      qMsg->t.alarmMsg.almInfo.lyr.sd = *(SdMngmt *)apMgmt;
      qMsg->t.alarmMsg.almInfo.miTransId = ((SdMngmt *)apMgmt)->hdr.transId;
      break;

    case ENTRY:       // Relay layer
      qMsg->t.alarmMsg.almInfo.miLayerId = BP_AIN_SM_RY_LAYER;
      qMsg->t.alarmMsg.almInfo.lyr.ry = *(RyMngmt *)apMgmt;
      qMsg->t.alarmMsg.almInfo.miTransId = ((RyMngmt *)apMgmt)->hdr.transId;
      break;

    default:
      logger.logMsg (ERROR_FLAG, 0,
      "iNGwSmMkStkAlmQueMsg: Unhandled Entity <%d>",aLayerId);
      // log error, unrecognized layerId in stack confirm msg
      delete qMsg;
      return (0);
  }

  logger.logMsg (TRACE_FLAG, 0,
      "Leaving INGwSmAdaptor::iNGwSmMkStkAlmQueMsg");

  return (qMsg);
}


int
INGwSmAdaptor::sendMsgToDispatcher(INGwSmQueueMsg *aQMsg,
                                    bool aPstFlag)
{
  int ret=0;

  logger.logMsg (TRACE_FLAG, 0, 
    "Entering sendMsgToDispatcher.");

  if (INGwSmAdaptor::mState == BP_AIN_SM_SHUTTING_DOWN) {
      logger.logMsg (ERROR_FLAG, 0, 
          "sendMsgToDispatcher - Discarded mesg. as SM state is SHUTTING_DOWN");

      delete aQMsg; 

      return (BP_AIN_SM_OK);
  }

  ret = INGwSmAdaptor::getDistributor()->postMsg(aQMsg, aPstFlag);

  logger.logMsg (TRACE_FLAG, 0, 
    "Leaving sendMsgToDispatcher.");

  return ret;
}
