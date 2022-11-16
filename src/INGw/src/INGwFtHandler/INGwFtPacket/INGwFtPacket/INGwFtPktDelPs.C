#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktDelPs.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktDelPs::INGwFtPktDelPs()
{
   mMsgData.msMsgType   = MSG_DEL_PS;
	 m_isDelPsSet       = false;
	 memset(&m_delPs, 0, sizeof(DelPs));
}

void
INGwFtPktDelPs::initialize(short senderid, short receiverid, DelPs& delPs)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isDelPsSet = true;
	memcpy(&m_delPs, &delPs, sizeof(DelPs));
}

INGwFtPktDelPs::~INGwFtPktDelPs()
{
}

int 
INGwFtPktDelPs::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelPs::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelPs::depacketize sizeofBuf:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U32 lLdata =0;

  // U32   psId
	memcpy((void*)&lLdata, apcData+offset, SIZE_OF_LONG);
	m_delPs.psId = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	//   U8    nwkId
	memcpy((void*)&m_delPs.nwkId, apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelPs::depacketize");
	return (offset);
}

int INGwFtPktDelPs::packetize(char** apcData, int version)
{
	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelPs::packetize sizeAllocated:%d", sizeof(DelPs));

	int offset = INGwFtPktMsg::createPacket(sizeof(DelPs), apcData, version);

	char *pkt = *apcData;

	if(!m_isDelPsSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktDelPs::packetize DelPs has not been set");
	}

	U32 lLdata =0;

  // U32   psId
	lLdata = htonl(m_delPs.psId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	//   U8    nwkId
	memcpy(pkt + offset, (void*)&m_delPs.nwkId, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	logger.logMsg(TRACE_FLAG, 0, 
	"OUT INGwFtPktDelPs::packetize sizeAllocated:%d", sizeof(DelPs));

	return (offset);
}

void
INGwFtPktDelPs::getPsData(DelPs &delPs)
{
	memcpy(&delPs, &m_delPs, sizeof(DelPs));
}

void
INGwFtPktDelPs::setPsData(DelPs &delPs)
{
	m_isDelPsSet = true;
	memcpy(&m_delPs, &delPs, sizeof(DelPs));
}

std::string INGwFtPktDelPs::toLog(void) const
{
   char buf[1000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
			"Delete Ps: psId:%d, nwkId:%d", m_delPs.psId, m_delPs.nwkId);

   return buf;
}

