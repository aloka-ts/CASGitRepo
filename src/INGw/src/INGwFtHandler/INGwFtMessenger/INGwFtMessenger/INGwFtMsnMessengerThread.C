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
//     File:     INGwFtMsnMessengerThread.h
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtMessenger");

#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>

#include <INGwFtMessenger/INGwFtMsnMessengerThread.h>

INGwFtMsnMessengerThread::INGwFtMsnMessengerThread() :
mbRunStatus(false),
mSelfDestruct(false),
msgQueue(true, 2048, 1024)
{
   msgQueue.setName("INGW_MessengerQueue");
}

INGwFtMsnMessengerThread::~INGwFtMsnMessengerThread()
{
   LogINGwTrace(false, 0, "INGwFtMsnMessengerThread::~INGwFtMsnMessengerThread");
   stop(true);
}

extern "C" void* INGwFtMsnMessengerThread::_messengerStart(void* apArg)
{
   LogAlways(0, "INGwFtMsnMessengerThread::_messengerStart");

   sigignore(SIGCHLD);
   sigignore(SIGPIPE);
   sigignore(SIGALRM);

   INGwFtMsnMessengerThread *pThis = (INGwFtMsnMessengerThread*)apArg;
   pThis->execute();

   pThis->stop(false);

   if(pThis->mSelfDestruct)
   {
      LogAlways(0, "INGwFtMsnMessengerThread self destruction.");
      pthread_detach(pthread_self());
      sleep(2);
      delete pThis;
   }

   LogAlways(0, "INGwFtMsnMessengerThread end of thread.");
   return NULL;
}

int INGwFtMsnMessengerThread::start(void)
{
   if(true == mbRunStatus)
   {
      return -1;
   }

   mbRunStatus = true;

   if(pthread_create(&mThreadId, NULL, _messengerStart, this) != 0) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Thread creation failed [%s]", 
                      strerror(errno));
      mbRunStatus = false;
      return -1;
   } 
   
   logger.logINGwMsg(false, ALWAYS_FLAG, 0, "INGwFtMsnMessengerThread created."); 
   return 0;
}

void INGwFtMsnMessengerThread::stop(bool abStop)
{
   LogINGwTrace(false, 0, "INGwFtMsnMessengerThread::stop");

   mbRunStatus = false;
   msgQueue.stopQueue();

   if(true == abStop)
   {
      if(mSelfDestruct)
      {
         return;
      }

      pthread_join(mThreadId, NULL);
   } 

   return;
}

void INGwFtMsnMessengerThread::end()
{
   LogINGwTrace(false, 0, "INGwFtMsnMessengerThread::end");

   if(mbRunStatus == false)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Cant end an stopped Thread.");
      return;
   }

   mSelfDestruct = true;
   stop(false);
}

