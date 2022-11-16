/*------------------------------------------------------------------------------
         File: INGwFtTkMsgHandlerInf.C
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#include <INGwFtTalk/INGwFtTkMsgHandlerInf.h>

/*
 *
 *   Default implementation assumes the message buffer is char * allocated using
 *   new [] operator. If different type of messages are sent then its the 
 *   application responsibility to extend this interface and clean the message 
 *   buf properly.
 *
 *   General comment: MessageNotdelivered is not invoked by INGwFtTalk. So its the 
 *                    application responsibility to clean the unsuccessful 
 *                    message. Then why we need the message delivered and all!!
 *                    -Suriya.
 */

void INGwFtTkMsgHandlerInf::messageDelivered(INGwFtTkConnHandle *handle, 
                                        unsigned char opCode,
                                        unsigned short dataStructureId,
                                        unsigned int msgLen, char *msg)
{
   delete []msg;
}

void INGwFtTkMsgHandlerInf::messageNotDelivered(INGwFtTkConnHandle *handle, 
                                           unsigned char opCode,
                                           unsigned short dataStructureId,
                                           unsigned int msgLen, char *msg,
                                           int errorCode)
{
   //Not been invoked by INGwFtTalk.
   delete []msg;
}

void INGwFtTkMsgHandlerInf::multiMessageDelivered(INGwFtTkConnHandle *handle,
                                             unsigned char opCode,
                                             unsigned short dataStructureId,
                                             INGwFtTkMessageBuf *message,
                                             unsigned int no_of_messages)
{
   for(int idx = 0; idx < no_of_messages; idx++)
   {
      delete [] ((char *)(message[idx].msg));
   }
}
