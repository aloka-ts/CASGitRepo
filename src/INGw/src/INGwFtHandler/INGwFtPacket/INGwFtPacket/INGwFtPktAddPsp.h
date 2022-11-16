#ifndef __INGW_FT_PKT_ADD_PSP_H__
#define __INGW_FT_PKT_ADD_PSP_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAddPsp: public INGwFtPktMsg
{
   public:

      INGwFtPktAddPsp();
      virtual ~INGwFtPktAddPsp(void);

			void
			initialize(short senderid, short receiverid, AddPsp& psp);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getPspData(AddPsp &addPsp);

			void
			setPspData(AddPsp &addPsp);

      std::string toLog(void) const;

   private:
			AddPsp m_addPsp;
			bool m_isAddPspSet;

      INGwFtPktAddPsp& operator= (const INGwFtPktAddPsp& arSelf);

      INGwFtPktAddPsp(const INGwFtPktAddPsp& arSelf);
};

#endif

