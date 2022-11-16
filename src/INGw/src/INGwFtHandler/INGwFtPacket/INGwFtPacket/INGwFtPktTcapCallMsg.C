//********************************************************************
//
//     File:    INGwFtPktTcapCallMsg.h
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh M. Tripathi 1/12/11     Initial Creation
//********************************************************************
#define MESSAGE_FT
#include <INGwFtPacket/INGwFtPktTcapCallMsg.h>

INGwFtPktTcapCallMsg::INGwFtPktTcapCallMsg():m_tcapMsgMsgType(0)
{
    mMsgData.msMsgType = MSG_TCAP_CALL_DATA; 
}

INGwFtPktTcapCallMsg::~INGwFtPktTcapCallMsg()
{
}
void 
INGwFtPktTcapCallMsg::initialize(short p_tcapMsgMsgType, short p_srcid, short p_destid)
{
	mMsgData.msSender   = p_srcid;
	mMsgData.msReceiver = p_destid;
	m_tcapMsgMsgType    = p_tcapMsgMsgType;
}

int 
INGwFtPktTcapCallMsg::packetize(char** apcData, int version)
{
 	if (1 != version) {
		return 0;
	}
  *apcData = NULL;
  
  char  lBuf[1024];
  short value = 0;
  int lBufLen = 0;

  value = htons(m_tcapMsgMsgType);
	memcpy(lBuf + lBufLen, (void*)&value, SIZE_OF_SHORT);
	lBufLen += SIZE_OF_SHORT;
 
  int lseqNum     = htons(m_info.m_seqNum);
  memcpy(lBuf + lBufLen, (void*)&lseqNum, SIZE_OF_INT);
  lBufLen +=  SIZE_OF_INT;

  U8  lssn    = htons(m_info.m_ssn);
  memcpy(lBuf + lBufLen, (void*)&lssn, SIZE_OF_CHAR);
  lBufLen += SIZE_OF_CHAR;

  S16 lsuId = htons(m_info.m_appInstId.suId);
  memcpy(lBuf + lBufLen, (void*)&lsuId, SIZE_OF_SHORT);
  lBufLen += SIZE_OF_SHORT;

  S16 lspId = htons(m_info.m_appInstId.spId);
  memcpy(lBuf + lBufLen, (void*)&lspId, SIZE_OF_SHORT);
  lBufLen += SIZE_OF_SHORT;
 
  U32 lStackDialogue = htons(m_info.m_stackDialogue);
  memcpy(lBuf + lBufLen, (void*)&lStackDialogue, SIZE_OF_LONG);
  lBufLen += SIZE_OF_LONG;

  U32 lMsgLen = htons(m_info.m_bufLen);   
  memcpy(lBuf + lBufLen, (void*)&lMsgLen, SIZE_OF_LONG);
  lBufLen +=  SIZE_OF_LONG;
  
  if(NULL != m_info.m_buf) {
    memcpy(lBuf + lBufLen,(void*)m_info.m_buf, m_info.m_bufLen);
    lBufLen += m_info.m_bufLen;
  }
  else {
  }

   int offset = 
      INGwFtPktMsg::createPacket(lBufLen , apcData, version);

   char *pkt = *apcData;

   memcpy(pkt + offset, (void*)lBuf, lBufLen);
   offset += lBufLen;

   return offset;
}

std::string 
INGwFtPktTcapCallMsg::toLog(void) const
{
	std::ostringstream    oStr;
	std::string lRoleStr = (m_tcapMsgMsgType == MSG_TCAP_INBOUND)? 
					 	" - MSG_TCAP_INBOUND" : (m_tcapMsgMsgType== MSG_TCAP_OUTBOUND)?
					  " - MSG_TCAP_OUTBOUND" : " - MSG_TCAP_UNKNOWN";

	oStr << INGwFtPktMsg::toLog();

  oStr <<" suId: "<<m_info.m_appInstId.suId<<" spId: " <<m_info.m_appInstId.spId
       <<" TC-SeqNum: "<<m_info.m_seqNum;

  oStr <<" SSN: "<<(int)m_info.m_ssn <<" dlgId "<<m_info.m_stackDialogue<<endl; 

	oStr << " , Tcap Call Msg: "  << m_tcapMsgMsgType<< lRoleStr ;
  char lBuf[1000];
  short lBufLen = 0;

	for (int i=0; i  < m_info.m_bufLen; i++) {
   lBufLen += sprintf(lBuf + lBufLen, "%02X ", m_info.m_buf[i]);
	}
   
  std::string str( reinterpret_cast<char*>(lBuf), lBufLen);
  oStr<<"\n BYTE ARRAY : "<<str.c_str()<< endl;
	return oStr.str();
}

short 
INGwFtPktTcapCallMsg::getTcapMsgMsgType()
{
	return m_tcapMsgMsgType;
}

int
INGwFtPktTcapCallMsg::setTcapMsgInfo(U8* p_buf, U32 p_bufLen, U32 p_stackDialogue, U32 p_userDialogue, int p_seqNum, U8 p_ssn, AppInstId p_appInstId, short p_msgType)
{

  if(p_buf == 0 || p_bufLen ==0) {
    return -1; 
  }

  m_info.m_buf    =  p_buf;
  m_info.m_bufLen =  p_bufLen;
  m_info.m_stackDialogue = p_stackDialogue; 
  m_info.m_userDialogue  = p_userDialogue;
  memcpy(&(m_info.m_appInstId), &p_appInstId, sizeof(AppInstId));
  m_info.m_ssn = p_ssn;
  m_info.m_seqNum  = p_seqNum;
  m_tcapMsgMsgType = p_msgType;
  return 0; 
}


t_tcapMsgInfo*
INGwFtPktTcapCallMsg::getTcapMsgInfo(){
  return &m_info;
}

int 
INGwFtPktTcapCallMsg::depacketize(const char* apcData, int asSize, int version) {
	if (1 != version) {
		return 0;
	}

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);
	memcpy(&m_tcapMsgMsgType, apcData + offset, SIZE_OF_SHORT);
	m_tcapMsgMsgType= ntohs(m_tcapMsgMsgType);
	offset += SIZE_OF_SHORT;

  int l_seqNum= 0;
  memcpy(&l_seqNum, apcData + offset, SIZE_OF_INT);
  m_info.m_seqNum = ntohs(l_seqNum);
  offset += SIZE_OF_INT;

  U8 lssn = 0;
  memcpy(&lssn, apcData + offset, SIZE_OF_CHAR);  
  m_info.m_ssn = ntohs(lssn);
  offset += SIZE_OF_CHAR;

  S16 lsuId = 0;
  memcpy(&lsuId, apcData + offset, SIZE_OF_SHORT);
  m_info.m_appInstId.suId = ntohs(lsuId);
  offset += SIZE_OF_SHORT;

  S16 lspId = 0;
  memcpy(&lspId, apcData + offset, SIZE_OF_SHORT);
  m_info.m_appInstId.spId = ntohs(lspId);
  offset += SIZE_OF_SHORT; 

  U32 lStackDialogue = 0;
  memcpy(&lStackDialogue, apcData + offset, SIZE_OF_LONG);
  m_info.m_stackDialogue = ntohs(lStackDialogue);
  offset += SIZE_OF_LONG;

  U32  lbufLen = 0;
  memcpy(&lbufLen, apcData + offset, SIZE_OF_LONG);
  m_info.m_bufLen = ntohs(lbufLen);
  offset += SIZE_OF_LONG;
  
  //this is to be deleted further
  m_info.m_buf = new unsigned char[m_info.m_bufLen];
  
  memcpy(m_info.m_buf, apcData+offset, m_info.m_bufLen);
  offset += m_info.m_bufLen;

  return offset;   
}
