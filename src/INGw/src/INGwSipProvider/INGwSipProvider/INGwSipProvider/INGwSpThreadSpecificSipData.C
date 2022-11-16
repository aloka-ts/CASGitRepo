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
//     File:     INGwSpINGwSpThreadSpecificSipData.C
//
//     Desc:
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <INGwSipProvider/INGwSpThreadSpecificSipData.h>
#include <INGwSipProvider/INGwSpSipCommon.h>

using namespace std;


INGwSpThreadSpecificSipData::INGwSpThreadSpecificSipData()
{
   // Initialize the remote retransmission hash table.

   mSendSocket = socket(AF_INET, SOCK_DGRAM, 0);

   int dSocketBufSize = 262144;
   if(setsockopt(mSendSocket, SOL_SOCKET, SO_SNDBUF, &dSocketBufSize, 
      sizeof(dSocketBufSize)) != 0)
   {
      perror("Failed to increase UDP max buffer size\n");
      exit(0);
   }

   mpRemoteRetransHash = NULL;

   mpcFTContextData   =  0;
   miFTContextOffset  = -1;
   miFTContextMaxSize = -1;
   conn = NULL;
   msgBuf = NULL;
   msgTransport = NULL;

   //Static commonly used params.

   {
      SipError err;
      SIP_S8bit *value;

      sip_initSipParam(&userPhoneParam, &err);

      userPhoneParam->pName = (SIP_S8bit *)fast_memget(0, 5, &err);
      strcpy(userPhoneParam->pName, "user");

      value = (SIP_S8bit *)fast_memget(0, 6, &err);
      strcpy(value, "phone");

      sip_listInsertAt(&(userPhoneParam->slValue), 0, value, &err);

      sip_initSipParam(&earlyYesParam, &err);

      earlyYesParam->pName = (SIP_S8bit *)fast_memget(0, 6, &err);
      strcpy(earlyYesParam->pName, "early");

      value = (SIP_S8bit *)fast_memget(0, 4, &err);
      strcpy(value, "yes");

      sip_listInsertAt(&(earlyYesParam->slValue), 0, value, &err);

      sip_initSipParam(&earlyNoParam, &err);

      earlyNoParam->pName = (SIP_S8bit *)fast_memget(0, 6, &err);
      strcpy(earlyNoParam->pName, "early");

      value = (SIP_S8bit *)fast_memget(0, 3, &err);
      strcpy(value, "no");

      sip_listInsertAt(&(earlyNoParam->slValue), 0, value, &err);
   }
}

int INGwSpThreadSpecificSipData::getSendSocket()
{
   return mSendSocket;
}

INGwSpSipCallTable& INGwSpThreadSpecificSipData::getCallTable()
{
   return mCallTable;
}

INGwSpStackTimer& INGwSpThreadSpecificSipData::getStackTimer()
{
   return mStackTimer;
}

Sdf_st_hash* INGwSpThreadSpecificSipData::getRemoteRetransHash()
{
   return mpRemoteRetransHash;
}

void INGwSpThreadSpecificSipData::setRemoteRetransHash(Sdf_st_hash* aHash)
{
   mpRemoteRetransHash = aHash;
}

INGwSpSipProviderConfig& INGwSpThreadSpecificSipData::getConfigRepository()
{
   return mConfigRep;
}

void INGwSpThreadSpecificSipData::setStackSerializationContext(unsigned char* apcData,
                                                    int aiOffset, int aiMaxSize)
{
   mpcFTContextData = apcData;
   miFTContextOffset = aiOffset;
   miFTContextMaxSize = aiMaxSize;
}

void INGwSpThreadSpecificSipData::getStackSerializationContext(
                         unsigned char*& apcData, int& aiOffset, int& aiMaxSize)
{
   apcData = mpcFTContextData;
   aiOffset = miFTContextOffset;
   aiMaxSize = miFTContextMaxSize;
}

const RSI_NSP_SIP::INGwSpSipHeaderDefaultData &
                                    INGwSpThreadSpecificSipData::getHdrDefault() const
{
   return headerDefault;
}

void INGwSpThreadSpecificSipData::setHdrDefault(
                              const RSI_NSP_SIP::INGwSpSipHeaderDefaultData &inData)
{
   headerDefault = inData;
}

