#ifndef _INGW_BLK_CONFIG_H_
#define _INGW_BLK_CONFIG_H_

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <vector>
#include <queue>

using namespace std;

typedef vector<AddLink> LnkSeq;
typedef vector<AddLinkSet> LnkSetSeq;
typedef vector<AddRoute> RouteSeq;
typedef vector<AddUserPart> UserPartSeq;
typedef vector<AddGtRule> GtRuleSeq;
typedef vector<AddAddrMapCfg> AddrMapSeq;
typedef vector<AddLocalSsn> LocalSsnSeq;
typedef vector<AddRemoteSsn> RemoteSsnSeq;
typedef vector<AddNetwork> AddNetworkSeq;
typedef vector<AddEndPoint> EpSeq;
typedef vector<AddPsp> AspSeq;
typedef vector<AddPs> AsSeq;
typedef vector<M3uaAssocUp> M3uaAssocUpSeq;
typedef vector<M3uaAspAct> M3uaAspActSeq;

class INGwSmBlkConfig
{
	public:
		
		typedef enum {
			UNINITIALIZED     = 0, // Initial State
			INITIALIZED				= 1, // Stable State
			CONFIG_INPROGRESS = 2, // Role Standby; Rxed Config start from peer
			CONFIG_COMPLETE   = 3 // Role Standby; Rxed Config end from peer
		} eStackConfigState;	

		typedef enum {
			STACK_INIT = 1,
			PEER_UP	   = 2,
			PEER_DOWN	 = 3,
			ROLE_CHANGED_ACTIVE		
		} eAction;

		typedef struct _msgQ
		{
			int m_action;
		} msgQStruct;

		static INGwSmBlkConfig&
	 	getInstance();

		int 
		updateNode(Ss7SigtranSubsReq *req, int from);

		int 
		updateNode(INGwFtPktMsg *msg);

		int 
		modNode(Ss7SigtranSubsReq *req, int nodeType);

		int addNetwork(AddNetwork &addNw);
		int delNetwork(DelNetwork &delNw);
		AddNetworkSeq* getNetworkList();

		int addLinkset(AddLinkSet &addLnk);
		int delLinkset(DelLinkSet &delLnk);
		int modLinkset(AddLinkSet &addLnk);
		LnkSetSeq* getLinkSetList();

		int addUserPart(AddUserPart &addUp);
		int delUserPart(DelUserPart &delUp);
		UserPartSeq* getUserPartList();
		//int modUserPart();

		int addLocalSsn(AddLocalSsn &addSsn);
		int delLocalSsn(DelLocalSsn &delSsn);
		LocalSsnSeq* getLocalSsnList();
		//int modLocalSsn();

		int addRoute(AddRoute &addRte);
		int delRoute(DelRoute &delRte);
		RouteSeq* getRouteList();
		//int modRoute();

		int addLink(AddLink &addLnk);
		int delLink(DelLink &delLnk);
		int modLink(AddLink &addLnk);
		LnkSeq* getLinkList();
		bool getLinkInfo(U16 p_dlSapId, AddLink &p_linkInfo);
		bool getLinkInfoForLinkId(U16 p_lnkId, AddLink &p_linkInfo);
    
		LnkSeq* getPeerLinkList();


		int addGtRule(AddGtRule &addGtRule);
		int delGtRule(DelGtRule &delGtRule);
		GtRuleSeq* getGtRuleList();
		//int modGtRule();

		int addGtAddrMap(AddAddrMapCfg &addGtMap);
		int delGtAddrMap(DelAddrMapCfg &delGtMap);
		AddrMapSeq* getGtAddrMapList();
		//int modGtAddrMap();

		int addEp(AddEndPoint &addEp);
		int delEp(DelEndPoint &delEp);
		EpSeq* getEpList();
		EpSeq* getPeerEpList();
		//int modEp();

		int addAsp(AddPsp &addPsp);
		int delAsp(DelPsp &delPsp);
		AspSeq* getAspList();
		bool getAsp(U32 pspId, AddPsp &psp);
		//int modAsp();

		int addAs(AddPs &addPs);
		int delAs(DelPs &delPs);
		int modAs(AddPs &addPs);
		AsSeq* getAsList();
		//int modAs();

		int addActvAsp(M3uaAspAct &actAsp);
		int delActvAsp(M3uaAspInAct &inactAsp);
		M3uaAspActSeq* getActvAspList();
		M3uaAspActSeq* getPeerActvAspList();

		int addM3uaAssocUp(M3uaAssocUp &addM3ua);
		int delM3uaAssocUp(M3uaAssocDown &delM3ua);
		M3uaAssocUpSeq* getM3uaAssocUpList();
		M3uaAssocUpSeq* getPeerM3uaAssocUpList();
    U16 getAssocProcId(M3uaAssocUp &addM3ua);
    U16 getAssocProcId_Aspup(M3uaAspUp &addM3ua);
    U16 getAssocProcId_Aspactv(M3uaAspAct &addM3ua);
    S16 getProtoTypeForNwId(U16 nwId);

		void
		initialize();

		void 
		roleChangedToActive();

		void 
		peerUp();

		void 
		peerDown();

		void
		getActiveProcIds(vector<int> &procIdList);

		void
		getPeerProcId(vector<int> &procId);

		void
		getSelfProcId(vector<int> &procId);

		void 
		getAllProcId(vector<int> &procId);

		void
		updateRelayStatus(bool status);

		bool
		getRelayStatus();

		void
		initializeStack();

		void 
		execute();

		bool
		getMode();

		int 
		getRelayChannelRole();

		void
		updatePeerEnabled(bool flag);

		bool
		isPeerEnabled();

    bool
		isSctpTuclEnabled();

    void
    setSctpTuclEnabled(bool flag);

		void
		updateState(StkConfigStatus &stk);

		// Logging Function
		void log();
		string dumpNw();
		string dumpUserPart();
		string dumpLinkset();
		string dumpLink();
		string dumpRoute();
		string dumpLocalSsn();
		string dumpRule();
		string dumpAddrMap();
		string dumpEp();
		string dumpAs();
		string dumpAsp();
		string dumpAssocUp();
		string dumpActvAsp();

		void updateNetworkState(StkConfigStatus &stk);
		void updateLinkState(StkConfigStatus &stk);
		void updateUserPartState(StkConfigStatus &stk);
		void updateSsnState(StkConfigStatus &stk);
		void updateRouteState(StkConfigStatus &stk);
		void updateLinksetState(StkConfigStatus &stk);
		void updateGtRuleState(StkConfigStatus &stk);
		void updateGtAddrMapState(StkConfigStatus &stk);
		void updateEpState(StkConfigStatus &stk);
		void updateAsState(StkConfigStatus &stk);
		void updateAspState(StkConfigStatus &stk);
		void updateAssocUpState(StkConfigStatus &stk);
		void updateOperState(StkConfigStatus &stk);

		bool  m_toggleFlag; // temp fix to avoid calling HandlePeerFailure twice
												// on role change over
		long 	m_selfId;
		long	m_peerId;
		int   m_selfProcId;
		int   m_peerProcId;
		bool	m_relayStatus;
		queue<msgQStruct*> m_msgQ;
		bool 	m_isRunning;
		bool	m_mode; // FT=true or Non-FT=false
		int		m_relayChannelRole;
    bool  m_isSctpTuclEnabled;

		eStackConfigState m_selfState;
		eStackConfigState m_peerState;

	private:

		INGwSmBlkConfig();
		~INGwSmBlkConfig();

		void stackConfigForPeer();
		void performStackConfig(vector<int> &procIdList, bool isForPeer);
		void executeStackInit();
		void enQ(msgQStruct* req);
		msgQStruct* dQ();
		void initializePeerNode(int aSpStIt = 0);
		void enaSgtrnLyrs(int from, int action = 0);

		static INGwSmBlkConfig *m_selfPtr;
		pthread_mutex_t          m_lock;
		pthread_mutex_t          m_Qlock;
		int						 m_relayStatCheckCnt;
		int						 m_relayRetryTime;
		bool					 m_peerEnabled;

		AddNetworkSeq 	m_nwList; 				// List of AddNetwork
		LnkSeq 			  	m_linkList; 			// List of AddLink
		LnkSeq 			  	m_peerLinkList; 	// List of AddLink with proc ID of peer.
																		  // Needed for disabling during FT
		LnkSetSeq 			m_linksetList; 	  // List of AddLinkSet
		RouteSeq 		  	m_routeList; 		  // List of AddRoute
		UserPartSeq 		m_userPartList; 	// List of AddUserPart
		GtRuleSeq 			m_gtRuleList; 		// List of AddGtRule
		AddrMapSeq 	  	m_gtAddrMapSeq;   //List of AddAddrMapCfg
		LocalSsnSeq 		m_localSsnList; 	//List of AddLocalSsn
		EpSeq 					m_epList; 				//List of AddEndPoint
		EpSeq 					m_peerEpList; 		//List of Peer EndPoint
		AspSeq 			  	m_aspList; 			  // List of AddPsp
		AsSeq 					m_asList; 				// List of AddPs
		M3uaAssocUpSeq 	m_assocUpList;		// List of M3uaAssocUp
		M3uaAssocUpSeq 	m_peerAssocUpList;// List of Peer M3uaAssocUp
		M3uaAspActSeq 	m_actAspList;     // List of Active ASP List
		M3uaAspActSeq 	m_peerActAspList; // List of Peer Active ASP List
};
#endif
