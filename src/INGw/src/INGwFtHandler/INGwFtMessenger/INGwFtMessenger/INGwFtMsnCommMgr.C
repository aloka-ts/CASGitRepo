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

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtMessenger");

#include <INGwFtMessenger/INGwFtMsnCommMgr.h>
#include <INGwFtMessenger/INGwFtMsnPktDispatcher.h>
#include <INGwFtMessenger/INGwFtMsnPktReceiver.h>

#include <INGwFtPacket/INGwFtPktMsg.h>

INGwFtMsnCommMgr::INGwFtMsnCommMgr(INGwFtMsnMessenger *messenger, 
																	 INGwIfrMgrFtIface *ingwInf)
{
   _ingwMessenger = messenger;
   _ingwInf = ingwInf;
   pthread_mutex_init(&_dispatcherLock, NULL);

   _receiver = new INGwFtMsnPktReceiver(this, _ingwInf);
}

INGwFtMsnCommMgr::~INGwFtMsnCommMgr()
{
   delete _receiver;

   for(int idx = 0; idx < MAX_INGW_DISPATCHER; idx++)
   {
      if(_dispatchers[idx].dispatcher != NULL)
      {
         delete _dispatchers[idx].dispatcher;
         _dispatchers[idx].dispatcher = NULL;
      }
   }

   pthread_mutex_destroy(&_dispatcherLock);
}

int INGwFtMsnCommMgr::add(int ingwId)
{
   INGwFtMsnPktDispatcher *currDispatcher = NULL;

   pthread_mutex_lock(&_dispatcherLock);

   for(int idx = 0; idx < MAX_INGW_DISPATCHER; idx++)
   {
      if(ingwId == _dispatchers[idx].id)
      {
         currDispatcher = _dispatchers[idx].dispatcher;
         break;
      }
   }

   if(currDispatcher == NULL)
   {
      currDispatcher = new INGwFtMsnPktDispatcher(this, _ingwInf, ingwId);

      bool status = false;

      for(int idx = 0; idx < MAX_INGW_DISPATCHER; idx++)
      {
         if((_dispatchers[idx].id == ingwId) || (_dispatchers[idx].id == 0))
         {
            _dispatchers[idx].id = ingwId;
            _dispatchers[idx].dispatcher = currDispatcher;
            status = true;
            break;
         }
      }

      if(status == false)
      {
         pthread_mutex_unlock(&_dispatcherLock);
         delete currDispatcher;
         logger.logINGwMsg(false, ERROR_FLAG, 0,
                         "No holder to add dispatcher [%d].", ingwId);
         return -1;
      }
   }
   else
   {
      pthread_mutex_unlock(&_dispatcherLock);
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Dispatcher already exist for [%d]",
                      ingwId);
      return -1;
   }

   pthread_mutex_unlock(&_dispatcherLock);


   currDispatcher->initConnection();
   return 0;
}

int INGwFtMsnCommMgr::remove(int ingwId)
{
   INGwFtMsnPktDispatcher *currDispatcher = NULL;

   pthread_mutex_lock(&_dispatcherLock);

   for(int idx = 0; idx < MAX_INGW_DISPATCHER; idx++)
   {
      if(_dispatchers[idx].id == ingwId)
      {
         currDispatcher = _dispatchers[idx].dispatcher;
         _dispatchers[idx].dispatcher = NULL;
      }
   }

   pthread_mutex_unlock(&_dispatcherLock);

   if(currDispatcher == NULL)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "No dispatcher for ingw [%d]", 
                      ingwId);
      return -1;
   }

   currDispatcher->end();
   return 0;
}

void INGwFtMsnCommMgr::sendMsg(INGwFtPktMsg *msg)
{
   INGwFtMsnPktDispatcher *currDispatcher = NULL;
   int ingwId = msg->getReceiver();

   pthread_mutex_lock(&_dispatcherLock);

   for(int idx = 0; idx < MAX_INGW_DISPATCHER; idx++)
   {
      if(_dispatchers[idx].id == ingwId)
      {
         currDispatcher = _dispatchers[idx].dispatcher;
      }
   }

   pthread_mutex_unlock(&_dispatcherLock);

   if(currDispatcher == NULL)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "No dispatcher for ingw [%d]",
                      ingwId);
      return;
   }

   currDispatcher->sendMsg(msg);
}

int INGwFtMsnCommMgr::shutdown()
{
   _receiver->stop(false);

   pthread_mutex_lock(&_dispatcherLock);

   for(int idx = 0; idx < MAX_INGW_DISPATCHER; idx++)
   {
      if(_dispatchers[idx].dispatcher != NULL)
      {
         _dispatchers[idx].dispatcher->stop(false);
      }
   }

   pthread_mutex_unlock(&_dispatcherLock);
   return 0;
}
