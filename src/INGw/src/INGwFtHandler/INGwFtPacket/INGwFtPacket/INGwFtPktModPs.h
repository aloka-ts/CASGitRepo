#ifndef __INGW_FT_PKT_MOD_PS_H__
#define __INGW_FT_PKT_MOD_PS_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktModPs: public INGwFtPktMsg
{
   public:

      INGwFtPktModPs();
      virtual ~INGwFtPktModPs(void);

			void
			initialize(short senderid, short receiverid, AddPs& ps);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getPsData(AddPs &addPs);

			void
			setPsData(AddPs &addPs);

      std::string toLog(void) const;

   private:
			AddPs m_addPs;
			bool m_isAddPsSet;

      INGwFtPktModPs& operator= (const INGwFtPktModPs& arSelf);

      INGwFtPktModPs(const INGwFtPktModPs& arSelf);
};

#endif

