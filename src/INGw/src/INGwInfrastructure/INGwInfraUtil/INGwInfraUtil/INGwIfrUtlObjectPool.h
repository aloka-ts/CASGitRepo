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
//     File:     INGwIfrUtlObjectPool.h
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_IFR_UTL_OBJECT_POOL_H_
#define INGW_IFR_UTL_OBJECT_POOL_H_

#include <Util/StatCollectorInf.h>
#include <INGwInfraParamRepository/INGwIfrPrIncludes.h>

#include <string>

template <class T,  class Init, class Reuse, class MemMgr, typename InitParam>
class INGwIfrUtlObjectPool : public StatCollectorInf
{
   private:
     struct Node
     {
        Node *next;
        T* message;
        Node()
        {
           next = NULL;
           message = NULL;
        }
     };

     Node *_msgHead;
     Node *_msgTail;
     Node *_freeHead;
     Node *_freeTail;
     int _arySize;
     int _msgCount;
     int _freeCount;
     bool _disablePool;
     InitParam* mpInitParam;
     Init      mInit;
     Reuse     mReuse;
     MemMgr    mMemMgr;
     std::string _name;

   public :
     INGwIfrUtlObjectPool(const std::string &name);
     ~INGwIfrUtlObjectPool();
     void initialize(int aPreAllocSize, InitParam* apParam);
     int getObjCount(void) 
     { 
        return _msgCount;
     }
     int getFreeHolderCount()
     {
        return _freeCount;
     }

     T* getNewObject(void);
     void reuseObject(T*);
     void reuseObject(INGwIfrUtlObjectPool& arPool);
     std::string toLog(int tabCount) const;
};

#include <INGwInfraUtil/INGwIfrUtlObjectPool.C>
#endif
// EOF INGwIfrUtlObjectPool.h

