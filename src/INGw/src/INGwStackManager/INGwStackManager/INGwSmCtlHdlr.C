//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

/************************************************************************
   Name:     INAP Stack Manager Control Handler - Impl
 
   Type:     C file
 
   Desc:     Control Request Handler Impl

   File:     INGwSmCtlHdlr.C

   Sid:      INGwSmCtlHdlr.C 0  -  03/27/03 

   Prg:      gs

************************************************************************/

#define DBGMASK_IPH_DATA  0x00000200     /* iph data flow log */
#define DBGMASK_IPH_CNTRL 0x00000400     /* iph control flow log */
#define DBGMASK_IPH_PERF  0x00000800     /* iph performance test log */
#define DBGMASK_IPH_SNMP  0x00001000     /* iph snmp log */

#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
  #include "envopt.h"
#ifndef __CCPU_CPLUSPLUS
}
#endif

#include "INGwStackManager/INGwSmCtlHdlr.h"
#include "INGwStackManager/INGwSmRepository.h"
#include "INGwTcapProvider/INGwTcapProvider.h"
#include <INGwStackManager/INGwSmBlkConfig.h>

extern U16 selfProcId;

using namespace std;

#ifdef __cplusplus
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
#endif

#ifndef NA
#define NA 0
#endif

#ifndef STNA
#define STNA 0
#endif




#ifdef __cplusplus
#ifndef __CCPU_CPLUSPLUS
}
#endif
#endif

extern S16 cfgSdDbg ARGS((int dbgMask, int action, 
                           ProcId dstProcId, Selector selsm2sd, U32 transId)); 

void fillHdr(Header *hdr,U32 miTransId,U8 entId, U8 instId,U8 msgType,S16 elmntId,S16 elInst1Id)
{
 memset(hdr, 0, sizeof(Header));
 hdr->transId = miTransId;
 hdr->msgType = msgType;
 hdr->entId.inst = instId;
 hdr->entId.ent = entId;
 hdr->elmId.elmnt = elmntId;
 hdr->elmId.elmntInst1 = elInst1Id;
 hdr->response.selector = BP_AIN_SM_COUPLING;
 hdr->response.mem.region = BP_AIN_SM_REGION;
 hdr->response.mem.pool = BP_AIN_SM_POOL;
 hdr->response.prior = BP_AIN_SM_PRIOR;
 hdr->response.route = BP_AIN_SM_ROUTE;

}

int INGwSmCtlHdlr::delGtAddrMap (StackReqResp *stackReq)
{
        SpMngmt &cntrl = l.sp;
        cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

        LngAddrs  tmpBuf;

        logger.logMsg (TRACE_FLAG, 0,"Entering delGtAddrMap");

        INGwSmRepository *lpRep = mrDist.getSmRepository ();

        if (lpRep == 0)
        {
                logger.logMsg (ERROR_FLAG, 0,
                                "TID <%d> : Repository pointer is NULL", miTransId);
                return BP_AIN_SM_FAIL;
        }

        fillHdr(&(l.sp.hdr), miTransId, ENTSP, 0, TCNTRL, STDELADRMAP, 0);
        cntrl.t.cntrl.action = STDELADRMAP;

        /* update data recieved from the user */
        SpAddrMapCfg *spAddrMap = &cntrl.t.cntrl.cfg.spAddrMap;
#ifdef GTT_PER_NWK
        spAddrMap->nwId = stackReq->req.u.delAddrMapCfg.nwkId;
#endif
        spAddrMap->actn.type = stackReq->req.u.delAddrMapCfg.actn.type;
        spAddrMap->actn.param.range.startDigit = stackReq->req.u.delAddrMapCfg.actn.startDigit;
        spAddrMap->actn.param.range.endDigit = stackReq->req.u.delAddrMapCfg.actn.endDigit;
        spAddrMap->sw = stackReq->req.u.delAddrMapCfg.sw;
        spAddrMap->replGt = stackReq->req.u.delAddrMapCfg.replGt;
        /* This is the only parameter that can have default value.
         * so insted of reading the template, we are hardcoding it*/
        spAddrMap->noCplng = 0;
        spAddrMap->gt.format = stackReq->req.u.delAddrMapCfg.format;

        switch(spAddrMap->gt.format)
        {
                case 1:
                        spAddrMap->gt.gt.f1.oddEven = stackReq->req.u.delAddrMapCfg.oddEven;
                        spAddrMap->gt.gt.f1.natAddr = stackReq->req.u.delAddrMapCfg.natAddr;
                        break;
                case 2:
                        spAddrMap->gt.gt.f2.tType = stackReq->req.u.delAddrMapCfg.tType;
                        break;
                case 3:
                        spAddrMap->gt.gt.f3.tType = stackReq->req.u.delAddrMapCfg.tType;
                        spAddrMap->gt.gt.f3.numPlan = stackReq->req.u.delAddrMapCfg.numPlan;
                        spAddrMap->gt.gt.f3.encSch = stackReq->req.u.delAddrMapCfg.encSch;
                        break;
                case 4:
                        spAddrMap->gt.gt.f4.tType = stackReq->req.u.delAddrMapCfg.tType;
                        spAddrMap->gt.gt.f4.numPlan = stackReq->req.u.delAddrMapCfg.numPlan;
                        spAddrMap->gt.gt.f4.encSch = stackReq->req.u.delAddrMapCfg.encSch;
                        spAddrMap->gt.gt.f4.natAddr = stackReq->req.u.delAddrMapCfg.natAddr;
                        break;
        }

        tmpBuf.length = stackReq->req.u.delAddrMapCfg.gtDigLen;
        memcpy ((void*) tmpBuf.strg, stackReq->req.u.delAddrMapCfg.gtDigits, 
          stackReq->req.u.delAddrMapCfg.gtDigLen);
        lpRep->gttHexAddrToBcd (&tmpBuf, &spAddrMap->gt.addr);

        spAddrMap->mode = stackReq->req.u.delAddrMapCfg.mode;
#ifdef GTT_PER_NWK
        spAddrMap->outNwId = stackReq->req.u.delAddrMapCfg.outNwId;
#endif
        spAddrMap->numEntity = stackReq->req.u.delAddrMapCfg.numEntity;
        for (int i=0; i < spAddrMap->numEntity; i++)
        {
                spAddrMap->outAddr[i].spHdrOpt = stackReq->req.u.delAddrMapCfg.outAddr[i].spHdrOpt;
                spAddrMap->outAddr[i].pres = TRUE;
                spAddrMap->outAddr[i].sw = stackReq->req.u.delAddrMapCfg.outAddr[i].swtch;
                spAddrMap->outAddr[i].ssfPres = TRUE;
                spAddrMap->outAddr[i].ssf = stackReq->req.u.delAddrMapCfg.outAddr[i].ssf;
                spAddrMap->outAddr[i].niInd = stackReq->req.u.delAddrMapCfg.outAddr[i].niInd;
                spAddrMap->outAddr[i].rtgInd = stackReq->req.u.delAddrMapCfg.outAddr[i].rtgInd;
                spAddrMap->outAddr[i].ssnInd = stackReq->req.u.delAddrMapCfg.outAddr[i].ssnInd;
                spAddrMap->outAddr[i].pcInd = stackReq->req.u.delAddrMapCfg.outAddr[i].pcInd;
                spAddrMap->outAddr[i].ssn = stackReq->req.u.delAddrMapCfg.outAddr[i].ssn;
                spAddrMap->outAddr[i].pc = stackReq->req.u.delAddrMapCfg.outAddr[i].pc;
                switch(spAddrMap->gt.format)
                {
                        case 1:
                                spAddrMap->outAddr[i].gt.gt.f1.oddEven = stackReq->req.u.delAddrMapCfg.outAddr[i].oddEven;
                                spAddrMap->outAddr[i].gt.gt.f1.natAddr = stackReq->req.u.delAddrMapCfg.outAddr[i].natAddr;
                                break;
                        case 2:
                                spAddrMap->outAddr[i].gt.gt.f2.tType = stackReq->req.u.delAddrMapCfg.outAddr[i].tType;
                                break;
                        case 3:
                                spAddrMap->outAddr[i].gt.gt.f3.tType = stackReq->req.u.delAddrMapCfg.outAddr[i].tType;
                                spAddrMap->outAddr[i].gt.gt.f3.numPlan = stackReq->req.u.delAddrMapCfg.outAddr[i].numPlan;
                                spAddrMap->outAddr[i].gt.gt.f3.encSch = stackReq->req.u.delAddrMapCfg.outAddr[i].encSch;
                                break;
                        case 4:
                                spAddrMap->outAddr[i].gt.gt.f4.tType = stackReq->req.u.delAddrMapCfg.outAddr[i].tType;
                                spAddrMap->outAddr[i].gt.gt.f4.numPlan = stackReq->req.u.delAddrMapCfg.outAddr[i].numPlan;
                                spAddrMap->outAddr[i].gt.gt.f4.encSch = stackReq->req.u.delAddrMapCfg.outAddr[i].encSch;
                                spAddrMap->outAddr[i].gt.gt.f4.natAddr = stackReq->req.u.delAddrMapCfg.outAddr[i].natAddr;
                                break;
                }
                tmpBuf.length = stackReq->req.u.delAddrMapCfg.outAddr[i].gtDigLen;

                memcpy ((void*) tmpBuf.strg, stackReq->req.u.delAddrMapCfg.outAddr[i].gtDigits, 
                  stackReq->req.u.delAddrMapCfg.outAddr[i].gtDigLen); 
                lpRep->gttHexAddrToBcd (&tmpBuf, &spAddrMap->outAddr[i].gt.addr);
        }

        Pst *smPst = lpRep->getPst (BP_AIN_SM_SCC_LAYER);
        smPst->event = EVTLSPCNTRLREQ;
        smPst->dstProcId = SFndProcId();
        smPst->srcProcId = SFndProcId();

        SmMiLspCntrlReq (smPst, &l.sp);

        //stackReq->resp.procId = stackReq->procId;
        stackReq->resp.procId = SFndProcId();
        if(stackReq->txnType != ROLLBACK_TXN) 
          stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        logger.logMsg (TRACE_FLAG, 0," Leaving delGtAddrMap");
  return BP_AIN_SM_OK;
}




int INGwSmCtlHdlr::delGtRule (StackReqResp *stackReq)
{
   SpMngmt &cntrl = l.sp;
   cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));
   logger.logMsg (TRACE_FLAG, 0,"Entering delGtRule");

   INGwSmRepository *lpRep = mrDist.getSmRepository ();

   if (lpRep == 0)
   {
      logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Repository pointer is NULL", miTransId);
      return BP_AIN_SM_FAIL;
   }

   cntrl.t.cntrl.action = STDELASSO;

   fillHdr(&(l.sp.hdr), miTransId, ENTSP, 0, TCNTRL, STDELASSO, 0);

   Pst *smPst = lpRep->getPst (BP_AIN_SM_SCC_LAYER);
   smPst->event = EVTLSPCNTRLREQ;
   //smPst->dstProcId = stackReq->procId;
   smPst->dstProcId = SFndProcId();
   smPst->srcProcId = SFndProcId();


   /* update data recieved from the user */
   SpAssoCfg *spAsso = &cntrl.t.cntrl.cfg.spAsso;
#ifdef GTT_PER_NWK
   spAsso->nwId = stackReq->req.u.delGtRule.nwId;
#endif
   spAsso->rule.sw = stackReq->req.u.delGtRule.sw;
   spAsso->rule.formatPres = stackReq->req.u.delGtRule.formatPres;
   spAsso->rule.format = stackReq->req.u.delGtRule.format;
   switch(spAsso->rule.format)
   {
      case 1:
         spAsso->rule.gt.f1.oddEvenPres = stackReq->req.u.delGtRule.oddEvenPres;
         spAsso->rule.gt.f1.oddEven = stackReq->req.u.delGtRule.oddEven;
         spAsso->rule.gt.f1.natAddrPres = stackReq->req.u.delGtRule.natAddrPres;
         spAsso->rule.gt.f1.natAddr = stackReq->req.u.delGtRule.natAddr;
         break;
      case 2:
         spAsso->rule.gt.f2.tTypePres = stackReq->req.u.delGtRule.tTypePres;
         spAsso->rule.gt.f2.tType = stackReq->req.u.delGtRule.tType;
         break;
      case 3:
         spAsso->rule.gt.f3.tTypePres = stackReq->req.u.delGtRule.tTypePres;
         spAsso->rule.gt.f3.tType = stackReq->req.u.delGtRule.tType;
         spAsso->rule.gt.f3.numPlanPres = stackReq->req.u.delGtRule.numPlanPres;
         spAsso->rule.gt.f3.numPlan = stackReq->req.u.delGtRule.numPlan;
         spAsso->rule.gt.f3.encSchPres = stackReq->req.u.delGtRule.encSchPres;
         spAsso->rule.gt.f3.encSch = stackReq->req.u.delGtRule.encSch;
         break;
      case 4:
         spAsso->rule.gt.f4.tTypePres = stackReq->req.u.delGtRule.tTypePres;
         spAsso->rule.gt.f4.tType = stackReq->req.u.delGtRule.tType;
         spAsso->rule.gt.f4.numPlanPres = stackReq->req.u.delGtRule.numPlanPres;
         spAsso->rule.gt.f4.numPlan = stackReq->req.u.delGtRule.numPlan;
         spAsso->rule.gt.f4.encSchPres = stackReq->req.u.delGtRule.encSchPres;
         spAsso->rule.gt.f4.encSch = stackReq->req.u.delGtRule.encSch;
         spAsso->rule.gt.f4.natAddrPres = stackReq->req.u.delGtRule.natAddrPres;
         spAsso->rule.gt.f4.natAddr = stackReq->req.u.delGtRule.natAddr;
         break;
   }
   spAsso->nmbActns = stackReq->req.u.delGtRule.nmbActns;
   for (int i=0; i < spAsso->nmbActns; i++)
   {
      spAsso->actn[i].type = stackReq->req.u.delGtRule.actn[i].type;
      spAsso->actn[i].param.range.startDigit = stackReq->req.u.delGtRule.actn[i].startDigit;
      spAsso->actn[i].param.range.endDigit = stackReq->req.u.delGtRule.actn[i].endDigit;
   }
   /* This is the only parameter that can have default value.
    * so insted of reading the template, we are hardcoding it*/
   spAsso->fSetType = 1;

   SmMiLspCntrlReq (smPst, &l.sp);
   //stackReq->resp.procId = stackReq->procId;
   stackReq->resp.procId = SFndProcId();
   if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
   stackReq->txnStatus = INPROGRESS;

   mrDist.updateRspStruct(miTransId,stackReq);

   logger.logMsg (TRACE_FLAG, 0," Leaving delGtRule");
   return BP_AIN_SM_OK;
}




int INGwSmCtlHdlr::disableUserPart(StackReqResp *stackReq)
{
	logger.logMsg (TRACE_FLAG, 0,"Entering disableUserPart");

	SpMngmt &cntrl = l.sp;
	cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

	fillHdr(&(l.sh.hdr), miTransId, ENTSP, 0, TCNTRL, STNSAP, stackReq->req.u.disableUserPart.sccpLsapId );

	cntrl.t.cntrl.action = AUBND_DIS;
	cntrl.t.cntrl.subAction = STNA;


	Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
	lmPst->event = EVTLSPCNTRLREQ;
  lmPst->dstProcId = stackReq->procId;
  lmPst->srcProcId = SFndProcId();

	SmMiLspCntrlReq(lmPst, &cntrl);
	/* update the response structure */

  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

	
	logger.logMsg (TRACE_FLAG, 0,"Leaving disableUserPart");
	return BP_AIN_SM_OK;
}


int INGwSmCtlHdlr::enableUserPart(StackReqResp *stackReq)
{
	logger.logMsg (TRACE_FLAG, 0,"Entering enableUserPart");
	SpMngmt &cntrl = l.sp;
	cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

	fillHdr(&(l.sh.hdr), miTransId, ENTSP, 0, TCNTRL, STNSAP, stackReq->req.u.enableUserPart.sccpLsapId );

	cntrl.t.cntrl.action = ABND_ENA;
	cntrl.t.cntrl.subAction = STNA;


	Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
	lmPst->event = EVTLSPCNTRLREQ;
  lmPst->dstProcId = stackReq->procId;
  lmPst->srcProcId = SFndProcId();

	printf("[+INC+] sccpLsapId:%d, dstProcId:%d, srcProcId:%d\n",
	stackReq->req.u.enableUserPart.sccpLsapId,
	lmPst->dstProcId, lmPst->srcProcId);


	SmMiLspCntrlReq(lmPst, &cntrl);
	/* update the response structure */

  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

	logger.logMsg (TRACE_FLAG, 0,"Leaving enableUserPart");
	return BP_AIN_SM_OK;
}



/*int INGwSmCtlHdlr::cliEnableLink(LinkEnable *lnk)
{
	logger.logMsg (TRACE_FLAG, 0,"Entering cliEnableLink");
	enableMtp3Lnk(lnk);
	enableMtp2Lnk(lnk);
	logger.logMsg (TRACE_FLAG, 0,"Leaving cliEnableLink");
return BP_AIN_SM_OK;
}*/

int INGwSmCtlHdlr::enableMtp2Lnk(StackReqResp *stackReq)
{
	logger.logMsg (TRACE_FLAG, 0,"Entering EnableMtp2Link");
	SdMngmt &cntrl = l.sd;
	cmMemset((U8 *)&cntrl, 0, sizeof(SdMngmt));
        
	{
        fillHdr(&(l.sd.hdr), miTransId, ENTSD, 0, TCNTRL, STDLSAP, stackReq->req.u.lnkEnable.mtp2UsapId) ;

		cntrl.t.cntrl.action = AENA;
		cntrl.t.cntrl.subAction = SAELMNT;

		Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP2_LAYER);
		lmPst->event = EVTLSDCNTRLREQ;
    //lmPst->dstProcId = stackReq->procId;
    lmPst->dstProcId = SFndProcId();
    lmPst->srcProcId = SFndProcId();

		smMiLsdCntrlReq(lmPst, &cntrl);

    /* update the response structure */
    stackReq->resp.procId = stackReq->procId;
    if(stackReq->txnType != ROLLBACK_TXN) 
      stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

	}
	logger.logMsg (TRACE_FLAG, 0,"Leaving  EnableMtp2Link");
return BP_AIN_SM_OK;
}

int INGwSmCtlHdlr::enableMtp3Lnk(StackReqResp *stackReq)
{
	logger.logMsg (TRACE_FLAG, 0,"Entering EnableMtp3Link");
	SnMngmt &cntrl = l.sn;
	cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));

	logger.logMsg (TRACE_FLAG, 0,"In EnableMtp3Link: ElmntInst1 <%d>",stackReq->req.u.lnkEnable.mtp3LsapId);

	fillHdr(&(l.sn.hdr), miTransId, ENTSN, 0, TCNTRL, STDLSAP, (stackReq->req.u.lnkEnable.mtp3LsapId) );
	//fillHdr(&(l.sn.hdr), miTransId, ENTSN, 0, TCNTRL, STDLSAP, 0 );

	//cntrl.hdr.elmId.elmntInst2 = 0xff;

	cntrl.t.cntrl.action = ABND_ENA;
	cntrl.t.cntrl.subAction = SAELMNT;
	cntrl.t.cntrl.ctlType.srtReqFlg = FALSE; // required for other variant. Causing issue in 
						 // bringing up link for NTT variant

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
	lmPst->event = EVTLSNCNTRLREQ;
  //lmPst->dstProcId = stackReq->procId;
  lmPst->dstProcId = SFndProcId();
  lmPst->srcProcId = SFndProcId();

	SmMiLsnCntrlReq(lmPst, &cntrl);
	/* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

	logger.logMsg (TRACE_FLAG, 0,"leaving  EnableMtp3Link");

return BP_AIN_SM_OK;
}


/* Function to disable link */

/*int INGwSmCtlHdlr::cliDisableLink(LinkDisable *lnk)
{
	logger.logMsg (TRACE_FLAG, 0,"Entering cliDisableLink");
	disableMtp3Link(k);
	disableMtp2Link(lnk);
	logger.logMsg (TRACE_FLAG, 0,"Leaving  cliDisableLink");
return BP_AIN_SM_OK;
}*/

/* Function to disable link at MTP2 */
int INGwSmCtlHdlr::disableMtp2Link(StackReqResp *stackReq)
{
	logger.logMsg (TRACE_FLAG, 0,"Entering DisableMtp2Link");
	SdMngmt &cntrl = l.sd;
	cmMemset((U8 *)&cntrl, 0, sizeof(SdMngmt));

	{

        fillHdr(&(l.sd.hdr), miTransId, ENTSD, 0, TCNTRL, STDLSAP, stackReq->req.u.lnkDisable.mtp2UsapId );

		cntrl.t.cntrl.action = AUBND_DIS;
		cntrl.t.cntrl.subAction = SAELMNT;

		Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP2_LAYER);
		lmPst->event = EVTLSDCNTRLREQ;
    //lmPst->dstProcId = stackReq->req.u.lnkDisable.procId;
    lmPst->dstProcId = SFndProcId();
    lmPst->srcProcId = SFndProcId();
 
		smMiLsdCntrlReq(lmPst, &cntrl);

		/*here we need to update the response structure */
    stackReq->resp.procId = stackReq->procId;
    if(stackReq->txnType != ROLLBACK_TXN) 
      stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);
	}
	logger.logMsg (TRACE_FLAG, 0,"Leaving  DisableMtp2Link");

return BP_AIN_SM_OK;
}

/* Function to disable link at MTP3 */
int INGwSmCtlHdlr::disableMtp3Link(StackReqResp *stackReq)
{
	logger.logMsg (TRACE_FLAG, 0,"Entering DisableMtp3Link");
	SnMngmt &cntrl = l.sn;
	cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));

	fillHdr(&(l.sn.hdr), miTransId, ENTSN, 0, TCNTRL, STDLSAP, stackReq->req.u.lnkDisable.mtp3LsapId );
	//cntrl.hdr.elmId.elmntInst2 = 0xff;

	cntrl.t.cntrl.action = AUBND_DIS;
	cntrl.t.cntrl.subAction = SAELMNT;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
	lmPst->event = EVTLSNCNTRLREQ;
  //lmPst->dstProcId = stackReq->procId;
  lmPst->dstProcId = SFndProcId();
    lmPst->srcProcId = SFndProcId();

	smMiLsnCntrlReq(lmPst, &cntrl);
	/* update the response structure */
  stackReq->resp.procId = stackReq->procId;
    if(stackReq->txnType != ROLLBACK_TXN) 
      stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);
	logger.logMsg (TRACE_FLAG, 0,"Leaving  DisableMtp3Link");

return BP_AIN_SM_OK;
}


/* Function to delete Mtp2 Link */
int INGwSmCtlHdlr::delMtp2Link(StackReqResp *stackReq)
{
	logger.logMsg (TRACE_FLAG, 0,"Entering delMtp2Link");

    SdMngmt &cntrl = l.sd;
	cmMemset((U8 *)&cntrl, 0, sizeof(SdMngmt));

	fillHdr(&(l.sd.hdr), miTransId, ENTSD, 0, TCNTRL, STDLSAP,stackReq->req.u.delLnk.mtp2UsapId);

	/* Before deleting the link disable it */
		cntrl.t.cntrl.action = ADELLNK;
		cntrl.t.cntrl.subAction = SAELMNT;

		Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP2_LAYER);
		lmPst->event = EVTLSDCNTRLREQ;
    lmPst->srcProcId = SFndProcId();
    //lmPst->dstProcId = stackReq->req.u.delLnk.mtp2UsapId;
    lmPst->dstProcId = stackReq->procId;

	  logger.logMsg (TRACE_FLAG, 0,"delmtp2link, mtp2sap:%d, dstProcId:%d, srcProc:%d",stackReq->req.u.delLnk.mtp2UsapId,lmPst->dstProcId,lmPst->srcProcId);
		
    mrDist.updateRspStruct(miTransId,stackReq);

		/* Now we can delete the link */
		smMiLsdCntrlReq(lmPst, &cntrl);

    stackReq->resp.procId = stackReq->procId;
    if(stackReq->txnType != ROLLBACK_TXN) 
     stackReq->txnType = NORMAL_TXN;
     stackReq->txnStatus = INPROGRESS;

		/*here we need to update the response structure */
	logger.logMsg (TRACE_FLAG, 0,"Leaving  delMtp2Link");
	return BP_AIN_SM_OK;
}

/* Function to delete Mtp3 Link */
int INGwSmCtlHdlr::delMtp3Link(StackReqResp *stackReq)
{
	logger.logMsg (TRACE_FLAG, 0,"Entering delMtp3Link");
  SnMngmt &cntrl = l.sn;
	cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));

	fillHdr(&(l.sn.hdr), miTransId, ENTSN, 0, TCNTRL, STDLSAP, stackReq->req.u.delLnk.mtp3LsapId );
	//cntrl.hdr.elmId.elmntInst2 = 0xff;

	cntrl.t.cntrl.action = ADELLNK;
	cntrl.t.cntrl.subAction = SAELMNT;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
	lmPst->event = EVTLSNCNTRLREQ;
  lmPst->dstProcId = stackReq->procId;
  lmPst->srcProcId = SFndProcId();

	smMiLsnCntrlReq(lmPst, &cntrl);
	/* update the response structure */

  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);
	
	logger.logMsg (TRACE_FLAG, 0,"Leaving delMtp3Link");
return BP_AIN_SM_OK;
}	


/* Function to delete LDF Mtp3 Link */
int INGwSmCtlHdlr::delLdfMtp3Link(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,"Entering delLdfMtp3Link");
  LdnMngmt &cntrl = l.dn;
  cmMemset((U8 *)&cntrl, 0, sizeof(LdnMngmt));

  //fillHdr(&(l.dn.hdr), miTransId, ENTDN, 0, TCNTRL, STDNDLSAP, stackReq->req.u.delLnk.mtp3LsapId );
  //fillHdr(&(l.dn.hdr), miTransId, ENTDN, 0, TCNTRL, STDNDLSAP, NA);
  fillHdr(&(l.dn.hdr), miTransId, ENTDN, 0, TCNTRL, STDNGEN, NA);
  //cntrl.hdr.elmId.elmntInst2 = 0xff;

  //cntrl.t.cntrl.action = ADELLNK;
  cntrl.t.cntrl.action = LDN_ADEL;
  //cntrl.t.cntrl.subAction = SAELMNT;
  cntrl.t.cntrl.subAction = LDN_SAELMNT;

  cntrl.t.cntrl.ctlType.elmnt.elmntType = LDN_ELMNT_SAP;
  cntrl.t.cntrl.ctlType.elmnt.u.sap.ldfIf = 1;
  cntrl.t.cntrl.ctlType.elmnt.u.sap.id = MTP3_LINK_LSAPID;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
  lmPst->event = EVTLDNCNTRLREQ;
  lmPst->dstProcId = stackReq->procId;
  lmPst->srcProcId = SFndProcId();

  SmMiLdnCntrlReq(lmPst, &cntrl);
  /* update the response structure */

  logger.logMsg (TRACE_FLAG, 0,"delLdfMtp3Link ENT ID <%d>",l.dn.hdr.entId.ent);
  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

  logger.logMsg (TRACE_FLAG, 0,"Leaving delLdfMtp3Link");
return BP_AIN_SM_OK;
}


/* function to handle to del link */
/*int INGwSmCtlHdlr::cliDelLink(DelLink *lnk)
{
	logger.logMsg (TRACE_FLAG, 0,"Entering cliDelLink");
	delMtp3Link(lnk);
	delMtp2Link(lnk);
	logger.logMsg (TRACE_FLAG, 0,"Leaving cliDelLink");
return BP_AIN_SM_OK;
}*/

/* Funtion to delete link set */
int INGwSmCtlHdlr::delLinkSet(StackReqResp *stackReq)
{
	logger.logMsg (TRACE_FLAG, 0,"Entering delLinkSet");
	SnMngmt &cntrl = l.sn;
	cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));

	fillHdr(&(l.sn.hdr), miTransId, ENTSN, 0, TCNTRL, STLNKSET, stackReq->req.u.delLnkSet.lnkSetId);
	//cntrl.hdr.elmId.elmntInst2 = 0xff;

	cntrl.t.cntrl.action = ADELLNKSET;
	cntrl.t.cntrl.subAction = SAELMNT;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
	lmPst->event = EVTLSNCNTRLREQ;
  lmPst->dstProcId = stackReq->procId;
  lmPst->srcProcId = SFndProcId();

	smMiLsnCntrlReq(lmPst, &cntrl);
  /* update the response structure */

  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

	logger.logMsg (TRACE_FLAG, 0,"Leaving  delLinkSet");
return BP_AIN_SM_OK;
}

/* Function to delete Route */
/*int INGwSmCtlHdlr::cliDelRoute(DelRoute *dpc)
{
    delMtp3Route(dpc);
	delSccpRoute(dpc);

return BP_AIN_SM_OK;
}*/

/* Function to delete SCCP Route */

int INGwSmCtlHdlr::delSccpRoute(StackReqResp *stackReq)
{
	SpMngmt &cntrl = l.sp;
	cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

  fillHdr(&(l.sp.hdr), miTransId, ENTSP, 0, TCNTRL, STDELROUT, 0);

	cntrl.t.cntrl.action = STNA;
	cntrl.t.cntrl.subAction = STNA;

	cntrl.t.cntrl.cfg.spDelRte.nSapId = stackReq->req.u.delRoute.nSapId;
	cntrl.t.cntrl.cfg.spDelRte.dpc = stackReq->req.u.delRoute.dpc;
	cntrl.t.cntrl.cfg.spDelRte.ssnPres = FALSE;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
	lmPst->event = EVTLSPCNTRLREQ;
  lmPst->srcProcId = SFndProcId();
  lmPst->dstProcId = stackReq->procId;

	SmMiLspCntrlReq(lmPst, &cntrl);
  /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);


return BP_AIN_SM_OK;
}

/* Function to delete MTP3 Route */
int INGwSmCtlHdlr::delMtp3Route(StackReqResp *stackReq)
{
	SnMngmt &cntrl = l.sn;
	cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));

    fillHdr(&(l.sn.hdr), miTransId, ENTSN, 0, TCNTRL, STROUT, NA);

	cntrl.t.cntrl.action = ADELROUT;
	cntrl.t.cntrl.subAction = SAELMNT;

	cntrl.t.cntrl.ctlType.snRouteId.dpc = stackReq->req.u.delRoute.dpc;
	cntrl.t.cntrl.ctlType.snRouteId.upSwtch = stackReq->req.u.delRoute.upSwtch;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
	lmPst->event = EVTLSNCNTRLREQ;
  lmPst->srcProcId = SFndProcId();
  lmPst->dstProcId = stackReq->procId;

	smMiLsnCntrlReq(lmPst, &cntrl);
  /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

return BP_AIN_SM_OK;
}


/* Function to delete M3UA Route */
int INGwSmCtlHdlr::delM3uaRoute(StackReqResp *stackReq)
{
  ItMgmt &cntrl = l.it;
  cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

    fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITROUT, NA);

  cntrl.t.cntrl.action = ADEL;
  cntrl.t.cntrl.subAction = SAELMNT;
  cntrl.t.cntrl.t.rtEnt.indexType = LIT_RTINDEX_ROUTECFG;

  cntrl.t.cntrl.t.rtEnt.u.rteCfg.nwkId = stackReq->req.u.delPs.nwkId;
  cntrl.t.cntrl.t.rtEnt.u.rteCfg.psId = stackReq->req.u.delPs.psId;
  cntrl.t.cntrl.t.rtEnt.u.rteCfg.rtType = LIT_RTTYPE_PS;
  cntrl.t.cntrl.t.rtEnt.u.rteCfg.rtFilter.dpc = stackReq->req.u.delPs.dpc;
  cntrl.t.cntrl.t.rtEnt.u.rteCfg.rtFilter.dpcMask = stackReq->req.u.delPs.dpcMask;
  cntrl.t.cntrl.t.rtEnt.u.rteCfg.rtFilter.opc = stackReq->req.u.delPs.opc;
  cntrl.t.cntrl.t.rtEnt.u.rteCfg.rtFilter.opcMask = stackReq->req.u.delPs.opcMask;

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::delM3uaRoute psId<%d> and nwkId<%d> procId<%d>", 
    miTransId,stackReq->req.u.delPs.psId,stackReq->req.u.delPs.nwkId ,stackReq->procId );

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
  lmPst->event = EVTLITCNTRLREQ;
  lmPst->srcProcId = SFndProcId();
  lmPst->dstProcId = stackReq->procId;

  smMiLitCntrlReq(lmPst, &cntrl);
  /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

return BP_AIN_SM_OK;
}

/* Function to delete user part */
/*int INGwSmCtlHdlr::cliDelUserPart(DelUserPart *up)
{
   delM3uaUserPart(up);
   delSccpUserPart(up);
   delMtp3UserPart(up);
return BP_AIN_SM_OK;
}*/

/* Function to delete MTP3 user part */
int INGwSmCtlHdlr::delMtp3UserPart(StackReqResp *stackReq)
{
	SnMngmt &cntrl = l.sn;
	cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));
  if(stackReq->req.u.delUserPart.userPartType == MTP3_USER)
	{
		fillHdr(&(l.sn.hdr), miTransId, ENTSN, 0, TCNTRL, STNSAP, stackReq->req.u.delUserPart.mtp3UsapId);
		//cntrl.hdr.elmId.elmntInst2 = 0xff;

		cntrl.t.cntrl.action = ADEL;
		cntrl.t.cntrl.subAction = SAELMNT;

		Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
		lmPst->event = EVTLSNCNTRLREQ;
    lmPst->srcProcId = SFndProcId();
    lmPst->dstProcId = stackReq->procId;

		smMiLsnCntrlReq(lmPst, &cntrl);
		/* update the response structure */

    stackReq->resp.procId = stackReq->procId;
    if(stackReq->txnType != ROLLBACK_TXN) 
      stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

	}

return BP_AIN_SM_OK;
}

/* Function to delete MTP3 user part */
int INGwSmCtlHdlr::delLdfMtp3UserPart(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCtlHdlr::delLdfMtp3UserPart",
        miTransId);

  LdnMngmt &cntrl = l.dn;
  cmMemset((U8 *)&cntrl, 0, sizeof(LdnMngmt));
  if(stackReq->req.u.delUserPart.userPartType == MTP3_USER)
  {
    //fillHdr(&(l.dn.hdr), miTransId, ENTDN, 0, TCNTRL, STDNNSAP, stackReq->req.u.delUserPart.mtp3UsapId);
    fillHdr(&(l.dn.hdr), miTransId, ENTDN, 0, TCNTRL, STDNGEN, NA);
    //cntrl.hdr.elmId.elmntInst2 = 0xff;

    cntrl.t.cntrl.action = ADEL;
    cntrl.t.cntrl.subAction = SAELMNT;
    cntrl.t.cntrl.ctlType.elmnt.elmntType = LDN_ELMNT_SAP;
    cntrl.t.cntrl.ctlType.elmnt.u.sap.ldfIf = 1;
    cntrl.t.cntrl.ctlType.elmnt.u.sap.id = stackReq->req.u.delUserPart.mtp3UsapId;

    Pst *lmPst = mpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
    lmPst->event = EVTLDNCNTRLREQ;
    lmPst->srcProcId = SFndProcId();
    lmPst->dstProcId = stackReq->procId;

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : :Sending req to SM <%d>" ,
        miTransId,cntrl.t.cntrl.subAction );
    SmMiLdnCntrlReq(lmPst, &cntrl);
    /* update the response structure */

    stackReq->resp.procId = stackReq->procId;
    if(stackReq->txnType != ROLLBACK_TXN) 
      stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCtlHdlr::delLdfMtp3UserPart",
        miTransId);

  }

return BP_AIN_SM_OK;
}

/* Function to delete SCCP user part */
int INGwSmCtlHdlr::delSccpUserPart(StackReqResp *stackReq)
{
	SpMngmt &cntrl = l.sp;
	cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

    fillHdr(&(l.sp.hdr), miTransId, ENTSP, 0, TCNTRL, STDELNSAP, stackReq->req.u.delUserPart.sccpLsapId);

	cntrl.t.cntrl.action = NA;
	cntrl.t.cntrl.subAction = STNA;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
	
  lmPst->event = EVTLSPCNTRLREQ;
  lmPst->srcProcId = SFndProcId();
  lmPst->dstProcId = stackReq->procId;
	
  SmMiLspCntrlReq(lmPst, &cntrl);
  
  /* update the response structure */

  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

return BP_AIN_SM_OK;
}


/* Function to delete M3UA user part */
int INGwSmCtlHdlr::delM3uaUserPart(StackReqResp *stackReq)
{
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

	if(stackReq->req.u.delUserPart.userPartType == M3UA_USER)
	{
		fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITNSAP, NA);

		cntrl.t.cntrl.action = ADEL;
		cntrl.t.cntrl.subAction = STNA;

		cntrl.t.cntrl.s.spId = stackReq->req.u.delUserPart.m3uaUsapId;

		Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
		lmPst->event = EVTLITCNTRLREQ;
    lmPst->srcProcId = SFndProcId();
    lmPst->dstProcId = stackReq->procId;

		SmMiLitCntrlReq(lmPst, &cntrl);
		/* update the response structure */

    stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

	}
return BP_AIN_SM_OK;
}


int INGwSmCtlHdlr::delLdfM3uaUserPart(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCtlHdlr::delLdfM3uaUserPart",
        miTransId);

  LdvMngmt &cntrl = l.dv;
  cmMemset((U8 *)&cntrl, 0, sizeof(LdvMngmt));

  if(stackReq->req.u.delUserPart.userPartType == M3UA_USER)
  {
    fillHdr(&(l.dv.hdr), miTransId, ENTDV, 0, TCNTRL, STDVGEN, NA);

    cntrl.t.cntrl.action = ADEL;
    cntrl.t.cntrl.subAction = SAELMNT;
    cntrl.t.cntrl.ctlType.elmnt.elmntType = LDV_ELMNT_SAP;
    cntrl.t.cntrl.ctlType.elmnt.u.sapId = stackReq->req.u.delUserPart.m3uaUsapId;

    //cntrl.t.cntrl.s.spId = stackReq->req.u.delUserPart.m3uaUsapId;

    Pst *lmPst = mpRep->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
    lmPst->event = EVTLDVCNTRLREQ;
    lmPst->srcProcId = SFndProcId();
    lmPst->dstProcId = stackReq->procId;

  logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Sending Sap Id <%d>",
        miTransId,cntrl.t.cntrl.ctlType.elmnt.u.sapId);
    SmMiLdvCntrlReq(lmPst, &cntrl);
    /* update the response structure */

    stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

  }
return BP_AIN_SM_OK;
}

/* Function to delete PS */
int INGwSmCtlHdlr::delM3uaPs(StackReqResp *stackReq)
{
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

	//fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITROUT, NA);
	fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITPS, NA);

	cntrl.t.cntrl.action = ADEL;
	cntrl.t.cntrl.subAction = SAELMNT;

	cntrl.t.cntrl.t.rtEnt.indexType = LIT_RTINDEX_PSID;
	cntrl.t.cntrl.t.rtEnt.u.psId = stackReq->req.u.delPs.psId;
	//cntrl.t.cntrl.t.rtEnt.nwkId= stackReq->req.u.delPs.nwkId;

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::delM3uaPs psId<%d> and nwkId<%d> procId<%d>", 
    miTransId, cntrl.t.cntrl.t.rtEnt.u.psId, cntrl.t.cntrl.t.rtEnt.nwkId, stackReq->procId);

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  lmPst->srcProcId = SFndProcId();
  lmPst->dstProcId = stackReq->procId;


	cntrl.t.cntrl.s.psId = stackReq->req.u.delPs.psId;
	cntrl.hdr.elmId.elmnt = STITPS;                   /* delete peer server */
	
	SmMiLitCntrlReq(lmPst, &cntrl);
    /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

return BP_AIN_SM_OK;
}

/* Function to delete PSP */
int INGwSmCtlHdlr::delM3uaPsp(StackReqResp *stackReq)
{
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

	fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITPSP, NA);

	cntrl.t.cntrl.action = ADEL;
	cntrl.t.cntrl.subAction = SAELMNT;

	cntrl.t.cntrl.s.pspId = stackReq->req.u.delPsp.pspId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  lmPst->srcProcId = SFndProcId();
  lmPst->dstProcId = stackReq->procId;

	SmMiLitCntrlReq(lmPst, &cntrl);
  /* update the response structure */

  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

return BP_AIN_SM_OK;
}

/* Function to delete Network */
/*int INGwSmCtlHdlr::cliDelNetwork( DelNetwork *nwk)
{
	//delM3uaNwk(nwk);
	//delSccpNwk(nwk);
return BP_AIN_SM_OK;
}*/

/* Function to delete SCCP Network */
int INGwSmCtlHdlr::delSccpNwk(StackReqResp *stackReq)
{
	SpMngmt &cntrl = l.sp;
	cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

    fillHdr(&(l.sp.hdr), miTransId, ENTSP, 0, TCNTRL, STDELNW, stackReq->req.u.delNwk.nwkId);

	cntrl.t.cntrl.action = STNA;
	cntrl.t.cntrl.subAction = STNA;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
	lmPst->event = EVTLSPCNTRLREQ;
  lmPst->srcProcId = SFndProcId();
  lmPst->dstProcId = stackReq->procId;

	SmMiLspCntrlReq(lmPst, &cntrl);
  /* update the response structure */

  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN)
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

return BP_AIN_SM_OK;
}

/* Function to delete M3UA Network */
int INGwSmCtlHdlr::delM3uaNwk(StackReqResp *stackReq )
{
    ItMgmt &cntrl = l.it;
    cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

    fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITNWK, NA);

	  cntrl.t.cntrl.action = ADEL;
	  cntrl.t.cntrl.subAction = STNA;

	  cntrl.t.cntrl.s.nwkId = stackReq->req.u.delNwk.nwkId;

	  Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	  lmPst->event = EVTLITCNTRLREQ;
    lmPst->srcProcId = SFndProcId();
    lmPst->dstProcId = stackReq->procId;

	  SmMiLitCntrlReq(lmPst, &cntrl);

    stackReq->resp.procId = stackReq->procId;
    //if(stackReq->txnType == NORMAL_TXN) 
    if(stackReq->txnType != ROLLBACK_TXN)
       stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;
    mrDist.updateRspStruct(miTransId,stackReq);
    
    
    return BP_AIN_SM_OK;
}

/* Function Disable SSN */
/*int INGwSmCtlHdlr::cliDisableLocalSsn(SsnDisable *ssn)
{
	disableTcapLocalSsn(ssn);
return BP_AIN_SM_OK;
}*/

int INGwSmCtlHdlr::disableTcapLocalSsn(StackReqResp *stackReq)
{
	StMngmt &cntrl = l.st;
	cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

        fillHdr(&(l.st.hdr), miTransId, ENTST, 0, TCNTRL, STSPSAP, stackReq->req.u.ssnDisable.sccpUsapId);

	cntrl.t.cntrl.action = AUBND_DIS;
	cntrl.t.cntrl.subAction = STNA;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
	lmPst->event = EVTLSTCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

	SmMiLstCntrlReq(lmPst, &cntrl);
       /* update the response structure */

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

	return BP_AIN_SM_OK;
}
/* Function Enable SSN */
/*int INGwSmCtlHdlr::cliEnableLocalSsn(SsnEnable *ssn)
{
	enableTcapLocalSsn(ssn);
return BP_AIN_SM_OK;
}*/

int INGwSmCtlHdlr::enableTcapLocalSsn(StackReqResp *stackReq)
{
	StMngmt &cntrl = l.st;
	cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

        fillHdr(&(l.st.hdr), miTransId, ENTST, 0, TCNTRL, STSPSAP, stackReq->req.u.ssnEnable.sccpUsapId);

	cntrl.t.cntrl.action = ABND_ENA;
	cntrl.t.cntrl.subAction = STNA;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
	lmPst->event = EVTLSTCNTRLREQ;
	lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

	SmMiLstCntrlReq(lmPst, &cntrl);

	stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

    	mrDist.updateRspStruct(miTransId,stackReq);

    /* update the response structure */
return BP_AIN_SM_OK;
}

/* Function to delete Local Ssn */
/*int INGwSmCtlHdlr::cliDelLocalSsn(DelLocalSsn *ssn)
{
   delSccpSsn(ssn);
   delTcapSsn(ssn);
return BP_AIN_SM_OK;
}*/

/* Function to delete Local Ssn at TCAP */
int INGwSmCtlHdlr::delTcapSsn(StackReqResp *stackReq)
{
	StMngmt &cntrl = l.st;
	cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

    fillHdr(&(l.st.hdr), miTransId, ENTST, 0, TCNTRL, STSPSAP, stackReq->req.u.delLocalSsn.tcapLsapId);

	cntrl.t.cntrl.action = ADEL;
	cntrl.t.cntrl.subAction = STNA;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
	lmPst->event = EVTLSTCNTRLREQ;
  lmPst->srcProcId = SFndProcId();
    lmPst->dstProcId = stackReq->procId;

	SmMiLstCntrlReq(lmPst, &cntrl);
    /* update the response structure */

  stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);
return BP_AIN_SM_OK;
}


/* Function to delete Local Ssn at TCAP */
int INGwSmCtlHdlr::delTcapUsap(StackReqResp *stackReq)
{
  StMngmt &cntrl = l.st;
  cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

    fillHdr(&(l.st.hdr), miTransId, ENTST, 0, TCNTRL, STTCUSAP, stackReq->req.u.delLocalSsn.tcapUsapId);

  cntrl.t.cntrl.action = ADEL;
  cntrl.t.cntrl.subAction = STNA;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
  lmPst->event = EVTLSTCNTRLREQ;
  lmPst->srcProcId = SFndProcId();
    lmPst->dstProcId = stackReq->procId;

  SmMiLstCntrlReq(lmPst, &cntrl);
    /* update the response structure */

  stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);
return BP_AIN_SM_OK;
}


/* Function to delete Local Ssn at SCCP */

int INGwSmCtlHdlr::delSccpSsn(StackReqResp *stackReq)
{
	SpMngmt &cntrl = l.sp;
	cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

    fillHdr(&(l.sp.hdr), miTransId, ENTSP, 0, TCNTRL, STDELTSAP, stackReq->req.u.delLocalSsn.sccpUsapId);

	cntrl.t.cntrl.action = NA;
	cntrl.t.cntrl.subAction = STNA;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
	lmPst->event = EVTLSPCNTRLREQ;
  lmPst->srcProcId = SFndProcId();
    lmPst->dstProcId = stackReq->procId;

	SmMiLspCntrlReq(lmPst, &cntrl);
    /* update the response structure */

  stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);


return BP_AIN_SM_OK;
}

/* Function to send ASP-INAC */
int INGwSmCtlHdlr::m3uaSendAspInActive(StackReqResp *stackReq)
{
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

    fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITPSP, NA);

	cntrl.t.cntrl.action = AASPIA;
	cntrl.t.cntrl.subAction = SAELMNT;

	cntrl.t.cntrl.t.aspm.sctSuId = stackReq->req.u.m3uaAspInact.m3uaLsapId;
	cntrl.t.cntrl.t.aspm.autoCtx = FALSE;
	cntrl.t.cntrl.t.aspm.nmbPs = stackReq->req.u.m3uaAspInact.nmbPs;
  for (int i = 0; i < stackReq->req.u.m3uaAspInact.nmbPs; i++) {
	  cntrl.t.cntrl.t.aspm.psLst[i] = stackReq->req.u.m3uaAspInact.psLst[i];
  }
	cntrl.t.cntrl.s.pspId = stackReq->req.u.m3uaAspInact.pspId;

  //set the request context for alarms
  meReqContext.pspId = cntrl.t.cntrl.s.pspId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  //lmPst->dstProcId = stackReq->procId;
  lmPst->dstProcId = SFndProcId();
  lmPst->srcProcId = SFndProcId();

  printf("[+INC+] %s:%d m3uaSendAspInActive():: Asp Inactive Invoking SmMiLitCntrlReq with msgType %d elmnt %d action %d subAction %d sctSuId %d autoCtx %d pspId %d nmbPs %d srcProcId %d destProcId %d\n", __FILE__, __LINE__, cntrl.hdr.msgType, cntrl.hdr.elmId.elmnt, cntrl.t.cntrl.action, cntrl.t.cntrl.subAction, cntrl.t.cntrl.t.aspm.sctSuId, cntrl.t.cntrl.t.aspm.autoCtx, cntrl.t.cntrl.s.pspId, cntrl.t.cntrl.t.aspm.nmbPs, lmPst->srcProcId, lmPst->dstProcId);
  logger.logMsg (ALWAYS_FLAG, 0,
         "m3uaSendAspInActive():: Asp Inactive Invoking SmMiLitCntrlReq with msgType %d elmnt %d action %d subAction %d sctSuId %d autoCtx %d pspId %d nmbPs %d srcProcId %d destProcId %d\n", cntrl.hdr.msgType, cntrl.hdr.elmId.elmnt, cntrl.t.cntrl.action, cntrl.t.cntrl.subAction, cntrl.t.cntrl.t.aspm.sctSuId, cntrl.t.cntrl.t.aspm.autoCtx, cntrl.t.cntrl.s.pspId, cntrl.t.cntrl.t.aspm.nmbPs, lmPst->srcProcId, lmPst->dstProcId);

	SmMiLitCntrlReq(lmPst, &cntrl);
    /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;
	
  mrDist.updateRspStruct(miTransId,stackReq);

  return BP_AIN_SM_OK;
}

/* Function to send ASP-AC */
int INGwSmCtlHdlr::m3uaSendAspActive(StackReqResp *stackReq)
{
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

    fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITPSP, NA);

	cntrl.t.cntrl.action = AASPAC;
	cntrl.t.cntrl.subAction = SAELMNT;

	cntrl.t.cntrl.t.aspm.sctSuId = stackReq->req.u.m3uaAspAct.m3uaLsapId;
	cntrl.t.cntrl.t.aspm.autoCtx = FALSE;
	cntrl.t.cntrl.t.aspm.nmbPs = 1;
	cntrl.t.cntrl.t.aspm.psLst[0] = stackReq->req.u.m3uaAspAct.psId;
	cntrl.t.cntrl.s.pspId = stackReq->req.u.m3uaAspAct.pspId;

  //set the request context for alarms
  meReqContext.pspId = cntrl.t.cntrl.s.pspId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  lmPst->dstProcId = stackReq->procId;
  lmPst->srcProcId = SFndProcId();

  //sleep(150);
	SmMiLitCntrlReq(lmPst, &cntrl);
    /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;
	
  mrDist.updateRspStruct(miTransId,stackReq);

  return BP_AIN_SM_OK;
}

/* Function to send ASP-DOWN */
int INGwSmCtlHdlr::m3uaSendAspDown(StackReqResp *stackReq)
{
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

    fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITPSP, NA);

	cntrl.t.cntrl.action = AASPDN;
	cntrl.t.cntrl.subAction = SAELMNT;

	cntrl.t.cntrl.t.aspm.sctSuId = stackReq->req.u.m3uaAspDown.m3uaLsapId;
	cntrl.t.cntrl.s.pspId = stackReq->req.u.m3uaAspDown.pspId;

  //set the request context for alarms
  meReqContext.pspId = cntrl.t.cntrl.s.pspId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  //lmPst->dstProcId = stackReq->procId;
  lmPst->dstProcId = SFndProcId();
  lmPst->srcProcId = SFndProcId();

  printf("[+INC+] %s:%d m3uaSendAspDown():: Asp Down Invoking SmMiLitCntrlReq with msgType %d elmnt %d action %d subAction %d sctSuId %d pspId %d srcProcId %d destProcId %d\n", __FILE__, __LINE__, cntrl.hdr.msgType, cntrl.hdr.elmId.elmnt, cntrl.t.cntrl.action, cntrl.t.cntrl.subAction, cntrl.t.cntrl.t.aspm.sctSuId, cntrl.t.cntrl.s.pspId, lmPst->srcProcId, lmPst->dstProcId);
  logger.logMsg (ALWAYS_FLAG, 0,
         "m3uaSendAspDown():: Asp Down Invoking SmMiLitCntrlReq with msgType %d elmnt %d action %d subAction %d sctSuId %d pspId %d srcProcId %d destProcId %d\n", cntrl.hdr.msgType, cntrl.hdr.elmId.elmnt, cntrl.t.cntrl.action, cntrl.t.cntrl.subAction, cntrl.t.cntrl.t.aspm.sctSuId, cntrl.t.cntrl.s.pspId, lmPst->srcProcId, lmPst->dstProcId);
  
	SmMiLitCntrlReq(lmPst, &cntrl);
    /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;
	
  mrDist.updateRspStruct(miTransId,stackReq);

  return BP_AIN_SM_OK;
}
/* Function to send ASP-UP */
int INGwSmCtlHdlr::m3uaSendAspUp(StackReqResp *stackReq)
{
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaSendAspUp ", 
    miTransId );

    fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITPSP, NA);

	cntrl.t.cntrl.action = AASPUP;
	cntrl.t.cntrl.subAction = SAELMNT;

	cntrl.t.cntrl.t.aspm.sctSuId = stackReq->req.u.m3uaAspUp.m3uaLsapId;
	cntrl.t.cntrl.s.pspId = stackReq->req.u.m3uaAspUp.pspId;

  //set the request context for alarms
  meReqContext.pspId = cntrl.t.cntrl.s.pspId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  lmPst->dstProcId = stackReq->procId;
  lmPst->srcProcId = SFndProcId();

  //sleep(20);
	SmMiLitCntrlReq(lmPst, &cntrl);
    /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;
	
  mrDist.updateRspStruct(miTransId,stackReq);
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaSendAspUp ", 
    miTransId );

  return BP_AIN_SM_OK;
}

/* Function to bring down the Association */
int INGwSmCtlHdlr::m3uaAssocDown(StackReqResp *stackReq)
{
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

    fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITPSP, NA);

	cntrl.t.cntrl.action = ATERMINATE;
	cntrl.t.cntrl.subAction = SAELMNT;

	cntrl.t.cntrl.t.aspm.sctSuId = stackReq->req.u.m3uaAssocDown.m3uaLsapId;
	cntrl.t.cntrl.s.pspId = stackReq->req.u.m3uaAssocDown.pspId;

  //set the request context for alarms
  meReqContext.pspId = cntrl.t.cntrl.s.pspId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  //lmPst->dstProcId = stackReq->procId;
  lmPst->dstProcId = SFndProcId();
  lmPst->srcProcId = SFndProcId();

  printf("[+INC+] %s:%d m3uaAssocDown():: Assoc Down Invoking SmMiLitCntrlReq with msgType %d elmnt %d action %d subAction %d sctSuId %d pspId %d srcProcId %d destProcId %d\n", __FILE__, __LINE__, cntrl.hdr.msgType, cntrl.hdr.elmId.elmnt, cntrl.t.cntrl.action, cntrl.t.cntrl.subAction, cntrl.t.cntrl.t.aspm.sctSuId, cntrl.t.cntrl.s.pspId, lmPst->srcProcId, lmPst->dstProcId);
  logger.logMsg (ALWAYS_FLAG, 0,
         "m3uaAssocDown():: Assoc Down Invoking SmMiLitCntrlReq with msgType %d elmnt %d action %d subAction %d sctSuId %d pspId %d srcProcId %d destProcId %d\n", cntrl.hdr.msgType, cntrl.hdr.elmId.elmnt, cntrl.t.cntrl.action, cntrl.t.cntrl.subAction, cntrl.t.cntrl.t.aspm.sctSuId, cntrl.t.cntrl.s.pspId, lmPst->srcProcId, lmPst->dstProcId);

	SmMiLitCntrlReq(lmPst, &cntrl);
    /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

  return BP_AIN_SM_OK;
}

/* Function to establish the Association */
int INGwSmCtlHdlr::m3uaAssocUp(StackReqResp *stackReq)
{
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

    fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITPSP, NA);

	cntrl.t.cntrl.action = AESTABLISH;
	cntrl.t.cntrl.subAction = SAELMNT;

	cntrl.t.cntrl.t.aspm.sctSuId = stackReq->req.u.m3uaAssocUp.m3uaLsapId;
	cntrl.t.cntrl.s.pspId = stackReq->req.u.m3uaAssocUp.pspId;

  //set the request context for alarms
  meReqContext.pspId = cntrl.t.cntrl.s.pspId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  //lmPst->dstProcId = stackReq->procId;
  lmPst->dstProcId = SFndProcId();
  lmPst->srcProcId = SFndProcId();

  printf("[+INC+] %s:%d m3uaAssocUp():: Invoking SmMiLitCntrlReq with cntrl.t.aspm.sctSuId(m3uaAssocUp.m3uaLsapId) %d cntrl.s.pspId(m3uaAssocUp.pspId) %d m3uaAssocUp.endPointId %d\n", __FILE__, __LINE__, cntrl.t.cntrl.t.aspm.sctSuId, cntrl.t.cntrl.s.pspId, stackReq->req.u.m3uaAssocUp.endPointId);

	SmMiLitCntrlReq(lmPst, &cntrl);

    /* update the response structure */
  //stackReq->resp.procId = stackReq->procId;
  stackReq->resp.procId = SFndProcId();
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;
	
  mrDist.updateRspStruct(miTransId,stackReq);

  return BP_AIN_SM_OK;
}


#ifdef INC_ASP_SNDDAUD
int INGwSmCtlHdlr::m3uaSendDaud(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering m3uaSendDaud for SEND DAUD");
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

  fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITAPC, NA);

	cntrl.t.cntrl.action = ASNDDAUD;
	cntrl.t.cntrl.subAction = SAELMNT;

	cntrl.t.cntrl.t.sDaud.nwkId = stackReq->req.u.daud.nwkId;
	cntrl.t.cntrl.t.sDaud.dpc = stackReq->req.u.daud.dpc;

  //set the request context for alarms
  meReqContext.pspId = cntrl.t.cntrl.s.pspId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  lmPst->dstProcId = stackReq->procId;
  lmPst->srcProcId = SFndProcId();

	SmMiLitCntrlReq(lmPst, &cntrl);
    /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;
	
  mrDist.updateRspStruct(miTransId,stackReq);

  logger.logMsg (TRACE_FLAG, 0, "Exiting m3uaSendDaud");
  return BP_AIN_SM_OK;
}
#endif



/* Function to disable to endpoint */
/*int INGwSmCtlHdlr::cliDisableEndPoint(DisableEndPoint *ep)
{
	disableM3uaEndPoint(ep);
	disableSctpEndPoint(ep);
return BP_AIN_SM_OK;
}*/

int INGwSmCtlHdlr::disableM3uaEndPoint(StackReqResp *stackReq)
{
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

    fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITSCTSAP, NA);

	cntrl.t.cntrl.action = AUBND;
	cntrl.t.cntrl.subAction = STNA;

	cntrl.t.cntrl.s.suId = stackReq->req.u.disableEp.m3uaLsapId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  lmPst->dstProcId = stackReq->procId;
  lmPst->srcProcId = SFndProcId();

	SmMiLitCntrlReq(lmPst, &cntrl);
	/* update the response structure */

  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

	return BP_AIN_SM_OK;
}

int INGwSmCtlHdlr::disableSctpEndPoint(StackReqResp *stackReq)
{
	SbMgmt &cntrl = l.sb;
	cmMemset((U8 *)&cntrl, 0, sizeof(SbMgmt));

	{
   	fillHdr(&(l.sb.hdr), miTransId, ENTSB, 0, TCNTRL, STSBTSAP, NA);

		cntrl.t.cntrl.action = AUBND_DIS;
		cntrl.t.cntrl.subAction = STNA;

		cntrl.t.cntrl.sapId = stackReq->req.u.disableEp.sctpLsapId;    /* sctp upper sapId */

		Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCT_LAYER);
		lmPst->event = LSB_EVTCNTRLREQ;
		//lmPst->dstProcId = stackReq->req.u.disableEp.sctpProcId;
		lmPst->dstProcId = SFndProcId();
    lmPst->srcProcId = SFndProcId();

		SmMiLsbCntrlReq(lmPst, &cntrl);
		/* update the response structure */

		stackReq->resp.procId = stackReq->req.u.disableEp.sctpProcId;
   	stackReq->txnType = NORMAL_TXN;
   	stackReq->txnStatus = INPROGRESS;

   	mrDist.updateRspStruct(miTransId,stackReq);

	}
	return BP_AIN_SM_OK;
}


/* Function to enable to endpoint */

/*int INGwSmCtlHdlr::cliEnableEndPoint(EnableEndPoint *ep)
{
	enableM3uaEndPoint(ep);
	enableSctpEndPoint(ep);
return BP_AIN_SM_OK;
}*/

int INGwSmCtlHdlr::enableM3uaEndPoint(StackReqResp *stackReq)
{
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

    	fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITSCTSAP, NA);

	cntrl.t.cntrl.action = ABND;
	cntrl.t.cntrl.subAction = STNA;

	cntrl.t.cntrl.s.suId = stackReq->req.u.enableEp.m3uaLsapId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
	//lmPst->dstProcId = stackReq->procId;
	lmPst->dstProcId = SFndProcId();
    lmPst->srcProcId = SFndProcId();

	SmMiLitCntrlReq(lmPst, &cntrl);

    	/* update the response structure */
        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

	return BP_AIN_SM_OK;
}


int INGwSmCtlHdlr::enableSctpEndPoint(StackReqResp *stackReq)
{
	SbMgmt &cntrl = l.sb;
	cmMemset((U8 *)&cntrl, 0, sizeof(SbMgmt));

	{
        fillHdr(&(l.sb.hdr), miTransId, ENTSB, 0, TCNTRL, STSBTSAP, NA);
	//cntrl.hdr.elmId.elmntInst1 = nwk->nwkId;

	cntrl.t.cntrl.action = ABND_ENA;
	cntrl.t.cntrl.subAction = STNA;

	cntrl.t.cntrl.sapId = stackReq->req.u.enableEp.sctpLsapId;    /* sctp upper sapId */

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCT_LAYER);
	lmPst->event = LSB_EVTCNTRLREQ;
  //lmPst->dstProcId = stackReq->req.u.enableEp.sctpProcId;
  lmPst->dstProcId = SFndProcId();
  lmPst->srcProcId = SFndProcId();

	SmMiLsbCntrlReq(lmPst, &cntrl);
	/* update the response structure */

        stackReq->resp.procId = stackReq->req.u.enableEp.sctpProcId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

	}

	return BP_AIN_SM_OK;
}


/* Function to delete Endpoint */
/*int INGwSmCtlHdlr::cliDelEndPoint(DelEndPoint *ep)
{
	delM3uaEndPoint(ep);
	delSctpEndPoint(ep);
	delTuclEndPoint(ep);
return BP_AIN_SM_OK;
}*/

/* Function to delete TUCL Endpoint */
int INGwSmCtlHdlr::delTuclEndPoint(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering delTuclEndPoint for DEL_ENDPOINT ");

	HiMngmt &cntrl = l.hi;
	cmMemset((U8 *)&cntrl, 0, sizeof(HiMngmt));

	{
		fillHdr(&(l.hi.hdr), miTransId, ENTHI, 0, TCNTRL, STTSAP, NA);
		
		cntrl.t.cntrl.action = ADEL;
		cntrl.t.cntrl.subAction = SAELMNT;

		cntrl.t.cntrl.ctlType.sapId = stackReq->req.u.delEp.tuclUsapId;    /* sctp upper sapId */

		Pst *lmPst = mpRep->getPst (BP_AIN_SM_TUC_LAYER);
		lmPst->event = EVTLHICNTRLREQ;
    lmPst->dstProcId = stackReq->req.u.delEp.sctpProcId;
    lmPst->srcProcId = SFndProcId();

  logger.logMsg (TRACE_FLAG, 0,
    "delTuclEndPoint , data ( sapId %d, dstProcId %d, srcProcId %d)", cntrl.t.cntrl.ctlType.sapId,  lmPst->dstProcId, lmPst->srcProcId);
		SmMiLhiCntrlReq(lmPst, &cntrl);
		/* update the response structure */
    
    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving delTuclEndPoint for DEL_ENDPOINT ");

	}

return BP_AIN_SM_OK;
}

/* Function to delete SCTP Endpoint */
int INGwSmCtlHdlr::delSctpEndPoint(StackReqResp *stackReq)
{
	SbMgmt &cntrl = l.sb;
	cmMemset((U8 *)&cntrl, 0, sizeof(SbMgmt));

	{
		fillHdr(&(l.sb.hdr), miTransId, ENTSB, 0, TCNTRL, STSBSCTSAP, NA);

		cntrl.t.cntrl.action = ADEL;
		cntrl.t.cntrl.subAction = SAELMNT;

		cntrl.t.cntrl.sapId = stackReq->req.u.delEp.sctpUsapId;    /* sctp upper sapId */

		Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCT_LAYER);
		lmPst->event = LSB_EVTCNTRLREQ;
    //lmPst->dstProcId = SFndProcId();
    lmPst->dstProcId = stackReq->req.u.delEp.sctpProcId;
    lmPst->srcProcId = SFndProcId();

    logger.logMsg (TRACE_FLAG, 0,
    "delSctpEndPoint , data ( sapId %d, dstProcId %d, srcProcId %d)", cntrl.t.cntrl.sapId ,  lmPst->dstProcId, lmPst->srcProcId);

		SmMiLsbCntrlReq(lmPst, &cntrl);
		/* update the response structure */

    stackReq->resp.procId = SFndProcId();
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

	}
return BP_AIN_SM_OK;
}

/* Function to delete SCTP Lower Endpoint */
int INGwSmCtlHdlr::delSctpLsapEndPoint(StackReqResp *stackReq)
{
  SbMgmt &cntrl = l.sb;
  cmMemset((U8 *)&cntrl, 0, sizeof(SbMgmt));

  {
    fillHdr(&(l.sb.hdr), miTransId, ENTSB, 0, TCNTRL, STSBTSAP, NA);

    cntrl.t.cntrl.action = ADEL;
    cntrl.t.cntrl.subAction = SAELMNT;

    cntrl.t.cntrl.sapId = stackReq->req.u.delEp.sctpLsapId;    /* sctp upper sapId */

    Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCT_LAYER);
    lmPst->event = LSB_EVTCNTRLREQ;
    //lmPst->dstProcId = stackReq->req.u.delEp.sctpProcId;
    lmPst->dstProcId = stackReq->req.u.delEp.sctpProcId;
    lmPst->srcProcId = SFndProcId();

    logger.logMsg (TRACE_FLAG, 0,
    "delSctpLsapEndPoint , data ( sapId %d, dstProcId %d, srcProcId %d)", cntrl.t.cntrl.sapId ,  lmPst->dstProcId, lmPst->srcProcId);

    SmMiLsbCntrlReq(lmPst, &cntrl);
    /* update the response structure */

    stackReq->resp.procId = SFndProcId();
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

  }
return BP_AIN_SM_OK;
}


/* Function to delete M3UA Endpoint */
int INGwSmCtlHdlr::delM3uaEndPoint(StackReqResp *stackReq)
{
	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

	fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITSCTSAP, NA);
//	cntrl.hdr.elmId.elmntInst1 = nwk->nwkId;

	cntrl.t.cntrl.action = ADEL;
	cntrl.t.cntrl.subAction = STNA;

	//cntrl.t.cntrl.s.suId = stackReq->procId;
	cntrl.t.cntrl.s.suId = stackReq->req.u.delEp.m3uaLsapId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  lmPst->dstProcId = stackReq->procId;
  lmPst->srcProcId = SFndProcId();

	SmMiLitCntrlReq(lmPst, &cntrl);
    /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);


return BP_AIN_SM_OK;
}



/* Function to delete Remote Subsystem */
int INGwSmCtlHdlr::delRemoteSsn(StackReqResp *stackReq)
{
	SpMngmt &cntrl = l.sp;
	cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

    fillHdr(&(l.sp.hdr), miTransId, ENTSP, 0, TCNTRL, STDELROUT, NA);

	cntrl.t.cntrl.action = STNA;
	cntrl.t.cntrl.subAction = STNA;

	cntrl.t.cntrl.cfg.spDelRte.nSapId = stackReq->req.u.delRemoteSsn.nSapId;
	cntrl.t.cntrl.cfg.spDelRte.dpc = stackReq->req.u.delRemoteSsn.dpc;
	cntrl.t.cntrl.cfg.spDelRte.ssnPres = TRUE;
	cntrl.t.cntrl.cfg.spDelRte.ssn = stackReq->req.u.delRemoteSsn.ssn;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
	lmPst->event = EVTLSPCNTRLREQ;
  lmPst->srcProcId = SFndProcId();
  lmPst->dstProcId = stackReq->procId;


	SmMiLspCntrlReq(lmPst, &cntrl);
    /* update the response structure */

  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

return BP_AIN_SM_OK;
}



/******************************************************************************
*
*     Fun:   INGwSmCtlHdlr()
*
*     Desc:  default constructor
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
INGwSmCtlHdlr::INGwSmCtlHdlr(INGwSmDistributor& arDist, int aiLayer,
               int aiOper, int aiSubOp, int aiTransId):
INGwSmReqHdlr (arDist, aiLayer),
miOper (aiOper),
miSubOp (aiSubOp),
miIndex (-1)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::INGwSmCtlHdlr", aiTransId);

  miTransId = aiTransId;
  mpRep = arDist.getSmRepository ();
  mpDist = &arDist;

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::INGwSmCtlHdlr", miTransId);
}


// default destructor
/******************************************************************************
*
*     Fun:   ~INGwSmCtlHdlr()
*
*     Desc:  default destructor
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
INGwSmCtlHdlr::~INGwSmCtlHdlr()
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::~INGwSmCtlHdlr", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::~INGwSmCtlHdlr", miTransId);
}

/******************************************************************************
*
*     Fun:   sendRequest()
*
*     Desc:  Send the message to the Stack
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::sendRequest(INGwSmQueueMsg *apMsg,INGwSmRequestContext *apContext)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::sendRequest subOp<%d>", 
    miTransId, miSubOp);

  //check the layer and then invoke the corres. operation
  switch (miSubOp)
  {
    case BP_AIN_SM_SUBTYPE_ENAALM:
    case BP_AIN_SM_SUBTYPE_DISALM:
    {
      //int liLevel = mpRep->getAlarmLevel (miLayer);

      int liRetVal = setAlarms (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to enable/Disable alarms", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_ENATRC:
    {
      //int liLevel = mpRep->getTrcLevel (miLayer);

      //int liRetVal = setTrace (liLevel);
      int liRetVal = setTrace (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Set Trace", miTransId);
        return BP_AIN_SM_FAIL;
      }

      return liRetVal;

      break;
    }
    case BP_AIN_SM_SUBTYPE_DISTRC:
    {
      //int liLevel = mpRep->getTrcLevel (miLayer);

      //int liRetVal = setTrace (liLevel);
      int liRetVal = disableTrace (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to disable Trace", miTransId);
        return BP_AIN_SM_FAIL;
      }

      return liRetVal;

      break;
    }
    case BP_AIN_SM_SUBTYPE_ENADBG:
    {
      //int liLevel = mpRep->getDebugLevel (miLayer);

      int liRetVal = setDebugPrint (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to set Debug Printing", miTransId);
        return BP_AIN_SM_FAIL;
      }

      return liRetVal;
      break;
    }
    case BP_AIN_SM_SUBTYPE_DISDBG:
    {
      //int liLevel = mpRep->getDebugLevel (miLayer);

      int liRetVal = disableDebugPrint (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to disable Debug Printing", miTransId);
        return BP_AIN_SM_FAIL;
      }

      return liRetVal;
      break;
    }

#ifdef INC_DLG_AUDIT
    case BP_AIN_SM_SUBTYPE_AUDIT:
    {
      int liRetVal = ingwAuditMsg (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to do Audit at TCAP", miTransId);
        return BP_AIN_SM_FAIL;
      }

      return liRetVal;
      break;
    }
#endif

    case BP_AIN_SM_SUBTYPE_SHTDWN:
    {
      int liRetVal = shutdown ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Shutdown", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_OPENEP:
    {
      int liRetVal = m3uaOpenEp (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to open endpoint", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_ENASAP:
    case BP_AIN_SM_SUBTYPE_DISSAP:
    {
      int liRetVal = updateSAP (&(apMsg->t.stackData),miSubOp);
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to update SAP", miTransId);
      }

      return liRetVal;

      break;
    }

    case BP_AIN_SM_SUBTYPE_DISUSAP:
    {
      if(miLayer == BP_AIN_SM_TCA_LAYER)
      { 
        int liRetVal = disableUsap (&(apMsg->t.stackData));
        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Disable TCAP USAP", miTransId);
        }
        return liRetVal;
      }

      break;
    }


    case BP_AIN_SM_SUBTYPE_DISLSAP:
    {
      if(miLayer == BP_AIN_SM_TCA_LAYER)
      { 
        int liRetVal = disableLsap (&(apMsg->t.stackData));
        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Disable TCAP LSAP", miTransId);
        }
        return liRetVal;
      }

      break;
    }

    case BP_AIN_SM_SUBTYPE_ENB_LSSN:
    {
      int liRetVal = enableTcapLocalSsn (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to enable local SSN", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_ENB_USRPART:
    {
      int liRetVal = enableUserPart (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to enable User Part", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_M3U_ENB_EP:
    {
      int liRetVal = enableM3uaEndPoint (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to enable End Point at M3UA", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_SCT_ENB_EP:
    {
      int liRetVal = enableSctpEndPoint (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to enable End Point at SCTP", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_DIS_LSSN:
    {
      int liRetVal = disableTcapLocalSsn (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to disable local SSN", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_DIS_USRPART:
    {
      int liRetVal = disableUserPart (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to disable User Part", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_M3U_DIS_EP:
    {
      int liRetVal = disableM3uaEndPoint (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to disable End Point at M3UA", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_SCT_DIS_EP:
    {
      int liRetVal = disableSctpEndPoint (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to disable End Point at SCTP", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_GTADDRMAP:
    {
      int liRetVal = delGtAddrMap (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to delete GT address MAP at SCCP", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_GTRULE:
    {
      int liRetVal = delGtRule (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to delete GT Rule at SCCP", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }


#ifdef INC_DLG_AUDIT
    case BP_AIN_SM_SUBTYPE_AUDDLG:
    {
      int liRetVal = tcapAuditDialogs ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Audit Dialogs", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }

    case BP_AIN_SM_SUBTYPE_AUDINV:
    {
      int liRetVal = tcapAuditInvokes ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Audit Invokes", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
#endif

    case BP_AIN_SM_SUBTYPE_CNGCTL:
    {
      int liRetVal = sccpCongestionControl ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to SCCP Congestion Control", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_DISCSA:
    {
      int liRetVal = sccpConnStatusAudit ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Disable Connection Status Audit", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_ENACSA:
    {
      int liRetVal = sccpConnStatusAudit ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to enable Connextion Status Audit", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_ENAERR:
    {
      int liRetVal = sccpEnableErrorReport ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Enable Error Report", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_GRDTMR:
    {
      int liRetVal = sccpGuardTimer ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Enable Guard Timer", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_DELADM:
    {
      int liRetVal = sccpDeleteAddressMap ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Delete Address Map", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_DELASS:
    {
      int liRetVal = sccpDeleteAssociation ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Delete Association", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_DELNWK:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_SCC_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DELNWK Delete Network for BP_AIN_SM_SCC_LAYER ", miTransId);

        liRetVal = delSccpNwk (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to SCCP Delete Network", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      if (miLayer == BP_AIN_SM_M3U_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DELNWK Delete Network for BP_AIN_SM_M3U_LAYER ", miTransId);

        liRetVal = delM3uaNwk(&(apMsg->t.stackData));
        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to M3UA Delete Network", miTransId);
          return BP_AIN_SM_FAIL;
        }
         
      }
      break;
    }
    case BP_AIN_SM_SUBTYPE_DEL_UP:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_SCC_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_UP Delete User Part for BP_AIN_SM_SCC_LAYER ", miTransId);

        liRetVal = delSccpUserPart (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to SCCP Delete User Part", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      else if (miLayer == BP_AIN_SM_M3U_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_UP Delete User Part for BP_AIN_SM_M3U_LAYER ", miTransId);

        liRetVal = delM3uaUserPart (&(apMsg->t.stackData));
        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to M3UA Delete User Part", miTransId);
          return BP_AIN_SM_FAIL;
        }
        
       }
       else if (miLayer == BP_AIN_SM_MTP3_LAYER)
       {
         logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_UP Delete User Part for BP_AIN_SM_MTP3_LAYER ", miTransId);

        liRetVal = delMtp3UserPart (&(apMsg->t.stackData));
        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to MTP3 Delete User Part", miTransId);
          return BP_AIN_SM_FAIL;
        }
       }
       else if (miLayer == BP_AIN_SM_LDF_M3UA_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_UP Delete User Part for BP_AIN_SM_LDF_M3U_LAYER ", miTransId);

        liRetVal = delLdfM3uaUserPart (&(apMsg->t.stackData));
        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete LDF M3UA User Part", miTransId);
          return BP_AIN_SM_FAIL;
        }

       }
       else if (miLayer == BP_AIN_SM_LDF_MTP3_LAYER)
       {
         logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_UP Delete User Part for BP_AIN_SM_LDF_MTP3_LAYER ", miTransId);

        liRetVal = delLdfMtp3UserPart (&(apMsg->t.stackData));
        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Delete LDF MTP3 User Part", miTransId);
          return BP_AIN_SM_FAIL;
        }
       }


      break;
    }
    case BP_AIN_SM_SUBTYPE_DEL_LSSN:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_SCC_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_LSSN Delete Local SSN for BP_AIN_SM_SCC_LAYER ", miTransId);

        liRetVal = delSccpSsn (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Delete SCCP Local SSN", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      /*if (miLayer == BP_AIN_SM_TCA_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_LSSN Delete Local SSN for BP_AIN_SM_TCA_LAYER ", miTransId);

        liRetVal = delTcapSsn (&(apMsg->t.stackData));
        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Delete TCAP Local SSN", miTransId);
          return BP_AIN_SM_FAIL;
        }
         
      }*/
      break;
    }
    case BP_AIN_SM_SUBTYPE_DEL_LSAP:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_TCA_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_LSAP Delete Local SSN for BP_AIN_SM_TCA_LAYER ", miTransId);

        liRetVal = delTcapSsn (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Delete TCAP LSAP for Local SSN", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }

    case BP_AIN_SM_SUBTYPE_DEL_USAP:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_TCA_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_USAP Delete Local SSN for BP_AIN_SM_TCA_LAYER ", miTransId);

        liRetVal = delTcapUsap (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Delete TCAP USAP for Local SSN", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }

    case BP_AIN_SM_SUBTYPE_DEL_RSSN:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_SCC_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_RSSN Delete Remote SSN for BP_AIN_SM_SCC_LAYER ", miTransId);

        liRetVal = delRemoteSsn (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Delete SCCP Remote SSN", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }

    case BP_AIN_SM_SUBTYPE_DELRTE:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_SCC_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DELRTE Delete Route for BP_AIN_SM_SCC_LAYER ", miTransId);

        liRetVal = delSccpRoute (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to SCCP Delete Route", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      if (miLayer == BP_AIN_SM_MTP3_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DELRTE Delete Route for BP_AIN_SM_MTP3_LAYER ", miTransId);

        liRetVal = delMtp3Route(&(apMsg->t.stackData));
        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to M3UA Delete Route", miTransId);
          return BP_AIN_SM_FAIL;
        }
         
      }
      break;
    }

    case BP_AIN_SM_SUBTYPE_ENBLNK:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_MTP3_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest Enable Link for BP_AIN_SM_MTP3_LAYER ", miTransId);

        liRetVal = enableMtp3Lnk (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Enable MTP3 Link ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest Enable Link for BP_AIN_SM_MTP2_LAYER ", miTransId);

        liRetVal = enableMtp2Lnk (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Enable MTP2 Link ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }
    case BP_AIN_SM_SUBTYPE_DISLNK:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_MTP3_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DISLNK Disable Link for BP_AIN_SM_MTP3_LAYER ", miTransId);

        liRetVal = disableMtp3Link (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to disable MTP3 Link ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DISLNK Enable Link for BP_AIN_SM_MTP2_LAYER ", miTransId);

        liRetVal = disableMtp2Link (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Disable MTP2 Link ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }
    case BP_AIN_SM_SUBTYPE_ENBNODE:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_SG_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest Enable Node for BP_AIN_SM_SG_LAYER ", BP_AIN_SM_SG_TRANSID);

        liRetVal = sgEnableNode(&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Enable Node", BP_AIN_SM_SG_TRANSID);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }
    case BP_AIN_SM_SUBTYPE_ABORT_SG_TRANS:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_SG_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest To Abort the ongoing transaction for BP_AIN_SM_SG_LAYER ", BP_AIN_SM_SG_TRANSID);

        liRetVal = sgAbortTrans(&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to abort the ongoing transaction for SG Node", BP_AIN_SM_SG_TRANSID);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }

    case BP_AIN_SM_SUBTYPE_DISNODE:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_SG_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest Disable Node for BP_AIN_SM_SG_LAYER ", BP_AIN_SM_SG_TRANSID);

        liRetVal = sgDisableNode(&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to Disable Node SG", BP_AIN_SM_SG_TRANSID);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }
 
    case BP_AIN_SM_SUBTYPE_DELLNKSET:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_MTP3_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DELLNKSET Delete Linkset for BP_AIN_SM_MTP3_LAYER ", miTransId);

        liRetVal = delLinkSet (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete MTP3 LinkSet ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }
    case BP_AIN_SM_SUBTYPE_DELLNK:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_MTP3_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DELLNK Delete Link for BP_AIN_SM_MTP3_LAYER ", miTransId);

        liRetVal = delMtp3Link (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete MTP3 Link ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      else if(miLayer == BP_AIN_SM_LDF_MTP3_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DELLNK Delete Link for BP_AIN_SM_LDF_MTP3_LAYER ", miTransId);

        liRetVal = delLdfMtp3Link (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete LDF MTP3 Link ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }

      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DELLNK Delete Link for BP_AIN_SM_MTP2_LAYER ", miTransId);

        liRetVal = delMtp2Link (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete MTP2 Link ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }

    case BP_AIN_SM_SUBTYPE_DEL_T_EP:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_SCT_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_T_EP Delete End Point for BP_AIN_SM_SCT_LAYER ", miTransId);

        liRetVal = delSctpLsapEndPoint (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete SCTP Lower EP ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }
    case BP_AIN_SM_SUBTYPE_DEL_EP:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_M3U_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_EP Delete End Point for BP_AIN_SM_M3U_LAYER ", miTransId);

        liRetVal = delM3uaEndPoint (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete M3UA EP ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      else if(miLayer == BP_AIN_SM_SCT_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_EP Delete End Point for BP_AIN_SM_SCTP_LAYER and procId <%d>", miTransId,apMsg->t.stackData.req.u.delEp.sctpProcId);

        liRetVal = delSctpEndPoint (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete SCTP EP ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_EP Delete End Point for BP_AIN_SM_TUCL_LAYER ", miTransId);

        liRetVal = delTuclEndPoint (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete TUCL EP ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }
    case BP_AIN_SM_SUBTYPE_DEL_ASP:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_M3U_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_ASP Delete ASP for BP_AIN_SM_M3U_LAYER ", miTransId);

        liRetVal = delM3uaPsp (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete M3UA ASP ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }

    case BP_AIN_SM_SUBTYPE_DEL_AS:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_M3U_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_AS Delete AS for BP_AIN_SM_M3U_LAYER ", miTransId);

        liRetVal = delM3uaPs (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete M3UA AS ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      break;
    }


    case BP_AIN_SM_SUBTYPE_DEL_RTE:
    {
      int liRetVal;
      if(miLayer == BP_AIN_SM_MTP3_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_RTE Delete Route for BP_AIN_SM_MTP3_LAYER ", miTransId);

        liRetVal = delMtp3Route (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete MTP3 Route ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      else if(miLayer == BP_AIN_SM_SCC_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_RTE Delete Route for BP_AIN_SM_SCCP_LAYER ", miTransId);

        liRetVal = delSccpRoute (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete SCCP Route ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> :In INGwSmCtlHdlr::sendRequest case BP_AIN_SM_SUBTYPE_DEL_RTE Delete Route for BP_AIN_SM_M3UA_LAYER ", miTransId);

        liRetVal = delM3uaRoute (&(apMsg->t.stackData));

        if (liRetVal == BP_AIN_SM_FAIL)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> :Unable to delete M3UA Route ", miTransId);
          return BP_AIN_SM_FAIL;
        }
      }

      break;
    }
 
    case BP_AIN_SM_SUBTYPE_ESTASS:
    {
      logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA association UP",
         miTransId);
      int liRetVal = m3uaAssocUp (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to establish association", miTransId);
        return BP_AIN_SM_FAIL;
      }


#if 0
      int liPspId = mpRep->getPspId (apContext, miIndex);

      /*
       * PSP Id = 0 means no more PSPs remaining
       * PSP Id = -1 means error occurred
       */
      if (liPspId == -1)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :No PSP Id Configured", miTransId);
        return BP_AIN_SM_FAIL;
      }
      else if (liPspId == 0)
      {
        return BP_AIN_SM_INDEX_OVER;
      }

      /*
       * Check the PSP State and send request only if in
       * proper state
       */
      INGwSmPspState * lpPspState = mpRep->getPspState (liPspId);

      if (lpPspState == 0)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "PSP Id <%d> could not be located in PSP State Table",
          lpPspState);

        return BP_AIN_SM_FAIL;
      }

      if (lpPspState->state == BP_AIN_SM_PSP_ST_ESTASS)
      {
        logger.logMsg (WARNING_FLAG, 0,
          "TID <%d> :Association is already established <%d, %d>", 
          miTransId, liPspId, lpPspState->state);
        return BP_AIN_SM_SEND_NEXT;
      }

      if (lpPspState->state != BP_AIN_SM_PSP_ST_DOWN)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Association is wrong state to send EST ASS <%d, %d>", 
          miTransId, liPspId, lpPspState->state);
        return BP_AIN_SM_SEND_NEXT;
      }

      int liRetVal = m3uaEstablishAssociation (liPspId);
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to establish association", miTransId);
        return BP_AIN_SM_FAIL;
      }

      miIndex ++;
#endif
      break;
    }

    case BP_AIN_SM_SUBTYPE_ABRTASS:
    {
      logger.logMsg (ALWAYS_FLAG, 0,
         "TID <%d> : Starting M3UA Abort association",
         miTransId);
      int liRetVal = m3uaAssocAbort (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Abort association", miTransId);
        return BP_AIN_SM_FAIL;
      }
      break;
    }

    case BP_AIN_SM_SUBTYPE_M3UUBND:
    {
      logger.logMsg (ALWAYS_FLAG, 0,
         "TID <%d> : Starting M3UA SCTSAP UNBIND",
         miTransId);
      int liRetVal = m3uaSctsapUnbnd (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to UNBIND SCTSAP", miTransId);
        return BP_AIN_SM_FAIL;
      }
      break;
    }
    
       
    case BP_AIN_SM_SUBTYPE_TRMASS:
    {
      logger.logMsg (ALWAYS_FLAG, 0,
         "TID <%d> : Starting M3UA Term association",
         miTransId);
      int liRetVal = m3uaAssocDown (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Term association", miTransId);
        return BP_AIN_SM_FAIL;
      }
#if 0
      int liPspId = mpRep->getPspId (apContext, miIndex);

      /*
       * PSP Id = 0 means no more PSPs remaining
       * PSP Id = -1 means error occurred
       */
      if (liPspId == -1)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :No PSP Id Configured", miTransId);
        return BP_AIN_SM_FAIL;
      }
      else if (liPspId == 0)
      {
        return BP_AIN_SM_INDEX_OVER;
      }

      /*
       * Check the PSP State and send request only if in
       * proper state
       */
      INGwSmPspState *lpPspState = mpRep->getPspState (liPspId);

      if (lpPspState == 0)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "PSP Id <%d> could not be located in PSP State Table",
          lpPspState);

        return BP_AIN_SM_FAIL;
      }



      if (lpPspState->state == BP_AIN_SM_PSP_ST_DOWN)
      {
        logger.logMsg (WARNING_FLAG, 0,
          "TID <%d> :Association is already terminated <%d, %d>", 
          miTransId, liPspId, lpPspState->state);
        return BP_AIN_SM_SEND_NEXT;
      }

      if (lpPspState->state != BP_AIN_SM_PSP_ST_ESTASS)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Association is not in correct state for termination <%d, %d>", 
          miTransId, liPspId, lpPspState->state);
        return BP_AIN_SM_FAIL;
      }

      int liRetVal = m3uaTerminateAssociation (liPspId);
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to terminate association", miTransId);
        return BP_AIN_SM_FAIL;
      }


      miIndex++;
#endif
      break;
    }
    case BP_AIN_SM_SUBTYPE_SNDAUP:
    {
      logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA ASP UP",
         miTransId);
      int liRetVal = m3uaSendAspUp (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to send ASP UP", miTransId);
        return BP_AIN_SM_FAIL;
      }
#if 0
      int liPspId = mpRep->getPspId (apContext, miIndex);

      /*
       * PSP Id = 0 means no more PSPs remaining
       * PSP Id = -1 means error occurred
       */
      if (liPspId == -1)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :No PSP Id Configured", miTransId);
        return BP_AIN_SM_FAIL;
      }
      else if (liPspId == 0)
      {
        return BP_AIN_SM_INDEX_OVER;
      }

      /*
       * Check the PSP State and send request only if in
       * proper state
       */
      INGwSmPspState *lpPspState = mpRep->getPspState (liPspId);

      if (lpPspState == 0)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "PSP Id <%d> could not be located in PSP State Table",
          lpPspState);

        return BP_AIN_SM_FAIL;
      }



      if (lpPspState->state == BP_AIN_SM_PSP_ST_ASPUP)
      {
        logger.logMsg (WARNING_FLAG, 0,
          "TID <%d> :ASP is already in ASP UP <%d, %d>", 
          miTransId, liPspId, lpPspState->state);
        return BP_AIN_SM_SEND_NEXT;
      }

      else if (lpPspState->state == BP_AIN_SM_PSP_ST_DOWN)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :ASP is wrong state to send ASP UP <%d, %d>", 
          miTransId, liPspId, lpPspState->state);
        return BP_AIN_SM_FAIL;
      }

      else if (lpPspState->state != BP_AIN_SM_PSP_ST_ESTASS)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :ASP is wrong state to send ASP UP <%d, %d>",
          miTransId, liPspId, lpPspState->state);
        return BP_AIN_SM_SEND_NEXT;
      }

      int liRetVal = m3uaSendAspup (liPspId);
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to send ASPUP", miTransId);
        return BP_AIN_SM_FAIL;
      }


      miIndex++;
#endif
      break;
    }
    case BP_AIN_SM_SUBTYPE_SNDAAC:
    {
      logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA ASP AC",
         miTransId);
      int liRetVal = m3uaSendAspActive (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to send ASP AC", miTransId);
        return BP_AIN_SM_FAIL;
      }
#if 0
      int liPspId = mpRep->getPspId (apContext, miIndex);

      /*
       * PSP Id = 0 means no more PSPs remaining
       * PSP Id = -1 means error occurred
       */
      if (liPspId == -1)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :No PSP Id Configured", miTransId);
        return BP_AIN_SM_FAIL;
      }
      else if (liPspId == 0)
      {
        return BP_AIN_SM_INDEX_OVER;
      }

      /*
       * Check the PSP State and send request only if in
       * proper state
       */
      INGwSmPspState *lpPspState = mpRep->getPspState (liPspId);

      if (lpPspState == 0)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "PSP Id <%d> could not be located in PSP State Table",
          lpPspState);

        return BP_AIN_SM_FAIL;
      }



      if (lpPspState->state == BP_AIN_SM_PSP_ST_ASPAC)
      {
        logger.logMsg (WARNING_FLAG, 0,
          "TID <%d> :ASP is in already ASP AC <%d, %d>", 
          miTransId, liPspId, lpPspState->state);
        return BP_AIN_SM_SEND_NEXT;
      }

      if (lpPspState->state != BP_AIN_SM_PSP_ST_ASPUP)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :ASP is in wrong state to send ASP AC <%d, %d>", 
          miTransId, liPspId, lpPspState->state);
        return BP_AIN_SM_FAIL;
      }

      int liRetVal = m3uaSendAspac (liPspId);
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to send ASP Active", miTransId);
        return BP_AIN_SM_FAIL;
      }


      miIndex++;
#endif
      break;
    }
    case BP_AIN_SM_SUBTYPE_SNDADN:
    {
      logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA ASP Down",
         miTransId);
      int liRetVal = m3uaSendAspDown (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to send ASP down", miTransId);
        return BP_AIN_SM_FAIL;
      }
#if 0
      int liPspId = mpRep->getPspId (apContext, miIndex);

      /*
       * PSP Id = 0 means no more PSPs remaining
       * PSP Id = -1 means error occurred
       */
      if (liPspId == -1)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :No PSP Id Configured", miTransId);
        return BP_AIN_SM_FAIL;
      }
      else if (liPspId == 0)
      {
        return BP_AIN_SM_INDEX_OVER;
      }

      /*
       * Check the PSP State and send request only if in
       * proper state
       */
      INGwSmPspState *lpPspState = mpRep->getPspState (liPspId);

      if (lpPspState == 0)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "PSP Id <%d> could not be located in PSP State Table",
          lpPspState);

        return BP_AIN_SM_FAIL;
      }



      if (lpPspState->state == BP_AIN_SM_PSP_ST_ESTASS)
      {
        logger.logMsg (WARNING_FLAG, 0,
          "TID <%d> :ASP is already in ASP DN <%d, %d>", 
          miTransId, liPspId, lpPspState->state);
        return BP_AIN_SM_SEND_NEXT;
      }

      if (lpPspState->state == BP_AIN_SM_PSP_ST_DOWN)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :ASP is in DOWN state to not send ASP DN <%d, %d>", 
          miTransId, liPspId, lpPspState->state);
        return BP_AIN_SM_SEND_NEXT;
      }

      if (lpPspState->state != BP_AIN_SM_PSP_ST_ASPUP)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :ASP is not in correct state to send ASP DN <%d, %d>",
          miTransId, liPspId, lpPspState->state);
        return BP_AIN_SM_FAIL;
      }

      int liRetVal = m3uaSendAspdn (liPspId);
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to send ASP Down", miTransId);
        return BP_AIN_SM_FAIL;
      }


      miIndex++;
#endif
      break;
    }
    case BP_AIN_SM_SUBTYPE_SNDAIA:
    {
      logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA ASP INACT",
         miTransId);
      int liRetVal = m3uaSendAspInActive (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to send ASP INACT", miTransId);
        return BP_AIN_SM_FAIL;
      }
#if 0
      int liPspId = mpRep->getPspId (apContext, miIndex);

      /*
       * PSP Id = 0 means no more PSPs remaining
       * PSP Id = -1 means error occurred
       */
      if (liPspId == -1)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :No PSP Id Configured", miTransId);
        return BP_AIN_SM_FAIL;
      }
      else if (liPspId == 0)
      {
        return BP_AIN_SM_INDEX_OVER;
      }

      /*
       * Check the PSP State and send request only if in
       * proper state
       */
      INGwSmPspState *lpPspState = mpRep->getPspState (liPspId);

      if (lpPspState == 0)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "PSP Id <%d> could not be located in PSP State Table",
          lpPspState);

        return BP_AIN_SM_FAIL;
      }



      if (lpPspState->state == BP_AIN_SM_PSP_ST_ASPUP)
      {
        logger.logMsg (WARNING_FLAG, 0,
          "TID <%d> :ASP is already in ASP UP <%d, %d>", 
          miTransId, liPspId, lpPspState->state);

        return BP_AIN_SM_SEND_NEXT;
      }
      if (lpPspState->state != BP_AIN_SM_PSP_ST_ASPAC)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :ASP not in correct state to send ASP IA <%d, %d>", 
          miTransId, liPspId, lpPspState->state);

        return BP_AIN_SM_SEND_NEXT;
      }

      int liRetVal = m3uaSendAspia (liPspId);
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to send ASPIA", miTransId);
        return BP_AIN_SM_FAIL;
      }


      miIndex++;
#endif
      break;
    }

#ifdef INC_ASP_SNDDAUD
    case BP_AIN_SM_SUBTYPE_SNDDAUD:
    {
      logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA SEND DAUD", miTransId);
      int liRetVal = m3uaSendDaud (&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to send DAUD", miTransId);
        return BP_AIN_SM_FAIL;
      }
      break;
    }
#endif
    
    case BP_AIN_SM_SUBTYPE_SNDSCN:
    {
      int liRetVal = m3uaSendScon ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to send SCON", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_INHASS:
    {
      int liRetVal = m3uaInhibitAssociation ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Inhibit Association", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_UINASS:
    {
      int liRetVal = m3uaUninhibitAssociation ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to uninhibit Association", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_DELPS:
    {
      int liRetVal = m3uaDeletePS ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Dlete PS", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_DELPSP:
    {
      int liRetVal = m3uaDeletePSP ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to delete PSP", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_REGDRK:
    {
      int liRetVal = m3uaRegisterRoutingKeys ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to register Routing Key", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    case BP_AIN_SM_SUBTYPE_DERDRK:
    {
      int liRetVal = m3uaDeregisterRoutingKeys ();
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Deregister Routing Key", miTransId);
        return BP_AIN_SM_FAIL;
      }

      break;
    }

    case BP_AIN_SM_SUBTYPE_SHTDN_TCA:
    {
      logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Shutting down TCAP Layer", miTransId);
      int liRetVal = shutdownTcapLayer(&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Shutdown TCAP Layer", miTransId);
        return BP_AIN_SM_FAIL;
      }
    }
    break;

    case BP_AIN_SM_SUBTYPE_SHTDN_SCC:
    {
      logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Shutting down SCCP Layer", miTransId);
      int liRetVal = shutdownSccpLayer(&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Shutdown SCCP Layer", miTransId);
        return BP_AIN_SM_FAIL;
      }
    }
    break;

    case BP_AIN_SM_SUBTYPE_SHTDN_MTP3:
    {
      logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Shutting down MTP3 Layer", miTransId);
      int liRetVal = shutdownMtp3Layer(&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Shutdown MTP3 Layer", miTransId);
        return BP_AIN_SM_FAIL;
      }
    }
    break;

    case BP_AIN_SM_SUBTYPE_SHTDN_M3U:
    {
      logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Shutting down M3UA Layer", miTransId);
      int liRetVal = shutdownM3uaLayer(&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Shutdown M3UA Layer", miTransId);
        return BP_AIN_SM_FAIL;
      }
    }
    break;

    case BP_AIN_SM_SUBTYPE_SHTDN_SCT:
    {
      logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Shutting down SCTP Layer", miTransId);
      int liRetVal = shutdownSctpLayer(&(apMsg->t.stackData));
      if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Unable to Shutdown SCTP Layer", miTransId);
        return BP_AIN_SM_FAIL;
      }
    }
    break;
            
    default :
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d> :Unkown Control Message invoked <%d>",
        miTransId, miSubOp);
      return BP_AIN_SM_FAIL;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::sendRequest", miTransId);

  return BP_AIN_SM_OK;
}

/*
 * Available for All Layers
 */

/******************************************************************************
*
*     Fun:   setAlarms()
*
*     Desc:  Set the Alarms for a Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::setAlarms (StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::setAlarms ", 
    miTransId);

  switch (miLayer)
  {
#if 0
    case BP_AIN_SM_AIN_LAYER:
    {
      IeMngmt &cntrl = l.ie;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(IeMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if (aiAlarmType == 1)
        cntrl.t.cntrl.action = AENA;
      else 
        cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SAUSTA;
      cntrl.hdr.entId.ent = ENTIE;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_AIN_LAYER);
      lmPst->event = LIE_EVTCNTRLREQ;

      smMiLieCntrlReq(lmPst, &cntrl);

      break;
    }
#endif
    case BP_AIN_SM_TCA_LAYER:
    {
      StMngmt &cntrl = l.st;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = AENA;
      else
        cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SAUSTA;
      cntrl.hdr.entId.ent = ENTST;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
      lmPst->event = EVTLSTCNTRLREQ;

      smMiLstCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_SCC_LAYER:
    {
      SpMngmt &cntrl = l.sp;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = AENA;
      else
        cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.ctlType.ustaMask = LSP_USTAALARM;

      cntrl.t.cntrl.subAction = SAUSTA;
      cntrl.hdr.entId.ent = ENTSP;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
      lmPst->event = EVTLSPCNTRLREQ;

      smMiLspCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_M3U_LAYER:
    {
      ItMgmt &cntrl = l.it;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = AENA;
      else
        cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SAUSTA;
      cntrl.hdr.entId.ent = ENTIT;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STITGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
      lmPst->event = EVTLITCNTRLREQ;

      smMiLitCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_SCT_LAYER:
    {
      SbMgmt &cntrl = l.sb;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SbMgmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = AENA;
      else
        cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SAUSTA;
      cntrl.hdr.entId.ent = ENTSB;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STSBGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCT_LAYER);
      lmPst->event = LSB_EVTCNTRLREQ;

      smMiLsbCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_TUC_LAYER:
    {
      HiMngmt &cntrl = l.hi;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(HiMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = AENA;
      else
        cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SAUSTA;
      cntrl.hdr.entId.ent = ENTHI;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_TUC_LAYER);
      lmPst->event = EVTLHICNTRLREQ;

      smMiLhiCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_MTP3_LAYER:
    {
      SnMngmt &cntrl = l.sn;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = AENA;
      else
        cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SAUSTA;
      cntrl.hdr.entId.ent = ENTSN;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
      lmPst->event = EVTLSNCNTRLREQ;

      smMiLsnCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
   case BP_AIN_SM_MTP2_LAYER:
    {
      SdMngmt &cntrl = l.sd;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SdMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = AENA;
      else
        cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SAUSTA;
      cntrl.hdr.entId.ent = ENTSD;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP2_LAYER);
      lmPst->event = EVTLSDCNTRLREQ;

      smMiLsdCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }

	case BP_AIN_SM_PSF_MTP3_LAYER:
    {
      ZnMngmt &cntrl = l.zn;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZnMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = LZN_AENA;
      else
        cntrl.t.cntrl.action = LZN_ADISIMM;

      cntrl.t.cntrl.subAction = LZN_SAUSTA;
      cntrl.hdr.entId.ent = ENTSN;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STZNGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_PSF_MTP3_LAYER);
      lmPst->event = EVTZNMILZNCNTRLREQ;

      SmMiLznCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_LDF_MTP3_LAYER:
    {
      LdnMngmt &cntrl = l.dn;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(LdnMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = AENA;
      else
        cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SAUSTA;
      cntrl.hdr.entId.ent = ENTDN;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STDNGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
      lmPst->event = EVTLDNCNTRLREQ;

      SmMiLdnCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_LDF_M3UA_LAYER:
    {
      LdvMngmt &cntrl = l.dv;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(LdvMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = LDV_AENA;
      else
        cntrl.t.cntrl.action = LDV_ADISIMM;

      cntrl.t.cntrl.subAction = LDV_SAUSTA;
      cntrl.hdr.entId.ent = ENTDV;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STDVGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
      lmPst->event = EVTLDVCNTRLREQ;

      SmMiLdvCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_PSF_M3UA_LAYER:
    {
      ZvMngmt &cntrl = l.zv;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZvMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = LZV_AENA;
      else
        cntrl.t.cntrl.action = LZV_ADISIMM;

      cntrl.t.cntrl.subAction = LZV_SAUSTA;
      cntrl.hdr.entId.ent = ENTIT;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STZVGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_PSF_M3UA_LAYER);
      lmPst->event = EVTZVMILZVCNTRLREQ;

      SmMiLzvCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_PSF_SCCP_LAYER:
    {
      ZpMngmt &cntrl = l.zp;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZpMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = LZP_AENA;
      else
        cntrl.t.cntrl.action = LZP_ADISIMM;

      cntrl.t.cntrl.subAction = LZP_SAUSTA;
      cntrl.hdr.entId.ent = ENTSP;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STZPGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_PSF_SCCP_LAYER);
      lmPst->event = EVTZPMILZPCNTRLREQ;

      SmMiLzpCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_PSF_TCAP_LAYER:
    {
      ZtMngmt &cntrl = l.zt;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZtMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl.action = LZT_AENA;
      else
        cntrl.t.cntrl.action = LZT_ADISIMM;

      cntrl.t.cntrl.subAction = LZT_SAUSTA;
      cntrl.hdr.entId.ent = ENTST;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STZTGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_PSF_TCAP_LAYER);
      lmPst->event = EVTZTMILZTCNTRLREQ;

      SmMiLztCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_MR_LAYER:
    {
      MrMngmt &cntrl = l.mr;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(MrMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.s.cntrl.action = LMR_AENA;
      else
        cntrl.s.cntrl.action = LMR_ADISIMM;

      cntrl.s.cntrl.subAction = LMR_SAUSTA;
      cntrl.hdr.entId.ent = ENTMR;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STMRGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_MR_LAYER);
      lmPst->event = EVTLMRCNTRLREQ;

      SmMiLmrCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_SH_LAYER:
    {
      ShMngmt &cntrl = l.sh;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ShMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.cntrl[0].action = LSH_AENA;
      else
        cntrl.t.cntrl[0].action = LSH_ADISIMM;

      cntrl.t.cntrl[0].subAction = LSH_SAUSTA;
      cntrl.hdr.entId.ent = ENTSH;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STSHGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SH_LAYER);
      lmPst->event = EVTLSHCNTRLREQ;

      SmMiLshCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_SG_LAYER:
    {
      SgMngmt &cntrl = l.sg;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SgMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if(miSubOp == BP_AIN_SM_SUBTYPE_ENAALM)
        cntrl.t.hi.cntrl.action = AENA;
      else
        cntrl.t.hi.cntrl.action = ADISIMM;

      cntrl.t.hi.cntrl.subAction = SAUSTA;
      cntrl.hdr.entId.ent = ENTSG;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STSGGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SH_LAYER);
      lmPst->event = EVTLSGCNTRLREQ;

      SmMiLsgCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    default:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> :Invalid Layer passed <%d>", miLayer);
      return BP_AIN_SM_FAIL;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::setAlarms", miTransId);

  return BP_AIN_SM_OK;

}


/******************************************************************************
*
*     Fun:   setTrace()
*
*     Desc:  Set the Trace for a Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::setTrace (StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::setTrace ", 
    miTransId);

  int liMask=0;

  if(-1 == miIndex) {
    miIndex = 0;
  }

  switch (miLayer)
  {
#if 0
    case BP_AIN_SM_AIN_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      IeMngmt &cntrl = l.ie;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(IeMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if (aiTraceType == 1)
        cntrl.t.cntrl.action = AENA;
      else 
        cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SATRC;
      cntrl.hdr.entId.ent = ENTIE;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_AIN_LAYER);
      lmPst->event = LIE_EVTCNTRLREQ;
      
      smMiLieCntrlReq(lmPst, &cntrl);

      break;
    }
#endif
    case BP_AIN_SM_TCA_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      StMngmt &cntrl = l.st;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
      cntrl.t.cntrl.action = AENA;
      cntrl.t.cntrl.subAction = SATRC;
      cntrl.hdr.entId.ent = ENTST;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
      lmPst->event = EVTLSTCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
      lmPst->srcProcId = SFndProcId();

      smMiLstCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_SCC_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      SpMngmt &cntrl = l.sp;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      cntrl.t.cntrl.action = AENA;

      cntrl.t.cntrl.subAction = SATRC;
      cntrl.hdr.entId.ent = ENTSP;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STNSAP;
      cntrl.hdr.elmId.elmntInst1 = stackReq->req.u.trace.spNSapId;//Archana

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
      lmPst->event = EVTLSPCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLspCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      
      break;
    }
    case BP_AIN_SM_M3U_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      ItMgmt &cntrl = l.it;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = AENA;

      cntrl.t.cntrl.subAction = SATRC;

      //enable all the traces for now
      //cntrl.t.cntrl.t.trc.trcMask = LIT_TRC_ALL;
      cntrl.hdr.entId.ent = ENTIT;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STITGEN;
      //cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
  
      if(stackReq->req.u.trace.level == 1)
        liMask = LIT_TRC_SSNM;
      else if(stackReq->req.u.trace.level == 2)
        liMask = LIT_TRC_SSNM|LIT_TRC_ASPSM;
      else if(stackReq->req.u.trace.level == 3)
        liMask = LIT_TRC_SSNM|LIT_TRC_ASPSM|LIT_TRC_ASPTM;
      else if(stackReq->req.u.trace.level == 4)
        liMask = LIT_TRC_SSNM|LIT_TRC_ASPSM|LIT_TRC_ASPTM|LIT_TRC_M3UA_XFER;
      else if(stackReq->req.u.trace.level == 5)
        liMask = LIT_TRC_SSNM|LIT_TRC_ASPSM|LIT_TRC_ASPTM|LIT_TRC_M3UA_XFER|LIT_TRC_MGMT;
      else if(stackReq->req.u.trace.level == 6)
        liMask = LIT_TRC_SSNM|LIT_TRC_ASPSM|LIT_TRC_ASPTM|LIT_TRC_M3UA_XFER|LIT_TRC_MGMT|LIT_TRC_RKM;
      else if(stackReq->req.u.trace.level == 7)
        liMask = LIT_TRC_ALL;

      cntrl.t.cntrl.t.trc.trcMask = liMask;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
      lmPst->event = EVTLITCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
      lmPst->srcProcId = SFndProcId();
      logger.logMsg (TRACE_FLAG, 0,
        "setTrace: mask<%d>, dstproc<%d>, srcProc<%d>, action<%d>, subaction <%d>,  ",liMask, lmPst->dstProcId,lmPst->srcProcId,cntrl.t.cntrl.action , cntrl.t.cntrl.subAction );

      smMiLitCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_SCT_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      SbMgmt &cntrl = l.sb;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SbMgmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
      cntrl.t.cntrl.action = AENA;

      cntrl.t.cntrl.subAction = SATRC;
      cntrl.hdr.entId.ent = ENTSB;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STSBGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCT_LAYER);
      lmPst->event = LSB_EVTCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLsbCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_TUC_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      HiMngmt &cntrl = l.hi;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(HiMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
      cntrl.t.cntrl.action = AENA;

      cntrl.t.cntrl.ctlType.trcDat.trcLen = 272;
      cntrl.t.cntrl.ctlType.trcDat.sapId = 0;

      cntrl.t.cntrl.subAction = SATRC;
      cntrl.hdr.entId.ent = ENTHI;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STTSAP;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;


      Pst *lmPst = mpRep->getPst (BP_AIN_SM_TUC_LAYER);
      lmPst->event = EVTLHICNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLhiCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_MTP3_LAYER:
    {
      if(0 == miIndex) {
      }
      else if(miIndex < (mpRep->miNumberOfMTP3Dlsaps - 1)) {
        ++miIndex;
      }
      else {
        return BP_AIN_SM_INDEX_OVER;
      }

      SnMngmt &cntrl = l.sn;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
      cntrl.t.cntrl.action = AENA;
      cntrl.t.cntrl.subAction = SATRC;

      cntrl.hdr.entId.ent = ENTSN;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STDLSAP;
      cntrl.hdr.elmId.elmntInst1 = stackReq->req.u.trace.snDLSap;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
      lmPst->event = EVTLSNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLsnCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      if(0 == miIndex) {
        ++miIndex;
      }

      break;
    }
	case BP_AIN_SM_PSF_MTP3_LAYER:
    {
      ZnMngmt &cntrl = l.zn;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZnMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
#ifdef ZN_DFTHA
      cntrl.t.cntrl.t.trc.rsetId = ENTSN;
	  fillHdr(&(l.zn.hdr), miTransId, ENTSN, BP_AIN_SM_SRC_INST, TCNTRL, STZNRSET, NA );
#else
	  fillHdr(&(l.zn.hdr), miTransId, ENTSN, BP_AIN_SM_SRC_INST, TCNTRL, STZNGEN, NA );
#endif

      cntrl.t.cntrl.action = LZN_AENA;
      cntrl.t.cntrl.subAction = LZN_SATRC;

  

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_PSF_MTP3_LAYER);
      lmPst->event = EVTZNMILZNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();
      SmMiLznCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_PSF_TCAP_LAYER:
    {
      ZtMngmt &cntrl = l.zt;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZtMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

	  fillHdr(&(l.zt.hdr), miTransId, ENTST, BP_AIN_SM_SRC_INST, TCNTRL, STZTGEN, NA );
      cntrl.t.cntrl.action = LZT_AENA;
      cntrl.t.cntrl.subAction = LZT_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_PSF_TCAP_LAYER);
      lmPst->event = EVTZTMILZTCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLztCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_PSF_SCCP_LAYER:
    {
      ZpMngmt &cntrl = l.zp;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZpMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      fillHdr(&(l.zp.hdr), miTransId, ENTSP, BP_AIN_SM_SRC_INST, TCNTRL, STZPGEN, NA );

      cntrl.t.cntrl.action = LZP_AENA;
      cntrl.t.cntrl.subAction = LZP_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_PSF_SCCP_LAYER);
      lmPst->event = EVTZPMILZPCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLzpCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_PSF_M3UA_LAYER:
    {
      ZvMngmt &cntrl = l.zv;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZvMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

	  fillHdr(&(l.zv.hdr), miTransId, ENTIT, BP_AIN_SM_SRC_INST, TCNTRL, STZVGEN, NA );


      cntrl.t.cntrl.action = LZV_AENA;
      cntrl.t.cntrl.subAction = LZV_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_PSF_M3UA_LAYER);
      lmPst->event = EVTZVMILZVCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLzvCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_LDF_M3UA_LAYER:
    {
      LdvMngmt &cntrl = l.dv;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(LdvMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
      cntrl.t.cntrl.ctlType.trc.ifType = CMFTHA_IF_BOTH;
      fillHdr(&(l.dv.hdr), miTransId, ENTDV, BP_AIN_SM_SRC_INST, TCNTRL, STDVGEN, NA );

      cntrl.t.cntrl.action = LDV_AENA;
      cntrl.t.cntrl.subAction = LDV_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
      lmPst->event = EVTLDVCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLdvCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_LDF_MTP3_LAYER:
    {
      LdnMngmt &cntrl = l.dn;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(LdnMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
	  cntrl.t.cntrl.ctlType.trc.ifType = CMFTHA_IF_BOTH;
      /* set configuration parameters */
	  fillHdr(&(l.zn.hdr), miTransId, ENTDN, BP_AIN_SM_SRC_INST, TCNTRL, STDNGEN, NA );












      cntrl.t.cntrl.action = LDN_AENA;
      cntrl.t.cntrl.subAction = LDN_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
      lmPst->event = EVTLDNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLdnCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);












      break;
    }
    case BP_AIN_SM_SG_LAYER:
    {
      SgMngmt &cntrl = l.sg;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SgMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;

      /* set configuration parameters */
	  fillHdr(&(l.sg.hdr), miTransId, ENTSG, BP_AIN_SM_SRC_INST, TCNTRL, STSGGEN, NA );


      cntrl.t.hi.cntrl.action = AENA;
      cntrl.t.hi.cntrl.subAction = SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_SG_LAYER);
      //lmPst->event = EVTLDNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLsgCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);


      break;
    }
    case BP_AIN_SM_SH_LAYER:
    {
      ShMngmt &cntrl = l.sh;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ShMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;

      /* set configuration parameters */
	  fillHdr(&(l.sh.hdr), miTransId, ENTSH, BP_AIN_SM_SRC_INST, TCNTRL, STSHGEN, NA );

      cntrl.t.cntrl[0].action = LSH_AENA;
      cntrl.t.cntrl[0].subAction = LSH_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_SH_LAYER);
      //lmPst->event = EVTLDNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLshCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);


      break;
    }

    case BP_AIN_SM_MR_LAYER:
    {
      MrMngmt &cntrl = l.mr;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(MrMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;

      /* set configuration parameters */
	  fillHdr(&(l.mr.hdr), miTransId, ENTMR, BP_AIN_SM_SRC_INST, TCNTRL, STMRGEN, NA );
      cntrl.s.cntrl.action = LMR_AENA;
      cntrl.s.cntrl.subAction = LMR_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_MR_LAYER);
      //lmPst->event = EVTLDNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLmrCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);


      break;
    }
    default:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> :Invalid Layer passed <%d>", miLayer);
      return BP_AIN_SM_FAIL;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::setTrace", miTransId);

  return BP_AIN_SM_OK;

}



/******************************************************************************
*
*     Fun:   disableTrace()
*
*     Desc:  Set the Trace for a Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::disableTrace (StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::disableTrace ", 
    miTransId);

  if(-1 == miIndex) {
    miIndex = 0;
  }

  switch (miLayer)
  {
#if 0
    case BP_AIN_SM_AIN_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      IeMngmt &cntrl = l.ie;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(IeMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if (aiTraceType == 1)
        cntrl.t.cntrl.action = AENA;
      else 
        cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SATRC;
      cntrl.hdr.entId.ent = ENTIE;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_AIN_LAYER);
      lmPst->event = LIE_EVTCNTRLREQ;
      
      smMiLieCntrlReq(lmPst, &cntrl);

      break;
    }
#endif
    case BP_AIN_SM_TCA_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      StMngmt &cntrl = l.st;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;

      /* set configuration parameters */
      cntrl.t.cntrl.action = ADISIMM;
      cntrl.t.cntrl.subAction = SATRC;
      cntrl.hdr.entId.ent = ENTST;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
      lmPst->event = EVTLSTCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLstCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_SCC_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      SpMngmt &cntrl = l.sp;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SATRC;
      cntrl.hdr.entId.ent = ENTSP;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STNSAP;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
      lmPst->event = EVTLSPCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();
      smMiLspCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_M3U_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      ItMgmt &cntrl = l.it;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
      cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SATRC;

      //enable all the traces for now
      cntrl.t.cntrl.t.trc.trcMask = LIT_TRC_ALL;
      cntrl.hdr.entId.ent = ENTIT;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STITGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
      lmPst->event = EVTLITCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();
      smMiLitCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_SCT_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      SbMgmt &cntrl = l.sb;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SbMgmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
      cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SATRC;
      cntrl.hdr.entId.ent = ENTSB;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STSBGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCT_LAYER);
      lmPst->event = LSB_EVTCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLsbCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_TUC_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      HiMngmt &cntrl = l.hi;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(HiMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
      cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.ctlType.trcDat.trcLen = 272;
      cntrl.t.cntrl.ctlType.trcDat.sapId = 0;

      cntrl.t.cntrl.subAction = SATRC;
      cntrl.hdr.entId.ent = ENTHI;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STTSAP;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_TUC_LAYER);
      lmPst->event = EVTLHICNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLhiCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_MTP3_LAYER:
    {
      if(0 == miIndex) {
      }
      else if(miIndex < (mpRep->miNumberOfMTP3Dlsaps - 1)) {
        ++miIndex;
      }
      else {
        return BP_AIN_SM_INDEX_OVER;
      }

      SnMngmt &cntrl = l.sn;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
      cntrl.t.cntrl.action = ADISIMM;
      cntrl.t.cntrl.subAction = SATRC;

      cntrl.hdr.entId.ent = ENTSN;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STDLSAP;
      //cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmntInst1 = miIndex;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
      lmPst->event = EVTLSNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLsnCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      if(0 == miIndex) {
        ++miIndex;
      }

      break;
    }
	case BP_AIN_SM_PSF_MTP3_LAYER:
    {
      ZnMngmt &cntrl = l.zn;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZnMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
#ifdef ZN_DFTHA
      cntrl.t.cntrl.t.trc.rsetId = ENTSN;
	  fillHdr(&(l.zn.hdr), miTransId, ENTSN, BP_AIN_SM_SRC_INST, TCNTRL, STZNRSET, NA );
#else
	  fillHdr(&(l.zn.hdr), miTransId, ENTSN, BP_AIN_SM_SRC_INST, TCNTRL, STZNGEN, NA );
#endif

      cntrl.t.cntrl.action = LZN_ADISIMM;
      cntrl.t.cntrl.subAction = LZN_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_PSF_MTP3_LAYER);
      lmPst->event = EVTZNMILZNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();
      SmMiLznCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_PSF_TCAP_LAYER:
    {
      ZtMngmt &cntrl = l.zt;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZtMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

	  fillHdr(&(l.zt.hdr), miTransId, ENTST, BP_AIN_SM_SRC_INST, TCNTRL, STZTGEN, NA );
      cntrl.t.cntrl.action = LZT_ADISIMM;
      cntrl.t.cntrl.subAction = LZT_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_PSF_TCAP_LAYER);
      lmPst->event = EVTZTMILZTCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLztCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_PSF_SCCP_LAYER:
    {
      ZpMngmt &cntrl = l.zp;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZpMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      fillHdr(&(l.zp.hdr), miTransId, ENTSP, BP_AIN_SM_SRC_INST, TCNTRL, STZPGEN, NA );

      cntrl.t.cntrl.action = LZP_ADISIMM;
      cntrl.t.cntrl.subAction = LZP_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_PSF_SCCP_LAYER);
      lmPst->event = EVTZPMILZPCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLzpCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_PSF_M3UA_LAYER:
    {
      ZvMngmt &cntrl = l.zv;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZvMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

	  fillHdr(&(l.zv.hdr), miTransId, ENTIT, BP_AIN_SM_SRC_INST, TCNTRL, STZVGEN, NA );


      cntrl.t.cntrl.action = LZV_ADISIMM;
      cntrl.t.cntrl.subAction = LZV_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_PSF_M3UA_LAYER);
      lmPst->event = EVTZVMILZVCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLzvCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_LDF_M3UA_LAYER:
    {
      LdvMngmt &cntrl = l.dv;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(LdvMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
      cntrl.t.cntrl.ctlType.trc.ifType = CMFTHA_IF_BOTH;
      fillHdr(&(l.dv.hdr), miTransId, ENTDV, BP_AIN_SM_SRC_INST, TCNTRL, STDVGEN, NA );

      cntrl.t.cntrl.action = LDV_ADISIMM;
      cntrl.t.cntrl.subAction = LDV_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
      lmPst->event = EVTLDVCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLdvCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_LDF_MTP3_LAYER:
    {
      LdnMngmt &cntrl = l.dn;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(LdnMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
	  cntrl.t.cntrl.ctlType.trc.ifType = CMFTHA_IF_BOTH;
      /* set configuration parameters */
	  fillHdr(&(l.zn.hdr), miTransId, ENTDN, BP_AIN_SM_SRC_INST, TCNTRL, STDNGEN, NA );


      cntrl.t.cntrl.action = LDN_ADISIMM;
      cntrl.t.cntrl.subAction = LDN_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
      lmPst->event = EVTLDNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLdnCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_SG_LAYER:
    {
      SgMngmt &cntrl = l.sg;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SgMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;

      /* set configuration parameters */
	  fillHdr(&(l.sg.hdr), miTransId, ENTSG, BP_AIN_SM_SRC_INST, TCNTRL, STSGGEN, NA );


      cntrl.t.hi.cntrl.action = ADISIMM;
      cntrl.t.hi.cntrl.subAction = SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_SG_LAYER);
      //lmPst->event = EVTLDNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLsgCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);
      break;
    }
    case BP_AIN_SM_SH_LAYER:
    {
      ShMngmt &cntrl = l.sh;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ShMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;

      /* set configuration parameters */
	  fillHdr(&(l.sh.hdr), miTransId, ENTSH, BP_AIN_SM_SRC_INST, TCNTRL, STSHGEN, NA );

      cntrl.t.cntrl[0].action = LSH_ADISIMM;
      cntrl.t.cntrl[0].subAction = LSH_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_SH_LAYER);
      //lmPst->event = EVTLDNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLshCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);
      break;
    }

    case BP_AIN_SM_MR_LAYER:
    {
      MrMngmt &cntrl = l.mr;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(MrMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;

      /* set configuration parameters */
	  fillHdr(&(l.mr.hdr), miTransId, ENTMR, BP_AIN_SM_SRC_INST, TCNTRL, STMRGEN, NA );
      cntrl.s.cntrl.action = LMR_ADISIMM;
      cntrl.s.cntrl.subAction = LMR_SATRC;

      Pst* lmPst = mpRep->getPst (BP_AIN_SM_MR_LAYER);
      //lmPst->event = EVTLDNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLmrCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    default:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> :Invalid Layer passed <%d>", miLayer);
      return BP_AIN_SM_FAIL;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::disableTrace", miTransId);

  return BP_AIN_SM_OK;

}



/******************************************************************************
*
*     Fun:   setDebugPrint()
*
*     Desc:  Set the Debug Printing for a Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::setDebugPrint (StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Entering INGwSmCtlHdlr::setDebugPrint ",miTransId);



  switch (miLayer)
  {
#if 0
    case BP_AIN_SM_AIN_LAYER:
      {
        IeMngmt &cntrl = l.ie;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(IeMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */


        if (aiDebugType == 1)
          cntrl.t.cntrl.action = AENA;
        else 
          cntrl.t.cntrl.action = ADISIMM;

        cntrl.t.cntrl.subAction = SADBG;
        cntrl.hdr.entId.ent = ENTIE;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STGEN;

#ifdef DEBUGP
        cntrl.t.cntrl.s.dbg.dbgMask = (DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI);
#endif

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_AIN_LAYER);
        lmPst->event = LIE_EVTCNTRLREQ;

        smMiLieCntrlReq(lmPst, &cntrl);

        break;
      }
#endif
    case BP_AIN_SM_TCA_LAYER:
      {
        int liMask;
        StMngmt &cntrl = l.st;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = AENA;

        cntrl.t.cntrl.subAction = SADBG;
        cntrl.hdr.entId.ent = ENTST;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STGEN;


        if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_SI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = DBGMASK_SI|DBGMASK_MI;
        else if(stackReq->req.u.debug.level == 3)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;
        else if(stackReq->req.u.debug.level == 4)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;
        else if(stackReq->req.u.debug.level == 5)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI;
        else if(stackReq->req.u.debug.level == 6)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI;
        else if(stackReq->req.u.debug.level == 7)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI|DBGMASK_LYR;

        cntrl.t.cntrl.dbg.dbgMask = liMask;


        Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
        lmPst->event = EVTLSTCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();
        
        smMiLstCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);
        break;
      }
    case BP_AIN_SM_SCC_LAYER:
      {
        int liMask;
        SpMngmt &cntrl = l.sp;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = AENA;

        cntrl.t.cntrl.subAction = SADBG;
        cntrl.hdr.entId.ent = ENTSP;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STGEN;
        cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

        if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_SI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = DBGMASK_SI|DBGMASK_PLI;
        else if(stackReq->req.u.debug.level == 3)
          liMask = DBGMASK_SI|DBGMASK_PLI|DBGMASK_PI;
        else if(stackReq->req.u.debug.level == 4)
          liMask = DBGMASK_SI|DBGMASK_PLI|DBGMASK_PI|DBGMASK_MI;
        else if(stackReq->req.u.debug.level == 5)
          liMask = DBGMASK_SI|DBGMASK_PLI|DBGMASK_PI|DBGMASK_MI|DBGMASK_UI;
        else if(stackReq->req.u.debug.level == 6)
          liMask = DBGMASK_SI|DBGMASK_PLI|DBGMASK_PI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;
        else if(stackReq->req.u.debug.level == 7)
          liMask = DBGMASK_SI|DBGMASK_PLI|DBGMASK_PI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|SP_DBGMASK_INTERNAL;

        cntrl.t.cntrl.ctlType.spDbg.dbgMask = liMask;


        Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
        lmPst->event = EVTLSPCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        smMiLspCntrlReq(lmPst, &cntrl);
        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
    case BP_AIN_SM_M3U_LAYER:
      {
        int liMask;
        ItMgmt &cntrl = l.it;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = AENA;

        cntrl.t.cntrl.subAction = SADBG;
        cntrl.hdr.entId.ent = ENTIT;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STITGEN;
        cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

        if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_SI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = DBGMASK_SI|DBGMASK_MI;
        else if(stackReq->req.u.debug.level == 3)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;
        else if(stackReq->req.u.debug.level == 4)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;
        else
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_LYR;

        cntrl.t.cntrl.t.dbg.dbgMask = liMask;


        Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
        lmPst->event = EVTLITCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        smMiLitCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);
        break;
      }
    case BP_AIN_SM_SCT_LAYER:
      {
        int liMask;
        SbMgmt &cntrl = l.sb;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SbMgmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = AENA;

        cntrl.t.cntrl.subAction = SADBG;
        cntrl.hdr.entId.ent = ENTSB;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STSBGEN;
        cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

        if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_SI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = DBGMASK_SI|DBGMASK_MI;
        else if(stackReq->req.u.debug.level == 3)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;
        else
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;

        cntrl.t.cntrl.dbgMask = liMask;


        Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCT_LAYER);
        lmPst->event = LSB_EVTCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        smMiLsbCntrlReq(lmPst, &cntrl);
        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
    case BP_AIN_SM_TUC_LAYER:
      {
        int liMask;
        HiMngmt &cntrl = l.hi;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(HiMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = AENA;

        cntrl.t.cntrl.subAction = SADBG;
        cntrl.hdr.entId.ent = ENTHI;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STGEN;
        cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

        if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_SI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = DBGMASK_SI|DBGMASK_MI;
        else
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;

        cntrl.t.cntrl.ctlType.hiDbg.dbgMask = liMask;


        Pst *lmPst = mpRep->getPst (BP_AIN_SM_TUC_LAYER);
        lmPst->event = EVTLHICNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        smMiLhiCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
    case BP_AIN_SM_MTP3_LAYER:
      {
        int liMask;
        SnMngmt &cntrl = l.sn;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = AENA;
        cntrl.t.cntrl.subAction = SADBG;
        cntrl.hdr.entId.ent = ENTSN;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STGEN;
        cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

        if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_MI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = DBGMASK_MI|DBGMASK_LI;
        else
          liMask = DBGMASK_MI|DBGMASK_LI|DBGMASK_UI;

        cntrl.t.cntrl.ctlType.snDbg.dbgMask = liMask;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
        lmPst->event = EVTLITCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        smMiLsnCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
    case BP_AIN_SM_MTP2_LAYER:
      {
        int liAction, liMask;
        ProcId lsProcId = SFndProcId();

        lsProcId = stackReq->procId ;

        if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_SI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = DBGMASK_SI|DBGMASK_MI;
        else if(stackReq->req.u.debug.level == 3)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;
        else if(stackReq->req.u.debug.level == 4)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;
        else if(stackReq->req.u.debug.level == 5)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI;
        else if(stackReq->req.u.debug.level == 6)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI;
        else if(stackReq->req.u.debug.level == 7)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI|DBGMASK_LYR;



        liAction = AENA;


        int liRetVal = cfgSdDbg (liMask, liAction, 
            lsProcId, BP_AIN_SM_COUPLING,miTransId);
        if (liRetVal == RFAILED)
        {
          logger.logMsg (ERROR_FLAG, 0,
              "TID <%d>: cfgSdDbg failed for action<%d>",
              miTransId, liAction);
          return BP_AIN_SM_FAIL;
        }

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);
        break;
      }
    case BP_AIN_SM_PSF_MTP3_LAYER:
      {
        int liMask;
        ZnMngmt &cntrl = l.zn;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZnMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = LZN_AENA;

        cntrl.t.cntrl.subAction = LZN_SADBG;
        cntrl.hdr.entId.ent = ENTSN;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STZNGEN;

        if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_MI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = DBGMASK_MI|DBGMASK_PI;
        else if(stackReq->req.u.debug.level == 3)
          liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI;
        else if(stackReq->req.u.debug.level == 4)
          liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZN_DBGMASK_PACK;
        else if(stackReq->req.u.debug.level == 5)
          liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZN_DBGMASK_PACK|LZN_DBGMASK_UNPACK;
        else
          liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZN_DBGMASK_PACK|LZN_DBGMASK_UNPACK|LZN_DBGMASK_INTERNAL;

        cntrl.t.cntrl.t.dbg.mask = liMask;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_PSF_MTP3_LAYER);
        lmPst->event = EVTZVMILZVCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        SmMiLznCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
    case BP_AIN_SM_LDF_MTP3_LAYER:
      {
        int liMask;
        LdnMngmt &cntrl = l.dn;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(LdnMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = LDN_AENA;
        cntrl.t.cntrl.subAction = LDN_SADBG;
        cntrl.hdr.entId.ent = ENTDN;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STDNGEN;
      
        if(stackReq->req.u.debug.level == 1)
	        liMask = LDN_DBG_MI;
        else if(stackReq->req.u.debug.level == 2)
	        liMask = LDN_DBG_MI|LDN_DBG_LI;
        else if(stackReq->req.u.debug.level == 3)
	        liMask = LDN_DBG_MI|LDN_DBG_LI|LDN_DBG_UI;
        else if(stackReq->req.u.debug.level == 4)
	        liMask = LDN_DBG_MI|LDN_DBG_LI|LDN_DBG_UI|LDN_DBG_MR;
        else
	        liMask = LDN_DBG_MI|LDN_DBG_LI|LDN_DBG_UI|LDN_DBG_MR|LDN_DBG_ERR;

        cntrl.t.cntrl.ctlType.dbg.dbgMask = liMask;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
        lmPst->event = EVTLDNCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        SmMiLdnCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
    case BP_AIN_SM_PSF_M3UA_LAYER:
      {
        int liMask;
        ZvMngmt &cntrl = l.zv;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZvMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = LZV_AENA;
        cntrl.t.cntrl.subAction = LZV_SADBG;
        cntrl.hdr.entId.ent = ENTIT;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STZVGEN;

      if(stackReq->req.u.debug.level == 1)
        liMask = DBGMASK_MI;
      else if(stackReq->req.u.debug.level == 2)
        liMask = DBGMASK_MI|DBGMASK_PI;
      else if(stackReq->req.u.debug.level == 3)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI;
      else if(stackReq->req.u.debug.level == 4)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZV_DBGMASK_PACK;
      else if(stackReq->req.u.debug.level == 5)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZV_DBGMASK_PACK|LZV_DBGMASK_UNPACK;
      else
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZV_DBGMASK_PACK|LZV_DBGMASK_UNPACK|LZV_DBGMASK_INTERNAL;


        cntrl.t.cntrl.t.dbg.mask = liMask;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_PSF_M3UA_LAYER);
        lmPst->event = EVTZVMILZVCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        SmMiLzvCntrlReq(lmPst, &cntrl);
        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
    case BP_AIN_SM_LDF_M3UA_LAYER:
      {
        int liMask;
        LdvMngmt &cntrl = l.dv;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(LdvMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = LDV_AENA;
        cntrl.t.cntrl.subAction = LDV_SADBG;
        cntrl.hdr.entId.ent = ENTDV;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STDVGEN;

        if(stackReq->req.u.debug.level == 1)
          liMask = LDV_DBG_MI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = LDV_DBG_MI|LDV_DBG_LI;
        else if(stackReq->req.u.debug.level == 3)
          liMask = LDV_DBG_MI|LDV_DBG_LI|LDV_DBG_UI;
        else if(stackReq->req.u.debug.level == 4)
          liMask = LDV_DBG_MI|LDV_DBG_LI|LDV_DBG_UI|LDV_DBG_MR;
        else if(stackReq->req.u.debug.level == 5)
          liMask = LDV_DBG_MI|LDV_DBG_LI|LDV_DBG_UI|LDV_DBG_MR|LDV_DBG_ERR;

        cntrl.t.cntrl.ctlType.dbg.dbgMask = liMask;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
        lmPst->event = EVTLDVCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        SmMiLdvCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
    case BP_AIN_SM_PSF_SCCP_LAYER:
      {
        int liMask;
        ZpMngmt &cntrl = l.zp;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZpMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = LZP_AENA;
        cntrl.t.cntrl.subAction = LZP_SADBG;
        cntrl.hdr.entId.ent = ENTSP;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STZPGEN;

        if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_MI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = DBGMASK_MI|DBGMASK_PI;
        else if(stackReq->req.u.debug.level == 3)
          liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI;
        else
          liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZP_DBGMASK_INTERNAL;

        cntrl.t.cntrl.t.dbg.mask = liMask;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_PSF_SCCP_LAYER);
        lmPst->event = EVTZPMILZPCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        SmMiLzpCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
    case BP_AIN_SM_PSF_TCAP_LAYER:
      {
        int liMask;
        ZtMngmt &cntrl = l.zt;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZtMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = LZT_AENA;
       
        if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_MI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = DBGMASK_MI|DBGMASK_PI;
        else if(stackReq->req.u.debug.level == 3)
          liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI;
        else if(stackReq->req.u.debug.level == 4)
          liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZT_DBGMASK_PACK;
        else if(stackReq->req.u.debug.level == 5)
          liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZT_DBGMASK_PACK|LZT_DBGMASK_UNPACK;
        else
          liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZT_DBGMASK_PACK|LZT_DBGMASK_UNPACK|LZT_DBGMASK_INTERNAL;
 
        cntrl.t.cntrl.t.dbg.mask = liMask;

        cntrl.t.cntrl.subAction = LZT_SADBG;
        cntrl.hdr.entId.ent = ENTST;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STZTGEN;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_PSF_TCAP_LAYER);
        lmPst->event = EVTZTMILZTCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        SmMiLztCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
    case BP_AIN_SM_SG_LAYER:
      {
        int liMask;
        SgMngmt &cntrl = l.sg;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SgMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.apiType = LSG_HI_API;//HI_API
        cntrl.t.hi.cntrl.action = LSG_AENA;
        cntrl.t.hi.cntrl.subAction = SADBG;
        cntrl.hdr.entId.ent = ENTSG;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STSGGEN;

        if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_SI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = DBGMASK_SI|DBGMASK_MI;
        else if(stackReq->req.u.debug.level == 3)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;
        else if(stackReq->req.u.debug.level == 4)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;
        else if(stackReq->req.u.debug.level == 5)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI;
        else if(stackReq->req.u.debug.level == 6)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI;
        else if(stackReq->req.u.debug.level == 7)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI|DBGMASK_LYR;

        cntrl.t.hi.cntrl.u.dbg.dbgMask = liMask;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_SG_LAYER);
        lmPst->event = EVTLSGCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        SmMiLsgCntrlReq(lmPst, &cntrl);
        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
    case BP_AIN_SM_MR_LAYER:
      {
        int liMask;
        MrMngmt &cntrl = l.mr;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(MrMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.s.cntrl.action = LMR_AENA;
        cntrl.s.cntrl.subAction = LMR_SADBG;
        cntrl.hdr.entId.ent = ENTMR;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STMRGEN;

        if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_SI;
        else if(stackReq->req.u.debug.level == 2)
          liMask = DBGMASK_SI|DBGMASK_MI;
        else if(stackReq->req.u.debug.level == 3)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;
        else if(stackReq->req.u.debug.level == 4)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;
        else if(stackReq->req.u.debug.level == 5)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI;
        else if(stackReq->req.u.debug.level == 6)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI;
        else if(stackReq->req.u.debug.level == 7)
          liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI|DBGMASK_LYR;

        cntrl.s.cntrl.t.stkMgr.mrDbg.dbgMask = liMask;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_MR_LAYER);
        lmPst->event = EVTLMRCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        SmMiLmrCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
    case BP_AIN_SM_SH_LAYER:
      {
        int liMask;
        ShMngmt &cntrl = l.sh;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ShMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl[0].action = LSH_AENA;
        cntrl.t.cntrl[0].subAction = LSH_SADBG;
        cntrl.t.cntrl[1].action = ANOACT;
        cntrl.t.cntrl[1].subAction = 0;
        cntrl.hdr.entId.ent = ENTSH;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STSHGEN;

        if(stackReq->req.u.debug.level == 1)
          liMask = SHDBDSP;
        else if(stackReq->req.u.debug.level == 2)
          liMask = SHDBDSP|SHDBTRANS;
        else
          liMask = SHDBDSP|SHDBTRANS|SHDBFAIL;

        cntrl.t.cntrl[0].cntrlParams.stackMgr.shDbg.dbgMask = liMask; 

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_SH_LAYER);
        
        lmPst->event = EVTLSHCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        SmMiLshCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }

    case BP_AIN_SM_RY_LAYER:
      {
        int liMask;
        RyMngmt &cntrl = l.ry;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(RyMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = AENA;
        cntrl.t.cntrl.subAction = SADBG;
        cntrl.hdr.entId.ent = ENTRY;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STGEN;

        if(stackReq->req.u.debug.level == 1)
          liMask = RY_DBG_INFO;
        else if(stackReq->req.u.debug.level == 2)
          liMask = RY_DBG_INFO|RY_DBG_DEBUG;
        else if(stackReq->req.u.debug.level == 3)
          liMask = RY_DBG_INFO|RY_DBG_DEBUG|RY_DBG_ERROR;
        else
          liMask = RY_DBG_INFO|RY_DBG_DEBUG|RY_DBG_ERROR|RY_DBG_FATAL;

        cntrl.t.cntrl.dbgMask = liMask; 

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_RY_LAYER);
        lmPst->event = EVTLRYCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        SmMiLryCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }

    default:
      {
        logger.logMsg (TRACE_FLAG, 0,
            "TID <%d> :Invalid Layer passed <%d>", miTransId, miLayer);
        return BP_AIN_SM_FAIL;
      }
  }

  logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Leaving INGwSmCtlHdlr::setDebugPrint", miTransId);

  return BP_AIN_SM_OK;

}



#ifdef INC_DLG_AUDIT
/******************************************************************************
*
*     Fun:   ingwAuditMsg()
*
*     Desc: Audit the TCAP layer 
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::ingwAuditMsg (StackReqResp *stackReq)
{

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::ingwAuditMsg ",miTransId);

  StMngmt &cntrl = l.st;
  (Void) cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

  cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
  cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
  cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
  cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
  cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
  cntrl.hdr.transId = miTransId;

  cntrl.hdr.msgType = TCNTRL;
  /* set configuration parameters */

  //cntrl.t.cntrl.action = AAUDIT;
  cntrl.t.cntrl.action = 98;
  //cntrl.t.cntrl.subAction = UNUSED;
  cntrl.hdr.entId.ent = ENTST;
  cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
  cntrl.hdr.elmId.elmnt = STTCUSAP;
  cntrl.hdr.elmId.elmntInst1 = stackReq->req.u.audit.tcapSapId; //tcap sap id 

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
  lmPst->event = EVTLSTCNTRLREQ;
  lmPst->dstProcId = stackReq->procId;
  lmPst->srcProcId = SFndProcId();

  SmMiLstCntrlReq(lmPst, &cntrl);

  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);
 

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::ingwAuditMsg action:%d, elmntInst1:%d, elmnt:%d", miTransId,cntrl.t.cntrl.action,cntrl.hdr.elmId.elmntInst1,cntrl.hdr.elmId.elmnt );

  return BP_AIN_SM_OK;

}
#endif


/******************************************************************************
*
*     Fun:   disableDebugPrint()
*
*     Desc:  Set the Debug Printing for a Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::disableDebugPrint (StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::disableDebugPrint ",miTransId);

  switch (miLayer)
  {
#if 0
    case BP_AIN_SM_AIN_LAYER:
    {
      IeMngmt &cntrl = l.ie;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(IeMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */


      if (aiDebugType == 1)
        cntrl.t.cntrl.action = AENA;
      else 
        cntrl.t.cntrl.action = ADISIMM;

      cntrl.t.cntrl.subAction = SADBG;
      cntrl.hdr.entId.ent = ENTIE;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;

#ifdef DEBUGP
      cntrl.t.cntrl.s.dbg.dbgMask = (DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI);
#endif

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_AIN_LAYER);
      lmPst->event = LIE_EVTCNTRLREQ;
      
      smMiLieCntrlReq(lmPst, &cntrl);

      break;
    }
#endif
    case BP_AIN_SM_TCA_LAYER:
    {
	int liMask;
      StMngmt &cntrl = l.st;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = ADISIMM;
      cntrl.t.cntrl.subAction = SADBG;
      cntrl.hdr.entId.ent = ENTST;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;

		if(stackReq->req.u.debug.level == 1)
	liMask = DBGMASK_SI;
      else if(stackReq->req.u.debug.level == 2)
	liMask = DBGMASK_SI|DBGMASK_MI;
      else if(stackReq->req.u.debug.level == 3)
	liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;
      else if(stackReq->req.u.debug.level == 4)
	liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;
      else if(stackReq->req.u.debug.level == 5)
	liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI;
      else if(stackReq->req.u.debug.level == 6)
	liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI;
      else if(stackReq->req.u.debug.level == 7)
	liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI|DBGMASK_LYR;

      cntrl.t.cntrl.dbg.dbgMask = liMask;



      Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
      lmPst->event = EVTLSTCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLstCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);
      break;
    }
    case BP_AIN_SM_SCC_LAYER:
    {
	int liMask;
      SpMngmt &cntrl = l.sp;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = ADISIMM;
      cntrl.t.cntrl.subAction = SADBG;
      cntrl.hdr.entId.ent = ENTSP;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      if(stackReq->req.u.debug.level == 1)
        liMask = DBGMASK_SI;
      else if(stackReq->req.u.debug.level == 2)
        liMask = DBGMASK_SI|DBGMASK_PLI;
      else if(stackReq->req.u.debug.level == 3)
        liMask = DBGMASK_SI|DBGMASK_PLI|DBGMASK_PI;
      else if(stackReq->req.u.debug.level == 4)
        liMask = DBGMASK_SI|DBGMASK_PLI|DBGMASK_PI|DBGMASK_MI;
      else if(stackReq->req.u.debug.level == 5)
        liMask = DBGMASK_SI|DBGMASK_PLI|DBGMASK_PI|DBGMASK_MI|DBGMASK_UI;
      else if(stackReq->req.u.debug.level == 6)
        liMask = DBGMASK_SI|DBGMASK_PLI|DBGMASK_PI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;
      else if(stackReq->req.u.debug.level == 7)
        liMask = DBGMASK_SI|DBGMASK_PLI|DBGMASK_PI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|SP_DBGMASK_INTERNAL;

      cntrl.t.cntrl.ctlType.spDbg.dbgMask = liMask;



      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
      lmPst->event = EVTLSPCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLspCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_M3U_LAYER:
    {
	int liMask;
      ItMgmt &cntrl = l.it;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = ADISIMM;
      cntrl.t.cntrl.subAction = SADBG;
      cntrl.hdr.entId.ent = ENTIT;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STITGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      if(stackReq->req.u.debug.level == 1)
        liMask = DBGMASK_SI;
      else if(stackReq->req.u.debug.level == 2)
        liMask = DBGMASK_SI|DBGMASK_MI;
      else if(stackReq->req.u.debug.level == 3)
        liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;
      else if(stackReq->req.u.debug.level == 4)
        liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;
      else
        liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_LYR;

      cntrl.t.cntrl.t.dbg.dbgMask = liMask;



      Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
      lmPst->event = EVTLITCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLitCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);
      break;
    }
    case BP_AIN_SM_SCT_LAYER:
    {
	int liMask;
      SbMgmt &cntrl = l.sb;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SbMgmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = ADISIMM;
      cntrl.t.cntrl.subAction = SADBG;
      cntrl.hdr.entId.ent = ENTSB;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STSBGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

		if(stackReq->req.u.debug.level == 1)
	    liMask = DBGMASK_SI;
    else if(stackReq->req.u.debug.level == 2)
	    liMask = DBGMASK_SI|DBGMASK_MI;
    else if(stackReq->req.u.debug.level == 3)
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;
    else
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;

      cntrl.t.cntrl.dbgMask = liMask;



      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCT_LAYER);
      lmPst->event = LSB_EVTCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLsbCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_TUC_LAYER:
    {
	int liMask;
      HiMngmt &cntrl = l.hi;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(HiMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = ADISIMM;
      cntrl.t.cntrl.subAction = SADBG;
      cntrl.hdr.entId.ent = ENTHI;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

		if(stackReq->req.u.debug.level == 1)
	    liMask = DBGMASK_SI;
    else if(stackReq->req.u.debug.level == 2)
	    liMask = DBGMASK_SI|DBGMASK_MI;
    else
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;

      cntrl.t.cntrl.ctlType.hiDbg.dbgMask = liMask;



      Pst *lmPst = mpRep->getPst (BP_AIN_SM_TUC_LAYER);
      lmPst->event = EVTLHICNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      smMiLhiCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_MTP3_LAYER:
    {
	int liMask;
      SnMngmt &cntrl = l.sn;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));
      
      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = ADISIMM;
      cntrl.t.cntrl.subAction = SADBG;
      cntrl.hdr.entId.ent = ENTSN;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STGEN;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      if(stackReq->req.u.debug.level == 1)
        liMask = DBGMASK_MI;
      else if(stackReq->req.u.debug.level == 2)
        liMask = DBGMASK_MI|DBGMASK_LI;
      else
        liMask = DBGMASK_MI|DBGMASK_LI|DBGMASK_UI;

      cntrl.t.cntrl.ctlType.snDbg.dbgMask = liMask;



      Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
      lmPst->event = EVTLITCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
      lmPst->srcProcId = SFndProcId();

      smMiLsnCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_MTP2_LAYER:
    {
      int liAction, liMask;
      ProcId lsProcId = SFndProcId();

      if(stackReq->req.u.debug.level == 1)
	liMask = DBGMASK_SI;
      else if(stackReq->req.u.debug.level == 2)
	liMask = DBGMASK_SI|DBGMASK_MI;
      else if(stackReq->req.u.debug.level == 3)
	liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;
      else if(stackReq->req.u.debug.level == 4)
	liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;
      else if(stackReq->req.u.debug.level == 5)
	liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI;
      else if(stackReq->req.u.debug.level == 6)
	liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI;
      else if(stackReq->req.u.debug.level == 7)
	liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI|DBGMASK_LYR;


      liAction = ADISIMM;

      int liRetVal = cfgSdDbg (liMask, liAction, 
                              lsProcId, BP_AIN_SM_COUPLING,miTransId);
      if (liRetVal == RFAILED)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: cfgSdDbg failed for action<%d>",
          miTransId, liAction);
        return BP_AIN_SM_FAIL;
      }
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_PSF_MTP3_LAYER:
    {
      int liMask;
      ZnMngmt &cntrl = l.zn;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZnMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = LZN_ADISIMM;
      cntrl.t.cntrl.subAction = LZN_SADBG;
      cntrl.hdr.entId.ent = ENTSN;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STZNGEN;

      if(stackReq->req.u.debug.level == 1)
        liMask = DBGMASK_MI;
      else if(stackReq->req.u.debug.level == 2)
        liMask = DBGMASK_MI|DBGMASK_PI;
      else if(stackReq->req.u.debug.level == 3)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI;
      else if(stackReq->req.u.debug.level == 4)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZN_DBGMASK_PACK;
      else if(stackReq->req.u.debug.level == 5)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZN_DBGMASK_PACK|LZN_DBGMASK_UNPACK;
      else
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZN_DBGMASK_PACK|LZN_DBGMASK_UNPACK|LZN_DBGMASK_INTERNAL;



      cntrl.t.cntrl.t.dbg.mask = liMask;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_PSF_MTP3_LAYER);
      lmPst->event = EVTZVMILZVCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLznCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_LDF_MTP3_LAYER:
    {
      int liMask;
      LdnMngmt &cntrl = l.dn;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(LdnMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = LDN_ADISIMM;
      cntrl.t.cntrl.subAction = LDN_SADBG;
      cntrl.hdr.entId.ent = ENTDN;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STDNGEN;

      if(stackReq->req.u.debug.level == 1)
	      liMask = LDN_DBG_MI;
      else if(stackReq->req.u.debug.level == 2)
	      liMask = LDN_DBG_MI|LDN_DBG_LI;
      else if(stackReq->req.u.debug.level == 3)
	      liMask = LDN_DBG_MI|LDN_DBG_LI|LDN_DBG_UI;
      else if(stackReq->req.u.debug.level == 4)
	      liMask = LDN_DBG_MI|LDN_DBG_LI|LDN_DBG_UI|LDN_DBG_MR;
      else
	      liMask = LDN_DBG_MI|LDN_DBG_LI|LDN_DBG_UI|LDN_DBG_MR|LDN_DBG_ERR;


      cntrl.t.cntrl.ctlType.dbg.dbgMask = liMask;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
      lmPst->event = EVTLDNCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
      lmPst->srcProcId = SFndProcId();

      SmMiLdnCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_PSF_M3UA_LAYER:
    {
      int liMask;
      ZvMngmt &cntrl = l.zv;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZvMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = LZV_ADISIMM;
      cntrl.t.cntrl.subAction = LZV_SADBG;
      cntrl.hdr.entId.ent = ENTIT;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STZVGEN;

      if(stackReq->req.u.debug.level == 1)
        liMask = DBGMASK_MI;
      else if(stackReq->req.u.debug.level == 2)
        liMask = DBGMASK_MI|DBGMASK_PI;
      else if(stackReq->req.u.debug.level == 3)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI;
      else if(stackReq->req.u.debug.level == 4)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZV_DBGMASK_PACK;
      else if(stackReq->req.u.debug.level == 5)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZV_DBGMASK_PACK|LZV_DBGMASK_UNPACK;
      else
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZV_DBGMASK_PACK|LZV_DBGMASK_UNPACK|LZV_DBGMASK_INTERNAL;


      cntrl.t.cntrl.t.dbg.mask = liMask;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_PSF_M3UA_LAYER);
      lmPst->event = EVTZVMILZVCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLzvCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_LDF_M3UA_LAYER:
    {
      int liMask;
      LdvMngmt &cntrl = l.dv;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(LdvMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = LDV_ADISIMM;
      cntrl.t.cntrl.subAction = LDV_SADBG;
      cntrl.hdr.entId.ent = ENTDV;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STDVGEN;

      if(stackReq->req.u.debug.level == 1)
        liMask = LDV_DBG_MI;
      else if(stackReq->req.u.debug.level == 2)
        liMask = LDV_DBG_MI|LDV_DBG_LI;
      else if(stackReq->req.u.debug.level == 3)
        liMask = LDV_DBG_MI|LDV_DBG_LI|LDV_DBG_UI;
      else if(stackReq->req.u.debug.level == 4)
        liMask = LDV_DBG_MI|LDV_DBG_LI|LDV_DBG_UI|LDV_DBG_MR;
      else
        liMask = LDV_DBG_MI|LDV_DBG_LI|LDV_DBG_UI|LDV_DBG_MR|LDV_DBG_ERR;



      cntrl.t.cntrl.ctlType.dbg.dbgMask = liMask;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
      lmPst->event = EVTLDVCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLdvCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_PSF_SCCP_LAYER:
    {
      int liMask;
      ZpMngmt &cntrl = l.zp;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZpMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = LZP_ADISIMM;
      cntrl.t.cntrl.subAction = LZP_SADBG;
      cntrl.hdr.entId.ent = ENTSP;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STZPGEN;

      if(stackReq->req.u.debug.level == 1)
        liMask = DBGMASK_MI;
      else if(stackReq->req.u.debug.level == 2)
        liMask = DBGMASK_MI|DBGMASK_PI;
      else if(stackReq->req.u.debug.level == 3)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI;
      else
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZP_DBGMASK_INTERNAL;

      cntrl.t.cntrl.t.dbg.mask = liMask;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_PSF_SCCP_LAYER);
      lmPst->event = EVTZPMILZPCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
      lmPst->srcProcId = SFndProcId();

      SmMiLzpCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_PSF_TCAP_LAYER:
    {

      int liMask;
      ZtMngmt &cntrl = l.zt;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ZtMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl.action = LZT_ADISIMM;

      if(stackReq->req.u.debug.level == 1)
          liMask = DBGMASK_MI;
      else if(stackReq->req.u.debug.level == 2)
        liMask = DBGMASK_MI|DBGMASK_PI;
      else if(stackReq->req.u.debug.level == 3)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI;
      else if(stackReq->req.u.debug.level == 4)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZT_DBGMASK_PACK;
      else if(stackReq->req.u.debug.level == 5)
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZT_DBGMASK_PACK|LZT_DBGMASK_UNPACK;
      else
        liMask = DBGMASK_MI|DBGMASK_PI|DBGMASK_PLI|LZT_DBGMASK_PACK|LZT_DBGMASK_UNPACK|LZT_DBGMASK_INTERNAL;

      cntrl.t.cntrl.t.dbg.mask = liMask;

      cntrl.t.cntrl.subAction = LZT_SADBG;
      cntrl.hdr.entId.ent = ENTST;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STZTGEN;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_PSF_TCAP_LAYER);
      lmPst->event = EVTZTMILZTCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLztCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_SG_LAYER:
    {
      int liMask;
      SgMngmt &cntrl = l.sg;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SgMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.apiType = LSG_HI_API;//HI_API
      cntrl.t.hi.cntrl.action = LSG_ADISIMM;
      cntrl.t.hi.cntrl.subAction = SADBG;
      cntrl.hdr.entId.ent = ENTSG;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STSGGEN;

      if(stackReq->req.u.debug.level == 1)
	    liMask = DBGMASK_SI;
    else if(stackReq->req.u.debug.level == 2)
	    liMask = DBGMASK_SI|DBGMASK_MI;
    else if(stackReq->req.u.debug.level == 3)
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;
    else if(stackReq->req.u.debug.level == 4)
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;
    else if(stackReq->req.u.debug.level == 5)
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI;
    else if(stackReq->req.u.debug.level == 6)
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI;
    else if(stackReq->req.u.debug.level == 7)
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI|DBGMASK_LYR;

      cntrl.t.hi.cntrl.u.dbg.dbgMask = liMask;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SG_LAYER);
      lmPst->event = EVTLSGCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLsgCntrlReq(lmPst, &cntrl);
      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_MR_LAYER:
    {
      int liMask;
      MrMngmt &cntrl = l.mr;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(MrMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.s.cntrl.action = LMR_ADISIMM;
      cntrl.s.cntrl.subAction = LMR_SADBG;
      cntrl.hdr.entId.ent = ENTMR;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STMRGEN;

      if(stackReq->req.u.debug.level == 1)
	    liMask = DBGMASK_SI;
    else if(stackReq->req.u.debug.level == 2)
	    liMask = DBGMASK_SI|DBGMASK_MI;
    else if(stackReq->req.u.debug.level == 3)
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI;
    else if(stackReq->req.u.debug.level == 4)
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI;
    else if(stackReq->req.u.debug.level == 5)
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI;
    else if(stackReq->req.u.debug.level == 6)
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI;
    else if(stackReq->req.u.debug.level == 7)
	    liMask = DBGMASK_SI|DBGMASK_MI|DBGMASK_UI|DBGMASK_LI|DBGMASK_PI|DBGMASK_PLI|DBGMASK_LYR;

      cntrl.s.cntrl.t.stkMgr.mrDbg.dbgMask = liMask;


      Pst *lmPst = mpRep->getPst (BP_AIN_SM_MR_LAYER);
      lmPst->event = EVTLMRCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLmrCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
	case BP_AIN_SM_SH_LAYER:
    {
      int liMask;
      ShMngmt &cntrl = l.sh;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ShMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      cntrl.t.cntrl[0].action = LSH_ADISIMM;
      cntrl.t.cntrl[0].subAction = LSH_SADBG;
      cntrl.hdr.entId.ent = ENTSH;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STSHGEN;

      if(stackReq->req.u.debug.level == 1)
        liMask = SHDBDSP;
      else if(stackReq->req.u.debug.level == 2)
        liMask = SHDBDSP|SHDBTRANS;
      else
        liMask = SHDBDSP|SHDBTRANS|SHDBFAIL;

      cntrl.t.cntrl[0].cntrlParams.stackMgr.shDbg.dbgMask = liMask;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SH_LAYER);
      lmPst->event = EVTLSHCNTRLREQ;
      lmPst->dstProcId = stackReq->procId;
    lmPst->srcProcId = SFndProcId();

      SmMiLshCntrlReq(lmPst, &cntrl);

      stackReq->resp.procId = stackReq->procId;
      stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;

      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_RY_LAYER:
      {
        int liMask;
        RyMngmt &cntrl = l.ry;
        (Void) cmMemset((U8 *)&cntrl, 0, sizeof(RyMngmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */

        cntrl.t.cntrl.action = AENA;
        cntrl.t.cntrl.subAction = SADBG;
        cntrl.hdr.entId.ent = ENTRY;
        cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
        cntrl.hdr.elmId.elmnt = STGEN;

        cntrl.t.cntrl.dbgMask = 0; 

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_RY_LAYER);
        lmPst->event = EVTLRYCNTRLREQ;
        lmPst->dstProcId = stackReq->procId;
        lmPst->srcProcId = SFndProcId();

        SmMiLryCntrlReq(lmPst, &cntrl);

        stackReq->resp.procId = stackReq->procId;
        stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;

        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }

    default:
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> :Invalid Layer passed <%d>", miTransId, miLayer);
      return BP_AIN_SM_FAIL;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::disableDebugPrint", miTransId);

  return BP_AIN_SM_OK;

}




/******************************************************************************
*
*     Fun:   shutdown()
*
*     Desc:  Shutdown the layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::shutdown (int aiStackLayerId )
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::shutdown", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::shutdown", miTransId);

  return BP_AIN_SM_OK;

}

/******************************************************************************
*
*     Fun:   m3uaOpenEp()
*
*     Desc:  Open an endpoint in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaOpenEp (StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaOpenEp", miTransId);

  if (miLayer != BP_AIN_SM_M3U_LAYER)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :This operation is only permitted for M3UA Layer", miTransId);
    return BP_AIN_SM_FAIL;
  }

  ItMgmt &cntrl = l.it;
  (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

  cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
  cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
  cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
  cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
  cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
  cntrl.hdr.transId = miTransId;

  cntrl.hdr.msgType = TCNTRL;
  /* set configuration parameters */
  cntrl.hdr.elmId.elmnt = STITSCTSAP;
  cntrl.t.cntrl.subAction = SAELMNT;
  cntrl.t.cntrl.s.suId = stackReq->req.u.openEp.suId;
  cntrl.t.cntrl.action = AEOPENR;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
  lmPst->event = EVTLITCNTRLREQ;
  lmPst->srcProcId = SFndProcId();
  lmPst->dstProcId = stackReq->procId;

  printf("[+INC+] %s:%d m3uaOpenEp():: Open Endpoint Invoking smMiLitCntrlReq with msgType %d elmnt %d subAction %d suId %d action %d srcProcId %d destProcId %d\n", __FILE__, __LINE__, cntrl.hdr.msgType, cntrl.hdr.elmId.elmnt, cntrl.t.cntrl.subAction, cntrl.t.cntrl.s.suId, cntrl.t.cntrl.action, lmPst->srcProcId, lmPst->dstProcId);
  logger.logMsg (ALWAYS_FLAG, 0,
         "m3uaOpenEp():: Open Endpoint Invoking smMiLitCntrlReq with msgType %d elmnt %d subAction %d suId %d action %d srcProcId %d destProcId %d", cntrl.hdr.msgType, cntrl.hdr.elmId.elmnt, cntrl.t.cntrl.subAction, cntrl.t.cntrl.s.suId, cntrl.t.cntrl.action, lmPst->srcProcId, lmPst->dstProcId);

  smMiLitCntrlReq(lmPst, &cntrl);

  /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;
    
  mrDist.updateRspStruct(miTransId,stackReq);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaOpenEp", miTransId);

  return BP_AIN_SM_OK;

}



/******************************************************************************
*
*     Fun:   disableUsap()
*
*     Desc:  DISABLE TCAP upper sap
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::disableUsap(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::disableUsap", miTransId);


  StMngmt &cntrl = l.st;
  (Void) cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

  cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
  cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
  cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
  cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
  cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
  cntrl.hdr.transId = miTransId;

  cntrl.hdr.msgType = TCNTRL;
  cntrl.hdr.elmId.elmnt = STTCUSAP;
  cntrl.hdr.elmId.elmntInst1 = stackReq->req.u.disableSap.tcapUsapId;

  cntrl.t.cntrl.action = AUBND_DIS;
  cntrl.t.cntrl.subAction = 0;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
  lmPst->event = EVTSPTUBNDREQ;
  lmPst->srcProcId = SFndProcId();
  lmPst->dstProcId = SFndProcId();


  smMiLstCntrlReq(lmPst, &cntrl);

  /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;
    
  mrDist.updateRspStruct(miTransId,stackReq);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::disableUsap", miTransId);

  return BP_AIN_SM_OK;

}



/******************************************************************************
*
*     Fun:   disableLsap()
*
*     Desc:  DISABLE TCAP Lower sap
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::disableLsap(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::disableLsap", miTransId);


  StMngmt &cntrl = l.st;
  (Void) cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

  cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
  cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
  cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
  cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
  cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
  cntrl.hdr.transId = miTransId;

  cntrl.hdr.msgType = TCNTRL;
  cntrl.hdr.elmId.elmnt = STSPSAP;
  cntrl.hdr.elmId.elmntInst1 = stackReq->req.u.disableSap.tcapLsapId;

  cntrl.t.cntrl.action = AUBND_DIS;
  cntrl.t.cntrl.subAction = 0;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
  lmPst->event = EVTSPTUBNDREQ;
  lmPst->srcProcId = SFndProcId();
  lmPst->dstProcId = SFndProcId();


  smMiLstCntrlReq(lmPst, &cntrl);

  /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  if(stackReq->txnType != ROLLBACK_TXN) 
    stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;
    
  mrDist.updateRspStruct(miTransId,stackReq);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::disableLsap", miTransId);

  return BP_AIN_SM_OK;

}


/******************************************************************************
*
*     Fun:   updateSAP()
*
*     Desc:  Enable/Disable SAPs for a layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::updateSAP (StackReqResp *stackReq, int aiSapOper, int aiSapType)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::updateSAP, Layer <%d>, Operation <%d>",
    miTransId, miLayer, aiSapOper);

  if(-1 == miIndex) {
    miIndex = 0;
  }

  switch (miLayer)
  {
#if 0
    case BP_AIN_SM_IU_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      //initialize the post structure
      Pst lmPst;
      lmPst.selector  = BP_AIN_SM_COUPLING;        /* loosely coupled */
      lmPst.region    = BP_AIN_SM_REGION;        /* region */
      lmPst.pool      = BP_AIN_SM_POOL;       /* pool */
      lmPst.prior     = BP_AIN_SM_PRIOR;        /* priority */
      lmPst.route     = BP_AIN_SM_ROUTE;       /* route */
      lmPst.dstProcId = SFndProcId();  /* dst proc id */
      lmPst.dstEnt    = ENTIE;         /* dst entity */
      lmPst.dstInst   = BP_AIN_SM_DEST_INST;     /* dst inst */
      lmPst.srcProcId = SFndProcId();  /* src proc id */
      lmPst.srcEnt    = ENTIU;         /* src entity */
      lmPst.srcInst   = BP_AIN_SM_SRC_INST;     /* src inst */

      iuLiIetBndReq (&lmPst, 0, 0);

      return BP_AIN_SM_SEND_NEXT;

      break;
    }
#endif
#if 0
    case BP_AIN_SM_AIN_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      IeMngmt &cntrl = l.ie;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(IeMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      if (aiSapOper == BP_AIN_SM_SUBTYPE_ENASAP)
        cntrl.t.cntrl.action = ABND_ENA;
      else
        cntrl.t.cntrl.action = AUBND_DIS;

      cntrl.t.cntrl.subAction = 0;
      cntrl.hdr.entId.ent = ENTIE;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STTCSAP;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_AIN_LAYER);
      lmPst->event = LIE_EVTCNTRLREQ;

      smMiLieCntrlReq(lmPst, &cntrl);

      break;
    }
#endif
    case BP_AIN_SM_TCA_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      StMngmt &cntrl = l.st;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(StMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;
 
      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */

      if (aiSapOper == BP_AIN_SM_SUBTYPE_ENASAP)
        cntrl.t.cntrl.action = ABND_ENA;
      else
        cntrl.t.cntrl.action = AUBND_DIS;

      cntrl.t.cntrl.subAction = 0;
      cntrl.hdr.entId.ent = ENTST;
      cntrl.hdr.entId.inst = BP_AIN_SM_SRC_INST;
      cntrl.hdr.elmId.elmnt = STSPSAP;
      cntrl.hdr.elmId.elmntInst1 = BP_AIN_SM_SRC_INST;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_TCA_LAYER);
      lmPst->event = EVTLSTCNTRLREQ;

      smMiLstCntrlReq(lmPst, &cntrl);

      break;
    }
    case BP_AIN_SM_SCC_LAYER:
    {
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      SpMngmt &cntrl = l.sp;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;
  
      cntrl.hdr.msgType = TCNTRL;  
      cntrl.hdr.entId.inst = 0; 
      cntrl.hdr.entId.ent = ENTST; 
      /* set configuration parameters */
      cntrl.hdr.elmId.elmnt = STTSAP; 
      cntrl.hdr.elmId.elmntInst1 = stackReq->req.u.disableSap.sccpUsapId; 
      cntrl.t.cntrl.subAction = 0;

      if (aiSapOper == BP_AIN_SM_SUBTYPE_ENASAP)
        cntrl.t.cntrl.action = ABND_ENA;
      else
        cntrl.t.cntrl.action = AUBND_DIS;

      Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
      lmPst->event = EVTLSPCNTRLREQ;
      lmPst->srcProcId = SFndProcId();
      lmPst->dstProcId = SFndProcId();
    
      smMiLspCntrlReq(lmPst, &cntrl);
       /* update the response structure */
      stackReq->resp.procId = stackReq->procId;
      if(stackReq->txnType != ROLLBACK_TXN) 
        stackReq->txnType = NORMAL_TXN;
      stackReq->txnStatus = INPROGRESS;
    
      mrDist.updateRspStruct(miTransId,stackReq);

      break;
    }
    case BP_AIN_SM_M3U_LAYER:
    {
      //logger.logMsg (TRACE_FLAG, 0, "updateSAP with subopr:<%d>",stackReq->subOpr ); 
      if(0 != miIndex) {
        return BP_AIN_SM_INDEX_OVER;
      }
      ++miIndex;

      ItMgmt &cntrl = l.it;
      (Void) cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

      cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
      cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
      cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
      cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
      cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
      cntrl.hdr.transId = miTransId;

      cntrl.hdr.msgType = TCNTRL;
      /* set configuration parameters */
      if(stackReq->subOpr == BP_AIN_SM_IT_SCTSAP)
        cntrl.hdr.elmId.elmnt = STITSCTSAP;
      else
        cntrl.hdr.elmId.elmnt = STITNSAP;


      if (aiSapOper == BP_AIN_SM_SUBTYPE_ENASAP){
        cntrl.t.cntrl.action = ABND;
        cntrl.t.cntrl.s.suId = stackReq->req.u.bindSap.sapId;
      }
      else{
        cntrl.t.cntrl.action = AUBND;
        cntrl.hdr.elmId.elmnt = STITSCTSAP;
        cntrl.t.cntrl.s.suId = stackReq->req.u.delEp.m3uaLsapId;
      }

      cntrl.t.cntrl.subAction = SAELMNT;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
        lmPst->event = EVTLITCNTRLREQ;
        lmPst->srcProcId = SFndProcId();
        lmPst->dstProcId = stackReq->procId;

        smMiLitCntrlReq(lmPst, &cntrl);
        /* update the response structure */
        stackReq->resp.procId = stackReq->procId;
        if(stackReq->txnType != ROLLBACK_TXN) 
          stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;
      
        mrDist.updateRspStruct(miTransId,stackReq);

        break;
      }
      case BP_AIN_SM_SCT_LAYER:
      {
        if(0 != miIndex) {
          return BP_AIN_SM_INDEX_OVER;
        }
        ++miIndex;

        SbMgmt &cntrl = l.sb;
        cmMemset((U8 *)&cntrl, 0, sizeof(SbMgmt));

        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
        cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.transId = miTransId;

        cntrl.hdr.msgType = TCNTRL;
        /* set configuration parameters */
        cntrl.hdr.elmId.elmnt = STSBTSAP;

        if (aiSapOper == BP_AIN_SM_SUBTYPE_ENASAP){
          cntrl.t.cntrl.action = ABND_ENA;
          cntrl.t.cntrl.sapId = stackReq->req.u.bindSap.sapId;
          logger.logMsg (TRACE_FLAG, 0, "BndSap at sctp:<%d>", cntrl.t.cntrl.sapId); 
        }
        else{
          cntrl.t.cntrl.action = AUBND_DIS;
          cntrl.t.cntrl.sapId = stackReq->req.u.delEp.m3uaLsapId;
          logger.logMsg (TRACE_FLAG, 0, "UnBndSap at sctp:<%d>", cntrl.t.cntrl.sapId); 
        }

        cntrl.t.cntrl.subAction = SAELMNT;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCT_LAYER);
        lmPst->event = LSB_EVTCNTRLREQ;
        lmPst->srcProcId = SFndProcId();
        lmPst->dstProcId = stackReq->procId;

        smMiLsbCntrlReq(lmPst, &cntrl);
        /* update the response structure */
        stackReq->resp.procId = stackReq->procId;
        if(stackReq->txnType != ROLLBACK_TXN) 
          stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;
      
        mrDist.updateRspStruct(miTransId,stackReq);


        break;
      }
      case BP_AIN_SM_MTP3_LAYER:
      {
        logger.logMsg (TRACE_FLAG, 0, "MIINDEX:<%d>", miIndex); 

        if(0 == miIndex) {
        }
        //else if(miIndex <= (mpRep->miNumberOfMTP3Dlsaps - 1)) {
        else if(miIndex <= 1) {
        }
        else {
          logger.logMsg (TRACE_FLAG, 0,
             "Leaving INGwSmCtlHdlr::updateSAP");
          return BP_AIN_SM_INDEX_OVER;
        }

        SnMngmt cntrl;
        cmMemset((U8 *)&cntrl, '\0', sizeof(SnMngmt));
    
        cntrl.hdr.msgType = TCNTRL;
        cntrl.hdr.entId.ent = ENTSN;
        cntrl.hdr.entId.inst = 0;     
        cntrl.hdr.transId = miTransId;
        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.mem.region = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;

        cntrl.hdr.elmId.elmnt = STNSAP;
        //cntrl.hdr.elmId.elmnt = STDLSAP;
        //cntrl.hdr.elmId.elmntInst1 = 0;
        cntrl.hdr.elmId.elmntInst1 = stackReq->req.u.disableUserPart.mtp3UsapId;
        //cntrl.hdr.elmId.elmntInst2 = 0xff;

        if (aiSapOper == BP_AIN_SM_SUBTYPE_ENASAP) {
          cntrl.t.cntrl.action = ABND_ENA;
        }
        else {
          cntrl.t.cntrl.action = AUBND_DIS;
        }

        cntrl.t.cntrl.subAction = SAELMNT;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
        lmPst->event = EVTLSNCNTRLREQ;

        lmPst->srcProcId = SFndProcId();
        lmPst->dstProcId = SFndProcId();

        smMiLsnCntrlReq(lmPst, &cntrl);


       /* update the response structure */
        stackReq->resp.procId = stackReq->procId;
        if(stackReq->txnType != ROLLBACK_TXN) 
          stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;
     
        mrDist.updateRspStruct(miTransId,stackReq);

        ++miIndex;

        break;
      }
      case BP_AIN_SM_TUC_LAYER:
      {
        logger.logMsg (TRACE_FLAG, 0, "MIINDEX:<%d>", miIndex); 

        if(0 == miIndex) {
        }
        else if(miIndex <= 1) {
        }
        else {
          logger.logMsg (TRACE_FLAG, 0,
             "Leaving INGwSmCtlHdlr::updateSAP");
          return BP_AIN_SM_INDEX_OVER;
        }

        HiMngmt cntrl;
        cmMemset((U8 *)&cntrl, '\0', sizeof(HiMngmt));
    
        cntrl.hdr.msgType = TCNTRL;
        cntrl.hdr.entId.ent = ENTHI;
        cntrl.hdr.entId.inst = 0;     
        cntrl.hdr.transId = miTransId;
        cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
        cntrl.hdr.response.mem.region = BP_AIN_SM_REGION;
        cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
        cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
        cntrl.hdr.response.route = BP_AIN_SM_ROUTE;

        cntrl.hdr.elmId.elmnt = STTSAP;
        // to disable
        //cntrl.t.cntrl.action = ADISIMM;
        cntrl.t.cntrl.action = AUBND_DIS;
        cntrl.t.cntrl.subAction = SAELMNT;

        cntrl.t.cntrl.ctlType.sapId = stackReq->req.u.disableUserPart.mtp3UsapId;

        Pst *lmPst = mpRep->getPst (BP_AIN_SM_TUC_LAYER);
        lmPst->event = EVTLHICNTRLREQ;

        lmPst->srcProcId = SFndProcId();
        lmPst->dstProcId = SFndProcId();

        smMiLhiCntrlReq(lmPst, &cntrl);


       /* update the response structure */
        stackReq->resp.procId = stackReq->procId;
        if(stackReq->txnType != ROLLBACK_TXN) 
          stackReq->txnType = NORMAL_TXN;
        stackReq->txnStatus = INPROGRESS;
     
        mrDist.updateRspStruct(miTransId,stackReq);

        ++miIndex;

        break;
      }
    }

    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Leaving INGwSmCtlHdlr::updateSAP", miTransId);

    return BP_AIN_SM_OK;

  }


  /* 
   * TCAP Layer Operations
   */

#ifdef INC_DLG_AUDIT
  // Audit Unused Dialogs
  /******************************************************************************
  *
  *     Fun:   tcapAuditDialogs()
  *
  *     Desc:  Audit Unused DIalogs for TCAP
  *
  *     Notes: None
  *
  *     File:  INGwSmCtlHdlr.C
  *
  *******************************************************************************/
  int
  INGwSmCtlHdlr::tcapAuditDialogs ()
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Entering INGwSmCtlHdlr::tcapAuditDialogs", miTransId);

    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :INGwSmCtlHdlr::tcapAuditDialogs Not implemented in this release", miTransId);

    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Leaving INGwSmCtlHdlr::tcapAuditDialogs", miTransId);

    return BP_AIN_SM_FAIL;

  }


  /******************************************************************************
  *
  *     Fun:   tcapAuditInvokes()
  *
  *     Desc:  Audit unused Invokes for TCAP
  *
  *     Notes: None
  *
  *     File:  INGwSmCtlHdlr.C
  *
  *******************************************************************************/
  int
  INGwSmCtlHdlr::tcapAuditInvokes ()
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Entering INGwSmCtlHdlr::tcapAuditInvokes", miTransId);

    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :INGwSmCtlHdlr::tcapAuditInvokes Not implemented in this release", miTransId);

    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Leaving INGwSmCtlHdlr::tcapAuditInvokes", miTransId);

    return BP_AIN_SM_FAIL;

  }
#endif



  /******************************************************************************
  *
  *     Fun:   setAlarms()
  *
  *     Desc:  SCCP Congestion Control - ITU 96 only
  *
  *     Notes: NOT IMPLEMENTED
  *
  *     File:  INGwSmCtlHdlr.C
  *
  *******************************************************************************/
  int
  INGwSmCtlHdlr::sccpCongestionControl ()
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Entering INGwSmCtlHdlr::sccpCongestionControl", miTransId);

    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :INGwSmCtlHdlr::sccpCongestionControl Not implemented in this release", miTransId);

    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Leaving INGwSmCtlHdlr::sccpCongestionControl", miTransId);

    return BP_AIN_SM_FAIL;

  }


  /******************************************************************************
  *
  *     Fun:   sccpDeleteRoute()
  *
  *     Desc:  Delete an SCCP Route
  *
  *     Notes: None
  *
  *     File:  INGwSmCtlHdlr.C
  *
  *******************************************************************************/
  int
  INGwSmCtlHdlr::sccpDeleteRoute (int aiNetworkSAPId, int aiDpc, int aiSsn)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Entering INGwSmCtlHdlr::sccpDeleteRoute", miTransId);

    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :INGwSmCtlHdlr::sccpDeleteRoute Not implemented in this release", miTransId);

    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Leaving INGwSmCtlHdlr::sccpDeleteRoute", miTransId);

    return BP_AIN_SM_FAIL;

  }


  /******************************************************************************
  *
  *     Fun:   sccpConnStatusAudit()
  *
  *     Desc:  Enable/Disable Auditing of signalling connection status
  *
  *     Notes: None
  *
  *     File:  INGwSmCtlHdlr.C
  *
  *******************************************************************************/
  int
  INGwSmCtlHdlr::sccpConnStatusAudit (int aiConnStaType)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Entering INGwSmCtlHdlr::sccpConnStatusAudit", miTransId);

    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :INGwSmCtlHdlr::sccpConnStatusAudit Not implemented in this release", miTransId);

    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Leaving INGwSmCtlHdlr::sccpConnStatusAudit", miTransId);

    return BP_AIN_SM_FAIL;

  }


  /******************************************************************************
  *
  *     Fun:   setAlarms()
  *
  *     Desc:  SCCP Error Performance and System Availabiilty Report - ITU 96 only
  *
  *     Notes: NOT IMPLEMENTED
  *
  *     File:  INGwSmCtlHdlr.C
  *
  *******************************************************************************/
  int
  INGwSmCtlHdlr::sccpEnableErrorReport ()
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Entering INGwSmCtlHdlr::sccpEnableErrorReport", miTransId);

    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :INGwSmCtlHdlr::sccpEnableErrorReport Not implemented in this release", miTransId);

    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Leaving INGwSmCtlHdlr::sccpEnableErrorReport", miTransId);

    return BP_AIN_SM_FAIL;

  }


  /******************************************************************************
  *
  *     Fun:   sccpGuardTimer()
  *
  *     Desc:  set the Guard timer in SCCP
  *
  *     Notes: None
  *
  *     File:  INGwSmCtlHdlr.C
  *
  *******************************************************************************/
  int
  INGwSmCtlHdlr::sccpGuardTimer ()
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Entering INGwSmCtlHdlr::sccpGuardTimer", miTransId);

    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :INGwSmCtlHdlr::sccpGuardTimer Not implemented in this release", miTransId);

    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Leaving INGwSmCtlHdlr::sccpGuardTimer", miTransId);

    return BP_AIN_SM_FAIL;

  }


  /******************************************************************************
  *
  *     Fun:   sccpDeleteAddressMap()
  *
  *     Desc:  Delete the SCCP Address Map
  *
  *     Notes: None
  *
  *     File:  INGwSmCtlHdlr.C
  *
  *******************************************************************************/
  int
  INGwSmCtlHdlr::sccpDeleteAddressMap (int aiActionType, int aiSwitch, int aiReplaceFlag,
        int aiConnectionCouplingFlag, void *apGtt, 
        int aiNumSccpEntities)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Entering INGwSmCtlHdlr::sccpDeleteAddressMap", miTransId);

    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :INGwSmCtlHdlr::sccpDeleteAddressMap Not implemented in this release", miTransId);

    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Leaving INGwSmCtlHdlr::sccpDeleteAddressMap", miTransId);

    return BP_AIN_SM_FAIL;

  }


  /******************************************************************************
  *
  *     Fun:   sccpDeleteAssociation()
  *
  *     Desc:  Delete a SCCP Association
  *
  *     Notes: None
  *
  *     File:  INGwSmCtlHdlr.C
  *
  *******************************************************************************/
  int
  INGwSmCtlHdlr::sccpDeleteAssociation (void *apRule, void *apActionArray)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Entering INGwSmCtlHdlr::sccpDeleteAssociation", miTransId);

    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :INGwSmCtlHdlr::sccpDeleteAssociation Not implemented in this release", miTransId);

    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Leaving INGwSmCtlHdlr::sccpDeleteAssociation", miTransId);

    return BP_AIN_SM_FAIL;

  }


  /******************************************************************************
  *
  *     Fun:   sccpDeleteNetwork()
  *
  *     Desc:  Delete a Network not associated with a SAP in SCCP
  *
  *     Notes: None
  *
  *     File:  INGwSmCtlHdlr.C
  *
  *******************************************************************************/
  int
  INGwSmCtlHdlr::sccpDeleteNetwork (void *apNetworkId)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Entering INGwSmCtlHdlr::sccpDeleteNetwork", miTransId);

    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :INGwSmCtlHdlr::sccpDeleteNetwork Not implemented in this release", miTransId);

    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> :Leaving INGwSmCtlHdlr::sccpDeleteNetwork", miTransId);

    return BP_AIN_SM_FAIL;

  }


  /*
   * M3UA Layer operations
 */

/******************************************************************************
*
*     Fun:   m3uaEstablishAssociation()
*
*     Desc:  Establish association with a remote PSP in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaEstablishAssociation (INGwSmPspId &apPsp)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaEstablishAssociation", miTransId);

  if (miLayer != BP_AIN_SM_M3U_LAYER)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :This operation is only permitted for M3UA Layer", miTransId);
    return BP_AIN_SM_FAIL;
  }

  ItMgmt &cntrl = l.it;
  cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

  cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
  cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
  cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
  cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
  cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;

  cntrl.hdr.msgType = TCNTRL;
  cntrl.hdr.transId = miTransId; 
  /* set configuration parameters */
  cntrl.hdr.elmId.elmnt = STITPSP;
  cntrl.t.cntrl.action = AESTABLISH;
  cntrl.t.cntrl.subAction = SAELMNT;
  cntrl.t.cntrl.s.pspId = apPsp;

  //set the request context for alarms
  meReqContext.pspId = apPsp;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
  lmPst->event = EVTLITCNTRLREQ;
      
  smMiLitCntrlReq(lmPst, &cntrl);
 
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaEstablishAssociation", miTransId);

  return BP_AIN_SM_OK;

}

 
/******************************************************************************
*
*     Fun:   m3uaTerminateAssociation()
*
*     Desc:  Terminate Association with remote PSP in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaTerminateAssociation (INGwSmPspId &apPsp)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaTerminateAssociation", miTransId);

  if (miLayer != BP_AIN_SM_M3U_LAYER)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :This operation is only permitted for M3UA Layer", miTransId);
    return BP_AIN_SM_FAIL;
  } 
  
  ItMgmt &cntrl = l.it;
  cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));
  
  cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
  cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
  cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
  cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
  cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
  
  cntrl.hdr.msgType = TCNTRL;
  cntrl.hdr.transId = miTransId;
  /* set configuration parameters */
  cntrl.hdr.elmId.elmnt = STITPSP;
  cntrl.t.cntrl.action = ATERMINATE;
  cntrl.t.cntrl.subAction = SAELMNT;
  cntrl.t.cntrl.s.pspId = apPsp;

  //set the request context for alarms
  meReqContext.pspId = apPsp;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
  lmPst->event = EVTLITCNTRLREQ;

  smMiLitCntrlReq(lmPst, &cntrl);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaTerminateAssociation", miTransId);

  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   m3uaSendAspac()
*
*     Desc:  send ASP Active to remote PSP in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaSendAspac (int aiPsId, INGwSmPsId *apPsList, void *apInfoField)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaSendAspac PsId <%d>", aiPsId);

  if (miLayer != BP_AIN_SM_M3U_LAYER)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :This operation is only permitted for M3UA Layer", miTransId);
    return BP_AIN_SM_FAIL;
  } 
  
  ItMgmt &cntrl = l.it;
  cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));
  
  cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
  cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
  cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
  cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
  cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
  
  cntrl.hdr.msgType = TCNTRL;
  /* set configuration parameters */
  cntrl.hdr.elmId.elmnt = STITPSP;
  cntrl.hdr.transId = miTransId;
  cntrl.t.cntrl.action = AASPAC;
  cntrl.t.cntrl.subAction = SAELMNT;
  cntrl.t.cntrl.s.pspId = aiPsId;
  cntrl.t.cntrl.t.aspm.autoCtx = TRUE;
  cmMemcpy(cntrl.t.cntrl.t.aspm.info, (U8 *)"Hello world!", 13);

  //set the request context for alarms
  meReqContext.pspId = aiPsId;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
  lmPst->event = EVTLITCNTRLREQ;

  smMiLitCntrlReq(lmPst, &cntrl);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaSendAspac", miTransId);

  return BP_AIN_SM_OK;
}

#if 0
/******************************************************************************
*
*     Fun:   m3uaSendAspup()
*
*     Desc:  Send ASP UP to remote PSP in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaSendAspup (int aiPsId, INGwSmPsId *apPsList, void *apInfoField)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaSendAspup PsId <%d>", 
    miTransId, aiPsId);

  if (miLayer != BP_AIN_SM_M3U_LAYER)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :This operation is only permitted for M3UA Layer", miTransId);
    return BP_AIN_SM_FAIL;
  } 
  
  ItMgmt &cntrl = l.it;
  cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));
  
  cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
  cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
  cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
  cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
  cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
  
  cntrl.hdr.msgType = TCNTRL;
  /* set configuration parameters */
  cntrl.hdr.elmId.elmnt = STITPSP;
  cntrl.hdr.transId = miTransId;
  cntrl.t.cntrl.action = AASPUP;
  cntrl.t.cntrl.subAction = SAELMNT;
  cntrl.t.cntrl.s.pspId = aiPsId;

  //set the request context for alarms
  meReqContext.pspId = aiPsId;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
  lmPst->event = EVTLITCNTRLREQ;

  smMiLitCntrlReq(lmPst, &cntrl);


  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaSendAspup", miTransId);

  return BP_AIN_SM_OK;

}

/******************************************************************************
*
*     Fun:   m3uaSendAspdn()
*
*     Desc:  Send ASP Down to remote PSP in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaSendAspdn (int aiPsId, INGwSmPsId *apPsList, void *apInfoField)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaSendAspdn PsId <%d>", 
    miTransId, aiPsId);

  if (miLayer != BP_AIN_SM_M3U_LAYER)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :This operation is only permitted for M3UA Layer", miTransId);
    return BP_AIN_SM_FAIL;
  } 
  
  ItMgmt &cntrl = l.it;
  cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));
  
  cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
  cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
  cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
  cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
  cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
  
  cntrl.hdr.msgType = TCNTRL;
  cntrl.hdr.transId = miTransId;
  /* set configuration parameters */
  cntrl.hdr.elmId.elmnt = STITPSP;
  cntrl.t.cntrl.action = AASPDN;
  cntrl.t.cntrl.subAction = SAELMNT;
  cntrl.t.cntrl.s.pspId = aiPsId;

  //set the request context for alarms
  meReqContext.pspId = aiPsId;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
  lmPst->event = EVTLITCNTRLREQ;

  smMiLitCntrlReq(lmPst, &cntrl);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaSendAspdn", miTransId);

  return BP_AIN_SM_OK;

}


/******************************************************************************
*
*     Fun:   m3uaSendAspia()
*
*     Desc:  Send ASP Inactive to remote PSP in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaSendAspia (int aiPsId, INGwSmPsId *apPsList, void *apInfoField)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaSendAspia PsId <%d>", 
    miTransId, aiPsId);

  if (miLayer != BP_AIN_SM_M3U_LAYER)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> :This operation is only permitted for M3UA Layer", miTransId);
    return BP_AIN_SM_FAIL;
  } 
  
  ItMgmt &cntrl = l.it;
  cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));
  
  cntrl.hdr.response.selector = BP_AIN_SM_COUPLING;
  cntrl.hdr.response.prior = BP_AIN_SM_PRIOR;
  cntrl.hdr.response.route = BP_AIN_SM_ROUTE;
  cntrl.hdr.response.mem.region  = BP_AIN_SM_REGION;
  cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
  
  cntrl.hdr.msgType = TCNTRL;
  cntrl.hdr.transId = miTransId;
  /* set configuration parameters */
  cntrl.hdr.elmId.elmnt = STITPSP;
  cntrl.t.cntrl.action = AASPIA;
  cntrl.t.cntrl.subAction = SAELMNT;
  cntrl.t.cntrl.s.pspId = aiPsId;
  cntrl.t.cntrl.t.aspm.autoCtx = TRUE;
  cmMemcpy(cntrl.t.cntrl.t.aspm.info, (U8 *)"Hello world!", 13);

  //set the request context for alarms
  meReqContext.pspId = aiPsId;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
  lmPst->event = EVTLITCNTRLREQ;

  smMiLitCntrlReq(lmPst, &cntrl);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaSendAspia", miTransId);

  return BP_AIN_SM_OK;

}

#endif

/******************************************************************************
*
*     Fun:   m3uaSendScon()
*
*     Desc:  Send SCON to remote PSP in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaSendScon (int aiPsId, INGwSmPsId *apPsList, void *apInfoField)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaSendScon", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :INGwSmCtlHdlr::m3uaSendScon Not implemented in this release", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaSendScon", miTransId);

  return BP_AIN_SM_FAIL;

}


/******************************************************************************
*
*     Fun:   m3uaInhibitAssociation()
*
*     Desc:  send Management Inhibit of association with remote PSP in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaInhibitAssociation (INGwSmPsId apSp)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaInhibitAssociation", miTransId);

  logger.logMsg (ERROR_FLAG, 0,
    "TID <%d> :INGwSmCtlHdlr::m3uaInhibitAssociation Not implemented in this release", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaInhibitAssociation", miTransId);

  return BP_AIN_SM_FAIL;

}


/******************************************************************************
*
*     Fun:   m3uaUninhibitAssociation()
*
*     Desc:  send Management Uninhibit of association with remote PSP in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaUninhibitAssociation (INGwSmPsId apSp)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaUninhibitAssociation", miTransId);

  logger.logMsg (ERROR_FLAG, 0,
    "TID <%d> :INGwSmCtlHdlr::m3uaUninhibitAssociation Not implemented in this release", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaUninhibitAssociation", miTransId);

  return BP_AIN_SM_FAIL;

}


/******************************************************************************
*
*     Fun:   m3uaDeleteRoutingEntry()
*
*     Desc:  Delete Routing Entry in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaDeleteRoutingEntry (int aiNetworkId, INGwSmPsId aiPsId, 
                            INGwSmPc aiDpc, int aiRoutingEntry)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaDeleteRoutingEntry", miTransId);

  logger.logMsg (ERROR_FLAG, 0,
    "TID <%d> :INGwSmCtlHdlr::m3uaDeleteRoutingEntry Not implemented in this release", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaDeleteRoutingEntry", miTransId);

  return BP_AIN_SM_FAIL;

}


/******************************************************************************
*
*     Fun:   m3uaDeleteNetworkEntry()
*
*     Desc:  Delete Network Entry in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaDeleteNetworkEntry ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaDeleteNetworkEntry", miTransId);

  logger.logMsg (ERROR_FLAG, 0,
    "TID <%d> :INGwSmCtlHdlr::m3uaDeleteNetworkEntry Not implemented in this release", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaDeleteNetworkEntry", miTransId);

  return BP_AIN_SM_FAIL;

}


/******************************************************************************
*
*     Fun:   m3uaDeletePS()
*
*     Desc:  Delete PS in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaDeletePS ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaDeletePS", miTransId);

  logger.logMsg (ERROR_FLAG, 0,
    "TID <%d> :INGwSmCtlHdlr::m3uaDeletePS Not implemented in this release", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaDeletePS", miTransId);

  return BP_AIN_SM_FAIL;

}


/******************************************************************************
*
*     Fun:   m3uaDeletePSP()
*
*     Desc:  Delete remote PSP in M3UA
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaDeletePSP ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaDeletePSP", miTransId);

  logger.logMsg (ERROR_FLAG, 0,
    "TID <%d> :INGwSmCtlHdlr::m3uaDeletePSP Not implemented in this release", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaDeletePSP", miTransId);

  return BP_AIN_SM_FAIL;

}


/******************************************************************************
*
*     Fun:   m3uaRegisterRoutingKeys()
*
*     Desc:  register routing keys to peer server
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaRegisterRoutingKeys (int aiPsid, int aiDpc, int aiNumOpcs,
     void *apOpcList, int aiNumSio, void *apSioList, void *apCicRange)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaRegisterRoutingKeys", miTransId);

  logger.logMsg (ERROR_FLAG, 0,
    "TID <%d> :INGwSmCtlHdlr::m3uaRegisterRoutingKeys Not implemented in this release", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaRegisterRoutingKeys", miTransId);

  return BP_AIN_SM_FAIL;

}


/******************************************************************************
*
*     Fun:   m3uaDeregisterRoutingKeys()
*
*     Desc:  Deregister routing keys to peer server
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::m3uaDeregisterRoutingKeys (int aiPsid, int aiDpc, int aiNumOpcs,
      void *apOpcList, int aiNumSio, void *apSioList, void *apCicRange)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::m3uaDeregisterRoutingKeys", miTransId);

  logger.logMsg (ERROR_FLAG, 0,
    "TID <%d> :INGwSmCtlHdlr::m3uaDeregisterRoutingKeys Not implemented in this release", miTransId);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::m3uaDeregisterRoutingKeys", miTransId);

  return BP_AIN_SM_FAIL;
}


int INGwSmCtlHdlr::sgEnableNode(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,"Entering sgEnableNode");
	SgMngmt &cntrl = l.sg;
  U16 procIdList[MAX_PROC_IDS];
  U16 cnt = 0;
  int j;
  char* node;
  int nodeType;
	std::ostringstream oStr;
  SgHiEnableNode *enbNode;
	cmMemset((U8 *)&cntrl, 0, sizeof(SgMngmt));
  enbNode = &(cntrl.t.hi.cntrl.u.enableNode);


	fillHdr(&(l.sg.hdr), miTransId, ENTSG, 0, TCNTRL, STSGGEN, 0);

  int selfRole = mpDist->getTcapProvider()->myRole();

	cntrl.apiType = LSG_HI_API;
	cntrl.t.hi.cntrl.action = LSG_AENABLE;
	cntrl.t.hi.cntrl.subAction = STNA;
  nodeType = mrDist.getSmRepository()->getTransportType();

  cnt = 0;
  enbNode->nmbEnts = 0; 
  enbNode->procId = stackReq->req.u.sgNode.procId; 

	oStr<< "sgEnableNode for ProcId: " << enbNode->procId << " ";

  if (stackReq->req.u.sgNode.entId == ENTSG)
  {
    logger.logMsg (TRACE_FLAG, 0,"ENABLE_NODE for SG <%d>",enbNode->procId);
    /* layer ENTSG */
    enbNode->nmbEnts++;
    enbNode->entList[cnt].entId = ENTSG;
    enbNode->entList[cnt].instId = 0;
    //enbNode->entList[cnt].lastProc = stackReq->req.u.sgNode.lastProc;
    enbNode->entList[cnt].lastProc = TRUE;

		oStr<< "entId: ENTSG, ";

    if ((selfRole == 1) && (selfProcId == enbNode->procId)) {
      logger.logMsg (TRACE_FLAG, 0,"ENABLE_NODE for SG, procUsage is LSG_HI_USE_ACTIVE");
      enbNode->entList[cnt].procUsage = LSG_HI_USE_ACTIVE;
			oStr<< ", procUsage: LSG_HI_USE_ACTIVE";
    }
    else {
      logger.logMsg (TRACE_FLAG, 0,"ENABLE_NODE for SG, procUsage is LSG_HI_USE_STANDBY");
      enbNode->entList[cnt].procUsage= LSG_HI_USE_STANDBY;
			oStr<< ", procUsage: LSG_HI_USE_STANDBY";
    }

		// For FT
		if(INGwSmBlkConfig::getInstance().m_peerProcId != 0)
		{
			if(enbNode->procId == INGwSmBlkConfig::getInstance().m_selfProcId)
			{
        enbNode->entList[cnt].backupProcList[0] = 
												INGwSmBlkConfig::getInstance().m_peerProcId;
			}
			else {
        enbNode->entList[cnt].backupProcList[0] = 
												INGwSmBlkConfig::getInstance().m_selfProcId;
			}
			enbNode->entList[cnt].nmbBackupProcs= 1;
				oStr<< ", backupProcId: "<< enbNode->entList[cnt].backupProcList[0];
		}
		else // for Non-FT
    		enbNode->entList[cnt].nmbBackupProcs= 0;

		oStr<< ", numBackupProc: " << enbNode->entList[cnt].nmbBackupProcs;
  }
  else
  {
    if (((stackReq->req.u.sgNode.entId == ENT_INC_SS7) || 
         (stackReq->req.u.sgNode.entId == ENT_INC_MTP2_MTP3) ||
         (stackReq->req.u.sgNode.entId == ENT_INC_MTP3)) && 
        ((nodeType == SS7) || (nodeType == SS7_SIGTRAN))) {

			oStr<< " Enable Node: SS7 MTP3: ";
      /* layer ENTSN */
      enbNode->nmbEnts++;
      enbNode->entList[cnt].entId = ENTSN;
      enbNode->entList[cnt].instId= 0;
      enbNode->entList[cnt].lastProc = TRUE;
      enbNode->entList[cnt].procUsage= LSG_HI_USE_ACTIVE;
      enbNode->entList[cnt].nmbBackupProcs= 0;

			oStr<< " entId: ENTSN, procUsage: LSG_HI_USE_ACTIVE, nmbBackupProcs:0";

      logger.logMsg (TRACE_FLAG, 0,
             "sgEnableNode():MTP3 nmbEnts=<%d> entId=<%d> LastProc=<%d>", 
             enbNode->nmbEnts, enbNode->entList[cnt].entId, 
             enbNode->entList[cnt].lastProc);
      cnt++;
    }

    if ((stackReq->req.u.sgNode.entId == ENT_INC_SCTP_TUCL) && 
        ((nodeType == SIGTRAN) || (nodeType == SS7_SIGTRAN))) {

			oStr<< " Enable SCTP_TUCL entId: ENTHI, procUsage: LSG_HI_USE_ACTIVE, nmbBackupProcs:0";

      logger.logMsg (TRACE_FLAG, 0,"ENABLE_NODE for SIGTRAN layers");
      /* layer ENTHI */
      enbNode->nmbEnts++;
      enbNode->entList[cnt].entId = ENTHI;
      enbNode->entList[cnt].instId= 0;
      enbNode->entList[cnt].lastProc = TRUE;

      logger.logMsg (TRACE_FLAG, 0, "sgEnableNode():TUCL LastProc=<%d>", 
             enbNode->entList[cnt].lastProc);

      //enbNode->entList[cnt].lastProc = TRUE;  INCTBD
      enbNode->entList[cnt].procUsage= LSG_HI_USE_ACTIVE;
      enbNode->entList[cnt].nmbBackupProcs= 0;

      logger.logMsg (TRACE_FLAG, 0, "sgEnableNode():TUCL nmbEnts=<%d> entId=<%d>", 
             enbNode->nmbEnts, enbNode->entList[cnt].entId);
      cnt++;

			oStr<< " Enable SCTP entId: ENTSB, procUsage: LSG_HI_USE_ACTIVE, nmbBackupProcs:0";
      /* layer ENTSB */
      enbNode->nmbEnts++;
      enbNode->entList[cnt].entId = ENTSB;
      enbNode->entList[cnt].instId= 0;
      enbNode->entList[cnt].lastProc = stackReq->req.u.sgNode.lastProc;
      enbNode->entList[cnt].lastProc = TRUE;
      enbNode->entList[cnt].procUsage= LSG_HI_USE_ACTIVE;
      enbNode->entList[cnt].nmbBackupProcs= 0;

      logger.logMsg (TRACE_FLAG, 0, "sgEnableNode():SCTP nmbEnts=<%d> entId=<%d>", 
             enbNode->nmbEnts, enbNode->entList[cnt].entId);
      cnt++;

    }

    if((stackReq->req.u.sgNode.entId == ENT_INC_M3UA) &&
      ((nodeType == SIGTRAN) || (nodeType == SS7_SIGTRAN))){

			oStr << "Enable Node M3UA: entId: ENTIT, procUsage:LSG_HI_USE_ACTIVE, nmbBackupProcs:0";

      /* layer ENTIT */
      enbNode->nmbEnts++;
      enbNode->entList[cnt].entId = ENTIT;
      enbNode->entList[cnt].instId= 0;
      enbNode->entList[cnt].lastProc = TRUE;
      enbNode->entList[cnt].procUsage= LSG_HI_USE_ACTIVE;
      enbNode->entList[cnt].nmbBackupProcs = 0;

      logger.logMsg (TRACE_FLAG, 0, "sgEnableNode():M3UA nmbEnts=<%d> entId=<%d>",
             enbNode->nmbEnts, enbNode->entList[cnt].entId);
      cnt++;
    }
    

    if ((stackReq->req.u.sgNode.entId == ENT_INC_SS7) ||
        (stackReq->req.u.sgNode.entId == ENT_INC_SCCP_TCAP)) {

			oStr << "Enable Node SCCP: entId: ENTSP";
      /* layer ENTSP */
      enbNode->nmbEnts++;
      enbNode->entList[cnt].entId = ENTSP;
      enbNode->entList[cnt].instId= 0;
      enbNode->entList[cnt].lastProc = stackReq->req.u.sgNode.lastProc;
      enbNode->entList[cnt].lastProc = TRUE;
      if ((selfRole == 1) && (selfProcId == enbNode->procId)) {
        enbNode->entList[cnt].procUsage= LSG_HI_USE_ACTIVE;
				oStr << ", procUsage: LSG_HI_USE_ACTIVE";
      }
      else {
        enbNode->entList[cnt].procUsage= LSG_HI_USE_STANDBY;
				oStr << ", procUsage: LSG_HI_USE_STANDBY";
      }

			// For FT
			if(INGwSmBlkConfig::getInstance().m_peerProcId != 0)
			{
				if(enbNode->procId == INGwSmBlkConfig::getInstance().m_selfProcId)
				{
        	enbNode->entList[cnt].backupProcList[0] = 
												INGwSmBlkConfig::getInstance().m_peerProcId;
				}
				else {
        	enbNode->entList[cnt].backupProcList[0] = 
												INGwSmBlkConfig::getInstance().m_selfProcId;
				}

    		enbNode->entList[cnt].nmbBackupProcs= 1;
				oStr<< ", backupProcId: "<< enbNode->entList[cnt].backupProcList[0];
			}
			else // for Non-FT
    		enbNode->entList[cnt].nmbBackupProcs= 0;

			oStr<< ", numBackupProc: " << enbNode->entList[cnt].nmbBackupProcs;

      logger.logMsg (TRACE_FLAG, 0, "sgEnableNode:SCCP nmbEnts=<%d> entId=<%d>", 
             enbNode->nmbEnts, enbNode->entList[cnt].entId);
      cnt++;

			oStr << " Enable Node: TCAP, entId: ENTST";

      /* layer ENTST */
      enbNode->nmbEnts++;
      enbNode->entList[cnt].entId = ENTST;
      enbNode->entList[cnt].instId= 0;
      enbNode->entList[cnt].lastProc = stackReq->req.u.sgNode.lastProc;
      enbNode->entList[cnt].lastProc = TRUE;
      if ((selfRole == 1) && (selfProcId == enbNode->procId)) {
        enbNode->entList[cnt].procUsage= LSG_HI_USE_ACTIVE;
				oStr << ", procUsage: LSG_HI_USE_ACTIVE";
      }
      else {
        enbNode->entList[cnt].procUsage= LSG_HI_USE_STANDBY;
				oStr << ", procUsage: LSG_HI_USE_STANDBY";
      }

			// For FT
			if(INGwSmBlkConfig::getInstance().m_peerProcId != 0)
			{
				if(enbNode->procId == INGwSmBlkConfig::getInstance().m_selfProcId)
				{
        	enbNode->entList[cnt].backupProcList[0] = 
												INGwSmBlkConfig::getInstance().m_peerProcId;
				}
				else {
        	enbNode->entList[cnt].backupProcList[0] = 
												INGwSmBlkConfig::getInstance().m_selfProcId;
				}

    		enbNode->entList[cnt].nmbBackupProcs= 1;
				oStr<< ", backupProcId: "<< enbNode->entList[cnt].backupProcList[0];
			}
			else // for Non-FT
    		enbNode->entList[cnt].nmbBackupProcs= 0;

			oStr<< ", numBackupProc: " << enbNode->entList[cnt].nmbBackupProcs;

      logger.logMsg (TRACE_FLAG, 0, "sgEnableNode:TCAP nmbEnts=<%d> entId=<%d>", 
             enbNode->nmbEnts, enbNode->entList[cnt].entId);
    }
  }

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_SG_LAYER);
  lmPst->dstProcId = SFndProcId();
  logger.logMsg (TRACE_FLAG, 0, "before calling SmMiLsgCntrlReq:dstProcId=<%d> srcProcId=<%d>", 
              lmPst->dstProcId, lmPst->srcProcId );

  oStr<< " dstProcId: " << lmPst->dstProcId << ", srcProcId: " << lmPst->srcProcId ;
	g_dumpMsg("INGwSmCtlHdlr", __LINE__, oStr);

  char entPrntBuff[1024];
  memset(entPrntBuff, 0, sizeof(entPrntBuff));
  int len = 0;
  for(int t = 0; t < enbNode->nmbEnts; t++) {
    len += sprintf(entPrntBuff + len, "entId[%d]{entId: %d instId: %d lastProc: %d procUsage: %d nmbBackupProcs: %d%s", t, enbNode->entList[t].entId, enbNode->entList[t].instId, enbNode->entList[t].lastProc, enbNode->entList[t].procUsage, enbNode->entList[t].nmbBackupProcs, ((enbNode->entList[t].nmbBackupProcs == 0) || ((t + 1) == enbNode->nmbEnts))?"}":" ");
    
    for(int u = 0; u < enbNode->entList[t].nmbBackupProcs; u++) {
      len += sprintf(entPrntBuff + len, "BackupProc[%d]{procId: %d%s", u, enbNode->entList[t].backupProcList[u], ((u + 1) == enbNode->entList[t].nmbBackupProcs)?"}":" ");
    }
  }

  printf("[+INC+] %s:%d sgEnableNode():: Invoking SmMiLsgCntrlReq with apiType %d elmnt %d action %d subAction %d procId %d nmbEnts %d entLst %s srcProcId %d destProcId %d\n", __FILE__, __LINE__, cntrl.apiType, cntrl.hdr.elmId.elmnt, cntrl.t.hi.cntrl.action, cntrl.t.hi.cntrl.subAction, enbNode->procId, enbNode->nmbEnts, entPrntBuff, lmPst->srcProcId, lmPst->dstProcId);

  logger.logMsg (ALWAYS_FLAG, 0,
     "Invoking SmMiLsgCntrlReq with apiType %d elmnt %d action %d subAction %d procId %d nmbEnts %d entLst %s srcProcId %d destProcId %d\n", cntrl.apiType, cntrl.hdr.elmId.elmnt, cntrl.t.hi.cntrl.action, cntrl.t.hi.cntrl.subAction, enbNode->procId, enbNode->nmbEnts, entPrntBuff, lmPst->srcProcId, lmPst->dstProcId);

  SmMiLsgCntrlReq(lmPst, &cntrl);

	/* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

	logger.logMsg (TRACE_FLAG, 0,"Leaving sgEnableNode");
  return BP_AIN_SM_OK;
}



int INGwSmCtlHdlr::sgAbortTrans(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,"Entering sgAbortTrans");
	SgMngmt &cntrl = l.sg;
  U16 procIdList[MAX_PROC_IDS];
  U8 numProc;
  int nodeType;
  SgHiAbort *abrtTrans;
	cmMemset((U8 *)&cntrl, 0, sizeof(SgMngmt));
  abrtTrans = &(cntrl.t.hi.cntrl.u.abort);

	fillHdr(&(l.sg.hdr), miTransId, ENTSG, 0, TCNTRL, STSGGEN, 0);

  //int selfRole = mpDist->getTcapProvider()->myRole();

	cntrl.apiType = LSG_HI_API;
	cntrl.t.hi.cntrl.action = LSG_AMK_ABORT;
	cntrl.t.hi.cntrl.subAction = STNA;
  //nodeType = mrDist.getSmRepository()->getTransportType();
  //numProc = mpDist->getTcapProvider()->getProcIdList(procIdList);

  logger.logMsg (TRACE_FLAG, 0,"+VER+ Aborting SG Transactions <%d>",BP_AIN_SM_SG_TRANSID);
  /* layer ENTSG */
  
  abrtTrans->transId = BP_AIN_SM_SG_TRANSID;
  
  Pst *lmPst = mpRep->getPst (BP_AIN_SM_SG_LAYER);
  lmPst->dstProcId = SFndProcId();
  logger.logMsg (TRACE_FLAG, 0, "Invoking SmMiLsgCntrlReq:dstProcId=<%d> srcProcId=<%d>", 
              lmPst->dstProcId, lmPst->srcProcId );

  SmMiLsgCntrlReq(lmPst, &cntrl);

	/* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

	logger.logMsg (TRACE_FLAG, 0,"Leaving sgAbortTrans");
  return BP_AIN_SM_OK;
}



int INGwSmCtlHdlr::sgDisableNode(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,"Entering sgDisableNode");
	SgMngmt &cntrl = l.sg;
  U16 procIdList[MAX_PROC_IDS];
  U8 numProc;
	std::ostringstream oStr;
  U16 cnt;
  int i;
  char* node;
  int nodeType;
  SgHiDisableNode *disNode;
	cmMemset((U8 *)&cntrl, 0, sizeof(SgMngmt));
  disNode = &(cntrl.t.hi.cntrl.u.disableNode);

	fillHdr(&(l.sg.hdr), miTransId, ENTSG, 0, TCNTRL, STSGGEN, 0);

	cntrl.apiType = LSG_HI_API;
	cntrl.t.hi.cntrl.action = LSG_ADISABLE;
	cntrl.t.hi.cntrl.subAction = STNA;
  nodeType = mrDist.getSmRepository()->getTransportType();
  numProc = mpDist->getTcapProvider()->getProcIdList(procIdList);
  disNode->nmbEnts = 0; 
  cnt = 0;

  disNode->procId = stackReq->req.u.sgNode.procId;
  disNode->forcedDisable = TRUE; 
  disNode->reDistribute = TRUE; 

	oStr << "Disable Node for ProcId: "<< disNode->procId ;

  /* Entity list */
  /* layer ENTSG */

  if (stackReq->req.u.sgNode.entId == ENTSG)
  {
    disNode->nmbEnts++;
    disNode->entList[cnt].ent = ENTSG;
    disNode->entList[cnt++].inst= 0;
#ifdef LSGV2
    // This shall be used when more than one SG has failed. 
    // In that case other SG ProcIds needs to be specified 
    // in the dependentProcList
    disNode->nmbDependProcs = 0;

#endif
		oStr << " ent: ENTSG, ";
  }
  else{

    if(((stackReq->req.u.sgNode.entId == ENT_INC_SS7) ||
        (stackReq->req.u.sgNode.entId == ENT_INC_MTP2_MTP3)) && 
       ((nodeType == SS7) || (nodeType == SS7_SIGTRAN))) {

       /* layer ENTSD */
       disNode->nmbEnts++;
       disNode->entList[cnt].ent = ENTSD;
       disNode->entList[cnt].inst = 0;
       logger.logMsg (TRACE_FLAG, 0,
               "sgDisableNode():MTP2 nmbEnts=<%d> entId=<%d>", 
               disNode->nmbEnts, disNode->entList[cnt].ent);
			oStr << " entId: ENTSD MTP2_MTP3";
       cnt++;
    }


    if(((stackReq->req.u.sgNode.entId == ENT_INC_SS7) || 
        (stackReq->req.u.sgNode.entId == ENT_INC_MTP2_MTP3) ||
        (stackReq->req.u.sgNode.entId == ENT_INC_MTP3)) &&
       ((nodeType == SS7) || (nodeType == SS7_SIGTRAN))) {

      /* layer ENTSN */
      disNode->nmbEnts++;
      disNode->entList[cnt].ent = ENTSN;
      disNode->entList[cnt].inst = 0;
      logger.logMsg (TRACE_FLAG, 0,
              "sgDisableNode():MTP3 nmbEnts=<%d> entId=<%d>", 
              disNode->nmbEnts, disNode->entList[cnt].ent);
			oStr << " entId: ENTSN MTP2_MTP3";
      cnt++;
    }

    if (((stackReq->req.u.sgNode.entId == ENT_INC_SCTP_TUCL) ||
        (stackReq->req.u.sgNode.entId == ENT_INC_M3UA_SCTP_TUCL)) &&
        ((nodeType == SIGTRAN) || (nodeType == SS7_SIGTRAN))) {
       /* layer ENTHI */
       disNode->nmbEnts++;
       disNode->entList[cnt].ent = ENTHI;
       disNode->entList[cnt].inst = 0;
       logger.logMsg (TRACE_FLAG, 0,
               "sgDisableNode():TUCL nmbEnts=<%d> entId=<%d>", 
               disNode->nmbEnts, disNode->entList[cnt].ent);
       cnt++;

       /* layer ENTSB */
       disNode->nmbEnts++;
       disNode->entList[cnt].ent = ENTSB;
       disNode->entList[cnt].inst = 0;
       logger.logMsg (TRACE_FLAG, 0,
               "sgDisableNode():SCTP nmbEnts=<%d> entId=<%d>", 
               disNode->nmbEnts, disNode->entList[cnt].ent);
			oStr << " entId: ENTHI ENT_INC_SCTP_TUCL";
       cnt++;
    }
    
    if (((stackReq->req.u.sgNode.entId == ENT_INC_M3UA) ||
        (stackReq->req.u.sgNode.entId == ENT_INC_M3UA_SCTP_TUCL)) &&
        ((nodeType == SIGTRAN) || (nodeType == SS7_SIGTRAN))) {
       /* layer ENTIT */
       disNode->nmbEnts++;
       disNode->entList[cnt].ent = ENTIT;
       disNode->entList[cnt].inst = 0;
       logger.logMsg (TRACE_FLAG, 0,
               "sgDisableNode():M3UA nmbEnts=<%d> entId=<%d>", 
               disNode->nmbEnts, disNode->entList[cnt].ent);
			oStr << " entId: ENTIT ENT_INC_M3UA";
       cnt++;
    }


    
    if((stackReq->req.u.sgNode.entId == ENT_INC_SS7) || 
       (stackReq->req.u.sgNode.entId == ENT_INC_SCCP_TCAP)) {

      /* layer ENTSP */
      disNode->nmbEnts++;
      disNode->entList[cnt].ent = ENTSP;
      disNode->entList[cnt].inst = 0;

#ifdef LSGV2
      // This shall be used when more than one SG has failed. 
      // In that case other SG ProcIds needs to be specified 
      // in the dependentProcList
      disNode->nmbDependProcs = 0;

#endif
      logger.logMsg (TRACE_FLAG, 0,
         "sgDisableNode():SCCP nmbEnts=<%d> entId=<%d>", 
          disNode->nmbEnts, disNode->entList[cnt].ent);
			oStr << " entId: ENTSP SCCP";
      cnt++;

      /* layer ENTST */
      disNode->nmbEnts++;
      disNode->entList[cnt].ent = ENTST;
      disNode->entList[cnt].inst = 0;

#ifdef LSGV2
      // This shall be used when more than one SG has failed. 
      // In that case other SG ProcIds needs to be specified 
      // in the dependentProcList
      disNode->nmbDependProcs = 0;
#endif
      logger.logMsg (TRACE_FLAG, 0,
         "sgDisableNode():TCAP nmbEnts=<%d> entId=<%d>", 
          disNode->nmbEnts, disNode->entList[cnt].ent);
			oStr << " entId: ENTST TCAP";
      cnt++;
    }
  }

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_SG_LAYER);
  lmPst->dstProcId = SFndProcId();

  logger.logMsg (TRACE_FLAG, 0, "Before calling SmMiLsgCntrlReq:dstProcId=<%d> srcProcId=<%d>"    , lmPst->dstProcId, lmPst->srcProcId );
	oStr << " dstProcId: " << lmPst->dstProcId << " srcProcId: "<< lmPst->srcProcId ; 

	g_dumpMsg("INGwSmCtlHdlr", __LINE__, oStr);

  SmMiLsgCntrlReq(lmPst, &cntrl);
  /* update the response structure */

  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);

	logger.logMsg (TRACE_FLAG, 0,"Leaving sgDisableNode");
  return BP_AIN_SM_OK;
}




int INGwSmCtlHdlr::m3uaAssocAbort(StackReqResp *stackReq) {
	logger.logMsg (TRACE_FLAG, 0,"Enetering m3uaAssocAbort");

	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

  fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITPSP, NA);

	cntrl.t.cntrl.action = ATERMINATE;
	cntrl.t.cntrl.subAction = SAELMNT;

	cntrl.t.cntrl.t.aspm.sctSuId = stackReq->req.u.m3uaAssocAbort.m3uaLsapId;
#ifdef IT_ABORT_ASSOC
	cntrl.t.cntrl.t.aspm.abrtFlag = stackReq->req.u.m3uaAssocAbort.abrtFlag;
#endif
	cntrl.t.cntrl.s.pspId = stackReq->req.u.m3uaAssocAbort.pspId;

  //set the request context for alarms
  meReqContext.pspId = cntrl.t.cntrl.s.pspId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  lmPst->dstProcId = SFndProcId();
  lmPst->srcProcId = SFndProcId();

  printf("[+INC+] %s:%d m3uaAssocAbort():: Abort Assoc Invoking ItMiLitCntrlReq with msgType %d elmnt %d action %d subAction %d sctSuId %d abrtFlag %d pspId %d srcProcId %d destProcId %d\n", __FILE__, __LINE__, cntrl.hdr.msgType, cntrl.hdr.elmId.elmnt, cntrl.t.cntrl.action, cntrl.t.cntrl.subAction, cntrl.t.cntrl.t.aspm.sctSuId, cntrl.t.cntrl.t.aspm.abrtFlag, cntrl.t.cntrl.s.pspId, lmPst->srcProcId, lmPst->dstProcId);
  logger.logMsg (ALWAYS_FLAG, 0,
         "m3uaAssocAbort():: Abort Assoc Invoking ItMiLitCntrlReq with msgType %d elmnt %d action %d subAction %d sctSuId %d abrtFlag %d pspId %d srcProcId %d destProcId %d\n", cntrl.hdr.msgType, cntrl.hdr.elmId.elmnt, cntrl.t.cntrl.action, cntrl.t.cntrl.subAction, cntrl.t.cntrl.t.aspm.sctSuId, cntrl.t.cntrl.t.aspm.abrtFlag, cntrl.t.cntrl.s.pspId, lmPst->srcProcId, lmPst->dstProcId);

	ItMiLitCntrlReq(lmPst, &cntrl);
    /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);
  
	logger.logMsg (TRACE_FLAG, 0,"Leaving m3uaAssocAbort");
  return BP_AIN_SM_OK;
}


int INGwSmCtlHdlr::m3uaSctsapUnbnd(StackReqResp *stackReq) {
	logger.logMsg (TRACE_FLAG, 0,"Enetering m3uaSctsapUnbnd");

	ItMgmt &cntrl = l.it;
	cmMemset((U8 *)&cntrl, 0, sizeof(ItMgmt));

  fillHdr(&(l.it.hdr), miTransId, ENTIT, 0, TCNTRL, STITSCTSAP, NA);

	cntrl.t.cntrl.action = AUBND;
	cntrl.t.cntrl.subAction = SAELMNT;

	cntrl.t.cntrl.s.suId = stackReq->req.u.unbindSap.sapId;

	Pst *lmPst = mpRep->getPst (BP_AIN_SM_M3U_LAYER);
	lmPst->event = EVTLITCNTRLREQ;
  lmPst->dstProcId = SFndProcId();
  lmPst->srcProcId = SFndProcId();

  //printf("[+INC+] %s:%d m3uaSctsapUnbnd():: Abort Assoc Invoking ItMiLitCntrlReq with msgType %d elmnt %d action %d subAction %d sctSuId %d abrtFlag %d pspId %d srcProcId %d destProcId %d\n", __FILE__, __LINE__, cntrl.hdr.msgType, cntrl.hdr.elmId.elmnt, cntrl.t.cntrl.action, cntrl.t.cntrl.subAction, cntrl.t.cntrl.t.aspm.sctSuId, cntrl.t.cntrl.t.aspm.abrtFlag, cntrl.t.cntrl.s.pspId, lmPst->srcProcId, lmPst->dstProcId);
  //logger.logMsg (ALWAYS_FLAG, 0,
  //       "m3uaSctsapUnbnd():: Abort Assoc Invoking ItMiLitCntrlReq with msgType %d elmnt %d action %d subAction %d sctSuId %d abrtFlag %d pspId %d srcProcId %d destProcId %d\n", cntrl.hdr.msgType, cntrl.hdr.elmId.elmnt, cntrl.t.cntrl.action, cntrl.t.cntrl.subAction, cntrl.t.cntrl.t.aspm.sctSuId, cntrl.t.cntrl.t.aspm.abrtFlag, cntrl.t.cntrl.s.pspId, lmPst->srcProcId, lmPst->dstProcId);

	ItMiLitCntrlReq(lmPst, &cntrl);
    /* update the response structure */
  stackReq->resp.procId = stackReq->procId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;

  mrDist.updateRspStruct(miTransId,stackReq);
  
	logger.logMsg (TRACE_FLAG, 0,"Leaving m3uaSctsapUnbnd");
  return BP_AIN_SM_OK;
}


#ifdef INCTBD 
int INGwSmCtlHdlr::sgAbrtTrans()
{
  logger.logMsg (TRACE_FLAG, 0,"Entering sgAbrtTrans");
  SgMngmt &cntrl = l.sg;
  cmMemset((U8 *)&cntrl, 0, sizeof(SgMngmt));

  fillHdr(&(l.sg.hdr), miTransId, ENTSG, 0, TCNTRL, STSGGEN, 0);

  cntrl.t.cntrl.action = LSG_AMK_ABORT;
  cntrl.t.cntrl.subAction = STNA;

  cntrl->t.hi.cntrl.u.abort.transId = BP_AIN_SM_SG_TRANSID;

  Pst *lmPst = mpRep->getPst (BP_AIN_SM_SG_LAYER);
  lmPst->procId = SFndProcId();

  SmMiLsgCntrlReq(lmPst, &cntrl);
  /* update the response structure */
  logger.logMsg (TRACE_FLAG, 0,"Leaving sgAbrtTrans");
  return BP_AIN_SM_OK;
}
#endif


#if 0
/******************************************************************************
*
*     Fun:   iuLiIetBndReq()
*
*     Desc:  send the bind request for INAP User
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int 
INGwSmCtlHdlr::iuLiIetBndReq (Pst *pst, SuId suId, SpId spId)
{   
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::iuLiIetBndReq", miTransId);

  //only Loosely coupled is supported now
  cmPkIetBndReq (pst, suId, spId); 

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::iuLiIetBndReq", miTransId);

  return BP_AIN_SM_OK;
}
#endif

/******************************************************************************
*
*     Fun:   smMiLstCntrlReq()
*
*     Desc:  send the control request for TCAP Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::smMiLstCntrlReq (Pst *pst, StMngmt *cntrl)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::smMiLstCntrlReq", miTransId);
 
  //only Loosely coupled is supported now
  cmPkLstCntrlReq (pst, cntrl);
 
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::smMiLstCntrlReq", miTransId);
 
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLieCntrlReq()
*
*     Desc:  send the control request for INAP Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
#if 0
int
INGwSmCtlHdlr::smMiLieCntrlReq (Pst *pst, IeMngmt *cntrl)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::smMiLieCntrlReq", miTransId);

  //only Loosely coupled is supported now
  cmPkLieCntrlReq (pst, cntrl);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::smMiLieCntrlReq", miTransId);

  return BP_AIN_SM_OK;
}
#endif

/******************************************************************************
*
*     Fun:   smMiLspCntrlReq()
*
*     Desc:  send the control request for SCCP Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::smMiLspCntrlReq (Pst *pst, SpMngmt *cntrl)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::smMiLspCntrlReq", miTransId);

  //only Loosely coupled is supported now
  cmPkLspCntrlReq (pst, cntrl);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::smMiLspCntrlReq", miTransId);

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLitCntrlReq()
*
*     Desc:  send the control request for M3UA Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::smMiLitCntrlReq (Pst *pst, ItMgmt *cntrl)
{  
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::smMiLitCntrlReq", miTransId);
 
  //only Loosely coupled is supported now
  cmPkLitCntrlReq (pst, cntrl);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::smMiLitCntrlReq", miTransId);
 
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLsbCntrlReq()
*
*     Desc:  send the control request for SCTP Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int 
INGwSmCtlHdlr::smMiLsbCntrlReq (Pst *pst, SbMgmt *cntrl)
{   
  logger.logMsg (TRACE_FLAG, 0,    
    "TID <%d> :Entering INGwSmCtlHdlr::smMiLsbCntrlReq", miTransId); 
  
  //only Loosely coupled is supported now 
  cmPkLsbCntrlReq (pst, cntrl);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::smMiLsbCntrlReq", miTransId);
  
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLhiCntrlReq()
*
*     Desc:  send the control request for TUCL Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
int
INGwSmCtlHdlr::smMiLhiCntrlReq (Pst *pst, HiMngmt *cntrl)
{  
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::smMiLhiCntrlReq", miTransId);
 
  //only Loosely coupled is supported now
  cmPkLhiCntrlReq (pst, cntrl);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::smMiLhiCntrlReq", miTransId);
 
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLsnCntrlReq()
*
*     Desc:  send the control request for TUCL Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
//send the request to MTP3 layer
int
INGwSmCtlHdlr::smMiLsnCntrlReq (Pst *pst, SnMngmt *cntrl) {

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::smMiLsnCntrlReq", miTransId);
 
  //only Loosely coupled is supported now
  cmPkLsnCntrlReq (pst, cntrl);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::smMiLsnCntrlReq", miTransId);


  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   smMiLsdCntrlReq()
*
*     Desc:  send the control request for TUCL Layer
*
*     Notes: None
*
*     File:  INGwSmCtlHdlr.C
*
*******************************************************************************/
//send the request to MTP2 layer
int
INGwSmCtlHdlr::smMiLsdCntrlReq (Pst *pst, SdMngmt *cntrl) {

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Entering INGwSmCtlHdlr::smMiLsdCntrlReq", miTransId);
 
  //only Loosely coupled is supported now
  cmPkLsdCntrlReq (pst, cntrl);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> :Leaving INGwSmCtlHdlr::smMiLsdCntrlReq", miTransId);

  return BP_AIN_SM_OK;
}

int INGwSmCtlHdlr::shutdownTcapLayer(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,"Entering shutdownTcapLayer()");
  //  TCAP layer shutdown request
  //StMngmt.hdr.elmId.elmnt = STGEN;
  //StMngmt.t.cntrl.action = ASHUTDOWN;
  //StMngmt.t.cntrl.subAction = UNUSED;
  logger.logMsg (TRACE_FLAG, 0,"Leaving shutdownTcapLayer()");
  return BP_AIN_SM_OK;
}

int INGwSmCtlHdlr::shutdownSccpLayer(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,"Entering shutdownSccpLayer()");
  logger.logMsg (TRACE_FLAG, 0,"Leaving shutdownSccpLayer()");
  return BP_AIN_SM_OK;
}

int INGwSmCtlHdlr::shutdownMtp3Layer(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,"Entering shutdownMtp3Layer()");
  logger.logMsg (TRACE_FLAG, 0,"Leaving shutdownMtp3Layer()");
  return BP_AIN_SM_OK;
}

int INGwSmCtlHdlr::shutdownM3uaLayer(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,"Entering shutdownM3uaLayer()");
  // M3UA layer shutdown request
  //ItMngmt.hdr.elmId.elmnt = STITGEN;
  //ItMngmt.t.cntrl.action = ASHUTDOWN;
  //ItMngmt.t.cntrl.subAction = SAELMNT;
  logger.logMsg (TRACE_FLAG, 0,"Leaving shutdownM3uaLayer()");
  return BP_AIN_SM_OK;
}

int INGwSmCtlHdlr::shutdownSctpLayer(StackReqResp *stackReq)
{
  logger.logMsg (TRACE_FLAG, 0,"Entering shutdownSctpLayer()");
  // SCTP layer shutdown request
  //sbMgmt.t.cntrl.action = ASHUTDOWN;
  //sbMgmt.t.cntrl.subAction = UNUSED;
  logger.logMsg (TRACE_FLAG, 0,"Leaving shutdownSctpLayer()");
  return BP_AIN_SM_OK;
}



