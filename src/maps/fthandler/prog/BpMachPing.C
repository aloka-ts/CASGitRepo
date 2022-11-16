#include <Util/Logger.h>
LOG("LoadBalancer");

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
#include <stdio.h>
#include <string>

#include "BpMachPing.h"

BpMachPing *BpMachPing::me;

BpMachPing &
BpMachPing::getInstance()
{
   if (NULL == me)
      me = new BpMachPing();

   return *me;
}

void
BpMachPing::_setOptions(int sockID)
{
   LogTrace(0, "Entering _setOptions");

   int flags;

   if ((flags = fcntl(sockID, F_GETFL, 0)) < 0) {
		logger.logMsg(ERROR_FLAG, 0, "Error getting socket status = [%s]. Exit",
			strerror(errno));
      exit(1);
   }

   flags |= O_NONBLOCK;

   if (fcntl(sockID, F_SETFL, flags) < 0) {
		logger.logMsg(ERROR_FLAG, 0, "Error setting nonBlocking = [%s]. Exit",
			strerror(errno));
      exit(1);
   }

   int flag = 1;
   if (setsockopt(sockID, IPPROTO_TCP, TCP_NODELAY, &flag, sizeof(int)) < 0) {
		logger.logMsg(ERROR_FLAG, 0,
			"Error. Failure disabling Nagle algorithm = [%s]. Exit",
			strerror(errno));
      exit(1);
   }
   LogTrace(0, "Leaving _setOptions");
}

BpMachPing::BpMachStatus
BpMachPing::getStatus(uint32_t IP, int timeout)
{
   LogTrace(0, "Entering getStatus");

   int sock = socket(AF_INET, SOCK_STREAM, 0);

   if (sock == -1) {
	   LogError(0, "Socket creation Failed. Exit");
      exit(1);
   }

   _setOptions(sock);

   struct sockaddr_in srvAddr;
   memset(&srvAddr, 0, sizeof(srvAddr));
   srvAddr.sin_family = AF_INET;
   srvAddr.sin_port = htons(4);
   srvAddr.sin_addr.s_addr = htonl(IP);

   errno = 0;

   int retVal = 0;
   if ((retVal =
      connect(sock, (struct sockaddr *)&srvAddr, sizeof(srvAddr))) < 0) {
      if (errno != EINPROGRESS) {
         close(sock);
         return AVAILABLE;
      }

      while (true) {
         fd_set read_fds;   FD_ZERO(&read_fds);   FD_SET(sock, &read_fds);
         fd_set write_fds;  FD_ZERO(&write_fds);  FD_SET(sock, &write_fds);

         struct timeval time_out;
         time_out.tv_sec = 0;
         time_out.tv_usec = 1000 * timeout;

         retVal = select(sock + 1, &read_fds, &write_fds, NULL, &time_out);

         if ((retVal == -1) && (errno == EINTR)) {
			logger.logMsg(TRACE_FLAG, 0, "Select interrupted = [%s]. Continue",
				strerror(errno));
            continue;
         }

         break;
      }

      int err = 0;
      socklen_t errlen;

      errlen = sizeof(err);

      if (getsockopt(sock, SOL_SOCKET, SO_ERROR, &err, &errlen) < 0) {
			logger.logMsg(ERROR_FLAG, 0, "Error reading sock property = [%s]",
				strerror(errno));
         close(sock);
	   	LogTrace(0, "Leaving getStatus. Return NOT_AVAILABLE");
         return NOT_AVAILABLE;
      }

      if ((err == EHOSTUNREACH) || (err == ENETUNREACH)  ||
          (err == EHOSTDOWN) || (err == ENETDOWN) || (err == ENETRESET)) {
			logger.logMsg(ERROR_FLAG, 0, "Error = [%s]", strerror(errno));
         close(sock);
	   	LogTrace(0, "Leaving getStatus. Return NOT_AVAILABLE");
         return NOT_AVAILABLE;
      }

      close(sock);

      if (retVal == 0) {
			logger.logMsg(ERROR_FLAG, 0, "Connect Failed = [%s]",
				strerror(errno));
	   	LogTrace(0, "Leaving getStatus. Return NOT_AVAILABLE");
         return NOT_AVAILABLE;
      }
   }
   else {
   	LogTrace(0, "Connect successful");
      close(sock);
   }

	LogTrace(0, "Leaving getStatus. Return AVAILABLE");
   return AVAILABLE;
}
