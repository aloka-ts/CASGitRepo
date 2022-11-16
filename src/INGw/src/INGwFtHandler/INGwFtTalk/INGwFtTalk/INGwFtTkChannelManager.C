/*------------------------------------------------------------------------------
         File: INGwFtTkChannelManager.C
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

#include <INGwFtTalk/INGwFtTkCommunicator.h>
#include <INGwFtTalk/INGwFtTkChannelManager.h>
#include <INGwFtTalk/INGwFtTkConnHandle.h>
#include <INGwFtTalk/INGwFtTkUtil.h>
#include <INGwFtTalk/INGwFtTkRef.h>
#include <INGwFtTalk/INGwFtTkChannel.h>
#include <INGwFtTalk/INGwFtTkConnHandlerInf.h>
#include <unistd.h>
#include <stdlib.h>

INGwFtTkChannelManager::INGwFtTkChannelManager()
{
   pthread_mutex_init(&_lock, NULL);
}

INGwFtTkChannelManager::~INGwFtTkChannelManager()
{
   pthread_mutex_destroy(&_lock);
}

INGwFtTkChannel * INGwFtTkChannelManager::getChannel(INGwFtTkConnHandle *handle)
{
   unsigned int idx = handle->_locID;

   if(idx >= BTK_PRE_CHANNEL_BUF)
   {
      logger.logMsg(ERROR_FLAG, 0, "Invalid handle. IDX [%d]", idx);
      return NULL;
   }

   {
      INGwFtTkGuard(&_lock);

      INGwFtTkChannelHolder &currholder = channels[idx];

      if(currholder.handle != handle)
      {
         logger.logMsg(ERROR_FLAG, 0, "Reference handle is lost. InHandler [%x]"
                                      " MgrHandler [%x]", 
                       handle, currholder.handle);
         return NULL;
      }

      currholder.channel->addRef();
      return currholder.channel;
   }

   return NULL;
}

int INGwFtTkChannelManager::getChannelCount(unsigned int peerID)
{
   INGwFtTkGuard(&_lock);

   int ret = 0;

   for(int idx = 0; idx < BTK_PRE_CHANNEL_BUF; idx++)
   {
      if(channels[idx].peerID.getComponentId() == peerID)
      {
         ret++;
      }
   }

   logger.logMsg(TRACE_FLAG, 0, "Channel Count[%d] peer [%d]", ret, peerID);
   return ret;
}

INGwFtTkChannel * INGwFtTkChannelManager::getChannel(unsigned int peerID)
{
   INGwFtTkGuard(&_lock);

   for(int idx = 0; idx < BTK_PRE_CHANNEL_BUF; idx++)
   {
      if(channels[idx].peerID.getComponentId() == peerID)
      {
         channels[idx].channel->addRef();
         return channels[idx].channel;
      }
   }

   logger.logMsg(ERROR_FLAG, 0, "Channel Mgr doesnt hold peer [%d]", peerID);
   return NULL;
}

int INGwFtTkChannelManager::closeChannel(INGwFtTkConnHandle *handle)
{
   INGwFtTkChannel *currchannel = getChannel(handle);

   if(currchannel == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Error getting handle.");
      return -1;
   }

   INGwFtTkRefObjHolder holder(currchannel);

   unsigned int idx = handle->_locID;

   {
      INGwFtTkGuard(&_lock);

      INGwFtTkChannelHolder &currholder = channels[idx];

      if(currholder.handle != handle)
      {
         logger.logMsg(ERROR_FLAG, 0, "Reference handle is lost. InHandler [%x]"
                                      " MgrHandler [%x]", 
                       handle, currholder.handle);
         return -1;
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Peer [%d-%d] is removed from ChannelMgr.",
                    currholder.peerID.getComponentId(), 
                    currholder.peerID.getSubComponentId());

      currholder.peerID = 0;
      currholder.handle = NULL;
      currholder.channel->removeRef();
      currholder.channel = NULL;
   }

   INGwFtTkChannel *lookupChannel = getChannel(handle->_peerID.getComponentId());

   if(lookupChannel == NULL)
   {
      currchannel->resetHandlers();
      currchannel->disconnect();
      return 0;
   }

   INGwFtTkRefObjHolder lookupObjHoler(lookupChannel);

   return 0;
}

int INGwFtTkChannelManager::closeChannel(unsigned int peerID)
{
   INGwFtTkChannel *currchannel = getChannel(peerID);

   if(currchannel == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Error getting handle.");
      return -1;
   }

   INGwFtTkRefObjHolder holder(currchannel);
   currchannel->resetHandlers();
   currchannel->disconnect();

   {
      INGwFtTkGuard(&_lock);

      bool found = false;
      for(int idx = 0; idx < BTK_PRE_CHANNEL_BUF; idx++)
      {
         if(channels[idx].peerID.getComponentId() == peerID)
         {
            channels[idx].peerID = 0;
            channels[idx].handle = NULL;
            channels[idx].channel->removeRef();
            channels[idx].channel = NULL;

            found = true;
         }
      }

      if(found)
      {
         return 0;
      }

      logger.logMsg(ERROR_FLAG, 0, "Channel Mgr doesnt hold peer [%d]", peerID);
   }

   return -1;
}

INGwFtTkChannel * INGwFtTkChannelManager::openChannel(ObjectId peerID, int sockID, 
                                            int ver,
                                            INGwFtTkCreationStatus &creationStatus)
{
   INGwFtTkGuard(&_lock);

   for(int idx = 0; idx < BTK_PRE_CHANNEL_BUF; idx++)
   {
      if(channels[idx].peerID == peerID)
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Peer [%d-%d] channel exists.", 
                       peerID.getComponentId(), peerID.getSubComponentId());
         creationStatus = ALREADY_EXIST;
         channels[idx].channel->addRef();
         return channels[idx].channel;
      }
   }

   creationStatus = CREATED;

   INGwFtTkChannel *newChannel = NULL;

   for(int idx = 0; idx < BTK_PRE_CHANNEL_BUF; idx++)
   {
      if(channels[idx].peerID.getComponentId() == peerID.getComponentId())
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Peer [%d] channel exists.", 
                       peerID.getComponentId());
         creationStatus = ALREADY_EXIST;

         if(channels[idx].peerID.getSubComponentId() == 0)
         {
            channels[idx].peerID = peerID;
            channels[idx].channel->addRef();
            return channels[idx].channel;
         }

         newChannel = channels[idx].channel;
         break;
      }
   }

   if(newChannel == NULL)
   {
      newChannel = new INGwFtTkChannel(peerID.getComponentId(), sockID, ver);
   }
   else
   {
      newChannel->addRef();
   }

   for(int idx = 0; idx < BTK_PRE_CHANNEL_BUF; idx++)
   {
      if(channels[idx].peerID == 0)
      {
         channels[idx].peerID = peerID;
         channels[idx].handle = NULL;
         channels[idx].channel = newChannel;
         channels[idx].channel->addRef();
         return channels[idx].channel;
      }
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Too many open channels, cant find a "
                                 "location for new peer [%d]", 
                                 peerID.getComponentId());

   for(int idx = 0; idx < BTK_PRE_CHANNEL_BUF; idx++)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "IDX [%d] Peer [%d-%d]", idx, 
                    channels[idx].peerID.getComponentId(),
                    channels[idx].peerID.getSubComponentId());
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Quitting deliberatly.");
   printf("Too many open channels. Quitting deliberatly.\n");
   exit(1);

   return NULL;
}

int INGwFtTkChannelManager::setHandler(ObjectId peerID, INGwFtTkConnHandle *handle)
{
   INGwFtTkChannel *currchannel = getChannel(peerID.getComponentId());

   if(currchannel == NULL)
   {
      logger.logMsg(ERROR_FLAG, 0, "Error getting handle.");
      return -1;
   }

   INGwFtTkRefObjHolder holder(currchannel);

   {
      INGwFtTkGuard(&_lock);

      for(int idx = 0; idx < BTK_PRE_CHANNEL_BUF; idx++)
      {
         if(channels[idx].peerID == peerID)
         {
            if(channels[idx].handle != NULL)
            {
               logger.logMsg(ALWAYS_FLAG, 0, "Peer [%d-%d] handler overwritten",
                             peerID.getComponentId(), 
                             peerID.getSubComponentId());
            }

            handle->_locID = idx; 
            channels[idx].handle = handle;
            return 0;
         }
      }
   }

   logger.logMsg(ERROR_FLAG, 0, "Channel Mgr doesnt hold peer [%d]", 
                 peerID.getComponentId());
   return -1;
}

int INGwFtTkChannelManager::registerIncomingWatcher(unsigned int peerID, 
                                               INGwFtTkConnHandlerInf *inf)
{
   INGwFtTkGuard(&_lock);

   for(int idx = 0; idx < BTK_MAX_SUBSYS; idx++)
   {
      if(watchers[idx].peerID == peerID)
      {
         watchers[idx].inf = inf;
         return 0;
      }
   }

   for(int idx = 0; idx < BTK_MAX_SUBSYS; idx++)
   {
      if(watchers[idx].peerID == 0)
      {
         watchers[idx].inf = inf;
         watchers[idx].peerID = peerID;

         return 0;
      }
   }

   logger.logMsg(ERROR_FLAG, 0, "Too many watchers. ");

   for(int idx = 0; idx < BTK_MAX_SUBSYS; idx++)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Idx [%d] watch peer [%d]", 
                    idx, watchers[idx].peerID);
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Quitting deliberately.");
   printf("Too many watchers. Quitting deliberately.\n");
   exit(1);
   return -1;
}

int INGwFtTkChannelManager::deregisterIncomingWatcher(unsigned int peerID)
{
   INGwFtTkGuard(&_lock);

   for(int idx = 0; idx < BTK_MAX_SUBSYS; idx++)
   {
      if(watchers[idx].peerID == peerID)
      {
         watchers[idx].inf = NULL;
         return 0;
      }
   }

   return -1;
}

extern "C" void * INGwFtTkChannelManager::_connAcceptNotifierThread(void *indata)
{
   INGwFtTkCommunicator::setSignal();
   pthread_detach(pthread_self());

   INGwFtTkIncomingChannelHolder *data = (INGwFtTkIncomingChannelHolder *)indata;

   data->inf->connectionUpdate(data->peerID, INGwFtTkConnState::CONNECTED);
   delete data;
   return NULL;
}

void INGwFtTkChannelManager::channelAccepted(unsigned int peerID)
{
   INGwFtTkConnHandlerInf *inf = NULL;

   {
      INGwFtTkGuard(&_lock);

      for(int idx = 0; idx < BTK_MAX_SUBSYS; idx++)
      {
         if(watchers[idx].peerID == peerID)
         {
            inf = watchers[idx].inf;
         }
      }
   }

   if(inf != NULL)
   {

      INGwFtTkIncomingChannelHolder *data = new INGwFtTkIncomingChannelHolder();
      data->peerID = peerID;
      data->inf = inf;

      pthread_t notifier;
      pthread_create(&notifier, NULL, _connAcceptNotifierThread, data);
   }
}

