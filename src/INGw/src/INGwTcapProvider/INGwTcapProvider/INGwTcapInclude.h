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
//     File:     INGwTcapInclude.h
//
//     Desc:     <Description of file>
//
//     Author       Date        Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07     Initial Creation
//********************************************************************
#ifndef _INGW_TCAP_INCLUDE_H_
#define _INGW_TCAP_INCLUDE_H_

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwCommonTypes/INCCommons.h>

#define TCAP_MIN_APP_ID 0
#define TCAP_MAX_APP_ID 16
#define TCAP_MAX_NODE_ID 8

#define TCAP_APP_INFO_COUNT 	4

#define TCAP_OPC_INDEX 				  0
#define TCAP_SSN_INDEX 				  1
#define TCAP_APP_ID_INDEX 		  2
#define TCAP_PCOL_VAR_INDEX 	  3
#define TCAP_SCCP_PROTOCOL_VAR  3
//#define TCAP_NETWORK_APP      	5
//#define TCAP_APP_DLG_CNT        6
//#define TCAP_STACK_DLG_CNT      7
#define TCAP_REG_SLEEP 2

#define TCAP_REGISTER 		0
#define TCAP_RE_REGISTER 	1
#define TCAP_DE_REGISTER 	2

#define TCAP_PRIMARY     1
#define TCAP_SECONDARY   0

#define MAX_STACK_LOG_FILE_SIZE 255
#define TACP_DBG_DEFAULT_FILE "/LOGS/INGw/stackDbg.txt"

#define TCAP_SSN_IN_SERVICE 0
#define TCAP_SSN_OOS_SERVICE 1

#if 0
typedef enum {
	TCAP_UNKNOWN =0,
	TCAP_NOT_REGISTERED,
	TCAP_REGSITER_INPGRS,
	TCAP_REGISTERED,
	TCAP_DEREGISTERED
} g_tcapSsnState;

typedef struct t_tcapRegInfo
{
	S16 m_suId;
	int	m_regType;
	int	m_index;
}TcapRegInfo;

typedef struct t_ssnInfo 
{
	U8 				  		ssn;
	short 				  suId;
	short 				  spId;
	g_tcapSsnState	regState;

	bool            pcStateValid;
	PcSsnStatusData pcState;

	bool            ssnStateValid;
	PcSsnStatusData ssnState;

	t_ssnInfo()
	{
		ssn  = 0;
		suId = -1;
		spId = -1;
		regState = TCAP_UNKNOWN;
		ssnStateValid = pcStateValid = false;
		memset(&ssnState, 0, sizeof(PcSsnStatusData));
		memset(&pcState, 0, sizeof(PcSsnStatusData));
	}
}SsnInfo;

#endif

// checkers
#define tcapCheckAppId(i, ret) { \
	ret = G_SUCCESS;	\
	if (i < TCAP_MIN_APP_ID || i > TCAP_MAX_APP_ID) { \
		ret = G_FAILURE; \
	} \
}

#define tcapCheckSsn(i, ret) { \
	ret = G_SUCCESS; \
	if (i < G_SSN_MIN_LIMIT || i > G_SSN_MAX_LIMIT) { \
		ret = G_FAILURE; \
	} \
}

typedef enum {
	TCAP_PC  = 1,
	TCAP_SSN = 2
} g_PcOrSsn;

extern const char* g_tcapPrintMsgType(int p_msgType);
extern unsigned char g_tcapGetProtocolVar(string &p_pcolVar);
extern unsigned char g_sccpGetProtocolVar(string &p_pcolVar);
//extern string g_tcapDebugUsrInfo(M7S_UsrCfg &p_usr);
extern char* g_tcapPrintError(int &id);

#ifdef STUB
typedef enum {
	STUB_UNKNOWN =0,
	STUB_SCCP_MGMT_IN_SRV,
	STUB_SCCP_MGMT_OUT_SRV,
	STUB_OUTBOUND_RSLT,
	STUB_INBOUND,
} g_stubMsgType;

typedef t_stubInfo
{
	g_stubMsgType type;
	int					  dlgId;
}stubInfo;


#endif
#endif

