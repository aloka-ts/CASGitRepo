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
//     File:     INGwFtMsnCommMgr.h
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     pankaj Bathwal  26/11/07     Initial Creation
//********************************************************************

#ifndef INGW_FT_MSN_COMM_MGR_H_
#define INGW_FT_MSN_COMM_MGR_H_

#include <pthread.h>

class INGwIfrMgrFtIface;

class INGwFtMsnMessenger;
class INGwFtMsnPktDispatcher;
class INGwFtMsnPktReceiver;
class INGwFtPktMsg;

#define MAX_INGW_DISPATCHER 10

class INGwFtMsnCommMgr 
{
   private:

      class INGwFtMsnPktDispatcherHolder
      {
         public:

            int id;
            INGwFtMsnPktDispatcher *dispatcher;

         public:

            INGwFtMsnPktDispatcherHolder()
            {
               id = 0;
               dispatcher = NULL;
            }
      };

   private:

      INGwFtMsnMessenger   *_ingwMessenger;
      INGwIfrMgrFtIface *_ingwInf;
   
      INGwFtMsnPktReceiver           *_receiver;
      INGwFtMsnPktDispatcherHolder   _dispatchers[MAX_INGW_DISPATCHER];
      pthread_mutex_t          _dispatcherLock;

   public:

      INGwFtMsnCommMgr(INGwFtMsnMessenger *messenger, INGwIfrMgrFtIface *ingwInf);
      ~INGwFtMsnCommMgr();

      int add(int ingwId);
      int remove(int ingwId);

      void sendMsg(INGwFtPktMsg *msg);
      int shutdown();
};

#endif
