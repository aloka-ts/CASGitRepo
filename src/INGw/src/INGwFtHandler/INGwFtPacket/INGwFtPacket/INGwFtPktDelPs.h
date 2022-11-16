#ifndef __INGW_FT_PKT_DEL_PS_H__
#define __INGW_FT_PKT_DEL_PS_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktDelPs: public INGwFtPktMsg
{
   public:

      INGwFtPktDelPs();
      virtual ~INGwFtPktDelPs(void);

			void
			initialize(short senderid, short receiverid, DelPs& delPs);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getPsData(DelPs &delPs);

			void
			setPsData(DelPs &delPs);

      std::string toLog(void) const;

   private:
			DelPs m_delPs;
			bool m_isDelPsSet;

      INGwFtPktDelPs& operator= (const INGwFtPktDelPs& arSelf);

      INGwFtPktDelPs(const INGwFtPktDelPs& arSelf);
};

#endif

