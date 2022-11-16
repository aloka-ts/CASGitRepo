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
//     File:     INGwIfrUtlMacro.h
//
//     Desc:     Defines Macros
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_IFR_UTL_GLB_INCLUDE_H_
#define _INGW_IFR_UTL_GLB_INCLUDE_H_

#include <vector>
#include <string>
using namespace std;

typedef unsigned char  U8;
typedef unsigned short U16;
typedef unsigned long U32;

#define G_SUCCESS 1
#define G_FAILURE 0

#define G_COMMA_DELIM  ","
#define G_PIPE_DELIM   "|"

#define G_SSN_MIN_LIMIT 0
#define G_SSN_MAX_LIMIT 255

#define G_ITU_ZONE_MIN_LIMIT  0
#define G_ITU_ZONE_MAX_LIMIT  7

#define G_ITU_NETWORK_MIN_LIMIT 1 
#define G_ITU_NETWORK_MAX_LIMIT 255

#define G_ITU_SP_MIN_LIMIT  0
#define G_ITU_SP_MAX_LIMIT  7

#define G_JAPAN_ZONE_MIN_LIMIT  0
#define G_JAPAN_ZONE_MAX_LIMIT  127

#define G_JAPAN_NETWORK_MIN_LIMIT 1 
#define G_JAPAN_NETWORK_MAX_LIMIT 15

#define G_JAPAN_SP_MIN_LIMIT  0
#define G_JAPAN_SP_MAX_LIMIT  31

#define G_ANSI_ZONE_MIN_LIMIT  0
#define G_ANSI_ZONE_MAX_LIMIT  255

#define G_ANSI_NETWORK_MIN_LIMIT 1 
#define G_ANSI_NETWORK_MAX_LIMIT 255

#define G_ANSI_SP_MIN_LIMIT  0
#define G_ANSI_SP_MAX_LIMIT  255

#define G_MAX_SSN_SIZE 256

const int g_opcIndex = 0;
const int g_ssnIndex = 1;

typedef struct t_INGwLdPcSsn {

    U32 m_pc;
    U8  m_ssn;
    U8  m_pcDetail[3];
    U32 m_protoType;
}INGwLdPcSsn;

typedef vector<INGwLdPcSsn> INGwLdPcSsnList;

#define G_INVALID_OPC_SSN		99
#define G_REG_INPROGRESS  	100
#define G_DEREG_INPROGRESS  101
#define G_ALREADY_REG				102
#define G_ALREADY_DEREG			103
#define G_BAD_REQUEST				400


typedef struct t_transitObject
{
	int						m_msgType;
	U32 					m_pc;
	U8						m_ssn;
	std::string 	m_sasIp;

	int 					m_stackDlgId;
  int           m_userDlgId;
	int						m_appId;
	int						m_instId;
  short         m_suId;
  short         m_spId;
	bool					m_isDialogueComplete;

	U8  					*m_buf;				// SDP from SIP to Tca[
															// XML from TCap to SIP
	int						m_bufLen;			// Size of m_buf

	int						m_causeCode;	// G_SUCCESS, -1
  int           m_seqNum;
  int           m_threadIdx;
  int           m_billingNo;
}g_TransitObj;

//Yogesh: these blocks will be used 
//to create static blocks
struct initRWLock
{
  pthread_rwlock_t mLock;
  string mStr;
  initRWLock(pthread_rwlock_t &pLock,char* pStr){
    mLock = pLock;
    mStr = pStr;
    pthread_rwlock_init(&pLock, NULL);
  }
};

struct initMutex
{
  
  initMutex(pthread_mutex_t &pMutex){
    pthread_mutex_init(&pMutex, NULL);
  }

};

struct initCondVar
{
  initCondVar(pthread_cond_t &pCondVar)
  {
    pthread_cond_init(&pCondVar,NULL);
  }
};
#endif
