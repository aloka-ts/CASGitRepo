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
//     File:     INGwIfrMgrManager.h
//
//     Desc:     Has class INGwIfrMgrManager.
//               This is the main class of Infrastruture. It does the initialisation and shutdown
//               of the various INGw components. 
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal  10/12/07     Initial Creation
//********************************************************************

#ifndef INGW_IFR_MGR_MANAGER_H_
#define INGW_IFR_MGR_MANAGER_H_

#include <pthread.h>

#include <map>
#include <list>
#include <memory>
#include <strstream>
#include <Util/CoreScheduler.h>

#include <INGwInfraManager/INGwIfrMgrFtIface.h>
#include <INGwInfraManager/INGwIfrMgrIPAddrHandler.h>
#include <INGwInfraUtil/INGwIfrUtlConfigurable.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraTelnetIface/INGwIfrTlIfTelnetIntf.h>
#include <INGwInfraParamRepository/INGwIfrPrIncludes.h>
#include <INGwInfraUtil/INGwIfrUtlReject.h>
#include <INGwIwf/INGwIwfBaseProvider.h>
#include <INGwFtMessenger/INGwFtMsnMessenger.h>
#include <INGwFtTalk/INGwFtTkVersionHandlerInf.h>
#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>
#include <INGwInfraManager/INGwIfrMgrRoleMgr.h>
#include <INGwInfraResourceMonitor/INGwIfrResMonResourceMonitor.h>
//#include <INGwStackManager/INGwSmIncludes.h>



/** This is the main class of Infrastruture. It does the initialisation and shutdown
 *  of the various INGw components. 
 */

class INGwIfrMgrManager : public virtual INGwIfrMgrFtIface,
              public virtual INGwIfrUtlConfigurable,
							public INGwFtTkVersionHandlerInf, 
							public INGwIfrMgrWorkerClbkIntf
{
   public:

      static INGwIfrMgrManager& getInstance(void);

      virtual ~INGwIfrMgrManager();

      void initialize(int p_Argc, const char** p_Argv) throw (INGwIfrUtlReject);

      void registerIfaceWithAgent(void);
      bool isRoleResolutionDone();
      //Overriding Base class function of INGwIfrUtlConfigurable
      // This function will be call when there is change in INGW
      // parameters via ems console

      int configure(const char* p_OID, const char* p_Value, 
                    ConfigOpType p_OpType);


      //This Function is used to update the configuration parameters 
			//changed at EMS using Agent

      void modifyCfgParam(const char* p_OID, const char* p_Value,
                          ConfigOpType p_OpType, long p_SubsystemId,
                          bool aSyncFlag = false);

      // This function will be used by agent callback implementation
      // to pass on the information sent/requested by EMS.

      int startUp(void);
      int changeState(int p_Value);
      void performance(void* p_PerfData);
      int oidChanged(const char* p_OID, const char* p_Value, 
                     ConfigOpType p_OpType, long p_SubsystemId);

      // To handle the softshutdown request
      void softShutdown();
      std::string toLog(void) const;

      void getStatistics(std::ostrstream& p_Output, int p_TabCount,
                         bool p_DetailFlag);

      //Impementation for interfaces of INGwIfrMgrFtIface
      
      void recvMsgFromPeerINGw(char* p_MsgBuffer, int p_Len, int p_Version );

      short handleINGwFailure(int p_PeerINGwId);

      int getMsgDebugLevel();

      void bogusFault(unsigned int p_SubsysID);

      void doAction(unsigned int p_SubsysID, unsigned int p_ActionID);

      void ingwConnected(int p_aIngwId);

      //This api is used to send FT messsage to peer.
      void sendMsgToINGW(INGwFtPktMsg *p_Msg);

			inline CoreScheduler* getExtraScheduler()
			{
				 return m_ExtraScheduler;
      }  

      // Interface from INGwFtTkVersionHandlerInf
			int negotiateVersion(const ObjectId &peer,
													 const INGwFtTkVersionSet& peerVersion);

      void connectPeerSubsystems();

			int
			setSelfMode(bool p_bIsActive);

      // Will be interface for role manger
			// to notify when peer is connected in self active state.

			int
			handlePeerConnect();

			//Handle work posted
			int
			handleWorkerClbk(INGwIfrMgrWorkUnit* p_Work);

			void
			reportMeasurementSet(void *p_Obj);

			int
			fetchSs7InitialConfig(long cmdType, int myRole);
   
      //default parameter allows same function to set the flag from telnet iface
      void 
      setMsgFtFlag(int p_enableMsgFt = -1);

      int 
      getMsgFtFlag();

			void
			logPeerMsg(INGwFtPktMsg *msg);

			// Statistics
			static int miStatParamId_CreateTcapSession;
			static int miStatParamId_TcapCallSeqAck;
			static int miStatParamId_UpdateTcapSession;
			static int miStatParamId_CloseTcapSession;
			static int miStatParamId_CallBkup;
			static int miStatParamId_FtDelCall;
			static int miStatParamId_LoadDistMsg;
			static int miStatParamId_SasHbFailure;
			static int miStatParamId_TcapDlgInfo;
			static int miStatParamId_StackConfig;
			static int miStatParamId_AddLink;
			static int miStatParamId_DelLink;
			static int miStatParamId_AddLinkset;
			static int miStatParamId_DelLinkset;
			static int miStatParamId_AddNw;
			static int miStatParamId_DelNw;
			static int miStatParamId_AddRoute;
			static int miStatParamId_DelRoute;
			static int miStatParamId_AddSsn;
			static int miStatParamId_DelSsn;
			static int miStatParamId_AddUsrPart;
			static int miStatParamId_DelUsrPart;
			static int miStatParamId_AssocDown;
			static int miStatParamId_AssocUp;
			static int miStatParamId_AddPs;
			static int miStatParamId_DelPs;
			static int miStatParamId_AddEp;
			static int miStatParamId_DelEp;
			static int miStatParamId_AddPsp;
			static int miStatParamId_DelPsp;
			static int miStatParamId_AddRule;
			static int miStatParamId_DelRule;
			static int miStatParamId_AddGtAddrMap;
			static int miStatParamId_DelGtAddrMap;
			static int miStatParamId_ModLink;
			static int miStatParamId_ModLinkset;
			static int miStatParamId_ModPs;
			static int miStatParamId_CofigStatus;

			std::string toLogFtStats();
			void        setFtStatParamIndex();

   protected:
    
      typedef std::list<std::string> StrList;
      typedef std::map<std::string, std::string> StrStrMap;

			//This map will have provider mapped with their type key
      typedef std::map<int, INGwIwfBaseProvider*> ProviderMap;

      INGwIfrMgrManager();

      int getState(long subSysId);

      static INGwIfrMgrManager*         m_Self;
      INGwIfrPrParamRepository&         m_ParamRep;
      INGwIfrTlIfTelnetIntf*            m_TelnetIntf;
      INGwIfrMgrIPAddrHandler           m_IPAddrHndlr;

      pthread_rwlock_t                  m_RWLock;
      CoreScheduler*                    m_ExtraScheduler;
      CoreScheduler*                    m_CoreScheduler;
      bool                              m_StartAsPrimary;
      ProviderMap                       m_ProviderMap;
			int                               m_State;
			INGwFtMsnMessenger*               m_FtMsnMgr;
			INGwIfrMgrRoleMgr*                m_RoleManager;
			INGwIfrResMonResourceMonitor      m_ResourceMonitor;

      pthread_mutex_t m_startupMutex;
      pthread_cond_t  m_StartupCond;

      pthread_mutex_t m_PeerStateChangeMutex;
      
   private: 
      int mDisableMsgFtFlag;
      bool mIsRoleResDone;
      INGwIfrMgrManager& operator= (const INGwIfrMgrManager& arSelf);
      INGwIfrMgrManager(const INGwIfrMgrManager& arSelf);

			int
			resolveRole();
};

#endif 

