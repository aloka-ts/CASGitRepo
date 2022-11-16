/*------------------------------------------------------------------------------
         File: INGwFtTkChannel.h
  Application: INGwFtTalk Communicator
    Component: INGwFtTalk
   Programmer: S.Suriya prakash
      Written: 21-Jun-2003
  Description: BayPackets App message transfer protocol
      History: Maintained in clearcase
    Copyright: 2003, BayPackets
------------------------------------------------------------------------------*/

#ifndef __BTK_CHANNEL_H__
#define __BTK_CHANNEL_H__

#include <INGwFtTalk/INGwFtTkRef.h>
#include <INGwFtTalk/INGwFtTkVersionHandlerInf.h>
#include <sys/time.h>

class INGwFtTkMsgHandlerInf;
class INGwFtTkConnHandlerInf;
class INGwFtTkSubCompMgr;

class INGwFtTkInstanceID
{
   public:

      int startTime;

      INGwFtTkInstanceID()
      {
         startTime = 0;
      }
};

class INGwFtTkEndPointInfo
{
   public:

      unsigned int IP;
      unsigned int port;
   
   public:

      INGwFtTkEndPointInfo()
      {
         IP = 0;
         port = 0;
      }
};

class INGwFtTkControlMsg
{
   public:

      //00-24  INGwFtTk internal Requests.
      //25-50  INGwFtTk internal Resposes.
      //51-500 APP Action codes.

      enum INGwFtTkControlMsgReq
      {
         BTK_OPEN_CONNECTION = 10,
         BTK_FAULT_MGR_QUERY,
         BTK_VERSION_NEGOTIATION,
         BTK_CLOSE_CONNECTION,
         BTK_MAX_ACTION = 500   //Just to ensure the MAX.
      };

      enum INGwFtTkControlMsgRes
      {
         BTK_CONN_ACCEPTED = 25,
         BTK_CONN_REJECT,
         BTK_RECONN_REJECT,
         BTK_SIMULTANEOUS_CONN,
         BTK_ACTION_ACCEPTED,
         BTK_QUERY_SUCCESS
      };

   public:

      unsigned int peerID;
      INGwFtTkInstanceID instanceID; 
      INGwFtTkEndPointInfo listenDetail;

      union
      {
         unsigned int action;
         unsigned int result;
      };

      INGwFtTkControlMsg()
      {
         peerID = 0;
         action = 0;
      }
};

typedef struct INGwFtTkConnDownNotification
{
   INGwFtTkConnHandlerInf *handler;
   unsigned int peerID;
};

class INGwFtTkChannel : public INGwFtTkRef
{
   public:

      enum INGwFtTkEstablishStatus
      {
         ESTABLISHED = 1,
         EST_SOCKET_ERROR,
         SIMULTANEOUS_OPEN,
         VERSION_PROB
      };

      enum INGwFtTkWriteErrorState
      {
         WR_SOCKET_ERROR = 1,
         WR_TIMEDOUT,
         WR_VERSION_PROB
      };

      enum INGwFtTkReadErrorState
      {
         RD_SOCKET_ERROR = 1,
         RD_VERSION_PROB
      };

      enum INGwFtTkListenErrorState
      {
         LIS_SOCKET_NOERROR = 1,
         LIS_SOCKET_ERROR
      };

      enum INGwFtTkActionSentState
      {
         ACT_SEND_SUCCESS = 1,
         ACT_SEND_FAILED
      };

      enum INGwFtTkFaultMgrQueryState
      {
         QUERY_SUCCESS,
         QUERY_FAILED,
         QUERY_TIMEOUT
      };

   private:
      
      pthread_mutex_t _lock;
      pthread_mutex_t _rtimeLock;
      pthread_mutex_t _wtimeLock;
      pthread_mutex_t _writeLock;

      int _version;

      unsigned int _peerID;
      INGwFtTkEndPointInfo _peerListenerInfo;
      INGwFtTkInstanceID   _peerInstance;
      
      static int miSoSndRcvBuff;

      int miBkRepSize;
      int _sock;
      bool _toCloseStatus;

      INGwFtTkMsgHandlerInf  *_msgHandler;
      INGwFtTkConnHandlerInf *_connHandler;

      hrtime_t _lastReadTime;
      hrtime_t _lastWriteTime;

      bool _appClose;

   private:

      INGwFtTkChannel(); //Used by Clone.

      static void * INGwFtTkChannelBrkNotifier(void *data);

      int _connect();

      void _updateWriteTime();
      void _updateReadTime();

      int _readToBuf(char *addr, int len, unsigned int timeout = 60);

   public:

      static void setOptions(int sockID);

   public:

      INGwFtTkChannel(unsigned int peerID, int sock, int version);
      virtual ~INGwFtTkChannel();

      int getVersion();

      void resetHandlers();
      INGwFtTkChannel * clone();

      int establishConnection(INGwFtTkControlMsg selfDetail, 
                              INGwFtTkEstablishStatus &result);
      int sendAction(INGwFtTkControlMsg selfDetail, int action, 
                     INGwFtTkActionSentState &result);
      int negotiateVersion(INGwFtTkControlMsg selfDetail, int subCompID,
                           const INGwFtTkVersionSet &supportedVersion);
      int makeFaultQuery(INGwFtTkControlMsg selfDetail, 
                         INGwFtTkFaultMgrQueryState &result);
      int sendAry(struct iovec *, int len, INGwFtTkWriteErrorState &result,
                  unsigned int timeout = 60);
      int readMsg(INGwFtTkSubCompMgr *subCompMgr, INGwFtTkReadErrorState &result);
      INGwFtTkControlMsg listen(int &acceptedSock, int &acceptedVersion,
                           INGwFtTkListenErrorState &result);

      void disconnect();

      INGwFtTkMsgHandlerInf *getMessageHandler();
      void setMessageHandler(INGwFtTkMsgHandlerInf *);

      INGwFtTkConnHandlerInf *getConnHandler();
      void setConnHandler(INGwFtTkConnHandlerInf *);

      void setInstance(INGwFtTkInstanceID id);

      void setEndPointInfo(INGwFtTkEndPointInfo);
      INGwFtTkEndPointInfo getEndPointInfo();

      bool isChannelValid();

      bool isAppClose();
      void setAppClose(bool flag);

      hrtime_t getLastWriteTime();
      hrtime_t getLastReadTime();

      int sendControlMsg(const INGwFtTkControlMsg &msg, int timeout,
                         INGwFtTkWriteErrorState &wrResult, 
                         int sock = 0, int version = 0);

      int recvControlMsg(INGwFtTkControlMsg &msg, int timeout, 
                         INGwFtTkReadErrorState &rdResult, 
                         int sock = 0, int version = 0);

      int readVersionInfo(int &subComponentID, INGwFtTkVersionSet &peerVersions, 
                          int timeout, INGwFtTkReadErrorState &rdResult, 
                          int sock = 0, int version = 0);

   public:

      friend class INGwFtTkFaultManager;
};

#endif
