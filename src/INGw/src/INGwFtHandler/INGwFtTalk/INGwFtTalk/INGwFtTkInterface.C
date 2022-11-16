/*------------------------------------------------------------------------------
         File: INGwFtTkInterface.C
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
#include <INGwFtTalk/INGwFtTkInterface.h>
#include <INGwFtTalk/INGwFtTkCommunicator.h>
#include <INGwFtTalk/INGwFtTkHostMonitor.h>

INGwFtTkInterface * INGwFtTkInterface::_selfRef = NULL;

INGwFtTkInterface::INGwFtTkInterface(INGwFtTkVersionHandlerInf *verInf)
{
   _selfRef = this;
   new INGwFtTkCommunicator(verInf);
}

INGwFtTkInterface * INGwFtTkInterface::instance(INGwFtTkVersionHandlerInf *verInf)
{
   if(_selfRef == NULL)
   {
      new INGwFtTkHostMonitor();
      new INGwFtTkInterface(verInf);
   }

   return _selfRef;
}

INGwFtTkConnHandle * INGwFtTkInterface::openConnection(ObjectId selfID, ObjectId peerID)
{
   return INGwFtTkCommunicator::getInstance().openConnection(peerID);
}

void INGwFtTkInterface::closeConnection(INGwFtTkConnHandle *handle)
{
   if(handle == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Null handle to INGwFtTalk closeConn.");
      return;
   }

   INGwFtTkCommunicator::getInstance().closeConnection(handle);
}

int INGwFtTkInterface::sendMessageMultiBufMsg(INGwFtTkConnHandle *handle, 
                                         unsigned char opCode,
                                         unsigned short dataStructureId,
                                         INGwFtTkMessageBuf *message,
                                         unsigned int no_of_message, 
                                         int version)
{
   if(handle == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Null handle to INGwFtTalk sendMsg.");
      return -1;
   }

   return INGwFtTkCommunicator::getInstance().sendMessageMultiBufMsg(handle, opCode, 
                                                                dataStructureId,
                                                                message, 
                                                                no_of_message,
                                                                version);
}

int INGwFtTkInterface::sendMessageMultiMsg(INGwFtTkConnHandle *handle, 
                                      unsigned char opCode,
                                      unsigned short dataStructureId,
                                      INGwFtTkMessageBuf *message,
                                      unsigned int no_of_message, int version)
{
   if(handle == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Null handle to INGwFtTalk sendMsg.");
      return -1;
   }

   return INGwFtTkCommunicator::getInstance().sendMessageMultiMsg(handle, opCode, 
                                                             dataStructureId, 
                                                             message, 
                                                             no_of_message,
                                                             version);
}

INGwFtTkReceiverQueue * INGwFtTkInterface::registerMessageReceiver(ObjectId selfID)
{
   return INGwFtTkCommunicator::getInstance().registerMessageReceiver(selfID);
}

void INGwFtTkInterface::deregisterMessageReceiver(INGwFtTkReceiverQueue *queue)
{
   if(queue == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Null queue to INGwFtTalk deRegister.");
      return;
   }

   return INGwFtTkCommunicator::getInstance().deregisterMessageReceiver(queue);
}

void INGwFtTkInterface::registerFaultCallback(INGwFtTkFaultHandlerInf *inf)
{
   INGwFtTkCommunicator::getInstance().registerFaultCallback(inf);
}

void INGwFtTkInterface::deregisterFaultCallback()
{
   INGwFtTkCommunicator::getInstance().deregisterFaultCallback();
}

int INGwFtTkInterface::registerIncomingWatcher(unsigned int peerID, 
                                          INGwFtTkConnHandlerInf *inf)
{
   return INGwFtTkCommunicator::getInstance().registerIncomingWatcher(peerID, inf);
}

int INGwFtTkInterface::deregisterIncomingWatcher(unsigned int peerID)
{
   return INGwFtTkCommunicator::getInstance().deregisterIncomingWatcher(peerID);
}


void INGwFtTkInterface::sendAction(unsigned int peerID, unsigned int actionID)
{
   INGwFtTkCommunicator::getInstance().sendAction(peerID, actionID);
}

int INGwFtTkInterface::negotiateVersion(const ObjectId &peer, 
                                   const INGwFtTkVersionSet &supportedVersion)
{
   return INGwFtTkCommunicator::getInstance().negotiateVersion(peer, 
                                                          supportedVersion);
}
