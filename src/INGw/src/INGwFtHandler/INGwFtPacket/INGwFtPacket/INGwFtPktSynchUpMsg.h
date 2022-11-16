//********************************************************************
//
//     File:    INGwFtPktSynchUpMsg.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh M. Tripathi 1/12/11     Initial Creation
//********************************************************************
#ifndef INGW_FT_PKT_SYNCHUP_MSG_H
#define INGW_FT_PKT_SYNCHUP_MSG_H

#define MESSAGE_FT
#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwTcapProvider/INGwTcapSession.h>
#include <string>
#include <vector>
#include <INGwCommonTypes/INCCommons.h>
using namespace std;


class INGwFtPktSynchUpMsg : public INGwFtPktMsg
{
   public:

      INGwFtPktSynchUpMsg(void);

      virtual ~INGwFtPktSynchUpMsg();

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

      void 
			initialize(short srcid, short destid, U8* p_buf, int p_bufLen,
                 int p_faultCnt);

      std::string 
			toLog(void) const;

		  int	
			getFaultCount();

      U8*
      getSerializedBuffer();
     
      int
      getBufLen();

			int mFaultCnt;
      U8* m_buf;
      int m_bufLen;
   private:
      map<int,INGwTcapSession*> *mpSessionMap;
      INGwFtPktSynchUpMsg& operator= (const INGwFtPktSynchUpMsg& arSelf);
      INGwFtPktSynchUpMsg(const INGwFtPktSynchUpMsg& arSelf);
};

#endif //INGW_FT_PKT_SYNCHUP_MSG_H

