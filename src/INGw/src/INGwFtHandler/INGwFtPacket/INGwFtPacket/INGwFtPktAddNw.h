#ifndef __INGW_FT_PKT_ADD_NW_H__
#define __INGW_FT_PKT_ADD_NW_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAddNw: public INGwFtPktMsg
{
   public:

      INGwFtPktAddNw();
      virtual ~INGwFtPktAddNw(void);

			void
			initialize(short senderid, short receiverid, AddNetwork &addNw);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getNwData(AddNetwork &addNw);

			void
			setNwData(AddNetwork &addNw);

      std::string toLog(void) const;

   private:
			AddNetwork m_addNw;
			bool m_isAddNwSet;

      INGwFtPktAddNw& operator= (const INGwFtPktAddNw& arSelf);

      INGwFtPktAddNw(const INGwFtPktAddNw& arSelf);
};

#endif

