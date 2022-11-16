#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktDelLink.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktDelLink::INGwFtPktDelLink()
{
   mMsgData.msMsgType   = MSG_DEL_LINK;
	 m_isDelLinkSet       = false;
	 memset(&m_delLink, 0, sizeof(DelLink));
}

void
INGwFtPktDelLink::initialize(short senderid, short receiverid, DelLink& delLnk)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isDelLinkSet = true;
	memcpy(&m_delLink, &delLnk, sizeof(DelLink));
}

INGwFtPktDelLink::~INGwFtPktDelLink()
{
}

int 
INGwFtPktDelLink::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelLink::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelLink::depacketize buffSize:%d, expected:%d", asSize,
		(sizeof(m_delLink)+SIZE_OF_BPMSG));

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;

	// U16 lnkId
	memcpy(&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delLink.lnkId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// S16 mtp2UsapId
	memcpy(&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delLink.mtp2UsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// S16 mtp3LsapId
	memcpy(&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delLink.mtp3LsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U32 mtp2ProcId
	memcpy(&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delLink.mtp2ProcId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelLink::depacketize");
	return (offset);
}

int INGwFtPktDelLink::packetize(char** apcData, int version)
{
	int offset = INGwFtPktMsg::createPacket(sizeof(m_delLink), apcData, 
																					 version);
	char *pkt = *apcData;

	U16 lSdata =0;
	U32 lLdata =0;

	if(!m_isDelLinkSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktDelLink::packetize DelLink has not been set");
	}

	// U16 lnkId
	lSdata = htons(m_delLink.lnkId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 mtp2UsapId
	lSdata = htons(m_delLink.mtp2UsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 mtp3LsapId
	lSdata = htons(m_delLink.mtp3LsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U32 mtp2ProcId
	lSdata = htons(m_delLink.mtp2ProcId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelLink::packetize, offset:%d actualLength:%d", 
				offset, (sizeof(m_delLink) + SIZE_OF_BPMSG));

	return (offset);
}

void
INGwFtPktDelLink::getLinkData(DelLink &delLnk)
{
	memcpy(&delLnk, &m_delLink, sizeof(DelLink));
}

void
INGwFtPktDelLink::setLinkData(DelLink &delLnk)
{
	m_isDelLinkSet = true;
	memcpy(&m_delLink, &delLnk, sizeof(DelLink));
}

std::string INGwFtPktDelLink::toLog(void) const
{
   char buf[2000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
			"DelLink: LnkId:%d, mtp2UsapId:%d, mtp3LsapId:%d, "
			" mtp2ProcId:%d", m_delLink.lnkId, m_delLink.mtp2UsapId,
			m_delLink.mtp3LsapId, m_delLink.mtp2ProcId);

   return buf;
}

