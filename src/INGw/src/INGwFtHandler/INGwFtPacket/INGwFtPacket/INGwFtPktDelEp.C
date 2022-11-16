#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktDelEp.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktDelEp::INGwFtPktDelEp()
{
   mMsgData.msMsgType   = MSG_DEL_ENDPOINT;
	 m_isDelEpSet       	= false;
	 memset(&m_delEp, 0, sizeof(DelEndPoint));
}

void
INGwFtPktDelEp::initialize(short senderid, short receiverid, DelEndPoint& delEp)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isDelEpSet = true;
	memcpy(&m_delEp, &delEp, sizeof(DelEndPoint));
}

INGwFtPktDelEp::~INGwFtPktDelEp()
{
}

int 
INGwFtPktDelEp::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelEp::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelEp::depacketize buffSize:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U16 sctpProcId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_delEp.sctpProcId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	// S16 sctpLsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_delEp.sctpLsapId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	// S16 sctpUsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_delEp.sctpUsapId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	// S16 m3uaLsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_delEp.m3uaLsapId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	// S16 tuclUsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_delEp.tuclUsapId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelEp::depacketize");
	return (offset);
}

int INGwFtPktDelEp::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	int size = sizeof(DelEndPoint);

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelEp::packetize sizeAllocated:%d ", size);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isDelEpSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktDelEp::packetize DelEp has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U16 sctpProcId
	lSdata = htons(m_delEp.sctpProcId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 sctpLsapId
	lSdata = htons(m_delEp.sctpLsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 sctpUsapId
	lSdata = htons(m_delEp.sctpUsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 m3uaLsapId
	lSdata = htons(m_delEp.m3uaLsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 tuclUsapId
	lSdata = htons(m_delEp.tuclUsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelEp::packetize, offset:%d actualLength:%d", offset, size); 

	return (offset);
}

void
INGwFtPktDelEp::getEpData(DelEndPoint &delEp)
{
	memcpy(&delEp, &m_delEp, sizeof(DelEndPoint));
}

void
INGwFtPktDelEp::setEpData(DelEndPoint &delEp)
{
	m_isDelEpSet = true;
	memcpy(&m_delEp, &delEp, sizeof(DelEndPoint));
}

std::string INGwFtPktDelEp::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
					"DelEp: sctpProcId:%d sctpLsapId:%d, sctpUsapId:%d, m3uaLsapId:%d,"
					"tuclUsapId:%d", m_delEp.sctpProcId, m_delEp.sctpLsapId, 
					m_delEp.sctpUsapId, m_delEp.m3uaLsapId, m_delEp.tuclUsapId); 

   return buf;
}

