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
#define MESSAGE_FT
#define BYTES_PER_DLG 300
#define TAG_INB_MAP   254
#define TAG_OUTB_MAP  255

#include <INGwFtPacket/INGwFtPktSynchUpMsg.h>

INGwFtPktSynchUpMsg::INGwFtPktSynchUpMsg()
{
    mMsgData.msMsgType = MSG_HANDLE_SYNCH_UP;
}

INGwFtPktSynchUpMsg::~INGwFtPktSynchUpMsg()
{
}

void 
INGwFtPktSynchUpMsg::initialize(short p_srcid, short p_destid, 
                                U8* p_buf, int p_bufLen, 
                                int p_faultCnt)
{
	mMsgData.msSender   = p_srcid;
	mMsgData.msReceiver = p_destid;
  m_buf = p_buf;
  m_bufLen = p_bufLen;
  printf("\npacketize buffer len initialize <%d>", m_bufLen);
  mFaultCnt =  p_faultCnt;
}

int 
INGwFtPktSynchUpMsg::packetize(char** apcData, int version)
{
 	if (1 != version) {
		return 0;
	}
  *apcData = NULL;
  printf("\npacketize buffer len *** <%d>", m_bufLen);
  fflush(stdout);

  int offset = 
      INGwFtPktMsg::createPacket(m_bufLen, apcData, version);

  char *pkt = *apcData;
  //yogesh Log here
  memcpy(pkt + offset, m_buf, m_bufLen);
  offset += m_bufLen;
  return offset;
}

std::string 
INGwFtPktSynchUpMsg::toLog(void) const
{
	std::ostringstream    oStr;
	std::string msgType = "INGW_FT_SYNCH_UP_MSG";

	oStr << INGwFtPktMsg::toLog();
  //not portable
  oStr <<" mFaultCnt: "<<mFaultCnt;

  char lBuf[4096];
  short lBufLen = 0;

  oStr<<"\n BYTE ARRAY <"<<m_bufLen<<"> :"; 
	for (int i = 0, j =0; i  < m_bufLen; i++, j++) {
    if(!(i & 15)) {
      lBufLen += sprintf(lBuf + lBufLen, "\t\n");
    }
    lBufLen += sprintf(lBuf + lBufLen, "%02X ", m_buf[j]);

    if(lBufLen >= 4000) {
      std::string str( reinterpret_cast<char*>(lBuf), lBufLen);
      oStr<<str.c_str()<< endl;
      j = 0;
      lBufLen = 0;
    }
	}
   
  std::string str( reinterpret_cast<char*>(lBuf), lBufLen);
  oStr<<str.c_str()<< endl;
	return oStr.str();
}

int
INGwFtPktSynchUpMsg::getFaultCount()
{
	return mFaultCnt;
}

U8* 
INGwFtPktSynchUpMsg::getSerializedBuffer() {
  return m_buf;
}

int
INGwFtPktSynchUpMsg::getBufLen(){
  return m_bufLen;
}

int 
INGwFtPktSynchUpMsg::depacketize(const char* apcData, int asSize, 
                                 int version) 
{
	if (1 != version) {
		return 0;
	}
	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

  memcpy(&mFaultCnt, apcData + offset, SIZE_OF_INT);
  mFaultCnt = ntohs(mFaultCnt);
  offset += SIZE_OF_INT;

  m_buf = new (nothrow) U8[asSize - offset];
  m_bufLen = asSize - offset;

  if(NULL == m_buf) {
    //do error handling
  }
  memset(m_buf,0,asSize - offset -1);
  memcpy(m_buf,apcData + offset,asSize - offset);

  return offset;   
}

