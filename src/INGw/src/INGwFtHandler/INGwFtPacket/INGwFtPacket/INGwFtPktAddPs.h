#ifndef __INGW_FT_PKT_ADD_PS_H__
#define __INGW_FT_PKT_ADD_PS_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAddPs: public INGwFtPktMsg
{
   public:

      INGwFtPktAddPs();
      virtual ~INGwFtPktAddPs(void);

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

      INGwFtPktAddPs& operator= (const INGwFtPktAddPs& arSelf);

      INGwFtPktAddPs(const INGwFtPktAddPs& arSelf);
};

#endif

