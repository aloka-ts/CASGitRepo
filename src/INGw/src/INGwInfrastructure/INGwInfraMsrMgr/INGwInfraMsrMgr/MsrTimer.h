//////////////////////////////////////////////////////////////////////////
//
//        Copyright (c) 2000, Bay Packets Inc.
//        All rights reserved.
//
//        FILE_NAME: BpCCMTimer.h
//
//////////////////////////////////////////////////////////////////////////

#ifndef _MSR_TIMER_H_
#define _MSR_TIMER_H_

#include <unistd.h>
#include <pthread.h>

#include "Util/CoreScheduler.h"
#include "Util/TimerAdapter.h"
#include "Util/SchedulerMsg.h"

class MsrWorkerThread;

class MsrTimer : public virtual TimerAdapter
{
   private:

	  SchedulerInfo *extraScheduler;

   public:

      class MsrTimerMsg : public virtual SchedulerMsg
      {
         public:
            MsrTimerMsg*   mpNextMsg;
            void*          mpContext;

            bool           mbIsHBeat;
      };

      MsrTimer (MsrWorkerThread& arWorker, CoreScheduler *apSche);
      virtual ~MsrTimer();

      void ProcessMessages(SchedulerMsg* apMsg);
      void ProcessGarbageMessages(SchedulerMsg* apMsg);

      MsrTimerMsg* getMessage(void);
      void putMessage(MsrTimerMsg* apMsg);

      bool startTimer(unsigned int auiDuration, MsrTimerMsg* apMsg, 
					            unsigned int& aruiTimerId);
      bool stopTimer(unsigned int auiTimerId);

   protected:

      MsrWorkerThread &  mrWorkerThread;
      MsrTimerMsg    *   mpMsgList;

   private:

      MsrTimer (const MsrTimer& p_Self);
      MsrTimer & operator=(const MsrTimer& p_Self);
};

#endif /* _MSR_TIMER_ */
