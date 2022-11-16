#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwInfraMsrMgr");

//////////////////////////////////////////////////////////////////////////
//
//        Copyright (c) 2000, Bay Packets Inc.
//        All rights reserved.
//
//        FILE_NAME: MsrTimer.C
//
//////////////////////////////////////////////////////////////////////////

using namespace std;

#include "INGwInfraMsrMgr/MsrIncludes.h"
#include "INGwInfraMsrMgr/MsrWorkerThread.h"
#include "INGwInfraMsrMgr/MsrTimer.h"

MsrTimer::MsrTimer(MsrWorkerThread& arWorker, CoreScheduler *apSche) : 
mrWorkerThread(arWorker) 
{
   mpMsgList = NULL;
   extraScheduler = addScheduler(apSche);
}

MsrTimer::~MsrTimer() 
{
   detachSchedulers();

   MsrTimerMsg* pCurrMsg = mpMsgList;
   while(NULL != pCurrMsg) 
   {
      MsrTimerMsg* pNextMsg = pCurrMsg->mpNextMsg;
      delete pCurrMsg;
      pCurrMsg = pNextMsg;
   }
   mpMsgList = NULL;
}

void MsrTimer::ProcessGarbageMessages(SchedulerMsg* apMsg)
{
   ProcessMessages(apMsg);
}

void MsrTimer::ProcessMessages(SchedulerMsg* apMsg)
{
   LogINGwTrace(false, 0, "IN ProcessMessages");

   SchedulerMsg* pCurrSMsg = apMsg;
   SchedulerMsg* pNextSMsg = NULL;

   while(NULL != pCurrSMsg) 
   {
      pNextSMsg = pCurrSMsg->getNextMsg();
      MsrTimerMsg* pWork = dynamic_cast<MsrTimerMsg*>(pCurrSMsg);
      if(NULL == pWork) 
      {
         logger.logINGwMsg(false, ERROR_FLAG, 0, 
           "WorkUnit ptr is NULL in MsrTimer::ProcessMessages()");
      }
      else 
      {
         mrWorkerThread.postMsg(pWork);
      }

      pCurrSMsg = pNextSMsg;
   }

   LogINGwTrace(false, 0, "OUT ProcessMessages");
}

bool MsrTimer::startTimer(u_int auiDuration, MsrTimerMsg* apMsg, 
                            unsigned int& aruiTimerId)
{
   if(auiDuration > 100000000) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
        "Invalid timer [%ld] duration (> 100000000)", auiDuration);
      return false;
   }

   bool retValue = true;
   if(NULL == apMsg) 
   {
      retValue = false;
   }
   else 
   {
      aruiTimerId = reinterpret_cast<unsigned int>(apMsg);

      if(auiDuration < 60000)
      {
         retValue = startTimerEvent(apMsg, auiDuration, extraScheduler);
      }
      else
      {
         retValue = startTimerEvent(apMsg, auiDuration);
      }
   }

   return retValue;
}

bool MsrTimer::stopTimer(unsigned int auiTimerId)
{
   LogINGwTrace(false, 0, "IN stopTimer");

   bool retValue = true;
   if(0 == auiTimerId) 
   {
      retValue = false;
   }
   else 
   {
      MsrTimerMsg* pMsg = reinterpret_cast<MsrTimerMsg*>(auiTimerId);
      retValue = stopTimerEvent(pMsg);
   }
   LogINGwTrace(false, 0, "OUT stopTimer");
   return retValue;
}

MsrTimer::MsrTimerMsg* MsrTimer::getMessage(void)
{
   MsrTimerMsg* pMsg = mpMsgList;

   if(NULL != pMsg) 
   {
      mpMsgList = mpMsgList->mpNextMsg;
   }
   else 
   {
      pMsg = new MsrTimerMsg;
      pMsg->mbIsHBeat = false;
   }
   return pMsg;
}

void MsrTimer::putMessage(MsrTimerMsg* apMsg)
{
   if(NULL == apMsg) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Msg ptr is NULL in MsrTimer::putMessage()"
                                   " for thread Id %d", MACRO_THREAD_ID());
      return ;
   }

   apMsg->mpNextMsg = mpMsgList;
   mpMsgList        = apMsg;
}

// EOF MsrTimer.C
