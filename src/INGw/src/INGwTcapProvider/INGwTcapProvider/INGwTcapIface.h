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
//     File:     INGwTcapIface.h
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#ifndef _INGW_TCAP_IFACE_H_
#define _INGW_TCAP_IFACE_H_

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwIwf/INGwIwfBaseIface.h>
#include <INGwLoadDistributor/INGwLdDstMgr.h>
class INGwTcapIface:public INGwIwfBaseIface
{
	public:

		INGwTcapIface();

		~INGwTcapIface();

		int
		initialize(INGwLdDstMgr *p_ldDstMgr);

		void
		getOpcSsnList(g_TransitObj &p_transitObj);

		int
		processSasInfo(g_TransitObj &p_transitObj);

	 	int
		processOutboundCall(g_TransitObj	&p_tranitObj);

		int
		deregisterSas(g_TransitObj &p_transitObj);

		void
		setProtoType(bool p_protoType);

		void 
		processSasErrResp(g_TransitObj &p_transitObj);

		void
		cleanup(const string &p_sasIp, int p_appId, int p_instId); 

    int 
    processSeqNumAck(U8 p_direction, int p_dialogueId, int p_seqNum, bool p_isLast);

	private:
		
		INGwLdDstMgr		*m_ldDstMgr;
		long 						 m_selfId;
		long 						 m_peerId;
		bool						 m_protoType;
};
#endif
