#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktDelGtAddrMap.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktDelGtAddrMap::INGwFtPktDelGtAddrMap()
{
   mMsgData.msMsgType   = MSG_DEL_ADDRMAP;
	 m_isDelGtAddrMapSet       = false;
	 memset(&m_delGtAddrMap, 0, sizeof(DelAddrMapCfg));
}

void
INGwFtPktDelGtAddrMap::initialize(short senderid, short receiverid, 
															DelAddrMapCfg& delAddrMap)
{
  mMsgData.msSender    = senderid;
  mMsgData.msReceiver  = receiverid;
	m_isDelGtAddrMapSet = true;
	memcpy(&m_delGtAddrMap, &delAddrMap, sizeof(DelAddrMapCfg));
}

INGwFtPktDelGtAddrMap::~INGwFtPktDelGtAddrMap()
{
}

int 
INGwFtPktDelGtAddrMap::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelGtAddrMap::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelGtAddrMap::depacketize buffSize:%d, expected:%d", asSize,
		(sizeof(DelAddrMapCfg)+SIZE_OF_BPMSG));

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
  U32 lLdata =0;

  // U16 nwkId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delGtAddrMap.nwkId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

  // S16 sw 
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delGtAddrMap.sw = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

  // U8 format
	memcpy((void*)&m_delGtAddrMap.format, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // Bool oddEven
	memcpy((void*)&m_delGtAddrMap.oddEven, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 natAddr
	memcpy((void*)&m_delGtAddrMap.natAddr, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 tType
	memcpy((void*)&m_delGtAddrMap.tType, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 numPlan
	memcpy((void*)&m_delGtAddrMap.numPlan, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 encSch
	memcpy((void*)&m_delGtAddrMap.encSch, apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // actn U8 type
  memcpy((void*)&m_delGtAddrMap.actn.type, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // actn U8 nmbActns
  memcpy((void*)&m_delGtAddrMap.actn.nmbActns, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // actn U8 startDigit
  memcpy((void*)&m_delGtAddrMap.actn.startDigit, apcData + offset,SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // actn U8 endDigit
  memcpy((void*)&m_delGtAddrMap.actn.endDigit, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U8 gtDigLen
  memcpy((void*)&m_delGtAddrMap.gtDigLen, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U8 gtDigits[]
  memcpy((void*)&m_delGtAddrMap.gtDigits, apcData + offset, 
                                                  m_delGtAddrMap.gtDigLen);
  offset += m_delGtAddrMap.gtDigLen;

  // Bool replGt
  memcpy((void*)&m_delGtAddrMap.replGt, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U8 mode
  memcpy((void*)&m_delGtAddrMap.mode, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U16 outNwId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_delGtAddrMap.nwkId = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

  // U8 numEntity
  memcpy((void*)&m_delGtAddrMap.numEntity, apcData + offset, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // OutAddr outAddr[]
  for(int i=0; i < m_delGtAddrMap.numEntity; ++i)
  {
    // U8 spHdrOpt
    memcpy((void*)&m_delGtAddrMap.outAddr[i].spHdrOpt, apcData + offset, 
                                                    SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // S16 swtch
    memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
    m_delGtAddrMap.outAddr[i].swtch = ntohs(lSdata);
    offset += SIZE_OF_SHORT;

    // U8 ssf
    memcpy((void*)&m_delGtAddrMap.outAddr[i].ssf, apcData + offset,
                                                            SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // Bool niInd
    memcpy((void*)&m_delGtAddrMap.outAddr[i].niInd, apcData + offset, 
                                                           SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 rtgInd
    memcpy((void*)&m_delGtAddrMap.outAddr[i].rtgInd, apcData + offset,
                                                           SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // Bool ssnInd
    memcpy((void*)&m_delGtAddrMap.outAddr[i].ssnInd, apcData + offset,
                                                           SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    //  Bool pcInd
    memcpy((void*)&m_delGtAddrMap.outAddr[i].pcInd, apcData + offset, 
                                                          SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 ssn
    memcpy((void*)&m_delGtAddrMap.outAddr[i].ssn, apcData + offset, 
                                                          SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U32 pc
    memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
    m_delGtAddrMap.outAddr[i].pc = ntohl(lLdata); 
    offset += SIZE_OF_LONG;

    // U8 format
    memcpy((void*)&m_delGtAddrMap.outAddr[i].format, apcData + offset,
                                                          SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // Bool oddEven
    memcpy((void*)&m_delGtAddrMap.outAddr[i].oddEven, apcData + offset,
                                                          SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 tType
    memcpy((void*)&m_delGtAddrMap.outAddr[i].tType, apcData + offset, 
                                                          SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 natAddr
    memcpy((void*)&m_delGtAddrMap.outAddr[i].natAddr, apcData + offset,
                                                          SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 numPlan
    memcpy((void*)&m_delGtAddrMap.outAddr[i].numPlan, apcData + offset,
                                                         SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 encSch
    memcpy((void*)&m_delGtAddrMap.outAddr[i].encSch, apcData + offset, 
                                                         SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 gtDigLen
    memcpy((void*)&m_delGtAddrMap.outAddr[i].gtDigLen, apcData + offset,
                                                        SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 gtDigits[SHRTADRLEN]
    memcpy((void*)&m_delGtAddrMap.outAddr[i].gtDigits, apcData + offset, 
                                          m_delGtAddrMap.outAddr[i].gtDigLen);
    offset += m_delGtAddrMap.outAddr[i].gtDigLen;
  }

	logger.logINGwMsg(false, TRACE_FLAG, 0, "INGwFtPktDelGtAddrMap::depacketize");
	return (offset);
}

int INGwFtPktDelGtAddrMap::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	// depends on nmbBpc and nmbSsns
	int size = sizeof(DelAddrMapCfg) - 
             (MAX_GT_DIGITS - m_delGtAddrMap.gtDigLen) -
          ((MAXENTITIES - m_delGtAddrMap.numEntity)*sizeof(OutAddr));

  int size2 =0;
  for(int i=0; i < m_delGtAddrMap.numEntity; ++i)
  {
    size2 += (SHRTADRLEN - m_delGtAddrMap.outAddr[i].gtDigLen);
  }

  size -= size2;

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelGtAddrMap::packetize sizeAllocated:%d, size2:%d", size, size2); 

	int offset = INGwFtPktMsg::createPacket(size, apcData, version);

	char *pkt = *apcData;

	if(!m_isDelGtAddrMapSet)
	{
		logger.logMsg(ERROR_FLAG, 0, 
		"INGwFtPktDelGtAddrMap::packetize DelGtAddrMap has not been set");
	}

	U16 lSdata =0;
  U32 lLdata =0;

  // U16 nwkId
	lSdata = htons(m_delGtAddrMap.nwkId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

  // S16 sw 
	lSdata = htons(m_delGtAddrMap.sw);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

  // U8 format
	memcpy(pkt + offset, (void*)&m_delGtAddrMap.format, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // Bool oddEven
	memcpy(pkt + offset, (void*)&m_delGtAddrMap.oddEven, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 natAddr
	memcpy(pkt + offset, (void*)&m_delGtAddrMap.natAddr, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 tType
	memcpy(pkt + offset, (void*)&m_delGtAddrMap.tType, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 numPlan
	memcpy(pkt + offset, (void*)&m_delGtAddrMap.numPlan, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // U8 encSch
	memcpy(pkt + offset, (void*)&m_delGtAddrMap.encSch, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

  // actn U8 type
  memcpy(pkt + offset, (void*)&m_delGtAddrMap.actn.type, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // actn U8 nmbActns
  memcpy(pkt + offset, (void*)&m_delGtAddrMap.actn.nmbActns, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // actn U8 startDigit
  memcpy(pkt + offset, (void*)&m_delGtAddrMap.actn.startDigit, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // actn U8 endDigit
  memcpy(pkt + offset, (void*)&m_delGtAddrMap.actn.endDigit, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U8 gtDigLen
  memcpy(pkt + offset, (void*)&m_delGtAddrMap.gtDigLen, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U8 gtDigits[]
  memcpy(pkt + offset, (void*)&m_delGtAddrMap.gtDigits, 
                                                  m_delGtAddrMap.gtDigLen);
  offset += m_delGtAddrMap.gtDigLen;

  // Bool replGt
  memcpy(pkt + offset, (void*)&m_delGtAddrMap.replGt, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U8 mode
  memcpy(pkt + offset, (void*)&m_delGtAddrMap.mode, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // U16 outNwId
	lSdata = htons(m_delGtAddrMap.outNwId);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

  // U8 numEntity
  memcpy(pkt + offset, (void*)&m_delGtAddrMap.numEntity, SIZE_OF_CHAR);
  offset += SIZE_OF_CHAR;

  // OutAddr outAddr[]
  for(int i=0; i < m_delGtAddrMap.numEntity; ++i)
  {
    // U8 spHdrOpt
    memcpy(pkt + offset, (void*)&m_delGtAddrMap.outAddr[i].spHdrOpt, 
                                                    SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // S16 swtch
    lSdata = htons(m_delGtAddrMap.outAddr[i].swtch);
    memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
    offset += SIZE_OF_SHORT;

    // U8 ssf
    memcpy(pkt + offset, (void*)&m_delGtAddrMap.outAddr[i].ssf, SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // Bool niInd
    memcpy(pkt + offset, (void*)&m_delGtAddrMap.outAddr[i].niInd, SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 rtgInd
    memcpy(pkt + offset, (void*)&m_delGtAddrMap.outAddr[i].rtgInd,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // Bool ssnInd
    memcpy(pkt + offset, (void*)&m_delGtAddrMap.outAddr[i].ssnInd,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    //  Bool pcInd
    memcpy(pkt + offset, (void*)&m_delGtAddrMap.outAddr[i].pcInd, SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 ssn
    memcpy(pkt + offset, (void*)&m_delGtAddrMap.outAddr[i].ssn, SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U32 pc
    lLdata = htonl(m_delGtAddrMap.outAddr[i].pc);
    memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
    offset += SIZE_OF_LONG;

    // U8 format
    memcpy(pkt + offset, (void*)&m_delGtAddrMap.outAddr[i].format,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // Bool oddEven
    memcpy(pkt+ offset, (void*)&m_delGtAddrMap.outAddr[i].oddEven,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 tType
    memcpy(pkt + offset, (void*)&m_delGtAddrMap.outAddr[i].tType, SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 natAddr
    memcpy(pkt+ offset, (void*)&m_delGtAddrMap.outAddr[i].natAddr,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 numPlan
    memcpy(pkt+ offset, (void*)&m_delGtAddrMap.outAddr[i].numPlan,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 encSch
    memcpy(pkt+offset, (void*)&m_delGtAddrMap.outAddr[i].encSch, SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 gtDigLen
    memcpy(pkt+offset, (void*)&m_delGtAddrMap.outAddr[i].gtDigLen,SIZE_OF_CHAR);
    offset += SIZE_OF_CHAR;

    // U8 gtDigits[SHRTADRLEN]
    memcpy(pkt + offset, (void*)&m_delGtAddrMap.outAddr[i].gtDigits, 
                                          m_delGtAddrMap.outAddr[i].gtDigLen);
    offset += m_delGtAddrMap.outAddr[i].gtDigLen;
  }

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktDelGtAddrMap::packetize, offset:%d actualLength:%d",offset, 
                                                (size+ SIZE_OF_BPMSG));

	return (offset);
}

void
INGwFtPktDelGtAddrMap::getGtAddrMapData(DelAddrMapCfg &delGtAddrMap)
{
	memcpy(&delGtAddrMap, &m_delGtAddrMap, sizeof(DelAddrMapCfg));
}

void
INGwFtPktDelGtAddrMap::setGtAddrMapData(DelAddrMapCfg &delGtAddrMap)
{
	m_isDelGtAddrMapSet = true;
	memcpy(&m_delGtAddrMap, &delGtAddrMap, sizeof(DelAddrMapCfg));
}

std::string INGwFtPktDelGtAddrMap::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());
   len += sprintf((buf + len), 
        "Add GtAddrMap Request: nwId:%d, sw:%d, format:%d, replGt:%d,"
        "oddEven:%d, natAddr:%d, tType:%d, numPlan:%d, encSch:%d "
        "nmbActns:%d Action[type:%d, startDig:%d, endDig:%d],"
        "gtDigits:%s, gtDigLen:%d", 
        m_delGtAddrMap.nwkId, m_delGtAddrMap.sw, m_delGtAddrMap.format, 
        m_delGtAddrMap.replGt, m_delGtAddrMap.oddEven, m_delGtAddrMap.natAddr,
        m_delGtAddrMap.tType, m_delGtAddrMap.numPlan, m_delGtAddrMap.encSch, 
        m_delGtAddrMap.actn.nmbActns, m_delGtAddrMap.actn.type, 
        m_delGtAddrMap.actn.startDigit, m_delGtAddrMap.actn.endDigit,
        m_delGtAddrMap.gtDigits, m_delGtAddrMap.gtDigLen);

      for(int i=0; i < m_delGtAddrMap.numEntity; ++i)
      {
        len += sprintf((buf + len),
                   "OutAddress [(%d):spHdrOpt:%d, swtch:%d, ssf:%d,"
          "niInd:%d, rtgInd:%d, ssnInd:%d, pcInd:%d, ssn:%d"
          ", pc:%d, format:%d, oddEven:%d, tType:%d, "
          "natAddr:%d, numPlan:%d, encSch:%d, gtDigLen:%d,"
          "gtDigits:%s", i,
            m_delGtAddrMap.outAddr[i].spHdrOpt, m_delGtAddrMap.outAddr[i].swtch,
            m_delGtAddrMap.outAddr[i].ssf, m_delGtAddrMap.outAddr[i].niInd,
            m_delGtAddrMap.outAddr[i].rtgInd, m_delGtAddrMap.outAddr[i].ssnInd,
            m_delGtAddrMap.outAddr[i].pcInd, m_delGtAddrMap.outAddr[i].ssn,
            m_delGtAddrMap.outAddr[i].pc, m_delGtAddrMap.outAddr[i].format,
            m_delGtAddrMap.outAddr[i].oddEven, m_delGtAddrMap.outAddr[i].tType,
            m_delGtAddrMap.outAddr[i].natAddr,m_delGtAddrMap.outAddr[i].numPlan,
            m_delGtAddrMap.outAddr[i].encSch,m_delGtAddrMap.outAddr[i].gtDigLen,
            m_delGtAddrMap.outAddr[i].gtDigits);

      }

   return buf;
}

