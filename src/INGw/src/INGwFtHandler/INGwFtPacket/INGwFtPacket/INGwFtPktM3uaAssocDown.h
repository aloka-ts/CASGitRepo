#ifndef __INGW_FT_PKT_DEL_M3UAASSOCDOWN_H__
#define __INGW_FT_PKT_DEL_M3UAASSOCDOWN_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktM3uaAssocDown: public INGwFtPktMsg
{
   public:

      INGwFtPktM3uaAssocDown();
      virtual ~INGwFtPktM3uaAssocDown(void);

			void
			initialize(short senderid, short receiverid, M3uaAssocDown& assocDown);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getNwData(M3uaAssocDown &assocDown);

			void
			setNwData(M3uaAssocDown &assocDown);

      std::string toLog(void) const;

   private:
			M3uaAssocDown m_m3uaAssocDown;
			bool m_isM3uaAssocDownSet;

      INGwFtPktM3uaAssocDown& operator= (const INGwFtPktM3uaAssocDown& arSelf);

      INGwFtPktM3uaAssocDown(const INGwFtPktM3uaAssocDown& arSelf);
};

#endif

