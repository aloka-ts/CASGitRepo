/*------------------------------------------------------------------------------
         File: INGwFtTkHostMonitor.C
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
#include <INGwFtTalk/INGwFtTkHostMonitor.h>
#include <INGwFtTalk/INGwFtTkCommunicator.h>
#include <INGwFtTalk/INGwFtTkChannel.h>
#include <INGwFtTalk/INGwFtTkUtil.h>

#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <netdb.h>
#include <stdio.h>

#define BT_BOUND_PORT_PING 5000

extern "C" void * INGwFtTkHostMonitor::_monitorThreadStart(void *)
{
   INGwFtTkCommunicator::setSignal();

   logger.logMsg(ALWAYS_FLAG, 0, "INGwFtTalk host monitor started.");

   INGwFtTkHostMonitor::getInstance()._monitor();

   logger.logMsg(ALWAYS_FLAG, 0, "End of INGwFtTalk host monitor.");
   return NULL;
}

INGwFtTkHostMonitor::INGwFtTkHostMonitor()
{
   pthread_mutex_init(&_lock, NULL);
   pthread_cond_init(&_cond, NULL);

   _no_of_events = 0;
   _callback = NULL;
   _stopStatus = false;

   for(int idx = 0; idx < BTK_MAX_SELF_IPS; idx++)
   {
      _selfIP[idx] = 0;
   }

   char currhost[100];

   if(gethostname(currhost, 100) == 0)
   {
      struct hostent *hentry = gethostbyname(currhost);

      if(hentry != NULL)
      {
         char **currentry;
         int idx = 0;
   
         for(currentry = hentry->h_addr_list; 
             ((*currentry != NULL) && (idx < BTK_MAX_SELF_IPS)); 
             currentry++, idx++)
         {
            int netIP = 0;
            memcpy(&netIP, *currentry, sizeof(int));

            _selfIP[idx] = ntohl(netIP);

            logger.logMsg(ALWAYS_FLAG, 0, "CurrHost IPs [%d] [%x]", 
                          idx, _selfIP[idx]);
         }
      }
      else
      {
         logger.logMsg(ERROR_FLAG, 0, "Error getting hostentry. [%s]", 
                       strerror(errno));
      }
   }
   else
   {
      logger.logMsg(ERROR_FLAG, 0, "Error getting hostname. [%s]", 
                    strerror(errno));
   }

   pthread_create(&_monitorThread, NULL, _monitorThreadStart, NULL);
}

INGwFtTkHostMonitor::~INGwFtTkHostMonitor()
{
   {
      INGwFtTkGuard(&_lock);
      _stopStatus = true;
      pthread_cond_signal(&_cond);
   }

   void *result;
   pthread_join(_monitorThread, &result);

   pthread_mutex_destroy(&_lock);
   pthread_cond_destroy(&_cond);
}

int INGwFtTkHostMonitor::addMonitor(INGwFtTkHostEventDetail event)
{
   logger.logMsg(ALWAYS_FLAG, 0, "Adding monitor for [%x] evt [%d]", 
                 event.hostIP, event.interestedEvent);

   if(event.interestedEvent == INGwFtTkHostEventDetail::NOT_AVAILABLE)
   {
      for(int idx = 0; idx < BTK_MAX_SELF_IPS; idx++)
      {
         if(_selfIP[idx] == event.hostIP)
         {
            logger.logMsg(ALWAYS_FLAG, 0, "IP [%x] is self machine. So wont "
                                          "monitor for non available.", 
                                          event.hostIP);
            return 0;
         }
      }
   }

   INGwFtTkGuard(&_lock);

   for(int idx = 0; idx < _no_of_events; idx++)
   {
      if(_events[idx] == event)
      {
         logger.logMsg(VERBOSE_FLAG, 0, "Duplicate event.");
         return 0;
      }
   }

   if(_no_of_events == BTK_MAX_HOST_EVENT)
   {
      logger.logMsg(ERROR_FLAG, 0, "Max events already reached.");

      for(int idx = 0; idx < _no_of_events; idx++)
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Entry [%d] host [%d] evt [%d]",
                       idx, _events[idx].hostIP, _events[idx].interestedEvent);
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Deliberately quitting.");
      printf("INGwFtTk: Too many host to monitor.\n");
      exit(1);
      return -1;
   }

   _events[_no_of_events] = event;
   _no_of_events++;

   logger.logMsg(VERBOSE_FLAG, 0, "Event registered.");
   return 0;
}

int INGwFtTkHostMonitor::stopMonitor(INGwFtTkHostEventDetail event)
{
   logger.logMsg(ALWAYS_FLAG, 0, "Stopping monitor for [%x] evt [%d]",
                 event.hostIP, event.interestedEvent);

   INGwFtTkGuard(&_lock);

   for(int idx = 0; idx < _no_of_events; idx++)
   {
      if(_events[idx] == event)
      {
         memmove(_events + idx, _events + idx + 1, 
                 sizeof(INGwFtTkHostEventDetail) * (_no_of_events - idx - 1));
         _no_of_events--;

         return 0;
      }
   }

   logger.logMsg(VERBOSE_FLAG, 0, "Event not found.");
   return -1;
}

void INGwFtTkHostMonitor::registerCallback(INGwFtTkHostMonitorInf *inf)
{
   INGwFtTkGuard(&_lock);
   _callback = inf;
}

void INGwFtTkHostMonitor::deregisterCallback(INGwFtTkHostMonitorInf *inf)
{
   INGwFtTkGuard(&_lock);
   _callback = NULL;
}

INGwFtTkHostEventDetail::INGwFtTkHostEvent INGwFtTkHostMonitor::_getStatus(unsigned int IP, 
                                                            int timeout)
{
   logger.logMsg(TRACE_FLAG, 0, "IN _getStatus(): IP<%x> timeout<%d>",
                 IP, timeout);
   int sock = socket(AF_INET, SOCK_STREAM, 0);

   if(sock == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "_getStatus(): Socket opening error. [%s]", 
                    strerror(errno));
      printf("INGwFtTk: Socket creation failed.\n");
      exit(1);
   }

   INGwFtTkChannel::setOptions(sock);

   struct sockaddr_in srvAddr;
   memset(&srvAddr, 0, sizeof(srvAddr));
   srvAddr.sin_family = AF_INET;

   if(timeout == BT_BOUND_PORT_PING)
   {
      srvAddr.sin_port = htons(23);
      timeout = 1;
   }
   else
   {
      srvAddr.sin_port = htons(4);
   }

   srvAddr.sin_addr.s_addr = htonl(IP);

   errno = 0;

   int retVal = 0;
   if((retVal = 
       connect(sock, (struct sockaddr *)&srvAddr, sizeof(srvAddr))) < 0)
   {
      logger.logMsg(TRACE_FLAG, 0, "_getStatus():connect < 0");
      if(errno != EINPROGRESS)
      {
         logger.logMsg(TRACE_FLAG, 0, "_getStatus():connect errno!=EINPROGRESS");
         close(sock);
         return INGwFtTkHostEventDetail::AVAILABLE;
      }

      while(true)
      {
         fd_set read_fds;   FD_ZERO(&read_fds);   FD_SET(sock, &read_fds);
         fd_set write_fds;  FD_ZERO(&write_fds);  FD_SET(sock, &write_fds);

         struct timeval time_out;
         time_out.tv_sec = timeout;
         time_out.tv_usec = 0;

         retVal = select(sock + 1, &read_fds, &write_fds, NULL, &time_out);

         if (retVal < 0)
         {
            logger.logMsg(ERROR_FLAG, 0, "_getStatus(): select failed [%s]:[%d]",
               strerror(errno), retVal);
         }

         if((retVal == -1) && (errno == EINTR))
         {
            logger.logMsg(ERROR_FLAG, 0, "_getStatus(): Select interrupted. [%s]", 
                          strerror(errno));
            continue;
         }
         else {
           logger.logMsg(TRACE_FLAG, 0, "_getStatus():select returned [%d]", retVal);
         }

         break;
      }

      int err = 0;
      socklen_t errlen;

      errlen = sizeof(err);

      if(getsockopt(sock, SOL_SOCKET, SO_ERROR, &err, &errlen) < 0)
      {
         logger.logMsg(ERROR_FLAG, 0, "_getStatus(): Error reading sock property. [%s]:[%d]",
                       strerror(errno), err);
         close(sock);
         return INGwFtTkHostEventDetail::NOT_AVAILABLE;
      }

      if(err != 0)
      {
         logger.logMsg(TRACE_FLAG, 0,
            "_getStatus(): Error in getsockopt. [%d] [%s]", err, strerror(err));
      }

      if((err == EHOSTUNREACH) || (err == ENETUNREACH)  ||
         (err == EHOSTDOWN)    || (err == ENETDOWN)     || (err == ENETRESET))
      {
         logger.logMsg(ERROR_FLAG, 0, "_getStatus():getsockopt errorno equal EHOSTUNREACH");
         close(sock);
         return INGwFtTkHostEventDetail::NOT_AVAILABLE;
      }

      close(sock);

      if(retVal == 0)
      {
         logger.logMsg(ERROR_FLAG, 0, "_getStatus(): Connect failed, Timedout. [%x] [%s]",
                       IP, strerror(errno));
         return INGwFtTkHostEventDetail::NOT_AVAILABLE;
      }
   }
   else
   {
      logger.logMsg(TRACE_FLAG, 0, "_getStatus():connect >= 0");
      close(sock);
   }

   logger.logMsg(TRACE_FLAG, 0, "OUT _getStatus(): AVAILABLE. IP<%x>", IP);
   return INGwFtTkHostEventDetail::AVAILABLE;
}

void INGwFtTkHostMonitor::_monitor()
{
   INGwFtTkHostMonitorInf *localCbk = NULL;
   INGwFtTkHostEventDetail localCpy[BTK_MAX_HOST_EVENT];
   int localEvntCnt = 0;
   int max_retry_count = 5;

   {
      char *retry_val = getenv("MAX_BT_RETRY_COUNT");
      if(retry_val)
      {
         max_retry_count = atoi(retry_val);

         if(max_retry_count < 2 || max_retry_count > 10)
         {
            max_retry_count = 5;
         }
      }

      logger.logMsg(ALWAYS_FLAG, 0, "Retrycount is [%d]", max_retry_count);
   }

   while(_stopStatus == false)
   {
      {
         INGwFtTkGuard(&_lock);

         memcpy(localCpy, _events, sizeof(INGwFtTkHostEventDetail) * _no_of_events);
         localEvntCnt = _no_of_events;
         localCbk = _callback;
      }

      for(int idx = 0; idx < localEvntCnt; idx++)
      {
         int timeOut = 2;

         if(localCpy[idx].interestedEvent == INGwFtTkHostEventDetail::AVAILABLE)
         {
            //ie host is down and we are checking till it gets available.
            //So no need to wait for 2 secs.
            timeOut = 1;
         }

         INGwFtTkHostEventDetail::INGwFtTkHostEvent result;
         struct in_addr ipAddr;
         ipAddr.s_addr = htonl(localCpy[idx].hostIP);
         std::string locIP = (const char *) inet_ntoa(ipAddr);

         result = _getStatus(localCpy[idx].hostIP, timeOut);

         if(result == localCpy[idx].interestedEvent)
         {
            for(int ridx = 1; (ridx < max_retry_count) && 
                              (result == INGwFtTkHostEventDetail::NOT_AVAILABLE); 
                ridx++)
            {
               //Lets check one more time before reporting host unavailability.
               sleep(1);

               if(ridx == (max_retry_count - 1))
               {
                  timeOut = BT_BOUND_PORT_PING;
               }
               else
               {
                  timeOut = 1;
               }

               result = _getStatus(localCpy[idx].hostIP, timeOut);

               if(result == INGwFtTkHostEventDetail::AVAILABLE)
               {
                  logger.logMsg(ALWAYS_FLAG, 0, 
                                "TCP based host identification fails sometime "
                                "though the host is alive.");
               }
            }
         }

         if(result == localCpy[idx].interestedEvent)
         {
            if(localCbk)
            {
               localCbk->generatedEvent(localCpy[idx]);
            }
            else
            {
               logger.logMsg(ALWAYS_FLAG, 0, "No one to receive event. "
                                             "[%x][%d]", localCpy[idx].hostIP, 
                             localCpy[idx].interestedEvent);
            }
         }
      }

      {
         INGwFtTkGuard(&_lock);

         if(_stopStatus)
         {
            break;
         }

         struct timespec absTime;
         absTime.tv_sec = time(NULL) + 1;
         absTime.tv_nsec = 0;

         pthread_cond_timedwait(&_cond, &_lock, &absTime);
      }
   }

   return;
}
