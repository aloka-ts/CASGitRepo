/************************************************************************
     Name:     INAP Stack Manager Alarm Handler - impl
 
     Type:     implementation
 
     Desc:     Implementation of the Alarm Handler

     File:     INGwSmAlmHdlr.C

     Sid:      INGwSmAlmHdlr.C 0  -  03/27/03 

     Prg:      bd,gs

************************************************************************/
//#include <CCMUtil/BpLogger.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwStackManager");

#include <signal.h>
#include "INGwSmAlmHdlr.h"
#include <INGwStackManager/INGwSmBlkConfig.h>
#include "INGwInfraParamRepository/INGwIfrPrParamRepository.h"
#include <INGwInfraManager/INGwIfrMgrManager.h>
#include <INGwInfraUtil/INGwIfrUtlGlbFunc.h>
#include <INGwFtPacket/INGwFtPktAspActive.h>
#include <INGwFtPacket/INGwFtPktAspInActive.h>

#if (defined (_BP_AIN_PROVIDER_) && !defined (STUBBED))
#include "INGwTcapProvider/INGwTcapProvider.h"
#else
class INGwTcapProvider
{
};
#endif

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
//#include "ccm/BpCCMAlarmMgr.h"
#include "INGwInfraManager/INGwIfrMgrAlarmMgr.h"
#endif



using namespace std;

extern "C"
void*
launchThrdToChkSuicide(void *arg)
{
  logger.logMsg(ALWAYS_FLAG, 0, "IN launchThrdToChkSuicide()");

  if (arg) {
    INGwSmBlkConfig* blk =  static_cast<INGwSmBlkConfig*> (arg);

    int sleepCnt = 20;
    int sleepTm = 200; // Milli secs.
    char * lcSleepCnt = getenv("SUICIDE_DET_CNT");
    if (lcSleepCnt) {
      sleepCnt = atoi(lcSleepCnt);
      if (sleepCnt == 0) {
        logger.logMsg(ALWAYS_FLAG, 0, 
               "OUT launchThrdToChkSuicide():envVar SUICIDE_DET_CNT is 0 so returning.");
        return 0;
      }
    }

    char * lcSleepTm = getenv("SUICIDE_SLEEP_TM");
    if (lcSleepTm) {
      sleepTm = atoi(lcSleepTm);
    }

    logger.logMsg(ALWAYS_FLAG, 0, 
           "launchThrdToChkSuicide():SUICIDE_DET_CNT[%d] SUICIDE_SLEEP_TM[%d MilliSecs.]",
           sleepCnt, sleepTm);

    while (sleepCnt > 0) {
      logger.logMsg(ALWAYS_FLAG, 0,
             "launchThrdToChkSuicide():Suicide sleep CntDown %d", sleepCnt);
      usleep(sleepTm * 1000);

		  if ( 1 == INGwTcapProvider::getInstance().myRole() ) {
        logger.logMsg(ALWAYS_FLAG, 0, 
               "OUT launchThrdToChkSuicide():Self Role ACTIVE. NOT COMMITING SUICIDE.");
        return 0;
      }
      sleepCnt--;
    }
  }
  else {
    logger.logMsg(ERROR_FLAG, 0, "launchThrdToChkSuicide():INGwSmBlkConfig NULL");
  }

  char lpcTime[64];
  memset(lpcTime, 0, sizeof(lpcTime));
  lpcTime[0] = '1';
  g_getCurrentTime(lpcTime);

  printf("[+INC+] launchThrdToChkSuicide(): %s "
         "Relay DOWN and self role is standby. COMMITING SUICIDE\n",
         lpcTime); fflush(stdout);
  
  logger.logMsg(ERROR_FLAG, 0, "launchThrdToChkSuicide():Relay DOWN "
      "and self role is standby. COMMITING SUICIDE"); fflush(stdout);

  raise(9);

  logger.logMsg(ALWAYS_FLAG, 0, "OUT launchThrdToChkSuicide()");
  return 0;
}

//default constructor
INGwSmAlmHdlr::INGwSmAlmHdlr(INGwSmDistributor *apDist)
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::INGwSmAlmHdlr");

  mpDist = apDist;
  _exitOnFailureFlag = false;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::INGwSmAlmHdlr");
}

void INGwSmAlmHdlr::_checkFailureExit()
{
   if(_exitOnFailureFlag)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Quitting on major AIN failure.");
      cout << "Quitting on major AIN failure.";
      // The delay has been added so that the alarm
      // reaches the EMS before CMM exits
      sleep(1);
      exit(1);
   }
}


//default destructor
INGwSmAlmHdlr::~INGwSmAlmHdlr()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::~INGwSmAlmHdlr");

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::~INGwSmAlmHdlr");
}


//initialize the handler
int 
INGwSmAlmHdlr::initialize()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::initialize");

  INGwSmRepository* lpRep = mpDist->getSmRepository();

  if (lpRep)
    _exitOnFailureFlag = lpRep->getAinMemProbExit ();

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::initialize");

  return (BP_AIN_SM_OK);
}


//process the alarm 
int 
INGwSmAlmHdlr::handleAlarm(INGwSmAlarmMsg *apAlmMsg)
{
  int ret = BP_AIN_SM_OK;   // return value

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleAlarm");

  if (apAlmMsg == 0) {
    return (BP_AIN_SM_FAIL);
  }

  switch (apAlmMsg->almInfo.miLayerId) {
       
    case BP_AIN_SM_TCA_LAYER:
      ret = handleStAlarm(&(apAlmMsg->almInfo.lyr.st));
      break;


    case BP_AIN_SM_SCC_LAYER:
      ret = handleSpAlarm(&(apAlmMsg->almInfo.lyr.sp));
      break;


    case BP_AIN_SM_M3U_LAYER:
      ret = handleItAlarm(&(apAlmMsg->almInfo.lyr.it));
      break;


    case BP_AIN_SM_SCT_LAYER:
      ret = handleSbAlarm(&(apAlmMsg->almInfo.lyr.sb));
      break;


    case BP_AIN_SM_TUC_LAYER:
      ret = handleHiAlarm(&(apAlmMsg->almInfo.lyr.hi));
      break;

    case BP_AIN_SM_MTP3_LAYER:
      ret = handleSnAlarm(&(apAlmMsg->almInfo.lyr.sn));
      break;

    case BP_AIN_SM_MTP2_LAYER:
      ret = handleSdAlarm(&(apAlmMsg->almInfo.lyr.sd));
      break;
    //ALM_HDLR add RY,ZT,ZV,ZN,ZP,Dn,Dv[
    
    case BP_AIN_SM_RY_LAYER:
      ret = handleRyAlarm(&(apAlmMsg->almInfo.lyr.ry));
      break;

    case BP_AIN_SM_LDF_M3UA_LAYER:
      ret = handleDvAlarm(&(apAlmMsg->almInfo.lyr.dv));
      break;

    case BP_AIN_SM_LDF_MTP3_LAYER:
      ret = handleDnAlarm(&(apAlmMsg->almInfo.lyr.dn));
      break;

    case BP_AIN_SM_PSF_TCAP_LAYER:
      ret = handleZtAlarm(&(apAlmMsg->almInfo.lyr.zt));
      break;

    case BP_AIN_SM_PSF_SCCP_LAYER:
      ret = handleZpAlarm(&(apAlmMsg->almInfo.lyr.zp));
      break;

    case BP_AIN_SM_PSF_MTP3_LAYER:
      ret = handleZnAlarm(&(apAlmMsg->almInfo.lyr.zn));
      break;

    case BP_AIN_SM_PSF_M3UA_LAYER:
      ret = handleZvAlarm(&(apAlmMsg->almInfo.lyr.zv));
      break;
   
    case BP_AIN_SM_SG_LAYER:
      ret = handleSgAlarm(&(apAlmMsg->almInfo.lyr.sg));
      break;

    case BP_AIN_SM_SH_LAYER:
      ret = handleShAlarm(&(apAlmMsg->almInfo.lyr.sh));
      break;

    case BP_AIN_SM_MR_LAYER:
      ret = handleMrAlarm(&(apAlmMsg->almInfo.lyr.mr));
      break;
 
    //Added ALM_HDLR RY,ZT,ZV,ZN,ZP,DN,DV,SG, SH, MR]
    
    default:
      return (BP_AIN_SM_FAIL);

  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handleAlarm");

  return (ret);
}


/*
 * Protected interface
 */
#if 0
// handle INAP alarms
int 
INGwSmAlmHdlr::handleIeAlarm(IeMngmt *mgmt)
{
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleIeAlarm");

  integ = MMkIntegVal(ENTIE, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTIE, LIE_EVT_001, LCM_CAUSE_UNKNOWN):
    case MMkIntegVal(ENTIE, LIE_EVT_009, LCM_CAUSE_UNKNOWN):
    case MMkIntegVal(ENTIE, LIE_EVT_014, LCM_CAUSE_UNKNOWN):
    case MMkIntegVal(ENTIE, LIE_EVT_015, LCM_CAUSE_UNKNOWN):
			logger.logMsg(ERROR_FLAG, 0, 
			"StackMgmt - Memory allocation failed during initialization.");
      _checkFailureExit();
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_002, LCM_CAUSE_UNKNOWN):
#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 									 __FILE__, __LINE__,
                   BP_AIN_SM_ALM_SW_FAIL,
                   BP_AIN_SM_ALMENT_SW, 0, 
                   " - Initialization failed.");
#endif
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_005, LCM_CAUSE_UNKNOWN):
			logger.logMsg(ERROR_FLAG, 0, "StkMgmt - Exceeded maximum active dialogs limit.");
      _checkFailureExit();
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_006, LCM_CAUSE_UNKNOWN):
			logger.logMsg(ERROR_FLAG, 0, "Stack management - Cannot allocate memory for dialog");
      _checkFailureExit();
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_007, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Could not allocate a new dialogID.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_008, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Hash list initialization for dialog failed.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_010, LCM_CAUSE_UNKNOWN):
			logger.logMsg(ERROR_FLAG, 0, "stkMgmt - Exceeded maximum active invokes.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_013, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Timer initialization failed.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_101, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
      "INAP_ALARM Layer - Could not insert entry in dialog hash-list <%d>.",
		 	mgmt->t.usta.info.dlgId);
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_102, LCM_CAUSE_UNKNOWN):
    logger.logMsg (ERROR_FLAG, 0, 
		"INAP_ALARM_ Layer - Could not insert entry in invoke hash-list. <%d, %d>",
                    mgmt->t.usta.info.dlgId, mgmt->t.usta.info.invokeId);
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_103, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Could not delete entry in dialog hash-list <%d>.",
                     mgmt->t.usta.info.dlgId);
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_104, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Invalid dialogID <%d> to be freed.",
                     mgmt->t.usta.info.dlgId);
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_105, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Duplicate dialogID for this operation <%d, %d>.",
                     mgmt->t.usta.info.dlgId, mgmt->t.usta.info.invokeId);
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_302, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Wrong entity id.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_303, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Wrong instance id.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_308, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - SAP configuration before general configuration.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_312, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Out of bounds INSAP id.");
      break;
    case MMkIntegVal(ENTIE, LIE_EVT_313, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Unconfigured INSAP id.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_314, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Illegal state of INSAP id.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_315, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Out of bounds TCSAP id.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_316, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Unconfigured TCSAP id.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_317, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Illegal state of TCSAP id.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_318, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Bad dialog id <%d, %d>.", 
                     mgmt->t.usta.info.dlgId, mgmt->t.usta.info.param);
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_319, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Invoked id missing. <%d>",
                     mgmt->t.usta.info.dlgId);
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_321, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - Invalid element value in LM request.");
      break;

/** clash with LIE_EVT_321 (same value as LIE_EVT_401 *********************
    case MMkIntegVal(ENTIE, LIE_EVT_401, LCM_CAUSE_UNKNOWN):
      logger.logMsg (TRACE_FLAG, 0, 
                     "INAP_ALARM_ Layer - General configuration successful.");
      break;
**************************************************************************/

    case MMkIntegVal(ENTIE, LIE_EVT_402, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - IN SAP configuration successful.");
      break;

    case MMkIntegVal(ENTIE, LIE_EVT_403, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "INAP_ALARM_ Layer - TC SAP configuration successful.");
      break;

    default:
      logger.logMsg(ERROR_FLAG, 0,
                    "INAP_ALARM_ Layer - Unrecognized event : %u , cause : %u",
                    mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handleIeAlarm");

  return (BP_AIN_SM_OK);
}
#endif


// handle TCAP alarms
int 
INGwSmAlmHdlr::handleStAlarm(StMngmt *mgmt)
{
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleStAlarm");

  integ = MMkIntegVal(ENTST, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTST, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_INV_SAP):
    case MMkIntegVal(ENTST, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_INV_SPID):
    case MMkIntegVal(ENTST, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_PROT_NOT_ACTIVE):
    case MMkIntegVal(ENTST, LCM_EVENT_UI_INV_EVT, LST_CAUSE_SAP_UBND):
      logger.logMsg (ERROR_FLAG, 0, 
                     "TCAP_ALARM_ Layer - Upper interface error.");
      break;

    case MMkIntegVal(ENTST, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_INV_SAP):
    case MMkIntegVal(ENTST, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_INV_SUID):
    case MMkIntegVal(ENTST, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_PROT_NOT_ACTIVE):
    case MMkIntegVal(ENTST, LCM_EVENT_LI_INV_EVT, LST_CAUSE_SAP_UBND):
      logger.logMsg (ERROR_FLAG, 0, 
                     "TCAP_ALARM_ Layer - Lower interface error.");
      break;

    case MMkIntegVal(ENTST, LCM_EVENT_MI_INV_EVT, LCM_CAUSE_INV_SAP):
    case MMkIntegVal(ENTST, LCM_EVENT_MI_INV_EVT, LCM_CAUSE_INV_SPID):
    case MMkIntegVal(ENTST, LCM_EVENT_MI_INV_EVT, LCM_CAUSE_PROT_NOT_ACTIVE):
      logger.logMsg (ERROR_FLAG, 0, 
                     "TCAP_ALARM_ Layer - Layer Manager interface error.");
      break;

    case MMkIntegVal(ENTST, LCM_EVENT_INV_TMR_EVT, LCM_CAUSE_PROT_NOT_ACTIVE):
      logger.logMsg (ERROR_FLAG, 0, 
                     "TCAP_ALARM_ Layer - Illegal/unexpected timer expiry.");
      break;

    case MMkIntegVal(ENTST, LST_EVENT_GEN_UCFGOK, LCM_CAUSE_UNKNOWN):
      logger.logMsg (VERBOSE_FLAG, 0, 
                     "TCAP_ALARM_ Layer - Unconfigure successful.");
      break;

    case MMkIntegVal(ENTST, LST_EVENT_MAND_MIS, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "TCAP_ALARM_ Layer - Mandatory element is missing in component/dialog");
      break;

    case MMkIntegVal(ENTST, LST_EVENT_DUP_INVID, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "TCAP_ALARM_ Layer - invokeID in Component(invoke) reqested, already in use.");
      break;

    case MMkIntegVal(ENTST, LST_EVENT_UR_INVID, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "TCAP_ALARM_ Layer - invokeID in Component(non-invoke) reqested, does not exist.");
      break;

    case MMkIntegVal(ENTST, LST_EVENT_INV_COMPEV, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "TCAP_ALARM_ Layer - Invalid Component received.");
      break;

    case MMkIntegVal(ENTST, LST_EVENT_UX_MSG, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "TCAP_ALARM_ Layer - Data request expected in current Transaction-state.");
      break;

    case MMkIntegVal(ENTST, LST_EVENT_UNKN_MSG, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "TCAP_ALARM_ Layer - Unknown message type in Data request.");
      break;

    case MMkIntegVal(ENTST, LST_EVENT_ALOC_DLGID_FAIL, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                   "TCAP_ALARM_ Layer - Entire dialog-id range is used up.");
      _checkFailureExit();

      break;

    case MMkIntegVal(ENTST, LCM_EVENT_BND_FAIL, LCM_CAUSE_UNKNOWN):
			logger.logMsg(ERROR_FLAG, 0, 
			"TCAP Layer: Bind with lower layer failed.");
      break;

    case MMkIntegVal(ENTST, LST_EVENT_MAX_CFG, LST_CAUSE_DLG_ALOC):
			logger.logMsg(ERROR_FLAG, 0,
			"TCAP Layer: Max no. of dialogs reached.");
      _checkFailureExit();
      break;

    case MMkIntegVal(ENTST, LST_EVENT_MAX_CFG, LST_CAUSE_INV_ALOC):
			logger.logMsg(ERROR_FLAG, 0,
			"TCAP Layer: Max no. of invokes reached.");
      break;

    case MMkIntegVal(ENTST, LST_EVENT_HASH_FAIL, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                     "TCAP_ALARM_ Layer - Hash table function failed.");

      break;

    case MMkIntegVal(ENTST, LST_EVENT_MSG_FAIL, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                   "TCAP_ALARM_ Layer - Dynamic memory alloc/dealloc failed.");

      _checkFailureExit();
      break;

    case MMkIntegVal(ENTST, LST_EVENT_ALOC_FAIL, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0, 
                   "TCAP_ALARM_ Layer - Static buffer alloc/dealloc failed.");
      _checkFailureExit();

      break;

    default:
      logger.logMsg(TRACE_FLAG, 0,
                    "TCAP_ALARM_ Layer - Unrecognized event : %u , cause : %u",
                    mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handleStAlarm");

  return (BP_AIN_SM_OK);
}


// handle SCCP alarms
int 
INGwSmAlmHdlr::handleSpAlarm(SpMngmt *mgmt)
{
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleSpAlarm");

  integ = MMkIntegVal(ENTSP, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTSP, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_PROT_NOT_ACTIVE):
    case MMkIntegVal(ENTSP, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_INV_SPID):
    case MMkIntegVal(ENTSP, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_OUT_OF_RANGE):
      logger.logMsg (ERROR_FLAG, 0, 
                     "SCCP_ALARM_ Layer - Invalid upper interface event.");
      break;

    case MMkIntegVal(ENTSP, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_PROT_NOT_ACTIVE):
    case MMkIntegVal(ENTSP, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_INV_SUID):
    case MMkIntegVal(ENTSP, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_OUT_OF_RANGE):
      logger.logMsg (ERROR_FLAG, 0, 
                     "SCCP_ALARM_ Layer - Invalid lower interface event.");
      break;

    case MMkIntegVal(ENTSP, LSP_EVENT_SYN_ERROR, LCM_CAUSE_INV_NETWORK_MSG):
    case MMkIntegVal(ENTSP, LCM_EVENT_INV_EVT, LCM_CAUSE_OUT_OF_RANGE):
    case MMkIntegVal(ENTSP, LCM_EVENT_INV_EVT, LCM_CAUSE_DECODE_ERR):
    case MMkIntegVal(ENTSP, LCM_EVENT_INV_EVT, LCM_CAUSE_INV_NETWORK_MSG):
      logger.logMsg (ERROR_FLAG, 0, 
                     "SCCP_ALARM_ Layer - Errorenous/Invalid  message received from network.");
      break;

    case MMkIntegVal(ENTSP, LCM_EVENT_BND_FAIL, LCM_CAUSE_UNKNOWN):
			logger.logMsg(ERROR_FLAG, 0, 
			"SCCP Layer: Bind failed with M3UA/MTP3 layer.");
      break;

    case MMkIntegVal(ENTSP, LSP_EVENT_USER_INS, LCM_CAUSE_USER_INITIATED):
      logger.logMsg (ERROR_FLAG, 0, 
						 "SCCP_ALARM_ Layer - SCCP User came inservice. User initiated.");
      break;

    case MMkIntegVal(ENTSP, LSP_EVENT_USER_OOS, LCM_CAUSE_USER_INITIATED):
      logger.logMsg (ERROR_FLAG, 0, 
                     "SCCP_ALARM_ Layer - SCCP User going out of service. User initiated.");
      break;

    case MMkIntegVal(ENTSP, LSP_EVENT_ROUTING_ERR, LSP_CAUSE_INV_ROUTE):
      logger.logMsg (ERROR_FLAG, 0, 
                     "SCCP_ALARM_ Layer - Routing error. Invalid route or route not available.");
      break;

    case MMkIntegVal(ENTSP, LSP_EVENT_HOP_VIOLATION, LCM_CAUSE_OUT_OF_RANGE):
      logger.logMsg (ERROR_FLAG, 0, 
                     "SCCP_ALARM_ Layer - Message discarded due to hop counter violation.");
      break;

    default:
      logger.logMsg (ERROR_FLAG, 0,
                     "SCCP_ALARM_ Layer - Unrecognized event : %u , cause : %u",
                     mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handleSpAlarm");

  return (BP_AIN_SM_OK);
}


// handle M3UA alarms
int 
INGwSmAlmHdlr::handleItAlarm(ItMgmt  *mgmt)
{
  int pspId_m3uaSapId;
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleItAlarm");

  integ = MMkIntegVal(ENTIT, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  char lpcTime[64];

  switch (integ) {

    case MMkIntegVal(ENTIT, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_INV_PAR_VAL): 
    case MMkIntegVal(ENTIT, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_INV_SPID): 
    case MMkIntegVal(ENTIT, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_INV_MSG_LENGTH): 
    case MMkIntegVal(ENTIT, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_INV_STATE):
      logger.logMsg (ERROR_FLAG, 0,
                     "M3UA_ALARM_ Layer - Illegal/invalid event is rx'd at the upper interface.");
      break;

    case MMkIntegVal(ENTIT, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_INV_STATE): 
      logger.logMsg (ERROR_FLAG, 0,
                     "M3UA_ALARM_ Layer - Invalid state is rx'd at the lower interface.");
      break;
    case MMkIntegVal(ENTIT, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_INV_SAP):
      logger.logMsg (ERROR_FLAG, 0,
                     "M3UA_ALARM_ Layer - Invalid sap is rx'd at the lower interface.");
      break;

    case MMkIntegVal(ENTIT, LCM_EVENT_BND_FAIL, LCM_CAUSE_INV_STATE):
    case MMkIntegVal(ENTIT, LCM_EVENT_BND_FAIL, LIT_CAUSE_RETRY_EXCEED):
    case MMkIntegVal(ENTIT, LCM_EVENT_BND_FAIL, LCM_CAUSE_UNKNOWN):
			logger.logMsg(ERROR_FLAG, 0, 
			"M3UA_ALARM_ Layer: Bind procedure with SCTP Failed.");
      break; 

    case MMkIntegVal(ENTIT, LCM_EVENT_BND_OK, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ALWAYS_FLAG, 0,
						 "M3UA_ALARM_ Layer - Bind procedure with SCTP layer succeeded.");
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_MSG_FAIL, LCM_CAUSE_UNKNOWN):
    case MMkIntegVal(ENTIT, LIT_EVENT_MSG_FAIL, LCM_CAUSE_MEM_ALLOC_FAIL):
      logger.logMsg (ERROR_FLAG, 0,
                   "M3UA_ALARM_ Layer - Attempt to allocate or deallocate dynamic memory failed."); 
      _checkFailureExit();

    case MMkIntegVal(ENTIT, LIT_EVENT_ESTABLISH_FAIL, LCM_CAUSE_TMR_EXPIRED):
    {
      logger.logMsg (ERROR_FLAG, 0,
		 	"M3UA_ALARM_ Layer - Association establishment failed. pspId:%u and sctSuId:%d",
      mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);

      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
    
      bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmAssFail(pspId_m3uaSapId);

      if(alrmstate == false){ 
        INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_ASSOC_EST_FAILED, //+tag+
                   "Assoc Establishment Failed", 0,
                    " with pspId <%u>",
                    mgmt->t.usta.s.pspId);
          mpDist->mpSmWrapper->setPspMapAlarmAssFail(pspId_m3uaSapId, true);
      }
#endif
      mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOCUP_FAIL);

      handlePspAlmOp(BP_AIN_SM_ALMOP_ASSOC_ESTAB_FAIL,
                     mgmt->t.usta.s.pspId, 0, mgmt->t.usta.t.aspm.sctSuId);

      /*pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
      mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASPUP_DISCONN); 

      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_REMOTE_SHUTDN,
                        mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);*/
      }
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_ESTABLISH_FAIL, LIT_CAUSE_DUP_ASSOC):
      logger.logMsg (ERROR_FLAG, 0,
		 	"M3UA_ALARM_ Layer - Association establishment failed cause: Duplicate assoc:. pspId:%u and sctSuId:%d",
      mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);

      //handlePspAlmOp(BP_AIN_SM_ALMOP_ASSOC_ESTAB_FAIL,
      //               mgmt->t.usta.s.pspId, 0, mgmt->t.usta.t.aspm.sctSuId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_ESTABLISH_OK, LCM_CAUSE_UNKNOWN):
		{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			memset(&buf, 0, sizeof(buf));
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg (ALWAYS_FLAG, 0, "M3UA_ALARM_ Layer "
			"- Association establishment succeeded (Unknown). pspId:%u, sctsuid:%d"
			", found:%d, ip:%s", mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId, found, 
			(IPAddr.empty() == true)?"NULL":IPAddr.c_str());

      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
		if(!IPAddr.empty())
		{
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_ASSOC_EST_OK, //+tag+
                   "Assoc Establishment Success", 0,
                    " with Remote IP:%s and Port:%d", 
										IPAddr.c_str(), psp.dstPort);
      mpDist->mpSmWrapper->setPspMapAlarmAssFail(pspId_m3uaSapId, false);
		}
#endif

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_SCTP_COMM_OK, //+tag+
                   "Sctp Comm OK", 0,
                    "M3UA_ALARM_ Layer -  Assoc Establishment Success pspId[%u] .",
                    mgmt->t.usta.s.pspId);
      mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, false);
#endif

      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);
      if((state == ASSOCUP_SENT) || (state == ASSOCUP_FAIL ))
      {
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOCUP_DONE);
        mpDist->mpSmWrapper->sendAspUpCmd(mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);
      }
      else
      {
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOCUP_DONE);
      }

      handlePspAlmOp(BP_AIN_SM_ALMOP_ASSOC_ESTAB_OK,
                     mgmt->t.usta.s.pspId, 0, 0);

#if 0
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);
      if(state == ASPUP_DISCONN)
      {
        mpDist->mpSmWrapper->sendAspUpCmd(mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);
      }
      else if(state == ASSOC_DISABLE)
      {
        logger.logMsg (ALWAYS_FLAG, 0,
		      "M3UA_ALARM_ Layer - Association establishment OK, but Local Assoc" 
          "state is DOWN pspId:%d sctSuId:%d", mgmt->t.usta.s.pspId,
          mgmt->t.usta.t.aspm.sctSuId);
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASPUP_DISCONN);
      }
#endif

		 }
     break;

    case MMkIntegVal(ENTIT, LIT_EVENT_ESTABLISH_OK, SCT_STATUS_COMM_UP):
    {
      logger.logMsg (ALWAYS_FLAG, 0,
		  "M3UA_ALARM_ Layer - Association establishment succeeded (Comm Up). "
			"pspId:%u sctSuId:%d", mgmt->t.usta.s.pspId,mgmt->t.usta.t.aspm.sctSuId );

      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
      mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOCUP_DONE);

      handlePspAlmOp(BP_AIN_SM_ALMOP_ASSOC_ESTAB_OK,
                     mgmt->t.usta.s.pspId, 0, 0);

#if 0
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);
      if(state == ASPUP_DISCONN)
      {
        mpDist->mpSmWrapper->sendAspUpCmd(mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);
      }
      else if(state == ASSOC_DISABLE)
      {
        logger.logMsg (ALWAYS_FLAG, 0,
		      "handleItAlarm() - Association establishment OK, but Local Assoc" 
          "state is DOWN pspId:%u sctSuId:%d", mgmt->t.usta.s.pspId,
          mgmt->t.usta.t.aspm.sctSuId);
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASPUP_DISCONN);
      }
#endif
    } 
    break;

    case MMkIntegVal(ENTIT, LIT_EVENT_ESTABLISH_OK, SCT_STATUS_NET_UP):
      logger.logMsg (ALWAYS_FLAG, 0,
		  "M3UA_ALARM_ Layer - Association establishment succeeded (Network Up). "
			"pspId:%u", mgmt->t.usta.s.pspId);

      handlePspAlmOp(BP_AIN_SM_ALMOP_ASSOC_ESTAB_OK,
                     mgmt->t.usta.s.pspId,0, 0);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_ASPM, LIT_CAUSE_MSG_RECEIVED):
    {
      logger.logMsg(ERROR_FLAG, 0,
	 	  "M3UA_ALARM_ Layer - ASPM ACK message (type:%u,trfMo:%u)"
	 		"is received from a remote M3UA peer.", mgmt->t.usta.t.aspm.msgType,
	 		mgmt->t.usta.t.aspm.tmType);

//#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
//      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
//                   __FILE__, __LINE__,
//                   INC_SM_ALM_ASPM_ACK,
//                   "Aspm Ack", 0,
//                    " - M3UA_ALARM_ Layer: ASPM ACK type[%u],trfMo[%u]",
//                    mgmt->t.usta.t.aspm.msgType,
//                    mgmt->t.usta.t.aspm.tmType);
//#endif
			logger.logMsg(ERROR_FLAG, 0, 
			"M3UA_ALARM_ Layer: ASPM ACK type[%u],trfMo[%u]", mgmt->t.usta.t.aspm.msgType,
				mgmt->t.usta.t.aspm.tmType);


      handleAspmAckCase(mgmt);

      //posting ASP_ACTV if ASPUP_ACK is successful
      if(mgmt->t.usta.t.aspm.msgType == LIT_ASPM_ASPUP_ACK){
      //sending alarm to EMS
#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
	 		__FILE__, __LINE__, INC_SM_ALM_ASP_UP, "ASP Up", 0,
			" - M3UA_ALARM_ Layer: Asp Up  ");
#endif

        logger.logMsg (ALWAYS_FLAG, 0,
                "handleItAlarm()::ASPM received for LIT_ASPM_ASPUP_ACK");

        pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASPUP_DONE); 
        sendAspActvCmd(mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);
        //mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASPACTV_SENT); 
      }
      if(mgmt->t.usta.t.aspm.msgType == LIT_ASPM_ASPAC_ACK){
        logger.logMsg (ALWAYS_FLAG, 0,
                "handleItAlarm()::ASPM received for LIT_ASPM_ASPAC_ACK");
        pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASPACTV_DONE); 

        //update peer
        M3uaAspAct asp;
        memset(&asp, '\0',sizeof(M3uaAspAct)); 
        asp.pspId = mgmt->t.usta.s.pspId;
        asp.m3uaLsapId = mgmt->t.usta.t.aspm.sctSuId;

        int li_selfId = INGwIfrPrParamRepository::getInstance().getSelfId();
	      int li_peerId = INGwIfrPrParamRepository::getInstance().getPeerId();

        int size = INGwSmBlkConfig::getInstance().addActvAsp(asp);
        if(size != 0 && li_peerId !=0)
        {
          INGwFtPktAspActive ftAspAct;
				  ftAspAct.initialize(li_selfId, li_peerId, asp);
				  INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAspAct);
        }

      }
    }

    break;

    case MMkIntegVal(ENTIT, LIT_EVENT_NOTIFY, LIT_CAUSE_MSG_RECEIVED):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Notify message is received from a remote "
			"M3UA peer. type:%u,stinfo:%u", mgmt->t.usta.t.ntfy.stType,
	 		mgmt->t.usta.t.ntfy.stInfo);

      if ((mgmt->t.usta.t.ntfy.stType == LIT_NTFY_TYPE_ASCHG) &&
          (mgmt->t.usta.t.ntfy.stInfo == LIT_NTFY_INFO_AS_PENDING)) {

        handleAsPendingAlm();

      }
      break;

#if 0
    case MMkIntegVal(ENTIT, LIT_EVENT_PC_UNAVAILABLE, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - PC UNAVAILABLE message is received from remote "
			"M3UA peer. APC:0x%x", mgmt->t.usta.t.dpcEvt.aPc);
	 		printf("[+INC+] M3UA_ALARM_ Layer - PC UNAVAILABLE received from "
             "remote M3UA peer. APC:0x%x\n", mgmt->t.usta.t.dpcEvt.aPc);

      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_PC_CONGESTED, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - PC CONGESTED received from remote "
			"M3UA peer. APC:0x%x CongLevel:0x%x", mgmt->t.usta.t.dpcEvt.aPc,
       mgmt->t.usta.t.dpcEvt.p.congLevel);

	 		printf("[+INC+] M3UA_ALARM_ Layer - PC CONGESTED received from remote "
			       "M3UA peer. APC:0x%x CongLevel:0x%x\n", mgmt->t.usta.t.dpcEvt.aPc,
              mgmt->t.usta.t.dpcEvt.p.congLevel);

      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_PC_AVAILABLE, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - PC AVAILABLE received from remote "
			"M3UA peer. APC:0x%x", mgmt->t.usta.t.dpcEvt.aPc);

	 		printf("[+INC+] M3UA_ALARM_ Layer - PC AVAILABLE received from remote "
			       "M3UA peer. APC:0x%x\n", mgmt->t.usta.t.dpcEvt.aPc);

      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_PC_USER_PART_UNA, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - PC USER PART UNAVAILABLE received from remote "
			"M3UA peer. APC:%u, servInd:0x%x\n", mgmt->t.usta.t.dpcEvt.aPc,
      mgmt->t.usta.t.dpcEvt.p.servInd);

	 		printf("[+INC+] M3UA_ALARM_ Layer - PC USER PART UNAVAILABLE "
             "received from remote M3UA peer. APC:%u, servInd:0x%x\n",
             mgmt->t.usta.t.dpcEvt.aPc, mgmt->t.usta.t.dpcEvt.p.servInd);

      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_NO_ROUTE_FOUND, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - PC NO ROUTE FOUND received from remote "
			"M3UA peer. APC:0x%x", mgmt->t.usta.t.dpcEvt.aPc);

	 		printf("[+INC+] M3UA_ALARM_ Layer - PC NO ROUTE FOUND received "
             "from remote M3UA peer. APC:0x%x\n", mgmt->t.usta.t.dpcEvt.aPc);

      break;
    
    case MMkIntegVal(ENTIT, LIT_EVENT_PC_RESTRICTED, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - PC RESTRICTED received from remote "
			"M3UA peer. APC:0x%x", mgmt->t.usta.t.dpcEvt.aPc);

	 		printf("[+INC+] M3UA_ALARM_ Layer - PC RESTRICTED received from remote "
			       "M3UA peer. APC:0x%x\n", mgmt->t.usta.t.dpcEvt.aPc);

      break;
#endif

    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_INV_VERSION):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_INV_NWK_APP):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_UNSUP_MSG_CLASS):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_UNSUP_MSG_TYPE):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_INV_TRAFFIC_MODE):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_UNEXP_MSG):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_PROTO_ERR):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_INV_RCTX):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_INV_STRMID):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_INV_M3UA_PARMVAL):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_RFSD_MGMT_BLKD):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_PARAM_FIELD_ERROR):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_NO_AS_FOR_ASP):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_UNEXPECTED_PARAM):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_MISSING_PARAM):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_ASPID_REQUIRED):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_INVALID_ASPID):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LCM_CAUSE_UNKNOWN):
    case MMkIntegVal(ENTIT, LIT_EVENT_M3UA_PROTO_ERROR, LIT_CAUSE_DPC_STATUS_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
      "M3UA_ALARM_ Layer - Error detected in an M3UA protocol element. "
      "PspId:%u Cause:%u", mgmt->t.usta.s.pspId, mgmt->t.usta.alarm.cause);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_ASP_DOWN, LIT_CAUSE_STATUS_CHANGE):
    {

      //find hdr.elmnt
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      M3uaAspInAct asp;
      memset(&asp,'\0',sizeof(M3uaAspInAct));
      int elementId = mgmt->hdr.elmId.elmnt;

      if(elementId == STITPSP)
      {
#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
        INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
        __FILE__, __LINE__, INC_SM_ALM_ASP_DOWN, "ASP Down", 0,
        " - M3UA_ALARM_ Layer: Remote Asp Down ");
#endif

      }
      bool aspFound = INGwSmBlkConfig::getInstance().getAsp 
                         (mgmt->t.usta.s.pspId, psp);
      if(aspFound)
      {
        logger.logMsg(ERROR_FLAG, 0,
        "M3UA_ALARM_ Layer - Internal state remote ASP DOWN. pspId:%u "
        "and sctSuId:%d", mgmt->t.usta.s.pspId, 
        mgmt->t.usta.t.aspm.sctSuId);
       
//#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
//        INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
//        __FILE__, __LINE__, INC_SM_ALM_ASP_DOWN, "ASP Down", 0,
//        " - M3UA_ALARM_ Layer: Remote Asp Down pspId:%u and psId:%d",
//        mgmt->t.usta.s.pspId , mgmt->t.usta.s.psId);
//#endif

        //mpDist->mpSmWrapper->setM3uaFlag();

        //delete it from blkconfig map also and send this info to peer, starts
        int li_selfId = INGwIfrPrParamRepository::getInstance().getSelfId();
        int li_peerId = INGwIfrPrParamRepository::getInstance().getPeerId();
        asp.pspId     = mgmt->t.usta.s.pspId;
        asp.m3uaLsapId  = mgmt->t.usta.t.aspm.sctSuId;

        INGwSmBlkConfig::getInstance().delActvAsp(asp);

        if(li_peerId !=0)
        {
          INGwFtPktAspInActive ftAsp;
          ftAsp.initialize(li_selfId, li_peerId, asp);
          INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAsp);
        }
        //delete it from blkconfig map also and send this info to peer, ends

        handlePspAlmOp(BP_AIN_SM_ALMOP_ASP_ST_UPD_DOWN,
                      mgmt->t.usta.s.pspId, mgmt->t.usta.s.psId,0);
 
      }
      else
      {
        logger.logMsg(ERROR_FLAG, 0,
        "M3UA_ALARM_ Layer - Internal state remote ASP DOWN. psAspEvt.pspid:%u "
        "and psId:%d sctSuId:%d",mgmt->t.usta.t.psAspEvt.pspId, 
        mgmt->t.usta.s.psId, mgmt->t.usta.t.aspm.sctSuId);
//#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
//        INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
//        __FILE__, __LINE__, INC_SM_ALM_ASP_DOWN, "ASP Down", 0,
//        " - M3UA_ALARM_ Layer: Remote Asp Down pspId:%u and psId:%d",
//        mgmt->t.usta.t.psAspEvt.pspId , mgmt->t.usta.s.psId);
//#endif

        //mpDist->mpSmWrapper->setM3uaFlag();

        //delete it from blkconfig map also and send this info to peer, starts
        int li_selfId = INGwIfrPrParamRepository::getInstance().getSelfId();
        int li_peerId = INGwIfrPrParamRepository::getInstance().getPeerId();
        asp.pspId       = mgmt->t.usta.t.psAspEvt.pspId;
        asp.m3uaLsapId  = mgmt->t.usta.t.aspm.sctSuId;
  
        INGwSmBlkConfig::getInstance().delActvAsp(asp);
 
        if(li_peerId !=0)
        {
          INGwFtPktAspInActive ftAsp;
          ftAsp.initialize(li_selfId, li_peerId, asp);
          INGwIfrMgrManager::getInstance().sendMsgToINGW(&ftAsp);
        }
        //delete it from blkconfig map also and send this info to peer, ends

        handlePspAlmOp(BP_AIN_SM_ALMOP_ASP_ST_UPD_DOWN,
                      mgmt->t.usta.t.psAspEvt.pspId, mgmt->t.usta.s.psId,0);
      }


      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] handleItAlarm() %s M3UA_ALARM_ Layer - event LIT_EVENT_ASP_DOWN "
      "and cause LIT_CAUSE_STATUS_CHANGE\n", lpcTime);
    }
    break;

    case MMkIntegVal(ENTIT, LIT_EVENT_ASP_INACTIVE, LIT_CAUSE_STATUS_CHANGE):

      logger.logMsg(ERROR_FLAG, 0,
      "M3UA_ALARM_ Layer - Internal state remote ASP INACTIVE."
      "pspId:%u and psId:%d", mgmt->t.usta.s.pspId, mgmt->t.usta.s.psId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
      __FILE__, __LINE__, INC_SM_ALM_ASP_INACTIVE, "ASP Inactive", 0,
      " for pspId:%u and psId:%d", mgmt->t.usta.s.pspId, mgmt->t.usta.s.psId);
#endif

      handlePspAlmOp(BP_AIN_SM_ALMOP_ASP_ST_UPD_INACT,
                      mgmt->t.usta.s.pspId,mgmt->t.usta.s.psId, 0);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_ASPIA_FAIL, LCM_CAUSE_TMR_EXPIRED):
      logger.logMsg(ERROR_FLAG, 0,
			"M3UA_ALARM_ Layer - Internal state remote ASP INACTIVE. pspId:%u",
			mgmt->t.usta.s.pspId);

//#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
//      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
//	 		__FILE__, __LINE__, INC_SM_ALM_ASPIA_FAIL, "ASP InActive Fail", 0,
//			" for pspId:%u", mgmt->t.usta.s.pspId);
//#endif

      handlePspAlmOp(BP_AIN_SM_ALMOP_ASPIA_FAIL,
                      mgmt->t.usta.s.pspId,0,0);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_ASP_ACTIVE, LIT_CAUSE_STATUS_CHANGE):

      logger.logMsg(ERROR_FLAG, 0,
		  "M3UA_ALARM_ Layer - Internal state remote ASP ACTIVE. "
			"pspId:%u and psId:%d", mgmt->t.usta.t.psAspEvt.pspId, 
			mgmt->t.usta.s.psId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
	 		__FILE__, __LINE__, INC_SM_ALM_ASP_ACTIVE, "ASP Active", 0,
			" - M3UA_ALARM_ Layer: Remote Asp Active pspId:%u and psId:%d",
			mgmt->t.usta.t.psAspEvt.pspId, mgmt->t.usta.s.psId );
#endif
      handlePspAlmOp(BP_AIN_SM_ALMOP_ASP_ST_UPD_ACTV,
                      mgmt->t.usta.t.psAspEvt.pspId, mgmt->t.usta.s.psId,0);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_ASSOC_INHIBIT, LIT_CAUSE_ASSOC_INHIBIT):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Association is inhibited. pspId:%u",
	 		mgmt->t.usta.s.pspId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
	 		__FILE__, __LINE__, INC_SM_ALM_ASSOC_INHIBIT, "Assoc Inhibited", 0,
			" - M3UA_ALARM_ Layer: Association Inhibited pspId:%u",
			mgmt->t.usta.s.pspId);
#endif

      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_TERM_CONFIRM, LIT_CAUSE_LOCAL_SHUTDOWN):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Termination confirm is received from TUCL. pspId:%u",
	 		mgmt->t.usta.s.pspId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_FLC_DROP, SCT_FLC_DROP):
      logger.logMsg(VERBOSE_FLAG, 0,
      "M3UA_ALARM_ Layer- Data was dropped as flow control is active. pspId:%u",
			mgmt->t.usta.s.pspId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_LI_INV_PAR, LCM_CAUSE_INV_SUID):
      logger.logMsg(ERROR_FLAG, 0,
		  "M3UA_ALARM_ Layer - Received lower i/f primitive with invalid suId. "
			"suId:%u", mgmt->t.usta.s.suId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_LI_INV_PAR, LCM_CAUSE_INV_STATE): 
      logger.logMsg(ERROR_FLAG, 0,
		  "M3UA_ALARM_ Layer - Received lower i/f primitive in invalid state."
			"suId:%u", mgmt->t.usta.s.suId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_LI_INV_PAR, LCM_CAUSE_INV_PAR_VAL):
      logger.logMsg(ERROR_FLAG, 0,
			"M3UA_ALARM_ Layer - Received lower i/f primitive with invalid param."
			"suId:%u", mgmt->t.usta.s.suId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_STATUS_IND, LIT_CAUSE_INV_VERSION):
      logger.logMsg(WARNING_FLAG, 0,
			"M3UA_ALARM_ Layer - General status indication. Invalid version."
			"pspId:%u", mgmt->t.usta.s.pspId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_STATUS_IND, LCM_CAUSE_UNKNOWN):
      logger.logMsg(WARNING_FLAG, 0,
			"M3UA_ALARM_ Layer - General status indication. Unknown reason. pspId:%u",
			mgmt->t.usta.s.pspId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_SCT_SEND_FAIL, SCT_STATUS_COMM_LOST):
		{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Attempted to send data to the TUCL failed. "
			"pspId:%u, IP:%s", mgmt->t.usta.s.pspId, 
			(IPAddr.empty())?"NULL":IPAddr.c_str());

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
		if(!IPAddr.empty())
		{
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
	 		__FILE__, __LINE__, INC_SM_ALM_SCT_COMM_DOWN, "Sctp Comm Lost", 0, 
	 		" with IP:%s and Port:%d", IPAddr.c_str(), psp.dstPort);
		}
#endif

		}
    break;

    case MMkIntegVal(ENTIT, LIT_EVENT_HBEAT_LOST, LIT_CAUSE_M3UA_HBEAT_LOST):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - M3UA heartbeat lost. pspId:%u",
	 		mgmt->t.usta.s.pspId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_AS_DOWN, LIT_CAUSE_STATUS_CHANGE):
      logger.logMsg(ERROR_FLAG, 0,
	    "M3UA_ALARM_ Layer - Internal state of a PS changed. (AS_DOWN) psId:%u",
	 		mgmt->t.usta.s.psId);

//#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
//      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
//	 		__FILE__, __LINE__, INC_SM_ALM_AS_DOWN, "AS Down", 0,
//	 		" for pspId: %u", mgmt->t.usta.s.pspId);
//#endif

      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_AS_INACTIVE, LIT_CAUSE_STATUS_CHANGE):
      logger.logMsg(ERROR_FLAG, 0,
	   "M3UA_ALARM_ Layer - Internal state of a PS changed. (AS_INACTIVE)"
		 "psId:%u", mgmt->t.usta.s.psId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
	 		__FILE__, __LINE__, INC_SM_ALM_AS_INACTIVE, "AS Inactive", 0,
	 		" for pspId: %u", mgmt->t.usta.s.pspId);
#endif

      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_AS_ACTIVE, LIT_CAUSE_STATUS_CHANGE):
    {
      logger.logMsg(ALWAYS_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Internal state of a PS changed. (AS_ACTIVE) psId:%u",
	 		mgmt->t.usta.s.psId);


#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
	 		__FILE__, __LINE__, INC_SM_ALM_AS_ACTIVE, "AS Active", 0,
	 		" for pspId: %u", mgmt->t.usta.s.pspId);
#endif
  
#ifdef INC_ASP_SNDDAUD
      int sndDaud = 0;
      char * lcSndDaud = getenv("SEND_DAUD");
      if (lcSndDaud) {
        sndDaud = atoi(lcSndDaud);
        logger.logMsg(ALWAYS_FLAG, 0, "handleItAlarm(): "
               "EnvVar SEND_DAUD[%d]", sndDaud);
      }
      else {
        logger.logMsg(ALWAYS_FLAG, 0, "handleItAlarm(): "
               "Default value for envVar SEND_DAUD[%d]", sndDaud);
      }

     
      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);

      if (sndDaud) {
        printf("[+INC+] handleItAlarm() %s M3UA_ALARM_ Layer - "
            "Internal state of a PS changed. (AS_ACTIVE) psId:%u. "
            "Send DAUD after 1 sec sleep...\n", lpcTime, mgmt->t.usta.s.psId);
        sleep(1);
        // Send DAUD to SG
        sendDaud(mgmt->t.usta.s.psId);
      }
      else {
        printf("[+INC+] handleItAlarm() %s M3UA_ALARM_ Layer - "
               "Internal state of a PS changed. (AS_ACTIVE) psId:%u. "
               "Not required to send DAUD\n", lpcTime, mgmt->t.usta.s.psId);
        logger.logMsg(ALWAYS_FLAG, 0, "M3UA_ALARM_ Layer - "
               "Not required to send DAUD for psId:%u", mgmt->t.usta.s.psId);
      }
#endif
    }

    break;

    case MMkIntegVal(ENTIT, LIT_EVENT_AS_PENDING, LIT_CAUSE_STATUS_CHANGE):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Internal state of a PS changed. "
			"(AS_PENDING) psId:%u", mgmt->t.usta.s.psId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
	 		__FILE__, __LINE__, INC_SM_ALM_AS_PENDING, "AS State Pending", 0,
	 		" for pspId: %u", mgmt->t.usta.s.pspId);
#endif
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LCM_CAUSE_UNKNOWN):
		{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
			 "M3UA_ALARM_ Layer - Association has lost communication. "
			 "Cause:UNKNOWN PspId:%u and sctSuId:%d, ip:%s",
				mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId,
				(IPAddr.empty())?"NULL":IPAddr.c_str());
      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
		if(!IPAddr.empty())
		{
      bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
      if(alrmstate == false) 
      {
        INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
        __FILE__, __LINE__, INC_SM_ALM_SCT_COMM_DOWN, "Sct Comm Down", 0,
	 		  " with IP:%s and Port:%d", IPAddr.c_str(), psp.dstPort);
        mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		  }
		}
#endif

      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);
      if((state != ASSOCUP_SENT) && (state != ASPUP_SENT) && (state != ASPACTV_SENT))
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_STATE_UNKNOWN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not retrying since its already sent");
      }

      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_UNKNOWN,
                        mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);
		}
    break;

    case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_REMOTE_SHUTDOWN):
		{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
			 "M3UA_ALARM_ Layer - Association has lost communication. "
			 "Cause:REMOTE_SHUTDN  PspId:%u and sctSuId:%d, ip:%s",
				 mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId,
				(IPAddr.empty())?"NULL":IPAddr.c_str());

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
		if(!IPAddr.empty())
		{
      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
      bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
      if(alrmstate == false) 
      {
        INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
					 __FILE__, __LINE__,
					 INC_SM_ALM_SCT_COMM_DOWN,
					 "Sct Comm Down", 0,
			     " with IP:%s and Port:%d", IPAddr.c_str(), psp.dstPort);
        mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		  }
		}
#endif

#if 0
      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);

      if((state != ASSOCUP_SENT) && (state != ASSOCUP_SENT) && (state != ASSOCUP_SENT))
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_DISCONN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not retrying Association reestablishment, as its already sent ");
      }

      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_REMOTE_SHUTDN,
                        mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);
#endif

		}
    break;

    case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_SHUTDOWN_CMPLT):
    {
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Association has lost communication. "
			"Cause:SHUTDN_COMPLETE PspId:%u and sctSuId:%d",
			 mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);

      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);

      if((state != ASSOCUP_SENT) && (state != ASPUP_SENT) && (state != ASPACTV_SENT))
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_STATE_UNKNOWN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not retrying Association reestablishment, as its already sent ");
      }

      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_REMOTE_SHUTDN,
                        mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);
    }
    break;

    case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_REMOTE_ABORT):
		{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
	    "M3UA_ALARM_ Layer - Association has lost communication. "
			"Cause:REMOTE_ABORT PspId:%u and sctSuId:%d, IP:%s",
			 mgmt->t.usta.s.pspId,mgmt->t.usta.t.aspm.sctSuId,
			(IPAddr.empty())?"NULL":IPAddr.c_str());

      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
	if(!IPAddr.empty())
	{
      bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
      if(alrmstate == false) 
      {
        INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_SCT_COMM_DOWN,
                   "Sct Comm Down", 0,
			     				 " with IP:%s and Port:%d",
										IPAddr.c_str(), psp.dstPort);
        mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		  }
	}
#endif
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);

      if((state != ASSOCUP_SENT) && (state != ASPUP_SENT) && (state != ASPACTV_SENT))
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_STATE_UNKNOWN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not retrying Association reestablishment, as its already sent ");
      }

      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_REMOTE_ABORT,
                        mgmt->t.usta.s.pspId,mgmt->t.usta.t.aspm.sctSuId);
			}
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_COMM_LOST):
		{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Association has lost communication. "
			"Cause:COMM_LOST PspId:%u and sctSuId:%d, IP:%s",
			mgmt->t.usta.s.pspId,mgmt->t.usta.t.aspm.sctSuId,
			(IPAddr.empty())?"NULL":IPAddr.c_str());
      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
	if(!IPAddr.empty())
	{
      bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
      if(alrmstate == false) 
      {
        INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
					 __FILE__, __LINE__,
					 INC_SM_ALM_SCT_COMM_DOWN,
					 "Sct Comm Down", 0,
			      " with IP:%s and Port:%d", 
						IPAddr.c_str(), psp.dstPort);
        mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		  }
	}
#endif
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);
      if((state != ASSOCUP_SENT) && (state != ASPUP_SENT) && (state != ASPACTV_SENT))
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_STATE_UNKNOWN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not retrying Association reestablishment, as its already sent ");
      }

      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_COMM_LOST,
                        mgmt->t.usta.s.pspId,mgmt->t.usta.t.aspm.sctSuId);
		}
    break;

    case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_SCT_RESTART):
		{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Association has lost communication. "
			"Cause:SCTP_RESTART PspId:%u and sctSuId:%d, ip:%s",
			 mgmt->t.usta.s.pspId,mgmt->t.usta.t.aspm.sctSuId, 
			(IPAddr.empty())?"NULL":IPAddr.c_str());
      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
	if(!IPAddr.empty())
	{
      bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
      if(alrmstate == false) 
      {
        INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_SCT_COMM_DOWN,
                   "Sct Comm Down", 0,
			       " with IP:%s and Port:%d", 
										IPAddr.c_str(), psp.dstPort);
        mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		  }
	}
#endif
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);
      if((state != ASSOCUP_SENT) && (state != ASPUP_SENT) && (state != ASPACTV_SENT))
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_STATE_UNKNOWN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not retrying Association reestablishment, as its already sent ");
      }


      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_SCTP_RESTART,
                        mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);
		}
    break;

    case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_LOCAL_SCTP_PROCERR):
		{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Association has lost communication. "
			"Cause:SCTP_PROCERR PspId:%u and sctSuId:%d, ip:%s",
			 mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId, 
			(IPAddr.empty())?"NULL":IPAddr.c_str());
      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
	if(!IPAddr.empty())
	{
      bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
      if(alrmstate == false) 
      {
        INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_SCT_COMM_DOWN,
                   "Sct Comm Down", 0,
			      " with IP:%s and Port:%d", 
										IPAddr.c_str(), psp.dstPort);
        mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		  }
	}
#endif
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);
      if((state != ASSOCUP_SENT) && (state != ASPUP_SENT) && (state != ASPACTV_SENT))
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_STATE_UNKNOWN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not retrying Association reestablishment, as its already sent ");
      }

      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_SCTP_PROCERR,
                        mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);
		}
    break;

    case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_ASSOC_INHIBIT):
		{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Association has lost communication. "
			"Cause:ASSOC_INHIBIT PspId:%u and sctSuId:%d, ip:%s",
			 mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId,
			(IPAddr.empty())?"NULL":IPAddr.c_str());
      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
	if(!IPAddr.empty())
	{
    bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
    if(alrmstate == false) 
    {
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_SCT_COMM_DOWN,
                   "Sct Comm Down", 0,
						      " with IP:%s and Port:%d", 
										IPAddr.c_str(), psp.dstPort);
      mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		}
	}
#endif
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);
      if((state != ASSOCUP_SENT) && (state != ASPUP_SENT) && (state != ASPACTV_SENT))
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_STATE_UNKNOWN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not retrying Association reestablishment, as its already sent ");
      }

      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_ASSOC_INHIBIT,
                        mgmt->t.usta.s.pspId,mgmt->t.usta.t.aspm.sctSuId);
		}
    break;

    case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_LOCAL_SHUTDOWN):
		{ 
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Association has lost communication. "
			"Cause:LOCAL_SHUTDN  PspId:%u and sctSuId:%d, ip:%s",
			 mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId,
			(IPAddr.empty())?"NULL":IPAddr.c_str());
      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
			
#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
		if(!IPAddr.empty())
		{
      bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
      if(alrmstate == false) 
      {
        INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_SCT_COMM_DOWN,
                   "Sct Comm Down", 0,
						      " with IP:%s and Port:%d",
										IPAddr.c_str(), psp.dstPort);
        mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		  }
		}
#endif
      //mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_STATE_UNKNOWN);
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);

      if(state != ASSOC_DISABLE) 
        sendInitReq(mgmt->t.usta.s.pspId,  mgmt->t.usta.t.aspm.sctSuId);
      /*** commented below line becoz, its a local shutdown, i.e. we have made the assoc down ourself
      so no need to retry for assocUp

      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId);
      if(state != ASPUP_INPROG)
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASPUP_DISCONN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not Sending ASP_UP, as its already sent ");
      }

      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_LOCAL_SHUTDN,
                        mgmt->t.usta.s.pspId,mgmt->t.usta.t.aspm.sctSuId);
      ***/ 
		}
    break;

#if IT_TERM_CAUSE_INFO    
		case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_SCT_STALE):
		{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Association has lost communication. "
			"Cause:LIT_CAUSE_SCT_STALE PspId:%u and sctSuId:%d, ip:%s",
			 mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId,
			(IPAddr.empty())?"NULL":IPAddr.c_str());

      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
      
#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
	    if(!IPAddr.empty())
	    {
        bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
        if(alrmstate == false) 
        {
          INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_SCT_COMM_DOWN,
                   "Sct Comm Down", 0,
						      " with IP:%s and Port:%d",
										IPAddr.c_str(), psp.dstPort);
          mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		    }
      }
#endif
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId); 
      if((state != ASSOCUP_SENT) && (state != ASPUP_SENT) && (state != ASPACTV_SENT))
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_STATE_UNKNOWN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not retrying Association reestablishment, as its already sent ");
      }

      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_LOCAL_SHUTDN,
                        mgmt->t.usta.s.pspId,mgmt->t.usta.t.aspm.sctSuId);
		}
    break;
    
    case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_SCT_OUTRES):
		{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
      "M3UA_ALARM_ Layer - Association has lost communication. "
			"Cause:LIT_CAUSE_SCT_OUTRES PspId:%u and sctSuId:%d, ip:%s",
			 mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId,
			(IPAddr.empty())?"NULL":IPAddr.c_str());
      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
	    if(!IPAddr.empty())
	    {
        bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
        if(alrmstate == false) 
        {
          INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_SCT_COMM_DOWN,
                   "Sct Comm Down", 0,
						      " with IP:%s and Port:%d", 
										IPAddr.c_str(), psp.dstPort);
          mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		    }
	    }
#endif
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId); 
      if((state != ASSOCUP_SENT) && (state != ASPUP_SENT) && (state != ASPACTV_SENT))
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_STATE_UNKNOWN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not retrying Association reestablishment, as its already sent ");
      }

      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_LOCAL_SHUTDN,
                        mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);
		}
		break;

    case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_SCT_UNRSLV_ADDR):
		{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
      "M3UA_ALARM_ Layer - Association has lost communication. "
			"Cause:LIT_CAUSE_SCT_UNRSLV_ADDR  PspId:%u and sctSuId:%d, Ip:%s",
			 mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId,
			(IPAddr.empty())?"NULL":IPAddr.c_str());
      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
		  if(!IPAddr.empty())
		  {
        bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
        if(alrmstate == false) 
        {
          INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_SCT_COMM_DOWN,
                   "Sct Comm Down", 0,
						      " with IP:%s and Port:%d", 
										IPAddr.c_str(), psp.dstPort);
          mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		    }
		  }
#endif
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId); 
      if((state != ASSOCUP_SENT) && (state != ASPUP_SENT) && (state != ASPACTV_SENT))
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_STATE_UNKNOWN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not retrying Association reestablishment, as its already sent ");
      }

      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_LOCAL_SHUTDN,
                        mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);
		}
		break;

	case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_SCT_INVAL_MAND_PAR):
	{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Association has lost communication. "
			"Cause: INVALID MAND PARAM PspId:%u and sctSuId:%d",
			 mgmt->t.usta.s.pspId,mgmt->t.usta.t.aspm.sctSuId,
			(IPAddr.empty())?"NULL":IPAddr.c_str());

      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
	    if(!IPAddr.empty())
	    {
        bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
        if(alrmstate == false) 
        {
          INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_SCT_COMM_DOWN,
                   "Sct Comm Down", 0,
						      " with IP:%s and Port:%d",
										IPAddr.c_str(), psp.dstPort);
          mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		    }
	    }
#endif
#if 0
      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
      int state = mpDist->mpSmWrapper->getPspMap(pspId_m3uaSapId); 
      if(state != ASPUP_INPROG)
        mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASSOC_DISCONN); 
      else{
        logger.logMsg(TRACE_FLAG, 0,
	 		  "Not Sending ASP_UP, as its already sent ");
      }
#endif
      handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_LOCAL_SHUTDN,
                        mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId);
	}
  break;

#endif

	case MMkIntegVal(ENTIT, LIT_EVENT_SCT_COMM_DOWN, LIT_CAUSE_M3UA_HBEAT_LOST):
	{
      AddPsp psp;
      memset(&psp,0,sizeof(AddPsp)); 
      bool found = INGwSmBlkConfig::getInstance().getAsp 
															(mgmt->t.usta.s.pspId, psp);
			string IPAddr;
			char buf[30];
			if(found)
			{
				if(psp.nmbAddr > 0)
				{
					if(psp.addr[0].type == CM_NETADDR_IPV6)
					{
						memcpy(buf, &psp.addr[0].u.ipv6NetAddr, 
										sizeof(psp.addr[0].u.ipv6NetAddr));
						IPAddr = buf;
					}
					else
						IPAddr = g_convertIpLongToStr(psp.addr[0].u.ipv4NetAddr);
				}
			}

      logger.logMsg(ERROR_FLAG, 0,
			"M3UA_ALARM_ Layer - Association has lost communication. "
			"Cause:HBEAT_LOST PspId:%u and sctSuId:%d, ip:%s",
		    mgmt->t.usta.s.pspId, mgmt->t.usta.t.aspm.sctSuId,
			(IPAddr.empty())?"NULL":IPAddr.c_str());

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
	if(!IPAddr.empty())
	{
    pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
    bool alrmstate = mpDist->mpSmWrapper->getPspMapAlarmSct(pspId_m3uaSapId);
    if(alrmstate == false) 
    {
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                   __FILE__, __LINE__,
                   INC_SM_ALM_SCT_COMM_DOWN,
                   "Sct Comm Down", 0,
						      " with IP:%s and Port:%d", 
										IPAddr.c_str(), psp.dstPort);
      mpDist->mpSmWrapper->setPspMapAlarmSct(pspId_m3uaSapId, true);
		}
	}
#endif
      // refer to our issue # ccpu00017887 -- infinite loop
      //handleCommDownAlm(BP_AIN_SM_COMMDN_CAUSE_HBEAT_LOST, 
      //                  mgmt->t.usta.s.pspId);
	}
  break;

	case MMkIntegVal(ENTIT, LIT_EVENT_ASPAC_FAIL, LIT_REASON_NO_RESPONSE):
      logger.logMsg(ERROR_FLAG, 0,
			"M3UA_ALARM_ Layer - ASPAC Failed. pspId:%u", mgmt->t.usta.s.pspId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
	 		__FILE__, __LINE__, INC_SM_ALM_ASPAC_FAIL, "ASPAC Failed", 0,
	 		" for pspId: %u", mgmt->t.usta.s.pspId);
#endif
      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
      mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASPACTV_FAIL); 

      handlePspAlmOp(BP_AIN_SM_ALMOP_ASPAC_FAIL,
                     mgmt->t.usta.s.pspId,0,0);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_ASPUP_FAIL, LIT_REASON_NO_RESPONSE):
      logger.logMsg(ERROR_FLAG, 0,
			"M3UA_ALARM_ Layer - ASPUP Failed. pspId:%d sctSuId:%d", mgmt->t.usta.s.pspId,mgmt->t.usta.t.aspm.sctSuId);

      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
      mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASPUP_FAIL); 

      handlePspAlmOp(BP_AIN_SM_ALMOP_ASPUP_FAIL,
                     mgmt->t.usta.s.pspId,0,mgmt->t.usta.t.aspm.sctSuId);
      break;


    case MMkIntegVal(ENTIT, LIT_EVENT_ASPUP_FAIL, LIT_CAUSE_RETRY_EXCEED):
      logger.logMsg(ERROR_FLAG, 0,
			"M3UA_ALARM_ Layer - ASPUP Failed after max retry. pspId:%d and sctSuId:%d", mgmt->t.usta.s.pspId,mgmt->t.usta.t.aspm.sctSuId);

      pspId_m3uaSapId = (((mgmt->t.usta.s.pspId) << 4) | mgmt->t.usta.t.aspm.sctSuId);
      mpDist->mpSmWrapper->setPspMap(pspId_m3uaSapId, ASPUP_FAIL); 

      handlePspAlmOp(BP_AIN_SM_ALMOP_ASPUP_FAIL,
                     mgmt->t.usta.s.pspId,0,mgmt->t.usta.t.aspm.sctSuId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_ASPIA_FAIL, LIT_REASON_NO_RESPONSE):
      logger.logMsg(ERROR_FLAG, 0,
			"M3UA_ALARM_ Layer - ASPIA Failed. pspId:%u", mgmt->t.usta.s.pspId);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))//+tag+
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
	 		__FILE__, __LINE__, INC_SM_ALM_ASPIA_FAIL, "ASPIA Failed", 0,
	 		" for PspId:[%u]", mgmt->t.usta.s.pspId);
#endif
      handlePspAlmOp(BP_AIN_SM_ALMOP_ASPIA_FAIL,
                     mgmt->t.usta.s.pspId,0,0);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_PC_UNAVAILABLE, LCM_CAUSE_UNKNOWN):
#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED, 
	 		__FILE__, __LINE__, BP_AIN_SM_ALM_DPC_PAUSE, "PC Unavail", 0,
			" - M3UA_ALARM_ Layer: Point code :%d unavailable.", mgmt->t.usta.t.dpcEvt.aPc);
#endif
      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] handleItAlarm() %s M3UA_ALARM_ Layer - PC UNAVAILABLE received from "
             "remote M3UA peer. APC:0x%x\n", lpcTime, mgmt->t.usta.t.dpcEvt.aPc);

      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_PC_CONGESTED, LCM_CAUSE_UNKNOWN):
#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
	 		__FILE__, __LINE__, BP_AIN_SM_ALM_DPC_PAUSE, "PC Cong", 0,
			" - M3UA_ALARM_ Layer: Point code :%d congested", mgmt->t.usta.t.dpcEvt.aPc);
#endif
      logger.logMsg(ERROR_FLAG, 0,
		  "M3UA_ALARM_ Layer: Point code :%u congested, lvl:%d", mgmt->t.usta.t.dpcEvt.aPc,
			(int)mgmt->t.usta.t.dpcEvt.p.congLevel);

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] handleItAlarm() %s M3UA_ALARM_ Layer - PC CONGESTED received from remote "
             "M3UA peer. APC:0x%x CongLevel:0x%x\n", lpcTime, mgmt->t.usta.t.dpcEvt.aPc,
              mgmt->t.usta.t.dpcEvt.p.congLevel);

      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_PC_AVAILABLE, LCM_CAUSE_UNKNOWN):
#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
	 		__FILE__, __LINE__, BP_AIN_SM_ALM_DPC_RESUME, "PC Avail", 0,
	 		" - M3UA_ALARM_ Layer: Point code :%d available.", mgmt->t.usta.t.dpcEvt.aPc);
#endif

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] handleItAlarm() %s M3UA_ALARM_ Layer - PC AVAILABLE received from remote "
             "M3UA peer. APC:0x%x\n", lpcTime, mgmt->t.usta.t.dpcEvt.aPc);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_PC_USER_PART_UNA, LCM_CAUSE_UNKNOWN):
//#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
//      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
//                   __FILE__, __LINE__,
//                   BP_AIN_SM_ALM_DPC_PAUSE,
//                   "PC UPU", 0,
//                   " - M3UA_ALARM_ Layer: Point code :%u User-Part unavailable.",
//                   mgmt->t.usta.t.dpcEvt.aPc);
//#endif

      logger.logMsg(ERROR_FLAG, 0, 
			"3UA Layer: Point code :%d User-Part unavailable.",
			mgmt->t.usta.t.dpcEvt.aPc);

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] handleItAlarm() %s M3UA_ALARM_ Layer - PC USER PART UNAVAILABLE "
             "received from remote M3UA peer. APC:%u, servInd:0x%x\n",
             lpcTime, mgmt->t.usta.t.dpcEvt.aPc, mgmt->t.usta.t.dpcEvt.p.servInd);

      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_PC_RESTRICTED, LCM_CAUSE_UNKNOWN):
#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
	 		__FILE__, __LINE__, BP_AIN_SM_ALM_DPC_PAUSE, "PC Restricted", 0,
	 		" - M3UA_ALARM_ Layer: Point code :%d restricted.", mgmt->t.usta.t.dpcEvt.aPc);
#endif

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] handleItAlarm() %s M3UA_ALARM_ Layer - PC RESTRICTED received from remote "
             "M3UA peer. APC:0x%x\n", lpcTime, mgmt->t.usta.t.dpcEvt.aPc);

      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_DAUD_RXD, LCM_CAUSE_UNKNOWN):
      logger.logMsg(VERBOSE_FLAG, 0,
	 		"M3UA_ALARM_ Layer - DAUD message is received. nwkId:%u dpc:%u",
	 		mgmt->t.usta.s.nwkId, mgmt->t.usta.t.dpcEvt.aPc);

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] handleItAlarm() %s M3UA_ALARM_ Layer - DAUD RXD received from remote "
             "M3UA peer. nwkId:0x%x dpc:0x%x\n",
             lpcTime, mgmt->t.usta.s.nwkId, mgmt->t.usta.t.dpcEvt.aPc);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_RK_REGISTERED, LIT_CAUSE_MSG_RECEIVED):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Routing key was registered. psId:%u pspId:%u, "
			"rtCtxt:%u, lclRkId:%u", mgmt->t.usta.s.psId, mgmt->t.usta.s.pspId, 
	 		mgmt->t.usta.t.drkmEvt.rteCtx, mgmt->t.usta.t.drkmEvt.lclRkId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_RK_DEREGISTERED, LIT_CAUSE_MSG_RECEIVED):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Routing key was deregistered. pspId:%u, rtCtxt:%u",
	 		mgmt->t.usta.s.pspId, mgmt->t.usta.t.drkmEvt.rteCtx);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_RC_GENERATED, LIT_CAUSE_RK_REGISTERED):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Routing context was created. pspId:%u, rtCtxt:%u",
	 		mgmt->t.usta.s.pspId, mgmt->t.usta.t.drkmEvt.rteCtx);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_RC_DELETED, LIT_CAUSE_RK_DEREGISTERED):
      logger.logMsg(ERROR_FLAG, 0,
	    "M3UA_ALARM_ Layer - Routing context was deleted. pspId:%u, rtCtxt:%u",
	    mgmt->t.usta.s.pspId, mgmt->t.usta.t.drkmEvt.rteCtx);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_INV_LCLRKID, LIT_CAUSE_MSG_RECEIVED):
      logger.logMsg(ERROR_FLAG, 0,
	    "M3UA_ALARM_ Layer - Invalid local routing key ID was received in "
			"the REG RSP message. pspId:%u", mgmt->t.usta.s.pspId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_RK_TMOUT, LIT_CAUSE_RK_REG_NO_RESP):
    case MMkIntegVal(ENTIT, LIT_EVENT_RK_TMOUT, LIT_CAUSE_RK_DEREG_NO_RESP):
    case MMkIntegVal(ENTIT, LIT_EVENT_RK_TMOUT, LIT_CAUSE_RK_REG_PARTIAL_RESP):
    case MMkIntegVal(ENTIT, LIT_EVENT_RK_TMOUT, LIT_CAUSE_RK_DEREG_PARTIAL_RESP):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Timer for routing key registration/deregistration"
			"expired. pspId:%u", mgmt->t.usta.s.pspId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_PS_DELETED, LIT_CAUSE_RCTX_UNREG):
    case MMkIntegVal(ENTIT, LIT_EVENT_PS_DELETED, LIT_CAUSE_SCT_TERM_IND):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - PS ctrl-blk deleted before waiting for the "
			"reg/dereg response. psId:%u", mgmt->t.usta.s.psId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_PSP_REGD, LIT_CAUSE_RK_REGISTERED):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Added PSP in PS.PSP-list due to routing "
			"key registration. pspId:%u", mgmt->t.usta.s.pspId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_SRCADDRLST_CHG, LIT_CAUSE_PEER_ASSOC_PARAMS):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Assoc src-addr-list changed on recv'ng AssocInd "
			"INIT. pspId:%u", mgmt->t.usta.s.pspId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_DSTADDRLST_CHG, LIT_CAUSE_PEER_ASSOC_PARAMS):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Assoc dest-addr-list changed on recv'ng AssocInd "
			"COOKIE. pspId:%u", mgmt->t.usta.s.pspId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_PRIDSTADDR_CHG, LIT_CAUSE_PEER_ASSOC_PARAMS):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - Assoc with primary dest changed on recv'ng "
			"AssocInd COOKIE. pspId:%u", mgmt->t.usta.s.pspId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_EOPEN_OK, LCM_CAUSE_UNKNOWN):
      logger.logMsg(VERBOSE_FLAG, 0, 
	 		"M3UA_ALARM_ Layer - SCTP Open Endpoint - Ok. suId:%u",
		 	mgmt->t.usta.s.suId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_EOPEN_FAIL, LCM_CAUSE_INV_STATE):
      logger.logMsg(ERROR_FLAG, 0,
	 		"M3UA_ALARM_ Layer - SCTP Open Endpoint - Failed. suId:%u",
	 		mgmt->t.usta.s.suId);
      break;

    case MMkIntegVal(ENTIT, LIT_EVENT_NO_ROUTE_FOUND, LIT_CAUSE_MSG_RECEIVED):
      logger.logMsg(ERROR_FLAG, 0,
		 	"M3UA_ALARM_ Layer - NO ROUTE FOUND. Msg Rcvd PC %d SrcInd %d",
		 	mgmt->t.usta.t.dpcEvt.aPc, mgmt->t.usta.t.dpcEvt.p.servInd);

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
	 		printf("[+INC+] handleItAlarm() %s M3UA_ALARM_ Layer - PC NO ROUTE FOUND received "
             "from remote M3UA peer. APC:0x%x\n", lpcTime, mgmt->t.usta.t.dpcEvt.aPc);
      break;

    default:
      logger.logMsg(ERROR_FLAG, 0,
			"M3UA_ALARM_ Layer - Unrecognized event : %u , cause : %u",
			mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handleItAlarm");

  return (BP_AIN_SM_OK);
}


// handle SCTP alarms
int 
INGwSmAlmHdlr::handleSbAlarm(SbMgmt  *mgmt)
{
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleSbAlarm");

  integ = MMkIntegVal(ENTSB, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTSB, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_INV_SPID):
    case MMkIntegVal(ENTSB, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_INV_SAP):
    case MMkIntegVal(ENTSB, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_INV_PAR_VAL):
    case MMkIntegVal(ENTSB, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_DECODE_ERR):
      logger.logMsg (ERROR_FLAG, 0, 
                     "SCTP_ALARM_ Layer - Illegal/invalid event recv'd at upper interface.");
      break;

    case MMkIntegVal(ENTSB, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_INV_SUID):
    case MMkIntegVal(ENTSB, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_INV_SAP):
    case MMkIntegVal(ENTSB, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_INV_PAR_VAL):
    case MMkIntegVal(ENTSB, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_DECODE_ERR):
      logger.logMsg (ERROR_FLAG, 0, 
                     "SCTP_ALARM_ Layer - Illegal/invalid event recv'd at upper interface.");
      break;

    case MMkIntegVal(ENTSB, LCM_EVENT_MI_INV_EVT, LCM_CAUSE_INV_SPID):
    case MMkIntegVal(ENTSB, LCM_EVENT_MI_INV_EVT, LCM_CAUSE_INV_SAP):
    case MMkIntegVal(ENTSB, LCM_EVENT_MI_INV_EVT, LCM_CAUSE_INV_PAR_VAL):
    case MMkIntegVal(ENTSB, LCM_EVENT_MI_INV_EVT, LCM_CAUSE_DECODE_ERR):
      logger.logMsg (ERROR_FLAG, 0, 
                     "SCTP_ALARM_ Layer - Illegal/invalid event recv'd at layer manager interface.");
      break;

    case MMkIntegVal(ENTSB, LCM_EVENT_SMEM_ALLOC_FAIL, LCM_CAUSE_MEM_ALLOC_FAIL):
      logger.logMsg (ERROR_FLAG, 0, 
                   "SCTP_ALARM_ Layer - Static memory allocation failed.");
      _checkFailureExit();

      break;

    case MMkIntegVal(ENTSB, LCM_EVENT_DMEM_ALLOC_FAIL, LCM_CAUSE_MEM_ALLOC_FAIL):
      logger.logMsg (ERROR_FLAG, 0, 
        "SCTP_ALARM_ Layer - Dynamic memory allocation failed.");
      _checkFailureExit();

      break;

    default:
      logger.logMsg(TRACE_FLAG, 0,
                    "SCTP_ALARM_ Layer - Unrecognized event : %u , cause : %u",
                    mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handleSbAlarm");

  return (BP_AIN_SM_OK);
}


// handle TUCL alarms
int
INGwSmAlmHdlr::handleHiAlarm(HiMngmt *mgmt)
{
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handleHiAlarm");

  integ = MMkIntegVal(ENTHI, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTHI, LHI_EVENT_BNDREQ, LCM_CAUSE_INV_SPID):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid spId specified.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_BNDREQ, LCM_CAUSE_INV_SAP):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Bind failed as SAP not configured.");

      break;


    case MMkIntegVal(ENTHI, LHI_EVENT_BNDREQ, LCM_CAUSE_INV_STATE):
      logger.logMsg(ERROR_FLAG, 0,
                    " TUCL_ALARM_ Layer - Bind failed as invalid SAP state.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_UBNDREQ, LCM_CAUSE_INV_SPID):
      logger.logMsg(ERROR_FLAG, 0,
		  "TUCL_ALARM_ Layer - Unbind Request failed as invalid spId specified.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_UBNDREQ, LCM_CAUSE_INV_SAP):
      logger.logMsg(ERROR_FLAG, 0,
			"TUCL_ALARM_ Layer - Unbind Request failed as SAP not configured.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_SERVOPENREQ, LCM_CAUSE_INV_SPID):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid spId specified.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_SERVOPENREQ, LCM_CAUSE_INV_SAP):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - SAP not configured.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_SERVOPENREQ, LCM_CAUSE_INV_STATE):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid SAP state.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_SERVOPENREQ, LCM_CAUSE_INV_PAR_VAL):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid value of an input parameter to an interface function.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_CONREQ, LCM_CAUSE_INV_SPID):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid spId specified.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_CONREQ, LCM_CAUSE_INV_SAP):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - SAP not configured.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_CONREQ, LCM_CAUSE_INV_STATE):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid SAP state.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_CONREQ, LCM_CAUSE_INV_PAR_VAL):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid value of an input parameter to an interface function.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_CONRSP, LCM_CAUSE_INV_SPID):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid spId specified.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_CONRSP, LCM_CAUSE_INV_SAP):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - SAP not configured.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_CONRSP, LCM_CAUSE_INV_STATE):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid SAP state.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_CONRSP, LHI_CAUSE_INV_CON_STATE):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid connection state.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_DATREQ, LCM_CAUSE_INV_SPID):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid spId specified.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_DATREQ, LCM_CAUSE_INV_SAP):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - SAP not configured.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_DATREQ, LCM_CAUSE_INV_STATE):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid SAP state.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_DATREQ, LHI_CAUSE_INV_CON_STATE):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid connection state.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_DATREQ, LCM_CAUSE_INV_PAR_VAL):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid value of an input parameter to an interface function.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_UDATREQ, LCM_CAUSE_INV_SPID):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid spId specified.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_UDATREQ, LCM_CAUSE_INV_SAP):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - SAP not configured.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_UDATREQ, LCM_CAUSE_INV_STATE):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid SAP state.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_UDATREQ, LCM_CAUSE_INV_PAR_VAL):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid value of an input parameter to an interface function.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_CNTRLREQ, LHI_CAUSE_INTPRIM_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Internal error processing a control request.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_GRPCNTRLREQ, LHI_CAUSE_INTPRIM_ERR):
      logger.logMsg(ERROR_FLAG, 0,
        "TUCL_ALARM_ Layer - Internal error processing a group SAP control request.");

      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_SHTDWNREQ, LHI_CAUSE_INTPRIM_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Error while communicating with another TUCL instance.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_DISCREQ, LCM_CAUSE_INV_SPID):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid spId specified.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_DISCREQ, LCM_CAUSE_INV_SAP):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - SAP not configured.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_DISCREQ, LCM_CAUSE_INV_STATE):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid SAP state.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_DISCREQ, LCM_CAUSE_INV_PAR_VAL):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Invalid value of an input parameter to an interface function.");
      break;

    case MMkIntegVal(ENTHI, LCM_EVENT_SMEM_ALLOC_FAIL, LCM_CAUSE_UNKNOWN):
			logger.logMsg(ERROR_FLAG,0,
			"TUCL Layer: Failed to allocate static memory.");
      _checkFailureExit();
      break;

    case MMkIntegVal(ENTHI, LCM_EVENT_DMEM_ALLOC_FAIL, LCM_CAUSE_UNKNOWN):
			logger.logMsg(ERROR_FLAG, 0, 
			"TUCL Layer: Failed to allocate Dynamic memory.");
      _checkFailureExit();
      break;

    case MMkIntegVal(ENTHI, LCM_EVENT_DMEM_ALLOC_FAIL, LHI_CAUSE_SOCK_SEND_ERR):
			logger.logMsg(ERROR_FLAG, 0, 
			"TUCL Layer: Failed to allocate Dynamic memory.");
      _checkFailureExit();
      break;

    case MMkIntegVal(ENTHI, LCM_EVENT_DMEM_ALLOC_FAIL, LHI_CAUSE_SOCK_RECV_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Failed to allocate Dynamic memory.");
      _checkFailureExit();

      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_RES_CONG_STRT, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Dynamic resource congestion start limit has been hit.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_RES_CONG_DROP, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Dynamic resource congestion drop limit has been hit.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_RES_CONG_STOP, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - No more dynamic resource congestion.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_TXQ_CONG_ON, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Congestion of network buffers.");

      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_TXQ_CONG_OFF, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Network buffers no longer congested.");

      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_TXQ_CONG_DATA_DROP, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Severe congestion of network buffers.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_SERVOPENREQ, LHI_CAUSE_CONID_NOT_AVAIL):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Unable to find a connection identifier for the server request.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_CONREQ, LHI_CAUSE_INITLOCK_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Error in initializing a lock.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_CONREQ, LHI_CAUSE_CONID_NOT_AVAIL):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Unable to find a connection identifier.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_INET_ERR, LHI_CAUSE_SOCK_SEND_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Error in transmitting data on a socket.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_INET_ERR, LHI_CAUSE_SOCK_ACPT_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Error in accepting new incoming connections on a socket.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_INET_ERR, LHI_CAUSE_SOCK_RECV_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Error in receiving data on a socket.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_INET_ERR, LHI_CAUSE_SOCK_CONN_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Error in connecting to a TCP server socket.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_INET_ERR, LHI_CAUSE_SOCK_SLCT_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Error in socket select(): call.");
      break;

    case MMkIntegVal(ENTHI, LCM_EVENT_INV_EVT, LHI_CAUSE_CONID_NOT_AVAIL):
			logger.logMsg(ERROR_FLAG, 0, 
			"TUCL Layer: Maximum number of connections as specified in general"
			"configuration reached.");
      break;

    case MMkIntegVal(ENTHI, LCM_EVENT_INV_EVT, LCM_CAUSE_HASH_FAIL):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Hashing function failure.");
      break;

    case MMkIntegVal(ENTHI, LCM_EVENT_INV_EVT, LHI_CAUSE_LOCK_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Error in obtaining a lock.");
      break;

    case MMkIntegVal(ENTHI, LCM_EVENT_INV_EVT, LHI_CAUSE_UNLOCK_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Error in unlocking a lock.");
      break;

    case MMkIntegVal(ENTHI, LHI_EVENT_RECVTHR_CLOSED, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - One of the receive threads unexpectedly closed.");
      break;

    case MMkIntegVal(ENTHI, LCM_EVENT_PI_INV_EVT, LCM_CAUSE_DECODE_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "TUCL_ALARM_ Layer - Received TCP TPKT packet with a wrong version number.");
      break;

    default:
      logger.logMsg(TRACE_FLAG, 0,
                    "TUCL_ALARM_ Layer - Unrecognized event : %u , cause : %u",
                    mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handleHiAlarm");

  return (BP_AIN_SM_OK);

}

// handle MTP3 alarms
int
INGwSmAlmHdlr::handleSnAlarm(SnMngmt *mgmt) {
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleSnAlarm, event%d, cause:%d",
						mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  integ = MMkIntegVal(ENTSN, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  int liOpc = -1, liDpc = -1, liLinkSet = -1, liDevId = -1, liChannel = -1;
  int port, channel, pc;

  switch (integ) {

    case MMkIntegVal(ENTSN, LCM_EVENT_INV_TMR_EVT, LCM_CAUSE_UNKNOWN): 
      logger.logMsg (ERROR_FLAG, 0,
        "MTP3_ALARM_ Layer - Illegal/invalid timer event is rx'd at the upper interface.");
      break;

    case MMkIntegVal(ENTSN, LCM_EVENT_BND_FAIL, LCM_CAUSE_INV_STATE):
    case MMkIntegVal(ENTSN, LCM_EVENT_BND_FAIL, LIT_CAUSE_RETRY_EXCEED):
    case MMkIntegVal(ENTSN, LCM_EVENT_BND_FAIL, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ERROR_FLAG, 0,
						 "MTP3_ALARM_ Layer - Bind procedure with MTP2 (Init failed).");

      break;

    case MMkIntegVal(ENTSN, LCM_EVENT_BND_OK, LCM_CAUSE_UNKNOWN):
      logger.logMsg (ALWAYS_FLAG, 0,
						 "MTP3_ALARM_ Layer - Bind procedure with MTP2 layer succeeded.");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_PROT_ST_DN, LSN_CAUSE_MTP2_DISC):
      AddLink lLinkInfo;
      memset(&lLinkInfo,0,sizeof(AddLink)); 
      INGwSmBlkConfig::getInstance().getLinkInfo
																		(mgmt->hdr.elmId.elmntInst1, lLinkInfo);

      logger.logMsg (ERROR_FLAG, 0,
						 "MTP3_ALARM_ Layer - Link <%d> is down, cause MTP2 disconnect",lLinkInfo.lnkId);
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_PROT_ST_DN, LCM_CAUSE_UNKNOWN):
    case MMkIntegVal(ENTSN, LSN_EVENT_PROT_ST_DN, LSN_CAUSE_LNK_RSTR_FAILED):
    case MMkIntegVal(ENTSN, LSN_EVENT_PROT_ST_DN, LSN_CAUSE_T17_EXPIRED):
    case MMkIntegVal(ENTSN, LSN_EVENT_PROT_ST_DN, LSN_CAUSE_LNK_DEACT):
		{
      AddLink lLinkInfo;
      memset(&lLinkInfo,0,sizeof(AddLink)); 
      INGwSmBlkConfig::getInstance().getLinkInfo
																		(mgmt->hdr.elmId.elmntInst1, lLinkInfo);

      logger.logMsg (ERROR_FLAG, 0, "MTP3_ALARM_ Layer - Link is Down "
			"<lnkId:%d,OPC:%d,DPC:%d,LINKSET:%d, PORT:%d,TIMESLOT:%d,SLC:%d",
        lLinkInfo.lnkId, lLinkInfo.opc, lLinkInfo.adjDpc, lLinkInfo.lnkSetId, 
        lLinkInfo.physPort, lLinkInfo.timeSlot,lLinkInfo.slc);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(
        INGwIfrMgrAlarmMgr::NON_GAPPED, 
        __FILE__, __LINE__, 
        BP_AIN_SM_ALM_LINK_DOWN,
        BP_AIN_SM_ALMENT_TUCL_CONNS, 0, 
       " - LnkId:%d, OPC:%d, DPC:%d, LINKSET:%d, PORT:%d, TIMESLOT:%d, SLC:%d",
        lLinkInfo.lnkId, lLinkInfo.opc, lLinkInfo.adjDpc, lLinkInfo.lnkSetId, 
        lLinkInfo.physPort, lLinkInfo.timeSlot,lLinkInfo.slc);
#endif
			}
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_PROT_ST_UP, LCM_CAUSE_UNKNOWN):
		{
      
      AddLink lLinkInfo;
      memset(&lLinkInfo,0,sizeof(AddLink)); 
      INGwSmBlkConfig::getInstance().getLinkInfo
																		(mgmt->hdr.elmId.elmntInst1, lLinkInfo);

      logger.logMsg (ERROR_FLAG, 0, "MTP3_ALARM_ Layer - Link is Up "
			"<lnkId:%d,OPC:%d,DPC:%d,LINKSET:%d, PORT:%d,TIMESLOT:%d,SLC:%d",
        lLinkInfo.lnkId, lLinkInfo.opc, lLinkInfo.adjDpc, lLinkInfo.lnkSetId, 
        lLinkInfo.physPort, lLinkInfo.timeSlot,lLinkInfo.slc);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(
        INGwIfrMgrAlarmMgr::NON_GAPPED, 
        __FILE__, __LINE__, 
        BP_AIN_SM_ALM_LINK_UP,
        BP_AIN_SM_ALMENT_TUCL_CONNS, 0, 
      " - LinkId:%d, OPC:%d, DPC:%d, LINKSET:%d, PORT:%d, TIMESLOT:%d, SLC:%d",
        lLinkInfo.lnkId, lLinkInfo.opc, lLinkInfo.adjDpc, lLinkInfo.lnkSetId, 
        lLinkInfo.physPort, lLinkInfo.timeSlot,lLinkInfo.slc);
#endif
			}
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_PAUSE, LCM_CAUSE_UNKNOWN):
    case MMkIntegVal(ENTSN, LSN_EVENT_PAUSE, LSN_CAUSE_INVALID_DPC):
    case MMkIntegVal(ENTSN, LSN_EVENT_PAUSE, LSN_CAUSE_DPC_FAILED):
      pc = ((int) mgmt->t.usta.evntParm[3]) & 0x000000FF;
      liDpc = pc;
      pc = mgmt->t.usta.evntParm[2];
      liDpc |= ((pc << 8) & 0x0000FF00);
      pc = mgmt->t.usta.evntParm[1];
      liDpc |= ((pc << 16) & 0x00FF0000);

      logger.logMsg (ERROR_FLAG, 0, "MTP3_ALARM_ Layer - Dpc Pause: %d", liDpc);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 
                                            __FILE__, __LINE__, 
                   BP_AIN_SM_ALM_DPC_PAUSE,
                   BP_AIN_SM_ALMENT_TUCL_CONNS, 0, 
                   " Point Code - Dpc <%d> Pause.",
                   liDpc);
#endif
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_RESUME, LCM_CAUSE_UNKNOWN):
      pc = ((int) mgmt->t.usta.evntParm[3]) & 0x000000FF;
      liDpc = pc;
      pc = mgmt->t.usta.evntParm[2];
      liDpc |= ((pc << 8) & 0x0000FF00);
      pc = mgmt->t.usta.evntParm[1];
      liDpc |= ((pc << 16) & 0x00FF0000);

      logger.logMsg(ERROR_FLAG, 0, "MTP3_ALARM_ Layer - Dpc Resume:%d.",
																																liDpc);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::NON_GAPPED, 									 __FILE__, __LINE__, 
                   BP_AIN_SM_ALM_DPC_RESUME,
                   BP_AIN_SM_ALMENT_TUCL_CONNS, 0, 
                   " - Dpc <%d> Resume.",
                   liDpc);
#endif
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_INH_DEN, LSN_CAUSE_UNINH_IN_PROG):
    case MMkIntegVal(ENTSN, LSN_EVENT_INH_DEN, LSN_CAUSE_NO_RMT_ACK):
    case MMkIntegVal(ENTSN, LSN_EVENT_INH_DEN, LSN_CAUSE_RMT_NEG_ACK):
    case MMkIntegVal(ENTSN, LSN_EVENT_INH_DEN, LSN_CAUSE_DPC_UNAVAIL):
    case MMkIntegVal(ENTSN, LSN_EVENT_INH_DEN, LSN_CAUSE_PATH_UNAVAIL):
    case MMkIntegVal(ENTSN, LSN_EVENT_INH_DEN, LSN_CAUSE_INVALID_DPC):
    case MMkIntegVal(ENTSN, LSN_EVENT_INH_DEN, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Link inhibit denied");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_UNINH_DEN, LSN_CAUSE_INH_IN_PROG):
    case MMkIntegVal(ENTSN, LSN_EVENT_UNINH_DEN, LSN_CAUSE_NO_RMT_ACK):
    case MMkIntegVal(ENTSN, LSN_EVENT_UNINH_DEN, LSN_CAUSE_PATH_UNAVAIL):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Link un-inhibit denied");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_LOC_INH_ACK, LCM_CAUSE_MGMT_INITIATED):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Link inhibit ack");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_REM_INH_ACK, LSN_CAUSE_RMT_INIT):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Remote Link inhibit ack");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_LOC_UNINHED, LCM_CAUSE_MGMT_INITIATED):
    case MMkIntegVal(ENTSN, LSN_EVENT_LOC_UNINHED, LSN_CAUSE_LOC_RTE_MGMT_INIT):
    case MMkIntegVal(ENTSN, LSN_EVENT_LOC_UNINHED, LSN_CAUSE_RMT_INIT):
    case MMkIntegVal(ENTSN, LSN_EVENT_LOC_UNINHED, LSN_CAUSE_SELF_RST):
    case MMkIntegVal(ENTSN, LSN_EVENT_LOC_UNINHED, LSN_CAUSE_ADJ_RST):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Local Link un-inhibit");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_REM_UNINHED, LSN_CAUSE_LOC_RTE_MGMT_INIT):
    case MMkIntegVal(ENTSN, LSN_EVENT_REM_UNINHED, LSN_CAUSE_RMT_INIT):
    case MMkIntegVal(ENTSN, LSN_EVENT_REM_UNINHED, LSN_CAUSE_SELF_RST):
    case MMkIntegVal(ENTSN, LSN_EVENT_REM_UNINHED, LSN_CAUSE_ADJ_RST):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Remote Link un-inhibit");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_RMT_BLKD, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Remote Blocked");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_RMT_UNBLKD, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Remote un-blocked");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_LOC_BLKD, LCM_CAUSE_MGMT_INITIATED):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Local blocked");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_LOC_UNBLKD, LCM_CAUSE_MGMT_INITIATED):
    case MMkIntegVal(ENTSN, LSN_EVENT_LOC_UNBLKD, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Local un-blocked");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_CONG, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Congestion");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_STPCONG, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - STP Congestion");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_RMTUSRUNAV, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Remote user un-available");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_INV_SLC_OTHER_END, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Other end invalid SLC");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_SDT_INV_DATA_DRP, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Invalid data dropped");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_SDT_INV_DATA_DRP, LCM_CAUSE_DECODE_ERR):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Invalid data dropped, Decoding problem");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_LSET_ACTIVE, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Linkset Active, cause unknown");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_LSET_INACTIVE, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Linkset Inactive, cause unknown");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_SNT_INV, LCM_CAUSE_INV_STATE):
    case MMkIntegVal(ENTSN, LSN_EVENT_SNT_INV, LCM_CAUSE_OUT_OF_RANGE):
    case MMkIntegVal(ENTSN, LSN_EVENT_SNT_INV, LCM_CAUSE_INV_MSG_LENGTH):
    case MMkIntegVal(ENTSN, LSN_EVENT_SNT_INV, LCM_CAUSE_INV_PAR_VAL):
    case MMkIntegVal(ENTSN, LSN_EVENT_SNT_INV, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - LSN_EVENT_SNT_INV");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_SDT_INV, LCM_CAUSE_INV_PAR_VAL):
    case MMkIntegVal(ENTSN, LSN_EVENT_SDT_INV, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - LSN_EVENT_SDT_INV");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_SRTEST, LCM_CAUSE_MGMT_INITIATED):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - Signal routing test complete");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_INV_OPC_OTHER_END, LCM_CAUSE_UNKNOWN):

      logger.logMsg(ERROR_FLAG, 0,
                          "MTP3_ALARM_ Layer - SLTM with invalid OPC");
      break;

    case MMkIntegVal(ENTSN, LSN_EVENT_CRE_HMAP_FLR, LCM_CAUSE_UNKNOWN):
    case MMkIntegVal(ENTSN, LSN_EVENT_CRE_HMAP_FLR, LSN_CAUSE_INVALID_DPC):
    case MMkIntegVal(ENTSN, LSN_EVENT_CRE_HMAP_FLR, LSN_CAUSE_DPC_UNAVAIL):

      logger.logMsg(ERROR_FLAG, 0,
									"MTP3_ALARM_ Layer - SLS - link mapping failed");
      break;


    default:
      logger.logMsg(ERROR_FLAG, 0,
                    "MTP3_ALARM_ Layer - Unrecognized event : %u , cause : %u",
                    mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmAlmHdlr::handleSnAlarm");

  return (BP_AIN_SM_OK);
}

// handle MTP2 alarms
int 
INGwSmAlmHdlr::handleSdAlarm(SdMngmt *mgmt) {

  return (BP_AIN_SM_OK);
}

// handle RELAY alarms
int 
INGwSmAlmHdlr::handleRyAlarm(RyMngmt *mgmt)
{
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleRyAlarm");

  char lpcTime[64];

  switch(mgmt->hdr.elmId.elmnt) {
    case LRY_USTA_ERR:
    {
      switch(mgmt->t.usta.s.ryErrUsta.reason){
        
        case LRYRSNPF:               
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - post failed"
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id); 

	      memset(lpcTime, 0, sizeof(lpcTime));
        lpcTime[0] = '1';
        g_getCurrentTime(lpcTime);
        printf("[+INC+] handleRyAlarm():: %s "
                "RELAY_ALARM_  - Error Alarm, reason - post failed"
                "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]\n",
                lpcTime, mgmt->t.usta.s.ryErrUsta.sequence,
                mgmt->t.usta.s.ryErrUsta.sendPid,
                mgmt->t.usta.s.ryErrUsta.errPid,
                mgmt->t.usta.s.ryErrUsta.id); fflush(stdout);

        break;

        case LRYRSNKARX:              

	      INGwSmBlkConfig::getInstance().updateRelayStatus(false);

        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - keep alive rx expired" 
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);  

	      memset(lpcTime, 0, sizeof(lpcTime));
        lpcTime[0] = '1';
        g_getCurrentTime(lpcTime);
        printf("[+INC+] handleRyAlarm():: %s "
               "RELAY_ALARM_  - Error Alarm, reason - keep alive rx expired" 
               "Error Sequence - [%d] Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]\n",
               lpcTime,
               mgmt->t.usta.s.ryErrUsta.sequence,
               mgmt->t.usta.s.ryErrUsta.sendPid,
               mgmt->t.usta.s.ryErrUsta.errPid,
               mgmt->t.usta.s.ryErrUsta.id); fflush(stdout); 

        if((false == INGwSmBlkConfig::getInstance().getRelayStatus()) &&
            (1 != mpDist->getTcapProvider()->myRole())) {
          printf("[+INC+] handleRyAlarm():: %s "
                 "Relay DOWN and self role is standby\n",
                 lpcTime); fflush(stdout);

          logger.logMsg(ERROR_FLAG, 0, "handleRyAlarm():: Relay DOWN "
              "and self role is standby.");

          pthread_t suicide;
          pthread_create(&suicide, NULL, launchThrdToChkSuicide, &(INGwSmBlkConfig::getInstance()));
        }
        
        break;

        case LRYRSNLCKFAIL:          
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - lock failed" 
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNB3LCKFAIL:         
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - bit3 lock failed" 
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNB3UNLCKFAIL:      
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - bit3 unlock failed"
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNHSTLCKFAIL:       
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - hst lock failed" 
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNHSTUNLCKFAIL:     
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - hst unlock failed" 
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNNOROUTE:         
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - no route"
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);

	      memset(lpcTime, 0, sizeof(lpcTime));
        lpcTime[0] = '1';
        g_getCurrentTime(lpcTime);
        printf("[+INC+] handleRyAlarm():: %s"
               "RELAY_ALARM_  - Error Alarm, reason - no route"
               "Error Sequence - [%d] Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]\n",
               lpcTime,
               mgmt->t.usta.s.ryErrUsta.sequence,
               mgmt->t.usta.s.ryErrUsta.sendPid,
               mgmt->t.usta.s.ryErrUsta.errPid,
               mgmt->t.usta.s.ryErrUsta.id); fflush(stdout);
         
        break;

        case LRYRSNREMDOWN:          
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - remote down"
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);

	      INGwSmBlkConfig::getInstance().updateRelayStatus(false);

	      memset(lpcTime, 0, sizeof(lpcTime));
        lpcTime[0] = '1';
        g_getCurrentTime(lpcTime);
        printf("[+INC+] handleRyAlarm():: %s "
                "RELAY_ALARM_  - Error Alarm, reason - remote down"
                "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]\n",
                 lpcTime, mgmt->t.usta.s.ryErrUsta.sequence,
                 mgmt->t.usta.s.ryErrUsta.sendPid,
                 mgmt->t.usta.s.ryErrUsta.errPid,
                 mgmt->t.usta.s.ryErrUsta.id); fflush(stdout);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
	 		__FILE__, __LINE__, BP_AIN_SM_ALM_CHANNEL_DOWN, "Relay remote down", 0,
	 		" - RELAY_ALARM_ - Error Alarm, reason - remote down");
#endif
        break;

        case LRYRSNBRDFAIL:          
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - board failure" 
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNMGMTREQ:          
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - management request" 
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNHSTSHMEMFAIL:    
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - hst shared memory init failed"
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNRAWSHMEMFAIL:    
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - raw shared memory init failed" 
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNNOTFIRSTBD:       
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - not a first buffer descriptor"
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNEMPTYBD:          
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - empty buffer descriptor" 
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNWRONGSTATE:      
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - wrong state" 
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNB3SHMEMFAIL:     
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - b3 shared memory init failed" 
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNKHSHMEMFAIL:    
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason -  KH shared memory init failed" 
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;

        case LRYRSNS5SHMMEMFAIL:    
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Error Alarm, reason - Sys V shared memory init failed"
                      "Error Sequence - [%d]Sending ProcId[%d], Error ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryErrUsta.sequence,
                       mgmt->t.usta.s.ryErrUsta.sendPid,
                       mgmt->t.usta.s.ryErrUsta.errPid,
                       mgmt->t.usta.s.ryErrUsta.id);
        break;
       
        default:
    
        logger.logMsg(ERROR_FLAG, 0,"RELAY_ALARM_  - Error Alarm, Unknown Reason");
        
      }
    }
    break;

    case LRY_USTA_CNG: 
    {
      switch(mgmt->t.usta.s.ryCongUsta.flags) { 
        case LRY_CONG_NONE:
        //RAISE ALM
        logger.logMsg(ALWAYS_FLAG, 0,
                      "RELAY_ALARM_  - Cong Alarm, No Congestion"
                      "Channel Status [%d], Sending ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryCongUsta.status,
                       mgmt->t.usta.s.ryCongUsta.sendPid,
                       mgmt->t.usta.s.ryCongUsta.id);

	      memset(lpcTime, 0, sizeof(lpcTime));
        lpcTime[0] = '1';
        g_getCurrentTime(lpcTime);
        printf("[+INC+] handleRyAlarm():: %s "
               "RELAY_ALARM_  - Cong Alarm, No Congestion"
               "Channel Status [%d], Sending ProcId[%d], ChannelId[%d]\n",
               lpcTime,
               mgmt->t.usta.s.ryCongUsta.status,
               mgmt->t.usta.s.ryCongUsta.sendPid,
               mgmt->t.usta.s.ryCongUsta.id); fflush(stdout);
        

        break;
        case LRY_CONG_CONG:
        //RAISE ALM
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Cong Alarm, Channel Congested" 
                      "Channel Status - [%d]Sending ProcId[%d], ChannelId[%d]",
                       mgmt->t.usta.s.ryCongUsta.status,
                       mgmt->t.usta.s.ryCongUsta.sendPid,
                       mgmt->t.usta.s.ryCongUsta.id);

	      memset(lpcTime, 0, sizeof(lpcTime));
        lpcTime[0] = '1';
        g_getCurrentTime(lpcTime);
        printf("[+INC+] handleRyAlarm():: %s "
               "RELAY_ALARM_  - Cong Alarm, Channel Congested" 
               "Channel Status - [%d]Sending ProcId[%d], ChannelId[%d]\n",
               lpcTime,
               mgmt->t.usta.s.ryCongUsta.status,
               mgmt->t.usta.s.ryCongUsta.sendPid,
               mgmt->t.usta.s.ryCongUsta.id); fflush(stdout);
         

        break;
        case LRY_CONG_DROP:
        //RAISE ALM
        logger.logMsg(ERROR_FLAG, 0,
               "RELAY_ALARM_  - Cong Alarm, reason - Channel Dropping Packets" 
               "Channel Status - [%d]Sending ProcId[%d], ChannelId[%d]",
                mgmt->t.usta.s.ryCongUsta.status,
                mgmt->t.usta.s.ryCongUsta.sendPid,
                mgmt->t.usta.s.ryCongUsta.id);

	      memset(lpcTime, 0, sizeof(lpcTime));
        lpcTime[0] = '1';
        g_getCurrentTime(lpcTime);
        printf("[+INC+] handleRyAlarm():: %s "
               "RELAY_ALARM_  - Cong Alarm, reason - Channel Dropping Packets" 
                "Channel Status - [%d] Sending ProcId[%d], ChannelId[%d]\n",
                lpcTime,
                mgmt->t.usta.s.ryCongUsta.status,
                mgmt->t.usta.s.ryCongUsta.sendPid,
                mgmt->t.usta.s.ryCongUsta.id); fflush(stdout);
         

        break;
        
        default:
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Cong Alarm, Unknown Flag"); 

      }
    }
    break;

    case LRY_USTA_UP:
    {
        //RAISE ALM
	      INGwSmBlkConfig::getInstance().updateRelayStatus(true);
        logger.logMsg(ALWAYS_FLAG, 0,
                      "RELAY_ALARM_  - Channel Status Alarm, Channel Up,"
                      " Sending ProcId [%d],"
                      " Channel Id [%d]",mgmt->t.usta.s.ryUpUsta.sendPid,
                        mgmt->t.usta.s.ryUpUsta.id); 

	      memset(lpcTime, 0, sizeof(lpcTime));
        lpcTime[0] = '1';
        g_getCurrentTime(lpcTime);
        printf("[+INC+] handleRyAlarm():: %s "
                "RELAY_ALARM_  - Channel Status Alarm, Channel Up,"
                "Sending ProcId [%d], Channel Id [%d]\n",
                lpcTime, mgmt->t.usta.s.ryUpUsta.sendPid,
                mgmt->t.usta.s.ryUpUsta.id); fflush(stdout);

        int selfRole = mpDist->getTcapProvider()->myRole();
        logger.logMsg(ALWAYS_FLAG, 0, "handleRyAlarm():: Relay Channel UP "
            " Self role<%s>. SelfStackState<%d> PeerStackState<%d> "
            "SelfSgRole<%s>",
            (selfRole == 1)?"ACTIVE":"STANDBY",
            INGwSmBlkConfig::getInstance().m_selfState,
            INGwSmBlkConfig::getInstance().m_peerState,
            INGwTcapProvider::getInstance().getAinSilTxRef().getSelfSgRole().c_str());

        // Check if self role is active and peer stack in uninitialized
        // then configure the peer stack.
        if((true == INGwSmBlkConfig::getInstance().getRelayStatus()) &&
           (1 == selfRole)) {
          if(INGwSmBlkConfig::getInstance().m_selfState == INGwSmBlkConfig::INITIALIZED || 
             INGwSmBlkConfig::getInstance().m_peerState == INGwSmBlkConfig::UNINITIALIZED) {
            INGwSmBlkConfig::getInstance().peerUp();
          }
        }
        

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
	 		__FILE__, __LINE__, BP_AIN_SM_ALM_CHANNEL_UP, "Relay Up", 0,
	 		" - RELAY_ALARM_ - Channel Status Alarm, Channel Up");
#endif

    }        
    
    break;
    case LRY_USTA_DN: 
    {
        //RAISE ALM
	      INGwSmBlkConfig::getInstance().updateRelayStatus(false);
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - Channel Status Alarm, Channel Down"
                      " Sending ProcId [%d],"
                      " Channel Id [%d]",mgmt->t.usta.s.ryUpUsta.sendPid,
                       mgmt->t.usta.s.ryUpUsta.id); 

	      memset(lpcTime, 0, sizeof(lpcTime));
        lpcTime[0] = '1';
        g_getCurrentTime(lpcTime);
        printf("[+INC+] handleRyAlarm():: %s "
               "RELAY_ALARM_  - Channel Status Alarm, Channel Down"
               " Sending ProcId [%d], Channel Id [%d]\n",
               lpcTime, mgmt->t.usta.s.ryUpUsta.sendPid,
               mgmt->t.usta.s.ryUpUsta.id); 

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
      __FILE__, __LINE__, BP_AIN_SM_ALM_CHANNEL_DOWN, "Relay down", 0,
      " - RELAY_ALARM_ - Channel Status Alarm, Channel Down");
#endif


    }
    break;

    case LRY_USTA_TCP_CONN_FAILED:
    {
         //RAISE ALM
        INGwSmBlkConfig::getInstance().updateRelayStatus(false);
        logger.logMsg(ERROR_FLAG, 0,
                      "RELAY_ALARM_  - connect failed Alarm,"
                      " Sending ProcId [%d],"
                      " Channel Id [%d]",mgmt->t.usta.s.ryUpUsta.sendPid,
                       mgmt->t.usta.s.ryUpUsta.id); 

	      memset(lpcTime, 0, sizeof(lpcTime));
        lpcTime[0] = '1';
        g_getCurrentTime(lpcTime);
        printf("[+INC+] handleRyAlarm():: %s "
                "RELAY_ALARM_  - connect failed Alarm,"
                "Sending ProcId [%d], Channel Id [%d]\n",
                lpcTime, mgmt->t.usta.s.ryUpUsta.sendPid,
                mgmt->t.usta.s.ryUpUsta.id); fflush(stdout);

#if (defined (_BP_CCM_ALMMGR_) && !defined (STUBBED))
      INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::GAPPED,
      __FILE__, __LINE__, BP_AIN_SM_ALM_CHANNEL_DOWN, "Relay down", 0,
      " - RELAY_ALARM_ - connect failed Alarm");
#endif

    }
    break;

    default:
      logger.logMsg(ERROR_FLAG, 0,
                    "RELAY_ALARM_- Unrecognized Alarm: %d ",
                     mgmt->hdr.elmId.elmnt);
      return (BP_AIN_SM_FAIL);
  }
 


  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handleRyAlarm");

  return (BP_AIN_SM_OK);
}

//LDF_M3UA alarms
int
INGwSmAlmHdlr::handleDvAlarm(LdvMngmt *mgmt) {
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleDvAlarm");

  integ = MMkIntegVal(ENTDV, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTDV, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_INV_SAP):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_M3UA_ALARM_ - The SAP for Sap Id [%d] not configured",
                     mgmt->t.usta.info.parm.sapId);
    break;
      
    case MMkIntegVal(ENTDV, LCM_EVENT_LI_INV_EVT, LDV_CAUSE_INV_NWKAPP):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_M3UA_ALARM_ - The network appearance not configured");

    break;

    case MMkIntegVal(ENTDV, LCM_EVENT_INV_EVT, LCM_CAUSE_DECODE_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_M3UA_ALARM_ - a primitive with invalid "
                    "version no. is received on the LDV interface"); 

    break;

    case MMkIntegVal(ENTDV, LDV_EVENT_RSET_MAP_FAIL, LDV_CAUSE_NO_CRIT_RSET):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_M3UA_ALARM_ - No critical resource ID configured"); 
                     
    break;
    case MMkIntegVal(ENTDV, LDV_EVENT_RSET_MAP_FAIL, LDV_CAUSE_NO_DEF_RSET):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_M3UA_ALARM_ - No default resource ID configured");
    
    break;
    case MMkIntegVal(ENTDV, LDV_EVENT_RSET_MAP_FAIL, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_M3UA_ALARM_ - Unknown mapping failure"); 

    break;
    case MMkIntegVal(ENTDV, LDV_EVENT_MR_INV_EVT, LDV_CAUSE_MR_RSET_STATUS_FAIL):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_M3UA_ALARM_ - MR primitive failure"); 

    break;

    default:
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_M3UA_ALARM_ Layer - Unrecognized event : %u , cause : %u",
                    mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmAlmHdlr::handleDvAlarm");

  return (BP_AIN_SM_OK);
}

//LDF_MTP3 alarms
int
INGwSmAlmHdlr::handleDnAlarm(LdnMngmt *mgmt) {
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleDnAlarm");

  integ = MMkIntegVal(ENTDN, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTDN, LCM_EVENT_UI_INV_EVT, LCM_CAUSE_INV_SAP):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_MTP3_ALARM_ - The SAP for Sap Id [%d] not configured LCM_EVENT_UI_INV_EVT",
                     mgmt->t.usta.info.parm.sapId);
    break;
      
    case MMkIntegVal(ENTDN, LCM_EVENT_LI_INV_EVT, LCM_CAUSE_INV_SAP):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_MTP3_ALARM_ - Sap not configured LCM_EVENT_LI_INV_EVT");

    break;

    case MMkIntegVal(ENTDN, LCM_EVENT_MI_INV_EVT, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_MTP3_ALARM_ - Unknown message in management interface LCM_EVENT_MI_INV_EVT");

    break;

    case MMkIntegVal(ENTDV, LCM_EVENT_INV_EVT, LCM_CAUSE_DECODE_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_MTP3_ALARM_ - a primitive with invalid "
                    "version no. is received on the LDN interface"); 

    break;

    case MMkIntegVal(ENTDV, LDN_EVENT_RSET_MAP_FAIL, LDN_CAUSE_NO_CRIT_RSET):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_MTP3_ALARM_ - No critical resource ID configured LDN_EVENT_RSET_MAP_FAIL"); 
                     
    break;
    case MMkIntegVal(ENTDV, LDN_EVENT_RSET_MAP_FAIL, LDN_CAUSE_NO_DEF_RSET):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_MTP3_ALARM_ - No default resource ID configured LDN_EVENT_RSET_MAP_FAIL");
    
    break;
    case MMkIntegVal(ENTDV, LDN_EVENT_RSET_MAP_FAIL, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_MTP3_ALARM_ - Unknown mapping failure"); 

    break;
    case MMkIntegVal(ENTDV, LDN_EVENT_MR_INV_EVT, LDN_CAUSE_MR_RSET_STATUS_FAIL):
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_MTP3_ALARM_ - MR primitive failure"); 

    break;

    default:
      logger.logMsg(ERROR_FLAG, 0,
                    "LDF_MTP3_ALARM_ Layer - Unrecognized event : %u , cause : %u",
                    mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmAlmHdlr::handleDnAlarm");

  return (BP_AIN_SM_OK);
}

//start PSF-TCAP
int
INGwSmAlmHdlr::handleZtAlarm(ZtMngmt *mgmt) {
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleZtAlarm");

  integ = MMkIntegVal(ENTDN, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTZT, LCM_EVENT_SEQERR, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_TCAP_ALARM_ - Runtime sequence error detected");
     //at this point of time we should make s'by go OOS?? INCTBD 
    break;

    case MMkIntegVal(ENTZT, LCM_EVENT_UPDMSG_ERR, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_TCAP_ALARM_ - Wrong update message received by the active PSF-TCAP"); 
    break;

    case MMkIntegVal(ENTZT, LCM_EVENT_UI_INV_EVT, LZT_CAUSE_DLGFAIL_DUETO_RECOVERY):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_TCAP_ALARM_ - New dialogue open failed due to recovery"); 
  
    case MMkIntegVal(ENTZT, LCM_EVENT_HTBT_EXP, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_TCAP_ALARM_ - Receive transmit timer expires"); 
    default:
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_TCAP_ALARM_ Layer - Unrecognized event : %u , cause : %u",
                    mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmAlmHdlr::handleZtAlarm");

  return (BP_AIN_SM_OK);
}


//end   PSF-TCAP

//start PSF-SCCP
int
INGwSmAlmHdlr::handleZpAlarm(ZpMngmt *mgmt) {
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleZpAlarm");

  integ = MMkIntegVal(ENTZP, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTZP, LCM_EVENT_DMEM_ALLOC_FAIL, LCM_CAUSE_UNKNOWN)://RAISE ALM
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_SCCP_ALARM_ - Error in allocating dynamic memory");
      break;

    case MMkIntegVal(ENTZP, LCM_EVENT_HTBT_EXP, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_SCCP_ALARM_ - S'by copy has missed a h'beat message in an interval");
      break;

    case MMkIntegVal(ENTZP, LCM_EVENT_UPDMSG_ERR, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_SCCP_ALARM_ - wrongly formed Upd msg received by s'by copy");
      break;

    case MMkIntegVal(ENTZP, LCM_EVENT_SEQERR, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_SCCP_ALARM_ - Upd msg with wrong Seq No. received");
      break;

    case MMkIntegVal(ENTZP, LCM_EVENT_OOM, LCM_CAUSE_UNKNOWN): //RAISE ALM
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_SCCP_ALARM_ - Out of Memory -Sccp needs more memory");
      break;

    case MMkIntegVal(ENTZP, LCM_EVENT_INV_EVT, LCM_CAUSE_UNKNOWN)://RAISE ALM
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_SCCP_ALARM_ - Out of Memory -Sccp needs more memory");
      break;
    default:
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_SCCP_ALARM_ - Unrecognized event : %u , cause : %u",
                    mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmAlmHdlr::handleZpAlarm");

  return (BP_AIN_SM_OK);
} 
//end   PSF-SCCP

//start PSF-MTP3
int
INGwSmAlmHdlr::handleZnAlarm(ZnMngmt *mgmt) {
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleZnAlarm");

  integ = MMkIntegVal(ENTZN, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTZN, LCM_EVENT_DMEM_ALLOC_FAIL, LCM_CAUSE_UNKNOWN)://RAISE ALM
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_MTP3_ALARM_ - Error in allocating dynamic memory");
      break;

    case MMkIntegVal(ENTZN, LCM_EVENT_HTBT_EXP, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_MTP3_ALARM_ - S'by copy has missed a h'beat message in an interval");
      break;

    case MMkIntegVal(ENTZN, LCM_EVENT_UPDMSG_ERR, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_MTP3_ALARM_ - Wrongly formed Upd msg received by s'by copy");
      break;

    case MMkIntegVal(ENTZN, LCM_EVENT_UPDMSG_ERR, LCM_CAUSE_VERSION_MISMATCH):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_MTP3_ALARM_ - Mismatch in version numbers");
      break;

    case MMkIntegVal(ENTZN, LCM_EVENT_SEQERR, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_MTP3_ALARM_ - Upd msg with wrong Seq No. received");
      break;

    case MMkIntegVal(ENTZN, LCM_EVENT_OOM, LCM_CAUSE_UNKNOWN)://RAISE ALM
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_MTP3_ALARM_ - Out of Memory");
      break;

    case MMkIntegVal(ENTZN, LCM_EVENT_INV_EVT, LCM_CAUSE_DECODE_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_MTP3_ALARM_ - LCM_EVENT_INV_EVT,LCM_CAUSE_DECODE_ERR");
      break;
    default:
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_MTP3_ALARM_ - Unrecognized event : %u , cause : %u",
                    mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmAlmHdlr::handleZnAlarm");

  return (BP_AIN_SM_OK);
}
//end   PSF-MTP3

//start PSF-M3UA
int
INGwSmAlmHdlr::handleZvAlarm(ZvMngmt *mgmt) {
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleZvAlarm");

  integ = MMkIntegVal(ENTZV, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTZV, LCM_EVENT_DMEM_ALLOC_FAIL, LCM_CAUSE_UNKNOWN)://RAISE ALM
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_M3UA_ALARM_ - Error in allocating dynamic memory");
      break;

    case MMkIntegVal(ENTZV, LCM_EVENT_HTBT_EXP, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_M3UA_ALARM_ - S'by copy has missed a h'beat message in an interval");
      break;

    case MMkIntegVal(ENTZV, LCM_EVENT_UPDMSG_ERR, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_M3UA_ALARM_ - Wrongly formed Upd msg received by s'by copy");
      break;

    case MMkIntegVal(ENTZV, LCM_EVENT_UPDMSG_ERR, LCM_CAUSE_VERSION_MISMATCH):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_M3UA_ALARM_ - Mismatch in version numbers");
      break;

    case MMkIntegVal(ENTZV, LCM_EVENT_SEQERR, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_M3UA_ALARM_ - Upd msg with wrong Seq No. received");
      break;

    case MMkIntegVal(ENTZV, LCM_EVENT_OOM, LCM_CAUSE_UNKNOWN)://RAISE ALM
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_M3UA_ALARM_ - Out of Memory");
      break;

    case MMkIntegVal(ENTZV, LCM_EVENT_INV_EVT, LCM_CAUSE_DECODE_ERR):
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_M3UA_ALARM_ - Out of Memory");
      break;
    default:
      logger.logMsg(ERROR_FLAG, 0,
                    "PSF_M3UA_ALARM_ - Unrecognized event : %u , cause : %u",
                    mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmAlmHdlr::handleZvAlarm");

  return (BP_AIN_SM_OK);
}
//end   PSF-M3UA
// handle the AS_PENDING message condition, where the other
// CMM/ASP has gone down, or cannot be seen by the SG. in this
// case, if this ASP is Standby/Inactive, then it should initiate
// the role of taking over as Active.
// note: in this method no parameter is currently passed, but if
//       we to support mulitiple clusters of ASP's may need to
//       pass the list of affected pspId's.
int
INGwSmAlmHdlr::handleAsPendingAlm()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleAsPendingAlm");

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handleAsPendingAlm");

#if (defined (_BP_AIN_PROVIDER_) && !defined (STUBBED))
  // indicate to the INGwTcapProvider that the sibling ASP went went down
  // as we've got the AS_PENDING message, so check out status and
  // initiate failover if necessary.
  return mpDist->getTcapProvider()->handleAinSmEvent(0);
#else
  return BP_AIN_SM_OK;
#endif
}

#ifdef INC_ASP_SNDDAUD
int INGwSmAlmHdlr::sendDaud(ItPsId psId)
{
  logger.logMsg (ALWAYS_FLAG, 0, "Entering sendDaud(), psId:%d",psId);

  Ss7SigtranSubsReq *req = new Ss7SigtranSubsReq;
  memset ((void *)req, 0, sizeof(Ss7SigtranSubsReq));

  vector<int> lprocIdList;
  lprocIdList.push_back(SFndProcId());

  req->u.daud.psId = psId;
  AsSeq * asList = INGwSmBlkConfig::getInstance().getAsList();
  int found = 0;
  AsSeq::iterator asIt;
	for(asIt = asList->begin(); asIt != asList->end(); asIt++) {
    if((*asIt).psId == psId) {
      if ((*asIt).lFlag == true) {
        logger.logMsg(ALWAYS_FLAG, 0, 
                      "sendDaud() LOCAL PS, so not sending DAUD for "
                      "PsId<%d>", req->u.daud.psId);
        found = 1;
        break;
      }
      else {
        req->u.daud.dpc = (*asIt).dpc;
        req->u.daud.nwkId = (*asIt).nwkId;
        logger.logMsg(ALWAYS_FLAG, 0, "sendDaud() NwId<%d> Dpc<0x%x> PsId<%d>",
                      req->u.daud.nwkId, req->u.daud.dpc, req->u.daud.psId);
        found = 2;
        break;
      }
    }
  }

  if (found == 2) {
	  req->cmd_type  = SEND_DAUD;
    INGwSmConfigQMsg *lqueueMsg  =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = lprocIdList;
    lqueueMsg->from       = 2;

    logger.logMsg (ALWAYS_FLAG, 0, 
	         "sendDaud()::Posting DAUD REQUEST with psId<%d>", psId);

    mpDist->mpSmWrapper->postMsg(lqueueMsg,true);
    //if (mpDist->mpSmWrapper->postMsg(lqueueMsg,true) != BP_AIN_SM_OK) {
    //  logger.logMsg (ERROR_FLAG, 0, 
	  //         "sendDaud()::PostMsg failed for DAUD REQUEST with psId<%d>", psId);
    //  delete lqueueMsg;
    //  lqueueMsg = NULL;
    //  delete req;
    //  req = NULL;
    //}
  }
  else if (found == 1) {
    delete req;
  }
  else {
    logger.logMsg (ERROR_FLAG, 0, 
	         "sendDaud()::psId<%d> not found in list", psId);
    delete req;
  }
  
  logger.logMsg (ALWAYS_FLAG, 0, "Exiting sendDaud()");
  return BP_AIN_SM_OK;
}
#endif



int INGwSmAlmHdlr::sendInitReq(int pspid, int sctsuid)
{

  logger.logMsg (ALWAYS_FLAG, 0,
    "Entering INGwSmAlmHdlr::sendInitReq(), pspid:%d, sctSuId:%d",pspid,sctsuid);

    Ss7SigtranSubsReq *req = new Ss7SigtranSubsReq;
    memset ((void *)req, 0, sizeof(Ss7SigtranSubsReq));

    vector<int> lprocIdList;
 
     
    req->u.m3uaAssocUp.pspId = pspid;
    req->u.m3uaAssocUp.m3uaLsapId = sctsuid;

    lprocIdList.push_back(INGwSmBlkConfig::getInstance().getAssocProcId(req->u.m3uaAssocUp));
	  req->cmd_type  = SEND_INIT;
    INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = lprocIdList;
    lqueueMsg->from       = 2;

    logger.logMsg (ALWAYS_FLAG, 0, 
	    "sendInitReq()::Posting SEND_INIT with  "
      "pspId <%d>, m3uaLsap <%d>",
      lqueueMsg->req->u.m3uaAssocUp.pspId,
      lqueueMsg->req->u.m3uaAssocUp.m3uaLsapId );

    mpDist->mpSmWrapper->postMsg(lqueueMsg,true);
    //if (mpDist->mpSmWrapper->postMsg(lqueueMsg,true) != BP_AIN_SM_OK) {
    //  logger.logMsg (ERROR_FLAG, 0, "sendInitReq()::PostMsg failed for "
    //                 "SCTP INIT REQUEST with pspId<%d> m3uaLsapId<%d>",
    //                 pspid, sctsuid);
    //  delete lqueueMsg;
    //  lqueueMsg = NULL;
    //  delete req;
    //  req = NULL;
    //}
    logger.logMsg (VERBOSE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::sendInitReq()");

  return 1;
}


int INGwSmAlmHdlr::sendAspActvCmd(int pspid, int sctsuid)
{

  logger.logMsg (ALWAYS_FLAG, 0,
    "Entering INGwSmAlmHdlr::sendAspActvCmd(), pspid:%d, sctSuId:%d",pspid,sctsuid);

    Ss7SigtranSubsReq *req = new Ss7SigtranSubsReq;
    memset ((void *)req, 0, sizeof(Ss7SigtranSubsReq));

    vector<int> lprocIdList;
  
    req->u.m3uaAspAct.pspId = pspid;
    req->u.m3uaAspAct.m3uaLsapId = sctsuid;

    lprocIdList.push_back(INGwSmBlkConfig::getInstance().getAssocProcId_Aspactv(req->u.m3uaAspAct));
	  req->cmd_type  = ASP_ACTIVE;
    INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = lprocIdList;
    lqueueMsg->from       = 2;

    logger.logMsg (ALWAYS_FLAG, 0, 
	    "sendAspActvCmd()::Posting ASP_ACTIVE with  "
      "pspId <%d>, m3uaLsap <%d>",
      lqueueMsg->req->u.m3uaAspAct.pspId,
      lqueueMsg->req->u.m3uaAspAct.m3uaLsapId );

    mpDist->mpSmWrapper->postMsg(lqueueMsg,true);
    logger.logMsg (VERBOSE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::sendAspActvCmd()");

  return 1;
}
#if 0
int INGwSmAlmHdlr::sendAspUpCmd(int pspid, int sctsuid)
{

  logger.logMsg (ALWAYS_FLAG, 0,
    "Entering INGwSmAlmHdlr::sendAspUpCmd(), pspid:%d, sctSuId:%d",pspid,sctsuid);

    Ss7SigtranSubsReq *req = new Ss7SigtranSubsReq;
    memset ((void *)req, 0, sizeof(Ss7SigtranSubsReq));

    vector<int> lprocIdList;
  
    req->u.m3uaAspUp.pspId = pspid;
    req->u.m3uaAspUp.m3uaLsapId = sctsuid;

    lprocIdList.push_back(INGwSmBlkConfig::getInstance().getAssocProcId_Aspup(req->u.m3uaAspUp));
	  req->cmd_type  = ASP_UP;
    INGwSmConfigQMsg *lqueueMsg      =  new INGwSmConfigQMsg;
    lqueueMsg->req = req;
    lqueueMsg->src = BP_AIN_SM_SRC_CCM;
    lqueueMsg->procIdList = lprocIdList;
    lqueueMsg->from       = 2;

    logger.logMsg (ALWAYS_FLAG, 0, 
	    "sendAspUpCmd()::Posting ASP_UP with  "
      "pspId <%d>, m3uaLsap <%d>",
      lqueueMsg->req->u.m3uaAspUp.pspId,
      lqueueMsg->req->u.m3uaAspUp.m3uaLsapId );

    mpDist->mpSmWrapper->postMsg(lqueueMsg,true);
    logger.logMsg (VERBOSE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::sendAspUpCmd()");

  return 1;
}
#endif

int
INGwSmAlmHdlr::handleAspmAckCase(ItMgmt *mgmt)
{
  INGwSmQueueMsg *qMsg = 0;
  INGwSmAlmOpType aAlmOp;

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleAspmAckCase");

  if ((qMsg = new INGwSmQueueMsg) == 0) {
    // memory alloc failure -- alarm    
    return (BP_AIN_SM_FAIL);
  }

  memset (qMsg, 0, sizeof (INGwSmQueueMsg));

  switch (mgmt->t.usta.t.aspm.msgType) {

    case LIT_ASPM_ASPUP_ACK:
      aAlmOp = BP_AIN_SM_ALMOP_ASPUP_ACK;
      break;

    case LIT_ASPM_ASPDN_ACK:
      aAlmOp = BP_AIN_SM_ALMOP_ASPDN_ACK;
      break;

    case LIT_ASPM_ASPAC_ACK:
      aAlmOp = BP_AIN_SM_ALMOP_ASPAC_ACK;
      break;

    case LIT_ASPM_ASPIA_ACK:
      aAlmOp = BP_AIN_SM_ALMOP_ASPIA_ACK;
      break;

    default:
      delete qMsg;
      return (BP_AIN_SM_FAIL);
  }

  switch (aAlmOp) {

    case BP_AIN_SM_ALMOP_ASPIA_ACK:
    case BP_AIN_SM_ALMOP_ASPUP_ACK:
      qMsg->t.almOper.almOp.aspmInfo.aspDnReason =
                            BP_AIN_SM_ASPDN_REASON_INVALID;
      qMsg->t.almOper.almOp.aspmInfo.trfModeType = 
                            BP_AIN_SM_TRFMODE_INVALID; 
      break;

    case BP_AIN_SM_ALMOP_ASPAC_ACK:
      qMsg->t.almOper.almOp.aspmInfo.aspDnReason =
                            BP_AIN_SM_ASPDN_REASON_INVALID;

      switch (mgmt->t.usta.t.aspm.tmType) {

        case LIT_ASP_TMTYPE_OVERRIDE:
          qMsg->t.almOper.almOp.aspmInfo.trfModeType = 
                              BP_AIN_SM_TRFMODE_OVERRIDE;
          break;

        case LIT_ASP_TMTYPE_LOADSHARE:
          qMsg->t.almOper.almOp.aspmInfo.trfModeType = 
                              BP_AIN_SM_TRFMODE_LOADSHARE;
          break;

        default:
          delete qMsg;
          return (BP_AIN_SM_FAIL);

      }

      break;

    case BP_AIN_SM_ALMOP_ASPDN_ACK:
                            
      qMsg->t.almOper.almOp.aspmInfo.trfModeType = 
                            BP_AIN_SM_TRFMODE_INVALID; 
#if 0
/* TODO */
      switch (mgmt->t.usta.t.aspm.reason) {

        case LIT_ASPDN_REASON_UNSPEC:
          qMsg->t.almOper.almOp.aspmInfo.aspDnReason =
                              BP_AIN_SM_ASPDN_REASON_UNSPECIFIED;
          break;

        case LIT_ASPDN_REASON_USER_UNAVAIL:
          qMsg->t.almOper.almOp.aspmInfo.aspDnReason =
                              BP_AIN_SM_ASPDN_REASON_USR_UNAVL;
          break;

        case LIT_ASPDN_REASON_MGMNTINH:
          qMsg->t.almOper.almOp.aspmInfo.aspDnReason =
                              BP_AIN_SM_ASPDN_REASON_MGMT_INHIBIT;
          break;

        default:
          delete qMsg;
          return (BP_AIN_SM_FAIL);

      }
#endif
 
      break;

    default:
      delete qMsg;
      return BP_AIN_SM_FAIL;
  }

  qMsg->t.almOper.almOp.aspmInfo.nmbPs = 
                          mgmt->t.usta.t.aspm.nmbPs;
  for (int i=0; i < mgmt->t.usta.t.aspm.nmbPs; i++) 
  {
    qMsg->t.almOper.almOp.aspmInfo.psIdArr[i] =
                          mgmt->t.usta.t.aspm.psLst[i];
  }

  qMsg->mSrc = BP_AIN_SM_SRC_STACK_ALMHDLR;   // msg from alm hdlr for ctrl
  qMsg->t.almOper.mOpType = aAlmOp; 
  qMsg->t.almOper.almOp.aspmInfo.pspId = mgmt->t.usta.s.pspId;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handleAspmAckCase");

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) == BP_AIN_SM_OK) {
    return (BP_AIN_SM_OK);
  }
}


int
INGwSmAlmHdlr::handlePspAlmOp(INGwSmAlmOpType aAlmOp,
                               ItPspId aPspId, ItPsId aPsId, SuId asctSuId)
{
  INGwSmQueueMsg *qMsg = 0;

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handlePspAlmOp for pspId:%u almOp:%u psId:%d", aPspId, aAlmOp,aPsId);

  if ((qMsg = new INGwSmQueueMsg) == 0) {
    // memory alloc failure -- alarm    
    return (BP_AIN_SM_FAIL);
  }

  memset (qMsg, 0, sizeof (INGwSmQueueMsg));

  qMsg->mSrc = BP_AIN_SM_SRC_STACK_ALMHDLR;   // msg from alm hdlr for ctrl
  qMsg->t.almOper.mOpType = aAlmOp; 
  qMsg->t.almOper.almOp.pspInfo.pspId = aPspId; 
  qMsg->t.almOper.almOp.pspInfo.sctSuId = asctSuId; 
  if(aAlmOp == BP_AIN_SM_ALMOP_ASP_ST_UPD_DOWN || aAlmOp == BP_AIN_SM_ALMOP_ASP_ST_UPD_INACT ||
      aAlmOp == BP_AIN_SM_ALMOP_ASP_ST_UPD_ACTV)
    qMsg->t.almOper.almOp.pspInfo.psId = aPsId;

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handlePspAlmOp");

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) == BP_AIN_SM_OK) {
    return (BP_AIN_SM_OK);
  }
}

int
INGwSmAlmHdlr::handleCommDownAlm(INGwSmCommDownCause aCause,
                                  ItPspId aPspId, SuId asctSuId)
{
  INGwSmQueueMsg *qMsg = 0;

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleCommDownAlm for pspId:%u cause:%u sctSuId:%d", aPspId, aCause,asctSuId);

  if ((qMsg = new INGwSmQueueMsg) == 0) {
    // memory alloc failure -- alarm    
    return (BP_AIN_SM_FAIL);
  }

  memset (qMsg, 0, sizeof (INGwSmQueueMsg));

  qMsg->mSrc = BP_AIN_SM_SRC_STACK_ALMHDLR;   // msg from alm hdlr for ctrl
  qMsg->t.almOper.mOpType = BP_AIN_SM_ALMOP_COMM_DOWN; 
  qMsg->t.almOper.almOp.commDn.cause = aCause; 
  qMsg->t.almOper.almOp.commDn.pspId = aPspId; 
  qMsg->t.almOper.almOp.commDn.sctSuId = asctSuId; 

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving INGwSmAlmHdlr::handleCommDownAlm");

  if (INGwSmAdaptor::sendMsgToDispatcher(qMsg, false) == BP_AIN_SM_OK) {
    return (BP_AIN_SM_OK);
  }
}

//SH ALARMS 
int
INGwSmAlmHdlr::handleShAlarm(ShMngmt *mgmt) {
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleShAlarm");

  integ = MMkIntegVal(ENTSH, mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTSH, LCM_EVENT_INV_EVT, LCM_CAUSE_INV_PAR_VAL):
      logger.logMsg(ERROR_FLAG, 0,
                    "SH_ALARM_ - Invalid parameter is received in a layer management req");
      break;

    case MMkIntegVal(ENTSH, LCM_EVENT_NAK, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "SH_ALARM_ - A negative ack is received");
      break;

    case MMkIntegVal(ENTSH, LCM_EVENT_TIMEOUT, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "SH_ALARM_ - An ack was not received");
      break;

    case MMkIntegVal(ENTSH, LCM_EVENT_INV_EVT, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "SH_ALARM_ - An internal error has occured"); 
      break;
    
    default:
      logger.logMsg(ERROR_FLAG, 0,
                    "SH_ALARM_ - Unrecognized event : %u , cause : %u",
                    mgmt->t.usta.alarm.event, mgmt->t.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmAlmHdlr::handleShAlarm");

  return (BP_AIN_SM_OK);
}

//MR Alarm 
int
INGwSmAlmHdlr::handleMrAlarm(MrMngmt *mgmt) {
  int integ; // integrated alarmid

  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleMrAlarm");

  integ = MMkIntegVal(ENTMR, mgmt->s.usta.alarm.event, mgmt->s.usta.alarm.cause);

  switch (integ) {

    case MMkIntegVal(ENTMR, LCM_EVENT_MI_INV_EVT, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ - invalid primitive on management interface-ignored");
      break;
    case MMkIntegVal(ENTMR, LCM_EVENT_PI_INV_EVT, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ - invalid primitive on peer interface-ignored");
      break;

    case MMkIntegVal(ENTMR, LCM_EVENT_INV_EVT, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ - invalid primitive from unknown source-ignored");
      break;

    case MMkIntegVal(ENTMR, LCM_EVENT_INV_STATE, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ - Primitive received in  invalid state-ignored");
      break;

    case MMkIntegVal(ENTMR, LMR_EVENT_INVALID_RSET, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,//print RsetAddr here
                    "MR_ALARM_ - request for invalid resource set - ignored");
      break;

    case MMkIntegVal(ENTMR, LMR_EVENT_INVALID_UPD_SEQUENCE, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ - invalid sequence number in peer update messasge - ignored");
      break;

    case MMkIntegVal(ENTMR, LMR_EVENT_INVALID_ACK_SEQUENCE, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ - invalid sequence number in peer update confirm - ignored");
      break;

    case MMkIntegVal(ENTMR, LMR_EVENT_INVALID_FLUSHCFM, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ - flush confirm from unexpected processor Id - ignored");
      break;

    case MMkIntegVal(ENTMR, LMR_EVENT_DUPLCT_FLUSHCFM, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ - Duplicate flush confirm - ignored");
      break;

    case MMkIntegVal(ENTMR, LMR_EVENT_INVALID_EVENT_MCASTRSP, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ - Invalid multicast response primitive - ignored");
      break;

    case MMkIntegVal(ENTMR, LMR_EVENT_INVALID_NMB_MCASTRSP, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ - Invalid number of multicast response primitive received-ignored");
      break;

    case MMkIntegVal(ENTMR,LMR_EVENT_SYNCACKTMR_EXPIRY , LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ - Multicast sync ACK timer expired");
      break;

    case MMkIntegVal(ENTMR, LMR_EVENT_FLUSHTMR_EXPIRY, LCM_CAUSE_UNKNOWN):
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ -Flush ACK timer expired");
      break;

    case MMkIntegVal(ENTMR, LMR_EVENT_MEMBUF_NOT_AVAIL, LCM_CAUSE_UNKNOWN)://RAISE ALM
      logger.logMsg(ERROR_FLAG, 0,
                    "MR_ALARM_ - Memory not available");
      break;

    default:
      logger.logMsg(ERROR_FLAG, 0,
                    "SH_ALARM_ - Unrecognized event : %u , cause : %u",
                    mgmt->s.usta.alarm.event, mgmt->s.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmAlmHdlr::handleMrAlarm");

  return (BP_AIN_SM_OK);
}

//SG Alarm handler
int
INGwSmAlmHdlr::handleSgAlarm(SgMngmt *mgmt) {
  int integ; // integrated alarmid

  char lpcTime[64];
  logger.logMsg (TRACE_FLAG, 0,
    "Entering INGwSmAlmHdlr::handleSgAlarm");
  char lBuf[50];
  int lBufLen = 0;
  if(LSG_HI_API == mgmt->apiType){
    integ = MMkIntegVal(ENTSG, mgmt->t.hi.usta.alarm.event,  mgmt->t.hi.usta.alarm.cause);
    lBufLen += sprintf(lBuf + lBufLen, "SG_ALARM_ Source- Hi Level Api-");
    switch(integ) {
    case MMkIntegVal(ENTSG, LSG_EVENT_OOS, LSG_CAUSE_OAM)://RAISE ALM
      logger.logMsg(ERROR_FLAG, 0,"%s entity has gone into out of service state due to an OAM command",
                                 lBuf);
      break;
    //lo hi
    case MMkIntegVal(ENTSG, LSG_EVENT_ACTIVE, LSG_CAUSE_OAM)://RAISE ALM
      logger.logMsg(ERROR_FLAG, 0,"%s entity has gone into Active state due to an OAM command",
                                 lBuf);
      break;

    //lo hi
    case MMkIntegVal(ENTSG, LSG_EVENT_STANDBY, LSG_CAUSE_OAM):
      logger.logMsg(ERROR_FLAG, 0,"%s entity has gone into standby state due to an OAM command",
                                 lBuf);
      break;
    //lo hi
    case MMkIntegVal(ENTSG, LSG_EVENT_MASTER, LSG_CAUSE_OAM):
      logger.logMsg(ERROR_FLAG, 0,"%s critical resource set of an entity is made master on a processor",
                                 lBuf);
      break;
    //hi lo
    case MMkIntegVal(ENTSG, LSG_EVENT_OAM_FAIL, LSG_CAUSE_NAK):
      logger.logMsg(ERROR_FLAG, 0,"%s OAM issued command failed as a component of the"
                                  "architecture responded with a NAK for a command issued by"
                                 " the system manager",lBuf);
      break;
    //hi lo
    case MMkIntegVal(ENTSG, LSG_EVENT_OAM_FAIL, LSG_CAUSE_TIMEOUT):
      logger.logMsg(ERROR_FLAG, 0,"%s OAM issued command failed as a component of the" 
                                 "architecture did not responded to a command issued by"
                                 " the system manager",lBuf);
      break;
    //hi lo
    case MMkIntegVal(ENTSG, LSG_EVENT_OAM_FAIL, LSG_CAUSE_FAILURE):
      logger.logMsg(ERROR_FLAG, 0,"%s OAM issued command failed as a system component(SH/MR)"
                                 " did not respond to the system manager", lBuf);
      break;
    //hi lo
    //INCTBD LSG_CAUSE_INV_STATE is not found in code
    //case MMkIntegVal(ENTSG, LSG_EVENT_FAILURE, LSG_CAUSE_INV_STATE):
    //  logger.logMsg(ERROR_FLAG, 0,"%s An error is encountered within standy copy of SG while"
    //                              " receiving update messages from the active copy of SG");
    //  break;

    case MMkIntegVal(ENTSG, LSG_EVENT_STANDBY_OOS, LSG_CAUSE_NO_HEART_BEAT):
      logger.logMsg(ERROR_FLAG, 0,
             "%s standby out of sequence, lost hb event", lBuf);

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] handleSgAlarm(): %s "
             "LSG_HI_API standby out of sequence, lost hb event\n",
             lpcTime); fflush(stdout);
      
      break;
    
    default:
      //if(LSG_LO_API ==  mgmt->apiType)
      logger.logMsg(ERROR_FLAG, 0,
                    "SG_ALARM_ - Source- Hi Level Api-Unrecognized event : %u , cause : %u",
                    mgmt->t.hi.usta.alarm.event, mgmt->t.hi.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);

    }

  }
  else if(LSG_LO_API == mgmt->apiType){
    integ = MMkIntegVal(ENTSG, mgmt->t.lo.usta.alarm.event,  mgmt->t.lo.usta.alarm.cause);
    lBufLen += sprintf(lBuf + lBufLen, "SG_ALARM_ Source- Low Level Api-");
    switch(integ) {
    case MMkIntegVal(ENTSG, LSG_EVENT_OOS, LSG_CAUSE_OAM):
      logger.logMsg(ERROR_FLAG, 0,"%s entity has gone into out of service state due to an OAM command",
                                 lBuf);
      break;
    //lo hi
    case MMkIntegVal(ENTSG, LSG_EVENT_ACTIVE, LSG_CAUSE_OAM):
      logger.logMsg(ERROR_FLAG, 0,"%s entity has gone into Active state due to an OAM command",
                                 lBuf);
      break;

    //lo hi
    case MMkIntegVal(ENTSG, LSG_EVENT_STANDBY, LSG_CAUSE_OAM):
      logger.logMsg(ERROR_FLAG, 0,"%s entity has gone into standby state due to an OAM command",
                                 lBuf);
      break;
    //lo hi
    case MMkIntegVal(ENTSG, LSG_EVENT_MASTER, LSG_CAUSE_OAM):
      logger.logMsg(ERROR_FLAG, 0,"%s critical master resource set is activated on a node due to an OAM"                                  " command", lBuf);
                                
      break;
    //hi lo
    case MMkIntegVal(ENTSG, LSG_EVENT_OAM_FAIL, LSG_CAUSE_NAK):
      logger.logMsg(ERROR_FLAG, 0,"%s OAM issued command failed as a component of the"
                                  "architecture responded with a NAK for a command issued by"
                                 " the system manager",lBuf);
      break;
    //hi lo
    case MMkIntegVal(ENTSG, LSG_EVENT_OAM_FAIL, LSG_CAUSE_TIMEOUT):
      logger.logMsg(ERROR_FLAG, 0,"%s OAM issued command failed as a component of the" 
                                 "architecture did not responded to a command issued by"
                                 " the system manager",lBuf);
      break;
    //hi lo
    case MMkIntegVal(ENTSG, LSG_EVENT_OAM_FAIL, LSG_CAUSE_FAILURE):
      logger.logMsg(ERROR_FLAG, 0,"%s OAM issued command failed as a system component(SH/MR)"
                                 " did not respond to the system manager",lBuf);
      break;
    //hi lo
    //case MMkIntegVal(ENTSG, LSG_EVENT_FAILURE, LSG_CAUSE_INV_STATE):
    //  logger.logMsg(ERROR_FLAG, 0, "%s An error is encountered within standy copy of SG while"
    //                              " receiving update messages from the active copy of SG",lBuf);
    //  break;

    // INCTBD
    // LSG_CAUSE_INV_STATE is not found in code, LCM_CAUSE_INV_STATE seems correct
    // assuming it to be the right one putting it here. 

      break;

    case MMkIntegVal(ENTSG, LSG_EVENT_STANDBY_OOS, LSG_CAUSE_NO_HEART_BEAT):
      logger.logMsg(ERROR_FLAG, 0,
             "%s standby out of sequence, lost hb event", lBuf);

      memset(lpcTime, 0, sizeof(lpcTime));
      lpcTime[0] = '1';
      g_getCurrentTime(lpcTime);
      printf("[+INC+] handleSgAlarm(): %s "
             "LSG_LO_API standby out of sequence, lost hb event\n",
             lpcTime); fflush(stdout);
      
      break;
    
    //lo
    case MMkIntegVal(ENTSG, LSG_EVENT_SHADOW, LSG_CAUSE_INIT):
      logger.logMsg(ERROR_FLAG, 0,
			"%s critical shadow resource set is created due to activation"
	    " of an active/s'by resource set of the protocol layer on the node"
                                  ,lBuf);
      break;
    
    //lo
    case MMkIntegVal(ENTSG, LSG_EVENT_STANDBY_OOS, LSG_CAUSE_SBY_BAD_STATE):
      logger.logMsg(ERROR_FLAG, 0,
			"%s S'by copy of SG can't process the message because of bad state",lBuf); 
      break;
    //lo

    case MMkIntegVal(ENTSG, LSG_EVENT_STANDBY_OOS, LSG_CAUSE_BAD_UPD_SEQ):
      logger.logMsg(ERROR_FLAG, 0,
			"%s S'by has gone out of sync with the active due to a missed"
                                  " update sequence number",lBuf); 
      break;

    //lo
    case MMkIntegVal(ENTSG, LSG_EVENT_STANDBY_OOS, LSG_CAUSE_BAD_UPDATE):
      logger.logMsg(ERROR_FLAG, 0,
			"S'by has gone out of sync with the active due to"
      " bad or corrupted sequence number from the active");
      break;

    default:
      //if(LSG_LO_API ==  mgmt->apiType)
      logger.logMsg(ERROR_FLAG, 0,
                    "SG_ALARM_ - Source- Low Level Api-Unrecognized event : %u , cause : %u",
                    mgmt->t.lo.usta.alarm.event, mgmt->t.lo.usta.alarm.cause);
      return (BP_AIN_SM_FAIL);

    }
  }
  else{
    logger.logMsg (ERROR_FLAG, 0, 
           "Invalid API Type. Leaving INGwSmAlmHdlr::handleSgAlarm");
    return BP_AIN_SM_FAIL;
  }

  logger.logMsg (TRACE_FLAG, 0, "Leaving INGwSmAlmHdlr::handleSgAlarm");

  return (BP_AIN_SM_OK);
}
