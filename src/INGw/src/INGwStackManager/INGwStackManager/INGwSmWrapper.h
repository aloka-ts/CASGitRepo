/************************************************************************
     Name:     INAP Stack Manager Wrapper - defines
 
     Type:     C include file
 
     Desc:     Defines required to access Stack Manager

     File:     INGwSmWrapper.h

     Sid:      INGwSmWrapper.h 0  -  03/27/03 

     Prg:      gs,bd

************************************************************************/

#ifndef __BP_AINSMWRAPPER_H__
#define __BP_AINSMWRAPPER_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"
#include "INGwStackManager/INGwSmStkReqRespDat.h"
#include "Util/QueueMgr.h"

#define GT_RULE_TTYPE_INDEX       0
#define GT_RULE_NMB_ACTNS_INDEX   1
#define GT_RULE_ACTN_STR_INDEX    2
#define GT_RULE_FIELDS            3

#define GT_RULE_ACTNTYPE_INDEX 0
#define GT_RULE_ACTN_SD_INDEX  1
#define GT_RULE_ACTN_ED_INDEX  2
#define GT_RULE_ACTN_FIELDS    3

#define GT_ADDRMAP_ACTNTYPE_INDEX   0
#define GT_ADDRMAP_GT_DIGITS_INDEX  1
#define GT_ADDRMAP_TTYPE_INDEX      2
#define GT_ADDRMAP_SD_INDEX         3
#define GT_ADDRMAP_ED_INDEX         4
#define GT_ADDRMAP_OUTADDRLST_INDEX 5
#define GT_ADDRMAP_FIELDS           6

#define GT_OUTADDR_PC     0
#define GT_OUTADDR_SSN    1
#define GT_OUTADDR_TTYPE  2
#define GT_OUTADDR_DIGITS 3
#define GT_OUTADDR_FIELDS 4


//forward reference
class INGwSmDistributor;
class INGwSmRepository;
class INGwTcapProvider;
class INGwSmStsHdlr;

#ifdef ALM_TESTING
class INGwSmAlmHdlr; // added for UT :BD
#endif /* ALM_TESTING */

/* 
 * This class is instantiated by the AIN Provider and a reference will be
 * maintained by it for invoking any further operations. Multiple instances
 * of this class can be created if needed or can even work as a singleton
 * but then the locking needs to be taken care of by the invoking 
 * component
 */
#define INGW_SS7_LINK_TIME_SLOT 0
#define INGW_SS7_PHY_PORT       1
#define INGW_SS7_SLC            2
#define INGW_SS7_MTP2_PROC_ID   3
#define INGW_SS7_FIELD_SIZE     4


#define INGW_SIGTRAN_EP_IP        0      
#define INGW_SIGTRAN_SRC_PORT     1
#define INGW_SIGTRAN_SCTP_PROC_ID 2
#define INGW_SIGTRAN_FIELD_SIZE   3

#define INGW_SIGTRAN_IP_REMOTE_PSP         0
#define INGW_SIGTRAN_PORT_REMOTE_PSP       1
#define INGW_SIGTRAN_REMOTE_PSP_FIELD_SIZE 2  

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwInfraUtil/INGwIfrUtlGlbFunc.h>

typedef struct stateAlarmInfo_t
{
  int state;
  bool sctAlarmEms;
  bool assFailAlarmEms;
}stateAlarmInfo;

class INGwSmWrapper : public INGwIfrUtlThread
{
  /* public operations */
  public:
  static mEndPoint;
  // default constructor
  INGwSmWrapper(INGwTcapProvider* apTcapProvider);

  //default destructor
  ~INGwSmWrapper();

  //initialize to be invoked once only to initialize the static objects
  int startUp (PAIFS16 apAinProvActvInit,
                  ActvTsk apAinProvActvTsk,
                  int aiAinProvSapId = 0);

  //to be invoked by TcapProvider for changeState 
  int changeState (const INGwIwfBaseProvider::ProviderStateType  aiState);

  //to be invoked by TcapProvider for any self oid change
  int configure (const char* apcOID, const char* apcValue, 
                 ConfigOpType aeOpType);

  //to be invoked by TcapProvider for any self oid change
  int oidChanged (const char* apcOID, const char* apcValue, 
                  ConfigOpType aeOpType, long alSubsystemId);

  //to be invoked by TcapProvider for Peer Failure
  int handlePeerFailure ();

  //bind the MTP3 to MTP2
  int enableNode ();

  //operation to be invoked by INGwSmRequest to unblock the thread
  int continueOperation (INGwSmBlockingContext* apBlkCtx);

  /*
   * The bind confirm for IU --> IE will come to AIN Provider
   * so AIN Provider will send the confirm to SM.
   */
  int handleIuBndCfm (int aiSuId, int aiStatus);

#ifdef ALM_TESTING
  // UT ONLY : BD
  INGwSmDistributor* getDist();
#endif /* ALM_TESTING */
  int addSs7Config (Ss7SigtranSubsReq *req);
  int addSigtranConfig (Ss7SigtranSubsReq *req);

  int cliAddNetwork(AddNetwork *nwk, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);
  int cliDisSsnSap(DisSap *sapIdList, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

  int cliGetStats(Stat *sts);

  int processEmsReq(Ss7SigtranSubsReq *req);

  int cliAddLink(AddLink *apParm, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

  int cliAddLinkSet(AddLinkSet *apParm, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

  int cliModLink(AddLink *apParm, Ss7SigtranStackResp **resp, vector<int> &procIdList );

  int cliModLinkSet(AddLinkSet *apParm, Ss7SigtranStackResp **resp,
                     vector<int> &procIdList);

  int cliAddUserPart(AddUserPart *apParm, Ss7SigtranStackResp **resp,
                     vector<int> &procIdList);

  int cliEnaLnk(LinkEnable *enalnk, Ss7SigtranStackResp **resp);
  int cliEnaAlarm(AlarmEnableDisable *alm, vector<int> &procIdList);
  int cliEnableNode(SgNode *enaNode, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliDisableNode(SgNode *disNode, vector<int> &procIdList);
  int cliDisableAlarm(AlarmEnableDisable *alrm, vector<int> &procIdList);
  int cliEnaDebug(DebugEnableDisable *enaDbg, vector<int> &procIdList);
  int cliEnaTrace(TraceEnableDisable *enaTrc, vector<int> &procIdList);
  int cliEnaLocalSsn(SsnEnable *enaSsn, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

  int cliEnaUsrpart(EnableUserPart *enaUsrPart, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

  int cliEnaEp(EnableEndPoint *enaEp, vector<int> &procIdList);
  int cliDisableLnk(LinkDisable *dislnk, Ss7SigtranStackResp **resp);
  int cliDisableDebug(DebugEnableDisable *disDbg, vector<int> &procIdList);

  int cliDisableTrace(TraceEnableDisable *disTrc, vector<int> &procIdList);

  int cliDisableLocalSsn(SsnDisable *disSsn, Ss7SigtranStackResp **resp,vector<int> &procIdList);
  int cliDisableUsrpart(DisableUserPart *disUsrPart, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliUnbndSapMtp3(DisableUserPart *disUsrPart, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliDisableEp(DisableEndPoint *enaEp, vector<int> &procIdList);

  int cliAddRoute(AddRoute *rte, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

  int cliBndM3ua(BindSap *bnd, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliBndSctp(BindSap *bnd, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliOpenEp(OpenEp * openEp, Ss7SigtranStackResp **resp);

  int cliAddLocalSsn(AddLocalSsn *l_ssn, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

  int cliAddRemoteSsn(AddRemoteSsn *r_ssn, vector<int> &procIdList);
  int cliAddAs(AddPs *as, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliModAs(AddPs *as, vector<int> &procIdList);
  int cliAddAsp(AddPsp *asp, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliAddGtRule(AddGtRule *gt, Ss7SigtranStackResp **resp,
													vector<int> &procIdList);

  int cliAddGtAddrMap(AddAddrMapCfg *gtMap,Ss7SigtranStackResp **resp,
                      vector<int> &procIdList);

  int cliAssocUp(M3uaAssocUp *apParm, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

  int cliAssocDn(M3uaAssocDown *apParm, Ss7SigtranStackResp **resp, vector<int> &procIdList);

  int cliAspUp(M3uaAspUp *apParm, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

  int cliAspDn(M3uaAspDown *apParm, Ss7SigtranStackResp **resp, vector<int> &procIdList);

  int cliAspActive(M3uaAspAct *apParm, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

#ifdef INC_ASP_SNDDAUD
  int cliSendDaud(Daud *apParm, Ss7SigtranStackResp **resp);
#endif

  int cliAddEndPoint(AddEndPoint *ep, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliAspInActive(M3uaAspInAct *apParm, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliDelLink(DelLink *lnk, Ss7SigtranStackResp **resp = NULL);
  int cliDelLinkSet(DelLinkSet *lnkSet, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

  int cliDelRoute(DelRoute *rte, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

  int cliDelNetwork(DelNetwork *nwk, Ss7SigtranStackResp **resp,
                    vector<int> &procIdList);

  int cliDelEp(DelEndPoint *ep, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliUnbndM3uaSaps(DelEndPoint *ep, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliDelAsp(DelPsp *asp, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliDelAs(DelPs *as, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliDelLocalSsn(DelLocalSsn *lSsn, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliDelRemoteSsn(DelRemoteSsn *rSsn, vector<int> &procIdList);
  int cliDelUsrPart(DelUserPart *up, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliStatusLink(LinkStatus *apParm, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliStatusLnkSet(LinkSetStatus *apParm, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliStatusRte(RouteStatus *apParm, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliStatusPs(PsStatus *apParm, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliStatusPsp(PspStatus *apParm, Ss7SigtranStackResp **resp, vector<int> &procIdList);
  int cliStatusNode(NodeStatus *apParm, vector<int> &procIdList);

  int cliDelGtRule(DelGtRule *gt, Ss7SigtranStackResp **resp,
                   vector<int> &procIdList);
  int cliDelGtAddrMap(DelAddrMapCfg *gtMap, Ss7SigtranStackResp **resp,
                      vector<int> &procIdList);
  
  void confSs7SigtranData();
  int enableDisableDebug(char *cmd, char *layer, U32 dbgMask);
  int enableDisableTrace(char *cmd, char *layer, U32 trcMask);
  void setStkLogMask(char *layer);
  void getStkLogMask(std::ostrstream &layerName);
  void getStatsMask(std::ostrstream &layerName);
  void getStackDbgMask(std::ostrstream &layerName);
  void setM3uaFlag();
  void getSwtchFromLocalSSN(S16 *ssnSwtch);
  int setStatsMask(long int statsMask);
  int processEmsReq(Ss7SigtranSubsReq *req, Ss7SigtranStackResp *&resp, int from=0);
  int addDebugInfo (int layer, int state);
  int getDebugState (int layer);
	int postMsg(INGwSmConfigQMsg *msg, bool chkFlag = false);

	int stub();
	int initializeNode(vector<int> &procIdList, int aSpStIt = 0);
	int enaSgtrnLyrs(vector<int> &procIdList, int action = 0);
	int initializeStats();
	int startSendStsReq();
	int initializeAlarms(vector<int> &procIdList);

  bool cliShutdownLayers(ShutDownLayers *shutdownLyrs,
                         Ss7SigtranStackResp **resp, 
                         vector<int> &procIdList);

  int getPspMap(int id);
  bool getPspMapAlarmSct(int id);
  bool getPspMapAlarmAssFail(int id);
  int setPspMap(int id, int state);
  int setPspMapAlarmSct(int id, bool alrm);
  int setPspMapAlarmAssFail(int id, bool alrm);
  int getEpMap(int id);
  int setEpMap(int id, int state);
  int sendAspUpCmd(int pspid, int sctsuid);
  int getBlockOperation(int id){
     int ret_val = blockOperation(id);
      return ret_val;
  } 

  int getRemoveBlockOperation(int id,INGwSmBlockingContext *(&apBlockingContext)){
      int ret_val = removeBlockingContext(id,*(&apBlockingContext));
      return ret_val;
  }

  char  relayServerIp[32];
  char  relayClientIp[32];

  /* protected operations */
  protected:

  INGwSmStsHdlr *mpStsReq;
  //block the thread
  int blockOperation (int aiRequestId, int timeOut=30);
 
  //add the blocking context in the map
  int addBlockingContext (int aiRequestId, 
                          INGwSmBlockingContext *apBlockingContext);

  //update the blocking context in the map
  int updateBlockingContext (int aiRequestId, 
                          INGwSmBlockingContext *apBlockingContext);

  //remove the blocking context
  int removeBlockingContext (int aiRequestId,
                             INGwSmBlockingContext *(&apBlockingContext));

	void proxyRun ();

 	bool cleanUpOnTimeOut(int requestId); 
	static int getContextId();
	static int mpContext;

  /* private operations */
  private:

	QueueManager m_configQ;
  bool disM3uaFlag;
  int debugArr[20];
  int miSleepTime;
  int miSleepCount;

	std::string m_protocol;

  // Specifies whether some stack config
  // is in progress. Either durign startup time
  // or during FT takeover or during PEER UP
  // So, under these conditions configuration 
  // through EMS shall be on hold till the  time
  // the configuration is over.
  bool m_allowCfgFromEms;
  pthread_rwlock_t m_AllowCfgEmsLock;

  /* public data members */
  public:
  INGwSmStsHdlr *getStsHdlrInst ()
  {
    return mpStsReq;
  }

  INGwSmDistributor *getDistInst ()
  {
    return mpSmDistributor;
  }

  void setAllowCfgFromEms(bool val, char * aMsg, int aLine);
  bool getAllowCfgFromEms(char * aMsg, int aLine);

  /* protected data members */
  protected:

  // lock to protect shared access to the map
  pthread_mutex_t mMapLock;

  // Condition variable (predicate="unblock now")
  pthread_cond_t  mUnblock;
  pthread_mutex_t mCvLock;
  pthread_mutex_t assocMapLock;


  // thread blocked in this requestId can proceed, other block again
  int mUnblockedRequestId;

  typedef std::map <int, INGwSmRetryAssoc *> INGwSmRetryAssocMap;
  INGwSmRetryAssocMap meRetryAssoc;


  typedef std::map <int, int > INGwSmDebugMap;
  INGwSmDebugMap meDebug;

  //typedef std::map <int, int > INGwSmPspMap;
  typedef std::map <int, stateAlarmInfo > INGwSmPspMap;
  INGwSmPspMap mePspState;

  typedef std::map <int, int > INGwSmEpMap;
  INGwSmEpMap meEpState;

  typedef std::map <int, INGwSmBlockingContext*> INGwSmBlockingMap;

  //the map which is storing the requestId and INGwSmRequest
  INGwSmBlockingMap mRequestBlockingMap;

  //Distributor used for posting the messages to the Stack Manager
  INGwSmDistributor *mpSmDistributor;

  //INGwTcapProvider reference to be passed to the distributor
  INGwTcapProvider *mpTcapProvider;

  /* private data members */
  private:
    std::map<int, int> linkIdProcIdMap;
    std::map<int, int> epIdProcIdMap;
};

#endif /* __BP_AINSMWRAPPER_H__ */
