#ifndef __INGW_FT_PKT_ADD_M3UAASSOCUP_H__
#define __INGW_FT_PKT_ADD_M3UAASSOCUP_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktM3uaAssocUp: public INGwFtPktMsg
{
   public:

      INGwFtPktM3uaAssocUp();
      virtual ~INGwFtPktM3uaAssocUp(void);

			void
			initialize(short senderid, short receiverid, M3uaAssocUp& m3uaAssocUp);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getM3uaAssocUpData(M3uaAssocUp &assocUp);

			void
			setM3uaAssocUpData(M3uaAssocUp &assocUp);

      std::string toLog(void) const;

   private:
			M3uaAssocUp m_m3uaAssocUp;
			bool m_isM3uaAssocUpSet;

      INGwFtPktM3uaAssocUp& operator= (const INGwFtPktM3uaAssocUp& arSelf);

      INGwFtPktM3uaAssocUp(const INGwFtPktM3uaAssocUp& arSelf);
};

#endif

