//********************************************************************
//
//     File:    INGwFtPktUpdateTcapSession.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh M. Tripathi 27/02/12     Initial Creation
//********************************************************************
#define MESSAGE_FT
#include <INGwFtPacket/INGwFtPktUpdateTcapSession.h>

INGwFtPktUpdateTcapSession::INGwFtPktUpdateTcapSession():m_origin(0)
{
    mMsgData.msMsgType = MSG_UPDATE_TCAP_SESSION;
}

INGwFtPktUpdateTcapSession::~INGwFtPktUpdateTcapSession()
{
}

void 
INGwFtPktUpdateTcapSession::initialize(U8 p_origin, short p_srcid,
                                       short p_destid)
{
	mMsgData.msSender   = p_srcid;
	mMsgData.msReceiver = p_destid;
	m_origin = p_origin;
}

int 
INGwFtPktUpdateTcapSession::packetize(char** apcData, int version)
{
 	if (1 != version) {
		return 0;
	}
  *apcData = NULL;
  
  char  lBuf[1024];
  int lBufLen = 0;

  m_origin = htons(m_origin);
	memcpy(lBuf + lBufLen, (void*)&m_origin, SIZE_OF_CHAR);
	lBufLen += SIZE_OF_CHAR;
 
  int lseqNum     = htons(m_info.m_seqNum);
  memcpy(lBuf + lBufLen, (void*)&lseqNum, SIZE_OF_INT);
  lBufLen +=  SIZE_OF_INT;

  U32 lMsgLen = htons(m_info.m_bufLen);   
  memcpy(lBuf + lBufLen, (void*)&lMsgLen, SIZE_OF_LONG);
  lBufLen +=  SIZE_OF_LONG;
  
  memcpy(lBuf + lBufLen,(void*)m_info.m_buf, m_info.m_bufLen);
  lBufLen += m_info.m_bufLen;

   int offset = 
      INGwFtPktMsg::createPacket(lBufLen , apcData, version);

   char *pkt = *apcData;

   memcpy(pkt + offset, (void*)lBuf, lBufLen);
   offset += lBufLen;

   return offset;
}

std::string 
INGwFtPktUpdateTcapSession::toLog(void) const
{
	std::ostringstream    oStr;
	std::string lRoleStr = (m_origin == MSG_TCAP_INBOUND)? 
					 	" - MSG_TCAP_INBOUND" : (m_origin == MSG_TCAP_OUTBOUND)?
					  " - MSG_TCAP_OUTBOUND" : " - MSG_TCAP_UNKNOWN";

	oStr << INGwFtPktMsg::toLog();

  oStr <<" TC-SeqNum: "<<m_info.m_seqNum;

	oStr << " ,INGwFtPktUpdateTcapSession | Msg: "  <<(int) m_origin <<" Buf Len : "<< m_info.m_bufLen
       << lRoleStr.c_str()<<endl;
  char lBuf[1000];
  short lBufLen = 0;

	for (int i=0; i  < m_info.m_bufLen; i++) {
   if(i%16 == 0) {
     lBufLen += sprintf(lBuf + lBufLen, "\t\n", m_info.m_buf[i]);
   }
   lBufLen += sprintf(lBuf + lBufLen, "%02X ", m_info.m_buf[i]);
	}
   
  std::string str( reinterpret_cast<char*>(lBuf), lBufLen);
  oStr<<"\n BYTE ARRAY : "<<str.c_str()<< endl;
	return oStr.str();
}

U8
INGwFtPktUpdateTcapSession::getOrigin()
{
	return m_origin;
}

int
INGwFtPktUpdateTcapSession::setTcapMsgInfo(U8* p_buf, U32 p_bufLen, 
                                           int p_seqNum,bool p_isReplicated)
{

  if(p_buf == 0 || p_bufLen ==0) {
    return -1; 
  }

  m_info.m_isReplicated = p_isReplicated;
  if(p_isReplicated) {
    //printf("\nremft INGwFtPktUpdateTcapSession::setTcapMsgInfo p_isReplicated"
    //       " true!!!!!");
    m_info.m_buf    =  p_buf;
  } else {
    //printf("\nremft INGwFtPktUpdateTcapSession::setTcapMsgInfo p_isReplicated"
    //       " false should be accumulating"); 
    m_info.m_buf    =  new (nothrow) U8[p_bufLen];
    memcpy(m_info.m_buf, p_buf, p_bufLen);
  } 
  fflush(stdout);//remft
  m_info.m_bufLen =  p_bufLen;
  m_info.m_seqNum  = p_seqNum;
  return 0; 
}

bool 
INGwFtPktUpdateTcapSession::isReplicated(){
  return m_info.m_isReplicated;
}

t_tcapMsgBuffer*
INGwFtPktUpdateTcapSession::getTcapMsgBuffer() {
  //printf("\nremft in INGwFtPktUpdateTcapSession::getTcapMsgBuffer"
  //" origin<%s> isReplicated<%d>",
  //(MSG_TCAP_OUTBOUND == m_origin?"MSG_TCAP_OUTBOUND":"MSG_TCAP_INBOUND"),
  // m_info.m_isReplicated);

  //fflush(stdout);//remft
  if((MSG_TCAP_OUTBOUND == m_origin) && (!m_info.m_isReplicated)) {
    //printf("\nremft INGwFtPktUpdateTcapSession::getTcapMsgBuffer allocating");
    //fflush(stdout);//remft
    return (new (nothrow) t_tcapMsgBuffer(m_info));  
  }
  else {
   //printf("\nremft INGwFtPktUpdateTcapSession::getTcapMsgBuffer not allocating");
   //fflush(stdout);//remft
   return &m_info;
  }
}

int 
INGwFtPktUpdateTcapSession::depacketize(const char* apcData, int asSize, int version) {
	if (1 != version) {
		return 0;
	}

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);
	memcpy(&m_origin, apcData + offset, SIZE_OF_CHAR);
	m_origin = ntohs(m_origin);
	offset += SIZE_OF_CHAR;

  int l_seqNum= 0;
  memcpy(&l_seqNum, apcData + offset, SIZE_OF_INT);
  m_info.m_seqNum = ntohs(l_seqNum);
  offset += SIZE_OF_INT;

  U32  lbufLen = 0;
  memcpy(&lbufLen, apcData + offset, SIZE_OF_LONG);
  m_info.m_bufLen = ntohs(lbufLen);
  offset += SIZE_OF_LONG;
  
  //this is to be deleted further +delete+
  m_info.m_buf = new unsigned char[m_info.m_bufLen];
  
  memcpy(m_info.m_buf, apcData+offset, m_info.m_bufLen);
  offset += m_info.m_bufLen;

  return offset;   
}

U32
INGwFtPktUpdateTcapSession::getDialogueId()
{
  U32 lDlgId = 0;
  if(m_info.m_buf && m_info.m_bufLen){
    U8* lMsgBody = m_info.m_buf; 
    int lLenOffset = 0;
    if(0x00 != (lMsgBody[1] & 0x80)){
      lLenOffset = (lMsgBody[1]) - 128;
    }

    lDlgId = (lDlgId | lMsgBody[4 + lLenOffset]) << 8;
    lDlgId = (lDlgId | lMsgBody[5 + lLenOffset]) << 8;
    lDlgId = (lDlgId | lMsgBody[6 + lLenOffset]) << 8;
    lDlgId = lDlgId | lMsgBody[7 + lLenOffset];
  }
  return lDlgId; 
}

