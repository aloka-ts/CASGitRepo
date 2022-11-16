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
//     File:     INGwSpDataFactory.C
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <unistd.h>
#include <INGwInfraUtil/INGwIfrUtlMacro.h>
#include <INGwSipProvider/INGwSpDataFactory.h>


INGwSpDataFactory* INGwSpDataFactory::mpSelf = NULL;

INGwSpDataFactory::INGwSpDataFactory() 
{ 
   mpObjPool = NULL; 
}

INGwSpDataFactory::~INGwSpDataFactory() 
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

INGwSpDataFactory::INGwSpSipDataPool * 
INGwSpDataFactory::_getPool(int thrIdx)
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

INGwSpDataFactory::INGwSpSipDataPool *
INGwSpDataFactory::_createPool(int thrIdx)
{
   do
   {
      for(int idx = 0; idx < _allocPools; idx++)
      {
         if(mpObjPool[idx].thrIdx == thrIdx && mpObjPool[idx].pool == NULL)
         {
            char data[100];
            sprintf(data, "SipData id[%d]", thrIdx);
            mpObjPool[idx].pool = new INGwSpSipDataPool(data);
            mpObjPool[idx].pool->initialize(_poolSize, &_dummyVar);
            return mpObjPool[idx].pool;
         }
      }

      for(int idx = 0; idx < _allocPools; idx++)
      {
         if(mpObjPool[idx].pool == NULL && mpObjPool[idx].thrIdx == 0)
         {
            char data[100];
            sprintf(data, "SipData id[%d]", thrIdx);
            mpObjPool[idx].thrIdx = thrIdx;
            mpObjPool[idx].pool = new INGwSpSipDataPool(data);
            mpObjPool[idx].pool->initialize(_poolSize, &_dummyVar);
            return mpObjPool[idx].pool;
         }
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Expanding the pool count. Curr size[%d] "
                                    "New regis thr [%d]", _allocPools, thrIdx);

      INGwSpSipDataPoolHolder *temp;
      temp = new INGwSpSipDataPoolHolder[_allocPools + 50];

      for(int idx = 0; idx < _allocPools; idx++)
      {
         temp[idx] = mpObjPool[idx];
      }

      _allocPools += 50;

      INGwSpSipDataPoolHolder *prev = mpObjPool;
      mpObjPool = temp;
      logger.logMsg(ALWAYS_FLAG, 0, "_createPool():: sleeping 1 sec...");
      sleep(1);
      delete []prev;
   }while(true);

   return NULL;
}

void INGwSpDataFactory::initialize(int aPreAllocSize) 
{
   if(NULL == mpSelf)
   {
      mpSelf = new INGwSpDataFactory();
   }
   else
   {
      logger.logMsg(ERROR_FLAG, 0, "Multiple initialization for SipDataFac");
      logger.logMsg(ALWAYS_FLAG, 0, "Wrong flow. Contact developers.");
      exit(1);
   }

   mpSelf->_allocPools = 50;

   mpSelf->mpObjPool = new INGwSpSipDataPoolHolder[mpSelf->_allocPools];

   mpSelf->_dummyVar = 5;
   mpSelf->_poolSize = aPreAllocSize;
}

INGwSpData* INGwSpDataFactory::getNewObject() 
{
   int tId = MACRO_THREAD_ID();

   return _getPool(tId)->getNewObject();
}

void INGwSpDataFactory::reuseObject(INGwSpData* apObject) 
{
   int tId = MACRO_THREAD_ID();

   _getPool(tId)->reuseObject(apObject);
}
