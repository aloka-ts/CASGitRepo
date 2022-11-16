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
//     File:     INGwIfrMgrTimer.C
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

#include "INGwInfraParamRepository/INGwIfrPrIncludes.h"
#include "INGwInfraManager/INGwIfrMgrWorkerThread.h"
#include "INGwInfraManager/INGwIfrMgrTimer.h"
#include "INGwInfraUtil/INGwIfrUtlMacro.h"

INGwIfrMgrTimer::INGwIfrMgrTimer(INGwIfrMgrWorkerThread& arWorker, CoreScheduler *apSche) : 
mrWorkerThread(arWorker) 
{
   mpMsgList = NULL;
   _msgListCount = 0;
   _allocObjCount = 0;
   extraScheduler = addScheduler(apSche);
}

INGwIfrMgrTimer::~INGwIfrMgrTimer() 
{
   detachSchedulers();

   INGwIfrMgrTimerMsg* pCurrMsg = mpMsgList;
   while(NULL != pCurrMsg) 
   {
      INGwIfrMgrTimerMsg* pNextMsg = pCurrMsg->mpNextMsg;
      delete pCurrMsg;
      pCurrMsg = pNextMsg;
   }
   mpMsgList = NULL;
}

void INGwIfrMgrTimer::ProcessGarbageMessages(SchedulerMsg* apMsg)
{
   ProcessMessages(apMsg);
}

int INGwIfrMgrTimer::handleWorkerClbk(INGwIfrMgrWorkUnit *inTimerWorkUnit)
{
   SchedulerMsg *apMsg = (SchedulerMsg *)inTimerWorkUnit->mpMsg;

   LogINGwTrace(false, 0, "IN handleWorkerClbk");

   SchedulerMsg* pCurrSMsg = apMsg;
   SchedulerMsg* pNextSMsg = NULL;

   int count = 0;

   while(NULL != pCurrSMsg) 
   {
      count++;
      pNextSMsg = pCurrSMsg->getNextMsg();
      INGwIfrMgrWorkUnit* pWork = dynamic_cast<INGwIfrMgrWorkUnit*>(pCurrSMsg);

      if(NULL == pWork) 
      {
         logger.logINGwMsg(false, ERROR_FLAG, 0, "WorkUnit ptr is NULL");
      }
      else 
      {
         pWork->mpWorkerClbk->handleWorkerClbk(pWork);

         if(pWork->mpcCallId)
         {
            delete []pWork->mpcCallId;
            pWork->mpcCallId = NULL;
         }

         putMessage(dynamic_cast<INGwIfrMgrTimer::INGwIfrMgrTimerMsg*>(pWork));
      }

      pCurrSMsg = pNextSMsg;
   }

   logger.logMsg(TRACE_FLAG, 0, "Processed [%d] TimerMsg", count);

   LogINGwTrace(false, 0, "OUT handleWorkerClbk");
   return 0;
}

void INGwIfrMgrTimer::ProcessMessages(SchedulerMsg* apMsg)
{
   INGwIfrMgrWorkUnit* pWork = new INGwIfrMgrWorkUnit();
   pWork->mpWorkerClbk = this;
   pWork->meWorkType = INGwIfrMgrWorkUnit::TIMER_SELF_MSG;
   pWork->mpMsg = static_cast<void*>(apMsg);
   mrWorkerThread.postMsg(pWork);
}

bool INGwIfrMgrTimer::startTimer(u_int auiDuration, INGwIfrMgrTimerMsg* apMsg, 
                            unsigned int& aruiTimerId)
{
   LogINGwTrace(false, 0, "IN startTimer");

   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "Starting a timer for [%ld] duration", 
                 auiDuration);
   if(auiDuration > 100000000) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Invalid timer [%ld] duration (> 100000000)",
                    auiDuration);
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

   logger.logINGwMsg(false, TRACE_FLAG, 0, 
                   "OUT startTimer [%x] [%d]", aruiTimerId, retValue);
   return retValue;
}

bool INGwIfrMgrTimer::stopTimer(unsigned int &auiTimerId)
{
   LogINGwTrace(false, 0, "IN stopTimer");

   bool retValue = true;
   if(0 == auiTimerId) 
   {
      retValue = false;
   }
   else 
   {
      INGwIfrMgrTimerMsg* pMsg = reinterpret_cast<INGwIfrMgrTimerMsg*>(auiTimerId);
      retValue = stopTimerEvent(pMsg);
   }
   logger.logINGwMsg(false, TRACE_FLAG, 0, 
                   "OUT stopTimer [%x] [%d]", auiTimerId, retValue);
   auiTimerId = 0;
   return retValue;
}

INGwIfrMgrTimer::INGwIfrMgrTimerMsg* INGwIfrMgrTimer::getMessage(void)
{
   INGwIfrMgrTimerMsg* pMsg = mpMsgList;

   if(NULL != pMsg) 
   {
      mpMsgList = mpMsgList->mpNextMsg;
      --_msgListCount;
   }
   else 
   {
      pMsg = new INGwIfrMgrTimerMsg;
      pMsg->meWorkType = INGwIfrMgrWorkUnit::TIMER_MSG;
      ++_allocObjCount;
   }

   pMsg->mpNextMsg = NULL;

   logger.logINGwMsg(false, TRACE_FLAG, 0, 
                   "Giving out [%x]", pMsg);
   return pMsg;
}

void INGwIfrMgrTimer::putMessage(INGwIfrMgrTimerMsg* apMsg)
{
   if(NULL == apMsg) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Msg ptr is NULL in INGwIfrMgrTimer::putMessage()"
                                   " for thread Id %d", MACRO_THREAD_ID());
      return ;
   }

   logger.logINGwMsg(false, TRACE_FLAG, 0, 
                   "Adding to list [%x]", apMsg);

   apMsg->mpContextData = NULL;
   apMsg->mpWorkerClbk = NULL;
   apMsg->mpNextMsg = mpMsgList;
   mpMsgList        = apMsg;
   ++_msgListCount;
}

std::string INGwIfrMgrTimer::toLog(int tabCount) const
{
   char tabs[20];

   for(int idx = 0; idx < tabCount; idx++)
   {
      tabs[idx] = '\t';
   }

   tabs[tabCount] = '\0';

   std::string ret = tabs;
   ret += "CCMTimer";

   char id[50];
   sprintf(id, "[%d]\n", mrWorkerThread.getThreadId());
   ret += id;

   char data[1000];
   sprintf(data, "%s\tAllocObj[%d] FreeObj[%d]\n", 
           tabs, _allocObjCount, _msgListCount);

   ret += data;

   ret += tabs;
   ret += "-CCMTimer";
   ret += id;

   return ret;
}

// EOF INGwIfrMgrTimer.C
