//********************************************************************
//
//     File:    INGwFtPktUpdateTcapSession.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh M. Tripathi 1/12/11     Initial Creation
//********************************************************************
#ifndef INGW_FT_PKT_UPDATE_TCAP_SESSION_H
#define INGW_FT_PKT_UPDATE_TCAP_SESSION_H

#define MESSAGE_FT
#include <INGwFtPacket/INGwFtPktMsg.h>
#include <string>
#include <vector>
#include <INGwCommonTypes/INCCommons.h>
using namespace std;

//const short MSG_TCAP_INBOUND  = 1;
//const short MSG_TCAP_OUTBOUND = 2;
//converting structure to class
class t_tcapMsgBuffer {
  public:
	int m_bufLen;
  U8* m_buf;
  int m_seqNum;
  bool m_isReplicated;
  int m_userDlgId;

	t_tcapMsgBuffer() {
    m_isReplicated = true;
		m_bufLen = 0;
    m_buf    = 0;
    m_seqNum = 0;
    m_userDlgId = 0;
	}
 
  t_tcapMsgBuffer(const t_tcapMsgBuffer& pObj) {
    //deep copying is not required
    m_bufLen = pObj.m_bufLen;
    m_buf = pObj.m_buf;
    m_seqNum = pObj.m_seqNum;
    m_isReplicated = pObj.m_isReplicated;
    m_userDlgId = pObj.m_userDlgId;
  }

  ~t_tcapMsgBuffer() {

  }
};

class t_tcapMsgBuffer;
class INGwFtPktUpdateTcapSession : public INGwFtPktMsg
{
   public:

      INGwFtPktUpdateTcapSession(void);

      virtual ~INGwFtPktUpdateTcapSession();

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

      void 
			initialize(U8 p_origin, short srcid, short destid);

      std::string 
			toLog(void) const;

      int
      INGwFtPktUpdateTcapSession::setTcapMsgInfo(U8* p_buf, U32 p_bufLen, 
                                                int p_seqNum, 
                                                bool p_isReplicated = true);

      U32
      getDialogueId();

      t_tcapMsgBuffer *
      INGwFtPktUpdateTcapSession::getTcapMsgBuffer();

			t_tcapMsgBuffer m_info;
      
      bool
      isReplicated();
      
      U8
      getOrigin();
   private:

      U8 m_origin;
      INGwFtPktUpdateTcapSession& operator= (const INGwFtPktUpdateTcapSession& arSelf);
      INGwFtPktUpdateTcapSession(const INGwFtPktUpdateTcapSession& arSelf);
};

#endif // INGW_FT_PKT_UPDATE_TCAP_SESSION_H 

