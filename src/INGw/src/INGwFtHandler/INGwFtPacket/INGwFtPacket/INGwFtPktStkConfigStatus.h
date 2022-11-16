#ifndef __INGW_FT_PKT_STK_CONFIG_STATUS_H__
#define __INGW_FT_PKT_STK_CONFIG_STATUS_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktStkConfigStatus: public INGwFtPktMsg
{
   public:

      INGwFtPktStkConfigStatus();
      virtual ~INGwFtPktStkConfigStatus(void);

			void
			initialize(short senderid, short receiverid, StkConfigStatus &stat);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getStatusData(StkConfigStatus &stat);

      std::string toLog(void) const;

   private:
			StkConfigStatus m_stat;

      INGwFtPktStkConfigStatus& operator= 
					(const INGwFtPktStkConfigStatus& arSelf);

      INGwFtPktStkConfigStatus(const INGwFtPktStkConfigStatus& arSelf);
};

#endif

