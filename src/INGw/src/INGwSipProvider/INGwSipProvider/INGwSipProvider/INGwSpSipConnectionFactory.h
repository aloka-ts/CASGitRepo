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
//     File:     INGwSpSipConnectionFactory.h
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_SIP_CONNECTION_FACTORY_H_
#define INGW_SP_SIP_CONNECTION_FACTORY_H_

#include <INGwInfraUtil/INGwIfrUtlObjectPool.h>

#include <INGwSipProvider/INGwSpSipConnection.h>

class INGwSpSipConnectionFactory 
{
   public :

      typedef INGwIfrUtlObjectPool<INGwSpSipConnection, INGwSpSipConnection_Init, 
                           INGwSpSipConnection_Reuse, INGwSpSipConnection_MemMgr, int>
         INGwSpSipConnectionPool;

   private:

      class INGwSpSipConnectionPoolHolder
      {
         public:

            INGwSpSipConnectionPool  *pool;
            unsigned long int thrIdx;

         public:

            INGwSpSipConnectionPoolHolder()
            {
               pool = NULL;
               thrIdx = 0;
            }
      };

   public:

      static void initialize(int aPreAllocSize);

      INGwSpSipConnection* getNewObject();
      void reuseObject(INGwSpSipConnection* apObject);

   protected :

      INGwSpSipConnectionFactory();
      ~INGwSpSipConnectionFactory();

   private:

      int           _allocPools;
      INGwSpSipConnectionPoolHolder  *mpObjPool;

      int _dummyVar;
      int _poolSize;

      static INGwSpSipConnectionFactory* mpSelf;

      INGwSpSipConnectionPool * _getPool(int thrIdx);
      INGwSpSipConnectionPool * _createPool(int aThrId);

   public:

      static inline INGwSpSipConnectionFactory& getInstance()
      {
         return *mpSelf;
      }
};

#endif

// EOF INGwSpSipConnectionFactory.h
