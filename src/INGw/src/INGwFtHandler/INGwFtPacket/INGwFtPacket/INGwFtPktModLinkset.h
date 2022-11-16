#ifndef __INGW_FT_PKT_MOD_LINKSET_H__
#define __INGW_FT_PKT_MOD_LINKSET_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktModLinkset: public INGwFtPktMsg
{
   public:

      INGwFtPktModLinkset();
      virtual ~INGwFtPktModLinkset(void);

			void
			initialize(short senderid, short receiverid, AddLinkSet& addLs);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getLinksetData(AddLinkSet &addLnkset);

			void
			setLinksetData(AddLinkSet &addLnkset);

      std::string toLog(void) const;

   private:
			AddLinkSet m_addLinkset;
			bool m_isAddLinksetSet;

      INGwFtPktModLinkset& operator= (const INGwFtPktModLinkset& arSelf);

      INGwFtPktModLinkset(const INGwFtPktModLinkset& arSelf);
};

#endif

