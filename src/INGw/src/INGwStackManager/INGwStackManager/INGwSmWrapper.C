#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

/************************************************************************
   Name:     INAP Stack Manager Wrapper - Impl
 
   Type:     C impl file
 
   Desc:     Implementation of the Access Wrapper of Stack Manager

   File:     INGwSmWrapper.C

   Sid:      INGwSmWrapper.C 0  -  03/27/03 

   Prg:      gs,bd

************************************************************************/
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
  #include "envopt.h"
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
#ifndef __CCPU_CPLUSPLUS
}
#endif
#include "INGwSmWrapper.h"
#include <sys/utsname.h>
#include <sys/types.h>
#include <unistd.h>
#include "INGwStackManager/INGwSmRepository.h"
#include "INGwStackManager/INGwSmStsHdlr.h"
#include "INGwStackManager/INGwSmBlkConfig.h"
#include "INGwInfraParamRepository/INGwIfrPrParamRepository.h"
#include <INGwFtPacket/INGwFtPktStackConfig.h>
#include <INGwFtPacket/INGwFtPktAspInActive.h>
#include <arpa/inet.h>
#include <INGwInfraManager/INGwIfrMgrManager.h>
#include "INGwTcapProvider/INGwTcapProvider.h"
#include "INGwStackManager/INGwSmAlmCodes.h"
#include "INGwInfraManager/INGwIfrMgrAlarmMgr.h"
#include "INGwInfraManager/INGwIfrMgrRoleMgr.h"


#if (defined (_BP_AIN_PROVIDER_) && !defined (STUBBED))
#include "INGwTcapProvider/INGwTcapIncMsgHandler.h"

#else
class INGwTcapProvider
{
};
#endif

extern U16 selfProcId;
//extern int tcapLoDlgId;
//extern int tcapHiDlgId;

using namespace std;

#define CONFIG_QUEUE_LOW_MARK 50
#define CONFIG_QUEUE_DEQUEUE_TIME 0

int INGwSmWrapper::mpContext = 1;
int CCPU_LOG_MASK_INC_MSG  = 0x00000001;
int CCPU_LOG_MASK_RY       = 0x00000002;
int CCPU_LOG_MASK_SG       = 0x00000004;
int CCPU_LOG_MASK_SH       = 0x00000008;
int CCPU_LOG_MASK_MR       = 0x00000010;
int CCPU_LOG_MASK_MT       = 0x00000020;
int CCPU_LOG_MASK_SS       = 0x00000040;
int CCPU_LOG_MASK_SM       = 0x00000080;
int CCPU_LOG_MASK_CM       = 0x00000100;
int CCPU_LOG_MASK_TCAP     = 0x00000200;
int CCPU_LOG_MASK_SCCP     = 0x00000400;
int CCPU_LOG_MASK_PSF_TCAP = 0x00000800;
int CCPU_LOG_MASK_PSF_SCCP = 0x00001000;
int CCPU_LOG_MASK_MTP3     = 0x00002000;
int CCPU_LOG_MASK_LDF_MTP3 = 0x00004000;
int CCPU_LOG_MASK_PSF_MTP3 = 0x00008000;
int CCPU_LOG_MASK_MTP2     = 0x00010000;
int CCPU_LOG_MASK_M3UA     = 0x00020000;
int CCPU_LOG_MASK_LDF_M3UA = 0x00040000;
int CCPU_LOG_MASK_PSF_M3UA = 0x00080000;
int CCPU_LOG_MASK_SCTP     = 0x00100000;
int CCPU_LOG_MASK_TUCL     = 0x00200000;
int CCPU_LOG_MASK_MEM      = 0x00400000;

int g_GetMsgCountThrs = -1;
int g_PutMsgCountThrs = -1;
int g_EnableMBuffStore = 0;


const int   FAILURE_RESPONSE   = 1;
const char* FAILURE_STACK_RESP = "Request Timeout";
const char* FAILURE_STACK_LAYER= "Unknwon";
const int   FAILURE_REASON     = 1000;   // Dummy value

/******************************************************************************
*
*     Fun:   INGwSmWrapper()
*
*     Desc:  Default Contructor
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
INGwSmWrapper::INGwSmWrapper(INGwTcapProvider* apTcapProvider):
mpTcapProvider (apTcapProvider),
m_configQ(true, CONFIG_QUEUE_LOW_MARK*10, CONFIG_QUEUE_LOW_MARK)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::INGwSmWrapper");

  /*
   * Print the User id and the machine info
   */
  struct utsname leUname;
  uid_t leUid;
  gid_t leGid;
  uname (&leUname);
  leUid = geteuid();
  leGid = getegid();
  for(int i=0; i<20 ; i++)
  {
    debugArr[i]=0;
  }

  logger.logMsg (ALWAYS_FLAG, 0,
 "Uid<%d> Gid<%d> Sysname<%s> Nodename<%s> Release<%s> Version<%s> Machine<%s>",
    leUid, leGid, leUname.sysname, leUname.nodename, leUname.release, 
		leUname.version, leUname.machine);

  //initialize the mutexes etc
  pthread_mutex_init (&mMapLock, NULL);
  pthread_mutex_init (&mCvLock, NULL);
  pthread_cond_init  (&mUnblock, NULL);
  pthread_mutex_init (&assocMapLock, NULL);

  mpSmDistributor = 0;

  mRequestBlockingMap.clear();
  mUnblockedRequestId = -1;
  mpStsReq = NULL;

  memset(relayServerIp, 0, sizeof(relayServerIp));
  memset(relayClientIp, 0, sizeof(relayClientIp));

  m_allowCfgFromEms = false;
  pthread_rwlock_init (&m_AllowCfgEmsLock, 0);

  // sleep time and count is taken for waiting the ASP down alarm in case of FT
  char* alrmSleep = getenv("INGW_ALARM_WAIT_TIME"); //in millisec
  if(NULL != alrmSleep) {
    miSleepTime = atoi(alrmSleep);
    logger.logMsg(ALWAYS_FLAG,0,"INGW_ALARM_WAIT_TIME"
    " for waiting ASP down alarm: %d",miSleepTime);
  }
  else
    miSleepTime = 10; //10 millisec

  char* alrmCnt = getenv("INGW_ALARM_WAIT_CNT"); //Counter for wait
  if(NULL != alrmCnt) {
    miSleepCount = atoi(alrmCnt);
    logger.logMsg(ALWAYS_FLAG,0,"INGW_ALARM_WAIT_CNT"
    " wait count for ASP down alarm: %d",miSleepCount);
  }
  else
    miSleepCount = 5;


  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::INGwSmWrapper");
}


/******************************************************************************
*
*     Fun:   ~INGwSmWrapper()
*
*     Desc:  Default Destructor
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
INGwSmWrapper::~INGwSmWrapper()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::~INGwSmWrapper");

  if (mpSmDistributor)
    delete mpSmDistributor;

  mpSmDistributor = 0;

	mRunStatus = false;

  pthread_mutex_destroy (&mMapLock);
  pthread_mutex_destroy (&mCvLock);
  pthread_cond_destroy  (&mUnblock);
  pthread_mutex_destroy (&assocMapLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::~INGwSmWrapper");
}


/******************************************************************************
*
*     Fun:   startUp()
*
*     Desc:  AIN Provider needs to set the TAPA related functions here
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int
INGwSmWrapper::startUp (PAIFS16 apAinProvActvInit,
                            ActvTsk apAinProvActvTsk,
                            int aiAinProvSapId) 
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::startUp");

  if (mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Distributor object already exists");
    return -1;
  }

	if(0 != INGwIfrPrParamRepository::getInstance().
																			getValue("PROTO", m_protocol)) {
		m_protocol = "ITU"; // default
	}

	logger.logMsg(ALWAYS_FLAG, 0, "PROTO - set :%s", m_protocol);

	this->start(); // this is to instantiate proxyRun
	INGwSmBlkConfig::getInstance().initialize();

  /*
   * create a new Distributor object and invoked changeState to LOADED
   */
  mpSmDistributor = new INGwSmDistributor (this, mpTcapProvider);
  mpSmDistributor->start ();
  mpSmDistributor->initialize();

  if (mpSmDistributor->changeStateToLoaded (apAinProvActvInit, 
                              apAinProvActvTsk, aiAinProvSapId) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Change State to LOADED failed for Stack Manager ");
    return -1;
  }

  stack_log_mask = 0;
  char * lcStkLogMask = getenv("DEF_STK_LOG_MASK");
  if (lcStkLogMask) {
    int liStkLogMask = atoi(lcStkLogMask);
    stack_log_mask = liStkLogMask;
    logger.logMsg(ALWAYS_FLAG, 0, "startUp()::DEF_STK_LOG_MASK "
           "defined to <0x%X-%d> stack_log_mask<0x%X-%d>",
           liStkLogMask, liStkLogMask, stack_log_mask, stack_log_mask);
  }        
  else {   
    logger.logMsg(ALWAYS_FLAG, 0,
           "startUp()::EnvVar DEF_STK_LOG_MASK not defined. "
           "Setting to default value 0");
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::startUp");

  return 0;
}


/******************************************************************************
*
*     Fun:   changeState()
*
*     Desc:  to be invoked by TcapProvider for changeState
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int
INGwSmWrapper::changeState(const INGwIwfBaseProvider::ProviderStateType aiState)
{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,
              "Entering INGwSmWrapper::changeState");

  int liResult = BP_AIN_SM_OK;

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return BP_AIN_SM_FAIL;
  }

  switch (aiState)
  {
    case INGwIwfBaseProvider::PROVIDER_STATE_RUNNING :
    {
      /*
       * create a new Queue object and used the request Id as
       * the thread id for the current thread. This will be used by Stack
       * Manager to unblock it if needed.
       */
     
      INGwSmQueueMsg *lpQMsg = new INGwSmQueueMsg;
      int liRequestId = (int) pthread_self ();
      
      lpQMsg->mSrc = BP_AIN_SM_SRC_CCM;

      lpQMsg->t.ccmOper.mOpType 	  = BP_AIN_SM_CCMOP_CHANGESTATE;
      lpQMsg->t.ccmOper.miRequestId = liRequestId;
      lpQMsg->t.ccmOper.txnType 	  = GEN_CFG_TXN;

      lpQMsg->t.ccmOper.ccmOp.chngStInfo.mState = BP_AIN_SM_STATE_RUNNING;

      logger.logINGwMsg(false,ALWAYS_FLAG,0,"posting gencfg");
      //post the message to the Distributor
      if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "In change state to running, postMsg failed");
        delete lpQMsg;
        return BP_AIN_SM_FAIL;
      }

      //block the current thread
			int timeOut = 100;
      if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
      {
        /*
         * If the block operation was successful then remove the blocking
         * context from the Map and check the return value for success
         * and set the corres. result for CCM
         */
        INGwSmBlockingContext *lpContext = 0;
        if (removeBlockingContext (liRequestId, lpContext) == BP_AIN_SM_OK)
        {
          if (lpContext->returnValue != BP_AIN_SM_OK) {
            logger.logMsg (ERROR_FLAG, 0, "Failed to RemoveBlockingContext. "
                   "CntxtRetVal != OK");
            liResult = BP_AIN_SM_FAIL;
        	}
          delete lpContext;
        }
        else
        {
          logger.logMsg (ERROR_FLAG, 0, "Failed to RemoveBlockingContext. "
                 "RetVal != OK");
          liResult = BP_AIN_SM_FAIL;
        }
      }
      else {
        logger.logMsg (ERROR_FLAG, 0, "BlockOperation Failed");
        liResult = BP_AIN_SM_FAIL;
			}

      //INGwTcapIncMsgHandler::getInstance().initCallMap(tcapLoDlgId,tcapHiDlgId);
			INGwSmBlkConfig::getInstance().initializeStack();

      break;
    }
    case INGwIwfBaseProvider::PROVIDER_STATE_STOPPED :
    {
      /*
       * create a new Queue object and used the request Id as
       * the thread id for the current thread. This will be used by Stack
       * Manager to unblock it if needed.
       */
      INGwSmQueueMsg *lpQMsg = new INGwSmQueueMsg;
      int liRequestId = (int) pthread_self ();
      
      lpQMsg->mSrc = BP_AIN_SM_SRC_CCM;
      
      lpQMsg->t.ccmOper.mOpType = BP_AIN_SM_CCMOP_CHANGESTATE;
      lpQMsg->t.ccmOper.miRequestId = liRequestId;
      
      lpQMsg->t.ccmOper.ccmOp.chngStInfo.mState = BP_AIN_SM_STATE_STOPPED;
      
      //post the message to the Distributor
      if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "In change state to stopped, postMsg failed");
        delete lpQMsg;
        return BP_AIN_SM_FAIL;
      }
      
      //block the current thread
			int timeOut = 100;
      if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
      {
        INGwSmBlockingContext *lpContext = 0;
        if (removeBlockingContext (liRequestId, lpContext))
        {
          if (lpContext->returnValue != BP_AIN_SM_OK) {
            logger.logMsg (ERROR_FLAG, 0, "Failed to RemoveBlockingContext. "
                   "CntxtRetVal != OK");
            liResult = BP_AIN_SM_FAIL;
        	}   

          delete lpContext;
        }   
        else
        {
          logger.logMsg (ERROR_FLAG, 0, "Failed to RemoveBlockingContext. "
                 "RetVal != OK");
          liResult = BP_AIN_SM_FAIL;
        } 
      } 
      else 
      {
        logger.logMsg (ERROR_FLAG, 0, "BlockOperation Failed");
        liResult = BP_AIN_SM_FAIL;
      }
        
      break;
    }
  }

 logger.logINGwMsg(false,ALWAYS_FLAG,0,
         "Leaving INGwSmWrapper::changeState");
  return liResult;
}

//starts timer for stats and start sending stsReq to Stack
int 
INGwSmWrapper::startSendStsReq()
{
  logger.logMsg (TRACE_FLAG, 0,
         "Entering INGwSmWrapper::startSendStsReq");

  mpSmDistributor->initToRunning();

  logger.logMsg (TRACE_FLAG, 0,
         "Leaving INGwSmWrapper::startSendStsReq");
  return BP_AIN_SM_OK;
}


//Reading Oids for statistics
int 
INGwSmWrapper::initializeStats()
{
  logger.logMsg (TRACE_FLAG, 0,
         "Entering INGwSmWrapper::initializeStats");
  
  //get the transaction id used for statistics object
  int transId = mpSmDistributor->getSmRepository()->getStsTransId ();

  if (mpStsReq == 0)
  {
    mpStsReq = new INGwSmStsHdlr (mpSmDistributor,0);

    mpStsReq->initialize (transId);
  }

  logger.logMsg (TRACE_FLAG, 0,
         "Leaving INGwSmWrapper::initializeStats");
  return BP_AIN_SM_OK;
}

int 
INGwSmWrapper::initializeNode(vector<int> &procIdList, int aSpStIt)
{
  Ss7SigtranSubsReq *req = new Ss7SigtranSubsReq;
  SgNode *enaNode;
  Ss7SigtranStackResp *lpStackResp = NULL;
  cmMemset ((U8*)req, 0, sizeof (Ss7SigtranSubsReq));

  int liResult = BP_AIN_SM_OK;

  int transportType = mpSmDistributor->getSmRepository()->getTransportType();
  
  logger.logMsg (ALWAYS_FLAG, 0, 
       "initializeNode()::Waiting for Relay Est. Sleep 5 secs...");
	sleep(5);

  if (aSpStIt != 2) {
    std::ostringstream msg1;
    msg1 << " ENABLE_NODE SG Starts. SG Role :" <<
      INGwTcapProvider::getInstance().getAinSilTxRef().getSelfSgRole().c_str();
	  g_dumpMsg("SmWrapper", __LINE__, msg1);
	  logger.logMsg (ALWAYS_FLAG, 0,
        "initializeNode()::Sending request for ENABLE_NODE SG");

	  bool done = false;

	  //if(procIdList.size() == 2 || (procIdList.size() == 1 && procIdList[0] ==
	  //			INGwSmBlkConfig::getInstance().m_selfProcId))
	  //{
	  	req->cmd_type = ENABLE_NODE;
    	enaNode 		  = &(req->u.sgNode);
    	cmMemset((U8 *)enaNode, 0, sizeof(SgNode));
    	enaNode->entId = ENTSG;
    	liResult 			 = cliEnableNode(enaNode, &lpStackResp, procIdList);
	  	done = true;

			if(lpStackResp != NULL)
			{
				delete lpStackResp;
				lpStackResp = NULL;
			}
	  //}
      
    if((TRANSPORT_TYPE_MTP == transportType) || 
        (TRANSPORT_TYPE_BOTH == transportType)) 
	  {
	  	logger.logMsg (ALWAYS_FLAG, 0,
          "initializeNode()::Sending request for ENABLE_NODE MTP3");

        req->cmd_type = ENABLE_NODE;
        enaNode 		  = &(req->u.sgNode);
        cmMemset((U8 *)enaNode, 0, sizeof(SgNode));
        enaNode->entId = ENT_INC_MTP2_MTP3;
        liResult 			 = cliEnableNode(enaNode, &lpStackResp, procIdList);

				if(lpStackResp != NULL)
				{
					delete lpStackResp;
					lpStackResp = NULL;
				}

	  		g_dumpMsg("SmWrapper", __LINE__, "ENABLE_NODE MTP3 done");
	  }

	  logger.logMsg (ALWAYS_FLAG, 0,
        "initializeNode()::Sending request for ENABLE_NODE SCCP_TCAP");

    req->cmd_type = ENABLE_NODE;
    enaNode = &(req->u.sgNode);
    cmMemset((U8 *)enaNode, 0, sizeof(SgNode));
    enaNode->entId = ENT_INC_SCCP_TCAP;
    liResult = cliEnableNode(enaNode, &lpStackResp, procIdList);

		if(lpStackResp != NULL)
		{
			delete lpStackResp;
			lpStackResp = NULL;
		}

	  g_dumpMsg("SmWrapper", __LINE__, "ENABLE_NODE SCCP_TCAP done");
  }

  if ((aSpStIt == 2) || (aSpStIt == 0)) {
    if((TRANSPORT_TYPE_SIGTRAN == transportType) || 
       (TRANSPORT_TYPE_BOTH == transportType)) {

      logger.logMsg (ALWAYS_FLAG, 0,
        "initializeNode()::Sending request for ENABLE_NODE M3UA");

      req->cmd_type = ENABLE_NODE;
      enaNode = &(req->u.sgNode);
      cmMemset((U8 *)enaNode, 0, sizeof(SgNode));
      enaNode->entId = ENT_INC_M3UA;
      liResult = cliEnableNode(enaNode, &lpStackResp, procIdList);

			if(lpStackResp != NULL)
			{
				delete lpStackResp;
				lpStackResp = NULL;
			}

	  	g_dumpMsg("SmWrapper", __LINE__, "ENABLE_NODE M3UA done");
    }
  }

	//if(done == false)
	//{
	//	req->cmd_type = ENABLE_NODE;
  //	enaNode 		  = &(req->u.sgNode);
  //	cmMemset((U8 *)enaNode, 0, sizeof(SgNode));
  //	enaNode->entId = ENTSG;
  //	liResult 			 = cliEnableNode(enaNode, &lpStackResp, procIdList);
	//}
	delete req;    

  std::ostringstream msg;
  msg << " ENABLE_NODE SG Ends. SG Role :" <<
      INGwTcapProvider::getInstance().getAinSilTxRef().getSelfSgRole().c_str();

	g_dumpMsg("SmWrapper", __LINE__, msg);

  return liResult;
}


INGwSmWrapper::enaSgtrnLyrs(vector<int> &procIdList, int action)
{
  Ss7SigtranSubsReq *req = new Ss7SigtranSubsReq;
  SgNode *enaNode;
  Ss7SigtranStackResp *lpStackResp = NULL;
  cmMemset ((U8*)req, 0, sizeof (Ss7SigtranSubsReq));

  int liResult = BP_AIN_SM_OK;

  logger.logMsg (ALWAYS_FLAG, 0, "enaSgtrnLyrs()::Sleep 3 secs...");
	sleep(3);

  logger.logMsg (ALWAYS_FLAG, 0,
    "enaSgtrnLyrs()::Sending request for ENABLE_NODE TUCL & SCTP");

  req->cmd_type = ENABLE_NODE;
  enaNode = &(req->u.sgNode);
  cmMemset((U8 *)enaNode, 0, sizeof(SgNode));
  enaNode->entId = ENT_INC_SCTP_TUCL;
  liResult = cliEnableNode(enaNode, &lpStackResp, procIdList);

	if(lpStackResp != NULL)
	{
    INGwSmBlkConfig::getInstance().setSctpTuclEnabled(true);
		delete lpStackResp;
		lpStackResp = NULL;
	}

  logger.logMsg (ALWAYS_FLAG, 0, 
         "enaSgtrnLyrs(:):Sleep 3 secs. to let TUCL & SCTP enabled...");
  sleep(3);
  
	g_dumpMsg("enaSgtrnLyrs():", __LINE__, 
            "ENABLE_NODE TUCL & SCTP done");
  

  logger.logMsg (ALWAYS_FLAG, 0,
    "enaSgtrnLyrs()::Sending request for ENABLE_NODE M3UA");

  cmMemset ((U8*)req, 0, sizeof (Ss7SigtranSubsReq));

  req->cmd_type = ENABLE_NODE;
  enaNode = &(req->u.sgNode);
  cmMemset((U8 *)enaNode, 0, sizeof(SgNode));
  enaNode->entId = ENT_INC_M3UA;
  liResult = cliEnableNode(enaNode, &lpStackResp, procIdList);

	if(lpStackResp != NULL)
	{
		delete lpStackResp;
		lpStackResp = NULL;
	}

	g_dumpMsg("enaSgtrnLyrs()", __LINE__, "ENABLE_NODE M3UA done");

	delete req;    

	g_dumpMsg("enaSgtrnLyrs()", __LINE__, "ENABLE_NODE SG Ends");

  return liResult;
}


void INGwSmWrapper::setStkLogMask(char *Layer)
{

  logger.logMsg (ALWAYS_FLAG, 0, "Entering setStkLogMask()::with layer mask:%s",Layer);

  char layer[10] = {'\0',};
  strncpy(layer,Layer,strlen(Layer));

  if(!strcmp(layer,"MSG_FLOW"))
    stack_log_mask |= CCPU_LOG_MASK_INC_MSG;
  else if(!strcmp(layer,"RY"))
    stack_log_mask |= CCPU_LOG_MASK_RY;
  else if(!strcmp(layer,"SG"))
    stack_log_mask |= CCPU_LOG_MASK_SG;
  else if(!strcmp(layer,"SH"))
    stack_log_mask |= CCPU_LOG_MASK_SH;
  else if(!strcmp(layer,"MR"))
    stack_log_mask |= CCPU_LOG_MASK_MR;
  else if(!strcmp(layer,"MT"))
    stack_log_mask |= CCPU_LOG_MASK_MT;
  else if(!strcmp(layer,"SS"))
    stack_log_mask |= CCPU_LOG_MASK_SS;
  else if(!strcmp(layer,"SM"))
    stack_log_mask |= CCPU_LOG_MASK_SM;
  else if(!strcmp(layer,"CM"))
    stack_log_mask |= CCPU_LOG_MASK_CM;
  else if(!strcmp(layer,"TCAP"))
    stack_log_mask |= CCPU_LOG_MASK_TCAP;
  else if(!strcmp(layer,"SCCP"))
    stack_log_mask |= CCPU_LOG_MASK_SCCP;
  else if(!strcmp(layer,"PSF_TCAP"))
     stack_log_mask |= CCPU_LOG_MASK_PSF_TCAP;
  else if(!strcmp(layer,"PSF_SCCP"))
     stack_log_mask |= CCPU_LOG_MASK_PSF_SCCP;
  else if(!strcmp(layer,"MTP3"))
     stack_log_mask |= CCPU_LOG_MASK_MTP3;
  else if(!strcmp(layer,"LDF_MTP3"))
     stack_log_mask |= CCPU_LOG_MASK_LDF_MTP3;
  else if(!strcmp(layer,"PSF_MTP3"))
     stack_log_mask |= CCPU_LOG_MASK_PSF_MTP3;
  else if(!strcmp(layer,"MTP2"))
     stack_log_mask |= CCPU_LOG_MASK_MTP2;
  else if(!strcmp(layer,"M3UA"))
     stack_log_mask |= CCPU_LOG_MASK_M3UA;
  else if(!strcmp(layer,"LDF_M3UA"))
     stack_log_mask |= CCPU_LOG_MASK_LDF_M3UA;
  else if(!strcmp(layer,"PSF_M3UA"))
     stack_log_mask |= CCPU_LOG_MASK_PSF_M3UA;
  else if(!strcmp(layer,"SCTP"))
     stack_log_mask |= CCPU_LOG_MASK_SCTP;
  else if(!strcmp(layer,"TUCL"))
     stack_log_mask |= CCPU_LOG_MASK_TUCL;
  else if(!strcmp(layer,"MEM"))
     stack_log_mask |= CCPU_LOG_MASK_MEM;
  else if(!strcmp(layer,"RESET"))
     stack_log_mask = 0;
  else
    logger.logMsg (TRACE_FLAG, 0, "setStkLogMask():Invalid Input");  

  logger.logMsg (ALWAYS_FLAG, 0, "Leaving setStkLogMask()::stack_log_mask:%d",stack_log_mask);
}


void INGwSmWrapper::getStkLogMask(std::ostrstream &layerName)
{

  logger.logMsg (ALWAYS_FLAG, 0, "Entering getStkLogMask()::stack_log_mask:%d",stack_log_mask);

  bool i = false;

  if(stack_log_mask & CCPU_LOG_MASK_INC_MSG){
    layerName << ((i)?"|":"") ;
    layerName << "MSG_FLOW ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_RY){
    layerName << ((i)?"|":"") ;
    layerName << "RY ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_SG) {
    layerName << ((i)?"|":"") ;
    layerName << "SG ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_SH) {
    layerName << ((i)?"|":"") ;
    layerName << "SH ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_MR) {
    layerName << ((i)?"|":"") ;
    layerName << "MR ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_MT) {
    layerName << ((i)?"|":"") ;
    layerName << "MT ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_SS) {
    layerName << ((i)?"|":"") ;
    layerName << "SS ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_SM) {
    layerName << ((i)?"|":"") ;
    layerName << "SM ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_CM) {
    layerName << ((i)?"|":"") ;
    layerName << "CM ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_TCAP) {
    layerName << ((i)?"|":""); 
    layerName << "TCAP ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_SCCP) {
    layerName << ((i)?"|":"" );
    layerName << "SCCP ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_PSF_TCAP) {
    layerName << ((i)?"|":"" );
    layerName << "PSF_TCAP ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_PSF_SCCP) {
    layerName << ((i)?"|":"" );
    layerName << "PSF_SCCP ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_MTP3) {
    layerName << ((i)?"|":"" );
    layerName << "MTP3 ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_LDF_MTP3) {
    layerName << ((i)?"|":"" );
    layerName << "LDF_MTP3 ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_PSF_MTP3) {
    layerName << ((i)?"|":"" );
    layerName << "PSF_MTP3 ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_MTP2) {
    layerName << ((i)?"|":"" );
    layerName << "MTP2 ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_M3UA) {
    layerName << ((i)?"|":"" );
    layerName << "M3UA ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_LDF_M3UA) {
    layerName << ((i)?"|":"" );
    layerName << "LDF_M3UA ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_PSF_M3UA) {
    layerName << ((i)?"|":"" );
    layerName << "PSF_M3UA ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_SCTP) {
    layerName << ((i)?"|":"" );
    layerName << "SCTP ";
    i = true;
  }

  if(stack_log_mask & CCPU_LOG_MASK_MEM) {
    layerName << ((i)?"|":"" );
    layerName << "MEM ";
    i = true;
  }


  if(stack_log_mask & CCPU_LOG_MASK_TUCL) {
    layerName << ((i)?"|":"") ;
    layerName << "TUCL ";
    i = true;
  }
    
  layerName << "\n";

  logger.logMsg (ALWAYS_FLAG, 0, "Leaving getStkLogMask()::stack_log_mask:%d",stack_log_mask);
}

void INGwSmWrapper::getStatsMask(std::ostrstream &dbgLayer)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::getStatsMask");

  int selfRole = mpSmDistributor->getTcapProvider()->myRole();
  
  if (selfRole == 1) { 
    logger.logMsg (TRACE_FLAG, 0,
           "getStatsMask(),Self role ACTIVE, getting Stats-Mask");

    getStsHdlrInst()->getStatisticsMask(dbgLayer);

  }
  else {
    logger.logMsg (TRACE_FLAG, 0,
           "getStatsMask(), Self role STANDBY, not getting Statistics Mask.");
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::getStatsMask");
}




void INGwSmWrapper::getStackDbgMask(std::ostrstream &dbgLayer)
{

  logger.logMsg (ALWAYS_FLAG, 0, "Entering getStackDbgMask()");

  bool i = false;

  if(debugArr[LYR_TCAP] == 1){
    dbgLayer << ((i)?"|":"") ;
    dbgLayer << "TCAP ";
    i = true;
  }

  if(debugArr[LYR_PSF_TCAP] == 1){
    dbgLayer << ((i)?"|":"") ;
    dbgLayer << "PSF_TCAP ";
    i = true;
  }

  if(debugArr[LYR_SCCP] == 1){
    dbgLayer << ((i)?"|":"") ;
    dbgLayer << "SCCP ";
    i = true;
  }

  if(debugArr[LYR_PSF_SCCP] == 1){
    dbgLayer << ((i)?"|":"") ;
    dbgLayer << "PSF_SCCP ";
    i = true;
  }

  if(debugArr[LYR_M3UA] == 1){
    dbgLayer << ((i)?"|":"") ;
    dbgLayer << "M3UA ";
    i = true;
  }

  if(debugArr[LYR_PSF_M3UA] == 1){
    dbgLayer << ((i)?"|":"") ;
    dbgLayer << "PSF_M3UA ";
    i = true;
  }

  if(debugArr[LYR_LDF_M3UA] == 1){
    dbgLayer << ((i)?"|":"") ;
    dbgLayer << "LDF_M3UA ";
    i = true;
  }

  if(debugArr[LYR_SCTP] == 1){
    dbgLayer << ((i)?"|":"") ;
    dbgLayer << "SCTP ";
    i = true;
  }

  if(debugArr[LYR_TUCL] == 1){
    dbgLayer << ((i)?"|":"") ;
    dbgLayer << "TUCL ";
    i = true;
  }

  if(debugArr[LYR_MTP3] == 1){
    dbgLayer << ((i)?"|":""); 
    dbgLayer << "MTP3 ";
    i = true;
  }

  if(debugArr[LYR_PSF_MTP3] == 1){
    dbgLayer << ((i)?"|":"" );
    dbgLayer << "PSF_MTP3 ";
    i = true;
  }

  if(debugArr[LYR_LDF_MTP3] == 1){
    dbgLayer << ((i)?"|":"" );
    dbgLayer << "LDF_MTP3 ";
    i = true;
  }

  if(debugArr[LYR_MTP2] == 1){
    dbgLayer << ((i)?"|":"" );
    dbgLayer << "MTP2 ";
    i = true;
  }

  if(debugArr[LYR_RELAY] == 1){
    dbgLayer << ((i)?"|":"" );
    dbgLayer << "RELAY ";
    i = true;
  }

  if(debugArr[LYR_SG] == 1){
    dbgLayer << ((i)?"|":"" );
    dbgLayer << "SG ";
    i = true;
  }

  if(debugArr[LYR_MR] == 1){
    dbgLayer << ((i)?"|":"" );
    dbgLayer << "MR ";
    i = true;
  }

  if(debugArr[LYR_SH] == 1){
    dbgLayer << ((i)?"|":"" );
    dbgLayer << "SH ";
    i = true;
  }

    
  dbgLayer << "\n";

  logger.logMsg (ALWAYS_FLAG, 0, "Leaving getStackDbgMask()");
}



int
INGwSmWrapper::initializeAlarms(vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::initializeAlarms");

  Ss7SigtranSubsReq  *req = new Ss7SigtranSubsReq;
  DebugEnableDisable *dbg;
  AlarmEnableDisable *alrm;
  cmMemset ((U8*)req, 0, sizeof (Ss7SigtranSubsReq));

  int liResult = BP_AIN_SM_OK;
  int transportType = mpSmDistributor->getSmRepository()->getTransportType();

#if 0
	logger.logMsg (TRACE_FLAG, 0,
     "initializeAlarms()::Sending request for ENABLE_ALARM for TCAP Layer");

	printf("[+INC+] %s:%d initializeAlarms()::Sending request for "
		" ENABLE_ALARM -> TCAP Layer\n", __FILE__, __LINE__);

	req->cmd_type = ENABLE_ALARM;
  alrm = &(req->u.alarm);
  cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
  alrm->Layer = BP_AIN_SM_TCA_LAYER;
  liResult = cliEnaAlarm(alrm, procIdList);
#endif
    
  logger.logMsg (TRACE_FLAG, 0,
   "initializeAlarms()::Sending request for ENABLE_ALARM for SCCP Layer");

  printf("[+INC+] %s:%d initializeAlarms()::Sending request for ENABLE_ALARM"
	" -> SCCP Layer\n", __FILE__, __LINE__);

  req->cmd_type = ENABLE_ALARM;
  alrm = &(req->u.alarm);
  cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
  alrm->Layer = BP_AIN_SM_SCC_LAYER;
  liResult = cliEnaAlarm(alrm, procIdList);
  

  if((TRANSPORT_TYPE_SIGTRAN == transportType) || 
		 (TRANSPORT_TYPE_BOTH == transportType)) 
	{
		logger.logMsg (TRACE_FLAG, 0,
       "changeState()::Sending request for ENABLE_ALARM for M3UA Layer");

    printf("[+INC+] %s:%d changeState()::Sending request for "
			" ENABLE_ALARM -> M3UA Layer\n", __FILE__, __LINE__);

    req->cmd_type = ENABLE_ALARM;
    alrm = &(req->u.alarm);
    cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
    alrm->Layer = BP_AIN_SM_M3U_LAYER;
    liResult = cliEnaAlarm(alrm, procIdList);

    sleep(2);
		logger.logMsg (TRACE_FLAG, 0,
       "changeState()::Sending request for ENABLE_ALARM for SCTP Layer");

    printf("[+INC+] %s:%d changeState()::Sending request for "
			" ENABLE_ALARM -> SCTP Layer\n", __FILE__, __LINE__);

    req->cmd_type = ENABLE_ALARM;
    alrm = &(req->u.alarm);
    cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
    alrm->Layer = BP_AIN_SM_SCT_LAYER;
    liResult = cliEnaAlarm(alrm, procIdList);
  }

 
  if((TRANSPORT_TYPE_MTP == transportType) || 
		 (TRANSPORT_TYPE_BOTH == transportType)) 
	{
		logger.logMsg (TRACE_FLAG, 0,
       "initializeAlarms()::Sending request for ENABLE_ALARM for MTP3 Layer");

    	printf("[+INC+] %s:%d initializeAlarms()::Sending request for "
			" ENABLE_ALARM -> MTP3 Layer\n", __FILE__, __LINE__);

      req->cmd_type = ENABLE_ALARM;
      alrm = &(req->u.alarm);
      cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
      alrm->Layer = BP_AIN_SM_MTP3_LAYER;
      liResult = cliEnaAlarm(alrm, procIdList);

      logger.logMsg (TRACE_FLAG, 0,
       "initializeAlarms()::Sending request for ENABLE_ALARM for MTP2 Layer");

    	printf("[+INC+] %s:%d initializeAlarms()::Sending request for "
			"ENABLE_ALARM -> MTP2 Layer\n", __FILE__, __LINE__);

      req->cmd_type = ENABLE_ALARM;
      alrm = &(req->u.alarm);
      cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
      alrm->Layer = BP_AIN_SM_MTP2_LAYER;
      liResult = cliEnaAlarm(alrm, procIdList);

      logger.logMsg (TRACE_FLAG, 0,
       "initializeAlarms()::Sending request for ENABLE_ALARM for LDF-MTP3 Layer");

    	printf("[+INC+] %s:%d initializeAlarms()::Sending request for "
			"ENABLE_ALARM -> LDF-MTP3 Layer\n", __FILE__, __LINE__);

      req->cmd_type = ENABLE_ALARM;
      alrm = &(req->u.alarm);
      cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
      alrm->Layer = BP_AIN_SM_LDF_MTP3_LAYER;
      liResult = cliEnaAlarm(alrm, procIdList);

      logger.logMsg (TRACE_FLAG, 0,
       "initializeAlarms()::Sending request for ENABLE_ALARM for PSF-MTP3 Layer");

    	printf("[+INC+] %s:%d initializeAlarms()::Sending request for "
			"ENABLE_ALARM -> PSF-MTP3 Layer\n", __FILE__, __LINE__);

      req->cmd_type = ENABLE_ALARM;
      alrm = &(req->u.alarm);
      cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
      alrm->Layer = BP_AIN_SM_PSF_MTP3_LAYER;
      liResult = cliEnaAlarm(alrm, procIdList);
	}

#if 0
	logger.logMsg (TRACE_FLAG, 0,
     "initializeAlarms()::Sending request for ENABLE_ALARM for PSF-TCAP Layer");

	printf("[+INC+] %s:%d initializeAlarms()::Sending request for "
		"ENABLE_ALARM -> PSF-TCAP Layer\n", __FILE__, __LINE__);

	req->cmd_type = ENABLE_ALARM;
  alrm = &(req->u.alarm);
  cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
  alrm->Layer = BP_AIN_SM_PSF_TCAP_LAYER;
  liResult = cliEnaAlarm(alrm, procIdList);
#endif

  logger.logMsg (TRACE_FLAG, 0,
    "initializeAlarms()::Sending request for ENABLE_ALARM for PSF-SCCP Layer");

  printf("[+INC+] %s:%d initializeAlarms()::Sending request for "
	"ENABLE_ALARM -> PSF-SCCP Layer\n", __FILE__, __LINE__);

  req->cmd_type = ENABLE_ALARM;
  alrm = &(req->u.alarm);
  cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
  alrm->Layer = BP_AIN_SM_PSF_SCCP_LAYER;
  liResult = cliEnaAlarm(alrm, procIdList);

	delete req;
  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::initializeAlarms");

  
  logger.logMsg (TRACE_FLAG, 0, "Enabling DEBUG information starts");
  int dbgMask = 0, val=0;

  char * lcStkDbgMask = getenv("DEF_DBG_MASK");
  if (lcStkDbgMask) {
    int liStkDbgMask = atoi(lcStkDbgMask);
    dbgMask = liStkDbgMask;
    logger.logINGwMsg(false, ALWAYS_FLAG, 0, "startUp()::DEF_DBG_MASK "
           "defined to <0x%X-%d> dbgMask<0x%X-%d>",
           liStkDbgMask, liStkDbgMask, dbgMask, dbgMask);
  }        
  else {   
    logger.logINGwMsg(false, ALWAYS_FLAG, 0,
           "startUp()::EnvVar DEF_DBG_MASK not defined. "
           "Setting to default value 0");
  }


  for(int j = 1 ; j<20 ; j++)
  {
    val = dbgMask & 0x0001;  //65536 is 17 times 1 , to check that first bit is set or not 
    debugArr[j] = val;
    dbgMask = dbgMask >> 1;
  } 
 

      if(debugArr[LYR_TCAP] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "TCAP", strlen("TCAP"));
        enableDisableDebug(cmd, layer, 7);
      } 
      if(debugArr[LYR_PSF_TCAP] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "PSF_TCAP", strlen("PSF_TCAP"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_SCCP] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "SCCP", strlen("SCCP"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_PSF_SCCP] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "PSF_SCCP", strlen("PSF_SCCP"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_M3UA] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "M3UA", strlen("M3UA"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_PSF_M3UA] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "PSF_M3UA", strlen("PSF_M3UA"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_LDF_M3UA] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "LDF_M3UA", strlen("LDF_M3UA"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_SCTP] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "SCTP", strlen("SCTP"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_TUCL] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "TUCL", strlen("TUCL"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_MTP3] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "MTP3", strlen("MTP3"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_PSF_MTP3] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "PSF_MTP3", strlen("PSF_MTP3"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_LDF_MTP3] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "LDF_MTP3", strlen("LDF_MTP3"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_MTP2] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "MTP2", strlen("MTP2"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_RELAY] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "RELAY", strlen("RELAY"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_SG] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "SG", strlen("SG"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_MR] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "MR", strlen("MR"));
        enableDisableDebug(cmd, layer, 7); 
      }
      if(debugArr[LYR_SH] == 1){
        char *cmd = new char[10];
        char *layer = new char[10];
        memset(cmd , '\0', 10);
        memset(layer , '\0', 10);
        strncpy(cmd , "ENABLE", strlen("ENABLE"));
        strncpy(layer, "SH", strlen("SH"));
        enableDisableDebug(cmd, layer, 7); 
      }
 
  logger.logMsg (TRACE_FLAG, 0, "Enabling DEBUG information ends");
  return liResult;
}

int
INGwSmWrapper::stub()
{
  int liResult = BP_AIN_SM_OK;
  Ss7SigtranSubsReq *req = new Ss7SigtranSubsReq;
  SgNode *enaNode;
  AddEndPoint *ep;
  DebugEnableDisable *dbg;
  AlarmEnableDisable *alrm;
  
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ADD_NETWORK ");
  req->cmd_type = ADD_NETWORK;
  liResult = processEmsReq(req);

#if 0
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for DEL_NETWORK ");
  req->cmd_type = DEL_NETWORK;
  liResult = processEmsReq(req);
#endif

  
#if 1 
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ADD_LINKSET");
  req->cmd_type = ADD_LINKSET;
  liResult = processEmsReq(req);
#endif

#if 0
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for MODIFY_LINKSET");
  req->cmd_type = MODIFY_LINKSET;
  liResult = processEmsReq(req);
#endif
 
#if 0
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for DEL_LINKSET");
  req->cmd_type = DEL_LINKSET;
  liResult = processEmsReq(req);
#endif

#if 1
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_NODE SG");
  req->cmd_type = ENABLE_NODE;
  enaNode = &(req->u.sgNode);
  //enaNode->procId = mpSmDistributor->getTcapProvider()->selfProcId;
  enaNode->procId = selfProcId;
  enaNode->lastProc = TRUE;
  enaNode->entId = ENTSG;
  liResult = processEmsReq(req);
#endif 

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_DEBUG");
  req->cmd_type = ENABLE_DEBUG;
  dbg = &(req->u.debug);
  cmMemset((U8 *)dbg, 0, sizeof(DebugEnableDisable));
  dbg->layer = BP_AIN_SM_SCC_LAYER;
  dbg->level = 7;
  liResult = processEmsReq(req);


  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_DEBUG for Layer M3UA");
  req->cmd_type = ENABLE_DEBUG;
  dbg = &(req->u.debug);
  cmMemset((U8 *)dbg, 0, sizeof(DebugEnableDisable));
  dbg->layer = BP_AIN_SM_M3U_LAYER;
  dbg->level = 7;
  liResult = processEmsReq(req);



  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_DEBUG");
  req->cmd_type = ENABLE_DEBUG;
  dbg = &(req->u.debug);
  cmMemset((U8 *)dbg, 0, sizeof(DebugEnableDisable));
  dbg->layer = BP_AIN_SM_MTP3_LAYER;
  dbg->level = 7;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_DEBUG");
  req->cmd_type = ENABLE_DEBUG;
  dbg = &(req->u.debug);
  cmMemset((U8 *)dbg, 0, sizeof(DebugEnableDisable));
  dbg->layer = BP_AIN_SM_MTP2_LAYER;
  dbg->level = 7;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_DEBUG");
  req->cmd_type = ENABLE_DEBUG;
  dbg = &(req->u.debug);
  cmMemset((U8 *)dbg, 0, sizeof(DebugEnableDisable));
  dbg->layer = BP_AIN_SM_MR_LAYER;
  dbg->level = 7;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_DEBUG");
  req->cmd_type = ENABLE_DEBUG;
  dbg = &(req->u.debug);
  cmMemset((U8 *)dbg, 0, sizeof(DebugEnableDisable));
  dbg->layer = BP_AIN_SM_SH_LAYER;
  dbg->level = 7;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_DEBUG");
  req->cmd_type = ENABLE_DEBUG;
  dbg = &(req->u.debug);
  cmMemset((U8 *)dbg, 0, sizeof(DebugEnableDisable));
  dbg->layer = BP_AIN_SM_LDF_MTP3_LAYER;
  dbg->level = 7;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_DEBUG");
  req->cmd_type = ENABLE_DEBUG;
  dbg = &(req->u.debug);
  cmMemset((U8 *)dbg, 0, sizeof(DebugEnableDisable));
  dbg->layer = BP_AIN_SM_PSF_MTP3_LAYER;
  dbg->level = 7;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_DEBUG");
  req->cmd_type = ENABLE_DEBUG;
  dbg = &(req->u.debug);
  cmMemset((U8 *)dbg, 0, sizeof(DebugEnableDisable));
  dbg->layer = BP_AIN_SM_PSF_SCCP_LAYER;
  dbg->level = 7;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_DEBUG");
  req->cmd_type = ENABLE_DEBUG;
  dbg = &(req->u.debug);
  cmMemset((U8 *)dbg, 0, sizeof(DebugEnableDisable));
  dbg->layer = BP_AIN_SM_PSF_TCAP_LAYER;
  dbg->level = 7;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ADD_LINK");
  req->cmd_type = ADD_LINK;
  liResult = processEmsReq(req);

#if 1
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ADD_USERPART");
  req->cmd_type = ADD_USERPART;
  liResult = processEmsReq(req);
#endif

  /* Route cfg*/
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ADD_ROUTE");
  req->cmd_type = ADD_ROUTE;
  AddRoute *route = &(req->u.addRoute);
  route->dpc = 120;
  route->spType= LSN_TYPE_SP;
  route->cmbLnkSetId= 2;
  route->dir= LSN_RTE_DN;
  route->rteToAdjSp= TRUE;

  /* for sccp*/
  route->status= SP_ADJACENT;
  route->nmbBpc= 0;
  //route->bpcList[0]= ;
  route->nmbSsns= 1;
  route->ssnList[0].ssn = 146;
  route->ssnList[0].status = SS_INACC;
  route->ssnList[0].nmbBpc = 0;
  route->ssnList[0].nmbConPc = 0;
#if (SS7_ANS96 || SS7_BELL05)
  route->replicatedMode= DOMINANT;
#endif
  route->preferredOpc= 121;
  route->nSapId= SCCP_USERPART_SAPID;

	if(m_protocol == "ITU") {
  	route->swtchType= LSN_SW_ITU; // MTP3 Protocol Variant
  	route->upSwtch  = LSN_SW_ITU; // MTP3 Protocol Variant
  	route->swtch    = LSP_SW_ITU92;// SCCP Protocol Variant
    route->ssf= SSF_NAT;
	}
	else if(m_protocol == "NTT") {
  	route->swtchType= LSN_SW_NTT;
  	route->upSwtch  = LSN_SW_NTT;
  	route->swtch    = LSP_SW_JAPAN;
    route->ssf      = SSF_TTC;
	}
	else if(m_protocol == "ANSI") {
  	route->swtchType= LSN_SW_ANS;
  	route->upSwtch  = LSN_SW_ANS;
  	route->swtch    = LSP_SW_ANS96;
	} 
 
  liResult = processEmsReq(req);


#if 0
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for DEL_REMOTE_SSN");
  req->cmd_type = DEL_REMOTE_SSN;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for DEL_ROUTE");
  req->cmd_type = DEL_ROUTE;
  liResult = processEmsReq(req);
#endif


  /* self Route cfg*/
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for self ADD_ROUTE");
  req->cmd_type = ADD_ROUTE;
  route = &(req->u.addRoute);
  route->dpc = 121;
  route->spType= LSN_TYPE_SP;
  route->cmbLnkSetId= 1;
  route->dir= LSN_RTE_UP;
  route->rteToAdjSp= FALSE;
  route->ssf= SSF_NAT;
  /* for sccp*/
  route->status= SP_TRANS;
  route->nmbBpc= 0;
  //route->bpcList[0]= ;
  route->nmbSsns= 0;
  //route->ssnList= ;
#if (SS7_ANS96 || SS7_BELL05)
  route->replicatedMode= DOMINANT;
#endif
  route->preferredOpc= 0;
  route->nSapId= SCCP_USERPART_SAPID;

	if(m_protocol == "ITU") {
  	route->swtchType= LSN_SW_ITU; // MTP3 Protocol Variant
  	route->upSwtch  = LSN_SW_ITU; // MTP3 Protocol Variant
  	route->swtch    = LSP_SW_ITU92;// SCCP Protocol Variant
	}
	else if(m_protocol == "NTT") {
  	route->swtchType= LSN_SW_NTT;
  	route->upSwtch  = LSN_SW_NTT;
  	route->swtch    = LSP_SW_JAPAN;
    route->ssf      = SSF_TTC;
	}
	else if(m_protocol == "ANSI") {
  	route->swtchType= LSN_SW_ANS;
  	route->upSwtch  = LSN_SW_ANS;
  	route->swtch    = LSP_SW_ANS96;
	} 
  liResult = processEmsReq(req);
 
#if 0
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ADD_LINK");
  req->cmd_type = ADD_LINK;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for MODIFY_LINK");
  req->cmd_type = MODIFY_LINK;
  AddLink *lnk = &(req->u.lnk);
  //lnk->mtp2ProcId= mpSmDistributor->getTcapProvider()->selfProcId;
  lnk->mtp2ProcId= selfProcId;
  liResult = processEmsReq(req);
 
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for DEL_LINKSET");
  req->cmd_type = DEL_LINKSET;
  liResult = processEmsReq(req);
#endif


#if 0 
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for STA_LINK");
  req->cmd_type = STA_LINK;
  liResult = processEmsReq(req);
 
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for STA_LINKSET");
  req->cmd_type = STA_LINKSET;
  liResult = processEmsReq(req);

 
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for STA_ROUTE");
  req->cmd_type = STA_ROUTE;
  liResult = processEmsReq(req);
#endif

#if 0 
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for DEL_LINK");
  req->cmd_type = DEL_LINK;
  liResult = processEmsReq(req);
#endif

#if 0
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ADD_ASP");
  req->cmd_type = ADD_ASP;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ADD_AS");
  req->cmd_type = ADD_AS;
  liResult = processEmsReq(req);
#endif


#if 0
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for MODIFY_AS");
  req->cmd_type = MODIFY_AS;
  liResult = processEmsReq(req);
#endif


#if 0
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for STA_PS");
  req->cmd_type = STA_PS;
  liResult = processEmsReq(req);


  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for STA_PSP");
  req->cmd_type = STA_PSP;
  liResult = processEmsReq(req);
#endif

#if 0
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for STA_NODE");
  req->cmd_type = STA_NODE;
  liResult = processEmsReq(req);
#endif


#if 0 
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for DEL_ASP");
  req->cmd_type = DEL_ASP;
  liResult = processEmsReq(req);
#endif

#if 0 
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for DEL_AS");
  req->cmd_type = DEL_AS;
  liResult = processEmsReq(req);
#endif


#if 0
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ADD_USERPART");
  req->cmd_type = ADD_USERPART;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for DEL_USERPART");
  req->cmd_type = DEL_USR_PART;
  liResult = processEmsReq(req);
#endif

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_LINK");
  LinkEnable *enaLink = &(req->u.lnkEnable);
  enaLink->lnkId = 1; 
  enaLink->lnkSetId = 1; 
  //enaLink->procId = mpSmDistributor->getTcapProvider()->selfProcId;
  enaLink->procId = selfProcId;
  enaLink->mtp2UsapId = 0; 
  enaLink->mtp3LsapId = 0; 
  req->cmd_type = ENABLE_LINK;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_USERPART");
  EnableUserPart *enaUP = &(req->u.enableUserPart);
  enaUP->mtp3UsapId= 0; 
  enaUP->m3uaUsapId= 0; 
  enaUP->sccpLsapId= 0; 
  enaUP->nwkType= 0;// MTP 
  req->cmd_type = ENABLE_USERPART;
  liResult = processEmsReq(req);

  // In order to test ENABLE_LOCAL_SSN work we also need
  // to execute the command for ADD_NETWORK, ADD_LOCAL_SSN
  logger.logMsg (TRACE_FLAG, 0,
         "Sending request for ADD_LOCAL_SSN");
  req->cmd_type = ADD_LOCAL_SSN;
  AddLocalSsn *ssn = &(req->u.addLocalSsn);
  cmMemset((U8 *)ssn, 0, sizeof(AddLocalSsn));
  liResult = processEmsReq(req);

#if 0
  logger.logMsg (TRACE_FLAG, 0,
         "Sending request for DEL_LOCAL_SSN");
  req->cmd_type = DEL_LOCAL_SSN;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ENABLE_NODE MTP");
  req->cmd_type = ENABLE_NODE;
  
  enaNode = &(req->u.sgNode);
  //enaNode->procId = mpSmDistributor->getTcapProvider()->selfProcId;
  enaNode->procId = selfProcId;
  enaNode->lastProc = TRUE;
  enaNode->entId = 0;
  liResult = processEmsReq(req);
#endif

#if 0
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ADD_ENDPOINT");
  req->cmd_type = ADD_ENDPOINT;
  ep = &(req->u.addEp);
  ep->endPointid = 1;
  ep->srcPort= 2905;
  ep->nmbAddrs= 1;
  ep->nAddr[0].type = CM_NETADDR_IPV4;
  ep->nAddr[0].u.ipv4NetAddr = ntohl(inet_addr((S8*)"10.32.10.11"));
  //ep->sctpProcId= mpSmDistributor->getTcapProvider()->selfProcId;
  ep->sctpProcId= selfProcId;
  ep->sctpLsapId= 0;
  ep->sctpUsapId= 0;
  ep->m3uaLsapId= 0;
  ep->tuclUsapId= 0;
 
  liResult = processEmsReq(req);
#endif

#if 0  
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for DEL_ENDPOINT");
  req->cmd_type = DEL_ENDPOINT;
  liResult = processEmsReq(req);
#endif

#if 0
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ASP_UP");
  req->cmd_type = ASP_UP;
  liResult = processEmsReq(req);
  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ASP_ACTIVE");
  req->cmd_type = ASP_ACTIVE;
  liResult = processEmsReq(req);

  logger.logMsg (TRACE_FLAG, 0,
    "Sending request for ASSOC_UP");
  req->cmd_type = ASSOC_UP;
  liResult = processEmsReq(req);
#endif


#if 1
  logger.logMsg (TRACE_FLAG, 0,
         "Sending request for ENABLE_LOCAL_SSN");
  req->cmd_type = ENABLE_LOCAL_SSN;
  SsnEnable *lSsn = &(req->u.ssnEnable);
  cmMemset((U8 *)lSsn, 0, sizeof(SsnEnable));
  liResult = processEmsReq(req);
#endif

#if 0
  logger.logMsg (TRACE_FLAG, 0,
         "Sending request for ADD_NETWORK");
  req->cmd_type = ADD_NETWORK;
  liResult = processEmsReq(req);
  logger.logMsg (TRACE_FLAG, 0,
         "Sending request for ADD_GTRULE");
  req->cmd_type = ADD_GTRULE;
  liResult = processEmsReq(req);
  logger.logMsg (TRACE_FLAG, 0,
         "Sending request for ADD_GTADDRMAP");
  req->cmd_type = ADD_GTADDRMAP;
  liResult = processEmsReq(req);
  /*logger.logMsg (TRACE_FLAG, 0,
         "Sending second request for ADD_GTADDRMAP");
  req->cmd_type = ADD_GTADDRMAP;
  liResult = processEmsReq(req);*/
  logger.logMsg (TRACE_FLAG, 0,
         "Sending request for DEL_GTADDRMAP");
  req->cmd_type = DEL_GTADDRMAP;
  liResult = processEmsReq(req);
  logger.logMsg (TRACE_FLAG, 0,
         "Sending request for DEL_GTRULE");
  req->cmd_type = DEL_GTRULE;
  liResult = processEmsReq(req);
#endif 

  logger.logMsg (TRACE_FLAG, 0,
         "Leaving INGwSmWrapper::stub");
  return liResult;
}



//getting this statistics mask from telnet interface
int INGwSmWrapper::setStatsMask(long int statsMask)
{

  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::setStatsMask <%x>",statsMask);

  int liResult = BP_AIN_SM_OK;

  //Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();

  //memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

  int selfRole = mpSmDistributor->getTcapProvider()->myRole();
  
  if (selfRole == 1) { 
    // Do the hardcoded SS7 and SIGTRAN configuration only if it is ACTIVE node
    logger.logMsg (TRACE_FLAG, 0,
           "setStatsMask(),Self role ACTIVE, setting Mask <%lu> for statistics",statsMask);

    getStsHdlrInst()->setStatisticsMask(statsMask);

  }
  else {
    logger.logMsg (TRACE_FLAG, 0,
           "setStatsMask(), Self role STANDBY, not setting Statistics Mask.");
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::setStatsMask");
  return BP_AIN_SM_OK;
}


int INGwSmWrapper::enableDisableDebug(char *telCmd, char *telLayer, U32 dbgMask)
{

  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::enableDisableDebug");

  char cmd[10] = {'\0',};
  char layer[10] = {'\0',};
  strncpy(cmd, telCmd, strlen(telCmd));
  strncpy(layer, telLayer, strlen(telLayer));

  delete [] telCmd;
  //delete telCmd;
  telCmd = NULL;
  delete [] telLayer;
  //delete telLayer;
  telLayer = NULL;
  
  int liResult = BP_AIN_SM_OK;

  int selfRole = mpSmDistributor->getTcapProvider()->myRole();
  
  if (selfRole == 1) { 
    // Do the hardcoded SS7 and SIGTRAN configuration only if it is ACTIVE node
    logger.logMsg (ALWAYS_FLAG, 0,
           "enableDisableDebug(),Self role ACTIVE, Enabling/Disabling Debug level.cmd <%s> layer <%s> dbgMask <%d>",cmd,layer,dbgMask);

    int cmdType = 0, ret, i;
    vector<int> lprocIdList;
    lprocIdList.push_back(SFndProcId());

    int transportType = mpSmDistributor->getSmRepository()->getTransportType();

    if(strncasecmp( cmd, "ENABLE" , strlen("ENABLE")) == 0) 
    {
      cmdType = 1;
    }
    else if(strncasecmp( cmd, "DISABLE" , strlen("DISABLE")) == 0)
    {
      cmdType = 2;
    }
    else
    {
      //returning without doing anything
      return BP_AIN_SM_FAIL;
    }


    if((strncasecmp( layer, "TCAP" , strlen("TCAP")) == 0) ||  
        (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {
     
        logger.logMsg (ALWAYS_FLAG, 0,
          "enableDisableDebug()::Sending request for DEBUG <%d> for BP_AIN_SM_TCA_LAYER",cmdType);
        debugArr[LYR_TCAP] = 1;

        Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
        memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

        if( cmdType == 1){
          debugArr[LYR_TCAP] = 1;
          reqDebug->cmd_type = ENABLE_DEBUG;
        }
        else{
          debugArr[LYR_TCAP] = 0;
          reqDebug->cmd_type = DISABLE_DEBUG;
        }

        reqDebug->u.debug.level = dbgMask;
        reqDebug->u.debug.layer = BP_AIN_SM_TCA_LAYER;
        INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = reqDebug;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 2;
        postMsg(lqueueMsg,true);

    }


    if((strncasecmp( layer, "PSF_TCAP" , strlen("PSF_TCAP")) == 0) ||  
        (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {
     
        logger.logMsg (ALWAYS_FLAG, 0,
          "enableDisableDebug()::Sending request for DEBUG <%d> for BP_AIN_SM_PSF_TCAP_LAYER",cmdType);
        debugArr[LYR_PSF_TCAP] = 1;

        Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
        memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

        if( cmdType == 1){
          debugArr[LYR_PSF_TCAP] = 1;
          reqDebug->cmd_type = ENABLE_DEBUG;
        }
        else{
          debugArr[LYR_PSF_TCAP] = 0;
          reqDebug->cmd_type = DISABLE_DEBUG;
        }

        reqDebug->u.debug.level = dbgMask;
        reqDebug->u.debug.layer = BP_AIN_SM_PSF_TCAP_LAYER;
        INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = reqDebug;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 2;
        postMsg(lqueueMsg,true);

    }

    if((strncasecmp( layer, "SCCP" , strlen("SCCP")) == 0)||  
          (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {

        logger.logMsg (ALWAYS_FLAG, 0,
          "enableDisableDebug()::Sending request for DEBUG <%d> for BP_AIN_SM_SCC_LAYER",cmdType);
        debugArr[LYR_SCCP] = 1;

        Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
        memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

        if( cmdType == 1){
          debugArr[LYR_SCCP] = 1;
          reqDebug->cmd_type = ENABLE_DEBUG;
        }
        else{
          debugArr[LYR_SCCP] = 0;
          reqDebug->cmd_type = DISABLE_DEBUG;
        }

        reqDebug->u.debug.level = dbgMask;
        reqDebug->u.debug.layer = BP_AIN_SM_SCC_LAYER;
        INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = reqDebug;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 2;
        postMsg(lqueueMsg,true);

    }

    if((strncasecmp( layer, "PSF_SCCP" , strlen("PSF_SCCP")) == 0) ||  
        (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {

        logger.logMsg (ALWAYS_FLAG, 0,
          "enableDisableDebug()::Sending request for DEBUG <%d> for BP_AIN_SM_PSF_SCCP_LAYER",cmdType);
        debugArr[LYR_PSF_SCCP] = 1;
        Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
        memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

        if( cmdType == 1){
          debugArr[LYR_PSF_SCCP] = 1;
          reqDebug->cmd_type = ENABLE_DEBUG;
        }
        else{
          debugArr[LYR_PSF_SCCP] = 0;
          reqDebug->cmd_type = DISABLE_DEBUG;
        }

        reqDebug->u.debug.level = dbgMask;
        reqDebug->u.debug.layer = BP_AIN_SM_PSF_SCCP_LAYER;
        INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = reqDebug;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 2;
        postMsg(lqueueMsg,true);

    }


      if((TRANSPORT_TYPE_MTP == transportType) || 
         (TRANSPORT_TYPE_BOTH == transportType)) {

        if((strncasecmp( layer, "MTP3" , strlen("MTP3")) == 0) ||  
            (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {

            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableDebug()::Sending request for DEBUG <%d> for BP_AIN_SM_MTP3_LAYER",cmdType);
            debugArr[LYR_MTP3] = 1;

            Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
            memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

            if( cmdType == 1){
              debugArr[LYR_MTP3] = 1;
              reqDebug->cmd_type = ENABLE_DEBUG;
            }
            else{
              debugArr[LYR_MTP3] = 0;
              reqDebug->cmd_type = DISABLE_DEBUG;
            }

            reqDebug->u.debug.level = dbgMask;
            reqDebug->u.debug.layer = BP_AIN_SM_MTP3_LAYER;
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;
            postMsg(lqueueMsg,true);

        } 
      
        if((strncasecmp( layer, "MTP2" , strlen("MTP2")) == 0) ||  
            (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {

            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableDebug()::Sending request for DEBUG <%d> for BP_AIN_SM_MTP2_LAYER",cmdType);
            debugArr[LYR_MTP2] = 1;

            Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
            memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

            if( cmdType == 1){
              debugArr[LYR_MTP2] = 1;
              reqDebug->cmd_type = ENABLE_DEBUG;
            }
            else{
              debugArr[LYR_MTP2] = 0;
              reqDebug->cmd_type = DISABLE_DEBUG;
            }

            reqDebug->u.debug.level = dbgMask;
            reqDebug->u.debug.layer = BP_AIN_SM_MTP2_LAYER;
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;
            postMsg(lqueueMsg,true);


        }

        if((strncasecmp( layer, "PSF_MTP3" , strlen("PSF_MTP3")) == 0) ||  
            (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {

            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableDebug()::Sending request for DEBUG <%d> for BP_AIN_SM_PSF_MTP3_LAYER",cmdType);
            debugArr[LYR_PSF_MTP3] = 1;
      
            Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
            memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

            if( cmdType == 1){
              debugArr[LYR_PSF_MTP3] = 1;
              reqDebug->cmd_type = ENABLE_DEBUG;
            }
            else{
              debugArr[LYR_PSF_MTP3] = 0;
              reqDebug->cmd_type = DISABLE_DEBUG;
            }

            reqDebug->u.debug.level = dbgMask;
            reqDebug->u.debug.layer = BP_AIN_SM_PSF_MTP3_LAYER;
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;
            postMsg(lqueueMsg,true);


        }

        if((strncasecmp( layer, "LDF_MTP3" , strlen("LDF_MTP3")) == 0) ||  
            (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {

            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableDebug()::Sending request for DEBUG <%d> for BP_AIN_SM_LDF_MTP3_LAYER",cmdType);
            debugArr[LYR_LDF_MTP3] = 1;
    
            Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
            memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

            if( cmdType == 1){
              debugArr[LYR_LDF_MTP3] = 1;
              reqDebug->cmd_type = ENABLE_DEBUG;
            }
            else{
              debugArr[LYR_LDF_MTP3] = 0;
              reqDebug->cmd_type = DISABLE_DEBUG;
            }

            reqDebug->u.debug.level = dbgMask;
            reqDebug->u.debug.layer = BP_AIN_SM_LDF_MTP3_LAYER;
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;
            postMsg(lqueueMsg,true);


        }


      }

      if((TRANSPORT_TYPE_SIGTRAN == transportType) || 
        (TRANSPORT_TYPE_BOTH == transportType)) {

        if((strncasecmp( layer, "M3UA" , strlen("M3UA")) == 0) ||  
            (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {

            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableDebug()::Sending request for DEBUG <%d> for M3UA",cmdType);
            debugArr[LYR_M3UA] = 1;

            Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
            memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

            if( cmdType == 1){
              debugArr[LYR_M3UA] = 1;
              reqDebug->cmd_type = ENABLE_DEBUG;
            }
            else{
              debugArr[LYR_M3UA] = 0;
              reqDebug->cmd_type = DISABLE_DEBUG;
            }

            reqDebug->u.debug.level = dbgMask;
            reqDebug->u.debug.layer = BP_AIN_SM_M3U_LAYER;
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;
            postMsg(lqueueMsg,true);

        }
        
        if((strncasecmp( layer, "PSF_M3UA" , strlen("PSF_M3UA")) == 0) ||  
            (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {

            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableDebug()::Sending request for DEBUG <%d> for PSF_M3UA",cmdType);
            debugArr[LYR_PSF_M3UA] = 1;
  
            Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
            memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

            if( cmdType == 1){
              debugArr[LYR_PSF_M3UA] = 1;
              reqDebug->cmd_type = ENABLE_DEBUG;
            }
            else{
              debugArr[LYR_PSF_M3UA] = 0;
              reqDebug->cmd_type = DISABLE_DEBUG;
            }

            reqDebug->u.debug.level = dbgMask;
            reqDebug->u.debug.layer = BP_AIN_SM_PSF_M3UA_LAYER;
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;
            postMsg(lqueueMsg,true);

        }

        if((strncasecmp( layer, "LDF_M3UA" , strlen("LDF_M3UA")) == 0) ||  
            (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)){

            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableDebug()::Sending request for DEBUG <%d> for LDF_M3UA",cmdType);
            debugArr[LYR_LDF_M3UA] = 1;

            Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
            memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

            if( cmdType == 1){
              debugArr[LYR_LDF_M3UA] = 1;
              reqDebug->cmd_type = ENABLE_DEBUG;
            }
            else{
              debugArr[LYR_LDF_M3UA] = 0;
              reqDebug->cmd_type = DISABLE_DEBUG;
            }

            reqDebug->u.debug.level = dbgMask;
            reqDebug->u.debug.layer = BP_AIN_SM_LDF_M3UA_LAYER;
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;
            postMsg(lqueueMsg,true);

        }
 
        if((strncasecmp( layer, "SCTP" , strlen("SCTP")) == 0) || 
           (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)){

            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableDebug()::Sending request for DEBUG <%d> for SCTP",cmdType);
            debugArr[LYR_SCTP] = 1;

            Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
            memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

            if( cmdType == 1){
              debugArr[LYR_SCTP] = 1;
              reqDebug->cmd_type = ENABLE_DEBUG;
            }
            else{
              debugArr[LYR_SCTP] = 0;
              reqDebug->cmd_type = DISABLE_DEBUG;
            }

            reqDebug->u.debug.level = dbgMask;
            reqDebug->u.debug.layer = BP_AIN_SM_SCT_LAYER;
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;
            postMsg(lqueueMsg,true);

        }


        if((strncasecmp( layer, "TUCL" , strlen("TUCL")) == 0) ||  
            (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {

            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableDebug()::Sending request for DEBUG <%d> for TUCL",cmdType);
            debugArr[LYR_TUCL] = 1;

            Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
            memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

            if( cmdType == 1){
              debugArr[LYR_TUCL] = 1;
              reqDebug->cmd_type = ENABLE_DEBUG;
            }
            else{
              debugArr[LYR_TUCL] = 0;
              reqDebug->cmd_type = DISABLE_DEBUG;
            }

            reqDebug->u.debug.level = dbgMask;
            reqDebug->u.debug.layer = BP_AIN_SM_TUC_LAYER;
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;
            postMsg(lqueueMsg,true);

        }
      }

      if((strncasecmp( layer, "RELAY" , strlen("RELAY")) == 0) ||  
          (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)){

      
        logger.logMsg (ALWAYS_FLAG, 0,
          "enableDisableDebug()::Sending request for DEBUG <%d> for BP_AIN_SM_RY_LAYER",cmdType);
        debugArr[LYR_RELAY] = 1;
  
        Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
        memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

        if( cmdType == 1){
          debugArr[LYR_RELAY] = 1;
          reqDebug->cmd_type = ENABLE_DEBUG;
        }
        else{
          debugArr[LYR_RELAY] = 0;
          reqDebug->cmd_type = DISABLE_DEBUG;
        }

        reqDebug->u.debug.level = dbgMask;
        reqDebug->u.debug.layer = BP_AIN_SM_RY_LAYER;
        INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = reqDebug;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 2;
        postMsg(lqueueMsg,true);

      }

    if((strncasecmp( layer, "SG" , strlen("SG")) == 0) ||  
        (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {

        logger.logMsg (ALWAYS_FLAG, 0,
          "enableDisableDebug()::Sending request for DEBUG <%d> for BP_AIN_SM_SG_LAYER",cmdType);
        debugArr[LYR_SG] = 1;

        Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
        memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

        if( cmdType == 1){
          debugArr[LYR_SG] = 1;
          reqDebug->cmd_type = ENABLE_DEBUG;
        }
        else{
          debugArr[LYR_SG] = 0;
          reqDebug->cmd_type = DISABLE_DEBUG;
        }

        reqDebug->u.debug.level = dbgMask;
        reqDebug->u.debug.layer = BP_AIN_SM_SG_LAYER;
        INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = reqDebug;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 2;
        postMsg(lqueueMsg,true);

    }


    if((strncasecmp( layer, "MR" , strlen("MR")) == 0) ||  
        (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {

        logger.logMsg (ALWAYS_FLAG, 0,
          "enableDisableDebug()::Sending request for DEBUG <%d> for BP_AIN_SM_MR_LAYER",cmdType);
        debugArr[LYR_MR] = 1;

        Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
        memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

        if( cmdType == 1){
          debugArr[LYR_MR] = 1;
          reqDebug->cmd_type = ENABLE_DEBUG;
        }
        else{
          debugArr[LYR_MR] = 0;
          reqDebug->cmd_type = DISABLE_DEBUG;
        }

        reqDebug->u.debug.level = dbgMask;
        reqDebug->u.debug.layer = BP_AIN_SM_MR_LAYER;
        INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = reqDebug;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 2;
        postMsg(lqueueMsg,true);

    }

    if((strncasecmp( layer, "SH" , strlen("SH")) == 0) ||  
        (strncasecmp( layer, "ALL" , strlen("ALL")) == 0)) {

        logger.logMsg (ALWAYS_FLAG, 0,
          "enableDisableDebug()::Sending request for DEBUG <%d> for BP_AIN_SM_SH_LAYER",cmdType);
        debugArr[LYR_SH] = 1;

        Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();
        memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

        if( cmdType == 1){
          debugArr[LYR_SH] = 1;
          reqDebug->cmd_type = ENABLE_DEBUG;
        }
        else{
          debugArr[LYR_SH] = 0;
          reqDebug->cmd_type = DISABLE_DEBUG;
        }

        reqDebug->u.debug.level = dbgMask;
        reqDebug->u.debug.layer = BP_AIN_SM_SH_LAYER;
        INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = reqDebug;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 2;
        postMsg(lqueueMsg,true);

    }




      memset(layer, '\0',strlen(layer));

  }
  else {
    logger.logMsg (TRACE_FLAG, 0,
           "enableDisableDebug(), Self role STANDBY, not performing SS7 and SIGTRAN configurations.");
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::enableDisableDebug");
  return BP_AIN_SM_OK;
}

int INGwSmWrapper::enableDisableTrace(char *telCmd, char *telLayer, U32 dbgMask)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::enableDisableTrace");

  char cmd[10] = {'\0',};
  char layer[10] = {'\0',};
  strncpy(cmd, telCmd, strlen(telCmd));
  strncpy(layer, telLayer, strlen(telLayer));

  delete telCmd;
  telCmd = 0;
  delete telLayer;
  telLayer = 0;

  int liResult = BP_AIN_SM_OK;

  Ss7SigtranSubsReq *reqDebug = new Ss7SigtranSubsReq();

  memset ((void *)reqDebug, 0, sizeof (Ss7SigtranSubsReq));

  int selfRole = mpSmDistributor->getTcapProvider()->myRole();
  
  if (selfRole == 1) { 
    // Do the hardcoded SS7 and SIGTRAN configuration only if it is ACTIVE node
    logger.logMsg (ALWAYS_FLAG, 0,
           "enableDisableTrace(),Self role ACTIVE, Enabling/Disabling Debug level.cmd <%s> layer <%s> dbgMask <%d>",cmd,layer,dbgMask);

    int cmdType = 0, ret=0, i;
    vector<int> lprocIdList;

    int transportType = mpSmDistributor->getSmRepository()->getTransportType();

    if(strncasecmp( cmd, "ENABLE" , strlen("ENABLE")) == 0) 
    {
      reqDebug->cmd_type = ENABLE_TRACE;
      cmdType = 1;
    }
    else if(strncasecmp( cmd, "DISABLE" , strlen("DISABLE")) == 0)
    {
      reqDebug->cmd_type = DISABLE_TRACE;
      cmdType = 2;
    }
    else
    {
      //returning without doing anything
      return BP_AIN_SM_FAIL;
    }

    
    if(strncasecmp( layer, "TCAP" , strlen("TCAP")) == 0) {
     
      //ret = getDebugState(layerId); 
      if( (0 == ret) || ((cmdType == 1) && (1 != ret)) || ((cmdType == 2) && (2 != ret))) //0 -> not found in MAP, 1 -> Enable, 2 -> disable
      {
        logger.logMsg (ALWAYS_FLAG, 0,
          "enableDisableTrace()::Sending request for DEBUG <%d> for BP_AIN_SM_TCA_LAYER",cmdType);
        reqDebug->u.debug.layer = BP_AIN_SM_TCA_LAYER;
        lprocIdList.push_back(SFndProcId());
        INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = reqDebug;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 2;

        postMsg(lqueueMsg,true);

        //addDebugInfo ( layerId, cmdType );
       
      }
      else
      {
        logger.logMsg (ALWAYS_FLAG, 0,
          "enableDisableTrace()::Wrong cmdtype for TCAP layer, Layer may be already enabled/disabled");
      }
    }

    else if(strncasecmp( layer, "SCCP" , strlen("SCCP")) == 0) {

      //ret = getDebugState(layerId); 
      if( (0 == ret) || ((cmdType == 1) && (1 != ret)) || ((cmdType == 2) && (2 != ret))) //0 -> not found in MAP, 1 -> Enable, 2 -> disable
      {
        logger.logMsg (ALWAYS_FLAG, 0,
          "enableDisableTrace()::Sending request for DEBUG <%d> and NsapId <%d> for BP_AIN_SM_SCC_LAYER ",cmdType,dbgMask);

        UserPartSeq* upList = INGwSmBlkConfig::getInstance().getUserPartList();
        UserPartSeq::iterator it;
        for(it=upList->begin(); it != upList->end(); ++it) {
          if ((*it).userPartType == dbgMask) {
            reqDebug->u.trace.spNSapId = (*it).sccpLsapId;
            break;
          }
          else{
            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableTrace()::Wrong Usertype <%d> provided for Enable/Disable Trace cmd ",dbgMask);
          }
        }


        reqDebug->u.debug.layer = BP_AIN_SM_SCC_LAYER;
        lprocIdList.push_back(SFndProcId());
        INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = reqDebug;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 2;

        postMsg(lqueueMsg,true);

        //addDebugInfo ( layerId, cmdType );

      }
      else
      {
        logger.logMsg (ALWAYS_FLAG, 0,
          "enableDisableTrace()::Wrong cmdtype for SCCP layer, Layer may be already enabled/disabled");
      }
    }

      if((TRANSPORT_TYPE_MTP == transportType) || 
         (TRANSPORT_TYPE_BOTH == transportType)) {

        if(strncasecmp( layer, "MTP3" , strlen("MTP3")) == 0) {

          //ret = getDebugState(layerId); 
          if( (0 == ret) || ((cmdType == 1) && (1 != ret)) || ((cmdType == 2) && (2 != ret))) 
          {
            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableTrace()::Sending request for DEBUG <%d> for BP_AIN_SM_MTP3_LAYER",cmdType);

            /*LnkSeq* snList = blkCfg.getLinkList();
            LnkSeq::iterator it;
            for(it=snList->begin(); it != snList->end(); ++it) {
              if (strncmp((*it).lnkName,) {
                reqDebug->u.trace.snDLSap = (*it).Mtp3LsapId;
                break;
              }
              else{
                logger.logMsg (ALWAYS_FLAG, 0,
                  "enableDisableTrace()::Wrong LinkName <%d> provided for Enable/Disable Trace cmd ",dbgMask);
              }
            }*/

            reqDebug->u.debug.layer = BP_AIN_SM_MTP3_LAYER;
            reqDebug->u.trace.snDLSap = dbgMask;
            lprocIdList.push_back(SFndProcId());
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;

            postMsg(lqueueMsg,true);

            //addDebugInfo ( layerId, cmdType );
          }
          else
          {
            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableTrace()::Wrong cmdtype for MTP3 layer, Layer may be already enabled/disabled");
          }
        } 
      
        else if(strncasecmp( layer, "MTP2" , strlen("MTP2")) == 0) {

          //ret = getDebugState(layerId); 
          if( (0 == ret) || ((cmdType == 1) && (1 != ret)) || ((cmdType == 2) && (2 != ret))) //0 -> not found in MAP, 1 -> Enable, 2 -> disable 
          {
            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableTrace()::Sending request for DEBUG <%d> for BP_AIN_SM_MTP2_LAYER",cmdType);
            reqDebug->u.debug.layer = BP_AIN_SM_MTP2_LAYER;
            lprocIdList.push_back(SFndProcId());
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;

            postMsg(lqueueMsg,true);

            //addDebugInfo ( layerId, cmdType );
          }
          else
          {
            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableTrace()::Wrong cmdtype for MTP2 layer, Layer may be already enabled/disabled");
          }

        }

      }

      if((TRANSPORT_TYPE_SIGTRAN == transportType) || 
        (TRANSPORT_TYPE_BOTH == transportType)) {

        if(strncasecmp( layer, "M3UA" , strlen("M3UA")) == 0) {

          //ret = getDebugState(layerId); 
          if( (0 == ret) || ((cmdType == 1) && (1 != ret)) || ((cmdType == 2) && (2 != ret))) //0 -> not found in MAP, 1 -> Enable, 2 -> disable 
          {
            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableTrace()::Sending request for DEBUG <%d> for M3UA",cmdType);
            reqDebug->u.debug.layer = BP_AIN_SM_M3U_LAYER;
            reqDebug->u.trace.level = dbgMask;
            lprocIdList.push_back(SFndProcId());
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;

            postMsg(lqueueMsg,true);

            //addDebugInfo ( layerId, cmdType );
          }
          else
          {
            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableTrace()::Wrong cmdtype for M3UA layer, Layer may be already enabled/disabled");
          }
        }
 
        else if(strncasecmp( layer, "SCTP" , strlen("SCTP")) == 0) {

          //ret = getDebugState(layerId); 
          if( (0 == ret) || ((cmdType == 1) && (1 != ret)) || ((cmdType == 2) && (2 != ret))) //0 -> not found in MAP, 1 -> Enable, 2 -> disable 
          {
            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableTrace()::Sending request for DEBUG <%d> for SCTP",cmdType);
            reqDebug->u.debug.layer = BP_AIN_SM_SCT_LAYER;
            lprocIdList.push_back(SFndProcId());
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;

            postMsg(lqueueMsg,true);

            //addDebugInfo ( layerId, cmdType );
          }
          else
          {
            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableTrace()::Wrong cmdtype for SCTP layer, Layer may be already enabled/disabled");
          }

        }


        else if(strncasecmp( layer, "TUCL" , strlen("TUCL")) == 0) {

          //ret = getDebugState(layerId); 
          if( (0 == ret) || ((cmdType == 1) && (1 != ret)) || ((cmdType == 2) && (2 != ret))) //0 -> not found in MAP, 1 -> Enable, 2 -> disable 
          {
            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableTrace()::Sending request for DEBUG <%d> for TUCL",cmdType);
            reqDebug->u.debug.layer = BP_AIN_SM_TUC_LAYER;
            lprocIdList.push_back(SFndProcId());
            INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
            lqueueMsg->req = reqDebug;
            lqueueMsg->src = BP_AIN_SM_SRC_CCM;
            lqueueMsg->procIdList = lprocIdList;
            lqueueMsg->from       = 2;

            postMsg(lqueueMsg,true);

            //addDebugInfo ( layerId, cmdType );
          }
          else
          {
            logger.logMsg (ALWAYS_FLAG, 0,
              "enableDisableTrace()::Wrong cmdtype for TUCL layer, Layer may be already enabled/disabled");
          }
        }
      }
      memset(layer, '\0',strlen(layer));

  }
  else {
    logger.logMsg (TRACE_FLAG, 0,
           "enableDisableTrace(), Self role STANDBY, not performing SS7 and SIGTRAN configurations.");
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::enableDisableTrace");
  return BP_AIN_SM_OK;
}



void INGwSmWrapper::confSs7SigtranData()
{

  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::confSs7SigtranData");
  Ss7SigtranSubsReq *req = new Ss7SigtranSubsReq;
  cmMemset ((U8*)req, 0, sizeof (Ss7SigtranSubsReq));

  int selfRole = mpSmDistributor->getTcapProvider()->myRole();
  
  if (selfRole == 1) { 
    // Do the hardcoded SS7 and SIGTRAN configuration only if it is ACTIVE node
    logger.logMsg (TRACE_FLAG, 0,
           "Self role ACTIVE, performing hardcoded SS7 and SIGTRAN configurations.");

    int transportType = mpSmDistributor->getSmRepository()->getTransportType();

    if((TRANSPORT_TYPE_MTP == transportType) || 
       (TRANSPORT_TYPE_BOTH == transportType)) {
      addSs7Config(req);
    }

    if((TRANSPORT_TYPE_SIGTRAN == transportType) || 
       (TRANSPORT_TYPE_BOTH == transportType)) {
      addSigtranConfig(req);
    }
  }
  else {
    logger.logMsg (TRACE_FLAG, 0,
           "Self role STANDBY, not performing SS7 and SIGTRAN configurations.");
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::confSs7SigtranData");
  return;
}



int INGwSmWrapper::addSs7Config (Ss7SigtranSubsReq *req)
{
  logger.logMsg (ALWAYS_FLAG, 0,
         "Entering addSs7Config()");

  int liResult = BP_AIN_SM_OK;
  SgNode *enaNode;
  DebugEnableDisable *dbg;
  AlarmEnableDisable *alrm;
  U32 availmem;
  U16 procIdList[MAX_PROC_IDS];
  U8 numProc;
  numProc =  mpTcapProvider->getProcIdList(procIdList);
  vector<string> tsSltSlcVector;


  char * lpcIncAsSimulator = getenv("INC_AS_SIMULATOR");
  bool lbIncAsSimulator = false;
  if (lpcIncAsSimulator) {
    lbIncAsSimulator = (atoi(lpcIncAsSimulator) == 1)?true:false;
  }

  logger.logMsg (ALWAYS_FLAG, 0,
         "lbIncAsSimulator [%d]", lbIncAsSimulator);

  //logger.logMsg (ALWAYS_FLAG, 0, "addSs7Config()::Sleep 100 secs...");
  //sleep(100);

  // SRegInfoShow(0, &availmem);

  // ENABLE_NODE for ENTSG, ENT_INC_MTP2, ENT_INC_SCCP_TCAP 
  // is being done in changeState() method


  logger.logMsg (ALWAYS_FLAG, 0,
    "addSs7Config()::Sending request for ADD_NETWORK ");
  req->cmd_type = ADD_NETWORK;
  AddNetwork *nwk = &(req->u.addNwk);
  cmMemset((U8 *)nwk, 0, sizeof(AddNetwork));
  liResult = processEmsReq(req);

  logger.logMsg (ALWAYS_FLAG, 0,
    "addSs7Config()::Sending request for ADD_LINKSET");
  req->cmd_type = ADD_LINKSET;
  AddLinkSet *lnkSet = &(req->u.lnkSet);
  cmMemset((U8 *)lnkSet, 0, sizeof(AddLinkSet));
  liResult = processEmsReq(req);

  logger.logMsg (ALWAYS_FLAG, 0,
    "addSs7Config()::Sending request for ADD_LINK");
  req->cmd_type = ADD_LINK;
  AddLink *lnk = &(req->u.lnk);
  cmMemset((U8 *)lnk, 0, sizeof(AddLink));
  //lnk->mtp2ProcId= selfProcId;
  lnk->ssf = 2;
  lnk->lnkPrior = 0;
  lnk->mtp2UsapId = MTP2_LINK_USAPID;
  lnk->mtp3LsapId = MTP3_LINK_LSAPID;

  char *liTimeSlotSlCstr = getenv("INGW_TSLOT_SLC_PORT");
	if( NULL == liTimeSlotSlCstr){ 
    lnk->lnkId = 1;
    lnk->timeSlot = 0;
    lnk->physPort = 0;
    lnk->slc = 0;
    lnk->mtp2ProcId = selfProcId; 

    logger.logMsg (ALWAYS_FLAG, 0,
	  	"addSs7Config()::cmdType ADD_LINK, Adding one link only "
      "timeSlot <%d> phy port <%d> slc <%d> procId <%d>",lnk->timeSlot,
      lnk->physPort,lnk->slc,lnk->mtp2ProcId);

    liResult = processEmsReq(req);
  }
  else{
    string lsTimeSlotSlCstr(liTimeSlotSlCstr);
    printf("\n+VER+ TS PORT SLC String <%s>\n",lsTimeSlotSlCstr.c_str());
    g_tokenizeValue(lsTimeSlotSlCstr, G_PIPE_DELIM, tsSltSlcVector); 

    for(int i=0; i< tsSltSlcVector.size(); i++) 
    {
       lnk->mtp2UsapId = MTP2_LINK_USAPID + i;
       lnk->mtp3LsapId = MTP3_LINK_LSAPID + i;
       lnk->lnkId = i; 
       vector<string> fieldList;
       g_tokenizeValue(tsSltSlcVector[i], G_COMMA_DELIM, fieldList);
       printf("\n+VER+  TS, PHY_PORT, SLC [%d] [%s]\n",i,tsSltSlcVector[i].c_str());
       if(INGW_SS7_FIELD_SIZE != fieldList.size()) {
         printf("\n+VER+ Unequal fields %d \n",fieldList.size());
         return -1;
       }
       printf("\n+VER+ INGW_SS7_LINK_TIME_SLOT[%s]\n",fieldList[INGW_SS7_LINK_TIME_SLOT].c_str());
       printf("\n+VER+ INGW_SS7_PHY_PORT[%s]\n",fieldList[INGW_SS7_PHY_PORT].c_str());
       printf("\n+VER+ INGW_SS7_SLC [%s]\n",fieldList[INGW_SS7_SLC].c_str());
       printf("\n+VER+ INGW_SS7_MTP2_PROC_ID [%s]\n",fieldList[INGW_SS7_MTP2_PROC_ID].c_str());
       printf("\n+VER+ LINK ID [%d]\n",lnk->lnkId);
       printf("\n+VER+ MTP3LSAP_ID  [%d]\n",lnk->mtp3LsapId);

       lnk->timeSlot = atoi(fieldList[INGW_SS7_LINK_TIME_SLOT].c_str());
       lnk->physPort = atoi(fieldList[INGW_SS7_PHY_PORT].c_str()); 
       lnk->slc      = atoi(fieldList[INGW_SS7_SLC].c_str());
       U16 lProcId   = atoi(fieldList[INGW_SS7_MTP2_PROC_ID].c_str()); 
       linkIdProcIdMap[i] = lProcId;
       U16 procIdList[MAX_PROC_IDS];
       U8 numProc;
       numProc =  mpTcapProvider->getProcIdList(procIdList);
       bool isProcIdValid = false;
       for(int i=0;i<numProc;i++) {
         if(lProcId == procIdList[i]){
           isProcIdValid = true;
           break;
         }
       } 
       
       if(isProcIdValid) {
         logger.logMsg (ALWAYS_FLAG, 0,
           "addSs7Config()::cmdType ADD_LINK,+VER+"
           "ProcId <%d> is valid",lProcId);
         lnk->mtp2ProcId = lProcId;
         liResult = processEmsReq(req);
       }
       else{
         logger.logMsg (ERROR_FLAG, 0,
           "addSs7Config()::cmdType ADD_LINK,+VER+"
           "ProcId <%d> is Invalid",lProcId);
         return -1;
       }
       
    }
    logger.logMsg (ALWAYS_FLAG, 0,
	  	"addSs7Config()::cmdType ADD_LINK,+VER+ Added <%d> links. linkIdProcIdMap size[%d]",
       tsSltSlcVector.size(), linkIdProcIdMap.size());
  }


  logger.logMsg (ALWAYS_FLAG, 0,
    "addSs7Config()::Sending request for ADD_USERPART");
  req->cmd_type = ADD_USERPART;
  AddUserPart *up = &(req->u.addUserPart);
  cmMemset((U8 *)up, 0, sizeof(AddUserPart));
  up->userPartType = MTP3_USER;
  up->ssf = 2;
  up->mtp3UsapId = MTP3_USERPART_SAPID;
  up->nwId= 1;
  up->suType = 3; // LIT_SU_SCCP: 3
  up->m3uaUsapId = M3UA_USERPART_SAPID;
  up->sccpLsapId = SCCP_USERPART_SAPID;

	if(m_protocol == "ITU") {
    	up->lnkType = LSN_SW_ITU; // MTP2 Protocol Variant
			up->upSwtch = LSN_SW_ITU; // MTP3 Protocol Variant
	}
	else if(m_protocol == "NTT") {
     	up->lnkType = LSN_SW_NTT;
			up->upSwtch = LSN_SW_NTT;
      up->ssf     = SSF_TTC;
	}
	else if(m_protocol == "ANSI") {
     	up->lnkType = LSN_SW_ANS;
			up->upSwtch = LSN_SW_ANS;
	} 

  liResult = processEmsReq(req);
  

  /* Route cfg*/
  logger.logMsg (ALWAYS_FLAG, 0,
    "addSs7Config()::Sending request for ADD_ROUTE");
  req->cmd_type = ADD_ROUTE;
  AddRoute *route = &(req->u.addRoute);
  cmMemset((U8 *)route, 0, sizeof(AddRoute));
  //route->dpc = 120;
  if (lbIncAsSimulator == false) {
    route->dpc = 120;
  }
  else {
    route->dpc = 121;
  }
  route->spType= LSN_TYPE_SP;
  route->cmbLnkSetId= 2;
  route->dir= LSN_RTE_DN;
  route->rteToAdjSp= TRUE;
  route->ssf= SSF_NAT;
  
  /* for sccp*/
  route->status= SP_ADJACENT;
  route->nmbBpc= 0;
  //route->bpcList[0]= ;
  route->nmbSsns= 1;
  route->ssnList[0].ssn = 146;
  route->ssnList[0].status = SS_INACC;
  route->ssnList[0].nmbBpc = 0;
  route->ssnList[0].nmbConPc = 0;
#if (SS7_ANS96 || SS7_BELL05)
  route->replicatedMode= DOMINANT;
#endif
  //route->preferredOpc= 121;
  if (lbIncAsSimulator == false) {
    route->preferredOpc= 121;
  }
  else {
    route->preferredOpc= 120;
  }
  route->nSapId= SCCP_USERPART_SAPID;

	if(m_protocol == "ITU") {
  	route->swtchType= LSN_SW_ITU; // MTP3 Protocol Variant
  	route->upSwtch  = LSN_SW_ITU; // MTP3 Protocol Variant
  	route->swtch    = LSP_SW_ITU92;// SCCP Protocol Variant
	}
	else if(m_protocol == "NTT") {
  	route->swtchType= LSN_SW_NTT;
  	route->upSwtch  = LSN_SW_NTT;
  	route->swtch    = LSP_SW_JAPAN;
    route->ssf      = SSF_TTC;
	}
	else if(m_protocol == "ANSI") {
  	route->swtchType= LSN_SW_ANS;
  	route->upSwtch  = LSN_SW_ANS;
  	route->swtch    = LSP_SW_ANS96;
	} 
  
  liResult = processEmsReq(req);
  

  
  /* self Route cfg*/
  logger.logMsg (ALWAYS_FLAG, 0,
    "addSs7Config()::Sending request for self ADD_ROUTE");
  req->cmd_type = ADD_ROUTE;
  route = &(req->u.addRoute);
  cmMemset((U8 *)route, 0, sizeof(AddRoute));
  //route->dpc = 121;
  if (lbIncAsSimulator == false) {
    route->dpc = 121;
  }
  else {
    route->dpc = 120;
  }
  route->spType= LSN_TYPE_SP;
  route->cmbLnkSetId= 1;
  route->dir= LSN_RTE_UP;
  route->rteToAdjSp= FALSE;
  route->ssf= SSF_NAT;
  /* for sccp*/
  route->status= SP_TRANS;
  route->nmbBpc= 0;
  //route->bpcList[0]= ;
  route->nmbSsns= 0;
  //route->ssnList= ;
#if (SS7_ANS96 || SS7_BELL05)
  route->replicatedMode= DOMINANT;
#endif
  route->preferredOpc= 0;
  route->nSapId= SCCP_USERPART_SAPID;

	if(m_protocol == "ITU") {
  	route->swtchType= LSN_SW_ITU; // MTP3 Protocol Variant
  	route->upSwtch  = LSN_SW_ITU; // MTP3 Protocol Variant
  	route->swtch    = -1;         // SCCP Protocol Variant
	}
	else if(m_protocol == "NTT") {
  	route->swtchType= LSN_SW_NTT;
  	route->upSwtch  = LSN_SW_NTT;
    // value -1 specific that no need to configure 
    // route at SCCP layer for self route 
  	route->swtch    = -1;         
    route->ssf      = SSF_TTC;
	}
	else if(m_protocol == "ANSI") {
  	route->swtchType= LSN_SW_ANS;
  	route->upSwtch  = LSN_SW_ANS;
  	route->swtch    = -1;
	} 

  liResult = processEmsReq(req);



  for(int i=0; i < tsSltSlcVector.size(); i++)
  {
    logger.logMsg (ALWAYS_FLAG, 0,
      "addSs7Config()::Sending request for ENABLE_LINK on Proc <%d>", linkIdProcIdMap[i]);
    req->cmd_type = ENABLE_LINK;
    LinkEnable *enaLink = &(req->u.lnkEnable);
    cmMemset((U8 *)enaLink, 0, sizeof(LinkEnable));
    enaLink->lnkId = i; 
    enaLink->lnkSetId = 1; 
    //enaLink->procId = mpSmDistributor->getTcapProvider()->selfProcId;
    //enaLink->procId = procIdList[i];
    enaLink->procId = linkIdProcIdMap[i];
    //enaLink->mtp2UsapId = 0; 
    //enaLink->mtp3LsapId = i; 
    enaLink->mtp2UsapId = MTP2_LINK_USAPID + i;
    enaLink->mtp3LsapId = MTP3_LINK_LSAPID + i;

    liResult = processEmsReq(req);
  }


  logger.logMsg (ALWAYS_FLAG, 0,
    "addSs7Config()::Sending request for ENABLE_USERPART");
  req->cmd_type = ENABLE_USERPART;
  EnableUserPart *enaUP = &(req->u.enableUserPart);
  cmMemset((U8 *)enaUP, 0, sizeof(EnableUserPart));
  enaUP->mtp3UsapId= 0; 
  enaUP->m3uaUsapId= 0; 
  enaUP->sccpLsapId= 0; 
  enaUP->nwkType= 0;// MTP 
  liResult = processEmsReq(req);
  

  // In order to test ENABLE_LOCAL_SSN work we also need
  // to execute the command for ADD_NETWORK, ADD_LOCAL_SSN
  printf("\n[+INC+] addSs7Config()::Sending request for ADD_LOCAL_SSN\n");
  logger.logMsg (ALWAYS_FLAG, 0,
         "addSs7Config()::Sending request for ADD_LOCAL_SSN");
  req->cmd_type = ADD_LOCAL_SSN;
  AddLocalSsn *ssn = &(req->u.addLocalSsn);
  cmMemset((U8 *)ssn, 0, sizeof(AddLocalSsn));
  liResult = processEmsReq(req);
  

  printf("\n[+INC+] addSs7Config()::Sending request for ENABLE_LOCAL_SSN\n");
  logger.logMsg (ALWAYS_FLAG, 0,
         "addSs7Config()::Sending request for ENABLE_LOCAL_SSN");
  req->cmd_type = ENABLE_LOCAL_SSN;
  SsnEnable *lSsn = &(req->u.ssnEnable);
  cmMemset((U8 *)lSsn, 0, sizeof(SsnEnable));
  liResult = processEmsReq(req);



  #if 0 
    logger.logMsg (ALWAYS_FLAG, 0,
      "Sending request for STA_LINK");
    req->cmd_type = STA_LINK;
    LinkStatus *lnk = &(req->u.lnkstatus);
    cmMemset((U8 *)lnk, 0,sizeof(LinkStatus));
    liResult = processEmsReq(req);
   
    logger.logMsg (ALWAYS_FLAG, 0,
      "Sending request for STA_LINKSET");
    req->cmd_type = STA_LINKSET;
    LinkSetStatus *lnk = &(req->u.lnkStatus);
    cmMemset((U8 *)lnk, 0,sizeof(LinkSetStatus));
    liResult = processEmsReq(req);
  
   
    logger.logMsg (ALWAYS_FLAG, 0,
      "Sending request for STA_ROUTE");
    req->cmd_type = STA_ROUTE;
    RouteStatus *rte = &(req->u.dpcStatus);
    cmMemset((U8 *)rte, 0,sizeof(RouteStatus));
    liResult = processEmsReq(req);
  #endif

	// ADD_GTRULE - for incoming
  logger.logMsg (ALWAYS_FLAG, 0,
         "addSs7Config()::Sending request for ADD_GTRULE");
  req->cmd_type = ADD_GTRULE;
  AddGtRule *gt = &(req->u.addGtRule);
  cmMemset((U8 *)gt, 0, sizeof(AddGtRule));
  liResult = processEmsReq(req);

  //If the environment "INGW_ADD_GT_RULE" is defined
  //then the second GT RULE shall be added from environment values
  char *lpcGtRule = getenv("INGW_ADD_GT_RULE");
  if(NULL == lpcGtRule) {
    // ADD_GTRULE - for outgoing
    logger.logMsg (ALWAYS_FLAG, 0,
           "addSs7Config()::Sending request for ADD_GTRULE1");
    req->cmd_type = ADD_GTRULE;
    gt = &(req->u.addGtRule);
    cmMemset((U8 *)gt, 0, sizeof(AddGtRule));
    liResult = processEmsReq(req);
  }

	// ADD_GTADDRMAP - for incoming
  logger.logMsg (ALWAYS_FLAG, 0,
         "addSs7Config()::Sending request for ADD_GTADDRMAP");
  req->cmd_type = ADD_GTADDRMAP;
  AddAddrMapCfg *map = &(req->u.addAddrMapCfg);
  cmMemset((U8 *)map, 0, sizeof(AddAddrMapCfg));
  liResult = processEmsReq(req);

  //If the environment "INGW_GTADDRMAP_CONFIG" is defined
  //then the second GT RULE shall be added from environment values
  char *lpcGtAddrMap = getenv("INGW_GTADDRMAP_CONFIG");
  if(NULL == lpcGtAddrMap) {
	  // ADD_GTADDRMAP - for outgoing
    logger.logMsg (ALWAYS_FLAG, 0,
           "addSs7Config()::Sending request for ADD_GTADDRMAP2");
    req->cmd_type = ADD_GTADDRMAP;
    map = &(req->u.addAddrMapCfg);
    cmMemset((U8 *)map, 0, sizeof(AddAddrMapCfg));
    liResult = processEmsReq(req);
  }
  
  logger.logMsg (ALWAYS_FLAG, 0,
         "Leaving addSs7Config()");
  return liResult;
}



int INGwSmWrapper::addSigtranConfig (Ss7SigtranSubsReq *req)
{
  logger.logMsg (ALWAYS_FLAG, 0, "Entering addSigtranConfig()");

  int liResult = BP_AIN_SM_OK;

  SgNode 						 *enaNode;
  AlarmEnableDisable *alrm;
  DebugEnableDisable *dbg;
  vector<string> 		 epIpPortVector;
  vector<string> 		 pspIpPortVector;
  vector<int>        procIdList;

  int transportType = mpSmDistributor->getSmRepository()->getTransportType();

	INGwSmBlkConfig::getInstance().getActiveProcIds(procIdList);
  U8 numProc =  procIdList.size();


  if((TRANSPORT_TYPE_SIGTRAN == transportType) || 
     (TRANSPORT_TYPE_BOTH == transportType)) {
    
    // ENABLE_NODE for ENTSG, ENT_INC_SCCP_TCAP is being done in changeState() method
    
    printf("\n[+INC+] addSigtranConfig()::Sending request for ENABLE_NODE M3UA\n");
    
    logger.logMsg (ALWAYS_FLAG, 0,
      "addSigtranConfig()::Sending request for ENABLE_NODE M3UA");
    req->cmd_type = ENABLE_NODE;
    enaNode = &(req->u.sgNode);
    cmMemset((U8 *)enaNode, 0, sizeof(SgNode));
    enaNode->entId = ENT_INC_M3UA;
    liResult = processEmsReq(req);
  }
 

  if(TRANSPORT_TYPE_SIGTRAN == transportType) {

    printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_NETWORK\n");
    
    logger.logMsg (ALWAYS_FLAG, 0,
      "addSigtranConfig()::Sending request for ADD_NETWORK ");
    req->cmd_type = ADD_NETWORK;
    AddNetwork *nwk = &(req->u.addNwk);
    cmMemset((U8 *)nwk, 0, sizeof(AddNetwork));
    liResult = processEmsReq(req);

    printf("\n[+INC+] addSigtranConfig()::Sending request for ENABLE_DEBUG\n");
    
    logger.logMsg (ALWAYS_FLAG, 0,
      "addSigtranConfig()::Sending request for ENABLE_DEBUG");
    req->cmd_type = ENABLE_DEBUG;
    dbg = &(req->u.debug);
    cmMemset((U8 *)dbg, 0, sizeof(DebugEnableDisable));
    dbg->layer = BP_AIN_SM_MR_LAYER;
    dbg->level = 7;
    liResult = processEmsReq(req);
    
    printf("\n[+INC+] addSigtranConfig()::Sending request for ENABLE_DEBUG\n");
    
    logger.logMsg (ALWAYS_FLAG, 0,
      "addSigtranConfig()::Sending request for ENABLE_DEBUG");
    req->cmd_type = ENABLE_DEBUG;
    dbg = &(req->u.debug);
    cmMemset((U8 *)dbg, 0, sizeof(DebugEnableDisable));
    dbg->layer = BP_AIN_SM_SH_LAYER;
    dbg->level = 7;
    liResult = processEmsReq(req);
  }

  printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_USERPART\n");
  
  logger.logMsg (ALWAYS_FLAG, 0,
    "addSigtranConfig()::Sending request for ADD_USERPART");
  req->cmd_type = ADD_USERPART;
  AddUserPart *up = &(req->u.addUserPart);
  cmMemset((U8 *)up, 0, sizeof(AddUserPart));
  up->userPartType = M3UA_USER;
  up->ssf = SSF_NAT;
  up->mtp3UsapId = MTP3_USERPART_SAPID;
  up->nwId= 1;
  up->suType = 3; // LIT_SU_SCCP: 3
  up->m3uaUsapId = M3UA_USERPART_SAPID;
  up->sccpLsapId = SCCP_USERPART_SAPID_2;

	if(m_protocol == "ITU") {
    	up->lnkType = LSN_SW_ITU; // MTP2 Protocol Variant
			up->upSwtch = LSN_SW_ITU; // MTP3 Protocol Variant
	}
	else if(m_protocol == "NTT") {
     	up->lnkType = LSN_SW_NTT;
			up->upSwtch = LSN_SW_NTT;
      up->ssf     = SSF_TTC;
	}
	else if(m_protocol == "ANSI") {
     	up->lnkType = LSN_SW_ANS;
			up->upSwtch = LSN_SW_ANS;
	} 

  liResult = processEmsReq(req);


  printf("\n[+INC+] addSigtranConfig()::Sending request for ENABLE_USERPART\n");
  
  logger.logMsg (ALWAYS_FLAG, 0,
    "addSigtranConfig()::Sending request for ENABLE_USERPART");
  EnableUserPart *enaUP = &(req->u.enableUserPart);
  cmMemset((U8 *)enaUP, 0, sizeof(EnableUserPart));
  enaUP->mtp3UsapId= 0; 
  enaUP->m3uaUsapId= 0; 
  enaUP->sccpLsapId= 1; 
  enaUP->nwkType= 1;// SIGTRAN 
  req->cmd_type = ENABLE_USERPART;
  liResult = processEmsReq(req);

  
  if(TRANSPORT_TYPE_SIGTRAN == transportType) {
    
    printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_LOCAL_SSN\n");
    
    logger.logMsg (ALWAYS_FLAG, 0,
           "addSigtranConfig()::Sending request for ADD_LOCAL_SSN");
    req->cmd_type = ADD_LOCAL_SSN;
    AddLocalSsn *ssn = &(req->u.addLocalSsn);
    cmMemset((U8 *)ssn, 0, sizeof(AddLocalSsn));
    liResult = processEmsReq(req);
    
    printf("\n[+INC+] addSigtranConfig()::Sending request for ENABLE_LOCAL_SSN\n");
    
    logger.logMsg (ALWAYS_FLAG, 0,
           "addSigtranConfig()::Sending request for ENABLE_LOCAL_SSN");
    req->cmd_type = ENABLE_LOCAL_SSN;
    SsnEnable *lSsn = &(req->u.ssnEnable);
    cmMemset((U8 *)lSsn, 0, sizeof(SsnEnable));
    liResult = processEmsReq(req);

  }

#if 0 
    logger.logMsg (ALWAYS_FLAG, 0,
      "addSigtranConfig()::Sending request for STA_ROUTE");
    req->cmd_type = STA_ROUTE;
    RouteStatus *rte = &(req->u.dpcStatus);
    cmMemset((U8 *)rte, 0,sizeof(RouteStatus));
    liResult = processEmsReq(req);
#endif


  req->cmd_type = ADD_ENDPOINT;
  AddEndPoint *ep = &(req->u.addEp);
  cmMemset((U8 *)ep, 0, sizeof(AddEndPoint));

  char *epIpPortstr = getenv("INGW_SIGTRAN_IP_PORT");

  if( NULL == epIpPortstr){
    printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_ENDPOINT EP0\n");
    
    logger.logMsg (ALWAYS_FLAG, 0,
           "addSigtranConfig()::Sending request for ADD_ENDPOINT EP0");

  ep->endPointid = 1;

  ep->sctpLsapId= 0;
  ep->sctpUsapId= 0;
  ep->m3uaLsapId= 0;
  ep->tuclUsapId= 0;

  char  selfEpIp[20];
  memset(selfEpIp, 0, sizeof(selfEpIp));

    ep->srcPort= 2905; //2905 default M3UA port
    strncpy(selfEpIp,"10.32.10.43",strlen("10.32.10.43")); //Spectra IP
    ep->sctpProcId= selfProcId;
    ep->nmbAddrs= 1;
    ep->nAddr[0].type = CM_NETADDR_IPV4;
    ep->nAddr[0].u.ipv4NetAddr = ntohl(inet_addr((S8*)"10.32.10.43"));
    liResult = processEmsReq(req);
  }
  else{

    char lBuf[512];
    int lBufLen = 0;
    string lsIpPortstr(epIpPortstr);
    lBufLen += sprintf(lBuf + lBufLen,"\n+VER+ YOGESH IP PORT String <%s>\n",lsIpPortstr.c_str());
    g_tokenizeValue(lsIpPortstr, G_PIPE_DELIM, epIpPortVector);

    for(int i=0; i< epIpPortVector.size(); i++) 
    {
      printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_ENDPOINT EP%d\n", i);
      
      logger.logMsg (ALWAYS_FLAG, 0,
             "addSigtranConfig()::Sending request for ADD_ENDPOINT EP%d", i);
       
      cmMemset((U8 *)ep, 0, sizeof(AddEndPoint));
      memset(lBuf, 0, sizeof(lBuf));
      lBufLen = 0;

      ep->endPointid = i + 1;

      ep->sctpLsapId= i;
      ep->sctpUsapId= i;
      ep->m3uaLsapId= i;
      ep->tuclUsapId= i;
      
      vector<string> fieldList;
      g_tokenizeValue(epIpPortVector[i], G_COMMA_DELIM, fieldList);
      lBufLen += sprintf(lBuf + lBufLen,"\n+VER+  IP PORT [%s]\n",
                         epIpPortVector[i].c_str());
      if(INGW_SIGTRAN_FIELD_SIZE != fieldList.size()) {
        lBufLen += sprintf(lBuf + lBufLen,
          "\n+VER+ Unequal fields %d \n",fieldList.size());
        return -1;
      }
      lBufLen += sprintf(lBuf + lBufLen,
        "\n+VER+ INGW_SIGTRAN_EP_IP [%s]\n",fieldList[INGW_SIGTRAN_EP_IP].c_str());
      
      lBufLen += sprintf(lBuf + lBufLen,
        "\n+VER+ INGW_SIGTRAN_SRC_PORT [%s]\n",fieldList[INGW_SIGTRAN_SRC_PORT].c_str());

      lBufLen += sprintf(lBuf + lBufLen,
        "\n+VER+ INGW_SCTP_PROC_ID [%s]\n",fieldList[INGW_SIGTRAN_SCTP_PROC_ID].c_str());
      printf("%s:%d: %s", __FILE__, __LINE__, lBuf);
      
      ep->srcPort    = atoi(fieldList[INGW_SIGTRAN_SRC_PORT].c_str());
      string lsIpstr = fieldList[INGW_SIGTRAN_EP_IP].c_str();

      vector<string> epIpList;
      epIpList.clear();

      g_tokenizeValue(fieldList[INGW_SIGTRAN_EP_IP], ":", epIpList);

      ep->nmbAddrs =  epIpList.size();

      for (int j = 0; j < epIpList.size(); j++) {
        ep->nAddr[j].type = CM_NETADDR_IPV4; 
        //not validating IP
        ep->nAddr[j].u.ipv4NetAddr = ntohl(inet_addr((S8*) epIpList[j].c_str()));
      }

      U16 lProcId = atoi(fieldList[INGW_SIGTRAN_SCTP_PROC_ID].c_str());
      bool isProcIdValid = false;
      for(int i=0;i<numProc;i++) {
        if(lProcId == procIdList[i]){
          isProcIdValid = true;
          break;
        }
      }
      if(false == isProcIdValid){
        lBufLen += sprintf(lBuf + lBufLen,
          "Invalid procId <%d>",lProcId);
        logger.logMsg (ERROR_FLAG, 0, "addSigtranConfig(): %s", lBuf);
        return -1;
      }
      ep->sctpProcId = lProcId; 
      epIdProcIdMap[i] = lProcId;

      liResult = processEmsReq(req);
    } 
  }
  

 

#if 0
  // Need to check whether such command is there
  logger.logMsg (ALWAYS_FLAG, 0,
    "addSigtranConfig()::Sending request for ENABLE_ENDPOINT");
  req->cmd_type = ENABLE_ENDPOINT;
  liResult = processEmsReq(req);
#endif

  if(TRANSPORT_TYPE_SIGTRAN == transportType) {
    // ADD_GTRULE - for incoming
    printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_GTRULE\n");
    
    logger.logMsg (ALWAYS_FLAG, 0,
           "addSigtranConfig()::Sending request for ADD_GTRULE");
    req->cmd_type = ADD_GTRULE;
    AddGtRule *gt = &(req->u.addGtRule);
    cmMemset((U8 *)gt, 0, sizeof(AddGtRule));
    liResult = processEmsReq(req);

    //If the environment "INGW_ADD_GT_RULE" is defined
    //then the second GT RULE shall be added from environment values
    char *lpcGtRule = getenv("INGW_ADD_GT_RULE");
    if(NULL == lpcGtRule) {
      // ADD_GTRULE - for outgoing
      printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_GTRULE1\n");
      
      logger.logMsg (ALWAYS_FLAG, 0,
           "addSigtranConfig()::Sending request for ADD_GTRULE1");
      req->cmd_type = ADD_GTRULE;
      gt = &(req->u.addGtRule);
      cmMemset((U8 *)gt, 0, sizeof(AddGtRule));
      liResult = processEmsReq(req);
    }

    // ADD_GTADDRMAP - for incoming
    printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_GTADDRMAP\n");
    
    logger.logMsg (ALWAYS_FLAG, 0,
           "addSigtranConfig()::Sending request for ADD_GTADDRMAP");
    req->cmd_type = ADD_GTADDRMAP;
    AddAddrMapCfg *map = &(req->u.addAddrMapCfg);
    cmMemset((U8 *)map, 0, sizeof(AddAddrMapCfg));
    liResult = processEmsReq(req);

    //If the environment "INGW_GTADDRMAP_CONFIG" is defined
    //then the second GT RULE shall be added from environment values
    char *lpcGtAddrMap = getenv("INGW_GTADDRMAP_CONFIG");
    if(NULL == lpcGtAddrMap) {
      // ADD_GTADDRMAP - for outgoing
      printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_GTADDRMAP2\n");
      
      logger.logMsg (ALWAYS_FLAG, 0,
           "addSigtranConfig()::Sending request for ADD_GTADDRMAP2");
      req->cmd_type = ADD_GTADDRMAP;
      map = &(req->u.addAddrMapCfg);
      cmMemset((U8 *)map, 0, sizeof(AddAddrMapCfg));
      liResult = processEmsReq(req);
    }
  }



  req->cmd_type = ADD_ASP;
  AddPsp *psp = &(req->u.addpsp);
  cmMemset((U8 *)psp, 0, sizeof(AddPsp));

  char pspIpaddr[20];
  memset(pspIpaddr, 0, sizeof(pspIpaddr));

  char *pspIpPortstr = getenv("INGW_PSP_IP_PORT");

  if(NULL == pspIpPortstr) {
    printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_ASP ASP1\n");
    
    logger.logMsg (ALWAYS_FLAG, 0,
           "addSigtranConfig()::Sending request for ADD_ASP ASP1");
    psp->pspId = 1;
    psp->nmbAddr = 1;
    psp->addr[0].type = 4;
    strncpy(pspIpaddr,"10.32.10.71",strlen("10.32.10.71")); //Spectra IP
    logger.logMsg (ALWAYS_FLAG, 0,"REMOTE IP FOR ASP OR SGP <%s>",pspIpaddr);

    psp->addr[0].u.ipv4NetAddr = ntohl(inet_addr((S8 *)pspIpaddr));
    psp->dstPort = 2905; //default M3ua port:2905
    
    liResult = processEmsReq(req);
     
  }
  else
  {
    char lBuf[512];
    int lBufLen = 0;
    string lsIpPortstr(pspIpPortstr);
    lBufLen += sprintf(lBuf + lBufLen,"\n+VER+ PSP IP PORT String <%s>\n",lsIpPortstr.c_str());
    g_tokenizeValue(lsIpPortstr, G_PIPE_DELIM, pspIpPortVector);


    for(int i=0; i< pspIpPortVector.size(); i++) 
    {
      printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_ASP ASP%d\n", i);
      
      logger.logMsg (ALWAYS_FLAG, 0,
             "addSigtranConfig()::Sending request for ADD_ASP ASP%d", i);

      cmMemset((U8 *)psp, 0, sizeof(AddPsp));

      memset(lBuf, 0, sizeof(lBuf));
      lBufLen = 0;

      psp->pspId =  i + 1;
  
      vector<string> fieldList;
      g_tokenizeValue(pspIpPortVector[i], G_COMMA_DELIM, fieldList);
      lBufLen += sprintf(lBuf + lBufLen,"\n+VER+  IP PORT [%s]\n",
                         pspIpPortVector[i].c_str());
      if(INGW_SIGTRAN_REMOTE_PSP_FIELD_SIZE != fieldList.size()) {
        lBufLen += sprintf(lBuf + lBufLen,
          "\n+VER+ INGW_SIGTRAN_REMOTE_PSP_FIELD_SIZE Unequal fields %d \n",
          fieldList.size());
           return -1;
      }
      lBufLen += sprintf(lBuf + lBufLen,
        "\n+VER+ INGW_SIGTRAN_IP_REMOTE_PSP[%s]\n",fieldList[INGW_SIGTRAN_IP_REMOTE_PSP].c_str());

      lBufLen += sprintf(lBuf + lBufLen,
        "\n+VER+ INGW_SIGTRAN_PORT_REMOTE_PSP[%s]\n",
        fieldList[INGW_SIGTRAN_PORT_REMOTE_PSP].c_str());

      //strncpy(pspIpaddr,fieldList[INGW_SIGTRAN_IP_REMOTE_PSP].c_str(),
      //  strlen(fieldList[INGW_SIGTRAN_IP_REMOTE_PSP].size())); //Spectra IP

      vector<string> pspIpList;
      pspIpList.clear();

      g_tokenizeValue(fieldList[INGW_SIGTRAN_IP_REMOTE_PSP], ":", pspIpList);

      psp->nmbAddr = pspIpList.size();

      for (int j = 0; j < pspIpList.size(); j++) {
        psp->addr[j].type = CM_NETADDR_IPV4;
        psp->addr[j].u.ipv4NetAddr = 
               ntohl(inet_addr((S8 *)pspIpList[j].c_str()));
      }
      //default M3ua port:2905
      psp->dstPort = atoi(fieldList[INGW_SIGTRAN_PORT_REMOTE_PSP].c_str());
      printf("%s:%d %s\n", __FILE__, __LINE__, lBuf); 

      liResult = processEmsReq(req);
    }
  }

  printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_AS LOCAL\n");
  
  logger.logMsg (ALWAYS_FLAG, 0,
         "addSigtranConfig()::Sending request for ADD_AS LOCAL");
  req->cmd_type = ADD_AS;
  AddPs *ps1 = &(req->u.addPs);
  cmMemset((U8 *)ps1, 0,sizeof(AddPs));
  ps1->routCtx = 100;
  ps1->lFlag = 1;
  ps1->psId = 1;
  ps1->dpcMask = 0x3fff;
  ps1->dpc = 123; // Self SIGTRAN PointCode
  ps1->opcMask = 0x3fff;
  ps1->opc = 124; // VSP PointCode

  ps1->nmbActPspReqd = 1;
  ps1->nmbPsp = 1;
  //ps1->nmbPsp = pspIpPortVector.size();
  logger.logMsg (ALWAYS_FLAG, 0,
         "addSigtranConfig()::ADD_AS LOCAL ps1->nmbPsp %d", ps1->nmbPsp);
  for (int i = 0; i < ps1->nmbPsp; i++) {
    ps1->psp[i] = 1;
    ps1->pspEpLst[i].nmbEp = 2;
    ps1->pspEpLst[i].endpIds[0] = 1;
    ps1->pspEpLst[i].endpIds[1] = 2;
  }
  
  liResult = processEmsReq(req);

  printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_AS 1 REMOTE\n");
  
  logger.logMsg (ALWAYS_FLAG, 0,
         "addSigtranConfig()::Sending request for ADD_AS 1 REMOTE");
  req->cmd_type = ADD_AS;
  AddPs *ps2 = &(req->u.addPs);
  cmMemset((U8 *)ps2, 0,sizeof(AddPs));
  ps2->routCtx = 100;
  ps2->lFlag = 0;
  ps2->psId = 2;
  ps2->dpcMask = 0x3fff;
  ps2->dpc = 122; // SG PointCode
  ps2->opcMask = 0x3fff;
  ps2->opc = 123; // Self SIGTRAN PointCode

  ps2->nmbActPspReqd = 1;
  ps2->nmbPsp = 1;
  //ps2->nmbPsp = pspIpPortVector.size();
  logger.logMsg (ALWAYS_FLAG, 0,
         "addSigtranConfig()::ADD_AS 1 REMOTE ps2->nmbPsp %d", ps2->nmbPsp);
  for (int i = 0; i < ps2->nmbPsp; i++) {
    ps2->psp[i] = 1;
    ps2->pspEpLst[i].nmbEp = 2;
    ps2->pspEpLst[i].endpIds[0] = 1;
    ps2->pspEpLst[i].endpIds[1] = 2;
  }

  liResult = processEmsReq(req);



  printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_ROUTE for SG\n");
  
  /* Route cfg*/
  logger.logMsg (ALWAYS_FLAG, 0,
    "addSigtranConfig()::Sending request for ADD_ROUTE for SG");
  req->cmd_type = ADD_ROUTE;
  AddRoute *route1 = &(req->u.addRoute);
  cmMemset((U8 *)route1, 0, sizeof(AddRoute));
  route1->dpc = 122;   // SGP pointcode
  route1->spType= LSN_TYPE_SP;
  route1->dir= LSN_RTE_DN;
  route1->rteToAdjSp= TRUE;
  route1->ssf= SSF_NAT;
  
  /* for sccp*/
  route1->status= SP_ADJACENT;
  route1->nmbBpc= 0;
  route1->nmbSsns= 0;
#if (SS7_ANS96 || SS7_BELL05)
  route1->replicatedMode= DOMINANT;
#endif
  route1->preferredOpc= 123;
  route1->nSapId= SCCP_USERPART_SAPID_2;

	if(m_protocol == "ITU") {
   // -1 specifies not to configure route for MTP3 lavel, 
   // since configuring route for SIGTRAN
  	route1->swtchType= 255;         // MTP3 Protocol Variant, 
  	route1->upSwtch  = LSN_SW_ITU; // MTP3 Protocol Variant
  	route1->swtch    = LSP_SW_ITU92;// SCCP Protocol Variant
	}
	else if(m_protocol == "NTT") {
  	route1->swtchType= 255;
  	route1->upSwtch  = LSN_SW_NTT;
  	route1->swtch    = LSP_SW_JAPAN;
    route1->ssf      = SSF_TTC;
	}
	else if(m_protocol == "ANSI") {
  	route1->swtchType= 255;
  	route1->upSwtch  = LSN_SW_ANS;
  	route1->swtch    = LSP_SW_ANS96;
    route1->ssf      = SSF_NAT;
	} 
  
  liResult = processEmsReq(req);

  printf("\n[+INC+] addSigtranConfig()::Sending request for ADD_ROUTE for VSP\n");
  
  /* Route cfg*/
  logger.logMsg (ALWAYS_FLAG, 0,
    "addSigtranConfig()::Sending request for ADD_ROUTE for VSP");
  req->cmd_type = ADD_ROUTE;
  AddRoute *route2 = &(req->u.addRoute);
  cmMemset((U8 *)route2, 0, sizeof(AddRoute));
  route2->dpc = 124;   // VSP pointcode
  route2->spType= LSN_TYPE_SP;
  route2->dir= LSN_RTE_DN;
  route2->rteToAdjSp= TRUE;
  route2->ssf= SSF_NAT;
  
  /* for sccp*/
  route2->status= SP_ADJACENT;
  route2->nmbBpc= 0;
  route2->nmbSsns= 1;
  route2->ssnList[0].ssn = 146;
  route2->ssnList[0].status = SS_INACC;
  route2->ssnList[0].nmbBpc = 0;
  route2->ssnList[0].nmbConPc = 0;
#if (SS7_ANS96 || SS7_BELL05)
  route2->replicatedMode= DOMINANT;
#endif
  route2->preferredOpc= 123;
  route2->nSapId= SCCP_USERPART_SAPID_2;

	if(m_protocol == "ITU") {
   // -1 specifies not to configure route for MTP3 lavel, 
   // since configuring route for SIGTRAN
  	route2->swtchType= 255;         // MTP3 Protocol Variant, 
  	route2->upSwtch  = LSN_SW_ITU; // MTP3 Protocol Variant
  	route2->swtch    = LSP_SW_ITU92;// SCCP Protocol Variant
	}
	else if(m_protocol == "NTT") {
  	route2->swtchType= 255;
  	route2->upSwtch  = LSN_SW_NTT;
  	route2->swtch    = LSP_SW_JAPAN;
    route2->ssf      = SSF_TTC;
	}
	else if(m_protocol == "ANSI") {
  	route2->swtchType= 255;
  	route2->upSwtch  = LSN_SW_ANS;
  	route2->swtch    = LSP_SW_ANS96;
    route2->ssf      = SSF_NAT;
	} 
  
  liResult = processEmsReq(req);


  printf("\n[+INC+] addSigtranConfig()::Sending request for ENABLE_NODE SCTP  & TUCL\n");
  
  logger.logMsg (ALWAYS_FLAG, 0,
    "addSigtranConfig()::Sending request for ENABLE_NODE SCTP  & TUCL");
  req->cmd_type = ENABLE_NODE;
  enaNode = &(req->u.sgNode);
  cmMemset((U8 *)enaNode, 0, sizeof(SgNode));
  enaNode->entId = ENT_INC_SCTP_TUCL;
  liResult = processEmsReq(req);

  if((TRANSPORT_TYPE_SIGTRAN == transportType) || 
       (TRANSPORT_TYPE_BOTH == transportType)) {

      logger.logMsg (TRACE_FLAG, 0,
        "addSigtranConfig()::Sending request for ENABLE_ALARM for M3UA Layer");
      req->cmd_type = ENABLE_ALARM;
      alrm = &(req->u.alarm);
      cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
      alrm->Layer = BP_AIN_SM_M3U_LAYER;
      liResult = cliEnaAlarm(alrm, procIdList);

      //logger.logMsg (TRACE_FLAG, 0, "addSigtranConfig()::Sleep 5 secs...");
      //sleep(5);

      logger.logMsg (TRACE_FLAG, 0,
        "addSigtranConfig()::Sending request for ENABLE_ALARM for SCTP Layer");
      req->cmd_type = ENABLE_ALARM;
      alrm = &(req->u.alarm);
      cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
      alrm->Layer = BP_AIN_SM_SCT_LAYER;
      liResult = cliEnaAlarm(alrm, procIdList);
   
      logger.logMsg (TRACE_FLAG, 0,
        "addSigtranConfig()::Sending request for ENABLE_ALARM for TUCL Layer");
      req->cmd_type = ENABLE_ALARM;
      alrm = &(req->u.alarm);
      cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
      alrm->Layer = BP_AIN_SM_TUC_LAYER;
      liResult = cliEnaAlarm(alrm, procIdList);

      logger.logMsg (TRACE_FLAG, 0,
        "addSigtranConfig()::Sending request for ENABLE_ALARM for LDF-M3UA Layer");
      req->cmd_type = ENABLE_ALARM;
      alrm = &(req->u.alarm);
      cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
      alrm->Layer = BP_AIN_SM_LDF_M3UA_LAYER;
      liResult = cliEnaAlarm(alrm, procIdList);
    
      logger.logMsg (TRACE_FLAG, 0,
        "addSigtranConfig()::Sending request for ENABLE_ALARM for PSF-M3UA Layer");
      req->cmd_type = ENABLE_ALARM;
      alrm = &(req->u.alarm);
      cmMemset((U8 *)alrm, 0, sizeof(AlarmEnableDisable));
      alrm->Layer = BP_AIN_SM_PSF_M3UA_LAYER;
      liResult = cliEnaAlarm(alrm, procIdList);
   }    

#if 0
  // Used to manually trigger bind request between M3UA <=> SCTP <=> TUCL
  logger.logMsg (ALWAYS_FLAG, 0,
    "addSigtranConfig()::Sending request for BND_M3UA");
  req->cmd_type = BND_M3UA;
  liResult = processEmsReq(req);
#endif

  logger.logMsg (ALWAYS_FLAG, 0, 
         "addSigtranConfig()::Wait to receive alarm. Sleep 3 secs...");
  sleep(3);//Putting sleep to receive alarm

  printf("\n[+INC+] addSigtranConfig()::Sending request for OPEN_ENDPOINT\n");
  
  logger.logMsg (ALWAYS_FLAG, 0,
    "addSigtranConfig()::Sending request for OPEN_ENDPOINT");
  req->cmd_type = OPEN_ENDPOINT;
  liResult = processEmsReq(req);

  printf("\n[+INC+] addSigtranConfig()::Sending request for OPEN_ENDPOINT\n");
  
  logger.logMsg (ALWAYS_FLAG, 0,
    "addSigtranConfig()::Sending request for OPEN_ENDPOINT");
  req->cmd_type = OPEN_ENDPOINT;
  liResult = processEmsReq(req);
  

  logger.logMsg (ALWAYS_FLAG, 0, 
         "addSigtranConfig()::Wait to receive alarm OPEN_ENDPOINT. Sleep 3 secs...");
  sleep(3);//Putting sleep to receive alarm

#if 0
  logger.logMsg (ALWAYS_FLAG, 0,
         "addSigtranConfig()::Sending request for STA_PS");
  req->cmd_type = STA_PS;
  PsStatus *ps = &(req->u.ps);
  cmMemset((U8 *)ps, 0,sizeof(PsStatus));
  ps->psId = 2;

  liResult = processEmsReq(req);

  logger.logMsg (ALWAYS_FLAG, 0,
         "addSigtranConfig()::Sending request for STA_PSP");
  req->cmd_type = STA_PSP;
  PspStatus *psp = &(req->u.psp);
  cmMemset((U8 *)psp, 0,sizeof(PspStatus));
  liResult = processEmsReq(req);
#endif

  for (int i = 0; i < epIpPortVector.size(); i++) {
    printf("\n[+INC+] addSigtranConfig()::Sending request for ASSOC_UP with EP%d\n", i);
    
    logger.logMsg (ALWAYS_FLAG, 0,
           "addSigtranConfig()::Sending request for ASSOC_UP with EP%d", i);
  req->cmd_type = ASSOC_UP;
  M3uaAssocUp *assoc = &(req->u.m3uaAssocUp);
  cmMemset((U8 *)assoc, 0,sizeof(M3uaAssocUp));
    assoc->pspId = 1; 
    // Not being used by stack, 
    // this is derived from the ASP-ENDPOINTID information 
    // supplied during AS configuration
    assoc->endPointId = i + 1;
    assoc->m3uaLsapId = i;
          
    liResult = processEmsReq(req);
  }


  logger.logMsg (ALWAYS_FLAG, 0, 
         "addSigtranConfig()::Wait to receive alarm for ASSOC_UP. Sleep 3 secs...");
  sleep(3);//Putting sleep to receive alarm


  for (int i = 0; i < epIpPortVector.size(); i++) {
    printf("\n[+INC+] addSigtranConfig()::Sending request for ASP_UP for EP%d\n", i);
    
    logger.logMsg (ALWAYS_FLAG, 0,
           "addSigtranConfig()::Sending request for ASP_UP for EP%d", i);
  req->cmd_type = ASP_UP;
  M3uaAspUp *aspUp = &(req->u.m3uaAspUp);
  cmMemset((U8 *)aspUp, 0,sizeof(M3uaAspUp));
    aspUp->pspId = 1;
    // Not being used by stack, 
    // this is derived from the ASP-ENDPOINTID information 
    // supplied during AS configuration
    aspUp->endPointId = i + 1;
    aspUp->m3uaLsapId = i;
    liResult = processEmsReq(req);
  }

  logger.logMsg (ALWAYS_FLAG, 0, 
         "addSigtranConfig()::Wait to receive alarm for ASP_UP. Sleep 3 secs...");
  sleep(3);//Putting sleep to receive alarm

  for (int i = 0; i < epIpPortVector.size(); i++) {
    printf("\n[+INC+] addSigtranConfig()::Sending request for ASP_ACTIVE for EP%d\n", i);
    
    logger.logMsg (ALWAYS_FLAG, 0,
           "addSigtranConfig()::Sending request for ASP_ACTIVE for EP%d", i);
  req->cmd_type = ASP_ACTIVE;
  M3uaAspAct *aspAct = &(req->u.m3uaAspAct);
  cmMemset((U8 *)aspAct, 0,sizeof(M3uaAspAct));
    aspAct->psId = 2;
    aspAct->pspId = 1;
    // Not being used by stack, 
    // this is derived from the ASP-ENDPOINTID information 
    // supplied during AS configuration
    aspAct->endPointId = i + 1;
    aspAct->m3uaLsapId = i;
 
    liResult = processEmsReq(req);
  }

  logger.logMsg (ALWAYS_FLAG, 0,
         "Leaving addSigtranConfig()");
  return liResult;
}


/******************************************************************************
*
*     Fun:   configure()
*
*     Desc:  to be invoked by TcapProvider for any self oid change
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int
INGwSmWrapper::configure (const char* apcOID, 
                           const char* apcValue, 
                           ConfigOpType aeOpType)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::configure <%s, %s>", apcOID, apcValue);

  int liResult = G_SUCCESS;

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  /*
   * if the oid is for CCM becoming primary then
   * invoke handlePeerFailure.
   */
  //if (strcmp(apcOID, ccmIS_PRIMARY) == 0 &&
  if (strcmp(apcOID, ingwIS_PRIMARY) == 0 &&
      strcmp(apcValue, "1") == 0)
  {
    logger.logMsg (ALWAYS_FLAG, 0,
      "TcapProvider has moved to primary state.");
		g_dumpMsg("SmWrapper", __LINE__, "Role Changed to Active Starts");
    handlePeerFailure ();
		INGwSmBlkConfig::getInstance().m_toggleFlag = false;
		INGwSmBlkConfig::getInstance().roleChangedToActive();
		g_dumpMsg("SmWrapper", __LINE__, "Role Changed to Active Ends");
    
    return G_SUCCESS;
  }

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  INGwSmQueueMsg *lpQMsg = new INGwSmQueueMsg;
  int liRequestId = (int) pthread_self ();

  lpQMsg->mSrc = BP_AIN_SM_SRC_CCM;

  lpQMsg->t.ccmOper.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  lpQMsg->t.ccmOper.miRequestId = liRequestId;

  /*
   * NOTE : The oid and value are allocated here and hence should be 
   * freed by the receiver of the message.
   */
  lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcOid = new char [strlen (apcOID) + 1];
  strcpy (lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcOid, apcOID);

  lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcValue = new char [strlen (apcValue) + 1];
  strcpy (lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcValue, apcValue);

  lpQMsg->t.ccmOper.ccmOp.oidInfo.mConfigOpTyp = aeOpType;

  //post the message to the Distributor
  if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "In configure, postMsg failed");
    delete [] lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcOid;
    delete [] lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcValue;
    delete lpQMsg;
    return -1;
  }


  //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::configure");

  return liResult;
}


/******************************************************************************
*
*     Fun:   oidChanged()
*
*     Desc:  to be invoked by TcapProvider for any self oid change
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int
INGwSmWrapper::oidChanged (const char* apcOID, 
                            const char* apcValue, 
                            ConfigOpType aeOpType, 
                            long alSubsystemId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::oidChanged <%s, %s, %d>",
    apcOID, apcValue, alSubsystemId);

  int liResult = 0;

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */

  INGwSmQueueMsg *lpQMsg = new INGwSmQueueMsg;
  int liRequestId = (int) pthread_self ();

  lpQMsg->mSrc = BP_AIN_SM_SRC_CCM;

  lpQMsg->t.ccmOper.mOpType = BP_AIN_SM_CCMOP_OIDCHANGED;
  lpQMsg->t.ccmOper.miRequestId = liRequestId;

  /*
   * NOTE : The oid and value are allocated here and hence should be
   * freed by the receiver of the message.
   */

  lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcOid = new char [strlen (apcOID) + 1];
  strcpy (lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcOid, apcOID);
      
  lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcValue = new char [strlen (apcValue) + 1];
  strcpy (lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcValue, apcValue);
        
  lpQMsg->t.ccmOper.ccmOp.oidInfo.mConfigOpTyp = aeOpType;
  lpQMsg->t.ccmOper.ccmOp.oidInfo.mlSubsystemId = alSubsystemId;
            
  //post the message to the Distributor
  if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "In change state to oidChanged, postMsg failed");
    delete [] lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcOid;
    delete [] lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcValue;
    delete lpQMsg;
    return -1;
  }

        
  //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {   
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::oidChanged");

  return liResult;
}

/******************************************************************************
*
*     Fun:   enableNode ()
*
*     Desc:  to be invoked by TcapProvider for enabling binding in MTP
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int
INGwSmWrapper::enableNode ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::enableNode ");

  int liResult = 0;

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */

  INGwSmQueueMsg *lpQMsg = new INGwSmQueueMsg;
  int liRequestId = (int) pthread_self ();

  lpQMsg->mSrc = BP_AIN_SM_SRC_CCM;

  lpQMsg->t.ccmOper.mOpType = BP_AIN_SM_CCMOP_ENABLE_NODE;
  lpQMsg->t.ccmOper.miRequestId = liRequestId;
  
  //post the message to the Distributor 
  if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "In enableNode, postMsg failed");
    delete lpQMsg;
    return -1;
  }


  //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::enableNode");

  return liResult;
}


/******************************************************************************
*
*     Fun:   handleIuBndCfm()
*
*     Desc:  to be invoked by AIN Provider when Bind Confirm is received by it
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int
INGwSmWrapper::handleIuBndCfm (int aiSuId, int aiStatus)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::handleIuBndCfm");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  int liResult = 0;

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */

  INGwSmQueueMsg *lpQMsg = new INGwSmQueueMsg;

  lpQMsg->mSrc = BP_AIN_SM_SRC_STACK;

  //post the message to the Distributor
  if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "In handleIuBndCfm, postMsg failed");
    delete lpQMsg;
    return -1;
  }


  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::handleIuBndCfm");
  return 0;
}

/******************************************************************************
*
*     Fun:   handlePeerFailure()
*
*     Desc:  to be invoked by AIN Provider when the peer fails:
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int
INGwSmWrapper::handlePeerFailure ()
{
  logger.logMsg (ALWAYS_FLAG, 0,
    "Entering INGwSmWrapper::handlePeerFailure");

	std::ostringstream msg;

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

 	int liResult = G_SUCCESS;
  int nodeType    = mpSmDistributor->getSmRepository()->getTransportType();
  int liRequestId = getContextId();

  setAllowCfgFromEms(false,
           (char*)"INGwSmWrapper::handlePeerFailure()", __LINE__);

  INGwSmBlkConfig::eStackConfigState selfStackState = INGwSmBlkConfig::getInstance().m_selfState;
	INGwSmBlkConfig::eStackConfigState peerStackState = INGwSmBlkConfig::getInstance().m_peerState;

  logger.logMsg (ALWAYS_FLAG, 0,
         "handlePeerFailure(): Stack m_selfState<%d> m_peerState<%d>",
         selfStackState, peerStackState);

  if (selfStackState == INGwSmBlkConfig::UNINITIALIZED) {
    logger.logMsg (ALWAYS_FLAG, 0,
           "Leaving handlePeerFailure(). SelfStackState<%d> or "
           "PeerStackState<%d> is Uninitialized.");
    return liResult;
  }

  /*
   * create a new Queue object and used the request Id as running number. 
	 * This has been done to avoid thread from blocking incase no 
	 * response from stackis received. 
   * This will be used by Stack Manager to unblock it if needed.
   */

  INGwSmQueueMsg *lpQMsg = new INGwSmQueueMsg;
  memset ((void *)lpQMsg, 0, sizeof (INGwSmQueueMsg));

  // Change roles of SG and other lyers on takeover node Sending request to 
	// DISABLE SG for FailedNode and stack on takeover node shall automatically 
	// change self SG to ACTIVE mode from STANDBY mode.
  // No need to send explicit ENABLE request for SG on takeover node

  std::string sgRole = 
       INGwTcapProvider::getInstance().getAinSilTxRef().getSelfSgRole();
  logger.logMsg (ALWAYS_FLAG, 0,
         "handlePeerFailure()::Self SG Role<%s>. Sending request to DISABLE SG "
         "for FailedNode ProcId:%d, SelfProcId:%d",
         sgRole.c_str(), INGwSmBlkConfig::getInstance().m_peerProcId, 
         INGwSmBlkConfig::getInstance().m_selfProcId);

	msg << "HandlePeerFailure Self SG Role<" << sgRole.c_str() <<
         "> peerProcId:" <<
				 INGwSmBlkConfig::getInstance().m_peerProcId <<
  " selfProcId:" << INGwSmBlkConfig::getInstance().m_selfProcId;

	g_dumpMsg("SmWrapper", __LINE__, msg);

  memset ((void *)lpQMsg, 0, sizeof (INGwSmQueueMsg));
  mpSmDistributor->ss7SigtranStackRespPending = 1;

  lpQMsg->mSrc = BP_AIN_SM_SRC_EMS;
  lpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_PEERFAILED;
  lpQMsg->t.stackData.cmdType = DISABLE_NODE;
  lpQMsg->t.stackData.req.cmd_type = DISABLE_NODE;
  lpQMsg->t.stackData.req.u.sgNode.entId = ENTSG;
  lpQMsg->t.stackData.stackLayer = BP_AIN_SM_SG_LAYER;
  lpQMsg->t.stackData.procId = SFndProcId();
	lpQMsg->t.stackData.req.u.sgNode.procId = 
						INGwSmBlkConfig::getInstance().m_peerProcId;
  lpQMsg->t.stackData.miRequestId = liRequestId;

  //post the message to the Distributor
  if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "In change state to stopped, postMsg failed");
    delete lpQMsg;
    return -1;
  }

	g_dumpMsg("SmWrapper", __LINE__, "DISABLE SG NODE Posted");

  //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {   
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
      liResult = -1;

	  g_dumpMsg("SmWrapper", __LINE__, "DISABLE SG NODE Done");
  }
	else {
		logger.logMsg(ERROR_FLAG, 0, "DISABLE SG NODE Failed in blockOperation. Timedout Failed");
		g_dumpMsg("SmWrapper", __LINE__, "DISABLE SG NODE Done, Timedout Failed");
	}

  char lpcTime[64];
  memset(lpcTime, 0, sizeof(lpcTime));
  lpcTime[0] = '1';
  g_getCurrentTime(lpcTime);
  sgRole = 
       INGwTcapProvider::getInstance().getAinSilTxRef().getSelfSgRole();
  printf("[+INC+] %s handlePeerFailure(): New Self SG Role<%s>\n",
         lpcTime, sgRole.c_str()); fflush(stdout);

  //mpSmDistributor->ss7SigtranStackRespPending = 1;
  //lpQMsg->mSrc = BP_AIN_SM_SRC_EMS;
  //lpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_PEERFAILED;
  //lpQMsg->t.stackData.cmdType = ABORT_SG_TRANS;
  //lpQMsg->t.stackData.req.cmd_type = ABORT_SG_TRANS;
  //lpQMsg->t.stackData.req.u.sgNode.entId = ENTSG;
  //lpQMsg->t.stackData.stackLayer = BP_AIN_SM_SG_LAYER;
  //lpQMsg->t.stackData.procId = SFndProcId();
  //lpQMsg->t.stackData.req.u.sgNode.procId = SFndProcId();
  //lpQMsg->t.stackData.miRequestId = liRequestId;

  ////post the message to the Distributor
  //if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
  //{
  //  logger.logMsg (ERROR_FLAG, 0,
  //    "In change state to stopped, postMsg failed");
  //  delete lpQMsg;
  //  return -1;
  //}

  //      
  ////block the current thread
  //if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  //{   
  //  INGwSmBlockingContext *lpContext = 0;
  //  if (removeBlockingContext (liRequestId, lpContext))
  //  {
  //    if (lpContext->returnValue != BP_AIN_SM_OK)
  //      liResult = -1;

  //    delete lpContext;
  //  }
  //  else
  //  {
  //    liResult = -1;
  //  }
  //}

  // Sending request to DISABLE SS7 SCCP & TCAP Layers for FailedNode 
  // and stack on takeover node shall automatically change self 
  // TCAP and SCCP layer to ACTIVE mode from STANDBY mode.
  // No need to send explicit ENABLE request for these layes
  // on takeover node
  logger.logMsg (ALWAYS_FLAG, 0,
    "handlePeerFailure()::Sending request to DISABLE SCCP & TCAP Layers for "
		" Failed Node");

  lpQMsg = new INGwSmQueueMsg;
  memset ((void *)lpQMsg, 0, sizeof (INGwSmQueueMsg));
  mpSmDistributor->ss7SigtranStackRespPending = 1;

  lpQMsg->mSrc = BP_AIN_SM_SRC_EMS;
  lpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_PEERFAILED;
  lpQMsg->t.stackData.cmdType = DISABLE_NODE;
  lpQMsg->t.stackData.req.cmd_type= DISABLE_NODE;
  lpQMsg->t.stackData.req.u.sgNode.entId = ENT_INC_SCCP_TCAP;
  lpQMsg->t.stackData.stackLayer = BP_AIN_SM_SG_LAYER;
  lpQMsg->t.stackData.procId = SFndProcId();
	lpQMsg->t.stackData.req.u.sgNode.procId= 
											INGwSmBlkConfig::getInstance().m_peerProcId;

  lpQMsg->t.stackData.miRequestId = liRequestId;

	g_dumpMsg("SmWrapper", __LINE__, "DISABLE SCCP TCAP NODE Posted");

  //post the message to the Distributor
  if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "In change state to stopped, postMsg failed");
    delete lpQMsg;
    return -1;
  }

  //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {   
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
	  g_dumpMsg("SmWrapper", __LINE__, "DISABLE SCCP TCAP Done");
  }
	else {
		logger.logMsg(ERROR_FLAG, 0, "DISABLE SCCP & TCAP Failed in blockOperation. Timedout Failed");
		g_dumpMsg("SmWrapper", __LINE__, 
		"DISABLE SCCP TCAP NODE Done, Timedout Failed");
	}


  logger.logMsg (ALWAYS_FLAG, 0,
    "handlePeerFailure()::Sending request to DISABLE TUCL, SCTP and M3UA Layers for "
		" Failed Node");

  if ( (nodeType == TRANSPORT_TYPE_SIGTRAN) ||
       (nodeType == TRANSPORT_TYPE_BOTH)) 
	{
  	lpQMsg = new INGwSmQueueMsg;
    memset ((void *)lpQMsg, 0, sizeof (INGwSmQueueMsg));
    mpSmDistributor->ss7SigtranStackRespPending = 1;

    lpQMsg->mSrc 						    	   = BP_AIN_SM_SRC_EMS;
    lpQMsg->t.stackData.mOpType 	   = BP_AIN_SM_CCMOP_PEERFAILED;
    lpQMsg->t.stackData.cmdType 	   = DISABLE_NODE;
    lpQMsg->t.stackData.req.cmd_type = DISABLE_NODE;
    lpQMsg->t.stackData.req.u.sgNode.entId = ENT_INC_M3UA_SCTP_TUCL;
    lpQMsg->t.stackData.stackLayer 				 = BP_AIN_SM_SG_LAYER;
    lpQMsg->t.stackData.procId 						 = SFndProcId();
	  lpQMsg->t.stackData.req.u.sgNode.procId= 
	  								INGwSmBlkConfig::getInstance().m_peerProcId;
    lpQMsg->t.stackData.miRequestId = liRequestId;

	  g_dumpMsg("SmWrapper", __LINE__, "DISABLE TUCL, SCTP and M3UA Posted");

    //post the message to the Distributor
    if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "In change state to stopped, postMsg failed");
      delete lpQMsg;
      return -1;
    }

    //block the current thread
    if (blockOperation (liRequestId, 100) == BP_AIN_SM_OK)
    {   
      INGwSmBlockingContext *lpContext = 0;
      if (removeBlockingContext (liRequestId, lpContext))
      {
        if (lpContext->returnValue != BP_AIN_SM_OK)
          liResult = -1;

        delete lpContext;
      }
      else
        liResult = -1;

	    g_dumpMsg("SmWrapper", __LINE__, "DISABLE TUCL, SCTP and M3UA Done");
  }
		else {
			logger.logMsg(ERROR_FLAG, 0, "DISABLE TUCL, SCTP and M3UA Failed in blockOperation. Timedout Failed");
			g_dumpMsg("SmWrapper", __LINE__, 
			"DISABLE TUCL, SCTP and M3UA Done, Timedout Failed");
		}
  }


  logger.logMsg (ALWAYS_FLAG, 0,
    "handlePeerFailure()::Sending request to DISABLE MTP3 Layer for "
		" Failed Node");

  if ( (nodeType == TRANSPORT_TYPE_MTP) ||
       (nodeType == TRANSPORT_TYPE_BOTH)) 
	{
  	lpQMsg = new INGwSmQueueMsg;
    memset ((void *)lpQMsg, 0, sizeof (INGwSmQueueMsg));
    mpSmDistributor->ss7SigtranStackRespPending = 1;

    lpQMsg->mSrc 						    	   = BP_AIN_SM_SRC_EMS;
    lpQMsg->t.stackData.mOpType 	   = BP_AIN_SM_CCMOP_PEERFAILED;
    lpQMsg->t.stackData.cmdType 	   = DISABLE_NODE;
    lpQMsg->t.stackData.req.cmd_type = DISABLE_NODE;
    lpQMsg->t.stackData.req.u.sgNode.entId = ENT_INC_MTP3;
    lpQMsg->t.stackData.stackLayer 				 = BP_AIN_SM_SG_LAYER;
    lpQMsg->t.stackData.procId 						 = SFndProcId();
	  lpQMsg->t.stackData.req.u.sgNode.procId= 
	  								INGwSmBlkConfig::getInstance().m_peerProcId;
    lpQMsg->t.stackData.miRequestId = liRequestId;

	  g_dumpMsg("SmWrapper", __LINE__, "DISABLE MTP3 Posted");

    //post the message to the Distributor
    if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "In change state to stopped, postMsg failed");
      delete lpQMsg;
      return -1;
    }

    //block the current thread
    if (blockOperation (liRequestId, 100) == BP_AIN_SM_OK)
    {   
      INGwSmBlockingContext *lpContext = 0;
      if (removeBlockingContext (liRequestId, lpContext))
      {
        if (lpContext->returnValue != BP_AIN_SM_OK)
          liResult = -1;

        delete lpContext;
      }
      else
        liResult = -1;

	    g_dumpMsg("SmWrapper", __LINE__, "DISABLE MTP3 Done");
    }
		else {
			logger.logMsg(ERROR_FLAG, 0, "DISABLE MTP3 Failed in blockOperation. Timedout Failed");
			g_dumpMsg("SmWrapper", __LINE__, "DISABLE MTP3 Done, Timedout Failed");
		}
  }
  


 if ((nodeType == TRANSPORT_TYPE_SIGTRAN)||(nodeType == TRANSPORT_TYPE_BOTH))	
 {
   logger.logMsg (ALWAYS_FLAG, 0,
      "handlePeerFailure()::Sending request to DISABLE SIGTRAN Links on "
			"FailedNode");

    int waitCounter = 0;
    M3uaAspActSeq* asp = INGwSmBlkConfig::getInstance().getPeerActvAspList();
    int alarmCount = (*asp).size();
    if(alarmCount == 0){
      logger.logMsg (ALWAYS_FLAG, 0,"All the alarms came");
    }
    while(alarmCount != 0)
    {
      waitCounter++;
      if(waitCounter > miSleepCount)
      {
        logger.logMsg (ALWAYS_FLAG, 0,
          "handlePeerFailure()::Wait counter has expired,"
          "counter val reached to :%d", waitCounter);
        break;
      }
      logger.logMsg (ALWAYS_FLAG, 0,
        "handlePeerFailure()::Waiting for <%d> ASP Down alarm more",
			  alarmCount);

      usleep(miSleepTime*1000);//5 millisecs
      alarmCount = (*asp).size();
    }


    // Fetch List of Enpoint Vs ProcId
		M3uaAssocUpSeq* assoc = INGwSmBlkConfig::getInstance().
																getPeerM3uaAssocUpList();
    bool dontAbrt = false;
		std::ostringstream msg1;
		msg1 << "DISABLE SIGTRAN Links:[" << (*assoc).size() << "] "; 

		g_dumpMsg("SmWrapper", __LINE__, msg1);

    for(int i=0; i < (*assoc).size(); ++i)
    {
  	  for(int j=0; j < (*asp).size(); ++j)
		  {
        if(((*assoc)[i].pspId == (*asp)[j].pspId) && 
             ((*assoc)[i].m3uaLsapId == (*asp)[j].m3uaLsapId))
        {
          logger.logMsg (ALWAYS_FLAG, 0, 
          "handlePeerFailure()::ASPDn not received for pspId<%d> and lSapId<%d> ",
            (*assoc)[i].pspId,(*assoc)[i].m3uaLsapId);
          dontAbrt = true;
          break;
        }
      }

      if(!dontAbrt)
      {
			  std::ostringstream msg1;
			  std::ostringstream msg2;

        logger.logMsg (ALWAYS_FLAG, 0, 
          "handlePeerFailure()::ABORTING SIGTRAN Association (ASSOC_ABRT) "
				  "EndPtId[%d] PSPId[%d]", (*assoc)[i].endPointId, (*assoc)[i].pspId);

        liResult = G_SUCCESS;
  		  lpQMsg = new INGwSmQueueMsg;
        memset ((void *)lpQMsg, 0, sizeof (INGwSmQueueMsg));
        mpSmDistributor->ss7SigtranStackRespPending = 1;

        lpQMsg->mSrc                		 = BP_AIN_SM_SRC_EMS;
        lpQMsg->t.stackData.mOpType      = BP_AIN_SM_CCMOP_PEERFAILED;
        lpQMsg->t.stackData.cmdType      = ASSOC_ABRT;
        lpQMsg->t.stackData.req.cmd_type = ASSOC_ABRT;
        lpQMsg->t.stackData.stackLayer   = BP_AIN_SM_M3U_LAYER;
        lpQMsg->t.stackData.procId 			 = SFndProcId();
        lpQMsg->t.stackData.miRequestId  = liRequestId;

        M3uaAssocAbort *assocAbort = &(lpQMsg->t.stackData.req.u.m3uaAssocAbort);
  
        assocAbort->pspId      = (*assoc)[i].pspId;
        assocAbort->abrtFlag = TRUE;
        assocAbort->m3uaLsapId = (*assoc)[i].m3uaLsapId; // Not Used by stack
  
		    msg1 << " ASSOC Abort pspId: "<<(*assoc)[i].pspId << " EpId:"
				 << (*assoc)[i].endPointId << " m3uaLsapId: " << 
					(*assoc)[i].m3uaLsapId << " abrtFlag: TRUE" << " Posted";
     

		    g_dumpMsg("SmWrapper", __LINE__, msg1);


        //post the message to the Distributor
        if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "In change state to stopped, postMsg failed");
          delete lpQMsg;
          return -1;
        }

        //block the current thread
        if (blockOperation (liRequestId) == BP_AIN_SM_OK)
        {   
          INGwSmBlockingContext *lpContext = 0;
          if (removeBlockingContext (liRequestId, lpContext))
          {
            if (lpContext->returnValue != BP_AIN_SM_OK)
              liResult = -1;

            delete lpContext;
          }
          else
            liResult = -1;
  
			    g_dumpMsg("SmWrapper", __LINE__, "ASSOC_ABRT Done");
        }
		    else {
				  logger.logMsg(ERROR_FLAG, 0, 
												"ASSOC_ABRT Failed in blockOperation");
				  g_dumpMsg("SmWrapper", __LINE__, 
													  "ASSOC_ABRT Done, Timedout Failed");
        }

        //logger.logMsg (ALWAYS_FLAG, 0, 
        //     "handlePeerFailure()::Waiting for ASSOC_ABRT Alarm. Sleep 2 secs...");
        //sleep(2);
  
        // Unbind the SCT SAP
        logger.logMsg (ALWAYS_FLAG, 0, 
          "handlePeerFailure():: UNBIND SCTSAP m3uaLSapId <%d>",
				  (*assoc)[i].m3uaLsapId);
  
        liResult = G_SUCCESS;
  		  lpQMsg = new INGwSmQueueMsg;
        memset ((void *)lpQMsg, 0, sizeof (INGwSmQueueMsg));
        mpSmDistributor->ss7SigtranStackRespPending = 1;
  
        lpQMsg->mSrc                		 = BP_AIN_SM_SRC_EMS;
        lpQMsg->t.stackData.mOpType      = BP_AIN_SM_CCMOP_PEERFAILED;
        lpQMsg->t.stackData.cmdType      = UNBIND;
        lpQMsg->t.stackData.req.cmd_type = UNBIND;
        lpQMsg->t.stackData.stackLayer   = BP_AIN_SM_M3U_LAYER;
        lpQMsg->t.stackData.procId 			 = SFndProcId();
        lpQMsg->t.stackData.miRequestId  = liRequestId;
  
        UnbindSap *ubndSap = &(lpQMsg->t.stackData.req.u.unbindSap);
  
        ubndSap->sapId = (*assoc)[i].m3uaLsapId;
  
 		    msg2 << "UNBIND SCTSAP m3uaLsapId: "<<
 					(*assoc)[i].m3uaLsapId << " Posted";
      

 		    g_dumpMsg("SmWrapper", __LINE__, msg2);

        //post the message to the Distributor
        if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "In change state to stopped, postMsg failed");
          delete lpQMsg;
          return -1;
        }

        //block the current thread
        if (blockOperation (liRequestId) == BP_AIN_SM_OK)
        {   
          INGwSmBlockingContext *lpContext = 0;
          if (removeBlockingContext (liRequestId, lpContext))
          {
            if (lpContext->returnValue != BP_AIN_SM_OK)
              liResult = -1;
  
            delete lpContext;
          }
          else
            liResult = -1;

 			    g_dumpMsg("SmWrapper", __LINE__, "UNBIND SCTSAP Done");
        }
			  else {
			    logger.logMsg(ERROR_FLAG, 0, 
					  "UNBIND SCTSAP Failed in blockOperation. Timedout Failed");
				  g_dumpMsg("SmWrapper", __LINE__, 
									  "UNBIND SCTSAP Done, Timedout Failed");
        }
  
        //logger.logMsg (ALWAYS_FLAG, 0, 
        //     "handlePeerFailure()::Waiting for UNBIND SCT SAP Alarm. Sleep 2 secs...");

        //sleep(2);
        }
        dontAbrt = false; 
   	  } // End for loop disable sigtran links
    //}
 	}

	// Disbaling Links pertaining to peer
	LnkSeq* peerLinks = INGwSmBlkConfig::getInstance().getPeerLinkList();

	{ // Scoping is needed for ostringstream to flush data
		std::ostringstream msg1;
		msg1 << "DISABLE SS7 Link(" << (*peerLinks).size() << ")";
		g_dumpMsg("SmWrapper", __LINE__, msg1);
	}

	for(int i=0; i < (*peerLinks).size(); ++i)
	{
		std::ostringstream msg1;
		std::ostringstream msg2;

		liResult = G_SUCCESS;
    lpQMsg = new INGwSmQueueMsg;
    memset ((void *)lpQMsg, 0, sizeof (INGwSmQueueMsg));
    mpSmDistributor->ss7SigtranStackRespPending = 1;

		lpQMsg->mSrc 								= BP_AIN_SM_SRC_EMS;
    lpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_PEERFAILED;
    lpQMsg->t.stackData.cmdType = DISABLE_LINK;
    lpQMsg->t.stackData.req.cmd_type = DISABLE_LINK;
    lpQMsg->t.stackData.stackLayer   = BP_AIN_SM_MTP3_LAYER;
    lpQMsg->t.stackData.procId 		   = SFndProcId();
    lpQMsg->t.stackData.miRequestId  = liRequestId;

		lpQMsg->t.stackData.req.u.lnkDisable.lnkId 		 = (*peerLinks)[i].lnkId;
    lpQMsg->t.stackData.req.u.lnkDisable.lnkSetId  = (*peerLinks)[i].lnkSetId;
    lpQMsg->t.stackData.req.u.lnkDisable.procId 	 = (*peerLinks)[i].mtp2ProcId;
    lpQMsg->t.stackData.req.u.lnkDisable.mtp2UsapId= (*peerLinks)[i].mtp2UsapId;
    lpQMsg->t.stackData.req.u.lnkDisable.mtp3LsapId= (*peerLinks)[i].mtp3LsapId;

		msg1 << "SS7 Link linkId:"<<lpQMsg->t.stackData.req.u.lnkDisable.lnkId
				<< " lnkSetId: " << lpQMsg->t.stackData.req.u.lnkDisable.lnkSetId
				<< " procId: " << lpQMsg->t.stackData.req.u.lnkDisable.procId
				<< " mtp2UsapId: " << lpQMsg->t.stackData.req.u.lnkDisable.mtp2UsapId
				<< " mtp3LsapId: " << lpQMsg->t.stackData.req.u.lnkDisable.mtp3LsapId;

		msg2 << msg1;
		msg1 << " Posted";
		g_dumpMsg("SmWrapper", __LINE__, msg1);

    //post the message to the Distributor
		if (mpSmDistributor->postMsg (lpQMsg) != BP_AIN_SM_OK)
		{
			logger.logMsg (ERROR_FLAG, 0,
          "In change state to stopped, postMsg failed");
      delete lpQMsg;
      return -1;
    }

		//block the current thread
		if (blockOperation (liRequestId) == BP_AIN_SM_OK)
		{
				INGwSmBlockingContext *lpContext = 0;
        if (removeBlockingContext (liRequestId, lpContext))
        {
          if (lpContext->returnValue != BP_AIN_SM_OK)
						liResult = -1;

           delete lpContext;
				}
				else
					liResult = -1;
		}
		else {
			logger.logMsg(ERROR_FLAG, 0, "DISABLE SS7 Links Failed in blockOperation. Timedout Failed");
			g_dumpMsg("SmWrapper", __LINE__, "DISABLE SS7 Link Timeout. Timedout Failed");
		}

		msg2 << " Done";
		g_dumpMsg("SmWrapper", __LINE__, msg2);
	}

  setAllowCfgFromEms(true,
         (char*)"INGwSmWrapper::handlePeerFailure()", __LINE__);

#ifdef INC_DLG_AUDIT
	INGwIfrMgrRoleMgr*  m_RoleManager;
	m_RoleManager = &INGwIfrMgrRoleMgr::getInstance();
	if(m_RoleManager->getSbyToActive())
	{
  	char* lpcDoAudit = getenv("INGW_PERFORM_AUDIT");
  	if(NULL == lpcDoAudit) {
    	//int liDoAudit = atoi(lpcDoAudit);
    	//if(1 == liDoAudit) {
     	INGwTcapProvider::getInstance().auditTcapDlg();
    	//}
    	//else{
      //logger.logMsg(ALWAYS_FLAG,0,"Audit is disabled");
    	//}
  	}
  	else
  	{
    	logger.logMsg(ALWAYS_FLAG,0,"INGW_PERFORM_AUDIT"
    	" is defined, Not executing Audit");
  	}
	}
#else
  	logger.logMsg(ALWAYS_FLAG,0,"INC_DLG_AUDIT not defined");
#endif /*INC_DLG_AUDIT*/	

  logger.logMsg (ALWAYS_FLAG, 0, "Leaving INGwSmWrapper::handlePeerFailure");
  return liResult;
}

/******************************************************************************
*
*     Fun:   blockOperation()
*
*     Desc:  block the current thread
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int
INGwSmWrapper::blockOperation (int aiRequestId, int  timeOut)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::blockOperation, contextId:%d", aiRequestId);

  /*
   * Use the thread Id for request Id since it will be unique
   */
  int liRequestId = aiRequestId;

  INGwSmBlockingContext *lpBlkCtx = new INGwSmBlockingContext;

  lpBlkCtx->requestId = liRequestId;

  /*
   * Add the blocking context into the map and check the request Id 
   * whenever the thread is unblocked. This is needed since there can
   * be some condition where the signal might unblock the thread.
   */

	 struct timespec waittime;
	 waittime.tv_nsec = 0;
	 waittime.tv_sec  = time(NULL) + timeOut;

  if (addBlockingContext (lpBlkCtx->requestId, lpBlkCtx))
  {
    pthread_mutex_lock (&mCvLock);

    while (mUnblockedRequestId != lpBlkCtx->requestId)
		{
      if(ETIMEDOUT == pthread_cond_timedwait (&mUnblock, &mCvLock, &waittime))
			{
				logger.logMsg(ERROR_FLAG, 0, 
				"blockOperation: Request:%d Timeout. No response received from stack",
				liRequestId);

				cleanUpOnTimeOut(liRequestId);			
    		pthread_mutex_unlock (&mCvLock);
    		logger.logMsg (TRACE_FLAG, 0, 
											"Leaving INGwSmWrapper::blockOperation, timedout");
    		return BP_AIN_SM_FAIL;
			}
		}

    mUnblockedRequestId = -1;

    pthread_mutex_unlock (&mCvLock);

    logger.logMsg (TRACE_FLAG, 0,
      "Leaving INGwSmWrapper::blockOperation");
    return BP_AIN_SM_OK;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::blockOperation");

  return BP_AIN_SM_FAIL;
}


/******************************************************************************
*
*     Fun:   continueOperation()
*
*     Desc:  operation to be invoked by INGwSmDistributor to unblock the thread
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int
INGwSmWrapper::continueOperation (INGwSmBlockingContext* apBlkCtx)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::continueOperation");


  if (apBlkCtx != 0 && apBlkCtx->requestId == -1)
  {
    logger.logMsg (WARNING_FLAG, 0,
      "The Blocking context is invalid <%d>. The operation might have been initiated by Stack Manager itself instead of CCM.", apBlkCtx->requestId);

    return BP_AIN_SM_FAIL;
  }

  if (updateBlockingContext (apBlkCtx->requestId, apBlkCtx))
  {
    //signal the blocking thread to proceed
    pthread_mutex_lock (&mCvLock);
    mUnblockedRequestId = apBlkCtx->requestId;
    pthread_cond_broadcast (&mUnblock);
    pthread_mutex_unlock (&mCvLock);

    logger.logMsg (TRACE_FLAG, 0,
      "Leaving INGwSmWrapper::continueOperation");
    return BP_AIN_SM_OK;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::continueOperation failed, contextId:%d", 
		apBlkCtx->requestId );
  return BP_AIN_SM_FAIL;
}


/******************************************************************************
*
*     Fun:   addBlockingContext()
*
*     Desc:  add the blocking context in the map
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int
INGwSmWrapper::addBlockingContext (int aiRequestId, 
                                    INGwSmBlockingContext *apBlockingContext)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::addBlockingContext");


  if (!apBlockingContext)
    return BP_AIN_SM_FAIL;

  INGwSmBlockingMap::iterator leMapIter;

  pthread_mutex_lock (&mMapLock);

  leMapIter = mRequestBlockingMap.find (aiRequestId);

  if (leMapIter != mRequestBlockingMap.end())
  {
    pthread_mutex_unlock (&mMapLock);
    return BP_AIN_SM_FAIL;
  }

  mRequestBlockingMap [aiRequestId] =
                               apBlockingContext;

  pthread_mutex_unlock (&mMapLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::addBlockingContext");
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   updateBlockingContext()
*
*     Desc:  update the blocking context
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int
INGwSmWrapper::updateBlockingContext (int aiRequestId,
                                       INGwSmBlockingContext *apBlockingContext)
{ 
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::updateBlockingContext");


  if (!apBlockingContext)
    return BP_AIN_SM_FAIL;

  INGwSmBlockingMap::iterator leMapIter;
  
  pthread_mutex_lock (&mMapLock);
  
  leMapIter = mRequestBlockingMap.find (aiRequestId);
  
  if (leMapIter == mRequestBlockingMap.end())
  { 
    pthread_mutex_unlock (&mMapLock);
		logger.logMsg(ERROR_FLAG, 0, "updateBlockingContext: ContextID:%d Not FOund",
		aiRequestId);
    return BP_AIN_SM_FAIL;
  }

  INGwSmBlockingContext *lpBlkCtx = 
                              leMapIter->second;

  if (!lpBlkCtx)
  { 
    pthread_mutex_unlock (&mMapLock);
    return BP_AIN_SM_FAIL;
  }
  
  //set the correct return value into the Blocking Context
  lpBlkCtx->returnValue = apBlockingContext->returnValue;
  lpBlkCtx->status      = apBlockingContext->status     ;
  memcpy(&lpBlkCtx->resp, &apBlockingContext->resp, 
																						sizeof(Ss7SigtranStackResp));

  pthread_mutex_unlock (&mMapLock);
  
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving  INGwSmWrapper::updateBlockingContext");
  return BP_AIN_SM_OK;                 
}

/******************************************************************************
*
*     Fun:   removeBlockingContext()
*
*     Desc:  remove the blocking context
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int
INGwSmWrapper::removeBlockingContext (int aiRequestId,
                                       INGwSmBlockingContext *(&apBlockingContext))
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::removeBlockingContext");


  INGwSmBlockingMap::iterator leMapIter;

  pthread_mutex_lock (&mMapLock);

  leMapIter = mRequestBlockingMap.find (aiRequestId);

  if (leMapIter == mRequestBlockingMap.end())
  {
    pthread_mutex_unlock (&mMapLock);
    logger.logMsg (ERROR_FLAG, 0, 
           "Unlock RequestId[%d] not found", aiRequestId);
    return BP_AIN_SM_FAIL;
  }

  apBlockingContext = leMapIter->second;

  mRequestBlockingMap.erase (leMapIter);

  pthread_mutex_unlock (&mMapLock);

  if (apBlockingContext == 0) {
    logger.logMsg (ERROR_FLAG, 0, 
           "BlockingContext for RequestId[%d] is NULL", aiRequestId);
    return BP_AIN_SM_FAIL;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::removeBlockingContext");
  return BP_AIN_SM_OK;
}

/* This command is used for configuring SS7 information on stack. 
 * This is called when configuring through EMS, whne INC comes up
 * for first time and fetches information from EMS, and when peer
 * comes UP.
 * from: 0= indicates that it is being called from EMS. So data 
 *					will get added/deleted to/from BulkConfig
 *       1= indicates that it is called from SS7 fetch. This is 
 *          called first time when system comes up. This data will
 *			    also get stored in BulkConfig
 *			 2= indicates that it is being called for peer configuration
 */
int 
INGwSmWrapper::processEmsReq(Ss7SigtranSubsReq *req, 
														 Ss7SigtranStackResp *&resp, int from)
{
  int                 pspId_m3uaSapid = 0;
  int 							  retVal = BP_AIN_SM_OK;
	char  							outParam[2000];
	int   							outLen       =	0;
	bool								isCmdSuccess = false;
	bool								isCmdSuccess1 = false;
	bool								isCmdSuccess2 = false;
	bool								isCmdSuccess3 = false;
	bool								isCmdSuccess4 = false;
	EnableUserPart      *enaUP       = NULL;
	DisableUserPart      *disUP       = NULL;
	LinkEnable 					*enaLink     = NULL;
	SsnEnable 					*enaSsn      = NULL;
	SsnDisable 					*disSsn      = NULL;
	Ss7SigtranStackResp *resp1       = NULL;
	Ss7SigtranStackResp *resp2       = NULL;
	Ss7SigtranStackResp *resp3       = NULL;
	Ss7SigtranStackResp *resp4       = NULL;
  vector<int>         lprocIdList;

	if (req->cmd_type != GET_STATS) {
    logger.logMsg (ALWAYS_FLAG, 0,
        "Entering INGwSmWrapper::processEmsReq <cmdType><%d> from<%d>",
        req->cmd_type, from);
  }

  INGwSmBlkConfig & blkCfg = INGwSmBlkConfig::getInstance();

  if ((from == 0) && 
      (!getAllowCfgFromEms((char *)"INGwSmWrapper::processEmsReq()", __LINE__))) 
  {
  	logger.logMsg (ALWAYS_FLAG, 0,
           "Stack cfg in progress, Cfg through EMS not allowed");
	  resp = new Ss7SigtranStackResp;
    resp->procId     = SFndProcId();
    resp->status     = FAILURE_RESPONSE;
    resp->reason     = FAILURE_REASON;
    resp->reasonStr  = (char*) "Stack cfg in progress, Try after sometime.";
    resp->stackLayer = (char*)"NOT_APPLICABLE";
		return retVal;
  }

  
	// if myRole is Standby then simple
	// add to inMemory
	if(INGwTcapProvider::getInstance().myRole() == TCAP_SECONDARY)
	{
		INGwSmBlkConfig::getInstance().updateNode(req, from);
  	logger.logMsg (ALWAYS_FLAG, 0,
      "Leaving INGwSmWrapper::processEmsReq <cmdType><%d>, Standby", 
			req->cmd_type);
		return retVal;
	}

	std::ostringstream msg;
	msg << "processEmsReq[From->" << 
	string((from==0)?"EMS":(from==1)?"Fetch":"BulkConfig");
	msg << "] ";

	// get ProcIdList based on Active Procs only if configuration 
	// is through EMS or fetchSS7 list else peerProcId
	if(from == 2)
		INGwSmBlkConfig::getInstance().getPeerProcId(lprocIdList);
	else
		INGwSmBlkConfig::getInstance().getActiveProcIds(lprocIdList);
  
	switch(req->cmd_type)
  {
    case ADD_NETWORK:
		{
        retVal = cliAddNetwork(&(req->u.addNwk), &resp,lprocIdList);

				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
						isCmdSuccess = true;
				}
        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ADD_NETWORK, retVal:%d,resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< " Add Network: nwId: "<< req->u.addNwk.nwId << " status: "
	 			<< string((isCmdSuccess == true)?"Success, ProcId:":"Failure, ProcId");
	 			msg << lprocIdList[0] << "-"; 

				if(lprocIdList.size() == 2)
					msg << lprocIdList[1];
		}
    break;

    case ENABLE_LINK:
		{
        retVal = cliEnaLnk(&(req->u.lnkEnable), &resp);

				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
						isCmdSuccess = true;
				}
        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ENABLE_LINK, retVal:%d,resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< " Enable Link: LnkId: "<< req->u.lnkEnable.lnkId << " status: "
	 			<< string((isCmdSuccess == true)?"Success, ProcId:":"Failure, ProcId");
	 			msg << lprocIdList[0] << "-"; 

				if(lprocIdList.size() == 2)
					msg << lprocIdList[1];
		}
    break;

    case DISABLE_LINK:
		{
        retVal = cliDisableLnk(&(req->u.lnkDisable), &resp);

				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
						isCmdSuccess = true;
				}
        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:DISABLE_LINK, retVal:%d,resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< " Disable Link: LnkId: "<< req->u.lnkDisable.lnkId << " status: "
	 			<< string((isCmdSuccess == true)?"Success, ProcId:":"Failure, ProcId");
	 			msg << lprocIdList[0] << "-"; 

				if(lprocIdList.size() == 2)
					msg << lprocIdList[1];
		}
    break;

    case ADD_LINKSET:
    {
        retVal = cliAddLinkSet(&(req->u.lnkSet), &resp,lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
						isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ADD_LINKSET, retVal:%d,resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< " Add Linkset: lnkSetId: "<< req->u.lnkSet.lnkSetId << 
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 

				if(lprocIdList.size() == 2)
					msg << lprocIdList[1];
    }
    break;

    case ADD_USERPART:
    {
				int status = -1;

				req->u.addUserPart.currentUserState = 0;

        retVal = cliAddUserPart(&(req->u.addUserPart), &resp, lprocIdList);
				if (resp != NULL) 
				{
					status = resp->status;
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
						isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ADD_USERPART, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg << " Add UserPart: "<< 
string((req->u.addUserPart.userPartType == MTP3_USER)?"MTP3_USER":"M3UA_USER");
				msg << " m3uaUsapId: " << req->u.addUserPart.m3uaUsapId <<
						" mtp3UsapId: " << req->u.addUserPart.mtp3UsapId <<
						" sccpLsapId: " << req->u.addUserPart.sccpLsapId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 

				if(lprocIdList.size() == 2)
					msg << lprocIdList[1];

				// Enable User Part if status is successful
				if(status == 0) {
  				enaUP = new EnableUserPart();
  				cmMemset((U8 *)enaUP, 0, sizeof(EnableUserPart));

					// currently using user state for passing
					// user id as there if no placeholder in AddUserPart
					// for user id.
  				enaUP->mtp3UsapId= req->u.addUserPart.mtp3UsapId;
  				enaUP->m3uaUsapId= req->u.addUserPart.m3uaUsapId;
  				enaUP->sccpLsapId= req->u.addUserPart.sccpLsapId;

					if(req->u.addUserPart.userPartType == MTP3_USER)
  					enaUP->nwkType = 1;
					else
  					enaUP->nwkType = 2;

	      	retVal = cliEnaUsrpart(enaUP, &resp1, lprocIdList);
					if (resp1 != NULL) 
					{
						outLen =0;
						outLen += sprintf(outParam+outLen, 
						"StkResp ProcId: %d, status: %d, Reason: %d,"
						" reasonStr:%s, stackLayer:%s", resp1->procId, 
						resp1->status, resp1->reason, 
						(resp1->reasonStr != NULL)?resp1->reasonStr:"NULL",
						(resp1->stackLayer != NULL)?resp1->stackLayer:"NULL");

						if(resp1->status == 0)
							isCmdSuccess = true;

						delete resp1;
					}

        	logger.logMsg (ALWAYS_FLAG, 0, 
					"INGwSmWrapper::processEmsReq cmdType:ENABLE_USERPART," 
					"retVal:%d, resp:%s",
					retVal, (outLen == 0)?"Not Received":outParam);

				  msg << "\n Enable UserPart: " << 
	    string((req->u.addUserPart.userPartType == MTP3_USER)?"MTP3_USER":"M3UA_USER");
				  msg << " m3uaUsapId: " << enaUP->m3uaUsapId <<
							   " mtp3UsapId: " << enaUP->mtp3UsapId <<
						     " sccpLsapId: " << enaUP->sccpLsapId <<
				 " status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 				msg << ", ProcId: " << lprocIdList[0] << "-"; 

					if(lprocIdList.size() == 2)
						msg << lprocIdList[1];

					delete enaUP;
				}

     }
     break;

    case DEL_NETWORK:
    {
        retVal = cliDelNetwork(&(req->u.delNwk), &resp,lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:DEL_NETWORK, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< " Delete Network: nwId: "<< req->u.delNwk.nwkId << " status: "
					 << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 

				if(lprocIdList.size() == 2)
					msg << lprocIdList[1];
    }
    break;

    case DEL_LINKSET:
    {
        retVal = cliDelLinkSet(&(req->u.delLnkSet), &resp,lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:DEL_LINKSET, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< " Delete Linkset: lnkSetId: "<< req->u.delLnkSet.lnkSetId << 
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 

				if(lprocIdList.size() == 2)
					msg << lprocIdList[1];
    }
    break;

    case DEL_LINK:
    {

	    retVal = cliDelLink(&(req->u.delLnk), &resp);
			if (resp != NULL) 
			{
			  outLen = 0;
			  outLen += sprintf(outParam+outLen, 
					  "StkResp ProcId: %d, status: %d, Reason: %d,"
					  " reasonStr:%s, stackLayer:%s", resp->procId, 
					  resp->status, resp->reason, 
					  (resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					  (resp->stackLayer != NULL)?resp->stackLayer:"NULL");
 
        if(resp->status == 0)
				  isCmdSuccess = true;
		  }
      logger.logMsg (ALWAYS_FLAG, 0, 
		  "INGwSmWrapper::processEmsReq cmdType:DEL_LINK, retVal:%d, resp:%s and mtp3Lsap<%d>,"
      "mtp2UsapId<%d>,lnkId<%d>,mtp2ProcId<%d>", retVal, (outLen == 0)?"Not Received":outParam,
      req->u.delLnk.mtp3LsapId,req->u.delLnk.mtp2UsapId,req->u.delLnk.lnkId,  req->u.delLnk.mtp2ProcId);

      msg << " Delete Link: "<< 
			msg << " mtp3LsapId: " << req->u.delLnk.mtp3LsapId <<
						" mtp2UsapId: " << req->u.delLnk.mtp2UsapId <<
						" lnkId: " << req->u.delLnk.lnkId <<
						" mtp2ProcId: " << req->u.delLnk.mtp2ProcId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 		msg << ", ProcId: " << lprocIdList[0] << "-"; 


			if(lprocIdList.size() == 2)
			  msg << lprocIdList[1];

    }
    break;

    case DEL_USR_PART:
    {
          int status = -1;
        // Disable User Part first and then delete Userpart
  				disUP = new DisableUserPart();
  				cmMemset((U8 *)disUP, 0, sizeof(DisableUserPart));

  				disUP->mtp3UsapId= req->u.delUserPart.mtp3UsapId;
  				disUP->m3uaUsapId= req->u.delUserPart.m3uaUsapId;
  				disUP->sccpLsapId= req->u.delUserPart.sccpLsapId;

					if(req->u.delUserPart.userPartType == MTP3_USER)
  					disUP->nwkType = 1;
					else
  					disUP->nwkType = 2;

	      	retVal = cliDisableUsrpart(disUP, &resp1, lprocIdList);
					if (resp1 != NULL) 
					{
            status = resp1->status;
						outLen += sprintf(outParam+outLen, 
						"StkResp ProcId: %d, status: %d, Reason: %d,"
						" reasonStr:%s, stackLayer:%s", resp1->procId, 
						resp1->status, resp1->reason, 
						(resp1->reasonStr != NULL)?resp1->reasonStr:"NULL",
						(resp1->stackLayer != NULL)?resp1->stackLayer:"NULL");

						if(resp1->status == 0)
							isCmdSuccess = true;

						delete resp1;
					}


        	logger.logMsg (ALWAYS_FLAG, 0, 
					"INGwSmWrapper::processEmsReq cmdType:DISABLE_USERPART," 
					"retVal:%d, resp:%s",
					retVal, (outLen == 0)?"Not Received":outParam);

				  msg << "\n Disable UserPart: " << 
	        string((req->u.delUserPart.userPartType == MTP3_USER)?"MTP3_USER":"M3UA_USER");
				  msg << " m3uaUsapId: " << disUP->m3uaUsapId <<
							   " mtp3UsapId: " << disUP->mtp3UsapId <<
						     " sccpLsapId: " << disUP->sccpLsapId <<
				 " status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 				msg << ", ProcId: " << lprocIdList[0] << "-"; 

					if(lprocIdList.size() == 2)
						msg << lprocIdList[1];

				if(status == 0) {
          if(req->u.delUserPart.userPartType == MTP3_USER)
          {
	      	  retVal = cliUnbndSapMtp3(disUP, &resp2, lprocIdList);
					  if (resp2 != NULL) 
					  {
              status = resp2->status;
						  /*outLen = 0;
              outLen += sprintf(outParam+outLen, 
						  "StkResp ProcId: %d, status: %d, Reason: %d,"
						  " reasonStr:%s, stackLayer:%s", resp->procId, 
						  resp2->status, resp2->reason, 
						  (resp2->reasonStr != NULL)?resp2->reasonStr:"NULL",
						  (resp2->stackLayer != NULL)?resp2->stackLayer:"NULL");*/
  
						  if(resp2->status == 0)
							  isCmdSuccess = true;
  
						  delete resp2;
					  }


        	  logger.logMsg (ALWAYS_FLAG, 0, 
					  "INGwSmWrapper::processEmsReq cmdType:DISABLE_SAPS," 
					  "retVal:%d, resp:%s",
					  retVal, (outLen == 0)?"Not Received":outParam);

				    msg << "\n Disable Saps: " << 
	          string((req->u.delUserPart.userPartType == MTP3_USER)?"MTP3_USER":"M3UA_USER");
				    msg << " m3uaUsapId: " << disUP->m3uaUsapId <<
							   " mtp3UsapId: " << disUP->mtp3UsapId <<
						     " sccpLsapId: " << disUP->sccpLsapId <<
				    " status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 				  msg << ", ProcId: " << lprocIdList[0] << "-"; 

				    if(lprocIdList.size() == 2)
					    msg << lprocIdList[1];
          }


        if(disUP != NULL){
				  delete disUP;
          disUP = NULL;
        }

       }

				if(status == 0) {
        retVal = cliDelUsrPart(&(req->u.delUserPart), &resp,lprocIdList);
				if (resp != NULL) 
				{
					outLen =0;
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:DEL_USR_PART, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

        msg << " Delete UserPart: "<< 
        string((req->u.delUserPart.userPartType == MTP3_USER)?"MTP3_USER":"M3UA_USER");
				msg << " m3uaUsapId: " << req->u.delUserPart.m3uaUsapId <<
						" mtp3UsapId: " << req->u.delUserPart.mtp3UsapId <<
						" sccpLsapId: " << req->u.delUserPart.sccpLsapId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 


				if(lprocIdList.size() == 2)
					msg << lprocIdList[1];
      }
    }
    break;

    case MODIFY_LINKSET:
    {
        retVal = cliModLinkSet(&(req->u.lnkSet), &resp, lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
						isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:MODIFY_LINKSET, retVal:%d," 
				"resp:%s", retVal, (outLen == 0)?"Not Received":outParam);
		}
    break; 

    case ADD_LINK:
    {
			int status = -1;
			// avoid adding a link for peer if the peer is not up. 
			// This might result in thread getting stuck.
			if(lprocIdList.size() == 1 && 
						lprocIdList[0] == INGwSmBlkConfig::getInstance().m_selfProcId)
			{
				if(req->u.lnk.mtp2ProcId == 
												INGwSmBlkConfig::getInstance().m_peerProcId)
				{
          if (from == 0) {
	          resp = new Ss7SigtranStackResp;
            resp->procId     = SFndProcId();
            resp->status     = FAILURE_RESPONSE;
            resp->reason     = FAILURE_REASON;
            resp->reasonStr  = (char*) "INC Node on which LINK is to be added is DOWN";
            resp->stackLayer = (char*)"NOT_APPLICABLE";
          }
          else {
					  isCmdSuccess = true;
          }
          
					break;
				}
			}

			// This is the case of PeerUP and we have not configured Links 
			// earlier for MTP2ProcId as Peer with stack Manager.
			// Configure all Links with MTP2ProcId as peer on both 51&52 procIds
			// and configure all Links with MTP2ProcId as self on 52 procId.
			if(from == 2 && !INGwSmBlkConfig::getInstance().isPeerEnabled())
			{
				if(req->u.lnk.mtp2ProcId == INGwSmBlkConfig::getInstance().m_peerProcId)	
						INGwSmBlkConfig::getInstance().getActiveProcIds(lprocIdList);
			}

			retVal = cliAddLink(&(req->u.lnk), &resp,lprocIdList);
            
			if (resp != NULL) 
			{
				status = resp->status;
				outLen += sprintf(outParam+outLen, 
				"StkResp ProcId: %d, status: %d, Reason: %d,"
				" reasonStr:%s, stackLayer:%s", resp->procId, 
				resp->status, resp->reason, 
				(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
				(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

				if(resp->status == 0)
						isCmdSuccess = true;
			}

      logger.logMsg (ALWAYS_FLAG, 0, 
			"INGwSmWrapper::processEmsReq cmdType:ADD_LINK, retVal:%d, resp:%s",
			retVal, (outLen == 0)?"Not Received":outParam);

			msg<< " Add Link: lnkId: "<< req->u.lnk.lnkId << 
						" lnkSetId: "<<req->u.lnk.lnkSetId <<
						" mtp2ProcId: " << req->u.lnk.mtp2ProcId <<
						" timeSlot: " << req->u.lnk.timeSlot <<
						" slc: " << req->u.lnk.slc <<
						" currentState: " << req->u.lnk.currentLinkState <<
						" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 		msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
			if(lprocIdList.size() == 2)
				msg << lprocIdList[1];

      if (req->u.lnk.currentLinkState == 1) {

			  if(status == 0 && from != 2) 
			  {
				  enaLink = new LinkEnable();
				  cmMemset((U8 *)enaLink, 0, sizeof(LinkEnable));
				  enaLink->lnkId 		  = req->u.lnk.lnkId;
				  enaLink->lnkSetId   = req->u.lnk.lnkSetId;
				  enaLink->procId 	  = req->u.lnk.mtp2ProcId;
				  enaLink->mtp2UsapId = req->u.lnk.mtp2UsapId;
				  enaLink->mtp3LsapId = req->u.lnk.mtp3LsapId;
  
	     	  retVal = cliEnaLnk(enaLink, &resp1);
				  if (resp1 != NULL) 
				  {
					  outLen = 0;
					  outLen += sprintf(outParam+outLen, 
					  "StkResp ProcId: %d, status: %d, Reason: %d,"
					  " reasonStr:%s, stackLayer:%s", resp1->procId, 
					  resp1->status, resp1->reason, 
					  (resp1->reasonStr != NULL)?resp1->reasonStr:"NULL",
					  (resp1->stackLayer != NULL)?resp1->stackLayer:"NULL");

				    if(resp1->status == 0)
				    		isCmdSuccess1 = true;
  
					  delete resp1;
				  }
  
       	  logger.logMsg (ALWAYS_FLAG, 0, 
				  "INGwSmWrapper::processEmsReq cmdType:ENABLE_LINK,"
					  "retVal:%d, resp:%s",
					  retVal, (outLen == 0)?"Not Received":outParam);
  
				  msg<< "\n Enable Link: lnkId: "<< enaLink->lnkId <<
							" lnkSetId: "<<enaLink->lnkSetId <<
							" procId: " << enaLink->procId  <<
							" status: " << string((isCmdSuccess1 == true)?"Success":"Failure");
	 		 	  msg << ", ProcId: " << lprocIdList[0] << "-"; 
			
				  if(lprocIdList.size() == 2)
					  msg << lprocIdList[1] << "\n";

				  delete enaLink;
			  }
      }
    }
    break;

    case MODIFY_LINK:
    {
        retVal = cliModLink(&(req->u.lnk), &resp, lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
						isCmdSuccess = true;
				}

        logger.logMsg (TRACE_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:MODIFY_LINK, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< " Modify Link: lnkId: "<< req->u.lnk.lnkId << 
						" lnkSetId: "<<req->u.lnk.lnkSetId <<
						" mtp2ProcId: " << req->u.lnk.mtp2ProcId <<
						" timeSlot: " << req->u.lnk.timeSlot <<
						" slc: " << req->u.lnk.slc <<
						" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
			if(lprocIdList.size() == 2)
				msg << lprocIdList[1];
    }
    break;

    case STA_LINK:
    {
        retVal = cliStatusLink(&(req->u.lnkstatus), &resp, lprocIdList);
				if (resp != NULL) 
				{
          outLen = 0;
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");
				}

        logger.logMsg (TRACE_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:STA_LINK, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

     }
    break;

    case STA_LINKSET:
    {
        retVal = cliStatusLnkSet(&(req->u.lnkStatus), &resp, lprocIdList);
				if (resp != NULL) 
				{
          outLen = 0;
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");
				}

        logger.logMsg (TRACE_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:STA_LINKSET, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);
     }
    break;

    case STA_ROUTE:
    {
        retVal = cliStatusRte(&(req->u.dpcStatus), &resp, lprocIdList);
				if (resp != NULL) 
				{
          outLen = 0;
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, "
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status,  
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");
				}

        logger.logMsg (TRACE_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:STA_ROUTE, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);
     }
    break;

    case STA_PS:
    {
        retVal = cliStatusPs(&(req->u.ps), &resp, lprocIdList);
				if (resp != NULL) 
				{
          outLen = 0;
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");
				}

        logger.logMsg (TRACE_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:STA_PS, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);
     }
    break;

    case STA_PSP:
    {
        retVal = cliStatusPsp(&(req->u.psp), &resp, lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");
				}

        logger.logMsg (TRACE_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:STA_PSP, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);
     }
    break;

		case INITIALIZE_ENA_LINK:
		{
			initializeNode(lprocIdList, 1);

			LnkSeq* lnkList = INGwSmBlkConfig::getInstance().getLinkList();
			int status = -1;

			for(int i=0; i < (*lnkList).size(); ++i)
			{
				enaLink = new LinkEnable();
				cmMemset((U8 *)enaLink, 0, sizeof(LinkEnable));
				enaLink->lnkId 		  = (*lnkList)[i].lnkId;
				enaLink->lnkSetId   = (*lnkList)[i].lnkSetId;
				enaLink->procId 	  = (*lnkList)[i].mtp2ProcId;
				enaLink->mtp2UsapId = (*lnkList)[i].mtp2UsapId;
				enaLink->mtp3LsapId = (*lnkList)[i].mtp3LsapId;

	    	retVal = cliEnaLnk(enaLink, &resp1);
				if (resp1 != NULL) 
				{
					outLen = 0;
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
						" reasonStr:%s, stackLayer:%s", resp1->procId, 
						resp1->status, resp1->reason, 
						(resp1->reasonStr != NULL)?resp1->reasonStr:"NULL",
						(resp1->stackLayer != NULL)?resp1->stackLayer:"NULL");

						delete resp1;
				}

     		logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:INITIALIZE_ENA_LINK,"
				"retVal:%d, resp:%s",
					retVal, (outLen == 0)?"Not Received":outParam);

				msg<< "\n Initialize & Enable Link: lnkId: "<< enaLink->lnkId <<
							" lnkSetId: "<<enaLink->lnkSetId <<
							" procId: " << enaLink->procId  <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 	  	msg << ", ProcId: " << lprocIdList[0] << "-"; 
				delete enaLink;
			}
		}
		break;

		case ENA_SGTRN_LYRS:
    {
			enaSgtrnLyrs(lprocIdList);
    }
    break;

    case ADD_ROUTE:
    {
        retVal = cliAddRoute(&(req->u.addRoute), &resp, lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ADD_ROUTE, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg << " Add Route for: "<< 
								(req->u.addRoute.swtch == -1)?"Self":"Remote";
				msg << " dpc: "<< req->u.addRoute.dpc <<
							" preferredOpc: "<<req->u.addRoute.preferredOpc <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				

				if(lprocIdList.size() == 2)
					msg << lprocIdList[1];
    }
    break;

    case ADD_LOCAL_SSN:
    {
			int status = -1;

        retVal = cliAddLocalSsn(&(req->u.addLocalSsn), &resp,lprocIdList);
				if (resp != NULL) 
				{
					status = resp->status;
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ADD_LOCAL_SSN,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< " Add Local SSN: nwId: "<< req->u.addLocalSsn.nwId <<
							" ssn: " << req->u.addLocalSsn.ssn <<
							" tcapUsapId: " << req->u.addLocalSsn.tcapUsapId <<
							" tcapLsapId: " << req->u.addLocalSsn.tcapLsapId <<
							" sccpUsapId: " << req->u.addLocalSsn.sccpUsapId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 

				if(lprocIdList.size() == 2)
					msg << lprocIdList[1] ;

				if(status == 0)
				{
  				enaSsn = new SsnEnable();
  				cmMemset((U8 *)enaSsn, 0, sizeof(SsnEnable));
        	enaSsn->ssn = req->u.addLocalSsn.ssn;
        	enaSsn->tcapUsapId = req->u.addLocalSsn.tcapUsapId;
        	enaSsn->tcapLsapId = req->u.addLocalSsn.tcapLsapId;
        	enaSsn->sccpUsapId = req->u.addLocalSsn.sccpUsapId;

	      	retVal = cliEnaLocalSsn(enaSsn, &resp1, lprocIdList);
					if (resp1 != NULL) 
					{
						outLen =0;
						outLen += sprintf(outParam+outLen, 
						"StkResp ProcId: %d, status: %d, Reason: %d,"
						" reasonStr:%s, stackLayer:%s", resp1->procId, 
						resp1->status, resp1->reason, 
						(resp1->reasonStr != NULL)?resp1->reasonStr:"NULL",
						(resp1->stackLayer != NULL)?resp1->stackLayer:"NULL");

						delete resp1;
					}

        	logger.logMsg (ALWAYS_FLAG, 0, 
					"INGwSmWrapper::processEmsReq cmdType:ENABLE_LOCAL_SSN,"
					"retVal:%d, resp:%s",
					retVal, (outLen == 0)?"Not Received":outParam);

					msg<< "\n Enable Local SSN: ssn: " << enaSsn->ssn <<
							" tcapUsapId: " << enaSsn->tcapUsapId <<
							" tcapLsapId: " << enaSsn->tcapLsapId <<
							" sccpUsapId: " << enaSsn->sccpUsapId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 				msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
					if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;

          //deleting enaSsn
          if(enaSsn != NULL)
          {
            delete enaSsn;
            enaSsn = NULL;
          }
				}
				else {
        	logger.logMsg (ALWAYS_FLAG, 0, 
					"INGwSmWrapper::processEmsReq ADD_LOCAL_SSN Updating SPId as -1 for"
					" ssn:%d", 
					req->u.addLocalSsn.ssn);

					INGwTcapProvider::getInstance().updateSsnInfo
													(req->u.addLocalSsn.ssn, -1);
				}
    }
    break;


    case DEL_LOCAL_SSN:
    {

		  int status = -1;

      DisSap *disSap= new DisSap();
      memset((U8*) disSap, 0, sizeof(DisSap));
      disSap->tcapLsapId = req->u.delLocalSsn.tcapLsapId;
      disSap->tcapUsapId = req->u.delLocalSsn.tcapUsapId;
      disSap->sccpUsapId = req->u.delLocalSsn.sccpUsapId;

      retVal = cliDisSsnSap(disSap, &resp,lprocIdList);
			if (resp != NULL) 
			{
				status = resp->status;
        outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

				if(resp->status == 0)
					isCmdSuccess = true;
			}

      logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:Disable Local SSN Sap's,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

      delete disSap;
      if(status == 0)
      {
        retVal = cliDelLocalSsn(&(req->u.delLocalSsn), &resp1,lprocIdList);
			  if (resp1 != NULL) 
			  {
			    outLen =0;
				  outLen += sprintf(outParam+outLen, 
						"StkResp ProcId: %d, status: %d, Reason: %d,"
						" reasonStr:%s, stackLayer:%s", resp1->procId, 
						resp1->status, resp1->reason, 
						(resp1->reasonStr != NULL)?resp1->reasonStr:"NULL",
						(resp1->stackLayer != NULL)?resp1->stackLayer:"NULL");

				  delete resp1;
			  }

        logger.logMsg (ALWAYS_FLAG, 0, 
					"INGwSmWrapper::processEmsReq cmdType:DEL_LOCAL_SSN,"
					"retVal:%d, resp:%s",
					retVal, (outLen == 0)?"Not Received":outParam);

			  msg<< "\n Delete Local SSN: ssn: " << req->u.delLocalSsn.ssn <<
							" tcapUsapId: " << req->u.delLocalSsn.tcapUsapId <<
							" tcapLsapId: " << req->u.delLocalSsn.tcapLsapId <<
							" sccpUsapId: " << req->u.delLocalSsn.sccpUsapId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 		  msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
			  if(lprocIdList.size() == 2)
				  msg << lprocIdList[1] ;

      }
    }
    break;


    case ADD_GTADDRMAP:
    {
        retVal = cliAddGtAddrMap(&(req->u.addAddrMapCfg), &resp, lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ADD_GTADDRMAP,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< "Add GTAddrMap: nwId: " << req->u.addAddrMapCfg.nwkId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;
    }
    break;

    case ADD_GTRULE:
	  {
        retVal = cliAddGtRule(&(req->u.addGtRule), &resp, lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ADD_GTRULE,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< "Add GtRule: nwId: " << req->u.addGtRule.nwId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;
		}
    break;

    case DEL_GTADDRMAP:
    {

        retVal = cliDelGtAddrMap(&(req->u.delAddrMapCfg), &resp, lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}


        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:DEL_GTADDRMAP,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< "Delete GTAddrMap: nwId: " << req->u.delAddrMapCfg.nwkId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;
    }
    break;

    case DEL_GTRULE:
    {
        retVal = cliDelGtRule(&(req->u.delGtRule), &resp,lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:DEL_GTRULE,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< "Delete GTRule: nwId: " <<  req->u.delGtRule.nwId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;
    }
    break;


    case DEL_ROUTE:
    {
        retVal = cliDelRoute(&(req->u.delRoute), &resp, lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:DEL_ROUTE,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< "Delete Route: dpc: " << req->u.delRoute.dpc <<
							" nSapId: " << req->u.delRoute.nSapId <<
							" upSwtch: "<< req->u.delRoute.upSwtch <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;
    }
    break;

    case ADD_ASP:
    {
				retVal = cliAddAsp(&(req->u.addpsp), &resp, lprocIdList);

				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0) {
					  isCmdSuccess = true;
            mpSmDistributor->getSmRepository()->addPspState(
                 req->u.addpsp.pspId, BP_AIN_SM_PSP_ST_ASPUP);
            logger.logMsg (ALWAYS_FLAG, 0, "pspId<%d> state<%d>", 
                   req->u.addpsp.pspId, BP_AIN_SM_PSP_ST_ASPUP);
          }
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ADD_ASP,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< "Add ASP: pspId: " << req->u.addpsp.pspId <<
							" nmbAddr: " << req->u.addpsp.nmbAddr <<
							" dstPort: " << req->u.addpsp.dstPort <<
							" nwkId: "<< req->u.addpsp.nwkId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;
        
    }
    break;

    case DEL_ASP:
    {
				retVal = cliDelAsp(&(req->u.delPsp), &resp, lprocIdList);

				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:DEL_ASP,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< "Delete ASP: pspId: " << req->u.delPsp.pspId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;
        
    }
    break;


    case ADD_ENDPOINT:
    {
        // If peer node is down and it's a ADD_ENDPOINT request for peer
        // node, then we should simply retun without adding it.
			  if((lprocIdList.size() == 1) &&
			  		(lprocIdList[0] == INGwSmBlkConfig::getInstance().m_selfProcId)) {
			  	if(req->u.addEp.sctpProcId == 
			  					INGwSmBlkConfig::getInstance().m_peerProcId) {

            if (from == 0) {
	            resp = new Ss7SigtranStackResp;
              resp->procId     = SFndProcId();
              resp->status     = FAILURE_RESPONSE;
              resp->reason     = FAILURE_REASON;
              resp->reasonStr  = (char*) "INC Node on which ENDPOINT is to be added is DOWN";
              resp->stackLayer = (char*)"NOT_APPLICABLE";
            }
            else {
					    isCmdSuccess = true;
            }
            
			  		break;
			  	}
			  }
        

			  // This is the case of PeerUP and we have not configured EndPoints 
			  // earlier for sctpProcId as Peer with stack Manager.
			  // Configure all EndPoints with sctpProcId as peer on both 51&52 procIds
			  // and configure all EndPoints with sctpProcId as self on 52 procId.
			  //if(from == 2 && !INGwSmBlkConfig::getInstance().isPeerEnabled())
			  if(from == 2) {
			  	if(req->u.addEp.sctpProcId == INGwSmBlkConfig::getInstance().m_peerProcId)
			  			INGwSmBlkConfig::getInstance().getActiveProcIds(lprocIdList);
			  }

        retVal = cliAddEndPoint(&(req->u.addEp), &resp, lprocIdList);
   
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ADD_ENDPOINT,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< " Add Endpoint: endPointid: " << req->u.addEp.endPointid <<
							" srcPort: "<< req->u.addEp.srcPort <<
							" nmbAddrs: " << req->u.addEp.nmbAddrs <<
							" sctpProcId: " << req->u.addEp.sctpProcId <<
							" sctpLsapId: " << req->u.addEp.sctpLsapId <<
							" sctpUsapId: " << req->u.addEp.sctpUsapId <<
							" m3uaLsapId: " << req->u.addEp.m3uaLsapId <<
							" tuclUsapId: " << req->u.addEp.tuclUsapId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;

        logger.logMsg (ALWAYS_FLAG, 0, 
           "processEmsReq()::Sleep 3 secs to let EndPoint be added...");
        sleep(3);

    }
    break;

    case DEL_ENDPOINT:
    {

        int status = -1, epState = -1;
        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq Entering DEL_ENDPOINT");


        /*** Unbinding the M3UA SCTSAP and SCTP TSAP, starts ***/
        epState = getEpMap(req->u.delEp.m3uaLsapId);
        if(epState == BINDED){
        retVal = cliUnbndM3uaSaps(&(req->u.delEp), &resp1, lprocIdList);
        if (resp1 != NULL) 
				{
          status = resp1->status;
          setEpMap(req->u.delEp.m3uaLsapId, UNBINDED);
          logger.logMsg (ALWAYS_FLAG, 0, 
				   "INGwSmWrapper::Setting sapId <%d> state to UNBINDED",req->u.delEp.m3uaLsapId);

					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp1->procId, 
					resp1->status, resp1->reason, 
					(resp1->reasonStr != NULL)?resp1->reasonStr:"NULL",
					(resp1->stackLayer != NULL)?resp1->stackLayer:"NULL");

          if(resp1->status == 0)
					  isCmdSuccess = true;
          delete resp1;
        }

        logger.logMsg (ALWAYS_FLAG, 0, 
					  "INGwSmWrapper::processEmsReq cmdType:DISABLE_SAPS <%d> in Del Endpoint," 
					  "retVal:%d, resp:%s",
					  req->u.delEp.m3uaLsapId, retVal, (outLen == 0)?"Not Received":outParam);

				    msg << "\n Disable Saps: " << 
				    msg << " m3uaLsapId: " << req->u.delEp.m3uaLsapId <<
				    " status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 				  msg << ", ProcId: " << lprocIdList[0] << "-"; 


        if(lprocIdList.size() == 2)
					  msg << lprocIdList[1];
       
        } 
        //if(status == 0) 
        {


        /*** Unbinding the M3UA SCTSAP and SCTP TSAP, ends ***/
        
        retVal = cliDelEp(&(req->u.delEp), &resp, lprocIdList);
   
				if (resp != NULL) 
				{
          outLen = 0;
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:DEL_ENDPOINT,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< "Delete Endpoint: Sctp ProcId: " << req->u.delEp.sctpProcId <<
							" SctpLsapId: "<< req->u.delEp.sctpLsapId <<
							" SctpUsapId: " << req->u.delEp.sctpUsapId <<
							" M3uaLsapId: " << req->u.delEp.m3uaLsapId <<
							" TuclUsapId: " << req->u.delEp.tuclUsapId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;



      }

    }
    break;

    case ADD_AS:
    {
        
        retVal = cliAddAs(&(req->u.addPs), &resp, lprocIdList);

				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ADD_AS,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< " Add AS: psId: " << req->u.addPs.psId <<
							" nwkId: " << req->u.addPs.nwkId <<
							" nmbPsp: "<< req->u.addPs.nmbPsp <<
							" dpc: "<< req->u.addPs.dpc <<
							" opc: "<< req->u.addPs.opc <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;
        
    }
    break;

    case DEL_AS:
    {
        AsSeq* asList = INGwSmBlkConfig::getInstance().getAsList();

        logger.logMsg (ALWAYS_FLAG, 0, 
           "INGwSmWrapper::processEmsReq AsList Size [%d]", asList->size());
        int flag = 0;
        AsSeq::iterator asIt;
	      for(asIt = asList->begin(); asIt != asList->end(); asIt++) 
	      {
          if((*asIt).psId == req->u.delPs.psId)
          {
            flag = 1;
            req->u.delPs.rtType = (*asIt).rtType;
            req->u.delPs.opcMask = (*asIt).opcMask;
            req->u.delPs.opc = (*asIt).opc;
            req->u.delPs.dpcMask = (*asIt).dpcMask;
            req->u.delPs.dpc = (*asIt).dpc;
            break;
          }
        } 

        if(flag == 1) { //Found the data in ASList
        
          retVal = cliDelAs(&(req->u.delPs), &resp, lprocIdList);

				  if (resp != NULL) 
				  {
					  outLen += sprintf(outParam+outLen, 
					  "StkResp ProcId: %d, status: %d, Reason: %d,"
					  " reasonStr:%s, stackLayer:%s", resp->procId, 
					  resp->status, resp->reason, 
					  (resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					  (resp->stackLayer != NULL)?resp->stackLayer:"NULL");
  
					  if(resp->status == 0)
							  isCmdSuccess = true;
				  }

          logger.logMsg (ALWAYS_FLAG, 0, 
				  "INGwSmWrapper::processEmsReq cmdType:DEL_AS,"
				  "retVal:%d, resp:%s psId<%d> nwkId<%d> rtType<%d> dpcMask<%d> dpc<%d> opcMask<%d> opc<%d>",
				  retVal, (outLen == 0)?"Not Received":outParam,req->u.delPs.psId,req->u.delPs.nwkId,
          req->u.delPs.rtType, req->u.delPs.dpcMask,req->u.delPs.dpc, req->u.delPs.opcMask, 
          req->u.delPs.opc );

				  msg<< "Del AS: psId: " << req->u.delPs.psId <<
							" nwkId: " << req->u.delPs.nwkId <<
							" rtType: " << req->u.delPs.rtType <<
							" dpcMask: " << req->u.delPs.dpcMask <<
							" dpc: " << req->u.delPs.dpc <<
							" opcMask: " << req->u.delPs.opcMask <<
							" opc: " << req->u.delPs.opc <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			  msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				  if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;
       }    
    }
    break;

   
    case SEND_INIT:
		{
        pspId_m3uaSapid = (((req->u.m3uaAssocUp.pspId) << 4) | req->u.m3uaAssocUp.m3uaLsapId);
        int state = getPspMap(pspId_m3uaSapid);

        setPspMap(pspId_m3uaSapid, ASSOCUP_SENT);
        retVal = cliAssocUp(&(req->u.m3uaAssocUp), &resp,lprocIdList);

				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
						isCmdSuccess = true;
				}
        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:SEND_INIT, retVal:%d,resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< " Send Init: pspId: "<< req->u.m3uaAssocUp.pspId << 
            " M3uaLSapId: "<< req->u.m3uaAssocUp.m3uaLsapId <<
            " status: "<< string((isCmdSuccess == true)?"Success, ProcId:":"Failure, ProcId");
	 			msg << lprocIdList[0] << "-"; 

				if(lprocIdList.size() == 2)
					msg << lprocIdList[1];
		}
    break;

 
      case ASSOC_UP:
      {
        int epState = -1;
        pspId_m3uaSapid = (((req->u.m3uaAssocUp.pspId) << 4) | req->u.m3uaAssocUp.m3uaLsapId);
        int state = getPspMap(pspId_m3uaSapid);

        if(req->u.m3uaAssocUp.isRetry == TRUE){
          if(state == ASSOCUP_DONE){
            logger.logMsg (ALWAYS_FLAG, 0,
              "processEmsReq():: Association establishment OK receveied already, Just send ASP_UP");
            sendAspUpCmd(req->u.m3uaAssocUp.pspId, req->u.m3uaAssocUp.m3uaLsapId);
            break;
          }
        }

        logger.logMsg (ALWAYS_FLAG, 0,
            "processEmsReq():: mePspState[%d]: state: %d",pspId_m3uaSapid, mePspState[pspId_m3uaSapid].state);

        logger.logMsg (ALWAYS_FLAG, 0, 
               "processEmsReq():: %s SCTP & TUCL layers",
               blkCfg.isSctpTuclEnabled()?
               "ALREADY ENABLED":"NEED TO ENABLE");

      if((from == 0) || (from == 1)){
        if ( ( ! blkCfg.isSctpTuclEnabled() ) ) {

          logger.logMsg (ALWAYS_FLAG, 0,
            "INGwSmWrapper::processEmsReq cmdType:ENABLE_NODE SCTP & TUCL");
          SgNode * enaNode = new SgNode();
          cmMemset((U8 *)enaNode, 0, sizeof(SgNode));
          enaNode->entId = ENT_INC_SCTP_TUCL;
          retVal = cliEnableNode(enaNode , &resp1, lprocIdList);

          if (resp1 != NULL) 
          {
            setEpMap(req->u.m3uaAssocUp.m3uaLsapId, BINDED);
            outLen =0;
            outLen += sprintf(outParam+outLen, 
            "StkResp ProcId: %d, status: %d, Reason: %d,"
            " reasonStr:%s, stackLayer:%s", resp1->procId, 
            resp1->status, resp1->reason, 
            (resp1->reasonStr != NULL)?resp1->reasonStr:"NULL",
            (resp1->stackLayer != NULL)?resp1->stackLayer:"NULL");

            if(resp1->status == 0)
                isCmdSuccess1 = true;

            delete resp1;
          }

          logger.logMsg (ALWAYS_FLAG, 0, 
          "INGwSmWrapper::processEmsReq cmdType:ENABLE_NODE SCTP & TUCL,"
          "retVal:%d, resp:%s",
          retVal, (outLen == 0)?"Not Received":outParam);

          msg<< " Enable Node: ENABLE_NODE SCTP & TUCL" <<
                " status: " << string((isCmdSuccess1 == true)?"Success":"Failure");
          msg << ", ProcId: " << lprocIdList[0] << "-"; 

          if(lprocIdList.size() == 2)
              msg << lprocIdList[1] ;

          delete enaNode;

          blkCfg.setSctpTuclEnabled(true);

          //sleep(3);
        }
      }

        bool configToBeDone = true;
        if (lprocIdList.size() == 1) {

          EpSeq* epList = blkCfg.getEpList();
          EpSeq::iterator it;
          
          U16 assocProcId = blkCfg.getAssocProcId(req->u.m3uaAssocUp);

          if ((from == 2) || (from == 0)) {

            vector<int>   actvProcIdList;
            blkCfg.getActiveProcIds(actvProcIdList);

            bool assocProcUp = false;
            for (int i = 0; i < actvProcIdList.size(); i++) {
              if (actvProcIdList[i] == assocProcId) {
                assocProcUp = true;
                logger.logMsg (ALWAYS_FLAG, 0, 
                       "Assoc procId <%d> is UP.", assocProcId);
                break;
              }
            }

            if ( ! assocProcUp) {
              logger.logMsg (ALWAYS_FLAG, 0, 
                "Assoc procId <%d> is NOT UP, couldn't bring up association",
                assocProcId);
              if (from == 0) {
                resp = new Ss7SigtranStackResp;
                resp->procId     = SFndProcId();
                resp->status     = FAILURE_RESPONSE;
                resp->reason     = FAILURE_REASON;
                resp->reasonStr  = (char*) "INC Node on which ASSOCIATION is to be brought up is DOWN";
                resp->stackLayer = (char*)"NOT_APPLICABLE";
              }
              else {
                isCmdSuccess = true;
              }
              
              break;
            }
          }

          if ( ! req->u.m3uaAssocUp.isRetry) {

            // peer node is down and it's a ASSOC_UP request for peer
            // node, then we should simply retun without adding it.
            if (lprocIdList[0] == blkCfg.m_selfProcId) {
              if (assocProcId == blkCfg.m_peerProcId) {
                logger.logMsg (ALWAYS_FLAG, 0, "AssocUp not required. "
                       "assocId<%d> sctpProcId<%d>",
                       req->u.m3uaAssocUp.assocId, assocProcId);
                configToBeDone = false;
              }
            }
            else if (lprocIdList[0] == blkCfg.m_peerProcId) {
              if (assocProcId == blkCfg.m_selfProcId) {
                logger.logMsg (ALWAYS_FLAG, 0, "AssocUp not required. "
                       "assocId<%d> sctpProcId<%d>",
                       req->u.m3uaAssocUp.assocId, assocProcId);
                configToBeDone = false;
              }
              
            }
            
            if ( ! configToBeDone ) {
              if (from == 0) {
                resp = new Ss7SigtranStackResp;
                resp->procId     = SFndProcId();
                resp->status     = FAILURE_RESPONSE;
                resp->reason     = FAILURE_REASON;
                resp->reasonStr  = (char*) "INC Node on which ASSOCIATION is to be brought up is DOWN";
                resp->stackLayer = (char*)"NOT_APPLICABLE";
              }
              else {
                isCmdSuccess = true;
              }
              break;
            }
          }
          else {
            logger.logMsg (ALWAYS_FLAG, 0,
                   "OldProcId<%d> NewProcId<%d>", lprocIdList[0], assocProcId);
            lprocIdList[0] = assocProcId;
          }
        } // End if lprocIdList.size() == 1
        

//Archana
      //adding bind req
      epState = getEpMap(req->u.m3uaAssocUp.m3uaLsapId);
      if ( ! req->u.m3uaAssocUp.isRetry) {
      if((epState == UNBINDED) || (epState == EP_UNKNOWN)){
      logger.logMsg (ALWAYS_FLAG, 0,
        "INGwSmWrapper::processEmsReq cmdType:BIND M3UA sapId<%d>",
        req->u.bindSap.sapId);

      BindSap *bndSap = new BindSap();
      cmMemset((U8 *)bndSap, 0,sizeof(BindSap));

      bndSap->sapId = req->u.m3uaAssocUp.m3uaLsapId;
      bndSap->stackLayer = BP_AIN_SM_M3U_LAYER;

      retVal = cliBndM3ua(bndSap, &resp3, lprocIdList);
      if (resp3 != NULL) 
			  {
			  	outLen =0;
			  	outLen += sprintf(outParam+outLen, 
			  	"StkResp ProcId: %d, status: %d, Reason: %d,"
			  	" reasonStr:%s, stackLayer:%s", resp3->procId, 
			  	resp3->status, resp3->reason, 
			  	(resp3->reasonStr != NULL)?resp3->reasonStr:"NULL",
			  	(resp3->stackLayer != NULL)?resp3->stackLayer:"NULL");

			  	if(resp3->status == 0)
			  			isCmdSuccess3 = true;

          delete resp3;
			  }

        logger.logMsg (ALWAYS_FLAG, 0, 
			  "INGwSmWrapper::processEmsReq cmdType:,BND_M3UA"
			  "retVal:%d, resp:%s",
			  retVal, (outLen == 0)?"Not Received":outParam);

			  msg<< " BND_M3UA: Bind M3UA" <<
			  			" status: " << string((isCmdSuccess3 == true)?"Success":"Failure");
	 		  msg << ", ProcId: " << lprocIdList[0] << "-"; 

			  if(lprocIdList.size() == 2)
			  		msg << lprocIdList[1] ;


      {

      cmMemset((U8 *)bndSap, 0,sizeof(BindSap));
      bndSap->sapId = req->u.m3uaAssocUp.m3uaLsapId;
      bndSap->stackLayer = BP_AIN_SM_SCT_LAYER;
      retVal = cliBndSctp(bndSap, &resp4, lprocIdList);
      if (resp4 != NULL) 
			  {
			  	outLen =0;
			  	outLen += sprintf(outParam+outLen, 
			  	"StkResp ProcId: %d, status: %d, Reason: %d,"
			  	" reasonStr:%s, stackLayer:%s", resp4->procId, 
			  	resp4->status, resp4->reason, 
			  	(resp4->reasonStr != NULL)?resp4->reasonStr:"NULL",
			  	(resp4->stackLayer != NULL)?resp4->stackLayer:"NULL");

			  	if(resp4->status == 0){
			  			isCmdSuccess4 = true;
          }

          delete resp4;
			  }

        setEpMap(req->u.m3uaAssocUp.m3uaLsapId, BINDED);
        logger.logMsg (ALWAYS_FLAG, 0, 
			  "INGwSmWrapper::processEmsReq cmdType:BND_SCTP,"
			  "retVal:%d, resp:%s",
			  retVal, (outLen == 0)?"Not Received":outParam);

			  msg<< " Bind SCTP: BND_SCTP" <<
			  			" status: " << string((isCmdSuccess4 == true)?"Success":"Failure");
	 		  msg << ", ProcId: " << lprocIdList[0] << "-"; 

			  if(lprocIdList.size() == 2)
			  		msg << lprocIdList[1] ;

      }
      if(bndSap != NULL)
      {
        delete bndSap;
        bndSap = NULL;
      }
      }
      }



        // Though there is no problem in doing OPEN_ENDPOINT 
        // multiple times for same ENDPOINT. But ideally 
        // OPEN_ENDPOINT command shall be executed only once 
        // for each endpoint. We can maintain a state along with 
        // each endPoint and based on it we can decide whether 
        // we shall perform OPEN_ENDPOINT whenever we are doing ASSOC_UP.
        if ( ! req->u.m3uaAssocUp.isRetry) {
        if ((from == 0) || (from == 1)) {
          logger.logMsg (ALWAYS_FLAG, 0,
            "INGwSmWrapper::processEmsReq cmdType:OPEN_ENDPOINT");

          OpenEp * openEp = new OpenEp();
          cmMemset((U8 *)openEp, 0, sizeof(OpenEp));

          EpSeq* epList = blkCfg.getEpList();

          EpSeq::iterator it;
          for(it=epList->begin(); it != epList->end(); ++it) {
            if ((*it).m3uaLsapId == req->u.m3uaAssocUp.m3uaLsapId) {
              openEp->suId = (*it).sctpLsapId;
              break;
            }
          }

          retVal = cliOpenEp(openEp, &resp2);

          if (resp2 != NULL) 
          {
            outLen =0;
            outLen += sprintf(outParam+outLen, 
            "StkResp ProcId: %d, status: %d, Reason: %d,"
            " reasonStr:%s, stackLayer:%s", resp2->procId, 
            resp2->status, resp2->reason, 
            (resp2->reasonStr != NULL)?resp2->reasonStr:"NULL",
            (resp2->stackLayer != NULL)?resp2->stackLayer:"NULL");

            if(resp2->status == 0) {
                isCmdSuccess2 = true;
            }

            delete resp2;
          }

          logger.logMsg (ALWAYS_FLAG, 0, 
          "INGwSmWrapper::processEmsReq cmdType:OPEN_ENDPOINT"
          "retVal:%d, resp:%s",
          retVal, (outLen == 0)?"Not Received":outParam);

          msg<< "Open Endpoint: OPEN_ENDPOINT " <<
                "sctpLsapId(suId): " << openEp->suId <<
                " status: " << string((isCmdSuccess2 == true)?"Success":"Failure");
          msg << ", ProcId: " << lprocIdList[0] << "-"; 

          if(lprocIdList.size() == 2)
              msg << lprocIdList[1] ;

          delete openEp;

          //sleep(5);
        }
        }

        if(req->u.m3uaAssocUp.isRetry == FALSE)
        {
          state = getPspMap(pspId_m3uaSapid);
          if (state == ASSOCUP_DONE) {
            logger.logMsg (ALWAYS_FLAG, 0, 
            "ASSOC state id ASSOCUP_DONE, need to down it");
            M3uaAssocDown *assocDn = new M3uaAssocDown;
            memset(assocDn, '\0',sizeof(M3uaAssocDown));
            assocDn->assocId = req->u.m3uaAssocUp.assocId;
            assocDn->pspId = req->u.m3uaAssocUp.pspId;
            assocDn->endPointId = req->u.m3uaAssocUp.endPointId;
            assocDn->m3uaLsapId = req->u.m3uaAssocUp.m3uaLsapId;

            retVal = cliAssocDn(assocDn, &resp3, lprocIdList);
            setPspMap(pspId_m3uaSapid, ASSOC_DISABLE);
            if(assocDn != NULL)
            {
              delete assocDn;
              assocDn = NULL;
            }
            sleep(2);
          }
        }
         
        setPspMap(pspId_m3uaSapid, ASSOCUP_SENT);
        retVal = cliAssocUp(&(req->u.m3uaAssocUp), &resp, lprocIdList);

        if (resp != NULL) 
        {
          outLen = 0;
          outLen += sprintf(outParam+outLen, 
          "StkResp ProcId: %d, status: %d, Reason: %d,"
          " reasonStr:%s, stackLayer:%s", resp->procId, 
          resp->status, resp->reason, 
          (resp->reasonStr != NULL)?resp->reasonStr:"NULL",
          (resp->stackLayer != NULL)?resp->stackLayer:"NULL");

          if(resp->status == 0)
          {
              isCmdSuccess = true;
          }
          else{
            setPspMap(pspId_m3uaSapid, ASSOCUP_FAIL);
          }
        }

        logger.logMsg (ALWAYS_FLAG, 0, 
        "INGwSmWrapper::processEmsReq cmdType:ASSOC_UP,"
        "retVal:%d, resp:%s",
        retVal, (outLen == 0)?"Not Received":outParam);

        if(!req->u.m3uaAssocUp.isRetry){

        msg<< " Assoc Up: assocId: " << req->u.m3uaAssocUp.assocId <<
              " pspId: " << req->u.m3uaAssocUp.pspId <<
              " endPointId: "<< req->u.m3uaAssocUp.endPointId <<
              " m3uaLsapId: "<< req->u.m3uaAssocUp.m3uaLsapId <<
              " status: " << string((isCmdSuccess == true)?"Success":"Failure");
        }
        else{
        msg<< " Assoc Up: assocId: " << req->u.m3uaAssocUp.assocId <<
              " pspId: " << req->u.m3uaAssocUp.pspId <<
              " m3uaLsapId: "<< req->u.m3uaAssocUp.m3uaLsapId <<
              " status: " << string((isCmdSuccess == true)?"Success":"Failure");
        }
        msg << ", ProcId: " << lprocIdList[0] << "-"; 
        
        if(lprocIdList.size() == 2)
            msg << lprocIdList[1] ;

        //sleep(5);
        /*if( (state == ASSOCUP_DONE) && (req->u.m3uaAssocUp.isRetry == FALSE) )
        {
          logger.logMsg (ALWAYS_FLAG, 0,
              "processEmsReq():: Association establishment OK receveied already, Just send ASP_UP");
            sendAspUpCmd(req->u.m3uaAssocUp.pspId, req->u.m3uaAssocUp.m3uaLsapId);
            break;
        }*/
      }
      break;        

      case ASP_UP:
      {

          pspId_m3uaSapid = (((req->u.m3uaAspUp.pspId) << 4) | req->u.m3uaAspUp.m3uaLsapId);
          logger.logMsg (ALWAYS_FLAG, 0,
             "INGwSmWrapper::processEmsReq cmdType:ASP_UP");

           int state = getPspMap(pspId_m3uaSapid);

           if ( state == ASSOCUP_DONE)
           {
              setPspMap(pspId_m3uaSapid, ASPUP_SENT);
              retVal = cliAspUp(&(req->u.m3uaAspUp), &resp, lprocIdList);
              logger.logMsg (ALWAYS_FLAG, 0,
                  "processEmsReq, ASP_UP mePspState[%d]: %d",pspId_m3uaSapid, mePspState[pspId_m3uaSapid].state);

              if (resp != NULL) 
              {
                outLen += sprintf(outParam+outLen, 
                "StkResp ProcId: %d, status: %d, Reason: %d,"
                " reasonStr:%s, stackLayer:%s", resp->procId, 
                resp->status, resp->reason, 
                (resp->reasonStr != NULL)?resp->reasonStr:"NULL",
                (resp->stackLayer != NULL)?resp->stackLayer:"NULL");
    
                if(resp->status == 0){
                    isCmdSuccess = true;
                }
                else{
                    setPspMap(pspId_m3uaSapid, ASPUP_FAIL); //may be not handled all failure cases from almHdlr.C
                    logger.logMsg (ALWAYS_FLAG, 0,
                      "processEmsReq, ASP_UP mePspState[%d]: %d",pspId_m3uaSapid, mePspState[pspId_m3uaSapid].state);
                }
              }

              logger.logMsg (ALWAYS_FLAG, 0, 
              "INGwSmWrapper::processEmsReq cmdType:ASP_UP,"
              "retVal:%d, resp:%s",
              retVal, (outLen == 0)?"Not Received":outParam);
    
              msg<< " ASP Up: pspId: " << req->u.m3uaAspUp.pspId <<
                " m3uaLsapId: "<< req->u.m3uaAspUp.m3uaLsapId <<
                " status: " << string((isCmdSuccess == true)?"Success":"Failure");

              msg << ", ProcId: " << lprocIdList[0] << "-"; 
           
              if(lprocIdList.size() == 2)
                    msg << lprocIdList[1] ;
            }
      }
      break;

      case ASP_ACTIVE:
      {
        logger.logMsg (ALWAYS_FLAG, 0,
               "INGwSmWrapper::processEmsReq cmdType:ASP_ACTIVE");
        pspId_m3uaSapid = (((req->u.m3uaAssocUp.pspId) << 4) | req->u.m3uaAssocUp.m3uaLsapId);
        int state = getPspMap(pspId_m3uaSapid);

        if ( state == ASPUP_DONE)
        {
          M3uaAspAct *aspAct = new M3uaAspAct();
          memset((U8 *)aspAct, 0,sizeof(M3uaAspAct));
          aspAct->pspId = req->u.m3uaAspAct.pspId;
          aspAct->m3uaLsapId = req->u.m3uaAspAct.m3uaLsapId;

          AsSeq* asList = blkCfg.getAsList();

          logger.logMsg (ALWAYS_FLAG, 0, 
           "INGwSmWrapper::processEmsReq AsList Size [%d]", asList->size());

          AsSeq::iterator asIt;
          for(asIt = asList->begin(); asIt != asList->end(); asIt++) 
          {
            logger.logMsg (ALWAYS_FLAG, 0, 
                 "INGwSmWrapper::processEmsReq AsId [%d] nmbPsp[%d]", 
                    (*asIt).psId, (*asIt).nmbPsp);

            for (int i = 0; i < (*asIt).nmbPsp; i++)
            {
              logger.logMsg (ALWAYS_FLAG, 0, 
                    "INGwSmWrapper::processEmsReq psId [%d] pspId[%d]",
                      (*asIt).psId, (*asIt).psp[i]);
              if((*asIt).psp[i] == req->u.m3uaAspAct.pspId)
              {
                aspAct->psId = (*asIt).psId;
                break;
              }
            }
          }
   
         if (aspAct->psId == 0) {
              logger.logMsg (ALWAYS_FLAG, 0, 
                 "INGwSmWrapper::processEmsReq cmdType:ASP_ACTIVE FAILED, couldn't"
                 " find PsId corresponding to PsdId [%d]", req->u.m3uaAspAct.pspId);
         }

             

         setPspMap(pspId_m3uaSapid, ASPACTV_SENT);
         retVal = cliAspActive(aspAct, &resp, lprocIdList);

         if (resp != NULL) 
         {
              outLen =0;
              outLen += sprintf(outParam+outLen, 
              "StkResp ProcId: %d, status: %d, Reason: %d,"
              " reasonStr:%s, stackLayer:%s", resp->procId, 
              resp->status, resp->reason, 
              (resp->reasonStr != NULL)?resp->reasonStr:"NULL",
              (resp->stackLayer != NULL)?resp->stackLayer:"NULL");

              if(resp->status == 0){
                  isCmdSuccess = true;
                  logger.logMsg (ALWAYS_FLAG, 0,
                    "processEmsReq, ASP_ACTIVE mePspState[%d]: %d",pspId_m3uaSapid, mePspState[pspId_m3uaSapid].state);
              }
              else{
                  setPspMap(pspId_m3uaSapid, ASPACTV_FAIL);
                  logger.logMsg (ALWAYS_FLAG, 0,
                    "processEmsReq, ASP_ACTIVE mePspState[%d]: %d",pspId_m3uaSapid, mePspState[pspId_m3uaSapid].state);
            }
	        }

           logger.logMsg (ALWAYS_FLAG, 0, 
			     "INGwSmWrapper::processEmsReq cmdType:ASP_ACTIVE,"
			     "retVal:%d, resp:%s",
			     retVal, (outLen == 0)?"Not Received":outParam);

           msg<< " ASP Active: psId: " << aspAct->psId <<
			     			" pspId: " << aspAct->pspId << 
			     			" m3uaLsapId: "<< aspAct->m3uaLsapId <<
			     			" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 		     msg << ", ProcId: " << lprocIdList[0] << "-"; 
			     
			     if(lprocIdList.size() == 2)
			       		msg << lprocIdList[1] ;

          if(aspAct != NULL){
           delete aspAct;
           aspAct = NULL;
          }
        }       

		}
    break;

#ifdef INC_ASP_SNDDAUD
    case SEND_DAUD:
    {
      logger.logMsg(ALWAYS_FLAG, 0,
             "processEmsReq cmdType:SEND DAUD NwId<%d> Dpc<0x%x> PsId<%d>",
             req->u.daud.nwkId, req->u.daud.dpc, req->u.daud.psId);

      retVal = cliSendDaud(&(req->u.daud), &resp);

      if (resp != NULL) 
		  {
		  	outLen =0;
		  	outLen += sprintf(outParam+outLen, 
		  	"StkResp ProcId: %d, status: %d, Reason: %d,"
		  	" reasonStr:%s, stackLayer:%s", resp->procId, 
		  	resp->status, resp->reason, 
		  	(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
		  	(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

		  	if(resp->status == 0)
		  			isCmdSuccess = true;
		  }

      logger.logMsg (ALWAYS_FLAG, 0, 
		  "INGwSmWrapper::processEmsReq cmdType:SEND_DAUD,"
		  "retVal:%d, resp:%s",
		  retVal, (outLen == 0)?"Not Received":outParam);

      msg<< " SEND DAUD " <<
		  			" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 	  msg << ", ProcId: " << SFndProcId();
      
    }
    break;
#endif

 
    case BND_M3UA:
    {
      logger.logMsg (ALWAYS_FLAG, 0,
        "INGwSmWrapper::processEmsReq cmdType:BIND SCTSAP sapId<%d>",
        req->u.bindSap.sapId);

      BindSap *bndSap = new BindSap();
      cmMemset((U8 *)bndSap, 0,sizeof(BindSap));

      bndSap->sapId = req->u.bindSap.sapId;

      retVal = cliBndM3ua(bndSap, &resp, lprocIdList);

      if (resp != NULL) 
		  {
		  	outLen =0;
		  	outLen += sprintf(outParam+outLen, 
		  	"StkResp ProcId: %d, status: %d, Reason: %d,"
		  	" reasonStr:%s, stackLayer:%s", resp->procId, 
		  	resp->status, resp->reason, 
		  	(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
		  	(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

		  	if(resp->status == 0)
		  			isCmdSuccess = true;
		  }

      logger.logMsg (ALWAYS_FLAG, 0, 
		  "INGwSmWrapper::processEmsReq cmdType:BND_ENA SCTSAP,"
		  "retVal:%d, resp:%s",
		  retVal, (outLen == 0)?"Not Received":outParam);

		  msg<< " BND_ENA SCTSAP " <<
		  			" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 	  msg << ", ProcId: " << lprocIdList[0] << "-"; 

		  if(lprocIdList.size() == 2)
		  		msg << lprocIdList[1] ;

      logger.logMsg (ALWAYS_FLAG, 0, 
         "processEmsReq()::Sleep 5 secs to let SCTSAP bind...");
      sleep(2);
      
      if(bndSap != NULL)
      { 
        delete bndSap;
        bndSap = NULL; 
      }
    }
    break;


    case ASSOC_DOWN:
    {
          int status = -1; 
         pspId_m3uaSapid = (((req->u.m3uaAssocUp.pspId) << 4) | req->u.m3uaAssocUp.m3uaLsapId);
         setPspMap(pspId_m3uaSapid, ASSOC_DISABLE);


         M3uaAspInAct *aspInact = new M3uaAspInAct();
         cmMemset((U8 *)aspInact, 0,sizeof(M3uaAspInAct));

         AsSeq* asList = INGwSmBlkConfig::getInstance().getAsList();

         logger.logMsg (ALWAYS_FLAG, 0, 
           "INGwSmWrapper::processEmsReq AsList Size [%d]", asList->size());

         AsSeq::iterator asIt;
	       for(asIt = asList->begin(); asIt != asList->end(); asIt++) 
	       {
           logger.logMsg (ALWAYS_FLAG, 0, 
                  "INGwSmWrapper::processEmsReq AsId [%d] nmbPsp[%d]", 
                  (*asIt).psId, (*asIt).nmbPsp);

           for (int i = 0; i < (*asIt).nmbPsp; i++)
           {
             logger.logMsg (ALWAYS_FLAG, 0, 
                    "INGwSmWrapper::processEmsReq psId [%d] pspId[%d]",
                    (*asIt).psId, (*asIt).psp[i]);
	       	  if((*asIt).psp[i] == req->u.m3uaAssocDown.pspId)
	       	  {
               aspInact->psId = (*asIt).psId;
	       	  	break;
	       	  }
           }
	       }
 
         if (aspInact->psId == 0) {
           logger.logMsg (ALWAYS_FLAG, 0, 
			       "INGwSmWrapper::processEmsReq cmdType:ASP_INACTIVE FAILED, couldn't"
             " find PsId corresponding to PsdId [%d]", req->u.m3uaAssocDown.pspId);
         }

         aspInact->pspId = req->u.m3uaAssocDown.pspId;
         aspInact->endPointId = req->u.m3uaAssocDown.endPointId;
         aspInact->m3uaLsapId = req->u.m3uaAssocDown.m3uaLsapId;
         

         retVal = cliAspInActive(aspInact, &resp1, lprocIdList);

			   if (resp1 != NULL) 
			   {
          status = resp1->status;
			   	outLen += sprintf(outParam+outLen, 
			   	"StkResp ProcId: %d, status: %d, Reason: %d,"
			   	" reasonStr:%s, stackLayer:%s", resp1->procId, 
			   	resp1->status, resp1->reason, 
			   	(resp1->reasonStr != NULL)?resp1->reasonStr:"NULL",
			   	(resp1->stackLayer != NULL)?resp1->stackLayer:"NULL");

			   	if(resp1->status == 0)
			   			isCmdSuccess = true;
          delete resp1;
			   }

        //delete it from blkconfig map also and send this info to peer, starts
        int li_selfId = INGwIfrPrParamRepository::getInstance().getSelfId();
	      int li_peerId = INGwIfrPrParamRepository::getInstance().getPeerId();

        //INGwSmBlkConfig::getInstance().delActvAsp(*aspInact);
        if(li_peerId !=0)
        {   
          INGwFtPktAspInActive ftAsp;
				  ftAsp.initialize(li_selfId, li_peerId, *aspInact);
				  INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAsp);
        }
        //delete it from blkconfig map also and send this info to peer, ends


         logger.logMsg (ALWAYS_FLAG, 0, 
			   "INGwSmWrapper::processEmsReq cmdType:ASP_INACTIVE,"
			   "retVal:%d, resp:%s",
			   retVal, (outLen == 0)?"Not Received":outParam);

				msg<< " Asp Inactive: pspId: " << aspInact->pspId <<
							" endPointId: "<< aspInact->endPointId <<
			   			" m3uaLsapId: "<< aspInact->m3uaLsapId <<
			   			" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 		   msg << ", ProcId: " << lprocIdList[0] << "-"; 
			   
			   if(lprocIdList.size() == 2)
			     		msg << lprocIdList[1] ;

      if(aspInact != NULL)
      {
        delete aspInact;
        aspInact = NULL;
      }


      logger.logMsg (ALWAYS_FLAG, 0, 
         "processEmsReq()::Sleep 5 secs to receive alarm for ASP_INACTIVE...");
      sleep(5);
       
      if (status == 0) {

         logger.logMsg (ALWAYS_FLAG, 0,
           "INGwSmWrapper::processEmsReq cmdType:ASP_DOWN");

         M3uaAspDown *aspDn = new M3uaAspDown();
         cmMemset((U8 *)aspDn, 0,sizeof(M3uaAspDown));
         aspDn->pspId = req->u.m3uaAssocDown.pspId;
         aspDn->endPointId = req->u.m3uaAssocDown.endPointId;
         aspDn->m3uaLsapId = req->u.m3uaAssocDown.m3uaLsapId;

         retVal = cliAspDn(aspDn, &resp2, lprocIdList);

			   if (resp2 != NULL) 
			   {
			 		outLen =0;
			   	outLen += sprintf(outParam+outLen, 
			   	"StkResp ProcId: %d, status: %d, Reason: %d,"
			   	" reasonStr:%s, stackLayer:%s", resp2->procId, 
			   	resp2->status, resp2->reason, 
			   	(resp2->reasonStr != NULL)?resp2->reasonStr:"NULL",
			   	(resp2->stackLayer != NULL)?resp2->stackLayer:"NULL");

			   	if(resp2->status == 0)
			   			isCmdSuccess = true;

          delete resp2;
			   }

         logger.logMsg (ALWAYS_FLAG, 0, 
			   "INGwSmWrapper::processEmsReq cmdType:ASP_DOWN,"
			   "retVal:%d, resp:%s",
			   retVal, (outLen == 0)?"Not Received":outParam);

			   msg<< " ASP Down: pspId: " << aspDn->pspId <<
			   			" endPointId: " << aspDn->endPointId <<
			   			" m3uaLsapId: "<< aspDn->m3uaLsapId <<
			   			" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 		   msg << ", ProcId: " << lprocIdList[0] << "-"; 
			   
			   if(lprocIdList.size() == 2)
			     		msg << lprocIdList[1] ;


      if(aspDn != NULL)
      {
        delete aspDn;
        aspDn = NULL;
      }
         logger.logMsg (ALWAYS_FLAG, 0, 
            "processEmsReq()::Sleep 5 secs to receive alarm for ASP_DOWN...");
         sleep(5);

         logger.logMsg (ALWAYS_FLAG, 0,
           "INGwSmWrapper::processEmsReq cmdType:ASSOC_DOWN");

         retVal = cliAssocDn(&(req->u.m3uaAssocDown), &resp, lprocIdList);

			   if (resp != NULL) 
			   {
			 		outLen =0;
			   	outLen += sprintf(outParam+outLen, 
			   	"StkResp ProcId: %d, status: %d, Reason: %d,"
			   	" reasonStr:%s, stackLayer:%s", resp->procId, 
			   	resp->status, resp->reason, 
			   	(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
			   	(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

			   	if(resp->status == 0)
			   			isCmdSuccess = true;
			   }

         logger.logMsg (ALWAYS_FLAG, 0, 
			   "INGwSmWrapper::processEmsReq cmdType:ASP_INACTIVE,"
			   "retVal:%d, resp:%s",
			   retVal, (outLen == 0)?"Not Received":outParam);

			   msg<< " ASSOC Down: assocId: " << req->u.m3uaAssocDown.assocId <<
			   			" pspId: " << req->u.m3uaAssocDown.pspId <<
			   			" endPointId: " << req->u.m3uaAssocDown.endPointId <<
			   			" m3uaLsapId: "<< req->u.m3uaAssocDown.m3uaLsapId <<
			   			" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 		   msg << ", ProcId: " << lprocIdList[0] << "-"; 
			   
			   if(lprocIdList.size() == 2)
			     		msg << lprocIdList[1] ;
      }
    }
    break;


    case ASP_DOWN:
    {
        retVal = cliAspDn(&(req->u.m3uaAspDown), &resp, lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:ASP_DN, retVal:%d", retVal);
    }
    break;

    case ASP_INACTIVE:
    {
        retVal = cliAspInActive(&(req->u.m3uaAspInact), &resp, lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:ASP_INACT, retVal:%d", retVal);
    }
    break;

    case GET_STATS:
		{
        retVal = cliGetStats(&(req->u.stat));
				//msg<< "Get Stats : " ;

				/*if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
						isCmdSuccess = true;
				}*/
        logger.logMsg (TRACE_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:GET_STATS");
		}
    break;

    case OPEN_ENDPOINT:
    {
        retVal = cliOpenEp(&(req->u.openEp), &resp);

				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:OPEN_ENDPOINT,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< "Open Endpoint: " <<
              "sctpLsapId(suId): " << req->u.openEp.suId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;
        
    }
    break;

    case SHUTDOWN_LAYERS:
    {
      retVal = cliShutdownLayers(&(req->u.shutDownLayers), &resp,lprocIdList);

		  if (resp != NULL) 
		  {
		  	outLen += sprintf(outParam+outLen, 
		  	"StkResp ProcId: %d, status: %d, Reason: %d,"
		  	" reasonStr:%s, stackLayer:%s", resp->procId, 
		  	resp->status, resp->reason, 
		  	(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
		  	(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

		  	if(resp->status == 0)
		  		isCmdSuccess = true;
		  }
      logger.logMsg (ALWAYS_FLAG, 0, 
		  "INGwSmWrapper::processEmsReq cmdType:SHUTDOWN_LAYERS, retVal:%d,resp:%s",
		  retVal, (outLen == 0)?"Not Received":outParam);

		  msg<< "Shutdown Layers: "<< " status: "
	 	  << string((isCmdSuccess == true)?"Success, ProcId:":"Failure, ProcId");
	 	  msg << lprocIdList[0] << "-"; 

		  if(lprocIdList.size() == 2)
		  	msg << lprocIdList[1];
    }
    break;

    case ENABLE_DEBUG:
    {
      retVal = cliEnaDebug(&(req->u.debug), lprocIdList);

    }
    break;

    case DISABLE_DEBUG:
    {
      retVal = cliDisableDebug(&(req->u.debug), lprocIdList);

    }
    break;

    case ENABLE_TRACE:
    {
	      retVal = cliEnaTrace(&(req->u.trace),lprocIdList);
	      logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:ENABLE_TRACE");
     }
     break;

    case DISABLE_TRACE:
    {
	      retVal = cliDisableTrace(&(req->u.trace),lprocIdList);
	      logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:DISABLE_TRACE");
     }
     break;

#if 0
    case MODIFY_LINK:
    {
        retVal = cliModLink(&(req->u.lnk), &resp);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");
				}

        logger.logMsg (TRACE_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:MODIFY_LINK, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);
    }
    break;

    case ENABLE_LINK:
    {
	      retVal = cliEnaLnk(&(req->u.lnkEnable), &resp);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");
				}

        logger.logMsg (TRACE_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ENABLE_LINK, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);
    }
    break;

    case DISABLE_LINK:
    {
        retVal = cliDisableLnk(&(req->u.lnkDisable), &resp, lprocIdList);
				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");
				}

        logger.logMsg (TRACE_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:DISABLE_LINK, retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);
    }
		break;

    case ENABLE_DEBUG:
    {
	      retVal = cliEnaDebug(&(req->u.debug), lprocIdList);
	      logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:ENABLE_DEBUG, retVal:%d", retVal);
    }
    break;

    case ENABLE_TRACE:
    {
	      retVal = cliEnaTrace(&(req->u.trace),lprocIdList);
	      logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:ENABLE_TRACE, retVal:%d", retVal);
     }
     break;

    case ENABLE_LOCAL_SSN:
    {
	      retVal = cliEnaLocalSsn(&(req->u.ssnEnable),lprocIdList);
	      logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:ENABLE_LOCAL_SSN, retVal:%d", retVal);
     }
     break;

    case ENABLE_USERPART:
    {
	      retVal = cliEnaUsrpart(&(req->u.enableUserPart),lprocIdList);
	      logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:ENABLE_USERPART, retVal:%d", retVal);
    }
    break;

    case ENABLE_ENDPOINT:
    {
	      retVal = cliEnaEp(&(req->u.enableEp),lprocIdList);
	      logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:ENABLE_ENDPOINT, retVal:%d", retVal);
    }
    break;

    case DISABLE_DEBUG:
    {
        retVal = cliDisableDebug(&(req->u.debug),lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:DISABLE_DEBUG, retVal:%d", retVal);
    }
    break;

    case DISABLE_TRACE:
    {
        retVal = cliDisableTrace(&(req->u.trace),lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:DISABLE_TRACE, retVal:%d", retVal);
    }
    break;

    case DISABLE_LOCAL_SSN:
    {
        retVal = cliDisableLocalSsn(&(req->u.ssnDisable),lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:DISABLE_LOCAL_SSN, retVal:%d", retVal);
    }
    break;

    case DISABLE_USERPART:
    {
        retVal = cliDisableUsrpart(&(req->u.disableUserPart),lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:DISABLE_USERPART, retVal:%d", retVal);
    }
    break;

    case DISABLE_ENDPOINT:
    {
        retVal = cliDisableEp(&(req->u.disableEp),lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:DISABLE_ENDPOINT, retVal:%d", retVal);
    }
    break;

    case MODIFY_AS:
    {
        retVal = cliModAs(&(req->u.addPs), lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:MODIFY_AS, retVal:%d", retVal);
    }
    break;


    case DEL_ENDPOINT:
    {
        retVal = cliDelEp(&(req->u.delEp),lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:DEL_ENDPOINT, retVal:%d", retVal);

				if(resp->status == 0)
						isCmdSuccess = true;
    }
    break;

    case DEL_ASP:
    {
        retVal = cliDelAsp(&(req->u.delPsp), lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:DEL_ASP, retVal:%d", retVal);
    }
    break;

    case DEL_AS:
    {
        retVal = cliDelAs(&(req->u.delPs),lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:DEL_AS, retVal:%d", retVal);

				if(resp->status == 0)
						isCmdSuccess = true;
    }
    break;

    case DEL_LOCAL_SSN:
    {
        retVal = cliDelLocalSsn(&(req->u.delLocalSsn),lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:DEL_LOCAL_SSN, retVal:%d", retVal);
    }
    break;

    //case DEL_REMOTE_SSN:
    //{
    //    retVal = cliDelRemoteSsn(&(req->u.delRemoteSsn),lprocIdList);
    //    logger.logMsg (TRACE_FLAG, 0,
		//		"INGwSmWrapper::processEmsReq cmdType:DEL_REMOTE_SSN, retVal:%d", retVal);
    //}
    //break;

    case DEL_USR_PART:
    {
        retVal = cliDelUsrPart(&(req->u.delUserPart), lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:DEL_USERPART, retVal:%d", retVal);
    }
    break;


    case STA_LINK:
    {
        retVal = cliStatusLink(&(req->u.lnkstatus), &lpStackResp,lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:STA_LINK, retVal:%d", retVal);
    }
    break;

    case STA_LINKSET:
    {
        retVal = cliStatusLnkSet(&(req->u.lnkStatus), lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:STA_LINKSET, retVal:%d", retVal);
    }
    break;

    case STA_ROUTE:
    {
        retVal = cliStatusRte(&(req->u.dpcStatus),lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:STA_ROUTE, retVal:%d", retVal);

    }
    break;

    case STA_PS:
    {
        retVal = cliStatusPs(&(req->u.ps), lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:STA_PS, retVal:%d", retVal);

    }
    break;

    case STA_PSP:
    {
        retVal = cliStatusPsp(&(req->u.psp),lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:STA_PSP, retVal:%d", retVal);
    }
    break;

    case STA_NODE:
    {
        retVal = cliStatusNode(&(req->u.nodeStatus),lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:STA_NODE, retVal:%d", retVal);
    }
    break;

    case ENABLE_NODE:
    {
        retVal = cliEnableNode(&(req->u.sgNode), lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:ENABLE_NODE, retVal:%d", retVal);
    }
    break;

    case DISABLE_NODE:
    {
        retVal = cliDisableNode(&(req->u.sgNode), lprocIdList);
        logger.logMsg (TRACE_FLAG, 0,
				"INGwSmWrapper::processEmsReq cmdType:DISABLE_NODE, retVal:%d", retVal);
    }
    break;

    case ASP_UP:
    {
        retVal = cliAspUp(&(req->u.m3uaAspUp), &resp, lprocIdList);

				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ASP_UP,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< "ASP Up: pspId: " << req->u.m3uaAspUp.pspId <<
							" endPointId: " << req->u.m3uaAspUp.endPointId <<
							" m3uaLsapId: "<< req->u.m3uaAspUp.m3uaLsapId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;
        
    }
    break;

    case ASP_ACTIVE:
    {
        retVal = cliAspActive(&(req->u.m3uaAspAct), &resp, lprocIdList);

				if (resp != NULL) 
				{
					outLen += sprintf(outParam+outLen, 
					"StkResp ProcId: %d, status: %d, Reason: %d,"
					" reasonStr:%s, stackLayer:%s", resp->procId, 
					resp->status, resp->reason, 
					(resp->reasonStr != NULL)?resp->reasonStr:"NULL",
					(resp->stackLayer != NULL)?resp->stackLayer:"NULL");

					if(resp->status == 0)
							isCmdSuccess = true;
				}

        logger.logMsg (ALWAYS_FLAG, 0, 
				"INGwSmWrapper::processEmsReq cmdType:ASP_ACTIVE,"
				"retVal:%d, resp:%s",
				retVal, (outLen == 0)?"Not Received":outParam);

				msg<< "ASP Active: psId: " << req->u.m3uaAspAct.psId <<
							" pspId: " << req->u.m3uaAspAct.pspId <<
							" endPointId: "<< req->u.m3uaAspAct.endPointId <<
							" m3uaLsapId: "<< req->u.m3uaAspAct.m3uaLsapId <<
							" status: " << string((isCmdSuccess == true)?"Success":"Failure");
	 			msg << ", ProcId: " << lprocIdList[0] << "-"; 
				
				if(lprocIdList.size() == 2)
						msg << lprocIdList[1] ;
        
    }
    break;
#endif

		default:
			retVal = BP_AIN_SM_FAIL;		
			logger.logMsg(ERROR_FLAG, 0,
			"INGwSmWrapper::processEmsReq cmdType:UNKNOWN, %d, returning error", 
			req->cmd_type);
  }

	// Add into Bulk Config if command execution
	// is successful
	if(isCmdSuccess && from != 2)
		INGwSmBlkConfig::getInstance().updateNode(req, from);
		
	if (req->cmd_type != GET_STATS ) {
	  g_dumpMsg("SmWrapper", __LINE__, msg);
  }

	if (req->cmd_type != GET_STATS) {
    logger.logMsg (TRACE_FLAG, 0,
        "Entering INGwSmWrapper::processEmsReq <cmdType><%d> from<%d>",
        req->cmd_type, from);
  }
  else {
    logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::processEmsReq");
  }
  return retVal;
}

/******************************************************************************
*
*     Fun:   processEmsReq()
*
*     Desc:  process EMS request 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::processEmsReq(Ss7SigtranSubsReq *req)
{
  int retVal = BP_AIN_SM_OK;
  logger.logMsg (TRACE_FLAG, 0,
      "Entering processEmsReq(Ss7SigtranSubsReq *)::<cmdType><%d>", req->cmd_type);

  char * lpcIncAsSimulator = getenv("INC_AS_SIMULATOR");
  bool lbIncAsSimulator = false;
    char* node;
  // This shall have the response of Stack after command execution
  Ss7SigtranStackResp *lpStackResp = NULL;
  int nodeType = mpSmDistributor->getSmRepository()->getTransportType();

  if (lpcIncAsSimulator) {
    lbIncAsSimulator = (atoi(lpcIncAsSimulator) == 1)?true:false;
  }
  vector<int> lprocIdList;
	INGwSmBlkConfig::getInstance().getActiveProcIds(lprocIdList);

  logger.logMsg (TRACE_FLAG, 0,
         "lbIncAsSimulator [%d]", lbIncAsSimulator);
  switch(req->cmd_type)
  {
    case ADD_NETWORK:
      {
        logger.logMsg (TRACE_FLAG, 0,
		     "processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_NETWORK, proto:%s",
				 m_protocol);
//INCTBD
        AddNetwork *nwk = &(req->u.addNwk);

        nwk->nwId = 1;
        nwk->spcBroadcastOn = TRUE;
        //nwk->defaultPc = 121; 
        if (lbIncAsSimulator == false) {
          if(nodeType == TRANSPORT_TYPE_MTP) {
            nwk->defaultPc = 121; 
          }
          else if((nodeType == TRANSPORT_TYPE_SIGTRAN) || 
                  (nodeType == TRANSPORT_TYPE_BOTH)) {
            nwk->defaultPc = 123; 
          }
        }
        else {
          if(nodeType == TRANSPORT_TYPE_MTP) {
            nwk->defaultPc = 120; 
          }
          else if((nodeType == TRANSPORT_TYPE_SIGTRAN) || 
                  (nodeType == TRANSPORT_TYPE_BOTH)) {
            nwk->defaultPc = 122; 
          }
          
        }

        if (lbIncAsSimulator == false) {
          if(nodeType == TRANSPORT_TYPE_MTP) {
            nwk->nmbSpcs = 1;
            nwk->selfPc[0] = 121;
          }
          else if(nodeType == TRANSPORT_TYPE_SIGTRAN) {
            nwk->nmbSpcs = 1;
            nwk->selfPc[0] = 123;
          }
          else if(nodeType == TRANSPORT_TYPE_BOTH) {
            nwk->nmbSpcs = 2;
            nwk->selfPc[0] = 123;
            nwk->selfPc[1] = 121;
          }
        }
        else {
          if(nodeType == TRANSPORT_TYPE_MTP) {
            nwk->nmbSpcs = 1;
            nwk->selfPc[0] = 120;
          }
          else if(nodeType == TRANSPORT_TYPE_SIGTRAN) {
            nwk->nmbSpcs = 1;
            nwk->selfPc[0] = 122;
          }
          else if(nodeType == TRANSPORT_TYPE_BOTH) {
            nwk->nmbSpcs = 2;
            nwk->selfPc[0] = 122;
            nwk->selfPc[1] = 120;
          }
        }

        nwk->niInd = 4;/*NAT_IND*/
        nwk->subService = 2;
        for (int cnt = 0; cnt < LIT_MAX_PSP; cnt++)
        {
                nwk->nwkApp[cnt] = 1;
        }
        nwk->ssf = 2;
        nwk->slsLen = 4;

				if(m_protocol == "ITU") {
        	nwk->variant = LSP_SW_ITU;
        	nwk->dpcLen  = DPC14;
        	nwk->suSwtch = LIT_SW_ITU;
        	nwk->su2Swtch= LIT_SW2_ITU;
				}
				else if(m_protocol == "NTT") {
        	nwk->variant = LSP_SW_JAPAN;
        	nwk->dpcLen  = DPC16;
        	nwk->suSwtch = LIT_SW_NTT; // M3UA User - SCCP
        	nwk->su2Swtch= LIT_SW2_TTC;// M3UA User's User - TCAP
          nwk->ssf     = SSF_TTC;
				}
				else if(m_protocol == "ANSI") {
        	nwk->variant  = LSP_SW_ANS;
        	nwk->dpcLen   = DPC24;
        	nwk->suSwtch  = LIT_SW_ANS;
        	nwk->su2Swtch = LIT_SW_ANS;
				}
         
        retVal = cliAddNetwork(&(req->u.addNwk), &lpStackResp,lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliAddNetwork FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case ADD_LINKSET:
      {
        logger.logMsg (TRACE_FLAG, 0,
						"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_LINKSET");

        AddLinkSet *lnkSet = &(req->u.lnkSet);

        lnkSet->lnkSetId = 1;
        //lnkSet->adjDpc = 120;
        if (lbIncAsSimulator == false) {
          lnkSet->adjDpc = 120;
        }
        else {
          lnkSet->adjDpc = 121;
        }
        //lnkSet->nmbActLnkReqd = 2;
        lnkSet->nmbActLnkReqd = 16;
        lnkSet->nmbCmbLnkSet = 1;
        for(int cnt=0;cnt < lnkSet->nmbCmbLnkSet;cnt++)
        {
                lnkSet->cmbLnkSet[cnt].cmbLnkSetId = 2;
                lnkSet->cmbLnkSet[cnt].lnkSetPrior = 0;
                lnkSet->cmbLnkSet[cnt].nmbPrefLinks = 0;
          for(int cnt1=0; cnt1 < lnkSet->nmbCmbLnkSet; cnt1++)
                         lnkSet->cmbLnkSet[cnt].prefLnkId[cnt1] = 0;
        }

				if(m_protocol == "ITU") {
        	lnkSet->lnkSetType = LSN_SW_ITU;
				}
				else if(m_protocol == "NTT") {
        	lnkSet->lnkSetType = LSN_SW_NTT;
				}
				else if(m_protocol == "ANSI") {
					lnkSet->lnkSetType = LSN_SW_ANS;
				} 
        
        retVal = cliAddLinkSet(&(req->u.lnkSet), &lpStackResp,lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliAddLinkSet FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case MODIFY_LINKSET:
      {
        logger.logMsg (TRACE_FLAG, 0,
						"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType MODIFY_LINKSET");

        AddLinkSet *lnkSet = &(req->u.lnkSet);
        lnkSet->lnkSetId = 2;
        lnkSet->nmbActLnkReqd = 4;
        lnkSet->nmbCmbLnkSet = 1;
        for(int cnt=0;cnt < lnkSet->nmbCmbLnkSet;cnt++)
        {
                lnkSet->cmbLnkSet[cnt].cmbLnkSetId = 2;
                lnkSet->cmbLnkSet[cnt].lnkSetPrior = 1;
                lnkSet->cmbLnkSet[cnt].nmbPrefLinks = 1;
          for(int cnt1=0; cnt1 < lnkSet->nmbCmbLnkSet; cnt1++)
                         lnkSet->cmbLnkSet[cnt].prefLnkId[cnt1] = 1;
        }
 
        retVal = cliModLinkSet(&(req->u.lnkSet), &lpStackResp,lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliModLinkSet FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break; 
    case ADD_LINK:
      {
        logger.logMsg (TRACE_FLAG, 0,
							"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_LINK");
        AddLink *lnk = &(req->u.lnk);
        lnk->lnkSetId = 1;
        //lnk->opc = 121;
        //lnk->adjDpc = 120;
        if (lbIncAsSimulator == false) {
          lnk->opc = 121;
          lnk->adjDpc = 120;
        }
        else {
          lnk->opc = 120;
          lnk->adjDpc = 121;
        }

				if(m_protocol == "ITU") {
        	lnk->lnkType = LSN_SW_ITU;
          lnk->dpcLen = DPC14;
				}
				else if(m_protocol == "NTT") {
        	lnk->lnkType = LSN_SW_NTT;
        	//lnk->lnkType   = 6;	// NTT_Q703
          lnk->ssf       = SSF_TTC;
          lnk->dpcLen = DPC16;
				}
				else if(m_protocol == "ANSI") {
        	lnk->lnkType = LSN_SW_ANS;
				}

        retVal = cliAddLink(&(req->u.lnk), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliAddLink FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case MODIFY_LINK:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType MODIFY_LINK");

        AddLink *lnk = &(req->u.lnk);
        cmMemset((U8 *)lnk, 0, sizeof(AddLink));
        lnk->lnkId = 1;
        lnk->lnkSetId = 1;
        lnk->opc = 121;
        lnk->ssf = 1;
        lnk->slc = 1;

				if(m_protocol == "ITU") {
        	lnk->lnkType = LSN_SW_ITU;
				}
				else if(m_protocol == "NTT") {
        	lnk->lnkType = LSN_SW_NTT;
          lnk->ssf       = SSF_TTC;
				}
				else if(m_protocol == "ANSI") {
        	lnk->lnkType = LSN_SW_ANS;
				} 

        retVal = cliModLink(&(req->u.lnk), &lpStackResp,lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliModLink FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case ENABLE_LINK:
      {
	      logger.logMsg (TRACE_FLAG, 0,
						"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ENABLE_LINK");
	      retVal = cliEnaLnk(&(req->u.lnkEnable), &lpStackResp);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliEnaLnk FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case DISABLE_LINK:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DISABLE_LINK");
        retVal = cliDisableLnk(&(req->u.lnkDisable), &lpStackResp);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliDisableLnk FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }
        
      }
    case ADD_USERPART:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_USERPART");
        retVal = cliAddUserPart(&(req->u.addUserPart), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliAddUserPart FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case ENABLE_DEBUG:
      {
	      logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ENABLE_DEBUG");
	      retVal = cliEnaDebug(&(req->u.debug), lprocIdList);
      }
      break;
    case ENABLE_TRACE:
      {
	      logger.logMsg (TRACE_FLAG, 0,
						"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ENABLE_TRACE");
	      retVal = cliEnaTrace(&(req->u.trace), lprocIdList);
      }
      break;
    case ENABLE_LOCAL_SSN:
      {
	      logger.logMsg (TRACE_FLAG, 0,
				"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ENABLE_LOCAL_SSN");
        SsnEnable *lSsn = &(req->u.ssnEnable);
        lSsn->ssn = 146;
        lSsn->tcapUsapId = 0;
        lSsn->tcapLsapId = 0;
        lSsn->sccpUsapId = 0;

	      retVal = cliEnaLocalSsn(&(req->u.ssnEnable), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliEnaLocalSsn FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case ENABLE_USERPART:
      {
	      logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ENABLE_USERPART");
	      retVal = cliEnaUsrpart(&(req->u.enableUserPart), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliEnaUsrpart FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case ENABLE_ENDPOINT:
      {
	      logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ENABLE_ENDPOINT");
	      retVal = cliEnaEp(&(req->u.enableEp), lprocIdList);
      }
      break;
    case DISABLE_DEBUG:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DISABLE_DEBUG");
        retVal = cliDisableDebug(&(req->u.debug), lprocIdList);
      }
      break;
    case DISABLE_TRACE:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DISABLE_TRACE");
        retVal = cliDisableTrace(&(req->u.trace),lprocIdList);
      }
      break;
    case DISABLE_LOCAL_SSN:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DISABLE_LOCAL_SSN");
        retVal = cliDisableLocalSsn(&(req->u.ssnDisable),&lpStackResp,lprocIdList);
      }
      break;
    case DISABLE_USERPART:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DISABLE_USERPART");
        retVal = cliDisableUsrpart(&(req->u.disableUserPart), &lpStackResp, lprocIdList);
      }
      break;
    case DISABLE_ENDPOINT:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DISABLE_ENDPOINT");
        retVal = cliDisableEp(&(req->u.disableEp), lprocIdList);
      }
      break;

    case ADD_ROUTE:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_ROUTE");
        retVal = cliAddRoute(&(req->u.addRoute), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliAddRoute FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case ADD_LOCAL_SSN:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_LOCAL_SSN");

        AddLocalSsn *ssn = &(req->u.addLocalSsn);
        ssn->nwId = 1;
        ssn->nmbBpc = 0;
        //ssn->bpcList[0].bpc = 2212;
        //ssn->bpcList[0].prior = 1;
        ssn->nmbConPc = 1;
        //ssn->conPc[0] = 120;
        if (lbIncAsSimulator == false) {
          if(nodeType == TRANSPORT_TYPE_MTP)
            ssn->conPc[0] = 120;
          else if((nodeType == TRANSPORT_TYPE_SIGTRAN) || 
                  (nodeType == TRANSPORT_TYPE_BOTH))
            ssn->conPc[0] = 122;
        }
        else {
          if(nodeType == TRANSPORT_TYPE_MTP)
            ssn->conPc[0] = 121;
          else if((nodeType == TRANSPORT_TYPE_SIGTRAN) || 
                  (nodeType == TRANSPORT_TYPE_BOTH))
            ssn->conPc[0] = 123;
        }
        ssn->sccpUsapId = 0;
        ssn->ssn = 146;
        ssn->tcapUsapId = 0;
        ssn->tcapLsapId = 0;
        ssn->currentSsnState = 0;

				if(m_protocol == "ITU") {
        	ssn->swtch = LST_SW_ITU92; // TCAP Protocol Variant.
				}
				else if(m_protocol == "NTT") {
        	ssn->swtch = LST_SW_NTT_INTE_NW; // NTT Inter N/w
				}
				else if(m_protocol == "ANSI") {
        	ssn->swtch = LST_SW_ANS96; 
				} 

        retVal = cliAddLocalSsn(&(req->u.addLocalSsn), &lpStackResp,lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliAddLocalSsn FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case ADD_REMOTE_SSN:
      {
        logger.logMsg (TRACE_FLAG, 0,
				"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_REMOTE_SSN");

        AddRemoteSsn *ssn = &(req->u.addRemoteSsn);
        cmMemset((U8 *)ssn, 0, sizeof(AddRemoteSsn));

        retVal = cliAddRemoteSsn(&(req->u.addRemoteSsn), lprocIdList);
      }
      break;
    case ADD_AS:
      {
        logger.logMsg (TRACE_FLAG, 0,
						"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_AS");

        AddPs *ps = &(req->u.addPs);
        ps->nwkId = 1;
        // LIT_MODE_ACTSTANDBY: 1, LIT_MODE_LOADSHARE: 2, LIT_MODE_BROADCAST:3
        ps->mode = 1;
        // LIT_LOADSH_RNDROBIN: 1, LIT_LOADSH_SLS: 2, LIT_LOADSH_CIC: 3, LIT_LOADSH_USR_DEFINED: 4
        ps->loadShareMode = 0; 
        ps->nmbActPspReqd = 1;
        ps->nmbPsp = 1;
        ps->psp[0] = 1;
        ps->pspEpLst[0].nmbEp = 1;
        ps->pspEpLst[0].endpIds[0] = 1;

        // Route Config params
        ps->rtType = LIT_RTTYPE_PS;
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96 || SS7_BELL05)
        ps->slsMask = 0;
#endif
        //ps->sls = 1;
        ps->sioMask = 0;
        ps->sio = LIT_SU_SCCP;
        ps->currentPsState = LIT_AS_ACTIVE;


        retVal = cliAddAs(&(req->u.addPs), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliAddAs FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }
      }
      break;
    case MODIFY_AS:
      {
        logger.logMsg (TRACE_FLAG, 0,
						"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType MODIFY_AS");

        AddPs *ps = &(req->u.addPs);
        cmMemset((U8 *)ps, 0,sizeof(AddPs));
        ps->psId = 1;
        ps->routCtx = 110;
        ps->nwkId = 1;
        ps->mode = 2; //LIT_MODE_LOADSHARE;
        ps->nmbActPspReqd = 1;
        ps->nmbPsp = 1;
        ps->psp[0] = 1;


        retVal = cliModAs(&(req->u.addPs), lprocIdList);
      }
      break;

    case ADD_ASP:
      {
        logger.logMsg (TRACE_FLAG, 0,
						"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_ASP");

        AddPsp *psp = &(req->u.addpsp);
        // pspType in pspCfg 
        //LIT_PSPTYPE_LOCAL     0     /* special case for the local entity */
        //LIT_PSPTYPE_ASP       1     /* remote PSP is a ASP */
        //LIT_PSPTYPE_SGP       2     /* remote PSP is a SGP */
        //LIT_PSPTYPE_IPSP      3     /* remote PSP is a IPSP */
    
        if (lbIncAsSimulator == false) {
         psp->pspType = LIT_PSPTYPE_SGP; 
         psp->ipspMode = LIT_IPSPMODE_DE; //Double ended mode IPSP
        }
        else {
          psp->pspType = LIT_PSPTYPE_ASP; 
          psp->ipspMode = LIT_IPSPMODE_SE; //Single ended mode IPSP
        }


        psp->nwkId = 1;
        psp->includeRC = 1;
        psp->cfgForAllLps = 0;
        psp->currentPspState = 1;

        retVal = cliAddAsp(&(req->u.addpsp), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliAddAsp FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }
      }
  
      break;
    case ADD_ENDPOINT:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_ENDPOINT");
        retVal = cliAddEndPoint(&(req->u.addEp), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliAddEndPoint FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }
      }
      break;
    case BND_M3UA:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType BND_M3UA");
        retVal = cliBndM3ua(&(req->u.bindSap), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliBndM3ua FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }
      }
      break;
    case OPEN_ENDPOINT:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType OPEN_ENDPOINT");
        retVal = cliOpenEp(&(req->u.openEp), &lpStackResp);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliOpenEp FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }
      }
      break;
    case ADD_GTADDRMAP:
      {
        logger.logMsg (TRACE_FLAG, 0,
				"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_GTADDRMAP");
        static int x=0;
        char *lpcAddrMap = getenv("INGW_GTADDRMAP_CONFIG");
        //only for NTT
        if(NULL != lpcAddrMap) {
            printf("\n[GTADDRMAP]Adding GT Addr Map\n");

            AddAddrMapCfg *map = &(req->u.addAddrMapCfg);
            
#ifdef GTT_PER_NWK
            map->nwkId = 1;
#endif
            /* If TRUE replace outgoing Addr with the incoming one */
            map->replGt = 1;
            // DOMINANT: 1, LOADSHARE: 2, DOMINANT_ALTERNATE: 3, LOADSHARE_ALTERNATE: 4
            map->mode = DOMINANT;
#ifdef GTT_PER_NWK
            map->outNwId = 1;
#endif
              if(m_protocol == "NTT") {
                map->sw = SW_JAPAN;         // SW_ITU:1, SW_ANSI:2, SW_CHINA:6, SW_JAPAN:7
                map->format = GTFRMT_2;     // GT_WITH_TT
              }
              else if(m_protocol == "ITU") {
                map->sw = SW_ITU;           // SW_ITU:1, SW_ANSI:2, SW_CHINA:6, SW_JAPAN:7
                map->format = GTFRMT_4;     // GT_WITH_TT_NP_ENC_NAI
                map->outAddr[0].format = GTFRMT_4;
              }

              if (map->format == GTFRMT_1) {
                map->oddEven = OE_ODD;     // OE_EVEN: 0x00, OE_ODD:0x01
                map->natAddr = NA_INTNUM;
              }
              else if (map->format == GTFRMT_3) {
                map->tType = 0x01;
                map->numPlan = NP_ISDN;
                // ES_UNKN: 00, ES_BCDODD: 0x01, ES_BCDEVEN: 0x02, ES_NWSPEC: 0x03
                map->encSch = ES_BCDODD;
              }
              else if (map->format == GTFRMT_4) {
                map->tType = 0x01;
                map->numPlan = NP_ISDN;
                // ES_UNKN: 00, ES_BCDODD: 0x01, ES_BCDEVEN: 0x02, ES_NWSPEC: 0x03
                map->encSch = ES_BCDODD;
                map->natAddr = NA_INTNUM;
              }
              
            //actionType,gtdigits,ttype,sd,ed,pc:ssn:ttype:gtdigits;pc:ssn:ttype:gtdigits|
            vector<string> lAddrMapVector; 
            vector<string> lAddrMapFields; 
            vector<string> loutAddrMapList;
            vector<string> loutAddrFields; 

            string  lsAddrMap(lpcAddrMap);
            printf("\n[AddrMap]Env Var Read [%s]\n",lsAddrMap.c_str());
            g_tokenizeValue(lsAddrMap, G_PIPE_DELIM, lAddrMapVector);
             
            for(int i =0; i < lAddrMapVector.size();i++)
            {
              
              printf("\n[AddrMap]AddrMap<%d> [%s]\n",i,lAddrMapVector[i].c_str());
              lAddrMapFields.clear();
              g_tokenizeValue(lAddrMapVector[i], G_COMMA_DELIM, lAddrMapFields);

              if(GT_ADDRMAP_FIELDS != lAddrMapFields.size()) {
                printf("\n[AddrMap]<%d>Invalid string <%s> Fields %d\n",
                  i,lAddrMapVector[i].c_str(),lAddrMapFields.size());
                
                return -1;
              }
	    		  	// read digits from environment variable 
    			  	
              printf("\n[AddrMap]<%d> GtDigits <%s>\n",i,
                lAddrMapFields[GT_ADDRMAP_GT_DIGITS_INDEX].c_str());
               
              printf("\n[AddrMap]<%d> Action Type <%s>\n",i,
                lAddrMapFields[GT_ADDRMAP_ACTNTYPE_INDEX].c_str());
               
              printf("\n[AddrMap]<%d> Translation type <%s>\n",i,
                lAddrMapFields[GT_ADDRMAP_TTYPE_INDEX].c_str());
              
              if (map->format == GTFRMT_2) {
                  map->tType = atoi(lAddrMapFields[GT_ADDRMAP_TTYPE_INDEX].c_str());//0xe8;
              }
               
              map->actn.nmbActns = 1;
              strncpy((char *)map->gtDigits,lAddrMapFields[GT_ADDRMAP_GT_DIGITS_INDEX].c_str()
                ,lAddrMapFields[GT_ADDRMAP_GT_DIGITS_INDEX].size());

              if (0 == strcmp(lAddrMapFields[GT_ADDRMAP_ACTNTYPE_INDEX].c_str(), "SP_ACTN_FIX"))
                map->actn.type =  SP_ACTN_FIX;

              else if (0 == strcmp(lAddrMapFields[GT_ADDRMAP_ACTNTYPE_INDEX].c_str(), "SP_ACTN_VAR_ASC")) 
                map->actn.type = SP_ACTN_VAR_ASC;

              else if (0 == strcmp(lAddrMapFields[GT_ADDRMAP_ACTNTYPE_INDEX].c_str(), "SP_ACTN_VAR_DES")) 
                map->actn.type = SP_ACTN_VAR_DES;

              else if (0 == strcmp(lAddrMapFields[GT_ADDRMAP_ACTNTYPE_INDEX].c_str(), "SP_ACTN_CONST")) 
                map->actn.type = SP_ACTN_CONST;

              else if (0 == strcmp(lAddrMapFields[GT_ADDRMAP_ACTNTYPE_INDEX].c_str(), "SP_ACTN_GT_TO_PC")) 
                map->actn.type = SP_ACTN_GT_TO_PC;

              else if (0 == strcmp(lAddrMapFields[GT_ADDRMAP_ACTNTYPE_INDEX].c_str(), "SP_ACTN_INSERT_PC")) 
                map->actn.type = SP_ACTN_INSERT_PC;

              else if (0 == strcmp(lAddrMapFields[GT_ADDRMAP_ACTNTYPE_INDEX].c_str(), "SP_ACTN_STRIP_PC"))
                map->actn.type = SP_ACTN_STRIP_PC;
              #ifdef SP_SEL_INS_GT
              else if (0 == strcmp(lAddrMapFields[GT_ADDRMAP_ACTNTYPE_INDEX].c_str(), "SP_ACTN_SELINS")) 
                map->actn.type = SP_ACTN_SELINS;
              #endif
              else{
                printf("+VER+[ADDRMAP] ACTION TYPE NOT SUPPORTED %s",
                  lAddrMapFields[GT_ADDRMAP_ACTNTYPE_INDEX].c_str());
                
                return -1;
              }
              map->actn.nmbActns = 1;
              map->gtDigLen = lAddrMapFields[GT_ADDRMAP_GT_DIGITS_INDEX].size();

              printf("+VER+[ADDRMAP]Digits len %d",map->gtDigLen);
              printf("+VER+[ADDRMAP]SD %s",lAddrMapFields[GT_ADDRMAP_SD_INDEX].c_str());
              printf("+VER+[ADDRMAP]ED %s",lAddrMapFields[GT_ADDRMAP_ED_INDEX].c_str());
              
              map->actn.startDigit = atoi(lAddrMapFields[GT_ADDRMAP_SD_INDEX].c_str());
              map->actn.endDigit   = atoi(lAddrMapFields[GT_ADDRMAP_ED_INDEX].c_str());
              if(map->actn.startDigit > map->gtDigLen ||  map->actn.endDigit > map->gtDigLen){
					  	  printf("\n+VER+[ADDRMAP]Invalid Sd, Ed \n");
                
                return -1;
              }
  
              //get the list of ; separated outaddr strings in loutAddrMapList
              loutAddrMapList.clear();
              g_tokenizeValue(lAddrMapFields[GT_ADDRMAP_OUTADDRLST_INDEX]
                , ";", loutAddrMapList);
              printf("+VER+[ADDRMAP] NMB OUTADDRS <%d> String <%s>",
                loutAddrMapList.size(),lAddrMapFields[GT_ADDRMAP_OUTADDRLST_INDEX].c_str());
              

              map->numEntity = loutAddrMapList.size();
     
              map->outAddr[i].spHdrOpt = 1;
              for(int j= 0; j< loutAddrMapList.size();j++)
              {
                loutAddrFields.clear(); 
                printf("+VER+[ADDRMAP]tokenize outaddr <%d> <%s>",j,loutAddrMapList[j].c_str());
                g_tokenizeValue(loutAddrMapList[j]
                  , ":", loutAddrFields);
            
                if(GT_OUTADDR_FIELDS != loutAddrFields.size()){
                  printf("+VER+[ADDRMAP] Invalid String <%s> size <%d>",
                    loutAddrMapList[j].c_str(),loutAddrFields.size());
                  return -1;
                } 
                if(m_protocol == "NTT") {
                  map->outAddr[j].format = GTFRMT_2;
                  map->outAddr[j].niInd = INAT_IND; // INAT_IND: 0x00, NAT_IND: 0x01
                  map->outAddr[j].swtch = SW_JAPAN;
                  map->outAddr[j].ssf = SSF_TTC;
                  map->outAddr[j].rtgInd = RTE_GT; // RTE_GT: 0x00, RTE_SSN: 0x01

                  map->outAddr[j].ssnInd = 1;
                  map->outAddr[j].ssn = atoi(loutAddrFields[GT_OUTADDR_SSN].c_str());

					  	  	// As per Ajay (ccpu) we should provide point code 
					  	  	// to which above digit shall map to. 
					  	  	// out address means the address to which call shall be routed/
                    printf("\n[ADDRMAP] PC  <%s>\n",loutAddrFields[GT_OUTADDR_PC].c_str());
                    printf("\n[ADDRMAP] SSN <%s>\n",loutAddrFields[GT_OUTADDR_SSN].c_str());
                    
                    map->outAddr[j].pc = atoi(loutAddrFields[GT_OUTADDR_PC].c_str());
                }
                

                
                if (map->outAddr[j].format == GTFRMT_2) {
                    printf("\n[ADDRMAP] Ttype  <%s>\n",loutAddrFields[GT_OUTADDR_TTYPE].c_str());
                    
                    map->outAddr[j].tType = atoi(loutAddrFields[GT_OUTADDR_TTYPE].c_str());
                }

                printf("\n[ADDRMAP] Digits<outaddr %d> %s\n",j,loutAddrFields[GT_OUTADDR_DIGITS].c_str());
                
                map->outAddr[j].gtDigLen = loutAddrFields[GT_OUTADDR_DIGITS].size(); //parse
                strncpy((char *)map->outAddr[i].gtDigits,loutAddrFields[GT_OUTADDR_DIGITS].c_str() ,
                  loutAddrFields[GT_OUTADDR_DIGITS].size());

              }
              cliAddGtAddrMap(&(req->u.addAddrMapCfg), &lpStackResp, lprocIdList);

              if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
                logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                       "+CONF+ cliAddGtAddrMap FAILED");
                // In case where command execution failed at stack level
                retVal = -1;
              }

            }
        }    
      
        else if(x == 0){
            x++;
            logger.logMsg (TRACE_FLAG, 0,"INGwSmWrapper::Using first address map");
            AddAddrMapCfg *map = &(req->u.addAddrMapCfg);

#ifdef GTT_PER_NWK
            map->nwkId = 1;
#endif

            //LSP_SW_ITU   SW_ITU
            //LSP_SW_ANS   SW_ANSI
            //LSP_SW_CHINA SW_CHINA
            //LSP_SW_JAPAN SW_JAPAN
            //
            //LSP_SW_ITU88   11
            //LSP_SW_ITU92   12
            //LSP_SW_ANS88   13
            //LSP_SW_ANS92   14
            //LSP_SW_ITU96   15
            //LSP_SW_ANS96   16
            //LSP_SW_BELL05  17
            //LSP_SW_GSM0806 18
            //LSP_SW_ITU2001 19

            /* If TRUE replace outgoing Addr with the incoming one */
            map->replGt = 1;
            // DOMINANT: 1, LOADSHARE: 2, DOMINANT_ALTERNATE: 3, LOADSHARE_ALTERNATE: 4
            map->mode = DOMINANT;
#ifdef GTT_PER_NWK
            map->outNwId = 1;
#endif

            if (nodeType == TRANSPORT_TYPE_BOTH) {
              map->numEntity = 2;
            }
            else {
              map->numEntity = 1;
            }

            if(m_protocol == "NTT") {
              map->sw = SW_JAPAN;         // SW_ITU:1, SW_ANSI:2, SW_CHINA:6, SW_JAPAN:7
              map->format = GTFRMT_2;     // GT_WITH_TT
              map->outAddr[0].format = GTFRMT_2;

              if (nodeType == TRANSPORT_TYPE_BOTH) {
                map->outAddr[1].format = GTFRMT_2;
              }
            }
            else if(m_protocol == "ITU") {
              map->sw = SW_ITU;           // SW_ITU:1, SW_ANSI:2, SW_CHINA:6, SW_JAPAN:7
              map->format = GTFRMT_4;     // GT_WITH_TT_NP_ENC_NAI
              map->outAddr[0].format = GTFRMT_4;

              if (nodeType == TRANSPORT_TYPE_BOTH) {
                map->outAddr[1].format = GTFRMT_4;
              }
            }

            if (map->format == GTFRMT_1) {
              map->oddEven = OE_ODD;     // OE_EVEN: 0x00, OE_ODD:0x01
              map->natAddr = NA_INTNUM;
            }
            else if (map->format == GTFRMT_2) {
                map->tType = 0xe8;
            }
            else if (map->format == GTFRMT_3) {
              map->tType = 0x01;
              map->numPlan = NP_ISDN;
              // ES_UNKN: 00, ES_BCDODD: 0x01, ES_BCDEVEN: 0x02, ES_NWSPEC: 0x03
              map->encSch = ES_BCDODD;
            }
            else if (map->format == GTFRMT_4) {
              map->tType = 0x01;
              map->numPlan = NP_ISDN;
              // ES_UNKN: 00, ES_BCDODD: 0x01, ES_BCDEVEN: 0x02, ES_NWSPEC: 0x03
              map->encSch = ES_BCDODD;
              map->natAddr = NA_INTNUM;
            }
            
            map->actn.type = SP_ACTN_FIX;
            map->actn.nmbActns = 1;

	    			// read digits from environment variable 
    				char *gttStr = getenv("GTT_DIGITS");
						if(gttStr == NULL){ 
            	strncpy((char *)map->gtDigits,"1234",4);
            	map->gtDigLen        = 4;
            	map->actn.startDigit = 1;
            	map->actn.endDigit   = 4;
						}
						else {
            	strncpy((char *)map->gtDigits,gttStr,strlen(gttStr));
            	map->actn.type = SP_ACTN_FIX;
              map->actn.nmbActns = 1;
            	map->gtDigLen = strlen(gttStr);
            	map->actn.startDigit = 1;
            	map->actn.endDigit   = 4;
							printf("\nRajeev......GTT DIGITS...diglen[%d], dig[%s]\n",
							map->gtDigLen, gttStr);
						}

            map->outAddr[0].spHdrOpt = 1;

            if(m_protocol == "NTT") {
              map->outAddr[0].niInd = INAT_IND; // INAT_IND: 0x00, NAT_IND: 0x01
              map->outAddr[0].swtch = SW_JAPAN;
              map->outAddr[0].ssf = SSF_TTC;
              map->outAddr[0].rtgInd = RTE_GT; // RTE_GT: 0x00, RTE_SSN: 0x01

              map->outAddr[0].ssnInd = 1;
              map->outAddr[0].ssn = 146;

							// As per Ajay (ccpu) we should provide point code 
							// to which above digit shall map to. 
							// out address means the address to which call shall be routed
               map->outAddr[0].pcInd = true;
              if (lbIncAsSimulator == false) {
                if(nodeType == TRANSPORT_TYPE_MTP) {
                  map->outAddr[0].pc = 121;
                }
                else if(nodeType == TRANSPORT_TYPE_SIGTRAN) {
                  map->outAddr[0].pc = 123;
                }
                else if (nodeType == TRANSPORT_TYPE_BOTH) {
                  map->outAddr[0].pc = 123;
                }
              }
              else {
                map->outAddr[0].pc = 122;
              }

              map->outAddr[0].gtDigLen = 4;
              strncpy((char *)map->outAddr[0].gtDigits, "1234",4);

              if (nodeType == TRANSPORT_TYPE_BOTH) {
                map->outAddr[1].spHdrOpt = 1;
  
                map->outAddr[1].niInd = INAT_IND; // INAT_IND: 0x00, NAT_IND: 0x01
                map->outAddr[1].swtch = SW_JAPAN;
                map->outAddr[1].ssf = SSF_TTC;
                map->outAddr[1].rtgInd = RTE_GT; // RTE_GT: 0x00, RTE_SSN: 0x01

                map->outAddr[1].ssnInd = 1;
                map->outAddr[1].ssn = 146;

							  // As per Ajay (ccpu) we should provide point code 
							  // to which above digit shall map to. 
							  // out address means the address to which call shall be routed
                 map->outAddr[1].pcInd = true;
                if (lbIncAsSimulator == false) {
                  map->outAddr[1].pc = 121;
                }
                else {
                  map->outAddr[1].pc = 123;
                }

                map->outAddr[1].gtDigLen = 4;
                strncpy((char *)map->outAddr[1].gtDigits, "2009",4);
              }
            }
            else if(m_protocol == "ITU") {
              map->outAddr[0].niInd = NAT_IND; // INAT_IND: 0x00, NAT_IND: 0x01
              map->outAddr[0].swtch = SW_ITU;
              map->outAddr[0].ssf = SSF_NAT;
              map->outAddr[0].rtgInd = RTE_SSN; // RTE_GT: 0x00, RTE_SSN: 0x01

              map->outAddr[0].ssnInd = 1;
              map->outAddr[0].ssn = 146;
              map->outAddr[0].pcInd = 1;
              //map->outAddr[0].pc = 121;
              if (lbIncAsSimulator == false) {
                if(nodeType == TRANSPORT_TYPE_MTP) {
                  map->outAddr[0].pc = 121;
                }
                else if((nodeType == TRANSPORT_TYPE_SIGTRAN) || 
                        (nodeType == TRANSPORT_TYPE_BOTH)) {
                  map->outAddr[0].pc = 123;
                }
              }
              else {
                map->outAddr[0].pc = 122;
              }

              map->outAddr[0].gtDigLen = 4;
              strncpy((char *)map->outAddr[0].gtDigits, "1234",4);
            }

            for (int i = 0; i < map->numEntity; i++) {
              if (map->outAddr[i].format == GTFRMT_1) {
                map->outAddr[i].oddEven = OE_ODD;     // OE_EVEN: 0x00, OE_ODD:0x01
                map->outAddr[i].natAddr = NA_INTNUM;
              }
              else if (map->outAddr[0].format == GTFRMT_2) {
                map->outAddr[i].tType = 0xe8;
              }
              else if (map->outAddr[0].format == GTFRMT_3) {
                map->outAddr[i].tType = 0x01;
                map->outAddr[i].numPlan = NP_ISDN;
                map->outAddr[i].encSch = ES_BCDODD;
              }
              else if (map->outAddr[0].format == GTFRMT_4) {
                map->outAddr[i].tType = 0x01;
                map->outAddr[i].numPlan = NP_ISDN;
                map->outAddr[i].encSch = ES_BCDODD;
                map->outAddr[i].natAddr = NA_INTNUM;
              }
            }
         }
        else 
        {
            logger.logMsg (TRACE_FLAG, 0,"INGwSmWrapper::Using second address map");
            AddAddrMapCfg *map = &(req->u.addAddrMapCfg);

#ifdef GTT_PER_NWK
            map->nwkId = 1;
#endif

            //LSP_SW_ITU   SW_ITU
            //LSP_SW_ANS   SW_ANSI
            //LSP_SW_CHINA SW_CHINA
            //LSP_SW_JAPAN SW_JAPAN
            //
            //LSP_SW_ITU88   11
            //LSP_SW_ITU92   12
            //LSP_SW_ANS88   13
            //LSP_SW_ANS92   14
            //LSP_SW_ITU96   15
            //LSP_SW_ANS96   16
            //LSP_SW_BELL05  17
            //LSP_SW_GSM0806 18
            //LSP_SW_ITU2001 19

            /* If TRUE replace outgoing Addr with the incoming one */
            map->replGt = 1;
            // DOMINANT: 1, LOADSHARE: 2, DOMINANT_ALTERNATE: 3, LOADSHARE_ALTERNATE: 4
            map->mode = DOMINANT;
#ifdef GTT_PER_NWK
            map->outNwId = 1;
#endif

            if (nodeType == TRANSPORT_TYPE_BOTH) {
              map->numEntity = 2;
            }              
            else {
              map->numEntity = 1;
            }

            if(m_protocol == "NTT") {
              map->sw = SW_JAPAN;         // SW_ITU:1, SW_ANSI:2, SW_CHINA:6, SW_JAPAN:7
              map->format = GTFRMT_2;     // GT_WITH_TT
              map->outAddr[0].format = GTFRMT_2;
              
              if (nodeType == TRANSPORT_TYPE_BOTH) {
                map->outAddr[1].format = GTFRMT_2;
              }
            }
            else if(m_protocol == "ITU") {
              map->sw = SW_ITU;           // SW_ITU:1, SW_ANSI:2, SW_CHINA:6, SW_JAPAN:7
              map->format = GTFRMT_4;     // GT_WITH_TT_NP_ENC_NAI
              map->outAddr[0].format = GTFRMT_4;
              
              if (nodeType == TRANSPORT_TYPE_BOTH) {
                map->outAddr[1].format = GTFRMT_4;
              }
            }

            if (map->format == GTFRMT_1) {
              map->oddEven = OE_ODD;     // OE_EVEN: 0x00, OE_ODD:0x01
              map->natAddr = NA_INTNUM;
            }
            else if (map->format == GTFRMT_2) {
                map->tType = 0xe9;
            }
            else if (map->format == GTFRMT_3) {
              map->tType = 0x01;
              map->numPlan = NP_ISDN;
              // ES_UNKN: 00, ES_BCDODD: 0x01, ES_BCDEVEN: 0x02, ES_NWSPEC: 0x03
              map->encSch = ES_BCDODD;
            }
            else if (map->format == GTFRMT_4) {
              map->tType = 0x01;
              map->numPlan = NP_ISDN;
              // ES_UNKN: 00, ES_BCDODD: 0x01, ES_BCDEVEN: 0x02, ES_NWSPEC: 0x03
              map->encSch = ES_BCDODD;
              map->natAddr = NA_INTNUM;
            }
            
            map->actn.type = SP_ACTN_FIX;
            map->actn.nmbActns = 1;

	    			// read digits from environment variable 
    				char *gttStr = getenv("GTT_DIGITS1");
						if(gttStr == NULL){ 
            	strncpy((char *)map->gtDigits,"1234",4);
            	map->gtDigLen        = 4;
            	map->actn.startDigit = 1;
            	map->actn.endDigit   = 4;
						}
						else {
            	strncpy((char *)map->gtDigits,gttStr,strlen(gttStr));
            	map->actn.type = SP_ACTN_FIX;
              map->actn.nmbActns = 1;
            	map->gtDigLen = strlen(gttStr);
            	map->actn.startDigit = 1;
            	map->actn.endDigit   = 4;
							printf("\nRajeev......GTT DIGITS1...diglen[%d], dig[%s] x[%d]\n",
							map->gtDigLen, gttStr, x);
						}


            map->outAddr[0].spHdrOpt = 1;

            if(m_protocol == "NTT") {

              map->outAddr[0].spHdrOpt = 1;
  
              map->outAddr[0].niInd = INAT_IND; // INAT_IND: 0x00, NAT_IND: 0x01
              map->outAddr[0].swtch = SW_JAPAN;
              map->outAddr[0].ssf = SSF_TTC;
              map->outAddr[0].rtgInd = RTE_GT; // RTE_GT: 0x00, RTE_SSN: 0x01

              map->outAddr[0].ssnInd = 1;
              map->outAddr[0].ssn = 146;

							// As per Ajay (ccpu) we should provide point code 
							// to which above digit shall map to. 
							// out address means the address to which call shall be routed
               map->outAddr[0].pcInd = true;
              if (lbIncAsSimulator == false) {
                //map->outAddr[0].pc = 120; // For SS7
                map->outAddr[0].pc = 122;  // For Sigtran outgoing through INC TelnetIntf
                if(nodeType == TRANSPORT_TYPE_MTP) {
                  map->outAddr[0].pc = 120;
                }
                else if(nodeType == TRANSPORT_TYPE_SIGTRAN) {
                
                  map->outAddr[0].pc = 122;     // SGP pointcode
                  //map->outAddr[0].pc = 124;   // VSP pointcode
                }
                else if (nodeType == TRANSPORT_TYPE_BOTH) {
                  map->outAddr[0].pc = 122;     // SGP pointcode
                  //map->outAddr[0].pc = 124;   // VSP pointcode
                }
              }
              else {
                //map->outAddr[0].pc = 121; // For SS7
                map->outAddr[0].pc = 123; // For Sigtran outgoing through INC TelnetIntf
              }

              map->outAddr[0].gtDigLen = 4;
              strncpy((char *)map->outAddr[0].gtDigits, "2009",4);

              if (nodeType == TRANSPORT_TYPE_BOTH) {
              map->outAddr[1].spHdrOpt = 1;
  
              map->outAddr[1].niInd = INAT_IND; // INAT_IND: 0x00, NAT_IND: 0x01
              map->outAddr[1].swtch = SW_JAPAN;
              map->outAddr[1].ssf = SSF_TTC;
              map->outAddr[1].rtgInd = RTE_GT; // RTE_GT: 0x00, RTE_SSN: 0x01

              map->outAddr[1].ssnInd = 1;
              map->outAddr[1].ssn = 146;

							// As per Ajay (ccpu) we should provide point code 
							// to which above digit shall map to. 
							// out address means the address to which call shall be routed
               map->outAddr[1].pcInd = true;
              if (lbIncAsSimulator == false) {
                  map->outAddr[1].pc = 120;
                }
                else {
                  map->outAddr[1].pc = 121;   // For SS7
                  //map->outAddr[1].pc = 123; // For Sigtran outgoing through INC TelnetIntf
                }

                map->outAddr[1].gtDigLen = 4;
                strncpy((char *)map->outAddr[1].gtDigits, "2009",4);
              }
            }
            else if(m_protocol == "ITU") {
              map->outAddr[0].niInd = NAT_IND; // INAT_IND: 0x00, NAT_IND: 0x01
              map->outAddr[0].swtch = SW_ITU;
              map->outAddr[0].ssf = SSF_NAT;
              map->outAddr[0].rtgInd = RTE_SSN; // RTE_GT: 0x00, RTE_SSN: 0x01

              map->outAddr[0].ssnInd = 1;
              map->outAddr[0].ssn = 146;
              map->outAddr[0].pcInd = 1;
              //map->outAddr[0].pc = 121;
              if (lbIncAsSimulator == false) {
                map->outAddr[0].pc = 121;
              }
              else {
                //map->outAddr[0].pc = 121; // For SS7
                map->outAddr[0].pc = 123; // For Sigtran
              }

              map->outAddr[0].gtDigLen = 4;
              strncpy((char *)map->outAddr[0].gtDigits, "2009",4);
            }

            for (int i = 0; i < map->numEntity; i++) {
              if (map->outAddr[i].format == GTFRMT_1) {
                map->outAddr[i].oddEven = OE_ODD;     // OE_EVEN: 0x00, OE_ODD:0x01
                map->outAddr[i].natAddr = NA_INTNUM;
              }
              else if (map->outAddr[0].format == GTFRMT_2) {
                map->outAddr[i].tType = 0xe9;
              }
              else if (map->outAddr[0].format == GTFRMT_3) {
                map->outAddr[i].tType = 0x01;
                map->outAddr[i].numPlan = NP_ISDN;
                map->outAddr[i].encSch = ES_BCDODD;
              }
              else if (map->outAddr[0].format == GTFRMT_4) {
                map->outAddr[i].tType = 0x01;
                map->outAddr[i].numPlan = NP_ISDN;
                map->outAddr[i].encSch = ES_BCDODD;
                map->outAddr[i].natAddr = NA_INTNUM;
              }
            }
         } 
        x++;
        
        cliAddGtAddrMap(&(req->u.addAddrMapCfg), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliAddGtAddrMap FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case ADD_GTRULE:
	  {
			static int y=0;
      char *lpcGtRule = getenv("INGW_ADD_GT_RULE");

      if(NULL != lpcGtRule) {
        AddGtRule *gt = &(req->u.addGtRule);
        gt->nwId = 1;

        if(m_protocol == "NTT") 
        {
          gt->sw = SW_JAPAN;     // SW_ITU:1, SW_ANSI:2, SW_CHINA:6, SW_JAPAN:7
          gt->format = GTFRMT_2; // GT_WITH_TT
          gt->formatPres = 1;
        }
        else
        {
          printf("+VER+ [ADD_GTRULE]protocol varient is not \"NTT\" ");
          
        }
        
        string lsGtRuleStr(lpcGtRule);
        
        printf("\n+VER+ [ADD_GTRULE]GT Rule String <%s>\n",lsGtRuleStr.c_str());
        
         
        vector<string> lgtRuleList;
        vector<string> lgtRuleFieldVector;
        vector<string> lActnStrVector;
        vector<string> lActnList;

        g_tokenizeValue(lsGtRuleStr, G_PIPE_DELIM, lgtRuleList); 
 
        for(int jj = 0;jj < lgtRuleList.size();jj++)
        {        

          printf("\n+VER+[ADD_GTRULE] GT Rule <%d> [%s]",jj,lgtRuleList[jj].c_str()); 
          lgtRuleFieldVector.clear();
          g_tokenizeValue(lgtRuleList[jj], G_COMMA_DELIM, lgtRuleFieldVector); 
          if(GT_RULE_FIELDS != lgtRuleFieldVector.size()){
            printf("\n+VER+[ADD_GTRULE] Invalid String %s\n",(lgtRuleList[jj]).c_str());
            
            return -1;
          } 
          
          if (gt->format == GTFRMT_2) {
            printf("\n+VER+[ADD_GTRULE] TType <%s>\n",
              lgtRuleFieldVector[GT_RULE_TTYPE_INDEX].c_str());
            
            gt->tType = atoi(lgtRuleFieldVector[GT_RULE_TTYPE_INDEX].c_str());//0xe8;
            gt->tTypePres = 1;
          }
          else
          {
            printf("+VER+[ADD_GTRULE] GT FRMT NOT GTFRMT_2");
            
          }

          printf("\n+VER+[ADD_GTRULE] NmbActions <%s>\n",
            lgtRuleFieldVector[GT_RULE_NMB_ACTNS_INDEX].c_str());
          

          gt->nmbActns = atoi(lgtRuleFieldVector[GT_RULE_NMB_ACTNS_INDEX].c_str());
//
          lActnList.clear();
          g_tokenizeValue(lgtRuleFieldVector[GT_RULE_ACTN_STR_INDEX],
            ";",lActnList);        
          if(lActnList.size() != gt->nmbActns){
            printf("\n+VER+[ADD_GTRULE] Nmb Actions Mismatch  <%d>\n",
              lActnList.size());
            
          } 
          for(int ii = 0;ii<lActnList.size();ii++) 
          {
            lActnStrVector.clear();
            g_tokenizeValue(lActnList[ii],
                            ":",
                            lActnStrVector);

            printf("\n+VER+[ADD_GTRULE] Action String %s\n",
              lActnList[ii].c_str());

            if(GT_RULE_ACTN_FIELDS != lActnStrVector.size()){
                printf("\n+VER+[ADD_GTRULE] Invalid String %s\n",
                lActnList[ii].c_str());
                return -1;
              
            }
//
            //for(int i=0; i < lActnStrVector.size();i++) 
            //{
              if (0 == strcmp(lActnStrVector[GT_RULE_ACTNTYPE_INDEX].c_str(), "SP_ACTN_FIX"))
                gt->actn[ii].type =  SP_ACTN_FIX;

              else if (0 == strcmp(lActnStrVector[GT_RULE_ACTNTYPE_INDEX].c_str(), "SP_ACTN_VAR_ASC"))
                gt->actn[ii].type = SP_ACTN_VAR_ASC;

              else if (0 == strcmp(lActnStrVector[GT_RULE_ACTNTYPE_INDEX].c_str(), "SP_ACTN_VAR_DES"))
                gt->actn[ii].type = SP_ACTN_VAR_DES;

              else if (0 == strcmp(lActnStrVector[GT_RULE_ACTNTYPE_INDEX].c_str(), "SP_ACTN_CONST"))
                gt->actn[ii].type = SP_ACTN_CONST;

              else if (0 == strcmp(lActnStrVector[GT_RULE_ACTNTYPE_INDEX].c_str(), "SP_ACTN_GT_TO_PC"))
                gt->actn[ii].type = SP_ACTN_GT_TO_PC;

              else if (0 == strcmp(lActnStrVector[GT_RULE_ACTNTYPE_INDEX].c_str(), "SP_ACTN_INSERT_PC"))
                gt->actn[ii].type = SP_ACTN_INSERT_PC;

              else if (0 == strcmp(lActnStrVector[GT_RULE_ACTNTYPE_INDEX].c_str(), "SP_ACTN_STRIP_PC"))
                gt->actn[ii].type = SP_ACTN_STRIP_PC;
              #ifdef SP_SEL_INS_GT
              else if (0 == strcmp(lActnStrVector[GT_RULE_ACTNTYPE_INDEX].c_str(), "SP_ACTN_SELINS"))
                gt->actn[ii].type = SP_ACTN_SELINS;
              #endif
              else{
                printf("+VER+[ADD_GTRULE] ACTION TYPE NOT SUPPORTED %s",
                  lActnStrVector[GT_RULE_ACTNTYPE_INDEX].c_str());
                
                return -1;
              }
              
                // Required in Association Configuration (part of SpAssoCfg)
              gt->actn[ii].nmbActns   = 1;

              printf("+VER+[ADD_GTRULE] Start Digit <%s>, End Digit <%s>",
                lActnStrVector[GT_RULE_ACTN_SD_INDEX].c_str(),
                lActnStrVector[GT_RULE_ACTN_ED_INDEX].c_str());
              

              gt->actn[ii].startDigit = atoi(lActnStrVector[GT_RULE_ACTN_SD_INDEX].c_str());
              gt->actn[ii].endDigit   = atoi(lActnStrVector[GT_RULE_ACTN_ED_INDEX].c_str());

            //}//action loop
          }  
          retVal = cliAddGtRule(&(req->u.addGtRule), &lpStackResp, lprocIdList);

          if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
            logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                   "+CONF+ cliAddGtRule FAILED");
            // In case where command execution failed at stack level
            retVal = -1;
          }

        }//rulelist loop
  
      }
      else if (y==0) {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_GTRULE");

        AddGtRule *gt = &(req->u.addGtRule);

        gt->nwId = 1;

        if(m_protocol == "NTT") {
          gt->sw = SW_JAPAN;     // SW_ITU:1, SW_ANSI:2, SW_CHINA:6, SW_JAPAN:7
          gt->format = GTFRMT_2; // GT_WITH_TT
          gt->formatPres = 1;
        }
        else if(m_protocol == "ITU") {
          gt->sw = SW_ITU;       // SW_ITU:1, SW_ANSI:2, SW_CHINA:6, SW_JAPAN:7
          gt->format = GTFRMT_4; // GT_WITH_TT_NP_ENC_NAI
          gt->formatPres = 1;
        }

        if (gt->format == GTFRMT_1) {
          gt->oddEven = OE_ODD;
          gt->oddEvenPres = 1;
          gt->natAddr = NA_INTNUM;  // NA_SUBNUM: 0x00, NA_NATSIGNUM: 0x03, NA_INTNUM: 0x04
          gt->natAddrPres = 1;
        }
        else if (gt->format == GTFRMT_2) {
          //11101000(0xe8): [Carrier identification code + 
          //                 service identification information (signalling network connection)]
          //11101001(0xe9): [Carrier identification code + 
          //                 intra-network information (signalling network connection)]
          gt->tType = 0xe8;
          gt->tTypePres = 1;
        }
        else if (gt->format == GTFRMT_3) {
          gt->tType = 0x01;
          gt->tTypePres = 1;
          gt->numPlan = NP_ISDN;
          gt->numPlanPres = 1;
          gt->encSch = ES_BCDODD; // ES_UNKN: 00, ES_BCDODD: 0x01, ES_BCDEVEN: 0x02, ES_NWSPEC: 0x03
          gt->encSchPres = 1;
        }
        else if (gt->format == GTFRMT_4) {
          gt->tType = 0x00;
          gt->tTypePres = 1;
          gt->numPlan = NP_ISDN;
          gt->numPlanPres = 1;
          gt->encSch  = ES_BCDODD;
          gt->encSchPres = 1;
          gt->natAddr = NA_INTNUM;
          gt->natAddrPres = 1;
        }
        

        gt->nmbActns = 2;
        
        /* GTT Action Types
           ================ */
        // SP_ACTN_FIX         1   /* Fixed range */
        // SP_ACTN_VAR_ASC     2   /* Var range, ascending */
        // SP_ACTN_VAR_DES     3   /* Var range, descending */
        // SP_ACTN_CONST       4   /* Constant outAddr */
        // SP_ACTN_GT_TO_PC    5   /* GT goes into outgoing PC */
        // SP_ACTN_INSERT_PC   6   /* Insert PC in the beginning of GTAI -
        //                            for Generic Numbering Plan */
        // SP_ACTN_STRIP_PC    7   /* Strip off PC from the beginning of GTAI -
        //                            for Generic Numbering Plan */
        // SP_ACTN_SELINS      8   /* (If both LSPV2_8 and SP_SEL_INS_GT enabled) */

        gt->actn[0].type = SP_ACTN_FIX;
        // Required in Association Configuration (part of SpAssoCfg)
        gt->actn[0].nmbActns = 1;
        gt->actn[0].startDigit = 1;
        gt->actn[0].endDigit = 4;

        gt->actn[1].type = SP_ACTN_FIX;
        // Required in Association Configuration (part of SpAssoCfg)
        gt->actn[1].nmbActns = 1;
        gt->actn[1].startDigit = 7;
        gt->actn[1].endDigit = 8;

        cliAddGtRule(&(req->u.addGtRule), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliAddGtRule FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      else 
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ADD_GTRULE");

        AddGtRule *gt = &(req->u.addGtRule);

        gt->nwId = 1;

        if(m_protocol == "NTT") {
          gt->sw = SW_JAPAN;     // SW_ITU:1, SW_ANSI:2, SW_CHINA:6, SW_JAPAN:7
          gt->format = GTFRMT_2; // GT_WITH_TT
          gt->formatPres = 1;
        }
        else if(m_protocol == "ITU") {
          gt->sw = SW_ITU;       // SW_ITU:1, SW_ANSI:2, SW_CHINA:6, SW_JAPAN:7
          gt->format = GTFRMT_4; // GT_WITH_TT_NP_ENC_NAI
          gt->formatPres = 1;
        }

        if (gt->format == GTFRMT_1) {
          gt->oddEven = OE_ODD;
          gt->oddEvenPres = 1;
          gt->natAddr = NA_INTNUM;  // NA_SUBNUM: 0x00, NA_NATSIGNUM: 0x03, NA_INTNUM: 0x04
          gt->natAddrPres = 1;
        }
        else if (gt->format == GTFRMT_2) {
          //11101000(0xe8): [Carrier identification code + 
          //                 service identification information (signalling network connection)]
          //11101001(0xe9): [Carrier identification code + 
          //                 intra-network information (signalling network connection)]
          gt->tType = 0xe9;
          gt->tTypePres = 1;
        }
        else if (gt->format == GTFRMT_3) {
          gt->tType = 0x01;
          gt->tTypePres = 1;
          gt->numPlan = NP_ISDN;
          gt->numPlanPres = 1;
          gt->encSch = ES_BCDODD; // ES_UNKN: 00, ES_BCDODD: 0x01, ES_BCDEVEN: 0x02, ES_NWSPEC: 0x03
          gt->encSchPres = 1;
        }
        else if (gt->format == GTFRMT_4) {
          gt->tType = 0x00;
          gt->tTypePres = 1;
          gt->numPlan = NP_ISDN;
          gt->numPlanPres = 1;
          gt->encSch  = ES_BCDODD;
          gt->encSchPres = 1;
          gt->natAddr = NA_INTNUM;
          gt->natAddrPres = 1;
        }
        

        gt->nmbActns = 2;
        
        /* GTT Action Types
           ================ */
        // SP_ACTN_FIX         1   /* Fixed range */
        // SP_ACTN_VAR_ASC     2   /* Var range, ascending */
        // SP_ACTN_VAR_DES     3   /* Var range, descending */
        // SP_ACTN_CONST       4   /* Constant outAddr */
        // SP_ACTN_GT_TO_PC    5   /* GT goes into outgoing PC */
        // SP_ACTN_INSERT_PC   6   /* Insert PC in the beginning of GTAI -
        //                            for Generic Numbering Plan */
        // SP_ACTN_STRIP_PC    7   /* Strip off PC from the beginning of GTAI -
        //                            for Generic Numbering Plan */
        // SP_ACTN_SELINS      8   /* (If both LSPV2_8 and SP_SEL_INS_GT enabled) */

        gt->actn[0].type = SP_ACTN_FIX;
        // Required in Association Configuration (part of SpAssoCfg)
        gt->actn[0].nmbActns = 1;
        gt->actn[0].startDigit = 1;
        gt->actn[0].endDigit = 4;

        gt->actn[1].type = SP_ACTN_FIX;
        // Required in Association Configuration (part of SpAssoCfg)
        gt->actn[1].nmbActns = 1;
        gt->actn[1].startDigit = 7;
        gt->actn[1].endDigit = 8;

        cliAddGtRule(&(req->u.addGtRule), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliAddGtRule FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
			y++;
		}
      break;

    case DEL_GTADDRMAP:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DEL_GTADDRMAP");

        DelAddrMapCfg *map = &(req->u.delAddrMapCfg);
        cmMemset((U8 *)map, 0, sizeof(DelAddrMapCfg));
#ifdef GTT_PER_NWK
        map->nwkId = 1;
#endif
        map->sw = 1;
        map->format = 4;
        map->oddEven = OE_ODD;
        map->natAddr = NA_INTNUM;
        map->tType = 0;
        map->numPlan = NP_ISDN;
        map->encSch = ES_BCDODD;
        map->actn.type = 1;
        map->actn.nmbActns = 1;
        map->actn.startDigit = 1;
        map->actn.endDigit = 4;

        map->gtDigLen = 4;
        strncpy((char *)map->gtDigits,"1234",4);
        //map->gtDigits[map->gtDigLen + 1] = '\0';

        /* If TRUE replace outgoing Addr with the incoming one */
        map->replGt = 1;
        map->mode = DOMINANT;
#ifdef GTT_PER_NWK
        map->outNwId = 1;
#endif
        map->numEntity = 1;
        map->outAddr[0].spHdrOpt = 1;
        map->outAddr[0].swtch = 1;
        map->outAddr[0].ssf = 2;
        map->outAddr[0].niInd = 1;
        map->outAddr[0].rtgInd = 1;
        map->outAddr[0].ssnInd = 1;
        map->outAddr[0].pcInd = 1;
        map->outAddr[0].ssn = 1;
        map->outAddr[0].pc = 121 ;
        map->outAddr[0].format = 4;
        map->outAddr[0].oddEven = OE_ODD;
        map->outAddr[0].tType = 0;
        map->outAddr[0].natAddr = NA_INTNUM;
        map->outAddr[0].numPlan = NP_ISDN;
        map->outAddr[0].encSch = ES_BCDODD;
        map->outAddr[0].gtDigLen = 4;
        strncpy((char *)map->outAddr[0].gtDigits,"1234",4);
        //map->outAddr[0].gtDigits[map->outAddr[0].gtDigLen + 1] = '\0';

        cliDelGtAddrMap(&(req->u.delAddrMapCfg), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliDelGtAddrMap FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case DEL_GTRULE:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DEL_GTRULE");
  
        DelGtRule *gt = &(req->u.delGtRule);
        cmMemset((U8 *)gt, 0, sizeof(DelGtRule));
        gt->nwId = 1;
        gt->sw = 1;
        gt->formatPres = 1;
        gt->format = 4;
        gt->oddEven = OE_ODD;
        gt->oddEvenPres = 1;
        gt->natAddr = NA_INTNUM;
        gt->natAddrPres = 1;
        gt->tType = 0;
        gt->tTypePres = 1;
        gt->numPlan = NP_ISDN;
        gt->numPlanPres = 1;
        gt->encSch = ES_BCDODD;
        gt->encSchPres = 1;
        gt->nmbActns = 1;
        gt->actn[0].type = 1;
        gt->actn[0].nmbActns = 1;
        gt->actn[0].startDigit = 1;
        gt->actn[0].endDigit = 4;


        cliDelGtRule(&(req->u.delGtRule), &lpStackResp,lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliDelGtRule FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case DEL_LINK:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DEL_LINK");

        DelLink *lnk = &(req->u.delLnk);
        cmMemset((U8 *)lnk, 0,sizeof(DelLink));
        lnk->lnkId = 1;
        lnk->mtp2UsapId = MTP2_LINK_USAPID;
        lnk->mtp3LsapId = MTP3_LINK_LSAPID;
        //lnk->mtp2ProcId = mpSmDistributor->getTcapProvider()->selfProcId;
        lnk->mtp2ProcId = selfProcId;

        retVal = cliDelLink(&(req->u.delLnk), &lpStackResp);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliDelLink FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case DEL_LINKSET:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DEL_LINKSET");

        DelLinkSet *lset = &(req->u.delLnkSet);
        cmMemset((U8 *)lset, 0,sizeof(DelLinkSet));
        lset->lnkSetId = 1;

        retVal = cliDelLinkSet(&(req->u.delLnkSet), &lpStackResp, lprocIdList);
      }
      break;
    case DEL_ROUTE:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DEL_ROUTE");

        DelRoute *rte = &(req->u.delRoute);
        cmMemset((U8 *)rte, 0,sizeof(DelRoute));
        rte->dpc = 120;
        rte->nSapId = SCCP_USERPART_SAPID;

				if(m_protocol == "ITU") {
  				rte->upSwtch  = LSN_SW_ITU; // MTP3 Protocol Variant
				}
				else if(m_protocol == "NTT") {
  				rte->upSwtch  = LSN_SW_NTT;
				}
				else if(m_protocol == "ANSI") {
  				rte->upSwtch  = LSN_SW_ANS;
				} 

        retVal = cliDelRoute(&(req->u.delRoute), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliDelRoute FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case DEL_NETWORK:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DEL_NETWORK");

        DelNetwork *nw = &(req->u.delNwk);
        cmMemset((U8 *)nw, 0,sizeof(DelNetwork));
        nw->nwkId = 1; 

				if(m_protocol == "ITU") {
        	nw->variant = LSP_SW_ITU;
				}
				else if(m_protocol == "NTT") {
        	nw->variant = LSP_SW_JAPAN;
				}
				else if(m_protocol == "ANSI") {
        	nw->variant = LSP_SW_ANS;
				} 

        retVal = cliDelNetwork(&(req->u.delNwk), &lpStackResp,lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *)::"
                 "+CONF+ cliDelNetwork FAILED");
          // In case where command execution failed at stack level
          retVal = -1;
        }

      }
      break;
    case DEL_ENDPOINT:
      {
        logger.logMsg (TRACE_FLAG, 0,
						"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DEL_ENDPOINT");
        
        DelEndPoint *ep = &(req->u.delEp);
        cmMemset((U8 *)ep, 0,sizeof(DelEndPoint));
        //ep->sctpProcId = mpSmDistributor->getTcapProvider()->selfProcId;
        ep->sctpProcId = selfProcId;
        ep->sctpLsapId = 0; 
        ep->sctpUsapId = 0; 
        ep->m3uaLsapId = 0; 
        ep->tuclUsapId = 0; 

        retVal = cliDelEp(&(req->u.delEp),&lpStackResp,lprocIdList);
      }
      break;
    case DEL_ASP:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DEL_ASP");

        DelPsp *psp = &(req->u.delPsp);
        cmMemset((U8 *)psp, 0,sizeof(DelPsp));
        psp->pspId = 1; 

        retVal = cliDelAsp(&(req->u.delPsp), &lpStackResp, lprocIdList);
      }
      break;
    case DEL_AS:
      {
        logger.logMsg (TRACE_FLAG, 0,
						"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DEL_AS");

        DelPs *ps = &(req->u.delPs);
        cmMemset((U8 *)ps, 0,sizeof(DelPs));
        ps->psId = 1; 
        ps->nwkId = 1; 

        retVal = cliDelAs(&(req->u.delPs),&lpStackResp, lprocIdList);
      }
      break;
    case DEL_LOCAL_SSN:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DEL_LOCAL_SSN");

        DelLocalSsn *ssn = &(req->u.delLocalSsn);
        cmMemset((U8 *)ssn, 0,sizeof(DelLocalSsn));
        ssn->ssn = 146; 
        ssn->tcapLsapId = 0; 
        ssn->tcapUsapId = 0; 
        ssn->sccpUsapId = 0; 

        retVal = cliDelLocalSsn(&(req->u.delLocalSsn), &lpStackResp, lprocIdList);
      }
      break;

    case DEL_USR_PART:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DEL_USERPART");
        DelUserPart *up = &(req->u.delUserPart);
        cmMemset((U8 *)up, 0,sizeof(DelUserPart));
        up->userPartType = MTP3_USER;//M3UA_USER;
        up->mtp3UsapId = MTP3_USERPART_SAPID;
        up->m3uaUsapId = M3UA_USERPART_SAPID;
        up->sccpLsapId = SCCP_USERPART_SAPID;

        retVal = cliDelUsrPart(&(req->u.delUserPart), &lpStackResp, lprocIdList);
      }
      break;

    case ASSOC_UP:
      {
        logger.logMsg (TRACE_FLAG, 0,
				"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ASSOC_UP");

        retVal = cliAssocUp(&(req->u.m3uaAssocUp), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *) "
                 "+CONF+ cliAssocUp FAILED");
          retVal = -1;
        }
      }
      break;
    case ASSOC_DOWN:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ASSOC_DN");

        M3uaAssocDown *assoc = &(req->u.m3uaAssocDown);
        cmMemset((U8 *)assoc, 0,sizeof(M3uaAssocDown));
        assoc->pspId = 1; 
        assoc->endPointId = 1;
        //assoc->endPointId = selfProcId;
        assoc->m3uaLsapId = 0;
        
        retVal = cliAssocDn(&(req->u.m3uaAssocDown),  &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *) "
                 "+CONF+ cliAssocUp FAILED");
          retVal = -1;
        }
      }
      break;
    case ASP_UP:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ASP_UP");

        retVal = cliAspUp(&(req->u.m3uaAspUp), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *) "
                 "+CONF+ cliAspUp FAILED");
          retVal = -1;
        }
      }
      break;
    case ASP_DOWN:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ASP_DN");

        M3uaAspDown *asp = &(req->u.m3uaAspDown);
        cmMemset((U8 *)asp, 0,sizeof(M3uaAspDown));
        asp->pspId = 1;
        asp->endPointId = 1;
        //asp->endPointId = selfProcId;
        asp->m3uaLsapId = 0;

        retVal = cliAspDn(&(req->u.m3uaAspDown), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *) "
                 "+CONF+ cliAspDown FAILED");
          retVal = -1;
        }
      }
      break;
    case ASP_ACTIVE:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ASP_ACTIVE");
       
        retVal = cliAspActive(&(req->u.m3uaAspAct), &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *) "
                 "+CONF+ cliAspActive FAILED");
          retVal = -1;
        }
      }
      break;
    case ASP_INACTIVE:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ASP_INACT");
       
        M3uaAspInAct *asp = &(req->u.m3uaAspInact);
        cmMemset((U8 *)asp, 0,sizeof(M3uaAspInAct));
        asp->psId = 1;
        asp->pspId = 1;
        asp->endPointId = 1;
        //asp->endPointId = selfProcId;
        asp->m3uaLsapId = 0;
 
        retVal = cliAspInActive(&(req->u.m3uaAspInact), &lpStackResp, lprocIdList);
        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *) "
                 "+CONF+ cliAspInActive FAILED");
          retVal = -1;
        }
      }
      break;
    case STA_LINK:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType STA_LINK");

        LinkStatus *lnk = &(req->u.lnkstatus);
        lnk->lnkId = 1;
        lnk->layer = 1;
        lnk->mtp2UsapId = MTP2_LINK_USAPID;
        lnk->mtp3LsapId = MTP3_LINK_LSAPID;

        retVal = cliStatusLink(&(req->u.lnkstatus),&lpStackResp,lprocIdList);
      }
      break;
    case STA_LINKSET:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType STA_LINKSET");

        LinkSetStatus *lnk = &(req->u.lnkStatus);
        lnk->lnkSet = 1;
        lnk->cmbLnkSetId = 2;

        retVal = cliStatusLnkSet(&(req->u.lnkStatus),&lpStackResp, lprocIdList);
      }
      break;
    case STA_ROUTE:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType STA_ROUTE");

        RouteStatus *rte = &(req->u.dpcStatus);
        rte->dpc = 120;
        rte->nwkId = 1;

        retVal = cliStatusRte(&(req->u.dpcStatus),&lpStackResp,lprocIdList);
      }
      break;
    case STA_PS:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType STA_PS");

        retVal = cliStatusPs(&(req->u.ps),&lpStackResp, lprocIdList);
      }
      break;
    case STA_PSP:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType STA_PSP");

        PspStatus *psp = &(req->u.psp);
        psp->pspId = 1;

        retVal = cliStatusPsp(&(req->u.psp),&lpStackResp,lprocIdList);
      }
      break;
    case STA_NODE:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType STA_NODE");

        NodeStatus *node = &(req->u.nodeStatus);
        cmMemset((U8 *)node, 0,sizeof(NodeStatus));
        node->entId = ENTHI;
        node->instId = 0;
        //node->procId = mpSmDistributor->getTcapProvider()->selfProcId;
        node->procId = selfProcId;

        retVal = cliStatusNode(&(req->u.nodeStatus),lprocIdList);
      }
      break;

    case ENABLE_NODE:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType ENABLE_NODE");
        retVal = cliEnableNode(&(req->u.sgNode) , &lpStackResp, lprocIdList);

        if((-1 == retVal) || (lpStackResp && (0 != lpStackResp->status))) {
          logger.logMsg(ERROR_FLAG, 0, "processEmsReq(Ss7SigtranSubsReq *) "
                 "+CONF+ cliEnableNode FAILED");
          retVal = -1;
        }
      }
      break;
    case DISABLE_NODE:
      {
        logger.logMsg (TRACE_FLAG, 0,
					"processEmsReq(Ss7SigtranSubsReq *)::recieved cmdType DISABLE_NODE");
        retVal = cliDisableNode(&(req->u.sgNode), lprocIdList);
      }
      break;

  }

  if(NULL != lpStackResp) {
    delete lpStackResp;
    lpStackResp = NULL; 
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving processEmsReq(Ss7SigtranSubsReq *)");
  return retVal;
}



/******************************************************************************
*
*     Fun:   cliAddNetwork()
*
*     Desc:  Add network
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliAddNetwork(AddNetwork *nwk, Ss7SigtranStackResp **resp,
                                 vector<int> &procIdList) 
{
logger.logMsg (TRACE_FLAG, 0,
   "Entering INGwSmWrapper::cliAddNetwork() <NetworkId, Variant><%d, %d>", 
	 nwk->nwId, nwk->variant);

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  //to enable purifying in FT scenarios
  char* lpcAddNwSleep = getenv("INGW_ADD_NW_SLEEP");
  if(NULL != lpcAddNwSleep) {
    int liSleepTime = atoi(lpcAddNwSleep);
    logger.logINGwMsg(false,ERROR_FLAG,0,"INGwSmWrapper::cliAddNetwork"
    " sleeping for %d seconds",liSleepTime);
    sleep(liSleepTime);
  }

  int liResult 	  = 0;
  int liRequestId = getContextId();
  U8 numProc      = procIdList.size();
  int nodeType 		= mpSmDistributor->getSmRepository()->getTransportType();
	int timeOut     = 3*numProc+3;

  for(int i=0;i<numProc;i++) 
	{
    if ((nwk->protoType == TRANSPORT_TYPE_SIGTRAN)||
                  (nwk->protoType == TRANSPORT_TYPE_BOTH)) 		
    {
      logger.logMsg(TRACE_FLAG, 0, 
       "cliAddNetwork()::ADD_NETWORK M3UA Layer ProcId[%d] TransPortType[%d]", 
             procIdList[i], nodeType);

      /*
       * create a new Queue object and used the request Id as
       * the thread id for the current thread. This will be used by Stack
       * Manager to unblock it if needed.
       */
      if (i == 0) {
        mpSmDistributor->ss7SigtranStackRespPending = 1;
      }
      else {
        mpSmDistributor->ss7SigtranStackRespPending++;
      }
      INGwSmQueueMsg *addNwkM3uaQMsg = new INGwSmQueueMsg;

      addNwkM3uaQMsg->mSrc = BP_AIN_SM_SRC_EMS;

      addNwkM3uaQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
      cmMemcpy((U8*)&(addNwkM3uaQMsg->t.stackData.req.u.addNwk),
							 (U8*)nwk,sizeof(AddNetwork));
      addNwkM3uaQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
      addNwkM3uaQMsg->t.stackData.cmdType = ADD_NETWORK;
      addNwkM3uaQMsg->t.stackData.procId = procIdList[i];
      addNwkM3uaQMsg->t.stackData.miRequestId= liRequestId;

      //post the message to the Distributor
      if (mpSmDistributor->postMsg (addNwkM3uaQMsg) != BP_AIN_SM_OK)
      {
        logger.logMsg (ERROR_FLAG, 0,
            "In configure, postMsg failed");
        delete addNwkM3uaQMsg;
        return -1;
      }
    }

    logger.logMsg(TRACE_FLAG, 0, 
           "cliAddNetwork():: ADD_NETWORK SCCP Layer ProcId[%d] TransPortType[%d]", 
           procIdList[i], nodeType);

    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
	  if ((nwk->protoType == TRANSPORT_TYPE_SIGTRAN) ||
        (nwk->protoType == TRANSPORT_TYPE_BOTH)) {
       mpSmDistributor->ss7SigtranStackRespPending++;
    }
    else {
      if (i == 0) {
        mpSmDistributor->ss7SigtranStackRespPending = 1;
      }
      else {
        mpSmDistributor->ss7SigtranStackRespPending++;
      }
    }
    
    INGwSmQueueMsg *addNwkSccpQMsg = new INGwSmQueueMsg;

    addNwkSccpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

    addNwkSccpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    cmMemcpy((U8*)&(addNwkSccpQMsg->t.stackData.req.u.addNwk),
						(U8*)nwk,sizeof(AddNetwork));
    addNwkSccpQMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
    addNwkSccpQMsg->t.stackData.cmdType = ADD_NETWORK;
    addNwkSccpQMsg->t.stackData.procId = procIdList[i];
    addNwkSccpQMsg->t.stackData.miRequestId= liRequestId;

    //post the message to the Distributor
    if (mpSmDistributor->postMsg (addNwkSccpQMsg) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0,
          "In configure, postMsg failed");
      delete addNwkSccpQMsg;
      return -1;
    }
  } // End for loop

  INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

  //block the current thread
  //if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else 
	{
		logger.logMsg(ERROR_FLAG, 0, 
											"INGwSmWrapper::cliAddNetwork() Failed due to Timedout");
		liResult = -1;
	}

	if(lpContext != NULL)
	{
		(**resp).procId     = lpContext->resp.procId;
		(**resp).status     = lpContext->resp.status;
		(**resp).reason     = lpContext->resp.reason;
		(**resp).reasonStr  = lpContext->resp.reasonStr;
		(**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
	}
	else 
	{
		(**resp).procId     = SFndProcId();
		(**resp).status     = FAILURE_RESPONSE;
		(**resp).reason     = FAILURE_REASON;
		(**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
		(**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::cliAddNetwork(), result[%d]", liResult);

  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAddLinkSet()
*
*     Desc:  Add LInkSet
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliAddLinkSet(AddLinkSet *apParm, Ss7SigtranStackResp **resp,
                                 vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0,
	"Entering INGwSmWrapper::cliAddLinkSet() <lnkSet ID<%d>", apParm->lnkSetId);
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size();;
  int liRequestId = getContextId();

  for(int i=0;i<numProc;i++) {
    logger.logMsg(TRACE_FLAG, 0, 
	  "cliAddLinkSet()::ADD_LINKSET MTP3 Layer ProcId[%d] ", procIdList[i]);

    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
     INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
     if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     }
     else {
       mpSmDistributor->ss7SigtranStackRespPending++;
     }
     cmMemset ((U8*)qMsg, 0, sizeof (INGwSmQueueMsg));

     qMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(qMsg->t.stackData.req.u.lnkSet),
						 (U8*)apParm,sizeof(AddLinkSet));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
     qMsg->t.stackData.cmdType = ADD_LINKSET;
     qMsg->t.stackData.procId = procIdList[i];
     qMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0,
         "In configure, postMsg failed");
       delete qMsg;
       return -1;
     }
  } // For loop end

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		liResult = -1;
		logger.logMsg(ERROR_FLAG, 0,
					"INGwSmWrapper::cliAddLinkSet() Failed due to Timedout");
	}

 	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }


  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAddLinkSet()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliModLinkSet()
*
*     Desc:  Modify LinkSet
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliModLinkSet(AddLinkSet *apParm, Ss7SigtranStackResp **resp,
                                 vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0,
  "Entering INGwSmWrapper::cliModLinkSet() <lnkSet ID<%d>", apParm->lnkSetId);
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  int liResult 	  = 0;
  U8 numProc 	    = procIdList.size();
  int liRequestId = getContextId();

  for(int i=0;i<numProc;i++) {
    logger.logMsg(TRACE_FLAG, 0, 
    "cliModLinkSet()::MODIFY_LINKSET MTP3 Layer ProcId[%d] ", procIdList[i]);

    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
     INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
     if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     }
     else {
       mpSmDistributor->ss7SigtranStackRespPending++;
     }
     cmMemset ((U8*)qMsg, 0, sizeof (INGwSmQueueMsg));


     qMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(qMsg->t.stackData.req.u.lnkSet),
						 (U8*)apParm,sizeof(AddLinkSet));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
     qMsg->t.stackData.cmdType = MODIFY_LINKSET;
     qMsg->t.stackData.procId = procIdList[i];
     qMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0,
         "In configure, postMsg failed");
       delete qMsg;
       return -1;
     }
  }

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		liResult = -1;
		logger.logMsg(ERROR_FLAG, 0,
			"INGwSmWrapper::cliModLinkSet() Failed due to Timedout");
	}

 	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliModLinkSet()");

  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAddRoute()
*
*     Desc:  Add Route
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliAddRoute(AddRoute *rte, 
										Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::cliAddRoute() <swtch> <%d>", rte->swtch);

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  int liResult 		= 0;
  S16 ssnSwitch = 0;
  U8 numProc 			=  procIdList.size();
  int liRequestId = getContextId();

  for(int i=0;i<numProc;i++) {

    logger.logMsg(TRACE_FLAG, 0, 
	 "cliAddRoute()::ADD_ROUTE MTP3 Layer ProcId[%d] ", procIdList[i]);
    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
    if(rte->swtchType != 255)
    {
     INGwSmQueueMsg *addRteMtp3QMsg = new INGwSmQueueMsg;
     if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     }
     else {
       mpSmDistributor->ss7SigtranStackRespPending++;
     }

     // Need to investigate on requestId.
     addRteMtp3QMsg->mSrc = BP_AIN_SM_SRC_EMS;

     addRteMtp3QMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(addRteMtp3QMsg->t.stackData.req.u.addRoute),
						 (U8*)rte,sizeof(AddRoute));
     addRteMtp3QMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
     addRteMtp3QMsg->t.stackData.cmdType = ADD_ROUTE;
     addRteMtp3QMsg->t.stackData.procId = procIdList[i];
		 addRteMtp3QMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (addRteMtp3QMsg) != BP_AIN_SM_OK)
     {
     	  logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete addRteMtp3QMsg;
       return -1;
     }
    }

    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
    if(rte->swtch != -1)
    {
      logger.logMsg(TRACE_FLAG, 0, 
		  "cliAddRoute()::ADD_ROUTE SCCP Layer ProcId[%d] ", procIdList[i]);

      INGwSmQueueMsg *addRteSccpQMsg = new INGwSmQueueMsg;
     	if ((rte->swtchType == 255) && (i == 0)) {
        mpSmDistributor->ss7SigtranStackRespPending = 1;
     	}
     	else {
        mpSmDistributor->ss7SigtranStackRespPending++;
     	}

      addRteSccpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

      addRteSccpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
      cmMemcpy((U8*)&(addRteSccpQMsg->t.stackData.req.u.addRoute),
						  (U8*)rte,sizeof(AddRoute));

  if((rte->swtch == SW_ITU) || (rte->swtch == SW_ANSI))
  {
    getSwtchFromLocalSSN(&ssnSwitch);
    if(rte->swtch == SW_ITU)
    {
      switch(ssnSwitch)
      {
        case LST_SW_ITU88:
          logger.logMsg(TRACE_FLAG, 0, 
		      "swtch changed in addroute to ITU88");
	        addRteSccpQMsg->t.stackData.req.u.addRoute.swtch	= LSP_SW_ITU88;
          break;
        case LST_SW_ITU92:
          logger.logMsg(TRACE_FLAG, 0, 
		      "swtch changed in addroute to ITU92");
	        addRteSccpQMsg->t.stackData.req.u.addRoute.swtch	= LSP_SW_ITU92;
          break;
        case LST_SW_ITU96:
          logger.logMsg(TRACE_FLAG, 0, 
		      "swtch changed in addroute to ITU96");
	        addRteSccpQMsg->t.stackData.req.u.addRoute.swtch	= LSP_SW_ITU96;
          break;
      }
    }
    else if(rte->swtch == SW_ANSI)
    {
      switch(ssnSwitch)
      {
        case LST_SW_ANS88:
          logger.logMsg(TRACE_FLAG, 0, 
		      "swtch changed in addroute to ANS88");
	        addRteSccpQMsg->t.stackData.req.u.addRoute.swtch	= LSP_SW_ANS88;
          break;
        case LST_SW_ANS92:
          logger.logMsg(TRACE_FLAG, 0, 
		      "swtch changed in addroute to ANS92");
	        addRteSccpQMsg->t.stackData.req.u.addRoute.swtch	= LSP_SW_ANS92;
          break;
        case LST_SW_ANS96:
          logger.logMsg(TRACE_FLAG, 0, 
		      "swtch changed in addroute to ANS96");
	        addRteSccpQMsg->t.stackData.req.u.addRoute.swtch	= LSP_SW_ANS96;
          break;
      }
    }
  }

      addRteSccpQMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
      addRteSccpQMsg->t.stackData.cmdType = ADD_ROUTE;
      addRteSccpQMsg->t.stackData.procId = procIdList[i];
      addRteSccpQMsg->t.stackData.miRequestId = liRequestId;

        //post the message to the Distributor
      if (mpSmDistributor->postMsg (addRteSccpQMsg) != BP_AIN_SM_OK)
      {
        logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
        delete addRteSccpQMsg;
        return -1;
      }
    }
  } // For loop End

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAddRoute()");

  return liResult;
}


/******************************************************************************
*
*     Fun:   cliBndSctp()
*
*     Desc:  Bind M3UA
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliBndSctp(BindSap *bnd, Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliBndSctp()");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  int liResult 		= 0;
  U8 numProc 		  =  procIdList.size();
  int liRequestId = getContextId();
	int timeOut     = 3*numProc+3;

   /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *bndSct = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending++;
     cmMemcpy((U8*)&(bndSct->t.stackData.req.u.bindSap),
		 			 (U8*)bnd,sizeof(BindSap));

     bndSct->mSrc = BP_AIN_SM_SRC_EMS;
     bndSct->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     bndSct->t.stackData.stackLayer = BP_AIN_SM_SCT_LAYER;
     bndSct->t.stackData.cmdType = BND_SCTP;
     bndSct->t.stackData.procId = SFndProcId();
     bndSct->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (bndSct) != BP_AIN_SM_OK)
     {
      logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete bndSct;
       return -1;
     }


	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
  {
    liResult = -1;
    logger.logMsg(ERROR_FLAG, 0,
							"INGwSmWrapper::cliBndSctp() Failed due to Timedout");
  }

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }
  

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliBndSctp()");
  return liResult;
}


/******************************************************************************
*
*     Fun:   cliBndM3ua()
*
*     Desc:  Bind M3UA
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliBndM3ua(BindSap *bnd, Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliBndM3ua()");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  int liResult 		= 0;
  U8 numProc 		  =  procIdList.size();
  int liRequestId = getContextId();
	int timeOut     = 3*numProc+3;

  //for(int i=0;i<numProc;i++) 
	//{
#if 0
		logger.logMsg(TRACE_FLAG, 0,"cliBndM3ua()::BND_M3UA SCCP Layer");
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */

     INGwSmQueueMsg *bndSccpToM3ua = new INGwSmQueueMsg;
      mpSmDistributor->ss7SigtranStackRespPending = 1;
     	cmMemcpy((U8*)&(bndSccpToM3ua->t.stackData.req.u.bindSap),
						 (U8*)bnd,sizeof(BindSap));

     // Need to investigate on requestId.
     bndSccpToM3ua->mSrc = BP_AIN_SM_SRC_EMS;

     bndSccpToM3ua->t.stackData.req.u.bindSap.sapId = bnd->sapId;

     bndSccpToM3ua->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     bndSccpToM3ua->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     bndSccpToM3ua->t.stackData.cmdType = BND_M3UA;
     //bndSccpToM3ua->t.stackData.procId = procIdList[i];
     bndSccpToM3ua->t.stackData.procId = SFndProcId();
    bndSccpToM3ua->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (bndSccpToM3ua) != BP_AIN_SM_OK)
     {
      logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete bndSccpToM3ua;
       return -1;
     }
#endif

		logger.logMsg(TRACE_FLAG, 0,"cliBndM3ua()::BND_M3UA M3UA Layer");
   /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *bndm3uaToSct = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending++;
     cmMemcpy((U8*)&(bndm3uaToSct->t.stackData.req.u.bindSap),
		 			 (U8*)bnd,sizeof(BindSap));
     bndm3uaToSct->t.stackData.req.u.bindSap.sapId = bnd->sapId;

     bndm3uaToSct->mSrc = BP_AIN_SM_SRC_EMS;
     bndm3uaToSct->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     bndm3uaToSct->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     bndm3uaToSct->t.stackData.subOpr = BP_AIN_SM_IT_SCTSAP;
     bndm3uaToSct->t.stackData.cmdType = BND_M3UA;
     //bndm3uaToSct->t.stackData.procId = procIdList[i];
     bndm3uaToSct->t.stackData.procId = SFndProcId();
    bndm3uaToSct->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (bndm3uaToSct) != BP_AIN_SM_OK)
     {
      logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete bndm3uaToSct;
       return -1;
     }

#if 0
		logger.logMsg(TRACE_FLAG, 0,"cliBndM3ua()::BND_M3UA SCTP Layer");
    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
     INGwSmQueueMsg *bndSctpToTucl = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending++;
     cmMemcpy((U8*)&(bndSctpToTucl->t.stackData.req.u.bindSap),
		 			 (U8*)bnd,sizeof(BindSap));
     bndSctpToTucl->t.stackData.req.u.bindSap.sapId = bnd->sapId;

     bndSctpToTucl->mSrc = BP_AIN_SM_SRC_EMS;

     bndSctpToTucl->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     bndSctpToTucl->t.stackData.stackLayer = BP_AIN_SM_SCT_LAYER;
     bndSctpToTucl->t.stackData.cmdType = BND_M3UA;
     //bndSctpToTucl->t.stackData.procId = procIdList[i];
     bndSctpToTucl->t.stackData.procId = SFndProcId();
     bndSctpToTucl->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (bndSctpToTucl) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete bndSctpToTucl;
       return -1;
     }
#endif

  //} // End for loop


	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
  {
    liResult = -1;
    logger.logMsg(ERROR_FLAG, 0,
							"INGwSmWrapper::cliBndM3ua() Failed due to Timedout");
  }

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }
  

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliBndM3ua()");
  return liResult;
}


/******************************************************************************
*
*     Fun:   cliOpenEp()
*
*     Desc:  Open END POINT
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliOpenEp(OpenEp * openEp, Ss7SigtranStackResp **resp)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliOpenEp()");


  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();

  logger.logMsg(TRACE_FLAG, 0, 
         "cliOpenEp():: OPEN_ENDPOINT M3UA Layer ProcId[%d]", selfProcId);
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
   INGwSmQueueMsg *openEpM3ua = new INGwSmQueueMsg;
   mpSmDistributor->ss7SigtranStackRespPending = 1;

   openEpM3ua->mSrc = BP_AIN_SM_SRC_EMS;

   openEpM3ua->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
   cmMemcpy((U8*)&(openEpM3ua->t.stackData.req.u.openEp),
					 (U8*)openEp, sizeof(OpenEp));
   openEpM3ua->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
   openEpM3ua->t.stackData.cmdType = OPEN_ENDPOINT;
   openEpM3ua->t.stackData.procId = selfProcId;
   openEpM3ua->t.stackData.miRequestId = liRequestId;

   //post the message to the Distributor
   if (mpSmDistributor->postMsg (openEpM3ua) != BP_AIN_SM_OK)
   {
     logger.logMsg (ERROR_FLAG, 0, "In cliOpenEp, postMsg failed");
     delete openEpM3ua;
     return -1;
   }

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

  //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
  {
    liResult = -1;
    logger.logMsg(ERROR_FLAG, 0,
							"INGwSmWrapper::cliAddNetwork() Failed due to Timedout");
  }

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliOpenEp()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAddEndPoint()
*
*     Desc:  Add End Point
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliAddEndPoint(AddEndPoint *ep, Ss7SigtranStackResp **resp, 
																  vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAddEndPoint()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  INGwSmQueueMsg *addEpM3uaQMsg;
  INGwSmQueueMsg *addEpSctpQMsg;

  int liResult 		= 0;
  U8 numProc  		= procIdList.size();
  int liRequestId = getContextId();
	int timeOut     = 50;

  for(int i=0;i<numProc;i++) 
	{
    logger.logMsg(TRACE_FLAG, 0, 
           "cliAddEndPoint():: ADD_ENDPOINT M3UA Layer ProcId[%d]", procIdList[i]);
    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
    if (i == 0) {
      mpSmDistributor->ss7SigtranStackRespPending = 1;
    }
    else {
      mpSmDistributor->ss7SigtranStackRespPending++;
    }
    addEpM3uaQMsg = new INGwSmQueueMsg;

    addEpM3uaQMsg->mSrc = BP_AIN_SM_SRC_EMS;

    addEpM3uaQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    cmMemcpy((U8*)&(addEpM3uaQMsg->t.stackData.req.u.addEp),
						(U8*)ep,sizeof(AddEndPoint));
    addEpM3uaQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
    addEpM3uaQMsg->t.stackData.cmdType = ADD_ENDPOINT;
    addEpM3uaQMsg->t.stackData.procId = procIdList[i];
    addEpM3uaQMsg->t.stackData.miRequestId= liRequestId;

    //post the message to the Distributor
    if (mpSmDistributor->postMsg (addEpM3uaQMsg) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
      delete addEpM3uaQMsg;
      return -1;
    }
  } // End for loop

  logger.logMsg(TRACE_FLAG, 0, 
         "cliAddEndPoint():: ADD_ENDPOINT SCTP Layer SCTSAP ProcId[%d]",
         ep->sctpProcId);

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  addEpSctpQMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending++;

  addEpSctpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

  addEpSctpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(addEpSctpQMsg->t.stackData.req.u.addEp),
				  (U8*)ep,sizeof(AddEndPoint));
  addEpSctpQMsg->t.stackData.stackLayer = BP_AIN_SM_SCT_LAYER;
  addEpSctpQMsg->t.stackData.subOpr = BP_AIN_SM_SCT_SCTSAP;
  addEpSctpQMsg->t.stackData.cmdType = ADD_ENDPOINT;
  addEpSctpQMsg->t.stackData.procId = ep->sctpProcId;
  addEpSctpQMsg->t.stackData.miRequestId= liRequestId;

    //post the message to the Distributor
  if (mpSmDistributor->postMsg (addEpSctpQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0, "cliAddEndPoint, postMsg failed");
    delete addEpSctpQMsg;
    return -1;
  }

  logger.logMsg(TRACE_FLAG, 0, 
 	"cliAddEndPoint():: ADD_ENDPOINT SCTP Layer TSAP ProcId[%d]", ep->sctpProcId);

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */

  addEpSctpQMsg = new INGwSmQueueMsg;
  mpSmDistributor->ss7SigtranStackRespPending++;

  addEpSctpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

  addEpSctpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(addEpSctpQMsg->t.stackData.req.u.addEp),
				  (U8*)ep,sizeof(AddEndPoint));
  addEpSctpQMsg->t.stackData.stackLayer = BP_AIN_SM_SCT_LAYER;
  addEpSctpQMsg->t.stackData.subOpr = BP_AIN_SM_SCT_TSAP;
  addEpSctpQMsg->t.stackData.cmdType = ADD_ENDPOINT;
  addEpSctpQMsg->t.stackData.procId = ep->sctpProcId;
  addEpSctpQMsg->t.stackData.miRequestId= liRequestId;

  //post the message to the Distributor
  if (mpSmDistributor->postMsg (addEpSctpQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0, "cliAddEndPoint, postMsg failed");
    delete addEpSctpQMsg;
    return -1;
  }

  logger.logMsg(TRACE_FLAG, 0, 
         "cliAddEndPoint():: ADD_ENDPOINT TUCL Layer ProcId[%d]",
         ep->sctpProcId);
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  addEpSctpQMsg = new INGwSmQueueMsg;
  mpSmDistributor->ss7SigtranStackRespPending++;

  addEpSctpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

  addEpSctpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(addEpSctpQMsg->t.stackData.req.u.addEp),
					(U8*)ep,sizeof(AddEndPoint));
  addEpSctpQMsg->t.stackData.stackLayer = BP_AIN_SM_TUC_LAYER;
  addEpSctpQMsg->t.stackData.cmdType = ADD_ENDPOINT;
  addEpSctpQMsg->t.stackData.procId = ep->sctpProcId;
  addEpSctpQMsg->t.stackData.miRequestId= liRequestId;

  //post the message to the Distributor
  if (mpSmDistributor->postMsg (addEpSctpQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0, "cliAddEndPoint , postMsg failed");
    delete addEpSctpQMsg;
    return -1;
  }

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
						"INGwSmWrapper::cliAddEndPoint() Failed due to Timedout");
		liResult = -1;
	}

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }


  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAddEndPoint()");

  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAddLocalSsn()
*
*     Desc:  Add Route
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliAddLocalSsn(AddLocalSsn *l_ssn, 
								Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAddLocalSsn()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size();
  int liRequestId = getContextId();
	int timeOut     = numProc*3+3; // Number of procId X Number of Post+grace time

  for(int i=0;i<numProc;i++) 
	{
    logger.logMsg(TRACE_FLAG, 0, 
    "cliAddLocalSsn():: ADD_LOCAL_SSN SCCP Layer ProcId[%d]", procIdList[i]);
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *addLocalSsnSccpQMsg = new INGwSmQueueMsg;
     if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     }
     else {
       mpSmDistributor->ss7SigtranStackRespPending++;
     }


     addLocalSsnSccpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     addLocalSsnSccpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
		cmMemcpy((U8*)&(addLocalSsnSccpQMsg->t.stackData.req.u.addLocalSsn),
						(U8*)l_ssn,sizeof(AddLocalSsn));
     addLocalSsnSccpQMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     addLocalSsnSccpQMsg->t.stackData.cmdType = ADD_LOCAL_SSN;
     addLocalSsnSccpQMsg->t.stackData.procId = procIdList[i];
		addLocalSsnSccpQMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (addLocalSsnSccpQMsg) != BP_AIN_SM_OK)
     {
			logger.logMsg (ERROR_FLAG, 0, "cliAddLocalSsn postMsg failed");
       delete addLocalSsnSccpQMsg;
       return -1;
     }

    logger.logMsg(TRACE_FLAG, 0, 
           "cliAddLocalSsn():: ADD_LOCAL_SSN TCAP Layer USAP ProcId[%d]",
           procIdList[i]);
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  INGwSmQueueMsg *addLocalSsnUsapTcapQMsg = new INGwSmQueueMsg;
  mpSmDistributor->ss7SigtranStackRespPending++;

  addLocalSsnUsapTcapQMsg->mSrc = BP_AIN_SM_SRC_EMS;

  addLocalSsnUsapTcapQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  	cmMemcpy((U8*)&(addLocalSsnUsapTcapQMsg->t.stackData.req.u.addLocalSsn),
						(U8*)l_ssn,sizeof(AddLocalSsn));
  addLocalSsnUsapTcapQMsg->t.stackData.stackLayer = BP_AIN_SM_TCA_LAYER;
  addLocalSsnUsapTcapQMsg->t.stackData.subOpr = BP_AIN_SM_TCA_USAP;
  addLocalSsnUsapTcapQMsg->t.stackData.cmdType = ADD_LOCAL_SSN;
  addLocalSsnUsapTcapQMsg->t.stackData.procId = procIdList[i];
  addLocalSsnUsapTcapQMsg->t.stackData.miRequestId = liRequestId;

    //post the message to the Distributor
  if (mpSmDistributor->postMsg (addLocalSsnUsapTcapQMsg) != BP_AIN_SM_OK)
  {
			logger.logMsg (ERROR_FLAG, 0, "cliAddLocalSsn, postMsg failed");
    delete addLocalSsnUsapTcapQMsg;
    return -1;
  }

  	logger.logMsg(TRACE_FLAG, 0, 
 		"cliAddLocalSsn()::ADD_LOCAL_SSN TCAP Layer LSAP ProcId[%d]",procIdList[i]);

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  INGwSmQueueMsg *addLocalSsnLsapTcapQMsg = new INGwSmQueueMsg;
  mpSmDistributor->ss7SigtranStackRespPending++;

  addLocalSsnLsapTcapQMsg->mSrc = BP_AIN_SM_SRC_EMS;

  addLocalSsnLsapTcapQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  	cmMemcpy((U8*)&(addLocalSsnLsapTcapQMsg->t.stackData.req.u.addLocalSsn),
						(U8*)l_ssn,sizeof(AddLocalSsn));
  addLocalSsnLsapTcapQMsg->t.stackData.stackLayer = BP_AIN_SM_TCA_LAYER;
  addLocalSsnLsapTcapQMsg->t.stackData.subOpr = BP_AIN_SM_TCA_LSAP;
  addLocalSsnLsapTcapQMsg->t.stackData.cmdType = ADD_LOCAL_SSN;
  addLocalSsnLsapTcapQMsg->t.stackData.procId = procIdList[i];
  addLocalSsnLsapTcapQMsg->t.stackData.miRequestId = liRequestId;

    //post the message to the Distributor
  if (mpSmDistributor->postMsg (addLocalSsnLsapTcapQMsg) != BP_AIN_SM_OK)
  {
			logger.logMsg (ERROR_FLAG, 0, "cliAddLocalSsn, postMsg failed");
    delete addLocalSsnLsapTcapQMsg;
    return -1;
  }
  } // For loop end

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 //block the current thread
  if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else 
	{
		logger.logMsg(ERROR_FLAG, 0,
                "INGwSmWrapper::cliAddLocalSsn() Failed due to Timedout");
    liResult = -1;
	}

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::cliAddLocalSsn()");

  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAddRemoteSsn()
*
*     Desc:  Add Route
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliAddRemoteSsn(AddRemoteSsn *r_ssn, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAddRemoteSsn()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();
  U8 numProc      = procIdList.size();

  for(int i=0;i<numProc;i++) 
	{
    logger.logMsg(TRACE_FLAG, 0, 
	  "cliAddRemoteSsn():: ADD_REMOTE_SSN SCCP Layer ProcId[%d]", procIdList[i]);
    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
     INGwSmQueueMsg *addRemoteSsnSccpQMsg = new INGwSmQueueMsg;
     if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     }
     else {
       mpSmDistributor->ss7SigtranStackRespPending++;
     }

     addRemoteSsnSccpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     addRemoteSsnSccpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(addRemoteSsnSccpQMsg->t.stackData.req.u.addRemoteSsn),
              (U8*)r_ssn,sizeof(AddRemoteSsn));
     addRemoteSsnSccpQMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     addRemoteSsnSccpQMsg->t.stackData.cmdType = ADD_REMOTE_SSN;
     addRemoteSsnSccpQMsg->t.stackData.procId = procIdList[i];
     addRemoteSsnSccpQMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (addRemoteSsnSccpQMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0,
         "In configure, postMsg failed");
       delete addRemoteSsnSccpQMsg;
       return -1;
     }
  } // For loop end

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAddRemoteSsn()");

  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAddAs()
*
*     Desc:  Add AS
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliAddAs(AddPs *as, Ss7SigtranStackResp **resp, 
																					vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAddAs()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();
  U8 numProc      = procIdList.size();

  for(int i=0;i<numProc;i++) 
	{
    logger.logMsg(TRACE_FLAG, 0, "cliAddAs():: ADD_AS M3UA Layer PS ProcId[%d] and numProc[%d]",
                   procIdList[i],numProc);
  	/*
   	* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
     if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     }
     else {
       mpSmDistributor->ss7SigtranStackRespPending++;
     }
     INGwSmQueueMsg *addAsM3uaQMsg = new INGwSmQueueMsg;

     addAsM3uaQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     addAsM3uaQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(addAsM3uaQMsg->t.stackData.req.u.addPs),
						 (U8*)as,sizeof(AddPs));
     addAsM3uaQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     addAsM3uaQMsg->t.stackData.subOpr = BP_AIN_SM_M3U_PS;
     addAsM3uaQMsg->t.stackData.cmdType = ADD_AS;
     addAsM3uaQMsg->t.stackData.procId = procIdList[i];
     addAsM3uaQMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (addAsM3uaQMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliAddAs, postMsg failed");
       delete addAsM3uaQMsg;
       return -1;
     }

    logger.logMsg(TRACE_FLAG, 0, 
		"cliAddAs():: ADD_AS M3UA Layer ROUTE ProcId[%d]", procIdList[i]);

    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
     mpSmDistributor->ss7SigtranStackRespPending++;
     INGwSmQueueMsg *addAsM3uaRteQMsg = new INGwSmQueueMsg;

     addAsM3uaRteQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     addAsM3uaRteQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(addAsM3uaRteQMsg->t.stackData.req.u.addPs),
						 (U8*)as,sizeof(AddPs));
     addAsM3uaRteQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     addAsM3uaRteQMsg->t.stackData.subOpr = BP_AIN_SM_M3U_ROUTE;
     addAsM3uaRteQMsg->t.stackData.cmdType = ADD_AS;
     addAsM3uaRteQMsg->t.stackData.procId = procIdList[i];
     addAsM3uaRteQMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (addAsM3uaRteQMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliAddAs, postMsg failed");
       delete addAsM3uaRteQMsg;
       return -1;
     }
  } // For loop end

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
							"INGwSmWrapper::cliAddAs() Failed due to Timedout");
    liResult = -1;
	}

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAddAs()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliModAs()
*
*     Desc:  Modify AS
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliModAs(AddPs *as, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliModAs()");

  if (!mpSmDistributor)
  {
		logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();
  U8 numProc      = procIdList.size();

  for(int i=0;i<numProc;i++) 
	{
    logger.logMsg(TRACE_FLAG, 0, 
    "cliModAs():: MODIFY_AS M3UA Layer PS ProcId[%d]", procIdList[i]);

    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
     if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     }
     else {
       mpSmDistributor->ss7SigtranStackRespPending++;
     }
     INGwSmQueueMsg *addAsM3uaQMsg = new INGwSmQueueMsg;

     addAsM3uaQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     addAsM3uaQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(addAsM3uaQMsg->t.stackData.req.u.addPs),
						 (U8*)as,sizeof(AddPs));
     addAsM3uaQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     addAsM3uaQMsg->t.stackData.subOpr = BP_AIN_SM_M3U_PS;
     addAsM3uaQMsg->t.stackData.cmdType = MODIFY_AS;
     addAsM3uaQMsg->t.stackData.procId = procIdList[i];
     addAsM3uaQMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (addAsM3uaQMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliModAs, postMsg failed");
       delete addAsM3uaQMsg;
       return -1;
     }
  } // End for loop

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliModAs()");

  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAddAsp()
*
*     Desc:  Add ASP
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliAddAsp(AddPsp *asp, Ss7SigtranStackResp **resp, 
														 vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAddAsp()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();
  U8 numProc      = procIdList.size();

  for(int i=0;i<numProc;i++) 
	{
		/*
		* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
     if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     }
     else {
       mpSmDistributor->ss7SigtranStackRespPending++;
     }

     INGwSmQueueMsg *addAspM3uaQMsg = new INGwSmQueueMsg;

     addAspM3uaQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     addAspM3uaQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(addAspM3uaQMsg->t.stackData.req.u.addpsp),
						 (U8*)asp,sizeof(AddPsp));
     addAspM3uaQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     addAspM3uaQMsg->t.stackData.cmdType = ADD_ASP;
     addAspM3uaQMsg->t.stackData.procId = procIdList[i];
     addAspM3uaQMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (addAspM3uaQMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliAddAsp , postMsg failed");
       delete addAspM3uaQMsg;
       return -1;
     }
  } // For loop

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

  //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
							"INGwSmWrapper::cliAddAsp() Failed due to Timedout");
    liResult = -1;
	}

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAddAsp()");

  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAddUserPart()
*
*     Desc:  Add network
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliAddUserPart(AddUserPart *apParm, 
									Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAddUserPart()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size();
	int liRequestId = getContextId();
	//int timeOut     = 3*numProc+2;
	int timeOut     = 30;

  for(int i=0;i<numProc;i++) 
	{
  	/*
   	* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
    if (i == 0) {
      mpSmDistributor->ss7SigtranStackRespPending = 1;
    }
    else {
      mpSmDistributor->ss7SigtranStackRespPending++;
    }
    INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

		qMsg->mSrc = BP_AIN_SM_SRC_EMS;

    qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    cmMemcpy((U8*)&(qMsg->t.stackData.req.u.addUserPart),
																				(U8*)apParm,sizeof(AddUserPart));
      
    if (qMsg->t.stackData.req.u.addUserPart.userPartType == MTP3_USER)
    {
       qMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
    }
    else /* M3UA user */
    {
       qMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
    }

    qMsg->t.stackData.cmdType     = ADD_USERPART;
    qMsg->t.stackData.procId      = procIdList[i];
    qMsg->t.stackData.miRequestId = liRequestId;

    //post the message to the Distributor
    if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0, "cliAddUserPart, postMsg failed");
       delete qMsg;
       return -1;
    }

  	/*
   	* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
		mpSmDistributor->ss7SigtranStackRespPending++;
    qMsg = new INGwSmQueueMsg;

     qMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(qMsg->t.stackData.req.u.addUserPart),
																			(U8*)apParm,sizeof(AddUserPart));
     if (qMsg->t.stackData.req.u.addUserPart.userPartType == MTP3_USER)
     {
        qMsg->t.stackData.stackLayer = BP_AIN_SM_LDF_MTP3_LAYER;
     }
     else /* M3UA user */
     {
        qMsg->t.stackData.stackLayer = BP_AIN_SM_LDF_M3UA_LAYER;
     }
     qMsg->t.stackData.cmdType = ADD_USERPART;
     qMsg->t.stackData.procId = procIdList[i];
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliAddUserPart, postMsg failed");
       delete qMsg;
       return -1;
     }

     mpSmDistributor->ss7SigtranStackRespPending++;
     qMsg = new INGwSmQueueMsg;

    qMsg->mSrc = BP_AIN_SM_SRC_EMS;

    qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    cmMemcpy((U8*)&(qMsg->t.stackData.req.u.addUserPart),
						(U8*)apParm,sizeof(AddUserPart));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     qMsg->t.stackData.cmdType = ADD_USERPART;
     qMsg->t.stackData.procId = procIdList[i];
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
      logger.logMsg (ERROR_FLAG, 0, "cliAddUserPart, postMsg failed");
       delete qMsg;
       return -1;
     }
  } // End for loop

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else 
	{
	   logger.logMsg(ERROR_FLAG, 0,
							"INGwSmWrapper::cliAddUserPart() Failed due to Timedout");
    liResult = -1;
  }

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAddUserPart()");
  return liResult;
}


/******************************************************************************
*
*     Fun:   cliAddLink()
*
*     Desc:  Add Link
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliAddLink(AddLink *apParm, Ss7SigtranStackResp **resp,
                              vector<int> &procIdList)
{
  logger.logMsg(TRACE_FLAG, 0,"Entering INGwSmWrapper::cliAddLink() linkID<%d>",
	apParm->lnkId);
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  INGwSmQueueMsg *qMsg = NULL;
  int liResult 	 	     = 0;
  U8 numProc   	 	     = procIdList.size();
  int liRequestId 	 	 = getContextId();
	int timeOut          = 2*numProc+3;

  for(int i=0;i<numProc;i++) 
	{
    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
     qMsg = new INGwSmQueueMsg;
     if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     }
     else {
       mpSmDistributor->ss7SigtranStackRespPending++;
     }

     qMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(qMsg->t.stackData.req.u.lnk),(U8*)apParm,sizeof(AddLink));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
     qMsg->t.stackData.cmdType    = ADD_LINK;
     qMsg->t.stackData.procId     = procIdList[i];
     qMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliAddLink, postMsg failed");
       delete qMsg;
       return -1;
     }

  	/*
   	* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
		qMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending++;

		qMsg->mSrc = BP_AIN_SM_SRC_EMS;

		qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    cmMemcpy((U8*)&(qMsg->t.stackData.req.u.lnk),(U8*)apParm,sizeof(AddLink));
    qMsg->t.stackData.stackLayer = BP_AIN_SM_LDF_MTP3_LAYER;
    qMsg->t.stackData.cmdType 		= ADD_LINK;
    qMsg->t.stackData.procId 			= procIdList[i];
    qMsg->t.stackData.miRequestId = liRequestId;

		//post the message to the Distributor
    if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
    {
			logger.logMsg (ERROR_FLAG, 0, "cliAddLink, postMsg failed");
       delete qMsg;
       return -1;
		}
  } // End for loop

	qMsg = new INGwSmQueueMsg;
	mpSmDistributor->ss7SigtranStackRespPending++;

  qMsg->mSrc = BP_AIN_SM_SRC_EMS;

  qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(qMsg->t.stackData.req.u.lnk),(U8*)apParm,sizeof(AddLink));
  qMsg->t.stackData.stackLayer = BP_AIN_SM_MTP2_LAYER;
  qMsg->t.stackData.cmdType 	 = ADD_LINK;
  qMsg->t.stackData.procId 		 = apParm->mtp2ProcId;
  qMsg->t.stackData.miRequestId= liRequestId;

	//post the message to the Distributor
  if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
  {
		logger.logMsg (ERROR_FLAG, 0, "cliAddLink, postMsg failed");
		delete qMsg;
		return -1;
	}

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 	//block the current thread
	if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
                  "INGwSmWrapper::cliAddLink() Failed due to Timedout");
    liResult = -1;
	}

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAddLink()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliModLink()
*
*     Desc:  Modify Link
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliModLink(AddLink *apParm, Ss7SigtranStackResp **resp, vector<int> &procIdList )
{
  logger.logMsg (TRACE_FLAG, 0,
      "Entering INGwSmWrapper::cliModLink() <link ID<%d>",
       apParm->lnkId);

  int liResult = 0;
  U8 numProc;
  INGwSmQueueMsg *qMsg;
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  int liRequestId = (int) pthread_self ();
  
  numProc =  procIdList.size();
  for(int i=0;i<numProc;i++) { 
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     qMsg = new INGwSmQueueMsg;
     if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     }
     else {
       mpSmDistributor->ss7SigtranStackRespPending++;
     }

     qMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(qMsg->t.stackData.req.u.lnk),(U8*)apParm,sizeof(AddLink));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
     qMsg->t.stackData.cmdType = MODIFY_LINK;
     //qMsg->t.stackData.procId = apParm->mtp2ProcId;
     qMsg->t.stackData.procId = procIdList[i];
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0,
         "In configure, postMsg failed");
       delete qMsg;
       return -1;
     }
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     qMsg = new INGwSmQueueMsg;
      mpSmDistributor->ss7SigtranStackRespPending++;

     qMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(qMsg->t.stackData.req.u.lnk),(U8*)apParm,sizeof(AddLink));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_LDF_MTP3_LAYER;
     qMsg->t.stackData.cmdType = MODIFY_LINK;
     //qMsg->t.stackData.procId = apParm->mtp2ProcId;
     qMsg->t.stackData.procId = procIdList[i];
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0,
         "In configure, postMsg failed");
       delete qMsg;
       return -1;
     }
  } // End for loop

  qMsg = new INGwSmQueueMsg;
  mpSmDistributor->ss7SigtranStackRespPending++;

  // Need to investigate on requestId.
  liRequestId = (int) pthread_self ();

  qMsg->mSrc = BP_AIN_SM_SRC_EMS;

  qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(qMsg->t.stackData.req.u.lnk),(U8*)apParm,sizeof(AddLink));
  qMsg->t.stackData.stackLayer = BP_AIN_SM_MTP2_LAYER;
  qMsg->t.stackData.cmdType = MODIFY_LINK;
  qMsg->t.stackData.procId = apParm->mtp2ProcId;
  qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0,
         "In configure, postMsg failed");
       delete qMsg;
       return -1;
     }
 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

			 *resp = new Ss7SigtranStackResp;
			 (**resp).procId     = lpContext->resp.procId;
			 (**resp).status     = lpContext->resp.status;
			 (**resp).reason     = lpContext->resp.reason;
			 (**resp).reasonStr  = lpContext->resp.reasonStr;
			 (**resp).stackLayer = lpContext->resp.stackLayer;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::cliModLink()");

  return liResult;
}



/******************************************************************************
*
*     Fun:   cliEnaDebug()
*
*     Desc:  Enable Debug 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliEnaDebug(DebugEnableDisable *enaDbg, 
																								vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliEnaDebug()");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size(); 
  int liRequestId = getContextId();

  //for(int i=0;i<numProc;i++) 
	{
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *enbDbgQMsg = new INGwSmQueueMsg;
     //if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     //}
     //else {
     //  mpSmDistributor->ss7SigtranStackRespPending++;
     //}

     enbDbgQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     enbDbgQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(enbDbgQMsg->t.stackData.req.u.debug),
						(U8*)enaDbg,sizeof(DebugEnableDisable));
     enbDbgQMsg->t.stackData.stackLayer = 
												enbDbgQMsg->t.stackData.req.u.debug.layer;
     enbDbgQMsg->t.stackData.cmdType = ENABLE_DEBUG;
     //enbDbgQMsg->t.stackData.procId = procIdList[i];
     enbDbgQMsg->t.stackData.procId = SFndProcId();
     enbDbgQMsg->t.stackData.miRequestId = liRequestId;
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (enbDbgQMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliEnaDebug, postMsg failed");
       delete enbDbgQMsg;
       return -1;
     }
  }

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliEnaDebug()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliEnaAlarm()
*
*     Desc:  Enable Alarm 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliEnaAlarm(AlarmEnableDisable *alm, 
																									vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliEnaAlarm()");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();
  U8 numProc      = 1;       // control command should be gone 

  for(int i=0;i<numProc;i++) 
	{
  	/*
   	* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
		INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
    if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
    }
    else {
       mpSmDistributor->ss7SigtranStackRespPending++;
    }

		qMsg->mSrc = BP_AIN_SM_SRC_EMS;

		qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
		cmMemcpy((U8*)&(qMsg->t.stackData.req.u.alarm),
						(U8*)alm,sizeof(AlarmEnableDisable));
     qMsg->t.stackData.stackLayer = qMsg->t.stackData.req.u.alarm.Layer;
     qMsg->t.stackData.cmdType = ENABLE_ALARM;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

    /*if(qMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER) 
     qMsg->mSrc = BP_AIN_SM_SRC_STACK_ALM;
    else if(qMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
     qMsg->mSrc = BP_AIN_SM_SRC_EMS;
    */
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
			logger.logMsg (ERROR_FLAG, 0, "cliEnaDebug, postMsg failed");
       delete qMsg;
       return -1;
     }
  }

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliEnaAlarm()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDisableAlarm()
*
*     Desc:  Disable Alarm 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDisableAlarm(AlarmEnableDisable *alrm, 
																	vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDisableAlarm()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult 	  = 0;
  U8 numProc 	    = procIdList.size();
	int liRequestId = getContextId();

  for(int i=0;i<numProc;i++) 
	{
  	/*
   	* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
		INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
    if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
    }
    else {
      mpSmDistributor->ss7SigtranStackRespPending++;
    }

		qMsg->mSrc = BP_AIN_SM_SRC_EMS;

		qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
		cmMemcpy((U8*)&(qMsg->t.stackData.req.u.alarm),
																(U8*)alrm,sizeof(AlarmEnableDisable));
     qMsg->t.stackData.stackLayer = qMsg->t.stackData.req.u.alarm.Layer;
     qMsg->t.stackData.cmdType = DISABLE_ALARM;
     qMsg->t.stackData.procId = procIdList[i];
     qMsg->t.stackData.miRequestId = liRequestId;
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliDisableAlarm, postMsg failed");
       delete qMsg;
       return -1;
     }
  }

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDisableAlarm()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliEnaTrace()
*
*     Desc:  Enable Trace 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliEnaTrace(TraceEnableDisable *enaTrc, 
																								vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliEnaTrace()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult 	  = 0;
  int liRequestId = getContextId();
  U8 numProc 		  = procIdList.size();

  //for(int i=0;i<numProc;i++) 
	{
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *enbTrcQMsg = new INGwSmQueueMsg;
     //if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     //}
     //else {
     //  mpSmDistributor->ss7SigtranStackRespPending++;
     //}

     enbTrcQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     enbTrcQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    cmMemcpy((U8*)&(enbTrcQMsg->t.stackData.req.u.trace),
						(U8*)enaTrc,sizeof(TraceEnableDisable));
		enbTrcQMsg->t.stackData.stackLayer = 
														enbTrcQMsg->t.stackData.req.u.trace.layer;
     enbTrcQMsg->t.stackData.cmdType = ENABLE_TRACE;
     //enbTrcQMsg->t.stackData.procId = procIdList[i];
     enbTrcQMsg->t.stackData.procId = SFndProcId();
     enbTrcQMsg->t.stackData.miRequestId = liRequestId;
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (enbTrcQMsg) != BP_AIN_SM_OK)
     {
			logger.logMsg (ERROR_FLAG, 0, "cliEnaTrace, postMsg failed");
       delete enbTrcQMsg;
       return -1;
     }
  }

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		liResult = -1;
	 	logger.logMsg(ERROR_FLAG, 0,
	 	"INGwSmWrapper::cliEnaTrace() Failed due to Timedout");
	}

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliEnaTrace()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliEnaLocalSsn()
*
*     Desc:  Enable Local SSN 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliEnaLocalSsn(SsnEnable *enaSsn,
												Ss7SigtranStackResp **resp , vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliEnaLocalSsn()");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();
  U8 numProc      = procIdList.size();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *enbLocalSsnQMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending = 1;

     enbLocalSsnQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     enbLocalSsnQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(enbLocalSsnQMsg->t.stackData.req.u.ssnEnable),
					(U8*)enaSsn,sizeof(SsnEnable));
     enbLocalSsnQMsg->t.stackData.stackLayer = BP_AIN_SM_TCA_LAYER;
     enbLocalSsnQMsg->t.stackData.cmdType = ENABLE_LOCAL_SSN;
     enbLocalSsnQMsg->t.stackData.procId = SFndProcId();
     enbLocalSsnQMsg->t.stackData.miRequestId = liRequestId;
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (enbLocalSsnQMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliEnaLocalSsn, postMsg failed");
       delete enbLocalSsnQMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
		 "INGwSmWrapper::cliEnaLocalSsn() Failed due to Timedout");
		liResult = -1;
	}

 	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::cliEnaLocalSsn()");

  return liResult;
}

/******************************************************************************
*
*     Fun:   cliEnaUsrpart()
*
*     Desc:  Enable User Part
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliEnaUsrpart(EnableUserPart *enaUsrPart, 
								Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliEnaUsrpart()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      =  procIdList.size();
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *enbUsrPartQMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending = 1;

     enbUsrPartQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     enbUsrPartQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(enbUsrPartQMsg->t.stackData.req.u.enableUserPart),
					(U8*)enaUsrPart,sizeof(EnableUserPart));
     enbUsrPartQMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     enbUsrPartQMsg->t.stackData.cmdType = ENABLE_USERPART;
     enbUsrPartQMsg->t.stackData.procId = SFndProcId();
	enbUsrPartQMsg->t.stackData.miRequestId= liRequestId;
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (enbUsrPartQMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliEnaUsrpart, postMsg failed");
       delete enbUsrPartQMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
					"INGwSmWrapper::cliEnaUsrpart() Failed due to Timedout");
		liResult = -1;
	}

 	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliEnaUsrpart()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliEnaEp()
*
*     Desc:  Enable End Point 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliEnaEp(EnableEndPoint *enaEp, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliEnaEp()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size();
  int liRequestId = getContextId();

  //for(int i=0;i<numProc;i++) 
	//{
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *enbEpQMsg = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending = 1;

     enbEpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     enbEpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(enbEpQMsg->t.stackData.req.u.enableEp),
						 (U8*)enaEp,sizeof(EnableEndPoint));
     enbEpQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     enbEpQMsg->t.stackData.cmdType = ENABLE_ENDPOINT;
     //enbEpQMsg->t.stackData.procId = procIdList[i];
     enbEpQMsg->t.stackData.procId = SFndProcId();
     enbEpQMsg->t.stackData.miRequestId = liRequestId;
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (enbEpQMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliEnaEp, postMsg failed");
       delete enbEpQMsg;
       return -1;
     }

     INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
      mpSmDistributor->ss7SigtranStackRespPending++;

     qMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(qMsg->t.stackData.req.u.enableEp),
						 (U8*)enaEp,sizeof(EnableEndPoint));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_SCT_LAYER;
     qMsg->t.stackData.cmdType = ENABLE_ENDPOINT;
     //qMsg->t.stackData.procId = procIdList[i];
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0,"cliEnaEp, postMsg failed");
       delete qMsg;
       return -1;
     }
  //}

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
					 "INGwSmWrapper::cliEnaEp() Failed due to Timedout");
		liResult = -1;
	}

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliEnaEp()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDisableEp()
*
*     Desc:  Disable End Point 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDisableEp(DisableEndPoint *disEp, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDisableEp()");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size();
  int liRequestId = getContextId();

  //for(int i=0;i<numProc;i++) 
	//{
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *disEpQMsg = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending = 1;

     disEpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     disEpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(disEpQMsg->t.stackData.req.u.disableEp),
						 (U8*)disEp,sizeof(DisableEndPoint));
     disEpQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     disEpQMsg->t.stackData.cmdType = DISABLE_ENDPOINT;
     //disEpQMsg->t.stackData.procId = procIdList[i];
     disEpQMsg->t.stackData.procId = SFndProcId();
     disEpQMsg->t.stackData.miRequestId= liRequestId;
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (disEpQMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliDisableEp, postMsg failed");
       delete disEpQMsg;
       return -1;
     }

     INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
      mpSmDistributor->ss7SigtranStackRespPending++;

     qMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(qMsg->t.stackData.req.u.disableEp),
						 (U8*)disEp,sizeof(DisableEndPoint));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_SCT_LAYER;
     qMsg->t.stackData.cmdType = DISABLE_ENDPOINT;
     //qMsg->t.stackData.procId = procIdList[i];
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliDisableEp, postMsg failed");
       delete qMsg;
       return -1;
     }
  //}

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
						"INGwSmWrapper::cliDisableEp() Failed due to Timedout");
		liResult = -1;
	}

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDisableEp()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDisableUsrpart()
*
*     Desc:  Disable User Part
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDisableUsrpart(DisableUserPart *disUsrPart, 
											Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDisableUsrpart()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult     = 0;
  U8 numProc       = procIdList.size();
  int liRequestId  = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *disUsrPartQMsg = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending = 1;

     disUsrPartQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     disUsrPartQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	   cmMemcpy((U8*)&(disUsrPartQMsg->t.stackData.req.u.disableUserPart),
					(U8*)disUsrPart,sizeof(DisableUserPart));
     disUsrPartQMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     disUsrPartQMsg->t.stackData.cmdType = DISABLE_USERPART;
     disUsrPartQMsg->t.stackData.procId = SFndProcId();
     disUsrPartQMsg->t.stackData.miRequestId = liRequestId;
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (disUsrPartQMsg) != BP_AIN_SM_OK)
     {
		    logger.logMsg (ERROR_FLAG, 0, "cliDisableUsrpart, postMsg failed");
        delete disUsrPartQMsg;
        return -1;
     }



	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		 logger.logMsg(ERROR_FLAG, 0,
									 "INGwSmWrapper::cliDisableUsrpart() Failed due to Timedout");
			liResult = -1;
	}

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDisableUsrpart()");
  return liResult;
}


/******************************************************************************
*
*     Fun:   cliUnbndSapMtp3()
*
*     Desc:  Unbind MTP3 SAP
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliUnbndSapMtp3(DisableUserPart *disUsrPart, 
											Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliUnbndSapMtp3()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult     = 0;
  U8 numProc       = procIdList.size();
  int liRequestId  = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
   INGwSmQueueMsg *disMtp3UsrPartQMsg = new INGwSmQueueMsg;
   mpSmDistributor->ss7SigtranStackRespPending = 1;

   disMtp3UsrPartQMsg->mSrc = BP_AIN_SM_SRC_EMS;

   disMtp3UsrPartQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	 cmMemcpy((U8*)&(disMtp3UsrPartQMsg->t.stackData.req.u.disableUserPart),
	 		(U8*)disUsrPart,sizeof(DisableUserPart));
   disMtp3UsrPartQMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
   disMtp3UsrPartQMsg->t.stackData.cmdType = DISABLE_SAPS;
   disMtp3UsrPartQMsg->t.stackData.procId = SFndProcId();
   disMtp3UsrPartQMsg->t.stackData.miRequestId = liRequestId;
   
   //post the message to the Distributor
   if (mpSmDistributor->postMsg (disMtp3UsrPartQMsg) != BP_AIN_SM_OK)
   {
	   logger.logMsg (ERROR_FLAG, 0, "cliUnbndSapMtp3, postMsg failed");
     delete disMtp3UsrPartQMsg;
     return -1;
   }

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		 logger.logMsg(ERROR_FLAG, 0,
									 "INGwSmWrapper::cliUnbndSapMtp3() Failed due to Timedout");
			liResult = -1;
	}

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliUnbndSapMtp3()");
  return liResult;
}



/******************************************************************************
*
*     Fun:   cliUnbndM3uaSaps()
*
*     Desc:  Unbind M3UA SCTSAP & SCTP TSAP
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliUnbndM3uaSaps(DelEndPoint *ep, 
											Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliUnbndM3uaSaps()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult     = 0;
  U8 numProc       = procIdList.size();
  int liRequestId  = getContextId();
  int timeOut = 20;

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
   INGwSmQueueMsg *disSapQMsg = new INGwSmQueueMsg;
   mpSmDistributor->ss7SigtranStackRespPending = 1;

   disSapQMsg->mSrc = BP_AIN_SM_SRC_EMS;

   disSapQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	 cmMemcpy((U8*)&(disSapQMsg->t.stackData.req.u.delEp),
	 		(U8*)ep,sizeof(DelEndPoint));
   disSapQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
   disSapQMsg->t.stackData.cmdType = DISABLE_SAPS;
   disSapQMsg->t.stackData.procId = SFndProcId();
   disSapQMsg->t.stackData.miRequestId = liRequestId;
   
   //post the message to the Distributor
   if (mpSmDistributor->postMsg (disSapQMsg) != BP_AIN_SM_OK)
   {
	   logger.logMsg (ERROR_FLAG, 0, "cliUnbndM3uaSaps, postMsg failed");
     delete disSapQMsg;
     return -1;
   }


  INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
   mpSmDistributor->ss7SigtranStackRespPending++;

   qMsg->mSrc = BP_AIN_SM_SRC_EMS;

   qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	 cmMemcpy((U8*)&(qMsg->t.stackData.req.u.delEp),
	 		(U8*)ep,sizeof(DelEndPoint));
   qMsg->t.stackData.stackLayer = BP_AIN_SM_SCT_LAYER;
   qMsg->t.stackData.cmdType = DISABLE_SAPS;
   qMsg->t.stackData.procId = SFndProcId();
   qMsg->t.stackData.miRequestId = liRequestId;
   
   //post the message to the Distributor
   if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
   {
	   logger.logMsg (ERROR_FLAG, 0, "cliUnbndM3uaSaps, postMsg failed");
     delete qMsg;
     return -1;
   }

  
#if 0
   INGwSmQueueMsg *qMsg1 = new INGwSmQueueMsg;
   mpSmDistributor->ss7SigtranStackRespPending++;

   qMsg1->mSrc = BP_AIN_SM_SRC_EMS;

   qMsg1->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	 cmMemcpy((U8*)&(qMsg1->t.stackData.req.u.delEp),
	 		(U8*)ep,sizeof(DelEndPoint));
   qMsg1->t.stackData.stackLayer = BP_AIN_SM_TUC_LAYER;
   qMsg1->t.stackData.cmdType = DISABLE_SAPS;
   qMsg1->t.stackData.procId = SFndProcId();
   qMsg1->t.stackData.miRequestId = liRequestId;
   
   //post the message to the Distributor
   if (mpSmDistributor->postMsg (qMsg1) != BP_AIN_SM_OK)
   {
	   logger.logMsg (ERROR_FLAG, 0, "cliUnbndM3uaSaps, postMsg failed");
     delete qMsg1;
     return -1;
   }
#endif

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 //block the current thread
  if (blockOperation (liRequestId,timeOut) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		 logger.logMsg(ERROR_FLAG, 0,
									 "INGwSmWrapper::cliUnbndM3uaSaps() Failed due to Timedout");
			liResult = -1;
	}

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliUnbndM3uaSaps()");
  return liResult;
}


/******************************************************************************
*
*     Fun:   cliDisLocalSsn()
*
*     Desc:  Disable Local SSN 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDisableLocalSsn(SsnDisable *disSsn, 
												Ss7SigtranStackResp **resp, vector<int> &procIdList) 
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDisLocalSsn()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult 	  = 0;
  U8 numProc 		  = procIdList.size();
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *disLocalSsnQMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending = 1;

     disLocalSsnQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     disLocalSsnQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(disLocalSsnQMsg->t.stackData.req.u.ssnDisable),
					(U8*)disSsn,sizeof(SsnDisable));
     disLocalSsnQMsg->t.stackData.stackLayer = BP_AIN_SM_TCA_LAYER;
     disLocalSsnQMsg->t.stackData.cmdType = DISABLE_LOCAL_SSN;
     disLocalSsnQMsg->t.stackData.procId = SFndProcId();
     disLocalSsnQMsg->t.stackData.miRequestId = liRequestId;
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (disLocalSsnQMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliDisLocalSsn, postMsg failed");
       delete disLocalSsnQMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
							"INGwSmWrapper::cliDisableLocalSsn() Failed due to Timedout");
    liResult = -1;
	}

 	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDisableLocalSsn()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDisableDebug()
*
*     Desc:  Disable Debug 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDisableDebug(DebugEnableDisable *disDbg, 
																								vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDisableDebug()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size();;
  int liRequestId = getContextId();

  //for(int i=0;i<numProc;i++) 
	{
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *disDbgQMsg = new INGwSmQueueMsg;
     //if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     //}
     //else {
     //  mpSmDistributor->ss7SigtranStackRespPending++;
     //}

     disDbgQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     disDbgQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(disDbgQMsg->t.stackData.req.u.debug),
						 (U8*)disDbg,sizeof(DebugEnableDisable));
     disDbgQMsg->t.stackData.stackLayer = 
												disDbgQMsg->t.stackData.req.u.debug.layer;
     disDbgQMsg->t.stackData.cmdType = DISABLE_DEBUG;
     //disDbgQMsg->t.stackData.procId = procIdList[i];
     disDbgQMsg->t.stackData.procId = SFndProcId();
     disDbgQMsg->t.stackData.miRequestId = liRequestId;
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (disDbgQMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliDisableDebug, postMsg failed");
       delete disDbgQMsg;
       return -1;
     }
  }

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
										"INGwSmWrapper::cliDisableDebug() Failed due to Timedout");
		liResult = -1;
	}

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDisableDebug()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDisableTrace()
*
*     Desc:  Disable Trace 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDisableTrace(TraceEnableDisable *disTrc, 
																						vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDisableTrace()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  int liResult 		= 0;
  U8 numProc 		  =  procIdList.size();
  int liRequestId = getContextId();

  //for(int i=0;i<numProc;i++) 
	{
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *disTrcQMsg = new INGwSmQueueMsg;
     //if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
     //}
     //else {
       //mpSmDistributor->ss7SigtranStackRespPending++;
     //}

     disTrcQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     disTrcQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
		cmMemcpy((U8*)&(disTrcQMsg->t.stackData.req.u.trace),
						(U8*)disTrc,sizeof(TraceEnableDisable));
		disTrcQMsg->t.stackData.stackLayer = 
																disTrcQMsg->t.stackData.req.u.trace.layer;
     disTrcQMsg->t.stackData.cmdType = DISABLE_TRACE;
     //disTrcQMsg->t.stackData.procId = procIdList[i];
     disTrcQMsg->t.stackData.procId = SFndProcId();
     disTrcQMsg->t.stackData.miRequestId = liRequestId;
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (disTrcQMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliDisableDebug, postMsg failed");
       delete disTrcQMsg;
       return -1;
     }
  }

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		liResult = -1;
		logger.logMsg(ERROR_FLAG, 0,
									"INGwSmWrapper::cliDisableTrace() Failed due to Timedout");
	}

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDisableTrace()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAssocUp()
*
*     Desc:  Association UP
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliAssocUp(M3uaAssocUp *apParm, Ss7SigtranStackResp **resp,
                              vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAssocUp()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
	mpSmDistributor->ss7SigtranStackRespPending = 1;
	INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

	qMsg->mSrc = BP_AIN_SM_SRC_EMS;

	qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(qMsg->t.stackData.req.u.m3uaAssocUp),
					(U8*)apParm,sizeof(M3uaAssocUp));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     qMsg->t.stackData.cmdType = ASSOC_UP;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliAssocUp, postMsg failed");
       delete qMsg;
       return -1;
     }

 	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
	   logger.logMsg(ERROR_FLAG, 0,
							"INGwSmWrapper::cliAssocUp() Failed due to Timedout");
    liResult = -1;
	}

 	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAssocUp()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAssocDn()
*
*     Desc:  Association UP
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliAssocDn(M3uaAssocDown *apParm, 
											Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAssocDn()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc 		  = procIdList.size();
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
	mpSmDistributor->ss7SigtranStackRespPending = 1;
	INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

	qMsg->mSrc = BP_AIN_SM_SRC_EMS;

	qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(qMsg->t.stackData.req.u.m3uaAssocDown),
				  (U8*)apParm,sizeof(M3uaAssocDown));

     qMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     qMsg->t.stackData.cmdType = ASSOC_DOWN;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliAssocDn, postMsg failed");
       delete qMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
	  logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliAssocDn() Failed due to Timedout");
    liResult = -1;
	}

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAssocDn()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAspUp()
*
*     Desc:  ASP UP
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliAspUp(M3uaAspUp *apParm, Ss7SigtranStackResp **resp,
                            vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAspUp()");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
  */
  mpSmDistributor->ss7SigtranStackRespPending = 1;

	INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

	qMsg->mSrc = BP_AIN_SM_SRC_EMS;

	qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(qMsg->t.stackData.req.u.m3uaAspUp),
					(U8*)apParm,sizeof(M3uaAspUp));

     qMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     qMsg->t.stackData.cmdType = ASP_UP;
	qMsg->t.stackData.procId     = SFndProcId();
	qMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliAspUp, postMsg failed");
       delete qMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
 		logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliAspUp() Failed due to Timedout");
    liResult = -1;
	}

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAspUp()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAspDn()
*
*     Desc:  ASP UP
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliAspDn(M3uaAspDown *apParm, Ss7SigtranStackResp **resp, 
															vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAspDn()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = 1;
  int liRequestId = getContextId();

  //for(int i=0;i<numProc;i++) 
	//{
  	/*
   	* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
    mpSmDistributor->ss7SigtranStackRespPending = 1;

		INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

		qMsg->mSrc = BP_AIN_SM_SRC_EMS;

		qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
		cmMemcpy((U8*)&(qMsg->t.stackData.req.u.m3uaAspDown),
						(U8*)apParm,sizeof(M3uaAspDown));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     qMsg->t.stackData.cmdType = ASP_DOWN;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliAspDn, postMsg failed");
       delete qMsg;
       return -1;
     }
  //}

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
 	else
	{
    logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliAspDn() Failed due to Timedout");
    liResult = -1;
  }

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAspDn()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAspActive()
*
*     Desc:  ASP Active
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliAspActive(M3uaAspAct *apParm, Ss7SigtranStackResp **resp,
                                vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAspActive()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
	mpSmDistributor->ss7SigtranStackRespPending = 1;
	INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

	qMsg->mSrc = BP_AIN_SM_SRC_EMS;

	qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(qMsg->t.stackData.req.u.m3uaAspAct),
					(U8*)apParm,sizeof(M3uaAspAct));

     qMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     qMsg->t.stackData.cmdType = ASP_ACTIVE;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliAspActive, postMsg failed");
       delete qMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

  //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
	  logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliAspActive() Failed due to Timedout");
    liResult = -1;
	}

 if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAspActive()");
  return liResult;
}


#ifdef INC_ASP_SNDDAUD
/******************************************************************************
*
*     Fun:   cliSendDaud()
*
*     Desc:  Send DAUD
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliSendDaud(Daud *apParm, Ss7SigtranStackResp **resp)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering cliSendDaud()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
	mpSmDistributor->ss7SigtranStackRespPending = 1;
	INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

	qMsg->mSrc = BP_AIN_SM_SRC_EMS;

	qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(qMsg->t.stackData.req.u.daud),
					(U8*)apParm,sizeof(Daud));

  qMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
  qMsg->t.stackData.cmdType = SEND_DAUD;
  qMsg->t.stackData.procId = SFndProcId();
  qMsg->t.stackData.miRequestId = liRequestId;

  //post the message to the Distributor
  if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
  {
		logger.logMsg (ERROR_FLAG, 0, "cliSendDaud, postMsg failed");
       delete qMsg;
       return -1;
  }

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

  //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
	  logger.logMsg(ERROR_FLAG, 0,
                      "cliSendDaud() Failed due to Timedout");
    liResult = -1;
	}

 if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving cliSendDaud()");
  return liResult;
}
#endif


/******************************************************************************
*
*     Fun:   cliAspInActive()
*
*     Desc:  ASP Active
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliAspInActive(M3uaAspInAct *apParm, 
												Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAspInActive()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult = 0;
  U8 numProc;
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
	mpSmDistributor->ss7SigtranStackRespPending = 1;
	INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

	qMsg->mSrc = BP_AIN_SM_SRC_EMS;

	qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(qMsg->t.stackData.req.u.m3uaAspInact),
					(U8*)apParm,sizeof(M3uaAspInAct));

     qMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     qMsg->t.stackData.cmdType = ASP_INACTIVE;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

	if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
	{
		logger.logMsg (ERROR_FLAG, 0, "cliAspInActive, postMsg failed");
		delete qMsg;
		return -1;
	}

	INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

  //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
                   "INGwSmWrapper::cliAspInActive() Failed due to Timedout");
    liResult = -1;

	}

 	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAspInActive()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliEnaLnk()
*
*     Desc:  Enable Link 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliEnaLnk(LinkEnable *enalnk, Ss7SigtranStackResp **resp)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliEnaLnk");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
   INGwSmQueueMsg *enbLnkMtp3QMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending = 1;


   enbLnkMtp3QMsg->mSrc = BP_AIN_SM_SRC_EMS;

   enbLnkMtp3QMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(enbLnkMtp3QMsg->t.stackData.req.u.lnkEnable),
					(U8*)enalnk,sizeof(LinkEnable));
   enbLnkMtp3QMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
   enbLnkMtp3QMsg->t.stackData.cmdType = ENABLE_LINK;
   enbLnkMtp3QMsg->t.stackData.procId = SFndProcId();
   enbLnkMtp3QMsg->t.stackData.miRequestId = liRequestId;
   
   //post the message to the Distributor
   if (mpSmDistributor->postMsg (enbLnkMtp3QMsg) != BP_AIN_SM_OK)
   {
		logger.logMsg (ERROR_FLAG, 0, "cliEnaLnk, postMsg failed");
     delete enbLnkMtp3QMsg;
     return -1;
   }

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
    logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliEnaLnk() Failed due to Timedout");
		liResult = -1;
	}

 	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliEnaLnk()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDisableLnk()
*
*     Desc:  Disable Link 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliDisableLnk(LinkDisable *dislnk, 
															  Ss7SigtranStackResp **resp)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDisableLnk");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();

  /*
  * create a new Queue object and used the request Id as
  * the thread id for the current thread. This will be used by Stack
  * Manager to unblock it if needed.
  */
  mpSmDistributor->ss7SigtranStackRespPending = 1;

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *disLnkMtp3QMsg = new INGwSmQueueMsg;


     disLnkMtp3QMsg->mSrc = BP_AIN_SM_SRC_EMS;

     disLnkMtp3QMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(disLnkMtp3QMsg->t.stackData.req.u.lnkDisable),
			    (U8*)dislnk,sizeof(LinkDisable));

     disLnkMtp3QMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
     disLnkMtp3QMsg->t.stackData.cmdType = DISABLE_LINK;
     disLnkMtp3QMsg->t.stackData.procId = SFndProcId();
     disLnkMtp3QMsg->t.stackData.miRequestId = liRequestId;
     
     //post the message to the Distributor
     if (mpSmDistributor->postMsg (disLnkMtp3QMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliDisableLnk, postMsg failed");
       delete disLnkMtp3QMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliDisableLnk() Failed due to Timedout");
    liResult = -1;
	}

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDisableLnk()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDelLink()
*
*     Desc:  Delete Link
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDelLink(DelLink *lnk, Ss7SigtranStackResp **resp)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDelLink");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult 	  = 0;
  int liRequestId = getContextId();


  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *delLnkMtp3QMsg = new INGwSmQueueMsg;
      mpSmDistributor->ss7SigtranStackRespPending = 1;

     delLnkMtp3QMsg->mSrc = BP_AIN_SM_SRC_EMS;

     delLnkMtp3QMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	   cmMemcpy((U8*)&(delLnkMtp3QMsg->t.stackData.req.u.delLnk),
					(U8*)lnk,sizeof(DelLink));
     delLnkMtp3QMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
     delLnkMtp3QMsg->t.stackData.cmdType = DEL_LINK;
     delLnkMtp3QMsg->t.stackData.procId = SFndProcId();
     delLnkMtp3QMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (delLnkMtp3QMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliDelLink, postMsg failed");
       delete delLnkMtp3QMsg;
       return -1;
     }

    /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *delLnkLMtp3QMsg = new INGwSmQueueMsg;
      mpSmDistributor->ss7SigtranStackRespPending++;

     delLnkLMtp3QMsg->mSrc = BP_AIN_SM_SRC_EMS;

     delLnkLMtp3QMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	   cmMemcpy((U8*)&(delLnkLMtp3QMsg->t.stackData.req.u.delLnk),
			    (U8*)lnk,sizeof(DelLink));
     delLnkLMtp3QMsg->t.stackData.stackLayer = BP_AIN_SM_LDF_MTP3_LAYER;
     delLnkLMtp3QMsg->t.stackData.cmdType = DEL_LINK;
     delLnkLMtp3QMsg->t.stackData.procId = SFndProcId();
     delLnkLMtp3QMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (delLnkLMtp3QMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliDelLink, postMsg failed");
       delete delLnkLMtp3QMsg;
       return -1;
     }


	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliDelLink() Failed due to Timedout");
    liResult = -1;
	}

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::cliDelLink()");

  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDelLinkSet()
*
*     Desc:  Delete LinkSet
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDelLinkSet(DelLinkSet *lnkSet, Ss7SigtranStackResp **resp,
                                 vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDelLinkSet");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size();
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *delLnkSetMtp3QMsg = new INGwSmQueueMsg;
      mpSmDistributor->ss7SigtranStackRespPending = 1;

     delLnkSetMtp3QMsg->mSrc = BP_AIN_SM_SRC_EMS;

     delLnkSetMtp3QMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(delLnkSetMtp3QMsg->t.stackData.req.u.delLnkSet),
				  (U8*)lnkSet,sizeof(DelLinkSet));
     delLnkSetMtp3QMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
     delLnkSetMtp3QMsg->t.stackData.cmdType = DEL_LINKSET;
     delLnkSetMtp3QMsg->t.stackData.procId = SFndProcId();
	delLnkSetMtp3QMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (delLnkSetMtp3QMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete delLnkSetMtp3QMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliDelLinkSet() Failed due to Timedout");
    liResult = -1;
	}

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }


  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDelLinkSet()");

  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDelRoute()
*
*     Desc:  Delete Route
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDelRoute(DelRoute *rte, Ss7SigtranStackResp **resp,
                               vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDelRoute");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      =  procIdList.size();
  int liRequestId = getContextId();
	int timeOut     = 10;

  RouteSeq* rteList = INGwSmBlkConfig::getInstance().getRouteList();

  logger.logMsg (ALWAYS_FLAG, 0, 
      "INGwSmWrapper::cliDelRoute RouteList Size [%d]", rteList->size());

  int i = 0;
  RouteSeq::iterator rteIt;
	for(rteIt = rteList->begin(); rteIt != rteList->end(); rteIt++,i++) 
	{
    //i++;
    logger.logMsg (ALWAYS_FLAG, 0, 
		"INGwSmWrapper::cliDelRoute DPC [%d] nSapId[%d] ", (*rteIt).dpc, 
		(*rteIt).nSapId);

    if((*rteIt).dpc == rte->dpc)
    {
      if((*rteIt).swtchType != 255)
      {
      /*
      * create a new Queue object and used the request Id as
      * the thread id for the current thread. This will be used by Stack
      * Manager to unblock it if needed.
     */
        INGwSmQueueMsg *delRteMtp3QMsg = new INGwSmQueueMsg;
        if (i == 0) {
          mpSmDistributor->ss7SigtranStackRespPending = 1;
        }
        else {
          mpSmDistributor->ss7SigtranStackRespPending++;
        }

        delRteMtp3QMsg->mSrc = BP_AIN_SM_SRC_EMS;
    
        delRteMtp3QMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
        cmMemcpy((U8*)&(delRteMtp3QMsg->t.stackData.req.u.delRoute),
							  (U8*)rte,sizeof(DelRoute));
        delRteMtp3QMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
        delRteMtp3QMsg->t.stackData.cmdType = DEL_ROUTE;
        delRteMtp3QMsg->t.stackData.procId = SFndProcId();
        delRteMtp3QMsg->t.stackData.miRequestId= liRequestId;

        //post the message to the Distributor
        if (mpSmDistributor->postMsg (delRteMtp3QMsg) != BP_AIN_SM_OK)
        {
          logger.logMsg (ERROR_FLAG, 0, "cliDelRoute, postMsg failed");
          delete delRteMtp3QMsg;
          return -1;
        }
      }

      /*
      * create a new Queue object and used the request Id as
      * the thread id for the current thread. This will be used by Stack
      * Manager to unblock it if needed.
      */
      if((*rteIt).swtch != -1)
      {

        INGwSmQueueMsg *delRteSccpQMsg = new INGwSmQueueMsg;
        if (((*rteIt).swtchType == 255) && (i == 0)) {
          mpSmDistributor->ss7SigtranStackRespPending = 1;
        }
        else {
          mpSmDistributor->ss7SigtranStackRespPending++;
        }

        delRteSccpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

        delRteSccpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
        cmMemcpy((U8*)&(delRteSccpQMsg->t.stackData.req.u.delRoute),
							  (U8*)rte,sizeof(DelRoute));
        delRteSccpQMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
        delRteSccpQMsg->t.stackData.cmdType = DEL_ROUTE;
        delRteSccpQMsg->t.stackData.procId = SFndProcId();
        delRteSccpQMsg->t.stackData.miRequestId= liRequestId;

          //post the message to the Distributor
        if (mpSmDistributor->postMsg (delRteSccpQMsg) != BP_AIN_SM_OK)
        {
          logger.logMsg (ERROR_FLAG, 0, "cliDelRoute, postMsg failed");
          delete delRteSccpQMsg;
          return -1;
        }
      }
    }
  }

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

  if (i > 0) {
 	  //block the current thread
    if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
    {
      if (removeBlockingContext (liRequestId, lpContext))
      {
        if (lpContext->returnValue != BP_AIN_SM_OK)
          liResult = -1;
      }
      else
      {
        liResult = -1;
      }
    }
	  else
	  {
	  	logger.logMsg(ERROR_FLAG, 0,
                        "INGwSmWrapper::cliDelRoute() Failed due to Timedout");
      liResult = -1;
	  }
  }

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    if (i > 0) {
      (**resp).reason     = FAILURE_REASON;
      (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
      (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
    }
    else {
      (**resp).reason     = FAILURE_REASON + 1;
      (**resp).reasonStr  = (char*)"Unknown";
      (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
    }
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDelRoute()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDelNetwork()
*
*     Desc:  Delete Network
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDelNetwork(DelNetwork *nwk, Ss7SigtranStackResp **resp, 
                                 vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDelNetwork");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();

	int nodeType = INGwSmBlkConfig::getInstance().getProtoTypeForNwId(nwk->nwkId);

	if ((nodeType == TRANSPORT_TYPE_SIGTRAN) || (nodeType == TRANSPORT_TYPE_BOTH))
	{
		/*
		 * create a new Queue object and used the request Id as
		 * the thread id for the current thread. This will be used by Stack
		 * Manager to unblock it if needed.
		 */
		 INGwSmQueueMsg *delNwkM3uQMsg = new INGwSmQueueMsg;
		 mpSmDistributor->ss7SigtranStackRespPending = 1;

		// Need to investigate on requestId.

		delNwkM3uQMsg->mSrc = BP_AIN_SM_SRC_EMS;

		delNwkM3uQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
		cmMemcpy((U8*)&(delNwkM3uQMsg->t.stackData.req.u.delNwk),
																			(U8*)nwk,sizeof(DelNetwork));
		 delNwkM3uQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
		 delNwkM3uQMsg->t.stackData.cmdType = DEL_NETWORK;
		 delNwkM3uQMsg->t.stackData.procId = SFndProcId();
		 delNwkM3uQMsg->t.stackData.miRequestId = liRequestId;

		 //post the message to the Distributor
		 if (mpSmDistributor->postMsg (delNwkM3uQMsg) != BP_AIN_SM_OK)
		 {
				logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
						 delete delNwkM3uQMsg;
				 return -1;
			}
		}

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  INGwSmQueueMsg *delNwkSccpQMsg = new INGwSmQueueMsg;
 
	if ((nodeType == TRANSPORT_TYPE_SIGTRAN) ||(nodeType == TRANSPORT_TYPE_BOTH)) 	{
     mpSmDistributor->ss7SigtranStackRespPending++;
  }
  else {
     mpSmDistributor->ss7SigtranStackRespPending = 1;
  }

  delNwkSccpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

  delNwkSccpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(delNwkSccpQMsg->t.stackData.req.u.delNwk),
																					(U8*)nwk,sizeof(DelNetwork));

  delNwkSccpQMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
  delNwkSccpQMsg->t.stackData.cmdType = DEL_NETWORK;
  delNwkSccpQMsg->t.stackData.procId = SFndProcId();
  delNwkSccpQMsg->t.stackData.miRequestId = liRequestId;

  if (mpSmDistributor->postMsg (delNwkSccpQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0, "cliDelNetwork, postMsg failed");
    delete delNwkSccpQMsg;
    return -1;
  }

	 INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliDelNetwork() Failed due to Timedout");
 	   liResult = -1;
	}

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDelNetwork()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDelEp()
*
*     Desc:  Delete End Point
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDelEp(DelEndPoint *ep, Ss7SigtranStackResp **resp, 
												    vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDelEp");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc 		  =  procIdList.size();
  int liRequestId = getContextId();
	int timeOut     = 8;

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *delEpM3uQMsg = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending = 1;

     delEpM3uQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     delEpM3uQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(delEpM3uQMsg->t.stackData.req.u.delEp),
				  (U8*)ep,sizeof(DelEndPoint));

     delEpM3uQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     delEpM3uQMsg->t.stackData.cmdType = DEL_ENDPOINT;
     delEpM3uQMsg->t.stackData.procId = SFndProcId();
     delEpM3uQMsg->t.stackData.miRequestId = liRequestId;

     if (mpSmDistributor->postMsg (delEpM3uQMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliDelEp, postMsg failed");
       delete delEpM3uQMsg;
       return -1;
     }

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  INGwSmQueueMsg *delEpSctpQMsg = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending++;

  delEpSctpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

  delEpSctpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(delEpSctpQMsg->t.stackData.req.u.delEp),
					(U8*)ep,sizeof(DelEndPoint));
  delEpSctpQMsg->t.stackData.stackLayer = BP_AIN_SM_SCT_LAYER;
  delEpSctpQMsg->t.stackData.subOpr = BP_AIN_SM_SCT_SCTSAP;
  delEpSctpQMsg->t.stackData.cmdType = DEL_ENDPOINT;
  delEpSctpQMsg->t.stackData.procId = SFndProcId();
  delEpSctpQMsg->t.stackData.miRequestId = liRequestId;
  
  if (mpSmDistributor->postMsg (delEpSctpQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0, "cliDelEp, postMsg failed");
    delete delEpSctpQMsg;
    return -1;
  }

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  INGwSmQueueMsg *delEpSctpLQMsg = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending++;

  delEpSctpLQMsg->mSrc = BP_AIN_SM_SRC_EMS;

  delEpSctpLQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(delEpSctpLQMsg->t.stackData.req.u.delEp),
				  (U8*)ep,sizeof(DelEndPoint));
  delEpSctpLQMsg->t.stackData.stackLayer = BP_AIN_SM_SCT_LAYER;
  delEpSctpLQMsg->t.stackData.subOpr = BP_AIN_SM_SCT_TSAP;
  delEpSctpLQMsg->t.stackData.cmdType = DEL_ENDPOINT;
  delEpSctpLQMsg->t.stackData.procId = SFndProcId();
  delEpSctpLQMsg->t.stackData.miRequestId= liRequestId;

  if (mpSmDistributor->postMsg (delEpSctpLQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0, "cliDelEp, postMsg failed");
    delete delEpSctpLQMsg;
    return -1;
  }

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  /*INGwSmBlkConfig & blkCfg = INGwSmBlkConfig::getInstance();
  EpSeq* epList = blkCfg.getEpList();
  if(epList->size() == 1){

  INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending++;

  qMsg->mSrc = BP_AIN_SM_SRC_EMS;

  qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(qMsg->t.stackData.req.u.delEp),(U8*)ep,sizeof(DelEndPoint));
  qMsg->t.stackData.stackLayer = BP_AIN_SM_TUC_LAYER;
  qMsg->t.stackData.cmdType = DEL_ENDPOINT;
  qMsg->t.stackData.procId = SFndProcId();
  qMsg->t.stackData.miRequestId = liRequestId;

  if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0, "cliDelEp, postMsg failed");
    delete qMsg;
    return -1;
  }
  /*}
  else{
  }*/

 INGwSmBlockingContext *lpContext = 0;
 *resp = new Ss7SigtranStackResp;

	//block the current thread
	if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliDelEp() Failed due to Timedout");
    liResult = -1;
	}

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDelEp()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDelAsp()
*
*     Desc:  Delete ASP
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDelAsp(DelPsp *asp, Ss7SigtranStackResp **resp, 
															vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDelAsp");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult = 0;
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *delAspM3uQMsg = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending = 1;

     delAspM3uQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     delAspM3uQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(delAspM3uQMsg->t.stackData.req.u.delPsp),
				  (U8*)asp,sizeof(DelPsp));
     delAspM3uQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     delAspM3uQMsg->t.stackData.cmdType = DEL_ASP;
     delAspM3uQMsg->t.stackData.procId = SFndProcId();
  delAspM3uQMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (delAspM3uQMsg) != BP_AIN_SM_OK)
     {
    logger.logMsg (ERROR_FLAG, 0, "cliDelAsp, postMsg failed");
       delete delAspM3uQMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
	if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliDelAsp() Failed due to Timedout");
    liResult = -1;
	}

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDelAsp()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDelAs()
*
*     Desc:  Delete AS
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDelAs(DelPs *as, Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDelAs");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc 		  =  procIdList.size();
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *delAsM3uQMsg = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending = 1;


     delAsM3uQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     delAsM3uQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(delAsM3uQMsg->t.stackData.req.u.delPs),(U8*)as,sizeof(DelPs));
     delAsM3uQMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     delAsM3uQMsg->t.stackData.subOpr = BP_AIN_SM_M3U_ROUTE;
     delAsM3uQMsg->t.stackData.cmdType = DEL_AS;
     delAsM3uQMsg->t.stackData.procId = SFndProcId();
     delAsM3uQMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (delAsM3uQMsg) != BP_AIN_SM_OK)
     {
    logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete delAsM3uQMsg;
       return -1;
     }

     /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending++;


     qMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(qMsg->t.stackData.req.u.delPs),(U8*)as,sizeof(DelPs));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     qMsg->t.stackData.subOpr = BP_AIN_SM_M3U_PS;
     qMsg->t.stackData.cmdType = DEL_AS;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
    logger.logMsg (ERROR_FLAG, 0, "cliDelAs, postMsg failed");
       delete qMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		 logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliDelAs() Failed due to Timedout");
    liResult = -1;
	}

  if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDelAs(), status<%d>, reason<%d>",(**resp).status,(**resp).reason);
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDelLocalSsn()
*
*     Desc:  Delete Local SSN
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDelLocalSsn(DelLocalSsn *lSsn, Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDelLocalSsn");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult 		= 0;
  int liRequestId = getContextId();
	int timeOut     = 6;

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
	INGwSmQueueMsg *delLSsnTcapQMsg = new INGwSmQueueMsg;
  mpSmDistributor->ss7SigtranStackRespPending = 1;

	delLSsnTcapQMsg->mSrc = BP_AIN_SM_SRC_EMS;

	delLSsnTcapQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(delLSsnTcapQMsg->t.stackData.req.u.delLocalSsn),
				  (U8*)lSsn,sizeof(DelLocalSsn));
	delLSsnTcapQMsg->t.stackData.stackLayer = BP_AIN_SM_TCA_LAYER;
	delLSsnTcapQMsg->t.stackData.subOpr = BP_AIN_SM_TCA_USAP;
	delLSsnTcapQMsg->t.stackData.cmdType = DEL_LOCAL_SSN;
	delLSsnTcapQMsg->t.stackData.procId = SFndProcId();
	delLSsnTcapQMsg->t.stackData.miRequestId = liRequestId;

	//post the message to the Distributor
	if (mpSmDistributor->postMsg (delLSsnTcapQMsg) != BP_AIN_SM_OK)
	{
    logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete delLSsnTcapQMsg;
       return -1;
	}

	/*
  create a new Queue object and used the request Id as
  the thread id for the current thread. This will be used by Stack
  Manager to unblock it if needed.
	*/
	INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
	mpSmDistributor->ss7SigtranStackRespPending++;

	qMsg->mSrc = BP_AIN_SM_SRC_EMS;

	qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(qMsg->t.stackData.req.u.delLocalSsn),
				  (U8*)lSsn,sizeof(DelLocalSsn));
	qMsg->t.stackData.stackLayer = BP_AIN_SM_TCA_LAYER;
	qMsg->t.stackData.subOpr = BP_AIN_SM_TCA_LSAP;
	qMsg->t.stackData.cmdType = DEL_LOCAL_SSN;
	qMsg->t.stackData.procId = SFndProcId();
	qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
    logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete qMsg;
       return -1;
     }

     /*
  create a new Queue object and used the request Id as
  the thread id for the current thread. This will be used by Stack
  Manager to unblock it if needed.
   */
     INGwSmQueueMsg *delLSsnSccpQMsg = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending++;

     delLSsnSccpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     delLSsnSccpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(delLSsnSccpQMsg->t.stackData.req.u.delLocalSsn),
		   (U8*)lSsn,sizeof(DelLocalSsn));
     delLSsnSccpQMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     delLSsnSccpQMsg->t.stackData.cmdType = DEL_LOCAL_SSN;
     delLSsnSccpQMsg->t.stackData.procId = SFndProcId();
  delLSsnSccpQMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (delLSsnSccpQMsg) != BP_AIN_SM_OK)
     {
    logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete delLSsnSccpQMsg;
       return -1;
     }
 
	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

	//block the current thread
 	if(blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
 	{
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliDelLocalSsn() Failed due to Timedout");
    liResult = -1;
	}

 	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDelLocalSsn()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDelRemoteSsn()
*
*     Desc:  Delete Remote SSN
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDelRemoteSsn(DelRemoteSsn *rSsn, vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0,
      "Entering INGwSmWrapper::cliDelRemoteSsn");
  return BP_AIN_SM_OK;

#if 0

  int liResult = 0;
  U8 numProc;
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor object is NULL");
    return -1;
  }

  //numProc =  mpTcapProvider->getProcIdList(procIdList);
  //int liRequestId = (int) pthread_self ();
  int liRequestId = getContextId();
  numProc =  procIdList.size();
  //for(int i=0;i<numProc;i++) {
  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *delRSsnSccpQMsg = new INGwSmQueueMsg;
     mpSmDistributor->ss7SigtranStackRespPending = 1;

  // Need to investigate on requestId.

     delRSsnSccpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     delRSsnSccpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(delRSsnSccpQMsg->t.stackData.req.u.delRemoteSsn),(U8*)rSsn,sizeof(DelRemoteSsn));
     delRSsnSccpQMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     delRSsnSccpQMsg->t.stackData.cmdType = DEL_REMOTE_SSN;
     //delRSsnSccpQMsg->t.stackData.procId = procIdList[i];
     delRSsnSccpQMsg->t.stackData.procId = SFndProcId();
     delRSsnSccpQMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (delRSsnSccpQMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0,
         "In configure, postMsg failed");
       delete delRSsnSccpQMsg;
       return -1;
     }


  //}


 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::cliDelRemoteSsn()");

  return liResult;
#endif
}

/******************************************************************************
*
*     Fun:   cliDelUsrPart
*
*     Desc:  Delete User Part
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDelUsrPart(DelUserPart *up, Ss7SigtranStackResp **resp, 
																 vector<int> &procIdList)
{
	logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDelUsrPart");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult 	  = 0;
  int liRequestId = getContextId();
	int timeOut     = 30;

	/*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
	INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
	mpSmDistributor->ss7SigtranStackRespPending = 1;

  qMsg->mSrc = BP_AIN_SM_SRC_EMS;

  qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(qMsg->t.stackData.req.u.delUserPart),
					(U8*)up,sizeof(DelUserPart));
     if (qMsg->t.stackData.req.u.delUserPart.userPartType == MTP3_USER)
     {
        qMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
     }
     else /* M3UA user */
     {
        qMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     }

     qMsg->t.stackData.cmdType = DEL_USR_PART;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete qMsg;
       return -1;
     }

    /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *qlMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending++;

     qlMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qlMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(qlMsg->t.stackData.req.u.delUserPart),
				  (U8*)up,sizeof(DelUserPart));

      if (qlMsg->t.stackData.req.u.delUserPart.userPartType == MTP3_USER)
     {
        qlMsg->t.stackData.stackLayer = BP_AIN_SM_LDF_MTP3_LAYER;
     }
     else /* M3UA user */
     {
        qlMsg->t.stackData.stackLayer = BP_AIN_SM_LDF_M3UA_LAYER;
     }

     qlMsg->t.stackData.cmdType = DEL_USR_PART;
     qlMsg->t.stackData.procId = SFndProcId();
     qlMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qlMsg) != BP_AIN_SM_OK)
     {
    logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete qlMsg;
       return -1;
     }

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
     INGwSmQueueMsg *delUpSccpQMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending++;

     delUpSccpQMsg->mSrc = BP_AIN_SM_SRC_EMS;

     delUpSccpQMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  cmMemcpy((U8*)&(delUpSccpQMsg->t.stackData.req.u.delUserPart),
				  (U8*)up,sizeof(DelUserPart));
     delUpSccpQMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     delUpSccpQMsg->t.stackData.cmdType = DEL_USR_PART;
     delUpSccpQMsg->t.stackData.procId = SFndProcId();
     delUpSccpQMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (delUpSccpQMsg) != BP_AIN_SM_OK)
     {
    logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete delUpSccpQMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
	if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
	   logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliDelUsrPart() Failed due to Timedout");
    liResult = -1;
	}

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDelUsrPart()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliStatusLink()
*
*     Desc:  Link Status
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliStatusLink(LinkStatus *apParm, Ss7SigtranStackResp **resp,
																vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0,
	"Entering INGwSmWrapper::cliStatusLink() for LinkId <%d>",apParm->lnkId);

	if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc   		=  procIdList.size();
  int liRequestId = getContextId();
	int timeOut			= 3*numProc+3;

  //for(int i=0;i<numProc;i++) 
	//{
		/*
		* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
    INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending = 1;

    qMsg->mSrc = BP_AIN_SM_SRC_EMS;

    qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    cmMemcpy((U8*)&(qMsg->t.stackData.req.u.lnkstatus),
						(U8*)apParm,sizeof(LinkStatus));

    qMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
    qMsg->t.stackData.cmdType = STA_LINK;
    qMsg->t.stackData.procId = SFndProcId();
    qMsg->t.stackData.miRequestId = liRequestId;

    //post the message to the Distributor
    if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
    {
     logger.logMsg (ERROR_FLAG, 0, "cliStatusLink, postMsg failed");
     delete qMsg;
     return -1;
		}
	//} //for loop
#if 0
	/*
	* create a new Queue object and used the request Id as
  * the thread id for the current thread. This will be used by Stack
  * Manager to unblock it if needed.
  */
	INGwSmQueueMsg *qSdMsg = new INGwSmQueueMsg;
  mpSmDistributor->ss7SigtranStackRespPending++;

	qSdMsg->mSrc = BP_AIN_SM_SRC_EMS;

	qSdMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(qSdMsg->t.stackData.req.u.lnkstatus),
					  (U8*)apParm,sizeof(LinkStatus));

	qSdMsg->t.stackData.stackLayer = BP_AIN_SM_MTP2_LAYER;
	qSdMsg->t.stackData.cmdType = STA_LINK;
	qSdMsg->t.stackData.procId = SFndProcId();
	qSdMsg->t.stackData.miRequestId = liRequestId;

	//post the message to the Distributor
	if (mpSmDistributor->postMsg (qSdMsg) != BP_AIN_SM_OK)
	{
		logger.logMsg (ERROR_FLAG, 0, "cliStatusLink, postMsg failed");
		delete qSdMsg;
		return -1;
	}
#endif

  INGwSmBlockingContext *lpContext = 0;
	*resp = new Ss7SigtranStackResp;

  //block the current thread
  if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else 
	{
		logger.logMsg(ERROR_FLAG, 0, 
											"INGwSmWrapper::cliStatusLink() Failed due to Timedout");
		liResult = -1;
	}

	if(lpContext != NULL)
	{
		(**resp).procId     = lpContext->resp.procId;
		//(**resp).status     = lpContext->status; // status of Link
		logger.logMsg(TRACE_FLAG, 0, 
											"INGwSmWrapper::Link status is <%d>",lpContext->status);
    if(lpContext->status == 0)
      (**resp).reasonStr = ((char *)"Inactive");
    else if(lpContext->status == 1)
      (**resp).reasonStr = ((char *)"Connecting");
    else if(lpContext->status == 2)
      (**resp).reasonStr = ((char *)"Active");
    else if(lpContext->status == 3)
      (**resp).reasonStr = ((char *)"Failed");
    else if(lpContext->status == 4)
      (**resp).reasonStr = ((char *)"Waiting for Connection");
    else if(lpContext->status == 5)
      (**resp).reasonStr = ((char *)"Suspended");

		logger.logMsg(TRACE_FLAG, 0, 
											"INGwSmWrapper::Reason string is <%s>",(**resp).reasonStr);
		(**resp).status     = 0;
		(**resp).reason     = lpContext->status;
		(**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
	}
	else 
	{
		(**resp).procId     = SFndProcId();
		(**resp).status     = FAILURE_RESPONSE;
		(**resp).reason     = FAILURE_REASON;
		(**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
		(**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliStatusLink()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliStatusLnkSet()
*
*     Desc:  LinkSet Status
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliStatusLnkSet(LinkSetStatus *apParm, Ss7SigtranStackResp **resp, 
																	vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliStatusLnkSet()");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      =  procIdList.size();
  int liRequestId = getContextId();

  //for(int i=0;i<numProc;i++) 
	//{
		/*
		* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
    INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending = 1;

    qMsg->mSrc = BP_AIN_SM_SRC_EMS;

    qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    cmMemcpy((U8*)&(qMsg->t.stackData.req.u.lnkStatus),
						(U8*)apParm,sizeof(LinkSetStatus));

     qMsg->t.stackData.stackLayer = BP_AIN_SM_MTP3_LAYER;
     qMsg->t.stackData.cmdType = STA_LINKSET;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
      logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete qMsg;
       return -1;
     }
  //} //for loop

  INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;
 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

    }
    else
    {
      liResult = -1;
    }
  }

  if(lpContext != NULL)
	{
		(**resp).procId     = lpContext->resp.procId;
    if(lpContext->status == 0)
      (**resp).reasonStr = ((char *)"Active");
    else if(lpContext->status == 1)
      (**resp).reasonStr = ((char *)"Inactive");

		(**resp).status     = 0;
		(**resp).reason     = lpContext->status;
		(**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
	}
	else 
	{
		(**resp).procId     = SFndProcId();
		(**resp).status     = FAILURE_RESPONSE;
		(**resp).reason     = FAILURE_REASON;
		(**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
		(**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }


  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliStatusLnkSet()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliStatusRte()
*
*     Desc:  Route (DPC) Status
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliStatusRte(RouteStatus *apParm, Ss7SigtranStackResp **resp,
                                vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliStatusRte()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size();
  int liRequestId = getContextId();

  	/*
   	* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
    INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending = 1;

    qMsg->mSrc = BP_AIN_SM_SRC_EMS;

    qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    cmMemcpy((U8*)&(qMsg->t.stackData.req.u.dpcStatus),
				    (U8*)apParm,sizeof(RouteStatus));

     qMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     qMsg->t.stackData.cmdType = STA_ROUTE;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
      logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete qMsg;
       return -1;
     }

  INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;
 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

    }
    else
    {
      liResult = -1;
    }
  }

  if(lpContext != NULL)
	{
		(**resp).procId     = lpContext->resp.procId;
    if(lpContext->status == 0)
      (**resp).reasonStr = ((char *)"Accessible");
    else if(lpContext->status == 1)
      (**resp).reasonStr = ((char *)"Inaccessible");
    else if(lpContext->status == 2)
      (**resp).reasonStr = ((char *)"Congested");
    else if(lpContext->status == 3)
      (**resp).reasonStr = ((char *)"User_OutofService");
    else if(lpContext->status == 4)
      (**resp).reasonStr = ((char *)"User_InService");
    else if(lpContext->status == 5)
      (**resp).reasonStr = ((char *)"SP_Restricted");

		(**resp).status     = 0;
		(**resp).reason     = lpContext->status;
		(**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
	}
	else 
	{
		(**resp).procId     = SFndProcId();
		(**resp).status     = FAILURE_RESPONSE;
		(**resp).reason     = FAILURE_REASON;
		(**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
		(**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliStatusRte()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliStatusPs()
*
*     Desc:  PS Status
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliStatusPs(PsStatus *apParm, Ss7SigtranStackResp **resp,
                                        vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliStatusPs()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc 		  =  procIdList.size();
  int liRequestId = getContextId();

  	/*
   	* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
    INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending = 1;

     qMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(qMsg->t.stackData.req.u.ps),(U8*)apParm,sizeof(PsStatus));

     qMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
     qMsg->t.stackData.cmdType = STA_PS;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "In configure, postMsg failed");
       delete qMsg;
       return -1;
     }

  INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;
 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

    }
    else
    {
      liResult = -1;
    }
  }

  if(lpContext != NULL)
	{
		(**resp).procId     = lpContext->resp.procId;
    if(lpContext->status == 0)
      (**resp).reasonStr = ((char *)"In unknown State");
    else if(lpContext->status == 1)
      (**resp).reasonStr = ((char *)"Down");
    else if(lpContext->status == 2)
      (**resp).reasonStr = ((char *)"Inactive");
    else if(lpContext->status == 3)
      (**resp).reasonStr = ((char *)"Active");
    else if(lpContext->status == 4)
      (**resp).reasonStr = ((char *)"Pending");

		(**resp).status     = 0;
		(**resp).reason     = lpContext->status;
		(**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
	}
	else 
	{
		(**resp).procId     = SFndProcId();
		(**resp).status     = FAILURE_RESPONSE;
		(**resp).reason     = FAILURE_REASON;
		(**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
		(**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }


  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliStatusPs()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliStatusPsp()
*
*     Desc:  PSP Status
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliStatusPsp(PspStatus *apParm, 
                          Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliStatusPsp()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult 	  = 0;
  U8 numProc 		  =  procIdList.size();
  int liRequestId = getContextId();

  
  	/*
   	* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
    INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
    mpSmDistributor->ss7SigtranStackRespPending = 1;

    qMsg->mSrc = BP_AIN_SM_SRC_EMS;

    qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    cmMemcpy((U8*)&(qMsg->t.stackData.req.u.psp),(U8*)apParm,sizeof(PspStatus));

    qMsg->t.stackData.stackLayer = BP_AIN_SM_M3U_LAYER;
    qMsg->t.stackData.cmdType = STA_PSP;
    qMsg->t.stackData.procId = SFndProcId();
    qMsg->t.stackData.miRequestId = liRequestId;

    //post the message to the Distributor
    if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0, "cliStatusPsp, postMsg failed");
       delete qMsg;
       return -1;
    }

  INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;
 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      //delete lpContext; // FMR Fix
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
  	logger.logMsg (ERROR_FLAG, 0, 
							"INGwSmWrapper::cliStatusPsp() Failed due to Timedout");
    liResult = -1;
	}

  if(lpContext != NULL)
	{
    int EP[4] = {0,0,0,0},stat[4]={0,0,0,0}, ep_status=0;
    int i=0,j=0,k=0;
    /*** find the EP ids so that status can be shown for each endpoints ***/
    AsSeq* asList = INGwSmBlkConfig::getInstance().getAsList();

    AsSeq::iterator it;
    for(it=asList->begin(); it != asList->end(); ++it) {
      k = 0;
      for(i = 0; i < (*it).nmbPsp ; i++)
      {
        if ((*it).psp[i] == apParm->pspId) {
          for(j=0;j<(*it).pspEpLst[i].nmbEp;j++){
            EP[k] = (*it).pspEpLst[i].endpIds[j];
            //logger.logMsg (TRACE_FLAG, 0, 
						//	"Found EP[%d]--:%d ",k,EP[k] );
            k++;
          }
          break;
        }
      }
    }

    for ( i = 3,j=1 ; i >= 0; i--,j++) {
      ep_status = lpContext->status;
      stat[i] = ((ep_status >> (j * 8 - 8)) & 255);
      //logger.logMsg (TRACE_FLAG, 0, 
				//			"status of EP:%d--:%d ",i,stat[i] );

    }
 
		(**resp).procId     = lpContext->resp.procId;

    char result[60]={'\0',};
    char buffer[20]={'\0',};
    for(i=0; i<4; i++)
    {
      if ( EP[i] != 0 )
      {
        sprintf (buffer, " EP[%d]:",EP[i]);
        strncat(result, buffer, strlen(buffer));
        if(stat[EP[i]] == 0)
          strncat(result,"Unsupported",strlen("Unsupported"));
        else if(stat[EP[i]] == 1)
          strncat(result,"Down",strlen("Down"));
        else if(stat[EP[i]] == 2)
          strncat(result,"Inactive",strlen("Inactive"));
        else if(stat[EP[i]] == 3)
          strncat(result,"Active",strlen("Active"));
      }
    } 
    if((EP[0] == 0) && (EP[1] == 0) && (EP[2]==0) && (EP[3]==0)) 
      strncpy(result,":Partial data configured",strlen(":Partial data configured"));

    (**resp).reasonStr = new char[60];
    memcpy((**resp).reasonStr, result, strlen(result)+1);

		(**resp).status     = 0;
		(**resp).reason     = lpContext->status;
		(**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
	}
	else 
	{
		(**resp).procId     = SFndProcId();
		(**resp).status     = FAILURE_RESPONSE;
		(**resp).reason     = FAILURE_REASON;
		(**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
		(**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliStatusPsp()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliStatusNode()
*
*     Desc:  Status Node
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliStatusNode(NodeStatus *apParm,  vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliStatusNode()");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size(); 
  int liRequestId = getContextId();

  for(int i=0;i<numProc;i++) 
	{
		/*
		* create a new Queue object and used the request Id as
    * the thread id for the current thread. This will be used by Stack
    * Manager to unblock it if needed.
    */
    if (i == 0) {
      mpSmDistributor->ss7SigtranStackRespPending = 1;
    }
    else {
      mpSmDistributor->ss7SigtranStackRespPending++;
    }
		INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

		qMsg->mSrc = BP_AIN_SM_SRC_EMS;

		qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
		cmMemcpy((U8*)&(qMsg->t.stackData.req.u.nodeStatus),
					  (U8*)apParm,sizeof(NodeStatus));
        
     qMsg->t.stackData.stackLayer = BP_AIN_SM_SG_LAYER;

     qMsg->t.stackData.cmdType = STA_NODE;
     qMsg->t.stackData.procId = procIdList[i];
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliStatusNode, postMsg failed");
       delete qMsg;
       return -1;
     }
  }

 //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;

      delete lpContext;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliStatusNode() Failed due to Timedout");
    liResult = -1;
	}

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliStatusNode()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliEnableNode()
*
*     Desc:  Enable node 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliEnableNode(SgNode *enaNode, Ss7SigtranStackResp **resp, 
												         vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliEnableNode");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size();
  int liRequestId = getContextId();

  for(int i=0;i<numProc;i++)
  {
    logger.logMsg(TRACE_FLAG, 0, 
           "cliEnableNode()::ENABLE_NODE SG for ProcId[%d]", procIdList[i]);

    mpSmDistributor->ss7SigtranStackRespPending = 1;

    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
     INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

     enaNode->procId = procIdList[i];
    enaNode->lastProc = TRUE;

    qMsg->mSrc = BP_AIN_SM_SRC_EMS;
    qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    cmMemcpy((U8*)&(qMsg->t.stackData.req.u.sgNode),
					   (U8*)enaNode,sizeof(SgNode));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_SG_LAYER;
     qMsg->t.stackData.cmdType    = ENABLE_NODE;
     qMsg->t.stackData.procId     = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
      logger.logMsg (ERROR_FLAG, 0, "cliEnableNode, postMsg failed");
       delete qMsg;
       return -1;
     }

		INGwSmBlockingContext *lpContext = 0;
  	*resp = new Ss7SigtranStackResp;

		//block the current thread
    if (blockOperation (liRequestId) == BP_AIN_SM_OK)
    {
      if (removeBlockingContext (liRequestId, lpContext))
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
		else
		{
			logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliEnableNode() Failed due to Timedout");
			liResult = -1;
		}

		if(lpContext != NULL)
  	{
    	(**resp).procId     = lpContext->resp.procId;
    	(**resp).status     = lpContext->resp.status;
    	(**resp).reason     = lpContext->resp.reason;
    	(**resp).reasonStr  = lpContext->resp.reasonStr;
    	(**resp).stackLayer = lpContext->resp.stackLayer;
    	delete lpContext;
  	}
  	else
 	 	{
    	(**resp).procId     = SFndProcId();
    	(**resp).status     = FAILURE_RESPONSE;
    	(**resp).reason     = FAILURE_REASON;
    	(**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    	(**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  	}
  } // End for loop

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliEnableNode()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDisableNode()
*
*     Desc:  Disable node 
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDisableNode(SgNode *disNode, vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDisableNode");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult 	  = 0;
  U8 numProc 		  =  procIdList.size();
  int liRequestId = getContextId();

  for(int i=0;i<numProc;i++) 
	{
    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
     logger.logMsg(TRACE_FLAG, 0, 
           "cliDisableNode()::DISABLE_NODE for ProcId[%d]", procIdList[i]);

     mpSmDistributor->ss7SigtranStackRespPending = 1;

     INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

     disNode->procId = procIdList[i];
     if (i == (numProc - 1)) {
       disNode->lastProc = TRUE;
     }
     else {
       disNode->lastProc = FALSE;
     }

     qMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(qMsg->t.stackData.req.u.sgNode),
						 (U8*)disNode,sizeof(SgNode));
     qMsg->t.stackData.stackLayer = BP_AIN_SM_SG_LAYER;
     qMsg->t.stackData.cmdType = DISABLE_NODE;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
       logger.logMsg (ERROR_FLAG, 0, "cliDisableNode, postMsg failed");
       delete qMsg;
       return -1;
     }

     //block the current thread
     if (blockOperation (liRequestId) == BP_AIN_SM_OK)
     {
       INGwSmBlockingContext *lpContext = 0;
       if (removeBlockingContext (liRequestId, lpContext))
       {
         if (lpContext->returnValue != BP_AIN_SM_OK){
           logger.logMsg (ERROR_FLAG, 0,
        	"failed to remove blocking ctx lpContext->returnValue "
					"!= BP_AIN_SM_OK");
           liResult = -1;
         }

         delete lpContext;
       }
       else
       {
         logger.logMsg (ERROR_FLAG, 0,
										"cliDisableNode: failed to remove blocking ctx ");
         liResult = -1;
       }
     }
		 else
		 {
				logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliDisableNode() Failed due to Timedout");
    		liResult = -1;
		 }
  } // End for loop

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDisableNode()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAddGtAddrMap()
*
*     Desc:  Add GT Address Map
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliAddGtAddrMap(AddAddrMapCfg *gtMap,
																Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAddGtAddrMap()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      =  procIdList.size();
  int liRequestId = getContextId();
	int timeOut     = 70;

  for(int i=0;i<numProc;i++) 
	{
  	/*
   	* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
		INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

		if (i == 0) {
      mpSmDistributor->ss7SigtranStackRespPending = 1;
    }
    else {
      mpSmDistributor->ss7SigtranStackRespPending++;
    }

		qMsg->mSrc = BP_AIN_SM_SRC_EMS;

    qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
    cmMemcpy((U8*)&(qMsg->t.stackData.req.u.addAddrMapCfg),
				    (U8*)gtMap,sizeof(AddAddrMapCfg));

     qMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     qMsg->t.stackData.cmdType = ADD_GTADDRMAP;
     qMsg->t.stackData.procId = procIdList[i];
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
      logger.logMsg (ERROR_FLAG, 0, "cliAddGtAddrMap, postMsg failed");
       delete qMsg;
       return -1;
     }
  } // End for loop

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		 logger.logMsg(ERROR_FLAG, 0,
			"INGwSmWrapper::cliAddGtAddrMap() Failed due to Timedout");
    liResult = -1;
	}

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAddGtAddrMap()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliAddGtRule()
*
*     Desc:  Add GT Rule
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliAddGtRule(AddGtRule *gt, 
																Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliAddGtRule()");
  
  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      =  procIdList.size();
  int liRequestId = getContextId();
	int timeOut     = 70;

  for(int i=0;i<numProc;i++) 
	{
  	/*
   	* create a new Queue object and used the request Id as
   	* the thread id for the current thread. This will be used by Stack
   	* Manager to unblock it if needed.
   	*/
		INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

		if (i == 0) {
       mpSmDistributor->ss7SigtranStackRespPending = 1;
		}
		else {
       mpSmDistributor->ss7SigtranStackRespPending++;
		}

		qMsg->mSrc = BP_AIN_SM_SRC_EMS;

		qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
		cmMemcpy((U8*)&(qMsg->t.stackData.req.u.addGtRule),
					  (U8*)gt,sizeof(AddGtRule));

     qMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     qMsg->t.stackData.cmdType = ADD_GTRULE;
     qMsg->t.stackData.procId = procIdList[i];
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
      logger.logMsg (ERROR_FLAG, 0, "cliAddGtRule, postMsg failed");
       delete qMsg;
       return -1;
     }
  }

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId, timeOut) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
	  logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliAddGtRule() Failed due to Timedout");
    liResult = -1;
	}

 	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliAddGtRule()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliDelGtRule()
*
*     Desc:  Delete GT Rule
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/

int INGwSmWrapper::cliDelGtRule(DelGtRule *gt, Ss7SigtranStackResp **resp,
                                vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDelGtRule()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();

	/*
	* create a new Queue object and used the request Id as
  * the thread id for the current thread. This will be used by Stack
  * Manager to unblock it if needed.
 */
 INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

 mpSmDistributor->ss7SigtranStackRespPending = 1;

     qMsg->mSrc = BP_AIN_SM_SRC_EMS;

     qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
     cmMemcpy((U8*)&(qMsg->t.stackData.req.u.delGtRule),(U8*)gt,sizeof(DelGtRule));

     qMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     qMsg->t.stackData.cmdType = DEL_GTRULE;
	qMsg->t.stackData.procId 	 	 = SFndProcId();
	qMsg->t.stackData.miRequestId= liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliDelGtRule, postMsg failed");
       delete qMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

 	//block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		logger.logMsg(ERROR_FLAG, 0,
                      "INGwSmWrapper::cliDelGtRule() Failed due to Timedout");
    liResult = -1;
	}

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDelGtRule()");

  return liResult;
}



/******************************************************************************
*
*     Fun:   cliDelGtAddrMap()
*
*     Desc:  Delete GT Address Map
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDelGtAddrMap(DelAddrMapCfg *gtMap,
											Ss7SigtranStackResp **resp, vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDelGtAddrMap()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size();
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
	INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

	mpSmDistributor->ss7SigtranStackRespPending = 1;

	qMsg->mSrc = BP_AIN_SM_SRC_EMS;

	qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	cmMemcpy((U8*)&(qMsg->t.stackData.req.u.delAddrMapCfg),
				  (U8*)gtMap,sizeof(DelAddrMapCfg));

     qMsg->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
     qMsg->t.stackData.cmdType = DEL_GTADDRMAP;
     qMsg->t.stackData.procId = SFndProcId();
     qMsg->t.stackData.miRequestId = liRequestId;

     //post the message to the Distributor
     if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
     {
		logger.logMsg (ERROR_FLAG, 0, "cliDelGtAddrMap, postMsg failed");
       delete qMsg;
       return -1;
     }

	INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

  //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }
	else
	{
		 logger.logMsg(ERROR_FLAG, 0,
							"INGwSmWrapper::cliDelGtAddrMap() Failed due to Timedout");
    liResult = -1;
	}

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDelGtAddrMap()");
  return liResult;
}

/******************************************************************************
*
*     Fun:   cliGetStats()
*
*     Desc:  Get Statistics for all layers
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliGetStats(Stat* sts)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliGetStats()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

  qMsg->mSrc = BP_AIN_SM_SRC_EMS;

  memcpy((U8*)&(qMsg->t.stackData.req.u.stat),(U8*)sts,sizeof(Stat));
  qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  qMsg->t.stackData.stackLayer = sts->layer;
  qMsg->t.stackData.cmdType = GET_STATS;
  qMsg->t.stackData.procId = INGwSmBlkConfig::getInstance().m_selfProcId;
  qMsg->t.stackData.miRequestId = liRequestId;

  //post the message to the Distributor
  if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0, "cliGetStats, postMsg failed");
    delete qMsg;
    return -1;
  }

    logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliGetStats()");
    return liResult;
  }

  /******************************************************************************
*
*     Fun:   cliDisSsnSap()
*
*     Desc:  Disable TCAP upper/Lower and SCCP Upper sap
*
*     Notes: None
*
*     File:  INGwSmWrapper.C
*
*******************************************************************************/
int INGwSmWrapper::cliDisSsnSap(DisSap *sapIdList, Ss7SigtranStackResp **resp, 
                        vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cliDisSsnSap()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  int liRequestId = getContextId();

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
  mpSmDistributor->ss7SigtranStackRespPending = 1;

  qMsg->mSrc = BP_AIN_SM_SRC_EMS;

  memcpy((U8*)&(qMsg->t.stackData.req.u.disableSap),(U8*)sapIdList,sizeof(DisSap));
  qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  qMsg->t.stackData.stackLayer = BP_AIN_SM_TCA_LAYER;
  qMsg->t.stackData.subOpr = BP_AIN_SM_TCA_LSAP;
  qMsg->t.stackData.cmdType = DISABLE_SAPS;
  qMsg->t.stackData.procId = SFndProcId();
  qMsg->t.stackData.miRequestId = liRequestId;

  //post the message to the Distributor
  if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0, "cliDisSsnSap, postMsg failed");
    delete qMsg;
    return -1;
  }

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  INGwSmQueueMsg *qMsg1 = new INGwSmQueueMsg;
  mpSmDistributor->ss7SigtranStackRespPending++;
  qMsg1->mSrc = BP_AIN_SM_SRC_EMS;

  memcpy((U8*)&(qMsg1->t.stackData.req.u.disableSap),(U8*)sapIdList,sizeof(DisSap));
  qMsg1->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  qMsg1->t.stackData.stackLayer = BP_AIN_SM_TCA_LAYER;
  qMsg1->t.stackData.subOpr = BP_AIN_SM_TCA_USAP;
  qMsg1->t.stackData.cmdType = DISABLE_SAPS;
  qMsg1->t.stackData.procId = SFndProcId();
  qMsg1->t.stackData.miRequestId = liRequestId;

  //post the message to the Distributor
  if (mpSmDistributor->postMsg (qMsg1) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0, "cliDisSsnSap, postMsg failed");
    delete qMsg1;
    return -1;
  }


  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */
  INGwSmQueueMsg *qMsg2 = new INGwSmQueueMsg;
  mpSmDistributor->ss7SigtranStackRespPending++;
  qMsg2->mSrc = BP_AIN_SM_SRC_EMS;

  memcpy((U8*)&(qMsg2->t.stackData.req.u.disableSap),(U8*)sapIdList,sizeof(DisSap));
  qMsg2->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  qMsg2->t.stackData.stackLayer = BP_AIN_SM_SCC_LAYER;
  qMsg2->t.stackData.cmdType = DISABLE_SAPS;
  qMsg2->t.stackData.procId = SFndProcId();
  qMsg2->t.stackData.miRequestId = liRequestId;

  //post the message to the Distributor
  if (mpSmDistributor->postMsg (qMsg2) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0, "cliDisSsnSap, postMsg failed");
    delete qMsg2;
    return -1;
  }

  INGwSmBlockingContext *lpContext = 0;
  *resp = new Ss7SigtranStackResp;

  //block the current thread
  if (blockOperation (liRequestId) == BP_AIN_SM_OK)
  {
    if (removeBlockingContext (liRequestId, lpContext))
    {
      if (lpContext->returnValue != BP_AIN_SM_OK)
        liResult = -1;
    }
    else
    {
      liResult = -1;
    }
  }

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }


  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cliDisSsnSap()");
  return liResult;
}



  /******************************************************************************
   *
   *     Fun:   addDebugState()
   *
   *     Desc:  This function adds the Information related to Debug level
   *
   *     Notes: None
   *
   *     File:  INGwSmWrapper.C
   *
   ******************************************************************************/
      int
  INGwSmWrapper::addDebugInfo (int layer, int state )
  {
      logger.logMsg (TRACE_FLAG, 0,
          "addDebugInfo(), Entering INGwSmWrapper::addDebugInfo for layer <%d>", layer);

      INGwSmDebugMap::iterator iter = meDebug.find (layer);

      if (iter != meDebug.end ())
      {
        logger.logMsg (ERROR_FLAG, 0,
              "addDebugInfo(), Layer <%d> already exists in the Debug Map", layer);

        meDebug [layer] = state;
        logger.logMsg (TRACE_FLAG, 0,
      "addDebugInfo(), Leaving INGwSmWrapper::addDebugInfo layer <%d> state <%d>",		layer,iter->second);
        return BP_AIN_SM_OK;
      }

      meDebug [layer] = state; //lpState;

      logger.logMsg (TRACE_FLAG, 0,
      "addDebugInfo(), Leaving INGwSmWrapper::addDebugInfo layer <%d> state <%d>",	layer, meDebug [layer]);
      return BP_AIN_SM_OK;
  }


  /******************************************************************************
   *
   *     Fun:   getDebugState()
   *
   *     Desc:  This function returns the Debug State for a layer
   *
   *     Notes: None
   *
   *     File:  INGwSmWrapper.C
   *
   *******************************************************************************/
      int 
  INGwSmWrapper::getDebugState (int layer)
  {
      logger.logMsg (TRACE_FLAG, 0,
          "getDebugState(), Entering INGwSmWrapper::getDebugState for layer <%d>", layer);


      INGwSmDebugMap::iterator iter = meDebug.find (layer);

      if (iter == meDebug.end ())
      {
        logger.logMsg (ERROR_FLAG, 0,
              "getDebugState(), Layer <%d> Could not be located in the Debug State Map", layer);

        return BP_AIN_SM_FAIL;
      }


      logger.logMsg (TRACE_FLAG, 0,
          "getDebugState(), Layer <%d> is found in map with state <%d>", layer,iter->second); 

      logger.logMsg (TRACE_FLAG, 0,
          "getDebugState(), Leaving INGwSmWrapper::getDebugState");

      return iter->second;
  }




  /******************************************************************************
   *
   *     Fun:   getPspMap()
   *
   *     Desc:  This function returns the PSP MAP
   *
   *     Notes: None
   *
   *     File:  INGwSmWrapper.C
   *
   *******************************************************************************/
int       
  INGwSmWrapper::getPspMap (int id)
  {
      logger.logMsg (TRACE_FLAG, 0,
          "getPspMap(), Entering INGwSmWrapper::getPspMap id<%d> ",id);

      INGwSmPspMap::iterator iter = mePspState.find(id);
      if(iter == mePspState.end()) {
        logger.logMsg (TRACE_FLAG, 0,
          "getPspMap(), Leaving INGwSmWrapper:: state not found for id<%d>",id);
        mePspState[id].state = ASSOC_STATE_UNKNOWN;

        return ASSOC_STATE_UNKNOWN;
      }
      logger.logMsg (TRACE_FLAG, 0,
          "getPspMap(), Leaving INGwSmWrapper::getPspMap state <%d>",(iter->second).state);

      return((iter->second).state);

  }

bool       
INGwSmWrapper::getPspMapAlarmAssFail (int id)
{
      logger.logMsg (TRACE_FLAG, 0,
          "Entering INGwSmWrapper::getPspMapAlarmAssFail id<%d> ",id);

      INGwSmPspMap::iterator iter = mePspState.find(id);
      if(iter == mePspState.end()) {
        logger.logMsg (TRACE_FLAG, 0,
          "getPspMapAlarmAssFail(), Leaving INGwSmWrapper:: state not found for id<%d>",id);
        (iter->second).assFailAlarmEms = false;

        return ASSOC_STATE_UNKNOWN;
      }
      logger.logMsg (TRACE_FLAG, 0,
          "Leaving INGwSmWrapper::getPspMapAlarmAssFail state <%d>",(iter->second).assFailAlarmEms);

      return((iter->second).assFailAlarmEms);

}


bool       
INGwSmWrapper::getPspMapAlarmSct (int id)
{
      logger.logMsg (TRACE_FLAG, 0,
          "Entering INGwSmWrapper::getPspMapAlarmSct id<%d> ",id);

      INGwSmPspMap::iterator iter = mePspState.find(id);
      if(iter == mePspState.end()) {
        logger.logMsg (TRACE_FLAG, 0,
          "getPspMapAlarmAssFail(), Leaving INGwSmWrapper:: state not found for id<%d>",id);
        (iter->second).sctAlarmEms = false;

        return ASSOC_STATE_UNKNOWN;
      }
      logger.logMsg (TRACE_FLAG, 0,
          "Leaving INGwSmWrapper::getPspMapAlarmSct state <%d>",(iter->second).sctAlarmEms);

      return((iter->second).sctAlarmEms);

}


int 
INGwSmWrapper::setPspMap (int id, int state)
{
  logger.logMsg (TRACE_FLAG, 0,
    "setPspMap(), Entering INGwSmWrapper::setPspMap, id:%d state:%d ",id,state);

  INGwSmPspMap::iterator iter = mePspState.find(id);
  if(mePspState.end() != iter){
    (iter->second).state = state;
  }
  else{
    logger.logMsg (TRACE_FLAG, 0,
    "setPspMap(), Not setting the value since id not found in mePspState");
    //mePspState[id] = state;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "setPspMap(), Leaving INGwSmWrapper::setPspMap");

  return BP_AIN_SM_OK; 
}

int 
INGwSmWrapper::setPspMapAlarmSct (int id, bool alrm)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper:: setPspMapAlarmSct, id:%d sctAlarmEms:%d ",id,alrm);

  INGwSmPspMap::iterator iter = mePspState.find(id);
  if(mePspState.end() != iter){
    (iter->second).sctAlarmEms = alrm;
  }
  else{
    logger.logMsg (TRACE_FLAG, 0,
    "setPspMapAlarmSct(), Not setting the value since id not found in mePspState");
    //mePspState[id] = state;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::setPspMapAlarmSct");

  return BP_AIN_SM_OK; 
}

int 
INGwSmWrapper::setPspMapAlarmAssFail (int id, bool alrm)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper:: setPspMapAlarmAssFail(), id:%d sctAlarmEms:%d ",id,alrm);

  INGwSmPspMap::iterator iter = mePspState.find(id);
  if(mePspState.end() != iter){
    (iter->second).assFailAlarmEms = alrm;
  }
  else{
    logger.logMsg (TRACE_FLAG, 0,
    "setPspMapAlarmAssFail(), Not setting the value since id not found in mePspState");
    //mePspState[id] = state;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::setPspMapAlarmAssFail");

  return BP_AIN_SM_OK; 
}


int       
  INGwSmWrapper::getEpMap (int id)
  {
      logger.logMsg (TRACE_FLAG, 0,
          "getEpMap(), Entering INGwSmWrapper::getEpMap id<%d> ",id);

      INGwSmEpMap::iterator iter = meEpState.find(id);
      if(iter == meEpState.end()) {
        logger.logMsg (TRACE_FLAG, 0,
          "getEpMap(), Leaving INGwSmWrapper:: state not found for id<%d>",id);
        meEpState[id] = EP_UNKNOWN;

        return EP_UNKNOWN;
      }
      logger.logMsg (TRACE_FLAG, 0,
          "getEpMap(), Leaving INGwSmWrapper::getEpMap state <%d>",iter->second);

      return(iter->second);

  }


int 
INGwSmWrapper::setEpMap (int id, int state)
{
  logger.logMsg (TRACE_FLAG, 0,
    "setEpMap(), Entering INGwSmWrapper::setEpMap, id:%d state:%d ",id,state);

  INGwSmEpMap::iterator iter = meEpState.find(id);
  if(meEpState.end() != iter){
    iter->second = state;
  }
  else{
    logger.logMsg (TRACE_FLAG, 0,
    "setEpMap(), setting the state <%d> for id <%d>",state, id);
    meEpState[id] = state;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "setEpMap(), Leaving INGwSmWrapper::setEpMap");

  return BP_AIN_SM_OK; 
}



  /******************************************************************************
  *
  *     Fun:   proxyRun()
  *
  *     Desc:  main worker function to poll over the Queue
  *
  *     Notes: None
  *
  *     File:  INGwSmWrapper.C
  *
  *******************************************************************************/
  void
  INGwSmWrapper::proxyRun ()
  {
    logger.logMsg (TRACE_FLAG, 0,
      "Entering INGwSmWrapper::proxyRun");

    int dequeCount = CONFIG_QUEUE_LOW_MARK;
    mRunStatus = true;
    while(true == mRunStatus)
    {
      logger.logMsg (VERBOSE_FLAG, 0, 
        "INGwSmWrapper:proxyRun Listening for new event");

      QueueData* pQueueData = new QueueData;

      int eventCount = m_configQ.eventDequeue(pQueueData,
                                   CONFIG_QUEUE_DEQUEUE_TIME, true);

      logger.logMsg (VERBOSE_FLAG, 0, 
      "INGwSmWrapper::Dequeued events[%d] ", eventCount);

      if (eventCount == 1)
      {
        INGwSmConfigQMsg* pWork = static_cast<INGwSmConfigQMsg*>(pQueueData->data);
        if(NULL != pWork)
        {
          logger.logMsg (VERBOSE_FLAG, 0, 
            "INGwSmWrapper::Dequeued eventsource[%d]", pWork->src);

          switch (pWork->src)
          {
            case BP_AIN_SM_SRC_CCM:
            {
              Ss7SigtranStackResp *resp = NULL;	
              if(pWork->req != NULL) {
                processEmsReq(pWork->req, resp, pWork->from);
                delete pWork->req;

                if(resp != NULL)
                  delete resp;
              }
            }
            break;
            case BP_AIN_SM_STACK_CONFIG_START:
            {
              // Only Primary need to convey this message
              // to peer.
              if(INGwTcapProvider::getInstance().myRole() == TCAP_PRIMARY)
              {
      						INGwIfrMgrAlarmMgr::getInstance().logAlarm
									(INGwIfrMgrAlarmMgr::NON_GAPPED, __FILE__, __LINE__,
                   INC_SM_ALM_STK_CONFIG_INPGRS, "Stack Config", 0, "INC_SM_ALM_STK_CONFIG_INPGRS");

                INGwFtPktStackConfig lStkConfig;
                lStkConfig.initialize(1, 				// STACK_CONFIG_INITIATED
                            INGwSmBlkConfig::getInstance().m_selfId,
                            INGwSmBlkConfig::getInstance().m_peerId);

                int ryStatus = INGwSmBlkConfig::getInstance().getRelayStatus();
                if ((pWork->from != 3) && (ryStatus)) {
                  INGwIfrMgrManager::getInstance().sendMsgToINGW(&lStkConfig);
                  logger.logMsg (ALWAYS_FLAG, 0, 
                         "proxyRun(), Sending BP_AIN_SM_STACK_CONFIG_START to peer,Role:ACTV");
                }
                else {
                  logger.logMsg (ALWAYS_FLAG, 0, "proxyRun(), "
                         "Not sending BP_AIN_SM_STACK_CONFIG_START, since "
                         "From<%d> RelayStatus<%d>", pWork->from, ryStatus);
                }
              }
              else { // Will receive this message if secondary
                
                logger.logMsg (ALWAYS_FLAG, 0, "proxyRun(): m_selfState "
                       "OldVal<%d> NewVal CONFIG_INPROGRESS",
                       INGwSmBlkConfig::getInstance().m_selfState);
                INGwSmBlkConfig::getInstance().m_selfState = 
                                        INGwSmBlkConfig::CONFIG_INPROGRESS;
                logger.logMsg (ALWAYS_FLAG, 0, 
                       "proxyRun(), Received BP_AIN_SM_STACK_CONFIG_START, selfRole:SBY");
              }
            }
            break;

            case BP_AIN_SM_STACK_CONFIG_END:
              if(INGwTcapProvider::getInstance().myRole() == TCAP_PRIMARY)
              {
                setAllowCfgFromEms(true, 
                        (char*)"INGwSmWrapper::proxyRun()", __LINE__);

                INGwFtPktStackConfig lStkConfig;
                lStkConfig.initialize(2,				// STACK_CONFIG_END
                            INGwSmBlkConfig::getInstance().m_selfId,
                            INGwSmBlkConfig::getInstance().m_peerId);

                int ryStatus = INGwSmBlkConfig::getInstance().getRelayStatus();
                if ((pWork->from != 3) && (ryStatus)) {
                  INGwIfrMgrManager::getInstance().sendMsgToINGW(&lStkConfig);
                  logger.logMsg (ALWAYS_FLAG, 0, "proxyRun(), "
                       "Sending BP_AIN_SM_STACK_CONFIG_END to peer,Role:ACTV");
                }
                else {
                  logger.logMsg (ALWAYS_FLAG, 0, "proxyRun(), "
                         "Not sending BP_AIN_SM_STACK_CONFIG_END, since "
                         "From<%d> RelayStatus<%d>", pWork->from, ryStatus);
                }

                if(pWork->from == 2)
                {
                  INGwSmBlkConfig::getInstance().updatePeerEnabled(true);
                }

      					INGwIfrMgrAlarmMgr::getInstance().logAlarm
								(INGwIfrMgrAlarmMgr::NON_GAPPED, __FILE__, __LINE__,
                INC_SM_ALM_STK_CONFIG_COMP, "Stack Config", 0, "INC_SM_ALM_STK_CONFIG_COMP");

              }
              else { // Will receive this message if secondary
                logger.logMsg (ALWAYS_FLAG, 0, "proxyRun(): m_selfState "
                       "OldVal<%d> NewVal INITIALIZED",
                       INGwSmBlkConfig::getInstance().m_selfState);
                INGwSmBlkConfig::getInstance().m_selfState = 
                                            INGwSmBlkConfig::INITIALIZED;
                INGwSmBlkConfig::getInstance().updatePeerEnabled(true);
                logger.logMsg (ALWAYS_FLAG, 0, "proxyRun(), "
                       "Received BP_AIN_SM_STACK_CONFIG_END, selfRole:SBY");
              }
            break;

            case BP_AIN_SM_PEER_UP:
            {
              g_dumpMsg("SmWrapper", __LINE__, "PEER UP rxed starts");
              logger.logMsg(ALWAYS_FLAG, 0, "PEER UP rxed");
              INGwSmBlkConfig::getInstance().peerUp();	
              INGwSmBlkConfig::getInstance().m_toggleFlag = true;
              g_dumpMsg("SmWrapper", __LINE__, "PEER UP rxed ends");
            }
            break;

            case BP_AIN_SM_PEER_DOWN:
            {
              g_dumpMsg("SmWrapper", __LINE__, "PEER DOWN rxed starts");
              logger.logMsg(ALWAYS_FLAG, 0, "PEER DOWN rxed");

              if(INGwSmBlkConfig::getInstance().isPeerEnabled() &&
                INGwSmBlkConfig::getInstance().m_toggleFlag == true)
                handlePeerFailure();

              INGwSmBlkConfig::getInstance().peerDown();	
              g_dumpMsg("SmWrapper", __LINE__, "PEER DOWN rxed ends");
            }
            break;

            case BP_AIN_SM_SRC_PEER_INC:
            {
              if(pWork->msg != NULL)
              {
                INGwSmBlkConfig::getInstance().updateNode(pWork->msg);	
                delete pWork->msg;
              }
            }
            break;

            default: 
            {
              logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::proxyRun");
            }
          }
          
          delete pWork; 
	  pWork=NULL;
        }
      }
      else {
        logger.logMsg (ERROR_FLAG, 0, 
        "INGwSmWrapper:proxyRun dequed multiple Evt[%d]",
        eventCount);
      }

      delete pQueueData;
    }

    logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::proxyRun");
    return;
  }

  /******************************************************************************
  *
  *     Fun:   postMsg()
  *
  *     Desc:  method for posting SS7 config message, sent from INGwInfraManager
  *
  *     Notes: None
  *
  *     File:  INGwSmWrapper.C
  *
  *******************************************************************************/
  int 
  INGwSmWrapper::postMsg(INGwSmConfigQMsg *msg, bool chkFlag )
  {
    logger.logMsg (TRACE_FLAG, 0,
      "Entering INGwSmWrapper::postMsg");

    if (mRunStatus == false)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "The SmWrapper has been stopped. Post Msg failed.");
      return BP_AIN_SM_FAIL;
    }

    QueueData lData;
    lData.data = static_cast<void*>(msg);

    if (lData.data == 0)
    {
     logger.logMsg (ERROR_FLAG, 0, "Static cast failed for QueueMsg ");
      return BP_AIN_SM_FAIL;
    }

    if (chkFlag)
    {
      if(m_configQ.queueSize() > CONFIG_QUEUE_LOW_MARK)
      {
        logger.logMsg (ERROR_FLAG, 0, "Enqueing Data Failed");
        return BP_AIN_SM_FAIL;
      }
    }

    if (m_configQ.eventEnqueue (&lData) == 0)
    {
      logger.logMsg (ERROR_FLAG, 0, "Enqueing Data Failed");
      return BP_AIN_SM_FAIL;
    }

    logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::postMsg");
  }

  bool 
  INGwSmWrapper::cleanUpOnTimeOut(int requestId) 
  {
    bool retVal = true;
    logger.logMsg (TRACE_FLAG, 0, "Entering INGwSmWrapper::cleanUpOnTimeOut");

    // cleaup blocking context
    INGwSmBlockingContext *lpContext = 0;
    if (removeBlockingContext (requestId, lpContext) == BP_AIN_SM_OK)
            delete lpContext;

    mUnblockedRequestId = -1;

    logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmWrapper::cleanUpOnTimeOut");
    return false;
  }

  int 
  INGwSmWrapper::getContextId()
  {
    return mpContext++;
}

void INGwSmWrapper::setAllowCfgFromEms(bool val, char * aMsg, int aLine){
  logger.logMsg (TRACE_FLAG, 0, 
         "Entering setAllowCfgFromEms(). From %s:%d", aMsg, aLine);
  pthread_rwlock_wrlock (&m_AllowCfgEmsLock);
  logger.logMsg (ALWAYS_FLAG, 0, 
         "setAllowCfgFromEms():: m_allowCfgFromEms: %d", val);

  m_allowCfgFromEms = val;

  pthread_rwlock_unlock (&m_AllowCfgEmsLock);
  logger.logMsg (TRACE_FLAG, 0, "Leaving setAllowCfgFromEms()");
}

bool INGwSmWrapper::getAllowCfgFromEms(char * aMsg, int aLine){
  logger.logMsg (TRACE_FLAG, 0,
         "Entering getAllowCfgFromEms(). From %s:%d", aMsg, aLine);
  bool flag = false;
  pthread_rwlock_rdlock (&m_AllowCfgEmsLock);

  flag = m_allowCfgFromEms;

  logger.logMsg (VERBOSE_FLAG, 0, 
         "getAllowCfgFromEms():: m_allowCfgFromEms: %d", 
         flag);

  pthread_rwlock_unlock (&m_AllowCfgEmsLock);
  logger.logMsg (TRACE_FLAG, 0, "Leaving getAllowCfgFromEms()");
  return flag;
}

bool INGwSmWrapper::cliShutdownLayers(ShutDownLayers *shutdownLyrs,
                                     Ss7SigtranStackResp **resp, 
                                     vector<int> &procIdList)
{
  logger.logMsg (TRACE_FLAG, 0, "Entering cliShutdownLayers()");

  if (!mpSmDistributor)
  {
    logger.logMsg (ERROR_FLAG, 0, "The Distributor object is NULL");
    return -1;
  }

  int liResult    = 0;
  U8 numProc      = procIdList.size();
  int liRequestId = getContextId();
	INGwSmBlockingContext *lpContext = 0;

  for (int i=0; i < shutdownLyrs->nmbEntity; i++) {
	  logger.logMsg (TRACE_FLAG, 0, "cliShutdownLayers():: Layer<%d>",
                   shutdownLyrs->entId[i]);
    /*
     * create a new Queue object and used the request Id as
     * the thread id for the current thread. This will be used by Stack
     * Manager to unblock it if needed.
     */
	  INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;

	  mpSmDistributor->ss7SigtranStackRespPending = 1;

	  qMsg->mSrc = BP_AIN_SM_SRC_EMS;

	  qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
	  cmMemcpy((U8*)&(qMsg->t.stackData.req.u.shutDownLayers),
	  			   (U8*)shutdownLyrs,sizeof(ShutDownLayers));

    qMsg->t.stackData.stackLayer = shutdownLyrs->entId[i];
    qMsg->t.stackData.cmdType = SHUTDOWN_LAYERS;
    qMsg->t.stackData.procId = SFndProcId();
    qMsg->t.stackData.miRequestId = liRequestId;

    //post the message to the Distributor
    if (mpSmDistributor->postMsg (qMsg) != BP_AIN_SM_OK)
    {
	    logger.logMsg (ERROR_FLAG, 0, "cliShutdownLayers():: postMsg failed");
      delete qMsg;
      return -1;
    }


    *resp = new Ss7SigtranStackResp;

    //block the current thread
    if (blockOperation (liRequestId) == BP_AIN_SM_OK)
    {
      if (removeBlockingContext (liRequestId, lpContext))
      {
        if (lpContext->returnValue != BP_AIN_SM_OK)
          liResult = -1;
      }
      else
      {
        liResult = -1;
      }
    }
	  else
	  {
	  	 logger.logMsg(ERROR_FLAG, 0,
	  						     "cliShutdownLayers():: Failed due to Timedout");
       liResult = -1;
	  }

    if(liResult == -1) {
      break;
    }
  } // End for loop

	if(lpContext != NULL)
  {
    (**resp).procId     = lpContext->resp.procId;
    (**resp).status     = lpContext->resp.status;
    (**resp).reason     = lpContext->resp.reason;
    (**resp).reasonStr  = lpContext->resp.reasonStr;
    (**resp).stackLayer = lpContext->resp.stackLayer;
    delete lpContext;
  }
  else
  {
    (**resp).procId     = SFndProcId();
    (**resp).status     = FAILURE_RESPONSE;
    (**resp).reason     = FAILURE_REASON;
    (**resp).reasonStr  = (char*)FAILURE_STACK_RESP;
    (**resp).stackLayer = (char*)FAILURE_STACK_LAYER;
  }
  
  logger.logMsg (TRACE_FLAG, 0, "Leaving cliShutdownLayers()");
  return liResult;
}


int INGwSmWrapper::sendAspUpCmd(int pspid, int sctsuid)
{

  logger.logMsg (ALWAYS_FLAG, 0,
    "Entering INGwSmWrapper::sendAspUpCmd(), pspid:%d, sctSuId:%d",pspid,sctsuid);

    Ss7SigtranSubsReq *req = new Ss7SigtranSubsReq;
    memset ((void *)req, 0, sizeof(Ss7SigtranSubsReq));

    vector<int> lprocIdList;
  
    req->u.m3uaAspUp.pspId = pspid;
    req->u.m3uaAspUp.m3uaLsapId = sctsuid;

    lprocIdList.push_back(INGwSmBlkConfig::getInstance().getAssocProcId_Aspup(req->u.m3uaAspUp));
	  req->cmd_type  = ASP_UP;
    INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = lprocIdList;
    lqueueMsg->from       = 2;

    logger.logMsg (ALWAYS_FLAG, 0, 
	    "sendAspUpCmd()::Posting ASP_UP with  "
      "pspId <%d>, m3uaLsap <%d>",
      lqueueMsg->req->u.m3uaAspUp.pspId,
      lqueueMsg->req->u.m3uaAspUp.m3uaLsapId );

    postMsg(lqueueMsg,true);
    logger.logMsg (VERBOSE_FLAG, 0,
    "Leaving INGwSmWrapper::sendAspUpCmd()");

  return 1;
}
void INGwSmWrapper::setM3uaFlag()
{
  logger.logMsg (ERROR_FLAG, 0,
    "INGwSmWrapper::setM3uaFlag(), setting disM3uaFlag true (ASP Down alarm received)");
  disM3uaFlag = true;
}

void INGwSmWrapper::getSwtchFromLocalSSN(S16 *ssnSwtch)
{
  *ssnSwtch = 0;
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmWrapper::getSwtchFromLocalSSN()");
  LocalSsnSeq* ssnList = INGwSmBlkConfig::getInstance().getLocalSsnList();
  LocalSsnSeq::iterator it;
  //for(it=ssnList->begin(); it!=ssnList->end(); ++it) {
    logger.logMsg (TRACE_FLAG, 0,
      "Entering in for loop,getSwtchFromLocalSSN" );
    it=ssnList->begin();
    *ssnSwtch = (*it).swtch;
    logger.logMsg (TRACE_FLAG, 0,
      "before break ::getSwtchFromLocalSSN():%x",*ssnSwtch);
  //  break;
  //}
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmWrapper::getSwtchFromLocalSSN():%x",*ssnSwtch);
}
