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
//     File:     INGwIfrPrParamRepository.C
//
//     Desc:     This class represents repository for all the configuration parameters for INGw
//               Provided function to get and set configuration data
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraParamRep");

#include <fstream>

using namespace std;

#include <Agent/BayAgentImpl.h>
#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>

// BPUsa07781 : [
#ifndef mgcpIVR_FAILURE_LEAKS_PER_MINUTE
#define mgcpIVR_FAILURE_LEAKS_PER_MINUTE "IVR_FAILURE_LEAKS_PER_MINUTE"
#endif

#ifndef mgcpMAX_TOLERATED_IVR_FAILURES
#define mgcpMAX_TOLERATED_IVR_FAILURES "MAX_TOLERATED_IVR_FAILURES"
#endif
// : ]

//PANKAJ To replace with Actual Streamer - DONE To compile
extern BpGenUtil::INGwIfrSmAppStreamer *ingwMsgStream;

// Default values for the parameters
const char* ingwDEBUG_PORT_DEF                      = "5060";
const char* ingwDEBUG_WAIT_FOR_DEBUGGER_IN_MSEC_DEF = "0";
const char* ingwDEBUG_CHECK_ALLOC_SIZE_IN_BYTES_DEF = "5000";
const char* ingwDEBUG_DIR_PATH_FOR_PERF_DAT_DEF     = "/tmp";
const char* ingwDEBUG_USE_MEM_POOL_DEF              = "enabled";
const char* ingwWORKER_Q_SIZE_LIMIT_DEF             = "45";
const char* ingwIVR_REPLICATION_DEF                 = "init";

const char* ingwSOLE_CONN_TIMEOUT_IN_MSEC_DEF_MIN   = "5000";
const char* ingwSOLE_CONN_TIMEOUT_IN_MSEC_DEF       = "30000";
const char* ingwPREF_NW_DEF                         = "SIGTRAN";

// protocol Specifier
const char* ingwPROTOCOL = "PROTO";

INGwIfrPrParamRepository* INGwIfrPrParamRepository::mpSelf = 0;

INGwIfrPrParamRepository& INGwIfrPrParamRepository::getInstance(void) 
{ 
   if(0 == mpSelf) 
   {
      mpSelf  = new INGwIfrPrParamRepository();
   }

   return *mpSelf; 
}

INGwIfrPrParamRepository::INGwIfrPrParamRepository() :
   mlSelfId(0),
   mlPeerId(0),
   mlAgentPort(0),
   miMsgDebugLevel(0),
   mPeerStatus(0),
   mPeerStartTime(0),
   miSoleConnTimerVal(0),
   eventStoreSleeFTReplay(true),
   eventStoreHeldInMSec(180000),
   _processingState(0),
   _operationMode(UnknownMode),
	 mHBTimeout(5000),
	 mHBTimeoutCount(2),
	 mSasFipList(NULL)
{ 
   LogINGwTrace(false, 0, "IN INGwIfrPrParamRepository()");
   LogINGwTrace(false, 0, "OUT INGwIfrPrParamRepository()");
}

INGwIfrPrParamRepository::~INGwIfrPrParamRepository()
{ 
   LogINGwTrace(false, 0, "IN ~INGwIfrPrParamRepository()");
   LogINGwTrace(false, 0, "OUT ~INGwIfrPrParamRepository()");
}

void INGwIfrPrParamRepository::initialize(int aiArgc, const char** acArgv) 
                                  throw (INGwIfrUtlReject)
{
   LogINGwTrace(false, 0, "IN initialize()");

   if((0 == aiArgc) && (0 == acArgv)) 
   {
      if(0 != mlSelfId) 
      {
         mSelfIPAddr = getIPAddr(mlSelfId);
         logger.logINGwMsg(false, VERBOSE_FLAG, 0, "IP addr for this INGw is [%s]",
                         mSelfIPAddr.c_str());
      }

      if(0 != mlPeerId) 
      {
         mPeerIPAddr = getIPAddr(mlPeerId);
         logger.logINGwMsg(false, VERBOSE_FLAG, 0, "IP addr for peer INGw is [%s]",
                         mPeerIPAddr.c_str());
      }
      LogINGwTrace(false, 0, "OUT initialize()");
      return ;
   }

   pthread_rwlock_init(&mRWLock, 0);

   //
   // Obtain and store all the env variables required by INGw in the repository
   //
   getEnVar("PREF_NW", ingwPREF_NW_DEF);
   getEnVar(ingwDEBUG_PORT, ingwDEBUG_PORT_DEF);
   getEnVar(ingwDEBUG_WAIT_FOR_DEBUGGER_IN_MSEC, 
            ingwDEBUG_WAIT_FOR_DEBUGGER_IN_MSEC_DEF);
   getEnVar(ingwDEBUG_CHECK_ALLOC_SIZE_IN_BYTES, 
            ingwDEBUG_CHECK_ALLOC_SIZE_IN_BYTES_DEF);
   getEnVar(ingwDEBUG_DIR_PATH_FOR_PERF_DAT, ingwDEBUG_DIR_PATH_FOR_PERF_DAT_DEF);
   getEnVar(ingwDEBUG_USE_MEM_POOL, ingwDEBUG_USE_MEM_POOL_DEF);
   getEnVar(ingwIVR_REPLICATION, ingwIVR_REPLICATION_DEF);
   getEnVar(ingwWORKER_Q_SIZE_LIMIT, ingwWORKER_Q_SIZE_LIMIT_DEF);

   // ENV flag for round-robin IVR selection policy
   getEnVar("ingwROUNDROBIN_IVR_SEL", "ENABLED");

   // ENV related to SIP provider
   getEnVar(sipSIPIVR_RECFILE_COUNTER_FILE, "");
   getEnVar(sipISSDPPARSED, "DISABLED");
   getEnVar(sipHOLD_SIP_MSG, "DISABLED");
   getEnVar(sipPSX_ALARM_INTERVAL_IN_SECS, "120");
   getEnVar(sipBP_SESSION_TIMER_ON, "DISABLED");
   getEnVar(sipANCHOR_CONNECTED_EVENT, "DISABLED");
   getEnVar(sipCHECK_ST_REINVITE, "CHECK_SE_HDR");
   getEnVar(sipCONVEDIA_ENABLED, "DISABLED");
   getEnVar(sipVXML_REQ_URI_FORMAT, "PARAM_PART");
   getEnVar(sipVXML_URL_SLASH_REPLACEMENT, "DISABLED");
   getEnVar(sipIVR_REC_FILE_PATH, "");
   getEnVar(sipIVR_ANN_SET_PATH, "");
   getEnVar(sipTIMER_T1_IN_MSECS, "");
   getEnVar(sipTIMER_T2_IN_MSECS, "");
   getEnVar(sipMAX_RETRAN, "");
   getEnVar(sipMAX_INV_RETRAN, "");

   // Statistics manager related parmeters
   getEnVar(MAX_STATPARAM_COUNT, "");
   getEnVar(STAT_DEFERREDTHREAD_DURATION, "");
   getEnVar(STAT_CONFIG_FILE, "");
   getEnVar(STAT_LEVEL, "");

   getEnVar(CCM_MAX_COMPOSITE_MSG_SIZE, "100000");

   getEnVar (SIP_OOD_FAILURE, "0");

   getEnVar(ingwSHUTDOWN_INTVL_SECS, "300");

   getEnVar(ingwPEER_REPLICATION_DELAY_MSEC, "360000");
   getEnVar(ingwPEER_REPLICATION_BURST_PER_SEC, "100");
	 // Possible values, ITU, NTT, default is ITU
   getEnVar(ingwPROTOCOL, "ITU");

	 loadDefaultParamValues();

   for(int i = 1; i < aiArgc; i++) 
   {
      string arg = acArgv[i];
      string val;
      
      if((i + 1) < aiArgc)
      {
         val = acArgv[i+1];
      }

      if(0 == strcmp("-a", acArgv[i])) 
      {
         setValue(arg, val);
         mlSelfId = strtol(acArgv[i+1], 0, 10);
         mSelfIdStr = acArgv[i+1];
      }
      else if((0 == strcmp("-s", acArgv[i])) ||
              (0 == strcmp("-a", acArgv[i])) ||
              (0 == strcmp("-d", acArgv[i])) ) 
      {
         setValue(arg, val);
      }
      else if(0 == strcmp("-cfgFile", acArgv[i])) 
      {
         try 
         {
            logger.logINGwMsg(false, TRACE_FLAG, 0, 
                            "Loading file [%s] for parameters", acArgv[i+1]);
            loadFile(acArgv[i+1]);
         } 
         catch(...) 
         { 
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
                            "Unable to read file : <%s>", acArgv[i+1]);
            logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                            "Quitting deliberately");
            exit(1);
         }
      }
   } 

   LogINGwTrace(false, 0, "OUT initialize()");
}

void INGwIfrPrParamRepository::getEnVar(const char* apcParamName, 
                                        const char* apcDefValue, char* apcOID) 
{
   const char* pcValue = NULL;
   if (NULL == (pcValue = getenv(apcParamName)) ) 
   {
      pcValue = apcDefValue;
   }

   string arg = apcParamName;
	 // If oid is present as third arg, we will use it to fill the param map
	 if(apcOID != NULL)
	 {
		 arg = string(apcOID);
	 }
   string val = pcValue;

   setValue(arg, val);

   logger.logMsg(ALWAYS_FLAG, 0, "Env [%s] loaded value [%s]",
                 arg.c_str(), val.c_str());
}

void INGwIfrPrParamRepository::loadFile(const string& aFileName) throw (INGwIfrUtlReject)
{
   LogINGwTrace(false, 0, "IN loadFile");

   ifstream inFile(aFileName.c_str());
   if(!inFile)
   {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_CONFIGURATION, 
                      "Unable to open Configuration file [%s]", 
                      aFileName.c_str());
      LogINGwTrace(false, 0, "OUT loadFile");
      _THROW_REJECT("FILE_NOT_FOUND")
   }

   string line;
   while(getline(inFile, line))
   {
      unsigned long pos = line.rfind(":");
      string id  = line.substr(0, pos);
      string val = line.substr(pos + 1, line.size() - pos - 1);
      setValue(id, val);
   }

   LogINGwTrace(false, 0, "OUT loadFile");
}

string INGwIfrPrParamRepository::getValue(const string& aOID) const 
                                  throw (INGwIfrUtlReject)
{
   string value;

   pthread_rwlock_rdlock(&mRWLock);
   StrStrMap::const_iterator it = mConfigMap.find(aOID);
   if(it != mConfigMap.end())
   {
      value = it->second;
   }
   else 
   {
      pthread_rwlock_unlock(&mRWLock);
      _THROW_REJECT("PARAM_NOT_FOUND") 
   }
   pthread_rwlock_unlock(&mRWLock);

   return value;
}

int INGwIfrPrParamRepository::getValue(const string& aOID, string& arValue) const
{
   int retValue = 0;
   pthread_rwlock_rdlock(&mRWLock);

   StrStrMap::const_iterator it = mConfigMap.find(aOID);
   if(it != mConfigMap.end())
   {
      arValue = it->second;
   }
   else 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "INGwIfrPrParamRepository: oid [%s] not configured.", 
                      aOID.c_str());
      retValue = -1;
   }

   pthread_rwlock_unlock(&mRWLock);

   return retValue;
}

string INGwIfrPrParamRepository::getIPAddr(long alSubsystemId) 
                                   throw (INGwIfrUtlReject)
{
   BayAgentImpl& agent = (*BayAgentImpl::instance());
   RSIEmsTypes::ConfigurationDetail* retValue1 = NULL;

   if (NULL == (retValue1 = agent.getConfigParam(alSubsystemId, cmNP_IP_ADDR))) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_RESOURCE_FAILED, 
                      "Unable to get IP address for [%d]", alSubsystemId);
      _THROW_REJECT("PARAM_NOT_FOUND") 
   }

   RSIEmsTypes::ConfigurationDetail_var retValue = retValue1;
   string strIPAddr((*retValue).paramValue);
   return strIPAddr;
}

void INGwIfrPrParamRepository::setValue(const string& aOID, const string& inString)
{
   string aValue;

   {
      const char *start = inString.c_str();
      const char *end = start + inString.size() - 1;

      while((end > start) && 
            (*start == ' ' || *start == '\n' || *start == '\r'))
      {
         start++;
      }

      while((end > start) && 
            (*end == ' ' || *end == '\n' || *end == '\r')) end--;

      aValue = string(start, (end - start + 1));
   }

   pthread_rwlock_wrlock(&mRWLock);

   mConfigMap[aOID] = aValue;

   logger.logMsg(ALWAYS_FLAG, 0, "Param [%s] Value [%s] included.",
                 aOID.c_str(), aValue.c_str());

   if(0 == strcmp(ingwPEER_INGW_ID, aOID.c_str())) 
   {
      mPeerIdStr = aValue;
      mlPeerId = strtol(aValue.c_str(), 0, 10);

			if (0 != mlPeerId) {
				mINGwIdList.push_back(mlPeerId);
			}
   }
   else if(0 == strcmp(ingwAGENT_PORT, aOID.c_str())) 
   {
      mlAgentPort = strtol(aValue.c_str(), 0, 10);
   }
   else if(0 == strcmp(ingwSIP_STACK_DEBUG_LEVEL, aOID.c_str())) 
   {
      miMsgDebugLevel = atoi(aValue.c_str());
   }
   else if(0 == strcmp(ingwSIP_HEART_BEAT_TIMEOUT_MSEC, aOID.c_str())) 
   {
      mHBTimeout = atoi(aValue.c_str());
   }
   else if(0 == strcmp(ingwSIP_HEART_BEAT_TIMEOUT_MAX_COUNT, aOID.c_str())) 
   {
      mHBTimeoutCount = atoi(aValue.c_str());
   }
   else if(0 == strcmp(ingwSAS_FIP_ADDR_LIST, aOID.c_str())) 
   {
			mSasFipList = INGwIfrPrParamRepository::convertCSVLToStrList(aValue.c_str());
   }

   pthread_rwlock_unlock(&mRWLock);
}

list<string>* INGwIfrPrParamRepository::convertCSVLToStrList(const char* apcCSVL)
{
   char separator = ',';
   list<string>* retList = new list<string>();

   string csvl(apcCSVL);

   unsigned long startPos = 0;
   long pos = 0;
   string id;

   while (-1 != pos)
   {
      pos = csvl.find(separator, startPos);
      if(-1 == pos) 
      {
         if(0 == startPos) 
         {
            id = csvl;
         }
         else 
         {
            id = csvl.substr(startPos, csvl.size() - startPos);
         }
      }
      else 
      {
         id = csvl.substr(startPos, pos - startPos);
      }

      retList->push_back(id);
      startPos = pos + 1;
   }

   return retList;
}

list<long>* INGwIfrPrParamRepository::convertCSVLToLongList(const char* apcCSVL)
{
   char separator = ',';
   list<long>* retList = new list<long>();

   string csvl(apcCSVL);

   unsigned long startPos = 0;
   long pos = 0;
   string id;

   while (-1 != pos)
   {
      pos = csvl.find(separator, startPos);
      if(-1 == pos) 
      {
         if(0 == startPos) 
         {
            id = csvl;
         }
         else 
         {
            id = csvl.substr(startPos, csvl.size() - startPos);
         }
      }
      else 
      {
         id = csvl.substr(startPos, pos - startPos);
      }

      retList->push_back(strtol(id.c_str(), NULL, 10));
      startPos = pos + 1;
   }

   return retList;
}

std::string INGwIfrPrParamRepository::getSubsysName(int subsystemID)
{
   {
      pthread_rwlock_rdlock(&mRWLock);

      SubsysNameMapCIt it = mNameMap.find(subsystemID);

      if(it != mNameMap.end())
      {
         std::string retVal = it->second;
         pthread_rwlock_unlock(&mRWLock);
         return retVal;
      }

      pthread_rwlock_unlock(&mRWLock);
   }

   std::string retVal;

   try
   {
      retVal = BayAgentImpl::getInstance().getComponentName(subsystemID);

      logger.logMsg(ALWAYS_FLAG, 0, "Resolved [%d] to [%s]", 
                    subsystemID, retVal.c_str());

      pthread_rwlock_wrlock(&mRWLock);
      mNameMap[subsystemID] = retVal;
      pthread_rwlock_unlock(&mRWLock);
   }
   catch(...)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to get comp [%d] name from Bmgr.",
                    subsystemID);
   }

   return retVal;
}

void  INGwIfrPrParamRepository::setMsgDebugLevel(int aiMsgDebugLevel)
{
   if(aiMsgDebugLevel & 0x1)
   {
      ingwMsgStream->setLoggable(true);
   }
   else
   {
      ingwMsgStream->setLoggable(false);
   }
   miMsgDebugLevel = aiMsgDebugLevel;
}

std::string INGwIfrPrParamRepository::toLog()
{
   INGwIfrUtlRGuard(&mRWLock);
   std::string ret;

   for(StrStrMap::const_iterator it = mConfigMap.begin(); 
       it != mConfigMap.end(); it++)
   {
      ret += it->first;
      ret += " = ";
      ret += it->second;
      ret += "\n";
   }
   return ret;
}

CCMOperationMode INGwIfrPrParamRepository::getOperationMode()
{
	 // PANKAJ Temp Fix

   const char* pcValue = NULL;
   if (NULL != (pcValue = getenv("OPER_MODE_TEMP")) ) 
   {
	    if( strncmp (pcValue, "1+1", 3) == 0)
	    {
				 _operationMode = OnePlusOne;
	    }
			else
			{
				 _operationMode = OnePlusZero;
			}

	 }
	 else
	 {
	 }

   return _operationMode;
}

void INGwIfrPrParamRepository::setOperationMode(CCMOperationMode operationMode)
{
   _operationMode = operationMode;
}

int INGwIfrPrParamRepository::getProcessingState()
{
   INGwIfrUtlRGuard(&mRWLock);
   return _processingState;
}

void INGwIfrPrParamRepository::setProcessingState(int state)
{
   INGwIfrUtlRGuard(&mRWLock);
   _processingState = state;
}

void INGwIfrPrParamRepository::setPeerIP(const char *val)
{
   INGwIfrUtlRGuard(&mRWLock);
   mPeerIPAddr = val;
}

std::vector<int>
INGwIfrPrParamRepository::getINGwList()
{
	return mINGwIdList;
}

void 
INGwIfrPrParamRepository::loadDefaultParamValues()
{
}

std::list<std::string>* 
INGwIfrPrParamRepository::getSasFipList()
{
	 return mSasFipList;
}

bool 
INGwIfrPrParamRepository::isHostPresentInSasList(std::string p_HostIP)
{
	 if(mSasFipList == NULL)
	 {
      logger.logMsg(TRACE_FLAG, 0, "SasFip List Is NULL.");
			return false;
	 }
	 bool retVal = false;
	 std::list<std::string>::iterator lIter;
	 for(lIter = mSasFipList->begin(); lIter != mSasFipList->end();
			 lIter++)
   {
      logger.logMsg(TRACE_FLAG, 0, "SAS IP in List <%s>.", 
									 (*lIter).c_str());
			if(p_HostIP == (*lIter))
			{
				 retVal = true;
				 break;
			}
	 }
	 return retVal;
}

