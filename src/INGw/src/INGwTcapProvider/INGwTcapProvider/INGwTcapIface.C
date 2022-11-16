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
//     File:     INGwTcapIface.C
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwTcapProvider");

#include <INGwTcapProvider/INGwTcapProvider.h>
#include <INGwTcapProvider/INGwTcapIface.h>
#include <INGwTcapProvider/INGwTcapUtil.h>

#include <INGwInfraManager/INGwIfrMgrManager.h>
#include <INGwFtPacket/INGwFtPktLoadDistMsg.h>
#include <INGwTcapProvider/INGwTcapMsgLogger.h>
#include <INGwTcapProvider/INGwTcapFtHandler.h>
//#include <INGwTcapProvider/INGwTcapMsgReceiver.h>

#include <strstream>
#include <sstream>
#include <list>
#include <string>

using namespace std;

INGwTcapIface::INGwTcapIface():INGwIwfBaseIface(INGwIwfBaseIface::TCAP)
{
	m_ldDstMgr  = NULL;
}

INGwTcapIface::~INGwTcapIface()
{
	TcapMessage::Terminate();
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
INGwTcapIface::initialize(INGwLdDstMgr *p_ldDstMgr)
{
	LogINGwTrace(false, 0,"IN INGwTcapIface::initialize()");
	int retVal = G_SUCCESS;

	m_ldDstMgr = p_ldDstMgr;

	m_selfId   = INGwIfrPrParamRepository::getInstance().getSelfId();
	m_peerId   = INGwIfrPrParamRepository::getInstance().getPeerId();

	TcapMessage::Initialize();

	LogINGwTrace(false, 0,"OUT INGwTcapIface::initialize()");
	return retVal;
}

void
INGwTcapIface::setProtoType(bool p_protoType) 
{
	m_protoType = p_protoType;
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
//remove this function
void g_printAddress(SccpAddr pAddr){
	LogINGwTrace(false, 0,"IN INGwTcapIface::g_printAddress");
  printf("\ng_printAddress:-\nPC [Ox%08X], SSN [0x%02X]",pAddr.pc,pAddr.ssn);
  printf("\npcInd [Ox%02X],ssnInd[0x%02X]",pAddr.pcInd,pAddr.ssnInd);
  printf("\nRouting indicator [0x%02x]",pAddr.rtgInd);
  printf("\nProtocol sw [0x%02X]",pAddr.sw);
	LogINGwTrace(false, 0,"IN INGwTcapIface::g_printAddress");
}

void
INGwTcapIface::getOpcSsnList(g_TransitObj &p_transitObj)
{
	LogINGwTrace(false, 0,"IN INGwTcapIface::getOpcSsnList()");


	vector<SccpAddr> lOrigAddrVec;	
	lOrigAddrVec = INGwTcapProvider::getInstance().getOrigAddress();

	list<SccpAddr> lOrigAddrList;	
	for (int i=0; i < lOrigAddrVec.size(); ++i) {
		lOrigAddrList.push_back(lOrigAddrVec[i]);
    g_printAddress(lOrigAddrVec[i]);
    printf("\nNow printing before g_tcapEncodeOpcSsnList from list");
    g_printAddress(lOrigAddrList.front());
    //lOrigAddrVec[i] is printed fine
    //remove
	}

	g_tcapEncodeOpcSsnList(lOrigAddrList, p_transitObj.m_buf, p_transitObj.m_bufLen);

	p_transitObj.m_causeCode = G_SUCCESS;
  
	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
	"[getOpcSsnList] SAS App [%s] requested Opc-Ssn [%s]", 
	p_transitObj.m_sasIp.c_str(), p_transitObj.m_buf);
  
  g_printHexBuff((unsigned char *)p_transitObj.m_buf, p_transitObj.m_bufLen, Logger::TRACE); 
	LogINGwTrace(false, 0,"OUT INGwTcapIface::getOpcSsnList()");
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
INGwTcapIface::processSasInfo(g_TransitObj &p_transitObj)
{
	LogINGwTrace(false, 0,"IN INGwTcapIface::processSasInfo()");
	int retVal = G_SUCCESS;

	SccpAddr 					 lAddr;
  U8                 lState;
	g_tcapSsnState     lSsnState = TCAP_UNKNOWN;
	AppInstId          lAppId;

  //Yogesh INCTBD
	logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	"[processSasInfo] protoType [%s]", (m_protoType == true)?"ITU":"ANSI");

	auto_ptr<TcapMessage> msg(new TcapMessage(p_transitObj.m_sasIp, m_protoType));
	logger.logINGwMsg(false, VERBOSE_FLAG, 0,
						"[processSasInfo] +rem+ before calling decodeNStateReqEvent()"
            "SAS ip [%s]",p_transitObj.m_sasIp);
                    
  g_printHexBuff((unsigned char *)p_transitObj.m_buf, p_transitObj.m_bufLen, 
							  	Logger::TRACE); 

  p_transitObj.m_bufLen = 1;
  if(false == msg->decodeNStateReqEvent(p_transitObj.m_buf,
                              p_transitObj.m_bufLen, lAddr, lState))
	{
		logger.logINGwMsg(false, ERROR_FLAG, 0,
		"[processSasInfo] TcapMessage returned decodeNstateReqEvent as false, "
		"for Info from SASIp[%s]", p_transitObj.m_sasIp.c_str());

		p_transitObj.m_causeCode = G_INVALID_OPC_SSN;

		LogINGwTrace(false, 0,"OUT INGwTcapIface::processSasInfo()");
		return G_FAILURE;
	}
  
	// Check if OPC-SSN is from configured List
	retVal = INGwTcapProvider::getInstance().verifyOpcSsnInConfigList
																									(lAddr.pc, lAddr.ssn);

	if (false == retVal) {
		p_transitObj.m_causeCode = G_INVALID_OPC_SSN;

		logger.logINGwMsg( false, ERROR_FLAG, 0,
		"[processSasInfo] Opc SSN mismatch pc[%d] ssn[%d] protoType[%s]",
		lAddr.pc, lAddr.ssn, (m_protoType == true)?"ITU":"ANSI");

		LogINGwTrace(false, 0,"OUT INGwTcapIface::processSasInfo()" 
			" InvalidOPC/SSN");
		return (retVal = G_FAILURE);
	}

	int numOfApp = 0;

	if (SS_UIS == lState) { 

    // USER_IN_SERVICE received from SAS
		if (G_SUCCESS != m_ldDstMgr->registerSASApp(lAddr.pc, lAddr.ssn, 
																								p_transitObj.m_sasIp)) {
			logger.logINGwMsg(false, ERROR_FLAG, 0, 
			"[processSasInfo] registerSASApp May be already registered for "
			"pc-ssn[%d-%d] ip[%s]", lAddr.pc, lAddr.ssn, 
			p_transitObj.m_sasIp.c_str());

			p_transitObj.m_causeCode = G_ALREADY_REG;
		}

		// send registration info to peer
		INGwFtPktLoadDistMsg ldDistPkt;

		ldDistPkt.initialize(REGISTER_SAS_APP, m_selfId, m_peerId);
		ldDistPkt.appendSasDestAddrToList(p_transitObj.m_sasIp,lAddr.pc, lAddr.ssn);

		INGwIfrMgrManager::getInstance().sendMsgToINGW(&ldDistPkt);

		char lBuf[100];
		sprintf(lBuf, 
								"Registration Info Rxed from SAS Ip[%s] PC[%d] SSN[%d]\n",
								p_transitObj.m_sasIp.c_str(), lAddr.pc, lAddr.ssn);	

		INGwTcapMsgLogger::getInstance().dumpMsg(lBuf, -1);
		lSsnState = INGwTcapProvider::getInstance().registerWithStack
																(lAddr.pc, lAddr.ssn, TCAP_REGISTER);

		if (TCAP_REGISTERED == lSsnState) {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,"SSN <%d> already registered",lAddr.ssn);
			p_transitObj.m_causeCode = G_ALREADY_REG;
    
			delete [] p_transitObj.m_buf;
      p_transitObj.m_buf = 0;
			p_transitObj.m_bufLen =0;

			// fetch PC and SSN state
			PcSsnStatusData ssnStat;

			if (true == INGwTcapProvider::getInstance().getSCCPState(
												lAddr.pc, lAddr.ssn, ssnStat, TCAP_SSN) ) {
        logger.logINGwMsg(false,ALWAYS_FLAG,0,"SSN StateValid SSN <%d> PC<%d> Ustat <%d>",
          lAddr.ssn, lAddr.pc, ssnStat.ustat);
				vector<SccpAddr> lOpcAddr = INGwTcapProvider::getInstance().
																		getOrigAddress(lAddr.pc, lAddr.ssn);

				g_tcapEncodeMgmtMsg(lOpcAddr[0], ssnStat, p_transitObj.m_buf,
											p_transitObj.m_bufLen, (int)TCAP_SSN);
			}
		}
		else {
			p_transitObj.m_causeCode = G_REG_INPROGRESS;
		}
	}
	else if (SS_UOS== lState) {//USER_OUT_OF_SERVICE  
		m_ldDstMgr->deRegisterSASApp(lAddr.pc, lAddr.ssn, p_transitObj.m_sasIp);

		// Send de-registeration to peer
		INGwFtPktLoadDistMsg ldDistPkt;

		ldDistPkt.initialize(DEREGISTER_SAS_APP, m_selfId, m_peerId);
		ldDistPkt.appendSasDestAddrToList(p_transitObj.m_sasIp, lAddr.pc, lAddr.ssn);

		INGwIfrMgrManager::getInstance().sendMsgToINGW(&ldDistPkt);

		numOfApp = m_ldDstMgr->getNumOfSasAppReg(lAddr.pc, lAddr.ssn); 

		// Dump Info
		char lBuf[256];
		sprintf(lBuf, 
		"De-Registration Info Rxed from SAS Ip[%s] PC[%d] SSN[%d] NumOfSASApp Reg[%d]\n",
								p_transitObj.m_sasIp.c_str(), lAddr.pc, lAddr.ssn, numOfApp);	

    // Mriganka - Trillium stack integration
		INGwTcapMsgLogger::getInstance().dumpMsg(lBuf, -1);

		if (0 == numOfApp) {
		  INGwTcapProvider::getInstance().getSsnInfo(lAddr.ssn, lAppId.suId, lAppId.spId);
		  cleanup(p_transitObj.m_sasIp, lAppId.suId, lAppId.spId);

			lSsnState = INGwTcapProvider::getInstance().deregisterWithStack(lAddr.pc, 
																							lAddr.ssn, TCAP_DE_REGISTER);
		}
		else {
			logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
			"[processSasInfo] Not deregistering on stack, more app are regsitered"
			" for same pc/ssn SAS App [%s] pc[%d] ssn[%d]", 
			p_transitObj.m_sasIp.c_str(), lAddr.pc, lAddr.ssn);
		}


		p_transitObj.m_causeCode = G_DEREG_INPROGRESS;
	}
	else {
		p_transitObj.m_causeCode = G_INVALID_OPC_SSN;
	}

	if (0 == numOfApp) {
		logger.logINGwMsg(false, ALWAYS_FLAG, 0,
		"[processSasInfo] SAS App [%s] calling [%s] for PC[%d] SSN[%d] return[%d]",
		p_transitObj.m_sasIp.c_str(), (SS_UIS == lState)?"REGSITER":
		"DE-REGISTER", lAddr.pc, lAddr.ssn, p_transitObj.m_causeCode); 
	}
  
	LogINGwTrace(false, 0,"OUT INGwTcapIface::processSasInfo()");

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
INGwTcapIface::processOutboundCall(g_TransitObj &p_transitObj)
{
	LogINGwTrace(false, 0,"IN INGwTcapIface::processOutboundCall()");
	int retVal = G_SUCCESS;
	p_transitObj.m_causeCode = G_FAILURE;

	if ((NULL == p_transitObj.m_buf) ||
			(true == p_transitObj.m_sasIp.empty())) {
		LogINGwError(false, 0, "[processOutboundCall] Either buf is NULL or SAS IP not available");
		LogINGwTrace(false, 0,"OUT INGwTcapIface::processOutboundCall()");
		return (retVal = G_FAILURE);
	}


	auto_ptr<TcapMessage> msg(new TcapMessage(p_transitObj.m_sasIp, m_protoType));

#ifdef STUB
	retVal = G_SUCCESS;
#else
  
	   retVal = msg->processReceivedContent((unsigned char *)p_transitObj.m_buf, 
                                          (unsigned int)p_transitObj.m_bufLen,
                                           p_transitObj.m_seqNum);
     
#endif

	if (G_SUCCESS != retVal) {
    if(499 == retVal) {
      p_transitObj.m_causeCode = 499;
      logger.logINGwMsg(false,ERROR_FLAG,0,"+retransmission+ detected "
      "everything ok ");
    }
    else{
		  retVal = G_FAILURE;
    }
		logger.logINGwMsg(false, ERROR_FLAG, 0,
		"[processOutboundCall] Failed processing Msg from SAS IP [%s]\n",
		 p_transitObj.m_sasIp.c_str());
	}
	else {
		p_transitObj.m_causeCode = G_SUCCESS;
	}

	LogINGwTrace(false, 0,"OUT INGwTcapIface::processOutboundCall()");
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
INGwTcapIface::deregisterSas(g_TransitObj &p_transitObj)
{
  LogINGwTrace(false, 0,"IN INGwTcapIface::deregisterSas()");
	int retVal = G_SUCCESS;

	U32 lPc  = 0;
	U8	lSsn = 0;
	bool found = false;
	AppInstId lAppId;

	vector<AppIdInfo> *regApp = INGwTcapProvider::getInstance().getAppConfigInfoList();
  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ +ds+ sizeof regApp %d",regApp->size()); 
	for (int i=0; i < regApp->size(); ++i) {
		lPc  = ((*regApp)[i]).pc;
		lSsn = ((*regApp)[i]).ssn;
     
		if (false == (found = m_ldDstMgr->isSasIpRegistered(lPc, lSsn, p_transitObj.m_sasIp))){
      logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ +ds+ continuing for PC %d SSN %d IP %s",
        lPc,lSsn, p_transitObj.m_sasIp.c_str()); 
			continue;
		}
    
		// Send de-registeration to peer
		INGwFtPktLoadDistMsg ldDistPkt;

		ldDistPkt.initialize(DELETE_SAS_APP, m_selfId, m_peerId);
		ldDistPkt.appendSasDestAddrToList(p_transitObj.m_sasIp, lPc, lSsn);

		INGwIfrMgrManager::getInstance().sendMsgToINGW(&ldDistPkt);

		retVal = m_ldDstMgr->removeSasApp(lPc, lSsn, p_transitObj.m_sasIp);

		// should never hit this.
		if (G_SUCCESS != retVal) {
			LogINGwTrace(false, 0,"OUT INGwTcapIface::deregisterSas(), failed");
			return retVal;
		}

		int numOfApp = m_ldDstMgr->getNumOfSasAppReg(lPc, lSsn); 

		if (0 == numOfApp) {
			INGwTcapProvider::getInstance().deregisterWithStack(lPc, lSsn, 
																								TCAP_DE_REGISTER);
		}
    INGwTcapProvider::getInstance().getSsnInfo(lSsn, lAppId.suId, lAppId.spId);
		cleanup(p_transitObj.m_sasIp, lAppId.suId, lAppId.spId);
	}

	// Not even single entry found
	if (false == found) {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
		"[deregisterSas] Not doing de-registeration as SAS [%s] not registered with LDMgr",
		p_transitObj.m_sasIp.c_str());
	}

	LogINGwTrace(false, 0,"IN INGwTcapIface::deregisterSas()");
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
INGwTcapIface::processSasErrResp(g_TransitObj &p_transitObj)
{
	LogINGwTrace(false, 0,"IN INGwTcapIface::processSasErrResp()");

	auto_ptr<TcapMessage> msg(new TcapMessage(p_transitObj.m_sasIp, m_protoType));

	AppInstId lAppId;
  if(INGwIfrPrParamRepository::getInstance().getPeerStatus()) {
      INGwTcapFtHandler::getInstance().sendTerminateTcapSession(
                                                    p_transitObj.m_stackDlgId);
  }
	

	logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	"[processSasErrResp] Rx error for StackDlgId[%d] suId-spId[%d-%d] sending Abort",
	p_transitObj.m_stackDlgId,  p_transitObj.m_suId,  p_transitObj.m_spId);

        msg->closeDialogue(0x01, p_transitObj.m_stackDlgId, p_transitObj.m_suId, p_transitObj.m_spId); 

	LogINGwTrace(false, 0,"OUT INGwTcapIface::processSasErrResp()");
	return;
}

void
INGwTcapIface::cleanup(const string &p_sasIp, int p_suId, int p_spId)
{
	LogINGwTrace(false, 0,"IN INGwTcapIface::cleanup()");
	AppInstId lAppId;
	lAppId.suId = p_suId;
	lAppId.spId = p_spId;

	logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	"[cleanup] calling cleanup for SasIp[%s] suId[%d] spId[%d]",
	p_sasIp.c_str(), p_suId, p_spId);

	auto_ptr<TcapMessage> msg(new TcapMessage(p_sasIp, m_protoType));
	msg->cleanup(p_sasIp, lAppId);

	LogINGwTrace(false, 0,"OUT INGwTcapIface::cleanup()");
}


int 
INGwTcapIface::processSeqNumAck(U8 p_direction, int p_dialogueId, int p_seqNum,
                                bool p_isLast)
{
  int retVal = G_SUCCESS;
  logger.logINGwMsg(false,TRACE_FLAG,0,"In processSeqNumAck dlgId <%d> "
    "seqNum<%d> p_isLast<%d>" ,p_dialogueId, p_seqNum, p_isLast);
  if(1/*INGwIfrPrParamRepository::getInstance().getPeerStatus()*/) {
    if(!p_isLast) {
      INGwTcapFtHandler::getInstance().sendTcapSessionUpdateInfo(p_direction,
                                       p_dialogueId, p_seqNum); 
    }
    else {
       INGwTcapFtHandler::getInstance().sendTerminateTcapSession(p_dialogueId);
    }
  }
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out processSeqNumAck");
  return retVal;
}
