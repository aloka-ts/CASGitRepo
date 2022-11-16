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
//     File:     INGwSpSipProvider.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <INGwSipProvider/INGwSpSipProvider.h>
#include <INGwSipProvider/INGwSpSipStackIntfLayer.h>
#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipProvider/INGwSpSipConnection.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpThreadSpecificSipData.h>
#include <INGwSipProvider/INGwSpSipCall.h>

#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>
#include <INGwInfraParamRepository/INGwIfrPrConfigMgr.h>
#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>
#include <INGwInfraManager/INGwIfrMgrWorkerThread.h>
#include <INGwSipProvider/INGwSpDataFactory.h>
#include <INGwSipProvider/INGwSpSipCallFactory.h>
#include <INGwSipProvider/INGwSpSipConnectionFactory.h>
#include <INGwSipProvider/INGwSpStackConfigMgr.h>

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwInfraMsrMgr/MsrMgr.h>
#include <INGwInfraStatMgr/INGwIfrSmStatMgr.h>
#include <INGwInfraUtil/INGwIfrUtlSerializable.h>
#include <INGwInfraUtil/INGwIfrUtlRefCount.h>
#include <INGwFtPacket/INGwFtPktDeleteCallMsg.h>
#include <INGwFtPacket/INGwFtPktCallDataMsg.h>

using namespace std;

SipHdrTypeList dHdrsTobeDecodedList;

const int sipMAX_INVITE_IN_PROGRESS__DEF = 1500;

static void** g_tsdArray;
Sdf_st_initData *pGlbProfile;

INGwSpSipProvider* INGwSpSipProvider::mSipProvider = NULL;

//Static Stats param index

int INGwSpSipProvider::miStatParamId_SipMsgRecvd = 0;
int INGwSpSipProvider::miStatParamId_SipMsgSent = 0;
int INGwSpSipProvider::miStatParamId_NumInvRecvd = 0;
int INGwSpSipProvider::miStatParamId_NumInvRejected = 0;
int INGwSpSipProvider::miStatParamId_NumNotifyRecvd = 0;
int INGwSpSipProvider::miStatParamId_NumNotifyRejected = 0;
int INGwSpSipProvider::miStatParamId_NumNotifySent = 0;
int INGwSpSipProvider::miStatParamId_NumNotifySentRejected = 0;
int INGwSpSipProvider::miStatParamId_NumInfoRecvd = 0;
int INGwSpSipProvider::miStatParamId_NumInfoRejected = 0;
int INGwSpSipProvider::miStatParamId_NumInfoSent = 0;
int INGwSpSipProvider::miStatParamId_NumInfoSentRejected = 0;
int INGwSpSipProvider::miStatParamId_NumOptRecvd = 0;
int INGwSpSipProvider::miStatParamId_NumHBFailures = 0;

BpGenUtil::INGwIfrSmAppStreamer *sipMsgStream = NULL;

const char* INGwSpSipProvider::mpcOIDsOfInterest[] =
{
   ingwIS_PRIMARY,
   ingwSIP_STACK_DEBUG_LEVEL,
   ingwSIP_STACK_USER_PROFILE,
};

INGwSpSipProvider& INGwSpSipProvider::getInstance()
{
   if(!mSipProvider)
   {
      const char *lpcDebugFile = getenv ("INGW_DEBUG_FILE");

      if (lpcDebugFile)
        sipMsgStream = new BpGenUtil::INGwIfrSmAppStreamer("SIP_Messages", lpcDebugFile);
      else
        sipMsgStream = new BpGenUtil::INGwIfrSmAppStreamer("SIP_Messages", "ingwDebug.out");

      mSipProvider = new INGwSpSipProvider();
   }

   return *mSipProvider;
}

INGwSpSipProvider::INGwSpSipProvider():INGwIwfBaseProvider(PROVIDER_TYPE_SIP)
{
   mSipProvider = this;

#if 0
   INGwSpSipCall::setVersionDetail();
   INGwSpSipConnection::setVersionDetail();
#endif

   logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                     "INGwSpSipProvider::INGwSpSipProvider: "
										 "provider id [%d], this [%p]", 
                     getProviderType(), this);

   m_SipIface = new INGwSpSipIface();

	 m_SipCallController = new INGwSpSipCallController(m_SipIface);

   //INCTBD - Temporary logging for debugging
   logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
          "INGwSpSipProvider::INGwSpSipProvider: m_SipIface<%x>", m_SipIface);
   
	 mThreadSpecificKey = getProviderType();
	 setSipStatParamIndex();

   INGwSpData::initStaticCount();
   INGwSpSipCall::initStaticCount();
   INGwSpSipConnection::initStaticCount();
} 

INGwSpSipProvider::~INGwSpSipProvider()
{
} 

INGwSpThreadSpecificSipData& INGwSpSipProvider::getThreadSpecificSipData()
{
   pthread_key_t& l_key = INGwSpSipProvider::getInstance().getKey();
   void* l_voidTsd = pthread_getspecific(l_key);
   INGwSpThreadSpecificSipData* l_tsd = 
                                 static_cast<INGwSpThreadSpecificSipData*>(l_voidTsd);
   return *l_tsd;
}

int INGwSpSipProvider::changeState(INGwIwfBaseProvider::ProviderStateType aeState)
{
   LogINGwTrace(false, 0, "IN changeState");
   if (aeState == INGwIwfBaseProvider::PROVIDER_STATE_LOADED)
   {
			meProviderState = INGwIwfBaseProvider::PROVIDER_STATE_LOADED;
   }
   else if(aeState == INGwIwfBaseProvider::PROVIDER_STATE_RUNNING)
   {
    // If the mode of this INGW is primary, initialize and start the network
    // listener thread for sip messages.

    std::string isprimarystr = "";
    INGwIfrPrParamRepository::getInstance().getValue(ingwIS_PRIMARY, isprimarystr);

    if(atoi(isprimarystr.c_str()))
    {
      logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
										  "INGwSpSipProvider::changeState: INGW mode is primary, so starting SIP listener thread ");

      std::string listenerport = "";
      std::string listenerhost = "";
      INGwIfrPrParamRepository::getInstance().getValue
        (ingwSIP_STACK_LISTENER_PORT, listenerport);

      INGwIfrPrParamRepository::getInstance().getValue
        (ingwFLOATING_IP_ADDR, listenerhost);

      if(INGwSpStackConfigMgr::getInstance()->startListenerThread(atoi(listenerport.c_str()),
                                                                 (char *)listenerhost.c_str(), 
																						  										g_tsdArray) < 0 )
      {
        logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
													"error starting the sip network listener thread\n");
        LogINGwTrace(false, 0, "OUT changeState");
        return G_FAILURE;
      }
    }
		meProviderState = INGwIwfBaseProvider::PROVIDER_STATE_RUNNING;
  } // end of if(RUNNING...
  else if(aeState == INGwIwfBaseProvider::PROVIDER_STATE_STOPPED)
  {
    INGwIfrPrConfigMgr::getInstance().unregisterForConfig(mpcOIDsOfInterest, 
																									 sizeof(mpcOIDsOfInterest)/sizeof(char*), 
																									 this);

		meProviderState = INGwIwfBaseProvider::PROVIDER_STATE_STOPPED;
  } // end of if(STOPPED...

  LogINGwTrace(false, 0, "OUT changeState");
  return G_SUCCESS;
} // end of changeState

bool INGwSpSipProvider::getNewCallsStatus(void)
{
  return true;
}

#define MAX_USERPROFILE_SIZE 10000

bool INGwSpSipProvider::generateCallid(char *outCallid, int aiThreadIdx)
{
  LogINGwTrace(false, 0, "IN generateCallid");
	// Out Going call id will be of format GB-threadIdx-hrtime-threadid@hostIp
	// eg GB-9223372036722062514-22@192.168.1.111
  hrtime_t currTime = gethrtime();
  const std::string& lSelfIp = 
				INGwIfrPrParamRepository::getInstance().getSelfIPAddr();
  int lThreadId = INGwIfrMgrThreadMgr::getInstance().getCurrentThread().getThreadId();
	sprintf(outCallid, "GB-%d-%lld-%d@%s", aiThreadIdx, currTime, lThreadId, lSelfIp.c_str());

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										"INGwSpSipProvider::generateCallid : Generated Call ID is  <%s>", 
										outCallid);
  LogINGwTrace(false, 0, "OUT generateCallid");
  return true;
}

INGwSpSipConnection* INGwSpSipProvider::getNewConnection(INGwSpSipCall& arCall)
{
  LogINGwTrace(false, 0, "IN getNewConnection");
  INGwSpSipConnection* pConn = INGwSpSipConnectionFactory::getInstance().getNewObject();

  if(NULL != pConn) {
    pConn->setCall(&arCall);
  }
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, 
										"INGwSpSipProvider::getNewConnection: connection <%p>, call <%p>", 
										pConn, &arCall);
  LogINGwTrace(false, 0, "OUT getNewConnection");
  return pConn;
}

INGwSpSipCall* INGwSpSipProvider::getNewCall(const std::string& arconCallId, bool abGenCallId)
{
   INGwSpSipCall* pCall = INGwSpSipCallFactory::getInstance().getNewObject();

   if(NULL != pCall) 
   {
      pCall->setCallId(arconCallId);
   }

   return pCall;
}

void INGwSpSipProvider::handleBkupInfo(const std::string& arCallId, 
                                       const char* apcMsg, 
																			 short asLength)
{
   LogINGwTrace(false, 0, "IN INGwSpSipProvider::handleBkupInfo");

   INGwIfrUtlSerializable::ReplicationOperationType opType = 
                 (INGwIfrUtlSerializable::ReplicationOperationType) apcMsg[0];

   switch(opType)
   {
      case INGwIfrUtlSerializable::REP_CREATE:
      {
				 std::string lCallIdStr = arCallId;

         logger.logINGwMsg(false, TRACE_FLAG, 0, 
                           "REP_CREATE MSG recvd for <%s>",
														arCallId.c_str());

         INGwSpSipCall *currCall = m_SipCallController->getCall(lCallIdStr);
         INGwIfrUtlRefCount_var callHolder(currCall);

         if(currCall == NULL)
         {
            currCall = getNewCall(lCallIdStr, false);

            m_SipCallController->addCall(lCallIdStr, currCall);
         }
         else
         {
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
                             "Create message recvd for already created obj <%s>",
														 arCallId.c_str());
            LogINGwTrace(false, 0, "OUT INGwSpSipProvider::handleBkupInfo");
            return;
         }

         INGwIfrUtlRefCount_var callHolder1(currCall); //Call obtained from getNewCall.
                                                       //callHolder wont have this.

         int dataUsed = 0;
         if(currCall->deserialize((unsigned char *)apcMsg, 0, dataUsed, 
                                  asLength) == false)
         {
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
                            "Deserialize of Call failed.");
            currCall->cleanup();
            LogINGwTrace(false, 0, "OUT INGwSpSipProvider::handleBkupInfo");
            return;
         }

         if(dataUsed != asLength)
         {
            logger.logINGwMsg(false, ERROR_FLAG, 0, 
                            "Not all the data were used. ");
         }

         //Now we have inly one connection in Call with ID 0
			   int connId = 0;
		   	 INGwSpSipConnection* lSipConn = currCall->getConnection(connId);

			   if(lSipConn == NULL)
			   {
			     logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
			    								  "Connection not serialized in REP_CREATE call packet");
			   }
				 else
				 {
					 INGwIfrUtlRefCount_var connRef(lSipConn);
					 INGwSpSipUtil::updateSipAddSpec(lSipConn);

					 //Make a SAS structure
					 INGwSipEPInfo lSipEPInfo;
           // This is replicated message so all he client port will be same
           lSipEPInfo.port = lSipConn->getGwPort();
           lSipEPInfo.clientport = lSipConn->getGwPort();
           lSipEPInfo.initalClientPort = lSipConn->getGwPort();
           lSipEPInfo.msLocalCallID = lSipConn->getLocalCallId();

           strncpy((char*)lSipEPInfo.mEpHost, lSipConn->getGwIPAddress(),MAX_EP_HOST_LENGTH);
           strncpy((char*)lSipEPInfo.mContactHdr, lSipConn->getContactHdr(), MAX_HEADER_LEN  );

           lSipEPInfo.mContactAddSpec = lSipConn->getContactAddSpec();
           lSipEPInfo.mFromAddSpec = lSipConn->getFromAddSpec();
           lSipEPInfo.mEPTimer = new INGWSipEPTimer();

           // Insert it in End point map
					 m_SipCallController->addEndPoint(lSipEPInfo);

				 }

      }// end of case REP_CREATE
      break;

      case INGwIfrUtlSerializable::REP_REPLICATE:
      {
        logger.logINGwMsg(false, ERROR_FLAG, 0, 
													"ERROR : Should not have recvd REP_REPLICATE for <%s> "
                          "REP_REPLICATE Ft msg not supported.",
													arCallId.c_str());
        LogINGwTrace(false, 0, "OUT INGwSpSipProvider::handleBkupInfo");
        return;

      } //end of case REP_REPLICATE
      break;

      case INGwIfrUtlSerializable::REP_DELETE:
      {
				 std::string lCallIdStr = arCallId;

         logger.logINGwMsg(false, TRACE_FLAG, 0, 
                           "REP_DELETE MSG recvd for <%s>",
														arCallId.c_str());

         INGwSpSipCall *currCall = m_SipCallController->getCall(lCallIdStr);
         INGwIfrUtlRefCount_var callHolder(currCall);

         if(currCall == NULL)
         {
            logger.logINGwMsg(false, WARNING_FLAG, 0, 
                             "Delete call for a non existing call. <%s>",
														 arCallId.c_str());
            LogINGwTrace(false, 0, "OUT INGwSpSipProvider::handleBkupInfo");
            return;
         }

         //Now we have only one connection in Call with ID 0
			   int connId = 0;
		   	 INGwSpSipConnection* lSipConn = currCall->getConnection(connId);

			   if(lSipConn == NULL)
			   {
			     logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
			    								  "Connection not found in call obj for REP_DELETE msg for <%s>. "
														"Could not remove End point from its map", arCallId.c_str());
			   }
				 else
				 {
					 std::string lEpHost = lSipConn->getGwIPAddress();
					 m_SipCallController->removeEndPoint(lEpHost, lCallIdStr);
				 }

				 // PR - 51484
				 INGwIfrUtlRefCount_var connRef(lSipConn);

         currCall->cleanup();
      }//end of case  REP_DELETE
      break;
      default:
      {
         logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "Unknown type of operation [%d] on Call.", opType);
      }
   } // end of switch opType
   LogINGwTrace(false, 0, "OUT INGwSpSipProvider::handleBkupInfo");
   return;
}


int
INGwSpSipProvider::setTSD() {

  INGwIfrMgrThreadMgr& l_bpCcmTm = INGwIfrMgrThreadMgr::getInstance();
  int l_thrCnt = l_bpCcmTm.getThreadCount();

  if(l_thrCnt < 1) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
       "WorkerThreadCount is less than 1....\n");
    return OP_FAIL;
  }


  g_tsdArray = new void *[l_thrCnt];

  for (int i= 0; i < l_thrCnt; ++i) 
  {
     INGwSpThreadSpecificSipData *thrData = new INGwSpThreadSpecificSipData();
     thrData->setHdrDefault(INGwSpSipProviderConfig::getSipHeaderDefault());
     g_tsdArray[i] =  static_cast<void*>(thrData);
  }

  int l_retVal = OP_SUCCESS;
  pthread_key_t* l_key = getThreadSpecificKey();
  l_retVal =
     l_bpCcmTm.setWorkerTSD("INGW_SIP_PROVIDER", g_tsdArray, l_key);

  if(OP_FAIL == l_retVal) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
       "setWorkerTSD returned OP_FAIL\n");
  }

  return l_retVal;
}
/***************************************************************************************/
#define INITPARAM_HANDLE(paramname) \
  if(meProviderState == PROVIDER_STATE_RUNNING)                                         \
  {                                                                                     \
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,                                    \
    "INGwSpSipProvider::configure: <%s> is not modifiable at run time.", paramname);    \
    return -1;                                                                          \
  }                                                                                     \
  else                                                                                  \
  {                                                                                     \
    INGwIfrPrParamRepository::getInstance().setValue(apcOID, apcValue);                 \
  }
/******************************************************************************/

int INGwSpSipProvider::configure(const char* apcOID,
                          const char* apcValue,
                          ConfigOpType aeOpType)
{
  LogINGwTrace(false, 0, "IN configure");

  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
										"INGwSpSipProvider::configure: "
										"Received config update with oid <%s>, value <%s> "
										"in provider state <%d>", 
										apcOID, apcValue, int(meProviderState));

  if(!strcmp(ingwSIP_STACK_DEBUG_LEVEL, apcOID))
  {
    // Update the config
    bool debug = false;
    if(atoi(apcValue))
      debug = true;
    logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
										 "INGwSpSipProvider::configure: "
										 "updating the stack debug level to <%d>", 
										 debug);
    INGwIfrPrParamRepository::getInstance().setValue(apcOID, apcValue);
    INGwSpSipProviderConfig::setStackDebugLevel(debug);
  }
  else if(!strcmp(ingwSIP_STACK_USER_PROFILE, apcOID))
  {
    INITPARAM_HANDLE(apcOID)
  }
  else if(!strcmp(ingwIS_PRIMARY, apcOID))
  {
    // Update the param repository and if the mode is changing from secondary
    // to primary, start the listener thread.
    if(atoi(apcValue))
    {
      std::string previousValue = "";
      INGwIfrPrParamRepository::getInstance().getValue(
																std::string(apcOID), previousValue);
      if(atoi(previousValue.c_str()) == 0)
      {
        logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
													"INGwSpSipProvider::configure: Received CCM mode change  \
													from seconday to primary.  Starting listener thread.");

        std::string listenerport = "";
        std::string listenerhost = "";
        INGwIfrPrParamRepository::getInstance().getValue
          (ingwSIP_STACK_LISTENER_PORT, listenerport);

        INGwIfrPrParamRepository::getInstance().getValue(ingwFLOATING_IP_ADDR, 
																												 listenerhost);
        if(INGwSpStackConfigMgr::getInstance()->
							startListenerThread(atoi(listenerport.c_str()),
                                  (char *)listenerhost.c_str(), g_tsdArray) < 0)
        {
          logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
														"INGwSpSipProvider::configure: "
														"error starting sip network listener thread");
          LogINGwTrace(false, 0, "OUT configure");
          return -1;
        }
        else
          logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
														"INGwSpSipProvider::configure: "
														"Successfully started the listener thread");
      }
    }
    // Update the config repository
    // INGwIfrPrParamRepository::getInstance().setValue(apcOID, apcValue);
  }
  else if (0 == strcasecmp(ingwPEER_CONNECTED, apcOID)) 
  {
    if (atoi(apcValue))
    {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
		  									"INGwSpSipProvider::configure: "
                        "Peer Connected. Sending registered Endpoint information");
      m_SipCallController->replicateEpToPeer();
    }
  }
  else
  {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
											"INGwSpSipProvider::configure: Unrecognized oid <%s>", apcOID);
  }

  LogINGwTrace(false, 0, "OUT configure");
  return 0;
} // end of configure

void INGwSpSipProvider::updateWorkerThreadConfig(char* apcOID, char* apcValue)
{
  LogINGwTrace(false, 0, "IN updateWorkerThreadConfig");

  // Get the number of threads
  int nthreads = INGwIfrMgrThreadMgr::getInstance().getThreadCount();

  for(int i = 0; i < nthreads; ++i)
  {
    // Create a config context
    INGwSipConfigContext* context = new INGwSipConfigContext;
    context->mOid = apcOID;
    context->mVal = apcValue;

    // Create a workunit and post it
    INGwIfrMgrWorkUnit *workunit    = new INGwIfrMgrWorkUnit;
    workunit->meWorkType    = INGwIfrMgrWorkUnit::CONFIG_UPDATE;
    workunit->mpContextData = (void *)context;
    workunit->mpWorkerClbk  = &(INGwSpSipStackIntfLayer::instance());
    workunit->setHash(i);

    INGwIfrMgrThreadMgr::getInstance().postOutOfBandMsg(workunit);
  }
  LogINGwTrace(false, 0, "OUT updateWorkerThreadConfig");
}

void INGwSpSipProvider::getStatistics(ostrstream &output, int tabCount)
{
   char tabs[20];

   for(int idx = 0; idx < tabCount; idx++)
   {
      tabs[idx] = '\t';
   }
   tabs[tabCount] = '\0';


   output << tabs << "SipProvider\n";

   output << tabs << "\tTotal SIP Call Object In Use :  " 
          << INGwSpSipCall::getCount()<< "\n";
   output << tabs << "\tTotal SIP Connection Object In Use :  " 
          << INGwSpSipConnection::getCount()<< "\n";
   output << tabs << "\tTotal SIP Body Data Object In Use :  " 
          << INGwSpData::getCount()<< "\n";


   int thrCount = INGwIfrMgrThreadMgr::getInstance().getThreadCount();

   unsigned long long liTotalRetranCnt = 0;
   for(int idx = 0; idx < thrCount; idx++)
   {
      output << tabs << "\tThr" << idx << "\n";

      INGwSpThreadSpecificSipData *thrData = (INGwSpThreadSpecificSipData *)g_tsdArray[idx];
      int callCount = thrData->getCallTable().getCount();
      output << tabs << "\t\tCallCount    " << callCount << "\n";

      int timerCount = thrData->getStackTimer().getCount();
      output << tabs << "\t\tStackTimers " << timerCount << "\n";

      if(thrData->getRemoteRetransHash() != NULL)
      {
         int reCount = thrData->getRemoteRetransHash()->numberOfElements;
         output << tabs << "\t\tRemoteRetran " << reCount << "\n";
      }
      else
      {
         output << tabs << "\t\tRemoteRetran not initialized\n";
      }

      output << tabs << "\t\tRecoveryAggregate " 
             << 0 << "\n";

      output << tabs << "\t\tSipMsgRetranCnt "<< 
      INGwSpSipStackIntfLayer::instance().mplSipRetransCount[idx]<<"\n";
      liTotalRetranCnt +=  INGwSpSipStackIntfLayer::instance().mplSipRetransCount[idx];
      output << tabs << "\t-Thr" << idx << "\n";
   }

   output << tabs << "\t\tTotal Retrans Cnt "<< 
                     liTotalRetranCnt<<"\n";
   
   output << tabs << "-SipProvider\n";
}

int
INGwSpSipProvider::startUp(void)
{
  LogINGwTrace(false, 0, "IN INGwSpSipProvider::startUp");
  INGwSpSipUtil::initialize();

  INGwIfrPrConfigMgr::getInstance().registerForConfig(mpcOIDsOfInterest, 
                                  sizeof(mpcOIDsOfInterest)/sizeof(char*), 
																	this);

  INGwIfrMgrThreadMgr& l_bpCcmTm = INGwIfrMgrThreadMgr::getInstance();
  int l_thrCnt = l_bpCcmTm.getThreadCount();

  INGwSpSipProviderConfig::initialize();

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                   "Initializing call, sipconn and sipdata object pools");
  INGwSpSipConnectionFactory::initialize(1000);
  INGwSpSipCallFactory::initialize(1000);
  INGwSpDataFactory::initialize(1000);

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                    "INGwSpSipProvider::startUp: initializing "
                    "INGwSpSipStackIntfLayer");

  INGwSpSipStackIntfLayer::instance().initialize();

  if(OP_FAIL == setTSD()) 
  { 
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
		  								"error setTSD Failed ..\n");
    return G_FAILURE;
  }

  logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                   "INGwSpSipProvider::startUp: initializing sip stack");

  if(-1 == INGwSpStackConfigMgr::getInstance()->initializeSipStack())
	{
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
		  								"error initializing sip stack.\n");
    return G_FAILURE;
	}

  LogINGwTrace(false, 0, "OUT INGwSpSipProvider::startUp");
	return G_SUCCESS;
}

int 
INGwSpSipProvider::initInboundMsgConn(INGwSpSipConnection* p_SipConn)
{
	return INGwSpSipStackIntfLayer::instance().initInboundMsgConn(p_SipConn);
}

void 
INGwSpSipProvider::setSipStatParamIndex()
{
	INGwIfrSmStatMgr &lStatMgr = INGwIfrSmStatMgr::instance();

  //USING Random Oid

	miStatParamId_SipMsgRecvd = 
		lStatMgr.getParamIndex("101.101.1");
  
	miStatParamId_SipMsgSent = 
		lStatMgr.getParamIndex("101.101.2");

	miStatParamId_NumInvRecvd = 
		lStatMgr.getParamIndex("101.101.3");

	miStatParamId_NumInvRejected = 
		lStatMgr.getParamIndex("101.101.4");

	miStatParamId_NumNotifyRecvd = 
		lStatMgr.getParamIndex("101.101.5");

	miStatParamId_NumNotifyRejected = 
		lStatMgr.getParamIndex("101.101.6");

	miStatParamId_NumNotifySent = 
		lStatMgr.getParamIndex("101.101.7");

	miStatParamId_NumNotifySentRejected = 
		lStatMgr.getParamIndex("101.101.8");

	miStatParamId_NumInfoRecvd = 
		lStatMgr.getParamIndex("101.101.9");

	miStatParamId_NumInfoRejected = 
		lStatMgr.getParamIndex("101.101.10");

	miStatParamId_NumInfoSent = 
		lStatMgr.getParamIndex("101.101.11");

	miStatParamId_NumInfoSentRejected = 
		lStatMgr.getParamIndex("101.101.12");

	miStatParamId_NumOptRecvd = 
		lStatMgr.getParamIndex("101.101.130000");

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"101.101.13000 <%d>",miStatParamId_NumOptRecvd);

	miStatParamId_NumHBFailures = 
		lStatMgr.getParamIndex("101.101.1400000");
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"101.101.1400000 <%d>",miStatParamId_NumHBFailures);
}

std::string
INGwSpSipProvider::toLogSipStats()
{
	ostrstream output;

	output << "---- SIP MSG STATS ----" << endl;

	INGwIfrSmStatMgr &lStatMgr = INGwIfrSmStatMgr::instance();

	int curVal = 0;
	lStatMgr.getCurValue(miStatParamId_SipMsgRecvd, curVal);
	output << "Total Sip Msg Recvd : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_SipMsgSent, curVal);
	output << "Total Sip Msg Sent : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_NumInvRecvd, curVal);
	output << "Total Sip Invite Msg Recvd : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_NumInvRejected, curVal);
	output << "Total Sip Invite Msg Recvd Rejected : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_NumNotifyRecvd, curVal);
	output << "Total Sip Notify Msg Recvd : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_NumNotifyRejected, curVal);
	output << "Total Sip Notify Msg Recvd Rejected : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_NumNotifySent, curVal);
	output << "Total Sip Notify Msg Sent : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_NumNotifySentRejected, curVal);
	output << "Total Sip Notify Msg Sent Rejected by SAS : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_NumInfoRecvd, curVal);
	output << "Total Sip Info Msg Recvd : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_NumInfoRejected, curVal);
	output << "Total Sip Info Msg Recvd Rejected : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_NumInfoSent, curVal);
	output << "Total Sip Info Msg Sent : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_NumInfoSentRejected, curVal);
	output << "Total Sip Info Msg Sent Rejected by SAS : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_NumOptRecvd, curVal);
	output << "Total Sip Option Msg Recvd : " << curVal << endl; 

	curVal = 0;
	lStatMgr.getCurValue(miStatParamId_NumHBFailures, curVal);
	output << "Total HB Failure Count           : " << curVal << endl; 

	return output.str();
}
