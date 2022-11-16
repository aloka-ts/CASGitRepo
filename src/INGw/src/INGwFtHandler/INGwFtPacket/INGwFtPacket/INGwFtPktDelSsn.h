#ifndef __INGW_FT_PKT_DEL_SSN_H__
#define __INGW_FT_PKT_DEL_SSN_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktDelSsn: public INGwFtPktMsg
{
   public:

      INGwFtPktDelSsn();
      virtual ~INGwFtPktDelSsn(void);

			void
			initialize(short senderid, short receiverid, DelLocalSsn& delSsn);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getSsnData(DelLocalSsn &delRte);

			void
			setSsnData(DelLocalSsn &delRte);

      std::string toLog(void) const;

   private:
			DelLocalSsn m_delSsn;
			bool m_isDelSsnSet;

      INGwFtPktDelSsn& operator= (const INGwFtPktDelSsn& arSelf);

      INGwFtPktDelSsn(const INGwFtPktDelSsn& arSelf);
};

#endif

