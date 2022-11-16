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
//     File:     INGwSpDataFactory.h
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_SIP_DATA_FACTORY_H_
#define INGW_SP_SIP_DATA_FACTORY_H_


#include <INGwSipProvider/INGwSpData.h>
#include <INGwSipProvider/INGwSpSipIncludes.h>

#include <INGwInfraUtil/INGwIfrUtlObjectPool.h>

class INGwSpDataFactory 
{
   public :

      typedef INGwIfrUtlObjectPool<INGwSpData, INGwSpData_Init, 
													 INGwSpData_Reuse, INGwSpData_MemMgr, int> INGwSpSipDataPool;

   private:

      class INGwSpSipDataPoolHolder
      {
         public:

            INGwSpSipDataPool *pool;
            unsigned long int thrIdx;

         public:

            INGwSpSipDataPoolHolder()
            {
               pool = NULL;
               thrIdx = 0;
            }
      };

   public:

      static void initialize(int aPreAllocSize);

      INGwSpData* getNewObject();
      void reuseObject(INGwSpData* apObject);

   protected :

      INGwSpDataFactory();
      ~INGwSpDataFactory();

   private:
      int           _allocPools;
      INGwSpSipDataPoolHolder  *mpObjPool;

      int _dummyVar;
      int _poolSize;

      static INGwSpDataFactory* mpSelf;

      INGwSpSipDataPool * _getPool(int thrIdx);
      INGwSpSipDataPool * _createPool(int aThrId);

   public:

      static inline INGwSpDataFactory& getInstance()
      {
         return *mpSelf;
      }

};

#endif
