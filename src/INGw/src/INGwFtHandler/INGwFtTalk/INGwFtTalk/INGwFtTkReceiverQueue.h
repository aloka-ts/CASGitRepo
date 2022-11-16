/*------------------------------------------------------------------------------
         File: INGwFtTkReceiverQueue.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_RECEIVER_QUEUE_H__
#define __BTK_RECEIVER_QUEUE_H__

#include <Util/ObjectId.h>
#include <Util/QueueMgr.h>
#include <INGwFtTalk/INGwFtTkRef.h>

class INGwFtTkSubCompMgr;

typedef struct INGwFtTkOutData
{
   int version;
   ObjectId senderID;
   unsigned short opCode;
   unsigned short dataStructureID;
   int  msgLen;
   char *msg;
};

class INGwFtTkReceiverQueue : public INGwFtTkRef
{
   private:

      QueueManager *_msgQueue;

      QueueData *_dequeAry;
      int _aryLen;

      INGwFtTkReceiverQueue();

   protected:

      virtual ~INGwFtTkReceiverQueue();

   public:

      int receive(ObjectId &senderId, unsigned char &opCode, 
                  unsigned short &dataStructureId, unsigned int &msgLen, 
                  char *&msg, int &version);

      //Bulk receiver for performance. Interface assumes single thread 
      //invocation.
      int receive(INGwFtTkOutData *data, int len);

   public:

      friend class INGwFtTkSubCompMgr;
      friend class INGwFtTkChannel;

      static void INGwFtTkReceivedMsgCleaner(QueueData inMsg);

   private:

      INGwFtTkReceiverQueue(const INGwFtTkReceiverQueue &);
      INGwFtTkReceiverQueue & operator = (const INGwFtTkReceiverQueue &);
};

#endif
