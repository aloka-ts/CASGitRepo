/*------------------------------------------------------------------------------
         File: INGwFtTkConnHandle.C
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
#include <INGwFtTalk/INGwFtTkConnHandle.h>
#include <INGwFtTalk/INGwFtTkRef.h>
#include <INGwFtTalk/INGwFtTkChannel.h>
#include <INGwFtTalk/INGwFtTkCommunicator.h>
#include <INGwFtTalk/INGwFtTkChannelManager.h>

INGwFtTkConnHandle::INGwFtTkConnHandle(ObjectId inPeerID)
{
   _locID = 0;
   _peerID = inPeerID;
}

void INGwFtTkConnHandle::setConnectionHandler(INGwFtTkConnHandlerInf *inf)
{
   INGwFtTkChannel *currChannel = 
                   INGwFtTkCommunicator::getInstance()._channelMgr->getChannel(this);
   if(currChannel == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to find the channel.");
      return;
   }

   INGwFtTkRefObjHolder holder(currChannel);

   currChannel->setConnHandler(inf);
}

void INGwFtTkConnHandle::setMessageHandler(INGwFtTkMsgHandlerInf *inf)
{
   INGwFtTkChannel *currChannel = 
                   INGwFtTkCommunicator::getInstance()._channelMgr->getChannel(this);
   if(currChannel == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to find the channel.");
      return;
   }

   INGwFtTkRefObjHolder holder(currChannel);

   currChannel->setMessageHandler(inf);
}

ObjectId INGwFtTkConnHandle::getPeerID()
{
   return _peerID;
}

INGwFtTkTempLink INGwFtTkConnHandle::getConnState()
{
   INGwFtTkChannel *currChannel = 
                   INGwFtTkCommunicator::getInstance()._channelMgr->getChannel(this);
   if(currChannel == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to find the channel.");
      return INGwFtTkTempLink(INGwFtTkConnState::DISCONNECTED);
   }

   INGwFtTkRefObjHolder holder(currChannel);

   if(currChannel->isChannelValid())
   {
      return INGwFtTkTempLink(INGwFtTkConnState::CONNECTED);
   }

   return INGwFtTkTempLink(INGwFtTkConnState::DISCONNECTED);
}
