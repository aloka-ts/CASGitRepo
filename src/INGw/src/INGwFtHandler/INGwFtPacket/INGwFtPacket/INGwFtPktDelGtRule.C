#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktDelGtRule.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktDelGtRule::INGwFtPktDelGtRule()
{
   mMsgData.msMsgType   = MSG_DEL_RULE;
	 m_isDelGtRuleSet       = false;
	 memset(&m_delGtRule, 0, sizeof(DelGtRule));
}

void
INGwFtPktDelGtRule::initialize(short senderid, short receiverid, 
															DelGtRule& delRule)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isDelGtRuleSet = true;
	memcpy(&m_delGtRule, &delRule, sizeof(DelGtRule));
}

INGwFtPktDelGtRule::~INGwFtPktDelGtRule()
{
}

int 
INGwFtPktDelGtRule::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelGtRule::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelGtRule::depacketize buffSize:%d, expected:%d", asSize,
		(sizeof(DelGtRule)+SIZE_OF_BPMSG));

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;

  // U16 nwId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delGtRule.nwId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

  // S16 sw 
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delGtRule.sw = htonl(lSdata); 
	offset += SIZE_OF_SHORT;

  // U8 formatPres
	memcpy((void*)&m_delGtRule.formatPres, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 format
	memcpy((void*)&m_delGtRule.format, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // Bool oddEven
	memcpy((void*)&m_delGtRule.oddEven, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 oddEvenPres
	memcpy((void*)&m_delGtRule.oddEvenPres, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 natAddr
	memcpy((void*)&m_delGtRule.natAddr, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 natAddrPres
	memcpy((void*)&m_delGtRule.natAddrPres, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 tType
	memcpy((void*)&m_delGtRule.tType, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 tTypePres
	memcpy((void*)&m_delGtRule.tTypePres, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 numPlan
	memcpy((void*)&m_delGtRule.numPlan, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 numPlanPres
	memcpy((void*)&m_delGtRule.numPlanPres, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 encSch
	memcpy((void*)&m_delGtRule.encSch, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 encSchPres
	memcpy((void*)&m_delGtRule.encSchPres, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 nmbActns
	memcpy((void*)&m_delGtRule.nmbActns, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // GtAction
  for(int i=0; i < m_delGtRule.nmbActns; ++i)
  {
    // U8 type
	  memcpy((void*)&m_delGtRule.actn[i].type, apcData + offset, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;

    // U8 nmbActns
	  memcpy((void*)&m_delGtRule.actn[i].nmbActns, apcData+offset, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;

    // U8 startDigit
	  memcpy((void*)&m_delGtRule.actn[i].startDigit,apcData+offset, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;

    // U8 endDigit
	  memcpy((void*)&m_delGtRule.actn[i].endDigit,apcData+offset, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;
  }

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelGtRule::depacketize");
	return (offset);
}

int INGwFtPktDelGtRule::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	// depends on nmbBpc and nmbSsns
	int size = sizeof(DelGtRule) - 
          ((MAX_NUM_OF_ACTION - m_delGtRule.nmbActns)*sizeof(GtAction));

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelGtRule::packetize sizeAllocated:%d", size); 

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isDelGtRuleSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktDelGtRule::packetize DelGtRule has not been set");
	}

	U16 lSdata =0;

  // U16 nwId
	lSdata = htons(m_delGtRule.nwId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

  // S16 sw 
	lSdata = htonl(m_delGtRule.sw);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

  // U8 formatPres
	memcpy(pkt + offset, (void*)&m_delGtRule.formatPres, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 format
	memcpy(pkt + offset, (void*)&m_delGtRule.format, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // Bool oddEven
	memcpy(pkt + offset, (void*)&m_delGtRule.oddEven, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 oddEvenPres
	memcpy(pkt + offset, (void*)&m_delGtRule.oddEvenPres, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 natAddr
	memcpy(pkt + offset, (void*)&m_delGtRule.natAddr, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 natAddrPres
	memcpy(pkt + offset, (void*)&m_delGtRule.natAddrPres, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 tType
	memcpy(pkt + offset, (void*)&m_delGtRule.tType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 tTypePres
	memcpy(pkt + offset, (void*)&m_delGtRule.tTypePres, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 numPlan
	memcpy(pkt + offset, (void*)&m_delGtRule.numPlan, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 numPlanPres
	memcpy(pkt + offset, (void*)&m_delGtRule.numPlanPres, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 encSch
	memcpy(pkt + offset, (void*)&m_delGtRule.encSch, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 encSchPres
	memcpy(pkt + offset, (void*)&m_delGtRule.encSchPres, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 nmbActns
	memcpy(pkt + offset, (void*)&m_delGtRule.nmbActns, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // GtAction
  for(int i=0; i < m_delGtRule.nmbActns; ++i)
  {
    // U8 type
	  memcpy(pkt + offset, (void*)&m_delGtRule.actn[i].type, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;

    // U8 nmbActns
	  memcpy(pkt + offset, (void*)&m_delGtRule.actn[i].nmbActns, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;

    // U8 startDigit
	  memcpy(pkt + offset, (void*)&m_delGtRule.actn[i].startDigit, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;

    // U8 endDigit
	  memcpy(pkt + offset, (void*)&m_delGtRule.actn[i].endDigit, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;
  }

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelGtRule::packetize, offset:%d actualLength:%d",offset, 
                                  (size+ SIZE_OF_BPMSG));

	return (offset);
}

void
INGwFtPktDelGtRule::getGtRuleData(DelGtRule &DelGtRule)
{
	memcpy(&DelGtRule, &m_delGtRule, sizeof(DelGtRule));
}

void
INGwFtPktDelGtRule::setGtRuleData(DelGtRule &delGtRule)
{
	m_isDelGtRuleSet = true;
	memcpy(&m_delGtRule, &delGtRule, sizeof(DelGtRule));
}

std::string INGwFtPktDelGtRule::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
       "DelRule: nwId:%d, sw:%d, formatPres:%d, format:%d, "
        "oddEven:%d, oddEvenPres:%d, natAddr:%d, natAddrPres:%d, "
        "tType:%d, tTypePres:%d, numPlan:%d, numPlanPres:%d, encSch:%d "
        "encSchPres:%d, nmbActns:%d",
        m_delGtRule.nwId, m_delGtRule.sw, m_delGtRule.formatPres,
        m_delGtRule.format, m_delGtRule.oddEven,
        m_delGtRule.oddEvenPres, m_delGtRule.natAddr,
        m_delGtRule.natAddrPres, m_delGtRule.tType,
        m_delGtRule.tTypePres, m_delGtRule.numPlan,
        m_delGtRule.numPlanPres, m_delGtRule.encSch,
        m_delGtRule.encSchPres, m_delGtRule.nmbActns);

		for(int i=0; i < m_delGtRule.nmbActns ; ++i) 
		{
           len += sprintf(buf+len,
       " actn[%d]: {type:%d, startDigit:%d, endDigit:%d}, ",
        i, m_delGtRule.actn[i].type, m_delGtRule.actn[i].startDigit, 
        m_delGtRule.actn[i].endDigit);
		}
   return buf;
}

