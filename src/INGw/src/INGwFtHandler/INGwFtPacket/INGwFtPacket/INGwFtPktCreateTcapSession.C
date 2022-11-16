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
#define MESSAGE_FT
#include <INGwFtPacket/INGwFtPktCreateTcapSession.h>

INGwFtPktCreateTcapSession::INGwFtPktCreateTcapSession():m_tcapMsgMsgType(0)
{
    mMsgData.msMsgType = MSG_CREATE_TCAP_SESSION;
}

INGwFtPktCreateTcapSession::~INGwFtPktCreateTcapSession()
{
}

void 
INGwFtPktCreateTcapSession::initialize(short p_tcapMsgMsgType, short p_srcid, short p_destid)
{
	mMsgData.msSender   = p_srcid;
	mMsgData.msReceiver = p_destid;
	m_tcapMsgMsgType    = p_tcapMsgMsgType;

}

int 
INGwFtPktCreateTcapSession::packetize(char** apcData, int version)
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

	int addrLen = sizeof(SccpAddr);

	memcpy(lBuf + lBufLen, (void*)&m_srcAddr, addrLen);
	lBufLen += addrLen;

	memcpy(lBuf + lBufLen, (void*)&m_dstAddr, addrLen);
	lBufLen += addrLen;

	int ipSizevalue = htons(m_sasIp.size());
	memcpy(lBuf + lBufLen, (void*)&ipSizevalue, SIZE_OF_INT);
	lBufLen += SIZE_OF_INT;

	memcpy(lBuf + lBufLen, (void*)m_sasIp.c_str(), m_sasIp.size());
	lBufLen += m_sasIp.size();


  int liBillingId = htons(m_billingNo);
  memcpy(lBuf + lBufLen, &liBillingId, SIZE_OF_INT);
	lBufLen += SIZE_OF_INT;

  int offset = 
     INGwFtPktMsg::createPacket(lBufLen , apcData, version);

  char *pkt = *apcData;

  memcpy(pkt + offset, (void*)lBuf, lBufLen);
  offset += lBufLen;

  return offset;
}

std::string 
INGwFtPktCreateTcapSession::toLog(void) const
{
	std::ostringstream    oStr;
	std::string lRoleStr = (m_tcapMsgMsgType == MSG_TCAP_INBOUND)? 
					 	" - MSG_TCAP_INBOUND" : (m_tcapMsgMsgType== MSG_TCAP_OUTBOUND)?
					  " - MSG_TCAP_OUTBOUND" : " - MSG_TCAP_UNKNOWN";

	oStr << INGwFtPktMsg::toLog();

  oStr <<" suId: "<<m_info.m_appInstId.suId<<" spId: " <<m_info.m_appInstId.spId
       <<" TC-SeqNum: "<<m_info.m_seqNum <<" BillingId: "<<m_billingNo;

  oStr <<" SSN: "<<(int)m_info.m_ssn <<" dlgId "<<m_info.m_stackDialogue<<endl; 

	oStr << " , INGwFtPktCreateTcapSession: "  << m_tcapMsgMsgType<< lRoleStr ;
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

short 
INGwFtPktCreateTcapSession::getTcapMsgMsgType()
{
	return m_tcapMsgMsgType;
}

int
INGwFtPktCreateTcapSession::setTcapMsgInfo(U8* p_buf, U32 p_bufLen, 
                                           U32 p_stackDialogue,
                                           U32 p_userDialogue, int p_seqNum, 
                                           U8 p_ssn, AppInstId p_appInstId, 
                                           short p_msgType, 
                                           string p_sasIp,
                                           SccpAddr p_srcAddr,
                                           SccpAddr p_dstAddr,
                                           bool isReplicated)
{

  if(p_buf == 0 || p_bufLen ==0) {
    return -1; 
  }

  if(isReplicated){
    m_info.m_buf    =  p_buf;
  }
  else{
    //delete this memory after replicating accumulated calls
    m_info.m_buf    =  new (nothrow) U8[p_bufLen];
    memcpy(m_info.m_buf,p_buf,p_bufLen);
  }
  m_info.isReplicated = isReplicated;
  m_info.m_bufLen =  p_bufLen;
  m_info.m_stackDialogue = p_stackDialogue; 
  m_info.m_userDialogue  = p_userDialogue;
  memcpy(&(m_info.m_appInstId), &p_appInstId, sizeof(AppInstId));
  m_info.m_ssn = p_ssn;
  m_info.m_seqNum  = p_seqNum;
  m_tcapMsgMsgType = p_msgType;

  memcpy(&m_srcAddr,&p_srcAddr,sizeof(SccpAddr));
  memcpy(&m_dstAddr,&p_dstAddr,sizeof(SccpAddr));
  m_sasIp = p_sasIp;
  return 0; 
}


t_tcapMsgInfo*
INGwFtPktCreateTcapSession::getTcapMsgInfo(){
  return &m_info;
}

int 
INGwFtPktCreateTcapSession::depacketize(const char* apcData, int asSize, int version) {
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

	memcpy(&m_srcAddr, apcData + offset, sizeof(SccpAddr));
	offset += sizeof(SccpAddr);

	memcpy(&m_dstAddr, apcData + offset, sizeof(SccpAddr));
	offset += sizeof(SccpAddr);
  
  int ipAddSize = 0;
	memcpy(&ipAddSize, apcData + offset, SIZE_OF_INT);
	ipAddSize = ntohs(ipAddSize);
	offset += SIZE_OF_INT;

	char buf[60];
  memset(buf,0,sizeof(buf));
	strncpy(buf, apcData + offset, ipAddSize);
  
	m_sasIp = buf;
	offset += ipAddSize;

  
  int liBillingId = -1;
  memcpy(&liBillingId, apcData + offset, SIZE_OF_INT);
  liBillingId = ntohs(liBillingId);

	offset += SIZE_OF_INT;

  return offset;   
}

