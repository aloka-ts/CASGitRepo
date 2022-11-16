//********************************************************************
//
//     File:    INGwFtPktTcapCallSeqAck.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh Tripathi 23/02/12       Initial Creation
//********************************************************************

#ifndef INGW_FT_PKT_CALL_SEQ_ACK_H_
#define INGW_FT_PKT_CALL_SEQ_ACK_H_

#include <INGwFtPacket/INGwFtPktMsgDefine.h>
#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwCommonTypes/INCCommons.h>

class INGwFtPktTcapCallSeqAck : public INGwFtPktMsg
{
   public:

      INGwFtPktTcapCallSeqAck(void);

      virtual ~INGwFtPktTcapCallSeqAck();

      int depacketize(const char* apcData, int asSize, int version);

      int packetize(char** apcData, int version);

      void initialize (U8 p_origin, U32 p_dialogueId, 
                       int p_seqNum, short srcid, short destid,
                       bool p_isReplicated = true);

      std::string toLog(void) const;

			int getSeqNum();
      U32 getDialogueId();
      U8  getOrigin();
      bool isReplicated();
   private:
			int   m_seqNum;
      U32   m_dialogueId;
      bool  m_isReplicated;
      //m_origin 1 if INC is receiving 200 OK 
      //2 if INC is sending 200 OK 
      U8   m_origin;

      INGwFtPktTcapCallSeqAck& operator= (const INGwFtPktTcapCallSeqAck& arSelf);
      INGwFtPktTcapCallSeqAck(const INGwFtPktTcapCallSeqAck& arSelf);
};

#endif // INGW_FT_PKT_CALL_SEQ_ACK_H_

