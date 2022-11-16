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
//     File:     INGwIfrUtlObjectPool.C
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <string>

template <class T, class Init, class Reuse, class MemMgr, typename InitParam>
INGwIfrUtlObjectPool<T, Init, Reuse, MemMgr, InitParam>::
INGwIfrUtlObjectPool<T, Init, Reuse, MemMgr, InitParam>(const std::string &name) : 
_name(name)
{
   _msgHead = NULL;
   _msgTail = NULL;
   _freeHead = NULL;
   _freeTail = NULL;
   _msgCount = 0;
   _freeCount = 0;
   _disablePool = false;

   try
   {
      std::string dp = 
                  INGwIfrPrParamRepository::getInstance().getValue(ingwDEBUG_USE_MEM_POOL);
      if(0 == strcasecmp(dp.c_str(), "disabled")) {
         _disablePool = true;
      }
   }
   catch(...) { }
}

template <class T, class Init, class Reuse, class MemMgr, typename InitParam>
INGwIfrUtlObjectPool<T, Init, Reuse, MemMgr, InitParam>::
~INGwIfrUtlObjectPool<T, Init, Reuse, MemMgr, InitParam>() 
{
   Node *currMsg = _msgHead;
   Node *nextMsg = NULL;

   while(currMsg)
   {
      nextMsg = currMsg->next;
      mMemMgr.deallocate(currMsg->message);
      delete currMsg;
      currMsg = nextMsg;
   }

   _msgHead = _msgTail = NULL;

   currMsg = _freeHead;

   while(currMsg)
   {
      nextMsg = currMsg->next;
      delete currMsg;
      currMsg = nextMsg;
   }

   _freeHead = _freeTail = NULL;
   _msgCount = 0;
   _freeCount = 0;
}

template <class T, class Init, class Reuse, class MemMgr, typename InitParam>
void INGwIfrUtlObjectPool<T, Init, Reuse, MemMgr, InitParam>::
initialize(int aPreAllocSize, InitParam* apParam) 
{
   mpInitParam = apParam;

   _arySize = aPreAllocSize;

   if(_arySize < 100)
   {
      _arySize = 100;
   }

#ifdef BP_DISABLE_MEMPOOL
   {
      return;
   }
#endif

   if(_disablePool)
   {
      return;
   }

   int no_of_msg = _arySize >> 3;

   for(int idx = 0; idx < no_of_msg; idx++)
   {
      Node *currNode = new Node;
      currNode->message = mMemMgr.allocate(*mpInitParam);

      if(_msgHead == NULL)
      {
         _msgHead = _msgTail = currNode;
      }
      else
      {
         _msgTail->next = currNode;
         _msgTail = currNode;
      }
   }

   _msgCount = no_of_msg;
   return;
}

template <class T, class Init, class Reuse, class MemMgr, typename InitParam>
T* INGwIfrUtlObjectPool<T, Init, Reuse, MemMgr, InitParam>::getNewObject(void) 
{
#ifdef BP_DISABLE_MEMPOOL
   {
      T *message = mMemMgr.allocate(*mpInitParam);
      mInit(message);
      return message;
   }
#endif

   if(_disablePool)
   {
      T *message = mMemMgr.allocate(*mpInitParam);
      mInit(message);
      return message;
   }

   if(_msgHead == NULL)
   {
      int no_of_msg = _arySize >> 3;

      for(int idx = 0; idx < no_of_msg; idx++)
      {
         Node *currNode = new Node;
         currNode->message = mMemMgr.allocate(*mpInitParam);
   
         if(_msgHead == NULL)
         {
            _msgHead = _msgTail = currNode;
         }
         else
         {
            _msgTail->next = currNode;
            _msgTail = currNode;
         }
      }

      _msgCount = no_of_msg;
   }

   Node *curr = _msgHead;
   _msgHead = _msgHead->next;
   _msgCount--;

   curr->next = NULL;

   if(_msgHead == NULL)
   {
      _msgTail = NULL;
   }

   if(_freeHead == NULL)
   {
      _freeCount = 1;
      _freeHead = _freeTail = curr;
   }
   else
   {
      _freeCount++;
      _freeTail->next = curr;
      _freeTail = curr;
   }

   T *message = curr->message;
   curr->message = NULL;

   mInit(message);
   return message;
}

template <class T, class Init, class Reuse, class MemMgr, typename InitParam>
void
INGwIfrUtlObjectPool<T, Init, Reuse, MemMgr, InitParam>::reuseObject(T* apElem) 
{
   mReuse(apElem);

#ifdef BP_DISABLE_MEMPOOL
   {
      mMemMgr.deallocate(apElem);
      return;
   }
#endif

   if(_disablePool)
   {
      mMemMgr.deallocate(apElem);
      return;
   }

   Node *currNode;
   if(_freeHead == NULL)
   {
      currNode = new Node;
   }
   else
   {
      _freeCount--;
      currNode = _freeHead;
      _freeHead = _freeHead->next;

      if(_freeHead == NULL)
      {
         _freeTail = NULL;
      }
   }

   currNode->message = apElem;
   currNode->next = NULL;

   if(_msgTail)
   {
      _msgTail->next = currNode;
      _msgTail = currNode;
   }
   else
   {
      _msgHead = _msgTail = currNode;
   }

   _msgCount++;

   return;
}

template <class T, class Init, class Reuse, class MemMgr, typename InitParam>
void INGwIfrUtlObjectPool<T, Init, Reuse, MemMgr, InitParam>::
reuseObject(INGwIfrUtlObjectPool& arPool)
{
#ifdef BP_DISABLE_MEMPOOL
   {
      return;
   }
#endif

   if(_disablePool)
   {
      return;
   }

   if(arPool._msgHead == NULL)
   {
      return;
   }

   if(_msgTail)
   {
      _msgTail->next = arPool._msgHead;
   }
   else
   {
      _msgHead = arPool._msgHead;
   }

   _msgTail = arPool._msgTail;
   _msgCount += arPool._msgCount;

   int locNoMsgTransferred = arPool._msgCount;

   arPool._msgCount = 0;
   arPool._msgHead = arPool._msgTail = NULL;

   if((arPool._freeHead == NULL) && (locNoMsgTransferred != 0))
   {
      if(_freeCount <= locNoMsgTransferred)
      {
         arPool._freeHead = _freeHead;
         arPool._freeTail = _freeTail;
         arPool._freeCount = _freeCount;

         _freeHead = _freeTail = NULL;
         _freeCount = 0;
      }
      else
      {
         arPool._freeHead = _freeHead;
         arPool._freeCount = locNoMsgTransferred;
         _freeCount -= locNoMsgTransferred;

         Node *prev = _freeHead;

         while(locNoMsgTransferred != 0)
         {
            locNoMsgTransferred--;
            prev = _freeHead;
            _freeHead = _freeHead->next;
         }

         arPool._freeTail = prev;
         prev->next = NULL;

         if(_freeHead == NULL)
         {
            _freeTail = NULL;
            _freeCount = 0;
         }
      }
   }

   return;
}

template <class T, class Init, class Reuse, class MemMgr, typename InitParam>
std::string 
INGwIfrUtlObjectPool<T, Init, Reuse, MemMgr, InitParam>::toLog(int tabCount) const
{
   char tabs[20];

   for(int idx = 0; idx < tabCount; idx++)
   {
      tabs[idx] = '\t';
   }

   tabs[tabCount] = '\0';

   std::string ret = tabs;
   ret += "ObjectPool [" + _name + "]\n";

   char data[1000];
   sprintf(data, "%s\tFreeHolder[%d] Msg[%d]\n", tabs, _freeCount, _msgCount);

   ret += data;

   ret += tabs;
   ret += "-ObjectPool [" + _name + "]\n";

   return ret;
}

// EOF INGwIfrUtlObjectPool.C
