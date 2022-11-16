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
//     File:     INGwTcapIncMsgHandler.h
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#ifndef _INGW_TCAP_INC_MSG_HANDLER_H_
#define _INGW_TCAP_INC_MSG_HANDLER_H_

#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>

//#ifdef _INGW_IFR_TL_IF_TELNET_INTF_H_
//#undef _INGW_TCAP_WORK_UNIT_MSG_H_
//#endif

#include <INGwTcapProvider/INGwTcapWorkUnitMsg.h>
#include <INGwTcapProvider/INGwTcapFtHandler.h>
#include <INGwIwf/INGwIwfIface.h>
#include <INGwLoadDistributor/INGwLdDstMgr.h>

//#include "INGwSilRx.h"
//#include "INGwSilTx.h"


#define STATUS_PC  0x01
#define STATUS_SSN 0x02
#include "spt.h"
typedef struct t_INGwTcapWorkUnitMsg INGwTcapWorkUnitMsg;
class INGwTcapFtHandler;
class INGwTcapIncMsgHandler: public INGwIfrMgrWorkerClbkIntf
{
  //friend int INGwTcapFtHandler::handleFtTakeOver();
	public:

		static INGwTcapIncMsgHandler&
		getInstance();

		~INGwTcapIncMsgHandler();

		void 
		initialize(INGwIwfBaseIface *p_iface, INGwLdDstMgr *p_ldDstMgr);

		int
		handleWorkerClbk(INGwIfrMgrWorkUnit* p_workUnit);

		int
		handleRxMsg(INGwTcapWorkUnitMsg &p_tcapWorkUnit);

		int
		handleRxTcapMsg(INGwTcapWorkUnitMsg &p_tcapWorkUnit);
   
    /*pPcOrSsn SPT_STATUS_PC 0x1
               SPT_STATUS_SS 0x2 
    */  
    int
    sendSteReqToStack(INGwTcapWorkUnitMsg &p_tcapWorkUnit, 
                      U8 pPcOrSsn = SPT_STATUS_PC);
    
    TcapMessage **
    getCallMap();
   
//    int**
//    getSeqNumMap();
   
    //this should be called from TcapMessage::removeDialogue method 
    void 
    resetSeqNumForDlg(U32 pDlgId, int pVal = 0);

    //update the sequence number outbound message
    void 
    updateLastSeqNumForDlg(U32 pDlgId, int pVal);

    //returns last seq num for outbound messages
    int getLastSequenceNumForDlg(U32 pDlgId);

    void 
    setInBoundSeqNum(U32,int);

    int 
    getLastInBoundSeqNum(U32 p_dlgId);

    int
    getLastOutBoundSeqNum(U32 pDlgId);

    void 
    setOutBoundSeqNum(U32 p_dlgId, int p_seqNum);

    void 
    initCallMap(int p_loDlg, int p_hiDlg);
   
    void getDldIdRange(int *pLowDlg, int *pTotalDlg);
    void getLowDlgId(int *pLoDlgId);
    void getTotalDlg(int *pTotalDlg);

    handleInbMsgReplay(g_TransitObj *p_transObj);
	private:

		int
		handleStatusMsg(INGwTcapWorkUnitMsg &p_tcapWorkUnit);

		int
		handleBindCfmMsg(INGwTcapWorkUnitMsg &p_tcapWorkUnit);

	  int 
		sendMsgToSas(INGwTcapWorkUnitMsg &p_tcapWorkUnit,
								 TcapMessage *p_tcapMsg, U8* p_encBuf,
                 int p_bufLen);



		int
		sendSasAppResp(INGwTcapWorkUnitMsg &p_tcapWorkUnit, int p_flag);
																	
		int
		sendSccpMgmtInfo(INGwTcapWorkUnitMsg &p_tcapWorkUnit, 
									   PcSsnStatusData &p_status, int p_flag);

    void
    cleanHangingCall(TcapMessage **pTcapMsg);

    void
    modifyGtDigits(SccpAddr **appSpAddr, TcapMessage *pTcMsg);

		INGwTcapIncMsgHandler();

		static INGwTcapIncMsgHandler*	m_selfPtr;

		void					*m_appContext;
		INGwIwfIface	*m_iwfIface;
		INGwLdDstMgr	*m_ldDstMgr;
		TcapMessage	  **m_callMap;
    int TCAP_MAX_DLG_ID;
    int mLoDlgId;
    int mTotalDlg;
    //array to store sequence number at the index dlgId%TCAP_MAX_DLG_ID
    int            **m_seqNumMap;
    
};
#endif
