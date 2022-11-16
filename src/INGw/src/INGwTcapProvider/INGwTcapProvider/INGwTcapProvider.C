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
//     File:     INGwTcapProvider.C
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwTcapProvider");

#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraParamRepository/INGwIfrPrConfigMgr.h>
#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwInfraUtil/INGwIfrUtlGlbFunc.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>

#include <INGwTcapProvider/INGwTcapProvider.h>
#include <INGwTcapProvider/INGwTcapInclude.h>
#include <INGwTcapProvider/INGwTcapFtHandler.h>
#include <INGwTcapProvider/INGwTcapMsgLogger.h>
#include "INGwStackManager/INGwSmWrapper.h"
#include <INGwIwf/INGwIwfProvider.h>
#include <INGwInfraMsrMgr/MsrMgr.h>

#include "INGwTcapIncMsgHandler.h"
#include <Util/imOid.h>
#include <signal.h>
#include <unistd.h>
#define CCPU_STUB
int INGwTcapStatParam::INGW_INBOUND_DLG_INDX   = 0;
int INGwTcapStatParam::INGW_INBOUND_CMP_INDX   = 0;  
int INGwTcapStatParam::INGW_INBOUND_NOT_INDX   = 0;  
int INGwTcapStatParam::INGW_INBOUND_ABRT_INDX  = 0; 
int INGwTcapStatParam::INGW_INBOUND_UNI_INDX   = 0;   
int INGwTcapStatParam::INGW_OUTBOUND_DLG_INDX  = 0; 
int INGwTcapStatParam::INGW_OUTBOUND_CMP_INDX  = 0;  
int INGwTcapStatParam::INGW_OUTBOUND_NOT_INDX  = 0;  
int INGwTcapStatParam::INGW_OUTBOUND_ABRT_INDX = 0; 
int INGwTcapStatParam::INGW_OUTBOUND_UNI_INDX  = 0; 

int INGwTcapStatParam::INGW_INBOUND_BGN_INDEX  = 0;
int INGwTcapStatParam::INGW_OUTBOUND_BGN_INDEX = 0;
int INGwTcapStatParam::INGW_INBOUND_CNT_INDEX  = 0;
int INGwTcapStatParam::INGW_OUTBOUND_CNT_INDX  = 0;
int INGwTcapStatParam::INGW_INBOUND_END_INDEX  = 0;
int INGwTcapStatParam::INGW_OUTBOUND_END_INDEX = 0;
int INGwTcapStatParam::INGW_INBOUND_PABRT_INDEX= 0;

int INGwTcapStatParam::INGW_TRIGR_UABRT_INDEX  = 0;
int INGwTcapStatParam::INGW_TRIGR_END_INDEX    = 0;

extern S16 sgLoDbGetMyState(void);

INGwTcapProvider* INGwTcapProvider::m_selfPtr = NULL;

int ** gDlgInvBuff;
U16 selfProcId = DEF_INGW_SELF_PROC_ID;

// For debugging - Start [
int mActCallsThres;
bool actCallIncDiagTriggered = false;
bool hbIncDiagTriggered = false;
// ] End - For debugging

const char* INGwTcapProvider::mpcOIDsOfInterest[] =
{
	ingwIS_PRIMARY,
	ingwPDU_LOG_LEVEL,
	ingwSIP_STACK_USER_PROFILE,
};

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
extern "C"
void*
launchRegstrationWithStackThread(void *arg)
{
  LogINGwTrace(false, 0,"IN launchRegstrationWithStackThread");
  INGwTcapProvider* pProvider =  static_cast<INGwTcapProvider*> (arg);

  int retVal = pProvider->regAppWithStackWrapper();

  LogINGwTrace(false, 0,"OUT launchRegstrationWithStackThread");
  return 0;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
void
getAppId(SccpAddr &p_ownAddr, AppInstId &p_appId)
{
	INGwTcapProvider::getInstance().getAppId(p_ownAddr.pc, 
										p_ownAddr.ssn, p_appId);
}

void
getAppId(SccpAddr &p_ownAddr, SccpAddr &p_destAddr, AppInstId &p_appId)
{
	INGwTcapProvider::getInstance().getAppId(p_ownAddr.pc, 
										p_ownAddr.ssn, p_appId);

	if (p_appId.appId == 0  && p_appId.instId == 0) {
		INGwTcapProvider::getInstance().getAppId(p_destAddr.pc, 
										p_destAddr.ssn, p_appId);
	}
}

/**
* Constructor
*/
INGwTcapProvider::INGwTcapProvider():INGwIwfBaseProvider(PROVIDER_TYPE_TCAP),
									m_isRunning(false), m_firstTimeRegistration(true), 
									m_protoType(true), m_selfState(-1),
                  mINGwSilRx(INGwSilRx::instance()),
                  mINGwSilTx(INGwSilTx::instance())
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::constructor()");

	pthread_mutex_init(&m_appIdSsnInfoLock, NULL);

	m_tcapIface = new INGwTcapIface();

	memset(&m_stkFileName, 0, MAX_STACK_LOG_FILE_SIZE);	

  invalid = new AppIdInfo();

	for (int i=0; i < TCAP_APP_INFO_COUNT; ++i) {
		m_appIdSsnInfo[i] = NULL;
	}

	char *debugFile = getenv("STACK_DBG_FILE_NAME");

 	if( NULL == debugFile) {
		strcpy(m_stkFileName, TACP_DBG_DEFAULT_FILE);
	}
	else {
		strcpy(m_stkFileName, debugFile);
	}

	m_iwfIface = INGwIwfProvider::getInstance().getInterface();

	m_ldDstMgr = new INGwLdDstMgr();	// Load Distributor

  // Mriganka - Trillium stack integration - Start [

  mINGwSmWrapper = new INGwSmWrapper(this);

  // ] - Mriganka - Trillium stack integration - End
  mFpMask = 0; 

  // For debugging - Start [
	char *lcActCallsThres = getenv("ACT_CALLS_THRESHOLD");
 	if(lcActCallsThres) {
		mActCallsThres = atoi(lcActCallsThres);
	}
	else {
		mActCallsThres = 40000;
	}
  // ] End - For debugging
  
	getTcapStatParamIndex();

	LogINGwTrace(false, 0,"OUT INGwTcapProvider::constructor()");
}

/**
* Destructor
*/
INGwTcapProvider::~INGwTcapProvider()
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::destructor()");

	pthread_mutex_destroy(&m_appIdSsnInfoLock);

	if (NULL != m_tcapIface) {
		delete m_tcapIface;
	}

	for (int i=0; i < TCAP_APP_INFO_COUNT; ++i) {
		if (NULL != m_appIdSsnInfo[i]) {
			delete (m_appIdSsnInfo[i]);
		}	
	}

	LogINGwTrace(false, 0,"OUT INGwTcapProvider::destructor()");
}

/**
*	This method instantiate IwfProvider.
*
*/
INGwTcapProvider&
INGwTcapProvider::getInstance()
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::getInstance()");
             
  if (NULL == m_selfPtr) {
    m_selfPtr = new INGwTcapProvider();
	}
             
	LogINGwTrace(false, 0,"OUT INGwTcapProvider::getInstance()");
  return *(INGwTcapProvider::m_selfPtr);
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int 
INGwTcapProvider::changeState(INGwIwfBaseProvider::ProviderStateType p_state)
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::changeState()");

	int lRetVal = G_SUCCESS;
	string lStateChange;

	if (INGwIwfBaseProvider::PROVIDER_STATE_LOADED == p_state) {
		lStateChange = "LOADED";
	}
	else if (INGwIwfBaseProvider::PROVIDER_STATE_RUNNING == p_state) {
		lStateChange = "RUNNING";

		INGwIfrPrConfigMgr::getInstance().
										registerForConfig(mpcOIDsOfInterest, 
										sizeof(mpcOIDsOfInterest)/sizeof(char*), this);

		// Chaged to state RUNNING.
		// Calling the ::run() API
    string transporttypestr = "";
    string isprimarystr = "";
    INGwIfrPrParamRepository::getInstance().getValue(ingwIS_PRIMARY, isprimarystr);
    INGwIfrPrParamRepository::getInstance().getValue(ainTRANSPORT_TYPE, transporttypestr);
    int isprimary = atoi(isprimarystr.c_str());
    int transporttype = atoi(transporttypestr.c_str());

    if( (TRANSPORT_TYPE_MTP == transporttype) && (0 == isprimary) ) {
			logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
			    "ChangeState to RUNNING for MTP secondary CMM");
      // INCTBD - Mriganka
			//LogINGwTrace(false, 0,"OUT BpAinProvider::changeState()");
			//return 0;
    }

		lRetVal = run();
		if(AIN_SUCCESS != lRetVal) {
			logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
								 "ChangeState to RUNNING FAILED : error code <%d>",
								 lRetVal);
			
			LogINGwTrace(false, 0,"OUT INGwTcapProvider::changeState()");
			return lRetVal;
		}
    
	}
	else if (INGwIwfBaseProvider::PROVIDER_STATE_STOPPED == p_state) {
		 INGwIfrPrConfigMgr::getInstance().unregisterForConfig(
							mpcOIDsOfInterest, sizeof(mpcOIDsOfInterest)/sizeof(char*), this);
		lStateChange = "STOPPED";

		// Chaged to state STOPPED
		// Calling the ::stop() API
		lRetVal = stop();
		if(AIN_SUCCESS != lRetVal) {
			logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
								 "ChangeState to STOPPED FAILED : error code <%d>",
								 lRetVal);
			
			LogINGwTrace(false, 0,"OUT BpAinProvider::changeState()");
			return lRetVal;
		}
    
	}

	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
	       "[changeState] INGwTcapProvider state changed to [%s]", 
	       lStateChange.c_str());

	LogINGwTrace(false, 0,"OUT INGwTcapProvider::changeState()");
	return lRetVal;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapProvider::startUp(void)
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::startUp()");

	int retVal = G_SUCCESS;

  setSubsysIdProcId();
  setProcStatus();
  int lNumOfInsts;
  m_selfInstId = getSelfInstId(lNumOfInsts);
	// Initialize stack specific paramaters
	if (G_SUCCESS != (retVal = initSs7Config())) {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"[startUp] Failed to startUp, initSs7Config failed");
	}

	m_tcapIface->setProtoType(m_protoType);

	// Initialize PDU Logger
	retVal = INGwTcapMsgLogger::getInstance().initialize();

	if (G_SUCCESS != retVal) {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
				"[startUp] TcapMsgLogger Initialize Failed");
		return retVal;
	}

	// Initialize Load Distributor
	if (G_SUCCESS != (retVal = m_ldDstMgr->initialize())) {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"[startUp] Initialization Failed for LoadDistributor");
		retVal = G_FAILURE;
	}

	// Initialize TcapIncMsgReceiver
	INGwTcapIncMsgHandler::getInstance().initialize(m_iwfIface, m_ldDstMgr);

	// Initialize Ft Handler
	INGwTcapFtHandler::getInstance().initialize(m_ldDstMgr);

	// Initialize Tcap Interface
	m_tcapIface->initialize(m_ldDstMgr);


  // Mriganka - Trillium stack integration - Start [

 	LogINGwVerbose(false, 0,"Invoking start up on SM");
	retVal = mINGwSmWrapper->startUp(tuActvInit, tuActvTsk);
	if(AIN_SUCCESS != retVal) {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
							 "StartUp on SM wrapper failed with return code <%d>",
							 retVal);
		
		// Do we have to delete SmWrapper now!!
		
		LogINGwTrace(false, 0,"OUT startUp()");
		return retVal;
	}
		
	retVal = mINGwSilRx.init(&(INGwTcapIncMsgHandler::getInstance()));
	if(AIN_SUCCESS != retVal) {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
							 "Init on INGwSilRx failed with code <%d>",
							 retVal);
		
		LogINGwTrace(false, 0,"OUT INGwTcapProvider::startUp()");
		return retVal;
	}
		
  retVal = mINGwSilTx.init();
	if(AIN_SUCCESS != retVal) {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
							 "Init on INGwSilTx failed with code <%d>",
							 retVal);
		
		LogINGwTrace(false, 0,"OUT INGwTcapProvider::startUp()");
		return retVal;
	}

  // ] - Mriganka - Trillium stack integration - End

	LogINGwTrace(false, 0,"OUT INGwTcapProvider::startUp()");
	return retVal;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
void
INGwTcapProvider::getStatistics(std::ostrstream &output, int tabCount)
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::getStatistics()");

	char tabs[20];
	for (int idx = 0; idx < tabCount; idx++) {
		tabs[idx] = '\t';
	}
	tabs[tabCount] = '\0';

	output << tabs << "TcapProvider Statistics\n";

	INGwIfrSmStatMgr &lStatMgr = INGwIfrSmStatMgr::instance();

	unsigned long  curVal1 = 0;
	MsrMgr::getInstance()->getValue("Active Call", "INGw", 
					"Active Call", curVal1);
	output << tabs << "\tActive Call   : " << curVal1 << "\n";

	int curVal =0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_INBOUND_DLG_INDX, curVal);
	output << tabs << "\tTotal InDlg   : " << curVal << "\n";

	curVal =0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_INBOUND_CMP_INDX, curVal);
	output << tabs << "\tTotal InCmp   : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_INBOUND_NOT_INDX, curVal);
	output << tabs << "\tTotal InNotice: " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_INBOUND_ABRT_INDX, curVal);
	output << tabs << "\tTotal InAbort : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_INBOUND_UNI_INDX, curVal);
	output << tabs << "\tTotal InUniDir: " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_OUTBOUND_DLG_INDX, curVal);
	output << tabs << "\tTotal OutDlg  : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_OUTBOUND_CMP_INDX, curVal);
	output << tabs << "\tTotal OutCmp  : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_OUTBOUND_NOT_INDX, curVal);
	output << tabs << "\tTotal OutNot  : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_OUTBOUND_ABRT_INDX, curVal);
	output << tabs << "\tTotal OutAbort: " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_OUTBOUND_UNI_INDX, curVal);
	output << tabs << "\tTotal OutUniDir: " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_INBOUND_BGN_INDEX, curVal);
	output << tabs << "\tTotal InBegin  : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_OUTBOUND_BGN_INDEX, curVal);
	output << tabs << "\tTotal OutBegin : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_INBOUND_CNT_INDEX, curVal);
	output << tabs << "\tTotal InCont   : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_OUTBOUND_CNT_INDX, curVal);
	output << tabs << "\tTotal OutCont  : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_INBOUND_END_INDEX, curVal);
	output << tabs << "\tTotal InEnd    : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_OUTBOUND_END_INDEX, curVal);
	output << tabs << "\tTotal OutEnd   : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_INBOUND_PABRT_INDEX, curVal);
	output << tabs << "\tTotal InPAbort  : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_TRIGR_UABRT_INDEX, curVal);
	output << tabs << "\tTotal OutUAbort [INGw]  : " << curVal << "\n";

	curVal = 0;
	lStatMgr.getCurValue(INGwTcapStatParam::INGW_TRIGR_END_INDEX, curVal);
	output << tabs << "\tTotal OutEnd [INGw]  : " << curVal << "\n";

	LogINGwTrace(false, 0,"OUT INGwTcapProvider::getStatistics()");
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapProvider::configure(const char* p_oid, const char* p_value, 
													  ConfigOpType p_opType)
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::configure()");

	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
	"[configure] OID Change received by TCAP Provider oid[%s] value[%s]",
	p_oid, p_value);

	if (0 == strcasecmp(ingwIS_PRIMARY, p_oid)) {
    if(0 == strcasecmp(p_value, "0")) {
      if (m_selfState == -1) {
	      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "m_selfState CurrVal[%s] PrevVal[%d] NewVal[0]", 
               p_value, m_selfState);
        m_selfState =0;
        // Role is Standby
      }
    }
    else if(0 == strcasecmp(p_value, "1")) {
      if(m_selfState == -1) {
	      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "m_selfState CurrVal[%s] PrevVal[%d] NewVal[1]", 
               p_value, m_selfState);
        m_selfState = 1;
        // Role is Active
      }
      else if (m_selfState == 0) {
	      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "m_selfState CurrVal[%s] PrevVal[%d] NewVal[1]", 
               p_value, m_selfState);
        m_selfState =1;
        // Role Changeover to Active

	      if (G_SUCCESS != mINGwSmWrapper->configure(p_oid, p_value, p_opType)) {
		    			logger.logINGwMsg(false, ERROR_FLAG, 0,
		    			"[configure] Failed to change state of stack on becoming primary, Quitting");
		    			raise(9);
		    }
      }
    }
	}
	else if (0 == strcasecmp(ingwPEER_CONNECTED, p_oid)) {
		if (atoi(p_value)) {

      // Update the status of Peer ProcId (INC) to connected
      for( map<int,ProcStatus*>::iterator ii = m_incProcStatusMap.begin(); 
           ii != m_incProcStatusMap.end(); ++ii) {

        if ( selfProcId != (*ii).first) {
          ProcStatus * lProcStatus = (*ii).second;
          lProcStatus->stateFromEms = 1;
          logger.logINGwMsg(false, TRACE_FLAG, 0, 
                 "ProcId[%d] Status EmsStatus[%d],"
                 "RelayStatus[%d], INCStatus[%d]",
                 (*ii).first, lProcStatus->stateFromEms, 
                 lProcStatus->stateFromRelay, lProcStatus->stateFromInc);
        }
      }

      // Replicate the LoadDistribution Map to peer INC, 
      // which has become connected
			INGwTcapFtHandler::getInstance().replicateInfoToPeer();
		}
	}
	else if (0 == strcasecmp(ingwPDU_LOG_LEVEL, p_oid)) {
		INGwTcapMsgLogger::getInstance().setLoggingLevel(atoi(p_value));
	}
	else if (0 == strcasecmp("tcapClientDbgLvl", p_oid)) {
		int lvl = atoi(p_value);

		if (0 < lvl && 4 > lvl ) {
      /* INCTBD
			TuChgDbgLevel(m_appContext, lvl);
      */
			logger.logINGwMsg(false, ALWAYS_FLAG, 0,
			"[configure] TCAP Client Log level modified to [%d]", lvl);
		}
	}
	else {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
		"[conifgure] Unknown OID [%s] value [%s] received by TCAP Provider",
		p_oid, p_value);
	}

	LogINGwTrace(false, 0,"OUT INGwTcapProvider::configure()");
	return 0;
}


/**
* Description : This is invoked when INFO message containing USER_IN_SERVICE 
*               for a particular PC-SSN is received from SAS and also from 
*               INGwTcapFtHandler::handleLoadDistMsg() in case of REGISTER_SAS_APP
*
* @param <> -
*
* @return <> -
*
*/
g_tcapSsnState
INGwTcapProvider::registerWithStack(U32 &p_pc, U8 &p_ssn, int p_flag)
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::registerWithStack()");

	g_tcapSsnState retVal = TCAP_UNKNOWN;
	U8 appId   					  = 0;
	int index 						= 0;

	pthread_mutex_lock (&m_appIdSsnInfoLock);

		for(int i=0; i < m_ssnInfo.size(); ++i)
		{
			if(m_ssnInfo[i].ssn == p_ssn)
			{
				retVal = m_ssnInfo[i].regState;

				// This is case where peer has got registration request 
				// from SAS and has send message to standby. Standby INC
				// will mark state as PENDING so that when failover happens
				// it can start registration
				if(TCAP_SECONDARY == myRole())
				{
					if(p_flag == TCAP_REGISTER)
						m_ssnInfo[i].regState = TCAP_REGISTER_PENDING;
					else
						m_ssnInfo[i].regState = TCAP_UNKNOWN;
					break;
				}

				if ((TCAP_REGISTERED != retVal) || 
								(TCAP_REGISTERED == retVal && p_flag == TCAP_RE_REGISTER)) 
				{
					m_ssnInfo[i].regState = TCAP_REGSITER_INPGRS;

					TcapRegInfo *reg = new TcapRegInfo;
					reg->m_suId      =  m_ssnInfo[i].suId;
					reg->m_regType   = p_flag;
					reg->m_index     = index;
		 			m_registerQ.push(reg);
				}
				else if (TCAP_REGISTERED == retVal) {
					logger.logINGwMsg(false, ALWAYS_FLAG, 0,
					"[registerWithStack] Already Registered ssn[%d] suId[%d]",
					p_ssn, m_ssnInfo[i].suId);
				}
			  break;
			} // end of ssn match
		}   // end of for loop
	
	pthread_mutex_unlock (&m_appIdSsnInfoLock);

	LogINGwTrace(false, 0,"OUT INGwTcapProvider::registerWithStack()");
	return retVal;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
void
INGwTcapProvider::registerAllAppWithStack()
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::registerAllAppWithStack()");

	g_tcapSsnState retVal = TCAP_UNKNOWN;
	U8 appId   					  = 0;
	bool found 						= false;
	int index 						= 0;

	pthread_mutex_lock (&m_appIdSsnInfoLock);

	for(int i=0; i < m_ssnInfo.size(); ++i)
	{
		if(m_ssnInfo[i].regState == TCAP_REGISTER_PENDING)
		{
			m_ssnInfo[i].regState = TCAP_REGSITER_INPGRS;

			TcapRegInfo *reg = new TcapRegInfo;
			reg->m_suId      =  m_ssnInfo[i].suId;
			reg->m_regType   = TCAP_REGISTER;
			reg->m_index     = index;
			m_registerQ.push(reg);

			logger.logMsg(ERROR_FLAG, 0, 
				"Registering for SSN[%d] SUId[%d]",  m_ssnInfo[i].ssn,
						 m_ssnInfo[i].suId);
		}
		else {
			logger.logMsg(ERROR_FLAG, 0, 
				"Not Registering for SSN[%d] SUId[%d] after takeover",  
				m_ssnInfo[i].ssn, m_ssnInfo[i].suId);
		}
	}   // end of for loop

	pthread_mutex_unlock (&m_appIdSsnInfoLock);

	LogINGwTrace(false, 0,"OUT INGwTcapProvider::registerAllAppWithStack()");
	return;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
g_tcapSsnState
INGwTcapProvider::deregisterWithStack(U32 &p_pc, U8 &p_ssn, int p_flag)
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::deregisterWithStack()");
	g_tcapSsnState retVal = TCAP_UNKNOWN;

	U8 appId   = 0;
	bool found = false;
	int index =0;

	for (int i=0; i <  m_appIdInfo.size(); ++i) {
		if ( p_pc ==  m_appIdInfo[i].pc && p_ssn ==  m_appIdInfo[i].ssn) {
			appId = m_appIdInfo[i].appId;
			index = i;
			found = true;
			break;
		}
	}

	pthread_mutex_lock (&m_appIdSsnInfoLock);

	if (true == found) {
		for(int i=0; i < m_ssnInfo.size(); ++i)
		{
			if(m_ssnInfo[i].ssn == p_ssn)
			{
				retVal = m_ssnInfo[i].regState;

				if (TCAP_REGISTERED == retVal || TCAP_REGSITER_INPGRS == retVal) 
				{
					m_ssnInfo[i].regState = TCAP_DEREGISTERED;

					TcapRegInfo *reg = new TcapRegInfo;
					reg->m_suId      =  m_ssnInfo[i].suId;
					reg->m_regType   = p_flag;
					reg->m_index     = index;
		 			m_registerQ.push(reg);
				}
				else if (TCAP_DEREGISTERED == retVal || TCAP_UNKNOWN == retVal) {
					logger.logINGwMsg(false, ALWAYS_FLAG, 0,
					"[deregisterWithStack] stack not regsitered ssn[%d] suId[%d]",
					p_ssn, m_ssnInfo[i].suId);
				}
				else {
					logger.logINGwMsg(false, ERROR_FLAG, 0,
					"[deregisterWithStack] Invalid Registration State [%d] for ssn[%d]"
					" suId[%d]", retVal, p_ssn,  m_ssnInfo[i].suId);
				}
				break;
			} // end of ssn match
		}   // end of for loop
	}
	else {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"[deregisterWithStack] opc[%d] ssn[%d] is not in list", p_pc, p_ssn);
		retVal = TCAP_UNKNOWN;
	}

	pthread_mutex_unlock (&m_appIdSsnInfoLock);

	LogINGwTrace(false, 0,"OUT INGwTcapProvider::deregisterWithStack()");
	return retVal;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapProvider::changeSsnStatus(U32 &p_pc, U8 &p_ssn, int p_flag)
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::changeSsnStatus()");
	int retVal = G_SUCCESS;
/*
	U8 appId   = 0;
	bool found = false;
	int index =0;

	for (int i=0; i < m_appIdInfo.size(); ++i) {
		if ( p_pc == m_appIdInfo[i].pc && p_ssn == m_appIdInfo[i].ssn) {
			appId = m_appIdInfo[i].appId;
			index = i;
			found = true;
			break;
		}
	}

	if (false == found) {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"[changeSsnStatus] PC-SSN is not in my list [%d-%d]", p_pc, p_ssn);

		LogINGwTrace(false, 0,"OUT INGwTcapProvider::changeSsnStatus()");
		return (retVal=G_FAILURE);
	}

	U8 ssnStat = (p_flag == TCAP_SSN_IN_SERVICE)?SS_UIS:SS_UOS;

	AppInstId appInfo;
	appInfo.appId  = m_appIdInfo[index].appId;
	appInfo.instId = m_selfInstId;

	if (INC_ROK != (retVal = TuQuery (m_appContext, M7H_SSN_STE_REQ, ssnStat,
																											appInfo ))) {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"[changeSsnStatus] TuQuery() for SSN status change Failed"
		":AppId [%d] ssn[%d] status[%s]",
		m_appIdInfo[index].appId, p_ssn, (p_flag == TCAP_SSN_IN_SERVICE)?
		"M7H_SS_UIS":"M7H_SS_UOS");
		retVal = G_FAILURE;
	}
	else {
		logger.logINGwMsg(false, ERROR_FLAG, 0, 
		"[changeSsnStatus] TuQuery() for SSN status changed"
		":AppId [%d] ssn[%d] status[%s]",
		m_appIdInfo[index].appId, p_ssn, (p_flag == TCAP_SSN_IN_SERVICE)?
		"M7H_SS_UIS":"M7H_SS_UOS");
		retVal = G_SUCCESS;
	}
*/
	LogINGwTrace(false, 0,"OUT INGwTcapProvider::changeSsnStatus()");
	return retVal;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapProvider::initSs7Config()
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::initSs7Config()");

  	int retVal = G_SUCCESS;
	if (G_SUCCESS != (retVal = fetchOpcSsnInfo())) {
		LogINGwTrace(false, 0,"OUT INGwTcapProvider::initSs7Config(), Failed");
		return retVal;
	}

 	pthread_t regThdId;
  if (0 != pthread_create(&regThdId, 0, launchRegstrationWithStackThread,
                                          &INGwTcapProvider::getInstance())) {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
    "[initSs7Reg] Thread creation failed. launchRegstrationWithStackThread");

    LogINGwTrace(false, 0, "OUT INGwTcapProvider::initSs7Reg()");
    return G_FAILURE;
  }

	LogINGwTrace(false, 0,"OUT INGwTcapProvider::initSs7Config()");
	return retVal;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapProvider::initAppInstState(int p_state)
{
        LogINGwTrace(false, 0, "IN INGwTcapProvider::initAppInstState()");
        int retVal = G_SUCCESS;

#if 0
  int lCurrRole = myRole();

        if (true == p_forceUpdate) {
                lCurrRole = TCAP_PRIMARY;
        }


        if ((TCAP_PRIMARY == lCurrRole) && (false == p_forceUpdate)) {
                logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                "[initAppInstState] I am Primary, Not calling TuInformInstStatus
()");

                LogINGwTrace(false, 0, "OUT INGwTcapProvider::initAppInstState()
");
                return retVal;
        }

  if (-1 == lCurrRole) {
                LogINGwError (false, 0, "[initAppInstState] Error in resolving m
y role");
                LogINGwTrace(false, 0, "OUT INGwTcapProvider::initAppInstState()
");
    return G_FAILURE;
  }

        U8 state = (lCurrRole == TCAP_PRIMARY) ? INS_ACT : INS_SBY;

#endif
        LogINGwTrace(false, 0, "OUT INGwTcapProvider::initAppInstState()");
        return G_SUCCESS;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapProvider::fetchOpcSsnInfo()
{
	LogINGwTrace(false, 0, "IN INGwTcapProvider::fetchOpcSsnInfo()");
	int retVal = G_SUCCESS;

	vector<string> ss7AppInfo;

	string ss7AppOid;
	INGwIfrPrParamRepository::getInstance().getValue(ingwSS7_APP_INFO, 
																									 ss7AppOid);

	if (true == ss7AppOid.empty()) {
		LogINGwTrace(false, 0, "OUT INGwTcapProvidewr::fetchOpcSsnInfo(), failed");
		return (retVal = G_FAILURE);
	}

	//ss7AppInfo = g_tokenizeValue(ss7AppOid, G_PIPE_DELIM);
	g_tokenizeValue(ss7AppOid, G_PIPE_DELIM, ss7AppInfo);

	if (true == ss7AppInfo.empty()) {
		LogINGwTrace(false, 0, "OUT INGwTcapProvidewr::fetchOpcSsnInfo(), failed");
		return (retVal = G_FAILURE);
	}
  
	for (int i=0; i < ss7AppInfo.size(); ++i) {

		vector<string> fieldList;
		g_tokenizeValue(ss7AppInfo[i], G_COMMA_DELIM, fieldList);
    
    for(int i=0; i< fieldList.size();i++) {
	    
      logger.logINGwMsg(false, ALWAYS_FLAG, 0,
      "[fetchOpcSsnInfo] fieldList[%d] = %s",i,(fieldList[i]).c_str());
    }

		if (TCAP_APP_INFO_COUNT != fieldList.size()) {
			LogINGwTrace(false, 0, 
			"OUT INGwTcapProvidewr::fetchOpcSsnInfo(), unequal fields");
			return (retVal = G_FAILURE);
		}
		
		AppIdInfo	      *lAppIdInfo = new AppIdInfo();

		// Application ID
    (*lAppIdInfo).appId   = atoi(fieldList[TCAP_APP_ID_INDEX].c_str());
    
		tcapCheckAppId((*lAppIdInfo).appId, retVal);
		if (retVal == G_FAILURE) {
			logger.logMsg(ERROR_FLAG, 0, 
				"OUT INGwTcapProvidewr::fetchOpcSsnInfo(), Invalid Range of App ID:%d",
				(*lAppIdInfo).appId);
			return retVal;
		}
    //remove
		if ( NULL != m_appIdSsnInfo[(*lAppIdInfo).appId]) {
			logger.logMsg(ERROR_FLAG, 0, 
				"OUT INGwTcapProvidewr::fetchOpcSsnInfo(), Duplicate App ID, %d",
				(*lAppIdInfo).appId);
			return (retVal = G_FAILURE) ;
		}
		else {
      //remove
			m_appIdSsnInfo[(*lAppIdInfo).appId] = new TcapAppIfInfo;
			m_appIdSsnInfo[(*lAppIdInfo).appId]->m_appId = (*lAppIdInfo).appId;
		}

		// SSN
   	(*lAppIdInfo).ssn = atoi(fieldList[TCAP_SSN_INDEX].c_str());
		m_appIdSsnInfo[(*lAppIdInfo).appId]->m_ssn = (*lAppIdInfo).ssn;

		tcapCheckSsn((*lAppIdInfo).ssn, retVal);
		if (retVal == G_FAILURE) {
			logger.logMsg(ERROR_FLAG, 0, 
				"OUT INGwTcapProvidewr::fetchOpcSsnInfo(), Invalid Range of SSN, %d",
				(*lAppIdInfo).ssn);
			return retVal;
		}
		
#if NOT_NEEDED_NOW
		// Tcap Protocol Variant
    (*lAppIdInfo).tcapProtoVar = g_tcapGetProtocolVar(fieldList[TCAP_PCOL_VAR_INDEX]);
#endif	

		// SCCP Protocol Variant
    (*lAppIdInfo).sccpProtoVar = g_sccpGetProtocolVar(fieldList[TCAP_SCCP_PROTOCOL_VAR]);

		// OPC
    (*lAppIdInfo).pc  = g_convertPcToDec((char*)(fieldList[TCAP_OPC_INDEX].c_str()),
						 m_appIdSsnInfo[(*lAppIdInfo).appId]->m_pcDetail, (*lAppIdInfo).sccpProtoVar);

		m_appIdSsnInfo[(*lAppIdInfo).appId]->m_pc = (*lAppIdInfo).pc;

		// add SSN Info. This would be used while
		// doing binding of TCAP Layer
		addSsnInfo ((*lAppIdInfo).ssn, (*lAppIdInfo).appId);

    m_appIdInfo.push_back((*lAppIdInfo));

	  for (int i=0; i < m_appIdInfo.size(); ++i) {
	    logger.logINGwMsg(false, ALWAYS_FLAG, 0,
             "fetchOpcSsnInfo():: PC[%d] PC(ZN-CLU-MEM)[%d-%d-%d] "
             "SSN[%d] AppId[%d] TcapProtoVar[%d] SccpProtoVar[%d]", 
             m_appIdInfo[i].pc, m_appIdInfo[i].m_pcDetail[0],
             m_appIdInfo[i].m_pcDetail[1], m_appIdInfo[i].m_pcDetail[2],
             m_appIdInfo[i].ssn, m_appIdInfo[i].appId, 
             m_appIdInfo[i].tcapProtoVar, m_appIdInfo[i].sccpProtoVar);
    }
    

	}

	LogINGwTrace(false, 0, "OUT INGwTcapProvider::fetchOpcSsnInfo()");
	return retVal;
}


/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapProvider::getSelfInstId(int &p_numOfInstance)
{
	LogINGwTrace(false, 0, "IN INGwTcapProvider::getSelfInstId()");

  int retVal = 0;
  vector<string> value;
  INGwIfrPrParamRepository& paramRep = INGwIfrPrParamRepository::getInstance();
  try {
    int selfId = paramRep.getSelfId();
    std::vector<int> ingwId = paramRep.getINGwList();

    ingwId.push_back(selfId);
    std::sort(ingwId.begin(), ingwId.end());

    p_numOfInstance = ingwId.size();

    int instIdx = 0;
    for (int i = 0; i < ingwId.size(); i++) {
      if (ingwId[i] == selfId) {
        instIdx = i;
        logger.logINGwMsg(false, ALWAYS_FLAG, 0,
        "[getSelfInstId] My Instance Id = <%d> INGw Subsys ID<%d>", 
        instIdx + 1, selfId);

        retVal = instIdx+1;
        break;
      }
    }
  }  
  catch (...) {
  }            
               
	LogINGwTrace(false, 0, "OUT INGwTcapProvider::getSelfInstId()");
  return retVal;   
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
void
INGwTcapProvider::getTcapStatParamIndex()
{
  INGwIfrSmStatMgr &lStatMgr = INGwIfrSmStatMgr::instance();

	INGwTcapStatParam::INGW_INBOUND_DLG_INDX   =
				 lStatMgr.getParamIndex(INGW_INBOUND_DLG);
	INGwTcapStatParam::INGW_INBOUND_CMP_INDX   =
				 lStatMgr.getParamIndex(INGW_INBOUND_CMP);
	INGwTcapStatParam::INGW_INBOUND_NOT_INDX   =
				 lStatMgr.getParamIndex(INGW_INBOUND_NOT);
	INGwTcapStatParam::INGW_INBOUND_ABRT_INDX  =
				 lStatMgr.getParamIndex(INGW_INBOUND_ABRT);
	INGwTcapStatParam::INGW_INBOUND_UNI_INDX   =
				 lStatMgr.getParamIndex(INGW_INBOUND_UNI);
	INGwTcapStatParam::INGW_OUTBOUND_DLG_INDX  =
				 lStatMgr.getParamIndex(INGW_OUTBOUND_DLG);
	INGwTcapStatParam::INGW_OUTBOUND_CMP_INDX  =
				 lStatMgr.getParamIndex(INGW_OUTBOUND_CMP);
	INGwTcapStatParam::INGW_OUTBOUND_NOT_INDX  = 
				 lStatMgr.getParamIndex(INGW_OUTBOUND_NOT);
	INGwTcapStatParam::INGW_OUTBOUND_ABRT_INDX = 
				 lStatMgr.getParamIndex(INGW_OUTBOUND_ABRT);
	INGwTcapStatParam::INGW_OUTBOUND_UNI_INDX =
				 lStatMgr.getParamIndex(INGW_OUTBOUND_UNI);
	INGwTcapStatParam::INGW_INBOUND_BGN_INDEX =
				 lStatMgr.getParamIndex(INGW_INBOUND_BGN);
	INGwTcapStatParam::INGW_OUTBOUND_BGN_INDEX   =
				 lStatMgr.getParamIndex(INGW_OUTBOUND_BGN);
	INGwTcapStatParam::INGW_INBOUND_CNT_INDEX  =
				 lStatMgr.getParamIndex(INGW_INBOUND_CNT);
	INGwTcapStatParam::INGW_OUTBOUND_CNT_INDX  =
				 lStatMgr.getParamIndex(INGW_OUTBOUND_CNT);
	INGwTcapStatParam::INGW_INBOUND_END_INDEX  = 
				 lStatMgr.getParamIndex(INGW_INBOUND_END);
	INGwTcapStatParam::INGW_OUTBOUND_END_INDEX = 
				 lStatMgr.getParamIndex(INGW_OUTBOUND_END);
	INGwTcapStatParam::INGW_INBOUND_PABRT_INDEX =
				 lStatMgr.getParamIndex(INGW_INBOUND_PABRT);
	INGwTcapStatParam::INGW_TRIGR_UABRT_INDEX=
				 lStatMgr.getParamIndex(INGW_TRIGR_UABRT);
	INGwTcapStatParam::INGW_TRIGR_END_INDEX=
				 lStatMgr.getParamIndex(INGW_TRIGR_END);
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapProvider::myRole()
{
	LogINGwTrace(false, 0, "IN INGwTcapProvider::myRole()");
  int retVal = -1;

  try {
    std::string isprimarystr = "";
    INGwIfrPrParamRepository::getInstance().getValue(ingwIS_PRIMARY, 
																									   isprimarystr);
    retVal = (isprimarystr.empty()) ? -1 :
                 (0 > atoi(isprimarystr.c_str()))?-1 :
                 (atoi(isprimarystr.c_str()) == 1)?TCAP_PRIMARY:TCAP_SECONDARY;
  }
  catch (...) {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "Exception caught in myRole()");
  }
   
  logger.logINGwMsg(false, TRACE_FLAG, 0,
					"[myRole] myRole = <%s> <%d>, OUT myRole()", 
					(TCAP_PRIMARY == retVal)?"TCAP_PRIMARY":"TCAP_SECONDARY",
					retVal);

	LogINGwTrace(false, 0, "OUT INGwTcapProvider::myRole()");
  return retVal;   
}

/*
INGwIfrMgrWorkerClbkIntf*
INGwTcapProvider::getTcapCallHandler()
{
	INGwIfrMgrWorkerClbkIntf &incMsgHandler = INGwTcapIncMsgHandler::getInstance();
	INGwIfrMgrWorkerClbkIntf *retVal = &(incMsgHandler);
	return (retVal);
}
*/

INGwIfrMgrWorkerClbkIntf*
INGwTcapProvider::getTcapFtHandler()
{
	INGwIfrMgrWorkerClbkIntf &ftHandler = INGwTcapFtHandler::getInstance();
	INGwIfrMgrWorkerClbkIntf *retVal = &(ftHandler);
	return (retVal);
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
bool
INGwTcapProvider::updateSCCPState(U32 &p_pc, U8 &p_ssn, 
										PcSsnStatusData &p_state, g_PcOrSsn p_flag)
{
	LogINGwTrace(false, 0, "IN INGwTcapProvider::updateSCCPState()");

	bool retVal = false;
	int appId = -1;

	for (int i=0; i < m_ssnInfo.size(); ++i) {
		if (p_ssn == m_ssnInfo[i].ssn) { 
					appId  = i;
					break;
		}
	}

	if (-1 != appId) {
		pthread_mutex_lock(&m_appIdSsnInfoLock);

		if (TCAP_PC == p_flag ) {
			bcopy(&p_state, &m_ssnInfo[appId].pcState, sizeof(PcSsnStatusData));
			m_ssnInfo[appId].pcStateValid = true;
			retVal = true;
		}
		else if (TCAP_SSN == p_flag) {
			bcopy(&p_state, &m_ssnInfo[appId].ssnState, sizeof(PcSsnStatusData));
			m_ssnInfo[appId].ssnStateValid = true;
			retVal = true;
		}
		pthread_mutex_unlock(&m_appIdSsnInfoLock);
	}

	LogINGwTrace(false, 0, "OUT INGwTcapProvider::updateSCCPState()");
  return retVal;   
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
bool 
INGwTcapProvider::getSCCPState(U32 &p_pc, U8 &p_ssn, 
															PcSsnStatusData &p_state, g_PcOrSsn p_flag)
{
	LogINGwTrace(false, 0, "IN INGwTcapProvider::getSCCPState()");
	bool retVal = false;
	int appId = -1;

	for (int i=0; i < m_ssnInfo.size(); ++i) {
		if (p_ssn == m_ssnInfo[i].ssn) { 
					appId  = i;
					break;
		}
	}

	if (-1 != appId) {
		pthread_mutex_lock(&m_appIdSsnInfoLock);

		if (TCAP_PC == p_flag ) {
			if (true == m_ssnInfo[appId].pcStateValid) {
				bcopy(&m_ssnInfo[appId].pcState, &p_state, sizeof(PcSsnStatusData));
				retVal = true;
			}
		}
		else if (TCAP_SSN == p_flag) {
			if (true == m_ssnInfo[appId].ssnStateValid) {
				bcopy(&m_ssnInfo[appId].ssnState, &p_state, sizeof(PcSsnStatusData));
				retVal = true;

			}
			else {
				logger.logMsg(ERROR_FLAG, 0, 
				"TCAP_SSN is asked...but valid flag is false\n");
			}
		}
		pthread_mutex_unlock(&m_appIdSsnInfoLock);
	}

	LogINGwTrace(false, 0, "OUT INGwTcapProvider::getSCCPState()");
  return retVal;   
}

string
INGwTcapProvider::getLoadDistDebugInfo()
{
	if (NULL != m_ldDstMgr ) {
		return m_ldDstMgr->debugLoadDistInfo();
	}
}

INGwSilTx& INGwTcapProvider::getAinSilTxRef()
{
	return mINGwSilTx;
}

INGwSilRx& INGwTcapProvider::getAinSilRxRef()
{
	return mINGwSilRx;
}

int INGwTcapProvider::handleAinSmEvent(int pVal)
{
        LogINGwTrace(false, 0, "IN INGwTcapProvider::handleAinSmEvent()");

  /*
        char str[10];
        sprintf(str,"%d", pVal);
        mpCallController->modifyCfgParam(ASP_SWITCH_OVER, str,
                                                                                                CONFIG_OP_T
YPE_REPLACE,
                                                                                                INGwIfrPrPa
ramRepository::getInstance().getSelfId());
  */

        LogINGwTrace(false, 0, "OUT INGwTcapProvider::handleAinSmEvent()");
        return AIN_SUCCESS;
}


int INGwTcapProvider::run()
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::run()");

	int lRetVal = AIN_SUCCESS;
	
	lRetVal = mINGwSilRx.start();
	if(AIN_SUCCESS != lRetVal) {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
							 "Start on INGwSilRx failed with code <%d>",
							 lRetVal);
		
		LogINGwTrace(false, 0,"OUT INGwTcapProvider::run()");
		return lRetVal;
	}
	
  
	lRetVal = mINGwSmWrapper->changeState(INGwIwfBaseProvider::PROVIDER_STATE_RUNNING);
        // ReturnVal 1 is SUCCESS
	if(1 != lRetVal) {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
                       "ChangeState to RUNNING on SM wrapper failed with code <%d>",
		       lRetVal);
		
		LogINGwTrace(false, 0,"OUT INGwTcapProvider::run()");
		return lRetVal;
	}

	LogINGwTrace(false, 0,"OUT INGwTcapProvider::run()");
	return AIN_SUCCESS;
}


int INGwTcapProvider::stop()
{
	LogINGwTrace(false, 0,"IN INGwTcapProvider::stop()");

	int lRetVal = AIN_SUCCESS;
	
	lRetVal = mINGwSilRx.stop();
	if(AIN_SUCCESS != lRetVal) {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
							 "Stop on INGwSilRx failed with code <%d>",
							 lRetVal);
		
		LogINGwTrace(false, 0,"OUT INGwTcapProvider::stop()");
		return lRetVal;
	}
		
	lRetVal = mINGwSmWrapper->changeState(INGwIwfBaseProvider::PROVIDER_STATE_STOPPED);
	if(AIN_SUCCESS != lRetVal) {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
							 "ChangeState to STOPPED on SM wrapper failed with code <%d>",
							 lRetVal);
		
		LogINGwTrace(false, 0,"OUT INGwTcapProvider::stop()");
		return lRetVal;
	}
	
	delete mINGwSmWrapper;
	mINGwSmWrapper = NULL;

	LogINGwTrace(false, 0,"OUT INGwTcapProvider::stop()");
	return AIN_SUCCESS;
}


void INGwTcapProvider::setSubsysIdProcId()
{
	LogINGwTrace(false, 0, "IN INGwTcapProvider::setSubsysIdProcId()");

  INGwIfrPrParamRepository& paramRep = INGwIfrPrParamRepository::getInstance();
  try {
    int selfId = (U16)paramRep.getSelfId();
    std::vector<int> ingwId = paramRep.getINGwList();

    ingwId.push_back(selfId);
    std::sort(ingwId.begin(), ingwId.end());

    int procId= DEF_INGW_SELF_PROC_ID;

    for (int i = 0; i < ingwId.size(); i++) {
      subsysIdProcIdMap[ingwId[i]] = procId++;
    }

    for( map<int,int>::iterator ii = subsysIdProcIdMap.begin(); 
         ii != subsysIdProcIdMap.end(); ++ii) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "INGwSubsysId[%d]->ProcId[%d]",
                        (*ii).first, (*ii).second);
      if ((*ii).first == selfId) {
        selfProcId = (*ii).second;
        logger.logINGwMsg(false, ALWAYS_FLAG, 0, "SelfProcId[%d]", selfProcId);
      }
    }
  }  
  catch (...) {
  }            
               
	LogINGwTrace(false, 0, "OUT INGwTcapProvider::setSubsysIdProcId()");
  return;
}

int INGwTcapProvider::getProcIdList(U16 procIdList[])
{
	LogINGwTrace(false, 0, "IN INGwTcapProvider::getProcIdList()");

  int index = 0;

  for( map<int,int>::iterator ii = subsysIdProcIdMap.begin(); 
       ii != subsysIdProcIdMap.end(); ++ii) {
    logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwSubsysId[%d]->ProcId[%d]",
                      (*ii).first, (*ii).second);
    
    if (index < MAX_PROC_IDS) {
      procIdList[index++] = (U16)(*ii).second;
    }
    else {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                        "Exceeded maximum number of PROC_IDs");
    }

    //Used to force CCPU stack configuration on single Node1
    //on a FT setup with stack on Node1 and Node2.
    //If set to "1" then it is enabled otherwise it is disabled
    char * singleNodeCfg = getenv("CCPUSTK_SGL_ND_CFG");
    if ( singleNodeCfg && 0 == strcasecmp(singleNodeCfg, "1")) {
      break;
    }
  }

	LogINGwTrace(false, 0, "OUT INGwTcapProvider::getProcIdList()");
  return index;   
}

int INGwTcapProvider::getProcIdForSubsysId(U32 subsysId)
{
	LogINGwTrace(false, 0, "IN INGwTcapProvider::getProcIdForSubsysId()");

  int retVal = 0;
	map<int,int>::iterator it = subsysIdProcIdMap.find(subsysId);

	if(it != subsysIdProcIdMap.end())
		retVal = (*it).second;

	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
							 "getProcIdForSubsysId: subSysId:%d, procId:%d",
							 subsysId, retVal);

	LogINGwTrace(false, 0, "OUT INGwTcapProvider::getProcIdList()");
  return retVal;   
}


int INGwTcapProvider::setProcStatus()
{
	LogINGwTrace(false, 0, "IN INGwTcapProvider::setProcStatus()");

  int  retVal = G_FAILURE;

  U16 procIdList[MAX_PROC_IDS];

  int lProcCount = getProcIdList(procIdList);
  int i=0;
  for (i = 0; i < lProcCount; i++) {
    ProcStatus * lprocSts = new ProcStatus();
    m_incProcStatusMap[procIdList[i]] = lprocSts;
    logger.logINGwMsg(false, TRACE_FLAG, 0, 
           "ProcId[%d] Status initialized to EmsStatus[%d],"
           "RelayStatus[%d], INCStatus[%d]",
           procIdList[i], lprocSts->stateFromEms, lprocSts->stateFromRelay,
           lprocSts->stateFromInc);
  }

  if (i > 0) retVal = G_SUCCESS;

	LogINGwTrace(false, 0, "OUT INGwTcapProvider::setProcStatus()");
  return retVal;   
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
int
INGwTcapProvider::regAppWithStackWrapper()
{
  LogINGwTrace(false, 0,"IN INGwTcapProvider::regAppWithStackWrapper()");

  int retVal        = G_SUCCESS;

	m_isRunning = true;
  while (m_isRunning) {

    if (true == m_registerQ.empty()) {
      sleep (TCAP_REG_SLEEP);
      continue;
    }

    TcapRegInfo *lregInfo = m_registerQ.front();
    m_registerQ.pop();

		// check if we have configured local ssn 
		// If not we will again push back in Q.
		S16 suId = lregInfo->m_suId;
		S16 spId = 0;
		U8  ssn  = 0;
		getSpIdForSuId(suId, spId, ssn);

		if(spId == -1) {
    	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
			"++VER++ [regAppWithStackWrapper] LocalSSN has"
			" not been configured for ssn[%d] suId[%d] spId[%d]"
			" Not calling Bind", ssn, suId, spId);

      sleep (TCAP_REG_SLEEP);
			m_registerQ.push(lregInfo);
			continue;
		}

    logger.logINGwMsg(false, ALWAYS_FLAG, 0,
    "++VER++ [regAppWithStackWrapper] [%s] for suId[%d] SpId[%d] ssn[%d]", 
    (lregInfo->m_regType == TCAP_DE_REGISTER)?"Deregistering":"Registering",
    suId, spId, ssn);

		INGwTcapWorkUnitMsg workUnitMsg;
    memset(&workUnitMsg,0,sizeof(INGwTcapWorkUnitMsg));

		if(lregInfo->m_regType == TCAP_REGISTER)
		{
    	workUnitMsg.eventType = EVTSTUBNDREQ;
    	workUnitMsg.ss7SteMgmt.mgmt.steReq.uStat= SS_UIS;
		}
		else if(lregInfo->m_regType == TCAP_DE_REGISTER)
		{
    	workUnitMsg.eventType = EVTSTUUBNDREQ;
      logger.logINGwMsg(false, ALWAYS_FLAG, 0,
      "Deregistering SuId<%d>, SpId<%d>, ssn<%d>",suId,spId,ssn);
 
		}
  
		workUnitMsg.m_suId = suId;
		workUnitMsg.m_spId = spId;

    workUnitMsg.ss7SteMgmt.mgmt.steReq.aSsn = ssn;

		logger.logINGwMsg(false, VERBOSE_FLAG, 0,"sending %s",
    workUnitMsg.eventType == EVTSTUBNDREQ?"EVTSTUBNDREQ":
    workUnitMsg.eventType == EVTSTUUBNDREQ?"EVTSTUUBNDREQ":"UNKNOWN_REQ");
      
		mINGwSilTx.sendTcapReq(&workUnitMsg);

    if (NULL != lregInfo) {
      delete lregInfo;
      lregInfo = 0;
    }
  }

  LogINGwTrace(false, 0,"OUT INGwTcapProvider::regAppWithStackWrapper()");
  return retVal;
}

void 
INGwTcapProvider::updateSsnState(S16 suId, g_tcapSsnState state) 
{
	U8 lSsn;
	for(int i=0; i < m_ssnInfo.size(); ++i)
	{
		if(m_ssnInfo[i].suId == suId)
		{
			m_ssnInfo[i].regState = state;
      m_ssnInfo[i].ssnStateValid = true;
      if(TCAP_REGISTERED == state)
      {
         m_ssnInfo[i].ssnState.ustat = SS_UIS; 
      }
      else if(TCAP_NOT_REGISTERED == state)
      {
         m_ssnInfo[i].ssnState.ustat = SS_UOS;
      } 
			lSsn = m_ssnInfo[i].ssn;
			break;
		}
	}

	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
 	"++VER++ ssn[%d] suId[%d] State[%d] updated", suId, lSsn, state);
}

bool
INGwTcapProvider::getSsnInfo(U8 p_ssn, S16 &suId, S16 &spId)
{
	bool found = false;

	for (int i=0; i < m_ssnInfo.size(); ++i) 
	{
		if(m_ssnInfo[i].ssn == p_ssn)
		{
			suId = m_ssnInfo[i].suId;
			spId = m_ssnInfo[i].spId;
			found = true;
			break;
		}
	}

	return found;
}

void
INGwTcapProvider::getSpIdForSuId(S16 &suId, S16 &spId, U8 &ssn)
{
	for (int i=0; i < m_ssnInfo.size(); ++i) 
	{
		if(m_ssnInfo[i].suId == suId)
		{
			spId = m_ssnInfo[i].spId;
			ssn  = m_ssnInfo[i].ssn;
			break;
		}
	}
}

void
INGwTcapProvider::getSuIdForSpId(S16 &suId, S16 &spId, U8 &ssn)
{
	for (int i=0; i < m_ssnInfo.size(); ++i) 
	{
		if(m_ssnInfo[i].spId == spId)
		{
			suId = m_ssnInfo[i].suId;
			ssn  = m_ssnInfo[i].ssn;
			break;
		}
	}
}

bool
INGwTcapProvider::updateSsnInfo(U8 p_ssn, S16 spId)
{
	bool found = false;
	for (int i=0; i < m_ssnInfo.size(); ++i) 
	{
		if(m_ssnInfo[i].ssn == p_ssn)
		{
			m_ssnInfo[i].spId = spId;
			found = true;
			break;
		}
	}

	return found;
}

void
INGwTcapProvider::addSsnInfo(U8 p_ssn, S16 suId)
{
	bool found = false;

	for (int i=0; i < m_ssnInfo.size(); ++i) 
	{
		if(m_ssnInfo[i].ssn == p_ssn)
		{
			found = true;
			break;
		}
	}

	if(!found) 
	{
		SsnInfo ssnInf;
		ssnInf.ssn = p_ssn;
		ssnInf.suId = m_ssnInfo.size()+1;
		ssnInf.spId = -1;

		m_ssnInfo.push_back(ssnInf);
	}
}

bool
INGwTcapProvider::getPcSsnForSuId(vector<U32> &p_pcVector, U8 &p_ssn, S16 &p_suId)
{
	bool found = false;
	for(int i=0; i < m_appIdInfo.size(); ++i)
	{
		if(m_appIdInfo[i].appId == p_suId)
		{
			p_pcVector.push_back(m_appIdInfo[i].pc);
			p_ssn = m_appIdInfo[i].ssn;
			found = true;
			break;
		}
	}

	return found;
}

void
INGwTcapProvider::getSsnForSuId(U8 &p_ssn, S16 &p_suId)
{
	for(int i=0; i < m_ssnInfo.size(); ++i)
	{
		if(m_ssnInfo[i].suId == p_suId) {
			p_ssn = m_ssnInfo[i].ssn;
			break;
		}
	}
}

int 
INGwTcapProvider::getFpMask(){
  return mFpMask;
}

void
INGwTcapProvider::setFpMask(int pFpMask){
  mFpMask = pFpMask;

#ifdef MSG_FT_TEST
//these fpmasks 21 - 30 are reserved for hookIface member function 
//of INGwTcapFtHandler class
  if((mFpMask >=21) && (mFpMask <= 35 )) 
  {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"setFpMask unblocking BR holding "
                                          "thread");

    INGwTcapFtHandler::getInstance().hookIface(pFpMask); 
  }
#endif
}

bool
INGwTcapProvider::verifyOpcSsnInConfigList(unsigned long &p_pc, U8 &p_ssn) {
		bool retVal = true;
		for (int i=0; i < m_appIdInfo.size(); ++i) {
      logger.logINGwMsg(false,VERBOSE_FLAG,0,"verifyOpcSsnInConfigList"
        "default pc[%d] = <%d> param pc <%d>",i,m_appIdInfo[i].pc,p_pc);
			if(p_ssn != 0 && p_pc != 0) {
				if ((m_appIdInfo[i].pc == p_pc) && (m_appIdInfo[i].ssn == p_ssn)) {
					return retVal;
				}
			}
			else if(p_ssn == 0) { // check for PC only
				if (m_appIdInfo[i].pc == p_pc) {
					return retVal;
				}
		 }
		 else if(p_pc == 0) { // check for SSN only
				if (m_appIdInfo[i].ssn == p_ssn) {
					return retVal;
				}
		 }
		}
		return (false);
	}

#ifdef INC_DLG_AUDIT
int INGwTcapProvider::auditTcapDlg() 
{
  logger.logMsg (ALWAYS_FLAG, 0, "Entering auditTcapDlg(), Total <%d> saps available", m_ssnInfo.size());
  int sapId = -1;
  int liResult    = 0;
  int liRequestId = (int) pthread_self ();

  //int rtVal = sgLoDbGetMyState();
  //logger.logINGwMsg(false,TRACE_FLAG,0,"SG ROLE from auditTcapDlg %d",rtVal);

  if (!(mINGwSmWrapper->getDistInst()))
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }
  for(int i =0 ; i< m_ssnInfo.size(); i++)
  {
      
    sapId = m_ssnInfo[i].spId;

    logger.logMsg (ALWAYS_FLAG, 0, "auditTcapDlg(): SapId:%d",sapId);

    if (i == 0) {
       mINGwSmWrapper->getDistInst()->ss7SigtranStackRespPending = 1;
     }
     else {
       mINGwSmWrapper->getDistInst()->ss7SigtranStackRespPending++;
     }

    INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

    qMsg->t.stackData.req.u.audit.tcapSapId = sapId;

    qMsg->mSrc = BP_AIN_SM_SRC_EMS;
    qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    qMsg->t.stackData.stackLayer = BP_AIN_SM_TCA_LAYER;
    qMsg->t.stackData.cmdType    = AUDIT_INC;
    qMsg->t.stackData.procId     = SFndProcId();
    qMsg->t.stackData.miRequestId = liRequestId;//transaction Id: BP_AIN_SM_AUDIT_TRANSID;

     //post the message to the Distributor
     if (mINGwSmWrapper->getDistInst()->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "Audit msg post failed to distributor");
       delete qMsg;
       return -1;
     }

  INGwSmBlockingContext *lpContext = 0;
 	//*resp = new Ss7SigtranStackResp;

		//block the current thread
    if (mINGwSmWrapper->getBlockOperation (liRequestId) == BP_AIN_SM_OK)
    {
      if (mINGwSmWrapper->getRemoveBlockOperation (liRequestId, lpContext))
      {
        if (lpContext->returnValue != BP_AIN_SM_OK)
        {
					logger.logMsg (ERROR_FLAG, 0,
         	"failed to remove blocking ctx lpContext->returnValue "
				  "!= BP_AIN_SM_OK");
          liResult = -1;
        }
      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
            "failed to remove blocking ctx ");
        liResult = -1;
      }
    }
  } //for loop

  logger.logMsg (ALWAYS_FLAG, 0, "Leaving auditTcapDlg()");
  return true;
}

void
INGwTcapProvider::handleAuditCfm()
{
  logger.logINGwMsg(false,TRACE_FLAG,0,"In handleAuditCfm");

  logAuditInfo();

  INGwIfrUtlBitArray* lpDlgBitArray = TcapMessage::cloneDialogueStateMap();
  int liDlgCnt = 0;
  int liIndex = 1;
  TcapMessage ltcMsgObj;

  S16 suId = -1;
  S16 spId = -1;
  U8  ssn = 255;


  int liBothActive  = 0;
  int liStackOnly   = 0;
  int liINGwOnly    = 0;

  for(int sapIter = 0; sapIter < gDlgInvBuff[0][0]; sapIter++) 
  {
    //over a sap
    spId = gDlgInvBuff[liIndex][0];
    liDlgCnt     = gDlgInvBuff[liIndex][1];
    liIndex++;
    int liTotalDlg = liIndex+liDlgCnt;
    logger.logINGwMsg(false,VERBOSE_FLAG,0,"handleAuditCfm sapId <%d dlgCnt<%d>",spId, liDlgCnt);


    for(liIndex; liIndex < liTotalDlg; liIndex++) 
    {
      if(1 == lpDlgBitArray->getBitState(gDlgInvBuff[liIndex][0]))
      {
        //all is well
        lpDlgBitArray->resetBitState(gDlgInvBuff[liIndex][0]);
        ++liBothActive;
        
      }
      else
      {
        //stack is having but we don't, abort it
        //TBD:
        
        getSuIdForSpId(suId,spId, ssn);

        logger.logINGwMsg(false,ERROR_FLAG,0,"handleAuditCfm dlg <%d> suId<%d>"
        " spId <%d> ssn <%d> aborting", gDlgInvBuff[liIndex][0], suId, spId,
        ssn);

        ++liStackOnly;
        ltcMsgObj.closeDialogue(0x01, gDlgInvBuff[liIndex][0], suId, spId, false,
                            true, false, true);
      }
    } 
  //over a sap 
  }

  vector<int> lvector = lpDlgBitArray->getAllSetBitIndex();
  liINGwOnly = lvector.size();

  if(0 != liINGwOnly) 
  {
    //INC is having it but stack is not  having it
    //generate provider abort and clean dialogue
    TcapMessage::auditDlg(lvector);
  }
  else 
  {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleAuditCfm no need to cleanup");
  }

/*
now "clone" array is not deallocated 
  delete lpDlgBitArray; lpDlgBitArray = 0;
*/
  gDlgInvBuff[0][0] = 0;
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out handleAuditCfm liStackOnly<%d>"
  " liBothActive<%d> liINGwOnly<%d> total <%d>",
   liStackOnly, liBothActive, liINGwOnly, 
   (liStackOnly + liBothActive+ liINGwOnly));
}

void
INGwTcapProvider::logAuditInfo()
{
  char lBuf[4096];
  int lBufLen = 0;

  int liDlgCnt = 0, i = 0;
  int liIndex = 1, spId = 0;

  lBufLen += sprintf(lBuf + lBufLen,"logAuditInfo <%d> No. of saps\n",
                    gDlgInvBuff[0][0]);

  for(int sapIter = 0; sapIter < gDlgInvBuff[0][0]; sapIter++) 
  {
    //over a sap
    spId = gDlgInvBuff[liIndex][0];
    liDlgCnt     = gDlgInvBuff[liIndex][1];
    liIndex++;
   
    lBufLen += sprintf(lBuf + lBufLen,"\nlogAuditInfo spId <%d>"
    " ActiveDlgs<%d>\n", spId, liDlgCnt);

    int totalDlgs = liIndex + liDlgCnt;

    for(liIndex,i=0; liIndex < totalDlgs; liIndex++,i++) 
    {
      //if(!(lBufLen & 15)) {
      if(!(i & 3)) {
        lBufLen += sprintf(lBuf + lBufLen,"\n\t");
      }
  
      lBufLen += sprintf(lBuf + lBufLen,"%d ",gDlgInvBuff[liIndex][0]);

      if(lBufLen >= 4000) {
        INGwTcapMsgLogger::getInstance().dumpString(lBuf); 
        lBufLen = 0;
        lBuf[0] = 0;
      }
    } 
  //over a sap 
  }

  INGwTcapMsgLogger::getInstance().dumpString(lBuf); 
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out logAuditInfo");
}

#endif

int
INGwTcapProvider::getSapCnt() 
{
  int retVal = 0; 
  logger.logINGwMsg(false,TRACE_FLAG,0,"In getSapCnt");
    retVal = m_ssnInfo.size();
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out getSapCnt <%d>", retVal);
  return retVal;
}
