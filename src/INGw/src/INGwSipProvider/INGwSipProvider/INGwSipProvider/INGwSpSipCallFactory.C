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
//     File:     INGwSpSipCallFactory.C
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

#include <INGwSipProvider/INGwSpSipCallFactory.h>

using namespace std;

INGwSpSipCallFactory* INGwSpSipCallFactory::mpSelf = NULL;

INGwSpSipCallFactory::INGwSpSipCallFactory() 
{ 
   mpObjPool = NULL; 
}

INGwSpSipCallFactory::~INGwSpSipCallFactory() 
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

INGwSpSipCallFactory::INGwSpSipCallPool * INGwSpSipCallFactory::_getPool(int thrIdx)
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

INGwSpSipCallFactory::INGwSpSipCallPool * INGwSpSipCallFactory::_createPool(int thrIdx)
{
   do
   {
      for(int idx = 0; idx < _allocPools; idx++)
      {
         if(mpObjPool[idx].thrIdx == thrIdx && mpObjPool[idx].pool == NULL)
         {
            char data[100];
            sprintf(data, "GWCallPool id[%d]", thrIdx);
            mpObjPool[idx].pool = new INGwSpSipCallPool(data);
            mpObjPool[idx].pool->initialize(_poolSize, &_unused);
            return mpObjPool[idx].pool;
         }
      }

      for(int idx = 0; idx < _allocPools; idx++)
      {
         if(mpObjPool[idx].pool == NULL && mpObjPool[idx].thrIdx == 0)
         {
            char data[100];
            sprintf(data, "GWCallPool id[%d]", thrIdx);
            mpObjPool[idx].thrIdx = thrIdx;
            mpObjPool[idx].pool = new INGwSpSipCallPool(data);
            mpObjPool[idx].pool->initialize(_poolSize, &_unused);
            return mpObjPool[idx].pool;
         }
      }


      logger.logMsg(ALWAYS_FLAG, 0, "Expanding the pool count. Curr size[%d] "
                                    "New regis thr [%d]", _allocPools, thrIdx);

      INGwSpSipCallPoolHolder *temp;
      temp = new INGwSpSipCallPoolHolder[_allocPools + 50];

      for(int idx = 0; idx < _allocPools; idx++)
      {
         temp[idx] = mpObjPool[idx];
      }

      _allocPools += 50;

      INGwSpSipCallPoolHolder *prev = mpObjPool;
      mpObjPool = temp;
      logger.logMsg(ALWAYS_FLAG, 0, "_createPool():: sleeping 1 sec...");
      sleep(1);
      delete []prev;
   }while(true);

   return NULL;
}

void INGwSpSipCallFactory::initialize(int aPreAllocSize) 
{
   if(NULL == mpSelf)
   {
      mpSelf = new INGwSpSipCallFactory();
   }
   else
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Multiple initialization for GwCallFactory");
      logger.logINGwMsg(false, ALWAYS_FLAG, 0, "Wrong flow. Contact developers.");
      exit(1);
   }

   mpSelf->_allocPools = 50;

   mpSelf->mpObjPool = new INGwSpSipCallPoolHolder[mpSelf->_allocPools];

   mpSelf->_poolSize = aPreAllocSize;
   mpSelf->_unused = 0;
}

INGwSpSipCall* INGwSpSipCallFactory::getNewObject()
{
   int tId = MACRO_THREAD_ID();

   INGwSpSipCall *pCall = _getPool(tId)->getNewObject();
   pCall->getRef();
   return pCall;
}

void INGwSpSipCallFactory::reuseObject(INGwSpSipCall* apObject) 
{
   int tId = MACRO_THREAD_ID();

   _getPool(tId)->reuseObject(apObject);
}

// EOF INGwSpSipCallFactory.C
