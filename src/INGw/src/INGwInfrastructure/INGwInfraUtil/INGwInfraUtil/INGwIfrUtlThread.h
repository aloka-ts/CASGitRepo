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
//     File:     INGwIfrUtlThread.h
//
//     Desc:      Utility used to ping the machine availability.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <unistd.h>
#include <pthread.h>

#include <string>


#ifndef INCLUDE_INGwIfrUtlThread
#define INCLUDE_INGwIfrUtlThread

#ifdef BIND_THREADS_TO_CPU
#include <sys/processor.h>
#include <sys/procset.h>
#endif

#include <INGwInfraUtil/INGwIfrUtlMacro.h>

class INGwIfrUtlThread
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

      INGwIfrUtlThread(void);
      virtual ~INGwIfrUtlThread();

      void bindToCPU(void);

      virtual void proxyRun(void) = 0;
      static void* workerThread(void*);

      pthread_attr_t mAttr;

      int mProcessorId;
   
      bool mRunStatus;

   private:

      pthread_t mThreadId;

      INGwIfrUtlThread(const INGwIfrUtlThread& p_Self);
      INGwIfrUtlThread& operator=(const INGwIfrUtlThread& p_Self);
};

#endif

// EOF INGwIfrUtlThread.h
