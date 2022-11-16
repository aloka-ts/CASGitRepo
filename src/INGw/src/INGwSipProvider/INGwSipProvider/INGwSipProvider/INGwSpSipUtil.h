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
//     File:     INGwSpSipUtil.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************


#ifndef INGW_SP__SIP_UTIL_H_
#define INGW_SP__SIP_UTIL_H_

#include <INGwInfraUtil/INGwIfrUtlStrStr.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpData.h>
#include <INGwSipProvider/INGwSpSipConnection.h>

#include <string>

#define MAX_HEADER_LEN	1024

class INGwSpSipUtil
{
   private:

      static std::string ourEndPoint;
      static INGwIfrUtlStrStr _sdpAppSDPStrStr;
      static INGwIfrUtlStrStr _isupAppISUPStrStr; //BPUsa07888
      static INGwIfrUtlStrStr _gtdAppGTDStrStr;   //BPInd17865
      static INGwIfrUtlStrStr _sdpCStrStr;
      static int miSipHdrOpt; 
   public:

      static void initialize();

    static void checkError
      (Sdf_ty_retVal aRetval, Sdf_st_error &aError);

    static char* getUserFromNameInfo
      (Sdf_st_nameInfo *aNameInfo);

    static Sdf_ty_retVal createIpDataFromSipMessage
      (SipMessage         *aSipMsg,
       Sdf_ty_slist       *aMsgBodyList,
       en_SipMessageType   aType,
       INGwSpData            *aSipData,
       Sdf_st_error       *aErr,
       int                  aAttrib,
			 INGwSpSipConnection	*aSipConnection = NULL);

    static INGwSipMethodType getMethodFromSipMessage
      (SipMessage *aSipMessage);

    static Sdf_ty_retVal copyMsgBodyIntoSipMessage
     (SipMessage *aSipMessage,
      const char *aMsgBody,
      int         aMsgBodyLen);

    static char* getUserFromHdr
      (SipHeader *aHdr,
       char *aOutUser,
       int aMaxLen);

    static char* getDNameFromHdr
      (SipHeader *aHdr,
       char *aOutUser,
       int aMaxLen);

    static Sdf_ty_retVal setContentTypeInMessage
      (SipMessage *aSipMessage,
       const char *aType,
       int        contentLength,
       int contentTypeLength = 0);

    static Sdf_ty_retVal sendCallToPeer
      (Sdf_st_callObject *aCallObj   ,
       SipMessage        *aSipMsg    ,
       INGwSipMethodType       aMethodType,
       Sdf_st_error      *aErr       ,
       INGwSpSipConnection  *apConn = NULL);

    static short releaseConnection
      (INGwSpSipConnection *aSipConnection);

    static char* muteBody
      (const char* aType, const char *aBody, int aBodyLength);

    static char* muteSdp
      (const char *aSdp, int sdpLength);

    static char* muteMultipart
      (const char *aSdp, int sdpLength);

    static void getHostFromHdr
      (SipHeader *aHdr,
       char **aOutHost,
       int *aOutPort = NULL);

    static Sdf_ty_retVal copyFromHdr
      (Sdf_st_callObject* fromCallObj,
       Sdf_st_callObject* toCallObj,
       bool copyUrlParams = false,
       bool copyHdrParams = false,
       void* paramList = 0);

    static Sdf_ty_retVal copyToHdr
      (Sdf_st_callObject* fromCallObj,
       Sdf_st_callObject* toCallObj);

    static Sdf_ty_retVal copyFromHdrUser
      (Sdf_st_callObject* fromCallObj,
       Sdf_st_callObject* toCallObj);

    static Sdf_ty_retVal replaceFromHdrUser
      (Sdf_st_callObject* apCallObj,
       INGwSpAddress &arOrigAddr);

    static Sdf_ty_retVal copyUrlParams
      (SipUrl* fromUrl,
       SipUrl* toUrl,
       Sdf_st_error* pError);

    static Sdf_ty_retVal copyHeaderParams
      (SipHeader* fromHeader,
       SipHeader* toHeader,
       Sdf_st_error* pError);

    static char* getReqUriParams
      (SipUrl* reqUri, 
       INGwSpSipConnection* bpConn,
       Sdf_st_error* pError);

    static char* getFromUrlParams
      (SipHeader* fromHdr, 
       INGwSpSipConnection* bpConn,
       Sdf_st_error* pError);

    static char* getToUrlParams
      (SipHeader* toHdr,
       INGwSpSipConnection* bpConn,
       Sdf_st_error* pError, bool &isTelURL, INGwSpAddress &dialAddrStr);

    static Sdf_ty_retVal processDiversionHdr
      (SIP_S8bit* hdr, 
       INGwSpSipConnection* bpConn, SIP_U32bit & causeCode); // Mriganka - BPInd07988

    static SIP_U32bit getDiversionReason(SIP_S8bit* apHdr); // Mriganka - BPInd07988

    static void addInformationalHeaders (SipMessage             *aSrc,
                            Sdf_st_callObject      *hssCallObject);

    static void removeInformationalHeaders (SipMessage             *aSrc);


    static Sdf_ty_retVal processPrivacyHeader
      (SIP_S8bit* hdr, 
       INGwSpSipConnection* bpConn);

    static Sdf_ty_retVal processPAIHeader
      (const char* hdr, INGwSpSipConnection* bpConn, int &aiPrivacyParams);

   private:

      static void _removePrivacyHeader(SipMessage *ourSipMsg);
      static void _addPrivacyHeader(int gwToProcessMask, SipMessage *ourSipMsg);
      static void _anonymizeHeaders(SipMessage *ourSipMsg);
      static void _addAssertedHeader(SipMessage *ourSipMsg, INGwSpSipConnection *apPeerConn);
      static void _removeAssertedHeader(SipMessage *ourSipMsg);
      static void _addInformationalHeaders(SipMessage *ourSipMsg,
                                           SipMessage *peerSipMsg);
      static void _removeInformationalHeaders(SipMessage *ourSipMsg);

   public:

		//	BPUsa07893 : 
		static int screenSipMessageForCorruption(const char* buffer, unsigned int length);

		// BPUsa07898 : 
		static int fixSDPBody(const char* buffer, unsigned int length, char** pNewBuffer, unsigned int* pNewLength);

    static Sdf_ty_retVal applyPrivacy (Sdf_st_callObject *aCallObj,
                                  SipMessage        *aSipMsg,
                                  INGwSpSipConnection   *apConn,
                                  INGwSpSipConnection   *apPeerConn);

    static Sdf_ty_retVal processPPIHeader
      (SIP_S8bit* hdr, 
       INGwSpSipConnection* bpConn);

    static std::string getNumber
      (std::string url);

    static int getFromHdrTag
      (SipHeader* fromHdr,
       char** fromTag);

    static int getToHdrTag
      (SipHeader* toHdr,
       char** toTag);

    static int createAndAddHeader
      (Sdf_st_callObject* hssCallObject,
       Sdf_ty_transactionType txType,
       en_HeaderType hdrType,
       Sdf_ty_s8bit* pHdr,
       Sdf_st_error* pErr);

    static int createAndAddHeader
      (Sdf_st_callObject* hssCallObject,
       Sdf_ty_transactionType txType,
       en_HeaderType hdrType,
       SipHeader* sourceHeader,
       Sdf_st_error* pErr);

    static int createAndAddUnknownHeader
      (Sdf_st_callObject* hssCallObject,
       Sdf_ty_transactionType txType,
       Sdf_ty_s8bit* pHdr,
       Sdf_ty_s8bit* pHdrName,
       Sdf_st_error* pErr);

    static Sdf_ty_retVal deleteUnknownHeader(const char*, SipMessage*);

    static bool isSessionRefreshReinvite
      (INGwSpData* newSipData,
       INGwSpData* baseSipData,
       Sdf_st_callObject* pCallObj);

    static bool isSessionRefreshReinviteSE(
      Sdf_st_callObject* pCallObj);

    static bool isSessionRefreshReinviteSDP(
      INGwSpData* newSipData,
      INGwSpData* baseSipData);

    static INGwSpSipConnection* getINGwSipConnFromHssCall
      (Sdf_st_callObject* hssCallObj);

    static SipHeader* makeFromHeader
      (const char* aUsername,
       const char* aAddr    ,
       int         aPort    );

    static SipHeader* makeContactHeader
      (const char* aUsername,
       const char* aAddr    ,
       int         aPort    );

    static bool getContactInfo
      (SipHeader *aHdr,
       INGwSipEPInfo *aInfo);

    static bool isSupported(
      const char* option,
      SipMessage* sipMsg);

    static bool addSupportedHdr(
      SIP_S8bit* hdrString, 
      Sdf_st_callObject* hssCallObj);

    static Sdf_st_overlapTransInfo* getLastOverlapTransInfo
      (Sdf_st_callObject* aCallObj);

   static void processRemotePartyID(INGwSpSipConnection *conn, INGwSpAddress &addr,
                                    SipHeader hdrWrap);

   static bool INGwSpSipUtil::setContactUser
      (SipMessage* aSipMsg, const char *aUser);

   static void buildHeaderCopyProfile(const char* apListOfHeaders);
   static void copyHeaders(SipMessage*             aSrc,
                           Sdf_st_callObject*      hssCallObject,
                           Sdf_ty_transactionType  txType);

   static bool setBodyFromAttr(SipMessage *msg, INGwSpSipConnection *conn);
   static SipUnknownHeader * makeUnknownHeader(const char *name, int nameLen,
                                               const char *val, int valLen);
   static bool setBillingInfoFromAttr(SipMessage *msg, INGwSpSipConnection *conn);
   static bool setReasonInfoFromAttr(SipMessage *msg, INGwSpSipConnection *conn);
   static bool setAssertedFromAttr(SipMessage *msg, INGwSpSipConnection *conn);
   static void addHeadersFromAttr(SipMessage *msg, INGwSpSipConnection *conn);

   static bool setContactInAttr(SipMessage *msg, INGwSpSipConnection *conn);
   
	 static SipUrl * getSipUrlFromHdr(SipHeader *hdr);

	 static char*	getRouteHeaderFromRR( Sdf_st_callObject *hssCallObj);

	 static bool setContactAddSpec(SipContactHeader* p_ContactHeader,
																 INGwSpSipConnection* p_Conn);

	 static bool updateSipAddSpec(INGwSpSipConnection* p_Conn);

   static bool processIncomingViaHeader(INGwSpSipConnection *conn, 
                                        Sdf_st_callObject *hssObj);

};

#endif
