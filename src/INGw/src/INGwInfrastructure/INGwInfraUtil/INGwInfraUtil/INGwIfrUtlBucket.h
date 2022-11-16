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
//     File:     INGwIfrUtlBucket.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_INFRA_UTIL_BUCKET_H_
#define _INGW_INFRA_UTIL_BUCKET_H_

#include <pthread.h>
#include <map>
#include <iostream>
#include <utility>
#include <memory>


template <class TKey, class TValue>
class INGwIfrUtlBucket : public virtual std::map<TKey, TValue> {

     pthread_rwlock_t mRWLock;
     int              mTotalCount;
     int              mActiveCount;

     public :

          int initialise(void) { }
          void getRLock(void) { pthread_rwlock_rdlock(&mRWLock); }
          void getWLock(void) { pthread_rwlock_wrlock(&mRWLock); }
          void unLock(void) { pthread_rwlock_unlock(&mRWLock); }
          void add(const TKey& k, TValue* v) { insert(value_type(k, v)); }
          void printAll(std::ostream& os);
};

template<class Cont, class PtrMemFun>
void apply(Cont& c, PtrMemFun f, const char* apcData);

template<class Cont, class PtrMemFun>
void apply(Cont& c, PtrMemFun f);

template<class Cont>
void print(Cont& c, std::ostream& os);

#endif
