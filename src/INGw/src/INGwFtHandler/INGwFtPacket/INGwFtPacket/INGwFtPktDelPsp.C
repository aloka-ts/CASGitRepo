#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktDelPsp.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktDelPsp::INGwFtPktDelPsp()
{
   mMsgData.msMsgType   = MSG_DEL_PSP;
	 m_isDelPspSet       	= false;
	 memset(&m_delPsp, 0, sizeof(DelPsp));
}

void
INGwFtPktDelPsp::initialize(short senderid, short receiverid, DelPsp& delPsp)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isDelPspSet = true;
	memcpy(&m_delPsp, &delPsp, sizeof(DelPsp));
}

INGwFtPktDelPsp::~INGwFtPktDelPsp()
{
}

int 
INGwFtPktDelPsp::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelPsp::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelPsp::depacketize buffSize:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 pspId
	memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
	m_delPsp.pspId = htonl(lLdata); 
	offset += SIZE_OF_LONG;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelPsp::depacketize");
	return (offset);
}

int INGwFtPktDelPsp::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	int size = sizeof(DelPsp);

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelPsp::packetize sizeAllocated:%d ", size);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isDelPspSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktDelPsp::packetize DelPsp has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U32 pspId
	lLdata = htonl(m_delPsp.pspId);
	memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
	offset += SIZE_OF_LONG;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelPsp::packetize, offset:%d actualLength:%d", offset, size); 

	return (offset);
}

void
INGwFtPktDelPsp::getPspData(DelPsp &delPsp)
{
	memcpy(&delPsp, &m_delPsp, sizeof(DelPsp));
}

void
INGwFtPktDelPsp::setPspData(DelPsp &delPsp)
{
	m_isDelPspSet = true;
	memcpy(&m_delPsp, &delPsp, sizeof(DelPsp));
}

std::string INGwFtPktDelPsp::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), "DelPsp: pspId:%d", m_delPsp.pspId);

   return buf;
}

