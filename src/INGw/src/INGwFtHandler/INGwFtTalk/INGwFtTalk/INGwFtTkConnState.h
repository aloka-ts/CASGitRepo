/*------------------------------------------------------------------------------
         File: INGwFtTkConnState.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_CONN_STATE_H__
#define __BTK_CONN_STATE_H__

class INGwFtTkConnState
{
   public:

      enum LinkState
      {
         CONNECTING = 1,
         CONNECTED,
         DISCONNECTED,
         DISCONNECTING,
         INVALID
      };
};

#endif
