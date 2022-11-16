//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

/************************************************************************
     Name:     INAP Stack Manager Request Object - Implementation
 
     Type:     C impl file
 
     Desc:     Implementation of the request objest
               which will be created per request from CCM

     File:     INGwSmRequest.C

     Sid:      INGwSmRequest.C 0  -  03/27/03 

     Prg:      gs,bd

************************************************************************/


#include "INGwStackManager/INGwSmRequest.h"
#include "INGwStackManager/INGwSmRepository.h"
#include "INGwTcapProvider/INGwTcapProvider.h"
#include "INGwStackManager/INGwSmRequestTable.h"
#include "INGwStackManager/INGwSmCfgHdlr.h"
#include "INGwStackManager/INGwSmStaHdlr.h"
#include "INGwStackManager/INGwSmCtlHdlr.h"
#include "INGwStackManager/INGwSmStsHdlr.h"

using namespace std;


/******************************************************************************
*
*     Fun:   INGwSmRequest()
*
*     Desc:  Default Contructor
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
INGwSmRequest::INGwSmRequest(int aiTransId, INGwSmQueueMsg *apOrigMsg, 
                               INGwSmDistributor* apDistributor):
mpOrigReq (apOrigMsg),
miTransactionId (aiTransId),
mpReqHdlr (0),
mpReqContext (0),
miIsWaitingForAlarm (0),
m_isReqHdlrForStats(false)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Entering INGwSmRequest::INGwSmRequest", miTransactionId);

  mpDist = apDistributor;

  mpRepository = mpDist->getSmRepository ();

  //set the request Id for CCM Operation 
  if (apOrigMsg != 0 && 
      apOrigMsg->mSrc == BP_AIN_SM_SRC_CCM)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> apOrigMsg->mSrc == BP_AIN_SM_SRC_CCM", miTransactionId);
    meBlockCtx.requestId = apOrigMsg->t.ccmOper.miRequestId;
  }
  else if(apOrigMsg != 0 && apOrigMsg->mSrc == BP_AIN_SM_SRC_EMS)
    meBlockCtx.requestId = apOrigMsg->t.stackData.miRequestId;
  else 
    meBlockCtx.requestId = -1;

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Leaving INGwSmRequest::INGwSmRequest", miTransactionId);
}


/******************************************************************************
*
*     Fun:   ~INGwSmRequest()
*
*     Desc:  Default Destructor
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
INGwSmRequest::~INGwSmRequest()
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Entering INGwSmRequest::~INGwSmRequest", miTransactionId);


  if (mpOrigReq)
    delete mpOrigReq;
  mpOrigReq = 0;

  //delete the request handler
  if (mpReqHdlr && !m_isReqHdlrForStats)
    delete mpReqHdlr;
  mpReqHdlr = 0;

  if (mpReqContext)
    delete mpReqContext;
  mpReqContext = 0;

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Leaving INGwSmRequest::~INGwSmRequest", miTransactionId);
}

/******************************************************************************
*
*     Fun:   getBlockingContext()
*
*     Desc:  this will return the blocking context needed for unblocking CCM Thread
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
INGwSmBlockingContext *
INGwSmRequest::getBlockingContext(int &aiScenario) 
{ 
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Entering INGwSmRequest::getBlockingContext for scenario <%d>",
    miTransactionId, miScenario);

  aiScenario = miScenario;

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Leaving INGwSmRequest::getBlockingContext", miTransactionId);

  return &meBlockCtx; 
}

/******************************************************************************
*
*     Fun:   decodeState()
*
*     Desc:  get the layer and operation from the Next State
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
int
INGwSmRequest::decodeState (int aiState, int &aiLayer, int &aiOper, int &aiSubOp)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Entering INGwSmRequest::decodeState <%X>", miTransactionId, aiState);

  /*
   * The first byte of state will be the Layer Id,
   * The second byte of state will be operation type,
   * the last two bytes will be sub operation type
   */

  aiLayer = (aiState & 0xFF000000) >> 24;
  aiOper  = (aiState & 0x00FF0000) >> 16;
  aiSubOp = (aiState & 0x0000FFFF);

  logger.logMsg (VERBOSE_FLAG, 0,
    "TID <%d> : decoded state is Layer<%d> Oper<%d> SubOp<%d>",
    miTransactionId, aiLayer, aiOper, aiSubOp);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Leaving INGwSmRequest::decodeState", miTransactionId);

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   INGwSmRequest()
*
*     Desc:  create the Request Handler request based on the Operation
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
int
INGwSmRequest::createRequestHdlr (int aiLayer, int aiOper, int aiSubOp)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Entering createRequestHdlr Layer<%d>, Oper<%d>, SubOp<%d>",
    miTransactionId, aiLayer, aiOper, aiSubOp);

  switch (aiOper)
  {
    case BP_AIN_SM_OPTYPE_CFG :
    {
      mpReqHdlr = new INGwSmCfgHdlr (*mpDist, aiLayer, aiOper, aiSubOp, miTransactionId);
      break;
    }
    case BP_AIN_SM_OPTYPE_CTL :
    {
      mpReqHdlr = new INGwSmCtlHdlr (*mpDist, aiLayer, aiOper, aiSubOp, miTransactionId);
      break;
    }
    case BP_AIN_SM_OPTYPE_STA :
    {
      mpReqHdlr = new INGwSmStaHdlr (*mpDist, aiLayer, aiOper, aiSubOp, miTransactionId);
      break;
    }
    case BP_AIN_SM_OPTYPE_STS :
    {
      //mpReqHdlr = new INGwSmStsHdlr (*mpDist, aiLayer, aiOper, aiSubOp, miTransactionId);
      mpReqHdlr = mpDist->mpSmWrapper->getStsHdlrInst();
			m_isReqHdlrForStats = true;
      
      break;
    }
    case BP_AIN_SM_OPTYPE_RCF :
    {
      mpReqHdlr = new INGwSmCfgHdlr (*mpDist, aiLayer, aiOper, aiSubOp, miTransactionId);
      break;
    }
    default:
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d> Invalid Operation <%d> passed", miTransactionId, aiOper);
      return BP_AIN_SM_FAIL;
      break;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Leaving INGwSmRequest::createRequestHdlr", miTransactionId);
  return BP_AIN_SM_OK;
}

#if 0
/******************************************************************************
*
*     Fun:   start()
*
*     Desc:  start processing the scenario
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
int
INGwSmRequest::start(int aiScenario,INGwSmQueueMsg *apQueMsg)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Entering INGwSmRequest::start", miTransactionId);

  if (aiScenario == -1)
    return BP_AIN_SM_SCENARIO_COMP;

  miScenario = aiScenario;

  //fetch the state transition from the repository for the scenario
  if (mpRepository->getStateTransitionList (aiScenario, meStateVector) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "State Vector could not obtained for TID <%d>",
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;
    return BP_AIN_SM_SCENARIO_COMP;
  }

  /*
   * This state vector contains all the states needed for the scenario to be
   * a success. We can pop out the state from the list and proceed sequentially
   */
  INGwSmIntVector::iterator iter = meStateVector.begin();

  if (iter == meStateVector.end())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "State Vector obtained is Empty for TID <%d>",
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }

  int liState = *(iter);

  meStateVector.erase (iter++);

  int liLayer, liOper, liSubOp;

  /*
   * get the layer, operation and suboperation from the state and 
   * create the appropriate request handler for it.
   */

  if (decodeState (liState, liLayer, liOper, liSubOp) == BP_AIN_SM_FAIL)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to decode state for TID <%d>",
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }

  if (createRequestHdlr (liLayer, liOper, liSubOp) == BP_AIN_SM_FAIL)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to create request handler for TID <%d>",
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }

  if (mpReqHdlr == 0)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "request handler obtained is NULL for TID <%d>",
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }

  //set the major and minor states
  miMajorState = liState;
  miMinorState = liSubOp;

  /*
   * Send the request to the Stack
   */

  int liRetVal = mpReqHdlr->sendRequest (apQueMsg);

  /*
   *  The return valye can be either Failure or Success or INDEX_OVER.
   *  INDEX_OVER means that the same suboperation needs to be invoked
   *  again for a different set of parameters. Hence the scenario and
   *  state needs to be maintained for now. On Failure, we need to 
   *  clean up the scenario in the Stack Manager.
   *  If retVal is SEND_NEXT then we need to call sendRequest to move
   *  to next state or send next operation
   */

  while (liRetVal == BP_AIN_SM_SEND_NEXT)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> : Send Next is returned. Continuing with request.",
      miTransactionId);

    liRetVal = sendRequest (liRetVal);
  }   

  if (liRetVal == BP_AIN_SM_FAIL)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "sendRequest Failed for TID <%d>",
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }
  else if (liRetVal == BP_AIN_SM_INDEX_OVER)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> Index can not be over during the start of the request", 
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }


  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Leaving INGwSmRequest::start", miTransactionId);
  return liRetVal;
}
#endif


/******************************************************************************
*
*     Fun:   start()
*
*     Desc:  start processing the scenario
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
int
INGwSmRequest::start(int aiScenario, INGwSmQueueMsg *apQueMsg, INGwSmRequestContext *apContext)
{
  logger.logINGwMsg(false,TRACE_FLAG,0,
    "TID <%d> aiScenario=<%d> Entering INGwSmRequest::start", miTransactionId, aiScenario);

  if (aiScenario == -1)
    return BP_AIN_SM_SCENARIO_COMP;

  if (apContext)
    mpReqContext = apContext;

  if (apQueMsg)
    mpQueMsg = apQueMsg;

  miScenario = aiScenario;

  //fetch the state transition from the repository for the scenario
  if (mpRepository->getStateTransitionList (aiScenario, meStateVector) != BP_AIN_SM_OK)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "State Vector could not obtained for TID <%d>",
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;
    return BP_AIN_SM_SCENARIO_COMP;
  }

  logger.logMsg (TRACE_FLAG, 0,
    "meStateVector size <%d> INGwSmRequest::start", meStateVector.size());
  /*
   * This state vector contains all the states needed for the scenario to be
   * a success. We can pop out the state from the list and proceed sequentially
   */
  INGwSmIntVector::iterator iter = meStateVector.begin();

  if (iter == meStateVector.end())
  {
    logger.logMsg (ERROR_FLAG, 0,
      "State Vector obtained is Empty for TID <%d>",
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }

  int liState = *(iter);

  meStateVector.erase (iter++);

  int liLayer, liOper, liSubOp;

  /*
   * get the layer, operation and suboperation from the state and 
   * create the appropriate request handler for it.
   */

  if (decodeState (liState, liLayer, liOper, liSubOp) == BP_AIN_SM_FAIL)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to decode state for TID <%d>",
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }

  if (createRequestHdlr (liLayer, liOper, liSubOp) == BP_AIN_SM_FAIL)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "Unable to create request handler for TID <%d>",
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }

  if (mpReqHdlr == 0)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "request handler obtained is NULL for TID <%d>",
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }

  //set the major and minor states
  miMajorState = liState;
  miMinorState = liSubOp;

  if(liOper == BP_AIN_SM_OPTYPE_STS)
  {
    INGwSmStsHdlr *stsHdlr = mpDist->mpSmWrapper->getStsHdlrInst();
    stsHdlr->miSubOp = liSubOp;
    stsHdlr->miOper = liOper;
    stsHdlr->miLayer = liLayer;
    stsHdlr->miTransId = miTransactionId;

  }

  /*
   * Send the request to the Stack
   */

  int liRetVal = mpReqHdlr->sendRequest (mpQueMsg,mpReqContext);

  /*
   *  The return valye can be either Failure or Success or INDEX_OVER.
   *  INDEX_OVER means that the same suboperation needs to be invoked
   *  again for a different set of parameters. Hence the scenario and
   *  state needs to be maintained for now. On Failure, we need to 
   *  clean up the scenario in the Stack Manager.
   *  If retVal is SEND_NEXT then we need to call sendRequest to move
   *  to next state or send next operation
   */

  while (liRetVal == BP_AIN_SM_SEND_NEXT)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> : Send Next is returned. Continuing with request.",
      miTransactionId);

    liRetVal = sendRequest (liRetVal);
  }   

  if (liRetVal == BP_AIN_SM_FAIL)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "sendRequest Failed for TID <%d>",
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }
  else if (liRetVal == BP_AIN_SM_INDEX_OVER)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> Index can not be over during the start of the request", 
      miTransactionId);

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }


 logger.logINGwMsg(false,TRACE_FLAG,0,
    "TID <%d> Leaving INGwSmRequest::start", miTransactionId);
  return liRetVal;
}


/******************************************************************************
*
*     Fun:   rollBack()
*
*     Desc:  Rolling back the commands executed in case of partial failure
*
*     Notes: none
*
*     File:  INGwSmDistributor.C
*
*******************************************************************************/
int
INGwSmRequest::rollBack(StackReqResp *rb)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmDistributor::rollBack");
  INGwSmQueueMsg *qMsg = new INGwSmQueueMsg;
  
  switch(rb->cmdType)
  {
     case ADD_NETWORK:
     {
       logger.logMsg (TRACE_FLAG, 0,
          " ADD_NETWORK rollback initiated, Layer: <%d>",
          rb->stackLayer);
       qMsg->mSrc = BP_AIN_SM_SRC_EMS;
       qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
       DelNetwork *nwk;

       qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
       nwk = &qMsg->t.stackData.req.u.delNwk;
       cmMemset((U8 *)nwk, 0, sizeof(DelNetwork));
       nwk->nwkId = rb->req.u.addNwk.nwId;
       nwk->variant = rb->req.u.addNwk.variant;
       qMsg->t.stackData.stackLayer = rb->stackLayer;
       qMsg->t.stackData.cmdType = DEL_NETWORK;
       qMsg->t.stackData.txnType= ROLLBACK_TXN;
       qMsg->t.stackData.txnStatus= INPROGRESS;
       qMsg->t.stackData.procId = rb->procId;
  
    //post the message to the Distributor
       if (mpDist->postMsg (qMsg) != BP_AIN_SM_OK)
       {
         logger.logMsg (ERROR_FLAG, 0,
             "In configure, postMsg failed");
         delete qMsg;
         return -1;
       }
       break;
     }
     case ADD_ROUTE:
     {
        logger.logMsg (TRACE_FLAG, 0,
          " ADD_ROUTE rollback initiated, Layer: <%d>",
          rb->stackLayer);
        qMsg->mSrc = BP_AIN_SM_SRC_EMS;
        qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;

        DelRoute Rte;
        Rte = qMsg->t.stackData.req.u.delRoute;
        cmMemset((U8 *)&Rte, 0, sizeof(DelRoute));
        Rte.dpc = rb->req.u.addRoute.dpc;
        Rte.upSwtch = rb->req.u.addRoute.upSwtch;
        Rte.nSapId = rb->req.u.addRoute.nSapId;

        qMsg->t.stackData.stackLayer = rb->stackLayer;
        qMsg->t.stackData.cmdType = DEL_ROUTE;
        qMsg->t.stackData.txnType= ROLLBACK_TXN;
        qMsg->t.stackData.txnStatus= INPROGRESS;
        qMsg->t.stackData.procId = rb->procId;

        //post the message to the Distributor
       if (mpDist->postMsg (qMsg) != BP_AIN_SM_OK)
       {
         logger.logMsg (ERROR_FLAG, 0,
             "In configure, postMsg failed");
         delete qMsg;
         return -1;
       }
     break;
     }

     case ADD_USERPART:
     {
        logger.logMsg (TRACE_FLAG, 0,
          " ADD_USERPART rollback initiated, Layer: <%d> and userparttype <%d> & mtp3Usap <%d> & proc <%d>",
          rb->stackLayer,rb->req.u.addUserPart.userPartType,rb->req.u.addUserPart.mtp3UsapId,rb->procId);
        qMsg->mSrc = BP_AIN_SM_SRC_EMS;
        qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;

        DelUserPart up;
        up = qMsg->t.stackData.req.u.delUserPart;
        cmMemset((U8 *)&up, 0, sizeof(DelUserPart));

        up.userPartType = rb->req.u.addUserPart.userPartType;
        up.mtp3UsapId = rb->req.u.addUserPart.mtp3UsapId;
        up.m3uaUsapId = rb->req.u.addUserPart.m3uaUsapId;
        up.sccpLsapId = rb->req.u.addUserPart.sccpLsapId;

        qMsg->t.stackData.stackLayer = rb->stackLayer;
        qMsg->t.stackData.cmdType = DEL_USR_PART;
        qMsg->t.stackData.txnType= ROLLBACK_TXN;
        qMsg->t.stackData.txnStatus= INPROGRESS;
        qMsg->t.stackData.procId = rb->procId;

       //post the message to the Distributor
       if (mpDist->postMsg (qMsg) != BP_AIN_SM_OK)
       {
         logger.logMsg (ERROR_FLAG, 0,
             "In configure, postMsg failed");
         delete qMsg;
         return -1;
       } 
     break;
     }

     case ADD_LOCAL_SSN:
     {
        logger.logMsg (TRACE_FLAG, 0,
          " ADD_Local_SSN rollback initiated, Layer: <%d> and SSN<%d>, TcapLSap <%d> TcapUsap <%d> SccpUsap <%d>",
          rb->stackLayer,rb->req.u.addLocalSsn.ssn,rb->req.u.addLocalSsn.tcapLsapId,rb->req.u.addLocalSsn.tcapUsapId, rb->req.u.addLocalSsn.sccpUsapId );
        qMsg->mSrc = BP_AIN_SM_SRC_EMS;
        qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;

        DelLocalSsn ssn;
        ssn = qMsg->t.stackData.req.u.delLocalSsn;
        cmMemset((U8 *)&ssn, 0, sizeof(DelLocalSsn));

        ssn.ssn = rb->req.u.addLocalSsn.ssn;
        ssn.tcapLsapId = rb->req.u.addLocalSsn.tcapLsapId;
        ssn.tcapUsapId = rb->req.u.addLocalSsn.tcapUsapId;
        ssn.sccpUsapId = rb->req.u.addLocalSsn.sccpUsapId;

        qMsg->t.stackData.stackLayer = rb->stackLayer;
        qMsg->t.stackData.subOpr = rb->subOpr;
        qMsg->t.stackData.cmdType = DEL_LOCAL_SSN;
        qMsg->t.stackData.txnType= ROLLBACK_TXN;
        qMsg->t.stackData.txnStatus= INPROGRESS;
        qMsg->t.stackData.procId = rb->procId;

       //post the message to the Distributor
       if (mpDist->postMsg (qMsg) != BP_AIN_SM_OK)
       {
         logger.logMsg (ERROR_FLAG, 0,
             "In configure, postMsg failed");
         delete qMsg;
         return -1;
       }

     break;
     }
     case ADD_ENDPOINT:
     {
        logger.logMsg (TRACE_FLAG, 0,
          " ADD_ENDPOINT rollback initiated, Layer: <%d>",
          rb->stackLayer);
        qMsg->mSrc = BP_AIN_SM_SRC_EMS;
        qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;

        DelEndPoint ep;
        ep = qMsg->t.stackData.req.u.delEp;
        cmMemset((U8 *)&ep, 0, sizeof(DelEndPoint));

        ep.sctpProcId = rb->req.u.addEp.sctpProcId;
        ep.sctpLsapId = rb->req.u.addEp.sctpLsapId;
        ep.sctpUsapId = rb->req.u.addEp.sctpUsapId;
        ep.m3uaLsapId = rb->req.u.addEp.m3uaLsapId;
        ep.tuclUsapId = rb->req.u.addEp.tuclUsapId;
    
        qMsg->t.stackData.stackLayer = rb->stackLayer;
        qMsg->t.stackData.subOpr = rb->subOpr;
        qMsg->t.stackData.cmdType = DEL_ENDPOINT;
        qMsg->t.stackData.txnType= ROLLBACK_TXN;
        qMsg->t.stackData.txnStatus= INPROGRESS;
        qMsg->t.stackData.procId = rb->procId;

       //post the message to the Distributor
       if (mpDist->postMsg (qMsg) != BP_AIN_SM_OK)
       {
         logger.logMsg (ERROR_FLAG, 0,
             "In configure, postMsg failed");
         delete qMsg;
         return -1;
       }
     break;
     }

     case ADD_LINK:
     {
        logger.logMsg (TRACE_FLAG, 0,
          " ADD_LINK rollback initiated, Layer: <%d>",
          rb->stackLayer);
        qMsg->mSrc = BP_AIN_SM_SRC_EMS;
        qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;

        DelLink lnk;
        lnk = qMsg->t.stackData.req.u.delLnk;
        cmMemset((U8 *)&lnk, 0, sizeof(DelLink));

        lnk.lnkId = rb->req.u.lnk.lnkId;
        lnk.mtp2ProcId = rb->req.u.lnk.mtp2ProcId;
        lnk.mtp2UsapId = rb->req.u.lnk.mtp2UsapId;
        lnk.mtp3LsapId = rb->req.u.lnk.mtp3LsapId;

        qMsg->t.stackData.stackLayer = rb->stackLayer;
        qMsg->t.stackData.cmdType = DEL_LINK;
        qMsg->t.stackData.txnType= ROLLBACK_TXN;
        qMsg->t.stackData.txnStatus= INPROGRESS;
        qMsg->t.stackData.procId = rb->procId;

       //post the message to the Distributor
       if (mpDist->postMsg (qMsg) != BP_AIN_SM_OK)
       {
         logger.logMsg (ERROR_FLAG, 0,
             "In configure, postMsg failed");
         delete qMsg;
         return -1;
       }
     break;
     }


     case ADD_AS:
     {
        logger.logMsg (TRACE_FLAG, 0,
          " ADD_AS rollback initiated, Layer: <%d> subOpr <%d> psId<%d> nwkId<%d>",
          rb->stackLayer,rb->subOpr,rb->req.u.addPs.psId,rb->req.u.addPs.nwkId);
        qMsg->mSrc = BP_AIN_SM_SRC_EMS;
        qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;
        
        DelPs ps;
        ps = qMsg->t.stackData.req.u.delPs;
        cmMemset((U8 *)&ps, 0, sizeof(DelPs));
        
        ps.psId = rb->req.u.addPs.psId;
        ps.nwkId = rb->req.u.addPs.nwkId;
        
        qMsg->t.stackData.stackLayer = rb->stackLayer;
        qMsg->t.stackData.subOpr = rb->subOpr;
        qMsg->t.stackData.cmdType = DEL_AS;
        qMsg->t.stackData.txnType= ROLLBACK_TXN;
        qMsg->t.stackData.txnStatus= INPROGRESS;
        qMsg->t.stackData.procId = rb->procId;
        
       //post the message to the Distributor
       if (mpDist->postMsg (qMsg) != BP_AIN_SM_OK)
       {
         logger.logMsg (ERROR_FLAG, 0,
             "In configure, postMsg failed");
         delete qMsg;
         return -1;
       }
     break;
     }


#if 0
     case ENABLE_LINK:
     {
        LinkDisable lnk;
        cmMemset((U8 *)&lnk, 0, sizeof(LinkDisable));
        lnk.lnkId = rb->req.u.lnk.lnkId;
        lnk.procId = rb->req.u.lnk.procId;
        lnk.lnkSetId = rb->req.u.lnk.lnkSetId;
        lnk.mtp2UsapId = rb->req.u.lnk.mtp2UsapId;
        lnk.mtp3LsapId = rb->req.u.lnk.mtp3LsapId;
        if(rb->stackLayer == BP_AIN_SM_MTP2_LAYER)
        {
             disableMtp2Link(lnk);
        }
        else
        {
             disableMtp3Link(lnk);
        }
     break;
     }
#endif
     case ENABLE_USERPART:
     {
        logger.logMsg (TRACE_FLAG, 0,
          " ENABLE_USERPART rollback initiated, Layer: <%d>",
          rb->stackLayer);
        qMsg->mSrc = BP_AIN_SM_SRC_EMS;
        qMsg->t.stackData.mOpType = BP_AIN_SM_CCMOP_CONFIGURE;

        DisableUserPart up;
        up = qMsg->t.stackData.req.u.disableUserPart;
        cmMemset((U8 *)&up, 0, sizeof(DisableUserPart));

        up.mtp3UsapId = rb->req.u.enableUserPart.mtp3UsapId;
        up.m3uaUsapId = rb->req.u.enableUserPart.m3uaUsapId ;
        up.sccpLsapId = rb->req.u.enableUserPart.sccpLsapId;
        up.nwkType = rb->req.u.enableUserPart.nwkType;

        qMsg->t.stackData.stackLayer = rb->stackLayer;
        qMsg->t.stackData.cmdType = DISABLE_USERPART;
        qMsg->t.stackData.txnType= ROLLBACK_TXN;
        qMsg->t.stackData.txnStatus= INPROGRESS;
        qMsg->t.stackData.procId = rb->procId;

       //post the message to the Distributor
       if (mpDist->postMsg (qMsg) != BP_AIN_SM_OK)
       {
         logger.logMsg (ERROR_FLAG, 0,
             "In configure, postMsg failed");
         delete qMsg;
         return -1;
       }

     break;
     }
  }
  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmDistributor::rollBack");
   return (ROK);
}


/******************************************************************************
*
*     Fun:   getCommonReasonStr()
*
*     Desc:  handle common stack response for all layers to last operation(If ret_val is set to 1 , it means layer specific error)
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
#if 1
char *
getCommonReasonStr(U16 reason, int layer)
{
  char *err_str = (char *)"UNKNOWN";

    logger.logMsg (TRACE_FLAG, 0,
                 "Entering getCommonReasonStr");

      switch (reason)
      {
         case LCM_REASON_INVALID_ENTITY:
          {
            err_str = (char *)"INVALID_ENTITY";
          }
          break;
         case LCM_REASON_INVALID_INSTANCE:
          {
            err_str = (char *)"INVALID_INSTANCE";
          }
          break;
         case LCM_REASON_INVALID_MSGTYPE:
          {
            err_str = (char *)"INVALID_MSG_TYPE";
          }
          break;
         case LCM_REASON_MEM_NOAVAIL:
          {
            err_str = (char *)"MEMORY_NOT_AVAILABLE";
          }
          break;
         case LCM_REASON_INVALID_ELMNT:
          {
            err_str = (char *)"INVALID_HDR_ENTITY";
          }
          break;
         case LCM_REASON_RECONFIG_FAIL:
          {
            err_str = (char *)"RECONFIG_FAIL";
          }
          break;
         case LCM_REASON_REGTMR_FAIL:
          {
            err_str = (char *)"TIMER_REGISTRATION_FAILED";
          }
          break;
         case LCM_REASON_GENCFG_NOT_DONE:
          {
            err_str = (char *)"GENCFG_NOT_DONE";
          }
          break;
         case LCM_REASON_INVALID_ACTION:
          {
            err_str = (char *)"INVALID_CONTROL_ACTION";
          }
          break;
         case LCM_REASON_INVALID_SUBACTION:
          {
            err_str = (char *)"INVALID_CONTROL_SUBACTION";
          }
          break;
         case LCM_REASON_INVALID_STATE:
          {
            err_str = (char *)"INVALID_STATE";
          }
          break;
         case LCM_REASON_INVALID_SAP:
          {
            err_str = (char *)"INVALID_SAP_ID";
          }
          break;
         case LCM_REASON_INVALID_PAR_VAL:
          {
            err_str = (char *)"INVALID_PARAM_VALUE";
          }
          break;
         case LCM_REASON_QINIT_FAIL:
          {
            err_str = (char *)"QUEUE_INIT_FAILED";
          }
          break;
         case LCM_REASON_NEG_CFM:
          {
            err_str = (char *)"NEGATIVE_CONFIRMATION";
          }
          break;
         case LCM_REASON_UPDTMR_EXPIRED:
          {
            err_str = (char *)"UPDATE_TIMER_EXPIRED";
          }
          break;
         case LCM_REASON_MISC_FAILURE:
          {
            err_str = (char *)"MISC_FAILURE";
          }
          break;
         case LCM_REASON_EXCEED_CONF_VAL:
          {
            err_str = (char *)"EXCEEDS_CONFIGURED_VAL";
          }
          break;
         case LCM_REASON_HASHING_FAILED:
          {
            err_str = (char *)"HASHING_FAILED";
          }
          break;
         case LCM_REASON_PEERCFG_NOT_DONE:
          {
            err_str = (char *)"PEER_SAP_NOT_CONFIGURED";
          }
          break;
         case LCM_REASON_PRTLYRCFG_NOT_DONE:
          {
            err_str = (char *)"PORTABLE_LAYER_NOT_CONFIGURED";
          }
          break;
         case LCM_REASON_INV_RSET:
          {
            err_str = (char *)"INVALID_RESOURCESET";
          }
          break;
         case LCM_REASON_INV_RSET_RANGE:
          {
            err_str = (char *)"INVALID_RESOURCESET_RANGE";
          }
          break;
         case LCM_REASON_INV_RSET_TYPE:
          {
            err_str = (char *)"INVALID_RESOURCESET_TYPE";
          }
          break;
         case LCM_REASON_INV_RSET_QUAL:
          {
            err_str = (char *)"INVALID_QUAL_OF_RSET";
          }
          break;
         case LCM_REASON_INV_INTERFACE:
          {
            err_str = (char *)"INVALID_DIST_INTERFACE";
          }
          break;
         case LCM_REASON_INV_DIST_TYPE:
          {
            err_str = (char *)"INVALID_DIST_TYPE";
          }
          break;
         case LCM_REASON_INV_DIST_QUAL:
          {
            err_str = (char *)"INVALID_QUAL_OF_DIST_TYPE";
          }
          break;
         case LCM_REASON_NAK_RCVD:
          {
            err_str = (char *)"NAK_RECEIVED";
          }
          break;
         case LCM_REASON_TIMEOUT:
          {
            err_str = (char *)"TIMEOUT_RECEIVED";
          }
          break;
         case LCM_REASON_PURE_FTHA:
          {
            err_str = (char *)"REQ_FOR_DFTHA_RECVD_BY_PURE_FTHA_MOD";
          }
          break;
         case LCM_REASON_DIST_FTHA:
          {
            err_str = (char *)"REQ_FOR_PURE_FTHA_RECVD_BY_DFTHA_MOD";
          }
          break;
         case LCM_REASON_INV_KEY:
          {
            err_str = (char *)"INVALID_KEY";
          }
          break;
         case LCM_REASON_SW_INCOMP:
          {
            err_str = (char *)"ENABLE_NODE_FAIL_BECOZ_OF_SW_INTERFACE_VERSION_INCOMPATIBLE";
          }
          break;
         case LCM_REASON_VERSION_MISMATCH:
          {
            err_str = (char *)"INTERFACE_VERSION_MISMATCH";
          }
          break;
         case LCM_REASON_SWVER_NAVAIL:
          {
            err_str = (char *)"INTERFACE_VERSION_NOT_FOUND";
          }
          break;
         case LCM_REASON_INVALID_RTENT:
          {
            err_str = (char *)"INVALID_ROUTING_ENTRY";
          }
          break;
         case LCM_REASON_MAXSPC_EXCEEDING:
          {
            err_str = (char *)"MAX_CONFIGURED_SPC_EXCEEDING";
          }
          break;
         case LCM_REASON_WRONG_DEFAULT_SPC:
          {
            err_str = (char *)"WRONG_DEFAULT_SPC_IN_CNTRL_REQ";
          }
          break;
         case  LCM_REASON_SPC_EXISTS:
          {
            err_str = (char *)"SPC_ALREADY_EXISTS";
          }
          break;
         case LCM_REASON_MINSPC_REACHED:
          {
            err_str = (char *)"NETWORK_ALREADY_REACHED_TO_MINSPC";
          }
          break;
         case LCM_REASON_MORE_SPC_THAN_CONFIGURED:
          {
            err_str = (char *)"CNTRL_REQ_GOT_MORE_SPC_THAN_CONFIGURED";
          }
          break;
         case LCM_REASON_DFL_SPC_DEL_NOT_ALLOWED:
          {
            err_str = (char *)"DEFAULT_SPC_DELETION_NOT_ALLOWED";
          }
          break;
         case LCM_REASON_NOTHING_TO_DELETE:
          {
            err_str = (char *)"CNTRL_REQ_GOT_ZERO_SPC_TO_DELETE";
          }
          break;
         case LCM_REASON_SPC_ALREADY_DELETED:
          {
            err_str = (char *)"SPC_ALREADY_DELETED";
          }
          break;
         case LCM_REASON_ASP_CONFIG:
          {
            err_str = (char *)"ASP_CONFIGURATION_ERROR";
          }
          break;
         default:
          {
            err_str = (char *)"UNKNOWN_ERROR";
          }
          break;
       }
    logger.logMsg (TRACE_FLAG, 0,
                 "Leaving getCommonReasonStr");
      return err_str;
}
#endif

/******************************************************************************
*
*     Fun:   getReasonStr()
*
*     Desc:  handle stack response to last operation
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
char *
getReasonStr(U16 reason, int layer)
{
  char *str = (char *)"UNKNOWN_ERROR";
  int ret_val = 0;

  logger.logMsg (TRACE_FLAG, 0,
                 "Entering getReasonStr Layer Id <%d>",layer);
  switch (layer)
  {
    case BP_AIN_SM_TCA_LAYER:
    {
      if((reason >=0) && (reason <256)) 
      {
        str = getCommonReasonStr(reason,layer);
      }
      else
      {
       switch (reason)
       {
          /***** Layer Specific Errors *****/

         case LST_REASON_INVALID_NMBSAPS:
         {
           str = (char *)"INVALID_NUMBER_OF_SAPS";
         }
         break;
         case LST_REASON_INVALID_TMRRES:
         {
           str = (char *)"INVALID_TIMER_RESOLUTION";
         }
         break;
         case LST_REASON_INVALID_DLGRANGE:
         {
           str = (char *)"INVALID_DIALOGUE_RANGE";
         }
         break;
#ifdef ST_TC_USER_DIST
         case LST_REASON_SSN_ABSENT:
         {
           str = (char *)"SSN_ABSENT";
         }
         break;
         case LST_REASON_DUPLICATE_SSN:
         {
           str = (char *)"DUPLICATE_SSN_ON_LOWER_SAP";
         }
         break;
#endif
         case LST_REASON_PAR_LENGTH:
         {
           str = (char *)"INVALID_PARAMETER_LENGTH";
         }
         break;
         default:
         {
           str = (char *)"UNKNOWN_ERROR";
         }
         break;
       }
      }
    }
    break; 
    case BP_AIN_SM_SCC_LAYER:
    {
      if((reason >=0) && (reason <256)) 
      {
        str = getCommonReasonStr(reason,layer);
      }
      else
      {
       switch (reason)
       {
          /***** Layer Specific Errors *****/

         case LSP_REASON_NO_ROUTES:
         {
           str = (char *)"NO_ROUTES_CONFIGURED";
         }
         break;
         case LSP_REASON_MAXSAP_CFG:
         {
           str = (char *)"MAX_SAPS_ALREADY_CONFIGURED";
         }
         break;
         case LSP_REASON_INVALID_SWTCH:
         {
           str = (char *)"INVALID_SWTCH";
         }
         break;
         case LSP_REASON_MFINIT_FAIL:
         {
           str = (char *)"MF_INIT_FAILED";
         }
         break;
         case LSP_REASON_NSAPBND_FAIL:
         {
           str = (char *)"NETWORK_SAP_BIND_FAILURE";
         }
         break;
         case LSP_REASON_GTT_NOTPRSNT:
         {
           str = (char *)"GLOBAL_TITLE_NOT_PRESENT";
         }
         break;
         case LSP_REASON_CONV_FAIL:
         {
           str = (char *)"CONVERSION_FAILED";
         }
         break;
         case LSP_REASON_ALREADY_CFG:
         {
           str = (char *)"ALREADY_CONFIGURED";
         }
         break;
         case LSP_REASON_NSAP_NOTCFG:
         {
           str = (char *)"NWK_SAP_NOT_CONFIGURED";
         }
         break;
         case LSP_REASON_RULE_NOTPRSNT:
         {
           str = (char *)"RULE_NOT_PRESENT";
         }
         break;
         case LSP_REASON_ADD_FAIL:
         {
           str = (char *)"GT_ADDITION_FAILED";
         }
         break;
         case LSP_REASON_DEL_FAIL:
         {
           str = (char *)"GT_DELETION_FAILED";
         }
         break;
         case LSP_REASON_INIT_FAIL:
         {
           str = (char *)"GT_INIT_FAILED";
         }
         break;
         case LSP_REASON_MAXASSO_CFG:
         {
           str = (char *)"MAX_ASSOCIATIONS_ALREADY_CONFIGURED";
         }
         break;
         case LSP_REASON_MAXADDR_CFG:
         {
           str = (char *)"MAX_ADDRESS_CONFIGURED";
         }
         break;
         case LSP_REASON_MAXACTNS_CFG:
         {
           str = (char *)"MAX_ACTIONS_CONFIGURED";
         }
         break;
         case LSP_REASON_ASSO_NOT_CFG:
         {
           str = (char *)"ASSOCIATION_NOT_CONFIGURED";
         }
         break;
         case LSP_REASON_MAXNW_CFG:
         {
           str = (char *)"MAX_NETWORK_CONFIGURED";
         }
         break;
         case LSP_REASON_NW_NOTCFG:
         {
           str = (char *)"ASSOCIATED_NWK_NOT_CONFIGURED";
         }
         break;
         case LSP_REASON_MAXSSN_CFG:
         {
           str = (char *)"MAX_SSN_CONFIGURED";
         }
         break;
         case LSP_REASON_INV_SWITCH:
         {
           str = (char *)"INVALID_SWTCH";
         }
         break;
         case LSP_REASON_INV_REPL_MODE:
         {
           str = (char *)"INVALID_OPERATION_FOR_REPLICATED_NODES";
         }
         break;
         case LSP_REASON_INV_SIOPRIO_IMP:
         {
           str = (char *)"INVALID_SIO_PRIORITY_TO_IMPORTANCE";
         }
         break;
         case LSP_REASON_INVALID_CONGLVL:
         {
           str = (char *)"INVALID_CONGESTION_LEVEL";
         }
         break;
         case LSP_REASON_INVALID_SSCTHRESH:
         {
           str = (char *)"INVALID_SSC_THRESHOLD";
         }
         break;
         case LSP_REASON_TRFLIM_NOT_CFG:
         {
           str = (char *)"TRAFFIC_LIMITATION_DATA_NOT_CONFIGURED";
         }
         break;
         case LSP_REASON_MAX_SCCP_ENT_CFG:
         {
           str = (char *)"MAX_SCCP_ENTITIES_CONFIGURED";
         }
         break;
         case LSP_REASON_INVALID_ROUTE:
         {
           str = (char *)"INVALID_ROUTE_IN_RTE_STATISTICS_REQ";
         }
         break;
         case LSP_REASON_INVALID_NUMENTITIES:
         {
           str = (char *)"INVALID_NUMBER_OF_ENTITIES";
         }
         break;
         case LSP_REASON_INVALID_NUMNIDS:
         {
           str = (char *)"INVALID_NUMBER_OF_NIDs";
         }
         break;
         case LSP_REASON_OVERLAPPING_RULE:
         {
           str = (char *)"OVERLAPPING_RULE";
         }
         break;
         case LSP_REASON_MAXROUTE_CFG:
         {
           str = (char *)"MAX_ROOUTES_ALREADY_CONFIGURED";
         }
         break;
#ifdef LSPV2_8
         case LSP_REASON_MAXASP_CFG:
         {
           str = (char *)"MAX_ASP_ALREADY_CONFIGURED";
         }
         break;
         case LSP_REASON_MAXASPSSN_CFG:
         {
           str = (char *)"MAX_SSN_ALREADY_CONFIGURED_FOR_ASP";
         }
         break;
         case LSP_REASON_MAXASPCPC_CFG:
         {
           str = (char *)"MAX_PC_ALREADY_CONFIGURED";
         }
         break;
#endif

#ifdef LSPV2_9
         case LSP_REASON_INVALID_NUM_SNRI:
         {
           str = (char *)"INVALID_NO_OF_SNRI";
         }
         break;
#endif
         case LSP_REASON_INVALID_MODE:
         {
           str = (char *)"INVALID_MODE_OF_OPERATION";
         }
         break;
         default:
         {
           str = (char *)"UNKNOWN_ERROR";
         }
         break;
       }
      }
    }
    break;
    case BP_AIN_SM_MTP3_LAYER:
    {
      if((reason >=0) && (reason <256)) 
      {
        str = getCommonReasonStr(reason,layer);
      }
      else
      {
       switch (reason)
       {
          /***** Layer Specific Errors *****/

         case LSN_REASON_NMBDLSAP_NOK:
         {
           str = (char *)"NUMBER_OF_DLSAP_IN_GEN_CFG_IS_MORE";
         }
         break;
         case LSN_REASON_INV_LNKTYPE:
         {
           str = (char *)"INVALID_LINK_TYPE";
         }
         break;
         case LSN_REASON_INV_QLEN:
         {
           str = (char *)"INVALID_QUEUE_LENGTH_THRESHOLD";
         }
         break;
         case LSN_REASON_INV_LNKTSTSLC:
         {
           str = (char *)"INVALID_LINK_TST_SLC_IN_DLSAP";
         }
         break;
         case LSN_REASON_INV_CMBLNKSETID:
         {
           str = (char *)"INVALID_CMBINE_LINKSET_ID";
         }
         break;
         case LSN_REASON_INV_DEL_PRIOR:
         {
           str = (char *)"INVALID_PRIORITY_TO_DELETE";
         }
         break;
         case LSN_REASON_INV_BNDSTATE:
         {
           str = (char *)"INVALID_BIND_STATE";
         }
         break;
         case LSN_REASON_INV_LNKNUM:
         {
           str = (char *)"INVALID_LINK_NUMBER";
         }
         break;
         case LSN_REASON_EXCESS_RTECFG:
         {
           str = (char *)"EXCESS_ROUTE_CONFIGURATION";
         }
         break;
         case LSN_REASON_SRT_INPROG:
         {
           str = (char *)"SRT_INPROGRESS";
         }
         break;
         case LSN_REASON_INV_SPID:
         {
           str = (char *)"INVALID_SPID";
         }
         break;
         case LSN_REASON_EXCESS_LNK_IN_CMBLNKSET:
         {
           str = (char *)"EXCESS_LINK_IN_COMBINE_LINKSET";
         }
         break;
         case LSN_REASON_EXCESS_LNKSET_IN_CMBLNKSET:
         {
           str = (char *)"EXCESS_LINKSET_IN_COMBINE_LINKSET";
         }
         break;
         case LSN_REASON_PREFLINK_WITH_LOWPRI:
         {
           str = (char *)"PREF_LINK_WITH_LOWER_PRI";
         }
         break;
         default:
         {
           str = (char *)"UNKNOWN_ERROR";
         }
         break;
       }
      }
    }
    break;
    case BP_AIN_SM_M3U_LAYER:
    {
      if((reason >=0) && (reason <256)) 
      {
        str = getCommonReasonStr(reason,layer);
      }
      else
      {
       switch (reason)
       {
          /***** Layer Specific Errors *****/

         case LIT_REASON_INVALID_PSID:
         {
           str = (char *)"INVALID_PSID";
         }
         break;
         case LIT_REASON_INVALID_PSPID:
         {
           str = (char *)"INVALID_PSPID";
         }
         break;
         case LIT_REASON_INVALID_NWKID:
         {
           str = (char *)"INVALID_NETWORK_ID";
         }
         break;
         case LIT_REASON_INVALID_NSAPID:
         {
           str = (char *)"INVALID_NSAP_ID";
         }
         break;
         case LIT_REASON_INVALID_RTENT:
         {
           str = (char *)"INVALID_ROUTE_ENTRY";
         }
         break;
         case LIT_REASON_SERVICE_IN_USE:
         {
           str = (char *)"SERVICE_IN_USE";
         }
         break;
         case LIT_REASON_MNGMNT_INHIBIT:
         {
           str = (char *)"MANAGEMENT_INHIBITED";
         }
         break;
         case LIT_REASON_INVALID_ASSOCID:
         {
           str = (char *)"INVALID_ASSOC_ID";
         }
         break;
         case LIT_REASON_NO_RESPONSE:
         {
           str = (char *)"NO_RESPONSE";
         }
         break;
         case LIT_REASON_INV_PEERTYPE:
         {
           str = (char *)"INVALID_PEER_TYPE";
         }
         break;
         case LIT_REASON_INV_PEER_STATE:
         {
           str = (char *)"INVALID_PEER_STATE";
         }
         break;
         case LIT_REASON_MSG_NOT_SENT:
         {
           str = (char *)"MSG_NOT_SENT";
         }
         break;
         case LIT_REASON_UNREG_RK:
         {
           str = (char *)"UNREGISTERED_RK";
         }
         break;
         case LIT_REASON_INVALID_RK:
         {
           str = (char *)"INVALID_RK";
         }
         break;
         case LIT_REASON_INVALID_RC:
         {
           str = (char *)"INVALID_RC";
         }
         break;
         case LIT_REASON_PSID_ALREADY_USED:
         {
           str = (char *)"PSID_ALREADY_USED";
         }
         break;
         case LIT_REASON_RKREGREQ_PEND:
         {
           str = (char *)"RK_REGISTER_REQ_PENDING";
         }
         break;
         case LIT_REASON_RKDEREGREQ_PEND:
         {
           str = (char *)"RK_DEREGISTER_REQ_PENDING";
         }
         break;
         case LIT_REASON_DRKM_NOT_SUPP:
         {
           str = (char *)"DRKM_NOT_SUPPORTED";
         }
         break;
         case LIT_REASON_INVALID_THM:
         {
           str = (char *)"INVALID_THM";
         }
         break;
         case LIT_REASON_INVALID_RTINDEX:
         {
           str = (char *)"INVALID_RT_INDEX";
         }
         break;
         case LIT_REASON_UNMATCH_NSAP:
         {
           str = (char *)"UNMATCHED_NSAP";
         }
         break;
         case LIT_REASON_UNMATCH_RTTYPE:
         {
           str = (char *)"UNMATCHED_RT_TYPE";
         }
         break;
         case LIT_REASON_UNMATCH_NOSTATUS:
         {
           str = (char *)"UNMATCHED_NO_STATUS";
         }
         break;
         case LIT_REASON_UNMATCH_RTFILTER:
         {
           str = (char *)"UNMATCHED_RT_FILTER";
         }
         break;
         case LIT_REASON_OVERLAPPING_THM:
         {
           str = (char *)"OVERLAPPING_THM";
         }
         break;
         case LIT_REASON_INVALID_CONG_LEVEL:
         {
           str = (char *)"INVALID_CONGESTION_LEVEL";
         }
         break;
         case LIT_REASON_INVALID_DPC:
         {
           str = (char *)"INVALID_CONGESTION_LEVEL";
         }
         break;
         case LIT_REASON_PS_NOT_LOCAL:
         {
           str = (char *)"PS_NOT_LOCAL";
         }
         break;
         case LIT_REASON_NSAP_CFGED_LPS:
         {
           str = (char *)"NSAP_CONFIGURED_LPS";
         }
         break;
         case LIT_REASON_INVALID_NUM_PS:
         {
           str = (char *)"INVALID_NUMBER_OF_PS";
         }
         break;
         case LIT_REASON_INVALID_NUM_RC:
         {
           str = (char *)"INVALID_NUMBER_OF_RC";
         }
         break;
         case LIT_REASON_OVERLAPPING_ASPID:
         {
           str = (char *)"OVERLAPPING_ASPID";
         }
         break;
         case LIT_REASON_MAX_DPC_LIM_EXCEED:
         {
           str = (char *)"MAX_DPC_LIMIT_EXCEEDS";
         }
         break;
         default:
         {
           str = (char *)"UNKNOWN_ERROR";
         }
         break;
       }
      }
    }
    break;
    case BP_AIN_SM_SCT_LAYER:
    {
      if((reason >=0) && (reason <256)) 
      {
        str = getCommonReasonStr(reason,layer);
      }
      else
      {
       switch (reason)
       {
          /***** Layer Specific Errors *****/

         case LSB_REASON_INV_ASSOC:
         {
           str = (char *)"INVALID_ASSOCIATION";
         }
         break;
         case LSB_REASON_INV_DTA:
         {
           str = (char *)"INVALID_DTA";
         }
         break;
         case LSB_REASON_MAX_BND_TRY:
         {
           str = (char *)"MAX_BIND_RETRIES_REACHED";
         }
         break;
         case LSB_REASON_LMI_BUSY:
         {
           str = (char *)"LAYER_MANAGER_REQ_BUSY";
         }
         break;
         case LSB_REASON_INVALID_HOSTNAME:
         {
           str = (char *)"OWN_HOSTNAME_CAN_NOT_BE_RESOLVED";
         }
         break;
         default:
         {
           str = (char *)"UNKNOWN_ERROR";
         }
         break;
       }
      }
    }
    break;
    case BP_AIN_SM_TUC_LAYER:
    {
      if((reason >=0) && (reason <256)) 
      {
        str = getCommonReasonStr(reason,layer);
      }
      else
      {
       switch (reason)
       {
          /***** Layer Specific Errors *****/

         case LHI_REASON_SOCKLIB_INIT_FAIL:
         {
           str = (char *)"SOCK_LIB_INIT_FAIL";
         }
         break;
         case LHI_REASON_NO_SAP_FOUND:
         {
           str = (char *)"NO_SAP_FOUND";
         }
         break;
         case LHI_REASON_SOCK_FAIL:
         {
           str = (char *)"SOCK_FAIL";
         }
         break;
         case LHI_REASON_INV_SPID:
         {
           str = (char *)"INVALID_SPID";
         }
         break;
         case LHI_REASON_CREATE_RECVTSKS_FAILED:
         {
           str = (char *)"CREATE_RECEIVE_TASK_FAILED";
         }
         break;
         case LHI_REASON_LOCK_INIT_FAILED:
         {
           str = (char *)"LOCK_INIT_FAILED";
         }
         break;
         case LHI_REASON_OPINPROG:
         {
           str = (char *)"OP_INPROGRESS";
         }
         break;
         case LHI_REASON_INT_ERROR:
         {
           str = (char *)"INTERNAL_ERROR";
         }
         break;
         case LHI_REASON_DIFF_OPINPROG:
         {
           str = (char *)"DIFFERENT_OP_INPROGRESS";
         }
         break;
         case LHI_REASON_OSSL_LIBINIT_FAILED:
         {
           str = (char *)"OSSL_LIB_INIT_FAILED";
         }
         break;
         case LHI_REASON_OSSL_CTXINIT_FAILED:
         {
           str = (char *)"OSSL_CTX_INIT_FAILED";
         }
         break;
         case LHI_REASON_OSSL_VRFYLOC_FAILED:
         {
           str = (char *)"OSSL_VERIFY_LOC_FAILED";
         }
         break;
         case LHI_REASON_OSSL_CERTCHN_FAILED:
         {
           str = (char *)"OSSL_CERT_CHANGE_FAILED";
         }
         break;
         case LHI_REASON_OSSL_PKEY_FAILED:
         {
           str = (char *)"OSSL_PKEY_FAILED";
         }
         break;
         case LHI_REASON_OSSL_CIPHER_FAILED:
         {
           str = (char *)"OSSL_CIPHER_FAILED";
         }
         break;
         case LHI_REASON_OSSL_SESSIDCTX_FAILED:
         {
           str = (char *)"OSSL_SESSION_ID_CTX_FAILED";
         }
         break;
         case LHI_REASON_OSSL_SESSTIMEOUT_FAILED:
         {
           str = (char *)"OSSL_SESSTIMEOUT_FAILED";
         }
         break;
         case LHI_REASON_INST0_NTREG:
         {
           str = (char *)"INSTANCE0_NOT_REGISTERED";
         }
         break;
         case LHI_REASON_NO_PENDOP:
         {
           str = (char *)"NO_PENDING_OPR";
         }
         break;
         case LHI_REASON_WRONG_INST:
         {
           str = (char *)"WRONG_INSTANCE";
         }
         break;
         default:
         {
           str = (char *)"UNKNOWN_ERROR";
         }
         break;
       }
      }
    }
    break;
    case BP_AIN_SM_LDF_MTP3_LAYER:
    case BP_AIN_SM_PSF_MTP3_LAYER:
    {
      if((reason >=0) && (reason <256)) 
      {
        str = getCommonReasonStr(reason,layer);
      }
      else
      {
       switch (reason)
       {
          /***** Layer Specific Errors *****/

         case LDN_REASON_INV_DYN_PAR_VAL:
         {
           str = (char *)"INVALID_DYNAMIC_PARAM_VAL";
         }
         break;
         case LDN_REASON_INV_STAT_PAR_VAL:
         {
           str = (char *)"INVALID_STATIC_PARAM_VAL";
         }
         break;
         case LDN_REASON_DIST_TYPE_MISMATCH:
         {
           str = (char *)"DISTRIBUTION_TYPE_MISMATCH";
         }
         break;
         case LDN_REASON_RSET_RANGE_OVERFLOW:
         {
           str = (char *)"RESOURCESET_RANGE_OVERFLOW";
         }
         break;
         case LDN_REASON_RSET_RANGE_OVERLAP:
         {
           str = (char *)"RESOURCESET_RANGE_OVERLAP";
         }
         break;
         case LDN_REASON_HASH_INSERT_FAILED:
         {
           str = (char *)"HASH_INSERT_FAILED";
         }
         break;
         case LDN_REASON_ARRAY_MEM_NOAVAIL:
         {
           str = (char *)"ARRAY_MEM_NOT_AVAILABLE";
         }
         break;
         case LDN_REASON_DYNAMIC_MEM_NOAVAIL:
         {
           str = (char *)"DYNAMIC_MEMORY_NOT_AVAILABLE";
         }
         break;
         case LDN_REASON_SAMEPROC_MEM_NOAVAIL:
         {
           str = (char *)"SAMEPROC_MEMORY_NOT_AVAILABLE";
         }
         break;
         case LDN_REASON_BITMAP_MEM_NOAVAIL:
         {
           str = (char *)"BITMAP_MEMORY_NOT_AVAILABLE";
         }
         break;
         case LDN_REASON_STSMEM_NOAVAIL:
         {
           str = (char *)"STS_MEMORY_NOT_AVAILABLE";
         }
         break;
         case LDN_REASON_MAPCB_MEM_NOAVAIL:
         {
           str = (char *)"MAPCB_MEMORY_NOT_AVAILABLE";
         }
         break;
         case LDN_REASON_PC_INDEX_OVERFLOW:
         {
           str = (char *)"PC_INDEX_OVERFLOW";
         }
         break;
         case LDN_REASON_INV_DIST_ATTR:
         {
           str = (char *)"INVALID_DISTRIBUTION_ATTRIBUTE";
         }
         break;
         case LDN_REASON_BOTH_OPC_DPC_PRESENT:
         {
           str = (char *)"BOTH_OPC_DPC_PRESENT";
         }
         break;
         case LDN_REASON_INV_MAX_SI:
         {
           str = (char *)"INVALID_MAX_SI";
         }
         break;
         case LDN_REASON_INV_MAX_SSF:
         {
           str = (char *)"INVALID_MAX_SSF";
         }
         break;
         case LDN_REASON_INV_MAX_VAR:
         {
           str = (char *)"INVALID_MAX_VARIANT";
         }
         break;
         case LDN_REASON_INV_MAX_RSETS:
         {
           str = (char *)"INVALID_MAX_RESOURCESET";
         }
         break;
         case LDN_REASON_SAMEPROC_RECONFIG:
         {
           str = (char *)"SAMEPROC_RECONFIG";
         }
         break;
         case LDN_REASON_CRIT_RSET_RECONFIG:
         {
           str = (char *)"CRITICAL_RESOURCESET_RECONFIGURED";
         }
         break;
         case LDN_REASON_DEFAULT_RSET_RECONFIG:
         {
           str = (char *)"DEFAULT_RESOURCESET_RECONFIGURED";
         }
         break;
         case LDN_REASON_MR_REG_FAILURE:
         {
           str = (char *)"MR_REGISTRATION_FAILURE";
         }
         break;
         case LDN_REASON_MAP_INSERT_FAILED:
         {
           str = (char *)"MAP_INSERT_FAILED";
         }
         break;
         case LDN_REASON_INV_DPC_DELETION:
         {
           str = (char *)"INVALID_DPC_DELETEION";
         }
         break;
         case LDN_REASON_DEL_HASH_FAILED:
         {
           str = (char *)"DELETE_HASH_FAILED";
         }
         break;
         case LDN_REASON_DPC_NOT_FOUND:
         {
           str = (char *)"DPC_NOT_FOUND";
         }
         break;
         case LDN_REASON_PORT_DIST_CONFIG:
         {
           str = (char *)"PORT_DISTRIBUTION_CONFIG";
         }
         break;
         case LDN_REASON_INV_NMB_SAPS:
         {
           str = (char *)"INVALID_NUMBER_OF_SAPS";
         }
         break;
         default:
         {
           str = (char *)"UNKNOWN_ERROR";
         }
         break;
       }
      }
    }
    break;
    case BP_AIN_SM_LDF_M3UA_LAYER:
    case BP_AIN_SM_PSF_M3UA_LAYER:
    {
      if((reason >=0) && (reason <256)) 
      {
        str = getCommonReasonStr(reason,layer);
      }
      else
      {
       switch (reason)
       {
          /***** Layer Specific Errors *****/

         case LDV_REASON_INV_DYN_PAR_VAL:
         {
           str = (char *)"INVALID_DYNAMIC_PARAM_VAL";
         }
         break;
         case LDV_REASON_INV_STAT_PAR_VAL:
         {
           str = (char *)"INVALID_STATIC_PARAM_VAL";
         }
         break;
         case LDV_REASON_DIST_TYPE_MISMATCH:
         {
           str = (char *)"DISTRIBUTION_TYPE_MISMATCH";
         }
         break;
         case LDV_REASON_RSET_RANGE_OVERFLOW:
         {
           str = (char *)"RESOURCESET_RANGE_OVERFLOW";
         }
         break;
         case LDV_REASON_RSET_RANGE_OVERLAP:
         {
           str = (char *)"RESOURCESET_RANGE_OVERLAP";
         }
         break;
         case LDV_REASON_HASH_INSERT_FAILED:
         {
           str = (char *)"HASH_INSERT_FAILED";
         }
         break;
         case LDV_REASON_ARRAY_MEM_NOAVAIL:
         {
           str = (char *)"ARRAY_MEM_NOT_AVAILABLE";
         }
         break;
         case LDV_REASON_DYNAMIC_MEM_NOAVAIL:
         {
           str = (char *)"DYNAMIC_MEMORY_NOT_AVAILABLE";
         }
         break;
         case LDV_REASON_PREFPROC_MEM_NOAVAIL:
         {
           str = (char *)"PREFPROC_MEMORY_NOT_AVAILABLE";
         }
         break;
         case LDV_REASON_BITMAP_MEM_NOAVAIL:
         {
           str = (char *)"BITMAP_MEMORY_NOT_AVAILABLE";
         }
         break;
         case LDV_REASON_STSMEM_NOAVAIL:
         {
           str = (char *)"STS_MEMORY_NOT_AVAILABLE";
         }
         break;
         case LDV_REASON_MAPCB_MEM_NOAVAIL:
         {
           str = (char *)"MAPCB_MEMORY_NOT_AVAILABLE";
         }
         break;
         case LDV_REASON_PC_INDEX_OVERFLOW:
         {
           str = (char *)"PC_INDEX_OVERFLOW";
         }
         break;
         case LDV_REASON_INV_DIST_ATTR:
         {
           str = (char *)"INVALID_DISTRIBUTION_ATTRIBUTE";
         }
         break;
         case LDV_REASON_BOTH_OPC_DPC_PRESENT:
         {
           str = (char *)"BOTH_OPC_DPC_PRESENT";
         }
         break;
         case LDV_REASON_INV_MAX_SI:
         {
           str = (char *)"INVALID_MAX_SI";
         }
         break;
         case LDV_REASON_INV_MAX_SSF:
         {
           str = (char *)"INVALID_MAX_SSF";
         }
         break;
         case LDV_REASON_INV_MAX_NWK:
         {
           str = (char *)"INVALID_MAX_NETWORK";
         }
         break;
         case LDV_REASON_INV_MAX_RSETS:
         {
           str = (char *)"INVALID_MAX_RESOURCESET";
         }
         break;
         case LDV_REASON_PREFPROC_RECONFIG:
         {
           str = (char *)"PREFPROC_RECONFIG";
         }
         break;
         case LDV_REASON_CRIT_RSET_RECONFIG:
         {
           str = (char *)"CRITICAL_RESOURCESET_RECONFIGURED";
         }
         break;
         case LDV_REASON_DEFAULT_RSET_RECONFIG:
         {
           str = (char *)"DEFAULT_RESOURCESET_RECONFIGURED";
         }
         break;
         case LDV_REASON_MR_REG_FAILURE:
         {
           str = (char *)"MR_REGISTRATION_FAILURE";
         }
         break;
         case LDV_REASON_MAP_INSERT_FAILED:
         {
           str = (char *)"MAP_INSERT_FAILED";
         }
         break;
         case LDV_REASON_DEL_HASH_FAILED:
         {
           str = (char *)"DELETE_HASH_FAILED";
         }
         break;
         case LDV_REASON_PORT_DIST_CONFIG:
         {
           str = (char *)"PORT_DIST_CONFIG";
         }
         break;
         case LDV_REASON_INV_NMB_SAPS:
         {
           str = (char *)"INVALID_NUMBER_OF_SAPS";
         }
         break;
         case LDV_REASON_INV_NWKAPP:
         {
           str = (char *)"INVALID_NETWORK_APP";
         }
         break;
         default:
         {
           str = (char *)"UNKNOWN_ERROR";
         }
         break;
       }
      }
    }
    break;
    case BP_AIN_SM_RELAY_LAYER:
    {
       switch (reason)
       {
          /***** Layer Specific Errors *****/

         case LRYRSNPF:
         {
           str = (char *)"POST_FAILED";
         }
         break;
         case LRYRSNKARX:
         {
           str = (char *)"KEEP_ALIVE_RX_EXPIRED";
         }
         break;
         case LRYRSNLCKFAIL:
         {
           str = (char *)"LOCK_FAILED";
         }
         break;
         case LRYRSNB3LCKFAIL:
         {
           str = (char *)"BIT3_LOCK_FAILURE";
         }
         break;
         case LRYRSNB3UNLCKFAIL:
         {
           str = (char *)"BIT3_UNLOCK_FAILURE";
         }
         break;
         case LRYRSNHSTLCKFAIL:
         {
           str = (char *)"HST_LOCK_FAILURE";
         }
         break;
         case LRYRSNHSTUNLCKFAIL:
         {
           str = (char *)"HST_UNLOCK_FAILURE";
         }
         break;
         case LRYRSNNOROUTE:
         {
           str = (char *)"NO_ROUTE";
         }
         break;
         case LRYRSNREMDOWN:
         {
           str = (char *)"REMOTE_DOWN";
         }
         break;
         case LRYRSNBRDFAIL:
         {
           str = (char *)"BOARD_FAILURE";
         }
         break;
         case LRYRSNMGMTREQ:
         {
           str = (char *)"MANAGEMENT_REQUEST";
         }
         break;
         case LRYRSNHSTSHMEMFAIL:
         {
           str = (char *)"HST_SHARED_MEMORY_INIT_FAILED";
         }
         break;
         case LRYRSNRAWSHMEMFAIL:
         {
           str = (char *)"RAW_SHARED_MEMORY_INIT_FAILED";
         }
         break;
         case LRYRSNNOTFIRSTBD:
         {
           str = (char *)"NOT_A_FIRST_BUFFER_DESCRIPTOR";
         }
         break;
         case LRYRSNEMPTYBD:
         {
           str = (char *)"EMPTY_BUFFER_DESCRIPTOR";
         }
         break;
         case LRYRSNWRONGSTATE:
         {
           str = (char *)"WRONG_STATE";
         }
         break;
         case LRYRSNB3SHMEMFAIL:
         {
           str = (char *)"B3_SHARED_MEMORY_INIT_FAILED";
         }
         break;
         case LRYRSNKHSHMEMFAIL:
         {
           str = (char *)"KH_SHARED_MEMORY_INIT_FAILED";
         }
         break;
         case LRYRSNS5SHMMEMFAIL:
         {
           str = (char *)"SYS_V_SHARED_MEMORY_INIT_FAILED";
         }
         break;
         case LRYRSNNOSRVRCFG:
         {
           str = (char *)"SERVER_NOT_CONFIGURED";
         }
         break;
#ifdef LRY1
         case LRY_REASON_INVALID_OPT:
         {
           str = (char *)"INVALID_OPTION_LEVEL_IN_CFG";
         }
         break;
#endif
         default:
         {
           str = (char *)"UNKNOWN_ERROR";
         }
         break;
       }
    }
    break;
    case BP_AIN_SM_MR_LAYER:
    {
      if((reason >=0) && (reason <256)) 
      {
        str = getCommonReasonStr(reason,layer);
      }
      else
      {
       switch (reason)
       {
          /***** Layer Specific Errors *****/

         case LMR_REASON_INVALID_ENTRIES:
         {
           str = (char *)"INVALID_ENTRIES";
         }
         break;
         case LMR_REASON_LOCK_FAIL:
         {
           str = (char *)"LOCK_FAILED";
         }
         break;
         case LMR_REASON_PEER_TO:
         {
           str = (char *)"PEER_TO";
         }
         break;
         case LMR_REASON_PEERSYNCINPROGRESS:
         {
           str = (char *)"PEER_SYN_INPROGRESS";
         }
         break;
         case LMR_REASON_INVALID_RSET:
         {
           str = (char *)"INVALID_RESOURCESET";
         }
         break;
         case LMR_REASON_QLEN_FAIL:
         {
           str = (char *)"QUEUE_LENGTH_FAILED";
         }
         break;
         case LMR_REASON_NO_MORE_ENTRIES:
         {
           str = (char *)"NO_MORE_ENTRIES";
         }
         break;
         default:
         {
           str = (char *)"UNKNOWN_ERROR";
         }
         break;
       }
      }
    }
    break;
    case BP_AIN_SM_SH_LAYER:
    {
      if((reason >=0) && (reason <256)) 
      {
        str = getCommonReasonStr(reason,layer);
      }
      else
      {
       switch (reason)
       {
          /***** Layer Specific Errors *****/

         case LSH_REASON_NAK_RCVD:
         {
           str = (char *)"NAK_RECEIVED";
         }
         break;
         case LSH_REASON_TIMEOUT:
         {
           str = (char *)"TIMEOUT";
         }
         break;
         default:
         {
           str = (char *)"UNKNOWN_ERROR";
         }
         break;
       }
      }
    }
    break;
    case BP_AIN_SM_SG_LAYER:
    {
      if((reason >=0) && (reason <256)) 
      {
        str = getCommonReasonStr(reason,layer);
      }
      else
      {
       switch (reason)
       {
          /***** Layer Specific Errors *****/

         case LSG_REASON_INV_API:
         {
           str = (char *)"INVALID_API";
         }
         break;
         case LSG_REASON_INV_ENTITY:
         {
           str = (char *)"INVALID_ENTITY";
         }
         break;
         case LSG_REASON_BAD_RSET_STATE:
         {
           str = (char *)"BAD_RESOURCESET_STATE";
         }
         break;
         case LSG_REASON_NO_TAPA_RSET:
         {
           str = (char *)"NO_TAPA_RESOURCESET";
         }
         break;
         case LSG_REASON_BUSY:
         {
           str = (char *)"BUSY_NOW";
         }
         break;
         case LSG_REASON_NODE_NA:
         {
           str = (char *)"NO_SUCH_NODE";
         }
         break;
         case LSG_REASON_ENT_NA:
         {
           str = (char *)"NO_SUCH_ENTITY";
         }
         break;
         case LSG_REASON_RSET_NA:
         {
           str = (char *)"NO_SUCH_RESOURCESET";
         }
         break;
         case LSG_REASON_DISPATCH_FAIL:
         {
           str = (char *)"DISPATCH_FAIL";
         }
         break;
         case LSG_REASON_TIMEOUT:
         {
           str = (char *)"TIMEOUT";
         }
         break;
         case LSG_REASON_FRAMING_FAIL:
         {
           str = (char *)"FRAMING_FAILED";
         }
         break;
         case LSG_REASON_NO_CONTX:
         {
           str = (char *)"NO_CONTEXT";
         }
         break;
         case LSG_REASON_VALID_FAIL:
         {
           str = (char *)"VALIDATION_FAILED";
         }
         break;
         case LSG_REASON_UPDATE_FAIL:
         {
           str = (char *)"UPDATE_FAILED";
         }
         break;
         case LSG_REASON_ENUM_FAIL:
         {
           str = (char *)"ENUMERATION_FAILED";
         }
         break;
         case LSG_REASON_ABORT:
         {
           str = (char *)"ABORT";
         }
         break;
         case LSG_REASON_INVALID_LAYOUT:
         {
           str = (char *)"INVALID_LAYOUT";
         }
         break;
         case LSG_REASON_ABORT_NPOSSIBLE:
         {
           str = (char *)"ABORT_NOT_POSSIBLE";
         }
         break;
         case LSG_REASON_INVALID_BKUP_DATA:
         {
           str = (char *)"INVALID_BACKUP_DATA";
         }
         break;
         default:
         {
           str = (char *)"UNKNOWN_ERROR";
         }
         break;
       }
      }
    }
    break;
    default:
    {
      str = (char *)"UNKNOWN_LAYER";
    }
    break;
  }
  logger.logMsg (TRACE_FLAG, 0,
                 "Leaving getReasonStr Layer Id <%d>",layer);
  return str;
}

char *
getLayerIdStr(int layer)
{
  char *str ;
  logger.logMsg (TRACE_FLAG, 0,
                 "Entering getLayerIdStr");
  switch (layer)
  {
    case BP_AIN_SM_TCA_LAYER:
    {
       str = (char *)"TCAP_LAYER";
    }
    break; 
    case BP_AIN_SM_SCC_LAYER:
    {
       str = (char *)"SCCP_LAYER";
    }
    break; 
    case BP_AIN_SM_MTP3_LAYER:
    {
       str = (char *)"MTP3_LAYER";
    }
    break; 
    case BP_AIN_SM_MTP2_LAYER:
    {
       str = (char *)"MTP2_LAYER";
    }
    break; 
    case BP_AIN_SM_M3U_LAYER:
    {
       str = (char *)"M3UA_LAYER";
    }
    break; 
    case BP_AIN_SM_SCT_LAYER:
    {
       str = (char *)"SCTP_LAYER";
    }
    break; 
    case BP_AIN_SM_TUC_LAYER:
    {
       str = (char *)"TUCL_LAYER";
    }
    break; 
    case BP_AIN_SM_PSF_TCAP_LAYER:
    {
       str = (char *)"PSF_TCAP_LAYER";
    }
    break; 
    case BP_AIN_SM_IU_LAYER:
    {
       str = (char *)"IU_LAYER";
    }
    break; 
    case BP_AIN_SM_PSF_SCCP_LAYER:
    {
       str = (char *)"PSF_SCCP_LAYER";
    }
    break; 
    case BP_AIN_SM_LDF_MTP3_LAYER:
    {
       str = (char *)"LDF_MTP3_LAYER";
    }
    break; 
    case BP_AIN_SM_PSF_MTP3_LAYER:
    {
       str = (char *)"PSF_MTP3_LAYER";
    }
    break; 
    case BP_AIN_SM_LDF_M3UA_LAYER:
    {
       str = (char *)"LDF_M3UA_LAYER";
    }
    break; 
    case BP_AIN_SM_PSF_M3UA_LAYER:
    {
       str = (char *)"PSF_M3UA_LAYER";
    }
    break; 
    case BP_AIN_SM_SG_LAYER:
    {
       str = (char *)"SG_LAYER";
    }
    break; 
    case BP_AIN_SM_SH_LAYER:
    {
       str = (char *)"SH_LAYER";
    }
    break; 
    case BP_AIN_SM_MR_LAYER:
    {
       str = (char *)"MR_LAYER";
    }
    break; 
    case BP_AIN_SM_ALL_LAYER:
    {
       str = (char *)"ALL_LAYER";
    }
    break; 
    case BP_AIN_SM_AIN_LAYER:
    {
       str = (char *)"AIN_LAYER";
    }
    break; 
    default:
    {
       str = (char *)"INVALID_LAYER";
    }
    break; 
  }

  logger.logMsg (TRACE_FLAG, 0,
                 "Leaving getLayerIdStr");

  return str;
}
/******************************************************************************
*
*     Fun:   updStkRspMap()
*
*     Desc:  handle stack response to last operation
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
int
INGwSmRequest::updStkRspMap(INGwSmStackMsg *smStkMsg)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Entering INGwSmRequest::updStkRspMap", miTransactionId);
  StackReqResp *stkRsp = NULL;
  Ss7SigtranSubsReq *reqInfo;
  U16 cmdStatus = LCM_PRIM_OK;
  Bool successFlag = FALSE;
  map<int,StackReqResp*>::iterator itr;
  Ss7SigtranStackResp rsp;
  int liScenario;
  int layer;
  int status = -1;
  INGwSmBlockingContext *lpCtx;
  INGwSmBlockingContext unBlkCtx;
  INGwSmRequestTable *requestTable = mpDist->getRequestTable();


  itr = mpDist->mStkReqRspMap.find (smStkMsg->stkMsg.miTransId);
  if(itr == mpDist->mStkReqRspMap.end())
  {
    logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> entry does not exist in StackReqRespMap",
              miTransactionId);
    return BP_AIN_SM_FAIL;
  }
  stkRsp = itr->second;
  layer = smStkMsg->stkMsg.miLayerId;
  switch (smStkMsg->stkMsg.miLayerId)
  {
    case BP_AIN_SM_SCC_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
      "BP_AIN_SM_SCC_LAYER response received txnType:<%d>", stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.sp.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.sp.cfm.reason;

      if(smStkMsg->stkMsg.lyr.sp.hdr.elmId.elmnt == STROUT)
      {
        status = smStkMsg->stkMsg.lyr.sp.t.ssta.s.spRteSta.pcSta.status;
        logger.logMsg (TRACE_FLAG, 0, "DPC Status received <%d>", status);
      }

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_M3U_LAYER:
    {
      int i=0,j=0;
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_M3U_LAYER response received txnType:<%d>", stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.it.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.it.cfm.reason;

      if(smStkMsg->stkMsg.lyr.it.hdr.elmId.elmnt == STITPS)
      {
        status = smStkMsg->stkMsg.lyr.it.t.ssta.s.psSta.asSt;
        logger.logMsg (TRACE_FLAG, 0,
              "PS Status received <%d> <%d>", status, sizeof(U8));
      }
      if(smStkMsg->stkMsg.lyr.it.hdr.elmId.elmnt == STITPSP)
      {
        status = 0;
        for (int i = 0; i != 4; ++i) {
          status += smStkMsg->stkMsg.lyr.it.t.ssta.s.pspSta.assocSta[i].aspSt << (24 - i * 8); 
        }
    
        logger.logMsg (TRACE_FLAG, 0, "PSP Status received <%d> ",status );
      }


      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
        mpDist->ss7SigtranStackRespPending--;
        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }
      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_LDF_M3UA_LAYER: 
    {      
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_LDF_M3UA_LAYER response received txnType:<%d>", 
			stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.dv.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.dv.cfm.reason;
    
      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      
        mpDist->ss7SigtranStackRespPending--;
        
        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      } 
           
      stkRsp->txnStatus = COMPLETED;
    }   
    break;

    case BP_AIN_SM_MTP3_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_MTP3_LAYER response received txnType:<%d>", stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.sn.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.sn.cfm.reason;

      if(smStkMsg->stkMsg.lyr.sn.hdr.elmId.elmnt == STDLSAP)
      {
        status = smStkMsg->stkMsg.lyr.sn.t.ssta.s.snDLSAP.state;
        logger.logMsg (TRACE_FLAG, 0, "Link Status received <%d>", status);
      }
      if(smStkMsg->stkMsg.lyr.sn.hdr.elmId.elmnt == STLNKSET)
      {
        status = smStkMsg->stkMsg.lyr.sn.t.ssta.s.snLnkSet.state;
        logger.logMsg (TRACE_FLAG, 0, "LinkSet Status received <%d>", status);
      }
 
      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_MTP2_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_MTP2_LAYER response received txnType:<%d>", stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.sd.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.sd.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_SG_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_SG_LAYER response received txnType:<%d>", stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.sg.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.sg.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_RELAY_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_RELAY_LAYER response received txnType:<%d>", stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.ry.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.ry.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    
    case BP_AIN_SM_LDF_MTP3_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_LDF_MTP3_LAYER response received txnType:<%d>", 
			stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.dn.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.dn.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_TCA_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_TCA_LAYER response received txnType:<%d>", stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.st.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.st.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_PSF_TCAP_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_PSF_TCAP_LAYER response received txnType:<%d>", 
			stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.zt.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.zt.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_PSF_SCCP_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_PSF_SCCP_LAYER response received txnType:<%d>", 
			stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.zp.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.zp.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
        "before decrementing ss7SigtranStackRespPending:<%d>", 
				mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_PSF_MTP3_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
              "BP_AIN_SM_PSF_MTP3_LAYER response received txnType:<%d>", 
							stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.zn.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.zn.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_PSF_M3UA_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
              "BP_AIN_SM_PSF_M3UA_LAYER response received txnType:<%d>", 
							stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.zn.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.zn.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_SH_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_SH_LAYER response received txnType:<%d>", stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.sh.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.sh.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_MR_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_MR_LAYER response received txnType:<%d>", stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.mr.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.mr.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_SCT_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_SCT_LAYER response received txnType:<%d>", stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.sb.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.sb.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    case BP_AIN_SM_TUC_LAYER:
    {
      logger.logMsg (TRACE_FLAG, 0,
			"BP_AIN_SM_TUC_LAYER response received txnType:<%d>", stkRsp->txnType);
      stkRsp->resp.status = smStkMsg->stkMsg.lyr.hi.cfm.status;
      stkRsp->resp.reason = smStkMsg->stkMsg.lyr.hi.cfm.reason;

      if (stkRsp->resp.status != LCM_PRIM_OK_NDONE)
      {
        logger.logMsg (TRACE_FLAG, 0,
           "before decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);

        mpDist->ss7SigtranStackRespPending--;

        logger.logMsg (TRACE_FLAG, 0,
           "after decrementing ss7SigtranStackRespPending:<%d>", 
					 mpDist->ss7SigtranStackRespPending);
      }

      stkRsp->txnStatus = COMPLETED;
    }
    break;
    default:
      logger.logMsg (ERROR_FLAG, 0,
			"Unknown Layer response received txnType:<%d>", stkRsp->txnType);
      return BP_AIN_SM_FAIL;
  }
  if ((mpDist->ss7SigtranStackRespPending == 0) && 
																	(stkRsp->txnType != ROLLBACK_TXN))
  {
    logger.logMsg (TRACE_FLAG, 0,
              " All the responses for the command received");
    for (itr = mpDist->mStkReqRspMap.begin(); 
													itr != mpDist->mStkReqRspMap.end(); itr++)
    {
      stkRsp = itr->second;
      if (stkRsp->resp.status == LCM_PRIM_NOK)
      {
        cmdStatus = LCM_PRIM_NOK;
      }
      if (stkRsp->resp.status == LCM_PRIM_OK)
      {
        successFlag= TRUE;
      }
    }
  }
  if (cmdStatus == LCM_PRIM_NOK)
  {
    logger.logMsg (ERROR_FLAG, 0,
              "command execution failed");
  }
  /* rollback required if command is partially successfull */
#if 0 //Currently commenting rollback option
  if ((cmdStatus == LCM_PRIM_NOK) && (successFlag == TRUE) && (layer != BP_AIN_SM_SG_LAYER))
  {
    logger.logMsg (TRACE_FLAG, 0,
              "command partially successful, rollback initiated");
    for (itr = mpDist->mStkReqRspMap.begin(); 
				itr != mpDist->mStkReqRspMap.end(); itr++)
    {
      stkRsp = itr->second;
      if (stkRsp->resp.status == LCM_PRIM_OK)
      {
        mpDist->ss7SigtranStackRespPending++;
        
    logger.logMsg (TRACE_FLAG, 0,
              " layer = %d , cmdType = %d ",stkRsp->stackLayer,stkRsp->cmdType);
        rollBack (stkRsp);
      }
    }
  }
#endif

  /* INCTBD: Now send rspList to EMS 
     response has to be sent per proc change the logic */
  if (!mpDist->ss7SigtranStackRespPending)
  {
    logger.logMsg (TRACE_FLAG, 0,
              "sending response to EMS");
    // set default value as success
		unBlkCtx.status = 0;	
    while(!(mpDist->mStkReqRspMap.empty()))
    {
      itr = mpDist->mStkReqRspMap.begin();

      logger.logMsg (TRACE_FLAG, 0,
              "Processing response of TransId <%d>", itr->first);

      stkRsp = itr->second;
      INGwSmRequest *lpReq =
                    requestTable->getRequest (itr->first);
      if (stkRsp->txnType != ROLLBACK_TXN)
      {
        rsp.procId = stkRsp->resp.procId;
        rsp.status = stkRsp->resp.status;
        rsp.stackLayer = getLayerIdStr (smStkMsg->stkMsg.miLayerId);
        rsp.reason = stkRsp->resp.reason;
        rsp.reasonStr = getReasonStr (stkRsp->resp.reason, 
			  																 smStkMsg->stkMsg.miLayerId);

				// Commenting as no body reads from emsRespList
        // mpDist->emsRespList.push_back (rsp);

         if (lpReq)
         {
           lpCtx = lpReq->getBlockingContext (liScenario);
           logger.logMsg (TRACE_FLAG, 0,
              "CCM thread requestID <%d>, unblockStatus[%d]", lpCtx->requestId, unBlkCtx.status);

					 // This check has been introduced to make sure that
					 // failure is returned back to EMS incase multiple 
					 // transaction. If any command has failed then failure should
					 // be returned. 
					 if (unBlkCtx.status == 0)
					 {
						unBlkCtx.requestId = lpCtx->requestId;
           	unBlkCtx.returnValue = BP_AIN_SM_OK;
	   				unBlkCtx.status      = status;
           	memcpy(&unBlkCtx.resp, &rsp, sizeof(Ss7SigtranStackResp));
					 }
         }
      }
      if (lpReq)
      {
        //remove the request from the request table and delete it
        requestTable->removeRequest (itr->first, lpReq);

				logger.logMsg(VERBOSE_FLAG, 0, "Request TxnId:<%d>, ResponseTID<%d>", 
				lpReq->miTransactionId, miTransactionId);

				// This is case where multiple transaction are sent corresponding
				// to one command e.g., AddUserPart. On getting final response
				// need to delete lpRequest for initial responses. 
				// Self will be deleted from Distributor. 
				if(lpReq->miTransactionId != miTransactionId)
      		delete lpReq;
      }

      mpDist->mStkReqRspMap.erase (itr);

      //delete stkRsp;
    }
    logger.logMsg (TRACE_FLAG, 0,
              "unblocking the CCM thread requestID <%d>", unBlkCtx.requestId);
    mpDist->notifyCCM (&unBlkCtx);
    logger.logMsg (TRACE_FLAG, 0,
              "command execution completed");
  }
  
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Leaving INGwSmRequest::updStkRspMap", miTransactionId);
  return BP_AIN_SM_OK;

}  
/******************************************************************************
*
*     Fun:   handleResponse()
*
*     Desc:  handle stack response to last operation
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
int
INGwSmRequest::handleResponse (INGwSmQueueMsg *apFromStack)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Entering INGwSmRequest::handleResponse", miTransactionId);

  /*
   * First we need to ensure if we were indeed waiting for this
   * response.
   */
  int liLayer, liOper, liSubOp;

  //decode the state to get the layer and operation type
  if (decodeState (miMajorState, liLayer, liOper, liSubOp) == BP_AIN_SM_FAIL)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d>: State could not be decoded", miTransactionId);

    delete apFromStack;

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }

  //extract the layer and operation from the response
  
  /*
   * Now we need to validate whether the response was successful
   * or not. it will be fatal to proceed further
   */
  int liRespOper = apFromStack->t.stackMsg.stkMsgTyp;
  int liRespLayer = apFromStack->t.stackMsg.stkMsg.miLayerId;

  if (liRespLayer != liLayer)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> Response received for Layer <%d> instead of Layer <%d>",
      miTransactionId, liRespLayer, liLayer);

    delete apFromStack;
    return BP_AIN_SM_OK;
  }

  switch (liRespOper)
  {
    case BP_AIN_SM_STKOP_CFGCFM:
    {
      if (liOper != BP_AIN_SM_OPTYPE_CFG)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> Conf Confirm received for Non Config Request", miTransactionId);

        delete apFromStack;

        return BP_AIN_SM_OK;
      }
      break;
    }
    case BP_AIN_SM_STKOP_CTLCFM:
    {
      if (liOper != BP_AIN_SM_OPTYPE_CTL)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> Control Confirm received for Non Control Request",
          miTransactionId);

        delete apFromStack;

        return BP_AIN_SM_OK;
      }
      break;
    }
    case BP_AIN_SM_STKOP_STACFM:
    {
      if (liOper != BP_AIN_SM_OPTYPE_STA)
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d> Status Confirm received for Non Status Request", 
          miTransactionId);

        delete apFromStack;

        return BP_AIN_SM_OK;
      }
      break;
    }
    default:
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d> Unknown response type encountered", miTransactionId);

      delete apFromStack;

      return BP_AIN_SM_OK;
      break;
    }
  }


  int liRetVal = BP_AIN_SM_FAIL;

  switch (apFromStack->t.stackMsg.stkMsgTyp)
  {
    case BP_AIN_SM_STKOP_CTLCFM:
    case BP_AIN_SM_STKOP_CFGCFM:
    case BP_AIN_SM_STKOP_STACFM:
    {
      /*logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>, Layer <%d> Confirm Status <%d> for <%d> request received",
        miTransactionId, apFromStack->t.stackMsg.stkMsg.miLayerId, 
        apFromStack->t.stackMsg.stkMsg.lyr.ie.cfm.status, liOper);
*/
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>, Response from Layer<%d> for liOper<%d>",
        miTransactionId, apFromStack->t.stackMsg.stkMsg.miLayerId, 
        liOper);
      switch (apFromStack->t.stackMsg.stkMsg.miLayerId)
      {
#if 0
        case BP_AIN_SM_AIN_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.ie.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.ie.cfm.status == LCM_PRIM_OK_NDONE)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> Response for AIN Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.ie.cfm.reason);

            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.ie.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> Response for AIN Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.ie.cfm.reason);

            liRetVal = BP_AIN_SM_FAIL;
          }
          break;
        }
#endif
        case BP_AIN_SM_MR_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.mr.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_MR_LAYER: Response MR from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.mr.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_MR_LAYER: Response for MR Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.mr.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.mr.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_MR_LAYER: Response for MR Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.mr.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG MR layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.mr.cfm.reason,BP_AIN_SM_MR_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_SG_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.sg.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_SG_LAYER: Response SG from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.sg.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_SG_LAYER: Response for SG Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sg.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.sg.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_SG_LAYER: Response for SG Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sg.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG SG layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.sg.cfm.reason,BP_AIN_SM_SG_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_RELAY_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.ry.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_RELAY_LAYER: Response for RY from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.ry.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_RELAY_LAYER: Response for RY Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.ry.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.ry.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_RELAY_LAYER: Response for RY Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.ry.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG RELAY layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.ry.cfm.reason,BP_AIN_SM_RELAY_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }

        case BP_AIN_SM_SH_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.sh.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_SH_LAYER: Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.sh.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_SH_LAYER: Response for SH Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sh.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.sh.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_SH_LAYER: Response for SH Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sh.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG SH layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.sh.cfm.reason,BP_AIN_SM_SH_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_TCA_LAYER:
        {
#ifdef INC_DLG_AUDIT
        /** Check for AUDIT_INC command's control cfm, and then call fthandler function **/
          if((miTransactionId == BP_AIN_SM_AUDIT_TRANSID) && 
            (mpDist->getTcapProvider()->getSapCnt() == mpDist->getAuditCounter())){

            logger.logINGwMsg(false,ALWAYS_FLAG,0,"Got audit cfm");
            mpDist->handleAuditCfm();
            mpDist->resetAuditCounter();
          }
#endif
          if (apFromStack->t.stackMsg.stkMsg.lyr.st.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_TCA_LAYER: Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.st.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_TCA_LAYER: Response for TCAP Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.st.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.st.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_TCA_LAYER: Response for TCAP Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.st.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG TCAP layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.st.cfm.reason,BP_AIN_SM_TCA_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_PSF_TCAP_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.zt.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_PSF_TCAP_LAYER: Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.zt.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_PSF_TCAP_LAYER: Response for TCAP Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.zt.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.zt.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_PSF_TCAP_LAYER: Response for TCAP Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.zt.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG PSF TCAP layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.zt.cfm.reason,BP_AIN_SM_PSF_TCAP_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_SCC_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.sp.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_SCC_LAYER: Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.sp.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_SCC_LAYER: Response for SCCP Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sp.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.sp.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_SCC_LAYER: Response for SCCP Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sp.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG SCCP layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.sp.cfm.reason,BP_AIN_SM_SCC_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_PSF_SCCP_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.zp.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_PSF_SCCP_LAYER: Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.zp.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_PSF_SCCP_LAYER: Response for PSF-SCCP Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sp.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.zp.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_PSF_SCCP_LAYER: Response for PSF-SCCP Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sp.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG PSF SCCP layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.zp.cfm.reason,BP_AIN_SM_PSF_SCCP_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_PSF_M3UA_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.zv.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_PSF_M3UA_LAYER: Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.zv.cfm.status == LCM_PRIM_OK_NDONE)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_PSF_M3UA_LAYER: Response for PSF_M3UA Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.zv.cfm.reason);

            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.zv.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_PSF_M3UA_LAYER: Response for PSF_M3UA Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.zv.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG PSF M3UA layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.zv.cfm.reason,BP_AIN_SM_PSF_M3UA_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_LDF_M3UA_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.dv.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_LDF_M3UA_LAYER: Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.dv.cfm.status == LCM_PRIM_OK_NDONE)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_LDF_M3UA_LAYER: Response for LDF_M3UA Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.dv.cfm.reason);

            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.dv.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_LDF_M3UA_LAYER: Response for LDF_M3UA Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.dv.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG LDF M3UA layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.dv.cfm.reason,BP_AIN_SM_LDF_M3UA_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }

        case BP_AIN_SM_M3U_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.it.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_M3U_LAYER: Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.it.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> Response for M3UA Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.it.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.it.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> Response for M3UA Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.it.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG M3UA layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.it.cfm.reason,BP_AIN_SM_M3U_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_SCT_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.sb.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_SCT_LAYER Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.sb.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> Response for SCTP Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sb.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.sb.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> Response for SCTP Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sb.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG SCTP layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.sb.cfm.reason,BP_AIN_SM_SCT_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_TUC_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.hi.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_TUC_LAYER: Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.hi.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> Response for TUCL Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.hi.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.hi.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> Response for TUCL Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.hi.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG TUCL layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.hi.cfm.reason,BP_AIN_SM_TUC_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_PSF_MTP3_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.zn.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_PSF_MTP3_LAYER: Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.zn.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_PSF_MTP3_LAYER: Response for PSF_MTP3 Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.zn.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.zn.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_PSF_MTP3_LAYER: Response for PSF_MTP3 Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.zn.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG PSF MTP3 layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.zn.cfm.reason,BP_AIN_SM_PSF_MTP3_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_LDF_MTP3_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.dn.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_LDF_MTP3_LAYER: Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.dn.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_LDF_MTP3_LAYER: Response for LDF_MTP3 Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.dn.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.dn.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_LDF_MTP3_LAYER: Response for LDF_MTP3 Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.dn.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG LDF MTP3 layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.dn.cfm.reason,BP_AIN_SM_LDF_MTP3_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_MTP3_LAYER:
        {
          if (apFromStack->t.stackMsg.stkMsg.lyr.sn.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_MTP3_LAYER: Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.sn.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> BP_AIN_SM_MTP3_LAYER: Response for MTP3 Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sn.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.sn.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> BP_AIN_SM_MTP3_LAYER: Response for MTP3 Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sn.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG MTP3 layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.sn.cfm.reason,BP_AIN_SM_MTP3_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }
            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        case BP_AIN_SM_MTP2_LAYER:
        {

          if (apFromStack->t.stackMsg.stkMsg.lyr.sd.cfm.status == LCM_PRIM_OK)
          {
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> Response from Stack was successful", miTransactionId);
            liRetVal = BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.sd.cfm.status == LCM_PRIM_OK_NDONE)
          { 
            logger.logMsg (TRACE_FLAG, 0,
              "TID <%d> Response for MTP2 Layer is PENDING with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sd.cfm.reason);
        
            delete apFromStack;
            return BP_AIN_SM_OK;
          }
          else if (apFromStack->t.stackMsg.stkMsg.lyr.sd.cfm.status == LCM_PRIM_NOK)
          {
            logger.logMsg (ERROR_FLAG, 0,
              "TID <%d> Response for MTP2 Layer was FAILED with reason <%d>",
              miTransactionId, apFromStack->t.stackMsg.stkMsg.lyr.sd.cfm.reason);
            if(mpOrigReq->t.ccmOper.txnType == GEN_CFG_TXN) {
              char msg[256];
              memset(msg, 0, sizeof(msg));
              sprintf(msg,
                 "handleResponse()::GENERAL CONFIG MTP2 layer failed with reason<%s>",
                  getReasonStr(apFromStack->t.stackMsg.stkMsg.lyr.sd.cfm.reason,BP_AIN_SM_MTP2_LAYER));
              g_exit((char *)"INGwSmRequest.C", __LINE__, msg);
            }

            liRetVal = BP_AIN_SM_FAIL;
          }
          updStkRspMap (&apFromStack->t.stackMsg);
          break;
        }
        default:
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> Stack Message received for unknown Layer", miTransactionId);

          //need to free the message received from the stack
          if (apFromStack)
            delete apFromStack;

          //set the return value for Blocking Context to be used by Distributor
          meBlockCtx.returnValue = BP_AIN_SM_FAIL;

          return BP_AIN_SM_SCENARIO_COMP;
          break;
        }
      }
      break;
    }

    default:
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d> Unknown Stack Message type received", miTransactionId);

      //need to free the message received from the stack
      if (apFromStack)
        delete apFromStack;

      //set the return value for Blocking Context to be used by Distributor
      meBlockCtx.returnValue = BP_AIN_SM_FAIL;

      return BP_AIN_SM_SCENARIO_COMP;
      break;
    }
  }//switch

  //check if the operation had failed
  if (liRetVal == BP_AIN_SM_FAIL)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d> Operation Failed ", miTransactionId);

    //need to free the message received from the stack
    if (apFromStack)
      delete apFromStack;

    //set the return value for Blocking Context to be used by Distributor
    meBlockCtx.returnValue = BP_AIN_SM_FAIL;

    return BP_AIN_SM_SCENARIO_COMP;
  }

  /*
   * In case the response is of Status request we can let the response be
   * handler by the handler object which will take the appropriate action.
   */
  if (apFromStack->t.stackMsg.stkMsgTyp == BP_AIN_SM_STKOP_STACFM)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> Status response received. Passing it to reqHdlr object.", 
      miTransactionId);

    mpReqHdlr->handleResponse (apFromStack);
  }


  //need to free the message received from the stack
  if (apFromStack)
    delete apFromStack;

  /*
   *
   * Here, we need handle the case where the confirm might not be a sufficient
   * condition to proceed to next state or operation. For ASPUP, ASPDN, ASPAC,
   * ASPIA, ESTASS, TRMASS messages, we need to wait for the Alarm to occur for
   * the request to be successful or failure. 
   * TRMASS_FAIL and ASPDN_FAIL alarm are absent so removing them for this case
   */
  if  (miMinorState == BP_AIN_SM_SUBTYPE_ESTASS)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> Minor State <%d> : Need to wait for the alarm", 
      miTransactionId, miMinorState);

    //set the flag to indicate that the request is waiting for alarm
    miIsWaitingForAlarm = 1;

#if TO_BE_REVIEWED_BY_ARCHANA
    //update the distributors list of requests waiting for the alarm
    if (mpDist->waitForAlarm (BP_AIN_SM_ALMOP_ASSOC_ESTAB_OK, 
                              miTransactionId) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d>: Unable to notify Distributor to wait for alarm <%d>",
        miTransactionId, miMinorState);
    }
#endif

    return BP_AIN_SM_OK;
  }
  else if  (miMinorState == BP_AIN_SM_SUBTYPE_SNDAUP)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> Minor State <%d> : Need to wait for the alarm", 
      miTransactionId, miMinorState);

    //set the flag to indicate that the request is waiting for alarm
    miIsWaitingForAlarm = 1;

#if TO_BE_REVIEWED_BY_ARCHANA
    //update the distributors list of requests waiting for the alarm
    if (mpDist->waitForAlarm (BP_AIN_SM_ALMOP_ASPUP_ACK, 
                              miTransactionId) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d>: Unable to notify Distributor to wait for alarm <%d>",
        miTransactionId, miMinorState);
    }
#endif

    return BP_AIN_SM_OK;
  }
  else if  (miMinorState == BP_AIN_SM_SUBTYPE_SNDAAC)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> Minor State <%d> : Need to wait for the alarm", 
      miTransactionId, miMinorState);

    //set the flag to indicate that the request is waiting for alarm
    miIsWaitingForAlarm = 1;

#if TO_BE_REVIEWED_BY_ARCHANA
    //update the distributors list of requests waiting for the alarm
    if (mpDist->waitForAlarm (BP_AIN_SM_ALMOP_ASPAC_ACK, 
                              miTransactionId) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d>: Unable to notify Distributor to wait for alarm <%d>",
        miTransactionId, miMinorState);
    }
#endif

    return BP_AIN_SM_OK;
  }
  else if  (miMinorState == BP_AIN_SM_SUBTYPE_SNDAIA)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d> Minor State <%d> : Need to wait for the alarm", 
      miTransactionId, miMinorState);

    //set the flag to indicate that the request is waiting for alarm
    miIsWaitingForAlarm = 1;

#if TO_BE_REVIEWED_BY_ARCHANA
    //update the distributors list of requests waiting for the alarm
    if (mpDist->waitForAlarm (BP_AIN_SM_ALMOP_ASPIA_ACK, 
                              miTransactionId) != BP_AIN_SM_OK)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d>: Unable to notify Distributor to wait for alarm <%d>",
        miTransactionId, miMinorState);
    }
#endif

    return BP_AIN_SM_OK;
  }

  /*
   * For TRMASS and ASPDN, if the confirm is success then move the state
   * of PSP
   */

  else if (miMinorState == BP_AIN_SM_SUBTYPE_TRMASS && 
           liRetVal == BP_AIN_SM_OK)
  {
    INGwSmRequestContext *lpReqContext =
                                 mpReqHdlr->getRequestContext();

    //move the PSP to the new state
    mpRepository->setPspState (lpReqContext->pspId, BP_AIN_SM_PSP_ST_DOWN);
  }
  else if (miMinorState == BP_AIN_SM_SUBTYPE_SNDADN && 
           liRetVal == BP_AIN_SM_OK)
  {
    INGwSmRequestContext *lpReqContext =
                                 mpReqHdlr->getRequestContext();

    //move the PSP to the new state
    mpRepository->setPspState (lpReqContext->pspId, BP_AIN_SM_PSP_ST_ESTASS);
  }

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Calling SendRequest ",miTransactionId);

  //send the next request to the stack
  liRetVal = sendRequest ();

  while (liRetVal == BP_AIN_SM_SEND_NEXT)
  {
    liRetVal = sendRequest (liRetVal);
  }

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Leaving INGwSmRequest::handleResponse", miTransactionId);
  return liRetVal;
}

/******************************************************************************
*
*     Fun:   sendRequest()
*
*     Desc:  send the request to the stack
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
int
INGwSmRequest::sendRequest (int aiLastRetVal)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Entering INGwSmRequest::sendRequest," 
    "aiLastRetVal:<%d>, MinorState:<%d>", 
     miTransactionId, aiLastRetVal, miMinorState);

  /* 
   * For the RTECFG, PSCFG, PSPCFG we need to iterate to configure
   * all the indexed nodes in the DOM Tree
   */
  bool lbNextState = true;

  if  (aiLastRetVal != BP_AIN_SM_SEND_NEXT && 
       (//miMinorState == BP_AIN_SM_SUBTYPE_RTECFG     ||
   //    miMinorState == BP_AIN_SM_SUBTYPE_LNKSETCFG   ||
       miMinorState == BP_AIN_SM_SUBTYPE_RTECFG_PEER ||
       //miMinorState == BP_AIN_SM_SUBTYPE_LSPCFG      ||
       miMinorState == BP_AIN_SM_SUBTYPE_PSPSTA      ||
     //  miMinorState == BP_AIN_SM_SUBTYPE_PSCFG       ||
      // miMinorState == BP_AIN_SM_SUBTYPE_PSPCFG      ||
       miMinorState == BP_AIN_SM_SUBTYPE_ESTASS      ||
       //miMinorState == BP_AIN_SM_SUBTYPE_TRMASS      ||
       miMinorState == BP_AIN_SM_SUBTYPE_SNDAUP      ||
       miMinorState == BP_AIN_SM_SUBTYPE_SNDAAC      ||
       //miMinorState == BP_AIN_SM_SUBTYPE_SNDADN      ||
       miMinorState == BP_AIN_SM_SUBTYPE_ENASAP      ||
       miMinorState == BP_AIN_SM_SUBTYPE_ENATRC      
       //miMinorState == BP_AIN_SM_SUBTYPE_SNDAIA
      ))
  {
    logger.logMsg (TRACE_FLAG, 0, "Indexed cfg in progress");

    /*
     * For the above subops, we need to call sendrequest on the old
     * reqHdlr and move to next state only when we get a response of
     * BP_AIN_SM_INDEX_OVER. 
     */
    int liRetVal = mpReqHdlr->sendRequest (0,mpReqContext);

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      //set the return value for Blocking Context to be used by Distributor
      meBlockCtx.returnValue = BP_AIN_SM_FAIL;

      return BP_AIN_SM_SCENARIO_COMP;
    }
    else if (liRetVal == BP_AIN_SM_SEND_NEXT)
    {
      logger.logMsg (WARNING_FLAG, 0,
        "TID <%d> This operation doesn't need a response so move to next state", 
        miTransactionId);

      return BP_AIN_SM_SEND_NEXT;
    }
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (WARNING_FLAG, 0,
        "TID <%d> Index is over so need to move to next state", 
        miTransactionId);
    }
    else if (liRetVal == BP_AIN_SM_INDEX_REPEAT)
    {
      lbNextState = false;
    }
    else if (liRetVal == BP_AIN_SM_OK)
    {
      lbNextState = false;
    }
  }

  if (lbNextState == true)
  {
    logger.logMsg (TRACE_FLAG, 0,
      "TID <%d>: Need to move to the Next State", miTransactionId);

    //destroy the previous request
    if (mpReqHdlr)
      delete mpReqHdlr;

    mpReqHdlr = 0;

    INGwSmIntVector::iterator iter = meStateVector.begin();

    logger.logMsg (TRACE_FLAG, 0,
        "meStateVector size <%d>",meStateVector.size());
    //obtain the next state
    if (iter == meStateVector.end())
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: State Vector is NULL. Scenario Completed", miTransactionId);

      //set the return value for Blocking Context to be used by Distributor
      meBlockCtx.returnValue = BP_AIN_SM_OK;

      return BP_AIN_SM_SCENARIO_COMP;
    }

    int liState = *(iter);

    meStateVector.erase (iter++);

    logger.logMsg (TRACE_FLAG, 0,
        "meStateVector size <%d>",meStateVector.size());

    int liLayer, liOper, liSubOp;

    //decode the state to get the layer and operation type
    if (decodeState (liState, liLayer, liOper, liSubOp) == BP_AIN_SM_FAIL)
    {     
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: State could not be decoded", miTransactionId);

      //set the return value for Blocking Context to be used by Distributor
      meBlockCtx.returnValue = BP_AIN_SM_FAIL;

      return BP_AIN_SM_SCENARIO_COMP;
    }         
    logger.logMsg (TRACE_FLAG, 0,
        "liLayer<%d>,liOper<%d>,liSubOp<%d> ", liLayer,liOper,liSubOp );

    //set the major and minor states
    miMajorState = liState;
    miMinorState = liSubOp;

    if (createRequestHdlr (liLayer, liOper, liSubOp) == BP_AIN_SM_FAIL || 
      mpReqHdlr == 0)
    {     
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: Request Handler could not be created", miTransactionId);

      //set the return value for Blocking Context to be used by Distributor
      meBlockCtx.returnValue = BP_AIN_SM_FAIL;

      return BP_AIN_SM_SCENARIO_COMP;
    }         

    int liRetVal = mpReqHdlr->sendRequest (0,mpReqContext);

    /*
     *  The return valye can be either Failure or Success or INDEX_OVER.
     *  INDEX_OVER means that the same suboperation needs to be invoked
     *  again for a different set of parameters. Hence the scenario and
     *  state needs to be maintained for now. On Failure, we need to 
     *  clean up the scenario in the Stack Manager.
     */

    if (liRetVal == BP_AIN_SM_FAIL)
    {
      logger.logMsg (TRACE_FLAG, 0,
        "TID <%d>: sendRequest Failed", miTransactionId);

      //set the return value for Blocking Context to be used by Distributor
      meBlockCtx.returnValue = BP_AIN_SM_FAIL;

      return BP_AIN_SM_SCENARIO_COMP;
    }
    else if (liRetVal == BP_AIN_SM_SEND_NEXT)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d> This operation doesn't need a response so move to next state", 
        miTransactionId);

      return BP_AIN_SM_SEND_NEXT;
    }
    else if (liRetVal == BP_AIN_SM_INDEX_OVER)
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d>: Index can not be over during the start of the next state",
        miTransactionId);

      //set the return value for Blocking Context to be used by Distributor
      meBlockCtx.returnValue = BP_AIN_SM_FAIL;

      return BP_AIN_SM_SCENARIO_COMP;
    }
  }

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Leaving INGwSmRequest::sendRequest", miTransactionId);

  return BP_AIN_SM_OK;
}

/******************************************************************************
*
*     Fun:   handleAlarm()
*
*     Desc:  handle alarm received in case the request needs to wait for it
*
*     Notes: None
*
*     File:  INGwSmRequest.C
*
*******************************************************************************/
int
INGwSmRequest::handleAlarm (INGwSmQueueMsg *apAlmMsg, int aiAlmState)
{
  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Entering INGwSmRequest::handleAlarm", miTransactionId);

  if (miIsWaitingForAlarm == 0 || mpReqHdlr == 0)
  {
    logger.logMsg (ERROR_FLAG, 0,
      "TID <%d>: Not waiting for any ALARM. return ERROR");

    return BP_AIN_SM_FAIL;
  }

  int liRetVal = BP_AIN_SM_OK;
  int liAlmState = (apAlmMsg->t.almOper.mOpType);

  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d>: Received Alarm <%d>",
    miTransactionId, liAlmState);

  /*
   * Now, check if the alarm received is the correct alarm required for this request
   */
  switch (miMinorState)
  {
    case BP_AIN_SM_SUBTYPE_ESTASS:
    {
      /*
       * Since the state was ESTASS, it has send ESTASS and is
       * waiting for ESTASS_ACK or ESTASS_FAIL
       */
      if (liAlmState == BP_AIN_SM_ALMOP_ASSOC_ESTAB_OK)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d>: ASSOC_ESTAB_OK received", miTransactionId);

        //retrieve the PSP Id from the ASPM context
        int liPspId = static_cast<int> (apAlmMsg->t.almOper.almOp.pspInfo.pspId);

        INGwSmRequestContext *lpReqContext = 
                                 mpReqHdlr->getRequestContext();

        if (liPspId != lpReqContext->pspId)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : PSP Id <%d> is not valid for request PSP <%d>",
            miTransactionId, liPspId, lpReqContext->pspId);

          liRetVal = BP_AIN_SM_SEND_NEXT;
        }
        else
        {
          //move the PSP to the new state
          mpRepository->setPspState (liPspId, BP_AIN_SM_PSP_ST_ESTASS);

          //reset the alarm waiting indication since the alarm is received
          miIsWaitingForAlarm = 0;
        }

      }
      else if (liAlmState == BP_AIN_SM_ALMOP_ASSOC_ESTAB_FAIL)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d>: ASSOC_ESTAB_FAIL received", miTransactionId);
        
        //retrieve the PSP Id from the ASPM context
        int liPspId = static_cast<int> (apAlmMsg->t.almOper.almOp.pspInfo.pspId);
        
        INGwSmRequestContext *lpReqContext = 
                                 mpReqHdlr->getRequestContext();
        
        if (liPspId != lpReqContext->pspId)
        { 
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : PSP Id <%d> is not valid for request PSP <%d>",
            miTransactionId, liPspId, lpReqContext->pspId);
          
          liRetVal = BP_AIN_SM_SEND_NEXT;
        }
        else
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : association establish failed for PSP <%d>",
            miTransactionId, lpReqContext->pspId);

          //set the return value for Blocking Context to be used by Distributor
          meBlockCtx.returnValue = BP_AIN_SM_FAIL;

          //reset the alarm waiting indication since the alarm is received
          miIsWaitingForAlarm = 0;

          liRetVal = BP_AIN_SM_SCENARIO_COMP;
        }

      }
      else if (liAlmState == BP_AIN_SM_ALMOP_COMM_DOWN)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d>: COMM_DOWN received", miTransactionId);
  
        //retrieve the PSP Id from the ASPM context
        int liPspId = static_cast<int> (apAlmMsg->t.almOper.almOp.commDn.pspId);
  
        INGwSmRequestContext *lpReqContext =
                                 mpReqHdlr->getRequestContext();
  
        if (liPspId != lpReqContext->pspId)
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : PSP Id <%d> is not valid for request PSP <%d>",
            miTransactionId, liPspId, lpReqContext->pspId);

          liRetVal = BP_AIN_SM_SEND_NEXT;
        }
        else
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : association establish failed for PSP <%d>",
            miTransactionId, lpReqContext->pspId);

          //set the return value for Blocking Context to be used by Distributor
          meBlockCtx.returnValue = BP_AIN_SM_FAIL;

          //reset the alarm waiting indication since the alarm is received
          miIsWaitingForAlarm = 0;

          liRetVal = BP_AIN_SM_SCENARIO_COMP;
        }

      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Minor state <%d> not expecting alarm <%d>",
          miTransactionId, miMinorState, liAlmState);

        liRetVal = BP_AIN_SM_FAIL;
      }
      break;
    }
    case BP_AIN_SM_SUBTYPE_SNDAUP:
    {
      /*
       * Since the state was ASPUP, it is waiting for ASPUP_ACK
       * or ASPUP_FAIL
       */
      if (liAlmState == BP_AIN_SM_ALMOP_ASPUP_ACK)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d>: ASPUP_ACK received", miTransactionId);
        
        //retrieve the PSP Id from the ASPM context
        int liPspId = static_cast<int> (apAlmMsg->t.almOper.almOp.aspmInfo.pspId);
        
        INGwSmRequestContext *lpReqContext = 
                                 mpReqHdlr->getRequestContext();
        
        if (liPspId != lpReqContext->pspId)
        { 
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : PSP Id <%d> is not valid for request PSP <%d>",
            miTransactionId, liPspId, lpReqContext->pspId);
          
          liRetVal = BP_AIN_SM_SEND_NEXT;
        }
        else
        {
          //move the PSP to the new state
          mpRepository->setPspState (liPspId, BP_AIN_SM_PSP_ST_ASPUP);

          //reset the alarm waiting indication since the alarm is received
          miIsWaitingForAlarm = 0;
        }

      }
      else if (liAlmState == BP_AIN_SM_ALMOP_ASPUP_FAIL)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d>: ASPUP_FAIL received", miTransactionId);
        
        //retrieve the PSP Id from the ASPM context
        int liPspId = static_cast<int> (apAlmMsg->t.almOper.almOp.pspInfo.pspId);
        
        INGwSmRequestContext *lpReqContext = 
                                 mpReqHdlr->getRequestContext();
        
        if (liPspId != lpReqContext->pspId)
        { 
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : PSP Id <%d> is not valid for request PSP <%d>",
            miTransactionId, liPspId, lpReqContext->pspId);
          
          liRetVal = BP_AIN_SM_SEND_NEXT;
        }
        else
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : ASP UP failed for PSP <%d>",
            miTransactionId, lpReqContext->pspId);

          //set the return value for Blocking Context to be used by Distributor
          meBlockCtx.returnValue = BP_AIN_SM_FAIL;

          //reset the alarm waiting indication since the alarm is received
          miIsWaitingForAlarm = 0;

          liRetVal = BP_AIN_SM_SCENARIO_COMP;
        }

      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Minor state <%d> not expecting alarm <%d>",
          miTransactionId, miMinorState, liAlmState);

        liRetVal = BP_AIN_SM_FAIL;
      }
      break;
    }
    case BP_AIN_SM_SUBTYPE_SNDAAC:
    {
      /*
       * Since state is ASPAC, it is waiting for ASPAC_ACK or
       * ASPAC_FAIL
       */
      if (liAlmState == BP_AIN_SM_ALMOP_ASPAC_ACK)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d>: ASPAC_ACK received", miTransactionId);
        
        //retrieve the PSP Id from the ASPM context
        int liPspId = static_cast<int> (apAlmMsg->t.almOper.almOp.aspmInfo.pspId);
        
        INGwSmRequestContext *lpReqContext = 
                                 mpReqHdlr->getRequestContext();
        
        if (liPspId != lpReqContext->pspId)
        { 
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : PSP Id <%d> is not valid for request PSP <%d>",
            miTransactionId, liPspId, lpReqContext->pspId);
          
          liRetVal = BP_AIN_SM_SEND_NEXT;
        }
        else
        {
          //move the PSP to the new state
          mpRepository->setPspState (liPspId, BP_AIN_SM_PSP_ST_ASPAC);

          //reset the alarm waiting indication since the alarm is received
          miIsWaitingForAlarm = 0;
        }

      }
      else if (liAlmState == BP_AIN_SM_ALMOP_ASPAC_FAIL)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d>: ASPAC_FAIL received", miTransactionId);
        
        //retrieve the PSP Id from the ASPM context
        int liPspId = static_cast<int> (apAlmMsg->t.almOper.almOp.pspInfo.pspId);
        
        INGwSmRequestContext *lpReqContext = 
                                 mpReqHdlr->getRequestContext();
        
        if (liPspId != lpReqContext->pspId)
        { 
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : PSP Id <%d> is not valid for request PSP <%d>",
            miTransactionId, liPspId, lpReqContext->pspId);
          
          liRetVal = BP_AIN_SM_SEND_NEXT;
        }
        else
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : ASP AC failed for PSP <%d>",
            miTransactionId, lpReqContext->pspId);

          //set the return value for Blocking Context to be used by Distributor
          meBlockCtx.returnValue = BP_AIN_SM_FAIL;

          //reset the alarm waiting indication since the alarm is received
          miIsWaitingForAlarm = 0;

          liRetVal = BP_AIN_SM_SCENARIO_COMP;
        }

      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Minor state <%d> not expecting alarm <%d>",
          miTransactionId, miMinorState, liAlmState);

        liRetVal = BP_AIN_SM_FAIL;
      }
      break;
    }
    case BP_AIN_SM_SUBTYPE_SNDAIA:
    {
      /*
       * Since state is ASPIA, it is waiting for ASPIA_ACK or
       * ASPIA_FAIL
       */
      if (liAlmState == BP_AIN_SM_ALMOP_ASPIA_ACK)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d>: ASPIA_ACK received", miTransactionId);
        
        //retrieve the PSP Id from the ASPM context
        int liPspId = static_cast<int> (apAlmMsg->t.almOper.almOp.aspmInfo.pspId);
        
        INGwSmRequestContext *lpReqContext = 
                                 mpReqHdlr->getRequestContext();
        
        if (liPspId != lpReqContext->pspId)
        { 
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : PSP Id <%d> is not valid for request PSP <%d>",
            miTransactionId, liPspId, lpReqContext->pspId);
          
          liRetVal = BP_AIN_SM_SEND_NEXT;
        }
        else
        {
          //move the PSP to the new state
          mpRepository->setPspState (liPspId, BP_AIN_SM_PSP_ST_ASPUP);

          //reset the alarm waiting indication since the alarm is received
          miIsWaitingForAlarm = 0;
        }

      }
      else if (liAlmState == BP_AIN_SM_ALMOP_ASPIA_FAIL)
      {
        logger.logMsg (TRACE_FLAG, 0,
          "TID <%d>: ASPIA_ACK received", miTransactionId);
        
        //retrieve the PSP Id from the ASPM context
        int liPspId = static_cast<int> (apAlmMsg->t.almOper.almOp.pspInfo.pspId);
        
        INGwSmRequestContext *lpReqContext = 
                                 mpReqHdlr->getRequestContext();
        
        if (liPspId != lpReqContext->pspId)
        { 
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : PSP Id <%d> is not valid for request PSP <%d>",
            miTransactionId, liPspId, lpReqContext->pspId);
          
          liRetVal = BP_AIN_SM_SEND_NEXT;
        }
        else
        {
          logger.logMsg (ERROR_FLAG, 0,
            "TID <%d> : ASP IA failed for PSP <%d>",
            miTransactionId, lpReqContext->pspId);

          //set the return value for Blocking Context to be used by Distributor
          meBlockCtx.returnValue = BP_AIN_SM_FAIL;

          //reset the alarm waiting indication since the alarm is received
          miIsWaitingForAlarm = 0;

          liRetVal = BP_AIN_SM_SCENARIO_COMP;
        }

      }
      else
      {
        logger.logMsg (ERROR_FLAG, 0,
          "TID <%d>: Minor state <%d> not expecting alarm <%d>",
          miTransactionId, miMinorState, liAlmState);

        liRetVal = BP_AIN_SM_FAIL;
      }
      break;
    }
    default :
    {
      logger.logMsg (ERROR_FLAG, 0,
        "TID <%d> : MinorState <%d> doesn't need to wait for any alarms");
      break;
    }
  }



  /*
   * In case of return val of success, proceed with state model
   */
  if (liRetVal == BP_AIN_SM_OK)
  {
    //send the next request to the stack
    liRetVal = sendRequest ();

    while (liRetVal == BP_AIN_SM_SEND_NEXT)
    {
      liRetVal = sendRequest (liRetVal);
    }
  }

  /*
   * In case scenario completed, remove the request from the map
   */
  else if (liRetVal == BP_AIN_SM_SCENARIO_COMP)
  {
    logger.logMsg (WARNING_FLAG, 0,
      "TID <%d> : Scenario completed via failure", miTransactionId);
  }     


  logger.logMsg (TRACE_FLAG, 0,
    "TID <%d> Leaving INGwSmRequest::handleAlarm", miTransactionId);
  return liRetVal;
}

/******************************************************************************
*
*     Fun:   dump()
*
*     Desc:  dump the information about this request
*     
*     Notes: None
*     
*     File:  INGwSmRequest.C
*
*******************************************************************************/
int
INGwSmRequest::dump ()
{ 
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmRequest::dump : TID <%d>, Scenario <%d>, Major <%X>, "
    "Minor <%d>, AlarmWait <%d>, Statevector Size <%d>",
    miTransactionId, miScenario, miMajorState, miMinorState, miIsWaitingForAlarm,
    meStateVector.size ());

#ifdef _BP_AIN_SM_DMP_

  INGwSmIntVector::iterator iter;

  for (iter = meStateVector.begin(); iter != meStateVector.end(); iter++)
  {
    int liState = *iter;

    logger.logMsg (TRACE_FLAG, 0,
      "REQUEST DUMP : TID <%d>, State <%X> in the StateVector",
      miTransactionId, liState);
  }

#endif /* _BP_AIN_SM_DMP_ */

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmRequest::dump");
  return BP_AIN_SM_OK;
}
