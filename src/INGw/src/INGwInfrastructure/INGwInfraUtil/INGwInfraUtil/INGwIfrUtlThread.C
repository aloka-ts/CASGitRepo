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
//     File:     INGwIfrUtlThread.C
//
//     Desc:      Utility used to ping the machine availability.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraUtil");

#include <INGwInfraUtil/INGwIfrUtlThread.h>

#include <errno.h>

#ifdef sun4Sol28
#include <sys/types.h>
#include <sys/processor.h>
#endif

using namespace std;

INGwIfrUtlThread::INGwIfrUtlThread(void) : 
mRunStatus(false) ,
mProcessorId(-1)
{ 
}

INGwIfrUtlThread::~INGwIfrUtlThread() { }

int INGwIfrUtlThread::initialise(void) 
{ 
   return 0; 
}

extern "C" void* INGwIfrUtlThread::workerThread(void* p_Arg)
{
   INGwIfrUtlThread* thisPtr = (INGwIfrUtlThread*)p_Arg;
   thisPtr->bindToCPU();
   thisPtr->proxyRun();
   return 0;
}

int INGwIfrUtlThread::start(bool abIsBound)
{
   if(true == mRunStatus) 
   {
      LogINGwWarning(false, 0, "Thread already started");
      return 0;
   }

   int retValue = -1;

   pthread_attr_init(&mAttr);
   if(true == abIsBound) 
   {
      retValue = pthread_attr_setscope(&mAttr, PTHREAD_SCOPE_SYSTEM);

      if(0 != retValue) 
      {
         logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "pthread_attr_setscope() returned %d", retValue);
      }

      retValue = pthread_attr_setschedpolicy(&mAttr, SCHED_FIFO);

      if(0 != retValue) 
      {
         logger.logINGwMsg(false, ERROR_FLAG, 0, 
                         "pthread_attr_setschedpolicy() returned %d", retValue);
      }
   }

   mRunStatus = true;

   if( 0 == pthread_create(&mThreadId, &mAttr, workerThread, (void*)this) ) 
   {
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Thread Id %d started", 
                      getThreadId());
      retValue = 0;
   }
   else 
   {
      LogINGwError(false, 0, "Could not start the thread");
   }

    return retValue;
}

int INGwIfrUtlThread::start(int aiCPUId)
{
    int retValue = -1;
    mProcessorId = aiCPUId;
    return retValue;
}

void INGwIfrUtlThread::bindToCPU(void)
{
   // Not using bind for Now
   return;

   if(mProcessorId == -1)
   {
      return;
   }

#ifdef sun4Sol28

#if 0
   From CCM prespective we assume the processor id to be 0, 1, 2, 3...
   From System prespective processor id can be 2, 5, 6, 9 ...

   Say if there are only 4 processor available (3, 5, 6, 8) and CCM asks the 
   thread to bind to processor 7. Since 7 is invalid processor we make mod to 
   make it valid processor 3. CCM Processor 3 is system processor 6. So the 
   thread will bind to System processor id 6. -Suriya
#endif

   int numOfProcessor = sysconf(_SC_NPROCESSORS_ONLN);
   mProcessorId %= numOfProcessor;

   logger.logMsg(ALWAYS_FLAG, 0, "Total processor online [%d]", numOfProcessor);

   processorid_t reqProcessorId = 0;
   numOfProcessor = mProcessorId + 1;

   for(int idx = 0; numOfProcessor > 0; idx++)
   {
      int status = p_online(idx, P_STATUS);

      if(status == -1)
      {
         if(errno == EPERM)
         {
            logger.logMsg(ERROR_FLAG, 0, "Permission problem [%s]", 
                          strerror(errno));
            return;
         }

         logger.logMsg(ERROR_FLAG, 0, "P_online returns [%s] for [%d]",
                       strerror(errno), idx);
         continue;
      }

      numOfProcessor--;

      if(numOfProcessor == 0)
      {
         reqProcessorId = idx;
         break;
      }
   }

   logger.logMsg(ALWAYS_FLAG, 0, "System processor id [%d]", reqProcessorId);

   processorid_t obind;

   if(processor_bind(P_LWPID, P_MYID, reqProcessorId, &obind) == -1) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
                      "Failed to bind LWP to processor [%d] [%s]", 
                      reqProcessorId, strerror(errno));
      return;
   }

   logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Binding TID %d to processor %d\n", 
                   MACRO_THREAD_ID(), reqProcessorId);
#endif
}

int INGwIfrUtlThread::stop(bool abIsGraceful)
{
   int retValue = 0;

   if(true == abIsGraceful) 
   {
      mRunStatus = false;

      logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
                      "Going into wait on join of thread Id %d", getThreadId());

      pthread_join(mThreadId, NULL);

      logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
                      "Coming out of wait on join of thread Id %d", 
                      getThreadId());
   }
   else 
   {
      mRunStatus = false;
   }

   return retValue;
}

void INGwIfrUtlThread::waitInSec(unsigned long aulWaitTime)
{
   sleep(aulWaitTime);
}

void INGwIfrUtlThread::waitInMiliSec(unsigned long aulWaitTime)
{
   usleep(aulWaitTime * 1000);
}

// EOF INGwIfrUtlThread.C
