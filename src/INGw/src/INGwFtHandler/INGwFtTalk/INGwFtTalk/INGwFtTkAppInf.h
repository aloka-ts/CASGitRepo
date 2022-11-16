/*------------------------------------------------------------------------------
         File: INGwFtTkAppInf.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_APP_INF_H__
#define __BTK_APP_INF_H__

typedef struct INGwFtTkMessageBuf
{
   void *msg;
   unsigned int len;
};

#endif
