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
//     File:     INGwIfrMgrThreadMgr.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal   27/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraManager");

#include <assert.h>

using namespace std;

#include "INGwInfraUtil/INGwIfrUtlMacro.h"
#include "INGwInfraParamRepository/INGwIfrPrIncludes.h"


#include <INGwInfraManager/INGwIfrMgrWorkUnit.h>
#include "INGwInfraManager/INGwIfrMgrWorkerThread.h"
#include "INGwInfraParamRepository/INGwIfrPrParamRepository.h"
#include <INGwInfraManager/INGwIfrMgrWorkerClbkIntf.h>
#include <INGwInfraManager/INGwIfrMgrThreadMgr.h>

#define WORKUNIT_BULK_COUNT 50

const int   ingwWORKER_THREAD_COUNT__DEF = 8;
const int   ingwWORKER_THREAD_COUNT__MAX = 64;
const int   ingwWORKER_THREAD_COUNT__MIN = 1;
const int   ingwREPLAY_THREAD_COUNT__MAX = 10;
const int   ingwREPLAY_THREAD_COUNT__MIN = 1;
const int   ingwREPLAY_THREAD_COUNT      = 2;

//threads are arranged as 
//|<---WORKER-THREADS--->|<--Sip-IO-THREAD-->|<--HOUSEKEEPING-THREAD-->|<--REPLAY-THREADS-->|

INGwIfrMgrThreadMgr* INGwIfrMgrThreadMgr::mpSelf = NULL;

INGwIfrMgrThreadMgr& INGwIfrMgrThreadMgr::getInstance()
{
   if(NULL == mpSelf) 
   {
      mpSelf = new INGwIfrMgrThreadMgr();
   }

   return *mpSelf;
}

INGwIfrMgrThreadMgr::INGwIfrMgrThreadMgr() : 
   mlWorkerThreadCount(ingwWORKER_THREAD_COUNT__DEF),
   mlThreadCount(ingwWORKER_THREAD_COUNT__DEF + 1),
   mplThreadId(NULL),
   mpThreadArray(NULL)
{
   LogINGwTrace(false, 0, "IN INGwIfrMgrThreadMgr()");
   mbIsInitialized = false;
   LogINGwTrace(false, 0, "OUT INGwIfrMgrThreadMgr()");
}

INGwIfrMgrThreadMgr::~INGwIfrMgrThreadMgr()
{
   LogINGwTrace(false, 0, "IN ~INGwIfrMgrThreadMgr()");

   list<pthread_key_t*>::iterator it = mKeyList.begin();

   for( ; it != mKeyList.end(); it++) 
   {
      pthread_key_delete(**it);
   }

   mKeyList.clear();
   pthread_mutex_destroy(&mKeyListMutex);
   LogINGwTrace(false, 0, "OUT ~INGwIfrMgrThreadMgr()");
}

int INGwIfrMgrThreadMgr::initialize(void)
{
   LogINGwTrace(false, 0, "IN initialize");

   if(mpThreadArray != NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Threadmgr multiple initialization.");
      LogINGwTrace(false, 0, "OUT initialize");
      return 0;
   }

   int retValue = 0;

   INGwIfrPrParamRepository& paramRep = INGwIfrPrParamRepository::getInstance();
   try 
   {
			//PANKAJ - Which worker thd count to be used? Using TCAP now

      string strWorkerThreadCount = paramRep.getValue(ingwTCAP_WORKER_THREAD_COUNT);
      configure(ingwTCAP_WORKER_THREAD_COUNT, strWorkerThreadCount.c_str());
   }
   catch(...) 
   {
      logger.logMsg(WARNING_FLAG, 0, "Unable to get the value for CG OID %s", 
                    ingwTCAP_WORKER_THREAD_COUNT);
   }

   int errCode = pthread_mutex_init(&mKeyListMutex, NULL);

   if(0 != errCode) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_RESOURCE_FAILED, 
                      "Error : pthread_mutex_init() returned %d", errCode);
      retValue = -1;
   }
   else 
   {
      mlReplayThreadIndex = mlWorkerThreadCount+1;
      mpThreadArray = new INGwIfrMgrWorkerThread[mlThreadCount];

      INGwIfrMgrWorkerThread **workers = new INGwIfrMgrWorkerThread *[mlThreadCount];

      for(int idx = 0; idx < mlThreadCount; idx++) 
      {
         workers[idx] = mpThreadArray + idx;
         (mpThreadArray[idx]).initialise();
         (mpThreadArray[idx]).setThreadIdx(idx);
         if(idx >= mlReplayThreadIndex)
         {
           (mpThreadArray[idx]).setReplayThreadFlg(true);

           logger.logINGwMsg(false,ALWAYS_FLAG,0,
           "replay thread index<%d> id<%d>",idx);
         }
      }

      setWorkerTSD("SelfThread", (void **) workers, &mSelfKey);
      delete []workers;
   }

   mlHKThreadIndex     = mlWorkerThreadCount;
   mlReplayThreadIndex = mlWorkerThreadCount+1;
   mlSipReqThread      = --mlWorkerThreadCount;

  (mpThreadArray[mlSipReqThread]).setSipIOThreadFlg(true);
   
   mbIsInitialized = true;

   logger.logMsg(ALWAYS_FLAG, 0, "WORKER THRD CNT:<%d>", mlWorkerThreadCount);

   LogINGwTrace(false, 0, "OUT initialize");
   return retValue;
}

INGwIfrMgrWorkerThread & INGwIfrMgrThreadMgr::getCurrentThread()
{
   return *((INGwIfrMgrWorkerThread *)pthread_getspecific(mSelfKey));
}

int INGwIfrMgrThreadMgr::shutdown(void)
{
   LogINGwTrace(false, 0, "IN shutdown");

   int retValue = 0;

   for(int i = 0; i < mlThreadCount; i++) 
   {
      (mpThreadArray[i]).stop();
   }

   delete [] mpThreadArray;

   LogINGwTrace(false, 0, "OUT shutdown");
   return retValue;
}

int INGwIfrMgrThreadMgr::configure(const char* apcOID, const char* apcValue)
{
   LogINGwTrace(false, 0, "IN configure");
   int retValue = 0;

   if(true == mbIsInitialized) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_RESOURCE_FAILED, 
                      "Runtime updation of OID [%s] not supported", apcOID);
      retValue = -1;
      return retValue;
   }

   if(0 == strcmp(ingwTCAP_WORKER_THREAD_COUNT,  apcOID)) 
   {
      long value = strtol(apcValue, NULL, 10);
      if((ingwWORKER_THREAD_COUNT__MIN <= value) && 
         (ingwWORKER_THREAD_COUNT__MAX >= value)) 
      {
         long tempValue = mlWorkerThreadCount;
         retValue = 0;
         logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
                         "Parameter Worker thread count updated from %d to %d", 
                         tempValue, value);
         mlWorkerThreadCount = value;
      }
      else 
      {
         logger.logINGwMsg(false, ERROR_FLAG, imERR_RESOURCE_FAILED, 
                         "Invalid new value for parameter Worker thread count "
                         "%d (Max %d, Min %d)", value, 
                         ingwWORKER_THREAD_COUNT__MAX, 
                         ingwWORKER_THREAD_COUNT__MIN);
#ifdef sun4Sol28
         mlWorkerThreadCount = sysconf(_SC_NPROCESSORS_ONLN);

         if(mlWorkerThreadCount > ingwWORKER_THREAD_COUNT__MAX)
         {
            mlWorkerThreadCount = ingwWORKER_THREAD_COUNT__MAX;
         }
#else
#endif
         retValue = -1;
      }
   }

   
   char* lpcReplayThreadCnt = getenv("INGW_REPLAY_THREAD_COUNT");
   if(NULL != lpcReplayThreadCnt){
     mlReplayThreadCount = strtol(lpcReplayThreadCnt,NULL, 10);
     if(!(mlReplayThreadCount <= ingwREPLAY_THREAD_COUNT__MAX &&
        mlReplayThreadCount >= ingwREPLAY_THREAD_COUNT__MIN)) {
     }
   }
   else{
     mlReplayThreadCount = ingwREPLAY_THREAD_COUNT__MIN;
   }

   mlThreadCount = mlWorkerThreadCount + 1 + mlReplayThreadCount;
   LogINGwTrace(false, 0, "OUT config");
   return retValue;
}

int INGwIfrMgrThreadMgr::startWorkerThread(void)
{
   LogINGwTrace(false, 0, "IN startWorkerThread");

   int retValue = 0;
   mplThreadId = new long[mlThreadCount];

   for(int i = 0; i < mlThreadCount; i++) 
   {
      (mpThreadArray[i]).start(i);
      (mpThreadArray[i]).start();
      mplThreadId[i] = (mpThreadArray[i]).getThreadId();

      logger.logINGwMsg(false,ALWAYS_FLAG,0,
           "startWorkerThread Tid <%d> ",mplThreadId[i]);
   }

   LogINGwTrace(false, 0, "OUT startWorkerThread");
   return retValue;
}

int INGwIfrMgrThreadMgr::stopWorkerThread(bool abIsGraceful)
{
   LogINGwTrace(false, 0, "IN stopWorkerThread");

   int retValue = 0;

   for(int i = 0; i < mlThreadCount; i++) 
   {
      (mpThreadArray[i]).stop();
   }

   LogINGwTrace(false, 0, "OUT stopWorkerThread");
   return retValue;
}

int INGwIfrMgrThreadMgr::setWorkerTSD(const char* apcKeyStr, void** apData, 
                                 pthread_key_t* apKey) 
{
   LogINGwTrace(false, 0, "IN setWorkerTSD");

   int retResult = 0;
   int errCode = pthread_key_create(apKey, NULL);
   
   logger.logINGwMsg(false,ALWAYS_FLAG,0,"setWorkerTSD key <%d>",*apKey);

   if(0 != errCode) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, imERR_RESOURCE_FAILED, 
                      "Error : pthread_key_create() returned %d CANNOT set the "
                      "TSD for %s", errCode, apcKeyStr);
      retResult = -1;
   }
   else 
   {
      for(int idx = 0; idx < getThreadCount(); idx++) 
      {
         INGwIfrMgrWorkerThread& thread = mpThreadArray[idx]; 
         INGwIfrMgrWorkUnit* pWork    = new INGwIfrMgrWorkUnit;
         pWork->meWorkType    = INGwIfrMgrWorkUnit::UPDATE_TSD_MSG;
         pWork->mpMsg         = apData[idx];
         pWork->mpContextData = static_cast<void*>(apKey);
         pWork->mpWorkerClbk  = &thread;
         thread.postMsg(pWork);
      }

      pthread_mutex_lock(&mKeyListMutex);
      mKeyList.push_back(apKey);
      pthread_mutex_unlock(&mKeyListMutex);
   }
    
   LogINGwTrace(false, 0, "OUT setWorkerTSD");
   return retResult;
}

int INGwIfrMgrThreadMgr::startTimer(int aiDuration, void* apContext,
                               INGwIfrMgrWorkerClbkIntf* apClbk, 
                               unsigned int& auiTimerId)
{
   int retValue = -1;
   int threadId = MACRO_THREAD_ID();

   for(int idx = 0; idx < getThreadCount(); idx++) 
   {
      INGwIfrMgrWorkerThread& thread = mpThreadArray[idx];

      if(threadId == thread.getThreadId()) 
      {
         retValue = thread.startTimer(aiDuration, apContext, apClbk, 
                                      auiTimerId);
         break;
      }
   }

   return retValue;
}

int INGwIfrMgrThreadMgr::stopTimer(unsigned int& auiTimerId, int aiThreadId)
{
   int retValue = -1;
	 int threadId = 0;
	 if(-1 == aiThreadId)
	 {
     threadId = MACRO_THREAD_ID();
	 }
	 else
	 {
     threadId = aiThreadId;
	 }

   for(int idx = 0; idx < getThreadCount(); idx++) 
   {
      INGwIfrMgrWorkerThread& thread= mpThreadArray[idx];

      if(threadId == thread.getThreadId()) 
      {
         retValue = thread.stopTimer(auiTimerId);
         break;
      }
   }

   return retValue;
}

bool INGwIfrMgrThreadMgr::postMsg(INGwIfrMgrWorkUnit* apWork, bool chkFlag)
{
   int thdIdx = _getTargetThread(apWork->mlHashId);
   apWork->setWorkerThreadIdx(thdIdx);

   logger.logINGwMsg(false,TRACE_FLAG,0,
                     "+rem+ INGwIfrMgrThreadMgr::postMsg tIdx<%d>",thdIdx);

#ifdef INGW_TRACE_CALL_THREAD
   char* lpcStr = (NULL == apWork->mpcCallId)?"<INFO - NULL>":apWork->mpcCallId;
   logger.logINGwMsg(false,TRACE_FLAG,0,"INGwIfrMgrThreadMgr::postMsg "
                                        "+THREAD+ thdIdx<%d> callId<%s>",
                                         thdIdx, lpcStr);
#endif

   return (mpThreadArray[thdIdx]).postMsg(apWork, chkFlag);
}


//called to post message from sip listener
bool INGwIfrMgrThreadMgr::postMsg(INGwIfrMgrWorkUnit* apWork, int aiThreadIdx, bool chkFlag)
{
   if(-1 != aiThreadIdx)
   return (mpThreadArray[aiThreadIdx]).postMsg(apWork, chkFlag);
   else
   return postMsg(apWork);
}

void INGwIfrMgrThreadMgr::postBulkMsg(INGwIfrMgrWorkUnit **apWorkList, int count)
{
   INGwIfrMgrWorkUnit *sortedUnits[ingwWORKER_THREAD_COUNT__MAX][WORKUNIT_BULK_COUNT];
   int workCount[ingwWORKER_THREAD_COUNT__MAX];

   for(int idx = 0; idx < ingwWORKER_THREAD_COUNT__MAX; idx++)
   {
      workCount[idx] = 0;
   }

   for(int idx = 0; idx < count; idx++)
   {
      INGwIfrMgrWorkUnit *apWork = apWorkList[idx];

      int thdIdx = _getTargetThread(apWork->mlHashId);

      sortedUnits[thdIdx][workCount[thdIdx]] = apWork;
      workCount[thdIdx]++;

      if(workCount[thdIdx] == WORKUNIT_BULK_COUNT)
      {
         mpThreadArray[thdIdx].postBulkMsg(sortedUnits[thdIdx], 
                                           workCount[thdIdx]);
         workCount[thdIdx] = 0;
      }
   }

   for(int idx = 0; idx < ingwWORKER_THREAD_COUNT__MAX; idx++)
   {
      if(workCount[idx] == 0)
      {
         continue;
      }

      if(workCount[idx] == 1)
      {
         mpThreadArray[idx].postMsg(sortedUnits[idx][0]);
      }
      else
      {
         mpThreadArray[idx].postBulkMsg(sortedUnits[idx], workCount[idx]);
         workCount[idx] = 0;
      }
   }

   return;
}

bool INGwIfrMgrThreadMgr::postMsgForHK(INGwIfrMgrWorkUnit* apWork, bool chkFlag)
{
   apWork->setWorkerThreadIdx(mlHKThreadIndex);

   logger.logINGwMsg(false,TRACE_FLAG,0,
                     "+rem+ INGwIfrMgrThreadMgr::postMsg mlHKThreadIndex<%d>",mlHKThreadIndex);

   return (mpThreadArray[mlHKThreadIndex]).postMsg(apWork, chkFlag);
}

bool INGwIfrMgrThreadMgr::postMsgForReplay(INGwIfrMgrWorkUnit* apWork, 
                                           int pDlgId, bool chkFlag)
{
   int replayThreadindex =   
                     ((pDlgId %97)%(mlReplayThreadCount)) + mlReplayThreadIndex;

   //set worker thread index make it a part of call Id
   apWork->setWorkerThreadIdx(replayThreadindex);

   logger.logINGwMsg(false,TRACE_FLAG,0,
                     "+rem+ INGwIfrMgrThreadMgr::postMsgForReplay" 
                     "replayThreadindex<%d>",replayThreadindex);

#ifdef INGW_TRACE_CALL_THREAD
   logger.logINGwMsg(false,TRACE_FLAG,0,"INGwIfrMgrThreadMgr::postMsg "
                                        "+THREAD+ thdIdx<%d> <%d>",
                                         replayThreadindex, pDlgId);
#endif

   return (mpThreadArray[replayThreadindex]).postMsg(apWork, chkFlag);
}

bool INGwIfrMgrThreadMgr::postMsgForSipReq(INGwIfrMgrWorkUnit* apWork, 
                                           bool chkFlag)
{
 return (mpThreadArray[mlSipReqThread]).postMsg(apWork, chkFlag);
}

bool INGwIfrMgrThreadMgr::postMsgForTakeOverCalls(INGwIfrMgrWorkUnit* apWork,
                                                  int pDlgId, bool chkFlag)
{
   int replayThreadindex =   
                     ((pDlgId%97) % (mlReplayThreadCount)) + mlReplayThreadIndex;

   apWork->setWorkerThreadIdx(replayThreadindex);
   logger.logINGwMsg(false,VERBOSE_FLAG,0,"+rem+ replayThreadindex <%d>",
                                          replayThreadindex);  
#ifdef INGW_TRACE_CALL_THREAD
   logger.logINGwMsg(false,TRACE_FLAG,0,
                     "INGwIfrMgrThreadMgr::postMsgForTakeOverCalls"
                     "+THREAD+ thdIdx<%d> <%d>", replayThreadindex, pDlgId);
#endif


   return (mpThreadArray[replayThreadindex]).postMsg(apWork, chkFlag);
}

void INGwIfrMgrThreadMgr::informMsg(INGwIfrMgrWorkUnit **apWork)
{
   for(int idx = 0; idx < mlWorkerThreadCount; idx++)
   {
      mpThreadArray[idx].postMsg(apWork[idx]);
   }
}

void INGwIfrMgrThreadMgr::postOutOfBandMsg(INGwIfrMgrWorkUnit* apWork)
{
   int thdIdx = (apWork->mlHashId)%mlWorkerThreadCount;
   (mpThreadArray[thdIdx]).postOutOfBandMsg(apWork);
}

void INGwIfrMgrThreadMgr::postOutOfBandMsgForHK(INGwIfrMgrWorkUnit* apWork)
{
   (mpThreadArray[mlHKThreadIndex]).postOutOfBandMsg(apWork);
}

void INGwIfrMgrThreadMgr::printStats(void)
{
}

int 
INGwIfrMgrThreadMgr::getReplayThreadIdx(int pDlgId){
  int replayThreadindex =   
    ((pDlgId%97) % (mlReplayThreadCount)) + mlReplayThreadIndex;
  return replayThreadindex;
}

INGwIfrMgrWorkerThread& INGwIfrMgrThreadMgr::getThreadSpecificData(){
  void* l_voidTsd = pthread_getspecific(mSelfKey);
  INGwIfrMgrWorkerThread* l_tsd =
  static_cast<INGwIfrMgrWorkerThread*>(l_voidTsd);
  return *l_tsd;
}

// EOF INGwIfrMgrThreadMgr.C
