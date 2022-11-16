#ifndef __INGW_FT_PKT_ADD_LINK_H__
#define __INGW_FT_PKT_ADD_LINK_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAddLink: public INGwFtPktMsg
{
   public:

      INGwFtPktAddLink();
      virtual ~INGwFtPktAddLink(void);

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

      INGwFtPktAddLink& operator= (const INGwFtPktAddLink& arSelf);

      INGwFtPktAddLink(const INGwFtPktAddLink& arSelf);
};

#endif

