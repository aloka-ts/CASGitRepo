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
//     File:     INGwTcapMsgReceiver.h
//
//     Desc:     <Description of file>
//
//     Author     	Date     		Description
//    ----------------------------------------------------------------
//     Rajeev Arya     11/21/07			Initial Creation
//********************************************************************
#ifndef _INGWE_TCAP_MSG_RECEIVER_H_
#define _INGWE_TCAP_MSG_RECEIVER_H_

#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>
#include<pthread.h>

// PR - 51361

enum e_ReRegisterState
{
  ReRegisterState_NONE,
  ReRegisterState_REREGISTER,
  ReRegisterState_INPROGRESS,
  ReRegisterState_COMPLETE
};

static const char* registerStateStr [] = 
{
  "ReRegisterState_NONE",
  "ReRegisterState_REREGISTER",
  "ReRegisterState_INPROGRESS",
  "ReRegisterState_COMPLETE"
};


class INGwTcapMsgReceiver
{
	public:

		static INGwTcapMsgReceiver&
		getInstance();

		int
		initialize(INGwIfrMgrWorkerClbkIntf& p_callbackPtr);

		int
		start();

		int 
		stop();

		void
		startReading();

		void
		recvMsgFromStack();

		void
		markRegistered();

    // PR - 51361
    void 
    setReRegisterState(e_ReRegisterState p_ReRegisterState);

    e_ReRegisterState 
    getReRegisterState();

	private:

		INGwTcapMsgReceiver();

		static INGwTcapMsgReceiver*	m_selfPtr;

		bool		m_isRunning;
		bool		m_isRegistered;
		bool		m_readerFlag;

		void 		*m_appContext;

		INGwIfrMgrWorkerClbkIntf*	m_workerCallBackIf;

    pthread_rwlock_t m_RWLock;
    e_ReRegisterState  m_ReRegisterState;
};
#endif
