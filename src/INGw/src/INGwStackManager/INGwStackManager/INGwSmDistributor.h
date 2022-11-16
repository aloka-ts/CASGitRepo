/************************************************************************
     Name:     INAP Stack Manager Distributor - defines
 
     Type:     C include file
 
     Desc:     Defines required to access Distributor for SM Queue

     File:     INGwSmDistributor.h

     Sid:      INGwSmDistributor.h 0  -  03/27/03 

     Prg:      gs,bd

************************************************************************/

#ifndef __BP_AINSMDISTRIBUTOR_H__
#define __BP_AINSMDISTRIBUTOR_H__

//include the various header files
#include "INGwStackManager/INGwSmIncludes.h"
#include "INGwStackManager/INGwSmRequest.h"
#include "Util/QueueMgr.h"

//forward reference
class INGwTcapProvider;
class INGwSmAdaptor;
class INGwSmWrapper;
class INGwSmRequestTable;
class INGwSmStsHdlr;
class INGwSmAlmHdlr;

/*
 * This class will be actually waiting on the Queue and then
 * invoking the corresponding classes for various types
 */

class INGwSmDistributor : public INGwIfrUtlThread
{
  /*
   * Public interface
   */
  public:

  //default constructor
  INGwSmDistributor(INGwSmWrapper *apSmWrapper,
                     INGwTcapProvider *apTcapProvider);

  //default destructor
  virtual ~INGwSmDistributor();

  //primitive used by other classes to post message to this Queue
  int postMsg (INGwSmQueueMsg *apQueueMsg, bool chkFlag = false);

  //initialize to be invoked once only to initialize the static objects
  int initialize();

  //to be invoked directly by INGwSmWrapper for changeState to LOADED
  int changeStateToLoaded (PAIFS16 afpActvInit, ActvTsk afpActvTsk, int aiSapId);

  //get access for SmUtils class
  INGwSmRepository* getSmRepository() { return mpRepository; }

  //get access for INGwTcapProvider
  INGwTcapProvider * getTcapProvider() { return mpTcapProvider; }

  //initialize the statistics handling
  int initStats ();

  //insert into Alarm wait list
  int waitForAlarm (int aiState, int aiTid);

  //remove from Alarm wait list
  int removeWaitForAlarm (int aiState, int aiTid);

  //sync up the PSP State in Stack and SM
  int syncPspState ();
  int assocUp_retry (int pspId, int sctSuId);

  //bring up the PSPs
  int bringUpPsp (int aiPspId);

  //register timer for a request
  int registerTimer (int aiDuration, int aiRequestId);

  //deregister a timer for a request
  int deregisterTimer (int aiDuration, int aiRequestId);

  //monitor resources
  int monitorResource ();

  //get the versions of the layers
  int getLayerVersion ();

  INGwSmAlmHdlr* getAlmHdlr();

  int updateRspStruct(int transId,StackReqResp *stackReq);
 
  int notifyCCM (INGwSmBlockingContext *apCtx);

  //move the SM to primary state
  int moveToPrimary ();
  int initToRunning ();
 
#ifdef INC_DLG_AUDIT 
  void 
  handleAuditCfm();

  int 
  getAuditCounter(){
    return miAuditCounter;
  }

  void
  resetAuditCounter(){
    miAuditCounter = 0;
  }
#endif
  /*
   * Protected interface
   */
  protected:

  //initialize the distributor in running state

  //main worker function to poll over the Queue
  void proxyRun ();

  //shutdown the statistics and the distributor thread
  int shutdown ();

  //to be invoked for changeState
  int handleChangeState(INGwSmQueueMsg *apQueMsg);

  //to be invoked for configure
  int handleConfigure(INGwSmQueueMsg *apQueMsg);

  //to be invoked for oidChanged
  int handleOidChanged(INGwSmQueueMsg *apQueMsg);

  //to be invoked for PeerFailed
  int handlePeerFailed(INGwSmQueueMsg *apQueMsg);

  //to be invoked for enabling MTP binding
  int handleEnableNode (INGwSmQueueMsg *apQueMsg);

  //to be invoked for stack alarms
  int handleAlarm(INGwSmQueueMsg *apQueMsg);

  //to be invoked for stack response
  int handleResponse(INGwSmQueueMsg *apQueMsg);

  //to be invoked for AlarmHandler raised alarm action requests
  int handleAlmAction(INGwSmQueueMsg *apQueMsg);

  //unblock the CCM Thread
  //int notifyCCM (INGwSmBlockingContext *apCtx);

  //dump Queue Msg
  void dumpQueueMsg (INGwSmQueueMsg *apMsg);


  //start the timer
  int startTimer();

  //stop the timer
  int stopTimer ();
  /*
   * Private interface
   */
  private:

#ifdef INC_DLG_AUDIT
  static int miAuditCounter;
#endif

  /*
   * Public Data Members
   */
  public:

  U16 selfProcId;
  int ss7SigtranStackRespPending;
  StackReqRespMap mStkReqRspMap;
  Ss7SigtranStackRespList emsRespList;

  /* set to 0 if configuration is from XML and 1 for EMS */
  U8 mConfigType;

  int maxAlarmCounter;

  INGwSmWrapper  *mpSmWrapper;

  INGwSmRequestTable *getRequestTable ()
  {
    return mpRequestTable;
  }
  
   
  /*
   * Protected Data Members
   */
  protected:

  //Ptr to INGwSmWrapper for invoking continueOperation()

  //Ptr to INGwSmAdaptor
  INGwSmAdaptor *mpSmAdaptor;

  //Queue for receiving messages from CCM or INGwSmAdaptor
  QueueManager mpSmQueue;

  //Table containing INGwSmRequests keyed on transactionId
  INGwSmRequestTable *mpRequestTable;

  //Utility functions
  INGwSmRepository  *mpRepository;

  //Alarm handler, initialized by c'tor with "this" ptr.
  INGwSmAlmHdlr  *mpAlmHdlr;

  //Tcap Provider reference
  INGwTcapProvider * mpTcapProvider;

  //make the queue thread safe
  pthread_mutex_t meQueueLock;

  //transaction id for STS HDLR
  int miStsTransId;
  //int miAuditTransId;

  //transaction id for distributor based transactions
  int miDistTransId;

  //transaction id for ChangeStateToRunning
  int miChangeState2RunningTransId;

  //identifier of Queue Message
  unsigned int miQueueId;

  /*
   * Define a multimap for storing the different request for alarms
   */

  typedef std::multimap <int, int> INGwSmIntIntMMap;

  INGwSmIntIntMMap meAlarmWaitMap;

  //define a list for timer registration and store the duration
  //as the ticks per resolution.

  typedef struct _INGwSmTimerNode {
    int id;
    int ticks;
    int expire;
    int requestId;
  } INGwSmTimerNode;

  std::vector <INGwSmTimerNode*> meTimerList ;

  //this will be max number of timers registered
  unsigned int miMaxTimer;

  //check for if timer is running
  bool mbIsTimerRunning;

  //check if someone deregistered the timer during handleResponse callback
  bool mbAnyTimerDeregistered;

  //check if the CCM is primary or not
  bool mbIsPrimary;

  /*
   * Private Data Members
   */
  private:

};


#endif /* __BP_AINSMDISTRIBUTOR_H__ */
