//////////////////////////////////////////////////////////////////////////
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
//     File:     INGwFtPktMsgDefine.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   23/11/07     Initial Creation
//********************************************************************

#ifndef INGW_FT_PKT_MSG_DEFINE_H_
#define INGW_FT_PKT_MSG_DEFINE_H_

#include <list>
#include <string>
#include <set>
#include <map>
const unsigned char MSG_TCAP_INBOUND  = 1;
const unsigned char MSG_TCAP_OUTBOUND = 2;
const unsigned char INGW_CLEAN_INBOUND_MESSAGE  = 1;
const unsigned char INGW_CLEAN_OUTBOUND_MESSAGE = 2;
//Message Type
#define MSG_EVENT                1
#define MSG_CCMP_ACK             3
#define MSG_COMPOSITE            6

#define MSG_CALL_PROC            4
#define MSG_CCM_DYN_FT           9

#define MSG_CCMP_FT_ACK        101
#define MSG_DELETE_HISTORY     102
#define MSG_CHANGE_CCM         103
#define MSG_REPLAY_CALL        104
#define MSG_FT_CCMP_ACK        105
#define MSG_DROP_CALL          106
#define MSG_CHANGE_SLEE        107
#define MSG_CCM_STATUS         108

#define MSG_FT_DELETE_CALL        121
#define MSG_PEER_FT_ACK           122
#define MSG_CALL_BACKUP           123
#define MSG_FT_ROLE_NEGOTIATION   124

#define MSG_LOAD_DIST_MSG       125
#define MSG_TCAP_DLG_INFO       126

#define MSG_STACK_CONFIG        128

// Message Corresponding to Stack Config
#define MSG_ADD_LINK    		129
#define MSG_DEL_LINK   		 	130
#define MSG_ADD_LINKSET 		131
#define MSG_DEL_LINKSET 		132
#define MSG_ADD_NW		  		133
#define MSG_DEL_NW					134
#define MSG_ADD_ROUTE				135
#define MSG_DEL_ROUTE				136
#define MSG_ADD_LOCAL_SSN		137
#define MSG_DEL_LOCAL_SSN   138
#define MSG_ADD_USER_PART   139
#define MSG_DEL_USER_PART   140
#define MSG_M3UA_ASSOC_DOWN 141
#define MSG_M3UA_ASSOC_UP   142
#define MSG_ADD_PS          143
#define MSG_DEL_PS					144
#define MSG_ADD_ENDPOINT		145
#define MSG_DEL_ENDPOINT		146
#define MSG_ADD_PSP         147
#define MSG_DEL_PSP					148
#define MSG_ADD_ADDRMAP     149
#define MSG_ADD_RULE        150
#define MSG_DEL_ADDRMAP     151
#define MSG_DEL_RULE        152
#define MSG_MOD_LINK        153
#define MSG_MOD_LINKSET     154
#define MSG_MOD_PS          155
#define MSG_CONFIG_STATUS   156
#define MSG_ASP_ACTV        157
#define MSG_ASP_INACTV      158

//Tcap Session Replication
#define MSG_TCAP_CALL_SEQ_ACK     200
#define MSG_TCAP_CALL_DATA        201 
#define MSG_CREATE_TCAP_SESSION   202
#define MSG_UPDATE_TCAP_SESSION   203
#define MSG_CLOSE_TCAP_SESSION    204
#define MSG_HANDLE_SAS_HB_FAILURE 205
#define MSG_HANDLE_SYNCH_UP       206
//Admin message.
#define MSG_FILTER_EVENT        51
#define MSG_LOAD_FACTOR         52
#define MSG_BLK_FOR_NEW_CALLS   53

//Field Size

#define SIZE_OF_ADDR           128
#define SIZE_OF_IPADDR          15
#define SIZE_OF_SHORT            2
#define SIZE_OF_INT              4
#define SIZE_OF_LONG             4
#define SIZE_OF_ANN_SET        128
#define SIZE_OF_CHARGE_NUMBER   20
#define SIZE_OF_LRN             20
#define SIZE_OF_CIC             10
#define SIZE_OF_BG_ID           10
#define SIZE_OF_TRUNK_ID        40
#define SIZE_OF_TRUNK_GRP       40
#define SIZE_OF_DISPLAY_NAME    40
#define SIZE_OF_CHAR						1

// --EVERCOMM CHAGES STARTS--
#define SIZE_OF_MEDIA_SERVER_ENDPOINT  128
// --EVERCOMM CHAGES ENDS--

// BPInd09642
#define SIZE_OF_AMA_SLP_ID      10
#define SIZE_OF_OVERFLOW_BILLING_IND 10

#define SIZE_OF_BPADDRESS       ( SIZE_OF_ADDR + (2 * SIZE_OF_INT) + \
                                  SIZE_OF_BG_ID + SIZE_OF_INT + \
                                  SIZE_OF_DISPLAY_NAME )
//40 + (2 * 4) + 10 + 4 = 62

#define SIZE_OF_BPIVRADDRESS    ( SIZE_OF_ANN_SET )
//20

#define SIZE_OF_BPROUTINGINFO   ( SIZE_OF_INT + SIZE_OF_BPADDRESS +  \
                                  (3 * SIZE_OF_TRUNK_GRP) + (3 * SIZE_OF_CIC) )
//4 + 62 + (3 * 40) + (3 * 10) = 216

#define SIZE_OF_BPMSG           ( (5 * SIZE_OF_SHORT) + SIZE_OF_LONG + \
                                  SIZE_OF_INT)
// (5 * 2) + 4 + 4 = 18

#define SIZE_OF_BPEVENTMSG_FP   ( SIZE_OF_SHORT + SIZE_OF_SHORT + \
                                  SIZE_OF_INT + \
                                  SIZE_OF_SHORT + (5 * SIZE_OF_INT) + \
                                  SIZE_OF_SHORT + (2 * SIZE_OF_INT) + \
                                  (4 * SIZE_OF_BPADDRESS) + \
                                  SIZE_OF_INT + SIZE_OF_TRUNK_ID + \
                                  SIZE_OF_CIC + SIZE_OF_INT + SIZE_OF_LRN + \
                                  SIZE_OF_CHARGE_NUMBER + (2 * SIZE_OF_INT) + \
                                  SIZE_OF_IPADDR + (2 * SIZE_OF_INT) )
// 2 + 2 + 4 + 2 + (5 * 4) + 2 + (2 * 4) + (4 * 62) + 40 + 10 + 4 + 
// 20 + 20 + (2 * 4) + 16 + (2 * 4) = 414

#define SIZE_OF_BPCALLPROCMSG_FP ( (4 * SIZE_OF_SHORT) + (4 * SIZE_OF_INT) )
// (4 * 2) + (4 * 4) = 24

//BpMsg Size
#define PKT_HDR_SIZE                   22

//MessageSize

#define BASE_CCMP_ACK_MSG_SIZE         10
#define BASE_CALL_PROC_MSG_SIZE        14

#define BASE_CCMP_FT_ACK_MSG_SIZE      12
#define BASE_DELETE_EVENT_MSG_SIZE      8
#define BASE_CHANGECCM_MSG_SIZE         4
#define BASE_REPLAY_MSG_SIZE            2
#define BASE_FT_CCMP_ACK_MSG_SIZE       8
#define BASE_DROP_CALL_MSG_SIZE         2
#define BASE_CHANGESLEE_MSG_SIZE        6
#define BASE_CCMSTATUS_MSG_SIZE         4

#define BASE_DELETE_CALL_MSG_SIZE       4
#define BASE_DYN_FT_MSG_SIZE            4
#define BASE_CCM_CALLDATA_MSG_SIZE      4

#define ADMIN_MSG_SIZE  10

//Field Identifier.

#define MSG_FIELD_ANNOUNCEMENT_SET           1
#define MSG_FIELD_BILLING_DATA               2
#define MSG_FIELD_CALL_ID                    3
#define MSG_FIELD_CALL_NATURE                4
#define MSG_FIELD_CAUSE                      5
#define MSG_FIELD_CHARGE_NUMBER              6
#define MSG_FIELD_CIC                        7
#define MSG_FIELD_CONNECTION_ID              8
#define MSG_FIELD_CR_SPEC                    9
#define MSG_FIELD_DIALED_ADDR               10
#define MSG_FIELD_EVENT_BLK_INFO            11
#define MSG_FIELD_EVENT_ID                  12
#define MSG_FIELD_EVENT_TYPE                13
#define MSG_FIELD_EVENT_TYPE_INFO           14
#define MSG_FIELD_FCI                       15
#define MSG_FIELD_INITIAL_EVENT_MASK        16
#define MSG_FIELD_LANG_CODE                 17
#define MSG_FIELD_LRN                       18
#define MSG_FIELD_MAJOR_SEQ_NUM             19
#define MSG_FIELD_MA_PARAMETER              20
#define MSG_FIELD_MINOR_SEQ_NUM             21
#define MSG_FIELD_MSG_TYPE                  22
#define MSG_FIELD_ORIGINATING_ADDR          23
#define MSG_FIELD_PLAY_SPEC                 24
#define MSG_FIELD_RECEIVER                  25
#define MSG_FIELD_REDIRECTING_ADDR          26
#define MSG_FIELD_RELEASE_CAUSE             27
#define MSG_FIELD_RETURN_PARAM              28
#define MSG_FIELD_SENDER                    29
#define MSG_FIELD_STATE                     30
#define MSG_FIELD_STATUS                    31
#define MSG_FIELD_SUB_MSG_TYPE              32
#define MSG_FIELD_TARGET_ADDR               33
#define MSG_FIELD_TRUNK_ID                  34
#define MSG_FIELD_VALUE                     35
#define MSG_FIELD_CHARGE_STATUS             36
#define MSG_FIELD_NO_ANSWER_TIMER           37
#define MSG_FIELD_CAUSE_OF_REDIRECTION      38
#define MSG_FIELD_PIGGY_BACK_DATA           39
#define MSG_FIELD_ORG_DIRECTION             40
#define MSG_FIELD_OLI                       41
#define MSG_FIELD_PROVIDER_TYPE             42
#define MSG_FIELD_OPTIONAL_ATTR_MASK        43
#define MSG_FIELD_RETX_COUNT                44
#define MSG_FIELD_OP_ORG                    45
#define MSG_FIELD_LAST_OP_INDICATOR         46
#define MSG_FIELD_OP_SPECFIC_ATTR_MASK      47
#define MSG_FIELD_DNIS                      48
#define MSG_FIELD_CALL_TRANSFER_MODE        49
#define MSG_FIELD_TIMER_DURATION            50
#define MSG_FIELD_SELECTIVE_LOGGING_IND     51
#define MSG_FIELD_EVENT_SPECIFIC_ATTR_MASK  52
#define MSG_FIELD_RTP_TUNNELING_INDICATOR   53
#define MSG_FIELD_CALL_TRACE_CRITERIA       54
#define MSG_FIELD_TEST_CALL_FLAG            55
#define MSG_FIELD_IVRCONN_IND               56
#define MSG_FIELD_SRCADDR_TYPE              57
#define MSG_FIELD_SRCADDR_PORT              58
#define MSG_FIELD_SRCADDR_ADDR              59
#define MSG_FIELD_PRIVACY                   60
#define MSG_FIELD_REASON_CODE               61

// --EVERCOMM CHANGES STARTS--
#define MSG_FIELD_MEDIA_SERVER_ENDPOINT    101
// --EVERCOMM CHANGES ENDS--

//Message Originator.

#define OP_ORG_SLEE      0
#define OP_ORG_SERVICE   1
#define OP_ORG_CCM       2

//Operations.

//Call proc SubMsg.
#define SUBMSG_ROUTE_CALL                1
#define SUBMSG_CONTINUE_CALL             2
#define SUBMSG_DROP_CONNECTION           3
#define SUBMSG_RELEASE_CALL              4
#define SUBMSG_SERVICE_COMPLETE          5
#define SUBMSG_CREATE_CONNECTION         6
#define SUBMSG_SELECT_ROUTE              7
#define SUBMSG_ROUTE_CALL_FORK           8
#define SUBMSG_CONNECT_IVR              10
#define SUBMSG_PLAY_MSG                 11
#define SUBMSG_PLAY_AND_COLLECT         12
#define SUBMSG_CANCEL_IVR_OP            13
#define SUBMSG_PLAY_AND_RECORD          14
#define SUBMSG_MANAGE_AUDIO             15
#define SUBMSG_BILLING_OP               16
#define SUBMSG_START_TIMER_OP           17
#define SUBMSG_HOLD_CALL                18
#define SUBMSG_RESYNCH_CALL             19
#define SUBMSG_DROP_CALL                20
#define SUBMSG_AUDIT_CALL               21
#define SUBMSG_DIAL_OUT                 22
#define SUBMSG_CANCEL_ROUTE             23

#define SUBMSG_SLEE_INT_ERROR           99
#define SUBMSG_CCM_INT_ERROR            98

//CCMPFT subMsg.
#define SUBMSG_CCM_CHANGE      0
#define SUBMSG_REPLAY_CALL     1

//Message status mask.

#define BPMSG_MSK_LOGGING_STATUS   0x01
#define BPMSG_MSK_REPLAY_STATUS    0x02
#define BPMSG_MSK_ABORT_STATUS     0x04

//Some miscellaneous

#define BP_FT_DO_NOT_DELETE_HISTORY     1
#define BP_FT_DELETE_HISTORY            0

#define DEBUG_LOAD_MSG               0x01
#define DEBUG_ADMIN_MSG              0x02
#define DEBUG_CALLASSIGN_MSG         0x04
#define DEBUG_CALLPROC_MSG           0x08
#define DEBUG_GEN_PERF_DAT           0x10
#define DEBUG_FT_MSG                 0x20

#define MAX_EXTRA_ROUTES 10

//Types used.

typedef std::list<std::string> callIdList;
typedef std::list<std::string> CallIdList;
typedef callIdList::iterator CallIdListIt;
typedef callIdList::const_iterator CallIdListCIt;

typedef std::set<std::string> CallIdSet;
typedef CallIdSet::iterator CallIdSetIt;
typedef CallIdSet::const_iterator CallIdSetCIt;

typedef std::map<int, CallIdSet> SleeCallSet;
typedef SleeCallSet::iterator SleeCallSetIt;
typedef SleeCallSet::const_iterator SleeCallSetCIt;


//some functional defines.

#define NEW_CHAR_ARY(SIZE)  new char[SIZE];

#endif //INGW_FT_PKT_MSG_DEFINE_H_
