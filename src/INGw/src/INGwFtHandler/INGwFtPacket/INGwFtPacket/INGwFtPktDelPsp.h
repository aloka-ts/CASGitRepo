#ifndef __INGW_FT_PKT_DEL_PSP_H__
#define __INGW_FT_PKT_DEL_PSP_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktDelPsp: public INGwFtPktMsg
{
   public:

      INGwFtPktDelPsp();
      virtual ~INGwFtPktDelPsp(void);

			void
			initialize(short senderid, short receiverid, DelPsp& delPsp);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getPspData(DelPsp &delPsp);

			void
			setPspData(DelPsp &delPsp);

      std::string toLog(void) const;

   private:
			DelPsp m_delPsp;
			bool m_isDelPspSet;

      INGwFtPktDelPsp& operator= (const INGwFtPktDelPsp& arSelf);

      INGwFtPktDelPsp(const INGwFtPktDelPsp& arSelf);
};

#endif

