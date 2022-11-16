#ifndef __INGW_FT_PKT_DEL_GT_H__
#define __INGW_FT_PKT_Del_GT_H__

#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwStackManager/INGwSmIncludes.h>
#include <string>

class INGwFtPktDelGtRule: public INGwFtPktMsg
{
   public:

      INGwFtPktDelGtRule();
      virtual ~INGwFtPktDelGtRule(void);

			void
			initialize(short senderid, short receiverid, DelGtRule& DelRule);

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

			void
			getGtRuleData(DelGtRule &delRule);

			void
			setGtRuleData(DelGtRule &delRule);

      std::string toLog(void) const;

   private:
			DelGtRule m_delGtRule;
			bool m_isDelGtRuleSet;

      INGwFtPktDelGtRule& operator= (const INGwFtPktDelGtRule& arSelf);

      INGwFtPktDelGtRule(const INGwFtPktDelGtRule& arSelf);
};

#endif

