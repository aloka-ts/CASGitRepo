//********************************************************************
//
//     File:    INGwFtPktCreateTcapSession.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh M. Tripathi 1/12/11     Initial Creation
//********************************************************************
#ifndef INGW_FT_PKT_CREATE_TCAP_SESSION_H
#define INGW_FT_PKT_CREATE_TCAP_SESSION_H

#define MESSAGE_FT
#include <INGwFtPacket/INGwFtPktMsg.h>
#include <string>
#include <vector>
#include <INGwCommonTypes/INCCommons.h>
using namespace std;

//const short MSG_TCAP_INBOUND  = 1;
//const short MSG_TCAP_OUTBOUND = 2;

typedef struct TcapMsgInfo {
	U32 m_pc;
	U8	m_ssn;
  
  U32 m_stackDialogue;
  U32 m_userDialogue;
	U32 m_bufLen;
  U8* m_buf;
  AppInstId m_appInstId;
  int m_seqNum;
  bool isReplicated;
	TcapMsgInfo () {
    isReplicated = true;
		m_bufLen = 0;
    m_ssn    = 0; 
    m_pc     = 0;
    m_buf    = 0;
    m_stackDialogue = 0;
    m_userDialogue  = 0;
    m_seqNum = 0;
    memset(&m_appInstId,0,sizeof(AppInstId));
	}
  
} t_tcapMsgInfo;

class INGwFtPktCreateTcapSession : public INGwFtPktMsg
{
   public:

      INGwFtPktCreateTcapSession(void);

      virtual ~INGwFtPktCreateTcapSession();

      int 
			depacketize(const char* apcData, int asSize, int version);

      int 
			packetize(char** apcData, int version);

      void 
			initialize(short p_tcapMsgType, short srcid, short destid);

      std::string 
			toLog(void) const;

			short 
			getTcapMsgMsgType();

      int
      INGwFtPktCreateTcapSession::setTcapMsgInfo(U8* p_buf, U32 p_bufLen, 
                                           U32 p_stackDialogue,
                                           U32 p_userDialogue, int p_seqNum, 
                                           U8 p_ssn, AppInstId p_appInstId, 
                                           short p_msgType, 
                                           string p_sasIp,
                                           SccpAddr p_srcAddr,
                                           SccpAddr p_dstAddr,
                                           bool isReplicated);

      t_tcapMsgInfo*
      INGwFtPktCreateTcapSession::getTcapMsgInfo();

      //misnomer
			short m_tcapMsgMsgType;
			t_tcapMsgInfo m_info;
      
      SccpAddr m_srcAddr;
      SccpAddr m_dstAddr;
      string   m_sasIp; 
      int      m_billingNo;
   private:
      
      INGwFtPktCreateTcapSession& operator= (const INGwFtPktCreateTcapSession& arSelf);
      INGwFtPktCreateTcapSession(const INGwFtPktCreateTcapSession& arSelf);
};

#endif // INGW_FT_PKT_CREATE_TCAP_SESSION_H 

