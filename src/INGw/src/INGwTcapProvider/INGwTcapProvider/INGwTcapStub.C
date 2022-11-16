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
//     File:     INGwTcapStub.h
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwTcapProvider");

#include <INGwTcapProvider/INGwTcapStub.h>

INGwTcapStub* INGwTcapStub::m_selfPtr = NULL;

INGwTcapStub::INGwTcapStub():m_running(false)
{
}

INGwTcapStub::~INGwTcapStub()
{
}

INGwTcapStub&
INGwTcapStub::getInstance()
{
	if (NULL == m_selfPtr) {
		m_selfPtr = new INGwTcapStub;
	}

	return *m_selfPtr;
}

void
INGwTcapStub::run()
{
	LogINGwTrace(false, 0,  "IN INGwTcapStub::StubMsgRcv()");

	m_running = true;
  while (m_running) {
	
		if (true == g_msgQ.empty()) {
			sleep (TCAP_REG_SLEEP);
			continue;
		}

		stubInfo *lMsg = g_msgQ.front();
		g_msgQ.pop();

		if (NULL == lMsg) {
			delete lMsg;
			continue;
		}

		processMsg(lMsg);
	}

	LogINGwTrace(false, 0, "OUT INGwTcapMsgReceiver::StubMsgRcv()");
}

int
INGwTcapStub::processMsg(stubInfo* p_msg)
{
	switch(p_msg->m_type) {

		case STUB_UNKNOWN: {
			break;
		}

		case STUB_ACK: {
			string inStr;
			inStr += "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n";
			inStr += "<tcap>\n";
		  inStr += "	<configuration>\n";
			inStr += "    <local-user-address sub-system-number=\"241\">\n";
			inStr += "       <signaling-point-code cluster=\"111\" member=\"5\" zone=\"1\"/>\n";
			inStr += "    </local-user-address>\n";
			inStr += "  </configuration>\n";
			inStr += "</tcap>\n";
		}

		case STUB_SCCP_MGMT_IN_SRV: {
		}

		case STUB_SCCP_MGMT_OUT_SRV: {
		}

		case STUB_OUTBOUND_RSLT: {
		}

		case STUB_INBOUND: {
		}
	}
}

