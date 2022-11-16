//*********************************************************************
//   GENBAND, Inc. Confidential and Proprietary
//
// This work contains valuable confidential and proprietary
// information.
// Disclosure, use or reproduction without the written authorization of
// GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
// is protected by the laws of the United States and other countries.
// If publication of the work should occur the following notice shall
// apply:
//
// "Copyright 2007 GENBAND, Inc.  All rights reserved."
//*********************************************************************
//********************************************************************
//
//     File:     INGwLdDstBucket.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwLoadDistributor");

#include <strings.h>

#include <INGwLoadDistributor/INGwLdDstInclude.h>
#include <INGwLoadDistributor/INGwLdDstBucket.h>
#include <INGwLoadDistributor/INGwLdDstPattern.h>

//
// INGwLdDstBucket Class Implementation
//
INGwLdDstInfo::INGwLdDstInfo():m_numOfRegSrv(0), m_markAvailability(false)
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::Constructor()");

  pthread_rwlock_init(&m_rwLock, 0);

	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::Constructor()");
}
 
INGwLdDstInfo::~INGwLdDstInfo()
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::Destructor()");

  pthread_rwlock_destroy(&m_rwLock);

	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::Destructor()");
}
 
void
INGwLdDstInfo::getOpcSsn( U32 &p_pc, U8 &p_ssn)
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::getOpcSsn()");
	p_pc =  m_opcSsnInfo.m_pc;
	p_ssn=  m_opcSsnInfo.m_ssn;
	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::getOpcSsn()");
}

bool
INGwLdDstInfo::belongTo(string &p_sasCallId)
{
	bool found = false;
  pthread_rwlock_rdlock (&m_rwLock);

	for (int i=0; i < m_sasCallId.size(); ++i) {
		if (p_sasCallId == m_sasCallId[i]) {
			found = true;
			break;
		}
	}
  pthread_rwlock_unlock (&m_rwLock);
	return found;
}

void
INGwLdDstInfo::addOpcSsnInfo(U32 p_pc, U8 p_ssn, U8* p_pcDetail,
                               string &p_patternType)
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::addOpcSsnInfo()");

  m_opcSsnInfo.m_pc  = p_pc;
  m_opcSsnInfo.m_ssn = p_ssn;
 
  bcopy(p_pcDetail, &m_opcSsnInfo.m_pcDetail, 3);
 
  m_pattern.initialize(p_patternType);

	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
	"[addOpcSsnInfo] pc[%d][%d-%d-%d] ssn[%d] patterType[%s]", m_opcSsnInfo.m_pc,
	m_opcSsnInfo.m_pcDetail[0], m_opcSsnInfo.m_pcDetail[1], 
	m_opcSsnInfo.m_pcDetail[2], m_opcSsnInfo.m_ssn, 
	(true == p_patternType.empty())?"NULL":p_patternType.c_str());

	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::addOpcSsnInfo()");
}
 
void
INGwLdDstInfo::markSsnAvailable()
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::markSsnAvailable()");

  pthread_rwlock_wrlock (&m_rwLock);
  m_markAvailability = true;
  pthread_rwlock_unlock (&m_rwLock);

	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::markSsnAvailable()");
}
 
void
INGwLdDstInfo::markSsnUnavailable()
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::markSsnUnavailable()");

  pthread_rwlock_wrlock (&m_rwLock);
  m_markAvailability = false;
  pthread_rwlock_unlock (&m_rwLock);

	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::markSsnUnavailable()");
}

bool
INGwLdDstInfo::isSsnAvailable()
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::isSsnAvailable()");

  pthread_rwlock_rdlock (&m_rwLock);
  return m_markAvailability;
  pthread_rwlock_unlock (&m_rwLock);

	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::isSsnAvailable()");
}
 
int
INGwLdDstInfo::addSasId(string &p_sasCallId)
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::addSasId()");

  int retVal = G_SUCCESS;
  bool found = false;
 
  // check if it is already available
  pthread_rwlock_wrlock (&m_rwLock);
 
  for (int indx=0; indx < m_sasCallId.size(); ++indx) {
    if (m_sasCallId[indx] == p_sasCallId) {
      found = true;
      break;
    }
  }
 
  if (false == found) {
		string lSasId = p_sasCallId;
    m_sasCallId.push_back(lSasId);

  	// increment number of sas Application registered and in pattern
  	m_numOfRegSrv++;
  	m_pattern.incrAppCount();
  }
  else {
    retVal = LD_DIST_DUP_ENTRY;
  }

	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
	"[addSasId] %s Sas Ip[%s] for pc[%d: %d-%d-%d] ssn[%d]", 
	(retVal == LD_DIST_DUP_ENTRY)?"Already added":"Adding",
	p_sasCallId.c_str(), m_opcSsnInfo.m_pc,
	m_opcSsnInfo.m_pcDetail[0], m_opcSsnInfo.m_pcDetail[1], 
	m_opcSsnInfo.m_pcDetail[2], m_opcSsnInfo.m_ssn);
 
  pthread_rwlock_unlock (&m_rwLock);

	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::addSasId()");
 
  return retVal;
}

int
INGwLdDstInfo::delSasId(string &p_sasCallId)
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::delSasId()");

  int retVal = G_SUCCESS;
  bool found = false;
 
  vector<string>::iterator startItr = m_sasCallId.begin();
 
  // check if it is already available
  pthread_rwlock_wrlock (&m_rwLock);
 
  while (startItr != m_sasCallId.end()) {
    if ((*startItr) == p_sasCallId) {
      m_sasCallId.erase(startItr);
      found = true;
      break;
    }
    startItr++;
  }
 
  if (true == found) {
    // decrement number of sas Application registered and in pattern
    m_numOfRegSrv--;
    m_pattern.decrAppCount();
  }
  else {
    retVal = LD_DIST_ENTRY_UNAVL;
  }

	logger.logINGwMsg(false, ALWAYS_FLAG, 0,
	"[delSasId] %s Sas Ip[%s] for pc[%d: %d-%d-%d] ssn[%d]", 
	(retVal == LD_DIST_ENTRY_UNAVL)?"Entry not found":"Deleting",
	p_sasCallId.c_str(), m_opcSsnInfo.m_pc,
	m_opcSsnInfo.m_pcDetail[0], m_opcSsnInfo.m_pcDetail[1], 
	m_opcSsnInfo.m_pcDetail[2], m_opcSsnInfo.m_ssn);
 
  pthread_rwlock_unlock (&m_rwLock);

	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::delSasId()");
 
  return retVal;
}
 
string
INGwLdDstInfo::getDestSasInfo()
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::getDestSasInfo()");

  string retVal;
  bool found = false;
 
  // check if it is already available
  pthread_rwlock_wrlock (&m_rwLock);

	if (0 == m_sasCallId.size()) {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
		"[getDestSasInfo] No Sas IP registered for PC[%d] SSN[%d]",
		m_opcSsnInfo.m_pc, m_opcSsnInfo.m_ssn);

  	pthread_rwlock_unlock (&m_rwLock);
		return retVal;
	}

	int index = m_pattern.getNextSasIndx();

	if (index < m_sasCallId.size()) {
		retVal = (m_sasCallId[index]).c_str();

		logger.logINGwMsg(false, VERBOSE_FLAG, 0,
		"[getDestSasInfo] callID fetched [%s] for index[%d]",
		m_sasCallId[index].c_str(), index);
	}
	else {
		logger.logINGwMsg(false, ERROR_FLAG, 0,
		"[getDestSasInfo] Error finding any registered SAS App");
	}

  pthread_rwlock_unlock (&m_rwLock);
 
	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::getDestSasInfo()");

  return retVal;
}

vector<string>
INGwLdDstInfo::getAllRegSasApp() 
{
	vector <string> lSasIp;

  pthread_rwlock_rdlock (&m_rwLock);

	for (int i=0; i < m_sasCallId.size(); ++i) {
		lSasIp.push_back(m_sasCallId[i]);
	}

  pthread_rwlock_unlock (&m_rwLock);
	return lSasIp;
}

bool
INGwLdDstInfo::operator==(U32 &p_pc)
{
  return (m_opcSsnInfo.m_pc == p_pc);
}

int
INGwLdDstInfo::getNumOfSasAppReg()
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::getNumOfSasAppReg()");

  pthread_rwlock_rdlock (&m_rwLock);
  int retVal = m_numOfRegSrv;
  pthread_rwlock_unlock (&m_rwLock);

	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::getNumOfSasAppReg()");
	return retVal;
}

string
INGwLdDstInfo::debugInfo()
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::debugInfo()");

	char localBuf[500];
	int  localBufLen =0;

	localBufLen += sprintf(localBuf + localBufLen, 
	"OPC [%d-%d-%d] SSN [%d]\n", m_opcSsnInfo.m_pcDetail[0], 
	m_opcSsnInfo.m_pcDetail[1], m_opcSsnInfo.m_pcDetail[2], m_opcSsnInfo.m_ssn);

  pthread_rwlock_rdlock (&m_rwLock);

	localBufLen += sprintf(localBuf+localBufLen, 
	"Number of Reg Service: [%d]\nIsAvailable[%d]\nSasCallId\n", m_numOfRegSrv,
	m_markAvailability);

	for (int i=0; i < m_sasCallId.size(); ++i) {
		localBufLen += sprintf(localBuf+localBufLen, 
		"CallID [%d][%s]\n", i+1, m_sasCallId[i].c_str());
	}

  pthread_rwlock_unlock (&m_rwLock);

	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::debugInfo()");
	return localBuf;
}

bool
INGwLdDstInfo::isSasIpRegistered(const string &p_sasIp)
{
	LogINGwTrace(false, 0, "IN INGwLdDstInfo::isSasIpRegistered()");

	bool retVal = false;
  pthread_rwlock_rdlock (&m_rwLock);

	for (int i=0; i < m_sasCallId.size(); ++i) {
		if (m_sasCallId[i] == p_sasIp)  {
			retVal = true;
			break;
		}
	}
  pthread_rwlock_unlock (&m_rwLock);
	LogINGwTrace(false, 0, "OUT INGwLdDstInfo::isSasIpRegistered()");
	return retVal;
}


//
// INGwLdDstBucket Implementation
//
INGwLdDstBucket::INGwLdDstBucket()
{
}

INGwLdDstBucket::~INGwLdDstBucket()
{
}

