#ifndef __INGW_FT_PKT_ASP_INACTV_H__
#define __INGW_FT_PKT_ASP_INACTV_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAspInActive: public INGwFtPktMsg
{
   public:

      INGwFtPktAspInActive();
      virtual ~INGwFtPktAspInActive(void);

			void
			initialize(short senderid, short receiverid, M3uaAspInAct& psp);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getPspData(M3uaAspInAct &asp);

			void
			setPspData(M3uaAspInAct &asp);

      //std::string toLog(void) const;

   private:
			M3uaAspInAct m_aspInActv;
			bool m_isAspInActvSet;

      INGwFtPktAspInActive& operator= (const INGwFtPktAspInActive& arSelf);

      INGwFtPktAspInActive(const INGwFtPktAspInActive& arSelf);
};

#endif

