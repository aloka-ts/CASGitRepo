/*------------------------------------------------------------------------------
         File: INGwFtTkCommunicator.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_COMMUNUCATOR_H__
#define __BTK_COMMUNUCATOR_H__

#include <Util/ObjectId.h>
#include <INGwFtTalk/INGwFtTkSingleton.h>
#include <INGwFtTalk/INGwFtTkVersionHandlerInf.h>

class INGwFtTkSubCompMgr;
class INGwFtTkChannelManager;
class INGwFtTkConnHandle;
class INGwFtTkMessageBuf;
class INGwFtTkReceiverQueue;
class INGwFtTkFaultHandlerInf;
class INGwFtTkConnHandlerInf;
class INGwFtTkEndPointInfo;
class INGwFtTkFaultManager;
class INGwFtTkChannel;
class INGwFtTkControlMsg;

#define BTK_MIN_APP_ACTION 50

class INGwFtTkCommunicator : public INGwFtTkSingleton<INGwFtTkCommunicator>
{
   private:

      INGwFtTkSubCompMgr        *_subCompMgr;
      INGwFtTkChannelManager    *_channelMgr;
      INGwFtTkFaultManager      *_faultMgr;
      int _sysID;

      INGwFtTkControlMsg *_selfAbout;

      INGwFtTkFaultHandlerInf *_faultCallback;
      INGwFtTkVersionHandlerInf *_versionInf;

      INGwFtTkChannel *_listenChannel;

      INGwFtTkVersionSet _versionSet;

   public:

      INGwFtTkCommunicator(INGwFtTkVersionHandlerInf *versionInf);
      ~INGwFtTkCommunicator();

   public:

      INGwFtTkConnHandle * openConnection(ObjectId peerID);
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

      int sendAction(unsigned int peerID, unsigned int actionID);

      int negotiateVersion(const ObjectId &peer, 
                           const INGwFtTkVersionSet &supportedVersion);

      static void setSignal();

      const INGwFtTkVersionSet & getVersionSet();

   private:

      static void * _listenerThreadStart(void *);
      static void * _readerThreadStart(void *peerDetail);
      static void * _actionNotifier(void *actionDetail);
      static void * _versionHandlerThreadStart(void *sockDetail);

      int _initialize();
      int _getINGwFtTkDetail(unsigned int peerID, INGwFtTkEndPointInfo *peerDetail);

      void _listen();
      void _read(unsigned int peerID);

      void _startReader(unsigned int peerID);
      void _notifyAction(unsigned int peerID, unsigned int actionID);

      int _sendAction(unsigned int peerID, unsigned int actionID, 
                      INGwFtTkEndPointInfo *);

      void _handleVersionQuery(int peerID, int acceptedSock, int acceptedVer);

   public:

      friend class INGwFtTkConnHandle;
      friend class INGwFtTkFaultManager;
};

#endif
