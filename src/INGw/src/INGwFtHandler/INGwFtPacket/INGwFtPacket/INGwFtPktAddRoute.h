#ifndef __INGW_FT_PKT_ADD_ROUTE_H__
#define __INGW_FT_PKT_ADD_ROUTE_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAddRoute: public INGwFtPktMsg
{
   public:

      INGwFtPktAddRoute();
      virtual ~INGwFtPktAddRoute(void);

			void
			initialize(short senderid, short receiverid, AddRoute& addRte);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getRouteData(AddRoute &addRte);

			void
			setRouteData(AddRoute &addRte);

      std::string toLog(void) const;

   private:
			AddRoute m_addRoute;
			bool m_isAddRouteSet;

      INGwFtPktAddRoute& operator= (const INGwFtPktAddRoute& arSelf);

      INGwFtPktAddRoute(const INGwFtPktAddRoute& arSelf);
};

#endif

