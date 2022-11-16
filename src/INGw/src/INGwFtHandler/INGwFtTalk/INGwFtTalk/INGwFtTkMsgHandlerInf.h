/*------------------------------------------------------------------------------
         File: INGwFtTkMsgHandlerInf.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_MSG_HANDLER_INF_H__
#define __BTK_MSG_HANDLER_INF_H__

#include <INGwFtTalk/INGwFtTkAppInf.h>
class INGwFtTkConnHandle;

class INGwFtTkMsgHandlerInf 
{
   public:

      virtual void messageDelivered(INGwFtTkConnHandle *handle, unsigned char opCode,
                                    unsigned short dataStructureId,
                                    unsigned int msgLen, char *msg);

      virtual void messageNotDelivered(INGwFtTkConnHandle *handle, 
                                       unsigned char opCode,
                                       unsigned short dataStructureId,
                                       unsigned int msgLen, char *msg,
                                       int errorCode);

      virtual void multiMessageDelivered(INGwFtTkConnHandle *handle,
                                         unsigned char opCode,
                                         unsigned short dataStructureId,
                                         INGwFtTkMessageBuf *message,
                                         unsigned int no_of_message);
};

#endif 
