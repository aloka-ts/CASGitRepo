/*------------------------------------------------------------------------------
         File: INGwFtTkConnHandlerInf.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_CONN_HANDLER_INF_H__
#define __BTK_CONN_HANDLER_INF_H__

#include <INGwFtTalk/INGwFtTkConnState.h>

class INGwFtTkConnHandlerInf
{
   public:

      virtual void connectionUpdate(unsigned int subsystemID,
                                    INGwFtTkConnState::LinkState connState) = 0;
};

#endif
