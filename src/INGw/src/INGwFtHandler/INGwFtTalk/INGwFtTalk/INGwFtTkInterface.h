/*------------------------------------------------------------------------------
         File: INGwFtTkInterface.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_INTERFACE_H__
#define __BTK_INTERFACE_H__

#include <Util/ObjectId.h>
#include <Util/QueueMgr.h>

#include <INGwFtTalk/INGwFtTkAppInf.h>
#include <INGwFtTalk/INGwFtTkConnHandle.h>
#include <INGwFtTalk/INGwFtTkConnHandlerInf.h>
#include <INGwFtTalk/INGwFtTkConnState.h>
#include <INGwFtTalk/INGwFtTkFaultHandlerInf.h>
#include <INGwFtTalk/INGwFtTkMsgHandlerInf.h>
#include <INGwFtTalk/INGwFtTkReceiverQueue.h>
#include <INGwFtTalk/INGwFtTkRef.h>
#include <INGwFtTalk/INGwFtTkVersionHandlerInf.h>

class INGwFtTkInterface
{
   private:

      static INGwFtTkInterface *_selfRef;

   private:

      INGwFtTkInterface(INGwFtTkVersionHandlerInf *);

   public:

      static INGwFtTkInterface * instance(INGwFtTkVersionHandlerInf * = NULL);

   public:

      INGwFtTkConnHandle * openConnection(ObjectId selfID, ObjectId peerID);
      void closeConnection(INGwFtTkConnHandle *);

      int sendMessageMultiBufMsg(INGwFtTkConnHandle *handle, unsigned char opCode,
                                 unsigned short dataStructureId, 
                                 INGwFtTkMessageBuf *message, 
                                 unsigned int no_of_message, int version);

      int sendMessageMultiMsg(INGwFtTkConnHandle *handle, unsigned char opCode,
                              unsigned short dataStructureId, 
                              INGwFtTkMessageBuf *message, 
                              unsigned int no_of_message, int version);

      INGwFtTkReceiverQueue * registerMessageReceiver(ObjectId selfID);
      void deregisterMessageReceiver(INGwFtTkReceiverQueue *);

      void registerFaultCallback(INGwFtTkFaultHandlerInf *);
      void deregisterFaultCallback();

      int registerIncomingWatcher(unsigned int peerID, INGwFtTkConnHandlerInf *inf);
      int deregisterIncomingWatcher(unsigned int peerID);

      void sendAction(unsigned int peerID, unsigned int actionID);

      int negotiateVersion(const ObjectId &peer, 
                           const INGwFtTkVersionSet &supportedVersion);
};

#endif
