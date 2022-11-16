//*********************************************************************
//   GENBAND, Inc. Confidential and Proprietary
//
// This work contains valuable confidential and proprietary
// information.
// Disclosure, use or reproduction without the written authorization of
// GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
// is protected by the laws of the United States and other countries.
// If publication of the work should occur the following notice shall
// apply:
//
// "Copyright 2007 GENBAND, Inc.  All rights reserved."
//*********************************************************************
//********************************************************************
//
//     File:     INGwIfrUtlMachPing.C
//
//     Desc:      Utility used to ping the machine availability.
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <Util/Logger.h>
LOG("INGwInfraUtil");

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

#include <INGwInfraUtil/INGwIfrUtlMachPing.h>

namespace RSI_NSP_CCM
{

void INGwIfrUtlMachPing::_setOptions(int sockID)
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
}

INGwIfrUtlMachPing::INGwIfrUtlMachStatus INGwIfrUtlMachPing::getStatus(unsigned int IP, int timeout)
{
   int sock = socket(AF_INET, SOCK_STREAM, 0);

   if(sock == -1)
   {
      logger.logMsg(ERROR_FLAG, 0, "Socket opening error. [%s]", 
                    strerror(errno));
      printf("BTk: Socket creation failed.\n");
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
   if((retVal = 
       connect(sock, (struct sockaddr *)&srvAddr, sizeof(srvAddr))) < 0)
   {
      if(errno != EINPROGRESS)
      {
         close(sock);
         return AVAILABLE;
      }

      while(true)
      {
         fd_set read_fds;   FD_ZERO(&read_fds);   FD_SET(sock, &read_fds);
         fd_set write_fds;  FD_ZERO(&write_fds);  FD_SET(sock, &write_fds);

         struct timeval time_out;
         time_out.tv_sec = 0;
         time_out.tv_usec = timeout * 1000;

         if(time_out.tv_usec >= 1000000)
         {
            time_out.tv_sec  = time_out.tv_usec / 1000000;
            time_out.tv_usec %= 1000000;
         }

         retVal = select(sock + 1, &read_fds, &write_fds, NULL, &time_out);

         if((retVal == -1) && (errno == EINTR))
         {
            logger.logMsg(WARNING_FLAG, 0, "Select interrupted. [%s]", 
                          strerror(errno));
            continue;
         }

         break;
      }

      int err = 0;
      socklen_t errlen;

      errlen = sizeof(err);

      if(getsockopt(sock, SOL_SOCKET, SO_ERROR, &err, &errlen) < 0)
      {
         logger.logMsg(ERROR_FLAG, 0, "Error reading sock property. [%s]",
                       strerror(errno));
         close(sock);
         return NOT_AVAILABLE;
      }

      if((err == EHOSTUNREACH) || (err == ENETUNREACH)  ||
         (err == EHOSTDOWN)    || (err == ENETDOWN)     || (err == ENETRESET))
      {
         close(sock);
         return NOT_AVAILABLE;
      }

      close(sock);

      if(retVal == 0)
      {
         logger.logMsg(ERROR_FLAG, 0, "Connect failed, Timedout. [%x] [%s]",
                       IP, strerror(errno));
         return NOT_AVAILABLE;
      }
   }
   else
   {
      close(sock);
   }

   return AVAILABLE;
}

};
