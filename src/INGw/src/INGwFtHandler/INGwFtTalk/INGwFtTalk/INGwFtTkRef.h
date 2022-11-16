/*------------------------------------------------------------------------------
         File: INGwFtTkRef.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_REF_H__
#define __BTK_REF_H__

#include <pthread.h>

class INGwFtTkRef
{
   private:

      pthread_mutex_t _mutex;
      int _count;

   protected:

      INGwFtTkRef();
      virtual ~INGwFtTkRef();

   public:

      void addRef();
      void removeRef();

   private:

      INGwFtTkRef(const INGwFtTkRef &);
      INGwFtTkRef & operator = (const INGwFtTkRef &);
};

class INGwFtTkRefObjHolder
{
   private:

      INGwFtTkRef *_obj;

   public:

      INGwFtTkRefObjHolder(INGwFtTkRef *);
      ~INGwFtTkRefObjHolder();

      INGwFtTkRef * getObj();

   private:

      INGwFtTkRefObjHolder(INGwFtTkRefObjHolder &);
      INGwFtTkRefObjHolder & operator = (const INGwFtTkRefObjHolder &);
};

#endif
