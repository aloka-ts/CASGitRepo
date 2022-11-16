#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktAddLink.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktAddLink::INGwFtPktAddLink()
{
   mMsgData.msMsgType   = MSG_ADD_LINK;
	 m_isAddLinkSet       = false;
	 memset(&m_addLink, 0, sizeof(AddLink));
}

void
INGwFtPktAddLink::initialize(short senderid, short receiverid, AddLink& addLnk)
{
	mMsgData.msSender    = senderid;
	mMsgData.msReceiver  = receiverid;
	m_isAddLinkSet = true;
	memcpy(&m_addLink, &addLnk, sizeof(AddLink));
}

INGwFtPktAddLink::~INGwFtPktAddLink()
{
}

int 
INGwFtPktAddLink::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddLink::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddLink::depacketize buffSize:%d, expected:%d", asSize,
		(sizeof(m_addLink)+SIZE_OF_BPMSG));

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U16 lnkId
	memcpy(&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addLink.lnkId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U16 lnkSetId
	memcpy(&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addLink.lnkSetId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U8 dpcLen
	memcpy(&m_addLink.lnkId, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U32 opc
	memcpy(&lLdata, apcData + offset, SIZE_OF_LONG);
	m_addLink.opc = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	// U32 adjDpc
	memcpy(&lLdata, apcData + offset, SIZE_OF_LONG);
	m_addLink.adjDpc = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	// U16 physPort
	memcpy(&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addLink.physPort = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U16 timeSlot
	memcpy(&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addLink.timeSlot = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U8 ssf
	memcpy((void*)&m_addLink.ssf, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 slc
	memcpy((void*)&m_addLink.slc, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 lnkType
	memcpy((void*)&m_addLink.lnkType, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 lnkPrior
	memcpy((void*)&m_addLink.lnkPrior, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 mtp2UsapId
	memcpy(&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addLink.mtp2UsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// S16 mtp3LsapId
	memcpy(&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addLink.mtp3LsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U32 mtp2ProcId
	memcpy(&lLdata, apcData + offset, SIZE_OF_LONG);
	m_addLink.mtp2ProcId = ntohl(lLdata);
	offset += SIZE_OF_LONG;

	// U8 currentLinkState
	memcpy(&m_addLink.currentLinkState, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddLink::depacketize");
	return (offset);
}

int INGwFtPktAddLink::packetize(char** apcData, int version)
{
	int offset = INGwFtPktMsg::createPacket(sizeof(m_addLink), apcData, 
																					 version);
	char *pkt = *apcData;

	U16 lSdata =0;
	U32 lLdata =0;

	if(!m_isAddLinkSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktAddLink::packetize AddLink has not been set");
	}

	// U16 lnkId
	lSdata = htons(m_addLink.lnkId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U16 lnkSetId
	lSdata = htons(m_addLink.lnkSetId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U8 dpcLen
	memcpy(pkt + offset, (void*)&m_addLink.dpcLen, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U32 opc
	lLdata = htonl(m_addLink.opc);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U32 adjDpc
	lLdata = htonl(m_addLink.adjDpc);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U16 physPort
	lSdata = htons(m_addLink.physPort);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U16 timeSlot
	lSdata = htons(m_addLink.timeSlot);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U8 ssf
	memcpy(pkt + offset, (void*)&m_addLink.ssf, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 slc
	memcpy(pkt + offset, (void*)&m_addLink.slc, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 lnkType
	memcpy(pkt + offset, (void*)&m_addLink.lnkType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8 lnkPrior
	memcpy(pkt + offset, (void*)&m_addLink.lnkPrior, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 mtp2UsapId
	lSdata = htons(m_addLink.mtp2UsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 mtp3LsapId
	lSdata = htons(m_addLink.mtp3LsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U32 mtp2ProcId
	lLdata = htonl(m_addLink.mtp2ProcId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	// U8 currentLinkState
	memcpy(pkt + offset, (void*)&m_addLink.currentLinkState, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddLink::packetize, offset:%d actualLength:%d", 
				offset, (sizeof(m_addLink) + SIZE_OF_BPMSG));

	return (offset);
}

void
INGwFtPktAddLink::getLinkData(AddLink &addLnk)
{
	memcpy(&addLnk, &m_addLink, sizeof(AddLink));
}

void
INGwFtPktAddLink::setLinkData(AddLink &addLnk)
{
	m_isAddLinkSet = true;
	memcpy(&m_addLink, &addLnk, sizeof(AddLink));
}

std::string INGwFtPktAddLink::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
			"Add Link: LnkId:%d, LnkSetId:%d, dpcLen:%d, opc:%d, "
			" adjDpc:%d, physPort:%d, timeSlot:%d, ssf:%d, slc:%d,"
			" lnkType:%d, lnkPrior:%d, mtp2UsapId:%d, mtp3LsapId:%d, "
			" mtp2ProcId:%d, currentLinkSTate:%d ",
			m_addLink.lnkId, m_addLink.lnkSetId, m_addLink.dpcLen,
			m_addLink.opc, m_addLink.adjDpc, m_addLink.physPort,
			m_addLink.timeSlot, m_addLink.ssf, m_addLink.slc,
			m_addLink.lnkType, m_addLink.lnkPrior, m_addLink.mtp2UsapId,
			m_addLink.mtp3LsapId, m_addLink.mtp2ProcId,
			m_addLink.currentLinkState);


   return buf;
}

