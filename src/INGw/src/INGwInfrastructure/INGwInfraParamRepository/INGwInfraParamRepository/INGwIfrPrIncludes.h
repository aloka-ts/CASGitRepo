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
//     File:     INGwIfrMgrIncludes.h
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#ifndef _INGW_IFR_MGR_INCLUDES_H_
#define _INGW_IFR_MGR_INCLUDES_H_

#include <Util/imOid.h>
#include <Util/imErrorCodes.h>
#include <Util/Logger.h>
#include <INGwInfraUtil/INGwIfrUtlReject.h>

#define _THROW_REJECT(X)   throw INGwIfrUtlReject(X);

// The OID sipPROVIDER_BILLING_STATUS is moved to CCM as ccmBILLING_STATUS
// and the actual OID number needs to be updated accordingly
#define ccmGC_ALL_CALLS                          "8.1.101"

#define ccmDROPPED_EXTRANEOUS_CALL_COUNT      "DROPPED_EXTRANEOUS_CALLS"
#define ccmNUM_DEAD_CONNECTION_COUNT          "NUM_DEAD_CONNECTIONS"
#define ccmNUM_NO_ANSWER_TIMEOUT_COUNT        "NUM_NO_ANSWER_TIMEOUTS"
#define ccmNUM_INITIAL_RESPONSE_TIMEOUT_COUNT "NUM_INITIAL_RESPONSE_TIMEOUTS"
#define ccmSIP_NUM_MSG_SENT_COUNT             "SIP_NUM_MSG_SENT"
#define ccmSIP_NUM_MSG_RECD_COUNT             "SIP_NUM_MSG_RECD"
#define ccmSIP_NUM_MSG_RETX_SENT_COUNT        "SIP_NUM_MSG_RETX_SENT"
#define ccmSIP_NUM_MSG_RETX_RECD_COUNT        "SIP_NUM_MSG_RETX_RECD"
#define ccmSIP_CALL_LEG_MAP_SIZE              "SIP_CALL_LEG_MAP_SIZE"
#define ccmSIP_NUM_STACK_TIMEOUT_COUNT        "SIP_NUM_STACK_TIMEOUTS"

// The following are the codes used to indicate the operation
// status in the ACKs sent by CCM for the CALL_PROC messages
#define BP_CCM_IN_PROGRESS       2
#define BP_CCM_ERROR            -1


// The following are the codes used to indicate the operation
// type for MSG_FILTER_EVENT and MSG_BLK_FOR_NEW_CALLS resp
#define BP_CCM_EVT_REG           0
#define BP_CCM_EVT_DEREG         1
#define BP_CCM_UNBLOCK           0
#define BP_CCM_BLOCK             1

//-----------------------------------------------------------------
//-----------------------------------------------------------------

#define BP_NEW_CALL              0
#define BP_REASSIGNED_CALL       1
#define BP_RECOVERED_CALL        2

#define ngwCLI_PROBE_BASE        "98"
#define ingwDUMP                  "98.99.99"
#define ingwDumpParam             "100.100.100"
#define ccmPERF_PARAM_RESET      "98.99.98"
#define ingwCLEAN                 "98.99.97"

#define ccmPROBE_POINT_INFO      "98.99.96"
#define ccmPROBE_POINT_LOC       "98.99.95"
#define ccmPROBE_TIMEOUT         "98.99.94"
#define ccmPROBE_EXIT_STATUS     "98.99.93"

#define ccmCLI_DUMMY_PROV_BASE   "99"
#define ccmSEND_EVENT            "99.99.99"
#define ccmADD_NEW_CALL          "99.99.98"
#define ccmRUN_LOAD              "99.99.96"



//
// Key messages for tracing the calls in *.rexec_out
//
#define DMSG_CALL_ID                 "CALL_Id : "
#define DMSG_SLEE_ID                 "SLEE_ID : "
#define DMSG_MSG_RECD                " + "
#define DMSG_MSG_SENT                " - "

#ifndef TESTING
extern int mgMaxRegularBufferSize;
#define BP_NEW_CHAR(SIZE)    new char[SIZE]; if(SIZE > mgMaxRegularBufferSize) { logger.logMsg(ERROR_FLAG, imERR_NONE, "Allocating buffer of size: %d", SIZE); }
#else
#define BP_NEW_CHAR(SIZE)    new char[SIZE];
#endif

#define DEBUG_MSG_RECD(MSG)                  cout << DMSG_MSG_RECD << MSG << endl;
#define DEBUG_MSG_SENT(MSG)                  cout << DMSG_MSG_SENT << MSG << endl;
#define DEBUG_CALL_ASSIGN(CALLID, SLEEID)    cout << DMSG_CALL_ID  << CALLID << " assigned to " << DMSG_SLEE_ID << SLEEID << endl;
#define DEBUG_CALL_REASSIGN(CALLID, SLEEID)  cout << DMSG_CALL_ID  << CALLID << " re-assigned to " << DMSG_SLEE_ID << SLEEID << endl;

 

#define SIM_CALL_COUNT           "CALL_COUNT"
#define SIM_START_CALL_ID        "START_CALL_ID"
#define SIM_START_CGPN           "START_CGPN"
#define SIM_START_CDPN           "START_CDPN"
#define SIM_INC_ADDRESS          "INC_ADDRESS"

//
// These types are used if USE_DLOPEN is FALSE
//
#define DUMMY_PROVIDER_TYPE          0
#define NEW_LUCENT_PROVIDER_TYPE     1
#define DY_SIP_PROVIDER_TYPE         2
#define BP_AIN_PROVIDER_TYPE         3
#define MGCP_PROVIDER_TYPE           100
#define INGW_NW_REF_IP_ADDRESS       "INGW_NW_REF_IP_ADDRESS"

//
//
//
#ifndef INVALID_CONN_ID
#define INVALID_CONN_ID -1
#endif

enum CCMOperationMode
{
   UnknownMode,
   OnePlusZero,
   OnePlusOne,
   NPlusZero,
   NPlusOne
};

#endif 
