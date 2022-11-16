#ifndef __INGW_FT_PKT_ADD_SSN_H__
#define __INGW_FT_PKT_ADD_SSN_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAddSsn: public INGwFtPktMsg
{
   public:

      INGwFtPktAddSsn();
      virtual ~INGwFtPktAddSsn(void);

			void
			initialize(short senderid, short receiverid, AddLocalSsn& ssn);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getSsnData(AddLocalSsn &addSsn);

			void
			setSsnData(AddLocalSsn &addSsn);

      std::string toLog(void) const;

   private:
			AddLocalSsn m_addSsn;
			bool m_isAddSsnSet;

      INGwFtPktAddSsn& operator= (const INGwFtPktAddSsn& arSelf);

      INGwFtPktAddSsn(const INGwFtPktAddSsn& arSelf);
};

#endif

