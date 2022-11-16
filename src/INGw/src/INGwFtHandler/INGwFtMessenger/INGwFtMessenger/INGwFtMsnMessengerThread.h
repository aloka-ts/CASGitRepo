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

#ifndef INGW_FT_MSN_MESSENGER_THREAD_H_
#define INGW_FT_MSN_MESSENGER_THREAD_H_

#include <pthread.h>
#include <Util/QueueMgr.h>

class INGwFtMsnMessengerThread
{
   private:

      pthread_t mThreadId;

      volatile bool mSelfDestruct;
      volatile bool mbRunStatus;

   public:

      INGwFtMsnMessengerThread(void);
      virtual ~INGwFtMsnMessengerThread();

      QueueManager msgQueue;

      void stop(bool abStop = false);
      void end();
      int start(void);

   protected:

      virtual void execute(void) = 0;
      inline bool isRunnable()
      {
         return mbRunStatus;
      }

   private:

      static void* _messengerStart(void* apArg);

      INGwFtMsnMessengerThread(const INGwFtMsnMessengerThread& arconRecv);
      INGwFtMsnMessengerThread& operator=(const INGwFtMsnMessengerThread& arconRecv);
};

#endif
