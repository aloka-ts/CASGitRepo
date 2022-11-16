/*------------------------------------------------------------------------------
         File: INGwFtTkSubCompMgr.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_SUBCOMP_MGR_H__
#define __BTK_SUBCOMP_MGR_H__

class QueueManager;
class INGwFtTkReceiverQueue;

#define BTK_PRE_SUBCOMP_BUF 500

/*
 *  Since the mutable interfaces register & unregister are to be called during 
 *  the application initialization and shutdown, mutex were not needed to 
 *  safeguard the _receivers. -Suriya
 */

class INGwFtTkSubCompMgr
{
   private:

      INGwFtTkReceiverQueue *_receivers[BTK_PRE_SUBCOMP_BUF];

   public:

      INGwFtTkSubCompMgr();
      ~INGwFtTkSubCompMgr();

      INGwFtTkReceiverQueue * registerMessageReceiver(unsigned int subComp);
      int unregisterMessageReceiver(unsigned int subComp);
      int unregisterMessageReceiver(INGwFtTkReceiverQueue *);

      INGwFtTkReceiverQueue * getQueue(unsigned int subComp);

   private:

      INGwFtTkSubCompMgr(const INGwFtTkSubCompMgr &);
      INGwFtTkSubCompMgr & operator = (const INGwFtTkSubCompMgr &);
};

#endif
