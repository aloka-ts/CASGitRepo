#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktAddSsn.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktAddSsn::INGwFtPktAddSsn()
{
   mMsgData.msMsgType   = MSG_ADD_LOCAL_SSN;
	 m_isAddSsnSet       = false;
	 memset(&m_addSsn, 0, sizeof(AddLocalSsn));
}

void
INGwFtPktAddSsn::initialize(short senderid, short receiverid, 
														AddLocalSsn &addSsn)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isAddSsnSet = true;
	memcpy(&m_addSsn, &addSsn, sizeof(AddLocalSsn));
}

INGwFtPktAddSsn::~INGwFtPktAddSsn()
{
}

int 
INGwFtPktAddSsn::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddSsn::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddSsn::depacketize buffSize:%d, expected:%d", asSize,
		(sizeof(m_addSsn)+SIZE_OF_BPMSG));

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U16 nwId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addSsn.nwId=ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U16 nmbBpc
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addSsn.nmbBpc=ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// bpcList
	for(int i=0; i < m_addSsn.nmbBpc; ++i)
	{
		// Dpc bpc
		memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
		m_addSsn.bpcList[i].bpc=ntohl(lLdata);
		offset += SIZE_OF_LONG;

		// U8  prior
		memcpy((void*)&m_addSsn.bpcList[i].prior, apcData + offset, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;
	}

	// U16 nmbConPc
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addSsn.nmbConPc=ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U32 conPc
	for(int i=0; i < m_addSsn.nmbConPc; ++i)
	{
		memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
		m_addSsn.conPc[i]=ntohl(lLdata);
		offset += SIZE_OF_LONG;
	}

	// S16 sccpUsapId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addSsn.sccpUsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U8  swtch
	memcpy((void*)&m_addSsn.swtch, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8  ssn
	memcpy((void*)&m_addSsn.ssn, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 tcapLsapId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addSsn.tcapLsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// S16 tcapUsapId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addSsn.tcapUsapId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// U8 currentSsnState
	memcpy((void*)&m_addSsn.currentSsnState, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddSsn::depacketize");
	return (offset);
}

int INGwFtPktAddSsn::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	// depends on nmbBpc and nmbSsns
	int size = sizeof(AddLocalSsn) - 
									 ((MAXNUMBPC- m_addSsn.nmbBpc)*sizeof(SpBpcCfg)) -
									 ((MAXCONPC - m_addSsn.nmbConPc)*sizeof(U32));

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddSsn::packetize sizeAllocated:%d, nmbBpc:%d, nmbConPc:%d ", 
	size, m_addSsn.nmbBpc, m_addSsn.nmbConPc);

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isAddSsnSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktAddSsn::packetize AddSsn has not been set");
	}

	U16 lSdata =0;
	U32 lLdata =0;

	// U16 nwId
	lSdata = htons(m_addSsn.nwId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U16 nmbBpc
	lSdata = htons(m_addSsn.nmbBpc);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// bpcList
	for(int i=0; i < m_addSsn.nmbBpc; ++i)
	{
		// Dpc bpc
		lLdata = htonl(m_addSsn.bpcList[i].bpc);
		memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
		offset += SIZE_OF_LONG;

		// U8  prior
		memcpy(pkt + offset, (void*)&m_addSsn.bpcList[i].prior, SIZE_OF_CHAR);
		offset += SIZE_OF_CHAR;
	}

	// U16 nmbConPc
	lSdata = htons(m_addSsn.nmbConPc);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U32 conPc
	for(int i=0; i < m_addSsn.nmbConPc; ++i)
	{
		lLdata = htonl(m_addSsn.conPc[i]);
		memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
		offset += SIZE_OF_LONG;
	}

	// S16 sccpUsapId
	lSdata = htons(m_addSsn.sccpUsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U8  swtch
	memcpy(pkt + offset, (void*)&m_addSsn.swtch, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// U8  ssn
	memcpy(pkt + offset, (void*)&m_addSsn.ssn, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	// S16 tcapLsapId
	lSdata = htons(m_addSsn.tcapLsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// S16 tcapUsapId
	lSdata = htons(m_addSsn.tcapUsapId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// U8 currentSsnState
	memcpy(pkt + offset, (void*)&m_addSsn.currentSsnState, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddSsn::packetize, offset:%d actualLength:%d", 
				offset, (sizeof(m_addSsn) + SIZE_OF_BPMSG));

	return (offset);
}

void
INGwFtPktAddSsn::getSsnData(AddLocalSsn &addSsn)
{
	memcpy(&addSsn, &m_addSsn, sizeof(AddLocalSsn));
}

void
INGwFtPktAddSsn::setSsnData(AddLocalSsn &addSsn)
{
	m_isAddSsnSet = true;
	memcpy(&m_addSsn, &addSsn, sizeof(AddLocalSsn));
}

std::string INGwFtPktAddSsn::toLog(void) const
{ char buf[2000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
					"Add LocalSSN Request: Nwid:%d, nmbBpc:%d, nmbConPc:%d, "
					"sccpUsapId:%d, swtch:%d, ssn:%d, tcapLsapId:%d, tcapUsapId:%d,"
					"currentSsnState:%d", m_addSsn.nwId, m_addSsn.nmbBpc,
					m_addSsn.nmbConPc, m_addSsn.sccpUsapId, m_addSsn.swtch,
			 		m_addSsn.ssn, m_addSsn.tcapLsapId, m_addSsn.tcapUsapId,
			 		m_addSsn.currentSsnState);

					for(int i=0; i < m_addSsn.nmbBpc; ++i) 
					{
						len += sprintf(buf+len, " bpcList[%d]:{bpc:%d, prior:%d}, ",
								i, m_addSsn.bpcList[i].bpc, m_addSsn.bpcList[i].prior);
					}

					for(int j=0; j < m_addSsn.nmbConPc; ++j) 
					{
						len += sprintf(buf+len, "conPc[%d]: %d, ", j, m_addSsn.conPc[j]);
					}
   return buf;
}

