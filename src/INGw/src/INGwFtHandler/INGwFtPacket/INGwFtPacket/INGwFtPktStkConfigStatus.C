#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktStkConfigStatus.h>
#include <INGwFtPacket/INGwFtPktStackConfig.h>

INGwFtPktStkConfigStatus::INGwFtPktStkConfigStatus()
{
   mMsgData.msMsgType   = MSG_CONFIG_STATUS;
	 memset(&m_stat, 0, sizeof(StkConfigStatus));
}

void
INGwFtPktStkConfigStatus::initialize(short senderid, short receiverid, 
																		 StkConfigStatus &stat)
{
	mMsgData.msSender    = senderid;
	mMsgData.msReceiver  = receiverid;
	memcpy(&m_stat, &stat, sizeof(StkConfigStatus));
}

INGwFtPktStkConfigStatus::~INGwFtPktStkConfigStatus()
{
}

int 
INGwFtPktStkConfigStatus::depacketize(const char* apcData, int asSize, int version)
{
	logger.logINGwMsg(false, TRACE_FLAG, 0, 
												"INGwFtPktStkConfigStatus::depacketize");

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktStkConfigStatus::depacketize buffSize:%d", asSize);

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	U16 lSdata =0;
	U32 lLdata =0;

	// U16 nwId
	memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
	m_stat.cmdType = ntohs(lSdata);
	offset += SIZE_OF_SHORT;

	// Bool procId[2]
	memcpy((void*)&m_stat.procId[0], apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	memcpy((void*)&m_stat.procId[1], apcData + offset, SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	switch(m_stat.cmdType)
	{
		case OPR_STATUS:
		{
			memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
			m_stat.u.oprSts.oprType = htonl(lLdata);
			offset += SIZE_OF_LONG;
		}
		break;

		case ADD_NETWORK:
		{
			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.nwSts.nwId = ntohs(lSdata);
			offset += SIZE_OF_SHORT;
		}
		break;

		case ADD_LINKSET:
		{
			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.lnkSetSts.lnkSetId = ntohs(lSdata);
			offset += SIZE_OF_SHORT;
		}
		break;

		case ADD_LINK:
		{
			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.lnkSts.lnkId = ntohs(lSdata);
			offset += SIZE_OF_SHORT;
		}
		break;

		case ADD_USERPART:
		{
			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.upSts.nwId = ntohs(lSdata);
			offset += SIZE_OF_SHORT;

			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.upSts.mtp3UsapId = ntohs(lSdata);
			offset += SIZE_OF_SHORT;

			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.upSts.m3uaUsapId = ntohs(lSdata);
			offset += SIZE_OF_SHORT;

			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.upSts.sccpLsapId = ntohs(lSdata);
			offset += SIZE_OF_SHORT;
		}
		break;

		case ADD_GTRULE:
		{
  		// U16 nwId;
			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.ruleSts.nwId = ntohs(lSdata);
			offset += SIZE_OF_SHORT;

  		// S16 sw;
			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.ruleSts.sw = ntohs(lSdata);
			offset += SIZE_OF_SHORT;

  		// U8 format;
			memcpy((void*)&m_stat.u.ruleSts.format, apcData + offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

  		// Bool oddEven;
			memcpy((void*)&m_stat.u.ruleSts.oddEven, apcData + offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

  		// U8 natAddr;
			memcpy((void*)&m_stat.u.ruleSts.natAddr, apcData + offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

  		// U8 tType;
			memcpy((void*)&m_stat.u.ruleSts.tType, apcData + offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

  		// U8 numPlan;
			memcpy((void*)&m_stat.u.ruleSts.numPlan, apcData + offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

  		//U8 encSch;
			memcpy((void*)&m_stat.u.ruleSts.encSch, apcData + offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

  		// U8 nmbActns;
			memcpy((void*)&m_stat.u.ruleSts.nmbActns, apcData + offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;
		}
		break;

		case ADD_GTADDRMAP:
		{
			// U16 nwkId;
			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.addrMapSts.nwkId = ntohs(lSdata);
			offset += SIZE_OF_SHORT;

			// S16 sw;
			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.addrMapSts.sw = ntohs(lSdata);
			offset += SIZE_OF_SHORT;

			// U8 format;
			memcpy((void*)&m_stat.u.addrMapSts.format, apcData+offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// Bool oddEven;
			memcpy((void*)&m_stat.u.addrMapSts.oddEven, apcData+offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// U8 natAddr;
			memcpy((void*)&m_stat.u.addrMapSts.natAddr, apcData+offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// U8 tType;
			memcpy((void*)&m_stat.u.addrMapSts.tType, apcData + offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// U8 numPlan;
			memcpy((void*)&m_stat.u.addrMapSts.numPlan, apcData+offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// U8 encSch;
			memcpy((void*)&m_stat.u.addrMapSts.encSch, apcData+offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// U8 gtDigLen;
			memcpy((void*)&m_stat.u.addrMapSts.gtDigLen,apcData+offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// U8 gtDigits[MAX_GT_DIGITS];
  		memcpy((void*)&m_stat.u.addrMapSts.gtDigits, apcData+offset,
																	m_stat.u.addrMapSts.gtDigLen);
			offset += m_stat.u.addrMapSts.gtDigLen;
		}
		break;

		case ASSOC_UP:
		{
			memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
			m_stat.u.assocUp.assocId = ntohl(lLdata);
			offset += SIZE_OF_LONG;

			memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
			m_stat.u.assocUp.pspId = ntohl(lLdata);
			offset += SIZE_OF_LONG;

			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.assocUp.endPointId = ntohs(lSdata);
			offset += SIZE_OF_SHORT;

			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.assocUp.m3uaLsapId = ntohs(lSdata);
			offset += SIZE_OF_SHORT;
		}
		break;

		case ADD_ROUTE:
		{
			memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
			m_stat.u.rteSts.dpc = ntohs(lLdata);
			offset += SIZE_OF_LONG;

			memcpy((void*)&lSdata, apcData + offset, SIZE_OF_SHORT);
			m_stat.u.rteSts.nSapId = ntohs(lSdata);
			offset += SIZE_OF_SHORT;
		}
		break;

		case ADD_LOCAL_SSN:
		{
			memcpy((void*)&m_stat.u.ssnSts.ssn,apcData+offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;
		}
		break;

		case ADD_AS:
		{
			memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
			m_stat.u.psSts.psId = ntohl(lLdata);
			offset += SIZE_OF_LONG;
		}
		break;

		case ADD_ASP:
		{
			memcpy((void*)&lLdata, apcData + offset, SIZE_OF_LONG);
			m_stat.u.pspSts.pspId = ntohl(lLdata);
			offset += SIZE_OF_LONG;
		}
		break;

		case ADD_ENDPOINT:
		{
			memcpy((void*)&m_stat.u.epSts.endPointid,apcData+offset, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;
		}
		break;

	}

	logger.logINGwMsg(false, TRACE_FLAG, 0, 
		"INGwFtPktStkConfigStatus::depacketize");
	return (offset);
}

int INGwFtPktStkConfigStatus::packetize(char** apcData, int version)
{
	// calculate size of buffer to be created
	int actualSize = sizeof(StkConfigStatus);

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktStkConfigStatus::packetize ActualSize:%d sizeAllocat+base:%d",
			actualSize, (actualSize+SIZE_OF_BPMSG));

	int offset = INGwFtPktMsg::createPacket(actualSize, apcData, version);

	char *pkt = *apcData;

	U16 lSdata =0;
	U32 lLdata =0;

	// U16 cmdType
	lSdata = htons(m_stat.cmdType);
	memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

	// Bool procId[2]
	memcpy(pkt + offset, (void*)&m_stat.procId[0], SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	memcpy(pkt + offset, (void*)&m_stat.procId[1], SIZE_OF_CHAR);
	offset += SIZE_OF_CHAR;

	switch(m_stat.cmdType)
	{
		case OPR_STATUS:
		{
			lLdata = htonl(m_stat.u.oprSts.oprType);
			memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
			offset += SIZE_OF_LONG;
		}
		break;

		case ADD_NETWORK:
		{
			lSdata = htons(m_stat.u.nwSts.nwId);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;
		}
		break;

		case ADD_LINKSET:
		{
			lSdata = htons(m_stat.u.lnkSetSts.lnkSetId);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;
		}
		break;

		case ADD_LINK:
		{
			lSdata = htons(m_stat.u.lnkSts.lnkId);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;
		}
		break;

		case ADD_USERPART:
		{
			lSdata = htons(m_stat.u.upSts.nwId);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;

			lSdata = htons(m_stat.u.upSts.mtp3UsapId);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;

			lSdata = htons(m_stat.u.upSts.m3uaUsapId);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;

			lSdata = htons(m_stat.u.upSts.sccpLsapId);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;
		}
		break;

		case ADD_GTRULE:
		{
  		// U16 nwId;
			lSdata = htons(m_stat.u.ruleSts.nwId);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;

  		// S16 sw;
			lSdata = htons(m_stat.u.ruleSts.sw);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;

  		// U8 format;
			memcpy(pkt + offset, (void*)&m_stat.u.ruleSts.format, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

  		// Bool oddEven;
			memcpy(pkt + offset, (void*)&m_stat.u.ruleSts.oddEven, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

  		// U8 natAddr;
			memcpy(pkt + offset, (void*)&m_stat.u.ruleSts.natAddr, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

  		// U8 tType;
			memcpy(pkt + offset, (void*)&m_stat.u.ruleSts.tType, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

  		// U8 numPlan;
			memcpy(pkt + offset, (void*)&m_stat.u.ruleSts.numPlan, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

  		//U8 encSch;
			memcpy(pkt + offset, (void*)&m_stat.u.ruleSts.encSch, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

  		// U8 nmbActns;
			memcpy(pkt + offset, (void*)&m_stat.u.ruleSts.nmbActns, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;
		}
		break;

		case ADD_GTADDRMAP:
		{
			// U16 nwkId;
			lSdata = htons(m_stat.u.addrMapSts.nwkId);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;

			// S16 sw;
			lSdata = htons(m_stat.u.addrMapSts.sw);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;

			// U8 format;
			memcpy(pkt + offset, (void*)&m_stat.u.addrMapSts.format, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// Bool oddEven;
			memcpy(pkt + offset, (void*)&m_stat.u.addrMapSts.oddEven, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// U8 natAddr;
			memcpy(pkt + offset, (void*)&m_stat.u.addrMapSts.natAddr, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// U8 tType;
			memcpy(pkt + offset, (void*)&m_stat.u.addrMapSts.tType, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// U8 numPlan;
			memcpy(pkt + offset, (void*)&m_stat.u.addrMapSts.numPlan, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// U8 encSch;
			memcpy(pkt + offset, (void*)&m_stat.u.addrMapSts.encSch, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// U8 gtDigLen;
			memcpy(pkt + offset, (void*)&m_stat.u.addrMapSts.gtDigLen, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;

			// U8 gtDigits[MAX_GT_DIGITS];
  		memcpy(pkt + offset, (void*)&m_stat.u.addrMapSts.gtDigits,
																	m_stat.u.addrMapSts.gtDigLen);
			offset += m_stat.u.addrMapSts.gtDigLen;
		}
		break;

		case ASSOC_UP:
		{
			lLdata = htonl(m_stat.u.assocUp.assocId);
			memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
			offset += SIZE_OF_LONG;

			lLdata = htonl(m_stat.u.assocUp.pspId);
			memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
			offset += SIZE_OF_LONG;

			lSdata = htons(m_stat.u.assocUp.endPointId);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;

			lSdata = htons(m_stat.u.assocUp.m3uaLsapId);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;
		}
		break;

		case ADD_ROUTE:
		{
			lLdata = htonl(m_stat.u.rteSts.dpc);
			memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
			offset += SIZE_OF_LONG;

			lSdata = htons(m_stat.u.rteSts.nSapId);
			memcpy(pkt + offset, (void*)&lSdata, SIZE_OF_SHORT);
			offset += SIZE_OF_SHORT;

		}
		break;

		case ADD_LOCAL_SSN:
		{
			memcpy(pkt + offset, (void*)&m_stat.u.ssnSts.ssn, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;
		}
		break;

		case ADD_AS:
		{
			lLdata = htonl(m_stat.u.psSts.psId);
			memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
			offset += SIZE_OF_LONG;
		}
		break;

		case ADD_ASP:
		{
			lLdata = htonl(m_stat.u.pspSts.pspId);
			memcpy(pkt + offset, (void*)&lLdata, SIZE_OF_LONG);
			offset += SIZE_OF_LONG;
		}
		break;

		case ADD_ENDPOINT:
		{
			memcpy(pkt + offset, (void*)&m_stat.u.epSts.endPointid, SIZE_OF_CHAR);
			offset += SIZE_OF_CHAR;
		}
		break;

	}

	logger.logMsg(VERBOSE_FLAG, 0, 
	"INGwFtPktStkConfigStatus::packetize, offset:%d actualLength:%d", offset, 
																					(actualSize+SIZE_OF_BPMSG)); 

	return (offset);
}

void
INGwFtPktStkConfigStatus::getStatusData(StkConfigStatus &stat)
{
	memcpy(&stat, &m_stat, sizeof(StkConfigStatus));
}

std::string INGwFtPktStkConfigStatus::toLog(void) const
{
   char buf[4000];
   int len =0;

	 memset(buf, 0, sizeof(buf));

   len += sprintf((buf + len), "%s", INGwFtPktMsg::toLog().c_str());

	switch(m_stat.cmdType)
	{
		case OPR_STATUS:
		{
			len += sprintf((buf + len), 
				"Cmd: OPR_STATUS, procId:%d-%d value:%d", 
					m_stat.procId[0], m_stat.procId[1], m_stat.u.oprSts.oprType);		
		}
		break;

		case ADD_NETWORK:
		{
			len += sprintf((buf + len), 
				"Cmd: ADD_NETWORK_STATUS, procId:%d-%d nwId:%d", 
					m_stat.procId[0], m_stat.procId[1], m_stat.u.nwSts.nwId);
		}
		break;

		case ADD_LINKSET:
		{
			len += sprintf((buf + len), 
				"Cmd: ADD_LINKSET_STATUS, procId:%d-%d lnkSetId:%d", 
					m_stat.procId[0], m_stat.procId[1], m_stat.u.lnkSetSts.lnkSetId);
		}
		break;

		case ADD_LINK:
		{
			len += sprintf((buf + len), 
				"Cmd: ADD_LINK_STATUS, procId:%d-%d lnkId:%d", 
					m_stat.procId[0], m_stat.procId[1], m_stat.u.lnkSts.lnkId);
		}
		break;

		case ADD_USERPART:
		{
			len += sprintf((buf + len), 
				"Cmd: ADD_USERPART_STATUS, procId:%d-%d nwId:%d, mtp3UsapId:%d, "
				" m3uaUsapId:%d, sccpLsapId:%d",
					m_stat.procId[0], m_stat.procId[1], m_stat.u.upSts.nwId,
			m_stat.u.upSts.mtp3UsapId, m_stat.u.upSts.m3uaUsapId,
			m_stat.u.upSts.sccpLsapId);
		}
		break;

		case ADD_GTRULE:
		{
  		// U16 nwId;
			len += sprintf((buf + len), 
				"Cmd: ADD_GTRULE_STATUS, procId:%d-%d, nwId:%d, sw:%d, "
				" format:%d, oddEven:%d, natAddr:%d, tType:%d, numPlan:%d, "
				"encSch:%d, nmbActns:%d", 
			m_stat.procId[0], m_stat.procId[1], m_stat.u.ruleSts.nwId,
			m_stat.u.ruleSts.sw, m_stat.u.ruleSts.format, m_stat.u.ruleSts.oddEven,
			m_stat.u.ruleSts.natAddr, m_stat.u.ruleSts.tType, 
			m_stat.u.ruleSts.numPlan, m_stat.u.ruleSts.encSch, 
			m_stat.u.ruleSts.nmbActns);
		}
		break;

		case ADD_GTADDRMAP:
		{
			len += sprintf((buf + len), 
				"Cmd: ADD_GTADDRMAP_SSTATUS, procId:%d-%d , nwId:%d, sw:%d, "
				"format:%d, oddEven:%d, natAddr:%d, tType:%d, numPlan:%d, "
				"encSch:%d, gtDigLen:%d, gtDigits:%s",
					m_stat.procId[0], m_stat.procId[1],
			m_stat.u.addrMapSts.nwkId, m_stat.u.addrMapSts.sw, 
			m_stat.u.addrMapSts.format, m_stat.u.addrMapSts.oddEven, 
			m_stat.u.addrMapSts.natAddr, m_stat.u.addrMapSts.tType, 
			m_stat.u.addrMapSts.numPlan, m_stat.u.addrMapSts.encSch, 
			m_stat.u.addrMapSts.gtDigLen, m_stat.u.addrMapSts.gtDigits);
			
		}
		break;

		case ASSOC_UP:
		{
			len += sprintf((buf + len), 
				"Cmd: ASSOC_UP_STATUS, procId:%d-%d assocId:%d, pspId:%d, "
				"endPointId:%d, m3uaLsapId:%d",
					m_stat.procId[0], m_stat.procId[1],
			m_stat.u.assocUp.assocId, m_stat.u.assocUp.pspId,
			m_stat.u.assocUp.endPointId, m_stat.u.assocUp.m3uaLsapId);
		}
		break;

		case ADD_ROUTE:
		{
			len += sprintf((buf + len), 
				"Cmd: ADD_ROUTE_STATUS, procId:%d-%d dpc:%d, nSapId:%d",
					m_stat.procId[0], m_stat.procId[1], 
			m_stat.u.rteSts.dpc, m_stat.u.rteSts.nSapId);

		}
		break;

		case ADD_LOCAL_SSN:
		{
			len += sprintf((buf + len), 
				"Cmd: ADD_LOCAL_SSN_STATUS, procId:%d-%d ssn:%d", 
					m_stat.procId[0], m_stat.procId[1], m_stat.u.ssnSts.ssn);
		}
		break;

		case ADD_AS:
		{
			len += sprintf((buf + len), 
				"Cmd: ADD_AS_STATUS, procId:%d-%d psId:%d", 
					m_stat.procId[0], m_stat.procId[1], m_stat.u.psSts.psId);
		}
		break;

		case ADD_ASP:
		{
			len += sprintf((buf + len), 
				"Cmd: ADD_ASP_STATUS, procId:%d-%d pspId:%d", 
					m_stat.procId[0], m_stat.procId[1], m_stat.u.pspSts.pspId);
		}
		break;

		case ADD_ENDPOINT:
		{
			len += sprintf((buf + len), 
				"Cmd: ADD_ENDPOINT_STATUS, procId:%d-%d endPointId:%d", 
					m_stat.procId[0], m_stat.procId[1], m_stat.u.epSts.endPointid);
		}
		break;

	}

   return buf;
}

