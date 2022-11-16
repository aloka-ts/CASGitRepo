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
//     File:     INGwTcapIncMsgHandler.C
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwTcapProvider");

#include <Util/imAlarmCodes.h>

#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>
#include <INGwInfraManager/INGwIfrMgrAlarmMgr.h>

#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwTcapProvider/INGwTcapIncMsgHandler.h>
#include <INGwTcapProvider/INGwTcapMsgReceiver.h>
#include <INGwTcapProvider/INGwTcapProvider.h>
#include <INGwTcapProvider/INGwTcapMsgLogger.h>
#include <INGwCommonTypes/INCCommons.h>
#include <INGwCommonTypes/INCTags.h>
#include <signal.h>
#include <strstream>
#include <list>
#define MESSAGE_FT
#include <sys/types.h>
#include <netinet/in.h>
#include <inttypes.h>
#include <sys/socket.h>
#include <arpa/inet.h>
/*
  Name: INCGetReport
  Copyright: 
  Author: 
  Date:
  Description: macro to fill PcSsnStatusData for backward compatibility
               to be revised
  Parameters: steInd object, pointer to PcSsnStatusData object
*/

extern int g_maxNmbOutDlg;

#define INCGetSSNReport(_steInd,_StatusStr){   \
          _StatusStr->ssn = _steInd.aSsn;      \
          _StatusStr->ustat = _steInd.uStat;   \
          _StatusStr->smi   = _steInd.smi;     \
          _StatusStr->dpc   = _steInd.aDpc;    \
        }

#define INCGetPCReport(_PCSteInd,_StatusStr){  \
          _StatusStr->sps   = _PCSteInd.sps;   \
          _StatusStr->dpc   = _PCSteInd.dpc;   \
          }                                   

#define MAX_SAS_SEQ_NUM 2147483647 
using namespace std;
INGwTcapIncMsgHandler* INGwTcapIncMsgHandler::m_selfPtr = NULL;

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
INGwTcapIncMsgHandler&
INGwTcapIncMsgHandler::getInstance()
{
  LogINGwTrace(false, 0, "IN INGwTcapIncMsgHandler::getInstance()");

  if (NULL == m_selfPtr) {
     m_selfPtr = new INGwTcapIncMsgHandler;
  }
 
  LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::getInstance()");
  return *m_selfPtr;
}

/**
* Constructor
*/
INGwTcapIncMsgHandler::INGwTcapIncMsgHandler():m_appContext(NULL)
{
	LogINGwTrace(false, 0, "IN INGwTcapIncMsgHandler::Constructor()");
  mLoDlgId = -1;
    /*memset(m_seqNumMap,0,TCAP_MAX_DLG_ID);
    for(int i=0; i< TCAP_MAX_DLG_ID; i++){
      m_seqNumMap[i][0] = 0;

      m_seqNumMap[i][1] = 0;
    }*/
	LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::Constructor()");
}

/**
* Destrcutor
*/
INGwTcapIncMsgHandler::~INGwTcapIncMsgHandler()
{
	LogINGwTrace(false, 0, "IN INGwTcapIncMsgHandler::Destructor()");
	LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::Destructor()");
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
INGwTcapIncMsgHandler::initialize(INGwIwfBaseIface *p_iwfIface, 
																	INGwLdDstMgr *p_ldDstMgr) 
{
	m_iwfIface 	 = static_cast<INGwIwfIface*>(p_iwfIface);
	m_ldDstMgr 	 = p_ldDstMgr;
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
INGwTcapIncMsgHandler::handleWorkerClbk(INGwIfrMgrWorkUnit* p_workUnit)
{
	LogINGwTrace(false, 0, "IN INGwTcapIncMsgHandler::handleWorkerClbk() +THREAD+ ");
	int retVal = G_SUCCESS;

	if (INGwIfrMgrWorkUnit::INGW_CALL_MSG == p_workUnit->meWorkType) {
		INGwTcapWorkUnitMsg *msg = static_cast<INGwTcapWorkUnitMsg*>
																							(p_workUnit->mpMsg);
    msg->m_threadIdx = p_workUnit->miThreadIdx;
		handleRxMsg(*msg);

		delete msg;
		p_workUnit->mpMsg = NULL;
    delete [] p_workUnit->mpcCallId;
    p_workUnit->mpcCallId = NULL;

	}
	else {
	 logger.logINGwMsg(false, ERROR_FLAG, 0,
		"[handleWorkerClbk] Rxed meWorkType [%d] other than INGW_CALL_MSG",
		p_workUnit->meWorkType);
	}

	LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::handleWorkerClbk()");
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
INGwTcapIncMsgHandler::handleRxMsg(INGwTcapWorkUnitMsg &p_tcapWorkUnit)
{
	LogINGwTrace(false, 0, "In INGwTcapIncMsgHandler::handleRxMsg()");
	int retVal = G_SUCCESS;

	U8 msgType = p_tcapWorkUnit.eventType;

	if (EVTSTUSTEIND == msgType || EVTSTUSTAIND == msgType){
		retVal = handleStatusMsg(p_tcapWorkUnit);
	}
	else if (EVTSTUBNDCFM == msgType ) {		  // Registration Ack Message
		retVal = handleBindCfmMsg(p_tcapWorkUnit);
	}
	else if (EVTSTUDATIND == msgType ||EVTSTUUDATIND == msgType ||
					 EVTSTUCMPIND == msgType ) {		// TCAP Message
			handleRxTcapMsg(p_tcapWorkUnit);
	}
  else if(EVTINBREPLAY == msgType){
   ; //handleInbMsgReplay(p_tcapWorkUnit); 
  }
	else {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
		"[handleRxMsg] Received Unknown Message of type: <%d>", msgType);
	}

	LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::handleRxMsg()");
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
INGwTcapIncMsgHandler::handleRxTcapMsg(INGwTcapWorkUnitMsg &p_tcapWorkUnit)
{
	LogINGwTrace(false, 0, "IN INGwTcapIncMsgHandler::handleRxTcapMsg()");

  logger.logINGwMsg(false,TRACE_FLAG,0,"_handleRxTcapMsg_ <%d> <%s>",
  p_tcapWorkUnit.m_dlgId, 
  p_tcapWorkUnit.m_tcapMsg->msg_type == EVTSTUCMPIND?"EVTSTUCMPIND":
  p_tcapWorkUnit.m_tcapMsg->msg_type == EVTSTUDATIND?"EVTSTUDATIND":"UNKNOWN");

	int retVal = G_SUCCESS;
	int	lDone = false;

	U8 	msgType = p_tcapWorkUnit.m_tcapMsg->msg_type; // replaced with eventType
	int dlgId   = p_tcapWorkUnit.m_dlgId;
  
	TcapMessage *lCallInfo = NULL; 
  int index = (dlgId - mLoDlgId);

  //if(index >= mTotalDlg || index<0) {
  //  logger.logINGwMsg(false,ERROR_FLAG,0,"Serious Error:DialogueId <%d> "
  //                    "out of Range LowDlgId<%d> TotalDlg<%d>",dlgId,
  //                     mLoDlgId, mTotalDlg);

  //  p_tcapWorkUnit.m_tcapMsg->releaseTcapMsg();
  //  delete p_tcapWorkUnit.m_tcapMsg;
  //  p_tcapWorkUnit.m_tcapMsg = NULL;

  //  return G_FAILURE;
  //}

	lCallInfo = m_callMap[index];
  U8 * lencodedBuf = NULL;
  unsigned int lbufLen = 0;
	bool toDelete = false;
  
	INGwTcapMsgLogger::getInstance().dumpMsg(dlgId, msgType,
												p_tcapWorkUnit.m_tcapMsg);

	if (lCallInfo == NULL) {
		  lCallInfo = p_tcapWorkUnit.m_tcapMsg;

    if(lCallInfo->msgTypeR == INC_BEGIN || lCallInfo->msgTypeR == INC_UNI) {
      INGwTcapIncMsgHandler::getInstance().resetSeqNumForDlg(dlgId);

#ifdef SBTM_FLAG               
      lCallInfo->dumpSpAddr(*(lCallInfo->dlgR.srcAddr),
      "before +rem+ I N C O M I N G - S O U R C E");

      lCallInfo->dumpSpAddr(*(lCallInfo->dlgR.dstAddr),
      "before +rem+ I N C O M I N G - D E S T I N A T I O N");

      SccpAddr *lpSrcSpAddr = 0;
      lpSrcSpAddr = lCallInfo->dlgR.srcAddr;
      modifyGtDigits(&(lCallInfo->dlgR.srcAddr), lCallInfo);
#endif /*SBTM_FLAG*/
    } 
	}
	else {
		toDelete = true;
    if(p_tcapWorkUnit.m_tcapMsg->msgTypeR == INC_BEGIN) {
      //clean the hanging call
      cleanHangingCall(&lCallInfo);

      //add new call into the callmap
      lCallInfo = p_tcapWorkUnit.m_tcapMsg;
      toDelete = false;
    }
	}

	
	lDone = lCallInfo->addReceivedContent(p_tcapWorkUnit.m_tcapMsg);

	if (MESSAGE_COMPLETE != lDone) {
    //why over writing the same pointer?
    //should be done only once when (lCallInfo == NULL)
		m_callMap[index] = lCallInfo;
	}
	else {
    vector<TcapComp*>* lcompVector = lCallInfo->getCompVector();
    lencodedBuf = lCallInfo->encode(lCallInfo->dlgR, lcompVector, &lbufLen);
    
    if(INGwTcapMsgLogger::getInstance().getLoggingLevel() >= TCAP_LOGGING_L1 || 
       NULL == lencodedBuf) {

      char lBuf[1024];
      int lBufLen = 0;
      lBufLen += sprintf(lBuf + lBufLen,"%s",
        "\n---Before sendMsgToSas buffer---\n");
      if(lencodedBuf) 
      {
        for(int jj=0;jj<lbufLen;jj++){
          if(0 == jj%16) {
            lBufLen += sprintf(lBuf+lBufLen,"%s","\n\t");
          }
          lBufLen += sprintf(lBuf+lBufLen," %02X",lencodedBuf[jj]);
        }
      }
      else{
        lBufLen += sprintf(lBuf + lBufLen,"%s","\nNULL\n");
        logger.logINGwMsg(false,ERROR_FLAG,0,"Err In Encoding Dlg <%d>",dlgId);
      }
      lBufLen += sprintf(lBuf + lBufLen,"%s",
        "\n---Before sendMsgToSas buffer---\n");

      INGwTcapMsgLogger::getInstance().dumpCodecMsg(lBuf,ENC);
    }

		int lMsgSent = G_FAILURE;
    if(NULL != lencodedBuf) {
      lMsgSent = sendMsgToSas(p_tcapWorkUnit, 
                   lCallInfo, lencodedBuf, lbufLen);
    }
    //while doing abort we are allocating new tc object so delete this
    if(G_SUCCESS != lMsgSent){
      if(INC_P_ABORT != lCallInfo->msgTypeR
         && INC_UNI != lCallInfo->msgTypeR
         && INC_U_ABORT != lCallInfo->msgTypeR
         && INC_END != lCallInfo->msgTypeR) 

        lCallInfo->closeDialogue(0x01, 
                                 lCallInfo->getDialogue(), 
                                 lCallInfo->m_suId,
                                 lCallInfo->m_spId);

        //release the allocated buffer
        delete [] lencodedBuf;
        lencodedBuf = NULL; 

      //not needed now after queuing changes@SipProvider
      //delete [] lencodedBuf;
      //lencodedBuf = NULL; 
    }
    
	  lCallInfo->releaseTcapMsg();
    delete lCallInfo;
    lCallInfo = NULL;
		m_callMap[index] = NULL;
	}
	if (true == toDelete) {
		delete p_tcapWorkUnit.m_tcapMsg;
	}

	LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::handleRxTcapMsg()");
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
int INGwTcapIncMsgHandler::handleStatusMsg(INGwTcapWorkUnitMsg &p_tcapWorkUnit)
{
	LogINGwTrace(false, 0, "IN INGwTcapIncMsgHandler::handleStatusMsg()");

	int retVal = G_SUCCESS;
	PcSsnStatusData  lStatusStr; 
	U32      	 lNoOfInsts;
	char		 	 lBuf[1024];
	int			 	 lBufLen =0;

	memset(lBuf, 0, sizeof(lBuf));
	lBufLen += sprintf(lBuf + lBufLen, "Status Message  : ");

	// No need send to SAS if my role is secondary
	int myRole = INGwTcapProvider::getInstance().myRole();

	INCSS7SteMgmt steMgmt = p_tcapWorkUnit.ss7SteMgmt;
  U8 msgType = steMgmt.evntType;

	switch (msgType) {

		case EVTSSN_STE_IND: {
      INCGetSSNReport((steMgmt.mgmt.steInd),(&lStatusStr)); //this is the macro 
																											//defined for INCGetReport
                              
			if (SS_UOS == steMgmt.mgmt.steInd.uStat) {		    // R-SSN Out-of-service
                       
				lBufLen += sprintf(lBuf + lBufLen, 
				"SSN Out-of-service: DPC[%d] SSN[%d] - SSN[%d]",
				steMgmt.mgmt.steInd.aDpc, steMgmt.mgmt.steInd.aSsn, 
				p_tcapWorkUnit.m_ssn);
			}
			else if (SS_UIS == steMgmt.mgmt.steInd.uStat) {  // R-SSN In-Service

				lBufLen += sprintf(lBuf + lBufLen, 
				"SSN In-service: DPC[%d] SSN[%d] - SSN[%d]",
				steMgmt.mgmt.steInd.aDpc, steMgmt.mgmt.steInd.aSsn, 
				p_tcapWorkUnit.m_ssn);
			}

			if (TCAP_PRIMARY == myRole) {
				sendSccpMgmtInfo(p_tcapWorkUnit, lStatusStr, EVTSSN_STE_IND);
			}
			else {
				logger.logINGwMsg(false, VERBOSE_FLAG, 0,
				"[handleStatusMsg] Not sending to SAS as my role is <%d>", myRole);
			}

			// update state 
			//update state is no
			INGwTcapProvider::getInstance().updateSCCPState(p_tcapWorkUnit.m_pc, 
											p_tcapWorkUnit.m_ssn, lStatusStr, TCAP_SSN);

			INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
			__FILE__, __LINE__, (lStatusStr.ustat == SS_UOS) ?
		    REMOTE_SSN_UOS : REMOTE_SSN_UIS, "TCAP Status Alarm", 0, lBuf);
		}
			break;

		case EVTPC_STE_IND: 
    {
      INCGetPCReport(steMgmt.mgmt.PCSteInd, (&lStatusStr)); //this will update only 
			if (SP_ACC == steMgmt.mgmt.PCSteInd.sps) 
      {				  // DPC Accessible
				lBufLen += sprintf(lBuf + lBufLen, "PC Accessible DPC[%d]", 
				steMgmt.mgmt.PCSteInd.dpc);
			}
			else if (SP_INACC == steMgmt.mgmt.PCSteInd.sps) {	// DPC In-accessible

				lBufLen += sprintf(lBuf + lBufLen, "PC Inaccessible DPC[%d]", 
				steMgmt.mgmt.PCSteInd.dpc);
			}
			else if (SP_CONG == steMgmt.mgmt.PCSteInd.sps) {		// DPC COngestion

				lBufLen += sprintf(lBuf + lBufLen, "PC Congested DPC[%d]", 
				steMgmt.mgmt.PCSteInd.dpc);
			}

			if (TCAP_PRIMARY == myRole) {
				sendSccpMgmtInfo(p_tcapWorkUnit, lStatusStr, EVTPC_STE_IND);
			}
			else {
				logger.logINGwMsg(false, VERBOSE_FLAG, 0,
				"[handleStatusMsg] Not sending to SAS as my role is <%d>", myRole);
			}

			INGwTcapProvider::getInstance().updateSCCPState(
			p_tcapWorkUnit.m_pc, p_tcapWorkUnit.m_ssn, lStatusStr, TCAP_PC);

			if (SP_ACC == steMgmt.mgmt.PCSteInd.sps || 
					SP_INACC == steMgmt.mgmt.PCSteInd.sps) 
			{
    		INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
				__FILE__, __LINE__, (steMgmt.mgmt.PCSteInd.sps == SP_ACC)?
				INGW_DPC_ALLOWED: INGW_DPC_PROHIBITED , "TCAP Status Alarm", 0, lBuf);	
			}

			break;
		}

    case EVTSSN_CORD_IND:
      lBufLen += sprintf(lBuf + lBufLen, 
			"Received SPT Coordinate Indication, affected SSN : [%d] SMI : [%d]",
		 	steMgmt.mgmt.cordInd.aSsn, steMgmt.mgmt.cordInd.smi);
    break;
    
    case EVTSSN_CORD_CFM:
     lBufLen += sprintf(lBuf + lBufLen, 
		 "Received SPT Coordinate Confirm, affected SSN : [%d] SMI : [%d]", 
     steMgmt.mgmt.cordCfm.aSsn, steMgmt.mgmt.cordCfm.smi);          
    break; 

#ifdef STU2
    case EVTSSN_STE_CFM:
     lBufLen += sprintf(lBuf + lBufLen, "Received SPT Coordinate Confirm, "); 
     lBufLen += sprintf(lBuf + lBufLen, 
			(steMgmt.mgmt.steCfm.status ? "Request failed!" : "Request succesful!")); 
    break;                                                       
    
    case EVTSSN_STA_CFM:
     lBufLen += sprintf(lBuf + lBufLen,
		 "Received State Confirm, DPC [%d], SSN [%d], SMI [%d]",
     steMgmt.mgmt.staCfm.dpc, steMgmt.mgmt.staCfm.ssn, steMgmt.mgmt.staCfm.smi);

     //handle ustat, status
     //if(SP_ACC == steMgmt.mgmt.staCfm. ustat){
     // lBufLen += sprintf(lBuf + lBufLen," ,Signalling Point Accessible");
     // }
     //if(SP_INACC == steMgmt.mgmt.staCfm. ustat){
     // lBufLen += sprintf(lBuf + lBufLen," ,Signalling Point Inaccessible");
     // }
     //if(SP_CONG == steMgmt.mgmt.staCfm. ustat){
     // lBufLen += sprintf(lBuf + lBufLen," ,Signalling Point Congested");
     // }
     if(SS_UOS == steMgmt.mgmt.staCfm. ustat){
      lBufLen += sprintf(lBuf + lBufLen," ,Subsystem user out of service");
      handleBindCfmMsg(p_tcapWorkUnit);
      }
     else if(SS_UIS == steMgmt.mgmt.staCfm. ustat){
      lBufLen += sprintf(lBuf + lBufLen," ,Subsystem user in service");
      handleBindCfmMsg(p_tcapWorkUnit);
      }
     INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
			                  __FILE__, __LINE__, (steMgmt.mgmt.staCfm.ustat == SS_UOS) ?
		                    REMOTE_SSN_UOS : REMOTE_SSN_UIS, "TCAP Status Alarm", 0, lBuf);
		  
		 if (SP_ACC == steMgmt.mgmt.staCfm. ustat || 
			SP_INACC == steMgmt.mgmt.staCfm.ustat) 
		{
			INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
			__FILE__, __LINE__, 
			(steMgmt.mgmt.staCfm.ustat == SP_ACC)? INGW_DPC_ALLOWED: INGW_DPC_PROHIBITED ,
       "TCAP Status Alarm", 0, lBuf);	
		}
		  
     break;                      
#endif             
		default: {
			logger.logINGwMsg(false, ALWAYS_FLAG, 0,
			"[handleStatusMsg] Received Unknown Status Message: <%d>", msgType);
		}
	}// End of Switch

	logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[handleStatusMsg] %s", lBuf);

	INGwTcapMsgLogger::getInstance().dumpMsg(lBuf, msgType);
	LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::handleStatusMsg()");
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
INGwTcapIncMsgHandler::handleBindCfmMsg(INGwTcapWorkUnitMsg &p_tcapWorkUnit)
{
	LogINGwTrace(false, 0, "IN INGwTcapIncMsgHandler::handleBindCfmMsg()");
	int retVal = G_SUCCESS;

	S16  ackType = p_tcapWorkUnit.sapStatus;

	char 	lBuf[512]; 
	int		lBufLen=0;

  U8    lcSsn = 0;
  INGwTcapProvider::getInstance().getSsnForSuId(lcSsn, p_tcapWorkUnit.m_suId);

  if (CM_BND_OK == ackType) {

    INGwTcapProvider::getInstance().updateSsnState 
							(p_tcapWorkUnit.m_suId, TCAP_REGISTERED);
  
    lBufLen += sprintf(lBuf, 
		" - Bind Confirmation For SuId[%d] SpId[%d] Ssn[%d] Successful\n",
						p_tcapWorkUnit.m_suId, p_tcapWorkUnit.m_spId, lcSsn);

		PcSsnStatusData lStatusStr;
		lStatusStr.ustat = SS_UIS;
		lStatusStr.dpc   = 0; // not used for IN-Service notification
		lStatusStr.ssn   = 0; // not used for IN-Service notification

	  // No need send to SAS if my role is secondary
    int myRole = INGwTcapProvider::getInstance().myRole();

		if (TCAP_PRIMARY == myRole) {
		  sendSccpMgmtInfo(p_tcapWorkUnit, lStatusStr, EVTSSN_STE_IND);
		}
		else {
			logger.logINGwMsg(false, VERBOSE_FLAG, 0,
			"[handleBindCfmMsg] Not sending to SAS as my role is <%d>", myRole);
		}

		INGwTcapProvider::getInstance().updateSCCPState(
		p_tcapWorkUnit.m_pc, p_tcapWorkUnit.m_ssn, lStatusStr, TCAP_SSN);

  }                     
  else if (CM_BND_NOK == ackType) {
    INGwTcapProvider::getInstance().updateSsnState
						(p_tcapWorkUnit.m_suId, TCAP_NOT_REGISTERED);

    logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
		"Quitting Deliberately. Bind failure"); 

		sprintf(lBuf, " - Bind Confirmation For SuId[%d] SpId[%d] Ssn[%d] :  Failed, Quitting",
    p_tcapWorkUnit.m_suId, p_tcapWorkUnit.m_spId, lcSsn);
  }                      

	logger.logINGwMsg(false, ALWAYS_FLAG, 0, "[handleBindCfmMsg] %s", lBuf);
                         
  // Raise Alarm
  INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
	__FILE__, __LINE__, (ackType == CM_BND_OK)?STACK_REG_SUCCESS:
	STACK_REG_FAILED, "TCAP Status Alarm", 0, lBuf);
                              
	INGwTcapMsgLogger::getInstance().dumpMsg(lBuf, ackType);

  if (ackType == CM_BND_NOK) {
    raise(9);                 
  }                           
            
	LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::handleBindCfmMsg()");
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
INGwTcapIncMsgHandler::sendMsgToSas(INGwTcapWorkUnitMsg &p_tcapWorkUnit,
																		TcapMessage *p_tcapMsg,
                                    unsigned char* p_encBuf, int p_bufLen)
{
	LogINGwTrace(false, 0, "IN INGwTcapIncsgHandler::sendMsgToSas()");
	int retVal = G_SUCCESS;
  bool beginFlag = true;
	g_TransitObj	lTransitObj;
  memset(&lTransitObj,0,sizeof(g_TransitObj));

	lTransitObj.m_bufLen = p_bufLen;
	lTransitObj.m_buf =  p_encBuf;
	lTransitObj.m_stackDlgId         = p_tcapMsg->getDialogue();
  lTransitObj.m_seqNum = 
               ++(m_seqNumMap[lTransitObj.m_stackDlgId - mLoDlgId][0]);

  lTransitObj.m_threadIdx = p_tcapWorkUnit.m_threadIdx;
  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+seqNum <%d> dlgId<%d>",
                   lTransitObj.m_seqNum, lTransitObj.m_stackDlgId);

  lTransitObj.m_suId               = p_tcapMsg->m_suId;
  lTransitObj.m_spId               = p_tcapMsg->m_spId;
	lTransitObj.m_isDialogueComplete = p_tcapMsg->dialogueClosed;
  lTransitObj.m_ssn                = p_tcapWorkUnit.m_ssn;

	logger.logINGwMsg(false, TRACE_FLAG, 0,
	              "[sendMsgToSas] +rem+ stackDialogueID[%d] appId[%d] instId[%d] "
                "isComplete[%d] suId <%d> spId <%d> ssn <%d>",
	              lTransitObj.m_stackDlgId, 
                lTransitObj.m_appId, 
                lTransitObj.m_instId, 
 	              lTransitObj.m_isDialogueComplete, 
                lTransitObj.m_suId, 
                lTransitObj.m_spId,
                lTransitObj.m_ssn);
 
	if (true != p_tcapMsg->getUserAddress(lTransitObj.m_sasIp))
  {
    if(
#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96) 
      !((p_tcapMsg->msgTypeR == STU_QRY_PRM || p_tcapMsg->msgTypeR == STU_QRY_NO_PRM) ||
#else
      !(p_tcapMsg->msgTypeR == INC_BEGIN)||
#endif
      (p_tcapMsg->msgTypeR == INC_UNI)))
    {
      logger.logINGwMsg(false,ERROR_FLAG,0,
                        "sendMsgToSas Session not found for msgType<%d>"
                        " dialogueId<%d>", p_tcapMsg->msgTypeR, 
                        p_tcapMsg->getDialogue());

      return G_FAILURE;
    } 
   
    lTransitObj.m_userDlgId = lTransitObj.m_stackDlgId; 
		lTransitObj.m_sasIp = m_ldDstMgr->getDestSasInfo(p_tcapWorkUnit.m_pc, 
																									 p_tcapWorkUnit.m_ssn);

    logger.logINGwMsg(false, VERBOSE_FLAG, 0,
                      "getUserAddress returning false PC[%d] SSN[%d]",
                       p_tcapWorkUnit.m_pc, 
                       p_tcapWorkUnit.m_ssn);

		if (true == lTransitObj.m_sasIp.empty()) {

			logger.logINGwMsg(false, ERROR_FLAG, 0,
			                "[sendMsgToSas]+VER+ No Sas Destination IP Returned"
                      " for pc[%d] SSN[%d]"
                      " OUT INGwTcapIncMsgHandler::sendMsgToSas()",
			                p_tcapWorkUnit.m_pc, 
                      p_tcapWorkUnit.m_ssn);

			return G_FAILURE;
		}

		p_tcapMsg->assignUserToDialogue(lTransitObj.m_sasIp);


    //replicate UNI messages (accumulate if peer is down) 
    INGwTcapFtHandler::getInstance().sendCreateTcapSessionMsg 
                                                       (lTransitObj,p_tcapMsg);
    beginFlag = false;

    //1 = replicate begin and fail
    if(INGwTcapProvider::getInstance().getFpMask() == 1){
      sleep(1); // to ensure that packets reaches peer.
      logger.logINGwMsg(false,ALWAYS_FLAG,0, 
      "replicate begin and forced to fail");
      exit(1);
    }
	} 
  
  //this variable is just to simulate Ft test scenarios
  static lsiInbndMsgCntr = 0;
  
  //replicate RSN, accumulate if peer is down.
  if(beginFlag)
  {
    INGwTcapFtHandler::getInstance().replicateTcapCallDataToPeer
    (MSG_TCAP_INBOUND, p_encBuf, p_bufLen, lTransitObj.m_seqNum,
     lTransitObj.m_stackDlgId);
  }

  if(INGwTcapProvider::getInstance().getFpMask() == 11) {
    lsiInbndMsgCntr++;
    if(2 == lsiInbndMsgCntr) {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
      "receive 2nd message from Nw replicate and forced " 
      "to fail"); 
      exit(1);
    }
  }

#if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
    lTransitObj.m_billingNo = p_tcapMsg->mBillingId.getBillingNo();

    logger.logINGwMsg(false, VERBOSE_FLAG, 0,"sendMsgToSas m_billingNo %d",
                                             lTransitObj.m_billingNo);
#endif

	retVal = m_iwfIface->processInboundMsg(lTransitObj); 


  if(INGwTcapProvider::getInstance().getFpMask() == 16) {
    ++lsiInbndMsgCntr;
    if(2 == lsiInbndMsgCntr) {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
      "receive 2nd message from Nw replicate, send to SAS and forced" 
      "to fail"); 

      exit(1);
    }
  }

  if(INGwTcapProvider::getInstance().getFpMask() == 2) {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,
    "send begin and fail without cleaning begin at S-INC");
    exit(1);
  }
	
#ifdef INC_DLG_AUDIT
    //INGwTcapProvider::getInstance().auditTcapDlg();
#endif
	
	LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::sendMsgToSas()");
	return retVal;
}

//this method will be used only for replay while takeover
//int
//INGwTcapIncMsgHandler::handleInbMsgReplay(INGwTcapWorkUnitMsg &p_tcapWorkUnit)
//{

int
INGwTcapIncMsgHandler::handleInbMsgReplay(g_TransitObj *p_transObj)
{
	LogINGwTrace(false, 0, "IN INGwTcapIncsgHandler::sendMsgToSas Replay()");
	int retVal = G_SUCCESS;

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"+rem+seqNum <%d> dlgId<%d>",
                   p_transObj->m_seqNum, p_transObj->m_stackDlgId);


	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
	"[sendMsgToSas] +rem+ +cleanDlg+ stackDialogueID[%d] appId[%d] instId[%d] "
  "isComplete[%d] suId <%d> spId <%d> ssn <%d> ip<%s> billingNo <%d>",
	p_transObj->m_stackDlgId, p_transObj->m_appId, p_transObj->m_instId, 
	p_transObj->m_isDialogueComplete, p_transObj->m_suId, p_transObj->m_spId,
  p_transObj->m_ssn, p_transObj->m_sasIp.c_str(), 
  p_transObj->m_billingNo);

  if(INGwTcapMsgLogger::getInstance().getLoggingLevel() >= TCAP_LOGGING_L1
     || NULL == p_transObj->m_buf) {
    char lBuf[1024];
    int lBufLen = 0;
    logger.logINGwMsg(false, ALWAYS_FLAG,0,"+rem+ PRINT!!");
    lBufLen += sprintf(lBuf + lBufLen,"%s",
      "\n---Before sendMsgToSas buffer---\n");
    if(p_transObj->m_buf) 
    {
      for(int jj=0;jj<p_transObj->m_bufLen;jj++){
        if(0 == jj%16) {
          lBufLen += sprintf(lBuf+lBufLen,"%s","\n\t");
        }
        lBufLen += sprintf(lBuf+lBufLen," %02X",p_transObj->m_buf[jj]);
      }
    }
    else{
      lBufLen += sprintf(lBuf + lBufLen,"%s","\nNULL\n");
      logger.logINGwMsg(false,ERROR_FLAG,0,"Err In Encoding Dlg <%d>",
                        p_transObj->m_stackDlgId);
    }
    lBufLen += sprintf(lBuf + lBufLen,"%s",
      "\n---Before sendMsgToSas buffer---\n");

    INGwTcapMsgLogger::getInstance().dumpCodecMsg(lBuf,ENC);
  }

	m_iwfIface->processReplayInboundMsg(*p_transObj);
		
	LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::sendMsgToSas() Replay");
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
INGwTcapIncMsgHandler::sendSccpMgmtInfo(INGwTcapWorkUnitMsg &p_tcapWorkUnit, 
																				PcSsnStatusData &p_status, int p_flag)
{
	LogINGwTrace(false, 0, "IN INGwTcapIncMsgHandler::sendSccpMgmtInfo()");

	if(EVTSSN_STE_IND != p_flag)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwTcapIncMsgHandler::sendSccpMgmtInfo returning as p_flag is not"
		"EVTSSN_STE_IND");
		LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::sendSccpMgmtInfo()");
		return G_SUCCESS;
	}

	int 					retVal = G_SUCCESS;
  SccpAddr  	  lDpc;
	g_TransitObj	lTransitObj;
  TcapMessage 	lTcapMessage;
  U8 						*msg = NULL;

	vector<SccpAddr> lAddr = INGwTcapProvider::getInstance().getOrigAddress
															(0 , 0, p_tcapWorkUnit.m_suId);

	for(int i=0; i < lAddr.size(); ++i)
	{
		bcopy(&lAddr[0], &lDpc, sizeof(SccpAddr));

		lDpc.pc  = p_status.dpc;
		lDpc.ssn = p_status.ssn;

   	unsigned int lMsgLen = 0;

		lTransitObj.m_buf = lTcapMessage.createNstateInd(lAddr[i], 
                                    lDpc,p_status.ustat,&lMsgLen);
														       
   	lTransitObj.m_bufLen = lMsgLen;

		if (NULL == lTransitObj.m_buf) 
		{
			logger.logINGwMsg(false, ERROR_FLAG, 0,
			"[sendSccpMgmtInfo] Error in getting createNpcstateInd or "
			" createNstateInd");
			LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::sendSasAppResp()");
			return retVal;
		}

		vector<string> sasIp = m_ldDstMgr->getAllRegSasApp
																						(lAddr[i].pc, lAddr[i].ssn);

		for (int i=0; i < sasIp.size(); ++i) 
		{
			lTransitObj.m_sasIp = sasIp[i];
			m_iwfIface->sendSasAppResp(lTransitObj);

			logger.logINGwMsg(false, ALWAYS_FLAG, 0,
			"[sendSasAppResp] Sending INFO to SAS IP [%s] on Successful Registration",
			lTransitObj.m_sasIp.c_str());
		}
  
		delete [] lTransitObj.m_buf;
  	lTransitObj.m_buf = 0;
	}

	LogINGwTrace(false, 0, "OUT INGwTcapIncMsgHandler::sendSccpMgmtInfo()");
	return retVal;
}

int
INGwTcapIncMsgHandler::sendSteReqToStack(INGwTcapWorkUnitMsg &p_tcapWorkUnit,U8 pPcOrSsn) {

	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                    "In INGwTcapIncMsgHandler::sendSteReqToStack, Pc <%d> Ssn <%d> "
                    "RequestType <%d>, appId<%d>", p_tcapWorkUnit.m_pc,
                     p_tcapWorkUnit.m_ssn , pPcOrSsn,
                     p_tcapWorkUnit.m_appInstId.appId);
  int retVal = 0;
  CmSS7SteMgmt lss7SteMgmt;
  memset(&lss7SteMgmt, 0, sizeof(CmSS7SteMgmt));

  lss7SteMgmt.evntType = EVTSSN_STA_REQ; 
  #ifdef STU2
	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                   "[sendSteReqToStack]Creating state Request");
  lss7SteMgmt.mgmt.staReq.dpc = p_tcapWorkUnit.m_pc;
  lss7SteMgmt.mgmt.staReq.ssn = p_tcapWorkUnit.m_ssn;
  lss7SteMgmt.mgmt.staReq.status = pPcOrSsn;
  #endif /* STU2 */
  INGwTcapWorkUnitMsg lWorkUnit;
  memset(&lWorkUnit, 0, sizeof(INGwTcapWorkUnitMsg));

  lWorkUnit.eventType = EVTSTUSTEREQ;
  lWorkUnit.m_appInstId.appId = p_tcapWorkUnit.m_appInstId.appId;
  memcpy(&(lWorkUnit.ss7SteMgmt), &lss7SteMgmt,sizeof(CmSS7SteMgmt));
  retVal = INGwTcapProvider::getInstance().getAinSilTxRef().sendTcapReq(&lWorkUnit);

	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                    "Out INGwTcapIncMsgHandler::sendSteReqToStack");
  return retVal;
}


TcapMessage** 
INGwTcapIncMsgHandler::getCallMap() {
	logger.logINGwMsg(false, TRACE_FLAG, 0,"In getCallMap");
	logger.logINGwMsg(false, TRACE_FLAG, 0,"Out getCallMap");
  return m_callMap;
}

//int**
//INGwTcapIncMsgHandler::getSeqNumMap(){
//  return m_seqNumMap;
//}

void 
INGwTcapIncMsgHandler::resetSeqNumForDlg(U32 pDlgId, int pVal) {
	logger.logINGwMsg(false, TRACE_FLAG, 0,"+rem+ In resetSeqNumForDlg DlgId <%d> SeqNum"
    "<%d>",pDlgId,pVal);
  m_seqNumMap[pDlgId - mLoDlgId][0] = pVal;
  m_seqNumMap[pDlgId - mLoDlgId][1] = pVal;
	logger.logINGwMsg(false, TRACE_FLAG, 0,"+rem+ Out resetSeqNumForDlg updated"); 
}


void 
INGwTcapIncMsgHandler::updateLastSeqNumForDlg(U32 pDlgId, int pVal) {
	logger.logINGwMsg(false, TRACE_FLAG, 0,"+rem+ In resetSeqNumForDlg DlgId <%d> SeqNum"
    "<%d>",pDlgId,pVal);
  m_seqNumMap[pDlgId - mLoDlgId][1] = pVal;
	logger.logINGwMsg(false, TRACE_FLAG, 0,"+rem+ Out resetSeqNumForDlg updated"); 
}

int
INGwTcapIncMsgHandler::getLastSequenceNumForDlg(U32 pDlgId){
  return m_seqNumMap[pDlgId - mLoDlgId][1];
}


void 
INGwTcapIncMsgHandler::setInBoundSeqNum(U32 p_dlgId, int p_seqNum){
  m_seqNumMap[p_dlgId - mLoDlgId][0] = p_seqNum;
}

void 
INGwTcapIncMsgHandler::setOutBoundSeqNum(U32 p_dlgId, int p_seqNum){
  m_seqNumMap[p_dlgId - mLoDlgId][1] = p_seqNum;
}


int 
INGwTcapIncMsgHandler::getLastInBoundSeqNum(U32 p_dlgId) {
  return (m_seqNumMap[p_dlgId - mLoDlgId][0]);
}

int
INGwTcapIncMsgHandler::getLastOutBoundSeqNum(U32 pDlgId){
  return m_seqNumMap[pDlgId - mLoDlgId][1];
}

void
INGwTcapIncMsgHandler::initCallMap(int p_loDlg, int p_hiDlg)
{
  mTotalDlg = (p_hiDlg - p_loDlg) + 1;
  mLoDlgId = p_loDlg;

  char *lpcSasNmbDlg = getenv("MAX_NMB_OUT_DLG");
  int noOfDlgFromSas = 0;
  if(NULL != lpcSasNmbDlg)
  {
    noOfDlgFromSas = atoi(lpcSasNmbDlg);
  }
  else {
    noOfDlgFromSas = 200000;
  }
  g_maxNmbOutDlg = noOfDlgFromSas;

	logger.logINGwMsg(false, TRACE_FLAG, 0,"initCallMap lodlgId<%d>, low<%d>,"
          " high<%d> noOfDlgFromSas<%d>",mLoDlgId,p_loDlg,p_hiDlg,g_maxNmbOutDlg); 

  //*m_callMap = new (TcapMessage *)[totalDlg];
  m_callMap = new (nothrow) TcapMessage* [mTotalDlg];
  if(NULL == m_callMap)
  {
    logger.logINGwMsg(false,ERROR_FLAG,0,
           "initCallMap dyn mem alloc failure");
    printf("initCallMap, dyn mem alloc failure");
    fflush(stdout);
    //perform shutdown if possible
    exit(1);    
  }
  memset(m_callMap, '\0', (sizeof(TcapMessage *)*mTotalDlg));
 
  //*m_seqNumMap = new  (int *)[totalDlg];
  m_seqNumMap = new  (nothrow) int* [mTotalDlg + g_maxNmbOutDlg];

  if(NULL == m_seqNumMap)
  {
    logger.logINGwMsg(false,ERROR_FLAG,0,
           "initCallMap dyn mem alloc failure");
    printf("initCallMap, dyn mem alloc failure");
    fflush(stdout);
    //perform shutdown if possible
    exit(1);    
  }
 
  int j=0; 
  for(int i = 0; i<(mTotalDlg + g_maxNmbOutDlg); i++){
    m_seqNumMap[i] = new (nothrow) int[2];

    if(NULL == m_seqNumMap[i])
    {
      logger.logINGwMsg(false,ERROR_FLAG,0,
             "initCallMap dyn mem alloc failure");
      printf("initCallMap, dyn mem alloc failure");
      fflush(stdout);
      //perform shutdown if possible
      exit(1);    
    }
    memset(m_seqNumMap[i], '\0', (sizeof(int)*2));
    j = i;
  }

 TcapMessage::initDialogueStateHashMap(p_loDlg,p_hiDlg);
 INGwTcapFtHandler::getInstance().initActiveDialogueMap(); 
 
#ifdef INGW_LOOPBACK_SAS
  m_iwfIface->initLoopbackDlgInfo(p_loDlg, p_hiDlg);
#endif

}


void
INGwTcapIncMsgHandler::getDldIdRange(int *pLowDlg, int *pTotalDlg) {
logger.logINGwMsg(false, TRACE_FLAG, 0,"In getDldIdRange");
  pLowDlg = &mLoDlgId;
  pTotalDlg = &mTotalDlg;
logger.logINGwMsg(false, TRACE_FLAG, 0,"Out getDldIdRange");
}

void
INGwTcapIncMsgHandler::getLowDlgId(int *pLoDlgId){
logger.logINGwMsg(false, TRACE_FLAG, 0,"In getLowDlgId");
  *pLoDlgId = mLoDlgId;
logger.logINGwMsg(false, TRACE_FLAG, 0,"In getLowDlgId <%d>", mLoDlgId);
}

void
INGwTcapIncMsgHandler::getTotalDlg(int *pTotalDlg){
logger.logINGwMsg(false, TRACE_FLAG, 0,"In getTotalDlg");
  *pTotalDlg = mTotalDlg;
logger.logINGwMsg(false, TRACE_FLAG, 0,"In getTotalDlg <%d>", mTotalDlg);
}

//this will clean TcapMessage Object
void 
INGwTcapIncMsgHandler::cleanHangingCall(TcapMessage **pTcapMsg) {
  int lDlgId = (*pTcapMsg)->getDialogue();
  logger.logINGwMsg(false,TRACE_FLAG,0,"In cleanHangingCall"
                    " Removing Dialogue[%d] from the Call Map",lDlgId);

  char lBuf[512];
  int lBufLen  = 0;

  (*pTcapMsg)->providerAbort(lDlgId, INC_ABORT_INC_TRANS);

  lBufLen = sprintf(lBuf,"+VER+ Dropping Dialogue src Addr <%d>");
  string newlogInfo1(lBuf, lBufLen);
  if((*pTcapMsg)->dlgR.srcAddr)
  (*pTcapMsg)->dumpSpAddr(*((*pTcapMsg)->dlgR.srcAddr),newlogInfo1); 

  lBufLen = sprintf(lBuf,"+VER+ Dropping Dialogue dst Addr <%d>");
  string newlogInfo2(lBuf, lBufLen);
  if((*pTcapMsg)->dlgR.dstAddr)
  (*pTcapMsg)->dumpSpAddr(*((*pTcapMsg)->dlgR.dstAddr), newlogInfo2);

  (*pTcapMsg)->releaseTcapMsg();

  delete  (*pTcapMsg);
  (*pTcapMsg) = NULL;
  logger.logINGwMsg(false,TRACE_FLAG,0, "Out cleanHangingCall");
}

void 
INGwTcapIncMsgHandler::modifyGtDigits(SccpAddr **appSpAddr, 
                                      TcapMessage *pTcMsg) {
  logger.logINGwMsg(false,TRACE_FLAG,0, "In modifyGtDigits");
        SccpAddr *lpSrcSpAddr = *appSpAddr;
        
        logger.logINGwMsg(false, TRACE_FLAG,0, "Modifying src tType to 0xe9");
        lpSrcSpAddr->gt.addr.strg[2] = (pTcMsg->dlgR.dpc) & 0x00ff;
        lpSrcSpAddr->gt.addr.strg[3] = (pTcMsg->dlgR.dpc >> 8) & 0x00ff;
        lpSrcSpAddr->gt.addr.length = 4;
        switch(lpSrcSpAddr->gt.format) {
          case GTFRMT_2:
            if(lpSrcSpAddr->gt.gt.f2.tType == 0xe8)
            lpSrcSpAddr->gt.gt.f2.tType =  0xe9;
          break;

          case GTFRMT_3:
            if(lpSrcSpAddr->gt.gt.f3.tType == 0xe8)
            lpSrcSpAddr->gt.gt.f3.tType = 0xe9;
          break;
          
          case GTFRMT_4:
            if(lpSrcSpAddr->gt.gt.f4.tType == 0xe8)
            lpSrcSpAddr->gt.gt.f4.tType = 0xe9;         
          break;

          default:
          logger.logINGwMsg(false,ERROR_FLAG,0,
            "[handleRxTcapMsg]Translation Type Not present");
       }

  logger.logINGwMsg(false,TRACE_FLAG,0, "Out modifyGtDigits");
}



/*after modifyGtDigits
      if(false && lpSrcSpAddr->status && lpSrcSpAddr->addrInfo.addrInfoType == 0xe8) {
        //no need to check for AddrInfo 
        logger.logINGwMsg(false,TRACE_FLAG,0,"Modifying Src SpAddr");
        //create address signal
        U8 lGtDigits[4];
        memcpy(lGtDigits,lpSrcSpAddr->addrInfo.careerIdenCode,2);
        lGtDigits[2] = (lCallInfo->dlgR.dpc >> 8) & 0x00ff;
        lGtDigits[3] = (lCallInfo->dlgR.dpc) & 0x00ff;
        
        if(lpSrcSpAddr->rtgInd == 0x00) 
        {
          memcpy(lpSrcSpAddr->gt.addr.strg, lGtDigits,4);
          lpSrcSpAddr->gt.addr.length = 4;
          switch(lpSrcSpAddr->gt.format) {
            case GTFRMT_2:
              if(lpSrcSpAddr->gt.gt.f2.tType == 0xe8)
              lpSrcSpAddr->gt.gt.f2.tType =  0xe9;
            break;

            case GTFRMT_3:
              if(lpSrcSpAddr->gt.gt.f3.tType == 0xe8)
              lpSrcSpAddr->gt.gt.f3.tType = 0xe9;
            break;
            
            case GTFRMT_4:
              if(lpSrcSpAddr->gt.gt.f4.tType == 0xe8)
              lpSrcSpAddr->gt.gt.f4.tType = 0xe9;         
            break;

            default:
            logger.logINGwMsg(false,ERROR_FLAG,0,
              "[handleRxTcapMsg]Translation Type Not present");
          }
        }        
      } else {
        //temporary fix
        logger.logINGwMsg(false, TRACE_FLAG,0, "Modifying src tType to 0xe9");
        lpSrcSpAddr->gt.addr.strg[2] = (lCallInfo->dlgR.dpc >> 8) & 0x00ff;
        lpSrcSpAddr->gt.addr.strg[3] = (lCallInfo->dlgR.dpc) & 0x00ff;
        lpSrcSpAddr->gt.addr.length = 4;
        switch(lpSrcSpAddr->gt.format) {
          case GTFRMT_2:
            if(lpSrcSpAddr->gt.gt.f2.tType == 0xe8)
            lpSrcSpAddr->gt.gt.f2.tType =  0xe9;
          break;

          case GTFRMT_3:
            if(lpSrcSpAddr->gt.gt.f3.tType == 0xe8)
            lpSrcSpAddr->gt.gt.f3.tType = 0xe9;
          break;
          
          case GTFRMT_4:
            if(lpSrcSpAddr->gt.gt.f4.tType == 0xe8)
            lpSrcSpAddr->gt.gt.f4.tType = 0xe9;         
          break;

          default:
          logger.logINGwMsg(false,ERROR_FLAG,0,
            "[handleRxTcapMsg]Translation Type Not present");
        }
      } 
*/
