/*------------------------------------------------------------------------------
         File: INGwFtTkChannel.C
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
#include <INGwFtTalk/INGwFtTkChannel.h>
#include <INGwFtTalk/INGwFtTkConnState.h>
#include <INGwFtTalk/INGwFtTkMsgHandlerInf.h>
#include <INGwFtTalk/INGwFtTkConnHandlerInf.h>
#include <INGwFtTalk/INGwFtTkSubCompMgr.h>
#include <INGwFtTalk/INGwFtTkReceiverQueue.h>
#include <INGwFtTalk/INGwFtTkRef.h>
#include <INGwFtTalk/INGwFtTkUtil.h>
#include <INGwFtTalk/INGwFtTkMessage.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <sys/uio.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <net/if.h>
#include <arpa/inet.h>
#include <inttypes.h>
#include <netdb.h>
#include <string.h>
#include <pthread.h>
#include <errno.h>
#include <unistd.h>
#include <stdlib.h>
#include <limits.h>
#include <fcntl.h>
#include <string>

int INGwFtTkChannel::miSoSndRcvBuff=49152;
INGwFtTkChannel::INGwFtTkChannel(unsigned int peerID, int sockID, int version)
{
   _peerID = peerID;
   _toCloseStatus = true;

   if(sockID != 0)
   {
      _sock = sockID;
   }
   else
   {
      _sock = socket(AF_INET, SOCK_STREAM, 0);

      if(_sock == -1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Socket creation failed. [%s]", 
                       strerror(errno));
         printf("Socket creation failed. [%s] Quitting.\n", strerror(errno));
         exit(1);
      }
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Channel [%x] Peer[%d] Sock[%d] constructed.",
                 this, peerID, _sock);

//888
   miSoSndRcvBuff = 49152;
   std::string isDefined;
   char *lpcBrSize = getenv("INGW_BK_REP_SIZE");

   if(NULL != lpcBrSize) {
     miBkRepSize = (atoi(lpcBrSize) * 1024);
     isDefined = "defined";
   }
   else {
     miBkRepSize = (1024 * 1024);
     isDefined = "not defined";
   }
 
   logger.logMsg(ALWAYS_FLAG,0,"INGwFtTkChannel::INGwFtTkChannel "
   "INGW_BK_REP_SIZE %s, miBkRepSize<%d> Bytes",
    isDefined.c_str(), miBkRepSize);

   char *lpcSndRcvBuf = getenv("INGW_TCP_SND_RCV");
   if(NULL != lpcSndRcvBuf) {
     miSoSndRcvBuff = atoi(lpcSndRcvBuf);
     logger.logMsg(ALWAYS_FLAG,0,"INGwFtTkChannel::INGwFtTkChannel"
                       " miSoSndRcvBuff<%d>",miSoSndRcvBuff);
   }
   else
   {
     int liSndRcvbuf = 0;
     int lenInt = sizeof(liSndRcvbuf);

     if(getsockopt(_sock, SOL_SOCKET, SO_SNDBUF, &liSndRcvbuf, &lenInt) < 0)
     {
        logger.logMsg(ERROR_FLAG, 0, "Error reading sock property. [%s]",
                      strerror(errno));
        disconnect();
     }
     else 
     {
        logger.logMsg(ALWAYS_FLAG,0,"getsockopts SO_SNDBUF<%d>",
                          liSndRcvbuf);
     }

     liSndRcvbuf = 0;

     if(getsockopt(_sock, SOL_SOCKET, SO_RCVBUF, &liSndRcvbuf, &lenInt) < 0)
     {
        logger.logMsg(ERROR_FLAG, 0, "Error reading sock property. [%s]",
                      strerror(errno));
        disconnect();
     }
     else 
     {
        logger.logMsg(ALWAYS_FLAG,0,"getsockopts SO_RCVBUF<%d>",
                          liSndRcvbuf);
     }
   }

   setOptions(_sock);

   _msgHandler = NULL;
   _connHandler = NULL;

   _lastReadTime = 0;
   _lastWriteTime = 0;
   _version = version;

   _appClose = false;

   pthread_mutex_init(&_lock, NULL);
   pthread_mutex_init(&_rtimeLock, NULL);
   pthread_mutex_init(&_wtimeLock, NULL);
   pthread_mutex_init(&_writeLock, NULL);
}

INGwFtTkChannel::INGwFtTkChannel()
{
   _peerID = 0;
   _toCloseStatus = false;
   _sock = 0;

   _msgHandler = NULL;
   _connHandler = NULL;

   _lastReadTime = 0;
   _lastWriteTime = 0;
   _version = 0;

   _appClose = false;

   pthread_mutex_init(&_lock, NULL);
   pthread_mutex_init(&_rtimeLock, NULL);
   pthread_mutex_init(&_wtimeLock, NULL);
   pthread_mutex_init(&_writeLock, NULL);
}

INGwFtTkChannel::~INGwFtTkChannel()
{
   logger.logMsg(ALWAYS_FLAG, 0, "Destruction channel obj for [%d] sock [%d]", 
                 _peerID, _sock);
   disconnect();

   pthread_mutex_destroy(&_lock);
   pthread_mutex_destroy(&_rtimeLock);
   pthread_mutex_destroy(&_wtimeLock);
   pthread_mutex_destroy(&_writeLock);
}

int INGwFtTkChannel::getVersion()
{
   return _version;
}

void INGwFtTkChannel::setOptions(int sockID)
{
   int flags;

   if((flags = fcntl(sockID, F_GETFL, 0)) < 0)
   {
      logger.logMsg(ERROR_FLAG, 0, "Error getting socket status. [%s]",
                    strerror(errno));
      printf("Error getting socket status. [%s]\n", strerror(errno));

      exit(1);
   }

   flags |= O_NONBLOCK;

   if(fcntl(sockID, F_SETFL, flags) < 0)
   {
      logger.logMsg(ERROR_FLAG, 0, "Error setting nonBlocking. [%s]",
                    strerror(errno));
      printf("Error setting nonBlocking. [%s]\n", strerror(errno));

      exit(1);
   }

   int flag = 1;
   if(setsockopt(sockID, IPPROTO_TCP, TCP_NODELAY, &flag, sizeof(int)) < 0)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to disable Nagle algorithm. [%s]",
                    strerror(errno));
      printf("Unable to disable Nagle algorithm. [%s]\n", strerror(errno));
      exit(1);
   }

   logger.logMsg(VERBOSE_FLAG,0,"miSoSndRcvBuff <%d>",
                                                             miSoSndRcvBuff);

   if(setsockopt(sockID, SOL_SOCKET, SO_SNDBUF, 
                 &miSoSndRcvBuff, sizeof(int)) < 0)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable set SO_SNDBUF. [%s]",
                    strerror(errno));

      printf("Unable to set SO_SNDBUF. [%s]\n", strerror(errno));
      exit(1);
   }

   if(setsockopt(sockID, SOL_SOCKET, SO_RCVBUF, 
                 &miSoSndRcvBuff, sizeof(int)) < 0)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to set SO_RCVBUF. [%s]",
                    strerror(errno));
      printf("Unable to set SO_RCVBUF. [%s]\n", strerror(errno));
      exit(1);
   }
}

void INGwFtTkChannel::resetHandlers()
{
   _msgHandler = NULL;
   _connHandler = NULL;
}

INGwFtTkChannel * INGwFtTkChannel::clone()
{
   INGwFtTkChannel *ret = new INGwFtTkChannel();

   ret->_peerID = _peerID;
   ret->_peerListenerInfo = _peerListenerInfo;
   ret->_peerInstance = _peerInstance;
   ret->_version = _version;
   ret->_appClose = _appClose;

   return ret;
}

extern "C" void * INGwFtTkChannel::INGwFtTkChannelBrkNotifier(void *data)
{
   INGwFtTkCommunicator::setSignal();
   pthread_detach(pthread_self());
   INGwFtTkConnDownNotification *notification = (INGwFtTkConnDownNotification *)data;

   notification->handler->connectionUpdate(notification->peerID, 
                                           INGwFtTkConnState::DISCONNECTED);
   delete notification;
   return NULL;
}

void INGwFtTkChannel::disconnect()
{
   INGwFtTkConnDownNotification *notification = NULL;

   pthread_mutex_lock(&_lock);

   if(_toCloseStatus)
   {
      if(close(_sock) == -1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Close syscall failed sock [%d]. [%s]",
                       _sock, strerror(errno));
      }

      _toCloseStatus = false;

      logger.logMsg(ALWAYS_FLAG, 0, "Subsys [%d] socket connection [%d] "
                                    "closed.", _peerID, _sock);

      if(_connHandler != NULL)
      {
         notification = new INGwFtTkConnDownNotification;
         notification->handler = _connHandler;
         notification->peerID = _peerID;
      }
   }

   pthread_mutex_unlock(&_lock);

   if(notification)
   {
      pthread_t notifier;
      pthread_create(&notifier, NULL, INGwFtTkChannelBrkNotifier, notification);
   }
}

INGwFtTkMsgHandlerInf * INGwFtTkChannel::getMessageHandler()
{
   INGwFtTkGuard(&_lock);
   return _msgHandler;
}

void INGwFtTkChannel::setMessageHandler(INGwFtTkMsgHandlerInf *inHandler)
{
   INGwFtTkGuard(&_lock);
   _msgHandler = inHandler;
}

INGwFtTkConnHandlerInf * INGwFtTkChannel::getConnHandler()
{
   INGwFtTkGuard(&_lock);
   return _connHandler;
}

void INGwFtTkChannel::setConnHandler(INGwFtTkConnHandlerInf *inHandler)
{
   INGwFtTkGuard(&_lock);
   _connHandler = inHandler;
}

void INGwFtTkChannel::setInstance(INGwFtTkInstanceID id)
{
   INGwFtTkGuard(&_lock);
   _peerInstance = id;
}

void INGwFtTkChannel::setEndPointInfo(INGwFtTkEndPointInfo info)
{
   INGwFtTkGuard(&_lock);
   _peerListenerInfo = info;
}

INGwFtTkEndPointInfo INGwFtTkChannel::getEndPointInfo()
{
   INGwFtTkGuard(&_lock);
   return _peerListenerInfo;
}

void INGwFtTkChannel::_updateWriteTime()
{
   INGwFtTkGuard(&_wtimeLock);
   _lastWriteTime = gethrtime();
}

void INGwFtTkChannel::_updateReadTime()
{
   INGwFtTkGuard(&_rtimeLock);
   _lastReadTime = gethrtime();
}

hrtime_t INGwFtTkChannel::getLastWriteTime()
{
   INGwFtTkGuard(&_wtimeLock);
   return _lastWriteTime;
}

hrtime_t INGwFtTkChannel::getLastReadTime()
{
   INGwFtTkGuard(&_rtimeLock);
   return _lastReadTime;
}

bool INGwFtTkChannel::isChannelValid()
{
   INGwFtTkGuard(&_lock);
   return _toCloseStatus;
}

void INGwFtTkChannel::setAppClose(bool flag)
{
   INGwFtTkGuard(&_lock);
   _appClose = flag;
}

bool INGwFtTkChannel::isAppClose()
{
   INGwFtTkGuard(&_lock);
   return _appClose;
}

int INGwFtTkChannel::_connect()
{
   int timeout = 10; //10 Secs.

   struct sockaddr_in srvAddr;
   memset(&srvAddr, 0, sizeof(srvAddr));
   srvAddr.sin_family = AF_INET;
   srvAddr.sin_port = htons((short)_peerListenerInfo.port);
   srvAddr.sin_addr.s_addr = htonl(_peerListenerInfo.IP);

   errno = 0;

   int retVal = 0;
   fd_set read_fds;   
   fd_set write_fds;  

   if((retVal = 
       connect(_sock, (struct sockaddr *)&srvAddr, sizeof(srvAddr))) < 0)
   {
      if(errno != EINPROGRESS)
      {
         logger.logMsg(ERROR_FLAG, 0, "_connect(): Error connecting server. [%s]", 
                       strerror(errno));
         disconnect();
         return -1;
      }

      while(true)
      {
         errno = 0;

         FD_ZERO(&read_fds);   FD_SET(_sock, &read_fds);
         FD_ZERO(&write_fds);  FD_SET(_sock, &write_fds);

         struct timeval time_out;
         time_out.tv_sec = timeout;
         time_out.tv_usec = 0;

         retVal = select(_sock + 1, &read_fds, &write_fds, NULL, &time_out);

         if((retVal == -1) && (errno == EINTR))
         {
            logger.logMsg(ERROR_FLAG, 0, "_connect(): Select interrupted. [%s]",
                          strerror(errno));
            continue;
         }

         break;
      }

      if(retVal == 0)
      {
         logger.logMsg(ERROR_FLAG, 0, "_connect(): Connect failed, Timedout. [%s]",
                       strerror(errno));
         disconnect();
         return -2;
      }

      if(retVal == -1)
      {
         logger.logMsg(ERROR_FLAG, 0, "_connect(): Select failed. [%s]", strerror(errno));
         disconnect();
         return -1;
      }

      if(FD_ISSET(_sock, &read_fds) || FD_ISSET(_sock, &write_fds))
      {
         int err;
         socklen_t errlen;
         int liSndRcvbuf = 0;
         errlen = sizeof(err);
         int lenInt = sizeof(liSndRcvbuf);
         if(getsockopt(_sock, SOL_SOCKET, SO_ERROR, &err, &errlen) < 0)
         {
            logger.logMsg(ERROR_FLAG, 0, "_connect(): Error reading sock property. [%s]",
                          strerror(errno));
            disconnect();
            return -1;
         }

         if(err != 0)
         {
            logger.logMsg(ERROR_FLAG, 0, "_connect(): Error in connect. [%d] [%s]",
                          err, strerror(err));
            disconnect();
            return -1;
         }

         if(getsockopt(_sock, SOL_SOCKET, SO_SNDBUF, &liSndRcvbuf, &lenInt) < 0)
         {
            logger.logMsg(ERROR_FLAG, 0, "_connect(): Error reading sock property. [%s]",
                          strerror(errno));
            disconnect();
            return -1;
         }
         else 
         {
            logger.logMsg(ALWAYS_FLAG,0,"_connect(): getsockopts SO_SNDBUF<%d>",
                              liSndRcvbuf);
         }

         liSndRcvbuf = 0;

         if(getsockopt(_sock, SOL_SOCKET, SO_RCVBUF, &liSndRcvbuf, &lenInt) < 0)
         {
            logger.logMsg(ERROR_FLAG, 0, "_connect(): Error reading sock property. [%s]",
                          strerror(errno));
            disconnect();
            return -1;
         }
         else 
         {
            logger.logMsg(ALWAYS_FLAG,0,"_connect(): getsockopts SO_RCVBUF<%d>",
                              liSndRcvbuf);
         }


      }
   }

   logger.logMsg(ALWAYS_FLAG, 0, "_connect(): Connection to peer [%d] [%x-%d] established.",
                 _peerID, _peerListenerInfo.IP, _peerListenerInfo.port);

   const INGwFtTkVersionSet &ourVer = INGwFtTkCommunicator::getInstance().getVersionSet();

   std::string versionInfo = ourVer.toString();
   int versionInfoLen = htonl(versionInfo.size());

   logger.logMsg(ALWAYS_FLAG, 0, "_connect(): Sending INGwFtTalk Version [%s] [%d]",
                 versionInfo.c_str(), versionInfo.size());

   struct iovec iov[2];
   iov[0].iov_base = (caddr_t)&versionInfoLen;
   iov[0].iov_len  = sizeof(int);
   iov[1].iov_base = (caddr_t)(versionInfo.c_str());
   iov[1].iov_len  = versionInfo.size();

   INGwFtTkWriteErrorState wrResult;

   if(sendAry(iov, 2, wrResult, 5) == -1)
   {
      disconnect();
      return -1;
   }

   int retVersion = 0;
   if(_readToBuf((char *)&retVersion, sizeof(int), 5) != sizeof(int))
   {
      logger.logMsg(ALWAYS_FLAG, 0, "_connect(): Error receiving version response.");

      disconnect();
      return -1;
   }

   _version = ntohl(retVersion);

   logger.logMsg(ALWAYS_FLAG, 0, "_connect(): Received negotiated version [%d]", _version);

   if(_version == 0)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "_connect(): INGwFtTalk version Negotiation failed.");
      disconnect();
      return -1;
   }

   if(!ourVer.contains(_version))
   {
      logger.logMsg(ALWAYS_FLAG, 0, "_connect(): Version [%d] not part of our version [%s]",
                    _version, versionInfo.c_str());
      disconnect();
      return -1;
   }

   return 0;
}

int INGwFtTkChannel::establishConnection(INGwFtTkControlMsg selfDetail, 
                                    INGwFtTkEstablishStatus &result)
{
   _peerInstance.startTime = 0;

   if(_connect() != 0)
   {
      result = EST_SOCKET_ERROR;
      logger.logMsg(ERROR_FLAG, 0, "establishConnection(): EST_SOCKET_ERROR");
      return -1;
   }

   selfDetail.action = INGwFtTkControlMsg::BTK_OPEN_CONNECTION;

   logger.logMsg(ALWAYS_FLAG, 0, "Sending request from SysID [%d] NetID "
                                 "[%x-%d] InstID [%d] action [%d]",
                selfDetail.peerID, selfDetail.listenDetail.IP, 
                selfDetail.listenDetail.port, selfDetail.instanceID.startTime, 
                selfDetail.action);

   INGwFtTkWriteErrorState wrResult;

   if(sendControlMsg(selfDetail, 1, wrResult) == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to send controlMsg closing.");
      disconnect();
      result = EST_SOCKET_ERROR;
      return -1;
   }

   INGwFtTkControlMsg peerDetail;
   INGwFtTkReadErrorState rdResult;

   if(recvControlMsg(peerDetail, 10, rdResult) == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to send controlMsg closing.");
      disconnect();
      result = EST_SOCKET_ERROR;
      logger.logMsg(ERROR_FLAG, 0, "establishConnection(): EST_SOCKET_ERROR");
      return -1;
   }

   _peerListenerInfo = peerDetail.listenDetail;
   _peerInstance = peerDetail.instanceID;

   logger.logMsg(ALWAYS_FLAG, 0, "Obtained response from SysID [%d] NetID "
                                 "[%x-%d] InstID [%d] result [%d]",
                 peerDetail.peerID, peerDetail.listenDetail.IP, 
                 peerDetail.listenDetail.port, peerDetail.instanceID.startTime, 
                 peerDetail.result);

   if(peerDetail.result == INGwFtTkControlMsg::BTK_CONN_ACCEPTED)
   {
      result = ESTABLISHED;
      logger.logMsg(ALWAYS_FLAG, 0, "establishConnection(): ESTABLISHED");
      return 0;
   }

   disconnect();

   if(peerDetail.result == INGwFtTkControlMsg::BTK_SIMULTANEOUS_CONN)
   {
      result = SIMULTANEOUS_OPEN;
      logger.logMsg(ERROR_FLAG, 0, "establishConnection(): SIMULTANEOUS_OPEN");
      return -1;
   }

   result = EST_SOCKET_ERROR;
   logger.logMsg(ERROR_FLAG, 0, "establishConnection(): EST_SOCKET_ERROR");
   return -1;
}

int INGwFtTkChannel::sendAction(INGwFtTkControlMsg selfDetail, int action, 
                           INGwFtTkActionSentState &result)
{
   _peerInstance.startTime = 0;

   if(_connect() != 0)
   {
      result = ACT_SEND_FAILED;
      return -1;
   }

   selfDetail.action = action;

   INGwFtTkWriteErrorState wrResult;

   if(sendControlMsg(selfDetail, 1, wrResult) == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to send controlMsg closing.");
      disconnect();
      result = ACT_SEND_FAILED;
      return -1;
   }

   INGwFtTkControlMsg peerDetail;
   INGwFtTkReadErrorState rdResult;

   if(recvControlMsg(peerDetail, 10, rdResult) == -1)
   {
      disconnect();
      result = ACT_SEND_FAILED;
      return -1;
   }

   disconnect();

   _peerListenerInfo = peerDetail.listenDetail;
   _peerInstance = peerDetail.instanceID;

   if(peerDetail.result == INGwFtTkControlMsg::BTK_ACTION_ACCEPTED)
   {
      result = ACT_SEND_SUCCESS;
      return 0;
   }

   result = ACT_SEND_FAILED;
   return -1;
}

int INGwFtTkChannel::makeFaultQuery(INGwFtTkControlMsg selfDetail, 
                               INGwFtTkFaultMgrQueryState &result)
{
   _peerInstance.startTime = 0;

   result = QUERY_FAILED;

   int ret = _connect();

   if(ret == -2)
   {
      result = QUERY_TIMEOUT;
      return -1;
   }

   if(ret == -1)
   {
      return -1;
   }

   selfDetail.action = INGwFtTkControlMsg::BTK_FAULT_MGR_QUERY;

   INGwFtTkWriteErrorState wrResult;

   if(sendControlMsg(selfDetail, 1, wrResult) == -1)
   {
      disconnect();
      return -1;
   }

   INGwFtTkControlMsg peerDetail;
   INGwFtTkReadErrorState rdResult;

   if(recvControlMsg(peerDetail, 10, rdResult) == -1)
   {
      logger.logMsg(ALWAYS_FLAG, 0, "Receive response failed.");

      if(isChannelValid())
      {
         logger.logMsg(ERROR_FLAG, 0, "Read timedout.");
         result = QUERY_TIMEOUT;
      }

      disconnect();
      return -1;
   }

   logger.logMsg(ALWAYS_FLAG, 0, "Successfully received response.");

   disconnect();

   logger.logMsg(ALWAYS_FLAG, 0, "Response detail peer [%d] instance [%d] "
                                 "result [%d]", peerDetail.peerID, 
                 peerDetail.instanceID.startTime, peerDetail.result);

   _peerListenerInfo = peerDetail.listenDetail;
   _peerInstance = peerDetail.instanceID;

   if(peerDetail.result == INGwFtTkControlMsg::BTK_QUERY_SUCCESS)
   {
      result = QUERY_SUCCESS;
      return 0;
   }

   return -1;
}

int INGwFtTkChannel::negotiateVersion(INGwFtTkControlMsg selfDetail, int subCompID, 
                                 const INGwFtTkVersionSet &supportedVersion)
{
   _peerInstance.startTime = 0;

   if(_connect() != 0)
   {
      return 0;
   }

   selfDetail.action = INGwFtTkControlMsg::BTK_VERSION_NEGOTIATION;

   INGwFtTkWriteErrorState wrResult;

   std::string versionStr = supportedVersion.toString();
   int len = htonl(versionStr.size());
   int subcomp = htonl(subCompID);

   struct iovec iovData[3];

   switch(_version)
   {
      case 1:
      {
         iovData[0].iov_base = (caddr_t )&subcomp;
         iovData[0].iov_len  = sizeof(int);
         iovData[1].iov_base = (caddr_t)&len;
         iovData[1].iov_len  = sizeof(int);
         iovData[2].iov_base = (caddr_t)versionStr.c_str();
         iovData[2].iov_len  = versionStr.size();
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unsupported INGwFtTalk version [%d] "
                                      "negotiated.", _version);
         disconnect();
         return 0;
      }
   }

   if(sendControlMsg(selfDetail, 1, wrResult) == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to send controlMsg closing.");
      disconnect();
      return 0;
   }

   if(sendAry(iovData, 3, wrResult, 1) == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Unable to send the Version Info.");
      disconnect();
      return 0;
   }

   INGwFtTkControlMsg peerDetail;
   INGwFtTkReadErrorState rdResult;

   if(recvControlMsg(peerDetail, 20, rdResult) == -1)
   {
      disconnect();
      logger.logMsg(ERROR_FLAG, 0, "Error receiving the result.");

      // vishal ... FOR NOW: return success as we may have timeouts
      //return 0;
      return 1;
   }

   disconnect();

   _peerListenerInfo = peerDetail.listenDetail;
   _peerInstance = peerDetail.instanceID;

   if(!supportedVersion.contains(peerDetail.result))
   {
      logger.logMsg(ERROR_FLAG, 0, "Unspecified version [%d] negotiated. [%d]",
                    peerDetail.result, subCompID);
      return 0;
   }

   logger.logMsg(ALWAYS_FLAG, 0, "subComp[%d] version[%d] negotiated.", 
                 subCompID, peerDetail.result);

   return peerDetail.result;
}

int INGwFtTkChannel::sendAry(struct iovec *iovData, int numofData, 
                        INGwFtTkWriteErrorState &result, unsigned int timeOut)
{
   if(!isChannelValid())
   {
      logger.logMsg(ERROR_FLAG, 0, "Channel already closed.");
      result = WR_SOCKET_ERROR;
      return -1;
   }

   int retVal = 0;

   if(timeOut == 0)
   {
      timeOut = 60;
   }

   INGwFtTkGuard(&_writeLock);

   fd_set write_fds;  

   int toSend = 0;

   for(int idx = 0; idx < numofData; idx++)
   {
      toSend += iovData[idx].iov_len;
   }

   logger.logMsg (VERBOSE_FLAG, 0,
     "INGwFtTalk::sendAry : Sending <%d> bytes over bayTalk <%d>",
     toSend, sizeof(ssize_t));

   while(toSend)
   {
      errno = 0;

      retVal = writev(_sock, iovData, numofData);

      if(retVal < 0)
      {
         if(errno == EINTR)
         {
            logger.logMsg(WARNING_FLAG, 0, "Writev interrupted. [%s]", 
                          strerror(errno));
            continue;
         }

         if(errno == EAGAIN)
         {
            time_t wait_start = time(NULL);

            while(true)
            {
               FD_ZERO(&write_fds);  FD_SET(_sock, &write_fds);

               struct timeval time_out;
               time_out.tv_sec = timeOut;
               time_out.tv_usec = 0;

               retVal = select(_sock + 1, NULL, &write_fds, NULL, &time_out);

               if((retVal == -1) && (errno == EINTR))
               {
                  logger.logMsg(WARNING_FLAG, 0, "Select interrupted. [%s]",
                                strerror(errno));
                  continue;
               }

               break;
            }

            if(retVal == 0)
            {
               time_t wait_end = time(NULL);
               logger.logMsg(ERROR_FLAG, 0, "Write failed. timedout. [%s] "
                                            "Time [%d-%d]", strerror(errno),
                             wait_start, wait_end);
               disconnect();
               result = WR_TIMEDOUT;
               retVal = -1;
               break;
            }

            if(retVal == -1)
            {
               logger.logMsg(ERROR_FLAG, 0, "Select failed. [%s]", 
                             strerror(errno));
               disconnect();
               result = WR_SOCKET_ERROR;
               retVal = -1;
               break;
            }

            continue;
         }
         else
         {
            logger.logMsg(ERROR_FLAG, 0, "Write failed with [%s]", 
                          strerror(errno));
            disconnect();
            result = WR_SOCKET_ERROR;
            retVal = -1;
            break;
         }
      }

      logger.logMsg(TRACE_FLAG, 0, "Written [%d] bytes to [%d]", retVal, 
                    _peerID); 

      toSend -= retVal;

      if(toSend == 0)
      {
         _updateWriteTime();
         retVal = 0;
         break;
      }

      for(int idx = 0; idx < numofData && retVal > 0; idx++)
      {
         if(retVal > iovData[idx].iov_len)
         {
            retVal -= iovData[idx].iov_len;
            iovData[idx].iov_len = 0;
            continue;
         }

         iovData[idx].iov_len -= retVal;
         iovData[idx].iov_base = ((char *)iovData[idx].iov_base) + retVal;
         retVal = 0;
      }
   }

   return retVal;
}

int INGwFtTkChannel::_readToBuf(char *addr, int len, unsigned int timeout)
{
   if(timeout <= 0)
   {
      timeout = 60;
   }

   int dataRead = 0;

   int retVal = 0;

   fd_set read_fds;  

   int toRead = len;

   while(toRead)
   {
      retVal = recv(_sock, addr, toRead, 0);

      if(retVal == 0)
      {
         logger.logMsg(ERROR_FLAG, 0, "Recv returned zero.");
         disconnect();
         break;
      }
      else if(retVal > 0)
      {
         logger.logMsg(TRACE_FLAG, 0, "Read [%d] from [%d]", retVal, _peerID);

         dataRead += retVal;

         toRead -= retVal;

         if(toRead == 0)
         {
            _updateReadTime();
            return dataRead;
         }
         else
         {
            addr += retVal;
         }

         continue;
      }
      else
      {
         switch(errno)
         {
            case EINTR:
            {
               logger.logMsg(WARNING_FLAG, 0, "Recv interrupted. [%s]",
                             strerror(errno));
            }
            break;

            case EWOULDBLOCK:
            {
               logger.logMsg(TRACE_FLAG, 0, "Recv blocking [%s]", 
                             strerror(errno));
            }
            break;

            default:
            {
               logger.logMsg(ERROR_FLAG, 0, "Recv failed with [%s]", 
                             strerror(errno));
               disconnect();
               return dataRead;
            }
         }
      }

      if(dataRead)
      {
         _updateReadTime();
         return dataRead;
      }

      while(true)
      {
         FD_ZERO(&read_fds);  FD_SET(_sock, &read_fds);

         struct timeval time_out;
         time_out.tv_sec = timeout;
         time_out.tv_usec = 0;

         retVal = select(_sock + 1, &read_fds, NULL, NULL, &time_out);

         if((retVal == -1) && (errno == EINTR))
         {
            logger.logMsg(WARNING_FLAG, 0, "Select interrupted. [%s]", 
                          strerror(errno));
            continue;
         }

         break;
      }

      if(retVal == 0)
      {
         //logger.logMsg(VERBOSE_FLAG, 0, "Select timedout.");
         break;
      }

      if(retVal == -1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Select failed. [%s]", 
                       strerror(errno));
         disconnect();
         break;
      }
   }

   return dataRead;
}


int INGwFtTkChannel::readMsg(INGwFtTkSubCompMgr *subCompMgr, INGwFtTkReadErrorState &result)
{
   result = RD_SOCKET_ERROR;
   QueueData qData;

   qData.type = 0;
   qData.extraInfo = 0;
   qData.length = 0;

   char *msgbuffer;
   
    
   logger.logMsg(VERBOSE_FLAG,0,"In Rcv INGwFtTkChannel");
   //msgbuffer = new char[0x40000]; //256k . Max message size = 64k
   msgbuffer = new char[miBkRepSize]; //256k . Max message size = 64k
   //Yogesh: now max SO_SNDBUF SO_RCVBUF is configurable

   //memset(msgbuffer, '\0', 0x40000);
   memset(msgbuffer, '\0', miBkRepSize);

   int dataPresent = 0;

   int headerLen = 0;

   switch(_version)
   {
      case 1:
      {
         headerLen = sizeof(INGwFtTkMesgHeader_v1);
      }
      break;

      default:
      {
         logger.logMsg(ERROR_FLAG, 0, "Unsupported version negotiated. [%d]",
                       _version);
         result = RD_VERSION_PROB;
      }
   }

   while(isChannelValid())
   {
      //int dataRead = _readToBuf(msgbuffer + dataPresent, 0x40000 - dataPresent,
      int dataRead = _readToBuf(msgbuffer + dataPresent, miBkRepSize - dataPresent,
                                1);

      dataPresent += dataRead;

      int dataCanConsume = dataPresent;
      int dataConsumed = 0;

      if(dataCanConsume)
      {
         logger.logMsg(TRACE_FLAG, 0, "Hdr [%d] can Consume [%d]", 
                       headerLen, dataCanConsume);
      }

      while(dataCanConsume >= headerLen)
      {
         unsigned int currMessageLen;

         switch(_version)
         {
            case 1:
            {
               //8 is the offset to the messageField;
               memcpy(&currMessageLen, msgbuffer + dataConsumed + 12, 
                      sizeof(int));
               currMessageLen = ntohl(currMessageLen);
            }
            break;
         }
         if(currMessageLen > miBkRepSize)
         logger.logMsg(ALWAYS_FLAG, 0, "Message Len [%d]", currMessageLen);

         logger.logMsg(TRACE_FLAG, 0, "Message Len [%d]", currMessageLen);
        
         //yogesh 
         //if(currMessageLen > 0x40000)
         if(currMessageLen > miBkRepSize)
         {
            logger.logMsg(ERROR_FLAG, 0, "Stream corrupted. [%d]", 
                          currMessageLen);
            disconnect();

            delete []msgbuffer;
            return -1;
         }

         if(dataCanConsume >= (headerLen + currMessageLen))
         {
            INGwFtTkReceivedMsg *currMsg = new INGwFtTkReceivedMsg;
            currMsg->version = _version;
            INGwFtTkMesgHeader &currHdr = currMsg->header;

            currMsg->messageBuf = new char[currMessageLen];
            memset(currMsg->messageBuf, '\0', currMessageLen);

            memcpy(&currHdr, msgbuffer + dataConsumed, headerLen);
            memcpy(currMsg->messageBuf, msgbuffer + dataConsumed + headerLen, 
                   currMessageLen);

            dataCanConsume -= (headerLen + currMessageLen);
            dataConsumed += (headerLen + currMessageLen);

            int subCompID = 0;

            switch(_version)
            {
               case 1:
               {
                  currHdr.v1Msg.version = ntohl(currHdr.v1Msg.version);
                  currHdr.v1Msg.opCode = ntohs(currHdr.v1Msg.opCode);
                  currHdr.v1Msg.dataStructureID = 
                                           ntohs(currHdr.v1Msg.dataStructureID);
                  currHdr.v1Msg.msgLen = ntohl(currHdr.v1Msg.msgLen);

                  logger.logMsg(TRACE_FLAG, 0, "Received Msg [%d] for [%d-%d]",
                                currHdr.v1Msg.msgLen, currHdr.v1Msg.instanceID, 
                                currHdr.v1Msg.subComponentID);

                  subCompID = ntohs(currHdr.v1Msg.subComponentID);

                  currHdr.v1Msg.instanceID = _peerID;
                  currHdr.v1Msg.subComponentID = 0;
               }
               break;
            }

            INGwFtTkReceiverQueue *queue = subCompMgr->getQueue(subCompID);

            if(queue == NULL)
            {
               logger.logMsg(ERROR_FLAG, 0, "No receiver for [%d] message "
                                            "discarded.", subCompID);
               delete []currMsg->messageBuf;
               delete currMsg;
               continue;
            }

            INGwFtTkRefObjHolder holder(queue);
            qData.data = currMsg;

            queue->_msgQueue->eventEnqueueBlk(&qData, 1);
         }
         else
         {
            break;
         }
      }

      memmove(msgbuffer, msgbuffer + dataConsumed, dataCanConsume);
      dataPresent = dataCanConsume;
   }

   delete []msgbuffer;
   return -1;
}

INGwFtTkControlMsg INGwFtTkChannel::listen(int &acceptedSock, int &acceptedVersion, 
                                 INGwFtTkListenErrorState &result)
{
   result = LIS_SOCKET_NOERROR;
   INGwFtTkControlMsg ret;

   while(isChannelValid())
   {
      fd_set readFds;
      FD_ZERO(&readFds);
      FD_SET(_sock, &readFds);

      int retVal = select(_sock + 1, &readFds, NULL, NULL, NULL);

      if(retVal != 1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Select returned [%d] [%s]",
                       retVal, strerror(errno));
         continue;
      }

      struct sockaddr_in peerInfo;
      socklen_t len = sizeof(sockaddr_in);

      acceptedSock = accept(_sock, (sockaddr *)&peerInfo, &len);

      if(acceptedSock <= 0)
      {
         logger.logMsg(ERROR_FLAG, 0, "Error in accept. [%s]", strerror(errno));
         continue;
      }

      setOptions(acceptedSock);

      while(true)
      {
         FD_ZERO(&readFds);
         FD_SET(acceptedSock, &readFds);

         struct timeval time_out;
         time_out.tv_sec = 5;
         time_out.tv_usec = 0;

         retVal = select(acceptedSock + 1, &readFds, NULL, NULL, &time_out);

         if((retVal == -1) && (errno == EINTR))
         {
            logger.logMsg(WARNING_FLAG, 0, "Select interrupted. [%s]", 
                          strerror(errno));
            continue;
         }

         break;
      }

      if(retVal != 1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Error reading Version data ret[%d] [%s]",
                       retVal, strerror(errno));
         close(acceptedSock);
         continue;
      }

      int versionInfoLen = 0;

      while(true)
      {
         retVal = recv(acceptedSock, &versionInfoLen, sizeof(int), 0);

         if((retVal == -1) && (errno == EINTR))
         {
            logger.logMsg(WARNING_FLAG, 0, "Recv interrupted. [%s]", 
                          strerror(errno));
            continue;
         }

         break;
      }

      if(retVal != sizeof(int))
      {
         logger.logMsg(ERROR_FLAG, 0, "Unable to get VersionInfoLen.");
         close(acceptedSock);
         continue;
      }

      versionInfoLen = ntohl(versionInfoLen);

      if(versionInfoLen <= 0 || versionInfoLen > 500)
      {
         logger.logMsg(ERROR_FLAG, 0, "Impossible version len. [%d]",
                       versionInfoLen);
         close(acceptedSock);
         continue;
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Received Version info Len [%d]",
                    versionInfoLen);

      while(true)
      {
         FD_ZERO(&readFds);
         FD_SET(acceptedSock, &readFds);

         struct timeval time_out;
         time_out.tv_sec = 5;
         time_out.tv_usec = 0;

         retVal = select(acceptedSock + 1, &readFds, NULL, NULL, &time_out);

         if((retVal == -1) && (errno == EINTR))
         {
            logger.logMsg(WARNING_FLAG, 0, "Select interrupted. [%s]", 
                          strerror(errno));
            continue;
         }

         break;
      }

      if(retVal != 1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Error reading VersionInfo ret[%d] [%s]",
                       retVal, strerror(errno));
         close(acceptedSock);
         continue;
      }

      char *versionData = new char[versionInfoLen + 1];
      versionData[versionInfoLen] = '\0';

      while(true)
      {
         retVal = recv(acceptedSock, versionData, versionInfoLen, 0);

         if((retVal == -1) && (errno == EINTR))
         {
            logger.logMsg(WARNING_FLAG, 0, "Recv interrupted. [%s]", 
                          strerror(errno));
            continue;
         }

         break;
      }

      if(retVal != versionInfoLen)
      {
         logger.logMsg(ERROR_FLAG, 0, "Unable to get VersionInfoData.");
         close(acceptedSock);
         delete []versionData;
         continue;
      }

      std::string peerVersionInfo = versionData;
      delete []versionData;

      logger.logMsg(ALWAYS_FLAG, 0, "Received peer versionInfo [%s]",
                    peerVersionInfo.c_str());
      INGwFtTkVersionSet peerVersions = INGwFtTkVersionSet::toVersionSet(peerVersionInfo);

      acceptedVersion = INGwFtTkCommunicator::getInstance().getVersionSet().
                                                   findVersion(0, peerVersions);

      if(acceptedVersion == 0)
      {
         logger.logMsg(ALWAYS_FLAG, 0, "INGwFtTalk version negotiation failed.");
         close(acceptedSock);
         continue;
      }

      int sendVersion = htonl(acceptedVersion);
      write(acceptedSock, &sendVersion, sizeof(int));

      INGwFtTkReadErrorState rdResult;

      if(recvControlMsg(ret, 5, rdResult, acceptedSock, acceptedVersion) == -1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Error reading control msg.");
         close(acceptedSock);
         continue;
      }

      return ret;
   }
}

/*
 *Version 1 Format.
 *         Peer(4) + startTime(4) + IP(4) + Port(4) + Action/Result(4) -> 20Bytes.

 *Current Structure Format is Version 1 format.

 *if the control mesg had been modified. We have to create a new version and 
 *populate the VersionDetail in INGwFtTkCommunicator constructor.
 *Below two functions had to be modified to take care of backward compatibility.
 */

int INGwFtTkChannel::sendControlMsg(const INGwFtTkControlMsg &msg, int timeout,
                               INGwFtTkWriteErrorState &wrResult,
                               int sock, int version)
{
   if(version == 0)
   {
      version = _version;
   }

   struct iovec iov;

   INGwFtTkControlMsg ver1_msg;

   switch(version)
   {
      case 1:
      {
         ver1_msg.peerID = htonl(msg.peerID);
         ver1_msg.instanceID.startTime = htonl(msg.instanceID.startTime);
         ver1_msg.listenDetail.IP   = htonl(msg.listenDetail.IP);
         ver1_msg.listenDetail.port = htonl(msg.listenDetail.port);
         ver1_msg.action = htonl(msg.action);
         iov.iov_base = (caddr_t)&ver1_msg;
         iov.iov_len  = sizeof(ver1_msg);
      }
      break;

      default:
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Unable to send controlMsg, unsupported "
                                       "Version [%d].", version);
         wrResult = WR_VERSION_PROB;
         return -1;
      }
   }

   if(sock == 0)
   {
      if(sendAry(&iov, 1, wrResult, timeout) == -1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Unable to send controlMsg closing.");
         return -1;
      }
   }
   else
   {
      write(sock, iov.iov_base, iov.iov_len);
   }

   return 0;
}

int INGwFtTkChannel::recvControlMsg(INGwFtTkControlMsg &msg, int timeout, 
                               INGwFtTkReadErrorState &rdResult, 
                               int sock, int version)
{
   if(version == 0)
   {
      version = _version;
   }

   INGwFtTkControlMsg ver1_msg;

   char *buf = NULL;
   int buflen = 0;

   switch(version)
   {
      case 1:
      {
         buf = (char *)&ver1_msg;
         buflen = sizeof(ver1_msg);
      }
      break;

      default:
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Unable to recv controlMsg, unsupported "
                                       "Version [%d].", version);
         rdResult = RD_VERSION_PROB;
         return -1;
      }
   }

   if(sock == 0)
   {
      if(_readToBuf((char *)buf, buflen, timeout) != buflen)
      {
         rdResult = RD_SOCKET_ERROR;
         return -1;
      }
   }
   else
   {
      int retVal = -1;

      while(true)
      {
         fd_set readFds;
         FD_ZERO(&readFds);
         FD_SET(sock, &readFds);

         struct timeval time_out;
         time_out.tv_sec = timeout;
         time_out.tv_usec = 0;

         retVal = select(sock + 1, &readFds, NULL, NULL, &time_out);

         if((retVal == -1) && (errno == EINTR))
         {
            logger.logMsg(WARNING_FLAG, 0, "Select interrupted. [%s]", 
                          strerror(errno));
            continue;
         }

         break;
      }

      if(retVal != 1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Error reading controlMsg ret[%d] [%s]",
                       retVal, strerror(errno));
         rdResult = RD_SOCKET_ERROR;
         return -1;
      }

      while(true)
      {
         retVal = recv(sock, buf, buflen, 0);

         if((retVal == -1) && (errno == EINTR))
         {
            logger.logMsg(WARNING_FLAG, 0, "Recv interrupted. [%s]", 
                          strerror(errno));
            continue;
         }

         break;
      }

      if(retVal != buflen)
      {
         logger.logMsg(ERROR_FLAG, 0, "Unable to get control message.");
         rdResult = RD_SOCKET_ERROR;
         return -1;
      }
   }

   switch(version)
   {
      case 1:
      {
         msg.peerID = ntohl(ver1_msg.peerID);
         msg.instanceID.startTime = ntohl(ver1_msg.instanceID.startTime);
         msg.listenDetail.IP = ntohl(ver1_msg.listenDetail.IP);
         msg.listenDetail.port = ntohl(ver1_msg.listenDetail.port);
         msg.result = ntohl(ver1_msg.result);
      }
      break;
   }

   return 0;
}

int INGwFtTkChannel::readVersionInfo(int &subComponentID, INGwFtTkVersionSet &peerVersions, 
                                int timeout, INGwFtTkReadErrorState &rdResult, 
                                int sock, int version)
{
   if(version == 0)
   {
      version = _version;
   }

   typedef struct 
   {
      int compID;
      int len;
   }VersionInfoHeader;

   VersionInfoHeader ver1_msg;

   char *buf = NULL;
   int buflen = 0;

   switch(version)
   {
      case 1:
      {
         buf = (char *)&ver1_msg;
         buflen = sizeof(ver1_msg);
      }
      break;

      default:
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Unable to recv controlMsg, unsupported "
                                       "Version [%d].", version);
         rdResult = RD_VERSION_PROB;
         return -1;
      }
   }

   if(sock == 0)
   {
      if(_readToBuf((char *)buf, buflen, timeout) != buflen)
      {
         rdResult = RD_SOCKET_ERROR;
         return -1;
      }
   }
   else
   {
      int retVal = -1;

      while(true)
      {
         fd_set readFds;
         FD_ZERO(&readFds);
         FD_SET(sock, &readFds);

         struct timeval time_out;
         time_out.tv_sec = timeout;
         time_out.tv_usec = 0;

         retVal = select(sock + 1, &readFds, NULL, NULL, &time_out);

         if((retVal == -1) && (errno == EINTR))
         {
            logger.logMsg(WARNING_FLAG, 0, "Select interrupted. [%s]", 
                          strerror(errno));
            continue;
         }

         break;
      }

      if(retVal != 1)
      {
         logger.logMsg(ERROR_FLAG, 0, "Error reading Version hdr ret[%d] [%s]",
                       retVal, strerror(errno));
         rdResult = RD_SOCKET_ERROR;
         return -1;
      }

      while(true)
      {
         retVal = recv(sock, buf, buflen, 0);

         if((retVal == -1) && (errno == EINTR))
         {
            logger.logMsg(WARNING_FLAG, 0, "Recv interrupted. [%s]", 
                          strerror(errno));
            continue;
         }

         break;
      }

      if(retVal != buflen)
      {
         logger.logMsg(ERROR_FLAG, 0, "Unable to get version hdr.");
         rdResult = RD_SOCKET_ERROR;
         return -1;
      }
   }

   switch(version)
   {
      case 1:
      {
         subComponentID = ntohl(ver1_msg.compID);
         buflen = ntohl(ver1_msg.len);

         if(buflen > 500)
         {
            logger.logMsg(ERROR_FLAG, 0, "Impossible version len [%d]", buflen);
            rdResult = RD_SOCKET_ERROR;
            return -1;
         }

         buf = new char[buflen + 1];
         buf[buflen] = '\0';
      }
      break;
   }

   if(buflen > 0)
   {
      if(sock == 0)
      {
         if(_readToBuf((char *)buf, buflen, timeout) != buflen)
         {
            rdResult = RD_SOCKET_ERROR;
            delete []buf;
            return -1;
         }
      }
      else
      {
         int retVal = -1;

         while(true)
         {
            fd_set readFds;
            FD_ZERO(&readFds);
            FD_SET(sock, &readFds);

            struct timeval time_out;
            time_out.tv_sec = timeout;
            time_out.tv_usec = 0;

            retVal = select(sock + 1, &readFds, NULL, NULL, &time_out);

            if((retVal == -1) && (errno == EINTR))
            {
               logger.logMsg(WARNING_FLAG, 0, "Select interrupted. [%s]", 
                             strerror(errno));
               continue;
            }

            break;
         }

         if(retVal != 1)
         {
            logger.logMsg(ERROR_FLAG, 0, "Error reading Version hdr ret[%d] "
                                         "[%s]", retVal, strerror(errno));
            rdResult = RD_SOCKET_ERROR;
            delete []buf;
            return -1;
         }

         while(true)
         {
            retVal = recv(sock, buf, buflen, 0);

            if((retVal == -1) && (errno == EINTR))
            {
               logger.logMsg(WARNING_FLAG, 0, "Recv interrupted. [%s]", 
                             strerror(errno));
               continue;
            }

            break;
         }

         if(retVal != buflen)
         {
            logger.logMsg(ERROR_FLAG, 0, "Unable to get version hdr.");
            rdResult = RD_SOCKET_ERROR;
            delete []buf;
            return -1;
         }
      }
   }

   std::string versionStr = buf;
   delete []buf;

   logger.logMsg(ALWAYS_FLAG, 0, "Received Version Str [%d] [%s]", 
                 subComponentID, versionStr.c_str());

   peerVersions = INGwFtTkVersionSet::toVersionSet(versionStr);

   return 0;
}
