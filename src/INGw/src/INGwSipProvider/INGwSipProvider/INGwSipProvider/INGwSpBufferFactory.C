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
//     File:     INGwSpBufferFactory.C
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");
#include <INGwSipProvider/INGwSpBufferFactory.h>

#include <unistd.h>
#include <INGwInfraUtil/INGwIfrUtlMacro.h>

using namespace std;

#if 0
   Buffer Factory is for allocation always from a thread and deallocation from 
   many thread.

   If many threads tries to allocate mem, it wont be efficient. 
#endif

INGwSpBufferFactory* INGwSpBufferFactory::mpSelf = NULL;

INGwSpBufferFactory::INGwSpBufferFactory() 
{ 
   mpObjPool = NULL; 
}

INGwSpBufferFactory::~INGwSpBufferFactory() 
{
   if(mpObjPool == NULL)
   {
      return;
   }

   for(int idx = 0; idx < _allocPools; idx++)
   {
      if(mpObjPool[idx].pool != NULL)
      {
         delete mpObjPool[idx].pool;
         mpObjPool[idx].pool = NULL;
      }
   }

   delete [] mpObjPool;
   mpObjPool = NULL;
   return;
}

void INGwSpBufferFactory::initialize(int aLoadDetail, int aArraySize)
{
   if(NULL == mpSelf) 
   {
      mpSelf = new INGwSpBufferFactory();
   }
   else
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Multiple initialization for BufferFactory");
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Wrong flow. Contact developers.");
      exit(1);
   }

   pthread_mutex_init(&(mpSelf->mPoolLock), 0);

   mpSelf->_allocPools = 50; 

   mpSelf->mpObjPool = new INGwSpPoolHolder[mpSelf->_allocPools];

   mpSelf->_charArySize = aArraySize;
   mpSelf->_poolSize = aLoadDetail;

   mpSelf->_getPool(0);
}

INGwSpBufferFactory::INGwSpBufferPool * INGwSpBufferFactory::_getPool(int thrIdx)
{
   for(int idx = 0; idx < _allocPools; idx++)
   {
      if(mpObjPool[idx].thrIdx == thrIdx && mpObjPool[idx].pool != NULL)
      {
         return mpObjPool[idx].pool;
      }
   }

   return _createPool(thrIdx);
}

INGwSpBufferFactory::INGwSpBufferPool * INGwSpBufferFactory::_createPool(int thrIdx) 
{
   do
   {
      for(int idx = 0; idx < _allocPools; idx++)
      {
         if(mpObjPool[idx].thrIdx == thrIdx && mpObjPool[idx].pool == NULL)
         {
            char data[100];
            sprintf(data, "BufferPool id[%d]", thrIdx);
            mpObjPool[idx].pool = new INGwSpBufferPool(data);
            mpObjPool[idx].pool->initialize(_poolSize, &_charArySize);
            return mpObjPool[idx].pool;
         }
      }

      for(int idx = 0; idx < _allocPools; idx++)
      {
         if(mpObjPool[idx].pool == NULL && mpObjPool[idx].thrIdx == 0)
         {
            char data[100];
            sprintf(data, "BufferPool id[%d]", thrIdx);
            mpObjPool[idx].thrIdx = thrIdx;
            mpObjPool[idx].pool = new INGwSpBufferPool(data);
            mpObjPool[idx].pool->initialize(_poolSize, &_charArySize);
            return mpObjPool[idx].pool;
         }
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Expanding the pool count. Curr size[%d] "
                                    "New regis thr [%d]", _allocPools, thrIdx);

      INGwSpPoolHolder *temp;
      temp = new INGwSpPoolHolder[_allocPools + 50];

      for(int idx = 0; idx < _allocPools; idx++)
      {
         temp[idx] = mpObjPool[idx];
      }

      _allocPools += 50;

      INGwSpPoolHolder *prev = mpObjPool;
      mpObjPool = temp;
      logger.logMsg(ALWAYS_FLAG, 0, "_createPool():: sleeping 1 sec...");
      sleep(1);
      delete []prev;
   }while(true);

   return NULL;
}

int INGwSpBufferFactory::getFreeCount() 
{
   return _getPool(0)->getObjCount();
}

char* INGwSpBufferFactory::getNewObject() 
{
   pthread_mutex_lock(&mPoolLock);
   char* pObject = _getPool(0)->getNewObject();
   pthread_mutex_unlock(&mPoolLock);

   return pObject;
}

void INGwSpBufferFactory::reuseObject(char* apObject) 
{
   int tId = MACRO_THREAD_ID();

   INGwSpBufferPool *pool = _getPool(tId);

   pool->reuseObject(apObject);

   if(pool->getObjCount() >= 1000) 
   {
      pthread_mutex_lock(&mPoolLock);
      mpObjPool[0].pool->reuseObject(*pool);
      pthread_mutex_unlock(&mPoolLock);
   }
}

// EOF INGwSpBufferFactory.C
