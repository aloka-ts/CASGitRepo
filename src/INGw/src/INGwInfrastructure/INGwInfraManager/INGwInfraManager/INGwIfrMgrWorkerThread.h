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
//     File:     INGwIfrMgrWorkerThread.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************


#ifndef _INGW_IFR_MGR_WORKER_THREAD_H_
#define _INGW_IFR_MGR_WORKER_THREAD_H_

#include <unistd.h>
#include <pthread.h>

#include <list>

#include <Util/QueueMgr.h>
#include <Util/TimerAdapter.h>
#include <Util/SchedulerMsg.h>
#include <INGwInfraUtil/INGwIfrUtlThread.h>

#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>
#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>
#include "INGwInfraManager/INGwIfrMgrTimer.h"
using namespace std;
class INGwIfrMgrThreadMgr;
class INGwIfrMgrTimer;

class INGwIfrMgrWorkerThread : public virtual INGwIfrUtlThread,
                       public virtual INGwIfrMgrWorkerClbkIntf
{
   public:

      INGwIfrMgrWorkerThread();
      virtual ~INGwIfrMgrWorkerThread();
      int handleWorkerClbk(INGwIfrMgrWorkUnit* apWork);

      bool postMsg(INGwIfrMgrWorkUnit* apWork, bool chkFlag = false);
      void postBulkMsg(INGwIfrMgrWorkUnit** apWork, int count);
      void postOutOfBandMsg(INGwIfrMgrWorkUnit* apWork);
      int startTimer(int aiDuration, void* apContext, INGwIfrMgrWorkerClbkIntf* apClbk, 
                     unsigned int& auiTimerId);
      int stopTimer(unsigned int& auiTimerId);
      void setReplayThreadFlg(bool pVal);
      void setSipIOThreadFlg(bool pVal);
      void setThreadIdx(int aiIndex);

      int getThreadIdx();
   private:

      int miThreadIdx;
      int _failureCount;
      bool isReplayThread;
      bool mbIsSipIOThr;
   protected:

      void proxyRun(void);

      INGwIfrMgrThreadMgr* mpThreadMgr;
      QueueManager    mQueue;
      INGwIfrMgrTimer*     mpTimerAdaptor;

      int                mDepthLimit;
      int                miOutOfBandMsgCount;
      pthread_mutex_t    mOutOfBandMsgLock;
      std::list<INGwIfrMgrWorkUnit*>* mpOutOfBandMsgList;

   private:

      INGwIfrMgrWorkerThread(const INGwIfrMgrWorkerThread& p_Self);
      INGwIfrMgrWorkerThread& operator=(const INGwIfrMgrWorkerThread& p_Self);

};

#endif

// EOF INGwIfrMgrWorkerThread.h
