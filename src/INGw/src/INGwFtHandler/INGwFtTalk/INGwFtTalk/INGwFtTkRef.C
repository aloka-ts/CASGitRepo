/*------------------------------------------------------------------------------
         File: INGwFtTkRef.C
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#include <Util/Logger.h>
LOG("INGwFtTalk");
#include <INGwFtTalk/INGwFtTkRef.h>

INGwFtTkRef::INGwFtTkRef()
{
   pthread_mutex_init(&_mutex, NULL);
   _count = 1;
}

INGwFtTkRef::~INGwFtTkRef()
{
   if(_count != 0)
   {
      logger.logMsg(ERROR_FLAG, 0, "Deletion with outstanding ref. [%d]", 
                    _count);
   }

   pthread_mutex_destroy(&_mutex);
}

void INGwFtTkRef::addRef()
{
   pthread_mutex_lock(&_mutex);
   _count++;
   int locCount = _count;
   pthread_mutex_unlock(&_mutex);

   logger.logMsg(TRACE_FLAG, 0, "Add Obj [%x] count [%d]", this, locCount);
}

void INGwFtTkRef::removeRef()
{
   pthread_mutex_lock(&_mutex);
   _count--;
   int locCount = _count;
   pthread_mutex_unlock(&_mutex);

   logger.logMsg(TRACE_FLAG, 0, "Rem Obj [%x] count [%d]", this, locCount);
   if(locCount == 0)
   {
      delete this;
   }
}

INGwFtTkRefObjHolder::INGwFtTkRefObjHolder(INGwFtTkRef *obj)
{
   _obj = obj;
}

INGwFtTkRefObjHolder::~INGwFtTkRefObjHolder()
{
   if(_obj != NULL)
   {
      _obj->removeRef();
   }
}

INGwFtTkRef * INGwFtTkRefObjHolder::getObj()
{
   return _obj;
}
