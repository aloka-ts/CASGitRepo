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
//     File:     INGwTcapMsgReceiver.C
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwTcapProvider");

#include <INGwTcapProvider/INGwTcapMsgReceiver.h>
#include <INGwTcapProvider/INGwTcapProvider.h>
#include <INGwTcapProvider/INGwTcapInclude.h>
#include <INGwTcapProvider/INGwTcapWorkUnitMsg.h>

#include <Util/imAlarmCodes.h>
#include <INGwInfraManager/INGwIfrMgrAlarmMgr.h>

#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>
#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>

#include <unistd.h>
#include <strings.h>

// Stub for testing 
#ifdef STUB
	/** For testing Purpose */
	#include <queue>

	using namespace std;

	typedef struct t_StubInfo 
	{
		UTcapMsg 		   *msg;
    AppInstId  appInstId;
	} stubInfo;
                 
	queue<stubInfo*>	g_msgQ;
	int  g_pc  =0;
	int  g_ssn =0;

	int
	StubMsgRcv(UTcapMsg *msg, AppInstId  &appInstId);
#endif

INGwTcapMsgReceiver* INGwTcapMsgReceiver::m_selfPtr = NULL;

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
launchRecvMsgFromStack(void *args)
{           
  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN recvMsgFromStack, pthread");
            
  INGwTcapMsgReceiver *rx = static_cast<INGwTcapMsgReceiver *>(args);
  rx->recvMsgFromStack();
            
  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT recvMsgFromStack, pthread");
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
INGwTcapMsgReceiver&
INGwTcapMsgReceiver::getInstance()
{
  LogINGwTrace(false, 0, "IN INGwTcapMsgReceiver::getInstance()");
  
  if (NULL == m_selfPtr) {
     m_selfPtr = new INGwTcapMsgReceiver;
  }
  
  LogINGwTrace(false, 0, "OUT INGwTcapMsgReceiver::getInstance()");
  return *m_selfPtr;
}

/**
* Constructor
*/
INGwTcapMsgReceiver::INGwTcapMsgReceiver(): m_isRunning(false), 
							m_isRegistered(false), m_readerFlag(false), m_ReRegisterState(ReRegisterState_COMPLETE)
{
	pthread_rwlock_init(&m_RWLock, 0);
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
INGwTcapMsgReceiver::initialize(INGwIfrMgrWorkerClbkIntf& p_callbackPtr)
{
  LogINGwTrace(false, 0, "IN INGwTcapMsgReceiver::initialize()");
	int retVal = G_SUCCESS;

	INGwIfrMgrThreadMgr::getInstance().initialize();
	INGwIfrMgrThreadMgr::getInstance().startWorkerThread();

	m_workerCallBackIf = &(p_callbackPtr);

	if (NULL == m_workerCallBackIf) {
		retVal = G_FAILURE;

		logger.logINGwMsg(false, ERROR_FLAG, 0,
		"INGwTcapMsgReceiver::initialize, workerCallbackIf is NULL");
	}



  LogINGwTrace(false, 0, "OUT INGwTcapMsgReceiver::initialize()");
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
INGwTcapMsgReceiver::start()
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "IN INGwTcapMsgReceiver::start()");

  int retValue = G_SUCCESS;

  if (true == m_isRunning) {
    logger.logINGwMsg(false, TRACE_FLAG, 0, 
		"OUT INGwTcapMsgReceiver::start(), ALreadyRunning");
    return retValue;
  }

	m_appContext = INGwTcapProvider::getInstance().getAppContext();
   
  pthread_t receiverThread;
  if (0 != pthread_create(&receiverThread, 0, launchRecvMsgFromStack, 
																	&INGwTcapMsgReceiver::getInstance())) {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
             "INGwTcapMsgReceiver::start() Error in pthread creation.");
    retValue = G_FAILURE;
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT INGwTcapMsgReceiver::start()");
  return retValue; 
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
INGwTcapMsgReceiver::stop()
{
  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN INGwTcapMsgReceiver::stop()");

	m_isRunning = false;

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT INGwTcapMsgReceiver::stop()");
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
void
INGwTcapMsgReceiver::startReading()
{
  LogINGwTrace(false, 0, "IN INGwTcapMsgReceiver::startReading()");

  m_readerFlag = true;

  LogINGwTrace(false, 0, "OUT INGwTcapMsgReceiver::startReading()");
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
INGwTcapMsgReceiver::markRegistered()
{
	m_isRegistered = true;

	//PR - 51361
	if(getReRegisterState() == ReRegisterState_INPROGRESS)
	{
		setReRegisterState(ReRegisterState_COMPLETE);
	  // Send alarm
		INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
																							 __FILE__, __LINE__, INGW_STACK_CONN_REESTABLISHED, 
																							 "INGw Stack Conn Reestablished", 0, 
																							 "Application re-registered with stack");
	}
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
INGwTcapMsgReceiver::recvMsgFromStack()
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, 
					"IN INGwTcapMsgReceiver::recvMsgFromStack()");

  /*
  S32        tcapErrno;
  UTcapMsg   *msgRxed = 0;
  bool       deleteMsg = true;
	bool			 isMgmtMsg = false;
                 
	// Wait till not successful registered with stack
  while (false == m_readerFlag) {
    sleep(1);    
  }              

  m_isRunning = true;
  while (m_isRunning) {
                 
		deleteMsg = true;
		isMgmtMsg = false;
    msgRxed = new UTcapMsg;

    AppInstId appInstId;
                 
#ifdef STUB
		tcapErrno = StubMsgRcv(msgRxed, appInstId);
#else
    tcapErrno = TuRcvMsg(m_appContext, msgRxed, &appInstId);
#endif

    // Check for possible return values
    // M7H_ROK, M7H_RFAIL, M7H_INST_NOT_INIT, M7H_NOT_REGISTERED,
    // M7H_BAD_PARAMETER, M7H_NO_DATA, M7H_NOTCONN
                 
    if (tcapErrno != M7H_ROK) {
                 
      logger.logINGwMsg(false, ERROR_FLAG, 0,
			"[TuRcvMsg] Error Received. Error <%s><%d> in TuRcvMsg", 
			g_tcapPrintError(tcapErrno), tcapErrno);
                 
      if (tcapErrno == M7H_NOTCONN || tcapErrno == M7H_NOT_REGISTERED) {

        //PR - 51361 
		    INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
																							 __FILE__, __LINE__, INGW_STACK_CONN_FAILURE, 
																							 "INGw Stack Conn Failure", 0, 
																							 "INGW not registered with stack");
        if (false == m_isRegistered) {
          sleep(4);
				}        
                 
				logger.logINGwMsg(false, ERROR_FLAG, 0,
 				"[TuRcvMsg]Re-Registering With TCAP Server after sleep[%d] seconds.",
                 (m_isRegistered == false) ? 4 : 0);

        //PR - 51361 
        //clean all stack messgaes
        INGwTcapProvider::getInstance().handleStackConnFailure();

        setReRegisterState(ReRegisterState_REREGISTER);         

				INGwTcapProvider::getInstance().registerAllAppWithStack();

        m_isRegistered = false;

        //PR - 51361 
				// We will wait until registration request is sent to stack
				// otherwise we will keep geeting same failure.
				while (true)
				{
					if(getReRegisterState() == ReRegisterState_INPROGRESS )
					{
						break;
					}
				  logger.logINGwMsg(false, ERROR_FLAG, 0,
 				         "[TuRcvMsg]Re-Registering not initiated with stack so sleeping");
					sleep(3);
        }
      }          

      else if (tcapErrno == M7H_INST_NOT_INIT) {
        logger.logINGwMsg(false, ERROR_FLAG, 0,
				 "[TuRcvMsg] Received M7H_INST_NOT_INIT : Instance not initialized.");
      }          
                 
			delete msgRxed;
      continue; 
    }            
               
    // Check Type of Message rxed.
    // M7H_USR_MSG      - not required
    // M7H_APPSTA_IND   - Status Indication
    // M7H_DLGTKOR_CFM  - Not used
    // M7H_DLG_IND      - Dialogue Indication
    // M7H_COMP_IND     - Component Request Indication
    // M7H_CORDCFM_IND  - Coordination Request Confirmation
    // M7H_CORD_IND     - Cordination Request Indication
    // M7H_STE_IND      - SSN Status indication
    // M7H_PCSTE_IND    - Point Code Status Indication
    // M7H_SYS_IND      - System Indication
    // M7H_CFG_CFM      - Registration/re-registration req confirm
                 
    logger.logINGwMsg(false, VERBOSE_FLAG, 0,
		"[TuRcvMsg] Receive TCAP User Message Type : <%s><%d>",
		g_tcapPrintMsgType(msgRxed->type), msgRxed->type);

                 
    // Handle Status messages.
    if (msgRxed->type == M7H_STE_IND    || msgRxed->type == M7H_PCSTE_IND || 
        msgRxed->type == M7H_APPSTA_IND || msgRxed->type == M7H_CFG_CFM ) {

				isMgmtMsg = true;
    }            
     
    if ((M7H_DLG_IND == msgRxed->type) || (M7H_COMP_IND == msgRxed->type) ||
			  (true == isMgmtMsg)) {

      deleteMsg = false;
      INGwTcapWorkUnitMsg *msg = new INGwTcapWorkUnitMsg;
      msg->m_rxMsg = msgRxed;

			if (false == isMgmtMsg) {

				msg->m_tcapMsg = TcapMessage::processReceivedContent(*msgRxed, 
																						 m_appContext, appInstId);

      	if (NULL == msg->m_tcapMsg) {
					// increment counter

					logger.logINGwMsg(false, ERROR_FLAG, 0,
					"[recvMsgFromStack] NULL returned from processReceivedContent");

        	delete msgRxed;
        	delete msg;
        	continue;
				}

				msg->m_dlgId = msg->m_tcapMsg->getDialogue();
      }           

			INGwTcapProvider::getInstance().getOpcSsnForAppId(appInstId.appId,
																									msg->m_ssn, msg->m_pc);


      bcopy(&appInstId, &msg->m_appInstId, sizeof(AppInstId));
                 
      // Create WorkUnit
      INGwIfrMgrWorkUnit *workUnit  = new INGwIfrMgrWorkUnit;
      workUnit->meWorkType    = INGwIfrMgrWorkUnit::INGW_CALL_MSG;
      workUnit->mpContextData = reinterpret_cast<void *>(0);
      workUnit->mulMsgSize    = sizeof(INGwTcapWorkUnitMsg);
      workUnit->mpWorkerClbk  = m_workerCallBackIf;
      workUnit->mpMsg         = static_cast<void *>(msg);
                 
			if (false == isMgmtMsg) {
      	workUnit->mpcCallId     = new char[20];
      	sprintf(workUnit->mpcCallId, "%d", msg->m_dlgId);
				msg->m_callId 						= workUnit->mpcCallId;
      	workUnit->getHash();

      	INGwIfrMgrThreadMgr::getInstance().postMsg(workUnit);
			}
			else {
      	workUnit->mpcCallId     = NULL;

      	INGwIfrMgrThreadMgr::getInstance().postMsgForHK(workUnit);
			}

      logger.logINGwMsg(false, VERBOSE_FLAG, 0,
			 "[TuRcvMsg] Submitted WorkUnit for [%s]", 
			 (isMgmtMsg == true)?"Management":"TCAP Message");
    } 
                 
    if (deleteMsg == true) {
      delete msgRxed;
      msgRxed = 0;
    }            
  } // End of while.
                 
  */
	logger.logINGwMsg(false, TRACE_FLAG, 0, 
						"OUT INGwTcapMsgReceiver::recvMsgFromStack()");
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
void
INGwTcapMsgReceiver::setReRegisterState(e_ReRegisterState p_ReRegisterState)
{
  //Allowed state change 

  //1) ReRegisterState_NONE ===> ReRegisterState_INPROGRESS
  //2) ReRegisterState_REREGISTER ===> ReRegisterState_INPROGRESS
  //3) ReRegisterState_INPROGRESS ===> ReRegisterState_COMPLETE
  //4) ReRegisterState_INPROGRESS ===> ReRegisterState_REREGISTER
  //5) ReRegisterState_COMPLETE ===> ReRegisterState_REREGISTER
  
	logger.logINGwMsg(false, TRACE_FLAG, 0, 
					"IN INGwTcapMsgReceiver::setReRegisterState()");

  pthread_rwlock_wrlock(&m_RWLock);

  logger.logINGwMsg(false, ALWAYS_FLAG, 0,
			"[setReRegisterState] New State Received <%s>. Current state <%s>",
			 registerStateStr[p_ReRegisterState], registerStateStr[m_ReRegisterState]);

  bool lIsUpdate = true;

  switch (m_ReRegisterState)
  {
    case ReRegisterState_NONE:
    case ReRegisterState_REREGISTER:

       if(p_ReRegisterState != ReRegisterState_INPROGRESS)
       {
         logger.logINGwMsg(false, ERROR_FLAG, 0,
			       "[setReRegisterState] Wrong state changes recvd, not updating the state");
         lIsUpdate = false;
       }
			 break;

    case ReRegisterState_INPROGRESS:

       if(p_ReRegisterState == ReRegisterState_NONE )
       {
         logger.logINGwMsg(false, ERROR_FLAG, 0,
			       "[setReRegisterState] Wrong state changes recvd, not updating the state");
         lIsUpdate = false;
       }
			 break;

    case ReRegisterState_COMPLETE:

       if(p_ReRegisterState != ReRegisterState_REREGISTER)
       {
         logger.logINGwMsg(false, ERROR_FLAG, 0,
			       "[setReRegisterState] Wrong state changes recvd, not updating the state");
         lIsUpdate = false;
       }
			 break;

    deafult : 

      logger.logINGwMsg(false, ERROR_FLAG, 0,
			       "[setReRegisterState] Wrong current state , not updating the state");
      lIsUpdate = false;
  };

	if (lIsUpdate)
	{
		m_ReRegisterState = p_ReRegisterState;
	}

  pthread_rwlock_unlock(&m_RWLock);

	logger.logINGwMsg(false, TRACE_FLAG, 0, 
						"OUT INGwTcapMsgReceiver::setReRegisterState()");
}


/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/

e_ReRegisterState
INGwTcapMsgReceiver::getReRegisterState()
{
  
	logger.logINGwMsg(false, TRACE_FLAG, 0, 
					"IN INGwTcapMsgReceiver::getReRegisterState()");

  pthread_rwlock_rdlock(&m_RWLock);
  e_ReRegisterState retVal = m_ReRegisterState;
  pthread_rwlock_unlock(&m_RWLock);

	logger.logINGwMsg(false, TRACE_FLAG, 0, 
						"OUT INGwTcapMsgReceiver::getReRegisterState()");
  return retVal;        

}

#ifdef STUB
int
StubMsgRcv(UTcapMsg *msg, AppInstId  &appInstId)
{
	LogINGwTrace(false, 0,  "IN INGwTcapMsgReceiver::StubMsgRcv()");

  while (1) {
	
		if (true == g_msgQ.empty()) {
			sleep (TCAP_REG_SLEEP);
			continue;
		}

		stubInfo *lMsg = g_msgQ.front();
		g_msgQ.pop();

		if (NULL != lMsg) {
			msg = lMsg->msg;
			memcpy(&appInstId, &lMsg->appInstId,
				sizeof(AppInstId));

			delete lMsg;
			break;
		}
	}

	LogINGwTrace(false, 0, "OUT INGwTcapMsgReceiver::StubMsgRcv()");

	return M7H_ROK;
}
#endif
