#ifndef __INGW_FT_PKT_ADD_ENDPOINT_H__
#define __INGW_FT_PKT_ADD_ENDPOINT_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAddEp: public INGwFtPktMsg
{
   public:

      INGwFtPktAddEp();
      virtual ~INGwFtPktAddEp(void);

			void
			initialize(short senderid, short receiverid, AddEndPoint& ep);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getEpData(AddEndPoint &addEp);

			void
			setEpData(AddEndPoint &addEp);

      std::string toLog(void) const;

   private:
			AddEndPoint m_addEp;
			bool m_isAddEpSet;

      INGwFtPktAddEp& operator= (const INGwFtPktAddEp& arSelf);

      INGwFtPktAddEp(const INGwFtPktAddEp& arSelf);
};

#endif

