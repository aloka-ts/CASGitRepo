/************************************************************************
     Name:     INAP Stack Manager Queue Messages - defines
 
     Type:     C include file
 
     Desc:     Defines required for creating the Queue Message to
               post to SM Queue

     File:     INGwSmQueueMsg.h

     Sid:      INGwSmQueueMsg.h 0  -  03/27/03 

     Prg:      gs,bd

************************************************************************/

#ifndef __BP_AINSMQUEUEMSG_H__
#define __BP_AINSMQUEUEMSG_H__

//include the various header files
#include <INGwStackManager/INGwSmIncludes.h>
#include <INGwStackManager/INGwSmStkReqRespDat.h>
#include <INGwFtPacket/INGwFtPktMsg.h>

#define BP_AIN_SM_M3UA_MAX_PS 60 // same as LIT_MAX_PSID in lit.h


typedef enum {
  BP_AIN_SM_SRC_CCM           = 0,   // CCM initiated operation
  BP_AIN_SM_SRC_STACK         = 1,   // response from Stack for some oper
  BP_AIN_SM_SRC_STACK_ALM     = 2,   // alarm indication from Stack
  BP_AIN_SM_SRC_STACK_ALMHDLR = 3,   // handle alarm condition request
  BP_AIN_SM_SRC_EMS           = 4,   // EMS initiated operation
	BP_AIN_SM_STACK_CONFIG_START= 5,
	BP_AIN_SM_STACK_CONFIG_END  = 6,
	BP_AIN_SM_PEER_UP					  = 7,
	BP_AIN_SM_PEER_DOWN		  	  = 8,
	BP_AIN_SM_BIND_LAYER    	  = 9,
  BP_AIN_SM_SRC_PEER_INC      = 10    // Messages received from Peer
} INGwSmMsgSrc;

#if 0
typedef enum {
  BP_AIN_SM_CCMOP_OIDCHANGED   = 0,
  BP_AIN_SM_CCMOP_CHANGESTATE  = 1,
  BP_AIN_SM_CCMOP_CONFIGURE    = 2,
  BP_AIN_SM_CCMOP_PEERFAILED   = 3,
  BP_AIN_SM_CCMOP_ENABLE_NODE  = 4
} INGwSmCcmOpType;
#endif

typedef enum {
  BP_AIN_SM_STATE_LOADED   = 0,
  BP_AIN_SM_STATE_RUNNING  = 1,
  BP_AIN_SM_STATE_STOPPED  = 2
} INGwSmState;



typedef enum {
  BP_AIN_SM_STKOP_CTLCFM  = 0,  // control confirm
  BP_AIN_SM_STKOP_CFGCFM  = 1,  // config confirm
  BP_AIN_SM_STKOP_STACFM  = 2,  // status confirm
  BP_AIN_SM_STKOP_STSCFM  = 3,  // stats confirm
  BP_AIN_SM_STKOP_ALARM   = 4,  // alarm indication
  BP_AIN_SM_STKOP_ALMACTN = 5,  // alarm handling ctrl action
  BP_AIN_SM_STKOP_TIMEOUT = 6   // Stack Timeout
} INGwSmStackMsgType;


/*
 * contants for AlarmHandlingOperation for particular alarms, for
 * which SM needs to invoke one or more control operations.
 */
typedef enum {

  BP_AIN_SM_ALMOP_ASPAC_ACK          = 0,
  BP_AIN_SM_ALMOP_ASPIA_ACK          = 1,
  BP_AIN_SM_ALMOP_ASPUP_ACK          = 2,
  BP_AIN_SM_ALMOP_ASPDN_ACK          = 3,
  BP_AIN_SM_ALMOP_ASPAC_FAIL         = 4,
  BP_AIN_SM_ALMOP_ASPIA_FAIL         = 5,
  BP_AIN_SM_ALMOP_ASPUP_FAIL         = 6,
  BP_AIN_SM_ALMOP_ASSOC_ESTAB_FAIL   = 7,
  BP_AIN_SM_ALMOP_ASSOC_ESTAB_OK     = 8,
  BP_AIN_SM_ALMOP_COMM_DOWN          = 9,
  BP_AIN_SM_ALMOP_ASP_ST_UPD_DOWN    = 10,
  BP_AIN_SM_ALMOP_ASP_ST_UPD_INACT   = 11,
  BP_AIN_SM_ALMOP_ASP_ST_UPD_ACTV    = 12,
  BP_AIN_SM_ALMOP_SCP_LINK_UP        = 13,
  BP_AIN_SM_ALMOP_SCP_LINK_DOWN      = 14,
  BP_AIN_SM_ALMOP_SCP_DPC_PAUSE      = 15,
  BP_AIN_SM_ALMOP_SCP_DPC_RESUME     = 16

} INGwSmAlmOpType;

/*
 * This is the message which will be received in the INGwSmDistributor
 * Queue, from either CCM or Stack.
 */


typedef struct _iNGwSmOidInfo
{
  char  *mpcOid;
  char  *mpcValue;
  ConfigOpType mConfigOpTyp;
  long   mlSubsystemId;
} INGwSmOidInfo;


typedef struct _iNGwSmChngStInfo
{
  INGwSmState mState;  // change to this state

  int  miSapId;         // etc...

} INGwSmChngStInfo;


typedef struct _iNGwSmCcmOperMsg
{
  INGwSmCcmOpType mOpType;    // ccm operation type

  int  miRequestId;            // correlates the synch-record used by Wrapper
   
  TxnType              txnType;

  union {

    INGwSmOidInfo oidInfo;

    INGwSmChngStInfo chngStInfo;

  } ccmOp;

} INGwSmCcmOperMsg;


typedef struct _iNGwSmStackMgmtInfo
{
  //layer for which the operation is invoked
  int miLayerId;

  //transaction Id for the Confirm
  int miTransId;

  union {

    /*IeMngmt  ie; */
    StMngmt  st;
    SpMngmt  sp;
    ItMgmt   it;
    SbMgmt   sb;
    HiMngmt  hi;
    SnMngmt  sn;
    SdMngmt  sd;
    ShMngmt  sh;
    SgMngmt  sg;
    MrMngmt  mr;
    RyMngmt  ry;

    ZnMngmt  zn;
    LdnMngmt dn;
    ZpMngmt  zp;
    ZtMngmt  zt;
    ZvMngmt  zv;
    LdvMngmt dv;
    
  } lyr;
} INGwSmStackMgmtInfo;



typedef struct _iNGwSmStackMsg
{
  INGwSmStackMsgType   stkMsgTyp;

  INGwSmStackMgmtInfo  stkMsg;   // operation confirmation info

} INGwSmStackMsg;


/*
 * ASPDN / ASPDN Ack reason
 */
typedef enum {

  BP_AIN_SM_ASPDN_REASON_INVALID      = 0,
  BP_AIN_SM_ASPDN_REASON_UNSPECIFIED  = 1,
  BP_AIN_SM_ASPDN_REASON_USR_UNAVL    = 2,
  BP_AIN_SM_ASPDN_REASON_MGMT_INHIBIT = 3

} INGwSmAspDownReason;


/*
 * ASP traffic modes.
 */
typedef enum {

  BP_AIN_SM_TRFMODE_INVALID   = 0,
  BP_AIN_SM_TRFMODE_OVERRIDE  = 1,
  BP_AIN_SM_TRFMODE_LOADSHARE = 2

} INGwSmTrafModeType;

/*
 * information structure for handling the ASPM indication.
 */
typedef struct _iNGwSmHdlAspmInfo
{
  ItPspId  pspId;      // pspId info

  INGwSmAspDownReason aspDnReason; // reason for ASPDN

  INGwSmTrafModeType  trfModeType; // traf mode for ASPIA/ASPAC

  int     nmbPs;       // number of psId's in psIdArr

  ItPsId  psIdArr[BP_AIN_SM_M3UA_MAX_PS];     // relevant psId's

} INGwSmHdlAspmInfo;


/*
 * common info structure for Alm Handling Operation that need pspId
 */
typedef struct _iNGwSmAlmOpPspInfo
{
  ItPspId  pspId;      // pspId info
  ItPsId  psId;      // pspId info
  SuId     sctSuId;

} INGwSmAlmOpPspInfo;


/*
 * possible causes of SCTP Communication Down
 */
typedef enum {

  BP_AIN_SM_COMMDN_CAUSE_UNKNOWN       = 0,
  BP_AIN_SM_COMMDN_CAUSE_REMOTE_SHUTDN = 1,
  BP_AIN_SM_COMMDN_CAUSE_REMOTE_ABORT  = 2,
  BP_AIN_SM_COMMDN_CAUSE_COMM_LOST     = 3,
  BP_AIN_SM_COMMDN_CAUSE_SCTP_RESTART  = 4,
  BP_AIN_SM_COMMDN_CAUSE_SCTP_PROCERR  = 5,
  BP_AIN_SM_COMMDN_CAUSE_ASSOC_INHIBIT = 6,
  BP_AIN_SM_COMMDN_CAUSE_LOCAL_SHUTDN  = 7,
  BP_AIN_SM_COMMDN_CAUSE_HBEAT_LOST    = 8

} INGwSmCommDownCause;


/*
 * common info structure for Alm Handling Operation for SCTP Comm Down case
 */
typedef struct _iNGwSmCommDownInfo
{
    INGwSmCommDownCause cause;

    ItPspId  pspId;      // pspId info
    SuId     sctSuId;      // sctSuId info

} INGwSmCommDownInfo;

     
/*
 * the message structure for AlarmHandlingOperation for particular
 * alarms, for which SM invokes one or more control operations.
 */
typedef struct _iNGwSmAlmOperMsg
{
  INGwSmAlmOpType mOpType;    // alarm handling control operation type

  union {

    INGwSmHdlAspmInfo  aspmInfo;   // situation specific info structure

    INGwSmAlmOpPspInfo pspInfo;

    INGwSmCommDownInfo commDn;     // comm down alarm handling info

  } almOp;

} INGwSmAlmOperMsg;



/*
 * alarm msg info if msg from Stack
 */
typedef struct _iNGwSmAlarmMsg
{
  INGwSmStackMgmtInfo  almInfo;   // alarm info
} INGwSmAlarmMsg;


typedef struct _iNGwSmQueueMsg
{
  unsigned int id;               //identifier for this queue msg
  
  INGwSmMsgSrc mSrc;            // message is from CCM or from Stack

  union {

    INGwSmCcmOperMsg ccmOper;   // msg info if msg from CCM

    INGwSmStackMsg   stackMsg;  // msg info if msg from Stack

    INGwSmAlarmMsg   alarmMsg;  // alarm msg info if msg from Stack

    INGwSmAlmOperMsg almOper;   // control message for alarm handling

    StackReqResp stackData; // SS7 or SIGTRAN config data received from EMS
                                    // In case of bulk config data or in case of 
                                    // individual config data

  } t;

} INGwSmQueueMsg;


typedef struct _INGwSmConfigQMsg
{
	INGwSmMsgSrc src;	
	Ss7SigtranSubsReq *req;
	vector<int> procIdList;
	int         from; // 0 = ems, 1=fetchSS7Info, 2=for peer	
	INGwFtPktMsg *msg;
} INGwSmConfigQMsg;



#endif /* __BP_AINSMQUEUEMSG_H__ */

