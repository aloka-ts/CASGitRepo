#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktM3uaAssocUp.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktM3uaAssocUp::INGwFtPktM3uaAssocUp()
{
   mMsgData.msMsgType   = MSG_M3UA_ASSOC_UP;
	 m_isM3uaAssocUpSet       	= false;
	 memset(&m_m3uaAssocUp, 0, sizeof(M3uaAssocUp));
}

void
INGwFtPktM3uaAssocUp::initialize(short senderid, short receiverid,
																M3uaAssocUp& m3uaAssocUp)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isM3uaAssocUpSet = true;
	memcpy(&m_m3uaAssocUp, &m3uaAssocUp, sizeof(M3uaAssocUp));
}

INGwFtPktM3uaAssocUp::~INGwFtPktM3uaAssocUp()
{
}

int 
INGwFtPktM3uaAssocUp::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktM3uaAssocUp::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktM3uaAssocUp::depacketize buffSize:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 assocId
	memcpy((void*)&lLdata, apcData+offset, SIZE_OF_LONG);
	m_m3uaAssocUp.assocId = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	// U32 pspId
	memcpy((void*)&lLdata, apcData+offset, SIZE_OF_LONG);
	m_m3uaAssocUp.pspId = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	// U16 endPointId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_m3uaAssocUp.endPointId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	// S16 m3uaLsapId
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_m3uaAssocUp.m3uaLsapId = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	// U16 currentAssocState
	memcpy((void*)&lSdata, apcData+offset, SIZE_OF_SHORT);
	m_m3uaAssocUp.currentAssocState = ntohs(lSdata); 
	offset += SIZE_OF_SHORT;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktM3uaAssocUp::depacketize");
	return (offset);
}

int INGwFtPktM3uaAssocUp::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	int size = sizeof(M3uaAssocUp);

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktM3uaAssocUp::packetize sizeAllocated:%d", size);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isM3uaAssocUpSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktM3uaAssocUp::packetize M3uaAssocUp has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 assocId
	lLdata = htonl(m_m3uaAssocUp.assocId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U32 pspId
	lLdata = htonl(m_m3uaAssocUp.pspId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U16 endPointId
	lSdata = htons(m_m3uaAssocUp.endPointId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 m3uaLsapId
	lSdata = htons(m_m3uaAssocUp.m3uaLsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U16 currentAssocState
	lSdata = htons(m_m3uaAssocUp.currentAssocState);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktM3uaAssocUp::packetize, offset:%d actualLength:%d", offset, size); 

	return (offset);
}

void
INGwFtPktM3uaAssocUp::getM3uaAssocUpData(M3uaAssocUp &m3uaAssocUp)
{
	memcpy(&m3uaAssocUp, &m_m3uaAssocUp, sizeof(M3uaAssocUp));
}

void
INGwFtPktM3uaAssocUp::setM3uaAssocUpData(M3uaAssocUp &m3uaAssocUp)
{
	m_isM3uaAssocUpSet = true;
	memcpy(&m_m3uaAssocUp, &m3uaAssocUp, sizeof(M3uaAssocUp));
}

std::string INGwFtPktM3uaAssocUp::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
					"M3uaAssocUp: assocId:%d, pspId:%d, endPointId:%d, m3uaLsapId:%d"
					"currentAssocState:%d", 
       		m_m3uaAssocUp.assocId, m_m3uaAssocUp.pspId, m_m3uaAssocUp.endPointId,
					m_m3uaAssocUp.m3uaLsapId, m_m3uaAssocUp.currentAssocState);

   return buf;
}

