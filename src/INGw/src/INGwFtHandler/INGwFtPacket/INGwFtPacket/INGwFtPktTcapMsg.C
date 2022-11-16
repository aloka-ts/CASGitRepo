//////////////////////////////////////////////////////////////////////////
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
//     File:    INGwFtPktTcapMsg.C
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya    31/01/08     Initial Creation
//********************************************************************
#include <INGwFtPacket/INGwFtPktTcapMsg.h>

static int a;
INGwFtPktTcapMsg::INGwFtPktTcapMsg():m_stackDialogue(0), 
			m_userDialogue(0), m_billingNo(-1)
{
	mMsgData.msMsgType = MSG_TCAP_DLG_INFO;

	memset(&m_srcAddr, 0, sizeof(SccpAddr));
	memset(&m_dstAddr, 0, sizeof(SccpAddr));
	memset(&m_appId, 0, sizeof(AppInstId));

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)  
  memset(&m_stAnsiDlgEv,0, sizeof(StAnsiDlgEv)); 
#else
  memset(&m_objAcn,0,sizeof(INcStr));
#endif

}

INGwFtPktTcapMsg::~INGwFtPktTcapMsg()
{
}

void 
INGwFtPktTcapMsg:: initialize(
                              int p_stackDialogue, 
                              int p_userDialogue, 
							                const SccpAddr &p_srcaddr,
                              const SccpAddr &p_destaddr, 
					                    const string & p_ipaddr, 
                              AppInstId &p_appid,

                              #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
                              StAnsiDlgEv p_stAnsiDlgEv,
                              int p_billingNo,
                              #else
                              INcStr p_objAcn,
                              #endif

								              short p_srcId, 
                              short p_dstId)
{
	mMsgData.msSender   = p_srcId;
	mMsgData.msReceiver = p_dstId;

	m_stackDialogue = p_stackDialogue;
	m_userDialogue  = p_userDialogue;
	m_ipAddr = p_ipaddr;

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
  memcpy(&m_stAnsiDlgEv, &p_stAnsiDlgEv, sizeof(StAnsiDlgEv));
  m_billingNo = p_billingNo;
#else
  memcpy(&m_objAcn,&p_objAcn,sizeof(INcStr));
#endif

	memcpy(&m_srcAddr, &p_srcaddr, sizeof(SccpAddr));
	memcpy(&m_dstAddr, &p_destaddr, sizeof(SccpAddr));
	memcpy(&m_appId, &p_appid, sizeof(AppInstId));
  
}

int 
INGwFtPktTcapMsg::depacketize(const char* apcData, int asSize, int version)
{
	if (1 != version) {
		return 0;
	}

	int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

	memcpy(&m_stackDialogue, apcData + offset, SIZE_OF_INT);

	m_stackDialogue = ntohs(m_stackDialogue);
	offset += SIZE_OF_INT;

	memcpy(&m_userDialogue, apcData + offset, SIZE_OF_INT);
	m_userDialogue = ntohs(m_userDialogue);
	offset += SIZE_OF_INT;

	memcpy(&m_srcAddr, apcData + offset, sizeof(SccpAddr));
	offset += sizeof(SccpAddr);

	memcpy(&m_dstAddr, apcData + offset, sizeof(SccpAddr));
	offset += sizeof(SccpAddr);

	memcpy(&m_appId, apcData + offset, sizeof(AppInstId));
	offset += sizeof(AppInstId);

	int ipAddSize = 0;
	memcpy(&ipAddSize, apcData + offset, SIZE_OF_INT);
	ipAddSize = ntohs(ipAddSize);
	offset += SIZE_OF_INT;

	char buf[60];
  memset(buf,0,sizeof(buf));
	strncpy(buf, apcData + offset, ipAddSize);
  
  a =  ipAddSize;
	m_ipAddr = buf;
	offset += ipAddSize;

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
  memcpy((void*)&m_stAnsiDlgEv, apcData + offset, sizeof(StAnsiDlgEv));
  offset = sizeof(StAnsiDlgEv);

  memcpy((void*)&(m_billingNo), apcData + offset, SIZE_OF_INT);
  m_billingNo = ntohs(m_billingNo); 
  offset += SIZE_OF_INT;
#else
  U16 lAcnLen = 0;
  memcpy(&lAcnLen, apcData + offset, SIZE_OF_SHORT);
  m_objAcn.len = ntohs(lAcnLen);
  offset += SIZE_OF_SHORT;

  memcpy(m_objAcn.string, apcData + offset, lAcnLen);
  offset += lAcnLen;
#endif

	return offset;
}

int 
INGwFtPktTcapMsg::packetize(char** apcData, int version)
{
	if (1 != version) {
		return 0;
	}

	*apcData = NULL;

	int pktSize = SIZE_OF_INT + SIZE_OF_INT + sizeof(SccpAddr) +
								sizeof(SccpAddr) + sizeof(AppInstId) + 
								SIZE_OF_INT + m_ipAddr.size() + 
                //this line is for serialization of App Context Name 
                SIZE_OF_SHORT + 
                #if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
                sizeof(StAnsiDlgEv) + 6;
                #else
                m_objAcn.len;
                #endif

	int offset = 
      INGwFtPktMsg::createPacket(pktSize , apcData, version);

	char *pkt = *apcData;

	int value = htons(m_stackDialogue);
	memcpy(pkt + offset, (void*)&value, SIZE_OF_INT);
	offset += SIZE_OF_INT;

	value = htons(m_userDialogue);
	memcpy(pkt + offset, (void*)&value, SIZE_OF_INT);
	offset += SIZE_OF_INT;

	int addrLen = sizeof(SccpAddr);

	memcpy(pkt + offset, (void*)&m_srcAddr, addrLen);
	offset += addrLen;

	memcpy(pkt + offset, (void*)&m_dstAddr, addrLen);
	offset += addrLen;

	memcpy(pkt + offset, (void*)&m_appId, sizeof(AppInstId));
	offset += sizeof(AppInstId);

	value = htons(m_ipAddr.size());
	memcpy(pkt + offset, (void*)&value, SIZE_OF_INT);
	offset += SIZE_OF_INT;

	memcpy(pkt + offset, (void*)m_ipAddr.c_str(), m_ipAddr.size());
	offset += m_ipAddr.size();

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)

  memcpy(pkt + offset, (void*)&m_stAnsiDlgEv, sizeof(StAnsiDlgEv));
  offset += sizeof(StAnsiDlgEv);

  m_billingNo = htons(m_billingNo);
  memcpy(pkt + offset, (void*)&m_billingNo, SIZE_OF_INT);
  offset += SIZE_OF_INT;

#else
  S16 lAcnLen = htons(m_objAcn.len);
  memcpy(pkt + offset, (void*)&m_objAcn.len, SIZE_OF_SHORT);
	offset += SIZE_OF_SHORT;

  memcpy(pkt + offset, (void*)(m_objAcn.string), m_objAcn.len);
  offset += m_objAcn.len;
#endif

	return offset;
}


std::string 
INGwFtPktTcapMsg::toLog(bool lbPrintFtInfo) const
{
	std::ostringstream    oStr;
  if(lbPrintFtInfo) {
	  oStr << INGwFtPktMsg::toLog();
  }
  
	oStr << "StkDlg:"  << m_stackDialogue;
	oStr << " UsrDlg:" << m_userDialogue ;

	oStr << " srcAddr:[";
	
  if(m_srcAddr.pcInd){
	  oStr << " pc:" << m_srcAddr.pc;
  }

  if(m_srcAddr.ssnInd)
  {
    oStr << " ssn:" << (int)m_srcAddr.ssn;
  }
 
  oStr <<"]";

	oStr << " dstAddr:[";

  if(m_dstAddr.pcInd){
	  oStr << " pc:" << m_dstAddr.pc;
  }

  if(m_dstAddr.ssnInd)
  {
    oStr << " ssn:" << (int)m_dstAddr.ssn;
  }

  oStr << "]";

	oStr << "\n[ suId :" << (int)m_appId.suId << " spId:" << 
	(int)m_appId.spId << "]";
	
	oStr << " SasIp:" << m_ipAddr; 
  char lBuf[200];
  short lBufLen = 0;
#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)
#else
	oStr << "\nACN Len:" << m_objAcn.len << endl;
  for(int i=0; i< m_objAcn.len;i++){
    lBufLen += sprintf(lBuf + lBufLen, "%02X ", m_objAcn.string[i]);
  }
  std::string str( reinterpret_cast<char*>(lBuf), lBufLen);
  oStr<<": ACN Array: "<<str.c_str();
#endif
	return oStr.str();
}

int
INGwFtPktTcapMsg::serialize(U8* p_buf, int &p_bufLen) 
{
  int value = htons(m_stackDialogue);
  memcpy(p_buf + p_bufLen, (void*)&value, SIZE_OF_INT);
  p_bufLen += SIZE_OF_INT;

  value = htons(m_userDialogue);
  memcpy(p_buf + p_bufLen, (void*)&value, SIZE_OF_INT);
  p_bufLen += SIZE_OF_INT;

  int addrLen = sizeof(SccpAddr);

  memcpy(p_buf + p_bufLen, (void*)&m_srcAddr, addrLen);
  p_bufLen += addrLen;

  memcpy(p_buf + p_bufLen, (void*)&m_dstAddr, addrLen);
  p_bufLen += addrLen;

  short sapId = m_appId.suId;
  sapId = htons(sapId);
  memcpy(p_buf + p_bufLen, &sapId, SIZE_OF_SHORT);
  p_bufLen += SIZE_OF_SHORT;

  sapId = m_appId.spId;
  sapId = htons(sapId);
  memcpy(p_buf + p_bufLen, &sapId, SIZE_OF_SHORT);
  p_bufLen += SIZE_OF_SHORT;

  value = htons(m_ipAddr.size());
  memcpy(p_buf + p_bufLen, (void*)&value, SIZE_OF_INT);
  p_bufLen += SIZE_OF_INT;

  memcpy(p_buf + p_bufLen, (void*)m_ipAddr.c_str(), m_ipAddr.size());
  p_bufLen += m_ipAddr.size();

#if (SS7_ANS88 ||SS7_ANS92 || SS7_ANS96)   

  //encodeAnsiDlgEv(m_stAnsiDlgEv,p_buf + p_bufLen,p_bufLen);
  memcpy(p_buf + p_bufLen, (void*)&m_stAnsiDlgEv, sizeof(StAnsiDlgEv));
  p_bufLen += sizeof(StAnsiDlgEv);

  value = htons(m_billingNo); 
  memcpy(p_buf + p_bufLen, (void*)&value, SIZE_OF_INT);
  p_bufLen += SIZE_OF_INT;
#else

  S16 lAcnLen = htons(m_objAcn.len);
  memcpy(p_buf + p_bufLen, (void*)&m_objAcn.len, SIZE_OF_SHORT);
  p_bufLen += SIZE_OF_SHORT;

  memcpy(p_buf + p_bufLen, (void*)(m_objAcn.string), m_objAcn.len);
  p_bufLen += m_objAcn.len;

#endif
  return 0;
}

#if 1
void addByte(U8* pBuffer, int &pLen, U8 pByte)
{
  pBuffer[pLen++] =  pByte;
}

void addInteger(U8* pBuf, int &pLen, int pVal) {
  //Yogesh:not portable 
  memcpy(pBuf + pLen, &pVal, SIZE_OF_INT);
  pLen += SIZE_OF_INT;
}

void addShort(U8* pBuffer, short pVal, int &pLen) {
  //Yogesh: not portable
  memcpy(&pBuffer, &pVal, SIZE_OF_SHORT);
  pLen += SIZE_OF_SHORT;
}

void encodeBuffer(U8* pBuf, int pContentLen,int &pBufLen,U8* pContentBuf) {
  
  encodeContentLength(pBuf,pBufLen,pBufLen,pContentLen);
  memcpy(pBuf + pBufLen, pContentBuf, pContentLen);
  pBufLen += pContentLen;
}

void encodeContentLength(U8* pBuf, int &pBufLen, int pIndex, int pContentLen) {
  if(pContentLen > 127)
  {             
    U8 lOctetLen = 0;           
    getOctetCount(pContentLen, lOctetLen); 

    int ltemp = (pBufLen - pIndex - 1);     
    pBuf[pIndex] = (U8)lOctetLen| 0x80;  

    //shifting the bytes after index by lOctetLen 
    for(int i=0;i<ltemp;i++){                        
      pBuf[pBufLen + lOctetLen - 1 - i] = pBuf[pBufLen-i-1];
    }                                                 

    for(int i=0;i<lOctetLen;i++){                     
      pBuf[pIndex+lOctetLen-i] = pContentLen & 0xff;         
      pContentLen = pContentLen >>8;                                 
    }     
     
    if(pBufLen == pIndex){                
      pBufLen++;                           
    }                                            

    pBufLen+=lOctetLen;                                
  }                                                 
  else
  {                                             
    pBuf[pIndex] = pContentLen;                          
  }                                     

  if(pBufLen == pIndex){                
    pBufLen++;                           
  }                                            
}

bool
INGwFtPktTcapMsg::encodeAnsiDlgEv(StAnsiDlgEv &pStAnsiDlgEv,
                                  unsigned char* pcBuf,
                                  int &piLen)
{
  if(pStAnsiDlgEv.pres.pres && pStAnsiDlgEv.pres.val)
  {
    addByte(pcBuf,piLen,APP_CONTEXT_NAME_TYPE);
    if(pStAnsiDlgEv.acnType.pres)
    {
      switch(pStAnsiDlgEv.acnType.val)
      {
        case INC_ENC_TYP_INT:
        {
          addByte(pcBuf, piLen, JAIN_ACN_TYPE_INT);
        
          addByte(pcBuf,piLen,APP_CONTEXT_NAME);

          //adding size of int as ACN len
          addByte(pcBuf,piLen,SIZE_OF_INT);
          if(pStAnsiDlgEv.acn.intAcn.pres) 
          {
            addInteger(pcBuf, piLen, pStAnsiDlgEv.acn.intAcn.val);
          }
          
          else
          {
             printf("error in encoding ACN TYP INT, but not pres");
             fflush(stdout);
             return false;
          }
           
        }
        break;

        //YOGESH
        //creating this separate case for oid type ACN in ansi because
        //the encoding of ACN in oid type is done as an array of U16
        case INC_ENC_TYP_OID:
        {
          addByte(pcBuf, piLen, JAIN_ACN_TYPE_OID);
          
          addByte(pcBuf,piLen,APP_CONTEXT_NAME);

          if(pStAnsiDlgEv.acn.oidAcn.pres)
          {
            addByte(pcBuf, piLen, pStAnsiDlgEv.acn.oidAcn.len <<1);
            
            memcpy(pcBuf + piLen, pStAnsiDlgEv.acn.oidAcn.val, 
                   pStAnsiDlgEv.acn.oidAcn.len);
            
            piLen += (pStAnsiDlgEv.acn.oidAcn.len<<1);
          }
          else
          {
            printf("Error MM ACNtype & ACN Rx");fflush(stdout);
            return false;
          }
        }
        break;
 
        default:
          printf("Unknown acnType<%d>",
                            pStAnsiDlgEv.acnType.val);fflush(stdout);
      }
    }
    else
    {
      printf("acnType not pres");fflush(stdout);
    }
  }
  else
  { 
    printf("dialogue portion not pres"); fflush(stdout);
    return false;  
  }
 
  ////have copied it at the time of Begin indication as it is indicated in
  ////Buffer data type allocated on heap by stack
  //if(0 != dlgR.dlgPortnUInfo.len)
  //{
  //  addByte(pcBuf, piLen, DLG_PORTN_USR_INFO);
  //  encodeBuffer(pcBuf,dlgR.dlgPortnUInfo.len,piLen,dlgR.dlgPortnUInfo.string);
  //}


  if(pStAnsiDlgEv.secType.pres) 
  {
    addByte(pcBuf, piLen, SEC_TYPE);
    addByte(pcBuf, piLen, pStAnsiDlgEv.secType.val);
    
    switch(pStAnsiDlgEv.secType.val) 
    {
      case ST_ANS_ENC_TYP_INT:
      {
        addByte(pcBuf, piLen, JAIN_SEC_TYPE_INT);
        addByte(pcBuf, piLen, SEC_VALUE);

        if(pStAnsiDlgEv.secCntxt.intSec.pres) 
        {
          addInteger(pcBuf, piLen, pStAnsiDlgEv.secCntxt.intSec.val);
        }
        else
        {
          printf("Out encodeAnsiDlgEv, intacn "
                            " not pres");
          return false;
        } 
      }
      break;

      case ST_ANS_ENC_TYP_OID:
      {
        addByte(pcBuf, piLen, JAIN_SEC_TYPE_OID);
        addByte(pcBuf, piLen, SEC_VALUE);

        if(pStAnsiDlgEv.secCntxt.oidSec.pres)
        {
          addByte(pcBuf, piLen, pStAnsiDlgEv.secCntxt.oidSec.len<<1);
          
          memcpy(pcBuf + piLen, pStAnsiDlgEv.secCntxt.oidSec.val, 
                 pStAnsiDlgEv.secCntxt.oidSec.len<<1);
          
          piLen += (pStAnsiDlgEv.secCntxt.oidSec.len<<1);
        }
        else
        {
          printf("Out encodeAnsiDlgEv "
                            "Error MM secType & SEC Rx");
          return false;
        }
      }
      break;

      default:
        printf("Out encodeAnsiDlgEv"
                         " Unknown value of sectype <%d>",
                          pStAnsiDlgEv.secType.val);
        fflush(stdout);
        return false;
    }
  }

  if(pStAnsiDlgEv.confInfo.pres)
  {
    addByte(pcBuf, piLen, DLG_CONF_INFO);
    addByte(pcBuf, piLen, pStAnsiDlgEv.confInfo.len);

    encodeBuffer(pcBuf, 
                 pStAnsiDlgEv.confInfo.len, 
                 piLen,
                 pStAnsiDlgEv.confInfo.val);
    
  } 

  return true;
}
#endif
