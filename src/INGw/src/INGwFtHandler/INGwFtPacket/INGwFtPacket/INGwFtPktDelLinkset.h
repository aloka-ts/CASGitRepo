#ifndef __INGW_FT_PKT_DEL_LINKSET_H__
#define __INGW_FT_PKT_DEL_LINKSET_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktDelLinkset: public INGwFtPktMsg
{
   public:

      INGwFtPktDelLinkset();
      virtual ~INGwFtPktDelLinkset(void);

			void
			initialize(short senderid, short receiverid, DelLinkSet& delLinkset);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getLinksetData(DelLinkSet &delRte);

			void
			setLinksetData(DelLinkSet &delRte);

      std::string toLog(void) const;

   private:
			DelLinkSet m_delLinkset;
			bool m_isDelLinksetSet;

      INGwFtPktDelLinkset& operator= (const INGwFtPktDelLinkset& arSelf);

      INGwFtPktDelLinkset(const INGwFtPktDelLinkset& arSelf);
};

#endif

