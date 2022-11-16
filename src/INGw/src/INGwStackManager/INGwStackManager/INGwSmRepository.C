//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

/************************************************************************
Name:     INAP Stack Manager Repository Implementation

Type:     C file

Desc:     Implementation of repository fuctions

File:     INGwSmRepository.C

Sid:      INGwSmRepository.C 0  -  03/27/03 

Prg:      gs,bd

 ************************************************************************/

#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
  #include "envopt.h"
#ifndef __CCPU_CPLUSPLUS
}
#endif

#include "INGwStackManager/INGwSmRepository.h"
#include "INGwStackManager/INGwSmAlmHdlr.h"
#include "INGwStackManager/INGwSmCommon.h"
#include "INGwTcapProvider/INGwTcapProvider.h"
#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
//#include "ccm/BpCCMAlarmMgr.h"
#include <INGwInfraManager/INGwIfrMgrAlarmMgr.h>
#endif

//#include "ccm/BpParamRepository.h"
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwStackManager/INGwSmBlkConfig.h>
#include <sys/time.h>
//#include <sys/resource.h>
#include <unistd.h>
#include <string.h>
using namespace std;

static const char *mpcCfgElement = "CONFIGURATION";
static const char *mpcCfgOidElement = "oid";
static const char *mpcCfgOidName = "name";
static const char *mpcCfgOidValue = "value";
static const char *mpcCfgIndexValue = "index";

static const char *mpcGenCfg = "gen";
static const char *mpcUspCfg = "usap";
static const char *mpcLspCfg = "lsap";
static const char *mpcNwkCfg = "nwcfg";
static const char *mpcRteCfg = "rtecfg";
static const char *mpcPsCfg  = "pscfg";
static const char *mpcPspCfg = "pspcfg";

static const char *mpcLnksetCfg  = "lnkset";
static const char *mpcRteCfgSelf = "route_self";
static const char *mpcRteCfgPeer = "route_dest";

static const char *mpcRsetMapCfgCritical = "RsetMapCfg_crit";
static const char *mpcRsetMapCfgDef = "RsetMapCfg_def";
static const char *mpcRsetMapCfgNonCritical = "RsetMapCfg_noncrit";
static const char *mpcEntSt = "HiEnt_TCAP";
static const char *mpcEntSp = "HiEnt_SCCP";
static const char *mpcEntHi = "HiEnt_TUCL";
static const char *mpcEntSb = "HiEnt_SCTP";
static const char *mpcEntIt = "HiEnt_M3UA";
static const char *mpcEntSg = "HiEnt_SG";
static const char *mpcEntSn = "HiEnt_MTP3";
static const char *mpcEntSd = "HiEnt_MTP2";
static const char *mpcRyLchanCfg = "ListenChannel";
static const char *mpcRySchanCfg = "ServerChannel";
static const char *mpcRyCchanCfg = "ClientChannel";

extern U16 selfProcId;
int tcapLoDlgId;
int tcapHiDlgId;

/******************************************************************************
 *
 *     Fun:   INGwSmRepository()
 *
 *     Desc:  Default C'tor
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
INGwSmRepository::INGwSmRepository()
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::INGwSmRepository");

    valScheme = AbstractDOMParser::Val_Auto;
    doNamespaces   = false;
    doSchema     = false;
    schemaFullChecking = false;
    mbAnyParsingErrors = false;
    recognizeNEL = false;
    memset(localeStr, 0, sizeof(localeStr));
    parser = 0;
    doc = 0;
    impl = 0;
    miStsTransId = BP_AIN_SM_STS_TRANSID;
    miDistTransId = BP_AIN_SM_DIST_TRANSID;
    miAuditTransId = BP_AIN_SM_AUDIT_TRANSID;
    miTransactionId = 0;
    mpRootNode = 0;
    miNumberOfMTP3Dlsaps = 0;

    // Initialize the XML4C system
    try
    {
      if (strlen(localeStr))
      {
        XMLPlatformUtils::Initialize(localeStr);
      }
      else
      {
        XMLPlatformUtils::Initialize();
      }

      if (recognizeNEL)
      {
        XMLPlatformUtils::recognizeNEL(recognizeNEL);
      }
    }
    catch (const XMLException& toCatch)
    {
      char *lpErrMsg = XMLString::transcode (toCatch.getMessage());
      logger.logMsg (ERROR_FLAG, 0,
            "Error during initialization! : %s", lpErrMsg);
      XMLString::release (&lpErrMsg);
    }

    initialize ();

    parseXmlFile (mstrSmXmlFile.c_str());

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::INGwSmRepository");
}


S16 INGwSmRepository::gttHexAddrToBcd(LngAddrs *inpBuf,ShrtAddrs *bcdBuf)
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
      return (RFAILED);
   for (i = inpBuf->length; i; i--)
   {
      d = 0;
      if (!cmIsANumber(&d, (c = *src++), (S16) BASE16))
         return (RFAILED);
      *dst = (U8) d;    /* The first digit */
      i--;
      if (!i)
         break;
      if (!cmIsANumber(&d, c = *src++, (S16) BASE16))
         return (RFAILED);
      *dst++ |= (U8) (d << 4);    /* The second digit */
   }
   bcdBuf->length = ( (inpBuf->length % 2) ?
      (U8)((U8)(inpBuf->length + 1)/2) : (U8)(inpBuf->length/2));
   return (ROK);
} /* gttHexAddrToBcd */

/******************************************************************************
 *
 *     Fun:   ~INGwSmRepository()
 *
 *     Desc:  Default Destrctor
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
INGwSmRepository::~INGwSmRepository()
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::~INGwSmRepository");

    //
    //  Delete the parser itself.  Must be done prior to calling Terminate, below.
    //

    if (parser)
      parser->release();
    parser = 0;

    // And call the termination method
    XMLPlatformUtils::Terminate();

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::~INGwSmRepository");
}

/******************************************************************************
 *
 *     Fun:   initialize()
 *
 *     Desc:  initialize the post structures etc
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::initialize ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::initialize");

    logger.logMsg (VERBOSE_FLAG, 0,
        "Sizeof StMngmt<%d>, SpMngmt<%d>, ItMngmt<%d>, SbMgmt<%d>, HiMnMgmt<%d>",
        sizeof (StMngmt), sizeof (SpMngmt), sizeof (ItMgmt), 
        sizeof (SbMgmt), sizeof (HiMngmt));

    pthread_rwlock_init (&mLinkLock, 0);

    /* initialize the post structures first */
    //cmMemset((U8 *)&liePst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lstPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lspPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&litPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lsbPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lhiPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lsnPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lsdPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lsgPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lshPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lmrPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&ldvPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&ldnPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lzvPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lznPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lztPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lzpPst, '\0', sizeof(Pst));
    cmMemset((U8 *)&lryPst, '\0', sizeof(Pst));

    /* Initialize the post structure for sending requests to INAP from SM */
    //liePst.selector  = BP_AIN_SM_COUPLING;
    //liePst.region    = BP_AIN_SM_REGION;
    //liePst.pool      = BP_AIN_SM_POOL;
    //liePst.prior     = BP_AIN_SM_PRIOR;
    //liePst.route     = BP_AIN_SM_ROUTE;
    //liePst.dstProcId = SFndProcId();
    //liePst.dstEnt    = ENTIE;
    //liePst.dstInst   = BP_AIN_SM_DEST_INST;
    //liePst.srcProcId = SFndProcId();
    //liePst.srcEnt    = ENTSM;
    //liePst.srcInst   = BP_AIN_SM_SRC_INST;

    /* Initialize the post structure for sending requests to TCAP from SM */
    lstPst.selector  = BP_AIN_SM_COUPLING;
    lstPst.region    = BP_AIN_SM_REGION;
    lstPst.pool      = BP_AIN_SM_POOL;
    lstPst.prior     = BP_AIN_SM_PRIOR;
    lstPst.route     = BP_AIN_SM_ROUTE;
    lstPst.dstProcId = SFndProcId();
    lstPst.dstEnt    = ENTST;
    lstPst.dstInst   = BP_AIN_SM_DEST_INST;
    lstPst.srcProcId = SFndProcId();
    lstPst.srcEnt    = ENTSM;
    lstPst.srcInst   = BP_AIN_SM_SRC_INST;

    /* Initialize the post structure for sending requests to SCCP from SM */
    lspPst.selector  = BP_AIN_SM_COUPLING;
    lspPst.region    = BP_AIN_SM_REGION;
    lspPst.pool      = BP_AIN_SM_POOL;
    lspPst.prior     = BP_AIN_SM_PRIOR;
    lspPst.route     = BP_AIN_SM_ROUTE;
    lspPst.dstProcId = SFndProcId();
    lspPst.dstEnt    = ENTSP;
    lspPst.dstInst   = BP_AIN_SM_DEST_INST;
    lspPst.srcProcId = SFndProcId();
    lspPst.srcEnt    = ENTSM;
    lspPst.srcInst   = BP_AIN_SM_SRC_INST;

    /* Initialize the post structure for sending requests to M3UA from SM */
    litPst.selector  = BP_AIN_SM_COUPLING;
    litPst.region    = BP_AIN_SM_REGION;
    litPst.pool      = BP_AIN_SM_POOL;
    litPst.prior     = BP_AIN_SM_PRIOR;
    litPst.route     = BP_AIN_SM_ROUTE;
    litPst.dstProcId = SFndProcId();
    litPst.dstEnt    = ENTIT;
    litPst.dstInst   = BP_AIN_SM_DEST_INST;
    litPst.srcProcId = SFndProcId();
    litPst.srcEnt    = ENTSM;
    litPst.srcInst   = BP_AIN_SM_SRC_INST;

    /* Initialize the post structure for sending requests to SCTP from SM */
    lsbPst.selector  = BP_AIN_SM_COUPLING;
    lsbPst.region    = BP_AIN_SM_REGION;
    lsbPst.pool      = BP_AIN_SM_POOL;
    lsbPst.prior     = BP_AIN_SM_PRIOR;
    lsbPst.route     = BP_AIN_SM_ROUTE;
    lsbPst.dstProcId = SFndProcId();
    lsbPst.dstEnt    = ENTSB;
    lsbPst.dstInst   = BP_AIN_SM_DEST_INST;
    lsbPst.srcProcId = SFndProcId();
    lsbPst.srcEnt    = ENTSM;
    lsbPst.srcInst   = BP_AIN_SM_SRC_INST;

    /* Initialize the post structure for sending requests to TUCL from SM */
    lhiPst.selector  = BP_AIN_SM_COUPLING;
    lhiPst.region    = BP_AIN_SM_REGION;
    lhiPst.pool      = BP_AIN_SM_POOL;
    lhiPst.prior     = BP_AIN_SM_PRIOR;
    lhiPst.route     = BP_AIN_SM_ROUTE;
    lhiPst.dstProcId = SFndProcId();
    lhiPst.dstEnt    = ENTHI;
    lhiPst.dstInst   = BP_AIN_SM_DEST_INST;
    lhiPst.srcProcId = SFndProcId();
    lhiPst.srcEnt    = ENTSM;
    lhiPst.srcInst   = BP_AIN_SM_SRC_INST;

    /* Initialize the post structure for sending requests to MTP3 from SM */
    lsnPst.selector  = BP_AIN_SM_COUPLING;
    lsnPst.region    = BP_AIN_SM_REGION;
    lsnPst.pool      = BP_AIN_SM_POOL;
    lsnPst.prior     = BP_AIN_SM_PRIOR;
    lsnPst.route     = BP_AIN_SM_ROUTE;
    lsnPst.dstProcId = SFndProcId();
    lsnPst.dstEnt    = ENTSN;
    lsnPst.dstInst   = BP_AIN_SM_DEST_INST;
    lsnPst.srcProcId = SFndProcId();
    lsnPst.srcEnt    = ENTSM;
    lsnPst.srcInst   = BP_AIN_SM_SRC_INST;

    /* Initialize the post structure for sending requests to MTP3 from SM */
    lryPst.selector  = BP_AIN_SM_COUPLING;
    lryPst.region    = BP_AIN_SM_REGION;
    lryPst.pool      = BP_AIN_SM_POOL;
    lryPst.prior     = BP_AIN_SM_PRIOR;
    lryPst.route     = BP_AIN_SM_ROUTE;
    lryPst.dstProcId = SFndProcId();
    lryPst.dstEnt    = ENTRY;
    lryPst.dstInst   = BP_AIN_SM_DEST_INST;
    lryPst.srcProcId = SFndProcId();
    lryPst.srcEnt    = ENTSM;
    lryPst.srcInst   = BP_AIN_SM_SRC_INST;
    /* Initialize the post structure for sending requests to MTP2 from SM */
    lsdPst.selector  = BP_AIN_SM_COUPLING;
    lsdPst.region    = BP_AIN_SM_REGION;
    lsdPst.pool      = BP_AIN_SM_POOL;
    lsdPst.prior     = BP_AIN_SM_PRIOR;
    lsdPst.route     = BP_AIN_SM_ROUTE;
    lsdPst.dstProcId = SFndProcId();
    lsdPst.dstEnt    = ENTSD;
    lsdPst.dstInst   = BP_AIN_SM_DEST_INST;
    lsdPst.srcProcId = SFndProcId();
    lsdPst.srcEnt    = ENTSM;
    lsdPst.srcInst   = BP_AIN_SM_SRC_INST;

	/* Initialize the post structure for sending requests to LDF-M3UA from SM */
    ldvPst.selector  = BP_AIN_SM_COUPLING;
    ldvPst.region    = BP_AIN_SM_REGION;
    ldvPst.pool      = BP_AIN_SM_POOL;
    ldvPst.prior     = BP_AIN_SM_PRIOR;
    ldvPst.route     = BP_AIN_SM_ROUTE;
    ldvPst.dstProcId = SFndProcId();
    ldvPst.dstEnt    = ENTDV;
    ldvPst.dstInst   = BP_AIN_SM_DEST_INST;
    ldvPst.srcProcId = SFndProcId();
    ldvPst.srcEnt    = ENTSM;
    ldvPst.srcInst   = BP_AIN_SM_SRC_INST;

	/* Initialize the post structure for sending requests to LDF-MTP3 from SM */
    ldnPst.selector  = BP_AIN_SM_COUPLING;
    ldnPst.region    = BP_AIN_SM_REGION;
    ldnPst.pool      = BP_AIN_SM_POOL;
    ldnPst.prior     = BP_AIN_SM_PRIOR;
    ldnPst.route     = BP_AIN_SM_ROUTE;
    ldnPst.dstProcId = SFndProcId();
    ldnPst.dstEnt    = ENTDN;
    ldnPst.dstInst   = BP_AIN_SM_DEST_INST;
    ldnPst.srcProcId = SFndProcId();
    ldnPst.srcEnt    = ENTSM;
    ldnPst.srcInst   = BP_AIN_SM_SRC_INST;

	/* Initialize the post structure for sending requests to PSF-SCCP from SM */
    lzpPst.selector  = BP_AIN_SM_COUPLING;
    lzpPst.region    = BP_AIN_SM_REGION;
    lzpPst.pool      = BP_AIN_SM_POOL;
    lzpPst.prior     = BP_AIN_SM_PRIOR;
    lzpPst.route     = BP_AIN_SM_ROUTE;
    lzpPst.dstProcId = SFndProcId();
    lzpPst.dstEnt    = ENTSP;
    lzpPst.dstInst   = BP_AIN_SM_DEST_INST;
    lzpPst.srcProcId = SFndProcId();
    lzpPst.srcEnt    = ENTSM;
    lzpPst.srcInst   = BP_AIN_SM_SRC_INST;
	
/* Initialize the post structure for sending requests to PSF-TCAP from SM */
    lztPst.selector  = BP_AIN_SM_COUPLING;
    lztPst.region    = BP_AIN_SM_REGION;
    lztPst.pool      = BP_AIN_SM_POOL;
    lztPst.prior     = BP_AIN_SM_PRIOR;
    lztPst.route     = BP_AIN_SM_ROUTE;
    lztPst.dstProcId = SFndProcId();
    lztPst.dstEnt    = ENTST;
    lztPst.dstInst   = BP_AIN_SM_DEST_INST;
    lztPst.srcProcId = SFndProcId();
    lztPst.srcEnt    = ENTSM;
    lztPst.srcInst   = BP_AIN_SM_SRC_INST;

/* Initialize the post structure for sending requests to PSF-M3UA from SM */
    lzvPst.selector  = BP_AIN_SM_COUPLING;
    lzvPst.region    = BP_AIN_SM_REGION;
    lzvPst.pool      = BP_AIN_SM_POOL;
    lzvPst.prior     = BP_AIN_SM_PRIOR;
    lzvPst.route     = BP_AIN_SM_ROUTE;
    lzvPst.dstProcId = SFndProcId();
    lzvPst.dstEnt    = ENTIT;
    lzvPst.dstInst   = BP_AIN_SM_DEST_INST;
    lzvPst.srcProcId = SFndProcId();
    lzvPst.srcEnt    = ENTSM;
    lzvPst.srcInst   = BP_AIN_SM_SRC_INST;

/* Initialize the post structure for sending requests to PSF-MTP3 from SM */
    lznPst.selector  = BP_AIN_SM_COUPLING;
    lznPst.region    = BP_AIN_SM_REGION;
    lznPst.pool      = BP_AIN_SM_POOL;
    lznPst.prior     = BP_AIN_SM_PRIOR;
    lznPst.route     = BP_AIN_SM_ROUTE;
    lznPst.dstProcId = SFndProcId();
    lznPst.dstEnt    = ENTSN;
    lznPst.dstInst   = BP_AIN_SM_DEST_INST;
    lznPst.srcProcId = SFndProcId();
    lznPst.srcEnt    = ENTSM;
    lznPst.srcInst   = BP_AIN_SM_SRC_INST;

/* Initialize the post structure for sending requests to SH from SM */
    lshPst.selector  = BP_AIN_SM_COUPLING;
    lshPst.region    = BP_AIN_SM_REGION;
    lshPst.pool      = BP_AIN_SM_POOL;
    lshPst.prior     = BP_AIN_SM_PRIOR;
    lshPst.route     = BP_AIN_SM_ROUTE;
    lshPst.dstProcId = SFndProcId();
    lshPst.dstEnt    = ENTSH;
    lshPst.dstInst   = BP_AIN_SM_DEST_INST;
    lshPst.srcProcId = SFndProcId();
    lshPst.srcEnt    = ENTSM;
    lshPst.srcInst   = BP_AIN_SM_SRC_INST;

/* Initialize the post structure for sending requests to SG from SM */
    lsgPst.selector  = BP_AIN_SM_COUPLING;
    lsgPst.region    = BP_AIN_SM_REGION;
    lsgPst.pool      = BP_AIN_SM_POOL;
    lsgPst.prior     = BP_AIN_SM_PRIOR;
    lsgPst.route     = BP_AIN_SM_ROUTE;
    lsgPst.dstProcId = SFndProcId();
    lsgPst.dstEnt    = ENTSG;
    lsgPst.dstInst   = BP_AIN_SM_DEST_INST;
    lsgPst.srcProcId = SFndProcId();
    lsgPst.srcEnt    = ENTSM;
    lsgPst.srcInst   = BP_AIN_SM_SRC_INST;

/* Initialize the post structure for sending requests to MR from SM */
    lmrPst.selector  = BP_AIN_SM_COUPLING;
    lmrPst.region    = BP_AIN_SM_REGION;
    lmrPst.pool      = BP_AIN_SM_POOL;
    lmrPst.prior     = BP_AIN_SM_PRIOR;
    lmrPst.route     = BP_AIN_SM_ROUTE;
    lmrPst.dstProcId = SFndProcId();
    lmrPst.dstEnt    = ENTMR;
    lmrPst.dstInst   = BP_AIN_SM_DEST_INST;
    lmrPst.srcProcId = SFndProcId();
    lmrPst.srcEnt    = ENTSM;
    lmrPst.srcInst   = BP_AIN_SM_SRC_INST;
   
 /*
   * read the required variabled from the ParamRepository
   */

    string lstrValue;

#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
    INGwIfrPrParamRepository &lrBpRep = INGwIfrPrParamRepository::getInstance();
#endif

    const char *lpcEnv = getenv ("SM_CCM_STS_LEVEL");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      //get the initial statistics level
      if (lrBpRep.getValue (SM_CCM_STS_LEVEL, lstrValue) == 0)
      {
        miStsLevel = atoi (lstrValue.c_str());
      }
      else
#endif
      {
        miStsLevel = 1;
      }
    }
    else
    {
      miStsLevel = atoi (lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_CCM_STS_LEVEL = <%d>", miStsLevel);

    lpcEnv = getenv ("SM_CCM_STS_TIMER");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      //get the initial statistics collection timer
      if (lrBpRep.getValue (SM_CCM_STS_TIMER, lstrValue) == 0)
      {
        miSmStsTmr = atoi (lstrValue.c_str());
      }
      else
#endif
      {
        miSmStsTmr = 2; // This is ticks, timer is define
												// by SM_TIMER_RES timer
      }
    }
    else
    {
      miSmStsTmr = atoi (lpcEnv);
    }

    logger.logMsg (TRACE_FLAG, 0,
        "SM_CCM_STS_TIMER = <%d>", miSmStsTmr);

    lpcEnv = getenv ("SM_CCM_DUMP_MEMORY");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      //get the boolean for dumping memory
      if (lrBpRep.getValue (SM_CCM_DUMP_MEMORY, lstrValue) == 0)
      {
        mbDumpMemory = atoi (lstrValue.c_str());
      }
      else
#endif
      {
        mbDumpMemory = false;
      }
    }
    else
    {
      mbDumpMemory = atoi (lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_CCM_DUMP_MEMORY = <%d>", mbDumpMemory);


    lpcEnv = getenv ("SM_SELF_PC");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      //get the self point code
      if (lrBpRep.getValue (SM_SELF_PC, lstrValue) == 0)
      {
        self.miSelfPC = atoi (lstrValue.c_str());
      }
      else
#endif
      {
        self.miSelfPC = 2;
      }
    }
    else
    {
      self.miSelfPC = atoi (lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_SELF_PC = <%d>", self.miSelfPC);


    lpcEnv = getenv ("SM_SELF_NWTYPE");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      //get the self Network Type
      if (lrBpRep.getValue (SM_SELF_NWTYPE, lstrValue) == 0)
      {
        self.miSelfNetworkType = atoi (lstrValue.c_str());
      }
      else
#endif
      {
        self.miSelfNetworkType = 1;
      }
    }
    else
    {
      self.miSelfNetworkType = atoi (lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_SELF_NWTYPE = <%d>", self.miSelfNetworkType);


    lpcEnv = getenv ("SM_SELF_ADDRESS");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      //get the ip addresses and port number of CCM
      if (lrBpRep.getValue (SM_SELF_ADDRESS, lstrValue) != 0)
#endif
      {
        lstrValue = "127.0.0.1|2905";
      }
    }
    else
    {
      lstrValue = lpcEnv;
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_SELF_ADDRESS = <%s>", lstrValue.c_str());


    if (lstrValue.empty() == false)
    {
      /*
       * The string obtained is in format IP1-IP2|PORT
       */
      char *tok, *ch;
      bool loop = true;

      tok = strtok_r ((char*)lstrValue.c_str(), "|", &ch);
      if (tok == 0)
      {
        logger.logMsg (ERROR_FLAG, 0,
              "The self ip and port could not be obtained");
      }
      else 
      {
        char *ch1, *tok1;

        tok1 = strtok_r (0, "|", &ch);
        if (tok1 == 0)
        {
            logger.logMsg (ERROR_FLAG, 0,
                "The self port could not be obtained");
        }
        else 
            self.selfPort = atoi (tok1);

        logger.logMsg (ERROR_FLAG, 0,
              "SELF PORT obtained = <%d>", self.selfPort);

        tok1 = strtok_r (tok, "-", &ch1);
        int count = 0;
        while (loop)
        {
            count++;
            if (tok1 == 0)
            {
              if (count == 1)
              {
                logger.logMsg (ERROR_FLAG, 0,
                      "The ip address and port couldn't be tokenized");
              }

              loop = false;
              break;
            }
            else
            {
              INGwSmAddress *lpAdd = new INGwSmAddress;

              //set the peer id to 0 for self
              lpAdd->peerId = 0;

              //set the self ipaddress
              char *ip, *ch3;
              int num = 0;

              ip = strtok_r (tok1, ".", &ch3);
              if (ip == 0)
                num = 0;
              else
                num = atoi (ip);
              lpAdd->address = num << 24;

              ip = strtok_r (0, ".", &ch3);
              if (ip == 0)
                num = 0;
              else
                num = atoi (ip);
              lpAdd->address += num << 16;

              ip = strtok_r (0, ".", &ch3);
              if (ip == 0)
                num = 0;
              else
                num = atoi (ip);
              lpAdd->address += num << 8;

              ip = strtok_r (0, ".", &ch3);
              if (ip == 0)
                num = 0;
              else 
                num = atoi (ip);
              lpAdd->address += num;

              lpAdd->port = self.selfPort;


              logger.logMsg (ERROR_FLAG, 0,
                    "SELF ADDRESS : ip = <%u.%u.%u.%u>",
                    INGwSmGetHiByte(GetHiWord(lpAdd->address)),
                    INGwSmGetLoByte(GetHiWord(lpAdd->address)),
                    INGwSmGetHiByte(GetLoWord(lpAdd->address)),
                    INGwSmGetLoByte(GetLoWord(lpAdd->address)));


              //insert the address node into the vector
              self.meSelfAddress.push_back (lpAdd);
            }

            tok1 = strtok_r (0, "-", &ch1);
        }
      }
    }

    lpcEnv = getenv ("SM_SELF_SSN");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      //get the self SSN
      if (lrBpRep.getValue (SM_SELF_SSN, lstrValue) == 0)
      {
        self.meSelfSsn = atoi (lstrValue.c_str());
      }
      else
#endif
      {
        self.meSelfSsn = 0x10;
      }
    }
    else
    {
      self.meSelfSsn = atoi (lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_SELF_SSN = <%d>", self.meSelfSsn);


    lpcEnv = getenv ("SM_PEER_ADDRESS");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      //get the Peer ip address
      if (lrBpRep.getValue (SM_PEER_ADDRESS, lstrValue) != 0)
#endif
      {
        lstrValue = "1|192.168.3.51|2905";
      }
    }
    else
    {
      lstrValue = lpcEnv;
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_PEER_ADDRESS = <%s>", lstrValue.c_str() );

    if (lstrValue.empty() == false)
    {
      /*
       * The string obtained is in format PSP1|IP1-IP2|PORT1,PSP2|IP3-IP4|PORT2
       */

      char *tok, *ch;
      bool loop = true;
      int line = 1, peerId = 1;
      while (loop)
      {
        if (line == 1)
            tok = strtok_r ((char*)lstrValue.c_str(), ",", &ch);
        else
            tok = strtok_r (0, ",", &ch);

        if (tok == 0)
        {
            if (line == 1)
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "peer ipaddress and port could not be obtained");
            }
            loop = false;
            break;
        }

        char *ch1, *tok1;
        int line1 = 1;
        bool loop1 = true;
        AddressVector *lpVec = new AddressVector;

        tok1 = strtok_r (tok, "|", &ch1);

        if (tok1 == 0)
        {
            logger.logMsg (ERROR_FLAG, 0,
                "The peer id couldn't be tokenized");
        }
        else
        {
            peerId = atoi (tok1);
            logger.logMsg (ERROR_FLAG, 0,
                "PEER ID Obtained = <%d>", peerId);

            tok1 = strtok_r (0, "|", &ch1);
            if (tok1 == 0)
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "The ip address list can't be tokenized");
            }
            else
            {
              int peerPort = 2905;

              char *tok3 = strtok_r (0, "|", &ch1);
              if (tok3 == 0)
              {
                logger.logMsg (ERROR_FLAG, 0,
                      "The port number can't be tokenized");
              }
              else
              {
                peerPort = atoi (tok3);
                logger.logMsg (ERROR_FLAG, 0,
                      "PEER PORT obtained = <%d>", peerPort);
              }

              char *ch2, *tok2;
              tok2 = strtok_r (tok1, "-", &ch2);
              int count = 0;
              while (loop1)
              {
                count ++;
                if (tok2 == 0)
                {
                    if (count == 1)
                    {
                      logger.logMsg (ERROR_FLAG, 0,
                            "The peer ip address couldn't be tokenized");
                    }
                    loop1 = false;
                    break;
                }
                else
                {
                    INGwSmAddress *lpAdd = new INGwSmAddress;

                    // set the peer id
                    lpAdd->peerId = peerId;


                    //set the peer ipaddress
                    char *ip, *ch3;
                    int num = 0;

                    ip = strtok_r (tok2, ".", &ch3);
                    if (ip == 0)
                      num = 0;
                    else
                      num = atoi (ip);
                    lpAdd->address = num << 24;

                    ip = strtok_r (0, ".", &ch3);
                    if (ip == 0)
                      num = 0;
                    else
                      num = atoi (ip);
                    lpAdd->address += num << 16;

                    ip = strtok_r (0, ".", &ch3);
                    if (ip == 0)
                      num = 0;
                    else
                      num = atoi (ip);
                    lpAdd->address += num << 8;

                    ip = strtok_r (0, ".", &ch3);
                    if (ip == 0)
                      num = 0;
                    else
                      num = atoi (ip);
                    lpAdd->address += num;

                    lpAdd->port = peerPort;

                    lpVec->push_back (lpAdd);

                    logger.logMsg (ERROR_FLAG, 0,
                        "PEER ADDRESS : id = <%d>, ip = <%u.%u.%u.%u>",
                        peerId, INGwSmGetHiByte(GetHiWord(lpAdd->address)),
                        INGwSmGetLoByte(GetHiWord(lpAdd->address)),
                        INGwSmGetHiByte(GetLoWord(lpAdd->address)),
                        INGwSmGetLoByte(GetLoWord(lpAdd->address)));
                }

                tok2 = strtok_r (0, "-", &ch2);
              }
              mePeerAddress [peerId] = lpVec;
              mePSPIdList.push_back (peerId);
            }

            if (addPspState (peerId, BP_AIN_SM_PSP_ST_DOWN) != BP_AIN_SM_OK)
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unable to add PSP Id<%d> in the PSP State Map", peerId);
            }
            line++;
        }
      }
    }


    lpcEnv = getenv ("SM_SELF_ASPID");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      //get the self ASP Id
      if (lrBpRep.getValue (SM_SELF_ASPID, lstrValue) == 0)
      {
        self.miSelfAspId = atoi (lstrValue.c_str());
      }
      else
#endif
      {
        self.miSelfAspId = 1;
      }
    }
    else
    {
      self.miSelfAspId = atoi (lpcEnv);
    }


    logger.logMsg (ERROR_FLAG, 0,
        "SM_SELF_ASPID = <%d>", self.miSelfAspId);

    lpcEnv = getenv ("SM_PDU_LOG");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      //get the file name for storing PDU Traces
      if (lrBpRep.getValue (SM_PDU_LOG, lstrValue) == 0)
      {
        mstrSmLogFile = lstrValue.c_str();
      }
      else 
#endif
      {
        mstrSmLogFile = "./traces.log";
      }
    }
    else
    {
      mstrSmLogFile = lpcEnv;
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_PDU_LOG = <%s>", mstrSmLogFile.c_str() );


    long selfId = INGwIfrPrParamRepository::getInstance().getSelfId();
	  long peerId = INGwIfrPrParamRepository::getInstance().getPeerId();
    bool ftMode = (peerId != 0)?true:false;
    std::string xmlCfgFile;
    xmlCfgFile = "./INGwSm_CCM1.xml";
    if(ftMode) {
	  	if(selfId > peerId)
        xmlCfgFile = "./INGwSm_CCM2.xml";
	  }

    lpcEnv = getenv ("SM_XML_FILE");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      //get the XML File for Stack Manager
      if (lrBpRep.getValue (SM_XML_FILE, lstrValue) == 0)
      {
        mstrSmXmlFile = lstrValue;
      }
      else 
#endif
      {
        mstrSmXmlFile = "../conf/" + xmlCfgFile;
      }
    }
    else
    {
      mstrSmXmlFile = lpcEnv + xmlCfgFile;
    }

    logger.logMsg (ALWAYS_FLAG, 0,
        "SM_XML_FILE = <%s>", mstrSmXmlFile.c_str() );

    FILE *fp = fopen (mstrSmXmlFile.c_str(), "r");
    if (fp == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to open <%s>", mstrSmXmlFile.c_str());
    }
    else
    {
      fclose (fp);
    }

    lpcEnv = getenv ("SM_TIMER_RES");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      if (lrBpRep.getValue (SM_TIMER_RES, lstrValue) == 0)
      {
        miTimerRes = atoi (lstrValue.c_str());
      }
      else
#endif
      {
        miTimerRes = 30;
      }
    }
    else
    { 
      miTimerRes = atoi (lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_TIMER_RES = <%d>", miTimerRes);

    lpcEnv = getenv ("SM_MONITOR_INTERVAL");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      if (lrBpRep.getValue (SM_MONITOR_INTERVAL, lstrValue) == 0)
      {
        miDistTimer = atoi (lstrValue.c_str());
      }
      else
#endif
      {
        miDistTimer = 2; //This is ticks, time is defined
												 // by SM_TIMER_RES
      }
    }
    else
    {
      miDistTimer = atoi (lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_MONITOR_INTERVAL = <%d>", miDistTimer);

    int liDebugLevel = 0;

    lpcEnv = getenv ("SM_CCM_DBG_LEVEL");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      if (lrBpRep.getValue (SM_CCM_DBG_LEVEL, lstrValue) == 0)
      {
        liDebugLevel = atoi(lstrValue.c_str());
      }
      else
#endif
      {
        liDebugLevel = 0;
      }
    }
    else
    { 
      liDebugLevel = atoi(lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_CCM_DBG_LEVEL = <%d>", liDebugLevel);

    setDebugLevel (BP_AIN_SM_ALL_LAYER, liDebugLevel);

    int liTrcLevel = 0;

    lpcEnv = getenv ("SM_CCM_TRC_LEVEL");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      if (lrBpRep.getValue (SM_CCM_TRC_LEVEL, lstrValue) == 0)
      {
        liTrcLevel = atoi(lstrValue.c_str());
      }
      else
#endif
      {
        liTrcLevel = 0;
      }
    }
    else
    { 
      liTrcLevel = atoi(lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_CCM_TRC_LEVEL = <%d>", liTrcLevel);

    setTrcLevel (BP_AIN_SM_ALL_LAYER, liTrcLevel);

    int liAlmLevel = 0;

    lpcEnv = getenv ("SM_CCM_ALM_LEVEL");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      if (lrBpRep.getValue (SM_CCM_ALM_LEVEL, lstrValue) == 0)
      {
        liAlmLevel = atoi(lstrValue.c_str());
      }
      else
#endif
      {
        liAlmLevel = 1;
      }
    }
    else
    { 
      liAlmLevel = atoi(lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_CCM_ALM_LEVEL = <%d>", liAlmLevel);

    setAlarmLevel (BP_AIN_SM_ALL_LAYER, liAlmLevel);


    // ------- stack mode ---------------------
    int l_transportType = 0;

    lpcEnv = getenv ("SM_TRANSPORT_TYPE");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      if (lrBpRep.getValue (ainTRANSPORT_TYPE, lstrValue) == 0)
      {
        l_transportType = atoi(lstrValue.c_str());
      }
      else
#endif
      {
        l_transportType = TRANSPORT_TYPE_SIGTRAN;
      }
    }
    else
    {
      l_transportType = atoi(lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_TRANSPORT_TYPE = <%d>", l_transportType);

    setTransportType (l_transportType);


    // ----- override xml -----------------------
    lpcEnv = getenv ("SM_OVERRIDE_XML");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      if (lrBpRep.getValue ("SM_OVERRIDE_XML", lstrValue) == 0)
      {
        mbOverrideXml = (bool) atoi(lstrValue.c_str());
      }
      else
#endif
      {
        mbOverrideXml = 1;
      }
    }
    else
    {
      mbOverrideXml = (bool) atoi(lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "SM_OVERRIDE_XML = <%d>", mbOverrideXml);

    lpcEnv = getenv ("AIN_MEM_PROB_EXIT");
    if (lpcEnv == 0)
    {
#if (defined (_BP_PARAM_REP_) && !defined (STUBBED))
      if (lrBpRep.getValue ("AIN_MEM_PROB_EXIT", lstrValue) == 0)
      {
        mbAinMemProbExit = (bool) atoi(lstrValue.c_str());
      }
      else
#endif
      {
        mbAinMemProbExit = false;
      }
    }
    else 
    {
      mbAinMemProbExit = (bool) atoi(lpcEnv);
    }

    logger.logMsg (ERROR_FLAG, 0,
        "AIN_MEM_PROB_EXIT = <%d>", mbAinMemProbExit);


    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::initialize");

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getlimits()
 * 
 *     Desc:  This function prints the resource limits
 * 
 *     Notes: None
 * 
 *     File:  INGwSmRepository.C
 * 
 *******************************************************************************/
    int
INGwSmRepository::getlimits ()
{ 
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getlimits");

    struct rlimit lsLimit;

    getrlimit(RLIMIT_CPU, &lsLimit);
    logger.logMsg (ERROR_FLAG, 0,
        "CPU LIMIT : Max <%d> Curr <%d>", lsLimit.rlim_max, lsLimit.rlim_cur);

    getrlimit(RLIMIT_DATA, &lsLimit);
    logger.logMsg (ERROR_FLAG, 0,
        "DATA LIMIT : Max <%d> Curr <%d>", lsLimit.rlim_max, lsLimit.rlim_cur);

    getrlimit(RLIMIT_FSIZE, &lsLimit);
    logger.logMsg (ERROR_FLAG, 0,
        "FSIZE LIMIT : Max <%d> Curr <%d>", lsLimit.rlim_max, lsLimit.rlim_cur);

    getrlimit(RLIMIT_NOFILE, &lsLimit);
    logger.logMsg (ERROR_FLAG, 0,
        "NOFILE LIMIT : Max <%d> Curr <%d>", lsLimit.rlim_max, lsLimit.rlim_cur);

    getrlimit(RLIMIT_STACK, &lsLimit);
    logger.logMsg (ERROR_FLAG, 0,
        "STACK LIMIT : Max <%d> Curr <%d>", lsLimit.rlim_max, lsLimit.rlim_cur);

#ifdef LINUX
    getrlimit(RLIMIT_LOCKS, &lsLimit);
    logger.logMsg (ERROR_FLAG, 0,
        "LOCKS LIMIT : Max <%d> Curr <%d>", lsLimit.rlim_max, lsLimit.rlim_cur);

    getrlimit(RLIMIT_MEMLOCK, &lsLimit);
    logger.logMsg (ERROR_FLAG, 0,
        "MEMLOCK LIMIT : Max <%d> Curr <%d>", lsLimit.rlim_max, lsLimit.rlim_cur);

    getrlimit(RLIMIT_NPROC, &lsLimit);
    logger.logMsg (ERROR_FLAG, 0,
        "NPROC LIMIT : Max <%d> Curr <%d>", lsLimit.rlim_max, lsLimit.rlim_cur);

    getrlimit(RLIMIT_RSS, &lsLimit);
    logger.logMsg (ERROR_FLAG, 0,
        "RSS LIMIT : Max <%d> Curr <%d>", lsLimit.rlim_max, lsLimit.rlim_cur);

#else 

    getrlimit(RLIMIT_VMEM, &lsLimit);
    logger.logMsg (ERROR_FLAG, 0,
        "VMEM LIMIT : Max <%d> Curr <%d>", lsLimit.rlim_max, lsLimit.rlim_cur);

    getrlimit(RLIMIT_AS, &lsLimit);
    logger.logMsg (ERROR_FLAG, 0,
        "AS LIMIT : Max <%d> Curr <%d>", lsLimit.rlim_max, lsLimit.rlim_cur);

    getrlimit(RLIMIT_CORE, &lsLimit);
    logger.logMsg (ERROR_FLAG, 0,
        "CORE LIMIT : Max <%d> Curr <%d>", lsLimit.rlim_max, lsLimit.rlim_cur);

#endif



    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getlimits");

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   parseXmlFile()
 * 
 *     Desc:  This function reads the XML file containing the initial
 *        configuration etc.
 *
 *     Notes: None
 * 
 *     File:  INGwSmRepository.C
 * 
 *******************************************************************************/
    int 
INGwSmRepository::parseXmlFile (const char *apcXmlFileName)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::parseXmlFile");

    if (parser)
      parser->release();

    // Instantiate the DOM parser.
    static const XMLCh gLS[] = { chLatin_L, chLatin_S, chNull };
    impl = DOMImplementationRegistry::getDOMImplementation(gLS);
    parser = ((DOMImplementationLS*)impl)->createDOMBuilder(
        DOMImplementationLS::MODE_SYNCHRONOUS, 0);

    parser->setFeature(XMLUni::fgDOMNamespaces, doNamespaces);
    parser->setFeature(XMLUni::fgXercesSchema, doSchema);
    parser->setFeature(XMLUni::fgXercesSchemaFullChecking, schemaFullChecking);

    if (valScheme == AbstractDOMParser::Val_Auto)
    {
      parser->setFeature(XMLUni::fgDOMValidateIfSchema, true);
    }
    else if (valScheme == AbstractDOMParser::Val_Never)
    {
      parser->setFeature(XMLUni::fgDOMValidation, false);
    }
    else if (valScheme == AbstractDOMParser::Val_Always)
    {
      parser->setFeature(XMLUni::fgDOMValidation, true);
    }

    // enable datatype normalization - default is off
    parser->setFeature(XMLUni::fgDOMDatatypeNormalization, true);

    // And create our error handler and install it
    DOMCountErrorHandler errorHandler;
    parser->setErrorHandler(&errorHandler);

    //
    //  Get the starting time and kick off the parse of the indicated
    //  file. Catch any exceptions that might propogate out of it.
    //
    //reset error count first
    errorHandler.resetErrors();

    mbAnyParsingErrors = false;
    try
    {
      // reset document pool
      parser->resetDocumentPool();

      doc = parser->parseURI(apcXmlFileName);

      if (doc)
      {
        mpRootNode = (DOMNode*) doc->getDocumentElement();

        if (mpRootNode->getNodeType() == DOMNode::ELEMENT_NODE)
        {
            DOMElement *lpElement = (DOMElement *) mpRootNode;

            XMLCh *lpTag = XMLString::transcode ("AINStackManager");
            DOMNodeList *lpNodeList = lpElement->getElementsByTagName (lpTag);
            XMLString::release (&lpTag);

            if (lpNodeList->getLength())
            {
              mpRootNode = lpNodeList->item(0);
            }
        }
      }
    }

    catch (const XMLException& toCatch)
    {
      char *lpErrMsg = XMLString::transcode (toCatch.getMessage());
      logger.logMsg (ERROR_FLAG, 0, 
            "Error during parsing: <%s>, Exception message is: <%s>",
            apcXmlFileName, lpErrMsg);
      XMLString::release (&lpErrMsg);
      mbAnyParsingErrors = true;
    }
    catch (const DOMException& toCatch)
    {
      const unsigned int maxChars = 2047;
      XMLCh errText[maxChars + 1];

      logger.logMsg (ERROR_FLAG, 0,
            "Error during parsing: <%s>, DOMException code is: <%d>",
            apcXmlFileName, toCatch.code);

      if (DOMImplementation::loadDOMExceptionMsg(toCatch.code, errText, maxChars))
      {
        char *lpErrMsg = XMLString::transcode (errText);
        logger.logMsg (ERROR_FLAG, 0, 
              "Message is: %s", lpErrMsg);
        XMLString::release (&lpErrMsg);
      }

      mbAnyParsingErrors = true;
    }
    catch (...)
    {
      logger.logMsg (ERROR_FLAG, 0, 
            "Unexpected exception during parsing: %s", apcXmlFileName);
      mbAnyParsingErrors = true;
    }

    //  
    //  Extract the DOM tree
    //
    if (errorHandler.getSawErrors())
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Errors occurred, no output available\n");
      mbAnyParsingErrors = true;
    }

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::parseXmlFile");

    if (mbAnyParsingErrors)
      return BP_AIN_SM_FAIL;
    else
      return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getStsOid()
 *
 *     Desc:  This function reads get the OID for the statistics from the
 *            DOME Tree
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int 
INGwSmRepository::getStsOid (DOMElement *apNode, char *apcId, 
      char*apcLayer, char *apcType, 
      char *apcElement, char *apcLevel)
{
    //logger.logMsg (TRACE_FLAG, 0,
    //    "Entering INGwSmRepository::getStsOid");

    if (apNode)
    {
      if (getAttrVal (apNode, (char*)"id", apcId) == 0 ||
            getAttrVal (apNode, (char*)"layer", apcLayer) == 0 ||
            getAttrVal (apNode, (char*)"type", apcType) == 0 ||
            getAttrVal (apNode, (char*)"element", apcElement) == 0 ||
            getAttrVal (apNode, (char*)"level", apcLevel) == 0)
      {
        return BP_AIN_SM_FAIL;
      }

      //logger.logMsg (TRACE_FLAG, 0,
      //      "Leaving INGwSmRepository::getStsOid");

      return BP_AIN_SM_OK;
    }
    return BP_AIN_SM_FAIL;
}

/******************************************************************************
 *
 *     Fun:   getLayerStrToVal()
 *
 *     Desc:  This function gets the layerId from the string name
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getLayerStrToVal (char *apcLayerName)
{
    //logger.logMsg (TRACE_FLAG, 0,
    //    "Entering INGwSmRepository::getLayerStrToVal");

    int liLayerId = BP_AIN_SM_INV_LAYER;

    if (strcmp (apcLayerName, "inap") == 0)
      liLayerId = BP_AIN_SM_AIN_LAYER;
    else if (strcmp (apcLayerName, "tcap") == 0)
      liLayerId = BP_AIN_SM_TCA_LAYER;
    else if (strcmp (apcLayerName, "sccp") == 0)
      liLayerId = BP_AIN_SM_SCC_LAYER;
    else if (strcmp (apcLayerName, "m3ua") == 0)
      liLayerId = BP_AIN_SM_M3U_LAYER;
    else if (strcmp (apcLayerName, "sctp") == 0)
      liLayerId = BP_AIN_SM_SCT_LAYER;
    else if (strcmp (apcLayerName, "tucl") == 0)
      liLayerId = BP_AIN_SM_TUC_LAYER;
    else if (strcmp (apcLayerName, "mtp3") == 0)
      liLayerId = BP_AIN_SM_MTP3_LAYER;
    else if (strcmp (apcLayerName, "mtp2") == 0)
      liLayerId = BP_AIN_SM_MTP2_LAYER;

    //logger.logMsg (TRACE_FLAG, 0,
    //    "Leaving INGwSmRepository::getLayerStrToVal");
    return liLayerId;
} 

/******************************************************************************
 *
 *     Fun:   getOperStrToVal()
 *
 *     Desc:  This function gets the OperationId from the string name
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getOperStrToVal (char *apcOperation)
{
    //logger.logMsg (TRACE_FLAG, 0,
    //    "Entering INGwSmRepository::getOperStrToVal");

    int liOperId = BP_AIN_SM_SUBTYPE_INVALID;

    if (strcmp (apcOperation, "gen") == 0)
      liOperId = BP_AIN_SM_SUBTYPE_GENSTS;
    else if (strcmp (apcOperation, "usp") == 0)
      liOperId = BP_AIN_SM_SUBTYPE_USPSTS;
    else if (strcmp (apcOperation, "lsp") == 0)
      liOperId = BP_AIN_SM_SUBTYPE_LSPSTS;
    else if (strcmp (apcOperation, "rte") == 0)
      liOperId = BP_AIN_SM_SUBTYPE_RTESTS;
    else if (strcmp (apcOperation, "psp") == 0)
      liOperId = BP_AIN_SM_SUBTYPE_PSPSTS;
    else if (strcmp (apcOperation, "lnk") == 0)
      liOperId = BP_AIN_SM_SUBTYPE_LNKSTS;

    //logger.logMsg (TRACE_FLAG, 0,
    //    "Leaving INGwSmRepository::getOperStrToVal");
    return liOperId;
}

/******************************************************************************
 *
 *     Fun:   getSystemTaskMask()
 *
 *     Desc:  This function returned Mask for system task creation
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getSystemTaskMask (int &aiMask)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSystemTaskMask");

    if (mbAnyParsingErrors)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Errors occurred while parsing the XML File");
      return BP_AIN_SM_FAIL;
    }

    DOMNode *lpNode = (DOMNode*) mpRootNode;
    if (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      { 
        DOMElement *lpElement = (DOMElement *) lpNode;

        XMLCh *lpTag = XMLString::transcode ("TAPA_CONF");
        DOMNodeList *lpNodeList = lpElement->getElementsByTagName (lpTag);
        XMLString::release (&lpTag);

        if (lpNodeList->getLength())
        { 
            DOMNode *lpRootNode = lpNodeList->item(0);

            lpNode = lpRootNode->getFirstChild ();

            char *lpcId, *lpcType;

            lpcId = new char[100];
            lpcType = new char [100];

            aiMask = 0;
            while (lpNode)
            {

              if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
              {
                char *nodeName = XMLString::transcode (lpNode->getNodeName());
                if (strcmp (nodeName, "layer") == 0 &&
                      getAttrVal ((DOMElement*) lpNode, (char*)"id", lpcId) &&
                      getAttrVal ((DOMElement*) lpNode, (char*)"STsk", lpcType))
                {
                    if (strcmp (lpcId, "SM") == 0 &&
                        strcmp (lpcType, "NEW") == 0)
                    {
                      aiMask = aiMask | BP_AIN_SM_SM_MASK;
                    }
                    else if (strcmp (lpcId, "IU") == 0 &&
                        strcmp (lpcType, "NEW") == 0)
                    {
                      aiMask = aiMask | BP_AIN_SM_TU_MASK;
                    }
                    else if (strcmp (lpcId, "IE") == 0 &&
                        strcmp (lpcType, "NEW") == 0)
                    {
                      aiMask = aiMask | BP_AIN_SM_AIN_MASK;
                    }
                    else if (strcmp (lpcId, "ST") == 0 &&
                        strcmp (lpcType, "NEW") == 0)
                    {
                      aiMask = aiMask | BP_AIN_SM_TCA_MASK;
                    }
                    else if (strcmp (lpcId, "SP") == 0 &&
                        strcmp (lpcType, "NEW") == 0)
                    {
                      aiMask = aiMask | BP_AIN_SM_SCC_MASK;
                    }
                    else if (strcmp (lpcId, "IT") == 0 &&
                        strcmp (lpcType, "NEW") == 0)
                    {
                      aiMask = aiMask | BP_AIN_SM_M3U_MASK;
                    }
                    else if (strcmp (lpcId, "SB") == 0 &&
                        strcmp (lpcType, "NEW") == 0)
                    {
                      aiMask = aiMask | BP_AIN_SM_SCT_MASK;
                    }
                    else if (strcmp (lpcId, "HI") == 0 &&
                        strcmp (lpcType, "NEW") == 0)
                    {
                      aiMask = aiMask | BP_AIN_SM_TUC_MASK;
                    }
                    else if (strcmp (lpcId, "SN") == 0 &&
                        strcmp (lpcType, "NEW") == 0)
                    {
                      aiMask = aiMask | BP_AIN_SM_MTP3_MASK;
                    }
                    else if (strcmp (lpcId, "SD") == 0 &&
                        strcmp (lpcType, "NEW") == 0)
                    {
                      aiMask = aiMask | BP_AIN_SM_MTP2_MASK;
                    }

                    logger.logMsg (TRACE_FLAG, 0,
                        "Layer Id = <%s>, MaskType = <%s>", lpcId, lpcType);
                }

                XMLString::release (&nodeName);
              }
              lpNode = lpNode->getNextSibling ();
            }

            delete [] lpcId;
            delete [] lpcType;
        }
      }
    }

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getSystemTaskMask");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getStatisticsList()
 *
 *     Desc:  This function returned the INGwSmOidList after parsing
 *            DOM Tree
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int 
INGwSmRepository::getStatisticsList (INGwSmStsOidList &aeOidList)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getStatisticsList");

    if (mbAnyParsingErrors)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Errors occurred while parsing the XML File");
      return BP_AIN_SM_FAIL;
    }


    DOMNode *lpNode = (DOMNode*) mpRootNode;
    if (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        DOMElement *lpElement = (DOMElement *) lpNode;

        XMLCh* lpTag = XMLString::transcode ("STATISTICS");
        DOMNodeList *lpNodeList = lpElement->getElementsByTagName (lpTag);

        XMLString::release (&lpTag);

        if (lpNodeList->getLength())
        {
            DOMNode *lpRootNode = lpNodeList->item(0);

            lpNode = lpRootNode->getFirstChild ();

            char *lpcId, *lpcLayer, *lpcType, *lpcElement, *lpcLevel;

            lpcId = new char[100];
            lpcLayer = new char [100];
            lpcType = new char [100];
            lpcElement = new char [100];
            lpcLevel = new char [100];

            while (lpNode)
            {

              if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
              {
                char *nodeName = XMLString::transcode (lpNode->getNodeName());
                if (strcmp (nodeName, "oid") == 0 &&
                      getStsOid ((DOMElement*) lpNode, lpcId,
                        lpcLayer, lpcType, lpcElement, lpcLevel))
                {
                    INGwSmStsOid *lpOid = new INGwSmStsOid;
                    strcpy (lpOid->oidString, lpcId);
                    lpOid->layer = getLayerStrToVal (lpcLayer);
                    lpOid->operation = getOperStrToVal (lpcType);
                    lpOid->element = atoi (lpcElement);
                    lpOid->level = atoi (lpcLevel);

                    aeOidList.push_back (lpOid);

                    logger.logMsg (TRACE_FLAG, 0,
                        "OID -> <%s, %d, %d, %d, %d>", lpOid->oidString, lpOid->layer,
                        lpOid->operation, lpOid->element, lpOid->level);
                }

                XMLString::release (&nodeName);
              }
              lpNode = lpNode->getNextSibling ();
            }

            logger.logMsg (TRACE_FLAG, 0,
                "Total statistics Oids obtained = %d", aeOidList.size());

            delete [] lpcId;
            delete [] lpcType;
            delete [] lpcElement;
            delete [] lpcLayer;
            delete [] lpcLevel;

            logger.logMsg (TRACE_FLAG, 0,
                "Leaving INGwSmRepository::getStatisticsList");
            return BP_AIN_SM_OK;
        }
      }
    }

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getStatisticsList");
    return BP_AIN_SM_FAIL;
}

/******************************************************************************
 *
 *     Fun:   getAttrVal()
 *
 *     Desc:  This function returns value of an attribute
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int 
INGwSmRepository::getAttrVal (DOMElement *apNode, char *apTag, char *apVal)
{
    if (apNode && apTag)   
    {                      
      char *attrVal;
      XMLCh *lpTag = (XMLString::transcode(apTag));

      attrVal = XMLString::transcode (apNode->getAttribute
            (lpTag));

      if (!attrVal)
      {  
        XMLString::release (&lpTag);
        XMLString::release (&attrVal);
        return BP_AIN_SM_FAIL;          
      }              

      strcpy (apVal, attrVal);

      XMLString::release (&lpTag);
      XMLString::release (&attrVal);

      return BP_AIN_SM_OK;
    }

    return BP_AIN_SM_FAIL;
}

/******************************************************************************
 *
 *     Fun:   getCfgOid()
 *
 *     Desc:  This function returns OID for Configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int 
INGwSmRepository::getCfgOid (DOMElement *apNode, char *apNameTag, 
      char *apName, char *apValTag, char *apValue)
{ 
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getCfgOid");

    if (apNode)
    {  
      if (getAttrVal (apNode, apNameTag, apName) == 0) 
      {   
        return BP_AIN_SM_FAIL;
      }   

      if (getAttrVal (apNode, apValTag, apValue) == 0)
      {
        return BP_AIN_SM_FAIL;
      }

      logger.logMsg (TRACE_FLAG, 0,
            "Leaving INGwSmRepository::getCfgOid");
      return BP_AIN_SM_OK;
    }
    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getCfgOid");
    return BP_AIN_SM_FAIL;
}


/******************************************************************************
 *
 *     Fun:   getSpDefSnri()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getSpDefSnri (DOMNode *apNode, U8 *defSNRI,
      int &aiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSpDefSnri");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *lpcChildName= XMLString::transcode (apNode->getNodeName());
        if (strcmp (lpcChildName, "defSNRI") == 0)
        {
            if (getAttrVal ((DOMElement*)apNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Concerned PC retrieved = <%s>", lpName);
              defSNRI[aiCount] = atoi (lpName);
              ++aiCount;
            }
        }
        XMLString::release (&lpcChildName);
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getSpDefSnri");
    return BP_AIN_SM_OK;
}


/******************************************************************************
 *    
 *     Fun:   getSpBpc()
 *         
 *     Desc:  This function returns Backup Point Code from the Node
 *     
 *     Notes: None
 * 
 *     File:  INGwSmRepository.C
 * 
 *******************************************************************************/
    int
INGwSmRepository::getSpBpc (DOMNode *apNode, SpBpcCfg *aeBpcList,
      int &aiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSpBpc");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {     
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      { 
        char *lpcChildName= XMLString::transcode (apNode->getNodeName());
        if (strcmp (lpcChildName, "bpc") == 0)
        {
            if (getAttrVal ((DOMElement*)apNode, (char*)"dpc", lpName) &&
                getAttrVal ((DOMElement*)apNode, (char*)"prior", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "BPC retrieved = <%s>, <%s>", lpName, lpValue);
              aeBpcList[aiCount].bpc = atoi (lpName);
              aeBpcList[aiCount].prior = atoi (lpValue);
              ++aiCount;
            }
        }
        XMLString::release (&lpcChildName);
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getSpBpc");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getZvDvKyVal()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZvDvKyVal (DOMNode *apNode, CmZvDvKyVal *aeKyVal)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZvDvKyVal");

    int aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "dpcEntries") == 0)
                {
                    aeKyVal->dpcEntries = atoi (lpValue);
                }
                else if (strcmp (lpName, "dpc") == 0)
                {
                    int liCount = 0;
                    if (aeKyVal->dpcEntries)
                      getU32List (apNode->getFirstChild(), &(aeKyVal->dpc[0]), liCount);
                    aeKyVal->dpcEntries = liCount;
                }
                else if (strcmp (lpName, "slsRange") == 0)
                {
                    aeKyVal->slsRange = atoi (lpValue);
                }
                else if (strcmp (lpName, "ssfEntries") == 0)
                {
                    aeKyVal->ssfEntries = atoi (lpValue);
                }
                else if (strcmp (lpName, "ssf") == 0)
                {
                    int liCount = 0;
                    if (aeKyVal->ssfEntries)
                      getU8ParmList (apNode->getFirstChild(), &(aeKyVal->ssf[0]), liCount);
                    aeKyVal->ssfEntries = liCount;
                }
                else if (strcmp (lpName, "siEntries") == 0)
                {
                    aeKyVal->siEntries = atoi (lpValue);
                }
                else if (strcmp (lpName, "si") == 0)
                {
                    int liCount = 0;
                    if (aeKyVal->siEntries)
                      getU8ParmList (apNode->getFirstChild(), &(aeKyVal->si[0]), liCount);
                    aeKyVal->siEntries = liCount;
                }
                else if (strcmp (lpName, "nwkEntries") == 0)
                {
                    aeKyVal->nwkEntries = atoi (lpValue);
                }
                else if (strcmp (lpName, "nwk") == 0)
                {
                    int liCount = 0;
                    if (aeKyVal->nwkEntries)
                      getU8ParmList (apNode->getFirstChild(), &(aeKyVal->nwk[0]), liCount);
                    aeKyVal->nwkEntries = liCount;
                }
              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZvDvKyVal");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getZnDnKyVal()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZnDnKyVal (DOMNode *apNode, CmZnDnKyVal *aeZnDnKyVal)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZnDnKyVal");

    int aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "dpcEntries") == 0)
                {
                    aeZnDnKyVal->dpcEntries = atoi (lpValue);
                }
                else if (strcmp (lpName, "dpc") == 0)
                {
                    int liCount = 0;
                    if (aeZnDnKyVal->dpcEntries)
                      getU32List (apNode->getFirstChild(), &(aeZnDnKyVal->dpc[0]), liCount);
                    aeZnDnKyVal->dpcEntries = liCount;
                }
                else if (strcmp (lpName, "slsRange") == 0)
                {
                    aeZnDnKyVal->slsRange = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                      "Oid slsRange= <%d>", aeZnDnKyVal->slsRange);
                }
                else if (strcmp (lpName, "ssfEntries") == 0)
                {
                    aeZnDnKyVal->ssfEntries = atoi (lpValue);
                }
                else if (strcmp (lpName, "ssf") == 0)
                {
                    int liCount = 0;
                    if (aeZnDnKyVal->ssfEntries)
                      getU8ParmList (apNode->getFirstChild(), &(aeZnDnKyVal->ssf[0]), liCount);
                    aeZnDnKyVal->ssfEntries = liCount;
                }
                else if (strcmp (lpName, "siEntries") == 0)
                {
                    aeZnDnKyVal->siEntries = atoi (lpValue);
                }
                else if (strcmp (lpName, "si") == 0)
                {
                    int liCount = 0;
                    if (aeZnDnKyVal->siEntries)
                      getU8ParmList (apNode->getFirstChild(), &(aeZnDnKyVal->si[0]), liCount);
                    aeZnDnKyVal->siEntries = liCount;
                }
                else if (strcmp (lpName, "varEntries") == 0)
                {
                    aeZnDnKyVal->varEntries = atoi (lpValue);
                }
                else if (strcmp (lpName, "var") == 0)
                {
                    int liCount = 0;
                    if (aeZnDnKyVal->varEntries)
                      getU8ParmList (apNode->getFirstChild(), &(aeZnDnKyVal->var[0]), liCount);
                    aeZnDnKyVal->varEntries = liCount;
                }
              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZnDnKyVal");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getZvDvDynamic()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZvDvDynamic (DOMNode *apNode, CmZvDvDynamic *aeDyn)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZvDvDynamic");

    int aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "cmFthaRsetRange") == 0)
                {
                    getAttrVal ((DOMElement*) apNode, (char*)"start", lpValue);
                    aeDyn->range.start = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                    getAttrVal ((DOMElement*) apNode, (char*)"end", lpValue);
                    aeDyn->range.end = atoi (lpValue);
                }
                else if (strcmp (lpName, "CmZvDvKyVal") == 0)
                {
                    getZvDvKyVal (apNode->getFirstChild(), &(aeDyn->keyVal));
                }
              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZvDvDynamic");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getZnDnDynamic()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZnDnDynamic (DOMNode *apNode, CmZnDnDynamic *aeZnDnDyn)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZnDnDynamic");

    int aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "cmFthaRsetRange") == 0)
                {
                    getAttrVal ((DOMElement*) apNode, (char*)"start", lpValue);
                    aeZnDnDyn->range.start = atoi (lpValue);
                    getAttrVal ((DOMElement*) apNode, (char*)"end", lpValue);
                    aeZnDnDyn->range.end = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                      "Manju:cmFthaRsetRange.start= <%d>, end:<%d>", aeZnDnDyn->range.start, aeZnDnDyn->range.end);
                }
                else if (strcmp (lpName, "CmZnDnKyVal") == 0)
                {
                    getZnDnKyVal (apNode->getFirstChild(), &(aeZnDnDyn->keyVal));
                }
              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZnDnDynamic");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getZvDvRsetMap()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZvDvRsetMap (DOMNode *apNode, CmZvDvRsetMap *aeRsetMap)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZvDvRsetMap");

    int aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "cmFthaDistType") == 0)
                {
                    getAttrVal ((DOMElement*) apNode, (char*)"dist", lpValue);
                    aeRsetMap->type.dist = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,"type.dist=%d",aeRsetMap->type.dist);
                    getAttrVal ((DOMElement*) apNode, (char*)"qual", lpValue);
                    aeRsetMap->type.qual = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,"type.qual=%d",aeRsetMap->type.qual);
                }
                else if (strcmp (lpName, "CmZvDvDynamic") == 0)
                {
                    if ((aeRsetMap->type.dist == CMFTHA_DIST_DYNAMIC) && (aeRsetMap->type.qual == CMFTHA_QUAL_DEFAULT)){
                      getZvDvDynamic (apNode->getFirstChild(), &(aeRsetMap->u.dyn));
                  }
                }
                else if (strcmp (lpName, "CmZvDvPrefProc") == 0)
                {
                    if ((aeRsetMap->type.dist == CMFTHA_DIST_DYNAMIC) && (aeRsetMap->type.qual == CMFTHA_QUAL_SAMEPROC))
                    {
                      getAttrVal ((DOMElement*) apNode, (char*)"start", lpValue);
                      aeRsetMap->u.prefProc.range.start = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,"prefProc.start=%d-%u",aeRsetMap->u.prefProc.range.start,
				&(aeRsetMap->u.prefProc.range.start));
                      getAttrVal ((DOMElement*) apNode, (char*)"end", lpValue);
                      aeRsetMap->u.prefProc.range.end = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,"prefProc.end=%d-%u",aeRsetMap->u.prefProc.range.end,
			&(aeRsetMap->u.prefProc.range.end));
                    }
                }
              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZvDvRsetMap");
    return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   getDnSingleSap()
 *
 *     Desc:  This function returns Single Sap Configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getDnSingleSap (DOMNode *apNode, LdnSingleSapCfg *aeDnSingleSap)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getDnSingleSap");

    int aiCount = 0;

    if (apNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
                     "apNode is NULL");
      return BP_AIN_SM_FAIL;
    }
    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);
              if (strcmp (lpName, "sapId") == 0)
              {
                aeDnSingleSap->sapId = atoi(lpValue);
              }
              else if (strcmp (lpName, "var") == 0)
              {
                aeDnSingleSap->var = atoi(lpValue);
              }
              else if (strcmp (lpName, "dpcLen") == 0)
              {
                aeDnSingleSap->dpcLen = atoi(lpValue);
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getDnSingleSap");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getDnSap()
 *
 *     Desc:  This function returns Entire Sap Configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getDnSap (DOMNode *apNode, LdnSapCfg *aeDnSingleSap, int numSaps)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getDnSap");
    int i=0;
    while (i < numSaps)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
      getDnSingleSap(apNode->getFirstChild(), &(aeDnSingleSap->sap[i]));
        i++;
      }
      apNode = apNode->getNextSibling();
    }
    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getDnSap");
    return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   getZnDnRsetMap()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZnDnRsetMap (DOMNode *apNode, CmZnDnRsetMap *aeZnDnRsetMap)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZnDnRsetMap");

    int aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "cmFthaDistType") == 0)
                {
                    getAttrVal ((DOMElement*) apNode, (char*)"dist", lpValue);
                    aeZnDnRsetMap->type.dist = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,"type.dist=%d",aeZnDnRsetMap->type.dist);
                    getAttrVal ((DOMElement*) apNode, (char*)"qual", lpValue);
                    aeZnDnRsetMap->type.qual = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,"type.qual=%d",aeZnDnRsetMap->type.qual);
                }
                else if (strcmp (lpName, "CmZnDnDynamic") == 0)
                {
                    if ((aeZnDnRsetMap->type.dist == CMFTHA_DIST_DYNAMIC) && (aeZnDnRsetMap->type.qual == CMFTHA_QUAL_DEFAULT))
                    {
                      logger.logMsg (TRACE_FLAG, 0,"IN CmZnDnDynamic");
                      getZnDnDynamic (apNode->getFirstChild(), &(aeZnDnRsetMap->u.dyn));
                }
                }
                else if (strcmp (lpName, "CmZnDnSameProc") == 0)
                {
                    if ((aeZnDnRsetMap->type.dist == CMFTHA_DIST_DYNAMIC) && (aeZnDnRsetMap->type.qual == CMFTHA_QUAL_SAMEPROC))
                    {
                      logger.logMsg (TRACE_FLAG, 0,"IN CmZnDnSameProc");
                      getAttrVal ((DOMElement*) apNode, (char*)"start", lpValue);
                      aeZnDnRsetMap->u.sameProc.range.start = atoi (lpValue);
                      getAttrVal ((DOMElement*) apNode, (char*)"end", lpValue);
                      aeZnDnRsetMap->u.sameProc.range.end = atoi (lpValue);
                      logger.logMsg (TRACE_FLAG, 0,"range.start = %d range.end=%d",aeZnDnRsetMap->u.sameProc.range.start,aeZnDnRsetMap->u.sameProc.range.end);
                    }
                }
              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZnDnRsetMap");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getZvDvRsetDefn()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZvDvRsetDefn (DOMNode *apNode, CmZvDvRsetDefn *aeRsetDefn)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZvDvRsetDefn");

    int aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "cmFthaDistType") == 0)
                {
                    getAttrVal ((DOMElement*) apNode, (char*)"dist", lpValue);
                    aeRsetDefn->type.dist = atoi (lpValue);
                    getAttrVal ((DOMElement*) apNode, (char*)"qual", lpValue);
                    aeRsetDefn->type.qual = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                       "type=<%d>,qual=<%d>", aeRsetDefn->type.dist,
                        aeRsetDefn->type.qual);
                }
                else if (strcmp (lpName, "attr") == 0)
                {
                    aeRsetDefn->attr = atoi (lpValue);    
                }
                else if (strcmp (lpName, "CmZvDvMaxKyElmts") == 0)
                {
                    getAttrVal ((DOMElement*) apNode, (char*)"sls", lpValue);
                    aeRsetDefn->max.sls = atoi (lpValue);
                    getAttrVal ((DOMElement*) apNode, (char*)"si", lpValue);
                    aeRsetDefn->max.si = atoi (lpValue);
                    getAttrVal ((DOMElement*) apNode, (char*)"nwk", lpValue);
                    aeRsetDefn->max.nwk = atoi (lpValue);
                    getAttrVal ((DOMElement*) apNode, (char*)"ssf", lpValue);
                    aeRsetDefn->max.ssf = atoi (lpValue);
                    getAttrVal ((DOMElement*) apNode, (char*)"dpc", lpValue);
                    aeRsetDefn->max.dpc = atoi (lpValue);
                    getAttrVal ((DOMElement*) apNode, (char*)"rsets", lpValue);
                    aeRsetDefn->max.rsets = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                       "sls=<%d>,si=<%d>,nwk=<%d>,ssf<%d>,dpc<%d>,rsets<%d>", aeRsetDefn->max.sls,
                        aeRsetDefn->max.si,aeRsetDefn->max.nwk,aeRsetDefn->max.ssf,aeRsetDefn->max.dpc,aeRsetDefn->max.rsets);
                }
              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZvDvRsetDefn");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getZnDnRsetDefn()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZnDnRsetDefn (DOMNode *apNode, CmZnDnRsetDefn *aeZnDnRsetDefn)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZnDnRsetDefn");

    int aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);
              if (strcmp (lpName, "cmFthaDistType") == 0)
              {
                getAttrVal ((DOMElement*) apNode, (char*)"dist", lpValue);
                aeZnDnRsetDefn->type.dist = atoi (lpValue);
                getAttrVal ((DOMElement*) apNode, (char*)"qual", lpValue);
                aeZnDnRsetDefn->type.qual = atoi (lpValue);
                logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved dist= <%d>, qual=<%d>", aeZnDnRsetDefn->type.dist, aeZnDnRsetDefn->type.qual);
              }
              else if (strcmp (lpName, "attr") == 0)
              {
                aeZnDnRsetDefn->attr = atoi (lpValue);    
              }
              else if (strcmp (lpName, "CmZnDnMaxKyElmts") == 0)
              {
                getAttrVal ((DOMElement*) apNode, (char*)"sls", lpValue);
                aeZnDnRsetDefn->max.sls = atoi (lpValue);
                logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved sls= <%d>", aeZnDnRsetDefn->max.sls);
                getAttrVal ((DOMElement*) apNode, (char*)"si", lpValue);
                aeZnDnRsetDefn->max.si = atoi (lpValue);
                logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved si= <%d>", aeZnDnRsetDefn->max.si);
                getAttrVal ((DOMElement*) apNode, (char*)"var", lpValue);
                aeZnDnRsetDefn->max.var = atoi (lpValue);
                logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved var= <%d>", aeZnDnRsetDefn->max.var);
                getAttrVal ((DOMElement*) apNode, (char*)"ssf", lpValue);
                aeZnDnRsetDefn->max.ssf = atoi (lpValue);
                logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved ssf= <%d>", aeZnDnRsetDefn->max.ssf);
                getAttrVal ((DOMElement*) apNode, (char*)"dpc", lpValue);
                aeZnDnRsetDefn->max.dpc = atoi (lpValue);
                logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved dpc= <%d>", aeZnDnRsetDefn->max.dpc);
                getAttrVal ((DOMElement*) apNode, (char*)"rsets", lpValue);
                aeZnDnRsetDefn->max.rsets = atoi (lpValue);
                logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved rsets= <%d>", aeZnDnRsetDefn->max.rsets);
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZnDnRsetDefn");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getZnDnRsetGen()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZnDnRsetGen (DOMNode *apNode, CmZnDnRsetGenCfg *aeRsetGen)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZnDNRsetGen");

    int aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);
              if (strcmp (lpName, "ifType") == 0)
              {
                aeRsetGen->ifType = atoi (lpValue);    
              }
              else if (strcmp (lpName, "CmZnDnRsetDefn_upper") == 0)
              {
                getZnDnRsetDefn (apNode->getFirstChild(), &(aeRsetGen->upper));
              }
              else if (strcmp (lpName, "CmZnDnRsetDefn_lower") == 0)
              {
                getZnDnRsetDefn (apNode->getFirstChild(), &(aeRsetGen->lower));
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZnDNRsetGen");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getZvDvRsetGen()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZvDvRsetGen (DOMNode *apNode, CmZvDvRsetGenCfg *aeRsetGen)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZvDvRsetGen");

    int aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "ifType") == 0)
                {
                    aeRsetGen->ifType = atoi (lpValue);    
                }
                else if (strcmp (lpName, "CmZvDvRsetDefn_upper") == 0)
                {
                    getZvDvRsetDefn (apNode->getFirstChild(), &(aeRsetGen->upper));
                }
                else if (strcmp (lpName, "CmZvDvRsetDefn_lower") == 0)
                {
                    getZvDvRsetDefn (apNode->getFirstChild(), &(aeRsetGen->lower));
                }
              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZvDvRsetGen");
    return BP_AIN_SM_OK;
}




/******************************************************************************
 *
 *     Fun:   getSpSelfPc()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getSpSelfPc (DOMNode *apNode, Dpc *aeSelfPc,
      int &aiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSpSelfPc");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *lpcChildName= XMLString::transcode (apNode->getNodeName());
        if (strcmp (lpcChildName, "pc") == 0)
        {
            if (getAttrVal ((DOMElement*)apNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Concerned PC retrieved = <%s>", lpName);
              aeSelfPc[aiCount] = atoi (lpName);
              ++aiCount;
            }
        }
        XMLString::release (&lpcChildName);
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getSpSelfPc");
    return BP_AIN_SM_OK;
}

#ifdef LSPV2_8

/******************************************************************************
 *
 *     Fun:   getSpAspSsn()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getSpAspSsn (DOMNode *apNode, SpAsSsn *aspList,
      int &aiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSpAspSsn");

    int licount = 0;
    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "ssNum") == 0)
                {
                    aspList->ssNum = atoi (lpValue);
                }
                else if (strcmp (lpName, "numAspCpc") == 0)
                {
                    aspList->numAspCpc = atoi (lpValue);
                }
                else if (strcmp (lpName, "ssnStatus") == 0)
                {
                    aspList->ssnStatus = atoi (lpValue);
                }
                else if (strcmp (lpName, "aspCpc") == 0)
                {
                   getU32List (apNode->getFirstChild (), &(aspList->aspCpc[0]), aiCount);
                }
              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getSpAspSsn");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getSpAspPcSsnList()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getSpAspPcSsnList (DOMNode *apNode, SpAspPcSsnList *aspList,
      int &aiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSpAspPcSsnList");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "numAspSsn") == 0)
                {
                    aspList->numAspSsn = atoi (lpValue);
                }
                else if (strcmp (lpName, "aspStatus") == 0)
                {
                    aspList->aspStatus = atoi (lpValue);
                }
                else if (strcmp (lpName, "aspPc") == 0)
                {
                    aspList->aspPc = atoi (lpValue);
                }
                else if (strcmp (lpName, "aspSsn") == 0)
                {
		   DOMNode *lpChild = apNode->getFirstChild ();

                   int liCount = 0;
                   for (int i=0; (i < aspList->numAspSsn) && apNode; i++)
		   { 
                     // getSpAsSsn (lpChild->getFirstChild (), &(aspList->aspSsn[i]), liCount);
                      lpChild = lpChild->getNextSibling ();
		   } 
                }

              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getSpAspPcSsnList");
    return BP_AIN_SM_OK;
}
#endif


/******************************************************************************
 *
 *     Fun:   getItSelfAspId()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getItSelfAspId (DOMNode *apNode, U32 *selfAspId,
      int &aiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getItSelfAspId");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *lpcChildName= XMLString::transcode (apNode->getNodeName());
        if (strcmp (lpcChildName, "aspId") == 0)
        {
            if (getAttrVal ((DOMElement*)apNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Concerned PC retrieved = <%s>", lpName);
              selfAspId[aiCount] = atoi (lpName);
              ++aiCount;
            }
        }
        XMLString::release (&lpcChildName);
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getItSelfAspId");
    return BP_AIN_SM_OK;
}




/******************************************************************************
 *
 *     Fun:   getHiRsetSpec()
 *
 *     Desc:  
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getHiRsetSpec (DOMNode *apNode, SgHiRsetSpec *aeParm,
      int &aiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getHiRsetSpec");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        if (getAttrVal ((DOMElement*)apNode, (char*)"rsetId", lpName))
        {
            aeParm[aiCount].rsetId = atoi (lpName);
            logger.logMsg (TRACE_FLAG, 0,
                "<rsetId> <%d>",aeParm[aiCount].rsetId);
        }
        if (getAttrVal ((DOMElement*)apNode, (char*)"rsetGrp", lpName))
        {
            aeParm[aiCount].rsetGrp = atoi (lpName);
            logger.logMsg (TRACE_FLAG, 0,
                "<rsetGrp> <%d>",aeParm[aiCount].rsetGrp);
        }
        ++aiCount;
    //    XMLString::release (&lpcChildName);
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getHiRsetSpec");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getU8ParmList()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getU8ParmList (DOMNode *apNode, U8 *aeParm,
      int &aiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getU8ParmList");

   aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        if (getAttrVal ((DOMElement*)apNode, (char*)"value", lpValue))
        {
            logger.logMsg (TRACE_FLAG, 0,
                "Concerned parm retrieved = <%s>", lpValue);
            aeParm[aiCount] = atoi (lpValue);
            ++aiCount;
        }
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getU8ParmList");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getU32List()
 *
 *     Desc:  This function returns Concerned PC from the Node
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getU32List (DOMNode *apNode, Dpc *aePc,
      int &aiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getU32List");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
        {
              logger.logMsg (TRACE_FLAG, 0,
                    "Concerned Value retrieved = <%s>", lpName);
              aePc[aiCount] = atoi (lpValue);
              ++aiCount;
        }
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getU32List");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getSpSsnCfg()
 *     
 *     Desc:  This function returns SSN Configuration from the Node
 *     
 *     Notes: None
 *     
 *     File:  INGwSmRepository.C
 *     
 *******************************************************************************/
    int
INGwSmRepository::getSpSsnCfg (DOMNode *apNode, SpSsnCfg *aeSsn,
      int &aiCount)
{                            
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSpSsnCfg");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *lpcChildName= XMLString::transcode (apNode->getNodeName());
        if (strcmp (lpcChildName, mpcCfgOidElement) == 0)
			
        {
           if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
           {
              logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
			  if (strcmp (lpName, "ssn") == 0)
              {
                    aeSsn->ssn = atoi (lpValue);
              }
			  else if (strcmp (lpName, "status") == 0)
              {
                    aeSsn->status = atoi (lpValue);
              }
#if (SS7_ANS96 || SS7_BELL05)
// lsp.x::SpSsnCfg
			  else if (strcmp (lpName, "replicatedMode") == 0)
              {
                    aeSsn->replicatedMode = atoi (lpValue);
              } 
#endif
			  else if (strcmp (lpName, "nmbBpc") == 0)
              {
                    aeSsn->nmbBpc = atoi (lpValue);
              } 
			  else if (strcmp (lpName, "bpcList") == 0)
              {
                 int liCount = 0;
   
                 if (aeSsn->nmbBpc && 
                       getSpBpc (apNode->getFirstChild(), &(aeSsn->bpcList[0]),
                         liCount))
                 {
                   aeSsn->nmbBpc = liCount;
                 }
                 else
                 {
                   aeSsn->nmbBpc = 0;
                 }
              } 
			  else if (strcmp (lpName, "nmbConPc") == 0)
              {
                    aeSsn->nmbConPc = atoi (lpValue);
              } 
			  else if (strcmp (lpName, "conPc") == 0)
              {
                 int liCount = 0;
   
                 if (aeSsn->nmbConPc && 
                       getU32List (apNode->getFirstChild(), &(aeSsn->conPc[0]),
                         liCount))
                 {
                   aeSsn->nmbConPc = liCount;
                 }
                 else
                 {
                   aeSsn->nmbConPc = 0;
                 }
              } 
           }
        }   
        XMLString::release (&lpcChildName);
      }     
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getSpSsnCfg");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getItNwkApp ()
 *     
 *     Desc:  This function returns network appearance codes from the node
 *     
 *     Notes: None
 *     
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getItNwkApp (DOMNode *apNode, ItNwkApp *aeNwkApp)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getItNwkApp");

    int liCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *lpcChildName= XMLString::transcode (apNode->getNodeName());
        if (strcmp (lpcChildName, "nwkApp") == 0)
        {
            if (getAttrVal ((DOMElement*)apNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Nwk Appearance retrieved = <%s>", lpName);
              aeNwkApp[liCount] = atoi (lpName);
              ++liCount;
            }
        }
        XMLString::release (&lpcChildName);
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getItNwkApp");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getItPsp ()
 *             
 *     Desc:  This function returns PSP list from a Node
 *         
 *     Notes: None 
 *         
 *     File:  INGwSmRepository.C
 *         
 *******************************************************************************/
    int       
INGwSmRepository::getItPsp (DOMNode *apNode, ItPspId *aePspList, int &aiCount)
{       
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getItPsp");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {         
        char *lpcChildName= XMLString::transcode (apNode->getNodeName());
        if (strcmp (lpcChildName, "psp") == 0)
        {     
            if (getAttrVal ((DOMElement*)apNode, (char*)"value", lpName))
            { 
              logger.logMsg (TRACE_FLAG, 0,
                    "PSP Id retrieved = <%s>", lpName);
              aePspList[aiCount] = atoi (lpName);
              ++aiCount;
            }
        }
        XMLString::release (&lpcChildName);
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getItPsp");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getHiPrngFile ()
 *             
 *     Desc:  This function returns PSP list from a Node
 *         
 *     Notes: None 
 *         
 *     File:  INGwSmRepository.C
 *         
 *******************************************************************************/
    int       
INGwSmRepository::getHiPrngFile (DOMNode *apNode, S8 *file, int &aiCount)
{       
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getHiPrngFile");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {         
        char *lpcChildName= XMLString::transcode (apNode->getNodeName());
        if (strcmp (lpcChildName, "file") == 0)
        {     
            if (getAttrVal ((DOMElement*)apNode, (char*)"value", lpName))
            { 
              logger.logMsg (TRACE_FLAG, 0,
                    "file retrieved = <%s>", lpName);
              file[aiCount] = atoi (lpName);
              ++aiCount;
            }
        }
        XMLString::release (&lpcChildName);
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getHiPrngFile");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getHiPrngSeed ()
 *             
 *     Desc:  This function returns PSP list from a Node
 *         
 *     Notes: None 
 *         
 *     File:  INGwSmRepository.C
 *         
 *******************************************************************************/
    int       
INGwSmRepository::getHiPrngSeed (DOMNode *apNode, U8 *seed, int &aiCount)
{       
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getHiPrngSeed");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {         
        char *lpcChildName= XMLString::transcode (apNode->getNodeName());
        if (strcmp (lpcChildName, "seed") == 0)
        {     
            if (getAttrVal ((DOMElement*)apNode, (char*)"value", lpName))
            { 
              logger.logMsg (TRACE_FLAG, 0,
                    "seed retrieved = <%s>", lpName);
              seed[aiCount] = atoi (lpName);
              ++aiCount;
            }
        }
        XMLString::release (&lpcChildName);
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getHiPrngSeed");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getItPsp ()
 *             
 *     Desc:  This function returns PSP list from a Node
 *         
 *     Notes: None 
 *         
 *     File:  INGwSmRepository.C
 *         
 *******************************************************************************/
    int       
INGwSmRepository::getEpIds (DOMNode *apNode, U32 *aeEpIds, int &aiCount)
{       
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getItPsp");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {         
        char *lpcChildName= XMLString::transcode (apNode->getNodeName());
        if (strcmp (lpcChildName, "epId") == 0)
        {     
            if (getAttrVal ((DOMElement*)apNode, (char*)"value", lpName))
            { 
              logger.logMsg (TRACE_FLAG, 0,
                    "PSP Id retrieved = <%s>", lpName);
              aeEpIds[aiCount] = atoi (lpName);
              ++aiCount;
            }
        }
        XMLString::release (&lpcChildName);
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getItPsp");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getItPspEpList ()
 *             
 *     Desc:  This function returns PSP list from a Node
 *         
 *     Notes: None 
 *         
 *     File:  INGwSmRepository.C
 *         
 *******************************************************************************/
    int       
INGwSmRepository::getItPspEpList (DOMNode *apNode, ItPspEp *pspEpList, int &aiCount)
{       
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getItPspEpList");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {         
        char *lpcChildName= XMLString::transcode (apNode->getNodeName());
        if (strcmp (lpcChildName, "nmbEp") == 0)
        {     
            logger.logMsg (TRACE_FLAG, 0,
                "Number of EP retrieved = <%s>", lpName);
            pspEpList[aiCount].nmbEp = atoi (lpName);

            int liCount = 0;

            if (pspEpList [aiCount].nmbEp && 
                getEpIds (apNode->getFirstChild(), &(pspEpList [aiCount].endpIds[0]),
                    liCount))
            {
              pspEpList [aiCount].nmbEp = liCount;
            }
            else
            {
              pspEpList [aiCount].nmbEp = 0;
            }
        }
        XMLString::release (&lpcChildName);
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getItPspEpList");
    return BP_AIN_SM_OK;
}




/******************************************************************************
 *
 *     Fun:   getItAddrLst ()
 *     
 *     Desc:  This function returns address list from a Node
 *     
 *     Notes: None
 *     
 *     File:  INGwSmRepository.C
 *     
 *******************************************************************************/
    int   
INGwSmRepository::getItAddrLst (DOMNode *apNode, 
      CmNetAddr *aeAddrLst, 
      int &aiCount)
{     
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getItAddrLst");

    aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      { 
        char *lpcChildName= XMLString::transcode (apNode->getNodeName());
        if (strcmp (lpcChildName, "addr") == 0)
        {
            getAttrVal ((DOMElement*)apNode, (char*)"type", lpName);
            if (!strcmp (lpName, "ipv4"))
            { 
              logger.logMsg (TRACE_FLAG, 0,
                    "PSP Address retrieved = <%s>", lpName);

              aeAddrLst[aiCount].type = CM_NETADDR_IPV4;
              getAttrVal ((DOMElement*)apNode, (char*)"value", lpName);
              logger.logMsg (TRACE_FLAG, 0,
                    "PSP Address = <%s>", lpName);

              //set the ipaddress
              char *ip, *ch3;
              int num = 0;
              unsigned int address = 0;

              ip = strtok_r (lpName, ".", &ch3);
              if (ip == 0)
                num = 0;
              else
                num = atoi (ip);
              address = num << 24;

              ip = strtok_r (0, ".", &ch3);
              if (ip == 0)
                num = 0;
              else
                num = atoi (ip);
              address += num << 16;

              ip = strtok_r (0, ".", &ch3);
              if (ip == 0)
                num = 0;
              else
                num = atoi (ip);
              address += num << 8;

              ip = strtok_r (0, ".", &ch3);
              if (ip == 0)
                num = 0;
              else
                num = atoi (ip);
              address += num;

              aeAddrLst[aiCount].u.ipv4NetAddr = address;

              logger.logMsg (ERROR_FLAG, 0,
                    "Address in int = <%u>",address);

              logger.logMsg (ERROR_FLAG, 0,
                    "Address Obtained = <%u.%u.%u.%u>",
                    INGwSmGetHiByte(GetHiWord(address)),
                    INGwSmGetLoByte(GetHiWord(address)),
                    INGwSmGetHiByte(GetLoWord(address)),
                    INGwSmGetLoByte(GetLoWord(address)));

              ++aiCount;
            }
            if (!strcmp (lpName, "ipv6"))
            { 
              logger.logMsg (TRACE_FLAG, 0,
                    "PSP Address retrieved = <%s>", lpName);

              aeAddrLst[aiCount].type = CM_NETADDR_IPV6;
              getAttrVal ((DOMElement*)apNode, (char*)"value", lpName);

              //set the ipaddress
              char *ip, *ch3;
              int num = 0;
              unsigned int address = 0;

              ip = strtok_r (lpName, ".", &ch3);
              if (ip == 0)
                num = 0;
              else
                num = atoi (ip);
              address = num << 24;

              ip = strtok_r (0, ".", &ch3);
              if (ip == 0)
                num = 0;
              else
                num = atoi (ip);
              address += num << 16;

              ip = strtok_r (0, ".", &ch3);
              if (ip == 0)
                num = 0;
              else
                num = atoi (ip);
              address += num << 8;

              ip = strtok_r (0, ".", &ch3);
              if (ip == 0)
                num = 0;
              else
                num = atoi (ip);
              address += num;

              strcpy((S8*)aeAddrLst[aiCount].u.ipv6NetAddr,(S8 *)address);

              logger.logMsg (ERROR_FLAG, 0,
                    "Address Obtained = <%u.%u.%u.%u>",
                    INGwSmGetHiByte(GetHiWord(address)),
                    INGwSmGetLoByte(GetHiWord(address)),
                    INGwSmGetHiByte(GetLoWord(address)),
                    INGwSmGetLoByte(GetLoWord(address)));

              ++aiCount;
            }
        }
        XMLString::release (&lpcChildName);
      }
      apNode = apNode->getNextSibling();
    }

    delete [] lpName;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getItAddrLst");
    return BP_AIN_SM_OK;
}



/******************************************************************************
 *     
 *     Fun:   getCfgNode()
 *     
 *     Desc:  This function returns root DOMElement for the Configuration
 *     
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    DOMNode* 
INGwSmRepository::getCfgNode (char *apEl,
      char *apLayer, char *apType)
{         
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getCfgNode");

    if (mbAnyParsingErrors)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Errors occurred while parsing the XML File");
      return 0;
    }


    DOMNode *lpNode = (DOMNode*) mpRootNode;
    if (lpNode)
    { 
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {     
        DOMElement *lpElement = (DOMElement *) lpNode;

        XMLCh *lpTag = XMLString::transcode (apEl);
        DOMNodeList *lpNodeList = lpElement->getElementsByTagName (lpTag);
        XMLString::release (&lpTag);

        for (int count = 0; count < lpNodeList->getLength(); count++)
        {          
            DOMNode *lpNode = lpNodeList->item(count);
            if (apLayer && apType)
            {   
              char *attrName, *attrVal;
              attrName = new char [100];
              attrVal = new char [100];
              if (getAttrVal ((DOMElement*) lpNode, (char*)"layer", attrName) == 0)
              {   
                delete [] attrName;
                delete [] attrVal;
                return 0;
              }   
              else if (getAttrVal ((DOMElement*) lpNode, (char*)"type", attrVal) == 0)
              {
                delete [] attrName;
                delete [] attrVal;
                return 0;
              }
              if (strcmp (attrName, apLayer) == 0 &&
                    strcmp (attrVal, apType) == 0)
              {
                delete [] attrName;
                delete [] attrVal;
                logger.logMsg (TRACE_FLAG, 0,
                      "Leaving INGwSmRepository::getCfgNode");
                return lpNode;
              }
              delete [] attrName;
              delete [] attrVal;
            }
        }
      }
    }

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getCfgNode");
    return 0;
}

/******************************************************************************
 * 
 *     Fun:   getLayerOpStr()
 * 
 *     Desc:  This function returns string for the layer operation
 * 
 *     Notes: None
 * 
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getLayerOpStr (int aiLayerOp, string &astrLayer)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getLayerOpStr");

    astrLayer.clear();

    switch (aiLayerOp)
    {
      case BP_AIN_SM_SUBTYPE_GENCFG:
        {
            astrLayer = mpcGenCfg;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_USPCFG:
        {
            astrLayer = mpcUspCfg;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_LSPCFG:
        {
            astrLayer = mpcLspCfg;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_NWKCFG:
        {
            astrLayer = mpcNwkCfg;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_RTECFG:
        {
            astrLayer = mpcRteCfg;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_PSCFG:
        {
            astrLayer = mpcPsCfg;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_PSPCFG:
        {
            astrLayer = mpcPspCfg;
            return BP_AIN_SM_OK;
        };
        break;

      case BP_AIN_SM_SUBTYPE_LNKSETCFG:
        {
            astrLayer = mpcLnksetCfg;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_RTECFG_SELF:
        {
            astrLayer = mpcRteCfgSelf;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_RTECFG_PEER:
        {
            astrLayer = mpcRteCfgPeer;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL:
        {
            astrLayer = mpcRsetMapCfgCritical;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF:
        {
            astrLayer = mpcRsetMapCfgDef;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL:
        {
            astrLayer = mpcRsetMapCfgNonCritical;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_ENT_SG:
        {
            astrLayer = mpcEntSg;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_ENT_SCCP:
        {
            astrLayer = mpcEntSp;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_ENT_TCAP:
        {
            astrLayer = mpcEntSt;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_ENT_TUCL:
        {
            astrLayer = mpcEntHi;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_ENT_SCTP:
        {
            astrLayer = mpcEntSb;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_ENT_M3UA:
        {
            astrLayer = mpcEntIt;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_ENT_MTP3:
        {
            astrLayer = mpcEntSn;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_ENT_MTP2:
        {
            astrLayer = mpcEntSd;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_LIS_CHANCFG:
        {
            astrLayer = mpcRyLchanCfg;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_SRV_CHANCFG:
        {
            astrLayer = mpcRySchanCfg;
            return BP_AIN_SM_OK;
        };
        break;
      case BP_AIN_SM_SUBTYPE_CLI_CHANCFG:
        {
            astrLayer = mpcRyCchanCfg;
            return BP_AIN_SM_OK;
        };
        break;
//#endif
      default:
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown layerOp passed <%d>", aiLayerOp);
        }
        break;
    }

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getLayerOpStr");
    return BP_AIN_SM_FAIL;
}

/******************************************************************************
 *
 *     Fun:   getStMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for TCAP
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int 
INGwSmRepository::getStMngmt (int aiType, StMngmt &aeMgmt, int aiIndex)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getStMngmt");

    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode *lpNode = getCfgNode ((char*)mpcCfgElement, 
        (char*)"tcap", (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <tcap:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    cmMemset((U8 *)&aeMgmt, '\0', sizeof(StMngmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;


    char *lpName = new char [100];
    char *lpValue = new char [100];

    lpNode = lpNode->getFirstChild();

    bool indexFound = false;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "nmbSaps") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.nmbSaps= atoi (lpValue);    /* number of saps */
                }
                else if (strcmp (lpName, "nmbDlgs") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.nmbDlgs = atoi (lpValue);    /* number of dlg - sys wide */
                }
                else if (strcmp (lpName, "nmbInvs") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.nmbInvs  = atoi (lpValue);    /* number of invs - sys wide */
                }
                else if (strcmp (lpName, "nmbBins") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.nmbBins = atoi (lpValue); /* number of hash bins */
                }
                else if (strcmp (lpName, "timeRes") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.timeRes = atoi (lpValue); /* timer resolution */
                }
                else if (strcmp (lpName, "sapTimeRes") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.sapTimeRes = atoi (lpValue); /* Sap timer resolution */
                }
                else if (strcmp (lpName, "loDlgId") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.loDlgId = atoi (lpValue);
                    tcapLoDlgId =  aeMgmt.t.cfg.s.genCfg.loDlgId;
                }
                else if (strcmp (lpName, "hiDlgId") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.hiDlgId = atoi (lpValue); 
                    tcapHiDlgId =  aeMgmt.t.cfg.s.genCfg.hiDlgId;

                }
                else if (strcmp (lpName, "bitMapFlg") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.bitMapFlg = atoi (lpValue); /* if bitmap to be used for dlgs */
                }
                else if (strcmp (lpName, "errCntrlFlg") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.errCntrlFlg = atoi (lpValue); /* error control */
                }
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_USPCFG)
              {
                if (strcmp (lpName, "swtch") == 0)
                { //ALLOWABLE VALUES
                    //LST_SW_ITU88
                    //LST_SW_ITU92
                    //LST_SW_ITU96
                    //LST_SW_ETS96
                    //LST_SW_ANS88
                    //LST_SW_ANS92
                    //LST_SW_ANS96  
                    string l_protocol("");
                    if(0 != INGwIfrPrParamRepository::getInstance().
																			getValue("PROTO", l_protocol))
                    {
                      l_protocol = "ITU";   
                      aeMgmt.t.cfg.s.tuSapCfg.swtch = LST_SW_ITU92;
                      //LST_SW_ITU92, LST_SW_ITU88
                    }

                    if("NTT" == l_protocol)
                    {
                      aeMgmt.t.cfg.s.tuSapCfg.swtch = LST_SW_NTT_INTE_NW;
                    }

                    else if("ANSI" == l_protocol)
                    {
                      aeMgmt.t.cfg.s.tuSapCfg.swtch = LST_SW_ANS96;
                      //LST_SW_ITU88, LST_SW_ITU92, LST_SW_ITU96
                    }

                    else if("ITU" == l_protocol)
                    {
                      aeMgmt.t.cfg.s.tuSapCfg.swtch = LST_SW_ITU92;
                      //LST_SW_ITU92, LST_SW_ITU88
                    } 

                }
                else if (strcmp (lpName, "sapId") == 0)
                {
                    aeMgmt.hdr.elmId.elmntInst1 = atoi (lpValue);
                }
                else if (strcmp (lpName, "loDlgId") == 0)
                {
                    aeMgmt.t.cfg.s.tuSapCfg.loDlgId = atoi (lpValue);
                }
                else if (strcmp (lpName, "hiDlgId") == 0)
                { 
                    aeMgmt.t.cfg.s.tuSapCfg.hiDlgId = atoi (lpValue);
                }
                else if (strcmp (lpName, "nmbDlgs") == 0)
                {
                    aeMgmt.t.cfg.s.tuSapCfg.nmbDlgs = atoi (lpValue);
                }
                else if (strcmp (lpName, "nmbInvs") == 0)
                {
                    aeMgmt.t.cfg.s.tuSapCfg.nmbInvs = atoi (lpValue);
                }
                else if (strcmp (lpName, "nmbBins") == 0)
                {
                    aeMgmt.t.cfg.s.tuSapCfg.nmbBins = atoi (lpValue);
                }
                //the following two times are used for ITU only
                else if (strcmp (lpName, "t1") == 0)
                {
                    aeMgmt.t.cfg.s.tuSapCfg.t1.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.tuSapCfg.t1.val)
                      aeMgmt.t.cfg.s.tuSapCfg.t1.enb = true;
                    else
                      aeMgmt.t.cfg.s.tuSapCfg.t1.enb = false;
                }
                else if (strcmp (lpName, "t2") == 0)
                {
                    aeMgmt.t.cfg.s.tuSapCfg.t2.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.tuSapCfg.t2.val)
                      aeMgmt.t.cfg.s.tuSapCfg.t2.enb = true;
                    else
                      aeMgmt.t.cfg.s.tuSapCfg.t2.enb = false;
                }
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_LSPCFG)
              {
                if (strcmp (lpName, "swtch") == 0)
                {
                    string l_protocol("");
                    if(0 != INGwIfrPrParamRepository::getInstance().
																			getValue("PROTO", l_protocol))
                    {
                      l_protocol = "ITU";   
                      aeMgmt.t.cfg.s.spSapCfg.swtch = LST_SW_ITU92;
                      //LST_SW_ITU92, LST_SW_ITU88
                    }

                    if("NTT" == l_protocol )
                    {
                      aeMgmt.t.cfg.s.spSapCfg.swtch = LST_SW_NTT_INTE_NW;
                    }

                    if("ANSI" == l_protocol )
                    {
                      aeMgmt.t.cfg.s.spSapCfg.swtch = LST_SW_ANS96;
                      //LST_SW_ITU88, LST_SW_ITU92, LST_SW_ITU96
                    }

                    if("ITU" == l_protocol )
                    {
                      aeMgmt.t.cfg.s.spSapCfg.swtch = LST_SW_ITU92;
                      //LST_SW_ITU92, LST_SW_ITU88
                    } 


                    
                }
                else if (strcmp (lpName, "sapId") == 0)
                {
                    aeMgmt.hdr.elmId.elmntInst1 = atoi (lpValue);
                }
                else if (strcmp (lpName, "spId") == 0)
                {
                    aeMgmt.t.cfg.s.spSapCfg.spId = atoi (lpValue);
                }
                else if (strcmp (lpName, "ssn") == 0)
                {
                    /*
                   *  IMPORTANT: override this value by Self SSN
                   */
                    aeMgmt.t.cfg.s.spSapCfg.ssn = atoi (lpValue);
                }
                else if (strcmp (lpName, "spTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spSapCfg.spTmr = atoi (lpValue);
                }
#ifdef SPT2
                else if (strcmp (lpName, "tIntTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spSapCfg.tIntTmr.val = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                       "TCAP LSAP tIntTmr = <%d>", 
                        aeMgmt.t.cfg.s.spSapCfg.tIntTmr.val);
                    if (aeMgmt.t.cfg.s.spSapCfg.tIntTmr.val)
                      aeMgmt.t.cfg.s.spSapCfg.tIntTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spSapCfg.tIntTmr.enb = false;
                }
#endif
              }
            }
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for TCAP", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiType == BP_AIN_SM_SUBTYPE_LSPCFG)
    {
      if (mbOverrideXml)
        aeMgmt.t.cfg.s.spSapCfg.ssn = self.meSelfSsn;
    }

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }


    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getStMngmt");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getSpMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for SCCP
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int 
INGwSmRepository::getSpMngmt (int aiType, SpMngmt &aeMgmt, int aiIndex)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSpMngmt <%d> with index <%d>", 
        aiType, aiIndex);

    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode *lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"sccp", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <sccp:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    cmMemset((U8 *)&aeMgmt, '\0', sizeof(SpMngmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;


    char *lpName = new char [100];
    char *lpValue = new char [100];

    lpNode = lpNode->getFirstChild();

    bool indexFound = false;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "nmbNws") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.nmbNws = atoi (lpValue); 
                }
                else if (strcmp (lpName, "nmbSaps") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.nmbSaps = atoi (lpValue); 
                }
                else if (strcmp (lpName, "nmbNSaps") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.nmbNSaps = atoi (lpValue); 
                }
                else if (strcmp (lpName, "nmbAsso") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.nmbAsso = atoi (lpValue); 
                }
                else if (strcmp (lpName, "nmbActns") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.nmbActns = atoi (lpValue); 
                }
                else if (strcmp (lpName, "nmbAddrs") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.nmbAddrs = atoi (lpValue); 
                }
                else if (strcmp (lpName, "nmbRtes") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.nmbRtes = atoi (lpValue); 
                }
                else if (strcmp (lpName, "nmbXUdCb") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.nmbXUdCb = atoi (lpValue); 
                }
                else if (strcmp (lpName, "mgmntOn") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.mngmntOn = atoi (lpValue); 
                }
                else if (strcmp (lpName, "sogThresh") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.sogThresh = atoi (lpValue); 
                }
                else if (strcmp (lpName, "ssnTimeRes") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.ssnTimeRes = atoi (lpValue); 
                }
                else if (strcmp (lpName, "AsmbTimeRes") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.AsmbTimeRes = atoi (lpValue); 
                }
                else if (strcmp (lpName, "recTimeRes") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.recTimeRes = atoi (lpValue); 
                }
                else if (strcmp (lpName, "maxRstLvl") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.maxRstLvl = atoi (lpValue); 
                }
                else if (strcmp (lpName, "maxRstSubLvl") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.maxRstSubLvl = atoi (lpValue); 
                }
                else if (strcmp (lpName, "atkDecTimeRes") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.atkDecTimeRes = atoi (lpValue); 
                }
                else if (strcmp (lpName, "nmbCon") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.nmbCon = atoi (lpValue); 
                }
#ifdef LSPV2_3
                else if (strcmp (lpName, "nmbConThresh") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.nmbConThresh = atoi (lpValue); 
                }
#endif
#ifdef SP_SLR_RANGE
                else if (strcmp (lpName, "slrLowRange") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.slrLowRange = atoi (lpValue); 
                }
#endif
                else if (strcmp (lpName, "conThresh") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.conThresh = atoi (lpValue); 
                }
                else if (strcmp (lpName, "queThresh") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.queThresh = atoi (lpValue); 
                }
                else if (strcmp (lpName, "itThresh") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.itThresh = atoi (lpValue); 
                }
#ifdef LSPV2_9
                else if (strcmp (lpName, "snriFeatEna") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.snriFeatEna = atoi (lpValue); 
                }
                else if (strcmp (lpName, "defSNRI") == 0)
                {
                    DOMNode *lpChild = lpNode->getFirstChild ();

                    int liCount = 0;

                    if (getSpDefSnri (lpChild, &(aeMgmt.t.cfg.s.spGen.defSNRI[0]), liCount))
                      aeMgmt.t.cfg.s.spGen.numSnri=liCount;
                    else
                      aeMgmt.t.cfg.s.spGen.numSnri=0;
                }
                else if (strcmp (lpName, "snriPos") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.snriPos = atoi (lpValue); 
                }
                else if (strcmp (lpName, "snriLen") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.snriLen = atoi (lpValue); 
                }
#endif
                else if (strcmp (lpName, "conTimeRes") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.conTimeRes = atoi (lpValue); 
                }
                else if (strcmp (lpName, "defGuaTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.defGuaTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spGen.defGuaTmr.val)
                      aeMgmt.t.cfg.s.spGen.defGuaTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spGen.defGuaTmr.enb = false;

                }
                else if (strcmp (lpName, "defRstEndTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.defRstEndTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spGen.defRstEndTmr.val)
                      aeMgmt.t.cfg.s.spGen.defRstEndTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spGen.defRstEndTmr.enb = false;

                }
#ifdef SNT2
                else if (strcmp (lpName, "tIntTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.tIntTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spGen.tIntTmr.val)
                      aeMgmt.t.cfg.s.spGen.tIntTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spGen.tIntTmr.enb = false;
                }
                else if (strcmp (lpName, "defStatusEnqTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spGen.defStatusEnqTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spGen.defStatusEnqTmr.val)
                      aeMgmt.t.cfg.s.spGen.defStatusEnqTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spGen.defStatusEnqTmr.enb = false;
                }
#endif
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_USPCFG)
              {
                if (strcmp (lpName, "sapId") == 0)
                {
                    aeMgmt.hdr.elmId.elmntInst1 = atoi (lpValue);
                }
                else if (strcmp (lpName, "nwId") == 0)
                {
                    aeMgmt.t.cfg.s.spSap.nwId = atoi (lpValue);
                }
                else if (strcmp (lpName, "nmbBpc") == 0)
                {
                    aeMgmt.t.cfg.s.spSap.nmbBpc = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spSap.nmbBpc)
                    {
                      DOMNode *lpChild = lpNode->getFirstChild ();

                      int liCount = 0;

                      if (getSpBpc (lpChild, &(aeMgmt.t.cfg.s.spSap.bpcList[0]), liCount))
                        aeMgmt.t.cfg.s.spSap.nmbBpc = liCount;
                      else
                        aeMgmt.t.cfg.s.spSap.nmbBpc = 0;
                    }
                }
                else if (strcmp (lpName, "nmbConPc") == 0)
                {
                    aeMgmt.t.cfg.s.spSap.nmbConPc = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spSap.nmbConPc)
                    {
                      DOMNode *lpChild = lpNode->getFirstChild ();

                      int liCount = 0;

                      if (getU32List (lpChild, &(aeMgmt.t.cfg.s.spSap.conPc[0]), liCount))
                        aeMgmt.t.cfg.s.spSap.nmbConPc = liCount;
                      else
                        aeMgmt.t.cfg.s.spSap.nmbConPc = 0;
                    }
                }
#ifdef LSPV2_5
                else if (strcmp (lpName, "msgInterceptEnabled") == 0)
                {
                    aeMgmt.t.cfg.s.spSap.msgInterceptEnabled = atoi (lpValue); 
                }
#endif
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_LSPCFG)
              {
                if (strcmp (lpName, "sapId") == 0)
                {
                    aeMgmt.hdr.elmId.elmntInst1 = atoi (lpValue);
                }
                else if (strcmp (lpName, "spId") == 0)
                {
                    aeMgmt.t.cfg.s.spNSap.spId = atoi (lpValue);
                }
                else if (strcmp (lpName, "nwId") == 0)
                {
                    aeMgmt.t.cfg.s.spNSap.nwId = atoi (lpValue);
                }
                else if (strcmp (lpName, "msgLen") == 0)
                {
                    aeMgmt.t.cfg.s.spNSap.msgLen = atoi (lpValue);
                }
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_NWKCFG)
              {
                if (strcmp (lpName, "nwId") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.nwId = atoi (lpValue);
                }
                else if (strcmp (lpName, "variant") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.variant = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                        "VARIANT = <%d>", aeMgmt.t.cfg.s.spNw.variant);
                }
                else if (strcmp (lpName, "pcLen") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.pcLen = atoi (lpValue);
                }
#ifdef LSPV2_6
                else if (strcmp (lpName, "spcBroadcastOn") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.spcBroadcastOn = atoi (lpValue);
                }
                else if (strcmp (lpName, "defaultPc") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defaultPc = atoi (lpValue);
                }
                else if (strcmp (lpName, "nmbSpcs") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.nmbSpcs = atoi (lpValue);

                    if (aeMgmt.t.cfg.s.spNw.nmbSpcs)
                    {
                      DOMNode *lpChild = lpNode->getFirstChild ();

                      int liCount = 0;

                      if (getSpSelfPc (lpChild, &(aeMgmt.t.cfg.s.spNw.selfPc[0]), liCount))
                        aeMgmt.t.cfg.s.spNw.nmbSpcs = liCount;
                      else
                        aeMgmt.t.cfg.s.spNw.nmbSpcs = 0;
                    }
                }
#else
                else if (strcmp (lpName, "selfPc") == 0)
                {
                    /*
                   * IMPORTANT : Override the value with self PC
                   */
                    aeMgmt.t.cfg.s.spNw.selfPc = atoi (lpValue);
                }
#endif
#ifdef LSPV2_8
                else if (strcmp (lpName, "numAsps") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.numAsps = atoi (lpValue);
		}
            else if (strcmp(lpName, "aspList") == 0)
                {
                      DOMNode *lpChild = lpNode->getFirstChild ();

                      int liCount = 0;
                      for (int i=0; (i < aeMgmt.t.cfg.s.spNw.numAsps) && lpNode; i++)
		      { 
                         getSpAspPcSsnList (lpChild->getFirstChild (), &(aeMgmt.t.cfg.s.spNw.aspList[0]), liCount);
                         lpChild = lpChild->getNextSibling ();
		      }
                }
#endif
                else if (strcmp (lpName, "subService") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.subService = atoi (lpValue);
                }
                else if (strcmp (lpName, "defHopCnt") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defHopCnt = atoi (lpValue);
                    logger.logMsg (ALWAYS_FLAG, 0, "getSpMngmt() defHopCnt<%d>",
                                   aeMgmt.t.cfg.s.spNw.defHopCnt);
                }
#ifdef SS7_JAPAN
                else if (strcmp (lpName, "jttMngmntOn") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.jttMngmntOn = atoi (lpValue);
                }
#endif
                else if (strcmp (lpName, "niInd") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.niInd = atoi (lpValue);
                }
                else if (strcmp (lpName, "sioPresImpPres") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.sioPrioImpPres = atoi (lpValue);

                    if (aeMgmt.t.cfg.s.spNw.sioPrioImpPres)
                    {
                      DOMNode *lpChild = lpNode->getFirstChild ();

                      int liCount = 0;

                      while (lpChild)
                      {
                        if (lpChild->getNodeType() == DOMNode::ELEMENT_NODE)
                        {
                            char *lpcChildName= XMLString::transcode (lpChild->getNodeName());
                            if (strcmp (lpcChildName, "sioPrioImp") == 0)
                            {
                              if (getAttrVal ((DOMElement*)lpNode, (char*)"value", lpName))
                              {
                                logger.logMsg (TRACE_FLAG, 0,
                                      "SIO Priority to Imp retrieved = <%s>", lpName);
                                aeMgmt.t.cfg.s.spNw.sioPrioImp[liCount] =
                                    atoi (lpName);
                                ++liCount;
                              }
                            }
                            XMLString::release (&lpcChildName);
                        }
                        lpChild = lpChild->getNextSibling();
                      }
                    }
                }
                else if (strcmp (lpName, "defSstTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defSstTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defSstTmr.val)
                      aeMgmt.t.cfg.s.spNw.defSstTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defSstTmr.enb = false;
              logger.logMsg (TRACE_FLAG, 0,
                  "defSstTmr.enb= <%d>, defSstTmr.val=<%d>", aeMgmt.t.cfg.s.spNw.defSstTmr.enb, aeMgmt.t.cfg.s.spNw.defSstTmr.val);
                }

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96 || SS7_BELL05)
                else if (strcmp (lpName, "defSrtTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defSrtTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defSrtTmr.val)
                      aeMgmt.t.cfg.s.spNw.defSrtTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defSrtTmr.enb = false;
                }
#endif /* (SS7_ANS88 || SS7_ANS92 || SS7_ANS96 || SS7_BELL05) */

                else if (strcmp (lpName, "defIgnTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defIgnTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defIgnTmr.val)
                      aeMgmt.t.cfg.s.spNw.defIgnTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defIgnTmr.enb = false;
                }
                else if (strcmp (lpName, "defCrdTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defCrdTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defCrdTmr.val)
                      aeMgmt.t.cfg.s.spNw.defCrdTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defCrdTmr.enb = false;
                }
                else if (strcmp (lpName, "defAsmbTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defAsmbTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defAsmbTmr.val)
                      aeMgmt.t.cfg.s.spNw.defAsmbTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defAsmbTmr.enb = false;
                }
                else if (strcmp (lpName, "defAttackTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defAttackTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defAttackTmr.val)
                      aeMgmt.t.cfg.s.spNw.defAttackTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defAttackTmr.enb = false;
                }
                else if (strcmp (lpName, "defDecayTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defDecayTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defDecayTmr.val)
                      aeMgmt.t.cfg.s.spNw.defDecayTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defDecayTmr.enb = false;
                }
                else if (strcmp (lpName, "defCongTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defCongTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defCongTmr.val)
                      aeMgmt.t.cfg.s.spNw.defCongTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defCongTmr.enb = false;
                }
                else if (strcmp (lpName, "defFrzTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defFrzTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defFrzTmr.val)
                      aeMgmt.t.cfg.s.spNw.defFrzTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defFrzTmr.enb = false;
                }
                else if (strcmp (lpName, "defConTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defConTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defConTmr.val)
                      aeMgmt.t.cfg.s.spNw.defConTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defConTmr.enb = false;
                }
                else if (strcmp (lpName, "defIasTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defIasTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defIasTmr.val)
                      aeMgmt.t.cfg.s.spNw.defIasTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defIasTmr.enb = false;
                }
                else if (strcmp (lpName, "defIarTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defIarTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defIarTmr.val)
                      aeMgmt.t.cfg.s.spNw.defIarTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defIarTmr.enb = false;
                }
                else if (strcmp (lpName, "defRelTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defRelTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defRelTmr.val)
                      aeMgmt.t.cfg.s.spNw.defRelTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defRelTmr.enb = false;
                }
                else if (strcmp (lpName, "defRepRelTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defRepRelTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defRepRelTmr.val)
                      aeMgmt.t.cfg.s.spNw.defRepRelTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defRepRelTmr.enb = false;
                }
                else if (strcmp (lpName, "defIntTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defIntTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defIntTmr.val)
                      aeMgmt.t.cfg.s.spNw.defIntTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defIntTmr.enb = false;
                }
                else if (strcmp (lpName, "defRstTmr") == 0)
                {
                    aeMgmt.t.cfg.s.spNw.defRstTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.spNw.defRstTmr.val)
                      aeMgmt.t.cfg.s.spNw.defRstTmr.enb = true;
                    else
                      aeMgmt.t.cfg.s.spNw.defRstTmr.enb = false;
                }
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_RTECFG)
              {
                if (strcmp (lpName, "swtch") == 0)
                {
                    // LSP_SW_ITU88   11                   
                    // LSP_SW_ITU92   12
                    // LSP_SW_ANS88   13
                    // LSP_SW_ANS92   14
                    // LSP_SW_ITU96   15
                    // LSP_SW_ANS96   16
                    // LSP_SW_BELL05  17 
                    // LSP_SW_GSM0806 18                    
                    // LSP_SW_ITU2001 19               
                    string l_protocol("");
                    if(0 != INGwIfrPrParamRepository::getInstance().
																			getValue("PROTO", l_protocol))
                    {
                      l_protocol = "ITU";  
                      aeMgmt.t.cfg.s.spRte.swtch = LSP_SW_ITU92;
                    }

                    if("NTT" == l_protocol )
                    {
                     aeMgmt.t.cfg.s.spRte.swtch =  LSP_SW_JAPAN;
                    }

                    else if("ANSI" == l_protocol )
                    {
                      aeMgmt.t.cfg.s.spRte.swtch = LSP_SW_ANS96;
                    }

                    else if("ITU" == l_protocol )
                    {
                      aeMgmt.t.cfg.s.spRte.swtch = LSP_SW_ITU92;
                    } 
                    logger.logMsg (TRACE_FLAG, 0,
                        "SWITCH = <%d>", aeMgmt.t.cfg.s.spRte.swtch);
                }
                else if (strcmp (lpName, "sapId") == 0)
                {
                    aeMgmt.t.cfg.s.spRte.nSapId = atoi (lpValue);
                }
                else if (strcmp (lpName, "dpc") == 0)
                {
                    aeMgmt.t.cfg.s.spRte.dpc = atoi (lpValue);
                }
                else if (strcmp (lpName, "status") == 0)
                {
                    aeMgmt.t.cfg.s.spRte.status = atoi (lpValue);
                }
#if (SS7_ANS96 || SS7_BELL05)
//lsp.x::SpRteCfg
                else if (strcmp (lpName, "replicatedMode") == 0)
                {
                    aeMgmt.t.cfg.s.spRte.replicatedMode = atoi (lpValue);
                }
#endif
                else if (strcmp (lpName, "nmbBpc") == 0)
                {
                    aeMgmt.t.cfg.s.spRte.nmbBpc = atoi (lpValue);
                    int liCount = 0;

                    if (aeMgmt.t.cfg.s.spRte.nmbBpc &&
                        getSpBpc (lpNode->getFirstChild(), 
                            &(aeMgmt.t.cfg.s.spRte.bpcList[0]), liCount))
                    {
                      aeMgmt.t.cfg.s.spRte.nmbBpc = liCount;
                    }
                    else
                    {
                      aeMgmt.t.cfg.s.spRte.nmbBpc = 0;
                    }

                    logger.logMsg (TRACE_FLAG, 0,
                        "Number of BPC in SCCP RTECFG = <%d>", 
                        aeMgmt.t.cfg.s.spRte.nmbBpc);
                }
                else if (strcmp (lpName, "nmbSsns") == 0)
                {
                    aeMgmt.t.cfg.s.spRte.nmbSsns = atoi (lpValue);
                }
				else if (strcmp (lpName, "ssn") == 0)
				{
                    int liCount = 0;
					DOMNode *tmpNode;
                  logger.logMsg (TRACE_FLAG, 0,
                      "Number of SSN in SCCP RTECFG = <%d>", 
                      aeMgmt.t.cfg.s.spRte.nmbSsns);

                    if (aeMgmt.t.cfg.s.spRte.nmbSsns)
                    {
                      tmpNode = lpNode->getFirstChild();
                    int i = 0;
                    while(tmpNode)
                      {
                         if (tmpNode->getNodeType() == DOMNode::ELEMENT_NODE)
                         {
                            getSpSsnCfg (tmpNode->getFirstChild(),
                            &(aeMgmt.t.cfg.s.spRte.ssnList[i++]), liCount);
                         }
						 tmpNode = tmpNode->getNextSibling();
                      }
                    aeMgmt.t.cfg.s.spRte.nmbSsns = i - 1;
                    } 
                }
                else if (strcmp (lpName, "flag") == 0)
                {
                    aeMgmt.t.cfg.s.spRte.flag = atoi (lpValue);
                }
#ifdef LSPV2_1
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96 || SS7_BELL05)
                else if (strcmp (lpName, "slsMask") == 0)
                {
                    aeMgmt.t.cfg.s.spRte.slsMask = atoi (lpValue);
                }
#endif
#endif
#ifdef LSPV2_7
                else if (strcmp (lpName, "preferredOpc") == 0)
                {
                    aeMgmt.t.cfg.s.spRte.preferredOpc = atoi (lpValue);
                }
#endif
#ifdef LSPV2_9
                else if (strcmp (lpName, "snri") == 0)
                {
                    DOMNode *lpChild = lpNode->getFirstChild ();

                    int liCount = 0;

                    if (getSpDefSnri (lpChild, &(aeMgmt.t.cfg.s.spRte.snri[0]), liCount))
                      aeMgmt.t.cfg.s.spRte.numSnri=liCount;
                    else
                      aeMgmt.t.cfg.s.spRte.numSnri=0;
                }
#endif 
              }
            }
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for SCCP", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>", 
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiType == BP_AIN_SM_SUBTYPE_NWKCFG)
    {
#ifndef LSPV2_6  
    if (mbOverrideXml)
        aeMgmt.t.cfg.s.spNw.selfPc = self.miSelfPC;
#endif
    }

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getSpMngmt");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getItMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int 
INGwSmRepository::getItMgmt  (int aiType, ItMgmt &aeMgmt, int aiIndex)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getItMgmt");

    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode *lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"m3ua", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <m3ua:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    cmMemset((U8 *)&aeMgmt, '\0', sizeof(ItMgmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;


    lpNode = lpNode->getFirstChild();

    char *lpName = new char [100];
    char *lpValue = new char [100];

    bool indexFound = false;
    int liMonitorResource = 0;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "nodeType") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.nodeType = atoi (lpValue);    
                }
                else if (strcmp (lpName, "dpcLrnFlag") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.dpcLrnFlag = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxNmbNSap") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbNSap = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxNmbSctSap") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbSctSap = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxNmbNwk") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbNwk = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxNmbRtEnt") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbRtEnt = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxNmbDpcEnt") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbDpcEnt = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxNmbPs") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbPs = atoi (lpValue);    
                }

#if (defined (ITASP) && defined (OG_RTE_ON_LPS_STA) )
                else if (strcmp (lpName, "maxNmbLps") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbLps = atoi (lpValue);    
                }
#endif

                else if (strcmp (lpName, "maxNmbPsp") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbPsp = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxNmbMsg") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbMsg = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxNmbRndRbnLs") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbRndRbnLs = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxNmbSlsLs") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbSlsLs = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxNmbSls") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbSls = atoi (lpValue);    
                }
#if (defined(LITV19) && defined(ITZV_AUDIT_ENABLED))
                else if (strcmp (lpName, "audSctSapCbBlkSize") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.audSctSapCbBlkSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "audAssocCbBlkSize") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.audAssocCbBlkSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "audDpcCbBlkSize") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.audDpcCbBlkSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "audPspCbBlkSize") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.audPspCbBlkSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "audPsCbBlkSize") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.audPsCbBlkSize = atoi (lpValue);    
                }
#endif
                else if (strcmp (lpName, "drkmSupp") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.drkmSupp = atoi (lpValue);    
                }
                else if (strcmp (lpName, "drstSupp") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.drstSupp = atoi (lpValue);    
                }
                else if (strcmp (lpName, "qSize") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.qSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "congLevel1") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.congLevel1 = atoi (lpValue);    
                }
                else if (strcmp (lpName, "congLevel2") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.congLevel2 = atoi (lpValue);    
                }
                else if (strcmp (lpName, "congLevel3") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.congLevel3 = atoi (lpValue);    
                }
#ifdef LITV15
                else if (strcmp (lpName, "multiMsgPrior") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.multiMsgPrior = atoi (lpValue);    
                }
#endif
                else if (strcmp (lpName, "tmrRestart") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrRestart.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrRestart.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrRestart.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrRestart.enb = false;
                }
                else if (strcmp (lpName, "tmrMtp3Sta") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrMtp3Sta.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrMtp3Sta.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrMtp3Sta.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrMtp3Sta.enb = false;
                }
                else if (strcmp (lpName, "tmrAsPend") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAsPend.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAsPend.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAsPend.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAsPend.enb = false;

                }
                else if (strcmp (lpName, "tmrHeartbeat") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrHeartbeat.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrHeartbeat.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrHeartbeat.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrHeartbeat.enb = false;

                }
                else if (strcmp (lpName, "tmrAspUp1") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAspUp1.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAspUp1.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAspUp1.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAspUp1.enb = false;

                }
                else if (strcmp (lpName, "tmrAspUp2") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAspUp2.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAspUp2.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAspUp2.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAspUp2.enb = false;

                }
                else if (strcmp (lpName, "nmbAspUp1") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.nmbAspUp1 = atoi (lpValue);    
                }
                else if (strcmp (lpName, "tmrAspDn") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAspDn.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAspDn.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAspDn.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAspDn.enb = false;

                }
                else if (strcmp (lpName, "tmrAspM") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAspM.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAspM.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAspM.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAspM.enb = false;

                }
                else if (strcmp (lpName, "tmrDaud") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrDaud.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrDaud.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrDaud.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrDaud.enb = false;

                }
                else if (strcmp (lpName, "tmrDrkm") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrDrkm.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrDrkm.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrDrkm.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrDrkm.enb = false;

                }
                else if (strcmp (lpName, "maxNmbRkTry") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.maxNmbRkTry = atoi (lpValue);    
                }
                else if (strcmp (lpName, "tmrDunaSettle") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrDunaSettle.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrDunaSettle.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrDunaSettle.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrDunaSettle.enb = false;

                }
                else if (strcmp (lpName, "tmrSeqCntrl") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrSeqCntrl.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrSeqCntrl.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrSeqCntrl.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrSeqCntrl.enb = false;

                }
#if (defined(LITV19) && defined(ITZV_AUDIT_ENABLED))
                else if (strcmp (lpName, "tmrAudSctSapCbBrk") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudSctSapCbBrk.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudSctSapCbBrk.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudSctSapCbBrk.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudSctSapCbBrk.enb = false;

                }
                else if (strcmp (lpName, "tmrAudSctSapCb") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudSctSapCb.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudSctSapCb.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudSctSapCb.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudSctSapCb.enb = false;

                }
                else if (strcmp (lpName, "tmrAudAssocCbBrk") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudAssocCbBrk.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudAssocCbBrk.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudAssocCbBrk.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudAssocCbBrk.enb = false;

                }
                else if (strcmp (lpName, "tmrAudAssocCb") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudAssocCb.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudAssocCb.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudAssocCb.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudAssocCb.enb = false;

                }
                else if (strcmp (lpName, "tmrAudDpcCbBrk") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDpcCbBrk.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDpcCbBrk.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDpcCbBrk.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDpcCbBrk.enb = false;

                }
                else if (strcmp (lpName, "tmrAudDpcCb") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDpcCb.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDpcCb.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDpcCb.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDpcCb.enb = false;

                }
                else if (strcmp (lpName, "tmrAudPspCbBrk") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPspCbBrk.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPspCbBrk.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPspCbBrk.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPspCbBrk.enb = false;

                }
                else if (strcmp (lpName, "tmrAudPspCb") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPspCb.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPspCb.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPspCb.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPspCb.enb = false;

                }
                else if (strcmp (lpName, "tmrAudPsCbBrk") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPsCbBrk.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPsCbBrk.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPsCbBrk.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPsCbBrk.enb = false;

                }
                else if (strcmp (lpName, "tmrAudPsCb") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPsCb.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPsCb.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPsCb.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudPsCb.enb = false;

                }
                else if (strcmp (lpName, "tmrAudDelSctSapCb") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDelSctSapCb.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDelSctSapCb.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDelSctSapCb.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDelSctSapCb.enb = false;

                }
                else if (strcmp (lpName, "tmrSeqCntrl") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDelPspCb.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDelPspCb.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDelPspCb.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDelPspCb.enb = false;

                }
                else if (strcmp (lpName, "tmrAudDelPsCb") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDelPsCb.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDelPsCb.val)
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDelPsCb.enb = true;
                    else
                      aeMgmt.t.cfg.s.genCfg.tmr.tmrAudDelPsCb.enb = false;

                }
#endif
                else if (strcmp (lpName, "timeRes") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.timeRes = atoi (lpValue);    
                }
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_USPCFG)
              {
                if (strcmp (lpName, "nwkId") == 0)
                {
                    aeMgmt.t.cfg.s.nSapCfg.nwkId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "sapId") == 0)
                {
                    aeMgmt.t.cfg.s.nSapCfg.sapId = atoi (lpValue);    
                    miItSpId = aeMgmt.t.cfg.s.nSapCfg.sapId;
                }

#ifdef ITSG
                else if (strcmp (lpName, "tmrPrim") == 0)
                {
                    aeMgmt.t.cfg.s.nSapCfg.tmrPrim.val = atoi (lpValue);    
                    if (aeMgmt.t.cfg.s.nSapCfg.tmrPrim.val)
                      aeMgmt.t.cfg.s.nSapCfg.tmrPrim.enb = true;
                    else
                      aeMgmt.t.cfg.s.nSapCfg.tmrPrim.enb = false;
                }
                else if (strcmp (lpName, "procId") == 0)
                {
                    aeMgmt.t.cfg.s.nSapCfg.procId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "ent") == 0)
                {
                    aeMgmt.t.cfg.s.nSapCfg.ent = atoi (lpValue);    
                }
                else if (strcmp (lpName, "inst") == 0)
                {
                    aeMgmt.t.cfg.s.nSapCfg.inst = atoi (lpValue);    
                }
                else if (strcmp (lpName, "mtp3SapId") == 0)
                {
                    aeMgmt.t.cfg.s.nSapCfg.mtp3SapId = atoi (lpValue);    
                }
#endif 

                else if (strcmp (lpName, "suType") == 0)
                {
                    aeMgmt.t.cfg.s.nSapCfg.suType = atoi (lpValue);    
                }
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_LSPCFG)
              {
                if (strcmp (lpName, "suId") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.suId = atoi (lpValue);    
                    miItSuId = aeMgmt.t.cfg.s.sctSapCfg.suId;
                }
                else if (strcmp (lpName, "spId") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.spId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "srcPort") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.srcPort = atoi (lpValue);    
                }
                else if (strcmp (lpName, "tmrPrim") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.tmrPrim.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.sctSapCfg.tmrPrim.val)
                      aeMgmt.t.cfg.s.sctSapCfg.tmrPrim.enb = true;
                    else
                      aeMgmt.t.cfg.s.sctSapCfg.tmrPrim.enb = false;
                }
                else if (strcmp (lpName, "tmrSta") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.tmrSta.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.sctSapCfg.tmrSta.val)
                      aeMgmt.t.cfg.s.sctSapCfg.tmrSta.enb = true;
                    else
                      aeMgmt.t.cfg.s.sctSapCfg.tmrSta.enb = false;
                }
                else if (strcmp (lpName, "srcAddrLst") == 0)
                {
                    int liCount = 0;
                    getItAddrLst (lpNode->getFirstChild(), 
                        &(aeMgmt.t.cfg.s.sctSapCfg.srcAddrLst.nAddr[0]), liCount);
                    aeMgmt.t.cfg.s.sctSapCfg.srcAddrLst.nmb = liCount;
                }
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_NWKCFG)
              {
                if (strcmp (lpName, "nwkId") == 0)
                {
                    aeMgmt.t.cfg.s.nwkCfg.nwkId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "nwkApp") == 0)
                {
                    getItNwkApp (lpNode->getFirstChild(), 
                        &(aeMgmt.t.cfg.s.nwkCfg.nwkApp[0]));
                }
                else if (strcmp (lpName, "ssf") == 0)
                {
                    aeMgmt.t.cfg.s.nwkCfg.ssf = atoi (lpValue);    
                }
                else if (strcmp (lpName, "dpcLen") == 0)
                {
                    aeMgmt.t.cfg.s.nwkCfg.dpcLen = atoi (lpValue);    
                }
                else if (strcmp (lpName, "slsLen") == 0)
                {
                    aeMgmt.t.cfg.s.nwkCfg.slsLen = atoi (lpValue);    
                }
                else if (strcmp (lpName, "suSwtch") == 0)
                {
                    aeMgmt.t.cfg.s.nwkCfg.suSwtch = atoi (lpValue);    
                }
                else if (strcmp (lpName, "su2Swtch") == 0)
                {
                    aeMgmt.t.cfg.s.nwkCfg.su2Swtch = atoi (lpValue);    
                }
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_RTECFG)
              {
                if (strcmp (lpName, "nwkId") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.nwkId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "rtType") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtType = atoi (lpValue);    
                }
                else if (strcmp (lpName, "psId") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.psId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "nSapIdPres") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.nSapIdPres = atoi (lpValue);    
                }
                else if (strcmp (lpName, "noStatus") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.noStatus = atoi (lpValue);    
                }
                else if (strcmp (lpName, "nSapId") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.nSapId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "dpcMask") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.dpcMask = atoi (lpValue);    
                }
                else if (strcmp (lpName, "dpc") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.dpc = atoi (lpValue);    
                }
                else if (strcmp (lpName, "opcMask") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.opcMask = atoi (lpValue);    
                }
                else if (strcmp (lpName, "opc") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.opc = atoi (lpValue);    
                }
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96 || SS7_BELL05)
                else if (strcmp (lpName, "slsMask") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.slsMask = atoi (lpValue);    
                }
#endif
                else if (strcmp (lpName, "sls") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.sls = atoi (lpValue);    
                }
                else if (strcmp (lpName, "sioMask") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.sioMask = atoi (lpValue);    
                }
                else if (strcmp (lpName, "sio") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.sio = atoi (lpValue);    
                }
                else if (strcmp (lpName, "includeCic") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.includeCic = atoi (lpValue);    
                }
                else if (strcmp (lpName, "cicStart") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.cicStart = atoi (lpValue);    
                }
                else if (strcmp (lpName, "cicEnd") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.cicEnd = atoi (lpValue);    
                }
                else if (strcmp (lpName, "includeSsn") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.includeSsn = atoi (lpValue);    
                }
                else if (strcmp (lpName, "ssn") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.ssn = atoi (lpValue);    
                }
                else if (strcmp (lpName, "includeTrid") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.includeTrid = atoi (lpValue);    
                }
                else if (strcmp (lpName, "tridStart") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.tridStart = atoi (lpValue);    
                }
                else if (strcmp (lpName, "tridEnd") == 0)
                {
                    aeMgmt.t.cfg.s.rteCfg.rtFilter.tridEnd = atoi (lpValue);    
                }
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_PSPCFG)
              {
                if (strcmp (lpName, "pspId") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.pspId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "monitorResource") == 0)
                {
                    liMonitorResource = atoi (lpValue);    
                }
                else if (strcmp (lpName, "pspType") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.pspType = atoi (lpValue);    
                }
                else if (strcmp (lpName, "ipspMode") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.ipspMode = atoi (lpValue);    
                }
                else if (strcmp (lpName, "dynRegRkallwd") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.dynRegRkallwd = atoi (lpValue);    
                }
                else if (strcmp (lpName, "dfltLshareMode") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.dfltLshareMode = atoi (lpValue);    
                }
                else if (strcmp (lpName, "nwkAppIncl") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.nwkAppIncl = atoi (lpValue);    
                }
                else if (strcmp (lpName, "rxTxAspId") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.rxTxAspId = atoi (lpValue);    
                }
#ifdef LITV11
                else if (strcmp (lpName, "selfAspId") == 0)
                {
                    DOMNode *lpChild = lpNode->getFirstChild ();

                    int liCount = 0;
                    getItSelfAspId (lpChild, &(aeMgmt.t.cfg.s.pspCfg.selfAspId[0]), liCount);
                }
#else
                else if (strcmp (lpName, "selfAspId") == 0)
                {
                    /* This will be overridden later */
                    aeMgmt.t.cfg.s.pspCfg.selfAspId = atoi (lpValue);    
                }
#endif
                else if (strcmp (lpName, "nwkId") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.nwkId = atoi (lpValue);    
                }

#ifdef ITASP
                else if (strcmp (lpName, "cfgForAllLps") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.cfgForAllLps = atoi (lpValue);    
                }
#ifdef SGVIEW
                else if (strcmp (lpName, "sgId") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.sgId = atoi (lpValue);    
                }
#endif
#endif
#ifdef LITV10
                else if (strcmp (lpName, "rcIsMand") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.rcIsMand = atoi (lpValue);    
                }
                else if (strcmp (lpName, "modRegRkallwd") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.modRegRkallwd = atoi (lpValue);    
                }
#endif
#ifdef LITV13
                else if (strcmp (lpName, "includeRC") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.includeRC = atoi (lpValue);    
                }
#endif
#ifdef LITV14
#if (defined(ITASP) && defined(SGVIEW))
                else if (strcmp (lpName, "sgMid") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.sgMid = atoi (lpValue);    
                }
                else if (strcmp (lpName, "dfltSlsDistMode") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.dfltSlsDistMode = atoi (lpValue);    
                }
#endif
#endif
/* TODO
                else if (strcmp (lpName, "suId") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.assocCfg.suId = atoi (lpValue);    
                }
*/
                else if (strcmp (lpName, "dstPort") == 0)
                {
                    /* This will be overridden later */
                    aeMgmt.t.cfg.s.pspCfg.assocCfg.dstPort = atoi (lpValue);    
                }
                else if (strcmp (lpName, "dstAddrLst") == 0)
                {
                    int liCount = 0;

                    /* This will be overridden later */
                    if (getItAddrLst (lpNode->getFirstChild(),
                            &aeMgmt.t.cfg.s.pspCfg.assocCfg.dstAddrLst.nAddr[0],
                            liCount) == BP_AIN_SM_OK)
                    {
                      aeMgmt.t.cfg.s.pspCfg.assocCfg.dstAddrLst.nmb = liCount;

                      /* set the primary destination address */
                      if (liCount > 0)
                      {
                        aeMgmt.t.cfg.s.pspCfg.assocCfg.priDstAddr =
                            aeMgmt.t.cfg.s.pspCfg.assocCfg.dstAddrLst.nAddr[0];
                      }

                      logger.logMsg (TRACE_FLAG, 0,
                            "Total dest addr retrieved = <%d>",
                            aeMgmt.t.cfg.s.pspCfg.assocCfg.dstAddrLst.nmb);
                    }
                }
                else if (strcmp (lpName, "locOutStrms") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.assocCfg.locOutStrms = atoi (lpValue);    
                }
#ifdef SCT3
                else if (strcmp (lpName, "tos") == 0)
                {
                    aeMgmt.t.cfg.s.pspCfg.assocCfg.tos = atoi (lpValue);    
                }
#endif
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_PSCFG)
              {
                if (strcmp (lpName, "nwkId") == 0)
                {
                    aeMgmt.t.cfg.s.psCfg.nwkId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "psId") == 0)
                {
                    aeMgmt.t.cfg.s.psCfg.psId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "mode") == 0)
                {
                    aeMgmt.t.cfg.s.psCfg.mode = atoi (lpValue);    
                }
                else if (strcmp (lpName, "routCtx") == 0)
                {
                    aeMgmt.t.cfg.s.psCfg.routCtx = atoi (lpValue);    
                }
                else if (strcmp (lpName, "loadShareMode") == 0)
                {
                    aeMgmt.t.cfg.s.psCfg.loadShareMode = atoi (lpValue);    
                }
                else if (strcmp (lpName, "reqAvail") == 0)
                {
                    aeMgmt.t.cfg.s.psCfg.reqAvail = atoi (lpValue);    
                }
                else if (strcmp (lpName, "nmbActPspReqd") == 0)
                {
                    aeMgmt.t.cfg.s.psCfg.nmbActPspReqd = atoi (lpValue);    
                }
                else if (strcmp (lpName, "lclFlag") == 0)
                {
                    aeMgmt.t.cfg.s.psCfg.lclFlag = atoi (lpValue);    
                }
                else if (strcmp (lpName, "nmbPsp") == 0)
                {
                    aeMgmt.t.cfg.s.psCfg.nmbPsp = atoi (lpValue);    

                    int liCount = 0;
                    if (aeMgmt.t.cfg.s.psCfg.nmbPsp && getItPsp (lpNode->getFirstChild(), 
                            &(aeMgmt.t.cfg.s.psCfg.psp[0]), liCount))
                    {
                      aeMgmt.t.cfg.s.psCfg.nmbPsp = liCount;
                    }
                    else
                    {
                      aeMgmt.t.cfg.s.psCfg.nmbPsp = 0;
                    }
                    logger.logMsg (TRACE_FLAG, 0,
                        "Number of PSPs in this PS are = <%d>", 
                        aeMgmt.t.cfg.s.psCfg.nmbPsp);
                }
                else if (strcmp (lpName, "pspEpLst") == 0)
                {
                    int liCount = 0;
                    getItPspEpList (lpNode->getFirstChild(), 
                        &(aeMgmt.t.cfg.s.psCfg.pspEpLst[0]), liCount);
                }
                else if (strcmp (lpName, "slsDistMode") == 0)
                {
                    aeMgmt.t.cfg.s.psCfg.slsDistMode = atoi (lpValue);    
                }
              }
            }
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for M3UA", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;


    /*
   * check if the PSP is to be managed via EMS
   */
    if (aiType == BP_AIN_SM_SUBTYPE_PSPCFG && indexFound)
    {
      if (liMonitorResource == 1)
      {
        INGwSmPspState *lpState = getPspState (aeMgmt.t.cfg.s.pspCfg.pspId);

        if (lpState == 0)
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Psp <%d> could not be found in PSP State Table",
                aeMgmt.t.cfg.s.pspCfg.pspId);
        }
        else
        {
            lpState->monitor = 1;

            logger.logMsg (ERROR_FLAG, 0,
                "PSP <%d> has to be monitored by the Stack Manager",
                aeMgmt.t.cfg.s.pspCfg.pspId);
        }
      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
              "PSP <%d> is not to be monitored by the Stack Manager",
              aeMgmt.t.cfg.s.pspCfg.pspId);
      }
    }


    /*
   * for the PSPCFG we need to fill in the addresses of peer and self
   */

    if (aiType == BP_AIN_SM_SUBTYPE_PSPCFG && 
        mbOverrideXml && indexFound)
    {
      map<INGwSmPeerId, AddressVector*>::iterator leMapIter;

      //first locate the address vector for the peer Id
      leMapIter = mePeerAddress.find (aeMgmt.t.cfg.s.pspCfg.pspId);

      if (leMapIter == mePeerAddress.end())
      {
        logger.logMsg (ERROR_FLAG, 0,
              "No address found for the Peer PSP <%d>", 
              aeMgmt.t.cfg.s.pspCfg.pspId);
      }
      else
      {
        AddressVector *lpVec = leMapIter->second;

        if (lpVec == 0 || lpVec->empty())
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Empty address found for the Peer PSP <%d>", 
                aeMgmt.t.cfg.s.pspCfg.pspId);
        }
        else
        {
            AddressVector::iterator leVecIter = lpVec->begin();
            INGwSmAddress *lpAdd = *(leVecIter);

            aeMgmt.t.cfg.s.pspCfg.assocCfg.priDstAddr.type = CM_NETADDR_IPV4;
            aeMgmt.t.cfg.s.pspCfg.assocCfg.priDstAddr.u.ipv4NetAddr = lpAdd->address;
            aeMgmt.t.cfg.s.pspCfg.assocCfg.dstPort = lpAdd->port;

            logger.logMsg (ERROR_FLAG, 0,
                "Peer PSP <%d>, priAddr = <%u.%u.%u.%u>, priPort = <%d>", 
                aeMgmt.t.cfg.s.pspCfg.pspId, 
                INGwSmGetHiByte(GetHiWord(lpAdd->address)),
                INGwSmGetLoByte(GetHiWord(lpAdd->address)),
                INGwSmGetHiByte(GetLoWord(lpAdd->address)),
                INGwSmGetLoByte(GetLoWord(lpAdd->address)),
                aeMgmt.t.cfg.s.pspCfg.assocCfg.dstPort);

            aeMgmt.t.cfg.s.pspCfg.assocCfg.dstAddrLst.nmb = lpVec->size();

            for (int i = 0; i < aeMgmt.t.cfg.s.pspCfg.assocCfg.dstAddrLst.nmb; i++)
            { 
              lpAdd = *(leVecIter);
              aeMgmt.t.cfg.s.pspCfg.assocCfg.dstAddrLst.nAddr[i].type = CM_NETADDR_IPV4;
              aeMgmt.t.cfg.s.pspCfg.assocCfg.dstAddrLst.nAddr[i].u.ipv4NetAddr = lpAdd->address;

              logger.logMsg (ERROR_FLAG, 0,
                    "Peer PSP <%d>, dstAddr = <%u.%u.%u.%u>, dstPort = <%d>", 
                    aeMgmt.t.cfg.s.pspCfg.pspId, 
                    INGwSmGetHiByte(GetHiWord(lpAdd->address)),
                    INGwSmGetLoByte(GetHiWord(lpAdd->address)),
                    INGwSmGetHiByte(GetLoWord(lpAdd->address)),
                    INGwSmGetLoByte(GetLoWord(lpAdd->address)),
                    aeMgmt.t.cfg.s.pspCfg.assocCfg.dstPort);

              leVecIter++;
            }

            aeMgmt.t.cfg.s.pspCfg.assocCfg.dstAddrLst.nmb = self.meSelfAddress.size();

            int i = 0;
            for (leVecIter = self.meSelfAddress.begin();
                leVecIter != self.meSelfAddress.end();
                leVecIter++)
            { 
              if (i >= aeMgmt.t.cfg.s.pspCfg.assocCfg.dstAddrLst.nmb)
                break;
              lpAdd = *(leVecIter);
              aeMgmt.t.cfg.s.pspCfg.assocCfg.dstAddrLst.nAddr[i].type = 
                CM_NETADDR_IPV4;
              aeMgmt.t.cfg.s.pspCfg.assocCfg.dstAddrLst.nAddr[i].u.ipv4NetAddr = 
                lpAdd->address;

              i++;

              logger.logMsg (ERROR_FLAG, 0,
                    "Peer PSP <%d>, srcAddr = <%u.%u.%u.%u>", 
                    aeMgmt.t.cfg.s.pspCfg.pspId, 
                    INGwSmGetHiByte(GetHiWord(lpAdd->address)),
                    INGwSmGetLoByte(GetHiWord(lpAdd->address)),
                    INGwSmGetHiByte(GetLoWord(lpAdd->address)),
                    INGwSmGetLoByte(GetLoWord(lpAdd->address)));

            }

        }
      }
#ifndef LITV11
      if (mbOverrideXml)
        aeMgmt.t.cfg.s.pspCfg.selfAspId = self.miSelfAspId;
#endif
    }
    else if (aiType == BP_AIN_SM_SUBTYPE_LSPCFG)
    {

      /*
       * Pg 3-62 of M3UA SD states that srcPort is used in SG to listen for
       * incoming association. In an ASP, this parameter is not used and may 
       * be set to 0.
       */
      aeMgmt.t.cfg.s.sctSapCfg.srcPort = self.selfPort;
    }


    if (aiIndex != -1 && indexFound == false)
      return BP_AIN_SM_INDEX_OVER;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getItMgmt");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getSbMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for SCTP
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int 
INGwSmRepository::getSbMgmt  (int aiType, SbMgmt &aeMgmt, int aiIndex)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSbMgmt");

    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode *lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"sctp", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <sctp:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    cmMemset((U8 *)&aeMgmt, '\0', sizeof(SbMgmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

#ifdef ST_LMINT3
    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;
#endif /* ST_LMINT3 */

    char *lpName = new char [100];
    char *lpValue = new char [100];

    lpNode = lpNode->getFirstChild();

    bool indexFound = false;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "serviceType") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.serviceType = atoi (lpValue);
                }
#ifdef SB_IPV6_SUPPORTED
                else if (strcmp (lpName, "ipv6SrvcReqdFlg") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.ipv6SrvcReqdFlg = atoi (lpValue);
                }
#endif
                else if (strcmp (lpName, "maxNmbSctSaps") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbSctSaps = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxNmbTSaps") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbTSaps = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxNmbEndp") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbEndp = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxNmbAssoc") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbAssoc = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxNmbDstAddr") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbDstAddr = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxNmbSrcAddr") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbSrcAddr = atoi (lpValue);
                }
#ifdef SB_SATELLITE
                else if (strcmp (lpName, "maxNmbPathProfs") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbPathProfs = atoi (lpValue);
                }
#endif
                else if (strcmp (lpName, "maxNmbTxChunks") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbTxChunks = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxNmbRxChunks") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbRxChunks = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxNmbInStrms") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbInStrms = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxNmbOutStrms") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxNmbOutStrms = atoi (lpValue);
                }
                else if (strcmp (lpName, "initARwnd") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.initARwnd = atoi (lpValue);
                }
                else if (strcmp (lpName, "mtuInitial") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.mtuInitial = atoi (lpValue);
                }
                else if (strcmp (lpName, "mtuMinInitial") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.mtuMinInitial = atoi (lpValue);
                }
                else if (strcmp (lpName, "mtuMaxInitial") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.mtuMaxInitial = atoi (lpValue);
                }
                else if (strcmp (lpName, "performMtu") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.performMtu = atoi (lpValue);
                }
                else if (strcmp (lpName, "timeRes") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.timeRes = atoi (lpValue);
                }
                else if (strcmp (lpName, "useHstName") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.useHstName = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxInitReTx") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.reConfig.maxInitReTx = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxAssocReTx") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.reConfig.maxAssocReTx = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxPathReTx") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.reConfig.maxPathReTx = atoi (lpValue);
                }
                else if (strcmp (lpName, "altAcceptFlg") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.reConfig.altAcceptFlg = atoi (lpValue);
                }
                else if (strcmp (lpName, "keyTm") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.reConfig.keyTm = atoi (lpValue);
                }
                else if (strcmp (lpName, "alpha") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.reConfig.alpha = atoi (lpValue);
                }
                else if (strcmp (lpName, "beta") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.reConfig.beta = atoi (lpValue);
                }
#ifdef SB_ECN
                else if (strcmp (lpName, "ecnFlg") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.reConfig.ecnFlg = atoi (lpValue);
                }
#endif
                else if (strcmp (lpName, "useHstName") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.useHstName = atoi (lpValue);
                }
                else if (strcmp (lpName, "hostname") == 0)
                {
                    if (strcmp (lpValue, "0") != 0)
                      strcpy ((char*)(&(aeMgmt.t.cfg.s.genCfg.hostname[0])), lpValue);
                    else
                      aeMgmt.t.cfg.s.genCfg.hostname[0] = 0;
                }
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_USPCFG)
              {
                if (strcmp (lpName, "swtch") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.swtch = atoi (lpValue);
                }
                else if (strcmp (lpName, "spId") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.spId = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxAckDelayTm") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.maxAckDelayTm = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxAckDelayDg") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.maxAckDelayDg = atoi (lpValue);
                }
                else if (strcmp (lpName, "rtoInitial") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.rtoInitial = atoi (lpValue);
                }
                else if (strcmp (lpName, "rtoMin") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.rtoMin = atoi (lpValue);
                }
                else if (strcmp (lpName, "rtoMax") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.rtoMax = atoi (lpValue);
                }
                else if (strcmp (lpName, "freezeTm") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.freezeTm = atoi (lpValue);
                }
#ifdef LSB4
                else if (strcmp (lpName, "bundleTm") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.bundleTm = atoi (lpValue);
                }
#endif
                else if (strcmp (lpName, "cookieLife") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.cookieLife = atoi (lpValue);
                }
                else if (strcmp (lpName, "intervalTm") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.intervalTm = atoi (lpValue);
                }
#ifdef LSB8
                else if (strcmp (lpName, "maxBurst") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.maxBurst = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxHbBurst") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.maxHbBurst = atoi (lpValue);
                }
                else if (strcmp (lpName, "t5SdownGrdTm") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.t5SdownGrdTm = atoi (lpValue);
                }
#endif
                else if (strcmp (lpName, "handleInitFlg") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.handleInitFlg = atoi (lpValue);
                }
                else if (strcmp (lpName, "negAbrtFlg") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.negAbrtFlg = atoi (lpValue);
                }
                else if (strcmp (lpName, "hBeatEnable") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.hBeatEnable = atoi (lpValue);
                }
                else if (strcmp (lpName, "flcUpThr") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.flcUpThr = atoi (lpValue);
                }
                else if (strcmp (lpName, "flcLowThr") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.flcLowThr = atoi (lpValue);
                }
#ifdef SB_CHECKSUM_DUAL
                else if (strcmp (lpName, "checksumType") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.checksumType = atoi (lpValue);
                }
#endif
#ifdef SB_ETSI
                else if (strcmp (lpName, "maxDataSize") == 0)
                {
                    aeMgmt.t.cfg.s.sctSapCfg.reConfig.maxDataSize = atoi (lpValue);
                }
#endif
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_LSPCFG)
              {
                if (strcmp (lpName, "swtch") == 0)
                {
                    aeMgmt.t.cfg.s.tSapCfg.swtch = atoi (lpValue);
                }
                else if (strcmp (lpName, "spId") == 0)
                {
                    aeMgmt.t.cfg.s.tSapCfg.reConfig.spId = atoi (lpValue);
                }
                else if (strcmp (lpName, "suId") == 0)
                {
                    aeMgmt.t.cfg.s.tSapCfg.suId = atoi (lpValue);
                }
                else if (strcmp (lpName, "srcNAddrLst") == 0)
                { 
                    int liCount = 0;

                    /* This will be overridden later */
                    if (getItAddrLst (lpNode->getFirstChild(),
                            &aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nAddr[0],
                            liCount) == BP_AIN_SM_OK)
                    { 
                      aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nmb = liCount;

                      logger.logMsg (TRACE_FLAG, 0,
                            "Total source addr retrieved = <%d>",
                            aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nmb);
                    }
                }
                else if (strcmp (lpName, "tIntTmr") == 0)
                {
                    aeMgmt.t.cfg.s.tSapCfg.reConfig.tIntTmr = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxBndRetry") == 0)
                {
                    aeMgmt.t.cfg.s.tSapCfg.reConfig.maxBndRetry = atoi (lpValue);
                }
                else if (strcmp (lpName, "dnsAddr.type") == 0)
                {
                    aeMgmt.t.cfg.s.tSapCfg.reConfig.sbDnsCfg.dnsAddr.type = atoi (lpValue);
                }
                else if (strcmp (lpName, "dnsAddr.port") == 0)
                {
                    aeMgmt.t.cfg.s.tSapCfg.reConfig.sbDnsCfg.dnsAddr.u.ipv4TptAddr.port = atoi (lpValue);
                }
                else if (strcmp (lpName, "dnsAddr.address") == 0)
                {
                    aeMgmt.t.cfg.s.tSapCfg.reConfig.sbDnsCfg.dnsAddr.u.ipv4TptAddr.address = atoi(lpValue);
                }
                else if (strcmp (lpName, "useDnsLib") == 0)
                {
                    aeMgmt.t.cfg.s.tSapCfg.reConfig.sbDnsCfg.useDnsLib = atoi (lpValue);
                }
                else if (strcmp (lpName, "dnsTmOut") == 0)
                {
                    aeMgmt.t.cfg.s.tSapCfg.reConfig.sbDnsCfg.dnsTmOut = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxRtxCnt") == 0)
                {
                    aeMgmt.t.cfg.s.tSapCfg.reConfig.sbDnsCfg.maxRtxCnt = atoi (lpValue);
                }
              }
            }
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for SCTP", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    /*
   * In SCTP LSAP config, the ip address of CCM need to be set
   */
    if (aiType == BP_AIN_SM_SUBTYPE_LSPCFG && indexFound)
    {
      if (mbOverrideXml)
      {
        aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nmb = self.meSelfAddress.size();      

        int i = 0;
        AddressVector::iterator leVecIter;
        INGwSmAddress *lpAdd = 0;
        for (leVecIter = self.meSelfAddress.begin();
              leVecIter != self.meSelfAddress.end();
              leVecIter++)
        {
            if (i >= aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nmb)
              break;
            lpAdd = *(leVecIter);
            aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nAddr[i].type =
              CM_NETADDR_IPV4;
            aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nAddr[i].u.ipv4NetAddr =
              lpAdd->address;

            i++;

            logger.logMsg (ERROR_FLAG, 0,
                "SCTP LSAP srcAddr = <%u.%u.%u.%u>",
                INGwSmGetHiByte(GetHiWord(lpAdd->address)),
                INGwSmGetLoByte(GetHiWord(lpAdd->address)),
                INGwSmGetHiByte(GetLoWord(lpAdd->address)),
                INGwSmGetLoByte(GetLoWord(lpAdd->address)));
        }
      }

      /*
       * The SCTP LSAP expects the last ip to be "0" for some reason
       */
      aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nmb++;
      aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nAddr
        [aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nmb - 1].type = CM_NETADDR_IPV4;
      aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nAddr
        [aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nmb - 1].u.ipv4NetAddr = 0;

      logger.logMsg (ERROR_FLAG, 0,
            "aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nmb = <%d>",
            aeMgmt.t.cfg.s.tSapCfg.srcNAddrLst.nmb);

    }

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }


    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getSbMgmt");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getHiMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for TUCL
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int 
INGwSmRepository::getHiMngmt (int aiType, HiMngmt &aeMgmt, int aiIndex)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getHiMngmt");

    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode *lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"tucl", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <tucl:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    cmMemset((U8 *)&aeMgmt, '\0', sizeof(HiMngmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

#ifdef ST_LMINT3
    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;
#endif /* ST_LMINT3 */


    char *lpName = new char [100];
    char *lpValue = new char [100];

    lpNode = lpNode->getFirstChild();

    bool indexFound = false;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "numSaps") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.numSaps = atoi (lpValue);    /* number of saps */
                }
                else if (strcmp (lpName, "numCons") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.numCons = atoi (lpValue); 
                }
#ifdef HI_REL_1_2
                else if (strcmp (lpName, "numFdsPerSet") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.numFdsPerSet = atoi (lpValue); 
                }
                else if (strcmp (lpName, "numFdBins") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.numFdBins = atoi (lpValue); 
                }
#endif
#ifdef HI006_12
                else if (strcmp (lpName, "selTimeout") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.selTimeout = atoi (lpValue); 
                }
#ifdef HI_REL_1_3
                else if (strcmp (lpName, "numRawMsgsToRead") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.numRawMsgsToRead = atoi (lpValue); 
                }
#endif
                else if (strcmp (lpName, "numUdpMsgsToRead") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.numUdpMsgsToRead = atoi (lpValue); 
                }
                else if (strcmp (lpName, "numClToAccept") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.numClToAccept = atoi (lpValue); 
                }
#endif
#ifdef HI_LKSCTP
                else if (strcmp (lpName, "numAssocBins") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.numAssocBins = atoi (lpValue); 
                }
                else if (strcmp (lpName, "maxInitReTx") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.maxInitReTx = atoi (lpValue); 
                }
                else if (strcmp (lpName, "maxAssocReTx") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.maxAssocReTx = atoi (lpValue); 
                }
                else if (strcmp (lpName, "maxPathReTx") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.maxPathReTx = atoi (lpValue); 
                }
#endif
                else if (strcmp (lpName, "permTsk") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.permTsk = atoi (lpValue); 
                }
                else if (strcmp (lpName, "schdTmrVal") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.schdTmrVal = atoi (lpValue); 
                }
                else if (strcmp (lpName, "poolStrtThr") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.poolStrtThr = atoi (lpValue); 
                }
                else if (strcmp (lpName, "poolDropThr") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.poolDropThr = atoi (lpValue); 
                }
                else if (strcmp (lpName, "poolStopThr") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.poolStopThr = atoi (lpValue); 
                }
                else if (strcmp (lpName, "timeRes") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.timeRes = atoi (lpValue); 
                }
#ifdef HI_SPECIFY_GENSOCK_ADDR
                else if (strcmp (lpName, "ipv4GenSockAddr") == 0)
                {
                    getAttrVal ((DOMElement*)lpNode, (char*)"port", lpName);
                    aeMgmt.t.cfg.s.hiGen.ipv4GenSockAddr.port = atoi (lpName);
                    char *ip, *ch3;
                    int num = 0;
                    unsigned int address = 0;

                    ip = strtok_r (lpValue, ".", &ch3);
                    if (ip == 0)
                      num = 0;
                    else
                      num = atoi (ip);
                    address = num << 24;

                    ip = strtok_r (0, ".", &ch3);
                    if (ip == 0)
                      num = 0;
                    else
                      num = atoi (ip);
                    address += num << 16;

                    ip = strtok_r (0, ".", &ch3);
                    if (ip == 0)
                      num = 0;
                    else
                      num = atoi (ip);
                    address += num << 8;

                    ip = strtok_r (0, ".", &ch3);
                    if (ip == 0)
                      num = 0;
                    else
                      num = atoi (ip);
                    address += num;
                    aeMgmt.t.cfg.s.hiGen.ipv4GenSockAddr.ipv4NetAddr= address;
                }
                else if (strcmp (lpName, "ipv6GenSockAddr") == 0)
                {
                    getAttrVal ((DOMElement*)lpNode, (char*)"port", lpName);
                    aeMgmt.t.cfg.s.hiGen.ipv6GenSockAddr.port = atoi (lpName);
                    osStrcpy(lpValue,
                        aeMgmt.t.cfg.s.hiGen.ipv6GenSockAddr.ipv6NetAdd);
                }
#endif
#ifdef HI_TLS
                else if (strcmp (lpName, "initOpenSSL") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.initOpenSSL = atoi (lpValue); 
                }
                else if (strcmp (lpName, "numContexts") == 0)
                {
                    aeMgmt.t.cfg.s.hiGen.numContexts = atoi (lpValue); 
                }
                else if (strcmp (lpName, "prng") == 0)
                {
                    getAttrVal ((DOMElement*)lpNode, (char*)"option", lpName);
                    aeMgmt.t.cfg.s.hiGen.prng.option = atoi (lpName);
                    if (aeMgmt.t.cfg.s.hiGen.prng.option == LHI_OSSL_PRNG_FILE)
                    {
                      getHiPrngFile (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.hiGen.prng.file[0]),
                            liCount); 
                    }
                    else
                    {
                      getHiPrngSeed (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.hiGen.prng.seed[0]),
                            liCount); 
                    }
                }
#endif
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_USPCFG)
              {
                if (strcmp (lpName, "flcEnb") == 0)
                {
                    aeMgmt.t.cfg.s.hiSap.flcEnb = atoi (lpValue); 
                }
                else if (strcmp (lpName, "spId") == 0)
                {
                    aeMgmt.t.cfg.s.hiSap.spId = atoi (lpValue); 
                }
                else if (strcmp (lpName, "txqCongStrtLim") == 0)
                {
                    aeMgmt.t.cfg.s.hiSap.txqCongStrtLim = atoi (lpValue); 
                }
                else if (strcmp (lpName, "txqCongDropLim") == 0)
                {
                    aeMgmt.t.cfg.s.hiSap.txqCongDropLim = atoi (lpValue); 
                }
                else if (strcmp (lpName, "txqCongStopLim") == 0)
                {
                    aeMgmt.t.cfg.s.hiSap.txqCongStopLim = atoi (lpValue); 
                }
                else if (strcmp (lpName, "numBins") == 0)
                {
                    aeMgmt.t.cfg.s.hiSap.numBins = atoi (lpValue); 
                }
              }
            }
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for TUCL", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }


    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getHiMngmt");
    return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   getRyMngmt()
*
*     Desc:  This function returns Configuration Management structure for Relay
*
*     Notes: None
*
*     File:  INGwSmRepository.C
*
*******************************************************************************/
int
INGwSmRepository::getRyMngmt (int aiType, RyMngmt &aeMgmt, int aiIndex, int* apiCount)
{
  logger.logMsg (TRACE_FLAG, 0,
      "Entering INGwSmRepository::getRyMngmt");

  string lstrOpName;

  if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
  {
    return BP_AIN_SM_FAIL;
  }

  DOMNode *lpNode = getCfgNode ((char*)mpcCfgElement,
      (char*)"relay", (char*)lstrOpName.c_str());

  if (lpNode == 0)
  {
    logger.logMsg (ERROR_FLAG, 0,
        "Unable to find DOMElement for <relay:%s>", lstrOpName.c_str());
    return BP_AIN_SM_FAIL;
  }

  Header &hdr = aeMgmt.hdr;

  cmMemset((U8 *)&aeMgmt, '\0', sizeof(StMngmt));
  hdr.msgType          = 0;
  hdr.msgLen           = 0;
  hdr.entId.ent        = 0;
  hdr.entId.inst       = 0;
  hdr.elmId.elmnt      = 0;
  hdr.elmId.elmntInst1 = 0;
  hdr.elmId.elmntInst2 = 0;
  hdr.elmId.elmntInst3 = 0;
  hdr.seqNmb           = 0;
  hdr.version          = 0;

  hdr.response.prior      = BP_AIN_SM_PRIOR;
  hdr.response.route      = BP_AIN_SM_ROUTE;
  hdr.response.mem.region = BP_AIN_SM_REGION;
  hdr.response.mem.pool   = BP_AIN_SM_POOL;
  hdr.response.selector   = BP_AIN_SM_COUPLING;


  char *lpName = new char [100];
  char *lpValue = new char [100];

  lpNode = lpNode->getFirstChild();

  bool indexFound = false;

  while (lpNode)
  {
    if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
    {
      char *nodeName;
      nodeName = XMLString::transcode (lpNode->getNodeName());
      if (strcmp (nodeName, mpcCfgOidElement) == 0)
      {
        if (aiIndex != -1)
          indexFound = true;

        if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
        {
          logger.logMsg (TRACE_FLAG, 0,
              "Oid retrieved = <%s>, <%s>", lpName, lpValue);

          if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
          {
            if (strcmp (lpName, "nmbChan") == 0)
            {
              aeMgmt.t.cfg.s.ryGenCfg.nmbChan= atoi (lpValue);    /* number of channels */
            }
            else if (strcmp (lpName, "tmrRes") == 0)
            {
              aeMgmt.t.cfg.s.ryGenCfg.tmrRes= atoi (lpValue);    /* timer resolution */
            }
            else if (strcmp (lpName, "usta") == 0)
            {
              aeMgmt.t.cfg.s.ryGenCfg.usta= atoi (lpValue);    /* unsolicited status */
            }
          }
          else if (aiType == BP_AIN_SM_SUBTYPE_LIS_CHANCFG)
          {
            if (strcmp (lpName, "id") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.id = atoi (lpValue);
            }
            else if (strcmp (lpName, "type") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.type = atoi (lpValue);
            }
            else if (strcmp (lpName, "msInd") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.msInd = atoi (lpValue);
            }
            else if (strcmp (lpName, "propErr") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.propErr = atoi (lpValue);
            }
            else if (strcmp (lpName, "low") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.low = atoi (lpValue);
            }
            else if (strcmp (lpName, "high") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.high = atoi (lpValue);
            }
            else if (strcmp (lpName, "memSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.memSize = atoi (lpValue);
            }
            else if (strcmp (lpName, "nmbScanQ") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.nmbScanQ = atoi (lpValue);
            }
            else if (strcmp (lpName, "flags") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.flags = atoi (lpValue);
            }
            else if (strcmp (lpName, "congThrsh") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.congThrsh = atoi (lpValue);
            }
            else if (strcmp (lpName, "dropThrsh") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.dropThrsh = atoi (lpValue);
            }
            else if (strcmp (lpName, "contThrsh") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.contThrsh = atoi (lpValue);
            }
            else if (strcmp (lpName, "poolSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.poolSize = atoi (lpValue);
            }
            else if (strcmp (lpName, "kaTxTmr") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.kaTxTmr.val = atoi (lpValue);
              if (aeMgmt.t.cfg.s.ryChanCfg.kaTxTmr.val)
                aeMgmt.t.cfg.s.ryChanCfg.kaTxTmr.enb = true;
              else
                aeMgmt.t.cfg.s.ryChanCfg.kaTxTmr.enb = false;
            }
            else if (strcmp (lpName, "kaRxTmr") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.kaRxTmr.val = atoi (lpValue);
              if (aeMgmt.t.cfg.s.ryChanCfg.kaRxTmr.val)
                aeMgmt.t.cfg.s.ryChanCfg.kaRxTmr.enb = true;
              else
                aeMgmt.t.cfg.s.ryChanCfg.kaRxTmr.enb = false;
            }
            else if (strcmp (lpName, "btTmr") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.btTmr.val = atoi (lpValue);
              if (aeMgmt.t.cfg.s.ryChanCfg.btTmr.val)
                aeMgmt.t.cfg.s.ryChanCfg.btTmr.enb = true;
              else
                aeMgmt.t.cfg.s.ryChanCfg.btTmr.enb = false;
            }
#ifdef RY_ENBS5SHM
            else if (strcmp (lpName, "key") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.key = atoi (lpValue);
            }
#endif /* RY_ENBS5SHM */
            else if (strcmp (lpName, "shMemBase") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.shMemBase = atoi (lpValue);
            }
            else if (strcmp (lpName, "shMemSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.shMemSize = atoi (lpValue);
            }
            else if (strcmp (lpName, "shCcbOff") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.shCcbOff = atoi (lpValue);
            }
            else if (strcmp (lpName, "txTblSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.txTblSize = atoi (lpValue);
            }
            else if (strcmp (lpName, "rxTblSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.rxTblSize = atoi (lpValue);
            }
#if (RY_ENBUDPSOCK || RY_ENBTCPSOCK)
            else if (strcmp (lpName, "listenPortNo") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.listenPortNo = atoi (lpValue);
            }
#ifdef LRYV2
            else if (strcmp (lpName, "selfHostName") == 0)
            {
              strncpy(aeMgmt.t.cfg.s.ryChanCfg.selfHostName,lpValue,strlen(lpValue));
            }
#endif /* LRY2 */
            else if (strcmp (lpName, "transmittoHostName") == 0)
            {
              strncpy(aeMgmt.t.cfg.s.ryChanCfg.transmittoHostName,lpValue,strlen(lpValue));
            }
            else if (strcmp (lpName, "transmittoPortNo") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.transmittoPortNo = atoi (lpValue);
            }
            else if (strcmp (lpName, "targetProcId") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.targetProcId = atoi (lpValue);
            }
#ifdef LRY1
            else if (strcmp (lpName, "sockParam") == 0)	
            {
              aeMgmt.t.cfg.s.ryChanCfg.sockParam= atoi (lpValue);
            }
#endif /* LRY1 */

#endif /* RY_ENBUDPSOCK || RY_ENBTCPSOCK */

#if (RY_ENBNTUK || RY_ENBNTKH)
            else if (strcmp (lpName, "deviceName") == 0)
            {
              strncpy(aeMgmt.t.cfg.s.ryChanCfg.deviceName,lpValue,strlen(lpValue));
            }
#endif /* (RY_ENBNTUK || RY_ENBNTKH) */

#ifdef RY_ENBNTKH
            else if (strcmp (lpName, "boardId") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.boardId = lpValue;
            }
#endif /* RY_ENBNTKH */
            else if (strcmp (lpName, "region") == 0)
            {
              //aeMgmt.t.cfg.s.ryChanCfg.region = atoi(lpValue);
              aeMgmt.t.cfg.s.ryChanCfg.region = BP_AIN_SM_REGION;
            }
            else if (strcmp (lpName, "pool") == 0)
            {
              //aeMgmt.t.cfg.s.ryChanCfg.pool = atoi(lpValue);
              aeMgmt.t.cfg.s.ryChanCfg.pool = BP_AIN_SM_POOL;
            }
          }
          else if (aiType == BP_AIN_SM_SUBTYPE_SRV_CHANCFG)
          {
            if (strcmp (lpName, "id") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.id = atoi (lpValue);
            }
            else if (strcmp (lpName, "type") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.type = atoi (lpValue);
            }
            else if (strcmp (lpName, "msInd") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.msInd = atoi (lpValue);
            }
            else if (strcmp (lpName, "propErr") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.propErr = atoi (lpValue);
            }
            else if (strcmp (lpName, "low") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.low = atoi (lpValue);
            }
            else if (strcmp (lpName, "high") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.high = atoi (lpValue);
            }
            else if (strcmp (lpName, "memSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.memSize = atoi (lpValue);
            }
            else if (strcmp (lpName, "nmbScanQ") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.nmbScanQ = atoi (lpValue);
            }
            else if (strcmp (lpName, "flags") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.flags = atoi (lpValue);
            }
            else if (strcmp (lpName, "congThrsh") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.congThrsh = atoi (lpValue);
            }
            else if (strcmp (lpName, "dropThrsh") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.dropThrsh = atoi (lpValue);
            }
            else if (strcmp (lpName, "contThrsh") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.contThrsh = atoi (lpValue);
            }
            else if (strcmp (lpName, "poolSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.poolSize = atoi (lpValue);
            }
            else if (strcmp (lpName, "kaTxTmr") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.kaTxTmr.val = atoi (lpValue);
              if (aeMgmt.t.cfg.s.ryChanCfg.kaTxTmr.val)
                aeMgmt.t.cfg.s.ryChanCfg.kaTxTmr.enb = true;
              else
                aeMgmt.t.cfg.s.ryChanCfg.kaTxTmr.enb = false;
            }
            else if (strcmp (lpName, "kaRxTmr") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.kaRxTmr.val = atoi (lpValue);
              if (aeMgmt.t.cfg.s.ryChanCfg.kaRxTmr.val)
                aeMgmt.t.cfg.s.ryChanCfg.kaRxTmr.enb = true;
              else
                aeMgmt.t.cfg.s.ryChanCfg.kaRxTmr.enb = false;
            }
            else if (strcmp (lpName, "btTmr") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.btTmr.val = atoi (lpValue);
              if (aeMgmt.t.cfg.s.ryChanCfg.btTmr.val)
                aeMgmt.t.cfg.s.ryChanCfg.btTmr.enb = true;
              else
                aeMgmt.t.cfg.s.ryChanCfg.btTmr.enb = false;
            }
#ifdef RY_ENBS5SHM
            else if (strcmp (lpName, "key") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.key = atoi (lpValue);
            }
#endif /* RY_ENBS5SHM */
            else if (strcmp (lpName, "shMemBase") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.shMemBase = atoi (lpValue);
            }
            else if (strcmp (lpName, "shMemSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.shMemSize = atoi (lpValue);
            }
            else if (strcmp (lpName, "shCcbOff") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.shCcbOff = atoi (lpValue);
            }
            else if (strcmp (lpName, "txTblSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.txTblSize = atoi (lpValue);
            }
            else if (strcmp (lpName, "rxTblSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.rxTblSize = atoi (lpValue);
            }
#if (RY_ENBUDPSOCK || RY_ENBTCPSOCK)
            else if (strcmp (lpName, "listenPortNo") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.listenPortNo = atoi (lpValue);
            }
#ifdef LRYV2
            else if (strcmp (lpName, "selfHostName") == 0)
            {
              strncpy(aeMgmt.t.cfg.s.ryChanCfg.selfHostName,lpValue,strlen(lpValue));
            }
#endif /* LRY2 */
            else if (strcmp (lpName, "transmittoHostName") == 0)
            {
              strncpy(aeMgmt.t.cfg.s.ryChanCfg.transmittoHostName,lpValue,strlen(lpValue));
            }
            else if (strcmp (lpName, "transmittoPortNo") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.transmittoPortNo = atoi (lpValue);
            }
            else if (strcmp (lpName, "targetProcId") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.targetProcId = atoi (lpValue);
            }
#ifdef LRY1
            else if (strcmp (lpName, "sockParam") == 0)	//needed correction
            {
              aeMgmt.t.cfg.s.ryChanCfg.sockParam= atoi (lpValue);
            }
#endif /* LRY1 */

#endif /* RY_ENBUDPSOCK || RY_ENBTCPSOCK */

#if (RY_ENBNTUK || RY_ENBNTKH)
            else if (strcmp (lpName, "deviceName") == 0)
            {
              strncpy(aeMgmt.t.cfg.s.ryChanCfg.deviceName,lpValue,strlen(lpValue));
            }
#endif /* (RY_ENBNTUK || RY_ENBNTKH) */

#ifdef RY_ENBNTKH
            else if (strcmp (lpName, "boardId") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.boardId = lpValue;
            }
#endif /* RY_ENBNTKH */
            else if (strcmp (lpName, "region") == 0)
            {
              //aeMgmt.t.cfg.s.ryChanCfg.region = atoi(lpValue);
              aeMgmt.t.cfg.s.ryChanCfg.region = BP_AIN_SM_REGION;
            }
            else if (strcmp (lpName, "pool") == 0)
            {
              //aeMgmt.t.cfg.s.ryChanCfg.pool = atoi(lpValue);
              aeMgmt.t.cfg.s.ryChanCfg.pool = BP_AIN_SM_POOL;
            }
          }
          else if (aiType == BP_AIN_SM_SUBTYPE_CLI_CHANCFG)
          {
            if (strcmp (lpName, "id") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.id = atoi (lpValue);
            }
            else if (strcmp (lpName, "type") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.type = atoi (lpValue);
            }
            else if (strcmp (lpName, "msInd") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.msInd = atoi (lpValue);
            }
            else if (strcmp (lpName, "propErr") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.propErr = atoi (lpValue);
            }
            else if (strcmp (lpName, "low") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.low = atoi (lpValue);
            }
            else if (strcmp (lpName, "high") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.high = atoi (lpValue);
            }
            else if (strcmp (lpName, "memSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.memSize = atoi (lpValue);
            }
            else if (strcmp (lpName, "nmbScanQ") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.nmbScanQ = atoi (lpValue);
            }
            else if (strcmp (lpName, "flags") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.flags = atoi (lpValue);
            }
            else if (strcmp (lpName, "congThrsh") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.congThrsh = atoi (lpValue);
            }
            else if (strcmp (lpName, "dropThrsh") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.dropThrsh = atoi (lpValue);
            }
            else if (strcmp (lpName, "contThrsh") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.contThrsh = atoi (lpValue);
            }
            else if (strcmp (lpName, "poolSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.poolSize = atoi (lpValue);
            }
            else if (strcmp (lpName, "kaTxTmr") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.kaTxTmr.val = atoi (lpValue);
              if (aeMgmt.t.cfg.s.ryChanCfg.kaTxTmr.val)
                aeMgmt.t.cfg.s.ryChanCfg.kaTxTmr.enb = true;
              else
                aeMgmt.t.cfg.s.ryChanCfg.kaTxTmr.enb = false;
            }
            else if (strcmp (lpName, "kaRxTmr") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.kaRxTmr.val = atoi (lpValue);
              if (aeMgmt.t.cfg.s.ryChanCfg.kaRxTmr.val)
                aeMgmt.t.cfg.s.ryChanCfg.kaRxTmr.enb = true;
              else
                aeMgmt.t.cfg.s.ryChanCfg.kaRxTmr.enb = false;
            }
            else if (strcmp (lpName, "btTmr") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.btTmr.val = atoi (lpValue);
              if (aeMgmt.t.cfg.s.ryChanCfg.btTmr.val)
                aeMgmt.t.cfg.s.ryChanCfg.btTmr.enb = true;
              else
                aeMgmt.t.cfg.s.ryChanCfg.btTmr.enb = false;
            }
#ifdef RY_ENBS5SHM
            else if (strcmp (lpName, "key") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.key = atoi (lpValue);
            }
#endif /* RY_ENBS5SHM */
            else if (strcmp (lpName, "shMemBase") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.shMemBase = atoi (lpValue);
            }
            else if (strcmp (lpName, "shMemSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.shMemSize = atoi (lpValue);
            }
            else if (strcmp (lpName, "shCcbOff") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.shCcbOff = atoi (lpValue);
            }
            else if (strcmp (lpName, "txTblSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.txTblSize = atoi (lpValue);
            }
            else if (strcmp (lpName, "rxTblSize") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.rxTblSize = atoi (lpValue);
            }
#if (RY_ENBUDPSOCK || RY_ENBTCPSOCK)
            else if (strcmp (lpName, "listenPortNo") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.listenPortNo = atoi (lpValue);
            }
#ifdef LRYV2
            else if (strcmp (lpName, "selfHostName") == 0)
            {
              strncpy(aeMgmt.t.cfg.s.ryChanCfg.selfHostName,lpValue,strlen(lpValue));
            }
#endif /* LRY2 */
            else if (strcmp (lpName, "transmittoHostName") == 0)
            {
              strncpy(aeMgmt.t.cfg.s.ryChanCfg.transmittoHostName,lpValue,strlen(lpValue));
            }
            else if (strcmp (lpName, "transmittoPortNo") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.transmittoPortNo = atoi (lpValue);
            }
            else if (strcmp (lpName, "targetProcId") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.targetProcId = atoi (lpValue);
            }
#ifdef LRY1
            else if (strcmp (lpName, "sockParam") == 0)	//needed correction
            {
              aeMgmt.t.cfg.s.ryChanCfg.sockParam= atoi (lpValue);
            }
#endif /* LRY1 */

#endif /* RY_ENBUDPSOCK || RY_ENBTCPSOCK */

#if (RY_ENBNTUK || RY_ENBNTKH)
            else if (strcmp (lpName, "deviceName") == 0)
            {
              strncpy(aeMgmt.t.cfg.s.ryChanCfg.deviceName,lpValue,strlen(lpValue));
            }
#endif /* (RY_ENBNTUK || RY_ENBNTKH) */

#ifdef RY_ENBNTKH
            else if (strcmp (lpName, "boardId") == 0)
            {
              aeMgmt.t.cfg.s.ryChanCfg.boardId = lpValue;
            }
#endif /* RY_ENBNTKH */
            else if (strcmp (lpName, "region") == 0)
            {
              //aeMgmt.t.cfg.s.ryChanCfg.region = atoi(lpValue);
              aeMgmt.t.cfg.s.ryChanCfg.region = BP_AIN_SM_REGION;
            }
            else if (strcmp (lpName, "pool") == 0)
            {
              //aeMgmt.t.cfg.s.ryChanCfg.pool = atoi(lpValue);
              aeMgmt.t.cfg.s.ryChanCfg.pool = BP_AIN_SM_POOL;
            }
          }
        }
      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
            "Unknown Element <%s> Encountered", nodeName);
      }

      XMLString::release (&nodeName);
      lpNode = lpNode->getNextSibling();
    }
    else
    {
      lpNode = lpNode->getNextSibling();
    }
  }

  delete [] lpName;
  delete [] lpValue;


  logger.logMsg (TRACE_FLAG, 0,
      "Leaving INGwSmRepository::getRyMngmt");
  return BP_AIN_SM_OK;
}

//#endif


/******************************************************************************
 *
 *     Fun:   getSnMgmt()
 * 
 *     Desc:  This function returns Configuration Management structure for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getSnMgmt (int aiType, SnMngmt &aeMgmt, int aiIndex, int* apiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSnMgmt");

    int result = BP_AIN_SM_OK;
    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode* lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"mtp3", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <mtp3:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    cmMemset((U8 *)&aeMgmt, '\0', sizeof(SnMngmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;


    lpNode = lpNode->getFirstChild();

    char *lpName = new char [100];
    char *lpValue = new char [100];

    bool indexFound = false;
    int liMonitorResource = 0;

    INGwSmLinkInfo *lpLinkInfo = 0;
    if (aiType == BP_AIN_SM_SUBTYPE_LSPCFG)
    {
      lpLinkInfo = new INGwSmLinkInfo;
      lpLinkInfo->sapId = 0;
      lpLinkInfo->opc = 0;
      lpLinkInfo->dpc = 0;
      lpLinkInfo->channelNum = 0;
      lpLinkInfo->linkSetId = 0;
      lpLinkInfo->devId = 0;
    }

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "typeOfSP") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.typeSP = atoi (lpValue);    
                }
#if (defined(LSNV4) || defined(SNZN_AUDIT_ENABLED))
                else if (strcmp (lpName, "audLnkBlkSize") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.audLnkBlkSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "audLnkSetBlkSize") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.audLnkSetBlkSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "audRteBlkSize") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.audRteBlkSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "audLnkBrkTmr") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.audLnkBrkTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.snGen.audLnkBrkTmr.val)
                      aeMgmt.t.cfg.s.snGen.audLnkBrkTmr.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.snGen.audLnkBrkTmr.enb = FALSE;
                }
                else if (strcmp (lpName, "audLnkSetBrkTmr") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.audLnkSetBrkTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.snGen.audLnkSetBrkTmr.val)
                      aeMgmt.t.cfg.s.snGen.audLnkSetBrkTmr.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.snGen.audLnkSetBrkTmr.enb = FALSE;
                }
                else if (strcmp (lpName, "audRteBrkTmr") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.audRteBrkTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.snGen.audRteBrkTmr.val)
                      aeMgmt.t.cfg.s.snGen.audRteBrkTmr.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.snGen.audRteBrkTmr.enb = FALSE;
                }
                else if (strcmp (lpName, "audLnkTmr") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.audLnkTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.snGen.audLnkTmr.val)
                      aeMgmt.t.cfg.s.snGen.audLnkTmr.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.snGen.audLnkTmr.enb = FALSE;
                }
                else if (strcmp (lpName, "audLnkSetTmr") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.audLnkSetTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.snGen.audLnkSetTmr.val)
                      aeMgmt.t.cfg.s.snGen.audLnkSetTmr.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.snGen.audLnkSetTmr.enb = FALSE;
                }
                else if (strcmp (lpName, "audRteTmr") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.audRteTmr.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.snGen.audRteTmr.val)
                      aeMgmt.t.cfg.s.snGen.audRteTmr.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.snGen.audRteTmr.enb = FALSE;
                }
#endif
                else if (strcmp (lpName, "pointCode1") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.spCode1 = atoi (lpValue);    
                    if (mbOverrideXml)
                      aeMgmt.t.cfg.s.snGen.spCode1 = self.miSelfPC;
                }
                else if (strcmp (lpName, "pointCode2") == 0)
                {
                    //aeMgmt.t.cfg.s.snGen.spCode2 = atoi (lpValue);    
                }
                else if (strcmp (lpName, "ssfValidation") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.ssfValid = atoi (lpValue);    
                }
                else if (strcmp (lpName, "restartingProcedure") == 0)
                {
                    //For NTT nw there is no MTP3 restart procedure
                    string l_protocol("");
                    if(0 != INGwIfrPrParamRepository::getInstance().
																			getValue("PROTO", l_protocol))
                    {

                      l_protocol = "ITU";   
                      aeMgmt.t.cfg.s.snGen. rstReq = atoi (lpValue);    
                    }
                  
                  else{
                      if(l_protocol == "NTT"){
                      aeMgmt.t.cfg.s.snGen. rstReq = LSN_NO_RST; 
                      }
                      else{
                        aeMgmt.t.cfg.s.snGen. rstReq = atoi (lpValue);    
                      }
                  }
                }
                else if (strcmp (lpName, "transferProcedure") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.tfrReq = atoi (lpValue);    
                }
                else if (strcmp (lpName, "nmbDLSAP") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.nmbDLSap = atoi (lpValue);
                    miNumberOfMTP3Dlsaps = aeMgmt.t.cfg.s.snGen.nmbDLSap;
                }
                else if (strcmp (lpName, "nmbNSAP") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.nmbNSap = atoi (lpValue);    
                }
                else if (strcmp (lpName, "nmbRoutes") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.nmbRouts = atoi (lpValue);    
                }
                else if (strcmp (lpName, "nmbLnkSets") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.nmbLnkSets = atoi (lpValue);    
                }
                else if (strcmp (lpName, "nmbRouteInstances") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.nmbRteInst = atoi (lpValue);    
                }
                else if (strcmp (lpName, "cbTimerResolution") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.cbTimeRes = atoi (lpValue);    
                }
                else if (strcmp (lpName, "spTimerResolution") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.spTimeRes = atoi (lpValue);    
                }
                else if (strcmp (lpName, "rteTimerResolution") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.rteTimeRes = atoi (lpValue);    
                }
                else if (strcmp (lpName, "extCmbndLnkst") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.extCmbndLnkst = atoi (lpValue);    
                }
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
                else if (strcmp (lpName, "mopc") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.mopc = atoi (lpValue);    
                }
#endif
                else if (strcmp (lpName, "t15") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.tmr.t15.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snGen.tmr.t15.val) {
                      aeMgmt.t.cfg.s.snGen.tmr.t15.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snGen.tmr.t15.enb = false;
                    }
                }
                else if (strcmp (lpName, "t16") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.tmr.t16.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snGen.tmr.t16.val) {
                      aeMgmt.t.cfg.s.snGen.tmr.t16.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snGen.tmr.t16.enb = false;
                    }
                }
                else if (strcmp (lpName, "t18") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.tmr.t18.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snGen.tmr.t18.val) {
                      aeMgmt.t.cfg.s.snGen.tmr.t18.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snGen.tmr.t18.enb = false;
                    }
                }
                else if (strcmp (lpName, "t19") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.tmr.t19.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snGen.tmr.t19.val) {
                      aeMgmt.t.cfg.s.snGen.tmr.t19.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snGen.tmr.t19.enb = false;
                    }
                }
                else if (strcmp (lpName, "t21") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.tmr.t21.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snGen.tmr.t21.val) {
                      aeMgmt.t.cfg.s.snGen.tmr.t21.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snGen.tmr.t21.enb = false;
                    }
                }
#if (SS7_ANS92 || SS7_ANS88 || SS7_ANS96 || defined(TDS_ROLL_UPGRADE_SUPPORT))
                else if (strcmp (lpName, "t26") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.tmr.t26.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snGen.tmr.t26.val) {
                      aeMgmt.t.cfg.s.snGen.tmr.t26.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snGen.tmr.t26.enb = false;
                    }
                }
#endif
#ifdef SNZN_AUDIT_ENABLED
                else if (strcmp (lpName, "tlsetrec") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.tmr.tlsetrec.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snGen.tmr.tlsetrec.val) {
                      aeMgmt.t.cfg.s.snGen.tmr.tlsetrec.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snGen.tmr.tlsetrec.enb = false;
                    }
                }
                else if (strcmp (lpName, "tlnkrec") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.tmr.tlnkrec.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snGen.tmr.tlnkrec.val) {
                      aeMgmt.t.cfg.s.snGen.tmr.tlnkrec.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snGen.tmr.tlnkrec.enb = false;
                    }
                }
                else if (strcmp (lpName, "trterec") == 0)
                {
                    aeMgmt.t.cfg.s.snGen.tmr.trterec.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snGen.tmr.trterec.val) {
                      aeMgmt.t.cfg.s.snGen.tmr.trterec.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snGen.tmr.trterec.enb = false;
                    }
                }
#endif
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_USPCFG)
              {
                if (strcmp (lpName, "ssf") == 0)
                {
                    aeMgmt.t.cfg.s.snNSAP.ssf = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkType") == 0)
                {
                    aeMgmt.t.cfg.s.snNSAP.lnkType = atoi (lpValue);
                }
                else if (strcmp (lpName, "userPartSwitchType") == 0)
                {
                    aeMgmt.t.cfg.s.snNSAP.upSwtch = atoi (lpValue);    
                }
                else if (strcmp (lpName, "destPointCodeLen") == 0)
                {
                    aeMgmt.t.cfg.s.snNSAP.dpcLen = atoi (lpValue);    
                }
#if (defined(SN_SG) || defined(TDS_ROLL_UPGRADE_SUPPORT))
                else if (strcmp (lpName, "usrParts") == 0)
                {
                    aeMgmt.t.cfg.s.snNSAP.usrParts = atoi (lpValue);    
                }
#endif
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_LSPCFG)
              {
                if (strcmp (lpName, "serviceProviderId") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.spId = atoi (lpValue);    
                    hdr.elmId.elmntInst1 = aeMgmt.t.cfg.s.snDLSAP.spId;
                    lpLinkInfo->sapId = hdr.elmId.elmntInst1;
                }
                else if (strcmp (lpName, "linksetId") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.lnkSetId = atoi (lpValue);    
                    lpLinkInfo->linkSetId = aeMgmt.t.cfg.s.snDLSAP.lnkSetId;
                }
                else if (strcmp (lpName, "origPointCode") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.opc = atoi (lpValue);    
                    if (mbOverrideXml)
                      aeMgmt.t.cfg.s.snDLSAP.opc = self.miSelfPC;

                    lpLinkInfo->opc = aeMgmt.t.cfg.s.snDLSAP.opc;
                }
                else if (strcmp (lpName, "adjDestPointCode") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.adjDpc = atoi (lpValue);

                    lpLinkInfo->dpc = aeMgmt.t.cfg.s.snDLSAP.adjDpc;
                }
                else if (strcmp (lpName, "linkPriority") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.lnkPrior = atoi (lpValue);
                }
                else if (strcmp (lpName, "msgLength") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.msgSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "msgPriority") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.msgPrior = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkType") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.lnkType = atoi (lpValue);    
                }
                else if (strcmp (lpName, "userPartSwitchType") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.upSwtch = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxSLTTry") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.maxSLTtry = atoi (lpValue);    
                }
                else if (strcmp (lpName, "priority0QLength") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.p0QLen = atoi (lpValue);    
                }
                else if (strcmp (lpName, "priority1QLength") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.p1QLen = atoi (lpValue);    
                }
                else if (strcmp (lpName, "priority2QLength") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.p2QLen = atoi (lpValue);    
                }
                else if (strcmp (lpName, "priority3QLength") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.p3QLen = atoi (lpValue);    
                }
                else if (strcmp (lpName, "discardPriority") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.discPrior = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkIdentity") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.lnkId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxCredit") == 0)
                {
                    //aeMgmt.t.cfg.s.snDLSAP.maxCredit = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkSLCForLinkTest") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.lnkTstSLC = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkTstPatternLen") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tstLen = atoi (lpValue);    
                    for (int i = 0; i < LSN_LNKTSTMAX; ++i) {
                      aeMgmt.t.cfg.s.snDLSAP.tst[i] = 0;
                    }
                }
                else if (strcmp (lpName, "linkTstPattern0") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[0] = atoi (lpValue);    
                    //aeMgmt.t.cfg.s.snDLSAP.tst[0] = 'L';    
                }
                else if (strcmp (lpName, "linkTstPattern1") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[1] = atoi (lpValue);    
                    //aeMgmt.t.cfg.s.snDLSAP.tst[1] = 'O';
                }
                else if (strcmp (lpName, "linkTstPattern2") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[2] = atoi (lpValue);    
                    //aeMgmt.t.cfg.s.snDLSAP.tst[2] = 'R';
                }
                else if (strcmp (lpName, "linkTstPattern3") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[3] = atoi (lpValue);    
                    //aeMgmt.t.cfg.s.snDLSAP.tst[3] = 'A';
                }
                else if (strcmp (lpName, "linkTstPattern4") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[4] = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkTstPattern5") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[5] = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkTstPattern6") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[6] = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkTstPattern7") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[7] = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkTstPattern8") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[8] = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkTstPattern9") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[9] = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkTstPattern10") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[10] = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkTstPattern11") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[11] = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkTstPattern12") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[12] = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkTstPattern13") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[13] = atoi (lpValue);    
                }
                else if (strcmp (lpName, "linkTstPattern14") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tst[14] = atoi (lpValue);    
                }
                else if (strcmp (lpName, "ssf") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.ssf = atoi (lpValue);    
                }
                else if (strcmp (lpName, "t1") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t1.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t1.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t1.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t1.enb = false;
                    }
                }
                else if (strcmp (lpName, "t2") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t2.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t2.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t2.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t2.enb = false;
                    }
                }
                else if (strcmp (lpName, "t3") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t3.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t3.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t3.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t3.enb = false;
                    }
                }
                else if (strcmp (lpName, "t4") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t4.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t4.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t4.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t4.enb = false;
                    }
                }
                else if (strcmp (lpName, "t5") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t5.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t5.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t5.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t5.enb = false;
                    }
                }
                else if (strcmp (lpName, "t7") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t7.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t7.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t7.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t7.enb = false;
                    }
                }
                else if (strcmp (lpName, "t12") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t12.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t12.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t12.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t12.enb = false;
                    }
                }
                else if (strcmp (lpName, "t13") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t13.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t13.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t13.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t13.enb = false;
                    }
                }
                else if (strcmp (lpName, "t14") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t14.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t14.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t14.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t14.enb = false;
                    }
                }
                else if (strcmp (lpName, "t17") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t17.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t17.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t17.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t17.enb = false;
                    }
                }
                else if (strcmp (lpName, "t22") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t22.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t22.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t22.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t22.enb = false;
                    }
                }
                else if (strcmp (lpName, "t23") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t23.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t23.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t23.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t23.enb = false;
                    }
                }
                else if (strcmp (lpName, "t24") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t24.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t24.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t24.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t24.enb = false;
                    }
                }
                else if (strcmp (lpName, "t31") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t31.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t31.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t31.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t31.enb = false;
                    }
                }
                else if (strcmp (lpName, "t32") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t32.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t32.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t32.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t32.enb = false;
                    }
                }
                else if (strcmp (lpName, "t33") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t33.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t33.val) {
                      printf("T33 ENABLED -- \n");
                      printf("T33 ENABLED -- \n");
                      printf("T33 ENABLED -- \n");
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t33.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t33.enb = false;
                    }
                }
                else if (strcmp (lpName, "t34") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t34.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t34.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t34.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t34.enb = false;
                    }
                }
#if (SS7_ANS92 || SS7_ANS88 || SS7_ANS96 || defined(TDS_ROLL_UPGRADE_SUPPORT))
                else if (strcmp (lpName, "t35") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t35.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t35.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t35.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t35.enb = false;
                    }
                }
                else if (strcmp (lpName, "t36") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t36.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t36.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t36.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t36.enb = false;
                    }
                }
                else if (strcmp (lpName, "t37") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.t37.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.t37.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t37.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.t37.enb = false;
                    }
                }
                else if (strcmp (lpName, "tCraft") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.tCraft.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.tCraft.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.tCraft.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.tCraft.enb = false;
                    }
                }
#endif
                else if (strcmp (lpName, "tFlc") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.tFlc.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.tFlc.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.tFlc.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.tFlc.enb = false;
                    }
                }
                else if (strcmp (lpName, "tBnd") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.tmr.tBnd.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.s.snDLSAP.tmr.tBnd.val) {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.tBnd.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snDLSAP.tmr.tBnd.enb = false;
                    }
                }
                else if (strcmp (lpName, "destPointCodeLen") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.dpcLen = atoi (lpValue);    
                }
                else if (strcmp (lpName, "srvProviderId") == 0)
                {
                    //aeMgmt.t.cfg.s.snDLSAP.spId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "flushContHandling") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.flushContFlag = atoi (lpValue);    
                }
#if (defined(TTC_BBAND) || defined(TDS_ROLL_UPGRADE_SUPPORT))
#if (SS7_ANS92 || SS7_ANS88 || SS7_ANS96 || SS7_TTC || SS7_CHINA || defined(TDS_ROLL_UPGRADE_SUPPORT))
                else if (strcmp (lpName, "l2Type") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.l2Type = atoi (lpValue);
                }
#endif
#if (SS7_ANS92 || SS7_ANS88 || SS7_ANS96 || defined(TDS_ROLL_UPGRADE_SUPPORT))
                else if (strcmp (lpName, "isCLink") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.isCLink = atoi (lpValue);
                }
#endif
#else
#if (SS7_ANS92 || SS7_ANS88 || SS7_ANS96 || SS7_CHINA)
                else if (strcmp (lpName, "l2Type") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.l2Type = atoi (lpValue);
                }
                else if (strcmp (lpName, "isCLink") == 0)
                {
                    aeMgmt.t.cfg.s.snDLSAP.isCLink = atoi (lpValue);
                }
#endif
#endif
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_RTECFG_SELF)
              {
                if (strcmp (lpName, "dpc") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.dpc = atoi (lpValue);
                    if (mbOverrideXml)
                      aeMgmt.t.cfg.s.snRout.dpc = self.miSelfPC;
                }
                else if (strcmp (lpName, "typeOfSP") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.spType = atoi (lpValue);
                }
                else if (strcmp (lpName, "switchType") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.swtchType = atoi (lpValue);
                }
                else if (strcmp (lpName, "userPartSwitchType") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.upSwtch = atoi (lpValue);
                }
                else if (strcmp (lpName, "cmbLnkSetId") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.cmbLnkSetId = atoi (lpValue);
                }
                else if (strcmp (lpName, "direction") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.dir = atoi (lpValue);
                }
                else if (strcmp (lpName, "ssf") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.ssf = atoi (lpValue);
                }
                else if (strcmp (lpName, "slsRange") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.slsRange = atoi (lpValue);
                }
                else if (strcmp (lpName, "lsetSel") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.lsetSel = atoi (lpValue);
                }
                else if (strcmp (lpName, "multiMsgPrior") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.multiMsgPrior = atoi (lpValue);
                }
                else if (strcmp (lpName, "rctReq") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.rctReq = atoi (lpValue);
                }
#ifdef LSNV2
#if (SS7_NTT || defined(TDS_ROLL_UPGRADE_SUPPORT))
                else if (strcmp (lpName, "destSpec") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.destSpec = atoi (lpValue);
                }
#endif
#endif
#if (defined(LSNV3) || defined(SN_MULTIPLE_NETWORK_RESTART))
                else if (strcmp (lpName, "tfrReq") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tfrReq = atoi (lpValue);
                }
#endif
                else if (strcmp (lpName, "t6") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t6.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t6.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t6.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t6.enb = false;
                    }
                }
                else if (strcmp (lpName, "t8") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t8.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t8.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t8.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t8.enb = false;
                    }
                }
                else if (strcmp (lpName, "t10") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t10.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t10.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t10.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t10.enb = false;
                    }
                }
                else if (strcmp (lpName, "t11") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t11.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t11.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t11.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t11.enb = false;
                    }
                }
#if (defined(LSNV3) || defined(SN_MULTIPLE_NETWORK_RESTART))
                else if (strcmp (lpName, "t18") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t18.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t18.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t18.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t18.enb = false;
                    }
                }
#endif
#if (SS7_ANS92 || SS7_ANS88 || SS7_ANS96 || defined(TDS_ROLL_UPGRADE_SUPPORT))
#if (defined(LSNV3) || defined(SN_MULTIPLE_NETWORK_RESTART))
#else
                else if (strcmp (lpName, "t18") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t18.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t18.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t18.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t18.enb = false;
                    }
                }
#endif
#endif
                else if (strcmp (lpName, "t19") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t19.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t19.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t19.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t19.enb = false;
                    }
                }
                else if (strcmp (lpName, "t21") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t21.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t21.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t21.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t21.enb = false;
                    }
                }
#if (SS7_ANS92 || SS7_ANS88 || SS7_ANS96 || defined(TDS_ROLL_UPGRADE_SUPPORT))
                else if (strcmp (lpName, "t25") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t25.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t25.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t25.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t25.enb = false;
                    }
                }
#endif
                else if (strcmp (lpName, "rteToAdjSp") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.rteToAdjSp = atoi (lpValue);
                }
                else if (strcmp (lpName, "ssf") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.ssf = atoi (lpValue);
                }
                else if (strcmp (lpName, "brdcastFlg") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.brdcastFlg = atoi (lpValue);
                }
                else if (strcmp (lpName, "slsRange") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.slsRange = atoi (lpValue);
                }
                else if (strcmp (lpName, "rstReq") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.rstReq = atoi (lpValue);
                }
                else if (strcmp (lpName, "lsetSel") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.lsetSel = atoi (lpValue);
                }
                else if (strcmp (lpName, "multiMsgPrior") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.multiMsgPrior = atoi (lpValue);
                }
                else if (strcmp (lpName, "rctReq") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.rctReq = atoi (lpValue);
                }
                else if (strcmp (lpName, "slsLnk") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.slsLnk = atoi (lpValue);
                }
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_RTECFG_PEER)
              {
                if (strcmp (lpName, "dpc") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.dpc = atoi (lpValue);
                }
                else if (strcmp (lpName, "typeOfSP") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.spType = atoi (lpValue);
                }
                else if (strcmp (lpName, "switchType") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.swtchType = atoi (lpValue);
                }
                else if (strcmp (lpName, "userPartSwitchType") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.upSwtch = atoi (lpValue);
                }
                else if (strcmp (lpName, "cmbLnkSetId") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.cmbLnkSetId = atoi (lpValue);
                }
                else if (strcmp (lpName, "direction") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.dir = atoi (lpValue);
                }
                else if (strcmp (lpName, "t6") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t6.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t6.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t6.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t6.enb = false;
                    }
                }
                else if (strcmp (lpName, "t8") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t8.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t8.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t8.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t8.enb = false;
                    }
                }
                else if (strcmp (lpName, "t10") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t10.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t10.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t10.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t10.enb = false;
                    }
                }
                else if (strcmp (lpName, "t11") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t11.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t11.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t11.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t11.enb = false;
                    }
                }
#if (defined(LSNV3) || defined(SN_MULTIPLE_NETWORK_RESTART))
                else if (strcmp (lpName, "t18") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t18.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t18.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t18.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t18.enb = false;
                    }
                }
#endif
#if (SS7_ANS92 || SS7_ANS88 || SS7_ANS96 || defined(TDS_ROLL_UPGRADE_SUPPORT))
#if (defined(LSNV3) || defined(SN_MULTIPLE_NETWORK_RESTART))
#else
                else if (strcmp (lpName, "t18") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t18.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t18.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t18.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t18.enb = false;
                    }
                }
#endif
#endif
                else if (strcmp (lpName, "t19") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t19.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t19.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t19.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t19.enb = false;
                    }
                }
                else if (strcmp (lpName, "t21") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t21.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t21.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t21.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t21.enb = false;
                    }
                }
#if (SS7_ANS92 || SS7_ANS88 || SS7_ANS96 || defined(TDS_ROLL_UPGRADE_SUPPORT))
                else if (strcmp (lpName, "t25") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tmr.t25.val = atoi (lpValue);
                    if(aeMgmt.t.cfg.s.snRout.tmr.t25.val) {
                      aeMgmt.t.cfg.s.snRout.tmr.t25.enb = true;
                    }
                    else {
                      aeMgmt.t.cfg.s.snRout.tmr.t25.enb = false;
                    }
                }
#endif
                else if (strcmp (lpName, "rteToAdjSp") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.rteToAdjSp = atoi (lpValue);
                }
                else if (strcmp (lpName, "ssf") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.ssf = atoi (lpValue);
                }
                else if (strcmp (lpName, "brdcastFlg") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.brdcastFlg = atoi (lpValue);
                }
                else if (strcmp (lpName, "slsRange") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.slsRange = atoi (lpValue);
                }
                else if (strcmp (lpName, "rstReq") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.rstReq = atoi (lpValue);
                }
                else if (strcmp (lpName, "lsetSel") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.lsetSel = atoi (lpValue);
                }
                else if (strcmp (lpName, "multiMsgPrior") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.multiMsgPrior = atoi (lpValue);
                }
                else if (strcmp (lpName, "rctReq") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.rctReq = atoi (lpValue);
                }
#ifdef LSNV2
#if (SS7_NTT || defined(TDS_ROLL_UPGRADE_SUPPORT))
                else if (strcmp (lpName, "destSpec") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.destSpec = atoi (lpValue);
                }
#endif
#endif
#if (defined(LSNV3) || defined(SN_MULTIPLE_NETWORK_RESTART))
                else if (strcmp (lpName, "tfrReq") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.tfrReq = atoi (lpValue);
                }
#endif
                else if (strcmp (lpName, "slsLnk") == 0)
                {
                    aeMgmt.t.cfg.s.snRout.slsLnk = atoi (lpValue);
                }
              }
              else if (aiType == BP_AIN_SM_SUBTYPE_LNKSETCFG)
              {
                if (strcmp (lpName, "linksetId") == 0)
                {
                    aeMgmt.t.cfg.s.snLnkSet.lnkSetId = atoi (lpValue);
                }
                else if (strcmp (lpName, "adjDestPointCode") == 0)
                {
                    aeMgmt.t.cfg.s.snLnkSet.adjDpc = atoi (lpValue);
                }
                else if (strcmp (lpName, "nmbActiveLnkReqd") == 0)
                {
                    aeMgmt.t.cfg.s.snLnkSet.nmbActLnkReqd = atoi (lpValue);
                }
                else if (strcmp (lpName, "lnkSetType") == 0)
                {
                    aeMgmt.t.cfg.s.snLnkSet.lnkSetType = atoi (lpValue);
                }
                else if (strcmp (lpName, "nmbCmbLnkSet") == 0)
                {
                    int totalCmbLnkSetCount = atoi (lpValue);
                    aeMgmt.t.cfg.s.snLnkSet.nmbCmbLnkSet = totalCmbLnkSetCount;

                    for (int i = 0; i < LSN_MAXCMBLNK; i++)
                    {
                      aeMgmt.t.cfg.s.snLnkSet.cmbLnkSet[i].cmbLnkSetId = 0;
                      aeMgmt.t.cfg.s.snLnkSet.cmbLnkSet[i].lnkSetPrior = 0;
                    }

                    if(0 < *apiCount) {
                      aeMgmt.t.cfg.s.snLnkSet.nmbCmbLnkSet -= (*apiCount);
                    }

                    if(aeMgmt.t.cfg.s.snLnkSet.nmbCmbLnkSet > LSN_MAXCMBLNK) {
                      aeMgmt.t.cfg.s.snLnkSet.nmbCmbLnkSet = LSN_MAXCMBLNK;
                    }

                    for (int i = 0; i < aeMgmt.t.cfg.s.snLnkSet.nmbCmbLnkSet; i++)
                    {
                      (*apiCount)++;
                      aeMgmt.t.cfg.s.snLnkSet.cmbLnkSet[i].cmbLnkSetId = (*apiCount);
                      aeMgmt.t.cfg.s.snLnkSet.cmbLnkSet[i].lnkSetPrior = 0;
                    }

                    if(*apiCount < totalCmbLnkSetCount) {
                      logger.logMsg (VERBOSE_FLAG, 0, "aiCount [%d] and nmbCmbLnkSet [%d]", 
                            *apiCount, aeMgmt.t.cfg.s.snLnkSet.nmbCmbLnkSet);
                      logger.logMsg (VERBOSE_FLAG, 0, "Should contine for linksetId [%d]", 
                            aeMgmt.t.cfg.s.snLnkSet.lnkSetId);
                      result = BP_AIN_SM_INDEX_REPEAT;
                    }
                    else {
                      logger.logMsg (VERBOSE_FLAG, 0, "aiCount [%d] and nmbCmbLnkSet [%d]", 
                            *apiCount, aeMgmt.t.cfg.s.snLnkSet.nmbCmbLnkSet);
                      logger.logMsg (VERBOSE_FLAG, 0, "All the cmbLnkSet configured for linksetId [%d]", 
                            aeMgmt.t.cfg.s.snLnkSet.lnkSetId);
                      (*apiCount) = 0;
                    }
                }
              }
            }//getCfgOid
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for MTP3", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      if (lpLinkInfo)
        delete lpLinkInfo;
      return BP_AIN_SM_INDEX_OVER;
    }

    if (aiType == BP_AIN_SM_SUBTYPE_LSPCFG)
    {
      addSnLinkInfo (lpLinkInfo);
    }


    return result;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getSnMgmt");
}



/******************************************************************************
 *
 *     Fun:   getZtMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for TCAP
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZtMngmt (int aiType, ZtMngmt &aeMgmt, int aiIndex, int* apiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZtMngmt");

    int result = BP_AIN_SM_OK;
    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode* lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"psf-tcap", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <psf-tcap:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    cmMemset((U8 *)&aeMgmt, '\0', sizeof(ZtMngmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;


    lpNode = lpNode->getFirstChild();

    char *lpName = new char [100];
    char *lpValue = new char [100];

    bool indexFound = false;
    int liMonitorResource = 0;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "timeRes") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.timeRes = atoi (lpValue);    
                }
                else if (strcmp (lpName, "tUpdCompAck") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tUpdCompAck.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tUpdCompAck.val)
                      aeMgmt.t.cfg.s.genCfg.tUpdCompAck.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.tUpdCompAck.enb = FALSE;
                }
                else if (strcmp (lpName, "tRecovery") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tRecovery.val = atoi (lpValue);    
                    if (aeMgmt.t.cfg.s.genCfg.tRecovery.val)
                      aeMgmt.t.cfg.s.genCfg.tRecovery.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.tRecovery.enb = FALSE;
                }
                else if (strcmp (lpName, "maxUpdMsgSize") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxUpdMsgSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxUpdMsgs") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxUpdMsgs = atoi (lpValue);    
                }
                else if (strcmp (lpName, "priorNonCrit") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.priorNonCrit = atoi (lpValue);    
                }
                else if (strcmp (lpName, "distEnv") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.distEnv = atoi (lpValue);    
                }
              }
            }//getCfgOid
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for psf-tcap", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }

    return result;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZtMngmt");
}



/******************************************************************************
 *
 *     Fun:   getZpMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZpMngmt (int aiType, ZpMngmt &aeMgmt, int aiIndex, int* apiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZpMngmt");

    int result = BP_AIN_SM_OK;
    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode* lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"psf-sccp", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <psf-sccp:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    cmMemset((U8 *)&aeMgmt, '\0', sizeof(ZpMngmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;


    lpNode = lpNode->getFirstChild();

    char *lpName = new char [100];
    char *lpValue = new char [100];

    bool indexFound = false;
    int liMonitorResource = 0;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "timeRes") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.timeRes = atoi (lpValue);    
                }
                else if (strcmp (lpName, "tUpdCompAck") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tUpdCompAck.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tUpdCompAck.val)
                      aeMgmt.t.cfg.s.genCfg.tUpdCompAck.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.tUpdCompAck.enb = FALSE;
                }
#ifdef LZPV2_1
                else if (strcmp (lpName, "breakTmr") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.breakTmr.val = atoi (lpValue);    
                    if (aeMgmt.t.cfg.s.genCfg.breakTmr.val)
                      aeMgmt.t.cfg.s.genCfg.breakTmr.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.breakTmr.enb = FALSE;
                }
#endif
                else if (strcmp (lpName, "maxUpdMsgSize") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxUpdMsgSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxUpdMsgs") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxUpdMsgs = atoi (lpValue);    
                }
                else if (strcmp (lpName, "priorNonCrit") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.priorNonCrit = atoi (lpValue);    
                }
                else if (strcmp (lpName, "distEnv") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.distEnv = atoi (lpValue);    
                }
              }
            }//getCfgOid
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for psf-sccp", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }

    return result;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZpMngmt");
}



/******************************************************************************
 *
 *     Fun:   getZnMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZnMngmt (int aiType, ZnMngmt &aeMgmt, int aiIndex, int* apiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZnMngmt");

    int result = BP_AIN_SM_OK;
    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode* lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"psf-mtp3", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <psf-mtp3:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    cmMemset((U8 *)&aeMgmt, '\0', sizeof(ZnMngmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;


    lpNode = lpNode->getFirstChild();

    char *lpName = new char [100];
    char *lpValue = new char [100];

    bool indexFound = false;
    int liMonitorResource = 0;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "timeRes") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.timeRes = atoi (lpValue);    
                }
                else if (strcmp (lpName, "tUpdCompAck") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tUpdCompAck.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tUpdCompAck.val)
                      aeMgmt.t.cfg.s.genCfg.tUpdCompAck.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.tUpdCompAck.enb = FALSE;
                }
                else if (strcmp (lpName, "maxUpdMsgSize") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxUpdMsgSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxUpdMsgs") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxUpdMsgs = atoi (lpValue);    
                }
                else if (strcmp (lpName, "priorCrit") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.priorCrit = atoi (lpValue);    
                }
                else if (strcmp (lpName, "distEnv") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.distEnv = atoi (lpValue);    
                }
#ifdef LZNV2
                else if (strcmp (lpName, "breakTmr") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.breakTmr.val = atoi (lpValue);    
                    if (aeMgmt.t.cfg.s.genCfg.breakTmr.val)
                      aeMgmt.t.cfg.s.genCfg.breakTmr.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.breakTmr.enb = FALSE;
                }
#endif
                else if (strcmp (lpName, "rsetGenCfg") == 0)
                {
                    getZnDnRsetGen (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.genCfg.rsetGenCfg));
                }
                else if (strcmp (lpName, "txHeartbeat") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.txHeartbeat.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.txHeartbeat.val)
                      aeMgmt.t.cfg.s.genCfg.txHeartbeat.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.txHeartbeat.enb = FALSE; 
                }
                else if (strcmp (lpName, "rxHeartbeat") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.rxHeartbeat.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.rxHeartbeat.val)
                      aeMgmt.t.cfg.s.genCfg.rxHeartbeat.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.rxHeartbeat.enb = FALSE;    
                }
                else if (strcmp (lpName, "tSync") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tSync.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tSync.val)
                      aeMgmt.t.cfg.s.genCfg.tSync.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.tSync.enb = FALSE;    
                }
                else if (strcmp (lpName, "priorNonCrit") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.priorNonCrit = atoi (lpValue);    
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL)
              {
                if (strcmp (lpName, "cmFthaRsetType") == 0)
                {
                    getAttrVal ((DOMElement*) lpNode, (char*)"type", lpValue);
                    aeMgmt.t.cfg.s.rsetMapCfg.rType.type = atoi (lpValue);
                    getAttrVal ((DOMElement*) lpNode, (char*)"qual", lpValue);
                    aeMgmt.t.cfg.s.rsetMapCfg.rType.qual = atoi (lpValue);
                }
                else if (strcmp (lpName, "id") == 0)
                {
                    aeMgmt.t.cfg.s.rsetMapCfg.u.crit.id = atoi (lpValue);
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF)
              {
                if (strcmp (lpName, "cmFthaRsetType") == 0)
                {
                    getAttrVal ((DOMElement*) lpNode, (char*)"type", lpValue);
                    aeMgmt.t.cfg.s.rsetMapCfg.rType.type = atoi (lpValue);
                    getAttrVal ((DOMElement*) lpNode, (char*)"qual", lpValue);
                    aeMgmt.t.cfg.s.rsetMapCfg.rType.qual = atoi (lpValue);
                }
                else if (strcmp (lpName, "id") == 0)
                {
                    aeMgmt.t.cfg.s.rsetMapCfg.u.def.id = atoi (lpValue);
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL)
              {
                if (strcmp (lpName, "cmFthaRsetType") == 0)
                {
                    getAttrVal ((DOMElement*) lpNode, (char*)"type", lpValue);
                    aeMgmt.t.cfg.s.rsetMapCfg.rType.type = atoi (lpValue);
                    getAttrVal ((DOMElement*) lpNode, (char*)"qual", lpValue);
                    aeMgmt.t.cfg.s.rsetMapCfg.rType.qual = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,"[CCC:cmFthaRsetType] rsetMapCfg.rType.type= %d, rsetMapCfg.rType.qual = %d",aeMgmt.t.cfg.s.rsetMapCfg.rType.type, aeMgmt.t.cfg.s.rsetMapCfg.rType.qual);
                }
                else if (strcmp (lpName, "ifType") == 0)
                {
                    aeMgmt.t.cfg.s.rsetMapCfg.u.nonCrit.ifType = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,"[CCC:cmFthaRsetType] nonCrit.ifType= %d",aeMgmt.t.cfg.s.rsetMapCfg.u.nonCrit.ifType);
                }
                else if (strcmp (lpName, "CmZnDnRsetMap_upper") == 0)
                {
                    getZnDnRsetMap (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.rsetMapCfg.u.nonCrit.upper));
                    logger.logMsg (TRACE_FLAG, 0,"[CCC] upper.type.dist= %d",aeMgmt.t.cfg.s.rsetMapCfg.u.nonCrit.upper.type.dist);
                    logger.logMsg (TRACE_FLAG, 0,"[CCC] upper.type.qual= %d",aeMgmt.t.cfg.s.rsetMapCfg.u.nonCrit.upper.type.qual);

                }
                else if (strcmp (lpName, "CmZnDnRsetMap_lower") == 0)
                {
                    getZnDnRsetMap (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.rsetMapCfg.u.nonCrit.lower));
                }
              }
            }//getCfgOid
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for psf-mtp3", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }

    return result;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZnMngmt");
}


void INGwSmRepository::getEntCfg(DOMNode *lpNode,char *lpName, char *lpValue, SgHiEntCfg *entCfg)
{
   if (strcmp (lpName, "entId") == 0)
   {
       entCfg->entId = atoi (lpValue);    
   }
   else if (strcmp (lpName, "instId") == 0)
   {
       entCfg->instId = atoi (lpValue);    
   }
   else if (strcmp (lpName, "entType") == 0)
   {
       entCfg->entType = atoi (lpValue);    
   }
   else if (strcmp (lpName, "nmbCritical") == 0)
   {
       entCfg->nmbCritical = atoi (lpValue);    
   }
   else if (strcmp (lpName, "critRsets") == 0)
   {
       int liCount=0;
       getU32List (lpNode->getFirstChild(), (U32*)&(entCfg->critRsets[0]), liCount);
       entCfg->nmbCritical = liCount;
   }
   else if (strcmp (lpName, "tapaRsetId") == 0)
   {
       entCfg->tapaRsetId = atoi (lpValue);    
   }
   else if (strcmp (lpName, "nmbNonCritical") == 0)
   {
       entCfg->nmbNonCritical = atoi (lpValue);    
   }
   else if (strcmp (lpName, "sgHiRsetSpec") == 0)
   {
       int liCount=0;
       getHiRsetSpec (lpNode->getFirstChild(), &(entCfg->nonCritRsets[0]), liCount);
       entCfg->nmbNonCritical = liCount;
   }
   else if (strcmp (lpName, "nmbUsers") == 0)
   {
       entCfg->nmbUsers = atoi (lpValue);    
   }
   else if (strcmp (lpName, "userList") == 0)
   {
       int liCount=0;
       getU8ParmList (lpNode->getFirstChild(), &(entCfg->userList[0]), liCount);
       entCfg->nmbUsers = liCount;
   }
   else if (strcmp (lpName, "userInst") == 0)
   {
       int liCount=0;
       getU8ParmList (lpNode->getFirstChild(), &(entCfg->userInst[0]), liCount);
   }
   else if (strcmp (lpName, "nmbProv") == 0)
   {
       entCfg->nmbProv = atoi (lpValue);    
   }
   else if (strcmp (lpName, "provList") == 0)
   {
       int liCount=0;
       getU8ParmList (lpNode->getFirstChild(), &(entCfg->provList[0]), liCount);
       entCfg->nmbProv = liCount;
   }
   else if (strcmp (lpName, "provInst") == 0)
   {
       int liCount=0;
       getU8ParmList (lpNode->getFirstChild(), &(entCfg->provInst[0]), liCount);
   }
}



/******************************************************************************
 *
 *     Fun:   getSgMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getSgMngmt (int aiType, SgMngmt &aeMgmt, int aiIndex, int* apiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSgMngmt");

    int result = BP_AIN_SM_OK;
    string lstrOpName;
    int liCount = 0;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode* lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"SG", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <SG:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    cmMemset((U8 *)&aeMgmt, '\0', sizeof(SgMngmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;


    lpNode = lpNode->getFirstChild();

    char *lpName = new char [100];
    char *lpValue = new char [100];

    bool indexFound = false;
    int liMonitorResource = 0;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              switch (aiType)
              {
			  case BP_AIN_SM_SUBTYPE_GENCFG :
              {
                if (strcmp (lpName, "maxSysNodes") == 0)
                {
                    aeMgmt.t.hi.cfg.gen.maxSysNodes = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxSysEnts") == 0)
                {
                    aeMgmt.t.hi.cfg.gen.maxSysEnts = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxEntRsets") == 0)
                {
                    aeMgmt.t.hi.cfg.gen.maxEntRsets = atoi (lpValue);    
                }
                else if (strcmp (lpName, "tmrRes") == 0)
                {
                    aeMgmt.t.hi.cfg.gen.tmrRes = atoi (lpValue);    
                }
                else if (strcmp (lpName, "rtxTimerCfg") == 0)
                {
                    aeMgmt.t.hi.cfg.gen.rtxTimerCfg.val = atoi (lpValue);
                    if (aeMgmt.t.hi.cfg.gen.rtxTimerCfg.val)
                      aeMgmt.t.hi.cfg.gen.rtxTimerCfg.enb = TRUE;
                    else
                      aeMgmt.t.hi.cfg.gen.rtxTimerCfg.enb = FALSE;
                }
                else if (strcmp (lpName, "rtxLongTimerCfg") == 0)
                {
                    aeMgmt.t.hi.cfg.gen.rtxLongTimerCfg.val = atoi (lpValue);
                    if (aeMgmt.t.hi.cfg.gen.rtxLongTimerCfg.val)
                      aeMgmt.t.hi.cfg.gen.rtxLongTimerCfg.enb = TRUE;
                    else
                      aeMgmt.t.hi.cfg.gen.rtxLongTimerCfg.enb = FALSE;
                }
                else if (strcmp (lpName, "rtxCount") == 0)
                {
                    aeMgmt.t.hi.cfg.gen.rtxCount = atoi (lpValue);    
                }
                else if (strcmp (lpName, "peerExists") == 0)
                {
                    aeMgmt.t.hi.cfg.gen.peerExists = atoi (lpValue);    
                }
                else if (strcmp (lpName, "peerMemory") == 0)
                {
                    //aeMgmt.t.hi.cfg.gen.peerMemory.region = atoi (lpValue);    
                    aeMgmt.t.hi.cfg.gen.peerMemory.region = BP_AIN_SM_REGION;
                }
                else if (strcmp (lpName, "txHeartBeatTimerCfg") == 0)
                {
                    aeMgmt.t.hi.cfg.gen.txHeartBeatTimerCfg.val = atoi (lpValue);
                    if (aeMgmt.t.hi.cfg.gen.txHeartBeatTimerCfg.val)
                      aeMgmt.t.hi.cfg.gen.txHeartBeatTimerCfg.enb = TRUE;
                    else
                      aeMgmt.t.hi.cfg.gen.txHeartBeatTimerCfg.enb = FALSE;
                }
                else if (strcmp (lpName, "rxHeartBeatTimerCfg") == 0)
                {
                      logger.logMsg (TRACE_FLAG, 0,
                          " rxHeartBeatTimerCfg ");
                    aeMgmt.t.hi.cfg.gen.rxHeartBeatTimerCfg.val = atoi (lpValue);
                    if (aeMgmt.t.hi.cfg.gen.rxHeartBeatTimerCfg.val)
                      aeMgmt.t.hi.cfg.gen.rxHeartBeatTimerCfg.enb = TRUE;
                    else
                      aeMgmt.t.hi.cfg.gen.rxHeartBeatTimerCfg.enb = FALSE;
                }
#ifdef LSGV3
                else if (strcmp (lpName, "nmbDbProcs") == 0)
                {
                      logger.logMsg (TRACE_FLAG, 0,"calling getU32List for nmbDbProcs");
                      logger.logMsg (TRACE_FLAG, 0,
                          " nmbDbProcs ");
                    aeMgmt.t.hi.cfg.gen.nmbDbProcs = atoi (lpValue);    
                }
                else if (strcmp (lpName, "dbProcLst") == 0)
                {
                      logger.logMsg (TRACE_FLAG, 0,"calling getU32List for dbProcLst");
                    getU32List (lpNode->getFirstChild(), &(aeMgmt.t.hi.cfg.gen.dbProcLst[0]), liCount);
                    aeMgmt.t.hi.cfg.gen.nmbDbProcs = liCount;
                }
#endif
				break;
              }
              case BP_AIN_SM_SUBTYPE_ENT_SG :
			  case BP_AIN_SM_SUBTYPE_ENT_TUCL :
		      case BP_AIN_SM_SUBTYPE_ENT_SCTP :
			  case BP_AIN_SM_SUBTYPE_ENT_M3UA :
			  case BP_AIN_SM_SUBTYPE_ENT_MTP2 :
			  case BP_AIN_SM_SUBTYPE_ENT_MTP3 :
			  case BP_AIN_SM_SUBTYPE_ENT_SCCP :
			  case BP_AIN_SM_SUBTYPE_ENT_TCAP :
              {
                getEntCfg(lpNode,lpName,lpValue,&(aeMgmt.t.hi.cfg.ent));
                    /*logger.logMsg (TRACE_FLAG, 0,
                        "entId = %d, instId = %d  entType = %d  nmbCritical = %d nmbNonCritical = %d  rsetId=%d  rsetGr[=%d   nmbProv=%d  tapaRsetId=%d", aeMgmt.t.hi.cfg.ent.entId,aeMgmt.t.hi.cfg.ent.instId,aeMgmt.t.hi.cfg.ent.entType,aeMgmt.t.hi.cfg.ent.nmbCritical,aeMgmt.t.hi.cfg.ent.nmbNonCritical,aeMgmt.t.hi.cfg.ent.nonCritRsets[0].rsetId,aeMgmt.t.hi.cfg.ent.nonCritRsets[0].rsetGrp,aeMgmt.t.hi.cfg.ent.nmbProv,aeMgmt.t.hi.cfg.ent.tapaRsetId);*/
              }
			  break;
              }
            }//getCfgOid
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for SG", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }

    return result;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getSgMngmt");
}



/******************************************************************************
 *
 *     Fun:   getZvMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getZvMngmt (int aiType, ZvMngmt &aeMgmt, int aiIndex, int* apiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getZvMngmt");

    int result = BP_AIN_SM_OK;
    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode* lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"psf-m3ua", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <psf-m3ua:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    //cmMemset((U8 *)&aeMgmt, '\0', sizeof(ZvMngmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;


    lpNode = lpNode->getFirstChild();

    char *lpName = new char [100];
    char *lpValue = new char [100];

    bool indexFound = false;
    int liMonitorResource = 0;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "timeRes") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.timeRes = atoi (lpValue);    
                }
                else if (strcmp (lpName, "tUpdCompAck") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tUpdCompAck.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tUpdCompAck.val)
                      aeMgmt.t.cfg.s.genCfg.tUpdCompAck.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.tUpdCompAck.enb = FALSE;
                }
                else if (strcmp (lpName, "maxUpdMsgSize") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxUpdMsgSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxUpdMsgs") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.maxUpdMsgs = atoi (lpValue);    
                }
                else if (strcmp (lpName, "priorCrit") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.priorCrit = atoi (lpValue);    
                }
                else if (strcmp (lpName, "distEnv") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.distEnv = atoi (lpValue);    
                }
#ifdef LZNV2
                else if (strcmp (lpName, "breakTmr") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.breakTmr.val = atoi (lpValue);    
                    if (aeMgmt.t.cfg.s.genCfg.breakTmr.val)
                      aeMgmt.t.cfg.s.genCfg.breakTmr.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.breakTmr.enb = FALSE;
                }
#endif
#ifdef ZV_DFTHA
                else if (strcmp (lpName, "rsetGenCfg") == 0)
                {
                    getZvDvRsetGen (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.genCfg.rsetGenCfg));
                }
#endif
                else if (strcmp (lpName, "txHeartbeat") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.txHeartbeat.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.txHeartbeat.val)
                      aeMgmt.t.cfg.s.genCfg.txHeartbeat.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.txHeartbeat.enb = FALSE; 
                }
                else if (strcmp (lpName, "rxHeartbeat") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.rxHeartbeat.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.rxHeartbeat.val)
                      aeMgmt.t.cfg.s.genCfg.rxHeartbeat.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.rxHeartbeat.enb = FALSE;    
                }
#ifdef ZV_DFTHA
                else if (strcmp (lpName, "tSync") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.tSync.val = atoi (lpValue);
                    if (aeMgmt.t.cfg.s.genCfg.tSync.val)
                      aeMgmt.t.cfg.s.genCfg.tSync.enb = TRUE;
                    else
                      aeMgmt.t.cfg.s.genCfg.tSync.enb = FALSE;    
                }
                else if (strcmp (lpName, "priorNonCrit") == 0)
                {
                    aeMgmt.t.cfg.s.genCfg.priorNonCrit = atoi (lpValue);    
                }
#endif
              }
#ifdef ZV_DFTHA
              if (aiType == BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL)
              {
                if (strcmp (lpName, "cmFthaRsetType") == 0)
                {
                    getAttrVal ((DOMElement*) lpNode, (char*)"type", lpValue);
                    aeMgmt.t.cfg.s.rsetMapCfg.rType.type = atoi (lpValue);
                    getAttrVal ((DOMElement*) lpNode, (char*)"qual", lpValue);
                    aeMgmt.t.cfg.s.rsetMapCfg.rType.qual = atoi (lpValue);
                }
                else if (strcmp (lpName, "id") == 0)
                {
                    aeMgmt.t.cfg.s.rsetMapCfg.u.crit.id = atoi (lpValue);
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF)
              {
                if (strcmp (lpName, "cmFthaRsetType") == 0)
                {
                    getAttrVal ((DOMElement*) lpNode, (char*)"type", lpValue);
                    aeMgmt.t.cfg.s.rsetMapCfg.rType.type = atoi (lpValue);
                    getAttrVal ((DOMElement*) lpNode, (char*)"qual", lpValue);
                    aeMgmt.t.cfg.s.rsetMapCfg.rType.qual = atoi (lpValue);
                }
                else if (strcmp (lpName, "id") == 0)
                {
                    aeMgmt.t.cfg.s.rsetMapCfg.u.def.id = atoi (lpValue);
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL)
              {
                if (strcmp (lpName, "cmFthaRsetType") == 0)
                {
                    getAttrVal ((DOMElement*) lpNode, (char*)"type", lpValue);
                    aeMgmt.t.cfg.s.rsetMapCfg.rType.type = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                     "psf-m3ua Non-crit Rset type <%d>", aeMgmt.t.cfg.s.rsetMapCfg.rType.type);
                    getAttrVal ((DOMElement*) lpNode, (char*)"qual", lpValue);
                    aeMgmt.t.cfg.s.rsetMapCfg.rType.qual = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                     "psf-m3ua Non-crit Rset qual <%d>", aeMgmt.t.cfg.s.rsetMapCfg.rType.qual);
                }
                else if (strcmp (lpName, "ifType") == 0)
                {
                    aeMgmt.t.cfg.s.rsetMapCfg.u.nonCrit.ifType = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                     "psf-m3ua Non-crit Rset ifType <%d>", aeMgmt.t.cfg.s.rsetMapCfg.u.nonCrit.ifType);
                }
                else if (strcmp (lpName, "CmZvDvRsetMap_upper") == 0)
                {
                    getZvDvRsetMap (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.rsetMapCfg.u.nonCrit.upper));
                }
                else if (strcmp (lpName, "CmZvDvRsetMap_lower") == 0)
                {
                    getZvDvRsetMap (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.rsetMapCfg.u.nonCrit.lower));
                }
              }
#endif
            }//getCfgOid
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for psf-m3ua", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }

    return result;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getZvMngmt");
}



/******************************************************************************
 *
 *     Fun:   addSnLinkInfo()
 *
 *     Desc:  This function adds Link Information for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::addSnLinkInfo (INGwSmLinkInfo *apLinkInfo)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::addSnLinkInfo");

    if (apLinkInfo == 0)
      return BP_AIN_SM_FAIL;

    INGwSmLinkInfoMap::iterator iter;

    pthread_rwlock_wrlock (&mLinkLock);

    iter = mLinkMap.find (apLinkInfo->sapId);

    if (iter == mLinkMap.end())
    {
      mLinkMap [apLinkInfo->sapId] = apLinkInfo;
    } 
    else
    {
      INGwSmLinkInfo *lpInfo = iter->second;
      lpInfo->sapId = apLinkInfo->sapId;
      lpInfo->opc = apLinkInfo->opc;
      lpInfo->dpc = apLinkInfo->dpc;
      lpInfo->linkSetId = apLinkInfo->linkSetId;

      delete apLinkInfo;
    }

    pthread_rwlock_unlock (&mLinkLock);

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::addSnLinkInfo");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   addSdChannelInfo ()
 *
 *     Desc:  This function adds channel Information for MTP2
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::addSdChannelInfo (INGwSmLinkInfo *apLinkInfo)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::addSdChannelInfo");

    if (apLinkInfo == 0)
      return BP_AIN_SM_FAIL;

    INGwSmLinkInfoMap::iterator iter;

    pthread_rwlock_wrlock (&mLinkLock);

    iter = mLinkMap.find (apLinkInfo->sapId);

    if (iter == mLinkMap.end())
    {
      mLinkMap [apLinkInfo->sapId] = apLinkInfo;
    }  
    else
    {
      INGwSmLinkInfo *lpInfo = iter->second;
      lpInfo->devId = apLinkInfo->devId;
      lpInfo->channelNum = apLinkInfo->channelNum;

      delete apLinkInfo;
    }

    pthread_rwlock_unlock (&mLinkLock);

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::addSdChannelInfo");
    return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   getSnLinkInfo()
 *
 *     Desc:  This function returns Link Information for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getSnLinkInfo (int aiSapId, 
      int &aiOpc, 
      int &aiDpc, 
      int &aiLinkSetId, 
      int &aiDevId, 
      int &aiChannelNum)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSnLinkInfo");

    INGwSmLinkInfoMap::iterator iter;

    pthread_rwlock_rdlock (&mLinkLock);

    iter = mLinkMap.find (aiSapId);

    if (iter == mLinkMap.end ())
    {
      pthread_rwlock_unlock (&mLinkLock);
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find link info for SAP <%d>", aiSapId);
      return BP_AIN_SM_FAIL;
    }

    INGwSmLinkInfo *lpInfo = iter->second;

    aiOpc = lpInfo->opc;
    aiDpc = lpInfo->dpc;
    aiLinkSetId = lpInfo->linkSetId;
    aiDevId = lpInfo->devId;
    aiChannelNum = lpInfo->channelNum;

    pthread_rwlock_unlock (&mLinkLock);

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getSnLinkInfo");

    return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   getSdMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for MTP2
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getSdMngmt (int aiType, SdMngmt &aeMgmt, int aiIndex)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getSdMngmt");

    return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   getStsTransId()
 * 
 *     Desc:  This function returns a transaction Id for Statistics
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getStsTransId()
{
    return miStsTransId;
}

/******************************************************************************
 *
 *     Fun:   getTransactionId()
 * 
 *     Desc:  This function returns a new transaction Id
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getTransactionId()
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getTransactionId()");

    ++miTransactionId;
    for (int i=0;; i++){
      logger.logMsg (TRACE_FLAG, 0,
        "getTransactionId(): i is <%d>",i);
      if (miTransactionId == miStsTransId ||
          miTransactionId == miDistTransId ||
          miTransactionId == miAuditTransId ||
          miTransactionId == BP_AIN_SM_MTP2_TRANSID)
      {
        miTransactionId++;
      }
      else
        break;
    }

    if (miTransactionId <= 0)
      miTransactionId = 1;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getTransactionId()");
    return miTransactionId;
}


/******************************************************************************
 *
 *     Fun:   getPst()
 * 
 *     Desc:  This function returns post structure 
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    Pst* 
INGwSmRepository::getPst (int aiLayerId)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getPst");

    Pst *lpPst = 0;

    switch (aiLayerId)
    {
      case BP_AIN_SM_TCA_LAYER:
        {
            lpPst = &lstPst;
            break;
        }
      case BP_AIN_SM_SCC_LAYER:
        {
            lpPst = &lspPst;
            break;
        }
      case BP_AIN_SM_M3U_LAYER:
        {
            lpPst = &litPst;
            break;
        }
      case BP_AIN_SM_SCT_LAYER:
        {
            lpPst = &lsbPst;
            break;
        }
      case BP_AIN_SM_TUC_LAYER:
        {
            lpPst = &lhiPst;
            break;
        }
      case BP_AIN_SM_MTP3_LAYER:
        {
            lpPst = &lsnPst;
            break;
        }
      case BP_AIN_SM_MTP2_LAYER:
        {
            lpPst = &lsdPst;
            break;
        }
	  case BP_AIN_SM_PSF_TCAP_LAYER:
	  	{
            lpPst = &lztPst;
            break;
        }
	  case BP_AIN_SM_PSF_SCCP_LAYER:
	  	{
            lpPst = &lzpPst;
            break;
        }
	  case BP_AIN_SM_LDF_MTP3_LAYER:
	  	{
            lpPst = &ldnPst;
            break;
        }
	  case BP_AIN_SM_PSF_MTP3_LAYER:
	  	{
            lpPst = &lznPst;
            break;
        }
	  case BP_AIN_SM_PSF_M3UA_LAYER:
	  	{
            lpPst = &lzvPst;
            break;
        }
	  case BP_AIN_SM_LDF_M3UA_LAYER:
        {
            lpPst = &ldvPst;
            break;
	}
	  case BP_AIN_SM_MR_LAYER:
        {
            lpPst = &lmrPst;
            break;
        }
	  case BP_AIN_SM_SH_LAYER:
        {
            lpPst = &lshPst;
            break;
        }
	  case BP_AIN_SM_SG_LAYER:
        {
            lpPst = &lsgPst;
            break;
        }
	  case BP_AIN_SM_RELAY_LAYER:
        {
            lpPst = &lryPst;
            break;
        }
      default:
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Uknown LayerId passed <%d>", aiLayerId);
            break;
        }
    }

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getPst");

    return lpPst;
}

/******************************************************************************
 *
 *     Fun:   fillDelM3uaNetworkState()
 *
 *     Desc:  Fillup AddNetwork states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelM3uaNetworkState(
      INGwSmIntVector &aeStateVector) {

	int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DELNWK;

	liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

	return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillDelM3uaNetworkState()
 *
 *     Desc:  Fillup AddNetwork states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddM3uaNetworkState(
      INGwSmIntVector &aeStateVector) {

	int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_NWKCFG;

	liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

	return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillDelSccpNetworkState()
 *
 *     Desc:  Fillup AddNetwork states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelSccpNetworkState(
      INGwSmIntVector &aeStateVector) {

	int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_NWKCFG;

	liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

	return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillAddSccpNetworkState()
 *
 *     Desc:  Fillup AddNetwork states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddSccpNetworkState(
      INGwSmIntVector &aeStateVector) {

	int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_NWKCFG;

	liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

	return BP_AIN_SM_OK;
}




/******************************************************************************
 *
 *     Fun:   fillAddSccpGtAddrMapState()
 *
 *     Desc:  Fillup Add GT Address MAP states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddSccpGtAddrMapState(
      INGwSmIntVector &aeStateVector) {

	int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GTADDRMAP;

	liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

	return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillDelSccpGtAddrMapState()
 *
 *     Desc:  Fillup Delete GT Address MAP states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelSccpGtAddrMapState(
      INGwSmIntVector &aeStateVector) {

	int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_GTADDRMAP;

	liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

	return BP_AIN_SM_OK;
}




/******************************************************************************
 *
 *     Fun:   fillAddSccpGtRuleState()
 *
 *     Desc:  Fillup Add GT Rule states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddSccpGtRuleState(
      INGwSmIntVector &aeStateVector) {

	int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GTRULE;

	liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

	return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   fillDelSccpGtRuleState()
 *
 *     Desc:  Fillup Del GT Rule states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelSccpGtRuleState(
      INGwSmIntVector &aeStateVector) {

	int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_GTRULE;

	liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

	return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   fillAddLnkSetState()
 *
 *     Desc:  Fillup AddNetwork states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddLnkSetState(
      INGwSmIntVector &aeStateVector) {

	int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_LNKSETCFG;

	liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

	return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillAddMtp3RouteState()
 *
 *     Desc:  Fillup Add Route states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddMtp3RouteState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RTECFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillAddSccpRouteState()
 *
 *     Desc:  Fillup Add Route states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddSccpRouteState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RTECFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillAddSccpLocalSsnState()
 *
 *     Desc:  Fillup Add Local SSN states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddSccpLocalSsnState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_USPCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}




/******************************************************************************
 *
 *     Fun:   fillAddTcapLocalSsnUsapState()
 *
 *     Desc:  Fillup Add Local SSN states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddTcapLocalSsnUsapState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_TCA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_USPCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillAddTcapLocalSsnLsapState()
 *
 *     Desc:  Fillup Add Local SSN states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddTcapLocalSsnLsapState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_TCA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_LSPCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillAddSccpRemoteSsnState()
 *
 *     Desc:  Fillup Add Remote SSN states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddSccpRemoteSsnState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RTECFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillAddM3uaAsState()
 *
 *     Desc:  Fillup Add AS states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddM3uaAsState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_PSCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   fillAddM3uaRouteState()
 *
 *     Desc:  Fillup Add Route states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddM3uaRouteState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RTECFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillAddM3uaAspState()
 *
 *     Desc:  Fillup Add ASP states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddM3uaAspState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_PSPCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillAddM3uaEpState()
 *
 *     Desc:  Fillup Add End point states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddM3uaEpState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_LSPCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillAddSctpTsapState()
 *
 *     Desc:  Fillup Add End point TSAP states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddSctpTsapState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCT_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_LSPCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}





/******************************************************************************
 *
 *     Fun:   fillAddSctpSctsapState()
 *
 *     Desc:  Fillup Add End point SCTSAP states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillAddSctpSctsapState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCT_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_USPCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   fillDelMtp3LinkState()
 *
 *     Desc:  Fillup Delete MTP3 Link states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelMtp3LinkState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DELLNK;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillDelLdfMtp3LinkState()
 *
 *     Desc:  Fillup Delete MTP3 Link states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelLdfMtp3LinkState(
      INGwSmIntVector &aeStateVector) {

        int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_LDF_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DELLNK;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillDelMtp2LinkState()
 *
 *     Desc:  Fillup Delete MTP2 Link states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelMtp2LinkState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_MTP2_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DELLNK;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillDelMtp3LinksetState()
 *
 *     Desc:  Fillup Delete MTP3 LinkSet states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelMtp3LinkSetState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DELLNKSET;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillDelM3uaNwkState()
 *
 *     Desc:  Fillup Delete M3UA Network states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelM3uaNwkState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DELNWK;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillDelSccpNwkState()
 *
 *     Desc:  Fillup Delete SCCP Network states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelSccpNwkState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DELNWK;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   fillDelSccpUpState()
 *
 *     Desc:  Fillup Delete SCCP User Part states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelSccpUpState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_UP;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillDelM3uaUpState()
 *
 *     Desc:  Fillup Delete M3UA User Part states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelM3uaUpState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_UP;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillDelLdfM3uaUpState()
 *
 *     Desc:  Fillup Delete LDF M3UA User Part states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelLdfM3uaUpState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_LDF_M3UA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_UP;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   fillDelMtp3UpState()
 *
 *     Desc:  Fillup Delete MTP3 User Part states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelMtp3UpState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_UP;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillDelLdfMtp3UpState()
 *
 *     Desc:  Fillup Delete LDF MTP3 User Part states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelLdfMtp3UpState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_LDF_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_UP;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillDelSccpRSsnState()
 *
 *     Desc:  Fillup Delete SCCP Remote SSN states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelSccpRSsnState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_RSSN;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillDelSccpLSsnState()
 *
 *     Desc:  Fillup Delete SCCP LOCAL SSN states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelSccpLSsnState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_LSSN;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   fillDelTcapLsapState()
 *
 *     Desc:  Fillup Delete TCAP Lower Sap states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelTcapLsapState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_TCA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_LSAP;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}

/******************************************************************************
 *        
 *     Fun:   fillDelTcapUsapState()
 *    
 *     Desc:  Fillup Delete TCAP Upper Sap states for Cfg
 *        
 *     Notes: None
 *        
 *     File:  INGwSmRepository.C
 *      
 *******************************************************************************/
        
int INGwSmRepository::fillDelTcapUsapState(
      INGwSmIntVector &aeStateVector) {
          
    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;
          
    liLayer = BP_AIN_SM_TCA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_USAP;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillDelM3uaRouteState()
 *
 *     Desc:  Fillup Delete M3UA Route states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelM3uaRouteState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_RTE;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}


/******************************************************************************
 *        
 *     Fun:   fillDelMtp3RouteState()
 *    
 *     Desc:  Fillup Delete MTP3 Route states for Cfg
 *        
 *     Notes: None
 *        
 *     File:  INGwSmRepository.C
 *      
 *******************************************************************************/
        
int INGwSmRepository::fillDelMtp3RouteState(
      INGwSmIntVector &aeStateVector) {
          
    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;
        
    liLayer = BP_AIN_SM_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_RTE;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillDelSccpRouteState()
 *
 *     Desc:  Fillup Delete SCCP Route states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelSccpRouteState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_RTE;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   fillDelM3uaEpState()
 *
 *     Desc:  Fillup Delete M3UA End Point states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelM3uaEpState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_EP;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}




/******************************************************************************
 *
 *     Fun:   fillDelSctpEpState()
 *
 *     Desc:  Fillup Delete SCTP End point states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelSctpEpState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCT_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_EP;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}




/******************************************************************************
 *
 *     Fun:   fillDelTuclEpState()
 *
 *     Desc:  Fillup Delete TUCL End Point states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelTuclEpState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_TUC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_EP;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   fillDelM3uaAspState()
 *
 *     Desc:  Fillup Delete M3UA ASP states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelM3uaAspState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_ASP;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   fillDelM3uaAsState()
 *
 *     Desc:  Fillup Delete M3UA AS states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelM3uaAsState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_AS;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   fillDelM3uaAsRteState()
 *
 *     Desc:  Fillup Delete M3UA AS route states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/

int INGwSmRepository::fillDelM3uaAsRteState(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_DEL_RTE;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

        return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getStateTransitionList()
 * 
 *     Desc:  This function returns state vector for a scenario
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getStateTransitionList (int aiScenario, INGwSmIntVector &aeStateVector)
{
    logger.logINGwMsg(false,TRACE_FLAG,0,
        "Entering INGwSmRepository::getStateTransitionList <%d>", aiScenario);

    if (!aeStateVector.empty())
      aeStateVector.clear();

    int liState = 0;

    int liLayer, liLayerOp, liOper;

    switch (aiScenario)
    {
      case BP_AIN_SM_CCM_ADD_M3UA_NETWORK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_M3UA_NETWORK");
          fillAddM3uaNetworkState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_ADD_SCCP_NETWORK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_SCCP_NETWORK");
          fillAddSccpNetworkState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_ADD_SCCP_GTADDR_MAP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_SCCP_GTADDR_MAP");
          fillAddSccpGtAddrMapState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_ADD_SCCP_GTRULE:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_SCCP_GTRULE");
          fillAddSccpGtRuleState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_SCCP_GTADDR_MAP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_SCCP_GTADDR_MAP");
          fillDelSccpGtAddrMapState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_SCCP_GTRULE:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_SCCP_GTRULE");
          fillDelSccpGtRuleState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_M3UA_NETWORK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_M3UA_NETWORK");
          fillDelM3uaNwkState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_SCCP_NETWORK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_SCCP_NETWORK");
          fillDelSccpNwkState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_SCCP_UP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_SCCP_UP");
          fillDelSccpUpState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_M3UA_UP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_M3UA_UP");
          fillDelM3uaUpState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_MTP3_UP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_MTP3_UP");
          fillDelMtp3UpState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_LDF_M3UA_UP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_LDF_M3UA_UP");
          fillDelLdfM3uaUpState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_LDF_MTP3_UP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_LDF_MTP3_UP");
          fillDelLdfMtp3UpState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_SCCP_RSSN:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_SCCP_RSSN");
          fillDelSccpRSsnState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_SCCP_LSSN:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_SCCP_LSSN");
          fillDelSccpLSsnState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_TCAP_LSAP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_TCAP_LSAP");
          fillDelTcapLsapState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_DEL_TCAP_USAP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_TCAP_USAP");
          fillDelTcapUsapState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_ADD_LNKSET:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_LNKSET");
          fillAddLnkSetState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_MOD_LNKSET:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_MOD_LNKSET");
          fillAddLnkSetState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_ADD_MTP3_ROUTE:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_MTP3_ROUTE");
          fillAddMtp3RouteState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_ADD_SCCP_ROUTE:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_SCCP_ROUTE");
          fillAddSccpRouteState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_OPEN_EP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_OPEN_EP");
          
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_OPENEP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }

      case BP_AIN_SM_CCM_M3UA_SCT_BND:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_SCT_BND");
          
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENASAP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }
      case BP_AIN_SM_CCM_M3UA_NSAP_BND:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_NSAP_BND");
          
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENASAP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }

      case BP_AIN_SM_CCM_DIS_SCCP_LSSN:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DIS_SCCP_LSSN");
          
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISSAP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }

      case BP_AIN_SM_CCM_DIS_MTP3_SAP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DIS_MTP3_SAP");
          
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISSAP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }

       case BP_AIN_SM_CCM_DIS_M3UA_SAP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DIS_M3UA_SAP");
          
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISSAP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }

       case BP_AIN_SM_CCM_DIS_SCT_SAP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DIS_SCT_SAP");
          
          liLayer = BP_AIN_SM_SCT_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISSAP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }

       case BP_AIN_SM_CCM_DIS_TUC_SAP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DIS_TUC_SAP");
          
          liLayer = BP_AIN_SM_TUC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISSAP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }

       case BP_AIN_SM_CCM_DIS_TCAP_USAP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DIS_TCAP_USAP");
          
          liLayer = BP_AIN_SM_TCA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISUSAP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }

       case BP_AIN_SM_CCM_DIS_TCAP_LSAP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DIS_TCAP_LSAP");
          
          liLayer = BP_AIN_SM_TCA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISLSAP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }

      case BP_AIN_SM_CCM_SCCP_BND:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_SCCP_BND");
          
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENASAP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }
       case BP_AIN_SM_CCM_SCTP_BND:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_SCTP_BND");
          
          liLayer = BP_AIN_SM_SCT_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENASAP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }


      case BP_AIN_SM_CCM_ADD_SCCP_L_SSN:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_SCCP_L_SSN");
          fillAddSccpLocalSsnState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_ADD_TCAP_USAP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_TCAP_USAP");
          fillAddTcapLocalSsnUsapState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_ADD_TCAP_LSAP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_TCAP_LSAP");
          fillAddTcapLocalSsnLsapState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_ADD_M3UA_AS:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_M3UA_AS");
          fillAddM3uaAsState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_MOD_M3UA_AS:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_MOD_M3UA_AS");
          fillAddM3uaAsState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_ADD_M3UA_ROUTE:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_M3UA_ROUTE");
          fillAddM3uaRouteState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_ADD_M3UA_ASP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_M3UA_ASP");
          fillAddM3uaAspState(aeStateVector);
          break;
        }
      case BP_AIN_SM_CCM_ADD_M3UA_EP:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_M3UA_EP");
          fillAddM3uaEpState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_ADD_SCTP_TSAP:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_SCTP_TSAP ");
          fillAddSctpTsapState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_ADD_SCTP_SCTSAP:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_SCTP_SCTSAP");
          fillAddSctpSctsapState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_ADD_TUCL_EP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_TUC_EP");

          liLayer = BP_AIN_SM_TUC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_USPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_DEL_MTP3_LINK:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_MTP3_LINK");
          fillDelMtp3LinkState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_DEL_LDF_MTP3_LINK:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_LDF_MTP3_LINK");
          fillDelLdfMtp3LinkState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_DEL_MTP2_LINK:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_MTP2_LINK");
          fillDelMtp2LinkState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_DEL_MTP3_LINKSET:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_MTP3_LINKSET");
          fillDelMtp3LinkSetState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_DEL_M3UA_RTE:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_M3UA_RTE");
          fillDelM3uaRouteState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_DEL_MTP3_RTE:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_MTP3_RTE");
          fillDelMtp3RouteState(aeStateVector);
          break;
        }

      case BP_AIN_SM_CCM_DEL_SCCP_RTE:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_SCCP_RTE");
          fillDelSccpRouteState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_DEL_M3UA_EP:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_M3UA_EP");
          fillDelM3uaEpState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_DEL_SCTP_EP:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_SCTP_EP");
          fillDelSctpEpState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_DEL_TUCL_EP:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_TUCL_EP");
          fillDelTuclEpState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_DEL_M3UA_AS:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_M3UA_AS");
          fillDelM3uaAsState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_DEL_M3UA_ASP:
	      {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_M3UA_ASP");
          fillDelM3uaAspState(aeStateVector);
          break;
	      }
      case BP_AIN_SM_CCM_DEL_SCTP_T_EP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_DEL_SCTP_T_EP");
          liLayer = BP_AIN_SM_SCT_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DEL_T_EP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);

          break;
        }
      case BP_AIN_SM_CCM_ADD_MTP3_LNK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_MTP3_LNK");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_LSPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_ADD_LDF_MTP3_LNK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_LDF_MTP3_LNK");
          liLayer = BP_AIN_SM_LDF_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_LSPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_ADD_MTP2_LNK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_MTP2_LNK");
          liLayer = BP_AIN_SM_MTP2_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_USPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_MOD_MTP3_LNK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_MOD_MTP3_LNK");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_LSPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_MOD_LDF_MTP3_LNK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_MOD_LDF_MTP3_LNK");
          liLayer = BP_AIN_SM_LDF_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_LSPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_MOD_MTP2_LNK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_MOD_MTP2_LNK");
          liLayer = BP_AIN_SM_MTP2_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_USPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_ENABLE_MTP3_LINK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENABLE_MTP3_LINK");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENBLNK;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
         }
       case BP_AIN_SM_ENABLE_MTP2_LINK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENABLE_MTP2_LINK");
          liLayer = BP_AIN_SM_MTP2_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_USPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISABLE_MTP3_LINK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISABLE_MTP3_LINK");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISLNK;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
         }
       case BP_AIN_SM_DISABLE_MTP2_LINK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISABLE_MTP2_LINK");
          liLayer = BP_AIN_SM_MTP2_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_USPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_SG_ENABLE_NODE:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_SG_ENABLE_NODE");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENBNODE;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
         }
      case BP_AIN_SM_SG_ABORT_TRANS:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_SG_ABORT_TRANS");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ABORT_SG_TRANS;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_SG_DISABLE_NODE:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_SG_DISABLE_NODE");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISNODE;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
       case BP_AIN_SM_ENB_TCA_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_TCA_ALM_LEVEL");
          liLayer = BP_AIN_SM_TCA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_SCC_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_SCC_ALM_LEVEL");
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_MTP3_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_MTP3_ALM_LEVEL");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_MTP2_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_MTP2_ALM_LEVEL");
          liLayer = BP_AIN_SM_MTP2_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_M3U_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_M3U_ALM_LEVEL");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_SCT_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_SCT_ALM_LEVEL");
          liLayer = BP_AIN_SM_SCT_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_TUC_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_TUC_ALM_LEVEL");
          liLayer = BP_AIN_SM_TUC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_ENB_SG_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_SG_ALM_LEVEL");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_ENB_SH_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_SH_ALM_LEVEL");
          liLayer = BP_AIN_SM_SH_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_MR_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_MR_ALM_LEVEL");
          liLayer = BP_AIN_SM_MR_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
       case BP_AIN_SM_ENB_PSF_MTP3_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_PSF_MTP3_ALM_LEVEL");
          liLayer = BP_AIN_SM_PSF_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_PSF_M3UA_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_PSF_M3UA_ALM_LEVEL");
          liLayer = BP_AIN_SM_PSF_M3UA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_LDF_MTP3_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_LDF_MTP3_ALM_LEVEL");
          liLayer = BP_AIN_SM_LDF_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_LDF_M3UA_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_LDF_M3UA_ALM_LEVEL");
          liLayer = BP_AIN_SM_LDF_M3UA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_PSF_SCCP_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_PSF_SCCP_ALM_LEVEL");
          liLayer = BP_AIN_SM_PSF_SCCP_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_PSF_TCAP_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_PSF_TCAP_ALM_LEVEL");
          liLayer = BP_AIN_SM_PSF_TCAP_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
       case BP_AIN_SM_DIS_TCA_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DIS_TCA_ALM_LEVEL");
          liLayer = BP_AIN_SM_TCA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DIS_SCC_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DIS_SCC_ALM_LEVEL");
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DIS_MTP3_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DIS_MTP3_ALM_LEVEL");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DIS_MTP2_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DIS_MTP2_ALM_LEVEL");
          liLayer = BP_AIN_SM_MTP2_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DIS_M3U_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DIS_M3U_ALM_LEVEL");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DIS_SCT_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DIS_SCT_ALM_LEVEL");
          liLayer = BP_AIN_SM_SCT_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DIS_TUC_ALM_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DIS_TUC_ALM_LEVEL");
          liLayer = BP_AIN_SM_TUC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISALM;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
       case BP_AIN_SM_ENB_TCA_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_TCA_DBG_LEVEL");
          liLayer = BP_AIN_SM_TCA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_SCC_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_SCC_DBG_LEVEL");
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_MTP3_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_MTP3_DBG_LEVEL");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_MTP2_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_MTP2_DBG_LEVEL");
          liLayer = BP_AIN_SM_MTP2_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_M3U_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_M3U_DBG_LEVEL");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_SCT_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_SCT_DBG_LEVEL");
          liLayer = BP_AIN_SM_SCT_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_TUC_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_TUC_DBG_LEVEL");
          liLayer = BP_AIN_SM_TUC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_ENB_SG_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_SG_DBG_LEVEL");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_ENB_SH_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_SH_DBG_LEVEL");
          liLayer = BP_AIN_SM_SH_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_MR_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_MR_DBG_LEVEL");
          liLayer = BP_AIN_SM_MR_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_RY_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_RY_DBG_LEVEL");
          liLayer = BP_AIN_SM_RY_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_ENB_PSF_MTP3_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_PSF_MTP3_DBG_LEVEL");
          liLayer = BP_AIN_SM_PSF_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_PSF_M3UA_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_PSF_M3UA_DBG_LEVEL");
          liLayer = BP_AIN_SM_PSF_M3UA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_LDF_MTP3_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_LDF_MTP3_DBG_LEVEL");
          liLayer = BP_AIN_SM_LDF_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_LDF_M3UA_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_LDF_M3UA_DBG_LEVEL");
          liLayer = BP_AIN_SM_LDF_M3UA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_PSF_SCCP_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_PSF_SCCP_DBG_LEVEL");
          liLayer = BP_AIN_SM_PSF_SCCP_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_PSF_TCAP_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_PSF_TCAP_DBG_LEVEL");
          liLayer = BP_AIN_SM_PSF_TCAP_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_TCA_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_TCA_TRC_LEVEL");
          liLayer = BP_AIN_SM_TCA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_SCC_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_SCC_TRC_LEVEL");
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_MTP3_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_MTP3_TRC_LEVEL");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_MTP2_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_MTP2_TRC_LEVEL");
          liLayer = BP_AIN_SM_MTP2_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_M3U_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_M3U_TRC_LEVEL");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_SCT_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_SCT_TRC_LEVEL");
          liLayer = BP_AIN_SM_SCT_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENB_TUC_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENB_TUC_TRC_LEVEL");
          liLayer = BP_AIN_SM_TUC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_ENABLE_TCAP_SSN:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENABLE_TCAP_SSN");
          liLayer = BP_AIN_SM_TCA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENB_LSSN;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENABLE_SCC_USRPART:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENABLE_SCC_USRPART");
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ENB_USRPART;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENABLE_M3UA_EP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENABLE_M3UA_EP");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_M3U_ENB_EP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_ENABLE_SCTP_EP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_ENABLE_SCTP_EP");
          liLayer = BP_AIN_SM_SCT_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_SCT_ENB_EP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_TCA_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_TCA_DBG_LEVEL");
          liLayer = BP_AIN_SM_TCA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_SCC_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_SCC_DBG_LEVEL");
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_MTP3_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_MTP3_DBG_LEVEL");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_MTP2_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_MTP2_DBG_LEVEL");
          liLayer = BP_AIN_SM_MTP2_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_M3U_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_M3U_DBG_LEVEL");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_SCT_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_SCT_DBG_LEVEL");
          liLayer = BP_AIN_SM_SCT_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_TUC_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_TUC_DBG_LEVEL");
          liLayer = BP_AIN_SM_TUC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_SG_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_SG_DBG_LEVEL");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_DISB_SH_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_SH_DBG_LEVEL");
          liLayer = BP_AIN_SM_SH_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_MR_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_MR_DBG_LEVEL");
          liLayer = BP_AIN_SM_MR_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_RY_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_RY_DBG_LEVEL");
          liLayer = BP_AIN_SM_RY_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_DISB_PSF_MTP3_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_PSF_MTP3_DBG_LEVEL");
          liLayer = BP_AIN_SM_PSF_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_PSF_M3UA_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_PSF_M3UA_DBG_LEVEL");
          liLayer = BP_AIN_SM_PSF_M3UA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_LDF_MTP3_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_LDF_MTP3_DBG_LEVEL");
          liLayer = BP_AIN_SM_LDF_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_LDF_M3UA_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_LDF_M3UA_DBG_LEVEL");
          liLayer = BP_AIN_SM_LDF_M3UA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_PSF_SCCP_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_PSF_SCCP_DBG_LEVEL");
          liLayer = BP_AIN_SM_PSF_SCCP_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_PSF_TCAP_DBG_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_PSF_TCAP_DBG_LEVEL");
          liLayer = BP_AIN_SM_PSF_TCAP_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISDBG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_DISB_TCA_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_TCA_TRC_LEVEL");
          liLayer = BP_AIN_SM_TCA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISTRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_SCC_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_SCC_TRC_LEVEL");
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISTRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_MTP3_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_MTP3_TRC_LEVEL");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISTRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_MTP2_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_MTP2_TRC_LEVEL");
          liLayer = BP_AIN_SM_MTP2_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISTRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_M3U_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_M3U_TRC_LEVEL");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISTRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_SCT_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_SCT_TRC_LEVEL");
          liLayer = BP_AIN_SM_SCT_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISTRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISB_TUC_TRC_LEVEL:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISB_TUC_TRC_LEVEL");
          liLayer = BP_AIN_SM_TUC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DISTRC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_DISABLE_TCAP_SSN:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISABLE_TCAP_SSN");
          liLayer = BP_AIN_SM_TCA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DIS_LSSN;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISABLE_SCC_USRPART:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISABLE_SCC_USRPART");
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_DIS_USRPART;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISABLE_M3UA_EP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISABLE_M3UA_EP");
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_M3U_DIS_EP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_DISABLE_SCTP_EP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_DISABLE_SCTP_EP");
          liLayer = BP_AIN_SM_SCT_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_SCT_DIS_EP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_CCM_ADD_MTP3_USERPART:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_MTP3_USERPART");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_USPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_CCM_ADD_M3UA_USERPART:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_M3UA_USERPART");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_USPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_CCM_ADD_SCCP_USERPART:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_SCCP_USERPART");
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_LSPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_ADD_LDF_MTP3_USERPART:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_LDF_MTP3_USERPART");
          liLayer = BP_AIN_SM_LDF_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_USPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_ADD_LDF_M3UA_USERPART:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_ADD_LDF_M3UA_USERPART");
          liLayer = BP_AIN_SM_LDF_M3UA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CFG;
          liOper  = BP_AIN_SM_SUBTYPE_USPCFG;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_M3UA_ASSOC_UP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_ASSOC_UP");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ESTASS;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_M3UA_ASSOC_DOWN:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_ASSOC_DOWN");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_TRMASS;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
#ifdef INC_DLG_AUDIT
      case BP_AIN_SM_CCM_AUDIT_INC:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_ASSOC_DOWN");
          liLayer = BP_AIN_SM_TCA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_AUDIT;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
#endif

      case BP_AIN_SM_CCM_M3UA_ASSOC_ABRT:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_ASSOC_ABRT");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_ABRTASS;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      
      case BP_AIN_SM_CCM_M3UA_ASP_UP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_ASP_UP");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_SNDAUP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_M3UA_ASP_DOWN:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_ASP_DOWN");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_SNDADN;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_M3UA_ASP_ACTIVE:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_ASP_ACTIVE");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_SNDAAC;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

#ifdef INC_ASP_SNDDAUD
      case BP_AIN_SM_CCM_M3UA_SNDDAUD:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_SNDDAUD");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_SNDDAUD;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
#endif

      case BP_AIN_SM_CCM_M3UA_UNBIND:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_UNBIND");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_M3UUBND;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      
      case BP_AIN_SM_CCM_M3UA_ASP_INACTIVE:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_ASP_INACTIVE");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_CTL;
          liOper  = BP_AIN_SM_SUBTYPE_SNDAIA;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_MTP3_STA_LINK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_MTP3_STA_LINK");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_LINK;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_MTP2_STA_LINK:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_MTP2_STA_LINK");
          liLayer = BP_AIN_SM_MTP2_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_LINK;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_CCM_MTP3_STA_LINKSET:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_MTP3_STA_LINKSET");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_LINKSET;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_SCCP_STA_ROUTE:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_SCCP_STA_ROUTE");
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_ROUTE;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_M3UA_STA_PS:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_STA_PS");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_PS;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }
      case BP_AIN_SM_CCM_M3UA_STA_PSP:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3UA_STA_PSP");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_PSP;

          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        }

      case BP_AIN_SM_CCM_TCA_STA_NODE:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_TCA_STA_NODE");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_NODE;
        
          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        } 

      case BP_AIN_SM_CCM_SCC_STA_NODE:
        { 
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_SCC_STA_NODE");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_NODE;
          
          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        } 

        case BP_AIN_SM_CCM_MTP3_STA_NODE:
        { 
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_MTP3_STA_NODE");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_NODE;
          
          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        } 

        case BP_AIN_SM_CCM_MTP2_STA_NODE:
        { 
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_MTP2_STA_NODE");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_NODE;
          
          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        } 

        case BP_AIN_SM_CCM_M3U_STA_NODE:
        { 
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_M3U_STA_NODE");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_NODE;
          
          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        } 

        case BP_AIN_SM_CCM_SCT_STA_NODE:
        { 
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_SCT_STA_NODE");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_NODE;
          
          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        } 


        case BP_AIN_SM_CCM_TUC_STA_NODE:
        { 
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_TUC_STA_NODE");
          liLayer = BP_AIN_SM_SG_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STA;
          liOper  = BP_AIN_SM_SUBTYPE_STA_NODE;
          
          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        } 

        case BP_AIN_SM_CCM_STS_TCA:
        { 
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_STS_TCA");
          liLayer = BP_AIN_SM_TCA_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STS;
          liOper  = BP_AIN_SM_SUBTYPE_STS;
          
          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        } 

        case BP_AIN_SM_CCM_STS_SCC:
        { 
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_STS_SCC");
          liLayer = BP_AIN_SM_SCC_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STS;
          liOper  = BP_AIN_SM_SUBTYPE_STS;
          
          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        } 

        case BP_AIN_SM_CCM_STS_MTP3:
        { 
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_STS_MTP3");
          liLayer = BP_AIN_SM_MTP3_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STS;
          liOper  = BP_AIN_SM_SUBTYPE_STS;
          
          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        } 

        case BP_AIN_SM_CCM_STS_M3U:
        { 
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_STS_M3U");
          liLayer = BP_AIN_SM_M3U_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STS;
          liOper  = BP_AIN_SM_SUBTYPE_STS;
          
          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        } 

        case BP_AIN_SM_CCM_STS_SCT:
        { 
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_STS_SCT");
          liLayer = BP_AIN_SM_SCT_LAYER;
          liLayerOp = BP_AIN_SM_OPTYPE_STS;
          liOper  = BP_AIN_SM_SUBTYPE_STS;
          
          liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
          aeStateVector.push_back (liState);
          break;
        } 




      case BP_AIN_SM_CCM_CS_RUNNING:
        {
          logger.logMsg (TRACE_FLAG, 0,"Filling state vector for scenario BP_AIN_SM_CCM_CS_RUNNING miTransportType = <%d>", miTransportType);
            fillCommonCfgStates(aeStateVector);

            if(TRANSPORT_TYPE_SIGTRAN == miTransportType) {
               logger.logMsg (TRACE_FLAG, 0,"Filling state vector TRANSPORT_TYPE_SIGTRAN");
              fillSigtranCfgStates(aeStateVector);
              //fillSigtranCtlSapStates(aeStateVector);
            }

            else if(TRANSPORT_TYPE_MTP == miTransportType){
               logger.logMsg (TRACE_FLAG, 0,"Filling state vector TRANSPORT_TYPE_MTP");
              fillMtpCfgStates(aeStateVector);
#ifdef _BIND_MTP_CS_RUNNING_
              /* fillMtpCtlSapStates(aeStateVector); INCTBD */
#endif /* _BIND_MTP_CS_RUNNING_ */
            }
            else{
              fillSigtranCfgStates(aeStateVector);
              logger.logMsg (TRACE_FLAG, 0,"Filling state vector both SS7/SIGTRAN");
              fillMtpCfgStates(aeStateVector);
            }
            /*putting psf-sccp gen configuration at last for testing */
            liLayer = BP_AIN_SM_PSF_SCCP_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CFG;
            liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);
            /*putting psf-TCAP gen configuration at last for testing */
            liLayer = BP_AIN_SM_PSF_TCAP_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CFG;
            liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);
            //fillCommonCtlSapStates(aeStateVector);
            //fillCommonCtlAdtStates(aeStateVector);

            if(TRANSPORT_TYPE_SIGTRAN == miTransportType) {
              //fillSigtranCtlAdtStates(aeStateVector);
            }
            else {
              //fillMtpCtlAdtStates(aeStateVector);
            }

            break;
        }
      case BP_AIN_SM_CCM_CS_STOPPED:
        {
            if(TRANSPORT_TYPE_SIGTRAN == miTransportType) {
              fillSigtranCtlStopStates(aeStateVector);
            }
            else {
              fillMtpCtlStopStates(aeStateVector);
            }

            break;
        }
      case BP_AIN_SM_CCM_ACTIVE:
        {
            if(TRANSPORT_TYPE_SIGTRAN == miTransportType) {
              liLayer = BP_AIN_SM_M3U_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ESTASS;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);

              liLayer = BP_AIN_SM_M3U_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_SNDAUP;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);

              liLayer = BP_AIN_SM_M3U_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_SNDAAC;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);
            }
            else {
            }

            break;
        }
      case BP_AIN_SM_CCM_ASP_ACTIVE:
        {
            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_ESTASS;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SNDAUP;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SNDAAC;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            break;
        }
      case BP_AIN_SM_CCM_ASP_UP:
        {
            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_ESTASS;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SNDAUP;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            break;
        }
      case BP_AIN_SM_CCM_EST_ASS:
        {
            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_ESTASS;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            break;
        }
      case BP_AIN_SM_CCM_ASP_INACTIVE:
        {
            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SNDAIA;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            break;
        }
      case BP_AIN_SM_CCM_ASP_DOWN:
        {
            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SNDAIA;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SNDADN;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            break;
        }
      case BP_AIN_SM_CCM_TRM_ASS:
        {
            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SNDAIA;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SNDADN;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_TRMASS;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            break;
        }
      case BP_AIN_SM_CCM_DBG_LEVEL:
        {
            liLayer = BP_AIN_SM_TCA_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            liLayer = BP_AIN_SM_SCC_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            if(TRANSPORT_TYPE_SIGTRAN == miTransportType) {
              liLayer = BP_AIN_SM_M3U_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);

              liLayer = BP_AIN_SM_SCT_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);

              liLayer = BP_AIN_SM_TUC_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);
            }
            else {
              liLayer = BP_AIN_SM_MTP3_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);

              liLayer = BP_AIN_SM_MTP2_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);

            }

            break;
        }
      case BP_AIN_SM_CCM_TRC_LEVEL:
        {
            liLayer = BP_AIN_SM_TCA_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            liLayer = BP_AIN_SM_SCC_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            if(TRANSPORT_TYPE_SIGTRAN == miTransportType) { 
              liLayer = BP_AIN_SM_M3U_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);

              liLayer = BP_AIN_SM_SCT_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);


              // commenting out TUCL trace request since TUCL doesn't print
              //anything in the traces currently.

#ifdef _ENABLE_TUCL_TRACE_
              liLayer = BP_AIN_SM_TUC_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);
#endif
            }
            else {
              liLayer = BP_AIN_SM_MTP3_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);
            }

            break;
        }
      case BP_AIN_SM_CCM_ALM_LEVEL:
        {
            liLayer = BP_AIN_SM_TCA_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            liLayer = BP_AIN_SM_SCC_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            if(TRANSPORT_TYPE_SIGTRAN == miTransportType) {
              liLayer = BP_AIN_SM_M3U_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);

              liLayer = BP_AIN_SM_SCT_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);

              liLayer = BP_AIN_SM_TUC_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);
            }
            else {
              liLayer = BP_AIN_SM_MTP3_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_CTL;
              liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);
            }

            break;
        }
      case BP_AIN_SM_CCM_VERSION:
        { 
            liLayer = BP_AIN_SM_TCA_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_STA;
            liOper  = BP_AIN_SM_SUBTYPE_SYSID;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            liLayer = BP_AIN_SM_SCC_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_STA;
            liOper  = BP_AIN_SM_SUBTYPE_SYSID;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            if(TRANSPORT_TYPE_SIGTRAN == miTransportType) {
              liLayer = BP_AIN_SM_M3U_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_STA;
              liOper  = BP_AIN_SM_SUBTYPE_SYSID;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState); 

              liLayer = BP_AIN_SM_SCT_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_STA;
              liOper  = BP_AIN_SM_SUBTYPE_SYSID;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);

              liLayer = BP_AIN_SM_TUC_LAYER;
              liLayerOp = BP_AIN_SM_OPTYPE_STA;
              liOper  = BP_AIN_SM_SUBTYPE_SYSID;

              liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
              aeStateVector.push_back (liState);
            }
            else {

              /*
               * GS
               * Commenting this out as the MTP3 sending is not implemented
               liLayer = BP_AIN_SM_MTP3_LAYER;
               liLayerOp = BP_AIN_SM_OPTYPE_STA;
               liOper  = BP_AIN_SM_SUBTYPE_SYSID;

               liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
               aeStateVector.push_back (liState);
               */
            }

            break;
        }
      case BP_AIN_SM_SYNC_PSP_STA:
        {
            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_STA;
            liOper  = BP_AIN_SM_SUBTYPE_PSPSTA;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            break;
        }

      case BP_AIN_SM_CCM_SHTDN_TCA:
        {
            liLayer = BP_AIN_SM_TCA_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SHTDN_TCA;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            break;
        }

      case BP_AIN_SM_CCM_SHTDN_SCC:
        {
            liLayer = BP_AIN_SM_SCC_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SHTDN_SCC;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            break;
        }

      case BP_AIN_SM_CCM_SHTDN_MTP3:
        {
            liLayer = BP_AIN_SM_MTP3_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SHTDN_MTP3;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            break;
        }

      case BP_AIN_SM_CCM_SHTDN_M3U:
        {
            liLayer = BP_AIN_SM_M3U_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SHTDN_M3U;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            break;
        }

      case BP_AIN_SM_CCM_SHTDN_SCT:
        {
            liLayer = BP_AIN_SM_SCT_LAYER;
            liLayerOp = BP_AIN_SM_OPTYPE_CTL;
            liOper  = BP_AIN_SM_SUBTYPE_SHTDN_SCT;

            liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
            aeStateVector.push_back (liState);

            break;
        }
      
      default:
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Scenario passed <%d>", aiScenario);
            return BP_AIN_SM_FAIL;
            break;
        }
    }

    logger.logINGwMsg(false,TRACE_FLAG,0,
        "Leaving INGwSmRepository::getStateTransitionList");

    return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   getSmLogFile()
 * 
 *     Desc:  This function returns stack manager log file
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    string
INGwSmRepository::getSmLogFile ()
{
    logger.logMsg (TRACE_FLAG, 0,
        "Invoked INGwSmRepository::getSmLogFile");

    return mstrSmLogFile;
}

/******************************************************************************
 *
 *     Fun:   getPspState()
 *
 *     Desc:  This function returns State for a PSP
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    INGwSmPspState *
INGwSmRepository::getPspState (int aiPspId)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getPspState for PSP <%d>", aiPspId);

    INGwSmPspStateMap::iterator iter = mePspStateMap.find (aiPspId);

    if (iter == mePspStateMap.end ())
    {
      logger.logMsg (ERROR_FLAG, 0,
            "PSP <%d> Could not be located in the PSP State Map", aiPspId);

      return 0;
    }

    logger.logMsg (TRACE_FLAG, 0,
        "PSP <%d> Is in State <%d, %d>", aiPspId, 
        iter->second->state, iter->second->monitor);

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getPspState");
    return iter->second;
}

/******************************************************************************
 *
 *     Fun:   setPspState()
 *
 *     Desc:  This function sets the state for a PSP
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::setPspState (int aiPspId, int aiPspState)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::setPspState for PSP <%d>", aiPspId);

    INGwSmPspStateMap::iterator iter = mePspStateMap.find (aiPspId);

    if (iter == mePspStateMap.end ())
    {
      logger.logMsg (ERROR_FLAG, 0,
            "PSP <%d> Could not be located in the PSP State Map", aiPspId);

      return BP_AIN_SM_FAIL;
    }

    if (iter->second->state == aiPspState)
    {
      logger.logMsg (WARNING_FLAG, 0,
            "PSP <%d> already in <%d> state", aiPspId, aiPspState);
      return BP_AIN_SM_OK;
    }

    logger.logMsg (ERROR_FLAG, 0,
        "PSP <%d> Is moving from State <%d> to <%d>", aiPspId,
        iter->second->state, aiPspState);

    iter->second->state = aiPspState;

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
		// Fetch IP and Port corresponding to Psp Id
		AddPsp psp;
		memset(&psp,0,sizeof(AddPsp));
		bool found = INGwSmBlkConfig::getInstance().getAsp (aiPspId, psp);

		string IPAddr;
		char buf[30];
		memset(&buf, 0, sizeof(buf));
		if(found)
    {
			if(psp.nmbAddr > 0)
			{
				if(psp.addr[0].type == CM_NETADDR_IPV6)
        {
					memcpy(buf, &psp.addr[0].u.ipv6NetAddr,
                    sizeof(psp.addr[0].u.ipv6NetAddr));
					IPAddr = buf;
				}
				else
            IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
			}
		}

    if (aiPspState == BP_AIN_SM_PSP_ST_DOWN && !IPAddr.empty())
    {
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
        __FILE__, __LINE__,
        INC_SM_ALM_ASP_DOWN, "ASP_DOWN", 0,
        " PSP Id<%d> IP<%s> Port<%d> Moved to State DOWN", 
        aiPspId, IPAddr.c_str(), psp.dstPort);
    }
    else if (aiPspState == BP_AIN_SM_PSP_ST_ESTASS && !IPAddr.empty())
    {
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
				__FILE__, __LINE__,
        INC_SM_ALM_ASSOC_EST_OK, "Assoc Establishment Success", 0,
        " PSP Id<%d> IP<%s> Port<%d> Moved to State ASSOCIATION ESTABLISHED", 
        aiPspId, IPAddr.c_str(), psp.dstPort);
    }
    else if (aiPspState == BP_AIN_SM_PSP_ST_ASPUP && !IPAddr.empty())
    {
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
            __FILE__, __LINE__,
            INC_SM_ALM_ASP_UP, "ASP_UP", 0,
            " PSP Id<%d> IP<%s> Port<%d> Moved to State ASP UP", 
            aiPspId, IPAddr.c_str(), psp.dstPort);
    }
    else if (aiPspState == BP_AIN_SM_PSP_ST_ASPAC && !IPAddr.empty())
    {
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
            __FILE__, __LINE__,
            INC_SM_ALM_ASP_ACTIVE, "ASP_UP", 0,
            " PSP Id<%d> IP<%s> Port<%d> Moved to State ASP ACTIVE", 
            aiPspId, IPAddr.c_str(), psp.dstPort);
    }
#endif

    logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmRepository::setPspState");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   addPspState()
 *
 *     Desc:  This function adds the state for a PSP
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::addPspState (int aiPspId, int aiPspState)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::addPspState for PSP <%d>", aiPspId);

    INGwSmPspStateMap::iterator iter = mePspStateMap.find (aiPspId);

    if (iter != mePspStateMap.end ())
    {
      logger.logMsg (ERROR_FLAG, 0,
            "PSP <%d> already exists in the PSP State Map", aiPspId);

      return BP_AIN_SM_FAIL;
    }

    INGwSmPspState *lpState = new INGwSmPspState;
    lpState->state = aiPspState;
    lpState->monitor = 0;

    mePspStateMap [aiPspId] = lpState;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::addPspState");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getDebugLevel()
 *
 *     Desc:  This function gets the debug level for a particular layer
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getDebugLevel (int aiLayer)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getDebugLevel Layer <%d>", aiLayer);

    switch (aiLayer)
    {
      case BP_AIN_SM_TCA_LAYER:
        {
            return miStDbgLevel;
            break;
        }
      case BP_AIN_SM_SCC_LAYER:
        {
            return miSpDbgLevel;
            break;
        }
      case BP_AIN_SM_M3U_LAYER:
        {
            return miItDbgLevel;
            break;
        }
      case BP_AIN_SM_SCT_LAYER:
        {
            return miSbDbgLevel;
            break;
        }
      case BP_AIN_SM_TUC_LAYER:
        {
            return miHiDbgLevel;
            break;
        }
      case BP_AIN_SM_MTP3_LAYER:
        {
            return miSnDbgLevel;
            break;
        }
      case BP_AIN_SM_MTP2_LAYER:
        {
            return miSdDbgLevel;
            break;
        }
      default:
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Uknown LayerId passed <%d>", aiLayer);
            break;
        }
    }


    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getDebugLevel");

    return -1;
}

/******************************************************************************
 *
 *     Fun:   getTrcLevel()
 *
 *     Desc:  This function gets the Trace level for a particular layer
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getTrcLevel (int aiLayer)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getTrcLevel Layer <%d>", aiLayer);

    switch (aiLayer)
    {
      case BP_AIN_SM_TCA_LAYER:
        {
            return miStTrcLevel;
            break;
        }
      case BP_AIN_SM_SCC_LAYER:
        {
            return miSpTrcLevel;
            break;
        }
      case BP_AIN_SM_M3U_LAYER:
        {
            return miItTrcLevel;
            break;
        }
      case BP_AIN_SM_SCT_LAYER:
        {
            return miSbTrcLevel;
            break;
        }
      case BP_AIN_SM_TUC_LAYER:
        {
            return miHiTrcLevel;
            break;
        }
      case BP_AIN_SM_MTP3_LAYER:
        {
            return miSnTrcLevel;
            break;
        }
      case BP_AIN_SM_MTP2_LAYER:
        {
            return miSdTrcLevel;
            break;
        }
      default:
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Uknown LayerId passed <%d>", aiLayer);
            break;
        }
    }


    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getTrcLevel");

    return -1;
}

/******************************************************************************
 *
 *     Fun:   getAlarmLevel()
 *
 *     Desc:  This function gets the Alarm level for a particular layer
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getAlarmLevel (int aiLayer)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getAlarmLevel Layer <%d>", aiLayer);

    switch (aiLayer)
    {
      case BP_AIN_SM_TCA_LAYER:
        {
            return miStAlmLevel;
            break;
        }
      case BP_AIN_SM_SCC_LAYER:
        {
            return miSpAlmLevel;
            break;
        }
      case BP_AIN_SM_M3U_LAYER:
        {
            return miItAlmLevel;
            break;
        }
      case BP_AIN_SM_SCT_LAYER:
        {
            return miSbAlmLevel;
            break;
        }
      case BP_AIN_SM_TUC_LAYER:
        {
            return miHiAlmLevel;
            break;
        }
      case BP_AIN_SM_MTP3_LAYER:
        {
            return miSnAlmLevel;
        }
      case BP_AIN_SM_MTP2_LAYER:
        {
            return miSdAlmLevel; 
        }
      default:
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Uknown LayerId passed <%d>", aiLayer);
            break;
        }
    }


    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getAlarmLevel");

    return -1;
}

/******************************************************************************
 *
 *     Fun:   setDebugLevel()
 *
 *     Desc:  This function sets the debug level for a particular layer
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::setDebugLevel (int aiLayer, int aiLevel)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::setDebugLevel Layer <%d> Level <%d>", 
        aiLayer, aiLevel);

    switch (aiLayer)
    {
      case BP_AIN_SM_TCA_LAYER:
        {
            miStDbgLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_SCC_LAYER:
        {
            miSpDbgLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_M3U_LAYER:
        {
            miItDbgLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_SCT_LAYER:
        {
            miSbDbgLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_TUC_LAYER:
        {
            miHiDbgLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_MTP3_LAYER:
        {
            miSnDbgLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_MTP2_LAYER:
        {
            miSdDbgLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_ALL_LAYER:
        {
            miIeDbgLevel = aiLevel;
            miStDbgLevel = aiLevel;
            miSpDbgLevel = aiLevel;
            miItDbgLevel = aiLevel;
            miSbDbgLevel = aiLevel;
            miHiDbgLevel = aiLevel;
            miSnDbgLevel = aiLevel;
            miSdDbgLevel = aiLevel;
            break;
        }
      default:
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Uknown LayerId passed <%d>", aiLayer);
            return BP_AIN_SM_FAIL;
            break;
        }
    }


    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::setDebugLevel");

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   setTrcLevel()
 *
 *     Desc:  This function sets the Trace level for a particular layer
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::setTrcLevel (int aiLayer, int aiLevel)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::setTrcLevel Layer <%d> Level <%d>", 
        aiLayer, aiLevel);

    switch (aiLayer)
    {
      case BP_AIN_SM_TCA_LAYER:
        {
            miStTrcLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_SCC_LAYER:
        {
            miSpTrcLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_M3U_LAYER:
        {
            miItTrcLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_SCT_LAYER:
        {
            miSbTrcLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_TUC_LAYER:
        {
            miHiTrcLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_MTP3_LAYER:
        {
            miSnTrcLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_MTP2_LAYER:
        {
            miSdTrcLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_ALL_LAYER:
        {
            miIeTrcLevel = aiLevel;
            miStTrcLevel = aiLevel;
            miSpTrcLevel = aiLevel;
            miItTrcLevel = aiLevel;
            miSbTrcLevel = aiLevel;
            miHiTrcLevel = aiLevel;
            miSnTrcLevel = aiLevel;
            miSdTrcLevel = aiLevel;
            break;
        }
      default:
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Uknown LayerId passed <%d>", aiLayer);
            break;
        }
    }



    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::setTrcLevel");

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   setTransportType()
 *
 *     Desc:  This function sets the Transport Type (MTP/SIGTRAN)
 *            For the stack manager
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::setTransportType (int transportType) {

    miTransportType = transportType;
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   setAlarmLevel()
 *
 *     Desc:  This function sets the Alarm level for a particular layer
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::setAlarmLevel (int aiLayer, int aiLevel)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::setAlarmLevel Layer <%d> Level <%d>", 
        aiLayer, aiLevel);

    switch (aiLayer)
    {
      case BP_AIN_SM_TCA_LAYER:
        {
            miStAlmLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_SCC_LAYER:
        {
            miSpAlmLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_M3U_LAYER:
        {
            miItAlmLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_SCT_LAYER:
        {
            miSbAlmLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_TUC_LAYER:
        {
            miHiAlmLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_MTP3_LAYER:
        {
            miSnAlmLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_MTP2_LAYER:
        {
            miSdAlmLevel = aiLevel;
            break;
        }
      case BP_AIN_SM_ALL_LAYER:
        {
            miIeAlmLevel = aiLevel;
            miStAlmLevel = aiLevel;
            miSpAlmLevel = aiLevel;
            miItAlmLevel = aiLevel;
            miSbAlmLevel = aiLevel;
            miHiAlmLevel = aiLevel;
            miSnAlmLevel = aiLevel;
            miSdAlmLevel = aiLevel;
            break;
        }
      default:
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Uknown LayerId passed <%d>", aiLayer);
            break;
        }
    }


    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::setAlarmLevel");

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   getDumpMemory()
 *
 *     Desc:  get the flag for dumping stack memory
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    bool
INGwSmRepository::getDumpMemory ()
{
    return mbDumpMemory;
}

/******************************************************************************
 *
 *     Fun:   getPspId()
 *
 *     Desc:  get the PSP Id
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getPspId (INGwSmRequestContext *apContext, int &aiIndex)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getPspId");

    /*
   * First check if the Request Context was passed and get
   * the PSP Id from it. PSP=0 means all layers
   */ 

    int liPspId = 0;

    if (apContext)
      liPspId = apContext->pspId;

    /*
   * if the PSPId is not equal to zero than check the state
   * of the PSP. If it is in establish state then just move to
   * SEND_NEXT
   */   

    if (liPspId == 0)
    { 
      /*
       * get the PSP Id from Repository
       */
      vector <INGwSmPeerId> &lrVec = getPSPIdList ();

      if (lrVec.empty())
      {
        logger.logMsg (ERROR_FLAG, 0,
              "No PSP Id Configured");
        return -1;
      } 

      if (aiIndex == -1)
        aiIndex = 0;

      if (aiIndex > 0 && aiIndex >= lrVec.size())
      { 
        return 0;
      }

      liPspId = lrVec[aiIndex];
    }

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getPspId");
    return liPspId;
}

/******************************************************************************
 *
 *     Fun:   fillMtpCtlAdtStates()
 *
 *     Desc:  Fillup SIGTRAN states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::fillMtpCtlStopStates(
      INGwSmIntVector &aeStateVector) {

    logger.logMsg (TRACE_FLAG, 0,
        "IN fillMtpCtlStopStates");

    logger.logMsg (TRACE_FLAG, 0,
        "OUT fillMtpCtlStopStates");

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillMtpCtlAdtStates()
 *
 *     Desc:  Fillup SIGTRAN states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::fillMtpCtlAdtStates(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    logger.logMsg (TRACE_FLAG, 0,
        "IN fillMtpCtlAdtStates");

    liLayer = BP_AIN_SM_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENAALM;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENADBG;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENATRC;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);


    liLayer = BP_AIN_SM_MTP2_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);


    logger.logMsg (TRACE_FLAG, 0,
        "OUT fillMtpCtlAdtStates");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillMtpCtlSapStates()
 *
 *     Desc:  Fillup SIGTRAN states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::fillMtpCtlSapStates(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    logger.logMsg (TRACE_FLAG, 0,
        "IN fillMtpCtlSapStates");

    liLayer = BP_AIN_SM_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENASAP;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    logger.logMsg (TRACE_FLAG, 0,
        "OUT fillMtpCtlSapStates");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillMtpCfgStates()
 *
 *     Desc:  Fillup SIGTRAN states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::fillMtpCfgStates(
      INGwSmIntVector &aeStateVector) {
          logger.logMsg (TRACE_FLAG, 0,"Entering INGwSmRepository::fillMtpCfgStates");

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    logger.logMsg (TRACE_FLAG, 0,
        "IN fillMtpCfgStates");

    liLayer = BP_AIN_SM_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_PSF_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_PSF_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_PSF_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_PSF_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_LDF_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_LDF_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_LDF_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_LDF_MTP3_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_MTP2_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

          logger.logMsg (TRACE_FLAG, 0,"Leaving  INGwSmRepository::fillMtpCfgStates");
    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillSigtranCfgStates()
 *
 *     Desc:  Fillup SIGTRAN states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::fillSigtranCfgStates(
      INGwSmIntVector &aeStateVector) {

          logger.logMsg (TRACE_FLAG, 0,"Entering INGwSmRepository::fillSigtranCfgStates");
    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);
    
    liLayer = BP_AIN_SM_LDF_M3UA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_LDF_M3UA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_LDF_M3UA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_LDF_M3UA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL;
    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_PSF_M3UA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);
    
    liLayer = BP_AIN_SM_PSF_M3UA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_PSF_M3UA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_PSF_M3UA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SCT_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_TUC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);


          logger.logMsg (TRACE_FLAG, 0,"Leaving INGwSmRepository::fillSigtranCfgStates");
    return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   fillCommonCfgStates()
 *
 *     Desc:  Fillup common states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::fillCommonCfgStates( INGwSmIntVector &aeStateVector) {

    logger.logINGwMsg(false,ALWAYS_FLAG,0,"IN fillCommonCfgStates");

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_TCA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SH_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);
    
    liLayer = BP_AIN_SM_MR_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SG_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SG_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_ENT_SG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SG_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_ENT_SCCP;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SG_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_ENT_TCAP;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SG_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_ENT_TUCL;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SG_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_ENT_SCTP;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);


    liLayer = BP_AIN_SM_SG_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_ENT_M3UA;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SG_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_ENT_MTP3;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);


    liLayer = BP_AIN_SM_SG_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CFG;
    liOper  = BP_AIN_SM_SUBTYPE_ENT_MTP2;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    if(!INGwSmBlkConfig::getInstance().getMode()) {
      
      logger.logMsg (ALWAYS_FLAG, 0,
        "IN fillCommonCfgStates: No Peer INGw found. NOT CONFIGURING RELAY");
    }
    else{
      // More than one INGw

      // If relayChannelRole is 1, then relay needs to be configured
      // for server and listen channels. If relayChannelRole is 2 then relay
      // needs to be configured for client channel.

      // EMS assign the designated primary INGw the lowest subsystem number
      // and the procIdList contains the procId as per sorted Subsystem number
      // of all INGws

      int relayChannelRole = 
				INGwSmBlkConfig::getInstance().getRelayChannelRole();

      logger.logMsg (ALWAYS_FLAG, 0, 
										"++VER++relayChannelRole[%d]", relayChannelRole);

      if(relayChannelRole == 1) {
        // relay needs to be configured for server and listen channels
        liLayer = BP_AIN_SM_RELAY_LAYER;
        liLayerOp = BP_AIN_SM_OPTYPE_CFG;
        liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
        aeStateVector.push_back (liState);

        liLayer = BP_AIN_SM_RELAY_LAYER;
        liLayerOp = BP_AIN_SM_OPTYPE_CFG;
        liOper  = BP_AIN_SM_SUBTYPE_LIS_CHANCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
        aeStateVector.push_back (liState);

        liLayer = BP_AIN_SM_RELAY_LAYER;
        liLayerOp = BP_AIN_SM_OPTYPE_CFG;
        liOper  = BP_AIN_SM_SUBTYPE_SRV_CHANCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
        aeStateVector.push_back (liState);
      }
      else {
        // relay needs to be configured for client channel
        liLayer = BP_AIN_SM_RELAY_LAYER;
        liLayerOp = BP_AIN_SM_OPTYPE_CFG;
        liOper  = BP_AIN_SM_SUBTYPE_GENCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
        aeStateVector.push_back (liState);

        liLayer = BP_AIN_SM_RELAY_LAYER;
        liLayerOp = BP_AIN_SM_OPTYPE_CFG;
        liOper  = BP_AIN_SM_SUBTYPE_CLI_CHANCFG;

        liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
        aeStateVector.push_back (liState);
      }
    }

    logger.logINGwMsg(false,ALWAYS_FLAG,0, "OUT fillCommonCfgStates");

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillSigtranCtlSapStates()
 *
 *     Desc:  Fillup common states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::fillSigtranCtlSapStates(
      INGwSmIntVector &aeStateVector) {

    int liState = 0; 
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCT_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENASAP;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENASAP;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_OPENEP;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillCommonCtlAdtStates()
 *
 *     Desc:  Fillup common states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::fillCommonCtlAdtStates(
      INGwSmIntVector &aeStateVector) {

    logger.logMsg (TRACE_FLAG, 0,
        "IN fillCommonCtlAdtStates");


    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_TCA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_TCA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_TCA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    logger.logMsg (TRACE_FLAG, 0,
        "OUT fillCommonCtlAdtStates");


    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillCommonCtlSapStates()
 *
 *     Desc:  Fillup common states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::fillCommonCtlSapStates(
      INGwSmIntVector &aeStateVector) {

    logger.logMsg (TRACE_FLAG, 0,
        "IN fillCommonCtlSapStates");


    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SCC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENASAP;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_TCA_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENASAP;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    logger.logMsg (TRACE_FLAG, 0,
        "OUT fillCommonCtlSapStates");


    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillSgCtlStates()
 *
 *     Desc:  Fillup common states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::fillSgCtlStates(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_SG_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENAENT;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    return BP_AIN_SM_OK;
}
/******************************************************************************
 *
 *     Fun:   fillSigtranCtlAdtStates()
 *
 *     Desc:  Fillup common states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::fillSigtranCtlAdtStates(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SCT_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SCT_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_SCT_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_TUC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENAALM;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_TUC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENADBG;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    // commenting out TUCL trace request since TUCL doesn't print
    //anything in the traces currently.

#ifdef _ENABLE_TUCL_TRACE_
    liLayer = BP_AIN_SM_TUC_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_ENATRC;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);
#endif

    return BP_AIN_SM_OK;
}

/******************************************************************************
 *
 *     Fun:   fillSigtranCtlStopStates()
 *
 *     Desc:  Fillup common states for Cfg
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
int
INGwSmRepository::fillSigtranCtlStopStates(
      INGwSmIntVector &aeStateVector) {

    int liState = 0;
    int liLayer;
    int liLayerOp;
    int liOper;

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_SNDAIA;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_SNDADN;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    liLayer = BP_AIN_SM_M3U_LAYER;
    liLayerOp = BP_AIN_SM_OPTYPE_CTL;
    liOper  = BP_AIN_SM_SUBTYPE_TRMASS;

    liState = (liLayer << 24) + (liLayerOp << 16) + liOper;
    aeStateVector.push_back (liState);

    return BP_AIN_SM_OK;
}

/*
 *
 * getMtp2DlsapParams
 *
 */

int
INGwSmRepository::getMtp2DlsapParams(
      int& devId,
      int& dlSapId,
      int& channelNum,
      int aiIndex) {

    DOMNode* lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"mtp2",
        (char*)"lsap");

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <mtp2:%s>", (char*)"lsap");
      return BP_AIN_SM_FAIL;
    }

    lpNode = lpNode->getFirstChild();

    char* lpName  = new char[100];
    char* lpValue = new char[100];
    bool indexFound = false;

    INGwSmLinkInfo *lpLinkInfo = new INGwSmLinkInfo;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());

        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (strcmp (lpName, "devId") == 0) {
                devId = atoi (lpValue);
                lpLinkInfo->devId = devId;
              }
              else if(strcmp (lpName, "dlSapId") == 0) {
                dlSapId = atoi (lpValue);
                lpLinkInfo->sapId = dlSapId;
              }
              else if(strcmp (lpName, "channelNum") == 0) {
                channelNum = atoi (lpValue);
                lpLinkInfo->channelNum = channelNum;
              }
              else {
              }

            }//getCfgOid
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for MTP2", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;


    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");

      delete lpLinkInfo;
      return BP_AIN_SM_INDEX_OVER;
    }

    addSdChannelInfo (lpLinkInfo);

    return BP_AIN_SM_OK;
}

/*
 *
 * getMtp2NmbLinks
 *
 */
int
INGwSmRepository::getMtp2NmbLinks(
      int& numberOfLinks) {

    DOMNode* lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"mtp2",
        (char*)"gen");

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <mtp2:%s>", (char*)"gen");
      return BP_AIN_SM_FAIL;
    }

    lpNode = lpNode->getFirstChild();

    char* lpName  = new char[100];
    char* lpValue = new char[100];
    bool indexFound = false;
    int aiIndex = -1;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());

        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (strcmp (lpName, "numberOfLinks") == 0)
              {
                numberOfLinks = atoi (lpValue);
              }

            }//getCfgOid
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for MTP2", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }

    return BP_AIN_SM_OK;
}



/******************************************************************************
 *
 *     Fun:   getDnMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for MTP3
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getDnMngmt (int aiType, LdnMngmt &aeMgmt, int aiIndex, int* apiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getDnMngmt");

    int result = BP_AIN_SM_OK;
    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode* lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"ldf-mtp3", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <ldf-mtp3:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    cmMemset((U8 *)&aeMgmt, '\0', sizeof(LdnMngmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;


    lpNode = lpNode->getFirstChild();

    char *lpName = new char [100];
    char *lpValue = new char [100];

    bool indexFound = false;
    int liMonitorResource = 0;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "protEnt") == 0)
                {
                    aeMgmt.t.cfg.s.gen.protEnt = atoi (lpValue);    
                }
                else if (strcmp (lpName, "protInst") == 0)
                {
                    aeMgmt.t.cfg.s.gen.protInst = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxUpperSaps") == 0)
                {
                    aeMgmt.t.cfg.s.gen.maxUpperSaps = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxLowerSaps") == 0)
                {
                    aeMgmt.t.cfg.s.gen.maxLowerSaps = atoi (lpValue);    
                }
                else if (strcmp (lpName, "rsetGenCfg") == 0)
                {
                    getZnDnRsetGen (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.gen.rsetCfg));
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL)
              {
                if (strcmp (lpName, "cmFthaRsetType") == 0)
                {
                    getAttrVal ((DOMElement*) lpNode, (char*)"type", lpValue);
                    aeMgmt.t.cfg.s.rset.rType.type = atoi (lpValue);
                    getAttrVal ((DOMElement*) lpNode, (char*)"qual", lpValue);
                    aeMgmt.t.cfg.s.rset.rType.qual = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                       "type=<%d>,qual=<%d>", aeMgmt.t.cfg.s.rset.rType.type,
                        aeMgmt.t.cfg.s.rset.rType.qual);
                }
                else if (strcmp (lpName, "id") == 0)
                {
                    aeMgmt.t.cfg.s.rset.u.crit.id = atoi (lpValue);
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF)
              {
                if (strcmp (lpName, "cmFthaRsetType") == 0)
                {
                    getAttrVal ((DOMElement*) lpNode, (char*)"type", lpValue);
                    aeMgmt.t.cfg.s.rset.rType.type = atoi (lpValue);
                    getAttrVal ((DOMElement*) lpNode, (char*)"qual", lpValue);
                    aeMgmt.t.cfg.s.rset.rType.qual = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                       "type=<%d>,qual=<%d>", aeMgmt.t.cfg.s.rset.rType.type,
                        aeMgmt.t.cfg.s.rset.rType.qual);
                }
                else if (strcmp (lpName, "id") == 0)
                {
                    aeMgmt.t.cfg.s.rset.u.def.id = atoi (lpValue);
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL)
              {
                if (strcmp (lpName, "cmFthaRsetType") == 0)
                {
                    getAttrVal ((DOMElement*) lpNode, (char*)"type", lpValue);
                    aeMgmt.t.cfg.s.rset.rType.type = atoi (lpValue);
                    getAttrVal ((DOMElement*) lpNode, (char*)"qual", lpValue);
                    aeMgmt.t.cfg.s.rset.rType.qual = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                       "type=<%d>,qual=<%d>", aeMgmt.t.cfg.s.rset.rType.type,
                        aeMgmt.t.cfg.s.rset.rType.qual);
                }
                else if (strcmp (lpName, "ifType") == 0)
                {
                    aeMgmt.t.cfg.s.rset.u.nonCrit.ifType = atoi (lpValue);
                }
                else if (strcmp (lpName, "CmZnDnRsetMap_upper") == 0)
                {
                    getZnDnRsetMap (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.rset.u.nonCrit.upper));
                }
                else if (strcmp (lpName, "CmZnDnRsetMap_lower") == 0)
                {
                    getZnDnRsetMap (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.rset.u.nonCrit.lower));
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_USPCFG)
              {
                if (strcmp (lpName, "nmbSaps") == 0)
                {
                    aeMgmt.t.cfg.s.sap.nmbSaps = atoi (lpValue);
                }
                else if (strcmp (lpName, "LdnSapCfg") == 0)
                {
                  getDnSap (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.sap),
                          aeMgmt.t.cfg.s.sap.nmbSaps);
                }
              }
			  if (aiType == BP_AIN_SM_SUBTYPE_LSPCFG)
              {
                if (strcmp (lpName, "nmbSaps") == 0)
                {
                    aeMgmt.t.cfg.s.sap.nmbSaps = atoi (lpValue);
                }
                else if (strcmp (lpName, "LdnSapCfg") == 0)
                {
                  getDnSap (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.sap),
                            aeMgmt.t.cfg.s.sap.nmbSaps);
                }
              }
            }//getCfgOid
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for ldf-mtp3", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }

    return result;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getDnMngmt");


}


/******************************************************************************
 *
 *     Fun:   getDvSap()
 *
 *     Desc:  This function returns Entire Sap Configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getDvSap (DOMNode *apNode, LdvSapCfg *aeDvSingleSap, int numSaps)
{
  DOMNode *tmpNode;
     logger.logMsg (TRACE_FLAG, 0,
                    "Entering getDvSap <%d>",numSaps);
    for(int i=0;i<numSaps;i++){

      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {

     logger.logMsg (TRACE_FLAG, 0,
                    "calling getDvSingleSap ");
      getDvSingleSap(apNode->getFirstChild(), &(aeDvSingleSap->sap[i]));
      }
    }
return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   getDvSingleSap()
 *
 *     Desc:  This function returns Single Sap Configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getDvSingleSap (DOMNode *apNode, LdvSingleSapCfg *aeDvSingleSap)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getDvSingleSap");

    int aiCount = 0;

    if (apNode == 0){
     logger.logMsg (TRACE_FLAG, 0,
                    "Exiting abnormaly from getDvSingleSap ");
      return BP_AIN_SM_FAIL;
    }

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "sapId") == 0)
                {
                    aeDvSingleSap->sapId = atoi(lpValue);
                }
                else if (strcmp (lpName, "nwkId") == 0)
                {
                    aeDvSingleSap->nwkId = atoi(lpValue);
                }
              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getDvSingleSap");
    return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   getDvSingleNwk()
 *
 *     Desc:  This function returns Single Sap Configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getDvSingleNwk (DOMNode *apNode, LdvSingleNwkCfg *aeDvSingleNwk)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getDvSingleNwk");

    int aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];

    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "nwkId") == 0)
                {
                    aeDvSingleNwk->nwkId = atoi(lpValue);
                }
                else if (strcmp (lpName, "nwkApp") == 0 )
                {
                    getU8ParmList(apNode->getFirstChild(),(U8*)&(aeDvSingleNwk->nwkApp[0]), aiCount);
                }
              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getDvSingleNwk");
    return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   getDvNwks()
 *
 *     Desc:  This function returns Entire Nwk Configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getDvNwks (DOMNode *apNode, LdvNwkCfg *aeDvSingleNwk, int numNwks)
{
    for(int i=0;i<numNwks;i++)
    {
      getDvSingleNwk(apNode->getFirstChild(), &(aeDvSingleNwk->nwk[i]));
      apNode = apNode->getNextSibling();
    }
return BP_AIN_SM_OK;
}


/******************************************************************************
 *
 *     Fun:   getAllNwkApp()
 *
 *     Desc:  This function returns Single Sap Configuration
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getAllNwkApp (DOMNode *apNode, U8 nwkApp[] )
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getAllNwkApp");

    int aiCount = 0;

    if (apNode == 0)
      return BP_AIN_SM_FAIL;

    char *lpName = new char [100];
    char *lpValue = new char [100];
   int cnt=0;
    while (apNode)
    {
      if (apNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (apNode->getNodeName());
            if (strcmp (nodeName, mpcCfgOidElement) == 0)
            {
              if (getCfgOid ((DOMElement*)apNode, (char*)"name", lpName, (char*)"value", lpValue))
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Oid retrieved = <%s>, <%s>", lpName, lpValue);
                if (strcmp (lpName, "nwkApp") == 0 )
                {
                    nwkApp[cnt++] = atoi(lpValue);
                }
              }
            }
            else
            {
              logger.logMsg (ERROR_FLAG, 0,
                    "Unknown Element <%s> Encountered", nodeName);
            }
        XMLString::release (&nodeName);
        apNode = apNode->getNextSibling();
      }
      else
      {
        apNode = apNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getAllNwkApp");
    return BP_AIN_SM_OK;
}






/******************************************************************************
 *
 *     Fun:   getDvMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for M3UA
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getDvMngmt (int aiType, LdvMngmt &aeMgmt, int aiIndex, int* apiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getDvMngmt");

    int result = BP_AIN_SM_OK;
    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode* lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"ldf-m3ua", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <ldf-m3ua:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    Header &hdr = aeMgmt.hdr;

    cmMemset((U8 *)&aeMgmt, '\0', sizeof(LdvMngmt));
    hdr.msgType          = 0;
    hdr.msgLen           = 0;
    hdr.entId.ent        = 0;
    hdr.entId.inst       = 0;
    hdr.elmId.elmnt      = 0;
    hdr.elmId.elmntInst1 = 0;
    hdr.elmId.elmntInst2 = 0;
    hdr.elmId.elmntInst3 = 0;
    hdr.seqNmb           = 0;
    hdr.version          = 0;

    hdr.response.prior      = BP_AIN_SM_PRIOR;
    hdr.response.route      = BP_AIN_SM_ROUTE;
    hdr.response.mem.region = BP_AIN_SM_REGION;
    hdr.response.mem.pool   = BP_AIN_SM_POOL;
    hdr.response.selector   = BP_AIN_SM_COUPLING;


    lpNode = lpNode->getFirstChild();

    char *lpName = new char [100];
    char *lpValue = new char [100];

    bool indexFound = false;
    int liMonitorResource = 0;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "protEnt") == 0)
                {
                    aeMgmt.t.cfg.s.gen.protEnt = atoi (lpValue);    
                }
                else if (strcmp (lpName, "protInst") == 0)
                {
                    aeMgmt.t.cfg.s.gen.protInst = atoi (lpValue);
                }
                else if (strcmp (lpName, "prefProcId") == 0)
                {
                    aeMgmt.t.cfg.s.gen.prefProcId = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxUpperSaps") == 0)
                {
                    aeMgmt.t.cfg.s.gen.maxUpperSaps = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxLowerNwks") == 0)
                {
                    aeMgmt.t.cfg.s.gen.maxLowerNwks = atoi (lpValue);    
                }
                else if (strcmp (lpName, "CmZvDvRsetGenCfg") == 0)
                {
                    getZvDvRsetGen (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.gen.rsetCfg));
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_RSETMAPCFG_CRITICAL)
              {
                if (strcmp (lpName, "cmFthaRsetType") == 0)
                {
                    getAttrVal ((DOMElement*) lpNode, (char*)"type", lpValue);
                    aeMgmt.t.cfg.s.rset.rType.type = atoi (lpValue);
                    getAttrVal ((DOMElement*) lpNode, (char*)"qual", lpValue);
                    aeMgmt.t.cfg.s.rset.rType.qual = atoi (lpValue);
                }
                else if (strcmp (lpName, "id") == 0)
                {
                    aeMgmt.t.cfg.s.rset.u.crit.id = atoi (lpValue);
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_RSETMAPCFG_DEF)
              {
                if (strcmp (lpName, "cmFthaRsetType") == 0)
                {
                    getAttrVal ((DOMElement*) lpNode, (char*)"type", lpValue);
                    aeMgmt.t.cfg.s.rset.rType.type = atoi (lpValue);
                    getAttrVal ((DOMElement*) lpNode, (char*)"qual", lpValue);
                    aeMgmt.t.cfg.s.rset.rType.qual = atoi (lpValue);
                }
                else if (strcmp (lpName, "id") == 0)
                {
                    aeMgmt.t.cfg.s.rset.u.def.id = atoi (lpValue);
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_RSETMAPCFG_NONCRITICAL)
              {
                if (strcmp (lpName, "cmFthaRsetType") == 0)
                {
                    getAttrVal ((DOMElement*) lpNode, (char*)"type", lpValue);
                    aeMgmt.t.cfg.s.rset.rType.type = atoi (lpValue);
                    getAttrVal ((DOMElement*) lpNode, (char*)"qual", lpValue);
                    aeMgmt.t.cfg.s.rset.rType.qual = atoi (lpValue);
                }
                else if (strcmp (lpName, "ifType") == 0)
                {
                    aeMgmt.t.cfg.s.rset.u.nonCrit.ifType = atoi (lpValue);
                }
                else if (strcmp (lpName, "CmZvDvRsetMap_upper") == 0)
                {
                    getZvDvRsetMap (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.rset.u.nonCrit.upper));
                }
                else if (strcmp (lpName, "CmZvDvRsetMap_lower") == 0)
                {
                    getZvDvRsetMap (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.rset.u.nonCrit.lower));
                }
              }
              if (aiType == BP_AIN_SM_SUBTYPE_USPCFG)
              {
                logger.logMsg (TRACE_FLAG, 0,
                    "Inside USPCFG ");
                if (strcmp (lpName, "nmbSaps") == 0)
                {
                    aeMgmt.t.cfg.s.sap.nmbSaps = atoi (lpValue);
                    logger.logMsg (TRACE_FLAG, 0,
                    "Inside USPCFG, numbSaps <%d>", aeMgmt.t.cfg.s.sap.nmbSaps);
                }
                getDvSap (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.sap),aeMgmt.t.cfg.s.sap.nmbSaps);
              }
              if (aiType == BP_AIN_SM_SUBTYPE_NWKCFG)
              {
                if (strcmp (lpName, "nmbNwks") == 0)
                {
                    aeMgmt.t.cfg.s.nwk.nmbNwks = atoi (lpValue);
                }
                else if (strcmp (lpName, "LdvNwkCfg") == 0 )
                {
                    int liCount = 0;
                    if (lpNode->getFirstChild())
                      getDvNwks (lpNode->getFirstChild(), &(aeMgmt.t.cfg.s.nwk),aeMgmt.t.cfg.s.nwk.nmbNwks);
                }

              }
            }//getCfgOid
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for ldf-mtp3", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }

    return result;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getDvMngmt");
}


/******************************************************************************
 *
 *     Fun:   getMrMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for Message Router
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getMrMngmt (int aiType, MrMngmt &aeMgmt, int aiIndex, int* apiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getMrMngmt");

    int result = BP_AIN_SM_OK;
    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode* lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"mr", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <mr:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    lpNode = lpNode->getFirstChild();

    char *lpName = new char [100];
    char *lpValue = new char [100];

    bool indexFound = false;
    int liMonitorResource = 0;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "maxEntities") == 0)
                {
                    aeMgmt.s.cfg.maxEntities = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxInstances") == 0)
                {
                    aeMgmt.s.cfg.maxInstances = atoi (lpValue);
                }
                else if (strcmp (lpName, "maxPeerSyncMsg") == 0)
                {
                    aeMgmt.s.cfg.maxPeerSyncMsg = atoi (lpValue);    
                }
                else if (strcmp (lpName, "maxQSize") == 0)
                {
                    aeMgmt.s.cfg.maxQSize = atoi (lpValue);    
                }
                else if (strcmp (lpName, "timeRes") == 0)
                {
                    aeMgmt.s.cfg.timeRes = atoi (lpValue);    
                }
                else if (strcmp (lpName, "peerTmr") == 0)
                {
                    aeMgmt.s.cfg.peerTmr.val = atoi (lpValue);    
                    if(aeMgmt.s.cfg.peerTmr.val)
                      aeMgmt.s.cfg.peerTmr.enb = TRUE;
                }
#ifdef MR_DFTHA
                else if (strcmp (lpName, "maxRsets") == 0)
                {
                    aeMgmt.s.cfg.maxRsets = atoi (lpValue);    
                }
                else if (strcmp (lpName, "syncAckTmr") == 0)
                {
                    aeMgmt.s.cfg.syncAckTmr.val = atoi (lpValue);    
                    if(aeMgmt.s.cfg.syncAckTmr.val)
                      aeMgmt.s.cfg.syncAckTmr.enb= TRUE;
                }
                else if (strcmp (lpName, "flushTmr") == 0)
                {
                    aeMgmt.s.cfg.flushTmr.val = atoi (lpValue);    
                    if(aeMgmt.s.cfg.flushTmr.val)
                      aeMgmt.s.cfg.flushTmr.enb= TRUE;
                }
#endif
              }
            }//getCfgOid
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for ldf-mtp3", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }

    return result;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getMrMngmt");
}


/******************************************************************************
 *
 *     Fun:   getShMngmt()
 * 
 *     Desc:  This function returns Configuration Management structure for System Agent
 *
 *     Notes: None
 *
 *     File:  INGwSmRepository.C
 *
 *******************************************************************************/
    int
INGwSmRepository::getShMngmt (int aiType, ShMngmt &aeMgmt, int aiIndex, int* apiCount)
{
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmRepository::getShMngmt");

    int result = BP_AIN_SM_OK;
    string lstrOpName;

    if (getLayerOpStr (aiType, lstrOpName) == BP_AIN_SM_FAIL)
    {
      return BP_AIN_SM_FAIL;
    }

    DOMNode* lpNode = getCfgNode ((char*)mpcCfgElement, (char*)"sh", 
        (char*)lstrOpName.c_str());

    if (lpNode == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
            "Unable to find DOMElement for <mr:%s>", lstrOpName.c_str());
      return BP_AIN_SM_FAIL;
    }

    lpNode = lpNode->getFirstChild();

    char *lpName = new char [100];
    char *lpValue = new char [100];

    bool indexFound = false;
    int liMonitorResource = 0;

    while (lpNode)
    {
      if (lpNode->getNodeType() == DOMNode::ELEMENT_NODE)
      {
        char *nodeName;
        nodeName = XMLString::transcode (lpNode->getNodeName());
        if (strcmp (nodeName, mpcCfgOidElement) == 0)
        {
            if (aiIndex != -1)
              indexFound = true;

            if (getCfgOid ((DOMElement*)lpNode, (char*)"name", lpName, (char*)"value", lpValue))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Oid retrieved = <%s>, <%s>", lpName, lpValue);

              if (aiType == BP_AIN_SM_SUBTYPE_GENCFG)
              {
                if (strcmp (lpName, "trcMask") == 0)
                {
                    aeMgmt.t.cfg.shGen.trcMask = atoi (lpValue);    
                }
                else if (strcmp (lpName, "dbgMask") == 0)
                {
                    aeMgmt.t.cfg.shGen.dbgMask = atoi (lpValue);
                }
                else if (strcmp (lpName, "tmrRes") == 0)
                {
                    aeMgmt.t.cfg.shGen.tmrRes = atoi (lpValue);
                }
                else if (strcmp (lpName, "trcLevel") == 0)
                {
                    aeMgmt.t.cfg.shGen.trcLevel = atoi (lpValue);    
                }
                else if (strcmp (lpName, "toLocal1") == 0)
                {
                    aeMgmt.t.cfg.shGen.toLocal1.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.shGen.toLocal1.val)
                      aeMgmt.t.cfg.shGen.toLocal1.enb = TRUE;
                }
                else if (strcmp (lpName, "tolocal2") == 0)
                {
                    aeMgmt.t.cfg.shGen.toLocal2.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.shGen.toLocal2.val)
                      aeMgmt.t.cfg.shGen.toLocal2.enb = TRUE;
                }
                else if (strcmp (lpName, "nLocal") == 0)
                {
                    aeMgmt.t.cfg.shGen.nLocal = atoi (lpValue);    
                }
                else if (strcmp (lpName, "toRemote") == 0)
                {
                    aeMgmt.t.cfg.shGen.toRemote.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.shGen.toRemote.val)
                      aeMgmt.t.cfg.shGen.toRemote.enb = TRUE;
                }
                else if (strcmp (lpName, "nRemote") == 0)
                {
                    aeMgmt.t.cfg.shGen.nRemote = atoi (lpValue);    
                }
#ifdef LSHV2
                else if (strcmp (lpName, "seqTmr") == 0)
                {
                    aeMgmt.t.cfg.shGen.seqTmr.val = atoi (lpValue);    
                    if(aeMgmt.t.cfg.shGen.seqTmr.val)
                      aeMgmt.t.cfg.shGen.seqTmr.enb= TRUE;
                }
#endif
              }
            }//getCfgOid
        }
        else if (aiIndex != -1 && strcmp (nodeName, mpcCfgIndexValue) == 0)
        {
            logger.logMsg (TRACE_FLAG, 0,
                "<%s> : Found an index Node for ldf-mtp3", nodeName);

            if (getAttrVal ((DOMElement*) lpNode, (char*)"value", lpName))
            {
              logger.logMsg (TRACE_FLAG, 0,
                    "Iterating over index <%s> while searching for <%d>",
                    lpName, aiIndex);
              int liCount = atoi (lpName);
              if (liCount == aiIndex)
              {
                logger.logMsg (TRACE_FLAG, 0,
                      "Found a child node for index <%d>", liCount);

                lpNode = lpNode->getFirstChild();
                XMLString::release (&nodeName);
                continue;
              }
            }
        }
        else
        {
            logger.logMsg (ERROR_FLAG, 0,
                "Unknown Element <%s> Encountered", nodeName);
        }

        XMLString::release (&nodeName);
        lpNode = lpNode->getNextSibling();
      }
      else
      {
        lpNode = lpNode->getNextSibling();
      }
    }

    delete [] lpName;
    delete [] lpValue;

    if (aiIndex != -1 && indexFound == false)
    {
      logger.logMsg (TRACE_FLAG, 0,
            "Index Over since no more nodes could be found");
      return BP_AIN_SM_INDEX_OVER;
    }

    return result;

    logger.logMsg (TRACE_FLAG, 0,
        "Leaving INGwSmRepository::getShMngmt");
}



