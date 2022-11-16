#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktDelSsn.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktDelSsn::INGwFtPktDelSsn()
{
   mMsgData.msMsgType   = MSG_DEL_LOCAL_SSN;
	 m_isDelSsnSet       = false;
	 memset(&m_delSsn, 0, sizeof(DelLocalSsn));
}

void
INGwFtPktDelSsn::initialize(short senderid, short receiverid, 
														DelLocalSsn& delSsn)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isDelSsnSet = true;
	memcpy(&m_delSsn, &delSsn, sizeof(DelLocalSsn));
}

INGwFtPktDelSsn::~INGwFtPktDelSsn()
{
}

int 
INGwFtPktDelSsn::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelSsn::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelSsn::depacketize sizeofBuf:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U8  ssn
	memcpy((void*)&m_delSsn.ssn, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 tcapLsapId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delSsn.tcapLsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// S16 tcapUsapId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delSsn.tcapUsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// S16 sccpUsapId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delSsn.sccpUsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelSsn::depacketize");
	return (offset);
}

int INGwFtPktDelSsn::packetize(char** apcData, int version)
{
	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelSsn::packetize sizeAllocated:%d", sizeof(DelLocalSsn));

	int offset = INGwFtPktMsg::createPacket(sizeof(DelLocalSsn), apcData, 
																					version);

	char *pkt = *apcData;

	if(!m_isDelSsnSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktDelSsn::packetize DelSsn has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U8  ssn
	memcpy(pkt + offset, (void*)&m_delSsn.ssn, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 tcapLsapId
	lSdata = htons(m_delSsn.tcapLsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 tcapUsapId
	lSdata = htons(m_delSsn.tcapUsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 sccpUsapId
	lSdata = htons(m_delSsn.sccpUsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	return (offset);
}

void
INGwFtPktDelSsn::getSsnData(DelLocalSsn &delSsn)
{
	memcpy(&delSsn, &m_delSsn, sizeof(DelLocalSsn));
}

void
INGwFtPktDelSsn::setSsnData(DelLocalSsn &delSsn)
{
	m_isDelSsnSet = true;
	memcpy(&m_delSsn, &delSsn, sizeof(DelLocalSsn));
}

std::string INGwFtPktDelSsn::toLog(void) const
{
   char buf[1000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
			"Delete Local Ssn: ssn:%d, tcapLsapId:%d, tcapUsapId:%d, sccpUsapId:%d",
			m_delSsn.ssn, m_delSsn.tcapLsapId, m_delSsn.tcapUsapId, 
			m_delSsn.sccpUsapId);

   return buf;
}

