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
//     File:     INGwSpSipProviderConfig.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************


#ifndef INGW_SP_SIP_PROVIDER_CONFIG_H_
#define INGW_SP_SIP_PROVIDER_CONFIG_H_

#include <string>
#include <vector>
#include <set>

#include <INGwSipProvider/INGwSpSipHeaderPolicy.h>

static const char* ip_port_seprator  = "|";
static const char* address_separator = ",";
static const char* colon             = ":";

struct GwInfo
{
  int    miPort;
  int    m_failureCount;
  std::string mpHost;
  unsigned long m_failureTime;

  GwInfo(){ miPort = 0;
            mpHost = "";
            m_failureCount = 0; 
            m_failureTime = 0; }
};

typedef std::vector<GwInfo*> VecGwInfo;

class INGwSpSipProviderConfig
{
  public:
    typedef enum
    {
      SDPTYPE_DEFAULT, // Copies SDP from originating side.
      SDPTYPE_NONE,    // No SDP is sent.
      SDPTYPE_MUTE     // SDP is sent with c=0.0.0.0
    } SdpType;

    typedef enum
    {
      FROMHDR_FROMPROFILE,  // BayProcessor's from is sent (B2BUA)
      FROMHDR_COPYUSER, // Only the user part is copied from originating side
      FROMHDR_COPYHDR   // The entire hdr is copied from originating side
    } FromHdrTreatment;

    typedef enum {
      HDR_COPY_ALLPARAM,
      HDR_COPY_URLPARAM,
      HDR_COPY_HDRPARAM,
      HDR_COPY_NOPARAM
    } HdrParamTreatment;

    typedef enum
    {
       REQ_URI_VXML_INPARAM,
       REQ_URI_VXML_INUSER
    }VxmlReqUriTreatment;

    typedef enum
    {
      TOHDR_FROMPROFILE,    // Target address
      TOHDR_COPYHDR,
      TOHDR_DEFAULT_DIAL,    // Target address
      TOHDR_DEFAULT_TARGET,    // Target address
      TOHDR_LOOPBACK_DIAL,    // Sent to the originating gateway
      TOHDR_LOOPBACK_TARGET    // Sent to the originating gateway
    } ToHdrTreatment;

    typedef enum
    {
      CONTACTHDR_FROMPROFILE,  // B2BUA behavior
      CONTACTHDR_PEERDN    // contact user part is the original DN
    } ContactHdrTreatment;

    typedef enum {
      CHECK_SE_HDR = 0,
      CHECK_SDP,
      CHECK_SDP_SE_HDR
    } SessionRefreshReinvite;

    typedef enum {
      REDIR_CONTACT_SIP_URI = 0,
      REDIR_CONTACT_TEL_URI
    } RedirectContactURI;

    //privacy related changes - rfc3323 - BPUsa05975
    enum PrivacyConfig 
    {
      SIP_PRIVACY_ABSENT   = 0x00,
      SIP_PRIVACY_NONE     = 0x01,
      SIP_PRIVACY_SESSION  = 0x02,
      SIP_PRIVACY_HEADER   = 0x04,
      SIP_PRIVACY_USER     = 0x08,
      SIP_PRIVACY_ID       = 0x10,
      SIP_PRIVACY_CRITICAL = 0x20
    };

    enum PrivacyProtocol {
      SIP_RPID = 1,
      SIP_RFC3323 = 2
    };

		// BPUsa07893 : [
		typedef enum
		{
			SCREEN_NONE, // No SIP message screening
			SCREEN_INCOMING,    // Only incoming messages are screened
			SCREEN_OUTGOING     // Only outgoing messages are screened
		} ScreenType;

		// : ]

    static int m_checkSessionRefreshReinvite;

    INGwSpSipProviderConfig();
    static void initialize();

    static RedirectContactURI getRedirectContactURI();
    static void setRedirectContactURI(RedirectContactURI uritype);

    static bool              isSdpParsed();
    static void              setSdpParsed(bool aParsed);

    static int               getMinSeValue();

    static int               getMaxSeValue();

    static int               getCopyRRFlag();

    static int               initObGwList();

    static bool              dropExtraneousCalls();
    static void              setDropExtraneousCalls(bool aDrop);

    static SdpType           getOutgoingSdpType();
    static void              setOutgoingSdpType(SdpType aType);

    static FromHdrTreatment  getFromHdrTreatment();
    static void              setFromHdrTreatment(FromHdrTreatment aTreatment);

    static ToHdrTreatment    getToHdrTreatment();
    static void              setToHdrTreatment(ToHdrTreatment aTreatment);

    static ContactHdrTreatment
                             getContactHdrTreatment();
    static void              setContactHdrTreatment
                               (ContactHdrTreatment aTreatment);

    static bool              getRtpTunneling();
    static void              setRtpTunneling(bool aTunnel);

    static int               getStackDebugLevel();
    static void              setStackDebugLevel(int aLevel);

    static void              getOutboundGw(int& port, char** ip, int& psxId);
    static void              markOutboundGwFailed(int psxId);

    static void              
    updatePsxFailStats(int failedPsx, 
      int newPsx, 
      unsigned long deltaFailTime);

    static HdrParamTreatment getToHdrParamTreatment();
    static void setToHdrParamTreatment(HdrParamTreatment aTreatment);

    static HdrParamTreatment getFromHdrParamTreatment();
    static void setFromHdrParamTreatment(HdrParamTreatment aTreatment);

    static HdrParamTreatment getReqLineParamTreatment();
    static void setReqLineParamTreatment(HdrParamTreatment aTreatment);

    static char*
    getDummyUserForFromHdr();

    static int
    getObGwNum();

    int                      getInitialRespTimer();
    void                     setInitialRespTimer(int aTimer);

    int                      getIvrRespTimeout();
    void                     setIvrRespTimeout(int aTimer);

    static bool              m_sessionTmrInfo;
    static bool              m_anchor_connected_event;
    static bool              m_bgidProcessingFlag;
    static bool              m_bgidAppendingFlag;

    static int               getNoAnsTimer();
    static void              setNoAnsTimer(int aTimer);
    static int               getNoAnsTimerReInv();
    static void              setNoAnsTimerReInv(int aTimer);
    static bool              getDontRetrans();
    static void              setDontRetrans(bool aDontRetrans);

    static int               getByeAlsoXfer();
    static void              setByeAlsoXfer(bool aXfer);

    static const std::string&     getRedirGwInfo();

    static bool              getSipRouteUseRedirect();
    static void              setSipRouteUseRedirect(bool aValue);

    static bool              isHoldMsg();
    static void              setHoldMsgFlag(bool);

    static VxmlReqUriTreatment getVxmlReqUriTreatment();
    static void setVxmlReqUriTreatment(VxmlReqUriTreatment intreatment);

    static bool isVxmlUrlSlashReplacement();
    static void setVxmlUrlSlashReplacement(bool inData);

    static bool isSdpSerializationReqd ();

    static int getDefaultPrivacy ();
    static void setDefaultPrivacy (int aiPrivacy);

    static int getPrivacyMask ();
    static void setPrivacyMask (int aiMask);

    static int getPrivacyProtocol ();
    static void setPrivacyProtocol (int aiProtocol);

    static std::set<int> &getProcessedInformationalHeaders ();

    static std::set<std::string> &getUnprocessedInformationalHeaders ();

    static void parseInformationalHeaders (const char *apcList);

    static const char * getAreaAppender();

    static bool updateIvrStatusOnTxnFailure();		// BPInd08347

    static int isConvedia();

    static bool offerSdpInIvrEnabled();		// Mriganka - BPInd08129
    
    static bool sendEmptyInvite(); // Mriganka

		static const GwInfo* getFirewallProxyInfo();

		// BPUsa07885 : [
		static const char* getSRVDomainName();

		static const unsigned long getSRVTTLValue();

		static bool isSRVLookupEnabled();
		// : ]

		// BPUsa07888 : [
		static bool isSIPTParsingEnabled();
		// : ]

		//	BPUsa07893 : [
		static bool isOutgoingMessageScreeningEnabled();

		static bool isIncomingMessageScreeningEnabled();

		static void configureMessageScreening( unsigned int valuemask);
		// : ]

		// BPUsa07898 : 
		static bool isSDPFixEnabled();

		static bool isUseContactForReqUri();

    static bool isTransportUdp();

	private:
		static void _parseFwProxy(std::string fwProxy);


   public:
      //ATT Changes
      static bool isSendBilling();
      static bool isSendReason();
      static bool isSendPhoneContext();
      static bool isSendBody();
      static bool isSendContact();
      static bool isSendAsserted();
      static bool isSendFromParams();
      static bool isSendToParams();
      static bool isSendReqURIParams();
      static bool isSupportEarlyMedia();

      static bool isSendOLI();
      static bool isSendContactAccess();
      static bool isSendContactParams();

      static bool isProcessIncomingVia();

      static const RSI_NSP_SIP::INGwSpSipHeaderPolicy & getSipHeaderPolicy();

      static int getSelfPort();
      static const char * getSelfIp();

   private:

      static bool _sendBilling;
      static bool _sendReason;
      static bool _sendPhoneContext;
      static bool _sendBody;
      static bool _sendContact;
      static bool _sendAsserted;
      static bool _sendFromParam;
      static bool _sendToParam;
      static bool _sendReqURIParam;
      static bool _supportEarlyMedia;

      static bool _sendOLI;
      static bool _sendContactAccess;
      static bool _sendContactParams;

      static bool _processIncomingVia;

      static RSI_NSP_SIP::INGwSpSipHeaderPolicy _headerPolicy;
      static RSI_NSP_SIP::INGwSpSipHeaderDefaultData _headerDefault;

      static std::string _selfIP;
      static int _selfPort;

   private:

      static void _loadHeaderDefaults();
      static void _loadHeaderPolicy();

      //Get the default data from ThreadSpecific. The params in the 
      //defaultData can be used by just incrementing the refCount.

      static const RSI_NSP_SIP::INGwSpSipHeaderDefaultData & getSipHeaderDefault();

   public: 

      friend class INGwSpSipProvider;

  private:

    static RedirectContactURI meRedirContactURI;
    static bool              mbIsSdpParsed;
    static bool              mbDropExtraneousCalls;
    static char*             mpOutboundGwHost;
    static int               miOutboundGwPort;
    static SdpType           meSdpType;
    static FromHdrTreatment  meFromHdrTreatment;
    static ToHdrTreatment    meToHdrTreatment;
    static ContactHdrTreatment    meContactHdrTreatment;
    //static GwInfo*           maGwInfo;
    static int               miActiveGw;
    static bool              mbRtpTunneling;
    static int               miStackDebugLevel;
    static int               miMinSeHdrValue;
    static int               miMaxSeHdrValue;
    static char*             mpDummyUserForFromHdr;

    static HdrParamTreatment m_fmHdrParamTreatment;
    static HdrParamTreatment m_toHdrParamTreatment;
    static HdrParamTreatment m_reqLineParamTreatment;

    int                      miInitialRespTimer;
    static int               miNoAnsTimer;
    static int               miNoAnsTimerReInv;
    static bool              mbDontRetrans;
    int                      miIvrRespTimeout;

    static pthread_mutex_t   m_activePsxLock;
    
    //Out Gw Leak Time 
    static int               m_outGwLeakTime;
    static int               m_outGwFailureLeakCount;

    static VecGwInfo         m_obGwList;
    static int               miNumOfGws;
    static int               m_activePsx;
    static unsigned long     m_psxFailCounter;
    static int               m_psxAlarmInterval;

    static bool              mByeAlsoXfer;
    static std::string            mCallRedirGwInfo;

    static bool              mSipRouteUseRedirect;

    static bool              m_holdSipMsg;
    static bool              mbIsSdpSerializationReqd;

    static VxmlReqUriTreatment   m_vxmlReqUriTreatment;
    static bool m_vxmlUrlSlashReplacement;

    //privacy related changes - rfc3323 - BPUsa05975
    static int               miDefaultPrivacy;

    static int               miPrivacyMask;

    static int               miPrivacyProtocol;

    static bool    			 miSetOfferSdpInIvr; // BPInd08129 // Mriganka

    static bool    			 miUpdateIvrStat; // BPInd08347

    static std::set<std::string> mUnprocessedInformationalHeaders;
    static std::set<int> mProcessedInformationalHeaders;

    static const char * _areaAppender;

    static bool m_isConvedia;

    static int               miCopyOrigRR;

    static bool mbSendEmptyInvite; // Mriganka

		static GwInfo *mFwProxyInfo;

		static std::string _srvDomainName;

		static unsigned long _srvTTLValue;

		static bool _parseSIPTBody;

		static unsigned int _screenSipMessages;
		
		static bool _sdpFixEnabled;

		static bool mIsUseContactForReqUri;

    static bool m_IsTransportUdp;

}; // End of INGwSpSipProviderConfig class

#endif //INGW_SP_SIP_PROVIDER_CONFIG_H_
