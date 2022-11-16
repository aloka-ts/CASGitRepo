#ifndef __INGW_FT_PKT_DEL_ENDPOINT_H__
#define __INGW_FT_PKT_DEL_ENDPOINT_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktDelEp: public INGwFtPktMsg
{
   public:

      INGwFtPktDelEp();
      virtual ~INGwFtPktDelEp(void);

			void
			initialize(short senderid, short receiverid, DelEndPoint& delEp);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getEpData(DelEndPoint &deladdEp);

			void
			setEpData(DelEndPoint &delEp);

      std::string toLog(void) const;

   private:
			DelEndPoint m_delEp;
			bool m_isDelEpSet;

      INGwFtPktDelEp& operator= (const INGwFtPktDelEp& arSelf);

      INGwFtPktDelEp(const INGwFtPktDelEp& arSelf);
};

#endif

