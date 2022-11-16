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
//     File:     INGwTcapFtHandler.C
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwTcapProvider");
#include <INGwInfraUtil/INGwIfrUtlGlbFunc.h>
#define MESSAGE_FT
#define INGW_CLEAR_BR_MODE      true
#define INGW_CLEAR_R_MODE       false 
#include <INGwTcapProvider/INGwTcapFtHandler.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>
#include <INGwInfraManager/INGwIfrMgrManager.h>
#include <INGwTcapProvider/INGwTcapMsgLogger.h>
#include <INGwTcapProvider/INGwTcapProvider.h>
#include <INGwTcapMessage/TcapMessage.hpp>
#ifdef MESSAGE_FT
#include <INGwTcapProvider/INGwTcapIncMsgHandler.h>
#endif
#include<INGwFtPacket/INGwFtPktTcapCallSeqAck.h>
#include<INGwFtPacket/INGwFtPktSynchUpMsg.h>
#include<INGwFtPacket/INGwFtPktCreateTcapSession.h>
#include<INGwFtPacket/INGwFtPktUpdateTcapSession.h>
#include<INGwFtPacket/INGwFtPktSasHBFailure.h>
#include <INGwInfraUtil/INGwIfrUtlBitArray.h>
#include "INGwStackManager/INGwSmBlkConfig.h"


#define BYTES_PER_DLG  512 
#define TAG_INB_MAP    255
#define TAG_OUTB_MAP   254
#define TAG_TUAI_LIST  253
#define MEM_CHUNK_SIZE 1024
int INGwTcapFtHandler::mPeerFaultCnt = 0;

map<short,bool> sapStatusMap;
pthread_mutex_t mutLockMap;
pthread_mutex_t mutLock;

#ifdef MSG_FT_TEST
pthread_cond_t  gUnblockBR;
pthread_mutex_t gCvLockBR;

static initCondVar gCondVar(gUnblockBR);
static initMutex   gCvMutex(gCvLockBR);

#endif

static initMutex mapLock(mutLockMap);
static initMutex mutexLock(mutLock);

bool isReplayInProgress;
U8* g_activeReplayCallMap;
int g_ActiveReplayCallCnt;
int loDlgId;
int ** gDlgInvBuff;
int g_maxNmbOutDlg;

extern pthread_cond_t gUnblock;
pthread_rwlock_t  gRWLock;
pthread_rwlock_t  gMapRWLock;
pthread_rwlock_t  gMsgStoreFlgLock;

static initRWLock g_flgLock(gRWLock,"gRWLock");
static initRWLock g_mapLock(gMapRWLock,"gMapRWLock");
static initRWLock g_msgStoreFlgLock(gMsgStoreFlgLock,"gMsgStoreFlgLock");

bool gbPeerConnected;

int  tcapLoDlgId;
int  tcapHiDlgId;

bool 
gGetReplayFlg(){
  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ In getReplayFlg");
  bool retVal = true;

  pthread_rwlock_rdlock(&gRWLock);
    retVal = isReplayInProgress;
  pthread_rwlock_unlock(&gRWLock);

  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ Out getReplayFlg %d",retVal);
  return retVal;
} 

void
gSetReplayFlg(bool pVal){
  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ In gSetReplayFlg <%d>",pVal);
  bool retVal = true;

  pthread_rwlock_wrlock(&gRWLock);
    isReplayInProgress = pVal;
  pthread_rwlock_unlock(&gRWLock);

  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ Out gSetReplayFlg");
} 

bool 
gIsSubSeqMsg(int pDlgId){
  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ In isSubSeqMsg DlgId<%d> LoDlgId<%d>",pDlgId,loDlgId);

  pthread_rwlock_rdlock(&gMapRWLock);
  bool retVal = false;
    if(NULL != g_activeReplayCallMap) {  
      retVal =  ((1 == g_activeReplayCallMap[pDlgId - loDlgId])?true:false);
    }
  pthread_rwlock_unlock(&gMapRWLock);

  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ Out isSubSeqMsg <%d>",retVal);
  return retVal;
}

//will return number of recovered active calls 
int gUpdateReplayCallCnt(int pDlgId) {
  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem In gUpdateReplayCallCnt <%d>",pDlgId);
  bool isPresent = false;

  //taking gMapRWLock lock on g_ActiveReplayCallCnt also 
  //in order to make the following an atomic operation
  pthread_rwlock_wrlock(&gMapRWLock);
  if(!gGetReplayFlg()){
    logger.logINGwMsg(false,TRACE_FLAG,0,
           "gUpdateReplayCallCnt(): unlocking gMapRWLock");

    pthread_rwlock_unlock(&gMapRWLock);
    return 0;
  }
  int retVal = g_ActiveReplayCallCnt;
  if(NULL != g_activeReplayCallMap) {  
    isPresent =  (1 == g_activeReplayCallMap[pDlgId - loDlgId])?true:false;
  }
  if(isPresent) { 
    g_activeReplayCallMap[pDlgId - loDlgId] = 0; 
    retVal = ((--g_ActiveReplayCallCnt) == -1)?0:g_ActiveReplayCallCnt;

   if(retVal <= 0) gSetReplayFlg(false);
  }
  pthread_rwlock_unlock(&gMapRWLock);

  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ Out gUpdateReplayCallCnt dlgId "
                                       "<%d> callCnt <%d> isPresent <%d>", 
                                       pDlgId, retVal, isPresent);
  return retVal;
}

bool gIsIsMsgStoreEmpty = false;
bool gIsMsgStoreEmpty() {
  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ In gIsMsgStoreEmpty");
  bool retVal = false;
  pthread_rwlock_rdlock(&gMsgStoreFlgLock);
    retVal  = gIsIsMsgStoreEmpty;
  pthread_rwlock_unlock(&gMsgStoreFlgLock);
  logger.logINGwMsg(false,TRACE_FLAG,0,"+rem+ In gIsMsgStoreEmpty <%d>",retVal);
  return retVal;
}

//static initSessionMutex(mutLock);
#define LOCKSESSION()                                      \
  struct autoMUT { autoMUT() {                             \
    pthread_mutex_lock(&mutLock);                          \
  }                                                        \
    ~autoMUT()                                             \
  {                                                        \
    pthread_mutex_unlock(&mutLock);                        \
  } }at; 

INGwTcapFtHandler* INGwTcapFtHandler::m_selfPtr = NULL;

#define NOT_SUPPORTED_BY_STACK
const static size_t sizeOfWorkUnit = sizeof(INGwTcapWorkUnitMsg);

void
INGwTcapFtHandler::tcapUserInformationForReplication(
					            int stackDialogue, 
                      int userDialogue, 
					            const SccpAddr &srcaddr, 
                      const SccpAddr &destaddr, 
					            const string &ipaddr, 
                      AppInstId & appid, 
                      #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
                      StAnsiDlgEv pStAnsiDlgEv,
                      int piBillingNo
                      #else
                      INcStr objAcn
                      #endif
                      )
{

  LogINGwTrace(false, 0, "IN INGwTcapFtHandler::TcapUserInformationForSerialization()");

  LOCKSESSION();

  if(INGwTcapFtHandler::getInstance().getMsgFtFlag()) {
    LogINGwTrace(false, 0, "Out TcapUserInformationForSerialization SKIPPING");
    return;
  }
  

	int selfId   = INGwIfrPrParamRepository::getInstance().getSelfId();
	int peerId   = INGwIfrPrParamRepository::getInstance().getPeerId();

	logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	"[TcapUserInformationForSerialization] To Serialize stkDlg[%d] "
	"userDlg[%d] ipadd[%s] destaddr[pc-ssn: %d-%d] srcaddr[pc-ssn: %d-%d]"
   " suid: %d spId %d ",
	stackDialogue, userDialogue, ipaddr.c_str(),destaddr.pc, destaddr.ssn,
  srcaddr.pc, srcaddr.ssn, appid.suId, appid.spId);

	INGwFtPktTcapMsg *lptcapMsg = new (nothrow) INGwFtPktTcapMsg;
	lptcapMsg->initialize(stackDialogue,
                        userDialogue,
                        srcaddr,
										    destaddr,
                        ipaddr,
                        appid, 
                        #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
                        pStAnsiDlgEv,
                        piBillingNo,
                        #else
                        objAcn, 
                        #endif
                        selfId,
                        peerId);
 

  map<int, INGwTcapSession*>::iterator it = 
                                         m_tcapSessionMap.find(stackDialogue);  
  INGwTcapSession *lpTcSession; 
  if(m_tcapSessionMap.end() != it) {
    lpTcSession = it->second;
  } else{
    logger.logINGwMsg(false,VERBOSE_FLAG,0, "Session Not found,"
                      " Creating new sessn <%d>",stackDialogue);

    lpTcSession = new INGwTcapSession;

    m_tcapSessionMap[stackDialogue] = lpTcSession;
  }

  if(NULL == lpTcSession->getInitialInfo()) {
    lpTcSession->setInitialInfo(lptcapMsg);
  } else {  
          logger.logINGwMsg(false,VERBOSE_FLAG,0,
          "TcapUserInformationForSerialization setInitialInfo returns non null");
  }
  
  LogINGwTrace(false, 0, "OUT INGwTcapFtHandler::TcapUserInformationForSerialization()");
}


/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
INGwTcapFtHandler&
INGwTcapFtHandler::getInstance()
{
  LogINGwTrace(false, 0, "IN INGwTcapFtHandler::getInstance()");

  if (NULL == m_selfPtr) {
     m_selfPtr = new INGwTcapFtHandler;
  }
   
  LogINGwTrace(false, 0, "OUT INGwTcapFtHandler::getInstance()");
  return *m_selfPtr;
}

/**
* Constructor
*/
INGwTcapFtHandler::INGwTcapFtHandler():m_ldDstMgr(NULL)
{
	LogINGwTrace(false, 0, "IN INGwTcapFtHandler::Constructor()");
    mpTcapIncMsgHandler =  &(INGwTcapIncMsgHandler::getInstance());
    int liDisableFtFlg = -1;
    m_selfId = INGwIfrPrParamRepository::getInstance().getSelfId();
    m_peerId = INGwIfrPrParamRepository::getInstance().getPeerId();
    if(0 == m_peerId) {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
      "INGwTcapFtHandler::INGwTcapFtHandler() m_peerId<>",m_peerId);
      liDisableFtFlg = 1;
    }
    else {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
      "INGwTcapFtHandler::INGwTcapFtHandler() m_peerId <%d>",m_peerId);
    }

    setMsgFtFlag(liDisableFtFlg);
#ifdef MSG_FT_TEST
   mbBlkTermination = false; 
   mbCondWaitBR     = false;
   mbBlkInbCleanup  = false;
   mbBlkOutbCleanup = false;
#endif
    gIsIsMsgStoreEmpty = false;
	LogINGwTrace(false, 0, "OUT INGwTcapFtHandler::Constructor()");
  gbPeerConnected = false;
}

/**
* Destrcutor
*/
INGwTcapFtHandler::~INGwTcapFtHandler()
{
	LogINGwTrace(false, 0, "IN INGwTcapFtHandler::Destructor()");
	LogINGwTrace(false, 0, "OUT INGwTcapFtHandler::Destructor()");
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
INGwTcapFtHandler::initialize(INGwLdDstMgr *p_ldDstMgr) 
{
	LogINGwTrace(false, 0, "IN INGwTcapFtHandler::initialize()");
	m_ldDstMgr 	 = p_ldDstMgr;

	LogINGwTrace(false, 0, "OUT INGwTcapFtHandler::initialize()");
}

/**
* Description :
*
* @param <> -
*
* @return <> -
*
*/
/*make function invocation based on
 array of pointer to functions instead of switch case

 handleUpdateTcapSession(INGwFtPktTcapCallSeqAck)
 handleUpdateTcapSession(INGwFtPktUpdateTcapSession)
 handleCreateTcapSessionMsg(INGwFtPktCreateTcapSession) 
 handleTerminateTcapSession()
*/

int
INGwTcapFtHandler::handleWorkerClbk(INGwIfrMgrWorkUnit* p_workUnit)
{
	LogINGwTrace(false, 0, "IN INGwTcapFtHandler::handleWorkerClbk()");
	int retVal = G_SUCCESS;
	if (INGwIfrMgrWorkUnit::MSG_TCAP_CLEAN_MSG_DATA == p_workUnit->meWorkType) 
  {
    INGwFtPktTcapCallSeqAck *lackMsg =  static_cast<INGwFtPktTcapCallSeqAck*>
                                       (p_workUnit->mpMsg);
    handleUpdateTcapSession(*lackMsg);
    delete lackMsg;
  }

	else if (INGwIfrMgrWorkUnit::MSG_MODIFY_TCAP_SESSION == p_workUnit->meWorkType) {
    INGwFtPktUpdateTcapSession *lupdTcSession=
      static_cast<INGwFtPktUpdateTcapSession*>(p_workUnit->mpMsg);
    handleUpdateTcapSession(*lupdTcSession);
    delete lupdTcSession;
  }

	else if (INGwIfrMgrWorkUnit::MSG_NEW_TCAP_SESSION == p_workUnit->meWorkType) {
    INGwFtPktCreateTcapSession *lpCreateTcSession = 
      static_cast<INGwFtPktCreateTcapSession*>(p_workUnit->mpMsg);
    handleCreateTcapSessionMsg(*lpCreateTcSession);
    delete lpCreateTcSession;
  }

  else if(INGwIfrMgrWorkUnit::MSG_TERMINATE_TCAP_SESSION ==
                                                     p_workUnit->meWorkType){
    INGwFtPktTermTcapSession *lpTermTcapSession= 
      static_cast<INGwFtPktTermTcapSession*>(p_workUnit->mpMsg);
    handleTerminateTcapSession(*lpTermTcapSession);
    delete lpTermTcapSession;
  }

	else if (INGwIfrMgrWorkUnit::LOAD_DIST_MSG  == p_workUnit->meWorkType) {
		INGwFtPktLoadDistMsg *msg = static_cast<INGwFtPktLoadDistMsg*>
																							(p_workUnit->mpMsg);
		handleLoadDistMsg(*msg);
		delete msg;
	}

	else if (INGwIfrMgrWorkUnit::MSG_TCAP_MSG_INFO == p_workUnit->meWorkType) {

		INGwFtPktTcapMsg *tcapMsg = static_cast<INGwFtPktTcapMsg*>
																					(p_workUnit->mpMsg);

		handleTcapMsgFromPeer(*tcapMsg);
		delete tcapMsg;
	}

  else if(INGwIfrMgrWorkUnit::SAS_HB_FAILURE == p_workUnit->meWorkType){
    INGwFtPktSasHBFailure *lHBFailureMsg = static_cast<INGwFtPktSasHBFailure*>
                                                      (p_workUnit->mpMsg);
    handleSasHBFailure(*lHBFailureMsg);
    delete lHBFailureMsg;
  }
  else if(INGwIfrMgrWorkUnit::DUMP_TCAP_SESSION_MAP == p_workUnit->meWorkType)  {
    dumpTcapSessionMap("<<Telnet-Interface>>");
  }
  else if(INGwIfrMgrWorkUnit::CLEAR_TCAP_SESSION_MAP == p_workUnit->meWorkType)
  {
    clearReplayMessageStore(); 
  }
  else if(INGwIfrMgrWorkUnit::MSG_PEER_FAILURE == p_workUnit->meWorkType)
  {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"calling handlePeerINCFailure");
    handlePeerINCFailure();
  }
  else if(INGwIfrMgrWorkUnit::MSG_PEER_UP == p_workUnit->meWorkType){
    //gbPeerConnected = true;

    handlePeerConnectedMsg(); 
  }
  else if(INGwIfrMgrWorkUnit::MSG_FT_SYNCHUP == p_workUnit->meWorkType) {
    INGwFtPktSynchUpMsg *lpSynchUpMsg= 
                   static_cast<INGwFtPktSynchUpMsg*> (p_workUnit->mpMsg);

    handleSynchUpMsgFromPeer(*lpSynchUpMsg);
    delete lpSynchUpMsg;
  }
	else {

	  logger.logINGwMsg(false, ERROR_FLAG, 0,
	 	"[handleWorkerClbk] Rxed Unknown FT message [%d]",
		p_workUnit->meWorkType);
	}

	p_workUnit->mpMsg =0;

	LogINGwTrace(false, 0, "OUT INGwTcapFtHandler::handleWorkerClbk()");
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
INGwTcapFtHandler::handleLoadDistMsg(INGwFtPktLoadDistMsg &p_ldMsg)
{
	LogINGwTrace(false, 0, "IN INGwTcapFtHandler::handleLoadDistMsg()");
	int retVal = G_SUCCESS;

	logger.logINGwMsg(false, VERBOSE_FLAG, 0,
	"[handleLoadDistMsg] Msg Rxed from peer [%s]", p_ldMsg.toLog().c_str());

	short lLoadType = p_ldMsg.getLoadDistMsgType();

	int sizeOfLdSdtInfo = p_ldMsg.getLdDistMsgCnt();

	if (0 == sizeOfLdSdtInfo) {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
		"[handleLoadDistMsg] Size of rxed Load Dist Info is 0");
		LogINGwTrace(false, 0, "OUT INGwTcapFtHandler::handleLoadDistMsg()");
		return retVal;
	}

	char lBuf[1024];
	int lBufLen = 0;

	int flag = -1;
	int lNumOfSSNReg = sizeOfLdSdtInfo;

	for (int i=0; i < lNumOfSSNReg; i++) {

		flag = -1;

		U32 lpc       = 0; 
		U8  lssn      = 0;
		int sizeOfSas = 0;

		p_ldMsg.getPcSsnAtIndex(lpc, lssn, sizeOfSas, i);

		//printf("+REM+ %s..Size of SAS[%d]\n", __FILE__, sizeOfSas);

		for (int j=0; j < sizeOfSas; ++j ) {

			string lSasIp = p_ldMsg.getIpFromInfoAtIndex(i, j);

			printf("+REM+ %s..lSasIp[%s] lLoadType)[%d]\n", 
			                        "handleLoadDistMsg()", lSasIp.c_str(), lLoadType);

			if (REGISTER_SAS_APP == lLoadType) {

				m_ldDstMgr->registerSASApp( lpc, lssn, lSasIp);


				flag = TCAP_REGISTER;
			}
			else if (DEREGISTER_SAS_APP == lLoadType) {

				m_ldDstMgr->deRegisterSASApp(lpc, lssn, lSasIp);

        logger.logINGwMsg(false,TRACE_FLAG,0,
                                   "handleLoadDistMsg +rem+ <%d>",lBufLen);


				// Not to call de-register if more than 1 app are registered.
				int numOfApp = m_ldDstMgr->getNumOfSasAppReg(lpc, lssn);
				if (0 == numOfApp) {
					flag = TCAP_DE_REGISTER;
				}
			}
			else if (DELETE_SAS_APP == lLoadType) {

				m_ldDstMgr->removeSasApp(lpc, lssn, lSasIp);

				// Not to call de-register if more than 1 app are registered.
				int numOfApp = m_ldDstMgr->getNumOfSasAppReg(lpc, lssn);
				if (0 == numOfApp) {
					flag = TCAP_DE_REGISTER;
				}
			}
		}

			printf("+REM+ %s..lLoadType[%d] flag[%d]\n", 
			"handleLoadDistMsg()", lLoadType, flag);

		if (-1 != flag) {
			if (TCAP_DE_REGISTER == flag) {
				INGwTcapProvider::getInstance().deregisterWithStack(lpc, lssn, flag);
			}
			else {
				INGwTcapProvider::getInstance().registerWithStack(lpc, lssn, flag);
			}
		}
	}

	LogINGwTrace(false, 0, "OUT INGwTcapFtHandler::handleLoadDistMsg()");
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
//this function is never used
//USING DUMMY BILLING ID TO COMPILE
int
INGwTcapFtHandler::handleTcapMsgFromPeer(INGwFtPktTcapMsg &p_tcapMsg)
{
LogINGwTrace(false, 0, "IN INGwTcapFtHandler::handleTcapMsgFromPeer()");
int retVal = G_SUCCESS;

logger.logINGwMsg(false, VERBOSE_FLAG, 0,
"[handleTcapMsgFromPeer] Msg Rxed from peer [%s]", p_tcapMsg.toLog().c_str());
  SccpAddr a;
 //SccpAddr &testThis =a;

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
  StAnsiDlgEv l_ansiDlgEv;
  int l_billingId;
  memset(&l_ansiDlgEv, 0, sizeof(StAnsiDlgEv));
#endif

  TcapMessage::storeUserInformationFT(p_tcapMsg.m_stackDialogue,
                                      p_tcapMsg.m_userDialogue,
                                      p_tcapMsg.m_srcAddr,
                                      p_tcapMsg.m_dstAddr,
                                      p_tcapMsg.m_ipAddr,
                                      p_tcapMsg.m_appId,
                                      #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
                                      l_ansiDlgEv,
                                      l_billingId
                                      #else
                                      p_tcapMsg.m_objAcn 
                                      #endif
                                      );

LogINGwTrace(false, 0, "OUT INGwTcapFtHandler::handleTcapMsgFromPeer()");
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
INGwTcapFtHandler::replicateInfoToPeer()
{
	LogINGwTrace(false, 0, "IN INGwTcapFtHandler::replicateInfoToPeer()");
	int retVal = G_SUCCESS;

	// based on opc/ssn fetch list of registered SAS Address 
	// and send to peer.
	INGwLdPcSsnList opcSsnLst = m_ldDstMgr->getOpcSsnList();

	INGwFtPktLoadDistMsg ldDistPkt;

	for (int i=0; i < opcSsnLst.size(); ++i) {
		vector<string> sasAddrList = m_ldDstMgr->getAllRegSasApp(
													opcSsnLst[i].m_pc, opcSsnLst[i].m_ssn);

		if (true != sasAddrList.empty()) {
			ldDistPkt.appendSasDestAddrToList(sasAddrList, 
							opcSsnLst[i].m_pc, opcSsnLst[i].m_ssn);
		}
	}

	if (0 < ldDistPkt.getLdDistMsgCnt()) {
		ldDistPkt.initialize(REGISTER_SAS_APP, 
				INGwIfrPrParamRepository::getInstance().getSelfId(),
				INGwIfrPrParamRepository::getInstance().getPeerId());

		INGwIfrMgrManager::getInstance().sendMsgToINGW(&ldDistPkt);
	}

	LogINGwTrace(false, 0, "OUT INGwTcapFtHandler::replicateInfoToPeer()");
	return retVal;
}

int
INGwTcapFtHandler::handleTcapCallDataFromPeer(INGwFtPktCreateTcapSession &p_tcapMsg)
{
  LogINGwTrace(false, 0, "+FT+ IN INGwTcapFtHandler::handleTcapCallDataFromPeer()");
  int retVal = G_SUCCESS;

  LogINGwTrace(false, 0, "+FT+ OUT INGwTcapFtHandler::handleTcapCallDataFromPeer()");
  return retVal;
}

int
INGwTcapFtHandler::sendCreateTcapSessionMsg(g_TransitObj	p_transitObj,
                                                TcapMessage *p_tcapMsg
                                                /*U8 p_msgType*/)
{
	logger.logINGwMsg(false,TRACE_FLAG, 0, 
     "IN INGwTcapFtHandler::sendCreateTcapSessionMsg() buflen <%d> dlg"
     " <%d> ssn <%d> suId <%d> spId <%d> seqNum <%d> msgType<%d>",
     p_transitObj.m_bufLen, p_transitObj.m_stackDlgId, p_transitObj.m_ssn, 
     p_transitObj.m_suId, p_transitObj.m_spId, p_transitObj.m_seqNum,p_tcapMsg->dlgR.dlgType);

 if(mDisableMsgFtFlag) {
   logger.logINGwMsg(false,TRACE_FLAG,0,
                                      "Out sendCreateTcapSessionMsg SKIPPING");
    return G_SUCCESS;
 }
  if(0 == p_transitObj.m_buf || 0 == p_transitObj.m_bufLen) {
    logger.logINGwMsg(false, VERBOSE_FLAG, 0,
    "+FT+ [sendCreateTcapSessionMsg] Cannot replicate Data <%x> Length<%d>",
     p_transitObj.m_buf, p_transitObj.m_bufLen);
    return G_FAILURE;
  }
	int retVal = G_SUCCESS;
	INGwFtPktCreateTcapSession lTcapCallDataPkt;
  AppInstId lappInstId;
  lappInstId.suId = p_transitObj.m_suId;
  lappInstId.spId = p_transitObj.m_spId;

  bool lIsPeerUp= 
  (INGwIfrPrParamRepository::getInstance().getPeerStatus("sendCreateTcapSessionMsg") == 1) ?
   true:false;

  lTcapCallDataPkt.setTcapMsgInfo(p_transitObj.m_buf, p_transitObj.m_bufLen,
                                  p_transitObj.m_stackDlgId,
                                  p_transitObj.m_userDlgId,
                                  p_transitObj.m_seqNum,
                                  p_transitObj.m_ssn, 
                                  lappInstId,
                                  p_tcapMsg->dlgR.dlgType,
                                  p_transitObj.m_sasIp,//<new>
                                  *(p_tcapMsg->dlgR.srcAddr),//<new>
                                  *(p_tcapMsg->dlgR.dstAddr),//<new>
                                  lIsPeerUp);

  short lsDirection = (p_transitObj.m_stackDlgId == p_transitObj.m_userDlgId)?
                       MSG_TCAP_INBOUND : MSG_TCAP_OUTBOUND;

	lTcapCallDataPkt.initialize(lsDirection, 
		INGwIfrPrParamRepository::getInstance().getSelfId(),
		INGwIfrPrParamRepository::getInstance().getPeerId());

  char*  lpcMode = "";
  if(lIsPeerUp) {
    lpcMode = "*R MODE*";
	  INGwIfrMgrManager::getInstance().sendMsgToINGW(&lTcapCallDataPkt);
  }
  else {
    lpcMode = "*BR MODE*";
    if(!gbPeerConnected) {
     handleCreateTcapSessionMsg(lTcapCallDataPkt);  
    }
    else {
       logger.logINGwMsg(false,ERROR_FLAG,0,
                                       "R E M F T handleCreateTcapSessionMsg");
    }
  }

  logger.logINGwMsg(false,TRACE_FLAG,0,
  "OUT INGwTcapFtHandler::sendCreateTcapSessionMsg() <%s>",lpcMode);
	return retVal;
}

int
INGwTcapFtHandler::replicateTcapCallDataToPeer ( U8 p_origin, U8* p_buf,
                                                 U32 p_bufLen, int p_seqNum, 
                                                 int p_dlgId)
{
	logger.logINGwMsg(false,TRACE_FLAG, 0,
   "IN INGwTcapFtHandler::replicateTcapCallDataToPeer() p_origin <%d> p_buf "
   "<%d> p_bufLen <%d> p_seqNum<%d> p_dlgId<%d>",p_origin, p_buf, p_bufLen, 
    p_seqNum, p_dlgId);

  if(mDisableMsgFtFlag) {
    logger.logINGwMsg(false,TRACE_FLAG,0,"Out replicateTcapCallDataToPeer "
                                         "SKIPPING");
    return G_SUCCESS;
  }

  
  int retVal = G_SUCCESS;
  INGwFtPktUpdateTcapSession lPktUpdateTcapSession;
  lPktUpdateTcapSession.initialize(p_origin, 
	INGwIfrPrParamRepository::getInstance().getSelfId(),
	INGwIfrPrParamRepository::getInstance().getPeerId());

  bool lIsPeerup =
  INGwIfrPrParamRepository::getInstance().getPeerStatus("INGwFtPktUpdateTcapSession") == 1?
                                                             true:false;

  lPktUpdateTcapSession.setTcapMsgInfo(p_buf, p_bufLen, p_seqNum,lIsPeerup);

  map<int, INGwTcapSession*>::iterator it = m_tcapSessionMap.find(p_dlgId);
  char*  lpcMode = "";
  if(lIsPeerup) {
    lpcMode = "*R MODE*";
    if(m_tcapSessionMap.end() != it) {
      if((it->second)->getMajorState() != TC_CONN_CREATED) {
         //check if already sent to peer    
      }
      
    }

	  INGwIfrMgrManager::getInstance().sendMsgToINGW(&lPktUpdateTcapSession);
  }
  else {
    lpcMode = "*BR MODE*";
   if(!gbPeerConnected) {
     handleUpdateTcapSession(lPktUpdateTcapSession);
   }
   else {
      logger.logINGwMsg(false,ERROR_FLAG,0,
                                      "R E M F T handleUpdateTcapSession");
   }
  }

	logger.logINGwMsg(false,TRACE_FLAG, 0,
	  "Out INGwTcapFtHandler::replicateTcapCallDataToPeer() <%s>",lpcMode);
  return retVal;
}

INGwTcSessionMinorState gcTrState;
int
INGwTcapFtHandler::handleCreateTcapSessionMsg(INGwFtPktCreateTcapSession &p_addTcapDlg)
{
	LogINGwTrace(false, 0, "IN INGwTcapFtHandler::handleCreateTcapSessionMsg()");
  LOCKSESSION();
	int retVal = G_SUCCESS;
	logger.logINGwMsg(false, TRACE_FLAG, 0,
	"[handleAddDialogue] Msg Rxed from peer [%s]", p_addTcapDlg.toLog().c_str());

	short ltcapMsgMsgType = p_addTcapDlg.getTcapMsgMsgType();
	t_tcapMsgInfo* ltcapMsgInfo = p_addTcapDlg.getTcapMsgInfo();
   
  int lStkDlg = ltcapMsgInfo->m_stackDialogue;
  //WIN get userdialogueId here
  int liUserDlgId = ltcapMsgInfo->m_userDialogue;

  map<int, INGwTcapSession*>::iterator it = m_tcapSessionMap.find(lStkDlg);  
  INGwTcapSession *lpTcSession; 

  if(m_tcapSessionMap.end() != it) {
    INGwFtPktTermTcapSession l_termSession;
    l_termSession.initialize(lStkDlg,0,0,ltcapMsgInfo->isReplicated);
    handleTerminateTcapSession(l_termSession);  
	  logger.logINGwMsg(false, ERROR_FLAG, 0,"Deleting hanging Session<%d>",
    lStkDlg);
  }


  INcStr lObjAcn;
  int lBufLen = 0;
  char lBuf[100];

  
#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
  StAnsiDlgEv l_ansiDlgEv;
  memset(&l_ansiDlgEv, 0, sizeof(StAnsiDlgEv));
#endif

  if(!(TcapMessage::getObjAcn(ltcapMsgInfo->m_buf, 
                         ltcapMsgInfo->m_bufLen,

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
                         l_ansiDlgEv,
#else
                         lObjAcn, 
#endif

                         APP_CONTEXT_NAME)))
  {
	  logger.logINGwMsg(false, ERROR_FLAG, 0,
                      "Not able to extract dialogue portion");
  } else {

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
   TcapMessage::dumpAnsiDlgEv(l_ansiDlgEv);
#endif

   ////remove this after testing
   // for(int i=0;i<lObjAcn.len;i++) {
   //   
   //   lBufLen += sprintf(lBuf + lBufLen," %02X ",lObjAcn.string[i]); //remft
   // }
   // logger.logINGwMsg(false,TRACE_FLAG,0," ACN extracted as<%d> :<%s>",
   //                   lObjAcn.len, lBuf);    
  }

  char* lpcMode = "";

  U8 lcTransaction = getTcTransType(ltcapMsgInfo->m_buf);

  if(ltcapMsgInfo->isReplicated)
  {
    lpcMode = "*R MODE*"; 

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96) 
    StAnsiDlgEv l_ansiDlgEv;
#endif
    if(!(JAIN_UNIDIRECTIONAL == lcTransaction || STU_UNI == lcTransaction))
    {
    
      TcapMessage::storeUserInformationFT(lStkDlg,
                                       lStkDlg,
                                       p_addTcapDlg.m_srcAddr,
                                       p_addTcapDlg.m_dstAddr,
                                       p_addTcapDlg.m_sasIp,
                                       ltcapMsgInfo->m_appInstId,
//decode OBJ/INT ACN FROM HERE
                                       #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)                                         
                                       l_ansiDlgEv,
                                       p_addTcapDlg.m_billingNo
                                       #else
                                       lObjAcn
                                       #endif
                                       );
    }



    switch(ltcapMsgMsgType)
    {
      case MSG_TCAP_INBOUND: 
      {
        mpTcapIncMsgHandler->setInBoundSeqNum(lStkDlg,ltcapMsgInfo->m_seqNum);
        mActiveReplayCallMap[lStkDlg - mLowDlgId] = 1;
        gcTrState = TC_MINSTATE_INBOUND_RX; 
      }
      break;

      case MSG_TCAP_OUTBOUND:
      {
        mpTcapIncMsgHandler->setOutBoundSeqNum
                             (liUserDlgId,ltcapMsgInfo->m_seqNum);

        mActiveReplayCallMap[liUserDlgId - mLowDlgId] = 1;
         gcTrState = TC_MINSTATE_OUTBOUND_TX; 
      }
      break;

      default:
      logger.logINGwMsg(false, ERROR_FLAG, 0,
       "unknown ltcapMsgMsgType %d userDid %d",ltcapMsgMsgType, liUserDlgId); 

      TcapMessage::clearUserInformationFT(liUserDlgId ,lStkDlg,
                                            ltcapMsgInfo->m_appInstId,
                                            p_addTcapDlg.m_sasIp);
      
      return G_FAILURE;
    }
  }
  else {
    lpcMode = "*BR MODE*"; 
  }

  logger.logINGwMsg(false,TRACE_FLAG,0,
                    "handleCreateTcapSessionMsg <%s>",lpcMode);

	 //int selfId   = INGwIfrPrParamRepository::getInstance().getSelfId();
	 //int peerId   = INGwIfrPrParamRepository::getInstance().getPeerId();
 
  int liDummy = 1;


  INGwFtPktTcapMsg *lptcapMsg = new (nothrow) INGwFtPktTcapMsg;

	lptcapMsg->initialize(lStkDlg,
                        liUserDlgId,
                        p_addTcapDlg.m_srcAddr,
									      p_addTcapDlg.m_dstAddr,
                        p_addTcapDlg.m_sasIp, 
                        ltcapMsgInfo->m_appInstId,

                        #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
                        l_ansiDlgEv, 
                        p_addTcapDlg.m_billingNo,
                        #else
                        lObjAcn,
                        #endif

                        liDummy,
                        liDummy);

     
  lpTcSession = new (nothrow) INGwTcapSession(ltcapMsgInfo->m_appInstId,
                                    ltcapMsgInfo->m_ssn,
                                    gcTrState);

  lpTcSession->setTcTransState(lcTransaction);
  lpTcSession->setInitialInfo(lptcapMsg);
  lpTcSession->setSasIp(p_addTcapDlg.m_sasIp);
  lpTcSession->setMajorState(TC_CONN_CREATED);

    switch(ltcapMsgMsgType)
    {
      case MSG_TCAP_INBOUND: 
      {
        //no check is required as we are creating the session (over seq Num) 
        lpTcSession->m_InbdlgMsgMap[ltcapMsgInfo->m_seqNum] =  new INGwTcapFtMsg
                                                  (ltcapMsgInfo->m_buf,
                                                   ltcapMsgInfo->m_bufLen,
                                                   ltcapMsgInfo->m_seqNum);
      }
      break;
 
     case MSG_TCAP_OUTBOUND:
     {
        INGwTcapWorkUnitMsg  *lWorkUnit;
        int liBufLen = ltcapMsgInfo->m_bufLen;
        TcapUserAddressInformation & tuai =
        TcapMessage::getUserAddressBy(lStkDlg, lpTcSession->m_appInstId);
        if(G_SUCCESS != createWorkUnit(ltcapMsgInfo->m_buf, 
                        liBufLen, 
                        &lWorkUnit,
                        tuai,
                        false))
        {
          //TBD error handling
          //how to store and update when SAS clust info is not available
        }
         
        lpTcSession->m_outboundMsgMap[ltcapMsgInfo->m_seqNum] =
                              new (nothrow) INGwTcOutMsgContainer(lWorkUnit);
                                                   
        
        lpTcSession->setMinorState(TC_MINSTATE_OUTBOUND_TX);
     }
     break;

     default:
     //control will never go here.
     {}
   }

//changes for QWP


//changes for QWP

  m_tcapSessionMap[lStkDlg] = lpTcSession;

  if(INGwTcapMsgLogger::getInstance().getLoggingLevel() > 2) {
    dumpTcapSessionMap("handleCreateTcapSessionMsg",false);
  }

	LogINGwTrace(false, 0, "OUT INGwTcapFtHandler::handleCreateTcapSessionMsg()");
	return retVal;
}

int 
INGwTcapFtHandler::handleFtTakeOver() {
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In handleFtTakeOver()"
  "+rem+ isReplayInProgress<%d>", gGetReplayFlg());

  map<short,bool>::iterator sapStatusIter;
  LOCKSESSION();
  int retVal = G_SUCCESS;
  TcapMessage *lptcMsg = new TcapMessage;
  g_TransitObj* lpTransitObj;

  int liBillingNo = 0;

  int count = 0;

  pthread_rwlock_wrlock(&gMapRWLock); 
    g_ActiveReplayCallCnt =  m_tcapSessionMap.size();
  pthread_rwlock_unlock(&gMapRWLock);


  char lBuf[4096];
  int lBufLen = 0;

  lBuf[0] = 0;

  map <int, INGwTcapSession*>::iterator tcSessionIter; 
  map<int,INGwTcapFtMsg*>::iterator  inbIter; 
  map<int, INGwTcOutMsgContainer*>::iterator  outbIter; 

  INGwTcapFtMsg* lMessageBuffer = NULL;
  INGwTcapWorkUnitMsg* lpTcWorkUnit  = NULL;
  INGwTcOutMsgContainer *lpContainer = NULL;

  int liSeqNum = -1;
  U32 lidlgId = 0;
  int retValSendTcReq;

  for(tcSessionIter = m_tcapSessionMap.begin();
      tcSessionIter!= m_tcapSessionMap.end()  ; tcSessionIter++)
  {
    INGwTcapSession* lpTcSession = tcSessionIter->second;

    lidlgId =  tcSessionIter->first;

    lBufLen += sprintf(lBuf + lBufLen, "\n[FTTO ML dId %d]",lidlgId);
    
    INGwTcSessionMinorState lMinState = lpTcSession->getMinorState();

    TcapUserAddressInformation & tuai = 
             TcapMessage::getUserAddressBy(lidlgId, lpTcSession->m_appInstId);  

    tuai.mDlgType = lpTcSession->getTcTransState();

    if(!tuai.isValid())
    {
      tuai.ipaddr = lpTcSession->getSasIp();
      lBufLen += sprintf(lBuf + lBufLen, "\nFTTO TUAI status <0> everyThing Ok,"
                         " returning", lidlgId);

      if(!(JAIN_UNIDIRECTIONAL == tuai.mDlgType || STU_UNI == tuai.mDlgType))
      {
        continue;
        //Yogesh changed on April 05'13
        //clearReplayMessageStore(false,INGW_CLEAR_R_MODE);
        //retVal = G_FAILURE;
        //break;
      }
      else{
        liBillingNo = lpTcSession->getBillingNo();
        tuai.mBillingId.setBillingNo(liBillingNo);
      }
    }

    for(inbIter = lpTcSession->m_InbdlgMsgMap.begin(); 
        inbIter!= lpTcSession->m_InbdlgMsgMap.end(); inbIter++)
    {
      lBufLen += sprintf(lBuf + lBufLen, " [FTTO IL dId %d]",lidlgId);

      liSeqNum = inbIter->first;
      lMessageBuffer = inbIter->second;
      if(lMessageBuffer->isValid())
      {
        lpTransitObj = new g_TransitObj;
        memset(lpTransitObj,0,sizeof(g_TransitObj));

        lpTransitObj->m_buf    = lMessageBuffer->m_buf;
        lpTransitObj->m_bufLen = lMessageBuffer->m_bufLen;
        lpTransitObj->m_stackDlgId = lidlgId;
        lpTransitObj->m_seqNum     = inbIter->first; 
        lpTransitObj->m_suId       = lpTcSession->m_appInstId.suId;
        lpTransitObj->m_spId       = lpTcSession->m_appInstId.spId;
        lpTransitObj->m_ssn        = lpTcSession->m_ssn;
        lpTransitObj->m_isDialogueComplete = lMessageBuffer->isLastMsg();
  
        logger.logINGwMsg(false,VERBOSE_FLAG,0,"tuai.ipaddr<%s>",
                          tuai.ipaddr.c_str());

        lpTransitObj->m_sasIp = tuai.ipaddr;

        lpTransitObj->m_billingNo = tuai.mBillingId.getBillingNo();

        //do not push this message in TCAP worker queues, send it via  IwIf
        mpTcapIncMsgHandler->handleInbMsgReplay(lpTransitObj);

        delete lMessageBuffer;//this wont delete m_buf;
        lMessageBuffer = NULL;
        //lpTcSession->m_InbdlgMsgMap.erase(liSeqNum);  
      }  
    }

//putimed wait here  
    while(true) {
      //rwlock is required here
      pthread_mutex_lock(&mutLockMap);
      if(((sapStatusIter = 
          sapStatusMap.find(lpTcSession->m_appInstId.suId)) != 
          sapStatusMap.end()) && (true==sapStatusIter->second)){
        count++;

        break;    
      }
      pthread_mutex_unlock(&mutLockMap);
      //10 milli sec
      //usleep(10000);
      count++;
      continue;
    }
    static bool lbPrintRetry = true;

    if(lbPrintRetry)
    {
      lBufLen += sprintf(lBuf + lBufLen, 
                          "\n[FTTO before OL dId %d suId <%d> iterCnt<%d>]",
                           lidlgId, sapStatusIter->first, count);
      lbPrintRetry = false;
    }

    pthread_mutex_unlock(&mutLockMap);

    for(outbIter = lpTcSession->m_outboundMsgMap.begin(); 
        outbIter != lpTcSession->m_outboundMsgMap.end(); outbIter++)
    {
      liSeqNum = outbIter->first;
      lpContainer = outbIter->second;

      if(lpContainer) {
        lpTcWorkUnit = lpContainer->mpWorkUnitMsg;
      

        if(NULL != lpTcWorkUnit)
        {

          lBufLen += sprintf(lBuf + lBufLen, " [FTTO OL dId %d]",lidlgId);

          retValSendTcReq = INGwTcapProvider::getInstance().getAinSilTxRef().
                                                    sendTcapReq(lpTcWorkUnit);
          if(-1 == retValSendTcReq)
          {
            logger.logINGwMsg(false, ERROR_FLAG,0,
                  "Replay: FTTO Cannot Send Req to stack");
          }

          delete lpTcWorkUnit->m_tcapMsg; lpTcWorkUnit->m_tcapMsg = NULL;
          delete lpTcWorkUnit; lpTcWorkUnit = NULL;
          delete lpContainer; lpContainer = NULL;
        }
      }
    }

    lpTcSession->m_outboundMsgMap.erase(lpTcSession->m_outboundMsgMap.begin(),
                                  lpTcSession->m_outboundMsgMap.end());

    lpTcSession->m_InbdlgMsgMap.erase(lpTcSession->m_InbdlgMsgMap.begin(),
                                      lpTcSession->m_InbdlgMsgMap.end());
    delete lpTcSession; lpTcSession = 0;

    if(lBufLen > 3500) 
    {
       logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleFtTakeOver() \n<%s>",lBuf);
       lBufLen = 0;
       lBuf[0] = 0;
    }
  }


  m_tcapSessionMap.erase(m_tcapSessionMap.begin(),
                         m_tcapSessionMap.end());

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleFtTakeOver() \n<%s>",lBuf);
  return retVal;
}

//clean from inbMap if rx'd 200 ok
//clean from outbMap if before tx 200 ok
int
INGwTcapFtHandler::handleUpdateTcapSession(INGwFtPktTcapCallSeqAck &p_ackMsg) {
  int retVal = G_SUCCESS;
	LogINGwTrace(false, 0, "In INGwTcapFtHandler::handleUpdateTcapSession()");
	logger.logINGwMsg(false, TRACE_FLAG, 0,
	"[handleUpdateDialogue] +rem+ Msg Rxed from peer [%s]",
   p_ackMsg.toLog().c_str());

  switch(p_ackMsg.getOrigin()) {
    case MSG_TCAP_INBOUND:
    {
      retVal = handleCleanInboundMessage(p_ackMsg); 
      break;
    }

    case MSG_TCAP_OUTBOUND:
    {
      retVal = handleCleanOutboundMessage(p_ackMsg);
      break;
    }

    default:
    logger.logINGwMsg(false,ERROR_FLAG,0,"handleUpdateTcapSession "
      "Unknown message %d", p_ackMsg.getOrigin());
  }

  return retVal;
}

//Add messages in TCAP Session
int
INGwTcapFtHandler::handleUpdateTcapSession
                  (INGwFtPktUpdateTcapSession &p_updTcapSession) {
  int retVal = G_SUCCESS;
	LogINGwTrace(false, 0, "In INGwTcapFtHandler::handleUpdateTcapSession()");
	logger.logINGwMsg(false, TRACE_FLAG, 0,
	"[handleUpdateDialogue] +rem+ Msg Rxed from peer [%s]",
   p_updTcapSession.toLog().c_str());

  
  switch(p_updTcapSession.getOrigin()) {
    case MSG_TCAP_INBOUND:
    {
      handleInboundMessage(p_updTcapSession); 
      break;
    }

    case MSG_TCAP_OUTBOUND:
    {
      handleOutboundMessage(p_updTcapSession);
      break;
    }
    default:
    logger.logINGwMsg(false,ERROR_FLAG,0,"handleUpdateTcapSession "
      "Unknown message %d", p_updTcapSession.getOrigin());
  }

	LogINGwTrace(false, 0, "OUT INGwTcapFtHandler::handleUpdateTcapSession()");
  return retVal;
}

int 
INGwTcapFtHandler::handleInboundMessage
                  (INGwFtPktUpdateTcapSession &p_updTcapSession)
{
	LogINGwTrace(false, 0, "In INGwTcapFtHandler::handleInboundMessage()");
  int retVal = G_SUCCESS; 
  int lStkDlg = p_updTcapSession.getDialogueId();
  
  //check map 
  INGwTcapSession *lpTcSession = NULL; 
  LOCKSESSION();
  map<int, INGwTcapSession*>::iterator it = m_tcapSessionMap.find(lStkDlg); 

  //Yogesh: use a "bit/byte array" to check dialogue status instead of tree and 
  //byte array
  if(m_tcapSessionMap.end() == it) {
	  logger.logINGwMsg(false, ERROR_FLAG, 0,
      "Out INGwTcapFtHandler::handleInboundMessage update a non"
      " existing session dlg<%d> ",lStkDlg);
    lpTcSession = new INGwTcapSession(TC_MINSTATE_FTSYNCH_INBOUND_RX,
                                      TC_CONN_SYNCH_IN_PRGRS);

    m_tcapSessionMap[lStkDlg] = lpTcSession;
    //Yogesh: commenting the following to enable replication of mid-call 
    //messages
    //return G_FAILURE;
  } else {
    lpTcSession = it->second;
  }

  //ltcapMsgInfo will never be allocated in heap 
  t_tcapMsgBuffer* ltcapMsgInfo = p_updTcapSession.getTcapMsgBuffer();
  
  //remove this check as A-INC is taking care of retransmissions +rem+
  if(lpTcSession->m_InbdlgMsgMap.end() == lpTcSession->m_InbdlgMsgMap.
                                   find(ltcapMsgInfo->m_seqNum))
  {
    lpTcSession->m_InbdlgMsgMap[ltcapMsgInfo->m_seqNum] =  new INGwTcapFtMsg
                                                    (ltcapMsgInfo->m_buf,
                                                     ltcapMsgInfo->m_bufLen,
                                                     ltcapMsgInfo->m_seqNum);
    
    lpTcSession->setMinorState(TC_MINSTATE_INBOUND_RX);
    lpTcSession->setTcTransState(getTcTransType(ltcapMsgInfo->m_buf));

    logger.logINGwMsg(false,TRACE_FLAG,0,"setting seq inb <%d>",
                      ltcapMsgInfo->m_seqNum);

    mpTcapIncMsgHandler->setInBoundSeqNum(lStkDlg,ltcapMsgInfo->m_seqNum); 
    
  }
  else{
    logger.logINGwMsg(false,ERROR_FLAG,0,"handleInboundMessage sequence "
    "No. already keyed in Inboundmap <%d>",ltcapMsgInfo->m_seqNum);
  } 
  if(INGwTcapMsgLogger::getInstance().getLoggingLevel() > 2){
    dumpTcapSessionMap("handleInboundMessage",false);
  }

  char* lpcMode = "";
  lpcMode = ((true == p_updTcapSession.isReplicated())?"*R MODE*":"*BR MODE*");

  logger.logINGwMsg(false,TRACE_FLAG,0,
  "OUT INGwTcapFtHandler::handleInboundMessage <%s>", lpcMode);
  return retVal;
}

int 
INGwTcapFtHandler::handleOutboundMessage
                  (INGwFtPktUpdateTcapSession &p_updTcapSession)
{
  //take care to delete WorkUnit, TcapMessage, and the component vector
	LogINGwTrace(false, 0, "In INGwTcapFtHandler::handleOutboundMessage()");
  int retVal = G_SUCCESS; 
  
  int lStkDlg = p_updTcapSession.getDialogueId();

  //check hashmap
  INGwTcapSession *lpTcSession = NULL; 
  LOCKSESSION();
  bool lbIsStrayMsg = false;
  map<int, INGwTcapSession*>::iterator it = m_tcapSessionMap.find(lStkDlg);  
  bool isSessionValid = false; 

  t_tcapMsgBuffer* ltcapMsgInfo = p_updTcapSession.getTcapMsgBuffer();

  //Yogesh: use a "bit/byte array" to check dialogue status instead of tree
  if(m_tcapSessionMap.end() == it) {
	  logger.logINGwMsg(false, ERROR_FLAG, 0,
      "Out INGwTcapFtHandler::handleOutboundMessage creating temporary "
      "session <dlg %d> ",lStkDlg);

    lpTcSession = new INGwTcapSession(TC_MINSTATE_FTSYNCH_INBOUND_RX,
                                      TC_CONN_SYNCH_IN_PRGRS);
    lbIsStrayMsg = true;
    m_tcapSessionMap[lStkDlg] = lpTcSession;

    //return G_FAILURE;
  }
  else{
    lpTcSession = it->second; 
  }

  //getTcapMsgBuffer will return object on heap or on stack depending on 
  //peer's state

  TcapUserAddressInformation & tuai =
  TcapMessage::getUserAddressBy(lStkDlg, lpTcSession->m_appInstId);

  if(p_updTcapSession.isReplicated())  {
    logger.logINGwMsg(false,TRACE_FLAG,0,"handleOutboundMessage *R Mode*");

    if((!tuai.isValid())  &&  (!lbIsStrayMsg)) 
    {
	    logger.logINGwMsg(false, ALWAYS_FLAG, 0,"handleOutboundMessage,Cannot "
      "obtain Ip Address from TUAI <%d> <%d> everything Ok",
      lStkDlg,lbIsStrayMsg);

      return G_FAILURE;   
    }
    
    logger.logINGwMsg(false,TRACE_FLAG,0,"setting seq outb <%d>",
                                            ltcapMsgInfo->m_seqNum);

    mpTcapIncMsgHandler->updateLastSeqNumForDlg(lStkDlg,
                                                ltcapMsgInfo->m_seqNum);
    
    INGwTcapWorkUnitMsg  *lWorkUnit;

    //remove this check as A-INC is taking care of retransmissions +rem+
    if(lpTcSession->m_outboundMsgMap.end() == 
       lpTcSession->m_outboundMsgMap.find(ltcapMsgInfo->m_seqNum)) {
       if(G_SUCCESS != createWorkUnit(ltcapMsgInfo->m_buf, 
                       ltcapMsgInfo->m_bufLen, 
                       &lWorkUnit,
                       tuai,
                       lbIsStrayMsg))
       {
         //TBD error handling
         //how to store and update when SAS clust info is not available
       }
        
      lpTcSession->m_outboundMsgMap[ltcapMsgInfo->m_seqNum] =
                                new (nothrow) INGwTcOutMsgContainer(lWorkUnit);
                                     //static_cast<void *>(lWorkUnit);        
                                                 
    
      lpTcSession->setMinorState(TC_MINSTATE_OUTBOUND_TX);
    }
    else{
      logger.logINGwMsg(false,ERROR_FLAG,0,"handleOutboundMessage sequence "
      "No.<%d> already keyed in outBoundmap ",ltcapMsgInfo->m_seqNum);
    }

    if(NULL != ltcapMsgInfo->m_buf) {
      delete [] ltcapMsgInfo->m_buf;
      ltcapMsgInfo->m_buf = NULL;
    }

  } else{
    //now we need to accumulate this message
    logger.logINGwMsg(false,TRACE_FLAG,0,"handleOutboundMessage *BR Mode*");
    lpTcSession->m_outboundMsgMap[ltcapMsgInfo->m_seqNum] = 
                             new (nothrow) INGwTcOutMsgContainer(ltcapMsgInfo);
                                     //static_cast<void *>(ltcapMsgInfo);

    lpTcSession->setMinorState(TC_MINSTATE_OUTBOUND_TX);
  } 
  if(INGwTcapMsgLogger::getInstance().getLoggingLevel() > 2){
    dumpTcapSessionMap("handleOutboundMessage",false);
  }   

  char *lpcMode = ((true == p_updTcapSession.isReplicated())?"*R MODE*":"*BR MODE*");
  logger.logINGwMsg(false,TRACE_FLAG,0,
  "OUT INGwTcapFtHandler::handleOutboundMessage() <%s>", lpcMode);
  return retVal;
}

int 
INGwTcapFtHandler::handleTerminateTcapSession
                  (INGwFtPktTermTcapSession& p_termSession) 
{
	logger.logINGwMsg(false, TRACE_FLAG, 0,"In handleTerminateTcapSession");
  LOCKSESSION();

#ifdef  MSG_FT_TEST
  if(mbBlkTermination) {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleTerminateTcapSession "
    " deliberately not cleaning the session");
    return G_SUCCESS;
  }
#endif

  int retVal = G_SUCCESS;
  int lStkDlg = p_termSession.getDialogueId();
  map <int, INGwTcapSession*> ::iterator it =  m_tcapSessionMap.find(lStkDlg);
  INGwTcapSession* lpTcapSession = NULL;
  //Yogesh: use a "bit/byte array" to check dialogue status instead of tree and 
  //byte array
  char* lpcMode = "";
  if(m_tcapSessionMap.end() == it) {
    logger.logINGwMsg(false, ERROR_FLAG, 0,
      "Out handleTerminateTcapSession- cannot terminate a non"
      " existing sequence dlg ",lStkDlg);//, p_AckMsg.getSeqNum());
    return G_FAILURE;
  }
  else
  {
    lpTcapSession = it->second;

    if(p_termSession.receivedFromPeer()) 
    {
      lpcMode = "*R MODE*";
      mpTcapIncMsgHandler->resetSeqNumForDlg(lStkDlg);
      TcapUserAddressInformation & tuai =
            TcapMessage::getUserAddressBy(lStkDlg, 
                                          lpTcapSession->m_appInstId);

      U8 lcTcDlgType = lpTcapSession->getTcTransState();

      if(!tuai.isValid()) 
      {
        if(!(STU_UNI == lcTcDlgType || JAIN_UNIDIRECTIONAL == lcTcDlgType))
        {
	        logger.logINGwMsg(false, ALWAYS_FLAG, 0,
          "Out handleTerminateTcapSession, cannot obtain Ip Address from TUAI"
          " everything ok <%d>",lStkDlg);
        }
      }
      else{
        TcapMessage::clearUserInformationFT(lStkDlg,lStkDlg,
                                            lpTcapSession->m_appInstId,
                                            tuai.ipaddr);
      } 
      mActiveReplayCallMap[lStkDlg - mLowDlgId] = 0;
    } else
    {
      lpcMode = "*BR MODE*";
    }


    logger.logINGwMsg(false,TRACE_FLAG,0," handleTerminateTcapSession <%s>",lpcMode);

    lpTcapSession->cleanTcapSession(p_termSession.receivedFromPeer()); 
    delete lpTcapSession;
    lpTcapSession = NULL;
    m_tcapSessionMap.erase(lStkDlg);
  }

  if(INGwTcapMsgLogger::getInstance().getLoggingLevel() > 2) {
    dumpTcapSessionMap("handleTerminateTcapSession",false);
  }   

	logger.logINGwMsg(false, TRACE_FLAG, 0,"Out handleTerminateTcapSession");
  return retVal;
}

int 
INGwTcapFtHandler::handleCleanInboundMessage(INGwFtPktTcapCallSeqAck &p_AckMsg){
  logger.logINGwMsg(false,TRACE_FLAG, 0, "IN handleCleanInboundMessage"
  " dlgId<%d> SeqNum<%d>",p_AckMsg.getDialogueId(), p_AckMsg.getSeqNum()); 
  int retVal = G_SUCCESS;

  LOCKSESSION();

#ifdef MSG_FT_TEST
  if(mbBlkInbCleanup) {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleCleanInboundMessage "
     "Deliberately not cleaning inbound msg");
    return retVal;
  }
#endif

  int lStkDlg = p_AckMsg.getDialogueId();
  int lSeqNum = p_AckMsg.getSeqNum();
  INGwTcapFtMsg* lmsgBuffer;


  
  map <int, INGwTcapSession*> ::iterator it =  m_tcapSessionMap.find(lStkDlg);
  //Yogesh: use a "bit/byte array" to check dialogue status instead of tree and 
  //byte array
  if(m_tcapSessionMap.end() == it) {
	  logger.logINGwMsg(false, WARNING_FLAG, 0,
      "Out handleCleanInboundMessage - cannot clean a non"
      " existing sequence <%d> in dlg <%d>",lSeqNum, lStkDlg);
      //, p_AckMsg.getSeqNum());

    return G_FAILURE;
  } 

  INGwTcapSession* lpTcSession = it->second;
  if( lpTcSession->m_InbdlgMsgMap.end() == 
      lpTcSession->m_InbdlgMsgMap.find(lSeqNum) ) {
	  logger.logINGwMsg(false, WARNING_FLAG, 0,"Out handleCleanInboundMessage"
    " Cannot find sequence number <%d> in inboundMsgMap dlg<%d>",
     lSeqNum, lStkDlg);

    return G_FAILURE;
  }

  lmsgBuffer = lpTcSession->m_InbdlgMsgMap[lSeqNum];
  if(lmsgBuffer->m_buf){
    //clean the buffer 
    delete [] lmsgBuffer->m_buf;
    lmsgBuffer->m_buf = 0;
    lmsgBuffer->m_bufLen = 0;
    delete  lmsgBuffer;
    lmsgBuffer = 0;
    logger.logINGwMsg(false,VERBOSE_FLAG,0, "+rem+ handleCleanInboundMessage"
    " Deleted buffer for sequence %d", lSeqNum);
    lpTcSession->setMinorState(TC_MINSTATE_INBOUND_RX_DONE);
    lpTcSession->m_InbdlgMsgMap.erase(lSeqNum);
  }
  
  else{
    logger.logINGwMsg(false,ERROR_FLAG,0,"handleCleanInboundMessage FT +VER+"
    " Cannot find the buffer to delete");
  }
  if(INGwTcapMsgLogger::getInstance().getLoggingLevel() > 2) {
    dumpTcapSessionMap("handleCleanInboundMessage",false);
  }

  if(STU_UNI == lpTcSession->getTcTransState()) 
  {
    INGwFtPktTermTcapSession l_termSession;
    l_termSession.initialize(lStkDlg, 0, 0, p_AckMsg.isReplicated());
    handleTerminateTcapSession(l_termSession);  
	  logger.logINGwMsg(false, ERROR_FLAG, 0,"Deleting STU_UNI Session<%d>",
                                            lStkDlg);
  }

  char *lpcMode = ((true == p_AckMsg.isReplicated())?"*R MODE*":"*BR MODE*"); 
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out handleCleanInboundMessage <%s>",lpcMode);
  return retVal;
}

int 
INGwTcapFtHandler::handleCleanOutboundMessage(INGwFtPktTcapCallSeqAck &p_AckMsg){
  int retVal = G_SUCCESS;
  logger.logINGwMsg(false,TRACE_FLAG, 0, "IN handleCleanOutboundMessage "
    "dlgId<%d> SeqNum<%d>",p_AckMsg.getDialogueId(), p_AckMsg.getSeqNum()); 
  LOCKSESSION();

#ifdef MSG_FT_TEST
  if(mbBlkOutbCleanup) {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleCleanOutboundMessage"
     "Deliberately not cleaning outbound msg");
    return retVal;
  }
#endif

  int lStkDlg = p_AckMsg.getDialogueId();
  int lSeqNum = p_AckMsg.getSeqNum();
  INGwTcapWorkUnitMsg* lpWorkUnit;
  map <int, INGwTcapSession*> ::iterator it =  m_tcapSessionMap.find(lStkDlg);

  //Yogesh: use a "bit/byte array" to check dialogue status instead of tree and 
  //byte array
  if(m_tcapSessionMap.end() == it) {
	  logger.logINGwMsg(false, WARNING_FLAG, 0,
      "Out handleCleanOutboundMessage - cannot clean a non"
      " existing sequence <%d> dlg <%d> ",lSeqNum, lStkDlg);//, p_AckMsg.getSeqNum());
    return G_FAILURE;
  }   

  INGwTcapSession *lpTcSession = it->second;
  INGwTcOutMsgContainer *lpContainer;
  map<int, INGwTcOutMsgContainer*> :: iterator  iter;
  if(lpTcSession->m_outboundMsgMap.end() == (iter = lpTcSession->m_outboundMsgMap.
                                     find(p_AckMsg.getSeqNum()) )) 
  {
	  logger.logINGwMsg(false, WARNING_FLAG, 0,"Out handleCleanOutboundMessage"
    " Cannot find sequence number <%d> in m_outboundMsgMap dlg<%d>",
    p_AckMsg.getSeqNum(), lStkDlg);

    return G_FAILURE;
  }
  
  lpContainer = iter->second;

  if(p_AckMsg.isReplicated()) {
    lpWorkUnit = lpContainer->mpWorkUnitMsg;
    lpTcSession->setTcTransState(lpWorkUnit->m_tcapMsg->dlgR.dlgType);
    lpWorkUnit  = lpContainer->mpWorkUnitMsg;

    INGwTcapProvider::getInstance().getAinSilTxRef().
                                                   releaseWorkUnit(lpWorkUnit);
    delete lpWorkUnit->m_tcapMsg;
    lpWorkUnit->m_tcapMsg = NULL;
    
    delete lpWorkUnit;
    lpWorkUnit = NULL;
    

  }else{
    t_tcapMsgBuffer* ltcapMsgInfo =  lpContainer->mtcapMsgInfo;
                                                
    if(ltcapMsgInfo && (NULL != ltcapMsgInfo->m_buf)) {
      delete [] ltcapMsgInfo->m_buf; ltcapMsgInfo->m_buf =NULL;
      delete ltcapMsgInfo; ltcapMsgInfo = NULL;
    }
    else 
    {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleCleanOutboundMessage"
      " tuai not found everything ok <%d>");
    } 
    
  }

  delete lpContainer; lpContainer = NULL;

  lpTcSession->setMajorState(TC_CONN_CONNECTED);  
  lpTcSession->setMinorState(TC_MINSTATE_OUTBOUND_TX_DONE);
  lpTcSession->m_outboundMsgMap.erase(lSeqNum);
  if(INGwTcapMsgLogger::getInstance().getLoggingLevel() > 2){
    dumpTcapSessionMap("handleCleanOutboundMessage",false);
  } 

  if(JAIN_UNIDIRECTIONAL == lpTcSession->getTcTransState()) 
  {
    INGwFtPktTermTcapSession l_termSession;
    l_termSession.initialize(lStkDlg,0,0,p_AckMsg.isReplicated());
    handleTerminateTcapSession(l_termSession);  
	  logger.logINGwMsg(false, ERROR_FLAG, 0,"Deleting STU_UNI Session<%d>",
                                            lStkDlg);
  }

  char *lpcMode = ((true == p_AckMsg.isReplicated())?"*R MODE*":"*BR MODE*"); 
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out handleCleanOutboundMessage <%s>",lpcMode);
  return retVal;
}

//to clean all the messages for a SAS cluster that has gone down
int 
INGwTcapFtHandler::handleINGwCleanup() {
 int retVal = G_SUCCESS;
 return retVal;
}

int 
INGwTcapFtHandler::sendTcapSessionUpdateInfo(U8 p_dir, int p_dialogueId,
                                             int p_seqNum) {
 logger.logINGwMsg(false,TRACE_FLAG,0,"In sendTcapSessionUpdateInfo p_dir<%d> "
   "p_dialogueId<%d>  p_seqNum<%d>",p_dir,p_dialogueId,p_seqNum);
 if(mDisableMsgFtFlag) {
   logger.logINGwMsg(false,TRACE_FLAG,0,"Out sendTcapSessionUpdateInfo SKIPPING");
    return G_SUCCESS;
 }

 int retVal = G_SUCCESS;

   bool lIsPeerUp= 
   (INGwIfrPrParamRepository::getInstance().getPeerStatus("INGwFtPktTcapCallSeqAck") == 1)?
   true:false;
   INGwFtPktTcapCallSeqAck lAckmsg; 
   lAckmsg.initialize(p_dir, p_dialogueId, p_seqNum,
    			            INGwIfrPrParamRepository::getInstance().getSelfId(),
    			            INGwIfrPrParamRepository::getInstance().getPeerId(),
                      lIsPeerUp);

 char * lpcMode = "";

 if(lIsPeerUp) {
   lpcMode = "*R MODE*";
   INGwIfrMgrManager::getInstance().sendMsgToINGW(&lAckmsg); 
 }
 else{
   lpcMode = "*BR MODE*";
   if(!gbPeerConnected) {
     handleUpdateTcapSession(lAckmsg);
   }
   else {
      logger.logINGwMsg(false,ERROR_FLAG,0,
                                      "R E M F T handleUpdateTcapSession");
   }
 }

 logger.logINGwMsg(false,TRACE_FLAG,0,"Out sendTcapSessionUpdateInfo <%s>",lpcMode);
 return retVal;
}

int
INGwTcapFtHandler::sendTerminateTcapSession(U32 p_dialogueId){
 logger.logINGwMsg(false,TRACE_FLAG,0,"In sendTerminateTcapSession "
                                      "DlgId <%d> ",p_dialogueId);
 if(mDisableMsgFtFlag) {
   logger.logINGwMsg(false,TRACE_FLAG,0,"Out sendTerminateTcapSession SKIPPING");
    return G_SUCCESS;
 }
 
 bool lIsPeerUp= 
 (INGwIfrPrParamRepository::getInstance().getPeerStatus("INGwFtPktTermTcapSession") == 1)?
  true:false;

 INGwFtPktTermTcapSession lTerminateSession;
 lTerminateSession.initialize(p_dialogueId,
  			            INGwIfrPrParamRepository::getInstance().getSelfId(),
  			            INGwIfrPrParamRepository::getInstance().getPeerId(),
                    lIsPeerUp);

 if(lIsPeerUp) {
   INGwIfrMgrManager::getInstance().sendMsgToINGW(&lTerminateSession);
 } 
 else {
   //yogesh rem
   if(!gbPeerConnected) {
     handleTerminateTcapSession(lTerminateSession); 
   }
   else {
      logger.logINGwMsg(false,ERROR_FLAG,0,
                                      "R E M F T handleTerminateTcapSession");
   }
 }

 logger.logINGwMsg(false,TRACE_FLAG,0,"Out sendTerminateTcapSession");

 return G_SUCCESS;
}

int
INGwTcapFtHandler::sendSasHBFailureMsg(const string &p_sasIpAddr,
                                      AppInstId &p_appId)
{
 int retVal = G_SUCCESS;
 logger.logINGwMsg(false,TRACE_FLAG,0,"In sendSasHBFailureMsg Ip <%s> "
   "suId <%d> spId<%d>",p_sasIpAddr.c_str(), p_appId.suId, p_appId.spId);
 if(mDisableMsgFtFlag) {
   logger.logINGwMsg(false,TRACE_FLAG,0,"Out sendSasHBFailureMsg SKIPPING");
    return G_SUCCESS;
 }
  
 if(INGwIfrPrParamRepository::getInstance().getPeerStatus("INGwFtPktSasHBFailure")) {
    INGwFtPktSasHBFailure lPktSasHBFailure;
    lPktSasHBFailure.initialize(p_sasIpAddr,p_appId,
    			            INGwIfrPrParamRepository::getInstance().getSelfId(),
    			            INGwIfrPrParamRepository::getInstance().getPeerId());

   INGwIfrMgrManager::getInstance().sendMsgToINGW(&lPktSasHBFailure); 
 }
 else {
   //TBD:
   if(!gbPeerConnected) {
    clearReplayMessageStore(true, INGW_CLEAR_BR_MODE);
   }
   else {
      logger.logINGwMsg(false,ERROR_FLAG,0,
                                      "R E M F T clearReplayMessageStore");
   }
 
 }
 logger.logINGwMsg(false,TRACE_FLAG,0,"Out sendSasHBFailureMsg");
 return retVal;
}

int
INGwTcapFtHandler::handleSasHBFailure(INGwFtPktSasHBFailure &p_hbFailureMsg) {
  int retVal = G_SUCCESS;
  logger.logINGwMsg(false,TRACE_FLAG,0,"In handleSasHBFailure");
  LOCKSESSION(); 
  string lsasIp = "";
  AppInstId lappId;
  p_hbFailureMsg.getSasIp(lsasIp);
  p_hbFailureMsg.getAppId(lappId);
  logger.logINGwMsg(false,TRACE_FLAG,0,"[handleSasHBFailure] Ip <%s> suId <%d>"
                    " spId<%d> ",lsasIp.c_str(),lappId.suId,lappId.spId);

  TcapMessage lTcapMessage;
  lTcapMessage.cleanup(lsasIp, lappId, true);
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out handleSasHBFailure");
  return retVal;
}

map <int, INGwTcapSession*> *
INGwTcapFtHandler::getTcapSessionMap(){
  logger.logINGwMsg(false,TRACE_FLAG,0,"In getTcapSessionMap");
  return  &m_tcapSessionMap;
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out getTcapSessionMap");
}

map <int, INGwFtPktTcapMsg*> *
INGwTcapFtHandler::getInitialInfoMap(){
  logger.logINGwMsg(false,TRACE_FLAG,0,"In getTcapSessionMap");
  return  &m_initialInfoMap;
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out getTcapSessionMap");
}

void 
INGwTcapFtHandler::dumpTcapSessionMap(char *aStr, bool abLockSessionMap) {
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In dumpTcapSessionMap");
  
  if(abLockSessionMap) {
    LOCKSESSION();
  }
  char lBuf[4096];
  int lBufLen = 0;
  if(aStr)
  lBufLen += sprintf(lBuf + lBufLen,"\n==>%s\n",aStr);

  lBufLen+= sprintf(lBuf + lBufLen, 
    "\n----------------------TC-Session-Map-Dump -------------------------\n");

  struct timeval currTime;
  struct tm localTime;
  gettimeofday(&currTime, 0);
  localtime_r(&currTime.tv_sec, &localTime);

  lBufLen+= strftime(lBuf + lBufLen, 64, "%C", &localTime);

  lBufLen+= sprintf(lBuf + lBufLen, " [%03d Msec.]\n", currTime.tv_usec/1000);

  lBufLen += sprintf(lBuf + lBufLen,"\nPrinting Tcap Session Map"
                                    "\nNo. of Sessions         :%d",
                                        m_tcapSessionMap.size());

  map<int,INGwTcapSession*>:: iterator tcSessionIter;

  if(m_tcapSessionMap.size() == 0) {
    lBufLen += sprintf(lBuf + lBufLen,"\nSession map is empty\n");
    INGwTcapMsgLogger::getInstance().dumpString(lBuf);
  } 

  for(tcSessionIter = m_tcapSessionMap.begin(); tcSessionIter != 
      m_tcapSessionMap.end(); tcSessionIter++) 
  {//outer for
    INGwTcapSession* tcSession = tcSessionIter->second;
    INGwFtPktTcapMsg *lpFtPktTcapMsg = NULL;
    if(NULL !=(lpFtPktTcapMsg = tcSession->getInitialInfo())) {
      lBufLen += sprintf(lBuf + lBufLen,"\n<%s>",
                         lpFtPktTcapMsg->toLog(false).c_str());
    }

    lBufLen += sprintf(lBuf + lBufLen,
    "\n*******************Session dlg-Id:%d*******************",
    tcSessionIter->first);

    if(!tcSession) {
      lBufLen += sprintf(lBuf + lBufLen,"\nTcapSession entry is NULL");
      continue;
    }
   
    lBufLen += sprintf(lBuf + lBufLen,"\n\tPrinting InBoundMsgMap");

    map<int,INGwTcapFtMsg*>::iterator iterIn;
    for(iterIn = tcSession->m_InbdlgMsgMap.begin(); 
        iterIn != tcSession->m_InbdlgMsgMap.end();
        iterIn++)
    {//inb for
      lBufLen += sprintf(lBuf + lBufLen,"\n\t\tSeqNum : %d ",iterIn->first);

      if(0 != (iterIn->second))
      {
        if(iterIn->second->m_buf){
          lBufLen += sprintf(lBuf + lBufLen,": INGwTcapFtMsg::m_Buf Non Null");
        }
        else{
          lBufLen += sprintf(lBuf + lBufLen,": INGwTcapFtMsg::m_Buf Null");
        }
      }
      else{
        lBufLen += sprintf(lBuf + lBufLen,": INGwTcapFtMsg* NULL");
      }
    } 

    lBufLen += sprintf(lBuf + lBufLen,"\n\tPrinting OutBoundMsgMap");
//mapchange
    bool isAccumulated = false;
    INGwTcOutMsgContainer *lpContainer = NULL;
    INGwTcapWorkUnitMsg *lpWorkUnitMsg;
    t_tcapMsgBuffer     *ltcapMsgInfo;

    map<int, INGwTcOutMsgContainer*>::iterator iterOut;
    for(iterOut = tcSession->m_outboundMsgMap.begin();
        iterOut!= tcSession->m_outboundMsgMap.end(); iterOut++) { //outb 
      lBufLen += sprintf(lBuf + lBufLen,"\n\t\tSeqNum : %d ",iterOut->first); 

      lpContainer = iterOut->second;
      if(lpContainer) {
        lpWorkUnitMsg = lpContainer->mpWorkUnitMsg;
        ltcapMsgInfo  = lpContainer->mtcapMsgInfo;

        if(NULL != lpWorkUnitMsg)
        {
          lBufLen += sprintf(lBuf + lBufLen,
          ": WorkUnit exists <R MODE>");

        }
        else if(NULL != ltcapMsgInfo){
          lBufLen += sprintf(lBuf + lBufLen,
                                  ": t_tcapMsgBuffer * exists <BR MODE>");
        }
        else{
          lBufLen += sprintf(lBuf + lBufLen,": Both Null");
        }
      } else{
        lBufLen += sprintf(lBuf + lBufLen,": INGwTcOutMsgContainer * Null");
      }
    }
    INGwTcapMsgLogger::getInstance().dumpString(lBuf);
    lBufLen = 0;
    lBuf[0]=0;
  }//outer for

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out dumpTcapSessionMap");
}

void
INGwTcapFtHandler::setMsgFtFlag(int p_disableMsgFt){
  logger.logINGwMsg(false,TRACE_FLAG,0,"In setMsgFtFlag p_disableMsgFt<%d>",
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

  logger.logINGwMsg(false,TRACE_FLAG,0,"Out setMsgFtFlag mDisableMsgFtFlag<%d>",
                    mDisableMsgFtFlag);
}

int
INGwTcapFtHandler::getMsgFtFlag(){
  return mDisableMsgFtFlag;
}

void
INGwTcapFtHandler::initActiveDialogueMap() {
logger.logINGwMsg(false, ALWAYS_FLAG, 0,"In initActiveDialogueMap");

  mpTcapIncMsgHandler->getLowDlgId(&mLowDlgId);
  mpTcapIncMsgHandler->getTotalDlg(&mTotalDlg);
  logger.logINGwMsg(false, TRACE_FLAG, 0,"g_maxNmbOutDlg <%d>",g_maxNmbOutDlg);

  mActiveReplayCallMap = new (nothrow) U8[mTotalDlg + g_maxNmbOutDlg];

  if(NULL == mActiveReplayCallMap)
  {
    logger.logINGwMsg(false,TRACE_FLAG,0,
           "INGwTcapFtHandler::initActiveDialogueMap, dyn mem alloc failure");
    printf("INGwTcapFtHandler::initActiveDialogueMap, dyn mem alloc failure");
    fflush(stdout);
    //perform shutdown if possible
    exit(1);
  }
  memset(mActiveReplayCallMap,0,mTotalDlg + g_maxNmbOutDlg);

  pthread_rwlock_wrlock(&gMapRWLock);
  g_activeReplayCallMap = mActiveReplayCallMap;
  pthread_rwlock_unlock(&gMapRWLock);
  loDlgId = mLowDlgId;
logger.logINGwMsg(false, ALWAYS_FLAG, 0,"Out initActiveDialogueMap "
                  "mLowDlgId<%d>, mTotalDlg<%d>",mLowDlgId,mTotalDlg);
}

void
INGwTcapFtHandler::clearReplayMessageStore(bool pbTakeLockbool,
                                           bool clearFtMsgStoreOnly) 
{
//AUD YOGESH
  static int lsiClearingCount = 0;

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In clearReplayMessageStore <%d>",
                                                        ++lsiClearingCount);

  map<short,bool>::iterator sapStatusIter;
  if(pbTakeLockbool) {
    LOCKSESSION();
  }
  else {
          logger.logINGwMsg(false,ALWAYS_FLAG,0,"clearReplayMessageStore"
          " not locking the session map");
  }
  int retVal = G_SUCCESS;
  g_TransitObj	lTransitObj;

  int count = 0;  
  map <int, INGwTcapSession*>::iterator tcSessionIter; 
  map<int,INGwTcapFtMsg*>::iterator  inbIter; 

  map<int, INGwTcOutMsgContainer*>::iterator  outbIter; 
  INGwTcapFtMsg* lMessageBuffer = NULL;
  INGwTcapWorkUnitMsg* lpTcWorkUnit = NULL;
  int liSeqNum = -1;
  U32 lidlgId = 0;
  int retValSendTcReq;
  INGwFtPktTcapMsg* lTuaiInfo = NULL;
  for(tcSessionIter = m_tcapSessionMap.begin();
      tcSessionIter!= m_tcapSessionMap.end()  ; tcSessionIter++)
  {
    INGwTcapSession* lpTcSession = tcSessionIter->second;
    lidlgId =  tcSessionIter->first;
    gUpdateReplayCallCnt(lidlgId);

    if(NULL != (lTuaiInfo = lpTcSession->getInitialInfo())) {
      delete lTuaiInfo;
      lpTcSession->setInitialInfo(NULL);
    }

    logger.logINGwMsg(false,TRACE_FLAG,0,
    "clearReplayMessageStore dlgId <%d>",lidlgId); 

    INGwTcSessionMinorState lMinState = lpTcSession->getMinorState();
      for(inbIter = lpTcSession->m_InbdlgMsgMap.begin(); 
          inbIter!= lpTcSession->m_InbdlgMsgMap.end(); inbIter++)
      {
        logger.logINGwMsg(false,TRACE_FLAG,0,
        "clearReplayMessageStore inbound message<%d>",lidlgId); 

        liSeqNum = inbIter->first;
        lMessageBuffer = inbIter->second;
        if(lMessageBuffer->isValid())
        {
            delete [] (lMessageBuffer->m_buf); lMessageBuffer->m_buf=0;
            lMessageBuffer->m_bufLen = 0;
            delete lMessageBuffer;
            lMessageBuffer= NULL;
        }
      }

      lpTcSession->m_InbdlgMsgMap.erase(lpTcSession->m_InbdlgMsgMap.begin(),
                                        lpTcSession->m_InbdlgMsgMap.end());   

      //clearFtMsgStoreOnly
      t_tcapMsgBuffer* ltcapMsgInfo;
      INGwTcOutMsgContainer *lpContainer = NULL;

      for(outbIter = lpTcSession->m_outboundMsgMap.begin(); 
          outbIter != lpTcSession->m_outboundMsgMap.end(); outbIter++)
      {
        lpTcWorkUnit = NULL;
        ltcapMsgInfo = NULL;
        liSeqNum = outbIter->first;
        lpContainer = outbIter->second;
        if(NULL != lpContainer) 
        {
          logger.logINGwMsg(false,TRACE_FLAG,0,"remft clearReplayMessageStore "
                            "cleaning mpWorkUnitMsg");

          if(NULL != lpContainer->mpWorkUnitMsg)
          {
            lpTcWorkUnit = lpContainer->mpWorkUnitMsg;

            if(NULL != lpTcWorkUnit) {
              INGwTcapProvider::getInstance().getAinSilTxRef().
                                              releaseWorkUnit(lpTcWorkUnit);

              delete lpTcWorkUnit->m_tcapMsg; lpTcWorkUnit->m_tcapMsg = 0;
              delete lpTcWorkUnit; lpTcWorkUnit = 0;
            }
          }
          else if(NULL != lpContainer->mtcapMsgInfo)
          {
            ltcapMsgInfo = lpContainer->mtcapMsgInfo;
            //yogesh
            if(ltcapMsgInfo) {
              if(NULL != ltcapMsgInfo->m_buf){
                delete [] ltcapMsgInfo->m_buf; ltcapMsgInfo->m_buf =NULL;
              }
              delete ltcapMsgInfo; ltcapMsgInfo = NULL;
            }
          }
          else {
            logger.logINGwMsg(false,ALWAYS_FLAG,0,
                                         "tcapInfo Null everything Ok");
          }
 
          delete lpContainer; lpContainer = NULL;
        }
      }

    lpTcSession->m_outboundMsgMap.erase(lpTcSession->m_outboundMsgMap.begin(),
                                  lpTcSession->m_outboundMsgMap.end());

    delete lpTcSession; lpTcSession = 0;
  }

  m_tcapSessionMap.erase(m_tcapSessionMap.begin(),m_tcapSessionMap.end());
  
  char* lpcBuf = ((INGW_CLEAR_R_MODE == clearFtMsgStoreOnly)? "*R MODE*": "*BR MODE*");

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out clearReplayMessageStore <%s>",
                    lpcBuf);
}

int 
INGwTcapFtHandler::
addInitialInfoForBackReplication(INGwFtPktTcapMsg& pInitInfo) {
/*
  logger.logINGwMsg(false,TRACE_FLAG,0,"In addInitialInfoForBackReplication");
     map<int, INGwFtPktTcapMsg*>::iterator lInitInfoMapIter = 
                        m_initialInfoMap.find(pInitInfo.m_stackDialogue);

  if(m_initialInfoMap.end() != lInitInfoMapIter) {
      delete lInitInfoMapIter->second;
      lInitInfoMapIter->second = &pInitInfo;
      logger.logINGwMsg(false,ERROR_FLAG,0,
                        "TcapUserInformationForSerialization deleting old "
                        "session");

      map<int, INGwTcapSession*>::lInitInfoMapIter it = 
                        m_tcapSessionMap.find(pInitInfo.m_stackDialogue);  

      INGwTcapSession *lpTcSession; 
      if(m_tcapSessionMap.end() != it) {
      //clean after testing
       
      INGwFtPktTermTcapSession p_termSession;
      p_termSession.initialize(stackDialogue,0,0,false);
      handleTerminateTcapSession(p_termSession);  
	    logger.logINGwMsg(false, ERROR_FLAG, 0,
                        "TcapUserInformationForSerialization Deleting hanging"
                        " Session<%d>", stackDialogue);
    }
  } else {
    m_initialInfoMap[stackDialogue] = &pInitInfo;
  } 
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out addInitialInfoForBackReplication");
*/
 return -1;
}
/*
 while doing synch up cases:
   case 1: Begin is replicated and peer goes down, 
           peer comes up and we want to replicate TC-CONT(Accumulated)
 
   case 2: Whole call is needed to be accumulated
 
   case 3: Send AuditDialogues message

   case 4: Begin is replicated and peer goes down,
           Dialogue is in stable state no messages are accumulated,
           peer comes up and synchsUp takes care of replication
           for this particular call on Sby as nothing is in store.
  
           a) if active goes down after any cont is received.
              handled.

           b) if active goes down before any message comes.
 *
*/


/*
******START OF SYNCH MSG******
loop over session map
do
  check for the session in Accumulation Map
  if TUAI is not null packetize it
  else ask TUAI  map to provide the same and then encode it
  encode buffers
  reset the dialogueBit
done  

  get the list of high bits from bitArray
  get from TUAI and update peer
******END OF SYNCH MSG******
*/

int 
INGwTcapFtHandler::sendMsgStoreSynchUpMsg() {
  int retVal = G_SUCCESS;
  logger.logINGwMsg(false,TRACE_FLAG,0,"In sendMsgStoreSynchUpMsg");

  U8* rawBuffer; int buflen = 0;
  char lBuf[256] = {0,}; 

  g_getCurrentTime(lBuf);
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"sendMsgStoreSynchUpMsg before "
  "serialization <%s>",lBuf);

  serializeSessionMap(&rawBuffer, buflen);

  g_getCurrentTime(lBuf);
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"sendMsgStoreSynchUpMsg after "
  "serialization <%s>",lBuf);

  int selfId   = INGwIfrPrParamRepository::getInstance().getSelfId();
  int peerId   = INGwIfrPrParamRepository::getInstance().getPeerId();  
  INGwFtPktSynchUpMsg   lPktSynchUpMsg;
  
  lPktSynchUpMsg.initialize(selfId, peerId, rawBuffer, buflen,
                                mPeerFaultCnt);
#ifdef MSG_FT_TEST
  if(mbCondWaitBR) {

    logger.logINGwMsg(false,ALWAYS_FLAG,0,"serializeSessionMap Waiting for"
    " signal from telnet interface");

    pthread_cond_wait(&gUnblockBR,&gCvLockBR);
    
    
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"serializeSessionMap signalled");
  }
#endif

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"BR MSG SIZE <%f> KB",
                    (float)buflen/(1024.0F));

  char *lpcEnableBR = getenv("INGW_BK_REP_SIZE"); //specify in KB
  int liBkRepSize = 0;
  if(NULL != lpcEnableBR) 
  {
    liBkRepSize = atoi(lpcEnableBR) * 1024;
  }
  else {
    liBkRepSize = 0x100000;
  }

  if(buflen >= liBkRepSize) {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"STOPPING BR <%d> KB",
       ((float)buflen)/1024);
  }
  else 
  {
    INGwIfrMgrManager::getInstance().sendMsgToINGW(&lPktSynchUpMsg); 
  }
  
  if(NULL != rawBuffer) {
    delete [] rawBuffer; rawBuffer = NULL;
  }

 

  //if(NULL != lpcAllowClearingRemFt) {

  //  if(1 == atoi(lpcAllowClearingRemFt)) {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
      "sendMsgStoreSynchUpMsg clearing messsage store");
      clearReplayMessageStore(false, INGW_CLEAR_BR_MODE);
  // }
  //}

  logger.logINGwMsg(false, TRACE_FLAG,0,"Out sendMsgStoreSynchUpMsg");
  return retVal;
}

void
INGwTcapFtHandler::handlePeerINCFailure() {
  logger.logINGwMsg(false, ALWAYS_FLAG,0, "In handlePeerINCFailure");

  int liRole = INGwTcapProvider::getInstance().myRole();

  logger.logINGwMsg(false, ALWAYS_FLAG, 0,
    "+++++++++++++++++Received Peer Failure Msg+++++++++++++++++\n"
    "+++++++++++++++++Role <%s> +++++++++++++++++",
    ((TCAP_SECONDARY==liRole)?"Standby":"Active"));

  mPeerFaultCnt++;

  //if(TCAP_SECONDARY != liRole) {
  //  //do nothing.. calls will accumulate automatically
  //  //raise an alarm: Discuss
  //}

  //post this from role change api 
  dumpTcapSessionMap("++++Received Peer Failure Msg++++"); 

  handleFtTakeOver();

  pthread_rwlock_wrlock(&gMsgStoreFlgLock);
    gIsIsMsgStoreEmpty = true;
  pthread_rwlock_unlock(&gMsgStoreFlgLock);

  //to avoid deadlock situation
  //while(pthread_cond_destroy(&gUnblock) == EBUSY) {
  pthread_cond_broadcast(&gUnblock); 
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Signalled after handleFtTakeOver");

    //relinquish the CPU 
#if defined(sun) || defined(__sun)    
  sched_yield();
#else
  pthread_yield(NULL);
#endif
    //} 
  logger.logINGwMsg(false, ALWAYS_FLAG,0,"Out handlePeerINCFailure");
}


void 
INGwTcapFtHandler::handlePeerConnectedMsg(){
  logger.logINGwMsg(false, ALWAYS_FLAG,0,"In handlePeerConnectedMsg");

  LOCKSESSION();

    sendMsgStoreSynchUpMsg();

  logger.logINGwMsg(false, ALWAYS_FLAG,0,"Out handlePeerConnectedMsg");
}

int
INGwTcapFtHandler::serializeSessionMap(U8** pBuf, int &pBufLen)
{
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "In serializeSessionMap");

  if(mDisableMsgFtFlag) {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out serializeSessionMap SKIPPING");
     return G_SUCCESS;
  }

  //reserving 1024 bytes for each dialogue 
  INGwIfrUtlBitArray* lpDlgBitArray = TcapMessage::cloneDialogueStateMap();
  int liSetBitsCnt = lpDlgBitArray->getSetBitsCountLookUp();
  int liMapSize = m_tcapSessionMap.size();

  int liDormantSize = 
             ((liSetBitsCnt - liMapSize) >0?(liSetBitsCnt - liMapSize):0);
                      
  int lReservedLen  = ((liMapSize+1) << 10) + ((liDormantSize)<<8); 

  logger.logINGwMsg(false, ALWAYS_FLAG, 0,
  "serializeSessionMap lReservedLen<%d>", lReservedLen, liMapSize, 
  liDormantSize);

  U8*  lBuf = new (nothrow) U8[lReservedLen];

  if(NULL == lBuf) {
    logger.logINGwMsg(false,ERROR_FLAG,0,"Out serializeSessionMap"
    "A-INC cannot replicate accumulated Calls: Dynamic memory allocation "
    "failed");

    logger.logINGwMsg(false,TRACE_FLAG,0,"Out serializeSessionMap ERROR");
    return -1;
  }
   
  pBufLen =  0;
  int value =  0;
  int lSeqNum = -1;
  int lDlgId  = -1;
  INGwFtPktTcapMsg* lInitialInfo = NULL;
  INGwTcapSession* lpTcSession = NULL;
  map<int,INGwTcapSession*>:: iterator   iter;
  map<int,INGwTcapFtMsg*>  :: iterator   lInbMapIter;
  map<int, INGwTcOutMsgContainer*>          :: iterator   lOutbMapIter;


  value = htons(mPeerFaultCnt);

  logger.logINGwMsg(false,ALWAYS_FLAG,0, "serializeSessionMap "
                                    "mPeerFaultCnt %d",mPeerFaultCnt);

  //encoding peer fault count
  if((lReservedLen - pBufLen) < SIZE_OF_INT)
  {
    
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
    "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
    " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

    doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
    lReservedLen += MEM_CHUNK_SIZE;

    logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
    "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
    " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
  }

  //encode mPeerFaultCnt
  memcpy(lBuf + pBufLen, &value, SIZE_OF_INT);
  pBufLen += SIZE_OF_INT;


  int sizeOfMap = m_tcapSessionMap.size();

  //encode size of session
  if((lReservedLen - pBufLen) < SIZE_OF_INT)
  {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
    "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
    " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

    doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
    lReservedLen += MEM_CHUNK_SIZE;

    logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
    "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
    " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
  }

  //encode size of session
  memcpy(lBuf + pBufLen, &sizeOfMap, SIZE_OF_INT);
  pBufLen += SIZE_OF_INT;

  logger.logINGwMsg(false,TRACE_FLAG,0,"serializeSessionMap sizeOfMap <%d>",
                                        sizeOfMap);

  //start encoding the session map
  for(iter = m_tcapSessionMap.begin(); iter != m_tcapSessionMap.end(); iter++) 
  {

    lDlgId = iter->first;

    //reset the bit state so that we can identify dormant dialogues later
    lpDlgBitArray->resetBitState(lDlgId);

    //check if the session is valid
    if(NULL != (lpTcSession = iter->second)) {
      lpTcSession = iter->second;
    }
    else {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,"Cannot find FT"
                        " session dlgId<%d>",lDlgId);
      continue;
    }


    //check if the initial info is present in the session or not
    //will never get into this condition
    if(NULL == (lInitialInfo = lpTcSession->getInitialInfo())) 
    {
      logger.logINGwMsg(false,TRACE_FLAG,0,"serializeSessionMap "
      "getInitialInfo is NULL trying to extract");

      TcapUserAddressInformation  tuai;
      TcapMessage::getUserAddressByDlgId(lDlgId,tuai);
      if(!tuai.isValid()) {
        logger.logINGwMsg(false,ALWAYS_FLAG,0,"serializeSessionMap "
        "Dialogue ID <%d> is not active",lDlgId);
        continue; 
      } 

      logger.logINGwMsg(false,ALWAYS_FLAG,0,"serializeSessionMap valid tuai"
      " found <%d>",lDlgId);

      if((lReservedLen - pBufLen) < 256) 
      {
       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <256>"
        " oldAdderss<%x>", lReservedLen, pBufLen ,lBuf);

        doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
        lReservedLen += MEM_CHUNK_SIZE;

       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <256>"
        " newAdderss<%x>", lReservedLen, pBufLen, lBuf);
      }

      int temp = pBufLen++;
      tuai.serialize(lBuf, pBufLen);

      //long form encoding is not required here
      lBuf[temp] = (pBufLen - temp);

      logger.logINGwMsg(false,ALWAYS_FLAG,0,"serializeSessionMap "
                              "tuai length extracted <%d>",pBufLen - temp);
    }
    else
    {
      //must always get into this block, we are encoding TUAI here
      if((lReservedLen - pBufLen) < 256) 
      {
       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <256>"
        " oldAdderss<%x>", lReservedLen, pBufLen, lBuf);

        doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
        lReservedLen += MEM_CHUNK_SIZE;

       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <256>"
        " newAdderss<%x>", lReservedLen, pBufLen, lBuf);
      }

      int temp = pBufLen++;
      lInitialInfo->serialize(lBuf, pBufLen);
      lBuf[temp] = (pBufLen - temp);

      logger.logINGwMsg(false,TRACE_FLAG,0,"serializeSessionMap "
                        "tuai length In Session <%d>",pBufLen - temp);
    }
    
    //serialize outbound map
    int integer = htons(TAG_OUTB_MAP);
    if((lReservedLen - pBufLen) < SIZE_OF_INT) 
    {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
      "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
      " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

      doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
      lReservedLen += MEM_CHUNK_SIZE;

      logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
      "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
      " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
    }

    //encoding the tag TAG_OUTB_MAP
    memcpy(lBuf + pBufLen,&integer,SIZE_OF_INT);
    pBufLen    += SIZE_OF_INT;
    integer     = lpTcSession->m_outboundMsgMap.size();
    integer     = htons(integer);
    
    logger.logINGwMsg(false,TRACE_FLAG,0,"serializeSessionMap "
                   "outboundMsgMap dlgId<%d> size<%d>", lDlgId, integer);

    if((lReservedLen - pBufLen) < SIZE_OF_INT) 
    {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
      "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
      " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

      doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
      lReservedLen += MEM_CHUNK_SIZE;

      logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
      "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
      " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
    }

    //encoding out bound messgae map size
    memcpy(lBuf + pBufLen, &integer,SIZE_OF_INT);
    pBufLen    += SIZE_OF_INT;
   
    //bugfix1
    if(0 == integer) {
      //encode lastoutbound sequence number here, take care while decoding
      int liOutBoundSeqNum = 
                        mpTcapIncMsgHandler->getLastOutBoundSeqNum(lDlgId);

      liOutBoundSeqNum = htons(liOutBoundSeqNum);

      if((lReservedLen - pBufLen) < SIZE_OF_INT) 
      {
        logger.logINGwMsg(false,ALWAYS_FLAG,0,
        "remft before serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

        doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
        lReservedLen += MEM_CHUNK_SIZE;

        logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
      }
      
      logger.logINGwMsg(false,TRACE_FLAG,0,"bugfix 1 liOutBoundSeqNum<%d>",
                                                      liOutBoundSeqNum);

      memcpy(lBuf + pBufLen, &liOutBoundSeqNum,SIZE_OF_INT);
      pBufLen    += SIZE_OF_INT;
    }

    lOutbMapIter =  lpTcSession->m_outboundMsgMap.begin();
    t_tcapMsgBuffer* ltcapMsgInfo = NULL;

    INGwTcOutMsgContainer *lpContainer = NULL;

    for( ;lOutbMapIter != lpTcSession->m_outboundMsgMap.end();lOutbMapIter++) 
    {
      lpContainer = lOutbMapIter->second;
      ltcapMsgInfo = lpContainer->mtcapMsgInfo;
      lSeqNum = lOutbMapIter->first;
      lSeqNum = htons(lSeqNum);

      logger.logINGwMsg(false,TRACE_FLAG,0,"serializeSessionMap "
      "seqNum <%d> dlgId<%d> buflen<%d>",lSeqNum, lDlgId, pBufLen);

      if((lReservedLen - pBufLen) < SIZE_OF_INT) 
      {
       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

        doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
        lReservedLen += MEM_CHUNK_SIZE;

       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
      }

      memcpy(lBuf+pBufLen, &lSeqNum, SIZE_OF_INT);
      pBufLen += SIZE_OF_INT;

      integer = ltcapMsgInfo->m_bufLen;
      integer = htons(integer);

      if((lReservedLen - pBufLen) < SIZE_OF_INT) 
      {
       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

        doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
        lReservedLen += MEM_CHUNK_SIZE;

       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
      }

      logger.logINGwMsg(false,TRACE_FLAG,0,"serializeSessionMap bufLen<%d>",
                        integer);

      memcpy(lBuf+pBufLen, &integer, SIZE_OF_INT);
      pBufLen += SIZE_OF_INT;

      if((lReservedLen - pBufLen) < ltcapMsgInfo->m_bufLen) 
      {
       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " oldAdderss<%x>", lReservedLen, pBufLen, ltcapMsgInfo->m_bufLen, lBuf);

        doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
        lReservedLen += MEM_CHUNK_SIZE;

       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " newAdderss<%x>", lReservedLen, pBufLen, ltcapMsgInfo->m_bufLen, lBuf);
      }
      memcpy(lBuf + pBufLen, ltcapMsgInfo->m_buf, ltcapMsgInfo->m_bufLen);
      pBufLen += ltcapMsgInfo->m_bufLen;

      logger.logINGwMsg(false,TRACE_FLAG,0,"serializeSessionMap bufLen<%d>",
      pBufLen);
    }

    //serialize inbound map
    integer = htons(TAG_INB_MAP);

    if((lReservedLen - pBufLen) < SIZE_OF_INT) 
    {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
      "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
      " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

      doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
      lReservedLen += MEM_CHUNK_SIZE;

      logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
      "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
      " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
    }

    //encoding tag TAG_INB_MAP
    memcpy(lBuf+pBufLen,&integer,SIZE_OF_INT);
    pBufLen    += SIZE_OF_INT;
    
    integer     = lpTcSession->m_InbdlgMsgMap.size();
    integer     = htons(integer);
    if((lReservedLen - pBufLen) < SIZE_OF_INT) 
    {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
      "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
      " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

      doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
      lReservedLen += MEM_CHUNK_SIZE;

      logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
      "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
      " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
    }

    //encoding message map size
    memcpy(lBuf + pBufLen,&integer,SIZE_OF_INT);
    pBufLen    += SIZE_OF_INT;

    logger.logINGwMsg(false,TRACE_FLAG,0,"serializeSessionMap serlBuflen<%d>"
    " m_InbdlgMsgMap size<%d>", pBufLen, integer);
    
    //bugfix1
    if(0 == integer) 
    {
      //encode last inbound message seq num, take care while decoding
          int liInBoundSeqNum = 
                        mpTcapIncMsgHandler->getLastInBoundSeqNum(lDlgId);

      liInBoundSeqNum = htons(liInBoundSeqNum);

      if((lReservedLen - pBufLen) < SIZE_OF_INT) 
      {
        logger.logINGwMsg(false,ALWAYS_FLAG,0,
        "remft before serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

        doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
        lReservedLen += MEM_CHUNK_SIZE;

        logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
      }
 
      memcpy(lBuf + pBufLen, &liInBoundSeqNum,SIZE_OF_INT);
      pBufLen    += SIZE_OF_INT;

      logger.logINGwMsg(false,TRACE_FLAG,0,"bugfix 1 liInBoundSeqNum<%d>",
                                                      liInBoundSeqNum);
    }

    lInbMapIter =  lpTcSession->m_InbdlgMsgMap.begin();
    ltcapMsgInfo = NULL;

    for( ;lInbMapIter != lpTcSession->m_InbdlgMsgMap.end();lInbMapIter++) 
    {
      INGwTcapFtMsg* ltcapMsgInfoInb = lInbMapIter->second;
      lSeqNum = lInbMapIter->first;
      lSeqNum = htons(lSeqNum);

      integer = ltcapMsgInfoInb->m_bufLen;
      integer = htons(integer);

      logger.logINGwMsg(false,TRACE_FLAG,0,"serializeSessionMap "
      "lSeqNum <%d> m_bufLen <%d> pBufLen<%d>",lSeqNum, 
       ltcapMsgInfoInb->m_bufLen, pBufLen);

      if((lReservedLen - pBufLen) < SIZE_OF_INT) 
      {
       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

        doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
        lReservedLen += MEM_CHUNK_SIZE;

       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
      }

      memcpy(lBuf+pBufLen, &lSeqNum, SIZE_OF_INT);
      pBufLen += SIZE_OF_INT;

      if((lReservedLen - pBufLen) < SIZE_OF_INT) 
      {
       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

        doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
        lReservedLen += MEM_CHUNK_SIZE;

       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
      }

      memcpy(lBuf+pBufLen, &integer, SIZE_OF_INT);
      pBufLen += SIZE_OF_INT;

      if((lReservedLen - pBufLen) < ltcapMsgInfoInb->m_bufLen) 
      {
       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " oldAdderss<%x>", lReservedLen, pBufLen, ltcapMsgInfoInb->m_bufLen, 
        lBuf);

        doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
        lReservedLen += MEM_CHUNK_SIZE;

       logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
        "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
        " newAdderss<%x>", lReservedLen, pBufLen, ltcapMsgInfoInb->m_bufLen,
        lBuf);
      }

      memcpy(lBuf + pBufLen, ltcapMsgInfoInb->m_buf, ltcapMsgInfoInb->m_bufLen);
      pBufLen += ltcapMsgInfoInb->m_bufLen;
      logger.logINGwMsg(false,TRACE_FLAG,0,"serializeSessionMap pBufLen<%d>",
                        pBufLen);
    }
  }

  vector<int> lvector = lpDlgBitArray->getAllSetBitIndex();
  int lDormantDlgCount = lvector.size();
  liSetBitsCnt = lpDlgBitArray->getSetBitsCountLookUp();

  logger.logINGwMsg(false,ALWAYS_FLAG, 0,"serializeSessionMap "
                    "DormantDlgCount <%d> setBits<%d>", lDormantDlgCount, 
                     liSetBitsCnt);

  int val = TAG_TUAI_LIST; 
  if((lReservedLen - pBufLen) < SIZE_OF_INT) 
  {

    logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
    "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
    " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

    doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
    lReservedLen += MEM_CHUNK_SIZE;

    logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
    "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
    " newAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);
  }

  val = htons(val); 
  //encode TAG_TUAI_LIST tag
  memcpy(lBuf + pBufLen, &val, SIZE_OF_INT);
  pBufLen += SIZE_OF_INT;
  
  vector<TcapUserAddressInformation> ltuaiVector; 
  if(0 != lDormantDlgCount) {
    TcapMessage::getUserAddressByDlgId(lvector, ltuaiVector);
    lDormantDlgCount = ltuaiVector.size();
    logger.logINGwMsg(false,ALWAYS_FLAG, 0,"serializeSessionMap "
                      "DormantDlgCount <%d>", lDormantDlgCount);
  }

  if((lReservedLen - pBufLen) < ((lDormantDlgCount<<8) + SIZE_OF_INT)) 
  {
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft before serializeSessionMap "
    "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
    " oldAdderss<%x>", lReservedLen, pBufLen, 
    ((lDormantDlgCount<<8) + SIZE_OF_INT), lBuf);

    doReallocation(&lBuf, lReservedLen, ((lDormantDlgCount<<8) + SIZE_OF_INT));
    lReservedLen += ((lDormantDlgCount<<8) + SIZE_OF_INT);

    logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
    "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
    " newAdderss<%x>", lReservedLen, pBufLen, 
    ((lDormantDlgCount<<8) + SIZE_OF_INT), lBuf);
  }

  val = htons(lDormantDlgCount);
  memcpy(lBuf + pBufLen, &val, SIZE_OF_INT);

  //saving this buffer to modify this count if needed further
  int liTuaiLenIndexInBuff = pBufLen;

  pBufLen += SIZE_OF_INT;

  logger.logINGwMsg(false,ALWAYS_FLAG, 0,"serializeSessionMap "
  "before serialization of  ltuaiVector BufferLen<%d> lReservedLen<%d> "
  "lDormantDlgCount<%d>", pBufLen, lReservedLen, lDormantDlgCount);

  int liLen = 0;
  int liInvalidCount = 0;
  for(int i=0; i < lDormantDlgCount; i++) 
  {
    //Yogesh_invalid
    if(!ltuaiVector[i].isValid())
    {
      ++liInvalidCount;

      logger.logINGwMsg(false,ALWAYS_FLAG,0,"serializeSessionMap in loop i<%d>"
      " pBufLen<%d> tuai Invalid", i, pBufLen);
      continue;
    }
    liLen = pBufLen++;

    logger.logINGwMsg(false,ALWAYS_FLAG,0,"serializeSessionMap in loop i<%d>"
    " pBufLen<%d>", i, pBufLen);

    ltuaiVector[i].serialize(lBuf, pBufLen);
    //lBuf[liLen] = pBufLen - liLen;

    //bugfix1 encode outbound then inbound seq num, take care in decode
  
    if((lReservedLen - pBufLen) < 2*SIZE_OF_INT) 
    {
      logger.logINGwMsg(false,ALWAYS_FLAG,0,
      "remft before serializeSessionMap "
      "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
      " oldAdderss<%x>", lReservedLen, pBufLen, SIZE_OF_INT, lBuf);

      doReallocation(&lBuf, lReservedLen, MEM_CHUNK_SIZE);
      lReservedLen += MEM_CHUNK_SIZE;

      logger.logINGwMsg(false,ALWAYS_FLAG,0,"remft after serializeSessionMap "
      "doReallocation reserved <%d> pBufLen <%d> toEncodeBufLen <%d>"
      " newAdderss<%x>", lReservedLen, pBufLen, 2*SIZE_OF_INT, lBuf);
    }  
    
    int liOutBoundSeqNum =
                       mpTcapIncMsgHandler->
                       getLastOutBoundSeqNum (ltuaiVector[i].did);

    liOutBoundSeqNum = htons(liOutBoundSeqNum);  

    int liInBoundSeqNum =
                       mpTcapIncMsgHandler->
                       getLastInBoundSeqNum(ltuaiVector[i].did);

    liInBoundSeqNum = htons(liInBoundSeqNum); 

    logger.logINGwMsg(false,TRACE_FLAG,0,"Setting Sequence in <%d> out<%d>"
                     " dlg-Id <%d>",
                     liInBoundSeqNum, liOutBoundSeqNum, ltuaiVector[i].did);
     
    memcpy(lBuf + pBufLen, &liOutBoundSeqNum,SIZE_OF_INT);
    pBufLen    += SIZE_OF_INT;

    memcpy(lBuf + pBufLen, &liInBoundSeqNum,SIZE_OF_INT);
    pBufLen    += SIZE_OF_INT;

    lBuf[liLen] = pBufLen - liLen;
  }

  if(0 != liInvalidCount){

    logger.logINGwMsg(false, ALWAYS_FLAG, 0,
           "PrevCount<0x%02x> lDormantDlgCount<%d> liInvalidCount<%d>",
           (lBuf +liTuaiLenIndexInBuff), lDormantDlgCount, liInvalidCount);

    int liDormantCnt = lDormantDlgCount - liInvalidCount;
    memcpy(lBuf +liTuaiLenIndexInBuff , &liDormantCnt, SIZE_OF_INT);
    //no need to increment length we are overwriting previously encoded buff
  }
#ifdef MSG_FT_TEST  

  char* remftBuf = new char[pBufLen<<5];
  remftBuf[0] = 0;
  int remftBufLen = 0;
  for(int i=0; i < pBufLen; i++) { //remft
    if(!(i & 15)) remftBufLen += sprintf(remftBuf + remftBufLen,"%s","\n\t");
    remftBufLen += sprintf(remftBuf + remftBufLen," %02X", lBuf[i]);//remft
  }                                //remft

  logger.logINGwMsg(false,ALWAYS_FLAG, 0,"serializeSessionMap "
  "after serialization of  ltuaiVector buflen<%d> vectorSize<%d>\n buffer<%s>",
  pBufLen, lDormantDlgCount, remftBuf);

  delete [] remftBuf;

#endif/*MSG_FT_TEST*/  
  /**
  now lpDlgBitArray the "clone" will remain allocated throughout
  delete lpDlgBitArray; lpDlgBitArray = 0;
  */
 
  *pBuf = lBuf;
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out serializeSessionMap");
  return pBufLen;
}

int
INGwTcapFtHandler::handleSynchUpMsgFromPeer(INGwFtPktSynchUpMsg &pSynchUpMsg)
{
  logger.logINGwMsg(false,TRACE_FLAG,0,"In handleSynchUpMsgFromPeer");

  LOCKSESSION();

  mPeerFaultCnt = pSynchUpMsg.getFaultCount();

  char lBuf[256] = {0,}; 

  g_getCurrentTime(lBuf);
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleSynchUpMsgFromPeer start<%s>",
                    lBuf);

  int lOffset = 0;
  U8* lpcBuf = pSynchUpMsg.getSerializedBuffer();

  logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer "
                    "getSerializedBuffer size <%d> sizeof<%d>",
                     pSynchUpMsg.getBufLen(),sizeof(lpcBuf));

  int liSizeOfSessionMap = 0;
  memcpy(&liSizeOfSessionMap, lpcBuf + lOffset, SIZE_OF_INT);
  liSizeOfSessionMap = ntohs(liSizeOfSessionMap);
  lOffset +=  SIZE_OF_INT;

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleSynchUpMsgFromPeer "
  "remft liSizeOfSessionMap <%d>", liSizeOfSessionMap);

  map<int, INGwTcapSession*>::iterator it;

  int liStackDlg = 0;
  char *lpcMode;
 
  bool protectFlg = false; 
  for(int ii = 0; ii < liSizeOfSessionMap; ii++) 
  {
    
    logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer in i<%d> "
                      "for loop offset<%d>", ii, lOffset);
    TcapUserAddressInformation  tuai;
    
    tuai.deSerialize(lpcBuf,lOffset);
    tuai.logTuai("TUAI WITHIN SESSION");

    liStackDlg = tuai.did;
    protectFlg = false;

    if((liStackDlg< tcapLoDlgId) || (liStackDlg>= tcapHiDlgId)) {
      logger.logINGwMsg(false,ERROR_FLAG,0,"handleSynchUpMsgFromPeer- Out of range" 
                        " dialogue Id Rxd dlgId <%d> lo<%d> hi<%d>",liStackDlg, 
                        tcapLoDlgId, tcapHiDlgId);
      protectFlg = true;
    }

    bool lbSessionExisting = false;
    it = m_tcapSessionMap.find(liStackDlg);

    //we dont need to terminate tcap session
    if(m_tcapSessionMap.end() != it) 
    {
	    logger.logINGwMsg(false, ALWAYS_FLAG, 0,"handleSynchUpMsgFromPeer "
      "we dont need to clean this session <%d>", liStackDlg);

      lbSessionExisting = true;
    }
//<added
    lpcMode = "*R MODE*";
    AppInstId lAppInstId;
    lAppInstId.suId =  tuai.suId;
    lAppInstId.spId =  tuai.spId;
  
    //associate the sas cluster with this dialogue

    //remft update transaction state 
    if(!protectFlg) 
    TcapMessage::storeUserInformationFT(tuai.did,
                                        tuai.userDlgId,
                                        tuai.srcaddr,
                                        tuai.destaddr,
                                        tuai.ipaddr,
                                        lAppInstId,

                                        #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
                                        tuai.ansiDlgEv,
                                        tuai.mBillingId.getBillingNo()
                                        #else
                                        tuai.objAcn
                                        #endif
                                        );
   
    if(!protectFlg) 
    mActiveReplayCallMap[tuai.did - mLowDlgId] = 1;

    int liDummy = 1;

    //duplicate info to prevent locking
    INGwFtPktTcapMsg *lptcapMsg = new (nothrow) INGwFtPktTcapMsg;

	  lptcapMsg->initialize(tuai.did,
                          tuai.userDlgId,
                          tuai.srcaddr,
	  								      tuai.destaddr,
                          tuai.ipaddr, 
                          lAppInstId,

                          #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
                          tuai.ansiDlgEv, 
                          tuai.mBillingId.getBillingNo(),
                          #else
                          tuai.objAcn,
                          #endif

                          liDummy,
                          liDummy);

    INGwTcapSession *lpTcSession = NULL;

    if(!lbSessionExisting) {
      lpTcSession = new (nothrow) INGwTcapSession(lAppInstId,
                                                      0,//remft
                                                      TC_MINSTATE_INBOUND_RX);
        lpTcSession->setSasIp(tuai.ipaddr);
        lpTcSession->setInitialInfo(lptcapMsg);
        //if begin else connected
        lpTcSession->setMajorState(TC_CONN_CREATED);
        m_tcapSessionMap[liStackDlg] = lpTcSession;
    }
    else {
      it->second->setSasIp(tuai.ipaddr);
      it->second->setInitialInfo(lptcapMsg);
      lpTcSession = it->second;
    }
   
//added>
    int lMapTag = 0;
    memcpy(&lMapTag,lpcBuf + lOffset,SIZE_OF_INT);
    lOffset += SIZE_OF_INT;
    lMapTag = ntohs(lMapTag);

    if(TAG_OUTB_MAP != lMapTag) {
      logger.logINGwMsg(false, ERROR_FLAG,0," ERROR decoding Synchup Message"
      " offset %d TAG_OUTB_MAP",lOffset);
      return 1;
    }
    
    int lMapSize = 0;
    memcpy(&lMapSize, lpcBuf + lOffset, SIZE_OF_INT);
    lOffset += SIZE_OF_INT;
    lMapSize = ntohs(lMapSize);
                   //map size  
    int lSeqNum = 0; 
    int lBufLen = 0;

    logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer"
                      " outbMsgSize <%d>",lMapSize);
    INGwTcapWorkUnitMsg *lWorkUnit;

    int  liOutBoundSeqNum = 0;

    if(!protectFlg)
    liOutBoundSeqNum = mpTcapIncMsgHandler->getLastOutBoundSeqNum(liStackDlg);

    logger.logINGwMsg(false,TRACE_FLAG,0,"1. OutBound SeqNum <%d> dlgId<%d>",
    liOutBoundSeqNum, liStackDlg);
   
    //updating stray messages
    map<int, INGwTcOutMsgContainer*>::iterator lIterOutBnd = 
                             lpTcSession->m_outboundMsgMap.begin();
     
    for(;lIterOutBnd != lpTcSession->m_outboundMsgMap.end(); lIterOutBnd++) {
      logger.logINGwMsg(false,TRACE_FLAG,0,"calling updStrayOutbWorkUnit <%d>",
                      lIterOutBnd->first);

      updStrayOutbWorkUnit(tuai, (lIterOutBnd->second->mpWorkUnitMsg));    
    }

    //bugfix2
    if(0 == lMapSize)
    {
      int litmpSeqNum = 0;
      memcpy(&litmpSeqNum, lpcBuf + lOffset ,SIZE_OF_INT);
      litmpSeqNum = ntohs(litmpSeqNum);
      lOffset += SIZE_OF_INT;

      if(litmpSeqNum > liOutBoundSeqNum)
      {
        liOutBoundSeqNum = litmpSeqNum;
      }
      logger.logINGwMsg(false,TRACE_FLAG,0,"bugfix2 liOutBoundSeqNum<%d>",
                                          liOutBoundSeqNum);
    }   
    else {
      for(int i = 0; i < lMapSize; i++) 
      {
        memcpy(&lSeqNum, lpcBuf + lOffset ,SIZE_OF_INT);
        lSeqNum = ntohs(lSeqNum);
        lOffset += SIZE_OF_INT;
            
        liOutBoundSeqNum =((liOutBoundSeqNum>lSeqNum)?liOutBoundSeqNum:lSeqNum);

        memcpy(&lBufLen, lpcBuf + lOffset, SIZE_OF_INT);
        lBufLen = ntohs(lBufLen);
        lOffset += SIZE_OF_INT;
  
        logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer"
        " Seq Num <%d> bufLen <%d>",lSeqNum, lBufLen);
  
        U8* lMsgBuf = new (nothrow) U8[lBufLen];
        memcpy(lMsgBuf, lpcBuf + lOffset, lBufLen);
        lOffset += lBufLen;
  
        //buffer, buflen, liDlgId, lSeqNum      
        //check sequence number in map create a work unit and update in map
        //no need to check seq Num 
         if(G_SUCCESS != createWorkUnit(lMsgBuf,
                                        lBufLen,
                                        &lWorkUnit,
                                        tuai))
         {
           //TBD error handling
           logger.logINGwMsg(false,ERROR_FLAG,0,
                             "createWorkUnit cannot create workUnit");
         }
  
   
        lpTcSession->m_outboundMsgMap[lSeqNum] =  
                               new (nothrow) INGwTcOutMsgContainer(lWorkUnit);
  
        lpTcSession->setMinorState(TC_MINSTATE_OUTBOUND_TX);
  
#ifdef MSG_FT_TEST  
  
        char* remftBuf = new char[(lBufLen<<5)];
        memset(remftBuf,0,lBufLen);//remft
        int remftBufLen = 0;
        for(int t = 0; t < lBufLen; t++) {                         //remft
         if(!(i & 15)) 
          remftBufLen += sprintf(remftBuf + remftBufLen,"%s","\n\t");
  
          remftBufLen += sprintf(remftBuf + remftBufLen," %02X", lMsgBuf[t]);
          //remft
        }
  
        logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer"
              " Seq Num<%d> bufLen <%d> buffer<%s>",lSeqNum, lBufLen,remftBuf);
  
        delete [] remftBuf;
  
#endif/*MSG_FT_TEST*/  
      }
    }
   
    if(!protectFlg){ 
      mpTcapIncMsgHandler->setOutBoundSeqNum(liStackDlg, liOutBoundSeqNum);
    }
    else{
      logger.logINGwMsg(false,TRACE_FLAG,0,"ERROR protect flag set");
    }

    logger.logINGwMsg(false,TRACE_FLAG,0,"2. OutBound SeqNum <%d> dlgId<%d>",
      liOutBoundSeqNum, liStackDlg);

    lMapTag = 0;
    memcpy(&lMapTag, lpcBuf + lOffset, SIZE_OF_INT);
    lMapTag = ntohs(lMapTag);
    lOffset += SIZE_OF_INT;
    
    if(TAG_INB_MAP != lMapTag) {
      logger.logINGwMsg(false, ERROR_FLAG,0," handleSynchUpMsgFromPeer"
      "ERROR decoding Synchup Message offset TAG_INB_MAP %d",lOffset);

      return 1;
    }

    
    memcpy(&lMapSize, lpcBuf + lOffset, SIZE_OF_INT);
    lMapSize = ntohs(lMapSize);
    lOffset += SIZE_OF_INT;

    logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer"
                      " TAG_INB_MAP size <%d>",lMapSize);

    int liInbndSeqNum = 0;

    if(!protectFlg)
    liInbndSeqNum = mpTcapIncMsgHandler->getLastInBoundSeqNum(liStackDlg);

    logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer"
                      " inbound seq Num <%d> dlgId<%d>", liInbndSeqNum, liStackDlg);
    //bugfix2
    if(0 == lMapSize)
    {
      int litmpSeqNum = 0;
      memcpy(&litmpSeqNum, lpcBuf + lOffset ,SIZE_OF_INT);
      litmpSeqNum = ntohs(litmpSeqNum);
      lOffset += SIZE_OF_INT;
      if(litmpSeqNum > liInbndSeqNum)
      {
        liInbndSeqNum = litmpSeqNum;
      }

      logger.logINGwMsg(false,TRACE_FLAG,0,"bugfix2 liInbndSeqNum <%d>",
                                           liInbndSeqNum);
    }
    else 
    {   
      for(int i = 0; i < lMapSize; i++) 
      {
        memcpy(&lSeqNum, lpcBuf + lOffset ,SIZE_OF_INT);
        lSeqNum = ntohs(lSeqNum);
        lOffset += SIZE_OF_INT;
   
        liInbndSeqNum = ((liInbndSeqNum>lSeqNum)?liInbndSeqNum:lSeqNum);
        memcpy(&lBufLen, lpcBuf + lOffset, SIZE_OF_INT);
        lBufLen = ntohs(lBufLen);
        lOffset += SIZE_OF_INT;
  
  
        U8* lMsgBuf = new (nothrow) U8[lBufLen];
        memcpy(lMsgBuf, lpcBuf + lOffset, lBufLen);
        lOffset += lBufLen;
  
        lpTcSession->m_InbdlgMsgMap[lSeqNum] =  new INGwTcapFtMsg (lMsgBuf,
                                                       lBufLen,
                                                       lSeqNum);
      
        lpTcSession->setMinorState(TC_MINSTATE_INBOUND_RX);
  
#ifdef MSG_FT_TEST  
        char* remftBuf = new char[lBufLen<<5];
        memset(remftBuf,0,lBufLen);//remft
        int remftBufLen = 0;
        for(int t = 0; t < lBufLen; t++)  {                        //remft
          if(!(i & 15))remftBufLen += sprintf(remftBuf+remftBufLen,"%s","\n\t");
          remftBufLen += sprintf(remftBuf + remftBufLen," %02X", lMsgBuf[t]);
        }
  
        logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer "
                          "Seq Num<%d> bufLen <%d> buffer<%s>",lSeqNum, lBufLen,
                          remftBuf);
  
        delete [] remftBuf;
  
#endif/*MSG_FT_TEST*/  
        logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer "
                          "Seq Num<%d> bufLen <%d> ",lSeqNum, lBufLen);
  
      }
    }
  
    if(!protectFlg) {
      mpTcapIncMsgHandler->setInBoundSeqNum(liStackDlg, liInbndSeqNum);
    }
    else{
      logger.logINGwMsg(false,ERROR_FLAG,0,"ERROR protect flag set");
    }

    logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer"
                      " inbound seq Num <%d> dlgId<%d>",liInbndSeqNum,
                      liStackDlg);
  }//ii loop

  logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer"
                                       "remft length <%d>",lOffset);

  int lTuaiListTag = 0;
  memcpy(&lTuaiListTag, lpcBuf + lOffset, SIZE_OF_INT);
  lTuaiListTag = ntohs(lTuaiListTag);
  lOffset += SIZE_OF_INT;

  if(TAG_TUAI_LIST != lTuaiListTag) {
    logger.logINGwMsg(false, ERROR_FLAG,0," handleSynchUpMsgFromPeer"
    "ERROR decoding Synchup Message offset TAG_TUAI_LIST %d",lOffset);
    return 1;
  }
    
  int lTuaiListLen = 0;
  memcpy(&lTuaiListLen,lpcBuf + lOffset, SIZE_OF_INT);
  lOffset += SIZE_OF_INT;
  lTuaiListLen = ntohs(lTuaiListLen);
  vector<TcapUserAddressInformation*> lTuaiVector; 
  logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer"
                    "remft size of tuai list <%d>",lTuaiListLen);

  for(int i =0 ; i < lTuaiListLen; i++) 
  {
    TcapUserAddressInformation * lpTuai = 
                                 new (nothrow) TcapUserAddressInformation;

    logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer"
                      "before lpTuai->deSerialize <%d> i<%d>",lOffset,i);

    lpTuai->deSerialize(lpcBuf, lOffset);

    //bugfix2
    int liOutSeqNum = 0;
    int liInSeqNum  = 0;
    
    logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer"
                      "before lpTuai->deSerialize <%d> i<%d>",lOffset,i);

    memcpy(&liOutSeqNum,lpcBuf + lOffset, SIZE_OF_INT);
    lOffset += SIZE_OF_INT;

    liOutSeqNum = ntohs(liOutSeqNum);
    mpTcapIncMsgHandler->setOutBoundSeqNum(lpTuai->did, liOutSeqNum);

    memcpy(&liInSeqNum,lpcBuf + lOffset, SIZE_OF_INT);
    lOffset += SIZE_OF_INT;

    liInSeqNum = ntohs(liInSeqNum);
    mpTcapIncMsgHandler->setInBoundSeqNum(lpTuai->did, liInSeqNum);

    logger.logINGwMsg(false,TRACE_FLAG,0,"Dormant dialogue updating sequence"
                      "no. Inb<%d> Outb<%d>", liInSeqNum, liOutSeqNum);

    AppInstId lAppInstId;
    lAppInstId.suId =  lpTuai->suId;
    lAppInstId.spId =  lpTuai->spId;

    protectFlg = false;

    if((lpTuai->did < tcapLoDlgId) || (lpTuai->did >= tcapHiDlgId)) {
      logger.logINGwMsg(false,ERROR_FLAG,0,"handleSynchUpMsgFromPeer- Out of range"
                        " dialogue Id Rxd dlgId <%d> lo<%d> hi<%d>",liStackDlg,
                        tcapLoDlgId, tcapHiDlgId);
      delete lpTuai; lpTuai = NULL;
      continue;
    }     
    //associate the sas cluster with this dialogue

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
      StAnsiDlgEv l_ansiDlgEv; 
#endif

      TcapMessage::storeUserInformationFT(lpTuai->did,
                                        lpTuai->userDlgId,
                                        lpTuai->srcaddr,
                                        lpTuai->destaddr,
                                        lpTuai->ipaddr,
                                        lAppInstId,

                                        #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
                                        l_ansiDlgEv,
                                        lpTuai->mBillingId.getBillingNo()
                                        #else
                                        lpTuai->objAcn
                                        #endif

                                        );

     
      mActiveReplayCallMap[lpTuai->did - mLowDlgId] = 1;

    int liDummy = 1;

    //cache info to prevent hampering(locking) of call processing

    INGwFtPktTcapMsg *lptcapMsg = new (nothrow) INGwFtPktTcapMsg;
	  lptcapMsg->initialize(lpTuai->did,
                          lpTuai->userDlgId,
                          lpTuai->srcaddr,
	  								      lpTuai->destaddr,
                          lpTuai->ipaddr, 
                          lAppInstId,

                          #if (SS7_ANS88 || SS7_ANS92 || SS7_ANS96)
                          lpTuai->ansiDlgEv,
                          lpTuai->mBillingId.getBillingNo(),
                          #else
                          lpTuai->objAcn,
                          #endif

                          liDummy,
                          liDummy);

    logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer"
                      "before lpTuai->deSerialize <%d> i<%d>",lOffset,i);

    //lpTuai->logTuai("TUAI LIST");
    INGwTcapSession *lpTcapSession = NULL;

    map<int, INGwTcapSession*>::iterator lIterSessionMap = 
                             m_tcapSessionMap.find(lpTuai->did);

    if(lIterSessionMap != m_tcapSessionMap.end()) {
      logger.logINGwMsg(false,TRACE_FLAG,0,"handleSynchUpMsgFromPeer"
                                            " Found Session");
      lIterSessionMap->second->setInitialInfo(lptcapMsg);
      lIterSessionMap->second->setSasIp(lpTuai->ipaddr);
    }
    else {
      INGwTcapSession *lpTcapSession = new INGwTcapSession();
      lpTcapSession->setInitialInfo(lptcapMsg);
      lpTcapSession->setSasIp(lpTuai->ipaddr);
      m_tcapSessionMap[lpTuai->did] = lpTcapSession;
    }
    delete lpTuai;  lpTuai = NULL;
  }
  
  delete [] lpcBuf; lpcBuf = NULL;

  g_getCurrentTime(lBuf);
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleSynchUpMsgFromPeer ends<%s>",
                    lBuf);

  dumpTcapSessionMap("handleSynchUpMsgFromPeer",false);
  logger.logINGwMsg(false,TRACE_FLAG,0,"Out handleSynchUpMsgFromPeer");
  return 0;
}

//#endif
//in case there is a stray outbound message TcapUserAddressInformation 
//will be dummy and isStray wil be true
int
INGwTcapFtHandler::createWorkUnit(U8* pBuf, 
                                  int &pBufLen, 
                                  INGwTcapWorkUnitMsg ** pWorkUnit,
                                  TcapUserAddressInformation &pTuai, bool isStray) 
{
  int retVal = G_FAILURE;
  
  TcapMessage *lpTcMsg = ((isStray == false)?new TcapMessage(pTuai.ipaddr):
                          new TcapMessage);
 
  INGwTcapWorkUnitMsg *lWorkUnit = NULL;

  if(lpTcMsg->decode(pBuf, pBufLen, lpTcMsg->dlgR, lpTcMsg->mCompVector)) {
    lWorkUnit = new INGwTcapWorkUnitMsg;
    memset (lWorkUnit,0,sizeof(INGwTcapWorkUnitMsg));
    lWorkUnit->eventType = EVTSTUDATREQ;

    lWorkUnit->m_tcapMsg =  lpTcMsg;
    lWorkUnit->compPres = lpTcMsg->compPresR;
    lpTcMsg->msg_type = lpTcMsg->dlgR.dlgType;
   
    lpTcMsg->dlgR.srcAddr = new SccpAddr;
    memset(lpTcMsg->dlgR.srcAddr,0,sizeof(SccpAddr));
    lpTcMsg->dlgR.dstAddr = new SccpAddr;        
    memset(lpTcMsg->dlgR.dstAddr,0,sizeof(SccpAddr));

    if(!isStray) {
      lWorkUnit->m_suId = pTuai.suId;
      lWorkUnit->m_spId = pTuai.spId;
      lWorkUnit->m_dlgId = pTuai.did;
	    memcpy(lpTcMsg->dlgR.srcAddr, &pTuai.srcaddr, sizeof(SccpAddr));
	    memcpy(lpTcMsg->dlgR.dstAddr, &pTuai.destaddr , sizeof(SccpAddr));

      if(0 != pTuai.objAcn.len) {
        //printf("\n[REPLICATION]+rem+------------------A C N------------------\n");
        //for(int i=0;i< pTuai.objAcn.len;i++) {
        //  printf("%02X ",pTuai.objAcn.string[i]);
        //}
        //printf("\n[REPLICATION]+rem+------------------A C N------------------\n");
        memcpy(lpTcMsg->dlgR.objAcn.string, pTuai.objAcn.string, pTuai.objAcn.len);
        lpTcMsg->dlgR.objAcn.len = pTuai.objAcn.len;
      }
    }

    lpTcMsg->dlgR.srcAddr->pc = 0;
    lpTcMsg->dlgR.dstAddr->pc = 0;
    lpTcMsg->dlgR.spdlgId = lpTcMsg->dlgR.sudlgId; 
 
    //lllll

    *pWorkUnit = lWorkUnit;
    retVal = G_SUCCESS;
  }
  else{
    logger.logINGwMsg(false,ERROR_FLAG,0,"+VER+ Error in decoding buffer for "
      "outbound message");

    INGwTcapProvider::getInstance().getAinSilTxRef().
                                    releaseWorkUnit(lWorkUnit);


    delete  lpTcMsg; lpTcMsg = 0;
    delete lWorkUnit; lWorkUnit = 0;
  }
  return retVal;
}

void
INGwTcapFtHandler::updStrayOutbWorkUnit(TcapUserAddressInformation &pTuai, 
                                        INGwTcapWorkUnitMsg * pWorkUnit)
{
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In updStrayOutbWorkUnit");

  pWorkUnit->m_suId = pTuai.suId;
  pWorkUnit->m_spId = pTuai.spId;
  pWorkUnit->m_dlgId = pTuai.did;
	memcpy(pWorkUnit->m_tcapMsg->dlgR.srcAddr, &pTuai.srcaddr, sizeof(SccpAddr));
	memcpy(pWorkUnit->m_tcapMsg->dlgR.dstAddr, &pTuai.destaddr, sizeof(SccpAddr));

  if(0 != pTuai.objAcn.len) 
  {
     memcpy(pWorkUnit->m_tcapMsg->dlgR.objAcn.string, 
            pTuai.objAcn.string, pTuai.objAcn.len);

     pWorkUnit->m_tcapMsg->dlgR.objAcn.len = pTuai.objAcn.len;
  }

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out updStrayOutbWorkUnit");
}

#ifdef INC_DLG_AUDIT
void
INGwTcapFtHandler::handleAuditCfm(vector<int> &avDldIdList) 
{
//AUD YOGESH
  static int lsiClearingCount = 0;

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In handleAuditCfm");

  if(0 == m_peerId){
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out handleAuditCfm peerId<0>");
    return;
  }

  LOCKSESSION();

  int retVal = G_SUCCESS;
  g_TransitObj	lTransitObj;

  int count = 0;  
  map <int, INGwTcapSession*>::iterator tcSessionIter; 
  map <int,INGwTcapFtMsg*>::iterator     inbIter; 

  map<int, INGwTcOutMsgContainer*>::iterator  outbIter; 
  INGwTcapFtMsg* lMessageBuffer = NULL;
  INGwTcapWorkUnitMsg* lpTcWorkUnit = NULL;
  int liSeqNum = -1;
  U32 lidlgId = 0;
  int liSize = avDldIdList.size();

  
  INGwFtPktTcapMsg* lTuaiInfo = NULL;
  for(int i=0;i<liSize; i++)
  {
    lidlgId = avDldIdList[i];
    tcSessionIter = m_tcapSessionMap.find(avDldIdList[i]);

    if(tcSessionIter == m_tcapSessionMap.end()) {
      logger.logINGwMsg(false,WARNING_FLAG,0,
        "handleAuditCfm dlg not found<%d>: everyThing ok", avDldIdList[i]);
      continue;
    }

    INGwTcapSession* lpTcSession = tcSessionIter->second;
    lidlgId =  tcSessionIter->first;
    gUpdateReplayCallCnt(lidlgId);
 
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"handleAuditCfm cleaning<%d>",
                      lidlgId);
                       
    if(NULL != (lTuaiInfo = lpTcSession->getInitialInfo())) {
      delete lTuaiInfo;
      lpTcSession->setInitialInfo(NULL);
    }

    logger.logINGwMsg(false,TRACE_FLAG,0,
    "handleAuditCfm dlgId <%d>",lidlgId); 

    INGwTcSessionMinorState lMinState = lpTcSession->getMinorState();
      for(inbIter = lpTcSession->m_InbdlgMsgMap.begin(); 
          inbIter!= lpTcSession->m_InbdlgMsgMap.end(); inbIter++)
      {
        logger.logINGwMsg(false,TRACE_FLAG,0,
        "handleAuditCfm inbound message<%d>",lidlgId); 

        liSeqNum = inbIter->first;
        lMessageBuffer = inbIter->second;
        if(lMessageBuffer->isValid())
        {
            delete [] (lMessageBuffer->m_buf); lMessageBuffer->m_buf=0;
            lMessageBuffer->m_bufLen = 0;
            delete lMessageBuffer;
            lMessageBuffer= NULL;
        }
      }

      lpTcSession->m_InbdlgMsgMap.erase(lpTcSession->m_InbdlgMsgMap.begin(),
                                        lpTcSession->m_InbdlgMsgMap.end());   

      //clearFtMsgStoreOnly
      t_tcapMsgBuffer* ltcapMsgInfo;
      INGwTcOutMsgContainer *lpContainer = NULL;

      for(outbIter = lpTcSession->m_outboundMsgMap.begin(); 
          outbIter != lpTcSession->m_outboundMsgMap.end(); outbIter++)
      {
        lpTcWorkUnit = NULL;
        ltcapMsgInfo = NULL;
        liSeqNum = outbIter->first;
        lpContainer = outbIter->second;
        if(NULL != lpContainer) 
        {
          logger.logINGwMsg(false,TRACE_FLAG,0,"remft handleAuditCfm"
                            "cleaning mpWorkUnitMsg");

          if(NULL != lpContainer->mpWorkUnitMsg)
          {
            lpTcWorkUnit = lpContainer->mpWorkUnitMsg;

            if(NULL != lpTcWorkUnit) {
              INGwTcapProvider::getInstance().getAinSilTxRef().
                                              releaseWorkUnit(lpTcWorkUnit);

              delete lpTcWorkUnit->m_tcapMsg; lpTcWorkUnit->m_tcapMsg = 0;
              delete lpTcWorkUnit; lpTcWorkUnit = 0;
            }
          }
          else if(NULL != lpContainer->mtcapMsgInfo)
          {
            ltcapMsgInfo = lpContainer->mtcapMsgInfo;

            if(ltcapMsgInfo) {
              if(NULL != ltcapMsgInfo->m_buf){
                delete [] ltcapMsgInfo->m_buf; ltcapMsgInfo->m_buf =NULL;
              }
              delete ltcapMsgInfo; ltcapMsgInfo = NULL;
            }
          }
          else {
            logger.logINGwMsg(false,ALWAYS_FLAG,0,
                                         "cannot clean Null");
          }
 
          delete lpContainer; lpContainer = NULL;
        }
      }

    lpTcSession->m_outboundMsgMap.erase(lpTcSession->m_outboundMsgMap.begin(),
                                  lpTcSession->m_outboundMsgMap.end());

    delete lpTcSession; lpTcSession = 0;
    m_tcapSessionMap.erase(tcSessionIter);
  }


  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out handleAuditCfm");
}
#endif /*INC_DLG_AUDIT*/

#ifdef MSG_FT_TEST
void
INGwTcapFtHandler::signalBR(){
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In signalBR");

    pthread_cond_signal(&gUnblockBR);

  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out signalBR");
}

//not taking any locks
void 
INGwTcapFtHandler::hookIface(int liHookMask){
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"In hookIface");
  switch(liHookMask){
    case HOOK_INB_CLEANUP:
          logger.logINGwMsg(false,ALWAYS_FLAG,0,"hookIface: HOOK_INB_CLEANUP");
          mbBlkInbCleanup = true;
    break;

    case HOOK_OUTB_CLEANUP:
          logger.logINGwMsg(false,ALWAYS_FLAG,0,"hookIface: HOOK_OUTB_CLEANUP");
          mbBlkOutbCleanup = true;
    break;

    case HOOK_COND_WAIT_BR:
          logger.logINGwMsg(false,ALWAYS_FLAG,0,"hookIface: HOOK_COND_WAIT_BR");
          mbCondWaitBR = true;
    break;

    case HOOK_SIGNAL_BR:
          logger.logINGwMsg(false,ALWAYS_FLAG,0,"hookIface: HOOK_SIGNAL_BR");
          mbCondWaitBR = false;
          signalBR();
    break;

    case HOOK_BLOCK_TERM:
          logger.logINGwMsg(false,ALWAYS_FLAG,0,"hookIface: HOOK_BLOCK_TERM");
          mbBlkTermination = true;
    break;

    case HOOK_RESUME_TERM:
          logger.logINGwMsg(false,ALWAYS_FLAG,0,"hookIface: HOOK_RESUME_TERM");
          mbBlkTermination = false;
    break;

    case HOOK_INCLEAN_RESUME:
       logger.logINGwMsg(false,ALWAYS_FLAG,0,"hookIface: HOOK_INCLEAN_RESUME");
          mbBlkInbCleanup = false;
    break;

    case HOOK_OUTCLEAN_RESUME:
       logger.logINGwMsg(false,ALWAYS_FLAG,0,"hookIface: HOOK_OUTCLEAN_RESUME");
         mbBlkOutbCleanup = false;
    break;

    case HOOK_RESET:
          logger.logINGwMsg(false,ALWAYS_FLAG,0,"hookIface: HOOK_RESET");
          mbBlkTermination = false; 
          mbCondWaitBR     = false;
          mbBlkInbCleanup  = false;
          mbBlkOutbCleanup = false;
          if(0 == m_peerId) {
            setMsgFtFlag(1); //1 means disable ft
          }
          else {
            setMsgFtFlag(0); //0 means enable ft
          }
    break;

    case HOOK_ENABLE_R_INNONFT:
          logger.logINGwMsg(false,ALWAYS_FLAG,0,
                                          "hookIface: HOOK_ENABLE_BR_INNONFT");

          setMsgFtFlag(0); // enabling ft
   break;
          
    case HOOK_DISABLE_R_INNONFT:
          logger.logINGwMsg(false,ALWAYS_FLAG,0,
                                          "hookIface: HOOK_ENABLE_BR_INNONFT");

          setMsgFtFlag(1); // disabling ft
    break;

    default:
    logger.logINGwMsg(false,ALWAYS_FLAG,0,"hookIface cannot take action<%d>",
                      liHookMask);
  }
  logger.logINGwMsg(false,ALWAYS_FLAG,0,"Out hookIface");
}
#endif/*MSG_FT_TEST*/
