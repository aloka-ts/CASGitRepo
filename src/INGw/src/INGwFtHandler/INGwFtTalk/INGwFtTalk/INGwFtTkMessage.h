/*------------------------------------------------------------------------------
         File: INGwFtTkMessage.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_MESSAGES_H__
#define __BTK_MESSAGES_H__

typedef struct INGwFtTkMesgHeader_v1
{
   int version;
   unsigned short instanceID;
   unsigned short subComponentID;
   unsigned short opCode;
   unsigned short dataStructureID;
   int msgLen;
};

typedef union INGwFtTkMesgHeader
{
   INGwFtTkMesgHeader_v1 v1Msg;
};

typedef struct INGwFtTkReceivedMsg
{
   int version;
   INGwFtTkMesgHeader header;
   char *messageBuf;
};

#endif
