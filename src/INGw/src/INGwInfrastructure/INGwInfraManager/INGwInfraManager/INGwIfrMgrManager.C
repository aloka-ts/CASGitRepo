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
//     File:     INGwIfrMgrManager.C
//
//     Desc:     Has class INGwIfrMgrManager.
//               This is the main class of Infrastruture. It does the initialisation and shutdown
//               of the various INGw components.
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal  10/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraManager");

#include <pthread.h>
#include <map>
#include <list>
#include <memory>
#include <strstream>

#include <Util/imOid.h>
#include <Util/imAlarmCodes.h>
#include <Util/CoreScheduler.h>
#include <Util/StatCollector.h>
#include <Util/SchedulerMsg.h>
#include <stdlib.h>

#include <Agent/BayAgentImpl.h>

#include <INGwInfraManager/INGwIfrMgrManager.h>
#include <INGwInfraManager/INGwIfrMgrFtIface.h>
#include <INGwInfraManager/INGwIfrMgrIPAddrHandler.h>
#include <INGwInfraManager/INGwIfrMgrAlarmMgr.h>
#include <INGwInfraManager/INGwIfrMgrNotificationMgr.h>
#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>
#include <INGwInfraManager/INGwIfrMgrAgentClbkImpl.h>

#include <INGwInfraUtil/INGwIfrUtlConfigurable.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraParamRepository/INGwIfrPrConfigMgr.h>
#include <INGwInfraTelnetIface/INGwIfrTlIfTelnetIntf.h>
#include <INGwInfraMsrMgr/MsrMgr.h>
#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwFtPacket/INGwFtPktRoleMsg.h>
#include <INGwFtPacket/INGwFtPktDeleteCallMsg.h>
#include <INGwFtPacket/INGwFtPktLoadDistMsg.h>
#include <INGwFtPacket/INGwFtPktCallDataMsg.h>
#include <INGwFtPacket/INGwFtPktCreateTcapSession.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>
#include <INGwFtPacket/INGwFtPktTcapMsg.h>
#include <INGwInfraManager/INGwIfrMgrAlarmMgr.h>
#include <INGwStackManager/INGwSmWrapper.h>
#include <INGwStackManager/INGwSmQueueMsg.h>
#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>
#include <INGwFtPacket/INGwFtPktAddLink.h>
#include <INGwFtPacket/INGwFtPktAddLinkset.h>
#include <INGwFtPacket/INGwFtPktDelLinkset.h>
#include <INGwFtPacket/INGwFtPktDelLink.h>
#include <INGwFtPacket/INGwFtPktAddNw.h>
#include <INGwFtPacket/INGwFtPktDelNw.h>
#include <INGwFtPacket/INGwFtPktAddRoute.h>
#include <INGwFtPacket/INGwFtPktDelRoute.h>
#include <INGwFtPacket/INGwFtPktAddSsn.h>
#include <INGwFtPacket/INGwFtPktDelSsn.h>
#include <INGwFtPacket/INGwFtPktAddUserPart.h>
#include <INGwFtPacket/INGwFtPktDelUserPart.h>
#include <INGwFtPacket/INGwFtPktM3uaAssocUp.h>
#include <INGwFtPacket/INGwFtPktM3uaAssocDown.h>
#include <INGwFtPacket/INGwFtPktAddPs.h>
#include <INGwFtPacket/INGwFtPktDelPs.h>
#include <INGwFtPacket/INGwFtPktAddEp.h>
#include <INGwFtPacket/INGwFtPktDelEp.h>
#include <INGwFtPacket/INGwFtPktAddPsp.h>
#include <INGwFtPacket/INGwFtPktDelPsp.h>
#include <INGwFtPacket/INGwFtPktAddGtAddrMap.h>
#include <INGwFtPacket/INGwFtPktDelGtAddrMap.h>
#include <INGwFtPacket/INGwFtPktAddGtRule.h>
#include <INGwFtPacket/INGwFtPktDelGtRule.h>
#include <INGwFtPacket/INGwFtPktModLink.h>
#include <INGwFtPacket/INGwFtPktModLinkset.h>
#include <INGwFtPacket/INGwFtPktModPs.h>
#include <INGwFtPacket/INGwFtPktAspActive.h>
#include <INGwFtPacket/INGwFtPktAspInActive.h>
#include <INGwFtPacket/INGwFtPktStkConfigStatus.h>


#include <INGwFtPacket/INGwFtPktTcapCallSeqAck.h>
#include <INGwFtPacket/INGwFtPktUpdateTcapSession.h>
#include <INGwFtPacket/INGwFtPktCreateTcapSession.h>
#include <INGwFtPacket/INGwFtPktTermTcapSession.h>
#include <INGwFtPacket/INGwFtPktSasHBFailure.h>
#include <INGwFtPacket/INGwFtPktSynchUpMsg.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>

extern bool configLoopBack;
extern bool isReplayInProgress;

extern BpGenUtil::INGwIfrSmAppStreamer *ingwMsgStream;
extern string
copyAddNetwork(Ss7SigtranSubsReq *req,
    const RSIEmsTypes::AddNetwork &addNw);

extern int
copyAddUserPart(Ss7SigtranSubsReq *req,  
    const RSIEmsTypes::AddUserPart &addUsrPart);

extern int 
copyAddLinkset(Ss7SigtranSubsReq *req,  
    const RSIEmsTypes::AddLinkSet &addLnkset);

extern string 
copyAddLocalSsn(Ss7SigtranSubsReq *req,
    const RSIEmsTypes::AddSsn &ssn);

extern int
copyAddRoute(Ss7SigtranSubsReq *req,
    const RSIEmsTypes::AddRoute &addRoute);   

extern int
copyAddLink(Ss7SigtranSubsReq *req, 
    const RSIEmsTypes::AddLink &addLink);  

extern void
copyAddRule(Ss7SigtranSubsReq *req,
    const RSIEmsTypes::AddGtRule &addRule);

extern int
copyAddGtAddrMap(Ss7SigtranSubsReq *req,
    const RSIEmsTypes::AddAddrMapCfg &addAddrMap);

extern void 
copyAddEndPoint(Ss7SigtranSubsReq *req, 
    const RSIEmsTypes::AddEndPoint &ep);

extern int
copyAddPs(Ss7SigtranSubsReq *req, 
    const RSIEmsTypes::AddPs &ps);

extern void 
copyAddPsp(Ss7SigtranSubsReq *req,
    const RSIEmsTypes::AddPsp &psp); 

extern void
copyM3uaAssocUp (Ss7SigtranSubsReq *req,
						const RSIEmsTypes::M3uaAssocUp &m3ua);

//PROVIDERS
#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwIwf/INGwIwfProvider.h>

#include <INGwTcapProvider/INGwTcapProvider.h>
int INGwIfrMgrManager::miStatParamId_CreateTcapSession = 0;
int INGwIfrMgrManager::miStatParamId_TcapCallSeqAck    = 0;
int INGwIfrMgrManager::miStatParamId_UpdateTcapSession = 0;
int INGwIfrMgrManager::miStatParamId_CloseTcapSession  = 0;
int INGwIfrMgrManager::miStatParamId_CallBkup          = 0;
int INGwIfrMgrManager::miStatParamId_FtDelCall         = 0;
int INGwIfrMgrManager::miStatParamId_LoadDistMsg       = 0;
int INGwIfrMgrManager::miStatParamId_SasHbFailure      = 0;
int INGwIfrMgrManager::miStatParamId_TcapDlgInfo       = 0;
int INGwIfrMgrManager::miStatParamId_StackConfig       = 0;
int INGwIfrMgrManager::miStatParamId_AddLink 				   = 0;
int INGwIfrMgrManager::miStatParamId_DelLink 				   = 0;
int INGwIfrMgrManager::miStatParamId_AddLinkset 			 = 0;
int INGwIfrMgrManager::miStatParamId_DelLinkset 		   = 0;
int INGwIfrMgrManager::miStatParamId_AddNw 						 = 0;
int INGwIfrMgrManager::miStatParamId_DelNw 						 = 0;
int INGwIfrMgrManager::miStatParamId_AddRoute 				 = 0;
int INGwIfrMgrManager::miStatParamId_DelRoute    			 = 0;
int INGwIfrMgrManager::miStatParamId_AddSsn    				 = 0;
int INGwIfrMgrManager::miStatParamId_DelSsn   				 = 0;
int INGwIfrMgrManager::miStatParamId_AddUsrPart        = 0;
int INGwIfrMgrManager::miStatParamId_DelUsrPart        = 0;
int INGwIfrMgrManager::miStatParamId_AssocDown         = 0;
int INGwIfrMgrManager::miStatParamId_AssocUp           = 0;
int INGwIfrMgrManager::miStatParamId_AddPs             = 0;
int INGwIfrMgrManager::miStatParamId_DelPs             = 0;
int INGwIfrMgrManager::miStatParamId_AddEp             = 0;
int INGwIfrMgrManager::miStatParamId_DelEp             = 0;
int INGwIfrMgrManager::miStatParamId_AddPsp            = 0;
int INGwIfrMgrManager::miStatParamId_DelPsp            = 0;
int INGwIfrMgrManager::miStatParamId_AddRule           = 0;
int INGwIfrMgrManager::miStatParamId_DelRule           = 0;
int INGwIfrMgrManager::miStatParamId_AddGtAddrMap      = 0;
int INGwIfrMgrManager::miStatParamId_DelGtAddrMap      = 0;
int INGwIfrMgrManager::miStatParamId_ModLink           = 0;
int INGwIfrMgrManager::miStatParamId_ModLinkset        = 0;
int INGwIfrMgrManager::miStatParamId_ModPs             = 0;
int INGwIfrMgrManager::miStatParamId_CofigStatus       = 0;

int mgMaxRegularBufferSize;
bool gbCcmOnlyHa = false;
INGwIfrMgrManager* INGwIfrMgrManager::m_Self = NULL;

INGwIfrMgrManager& 
INGwIfrMgrManager::getInstance(void)
{
	 if(m_Self == NULL)
			m_Self = new INGwIfrMgrManager;
	 return *m_Self;
}

INGwIfrMgrManager::~INGwIfrMgrManager()
{
}

void 
INGwIfrMgrManager::initialize(int p_Argc, const char** p_Argv) throw (INGwIfrUtlReject)
{ 
   LogINGwTrace(false, 0, "IN initialize()");

   int errCode = pthread_rwlock_init(&m_RWLock, 0);

   if(0 != errCode)
   {
      logger.logINGwMsg(false, ERROR_FLAG, -1, "pthread_rwlock_init failed [%s]", 
                      strerror(errCode));
      LogINGwTrace(false, 0, "OUT initialize()");
      _THROW_REJECT("INIT_FAILED")
   }

   INGwIfrPrConfigMgr::getInstance();

   m_ParamRep.initialize(p_Argc, p_Argv);
	 setFtStatParamIndex();

   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "Parameter repository initialised.");
   try {
      RSIEmsTypes::ConfigurationDetail_var threadDet = 
         BayAgentImpl::getInstance().getConfigParam(
          BayAgentImpl::getInstance().getSubsystemID(), ingwTCAP_WORKER_THREAD_COUNT);
      m_ParamRep.setValue(ingwTCAP_WORKER_THREAD_COUNT, threadDet->paramValue.in());

      RSIEmsTypes::ConfigurationDetail_var threadDetSip = 
         BayAgentImpl::getInstance().getConfigParam(
          BayAgentImpl::getInstance().getSubsystemID(), ingwSIP_WORKER_THREAD_COUNT);
      m_ParamRep.setValue(ingwSIP_WORKER_THREAD_COUNT, threadDetSip->paramValue.in());
   }
   catch(...) {
   }

   new RSI_NSP_CCM::INGwIfrMgrNotificationMgr();

   EmsConfigurationManager &cfgMgr = EmsConfigurationManager::getInstance();
   string refIP = cfgMgr.getParameter("Ref_Network_IP");

   m_ParamRep.setValue(INGW_NW_REF_IP_ADDRESS, refIP);

   logger.logINGwMsg(false, ALWAYS_FLAG, 0, "NW ref IP [%s]", refIP.c_str());

   mgMaxRegularBufferSize = 5000;
   try {
      string value = m_ParamRep.getValue(ingwDEBUG_CHECK_ALLOC_SIZE_IN_BYTES);
      mgMaxRegularBufferSize = strtol(value.c_str(), NULL, 10);
   }
   catch(...) {
      logger.logINGwMsg(false, VERBOSE_FLAG, 0, "value for [%s] missing"
          " using the default value [%d]" ingwDEBUG_CHECK_ALLOC_SIZE_IN_BYTES, 
          mgMaxRegularBufferSize);
   }

   logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Value of [%s] is  [%d]", 
       ingwDEBUG_CHECK_ALLOC_SIZE_IN_BYTES, mgMaxRegularBufferSize);

   INGwIfrMgrAlarmMgr& alarmMgr = INGwIfrMgrAlarmMgr::getInstance();
   alarmMgr.initialize();

   gbCcmOnlyHa = false;

   m_CoreScheduler = CoreScheduler::initialize(1800000, 1000);
   m_ExtraScheduler = CoreScheduler::addedInstance(64000, 250);

   INGwIfrMgrThreadMgr::getInstance().initialize();

	 //Init provider map
	 INGwIwfProvider& iwfProvider = INGwIwfProvider::getInstance();
	 INGwSpSipProvider& sipProvider = INGwSpSipProvider::getInstance();
	 INGwTcapProvider& tcapProvider = INGwTcapProvider::getInstance();

	 m_ProviderMap[iwfProvider.getProviderType()]  = &iwfProvider;
	 m_ProviderMap[sipProvider.getProviderType()]  = &sipProvider;
	 m_ProviderMap[tcapProvider.getProviderType()] = &tcapProvider;

   LogINGwTrace(false, 0, "OUT initialize()");
}


void 
INGwIfrMgrManager::registerIfaceWithAgent(void)
{
   //
   // NOTE - INGW registers with Agent for config and performace as OID
   // "xxx" as no other component should register with Agent for config and
   // performace. 
   //
   bool retVal = true;
   BayAgentImpl& agent = (*BayAgentImpl::instance());

   retVal = agent.registerCfgIf(INGW_BASE, configIfForINGw, this);
   logger.logMsg(ALWAYS_FLAG, 0, "+VER+ registerCfgIf INGW_BASE<%d>",retVal);

   retVal = agent.registerCfgIf(ngwCLI_PROBE_BASE, configIfForINGw, this);
   logger.logMsg(ALWAYS_FLAG, 0, "+VER+ registerCfgIf ngwCLI_PROBE_BASE<%d>",retVal);

   retVal = agent.registerPerformanceIf(INGW_BASE, performanceIfForINGw, this);
   logger.logMsg(ALWAYS_FLAG, 0,"+VER+ registerPerformanceIf <%d>",retVal); 

   retVal = agent.registerStartupIf(ingwCOMMON_BASE, startupIfForINGw, this);
   logger.logMsg(ALWAYS_FLAG, 0, "+VER+ registerStartupIf <%d>",retVal);

   retVal = agent.registerChangeStateIf(ingwCOMMON_BASE, changeStateIfForINGw, this);
   logger.logMsg(ALWAYS_FLAG, 0, "+VER+ registerChangeStateIf <%d>",retVal);

   retVal = agent.registerOidChangeIf(oidChangedIfForINGw, this);
   logger.logMsg(ALWAYS_FLAG, 0, "+VER+ registerOidChangeIf <%d>",retVal);

   retVal = agent.registerReconfigMeasMgrIf (reconfigMsrMgrForINGw);
   logger.logMsg(ALWAYS_FLAG, 0, "+VER+ registerReconfigMeasMgrIf <%d>",retVal);

   retVal = agent.registerSubsysAddIf(newSubsysAddedIf, this);
   logger.logMsg(ALWAYS_FLAG, 0, "+VER+ registerSubsysAddIf <%d>",retVal);

	 retVal = agent.registerSs7SigtranCfgIf(ss7ConfigurationClbk, this);
   logger.logMsg(ALWAYS_FLAG, 0,  "+VER+ registerSs7SigtranCfgIf <%d>",retVal);

}

//Overriding Base class function of INGwIfrUtlConfigurable
// This function will be call when there is change in INGW
// parameters via ems console

int 
INGwIfrMgrManager::configure(const char* p_OID, const char* p_Value, 
                    ConfigOpType p_OpType)
{
   LogINGwTrace(false, 0, "IN configure");

   int result = 0;
   result = INGwIfrPrConfigMgr::getInstance().configure(p_OID, p_Value, p_OpType);

   //
   // Handling of the probing OIDs used for analysis
   //

   if(0 == strcmp(ingwDumpParam, p_OID))
   {
      std::string params = m_ParamRep.toLog();

      fstream file(p_Value, ios::out);

      if(true != file.is_open())
      {
         logger.logMsg(ERROR_FLAG, 0, "Error opening file [%s]",
                       p_Value);
         return 0;
      }

      file << params;

      file.flush();
      file.close();

      return 0;
   }
   else if(0 == strcmp(ingwDUMP, p_OID)) 
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Dumping call operation NOT supported NOW");
   }
   else if(0 == strcmp(ingwCLEAN, p_OID)) 
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Cleaning call operation NOT supported NOW");
   }
   else if(0 == strcmp(ingwSIP_STACK_DEBUG_LEVEL, p_OID)) 
   {
      if(RSIEmsTypes::ComponentState_Running == m_State) 
      {
         m_ParamRep.setMsgDebugLevel(atoi(p_Value));
      }
   }

   if(strcmp(ingwSTANDBY_NUM, p_OID) == 0)
   {
      m_ParamRep.setValue(p_OID, p_Value);
   }

   if(RSIEmsTypes::ComponentState_Running != m_State) 
   {
      m_ParamRep.setValue(p_OID, p_Value);
   }

   LogINGwTrace(false, 0, "OUT configure");
   return result;
}


//This Function is used to update the configuration parameters changed at EMS using Agent

void 
INGwIfrMgrManager::modifyCfgParam(const char* p_OID, const char* p_Value,
                          ConfigOpType p_OpType, long p_SubsystemId,
                          bool p_SyncFlag)
{
   logger.logINGwMsg(false, TRACE_FLAG, 0, "modifyCfgParam: ENTRY with subsystem" 
	 	               "id [%s], status [%s]", p_OID, p_Value);


	 if( (0 == strcmp(p_OID, ingwFLOATING_IP_ADDR) ) ){
		 logger.logINGwMsg(false, TRACE_FLAG, 0, "modifyCfgParam: Not modifying the param" 
				 "id [%s], status [%s]", p_OID, p_Value);
		 return;
	 }

   ImMediateTypes::NameValueType nvType;
   nvType.oid = CORBA::string_dup(p_OID);
   nvType.value <<= CORBA::string_dup(p_Value);

   BayAgentImpl &agent = (*BayAgentImpl::instance());
   ImMediateTypes::OperationType opType = ImMediateTypes::OperationType_Modify;

   switch(p_OpType) 
   {
      case INGwIfrUtlConfigurable::CONFIG_OP_TYPE_REMOVE :
      {
         opType = ImMediateTypes::OperationType_Delete;
      }
      break;

      case INGwIfrUtlConfigurable::CONFIG_OP_TYPE_REPLACE :
      {
         opType = ImMediateTypes::OperationType_Modify;
      }
      break;

      case INGwIfrUtlConfigurable::CONFIG_OP_TYPE_ADD :
      {
         opType = ImMediateTypes::OperationType_Add;
      }
      break;

      default :
      {
         break;
      }
   }

   bool status = false;

   try 
   {
      if(p_SubsystemId == INGwIfrPrParamRepository::getInstance().getSelfId()) 
      {
         status = agent.modifyCfgParam(opType, &nvType, p_SyncFlag);
      }
      else 
      {
         status = agent.modifyOtherSysCfgParam(p_SubsystemId, opType, &nvType);
      }
   }
   catch(...)
   {
   }

   if(false == status) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "modifyCfg: subsystem [%ld] Oid [%s] value [%s] failed.", 
                      p_SubsystemId, p_OID, p_Value);
   }

   logger.logINGwMsg(false, TRACE_FLAG, 0, "modifyCfgParam: EXIT");
}

// This function will be used by agent callback implementation
// to pass on the information sent/requested by EMS.

int 
INGwIfrMgrManager::startUp(void)
{
   LogINGwTrace(false, 0, "IN startUp");

   int result = 0;
   try 
   {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "ParamRep initialized.");

      m_FtMsnMgr = new INGwFtMsnMessenger(this);

      m_ParamRep.setMsgDebugLevel(m_ParamRep.getMsgDebugLevel());

      new RSI_NSP_CCM::INGwIfrUtlMachPing();

   }
   catch(...) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, -1, "Init error");
      return -1;
   }
   // may be we will never install INGw in HA mode
	 // so need to remove this piece of code
   std::string value;
/*
   m_ParamRep.getValue(ccmFT_HA_MODE, value);
   if (0 == strcasecmp(value.c_str(), "ha")) {
       gbCcmOnlyHa = true;
   }
   else if (0 == strcasecmp(value.c_str(), "ft")) {
       gbCcmOnlyHa = false;
   }
   else {
       logger.logINGwMsg(false, VERBOSE_FLAG, 0, "Value for parameter [%s]"
           " not avl or invalid - Using the default [%s]",
           ccmFT_HA_MODE, gbCcmOnlyHa == true ? "ha" : "ft");
   }
   logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Value of [%s] is [%s]",
       ccmFT_HA_MODE, gbCcmOnlyHa == true ? "ha" : "ft");
*/
   value = "";

   m_ParamRep.initialize(0, 0);

   try
   {
     //If peer id is configured, mode is 1 + 1 otherwise 1 + 0
     // Not supporting N + 1 mode
		 if(m_ParamRep.getPeerId() == 0)
		 {
			 m_ParamRep.setOperationMode(OnePlusZero);
       gbCcmOnlyHa = true;

       std::string selfIP = m_ParamRep.getSelfIPAddr();
       m_ParamRep.setValue(ingwFLOATING_IP_ADDR, selfIP);
		 }
		 else
		 {
			 m_ParamRep.setOperationMode(OnePlusOne);
       gbCcmOnlyHa = false;
		 }

   }
   catch(...)
   {
      logger.logMsg(ERROR_FLAG, 0, "Error identifying operation mode.");
      return -1;
   }

   {
		 INGwIfrUtlGuard(&m_PeerStateChangeMutex);
     m_ParamRep.setPeerStatus(0, 0,"INGwIfrMgrManager::startUp");
	 }

   bool bIsPrimary = false;
   long lPeerId = 0;

   try 
   {
      string selfMode = m_ParamRep.getValue(ingwIS_PRIMARY);
      bIsPrimary = static_cast<bool>(atoi(selfMode.c_str()));
      lPeerId = m_ParamRep.getPeerId();
   }
   catch(...) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, -1, "Configuration parameter [%s] not "
                                             "found", ingwIS_PRIMARY);
      result = -1;
   }

   //m_ParamRep.initialize(0, 0);

   //Get the mode of operation;

   m_ParamRep.setProcessingState(0);

   if(m_ParamRep.getOperationMode() == NPlusOne)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unsupported mode [N+1] of operation. Aborting INGW.");
      raise(9);
   }

   try 
   {
			//Will initialize Ip handler as secondry
			//and will change its mode after role negotiation is over
      m_IPAddrHndlr.initialize(false);
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "IPAddrHandler initialized.");

      // It has been added to support multi-lan configuration
      // of INGw Host machine. INGw host should be able to get connected with
      // reference ip and peer ip throgh signaling link.

      m_IPAddrHndlr.isNetworkAvailable();

      m_State = RSIEmsTypes::ComponentState_Loaded;
   }
   catch(...) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, -1, "Unable to init IP handler");
      result = -1;
   }


   //initialize the measurement framework
   MsrMgr *lpMsrMgr = MsrMgr::getInstance ();
   if (lpMsrMgr)
   {
     lpMsrMgr->initialize ();
     logger.logINGwMsg(false, TRACE_FLAG, 0,
       "Measurement Manager initialized");
   }
   else
   {
     logger.logINGwMsg(false, ERROR_FLAG, 0,
       "Measurement Manager ::FAILED INITIALIZATION");
   }

   //
   // NOW start all the providers 
   //

   logger.logMsg(TRACE_FLAG, 0, "Starting all the providers");
   ProviderMap::iterator it = m_ProviderMap.begin();
   for( ; it != m_ProviderMap.end(); it++) {
       INGwIwfBaseProvider * baseprovider = it->second;
       result = baseprovider->startUp();
       if(G_SUCCESS != result) {
           logger.logINGwMsg(false, ERROR_FLAG, -1, "Startup failed for provider type <%d>", it->first);
           break;
       }
			 else {
					 result = 0;
       }
   }

   // init Role Mgr
	 m_RoleManager = &INGwIfrMgrRoleMgr::getInstance();

   //start resource monitor
   m_ResourceMonitor.initialize();

   LogINGwTrace(false, 0, "OUT startUp");
   return result;
}

int 
INGwIfrMgrManager::changeState(int p_Value)
{
   ImMediateTypes::SubsystemStateTypeValue aValue = 
                              (ImMediateTypes::SubsystemStateTypeValue) p_Value;
   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "State change recd [%d]", aValue);

   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "1. m_state = [%d]", m_State);
   int result = 0;
   m_ParamRep.setMsgDebugLevel(m_ParamRep.getMsgDebugLevel());

   switch(aValue) 
   {
      
      case RSIEmsTypes::ComponentState_Running : 
      {
        logger.logINGwMsg(false, VERBOSE_FLAG, 0, "2. m_state = [%d]", m_State);
         if(m_State == RSIEmsTypes::ComponentState_Loaded) 
         {
            logger.logINGwMsg(false, VERBOSE_FLAG, 0, "3. m_state = [%d]", m_State);
            INGwIfrMgrThreadMgr::getInstance().startWorkerThread();
            
            //worker threads initialized properly now retrans cnt array can be
            //initialized
            INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
            lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::INIT_RETRANS_ARRAY;
            lWorkUnit->mpcCallId = NULL;
            lWorkUnit->mpWorkerClbk = INGwSpSipProvider::getInstance().getCallController();
            lWorkUnit->mpMsg = NULL;
            lWorkUnit->mpContextData = NULL;
            INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit);     
 
            result = resolveRole();
            if(0 == result)
						{
              logger.logMsg(TRACE_FLAG, 0, "Role Negotiation completed. ");
              //Call state change for providers
              logger.logMsg(TRACE_FLAG, 0, "Call state change for providers");
              ProviderMap::iterator it = m_ProviderMap.begin();
              for( ; it != m_ProviderMap.end(); it++) {
                  result = it->second->changeState(INGwIwfBaseProvider::PROVIDER_STATE_RUNNING);
                  if(result != G_SUCCESS) {
                      logger.logINGwMsg(false, ERROR_FLAG, -1, 
                                        "State Change failed for provider type <%d>", it->first);
                      break;
                  }
								  else{
										  result = 0;
                  }
              }
						}
						else
						{
              logger.logMsg(ERROR_FLAG, 0, "ERROR: Role Negotiation failed. ");
						}

            if(0 == result) 
            {
               m_State = static_cast<RSIEmsTypes::ComponentState>(aValue);
            }
            else 
            {
               result = -1;
            }
         }
         else 
         {
            logger.logINGwMsg(false, ERROR_FLAG, 0, "???? Not in loaded state");
            result = -1;
         }

         //
         // Get the port number for debug telnet listener
         //
         logger.logINGwMsg(false, VERBOSE_FLAG, 0, "4. Before m_ParamRep.getValue");
         std::string value;
         m_ParamRep.getValue(ingwDEBUG_PORT, value);
         int debugPort = atoi(value.c_str());
         logger.logINGwMsg(false, ALWAYS_FLAG, -1, "Value for [%s] is [%s]", 
             ingwDEBUG_PORT, value.c_str());

         m_TelnetIntf = new INGwIfrTlIfTelnetIntf();
         if(false == m_TelnetIntf->initialize(NULL, debugPort, true, 1)) {
            logger.logINGwMsg(false, WARNING_FLAG, -1, 
                "Unable to init telnet interface on port [%d]", debugPort);
        } 
        else {
           logger.logMsg(ALWAYS_FLAG, 0, "Telnet interface initialized on port [%d]",
               debugPort);
        }

        //configure the measurement Manager
        MsrMgr *lpMsrMgr = MsrMgr::getInstance ();
        std::string lstrXml;

        try
        {
          BayAgentImpl::instance()->getMeasurementMgrConfig (lstrXml);
        }
        catch (...)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "Configuration of MsrMgr FAILED :: Agent threw exception");
        }
        if (lstrXml.empty())
        {
          logger.logMsg (ERROR_FLAG, 0,
            "Configuration of MsrMgr FAILED :: Agent returned NULL XML");
        }
        else
        {
          logger.logINGwMsg (false, VERBOSE_FLAG, 0,
            "configureMsrMgr <%s>", lstrXml.c_str());

          if (!lpMsrMgr || lpMsrMgr->configureMsrMgr (lstrXml) != 0)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "Configuration of MsrMgr FAILED");
          }
        }
      }
      break;

      case RSIEmsTypes::ComponentState_Stopped : 
      {
         logger.logMsg(ALWAYS_FLAG, 0, " RSIEmsTypes::ComponentState_Stopped  [%d]",
                       aValue);
         m_IPAddrHndlr.shutdown();

         raise(9);
      }
      break;

      case RSIEmsTypes::ComponentState_SoftStop :
      {
         pthread_t thread;
         // TO DO
         //pthread_create(&thread, NULL, softShutdownThread, NULL);
      }
      break;

      default:
      {
        logger.logMsg(ALWAYS_FLAG, 0, "In default RSIEmsTypes::ComponentState_Stopped [%d]",
                      aValue); 
        
      }
      break;
   }

   return result;
}

void 
INGwIfrMgrManager::performance(void* p_PerfData)
{
   EmsOidValMap *perfData = (EmsOidValMap *)p_PerfData;
   MsrMgr::getInstance()->getEmsParams(*perfData);

   logger.logINGwMsg(false, TRACE_FLAG, 0, "Got [%d] parameters from MsrMgr", 
                   perfData->size());
   return;
}

int 
INGwIfrMgrManager::oidChanged(const char* p_OID, const char* p_Value, 
                     ConfigOpType p_OpType, long p_SubsystemId)
{
   LogINGwTrace(false, 0, "IN oidChanged");

   int retval = 0;

   if(0 == strcmp(cmSUBSYS_STATE, p_OID))
   {
      RSIEmsTypes::ComponentState state = 
              static_cast<RSIEmsTypes::ComponentState>(strtol(p_Value, 0, 10));

      if(p_SubsystemId == m_ParamRep.getPeerId())
      {
         logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                         "Received oidChanged() for peer INGW [%d] state [%d]", 
                         p_SubsystemId, state);

         if(state == RSIEmsTypes::ComponentState_Running)
         {
            m_FtMsnMgr->addINGW(m_ParamRep.getPeerId());

            if(m_ParamRep.getPeerStatus() != 1)
            {
              {
		            INGwIfrUtlGuard(&m_PeerStateChangeMutex);
                m_ParamRep.setPeerStatus(1, time(NULL),"INGwIfrMgrManager::oidChanged");
	            }
            }
         }
      }
      else
      {
          logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                          "Received oidChanged() subsystem Id [%d] state [%d] "
                          "Ignoring this change", p_SubsystemId, state);
      }
   }
   else 
   {
      retval = INGwIfrPrConfigMgr::getInstance().oidChanged(p_OID, p_Value, p_OpType,
                                                     p_SubsystemId);
   }

   LogINGwTrace(false, 0, "OUT oidChanged");
   return retval;
}


// To handle the softshutdown request
void 
INGwIfrMgrManager::softShutdown()
{
}

std::string 
INGwIfrMgrManager::toLog(void) const
{
   return std::string("");
}

void 
INGwIfrMgrManager::getStatistics(std::ostrstream& p_Output, int p_TabCount,
                         bool p_DetailFlag)
{
   char tabs[20];

   for(int idx = 0; idx < p_TabCount; idx++)
   {
      tabs[idx] = '\t';
   }
   tabs[p_TabCount] = '\0';

   p_Output << tabs << "INGW\n";

   ProviderMap::iterator it = m_ProviderMap.begin();
   for( ; it != m_ProviderMap.end(); it++) {
       INGwIwfBaseProvider * baseprovider = it->second;
       baseprovider->getStatistics(p_Output, p_TabCount + 1);
   }

   p_Output << tabs << "-INGW\n";
}

//Impementation for interfaces of INGwIfrMgrFtIface
      
void 
INGwIfrMgrManager::recvMsgFromPeerINGw(char* p_MsgBuffer, int p_Len, 
																			 int p_Version )
{
   LogINGwTrace(false, 0, "IN recvMsgFromPeerINGw()");
   char lpcTime[64];
	 short msgType = 0;
	 memcpy(&msgType, p_MsgBuffer, sizeof(short));
	 msgType = ntohs(msgType);
	 INGwFtPktMsg *msg = NULL;

   Logger::LogLevel lglvl = Logger::TRACE;
   if (msgType < 200) {
     lglvl = Logger::ALWAYS;
   }
	 logger.logMsg(lglvl, __LINE__, 0, "recvMsgFromPeerINGw():MsgType[%d] Len[%d]",
	 							int(msgType), p_Len);

	 switch(msgType)
	 {
		  // Make a work unit and enqueue it.
		  // Handler will handle the request.
		  // Handler is responsible for cleaning up INGwFtPktMsg
		  // msgBuffer should be deleted here if buffer is not part of INGwFtPktMsg
      case MSG_CREATE_TCAP_SESSION:
      {
        msg = new INGwFtPktCreateTcapSession();
        if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
        {
          	delete []p_MsgBuffer;
						delete msg;
						logger.logMsg(ERROR_FLAG, 0,
									"+FT+ Depacketize failed. Msg Type  [%d]", int(msgType));
						return;
        }
        else{
						int lCurVal = 0;
						INGwIfrSmStatMgr::instance().increment
							(miStatParamId_CreateTcapSession, lCurVal, 1);

						INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
						lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::MSG_NEW_TCAP_SESSION;
						lWorkUnit->mpcCallId = NULL;
						lWorkUnit->mpWorkerClbk = 
										INGwTcapProvider::getInstance().getTcapFtHandler();
						lWorkUnit->mpMsg = static_cast<void *>(msg);
						lWorkUnit->mpContextData = NULL;

						logPeerMsg(msg);
						INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit);
						logger.logMsg(TRACE_FLAG, 0,
            "+FT+Depacketize INGwFtPktCreateTcapSession Success. MsgType [%d]",
						 int(msgType));
        }
				delete []p_MsgBuffer;
      }
      break;

      case MSG_TCAP_CALL_SEQ_ACK:
      {
        msg = new INGwFtPktTcapCallSeqAck();
			  if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
        {
          
					delete []p_MsgBuffer;
					delete msg;
					logger.logMsg(ERROR_FLAG, 0,
												"Depacketize failed. Msg Type  [%d]", int(msgType));

					return;
        }
        else
        {
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment(miStatParamId_TcapCallSeqAck, 
																									lCurVal, 1);

				  INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
				  lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::MSG_TCAP_CLEAN_MSG_DATA;
				  lWorkUnit->mpcCallId = NULL;
				  lWorkUnit->mpWorkerClbk = 
                           INGwTcapProvider::getInstance().getTcapFtHandler();
				  lWorkUnit->mpMsg = static_cast<void *>(msg);
				  lWorkUnit->mpContextData = NULL;

					logPeerMsg(msg);
				  INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit);
				  logger.logMsg(TRACE_FLAG, 0,
          "+FT+Depacketize decode INGwFtPktTcapCallSeqAck Success."
          " Msg Type  [%d]", int(msgType));
        }
				delete []p_MsgBuffer;
      }
      break;

      case MSG_UPDATE_TCAP_SESSION:
      {
        msg = new INGwFtPktUpdateTcapSession();
			  if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
        {
          
					delete []p_MsgBuffer;
					delete msg;
					logger.logMsg(ERROR_FLAG, 0,
												"Depacketize failed. Msg Type  [%d]", int(msgType));

					return;
        }
        else
        {
						int lCurVal = 0;
						INGwIfrSmStatMgr::instance().increment
										(miStatParamId_UpdateTcapSession, lCurVal, 1);

				  INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
				  lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::MSG_MODIFY_TCAP_SESSION;
				  lWorkUnit->mpcCallId = NULL;
				  lWorkUnit->mpWorkerClbk = 
                           INGwTcapProvider::getInstance().getTcapFtHandler();
				  lWorkUnit->mpMsg = static_cast<void *>(msg);
				  lWorkUnit->mpContextData = NULL;
					logPeerMsg(msg);

				  INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit);
				  logger.logMsg(TRACE_FLAG, 0,
          "+FT+Depacketize decode INGwFtPktUpdateTcapSession Success."
          " Msg Type  [%d]", int(msgType));
        }
				delete []p_MsgBuffer;

      }

      break;

      case MSG_CLOSE_TCAP_SESSION:
      {
        msg = new INGwFtPktTermTcapSession();
			  if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
        {
          
					delete []p_MsgBuffer;
					delete msg;
					logger.logMsg(ERROR_FLAG, 0,
												"Depacketize failed. Msg Type  [%d]", int(msgType));

					return;
        }
        else
        {
						int lCurVal = 0;
						INGwIfrSmStatMgr::instance().increment
								(miStatParamId_CloseTcapSession, lCurVal, 1);

				  INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
				  lWorkUnit->meWorkType =INGwIfrMgrWorkUnit::MSG_TERMINATE_TCAP_SESSION;
				  lWorkUnit->mpcCallId = NULL;
				  lWorkUnit->mpWorkerClbk = 
                           INGwTcapProvider::getInstance().getTcapFtHandler();
				  lWorkUnit->mpMsg = static_cast<void *>(msg);
				  lWorkUnit->mpContextData = NULL;
					logPeerMsg(msg);

				  INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit);
				  logger.logMsg(TRACE_FLAG, 0,
          "+FT+Depacketize decode INGwFtPktTermTcapSession Success."
          " Msg Type  [%d]", int(msgType));
        }
				delete []p_MsgBuffer;
      }
      break;

			case MSG_FT_ROLE_NEGOTIATION:
			{
				 msg = new INGwFtPktRoleMsg();
				 if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
				 {
						delete []p_MsgBuffer;
						delete msg;
						logger.logMsg(ERROR_FLAG, 0,
													"Depacketize failed. Msg Type  [%d]", int(msgType));

						return;
				 }
				 else
				 {
						std::string lDummyStr = "DUMMY_CALL_ID_TIMER";
						char* l_callId = new char[lDummyStr.length() + 1];
						strcpy(l_callId,  lDummyStr.c_str());
						INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
						lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::PEER_MSG_FT_ROLE_PKT;
						lWorkUnit->mpcCallId = l_callId;
						lWorkUnit->mpWorkerClbk = &INGwIfrMgrManager::getInstance();
						lWorkUnit->getHash();
						lWorkUnit->mpMsg = static_cast<void *>(msg);
						lWorkUnit->mpContextData = NULL;
						logPeerMsg(msg);

						INGwIfrMgrThreadMgr::getInstance().postMsg(lWorkUnit);
				    logger.logMsg(ALWAYS_FLAG, 0, "Enqueuing PEER_MSG_FT_ROLE_PKT");
				 }// end of else of depacketize
				 delete []p_MsgBuffer;
			}// end of case MSG_FT_ROLE_NEGOTIATION
			break;

			case MSG_CALL_BACKUP:
			{
				 msg = new INGwFtPktCallDataMsg();
				 if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
				 {
						delete []p_MsgBuffer;
						delete msg;
						logger.logMsg(ERROR_FLAG, 0,
													"Depacketize failed. Msg Type  [%d]", int(msgType));

						return;
				 }
				 else
				 {
						int lCurVal = 0;
						INGwIfrSmStatMgr::instance().increment(miStatParamId_CallBkup, 
																									lCurVal, 1);

						std::string lCallIdStr = ((INGwFtPktCallDataMsg *)msg)->getCallId();
						char* l_callId = new char[lCallIdStr.length() + 1];
						strcpy(l_callId,  lCallIdStr.c_str());
						INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
						lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::PEER_INGW_CALL_MSG;
						lWorkUnit->mpcCallId = l_callId;
						lWorkUnit->mpWorkerClbk = INGwSpSipProvider::getInstance().getCallController();
						lWorkUnit->getHash();
						lWorkUnit->mpMsg = static_cast<void *>(msg);
						lWorkUnit->mpContextData = NULL;
						logPeerMsg(msg);

						INGwIfrMgrThreadMgr::getInstance().postMsg(lWorkUnit);
						//Not deleting replication Msg Buffer here as it is part of 
						//FT packet

				 }// end of else of depacketize
			} // end of case MSG_CALL_BACKUP
			break;

			case MSG_FT_DELETE_CALL:
			{
				 msg = new INGwFtPktDeleteCallMsg();
				 if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
				 {
						delete []p_MsgBuffer;
						delete msg;
						logger.logMsg(ERROR_FLAG, 0,
													"Depacketize failed. Msg Type  [%d]", int(msgType));

						return;
				 }
				 else
				 {
						int lCurVal = 0;
						INGwIfrSmStatMgr::instance().increment(miStatParamId_FtDelCall, 
																									lCurVal, 1);

						std::string lCallIdStr = ((INGwFtPktCallDataMsg *)msg)->getCallId();
						char* l_callId = new char[lCallIdStr.length() + 1];
						strcpy(l_callId,  lCallIdStr.c_str());
						INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
						lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::PEER_INGW_CALL_MSG;
						lWorkUnit->mpcCallId = l_callId;
						lWorkUnit->mpWorkerClbk = INGwSpSipProvider::getInstance().getCallController();
						lWorkUnit->getHash();
						lWorkUnit->mpMsg = static_cast<void *>(msg);
						lWorkUnit->mpContextData = NULL;
						logPeerMsg(msg);

						INGwIfrMgrThreadMgr::getInstance().postMsg(lWorkUnit);

				 }// end of else of depacketize

				 delete []p_MsgBuffer;
			} // end of case MSG_FT_DELETE_CALL
			break;

			case MSG_LOAD_DIST_MSG:
			{
				 msg = new INGwFtPktLoadDistMsg();
				 if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
				 {
						delete []p_MsgBuffer;
						delete msg;
						logger.logMsg(ERROR_FLAG, 0,
													"Depacketize failed. Msg Type  [%d]", int(msgType));

						return;
				 }
				 else
				 {
						int lCurVal = 0;
						INGwIfrSmStatMgr::instance().increment(miStatParamId_LoadDistMsg, 
																									lCurVal, 1);

						INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
						lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::LOAD_DIST_MSG;
						lWorkUnit->mpcCallId = NULL;
						lWorkUnit->mpWorkerClbk = 
							INGwTcapProvider::getInstance().getTcapFtHandler();
						lWorkUnit->mpMsg = static_cast<void *>(msg);
						lWorkUnit->mpContextData = NULL;
						logPeerMsg(msg);

						INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit);

				 }// end of else of depacketize

				 delete []p_MsgBuffer;
			}
			break;

			case MSG_HANDLE_SAS_HB_FAILURE:
			{
				 msg = new INGwFtPktSasHBFailure();
				 if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
				 {
						delete []p_MsgBuffer;
						delete msg;
						logger.logMsg(ERROR_FLAG, 0,
													"Depacketize failed. Msg Type  [%d]", int(msgType));

						return;
				 }
				 else
				 {
						int lCurVal = 0;
						INGwIfrSmStatMgr::instance().increment(miStatParamId_SasHbFailure, 
																									lCurVal, 1);

						INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
						lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::SAS_HB_FAILURE;
						lWorkUnit->mpcCallId = NULL;
						lWorkUnit->mpWorkerClbk = 
										INGwTcapProvider::getInstance().getTcapFtHandler();
						lWorkUnit->mpMsg = static_cast<void *>(msg);
						lWorkUnit->mpContextData = NULL;
						logPeerMsg(msg);

						INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit);

				 }// end of else of depacketize

				 delete []p_MsgBuffer;
			}
      break;

			case MSG_HANDLE_SYNCH_UP:
			{
				 msg = new INGwFtPktSynchUpMsg();
				 if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
				 {
						delete []p_MsgBuffer;
						delete msg;
						logger.logMsg(ERROR_FLAG, 0,
													"Depacketize failed. Msg Type  [%d]", int(msgType));

						return;
				 }
				 else
				 {
						int lCurVal = 0;
            //Yogesh
            //introduce a counter over here to maintain failure count
						//INGwIfrSmStatMgr::instance().
            //increment(miStatParamId_SasHbFailure, 
						//lCurVal, 1);

						INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
						lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::MSG_FT_SYNCHUP;
						lWorkUnit->mpcCallId = NULL;
						lWorkUnit->mpWorkerClbk = 
										INGwTcapProvider::getInstance().getTcapFtHandler();
						lWorkUnit->mpMsg = static_cast<void *>(msg);
						lWorkUnit->mpContextData = NULL;
						logPeerMsg(msg);

						INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit);

				 }// end of else of depacketize

				 delete []p_MsgBuffer;
			}
      break;

			case MSG_TCAP_DLG_INFO:
			{
				 msg = new INGwFtPktTcapMsg();
				 if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
				 {
						delete []p_MsgBuffer;
						delete msg;
						logger.logMsg(ERROR_FLAG, 0,
													"Depacketize failed. Msg Type  [%d]", int(msgType));

						return;
				 }
				 else
				 {
						int lCurVal = 0;
						INGwIfrSmStatMgr::instance().increment(miStatParamId_TcapDlgInfo, 
																									lCurVal, 1);

						INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
						lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::MSG_TCAP_MSG_INFO;
						lWorkUnit->mpcCallId = NULL;
						lWorkUnit->mpWorkerClbk = 
											INGwTcapProvider::getInstance().getTcapFtHandler();
						lWorkUnit->mpMsg = static_cast<void *>(msg);
						lWorkUnit->mpContextData = NULL;
						logPeerMsg(msg);

						INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit);
				 }// end of else of depacketize

				delete []p_MsgBuffer;
			}
			break;

			case MSG_STACK_CONFIG:
			{
         logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                "Received BP_AIN_SM_STACK_CONFIG_START");

				 msg = new INGwFtPktStackConfig();
				 if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
				 {
						delete []p_MsgBuffer;
						delete msg;
						logger.logMsg(ERROR_FLAG, 0,
													"Depacketize failed. Msg Type  [%d]", int(msgType));

						return;
				 }
				 else
				 {
						int lCurVal = 0;
						INGwIfrSmStatMgr::instance().increment(miStatParamId_StackConfig, 
																									lCurVal, 1);

    				INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
    				lqueueMsg->req = NULL;
            lqueueMsg->from = 3;
						if(((INGwFtPktStackConfig*)msg)->getStackConfigType() == 1) {
    					lqueueMsg->src = BP_AIN_SM_STACK_CONFIG_START;

              memset(lpcTime, 0, sizeof(lpcTime));
              lpcTime[0] = '1';
              g_getCurrentTime(lpcTime);
              printf("[+INC+] recvMsgFromPeerINGw(): %s "
                     "Received BP_AIN_SM_STACK_CONFIG_START, Enqueuing it\n",
                     lpcTime); fflush(stdout);

              logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                     "Received BP_AIN_SM_STACK_CONFIG_START. Enqueuing it");
  
						}
						else {
    					lqueueMsg->src = BP_AIN_SM_STACK_CONFIG_END;

              memset(lpcTime, 0, sizeof(lpcTime));
              lpcTime[0] = '1';
              g_getCurrentTime(lpcTime);
              printf("[+INC+] recvMsgFromPeerINGw(): %s "
                     "Received BP_AIN_SM_STACK_CONFIG_END. Enqueuing it\n",
                     lpcTime); fflush(stdout);

              logger.logINGwMsg(false,ALWAYS_FLAG,0, "recvMsgFromPeerINGw(): "
                     "Received BP_AIN_SM_STACK_CONFIG_END, Enqueuing it");
						}

						logPeerMsg(msg);
    				INGwTcapProvider::getInstance().getSmWrapperPtr()->
																		postMsg(lqueueMsg,true);
				 }// end of else of depacketize

				delete []p_MsgBuffer;
				delete msg;
				msg=NULL;

			} // end of case MSG_STACK_CONFIG
			break;

		  case MSG_ADD_LINK:
      case MSG_DEL_LINK:
      case MSG_ADD_LINKSET:
      case MSG_DEL_LINKSET:
      case MSG_ADD_NW:
      case MSG_DEL_NW:
      case MSG_ADD_ROUTE:
      case MSG_DEL_ROUTE:
      case MSG_ADD_LOCAL_SSN:
      case MSG_DEL_LOCAL_SSN:
      case MSG_ADD_USER_PART:
      case MSG_DEL_USER_PART:
      case MSG_M3UA_ASSOC_DOWN:
      case MSG_M3UA_ASSOC_UP:
      case MSG_ADD_PS:
      case MSG_DEL_PS:
      case MSG_ADD_ENDPOINT:
      case MSG_DEL_ENDPOINT:
      case MSG_ADD_PSP:
      case MSG_DEL_PSP:
			case MSG_ADD_RULE:
			case MSG_DEL_RULE:
			case MSG_ADD_ADDRMAP:
			case MSG_DEL_ADDRMAP:
			case MSG_MOD_LINK:
			case MSG_MOD_LINKSET:
			case MSG_MOD_PS:
			case MSG_ASP_ACTV:
			case MSG_ASP_INACTV:
			case MSG_CONFIG_STATUS:
			{
				if(msgType == MSG_ADD_LINK)
				{
					msg = new INGwFtPktAddLink();
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																	(miStatParamId_AddLink, lCurVal, 1);
				}
				else if(msgType == MSG_DEL_LINK)
				{
					msg = new INGwFtPktDelLink();
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																	(miStatParamId_DelLink, lCurVal, 1);
				}
				else if(msgType == MSG_ADD_LINKSET)
				{
					msg = new INGwFtPktAddLinkset();
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																	(miStatParamId_AddLinkset, lCurVal, 1);
				}
				else if(msgType == MSG_DEL_LINKSET)
				{
					msg = new INGwFtPktDelLinkset();
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																		(miStatParamId_DelLinkset, lCurVal, 1);
				}
				else if(msgType == MSG_ADD_NW)
				{
					msg = new INGwFtPktAddNw();
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																		(miStatParamId_AddNw, lCurVal, 1);
				}
				else if(msgType == MSG_DEL_NW)
				{
					msg = new INGwFtPktDelNw();
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																		(miStatParamId_DelNw, lCurVal, 1);
				}
				else if(msgType == MSG_ADD_ROUTE)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																	(miStatParamId_AddRoute, lCurVal, 1);
					msg = new INGwFtPktAddRoute();
				}
				else if(msgType == MSG_DEL_ROUTE)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																		(miStatParamId_DelRoute, lCurVal, 1);
					msg = new INGwFtPktDelRoute();
				}
				else if(msgType == MSG_ADD_LOCAL_SSN)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																			(miStatParamId_AddSsn, lCurVal, 1);
					msg = new INGwFtPktAddSsn();
				}
				else if(msgType == MSG_DEL_LOCAL_SSN)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																			(miStatParamId_DelSsn, lCurVal, 1);
					msg = new INGwFtPktDelSsn();
				}
				else if(msgType == MSG_ADD_USER_PART)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																		(miStatParamId_AddUsrPart, lCurVal, 1);
					msg = new INGwFtPktAddUserPart();
				}
				else if(msgType == MSG_DEL_USER_PART)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																			(miStatParamId_DelUsrPart, lCurVal, 1);
			  	msg = new INGwFtPktDelUserPart();
				}
				else if(msgType == MSG_M3UA_ASSOC_UP)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																		(miStatParamId_AssocUp, lCurVal, 1);
					msg = new INGwFtPktM3uaAssocUp();
				}
				else if(msgType == MSG_M3UA_ASSOC_DOWN)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																	(miStatParamId_AssocDown, lCurVal, 1);
					msg = new INGwFtPktM3uaAssocDown();
				}
				else if(msgType == MSG_ADD_PS)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																		(miStatParamId_AddPs, lCurVal, 1);
					msg = new INGwFtPktAddPs();
				}
				else if(msgType == MSG_DEL_PS)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																		(miStatParamId_DelPs, lCurVal, 1);
					msg = new INGwFtPktDelPs();
				}
				else if(msgType == MSG_ADD_ENDPOINT)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																			(miStatParamId_AddEp, lCurVal, 1);
					msg = new INGwFtPktAddEp();
				}
				else if(msgType == MSG_DEL_ENDPOINT)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																			(miStatParamId_DelEp, lCurVal, 1);
					msg = new INGwFtPktDelEp();
				}
				else if(msgType == MSG_ADD_PSP)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																		(miStatParamId_AddPsp, lCurVal, 1);
					msg = new INGwFtPktAddPsp();
				}
				else if(msgType == MSG_DEL_PSP)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																	(miStatParamId_DelPsp, lCurVal, 1);
					msg = new INGwFtPktDelPsp();
				}
				else if(msgType == MSG_ADD_ADDRMAP)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																	(miStatParamId_AddGtAddrMap, lCurVal, 1);
					msg = new INGwFtPktAddGtAddrMap();
				}
				else if(msgType == MSG_DEL_ADDRMAP)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																	(miStatParamId_DelGtAddrMap, lCurVal, 1);
					msg = new INGwFtPktDelGtAddrMap();
				}
				else if(msgType == MSG_ADD_RULE)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																		(miStatParamId_AddRule, lCurVal, 1);
					msg = new INGwFtPktAddGtRule();
				}
				else if(msgType == MSG_DEL_RULE)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																			(miStatParamId_DelRule, lCurVal, 1);
					msg = new INGwFtPktDelGtRule();
				}
				else if(msgType == MSG_MOD_LINK)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																			(miStatParamId_ModLink, lCurVal, 1);
					msg = new INGwFtPktModLink();
				}
				else if(msgType == MSG_MOD_LINKSET)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																			(miStatParamId_ModLinkset, lCurVal, 1);
					msg = new INGwFtPktModLinkset();
				}
				else if(msgType == MSG_MOD_PS)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																			(miStatParamId_ModPs, lCurVal, 1);
					msg = new INGwFtPktModPs();
				}
        else if(msgType == MSG_ASP_ACTV)
				{
					int lCurVal = 0;
					//INGwIfrSmStatMgr::instance().increment
				  //													(miStatParamId_ModPs, lCurVal, 1);
					msg = new INGwFtPktAspActive();
				}
        else if(msgType == MSG_ASP_INACTV)
				{
					int lCurVal = 0;
					//INGwIfrSmStatMgr::instance().increment
				  //													(miStatParamId_ModPs, lCurVal, 1);
					msg = new INGwFtPktAspInActive();
				}

				else if(msgType == MSG_CONFIG_STATUS)
				{
					int lCurVal = 0;
					INGwIfrSmStatMgr::instance().increment
																		(miStatParamId_CofigStatus, lCurVal, 1);
					msg = new INGwFtPktStkConfigStatus();
				}

				 if(msg->depacketize(p_MsgBuffer, p_Len, p_Version) == 0)
				 {
						delete []p_MsgBuffer;
						delete msg;
						logger.logMsg(ERROR_FLAG, 0,
													"Depacketize failed. Msg Type  [%d]", int(msgType));

						return;
				 }
				 else
				 {
    				INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
    				lqueueMsg->src = BP_AIN_SM_SRC_PEER_INC;
						lqueueMsg->msg = msg;

						logPeerMsg(msg);
    				INGwTcapProvider::getInstance().getSmWrapperPtr()->
																		postMsg(lqueueMsg,true);
				 }// end of else of depacketize

				delete []p_MsgBuffer;
			} // end of case MSG_STACK_CONFIG
			break;
			default :
			{
				 delete []p_MsgBuffer;
				 delete msg;
				 logger.logMsg(ERROR_FLAG, 0,
											 "Unexpected msg type [%d] recvd from peer",
											 int(msgType));
         return;
			}
	 }// end of switch msgType

   LogINGwTrace(false, 0, "OUT recvMsgFromPeerINGw()");
}


short 
INGwIfrMgrManager::handleINGwFailure(int p_PeerINGwId)
{
   LogINGwTrace(false, 0, "IN handleINGwFailure");
   
   isReplayInProgress = true; 
   if(p_PeerINGwId != m_ParamRep.getPeerId())
   {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                       "Received failure of ingw  subsystem  [%d]  "
                       "Ignoring this change as it is not our peer ", p_PeerINGwId);

      return 0;
   }

   logger.logINGwMsg(false, ALWAYS_FLAG, 0, "handleINGwFailure: " 
                     "Received failure of peer ingw  subsystem  [%d]  ",
                      p_PeerINGwId);

   // Check if it is in the netwrok or isolated

   logger.logINGwMsg(false, TRACE_FLAG, 0, "handleINGwFailure: " 
                     "Checking for self isolation condition ");
   m_IPAddrHndlr.isNetworkAvailable();

   {
		 INGwIfrUtlGuard(&m_PeerStateChangeMutex);
		 m_ParamRep.setPeerStatus(0, 0,"handleINGwFailure");
	 }


   // notify the role manager to take control
   logger.logINGwMsg(false, TRACE_FLAG, 0, "handleINGwFailure: " 
                     "Self is in netwrok, peer is down, Notifying role mgr.");
   m_RoleManager->peerDown();

   //remove Peer connecton 
   logger.logINGwMsg(false, TRACE_FLAG, 0, "handleINGwFailure: " 
                     "Removing peer communication from its messenger list.");

   m_FtMsnMgr->removeINGW(p_PeerINGwId);

	 // Notify Wrapper regarding peer up
   
	 INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
   lqueueMsg->req = NULL;
   lqueueMsg->src = BP_AIN_SM_PEER_DOWN;
   logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleINGwFailure:"
                     "posting BP_AIN_SM_PEER_DOWN in wrapper");
   INGwTcapProvider::getInstance().getSmWrapperPtr()->postMsg(lqueueMsg,true);

   // post message to INGwTcapFtHandler for inititiation of takeover
   // actual place
   logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleINGwFailure:"
                     "after posting BP_AIN_SM_PEER_DOWN");
   if (!mDisableMsgFtFlag && m_RoleManager->getSbyToActive())
   {
     logger.logINGwMsg(false,ALWAYS_FLAG,0,
          "handleINGwFailure posting MSG_PEER_FAILURE");

     INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
     lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::MSG_PEER_FAILURE;
     lWorkUnit->mpcCallId = NULL;
     lWorkUnit->mpWorkerClbk = INGwTcapProvider::getInstance().getTcapFtHandler();
     lWorkUnit->mpMsg = NULL; 
     lWorkUnit->mpContextData = NULL;
     INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit);
   }
   else{
     logger.logINGwMsg(false,ALWAYS_FLAG,0,
          "handleINGwFailure NOT posting MSG_PEER_FAILURE");
   }

   LogINGwTrace(false, 0, "OUT handleINGwFailure");
   return 0;
}


int 
INGwIfrMgrManager::getMsgDebugLevel()
{
   return 0;
}

void 
INGwIfrMgrManager::bogusFault(unsigned int p_SubsysID)
{
   LogINGwTrace(false, 0, "IN INGwIfrMgrManager::bogusFault");

   logger.logMsg(ALWAYS_FLAG, 0, "Bogus fault of [%d]", p_SubsysID);

   if(p_SubsysID == m_ParamRep.getPeerId())
   {
      //Bogus Fault with the peerINGw.

      BayAgentImpl& agent = (*BayAgentImpl::instance());

      RSIEmsTypes::ConfigurationDetail_var ourRoleChange = 
                          agent.getConfigParam(m_ParamRep.getSelfId(), "1.12.3");

      RSIEmsTypes::ConfigurationDetail_var peerRoleChange = 
                                       agent.getConfigParam(p_SubsysID, "1.12.3");

      struct tm inTime, inTime1;
      inTime.tm_isdst = 0; 
      inTime1.tm_isdst = 0; 

      strptime(ourRoleChange->paramValue.in(), "%c %Z", &inTime);
      strptime(peerRoleChange->paramValue.in(), "%c %Z", &inTime1);

      int ourRoleChangeVal = mktime(&inTime);
      int peerRoleChangeVal = mktime(&inTime1);

      if(ourRoleChangeVal > peerRoleChangeVal)
      {
         // We were initially secondary and became primary by detecting bogus 
         // fault with the primary. So let the take over proceed and we will 
         // ask the other INGW to go down.

         logger.logMsg(ALWAYS_FLAG, 0, "Asking othere INGw to go down "
                                       "as we became primary by detecting Bogus fault.");
         m_FtMsnMgr->sendAction(p_SubsysID, 51);
      }
      else
      {
         // We were initially primary and because of the bogus fault, We closed 
         // the secondary INGw channel, this will cause the secondary to became 
         // primary. So we will go down.

         m_IPAddrHndlr.shutdown();

         logger.logMsg(ALWAYS_FLAG, 0, "Going down deliberatly on "
                                       "Bogus fault.");
         raise(9);
         sleep(1);
      }
   }
   LogINGwTrace(false, 0, "OUT INGwIfrMgrManager::bogusFault");
}


void 
INGwIfrMgrManager::doAction(unsigned int p_SubsysID, unsigned int p_ActionID)
{
   LogINGwTrace(false, 0, "IN INGwIfrMgrManager::doAction");
   //PR - 50798
   logger.logMsg(ALWAYS_FLAG, 0, "Action request [%d] from [%d]", 
                 p_ActionID, p_SubsysID);

   if(p_ActionID == 51)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Quitting on [%d] request.", p_SubsysID);
      // The delay has been added so that the alarm
      // reaches the EMS before INGw exits

      m_IPAddrHndlr.shutdown();
      sleep(1);
      raise(9);
      sleep(1);
   }
   LogINGwTrace(false, 0, "OUT INGwIfrMgrManager::doAction");
}


void 
INGwIfrMgrManager::ingwConnected(int p_IngwId)
{
   LogINGwTrace(false, 0, "IN INGwIfrMgrManager::ingwConnected");

   if(p_IngwId != m_ParamRep.getPeerId())
   {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                       "Received connection of ingw  subsystem  [%d]  "
                       "Ignoring this change as it is not our peer ", p_IngwId);

      return;
   }

   logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                     "INGwIfrMgrManager::ingwConnected: " 
                     "Received msn connection of peer ingw  subsystem  [%d]  ",
                      p_IngwId);

   if(m_ParamRep.getPeerStatus() != 1)
   {
     {
		   INGwIfrUtlGuard(&m_PeerStateChangeMutex);
		   m_ParamRep.setPeerStatus(1, time(NULL),"****ingwConnected****");
	   }
   }

   // notify the role manager to take control

   logger.logINGwMsg(false, TRACE_FLAG, 0, 
                     "INGwIfrMgrManager::ingwConnected: " 
                     "Peer is communication is connected, Notifying role mgr.");

   m_RoleManager->peerUp();

	 // Notify Wrapper regarding peer up
	 INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
   lqueueMsg->req = NULL;
   lqueueMsg->src = BP_AIN_SM_PEER_UP;
   INGwTcapProvider::getInstance().getSmWrapperPtr()->postMsg(lqueueMsg,true);


   //notify INGwTcapFtHandler
   if(true == isRoleResolutionDone()) {
     logger.logINGwMsg(false,ALWAYS_FLAG,0,"ingwConnected MSG_PEER_UP");
     INGwIfrMgrWorkUnit* lWorkUnit = new INGwIfrMgrWorkUnit;
     lWorkUnit->meWorkType = INGwIfrMgrWorkUnit::MSG_PEER_UP;
     lWorkUnit->mpcCallId = NULL;
     lWorkUnit->mpWorkerClbk = INGwTcapProvider::getInstance().
                                                 getTcapFtHandler();
     lWorkUnit->mpMsg = NULL;
     lWorkUnit->mpContextData = NULL;
     INGwIfrMgrThreadMgr::getInstance().postMsgForHK(lWorkUnit);
   }
   else {
     logger.logINGwMsg(false,ALWAYS_FLAG,0,"ingwConnected not posting "
     "MSG_PEER_UP role resolution not done");
   }

   LogINGwTrace(false, 0, "OUT INGwIfrMgrManager::ingwConnected");
}

INGwIfrMgrManager::INGwIfrMgrManager():
    m_ParamRep(INGwIfrPrParamRepository::getInstance()),
    m_FtMsnMgr(NULL),
    m_State(RSIEmsTypes::ComponentState_Stopped),
    m_StartAsPrimary(false),
		m_RoleManager(NULL),
    mIsRoleResDone(false)
{
   LogINGwTrace(false, 0, "IN INGwIfrMgrManager()");
   setMsgFtFlag();
   pthread_mutex_init(&m_PeerStateChangeMutex, NULL);
   LogINGwTrace(false, 0, "OUT INGwIfrMgrManager()");

}

int 
INGwIfrMgrManager::getState(long p_SubSysId)
{
   RSIEmsTypes::ComponentState retval = RSIEmsTypes::ComponentState_Unknown;

   try
   {
      retval = BayAgentImpl::instance()->getState(p_SubSysId);
   }
   catch(...)
   {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_CORBA_OPER,
                      "Exception in getState() for subsystem Id %d",
                      p_SubSysId);
   }

   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "subsystem [%d] state [%d]",
                   p_SubSysId, retval);
   return (int)retval;

}

void 
INGwIfrMgrManager::sendMsgToINGW(INGwFtPktMsg *p_Msg)
{
   LogINGwTrace(false, 0, "IN INGwIfrMgrManager::sendMsgToINGW()");

   if(INGwIfrPrParamRepository::getInstance().getPeerStatus())
   {
      m_FtMsnMgr->sendMsgToINGW(p_Msg);
   }
   else
   {
      LogINGwTrace(false, 0, "Not sending Msg to Peer as peer state is down");
   }

   LogINGwTrace(false, 0, "OUT INGwIfrMgrManager::sendMsgToINGW()");
}

// Interface from INGwFtTkVersionHandlerInf
int 
INGwIfrMgrManager::negotiateVersion(const ObjectId &peer,
																		const INGwFtTkVersionSet& peerVersion)
{
#if 0
   RSI_NSP_CCM::VersionMgr::SubsystemType type;

   type = RSI_NSP_CCM::VersionMgr::SLEE_SUBSYSTEM;

   if((peer.getComponentId() != mCallDist.getPrimarySlee()) &&
      (peer.getComponentId() != mCallDist.getBackUpSlee()))
   {
      type = RSI_NSP_CCM::VersionMgr::CCM_SUBSYSTEM;
   }

   return RSI_NSP_CCM::VersionMgr::getInstance().negotiateVersion(peer,
                                                                  peerVersion,
                                                                  type);
#endif
  return 0;
}

void 
INGwIfrMgrManager::connectPeerSubsystems()
{
   {
      INGwIfrUtlGuard(&m_PeerStateChangeMutex);

      if(m_ParamRep.getPeerId())
      {
         m_FtMsnMgr->addINGW(m_ParamRep.getPeerId());
         m_ParamRep.setPeerStatus(1, time(NULL),"INGwIfrMgrManager::connectPeerSubsystems");
      }
   }
}

int
INGwIfrMgrManager::setSelfMode(bool p_bIsActive)
{
   LogINGwTrace(false, 0, "IN INGwIfrMgrManager::setSelfMode()");
   logger.logINGwMsg(false, ALWAYS_FLAG, -1,
                     "Recvd seld mode change to [%s]",
                     p_bIsActive ? "TRUE" : "false");
   std::string lRoleStr = "0";
   try
   {
      if(p_bIsActive)
      {
         lRoleStr = "1";
         modifyCfgParam(cmCURRENT_ROLE, "Primary", CONFIG_OP_TYPE_REPLACE,
                       BayAgentImpl::instance()->getSubsystemID(), true);
      }
      else
      {
         modifyCfgParam(cmCURRENT_ROLE, "StandBy", CONFIG_OP_TYPE_REPLACE,
                       BayAgentImpl::instance()->getSubsystemID(), true);
      }
      char currentTime[101];
      currentTime[100] = '\0';
      time_t now = time(NULL);
      struct tm current;
      localtime_r(&now, &current);
      strftime(currentTime, 100, "%c %Z", &current);

      logger.logMsg(ALWAYS_FLAG, 0, "Time on Role change updated. [%d] [%s]",
                    now, currentTime);

      modifyCfgParam("1.12.3", currentTime, CONFIG_OP_TYPE_REPLACE,
                     BayAgentImpl::instance()->getSubsystemID(), true);
   }
   catch(...)
   {
      logger.logMsg(ALWAYS_FLAG, 0,
                    "Couldn't modify config param to BayManager.");
   }

   m_IPAddrHndlr.setSelfMode(p_bIsActive);

   //update role change to providers
   int result = 0;
   logger.logMsg(TRACE_FLAG, 0, "Call state change for providers");
   ProviderMap::iterator it = m_ProviderMap.begin();
   for( ; it != m_ProviderMap.end(); it++) {
      result = it->second->configure(ingwIS_PRIMARY, lRoleStr.c_str(),
                                     INGwIfrUtlConfigurable::CONFIG_OP_TYPE_REPLACE);
      if(0 != result) {
         logger.logINGwMsg(false, ERROR_FLAG, -1,
                           "State Change failed for provider type <%d>", it->first);
         break;
      }

   }

   m_ParamRep.setValue(ingwIS_PRIMARY, lRoleStr.c_str());

   LogINGwTrace(false, 0, "OUT INGwIfrMgrManager::setSelfMode()");
   return 0;
}

int
INGwIfrMgrManager::handleWorkerClbk(INGwIfrMgrWorkUnit* apWork)
{
  LogINGwTrace(false, 0, "IN handleWorkerClbk()");

  int l_retVal = OP_FAIL;
  char lpcTime[64];

  if(0 == apWork) {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
                  " INGwIfrMgrWorkUnit* apWork == NULL \n");
    LogINGwTrace(false, 0, "OUT handleWorkerClbk");
    return OP_FAIL;
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0, "handleWorkerClbk(): "
                    "WorkUnit type<%d> recvd, workunit <%p>",
                    (int)apWork->meWorkType, apWork);

  switch(apWork->meWorkType)
  {

    case INGwIfrMgrWorkUnit::TIMER_MSG:
    {
      logger.logINGwMsg(false, TRACE_FLAG, 0, "handleWorkerClbk(): "
                        "Timerid <%u> fired, workunit <%p>",
                        apWork->muiTimerId, apWork);
      // Get the context and call the handleTimeout method.
      INGwIfrRoleTimerContext *timercontext = (INGwIfrRoleTimerContext *)(apWork->mpContextData);

      if(!timercontext)
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0, "handleWorkerClbk(): "
                          "Found NULL timer context in WorkUnit");
        l_retVal = OP_FAIL;
      }
      else
      {
        // If the timer had already been stopped, its status will be false.
        // Check this and delete the timer if required.
        SchedulerMsg* sm = dynamic_cast<SchedulerMsg*>(apWork);
        if(!sm)
        {
          logger.logINGwMsg(false, ERROR_FLAG, 0, "handleWorkerClbk(): "
                            "Error casting workunit into scheduler msg");
          l_retVal = OP_FAIL;
        }
        else if(sm->getStatus())
        {
          logger.logINGwMsg(false, TRACE_FLAG, 0, "handleWorkerClbk(): "
                            "Calling Role Manager to handle TimeOut");

          m_RoleManager->handleTimerExpiry(apWork->muiTimerId, timercontext);
          delete timercontext;

          l_retVal = OP_SUCCESS;
        }
        else
        {
          logger.logINGwMsg(false, TRACE_FLAG, 0, "handleWorkerClbk(): "
                 "Not calling handleTimeout, since timer status is false");
          delete timercontext;
          l_retVal = OP_SUCCESS;
        }
      }

      break;
    }// case INGwIfrMgrWorkUnit::TIMER_MSG

		case INGwIfrMgrWorkUnit::START_ROLE_TIMER :
		{
			m_RoleManager->startRoleTimer();
		}
		break;

		case INGwIfrMgrWorkUnit::START_ROLE_RES_TIMER :
		{
			m_RoleManager->startRoleResponseTimer();
		}
		break;

		case INGwIfrMgrWorkUnit::PEER_MSG_FT_ROLE_PKT :
		{
			INGwFtPktRoleMsg* lRoleMsg = static_cast<INGwFtPktRoleMsg*> (apWork->mpMsg);
			if(NULL == lRoleMsg)
			{
          logger.logINGwMsg(false, ERROR_FLAG, 0, "handleWorkerClbk(): "
                            "INGwFtPktRoleMsg casted to NULL. Depacketization ERROR.");
			}
			else
			{
        logger.logINGwMsg(false, ALWAYS_FLAG, 0, "handleWorkerClbk(): "
                          "PEER_MSG_FT_ROLE_PKT Recvd from PEER : <%s> ",
													lRoleMsg->toLog().c_str());

        if(m_ParamRep.getPeerId() != lRoleMsg->getSender() )
				{
           logger.logINGwMsg(false, ERROR_FLAG, 0, "handleWorkerClbk(): "
                            "MSG sender <%d> is not self Peer <%d> ",
														(int)lRoleMsg->getSender(), (int)m_ParamRep.getPeerId());
				   delete lRoleMsg;
					 break;
				}

        //Recvd Msg from Peer so Peer FT conn shd be UP
				//If FT conn not UP here, connect now

        t_PeerState lPeerState = m_RoleManager->getPeerState();
				if(lPeerState != PeerState_Up)
				{
           m_FtMsnMgr->addINGW(m_ParamRep.getPeerId());

           if(m_ParamRep.getPeerStatus() != 1)
           {
             {
		           INGwIfrUtlGuard(&m_PeerStateChangeMutex);
               m_ParamRep.setPeerStatus(1, time(NULL),
               "INGwIfrMgrManager::handleWorkerClbk PEER_MSG_FT_ROLE_PKT");
	           }
           }
				}

				if(ACTIVE_ROLE_MSG_TYPE == lRoleMsg->getRoleMsgType())
				{
					 m_RoleManager->handlePeerActiveMsg();

           memset(lpcTime, 0, sizeof(lpcTime));
           lpcTime[0] = '1';
           g_getCurrentTime(lpcTime);
           printf("[+INC+] handleWorkerClbk(): %s "
                  "Processing ACTIVE_ROLE_MSG_TYPE\n",
                  lpcTime); fflush(stdout);

	         logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
              "handleWorkerClbk(), Processing ACTIVE_ROLE_MSG_TYPE");

				}
				else if(ACTIVE_PENDING_ROLE_MSG_TYPE == lRoleMsg->getRoleMsgType())
				{
					 m_RoleManager->handlePeerActivePendingMsg();

           memset(lpcTime, 0, sizeof(lpcTime));
           lpcTime[0] = '1';
           g_getCurrentTime(lpcTime);
           printf("[+INC+] handleWorkerClbk(): %s "
                  "Processing ACTIVE_PENDING_ROLE_MSG_TYPE\n",
                  lpcTime); fflush(stdout);

	         logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
              "handleWorkerClbk(), Processing ACTIVE_PENDING_ROLE_MSG_TYPE");

				}
				else if (ACTIVE_ROLE_ACK_MSG_TYPE == lRoleMsg->getRoleMsgType())
				{
					 m_RoleManager->handlePeerActiveAckMsg();

           memset(lpcTime, 0, sizeof(lpcTime));
           lpcTime[0] = '1';
           g_getCurrentTime(lpcTime);
           printf("[+INC+] handleWorkerClbk(): %s "
                  "Processing ACTIVE_ROLE_ACK_MSG_TYPE\n",
                  lpcTime); fflush(stdout);

	         logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
              "handleWorkerClbk(), Processing ACTIVE_ROLE_ACK_MSG_TYPE");

				}
				else
				{
           logger.logINGwMsg(false, ERROR_FLAG, 0, "handleWorkerClbk(): "
                            "ERROR.UNKNOWN Role TYPE recvd <%d> ",
														(int)lRoleMsg->getRoleMsgType());
				}

				delete lRoleMsg;
			}
		}
		break;

    default:
      logger.logINGwMsg(false, ERROR_FLAG, 0, "handleWorkerClbk(): "
                        "Unrecognized work type <%d>",
                        apWork->meWorkType);
      l_retVal = OP_FAIL;
      break;
  } // End of switch

  apWork->mpContextData = NULL;
  LogINGwTrace(false, 0, "OUT handleWorkerClbk()");
}

int
INGwIfrMgrManager::resolveRole()
{
  LogINGwTrace(false, 0, "IN INGwIfrMgrManager::resolveRole()");

	int retValue = -1;

	retValue = m_RoleManager->startRoleResolution();

  if(retValue == 0)
	{
    logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                  "INGwIfrMgrManager::resolveRole : "
									"Role Resolution started ");

    // Check the states of peer , 
    // update them in the config repository.
    long lPeerId = m_ParamRep.getPeerId();
    RSIEmsTypes::ComponentState peerINGwState;
		bool bPeerAdded = false;

    t_RoleResolutionState lRoleResoState = RoleResoState_Unknown;

		do
		{
      if(! bPeerAdded)
      {
				if(lPeerId)
				{
          peerINGwState = (RSIEmsTypes::ComponentState) getState(lPeerId);

          if( (peerINGwState == RSIEmsTypes::ComponentState_Running) ||
              (peerINGwState == RSIEmsTypes::ComponentState_Loaded))
          {
						{
              INGwIfrUtlGuard(&m_PeerStateChangeMutex);
              m_ParamRep.setPeerStatus(1, time(NULL),"INGwIfrMgrManager::resolveRole");
            }
            m_FtMsnMgr->addINGW(lPeerId);
					  bPeerAdded = true;
            logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
                              "INGwIfrMgrManager::resolveRole : "
                              "Setting peer INGW status in "
                              "the param repository to %d", peerINGwState);
          }

        }
				else
				{
					m_RoleManager->setPeerState(PeerState_No_Peer);
					bPeerAdded = true;
				}
      }
			lRoleResoState = m_RoleManager->getRoleResolutionState();
			int lWaitDuration = 2;
      logger.logINGwMsg(false, TRACE_FLAG, 0,
                        "INGwIfrMgrManager::resolveRole : "
									      "Role Resolution not completed. "
									      "Will wait for its completion.");
      sleep(lWaitDuration);
		}
		while(lRoleResoState != RoleResoState_Completed);

    logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                      "INGwIfrMgrManager::resolveRole : "
									    "Role Resolution completed. ");

	}
	else
	{
    logger.logINGwMsg(false, ERROR_FLAG, 0,
                  "INGwIfrMgrManager::resolveRole : "
									"ERROR : Role Resolution cannot be started "
									"Quitting deliberatly ");
    exit(1);
	}
  mIsRoleResDone = true;
  LogINGwTrace(false, 0, "OUT INGwIfrMgrManager::resolveRole()");
	return 0;
}

int
INGwIfrMgrManager::handlePeerConnect()
{
  LogINGwTrace(false, 0, "IN INGwIfrMgrManager::handlePeerConnect()");

   //update peer state change to providers
   int result = 0;
   logger.logMsg(TRACE_FLAG, 0, "Call state change for providers");

   INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
																						 __FILE__, __LINE__,
																						 INGW_FT_INITIATED,
																						 "INGW FT Alarm ", 0,
																						 " - FT Replication Initiated");

   ProviderMap::iterator it = m_ProviderMap.begin();
   for( ; it != m_ProviderMap.end(); it++) {
      result = it->second->configure(ingwPEER_CONNECTED, "1",
                                     INGwIfrUtlConfigurable::CONFIG_OP_TYPE_REPLACE);
      if(0 != result) {
         logger.logINGwMsg(false, ERROR_FLAG, -1,
                           "Peer State Change failed for provider type <%d>", it->first);
         break;
      }

   }

   INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
																						 __FILE__, __LINE__,
																						 INGW_FT_OVER,
																						 "INGW FT Alarm ", 0,
																						 " - FT Replication Completed");

  LogINGwTrace(false, 0, "OUT INGwIfrMgrManager::handlePeerConnect()");
	return 0;
}

void
INGwIfrMgrManager::reportMeasurementSet(void *p_Obj)
{
  LogINGwTrace(false, 0, "IN INGwIfrMgrManager::reportMeasurementSet()");

  RSIEmsTypes::MeasurementSetList *lpSet =
                (RSIEmsTypes::MeasurementSetList*) p_Obj;

  logger.logINGwMsg (false, VERBOSE_FLAG, 0,
    "Number of Measurement Sets <%d>", lpSet->listOfMeasurementSet.length());

  try
  {
    BayAgentImpl::instance()->reportMeasurementSets (*lpSet);
  }
  catch (...)
  {
    logger.logINGwMsg (false, ERROR_FLAG, 0,
      "Unable to reportMeasurementSets to BayAgent");
  }

  LogINGwTrace(false, 0, "OUT INGwIfrMgrManager::reportMeasurementSet()");
  return;
}

int
INGwIfrMgrManager::fetchSs7InitialConfig(long cmdType, int myRole)
{
  int retVal = 1;
  char lpcTime[64];
  LogINGwTrace(false, 0, "IN INGwIfrMgrManager::fetchSs7InitialConfig()");

	// To be used only for debugging
	if(configLoopBack == 1)
	{
    logger.logINGwMsg(false,ALWAYS_FLAG, 0, "fetchSs7InitialConfig(), loopback");
		return retVal;
	}

	long subSysId = INGwIfrPrParamRepository::getInstance().getSelfId();

	RSIEmsTypes::cmd_type cmd;
  INGwSmWrapper *lpSmWrapper= INGwTcapProvider::getInstance().getSmWrapperPtr(); 
  try 
  {
    RSIEmsTypes::Ss7SigtranInitialReq_var config =
		BayAgentImpl::getInstance().fetchSs7Config(subSysId, 
												(int)RSIEmsTypes::ALL_SS7);

		if(config->addNetworkList_ss7.length() ==0) {
      lpSmWrapper->setAllowCfgFromEms(true,
           "INGwIfrMgrManager::fetchSs7InitialConfig()", __LINE__);
      logger.logINGwMsg(false,ERROR_FLAG,0,"add network data list empty");
  		LogINGwTrace(false, 0, "OUT INGwIfrMgrManager::fetchSs7InitialConfig()");
      return 0;
		}

		// no need to send Start Trigger incase of standby
		if(myRole == 1) {

      lpSmWrapper->setAllowCfgFromEms(false,
           "INGwIfrMgrManager::fetchSs7InitialConfig()", __LINE__);

			INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
			lqueueMsg->req = NULL;
			lqueueMsg->src = BP_AIN_SM_STACK_CONFIG_START;

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] fetchSs7InitialConfig(): %s "
             "Enqueuing BP_AIN_SM_STACK_CONFIG_START\n",
             lpcTime); fflush(stdout);
      
      logger.logINGwMsg(false,ALWAYS_FLAG, 0,
             "fetchSs7InitialConfig(): Enqueuing BP_AIN_SM_STACK_CONFIG_START");
			lpSmWrapper->postMsg(lqueueMsg,true);
		}

    logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
      " addNetworkList size <%d>",config->addNetworkList_ss7.length());
     
		for (int i=0; i < config->addNetworkList_ss7.length(); ++i) 
    {
			RSIEmsTypes::AddNetwork laddnw = config->addNetworkList_ss7[i];
      Ss7SigtranSubsReq *lreq = new Ss7SigtranSubsReq;
      memset(lreq,0,sizeof(Ss7SigtranSubsReq));
			string cause = copyAddNetwork(lreq,laddnw);
      if(NULL == lreq)
			{
        logger.logINGwMsg(false, ERROR_FLAG, 0, "[fetchSs7InitialConfig] "
          "Error in processing addnw <%d> issue[%s] command quitting fetch",
					i, cause.c_str());
        retVal = 0; 
      }
      INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
      lqueueMsg->req = lreq;
      lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      lqueueMsg->from= 1;
      lpSmWrapper->postMsg(lqueueMsg,true);
		}

		if(config->userPartList_ss7.length() !=0) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
        " userPartList size <%d>",config->userPartList_ss7.length());

		  for (int i=0; i <config->userPartList_ss7.length(); ++i)
      {
        RSIEmsTypes::AddUserPart laddUsrPart = config->userPartList_ss7[i]; 
        Ss7SigtranSubsReq *lreq = new Ss7SigtranSubsReq;
        memset(lreq,0,sizeof(Ss7SigtranSubsReq));
        if(0 == copyAddUserPart(lreq,laddUsrPart)){
          logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig] "
            "Error in processing addUserPart <%d> command quitting fetch",i);
          retVal = 0; 
        }
        INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
        lqueueMsg->req = lreq;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      	lqueueMsg->from= 1;
        lpSmWrapper->postMsg(lqueueMsg,true);
      }
		}

		if(config->lnkSetList_ss7.length() != 0) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
        " linkSet List size <%d>",config->lnkSetList_ss7.length());
		  for (int i=0; i <config->lnkSetList_ss7.length(); ++i)
      { 
        RSIEmsTypes::AddLinkSet laddLnkset = config->lnkSetList_ss7[i]; 
        Ss7SigtranSubsReq *lreq = new Ss7SigtranSubsReq;
        memset(lreq,0,sizeof(Ss7SigtranSubsReq));
        if(0 == copyAddLinkset(lreq,laddLnkset)){
          logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig] "
            "Error in processing addlnkSet <%d> command quitting fetch",i);
          retVal = 0; 
        }
        INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
        lqueueMsg->req = lreq;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      	lqueueMsg->from= 1;
        lpSmWrapper->postMsg(lqueueMsg,true);
      }
		}

		if(config->SsnList_ss7.length() !=0) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
        " Ssn List size <%d>",config->SsnList_ss7.length());

		  for (int i=0; i <config->SsnList_ss7.length(); ++i)
      {
        RSIEmsTypes::AddSsn lssn = config->SsnList_ss7[i];

				// Only add local SSN else continue
				//Local-ssn = 0, Remote-ssn =1
				//Local-ssn = 1, Remote-ssn =2 
				if(lssn.ssnType == 2) 
				{
					logger.logMsg(ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
					" ssn is remote so continuing");
					continue;
				}

        Ss7SigtranSubsReq *lreq = new Ss7SigtranSubsReq;
        memset(lreq,0,sizeof(Ss7SigtranSubsReq));
        string cause = copyAddLocalSsn(lreq,lssn);
        if(NULL == lreq){
          logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig] "
            "Error in processing SsnList_ss7<%d> issue[%s] command quitting "
						" fetch",i, cause.c_str());
          retVal = 0; 
        }
        INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
        lqueueMsg->req = lreq;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      	lqueueMsg->from= 1;
        lpSmWrapper->postMsg(lqueueMsg,true);
      }
		}


		if(config->routeList_ss7.length() !=0) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
        " Route List size <%d>",config->routeList_ss7.length());

		  for (int i=0; i <config->routeList_ss7.length(); ++i)
      {
        RSIEmsTypes::AddRoute laddRoute = config->routeList_ss7[i];
        Ss7SigtranSubsReq *lreq = new Ss7SigtranSubsReq;
        memset(lreq,0,sizeof(Ss7SigtranSubsReq));
        if(0 == copyAddRoute(lreq,laddRoute)){
          logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig] "
            "Error in processing addRoute <%d> command quitting fetch",i);
          retVal = 0; 
        }
        INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
        lqueueMsg->req = lreq;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      	lqueueMsg->from= 1;
        lpSmWrapper->postMsg(lqueueMsg,true);
      }
		}

		if(config->lnkList_ss7.length() != 0) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
        " Link List size <%d>",config->lnkList_ss7.length());
		  for (int i=0; i < config->lnkList_ss7.length(); ++i) 
      {
        RSIEmsTypes::AddLink laddlink =  config->lnkList_ss7[i];
        Ss7SigtranSubsReq *lreq = new Ss7SigtranSubsReq;
        memset(lreq,0,sizeof(Ss7SigtranSubsReq));
        if(0 == copyAddLink(lreq,laddlink)){
          logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig] "
            "Error in processing addlnk <%d> command quitting fetch",i);
          retVal = 0; 
        }
        INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
        lqueueMsg->req = lreq;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      	lqueueMsg->from= 1;
        lpSmWrapper->postMsg(lqueueMsg,true);
      }
		}

    std::map<int, int> gtRuleIdVsIndx;
    gtRuleIdVsIndx.clear();

		if(config->gtRuleList_ss7.length() != 0) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
        " GtRule List size <%d>",config->gtRuleList_ss7.length());

		  for (int i=0; i <config->gtRuleList_ss7.length(); ++i)
      {
        RSIEmsTypes::AddGtRule laddRule = config->gtRuleList_ss7[i];

        gtRuleIdVsIndx[laddRule.gtRuleId] = i;

        Ss7SigtranSubsReq *lreq = new Ss7SigtranSubsReq;
        memset(lreq,0,sizeof(Ss7SigtranSubsReq));
        copyAddRule(lreq,laddRule);
          logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig] "
            "processed gtRuleList_ss7<%d> command data",i);
        INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
        lqueueMsg->req = lreq;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      	lqueueMsg->from= 1;
        lpSmWrapper->postMsg(lqueueMsg,true);
      }
		}

		if(config->addrMapList_ss7.length() !=0) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
        " addrMap List size <%d>",config->addrMapList_ss7.length());

		  for (int i=0; i <config->addrMapList_ss7.length(); ++i)
      {
        RSIEmsTypes::AddAddrMapCfg laddAddrMap = config->addrMapList_ss7[i]; 

        // Set the AddrMap attributes specific to the GTRule to which it is associated to
        laddAddrMap.format = config->gtRuleList_ss7[gtRuleIdVsIndx[laddAddrMap.gtRuleId]].format;
        laddAddrMap.oddEven = config->gtRuleList_ss7[gtRuleIdVsIndx[laddAddrMap.gtRuleId]].oddEven;
        laddAddrMap.natAddr = config->gtRuleList_ss7[gtRuleIdVsIndx[laddAddrMap.gtRuleId]].natAddr;
        laddAddrMap.tType = config->gtRuleList_ss7[gtRuleIdVsIndx[laddAddrMap.gtRuleId]].tType;
        laddAddrMap.numPlan = config->gtRuleList_ss7[gtRuleIdVsIndx[laddAddrMap.gtRuleId]].numPlan;
        laddAddrMap.encSch = config->gtRuleList_ss7[gtRuleIdVsIndx[laddAddrMap.gtRuleId]].encSch;

        Ss7SigtranSubsReq *lreq = new Ss7SigtranSubsReq;
        memset(lreq,0,sizeof(Ss7SigtranSubsReq));
        if(0 == copyAddGtAddrMap(lreq, laddAddrMap)){
          logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig] "
            "Error in processing addrMapList_ss7<%d> command quitting fetch",i);
          retVal = 0; 
        }
        INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
        lqueueMsg->req = lreq;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      	lqueueMsg->from= 1;
        lpSmWrapper->postMsg(lqueueMsg,true);
      }
		}

		if(config->apspList_ss7.length() !=0) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
        " apsp List size <%d>",config->apspList_ss7.length());

		  for (int i=0; i <config->apspList_ss7.length(); ++i)
      {
        RSIEmsTypes::AddPsp lasp = config->apspList_ss7[i]; 
        Ss7SigtranSubsReq *lreq = new Ss7SigtranSubsReq;
        memset(lreq,0,sizeof(Ss7SigtranSubsReq));
        copyAddPsp(lreq,lasp);
        logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig] "
            "processed apspList_ss7<%d> command data",i);
        INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
        lqueueMsg->req = lreq;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      	lqueueMsg->from= 1;
        lpSmWrapper->postMsg(lqueueMsg,true);
      }
		}

		if(config->epList_ss7.length() !=0) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
        " end point List size <%d>",config->epList_ss7.length());
		  for (int i=0; i <config->epList_ss7.length(); ++i)
      {
        RSIEmsTypes::AddEndPoint lep = config->epList_ss7[i];
        Ss7SigtranSubsReq *lreq = new Ss7SigtranSubsReq;
        memset(lreq,0,sizeof(Ss7SigtranSubsReq));
        copyAddEndPoint(lreq,lep);
        logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig] "
          "processed epList_ss7 <%d> command data",i);
        INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
        lqueueMsg->req = lreq;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      	lqueueMsg->from= 1;
        lpSmWrapper->postMsg(lqueueMsg,true);
      }
		}

		if(config->apsList_ss7.length() !=0) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
        " aps List size <%d>",config->apsList_ss7.length());

		  for (int i=0; i <config->apsList_ss7.length(); ++i)
      {
        RSIEmsTypes::AddPs lps = config->apsList_ss7[i];
        Ss7SigtranSubsReq *lreq = new Ss7SigtranSubsReq;
        memset(lreq,0,sizeof(Ss7SigtranSubsReq));
        copyAddPs(lreq,lps);
        logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig] "
          "processed apsList_ss7 <%d> command data",i);
          retVal = 0; 
        INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
        lqueueMsg->req = lreq;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      	lqueueMsg->from= 1;
        lpSmWrapper->postMsg(lqueueMsg,true);
      }
		}

		if(config->assocList_ss7.length() !=0) {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig]"
        " assoc List size <%d>",config->assocList_ss7.length());

		  for (int i=0; i <config->assocList_ss7.length(); ++i)
      {
        RSIEmsTypes::M3uaAssocUp assoc = config->assocList_ss7[i]; 
        Ss7SigtranSubsReq *lreq = new Ss7SigtranSubsReq;
        memset(lreq,0,sizeof(Ss7SigtranSubsReq));

				if (assoc.currentAssocState == 0)
				{
          logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig] "
                 "Association assocId[%d] pspId[%d] endPointId[%d] "
                 "m3uaLsapId[%d] currentAssocState[%d]",
                 assoc.assocId, assoc.pspId, assoc.endPointId,
                 assoc.m3uaLsapId, assoc.currentAssocState);
					continue;
				}

        copyM3uaAssocUp(lreq,assoc);
        logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[fetchSs7InitialConfig] "
            "processed assocList_ss7<%d> command data",i);
        INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
        lqueueMsg->req = lreq;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
      	lqueueMsg->from= 1;
        lpSmWrapper->postMsg(lqueueMsg,true);
      }
		}

		if(myRole == 1) {
    	INGwSmConfigQMsg *lqueueMsg =  new INGwSmConfigQMsg;
    	lqueueMsg->req = NULL;
    	lqueueMsg->src = BP_AIN_SM_STACK_CONFIG_END;

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] fetchSs7InitialConfig(): %s "
             "Enqueuing BP_AIN_SM_STACK_CONFIG_END\n",
             lpcTime); fflush(stdout);
      
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
        "fetchSs7InitialConfig(): Enqueuing BP_AIN_SM_STACK_CONFIG_END");
    	lpSmWrapper->postMsg(lqueueMsg,true);
		}
 	}
	catch(ImMediateTypes::Reject rej) {
    logger.logINGwMsg(false, ERROR_FLAG, -1, 
			"++VER++Exception in fetching SS7 Config, rej[%d]",
				rej.errorCode);
	}

  LogINGwTrace(false, 0, "OUT INGwIfrMgrManager::fetchSs7InitialConfig()");
  return retVal;
}

void
INGwIfrMgrManager::setMsgFtFlag(int p_disableMsgFt){
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In setMsgFtFlag p_disableMsgFt<%d>",
                    p_disableMsgFt);
  if(-1 == p_disableMsgFt){
    int liMsgFtFlag = 0;
    char* lpcMsgFt = getenv("INGW_DISABLE_MSG_FT");
    if(NULL != lpcMsgFt){
      liMsgFtFlag = atoi(lpcMsgFt);
    }
    
    mDisableMsgFtFlag = liMsgFtFlag;
  }
  else{
    mDisableMsgFtFlag = p_disableMsgFt;
  }

  logger.logINGwMsg(false,ALWAYS_FLAG,0,
  "Out setMsgFtFlag mDisableMsgFtFlag<%d>", mDisableMsgFtFlag);
}

int
INGwIfrMgrManager::getMsgFtFlag(){
  return mDisableMsgFtFlag;
}

void
INGwIfrMgrManager::logPeerMsg(INGwFtPktMsg *msg)
{
   if(ingwMsgStream->isLoggable() && msg != NULL)
   {
      msg->markMsgSendRecvTime();
      ingwMsgStream->log("-> %s\n", msg->toLog().c_str());
   }
}

void        
INGwIfrMgrManager::setFtStatParamIndex()
{
	INGwIfrSmStatMgr &lStatMgr = INGwIfrSmStatMgr::instance();

	miStatParamId_CreateTcapSession  = lStatMgr.getParamIndex("101.102.1");
	miStatParamId_TcapCallSeqAck	   = lStatMgr.getParamIndex("101.102.2");
	miStatParamId_UpdateTcapSession  = lStatMgr.getParamIndex("101.102.3");
	miStatParamId_CloseTcapSession   = lStatMgr.getParamIndex("101.102.4");
	miStatParamId_CallBkup					 = lStatMgr.getParamIndex("101.102.5");
	miStatParamId_FtDelCall					 = lStatMgr.getParamIndex("101.102.6");
	miStatParamId_LoadDistMsg				 = lStatMgr.getParamIndex("101.102.7");
	miStatParamId_SasHbFailure			 = lStatMgr.getParamIndex("101.102.8");
	miStatParamId_TcapDlgInfo			   = lStatMgr.getParamIndex("101.102.9");
	miStatParamId_StackConfig				 = lStatMgr.getParamIndex("101.102.10");
	miStatParamId_AddLink				 	   = lStatMgr.getParamIndex("101.102.11");
	miStatParamId_DelLink	  	  	   = lStatMgr.getParamIndex("101.102.12");
	miStatParamId_AddLinkset				 = lStatMgr.getParamIndex("101.102.13");
	miStatParamId_DelLinkset				 = lStatMgr.getParamIndex("101.102.14");
	miStatParamId_AddNw				 			 = lStatMgr.getParamIndex("101.102.15");
	miStatParamId_DelNw						   = lStatMgr.getParamIndex("101.102.16");
	miStatParamId_AddRoute					 = lStatMgr.getParamIndex("101.102.17");
	miStatParamId_DelRoute           = lStatMgr.getParamIndex("101.102.18");
	miStatParamId_AddSsn					   = lStatMgr.getParamIndex("101.102.19");
	miStatParamId_DelSsn						 = lStatMgr.getParamIndex("101.102.20");
	miStatParamId_AddUsrPart				 = lStatMgr.getParamIndex("101.102.21");
	miStatParamId_DelUsrPart			   = lStatMgr.getParamIndex("101.102.22");
	miStatParamId_AssocDown				   = lStatMgr.getParamIndex("101.102.23");
	miStatParamId_AssocUp						 = lStatMgr.getParamIndex("101.102.24");
	miStatParamId_AddPs							 = lStatMgr.getParamIndex("101.102.25");
	miStatParamId_DelPs							 = lStatMgr.getParamIndex("101.102.26");
	miStatParamId_AddEp							 = lStatMgr.getParamIndex("101.102.27");
	miStatParamId_DelEp							 = lStatMgr.getParamIndex("101.102.28");
	miStatParamId_AddPsp						 = lStatMgr.getParamIndex("101.102.29");
	miStatParamId_DelPsp						 = lStatMgr.getParamIndex("101.102.30");
	miStatParamId_AddRule						 = lStatMgr.getParamIndex("101.102.31");
	miStatParamId_DelRule						 = lStatMgr.getParamIndex("101.102.32");
	miStatParamId_AddGtAddrMap			 = lStatMgr.getParamIndex("101.102.33");
	miStatParamId_DelGtAddrMap       = lStatMgr.getParamIndex("101.102.34");
	miStatParamId_ModLink            = lStatMgr.getParamIndex("101.102.35");
	miStatParamId_ModLinkset		     = lStatMgr.getParamIndex("101.102.36");
	miStatParamId_ModPs		     		   = lStatMgr.getParamIndex("101.102.37");
	miStatParamId_CofigStatus				 = lStatMgr.getParamIndex("101.102.38");
}

std::string 
INGwIfrMgrManager::toLogFtStats()
{
	int curVal =0;
	ostrstream output;
	output << "---- MSG RX FROM PEER INC STATS ----" << endl;

	INGwIfrSmStatMgr &lStatMgr = INGwIfrSmStatMgr::instance();

	curVal =0;
	lStatMgr.getCurValue(miStatParamId_CreateTcapSession, curVal);
	output << "CreateTcapSession: " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_TcapCallSeqAck, curVal);
	output << "TcapCallSeqAck   : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_UpdateTcapSession, curVal);
	output << "UpdateTcapSession: " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_CloseTcapSession, curVal);
	output << "CloseTcapSession : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_CallBkup, curVal);
	output << "CallBackup       : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_FtDelCall, curVal);
	output << "FtDeleteCall     : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_LoadDistMsg, curVal);
	output << "LoadDistributoMsg: " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_SasHbFailure, curVal);
	output << "SASHeartbeatFail : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_TcapDlgInfo, curVal);
	output << "TcapDlgInfo      : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_StackConfig, curVal);
	output << "StackConfig      : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AddLink, curVal);
	output << "AddLink          : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_DelLink, curVal);
	output << "DeleteLink       : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AddLinkset, curVal);
	output << "AddLinkset       : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_DelLinkset, curVal);
	output << "DeleteLinkset    : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AddNw, curVal);
	output << "Add Network      : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_DelNw, curVal);
	output << "Delete Network   : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AddRoute, curVal);
	output << "Add Route        : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_DelRoute, curVal);
	output << "Delete Route     : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AddSsn, curVal);
	output << "Add Local SSN    : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_DelSsn, curVal);
	output << "Delete Local SSN : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AddUsrPart, curVal);
	output << "Add Userpart     : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_DelUsrPart, curVal);
	output << "Delete Userpart  : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AssocDown, curVal);
	output << "Assoc Down       : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AssocUp, curVal);
	output << "Assoc UP         : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AddPs, curVal);
	output << "Add Ps           : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_DelPs, curVal);
	output << "Delete Ps        : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AddEp, curVal);
	output << "Add Endpoint     : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_DelEp, curVal);
	output << "Delete Endpoint  : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AddPsp, curVal);
	output << "Add Psp          : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_DelPsp, curVal);
	output << "Delete Psp       : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AddRule, curVal);
	output << "Add Rule         : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_DelRule, curVal);
	output << "Delete Rule      : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_AddGtAddrMap, curVal);
	output << "Add Gt AddressMap: " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_DelGtAddrMap, curVal);
	output << "Del Gt AddressMap: " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_ModLink, curVal);
	output << "Modify Link      : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_ModLinkset, curVal);
	output << "Modify Linkset   : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_ModPs, curVal);
	output << "Modify Ps        : " << curVal << endl;

	curVal =0;
	lStatMgr.getCurValue( miStatParamId_CofigStatus, curVal);
	output << "Config Status    : " << curVal << endl;

	return output.str();
}

bool 
INGwIfrMgrManager::isRoleResolutionDone(){
 logger.logINGwMsg(false,ALWAYS_FLAG,0,
 "isRoleResolutionDone Role-resolution <%s>",
 ((mIsRoleResDone==true)?"done":"not done")); 
  return mIsRoleResDone;
 
}


