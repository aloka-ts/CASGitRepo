/*------------------------------------------------------------------------------
         File: INGwFtTkChannelManager.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_CHANNEL_MANAGER_H__
#define __BTK_CHANNEL_MANAGER_H__

#include <Util/ObjectId.h>
#include <pthread.h>

#define BTK_PRE_CHANNEL_BUF 1000
#define BTK_MAX_SUBSYS 200

class INGwFtTkChannel;
class INGwFtTkConnHandle;
class INGwFtTkConnHandlerInf;

class INGwFtTkChannelManager
{
   public:

      enum INGwFtTkCreationStatus
      {
         CREATED = 1,
         ALREADY_EXIST
      };

   private:

      class INGwFtTkChannelHolder
      {
         public:

            INGwFtTkChannel    *channel;
            INGwFtTkConnHandle *handle;
            ObjectId      peerID;

            INGwFtTkChannelHolder()
            {
               channel = NULL;
               handle = NULL;
               peerID = 0;
            }
      };

      class INGwFtTkIncomingChannelHolder
      {
         public:

            unsigned int peerID;
            INGwFtTkConnHandlerInf *inf;

            INGwFtTkIncomingChannelHolder()
            {
               peerID = 0;
               inf = NULL;
            }
      };

   private:

      pthread_mutex_t _lock;

      INGwFtTkChannelHolder channels[BTK_PRE_CHANNEL_BUF];
      INGwFtTkIncomingChannelHolder watchers[BTK_MAX_SUBSYS];

   private:

      static void * _connAcceptNotifierThread(void *);

   public:

      INGwFtTkChannelManager();
      ~INGwFtTkChannelManager();

      INGwFtTkChannel * getChannel(INGwFtTkConnHandle *);
      INGwFtTkChannel * getChannel(unsigned int peerID);
      int getChannelCount(unsigned int peerID);

      int closeChannel(INGwFtTkConnHandle *);
      int closeChannel(unsigned int peerID);

      INGwFtTkChannel * openChannel(ObjectId peerID, int sockID, int version,
                               INGwFtTkCreationStatus &creationStatus);
      int setHandler(ObjectId peerID, INGwFtTkConnHandle *handle);

      int registerIncomingWatcher(unsigned int peerID, INGwFtTkConnHandlerInf *inf);
      int deregisterIncomingWatcher(unsigned int peerID);

      void channelAccepted(unsigned int peerID);
};

#endif
