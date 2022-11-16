//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

/************************************************************************
   Name:   INAP Stack Manager Distributor - Implementation
 
   Type:   C file
 
   Desc:   Distributor Implementation

   File:   INGwSmDistributor.C

   Sid:  INGwSmDistributor.C 0  -  03/27/03 

   Prg:  gs,bd

************************************************************************/

#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
  #include "envopt.h"
#ifndef __CCPU_CPLUSPLUS
}
#endif

#include "INGwStackManager/INGwSmDistributor.h"
#include "INGwStackManager/INGwSmRepository.h"
#include "INGwStackManager/INGwSmRequestTable.h"
#include "INGwStackManager/INGwSmStsHdlr.h"
#include "INGwStackManager/INGwSmAlmHdlr.h"
#include "INGwStackManager/INGwSmBlkConfig.h"
#include "INGwStackManager/INGwSmAdaptor.h"
//#include "ccm/BpCallController.h"

/*
 * Include the header files for the osCp/SsOs
 */
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
  #include "ss_queue.h"
  #include "ss_queue.x"
  #include "ss_task.h"
  #include "ss_task.x"
  #include "ss_timer.x"
  #include "ss_msg.h"
  #include "ss_msg.x"
  #include "ss_mem.h"
  #include "ss_mem.x"
  #include "ss_gen.h"
  #include "ss_drvr.x"
  #include "ss_gen.x"
#ifndef __CCPU_CPLUSPLUS
}
#endif

#if (defined (_BP_AIN_PROVIDER_) && !defined (STUBBED))
#include "INGwTcapProvider/INGwTcapProvider.h"
#else
class INGwTcapProvider
{
};
#endif

extern U16 selfProcId;
extern int tcapLoDlgId;
extern int tcapHiDlgId;

using namespace std;

/*
 * Define the Queue Lower Water Mark
 */
#define SM_QUEUE_LOW_MARK             50
#define BP_AIN_SM_DEQUEUE_TIME        0     /* keep blocking till a message is received */

/*
 * Forward declaration of the stack functions. Will get resolved while linking
 */
#ifdef __cplusplus
#ifndef __CCPU_CPLUSPLUS
extern "C" {
#endif
#endif
EXTERN S16 SInit();
EXTERN S16 ssMain(int argc, char **argv);
EXTERN S16 ccActvInit ARGS((Ent ent, Inst inst, Region region, Reason reason));
EXTERN S16 ccActvTskNew  ARGS((Pst *pst, Buffer *mBuf));
EXTERN S16 spActvInit ARGS((Ent ent, Inst inst, Region region, Reason reason));
EXTERN S16 spActvTskNew  ARGS((Pst *pst, Buffer *mBuf));
EXTERN S16 snActvInit ARGS((Ent ent, Inst inst, Region region, Reason reason));
EXTERN S16 snActvTsk  ARGS((Pst *pst, Buffer *mBuf));
EXTERN S16 spActvInit ARGS((Ent ent, Inst inst, Region region, Reason reason));
EXTERN S16 spActvTsk  ARGS((Pst *pst, Buffer *mBuf)); 
EXTERN S16 stActvInit ARGS((Ent ent, Inst inst, Region region, Reason reason));
EXTERN S16 stActvTsk  ARGS((Pst *pst, Buffer *mBuf));
#if 0
EXTERN S16 ieActvInit ARGS((Ent ent, Inst inst, Region region, Reason reason));
EXTERN S16 ieActvTsk  ARGS((Pst *pst, Buffer *mBuf)); 
EXTERN S16 iuActvInit ARGS((Ent ent, Inst inst, Region region, Reason reason));
EXTERN S16 iuActvTsk  ARGS((Pst *pst, Buffer *mBuf));
#endif 
EXTERN S16 nfActvInit ARGS((Ent ent, Inst inst, Region region, Reason reason));
EXTERN S16 nfActvTsk  ARGS((Pst *pst, Buffer *mBuf));
EXTERN S16 itActvInit ARGS((Ent ent, Inst inst, Region region, Reason reason));
EXTERN S16 itActvTsk  ARGS((Pst *pst, Buffer *mBuf));
EXTERN S16 sbActvTsk  ARGS((Pst *pst, Buffer *mBuf));
EXTERN S16 sbActvInit ARGS((Ent ent, Inst inst, Region region, Reason reason));
EXTERN S16 hiActvTsk  ARGS((Pst *pst, Buffer *mBuf));
EXTERN S16 hiActvInit ARGS((Ent ent, Inst inst, Region region, Reason reason));
#if 0
EXTERN S16 hiScanPermTsk ARGS((Pst *tPst, Buffer *mBuf));
#endif
EXTERN S16 l4ActvTsk ARGS((Pst *, Buffer*));
#ifdef SOLARIS
//EXTERN S16 sdActvInit ARGS((Ent entity, Inst inst, Region region, Reason reason));
//EXTERN S16 sdActvTsk ARGS((Pst *pst, Buffer *mBuf));
EXTERN S16 SRegInfoShow(Region region, U32 *availmem);
#endif
S16 tst();
S16 rdConQ(Data data);
EXTERN SsOs osCp;
#ifdef __cplusplus
#ifndef __CCPU_CPLUSPLUS
}
#endif
#endif

extern "C" {
EXTERN S16 sdActvInit ARGS((Ent entity, Inst inst, Region region, Reason reason));
EXTERN S16 sdActvTsk ARGS((Pst *pst, Buffer *mBuf));
}

static int giINGwSmTapaMask;
static int giINGwSmTransportType;
static PAIFS16 gfpInitFunc;
static ActvTsk gfpActvFunc;


/******************************************************************************
*
*     Fun:   INGwSmDistributor()
*
*     Desc:  Default Contructor
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/

int INGwSmDistributor::miAuditCounter = 0;

INGwSmDistributor::INGwSmDistributor(INGwSmWrapper *apSmWrapper,
                                       INGwTcapProvider *apTcapProvider):
mpSmWrapper(apSmWrapper),
mpTcapProvider(apTcapProvider),
mpRepository(0),
miDistTransId (0),
mbIsTimerRunning (false),
miMaxTimer (0),
miQueueId (0),
mbIsPrimary (false),
miChangeState2RunningTransId (1),
mpSmQueue(true, SM_QUEUE_LOW_MARK * 10, SM_QUEUE_LOW_MARK)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::INGwSmDistributor");
  pthread_mutex_init (&meQueueLock, 0);
  mpAlmHdlr  = new INGwSmAlmHdlr(this);

  //Create the request table into which the scenario request are
  //stored based on the transaction Id.
  //the same transaction Id will be used for all the Stack Messages sent
  //in that particular scenario.
  mpRequestTable = new INGwSmRequestTable;

  char * configType = getenv ("STACK_CONFIG_TYPE");
  if (configType) {
    mConfigType = atoi(configType);
  }
  else {
    // Default: Stack configuration through EMS
    mConfigType = 1;
  }

  mStkReqRspMap.clear();
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::INGwSmDistributor");
}
int
INGwSmDistributor::updateRspStruct(int transId,StackReqResp *stackReq)
{
   logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::updateRspStruct <%d>", transId);
   mStkReqRspMap.insert(StackReqRespMap::value_type(transId,stackReq));
    return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   ~INGwSmDistributor()
*
*     Desc:  Default destructor
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
INGwSmDistributor::~INGwSmDistributor()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::~INGwSmDistributor");

  //delete the repository
  if (mpRepository)
    delete mpRepository;
  mpRepository = 0;

  //delete the adaptor
  if (mpSmAdaptor)
    delete mpSmAdaptor;
  mpSmAdaptor = 0;


  //delete the Alarm handler
  if (mpAlmHdlr)
    delete mpAlmHdlr;
  mpAlmHdlr = 0;

  if (mpRequestTable)
    delete mpRequestTable;
  mpRequestTable = 0;

  pthread_mutex_destroy (&meQueueLock);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::~INGwSmDistributor");
}

/******************************************************************************
*
*     Fun:   proxyRun()
*
*     Desc:  main worker function to poll over the Queue
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*                                      
*******************************************************************************/
void
INGwSmDistributor::proxyRun ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::proxyRun");

  logger.logMsg (ERROR_FLAG, 0,
    "Thread Id of Worker thread is %d", pthread_self());

  int dequeCount = SM_QUEUE_LOW_MARK;
  mRunStatus = true;

  while(true == mRunStatus) 
  {
    QueueData* pQueueData = new QueueData;

    int eventCount = mpSmQueue.eventDequeue(pQueueData,
                                 BP_AIN_SM_DEQUEUE_TIME, true);

    logger.logMsg (VERBOSE_FLAG, 0,
      "Dequeued <%d>", eventCount);

#ifdef _BP_AIN_SM_DMP_
    if (eventCount == 1)
    {

      /*
       * IMPORTANT : The Work Unit needs to be deleted by the user functions
       */

      INGwSmQueueMsg* pWork = static_cast<INGwSmQueueMsg*>(pQueueData->data);
      if(NULL != pWork)
      {
        logger.logMsg (VERBOSE_FLAG, 0,
          "Dequeued ID = <%d>", pWork->id);
      }
    }
#endif /* _BP_AIN_SM_DMP_ */

    if (eventCount == 1)
    {

      /*
       * IMPORTANT : The Work Unit needs to be deleted by the user functions
       */

      INGwSmQueueMsg* pWork = static_cast<INGwSmQueueMsg*>(pQueueData->data);
      if(NULL != pWork) 
      {
#ifdef  _BP_AIN_SM_DMP_
        dumpQueueMsg (pWork);
#endif /* _BP_AIN_SM_DMP_ */

        switch (pWork->mSrc)
        {
          case BP_AIN_SM_SRC_EMS:
          {
            if (pWork->t.stackData.mOpType == BP_AIN_SM_CCMOP_CONFIGURE)
            {
              handleConfigure (pWork);
            }
            else if (pWork->t.stackData.mOpType == BP_AIN_SM_CCMOP_PEERFAILED)
            {
              handlePeerFailed (pWork);
            }
             break;
          }
          /*
           * Check for the messages received from CCM. The only messages allowed
           * to CCM are OIDChange, Configure, changeState and PeerFailed.
           */ 
          case BP_AIN_SM_SRC_CCM:
          {
            if (pWork->t.ccmOper.mOpType == BP_AIN_SM_CCMOP_OIDCHANGED)
            {
              handleOidChanged (pWork);
            }
            else if (pWork->t.ccmOper.mOpType == BP_AIN_SM_CCMOP_CONFIGURE)
            {
              handleConfigure (pWork);
            }
            else if (pWork->t.ccmOper.mOpType == BP_AIN_SM_CCMOP_CHANGESTATE)
            {
              handleChangeState (pWork);
            }
            else if (pWork->t.ccmOper.mOpType == BP_AIN_SM_CCMOP_ENABLE_NODE)
            {
              handleEnableNode (pWork);
            }
            else 
            {
              logger.logMsg (ERROR_FLAG, 0,
                "Unknown CCM Message type received <%d>",
                pWork->t.ccmOper.mOpType);
              delete pWork;
            }
            break;
          }
          /*
           * handle the messages received from Stack - Confirms, Timeout
           */
          case BP_AIN_SM_SRC_STACK:
          {
            handleResponse (pWork);
            break;
          }
          case BP_AIN_SM_SRC_STACK_ALM:
          {
            handleAlarm (pWork);

            break;
          }
          /*
           * This action can be posted by the Alarm Handler as a new scenario
           * We need to define a new scenario for all actions taken by SM.
           */
          case BP_AIN_SM_SRC_STACK_ALMHDLR:
          {
            handleAlmAction (pWork);
            break;
          }
          default:
          {
            logger.logMsg (ERROR_FLAG, 0,
              "Unknown WorkUnit type received <%d>",
              pWork->mSrc);
            delete pWork;
            break;
          }
        }
        pQueueData->data = NULL;
      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
          "static case failed from void* to INGwSmQueueMsg*");
      }
    }

    delete pQueueData;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmDistributor::proxyRun");
  return;
}

/******************************************************************************
*
*     Fun:   postMsg()
*
*     Desc:  This function is invoked to post the message into the SM Queue.
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*                                      
*******************************************************************************/
int
INGwSmDistributor::postMsg (INGwSmQueueMsg *apQueueMsg, bool chkFlag)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::postMsg");

  pthread_mutex_lock (&meQueueLock);
  apQueueMsg->id = ++miQueueId;
  pthread_mutex_unlock (&meQueueLock);

#ifdef _BP_AIN_SM_DMP_
  dumpQueueMsg (apQueueMsg);
#endif /* _BP_AIN_SM_DMP_ */

  if (mRunStatus == false)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The Distributor has been stopped. Post Msg failed.");
    return BP_AIN_SM_FAIL;
  }

  QueueData lData;
  lData.data = static_cast<void*>(apQueueMsg);

  if (lData.data == 0)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Static cast failed for QueueMsg with ID <%d>", apQueueMsg->id);

    return BP_AIN_SM_FAIL;
  }

  if (chkFlag)
  {
    if(mpSmQueue.queueSize() > SM_QUEUE_LOW_MARK)
    {
      return BP_AIN_SM_FAIL;
    }
  }

  if (mpSmQueue.eventEnqueue (&lData) == 0)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Enqueing failed for ID <%d>", apQueueMsg->id);

    return BP_AIN_SM_FAIL;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::postMsg");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   initialize()
*
*     Desc:  used to initialize the members
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*                                      
*******************************************************************************/
int
INGwSmDistributor::initialize()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::initialize");

  //initialize the repository
  if (mpRepository == 0)
    mpRepository = new INGwSmRepository;

  // to set lo/hi dialogue id range
  StMngmt dummy;
  mpRepository->getStMngmt(BP_AIN_SM_SUBTYPE_GENCFG, dummy);
  INGwTcapIncMsgHandler::getInstance().initCallMap(tcapLoDlgId,tcapHiDlgId);

  //initialize the request Table
  mpRequestTable->initialize();

  //initialize the Adaptor
  mpSmAdaptor = INGwSmAdaptor::getInstance();
  mpSmAdaptor->initialize (this);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::initialize");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   initToRunning()
*
*     Desc:  used to initialize the members into running state
* 
*     Notes: None
*
*     File:  INGwSmDistributor.C
*  
*******************************************************************************/
int   
INGwSmDistributor::initToRunning()
{ 
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::initToRunning");

  bool lbIsPrimary = true;

#if _NOT_INCLUDED_NOW_
  /*
   * Post a message to print the layer versions
   */
  getLayerVersion ();
#endif

  /*
   * Start the timer for Stack Manager based on timer resolution
   */
  startTimer ();


  /*
   * register timer for the Distributor monitoring
   */
  int liDuration = mpRepository->getMonitorTimer();
  miDistTransId = mpRepository->getDistTransId ();

  logger.logMsg (VERBOSE_FLAG, 0,
    "registering distributor timer for TID <%d>, duration <%d>",
    miDistTransId, liDuration);

  registerTimer (liDuration, miDistTransId);

  if (mbIsPrimary != lbIsPrimary &&
      lbIsPrimary == true)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The stack manager is for primary CCM");

    //make the stack manager as primary
    moveToPrimary ();
  }
  else
  {
    logger.logMsg (ERROR_FLAG, 0,
      "The stack manager is for secondary CCM");

    mbIsPrimary = false;
  }

  /*
   * Start monitoring of the resources.
   */
  monitorResource ();


  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::initToRunning");
      
  return BP_AIN_SM_OK;
} 

/******************************************************************************
*
*     Fun:   moveToPrimary()
*     
*     Desc:  move the stack manager to primary state
*     
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::moveToPrimary()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::moveToPrimary");

  mbIsPrimary = true;

  /*
   * initialize the statistics handler
   */
  initStats (); 

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::moveToPrimary");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   changeStateToLoaded()
*
*     Desc:  to be invoked directly by INGwSmWrapper for changeState to LOADED
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*                                      
*******************************************************************************/
int
INGwSmDistributor::changeStateToLoaded (PAIFS16 afpInitFunc, 
                                         ActvTsk afpActvFunc, 
                                         int aiSapId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::changeStateToLoaded");

  if (mpRepository->getSystemTaskMask (giINGwSmTapaMask) == BP_AIN_SM_FAIL)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Error occurred while getting system task mask");

    return BP_AIN_SM_FAIL;
  }

  giINGwSmTransportType = mpRepository->getTransportType();

  gfpInitFunc = afpInitFunc;
  gfpActvFunc = afpActvFunc;

  int argc = 0;
  char **argv;

  argv = (char **) malloc(5 * sizeof(char *));

  for (int i = 0; i < 5; i++)
  {
    argv[i] = (char *) malloc(256 * sizeof(char));
  }

  strcpy((char *)argv[argc++], (char *)"-f ../conf/ccpu_ssi_mem.conf");
  printf("[+INC+] argc<%d> argv[0]<%s>\n", argc, argv[0]); fflush(stdout);

  //if (SInit () != ROK)
  if (ssMain(argc, argv) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SInit FAILED");
    return BP_AIN_SM_FAIL;
  }

  /*
   * Set the file the debug output of the stack
   */
  //use the stdout so that the stream manager can manipulate it
  osCp.dep.fileOutFp = stdout;

  if (osCp.dep.fileOutFp == 0)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to open file pointer for debug output of stack");
  }

  /* INCTBD : Hard coding the self procId, the logic to assign the procId has to be written.*/
  //osCp.procId = 51;


  logger.logMsg (TRACE_FLAG, 0,
    "ProcId = <%d> ",SFndProcId());

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::changeStateToLoaded");

  return BP_AIN_SM_OK;
}

/******************************************************************************
* 
*     Fun:   rdConQ()
*
*     Desc:  read console queue 
*
*     Notes: NOT IMPLEMENTED - DUMMY
*
*     File:  INGwSmDistributor.C
*  
*******************************************************************************/
short
rdConQ (Data data)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering rdConQ");

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving rdConQ");
  return ROK;
}

/******************************************************************************
* 
*     Fun:   tst()
*   
*     Desc:  this will be invoked by the SSI to initialize the TAPA tasks
*   
*     Notes: None
*
*     File:  INGwSmDistributor.C
*  
*******************************************************************************/
short
tst ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering tst <%X>", giINGwSmTapaMask);

  SSTskId tstTskId;
  SSTskId tuclTskId; 
  SSTskId permTskId;

/*DHEERAJ: Creating different system task for RY and MR*/
  SSTskId ryTskId; 
  SSTskId mrTskId;


  SSetProcId(selfProcId);

  /* Create System Task */
  if (SCreateSTsk((SSTskPrior)PRIOR0, &tstTskId) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SCreateSTsk() failed.");
    return RFAILED;
  }

  /* Register SM TAPA Task */
  if (SRegTTsk((Ent)ENTSM, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
               (Prior) BP_AIN_SM_PRIOR,
               iNGwSmActvInit, 
               iNGwSmActvTsk) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SRegTTsk() for SM failed.");
    return RFAILED;
  }
  else
  {
     logger.logMsg (TRACE_FLAG, 0,
        "SRegTTsk successful for ENTSM");
  }

  /* Attach SM TAPA Task */
  if (SAttachTTsk((Ent)ENTSM, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SAttachTTsk() for SM failed.");
    return RFAILED;
  }
     logger.logMsg (TRACE_FLAG, 0,
        "SAttachTTsk successful for ENTSM");

  /* INCTBD */
  if ((giINGwSmTapaMask & BP_AIN_SM_TU_MASK) == BP_AIN_SM_TU_MASK)
  {
    logger.logMsg (VERBOSE_FLAG, 0,
      "Creating a new system task for TU Layer");

    /* Create System Task */
    if (SCreateSTsk((SSTskPrior)BP_AIN_SM_PRIOR, &tstTskId) != ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SCreateSTsk() failed.");
      return RFAILED;
    }
  }

  /* Register TU TAPA Task */
  if (SRegTTsk((Ent)ENTTU, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
      (Prior) BP_AIN_SM_PRIOR,
      (PAIFS16) gfpInitFunc, (ActvTsk) gfpActvFunc) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SRegTTsk() for TU failed.");
    return RFAILED;
  }

    logger.logMsg (TRACE_FLAG, 0,
      "SRegTTsk() for TU successful");

  /* Attach TU TAPA Task */
  if (SAttachTTsk((Ent)ENTTU, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SAttachTTsk() for TU failed.");
    return RFAILED;
  }

    logger.logMsg (TRACE_FLAG, 0,
      "SAttachTTsk for TU successful");

#if 0
  if ((giINGwSmTapaMask & BP_AIN_SM_IU_MASK) == BP_AIN_SM_IU_MASK)
  {
    logger.logMsg (VERBOSE_FLAG, 0,
      "Creating a new system task for IU Layer");

    /* Create System Task */
    if (SCreateSTsk((SSTskPrior)BP_AIN_SM_PRIOR, &tstTskId) != ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SCreateSTsk() failed.");
      return RFAILED;
    }
  }

  /* Register IU TAPA Task */
  if (SRegTTsk((Ent)ENTIU, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
      (Prior) BP_AIN_SM_PRIOR,
      (PAIFS16) gfpInitFunc, (ActvTsk) gfpActvFunc) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SRegTTsk() for IU failed.");
    return RFAILED;
  }

  /* Attach IU TAPA Task */
  if (SAttachTTsk((Ent)ENTIU, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SAttachTTsk() for IU failed.");
    return RFAILED;
  }

  if ((giINGwSmTapaMask & BP_AIN_SM_AIN_MASK) == BP_AIN_SM_AIN_MASK)
  {
    logger.logMsg (VERBOSE_FLAG, 0,
      "Creating a new system task for IE Layer");

    /* Create System Task */
    if (SCreateSTsk((SSTskPrior)BP_AIN_SM_PRIOR, &tstTskId) != ROK)
    {                                    
      logger.logMsg (ERROR_FLAG, 0,
        "SCreateSTsk() failed.");
      return RFAILED;
    }
  }

  /* Register IE TAPA Task */
  if (SRegTTsk((Ent)ENTIE, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
      (Prior) BP_AIN_SM_PRIOR,
      ieActvInit, ieActvTsk) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SRegTTsk() for IE failed.");
    return RFAILED;
  }
  
  /* Attach IE TAPA Task */
  if (SAttachTTsk((Ent)ENTIE, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SAttachTTsk() for IE failed.");
    return RFAILED;
  } 

#endif

  if ((giINGwSmTapaMask & BP_AIN_SM_TCA_MASK) == BP_AIN_SM_TCA_MASK)
  {
    logger.logMsg (VERBOSE_FLAG, 0,
      "Creating a new system task for ST Layer");

    /* Create System Task */
    if (SCreateSTsk((SSTskPrior)BP_AIN_SM_PRIOR, &tstTskId) != ROK)
    {                                    
      logger.logMsg (ERROR_FLAG, 0,
        "SCreateSTsk() failed.");
      return RFAILED;
    }
  }

  /* Register ST TAPA Task */
  if (SRegTTsk((Ent)ENTST, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
      (Prior) BP_AIN_SM_PRIOR,
      stActvInit, stActvTsk) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SRegTTsk() for ST failed.");
    return RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "SRegTTsk() for ST successfull.");
 
  /* Attach ST TAPA Task */
  if (SAttachTTsk((Ent)ENTST, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SAttachTTsk() for ST failed.");
    return RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "SAttachTTsk for ST successfull.");

  /* Register SH TAPA Task */
  if (SRegTTsk((Ent)ENTSH, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
      (Prior) BP_AIN_SM_PRIOR,
      shActvInit, shActvTsk) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SRegTTsk() for SH failed.");
    return RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "SRegTTsk() for SH successfull.");
 
  /* Attach SH TAPA Task */
  if (SAttachTTsk((Ent)ENTSH, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SAttachTTsk() for SH failed.");
    return RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "SAttachTTsk for SH successfull.");

/*DHEERAJ: Creating different system task for RY and MR*/
  if (SCreateSTsk((SSTskPrior)PRIOR0, &mrTskId) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "MR SCreateSTsk() failed.");
    return RFAILED;
  }


  /* Register MR TAPA Task */
  if (SRegTTsk((Ent)ENTMR, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
      (Prior) BP_AIN_SM_PRIOR,
      mrActvInit, mrActvTsk) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SRegTTsk() for MR failed.");
    return RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "SRegTTsk() for MR successfull.");
 
/*DHEERAJ: Creating different system task for RY and MR*/
  /* Attach MR TAPA Task */
  if (SAttachTTsk((Ent)ENTMR, (Inst)BP_AIN_SM_DEST_INST, mrTskId)!= ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SAttachTTsk() for MR failed.");
    return RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "SAttachTTsk for MR successfull.");

  /* Register SG TAPA Task */
  if (SRegTTsk((Ent)ENTSG, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
      (Prior) BP_AIN_SM_PRIOR,
      sgActvInit, sgActvTsk) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SRegTTsk() for SG failed.");
    return RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "SRegTTsk() for SG successfull.");
 
  /* Attach SG TAPA Task */
  if (SAttachTTsk((Ent)ENTSG, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SAttachTTsk() for SG failed.");
    return RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "SAttachTTsk for SG successfull.");

#if 1

/*DHEERAJ: Creating different system task for RY and MR*/
  if (SCreateSTsk((SSTskPrior)PRIOR0, &ryTskId) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Relay SCreateSTsk() failed.");
    return RFAILED;
  }


  /* Register RY TAPA Task */
  if (SRegTTsk((Ent)ENTRY, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
      (Prior) BP_AIN_SM_PRIOR,
      ryActvInit, ryActvTsk) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SRegTTsk() for RY failed.");
    return RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "SRegTTsk() for RY successfull.");
 
/*DHEERAJ: Creating different system task for RY and MR*/
  /* Attach RY TAPA Task */
  if (SAttachTTsk((Ent)ENTRY, (Inst)BP_AIN_SM_DEST_INST, ryTskId)!= ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SAttachTTsk() for RY failed.");
    return RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "SAttachTTsk for RY successfull.");

#endif

  if ((giINGwSmTapaMask & BP_AIN_SM_SCC_MASK) == BP_AIN_SM_SCC_MASK)
  {
    logger.logMsg (VERBOSE_FLAG, 0,
      "Creating a new system task for SP Layer");

    /* Create System Task */
    if (SCreateSTsk((SSTskPrior)BP_AIN_SM_PRIOR, &tstTskId) != ROK)
    {                                    
      logger.logMsg (ERROR_FLAG, 0,
        "SCreateSTsk() failed.");
      return RFAILED;
    }
  }

  /* Register SP TAPA Task */
  if (SRegTTsk((Ent)ENTSP, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
      (Prior) BP_AIN_SM_PRIOR,
      spActvInit, spActvTsk) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SRegTTsk() for SP failed.");
    return RFAILED;
  }
  logger.logMsg (TRACE_FLAG, 0,
    "SRegTTsk for SP successfull.");

  /* Attach SP TAPA Task */
  if (SAttachTTsk((Ent)ENTSP, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SAttachTTsk() for SP failed.");
    return RFAILED;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "SAttachTTsk for SP successfull.");
  if(TRANSPORT_TYPE_SIGTRAN == giINGwSmTransportType ) {
      
  logger.logMsg (TRACE_FLAG, 0,
        "In tst() and transport type is set to SIGTRAN");

  logger.logMsg (TRACE_FLAG, 0,
    "TRANSPORT_TYPE_SIGTRAN == giINGwSmTransportType .");

    if ((giINGwSmTapaMask & BP_AIN_SM_M3U_MASK) == BP_AIN_SM_M3U_MASK)
    {
      logger.logMsg (TRACE_FLAG, 0,
        "Creating a new system task for IT Layer");

      /* Create System Task */
      if (SCreateSTsk((SSTskPrior)BP_AIN_SM_PRIOR, &tstTskId) != ROK)
      {                                    
        logger.logMsg (ERROR_FLAG, 0,
          "SCreateSTsk() failed.");
        return RFAILED;
      }
    }

    /* Register IT TAPA Task */
    if (SRegTTsk((Ent)ENTIT, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
        (Prior) BP_AIN_SM_PRIOR,
        itActvInit, itActvTsk) != ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for IT failed.");
      return RFAILED;
    }

    /* Attach IT TAPA Task */
    if (SAttachTTsk((Ent)ENTIT, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for IT failed.");
      return RFAILED;
    }

    logger.logMsg (TRACE_FLAG, 0, "Registering ENTDV");
    /* Register DV TAPA Task */
    if (SRegTTsk((Ent)ENTDV, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM,
        (Prior) BP_AIN_SM_PRIOR,
        dvActvInit, dvActvTsk) != ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for DV failed.");
      return RFAILED;
    } 
      
    logger.logMsg (TRACE_FLAG, 0, "Attaching ENTDV");
    /* Attach DV TAPA Task */
    if (SAttachTTsk((Ent)ENTDV, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for DV failed.");
      return RFAILED;
    }


    if ((giINGwSmTapaMask & BP_AIN_SM_SCT_MASK) == BP_AIN_SM_SCT_MASK)
    {
      logger.logMsg (TRACE_FLAG, 0,
        "Creating a new system task for SB Layer");

      /* Create System Task */
      if (SCreateSTsk((SSTskPrior)BP_AIN_SM_PRIOR, &tstTskId) != ROK)
      {                                    
        logger.logMsg (ERROR_FLAG, 0,
          "SCreateSTsk() failed.");
        return RFAILED;
      }
    }

    /* Register SB TAPA Task */
    if (SRegTTsk((Ent)ENTSB, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
        (Prior) BP_AIN_SM_PRIOR,
        sbActvInit, sbActvTsk) != ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for SB failed.");
      return RFAILED;
    }

    /* Attach SB TAPA Task */
    if (SAttachTTsk((Ent)ENTSB, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for SB failed.");
      return RFAILED;
    }


    if ((giINGwSmTapaMask & BP_AIN_SM_TUC_MASK) == BP_AIN_SM_TUC_MASK)
    {
      logger.logMsg (TRACE_FLAG, 0,
        "Creating a new system task for HI Layer");

      /* Create System Task */
      if (SCreateSTsk((SSTskPrior)BP_AIN_SM_PRIOR, &tstTskId) != ROK)
      {                                    
        logger.logMsg (ERROR_FLAG, 0,
          "SCreateSTsk() failed.");
        return RFAILED;
      }
    }

    /* Register HI TAPA Task */
    if (SRegTTsk((Ent)ENTHI, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
        (Prior) BP_AIN_SM_PRIOR,
        hiActvInit, hiActvTsk) != ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for HI failed.");
      return RFAILED;
    }

    /* Attach HI TAPA Task */
    if (SAttachTTsk((Ent)ENTHI, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for HI failed.");
      return RFAILED;
    }

#if 0
    /* Create System Task  for TUCL Scanner Permanent Task */
    if (SCreateSTsk((SSTskPrior)BP_AIN_SM_PRIOR, &tstTskId) != ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SCreateSTsk() failed.");
      return RFAILED;
    }

    /* Register HI Scanner TAPA Task */
    if (SRegTTsk((Ent)ENTHI, (Inst)BP_AIN_SM_DEST_INST + 1, (Ttype)TTPERM, 
        (Prior) BP_AIN_SM_PRIOR,
        NULLP, hiScanPermTsk) != ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for HI Perm Taskfailed.");
      return RFAILED;
    }

    /* Attach HI Perm TAPA Task */
    if (SAttachTTsk((Ent)ENTHI, (Inst)BP_AIN_SM_DEST_INST + 1, tstTskId)!= ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for HI Scanner Task failed.");
      return RFAILED;
    }
#endif
  }//SIGTRAN Transport Type
  else if(TRANSPORT_TYPE_MTP == giINGwSmTransportType){
      
    logger.logMsg (TRACE_FLAG, 0,
        "In tst() and transport type is set to SS7 ");

    logger.logMsg (TRACE_FLAG, 0, "Registering ENTSN");

    /* Register SN TAPA Task */
    if (SRegTTsk((Ent)ENTSN, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
        (Prior) BP_AIN_SM_PRIOR,
        snActvInit, snActvTsk) != ROK) {

      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for SN failed.");
      return RFAILED;
    }
    /* Attach SN TAPA Task */

    logger.logMsg (TRACE_FLAG, 0, "Attaching ENTSN");

    if (SAttachTTsk((Ent)ENTSN, (Inst)BP_AIN_SM_DEST_INST, 
                                             tstTskId)!= ROK) {

      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for SN failed.");
      return RFAILED;
    }

    logger.logMsg (TRACE_FLAG, 0, "Registering ENTDN");

    /* Register DN TAPA Task */
    if (SRegTTsk((Ent)ENTDN, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
        (Prior) BP_AIN_SM_PRIOR,
        dnActvInit, dnActvTsk) != ROK) {

      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for DN failed.");
      return RFAILED;
    }
    /* Attach DN TAPA Task */

    logger.logMsg (TRACE_FLAG, 0, "Attaching ENTDN");

    if (SAttachTTsk((Ent)ENTDN, (Inst)BP_AIN_SM_DEST_INST, 
                                             tstTskId)!= ROK) {

      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for DN failed.");
      return RFAILED;
    }
#ifdef SOLARIS
    logger.logMsg (TRACE_FLAG, 0, "Registering ENTSD");
    /* Register SD TAPA Task */
    if (SRegTTsk((Ent)ENTSD, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
        (Prior) BP_AIN_SM_PRIOR,
        sdActvInit, sdActvTsk) != ROK) {

      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for SD failed.");
      return RFAILED;
    }

    logger.logMsg (TRACE_FLAG, 0, "Attaching ENTSD");
    /* Attach SD TAPA Task */
    if (SAttachTTsk((Ent)ENTSD, (Inst)BP_AIN_SM_DEST_INST,
                                             tstTskId)!= ROK) {

      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for SD failed.");
      return RFAILED;
    }
#endif

  }//MTP Transport Type
  else{

      logger.logMsg (TRACE_FLAG, 0,
        "In tst() and transport type is set to both SS7 and SIGTRAN");
     
     if ((giINGwSmTapaMask & BP_AIN_SM_M3U_MASK) == BP_AIN_SM_M3U_MASK)
    {
      logger.logMsg (TRACE_FLAG, 0,
        "Creating a new system task for IT Layer");

      /* Create System Task */
      if (SCreateSTsk((SSTskPrior)BP_AIN_SM_PRIOR, &tstTskId) != ROK)
      {                                    
        logger.logMsg (ERROR_FLAG, 0,
          "SCreateSTsk() failed.");
        return RFAILED;
      }
    }

    /* Register IT TAPA Task */
    if (SRegTTsk((Ent)ENTIT, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
        (Prior) BP_AIN_SM_PRIOR,
        itActvInit, itActvTsk) != ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for IT failed.");
      return RFAILED;
    }

    /* Attach IT TAPA Task */
    if (SAttachTTsk((Ent)ENTIT, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for IT failed.");
      return RFAILED;
    }

    logger.logMsg (TRACE_FLAG, 0, "Registering ENTDV");
    /* Register DV TAPA Task */
    if (SRegTTsk((Ent)ENTDV, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM,
        (Prior) BP_AIN_SM_PRIOR,
        dvActvInit, dvActvTsk) != ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for DV failed.");
      return RFAILED;
    } 
      
    logger.logMsg (TRACE_FLAG, 0, "Attaching ENTDV");
    /* Attach DV TAPA Task */
    if (SAttachTTsk((Ent)ENTDV, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for DV failed.");
      return RFAILED;
    }


    if ((giINGwSmTapaMask & BP_AIN_SM_SCT_MASK) == BP_AIN_SM_SCT_MASK)
    {
      logger.logMsg (TRACE_FLAG, 0,
        "Creating a new system task for SB Layer");

      /* Create System Task */
      if (SCreateSTsk((SSTskPrior)BP_AIN_SM_PRIOR, &tstTskId) != ROK)
      {                                    
        logger.logMsg (ERROR_FLAG, 0,
          "SCreateSTsk() failed.");
        return RFAILED;
      }
    }

    /* Register SB TAPA Task */
    if (SRegTTsk((Ent)ENTSB, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
        (Prior) BP_AIN_SM_PRIOR,
        sbActvInit, sbActvTsk) != ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for SB failed.");
      return RFAILED;
    }

    /* Attach SB TAPA Task */
    if (SAttachTTsk((Ent)ENTSB, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for SB failed.");
      return RFAILED;
    }


    if ((giINGwSmTapaMask & BP_AIN_SM_TUC_MASK) == BP_AIN_SM_TUC_MASK)
    {
      logger.logMsg (TRACE_FLAG, 0,
        "Creating a new system task for HI Layer");

      /* Create System Task */
      if (SCreateSTsk((SSTskPrior)BP_AIN_SM_PRIOR, &tstTskId) != ROK)
      {                                    
        logger.logMsg (ERROR_FLAG, 0,
          "SCreateSTsk() failed.");
        return RFAILED;
      }
    }

    /* Register HI TAPA Task */
    if (SRegTTsk((Ent)ENTHI, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
        (Prior) BP_AIN_SM_PRIOR,
        hiActvInit, hiActvTsk) != ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for HI failed.");
      return RFAILED;
    }

    /* Attach HI TAPA Task */
    if (SAttachTTsk((Ent)ENTHI, (Inst)BP_AIN_SM_DEST_INST, tstTskId)!= ROK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for HI failed.");
      return RFAILED;
    }
    
    logger.logMsg (TRACE_FLAG, 0, "Registering ENTSN");

    /* Register SN TAPA Task */
    if (SRegTTsk((Ent)ENTSN, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
        (Prior) BP_AIN_SM_PRIOR,
        snActvInit, snActvTsk) != ROK) {

      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for SN failed.");
      return RFAILED;
    }
    /* Attach SN TAPA Task */

    logger.logMsg (TRACE_FLAG, 0, "Attaching ENTSN");

    if (SAttachTTsk((Ent)ENTSN, (Inst)BP_AIN_SM_DEST_INST, 
                                             tstTskId)!= ROK) {

      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for SN failed.");
      return RFAILED;
    }

    logger.logMsg (TRACE_FLAG, 0, "Registering ENTDN");

    /* Register DN TAPA Task */
    if (SRegTTsk((Ent)ENTDN, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
        (Prior) BP_AIN_SM_PRIOR,
        dnActvInit, dnActvTsk) != ROK) {

      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for DN failed.");
      return RFAILED;
    }
    /* Attach DN TAPA Task */

    logger.logMsg (TRACE_FLAG, 0, "Attaching ENTDN");

    if (SAttachTTsk((Ent)ENTDN, (Inst)BP_AIN_SM_DEST_INST, 
                                             tstTskId)!= ROK) {

      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for DN failed.");
      return RFAILED;
    }
#ifdef SOLARIS
    logger.logMsg (TRACE_FLAG, 0, "Registering ENTSD");
    /* Register SD TAPA Task */
    if (SRegTTsk((Ent)ENTSD, (Inst)BP_AIN_SM_DEST_INST, (Ttype)TTNORM, 
        (Prior) BP_AIN_SM_PRIOR,
        sdActvInit, sdActvTsk) != ROK) {

      logger.logMsg (ERROR_FLAG, 0,
        "SRegTTsk() for SD failed.");
      return RFAILED;
    }

    logger.logMsg (TRACE_FLAG, 0, "Attaching ENTSD");
    /* Attach SD TAPA Task */
    if (SAttachTTsk((Ent)ENTSD, (Inst)BP_AIN_SM_DEST_INST,
                                             tstTskId)!= ROK) {

      logger.logMsg (ERROR_FLAG, 0,
        "SAttachTTsk() for SD failed.");
      return RFAILED;
    }
#endif
  }// Transport is of type both SS7 and SIGTRAN

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving tst");

  return ROK;
}

/******************************************************************************
*
*     Fun:   handleChangeState()
*
*     Desc:  function to handle ChangeState to RUNNNING and STOPPED
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*                                      
*******************************************************************************/
int
INGwSmDistributor::handleChangeState(INGwSmQueueMsg *apQueMsg)
{

  logger.logINGwMsg(false,ALWAYS_FLAG,0,
    "Entering INGwSmDistributor::handleChangeState");
  int liScenario = 0;

  /*
   * The only change state which are allowed are RUNNING And STOPPED
   */
  if (apQueMsg->t.ccmOper.ccmOp.chngStInfo.mState == BP_AIN_SM_STATE_RUNNING)
  {
    /*
     * retrieve a new transaction Id from the Repository
     */

    int liTransId = mpRepository->getTransactionId();
    miChangeState2RunningTransId = liTransId;

    /*
     * create a new request for this scenario and insert it in the Request Table
     */
    INGwSmRequest *lpReq = new INGwSmRequest (liTransId, 
                                apQueMsg, this);
    if (mpRequestTable->addRequest (liTransId, lpReq) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Unable to add request in the request Table <%d>", liTransId);

      //unblock the CCM thread which is blocked by the wrapper
      INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
      notifyCCM (lpCtx);

      //delete the request object created for the scenario
      if (lpReq)
        delete lpReq;
      return BP_AIN_SM_FAIL;
    }


    INGwSmRequest *lpReq1 = mpRequestTable->getRequest(liTransId);

	logger.logMsg (TRACE_FLAG, 0,"[CCPU] request Table <%d>", liTransId);

    logger.logMsg (TRACE_FLAG, 0,"[CCPU] src <%d>", lpReq1->mpOrigReq->mSrc);
     /*
      * start the handling of the scenario by the Request Object
      */
    if (lpReq->start (BP_AIN_SM_CCM_CS_RUNNING) == BP_AIN_SM_SCENARIO_COMP)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "ChangeState to RUNNING Scenario FAILED");

      //unblock the CCM thread which is blocked by the wrapper
      INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
      notifyCCM (lpCtx);

      //remove the request from the request table
      mpRequestTable->removeRequest (liTransId, lpReq);

      //delete the request object created for the scenario
      if (lpReq)
        delete lpReq;
      return BP_AIN_SM_FAIL;
    }
    
  }
  else if (apQueMsg->t.ccmOper.ccmOp.chngStInfo.mState == BP_AIN_SM_STATE_STOPPED)
  {
    //get a new transaction id for this scenario
    int liTransId = mpRepository->getTransactionId();

    /*
     * create a new request for this scenario and insert it in the Request Table
     */
    INGwSmRequest *lpReq = new INGwSmRequest (liTransId,
                                apQueMsg, this);

    if (mpRequestTable->addRequest (liTransId, lpReq) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "Unable to add request in the request Table <%d>", liTransId);

      //unblock the CCM thread which is blocked by the wrapper
      INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);

      //cleanup everything in Stack Manager
      shutdown ();

      notifyCCM (lpCtx);

      //delete the request object created for the scenario
      if (lpReq)
        delete lpReq;
      return BP_AIN_SM_FAIL;
    }

    //start the handling of the scenario by calling start on the request
    if (lpReq->start (BP_AIN_SM_CCM_CS_STOPPED) == BP_AIN_SM_SCENARIO_COMP)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "ChangeState to STOPPED Scenario FAILED");

      //unblock the CCM thread which is blocked by the wrapper

      //cleanup everything in Stack Manager
      shutdown ();

      INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
      notifyCCM (lpCtx);

      //remove the request from the request table
      mpRequestTable->removeRequest (liTransId, lpReq);

      //delete the request object created for the scenario
      if (lpReq)
        delete lpReq;
      return BP_AIN_SM_FAIL;
    }
  }
  else 
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Invalid state in handleChangeState <%d>", 
      apQueMsg->t.ccmOper.ccmOp.chngStInfo.mState);

    //delete the original request object since it has to be deleted by the user
    delete apQueMsg;
		apQueMsg = NULL;
    return BP_AIN_SM_FAIL;
  }

  logger.logINGwMsg(false,ALWAYS_FLAG,0,
    "Leaving INGwSmDistributor::handleChangeState");

  return BP_AIN_SM_OK;

}

/******************************************************************************
*
*     Fun:   handleConfigure()
*
*     Desc:  function to handle configure request from the CCM
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::handleConfigure(INGwSmQueueMsg *apQueMsg)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::handleConfigure");

  int liScenario = -1;
  int liTransId = mpRepository->getTransactionId();
  INGwSmStsHdlr *mpStsReq = mpSmWrapper->getStsHdlrInst();

    //create a new request object for this scenario
    INGwSmRequest *lpReq = new INGwSmRequest (liTransId,
        apQueMsg, this);

  if (mConfigType == 0) /* 0 if configuration is from XML and 1 for EMS*/
  {
     logger.logMsg (TRACE_FLAG, 0,
       "[CCPU]Entering INGwSmDistributor::handleConfigure, mConfigType == 0");
    /*
     * We need to delete the oid and value string allocated in the wrapper
     */

    //get the OID string
    char *lpcOid = apQueMsg->t.ccmOper.ccmOp.oidInfo.mpcOid;

    //get the OID Value String
    char *lpcValue = apQueMsg->t.ccmOper.ccmOp.oidInfo.mpcValue;

    //get a new transaction id from the Repository
    
    if (lpcOid == 0 || lpcValue == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
          "Scenario failed as oid and value were NULL");

      //unblock the CCM thread which is blocked by the wrapper
      INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
      notifyCCM (lpCtx);

      if (lpcOid)
        delete [] lpcOid;
      if (lpcValue) 
        delete [] lpcValue;

      //delete the request object created for the scenario
      mpRequestTable->removeRequest (liTransId, lpReq);
      if (lpReq)
        delete lpReq;

      return BP_AIN_SM_FAIL;
    }

    logger.logMsg (ERROR_FLAG, 0,
        "configure request received for oid <%s>, value <%s>",
        lpcOid, lpcValue);

    //define the request context to be used in some cases
    INGwSmRequestContext *lpReqContext = 0;

    /* Check if oid is for Alarm Level */
    if (strcmp (lpcOid, SM_CCM_ALM_LEVEL) == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
          "Alarm Level has been modified. <%s>", lpcValue);

      liScenario = BP_AIN_SM_CCM_ALM_LEVEL;

      /*
       * Currently this will affect the Levels of all the layers
       */

      int liAlmLevel = atoi (lpcValue);
      mpRepository->setAlarmLevel (BP_AIN_SM_ALL_LAYER, liAlmLevel);
    }

    //check if oid is for Trace Level
    else if (strcmp (lpcOid, SM_CCM_TRC_LEVEL) == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
          "Trace Level has been modified. <%s>", lpcValue);

      liScenario = BP_AIN_SM_CCM_TRC_LEVEL;

      /*
       * Currently this will affect the Levels of all the layers
       */

      int liTrcLevel = atoi (lpcValue);
      mpRepository->setTrcLevel (BP_AIN_SM_ALL_LAYER, liTrcLevel);
    }

    //check if oid is for Debug Level
    else if (strcmp (lpcOid, SM_CCM_DBG_LEVEL) == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
          "Debug Level has been modified. <%s>", lpcValue);

      liScenario = BP_AIN_SM_CCM_DBG_LEVEL;

      /*
       * Currently this will affect the Levels of all the layers
       */

      int liDbgLevel = atoi (lpcValue);
      mpRepository->setDebugLevel (BP_AIN_SM_ALL_LAYER, liDbgLevel);
    }

    //check if oid is for statistics level
    else if (strcmp (lpcOid, SM_CCM_STS_LEVEL) == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
          "Statistics Level has been modified. <%s>", lpcValue);

      liScenario = BP_AIN_SM_CCM_STS_LEVEL;

      /*
       * Currently this will affect the Levels of all the layers
       */

      int liStsLevel = atoi (lpcValue);
      mpRepository->setStsLevel (liStsLevel);

      //update the operation mask
      mpStsReq->setOperationMask (liStsLevel);

      /*
       * The timer will take affect in the next cycle of statistics
       * so clear up the request etc
       */

      if (lpcOid)
        delete [] lpcOid;
      if (lpcValue)
        delete [] lpcValue;

      //unblock the CCM thread which is blocked by the wrapper
      INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
      lpCtx->returnValue = BP_AIN_SM_OK;
      notifyCCM (lpCtx);

      //delete the request object created for the scenario
      mpRequestTable->removeRequest (liTransId, lpReq);
      if (lpReq)
        delete lpReq;

      return BP_AIN_SM_OK;
    }

    //check if oid is for statistics timer
    else if (strcmp (lpcOid, SM_CCM_STS_TIMER) == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
          "Statistics Timer has been modified. <%s>", lpcValue);

      liScenario = BP_AIN_SM_CCM_STS_TIMER;

      int liStsTmr = atoi (lpcValue);
      int liDuration = mpRepository->getStsTimer ();

      mpRepository->setStsTimer (liStsTmr);

      if (deregisterTimer (liDuration, miStsTransId) == BP_AIN_SM_FAIL)
      {   
        logger.logMsg (ERROR_FLAG, 0,
            "deregisterTimer Failed for the statistics");
      }   

      if (registerTimer (liStsTmr, miStsTransId) == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
            "registerTimer Failed for the statistics");
      }

      /*
       * The timer will take affect in the next cycle of statistics
       * so clear up the request etc
       */

      if (lpcOid)
        delete [] lpcOid;
      if (lpcValue)
        delete [] lpcValue;

      //unblock the CCM thread which is blocked by the wrapper
      INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
      notifyCCM (lpCtx);

      //delete the request object created for the scenario
      mpRequestTable->removeRequest (liTransId, lpReq);
      if (lpReq)
        delete lpReq;

      return BP_AIN_SM_OK;

    }

    //check if oid is for ASP Active
    else if (strcmp (lpcOid, SM_CCM_ASP_ACTIVE) == 0)
    { 
      logger.logMsg (ERROR_FLAG, 0, 
          "ASP Active has been sent for PSP <%s>", lpcValue);

      liScenario = BP_AIN_SM_CCM_ASP_ACTIVE;

      //set the request context to PSP Id
      lpReqContext = new INGwSmRequestContext;
      lpReqContext->pspId = atoi (lpcValue);
    }

    //check if oid is for ASP Inactive
    else if (strcmp (lpcOid, SM_CCM_ASP_INACTIVE) == 0)
    { 
      logger.logMsg (ERROR_FLAG, 0, 
          "ASP Inactive has been sent for PSP <%s>", lpcValue);

      liScenario = BP_AIN_SM_CCM_ASP_INACTIVE;

      //set the request context to PSP Id
      lpReqContext = new INGwSmRequestContext;
      lpReqContext->pspId = atoi (lpcValue);
    }

    //check if oid is for ASP UP
    else if (strcmp (lpcOid, SM_CCM_ASP_UP) == 0)
    { 
      logger.logMsg (ERROR_FLAG, 0, 
          "ASP UP Has been sent for PSP <%s>", lpcValue);

      liScenario = BP_AIN_SM_CCM_ASP_UP;

      //set the request context to PSP Id
      lpReqContext = new INGwSmRequestContext;
      lpReqContext->pspId = atoi (lpcValue);
    }

    //check if oid is for ASP DOWN
    else if (strcmp (lpcOid, SM_CCM_ASP_DOWN) == 0)
    { 
      logger.logMsg (ERROR_FLAG, 0, 
          "ASP DOWN Has been sent for PSP <%s>", lpcValue);

      liScenario = BP_AIN_SM_CCM_ASP_DOWN;

      //set the request context to PSP Id
      lpReqContext = new INGwSmRequestContext;
      lpReqContext->pspId = atoi (lpcValue);
    }

    //check if oid is for Establish Association
    else if (strcmp (lpcOid, SM_CCM_EST_ASS) == 0)
    { 
      logger.logMsg (ERROR_FLAG, 0, 
          "Establish Association has been sent for PSP <%s>", lpcValue);

      liScenario = BP_AIN_SM_CCM_EST_ASS;

      //set the request context to PSP Id
      lpReqContext = new INGwSmRequestContext;
      lpReqContext->pspId = atoi (lpcValue);
    }

    //check if oid is for Terminate Association
    else if (strcmp (lpcOid, SM_CCM_TRM_ASS) == 0)
    { 
      logger.logMsg (ERROR_FLAG, 0, 
          "Terminate Association has been sent for PSP <%s>", lpcValue);

      liScenario = BP_AIN_SM_CCM_TRM_ASS;

      //set the request context to PSP Id
      lpReqContext = new INGwSmRequestContext;
      lpReqContext->pspId = atoi (lpcValue);
    }

    if (lpcOid)
      delete [] lpcOid;
    if (lpcValue)
      delete [] lpcValue;


    /*
     * TBD : Need to find out the scenario before passing it to request object
     */
    if (lpReq->start (liScenario, 0, lpReqContext) == BP_AIN_SM_SCENARIO_COMP)
    {
      logger.logMsg (ERROR_FLAG, 0,
          "Scenario FAILED");

      //unblock the CCM thread which is blocked by the wrapper
      INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
      notifyCCM (lpCtx);

      //delete the request object created for the scenario
      mpRequestTable->removeRequest (liTransId, lpReq);
      if (lpReq)
        delete lpReq;

      return BP_AIN_SM_FAIL;
    }
  }
  else
  {
    logger.logMsg (TRACE_FLAG, 0,
       "[CCPU]Entering INGwSmDistributor::handleConfigure else part ");
    if ((apQueMsg->t.stackData.cmdType == ENABLE_NODE) || (apQueMsg->t.stackData.cmdType == DISABLE_NODE))
    {
      lpReq->miTransactionId = BP_AIN_SM_SG_TRANSID;
      liTransId = BP_AIN_SM_SG_TRANSID;
    }
    if ((apQueMsg->t.stackData.cmdType == AUDIT_INC) )
    {
      lpReq->miTransactionId = BP_AIN_SM_AUDIT_TRANSID;
      liTransId = BP_AIN_SM_AUDIT_TRANSID;
    }
    //add the request in the request table
    if (mpRequestTable->addRequest (liTransId, lpReq) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0,
          "Unable to add request in the request Table <%d>", liTransId);

      //unblock the CCM thread which is blocked by the wrapper
      INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
      notifyCCM (lpCtx);

      //delete the request object created for the scenario
      if (lpReq)
        delete lpReq;
      return BP_AIN_SM_FAIL;
    }
      logger.logMsg (TRACE_FLAG, 0,"[CCPU] in HandleConfigure cmdType %d ",apQueMsg->t.stackData.cmdType);
    switch(apQueMsg->t.stackData.cmdType)
    {
      
      case ADD_NETWORK: 
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_NETWORK and for layer BP_AIN_SM_M3U_LAYER");

            liScenario = BP_AIN_SM_CCM_ADD_M3UA_NETWORK;
          }
          else
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_NETWORK and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_CCM_ADD_SCCP_NETWORK;
          }

          break;
        }
      case ADD_GTADDRMAP: 
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_GTADDRMAP and for layer BP_AIN_SM_SCC_LAYER");

            liScenario = BP_AIN_SM_CCM_ADD_SCCP_GTADDR_MAP;
          }
          break;
        }
      case ADD_GTRULE: 
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_GTRULE and for layer BP_AIN_SM_SCC_LAYER");

            liScenario = BP_AIN_SM_CCM_ADD_SCCP_GTRULE;
          }
          break;
        }
      case DEL_GTADDRMAP: 
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_GTADDRMAP and for layer BP_AIN_SM_SCC_LAYER");

            liScenario = BP_AIN_SM_CCM_DEL_SCCP_GTADDR_MAP;
          }
          break;
        }
      case DEL_GTRULE: 
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_GTRULE and for layer BP_AIN_SM_SCC_LAYER");

            liScenario = BP_AIN_SM_CCM_DEL_SCCP_GTRULE;
          }
          break;
        }
      case DEL_NETWORK:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {

            logger.logMsg (TRACE_FLAG, 0,"in DEL_NETWORK and for layer BP_AIN_SM_M3U_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_M3UA_NETWORK;
          }
          else
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_NETWORK and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_SCCP_NETWORK;
          }

          break;
        }
      case DEL_LINK:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {

            logger.logMsg (TRACE_FLAG, 0,"in DEL_LINK and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_MTP3_LINK;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_MTP3_LAYER)
          {
            
            logger.logMsg (TRACE_FLAG, 0,"in DEL_LINK and for layer BP_AIN_SM_LDF_MTP3_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_LDF_MTP3_LINK;
          }
          else
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_LINK and for layer BP_AIN_SM_MTP2_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_MTP2_LINK;
          }

          break;
        }
      case DEL_LINKSET:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {

            logger.logMsg (TRACE_FLAG, 0,"in DEL_LINKSET and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_MTP3_LINKSET;
          }
          break;
        }
      case DEL_ROUTE:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_ROUTE and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_MTP3_RTE;
          }
          else
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_ROUTE and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_SCCP_RTE;
          }
          break;
        }
      case DEL_ENDPOINT:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_ENDPOINT and for layer BP_AIN_SM_M3U_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_M3UA_EP;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCT_LAYER)
          {
            if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_SCT_SCTSAP)
            {
              logger.logMsg (TRACE_FLAG, 0,"in DEL_ENDPOINT and for layer BP_AIN_SM_SCT_LAYER and subOpr BP_AIN_SM_SCT_SCTSAP");
              liScenario = BP_AIN_SM_CCM_DEL_SCTP_EP;
            }
            else
            {
              logger.logMsg (TRACE_FLAG, 0,"in DEL_ENDPOINT and for layer BP_AIN_SM_SCT_LAYER and subOpr BP_AIN_SM_SCT_TSAP");
              liScenario = BP_AIN_SM_CCM_DEL_SCTP_T_EP;
            }

          }
          else
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_ENDPOINT and for layer BP_AIN_SM_TUC_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_TUCL_EP;
          }
          break;
        }
      case DEL_USR_PART:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_USR_PART and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_SCCP_UP;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_USR_PART and for layer BP_AIN_SM_M3U_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_M3UA_UP;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_M3UA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_USR_PART and for layer BP_AIN_SM_LDF_M3UA_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_LDF_M3UA_UP;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_USR_PART and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_MTP3_UP;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DEL_USR_PART and for layer BP_AIN_SM_LDF_MTP3_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_LDF_MTP3_UP;
          }
          break;
        }
      case DEL_REMOTE_SSN:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {

            logger.logMsg (TRACE_FLAG, 0,"in DEL_REMOTE_SSN and for layer BP_AIN_SM_SCCP_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_SCCP_RSSN;
          }
          break;
        }
      case DEL_LOCAL_SSN:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {

            logger.logMsg (TRACE_FLAG, 0,"in DEL_LOCAL_SSN and for layer BP_AIN_SM_SCCP_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_SCCP_LSSN;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
          {
            if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_TCA_USAP)
            {
              logger.logMsg (TRACE_FLAG, 0,"in DEL_LOCAL_SSN and for layer BP_AIN_SM_TCAP_LAYER and SubOpr BP_AIN_SM_TCA_USAP");
              liScenario = BP_AIN_SM_CCM_DEL_TCAP_USAP;
            }
            else if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_TCA_LSAP)
            {
              logger.logMsg (TRACE_FLAG, 0,"in DEL_LOCAL_SSN and for layer BP_AIN_SM_TCAP_LAYER and SubOpr BP_AIN_SM_TCA_LSAP");
              liScenario = BP_AIN_SM_CCM_DEL_TCAP_LSAP;
            }

            
          }
          break;
        }

      case DISABLE_SAPS:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {

            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_SAPS and for layer BP_AIN_SM_SCCP_LAYER");
            liScenario = BP_AIN_SM_CCM_DIS_SCCP_LSSN;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
          {
            if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_TCA_USAP)
            {
              logger.logMsg (TRACE_FLAG, 0,"in DISABLE_SAPS and for layer BP_AIN_SM_TCAP_LAYER and SubOpr BP_AIN_SM_TCA_USAP");
              liScenario = BP_AIN_SM_CCM_DIS_TCAP_USAP;
            }
            else if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_TCA_LSAP)
            {
              logger.logMsg (TRACE_FLAG, 0,"in DISABLE_SAPS and for layer BP_AIN_SM_TCAP_LAYER and SubOpr BP_AIN_SM_TCA_LSAP");
              liScenario = BP_AIN_SM_CCM_DIS_TCAP_LSAP;
            }
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {

            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_SAPS and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_CCM_DIS_MTP3_SAP;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_SAPS and for layer BP_AIN_SM_M3U_LAYER");
            liScenario = BP_AIN_SM_CCM_DIS_M3UA_SAP;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCT_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_SAPS and for layer BP_AIN_SM_SCT_LAYER");
            liScenario = BP_AIN_SM_CCM_DIS_SCT_SAP;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TUC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_SAPS and for layer BP_AIN_SM_TUC_LAYER");
            liScenario = BP_AIN_SM_CCM_DIS_TUC_SAP;
          }

          break;
        }

      case DEL_ASP:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {

            logger.logMsg (TRACE_FLAG, 0,"in DEL_ASP and for layer BP_AIN_SM_M3UA_LAYER");
            liScenario = BP_AIN_SM_CCM_DEL_M3UA_ASP;
          }
          break;
        }
      case DEL_AS:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
	          if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_M3U_PS)
	          {
            	logger.logMsg (TRACE_FLAG, 0,"in DEL_AS and for layer BP_AIN_SM_M3U_LAYER and subOpr BP_AIN_SM_M3U_PS");
            	liScenario = BP_AIN_SM_CCM_DEL_M3UA_AS;
	          }
	          else if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_M3U_ROUTE)
            {
              logger.logMsg (TRACE_FLAG, 0,"in DEL_AS and for layer BP_AIN_SM_M3UA_LAYER and subOpr BP_AIN_SM_M3U_ROUTE");
              liScenario = BP_AIN_SM_CCM_DEL_M3UA_RTE;
            }

          }

          break;
        }

      case ADD_LINKSET: 
        {
          logger.logMsg (TRACE_FLAG, 0,"case ADD_LINKSET layer=<%d> addrof stackData=<%d>,stackData.procId=<%d>,stackData.dummyShort=<%d>, stackData.dummyInt=<%d>, stackData.stackLayer=<%d>", apQueMsg->t.stackData.stackLayer, &(apQueMsg->t.stackData),&(apQueMsg->t.stackData.procId),&(apQueMsg->t.stackData.dummyShort), &(apQueMsg->t.stackData.dummyInt), &(apQueMsg->t.stackData.stackLayer));
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_LINKSET and for layer BP_AIN_SM_MTP3_LAYER");

            liScenario = BP_AIN_SM_CCM_ADD_LNKSET;
          }
          break;
        }
      case MODIFY_LINKSET: 
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in MODIFY_LINKSET and for layer BP_AIN_SM_MTP3_LAYER");

            liScenario = BP_AIN_SM_CCM_MOD_LNKSET;
          }
          break;
        }

      case ADD_LINK: 
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_LINK and for layer BP_AIN_SM_MTP3_LAYER");

            liScenario = BP_AIN_SM_CCM_ADD_MTP3_LNK;
          }
          else if (apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_LINK and for layer BP_AIN_SM_LDF_MTP3_LAYER");

            liScenario = BP_AIN_SM_CCM_ADD_LDF_MTP3_LNK;
          }
          else 
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_LINK and for layer BP_AIN_SM_MTP2_LAYER");

            liScenario = BP_AIN_SM_CCM_ADD_MTP2_LNK;
          }
          break;
        }
      case MODIFY_LINK:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in MODIFY_LINK and for layer BP_AIN_SM_MTP3_LAYER");

            liScenario = BP_AIN_SM_CCM_MOD_MTP3_LNK;
          }
          else if (apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in MODIFY_LINK and for layer BP_AIN_SM_LDF_MTP3_LAYER");

            liScenario = BP_AIN_SM_CCM_MOD_LDF_MTP3_LNK;
          }
          else 
          {
            logger.logMsg (TRACE_FLAG, 0,"in MODIFY_LINK and for layer BP_AIN_SM_MTP2_LAYER");

            liScenario = BP_AIN_SM_CCM_MOD_MTP2_LNK;
          }
          break;
        }

      case ADD_USERPART:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"ADD_USERPART for BP_AIN_SM_MTP3_LAYER layer");

            liScenario = BP_AIN_SM_CCM_ADD_MTP3_USERPART;
          }
          else if (apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_USERPART for BP_AIN_SM_M3U_LAYER layer");

            liScenario = BP_AIN_SM_CCM_ADD_M3UA_USERPART;
          }
          else if (apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_USERPART for BP_AIN_SM_SCC_LAYER layer");

            liScenario = BP_AIN_SM_CCM_ADD_SCCP_USERPART;
          }
          else if (apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_USERPART for BP_AIN_SM_LDF_MTP3_LAYER layer");

            liScenario = BP_AIN_SM_CCM_ADD_LDF_MTP3_USERPART;
          }
          else if (apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_M3UA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_USERPART for BP_AIN_SM_LDF_M3UA_LAYER layer");

            liScenario = BP_AIN_SM_CCM_ADD_LDF_M3UA_USERPART;
          }
          break;
        }
      case ENABLE_LINK:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_LINK and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_ENABLE_MTP3_LINK;
          }
          else
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_LINK and for layer BP_AIN_SM_MTP2_LAYER");
            liScenario = BP_AIN_SM_ENABLE_MTP2_LINK;
          }
          break;
        }
      case ENABLE_NODE:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SG_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_NODE and for layer BP_AIN_SM_SG_LAYER");
            liScenario = BP_AIN_SM_SG_ENABLE_NODE;
          }
          break;
        }
      case DISABLE_NODE:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SG_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_NODE and for layer BP_AIN_SM_SG_LAYER");
            liScenario = BP_AIN_SM_SG_DISABLE_NODE;
          }
          break;
        }
      case DISABLE_LINK:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_LINK and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_DISABLE_MTP3_LINK;
          }
          else
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_LINK and for layer BP_AIN_SM_MTP2_LAYER");
            liScenario = BP_AIN_SM_DISABLE_MTP2_LINK;
          }
          break;
        }
      case ADD_ROUTE:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_ROUTE and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_CCM_ADD_MTP3_ROUTE;
          }
          else
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_ROUTE and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_CCM_ADD_SCCP_ROUTE;
          }

          break;
        }
        case OPEN_ENDPOINT:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in OPEN_ENDPOINT and for layer BP_AIN_SM_M3U_LAYER");
            liScenario = BP_AIN_SM_CCM_OPEN_EP;
          }
          break;
        }

        case BND_M3UA:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_IT_SCTSAP)
            { 
              logger.logMsg (TRACE_FLAG, 0,"in BND_M3UA for layer BP_AIN_SM_M3U_LAYER and subopr BP_AIN_SM_IT_SCTSAP");
              liScenario = BP_AIN_SM_CCM_M3UA_SCT_BND;
            }
            else
            {
              logger.logMsg (TRACE_FLAG, 0,"in BND_M3UA for layer BP_AIN_SM_M3U_LAYER and subopr BP_AIN_SM_IT_NSAP");
              liScenario = BP_AIN_SM_CCM_M3UA_NSAP_BND;
            }
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in BND_M3UA and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_CCM_SCCP_BND;
          }

          break;
        }

       case BND_SCTP:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCT_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in BND_SCTP and for layer BP_AIN_SM_SCT_LAYER");
            liScenario = BP_AIN_SM_CCM_SCTP_BND;
          }

          break;
        }

       case ADD_ENDPOINT:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_ENDPOINT and for layer BP_AIN_SM_M3UA_LAYER");
            liScenario = BP_AIN_SM_CCM_ADD_M3UA_EP;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCT_LAYER)
          {
	          if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_SCT_TSAP)
            {
            	logger.logMsg (TRACE_FLAG, 0,"in ADD_ENDPOINT and for layer BP_AIN_SM_SCT_LAYER");
            	liScenario = BP_AIN_SM_CCM_ADD_SCTP_TSAP;
	          }
            else if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_SCT_SCTSAP)
            {
	  	        logger.logMsg (TRACE_FLAG, 0,"in ADD_ENDPOINT and for layer BP_AIN_SM_SCT_LAYER and subOpr BP_AIN_SM_SCT_SCTSAP");
              liScenario = BP_AIN_SM_CCM_ADD_SCTP_SCTSAP;
	          }
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TUC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_ENDPOINT and for layer BP_AIN_SM_TUC_LAYER");
            liScenario = BP_AIN_SM_CCM_ADD_TUCL_EP;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_M3UA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_ENDPOINT and for layer BP_AIN_SM_TUC_LAYER");
            liScenario = BP_AIN_SM_CCM_ADD_LDF_M3UA_EP;
          }

          break;
        }

       case ADD_LOCAL_SSN:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_LOCAL_SSN and for layer BP_AIN_SM_SCC_LAYER");

            liScenario = BP_AIN_SM_CCM_ADD_SCCP_L_SSN;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
          {
	          if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_TCA_USAP)
            {
		          logger.logMsg (TRACE_FLAG, 0,"in ADD_LOCAL_SSN and for layer BP_AIN_SM_TCA_LAYER and subOpr BP_AIN_SM_TCA_USAP");
            	liScenario = BP_AIN_SM_CCM_ADD_TCAP_USAP;
	          }
	          else if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_TCA_LSAP)
            {
		          logger.logMsg (TRACE_FLAG, 0,"in ADD_LOCAL_SSN and for layer BP_AIN_SM_TCA_LAYER and subOpr BP_AIN_SM_TCA_LSAP");
            	liScenario = BP_AIN_SM_CCM_ADD_TCAP_LSAP;
	          }
          }

          break;
        }


       case ADD_REMOTE_SSN:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_REMOTE_SSN and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_CCM_ADD_SCCP_ROUTE;
          }

          break;
        }

       case ADD_AS:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
	          if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_M3U_PS)
	          {
            	logger.logMsg (TRACE_FLAG, 0,"in ADD_AS and for layer BP_AIN_SM_M3U_LAYER and subOpr BP_AIN_SM_M3U_PS");
            	liScenario = BP_AIN_SM_CCM_ADD_M3UA_AS;
	          }
	          else if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_M3U_ROUTE)
            {
              logger.logMsg (TRACE_FLAG, 0,"in ADD_AS and for layer BP_AIN_SM_M3UA_LAYER and subOpr BP_AIN_SM_M3U_ROUTE");
              liScenario = BP_AIN_SM_CCM_ADD_M3UA_ROUTE;
            }

          }

          break;
        }
       case MODIFY_AS:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
	          if(apQueMsg->t.stackData.subOpr == BP_AIN_SM_M3U_PS)
	          {
            	logger.logMsg (TRACE_FLAG, 0,"in MODIFY_AS and for layer BP_AIN_SM_M3U_LAYER and subOpr BP_AIN_SM_M3U_PS");
            	liScenario = BP_AIN_SM_CCM_MOD_M3UA_AS;
	          }
          }

          break;
        }

       case ADD_ASP:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ADD_ASP and for layer BP_AIN_SM_M3U_LAYER");
            liScenario = BP_AIN_SM_CCM_ADD_M3UA_ASP;
          }

          break;
        }

      case ENABLE_ALARM:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_TCA_LAYER");
            liScenario = BP_AIN_SM_ENB_TCA_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_ENB_SCC_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_ENB_MTP3_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP2_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_MTP2_LAYER");
            liScenario = BP_AIN_SM_ENB_MTP2_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_M3U_LAYER");
            liScenario = BP_AIN_SM_ENB_M3U_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCT_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_SCT_LAYER");
            liScenario = BP_AIN_SM_ENB_SCT_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TUC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_TUC_LAYER");
            liScenario = BP_AIN_SM_ENB_TUC_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SG_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_SG_LAYER");
            liScenario = BP_AIN_SM_ENB_SG_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SH_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_SH_LAYER");
            liScenario = BP_AIN_SM_ENB_SH_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MR_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_MR_LAYER");
            liScenario = BP_AIN_SM_ENB_MR_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_RY_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_RY_LAYER");
            liScenario = BP_AIN_SM_ENB_RY_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_TCAP_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_PSF_TCAP_LAYER");
            liScenario = BP_AIN_SM_ENB_PSF_TCAP_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_SCCP_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_PSF_SCCP_LAYER");
            liScenario = BP_AIN_SM_ENB_PSF_SCCP_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_PSF_MTP3_LAYER");
            liScenario = BP_AIN_SM_ENB_PSF_MTP3_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_M3UA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_PSF_M3UA_LAYER");
            liScenario = BP_AIN_SM_ENB_PSF_M3UA_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_M3UA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_LDF_M3UA_LAYER");
            liScenario = BP_AIN_SM_ENB_LDF_M3UA_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ALARM and for layer BP_AIN_SM_LDF_MTP3_LAYER");
            liScenario = BP_AIN_SM_ENB_LDF_MTP3_ALM_LEVEL;
          }

          break;
        }

      case DISABLE_ALARM:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_TCA_LAYER");
            liScenario = BP_AIN_SM_DIS_TCA_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_DIS_SCC_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_DIS_MTP3_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP2_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_MTP2_LAYER");
            liScenario = BP_AIN_SM_DIS_MTP2_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_M3U_LAYER");
            liScenario = BP_AIN_SM_DIS_M3U_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCT_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_SCT_LAYER");
            liScenario = BP_AIN_SM_DIS_SCT_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TUC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_TUC_LAYER");
            liScenario = BP_AIN_SM_DIS_TUC_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SG_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_SG_LAYER");
            liScenario = BP_AIN_SM_DIS_SG_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SH_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_SH_LAYER");
            liScenario = BP_AIN_SM_DIS_SH_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MR_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_MR_LAYER");
            liScenario = BP_AIN_SM_DIS_MR_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_RY_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_RY_LAYER");
            liScenario = BP_AIN_SM_DIS_RY_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_TCAP_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_PSF_TCAP_LAYER");
            liScenario = BP_AIN_SM_DIS_PSF_TCAP_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_SCCP_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_PSF_SCCP_LAYER");
            liScenario = BP_AIN_SM_DIS_PSF_SCCP_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_PSF_MTP3_LAYER");
            liScenario = BP_AIN_SM_DIS_PSF_MTP3_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_M3UA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_PSF_M3UA_LAYER");
            liScenario = BP_AIN_SM_DIS_PSF_M3UA_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_M3UA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_LDF_M3UA_LAYER");
            liScenario = BP_AIN_SM_DIS_LDF_M3UA_ALM_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ALARM and for layer BP_AIN_SM_LDF_MTP3_LAYER");
            liScenario = BP_AIN_SM_DIS_LDF_MTP3_ALM_LEVEL;
          }

          break;
        }

      case ENABLE_DEBUG:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_TCA_LAYER");
            liScenario = BP_AIN_SM_ENB_TCA_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_ENB_SCC_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_ENB_MTP3_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP2_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_MTP2_LAYER");
            liScenario = BP_AIN_SM_ENB_MTP2_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_M3U_LAYER");
            liScenario = BP_AIN_SM_ENB_M3U_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCT_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_SCT_LAYER");
            liScenario = BP_AIN_SM_ENB_SCT_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TUC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_TUC_LAYER");
            liScenario = BP_AIN_SM_ENB_TUC_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SG_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_SG_LAYER");
            liScenario = BP_AIN_SM_ENB_SG_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_RY_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_RY_LAYER");
            liScenario = BP_AIN_SM_ENB_RY_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SH_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_SH_LAYER");
            liScenario = BP_AIN_SM_ENB_SH_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MR_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_MR_LAYER");
            liScenario = BP_AIN_SM_ENB_MR_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_TCAP_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_PSF_TCAP_LAYER");
            liScenario = BP_AIN_SM_ENB_PSF_TCAP_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_SCCP_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_PSF_SCCP_LAYER");
            liScenario = BP_AIN_SM_ENB_PSF_SCCP_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_PSF_MTP3_LAYER");
            liScenario = BP_AIN_SM_ENB_PSF_MTP3_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_M3UA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_PSF_M3UA_LAYER");
            liScenario = BP_AIN_SM_ENB_PSF_M3UA_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_M3UA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_LDF_M3UA_LAYER");
            liScenario = BP_AIN_SM_ENB_LDF_M3UA_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_DEBUG and for layer BP_AIN_SM_LDF_MTP3_LAYER");
            liScenario = BP_AIN_SM_ENB_LDF_MTP3_DBG_LEVEL;
          }

          break;
        }

      case ENABLE_TRACE:
        {
	  if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
	  {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_TRACE and for layer BP_AIN_SM_TCA_LAYER");
            liScenario = BP_AIN_SM_ENB_TCA_TRC_LEVEL;
          } 
	  else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_TRACE and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_ENB_SCC_TRC_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_TRACE and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_ENB_MTP3_TRC_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP2_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_TRACE and for layer BP_AIN_SM_MTP2_LAYER");
            liScenario = BP_AIN_SM_ENB_MTP2_TRC_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_TRACE and for layer BP_AIN_SM_M3U_LAYER");
            liScenario = BP_AIN_SM_ENB_M3U_TRC_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCT_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_TRACE and for layer BP_AIN_SM_SCT_LAYER");
            liScenario = BP_AIN_SM_ENB_SCT_TRC_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TUC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_TRACE and for layer BP_AIN_SM_TUC_LAYER");
            liScenario = BP_AIN_SM_ENB_TUC_TRC_LEVEL;
          }

	 break;
        }
      case ENABLE_LOCAL_SSN:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_LOCAL_SSN and for layer BP_AIN_SM_TCA_LAYER");

            liScenario = BP_AIN_SM_ENABLE_TCAP_SSN;
          }
          break;
        }
      case ENABLE_USERPART:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_USERPART and for layer BP_AIN_SM_SCC_LAYER");

            liScenario = BP_AIN_SM_ENABLE_SCC_USRPART;
          }
          break;
        }
      case ENABLE_ENDPOINT:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ENDPOINT and for layer BP_AIN_SM_M3U_LAYER");

            liScenario = BP_AIN_SM_ENABLE_M3UA_EP;
          }
	  else
          {
            logger.logMsg (TRACE_FLAG, 0,"in ENABLE_ENDPOINT and for layer BP_AIN_SM_SCT_LAYER");

            liScenario = BP_AIN_SM_ENABLE_SCTP_EP;
          }
          break;
        }

      case DISABLE_DEBUG:
        {
	        if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
	        {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_TCA_LAYER");
            liScenario = BP_AIN_SM_DISB_TCA_DBG_LEVEL;
          }
	        else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
	        {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_DISB_SCC_DBG_LEVEL;
          }
	        else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
	        {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_DISB_MTP3_DBG_LEVEL;
          }
	        else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP2_LAYER)
	        {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_MTP2_LAYER");
            liScenario = BP_AIN_SM_DISB_MTP2_DBG_LEVEL;
          }
	        else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
	        {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_M3U_LAYER");
            liScenario = BP_AIN_SM_DISB_M3U_DBG_LEVEL;
          }
	        else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCT_LAYER)
	        {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_SCT_LAYER");
            liScenario = BP_AIN_SM_DISB_SCT_DBG_LEVEL;
          }
	        else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TUC_LAYER)
	        {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_TUC_LAYER");
            liScenario = BP_AIN_SM_DISB_TUC_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SG_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_SG_LAYER");
            liScenario = BP_AIN_SM_DISB_SG_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_RY_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_RY_LAYER");
            liScenario = BP_AIN_SM_DISB_RY_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SH_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_SH_LAYER");
            liScenario = BP_AIN_SM_DISB_SH_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MR_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_MR_LAYER");
            liScenario = BP_AIN_SM_DISB_MR_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_TCAP_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_PSF_TCAP_LAYER");
            liScenario = BP_AIN_SM_DISB_PSF_TCAP_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_SCCP_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_PSF_SCCP_LAYER");
            liScenario = BP_AIN_SM_DISB_PSF_SCCP_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_PSF_MTP3_LAYER");
            liScenario = BP_AIN_SM_DISB_PSF_MTP3_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_PSF_M3UA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_PSF_M3UA_LAYER");
            liScenario = BP_AIN_SM_DISB_PSF_M3UA_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_M3UA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_LDF_M3UA_LAYER");
            liScenario = BP_AIN_SM_DISB_LDF_M3UA_DBG_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_LDF_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_DEBUG and for layer BP_AIN_SM_LDF_MTP3_LAYER");
            liScenario = BP_AIN_SM_DISB_LDF_MTP3_DBG_LEVEL;
          }
 
         break;
        }
      case DISABLE_TRACE:
        {
	  if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
	  {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_TRACE and for layer BP_AIN_SM_TCA_LAYER");
            liScenario = BP_AIN_SM_DISB_TCA_TRC_LEVEL;
          } 
	  else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_TRACE and for layer BP_AIN_SM_SCC_LAYER");
            liScenario = BP_AIN_SM_DISB_SCC_TRC_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_TRACE and for layer BP_AIN_SM_MTP3_LAYER");
            liScenario = BP_AIN_SM_DISB_MTP3_TRC_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP2_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_TRACE and for layer BP_AIN_SM_MTP2_LAYER");
            liScenario = BP_AIN_SM_DISB_MTP2_TRC_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_TRACE and for layer BP_AIN_SM_M3U_LAYER");
            liScenario = BP_AIN_SM_DISB_M3U_TRC_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCT_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_TRACE and for layer BP_AIN_SM_SCT_LAYER");
            liScenario = BP_AIN_SM_DISB_SCT_TRC_LEVEL;
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TUC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_TRACE and for layer BP_AIN_SM_TUC_LAYER");
            liScenario = BP_AIN_SM_DISB_TUC_TRC_LEVEL;
          }

	 break;
        }
      case DISABLE_LOCAL_SSN:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_LOCAL_SSN and for layer BP_AIN_SM_TCA_LAYER");

            liScenario = BP_AIN_SM_DISABLE_TCAP_SSN;
          }
          break;
        }
      case DISABLE_USERPART:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_USERPART and for layer BP_AIN_SM_SCC_LAYER");

            liScenario = BP_AIN_SM_DISABLE_SCC_USRPART;
          }
          break;
        }
      case DISABLE_ENDPOINT:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ENDPOINT and for layer BP_AIN_SM_M3U_LAYER");

            liScenario = BP_AIN_SM_DISABLE_M3UA_EP;
          }
	  else
          {
            logger.logMsg (TRACE_FLAG, 0,"in DISABLE_ENDPOINT and for layer BP_AIN_SM_SCT_LAYER");

            liScenario = BP_AIN_SM_DISABLE_SCTP_EP;
          }
          break;
        }

      case ASSOC_UP:
        {
          liScenario = BP_AIN_SM_CCM_M3UA_ASSOC_UP;
          logger.logMsg (TRACE_FLAG, 0,"ASSOC_UP for BP_AIN_SM_M3UA_LAYER layer");
          break;
        }
      case SEND_INIT:
        {
          liScenario = BP_AIN_SM_CCM_M3UA_ASSOC_UP;
          logger.logMsg (TRACE_FLAG, 0,"SEND_INIT for BP_AIN_SM_M3UA_LAYER layer");
          break;
        }
#ifdef INC_DLG_AUDIT
      case AUDIT_INC:
        {
          miAuditCounter++;
          liScenario = BP_AIN_SM_CCM_AUDIT_INC;
          logger.logMsg (TRACE_FLAG, 0,"AUDIT_INC for BP_AIN_SM_TCA_LAYER layer");
          break;
        }
#endif
      case ASSOC_DOWN:
        {
          liScenario = BP_AIN_SM_CCM_M3UA_ASSOC_DOWN;
          logger.logMsg (TRACE_FLAG, 0,"ASSOC_DOWN for BP_AIN_SM_M3UA_LAYER layer");
          break;
        }
      case ASP_UP:
        {
          liScenario = BP_AIN_SM_CCM_M3UA_ASP_UP;
          logger.logMsg (TRACE_FLAG, 0,"ASP_UP for BP_AIN_SM_M3UA_LAYER layer");
          break;
        }
      case ASP_DOWN:
        {
          liScenario = BP_AIN_SM_CCM_M3UA_ASP_DOWN;
          logger.logMsg (TRACE_FLAG, 0,"ASP_DOWN for BP_AIN_SM_M3UA_LAYER layer");
          break;
        }
      case ASP_ACTIVE:
        {
          liScenario = BP_AIN_SM_CCM_M3UA_ASP_ACTIVE;
          logger.logMsg (TRACE_FLAG, 0,"ASP_ACTIVE for BP_AIN_SM_M3UA_LAYER layer");
          break;
        }
      case ASP_INACTIVE:
        {
          liScenario = BP_AIN_SM_CCM_M3UA_ASP_INACTIVE;
          logger.logMsg (TRACE_FLAG, 0,"ASP_INACTIVE for BP_AIN_SM_M3UA_LAYER layer");
          break;
        }
#ifdef INC_ASP_SNDDAUD
      case SEND_DAUD:
        {
          liScenario = BP_AIN_SM_CCM_M3UA_SNDDAUD;
          logger.logMsg (TRACE_FLAG, 0,"SEND_DAUD for BP_AIN_SM_M3UA_LAYER layer");
          break;
        }
#endif

      case STA_LINK:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            liScenario = BP_AIN_SM_CCM_MTP3_STA_LINK;
            logger.logMsg (TRACE_FLAG, 0,"STA_LINK for BP_AIN_SM_MTP3_LAYER layer");
          }
          else
          {
            liScenario = BP_AIN_SM_CCM_MTP2_STA_LINK;
            logger.logMsg (TRACE_FLAG, 0,"STA_LINK for BP_AIN_SM_MTP2_LAYER layer");
          }
          break;
        }
      case STA_LINKSET:
        {
          liScenario = BP_AIN_SM_CCM_MTP3_STA_LINKSET;
          logger.logMsg (TRACE_FLAG, 0,"STA_LINKSET for BP_AIN_SM_MTP3_LAYER layer");
          break;
        }
      case STA_ROUTE:
        {
          liScenario = BP_AIN_SM_CCM_SCCP_STA_ROUTE;
          logger.logMsg (TRACE_FLAG, 0,"STA_ROUTE for BP_AIN_SM_SCC_LAYER layer");
          break;
        }
      case STA_PS:
        {
          liScenario = BP_AIN_SM_CCM_M3UA_STA_PS;
          logger.logMsg (TRACE_FLAG, 0,"STA_PS for BP_AIN_SM_M3U_LAYER layer");
          break;
        }
      case STA_PSP:
        {
          liScenario = BP_AIN_SM_CCM_M3UA_STA_PSP;
          logger.logMsg (TRACE_FLAG, 0,"STA_PSP for BP_AIN_SM_M3U_LAYER layer");
          break;
        }
      case STA_NODE:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SG_LAYER)
          {
            if(apQueMsg->t.stackData.req.u.nodeStatus.entId == ENTST)
            {
              logger.logMsg (TRACE_FLAG, 0,"in STATUS_NODE and for layer BP_AIN_SM_TCA_LAYER");
              liScenario = BP_AIN_SM_CCM_TCA_STA_NODE;
            }
            else if(apQueMsg->t.stackData.req.u.nodeStatus.entId == ENTSP)
            {
              logger.logMsg (TRACE_FLAG, 0,"in STATUS_NODE and for layer BP_AIN_SM_SCC_LAYER");
              liScenario = BP_AIN_SM_CCM_SCC_STA_NODE;
            }
            else if(apQueMsg->t.stackData.req.u.nodeStatus.entId == ENTSN) 
            {
              logger.logMsg (TRACE_FLAG, 0,"in STATUS_NODE and for layer BP_AIN_SM_MTP3_LAYER");
              liScenario = BP_AIN_SM_CCM_MTP3_STA_NODE;
            }
            else if(apQueMsg->t.stackData.req.u.nodeStatus.entId == ENTSD) 
            {
              logger.logMsg (TRACE_FLAG, 0,"in STATUS_NODE and for layer BP_AIN_SM_MTP2_LAYER");
              liScenario = BP_AIN_SM_CCM_MTP2_STA_NODE;
            }
            else if(apQueMsg->t.stackData.req.u.nodeStatus.entId == ENTIT) 
            {
              logger.logMsg (TRACE_FLAG, 0,"in STATUS_NODE and for layer BP_AIN_SM_M3U_LAYER");
              liScenario = BP_AIN_SM_CCM_M3U_STA_NODE;
            }
            else if(apQueMsg->t.stackData.req.u.nodeStatus.entId == ENTSB) 
            {
              logger.logMsg (TRACE_FLAG, 0,"in STATUS_NODE and for layer BP_AIN_SM_SCT_LAYER");
              liScenario = BP_AIN_SM_CCM_SCT_STA_NODE;
            }
            else if(apQueMsg->t.stackData.req.u.nodeStatus.entId == ENTHI) 
            {
              logger.logMsg (TRACE_FLAG, 0,"in STATUS_NODE and for layer BP_AIN_SM_TUC_LAYER");
              liScenario = BP_AIN_SM_CCM_TUC_STA_NODE;
            }
          }
          break;
        }
        case GET_STATS:
        {
          if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_TCA_LAYER)
          {
            liScenario = BP_AIN_SM_CCM_STS_TCA;
            logger.logMsg (TRACE_FLAG, 0,"GET_STATS for BP_AIN_SM_TCA_LAYER ");
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCC_LAYER)
          {
            liScenario = BP_AIN_SM_CCM_STS_SCC;
            logger.logMsg (TRACE_FLAG, 0,"GET_STATS for BP_AIN_SM_SCC_LAYER ");
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
          {
            liScenario = BP_AIN_SM_CCM_STS_MTP3;
            logger.logMsg (TRACE_FLAG, 0,"GET_STATS for BP_AIN_SM_MTP3_LAYER ");
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
          {
            liScenario = BP_AIN_SM_CCM_STS_M3U;
            logger.logMsg (TRACE_FLAG, 0,"GET_STATS for BP_AIN_SM_M3UA_LAYER");
          }
          else if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SCT_LAYER)
          {
            liScenario = BP_AIN_SM_CCM_STS_SCT;
            logger.logMsg (TRACE_FLAG, 0,"GET_STATS for BP_AIN_SM_SCT_LAYER");
          }

          break;
        }

    }

    if (lpReq->start (liScenario, apQueMsg) == BP_AIN_SM_SCENARIO_COMP)
    {
      logger.logMsg (ERROR_FLAG, 0,
          "Scenario FAILED");

      //unblock the CCM thread which is blocked by the wrapper
      INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
      notifyCCM (lpCtx);

      //delete the request object created for the scenario
      mpRequestTable->removeRequest (liTransId, lpReq);
      if (lpReq)
        delete lpReq;

      return BP_AIN_SM_FAIL;
    }
  }
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::handleConfigure");

  return BP_AIN_SM_OK;

}

/******************************************************************************
*
*     Fun:   handleOidChanged()
*
*     Desc:  function to handle oidChanged request from the CCM
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::handleOidChanged(INGwSmQueueMsg *apQueMsg)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::handleOidChanged");

  int liScenario = -1;

  /*
   * We need to delete the oid and value string allocated in the wrapper
   */

  //get the OID string
  char *lpcOid = apQueMsg->t.ccmOper.ccmOp.oidInfo.mpcOid;

  //get the OID Value String
  char *lpcValue = apQueMsg->t.ccmOper.ccmOp.oidInfo.mpcValue;

  //get a new transaction id for the scenario
  int liTransId = mpRepository->getTransactionId();

  //create a new request for this scenario
  INGwSmRequest *lpReq = new INGwSmRequest (liTransId,
                                apQueMsg, this);

  //add the request into the request table
 if (mpRequestTable->addRequest (liTransId, lpReq) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to add request in the request Table <%d>", liTransId);

    if (lpcOid)
      delete [] lpcOid;
    if (lpcValue)
      delete [] lpcValue;

    //unblock the CCM thread which is blocked by the wrapper
    INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
    notifyCCM (lpCtx);

    //delete the request object created for the scenario
    if (lpReq)
      delete lpReq;
    return BP_AIN_SM_FAIL;
  }

     
  if (lpcOid == 0 || lpcValue == 0)
  { 
    logger.logMsg (ERROR_FLAG, 0,
      "Scenario failed as oid and value were NULL");

    //unblock the CCM thread which is blocked by the wrapper
    INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
    notifyCCM (lpCtx);
    
    //delete the request object created for the scenario
    mpRequestTable->removeRequest (liTransId, lpReq);
    if (lpReq)
      delete lpReq;
     
    return BP_AIN_SM_FAIL;
  } 
    
  /*
   * TBD : Need to find out the scenario possible here before passing to request
   */
  if (lpReq->start (liScenario) == BP_AIN_SM_SCENARIO_COMP)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Scenario FAILED");

    //unblock the CCM thread which is blocked by the wrapper
    INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
    notifyCCM (lpCtx);

    if (lpcOid)
      delete [] lpcOid;
    if (lpcValue)
      delete [] lpcValue;

    //delete the request object created for the scenario
    mpRequestTable->removeRequest (liTransId, lpReq);
    if (lpReq)
      delete lpReq;

    return BP_AIN_SM_FAIL;
  }

  if (lpcOid)
    delete [] lpcOid;
  if (lpcValue)
    delete [] lpcValue;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::handleOidChanged");

  return BP_AIN_SM_OK;

}

/******************************************************************************
*
*     Fun:   handlePeerFailed()
*
*     Desc:  this will send the ASPUP and ASPAC to the SG when the CCM fails over
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::handlePeerFailed(INGwSmQueueMsg *apQueMsg)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::handlePeerFailed");

  int liScenario = -1;

	// rajeev
#if _TO_BE_REMOVED_
  //proceed only if the SM is in secondary mode
  if (mbIsPrimary)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Stack Manager is already in Primary Mode");

    if (apQueMsg->mSrc == BP_AIN_SM_SRC_EMS)
    {
      INGwSmBlockingContext *lpBlockCtx = new INGwSmBlockingContext;
      lpBlockCtx->requestId = apQueMsg->t.stackData.miRequestId;
      lpBlockCtx->returnValue = BP_AIN_SM_FAIL;
      ss7SigtranStackRespPending = 0;
      notifyCCM (lpBlockCtx);
    }

    delete apQueMsg;
    apQueMsg = 0;
    return BP_AIN_SM_FAIL;
  }
#endif

  //set the stack manager to primary
  //moveToPrimary ();

  //Ss7SigtranSubsReq *req = new Ss7SigtranSubsReq;
  //cmMemset ((U8*)req, 0, sizeof (Ss7SigtranSubsReq));

  //get the new transaction Id
  int liTransId = mpRepository->getTransactionId();

  //create a new request object for this scenario
  INGwSmRequest *lpReq = new INGwSmRequest (liTransId,
        apQueMsg, this);

  if ((apQueMsg->t.stackData.cmdType == ENABLE_NODE) || 
      (apQueMsg->t.stackData.cmdType == DISABLE_NODE) || 
      (apQueMsg->t.stackData.cmdType == ABORT_SG_TRANS)) {
      lpReq->miTransactionId = BP_AIN_SM_SG_TRANSID;
      liTransId = BP_AIN_SM_SG_TRANSID;
    }

  //add the request in the request table
  if (mpRequestTable->addRequest (liTransId, lpReq) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
        "Unable to add request in the request Table <%d>", liTransId);

   //unblock the CCM thread which is blocked by the wrapper
    INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
    notifyCCM (lpCtx);

    //delete the request object created for the scenario
    if (lpReq)
      delete lpReq;
    return BP_AIN_SM_FAIL;
  }

  switch(apQueMsg->t.stackData.cmdType)
  {

    case ABORT_SG_TRANS:
    {
      if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SG_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,"in ABORT_SG_TRANS and for layer BP_AIN_SM_SG_LAYER");
        liScenario = BP_AIN_SM_SG_ABORT_TRANS;
      }
      break;
    }

    case DISABLE_NODE:
    {
      if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SG_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,"in DISABLE_NODE for layer BP_AIN_SM_SG_LAYER");
        liScenario = BP_AIN_SM_SG_DISABLE_NODE;
      }
      break;
    }

    case ENABLE_NODE:
    {
      if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_SG_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,"in ENABLE_NODE for layer BP_AIN_SM_SG_LAYER");
        liScenario = BP_AIN_SM_SG_ENABLE_NODE;
      }
    }
    break;

    case DISABLE_LINK:
    {
      if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_MTP3_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,"DISABLE_LINK for layer BP_AIN_SM_MTP3_LAYER");
        liScenario = BP_AIN_SM_DISABLE_MTP3_LINK;
      }
      else
      {
        logger.logMsg (TRACE_FLAG, 0,"DISABLE_LINK for layer BP_AIN_SM_MTP2_LAYER");
        liScenario = BP_AIN_SM_DISABLE_MTP2_LINK;
      }
      
    }
    break;

    case ASSOC_DOWN:
    {
      if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,"ASSOC_DOWN for layer BP_AIN_SM_M3U_LAYER");
        liScenario = BP_AIN_SM_CCM_M3UA_ASSOC_DOWN;
      }
    }
    break;
    
    case ASP_DOWN:
    {
      if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,"ASP_DOWN for layer BP_AIN_SM_M3U_LAYER");
        liScenario = BP_AIN_SM_CCM_M3UA_ASP_DOWN;
      }
    }
    break;

    case ASP_INACTIVE:
    {
      if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,"ASP_INACTIVE for layer BP_AIN_SM_M3U_LAYER");
        liScenario = BP_AIN_SM_CCM_M3UA_ASP_INACTIVE;
      }
    }
    break;

    case ASSOC_ABRT:
    {
      if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,"ASSOC_ABRT for layer BP_AIN_SM_M3U_LAYER");
        liScenario = BP_AIN_SM_CCM_M3UA_ASSOC_ABRT;
      }
    }
    break;

    case UNBIND:
    {
      if(apQueMsg->t.stackData.stackLayer == BP_AIN_SM_M3U_LAYER)
      {
        logger.logMsg (TRACE_FLAG, 0,"SCTSAP UNBIND for layer BP_AIN_SM_M3U_LAYER");
        liScenario = BP_AIN_SM_CCM_M3UA_UNBIND;
      }
    }
    break;
    
    default:
        logger.logMsg (ERROR_FLAG, 0,"INVALID cmdType<%d>",
               apQueMsg->t.stackData.cmdType);
  }

  //start the processing for the scenario
  if (lpReq->start (liScenario, apQueMsg) == BP_AIN_SM_SCENARIO_COMP)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Scenario FAILED");

    //unblock the CCM thread which is blocked by the wrapper
    INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
    notifyCCM (lpCtx);

    //delete the request object created for the scenario
    mpRequestTable->removeRequest (liTransId, lpReq);
    if (lpReq)
      delete lpReq;

    return BP_AIN_SM_FAIL;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::handlePeerFailed");

  return BP_AIN_SM_OK;

}

/******************************************************************************
*
*     Fun:   handleEnableNode()
*
*     Desc:  this will send the bind request to the MTP
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::handleEnableNode (INGwSmQueueMsg *apQueMsg)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::handleEnableNode ");

  int liScenario = BP_AIN_SM_ENABLE_NODE;

  //proceed only if the SM is in secondary mode
  if (!mbIsPrimary)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Stack Manager is not in Primary Mode");

    if (apQueMsg->mSrc == BP_AIN_SM_SRC_CCM)
    {
      INGwSmBlockingContext *lpBlockCtx = new INGwSmBlockingContext;
      lpBlockCtx->requestId = apQueMsg->t.ccmOper.miRequestId;
      lpBlockCtx->returnValue = BP_AIN_SM_FAIL;
      notifyCCM (lpBlockCtx);
    }

    delete apQueMsg;
    apQueMsg = NULL;
    return BP_AIN_SM_FAIL;
  }

  //get the new transaction Id
  int liTransId = mpRepository->getTransactionId();

  //create a new request for the scenario
  INGwSmRequest *lpReq = new INGwSmRequest (liTransId,
                                apQueMsg, this);

  //add the request into the request table
 if (mpRequestTable->addRequest (liTransId, lpReq) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to add request in the request Table <%d>", liTransId);

    //unblock the CCM thread which is blocked by the wrapper
    INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
    notifyCCM (lpCtx);

    //delete the request object created for the scenario
    if (lpReq)
      delete lpReq;
    return BP_AIN_SM_FAIL;
  }

  //start the processing for the scenario
  if (lpReq->start (liScenario) == BP_AIN_SM_SCENARIO_COMP)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "handleEnableNode : Scenario FAILED");

    //unblock the CCM thread which is blocked by the wrapper
    INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);
    notifyCCM (lpCtx);

    //delete the request object created for the scenario
    mpRequestTable->removeRequest (liTransId, lpReq);
    if (lpReq)
      delete lpReq;

    return BP_AIN_SM_FAIL;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::handleEnableNode ");

  return BP_AIN_SM_OK;
}



/******************************************************************************
*
*     Fun:   handleAlarm()
*
*     Desc:  this will handle any alarm from the CCM
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::handleAlarm(INGwSmQueueMsg *apQueMsg)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::handleAlarm");


  mpAlmHdlr->handleAlarm (& (apQueMsg->t.alarmMsg));
  delete apQueMsg;
	apQueMsg = NULL;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::handleAlarm");

  return BP_AIN_SM_OK;

}


/******************************************************************************
*
*     Fun:   handleResponse()
*
*     Desc:  this will handle the Confirm and timeout message from the Stack
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::handleResponse(INGwSmQueueMsg *apQueMsg)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::handleResponse. stkMsgTyp<%d>", 
    apQueMsg->t.stackMsg.stkMsgTyp);

  INGwSmRequest *lpReq;
  int liScenario = -1;

  INGwSmStsHdlr *mpStsReq = mpSmWrapper->getStsHdlrInst();

  /*
   * The confirm can be Control, Config, Status, Statistics confirm and  Timeout message
   */
  switch (apQueMsg->t.stackMsg.stkMsgTyp)
  {
    /*
     * For the Control, Config and Status, the processing is similar and hence
     * pass the message to the request created for that scenario.
     * The request can be accessed from the request Table by using the 
     * transaction id as the correlation id
     */
    case BP_AIN_SM_STKOP_CTLCFM:
    case BP_AIN_SM_STKOP_CFGCFM:
    case BP_AIN_SM_STKOP_STACFM:
    {

      //get the transaction id from the stack message
      int liTransId = apQueMsg->t.stackMsg.stkMsg.miTransId;

      /*
       * MTP2 converts the transaction id 1 in the request to 2
       * in the confirm. So we need to use transid 1 whenever
       * we need a response for transid 2.
       */
#if 0
      if (liTransId == BP_AIN_SM_MTP2_TRANSID)
        liTransId = miChangeState2RunningTransId;
#endif
      //retrieve the request object from the request table
      INGwSmRequest *lpReq = 
                    mpRequestTable->getRequest (liTransId);

      if (lpReq)
      {
        /*
         * if the request is present then let it handle the response
         * Check the return value for the handleresponse. In case the scenario
         * is completed then we need to unblock the CCM thread otherwise let the
         * scenario proceed as normally
         */
 
        if (lpReq->handleResponse (apQueMsg) ==
              BP_AIN_SM_SCENARIO_COMP)
        {
          if(lpReq->mpOrigReq)
          {
          if(lpReq->mpOrigReq->mSrc == BP_AIN_SM_SRC_CCM )
          {
             logger.logMsg (TRACE_FLAG, 0,
               "src = %d [CCPU] General Configuration completed so unblocking the CCM Thread",lpReq->mpOrigReq->mSrc);
              logger.logMsg (TRACE_FLAG, 0,
                   "[CCPU] in BP_AIN_SM_SRC_CCM ");
             if(lpReq->mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN)
             { 
              logger.logMsg (TRACE_FLAG, 0,
                   "[CCPU] in GEN_CFG_TXN ");
                //unblock the CCM thread blocked on the wrapper
                INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);

                logger.logMsg (ERROR_FLAG, 0,
                  "<%d> Scenario Completed", liScenario);

                notifyCCM (lpCtx);

                //remove the request from the request table and delete it
                mpRequestTable->removeRequest (liTransId, lpReq);

              }
          //delete the request object
          delete lpReq;
          }
					else if (lpReq->mpOrigReq->mSrc == BP_AIN_SM_SRC_EMS) {

						// Before deleting Request check if it has been remvoed from
						// Request Table. Else do not delete.
						if(mpRequestTable->getRequest(liTransId) == NULL)
						{
							if(lpReq)
								delete lpReq;
						}
					}
          }

          /* 
           * In case the scenario is changestate to stopped, notify the
           * distributor that it needs to cleaup
           */
          if (liScenario == BP_AIN_SM_CCM_CS_STOPPED)
          {
            shutdown ();
          }

          return BP_AIN_SM_OK;
        }
				else
				{
					// memory leak fix
					// check if request has already been removed for request table 
					// then delete request.
						if(mpRequestTable->getRequest(liTransId) == NULL)
						{
							if(lpReq)
								delete lpReq;
						}
				}
      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
          "No Request Found for the response");

        //error scenario since the confirm is received without any request
        delete apQueMsg;
				apQueMsg = NULL;
        return BP_AIN_SM_FAIL;
      }

      break;
    }
    /*
     * Currently timeout is only for statistics. There is a singleton statistics 
     * object which is never deleted. Pass the response to it and let it take care of 
     * the statistics processing
     */
    case BP_AIN_SM_STKOP_STSCFM:
    {
      if (mpStsReq)
        mpStsReq->handleResponse (apQueMsg);

      break;
    }
    case BP_AIN_SM_STKOP_TIMEOUT:
    {
      vector <INGwSmTimerNode*>::iterator iter;

      mbAnyTimerDeregistered = false;

      iter = meTimerList.begin ();

      int *lpiNodesAccounted = new int [miMaxTimer + 1];

      for (int count = 0; count < miMaxTimer; count++)
        lpiNodesAccounted [count] = 0;

      while (iter != meTimerList.end())
      {
        //increment the ticks for all the nodes and for nodes which are expired
        //we need to invoke handleResponse

        INGwSmTimerNode *lpTimerNode = *(iter);

        if (lpTimerNode == 0 ||
            lpiNodesAccounted [lpTimerNode->id] == 1)
        {
          iter ++;
          continue;
        }

        ++(lpTimerNode->ticks);

        lpiNodesAccounted [lpTimerNode->id] = 1;

        logger.logMsg (TRACE_FLAG, 0,
          "TimeOut : Request <%d>, Expire <%d>, Ticks <%d>",
          lpTimerNode->requestId, lpTimerNode->expire, lpTimerNode->ticks);

        if (lpTimerNode->ticks >= lpTimerNode->expire)
        {
          lpTimerNode->ticks = 0;

          if (lpTimerNode->requestId == miStsTransId)
          {
            if (mpStsReq)
              mpStsReq->handleResponse (apQueMsg);
          }
          else if (lpTimerNode->requestId == miDistTransId)
          {
            monitorResource ();
          }
          else
          {
            //retrieve the request object from the request table
            INGwSmRequest *lpReqNode =
                      mpRequestTable->getRequest (lpTimerNode->requestId);

            if (lpReqNode)
            {
              lpReqNode->handleResponse (apQueMsg);
            }
          }
        }

        //if any timer was deregistered then restart from beginning since
        //iterator will be invalid
        if (mbAnyTimerDeregistered == true)
        {
          iter = meTimerList.begin ();

          logger.logMsg (WARNING_FLAG, 0,
            "Timer was deregistered during the handling of timerNode");
        }
        else
        {
          iter ++;
        }
      }

      delete [] lpiNodesAccounted;

      delete (apQueMsg);

      break;
    }
    default:
    {
      logger.logMsg (ERROR_FLAG, 0,
        "unknown stack message type <%d>", apQueMsg->t.stackMsg.stkMsgTyp);
      delete apQueMsg;
			apQueMsg = NULL;
      break;
    }
  }


  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::handleResponse");

  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   handleAlmAction()
*
*     Desc:  to be invoked for AlarmHandler raised alarm action requests
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::handleAlmAction(INGwSmQueueMsg *apQueMsg)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::handleAlmAction");

  /*
   * First we need to check whether there is any request waiting on this alarm
   * and wake up the request to proceed with its state model.
   * In case, there is no request waiting for this alarm, we need to see if
   * a new scenario needs to be created for it.
   */

  pair<INGwSmIntIntMMap::iterator, INGwSmIntIntMMap::iterator> lePair;
  int liTransId;
  int lbReqFound = false;
  int liScenario = -1;
  INGwSmRequestContext *lpReqContext = 0;

  //get the alarm state
  int liAlmState = (apQueMsg->t.almOper.mOpType);

  /*
   * We only wait for ACKS and not FAILURE, so search for the 
   * ACK instead of FAILURE state.
   */

  int liAlmId = liAlmState;

  switch (liAlmState)
  {
    case BP_AIN_SM_ALMOP_ASPAC_FAIL:
    {
      liAlmId = BP_AIN_SM_ALMOP_ASPAC_ACK;
      break;
    }
    case BP_AIN_SM_ALMOP_ASPIA_FAIL:
    {
      liAlmId = BP_AIN_SM_ALMOP_ASPIA_ACK;
      break;
    }
    case BP_AIN_SM_ALMOP_ASPUP_FAIL:
    {
      liAlmId = BP_AIN_SM_ALMOP_ASPUP_ACK;
      break;
    }
    case BP_AIN_SM_ALMOP_ASSOC_ESTAB_FAIL:
    {
      liAlmId = BP_AIN_SM_ALMOP_ASSOC_ESTAB_OK;
      break;
    }
    case BP_AIN_SM_ALMOP_COMM_DOWN:
    {
      liAlmId = BP_AIN_SM_ALMOP_ASSOC_ESTAB_OK;
      break;
    }
  }

  logger.logMsg (VERBOSE_FLAG, 0,
    "Checking the alarm wait map for AlmState <%d> --> almId <%d>",
    liAlmState, liAlmId);

#if TO_BE_REVIEWED_BY_ARCHANA
  lePair = meAlarmWaitMap.equal_range(liAlmId);

  INGwSmIntIntMMap::iterator iter;


#ifdef  _BP_AIN_SM_DMP_
  for (iter = lePair.first; iter != lePair.second; iter++ )
  {
    logger.logMsg (VERBOSE_FLAG, 0,
      "TID <%d> : found waiting for alarm <%d> in Alarm Wait Map",
      iter->second, liAlmId);
  }
#endif

  iter = lePair.first;

  while (iter != lePair.second)
  {
    liTransId = iter->second;

    //retrieve the request object from the request table
    INGwSmRequest *lpReq =
                    mpRequestTable->getRequest (liTransId);
    
    /*
     * if the request exists then invoke handleAlarm on it
     * and check the return code to be OK
     *
     */
    if (lpReq)
    {
      int liRetVal = lpReq->handleAlarm (apQueMsg, liAlmState);
      if (liRetVal == BP_AIN_SM_SCENARIO_COMP)
      {
        //request has been found for this alarm
        lbReqFound = true;

        //unblock the CCM thread blocked on the wrapper
        INGwSmBlockingContext *lpCtx = lpReq->getBlockingContext (liScenario);

        logger.logMsg (ERROR_FLAG, 0,
          "<%d> Scenario Completed", liScenario);

        notifyCCM (lpCtx);

        //remove the request from the request table and delete it
        mpRequestTable->removeRequest (liTransId, lpReq);
        delete lpReq;
          
        /*
         * In case the scenario is changestate to stopped, notify the
         * distributor that it needs to cleaup
         */
        if (liScenario == BP_AIN_SM_CCM_CS_STOPPED)
        {
          shutdown ();
        }

        //remove the request from the alarm wait map
        meAlarmWaitMap.erase (iter++);

        lePair = meAlarmWaitMap.equal_range(liAlmId);

        iter = lePair.first;
        continue;

      }
      else if (liRetVal == BP_AIN_SM_SEND_NEXT)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : is not waiting for alarm <%d> for this PSP",
          liTransId, liAlmState);
      }
      else if (liRetVal == BP_AIN_SM_FAIL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> : is not waiting for alarm <%d",
          liTransId, liAlmState);
      }
      else
      {
        //request has been found for this alarm
        lbReqFound = true;

        //remove the request from the alarm wait map
        meAlarmWaitMap.erase (iter++);

        lePair = meAlarmWaitMap.equal_range(liAlmId);

        iter = lePair.first;
        continue;
      }

    }
    iter++;
  }
#endif //END OF TO_BE_REVIEWED_BY_ARCHANA

  //reset the scenario
  liScenario = -1;

  /*
   * if there was no existing request for this alarm then find if a 
   * scenario needs to be created for it or not. and then do the needful.
   *
   */
  if (lbReqFound == false)
  {
    logger.logMsg (WARNING_FLAG, 0,
      "No request is found for this Alarm <%d>", liAlmState);

    //if a scenario will be created for this alarm
    bool lbScenarioCreated = false;

    /*
     * If the ASP is in ASP_ACTIVE/ASP_INACTIVE state and a ASP_DOWN_ACK
     * is received when no ASP_DOWN was sent then need to initiate 
     * procedures to bring the ASP back to original state wrt PSP.
     *
     */
    if (liAlmState == BP_AIN_SM_ALMOP_ASPDN_ACK)
    {
      //retrieve the PSP Id from the ASPM context
      int liPspId = apQueMsg->t.almOper.almOp.aspmInfo.pspId;

      INGwSmPspState *lpPspState = mpRepository->getPspState (liPspId);

      if (lpPspState == 0)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "PSP Id <%d> could not be located in PSP State Table",
          liPspId);

        // Mriganka - deleted apQueMsg
        delete apQueMsg;
        apQueMsg = NULL;
        return BP_AIN_SM_FAIL;
      }

      if (lpPspState->state == BP_AIN_SM_PSP_ST_ASPAC)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "ASP_DOWN_ACK received in ASP_ACTIVE state for PSP <%d>",
          liPspId);

        liScenario = BP_AIN_SM_CCM_ASP_ACTIVE;

        lbScenarioCreated = true;

        //we need to set the ASP to the state SG thinks it is in
        if (mpRepository->setPspState (liPspId, BP_AIN_SM_PSP_ST_ESTASS)
                          != BP_AIN_SM_OK)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "The PSP <%d> doesn't exist in the PSP Map", liPspId);
        }

        //set the request Context to PSPid
        lpReqContext = new INGwSmRequestContext;
        lpReqContext->pspId = liPspId;
      }
      else if (lpPspState->state == BP_AIN_SM_PSP_ST_ASPUP)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "ASP_DOWN_ACK received in ASP_UP state for PSP <%d>",
          liPspId);

        liScenario = BP_AIN_SM_CCM_ASP_UP;

        //we need to set the ASP to the state SG thinks it is in
        if (mpRepository->setPspState (liPspId, BP_AIN_SM_PSP_ST_ESTASS)
                          != BP_AIN_SM_OK)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "The PSP <%d> doesn't exist in the PSP Map", liPspId);
        }

        lbScenarioCreated = true;

        //set the request Context to PSPid
        lpReqContext = new INGwSmRequestContext;
        lpReqContext->pspId = liPspId;
      }
    }

    /*
     * In case it is neither then we can sync up the states between
     * Stack and the SM
     */

    if (lbScenarioCreated == false)
    {
      logger.logMsg (WARNING_FLAG, 0,
        "No Scenario was created for this Alarm, syncing up the PSP State");

    }

    //if(liAlmState == BP_AIN_SM_ALMOP_ASSOC_ESTAB_FAIL || liAlmState == BP_AIN_SM_ALMOP_COMM_DOWN || liAlmState == BP_AIN_SM_ALMOP_ASPUP_FAIL)
    if(liAlmState == BP_AIN_SM_ALMOP_COMM_DOWN )
    {
      int liPspId = 0, lisctSuId = 0;
      if(liAlmState == BP_AIN_SM_ALMOP_COMM_DOWN){
        liPspId = apQueMsg->t.almOper.almOp.commDn.pspId;
        lisctSuId = apQueMsg->t.almOper.almOp.commDn.sctSuId;
      }
      else{
        liPspId = apQueMsg->t.almOper.almOp.pspInfo.pspId;
        lisctSuId = apQueMsg->t.almOper.almOp.pspInfo.sctSuId;
      }
     
      INGwSmPspState *lpState = mpRepository->getPspState ( liPspId );
      if (lpState == 0)
      {
        logger.logMsg (ERROR_FLAG, 0,
              "Psp <%d> could not be found in PSP State Table",
                liPspId);
      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
             "assocUp_retry called for alarm : %d",liAlmState);
        if(assocUp_retry (liPspId, lisctSuId) != BP_AIN_SM_OK)
        {
          logger.logMsg (ERROR_FLAG, 0,
             "Unable to retry assoc Up");
        }
      }
    }
  }
  //else
  //{
    //free the Queue Message
    delete apQueMsg;
    apQueMsg = NULL;
  //}

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::handleAlmAction");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*     
*     Fun:   syncPspState()
*       
*     Desc:  sync up the PSP state in Stack and SM
*         
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::syncPspState ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::syncPspState");

  int liScenario = BP_AIN_SM_SYNC_PSP_STA;

  //get a new transaction id for this scenario
  int liTransId = mpRepository->getTransactionId();
    
  //create a new request for it
  INGwSmRequest *lpReq = new INGwSmRequest (liTransId,
                                0, this);
                                
  //add the request into the request table
  if (mpRequestTable->addRequest (liTransId, lpReq) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to add request in the request Table <%d>", liTransId);
      
    //delete the request object created for the scenario
    delete lpReq;
    return BP_AIN_SM_FAIL;
  } 
  

  //handle the request object
  if (lpReq->start (liScenario) == BP_AIN_SM_SCENARIO_COMP)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Scenario FAILED");

    //delete the request object created for the scenario
    mpRequestTable->removeRequest (liTransId, lpReq);
    delete lpReq;

    return BP_AIN_SM_FAIL;
  }

  printf("\n INGwSmDistributor: syncPspState() called");
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::syncPspState");

  return BP_AIN_SM_OK;
}



/******************************************************************************
*     
*     Fun:   assocUp_retry()
*       
*     Desc: ASSOC UP 
*         
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::assocUp_retry (int pspId, int sctSuId)
{
  int retVal = BP_AIN_SM_OK;
  int EpId = 0;

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::assocUp_retry pspId<%d> sctSuId<%d>",
    pspId, sctSuId);

  /** Submit the assoc Up to wrapper queue **/
#if 0  //no need to fill it here, processEms will search it in blkconfig
  M3uaAssocUpSeq* assoc = INGwSmBlkConfig::getInstance().getM3uaAssocUpList();
  if(assoc->size() == 0){
		logger.logMsg (TRACE_FLAG, 0, 
		"No entry in map of BlkConfig");
		return retVal;
	}

	M3uaAssocUpSeq::iterator it;
	for(it=assoc->begin(); it != assoc->end(); ++it) 
	{
		if((*it).pspId == pspId) 
		{
      logger.logMsg (ALWAYS_FLAG, 0, 
		    "Find pspId <%d> in assocUpList",pspId);

      /** Now find the sctSuId in endpoint sequence of blkConfig and if it matches 
              with assocUp EP then need to initiate assocUp rety**/

      EpSeq* ep = INGwSmBlkConfig::getInstance().getEpList();
      if(ep->size() == 0){
		    logger.logMsg (TRACE_FLAG, 0, 
		    "No entry in map of BlkConfig");
		    return retVal;
	    }

      EpSeq::iterator itEp;
	    for(itEp=ep->begin(); itEp != ep->end(); ++itEp) 
	    {
		    if(((*itEp).sctpLsapId == sctSuId) && ((*itEp).endPointid == (*it).endPointId))
		    {
          EpId = (*itEp).endPointid;
          logger.logMsg (ALWAYS_FLAG, 0, 
		        "Found EndPointId <%d> corresponding to SctSuId <%d> and pspId <%d> in EP List ",EpId, (*itEp).sctpLsapId, pspId);
			    break;
		    }
	    }

      if((*it).endPointId == EpId)
      {
#endif
      int index = (((pspId) << 4) | sctSuId);

      //INGwSmPspMap::iterator iter = mePspState.find(index);
      int state = mpSmWrapper->getPspMap(index); 
      
      if((state == ASSOC_STATE_UNKNOWN) || (state == ASSOCUP_FAIL)){
        //sleep(1);
        M3uaAssocUp *assocUp = new M3uaAssocUp;
        memset(assocUp, '\0', sizeof(M3uaAssocUp));
        assocUp->pspId = pspId;
        assocUp->m3uaLsapId = sctSuId;
        assocUp->isRetry = true;

        Ss7SigtranSubsReq *req = new Ss7SigtranSubsReq;
        memset ((void *)req, 0, sizeof(Ss7SigtranSubsReq));

        vector<int> lprocIdList;

		    memcpy(&(req->u.m3uaAssocUp), assocUp, sizeof(M3uaAssocUp));
        delete assocUp;
        assocUp = NULL;

        lprocIdList.push_back(INGwSmBlkConfig::getInstance().getAssocProcId(req->u.m3uaAssocUp));
		    req->cmd_type  = ASSOC_UP;
        INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
        lqueueMsg->req = req;
        lqueueMsg->src = BP_AIN_SM_SRC_CCM;
        lqueueMsg->procIdList = lprocIdList;
        lqueueMsg->from       = 2;

        logger.logMsg (ALWAYS_FLAG, 0, 
		      "assocUp_retry()::Posting ASSOC_UP with  "
          "pspId <%d>, m3uaLsap <%d>",
          lqueueMsg->req->u.m3uaAssocUp.pspId,
          lqueueMsg->req->u.m3uaAssocUp.m3uaLsapId );

        mpSmWrapper->postMsg(lqueueMsg,true);

      }
      else{
        logger.logMsg (ALWAYS_FLAG, 0, 
		      "assocUp_retry()::Not posting ASSOC_UP, State of PSP is <%d>",state);
      }

      //}//commented open bracket
      
		//} //commented open bracket
	//}//commented open bracket

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::assocUp_retry");

    return BP_AIN_SM_OK;
}




/******************************************************************************
*
*     Fun:   initStats()
*
*     Desc:  create the staticsti handler and let the games begin
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::initStats ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::initStats");

  INGwSmStsHdlr *mpStsReq = NULL;
  mpStsReq = mpSmWrapper->getStsHdlrInst(); 

	// rajeev
	if(mpStsReq == NULL)
	{
		logger.logMsg(ERROR_FLAG, 0, "INGwSmStsHdlr Pointer is NULL, returning");
    return BP_AIN_SM_FAIL;
	}

#if _TO_B_REMOVED_
//Now this class initializes from Wrapper initializeStats also, so removing the below check
  /*if (mpStsReq)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Statistics Object has already been initialized");
    return BP_AIN_SM_FAIL;
  }*/

  if (mpStsReq == 0)
  {
    mpStsReq = new INGwSmStsHdlr (this,0);

    mpStsReq->initialize (miStsTransId);
  }
#endif

  //get the transaction id used for statistics object
  miStsTransId = mpRepository->getStsTransId ();

  /*
   * register timer for the statistics handler
   */
  int liDuration = mpRepository->getStsTimer ();

  if (registerTimer (liDuration, miStsTransId) == BP_AIN_SM_FAIL)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "registerTimer Failed for the statistics");

    return BP_AIN_SM_FAIL;
  }

  //let the games begin
  mpStsReq->start ();

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::initStats");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   shutdown()
*     
*     Desc:  shutdown the stack manager
*     
*     Notes: None
*     
*     File:  INGwSmDistributor.C
*     
*******************************************************************************/
int
INGwSmDistributor::shutdown ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::shutdown");
    
  mRunStatus = false;
  INGwSmStsHdlr *mpStsReq = mpSmWrapper->getStsHdlrInst();
  if (mpStsReq)
  {
    mpStsReq->stop ();
  }
  
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::shutdown");

  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   notifyCCM()
*
*     Desc:  this will unblock the CCM thread which is blocked at the wrapper
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::notifyCCM (INGwSmBlockingContext *apCtx)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::notifyCCM");

  logger.logMsg (VERBOSE_FLAG, 0,
    "The requestId <%d> has response code <%d>",
    apCtx->requestId, apCtx->returnValue);

  //invoke continue operation which shud unblock the CCM thread
  mpSmWrapper->continueOperation (apCtx);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::notifyCCM");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   waitForAlarm()
*
*     Desc:  this will insert the request transaction id in alarm wait map
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::waitForAlarm (int aiMinorState, int aiTid)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::waitForAlarm <%d, %d>", aiMinorState, aiTid);

  meAlarmWaitMap.insert (INGwSmIntIntMMap::value_type (aiMinorState, aiTid));

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::waitForAlarm");
  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   removeWaitForAlarm()
*
*     Desc:  this will remove the request transaction id from alarm wait map
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::removeWaitForAlarm (int aiMinorState, int aiTid)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::removeWaitForAlarm <%d, %d>", aiMinorState, aiTid);

  pair<INGwSmIntIntMMap::iterator, INGwSmIntIntMMap::iterator> lePair;

  lePair = meAlarmWaitMap.equal_range(aiMinorState);

  INGwSmIntIntMMap::iterator iter;

  for (iter = lePair.first; iter != lePair.second; iter++)
  {
    if (iter->second == aiTid)
    {
      meAlarmWaitMap.erase (iter++);
      return BP_AIN_SM_OK;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::removeWaitForAlarm");
  return BP_AIN_SM_FAIL;
}

/******************************************************************************
*
*     Fun:   dumpQueueMsg()
*
*     Desc:  this will dump the QueueMsg
*
*     Notes: None
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
void
INGwSmDistributor::dumpQueueMsg (INGwSmQueueMsg *apMsg)
{
  logger.logMsg (TRACE_FLAG, 0,
   "Entering INGwSmDistributor::dumpQueueMsg");

  if (apMsg == 0)
    return;

  if (apMsg->mSrc == BP_AIN_SM_SRC_CCM)
  {
    logger.logMsg (VERBOSE_FLAG, 0,
      "ID <%d> DUMP CCM Message : OpType <%d>, ReqId <%d>",
      apMsg->id, apMsg->t.ccmOper.mOpType, apMsg->t.ccmOper.miRequestId);
  }
  else if (apMsg->mSrc == BP_AIN_SM_SRC_STACK)
  {
    logger.logMsg (VERBOSE_FLAG, 0,
      "ID <%d> DUMP STACK RESPONSE Message : MsgType <%d>, LayerId <%d>, TID <%d>",
      apMsg->id, apMsg->t.stackMsg.stkMsgTyp, apMsg->t.stackMsg.stkMsg.miLayerId,
      apMsg->t.stackMsg.stkMsg.miTransId);
  }
  else if (apMsg->mSrc == BP_AIN_SM_SRC_STACK_ALM)
  {
    logger.logMsg (VERBOSE_FLAG, 0,
      "ID <%d> DUMP ALARM Message : LayerId <%d>",
      apMsg->id, apMsg->t.alarmMsg.almInfo.miLayerId);
  }
  else if (apMsg->mSrc == BP_AIN_SM_SRC_STACK_ALMHDLR)
  {
    logger.logMsg (VERBOSE_FLAG, 0,
      "ID <%d> DUMP ALARM HANDLER Message : OpType <%d>",
      apMsg->id, apMsg->t.almOper.mOpType);
  }

  logger.logMsg (VERBOSE_FLAG, 0,
   "Leaving INGwSmDistributor::dumpQueueMsg");
  return;
}

/******************************************************************************
*
*     Fun:   monitorResource ()
*
*     Desc:  this will check the Connection with PSP and try to bring it up
*
*     Notes: noone
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::monitorResource ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::monitorResource");

  /*
   * First dump the Request Table
   */
  mpRequestTable->dump ();


#if TO_BE_REVIEWED_BY_ARCHANA
  /*
   * Now get the PSP List and bring up any PSP which is in DOWN state
   * NOTE : All PSPs should be in UP state with the CCM.
   */
  vector <INGwSmPeerId> &lrVec = mpRepository->getPSPIdList ();
  vector <INGwSmPeerId>::iterator iter;

  for (iter = lrVec.begin (); iter != lrVec.end() ; iter++)
  {
    int liPspId = *(iter);

    INGwSmPspState *lpPspState = mpRepository->getPspState (liPspId);

    if (lpPspState == 0)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "PSP Id <%d> could not be located in PSP State Table",
        lpPspState);

      return BP_AIN_SM_FAIL;
    }

    if (lpPspState->monitor == 0)
    {
      logger.logMsg (WARNING_FLAG, 0,
        "PSP <%d> is not being monitored by Stack Manager",
        liPspId);
      continue;
    }

    if (lpPspState->state == BP_AIN_SM_PSP_ST_DOWN)
    {
      bringUpPsp (liPspId);
    }
  }
#endif 

  /*
   * Finally dump the memory tables
   */

  unsigned long llAvailMem;

  if (mpRepository->getDumpMemory() == true &&
      SRegInfoShow(BP_AIN_SM_REGION, &llAvailMem) == ROK)
  {
    logger.logMsg (VERBOSE_FLAG, 0,
      "Total Available Stack Memory = <%ld>",
      llAvailMem);
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::monitorResource");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   getLayerVersion ()
*
*     Desc:  this will get the versions of all the layers
*
*     Notes: noone
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::getLayerVersion ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::getLayerVersion");

  int liScenario = BP_AIN_SM_CCM_VERSION;
  
  //get a new transaction id for this scenario
  int liTransId = mpRepository->getTransactionId();
    
  //create a new request for it
  INGwSmRequest *lpReq = new INGwSmRequest (liTransId,
                                0, this);
                                
  //add the request into the request table
  if (mpRequestTable->addRequest (liTransId, lpReq) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to add request in the request Table <%d>", liTransId);
      
    //delete the request object created for the scenario
    delete lpReq;
    return BP_AIN_SM_FAIL;
  } 
    

  //handle the request object
  if (lpReq->start (liScenario) == BP_AIN_SM_SCENARIO_COMP)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Scenario FAILED");

    //delete the request object created for the scenario
    mpRequestTable->removeRequest (liTransId, lpReq);
    delete lpReq;

    return BP_AIN_SM_FAIL;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::getLayerVersion");

  return BP_AIN_SM_OK;
}


/******************************************************************************
*
*     Fun:   bringUpPsp()
*
*     Desc:  this will bring up the PSP
*
*     Notes: this will fake a configure for ASPUP
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::bringUpPsp (int aiPspId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::bringUpPsp");

  /*
   * create a new Queue object and used the request Id as
   * the thread id for the current thread. This will be used by Stack
   * Manager to unblock it if needed.
   */

  INGwSmQueueMsg *lpQMsg = new INGwSmQueueMsg;
  int liRequestId = -1;

  lpQMsg->mSrc = BP_AIN_SM_SRC_CCM;

  lpQMsg->t.ccmOper.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
  lpQMsg->t.ccmOper.miRequestId = liRequestId;


  /*
   * NOTE : The oid and value are allocated here and hence should be
   * freed by the receiver of the message.
   */

  /*
   * now for primary CCM, we need to make is ASPAC and for backup
   * we need to make it ASPUP
   */

  if (mbIsPrimary)
  {
    lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcOid = new char [strlen (SM_CCM_ASP_ACTIVE) + 1];
    strcpy (lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcOid, SM_CCM_ASP_ACTIVE);

    logger.logMsg (ALWAYS_FLAG, 0,
      "PSP <%d> is being moved to state SM_CCM_ASP_ACTIVE", aiPspId);
  }
  else
  {
    lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcOid = new char [strlen (SM_CCM_ASP_UP) + 1];
    strcpy (lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcOid, SM_CCM_ASP_UP);

    logger.logMsg (ERROR_FLAG, 0,
      "PSP <%d> is being moved to state SM_CCM_ASP_UP", aiPspId);
  }

  lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcValue = new char [11];
  sprintf (lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcValue, "%d", aiPspId);


  //post the message to the Distributor
  if (postMsg (lpQMsg) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "In change state to stopped, postMsg failed");

    // Mriganka - deleted oid and value
    if (lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcOid)
      delete [] lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcOid;
    if (lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcValue) 
      delete [] lpQMsg->t.ccmOper.ccmOp.oidInfo.mpcValue;

    delete lpQMsg;
    return BP_AIN_SM_FAIL;
  }
        
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::bringUpPsp");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   startTimer()
*
*     Desc:  this will start up the timer
*
*     Notes: none
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::startTimer ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::startTimer");

  int liDuration = mpRepository->getTimerRes () * 10;

  /* Register timer */
  if (mbIsTimerRunning == false &&
      SRegTmr((Ent)ENTSM, (Inst)BP_AIN_SM_SRC_INST, liDuration,
      iNGwSmActvStsTmr) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SRegTmr() failed");
    return BP_AIN_SM_FAIL;
  }

  mbIsTimerRunning = true;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::startTimer");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   stopTimer()
*
*     Desc:  this will stop the timer 
*
*     Notes: none
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::stopTimer ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::stopTimer");

  int liDuration = mpRepository->getTimerRes () * 10;

  /* Register timer */
  if (mbIsTimerRunning == false &&
      SDeregTmr ((Ent)ENTSM, (Inst)BP_AIN_SM_SRC_INST, liDuration,
      iNGwSmActvStsTmr) != ROK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "SDeregTmr() failed");
    return BP_AIN_SM_FAIL;
  }

  mbIsTimerRunning = false;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::stopTimer");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   registerTimer()
*
*     Desc:  this will register the timer for the requests
*
*     Notes: none
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::registerTimer (int aiDuration, int aiRequestId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::registerTimer Dur <%d> TID <%d>",
    aiDuration, aiRequestId);

  INGwSmTimerNode *lpTimerNode = new INGwSmTimerNode;

  lpTimerNode->expire = aiDuration;
  lpTimerNode->requestId = aiRequestId;
  lpTimerNode->ticks = 0;
  lpTimerNode->id = miMaxTimer++;
  meTimerList.push_back (lpTimerNode);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::registerTimer");

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   deregisterTimer()
*
*     Desc:  this will deregister the timer for the requests
*
*     Notes: none
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmDistributor::deregisterTimer (int aiDuration, int aiRequestId)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::deregisterTimer Dur <%d> TID <%d>",
    aiDuration, aiRequestId);

  vector <INGwSmTimerNode*>::iterator iter;

  for (iter = meTimerList.begin (); iter != meTimerList.end(); iter++)
  {
    INGwSmTimerNode *lpTimerNode = *(iter);

    if (lpTimerNode == 0)
      continue;

    if (lpTimerNode->requestId == aiRequestId &&
        lpTimerNode->expire == aiDuration)
    {
      meTimerList.erase (iter++);
      --miMaxTimer;
      delete lpTimerNode;

      mbAnyTimerDeregistered = true;
      return BP_AIN_SM_OK;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::deregisterTimer");

  return BP_AIN_SM_FAIL;
}

INGwSmAlmHdlr* 
INGwSmDistributor::getAlmHdlr()
{
  return mpAlmHdlr;
}

#ifdef INC_DLG_AUDIT

void 
INGwSmDistributor::handleAuditCfm() {

  logger.logINGwMsg(false,TRACE_FLAG,0,"In INGwSmDistributor::handleAuditCfm");
    mpTcapProvider->handleAuditCfm();
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out INGwSmDistributor::handleAuditCfm");
}
#endif
