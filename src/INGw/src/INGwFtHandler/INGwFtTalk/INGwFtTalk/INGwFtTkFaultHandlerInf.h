/*------------------------------------------------------------------------------
         File: INGwFtTkFaultHandlerInf.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_FAULT_HANDLER_INF_H__
#define __BTK_FAULT_HANDLER_INF_H__

class INGwFtTkFaultHandlerInf
{
   public:

      virtual void bogusFault(unsigned int subsysID) = 0;
      virtual void doAction(unsigned int subsysID, unsigned int actionID) = 0;
};

#endif
