//********************************************************************
//
//     File:    INGwFtPktSasHBFailure.C
//
//     Desc:     
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Yogesh Tripathi 23/02/12       Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktSasHBFailure.h>

INGwFtPktSasHBFailure::INGwFtPktSasHBFailure(void)
{
    mMsgData.msMsgType = MSG_HANDLE_SAS_HB_FAILURE;
}

INGwFtPktSasHBFailure::~INGwFtPktSasHBFailure()
{
}

void
INGwFtPktSasHBFailure::initialize( string p_ipAddr, AppInstId p_appId,
                                   short srcid, short destid)
{
  mMsgData.msSender     = srcid;
  mMsgData.msReceiver   = destid;

  memset(m_ipAddr,0,sizeof(m_ipAddr));
  memcpy(m_ipAddr,p_ipAddr.c_str(),p_ipAddr.size());
  memcpy(&m_appId,&p_appId, sizeof(p_appId));
}

int
INGwFtPktSasHBFailure::depacketize(const char* apcData, int asSize, int version)
{
   if(version != 1)
   {
      return 0;
   }

   int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

   U8 ipSize = 0;
   memcpy(&ipSize, apcData + offset, SIZE_OF_CHAR);
   offset += SIZE_OF_CHAR;

   memset(m_ipAddr,0,sizeof(m_ipAddr));
   memcpy(m_ipAddr, apcData + offset, ipSize);
   offset +=  ipSize;

   short value = -1;
   memcpy(&value, apcData + offset, SIZE_OF_SHORT);
   offset += SIZE_OF_SHORT;
   m_appId.suId = ntohs(value);

   value = -1;
   memcpy(&value, apcData + offset, SIZE_OF_SHORT);
   offset += SIZE_OF_SHORT;
   m_appId.spId = ntohs(value);

   return (offset);
}

int
INGwFtPktSasHBFailure::packetize(char** apcData, int version)
{
   if(version != 1)
   {
      return 0;
   }

   *apcData = NULL;
   char lBuf[200];
	 int lBufLen = 0;
   int offset = 0;
   U8 ipSize = 0;
   short value;

   ipSize = strlen(m_ipAddr);
   memcpy(lBuf + lBufLen, (void*)&ipSize, SIZE_OF_CHAR);
   lBufLen+= SIZE_OF_CHAR;
  
   memcpy(lBuf + lBufLen, m_ipAddr, ipSize);
   lBufLen+= ipSize;
    
   value = htons(m_appId.suId);
   memcpy(lBuf + lBufLen, &value,SIZE_OF_SHORT);
   lBufLen+=SIZE_OF_SHORT;

   value = htons(m_appId.spId);
   memcpy(lBuf + lBufLen, &value, SIZE_OF_SHORT);
   lBufLen+=SIZE_OF_SHORT;

   offset = 
      INGwFtPktMsg::createPacket(lBufLen , apcData, version);
 
   char *pkt = *apcData;

   memcpy(pkt + offset, (void*)lBuf, lBufLen);
   offset += lBufLen;

   return offset;
}

std::string
INGwFtPktSasHBFailure::toLog(void) const
{
    std::ostringstream    oStr;
		std::string lRoleStr = " INC_HANDLE_SAS_HB_TIMEOUT";

    oStr << INGwFtPktMsg::toLog();
    oStr << "SAS IP: "  <<  m_ipAddr << " suId : "<<m_appId.suId
         <<"spId :"<<m_appId.spId << endl << lRoleStr.c_str()<<endl;
    return oStr.str();
}

void 
INGwFtPktSasHBFailure::getSasIp(string &p_ipAddr){
  p_ipAddr = m_ipAddr;
}

void 
INGwFtPktSasHBFailure::getAppId(AppInstId &p_appId){
  p_appId.suId = m_appId.suId;
  p_appId.spId = m_appId.spId; 
}
