#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr");
/************************************************************************
     Name:     Measurement Worker Thread - implementation

     Type:     C implementation file

     Desc:     This file provides access to Measurement Worker Thread

     File:     MsrWorkerThread.C

     Sid:      MsrWorkerThread.C 0  -  11/14/03

     Prg:      gs

************************************************************************/

#include <INGwInfraMsrMgr/MsrIncludes.h>
#include <INGwInfraMsrMgr/MsrWorkerThread.h>
#include <INGwInfraMsrMgr/MsrUpdateMsg.h>
#include <INGwInfraMsrMgr/MsrPool.h>
#include <INGwInfraMsrMgr/MsrTimer.h>
#include <INGwInfraMsrMgr/MsrWU.h>
#include <INGwInfraMsrMgr/MsrValueMgr.h>
#include <INGwInfraMsrMgr/MsrIntf.h>
#include <Util/CoreScheduler.h>
#include<time.h>

#define SYNC_EMS_TIME 1

#if (!defined (_MSR_STUB_))
#include <INGwInfraManager/INGwIfrMgrManager.h>
#endif

using namespace std;

static const int MSR_WRK_QUEUE_LOW_MARK = 5000;

static const int MSR_UPDATE_HBEAT_TIME = 5;
 
//C'tor
MsrWorkerThread::MsrWorkerThread ()
{
  logger.logMsg (TRACE_FLAG, 0,
    "Entering MsrWorkerThread::MsrWorkerThread");

  mpTimerAdaptor = 0;
  mpIntf = 0;
  mChangeQueue.clear();
  pthread_mutex_init (&mChangeQueueLock, 0);

  logger.logMsg (TRACE_FLAG, 0,
    "Leaving MsrWorkerThread::MsrWorkerThread");
}

//D'tor
MsrWorkerThread::~MsrWorkerThread ()
{
  pthread_mutex_lock (&mChangeQueueLock);

  std::deque <MsrUpdateMsg*>::iterator iter;
  for (iter = mChangeQueue.begin();
       iter != mChangeQueue.end();
       iter++)
  {
    MsrUpdateMsg *lpMsg = *(iter);
    delete lpMsg;
  }
  mChangeQueue.clear();

  pthread_mutex_unlock (&mChangeQueueLock);

  pthread_mutex_destroy (&mChangeQueueLock);
}

bool 
MsrWorkerThread::postMsg(MsrTimer::MsrTimerMsg* apWork, bool chkFlag)
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrWorkerThread::postMsg");

  QueueData lData;
  lData.data = static_cast<void*>(apWork);

  if(chkFlag)
  {
     if(mQueue.queueSize() > MSR_WRK_QUEUE_LOW_MARK)
     {
        return false;
     }
  }

  mQueue.eventEnqueue(&lData);

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrWorkerThread::postMsg");

  return true;
}

bool
MsrWorkerThread::postUpdateMsg (MsrUpdateMsg* apMsg, bool chkFlag)
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrWorkerThread::postUpdateMsg");

  pthread_mutex_lock (&mChangeQueueLock);

  mChangeQueue.push_back (apMsg);

  pthread_mutex_unlock (&mChangeQueueLock);

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrWorkerThread::postUpdateMsg");

  return true;
}


int 
MsrWorkerThread::startTimer(int aiDuration,
                            unsigned int& auiTimerId,
                            bool abHBeat)
{

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrWorkerThread::startTimer <%d>", aiDuration);

  int retValue = MSR_SUCCESS;

  if (!mpTimerAdaptor)
    return MSR_FAIL;

  int liDuration = aiDuration * 1000;

  MsrTimer::MsrTimerMsg* pMsg = mpTimerAdaptor->getMessage();
  pMsg->mbIsHBeat = abHBeat;
  bool result = mpTimerAdaptor->startTimer(liDuration, pMsg, auiTimerId);
 
  if(!result) 
  {
     retValue = MSR_FAIL; 
  }

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrWorkerThread::startTimer");
  
  return retValue;
}

int 
MsrWorkerThread::stopTimer(unsigned int& auiTimerId)
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrWorkerThread::stopTimer");

  if (!mpTimerAdaptor)
    return MSR_FAIL;

  if (mpTimerAdaptor->stopTimer(auiTimerId))
  {
    //logger.logMsg (TRACE_FLAG, 0,
    //  "Leaving MsrWorkerThread::stopTimer");
    return MSR_SUCCESS;
  }

  return MSR_FAIL;
}

//here first need to process the update message 
//then work on the Set or Counter
void 
MsrWorkerThread::proxyRun(void)
{
  logger.logMsg(TRACE_FLAG, 0, "Worker thread [%d] started",
    pthread_self());

  mQueue.setName("MsrWorkerQueue");

  mpIntf = new MsrInterface;

#if (!defined (_MSR_STUB_))
  mpTimerAdaptor = new MsrTimer(*this,
                        INGwIfrMgrManager::getInstance().getExtraScheduler());
#else
  CoreScheduler*        mCoreScheduler;
  mCoreScheduler = CoreScheduler::initialize(1800000, 1000);
  mpTimerAdaptor = new MsrTimer(*this,
                        CoreScheduler::addedInstance(64000, 250));
#endif

  int dequeCount = MSR_WRK_QUEUE_LOW_MARK;
  QueueData* pQueueData = new QueueData[dequeCount];

  int sleepTime = 10;
  unsigned liTimerId = 0;
  int liTimerInterval = 0;
  int liPrevTimerInterval = 0;
  bool lbStartTimer = true;
  bool lbIsTimerRunning = false;

  startTimer (MSR_UPDATE_HBEAT_TIME, liTimerId, true);

  while(true == mRunStatus)
  {

#ifdef _MSR_STUB_
    logger.logMsg (VERBOSE_FLAG, 0,
      "Waiting for Event to occur.");
#endif

    int eventCount = mQueue.eventDequeueBlk(pQueueData, dequeCount,
                                            sleepTime, true);


//#ifdef _MSR_STUB_
    //logger.logMsg (VERBOSE_FLAG, 0,
    //  "VIMP Event Occurred. <%d> Events gathered.", eventCount);
//#endif

    if(eventCount == 0)
    {
      sleepTime = 0;
      dequeCount = 1;

      continue;
    }
    logger.logMsg (VERBOSE_FLAG, 0,
      "VIMP Step 1.");
	
    sleepTime = 10;
    dequeCount = MSR_WRK_QUEUE_LOW_MARK;
    
    for(int i = 0; i < eventCount; i++)
    {  
      MsrTimer::MsrTimerMsg* pWork = static_cast<MsrTimer::MsrTimerMsg*>(pQueueData[i].data);
      if(NULL != pWork && pWork->mbIsHBeat == true)
      {
	  
        processUpdateMsg ();

        mpTimerAdaptor->putMessage(
                          dynamic_cast<MsrTimer::MsrTimerMsg*>(pWork));

        pQueueData[i].data = NULL;
		
#if SYNC_EMS_TIME		
		// Get present time 
		int lTimerInterval = MSR_UPDATE_HBEAT_TIME;//mpIntf->getTimerInterval();
#if 0		
	    logger.logMsg (VERBOSE_FLAG, 0,
    	  "VIMP MsrWorkerThread--- timer interval = [%d]",mpIntf->getTimerInterval() );
	    logger.logMsg (VERBOSE_FLAG, 0,
    	  "VIMP MsrWorkerThread--- timer interval = [%d]",liPrevTimerInterval);
#endif		  
		  

		struct timeval lVal;
		gettimeofday (&lVal, 0);
	    //logger.logMsg (VERBOSE_FLAG, 0,
    	    // "VIMP MsrWorkerThread--- time = [%d]",lVal.tv_sec );
		int modByFive = lVal.tv_sec % lTimerInterval;//5;
	    // logger.logMsg (VERBOSE_FLAG, 0,
    	    // "VIMP MsrWorkerThread--- Mod By Five = [%d]",modByFive );
		int timeToSleep = 0;
		if(modByFive!=0){
		
			if(modByFive < lTimerInterval) {
			
				timeToSleep = lTimerInterval - modByFive;
			    //logger.logMsg (VERBOSE_FLAG, 0,
    			//  "VIMP MsrWorkerThread--- Time to sleep = [%d]",timeToSleep );
				
				struct timespec ts = { timeToSleep, 0};//timeToSleep };
				nanosleep( &ts, NULL );
				
			}
			
		}
#endif

        startTimer (MSR_UPDATE_HBEAT_TIME, liTimerId, true);


        MsrValueMgr *lpMgr = MsrValueMgr::getInstance();

        MsrPool<MsrInstantValue> *lpInsPool;
        MsrPool<MsrAccValue> *lpAccPool;
		
    // logger.logMsg (VERBOSE_FLAG, 0,
    //   "VIMP MsrWorkerThread--- About to take backup" );
		// BPInd10676 --- Problem in backing up the pool hence data collecton was not proper
		
        lpMgr->backup (MsrValueMgr::INSTANTANEOUS, lpInsPool);
        lpMgr->backup (MsrValueMgr::ACCUMULATED, lpAccPool);

        mpIntf->processBackupPool (lpInsPool);
        mpIntf->processBackupPool (lpAccPool);
	

        if (lbIsTimerRunning == false)
        {
		
          liTimerInterval = mpIntf->getTimerInterval();
		  
    //logger.logMsg (VERBOSE_FLAG, 0,
    //  "MsrWorkerThread--- Timer Interval from interface == [%d] ",liTimerInterval );

          if (liPrevTimerInterval == 0)
            liPrevTimerInterval = liTimerInterval;
			
    //logger.logMsg (VERBOSE_FLAG, 0,
    //  "MsrWorkerThread--- Previous Timer Interval  == [%d] ",liPrevTimerInterval );

          if (liTimerInterval == liPrevTimerInterval &&
              liTimerInterval != 0)
          {
            lbIsTimerRunning = true;
          }
          else if (liTimerInterval != liPrevTimerInterval)
          {
            liPrevTimerInterval = liTimerInterval;
            if (liTimerInterval != 0)
              lbIsTimerRunning = true;
          }

          if (lbIsTimerRunning)
		  
            startTimer (liTimerInterval, liTimerId);
        } 
		// Sandeep To be removed
		// else {
	    	// logger.logMsg (VERBOSE_FLAG, 0,
    	        // "MsrWorkerThread--- Timer is not Running" );
		// 
		//}
      }
      else if (NULL != pWork)
      {
        processUpdateMsg ();
        MsrValueMgr *lpMgr = MsrValueMgr::getInstance();

        MsrPool<MsrInstantValue> *lpInsPool;
        MsrPool<MsrAccValue> *lpAccPool;
		
    logger.logMsg (VERBOSE_FLAG, 0,
      "VIMP Next MsrWorkerThread--- About to take backup" );

        lpMgr->backup (MsrValueMgr::INSTANTANEOUS, lpInsPool);
        lpMgr->backup (MsrValueMgr::ACCUMULATED, lpAccPool);

        mpIntf->processBackupPool (lpInsPool);
        mpIntf->processBackupPool (lpAccPool);

        liTimerInterval = mpIntf->getTimerInterval();

        lbStartTimer = false;
        if (liPrevTimerInterval == 0)
          liPrevTimerInterval = liTimerInterval;

        if (liTimerInterval == liPrevTimerInterval &&
            liTimerInterval != 0)
        {
          lbStartTimer = true;
        }
        else if (liTimerInterval != liPrevTimerInterval)
        {
          liPrevTimerInterval = liTimerInterval;
          if (liTimerInterval != 0)
            lbStartTimer = true;
        }


        std::vector <MsrWU *>::iterator iter;
        for (iter = mpIntf->mCounterTickList.begin();
             iter != mpIntf->mCounterTickList.end();
             iter++)
        {
          MsrWU *lpUnit = *(iter);
          if (lpUnit && lpUnit->miWUType == MsrWU::MeasurementCounterWU)
          {
            lpUnit->miTimeouts++;

            logger.logMsg (VERBOSE_FLAG, 0,
              "Checking ticks for Counter<%s> Ticks <%d, %d>",
              lpUnit->mstrID.c_str(), lpUnit->miTimeouts,
              lpUnit->miTimeoutsReqd);

            if (lpUnit->miTimeoutsReqd <= lpUnit->miTimeouts)
            {
              lpUnit->miTimeouts = 0;
              mpIntf->handleScanInterval (lpUnit->mstrID);
            }
          }
        }

        for (iter = mpIntf->mSetTickList.begin();
             iter != mpIntf->mSetTickList.end();
             iter++)
        {
          MsrWU *lpUnit = *(iter);
          if (lpUnit && lpUnit->miWUType == MsrWU::MeasurementSetWU)
          {
            lpUnit->miTimeouts++;

            logger.logMsg (VERBOSE_FLAG, 0,
              "Checking ticks for Set<%s> Ticks <%d, %d>",
              lpUnit->mstrID.c_str(), lpUnit->miTimeouts,
              lpUnit->miTimeoutsReqd);

            if (lpUnit->miTimeoutsReqd <= lpUnit->miTimeouts)
            {
              lpUnit->miTimeouts = 0;
              mpIntf->handleCollectionInterval (lpUnit->mstrID);
            }
          }
        }

        //cleanup
        mpTimerAdaptor->putMessage(
                          dynamic_cast<MsrTimer::MsrTimerMsg*>(pWork));

        pQueueData[i].data = NULL;

        if (lbStartTimer)
        {
          startTimer (liTimerInterval, liTimerId);
        }
        else
          lbIsTimerRunning = false;
      }
    }
  }

}

int
MsrWorkerThread::processUpdateMsg ()
{
  //logger.logMsg (TRACE_FLAG, 0,
  //  "Entering MsrWorkerThread::processUpdateMsg");

  pthread_mutex_lock (&mChangeQueueLock);

  if (mChangeQueue.empty())
  {
    pthread_mutex_unlock (&mChangeQueueLock);
    //logger.logMsg (TRACE_FLAG, 0,
    //  "Leaving MsrWorkerThread::processUpdateMsg");
    return MSR_FAIL;
  }

  std::deque <MsrUpdateMsg*>::iterator iter;
  MsrUpdateMsg *lpMsg;

  for (iter = mChangeQueue.begin ();
       iter != mChangeQueue.end();
       iter++)
  {
    lpMsg = *(iter);
    if (lpMsg)
    {
      mpIntf->processUpdateMsg (lpMsg);

      delete lpMsg;
    }
  }

  mChangeQueue.clear ();

  pthread_mutex_unlock (&mChangeQueueLock);

  //logger.logMsg (TRACE_FLAG, 0,
  //  "Leaving MsrWorkerThread::processUpdateMsg");

  return MSR_SUCCESS;
}
