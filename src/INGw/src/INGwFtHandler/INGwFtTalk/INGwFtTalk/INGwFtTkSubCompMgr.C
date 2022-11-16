/*------------------------------------------------------------------------------
         File: INGwFtTkSubCompMgr.C
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

#include <INGwFtTalk/INGwFtTkUtil.h>
#include <INGwFtTalk/INGwFtTkSubCompMgr.h>
#include <INGwFtTalk/INGwFtTkReceiverQueue.h>

INGwFtTkSubCompMgr::INGwFtTkSubCompMgr()
{
   for(int idx = 0; idx < BTK_PRE_SUBCOMP_BUF; idx++)
   {
      _receivers[idx] = NULL;
   }
}

INGwFtTkSubCompMgr::~INGwFtTkSubCompMgr()
{
   for(int idx = 0; idx < BTK_PRE_SUBCOMP_BUF; idx++)
   {
      if(_receivers[idx] != NULL)
      {
         logger.logMsg(ERROR_FLAG, 0, "Not all receivers deregisted. "
                                      "SubComp [%d]", idx);
         _receivers[idx]->_msgQueue->stopQueue();
         _receivers[idx]->removeRef();
         _receivers[idx] = NULL;
      }
   }
}

INGwFtTkReceiverQueue * INGwFtTkSubCompMgr::registerMessageReceiver(unsigned int subComp)
{
   if(subComp >= BTK_PRE_SUBCOMP_BUF)
   {
      logger.logMsg(ERROR_FLAG, 0, "SubComp out of range. [%d] max [%d]",
                    subComp, BTK_PRE_SUBCOMP_BUF);
      return NULL;
   }

   INGwFtTkReceiverQueue *ret = NULL;

   if(_receivers[subComp] == NULL)
   {
      ret = new INGwFtTkReceiverQueue();

      char name[500];
      sprintf(name, "INGwFtTk SubCompReceiver[%d]", subComp);
      ret->_msgQueue->setName(name);
      _receivers[subComp] = ret;

      logger.logMsg(ALWAYS_FLAG, 0, "Successfully registered subComp [%d]", 
                    subComp);
   }
   else
   {
      ret = _receivers[subComp];

      logger.logMsg(ERROR_FLAG, 0, "Multiple registration for subComp [%d]",
                    subComp);
      logger.logMsg(ERROR_FLAG, 0, "Usage violation.");
   }

   return ret;
}

int INGwFtTkSubCompMgr::unregisterMessageReceiver(unsigned int subComp)
{
   if(subComp >= BTK_PRE_SUBCOMP_BUF)
   {
      logger.logMsg(ERROR_FLAG, 0, "SubComp out of range. [%d] max [%d]",
                    subComp, BTK_PRE_SUBCOMP_BUF);
      return -1;
   }

   if(_receivers[subComp] == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "No receivers for subComp [%d]", subComp);
      return -1;
   }

   INGwFtTkReceiverQueue *locQueue = _receivers[subComp];
   _receivers[subComp] = NULL;

   locQueue->_msgQueue->stopQueue();
   locQueue->removeRef();

   return 0;
}

int INGwFtTkSubCompMgr::unregisterMessageReceiver(INGwFtTkReceiverQueue *inQueue)
{
   for(int idx = 0; idx < BTK_PRE_SUBCOMP_BUF; idx++)
   {
      if(_receivers[idx] == inQueue)
      {
         _receivers[idx] = NULL;
         inQueue->_msgQueue->stopQueue();
         inQueue->removeRef();

         return 0;
      }
   }

   logger.logMsg(ERROR_FLAG, 0, "Unknown receiver [%x]", inQueue);
   return -1;
}

INGwFtTkReceiverQueue * INGwFtTkSubCompMgr::getQueue(unsigned int subComp)
{
   if(subComp >= BTK_PRE_SUBCOMP_BUF)
   {
      logger.logMsg(ERROR_FLAG, 0, "SubComp out of range. [%d] max [%d]",
                    subComp, BTK_PRE_SUBCOMP_BUF);
      return NULL;
   }

   if(_receivers[subComp])
   {
      _receivers[subComp]->addRef();
      return _receivers[subComp];
   }

   return NULL;
}
