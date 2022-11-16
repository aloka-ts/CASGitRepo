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
//     File:     INGwTcapFtHandler.h
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#ifndef _INGW_TCAP_FT_HANDLER_H_
#define _INGW_TCAP_FT_HANDLER_H_

#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>

#include <INGwLoadDistributor/INGwLdDstMgr.h>
#include <INGwFtPacket/INGwFtPktLoadDistMsg.h>
#include <INGwFtPacket/INGwFtPktTcapMsg.h>
#include <INGwFtPacket/INGwFtPktCreateTcapSession.h>
#include <INGwFtPacket/INGwFtPktTermTcapSession.h>
#include <INGwFtPacket/INGwFtPktUpdateTcapSession.h>
#include <INGwFtPacket/INGwFtPktTcapCallSeqAck.h>
#include <INGwFtPacket/INGwFtPktSasHBFailure.h>
#include <INGwFtPacket/INGwFtPktSynchUpMsg.h>
#include <INGwTcapProvider/INGwTcapIncMsgHandler.h>
#include <INGwTcapProvider/INGwTcapSession.h>
#include <INGwInfraUtil/INGwIfrUtlBitArray.h>
#define EVTINBREPLAY 0x99

#ifdef MSG_FT_TEST
 #define  HOOK_INB_CLEANUP         21
 #define  HOOK_OUTB_CLEANUP        22
 #define  HOOK_COND_WAIT_BR        23
 #define  HOOK_SIGNAL_BR           24
 #define  HOOK_BLOCK_TERM          25
 #define  HOOK_RESUME_TERM         26
 #define  HOOK_INCLEAN_RESUME      27
 #define  HOOK_OUTCLEAN_RESUME     28
 #define  HOOK_RESET               29
 #define  HOOK_ENABLE_R_INNONFT    30
 #define  HOOK_DISABLE_R_INNONFT   31
#endif

class INGwTcapIncMsgHandler;
class INGwFtPktSynchUpMsg;
class TcapUserAddressInformation;
class INGwTcapFtHandler: public INGwIfrMgrWorkerClbkIntf
{
	public:

		static INGwTcapFtHandler&
		getInstance();

		INGwTcapFtHandler();

		~INGwTcapFtHandler();

		void 
		initialize(INGwLdDstMgr *p_ldDstMgr);

#ifdef INC_DLG_AUDIT 
    void
    handleAuditCfm(vector<int> &avDldIdList);
#endif

		int
		handleWorkerClbk(INGwIfrMgrWorkUnit* p_workUnit);

		int
		replicateInfoToPeer();

    int
    getMsgFtFlag();

    int 
    sendTcapSessionUpdateInfo(U8 p_dir, int p_dialogueId,
                              int p_seqNum);
    int 
    sendTerminateTcapSession(U32 p_dialogueId);

    void
    initActiveDialogueMap();

    bool isReplayedCall(int pDlgId);
    
#ifdef MESSAGE_FT_NOT_REQUIRED
    int (*ptArrToFtHandlerFunc[4])
        (INGwFtPktCreateTcapSession &INGwFtPktCreateTcapSession) = {NULL};
#endif


	//static void  
	//	TcapUserInformationForSerialization(
	//					int stackDialogue, int userDialogue, 
	//					const SccpAddr &srcaddr, const SccpAddr &destaddr, 
	//					const string &ipaddr, AppInstId & appid);

   int
   sendCreateTcapSessionMsg(g_TransitObj	p_transitObj, TcapMessage *p_tcapMsg);

   int
   replicateTcapCallDataToPeer(U8 p_origin, U8* p_buf, U32 p_bufLen, 
                               int p_seqNum, int p_dlgId);

   map <int, INGwTcapSession*> *
   getTcapSessionMap();

   INGwFtPktTcapMsg* m_tuaiInfo;
   map <int, INGwFtPktTcapMsg*> *
   getInitialInfoMap();

   int
   serializeSessionMap(U8** pBuf, int &pBufLen);

   void
   handleSbyINCFailure();
 
   int
   sendSasHBFailureMsg(const string &p_sasIpAddr, AppInstId &p_appId);

   void
   dumpTcapSessionMap(char* aStr, bool abLockSessionMap = true);

   void 
   clearReplayMessageStore(bool pbTakeLock = true,
                           bool clearFtMsgStoreOnly = false);
 
   void 
   tcapUserInformationForReplication( 
                        int stackDialogue, 
                        int userDialogue, 
					              const SccpAddr &srcaddr, 
                        const SccpAddr &destaddr, 
					              const string &ipaddr, 
                        AppInstId & appid, 
                        #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
                        StAnsiDlgEv pStansiDlgEv,
                        int piBillingNo 
                        #else
                        INcStr objAcn 
                        #endif
                        );

  static int 
  mPeerFaultCnt;

#ifdef MSG_FT_TEST  
  void  
  signalBR(void);

  //this function will serve as an interface to telnet iface
  void
  hookIface(int liHookMask);
#endif

	protected:

	private:
   INGwIfrUtlBitArray *m_tuaiFlgMap;
   int mDisableMsgFtFlag;

#ifdef MSG_FT_TEST
   bool mbBlkTermination;
   bool mbCondWaitBR;
   bool mbBlkInbCleanup;
   bool mbBlkOutbCleanup;
#endif
   
   long m_selfId;
   long m_peerId;

   int mLowDlgId;
   int mTotalDlg;
   U8 *mActiveReplayCallMap;

   void 
   setMsgFtFlag(int p_disableMsgFt = -1);

   map <int, INGwTcapSession*> m_tcapSessionMap;
 
   /*
    => m_initialInfoMap will be used only when we are supposed to 
       replicate accumulated calls

    if peer-up is detected
    replicate all accumulated calls
     in Tc Sesssion 
     if session is not found OR mMajorState is not TC_CONN_CREATED  
        serialize the object and send it across to peer
     else
        delete this object
     replicate the rest of messages in the map
    clean the map       
  */

   map <int, INGwFtPktTcapMsg*> m_initialInfoMap; 

   INGwTcapIncMsgHandler *mpTcapIncMsgHandler;

	 int
	 handleLoadDistMsg(INGwFtPktLoadDistMsg &p_ldMsg);

	 int
	 handleTcapMsgFromPeer(INGwFtPktTcapMsg &p_tcapMsg);

   int
   INGwTcapFtHandler::handleTcapCallDataFromPeer(INGwFtPktCreateTcapSession &p_tcapMsg);

	 INGwLdDstMgr	*m_ldDstMgr;
	 static INGwTcapFtHandler *m_selfPtr;

#ifdef MESSAGE_FT
   int 
   handleAddDialogue(INGwFtPktCreateTcapSession &p_addTcapDlg);

   int
   handleUpdateDialogue(INGwFtPktCreateTcapSession &p_updTcapDlg);

   //create a separate class for termination of session  
   int
   handleTerminateTcapSession(INGwFtPktTermTcapSession& p_termSession);
   
   int
   handleUpdateTcapSession(INGwFtPktUpdateTcapSession &p_updTcapSession);

   int
   handleUpdateTcapSession(INGwFtPktTcapCallSeqAck &p_ackMsg);

   int 
   handleCreateTcapSessionMsg(INGwFtPktCreateTcapSession &p_addTcapDlg);

   int
   handleRepOutBoundMsg(INGwFtPktCreateTcapSession &INGwFtPktCreateTcapSession);

   int 
   addInitialInfoForBackReplication(INGwFtPktTcapMsg& pInitInfo);

   int 
   handleFtTakeOver();
  
   int 
   sendMsgStoreSynchUpMsg();

   int
   handleMsgStoreSynchUpMsg();
 
   int 
   handleINGwCleanup();

   int 
   handleCleanTcapFtMsg();

   int 
   handleInboundMessage(INGwFtPktUpdateTcapSession &p_updTcapSession);

   int
   handleOutboundMessage (INGwFtPktUpdateTcapSession &p_updTcapSession);

   int 
   handleCleanInboundMessage(INGwFtPktTcapCallSeqAck &p_AckMsg);

   int 
   handleCleanOutboundMessage(INGwFtPktTcapCallSeqAck &p_AckMsg);

   int 
   handleSasHBFailure(INGwFtPktSasHBFailure &p_hbFailureMsg);

   int
   handleSynchUpMsgFromPeer(INGwFtPktSynchUpMsg &pSynchUpMsg);

   void
   handlePeerINCFailure();

   void 
   handlePeerConnectedMsg();
 
   int
   createWorkUnit(U8* pBuf, 
                  int &pBufLen, 
                  INGwTcapWorkUnitMsg **pWorkUnit,
                  TcapUserAddressInformation  &pTuai,
                  bool pIsStrayMsg = false);

  void 
  updStrayOutbWorkUnit(TcapUserAddressInformation &pTuai, 
                       INGwTcapWorkUnitMsg * pWorkUnit);
#endif
};

/*

  hooks to test back replication 
  hook mask 21 =>  stop cleaning of inbound message.
  hook mask 22 =>  stop cleaning of outbound message.
  hook mask 23 =>  conditional wait BR thread.
  hook mask 24 =>  signal BR serialization thread.
  hook mask 25 =>  stop terminate tcap session.
  hook mask 26 =>  start terminating tcap sessions on receiving requests.
  

*/

#endif
