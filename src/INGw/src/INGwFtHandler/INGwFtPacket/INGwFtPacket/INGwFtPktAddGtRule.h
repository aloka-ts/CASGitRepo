#ifndef __INGW_FT_PKT_ADD_GT_H__
#define __INGW_FT_PKT_ADD_GT_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktAddGtRule: public INGwFtPktMsg
{
   public:

      INGwFtPktAddGtRule();
      virtual ~INGwFtPktAddGtRule(void);

			void
			initialize(short senderid, short receiverid, AddGtRule& addRule);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getGtRuleData(AddGtRule &addRule);

			void
			setGtRuleData(AddGtRule &addRule);

      std::string toLog(void) const;

   private:
			AddGtRule m_addGtRule;
			bool m_isAddGtRuleSet;

      INGwFtPktAddGtRule& operator= (const INGwFtPktAddGtRule& arSelf);

      INGwFtPktAddGtRule(const INGwFtPktAddGtRule& arSelf);
};

#endif

