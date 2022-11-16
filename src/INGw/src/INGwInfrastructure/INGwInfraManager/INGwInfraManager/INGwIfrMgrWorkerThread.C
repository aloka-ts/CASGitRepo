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
//     File:     INGwIfrMgrWorkerThread.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraManager");


using namespace std;
#include <sstream>
#include <assert.h>


#include <INGwInfraParamRepository/INGwIfrPrIncludes.h>
#include <INGwInfraUtil/INGwIfrUtlMacro.h>
#include <INGwInfraManager/INGwIfrMgrTimer.h>
#include <INGwInfraManager/INGwIfrMgrWorkerThread.h>
#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>
#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>
#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>

#include <INGwInfraManager/INGwIfrMgrManager.h>

const int WRK_QUEUE_LOW_MARK = 45;
pthread_cond_t  gUnblock;
pthread_mutex_t gCvLock;
static initCondVar gCondVar(gUnblock);
static initMutex   gCvMutex(gCvLock);

extern bool gIsMsgStoreEmpty();

INGwIfrMgrWorkerThread::INGwIfrMgrWorkerThread() : 
    mpThreadMgr(NULL),
    mpTimerAdaptor(NULL),
    miOutOfBandMsgCount(0),
    mpOutOfBandMsgList(NULL),
    mDepthLimit(WRK_QUEUE_LOW_MARK),
    mQueue(false, WRK_QUEUE_LOW_MARK * 10, WRK_QUEUE_LOW_MARK),
    _failureCount(0)
{
   LogINGwTrace(false, 0, "IN INGwIfrMgrWorkerThread()");

   pthread_mutex_init(&mOutOfBandMsgLock, NULL);
   mpOutOfBandMsgList = new list<INGwIfrMgrWorkUnit*>();
   miThreadIdx = -1;
   string limitStr = 
                  INGwIfrPrParamRepository::getInstance().getValue(ingwWORKER_Q_SIZE_LIMIT);
   mDepthLimit = atoi(limitStr.c_str());

   if(mDepthLimit <= 0)
   {
      mDepthLimit = WRK_QUEUE_LOW_MARK;
   }
   logger.logMsg(ALWAYS_FLAG, 0, "Depth limit [%d]", mDepthLimit);
   isReplayThread = false;
   mbIsSipIOThr = false; 
   LogINGwTrace(false, 0, "OUT INGwIfrMgrWorkerThread()");
}

INGwIfrMgrWorkerThread::~INGwIfrMgrWorkerThread()
{
   LogINGwTrace(false, 0, "IN ~INGwIfrMgrWorkerThread()");

   pthread_mutex_destroy(&mOutOfBandMsgLock);
   delete mpOutOfBandMsgList;

   LogINGwTrace(false, 0, "OUT ~INGwIfrMgrWorkerThread()");
}

int 
INGwIfrMgrWorkerThread::handleWorkerClbk(INGwIfrMgrWorkUnit* apWork)
{
   LogINGwTrace(false, 0, "IN handleWorkerClbk()");

   int retValue = 0;

   switch(apWork->meWorkType)
   {
      case INGwIfrMgrWorkUnit::WORKER_SELF_MSG:
      {
         INGwIfrMgrWorkUnit **msgList = (INGwIfrMgrWorkUnit **)apWork->mpMsg;
         int count = apWork->mulMsgSize;

         for(int idx = 0; idx < count; idx++)
         {
            if(msgList[idx] != NULL)
            {
               msgList[idx]->mpWorkerClbk->handleWorkerClbk(msgList[idx]);
               delete msgList[idx];
            }
         }

         delete []msgList;

         logger.logMsg(TRACE_FLAG, 0, "Processed [%d] bulk units.", count);
      }
      break;

      case INGwIfrMgrWorkUnit::UPDATE_TSD_MSG: 
      {
         pthread_key_t* pKey = 
                             static_cast<pthread_key_t*>(apWork->mpContextData);

         int errCode = pthread_setspecific(*pKey, apWork->mpMsg);

         if(0 != errCode) 
         {
            logger.logINGwMsg(false, ERROR_FLAG, -1, 
                            "Error: pthread_setspecific() failed [%d] [%s]", 
                            errCode, strerror(errCode));
            retValue = -1;
         }
      }
      break;

      default: 
      {
         logger.logINGwMsg(false, ERROR_FLAG, -1, 
                         "Unhandled Work unit type [%d] received", 
                         apWork->meWorkType);
         retValue = -1;
      }
   }

   LogINGwTrace(false, 0, "OUT handleWorkerClbk()");
   return retValue;
}

bool INGwIfrMgrWorkerThread::postMsg(INGwIfrMgrWorkUnit* apWork, bool chkFlag)
{
   QueueData lData;
   lData.data = static_cast<void*>(apWork);

   //mDepthLimit condition is to disable the chkFlag.

   if(chkFlag && (mDepthLimit != 63))  
   {
      int queueSize = mQueue.queueSize();
      if(queueSize > mDepthLimit)
      {
         logger.logMsg(WARNING_FLAG, 0, 
                       "PostMsg failed. ID[%d] QueueSize[%d] Depth[%d]",
                       getThreadId(), queueSize, mDepthLimit);

         _failureCount++;

         //if(_failureCount > 10)
         if(queueSize > (mDepthLimit << 1))
         {
            struct timespec rqtp;
            rqtp.tv_sec = 0;
            rqtp.tv_nsec = 5000000L;

            nanosleep(&rqtp, NULL);
            _failureCount = 0;
         }

         return false;
      }
   }

   mQueue.eventEnqueue(&lData);
   return true;
}

void INGwIfrMgrWorkerThread::postBulkMsg(INGwIfrMgrWorkUnit **apWorkList, int count)
{
   INGwIfrMgrWorkUnit **msgList = new INGwIfrMgrWorkUnit*[count];

   for(int idx = 0; idx < count; idx++)
   {
      msgList[idx] = apWorkList[idx];
   }

   INGwIfrMgrWorkUnit *newUnit = new INGwIfrMgrWorkUnit();
   newUnit->meWorkType = INGwIfrMgrWorkUnit::WORKER_SELF_MSG;
   newUnit->mulMsgSize = count;
   newUnit->mpWorkerClbk = this;
   newUnit->mpMsg = msgList;

   postMsg(newUnit);
   return;
}

void INGwIfrMgrWorkerThread::postOutOfBandMsg(INGwIfrMgrWorkUnit* apWork)
{
   pthread_mutex_lock(&mOutOfBandMsgLock);
   miOutOfBandMsgCount++;
   mpOutOfBandMsgList->push_back(apWork);
   pthread_mutex_unlock(&mOutOfBandMsgLock);
}

int INGwIfrMgrWorkerThread::startTimer(int aiDuration, void* apContext, 
                               INGwIfrMgrWorkerClbkIntf* apClbk, 
                               unsigned int& aruiTimerId)
{
   int retValue = 0;
   INGwIfrMgrTimer::INGwIfrMgrTimerMsg* pMsg = mpTimerAdaptor->getMessage();
   pMsg->mpContextData = apContext;
   pMsg->mpWorkerClbk = apClbk;
   bool result = mpTimerAdaptor->startTimer(aiDuration, pMsg, aruiTimerId);

   if(!result) 
   {
      retValue = -1;
   }

   pMsg->muiTimerId = aruiTimerId;
   return retValue;
}

int INGwIfrMgrWorkerThread::stopTimer(unsigned int& auiTimerId)
{
   int retValue = 0;
   mpTimerAdaptor->stopTimer(auiTimerId);
   return retValue;
}

void INGwIfrMgrWorkerThread::proxyRun(void)
{
   LogINGwTrace(false, 0, "IN proxyRun");
   logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Worker thread [%d] started",
                   MACRO_THREAD_ID());
   long llThreadId = getThreadId();
   char qname[100];
   static int liReplayQCount = 0;

   if(isReplayThread){
     logger.logINGwMsg(false,ALWAYS_FLAG,0,"proxyRun replay threadIndex <%d> id<%d>",
                       miThreadIdx, llThreadId);

     sprintf(qname, "WorkerQueue [%d] Replay-Queue[%d]", llThreadId, ++liReplayQCount);
   }
   else if(mbIsSipIOThr){
     logger.logINGwMsg(false,ALWAYS_FLAG,0,"proxyRun SipIO threadIndex <%d> id<%d>",
                       miThreadIdx, llThreadId);

     sprintf(qname, "WorkerQueue [%d] SipIO-Queue",llThreadId);
   }
   else{
     sprintf(qname, "WorkerQueue [%d] ", llThreadId);
   }
   mQueue.setName(qname);
   
#ifdef BIND_THREADS_TO_CPU
    bindToCPU();
#endif

   mpTimerAdaptor = new INGwIfrMgrTimer(*this, 
                        INGwIfrMgrManager::getInstance().getExtraScheduler());

   //mpTimerAdaptor = new INGwIfrMgrTimer(*this, NULL);
   int dequeCount = WRK_QUEUE_LOW_MARK;
   QueueData* pQueueData = new QueueData[dequeCount];
   list<INGwIfrMgrWorkUnit*>* pOutOfBandMsg = new list<INGwIfrMgrWorkUnit*>();
   list<INGwIfrMgrWorkUnit*>::iterator itOutOfBand;

   int sleepTime = 10;
   bool lIsMsgStoreEmpty = false;
   while(true == mRunStatus) 
   {
      //better to check emptyFlag one check only
      if(!lIsMsgStoreEmpty) 
      {
         if(isReplayThread)
        { 
          pthread_mutex_lock(&gCvLock);

            logger.logINGwMsg(false,ALWAYS_FLAG,0,"+Thread+ Id <%d> idx<%d>"
                              " Waiting <%d>", llThreadId,miThreadIdx);

            pthread_cond_wait(&gUnblock,&gCvLock);

            lIsMsgStoreEmpty = gIsMsgStoreEmpty();
            logger.logINGwMsg(false,ALWAYS_FLAG,0,"+Thread+ Id <%d> got signal"
            " msgStoreIsEmpty <%d> Indx<%d>", llThreadId, lIsMsgStoreEmpty,
            miThreadIdx);

          pthread_mutex_unlock(&gCvLock);
        }
      }
        
      int eventCount = mQueue.eventDequeueBlk(pQueueData, dequeCount, 
                                              sleepTime, true);

      if(eventCount == 0)
      {
         sleepTime = 0;
         dequeCount = 1;

         continue;
      }

      sleepTime = 10;
      dequeCount = WRK_QUEUE_LOW_MARK;
  
      for(int i = 0; i < eventCount; i++) 
      {
         INGwIfrMgrWorkUnit* pWork = static_cast<INGwIfrMgrWorkUnit*>(pQueueData[i].data);
         if(NULL != pWork) 
         {
            pWork->mpWorkerClbk->handleWorkerClbk(pWork);
            delete pWork;
 
            pQueueData[i].data = NULL;
         }
      }

      //
      //Processing of out of band messages
      //

      if(0 < miOutOfBandMsgCount) 
      {
         pthread_mutex_lock(&mOutOfBandMsgLock);
         if(0 < miOutOfBandMsgCount) 
         {
            list<INGwIfrMgrWorkUnit*>* pMsgTemp = mpOutOfBandMsgList;
            mpOutOfBandMsgList = pOutOfBandMsg;
            pOutOfBandMsg = pMsgTemp;
            pthread_mutex_lock(&mOutOfBandMsgLock);

            for(itOutOfBand = pOutOfBandMsg->begin(); 
                itOutOfBand != pOutOfBandMsg->end(); itOutOfBand++) 
            {
               INGwIfrMgrWorkUnit* pWork = *itOutOfBand;
               pWork->mpWorkerClbk->handleWorkerClbk(pWork);
               delete pWork;
            }

            pOutOfBandMsg->clear();
         }
         else 
         {
            pthread_mutex_lock(&mOutOfBandMsgLock);
         }
      }
   } 

   logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Worker thread [%d] ends", 
                   MACRO_THREAD_ID());
   LogINGwTrace(false, 0, "OUT proxyRun");
}

void INGwIfrMgrWorkerThread::setReplayThreadFlg(bool pVal) {
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "In setReplayThreadFlg <%s>",
                                           (pVal == true)?"True":"False"); 
    isReplayThread = pVal;
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Out setReplayThreadFlg"); 
}

void INGwIfrMgrWorkerThread::setSipIOThreadFlg(bool pVal) {
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "In setSipIOThreadFlg<%s>",
                                           (pVal == true)?"True":"False"); 
    mbIsSipIOThr = pVal;
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Out setSipIOThreadFlg"); 
}

void INGwIfrMgrWorkerThread::setThreadIdx(int aiIndex){
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "In setThreadIdx  <%d>",aiIndex);
    miThreadIdx = aiIndex;
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Out setThreadIdx"); 
}

int INGwIfrMgrWorkerThread::getThreadIdx() {
  logger.logINGwMsg(false, TRACE_FLAG, 0, "In getThreadIdx <%d>",miThreadIdx);  
  logger.logINGwMsg(false, TRACE_FLAG, 0, "Out getThreadIdx");
  return miThreadIdx;
}
// EOF INGwIfrMgrWorkerThread.C
