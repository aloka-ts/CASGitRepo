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
//     File:     INGwIwfIface.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_IWF_IFACE_H_
#define _INGW_IWF_IFACE_H_

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwIwf/INGwIwfBaseIface.h>

#include <INGwTcapProvider/INGwTcapIface.h>
#include <INGwSipProvider/INGwSpSipIface.h>
#include <unistd.h>
#include <string>
using namespace std;

class INGwIwfIface: public INGwIwfBaseIface
{
	public:

		INGwIwfIface();

		~INGwIwfIface();
     
		void
		setTcapIface(INGwIwfBaseIface *p_iface);

		void
		setSipIface(INGwIwfBaseIface *p_iface);

		//
		// Following methods shall be used by SIP Provider
		//
		int
		processSasInfo(g_TransitObj &p_transitObj);
  
    int 
    processSeqNumAck(U8 p_direction, int p_dialogueId, int p_seqNum, bool isLast);

		void
		getOpcSsnList(g_TransitObj &p_transitObj);

		int
		processOutboundMsg(g_TransitObj	&p_transitObj);

		int
		deregisterSas(g_TransitObj &p_transitObj);

		void
		processSasErrResp(g_TransitObj &p_transitObj);

		//
		// Method to be used by TCAP provider
		//

		int
		processInboundMsg(g_TransitObj  &p_transitObj); 

		int
		processReplayInboundMsg(g_TransitObj  &p_transitObj); 

		int
		sendSasAppResp(g_TransitObj &p_transitObj);

#ifdef INGW_LOOPBACK_SAS
    int
    initLoopbackDlgInfo(int p_loDlg, int p_hiDlg);
#endif

	private:
		INGwTcapIface		*m_tcapIface;
		INGwSpSipIface 		*m_sipIface;
};

#endif

