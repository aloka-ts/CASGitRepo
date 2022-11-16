#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktDelLinkset.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktDelLinkset::INGwFtPktDelLinkset()
{
   mMsgData.msMsgType   = MSG_DEL_LINKSET;
	 m_isDelLinksetSet       = false;
	 memset(&m_delLinkset, 0, sizeof(DelLinkSet));
}

void
INGwFtPktDelLinkset::initialize(short senderid, short receiverid, 
																DelLinkSet& delLinkset)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isDelLinksetSet = true;
	memcpy(&m_delLinkset, &delLinkset, sizeof(DelLinkSet));
}

INGwFtPktDelLinkset::~INGwFtPktDelLinkset()
{
}

int 
INGwFtPktDelLinkset::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelLinkset::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelLinkset::depacketize sizeofBuf:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;

	// U16 lnkSetId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delLinkset.lnkSetId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelLinkset::depacketize");
	return (offset);
}

int INGwFtPktDelLinkset::packetize(char** apcData, int version)
{
	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelLinkset::packetize sizeAllocated:%d", sizeof(DelLinkSet));

	int offset = INGwFtPktMsg::createPacket(sizeof(DelLinkSet), apcData, version);

	char *pkt = *apcData;

	if(!m_isDelLinksetSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktDelLinkset::packetize DelLinkset has not been set");
	}

	U16 lSdata =0;

	// U16 lnkSetId
	lSdata = htons(m_delLinkset.lnkSetId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelLinkset::packetize, offset:%d actualLength:%d", 
				offset, (sizeof(m_delLinkset) + SIZE_OF_BPMSG));

	return (offset);
}

void
INGwFtPktDelLinkset::getLinksetData(DelLinkSet &delLinkset)
{
	memcpy(&delLinkset, &m_delLinkset, sizeof(DelLinkSet));
}

void
INGwFtPktDelLinkset::setLinksetData(DelLinkSet &delLinkset)
{
	m_isDelLinksetSet = true;
	memcpy(&m_delLinkset, &delLinkset, sizeof(DelLinkSet));
}

std::string INGwFtPktDelLinkset::toLog(void) const
{
   char buf[1000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
			"Delete Linkset: lnkSetId:%d", m_delLinkset.lnkSetId);

   return buf;
}

