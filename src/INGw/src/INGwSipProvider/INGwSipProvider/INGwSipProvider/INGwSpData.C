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
//     File:     INGwSpData.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");

#include <string.h>
#include <INGwSipProvider/INGwSpData.h>
#include <INGwSipProvider/INGwSpSipIncludes.h>

const char* MUTED_SDP_1          = "0.0.0.0";
const char* MUTED_SDP_2          = "inactive";
const char* NORMAL_SDP_SEND_RECV = "sendrecv";
const char* NORMAL_SDP_SEND_ONLY = "sendonly";
const char* NORMAL_SDP_RECV_ONLY = "recvonly";

INGwIfrUtlStrStr INGwSpData::_sdpAppSDPStrStr("application/sdp", false);
INGwIfrUtlStrStr INGwSpData::_sdp2CRLFStrStr("\r\n\r\n", true);
INGwIfrUtlStrStr INGwSpData::_sdpVStrStr("v=", true);
INGwIfrUtlStrStr INGwSpData::_sdpCStrStr("c=IN IP4 ", true);
INGwIfrUtlStrStr INGwSpData::_sdpAStrStr("a=", true);

int INGwSpData::miStCount = 0;
pthread_mutex_t  INGwSpData::mStMutex;


void INGwSpData::setCounters()
{
   pthread_mutex_lock(&mStMutex);
   miStCount++;
   pthread_mutex_unlock(&mStMutex);
}

void INGwSpData::decrementCounters()
{
   pthread_mutex_lock(&mStMutex);
   miStCount--;
   pthread_mutex_unlock(&mStMutex);
}

int
INGwSpData::initStaticCount(void)
{
   return pthread_mutex_init(&mStMutex, NULL);
}

int
INGwSpData::getCount(void) { return miStCount; }


using namespace std;


INGwSpData::INGwSpData()
{ //INCTBD remove logging
  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN INGwSpData Constructor");
  mSipBodyType[0]  = 0;
  mpSipBody        = 0;
  mBodyLength      = 0;
  miChargeStatus   = 0;
  mDialogueId      = 0;
  mSeqNum          = 0;
  mpSipMsg = NULL;

  initObject (true);
  logger.logINGwMsg(false, TRACE_FLAG, 0, "Out INGwSpData Constructor");
}

INGwSpData::~INGwSpData()
{
 //INCTBD remove logging
  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN INGwSpData destructor");
  reset();
  logger.logINGwMsg(false, TRACE_FLAG, 0, "Out INGwSpData destructor");
}

void INGwSpData::reset()
{
  SipError siperror;
  logger.logINGwMsg(false, TRACE_FLAG, 0, "IN INGwSpData::reset");
  if(mpSipBody)
    fast_memfree(0, mpSipBody, &siperror);
  // delete[] mpSipBody;
  mpSipBody = 0;
  setBodyLength(0);
  setBodyType("");
  serializationReadyFlag = COMP_SERI_NOT_READY;
  if(mpSipMsg)
	{
    logger.logINGwMsg(false, TRACE_FLAG, 0, 
										  "INGwSpData::reset: BEFORE FREE count <%u>", 
										  mpSipMsg->dRefCount.ref);
		sip_freeSipMessage(mpSipMsg);
	}
	mpSipMsg         = NULL;

  logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT INGwSpData::reset");
}

void
INGwSpData::getSDPGWData(INGwSpData* apData)
{
   logger.logINGwMsg(false, TRACE_FLAG, 0, "IN INGwSpData::getSDPGWData");

   if(NULL == apData) {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "Input GW data ptr is NULL");
      return;
   }

   if(NULL == mpSipBody) {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "GW body is NULL");
      return;
   }

   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "GW body type is [%s]", mSipBodyType);
   if(0 == strcasecmp(mSipBodyType, "application/sdp")) {
       apData->setBodyType("application/sdp"); 
       apData->setBody(mpSipBody); 
       apData->setBodyLength(mBodyLength);
   }
   else {
       logger.logINGwMsg(false, TRACE_FLAG, 0, "GW body is not application/SDP"
           " - assuming it to be multipart/mixed");

       const char* res = NULL;
       if((res = _sdpAppSDPStrStr.findPatternIn(mpSipBody, mBodyLength)) != NULL) {
           const char* start = _sdpVStrStr.findPatternIn(res, mBodyLength - (res - mpSipBody));
           if(NULL == start) {
               logger.logINGwMsg(false, ERROR_FLAG, 0, "Unable to find v= after application/sdp");
               return;
           }

           const char* end = _sdp2CRLFStrStr.findPatternIn(start, mBodyLength - (start - mpSipBody));
           if(NULL == end) {
               logger.logINGwMsg(false, ERROR_FLAG, 0, "Unable to find CRLFCRLF after v=");
               return;
           }

           logger.logINGwMsg(false, ERROR_FLAG, 0, "SDP size is [%d]", end - start + 2);
           apData->setBodyType("application/sdp"); 
           apData->setBody(start, end - start + 2); 
           apData->setBodyLength(end -start + 2);
       }
       else {
           logger.logINGwMsg(false, ERROR_FLAG, 0, "Unable to find application/sdp in body");
           return;
       }
   }

   logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT INGwSpData::getSDPGWData");
}

#if 0
BpGwConnection::MediaSessionStatus INGwSpData::getMediaSessionStatus(void)
{
   logger.logINGwMsg(false, TRACE_FLAG, 0, "IN INGwSpData::getMediaSessionStatus");

   if(NULL == mpSipBody) 
   {
      logger.logINGwMsg(false, TRACE_FLAG, 0, 
                      "Media session status is UNKNOWN as sdp is NULL, "
                      "assuming inactive");
      return BpGwConnection::INACTIVE;
   }

   // The parsing of the SDP needs to be done only if
   // the GW data is of type SDP and only one SDP as
   // the GW data

   if(0 != strcasecmp(mSipBodyType, "application/sdp")) 
   {
      return BpGwConnection::UNKNOWN;
   }

   // If SDP contains -
   // IP address as set to NULL i.e c=IN IP4 0.0.0.0
   // or session status as inactive i.e. a=inactive
   // the status is INACTIVE
   // else depending on the line "a=xxx" it can be
   // SEND_ONLY, RECV_ONLY or SEND_RECV

   const char* res = NULL;
   if((res = _sdpCStrStr.findPatternIn(mpSipBody, mBodyLength)) != NULL)
   {
      res += 9;

      if(strncmp(res, MUTED_SDP_1, 7) == 0)
      {
         logger.logINGwMsg(false, TRACE_FLAG, 0, 
                         "Media session status is INACTIVE - 0.0.0.0");
         return BpGwConnection::INACTIVE;
      }
   }

   if((res = _sdpAStrStr.findPatternIn(mpSipBody, mBodyLength)) == NULL)
   {
      logger.logINGwMsg(false, TRACE_FLAG, 0, "No a= in SDP.");
      return BpGwConnection::UNKNOWN;
   }

   while(res != NULL)
   {
      res += 2;

      switch(*res)
      {
         case 'i':
         {
            if(strncmp(res, MUTED_SDP_2, 8) == 0)
            {
               logger.logINGwMsg(false, TRACE_FLAG, 0, "Media inactive.");
               return BpGwConnection::INACTIVE;
            }
         }
         break;

         case 'r':
         {
            if(strncmp(res, NORMAL_SDP_RECV_ONLY, 8) == 0)
            {
               logger.logINGwMsg(false, TRACE_FLAG, 0, "Media recvonly.");
               return BpGwConnection::RECV_ONLY;
            }
         }
         break;

         case 's':
         {
            if(res[4] == 'r')
            {
               if(strncmp(res, NORMAL_SDP_SEND_RECV, 8) == 0)
               {
                  logger.logINGwMsg(false, TRACE_FLAG, 0, "Media SendRecv.");
                  return BpGwConnection::SEND_RECV;
               }
            }
            else
            {
               if(strncmp(res, NORMAL_SDP_SEND_ONLY, 8) == 0)
               {
                  logger.logINGwMsg(false, TRACE_FLAG, 0, "Media SendOnly.");
                  return BpGwConnection::SEND_ONLY;
               }
            }
         }
         break;
      }

      res = strstr(res, "a=");
   }

   return BpGwConnection::UNKNOWN;
}
#endif

const void* INGwSpData::getBody()
{
  return mpSipBody;
}

int INGwSpData::getChargeStatus()
{
  return miChargeStatus;
}

void INGwSpData::setChargeStatus(int aStatus)
{
  miChargeStatus = aStatus;
}

void INGwSpData::setBody(const char *aBody, int bodyLen)
{
   if (aBody) 
   {
      logger.logINGwMsg(false, TRACE_FLAG, 0, "IN setBody with body <%s>", aBody);
   }
   else 
   {
      logger.logINGwMsg(false, TRACE_FLAG, 0, "IN setBody with null body");
   }

   if(mpSipBody)
   {
      SipError siperror;
      fast_memfree(0, mpSipBody, &siperror);
      // delete[] mpSipBody;
      mpSipBody = 0;
   }

   if(aBody)
   {
      if(bodyLen == -1)
      {
         bodyLen = strlen(aBody);
      }

      SipError siperror;
      mpSipBody = (char *)fast_memget(0, bodyLen + 1, &siperror);
      memcpy((void *)mpSipBody, (void *)aBody, bodyLen);
      serializationReadyFlag = COMP_SERI_READY;
      //INCTBD
      mpSipBody[bodyLen] = '\0';
   }
   else
   {
      mpSipBody = 0;
   }

   if (mpSipBody) 
   {
      logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT setBody with body SET as <%s>",
                      mpSipBody);
   }

  serializationReadyFlag = COMP_SERI_READY;

}

unsigned INGwSpData::getBodyLength()
{
  return mBodyLength;
}

void INGwSpData::setBodyLength(unsigned aLen)
{
  mBodyLength = aLen;
}

const char* INGwSpData::getBodyType()
{
  return (const char *)mSipBodyType;
}

void INGwSpData::setBodyType(const char *aBodyType)
{
  strncpy(mSipBodyType, aBodyType, MAX_BODYTYPE_LEN - 1);
  mSipBodyType[MAX_BODYTYPE_LEN - 1] = 0;
}

void INGwSpData::copyMsgBody(INGwSpData &aGwData)
{
  if(this == &aGwData)
  {
    logger.logINGwMsg(false, ERROR_FLAG, 0, "Attempt to copy self");
    return;
  }

  if (aGwData.mpSipBody) {
    logger.logINGwMsg(false, TRACE_FLAG, 0, "IN copyMsgBody with body <%s>", aGwData.mpSipBody);
  }

  logger.logINGwMsg(false, TRACE_FLAG, 0, " copyMsgBody with length <%d>", aGwData.mBodyLength);

#if 0
  if(0 != aGwData.mSipBodyType) {
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
      "BDYLEN:<%d>, BDYTYPE:<%s>",
      aGwData.mBodyLength, aGwData.mSipBodyType);
  }
  else {
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
      "BDYLEN:<%d>, BDYTYPE:<null>",
      aGwData.mBodyLength);
  }

  if(0 != aGwData.mpSipBody) {
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
      "BDY:<%s>", aGwData.mpSipBody);
  }
#endif

  memcpy(mSipBodyType, aGwData.mSipBodyType, MAX_BODYTYPE_LEN);

  serializationReadyFlag = COMP_SERI_READY;

  if(mpSipBody)
  {
    SipError siperror;
    fast_memfree(0, mpSipBody, &siperror);
    // delete [] mpSipBody;
    mpSipBody    = 0;
    mBodyLength = 0;
  }

  if((aGwData.mpSipBody) && (aGwData.mBodyLength > 0))
  {
    SipError siperror;

    mpSipBody = (char *)fast_memget(0, aGwData.mBodyLength + 1, &siperror);
    // mpSipBody = new char[aGwData.mBodyLength + 1];
    memcpy((void *)mpSipBody, aGwData.mpSipBody, aGwData.mBodyLength);
    ((char *)mpSipBody)[aGwData.mBodyLength] = '\0';
    mBodyLength = aGwData.mBodyLength;
  }

  if (mpSipBody) {
    logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT copyMsgBody body is <%s> and length is <%d>", mpSipBody, mBodyLength);
  }
}

bool
INGwSpData::serialize (unsigned char*       apcData,
                     int                  aiOffset,
                     int&                 aiNewOffset,
                     int                  aiMaxSize,
                     bool                 abForceFullSerialization)
{
  logger.logINGwMsg (false, TRACE_FLAG, 0,
    "Entering INGwSpData::serialize");

  aiNewOffset = aiOffset;

  int liFlag = serializationReadyFlag;

  // BPInd11926 - STARTS.
  if (true == abForceFullSerialization) {
    liFlag = COMP_SERI_READY;
  }
  // BPInd11926 - ENDS.

  aiNewOffset += INGwIfrUtlSerializable::serializeInt (liFlag,
                             (char *)(apcData + aiNewOffset));

  // BPInd11926 - STARTS.
  // SDP i.e. the Gateway Data shall be serailized in "abForceFullSerialization"
  // is true.
  // BPInd11926 - ENDS.
  // if (serializationReadyFlag == COMP_SERI_READY)
  if ((true == abForceFullSerialization) ||
      (serializationReadyFlag == COMP_SERI_READY))
  {
    aiNewOffset += INGwIfrUtlSerializable::serializeStruct(mSipBodyType,
                             MAX_BODYTYPE_LEN, 
                             (char *)(apcData + aiNewOffset));
    aiNewOffset += INGwIfrUtlSerializable::serializeInt (mBodyLength,
                             (char *)(apcData + aiNewOffset));
    aiNewOffset += INGwIfrUtlSerializable::serializeStruct(mpSipBody,
                             mBodyLength, 
                             (char *)(apcData + aiNewOffset));
    aiNewOffset += INGwIfrUtlSerializable::serializeInt (miChargeStatus,
                             (char *)(apcData + aiNewOffset));

    logger.logINGwMsg (false, VERBOSE_FLAG, 0,
      "After serializing INGwSpData : <%s>, <%s>, <%d>",
      mSipBodyType, (mBodyLength == 0 ? "NULL" : mpSipBody), mBodyLength);
  }

  serializationReadyFlag = COMP_SERI_NOT_READY;

  logger.logINGwMsg (false, TRACE_FLAG, 0,
    "Leaving INGwSpData::serialize");

  return true;
}

bool
INGwSpData::deserialize (const unsigned char* apcData,
                       int                  aiOffset,
                       int&                 aiNewOffset,
                       int                  aiMaxSize)
{
  logger.logINGwMsg (false, TRACE_FLAG, 0,
    "Entering INGwSpData::deserialize");

  int liFlag;
  aiOffset += INGwIfrUtlSerializable::deserializeInt (
                       (char *)(apcData + aiOffset), liFlag);

  // BPInd11926 - STARTS.
  // In Pre-Paid flow when Low Balancer Timer is started,
  // SDP is not serialized by Active CCM, and because of that when deserialized
  // by Standby CCM the flag "serializationReadyFlag" is unset and hence when 
  // double fault happens this SDP never get serialized by takeover Stanby to
  // newly joind Standby system. So "serializationReadyFlag" should remain in
  // COMP_SERI_NOT_READY state oncw set to so.

  // serializationReadyFlag = (INGwIfrUtlSerializable::SerializationReadyState) liFlag;
  // if (serializationReadyFlag == COMP_SERI_READY)
  INGwIfrUtlSerializable::SerializationReadyState serFlagState = 
                    (INGwIfrUtlSerializable::SerializationReadyState) liFlag;
  if (serFlagState == COMP_SERI_READY)
  // BPInd11926 - ENDS.
  {
    aiOffset += INGwIfrUtlSerializable::deserializeStruct(
                             (char *)(apcData + aiOffset),
                             mSipBodyType, MAX_BODYTYPE_LEN);
    int liLength;
    aiOffset += INGwIfrUtlSerializable::deserializeInt (
                               (char *)(apcData + aiOffset), liLength);
    mBodyLength = liLength;
    SipError siperror;
    if(mpSipBody)
      fast_memfree(0, mpSipBody, &siperror);
    mpSipBody = (char *)fast_memget(0, mBodyLength+ 1, &siperror);
    aiOffset += INGwIfrUtlSerializable::deserializeStruct(
                               (char *)(apcData + aiOffset),
                               mpSipBody, mBodyLength);
    mpSipBody [mBodyLength] = '\0';
    aiOffset += INGwIfrUtlSerializable::deserializeInt (
                               (char *)(apcData + aiOffset),
                               miChargeStatus);

    // BPInd11926 - STARTS.
    serializationReadyFlag = serFlagState;
    // BPInd11926 - ENDS.

    logger.logINGwMsg (false, VERBOSE_FLAG, 0,
      "After deserializing INGwSpData : <%s>, <%s>, <%d>",
      mSipBodyType, (mBodyLength == 0 ? "NULL" : mpSipBody), mBodyLength);

  }

	aiNewOffset = aiOffset;

	// Fix for double-failure scenario in N+1 : [
	//	Since a new Connection is being created at the 
	//	time of de-serialization, it has to be a stable connection 
	//	and hence ready to be serialized along with SDPs.
	// serializationReadyFlag = COMP_SERI_NOT_READY;
	// : ]

  logger.logINGwMsg (false, TRACE_FLAG, 0,
    "Leaving INGwSpData::deserialize");

  return true;
}

std::string INGwSpData::toLog() const
{
   string ret = "\nGwData.\n";

   ret += "BodyType:";
   ret += mSipBodyType;

   if(mpSipBody != NULL)
   {
      ret += "\nBody:\n";
      ret += mpSipBody;
   }

   char *local = new char[500];
   sprintf(local, "\nBodyLength:[%d] ChargeStatus:[%d]\n", 
           mBodyLength, miChargeStatus);
   ret += local;
   delete []local;

   return ret;
}

SipMessage*  INGwSpData::getSipMessage()
{
 	 return mpSipMsg;
}


void  INGwSpData::setSipMessage(SipMessage * aSipMsg)
{
   if(mpSipMsg)
   {
		 logger.logINGwMsg(false, TRACE_FLAG, 0, 
										 "INGwSpData::setSipMessage: BEFORE FREE count <%u>", 
										 aSipMsg->dRefCount.ref);
		 sip_freeSipMessage(mpSipMsg);
	 }
   mpSipMsg = aSipMsg;
}


void INGwSpData::copy(INGwSpData &aSipData, int aCopyAttribute)
{
   if(this == &aSipData)
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, 
											"INGwSpData::copy: Attempt to copy self");
      return;
   }

   if(aCopyAttribute & ATTRIB_MSGBODY)
   {
      copyMsgBody(aSipData);
   }

   if(aCopyAttribute & ATTRIB_SIPMESG)
   {
      if(mpSipMsg)
      {
         logger.logINGwMsg(false, TRACE_FLAG, 0, 
												 "SipData::copy: BEFORE FREE count <%u>", 
												 mpSipMsg->dRefCount.ref);
				 sip_freeSipMessage(mpSipMsg);
				 mpSipMsg         = NULL;
			}
			if(aSipData.mpSipMsg)
			{
			   SipError    siperror;
			   SipMessage *clonedSipMsg = NULL;
			   sip_initSipMessage (&clonedSipMsg, aSipData.mpSipMsg->dType, &siperror);
			   sip_cloneSipMessage(clonedSipMsg , aSipData.mpSipMsg,        &siperror);
			   mpSipMsg = clonedSipMsg;
			} // end of if

   } // end of if
}

void INGwSpData::setDialogueId(int pDlgId){
  mDialogueId =  pDlgId;
}

void INGwSpData::setBillingId(int piBillingId){
  miBillingId =  piBillingId;
}

int INGwSpData::getDialogueId() {
  return mDialogueId;
}

int INGwSpData::getBillingId() {
  return miBillingId;
}

void INGwSpData::setSeqNum(int pSeqNum) {
  mSeqNum = pSeqNum;
}

int INGwSpData::getSeqNum() {
  return mSeqNum;
}
