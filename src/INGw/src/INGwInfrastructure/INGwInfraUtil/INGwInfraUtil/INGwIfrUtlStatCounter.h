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
//     File:     INGwIfrUtlStatCounter.C
//
//     Desc:      Utility used to ping the machine availability.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef __BP_STAT_COUNTER_H__
#define __BP_STAT_COUNTER_H__

#include <pthread.h>
#include <string>
using namespace std;

class BpStatCounter
{
   private:

      int _counter;
      mutable pthread_mutex_t _mutex;

   private:

      inline void _lock() const
      {
         pthread_mutex_lock(&_mutex);
      }

      inline void _unlock() const
      {
         pthread_mutex_unlock(&_mutex);
      }

   public:

      inline BpStatCounter()
      {
         _counter = 0;
         pthread_mutex_init(&_mutex, NULL);
      }

      inline ~BpStatCounter()
      {
         _counter = 0;
         pthread_mutex_destroy(&_mutex);
      }

      inline int operator++()
      {
         _lock();
         int ret = _counter;
         _counter++;

         _unlock();

         return ret;
      }

      inline int operator--()
      {
         _lock();
         int ret = _counter;
         _counter--;

         _unlock();

         return ret;
      }

      inline int operator++(int dummy)
      {
         _lock();
         _counter++;

         int ret = _counter;
         _unlock();

         return ret;
      }

      inline int operator--(int dummy)
      {
         _lock();
         _counter--;

         int ret = _counter;
         _unlock();

         return ret;
      }

      inline int operator+=(int val)
      {
         _lock();
         _counter += val;

         int ret = _counter;
         _unlock();

         return ret;
      }

      inline int operator-=(int val)
      {
         _lock();
         _counter -= val;

         int ret = _counter;
         _unlock();

         return ret;
      }

      int getValue() const
      {
         int ret = 0;
         _lock();
         ret = _counter;
         _unlock();
         return ret;
      }
};

#endif
