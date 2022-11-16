#ifndef __INGW_FT_PKT_ASP_ACTV_H__
#define __INGW_FT_PKT_ASP_ACTV_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAspActive: public INGwFtPktMsg
{
   public:

      INGwFtPktAspActive();
      virtual ~INGwFtPktAspActive(void);

			void
			initialize(short senderid, short receiverid, M3uaAspAct& psp);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getPspData(M3uaAspAct &aspActv);

			void
			setPspData(M3uaAspAct &aspActv);

      //std::string toLog(void) const;

   private:
			M3uaAspAct m_aspActv;
			bool m_isAspActvSet;

      INGwFtPktAspActive& operator= (const INGwFtPktAspActive& arSelf);

      INGwFtPktAspActive(const INGwFtPktAspActive& arSelf);
};

#endif

