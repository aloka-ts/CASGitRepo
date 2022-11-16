#ifndef __INGW_FT_PKT_DEL_ROUTE_H__
#define __INGW_FT_PKT_DEL_ROUTE_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktDelRoute: public INGwFtPktMsg
{
   public:

      INGwFtPktDelRoute();
      virtual ~INGwFtPktDelRoute(void);

			void
			initialize(short senderid, short receiverid, DelRoute& delRoute);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getRouteData(DelRoute &delRte);

			void
			setRouteData(DelRoute &delRte);

      std::string toLog(void) const;

   private:
			DelRoute m_delRoute;
			bool m_isDelRouteSet;

      INGwFtPktDelRoute& operator= (const INGwFtPktDelRoute& arSelf);

      INGwFtPktDelRoute(const INGwFtPktDelRoute& arSelf);
};

#endif

