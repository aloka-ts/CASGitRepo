#ifndef __INGW_FT_PKT_ADD_LINKSET_H__
#define __INGW_FT_PKT_ADD_LINKSET_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAddLinkset: public INGwFtPktMsg
{
   public:

      INGwFtPktAddLinkset();
      virtual ~INGwFtPktAddLinkset(void);

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

      INGwFtPktAddLinkset& operator= (const INGwFtPktAddLinkset& arSelf);

      INGwFtPktAddLinkset(const INGwFtPktAddLinkset& arSelf);
};

#endif

