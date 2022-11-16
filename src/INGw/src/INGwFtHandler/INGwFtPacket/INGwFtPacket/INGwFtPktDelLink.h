#ifndef __INGW_FT_PKT_DEL_LINK_H__
#define __INGW_FT_PKT_DEL_LINK_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktDelLink: public INGwFtPktMsg
{
   public:

      INGwFtPktDelLink();
      virtual ~INGwFtPktDelLink(void);

			void
			initialize(short senderid, short receiverid, DelLink& delLnk);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getLinkData(DelLink &delLnk);

			void
			setLinkData(DelLink &delLnk);

      std::string toLog(void) const;

   private:
			DelLink m_delLink;
			bool m_isDelLinkSet;

      INGwFtPktDelLink& operator= (const INGwFtPktDelLink& arSelf);

      INGwFtPktDelLink(const INGwFtPktDelLink& arSelf);
};

#endif

