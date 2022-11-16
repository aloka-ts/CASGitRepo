#include <INGwInfraUtil/INGwIfrUtlLogger.h>
#include <INGwTcapProvider/INGwTcapMsgLogger.h>
#include "INGwTcapProvider/INGwTcapStatParam.h"
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>
#include <INGwInfraUtil/INGwIfrUtlGlbFunc.h>
#include <INGwStackManager/INGwSmCommon.h>


#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
  #include "envopt.h"        /* environment options */
  #include "envdep.h"        /* environment dependent */
  #include "envind.h"        /* environment independent */
  #include "gen.h"           /* general layer */
  #include "ssi.h"           /* system services */
  #include "cm5.h"
  #include "cm_ss7.h"
  #include "cm_hash.h"       /* common hash */
  #include "cm_err.h"      
  #include "stu.h"
  #include "cm_ftha.h"
  #include "lsh.h"
  #include "lmr.h"
  #include "lsg.h"
  #include "sg.h"
  
  #include "gen.x"           /* general layer */
  #include "ssi.x"           /* system services */
  #include "cm5.x"
  #include "cm_ss7.x"        /* Common */
  #include "cm_hash.x"  
  #include "stu.x"
  #include "cm_ftha.x"
  #include "lsh.x"
  #include "lmr.x"
  #include "lsg.x"
  #include "sg.x"
#ifndef __CCPU_CPLUSPLUS
}
#endif

BPLOG("INGwTcapProvider");

////////////////////////////////////////////////////////////////////////////////
//
// Copyright (c) 2001, Bay Packets Inc.
// All rights reserved.
//
// Filename   : INGwSilTx.C
// Description: This file contains the declaration of the INGwSilRx.  
//   Provide the Receiver side functionality of the AIN Trillim stack SIL
//
// NAME           DATE           REASON
// ----------------------------------------------------------------------------
// SU      31 Jul 2002    Initial Creation
//
///////////////////////////////////////////////////////////////////////////////

#ifndef SIMULATOR

//#include "BpOCMgr/BpOCMgr.h"

#endif

#include "INGwTcapProvider/INGwSilTx.h"
#include "INGwTcapProvider/INGwTcapProvider.h"
#include "Util/QueueMgr.h"

#include <sys/time.h>

using namespace std;

extern U16 selfProcId;
extern S16 sgLoDbGetMyState(void);

// Initialization of static variables

INGwSilTx* INGwSilTx::mpSelf = 0;

#ifdef PK_UNPK_DEBUG_FLAG
int g_printLen =0;
int g_msgMatchLen = 0;
int g_msgLen[50];
extern void printStoreToFile(int dumpType);
extern void initPkUnpkStore();
#endif

#ifdef INC_DLG_AUDIT
extern void initDlgInvBuffer();
#endif

INGwSilTx::INGwSilTx()
{
   LogINGwTrace(false, 0, "IN INGwSilTx()");

#ifdef PK_UNPK_DEBUG_FLAG
	 if(NULL != getenv("PRINT_LEN"))
			g_printLen = 1;

	 char* msgLenVal = NULL;
	 if(NULL != (msgLenVal = getenv("MSG_LEN")))
	 {
			vector<string> msgLen;
      g_tokenizeValue(msgLenVal, G_PIPE_DELIM, msgLen);

		  for(int i=0; i < msgLen.size(); ++i)
			{
				g_msgLen[g_msgMatchLen] = atoi(msgLen[i].c_str());	
				g_msgMatchLen++;
			}
	 }

     logger.logINGwMsg(false, ALWAYS_FLAG, 0,
            "INGwSilTx:INGwSilTx g_printLen[%d], g_msgMatchLen[%d]",
						g_printLen, g_msgMatchLen);

		for(int i=0; i < g_msgMatchLen; ++i)
		{
     logger.logINGwMsg(false, ALWAYS_FLAG, 0,
            "INGwSilTx:INGwSilTx g_msgLen[%d]", g_msgLen[i]);
		}
#endif
   
   LogINGwTrace(false, 0, "OUT INGwSilTx()");
}

INGwSilTx::~INGwSilTx() {
}

INGwSilTx& INGwSilTx::instance() {
   if(!mpSelf)
      mpSelf = new INGwSilTx;
   return *mpSelf;
} // end of instance


int
INGwSilTx::init() {
   LogINGwTrace(false, 0, "IN INGwSilTx::init()");

#ifdef INC_DLG_AUDIT
   initDlgInvBuffer();
#endif

   // Now Initializing the PST structure for INAP User
   //
   // IDEALLY information should be populated from the stack manager
   // after registering the Rx side since Stack manager knows about
   // the Stack Configuration and how this layer is coupled with
   // INAP layer
   //
   // ENH
   
   mSccpSeqCtrl = 1; //SCCP Sequenced Connectionless protocol
   char * lcSccpSeqCtrl = getenv("SCCP_SEQ_CNTRL");
   if (lcSccpSeqCtrl) {
     int liSccpSeqCtrl = atoi(lcSccpSeqCtrl);
     if ((liSccpSeqCtrl == 0) || (liSccpSeqCtrl == 1)) {
       mSccpSeqCtrl = (U8)liSccpSeqCtrl;
     }
     logger.logINGwMsg(false, ALWAYS_FLAG, 0,
            "init()::SCCP_SEQ_CNTRL defined to <%d>,mSccpSeqCtrl<%d>",
            liSccpSeqCtrl, mSccpSeqCtrl);
   }
   else {
     logger.logINGwMsg(false, ALWAYS_FLAG, 0,
            "init()::EnvVar SCCP_SEQ_CNTRL not defined. "
            "Setting to default value 1");
   }

   mSccpRetOpt = 0x08; //Return Message on Error
   char * lcSccpRetOpt = getenv("SCCP_RET_OPT");
   if (lcSccpRetOpt) {
     int liSccpRetOpt = atoi(lcSccpRetOpt);
     mSccpRetOpt = (U8)liSccpRetOpt;
     logger.logINGwMsg(false, ALWAYS_FLAG, 0,
            "init()::SCCP_RET_OPT defined to <0x%X> mSccpRetOpt<0x%X>",
            liSccpRetOpt, mSccpRetOpt);
   }
   else {
     logger.logINGwMsg(false, ALWAYS_FLAG, 0,
            "init()::EnvVar SCCP_RET_OPT not defined. "
            "Setting to default value 0x08");
   }

   
   mInvokeTimer = 0; //Unit is 100s of milli secs.
   char * lcInvokeTimer = getenv("INVOKE_TIMER");
   if (lcInvokeTimer) {
     int liInvokeTimer = atoi(lcInvokeTimer);
     mInvokeTimer = liInvokeTimer;
     logger.logINGwMsg(false, ALWAYS_FLAG, 0,
            "init()::INVOKE_TIMER(100s Milli. Secs) defined to mInvokeTimer<%d>",
            mInvokeTimer);
   }
   else {
     logger.logINGwMsg(false, ALWAYS_FLAG, 0,
            "init()::EnvVar INVOKE_TIMER not defined. "
            "Setting mInvokeTimer to default value %d(100s of milli secs.)", mInvokeTimer);
   }
   
   
   getMsgBuf((char *)"NTT_UABORT_USR_INFO", &(uInfo.string), &(uInfo.len));
   logger.logINGwMsg(false, ALWAYS_FLAG, 0,
          "init()::TC-U-ABORT USER-INFO[Len:%d][%s]", uInfo.len, uInfo.string);

   getMsgBuf((char *)"NTT_RC_PARAM_BUF", &(mRCParamBuf.string), &(mRCParamBuf.len));

   if(NULL != getenv("NTT_ABRT_CAUSE")){
     TcapMessage::lcAbrtCause = atoi(getenv("NTT_ABRT_CAUSE"));  
     logger.logINGwMsg(false,ALWAYS_FLAG,0,"NTT_ABRT_CAUSE <%d>",
                             TcapMessage::lcAbrtCause);
   }

   if(NULL != getenv("NTT_DLG_CLOSE_OPT")){
     TcapMessage::lcdlgCloseOpt = atoi(getenv("NTT_DLG_CLOSE_OPT"));
     logger.logINGwMsg(false,ALWAYS_FLAG,0,"NTT_DLG_CLOSE_OPT <%d>",
                             TcapMessage::lcdlgCloseOpt);
   }

   LogINGwTrace(false, 0, "IN INGwSilTx::init()");
   return 0;
}

void copySccpAddrToSpAddr (SpAddr *dst, SccpAddr *src)
{
   dst->pres    =  src->pres;
#ifdef CMSS7_SPHDROPT
   //dst->spHdrOpt = src->spHdrOpt;
     ;
#endif
   dst->rtgInd    = src->rtgInd;
   dst->sw        = src->sw;
   dst->ssnInd    = src->ssnInd;
   dst->ssn       = src->ssn;
   dst->pcInd     = src->pcInd;
   dst->pc        = src->pc;
   dst->ssfPres   = src->ssfPres;
   dst->ssf       = src->ssf;
   dst->niInd     = src->niInd;
   //cmCopy ((U8 *)&src->gttl, (U8 *)&dst->gt, sizeof (INcGlbTi));
   cmCopy ((U8 *)&src->gt, (U8 *)&dst->gt, sizeof (src->gt));
}


void INGwSilTx::releaseWorkUnit(INGwTcapWorkUnitMsg *apMsg)
{
/* To be uncommented INCTBD */
   if(apMsg->compPres)
   {
      vector<TcapComp *>* lCompVector = apMsg->m_tcapMsg->getCompVector();
      for(int i=0;i< lCompVector->size();i++)
      {
        TcapComp *comp = (*lCompVector)[i];
        
        if(comp->param.len && comp->param.string)
        {
          delete [] comp->param.string;
          comp->param.len = 0;
          comp->param.string = 0; 
        }
        delete comp;
        comp = NULL;
     }
   }
   if((apMsg->m_tcapMsg) && (apMsg->m_tcapMsg->dlgR.dstAddr)){
     delete apMsg->m_tcapMsg->dlgR.dstAddr;
     apMsg->m_tcapMsg->dlgR.dstAddr = 0;
   }
   
   if((apMsg->m_tcapMsg) && (apMsg->m_tcapMsg->dlgR.srcAddr)){ 
     delete apMsg->m_tcapMsg->dlgR.srcAddr;
     apMsg->m_tcapMsg->dlgR.srcAddr = 0;
   }

   if((apMsg->m_tcapMsg) && (0 != apMsg->m_tcapMsg->dlgR.uInfo.len)) {
     delete apMsg->m_tcapMsg->dlgR.uInfo.string;
   }
}


int INGwSilTx::sendTcapReq(INGwTcapWorkUnitMsg *apMsg)
{
	LogINGwTrace(false, 0, "IN sendTcapReq()");

  //int rtVal = sgLoDbGetMyState();
  //logger.logINGwMsg(false,TRACE_FLAG,0,"SG ROLE %d",rtVal);
#ifdef INGW_TRACE_CALL_THREAD
  if(NULL != apMsg->m_tcapMsg)
  logger.logINGwMsg(false,VERBOSE_FLAG,0,"INGwSilTx sendTcapReq +THREAD+ <%d>",
                    apMsg->m_tcapMsg->dlgR.sudlgId);
#endif

	StDlgEv 			dlgEv;
	StQosSet    	qosSet;
	StDataParam 	dp;
	S16 					cCnt;
	SpAddr    		srcAddr;
	SpAddr    		dstAddr;
	Buffer 			*dlgBuf=NULL;
  Pst          lPst;

  bool isATMsg = false;

  lPst.selector  = TU_SEL_LC;

  lPst.region    = BP_AIN_SM_REGION;
  lPst.pool      = SP_POOL;
  lPst.prior     = PRIOR0;
  lPst.route     = RTE_PROTO;
  lPst.dstProcId = 0; 
  lPst.dstEnt    = ENTST;
  lPst.dstInst   = 0;
  lPst.srcProcId = SFndProcId();
  lPst.srcEnt    = ENTTU;
  lPst.srcInst   = 0;
  

	memset((char*)&dlgEv, 0, sizeof(StDlgEv));
	memset((char*)&qosSet, 0, sizeof(StQosSet));
	memset((char*)&dp, 0, sizeof(StDataParam));

	qosSet.retOpt = 0x08; // REC_ROE = 0x08 return on error 
  //static loutgngCnt = 0;
	if(apMsg->compPres) 
  {
    //loutgngCnt++;
		vector<TcapComp *>* lCompVector = apMsg->m_tcapMsg->getCompVector();

		int size= lCompVector->size();
   	int cmpCnt = size;

		vector<TcapComp *>::iterator iter;

   	for(iter=(*lCompVector).begin();iter != (*lCompVector).end();iter++)
   	{
      stComps compEv;
      TcapComp *comp;
      cmpCnt--;
      memset((char*)&compEv, 0, sizeof(stComps));
      Buffer *cpBuf=NULL;
      compEv.stCompType = (*iter)->compType;

      //common 
      compEv.stOpCodeFlg= (*iter)->opTag;

      compEv.stErrorCodeFlg = (*iter)->errTag;

      if((*iter)->opCode.string[0] == 0x37 && 0 != (*iter)->opCode.len) 
      { 
        // ActivityTest opcode
        isATMsg = true;
      }

#ifdef SS_HISTOGRAM_SUPPORT
      if (SGetMsg (BP_AIN_SM_REGION, DFLT_POOL, &cpBuf, __FILE__, __LINE__) != ROK)
#else
      if (SGetMsg (BP_AIN_SM_REGION, DFLT_POOL, &cpBuf) != ROK)
#endif
      {
          logger.logMsg(ERROR_FLAG, 0, "sendTcapReq():Failed to Allocate Message Buffer");
          printf("[+INC+] %s:%d sendTcapReq():Failed to Allocate Message Buffer\n", __FILE__, __LINE__);
          releaseWorkUnit(apMsg);
          return -1;
      }


      if(0 != (*iter)->param.len && (*iter)->param.string){
      	SAddPstMsgMult((Data *)((*iter)->param.string),
        (*iter)->param.len, cpBuf);
      }


#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
      if((*iter)->invIdAnsi.pres == true)
      {
          compEv.stCompId.pres     = true;
          compEv.stCompId.invPres  = true;
          compEv.stCompId.invokeId = (*iter)->invIdAnsi.octet;
          /* uncomment to simuate L-Reject
          U8 lcInvokeId;
          char* lpcLrejFlg = getenv("SIMULATE_L_REJECT");
          if(lpcLrejFlg){
            lcInvokeId = atoi(lpcLrejFlg);
            logger.logINGwMsg(false,ALWAYS_FLAG,0,"SIMULATE_L_REJECT SET %d",
              lcInvokeId);
            compEv.stInvokeId.octet = lcInvokeId;
          }
          */
      }

     if((*iter)->linkedId.pres == true)
     {
       compEv.stCompId.pres    = true;
       compEv.stCompId.corrPres = true;  
       compEv.stCompId.corrId   = (*iter)->linkedId.octet;
     }

     if(0 != (*iter)->opCode.len)
     {
       compEv.stAnsOpCode.pres      = true;
       compEv.stAnsOpCode.specifier = (*iter)->opCode.string[0];
       compEv.stAnsOpCode.type      = (*iter)->opClass;
     }

     if(0 != (*iter)->errCode.len)
     {
       compEv.stAnsErrCode.pres  = true;
       compEv.stAnsErrCode.octet = (*iter)->errCode.string[0];
     }

     if(0 != (*iter)->probCode.len)
     {
       compEv.stAnsProbCode.pres      = true;
       compEv.stAnsProbCode.type      = (*iter)->probTag;
       compEv.stAnsProbCode.specifier = (*iter)->probCode.string[0];
     }

     if(0 != (*iter)->param.len && (*iter)->param.string)
     {
       compEv.stParamFlg = (*iter)->paramTag;
     }

     logger.logINGwMsg(false, TRACE_FLAG,0,"ANSI InvokeId<0x%02x> ",
            ((true == compEv.stCompId.pres) && (true == compEv.stCompId.invPres))?
             compEv.stCompId.invokeId:255); 
#else
     if((*iter)->invIdItu.pres == true)
     {
         compEv.stInvokeId.octet = (*iter)->invIdItu.octet;
         /* uncomment to simuate L-Reject
         U8 lcInvokeId;
         char* lpcLrejFlg = getenv("SIMULATE_L_REJECT");
         if(lpcLrejFlg){
           lcInvokeId = atoi(lpcLrejFlg);
           logger.logINGwMsg(false,ALWAYS_FLAG,0,"SIMULATE_L_REJECT SET %d",
             lcInvokeId);
           compEv.stInvokeId.octet = lcInvokeId;
         }
         */
         compEv.stInvokeId.pres = (*iter)->invIdItu.pres;
     }

     if((*iter)->linkedId.pres == true)
     {
       compEv.stLinkedId.octet = (*iter)->linkedId.octet;
       compEv.stLinkedId.pres = (*iter)->linkedId.pres;
     }

     compEv.stInvokeTimer = (*iter)->invokeTimer;
     if (compEv.stInvokeTimer == 0) {
       compEv.stInvokeTimer = mInvokeTimer; // Unit is 100s of milli secs.
     }

     logger.logINGwMsg(false, TRACE_FLAG,0,"InvokeId<0x%02x> InvokeTimer<%d>",

            ((*iter)->invIdItu.pres)?compEv.stInvokeId.octet:255,
            compEv.stInvokeTimer); 

     compEv.opClass = (*iter)->opClass;

     if(0 != (compEv.stOpCode.len = (*iter)->opCode.len))
     {
		  	memcpy(compEv.stOpCode.string,(*iter)->opCode.string,
		 																compEv.stOpCode.len);
     }

     if(0 != (compEv.stErrorCode.len = (*iter)->errCode.len))
     {
     	 memcpy( compEv.stErrorCode.string,(*iter)->errCode.string, 
		 																		compEv.stErrorCode.len);
       logger.logINGwMsg(false,TRACE_FLAG,0,"INGwSilTx ERROR TAG:len %d" 
         "code%d", (*iter)->errCode.len, compEv.stErrorCode.string[0]);
     }

     compEv.stProbCodeFlg = (*iter)->probTag;

     if(0 != (compEv.stProbCode.len = (*iter)->probCode.len)){
     	memcpy( compEv.stProbCode.string,(*iter)->probCode.string, 
 																				compEv.stProbCode.len);
     }
#endif




      compEv.cancelFlg = (*iter)->cancelFlg;

      if(0 == cmpCnt)
          compEv.stLastCmp = TRUE;
      else
          compEv.stLastCmp = FALSE;


      if (apMsg->m_tcapMsg->dlgR.dlgType == INC_UNI) {
        apMsg->m_tcapMsg->dlgR.spdlgId = 0;
      }
      
      logger.logMsg(VERBOSE_FLAG, 0,
          "CMP REQ to stack with dlgIds - [SuDlgId - %d,SpDlgId - %d] "
          "dlgType<%d>",
          apMsg->m_tcapMsg->dlgR.sudlgId, apMsg->m_tcapMsg->dlgR.spdlgId,
          apMsg->m_tcapMsg->dlgR.dlgType);
      
      TuLiStuCmpReq(&lPst, apMsg->m_spId, apMsg->m_tcapMsg->dlgR.sudlgId, 
										apMsg->m_tcapMsg->dlgR.spdlgId, &compEv, cpBuf);

			int lCurVal = 0;
			INGwIfrSmStatMgr::instance().increment
											(INGwTcapStatParam::INGW_OUTBOUND_CMP_INDX, lCurVal, 1);

      //yogesh ft +rem+
      /*uncomment to verify replication point of components 
      static lCmpCnt = 0; lCmpCnt++;
      char* lpcOutgngCnt = getenv("INGW_OUTGNG_CNT");
      char* lpcOutgngCmp = getenv("INGW_OUTGNG_CMP");
      if(INGwTcapProvider::getInstance().getFpMask() == 15) {
        logger.logINGwMsg(false,ALWAYS_FLAG,0," Failure-Point-Mask 15 %d %d %d %d",
                          loutgngCnt, atoi(lpcOutgngCnt),lCmpCnt,atoi(lpcOutgngCmp));
        if(NULL != lpcOutgngCnt) {
          if(loutgngCnt == atoi(lpcOutgngCnt)){
          logger.logINGwMsg(false,ALWAYS_FLAG,0," Failure-Point-Mask 15 %d %d",
                            loutgngCnt, atoi(lpcOutgngCnt));

            if(lCmpCnt == atoi(lpcOutgngCmp)){
            logger.logINGwMsg(false,ALWAYS_FLAG,0," Failure-Point-Mask 15 %d %d",
                              lCmpCnt,atoi(lpcOutgngCmp));

              logger.logINGwMsg(false,ALWAYS_FLAG,0," exitting on cmp %d %d",
                atoi(lpcOutgngCnt), atoi(lpcOutgngCmp));
              sleep(2);
              exit(1);
            }
          }
        }
      }
      */
      //yogesh ft
			INGwTcapMsgLogger::getInstance().dumpComponent
										(apMsg->m_tcapMsg->dlgR.spdlgId, EVTSTUCMPIND, *iter, true);
    }
  }
  

	if (EVTSTUDATREQ == apMsg->eventType || EVTSTUUDATREQ== apMsg->eventType)
	{
    dlgEv.pres 					= apMsg->m_tcapMsg->dlgR.pres?TRUE:FALSE;
    logger.logINGwMsg(false,VERBOSE_FLAG,0,"DATREQ dlgEv.pres %d",dlgEv.pres);
    dlgEv.stDlgType 		= apMsg->m_tcapMsg->dlgR.dlgType;

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)   
   memcpy(&dlgEv.ansiDlgEv, &(apMsg->m_tcapMsg->dlgR.ansiDlgEv), 
          sizeof(stAnsiDlgEv));
#else
    dlgEv.apConName.len = apMsg->m_tcapMsg->dlgR.objAcn.len;
    
    if(dlgEv.apConName.len) {
       
      	memcpy((void*)dlgEv.apConName.string,
             (void*)apMsg->m_tcapMsg->dlgR.objAcn.string,
             apMsg->m_tcapMsg->dlgR.objAcn.len);
   } 
#endif
    
     
    dlgEv.resPres 	= apMsg->m_tcapMsg->dlgR.resultPres;
    dlgEv.result 		= apMsg->m_tcapMsg->dlgR.result;
    dlgEv.resReason = apMsg->m_tcapMsg->dlgR.reason;

    
    if(apMsg->m_tcapMsg->dlgR.resultPres){
      dlgEv.resPres = TRUE;
      dlgEv.resReason = apMsg->m_tcapMsg->dlgR.reason; 
      logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ abort reason:<%d>",
      (dlgEv.resReason)); 
    }

    if (isATMsg) {
      qosSet.msgPrior = 1;
    }
    else {
      qosSet.msgPrior = apMsg->m_tcapMsg->qosR.msgPrior;
    }
    logger.logMsg(VERBOSE_FLAG, 0, "sendTcapReq() msgPrior<%d>",
                  qosSet.msgPrior);

    //qosSet.retOpt = apMsg->m_tcapMsg->qosR.retOpt;
    //qosSet.seqCtl = apMsg->m_tcapMsg->qosR.seqCtl;
    qosSet.retOpt = mSccpRetOpt; //REC_ROE = 0x08 return message on error 
    qosSet.seqCtl = mSccpSeqCtrl;

#ifdef SS7_REUSE_SLS
    qosSet.lnkSel = apMsg->m_tcapMsg->qosR.lnkSel;
#endif

    if(apMsg->m_tcapMsg->dlgR.dlgType == INC_U_ABORT) {
#ifdef SS_HISTOGRAM_SUPPORT
      if (SGetMsg (BP_AIN_SM_REGION, DFLT_POOL, &dlgBuf, __FILE__, __LINE__) != ROK)
#else
      if (SGetMsg (BP_AIN_SM_REGION, DFLT_POOL, &dlgBuf) != ROK)
#endif
      {
        logger.logMsg(ERROR_FLAG, 0, "sendTcapReq():Failed to allocate Message Buffer");
        printf("[+INC+] %s:%d sendTcapReq():Failed to Allocate Message Buffer\n", __FILE__, __LINE__);
        releaseWorkUnit(apMsg);
        return -1;
      }
    
      if(0 != apMsg->m_tcapMsg->dlgR.uInfo.len) {
        //SCpyFixMsg((Data *)(apMsg->m_tcapMsg->dlgR.uInfo.string),dlgBuf,0,
        //           apMsg->m_tcapMsg->dlgR.uInfo.len,&cCnt);

        SAddPstMsgMult((Data *)(apMsg->m_tcapMsg->dlgR.uInfo.string),
                        apMsg->m_tcapMsg->dlgR.uInfo.len, dlgBuf);

        logger.logINGwMsg(false, VERBOSE_FLAG,0,"uInfo Len :<%d>",
                          apMsg->m_tcapMsg->dlgR.uInfo.len);
      }
    }

    if(NULL != apMsg->m_tcapMsg->dlgR.srcAddr){
      memcpy(&srcAddr,apMsg->m_tcapMsg->dlgR.srcAddr,sizeof(SccpAddr));
    }
    
    if(NULL != apMsg->m_tcapMsg->dlgR.dstAddr){
      memcpy(&dstAddr,apMsg->m_tcapMsg->dlgR.dstAddr,sizeof(SccpAddr));
    }

    StDataParam  dataParam; 
    memset ((void*) &dataParam, 0, sizeof (StDataParam));
    if( EVTSTUDATREQ== apMsg->eventType)
    {

        if (apMsg->m_tcapMsg->dlgR.sudlgId != apMsg->m_tcapMsg->dlgR.spdlgId)
        {
          logger.logMsg(VERBOSE_FLAG, 0, 
                        "sendTcapReq():SuDlgId<%d> and SpDlgId<%d> different.",
                        apMsg->m_tcapMsg->dlgR.sudlgId, 
                        apMsg->m_tcapMsg->dlgR.spdlgId);
        }

        logger.logMsg(VERBOSE_FLAG, 0,
				"Dat req to stack with dlgids - [%d,%d] SuId[%d] SpId[%d] "
        "resPres<%d> resReason<%d>", apMsg->m_tcapMsg->dlgR.sudlgId, 
        apMsg->m_tcapMsg->dlgR.spdlgId, apMsg->m_suId,
        apMsg->m_spId, dlgEv.resPres,dlgEv.resReason);

				int lCurVal = 0;
				if(apMsg->m_tcapMsg->dlgR.dlgType == INC_U_ABORT)
				{
					INGwIfrSmStatMgr::instance().increment
							(INGwTcapStatParam::INGW_OUTBOUND_ABRT_INDX, lCurVal, 1);
				}
				else
				{
					INGwIfrSmStatMgr::instance().increment
							(INGwTcapStatParam::INGW_OUTBOUND_DLG_INDX, lCurVal, 1);

					if(apMsg->m_tcapMsg->dlgR.dlgType ==  INC_CONTINUE) {
						INGwIfrSmStatMgr::instance().increment
							(INGwTcapStatParam::INGW_OUTBOUND_CNT_INDX, lCurVal, 1);
					}
					else if(apMsg->m_tcapMsg->dlgR.dlgType == INC_END) {
						INGwIfrSmStatMgr::instance().increment
							(INGwTcapStatParam::INGW_OUTBOUND_END_INDEX, lCurVal, 1);
					}
					else if(apMsg->m_tcapMsg->dlgR.dlgType == INC_BEGIN) {
						INGwIfrSmStatMgr::instance().increment
							(INGwTcapStatParam::INGW_OUTBOUND_BGN_INDEX, lCurVal, 1);
					}
				}
    
        TuLiStuDatReq(&lPst, apMsg->m_spId,
                      apMsg->m_tcapMsg->dlgR.dlgType, 
                      apMsg->m_tcapMsg->dlgR.sudlgId,
                      apMsg->m_tcapMsg->dlgR.spdlgId,
                      &dstAddr, &srcAddr,
                      apMsg->m_tcapMsg->dlgR.endFlag, 
                      &qosSet, &dlgEv, &dataParam, dlgBuf);
    }
		else
    {
			logger.logMsg(VERBOSE_FLAG, 0,
		 	"UDat req to stack with dlgids - [%d,%d] SuId[%d] SpId[%d]",
			 apMsg->m_tcapMsg->dlgR.sudlgId, 0,//apMsg->m_tcapMsg->dlgR.spdlgId,
			 apMsg->m_suId, apMsg->m_spId);

				int lCurVal = 0;
				INGwIfrSmStatMgr::instance().increment
											(INGwTcapStatParam::INGW_OUTBOUND_UNI_INDX, lCurVal, 1);


        TuLiStuUDatReq(&lPst, apMsg->m_spId,
                       apMsg->m_tcapMsg->dlgR.sudlgId, 
                       //apMsg->m_tcapMsg->dlgR.spdlgId,
                       0,
                       &dstAddr, &srcAddr,
                       &qosSet, &dlgEv, &dataParam, dlgBuf);

		}

		INGwTcapMsgLogger::getInstance().dumpMsg(apMsg->m_tcapMsg->dlgR.sudlgId, 
																apMsg->eventType, apMsg->m_tcapMsg, true);
    }
    else if ( EVTSTUSTEREQ == apMsg->eventType )
    {
        logger.logMsg(VERBOSE_FLAG, 0,
                "Invoking TuLiStuSteReq to stack");

        TuLiStuSteReq(&lPst, apMsg->m_spId, (cmSS7SteMgmt*)&apMsg->ss7SteMgmt);
    }
    else if ( EVTSTUBNDREQ== apMsg->eventType )
    {
        logger.logMsg(ALWAYS_FLAG, 0,
				"Invoking TuLiStuBndReq EVTSTUBNDREQ req to stack, suId[%d], spId[%d]",
					apMsg->m_suId, apMsg->m_spId);

        char lpcTime[64];
        memset(lpcTime, 0, sizeof(lpcTime));
        lpcTime[0] = '1';
        g_getCurrentTime(lpcTime);
        printf("[+INC+] %s INGxSilTx.C:Invoking TuLiStuBndReq EVTSTUBNDREQ req suId[%d] spId[%d]\n",
                lpcTime, apMsg->m_suId, apMsg->m_spId); fflush(stdout);

				// Always do UnBind before Binding self 
        // Yogesh: Not required as per Manu 
				//TuLiStuUbndReq(&lPst, apMsg->m_spId, 0);
        TuLiStuBndReq(&lPst, apMsg->m_suId,  apMsg->m_spId, 
									apMsg->ss7SteMgmt.mgmt.steReq.aSsn);
    }
    else if ( EVTSTUUBNDREQ== apMsg->eventType )
    {
        logger.logMsg(ALWAYS_FLAG, 0,
				       "Invoking TuLiStuUbndReq EVTSTUUBNDREQ req to stack, suId[%d], spId[%d]",
               apMsg->m_suId, apMsg->m_spId);
        char lpcTime[64];
        memset(lpcTime, 0, sizeof(lpcTime));
        lpcTime[0] = '1';
        g_getCurrentTime(lpcTime);
        printf("[+INC+] %s INGxSilTx.C:Invoking TuLiStuUbndReq EVTSTUUBNDREQ req suId[%d] spId[%d]\n",
                lpcTime, apMsg->m_suId, apMsg->m_spId); fflush(stdout);

        TuLiStuUbndReq(&lPst,apMsg->m_spId,0);
    }

    LogINGwTrace(false, 0, "OUT sendTcapReq()");
    releaseWorkUnit(apMsg);
    return 0;
}

#ifdef PK_UNPK_DEBUG_FLAG
void INGwSilTx::printMBufStore(int nmbDlgsPerFile)
{
  printStoreToFile(nmbDlgsPerFile);
}
#endif

string INGwSilTx::getSelfSgRole()
{
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "IN getSelfSgRole()");
  S16 retVal = sgLoDbGetMyState();
  std::string sgRoleStr =
              (retVal == 0x01)?"SG_STA_OOS":(retVal == 0x02)?"SG_STA_ACTIVE":
              (retVal == 0x04)?"SG_STA_STANDBY":(retVal == 0x08)?"SG_STA_TRANS":
              (retVal == 0x10)?"SG_STA_ABORTING":"INVALID";
  logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                    "OUT getSelfSgRole(). Role<%s>", sgRoleStr.c_str());
  return sgRoleStr;
}



