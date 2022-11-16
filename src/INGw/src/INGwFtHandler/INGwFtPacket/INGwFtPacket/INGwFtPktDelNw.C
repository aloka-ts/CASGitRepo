#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktDelNw.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktDelNw::INGwFtPktDelNw()
{
   mMsgData.msMsgType   = MSG_DEL_NW;
	 m_isDelNwSet       = false;
	 memset(&m_delNw, 0, sizeof(DelNetwork));
}

void
INGwFtPktDelNw::initialize(short senderid, short receiverid, DelNetwork& delNw)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isDelNwSet = true;
	memcpy(&m_delNw, &delNw, sizeof(DelNetwork));
}

INGwFtPktDelNw::~INGwFtPktDelNw()
{
}

int 
INGwFtPktDelNw::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelNw::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelNw::depacketize sizeofBuf:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;

	// U16 nwkId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delNw.nwkId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U16 variant
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delNw.variant = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelNw::depacketize");
	return (offset);
}

int INGwFtPktDelNw::packetize(char** apcData, int version)
{
	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelNw::packetize sizeAllocated:%d", sizeof(DelNetwork));

	int offset = INGwFtPktMsg::createPacket(sizeof(DelNetwork), apcData, version);

	char *pkt = *apcData;

	if(!m_isDelNwSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktDelNw::packetize DelNw has not been set");
	}

	U16 lSdata =0;

	// U16 nwkId
	lSdata = htons(m_delNw.nwkId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U16 variant
	lSdata = htons(m_delNw.variant);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	return (offset);
}

void
INGwFtPktDelNw::getNwData(DelNetwork &delNw)
{
	memcpy(&delNw, &m_delNw, sizeof(DelNetwork));
}

void
INGwFtPktDelNw::setNwData(DelNetwork &delNw)
{
	m_isDelNwSet = true;
	memcpy(&m_delNw, &delNw, sizeof(DelNetwork));
}

std::string INGwFtPktDelNw::toLog(void) const
{
   char buf[1000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
			"Delete Nw: nwkId:%d, variant:%d", m_delNw.nwkId, m_delNw.variant);

   return buf;
}

