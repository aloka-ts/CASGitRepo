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
#ifndef _INGW_TCAP_STUB_H_
#define _INGW_TCAP_STUB_H_

#include <INGwTcapProvider/INGwTcapInclude.h>
#include <queue>

using namespace std;

class INGwTcapStub
{
	public:

		INGwTcapStub();

		~INGwTcapStub();

		static INGwTcapStub&
		getInstance();

	private:

		void
		run();

		void
		processMsg(stubInfo* p_msg);

		queue<stubInfo*>	g_msgQ;
		bool	m_running;
		static INGwTcapStub *m_selfPtr;
};

#endif
