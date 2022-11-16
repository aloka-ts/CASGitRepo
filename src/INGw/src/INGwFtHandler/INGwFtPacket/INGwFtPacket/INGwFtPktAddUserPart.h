#ifndef __INGW_FT_PKT_ADD_USER_PART_H__
#define __INGW_FT_PKT_ADD_USER_PART_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAddUserPart: public INGwFtPktMsg
{
   public:

      INGwFtPktAddUserPart();
      virtual ~INGwFtPktAddUserPart(void);

			void
			initialize(short senderid, short receiverid, AddUserPart& addUp);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getUserPartData(AddUserPart &addUp);

			void
			setUserPartData(AddUserPart &addUp);

      std::string toLog(void) const;

   private:
			AddUserPart m_addUserPart;
			bool m_isAddUserPartSet;

      INGwFtPktAddUserPart& operator= (const INGwFtPktAddUserPart& arSelf);

      INGwFtPktAddUserPart(const INGwFtPktAddUserPart& arSelf);
};

#endif

