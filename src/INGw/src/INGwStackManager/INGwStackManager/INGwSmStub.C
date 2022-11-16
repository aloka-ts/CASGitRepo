//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

/************************************************************************
   Name:     INAP Stack Manager Stub - Impl
 
   Type:     C impl file
 
   Desc:     Implementation of the STUB of Stack Manager

   File:     INGwSmWrapper.C

   Sid:      INGwSmWrapper.C 0  -  03/27/03 

   Prg:      gs,bd

************************************************************************/

#include "INGwSmWrapper.h"

#ifdef STUBBED
/*
 * This is the stubbed out code needed for creating the binary of the stack manager
 * for unit testing purpose. It will only interact with the real Trillium stack 
 * and CCM etc will be stubbed out.
 * The main function will be driving the tests for now until i decide to change the UT flow
 */ 

#include "Util/LogMgr.h"

using namespace std;

class INGwTcapProvider
{
};


S16 IuActvInit 
(     
Ent      entity,              /* entity */
Inst     inst,                /* instance */
Region   region,              /* region */
Reason   reason               /* reason */
)     
{
  return ROK;
}

S16 IuActvTsk
(  
Pst *pst,                   /* post */
Buffer *mBuf                /* message buffer */
)  
{
  return ROK;
}

void
testASP (INGwSmWrapper *lpWrapper)
{

  sleep (15);

  if (lpWrapper->configure (SM_CCM_ASP_DOWN, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "ASP DN failed" << endl;
  else
    cout << "ASP DN successful" << endl;

  sleep (15);
  if (lpWrapper->configure (SM_CCM_ASP_ACTIVE, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "ASP ACTIVE failed" << endl;
  else
    cout << "ASP AC successful" << endl;

  sleep (15);
  if (lpWrapper->configure (SM_CCM_EST_ASS, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "EST ASS failed" << endl;
  else
    cout << "EST ASS successful" << endl;

  sleep (15);

  if (lpWrapper->configure (SM_CCM_ASP_INACTIVE, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "ASP IA failed" << endl;
  else
    cout << "ASP IAsuccessful" << endl;

  sleep (15);

  if (lpWrapper->configure (SM_CCM_ASP_DOWN, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "ASP DN failed" << endl;
  else
    cout << "ASP DN successful" << endl;

  sleep (15);

  if (lpWrapper->configure (SM_CCM_TRM_ASS, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "TRM ASS failed" << endl;
  else
    cout << "TRM ASSsuccessful" << endl;

  sleep (15);

  if (lpWrapper->configure (SM_CCM_EST_ASS, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "EST ASS failed" << endl;
  else
    cout << "EST ASS successful" << endl;

  sleep (15);

  if (lpWrapper->configure (SM_CCM_ASP_UP, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "ASP UP failed" << endl;
  else
    cout << "ASP UPsuccessful" << endl;

  sleep (15);

  if (lpWrapper->configure (SM_CCM_ASP_ACTIVE, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "ASP AC failed" << endl;
  else
    cout << "ASP AC successful" << endl;

  sleep (15);

  if (lpWrapper->configure (SM_CCM_ASP_DOWN, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "ASP DN failed" << endl;
  else
    cout << "ASP DN successful" << endl;

  sleep (15);

  if (lpWrapper->configure (SM_CCM_ASP_ACTIVE, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "ASP AC failed" << endl;
  else
    cout << "ASP AC successful" << endl;
}


#ifdef ALM_TESTING
INGwSmDistributor*
INGwSmWrapper::getDist()
{
  return mpSmDistributor;
}
#endif /* ALM_TESTING */



S16
IuLiIetOpenInd(Pst      *pst,       
	       SuId      suId,
	       IeDlgId   suDlgId,
	       IeDlgId   spDlgId, 
	       IeOpenEv *openEv) {

   RETVALUE(ROK);
}


S16
IuLiIetOpenCfm(Pst      *pst, 
	       SuId      suId,
	       IeDlgId   suDlgId, 
	       IeDlgId   spDlgId, 
	       IeOpenEv *openEv) {

   RETVALUE(ROK);
}

   
   
S16
IuLiIetDlgCfm(Pst      *pst,              /* post structure */
	      SuId      suId,             /* service user SAP id */
	      IeDlgId   suDlgId,          /* service user dialog id */
	      IeDlgId   spDlgId) {  /* service provider dialog id */

   
   RETVALUE(ROK);
}        

S16
IuLiIetDelimInd(Pst      *pst,              /* post structure */
		SuId      suId,             /* service user SAP id */
		IeDlgId   suDlgId,          /* service user dialog id */
		IeDlgId   spDlgId) {     /* service provider dialog id */

   
   RETVALUE(ROK);
}         

S16
IuLiIetCloseInd(Pst        *pst,            /* post structure */
		SuId        suId,           /* service user SAP id */
		IeDlgId     suDlgId,        /* service user dialog id */
		IeDlgId     spDlgId,        /* service provider dialog id */
		IeCloseEv  *closeEv) {      /* Close event structure */

   RETVALUE(ROK);
}       

S16
IuLiIetAbrtInd(Pst        *pst,            /* post structure */
	       SuId        suId,           /* service user SAP id */
	       IeDlgId     suDlgId,        /* service user dialog id */
	       IeDlgId     spDlgId,        /* service provider dialog id */
	       IeAbrtEv   *abrtEv) {       /* Abort event structure */

   RETVALUE(ROK);
}        

S16
IuLiIetNotInd(
	      Pst        *pst,            /* post structure */
	      SuId        suId,           /* service user SAP id */
	      IeDlgId     suDlgId,        /* service user dialog id */
	      IeDlgId     spDlgId,        /* service provider dialog id */
	      IeInvokeId *invId,          /* Invoke Id */
	      IeOprErr   *err,            /* Operation error */
	      U8          notSrc,         /* source */
	      RCause      cause,          /* SCCP Return Cause */
	      Buffer     *cpBuf) {        /* Component parameters buffer */

   RETVALUE(ROK);
}       

S16
IuLiIetOprInd(Pst        *pst,            /* post structure */
	      SuId        suId,           /* service user SAP id */
	      IeDlgId     suDlgId,        /* service user dialog id */
	      IeDlgId     spDlgId,        /* service provider dialog id */
	      IeInvokeId *invId,          /* Invoke Id */
	      IeInvokeId *lnkId,          /* Linked Invoke Id */
	      IeOprCode  *oprCode,        /* Operation Code */
	      PTR         oprEv,          /* Operation event structure */
	      Buffer     *uBuf) {       /* Unrecognized parameters buffer */
   
   RETVALUE(ROK);
}        

S16
IuLiIetOprCfm(Pst        *pst,          /* post structure */
	      SuId        suId,         /* service user SAP id */
	      IeDlgId     suDlgId,      /* service user dialog id */
	      IeDlgId     spDlgId,      /* service provider dialog id */
	      IeInvokeId *invId,        /* Invoke Id */
	      IeOprCode  *oprCode,      /* operation code */
	      IeOprErr   *oprErr,       /* Operation Error */
	      PTR         oprEv,        /* Operation event structure */
	      Buffer     *uBuf) {       /* Unrecognized parameters buffer */
   
   RETVALUE(ROK);
}       

S16
IuLiIetSteInd(Pst           *pst,         /* post structure */
	      SuId           suId,        /* service user SAP id */
	      CmSS7SteMgmt  *steMgmt) {   /* State structure */

   RETVALUE(ROK);
}   

S16
IuLiIetSteCfm(Pst           *pst,         /* post structure */
	      SuId           suId,        /* service user SAP id */
	      CmSS7SteMgmt  *steMgmt) {   /* State structure */

   RETVALUE(ROK);
}   

S16
IuLiIetBndCfm(Pst           *pst,         /* post structure */
	      SuId           suId,        /* service user SAP id */
	      U8             status       /* Status */
	      ) {
   
   RETVALUE(ROK);
}

int
main (int argc, char **argv)
{
  cout << "Starting the initialization of the stack Manager" << endl;

  /*
   * Need to initialize the Logger first.
   */
  AlarmReporterFunction alarmFunc=0;
  int i;
  for (i=1; i<argc;i++)
  {
    if (strcmp(argv[i], "-d") == 0)
      break;
  }

  char *p_lcfg;
  if (argc > i+1)
    p_lcfg = argv[i+1];
  if (p_lcfg == 0) p_lcfg = (char*)"./sm.lcfg";

  LogMgr::instance().init(p_lcfg, alarmFunc);

  /*
   * Create a INGwSmWrapper object and initialize it.
   */
  INGwTcapProvider *lpProvider = new INGwTcapProvider;

  cout << "Created the AIN Provider " << endl;

  INGwSmWrapper *lpWrapper = new INGwSmWrapper (lpProvider);

  cout << "Created the AIN Wrapper " << endl;

  if (lpWrapper->startUp (IuActvInit, IuActvTsk) == -1 )
    cout << "StartUp Failed" << endl;
  else
    cout << "StartUp Successful" << endl;

  cout << "Changing state to running " << endl;

  if (lpWrapper->changeState (BpProvider::PROVIDER_STATE_RUNNING) == -1)
    cout << "Change State to running failed" << endl;
  else
    cout << "Change State to running successful" << endl;

#if 0
  if (lpWrapper->configure (SM_CCM_DBG_LEVEL, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "Configure Debug failed" << endl;
  else
    cout << "Configure Debug successful" << endl;


  if (lpWrapper->configure (SM_CCM_TRC_LEVEL, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "Configure Trace failed" << endl;
  else
    cout << "Configure Trace successful" << endl;

  cout << "Configure Alarms" << endl;

  if (lpWrapper->configure (SM_CCM_ALM_LEVEL, "1", BpConfigurable::CONFIG_OP_TYPE_ADD) == -1)
    cout << "Configure Alarm failed" << endl;
  else
    cout << "Configure Alarm successful" << endl;
#endif


#if defined (_BP_AIN_SM_TESTING_)

  //testASP (lpWrapper);

#endif /* _BP_AIN_SM_TESTING_ */

#ifdef ALM_TESTING
  ItMgmt mgmt;

  cout  << "UT Test for Alarms.\n";

  mgmt.hdr.elmId.elmnt = STITPSP;
  mgmt.hdr.entId.ent  = ENTIT;
  mgmt.hdr.entId.inst = 0;
  mgmt.hdr.msgType = TUSTA;
  mgmt.hdr.transId = 0;
  mgmt.hdr.response.selector = 0;
  mgmt.hdr.response.prior = 0;
  mgmt.hdr.response.route = 0;
  mgmt.hdr.response.mem.region = BP_AIN_SM_REGION;
  mgmt.hdr.response.mem.pool = BP_AIN_SM_POOL;

  mgmt.cfm.status = LCM_PRIM_OK;
  mgmt.cfm.reason = LCM_REASON_NOT_APPL;

  SGetDateTime(&(mgmt.t.usta.alarm.dt));
  mgmt.t.usta.alarm.category = LCM_CATEGORY_INTERFACE;
  mgmt.t.usta.alarm.event = LCM_EVENT_BND_FAIL;
  mgmt.t.usta.alarm.cause = LCM_CAUSE_INV_STATE;

  mgmt.t.usta.s.suId = 1;

//  mgmt.t.usta.alarm.category = LIT_CATEGORY_STATUS;
//  mgmt.t.usta.alarm.event = LIT_EVENT_ASPM;
//  mgmt.t.usta.alarm.cause = LIT_CAUSE_MSG_RECEIVED;
//
//  mgmt.t.usta.s.pspId = 1;
//
//  mgmt.t.usta.t.aspm.msgType = LIT_ASPM_ASPDN_ACK;
//  mgmt.t.usta.t.aspm.reason = LIT_ASPDN_REASON_UNSPEC;
//  mgmt.t.usta.t.aspm.tmType = 0;
//  mgmt.t.usta.t.aspm.nmbPs = 1;
//  mgmt.t.usta.t.aspm.psLst[0] = 1;



  INGwSmAlarmMsg almMsg;
  almMsg.almInfo.miLayerId = BP_AIN_SM_M3U_LAYER;
  almMsg.almInfo.miTransId = 0;
  almMsg.almInfo.lyr.it = mgmt;    // ItMgmt

  ((lpWrapper->getDist())->getAlmHdlr())->handleAlarm(&almMsg);

#endif /* ALM_TESTING */

#if defined (_BP_AIN_SM_TESTING_)

/*
  sleep (15);

  cout << "handlePeerFailure" << endl;

  if (lpWrapper->handlePeerFailure () == -1)
    cout << "handlePeerFailure failed" << endl;
  else
    cout << "handlePeerFailure successful" << endl;

  sleep (15);

  cout << "Changestate to stopped" << endl;
  if (lpWrapper->changeState (BpProvider::PROVIDER_STATE_STOPPED) == -1)
    cout << "Change State to stopped failed" << endl;
  else
    cout << "Change State to stopped successful" << endl;

  delete lpWrapper;
  lpWrapper = 0;
*/
#endif /* _BP_AIN_SM_TESTING_ */


  pthread_mutex_t tmp;

  pthread_mutex_init (&tmp, 0);

  pthread_mutex_lock (&tmp);

  pthread_mutex_lock (&tmp);

  return 0;
}

#endif /* STUBBED */

