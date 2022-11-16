/*------------------------------------------------------------------------------
         File: INGwFtTkReceiverQueue.C
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
#include <INGwFtTalk/INGwFtTkReceiverQueue.h>
#include <INGwFtTalk/INGwFtTkMessage.h>
#include <stdlib.h>

INGwFtTkReceiverQueue::INGwFtTkReceiverQueue()
{
   _msgQueue = new QueueManager(true, 2048, 1024);
   _msgQueue->registerCleanupFunc(INGwFtTkReceivedMsgCleaner);
   _aryLen = 1024;
   _dequeAry = new QueueData[_aryLen];
}

INGwFtTkReceiverQueue::~INGwFtTkReceiverQueue()
{
   delete [] _dequeAry;
   delete _msgQueue;
}

INGwFtTkReceiverQueue::INGwFtTkReceiverQueue(const INGwFtTkReceiverQueue &)
{
   logger.logMsg(ERROR_FLAG, 0, "Wrong usage. copy cons");
   exit(1);
}

INGwFtTkReceiverQueue & INGwFtTkReceiverQueue::operator = (const INGwFtTkReceiverQueue &)
{
   logger.logMsg(ERROR_FLAG, 0, "Wrong usage. = oper");
   exit(1);
   return *this;
}

int INGwFtTkReceiverQueue::receive(ObjectId &senderID, unsigned char &opCode, 
                              unsigned short &dataStructureID, 
                              unsigned int &msgLen, char *&msg, int &version)
{
   QueueData inMsg;

   if(_msgQueue->eventDequeue(&inMsg, 0) == 0)
   {
      logger.logMsg(WARNING_FLAG, 0, "Receive from Queue returned 0.");
      return -1;
   }

   INGwFtTkReceivedMsg *rcvMsg = (INGwFtTkReceivedMsg *)inMsg.data;

   INGwFtTkMesgHeader &currHeader = rcvMsg->header;

   switch(rcvMsg->version)
   {
      case 1:
      {
         version = currHeader.v1Msg.version;
         senderID = ObjectId(currHeader.v1Msg.instanceID, 0);
         opCode = currHeader.v1Msg.opCode;
         dataStructureID = currHeader.v1Msg.dataStructureID;
         msgLen = currHeader.v1Msg.msgLen;
         msg = rcvMsg->messageBuf;
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unsupported version negotiated. [%d]",
                       rcvMsg->version);
         exit(0);
      }
   }

   delete rcvMsg;

   return 0;
}

int INGwFtTkReceiverQueue::receive(INGwFtTkOutData *data, int len)
{
   int ret = 0;

   int sleepTime = 10;

   while(ret != len)
   {
      int toget = len - ret;

      if(toget > _aryLen)
      {
         toget = _aryLen;
      }

      int dequeueCount = toget;

      if(sleepTime == 0)
      {
         dequeueCount = 1;
      }

      int dataret = _msgQueue->eventDequeueBlk(_dequeAry, dequeueCount, 
                                               sleepTime, true);

      if(dataret == 0)
      {
         if(_msgQueue->isQueueStopped() == true)
         {
            return -1;
         }

         if(ret)
         {
            break;
         }

         sleepTime = 0;

         continue;
      }

      sleepTime = 10;

      for(int idx = 0; idx < dataret; idx++)
      {
         INGwFtTkReceivedMsg *currMsg = (INGwFtTkReceivedMsg *)(_dequeAry[idx].data);

         INGwFtTkMesgHeader &currHeader = currMsg->header;

         switch(currMsg->version)
         {
            case 1:
            {
               data[ret].version = currHeader.v1Msg.version;
               data[ret].senderID = ObjectId(currHeader.v1Msg.instanceID, 0);
               data[ret].opCode = currHeader.v1Msg.opCode;
               data[ret].dataStructureID = currHeader.v1Msg.dataStructureID;
               data[ret].msgLen = currHeader.v1Msg.msgLen;
               data[ret].msg = currMsg->messageBuf;
            }
            break;

            default:
            {
               logger.logMsg(ERROR_FLAG, 0, 
                             "Unsupported version negotiated. [%d]",
                             currMsg->version);
               exit(0);
            }
         }

         ret++;

         delete currMsg;
      }

      if(toget != dataret)
      {
         break;
      }
   }

   return ret;
}

void INGwFtTkReceiverQueue::INGwFtTkReceivedMsgCleaner(QueueData inMsg)
{
   INGwFtTkReceivedMsg *rcvMsg = (INGwFtTkReceivedMsg *)inMsg.data;

   if(rcvMsg->messageBuf)
   {
      delete [](rcvMsg->messageBuf);
   }

   delete rcvMsg;

   return;
}

