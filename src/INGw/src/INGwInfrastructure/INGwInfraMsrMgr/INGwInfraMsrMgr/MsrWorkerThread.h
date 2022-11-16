/************************************************************************
     Name:     Measurement Worker Thread - includes

     Type:     C include file

     Desc:     This file provides access to Measurement Worker Thread

     File:     MsrWorkerThread.h

     Sid:      MsrWorkerThread.h 0  -  11/14/03

     Prg:      gs

************************************************************************/

#ifndef _MSR_WORKER_THREAD_H_
#define _MSR_WORKER_THREAD_H_

#include "Util/QueueMgr.h"
#include "Util/TimerAdapter.h"
#include "Util/SchedulerMsg.h"
#include <INGwInfraMsrMgr/MsrThread.h>
#include <deque>
#include <INGwInfraMsrMgr/MsrTimer.h>

class MsrWU;
class MsrUpdateMsg;
class MsrInterface;

class MsrWorkerThread : public virtual MsrThread
{
  public:

    //C'tor
    MsrWorkerThread ();

    //D'tor
    ~MsrWorkerThread ();

    bool postMsg(MsrTimer::MsrTimerMsg* apWork, bool chkFlag = false);
    bool postUpdateMsg (MsrUpdateMsg* apWork, bool chkFlag = false);

    int processUpdateMsg ();

    int startTimer(int aiDuration, unsigned int& auiTimerId, bool abHBeat = false);
    int stopTimer(unsigned int& auiTimerId);

  protected:

    //here first need to process the update message 
    //then work on the Set or Counter
    void proxyRun(void);

    QueueManager    mQueue;
    MsrTimer*     mpTimerAdaptor;
    MsrInterface *     mpIntf;

    //change queue may be worked on whenever the worker thread is free
    //and before processing the timeouts
    std::deque <MsrUpdateMsg*> mChangeQueue;
    pthread_mutex_t            mChangeQueueLock;
};

#endif /* _MSR_WORKER_THREAD_H_ */
