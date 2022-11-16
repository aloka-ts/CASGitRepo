#ifndef __INGW_FT_PKT_DEL_NW_H__
#define __INGW_FT_PKT_DEL_NW_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktDelNw: public INGwFtPktMsg
{
   public:

      INGwFtPktDelNw();
      virtual ~INGwFtPktDelNw(void);

			void
			initialize(short senderid, short receiverid, DelNetwork& delNw);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getNwData(DelNetwork &delNw);

			void
			setNwData(DelNetwork &delNw);

      std::string toLog(void) const;

   private:
			DelNetwork m_delNw;
			bool m_isDelNwSet;

      INGwFtPktDelNw& operator= (const INGwFtPktDelNw& arSelf);

      INGwFtPktDelNw(const INGwFtPktDelNw& arSelf);
};

#endif

