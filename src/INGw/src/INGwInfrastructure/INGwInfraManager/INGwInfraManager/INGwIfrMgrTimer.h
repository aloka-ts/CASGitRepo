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
//     File:     INGwIfrMgrTimer.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_IFR_MGR_TIMER_H_
#define _INGW_IFR_MGR_TIMER_H_

#include <unistd.h>
#include <pthread.h>

#include <Util/CoreScheduler.h>
#include <Util/TimerAdapter.h>
#include <Util/SchedulerMsg.h>
#include <Util/StatCollectorInf.h>

#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>
#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>

class INGwIfrMgrWorkerThread;

class INGwIfrMgrTimer : public virtual TimerAdapter,
                   public virtual INGwIfrMgrWorkerClbkIntf,
                   public StatCollectorInf
{
   private:

      SchedulerInfo *extraScheduler;

   public:

      class INGwIfrMgrTimerMsg : public virtual SchedulerMsg,
                            public virtual INGwIfrMgrWorkUnit
      {
         public:

            INGwIfrMgrTimerMsg* mpNextMsg;
            void*          mpContext;
      };

      INGwIfrMgrTimer(INGwIfrMgrWorkerThread& arWorker, CoreScheduler *apSche);
      virtual ~INGwIfrMgrTimer();

      virtual int handleWorkerClbk(INGwIfrMgrWorkUnit *apWork);

      void ProcessMessages(SchedulerMsg* apMsg);
      void ProcessGarbageMessages(SchedulerMsg* apMsg);

      INGwIfrMgrTimerMsg* getMessage(void);
      void putMessage(INGwIfrMgrTimerMsg* apMsg);

      bool startTimer(u_int auiDuration, INGwIfrMgrTimerMsg* apMsg, 
                      unsigned int& aruiTimerId);
      bool stopTimer(unsigned int &auiTimerId);

      std::string toLog(int tabCount) const;

   protected:

      INGwIfrMgrWorkerThread&  mrWorkerThread;

      INGwIfrMgrTimerMsg*   mpMsgList;
      int _msgListCount;
      int _allocObjCount;

   private:

      INGwIfrMgrTimer(const INGwIfrMgrTimer& p_Self);
      INGwIfrMgrTimer& operator=(const INGwIfrMgrTimer& p_Self);
};

#endif

// EOF INGwIfrMgrTimer.h
