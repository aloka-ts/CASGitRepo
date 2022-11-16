//////////////////////////////////////////////////////////////////////////
//
//        Copyright (c) 2000, Bay Packets Inc.
//        All rights reserved.
//
//        FILE_NAME: MsrThread.h
//
//////////////////////////////////////////////////////////////////////////

#include <unistd.h>
#include <pthread.h>

#include <string>


#ifndef INCLUDE_MsrThread
#define INCLUDE_MsrThread

#ifdef BIND_THREADS_TO_CPU
#include <sys/processor.h>
#include <sys/procset.h>
#endif

#include <INGwInfraUtil/INGwIfrUtlMacro.h>

class MsrThread
{
   public:

      int initialise(void);

      inline int getThreadId(void) { return MACRO_RET_THREAD_ID(mThreadId); }

      int start(bool abIsBound = false);
      int start(int aiCPUId);
      int stop(bool abIsGraceful = true);

      static void waitInSec(unsigned long aulWaitTime);
      static void waitInMiliSec(unsigned long aulWaitTime);

   protected:

      MsrThread(void);
      virtual ~MsrThread();

#ifdef BIND_THREADS_TO_CPU
      void bindToCPU(void);
#endif

      virtual void proxyRun(void) = 0;
      static void* workerThread(void*);

      pthread_attr_t          mAttr;

#ifdef BIND_THREADS_TO_CPU
      processorid_t           mProcessorId;
#endif
   
      bool                    mRunStatus;

   private:

      pthread_t               mThreadId;

      MsrThread(const MsrThread& p_Self);
      MsrThread& operator=(const MsrThread& p_Self);
};

#endif

// EOF MsrThread.h
