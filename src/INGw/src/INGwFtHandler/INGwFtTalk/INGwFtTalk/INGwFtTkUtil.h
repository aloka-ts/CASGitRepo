/*------------------------------------------------------------------------------
         File: INGwFtTkUtil.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_UTIL_H__
#define __BTK_UTIL_H__

#include <pthread.h>

class INGwFtTkLock
{
   private:

      pthread_mutex_t *_mutex;

      INGwFtTkLock(const INGwFtTkLock &) {}
      INGwFtTkLock & operator = (const INGwFtTkLock &) {return *this;}

   public:

      INGwFtTkLock(const pthread_mutex_t *inLock)
      {
         _mutex = const_cast<pthread_mutex_t *>(inLock);
         pthread_mutex_lock(_mutex);
      }

      ~INGwFtTkLock()
      {
         pthread_mutex_unlock(_mutex);
      }
};

#define INGwFtTkGuard(X) INGwFtTkLock local_lock(X)

#endif
