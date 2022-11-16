#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktAddGtRule.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktAddGtRule::INGwFtPktAddGtRule()
{
   mMsgData.msMsgType   = MSG_ADD_RULE;
	 m_isAddGtRuleSet       = false;
	 memset(&m_addGtRule, 0, sizeof(AddGtRule));
}

void
INGwFtPktAddGtRule::initialize(short senderid, short receiverid, 
															AddGtRule& addRule)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isAddGtRuleSet = true;
	memcpy(&m_addGtRule, &addRule, sizeof(AddGtRule));
}

INGwFtPktAddGtRule::~INGwFtPktAddGtRule()
{
}

int 
INGwFtPktAddGtRule::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddGtRule::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddGtRule::depacketize buffSize:%d, expected:%d", asSize,
		(sizeof(m_addGtRule)+SIZE_OF_BPMSG));

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;

  // U16 nwId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addGtRule.nwId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

  // S16 sw 
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_addGtRule.sw = htonl(lSdata); 
	offset += SIZE_OF_SHORT;

  // U8 formatPres
	memcpy((void*)&m_addGtRule.formatPres, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 format
	memcpy((void*)&m_addGtRule.format, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // Bool oddEven
	memcpy((void*)&m_addGtRule.oddEven, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 oddEvenPres
	memcpy((void*)&m_addGtRule.oddEvenPres, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 natAddr
	memcpy((void*)&m_addGtRule.natAddr, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 natAddrPres
	memcpy((void*)&m_addGtRule.natAddrPres, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 tType
	memcpy((void*)&m_addGtRule.tType, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 tTypePres
	memcpy((void*)&m_addGtRule.tTypePres, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 numPlan
	memcpy((void*)&m_addGtRule.numPlan, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 numPlanPres
	memcpy((void*)&m_addGtRule.numPlanPres, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 encSch
	memcpy((void*)&m_addGtRule.encSch, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 encSchPres
	memcpy((void*)&m_addGtRule.encSchPres, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 nmbActns
	memcpy((void*)&m_addGtRule.nmbActns, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // GtAction
  for(int i=0; i < m_addGtRule.nmbActns; ++i)
  {
    // U8 type
	  memcpy((void*)&m_addGtRule.actn[i].type, apcData + offset, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;

    // U8 nmbActns
	  memcpy((void*)&m_addGtRule.actn[i].nmbActns, apcData+offset, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;

    // U8 startDigit
	  memcpy((void*)&m_addGtRule.actn[i].startDigit,apcData+offset, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;

    // U8 endDigit
	  memcpy((void*)&m_addGtRule.actn[i].endDigit,apcData+offset, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;
  }

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktAddGtRule::depacketize");
	return (offset);
}

int INGwFtPktAddGtRule::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	// depends on nmbBpc and nmbSsns
	int size = sizeof(AddGtRule) - 
          ((MAX_NUM_OF_ACTION - m_addGtRule.nmbActns)*sizeof(GtAction));

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddGtRule::packetize sizeAllocated:%d", size); 

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isAddGtRuleSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktAddGtRule::packetize AddGtRule has not been set");
	}

	U16 lSdata =0;

  // U16 nwId
	lSdata = htons(m_addGtRule.nwId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

  // S16 sw 
	lSdata = htonl(m_addGtRule.sw);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

  // U8 formatPres
	memcpy(pkt + offset, (void*)&m_addGtRule.formatPres, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 format
	memcpy(pkt + offset, (void*)&m_addGtRule.format, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // Bool oddEven
	memcpy(pkt + offset, (void*)&m_addGtRule.oddEven, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 oddEvenPres
	memcpy(pkt + offset, (void*)&m_addGtRule.oddEvenPres, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 natAddr
	memcpy(pkt + offset, (void*)&m_addGtRule.natAddr, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 natAddrPres
	memcpy(pkt + offset, (void*)&m_addGtRule.natAddrPres, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 tType
	memcpy(pkt + offset, (void*)&m_addGtRule.tType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 tTypePres
	memcpy(pkt + offset, (void*)&m_addGtRule.tTypePres, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 numPlan
	memcpy(pkt + offset, (void*)&m_addGtRule.numPlan, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 numPlanPres
	memcpy(pkt + offset, (void*)&m_addGtRule.numPlanPres, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 encSch
	memcpy(pkt + offset, (void*)&m_addGtRule.encSch, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 encSchPres
	memcpy(pkt + offset, (void*)&m_addGtRule.encSchPres, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 nmbActns
	memcpy(pkt + offset, (void*)&m_addGtRule.nmbActns, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // GtAction
  for(int i=0; i < m_addGtRule.nmbActns; ++i)
  {
    // U8 type
	  memcpy(pkt + offset, (void*)&m_addGtRule.actn[i].type, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;

    // U8 nmbActns
	  memcpy(pkt + offset, (void*)&m_addGtRule.actn[i].nmbActns, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;

    // U8 startDigit
	  memcpy(pkt + offset, (void*)&m_addGtRule.actn[i].startDigit, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;

    // U8 endDigit
	  memcpy(pkt + offset, (void*)&m_addGtRule.actn[i].endDigit, SIZE_OF_CHAR);
	  offset += SIZE_OF_CHAR;
  }

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktAddGtRule::packetize, offset:%d actualLength:%d",offset, 
                                  (size+ SIZE_OF_BPMSG));

	return (offset);
}

void
INGwFtPktAddGtRule::getGtRuleData(AddGtRule &addGtRule)
{
	memcpy(&addGtRule, &m_addGtRule, sizeof(AddGtRule));
}

void
INGwFtPktAddGtRule::setGtRuleData(AddGtRule &addGtRule)
{
	m_isAddGtRuleSet = true;
	memcpy(&m_addGtRule, &addGtRule, sizeof(AddGtRule));
}

std::string INGwFtPktAddGtRule::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
       "AddRule: nwId:%d, sw:%d, formatPres:%d, format:%d, "
        "oddEven:%d, oddEvenPres:%d, natAddr:%d, natAddrPres:%d, "
        "tType:%d, tTypePres:%d, numPlan:%d, numPlanPres:%d, encSch:%d "
        "encSchPres:%d, nmbActns:%d",
        m_addGtRule.nwId, m_addGtRule.sw, m_addGtRule.formatPres,
        m_addGtRule.format, m_addGtRule.oddEven,
        m_addGtRule.oddEvenPres, m_addGtRule.natAddr,
        m_addGtRule.natAddrPres, m_addGtRule.tType,
        m_addGtRule.tTypePres, m_addGtRule.numPlan,
        m_addGtRule.numPlanPres, m_addGtRule.encSch,
        m_addGtRule.encSchPres, m_addGtRule.nmbActns);

		for(int i=0; i < m_addGtRule.nmbActns ; ++i) 
		{
			len += sprintf(buf + len, 
			" actn[%d]: {type:%d, startDigit:%d, endDigit:%d}, ",
      i, m_addGtRule.actn[i].type, m_addGtRule.actn[i].startDigit, 
			m_addGtRule.actn[i].endDigit);
		}
   return buf;
}

