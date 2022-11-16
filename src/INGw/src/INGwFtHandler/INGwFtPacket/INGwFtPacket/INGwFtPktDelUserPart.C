#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktDelUserPart.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktDelUserPart::INGwFtPktDelUserPart()
{
   mMsgData.msMsgType   = MSG_DEL_USER_PART;
	 m_isDelUserPartSet       = false;
	 memset(&m_delUserPart, 0, sizeof(DelUserPart));
}

void
INGwFtPktDelUserPart::initialize(short senderid, short receiverid, 
																DelUserPart& delUserPart)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isDelUserPartSet = true;
	memcpy(&m_delUserPart, &delUserPart, sizeof(DelUserPart));
}

INGwFtPktDelUserPart::~INGwFtPktDelUserPart()
{
}

int 
INGwFtPktDelUserPart::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelUserPart::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelUserPart::depacketize sizeofBuf:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;

	// U8 userPartType
	memcpy((void*)&m_delUserPart.userPartType, apcData+offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 mtp3UsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_delUserPart.mtp3UsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// S16 m3uaUsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_delUserPart.m3uaUsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// S16 sccpLsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_delUserPart.sccpLsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelUserPart::depacketize");
	return (offset);
}

int INGwFtPktDelUserPart::packetize(char** apcData, int version)
{
	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelUserPart::packetize sizeAllocated:%d", sizeof(DelUserPart));

	int size = sizeof(DelUserPart);
	int offset =INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isDelUserPartSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktDelUserPart::packetize DelUserPart has not been set");
	}

	U16 lSdata =0;

	// U8 userPartType
	memcpy(pkt + offset, (void*)&m_delUserPart.userPartType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 mtp3UsapId
	lSdata = htons(m_delUserPart.mtp3UsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 m3uaUsapId
	lSdata = htons(m_delUserPart.m3uaUsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 sccpLsapId
	lSdata = htons(m_delUserPart.sccpLsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddUserPart::packetize, offset:%d actualLength:%d", offset, size); 

	return (offset);
}

void
INGwFtPktDelUserPart::getUserPartData(DelUserPart &delUserPart)
{
	memcpy(&delUserPart, &m_delUserPart, sizeof(DelUserPart));
}

void
INGwFtPktDelUserPart::setUserPartData(DelUserPart &delUserPart)
{
	m_isDelUserPartSet = true;
	memcpy(&m_delUserPart, &delUserPart, sizeof(DelUserPart));
}

std::string INGwFtPktDelUserPart::toLog(void) const
{
   char buf[1000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
			"Delete UserPart: userPartType:%d,  mtp3UsapId:%d"
			", m3uaUsapId:%d, sccpLsapId:%d", 
			m_delUserPart.userPartType, 
			m_delUserPart.mtp3UsapId, m_delUserPart.m3uaUsapId,
			m_delUserPart.sccpLsapId);

   return buf;
}

