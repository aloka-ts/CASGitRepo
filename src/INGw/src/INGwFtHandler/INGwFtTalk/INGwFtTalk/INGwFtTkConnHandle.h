/*------------------------------------------------------------------------------
         File: INGwFtTkConnHandle.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_CONN_HANDLE_H__
#define __BTK_CONN_HANDLE_H__

#include <Util/ObjectId.h>
#include <INGwFtTalk/INGwFtTkConnState.h>

class INGwFtTkChannelManager;
class INGwFtTkConnHandlerInf;
class INGwFtTkMsgHandlerInf;

class INGwFtTkTempLink
{
   private:
      
      INGwFtTkConnState::LinkState _state;

   public:

      INGwFtTkTempLink(INGwFtTkConnState::LinkState inState)
      {
         _state = inState;
      }

      INGwFtTkConnState::LinkState getLinkState()
      {
         return _state;
      }
};

class INGwFtTkConnHandle
{
   private:

      unsigned int _locID;
      ObjectId _peerID;
   
   public:

      void setConnectionHandler(INGwFtTkConnHandlerInf *);
      void setMessageHandler(INGwFtTkMsgHandlerInf *);

      ObjectId getPeerID();

      INGwFtTkTempLink getConnState();

   private:

      INGwFtTkConnHandle(ObjectId inPeerID);

   public:

      friend class INGwFtTkChannelManager;
      friend class INGwFtTkCommunicator;
};

#endif
