#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr");

//////////////////////////////////////////////////////////////////////////
//
//        Copyright (c) 2000, Bay Packets Inc.
//        All rights reserved.
//
//        FILE_NAME: MsrThread.C
//
//////////////////////////////////////////////////////////////////////////

#include <INGwInfraMsrMgr/MsrThread.h>
#include <INGwInfraUtil/INGwIfrUtlMacro.h>

using namespace std;


MsrThread::MsrThread(void) :
    mRunStatus(false) { }

MsrThread::~MsrThread() { }

int
MsrThread::initialise(void) { return 0; }

extern "C" void* MsrThread::workerThread(void* p_Arg)
{
    MsrThread* thisPtr = (MsrThread*)p_Arg;
    thisPtr->proxyRun();
    return 0;
}

int
MsrThread::start(bool abIsBound)
{
    int retValue = -1;

    if(true == mRunStatus) {
        LogINGwError(false, 0, "Thread already started");
    }
    else {
        pthread_attr_init(&mAttr);
        if(true == abIsBound) {
            retValue = pthread_attr_setscope(&mAttr, PTHREAD_SCOPE_SYSTEM);
            if(0 != retValue) logger.logINGwMsg(false, ERROR_FLAG, 0, "pthread_attr_setscope() returned %d", retValue);
            retValue = pthread_attr_setschedpolicy(&mAttr, SCHED_FIFO);
            if(0 != retValue) logger.logINGwMsg(false, ERROR_FLAG, 0, "pthread_attr_setschedpolicy() returned %d", retValue);
        }

        mRunStatus = true;
        if( 0 == pthread_create(&mThreadId, &mAttr, workerThread, (void*)this) ) {
            logger.logINGwMsg(false, ERROR_FLAG, 0, "Thread Id %d started", getThreadId());
            retValue = 0;
        }
        else {
            LogINGwError(false, 0, "Could not start the thread");
        }
    }

    return retValue;
}

int
MsrThread::start(int aiCPUId)
{
    int retValue = -1;
#ifdef BIND_THREADS_TO_CPU
    mProcessorId = aiCPUId;
#endif
    LogINGwError(false, 0, "This method is Not implemented");
    return retValue;
}

#ifdef BIND_THREADS_TO_CPU
void 
MsrThread::bindToCPU(void)
{
    processorid_t obind;
    if (processor_bind(P_LWPID, P_MYID, mProcessorId, &obind) == -1) {
        logger.logINGwMsg(false, ERROR_FLAG, 0, "Failed to bind LWP to processor %d\n", mProcessorId);
        exit(1);
    }
    logger.logINGwMsg(false, ERROR_FLAG, 0, "Binding TID %d to processor %d\n", MACRO_THREAD_ID(), mProcessorId);
}
#endif

int
MsrThread::stop(bool abIsGraceful)
{
    int retValue = 0;
    if(true == abIsGraceful) {
        mRunStatus = false;
        logger.logINGwMsg(false, VERBOSE_FLAG, 0, "Going into wait on join of thread Id %d", getThreadId());
        pthread_join(mThreadId, NULL);
        logger.logINGwMsg(false, VERBOSE_FLAG, 0, "Coming out of wait on join of thread Id %d", getThreadId());
    }
    else {
        mRunStatus = false;
    }
    return retValue;
}

void 
MsrThread::waitInSec(unsigned long aulWaitTime)
{
    sleep(aulWaitTime);
}

void 
MsrThread::waitInMiliSec(unsigned long aulWaitTime)
{
    usleep(aulWaitTime * 1000);
}

// EOF MsrThread.C
