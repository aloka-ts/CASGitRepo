#ifndef __INGW_FT_PKT_MOD_LINK_H__
#define __INGW_FT_PKT_MOD_LINK_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktModLink: public INGwFtPktMsg
{
   public:

      INGwFtPktModLink();
      virtual ~INGwFtPktModLink(void);

			void
			initialize(short senderid, short receiverid, AddLink& addLnk);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getLinkData(AddLink &addLnk);

			void
			setLinkData(AddLink &addLnk);

      std::string toLog(void) const;

   private:
			AddLink m_addLink;
			bool m_isAddLinkSet;

      INGwFtPktModLink& operator= (const INGwFtPktModLink& arSelf);

      INGwFtPktModLink(const INGwFtPktModLink& arSelf);
};

#endif

