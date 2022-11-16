//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");
/************************************************************************
Name:    INAP Stack Manager Configuration Handler - Impl

Type:    C file

Desc:    Configuration Handler Impl

File:    INGwSmCfgHdlr.C

Sid:     INGwSmCfgHdlr.C 0  -  03/27/03 

Prg:     gs

 ************************************************************************/

#ifdef SOLARIS
//#include "iph_conv.h"
#endif

#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
  /* header include files (.h) */
  #include "envopt.h"        /* environment options */
  #include "envdep.h"        /* environment dependent */
  #include "envind.h"        /* environment independent */
  
  #include "gen.h"           /* general layer */
  #include "ssi.h"           /* system services */
  #include "lsd.h"           /* layer management */
  
  
  /* header/EXTERN include files (.x) */
  #include "gen.x"           /* general layer */
  #include "ssi.x"           /* system services */
  #include "lsd.x"           /* layer management */
  #include "cm_ss7.x"
#ifndef __CCPU_CPLUSPLUS
}
#endif

#include "INGwStackManager/INGwSmWrapper.h"
#include "INGwStackManager/INGwSmCfgHdlr.h"
#include "INGwStackManager/INGwSmRepository.h"
#include "INGwInfraParamRepository/INGwIfrPrParamRepository.h"

#include "arpa/inet.h"
using namespace std;

int tcapLoDlgId;

extern void fillHdr(Header *hdr,U32 miTransId,U8 entId, U8 instId,U8 msgType,S16
 elmntId,S16 elInst1Id);

S16 cfgSdGen(U16, ProcId, Selector);
S16 cfgSdDLSAP(U16, S16, U16, U16, U8, U8, ProcId, Selector, Selector, U32);
S16 cfgSdDbg(int DbgMask, int action, ProcId dstProcId, Selector selsm2sd, U32 transId);

#if 0
#define MTP2_LINK_USAPID 4
#define MTP3_LINK_LSAPID 5
#define SCTP_ENDPOINT_LSAPID 9
#define SCTP_ENDPOINT_USAPID 10
#define M3UA_ENDPOINT_SAPID 11
#define TUCL_ENDPOINT_USAPID 12
#define DOWN 1
#define ONLINE 1
#define ROUTE_NSAPID 20
#define LSN_SW_ITU 2
#define MTP3_USERPART_SAPID 1
#define M3UA_USERPART_SAPID 2
#define SCCP_USERPART_SAPID 3
#define RTTYPE_PS 3
#define SCCP_SSN_SAPID 6
#define TCAP_SSN_USAPID 7
#define TCAP_SSN_LSAPID 8
#define SCTP_PROC_ID 8
#endif

void printZvRset(CmZvDvRsetMapCfg cfg, int type )
{
  logger.logMsg (TRACE_FLAG, 0,"rType.type = %d",cfg.rType.type);
  logger.logMsg (TRACE_FLAG, 0,"rType.qual = %d",cfg.rType.qual);
  if(type == BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL)
  {
    logger.logMsg (TRACE_FLAG, 0,"id = %d",cfg.u.crit.id);

  }
  if(type == BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL)
  {
    logger.logMsg (TRACE_FLAG, 0,"id = %d",cfg.u.def.id);
    
  }

  if(type == BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL)
  {
    logger.logMsg (TRACE_FLAG, 0,"ifType= %d",cfg.u.nonCrit.ifType);

    logger.logMsg (TRACE_FLAG, 0,"upper.type.dist= %d",cfg.u.nonCrit.upper.type.dist);
    logger.logMsg (TRACE_FLAG, 0,"upper.type.qual= %d",cfg.u.nonCrit.upper.type.qual);

    {
      logger.logMsg (TRACE_FLAG, 0,"upper.u.prefProc.range.start = %d-%u",
	cfg.u.nonCrit.upper.u.prefProc.range.start,
	&(cfg.u.nonCrit.upper.u.prefProc.range.start));
      logger.logMsg (TRACE_FLAG, 0,"upper.u.prefProc.range.end = %d-%u",
	cfg.u.nonCrit.upper.u.prefProc.range.end,
	&(cfg.u.nonCrit.upper.u.prefProc.range.end));
    }

    logger.logMsg (TRACE_FLAG, 0,"lower.type.dist= %d",cfg.u.nonCrit.lower.type.dist);
    logger.logMsg (TRACE_FLAG, 0,"lower.type.qual= %d",cfg.u.nonCrit.lower.type.qual);

    {
      logger.logMsg (TRACE_FLAG, 0,"lower.u.prefProc.range.start = %d-%u",
	cfg.u.nonCrit.lower.u.prefProc.range.start,
	&(cfg.u.nonCrit.lower.u.prefProc.range.start));
      logger.logMsg (TRACE_FLAG, 0,"lower.u.prefProc.range.end = %d-%u",
	cfg.u.nonCrit.lower.u.prefProc.range.end,
	&(cfg.u.nonCrit.lower.u.prefProc.range.end));
    }
  }

}

void printZnRset(CmZnDnRsetMapCfg cfg, int type )
{
  logger.logMsg (TRACE_FLAG, 0,"rType.type = %d",cfg.rType.type);
  logger.logMsg (TRACE_FLAG, 0,"rType.qual = %d",cfg.rType.qual);
  if(type == BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL)
  {
    logger.logMsg (TRACE_FLAG, 0,"id = %d",cfg.u.crit.id);

  }
  if(type == BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL)
  {
    logger.logMsg (TRACE_FLAG, 0,"id = %d",cfg.u.def.id);
    
  }

  if(type == BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL)
  {
    logger.logMsg (TRACE_FLAG, 0,"ifType= %d",cfg.u.nonCrit.ifType);

    logger.logMsg (TRACE_FLAG, 0,"upper.type.dist= %d",cfg.u.nonCrit.upper.type.dist);
    logger.logMsg (TRACE_FLAG, 0,"upper.type.qual= %d",cfg.u.nonCrit.upper.type.qual);

    if(cfg.u.nonCrit.upper.type.dist == CMFTHA_DIST_DYNAMIC )
    {
      logger.logMsg (TRACE_FLAG, 0,"upper.u.dyn.range.start = %d",cfg.u.nonCrit.upper.u.dyn.range.start);
      logger.logMsg (TRACE_FLAG, 0,"upper.u.dyn.range.end   = %d",cfg.u.nonCrit.upper.u.dyn.range.end);
      logger.logMsg (TRACE_FLAG, 0,"upper.u.dyn.keyVal.dpcEntries= %d",cfg.u.nonCrit.upper.u.dyn.keyVal.dpcEntries);
       for(int i=0;i<cfg.u.nonCrit.upper.u.dyn.keyVal.dpcEntries;i++)
      logger.logMsg (TRACE_FLAG, 0,"upper.u.dyn.keyVal.dpc[%d]= %d",i,cfg.u.nonCrit.upper.u.dyn.keyVal.dpc[i]);
      logger.logMsg (TRACE_FLAG, 0,"upper.u.dyn.keyVal.slsRange= %d",cfg.u.nonCrit.upper.u.dyn.keyVal.slsRange);
      logger.logMsg (TRACE_FLAG, 0,"upper.u.dyn.keyVal.ssfEntries= %d",cfg.u.nonCrit.upper.u.dyn.keyVal.ssfEntries);
       for(int i=0;i<cfg.u.nonCrit.upper.u.dyn.keyVal.ssfEntries;i++)
      logger.logMsg (TRACE_FLAG, 0,"upper.u.dyn.keyVal.ssf[%d]= %d",i,cfg.u.nonCrit.upper.u.dyn.keyVal.ssf[i]);
      logger.logMsg (TRACE_FLAG, 0,"upper.u.dyn.keyVal.siEntries= %d",cfg.u.nonCrit.upper.u.dyn.keyVal.siEntries);
       for(int i=0;i<cfg.u.nonCrit.upper.u.dyn.keyVal.siEntries;i++)
      logger.logMsg (TRACE_FLAG, 0,"upper.u.dyn.keyVal.si[%d]= %d",i,cfg.u.nonCrit.upper.u.dyn.keyVal.si[i]);
      logger.logMsg (TRACE_FLAG, 0,"upper.u.dyn.keyVal.varEntries= %d",cfg.u.nonCrit.upper.u.dyn.keyVal.varEntries);
       for(int i=0;i<cfg.u.nonCrit.upper.u.dyn.keyVal.varEntries;i++)
      logger.logMsg (TRACE_FLAG, 0,"upper.u.dyn.keyVal.var[%d]= %d",i,cfg.u.nonCrit.upper.u.dyn.keyVal.var[i]);


    }
    else
    {
      logger.logMsg (TRACE_FLAG, 0,"upper.u.sameProc.range.start = %d",cfg.u.nonCrit.upper.u.sameProc.range.start);
      logger.logMsg (TRACE_FLAG, 0,"upper.u.sameProc.range.end = %d",cfg.u.nonCrit.upper.u.sameProc.range.end);
    }

    logger.logMsg (TRACE_FLAG, 0,"lower.type.dist= %d",cfg.u.nonCrit.lower.type.dist);
    logger.logMsg (TRACE_FLAG, 0,"lower.type.qual= %d",cfg.u.nonCrit.lower.type.qual);

    if(cfg.u.nonCrit.lower.type.dist == CMFTHA_DIST_DYNAMIC )
    {
      logger.logMsg (TRACE_FLAG, 0,"lower.u.dyn.range.start = %d",cfg.u.nonCrit.lower.u.dyn.range.start);
      logger.logMsg (TRACE_FLAG, 0,"lower.u.dyn.range.end   = %d",cfg.u.nonCrit.lower.u.dyn.range.end);
      logger.logMsg (TRACE_FLAG, 0,"lower.u.dyn.keyVal.dpcEntries= %d",cfg.u.nonCrit.lower.u.dyn.keyVal.dpcEntries);
       for(int i=0;i<cfg.u.nonCrit.lower.u.dyn.keyVal.dpcEntries;i++)
      logger.logMsg (TRACE_FLAG, 0,"lower.u.dyn.keyVal.dpc[%d]= %d",i,cfg.u.nonCrit.lower.u.dyn.keyVal.dpc[i]);
      logger.logMsg (TRACE_FLAG, 0,"lower.u.dyn.keyVal.slsRange= %d",cfg.u.nonCrit.lower.u.dyn.keyVal.slsRange);
      logger.logMsg (TRACE_FLAG, 0,"lower.u.dyn.keyVal.ssfEntries= %d",cfg.u.nonCrit.lower.u.dyn.keyVal.ssfEntries);
       for(int i=0;i<cfg.u.nonCrit.lower.u.dyn.keyVal.ssfEntries;i++)
      logger.logMsg (TRACE_FLAG, 0,"lower.u.dyn.keyVal.ssf[%d]= %d",i,cfg.u.nonCrit.lower.u.dyn.keyVal.ssf[i]);
      logger.logMsg (TRACE_FLAG, 0,"lower.u.dyn.keyVal.siEntries= %d",cfg.u.nonCrit.lower.u.dyn.keyVal.siEntries);
       for(int i=0;i<cfg.u.nonCrit.lower.u.dyn.keyVal.siEntries;i++)
      logger.logMsg (TRACE_FLAG, 0,"lower.u.dyn.keyVal.si[%d]= %d",i,cfg.u.nonCrit.lower.u.dyn.keyVal.si[i]);
      logger.logMsg (TRACE_FLAG, 0,"lower.u.dyn.keyVal.varEntries= %d",cfg.u.nonCrit.lower.u.dyn.keyVal.varEntries);
       for(int i=0;i<cfg.u.nonCrit.lower.u.dyn.keyVal.varEntries;i++)
      logger.logMsg (TRACE_FLAG, 0,"lower.u.dyn.keyVal.var[%d]= %d",i,cfg.u.nonCrit.lower.u.dyn.keyVal.var[i]);

    }
    else
    {
      logger.logMsg (TRACE_FLAG, 0,"lower.u.sameProc.range.start = %d",cfg.u.nonCrit.lower.u.sameProc.range.start);
      logger.logMsg (TRACE_FLAG, 0,"lower.u.sameProc.range.end = %d",cfg.u.nonCrit.lower.u.sameProc.range.end);
    }
  }

}

void printRsetGenCfg(CmZnDnRsetGenCfg cfg)
{
    logger.logMsg (TRACE_FLAG, 0,"ifType= %d",cfg.ifType);


  
    logger.logMsg (TRACE_FLAG, 0,"upper.type.dist= %d",cfg.upper.type.dist);
    logger.logMsg (TRACE_FLAG, 0,"upper.type.qual= %d",cfg.upper.type.qual);

    logger.logMsg (TRACE_FLAG, 0,"upper.attr = %d",cfg.upper.attr);

    logger.logMsg (TRACE_FLAG, 0,"upper.max.sls= %d",cfg.upper.max.sls);
    logger.logMsg (TRACE_FLAG, 0,"upper.max.si = %d",cfg.upper.max.si);
    logger.logMsg (TRACE_FLAG, 0,"upper.max.var= %d",cfg.upper.max.var);
    logger.logMsg (TRACE_FLAG, 0,"upper.max.ssf= %d",cfg.upper.max.ssf);
    logger.logMsg (TRACE_FLAG, 0,"upper.max.dpc= %d",cfg.upper.max.dpc);
    logger.logMsg (TRACE_FLAG, 0,"upper.max.rsets= %d",cfg.upper.max.rsets);

    logger.logMsg (TRACE_FLAG, 0,"lower.type.dist= %d",cfg.lower.type.dist);
    logger.logMsg (TRACE_FLAG, 0,"lower.type.qual= %d",cfg.lower.type.qual);

    logger.logMsg (TRACE_FLAG, 0,"lower.attr = %d",cfg.lower.attr);

    logger.logMsg (TRACE_FLAG, 0,"lower.max.sls= %d",cfg.lower.max.sls);
    logger.logMsg (TRACE_FLAG, 0,"lower.max.si = %d",cfg.lower.max.si);
    logger.logMsg (TRACE_FLAG, 0,"lower.max.var= %d",cfg.lower.max.var);
    logger.logMsg (TRACE_FLAG, 0,"lower.max.ssf= %d",cfg.lower.max.ssf);
    logger.logMsg (TRACE_FLAG, 0,"lower.max.dpc= %d",cfg.lower.max.dpc);
    logger.logMsg (TRACE_FLAG, 0,"lower.max.rsets= %d",cfg.lower.max.rsets);

}

void printZnGenStruct(ZnMngmt zn)
{
 logger.logMsg (TRACE_FLAG, 0,"timeRes=%d",zn.t.cfg.s.genCfg.timeRes);
    logger.logMsg (TRACE_FLAG, 0,"tUpdCompAck.enb=%d",zn.t.cfg.s.genCfg.tUpdCompAck.enb);
    logger.logMsg (TRACE_FLAG, 0,"tUpdCompAck.val=%d",zn.t.cfg.s.genCfg.tUpdCompAck.val);
    logger.logMsg (TRACE_FLAG, 0,"maxUpdMsgSize=%d",zn.t.cfg.s.genCfg.maxUpdMsgSize);
    logger.logMsg (TRACE_FLAG, 0,"maxUpdMsgs=%d",zn.t.cfg.s.genCfg.maxUpdMsgs);
    logger.logMsg (TRACE_FLAG, 0,"priorCrit=%d",zn.t.cfg.s.genCfg.priorCrit);
    logger.logMsg (TRACE_FLAG, 0,"distEnv=%d",zn.t.cfg.s.genCfg.distEnv);
#ifdef LZNV2
    logger.logMsg (TRACE_FLAG, 0,"breakTmr=%d",zn.t.cfg.s.genCfg.breakTmr);
#endif
#ifdef ZN_DFTHA
    printRsetGenCfg(zn.t.cfg.s.genCfg.rsetGenCfg);

    logger.logMsg (TRACE_FLAG, 0,"txHeartbeat.enb=%d",zn.t.cfg.s.genCfg.txHeartbeat.enb);
    logger.logMsg (TRACE_FLAG, 0,"txHeartbeat.val=%d",zn.t.cfg.s.genCfg.txHeartbeat.val);

    logger.logMsg (TRACE_FLAG, 0,"rxHeartbeat.enb=%d",zn.t.cfg.s.genCfg.rxHeartbeat.enb);
    logger.logMsg (TRACE_FLAG, 0,"rxHeartbeat.val=%d",zn.t.cfg.s.genCfg.rxHeartbeat.val);

    logger.logMsg (TRACE_FLAG, 0,"tSync.enb=%d",zn.t.cfg.s.genCfg.tSync.enb);
    logger.logMsg (TRACE_FLAG, 0,"tSync.val=%d",zn.t.cfg.s.genCfg.tSync.val);
    logger.logMsg (TRACE_FLAG, 0,"priorNonCrit=%d",zn.t.cfg.s.genCfg.priorNonCrit);
#endif

}

void printSgEnt(SgMngmt sg)
{
    logger.logMsg (TRACE_FLAG, 0,"entId = %d",sg.t.hi.cfg.ent.entId);
    logger.logMsg (TRACE_FLAG, 0,"instId= %d",sg.t.hi.cfg.ent.instId);
    logger.logMsg (TRACE_FLAG, 0,"entType= %d",sg.t.hi.cfg.ent.entType);
    logger.logMsg (TRACE_FLAG, 0,"nmbCritical= %d",sg.t.hi.cfg.ent.nmbCritical);
    for(int i=0;i<sg.t.hi.cfg.ent.nmbCritical;i++)
    {
      logger.logMsg (TRACE_FLAG, 0,"critRsets[%d] = %d",i,sg.t.hi.cfg.ent.critRsets[i]);
    }
    logger.logMsg (TRACE_FLAG, 0,"tapaRsetId= %d",sg.t.hi.cfg.ent.tapaRsetId);
    logger.logMsg (TRACE_FLAG, 0,"nmbNonCritical= %d",sg.t.hi.cfg.ent.nmbNonCritical);
    for(int i=0;i<sg.t.hi.cfg.ent.nmbNonCritical;i++)
    {
      logger.logMsg (TRACE_FLAG, 0,"nonCritRsets[%d].restId = %d",i,sg.t.hi.cfg.ent.nonCritRsets[i].rsetId);
      logger.logMsg (TRACE_FLAG, 0,"nonCritRsets[%d].rsetGrp = %d",i,sg.t.hi.cfg.ent.nonCritRsets[i].rsetGrp);
    }
    logger.logMsg (TRACE_FLAG, 0,"nmbUsers= %d",sg.t.hi.cfg.ent.nmbUsers);
    for(int i=0;i<sg.t.hi.cfg.ent.nmbUsers;i++)
    {
    logger.logMsg (TRACE_FLAG, 0,"userList[%d]= %d",i,sg.t.hi.cfg.ent.userList[i]);
    }
    for(int i=0;i<LSG_MAX_ASSOC;i++)
    {
    logger.logMsg (TRACE_FLAG, 0,"userInst[%d]= %d",i,sg.t.hi.cfg.ent.userInst[i]);
    }
    logger.logMsg (TRACE_FLAG, 0,"nmbProv= %d",sg.t.hi.cfg.ent.nmbProv);
    for(int i=0;i<sg.t.hi.cfg.ent.nmbProv;i++)
    {
    logger.logMsg (TRACE_FLAG, 0,"provList[%d]= %d",i,sg.t.hi.cfg.ent.provList[i]);
    }
    for(int i=0;i<LSG_MAX_ASSOC;i++)
    {
      logger.logMsg (TRACE_FLAG, 0,"provInst[%d]= %d",i,sg.t.hi.cfg.ent.provInst[i]);
    }


}

/******************************************************************************
 *
 *     Fun:   INGwSmCfgHdlr()
 *
 *     Desc:  Default Contructor
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
INGwSmCfgHdlr::INGwSmCfgHdlr(INGwSmDistributor& arDist, int aiLayer, 
      int aiOper, int aiSubOp, int aiTransId):
    INGwSmReqHdlr (arDist, aiLayer),
    miOper (aiOper),
    miSubOp (aiSubOp)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmCfgHdlr::INGwSmCfgHdlr");

    miTransId = aiTransId;
    miIndex = -1;
    miCount = 0;
    mpDist = &arDist;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmCfgHdlr::INGwSmCfgHdlr");
}


/******************************************************************************
 *
 *     Fun:   INGwSmCfgHdlr()
 *
 *     Desc:  Default Destructor
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
INGwSmCfgHdlr::~INGwSmCfgHdlr()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::~INGwSmCfgHdlr",
        miTransId);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::~INGwSmCfgHdlr",
        miTransId);
}

/* Function to fill header structure */

#if 0
void fillHdr(Header *hdr, U32 miTransId, U8 entId, U8 instId, U8 msgType, S16 elmntId, S16 elInst1Id )
{
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
#endif
void AddRemoteSsnSpRteCfg(SpRteCfg *spRte, AddRemoteSsn *ssn)
{
   spRte->nmbSsns = 1;
   for (int cnt = 0; cnt < spRte->nmbSsns; cnt++)
   {
    spRte->ssnList[cnt].ssn = ssn->ssnList[cnt].ssn;
    spRte->ssnList[cnt].nmbBpc = ssn->ssnList[cnt].nmbBpc;
    for (int cnt1 = 0; cnt1 < spRte->ssnList[cnt].nmbBpc && cnt1 < MAXNUMSSN; cnt1++)
    {
     spRte->ssnList[cnt].bpcList[cnt1].bpc = ssn->ssnList[cnt].bpcList[cnt1].bpc;
     spRte->ssnList[cnt].bpcList[cnt1].prior = ssn->ssnList[cnt].bpcList[cnt1].prior;
    }
    spRte->ssnList[cnt].nmbConPc = ssn->ssnList[cnt].nmbConPc;
    for (int cnt1 = 0; cnt1 < spRte->ssnList[cnt].nmbConPc; cnt1++)
    {
     spRte->ssnList[cnt].conPc[cnt1] = ssn->ssnList[cnt].conPc[cnt1];
    }
   }
#if (SS7_ANS96 || SS7_BELL05)
   spRte->replicatedMode = ssn->replicatedMode;
#endif
   spRte->dpc = ssn->dpc;
}

void AddDpcSpRteCfg(SpRteCfg *spRtpe, AddRoute *dpc)
{
  logger.logMsg (TRACE_FLAG, 0,"Entering AddDpcSpRteCfg");
	char buf[4000];
	int bufLen=0;

	memset(&buf, 0, sizeof(buf));

  spRtpe->swtch = dpc->swtch;
  spRtpe->dpc = dpc->dpc;
  spRtpe->status = dpc->status;
#if (SS7_ANS96 || SS7_BELL05)
  spRtpe->replicatedMode = dpc->replicatedMode;
#endif
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96 || SS7_BELL05)
  unsigned char slsMask;
  int noOfBits = 0;
  char* slsMaskchar = getenv("ANSI_SLS_MASK"); //in millisec
  if(NULL != slsMaskchar) {
    noOfBits = atoi(slsMaskchar);
    if(noOfBits == 5)
      slsMask = 0x1F;
    else if(noOfBits == 8)
      slsMask = 0xFF;
    else
      slsMask = 0x1F;
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"ANSI_SLS_MASK"
    " slsMask for ANSI network %x\n",slsMask);
  }
  else
    slsMask = 0x1F;

  spRtpe->slsMask = slsMask;
#endif /* SS7_ANS88 || SS7_ANS92 || SS7_ANS96 || SS7_BELL05 */

  spRtpe->nmbBpc = dpc->nmbBpc;
  spRtpe->nmbSsns = dpc->nmbSsns;

	bufLen += sprintf(buf+bufLen, 
	"+Ver+AddDpcSpRteCfg swtch:%d, dpc:%d, status:%d, nmbBpc:%d, nmbSsns:%d",
	spRtpe->swtch, spRtpe->dpc, spRtpe->status,
	spRtpe->nmbBpc, spRtpe->nmbSsns);

  for(int i=0;i<spRtpe->nmbBpc;i++)
  {
    spRtpe->bpcList[i].bpc = dpc->bpcList[i].bpc;
    spRtpe->bpcList[i].prior = dpc->bpcList[i].prior;

		bufLen += sprintf(buf+bufLen, 
		"bpcList[%d] {bpc:%d, prior:%d},",i,
		spRtpe->bpcList[i].bpc,
		spRtpe->bpcList[i].prior);
  }


  for(int i=0;i<dpc->nmbSsns;i++)
  {
    logger.logMsg (TRACE_FLAG, 0,"AddDpcSpRteCfg: Adding SSN in the route");
    spRtpe->ssnList[i].ssn = dpc->ssnList[i].ssn;
    spRtpe->ssnList[i].status = dpc->ssnList[i].status;
    spRtpe->ssnList[i].nmbBpc = dpc->ssnList[i].nmbBpc;

		bufLen += sprintf(buf+bufLen, 
		"ssnList[%d]{ssn:%d, status:%d, nmbBpc:%d},",
		i, spRtpe->ssnList[i].ssn, spRtpe->ssnList[i].status,
		spRtpe->ssnList[i].nmbBpc);

    for(int j=0;j < spRtpe->ssnList[i].nmbBpc;j++)
    {
      spRtpe->ssnList[i].bpcList[j].bpc = dpc->ssnList[i].bpcList[j].bpc;
      spRtpe->ssnList[i].bpcList[j].prior = dpc->ssnList[i].bpcList[j].prior;

			bufLen += sprintf(buf+bufLen,
			" bpcList[%d]{bpc:%d, prior:%d}, ",
			j, spRtpe->ssnList[i].bpcList[j].bpc,
			spRtpe->ssnList[i].bpcList[j].prior);
    }

    spRtpe->ssnList[i].nmbConPc = dpc->ssnList[i].nmbConPc;

		bufLen += sprintf(buf+bufLen,
		" nmbConPc:%d, ", spRtpe->ssnList[i].nmbConPc);

    for(int j=0;j < spRtpe->ssnList[i].nmbConPc;j++)
    {
      spRtpe->ssnList[i].conPc[j] = dpc->ssnList[i].conPc[j];

			bufLen += sprintf(buf+bufLen,
			" conPc[%d]{conPc:%d}, ",
			j, spRtpe->ssnList[i].conPc[j]);
    }
  }
#ifdef LSPV2_7
  spRtpe->preferredOpc = dpc->preferredOpc;
#endif
  spRtpe->nSapId = dpc->nSapId;

	bufLen += sprintf(buf+bufLen,
	", sapId:%d", spRtpe->nSapId);

#ifdef LSPV3_1                  
  /* Dual Route configuration */
  spRtpe->defaultRoutenSapId = dpc->defaultRoutenSapId;
  spRtpe->secRteCfg          = dpc->secRteCfg;
  spRtpe->nSap1RteStatus     = dpc->nSap1RteStatus;
  spRtpe->nSapId2            = dpc->nSapId2;
  spRtpe->nSap2RteStatus     = dpc->nSap2RteStatus;

	bufLen += sprintf(buf+bufLen,
	", defaultRoutenSapId:%d, nSap1RteStatus:%d"
  ", secRteCfg:%d, nSapId2:%d, nSap2RteStatus:%d ",
  spRtpe->defaultRoutenSapId, spRtpe->nSap1RteStatus,
  spRtpe->secRteCfg, spRtpe->nSapId2, spRtpe->nSap2RteStatus);
#endif


	logger.logMsg(ALWAYS_FLAG, 0, "%s", buf);
}

int INGwSmCfgHdlr::cliAddRemoteSsn(AddRemoteSsn *ssn)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering cliAddRemoteSsn");
	
	SpMngmt &cntrl = l.sp;
	cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

	INGwSmRepository *lpRep = mrDist.getSmRepository ();

	if (lpRep == 0)
	{
		logger.logMsg (ERROR_FLAG, 0,
				"TID <%d> : Repository pointer is NULL",
				miTransId);
		return BP_AIN_SM_FAIL;
	}

	if (miIndex == -1)
		miIndex = 1;
	else
		miIndex++;

	int liRetVal = lpRep->getSpMngmt (BP_AIN_SM_SUBTYPE_RTECFG, l.sp, miIndex);

	//get the management structure from the DOM Tree
	if (liRetVal == BP_AIN_SM_FAIL)
	{
		logger.logMsg (ERROR_FLAG, 0,
				"TID <%d> : Management structure couldn't be retrieved from the Repository for index<%d>",
				miTransId, miIndex);
		return BP_AIN_SM_FAIL;
	}

 /* here we need to read the default values from the template xml */
    AddDpcSpRteCfg(&(l.sp.t.cfg.s.spRte),ssn);
    AddRemoteSsnSpRteCfg(&(l.sp.t.cfg.s.spRte),ssn);
 
    fillHdr(&(l.sp.hdr), miTransId, ENTSP, 0, TCFG, STROUT, 0);

 Pst *lmPst = lpRep->getPst (BP_AIN_SM_SCC_LAYER);
 lmPst->event = EVTLSPCFGREQ;

 SmMiLspCfgReq(lmPst, &cntrl);
 logger.logMsg (TRACE_FLAG, 0,"Leaving  cliAddRemoteSsn");
        return BP_AIN_SM_OK;
}


int INGwSmCfgHdlr::addGtrule (StackReqResp *stackReq)
{
   logger.logMsg (TRACE_FLAG, 0,"Entering addGtrule");

	 char buf[6000];
	 int bufLen =0;
	 memset(&buf, 0, sizeof(buf));

	 bufLen += sprintf(buf+bufLen, "+VER addGtrule ");

   INGwSmRepository *lpRep = mrDist.getSmRepository ();

   if (lpRep == 0)
   {
      logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Repository pointer is NULL", miTransId);
      return BP_AIN_SM_FAIL;
   }
   
   //initialize the header
   Header *lpHdr = &(l.sp.hdr);
   lpHdr->msgType     = TCFG;
   lpHdr->entId.ent   = ENTSP;
   lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
   lpHdr->elmId.elmnt = STASSO;
   lpHdr->transId     = miTransId;

   lpHdr->response.prior      = BP_AIN_SM_PRIOR;
   lpHdr->response.route      = BP_AIN_SM_ROUTE;
   lpHdr->response.mem.region = BP_AIN_SM_REGION;
   lpHdr->response.mem.pool   = BP_AIN_SM_POOL;
   lpHdr->response.selector   = BP_AIN_SM_COUPLING;

   Pst *smPst = lpRep->getPst (BP_AIN_SM_SCC_LAYER);
   smPst->event = EVTLSPCFGREQ;
   smPst->dstProcId = stackReq->procId;
   smPst->srcProcId = SFndProcId();

   /* update data recieved from the user */
   SpAssoCfg *spAsso = &(l.sp.t.cfg.s.spAsso);
#ifdef GTT_PER_NWK
   spAsso->nwId = stackReq->req.u.addGtRule.nwId;
	 bufLen += sprintf(buf+bufLen, "nwId:%d, ", spAsso->nwId);
#endif 
  spAsso->rule.sw = stackReq->req.u.addGtRule.sw;
   spAsso->rule.formatPres = stackReq->req.u.addGtRule.formatPres;
   spAsso->rule.format = stackReq->req.u.addGtRule.format;

	 bufLen += sprintf(buf+bufLen, " sw:%d, formatPres:%d format:%d, ", 
	 spAsso->rule.sw, spAsso->rule.formatPres, spAsso->rule.format);

   switch(spAsso->rule.format)
   {
      case 1:
         spAsso->rule.gt.f1.oddEvenPres = stackReq->req.u.addGtRule.oddEvenPres;
         spAsso->rule.gt.f1.oddEven = stackReq->req.u.addGtRule.oddEven;
         spAsso->rule.gt.f1.natAddrPres = stackReq->req.u.addGtRule.natAddrPres;
         spAsso->rule.gt.f1.natAddr = stackReq->req.u.addGtRule.natAddr;

	 			 bufLen += sprintf(buf+bufLen, " gt{oddEvenPres:%d, oddEven:%d, natAddrPres:%d, natAddr:%d}",
				 spAsso->rule.gt.f1.oddEvenPres, spAsso->rule.gt.f1.oddEven,
				 spAsso->rule.gt.f1.natAddrPres, spAsso->rule.gt.f1.natAddr);

         break;
      case 2:
         spAsso->rule.gt.f2.tTypePres = stackReq->req.u.addGtRule.tTypePres;
         spAsso->rule.gt.f2.tType = stackReq->req.u.addGtRule.tType;

	 			 bufLen += sprintf(buf+bufLen, " gt{tTypePres:%d, tType:%d}",
				 spAsso->rule.gt.f2.tTypePres, spAsso->rule.gt.f2.tType);

         break;
      case 3:
         spAsso->rule.gt.f3.tTypePres = stackReq->req.u.addGtRule.tTypePres;
         spAsso->rule.gt.f3.tType = stackReq->req.u.addGtRule.tType;
         spAsso->rule.gt.f3.numPlanPres = stackReq->req.u.addGtRule.numPlanPres;
         spAsso->rule.gt.f3.numPlan = stackReq->req.u.addGtRule.numPlan;
         spAsso->rule.gt.f3.encSchPres = stackReq->req.u.addGtRule.encSchPres;
         spAsso->rule.gt.f3.encSch = stackReq->req.u.addGtRule.encSch;

	 			 bufLen += sprintf(buf+bufLen, 
				 " gt{tTypePres:%d, tType:%d, numPlanPres:%d, numPlan:%d, encSchPres:%d, encSch:%d}",
         spAsso->rule.gt.f3.tTypePres, spAsso->rule.gt.f3.tType,
         spAsso->rule.gt.f3.numPlanPres, spAsso->rule.gt.f3.numPlan,
         spAsso->rule.gt.f3.encSchPres, spAsso->rule.gt.f3.encSch);

         break;
      case 4:
         spAsso->rule.gt.f4.tTypePres = stackReq->req.u.addGtRule.tTypePres;
         spAsso->rule.gt.f4.tType = stackReq->req.u.addGtRule.tType;
         spAsso->rule.gt.f4.numPlanPres = stackReq->req.u.addGtRule.numPlanPres;
         spAsso->rule.gt.f4.numPlan = stackReq->req.u.addGtRule.numPlan;
         spAsso->rule.gt.f4.encSchPres = stackReq->req.u.addGtRule.encSchPres;
         spAsso->rule.gt.f4.encSch = stackReq->req.u.addGtRule.encSch;
         spAsso->rule.gt.f4.natAddrPres = stackReq->req.u.addGtRule.natAddrPres;
         spAsso->rule.gt.f4.natAddr = stackReq->req.u.addGtRule.natAddr;

	 			 bufLen += sprintf(buf+bufLen, 
				 " gt{tTypePres:%d, tType:%d, numPlanPres:%d, numPlan:%d, encSchPres:%d, encSch:%d, natAddrPres:%d, natAddr:%d}",
         spAsso->rule.gt.f4.tTypePres, spAsso->rule.gt.f4.tType,
         spAsso->rule.gt.f4.numPlanPres, spAsso->rule.gt.f4.numPlan,
         spAsso->rule.gt.f4.encSchPres, spAsso->rule.gt.f4.encSch,
         spAsso->rule.gt.f4.natAddrPres, spAsso->rule.gt.f4.natAddr);

         break;
   }
   spAsso->nmbActns = stackReq->req.u.addGtRule.nmbActns;

	 bufLen += sprintf(buf+bufLen, ", nmbActns:%d, ",  spAsso->nmbActns);

   for (int i=0; i < spAsso->nmbActns; i++)
   {
      spAsso->actn[i].type = stackReq->req.u.addGtRule.actn[i].type;
      spAsso->actn[i].param.range.startDigit = stackReq->req.u.addGtRule.actn[i].startDigit;
      spAsso->actn[i].param.range.endDigit = stackReq->req.u.addGtRule.actn[i].endDigit;

	 		bufLen += sprintf(buf+bufLen, " action[%d] {type:%d, startDigit:%d, endDigit:%d},",
			i, spAsso->actn[i].type, spAsso->actn[i].param.range.startDigit,
      spAsso->actn[i].param.range.endDigit);

   }
   /* This is the only parameter that can have default value.
    * so insted of reading the template, we are hardcoding it*/
   spAsso->fSetType = 1;

   SmMiLspCfgReq (smPst, &l.sp);
    
   stackReq->resp.procId = stackReq->procId;
   stackReq->txnType = NORMAL_TXN;
   stackReq->txnStatus = INPROGRESS;

   mrDist.updateRspStruct(miTransId,stackReq);

	 logger.logMsg(ALWAYS_FLAG, 0, "%s", buf);
   logger.logMsg (TRACE_FLAG, 0," Leaving addGtrule");
        return BP_AIN_SM_OK;
}

#if 0
/*
*
*       Fun:    gttHexAddrToBcd
*
*       Desc:   Converts Ascii (hexadecimal format) Address to BCD
*
*       Ret:    ROK  - ok
*
*       Notes:  None
*
*
*/

PUBLIC S16 gttHexAddrToBcd
(
LngAddrs *inpBuf,        /* ascii buffer */
ShrtAddrs *bcdBuf        /* bcd buffer */
)
{
   U8 c;
   U8 i;
   U8 *src;
   U8 *dst;
   S16 d;
  
   src = inpBuf->strg;
   dst = bcdBuf->strg;

   /* sanity check */
   if (inpBuf->length > LNGADRLEN)
      RETVALUE(RFAILED);

   for (i = inpBuf->length; i; i--)
   {
      d = 0;
      if (!cmIsANumber(&d, (c = *src++), (S16) BASE16))
         RETVALUE(RFAILED);
      *dst = (U8) d;    /* The first digit */
      i--;
      if (!i)
         break;
      if (!cmIsANumber(&d, c = *src++, (S16) BASE16))
         RETVALUE(RFAILED);
      *dst++ |= (U8) (d << 4);    /* The second digit */
   }
   bcdBuf->length = ( (inpBuf->length % 2) ?
      (U8)((U8)(inpBuf->length + 1)/2) : (U8)(inpBuf->length/2));
   RETVALUE(ROK);
} /* gttHexAddrToBcd */
#endif



int INGwSmCfgHdlr::addGtAddrMap (StackReqResp *stackReq)
{
   LngAddrs  tmpBuf;

	 char buf[8000];
	 int bufLen =0;
	 memset(&buf, 0, sizeof(buf));

   logger.logMsg (TRACE_FLAG, 0,"Entering addGtAddrMap");

   INGwSmRepository *lpRep = mrDist.getSmRepository ();

   if (lpRep == 0)
   {
      logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Repository pointer is NULL", miTransId);
      return BP_AIN_SM_FAIL;
   }
   
   //initialize the header
   Header *lpHdr = &(l.sp.hdr);
	 memset(&l.sp.hdr, 0, sizeof(l.sp.hdr));

   lpHdr->msgType     = TCFG;
   lpHdr->entId.ent   = ENTSP;
   lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
   lpHdr->elmId.elmnt = STADRMAP;
   lpHdr->transId     = miTransId;

   lpHdr->response.prior      = BP_AIN_SM_PRIOR;
   lpHdr->response.route      = BP_AIN_SM_ROUTE;
   lpHdr->response.mem.region = BP_AIN_SM_REGION;
   lpHdr->response.mem.pool   = BP_AIN_SM_POOL;
   lpHdr->response.selector   = BP_AIN_SM_COUPLING;


   /* update data recieved from the user */
   SpAddrMapCfg *spAddrMap = &(l.sp.t.cfg.s.spAddrMap);
   memset(&(l.sp.t.cfg.s.spAddrMap), 0, sizeof(l.sp.t.cfg.s.spAddrMap));

	 bufLen += sprintf(buf+bufLen, "+VER addGtAddrMap");

#ifdef GTT_PER_NWK 
   spAddrMap->nwId = stackReq->req.u.addAddrMapCfg.nwkId;
	 bufLen += sprintf(buf+bufLen, "nwId:%d, ", spAddrMap->nwId );
#endif
   spAddrMap->actn.type = stackReq->req.u.addAddrMapCfg.actn.type;
   spAddrMap->actn.param.range.startDigit = stackReq->req.u.addAddrMapCfg.actn.startDigit;
   spAddrMap->actn.param.range.endDigit = stackReq->req.u.addAddrMapCfg.actn.endDigit;
   spAddrMap->sw = stackReq->req.u.addAddrMapCfg.sw;
   spAddrMap->replGt = stackReq->req.u.addAddrMapCfg.replGt;
   /* This is the only parameter that can have default value.
    * so insted of reading the template, we are hardcoding it*/
   spAddrMap->noCplng = 0;
   spAddrMap->gt.format = stackReq->req.u.addAddrMapCfg.format;

	 bufLen += sprintf(buf+bufLen,
	 " actn{type:%d, startDigit:%d, endDigit:%d}, sw:%d, replGt:%d, gt{format:%d, ",
		spAddrMap->actn.type, spAddrMap->actn.param.range.startDigit,
		spAddrMap->actn.param.range.endDigit, spAddrMap->sw,
		spAddrMap->replGt, spAddrMap->gt.format);

   switch(spAddrMap->gt.format)
   {
      case 1:
         spAddrMap->gt.gt.f1.oddEven = stackReq->req.u.addAddrMapCfg.oddEven;
         spAddrMap->gt.gt.f1.natAddr = stackReq->req.u.addAddrMapCfg.natAddr;
	 			 bufLen += sprintf(buf+bufLen, "oddEven:%d, natAddr:%d}",
					spAddrMap->gt.gt.f1.oddEven, spAddrMap->gt.gt.f1.natAddr);
         break;
      case 2:
         spAddrMap->gt.gt.f2.tType = stackReq->req.u.addAddrMapCfg.tType;
	 			 bufLen += sprintf(buf+bufLen, "tType:%d}",
					spAddrMap->gt.gt.f2.tType);
         break;
      case 3:
         spAddrMap->gt.gt.f3.tType = stackReq->req.u.addAddrMapCfg.tType;
         spAddrMap->gt.gt.f3.numPlan = stackReq->req.u.addAddrMapCfg.numPlan;
         spAddrMap->gt.gt.f3.encSch = stackReq->req.u.addAddrMapCfg.encSch;
	 			 bufLen += sprintf(buf+bufLen, "ttype:%d, numPlan:%d, encSch:%d}",
				 spAddrMap->gt.gt.f3.tType, spAddrMap->gt.gt.f3.numPlan, 
				 spAddrMap->gt.gt.f3.encSch);
				
         break;
      case 4:
         spAddrMap->gt.gt.f4.tType = stackReq->req.u.addAddrMapCfg.tType;
         spAddrMap->gt.gt.f4.numPlan = stackReq->req.u.addAddrMapCfg.numPlan;
         spAddrMap->gt.gt.f4.encSch = stackReq->req.u.addAddrMapCfg.encSch;
         spAddrMap->gt.gt.f4.natAddr = stackReq->req.u.addAddrMapCfg.natAddr;
	 			 bufLen += sprintf(buf+bufLen, "ttype:%d, numPlan:%d, encSch:%d, natAddr:%d}",
				 spAddrMap->gt.gt.f4.tType, spAddrMap->gt.gt.f4.numPlan, 
				 spAddrMap->gt.gt.f4.encSch, spAddrMap->gt.gt.f4.natAddr);
         break;
   }

   tmpBuf.length = stackReq->req.u.addAddrMapCfg.gtDigLen;
   memcpy ((void*) tmpBuf.strg, stackReq->req.u.addAddrMapCfg.gtDigits, 
           stackReq->req.u.addAddrMapCfg.gtDigLen);

   if(0 != lpRep->gttHexAddrToBcd (&tmpBuf, &spAddrMap->gt.addr))
   {
		logger.logMsg (ERROR_FLAG, 0,
         "addGtAddrMap: Error converting Address Digits to BCD: DigLen[%d]",
			tmpBuf.length); 
 		bufLen += sprintf(buf+bufLen, "GTTHexAddr Conversion Failed for:%s-%d",
		(stackReq->req.u.addAddrMapCfg.gtDigits != NULL)?(char*)stackReq->req.u.addAddrMapCfg.gtDigits:"NULL", 
		stackReq->req.u.addAddrMapCfg.gtDigLen);
   }
	 else {
 		bufLen += sprintf(buf+bufLen, "GTTHexAddr: Len:%d, ", spAddrMap->gt.addr.length);
		for(int i=0; i < spAddrMap->gt.addr.length; ++i) 
		{
 			bufLen += sprintf(buf+bufLen, "%02X:", spAddrMap->gt.addr.strg[i]);
		}
	 }
   
   spAddrMap->mode = stackReq->req.u.addAddrMapCfg.mode;
	bufLen += sprintf(buf+bufLen, ", mode:%d", spAddrMap->mode);
#ifdef GTT_PER_NWK
   spAddrMap->outNwId = stackReq->req.u.addAddrMapCfg.outNwId;
	 bufLen += sprintf(buf+bufLen, ", outNwId:%d", spAddrMap->outNwId);
#endif
   spAddrMap->numEntity = stackReq->req.u.addAddrMapCfg.numEntity;

	 bufLen += sprintf(buf+bufLen, ", numEntity:%d", spAddrMap->numEntity);

   for (int i=0; i < spAddrMap->numEntity; i++)
   {
      spAddrMap->outAddr[i].spHdrOpt = stackReq->req.u.addAddrMapCfg.outAddr[i].spHdrOpt;
      spAddrMap->outAddr[i].pres = TRUE;
      spAddrMap->outAddr[i].sw = stackReq->req.u.addAddrMapCfg.outAddr[i].swtch;
      spAddrMap->outAddr[i].ssfPres = TRUE;
      spAddrMap->outAddr[i].ssf = stackReq->req.u.addAddrMapCfg.outAddr[i].ssf;
      spAddrMap->outAddr[i].niInd = stackReq->req.u.addAddrMapCfg.outAddr[i].niInd;
      spAddrMap->outAddr[i].rtgInd = stackReq->req.u.addAddrMapCfg.outAddr[i].rtgInd;
      spAddrMap->outAddr[i].ssnInd = stackReq->req.u.addAddrMapCfg.outAddr[i].ssnInd;
      spAddrMap->outAddr[i].pcInd = stackReq->req.u.addAddrMapCfg.outAddr[i].pcInd;
      spAddrMap->outAddr[i].ssn = stackReq->req.u.addAddrMapCfg.outAddr[i].ssn;
      spAddrMap->outAddr[i].pc = stackReq->req.u.addAddrMapCfg.outAddr[i].pc;
      spAddrMap->outAddr[i].gt.format = stackReq->req.u.addAddrMapCfg.outAddr[i].format;

	 		bufLen += sprintf(buf+bufLen, " outAddr[%d]{spHdrOpt:%d, sw:%d, ssf:%d, niInd:%d, rtgInd:%d, ssnInd:%d, pcInd:%d, ssn:%d, pc:%d format:%d ", i, 
      spAddrMap->outAddr[i].spHdrOpt, spAddrMap->outAddr[i].sw,
      spAddrMap->outAddr[i].ssf, spAddrMap->outAddr[i].niInd ,
      spAddrMap->outAddr[i].rtgInd, spAddrMap->outAddr[i].ssnInd,
      spAddrMap->outAddr[i].pcInd, spAddrMap->outAddr[i].ssn , 
			spAddrMap->outAddr[i].pc, spAddrMap->outAddr[i].gt.format);

		  logger.logMsg (TRACE_FLAG, 0, "outAddr[i].gt.format<%d>", spAddrMap->outAddr[i].gt.format);

      switch(spAddrMap->outAddr[i].gt.format)
      {
         case 1:
            spAddrMap->outAddr[i].gt.gt.f1.oddEven = stackReq->req.u.addAddrMapCfg.outAddr[i].oddEven;
            spAddrMap->outAddr[i].gt.gt.f1.natAddr = stackReq->req.u.addAddrMapCfg.outAddr[i].natAddr;

	 					bufLen += sprintf(buf+bufLen, ", gt{format:%d, oddEven:%d, natAddr:%d}",
						spAddrMap->outAddr[i].gt.format, spAddrMap->outAddr[i].gt.gt.f1.oddEven,
						spAddrMap->outAddr[i].gt.gt.f1.natAddr);
         break;
         case 2:
            spAddrMap->outAddr[i].gt.gt.f2.tType = stackReq->req.u.addAddrMapCfg.outAddr[i].tType;

	 					bufLen += sprintf(buf+bufLen, ", gt{format:%d, tType:%d}",
						spAddrMap->outAddr[i].gt.format, spAddrMap->outAddr[i].gt.gt.f2.tType);

         break;
         case 3:
            spAddrMap->outAddr[i].gt.gt.f3.tType = stackReq->req.u.addAddrMapCfg.outAddr[i].tType;
            spAddrMap->outAddr[i].gt.gt.f3.numPlan = stackReq->req.u.addAddrMapCfg.outAddr[i].numPlan;
            spAddrMap->outAddr[i].gt.gt.f3.encSch = stackReq->req.u.addAddrMapCfg.outAddr[i].encSch;
	 					bufLen += sprintf(buf+bufLen, ", gt{format:%d, tType:%d, numPlan:%d, encSch:%d}",
						spAddrMap->outAddr[i].gt.format, spAddrMap->outAddr[i].gt.gt.f3.tType,
						spAddrMap->outAddr[i].gt.gt.f3.numPlan, spAddrMap->outAddr[i].gt.gt.f3.encSch);
         break;
         case 4:
            spAddrMap->outAddr[i].gt.gt.f4.tType = stackReq->req.u.addAddrMapCfg.outAddr[i].tType;
            spAddrMap->outAddr[i].gt.gt.f4.numPlan = stackReq->req.u.addAddrMapCfg.outAddr[i].numPlan;
            spAddrMap->outAddr[i].gt.gt.f4.encSch = stackReq->req.u.addAddrMapCfg.outAddr[i].encSch;
            spAddrMap->outAddr[i].gt.gt.f4.natAddr = stackReq->req.u.addAddrMapCfg.outAddr[i].natAddr;

	 					bufLen += sprintf(buf+bufLen, ", gt{format:%d, tType:%d, numPlan:%d, encSch:%d, natAddr:%d}",
						spAddrMap->outAddr[i].gt.format, spAddrMap->outAddr[i].gt.gt.f4.tType,
						spAddrMap->outAddr[i].gt.gt.f4.numPlan, spAddrMap->outAddr[i].gt.gt.f4.encSch,
						spAddrMap->outAddr[i].gt.gt.f4.natAddr);
         break;
      }

  	tmpBuf.length = stackReq->req.u.addAddrMapCfg.outAddr[i].gtDigLen;
      memcpy ((void*) tmpBuf.strg, stackReq->req.u.addAddrMapCfg.outAddr[i].gtDigits, 
              stackReq->req.u.addAddrMapCfg.outAddr[i].gtDigLen);

   	if(0 != lpRep->gttHexAddrToBcd (&tmpBuf, &spAddrMap->outAddr[i].gt.addr))
   	{
		logger.logMsg (ERROR_FLAG, 0,
         "addGtAddrMap: Error converting Address Digits to BCD: DigLen[%d]",
			tmpBuf.length); 
			bufLen += sprintf(buf+bufLen, "OutAddr HEXAddrToBcd Failed");
   	}
		else 
		{
			bufLen += sprintf(buf+bufLen, "OutAddr: ");
			for(int i=0; i < spAddrMap->outAddr[i].gt.addr.length; ++i) 
			{
 				bufLen += sprintf(buf+bufLen, "%02X:", spAddrMap->outAddr[i].gt.addr.strg[i]);
			}
		}
   }

   Pst *smPst = lpRep->getPst (BP_AIN_SM_SCC_LAYER);
   smPst->event = EVTLSPCFGREQ;
   smPst->dstProcId = stackReq->procId;
   smPst->srcProcId = SFndProcId();

   SmMiLspCfgReq (smPst, &l.sp);

   stackReq->resp.procId = stackReq->procId;
   stackReq->txnType = NORMAL_TXN;
   stackReq->txnStatus = INPROGRESS;

   mrDist.updateRspStruct(miTransId,stackReq);

   logger.logMsg (TRACE_FLAG, 0," Leaving addGtAddrMap");
	 logger.logMsg(ALWAYS_FLAG, 0, "%s", buf);
   return BP_AIN_SM_OK;
}

#if 0
int  INGwSmCfgHdlr::cliAddLink(AddLink *addlink)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering cliAddLink");
 mtp3LSapCfg(addlink);
 mtp2DLSapCfg(addlink);
 logger.logMsg (TRACE_FLAG, 0,"leaving  cliAddLink");
   return BP_AIN_SM_OK;
}
#endif



#if 0
int INGwSmCfgHdlr::cliNodeStatus(NodeStatus *node)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering cliNodeStatus");
        INGwSmRepository *mpRep = mrDist.getSmRepository();
 SgMngmt &cntrl = l.sg;
 cmMemset((U8 *)&cntrl, 0, sizeof(SgMngmt));

    fillHdr(&(l.sg.hdr), miTransId, ENTSG, 0, TSSTA, STSGENT, 0);

 cntrl.t.hi.ssta.entId = node->entId;
 cntrl.t.hi.ssta.instId = node->instId;
 cntrl.t.hi.ssta.procId = node->procId;

 Pst *lmPst = mpRep->getPst (BP_AIN_SM_SG_LAYER);
 lmPst->event = EVTLSGSTAREQ;

 SmMiLsgStaReq(lmPst, &cntrl);
    /* update the response structure */
 logger.logMsg (TRACE_FLAG, 0,"Leaving  cliNodeStatus");

   return BP_AIN_SM_OK;
}
/* Function to get Route Status */
int INGwSmCfgHdlr::cliRouteStatus(RouteStatus *dpc)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering cliRouteStatus");
        INGwSmRepository *mpRep = mrDist.getSmRepository();
 SpMngmt &cntrl = l.sp;
 cmMemset((U8 *)&cntrl, 0, sizeof(SpMngmt));

    fillHdr(&(l.sp.hdr), miTransId, ENTSP, 0, TSSTA, STROUT, 0);

 cntrl.t.ssta.s.spRteSta.pcSta.pc = dpc->dpc;
 cntrl.t.ssta.s.spRteSta.pcSta.nwId = dpc->nwkId;

 Pst *lmPst = mpRep->getPst (BP_AIN_SM_SCC_LAYER);
 lmPst->event = EVTLSPSTAREQ;

/* SmMiLspStaReq(lmPst, &cntrl);*/
    /* update the response structure */
 logger.logMsg (TRACE_FLAG, 0,"Leaving  cliRouteStatus");
   return BP_AIN_SM_OK;
}



/* Function to get MTP2 Link status */
int INGwSmCfgHdlr::mtp2LnkStatus(LinkStatus *lnk)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering mtp2LnkStatus");
        INGwSmRepository *mpRep = mrDist.getSmRepository();
 SdMngmt &cntrl = l.sd;
 cmMemset((U8 *)&cntrl, 0, sizeof(SdMngmt));

    fillHdr(&(l.sd.hdr), miTransId, ENTSD, 0, TSSTA, STDLSAP, lnk->mtp2UsapId);

 Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
 lmPst->event = EVTLSDSTAREQ;

 /*SmMiLsdStaReq(lmPst, &cntrl);*/
 /*here we need to update the response structure */
 logger.logMsg (TRACE_FLAG, 0,"Leaving  mtp2LnkStatus");
   return BP_AIN_SM_OK;
}

/* Function to get MTP3 Link status */
int INGwSmCfgHdlr::mtp3LnkStatus(LinkStatus *lnk)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering mtp3LnkStatus");
        INGwSmRepository *mpRep = mrDist.getSmRepository();
 SnMngmt &cntrl = l.sn;
 cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));

    fillHdr(&(l.sn.hdr), miTransId, ENTSN, 0, TSSTA, STDLSAP, lnk->mtp3LsapId);
 //cntrl.hdr.elmId.elmntInst2 = 0xff;

 cntrl.t.cntrl.action = ADELLNK;
 cntrl.t.cntrl.subAction = SAELMNT;

 Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
 lmPst->event = EVTLSNSTAREQ;

 /*smMiLsnCntrlReq(lmPst, &cntrl);*/
 /* update the response structure */
 logger.logMsg (TRACE_FLAG, 0,"Leaving  mtp3LnkStatus");
   return BP_AIN_SM_OK;
}

/* Function to get Link status */
int INGwSmCfgHdlr::cliLinkStatus(LinkStatus *lnk)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering cliLinkStatus");
 if(lnk->layer == LINK_MTP3)
 {
  mtp3LnkStatus(lnk);
 }
 else
 {
  mtp2LnkStatus(lnk);
 }
 logger.logMsg (TRACE_FLAG, 0,"Leaving  cliLinkStatus");
   return BP_AIN_SM_OK;
}


/* Function to get LinkStatus */
int INGwSmCfgHdlr::cliLinkSetStatus(LinkSetStatus *lnkSet)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering cliLinkSetStatus");
        INGwSmRepository *mpRep = mrDist.getSmRepository();
 SnMngmt &cntrl = l.sn;
 cmMemset((U8 *)&cntrl, 0, sizeof(SnMngmt));

 fillHdr(&(l.sg.hdr), miTransId, ENTSN, 0, TSSTA, STLNKSET, 0);
 cntrl.hdr.elmId.elmntInst2 = lnkSet->cmbLnkSetId;

 cntrl.t.cntrl.action = ADELLNKSET;
 cntrl.t.cntrl.subAction = SAELMNT;

 Pst *lmPst = mpRep->getPst (BP_AIN_SM_MTP3_LAYER);
 lmPst->event = EVTLSNSTAREQ;

 /*SmMiLsnStaReq(lmPst, &cntrl);*/
    /* update the response structure */
 logger.logMsg (TRACE_FLAG, 0,"Leaving  cliLinkSetStatus");
   return BP_AIN_SM_OK;

}
#endif
#if 0
/* Function to add link set */
int INGwSmCfgHdlr::cliAddLinkSet(AddLinkSet *lnkSet)
{
 logger.logMsg (TRACE_FLAG, 0,"Entering cliAddLinkSet");
 mtp3LinkSetCfg(lnkSet);
 logger.logMsg (TRACE_FLAG, 0,"Leaving  cliAddLinkSet");
   return BP_AIN_SM_OK;
}
#endif

/* Function to add Route */
int INGwSmCfgHdlr::cliAddRoute(AddRoute *dpc)
{
 sccpRouteCfg(&(mpQueMsg->t.stackData));
 mtp3RouteCfg(&(mpQueMsg->t.stackData));
   return BP_AIN_SM_OK;
}

#if 0
/* Function to add user part */
int INGwSmCfgHdlr::cliAddUserPart(AddUserPart *up)
{
 sccpLSapCfg(up);
 mtp3USapCfg (up);
    m3uaUSapCfg(up);
   return BP_AIN_SM_OK;

}


/* Function to add PS */
int INGwSmCfgHdlr::cliAddPs(AddPs *ps)
{
 m3uaRouteEntryCfg(ps);
 m3uaPSCfg(ps);
   return BP_AIN_SM_OK;
}

/* function to add PSP */
int INGwSmCfgHdlr::cliAddPsp(AddPsp *psp)
{
 m3uaPSPCfg (psp);
   return BP_AIN_SM_OK;
}


int INGwSmCfgHdlr::cliAddNetwork( AddNetwork *nwk)
{
 m3uaNwCfg(nwk);
 sccpNwCfg(nwk);
   return BP_AIN_SM_OK;
}


/* Function add LocalSSN */
int INGwSmCfgHdlr::cliAddLocalSsn(AddLocalSsn *ssn)
{
   sccpUSapCfg(ssn);
   tcapLSapCfg(ssn);
   return BP_AIN_SM_OK;
}


/* Function to Add Endpoint */
int INGwSmCfgHdlr::cliAddEndPoint(AddEndPoint *ep)
{
   m3uaLSapCfg(ep);
   sctpUSapCfg(ep);
   sctpLSapCfg(ep);
   tuclUSapCfg(ep);
   return BP_AIN_SM_OK;
}

#endif

/******************************************************************************
 *
 *     Fun:   sendRequest()
 *
 *     Desc:  send the request to the Stack
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sendRequest(INGwSmQueueMsg *apQueMsg,INGwSmRequestContext *apContext)
{
 logger.logMsg (TRACE_FLAG, 0,
   "TID <%d> : Entering INGwSmCfgHdlr::sendRequest()",
   miTransId);

  int retVal = 0;
  if(apQueMsg)
     mpQueMsg = apQueMsg;
    

 switch (miLayer)
 {
#if 0
  case BP_AIN_SM_AIN_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Starting INAP General Configuration",
         miTransId);

       if (inapGenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : INAP General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_USPCFG:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Starting INAP Upper SAP Configuration",
         miTransId);

       if (inapUSapCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : INAP Upper SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_LSPCFG:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Starting INAP Lower SAP Configuration",
         miTransId);

       int liRetVal = inapLSapCfg ();
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : INAP Lower SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : INAP Route Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
  #endif
  case BP_AIN_SM_TCA_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting TCAP General Configuration",
         miTransId);

       
          retVal = tcapGenCfg ();
       if (retVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : TCAP General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       else
       {
         logger.logMsg (TRACE_FLAG, 0,
         "TCAP General Configuration retval= <%d>",
         retVal);
          return retVal;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_USPCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting TCAP Upper SAP Configuration",
         miTransId);

       if (tcapUSapCfg (&(mpQueMsg->t.stackData)) == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : TCAP Upper SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_LSPCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting TCAP Lower SAP Configuration",
         miTransId);

       int liRetVal = tcapLSapCfg (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : TCAP Lower SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
     /*  else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : TCAP Route Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }*/
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
  case BP_AIN_SM_SCC_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting SCCP General Configuration",
         miTransId);

       if (sccpGenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : SCCP General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_USPCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting SCCP Upper SAP Configuration",
         miTransId);

       if (sccpUSapCfg (&(mpQueMsg->t.stackData)) == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : SCCP Upper SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_LSPCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting SCCP Lower SAP Configuration",
         miTransId);

       int liRetVal = sccpLSapCfg (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : SCCP Lower SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d> : SCCP Route Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_NWKCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting SCCP Network Configuration",
         miTransId);

       if (sccpNwCfg (&(mpQueMsg->t.stackData)) == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : SCCP Network Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RTECFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting SCCP Route Configuration",
         miTransId);

       int liRetVal = sccpRouteCfg (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : SCCP Route Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
      else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (WARNING_FLAG, 0,
          "TID <%d> : SCCP Route Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }
       break;
      }
    case BP_AIN_SM_SUBTYPE_GTADDRMAP:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting SCCP GT ADDRESS MAP Configuration",
         miTransId);

       int liRetVal = addGtAddrMap (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : SCCP GT Address Map Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
      else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (WARNING_FLAG, 0,
          "TID <%d> : SCCP GT Address Map Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }
       break;
      }
    case BP_AIN_SM_SUBTYPE_GTRULE:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting SCCP GT Rule Configuration",
         miTransId);

       int liRetVal = addGtrule (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : SCCP GT Rule Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
      else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (WARNING_FLAG, 0,
          "TID <%d> : SCCP GT Rule Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }
       break;
      }

     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
  case BP_AIN_SM_M3U_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA general Configuration",
         miTransId);

       if (m3uaGenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : M3UA General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_USPCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA Upper SAP Configuration",
         miTransId);

       if (m3uaUSapCfg (&(mpQueMsg->t.stackData)) == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : M3UA Upper SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_LSPCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA Lower SAP Configuration",
         miTransId);

       int liRetVal = m3uaLSapCfg (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : M3UA Lower SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       /*else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : M3UA Route Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }*/
       break;
      }
     case BP_AIN_SM_SUBTYPE_NWKCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA Network Configuration",
         miTransId);

       if (m3uaNwCfg (&(mpQueMsg->t.stackData)) == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : M3UA Network Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RTECFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA Route Entry Configuration",
         miTransId);

       int liRetVal = m3uaRouteEntryCfg (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : M3UA Route Entry Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (WARNING_FLAG, 0,
          "TID <%d> : M3UA Route Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_PSCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA PS Configuration",
         miTransId);

       int liRetVal = m3uaPSCfg (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : M3UA PS Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       /*else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (WARNING_FLAG, 0,
          "TID <%d> : M3UA PS Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }*/
       break;
      }
     case BP_AIN_SM_SUBTYPE_PSPCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting M3UA PSP Configuration",
         miTransId);

       int liRetVal = m3uaPSPCfg (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : M3UA PSP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
      /* else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (WARNING_FLAG, 0,
          "TID <%d> : M3UA PSP Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }*/
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
  case BP_AIN_SM_SCT_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting SCTP General Configuration",
         miTransId);

       if (sctpGenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : SCTP General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_USPCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting SCTP Upper SAP Configuration",
         miTransId);

      if (sctpUSapCfg (&(mpQueMsg->t.stackData)) == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : SCTP Upper SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_LSPCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting SCTP Lower SAP Configuration",
         miTransId);

       int liRetVal = sctpLSapCfg (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : SCTP Lower SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       /*else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : SCTP Route Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }*/
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
  case BP_AIN_SM_TUC_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting TUCL General Configuration",
         miTransId);

       if (tuclGenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : TUCL General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_USPCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting TUCL Upper SAP Configuration",
         miTransId);

       if (tuclUSapCfg (&(mpQueMsg->t.stackData)) == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : TUCL Upper SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
  case BP_AIN_SM_MTP3_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       if (mtp3GenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_USPCFG:
      {
       if (mtp3USapCfg (&(mpQueMsg->t.stackData)) == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 Upper SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_LSPCFG:
      {
       int liRetVal = mtp3LSapCfg (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 Lower SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 Route Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_LNKSETCFG:
      {
       int liRetVal = mtp3LinkSetCfg (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 LinkSet Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 LinkSet Configuration Index Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }
       else if (liRetVal == BP_AIN_SM_INDEX_REPEAT)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 LinkSet Configuration Index repeat",
          miTransId);
        return BP_AIN_SM_INDEX_REPEAT;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RTECFG_SELF:
      {
       int liRetVal = mtp3RouteCfgSelf ();
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 Route Entry Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 Self Route Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RTECFG_PEER:
      {
       int liRetVal = mtp3RouteCfgPeer ();
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 Peer Route Entry Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 Route Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }
       break;
      }
      case BP_AIN_SM_SUBTYPE_RTECFG:
      {
       int liRetVal = mtp3RouteCfg (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 Peer Route Entry Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 Route Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }

//#ifdef INCTBD 
  case BP_AIN_SM_RELAY_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       if (ryGenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : Relay General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_LIS_CHANCFG:
      {
       if (ryLChanCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Relay channel Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_SRV_CHANCFG:
      {
       if (rySChanCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Relay channel Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_CLI_CHANCFG:
      {
       if (ryCChanCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :Relay channel Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
    }
    break;
   }
//#endif
  case BP_AIN_SM_MTP2_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       if (mtp2GenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : TUCL General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_USPCFG:
      {
       int liRetVal = mtp2DLSapCfg (&(mpQueMsg->t.stackData));
       if (liRetVal == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP2 Upper SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       else if (liRetVal == BP_AIN_SM_INDEX_OVER)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP2 DLSAP Configuration Over",
          miTransId);
        return BP_AIN_SM_INDEX_OVER;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
  case BP_AIN_SM_PSF_TCAP_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       if (psfTcapGenCfg() == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : PSF TCAP General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
   case BP_AIN_SM_PSF_SCCP_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       if (psfSccpGenCfg() == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : PSF SCCP General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
   case BP_AIN_SM_LDF_MTP3_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       if (ldfMtp3GenCfg() == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : LDF MTP3 General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL:
      {
       if (ldfMtp3RsetMapCriticalCfg() == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : LDF MTP3 critical rset Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF:
      {
       if (ldfMtp3RsetMapDefCfg() == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : LDF MTP3 default rset Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL:
      {
       if (ldfMtp3RsetMapNonCriticalCfg() == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : LDF MTP3 Non Critical rset Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_USPCFG:
      {
       if (ldfMtp3USapCfg(&(mpQueMsg->t.stackData)) == BP_AIN_SM_FAIL)
       {
         logger.logMsg (ERROR_FLAG, 0,
           "TID <%d> : LDF MTP3 USAP Configuration Failed",
            miTransId);
         return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_LSPCFG:
      {
       if (ldfMtp3LSapCfg(&(mpQueMsg->t.stackData)) == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : LDF MTP3 LSAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
   case BP_AIN_SM_LDF_M3UA_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       if (ldfM3uaGenCfg() == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : LDF M3UA General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL:
      {
       if (ldfM3uaRsetMapCriticalCfg() == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : LDF M3UA critical rset Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF:
      {
       if (ldfM3uaRsetMapDefCfg() == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : LDF M3UA default rset Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL:
      {
       if (ldfM3uaRsetMapNonCriticalCfg() == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : LDF M3UA Non Critical rset Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_USPCFG:
      {
       if (ldfM3uaUSapCfg(&(mpQueMsg->t.stackData)) == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : LDF M3UA USAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_NWKCFG:
      {
       if (ldfM3uaNwkCfg() == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : LDF M3UA Nwk Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }

  case BP_AIN_SM_PSF_MTP3_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting PSF MTP3 General Configuration",
         miTransId);

       if (psfMtp3GenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : TCAP General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting PSF MTP3 RSET Configuration",
         miTransId);

       if (psfMtp3RsetMapCriticalCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : TCAP Upper SAP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting TCAP Upper SAP Configuration",
         miTransId);

       if (psfMtp3RsetMapDefCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 Default RSET Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting MTP3 RSET Configuration",
         miTransId);

       //if (psfMtp3RsetMapNonCriticalCfg () == BP_AIN_SM_FAIL)
       if (psfMtp3RsetMapNonCrit() == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MTP3 RSET Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
  case BP_AIN_SM_SH_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting SH General Configuration",
         miTransId);

       if (shGenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : TCAP General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
  case BP_AIN_SM_SG_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting SG General Configuration",
         miTransId);

       if (sgGenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : SG General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_ENT_SG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting Ent SG Configuration",
         miTransId);

       if (sgEntSgCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : ENT SG Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_ENT_TUCL:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting ENT TUCL Configuration",
         miTransId);

       if (sgEntTuclCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : ENT TUCL Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_ENT_SCTP:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting ENT SCTP SG Configuration",
         miTransId);

       if (sgEntSctpCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : ENT SCTP General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_ENT_M3UA:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting ENT M3UA Configuration",
         miTransId);

       if (sgEntM3uaCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : ENT M3UA Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_ENT_MTP2:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting ENT MTP2 Configuration",
         miTransId);

       if (sgEntMtp2Cfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : ENT MTP2 Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_ENT_MTP3:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting ENT MTP3 Configuration",
         miTransId);

       if (sgEntMtp3Cfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : ENT MTP2 Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_ENT_SCCP:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting ENT SCCP Configuration",
         miTransId);

       if (sgEntSccpCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> :  ENT SCCP Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_ENT_TCAP:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting ENT TCAP General Configuration",
         miTransId);

       if (sgEntTcapCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : ENT TCAP General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
  case BP_AIN_SM_MR_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting MR General Configuration",
         miTransId);

       if (mrGenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : MR General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }
  case BP_AIN_SM_PSF_M3UA_LAYER:
   {
    switch (miSubOp)
    {
     case BP_AIN_SM_SUBTYPE_GENCFG:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting PSF M3UA General Configuration",
         miTransId);

       if (psfM3uaGenCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : PSF M3UA General Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting PSF M3UA RSET CRIT Configuration",
         miTransId);

       if (psfM3uaRsetMapCriticalCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : PSF M3UA RSET CRIT Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting PSF M3UA RSET DEF Configuration",
         miTransId);

       if (psfM3uaRsetMapDefCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : PSF M3UA RSET DEF Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     case BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL:
      {
       logger.logMsg (TRACE_FLAG, 0,
         "TID <%d> : Starting PSF M3UA RSET NON-CRIT Configuration",
         miTransId);
       if (psfM3uaRsetMapNonCriticalCfg () == BP_AIN_SM_FAIL)
       {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : PSF M3UA RSET NON-CRIT Configuration Failed",
          miTransId);
        return BP_AIN_SM_FAIL;
       }
       break;
      }
     default:
      {
       logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Unknown Sub Op <%d> passed", miSubOp);
       return BP_AIN_SM_FAIL;
      }
    }
    break;
   }

  default:
   {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> : Unknown LayerId <%d> passed", miLayer);
    return BP_AIN_SM_FAIL;
    break;  
   }

 }

 logger.logMsg (TRACE_FLAG, 0,
   "TID <%d> : Leaving INGwSmCfgHdlr::sendRequest()",
   miTransId);
 return BP_AIN_SM_OK;
}


/*
 * INAP Layer configuration
 */

/******************************************************************************
 *
 *     Fun:   inapGenCfg()
 *
 *     Desc:  General Configuration for the INAP Layer
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
#if 0
    int
INGwSmCfgHdlr::inapGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::inapGenCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (lpRep->getIeMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.ie) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.ie.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSM;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STGEN;
    lpHdr->transId     = miTransId;

    l.ie.t.cfg.s.ieGen.lmPst.selector  = BP_AIN_SM_COUPLING;     /* selector */

    l.ie.t.cfg.s.ieGen.lmPst.region    = BP_AIN_SM_REGION;        /* region */
    l.ie.t.cfg.s.ieGen.lmPst.pool      = BP_AIN_SM_POOL;       /* pool */
    l.ie.t.cfg.s.ieGen.lmPst.prior     = BP_AIN_SM_PRIOR;        /* priority */
    l.ie.t.cfg.s.ieGen.lmPst.route     = BP_AIN_SM_ROUTE;       /* route */
    l.ie.t.cfg.s.ieGen.lmPst.dstProcId = SFndProcId();  /* dst proc id */
    l.ie.t.cfg.s.ieGen.lmPst.dstEnt    = ENTSM;         /* dst entity */
    l.ie.t.cfg.s.ieGen.lmPst.dstInst   = BP_AIN_SM_DEST_INST;     /* dst inst */
    l.ie.t.cfg.s.ieGen.lmPst.srcProcId = SFndProcId();  /* src proc id */
    l.ie.t.cfg.s.ieGen.lmPst.srcEnt    = ENTIE;         /* src entity */
    l.ie.t.cfg.s.ieGen.lmPst.srcInst   = BP_AIN_SM_SRC_INST;     /* src inst */

    Pst *smPst = lpRep->getPst (BP_AIN_SM_AIN_LAYER);
    smPst->event = LIE_EVTCFGREQ;

    smMiLieCfgReq(smPst, &l.ie);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::inapGenCfg",
        miTransId);

    return BP_AIN_SM_OK;
}
#endif

#if 0
/******************************************************************************
 *
 *     Fun:   inapUSapCfg()
 *
 *     Desc:  Upper SAP COnfiguration for INAP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::inapUSapCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::inapUSapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1)
      miIndex = 1;
    else
      miIndex++;


    if (lpRep->getIeMngmt (BP_AIN_SM_SUBTYPE_USPCFG, l.ie, miIndex) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository for index <%d>",
            miTransId, miIndex);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.ie.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIE;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STINSAP;
    lpHdr->transId     = miTransId;

    l.ie.t.cfg.s.ieInSap.uiSel = BP_AIN_SM_COUPLING;  /* loosely coupled */

    l.ie.t.cfg.s.ieInSap.uiMemId.region = BP_AIN_SM_REGION;  /* defualt priority */
    l.ie.t.cfg.s.ieInSap.uiMemId.pool   = BP_AIN_SM_POOL; /* defualt priority */
    l.ie.t.cfg.s.ieInSap.uiPrior        = BP_AIN_SM_PRIOR;  /* defualt priority */
    l.ie.t.cfg.s.ieInSap.uiRoute        = BP_AIN_SM_ROUTE; /* default route */

    Pst *smPst = lpRep->getPst (BP_AIN_SM_AIN_LAYER);
    smPst->event = LIE_EVTCFGREQ;

    smMiLieCfgReq(smPst, &l.ie);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::inapUSapCfg",
        miTransId);

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   inapLSapCfg()
 *
 *     Desc:  Lower SAP Configuration for INAP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::inapLSapCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::inapLSapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1) {
      miIndex = 1;
    }
    else {
      miIndex++;
    }

    int liRetVal = lpRep->getIeMngmt (BP_AIN_SM_SUBTYPE_LSPCFG, l.ie, miIndex);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //check if any other route also needs to be configured
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved as index is finished",
            miTransId);
      return BP_AIN_SM_INDEX_OVER;
    }

    //initialize the header
    Header *lpHdr = &(l.ie.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIE;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STTCSAP;
    lpHdr->transId     = miTransId;

    /* update the lower interface parameters */

    l.ie.t.cfg.s.ieTcSap.liSel    = BP_AIN_SM_COUPLING;  /* loosely coupled */

    l.ie.t.cfg.s.ieTcSap.liMemId.region = BP_AIN_SM_REGION;  /* defualt region */
    l.ie.t.cfg.s.ieTcSap.liMemId.pool   = BP_AIN_SM_POOL; /* defualt pool */
    l.ie.t.cfg.s.ieTcSap.liPrior        = BP_AIN_SM_PRIOR;  /* defualt priority */
    l.ie.t.cfg.s.ieTcSap.liRoute        = BP_AIN_SM_ROUTE; /* default route */
    l.ie.t.cfg.s.ieTcSap.liProcId       = SFndProcId();
    l.ie.t.cfg.s.ieTcSap.liEnt          = ENTST;
    l.ie.t.cfg.s.ieTcSap.liInst         = BP_AIN_SM_DEST_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_AIN_LAYER);
    smPst->event = LIE_EVTCFGREQ;

    smMiLieCfgReq(smPst, &l.ie);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::inapLSapCfg",
        miTransId);

    return BP_AIN_SM_OK;

}
#endif


/*
 * TCAP Layer Configuration
 */

/******************************************************************************
 *
 *     Fun:   INGwSmCfgHdlr()
 *
 *     Desc:  General Configuration for TCAP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::tcapGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::tcapGenCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (lpRep->getStMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.st) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.st.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTST;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STGEN;
    lpHdr->transId     = miTransId;

    /* layer manager */

    l.st.t.cfg.s.genCfg.smPst.selector  = BP_AIN_SM_COUPLING;     /* selector */
    l.st.t.cfg.s.genCfg.smPst.region    = BP_AIN_SM_REGION;        /* region */
    l.st.t.cfg.s.genCfg.smPst.pool      = BP_AIN_SM_POOL;       /* pool */
    l.st.t.cfg.s.genCfg.smPst.prior     = BP_AIN_SM_PRIOR;        /* priority */
    l.st.t.cfg.s.genCfg.smPst.route     = BP_AIN_SM_ROUTE;       /* route */
    logger.logMsg (TRACE_FLAG, 0, "Tcap GenCfg Route [%d]",
                   l.st.t.cfg.s.genCfg.smPst.route);
    l.st.t.cfg.s.genCfg.smPst.dstProcId = SFndProcId();  /* dst proc id */
    l.st.t.cfg.s.genCfg.smPst.dstEnt    = ENTSM;         /* dst entity */
    l.st.t.cfg.s.genCfg.smPst.dstInst   = BP_AIN_SM_DEST_INST;     /* dst inst */
    l.st.t.cfg.s.genCfg.smPst.srcProcId = SFndProcId();  /* src proc id */
    l.st.t.cfg.s.genCfg.smPst.srcEnt    = ENTST;         /* src entity */
    l.st.t.cfg.s.genCfg.smPst.srcInst   = BP_AIN_SM_SRC_INST;     /* src inst */


    Pst *smPst = lpRep->getPst (BP_AIN_SM_TCA_LAYER);
    smPst->event = EVTLSTCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    logger.logMsg (TRACE_FLAG, 0,
        "<dstProcId,srcProcId,srcEnt,dstEnt> = <%d,%d,%x,%x> : tcapGenCfg",
        smPst->dstProcId,smPst->srcProcId,smPst->srcEnt,smPst->dstEnt);
    

    SmMiLstCfgReq(smPst, &l.st);

    
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::tcapGenCfg",
        miTransId);

    return BP_AIN_SM_OK;
    //return BP_AIN_SM_SEND_NEXT;

}


/******************************************************************************
 *
 *     Fun:   tcapUSapCfg()
 *
 *     Desc:  Upper SAP Configuration for TCAP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::tcapUSapCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::tcapUSapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1)
      miIndex = 1;
    else
      miIndex++;


    if (lpRep->getStMngmt (BP_AIN_SM_SUBTYPE_USPCFG, l.st, miIndex) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_FAIL;
    }

    int defSsn = 0;
    char * lSsn = getenv("DEF_RSN_SSN");
    if (lSsn) {
      defSsn = atoi(lSsn);
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "DEF_RSN_SSN:%d ",defSsn);
    }        
    else { 
      defSsn = 190;  
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Using Default value for DEF_RSN_SSN:%d ",defSsn);
    }

    int defNmbDlg = 0;
    char * lDlg = getenv("NUM_RSN_DLGS");
    if (lDlg) {
      defNmbDlg = atoi(lDlg);
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "NUM_RSN_DLGS:%d ",defNmbDlg);
    }        
    else { 
      defNmbDlg = 50;  
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Using Default value for NUM_RSN_DLGS:%d ",defNmbDlg);
    }


    if(((int)stackReq->req.u.addLocalSsn.ssn) == defSsn) //In JAPAN, RSN/RSA will come on ssn 190.so updating its dlg-id range , making it short
    {
      l.st.t.cfg.s.tuSapCfg.nmbDlgs = defNmbDlg; //get it from getEnv
      l.st.t.cfg.s.tuSapCfg.nmbInvs = defNmbDlg;
      l.st.t.cfg.s.tuSapCfg.nmbBins = defNmbDlg*5 + 8000;
      l.st.t.cfg.s.tuSapCfg.loDlgId = tcapLoDlgId;
      l.st.t.cfg.s.tuSapCfg.hiDlgId = tcapLoDlgId+defNmbDlg;
      logger.logMsg (TRACE_FLAG, 0,
            "tcapUSapCfg():FOUND SSN: %d,total dlg:%d, total inv:%d, total bins:%d, loDlgId:%d,hiDlgId:%d",
            defSsn,l.st.t.cfg.s.tuSapCfg.nmbDlgs,l.st.t.cfg.s.tuSapCfg.nmbInvs,
            l.st.t.cfg.s.tuSapCfg.nmbBins,l.st.t.cfg.s.tuSapCfg.loDlgId,l.st.t.cfg.s.tuSapCfg.hiDlgId);
    }
    //initialize the header
    Header *lpHdr = &(l.st.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTST;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STTCUSAP;
    lpHdr->elmId.elmntInst1 = stackReq->req.u.addLocalSsn.tcapUsapId;
    lpHdr->transId     = miTransId;

		logger.logMsg(ALWAYS_FLAG, 0, 
		"+VER+tcapUSapCfg tcapUsapId:%d", stackReq->req.u.addLocalSsn.tcapUsapId);

    /* layer manager */
    l.st.t.cfg.s.tuSapCfg.tuSel    = BP_AIN_SM_COUPLING;  /* loosely coupled */

    l.st.t.cfg.s.tuSapCfg.tuMemId.region = BP_AIN_SM_REGION;  /* defualt region */
    l.st.t.cfg.s.tuSapCfg.tuMemId.pool   = BP_AIN_SM_POOL; /* defualt pool */
    l.st.t.cfg.s.tuSapCfg.tuPrior        = BP_AIN_SM_PRIOR;  /* defualt priority */
    l.st.t.cfg.s.tuSapCfg.tuRoute        = BP_AIN_SM_ROUTE; /* default route */
    logger.logMsg (TRACE_FLAG, 0, "Tcap USAP Route [%d]",
                   l.st.t.cfg.s.tuSapCfg.tuRoute);

    Pst *smPst = lpRep->getPst (BP_AIN_SM_TCA_LAYER);
    smPst->event = EVTLSTCFGREQ;

    smPst->dstProcId = stackReq->procId;
    smPst->srcProcId = SFndProcId();

    smMiLstCfgReq(smPst, &l.st);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::tcapUSapCfg",
        miTransId);

    return BP_AIN_SM_OK;

}


void AddLocalSsnStSPSapCfg(StSPSapCfg *stSap,AddLocalSsn *ssn)
{
    stSap->ssn = ssn->ssn;
    stSap->swtch = ssn->swtch;
    stSap->spId = ssn->sccpUsapId;

		logger.logMsg(ALWAYS_FLAG, 0, 
		"+VER+AddLocalSsnStSPSapCfg ssn:%d, swtch:%d, spId:%d",
		stSap->ssn, stSap->swtch, stSap->spId );
}
/******************************************************************************
 *
 *     Fun:   tcapLSapCfg()
 *
 *     Desc:  Lower SAP Configuration for TCAP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::tcapLSapCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::tcapLSapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1) {
      miIndex = 1;
    }
    else {
      miIndex++;
    }

    int liRetVal = lpRep->getStMngmt (BP_AIN_SM_SUBTYPE_LSPCFG, l.st, miIndex);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //check if any other route also needs to be configured
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved as index is finished",
            miTransId);
      return BP_AIN_SM_INDEX_OVER;
    }

          /* update data received from the user */
    AddLocalSsnStSPSapCfg(&(l.st.t.cfg.s.spSapCfg),&(stackReq->req.u.addLocalSsn));

    //initialize the header
    Header *lpHdr = &(l.st.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTST;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSPSAP;
    lpHdr->elmId.elmntInst1 = stackReq->req.u.addLocalSsn.tcapLsapId;
    lpHdr->transId     = miTransId;

    /* layer manager */
    l.st.t.cfg.s.spSapCfg.spSel          = BP_AIN_SM_COUPLING;      /* selector */
    l.st.t.cfg.s.spSapCfg.spMemId.region = BP_AIN_SM_REGION;  /* defualt region */
    l.st.t.cfg.s.spSapCfg.spMemId.pool   = BP_AIN_SM_POOL; /* defualt pool */
    l.st.t.cfg.s.spSapCfg.spProcId       = 0;// not required in case of FT layer
    l.st.t.cfg.s.spSapCfg.spEnt          = ENTSP;
    l.st.t.cfg.s.spSapCfg.spInst         = BP_AIN_SM_DEST_INST;
    l.st.t.cfg.s.spSapCfg.spPrior        = BP_AIN_SM_PRIOR;  /* defualt priority */
    l.st.t.cfg.s.spSapCfg.spRoute        = RTE_PROTO; /* default route */
    logger.logMsg (TRACE_FLAG, 0, "Tcap LSAP Route [%d]", 
                   l.st.t.cfg.s.spSapCfg.spRoute);

    Pst *smPst = lpRep->getPst (BP_AIN_SM_TCA_LAYER);
    smPst->event = EVTLSTCFGREQ;
    smPst->dstProcId = stackReq->procId;
    smPst->srcProcId = SFndProcId();
 
    smMiLstCfgReq(smPst, &l.st);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);


    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::tcapLSapCfg",
        miTransId);

    return BP_AIN_SM_OK;

}


/*
 * SCCP Layer Configuration
 */

/******************************************************************************
 *
 *     Fun:   sccpGenCfg()
 *
 *     Desc:  General Configuration for SCCP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sccpGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sccpGenCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getSpMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.sp) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.sp.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSP;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STGEN;
    lpHdr->transId     = miTransId;

    /* layer manager */
    l.sp.t.cfg.s.spGen.sm.event      = 0; /* unused */
    l.sp.t.cfg.s.spGen.sm.selector   = BP_AIN_SM_COUPLING; /* selector */
    l.sp.t.cfg.s.spGen.sm.region     = BP_AIN_SM_REGION; /* SCCP Manager memory region */
    l.sp.t.cfg.s.spGen.sm.pool       = BP_AIN_SM_POOL; /* SCCP Manager memory pool */
    l.sp.t.cfg.s.spGen.sm.prior      = BP_AIN_SM_PRIOR; /* priority */
    l.sp.t.cfg.s.spGen.sm.route      = BP_AIN_SM_ROUTE; /* route */
    logger.logMsg (TRACE_FLAG, 0, "SCCP GenCfg Route [%d]", 
                   l.sp.t.cfg.s.spGen.sm.route);
    l.sp.t.cfg.s.spGen.sm.dstProcId  = SFndProcId();  /* processor id */
    l.sp.t.cfg.s.spGen.sm.dstEnt     = ENTSM; /* entity */
    l.sp.t.cfg.s.spGen.sm.dstInst    = BP_AIN_SM_DEST_INST; /* instance */
    l.sp.t.cfg.s.spGen.sm.srcProcId  = SFndProcId(); /* processor id */
    l.sp.t.cfg.s.spGen.sm.srcEnt     = ENTSP; /* entity */
    l.sp.t.cfg.s.spGen.sm.srcInst    = BP_AIN_SM_SRC_INST; /* instance */

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SCC_LAYER);
    smPst->event = EVTLSPCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    SmMiLspCfgReq(smPst, &l.sp);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sccpGenCfg",
        miTransId);

    return BP_AIN_SM_OK;

}

void AddLocalSsnSpSAPCfg(SpSAPCfg *spSap,AddLocalSsn *ssn)
{
	char buf[4000];
	int bufLen =0;
	memset(&buf, 0, sizeof(buf));

 spSap->nmbConPc = ssn->nmbConPc;
	bufLen += sprintf(buf+bufLen,
	"+VER+AddLocalSsnSpSAPCfg nmbConPc:%d", spSap->nmbConPc) ;

 for(int cnt=0;cnt<ssn->nmbConPc;cnt++)
 {
  spSap->conPc[cnt] = ssn->conPc[cnt]; 
	bufLen += sprintf(buf+bufLen,
	"conPc[%d]{conPc:%d}, ", cnt, spSap->conPc[cnt]);
}
 
 spSap->nmbBpc = ssn->nmbBpc;

	bufLen += sprintf(buf+bufLen,
	"nmbBpc:%d, ", spSap->nmbBpc);

 for(int cnt=0;cnt<ssn->nmbBpc;cnt++)
 {
  spSap->bpcList[cnt] = ssn->bpcList[cnt];
	bufLen += sprintf(buf+bufLen,
	"bpcList[%d]{bpcList:%d}, ",
	cnt, spSap->bpcList[cnt]);
 }

 spSap->nwId = ssn->nwId;

 logger.logMsg(ALWAYS_FLAG, 0, 
 "%s, nwId:%d", buf, spSap->nwId);
}

/******************************************************************************
 *
 *     Fun:   sccpUSapCfg()
 *
 *     Desc:  Upper SAP Configuration for SCCP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sccpUSapCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sccpUSapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1)
      miIndex = 1;
    else
      miIndex++;


    //get the management structure from the DOM Tree
    if (lpRep->getSpMngmt (BP_AIN_SM_SUBTYPE_USPCFG, l.sp, miIndex) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_FAIL;
    }

          /* update data received from user */
    AddLocalSsnSpSAPCfg(&(l.sp.t.cfg.s.spSap),&(stackReq->req.u.addLocalSsn));

    //initialize the header
    Header *lpHdr = &(l.sp.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSP;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STTSAP;
    lpHdr->elmId.elmntInst1 = stackReq->req.u.addLocalSsn.sccpUsapId;
    lpHdr->transId     = miTransId;

		logger.logMsg(ALWAYS_FLAG, 0, 
		"+VER+AddLocalSsnSpSAPCfg sccpUsapId:%d", 
		stackReq->req.u.addLocalSsn.sccpUsapId);

    /* layer manager */
    l.sp.t.cfg.s.spSap.mem.region = BP_AIN_SM_REGION;
    l.sp.t.cfg.s.spSap.mem.pool = BP_AIN_SM_POOL;
    l.sp.t.cfg.s.spSap.route = RTE_PROTO;
    logger.logMsg (TRACE_FLAG, 0, "SCCP USAP Route [%d]", 
                   l.sp.t.cfg.s.spSap.route);
    l.sp.t.cfg.s.spSap.prior = BP_AIN_SM_PRIOR;
    l.sp.t.cfg.s.spSap.selector = BP_AIN_SM_COUPLING;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SCC_LAYER);
    smPst->event = EVTLSPCFGREQ;
    smPst->dstProcId = stackReq->procId;
    smPst->srcProcId = SFndProcId();

    SmMiLspCfgReq(smPst, &l.sp);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);


    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sccpUSapCfg",
        miTransId);

    return BP_AIN_SM_OK;

}

void AddUserPartSpNSAPCfg(SpNSAPCfg *spNSap, AddUserPart  *up)
{
  spNSap->nwId = up->nwId;

  if(up->userPartType == MTP3_USER)
  {
    spNSap->spId = up->mtp3UsapId;
    spNSap->dstEnt = ENTSN;
  }
  else
  {
    spNSap->spId = up->m3uaUsapId;
    spNSap->dstEnt = ENTIT;
  }

	logger.logMsg(ALWAYS_FLAG, 0, 
	"+VER+AddUserPartSpNSAPCfg nwId:%d, userPartType:%d, spId:%d",
	spNSap->nwId, up->userPartType, spNSap->spId);
}

/******************************************************************************
 *
 *     Fun:   sccpLSapCfg()
 *
 *     Desc:  Lower SAP Configuration for SCCP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sccpLSapCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sccpLSapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1) {
      miIndex = 1;
    }
    else {
      miIndex++;
    }

    //get the management structure from the DOM Tree
    int liRetVal = lpRep->getSpMngmt (BP_AIN_SM_SUBTYPE_LSPCFG, l.sp, miIndex);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //check if any other route also needs to be configured
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved as index is finished",
            miTransId);
      return BP_AIN_SM_INDEX_OVER;
    }

   /* update the data recieved from the user */
    AddUserPartSpNSAPCfg(&(l.sp.t.cfg.s.spNSap), &(stackReq->req.u.addUserPart));

    //initialize the header
    Header *lpHdr = &(l.sp.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSP;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STNSAP;
    lpHdr->elmId.elmntInst1 = stackReq->req.u.addUserPart.sccpLsapId;
#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.sp.t.cfg.s.spNSap.mem.region = BP_AIN_SM_REGION;
    l.sp.t.cfg.s.spNSap.mem.pool = BP_AIN_SM_POOL;

    l.sp.t.cfg.s.spNSap.selector = BP_AIN_SM_COUPLING;
    l.sp.t.cfg.s.spNSap.dstProcId = stackReq->procId; /* local procid */
    l.sp.t.cfg.s.spNSap.prior = BP_AIN_SM_PRIOR;
    l.sp.t.cfg.s.spNSap.route = BP_AIN_SM_ROUTE;
    logger.logMsg (TRACE_FLAG, 0, "SCCP LSAP Route [%d]", 
                   l.sp.t.cfg.s.spNSap.route);

    INGwSmRepository& rep = *mrDist.getSmRepository ();
    if(stackReq->req.u.addUserPart.userPartType == MTP3_USER) {
      logger.logMsg (TRACE_FLAG, 0,
            "adding MTP3_USER");
      l.sp.t.cfg.s.spNSap.dstEnt = ENTSN;           /* Entity MTP3 */
    }
    else {
      logger.logMsg (TRACE_FLAG, 0,
            "adding M3UA_USER");
      l.sp.t.cfg.s.spNSap.dstEnt = ENTIT;           /* Entity M3UA */
    }

    l.sp.t.cfg.s.spNSap.dstInst = BP_AIN_SM_DEST_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SCC_LAYER);
    smPst->event = EVTLSPCFGREQ;
	  smPst->dstProcId = stackReq->procId;
	  smPst->srcProcId = SFndProcId();

    SmMiLspCfgReq(smPst, &l.sp);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;
	
    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sccpLSapCfg",
        miTransId);

    return BP_AIN_SM_OK;

}

void AddNetworkSpNwCfg(SpNwCfg *spNw, AddNetwork *nwk)
{
	char buf[1000];
	int bufLen=0;
	memset(&buf, 0, sizeof(buf));

 spNw->nwId = nwk->nwId;
 spNw->variant = nwk->variant;
 spNw->pcLen = nwk->dpcLen;
//#ifdef LSPV2_6
 spNw->spcBroadcastOn = nwk->spcBroadcastOn;
 spNw->defaultPc = nwk->defaultPc;
 spNw->nmbSpcs = nwk->nmbSpcs;
 for (int cnt = 0; cnt < nwk->nmbSpcs; cnt++)
 {
  	spNw->selfPc[cnt] = nwk->selfPc[cnt];
		bufLen += sprintf(buf+bufLen, "selfPc[%d]:%d,",
		cnt, spNw->selfPc[cnt]);
 }
//#else
// spNw->selfPc = nwk->selfPc[0];
//#endif
 spNw->niInd = nwk->niInd;
 spNw->subService = nwk->subService; 

	logger.logMsg (ALWAYS_FLAG, 0,
	"+VER+AddNetworkSpNwCfg nwId:%d, variant:%d, pcLen:%d, spcBroadcastOn:%d"
	", defaultPc:%d, nmbSpcs:%d, selfPc{%s}, niInd:%d, subService:%d HopCount: %d",
 	spNw->nwId, spNw->variant, spNw->pcLen, spNw->spcBroadcastOn, 
	spNw->defaultPc, spNw->nmbSpcs,(bufLen ==0)?"NO PC":buf,
 	spNw->niInd, spNw->subService, spNw->defHopCnt);
}


/******************************************************************************
 *
 *     Fun:   sccpNwCfg()
 *
 *     Desc:  Network Configuration for SCCP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sccpNwCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sccpNwCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getSpMngmt (BP_AIN_SM_SUBTYPE_NWKCFG, l.sp) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

          /* update the data received from the user */
    AddNetworkSpNwCfg(&(l.sp.t.cfg.s.spNw), &(stackReq->req.u.addNwk));

    //initialize the header
    Header *lpHdr = &(l.sp.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSP;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STNW;
    lpHdr->transId     = miTransId;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SCC_LAYER);
    smPst->event = EVTLSPCFGREQ;
	smPst->dstProcId = stackReq->procId;
	smPst->srcProcId = SFndProcId();

    SmMiLspCfgReq(smPst, &l.sp);

	stackReq->resp.procId = stackReq->procId;
	stackReq->txnType = NORMAL_TXN;
	stackReq->txnStatus = INPROGRESS;
	
    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sccpNwCfg",
        miTransId);

    return BP_AIN_SM_OK;

}

   /* 
void AddDpcSnRoutCfg(SnRoutCfg *snRtCfg , AddRoute *dpc)
{
  snRtCfg->dpc = dpc->dpc;
  snRtCfg->swtchType = dpc->swtchType;
  snRtCfg->upSwtch = dpc->upSwtch;
  snRtCfg->cmbLnkSetId = dpc->cmbLnkSetId;
  snRtCfg->dir = dpc->dir;
  snRtCfg->rteToAdjSp = dpc->rteToAdjSp;
  snRtCfg->ssf = dpc->ssf;
}*/


/******************************************************************************
 *
 *     Fun:   sccpRouteCfg()
 *
 *     Desc:  Route Configuration for SCCP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sccpRouteCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sccpRouteCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1)
      miIndex = 1;
    else
      miIndex++;

    int liRetVal = lpRep->getSpMngmt (BP_AIN_SM_SUBTYPE_RTECFG, l.sp, miIndex);

    //get the management structure from the DOM Tree
    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_FAIL;
    }


    //check if any other route also needs to be configured
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (WARNING_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved as index is finished for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_INDEX_OVER;
    }

      /* update data recieved from the user */
   AddDpcSpRteCfg(&(l.sp.t.cfg.s.spRte), &(stackReq->req.u.addRoute));


    //initialize the header
    Header *lpHdr = &(l.sp.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSP;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STROUT;
    lpHdr->transId     = miTransId;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SCC_LAYER);
    smPst->event = EVTLSPCFGREQ;
    smPst->dstProcId = stackReq->procId;
    smPst->srcProcId = SFndProcId();

    SmMiLspCfgReq(smPst, &l.sp);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sccpRouteCfg",
        miTransId);

    return BP_AIN_SM_OK;

}

/*
 * MTP3 configuration 
 */
/******************************************************************************
 *
 *     Fun:   mtp3GenCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::mtp3GenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::mtp3GenCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getSnMgmt (BP_AIN_SM_SUBTYPE_GENCFG, l.sn) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.sn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STGEN;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.sn.t.cfg.s.snGen.sm.selector   = BP_AIN_SM_COUPLING;
    l.sn.t.cfg.s.snGen.sm.region     = BP_AIN_SM_REGION;
    l.sn.t.cfg.s.snGen.sm.pool       = BP_AIN_SM_POOL;
    l.sn.t.cfg.s.snGen.sm.prior      = BP_AIN_SM_PRIOR;
    l.sn.t.cfg.s.snGen.sm.route      = BP_AIN_SM_ROUTE;
    logger.logMsg (TRACE_FLAG, 0, "MTP3 GenCfg Route [%d]", 
                   l.sn.t.cfg.s.snGen.sm.route);
    l.sn.t.cfg.s.snGen.sm.dstProcId  = SFndProcId();
    l.sn.t.cfg.s.snGen.sm.dstEnt     = ENTSM;
    l.sn.t.cfg.s.snGen.sm.dstInst    = BP_AIN_SM_DEST_INST;
    l.sn.t.cfg.s.snGen.sm.srcProcId  = SFndProcId();
    l.sn.t.cfg.s.snGen.sm.srcEnt     = ENTSN;
    l.sn.t.cfg.s.snGen.sm.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_MTP3_LAYER);
    smPst->event = EVTLSNCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();

    smMiLsnCfgReq(smPst, &l.sn);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::mtp3GenCfg",
        miTransId);

    return BP_AIN_SM_OK;
}

void AddUserPartSnNSAPCfg(SnNSAPCfg *snNSAP, AddUserPart *up)
{
  snNSAP->ssf = up->ssf;
  snNSAP->lnkType = up->lnkType;
  snNSAP->upSwtch = up->upSwtch;
#if (defined(SN_SG) || defined(TDS_ROLL_UPGRADE_SUPPORT))
  snNSAP->usrParts = up->mtp3UsapId;
#endif

	logger.logMsg(ALWAYS_FLAG, 0, 
	"+VER+AddUserPartSnNSAPCfg ssf:%d, lnkType:%d, upSwtch:%d",
	snNSAP->ssf, snNSAP->lnkType, snNSAP->upSwtch);
}



/******************************************************************************
 *
 *     Fun:   mtp3USapCfg()
 *
 *     Desc:  Upper SAP Configuration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::mtp3USapCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::mtp3USapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL", miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getSnMgmt (BP_AIN_SM_SUBTYPE_USPCFG, l.sn, miIndex) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

      /* update data received from the user */
   AddUserPartSnNSAPCfg(&(l.sn.t.cfg.s.snNSAP), &(stackReq->req.u.addUserPart));

    //initialize the header
    Header* lpHdr = &(l.sn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STNSAP;
    lpHdr->elmId.elmntInst1 = stackReq->req.u.addUserPart.mtp3UsapId;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* set configuration parameters */
    l.sn.t.cfg.s.snNSAP.selector = BP_AIN_SM_COUPLING;
    l.sn.t.cfg.s.snNSAP.mem.region = BP_AIN_SM_REGION;
    l.sn.t.cfg.s.snNSAP.mem.pool = BP_AIN_SM_POOL;
    l.sn.t.cfg.s.snNSAP.prior = BP_AIN_SM_PRIOR;
    l.sn.t.cfg.s.snNSAP.route = RTE_PROTO;
    logger.logMsg (TRACE_FLAG, 0, "MTP3 USAP Route [%d]", 
                   l.sn.t.cfg.s.snNSAP.route);

    Pst *smPst = lpRep->getPst (BP_AIN_SM_MTP3_LAYER);
    smPst->event = EVTLSNCFGREQ;
	  smPst->dstProcId = stackReq->procId;
	  smPst->srcProcId = SFndProcId();

    smMiLsnCfgReq(smPst, &l.sn);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;
	
    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::mtp3USapCfg",
        miTransId);

    return BP_AIN_SM_OK;
}

void addLinkSnDnLsapCfg(AddLink *addlink, SnDLSapCfg *snDLSAP)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering addLinkSnDnLsapCfg");

  snDLSAP->lnkSetId = addlink->lnkSetId;
  snDLSAP->dpcLen = addlink->dpcLen;
  snDLSAP->opc = addlink->opc;
  snDLSAP->adjDpc = addlink->adjDpc;
  snDLSAP->ssf = addlink->ssf;
  snDLSAP->lnkTstSLC = addlink->slc;
  snDLSAP->lnkType = addlink->lnkType;
  snDLSAP->upSwtch = addlink->lnkType;
  snDLSAP->lnkPrior = addlink->lnkPrior;
  snDLSAP->spId = addlink->mtp2UsapId;
  snDLSAP->dstProcId= addlink->mtp2ProcId;

	logger.logMsg (ALWAYS_FLAG, 0, 
	"+VER+addLinkSnDnLsapCfg lnkSetId:%d, dpcLen:%d, opc:%d, adjDpc:%d, ssf:%d,"
	"lnkTstSLC:%d, lnkType:%d, upSwtch:%d, lnkPrior:%d, spId:%d, dstProcId:%d",
  snDLSAP->lnkSetId, snDLSAP->dpcLen, snDLSAP->opc, snDLSAP->adjDpc, snDLSAP->ssf,
  snDLSAP->lnkTstSLC, snDLSAP->lnkType, snDLSAP->upSwtch, snDLSAP->lnkPrior,
  snDLSAP->spId, snDLSAP->dstProcId);

  snDLSAP->dstEnt= ENTSD;
  snDLSAP->dstInst= BP_AIN_SM_DEST_INST;
  snDLSAP->prior= BP_AIN_SM_PRIOR;
  snDLSAP->route= BP_AIN_SM_ROUTE;
  snDLSAP->selector = 0;
  
    logger.logMsg (TRACE_FLAG, 0,
        "Leaving addLinkSnDnLsapCfg");
}


/******************************************************************************
 *
 *     Fun:   mtp3LSapCfg()
 *
 *     Desc:  Lower SAP Configuration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::mtp3LSapCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::mtp3LSapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL", miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1) {
      miIndex = 1;
    }
    else {
      ++miIndex;
    }

    //get the management structure from the DOM Tree
    int liRetVal = lpRep->getSnMgmt (BP_AIN_SM_SUBTYPE_LSPCFG, l.sn, miIndex);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //check if any other route also needs to be configured
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved as index is finished",
            miTransId);
      return BP_AIN_SM_INDEX_OVER;
    }

     /* updating the paramters recieved from the user */
    addLinkSnDnLsapCfg(&(stackReq->req.u.lnk), &l.sn.t.cfg.s.snDLSAP);

    //initialize the header
    Header* lpHdr = &(l.sn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDLSAP;
    lpHdr->elmId.elmntInst1 = stackReq->req.u.lnk.mtp3LsapId;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    l.sn.t.cfg.s.snDLSAP.selector = BP_AIN_SM_COUPLING;
    l.sn.t.cfg.s.snDLSAP.mem.region = BP_AIN_SM_REGION;
    l.sn.t.cfg.s.snDLSAP.mem.pool = BP_AIN_SM_POOL;
    l.sn.t.cfg.s.snDLSAP.prior = BP_AIN_SM_PRIOR;
    l.sn.t.cfg.s.snDLSAP.route = BP_AIN_SM_ROUTE;
    logger.logMsg (TRACE_FLAG, 0, "MTP3 LSAP mtp3LsapId(elmntInst1)[%d] Route [%d]", 
           lpHdr->elmId.elmntInst1, l.sn.t.cfg.s.snDLSAP.route);
    //l.sn.t.cfg.s.snDLSAP.dstProcId = SFndProcId();
    l.sn.t.cfg.s.snDLSAP.dstEnt = ENTSD;
    l.sn.t.cfg.s.snDLSAP.dstInst = BP_AIN_SM_DEST_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_MTP3_LAYER);
    smPst->event = EVTLSNCFGREQ;
	  smPst->dstProcId = stackReq->procId;
	  smPst->srcProcId = SFndProcId();

    printf("[+INC+] %s:%d mtp3LSapCfg(): snDLSAP->dstProcId[%d] smPst->dstProcId[%d]\n", __FILE__, __LINE__, l.sn.t.cfg.s.snDLSAP.dstProcId, smPst->dstProcId);

    smMiLsnCfgReq(smPst, &l.sn);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;
	
    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::mtp3LSapCfg",
        miTransId);

    return BP_AIN_SM_OK;
}

void AddDpcSnRoutCfg(SnRoutCfg *snRout, AddRoute *dpc)
{
 snRout->dpc = dpc->dpc;
 snRout->spType = dpc->spType;
 snRout->cmbLnkSetId = dpc->cmbLnkSetId;
 snRout->rteToAdjSp = dpc->rteToAdjSp;
 snRout->swtchType = dpc->swtchType;
 snRout->dir = dpc->dir;
 snRout->upSwtch = dpc->upSwtch;
 snRout->ssf = dpc->ssf;

 logger.logMsg(ALWAYS_FLAG, 0, 
 "+VER+AddDpcSnRoutCfg dpc:%d, spType:%d, cmLnkSetId:%d, rteToAdjSp:%d"
 ", swtchType:%d, dir:%d, upSwtch:%d, ssf:%d",
 snRout->dpc , snRout->spType, snRout->cmbLnkSetId, snRout->rteToAdjSp,
 snRout->swtchType, snRout->dir, snRout->upSwtch,
 snRout->ssf);


}
/******************************************************************************
 *
 *     Fun:   mtp3RouteCfg ()
 *
 *     Desc:  Routing Entry Configuration for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::mtp3RouteCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::mtp3RouteCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL", miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1) {
      miIndex = 1;
    }
    else {
      ++miIndex;
    }

    //get the management structure from the DOM Tree
    int liRetVal = lpRep->getSnMgmt (BP_AIN_SM_SUBTYPE_RTECFG_PEER, l.sn, miIndex);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //check if any other route also needs to be configured
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved as index is finished",
            miTransId);
      return BP_AIN_SM_INDEX_OVER;
    }

    /* update the data received from the user */
          AddDpcSnRoutCfg(&(l.sn.t.cfg.s.snRout),&(stackReq->req.u.addRoute));

    //initialize the header
    Header *lpHdr = &(l.sn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STROUT;
#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    Pst *smPst = lpRep->getPst (BP_AIN_SM_MTP3_LAYER);
    smPst->event = EVTLSNCFGREQ;
    smPst->dstProcId = stackReq->procId;
    smPst->srcProcId = SFndProcId();

    smMiLsnCfgReq(smPst, &l.sn);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::mtp3RouteCfg",
        miTransId);
    
    return BP_AIN_SM_OK;
}
/******************************************************************************
 *
 *     Fun:   mtp3RouteCfgPeer ()
 *
 *     Desc:  Routing Entry Configuration for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::mtp3RouteCfgPeer ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::mtp3RouteCfgPeer",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL", miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1) {
      miIndex = 1;
    }
    else {
      ++miIndex;
    }

    //get the management structure from the DOM Tree
    int liRetVal = lpRep->getSnMgmt (BP_AIN_SM_SUBTYPE_RTECFG_PEER, l.sn, miIndex);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //check if any other route also needs to be configured
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved as index is finished",
            miTransId);
      return BP_AIN_SM_INDEX_OVER;
    }

    //initialize the header
    Header *lpHdr = &(l.sn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STROUT;
#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    Pst *smPst = lpRep->getPst (BP_AIN_SM_MTP3_LAYER);
    smPst->event = EVTLSNCFGREQ;

    smMiLsnCfgReq(smPst, &l.sn);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::mtp3RouteCfgPeer",
        miTransId);

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   mtp3RouteCfgSelf ()
 *
 *     Desc:  Routing Entry Configuration for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::mtp3RouteCfgSelf ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::mtp3RouteCfgSelf",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL", miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    int liRetVal = lpRep->getSnMgmt (BP_AIN_SM_SUBTYPE_RTECFG_SELF, l.sn, miIndex);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.sn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STROUT;
#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    Pst *smPst = lpRep->getPst (BP_AIN_SM_MTP3_LAYER);
    smPst->event = EVTLSNCFGREQ;

    smMiLsnCfgReq(smPst, &l.sn);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::mtp3RouteCfgSelf",
        miTransId);

    return BP_AIN_SM_OK;
}

void AddLinkSetSnLnkSetCfg(SnLnkSetCfg *snLnkSet, AddLinkSet *lnkSet)
{
	char buf[4000];
	int bufLen =0;
	memset(&buf, 0, sizeof(buf));

  snLnkSet->lnkSetId= lnkSet->lnkSetId;
  snLnkSet->lnkSetType = lnkSet->lnkSetType;
  snLnkSet->adjDpc = lnkSet->adjDpc;
  snLnkSet->nmbActLnkReqd = lnkSet->nmbActLnkReqd;
  snLnkSet->nmbCmbLnkSet = lnkSet->nmbCmbLnkSet;

	bufLen += sprintf(buf+bufLen, 
	"+VER+AddLinkSetSnLnkSetCfg lnkSetId:%d, lnkSetType:%d, adjDpc:%d, nmbActLnkReqd:%d, nmbCmbLnkSet:%d", 
  snLnkSet->lnkSetId, snLnkSet->lnkSetType, snLnkSet->adjDpc,
  snLnkSet->nmbActLnkReqd, snLnkSet->nmbCmbLnkSet);

  for(int cnt=0; cnt<lnkSet->nmbCmbLnkSet;cnt++)
  {
    snLnkSet->cmbLnkSet[cnt].cmbLnkSetId = lnkSet->cmbLnkSet[cnt].cmbLnkSetId;
    snLnkSet->cmbLnkSet[cnt].lnkSetPrior = lnkSet->cmbLnkSet[cnt].lnkSetPrior;
    snLnkSet->cmbLnkSet[cnt].nmbPrefLinks = lnkSet->cmbLnkSet[cnt].nmbPrefLinks;

		bufLen += sprintf(buf+bufLen, 
		"cmbLnkSet[%d] {cmbLnkSetId:%d, lnkSetPrior:%d, nmbPrefLinks:%d}, ",cnt,
    snLnkSet->cmbLnkSet[cnt].cmbLnkSetId, snLnkSet->cmbLnkSet[cnt].lnkSetPrior,
    snLnkSet->cmbLnkSet[cnt].nmbPrefLinks);

    for(int i=0;i<lnkSet->cmbLnkSet[cnt].nmbPrefLinks;i++) {
       snLnkSet->cmbLnkSet[cnt].prefLnkId[i] = lnkSet->cmbLnkSet[cnt].prefLnkId[i];

			 bufLen += sprintf(buf+bufLen, "nmbPrefLinks[%d]{prefLnkId:%d},", i,
			 snLnkSet->cmbLnkSet[cnt].prefLnkId[i]);
		}
  }
	logger.logMsg (ALWAYS_FLAG, 0, "%s", buf);
}
/******************************************************************************
 *
 *     Fun:   mtp3LinkSetCfg()
 *
 *     Desc:  Link Set Entry Configuration for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::mtp3LinkSetCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::mtp3LinkSetCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1) {
      miIndex = 1;
    }
    else if (miCount == 0) {
      ++miIndex;
    }

    //get the management structure from the DOM Tree
    int liRetVal = lpRep->getSnMgmt (BP_AIN_SM_SUBTYPE_LNKSETCFG, l.sn, miIndex, &miCount);
    logger.logMsg (ERROR_FLAG, 0, "TID <%d> : The value of Index/Count is %d/%d",
        miIndex, miCount);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //check if any other route also needs to be configured
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved as index is finished",
            miTransId);
      return BP_AIN_SM_INDEX_OVER;
    }

      /*update the data recived from the user */
   AddLinkSetSnLnkSetCfg(&(l.sn.t.cfg.s.snLnkSet), &(stackReq->req.u.lnkSet));

    //initialize the header
    Header *lpHdr = &(l.sn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STLNKSET;
#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    Pst *smPst = lpRep->getPst (BP_AIN_SM_MTP3_LAYER);
    smPst->event = EVTLSNCFGREQ;
	  smPst->dstProcId = stackReq->procId;
	  smPst->srcProcId = SFndProcId();

    smMiLsnCfgReq(smPst, &l.sn);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;
	
    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::mtp3LinkSetCfg",
        miTransId);

    return liRetVal;
}

//#ifdef INCTBD
/*
 * Relay configuration 
 */
/******************************************************************************
 *
 *     Fun:   ryGenCfg()
 *
 *     Desc:  General COnfiguration for Relay
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ryGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ryGenCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getRyMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.ry) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.ry.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTRY;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STGEN;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.ry.t.cfg.s.ryGenCfg.lmPst.selector   = BP_AIN_SM_COUPLING;
    l.ry.t.cfg.s.ryGenCfg.lmPst.region     = BP_AIN_SM_REGION;
    l.ry.t.cfg.s.ryGenCfg.lmPst.pool       = BP_AIN_SM_POOL;
    l.ry.t.cfg.s.ryGenCfg.lmPst.prior      = BP_AIN_SM_PRIOR;
    l.ry.t.cfg.s.ryGenCfg.lmPst.route      = BP_AIN_SM_ROUTE;
    l.ry.t.cfg.s.ryGenCfg.lmPst.dstProcId  = SFndProcId();
    l.ry.t.cfg.s.ryGenCfg.lmPst.dstEnt     = ENTSM;
    l.ry.t.cfg.s.ryGenCfg.lmPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.ry.t.cfg.s.ryGenCfg.lmPst.srcProcId  = SFndProcId();
    l.ry.t.cfg.s.ryGenCfg.lmPst.srcEnt     = ENTRY;
    l.ry.t.cfg.s.ryGenCfg.lmPst.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_RELAY_LAYER);
    smPst->event = EVTLRYCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();

    SmMiLryCfgReq(smPst, &l.ry);
#if 0 
    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;
	
    mrDist.updateRspStruct(miTransId,stackReq);
#endif

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ryGenCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   ryLChanCfg()
 *
 *     Desc:  Listen Channel Configuration for Relay
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ryLChanCfg()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ryLChanCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getRyMngmt (BP_AIN_SM_SUBTYPE_LIS_CHANCFG , l.ry) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.ry.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTRY;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STCHCFG;
    lpHdr->elmId.elmntInst1 = l.ry.t.cfg.s.ryChanCfg.id;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif


    Pst *smPst = lpRep->getPst (BP_AIN_SM_RELAY_LAYER);
    smPst->event = EVTLRYCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();

    SmMiLryCfgReq(smPst, &l.ry);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ryLChanCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   rySChanCfg()
 *
 *     Desc:  Setrver Channel Configuration for Relay
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::rySChanCfg()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::rySChanCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getRyMngmt (BP_AIN_SM_SUBTYPE_SRV_CHANCFG , l.ry) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    strcpy((char *)mpDist->mpSmWrapper->relayClientIp,
           l.ry.t.cfg.s.ryChanCfg.transmittoHostName);
    strcpy((char*)mpDist->mpSmWrapper->relayServerIp,
           l.ry.t.cfg.s.ryChanCfg.selfHostName);
    logger.logMsg(ALWAYS_FLAG, 0, "RelayClientIp<%s> RelayServerIp<%s>",
                  mpDist->mpSmWrapper->relayClientIp,
                  mpDist->mpSmWrapper->relayServerIp);
    

    //initialize the header
    Header* lpHdr = &(l.ry.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTRY;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STCHCFG;
    lpHdr->elmId.elmntInst1 = l.ry.t.cfg.s.ryChanCfg.id;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    Pst *smPst = lpRep->getPst (BP_AIN_SM_RELAY_LAYER);
    smPst->event = EVTLRYCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();

    SmMiLryCfgReq(smPst, &l.ry);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::rySChanCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   ryCChanCfg()
 *
 *     Desc:  Client Channel Configuration for Relay
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ryCChanCfg()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ryCChanCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getRyMngmt (BP_AIN_SM_SUBTYPE_CLI_CHANCFG , l.ry) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    strcpy((char *)mpDist->mpSmWrapper->relayClientIp,
           l.ry.t.cfg.s.ryChanCfg.selfHostName);
    strcpy((char*)mpDist->mpSmWrapper->relayServerIp,
           l.ry.t.cfg.s.ryChanCfg.transmittoHostName);
    logger.logMsg(ALWAYS_FLAG, 0, "RelayClientIp<%s> RelayServerIp<%s>",
                  mpDist->mpSmWrapper->relayClientIp,
                  mpDist->mpSmWrapper->relayServerIp);

    //initialize the header
    Header* lpHdr = &(l.ry.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTRY;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STCHCFG;
    lpHdr->elmId.elmntInst1 = l.ry.t.cfg.s.ryChanCfg.id;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif


    Pst *smPst = lpRep->getPst (BP_AIN_SM_RELAY_LAYER);
    smPst->event = EVTLRYCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();

    SmMiLryCfgReq(smPst, &l.ry);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ryCChanCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


//#endif

/*
 * M3UA Layer Configuration
 */

/******************************************************************************
 *
 *     Fun:   m3uaGenCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::m3uaGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::m3uaGenCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getItMgmt (BP_AIN_SM_SUBTYPE_GENCFG, l.it) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.it.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIT;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STITGEN;
    lpHdr->transId     = miTransId;
    /* layer manager */
    l.it.t.cfg.s.genCfg.smPst.selector   = BP_AIN_SM_COUPLING; /* selector */
    l.it.t.cfg.s.genCfg.smPst.region     = BP_AIN_SM_REGION; /* SCCP Manager memory region */
    l.it.t.cfg.s.genCfg.smPst.pool       = BP_AIN_SM_POOL; /* SCCP Manager memory pool */ 
    l.it.t.cfg.s.genCfg.smPst.prior      = BP_AIN_SM_PRIOR; /* priority */
    l.it.t.cfg.s.genCfg.smPst.route      = BP_AIN_SM_ROUTE; /* route */
    l.it.t.cfg.s.genCfg.smPst.dstProcId  = SFndProcId();  /* processor id */
    l.it.t.cfg.s.genCfg.smPst.dstEnt     = ENTSM; /* entity */
    l.it.t.cfg.s.genCfg.smPst.dstInst    = BP_AIN_SM_DEST_INST; /* instance */
    l.it.t.cfg.s.genCfg.smPst.srcProcId  = SFndProcId(); /* processor id */
    l.it.t.cfg.s.genCfg.smPst.srcEnt     = ENTIT; /* entity */
    l.it.t.cfg.s.genCfg.smPst.srcInst    = BP_AIN_SM_SRC_INST; /* instance */


    Pst *smPst = lpRep->getPst (BP_AIN_SM_M3U_LAYER);
    smPst->event = EVTLITCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    smMiLitCfgReq(smPst, &l.it);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::m3uaGenCfg",
        miTransId);

    return BP_AIN_SM_OK;

}

void AddUserPartItNSapCfg(ItNSapCfg *nSapCfg,AddUserPart *up)
{
   nSapCfg->nwkId = up->nwId;
   nSapCfg->sapId = up->m3uaUsapId;
   nSapCfg->suType = up->suType;
}

/******************************************************************************
 *
 *     Fun:   m3uaUSapCfg()
 *
 *     Desc:  Upper SAP Configuration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::m3uaUSapCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::m3uaUSapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1)
      miIndex = 1;
    else
      miIndex++;


    //get the management structure from the DOM Tree
    if (lpRep->getItMgmt (BP_AIN_SM_SUBTYPE_USPCFG, l.it, miIndex) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_FAIL;
    }

   /* update the data recieved from the user */
    AddUserPartItNSapCfg(&(l.it.t.cfg.s.nSapCfg),&(stackReq->req.u.addUserPart));
    //initialize the header
    Header *lpHdr = &(l.it.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIT;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STITNSAP;;
    lpHdr->transId     = miTransId;

    /* set configuration parameters */
    l.it.t.cfg.s.nSapCfg.selector = BP_AIN_SM_COUPLING;
    l.it.t.cfg.s.nSapCfg.mem.region = BP_AIN_SM_REGION;
    l.it.t.cfg.s.nSapCfg.mem.pool = BP_AIN_SM_POOL;
    l.it.t.cfg.s.nSapCfg.prior = BP_AIN_SM_PRIOR;
    l.it.t.cfg.s.nSapCfg.route = RTE_PROTO;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_M3U_LAYER);
    smPst->event = EVTLITCFGREQ;
	  smPst->dstProcId = stackReq->procId;
	  smPst->srcProcId = SFndProcId();

    smMiLitCfgReq(smPst, &l.it);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;
	
    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::m3uaUSapCfg",
        miTransId);

    return BP_AIN_SM_OK;

}


void AddEndPointItSctSapCfg(ItSctSapCfg *sctSapCfg,AddEndPoint *ep)
{
  sctSapCfg->srcPort = ep->srcPort;
  sctSapCfg->procId = ep->sctpProcId;

  sctSapCfg->srcAddrLst.nmb = ep->nmbAddrs;

  for (int indx = 0; indx < ep->nmbAddrs; indx++)
  {
    sctSapCfg->srcAddrLst.nAddr[indx].type = ep->nAddr[indx].type;
    if (sctSapCfg->srcAddrLst.nAddr[indx].type == CM_NETADDR_IPV4) 
    {
      sctSapCfg->srcAddrLst.nAddr[indx].u.ipv4NetAddr = ep->nAddr[indx].u.ipv4NetAddr; 
    }
    else if (sctSapCfg->srcAddrLst.nAddr[indx].type == CM_NETADDR_IPV6)
    {
      strcpy((S8 *)sctSapCfg->srcAddrLst.nAddr[indx].u.ipv6NetAddr,
          (S8 *)ep->nAddr[indx].u.ipv6NetAddr);
    } 
  }

  sctSapCfg->suId = ep->m3uaLsapId;
  sctSapCfg->spId = ep->sctpUsapId;

}

/******************************************************************************
 *
 *     Fun:   m3uaLSapCfg()
 *
 *     Desc:  Lower SAP Configuration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::m3uaLSapCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::m3uaLSapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1)
      miIndex = 1;
    else
      miIndex++;


    //get the management structure from the DOM Tree
    int liRetVal = lpRep->getItMgmt (BP_AIN_SM_SUBTYPE_LSPCFG, l.it, miIndex);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_FAIL;
    }

    //check if any other route also needs to be configured
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved as index is finished",
            miTransId);
      return BP_AIN_SM_INDEX_OVER;
    }


    /* update the date recieved from the user */
    AddEndPointItSctSapCfg(&(l.it.t.cfg.s.sctSapCfg),&(stackReq->req.u.addEp));
    //initialize the header
    Header *lpHdr = &(l.it.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIT;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STITSCTSAP;
    lpHdr->transId     = miTransId;

    l.it.t.cfg.s.sctSapCfg.selector = BP_AIN_SM_COUPLING;
    l.it.t.cfg.s.sctSapCfg.mem.region = BP_AIN_SM_REGION;
    l.it.t.cfg.s.sctSapCfg.mem.pool = BP_AIN_SM_POOL;
    //l.it.t.cfg.s.sctSapCfg.procId = stackReq->req.u.addEp.sctpProcId;
    l.it.t.cfg.s.sctSapCfg.ent = ENTSB;
    l.it.t.cfg.s.sctSapCfg.inst = BP_AIN_SM_DEST_INST;
    l.it.t.cfg.s.sctSapCfg.prior = BP_AIN_SM_PRIOR;
    l.it.t.cfg.s.sctSapCfg.route = RTESPEC;


    Pst *smPst = lpRep->getPst (BP_AIN_SM_M3U_LAYER);
    smPst->event = EVTLITCFGREQ;
	  smPst->dstProcId = stackReq->procId;
	  smPst->srcProcId = SFndProcId();

    printf("[+INC+] %s:%d m3uaLSapCfg():: Add EndPoint Invoking SmMiLitCfgReq with procId %d nmbAddr %d suId(m3uaLsapId) %d spId(sctpUsapId) %d srcProcId %d destProcId %d\n", __FILE__, __LINE__, l.it.t.cfg.s.sctSapCfg.procId, l.it.t.cfg.s.sctSapCfg.srcAddrLst.nmb, l.it.t.cfg.s.sctSapCfg.suId, l.it.t.cfg.s.sctSapCfg.spId, smPst->srcProcId, smPst->dstProcId);
    logger.logMsg (ALWAYS_FLAG, 0,
           "m3uaLSapCfg():: Add EndPoint Invoking SmMiLitCfgReq with procId %d nmbAddr %d suId(m3uaLsapId) %d spId(sctpUsapId) %d srcProcId %d destProcId %d", l.it.t.cfg.s.sctSapCfg.procId, l.it.t.cfg.s.sctSapCfg.srcAddrLst.nmb, l.it.t.cfg.s.sctSapCfg.suId, l.it.t.cfg.s.sctSapCfg.spId, smPst->srcProcId, smPst->dstProcId);

    SmMiLitCfgReq(smPst, &l.it);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;
	
    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::m3uaLSapCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


void AddNetworkItNwkCfg(itNwkCfg *nwkCfg,AddNetwork *nwk)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering AddNetworkItNwkCfg");
 nwkCfg->nwkId = nwk->nwId;
 nwkCfg->dpcLen = nwk->dpcLen;
 nwkCfg->slsLen = nwk->slsLen;
 nwkCfg->ssf = nwk->ssf;
 nwkCfg->suSwtch = nwk->suSwtch;
 nwkCfg->su2Swtch = nwk->su2Swtch;

 for (int cnt = 0; cnt < LIT_MAX_PSP; cnt++)
 {
  nwkCfg->nwkApp[cnt] = nwk->nwkApp[cnt];
 }
 logger.logMsg (TRACE_FLAG, 0,"nwkId = %d  dpcLen = %d  slsLen = %d  ssf = %d  suSwtch = %d  su2Swtch = %d ",nwk->nwId,nwk->dpcLen,nwk->slsLen,nwk->ssf,nwk->suSwtch,nwk->su2Swtch);

 logger.logMsg (TRACE_FLAG, 0,"dpcLen = %d",nwk->dpcLen);
 logger.logMsg (TRACE_FLAG, 0,"slsLen = %d",nwk->slsLen);
 logger.logMsg (TRACE_FLAG, 0,"ssf = %d",nwk->ssf);

    logger.logMsg (TRACE_FLAG, 0,
        ": Leaving AddNetworkItNwkCfg");
}

/******************************************************************************
 *
 *     Fun:   m3uaNwCfg()
 *
 *     Desc:  Network Configuration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::m3uaNwCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::m3uaNwCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getItMgmt (BP_AIN_SM_SUBTYPE_NWKCFG, l.it) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }


    /* update the data recieved from the user */
    AddNetworkItNwkCfg(&(l.it.t.cfg.s.nwkCfg),&(stackReq->req.u.addNwk));

    //initialize the header
    Header *lpHdr = &(l.it.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIT;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STITNWK;
    lpHdr->transId     = miTransId;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_M3U_LAYER);
    smPst->event = EVTLITCFGREQ;
    smPst->dstProcId = stackReq->procId;
    smPst->srcProcId = SFndProcId();
    logger.logMsg (TRACE_FLAG, 0,"nwkId = %d",l.it.t.cfg.s.nwkCfg.nwkId);
    logger.logMsg (TRACE_FLAG, 0,"dpcLen = %d",l.it.t.cfg.s.nwkCfg.dpcLen);
    logger.logMsg (TRACE_FLAG, 0,"slsLen = %d",l.it.t.cfg.s.nwkCfg.slsLen);
    logger.logMsg (TRACE_FLAG, 0,"ssf = %d",l.it.t.cfg.s.nwkCfg.ssf);
    SmMiLitCfgReq(smPst, &l.it);

    stackReq->resp.procId = stackReq->procId;
	stackReq->txnType = NORMAL_TXN;
	stackReq->txnStatus = INPROGRESS;
	
    mrDist.updateRspStruct(miTransId,stackReq);
	

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::m3uaNwCfg",
        miTransId);

    return BP_AIN_SM_OK;

}

void AddAsItRteCfg(ItRteCfg *rteCfg, AddPs *ps)
{
  rteCfg->nwkId = ps->nwkId;
  rteCfg->rtType = ps->rtType;
  rteCfg->psId = ps->psId;
  rteCfg->rtFilter.dpc = ps->dpc;
  rteCfg->rtFilter.dpcMask = ps->dpcMask;
  rteCfg->rtFilter.opc = ps->opc;
  rteCfg->rtFilter.opcMask = ps->opcMask;
  rteCfg->rtFilter.slsMask = ps->slsMask;
  rteCfg->rtFilter.sls = ps->sls;
  rteCfg->rtFilter.sio = ps->sio;
  rteCfg->rtFilter.sioMask = ps->sioMask;
}
/******************************************************************************
 *
 *     Fun:   m3uaRouteEntryCfg()
 *
 *     Desc:  Routing Entry Configuration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::m3uaRouteEntryCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::m3uaRouteEntryCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1)
      miIndex = 1;
    else
      miIndex++;

    //get the management structure from the DOM Tree
    int liRetVal = lpRep->getItMgmt (BP_AIN_SM_SUBTYPE_RTECFG, l.it, miIndex);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_FAIL;
    }

   /* update data received from the user */
    AddAsItRteCfg(&(l.it.t.cfg.s.rteCfg), &(stackReq->req.u.addPs));

    //initialize the header
    Header *lpHdr = &(l.it.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIT;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STITROUT;
    lpHdr->transId     = miTransId;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_M3U_LAYER);
    smPst->event = EVTLITCFGREQ;
    smPst->dstProcId = stackReq->procId;    
    smPst->srcProcId = SFndProcId();

    smMiLitCfgReq(smPst, &l.it);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::m3uaRouteEntryCfg",
        miTransId);

    return BP_AIN_SM_OK;

}
void AddAsItPsCfg(ItPsCfg *psCfg,AddPs *ps)
{
  psCfg->psId = ps->psId;
  psCfg->routCtx = ps->routCtx;
  psCfg->nwkId = ps->nwkId;
  psCfg->loadShareMode = ps->loadShareMode;
  psCfg->nmbActPspReqd = ps->nmbActPspReqd;
  psCfg->nmbPsp = ps->nmbPsp;
  psCfg->lclFlag = ps->lFlag;

  for(int i=0;i<ps->nmbPsp;i++)
  {
     psCfg->psp[i] = ps->psp[i];
     logger.logMsg (ALWAYS_FLAG, 0,
            "AddAsItPsCfg():: PspId [%d]", psCfg->psp[i]);

#ifdef LITV14
     psCfg->pspEpLst[i].nmbEp = ps->pspEpLst[i].nmbEp;

     for(int j=0; j < psCfg->pspEpLst[i].nmbEp; j++) {

       psCfg->pspEpLst[i].endpIds[j] = ps->pspEpLst[i].endpIds[j];
       logger.logMsg (ALWAYS_FLAG, 0,
              "AddAsItPsCfg():: PspId[%d] EpId[%d]",
              psCfg->psp[i], psCfg->pspEpLst[i].endpIds[j]);
     }
#endif

  }


}

/******************************************************************************
 *
 *     Fun:   m3uaPSCfg()
 *
 *     Desc:  Peer Server Configuration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::m3uaPSCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::m3uaPSCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1)
      miIndex = 1;
    else
      miIndex++;

    //get the management structure from the DOM Tree
    int liRetVal = lpRep->getItMgmt (BP_AIN_SM_SUBTYPE_PSCFG, l.it, miIndex);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_FAIL;
    }

    //check if any other route also needs to be configured
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (WARNING_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved as index is finished for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_INDEX_OVER;
    }

      /* update the data received ffrom the user */
   AddAsItPsCfg(&(l.it.t.cfg.s.psCfg),&(stackReq->req.u.addPs));

    //initialize the header
    Header *lpHdr = &(l.it.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIT;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STITPS;
    lpHdr->transId     = miTransId;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_M3U_LAYER);
    smPst->event = EVTLITCFGREQ;
    smPst->dstProcId = stackReq->procId;
    smPst->srcProcId = SFndProcId();

    smMiLitCfgReq(smPst, &l.it);
   
    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::m3uaPSCfg",
        miTransId);

    return BP_AIN_SM_OK;

}


void AddAspItPspCfg(ItPspCfg *spCfg, AddPsp *psp)
{
  spCfg->pspId = psp->pspId;
  spCfg->pspType = psp->pspType;
  spCfg->ipspMode = psp->ipspMode;
  spCfg->assocCfg.dstAddrLst.nmb = psp->nmbAddr;
  for(int cnt=0;cnt<psp->nmbAddr;cnt++)
  {
      spCfg->assocCfg.dstAddrLst.nAddr[cnt].type = psp->addr[cnt].type;
   if(spCfg->assocCfg.dstAddrLst.nAddr[cnt].type == CM_NETADDR_IPV4)
   {
         //spCfg->assocCfg.dstAddrLst.nAddr[cnt].u.ipv4NetAddr = ntohl(inet_addr((S8 *)psp->addr[cnt].u.ipv4NetAddr));
         spCfg->assocCfg.dstAddrLst.nAddr[cnt].u.ipv4NetAddr = psp->addr[cnt].u.ipv4NetAddr;
      if (cnt == 0)
      {
       spCfg->assocCfg.priDstAddr.type = CM_NETADDR_IPV4;
       //spCfg->assocCfg.priDstAddr.u.ipv4NetAddr = ntohl(inet_addr((S8 *)psp->addr[cnt].u.ipv4NetAddr));
       spCfg->assocCfg.priDstAddr.u.ipv4NetAddr = psp->addr[cnt].u.ipv4NetAddr;
      } 
   }
   else if(spCfg->assocCfg.dstAddrLst.nAddr[cnt].type == CM_NETADDR_IPV6)
   {
               strcpy((S8 *)spCfg->assocCfg.dstAddrLst.nAddr[cnt].u.ipv6NetAddr,(S8 *)psp->addr[cnt].u.ipv6NetAddr);
      if (cnt == 0)
      {
       spCfg->assocCfg.priDstAddr.type = CM_NETADDR_IPV6;
       strcpy((S8 *)spCfg->assocCfg.dstAddrLst.nAddr[cnt].u.ipv6NetAddr,
         (S8 *)psp->addr[cnt].u.ipv6NetAddr);
      } 

   }
  }
  spCfg->assocCfg.dstPort = psp->dstPort;
#ifdef ITASP
  spCfg->cfgForAllLps = psp->cfgForAllLps;
#endif
  spCfg->nwkId = psp->nwkId;
#ifdef LITV13
  spCfg->includeRC = psp->includeRC;
#endif
}


/******************************************************************************
 *
 *     Fun:   m3uaPSPCfg()
 *
 *     Desc:  Peer Signalling Process Configuration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::m3uaPSPCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::m3uaPSPCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1)
      miIndex = 1;
    else
      miIndex++;

    //get the management structure from the DOM Tree
    int liRetVal = lpRep->getItMgmt (BP_AIN_SM_SUBTYPE_PSPCFG, l.it, miIndex);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository for index <%d>",
            miTransId, miIndex);
      return BP_AIN_SM_FAIL;
    }

    //check if any other route also needs to be configured
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (WARNING_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved as index is finished for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_INDEX_OVER;
    }

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Assoc Cfg Info (before overwrite), dstPort <%d>,locOutStrms <%d> ",
        miTransId, l.it.t.cfg.s.pspCfg.assocCfg.dstPort, l.it.t.cfg.s.pspCfg.assocCfg.locOutStrms );


      /* update the data recieved from the user */
   AddAspItPspCfg(&(l.it.t.cfg.s.pspCfg),&(stackReq->req.u.addpsp));

    //initialize the header
    Header *lpHdr = &(l.it.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIT;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STITPSP;
    lpHdr->transId     = miTransId;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_M3U_LAYER);
    smPst->event = EVTLITCFGREQ;
    smPst->dstProcId = stackReq->procId;
    smPst->srcProcId = SFndProcId();

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Assoc Cfg Info , pspId <%d>,dstPort <%d>, nwkId <%d>,ipv4addr <%u> ",
        miTransId, stackReq->req.u.addpsp.pspId, stackReq->req.u.addpsp.dstPort, stackReq->req.u.addpsp.nwkId , stackReq->req.u.addpsp.addr[0].u.ipv4NetAddr );

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Assoc Cfg Info (After overwrite), dstPort <%d>,locOutStrms <%d> ",
        miTransId, l.it.t.cfg.s.pspCfg.assocCfg.dstPort, l.it.t.cfg.s.pspCfg.assocCfg.locOutStrms );

    //sleep(20);
    SmMiLitCfgReq(smPst, &l.it);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::m3uaPSPCfg",
        miTransId);

    return BP_AIN_SM_OK;

}


/*
 * SCTP Layer Configuration
 */

/******************************************************************************
 *
 *     Fun:   sctpGenCfg()
 *
 *     Desc:  General COnfiguration for SCTP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sctpGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sctpGenCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getSbMgmt (BP_AIN_SM_SUBTYPE_GENCFG, l.sb) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.sb.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSB;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSBGEN;
    lpHdr->transId     = miTransId;

    l.sb.t.cfg.s.genCfg.smPst.event      = 0; /* unused */
    l.sb.t.cfg.s.genCfg.smPst.selector   = BP_AIN_SM_COUPLING; /* selector */
    l.sb.t.cfg.s.genCfg.smPst.region     = BP_AIN_SM_REGION; /* SCCP Manager memory region */
    l.sb.t.cfg.s.genCfg.smPst.pool       = BP_AIN_SM_POOL; /* SCCP Manager memory pool */ 
    l.sb.t.cfg.s.genCfg.smPst.prior      = BP_AIN_SM_PRIOR; /* priority */
    l.sb.t.cfg.s.genCfg.smPst.route      = BP_AIN_SM_ROUTE; /* route */
    l.sb.t.cfg.s.genCfg.smPst.dstProcId  = SFndProcId();  /* processor id */
    l.sb.t.cfg.s.genCfg.smPst.dstEnt     = ENTSM; /* entity */
    l.sb.t.cfg.s.genCfg.smPst.dstInst    = BP_AIN_SM_DEST_INST; /* instance */
    l.sb.t.cfg.s.genCfg.smPst.srcProcId  = SFndProcId(); /* processor id */
    l.sb.t.cfg.s.genCfg.smPst.srcEnt     = ENTSB; /* entity */
    l.sb.t.cfg.s.genCfg.smPst.srcInst    = BP_AIN_SM_SRC_INST; /* instance */


    Pst *smPst = lpRep->getPst (BP_AIN_SM_SCT_LAYER);
    smPst->event = LSB_EVTCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    smMiLsbCfgReq(smPst, &l.sb);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sctpGenCfg",
        miTransId);

    return BP_AIN_SM_OK;

}


void AddEndPointSbSctSapCfg( SbSctSapCfg *sctSapCfg,AddEndPoint *ep)
{
 sctSapCfg->spId = ep->sctpUsapId;
}
/******************************************************************************
 *
 *     Fun:   sctpUSapCfg()
 *
 *     Desc:  Upper SAP Configuration for SCTP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sctpUSapCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sctpUSapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1)
      miIndex = 1;
    else
      miIndex++;


    //get the management structure from the DOM Tree
    if (lpRep->getSbMgmt (BP_AIN_SM_SUBTYPE_USPCFG, l.sb, miIndex) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_FAIL;
    }

          /* update the data received by the user */
    AddEndPointSbSctSapCfg(&(l.sb.t.cfg.s.sctSapCfg),&(stackReq->req.u.addEp));

    //initialize the header
    Header *lpHdr = &(l.sb.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSB;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSBSCTSAP;
    lpHdr->transId     = miTransId;

    /* Layer Configuration */
    l.sb.t.cfg.s.sctSapCfg.sel = BP_AIN_SM_COUPLING;
    l.sb.t.cfg.s.sctSapCfg.memId.region = BP_AIN_SM_REGION;
    l.sb.t.cfg.s.sctSapCfg.memId.pool = BP_AIN_SM_POOL;
    l.sb.t.cfg.s.sctSapCfg.prior = BP_AIN_SM_PRIOR;
    l.sb.t.cfg.s.sctSapCfg.route = RTESPEC;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SCT_LAYER);
    smPst->event = LSB_EVTCFGREQ;
    smPst->dstProcId = stackReq->procId;
    smPst->srcProcId = SFndProcId();

    printf("[+INC+] %s:%d sctpUSapCfg():: Add EndPoint Invoking SmMiLsbCfgReq with spId(sctpUsapId) %d srcProcId %d destProcId %d\n", __FILE__, __LINE__, l.sb.t.cfg.s.sctSapCfg.spId, smPst->srcProcId, smPst->dstProcId);
    logger.logMsg (ALWAYS_FLAG, 0,
           "sctpUSapCfg():: Add EndPoint Invoking SmMiLsbCfgReq with spId(sctpUsapId) %d  srcProcId %d destProcId %d", l.sb.t.cfg.s.sctSapCfg.spId, smPst->srcProcId, smPst->dstProcId);

    SmMiLsbCfgReq(smPst, &l.sb);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sctpUSapCfg",
        miTransId);

    return BP_AIN_SM_OK;

}


void AddEndPointSbTSapCfg(SbTSapCfg *tSapCfg,AddEndPoint *ep)
{
  tSapCfg->suId = ep->sctpLsapId;

  /* 
   * Get TUCL Upper SAP-Id (spId). 
   * Already allocated while Cfging TUCL Upper SAP 
   */
  tSapCfg->reConfig.spId = ep->tuclUsapId;
  tSapCfg->procId = ep->sctpProcId;

  tSapCfg->srcNAddrLst.nmb = ep->nmbAddrs;
  for (int indx = 0; indx < ep->nmbAddrs; indx++)
  {
    tSapCfg->srcNAddrLst.nAddr[indx].type = ep->nAddr[indx].type;
    if (tSapCfg->srcNAddrLst.nAddr[indx].type == CM_NETADDR_IPV4) 
    {
      tSapCfg->srcNAddrLst.nAddr[indx].u.ipv4NetAddr = ep->nAddr[indx].u.ipv4NetAddr; 
    }
    else if (tSapCfg->srcNAddrLst.nAddr[indx].type == CM_NETADDR_IPV6)
    {
      strcpy((S8 *)tSapCfg->srcNAddrLst.nAddr[indx].u.ipv6NetAddr,
          (S8 *)ep->nAddr[indx].u.ipv6NetAddr);
    } 
  }

}


/******************************************************************************
 *
 *     Fun:   sctpLSapCfg()
 *
 *     Desc:  Lower SAP Configuration for SCTP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sctpLSapCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sctpLSapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1)
      miIndex = 1;
    else
      miIndex++;

    //get the management structure from the DOM Tree
    int liRetVal = lpRep->getSbMgmt (BP_AIN_SM_SUBTYPE_LSPCFG, l.sb, miIndex);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_FAIL;
    }

    //check if any other route also needs to be configured
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved as index is finished",
            miTransId);
      return BP_AIN_SM_INDEX_OVER;
    }

          /* update the data received from the user */
    AddEndPointSbTSapCfg(&(l.sb.t.cfg.s.tSapCfg),&(stackReq->req.u.addEp));

    //initialize the header
    Header *lpHdr = &(l.sb.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSB;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSBTSAP;
    lpHdr->transId     = miTransId;

    l.sb.t.cfg.s.tSapCfg.sel = BP_AIN_SM_COUPLING;
    l.sb.t.cfg.s.tSapCfg.ent = ENTHI;
    l.sb.t.cfg.s.tSapCfg.inst = BP_AIN_SM_DEST_INST;
    //l.sb.t.cfg.s.tSapCfg.procId = stackReq->procId;
    l.sb.t.cfg.s.tSapCfg.memId.region = BP_AIN_SM_REGION;
    l.sb.t.cfg.s.tSapCfg.memId.pool = BP_AIN_SM_POOL;
    l.sb.t.cfg.s.tSapCfg.prior = BP_AIN_SM_PRIOR;
    l.sb.t.cfg.s.tSapCfg.route = RTESPEC;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SCT_LAYER);
    smPst->event = LSB_EVTCFGREQ;
    smPst->dstProcId = stackReq->procId;
    smPst->srcProcId = SFndProcId();

    printf("[+INC+] %s:%d sctpLSapCfg():: Add Endpoint Invoking smMiLsbCfgReq with suId(sctpLsapId) %d spId(tuclUsapId) %d procId %d nmbAddr %d srcProcId %d destProcId %d\n", __FILE__, __LINE__, l.sb.t.cfg.s.tSapCfg.suId, l.sb.t.cfg.s.tSapCfg.reConfig.spId, l.sb.t.cfg.s.tSapCfg.procId, l.sb.t.cfg.s.tSapCfg.srcNAddrLst.nmb, smPst->srcProcId, smPst->dstProcId);
    logger.logMsg (ALWAYS_FLAG, 0,
           "sctpLSapCfg():: Add Endpoint Invoking smMiLsbCfgReq with suId(sctpLsapId) %d spId(tuclUsapId) %d procId %d nmbAddr %d srcProcId %d destProcId %d", l.sb.t.cfg.s.tSapCfg.suId, l.sb.t.cfg.s.tSapCfg.reConfig.spId, l.sb.t.cfg.s.tSapCfg.procId, l.sb.t.cfg.s.tSapCfg.srcNAddrLst.nmb, smPst->srcProcId, smPst->dstProcId);

    smMiLsbCfgReq(smPst, &l.sb);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sctpLSapCfg",
        miTransId);

    return BP_AIN_SM_OK;

}


/*
 * TUCL Layer Configuration
 */

/******************************************************************************
 *
 *     Fun:   tuclGenCfg()
 *
 *     Desc:  General Configuration for TUCL
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::tuclGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::tuclGenCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getHiMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.hi) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.hi.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTHI;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STGEN;
    lpHdr->transId     = miTransId;

    l.hi.t.cfg.s.hiGen.lmPst.event      = 0; /* unused */
    l.hi.t.cfg.s.hiGen.lmPst.selector   = BP_AIN_SM_COUPLING; /* selector */
    l.hi.t.cfg.s.hiGen.lmPst.region     = BP_AIN_SM_REGION; /* SCCP Manager memory region */
    l.hi.t.cfg.s.hiGen.lmPst.pool       = BP_AIN_SM_POOL; /* SCCP Manager memory pool */ 
    l.hi.t.cfg.s.hiGen.lmPst.prior      = BP_AIN_SM_PRIOR; /* priority */
    l.hi.t.cfg.s.hiGen.lmPst.route      = BP_AIN_SM_ROUTE; /* route */
    l.hi.t.cfg.s.hiGen.lmPst.dstProcId  = SFndProcId();  /* processor id */
    l.hi.t.cfg.s.hiGen.lmPst.dstEnt     = ENTSM; /* entity */
    l.hi.t.cfg.s.hiGen.lmPst.dstInst    = BP_AIN_SM_DEST_INST; /* instance */
    l.hi.t.cfg.s.hiGen.lmPst.srcProcId  = SFndProcId(); /* processor id */
    l.hi.t.cfg.s.hiGen.lmPst.srcEnt     = ENTHI; /* entity */
    l.hi.t.cfg.s.hiGen.lmPst.srcInst    = BP_AIN_SM_SRC_INST; /* instance */


    Pst *smPst = lpRep->getPst (BP_AIN_SM_TUC_LAYER);
    smPst->event = EVTLHICFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    smMiLhiCfgReq(smPst, &l.hi);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::tuclGenCfg",
        miTransId);

    return BP_AIN_SM_OK;

}

void AddEndPointHiSapCfg(HiSapCfg *hiSap,AddEndPoint *ep)
{
 hiSap->spId = ep->tuclUsapId;
}

// Upper SAP Configuration for TUCL Layer
/******************************************************************************
 *
 *     Fun:   tuclUSapCfg()
 *
 *     Desc:  Upper SAP Configuration for TUCL
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::tuclUSapCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::tuclUSapCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (miIndex == -1)
      miIndex = 1;
    else
      miIndex++;


    //get the management structure from the DOM Tree
    if (lpRep->getHiMngmt (BP_AIN_SM_SUBTYPE_USPCFG, l.hi, miIndex) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository for index<%d>",
            miTransId, miIndex);
      return BP_AIN_SM_FAIL;
    }


    /* update the data received from the user */
    AddEndPointHiSapCfg(&(l.hi.t.cfg.s.hiSap), &(stackReq->req.u.addEp));

    //initialize the header
    Header *lpHdr = &(l.hi.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTHI;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STTSAP;
    lpHdr->transId     = miTransId;

    /* Layer Configuration */
    l.hi.t.cfg.s.hiSap.uiPrior = BP_AIN_SM_PRIOR;
    l.hi.t.cfg.s.hiSap.uiRoute = RTESPEC;
    l.hi.t.cfg.s.hiSap.uiSel = BP_AIN_SM_COUPLING;
    l.hi.t.cfg.s.hiSap.uiMemId.region = BP_AIN_SM_REGION;
    l.hi.t.cfg.s.hiSap.uiMemId.pool = BP_AIN_SM_POOL;


    Pst *smPst = lpRep->getPst (BP_AIN_SM_TUC_LAYER);
    smPst->event = EVTLHICFGREQ;
	  smPst->dstProcId = stackReq->procId;
	  smPst->srcProcId = SFndProcId();

    printf("[+INC+] %s:%d tuclUSapCfg():: Add Endpoint Invoking SmMiLhiCfgReq with spId(tuclUsapId) %d srcProcId %d destProcId %d\n", __FILE__, __LINE__, l.hi.t.cfg.s.hiSap.spId, smPst->srcProcId, smPst->dstProcId);
    logger.logMsg (ALWAYS_FLAG, 0,
           "tuclUSapCfg():: Add Endpoint Invoking SmMiLhiCfgReq with spId(tuclUsapId) %d srcProcId %d destProcId %d", l.hi.t.cfg.s.hiSap.spId, smPst->srcProcId, smPst->dstProcId);

    SmMiLhiCfgReq(smPst, &l.hi);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;
	
    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::tuclUSapCfg",
        miTransId);

    return BP_AIN_SM_OK;
}
#if 0
/******************************************************************************
 *
 *     Fun:   smMiLieCfgReq()
 *
 *     Desc:  send the configuration request to the INAP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::smMiLieCfgReq (Pst *pst, IeMngmt *cfg)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::smMiLieCfgReq",
        miTransId);

    cmPkLieCfgReq (pst, cfg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::smMiLieCfgReq",
        miTransId);

    return BP_AIN_SM_OK;
}

#endif
/******************************************************************************
 *
 *     Fun:   smMiLstCfgReq()
 *
 *     Desc:  send the configuration request to the TCAP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::smMiLstCfgReq (Pst *pst, StMngmt *cfg)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::smMiLstCfgReq",
        miTransId);

    cmPkLstCfgReq (pst, cfg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::smMiLstCfgReq",
        miTransId);

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   smMiLspCfgReq()
 *
 *     Desc:  send the configuration request to the SCCP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::smMiLspCfgReq (Pst *pst, SpMngmt *cfg)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::smMiLspCfgReq",
        miTransId);

    cmPkLspCfgReq (pst, cfg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::smMiLspCfgReq",
        miTransId);

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   smMiLitCfgReq()
 *
 *     Desc:  send the configuration request to the M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::smMiLitCfgReq (Pst *pst, ItMgmt *cfg)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::smMiLitCfgReq",
        miTransId);

    cmPkLitCfgReq (pst, cfg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::smMiLitCfgReq",
        miTransId);

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   smMiLsbCfgReq()
 *
 *     Desc:  send the configuration request to the SCTP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::smMiLsbCfgReq (Pst *pst, SbMgmt *cfg)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::smMiLsbCfgReq",
        miTransId);

    cmPkLsbCfgReq (pst, cfg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::smMiLsbCfgReq",
        miTransId);

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   smMiLhiCfgReq()
 *
 *     Desc:  send the configuration request to the TUCL
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::smMiLhiCfgReq (Pst *pst, HiMngmt *cfg)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::smMiLhiCfgReq",
        miTransId);

    cmPkLhiCfgReq (pst, cfg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::smMiLhiCfgReq",
        miTransId);

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   smMiLsnCfgReq()
 *
 *     Desc:  send the configuration request to the MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::smMiLsnCfgReq (Pst *pst, SnMngmt *cfg)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::smMiLsnCfgReq",
        miTransId);

    cmPkLsnCfgReq (pst, cfg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::smMiLsnCfgReq",
        miTransId);

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   smMiLsdCfgReq()
 *
 *     Desc:  send the configuration request to the MTP2
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::smMiLsdCfgReq (Pst *pst, SdMngmt *cfg)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::smMiLsdCfgReq",
        miTransId);

    cmPkLsdCfgReq (pst, cfg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::smMiLsdCfgReq",
        miTransId);

    return BP_AIN_SM_OK;
}

///////////////////////////////////////////////////////////////////////////////
//
//
// SD layer cfg (MTP2)
//
//
///////////////////////////////////////////////////////////////////////////////

/******************************************************************************
 *
 *     Fun:   mtp2GenCfg()
 *
 *     Desc:  General COnfiguration for MTP2 Layer
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
int
INGwSmCfgHdlr::mtp2GenCfg () {

    S16 l_mtp2CfgRet = ROK;
    int l_nmbLinks = 0;

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::mtp2GenCfg",
        miTransId);
#if 0
TODO
#ifdef SOLARIS
    IphConvConfigure(2, 16, DEF_MAX_BUFFER_SIZE);

#else
#endif
    logger.logMsg (ERROR_FLAG, 0, "MTP works in Solaris Only");
    logger.logMsg (ERROR_FLAG, 0, 
        "SM_TRANSPORT_TYPE->0 =MTP, SM_TRANSPORT_TYPE->1 =M3UA");
    exit(0);
#endif

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    Pst *smPst = lpRep->getPst (BP_AIN_SM_MTP2_LAYER);
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();
    lpRep->getMtp2NmbLinks(l_nmbLinks);

    l_mtp2CfgRet = cfgSdGen(l_nmbLinks, smPst->dstProcId, 0);

    if(RFAILED == l_mtp2CfgRet) {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : cfgSdGen failed", miTransId);
      return BP_AIN_SM_FAIL;
    }

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::mtp2GenCfg",
        miTransId);

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   mtp2DLSapCfg()
 *
 *     Desc:  Lower SAP Configuration for MTP3 Layer
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
int
INGwSmCfgHdlr::mtp2DLSapCfg (StackReqResp *stackReq) 
{
  
  logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> : Entering INGwSmCfgHdlr::mtp2DLSapCfg",
      miTransId);
  U8 sapType;
  U8 sapFormat;
  char *envSapType;
  char *envSapFormat;
  S16 l_mtp2CfgRet = ROK;
  int liRetVal;
  AddLink *addLink;

  addLink = &stackReq->req.u.lnk;

  envSapType = getenv("LINK_TYPE_LSL0_HSL1");
	if(envSapType != NULL)
	{
		sapType = atoi(envSapType);
	}
  else
  {
    logger.logMsg (ALWAYS_FLAG, 0,
        "LINK_TYPE_LSL0_HSL1 env not set, putting default as LSL");
    /* LSL=0, HSL=1 */
    sapType = 0;
  }

  envSapFormat = getenv("LINK_FRMT_NORM0_EXT1");
	if(envSapFormat != NULL)
	{
		sapFormat = atoi(envSapFormat);
	}
  else
  {
    logger.logMsg (ALWAYS_FLAG, 0,
        "LINK_FRMT_NORMSEQ0_EXTSEQ1 env not set, putting default as NORMAL SEQ");
    /* Normal seq num=0, Extended seq num=1 */
    sapFormat = 0;
  }

	logger.logMsg (ALWAYS_FLAG, 0,
         "mtp2DLSapCfg()::DLSAPs linkType<%d> physPort<%d> timeSlot<%d> "
         "mtp2UsapId[%d] ProcId[%d]",
				 addLink->lnkType, addLink->physPort,addLink->timeSlot,
         addLink->mtp2UsapId, addLink->mtp2ProcId);

  // get number of DLSAPS
  l_mtp2CfgRet = cfgSdDLSAP(addLink->mtp2UsapId, addLink->lnkType,
                   addLink->physPort,addLink->timeSlot, sapType, sapFormat,
                   addLink->mtp2ProcId, 0, 0, miTransId);  

	logger.logMsg(ALWAYS_FLAG, 0, 
	"+VER+cfgSdDLSAP mtp2UsapId:%d, lnkType:%d, physPort:%d, timeSlot:%d,"
	"sapType:%d, sapFormat:%d, mtp2ProcId:%d",
	addLink->mtp2UsapId, addLink->lnkType, addLink->physPort,addLink->timeSlot, 
	sapType, sapFormat, addLink->mtp2ProcId);

  if(RFAILED == l_mtp2CfgRet) {
    logger.logMsg (ERROR_FLAG, 0,
        "TID <%d> : cfgSdDLSAP failed", miTransId);
    return BP_AIN_SM_FAIL;
  }
  stackReq->resp.procId = addLink->mtp2ProcId;
  stackReq->txnType = NORMAL_TXN;
  stackReq->txnStatus = INPROGRESS;
	
  mrDist.updateRspStruct(miTransId,stackReq);


  logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> : Leaving INGwSmCfgHdlr::mtp2DLSapCfg",
      miTransId);

  return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   psfTcapGenCfg()
 *
 *     Desc:  General Configuration for SCCP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::psfTcapGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::psfTcapGenCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getZtMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.zt) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.zt.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTST;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STZTGEN;
    lpHdr->transId     = miTransId;

    /* layer manager */
    l.zt.t.cfg.s.genCfg.smPst.event      = 0; /* unused */
    l.zt.t.cfg.s.genCfg.smPst.selector   = BP_AIN_SM_COUPLING; /* selector */
    l.zt.t.cfg.s.genCfg.smPst.region     = BP_AIN_SM_REGION; /*  Manager memory region */
    l.zt.t.cfg.s.genCfg.smPst.pool       = BP_AIN_SM_POOL; /*  Manager memory pool */
    l.zt.t.cfg.s.genCfg.smPst.prior      = BP_AIN_SM_PRIOR; /* priority */
    l.zt.t.cfg.s.genCfg.smPst.route      = BP_AIN_SM_ROUTE; /* route */
    l.zt.t.cfg.s.genCfg.smPst.dstProcId  = SFndProcId();  /* processor id */
    l.zt.t.cfg.s.genCfg.smPst.dstEnt     = ENTSM; /* entity */
    l.zt.t.cfg.s.genCfg.smPst.dstInst    = BP_AIN_SM_DEST_INST; /* instance */
    l.zt.t.cfg.s.genCfg.smPst.srcProcId  = SFndProcId(); /* processor id */
    l.zt.t.cfg.s.genCfg.smPst.srcEnt     = ENTST; /* entity */
    l.zt.t.cfg.s.genCfg.smPst.srcInst    = BP_AIN_SM_SRC_INST; /* instance */

    Pst *smPst = lpRep->getPst (BP_AIN_SM_PSF_TCAP_LAYER);
    smPst->event = EVTZTMILZTCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    SmMiLztCfgReq(smPst, &(l.zt));

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::psfTcapGenCfg",
        miTransId);

    return BP_AIN_SM_OK;

}


/******************************************************************************
 *
 *     Fun:   psfSccpGenCfg()
 *
 *     Desc:  General Configuration for SCCP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::psfSccpGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::psfSccpGenCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getZpMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.zp) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.zp.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSP;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STZPGEN;
    lpHdr->transId     = miTransId;

    /* layer manager */
    l.zp.t.cfg.s.genCfg.smPst.event      = 0; /* unused */
    l.zp.t.cfg.s.genCfg.smPst.selector   = BP_AIN_SM_COUPLING; /* selector */
    l.zp.t.cfg.s.genCfg.smPst.region     = BP_AIN_SM_REGION; /*  Manager memory region */
    l.zp.t.cfg.s.genCfg.smPst.pool       = BP_AIN_SM_POOL; /*  Manager memory pool */
    l.zp.t.cfg.s.genCfg.smPst.prior      = BP_AIN_SM_PRIOR; /* priority */
    l.zp.t.cfg.s.genCfg.smPst.route      = BP_AIN_SM_ROUTE; /* route */
    l.zp.t.cfg.s.genCfg.smPst.dstProcId  = SFndProcId();  /* processor id */
    l.zp.t.cfg.s.genCfg.smPst.dstEnt     = ENTSM; /* entity */
    l.zp.t.cfg.s.genCfg.smPst.dstInst    = BP_AIN_SM_DEST_INST; /* instance */
    l.zp.t.cfg.s.genCfg.smPst.srcProcId  = SFndProcId(); /* processor id */
    l.zp.t.cfg.s.genCfg.smPst.srcEnt     = ENTSP; /* entity */
    l.zp.t.cfg.s.genCfg.smPst.srcInst    = BP_AIN_SM_SRC_INST; /* instance */

    Pst *smPst = lpRep->getPst (BP_AIN_SM_PSF_SCCP_LAYER);
    smPst->event = EVTZPMILZPCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    SmMiLzpCfgReq(smPst, &l.zp);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::psfSccpGenCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * LDF-MTP3 configuration 
 */
/******************************************************************************
 *
 *     Fun:   ldfMtp3GenCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ldfMtp3GenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ldfMtp3GenCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getDnMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.dn) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.dn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTDN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDNGEN;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.dn.t.cfg.s.gen.sm.selector   = BP_AIN_SM_COUPLING;
    l.dn.t.cfg.s.gen.sm.region     = BP_AIN_SM_REGION;
    l.dn.t.cfg.s.gen.sm.pool       = BP_AIN_SM_POOL;
    l.dn.t.cfg.s.gen.sm.prior      = BP_AIN_SM_PRIOR;
    l.dn.t.cfg.s.gen.sm.route      = BP_AIN_SM_ROUTE;
    l.dn.t.cfg.s.gen.sm.dstProcId  = SFndProcId();
    l.dn.t.cfg.s.gen.sm.dstEnt     = ENTSM;
    l.dn.t.cfg.s.gen.sm.dstInst    = BP_AIN_SM_DEST_INST;
    l.dn.t.cfg.s.gen.sm.srcProcId  = SFndProcId();
    l.dn.t.cfg.s.gen.sm.srcEnt     = ENTDN;
    l.dn.t.cfg.s.gen.sm.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
    smPst->event = EVTLDNCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


    SmMiLdnCfgReq(smPst, &l.dn);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ldfMtp3GenCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * LDF-MTP3 configuration 
 */
/******************************************************************************
 *
 *     Fun:   ldfMtp3RsetMapCriticalCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ldfMtp3RsetMapCriticalCfg()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ldfMtp3RsetMapCriticalCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getDnMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL, l.dn) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.dn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTDN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDNRSET;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif


    Pst *smPst = lpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
    smPst->event = EVTLDNCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


    SmMiLdnCfgReq(smPst, &l.dn);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ldfMtp3RsetMapCriticalCfg",
        miTransId);

    return BP_AIN_SM_OK;
}



/*
 * LDF-MTP3 configuration 
 */
/******************************************************************************
 *
 *     Fun:   ldfMtp3RsetMapNonCriticalCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ldfMtp3RsetMapNonCriticalCfg()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ldfMtp3RsetMapNonCriticalCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getDnMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL, l.dn) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.dn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTDN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDNRSET;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif


    Pst *smPst = lpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
    smPst->event = EVTLDNCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


    SmMiLdnCfgReq(smPst, &l.dn);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ldfMtp3RsetMapNonCriticalCfg",
        miTransId);

    return BP_AIN_SM_OK;
}



/*
 * LDF-MTP3 configuration 
 */
/******************************************************************************
 *
 *     Fun:   ldfMtp3RsetMapDefCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ldfMtp3RsetMapDefCfg()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ldfMtp3RsetMapDefCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getDnMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF, l.dn) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.dn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTDN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDNRSET;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif


    Pst *smPst = lpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
    smPst->event = EVTLDNCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


    SmMiLdnCfgReq(smPst, &l.dn);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ldfMtp3RsetMapDefCfg",
        miTransId);

    return BP_AIN_SM_OK;
}

/*
 * LDF-MTP3 configuration 
 */
/******************************************************************************
 *
 *     Fun:   ldfMtp3USapCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ldfMtp3USapCfg(StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ldfMtp3USapCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }
#if 0
    //get the management structure from the DOM Tree
    if (lpRep->getDnMngmt (BP_AIN_SM_SUBTYPE_USPCFG, l.dn) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }
#endif
    l.dn.t.cfg.s.sap.nmbSaps = 1;
    l.dn.t.cfg.s.sap.sap[0].sapId = stackReq->req.u.addUserPart.mtp3UsapId;
    l.dn.t.cfg.s.sap.sap[0].var = stackReq->req.u.addUserPart.lnkType; 
    if (stackReq->req.u.addUserPart.lnkType == LSN_SW_ITU)
       l.dn.t.cfg.s.sap.sap[0].dpcLen = DPC14; 
    else if (stackReq->req.u.addUserPart.lnkType == LSN_SW_ANS)
       l.dn.t.cfg.s.sap.sap[0].dpcLen = DPC24; 
    else if (stackReq->req.u.addUserPart.lnkType == LSN_SW_TTC ||
						stackReq->req.u.addUserPart.lnkType == LSN_SW_NTT) /* TTC Japan */
		{
       l.dn.t.cfg.s.sap.sap[0].dpcLen = DPC16; 
		}

		logger.logMsg(ALWAYS_FLAG, 0, 
		"+VER+ldfMtp3USapCfg nmbSaps:%d, sapId:%d, var:%d, lnkType:%d, dpcLen:%d",
		l.dn.t.cfg.s.sap.nmbSaps, l.dn.t.cfg.s.sap.sap[0].sapId,
		l.dn.t.cfg.s.sap.sap[0].var, stackReq->req.u.addUserPart.lnkType,
		l.dn.t.cfg.s.sap.sap[0].dpcLen);

    //initialize the header
    Header* lpHdr = &(l.dn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTDN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDNNSAP;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif


    Pst *smPst = lpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
    smPst->event = EVTLDNCFGREQ;
    smPst->dstProcId = stackReq->procId;
    smPst->srcProcId = SFndProcId();


    SmMiLdnCfgReq(smPst, &l.dn);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ldfMtp3USapCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * LDF-MTP3 configuration 
 */
/******************************************************************************
 *
 *     Fun:   ldfMtp3LSapCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ldfMtp3LSapCfg(StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ldfMtp3LSapCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }
#if 0
    //get the management structure from the DOM Tree
    if (lpRep->getDnMngmt (BP_AIN_SM_SUBTYPE_LSPCFG, l.dn) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }
#endif    
    l.dn.t.cfg.s.sap.nmbSaps = 1;
    l.dn.t.cfg.s.sap.sap[0].sapId = stackReq->req.u.lnk.mtp3LsapId;
    l.dn.t.cfg.s.sap.sap[0].var = stackReq->req.u.lnk.lnkType; 
    l.dn.t.cfg.s.sap.sap[0].dpcLen = stackReq->req.u.lnk.dpcLen; 

		logger.logMsg(ALWAYS_FLAG,0, 
		"+VER+ldfMtp3LSapCfg: nmbSaps:%d, sapId:%d, var:%d, dpcLen:%d",
    l.dn.t.cfg.s.sap.nmbSaps, l.dn.t.cfg.s.sap.sap[0].sapId,
    l.dn.t.cfg.s.sap.sap[0].var, l.dn.t.cfg.s.sap.sap[0].dpcLen);


    //initialize the header
    Header* lpHdr = &(l.dn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTDN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDNDLSAP;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif


    Pst *smPst = lpRep->getPst (BP_AIN_SM_LDF_MTP3_LAYER);
    smPst->event = EVTLDNCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = stackReq->procId;

    printf("[+INC+] %s:%d ldfMtp3LSapCfg(): l.dn.t.cfg.s.sap.sap[0].sapId[%d] smPst->dstProcId[%d]\n", __FILE__, __LINE__, l.dn.t.cfg.s.sap.sap[0].sapId, smPst->dstProcId);

    SmMiLdnCfgReq(smPst, &l.dn);

    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);
 
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ldfMtp3LSapCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * LDF-M3ua configuration 
 */
/******************************************************************************
 *
 *     Fun:   ldfM3uaGenCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ldfM3uaGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ldfM3uaGenCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getDvMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.dv) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.dv.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTDV;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDVGEN;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.dv.t.cfg.s.gen.sm.selector   = BP_AIN_SM_COUPLING;
    l.dv.t.cfg.s.gen.sm.region     = BP_AIN_SM_REGION;
    l.dv.t.cfg.s.gen.sm.pool       = BP_AIN_SM_POOL;
    l.dv.t.cfg.s.gen.sm.prior      = BP_AIN_SM_PRIOR;
    l.dv.t.cfg.s.gen.sm.route      = BP_AIN_SM_ROUTE;
    l.dv.t.cfg.s.gen.sm.dstProcId  = SFndProcId();
    l.dv.t.cfg.s.gen.sm.dstEnt     = ENTSM;
    l.dv.t.cfg.s.gen.sm.dstInst    = BP_AIN_SM_DEST_INST;
    l.dv.t.cfg.s.gen.sm.srcProcId  = SFndProcId();
    l.dv.t.cfg.s.gen.sm.srcEnt     = ENTDV;
    l.dv.t.cfg.s.gen.sm.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
    smPst->event = EVTLDVCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();
    smPst->srcEnt = ENTSM;
    smPst->dstEnt = ENTDV;


    SmMiLdvCfgReq(smPst, &l.dv);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ldfM3uaGenCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * LDF-M3ua configuration 
 */
/******************************************************************************
 *
 *     Fun:   ldfM3uaRsetMapCriticalCfg()
 *
 *     Desc:  LDF M3UA Critical Rset configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ldfM3uaRsetMapCriticalCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ldfM3uaRsetMapCriticalCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getDvMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL, l.dv) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.dv.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTDV;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDVRSET;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif


    Pst *smPst = lpRep->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
    smPst->event = EVTLDVCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


    SmMiLdvCfgReq(smPst, &l.dv);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ldfM3uaRsetMapCriticalCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * LDF-M3ua configuration 
 */
/******************************************************************************
 *
 *     Fun:   ldfM3uaRsetMapNonCriticalCfg()
 *
 *     Desc:  LDF M3UA Non-Critical Rset Configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ldfM3uaRsetMapNonCriticalCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ldfM3uaRsetMapNonCriticalCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getDvMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL, l.dv) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.dv.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTDV;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDVRSET;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif


    Pst *smPst = lpRep->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
    smPst->event = EVTLDVCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


    SmMiLdvCfgReq(smPst, &l.dv);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ldfM3uaRsetMapNonCriticalCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * LDF-M3ua configuration 
 */
/******************************************************************************
 *
 *     Fun:   ldfM3uaRsetMapDefCfg()
 *
 *     Desc:  LDF M3UA Default Rset configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ldfM3uaRsetMapDefCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ldfM3uaRsetMapDefCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getDvMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF, l.dv) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.dv.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTDV;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDVRSET;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif


    Pst *smPst = lpRep->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
    smPst->event = EVTLDVCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


    SmMiLdvCfgReq(smPst, &l.dv);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ldfM3uaRsetMapDefCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * LDF-M3ua configuration 
 */
/******************************************************************************
 *
 *     Fun:   ldfM3uaUSapCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ldfM3uaUSapCfg (StackReqResp *stackReq)
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ldfM3uaUSapCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }
#if 0
    //get the management structure from the DOM Tree
    if (lpRep->getDvMngmt (BP_AIN_SM_SUBTYPE_USPCFG, l.dv) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }
#endif


    l.dv.t.cfg.s.sap.nmbSaps = 1;
    l.dv.t.cfg.s.sap.sap[0].sapId = stackReq->req.u.addUserPart.m3uaUsapId;
    l.dv.t.cfg.s.sap.sap[0].nwkId = stackReq->req.u.addUserPart.nwId;

    //initialize the header
    Header* lpHdr = &(l.dv.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTDV;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDVNSAP;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif


    Pst *smPst = lpRep->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
    smPst->event = EVTLDVCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = stackReq->procId;

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Sending Sap Id  in Add User Part<%d>",
        miTransId,l.dv.t.cfg.s.sap.sap[0].sapId);

    SmMiLdvCfgReq(smPst, &l.dv);
    stackReq->resp.procId = stackReq->procId;
    stackReq->txnType = NORMAL_TXN;
    stackReq->txnStatus = INPROGRESS;

    mrDist.updateRspStruct(miTransId,stackReq);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ldfM3uaUSapCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * LDF-M3ua configuration 
 */
/******************************************************************************
 *
 *     Fun:   ldfM3uaNwkCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::ldfM3uaNwkCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::ldfM3uaNwkCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getDvMngmt (BP_AIN_SM_SUBTYPE_NWKCFG, l.dv) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.dv.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTDV;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STDVNWK;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif


    Pst *smPst = lpRep->getPst (BP_AIN_SM_LDF_M3UA_LAYER);
    smPst->event = EVTLDVCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


    SmMiLdvCfgReq(smPst, &l.dv);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::ldfM3uaNwkCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * PSF M3UA Configuration
 */
/******************************************************************************
 *
 *     Fun:   INGwSmCfgHdlr()
 *
 *     Desc:  PSF M3UA Default Rset Configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::psfM3uaRsetMapDefCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::psfm3uaRsetMapDefCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (lpRep->getZvMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF, l.zv) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.zv.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIT;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STZVRSET;
    lpHdr->transId     = miTransId;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_PSF_M3UA_LAYER);
    smPst->event = EVTZVMILZVCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


    SmMiLzvCfgReq(smPst, &l.zv);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::psfM3uaRsetMapDefCfg",
        miTransId);

    return BP_AIN_SM_OK;

}

/******************************************************************************
 *
 *     Fun:   INGwSmCfgHdlr()
 *
 *     Desc:  PSF Non Critical Rset Configuration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::psfM3uaRsetMapNonCriticalCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::psfm3uaRsetMapNonCriticalCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (lpRep->getZvMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL, l.zv) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

#if 0
    memset ((void *)&(l.zv), 0, sizeof (l.zv));
    l.zv.t.cfg.s.rsetMapCfg.rType.type = 2;
    l.zv.t.cfg.s.rsetMapCfg.rType.qual = 2;

    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.ifType = 3;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.upper.type.dist = 2;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.upper.type.qual= 3;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.upper.u.prefProc.range.start= 2;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.upper.u.prefProc.range.end= 5;

    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.lower.type.dist = 2;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.lower.type.qual= 3;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.lower.u.prefProc.range.start= 6;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.lower.u.prefProc.range.end= 10;
    
#endif

#if 1
    memset ((void *)&(l.zv), 0, sizeof (l.zv));
    l.zv.t.cfg.s.rsetMapCfg.rType.type = 2;
    l.zv.t.cfg.s.rsetMapCfg.rType.qual = 2;

    //l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.upper.u.dyn.keyVal.slsRange = 32;

    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.ifType = 3;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.upper.type.dist = 2;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.upper.type.qual= 2;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.upper.u.dyn.range.start= 2;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.upper.u.dyn.range.end= 6;

    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.lower.type.dist = 2;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.lower.type.qual= 2;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.lower.u.dyn.range.start= 7;
    l.zv.t.cfg.s.rsetMapCfg.u.nonCrit.lower.u.dyn.range.end= 11;
    
#endif

    //initialize the header
    Header *lpHdr = &(l.zv.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIT;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STZVRSET;
    lpHdr->transId     = miTransId;


    Pst *smPst = lpRep->getPst (BP_AIN_SM_PSF_M3UA_LAYER);
    smPst->event = EVTZVMILZVCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();

    

    SmMiLzvCfgReq(smPst, &l.zv);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::psfM3uaRsetMapNonCriticalCfg",
        miTransId);

    return BP_AIN_SM_OK;

}
/******************************************************************************
 *
 *     Fun:   INGwSmCfgHdlr()
 *
 *     Desc:  PSF Critical Rset Configuration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::psfM3uaRsetMapCriticalCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::psfm3uaRsetMapCriticalCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (lpRep->getZvMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL, l.zv) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.zv.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIT;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STZVRSET;
    lpHdr->transId     = miTransId;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_PSF_M3UA_LAYER);
    smPst->event = EVTZVMILZVCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


    SmMiLzvCfgReq(smPst, &l.zv);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::psfM3uaRsetMapCriticalCfg",
        miTransId);

    return BP_AIN_SM_OK;

}


/******************************************************************************
 *
 *     Fun:   INGwSmCfgHdlr()
 *
 *     Desc:  PSF General Configuration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::psfM3uaGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::psfm3uaGenCfg",
        miTransId);

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    if (lpRep->getZvMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.zv) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header *lpHdr = &(l.zv.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTIT;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STZVGEN;
    lpHdr->transId     = miTransId;
    /* layer manager */

    l.zv.t.cfg.s.genCfg.smPst.selector  = BP_AIN_SM_COUPLING;     /* selector */
    l.zv.t.cfg.s.genCfg.smPst.region    = BP_AIN_SM_REGION;        /* region */
    l.zv.t.cfg.s.genCfg.smPst.pool      = BP_AIN_SM_POOL;       /* pool */
    l.zv.t.cfg.s.genCfg.smPst.prior     = BP_AIN_SM_PRIOR;        /* priority */
    l.zv.t.cfg.s.genCfg.smPst.route     = BP_AIN_SM_ROUTE;       /* route */
    l.zv.t.cfg.s.genCfg.smPst.dstProcId = SFndProcId();  /* dst proc id */
    l.zv.t.cfg.s.genCfg.smPst.dstEnt    = ENTSM;         /* dst entity */
    l.zv.t.cfg.s.genCfg.smPst.dstInst   = BP_AIN_SM_DEST_INST;     /* dst inst */
    l.zv.t.cfg.s.genCfg.smPst.srcProcId = SFndProcId();  /* src proc id */
    l.zv.t.cfg.s.genCfg.smPst.srcEnt    = ENTIT;         /* src entity */
    l.zv.t.cfg.s.genCfg.smPst.srcInst   = BP_AIN_SM_SRC_INST;     /* src inst */


    Pst *smPst = lpRep->getPst (BP_AIN_SM_PSF_M3UA_LAYER);
    smPst->event = EVTZVMILZVCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


    SmMiLzvCfgReq(smPst, &l.zv);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::psfM3uaGenCfg",
        miTransId);

    return BP_AIN_SM_OK;

}

/*
 * PSF-MTP3 configuration 
 */
/******************************************************************************
 *
 *     Fun:   psfMtp3GenCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::psfMtp3GenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::psfMtp3GenCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getZnMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.zn) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.zn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STZNGEN;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.zn.t.cfg.s.genCfg.smPst.selector   = BP_AIN_SM_COUPLING;
    l.zn.t.cfg.s.genCfg.smPst.region     = BP_AIN_SM_REGION;
    l.zn.t.cfg.s.genCfg.smPst.pool       = BP_AIN_SM_POOL;
    l.zn.t.cfg.s.genCfg.smPst.prior      = BP_AIN_SM_PRIOR;
    l.zn.t.cfg.s.genCfg.smPst.route      = BP_AIN_SM_ROUTE;
    l.zn.t.cfg.s.genCfg.smPst.dstProcId  = SFndProcId();
    l.zn.t.cfg.s.genCfg.smPst.dstEnt     = ENTSM;
    l.zn.t.cfg.s.genCfg.smPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.zn.t.cfg.s.genCfg.smPst.srcProcId  = SFndProcId();
    l.zn.t.cfg.s.genCfg.smPst.srcEnt     = ENTSN;
    l.zn.t.cfg.s.genCfg.smPst.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_PSF_MTP3_LAYER);
    smPst->event = EVTZNMILZNCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();

    printZnGenStruct(l.zn);
       SmMiLznCfgReq(smPst, &l.zn);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::psfMtp3GenCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * PSF-MTP3 configuration 
 */
/******************************************************************************
 *
 *     Fun:   psfMtp3RsetMapCriticalCfg()
 *
 *     Desc:  PSF MTP3 Critical Rset Map Configuration 
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::psfMtp3RsetMapCriticalCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::psfMtp3RsetMapCriticalCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getZnMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL, l.zn) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.zn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STZNRSET;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    Pst *smPst = lpRep->getPst (BP_AIN_SM_PSF_MTP3_LAYER);
    smPst->event = EVTZNMILZNCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();

   printZnRset(l.zn.t.cfg.s.rsetMapCfg,BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL);
    SmMiLznCfgReq(smPst, &l.zn);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::psfMtp3RsetMapCriticalCfg",
        miTransId);

    return BP_AIN_SM_OK;
}

/*
 * PSF-MTP3 configuration 
 */
/******************************************************************************
 *
 *     Fun:   psfMtp3RsetMapNonCrit()
 *
 *     Desc:  PSF-M3UA Non-Critical Rset Configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::psfMtp3RsetMapNonCrit()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::psfMtp3RsetMapNonCrit",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getZnMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL, l.zn) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

#if 0
    memset ((void *)&(l.zn), 0, sizeof (l.zn));

    l.zn.t.cfg.s.rsetMapCfg.rType.type = 2;
    l.zn.t.cfg.s.rsetMapCfg.rType.qual = 2;

    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.ifType = 3;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.upper.type.dist = 2;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.upper.type.qual= 3;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.upper.u.sameProc.range.start= 6;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.upper.u.sameProc.range.end= 10;

    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.lower.type.dist = 2;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.lower.type.qual= 3;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.lower.u.sameProc.range.start= 2;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.lower.u.sameProc.range.end= 5;
#endif

#if 1
    memset ((void *)&(l.zn), 0, sizeof (l.zn));

    l.zn.t.cfg.s.rsetMapCfg.rType.type = 2;
    l.zn.t.cfg.s.rsetMapCfg.rType.qual = 2;

    //l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.upper.u.dyn.keyVal.slsRange = 32;

    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.ifType = 3;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.upper.type.dist = 2;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.upper.type.qual= 2;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.upper.u.dyn.range.start= 2;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.upper.u.dyn.range.end= 6;

    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.lower.type.dist = 2;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.lower.type.qual= 2;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.lower.u.dyn.range.start= 7;
    l.zn.t.cfg.s.rsetMapCfg.u.nonCrit.lower.u.dyn.range.end= 11;
#endif

    //initialize the header
    Header* lpHdr = &(l.zn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STZNRSET;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    Pst *smPst = lpRep->getPst (BP_AIN_SM_PSF_MTP3_LAYER);
    smPst->event = EVTZNMILZNCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


   printZnRset(l.zn.t.cfg.s.rsetMapCfg,BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF);
    SmMiLznCfgReq(smPst, &l.zn);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::psfMtp3RsetMapNonCrit",
        miTransId);

    return BP_AIN_SM_OK;
}
/*
 * PSF-MTP3 configuration 
 */
/******************************************************************************
 *
 *     Fun:   psfMtp3RsetMapDefCfg()
 *
 *     Desc:  PSF MTP3 Default Rset Configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::psfMtp3RsetMapDefCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::psfMtp3RsetMapDefCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getZnMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF, l.zn) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.zn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STZNRSET;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    Pst *smPst = lpRep->getPst (BP_AIN_SM_PSF_MTP3_LAYER);
    smPst->event = EVTZNMILZNCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();


   printZnRset(l.zn.t.cfg.s.rsetMapCfg,BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF);
    SmMiLznCfgReq(smPst, &l.zn);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::psfMtp3RsetMapDefCfg",
        miTransId);

    return BP_AIN_SM_OK;
}

/*
 * PSF-MTP3 configuration 
 */
/******************************************************************************
 *
 *     Fun:   psfMtp3RsetMapNonCriticalCfg()
 *
 *     Desc:  General COnfiguration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::psfMtp3RsetMapNonCriticalCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::psfMtp3RsetMapNonCriticalCfg",
        miTransId);
    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //get the management structure from the DOM Tree
    if (lpRep->getZnMngmt (BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL, l.zn) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.zn.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSN;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STZNRSET;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.zn.t.cfg.s.genCfg.smPst.selector   = BP_AIN_SM_COUPLING;
    l.zn.t.cfg.s.genCfg.smPst.region     = BP_AIN_SM_REGION;
    l.zn.t.cfg.s.genCfg.smPst.pool       = BP_AIN_SM_POOL;
    l.zn.t.cfg.s.genCfg.smPst.prior      = BP_AIN_SM_PRIOR;
    l.zn.t.cfg.s.genCfg.smPst.route      = BP_AIN_SM_ROUTE;
    l.zn.t.cfg.s.genCfg.smPst.dstProcId  = SFndProcId();
    l.zn.t.cfg.s.genCfg.smPst.dstEnt     = ENTSM;
    l.zn.t.cfg.s.genCfg.smPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.zn.t.cfg.s.genCfg.smPst.srcProcId  = SFndProcId();
    l.zn.t.cfg.s.genCfg.smPst.srcEnt     = ENTSN;
    l.zn.t.cfg.s.genCfg.smPst.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_PSF_MTP3_LAYER);
    smPst->event = EVTZNMILZNCFGREQ;
    smPst->srcProcId = SFndProcId();
    smPst->dstProcId = SFndProcId();
    smPst->dstEnt= ENTSN;
    smPst->srcEnt= ENTSM;


    printZnRset(l.zn.t.cfg.s.rsetMapCfg,BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL);

    SmMiLznCfgReq(smPst, &l.zn);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::psfMtp3RsetMapNonCriticalCfg",
        miTransId);

    return BP_AIN_SM_OK;

}


/*
 * MR configuration 
 */
/******************************************************************************
 *
 *     Fun:   mrGenCfg()
 *
 *     Desc:  General COnfiguration for MR - Message Router
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::mrGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::mrGenCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    cmMemset((U8*)&(l.mr), 0, sizeof(MrMngmt));

    //get the management structure from the DOM Tree
    if (lpRep->getMrMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.mr) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.mr.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTMR;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STMRGEN;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.mr.s.cfg.smPst.selector   = BP_AIN_SM_COUPLING;
    l.mr.s.cfg.smPst.region     = BP_AIN_SM_REGION;
    l.mr.s.cfg.smPst.pool       = BP_AIN_SM_POOL;
    l.mr.s.cfg.smPst.prior      = BP_AIN_SM_PRIOR;
    l.mr.s.cfg.smPst.route      = BP_AIN_SM_ROUTE;
    l.mr.s.cfg.smPst.dstProcId  = SFndProcId();
    l.mr.s.cfg.smPst.dstEnt     = ENTSM;
    l.mr.s.cfg.smPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.mr.s.cfg.smPst.srcProcId  = SFndProcId();
    l.mr.s.cfg.smPst.srcEnt     = ENTMR;
    l.mr.s.cfg.smPst.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_MR_LAYER);
    smPst->event = EVTLMRCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();
  
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> maxEnt = %d maxInst = %d maxRsets = %d timeRes=%d : Leaving INGwSmCfgHdlr::mrGenCfg",
        miTransId,l.mr.s.cfg.maxEntities,l.mr.s.cfg.maxInstances,l.mr.s.cfg.maxRsets,l.mr.s.cfg.timeRes);

    SmMiLmrCfgReq(smPst, &l.mr);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::mrGenCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * SH configuration 
 */
/******************************************************************************
 *
 *     Fun:   shGenCfg()
 *
 *     Desc:  General Configuration for SH - System Agent
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::shGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::shGenCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }


    //get the management structure from the DOM Tree
    if (lpRep->getShMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.sh) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }
    //initialize the header
    Header* lpHdr = &(l.sh.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSH;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSHGEN;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.sh.t.cfg.shGen.smPst.selector   = BP_AIN_SM_COUPLING;
    l.sh.t.cfg.shGen.smPst.region     = BP_AIN_SM_REGION;
    l.sh.t.cfg.shGen.smPst.pool       = BP_AIN_SM_POOL;
    l.sh.t.cfg.shGen.smPst.prior      = BP_AIN_SM_PRIOR;
    l.sh.t.cfg.shGen.smPst.route      = BP_AIN_SM_ROUTE;
    l.sh.t.cfg.shGen.smPst.dstProcId  = SFndProcId();
    l.sh.t.cfg.shGen.smPst.dstEnt     = ENTSM;
    l.sh.t.cfg.shGen.smPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.sh.t.cfg.shGen.smPst.srcProcId  = SFndProcId();
    l.sh.t.cfg.shGen.smPst.srcEnt     = ENTSH;
    l.sh.t.cfg.shGen.smPst.srcInst    = BP_AIN_SM_SRC_INST;

    l.sh.t.cfg.shGen.remotePst.selector   = BP_AIN_SM_COUPLING;
    l.sh.t.cfg.shGen.remotePst.region     = BP_AIN_SM_REGION;
    l.sh.t.cfg.shGen.remotePst.pool       = BP_AIN_SM_POOL;
    l.sh.t.cfg.shGen.remotePst.prior      = BP_AIN_SM_PRIOR;
    l.sh.t.cfg.shGen.remotePst.route      = BP_AIN_SM_ROUTE;
    l.sh.t.cfg.shGen.remotePst.dstProcId  = SFndProcId();
    l.sh.t.cfg.shGen.remotePst.dstEnt     = 0;
    l.sh.t.cfg.shGen.remotePst.dstInst    = BP_AIN_SM_DEST_INST;
    l.sh.t.cfg.shGen.remotePst.srcProcId  = SFndProcId();
    l.sh.t.cfg.shGen.remotePst.srcEnt     = ENTSH;
    l.sh.t.cfg.shGen.remotePst.srcInst    = BP_AIN_SM_SRC_INST;

    l.sh.t.cfg.shGen.localPst.selector   = BP_AIN_SM_COUPLING;
    l.sh.t.cfg.shGen.localPst.region     = BP_AIN_SM_REGION;
    l.sh.t.cfg.shGen.localPst.pool       = BP_AIN_SM_POOL;
    l.sh.t.cfg.shGen.localPst.prior      = BP_AIN_SM_PRIOR;
    l.sh.t.cfg.shGen.localPst.route      = BP_AIN_SM_ROUTE;
    l.sh.t.cfg.shGen.localPst.dstProcId  = SFndProcId();
    l.sh.t.cfg.shGen.localPst.dstEnt     = 0;
    l.sh.t.cfg.shGen.localPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.sh.t.cfg.shGen.localPst.srcProcId  = SFndProcId();
    l.sh.t.cfg.shGen.localPst.srcEnt     = ENTSH;
    l.sh.t.cfg.shGen.localPst.srcInst    = BP_AIN_SM_SRC_INST;

    l.sh.t.cfg.shGen.reverseHdr.msgLen = 0;
    l.sh.t.cfg.shGen.reverseHdr.msgType= 100;
    l.sh.t.cfg.shGen.reverseHdr.version= 10;
    l.sh.t.cfg.shGen.reverseHdr.seqNmb= 0;
    l.sh.t.cfg.shGen.reverseHdr.entId.ent= 0;
    l.sh.t.cfg.shGen.reverseHdr.entId.inst= 0;
    l.sh.t.cfg.shGen.reverseHdr.elmId.elmnt= 0;
    l.sh.t.cfg.shGen.reverseHdr.elmId.elmntInst1= 0;
    l.sh.t.cfg.shGen.reverseHdr.elmId.elmntInst2= 0;
    l.sh.t.cfg.shGen.reverseHdr.elmId.elmntInst3= 0;
    l.sh.t.cfg.shGen.reverseHdr.transId= 0;
    l.sh.t.cfg.shGen.reverseHdr.response.selector= 0;
    l.sh.t.cfg.shGen.reverseHdr.response.prior= 0;
    l.sh.t.cfg.shGen.reverseHdr.response.route= 0;
    l.sh.t.cfg.shGen.reverseHdr.response.mem.region= BP_AIN_SM_REGION;
    l.sh.t.cfg.shGen.reverseHdr.response.mem.pool= BP_AIN_SM_POOL;
    l.sh.t.cfg.shGen.reverseHdr.response.mem.spare= 0;
 
    Pst *smPst = lpRep->getPst (BP_AIN_SM_SH_LAYER);
    smPst->event = EVTLSHCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    SmMiLshCfgReq(smPst, &l.sh);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::shGenCfg",
        miTransId);

    return BP_AIN_SM_OK;
}



/*
 * SG configuration 
 */
/******************************************************************************
 *
 *     Fun:   sgGenCfg()
 *
 *     Desc:  General Configuration for SG - System Agent
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sgGenCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sgGenCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    cmMemset((U8*)&(l.sg), 0, sizeof(SgMngmt));

    //get the management structure from the DOM Tree
    if (lpRep->getSgMngmt (BP_AIN_SM_SUBTYPE_GENCFG, l.sg) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.sg.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSG;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSGGEN;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.sg.t.hi.cfg.gen.smPst.selector   = BP_AIN_SM_COUPLING;
    l.sg.t.hi.cfg.gen.smPst.region     = BP_AIN_SM_REGION;
    l.sg.t.hi.cfg.gen.smPst.pool       = BP_AIN_SM_POOL;
    l.sg.t.hi.cfg.gen.smPst.prior      = BP_AIN_SM_PRIOR;
    l.sg.t.hi.cfg.gen.smPst.route      = BP_AIN_SM_ROUTE;
    l.sg.t.hi.cfg.gen.smPst.dstProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.dstEnt     = ENTSM;
    l.sg.t.hi.cfg.gen.smPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.sg.t.hi.cfg.gen.smPst.srcProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.srcEnt     = ENTSG;
    l.sg.t.hi.cfg.gen.smPst.srcInst    = BP_AIN_SM_SRC_INST;

    //l.sg.t.hi.cfg.gen.remotePst.selector   = BP_AIN_SM_COUPLING;
    //l.sg.t.hi.cfg.gen.remotePst.region     = BP_AIN_SM_REGION;
    //l.sg.t.hi.cfg.gen.remotePst.pool       = BP_AIN_SM_POOL;
    //l.sg.t.hi.cfg.gen.remotePst.prior      = BP_AIN_SM_PRIOR;
    //l.sg.t.hi.cfg.gen.remotePst.route      = BP_AIN_SM_ROUTE;
    //l.sg.t.hi.cfg.gen.remotePst.dstProcId  = SFndProcId();
    //l.sg.t.hi.cfg.gen.remotePst.dstEnt     = ENTSM;
    //l.sg.t.hi.cfg.gen.remotePst.dstInst    = BP_AIN_SM_DEST_INST;
    //l.sg.t.hi.cfg.gen.remotePst.srcProcId  = SFndProcId();
    //l.sg.t.hi.cfg.gen.remotePst.srcEnt     = ENTSG;
    //l.sg.t.hi.cfg.gen.remotePst.srcInst    = BP_AIN_SM_SRC_INST;
    

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SG_LAYER);
    smPst->event = EVTLSGCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    l.sg.apiType = LSG_HI_API;

    SmMiLsgCfgReq(smPst, &l.sg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sgGenCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * SG-ENT configuration 
 */
/******************************************************************************
 *
 *     Fun:   sgEntSgCfg()
 *
 *     Desc:  Entity Configuration for SG - System Agent
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sgEntSgCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sgEntSgCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    cmMemset((U8*)&(l.sg), 0, sizeof(SgMngmt));

    //get the management structure from the DOM Tree
    if (lpRep->getSgMngmt (BP_AIN_SM_SUBTYPE_ENT_SG, l.sg) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.sg.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSG;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSGENT;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.sg.t.hi.cfg.gen.smPst.selector   = BP_AIN_SM_COUPLING;
    l.sg.t.hi.cfg.gen.smPst.region     = BP_AIN_SM_REGION;
    l.sg.t.hi.cfg.gen.smPst.pool       = BP_AIN_SM_POOL;
    l.sg.t.hi.cfg.gen.smPst.prior      = BP_AIN_SM_PRIOR;
    l.sg.t.hi.cfg.gen.smPst.route      = BP_AIN_SM_ROUTE;
    l.sg.t.hi.cfg.gen.smPst.dstProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.dstEnt     = ENTSM;
    l.sg.t.hi.cfg.gen.smPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.sg.t.hi.cfg.gen.smPst.srcProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.srcEnt     = ENTSG;
    l.sg.t.hi.cfg.gen.smPst.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SG_LAYER);
    smPst->event = EVTLSGCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    l.sg.apiType = LSG_HI_API;
    
    printSgEnt(l.sg);
    SmMiLsgCfgReq(smPst, &l.sg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sgEntSgCfg",
        miTransId);

    return BP_AIN_SM_OK;
}

/*
 * TUCL-ENT configuration 
 */
/******************************************************************************
 *
 *     Fun:   sgEntTuclCfg()
 *
 *     Desc:  Entity Configuration for TUCL
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sgEntTuclCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sgEntTuclCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    cmMemset((U8*)&(l.sg), 0, sizeof(SgMngmt));

    //get the management structure from the DOM Tree
    if (lpRep->getSgMngmt (BP_AIN_SM_SUBTYPE_ENT_TUCL, l.sg) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.sg.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSG;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSGENT;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.sg.t.hi.cfg.gen.smPst.selector   = BP_AIN_SM_COUPLING;
    l.sg.t.hi.cfg.gen.smPst.region     = BP_AIN_SM_REGION;
    l.sg.t.hi.cfg.gen.smPst.pool       = BP_AIN_SM_POOL;
    l.sg.t.hi.cfg.gen.smPst.prior      = BP_AIN_SM_PRIOR;
    l.sg.t.hi.cfg.gen.smPst.route      = BP_AIN_SM_ROUTE;
    l.sg.t.hi.cfg.gen.smPst.dstProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.dstEnt     = ENTSM;
    l.sg.t.hi.cfg.gen.smPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.sg.t.hi.cfg.gen.smPst.srcProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.srcEnt     = ENTSG;
    l.sg.t.hi.cfg.gen.smPst.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SG_LAYER);
    smPst->event = EVTLSGCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    l.sg.apiType = LSG_HI_API;

    printSgEnt(l.sg);
    SmMiLsgCfgReq(smPst, &l.sg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sgEntTuclCfg",
        miTransId);

    return BP_AIN_SM_OK;
}

/*
 * SCTP-ENT configuration 
 */
/******************************************************************************
 *
 *     Fun:   sgEntSctpCfg()
 *
 *     Desc:  Entity Configuration for SCTP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sgEntSctpCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sgEntSctpCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    cmMemset((U8*)&(l.sg), 0, sizeof(SgMngmt));

    //get the management structure from the DOM Tree
    if (lpRep->getSgMngmt (BP_AIN_SM_SUBTYPE_ENT_SCTP, l.sg) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.sg.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSG;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSGENT;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.sg.t.hi.cfg.gen.smPst.selector   = BP_AIN_SM_COUPLING;
    l.sg.t.hi.cfg.gen.smPst.region     = BP_AIN_SM_REGION;
    l.sg.t.hi.cfg.gen.smPst.pool       = BP_AIN_SM_POOL;
    l.sg.t.hi.cfg.gen.smPst.prior      = BP_AIN_SM_PRIOR;
    l.sg.t.hi.cfg.gen.smPst.route      = BP_AIN_SM_ROUTE;
    l.sg.t.hi.cfg.gen.smPst.dstProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.dstEnt     = ENTSM;
    l.sg.t.hi.cfg.gen.smPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.sg.t.hi.cfg.gen.smPst.srcProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.srcEnt     = ENTSG;
    l.sg.t.hi.cfg.gen.smPst.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SG_LAYER);
    smPst->event = EVTLSGCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    l.sg.apiType = LSG_HI_API;

    printSgEnt(l.sg);
    SmMiLsgCfgReq(smPst, &l.sg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sgEntSctpCfg",
        miTransId);

    return BP_AIN_SM_OK;
}

/*
 * M3UA-ENT configuration 
 */
/******************************************************************************
 *
 *     Fun:   sgEntM3uaCfg()
 *
 *     Desc:  Entity Configuration for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sgEntM3uaCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sgEntM3uaCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    cmMemset((U8*)&(l.sg), 0, sizeof(SgMngmt));

    //get the management structure from the DOM Tree
    if (lpRep->getSgMngmt (BP_AIN_SM_SUBTYPE_ENT_M3UA, l.sg) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.sg.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSG;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSGENT;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.sg.t.hi.cfg.gen.smPst.selector   = BP_AIN_SM_COUPLING;
    l.sg.t.hi.cfg.gen.smPst.region     = BP_AIN_SM_REGION;
    l.sg.t.hi.cfg.gen.smPst.pool       = BP_AIN_SM_POOL;
    l.sg.t.hi.cfg.gen.smPst.prior      = BP_AIN_SM_PRIOR;
    l.sg.t.hi.cfg.gen.smPst.route      = BP_AIN_SM_ROUTE;
    l.sg.t.hi.cfg.gen.smPst.dstProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.dstEnt     = ENTSM;
    l.sg.t.hi.cfg.gen.smPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.sg.t.hi.cfg.gen.smPst.srcProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.srcEnt     = ENTSG;
    l.sg.t.hi.cfg.gen.smPst.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SG_LAYER);
    smPst->event = EVTLSGCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    l.sg.apiType = LSG_HI_API;

    printSgEnt(l.sg);
    SmMiLsgCfgReq(smPst, &l.sg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sgEntM3uaCfg",
        miTransId);

    return BP_AIN_SM_OK;
}

/*
 * MTP2-ENT configuration 
 */
/******************************************************************************
 *
 *     Fun:   sgEntMtp2Cfg()
 *
 *     Desc:  Entity Configuration for MTP2
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sgEntMtp2Cfg()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sgEntMtp2Cfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    cmMemset((U8*)&(l.sg), 0, sizeof(SgMngmt));

    //get the management structure from the DOM Tree
    if (lpRep->getSgMngmt (BP_AIN_SM_SUBTYPE_ENT_MTP2, l.sg) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.sg.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSG;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSGENT;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.sg.t.hi.cfg.gen.smPst.selector   = BP_AIN_SM_COUPLING;
    l.sg.t.hi.cfg.gen.smPst.region     = BP_AIN_SM_REGION;
    l.sg.t.hi.cfg.gen.smPst.pool       = BP_AIN_SM_POOL;
    l.sg.t.hi.cfg.gen.smPst.prior      = BP_AIN_SM_PRIOR;
    l.sg.t.hi.cfg.gen.smPst.route      = BP_AIN_SM_ROUTE;
    l.sg.t.hi.cfg.gen.smPst.dstProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.dstEnt     = ENTSM;
    l.sg.t.hi.cfg.gen.smPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.sg.t.hi.cfg.gen.smPst.srcProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.srcEnt     = ENTSG;
    l.sg.t.hi.cfg.gen.smPst.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SG_LAYER);
    smPst->event = EVTLSGCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    l.sg.apiType = LSG_HI_API;

    printSgEnt(l.sg);
    SmMiLsgCfgReq(smPst, &l.sg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sgEntMtp2Cfg",
        miTransId);

    return BP_AIN_SM_OK;
}

/*
 * MTP3-ENT configuration 
 */
/******************************************************************************
 *
 *     Fun:   sgEntMtp3Cfg()
 *
 *     Desc:  Entity Configuration for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sgEntMtp3Cfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sgEntMtp3Cfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    cmMemset((U8*)&(l.sg), 0, sizeof(SgMngmt));

    //get the management structure from the DOM Tree
    if (lpRep->getSgMngmt (BP_AIN_SM_SUBTYPE_ENT_MTP3, l.sg) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.sg.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSG;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSGENT;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.sg.t.hi.cfg.gen.smPst.selector   = BP_AIN_SM_COUPLING;
    l.sg.t.hi.cfg.gen.smPst.region     = BP_AIN_SM_REGION;
    l.sg.t.hi.cfg.gen.smPst.pool       = BP_AIN_SM_POOL;
    l.sg.t.hi.cfg.gen.smPst.prior      = BP_AIN_SM_PRIOR;
    l.sg.t.hi.cfg.gen.smPst.route      = BP_AIN_SM_ROUTE;
    l.sg.t.hi.cfg.gen.smPst.dstProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.dstEnt     = ENTSM;
    l.sg.t.hi.cfg.gen.smPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.sg.t.hi.cfg.gen.smPst.srcProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.srcEnt     = ENTSG;
    l.sg.t.hi.cfg.gen.smPst.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SG_LAYER);
    smPst->event = EVTLSGCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    l.sg.apiType = LSG_HI_API;

    printSgEnt(l.sg);
    SmMiLsgCfgReq(smPst, &l.sg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sgEntMtp3Cfg",
        miTransId);

    return BP_AIN_SM_OK;
}

/*
 * SCCP-ENT configuration 
 */
/******************************************************************************
 *
 *     Fun:   sgEntSccpCfg()
 *
 *     Desc:  Entity Configuration for SCCP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sgEntSccpCfg ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Entering INGwSmCfgHdlr::sgEntSccpCfg",
        miTransId);

    //For Iphase cards call the configuration requests.

    INGwSmRepository *lpRep = mrDist.getSmRepository ();

    if (lpRep == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Repository pointer is NULL",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    cmMemset((U8*)&(l.sg), 0, sizeof(SgMngmt));

    //get the management structure from the DOM Tree
    if (lpRep->getSgMngmt (BP_AIN_SM_SUBTYPE_ENT_SCCP, l.sg) == BP_AIN_SM_FAIL)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : Management structure couldn't be retrieved from the Repository",
            miTransId);
      return BP_AIN_SM_FAIL;
    }

    //initialize the header
    Header* lpHdr = &(l.sg.hdr);

    lpHdr->msgType     = TCFG;
    lpHdr->entId.ent   = ENTSG;
    lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
    lpHdr->elmId.elmnt = STSGENT;

#if (SN_LMINT3 || SMSN_LMINT3)
    lpHdr->transId     = miTransId;
    lpHdr->response.selector = BP_AIN_SM_COUPLING;
    lpHdr->response.mem.region = BP_AIN_SM_REGION;
    lpHdr->response.mem.pool = BP_AIN_SM_POOL;
    lpHdr->response.prior = BP_AIN_SM_PRIOR;
    lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

    /* layer manager */
    l.sg.t.hi.cfg.gen.smPst.selector   = BP_AIN_SM_COUPLING;
    l.sg.t.hi.cfg.gen.smPst.region     = BP_AIN_SM_REGION;
    l.sg.t.hi.cfg.gen.smPst.pool       = BP_AIN_SM_POOL;
    l.sg.t.hi.cfg.gen.smPst.prior      = BP_AIN_SM_PRIOR;
    l.sg.t.hi.cfg.gen.smPst.route      = BP_AIN_SM_ROUTE;
    l.sg.t.hi.cfg.gen.smPst.dstProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.dstEnt     = ENTSM;
    l.sg.t.hi.cfg.gen.smPst.dstInst    = BP_AIN_SM_DEST_INST;
    l.sg.t.hi.cfg.gen.smPst.srcProcId  = SFndProcId();
    l.sg.t.hi.cfg.gen.smPst.srcEnt     = ENTSG;
    l.sg.t.hi.cfg.gen.smPst.srcInst    = BP_AIN_SM_SRC_INST;

    Pst *smPst = lpRep->getPst (BP_AIN_SM_SG_LAYER);
    smPst->event = EVTLSGCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    l.sg.apiType = LSG_HI_API;

    printSgEnt(l.sg);
    SmMiLsgCfgReq(smPst, &l.sg);

    logger.logMsg (TRACE_FLAG, 0,
        "TID <%d> : Leaving INGwSmCfgHdlr::sgEntSccpCfg",
        miTransId);

    return BP_AIN_SM_OK;
}


/*
 * TCAP-ENT configuration 
 */
/******************************************************************************
 *
 *     Fun:   sgEntTcapCfg()
 *
 *     Desc:  Entity Configuration for TCAP
 *
 *     Notes: None
 *
 *     File:  INGwSmCfgHdlr.C
 *
 *******************************************************************************/
    int
INGwSmCfgHdlr::sgEntTcapCfg ()
{
   logger.logMsg (TRACE_FLAG, 0,
  "TID <%d> : Entering INGwSmCfgHdlr::sgEntTcapCfg",
     miTransId);

 //For Iphase cards call the configuration requests.

 INGwSmRepository *lpRep = mrDist.getSmRepository ();

 if (lpRep == 0)
 {
   logger.logMsg (ERROR_FLAG, 0,
        "TID <%d> : Repository pointer is NULL",
         miTransId);
   return BP_AIN_SM_FAIL;
 }

 cmMemset((U8*)&(l.sg), 0, sizeof(SgMngmt));

 //get the management structure from the DOM Tree
 if (lpRep->getSgMngmt (BP_AIN_SM_SUBTYPE_ENT_TCAP, l.sg) == BP_AIN_SM_FAIL)
 {
   logger.logMsg (ERROR_FLAG, 0,
         "TID <%d> : Management structure couldn't be retrieved from the Repository",
         miTransId);
   return BP_AIN_SM_FAIL;
 }
    //initialize the header
 Header* lpHdr = &(l.sg.hdr);

 lpHdr->msgType     = TCFG;
 lpHdr->entId.ent   = ENTSG;
 lpHdr->entId.inst  = BP_AIN_SM_SRC_INST;
 lpHdr->elmId.elmnt = STSGENT;

#if (SN_LMINT3 || SMSN_LMINT3)
 lpHdr->transId     = miTransId;
 lpHdr->response.selector = BP_AIN_SM_COUPLING;
 lpHdr->response.mem.region = BP_AIN_SM_REGION;
 lpHdr->response.mem.pool = BP_AIN_SM_POOL;
 lpHdr->response.prior = BP_AIN_SM_PRIOR;
 lpHdr->response.route = BP_AIN_SM_ROUTE;
#endif

 /* layer manager */
 l.sg.t.hi.cfg.gen.smPst.selector   = BP_AIN_SM_COUPLING;
 l.sg.t.hi.cfg.gen.smPst.region     = BP_AIN_SM_REGION;
 l.sg.t.hi.cfg.gen.smPst.pool       = BP_AIN_SM_POOL;
 l.sg.t.hi.cfg.gen.smPst.prior      = BP_AIN_SM_PRIOR;
 l.sg.t.hi.cfg.gen.smPst.route      = BP_AIN_SM_ROUTE;
 l.sg.t.hi.cfg.gen.smPst.dstProcId  = SFndProcId();
 l.sg.t.hi.cfg.gen.smPst.dstEnt     = ENTSM;
 l.sg.t.hi.cfg.gen.smPst.dstInst    = BP_AIN_SM_DEST_INST;
 l.sg.t.hi.cfg.gen.smPst.srcProcId  = SFndProcId();
 l.sg.t.hi.cfg.gen.smPst.srcEnt     = ENTSG;
 l.sg.t.hi.cfg.gen.smPst.srcInst    = BP_AIN_SM_SRC_INST;

 Pst *smPst = lpRep->getPst (BP_AIN_SM_SG_LAYER);
 smPst->event = EVTLSGCFGREQ;
    smPst->dstProcId = SFndProcId();
    smPst->srcProcId = SFndProcId();

    l.sg.apiType = LSG_HI_API;

    printSgEnt(l.sg);
 SmMiLsgCfgReq(smPst, &l.sg);

 logger.logMsg (TRACE_FLAG, 0,
     "TID <%d> : Leaving INGwSmCfgHdlr::sgEntTcapCfg",
     miTransId);

 return BP_AIN_SM_OK;
}


/*********************************************************************

     Name: MTP level 2 -  Configuration

     Type: C source file

     Desc: MTP2 configuration can be done in following two ways:

     A) When SM is tightly coupled with SD:
        ----------------------------------

        In this case compile this file along with MTP2 conv lib. Once the MTP2
        task (ENTSD) is registered with SSI, call following functions from
        within tst() function itself to configure MTP2:

           1) For MTP2 general configuration, call

                 ret = cfgSdGen(nmbLnks, dstProcId, selsm2sd);

              where,
                    nmbLnks: Maximum number of MTP2 links i.e. maximum
                             number of DLSAPs

                    dstProcId: Destination TAPA proc id where SD resides.

                    selsm2sd: Selector SM to SD. Values are:
                                0 : loosely coupled SM to SD interface
                                2 : tightly coupled SM to SD interface

           2) For each MTP2 link (DLSAP) call following function to
              configure each DLSAP:

                 ret = cfgSdDLSAP(dlSapId, devId, portAndChannelNum, dstProcId,
                                  selsm2sd, selsd2sn);

              where,
                    dlSapId: ID of the DLSAP. This should be same as service
                             provider id (spId) in MTP3 DLSAP (SNDLSAP).

                    devId: Line card id. Valid values are:
                              0 : for iph_wan_0
                              1 : for iph_wan_1
                              2 : for iph_wan_2
                              3 : for iph_wan_3

                    portAndChannelNum: Port and Channel number on T1/E1 line. 

                   The S16 portAndChannelNum includes 2 things:
                    |<-one byte port num->|<-one byte channelnum->|

                    Port num can be 0-3. This is the virtual port num (not the physical port num).
                    So if on a card two ports are configured for MTP2 and two for ATM,
                    the cards 4 physical ports will provide
                    two MTP2 virtual ports numbered 0 and 1,
                    and two ATM virtual ports numbered 0 and 1.
                    If all four ports are MTP2, then there will be four MTP2 virtual ports, numbered 0-3.

                    Channel num in case of T1 is 0-23, corresponding to timeslots 0 to 23.
                    Channel num in case of E1 is 0-30, corresponding to timeslots 1 to 31, 
                    and timeslot 0 of an E1 is not avail. 
                    Note the virtual channel number 0 maps to T1 ts 0 but E1 ts 1.

                    dstProcId: Destination TAPA proc id where SD resides.

                    selsm2sd: Selector SM to SD. Values are:
                                 0 : loosely coupled SM to SD interface
                                 2 : tightly coupled SM to SD interface

                    selsd2sn: Selector for primitives from SD to SN.
                              Values are:
                                 0: loosely coupled SD - SN interface
                                 2: tightly coupled SD - SN interface

     B) When SM is loosely coupled with SD:
        ----------------------------------

        In this case compile this file along with stack manager files. From
        stack manager, call above mentioned functions to do configuration.
        In additon modify stack manager code to handle config confirm from SD,
        in following manner:

        smActvTsk(pst, mBuf)
        {
           ...
           ...

           / handle config cfm from SD /
           switch (pst->srcEnt)
           {
              case ENTSD:
                 switch (pst->event)
                 {
                    case EVTLSDCFGCFM:
                       / unpack cfg cfm from sd /
                       ret = cmUnpkLsdCfgCfm(SmMiLsdCfgCfm, pst, mBuf);
                       break;

                    default:
                       break;
                 }
            }

            ...
            ...
         }

**********************************************************************/



/* Forward reference */
//PUBLIC S16 cfgSdGen(U16 nmbLnks, ProcId dstProcId, Selector selsm2sd);
//PUBLIC S16 cfgSdDLSAP(U16 dlSapId, S16 swtch, U16 physPort, U16 timeSlot,
//                      U8 sapType, U8 sapFormat, ProcId dstProcId,
//                      Selector selsm2sd, Selector selsd2sn, U32 transId);
//PUBLIC S16 cfgSdDbg(int DbgMask, int action, ProcId dstProcId, Selector selsm2sd);
//
//PUBLIC S16 SmMiLsdCfgCfm(Pst *pst, SdMngmt *cfm);
//PUBLIC S16 SmMiLsdCntrlCfm(Pst *pst, SdMngmt *cfm);
//PUBLIC S16 SmMiLsdStaCfm(Pst *pst, SdMngmt *sta);
//PUBLIC S16 SmMiLsdStsCfm(Pst *pst, Action action, SdMngmt *sts);
//PUBLIC S16 SmMiLsdStaInd(Pst *pst, SdMngmt *sta);
//PUBLIC S16 SmMiLsdTrcInd(Pst *pst, SdMngmt *trc);


/*
*
*       Fun:   cfgSdGen
*
*       Desc:  mtp level 2 - configure general
*
*       Ret:   ROK, RFAILED
*
*       Parameters:
*                   nmbLnks: Maximum number of MTP2 links i.e. maximum
*                            number of DLSAPs
*
*                   dstProcId: Destination TAPA proc id where SD resides.
*
*                   selsm2sd: Selector SM to SD. Values are:
*                               0 : loosely coupled SM to SD interface
*                               2 : tightly coupled SM to SD interface
*
*       Notes: Same selector value for SM to SD is used for SD to SM also
*              with regard to handling config confirm.
*/
#ifdef ANSI
PUBLIC S16 cfgSdGen
(
U16 nmbLnks,             /* Number of mtp2 links */
ProcId dstProcId,        /* destination procId where SD is located */
Selector selsm2sd        /* selector SM to SD */
)
#else
PUBLIC S16 cfgSdGen(nmbLnks, dstProcId, selsm2sd)
U16 nmbLnks;             /* Number of mtp2 links */
ProcId dstProcId;        /* destination procId where SD is located */
Selector selsm2sd;       /* selector SM to SD */
#endif
{
   SdMngmt cfg;          /* management structure */
   Pst pst;              /* layer manager post structure */

   TRC2(cfgSdGen);

   /* initialize cfg struct */
   cmZero((U8 *) &cfg, sizeof(SdMngmt));

   cfg.hdr.msgType = TCFG;              /* configuration req */
   cfg.hdr.entId.ent = ENTSD;           /* entity */
   cfg.hdr.entId.inst = 0;              /* instance */
   cfg.hdr.elmId.elmnt = STGEN;         /* general */
   cfg.hdr.elmId.elmntInst1 = 0;
   cfg.hdr.elmId.elmntInst2 = 0;
   cfg.hdr.elmId.elmntInst3 = 0;
   cfg.hdr.seqNmb = 0;
   cfg.hdr.version = 0;
   cfg.hdr.msgLen = 0;

#if (SD_LMINT3 || SMSD_LMINT3)
   cfg.hdr.transId = 1;
   cfg.hdr.response.route = 0;
   cfg.hdr.response.prior = 0;

   /* use the same selector as SM to SD for SD to SM as well. For, if
    * SM to SD is tightly coupled then the confirm for config will be
    * handled by the SmMiLsdCfgCfm func written within this file. In case
    * SM to SD is loosely coupled, the SmMiLsdCfgCfm func should be invoked
    * by the stack manager activation task through smSdActvTsk func written
    * in this file.
    */
   cfg.hdr.response.selector = selsm2sd;
   cfg.hdr.response.mem.region = BP_AIN_SM_REGION;
   cfg.hdr.response.mem.pool = BP_AIN_SM_POOL;
#endif /* SD_LMINT3 || SMSD_LMINT3 */

   /* use the selector within sm pst as loosely coupled. For, even in case
    * of tightly coupled SM to SD (and SD to SM), the SM functions to handle
    * trace and alarm (if they are invoked at all) are not written in this file
    */
   cfg.t.cfg.s.sdGen.sm.selector = 0; /* loose coupled SD-SM for alarm, trace */
   cfg.t.cfg.s.sdGen.sm.region = BP_AIN_SM_REGION;
   cfg.t.cfg.s.sdGen.sm.pool = BP_AIN_SM_POOL;
   cfg.t.cfg.s.sdGen.sm.prior = 0;
   cfg.t.cfg.s.sdGen.sm.route = 0;
   logger.logMsg (TRACE_FLAG, 0, "MTP2 GenCfg Route [%d]", 
                  cfg.t.cfg.s.sdGen.sm.route);
   cfg.t.cfg.s.sdGen.sm.dstProcId = SFndProcId(); /* self processor id */
   cfg.t.cfg.s.sdGen.sm.dstEnt = ENTSM;
   cfg.t.cfg.s.sdGen.sm.dstInst = 0;
   cfg.t.cfg.s.sdGen.nmbLnks = nmbLnks;   /* Number of physical links */

   /* fill Pst struct */
   pst.selector = selsm2sd;
   pst.region = BP_AIN_SM_REGION;
   pst.pool = BP_AIN_SM_POOL;
   pst.prior = 0;
   pst.route = 0;
   pst.dstProcId = dstProcId;
   pst.dstEnt = ENTSD;
   pst.dstInst = 0;
   pst.srcProcId = SFndProcId();
   pst.srcEnt = ENTSM;
   pst.srcInst = 0;

   /* if SM to SD is tightly coupled, then call SD func directly else
    * pack primitive and post to SD
    */
   (Void) SmMiLsdCfgReq(&pst, &cfg);

   return (ROK);
} /* end of cfgSdGen */

/*
*
*       Fun:   cfgSdDLSAP
*
*       Desc:  mtp level 2 - configure data link sap
*
*       Ret:   ROK, RFAILED
*
*       Parameters:
*                   dlSapId: ID of the DLSAP. This should be same as service
*                            provider id (spId) in MTP3 DLSAP (SNDLSAP).
*
*                   devId: Line card id. Valid values are:
*                             0 : for iph_wan_0
*                             1 : for iph_wan_1
*                             2 : for iph_wan_2
*                             3 : for iph_wan_3
*
*                   portAndChannelNum: Port and Channel number for T1/E1 line.
*
*  The S16 portAndChannelNum includes 2 things:
*   |<-one byte port num->|<-one byte channelnum->|
*
*   Port num can be 0-3. This is the virtual port num (not the physical port num).
*   So if on a card two ports are configured for MTP2 and two for ATM,
*   the cards 4 physical ports will provide
*   two MTP2 virtual ports numbered 0 and 1,
*   and two ATM virtual ports numbered 0 and 1.
*   If all four ports are MTP2, then there will be four MTP2 virtual ports, numbered 0-3.
*
*   Channel num in case of T1 is 0-23, corresponding to timeslots 0 to 23.
*   Channel num in case of E1 is 0-30, corresponding to timeslots 1 to 31, 
*   and timeslot 0 of an E1 is not avail. 
*   Note the virtual channel number 0 maps to T1 ts 0 but E1 ts 1.
*
*
*                   dstProcId: Destination TAPA proc id where SD resides.
*
*                   selsm2sd: Selector SM to SD. Values are:
*                                0 : loosely coupled SM to SD interface
*                                2 : tightly coupled SM to SD interface
*
*                   selsd2sn: Selector for primitives from SD to SN. Values are:
*                                0: loosely coupled SD - SN interface
*                                2: tightly coupled SD - SN interface
*
*       Notes: Same selector value for SM to SD is used for SD to SM also
*              with regard to handling config confirm.
*
*/
PUBLIC S16 cfgSdDLSAP
(
U16 dlSapId,             /* DLSAP id ie. link number */
S16  swtch,                /* line card id, 0, 1 2, 3 etc */
U16 physPort,   /* port */
U16 timeSlot,   /* time slot */
U8 sapType,     /* LSL=0, HSL=1 */ 
U8 sapFormat,   /* Normal seq num=0, Extended seq num=1 */
ProcId dstProcId,        /* destination proc id where SD resides */
Selector selsm2sd,       /* selector SM to SD */
Selector selsd2sm,        /* selector SD to SN */
U32      transId
)
{
   SdMngmt cfg;          /* management structure */
   Pst pst;              /* layer manager post structure */

   /* initialize cfg struct */
   memset((U8 *) &cfg,'\0', sizeof(SdMngmt));

   cfg.hdr.msgType = TCFG;
   cfg.hdr.entId.ent = ENTSD;
   cfg.hdr.entId.inst = 0;
   cfg.hdr.elmId.elmnt = STDLSAP;
   cfg.hdr.elmId.elmntInst1 = dlSapId;
   cfg.hdr.elmId.elmntInst2 = 0;
   cfg.hdr.elmId.elmntInst3 = 0;
   cfg.hdr.seqNmb = 0;
   cfg.hdr.version = 0;
   cfg.hdr.msgLen = 0;

#if (SD_LMINT3 || SMSD_LMINT3)
   cfg.hdr.transId = transId;
   cfg.hdr.response.route = 0;
   cfg.hdr.response.prior = 0;

   /* use the same selector as SM to SD for SD to SM as well. For, if
    * SM to SD is tightly coupled then the confirm for config will be
    * handled by the SmMiLsdCfgCfm func written within this file. In case
    * SM to SD is loosely coupled, the SmMiLsdCfgCfm func should be invoked
    * by the stack manager activation task through smSdActvTsk func written
    * in this file.
    */
   cfg.hdr.response.selector = selsd2sm;
   cfg.hdr.response.mem.region = BP_AIN_SM_REGION;
   cfg.hdr.response.mem.pool = BP_AIN_SM_POOL;
#endif /* SD_LMINT3 || SMSD_LMINT3 */

	std::string protocol;
  int linkSpeed = 48;
	if(0 != INGwIfrPrParamRepository::getInstance().
																			getValue("PROTO", protocol)) {
		protocol = "ITU";
	}

  char * cLnkSpeed = getenv("LINK_SPEED");
  if (cLnkSpeed) {
		linkSpeed = atoi(cLnkSpeed);
  }
  else {
		linkSpeed = 48;
  }

  logger.logMsg (ALWAYS_FLAG, 0, "Configured LINK SPEED %d Kbps", linkSpeed); 

   cfg.t.cfg.s.sdDLSAP.mem.region    = BP_AIN_SM_REGION;
   cfg.t.cfg.s.sdDLSAP.mem.pool      = BP_AIN_SM_POOL;
   cfg.t.cfg.s.sdDLSAP.swtch         = swtch;

   logger.logMsg (ALWAYS_FLAG, 0, "sdDLSAP.swtch %d",
                  cfg.t.cfg.s.sdDLSAP.swtch);

	 if(protocol == "NTT" && linkSpeed == 48) {
	 //if(protocol == "NTT") {
   	cfg.t.cfg.s.sdDLSAP.swtch         = 6; // for NTT;
    logger.logMsg (ALWAYS_FLAG, 0, "Effective LINK SPEED 48 Kbps"); 
	 }
   else {
     logger.logMsg (ALWAYS_FLAG, 0, "Effective LINK SPEED 64 Kbps"); 
   }

   cfg.t.cfg.s.sdDLSAP.priorDl       = PRIOR0;
   cfg.t.cfg.s.sdDLSAP.routeDl       = RTESPEC;
   logger.logMsg (TRACE_FLAG, 0, "MTP2 DLSAP Route [%d]", 
                  cfg.t.cfg.s.sdDLSAP.routeDl);
   cfg.t.cfg.s.sdDLSAP.selectorDl    = selsm2sd;/* selector SD to SN */

   cfg.t.cfg.s.sdDLSAP.physPort      = physPort;
   cfg.t.cfg.s.sdDLSAP.timeSlot      = timeSlot;
#ifdef SD_HSL /* For High Speed Signaling link */
   cfg.t.cfg.s.sdDLSAP.sapType       = sapType;
   cfg.t.cfg.s.sdDLSAP.sapFormat     = sapFormat;
#endif

//filling below params from getenv
    int value = 0;

    getEnvVarInt((char *)"MTP2_TMR_T1", &value);
    if(value != 0)
    {
      cfg.t.cfg.s.sdDLSAP.t1.enb = TRUE;
      cfg.t.cfg.s.sdDLSAP.t1.val = value;
    }
    else
      cfg.t.cfg.s.sdDLSAP.t1.enb = FALSE;

    getEnvVarInt((char *)"MTP2_TMR_T2", &value);
    if(value != 0)
    {
      cfg.t.cfg.s.sdDLSAP.t2.enb = TRUE;
      cfg.t.cfg.s.sdDLSAP.t2.val = value;
    }
    else
      cfg.t.cfg.s.sdDLSAP.t2.enb = FALSE;


    getEnvVarInt((char *)"MTP2_TMR_T3", &value);
    if(value != 0)
    {
      cfg.t.cfg.s.sdDLSAP.t3.enb = TRUE;
      cfg.t.cfg.s.sdDLSAP.t3.val = value;
    }
    else
      cfg.t.cfg.s.sdDLSAP.t3.enb = FALSE;

    getEnvVarInt((char *)"MTP2_TMR_T5", &value);
    if(value != 0)
    {
      cfg.t.cfg.s.sdDLSAP.t5.enb = TRUE;
      cfg.t.cfg.s.sdDLSAP.t5.val = value;
    }
    else
      cfg.t.cfg.s.sdDLSAP.t5.enb = FALSE;

    getEnvVarInt((char *)"MTP2_TMR_T6", &value);
    if(value != 0)
    {
      cfg.t.cfg.s.sdDLSAP.t6.enb = TRUE;
      cfg.t.cfg.s.sdDLSAP.t6.val = value;
    }
    else
      cfg.t.cfg.s.sdDLSAP.t6.enb = FALSE;

    getEnvVarInt((char *)"MTP2_TMR_T7", &value);
    if(value != 0)
    {
      cfg.t.cfg.s.sdDLSAP.t7.enb = TRUE;
      cfg.t.cfg.s.sdDLSAP.t7.val = value;
    }
    else
      cfg.t.cfg.s.sdDLSAP.t7.enb = FALSE;

#ifdef SD_HSL /* For High Speed Signaling Link */
    getEnvVarInt((char *)"MTP2_TMR_T8", &value);
    if(value != 0)
    {
      cfg.t.cfg.s.sdDLSAP.t8.enb = TRUE;
      cfg.t.cfg.s.sdDLSAP.t8.val = value;
    }
    else
      cfg.t.cfg.s.sdDLSAP.t8.enb = FALSE;

    getEnvVarInt((char *)"MTP2_SDTE", &value);
    cfg.t.cfg.s.sdDLSAP.sdTe = value;
   
    getEnvVarInt((char *)"MTP2_SDUE", &value);
    cfg.t.cfg.s.sdDLSAP.sdUe = value;

    getEnvVarInt((char *)"MTP2_SDDE", &value);
    cfg.t.cfg.s.sdDLSAP.sdDe = value;
#endif

    getEnvVarInt((char *)"MTP2_PROVEMRGCY", &value);
    cfg.t.cfg.s.sdDLSAP.provEmrgcy = value;

    getEnvVarInt((char *)"MTP2_PROVNORMAL", &value);
    cfg.t.cfg.s.sdDLSAP.provNormal = value;

    getEnvVarInt((char *)"MTP2_MAXFRMLEN", &value);
    cfg.t.cfg.s.sdDLSAP.maxFrmLen = value;

    getEnvVarInt((char *)"MTP2_SUERM_THRESH", &value);
    cfg.t.cfg.s.sdDLSAP.SUERM_thresh = value;

    getEnvVarInt((char *)"MTP2_SUERM_ERR_RATE", &value);
    cfg.t.cfg.s.sdDLSAP.SUERM_err_rate = value;

    getEnvVarInt((char *)"MTP2_SDTIE", &value);
    cfg.t.cfg.s.sdDLSAP.sdTie = value;

    getEnvVarInt((char *)"MTP2_SDTIN", &value);
    cfg.t.cfg.s.sdDLSAP.sdTin = value;

    getEnvVarInt((char *)"MTP2_SDN2", &value);
    cfg.t.cfg.s.sdDLSAP.sdN2 = value;

    getEnvVarInt((char *)"MTP2_SDCP", &value);
    cfg.t.cfg.s.sdDLSAP.sdCp = value;

#if (SS7_TTC || SS7_NTT)
    getEnvVarInt((char *)"MTP2_TMR_TF", &value);
    if(value != 0)
    {
      cfg.t.cfg.s.sdDLSAP.tf.enb = TRUE;
      cfg.t.cfg.s.sdDLSAP.tf.val = value;
    }
    else
      cfg.t.cfg.s.sdDLSAP.tf.enb = FALSE;

    getEnvVarInt((char *)"MTP2_TMR_TO", &value);
    if(value != 0)
    {
      cfg.t.cfg.s.sdDLSAP.to.enb = TRUE;
      cfg.t.cfg.s.sdDLSAP.to.val = value;
    }
    else
      cfg.t.cfg.s.sdDLSAP.to.enb = FALSE;

    getEnvVarInt((char *)"MTP2_TMR_TA", &value);
    if(value != 0)
    {
      cfg.t.cfg.s.sdDLSAP.ta.enb = TRUE;
      cfg.t.cfg.s.sdDLSAP.ta.val = value;
    }
    else
      cfg.t.cfg.s.sdDLSAP.ta.enb = FALSE;

    getEnvVarInt((char *)"MTP2_TMR_TS", &value);
    if(value != 0)
    {
      cfg.t.cfg.s.sdDLSAP.ts.enb = TRUE;
      cfg.t.cfg.s.sdDLSAP.ts.val = value;
    }
    else
      cfg.t.cfg.s.sdDLSAP.ts.enb = FALSE;
#endif /*(SS7_TTC || SS7_NTT) */

    getEnvVarInt((char *)"MTP2_SDTFLCSTARTTR", &value);
    cfg.t.cfg.s.sdDLSAP.sdtFlcStartTr = value;

    getEnvVarInt((char *)"MTP2_SDTFLCENDTR", &value);
    cfg.t.cfg.s.sdDLSAP.sdtFlcEndTr = value;


   /* fill pst structure */
   pst.selector = selsm2sd;
   pst.region = BP_AIN_SM_REGION;
   pst.pool = BP_AIN_SM_POOL;
   pst.prior = 0;
   pst.route = 0;
   pst.dstProcId = dstProcId;
   pst.dstEnt = ENTSD;
   pst.dstInst = 0;
   pst.srcProcId = SFndProcId();
   pst.srcEnt = ENTSM;
   pst.srcInst = 0;

   /* if SM to SD is tightly coupled, then call SD func directly else
    * pack primitive and post to SD
    */
   (Void) SmMiLsdCfgReq(&pst, &cfg);

   return (ROK);
} /* end of cfgSdDLSAP */

/*
*
*       Fun:   cfgSdDbg
*
*       Desc:  mtp level 2 - configure dbg mask
*
*       Ret:   ROK, RFAILED
*
*       Parameters:
*                   dbgMask: Debug mask
*                   action : Enable or Disable the given mask
*
*                   dstProcId: Destination TAPA proc id where SD resides.
*
*                   selsm2sd: Selector SM to SD. Values are:
*                               0 : loosely coupled SM to SD interface
*                               2 : tightly coupled SM to SD interface
*
*       Notes: Same selector value for SM to SD is used for SD to SM also
*              with regard to handling config confirm.
*/
#ifdef ANSI
PUBLIC S16 cfgSdDbg
(
int dbgMask,             /* Debug mask */
int action,              /* AENA or ADISIMM */
ProcId dstProcId,        /* destination procId where SD is located */
Selector selsm2sd,        /* selector SM to SD */
U32      transId
)
#else
PUBLIC S16 cfgSdDbg(nmbLnks, dstProcId, selsm2sd)
int dbgMask;             /* Debug mask */
int action;              /* AENA or ADISIMM */
ProcId dstProcId;        /* destination procId where SD is located */
Selector selsm2sd;       /* selector SM to SD */
U32 transId;       	/* Transaction id */
#endif
{
   SdMngmt cntrl;        /* management structure */
   Pst pst;              /* layer manager post structure */

   TRC2(cfgSdDbg);

 logger.logMsg (TRACE_FLAG, 0,
        "In cfgSdDbg(), values are <%d>, <%d>,<%d>,<%d>,<%d>", dbgMask,action,dstProcId,selsm2sd,transId);


   /* initialize cntrl struct */
   cmZero((U8 *) &cntrl, sizeof(SdMngmt));

   cntrl.hdr.msgType = TCNTRL;              /* configuration req */
   cntrl.hdr.entId.ent = ENTSD;           /* entity */
   cntrl.hdr.entId.inst = 0;              /* instance */
   cntrl.hdr.elmId.elmnt = STGEN;         /* general */
   cntrl.hdr.elmId.elmntInst1 = 0;
   cntrl.hdr.elmId.elmntInst2 = 0;
   cntrl.hdr.elmId.elmntInst3 = 0;
   cntrl.hdr.seqNmb = 0;
   cntrl.hdr.version = 0;
   cntrl.hdr.msgLen = 0;

#if (SD_LMINT3 || SMSD_LMINT3)
   cntrl.hdr.transId = transId;
   cntrl.hdr.response.route = 0;
   cntrl.hdr.response.prior = 0;

   /* use the same selector as SM to SD for SD to SM as well. For, if
    * SM to SD is tightly coupled then the confirm for config will be
    * handled by the SmMiLsdCntrlCfm func written within this file. In case
    * SM to SD is loosely coupled, the SmMiLsdCntrlCfm func should be invoked
    * by the stack manager activation task through smSdActvTsk func written
    * in this file.
    */
   cntrl.hdr.response.selector = selsm2sd;
   cntrl.hdr.response.mem.region = BP_AIN_SM_REGION;
   cntrl.hdr.response.mem.pool = BP_AIN_SM_POOL;
#endif /* SD_LMINT3 || SMSD_LMINT3 */

   /* use the selector within sm pst as loosely coupled. For, even in case
    * of tightly coupled SM to SD (and SD to SM), the SM functions to handle
    * trace and alarm (if they are invoked at all) are not written in this file
    */
   cntrl.t.cntrl.action = action; /* enable/disable */
   cntrl.t.cntrl.subAction = SADBG; /* enable/disable */
   cntrl.t.cntrl.sdDbg.dbgMask = dbgMask; /* dbg mask to enable/disable */

   /* fill Pst struct */
   pst.selector = selsm2sd;
   pst.region = BP_AIN_SM_REGION;
   pst.pool = BP_AIN_SM_POOL;
   pst.prior = 0;
   pst.route = 0;
   pst.dstProcId = dstProcId;
   pst.dstEnt = ENTSD;
   pst.dstInst = 0;
   pst.srcProcId = SFndProcId();
   pst.srcEnt = ENTSM;
   pst.srcInst = 0;

   /* if SM to SD is tightly coupled, then call SD func directly else
    * pack primitive and post to SD
    */
   (Void) SmMiLsdCntrlReq(&pst, &cntrl);

   return (ROK);
} /* end of cfgSdDbg */


