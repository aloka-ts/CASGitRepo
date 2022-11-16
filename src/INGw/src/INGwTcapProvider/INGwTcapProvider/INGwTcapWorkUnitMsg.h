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
//     File:     INGwTcapWorkUnitMsg.h
//
//     Desc:     <Description of file>
//
//     Author       Date        Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07     Initial Creation
//********************************************************************
#ifndef _INGW_TCAP_WORK_UNIT_MSG_H_
#define _INGW_TCAP_WORK_UNIT_MSG_H_

#include <INGwTcapProvider/INGwTcapInclude.h>

#ifndef __TCAPMESSAGE
#include <INGwTcapMessage/TcapMessage.hpp>
#endif /*__TCAPMESSAGE*/

#include <string>

using namespace std;

typedef struct t_INGwTcapWorkUnitMsg 
{
  /* This shall have the message types corresponding to Dialogue,
     SCCP management, Stack layers bind,unbind,status*/
  U8             eventType;
	S16 					 m_suId;
	S16						 m_spId;
  U32            m_dlgId;  
  string         m_callId;
  AppInstId      m_appInstId;
  TcapMessage    *m_tcapMsg;
  /* Used for SCCP management messages*/
  INCSS7SteMgmt  ss7SteMgmt;
  /*This shall be used in case of Bind Confirm indication from stack*/
  S16           sapStatus;
  U8            m_ssn;
  U32           m_pc;
  /*Whether TCAP Component present in the TCAP Msg*/    
  bool          compPres;
  g_TransitObj  *m_transObj;  
  int            m_threadIdx; 
} INGwTcapWorkUnitMsg;

#endif

