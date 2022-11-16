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
//     File:     INGwSpBufferFactory.h
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_BUFFER_FACTORY_H_
#define INGW_SP_BUFFER_FACTORY_H_

#include <pthread.h>

#include <INGwInfraUtil/INGwIfrUtlObjectPool.h>

class char_Init;
class char_Reuse;
class char_MemMgr;

class INGwSpBufferFactory 
{
   public:

      typedef INGwIfrUtlObjectPool<char, char_Init, char_Reuse, char_MemMgr, int> 
                                                                   INGwSpBufferPool;

   private:

      class INGwSpPoolHolder
      {
         public:

            INGwSpBufferPool *pool;
            unsigned long int thrIdx;

         public:

            INGwSpPoolHolder()
            {
               pool = NULL;
               thrIdx = 0;
            }
      };

   public :


      static void initialize(int aLoadDetail, int aArraySize);

      int getFreeCount();
      char* getNewObject();
      void reuseObject(char* apObject);

   protected :

      INGwSpBufferFactory();
      ~INGwSpBufferFactory();

   private:

      pthread_mutex_t mPoolLock;

      INGwSpPoolHolder    *mpObjPool;
      int             _allocPools;

      int _poolSize;
      int _charArySize;

      static INGwSpBufferFactory* mpSelf;

      INGwSpBufferPool * _getPool(int thrIdx);
      INGwSpBufferPool * _createPool(int aThrId);

   public:

      static inline INGwSpBufferFactory& getInstance()
      {
         return *mpSelf;
      }
};

class char_Init 
{
   public :

     char_Init() { }

     void operator() (char* s) 
     { 
        s[0] = '\0';
     }
};

class char_Reuse 
{
   public :

      char_Reuse() { }

      void operator() (char* s) 
      {
         s[0] = '\0';
      }
};

class char_MemMgr
{
   public :

      char_MemMgr() { }

      char * allocate(int size)
      {
         return new char[size];
      }

      void deallocate(char* s) 
      { 
         delete []s;
         return;
      }
};

#endif //INGW_SP_BUFFER_FACTORY_H_

// EOF INGwSpBufferFactory.h
