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
//     File:     INGwSpSipCallFactory.h
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_SIP_CALL_FACTORY_H_
#define INGW_SP_SIP_CALL_FACTORY_H_


#include <INGwSipProvider/INGwSpSipCall.h>
#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwInfraUtil/INGwIfrUtlObjectPool.h>

class INGwSpSipCallFactory 
{
   public :

      typedef INGwIfrUtlObjectPool<INGwSpSipCall, INGwSpSipCall_Init, INGwSpSipCall_Reuse, 
                           INGwSpSipCall_MemMgr, int> 
         INGwSpSipCallPool;

   private:

      class INGwSpSipCallPoolHolder
      {
         public:

            INGwSpSipCallPool  *pool;
            unsigned long int thrIdx;

         public:

            INGwSpSipCallPoolHolder()
            {
               pool = NULL;
               thrIdx = 0;
            }
      };

   public:

      static void initialize(int aPreAllocSize);
      INGwSpSipCall* getNewObject();
      void reuseObject(INGwSpSipCall* apObject);

   protected :

      INGwSpSipCallFactory();
      ~INGwSpSipCallFactory();

   private:

      int          _allocPools;
      INGwSpSipCallPoolHolder *mpObjPool;

      int        _poolSize;
      int _unused;

      static INGwSpSipCallFactory* mpSelf;
      INGwSpSipCallPool * _getPool(int thrIdx);
      INGwSpSipCallPool *_createPool(int aThrId);

   public:

      static INGwSpSipCallFactory& getInstance()
      {
         return *mpSelf;
      }
};

#endif

// EOF INGwSpSipCallFactory.h
