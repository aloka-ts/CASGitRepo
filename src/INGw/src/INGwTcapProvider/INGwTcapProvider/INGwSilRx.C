#include <INGwInfraUtil/INGwIfrUtlGlbFunc.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
#include "INGwCommonTypes/INCCommons.h"
#include "INGwStackManager/INGwSmCommon.h"
#include "INGwCommonTypes/INCTagLengths.h"
#include "INGwCommonTypes/INCJainConstants.h"
#include "INGwCommonTypes/INCTags.h"
#include "INGwTcapProvider/INGwTcapStatParam.h"
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>

#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
  #include "ss_queue.h"
  #include "ss_mem.h"
  #include "mt_ss.h"

  #include "ss_queue.x"
  #include "ss_task.x"
  #include "ss_msg.x"
  #include "ss_mem.x"
#ifndef __CCPU_CPLUSPLUS
}
#endif


#include<map>
extern map<short,bool> sapStatusMap;
extern pthread_mutex_t mutLockMap;
static initMutex SapStatusLock(mutLockMap);

extern bool gGetReplayFlg();
extern bool gIsSubSeqMsg(int pDlgId);
extern int  tcapLoDlgId;
extern int  tcapHiDlgId;

#define LOCKMAP()                      \
  struct autoMUT { autoMUT() {         \
    pthread_mutex_lock(&mutLockMap);   \
  }                                    \
    ~autoMUT()                         \
  {                                    \
    pthread_mutex_unlock(&mutLockMap); \
  } }at; 

BPLOG("INGwTcapProvider");
////////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2001, Bay Packets Inc.
// All rights reserved.
//
// Filename   : INGwSilRx.C
// Description: This file contains the declaration of the INGwSilRx.  
//   Provide the Receiver side functionality of the AIN Trillim stack SIL
//
// NAME           DATE           REASON
// ----------------------------------------------------------------------------
// SU      31 Jul 2002    Initial Creation
//
///////////////////////////////////////////////////////////////////////////////

// For the Simulator we have to include its Thread Manager header files
#ifdef SIMULATOR
#include <Util/imErrorCodes.h>
#include "ainSim/BpWorkUnit.h"
#include "ainSim/BpCCMThreadMgr.h"

#else

#include "INGwInfraManager/INGwIfrMgrWorkUnit.h"
#include "INGwTcapProvider/INGwTcapWorkUnitMsg.h"
#include "INGwInfraManager/INGwIfrMgrThreadMgr.h"
#include "INGwTcapMessage/TcapMessage.hpp"
#include <sstream>
#include <string.h>
#include <INGwTcapProvider/INGwTcapMsgLogger.h>
// Mriganka - Trillium stack integratio
//#include "BpOCMgr/BpOCMgr.h"
#endif

#include "INGwTcapProvider/INGwSilRx.h"

using namespace std;
bool isReplayInProgress;

#ifdef BP_AIN_UT
#include "INGwProvider/INGwSilDriver.h"
#endif

#include "INGwTcapProvider/INGwTcapIncMsgHandler.h"
#include "INGwTcapProvider/INGwTcapProvider.h"
// Initialization of static variables

INGwSilRx* INGwSilRx::mpSelf = 0;

int chkStkMemUtil()
{
  LogINGwTrace(false, 0, "IN chkStkMemUtil()");
  SsMemDbgInfo memInfo;
  SGetRegInfo(0, &memInfo);
  for (int i = 0; i < memInfo.numBkts; i++)
  {
    //printf("[+INC+] Region     : %d\n             NumBlks    : %d\n"
    //  "             Size       : %d\n             NumAlloc   : %d\n"
    //  "             HeapSize   : %d\n             HeapAlloc  : %d\n"
    //  "             AvailMem   : %d\n\n",
    //  memInfo.region, memInfo.bktDbgTbl[i].numBlks,
    //  memInfo.bktDbgTbl[i].size, memInfo.bktDbgTbl[i].numAlloc,
    //  memInfo.heapSize, memInfo.heapAlloc, memInfo.availmem); fflush(stdout);
  }

  if (BP_AIN_SM_REGION != 0)
  {
    SGetRegInfo(1, &memInfo);
    for (int i = 0; i < memInfo.numBkts; i++)
    {
      //printf("[+INC+] Region     : %d\n             NumBlks    : %d\n"
      //  "             Size       : %d\n             NumAlloc   : %d\n"
      //  "             HeapSize   : %d\n             HeapAlloc  : %d\n"
      //  "             AvailMem   : %d\n\n",
      //  memInfo.region, memInfo.bktDbgTbl[i].numBlks,
      //  memInfo.bktDbgTbl[i].size, memInfo.bktDbgTbl[i].numAlloc,
      //  memInfo.heapSize, memInfo.heapAlloc, memInfo.availmem); fflush(stdout);
    }
  }

  LogINGwTrace(false, 0, "OUT chkStkMemUtil()");
  return 1;
}

/******************************************************************************
 ** METHOD  : Constructor for INGwSilRx
 ** ARGS
 **      IN - BpWorkerClbkIntf , Interface to AIN provider
 **
 ** RETURNS
 **         - None
 **
 ** DESCRIPTION :
 **   Constrcutor which would set the AIN Provider Worker Queue handler
 **   Interface
 *****************************************************************************/

INGwSilRx::INGwSilRx() {
   LogINGwTrace(false, 0, "IN INGwSilRx()");
   LogINGwTrace(false, 0, "OUT INGwSilRx()");
}

INGwSilRx& INGwSilRx::instance() {
   if(!mpSelf)
      mpSelf = new INGwSilRx;
   return *mpSelf;
} // end of instance


/******************************************************************************
 ** METHOD  : Destructor for INGwSilRx
 ** ARGS
 **         - None
 **
 ** RETURNS
 **         - None
 **
 ** DESCRIPTION :
 **   Destructor for INGwSilRx handler
 *****************************************************************************/

INGwSilRx::~INGwSilRx() {
   LogINGwTrace(false, 0, "IN ~INGwSilRx()");
   LogINGwTrace(false, 0, "OUT ~INGwSilRx()");
}

int
INGwSilRx::init(INGwIfrMgrWorkerClbkIntf *apINGwIfrMgrWorkerClbkIntf) {
   LogINGwTrace(false, 0, "IN INGwSilRx::init()");

   mpINGwIfrMgrWorkerClbkIntf = apINGwIfrMgrWorkerClbkIntf;
   
   LogINGwTrace(false, 0, "OUT INGwSilRx::init()");
   return 0;
}



/******************************************************************************
 ** METHOD  : start
 ** ARGS
 **         - None
 **
 ** RETURNS
 **         - None
 **
 ** DESCRIPTION :
 **   Management function for starting this component
 *****************************************************************************/
int
INGwSilRx::start() {
   LogINGwTrace(false, 0, "IN INGwSilRx::start()");

#ifdef BP_AIN_TEST
//sleep(30);

// Start a dummy thread to send INFO ANALYZE
   if(0 != pthread_create(&gDummyThr, NULL, testFunc, this)) {
      logger.logMsg(ERROR_FLAG, 0, 
                    "Thread to execute dummy test func created");
      return 1;
   }
#endif
   LogINGwTrace(false, 0, "OUT INGwSilRx::start()");
   return 0;
}

/******************************************************************************
 ** METHOD  : stop
 ** ARGS
 **         - None
 **
 ** RETURNS
 **         - None
 **
 ** DESCRIPTION :
 **   Management function for stopping this component
 *****************************************************************************/
int
INGwSilRx::stop() {
   LogINGwTrace(false, 0, "IN INGwSilRx::stop()");
   LogINGwTrace(false, 0, "OUT INGwSilRx::stop()");

   return 0;
}


/******************************************************************************
 ** METHOD  : pushMsg
 ** ARGS
 **         - INGwTcapWorkUnitMsg
 **
 ** RETURNS
 **         - int
 **
 ** DESCRIPTION :
 **   Method to push a message to Worker Queue
 *****************************************************************************/
int
INGwSilRx::pushMsg(INGwTcapWorkUnitMsg *apQMsg) {
   LogINGwTrace(false, 0, "IN pushMsg()");
   
   static bool lsbReplayFlg = true;
   INGwIfrMgrWorkUnit *workunit    = new INGwIfrMgrWorkUnit;
   
   workunit->meWorkType    = INGwIfrMgrWorkUnit::INGW_CALL_MSG;
   workunit->mpContextData = (void *)0;
   workunit->mpMsg         = (void *)apQMsg;
   workunit->mulMsgSize    = sizeof(INGwTcapWorkUnitMsg);
   //INCTBD we can create a new pushBndMsg  function in order to improve effcncy
   //the following null check will not be required then
   if(NULL != apQMsg->m_tcapMsg) {
   workunit->mpcCallId = new char[20];
   sprintf(workunit->mpcCallId,"%d",apQMsg->m_tcapMsg->dlgR.spdlgId);
   logger.logMsg(VERBOSE_FLAG, 0, "pushMsg():Callid[%s]",workunit->mpcCallId);
   }
// Now getting default Hash set by calling the gethash

#ifdef SIMULATOR
#else
   workunit->getHash();
#endif
   workunit->mpWorkerClbk  = mpINGwIfrMgrWorkerClbkIntf;

   // Now posting the Message to Worker Q

#ifdef BP_AIN_UT

   // STUBBING OUT for unit testting
   INGwSilDriver::instance().postMsg(workunit);
   
#else
   //do check here

   
      if(lsbReplayFlg && (lsbReplayFlg = gGetReplayFlg()) && apQMsg->m_tcapMsg && 
       gIsSubSeqMsg(apQMsg->m_tcapMsg->dlgR.spdlgId)) {
       logger.logINGwMsg(false,ALWAYS_FLAG,0,
                         "pushMsg():DlgId<%d> replayFlg<%d>",
                         apQMsg->m_tcapMsg->dlgR.spdlgId, lsbReplayFlg);
                          

       INGwIfrMgrThreadMgr::getInstance().
           postMsgForTakeOverCalls(workunit, apQMsg->m_tcapMsg->dlgR.spdlgId);
   }
   else {

     INGwIfrMgrThreadMgr::getInstance().postMsg(workunit);
   }

#endif   
   
   LogINGwTrace(false, 0, "OUT pushMsg()");
   return 0;
}


/******************************************************************************
 ** METHOD  : tuActvInit
 ** ARGS
 **     OUT - Ent
 **     OUT - inst
 **     OUT - region
 **     OUT - reason
 **
 ** RETURNS
 **         - S16
 **
 ** DESCRIPTION :
 **   TAPA init task cblk for the trillium stack to deliver TCAP messages
 *****************************************************************************/
S16
tuActvInit(Ent      ent,                 /* entity */
           Inst     inst,                /* instance */
           Region   region,              /* region */
           Reason   reason)             /* reason */
{
   // Call the INGwSilRx class
   return INGwSilRx::instance().tuActvInit(ent, inst, region, reason);
}

/******************************************************************************
 ** METHOD  : tuActvTsk
 ** ARGS
 **     OUT - Pst
 **     OUT - mBuf
 **
 ** RETURNS
 **         - S16
 ** DESCRIPTION :
 **   TAPA init task cblk for the trillium stack to deliver TCAP messages
 *****************************************************************************/

S16
tuActvTsk(Pst *pst, Buffer *mBuf)
{
   // Now call the function on INGwSilRx class
   return INGwSilRx::instance().tuActvTsk(pst, mBuf);
}

// Status Indication 
S16 TuLiStuStaInd(Pst           *pst,         /* post structure */
                  SuId           suId,        /* service user SAP id */
                  S16             status       /* Status */
                 )
{
   LogINGwTrace(false, 0, "IN TuLiStuStaInd()");
   INGwTcapWorkUnitMsg *qMsg = new INGwTcapWorkUnitMsg;  /* Queue element */
   memset(qMsg, 0, sizeof(INGwTcapWorkUnitMsg));

   // Stack reuses pst
   UNUSED(pst);
   UNUSED(status);
  
   qMsg->eventType = EVTSTUSTAIND;
	 qMsg->m_suId 	 = suId;

	 INGwTcapProvider::getInstance().getSpIdForSuId(qMsg->m_suId, qMsg->m_spId, qMsg->m_ssn);

   logger.logMsg(VERBOSE_FLAG, 0, "STA IND Received for suId[%d], spId[%d]",
	 qMsg->m_suId, qMsg->m_spId);

   INGwSilRx::instance().pushMsg(qMsg);

   LogINGwTrace(false, 0, "OUT TuLiStuStaInd()");
  
   return (ROK);
}

// Confirm success/failure for Bind Request
S16 TuLiStuBndCfm(Pst           *pst,         /* post structure */
                  SuId           suId,        /* service user SAP id */
                  U8             status       /* Status */
                 ) 
{
   LogINGwTrace(false, 0, "IN TuLiStuBndCfm()");
   INGwTcapWorkUnitMsg *qMsg = new INGwTcapWorkUnitMsg;     /* Queue element */
   LOCKMAP();
   memset(qMsg,0,sizeof(INGwTcapWorkUnitMsg));
   if(CM_BND_OK == status){
     sapStatusMap[suId] = true;
   }
   else{
     sapStatusMap[suId] = false;
   }

   //signal from here
   logger.logINGwMsg(false,ALWAYS_FLAG,0,
     "TuLiStuBndCfm suId<%d> status<%d>",suId,sapStatusMap[suId]);
   UNUSED(pst);
   UNUSED(status);

	 qMsg->m_suId 			 = suId;

	 INGwTcapProvider::getInstance().getSpIdForSuId(qMsg->m_suId, qMsg->m_spId, qMsg->m_ssn);

   logger.logMsg(ALWAYS_FLAG, 0,
     "+VER+ NOTE this has to be done via alarm handling ");

   logger.logMsg(ALWAYS_FLAG, 0,
     "+VER+ Bind Confirm For PC <%d> SSN <%d>",
							qMsg->m_pc, qMsg->m_ssn);

   char lpcTime[64];
   memset(lpcTime, 0, sizeof(lpcTime));
   lpcTime[0] = '1';
   g_getCurrentTime(lpcTime);
   printf("[+INC+] %s INGxSilRx.C:TuLiStuBndCfm EVTSTUBNDCFM Bind Confirm For PC <%d> SSN <%d>\n",
           lpcTime, qMsg->m_pc, qMsg->m_ssn); fflush(stdout);

   qMsg->eventType         = EVTSTUBNDCFM;
   qMsg->sapStatus         = status;
   qMsg->m_appInstId.appId = suId;

   logger.logMsg(VERBOSE_FLAG, 0, "BND CFM Received for SuId[%d] SpId[%d]", 
	 suId, qMsg->m_spId);

   // Now push this message into thread manager. 
   INGwSilRx::instance().pushMsg(qMsg);

   //sleep(1);
   LogINGwTrace(false, 0, "OUT TuLiStuBndCfm()");
   
   return (ROK);
}

// Subsystem and Point code confirmation
S16 TuLiStuSteCfm(Pst           *pst,         /* post structure */
                  SuId           suId,        /* service user SAP id */
                  CmSS7SteMgmt  *steMgmt
#ifdef STUV2
                 ,StMgmntParam   *mgmntParam
#endif
                 )     /* State structure */
{
   LogINGwTrace(false, 0, "IN TuLiStuSteCfm()");
   INGwTcapWorkUnitMsg *qMsg = new INGwTcapWorkUnitMsg;     /* Queue element */
   memset(qMsg, 0, sizeof(INGwTcapWorkUnitMsg));

   // Stack reuses pst
   UNUSED(pst);
   
   qMsg->eventType     = EVTSTUSTECFM;
	 qMsg->m_suId 			 = suId;

	 INGwTcapProvider::getInstance().getSpIdForSuId(qMsg->m_suId, 
														qMsg->m_spId, qMsg->m_ssn);

   cmCopy ((U8 *)steMgmt,
            (U8 *)&qMsg->ss7SteMgmt, (U32) sizeof(CmSS7SteMgmt));

   logger.logMsg(VERBOSE_FLAG, 0,
                 "AINF : [%d] : STE CFM Received",suId);

   // Now push this message to AIN Provider
   INGwSilRx::instance().pushMsg(qMsg);

   LogINGwTrace(false, 0, "OUT TuLiStuSteCfm()");
   
   return (ROK);
} 

// Subsystem and Point code Indication 
S16 TuLiStuSteInd(Pst           *pst,         /* post structure */
                  SuId           suId,        /* service user SAP id */
                  CmSS7SteMgmt  *steMgmt     /* State structure */
#ifdef STUV2
                 ,StMgmntParam   *mgmntParam
#endif
                 )     /* State structure */
{
   LogINGwTrace(false, 0, "IN TuLiStuSteInd()");
   INGwTcapWorkUnitMsg *qMsg = new INGwTcapWorkUnitMsg;     /* Queue element */
   memset(qMsg, 0, sizeof(INGwTcapWorkUnitMsg));
   // Stack reuses pst
   UNUSED(pst);
   
   qMsg->eventType     = EVTSTUSTEIND;
	 qMsg->m_suId 			 = suId;

	 INGwTcapProvider::getInstance().getSpIdForSuId(qMsg->m_suId, 
														qMsg->m_spId, qMsg->m_ssn);

   cmCopy ((U8 *)steMgmt,
            (U8 *)&qMsg->ss7SteMgmt, (U32) sizeof(CmSS7SteMgmt));

   logger.logMsg(VERBOSE_FLAG, 0,
                 "AINF [%d] : STE IND Received",suId);

   // Now push this message to AIN Provider
   INGwSilRx::instance().pushMsg(qMsg);

   LogINGwTrace(false, 0, "OUT TuLiStuSteInd()");
   
   return (ROK);
}   

// Notice Indication
S16 TuLiStuNotInd(Pst        *pst,            /* post structure */
                  SuId        suId,           /* service user SAP id */
                  StDlgId     suDlgId,        /* service user dialog id */
                  StDlgId     spDlgId,        /* service provider dialog id */
                  SpAddr      *dstAddr,          /* destination addr */
                  SpAddr      *srcAddr,            /* source address */
                  StDataParam    *dataParam,
                  RCause      cause)          /* SCCP Return Cause */
{
	 int lCurVal = 0;
	 INGwIfrSmStatMgr::instance().increment
								 (INGwTcapStatParam::INGW_INBOUND_NOT_INDX, lCurVal, 1);

   MsgLen msgLen;
   MsgLen len;
   LogINGwTrace(false, 0, "IN TuLiStuNotInd()");
   logger.logMsg(VERBOSE_FLAG, 0,
                 "Notice Ind Cause = %d", cause);

  //Yogesh: putting a check here to discard out of range dialogue Id's right 
  //from the entry point though they are observed with PAbort only(fixed)
   if((spDlgId < tcapLoDlgId) || (spDlgId >= tcapHiDlgId)) {
     logger.logINGwMsg(false,ERROR_FLAG,0,"Out TuLiStuNotInd - Out of range" 
                       "dialogue Id Rxd dlgId <%d> lo<%d> hi<%d>",spDlgId, 
                       tcapLoDlgId, tcapHiDlgId);
     return (ROK);
   }

   INGwTcapWorkUnitMsg *qMsg = new INGwTcapWorkUnitMsg;     /* Queue element */
   memset(qMsg, 0, sizeof(INGwTcapWorkUnitMsg));

   UNUSED(pst);

   qMsg->eventType     = EVTSTUNOTIND;
   qMsg->m_dlgId     = spDlgId;
	 qMsg->m_suId 			 = suId;

	 INGwTcapProvider::getInstance().getSpIdForSuId(qMsg->m_suId, 
														qMsg->m_spId, qMsg->m_ssn);

   qMsg->m_tcapMsg     = new TcapMessage();
   memset ((void *)qMsg->m_tcapMsg, 0, sizeof (TcapMessage));

   qMsg->m_tcapMsg->msg_type = EVTSTUDATIND; 
   qMsg->m_tcapMsg->msgTypeR = EVTSTUDATIND; 
   qMsg->m_tcapMsg->dlgR.sudlgId = suDlgId;
   qMsg->m_tcapMsg->dlgR.spdlgId = spDlgId;
   qMsg->m_tcapMsg->dlgR.cause= cause;
   qMsg->m_tcapMsg->dlgR.dlgType= INC_NOTICE;

   qMsg->m_tcapMsg->m_suId = qMsg->m_tcapMsg->appid.suId = qMsg->m_suId;
   qMsg->m_tcapMsg->m_spId = qMsg->m_tcapMsg->appid.spId = qMsg->m_spId;
   
   logger.logMsg(VERBOSE_FLAG, 0,
                 "AINF : [%d] : NOT IND Received",spDlgId);

   // Now push this message to AIN Provider
   INGwSilRx::instance().pushMsg(qMsg);
   LogINGwTrace(false, 0, "OUT TuLiStuNotInd()");

   return (ROK);
}

void copySpAddrToSccpAddr (SccpAddr *dst, SpAddr *src)
{
   dst->rtgInd = src->rtgInd;
   dst->sw     = src->sw;
   dst->ssn    = src->ssn;
   dst->pc     = src->pc;
   dst->pres   = src->pres;   
   dst->ssfPres= src->ssfPres;
   dst->niInd  = src->niInd;
   dst->pcInd  = src->pcInd;
   dst->ssnInd = src->ssnInd;
   //cmCopy ((U8 *)&src->gt, (U8 *)&dst->gttl, sizeof (INcGlbTi));
   cmCopy ((U8 *)&src->gt, (U8 *)&dst->gt, sizeof (src->gt));

   // Newly Added for NTT
   dst->status = src->status;
   cmCopy ((U8 *)&src->addrInfo, (U8 *)&dst->addrInfo, sizeof (src->addrInfo));
}


// Indicate the receipt of an unstructured TCAP message (Unidirectional
// message)
S16 TuLiStuUDatInd(Pst            *pst,
                   SuId            suId,
                   StDlgId         suDlgId,
                   StDlgId        spDlgId,
                   SpAddr         *dstAddr,
                   SpAddr         *srcAddr,
                   StQosSet       *qosSet,
                   Dpc             opc,
                   StDlgEv        *dlgEv,
                   StDataParam    *dataParam,
                   Buffer         *uiBuf)
{
   LogINGwTrace(false, 0, "IN TuLiStuUDatInd()");

#ifdef INGW_TRACE_CALL_THREAD
  logger.logINGwMsg(false,VERBOSE_FLAG,0,
                          "TuLiStuUDatInd +THREAD+ <%d>",spDlgId);
#endif

	 int lCurVal = 0;
	 INGwIfrSmStatMgr::instance().increment
								 (INGwTcapStatParam::INGW_INBOUND_UNI_INDX, lCurVal, 1);

  //Yogesh: putting a check here to discard out of range dialogue Id's right 
  //from the entry point though they are observed with PAbort only(fixed)
   if((spDlgId < tcapLoDlgId) || (spDlgId >= tcapHiDlgId)){
     logger.logINGwMsg(false,ERROR_FLAG,0,"Out TuLiStuUDatInd - Out of range" 
                       "dialogue Id Rxd dlgId <%d> lo<%d> hi<%d>",spDlgId, 
                       tcapLoDlgId, tcapHiDlgId);
     return (ROK);
   }

   MsgLen  msgLen;
   MsgLen  len;
   char lBuf[512];
   int lBufLen = 0;
   vector<U32> l_pcVector;

   INGwTcapWorkUnitMsg *qMsg = new INGwTcapWorkUnitMsg;     /* Queue element */
   memset(qMsg,0,sizeof(INGwTcapWorkUnitMsg));

   qMsg->eventType = EVTSTUUDATIND;
	 qMsg->m_suId    = suId;
   qMsg->m_dlgId   = spDlgId;  
   qMsg->m_ssn = 255;

	 INGwTcapProvider::getInstance().getSpIdForSuId(qMsg->m_suId, 
	 													qMsg->m_spId, qMsg->m_ssn);
   if(255 == qMsg->m_ssn){
     logger.logINGwMsg(false,ERROR_FLAG,0,"Out TuLiStuUDatInd cannot extract "
     "ssn for suId <%d>", suId);
     return (ROK);
   }else{
     logger.logINGwMsg(false,VERBOSE_FLAG,0,"TuLiStuUDatInd found SSN<%d>",
     qMsg->m_ssn);
   }

   if(!INGwTcapProvider::getInstance().
      getPcSsnForSuId(l_pcVector, qMsg->m_ssn, qMsg->m_suId)){
      logger.logINGwMsg(false,ERROR_FLAG,0,"Out TuLiStuUDatInd "
      "No PC SSN could be found found for suId <%d>",qMsg->m_suId);
      return (ROK);
   }
  
     
   UNUSED(pst);
   logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ TuLiStuUDatInd opc<%d>, dpc<%d>"
   "suid<%d> pc vector size<%d>", opc, l_pcVector[0],suId,l_pcVector.size());


   /* Allocate Tcap Message */
   qMsg->m_tcapMsg     = new TcapMessage();

   qMsg->m_tcapMsg->m_spId = qMsg->m_tcapMsg->appid.spId = qMsg->m_spId;
   qMsg->m_tcapMsg->m_suId = qMsg->m_tcapMsg->appid.suId = qMsg->m_suId;

   qMsg->m_tcapMsg->dlgR.spdlgId     = spDlgId;
   qMsg->m_tcapMsg->dlgR.sudlgId     = spDlgId;

   qMsg->m_tcapMsg->dlgR.opc = opc;
   qMsg->m_tcapMsg->dlgR.dpc = l_pcVector[0];

   logger.logMsg(TRACE_FLAG, 0,
    "[TuLiStuUDatInd]: [%d] : UDat IND Received ", spDlgId);

   qMsg->m_tcapMsg->msg_type   = EVTSTUUDATIND;
   qMsg->m_tcapMsg->msgTypeR   = INC_UNI;
   qMsg->m_tcapMsg->dlgR.pres  = dlgEv->pres;
   qMsg->m_tcapMsg->dlgR.dlgType  = INC_UNI;
   //qMsg->m_tcapMsg->dlgR.dlgType  = dlgEv->stDlgType;
   qMsg->m_tcapMsg->dlgR.resultPres = dlgEv->resPres;
   qMsg->m_tcapMsg->dlgR.result     = dlgEv->result;
   qMsg->m_tcapMsg->dlgR.reason     = dlgEv->resReason;

   //Yogesh:remove these new's MemLeak
   qMsg->m_tcapMsg->dlgR.srcAddr = new SccpAddr;
   qMsg->m_tcapMsg->dlgR.dstAddr = new SccpAddr;

   memset((void*)qMsg->m_tcapMsg->dlgR.srcAddr, 0, sizeof(SccpAddr));
   memset((void*)qMsg->m_tcapMsg->dlgR.dstAddr, 0, sizeof(SccpAddr));

   qMsg->m_tcapMsg->compPresR = TRUE;

   if(dlgEv->apConName.len){
     qMsg->m_tcapMsg->dlgR.acnType  = INC_ENC_TYP_OID;
     lBufLen +=sprintf(lBuf + lBufLen,
      "\n[TuLiStuUDatInd]------------------A C N------------------\n"
      "\n[TuLiStuUDatInd]ACN Length %d\n",dlgEv->apConName.len);   

       lBufLen +=sprintf(lBuf + lBufLen,
      "\n[TuLiStuUDatInd]------------------A C N------------------\n");

       memcpy((void*)(qMsg->m_tcapMsg->dlgR.objAcn.string),
              (void*)(dlgEv->apConName.string),dlgEv->apConName.len);

       qMsg->m_tcapMsg->dlgR.objAcn.len = dlgEv->apConName.len;
   }
   else{
    qMsg->m_tcapMsg->dlgR.objAcn.len = 0;
    lBufLen +=sprintf(lBuf + lBufLen,
      "\n[TuLiStuUDatInd]-------------A C N - N U L L-------------\n");
     qMsg->m_tcapMsg->dlgR.objAcn.len = 0;
   }

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
  
  logger.logINGwMsg(false,TRACE_FLAG,0,"pres field : %d",dlgEv->ansiDlgEv.pres);
  
  if(dlgEv->ansiDlgEv.pres.pres && dlgEv->ansiDlgEv.pres.val)
  {
    memcpy(&(qMsg->m_tcapMsg->dlgR.ansiDlgEv),
             &(dlgEv->ansiDlgEv),sizeof(stAnsiDlgEv));
  }

#endif

  if(NULL != srcAddr){
    qMsg->m_tcapMsg->dlgR.srcAddr = new SccpAddr;
    if(NULL == qMsg->m_tcapMsg->dlgR.srcAddr){
      logger.logINGwMsg(false,ERROR_FLAG,0, "TuLiStuUDatInd Cannot allocate "
        "memory");
    }
    memcpy(qMsg->m_tcapMsg->dlgR.srcAddr, srcAddr, sizeof(SccpAddr));
  }
  else{
    logger.logINGwMsg(false, ERROR_FLAG, 0, "TuLiStuUDatInd "
      "srcAddr is %x",srcAddr);
  }

  if(NULL != dstAddr){
    qMsg->m_tcapMsg->dlgR.dstAddr = new SccpAddr;
    if(NULL == qMsg->m_tcapMsg->dlgR.dstAddr){
      logger.logINGwMsg(false,ERROR_FLAG,0, "TuLiStuUDatInd Cannot allocate "
        "memory");
    }
    memcpy(qMsg->m_tcapMsg->dlgR.dstAddr, dstAddr, sizeof(SccpAddr));
  }
  else{
    logger.logINGwMsg(false, ERROR_FLAG, 0, "TuLiStuUDatInd  "
      "dstAddr is %x",dstAddr);
  }
   cmCopy((U8 *)qosSet, (U8 *)&qMsg->m_tcapMsg->dlgR.qosSet, 
					(U32) sizeof(TcapQosSet));

  qMsg->m_tcapMsg->dlgR.opc = opc;

   if(NULL != uiBuf){
   	SFndLenMsg (uiBuf, &msgLen);
   	qMsg->m_tcapMsg->dlgR.uInfo.len = msgLen;
   	qMsg->m_tcapMsg->dlgR.uInfo.string = new unsigned char [msgLen];
   	SCpyMsgFix (uiBuf, 0, msgLen, (Data *)qMsg->m_tcapMsg->dlgR.uInfo.string, 
					&len);

#ifdef SS_HISTOGRAM_SUPPORT
    SPutMsgNew(uiBuf, __LINE__, __FILE__);
#else
    SPutMsg(uiBuf);
#endif
   }
   logger.logMsg(VERBOSE_FLAG, 0, "TuLiStuUDatInd Received %d %s",spDlgId, lBuf);

   // Now push this message to AIN Provider
   INGwSilRx::instance().pushMsg(qMsg);

   LogINGwTrace(false, 0, "OUT TuLiStuUDatInd()");

   return (ROK);
}
static int testSecondIncomingMessage = 0;
// Indicate receipt of structured TCAP message
S16 TuLiStuDatInd(Pst            *pst,            /* post structure */
                  SuId            suId,
                  U8              msgType,
                  StDlgId         suDlgId,
                  StDlgId         spDlgId, 
                  SpAddr         *dstAddr,
                  SpAddr         *srcAddr,
                  Bool            compsPres,
                  StOctet        *pAbtCause,
                  StQosSet       *qosSet,
                  Dpc             opc,
                  /* Dpc Changes */
                  Dpc             dpc, 
                  StDlgEv        *dlgEv,
                  StDataParam    *dataParam,
                  Buffer         *uiBuf)
{
  LogINGwTrace(false, 0, "IN TuLiStuDatInd()");
  //HARDCODING dpc as stack is not sending dpc
/*********************************************************/
  dpc = 89;
/*********************************************************/
#ifdef INGW_TRACE_CALL_THREAD
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"TuLiStuDatInd +THREAD+ <%d>",spDlgId);
#endif 
  
  //Yogesh: putting a check here to discard out of range dialogue Id's right 
  //from the entry point though they are observed with PAbort only(fixed)
  
	int lCurVal = 0;
	INGwIfrSmStatMgr::instance().increment
								 (INGwTcapStatParam::INGW_INBOUND_DLG_INDX, lCurVal, 1);

	lCurVal=0;
  if(msgType == INC_BEGIN) {
		INGwIfrSmStatMgr::instance().increment
								 (INGwTcapStatParam::INGW_INBOUND_BGN_INDEX, lCurVal, 1);
    
    logger.logINGwMsg(false,VERBOSE_FLAG,0,"TuLiStuDatInd INC_BEGIN msgPrior<%d>",
    qosSet->msgPrior);
    //chkStkMemUtil();
	}
	else if(msgType == INC_CONTINUE) {
		INGwIfrSmStatMgr::instance().increment
								 (INGwTcapStatParam::INGW_INBOUND_CNT_INDEX, lCurVal, 1);
	}
	else if(msgType == INC_END) {
		INGwIfrSmStatMgr::instance().increment
								 (INGwTcapStatParam::INGW_INBOUND_END_INDEX, lCurVal, 1);
	}
	else if(msgType == INC_P_ABORT) {
		INGwIfrSmStatMgr::instance().increment
								 (INGwTcapStatParam::INGW_INBOUND_PABRT_INDEX, lCurVal, 1);
    //Yogesh: in order to prevent garbage spDlgId to cause array bounds r/w
    //this needs to be looked over when spDlgId will differ from suDlgId
    spDlgId = suDlgId;
	}

  //Yogesh: putting a check here to discard out of range dialogue Id's right 
  //from the entry point though they are observed with PAbort only(fixed)
  if((spDlgId < tcapLoDlgId) || (spDlgId >= tcapHiDlgId))
  {
    logger.logINGwMsg(false,ERROR_FLAG,0,"Out TuLiStuDatInd Out of range"
                      "dialogue Id Rxd dlgId <%d> lo<%d> hi<%d>",spDlgId, 
                      tcapLoDlgId, tcapHiDlgId);
    return (ROK);
  }

  MsgLen msgLen;
  MsgLen len;
  char lBuf[512];
  int lBufLen = 0;

  /* Queue element */
  INGwTcapWorkUnitMsg *qMsg = new INGwTcapWorkUnitMsg;
  memset(qMsg,0,sizeof(INGwTcapWorkUnitMsg));

  UNUSED(pst);
  qMsg->eventType = EVTSTUDATIND;
  qMsg->m_dlgId   = spDlgId;
	qMsg->m_suId 			 = suId;

	INGwTcapProvider::getInstance().getSpIdForSuId(qMsg->m_suId, 
														qMsg->m_spId, qMsg->m_ssn);

  lBufLen +=sprintf(lBuf + lBufLen,
    "\n[TuLiStuDatInd] Opc <%d> Dpc <%d> SSN <%d> spId <%d suId<%d>\n",
    opc, dpc,qMsg->m_ssn,qMsg->m_spId,qMsg->m_suId);

  qMsg->m_tcapMsg = new TcapMessage();

  qMsg->m_tcapMsg->msg_type         = EVTSTUDATIND;
  qMsg->m_tcapMsg->msgTypeR         = msgType;
  qMsg->m_tcapMsg->dlgR.pres        = dlgEv->pres;
  qMsg->m_tcapMsg->dlgR.dlgType     = msgType;
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)        
  if(dlgEv->ansiDlgEv.pres.pres && dlgEv->ansiDlgEv.pres.val)
  { 
    if(dlgEv->ansiDlgEv.acnType.pres)
    {
      qMsg->m_tcapMsg->dlgR.acnType     = dlgEv->ansiDlgEv.acnType.val;
    }
    else
    {
      logger.logINGwMsg(false,ERROR_FLAG,0,"acnType not present");
    }
   
    //not safe to access some memory region allocated by stack 
    //allocating buffer and copying userinfo
    if(dlgEv->ansiDlgEv.usrInfo.pres && (NULL != dlgEv->ansiDlgEv.usrInfo.val))
    {
      SFndLenMsg (dlgEv->ansiDlgEv.usrInfo.val, &msgLen);
      qMsg->m_tcapMsg->dlgR.dlgPortnUInfo.string = new unsigned char[msgLen];
      SCpyMsgFix (dlgEv->ansiDlgEv.usrInfo.val, 0, msgLen, 
                 (Data *)qMsg->m_tcapMsg->dlgR.dlgPortnUInfo.string, 
                  &len);

      qMsg->m_tcapMsg->dlgR.dlgPortnUInfo.len = msgLen;
    }
  }
#else
  qMsg->m_tcapMsg->dlgR.acnType     = INC_ENC_TYP_OID;
#endif

  qMsg->m_tcapMsg->m_spId = qMsg->m_tcapMsg->appid.spId = qMsg->m_spId;
  qMsg->m_tcapMsg->m_suId = qMsg->m_tcapMsg->appid.suId = qMsg->m_suId;

  if(dlgEv->apConName.len){
    lBufLen +=sprintf(lBuf + lBufLen,
      "\n[TuLiStuDatInd]------------------A C N------------------\n"
      "\n[TuLiStuDatInd]ACN Length %d\n",dlgEv->apConName.len);
    memcpy((void*)(qMsg->m_tcapMsg->dlgR.objAcn.string),
           (void*)(dlgEv->apConName.string),dlgEv->apConName.len);
    qMsg->m_tcapMsg->dlgR.objAcn.len = dlgEv->apConName.len;
    
    lBufLen +=sprintf(lBuf + lBufLen,
      "\n[TuLiStuDatInd]------------------A C N------------------\n");
  } else{ 
    qMsg->m_tcapMsg->dlgR.objAcn.len = 0;
    lBufLen +=sprintf(lBuf + lBufLen,
      "\n[TuLiStuDatInd]-------------A C N - N U L L-------------\n");
  }
  qMsg->m_tcapMsg->dlgR.resultPres  = dlgEv->resPres;
  qMsg->m_tcapMsg->dlgR.result      = dlgEv->result;
  qMsg->m_tcapMsg->dlgR.reason      = dlgEv->resReason;

  qMsg->m_tcapMsg->dlgR.spdlgId     = spDlgId;
  qMsg->m_tcapMsg->dlgR.sudlgId     = suDlgId;
  
  lBufLen +=sprintf(lBuf + lBufLen,
    "[TuLiStuDatInd]suDlgId = %d spDlgId = %d msgType %d\n",suDlgId, spDlgId,
     msgType);
  
  if((NULL != srcAddr) && 
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
    (STU_QRY_NO_PRM == msgType)|| (STU_QRY_PRM == msgType)
#else
      (INC_BEGIN == msgType)
#endif
   )
   {
    logger.logINGwMsg(false,VERBOSE_FLAG,0, "TuLiStuDatInd srcAddr is not "
      "+NULL+ msgType <%d>",msgType);

    qMsg->m_tcapMsg->dlgR.srcAddr = new (nothrow) SccpAddr;
    memset((void*)qMsg->m_tcapMsg->dlgR.srcAddr, 0, sizeof(SccpAddr));
    if(NULL == qMsg->m_tcapMsg->dlgR.srcAddr){
      logger.logINGwMsg(false,ERROR_FLAG,0, "TuLiStuDatInd Cannot allocate "
        "memory");
    }
    memcpy(qMsg->m_tcapMsg->dlgR.srcAddr, srcAddr, sizeof(SccpAddr));
    logger.logINGwMsg(false, TRACE_FLAG,0,
           "TuLiStuDatInd SccpClgAddr [PcPres<%d> Pc<0x%x>]",
           srcAddr->pcInd, (srcAddr->pcInd)?srcAddr->pc:0);
  }
  else{
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, "TuLiStuDatInd DlgType <%d> "
      "srcAddr is %x",msgType,srcAddr);
  }

  if((NULL != dstAddr) &&  
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
    (STU_QRY_NO_PRM == msgType)|| (STU_QRY_PRM == msgType)
#else
      (INC_BEGIN == msgType)
#endif 
    )
    {
    logger.logINGwMsg(false,VERBOSE_FLAG,0, "TuLiStuDatInd dstAddr is not "
    "+NULL+ msgType <%d>",msgType);

    qMsg->m_tcapMsg->dlgR.dstAddr = new (nothrow) SccpAddr;
    memset((void*)qMsg->m_tcapMsg->dlgR.dstAddr, 0, sizeof(SccpAddr));
    if(NULL == qMsg->m_tcapMsg->dlgR.dstAddr){
      logger.logINGwMsg(false,ERROR_FLAG,0, "TuLiStuDatInd Cannot allocate "
        "memory");
    }
    memcpy(qMsg->m_tcapMsg->dlgR.dstAddr, dstAddr, sizeof(SccpAddr));
    logger.logINGwMsg(false, TRACE_FLAG,0,
           "TuLiStuDatInd SccpCldAddr [PcPres<%d> Pc<0x%x>]",
           dstAddr->pcInd, (dstAddr->pcInd)?dstAddr->pc:0);
  }
  else{
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, "TuLiStuDatInd DlgType <%d> "
      "dstAddr is %x",msgType,dstAddr);
  }

  cmCopy((U8 *)qosSet,
         (U8 *)&qMsg->m_tcapMsg->dlgR.qosSet,
         (U32)  sizeof(TcapQosSet));

  logger.logINGwMsg(false,VERBOSE_FLAG,0,"TuLiStuDatInd msgPrior<%d>",
  qosSet->msgPrior);

  qMsg->m_tcapMsg->dlgR.opc = opc;
  qMsg->m_tcapMsg->dlgR.dpc = dpc;

  if(NULL != uiBuf){
    SFndLenMsg (uiBuf, &msgLen);
    qMsg->m_tcapMsg->dlgR.uInfo.len = msgLen;
    qMsg->m_tcapMsg->dlgR.uInfo.string = new unsigned char[msgLen];
    SCpyMsgFix (uiBuf, 0, msgLen, (Data *)qMsg->m_tcapMsg->dlgR.uInfo.string, &len);
#ifdef SS_HISTOGRAM_SUPPORT
    SPutMsgNew(uiBuf, __LINE__, __FILE__);
#else
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, "TuLiStuDatInd(): FreeMem");
    SPutMsg(uiBuf);
#endif
  }

  qMsg->m_tcapMsg->compPresR = compsPres;

  if(pAbtCause && pAbtCause->pres) {
    qMsg->m_tcapMsg->pAbtCauseR.pres = pAbtCause->pres;
    qMsg->m_tcapMsg->pAbtCauseR.octet = pAbtCause->octet;
    qMsg->m_tcapMsg->dlgR.cause = pAbtCause->octet;
    logger.logINGwMsg(false,ERROR_FLAG,0,
    "[TuLiStuDatInd]Dat IND pAbort received pAbtCause cause<%d>",
    pAbtCause->octet);

	 	int lCurVal = 0;
	 	INGwIfrSmStatMgr::instance().increment
								 (INGwTcapStatParam::INGW_INBOUND_ABRT_INDX, lCurVal, 1);
  }

  logger.logMsg(TRACE_FLAG, 0,
    "[TuLiStuDatInd]: [%d] : Dat IND Received %s", spDlgId,lBuf);

  INGwSilRx::instance().pushMsg(qMsg);
  LogINGwTrace(false, 0, "OUT TuLiStuDatInd()");
   
  return (ROK);
}

// Indicate receipt of component
S16 TuLiStuCmpInd(Pst        *pst,            /* post structure */
                  SuId        suId,
                  StDlgId     suDlgId,
                  StDlgId     spDlgId,
                  StComps    *compEv,
                  Dpc         opc,
                  Status      status,
                  Buffer     *cpBuf)
{
   LogINGwTrace(false, 0, "IN TuLiStuCmpInd()");

#ifdef INGW_TRACE_CALL_THREAD
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"TuLiStuCmpInd +THREAD+ <%d>",spDlgId);
#endif 

   static bool lsbReplayFlag = true; 
	 int lCurVal = 0;
	 INGwIfrSmStatMgr::instance().increment
								 (INGwTcapStatParam::INGW_INBOUND_CMP_INDX, lCurVal, 1);

  //Yogesh: putting a check here to discard out of range dialogue Id's right 
  //from the entry point though they are observed with PAbort only(fixed)
   if((spDlgId < tcapLoDlgId) || (spDlgId >= tcapHiDlgId)){
     logger.logINGwMsg(false,ERROR_FLAG,0,"Out TuLiStuCmpInd - Out of range" 
                       "dialogue Id Rxd dlgId <%d> lo<%d> hi<%d>",spDlgId, 
                       tcapLoDlgId, tcapHiDlgId);
     return (ROK);
   }


   MsgLen msgLen;
   MsgLen len;
   logger.logINGwMsg(false,TRACE_FLAG,0,"stCompType <%d>", compEv->stCompType);

   if(STU_REJECT == compEv->stCompType)
   {

     logger.logINGwMsg(false,ALWAYS_FLAG,0," STU_COMP_ERROR Type<%d>"
     " ProblemCode<%d> dlgId<%d> " ,

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96) 
     compEv->stAnsProbCode.type,
     compEv->stAnsProbCode.specifier,
#else 
     compEv->stProbCodeFlg, 
     compEv->stProbCode.string[0],
#endif
     suDlgId);

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96) 
     if(STU_ANSI_PRB_INV == compEv->stAnsProbCode.type 
        && STU_ANSI_PRB_DUP_ID == compEv->stAnsProbCode.specifier)
#else
     if(STU_PROB_INVOKE == compEv->stProbCodeFlg 
        && STU_DUP_INVOKE == compEv->stProbCode.string[0])
#endif

     {
        
         
       if(lsbReplayFlag && (lsbReplayFlag = gGetReplayFlg()) 
          && gIsSubSeqMsg(spDlgId)){ 
         logger.logINGwMsg(false,ALWAYS_FLAG,0," Local reject ignored for "
           "dialogueId <%d> - everything OK",suDlgId);
         LogINGwTrace(false, 0, "OUT TuLiStuCmpInd()");
         return (ROK);
       }
     }
   }

   INGwTcapWorkUnitMsg *qMsg = new INGwTcapWorkUnitMsg;     /* Queue element */
   memset(qMsg,0,sizeof(INGwTcapWorkUnitMsg));

   UNUSED(pst);
   
   qMsg->eventType     = EVTSTUCMPIND;
   qMsg->m_dlgId       = spDlgId;

	 qMsg->m_suId = suId;

	 INGwTcapProvider::getInstance().getSpIdForSuId(qMsg->m_suId, 
														qMsg->m_spId, qMsg->m_ssn);

   /*Allocate Tcap Message*/
   qMsg->m_tcapMsg     = new TcapMessage();
   qMsg->m_tcapMsg->comp     = new TcapComp;
   memset ((void *)qMsg->m_tcapMsg->comp, 0, sizeof (TcapComp));
   qMsg->m_tcapMsg->dlgR.spdlgId = spDlgId;
   qMsg->m_tcapMsg->dlgR.sudlgId = suDlgId;

   logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ TuLiStuCmpInd"
   " setting spdlgId as %d",qMsg->m_tcapMsg->dlgR.spdlgId);

   qMsg->m_tcapMsg->msg_type        = EVTSTUCMPIND;
   qMsg->m_tcapMsg->comp->compType  = compEv->stCompType;
   qMsg->m_tcapMsg->comp->opTag     = compEv->stOpCodeFlg;
   qMsg->m_tcapMsg->comp->errTag    = compEv->stErrorCodeFlg;

   qMsg->m_tcapMsg->m_suId = qMsg->m_tcapMsg->appid.suId = qMsg->m_suId;
   qMsg->m_tcapMsg->m_spId = qMsg->m_tcapMsg->appid.spId = qMsg->m_spId;

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)

   if(true == compEv->stCompId.pres)
   {
     if(true == compEv->stCompId.invPres) 
     {
       cmCopy ((U8 *)&(compEv->stCompId.invokeId), 
             (U8 *)&(qMsg->m_tcapMsg->comp->invIdAnsi),
             sizeof (INcOctet));
       qMsg->m_tcapMsg->comp->invIdAnsi.pres  = true;
       qMsg->m_tcapMsg->comp->invIdAnsi.octet = compEv->stCompId.invokeId; 
     }

     if(true == compEv->stCompId.corrPres)
     {
       qMsg->m_tcapMsg->comp->linkedId.pres  = true;
       qMsg->m_tcapMsg->comp->linkedId.octet = compEv->stCompId.corrId;
     }

     if(compEv->stAnsOpCode.pres) 
     {
       qMsg->m_tcapMsg->comp->opCode.string[0]  = compEv->stAnsOpCode.specifier;
       qMsg->m_tcapMsg->comp->opCode.len        = 0x01;
       qMsg->m_tcapMsg->comp->opClass = compEv->stAnsOpCode.type;
     }

     if(compEv->stAnsErrCode.pres)
     {
       qMsg->m_tcapMsg->comp->errCode.len = 0x01;
       qMsg->m_tcapMsg->comp->errCode.len = compEv->stAnsErrCode.octet;
     }
     
     if(true == compEv->stAnsProbCode.pres)
     {
       qMsg->m_tcapMsg->comp->probTag            = compEv->stAnsProbCode.type;
       qMsg->m_tcapMsg->comp->probCode.len       = 0x01;
       qMsg->m_tcapMsg->comp->probCode.string[0] = 
                                               compEv->stAnsProbCode.specifier;
     }

     if (NULL != cpBuf)
     {
       qMsg->m_tcapMsg->comp->paramTag = compEv->stParamFlg;
     }
   }

  
#else
   if (NULL != cpBuf)
   {
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
     qMsg->m_tcapMsg->comp->paramTag  = STU_SET; 
#else
     qMsg->m_tcapMsg->comp->paramTag  = STU_SEQUENCE; 
#endif
   }

   cmCopy ((U8 *)&(compEv->stInvokeId), 
           (U8 *)&(qMsg->m_tcapMsg->comp->invIdItu),
           sizeof (INcOctet));

   cmCopy ((U8 *)&(compEv->stLinkedId), 
           (U8 *)&(qMsg->m_tcapMsg->comp->linkedId), 
           sizeof (INcOctet));

   cmCopy ((U8 *)&compEv->stOpCode, 
           (U8*)&qMsg->m_tcapMsg->comp->opCode, 
           sizeof (INcStr));

   qMsg->m_tcapMsg->comp->opClass     = compEv->opClass;

   cmCopy ((U8 *)&compEv->stErrorCode, 
           (U8*)&qMsg->m_tcapMsg->comp->errCode, 
           sizeof (INcStr));

   qMsg->m_tcapMsg->comp->probTag   = compEv->stProbCodeFlg;

   cmCopy ((U8 *)&compEv->stProbCode, 
           (U8*)&qMsg->m_tcapMsg->comp->probCode, 
           sizeof (INcStr));

   qMsg->m_tcapMsg->comp->invokeTimer = compEv->stInvokeTimer;

#endif

 
   logger.logINGwMsg(false,TRACE_FLAG,0,"compEv->stLinkedId[pres,val]",
                     compEv->stLinkedId.pres, compEv->stLinkedId.octet);



   qMsg->m_tcapMsg->comp->lastComp    = compEv->stLastCmp;
   qMsg->m_tcapMsg->comp->cancelFlg   = compEv->cancelFlg;

#ifdef STUV4
   qMsg->m_tcapMsg->comp->update      = compEv->upd;
#endif

   if (NULL != cpBuf) 
   {
     SFndLenMsg (cpBuf, &msgLen);
     // temporary fix not taking care of long messags
     qMsg->m_tcapMsg->comp->param.string = new unsigned char[msgLen + 2];

     // temporary fix not taking care of long messags
     SCpyMsgFix (cpBuf, 0, msgLen, 
                (Data *)(qMsg->m_tcapMsg->comp->param.string + 2), 
                 &len);

     printf("\ncmp buf\n");

     qMsg->m_tcapMsg->comp->param.string[0] = 0xF2;
     qMsg->m_tcapMsg->comp->param.string[1] = len;

     for (int k = 0; k<msgLen + 2; k++)
     printf("%02X ",qMsg->m_tcapMsg->comp->param.string[k]);

     printf("\ncmp buf\n");
     fflush(stdout);

      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "TuLiStuCmpInd(): FreeMem");
#ifdef SS_HISTOGRAM_SUPPORT
      SPutMsgNew(cpBuf, __LINE__, __FILE__);
#else
      SPutMsg(cpBuf);
#endif
   }
   
   // temporary fix not taking care of long messags
   qMsg->m_tcapMsg->comp->param.len    = len + 2;
   
   logger.logMsg(VERBOSE_FLAG, 0,
                 "INGwSilRx: [%d] : Component IND  Received",spDlgId);

   // Now push this message to AIN Provider
   //free(compEv);
   //Yogesh remove the below statement
   INGwSilRx::instance().pushMsg(qMsg);
   LogINGwTrace(false, 0, "OUT TuLiStuCmpInd()");

   return (ROK);
}

// Confirm the receipt of new dialouge request
S16 TuLiStuCmpCfm(Pst        *pst,            /* post structure */
                  SuId        suId,           /* service user SAP id */
                  StDlgId     suDlgId,        /* service user dialog id */
                  StDlgId     spDlgId)        /* service provider dialog id */
{
   LogINGwTrace(false, 0, "IN TuLiStuCmpCfm()");

   INGwTcapWorkUnitMsg *qMsg = new INGwTcapWorkUnitMsg;     /* Queue element */
   memset(qMsg,0,sizeof(INGwTcapWorkUnitMsg));   
   UNUSED(pst);

   qMsg->eventType     = EVTSTUCMPCFM;
   qMsg->m_dlgId       = spDlgId;

	 qMsg->m_suId 			 = suId;

	 INGwTcapProvider::getInstance().getSpIdForSuId(qMsg->m_suId, 
														qMsg->m_spId, qMsg->m_ssn);
   
   logger.logMsg(VERBOSE_FLAG, 0,
                  "AINF : [%d] : Component cfm Received",spDlgId);

   // Now push this message to AIN Provider
   INGwSilRx::instance().pushMsg(qMsg);
   LogINGwTrace(false, 0, "OUT TuLiStuCmpCfm()");

   return (ROK);
}

// Receipt of GTT Indication
// Possible values of msgType defined in sp.h
// SP_GTPAUSE 0x0a
// SP_GTRESUME 0x0b
// SP_GTSTATUS_CONG 0x0c
// SP_GTSTATUS_NORM 0x0d

S16 TuLiStuGttInd    ARGS(( Pst      *pst,
                            SuId      suId,
                            Dpc       aDpc,
                            U8        msgType,
                            U8         *relGt,
                            U8         gtParamInd))
{
   LogINGwTrace(false, 0, "IN TuLiStuGttInd()");

   char buffer[100];
   int bufLen =0;

   sprintf(buffer+bufLen, "GTT Dpc:[%d], msgType:[%s], gtParamInd:[%d]", aDpc, 
	(msgType==0x0a)?"GT PAUSE":(msgType==0x0b)?"GT RESUME":(msgType==0x0c)?
	"GT CONGESTED":(msgType == 0x0d)?"GT NORMAL":"UNKNOWN", gtParamInd);

   INGwTcapMsgLogger::getInstance().dumpMsg(buffer, EVTSTUGTTIND); 
   LogINGwTrace(false, 0, "OUT TuLiStuGttInd()");

   return (ROK);
}


/******************************************************************************
 ** METHOD  : tuActvInit
 ** ARGS
 **     OUT - Ent
 **     OUT - inst
 **     OUT - region
 **     OUT - reason
 **
 ** RETURNS
 **         - S16
 **
 ** DESCRIPTION :
 **   TAPA init task cblk for the trillium stack to deliver INAP messages
 *****************************************************************************/
S16
INGwSilRx::tuActvInit(Ent      ent,                 /* entity */
                      Inst     inst,                /* instance */
                      Region   region,              /* region */
                      Reason   reason)             /* reason */
{
   LogINGwTrace(false, 0, "IN INGwSilRx::tuActvInit()");

   LogINGwTrace(false, 0, "OUT INGwSilRx::tuActvInit()");
   return (ROK);
}


/******************************************************************************
 ** METHOD  : tuActvTsk
 ** ARGS
 **     OUT - Pst
 **     OUT - mBuf
 **
 ** RETURNS
 **         - S16
 **IuLiIetOpenInd
 ** DESCRIPTION :
 **   TAPA init task cblk for the trillium stack to deliver INAP messages
 *****************************************************************************/

S16
INGwSilRx::tuActvTsk(Pst *pst, Buffer *mBuf)
{
   LogINGwTrace(false, 0, "IN INGwSilRx::tuActvTsk()");

   S16   ret;
   
   ret = ROK;
   
   switch(pst->event) 
   {
      case EVTSTUCMPIND:            /* Component Ind */
         ret = cmUnpkStuCmpInd(TuLiStuCmpInd, pst, mBuf);
         break;
      case EVTSTUCMPCFM:            /* Component cfm */
         ret = cmUnpkStuCmpCfm(TuLiStuCmpCfm, pst, mBuf);
         break;
      case EVTSTUDATIND:            /* data Ind */
         ret = cmUnpkStuDatInd(TuLiStuDatInd, pst, mBuf);
         break;
      case EVTSTUUDATIND:            /* unit data Ind */
         ret = cmUnpkStuUDatInd(TuLiStuUDatInd, pst, mBuf);
         break;
      case EVTSTUNOTIND:            /* notice Ind */
         ret = cmUnpkStuNotInd(TuLiStuNotInd, pst, mBuf);
         break;
      case EVTSTUSTEIND:            /* notice Ind */
         ret = cmUnpkStuSteInd(TuLiStuSteInd, pst, mBuf);
         break;
      case EVTSTUSTECFM:            /* notice Ind */
         ret = cmUnpkStuSteCfm(TuLiStuSteCfm, pst, mBuf);
         break;
      case EVTSTUSTAIND:            /* notice Ind */
         ret = cmUnpkStuStaInd(TuLiStuStaInd, pst, mBuf);
         break;
      case EVTSTUBNDCFM:            /* notice Ind */
         ret = cmUnpkStuBndCfm(TuLiStuBndCfm, pst, mBuf);
         break;
	  case EVTSTUGTTIND:           /* GTT Indication */
         ret = cmUnpkStuGttInd(TuLiStuGttInd, pst, mBuf);
         break;
      default:
         logger.logMsg(ERROR_FLAG, 0, "BPAIN : Unknow Message Received");
         UNUSED(mBuf);
         break;
   }
   
   SExitTsk();

   LogINGwTrace(false, 0, "OUT INGwSilRx::tuActvTsk()");
   return (ret);
}

