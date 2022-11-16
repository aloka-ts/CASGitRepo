#ifndef __INGW_FT_PKT_DEL_USER_PART_H__
#define __INGW_FT_PKT_DEL_USER_PART_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktDelUserPart: public INGwFtPktMsg
{
   public:

      INGwFtPktDelUserPart();
      virtual ~INGwFtPktDelUserPart(void);

			void
			initialize(short senderid, short receiverid, DelUserPart& delUserPart);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getUserPartData(DelUserPart &delUserPart);

			void
			setUserPartData(DelUserPart &delUserPart);

      std::string toLog(void) const;

   private:
			DelUserPart m_delUserPart;
			bool m_isDelUserPartSet;

      INGwFtPktDelUserPart& operator= (const INGwFtPktDelUserPart& arSelf);

      INGwFtPktDelUserPart(const INGwFtPktDelUserPart& arSelf);
};

#endif

