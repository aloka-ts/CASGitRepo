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
//     File:     INGwTcapProvider.h
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#ifndef _INGW_TCAP_PROVIDER_H_
#define _INGW_TCAP_PROVIDER_H_

#include <pthread.h>
#include <queue>

// Mriganka - Trillium stack integration
#include "INGwSilRx.h"
#include "INGwSilTx.h"

#include <INGwIwf/INGwIwfBaseProvider.h>
#include <INGwLoadDistributor/INGwLdDstMgr.h>
#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>

#include <INGwTcapProvider/INGwTcapInclude.h>
#include <INGwTcapProvider/INGwTcapIface.h>
#include <INGwTcapProvider/INGwTcapStatParam.h>

using namespace std;

// Mriganka - Trillium stack integration
class INGwSmWrapper;
#define MAX_SEQ_NUMBER_VALUE 50000 
#define DEF_INGW_SELF_PROC_ID 51

#define AIN_SUCCESS 0
#define AIN_FAILURE 1
#define AIN_ERROR 1

#define MAX_PROC_IDS 2


class INGwTcapProvider : public INGwIwfBaseProvider
{
	public:
	
		INGwTcapProvider();

		~INGwTcapProvider();
  

		static INGwTcapProvider&
		getInstance();

    int 
    getSapCnt();

		static void*
		getUserId(SccpAddr &p_ownAddress);

		static void
		getAppId(SccpAddr &ownAddress, AppInstId &appid);

		int 
		changeState(INGwIwfBaseProvider::ProviderStateType p_state);

		int
		startUp(void);

		void
		getStatistics(std::ostrstream &output, int tabCount);

		int
		configure(const char* apcOID, const char* apcValue, ConfigOpType aeOpType);

		g_tcapSsnState
		registerWithStack(U32 &p_pc, U8 &p_ssn, int p_flag);

    int
    regAppWithStackWrapper();

		void
		registerAllAppWithStack();

		g_tcapSsnState
		deregisterWithStack(U32 &p_pc, U8 &p_ssn, int p_flag);

		int
		changeSsnStatus(U32 &p_pc, U8 &p_ssn, int p_flag);

		inline INGwIwfBaseIface*    
		getInterface() {
			return m_tcapIface;
		}

		inline vector<AppIdInfo>*	
		getAppConfigInfoList() {
			return &m_appIdInfo;
		}


		inline void
		getAppId(U32 &p_pc, U8 &p_ssn, AppInstId& p_appId) {

		// As per latest logic we will fill suId and SpId
		// in AppInst based on ssn
		for(int i=0; i < m_ssnInfo.size(); ++i)
		{
			if(m_ssnInfo[i].ssn == p_ssn)
			{
				p_appId.suId = m_ssnInfo[i].suId;
				p_appId.spId = m_ssnInfo[i].spId;
				break;
			}
		}
#if 0
			for (int i=0; i < m_appIdInfo.size(); ++i) {
				if ((p_pc == m_appIdInfo[i].pc) && (p_ssn == m_appIdInfo[i].ssn)) { 
					p_appId.appId = i; 
					break;
				}
			}
#endif
			return;
		}

  AppIdInfo *invalid; 
	inline AppIdInfo* getAppIdInfo(S16 pSapId) {
    if(pSapId > m_appIdInfo.size() -1) {
      return  invalid;
    }
    else {
      return &(m_appIdInfo[pSapId]);
    }
  }

	bool
	getPcSsnForSuId(vector<U32> &p_pcVector, U8 &ssn, S16 &suId);

	inline vector<SccpAddr>
	getOrigAddress(U32 p_pc= 0, U8 p_ssn = 0, S16 p_suId = -1)
  {

		bool allReq     = false;
		bool suIdBased  = false;
		bool matchBased = false;
    
		U8 lSsn = 0;

    logger.logINGwMsg(false, TRACE_FLAG, 0, "[getOrigAddress]"
       "PC %d SSN %d suId %d",p_pc, p_ssn, p_suId);   

		if ((0 == p_pc) && (0 == p_ssn) && (-1 == p_suId))
    { 
      logger.logINGwMsg(false, TRACE_FLAG, 0, "[getOrigAddress]"
        "allReq is set true");
		 	allReq = true;
    }
		else if( (0 == p_ssn) && (0 == p_pc) && (p_suId != -1))
		{
      logger.logINGwMsg(false, TRACE_FLAG, 0, "[getOrigAddress]"
        "suIdBased is set true");
			suIdBased = true;
			getSsnForSuId(lSsn, p_suId);
		}
		else {
      logger.logINGwMsg(false, TRACE_FLAG, 0, "[getOrigAddress]"
        "matchBased is set true");
			matchBased = true;
    }

		vector<SccpAddr> lAddr;

    logger.logINGwMsg(false, TRACE_FLAG, 0, "[getOrigAddress]size of m_appIdInfo vector %d",m_appIdInfo.size());   

		for (int i=0; i < m_appIdInfo.size(); ++i) 
		{
      logger.logINGwMsg(false, TRACE_FLAG, 0, "[getOrigAddress] A");
			if(matchBased) {
      logger.logINGwMsg(false, TRACE_FLAG, 0, "[getOrigAddress] B");
				if (m_appIdInfo[i].pc != p_pc || 
						m_appIdInfo[i].ssn != p_ssn) {
						continue;
				}
			}
			else if(suIdBased) {
      logger.logINGwMsg(false, TRACE_FLAG, 0, "[getOrigAddress] C");
				if (m_appIdInfo[i].ssn != lSsn)
					continue;
			}

      SccpAddr   			lOrigInfo;
      // Filling SccpAddr for orig opc-ssn
		  lOrigInfo.pres   = INC_TRUE;
		  lOrigInfo.pc     = m_appIdInfo[i].pc;
		  lOrigInfo.pcInd  = INC_TRUE;
		  lOrigInfo.ssn    = m_appIdInfo[i].ssn;
		  lOrigInfo.ssnInd = INC_TRUE;
		  lOrigInfo.rtgInd = INC_RTE_SSN;
      logger.logINGwMsg(false, TRACE_FLAG, 0, "[getOrigAddress]PC[%d] SSN[%d]", lOrigInfo.pc, lOrigInfo.ssn);   
		  lOrigInfo.sw = m_appIdInfo[i].sccpProtoVar;

			lAddr.push_back(lOrigInfo);

      if (matchBased)
				  break;
		}
    
		return lAddr;
  }

	inline INGwIwfBaseIface*
	getIwfIface() {
		return m_iwfIface;
	}

	inline INGwLdDstMgr*
	getLoadDstMgr() {
		return m_ldDstMgr;
	}

	// if ssn = 0 then check for availability of OPC
	// if pc = 0 then check for the availability of ssn
	/*inline*/ bool 
	verifyOpcSsnInConfigList(unsigned long &p_pc, U8 &p_ssn); /*{
		bool retVal = true;
		for (int i=0; i < m_appIdInfo.size(); ++i) {
      logger.logINGwMsg(false,VERBOSE_FLAG,0,"verifyOpcSsnInConfigList"
        "default pc[%d] = <%d> param pc <%d>",i,m_appIdInfo[i].pc,p_pc);
			if(p_ssn != 0 && p_pc != 0) {
				if ((m_appIdInfo[i].pc == p_pc) && (m_appIdInfo[i].ssn == p_ssn)) {
					return retVal;
				}
			}
			else if(p_ssn == 0) { // check for PC only
				if (m_appIdInfo[i].pc == p_pc) {
					return retVal;
				}
		 }
		 else if(p_pc == 0) { // check for SSN only
				if (m_appIdInfo[i].ssn == p_ssn) {
					return retVal;
				}
		 }
		}
		return (false);
	}
*/
	INGwIfrMgrWorkerClbkIntf*
	getTcapCallHandler();

	INGwIfrMgrWorkerClbkIntf*
	getTcapFtHandler();
  auditTcapDlg();

	inline bool
	isAnsi() { return m_protoType; }

	bool
	updateSCCPState(U32 &p_pc, U8 &p_ssn, PcSsnStatusData &p_state, 
									g_PcOrSsn flag);

	bool 
	getSCCPState(U32 &p_pc, U8 &p_ssn, PcSsnStatusData &p_state, 
							 g_PcOrSsn flag);

	string
	getLoadDistDebugInfo();

	void  *m_appContext;


    // Mriganka - Trillium stack integration - Start [

    // Helper function to get the INGwSil for trillium stack
    INGwSilRx& getAinSilRxRef();
    INGwSilTx& getAinSilTxRef();
		int handleAinSmEvent(int pVal);
    int run();
    int stop();

    void setSubsysIdProcId();
    int INGwTcapProvider::setProcStatus();

    int getProcIdList(U16 procIdList[]);
    int getProcIdForSubsysId(U32 subsysId);
  
    // ] - Mriganka - Trillium stack integration - End
    inline 
    U8 getSelfInstId(){
     return m_selfInstId;
    }  

		inline INGwSmWrapper*
		getSmWrapperPtr(){ 
			return mINGwSmWrapper;
		}

		int myRole();

	  void 
		updateSsnState(S16 suId, g_tcapSsnState state);

		bool
		getSsnInfo(U8 p_ssn, S16 &suId, S16 &spId);

		bool
		updateSsnInfo(U8 p_ssn, S16 spId);

		void
		addSsnInfo(U8 p_ssn, S16 suId);

		void
		getSpIdForSuId(S16 &suId, S16 &spId, U8 &ssn);

		void
		getSuIdForSpId(S16 &suId, S16 &spId, U8 &ssn);

		void
		getSsnForSuId(U8 &p_ssn, S16 &p_suId);
  
    int 
    getFpMask();

    void 
    setFpMask(int pFpMask);

#ifdef INC_DLG_AUDIT
    void 
    handleAuditCfm(void);
#endif
	private:

    //-1 = replicate begin and fail 
    //2 =  send begin and fail without cleaning begin at S-INC
    //-3 = receive NOTIFY msg - replicate and fail 
    //-4 = receive NOTIFY msg - send to network fail before cleaning CONT
    //-5 = receive NOTIFY msg - send to network and fail after cleaning CONT
    //-6 = receive second NOTIFY  msg - replicate and fail
    //-7 = receive second NOTIFY  msg - send to network and fail before cleaning
    //-8 = receive second NOTIFY  msg - send no nw and fail after cleaning
    //9  = receive NULL END - replicate and fail
    //-10= receive NULL END - fail before terminating session
    //-11= receive 2nd message from Nw replicate and fail
    //12 = dont process the replicated messages only log the messages
    //15 = request for n number of components in nth outgoing message and fail
    //     enable INGW_OUTGNG_CNT, INGW_OUTGNG_CMP refer INGwSilTx
    int mFpMask;    
    
		int
		initSs7Config();
 
    void
    logAuditInfo();

		int
		initAppInstState(int p_state = 0);

		int
		fetchOpcSsnInfo();

		int
		getSelfInstId(int &p_numOfInstance);
    

		void
		getTcapStatParamIndex();

		static const char* mpcOIDsOfInterest[];

		static INGwTcapProvider *m_selfPtr;
		INGwTcapIface					  *m_tcapIface;
		INGwIwfBaseIface   		  *m_iwfIface;
		INGwLdDstMgr						*m_ldDstMgr;

		pthread_mutex_t				   m_appIdSsnInfoLock;

    // Contains AppId verses state of PC-SSN registered with stack.
		TcapAppIfInfo*					 m_appIdSsnInfo[TCAP_MAX_APP_ID];

		//queue<TcapRegInfo*>			 m_registerQ;

		bool										 m_firstTimeRegistration;
		unsigned char						 m_protoType; // ANSI: 1, ITU: 2,
                                          // JAPAN_TTC: 3, JAPAN_NTT: 4
                                          // CHINA: 5

    char            				 m_stkFileName[MAX_STACK_LOG_FILE_SIZE];

		INGwTcapProvider(const INGwTcapProvider& p_conSelf);
		INGwTcapProvider& operator=(const INGwTcapProvider& p_conSelf);

    // Mriganka - Trillium stack integration - Start [

    INGwSilRx  &mINGwSilRx;                // reference to AinSilRx
    INGwSilTx  &mINGwSilTx;                // reference to AinSilTx
  	INGwSmWrapper *mINGwSmWrapper;         // pointer to Stack Manager

    map <int, int>    			  subsysIdProcIdMap; // INGw SubsystemID and Its ProcID
		U8                			  m_selfInstId;
    vector<AppIdInfo> 	      m_appIdInfo;  // AppId, PC, SSN and ProtoVariant
    map <int, ProcStatus *>   m_incProcStatusMap;
    bool											m_isRunning;
	  queue<TcapRegInfo*>      m_registerQ;
    int                      m_selfState;
		vector<SsnInfo>					 m_ssnInfo;

};
#endif
