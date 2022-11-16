#ifndef __INGW_FT_PKT_ADD_GT_ADDR_MAP_H__
#define __INGW_FT_PKT_ADD_GT_ADDR_MAP_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAddGtAddrMap: public INGwFtPktMsg
{
   public:

      INGwFtPktAddGtAddrMap();
      virtual ~INGwFtPktAddGtAddrMap(void);

			void
			initialize(short senderid, short receiverid, AddAddrMapCfg& addAddrMap);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getGtAddrMapData(AddAddrMapCfg &addAddrMap);

			void
			setGtAddrMapData(AddAddrMapCfg &addAddrMap);

      std::string toLog(void) const;

   private:
			AddAddrMapCfg m_addGtAddrMap;
			bool m_isAddGtAddrMapSet;

      INGwFtPktAddGtAddrMap& operator= (const INGwFtPktAddGtAddrMap& arSelf);

      INGwFtPktAddGtAddrMap(const INGwFtPktAddGtAddrMap& arSelf);
};

#endif

