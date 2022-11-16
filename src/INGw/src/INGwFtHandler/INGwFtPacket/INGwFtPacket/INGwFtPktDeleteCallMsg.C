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
//     File:     INGwFtPktDeleteCallMsg.C
//
//     Desc:
//
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj Bathwal  23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwFtPacket");

#include <INGwFtPacket/INGwFtPktDeleteCallMsg.h>

INGwFtPktDeleteCallMsg::INGwFtPktDeleteCallMsg(void)
{
   mMsgData.msMsgType = MSG_FT_DELETE_CALL;
}

INGwFtPktDeleteCallMsg::~INGwFtPktDeleteCallMsg()
{
}

void INGwFtPktDeleteCallMsg::initialize(const char* callid, 
                                 short srcid, short destid)
{
   mMsgData.msSender        = srcid;
   mMsgData.msReceiver      = destid;
   mMsgData.msMajorSeqNum   = 0;
   mMsgData.msMinorSeqNum   = 0;

   mCallId           = callid;
}

const std::string& INGwFtPktDeleteCallMsg::getCallId(void)
{
   return mCallId;
}

int INGwFtPktDeleteCallMsg::depacketize(const char* apcData, int asSize, 
                                   int version)
{
   if(version != 1)
   {
      return 0;
   }
   LogINGwTrace(false, 0, "INGwFtPktDeleteCallMsg::depacketize");

   int offset = INGwFtPktMsg::depacketize(apcData, asSize, version);

   offset = getVLString(apcData, offset, mCallId);

   return (offset);
}

int INGwFtPktDeleteCallMsg::packetize(char** apcData, int version)
{
   if(version != 1)
   {
      return 0;
   }

   *apcData = NULL;

   short sCallIdLen = mCallId.size();

   // Create a packet 
   int offset = INGwFtPktMsg::createPacket(BASE_DELETE_CALL_MSG_SIZE + 
                                      SIZE_OF_SHORT + sCallIdLen,
                                      apcData, version);

   char *pkt = *apcData;

   // Add the call length
   sCallIdLen    = htons(sCallIdLen);
   memcpy(pkt + offset, (void*)&sCallIdLen, SIZE_OF_SHORT);
   sCallIdLen    = ntohs(sCallIdLen);
   offset += SIZE_OF_SHORT;

   // Add the callId
   memcpy(pkt + offset, (void*)mCallId.c_str(), sCallIdLen);
   offset += sCallIdLen;

   return offset;
}

std::string INGwFtPktDeleteCallMsg::toLog(void) const
{
   std::ostringstream    oStr;
   oStr << INGwFtPktMsg::toLog();
   oStr << ", Call Id : " << mCallId << "] ";
   return oStr.str();
}

