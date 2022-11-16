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
//     File:     INGwIfrUtlLock.h
//
//     Desc:     <Description of file>
//
//     Author     			Date     			Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGE_IFR_UTL_LOCK_H_
#define _INGE_IFR_UTL_LOCK_H_

#include <pthread.h>
#include <INGwInfraUtil/INGwIfrUtlLogger.h>

class INGwIfrUtlLock
{
   private:

      pthread_mutex_t *_mutex;

      INGwIfrUtlLock(const INGwIfrUtlLock &) {}
      INGwIfrUtlLock & operator = (const INGwIfrUtlLock &) {return *this;}

   public:

      INGwIfrUtlLock(const pthread_mutex_t *);
      ~INGwIfrUtlLock();
};

class INGwIfrUtlWLock
{
   private:

      pthread_rwlock_t *_mutex;

      INGwIfrUtlWLock(const INGwIfrUtlLock &) {}
      INGwIfrUtlWLock & operator = (const INGwIfrUtlLock &) {return *this;}

   public:

      INGwIfrUtlWLock(const pthread_rwlock_t *);
      ~INGwIfrUtlWLock();
};

class INGwIfrUtlRLock
{
   private:

      pthread_rwlock_t *_mutex;

      INGwIfrUtlRLock(const INGwIfrUtlLock &) {}
      INGwIfrUtlRLock & operator = (const INGwIfrUtlLock &) {return *this;}

   public:

      INGwIfrUtlRLock(const pthread_rwlock_t *);
      ~INGwIfrUtlRLock();
};

#define INGwIfrUtlGuard(X) INGwIfrUtlLock local_lock(X)
#define INGwIfrUtlRGuard(X) INGwIfrUtlRLock local_rlock(X)
#define INGwIfrUtlWGuard(X) INGwIfrUtlWLock local_wlock(X)

#endif
