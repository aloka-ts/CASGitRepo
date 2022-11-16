//********************************************************************
//
//     File:    INGwFtPktTermTcapSession.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh Tripathi 23/02/12       Initial Creation
//********************************************************************

#ifndef INGW_FT_PKT_TERM_TCAP_SESSION_H_
#define INGW_FT_PKT_TERM_TCAP_SESSION_H_

#include <INGwFtPacket/INGwFtPktMsgDefine.h>
#include <INGwFtPacket/INGwFtPktMsg.h>
#include <INGwCommonTypes/INCCommons.h>

class INGwFtPktTermTcapSession : public INGwFtPktMsg
{
   public:

      INGwFtPktTermTcapSession(void);

      virtual ~INGwFtPktTermTcapSession();

      int depacketize(const char* apcData, int asSize, int version);

      int packetize(char** apcData, int version);

      void initialize(U32 p_dialogueId,
                      short p_selfId, short p_peerId, bool p_isReplicated=true);

      std::string toLog(void) const;

			int getSeqNum();
      U32 getDialogueId();
      U8 getOrigin();
      bool receivedFromPeer();
   private:
      bool isReplicated;
			int m_seqNum;
      U32   m_dialogueId;
      //m_origin 1 if INC is receiving 200 OK 
      //2 if INC is sending 200 OK 
      U8   m_origin;

      INGwFtPktTermTcapSession& operator= (const INGwFtPktTermTcapSession& arSelf);
      INGwFtPktTermTcapSession(const INGwFtPktTermTcapSession& arSelf);
};

#endif // INGW_FT_PKT_TERM_TCAP_SESSION_H_

