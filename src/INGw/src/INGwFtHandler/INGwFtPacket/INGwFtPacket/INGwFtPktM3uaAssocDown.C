#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktM3uaAssocDown.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktM3uaAssocDown::INGwFtPktM3uaAssocDown()
{
   mMsgData.msMsgType   = MSG_M3UA_ASSOC_DOWN;
	 m_isM3uaAssocDownSet       = false;
	 memset(&m_m3uaAssocDown, 0, sizeof(M3uaAssocDown));
}

void
INGwFtPktM3uaAssocDown::initialize(short senderid, short receiverid,
																		M3uaAssocDown& assocDown)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isM3uaAssocDownSet = true;
	memcpy(&m_m3uaAssocDown, &assocDown, sizeof(M3uaAssocDown));
}

INGwFtPktM3uaAssocDown::~INGwFtPktM3uaAssocDown()
{
}

int 
INGwFtPktM3uaAssocDown::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktM3uaAssocDown::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktM3uaAssocDown::depacketize sizeofBuf:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 assocId
	memcpy((void*)&lLdata, apcData+offset, SIZE_OF_LONG);
	m_m3uaAssocDown.assocId = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	// U32 pspId
	memcpy((void*)&lLdata, apcData+offset, SIZE_OF_LONG);
	m_m3uaAssocDown.pspId = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	// U16 endPointId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_m3uaAssocDown.endPointId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	// S16 m3uaLsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_m3uaAssocDown.m3uaLsapId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	logger.logINGwMsg(false, TRACE_FLAG,0, "INGwFtPktM3uaAssocDown::depacketize");
	return (offset);
}

int INGwFtPktM3uaAssocDown::packetize(char** apcData, int version)
{
	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktM3uaAssocDown::packetize sizeAllocated:%d", sizeof(M3uaAssocDown));

	int size = sizeof(M3uaAssocDown);
	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isM3uaAssocDownSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktM3uaAssocDown::packetize M3uaAssocDown has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 assocId
	lLdata = htonl(m_m3uaAssocDown.assocId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U32 pspId
	lLdata = htonl(m_m3uaAssocDown.pspId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U16 endPointId
	lSdata = htons(m_m3uaAssocDown.endPointId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 m3uaLsapId
	lSdata = htons(m_m3uaAssocDown.m3uaLsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	return (offset);
}

void
INGwFtPktM3uaAssocDown::getNwData(M3uaAssocDown &delNw)
{
	memcpy(&delNw, &m_m3uaAssocDown, sizeof(M3uaAssocDown));
}

void
INGwFtPktM3uaAssocDown::setNwData(M3uaAssocDown &assocDown)
{
	m_isM3uaAssocDownSet = true;
	memcpy(&m_m3uaAssocDown, &assocDown, sizeof(M3uaAssocDown));
}

std::string INGwFtPktM3uaAssocDown::toLog(void) const
{
   char buf[1000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
					"M3uaAssocDown: assocId:%d, pspId:%d, endPointId:%d, m3uaLsapId:%d",
					m_m3uaAssocDown.assocId, m_m3uaAssocDown.pspId, 
					m_m3uaAssocDown.endPointId, m_m3uaAssocDown.m3uaLsapId);

   return buf;
}

