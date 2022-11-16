#include <Util/Logger.h>
LOG("LoadBalancer");

#include <iostream.h>
#include <strings.h>
#include <errno.h>
#include <unistd.h>
#include <netdb.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <sys/types.h>
#include <dirent.h>
#include <iostream.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>
#include <stropts.h>
#include </usr/include/net/if.h>

#if defined sun4Sol28
#include </usr/include/sys/sockio.h>

#elif defined LINUX
#include <linux/sockios.h>
#include <asm/sockios.h>
#include <sys/ioctl.h>
#include <sys/utsname.h>
#include <linux/if_ether.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#endif

// Returns 1 is we are on the primary machine
// Returns 0 if we are on the secondary machine
int
determineConfiguredRole(std::string &primaryIp, std::string &secondaryIp)
{
	LogTrace(0, "Entering getConfiguredRole");

	// Get a list of all the Network Interfaces on this box
	// Use ioctl for this

	int outData = 0;
   int prevOutData = -1;
   int inRec = 30;
   char *inputData = NULL;
	
   struct ifconf kernalData;
	
   int sock = socket(AF_INET, SOCK_DGRAM, 0);
	
   while (true) {
		if (inputData) {
			delete []inputData;
			inputData = NULL;
		}
			
		inputData = new char[sizeof(ifreq) * inRec];
		bzero(inputData, sizeof(ifreq) * inRec);

      kernalData.ifc_len = (sizeof(ifreq) * inRec);
      kernalData.ifc_ifcu.ifcu_buf = inputData;
		
      inRec += 10;
      if (ioctl(sock, SIOCGIFCONF, &kernalData) < 0) {
			logger.logMsg(ERROR_FLAG, 0, "ioctl Error = [%s]",
							  strerror(errno));
         close(sock);

         if (inputData)
            delete []inputData;

			LogError(0, "Exiting");
			exit(1);
      }

      if (prevOutData == kernalData.ifc_len) {
         break;
      }

      prevOutData = kernalData.ifc_len;
   }

	logger.logMsg(TRACE_FLAG, 0, "IOCTL returned [%d] data size ifreq [%d]",
                 kernalData.ifc_len, sizeof(ifreq));

   char *end_ptr = (char *)kernalData.ifc_ifcu.ifcu_req;
   end_ptr += kernalData.ifc_len;

   int interface_count = 1;
   for (ifreq *next_ptr, *req_ptr = kernalData.ifc_ifcu.ifcu_req;
		  (char *)req_ptr < end_ptr; req_ptr = next_ptr, interface_count++) {
      logger.logMsg(TRACE_FLAG, 0, "Current Req [%x] count [%d]", req_ptr,
                    interface_count);

      int len = 0;
#ifdef HAVE_SOCKADDR_SA_LEN
      len = max(sizeof(struct sockaddr), req_ptr->ifr_ifru.ifru_addr.sa_len);
#else
      switch (req_ptr->ifr_ifru.ifru_addr.sa_family) {
		case AF_INET6:
			len = sizeof(struct sockaddr_in6);
			break;

		default:
			len = sizeof(struct sockaddr);
      }
#endif

      len += sizeof(req_ptr->ifr_name);
      next_ptr = (ifreq *)(((char *) req_ptr) + len);

      if (req_ptr->ifr_ifru.ifru_addr.sa_family != AF_INET) {
         logger.logMsg(ERROR_FLAG, 0, "Interface family [%d] not supported "
							  "found skipping.",
                       req_ptr->ifr_ifru.ifru_addr.sa_family);
         continue;
      }
		
      sockaddr_in *insock = (sockaddr_in *)(&(req_ptr->ifr_ifru.ifru_addr));
		std::string interfaceIp = inet_ntoa(insock->sin_addr);
      logger.logMsg(TRACE_FLAG, 0, "Interface [%s] [%s] detected.",
                    req_ptr->ifr_name, interfaceIp.c_str());
		
		if (0 == strcmp(primaryIp.c_str(), interfaceIp.c_str())) {
			LogTrace(0, "Interface and Primary IPs match. Return 1");
			LogTrace(0, "Leaving getConfiguredRole");
			return 1;
		}
		if (0 == strcmp(secondaryIp.c_str(), interfaceIp.c_str())) {
			LogTrace(0, "Interface and Secondary IPs match. Return 0");
			LogTrace(0, "Leaving getConfiguredRole");
			return 0;
		}
   }

	logger.logMsg(ERROR_FLAG, 0, "Neither of [%s] and [%s] [%s]",
					  primaryIp.c_str(), secondaryIp.c_str(),
					  "are configured in the network interfaces");
	LogError(0, "Exiting");
	exit(1);
}

