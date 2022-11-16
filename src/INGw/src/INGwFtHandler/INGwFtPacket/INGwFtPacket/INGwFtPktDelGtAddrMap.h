#ifndef __INGW_FT_PKT_DEL_GT_ADDR_MAP_H__
#define __INGW_FT_PKT_DEL_GT_ADDR_MAP_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktDelGtAddrMap: public INGwFtPktMsg
{
   public:

      INGwFtPktDelGtAddrMap();
      virtual ~INGwFtPktDelGtAddrMap(void);

			void
			initialize(short senderid, short receiverid, DelAddrMapCfg& delAddrMap);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getGtAddrMapData(DelAddrMapCfg &delAddrMap);

			void
			setGtAddrMapData(DelAddrMapCfg &delAddrMap);

      std::string toLog(void) const;

   private:
			DelAddrMapCfg m_delGtAddrMap;
			bool m_isDelGtAddrMapSet;

      INGwFtPktDelGtAddrMap& operator= (const INGwFtPktDelGtAddrMap& arSelf);

      INGwFtPktDelGtAddrMap(const INGwFtPktDelGtAddrMap& arSelf);
};

#endif

