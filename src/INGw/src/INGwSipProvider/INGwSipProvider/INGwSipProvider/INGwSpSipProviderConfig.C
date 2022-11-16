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
//     File:     INGwSpSipProviderConfig.C
//
//     Desc:     
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipProvider");


#include <stdio.h>
#include <stdlib.h>

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <string>
#include <vector>
#include <strstream>
#include <iterator>

#include <INGwSipProvider/INGwSpSipProviderConfig.h>
#include <INGwInfraUtil/INGwIfrUtlAlgorithm.h>

#include <INGwInfraParamRepository/INGwIfrPrParamRepository.h>
#include <INGwInfraManager/INGwIfrMgrAlarmMgr.h>

#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipIncludes.h>

#include <INGwSipProvider/INGwSpSipUtil.h>
#include <INGwSipProvider/INGwSpStackConfigMgr.h>

using namespace std;

typedef vector<string> StrVector;
typedef StrVector::iterator StrVectorIt;

char userprofile[] = "/home/aditya/up.xml";

#include <Util/imErrorCodes.h>
#include <iostream.h>

#include <INGwInfraStreamManager/INGwIfrSmAppStreamer.h>

extern BpGenUtil::INGwIfrSmAppStreamer *sipMsgStream;

extern Sdf_ty_s32bit glbMinSE;

INGwSpSipProviderConfig::VxmlReqUriTreatment 
INGwSpSipProviderConfig::m_vxmlReqUriTreatment = REQ_URI_VXML_INPARAM;

bool    INGwSpSipProviderConfig::m_vxmlUrlSlashReplacement = false;
bool    INGwSpSipProviderConfig::m_holdSipMsg = false;

INGwSpSipProviderConfig::RedirectContactURI INGwSpSipProviderConfig::meRedirContactURI 
                                   = INGwSpSipProviderConfig::REDIR_CONTACT_SIP_URI;

bool    INGwSpSipProviderConfig::m_bgidProcessingFlag = true;
bool    INGwSpSipProviderConfig::m_bgidAppendingFlag  = false;
pthread_mutex_t INGwSpSipProviderConfig::m_activePsxLock;
VecGwInfo INGwSpSipProviderConfig::m_obGwList;
unsigned long   INGwSpSipProviderConfig::m_psxFailCounter = 0;
bool    INGwSpSipProviderConfig::m_sessionTmrInfo       = false;
bool    INGwSpSipProviderConfig::m_anchor_connected_event = false;
int     INGwSpSipProviderConfig::m_psxAlarmInterval     = 0;
int     INGwSpSipProviderConfig::m_outGwLeakTime        = 0;
int     INGwSpSipProviderConfig::m_outGwFailureLeakCount   = 0;
int     INGwSpSipProviderConfig::m_activePsx            = 0;
bool    INGwSpSipProviderConfig::mbIsSdpParsed          = false;
bool    INGwSpSipProviderConfig::mbDropExtraneousCalls  = false;
char*   INGwSpSipProviderConfig::mpOutboundGwHost       = NULL;
int     INGwSpSipProviderConfig::miOutboundGwPort       = 0;

int     INGwSpSipProviderConfig::miCopyOrigRR       = 0;

int     INGwSpSipProviderConfig::m_checkSessionRefreshReinvite =
          INGwSpSipProviderConfig::CHECK_SE_HDR;
string  INGwSpSipProviderConfig::mCallRedirGwInfo = "";

INGwSpSipProviderConfig::SdpType
        INGwSpSipProviderConfig::meSdpType              = SDPTYPE_DEFAULT;
INGwSpSipProviderConfig::FromHdrTreatment
        INGwSpSipProviderConfig::meFromHdrTreatment     = FROMHDR_FROMPROFILE;
INGwSpSipProviderConfig::ToHdrTreatment
        INGwSpSipProviderConfig::meToHdrTreatment       = TOHDR_FROMPROFILE;
INGwSpSipProviderConfig::ContactHdrTreatment
        INGwSpSipProviderConfig::meContactHdrTreatment  = CONTACTHDR_FROMPROFILE;
int     INGwSpSipProviderConfig::miNumOfGws             = 0;
//GwInfo* INGwSpSipProviderConfig::maGwInfo               = 0;
int     INGwSpSipProviderConfig::miActiveGw             = 0;
bool    INGwSpSipProviderConfig::mbRtpTunneling         = false;
INGwSpSipProviderConfig::HdrParamTreatment
        INGwSpSipProviderConfig::m_fmHdrParamTreatment  = HDR_COPY_ALLPARAM;
INGwSpSipProviderConfig::HdrParamTreatment
        INGwSpSipProviderConfig::m_toHdrParamTreatment  = HDR_COPY_ALLPARAM;
INGwSpSipProviderConfig::HdrParamTreatment
        INGwSpSipProviderConfig::m_reqLineParamTreatment= HDR_COPY_ALLPARAM;
int     INGwSpSipProviderConfig::miStackDebugLevel      = 0;
int     INGwSpSipProviderConfig::miMinSeHdrValue        = 1800;
int     INGwSpSipProviderConfig::miMaxSeHdrValue        = 1800;

char*   INGwSpSipProviderConfig::mpDummyUserForFromHdr  = 0;
int     INGwSpSipProviderConfig::miNoAnsTimer           = 0;
int     INGwSpSipProviderConfig::miNoAnsTimerReInv      = 40000;
bool    INGwSpSipProviderConfig::mbDontRetrans          = false;
bool    INGwSpSipProviderConfig::mByeAlsoXfer           = false;
bool    INGwSpSipProviderConfig::mSipRouteUseRedirect   = false;
bool    INGwSpSipProviderConfig::mbIsSdpSerializationReqd   = true;
int     INGwSpSipProviderConfig::miDefaultPrivacy      = 0;
int     INGwSpSipProviderConfig::miPrivacyProtocol      = 0;
int     INGwSpSipProviderConfig::miPrivacyMask      = 0;

std::set<int> INGwSpSipProviderConfig::mProcessedInformationalHeaders;
std::set<std::string> INGwSpSipProviderConfig::mUnprocessedInformationalHeaders;

const char * INGwSpSipProviderConfig::_areaAppender = "";

bool    INGwSpSipProviderConfig::m_isConvedia           = false;

bool    INGwSpSipProviderConfig::miSetOfferSdpInIvr = false;

bool    INGwSpSipProviderConfig::mbSendEmptyInvite = false;

bool    INGwSpSipProviderConfig::miUpdateIvrStat = false;

bool INGwSpSipProviderConfig::_sendBilling = false;
bool INGwSpSipProviderConfig::_sendReason = false;
bool INGwSpSipProviderConfig::_sendPhoneContext = false;
bool INGwSpSipProviderConfig::_sendBody = false;
bool INGwSpSipProviderConfig::_sendContact = false;
bool INGwSpSipProviderConfig::_sendAsserted = false;
bool INGwSpSipProviderConfig::_sendFromParam = false;
bool INGwSpSipProviderConfig::_sendToParam = false;
bool INGwSpSipProviderConfig::_sendReqURIParam = false;
bool INGwSpSipProviderConfig::_supportEarlyMedia = false;
bool INGwSpSipProviderConfig::_sendOLI = false;
bool INGwSpSipProviderConfig::_sendContactAccess = false;
bool INGwSpSipProviderConfig::_sendContactParams = false;

bool INGwSpSipProviderConfig::_processIncomingVia = true;

bool INGwSpSipProviderConfig::mIsUseContactForReqUri = false;


bool INGwSpSipProviderConfig::m_IsTransportUdp    = false;

RSI_NSP_SIP::INGwSpSipHeaderPolicy INGwSpSipProviderConfig::_headerPolicy;
RSI_NSP_SIP::INGwSpSipHeaderDefaultData INGwSpSipProviderConfig::_headerDefault;

string INGwSpSipProviderConfig::_selfIP;
int INGwSpSipProviderConfig::_selfPort = 5060;

GwInfo * INGwSpSipProviderConfig::mFwProxyInfo = NULL;

// BPUsa07885 : [
string INGwSpSipProviderConfig::_srvDomainName = "";
unsigned long INGwSpSipProviderConfig::_srvTTLValue = 0;
// : ]

// BPUsa07888 : [
bool INGwSpSipProviderConfig::_parseSIPTBody = false;
// : ]

//	BPUsa07893 : [
unsigned int INGwSpSipProviderConfig::_screenSipMessages = 0;
// : ]

// BPUsa07898 : [
bool INGwSpSipProviderConfig::_sdpFixEnabled = false;
// : ]

#define LOG_BOOL_CONFIG_PARAM(x, y) \
{ \
  char tmpstr[16]; \
  if(y) \
    strcpy(tmpstr, "ENABLED"); \
  else \
    strcpy(tmpstr, "DISABLED"); \
  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, "INGwSpSipProviderConfig::initialize: %s %s", x, tmpstr); \
}

INGwSpSipProviderConfig::INGwSpSipProviderConfig() {

   miInitialRespTimer     = 5000;
   miIvrRespTimeout       = 30000;

} 

void INGwSpSipProviderConfig::initialize()
{
  LogINGwTrace(false, 0, "IN initialize");
  string paramvalstr = "";

  //These params are configurable
  m_outGwLeakTime         = DEFAULT_LEAK_TIME; //seconds
  m_outGwFailureLeakCount = DEFAULT_LEAK_FAILURE_COUNT;
  m_psxAlarmInterval      = DEFAULT_PSX_ALARM_INTERVAL; //seconds

  //initialise psx lock mutex...............................
  if(0 > pthread_mutex_init(&m_activePsxLock, 0)) {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
      "pthread_mutex_init for m_activePsxLock failed ... ");
  }

  //Keeping as default
  m_vxmlReqUriTreatment = REQ_URI_VXML_INPARAM;

  if(m_vxmlReqUriTreatment == REQ_URI_VXML_INPARAM)
  {
     LogAlways(0, "VXML: Param based ReqUri treatment configured.");
  }
  else if(m_vxmlReqUriTreatment == REQ_URI_VXML_INUSER)
  {
     LogAlways(0, "VXML: User part based ReqUri treatment configured.");
  }
  else
  {
     LogError(0, "VXML: Some junk in ReqURI treatment correcting.");
     m_vxmlReqUriTreatment = REQ_URI_VXML_INPARAM;
     LogAlways(0, "VXML: Param based ReqUri treatment configured.");
  }

  paramvalstr = "";

	// Copying the value of Record-Route header in orig INVITE to Route header of Term INVITE
#ifndef sipCOPY_ORIG_RR
#define sipCOPY_ORIG_RR   "sipCOPY_ORIG_RR"
#endif

	{
		paramvalstr = "";
		miCopyOrigRR = 0;
		const char *l_copyOrigRR = getenv(sipCOPY_ORIG_RR);
		if( 0 != l_copyOrigRR){
			paramvalstr = l_copyOrigRR;

			if(!paramvalstr.empty())
			{
				miCopyOrigRR = atol(paramvalstr.c_str());
			}
		}
	}

#ifndef sipSESSION_EXPIRE_TIMEOUT
#define sipSESSION_EXPIRE_TIMEOUT		"sipSESSION_EXPIRE_TIMEOUT"
#endif
  //Min Se Val..............................................
  paramvalstr = "";
  string locParamName = sipSESSION_EXPIRE_TIMEOUT;
  //if(INGwIfrPrParamRepository::getInstance().getValue(locParamName, paramvalstr) != -1)
  if(false)
  {
     if(!paramvalstr.empty())
     {
        miMinSeHdrValue = atol(paramvalstr.c_str());
     }
  }

  glbMinSE = miMinSeHdrValue;
  logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                  "INGwSpSipProviderConfig::initialize: SessionTimer MinSe[%d]",
                  glbMinSE);

  //Max Se Val..............................................

#ifndef sipMAX_SESSION_EXPIRE_TIMEOUT
#define sipMAX_SESSION_EXPIRE_TIMEOUT		"sipMAX_SESSION_EXPIRE_TIMEOUT"
#endif

  paramvalstr = "";
	//locParamName = sipMAX_SESSION_EXPIRE_TIMEOUT;
	miMaxSeHdrValue = miMinSeHdrValue;
	//if(INGwIfrPrParamRepository::getInstance().getValue(locParamName, paramvalstr) != -1)
	const char *l_maxSETimeOut = getenv(sipMAX_SESSION_EXPIRE_TIMEOUT);
	if( 0 != l_maxSETimeOut){
		paramvalstr = l_maxSETimeOut;

		if(!paramvalstr.empty())
		{
			miMaxSeHdrValue = atol(paramvalstr.c_str());
		} 
	} 

	if( miMaxSeHdrValue <= miMinSeHdrValue){
		logger.logINGwMsg(false, ERROR_FLAG, 0,
				"initialize: SessionTimer MaxSe[%d] is <= MinSe[%d]. Therefore defaulting to MaxSe[%d]",
				miMaxSeHdrValue, miMinSeHdrValue, miMinSeHdrValue+20);

		miMaxSeHdrValue = miMinSeHdrValue + 20;
	} else {

		logger.logINGwMsg(false, ALWAYS_FLAG, 0,
				"initialize: SessionTimer MaxSe[%d]", miMaxSeHdrValue);
	}

  //checking for Session refresh Reinvite .............................
  paramvalstr = "";
  //INGwIfrPrParamRepository::getInstance().getValue(string(sipCHECK_ST_REINVITE), paramvalstr);
  if(!paramvalstr.empty()) {
    if(paramvalstr == "CHECK_SDP") {
      m_checkSessionRefreshReinvite = INGwSpSipProviderConfig::CHECK_SDP;
    }
    else if(paramvalstr == "CHECK_SDP_SE_HDR") {
      m_checkSessionRefreshReinvite = INGwSpSipProviderConfig::CHECK_SDP_SE_HDR;
    }
    else
      m_checkSessionRefreshReinvite = INGwSpSipProviderConfig::CHECK_SE_HDR;
  }
  {
    char *enumarray[3];
    enumarray[0] = (char *)"CHECK_SE_HDR";
    enumarray[1] = (char *)"CHECK_SDP";
    enumarray[2] = (char *)"CHECK_SDP_SE_HDR";
    logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
										 "INGwSpSipProviderConfig::initialize: "
										 "check sesstimer reinv <%s>", 
										 enumarray[m_checkSessionRefreshReinvite]);
  }

  // Debug level
  paramvalstr = "";
  INGwIfrPrParamRepository::getInstance().getValue(
														string(ingwSIP_STACK_DEBUG_LEVEL), paramvalstr);
  setStackDebugLevel(atoi(paramvalstr.c_str()));
  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
										"INGwSpSipProviderConfig::initialize: "
										"sipSTACK_DEBUG_LEVEL <%d>", getStackDebugLevel());

  // Is SDP parsed
  paramvalstr = "";
  //INGwIfrPrParamRepository::getInstance().getValue(string(sipISSDPPARSED), paramvalstr);
  if(paramvalstr == "ENABLED")
    setSdpParsed(true);
  else
    setSdpParsed(false);
  LOG_BOOL_CONFIG_PARAM("is sdp parsed", isSdpParsed())


  // SDP to be sent to IVR in INVITE
  const char *lofferSdpIvr = getenv ("SET_OFFER_SDP_IN_IVR_INVITE");

  miSetOfferSdpInIvr = false;
  if (lofferSdpIvr != NULL)
  {
    if (strncmp (lofferSdpIvr, "true", 4) == 0)
      miSetOfferSdpInIvr = true;
  }
  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
									 "INGwSpSipProviderConfig::initialize: "
									 "SET_OFFER_SDP_IN_IVR_INVITE <%d>", 
									 offerSdpInIvrEnabled());

  //Send Empty Invite
  mbSendEmptyInvite = ( NULL!=getenv("SIP_SEND_EMPTY_INVITE") ) ? true : false;
  if(mbSendEmptyInvite)
  	logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
											"INGwSpSipProviderConfig::initialize: "
											"SIP_SEND_EMPTY_INVITE <true>");
  else
  	logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
											"INGwSpSipProviderConfig::initialize: "
											"SIP_SEND_EMPTY_INVITE <false>");

#ifndef sipFROMHDR_TREATMENT
#define sipFROMHDR_TREATMENT		"sipFROMHDR_TREATMENT"
#endif

  // From header treatment
  paramvalstr = "";
  //INGwIfrPrParamRepository::getInstance().getValue(sipFROMHDR_TREATMENT, paramvalstr);
  if(!strcmp(paramvalstr.c_str(), "FROMHDR_COPYUSER"))
    setFromHdrTreatment(FROMHDR_COPYUSER);
  else if(!strcmp(paramvalstr.c_str(), "FROMHDR_COPYHDR"))
    setFromHdrTreatment(FROMHDR_COPYHDR);
  else
    setFromHdrTreatment(FROMHDR_FROMPROFILE);
  {
    char *enumarray[3];
    enumarray[0] = (char *)"FROMHDR_FROMPROFILE";
    enumarray[1] = (char *)"FROMHDR_COPYUSER";
    enumarray[2] = (char *)"FROMHDR_COPYHDR";
    logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
											"INGwSpSipProviderConfig::initialize: "
											"from header treatment <%s>", enumarray[getFromHdrTreatment()]);
  }

#ifndef sipTOHDR_TREATMENT
#define sipTOHDR_TREATMENT		"sipTOHDR_TREATMENT"
#endif
  // To header treatment
  paramvalstr = "";
  //INGwIfrPrParamRepository::getInstance().getValue(sipTOHDR_TREATMENT, paramvalstr);
  if(!strcmp(paramvalstr.c_str(), "TOHDR_LOOPBACK_DIAL"))
    setToHdrTreatment(TOHDR_LOOPBACK_DIAL);
  else if(!strcmp(paramvalstr.c_str(), "TOHDR_LOOPBACK_TARGET"))
    setToHdrTreatment(TOHDR_LOOPBACK_TARGET);
  else if(!strcmp(paramvalstr.c_str(), "TOHDR_DEFAULT_DIAL"))
    setToHdrTreatment(TOHDR_DEFAULT_DIAL);
  else if(!strcmp(paramvalstr.c_str(), "TOHDR_DEFAULT_TARGET"))
    setToHdrTreatment(TOHDR_DEFAULT_TARGET);
  else if(!strcmp(paramvalstr.c_str(), "TOHDR_COPYHDR"))
    setToHdrTreatment(TOHDR_COPYHDR);
  else
    setToHdrTreatment(TOHDR_FROMPROFILE);

  {
    char *enumarray[TOHDR_LOOPBACK_TARGET+1];
    enumarray[TOHDR_FROMPROFILE]     = (char *)"TOHDR_FROMPROFILE";
    enumarray[TOHDR_COPYHDR]         = (char *)"TOHDR_COPYHDR";
    enumarray[TOHDR_DEFAULT_DIAL]    = (char *)"TOHDR_DEFAULT_DIAL";
    enumarray[TOHDR_DEFAULT_TARGET]  = (char *)"TOHDR_DEFAULT_TARGET";
    enumarray[TOHDR_LOOPBACK_DIAL]   = (char *)"TOHDR_LOOPBACK_DIAL";
    enumarray[TOHDR_LOOPBACK_TARGET] = (char *)"TOHDR_LOOPBACK_TARGET";
    logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
                    "INGwSpSipProviderConfig::initialize: to header treatment <%s>",
                    enumarray[getToHdrTreatment()]);
  }

#ifndef sipCONTACTHDR_TREATMENT
#define sipCONTACTHDR_TREATMENT		"sipCONTACTHDR_TREATMENT"
#endif
  // Contact header treatment
  paramvalstr = "";
  //INGwIfrPrParamRepository::getInstance().getValue(sipCONTACTHDR_TREATMENT, paramvalstr);
  if(!strcmp(paramvalstr.c_str(), "CONTACTHDR_PEERDN"))
    setContactHdrTreatment(CONTACTHDR_PEERDN);
  else
    setContactHdrTreatment(CONTACTHDR_FROMPROFILE);
  {
    char *enumarray[2];
    enumarray[0] = (char *)"CONTACTHDR_FROMPROFILE";
    enumarray[1] = (char *)"CONTACTHDR_PEERDN";
    logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, "INGwSpSipProviderConfig::initialize: contact header treatment <%s>", enumarray[getContactHdrTreatment()]);
  }

  // RTP Tunnelling
  paramvalstr = "";
  //INGwIfrPrParamRepository::getInstance().getValue(sipRTP_TUNNELING, paramvalstr);
  logger.logINGwMsg(false, ALWAYS_FLAG, 0, "sipRTP_TUNNELING [%s]", 
                  paramvalstr.c_str());
  if(paramvalstr == "ENABLED")
    setRtpTunneling(true);
  else
    setRtpTunneling(false);
  LOG_BOOL_CONFIG_PARAM("rtp tunneling", getRtpTunneling())

  char *paramTreatmentEnumArray[4];
  paramTreatmentEnumArray[0] = (char*)"HDR_COPY_ALLPARAM";
  paramTreatmentEnumArray[1] = (char*)"HDR_COPY_URLPARAM";
  paramTreatmentEnumArray[2] = (char*)"HDR_COPY_HDRPARAM";
  paramTreatmentEnumArray[3] = (char*)"HDR_COPY_NOPARAM";

#ifndef sipFROMHDR_PARAM_TREATMENT
#define sipFROMHDR_PARAM_TREATMENT		"sipFROMHDR_PARAM_TREATMENT"
#endif
  // From header param treatment
  paramvalstr = "";
  //INGwIfrPrParamRepository::getInstance().
  //  getValue(sipFROMHDR_PARAM_TREATMENT, paramvalstr);
  if(!strcmp(paramvalstr.c_str(), "HDR_COPY_NOPARAM"))
    setFromHdrParamTreatment(HDR_COPY_NOPARAM);
  else if(!strcmp(paramvalstr.c_str(), "HDR_COPY_URLPARAM"))
    setFromHdrParamTreatment(HDR_COPY_URLPARAM); 
  else if(!strcmp(paramvalstr.c_str(), "HDR_COPY_HDRPARAM"))
    setFromHdrParamTreatment(HDR_COPY_HDRPARAM);
  else
    setFromHdrParamTreatment(HDR_COPY_ALLPARAM);
  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, "INGwSpSipProviderConfig::initialize: "
										"from header param treatment <%s>", 
										paramTreatmentEnumArray[getFromHdrParamTreatment()]);

#ifndef sipTOHDR_PARAM_TREATMENT
#define sipTOHDR_PARAM_TREATMENT		"sipTOHDR_PARAM_TREATMENT"
#endif
  // To header param treatment
  paramvalstr = "";
  //INGwIfrPrParamRepository::getInstance().getValue(sipTOHDR_PARAM_TREATMENT, paramvalstr);
  if(!strcmp(paramvalstr.c_str(), "HDR_COPY_NOPARAM"))
    setToHdrParamTreatment(HDR_COPY_NOPARAM);
  else if(!strcmp(paramvalstr.c_str(), "HDR_COPY_URLPARAM"))
    setToHdrParamTreatment(HDR_COPY_URLPARAM); 
  else if(!strcmp(paramvalstr.c_str(), "HDR_COPY_HDRPARAM"))
    setToHdrParamTreatment(HDR_COPY_HDRPARAM);
  else
    setToHdrParamTreatment(HDR_COPY_ALLPARAM);
  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, "INGwSpSipProviderConfig::initialize: to header param treatment <%s>", paramTreatmentEnumArray[getToHdrParamTreatment()]);

#ifndef sipREQLINE_PARAM_TREATMENT
#define sipREQLINE_PARAM_TREATMENT		"sipREQLINE_PARAM_TREATMENT"
#endif
  // Reqline header param treatment
  paramvalstr = "";
  //INGwIfrPrParamRepository::getInstance().getValue(sipREQLINE_PARAM_TREATMENT, paramvalstr);
  if(!strcmp(paramvalstr.c_str(), "HDR_COPY_NOPARAM"))
    setReqLineParamTreatment(HDR_COPY_NOPARAM);
  else if(!strcmp(paramvalstr.c_str(), "HDR_COPY_URLPARAM"))
    setReqLineParamTreatment(HDR_COPY_URLPARAM); 
  else if(!strcmp(paramvalstr.c_str(), "HDR_COPY_HDRPARAM"))
    setReqLineParamTreatment(HDR_COPY_HDRPARAM);
  else
    setReqLineParamTreatment(HDR_COPY_ALLPARAM);
  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
										"INGwSpSipProviderConfig::initialize: "
										"request line param treatment <%s>", 
										paramTreatmentEnumArray[getReqLineParamTreatment()]);

  // No answer timer
  setNoAnsTimer(120000);
  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, 
										"INGwSpSipProviderConfig::initialize: no answer timer <%d>", 
										getNoAnsTimer());

#ifndef sipHDR_COPY
#define sipHDR_COPY		"sipHDR_COPY"
#endif
  // Header copy list
  paramvalstr = "";
  //INGwIfrPrParamRepository::getInstance().getValue(sipHDR_COPY, paramvalstr);
  if(!paramvalstr.empty())
  {
    INGwSpSipUtil::buildHeaderCopyProfile(paramvalstr.c_str());
  }
  
  // Initialize Outbound Gateway list......................................
  miNumOfGws = initObGwList();

  paramvalstr = "";

  const char *lpcIsSdpSerReqd = getenv ("INGW_IS_SDP_SERIALIZATION_REQD");

  mbIsSdpSerializationReqd = true;
  if (lpcIsSdpSerReqd)
  {
    if (strncmp (lpcIsSdpSerReqd, "true", 4) != 0)
      mbIsSdpSerializationReqd = false;
  }

  const char *lpcPrivacyProtocol = getenv ("SIP_PRIVACY_PROTOCOL");

  int liPrivacyProtocol = SIP_RPID;
  if (lpcPrivacyProtocol)
  {
    if (strncasecmp (lpcPrivacyProtocol, "rfc3323", 7) == 0)
    {
      liPrivacyProtocol = SIP_RFC3323;
    }
    else if (strncasecmp (lpcPrivacyProtocol, "rpid", 4) == 0)
    {
      liPrivacyProtocol = SIP_RPID;
    }
  }

  setPrivacyProtocol (liPrivacyProtocol);

  const char *lpcPrivacyMask =  getenv ("SIP_PRIVACY_GW_CAPABILITY");

  int liPrivacyMask = SIP_PRIVACY_ABSENT;
  if (lpcPrivacyMask)
  {
    char *tok, *ch;
    tok = strtok_r ((char*)lpcPrivacyMask, ",\n", &ch);
    while (tok)
    {
      if (strncasecmp (tok, "NONE", 4) == 0)
        liPrivacyMask |= SIP_PRIVACY_NONE;
      else if (strncasecmp (tok, "SESSION", 7) == 0)
        liPrivacyMask |= SIP_PRIVACY_SESSION;
      else if (strncasecmp (tok, "HEADER", 6) == 0)
        liPrivacyMask |= SIP_PRIVACY_HEADER;
      else if (strncasecmp (tok, "USER", 4) == 0)
        liPrivacyMask |= SIP_PRIVACY_USER;
      else if (strncasecmp (tok, "ID", 2) == 0)
        liPrivacyMask |= SIP_PRIVACY_ID;
      else if (strncasecmp (tok, "CRITICAL", 8) == 0)
        liPrivacyMask |= SIP_PRIVACY_CRITICAL;

      tok = strtok_r (0, ",\n", &ch);
    }
  }

  logger.logINGwMsg (false, VERBOSE_FLAG, 0,
    "The privacy mask set via configuration for passthrough <%X>",
    liPrivacyMask);

  setPrivacyMask (liPrivacyMask);

  const char *lpcDefaultPrivacy =  getenv ("SIP_PRIVACY_DEFAULT_REQ");

  int liDefaultPrivacy = SIP_PRIVACY_ABSENT;
  if (lpcDefaultPrivacy)
  {
    char *tok, *ch;
    tok = strtok_r ((char*)lpcDefaultPrivacy, ",\n", &ch);
    while (tok)
    {
      if (strncasecmp (tok, "NONE", 4) == 0)
        liDefaultPrivacy |= SIP_PRIVACY_NONE;
      else if (strncasecmp (tok, "SESSION", 7) == 0)
        liDefaultPrivacy |= SIP_PRIVACY_SESSION;
      else if (strncasecmp (tok, "HEADER", 6) == 0)
        liDefaultPrivacy |= SIP_PRIVACY_HEADER;
      else if (strncasecmp (tok, "USER", 4) == 0)
        liDefaultPrivacy |= SIP_PRIVACY_USER;
      else if (strncasecmp (tok, "ID", 2) == 0)
        liDefaultPrivacy |= SIP_PRIVACY_ID;
      else if (strncasecmp (tok, "CRITICAL", 8) == 0)
        liDefaultPrivacy |= SIP_PRIVACY_CRITICAL;

      tok = strtok_r (0, ",\n", &ch);
    }
  }

  logger.logINGwMsg (false, VERBOSE_FLAG, 0,
    "The default privacy <%X>", liDefaultPrivacy);

  setDefaultPrivacy (liDefaultPrivacy);


  const char *lpcInfoHdr = getenv ("SIP_PRIVACY_INFORMATIONAL_HEADERS");

  if (!lpcInfoHdr)
    lpcInfoHdr = "Call-Info,User-Agent,Organization,Server,Subject,In-Reply-To,Warning";

  parseInformationalHeaders (lpcInfoHdr);

  _areaAppender = getenv("SIP_PRIVACY_TEL_AREA_SPEC");

  if(_areaAppender == NULL)
  {
     _areaAppender = "";
  }





      paramvalstr = "";

   {
#ifndef sipProcessIncomingVia
#define sipProcessIncomingVia   "sipProcessIncomingVia"
#endif
      //Header parsing vars.
      INGwIfrPrParamRepository &repo = INGwIfrPrParamRepository::getInstance();
      //repo.getValue(sipProcessIncomingVia, paramvalstr);
      const char *tempVal = paramvalstr.c_str();
 
      if(tempVal && (atoi(tempVal) > 0))
      {
         _processIncomingVia = true;
      }
 
   }

   _loadHeaderPolicy();
   _loadHeaderDefaults();

   {
      INGwIfrPrParamRepository::getInstance().getValue(ingwFLOATING_IP_ADDR, _selfIP);

      string portStr;

      INGwIfrPrParamRepository::getInstance().getValue(ingwSTACK_IP_PORT_LIST, 
                                                portStr);

      if(atoi(portStr.c_str()) > 0)
      {
         _selfPort = atoi(portStr.c_str());
      }
   }

#ifndef sipSCREEN_SIP_MESSAGES
#define sipSCREEN_SIP_MESSAGES   "sipSCREEN_SIP_MESSAGES"
#endif

	{
		paramvalstr = "";
		_screenSipMessages = 0;
		const char *l_screenSipMsg = getenv(sipSCREEN_SIP_MESSAGES);
		if( 0 != l_screenSipMsg){
			paramvalstr = l_screenSipMsg;

			if(!paramvalstr.empty())
			{
				_screenSipMessages = atol(paramvalstr.c_str());

				if( _screenSipMessages )
				{
					if (_screenSipMessages & SCREEN_OUTGOING)
						logger.logINGwMsg (false, ALWAYS_FLAG, 0, 
															 "Screening outgoing SIP messages is enabled.");

					if (_screenSipMessages & SCREEN_INCOMING)
						logger.logINGwMsg (false, ALWAYS_FLAG, 0, 
															 "Screening incoming SIP messages is enabled.");
				} 
				else
					logger.logINGwMsg (false, ALWAYS_FLAG, 0, "Screening SIP messages is disabled.");
			}
		}
	}

#ifndef sipSDP_FIX_ENABLED
#define sipSDP_FIX_ENABLED   "sipSDP_FIX_ENABLED"
#endif

	{
		paramvalstr = "";
		_sdpFixEnabled = false;
		const char *l_sdpFixEnabled = getenv(sipSDP_FIX_ENABLED);
		if( 0 != l_sdpFixEnabled){
			paramvalstr = l_sdpFixEnabled;

			if(!paramvalstr.empty())
			{
				_sdpFixEnabled = atol(paramvalstr.c_str());

				if( _sdpFixEnabled )
					logger.logINGwMsg (false, VERBOSE_FLAG, 0, "SDP fixing is enabled.");
				else
					logger.logINGwMsg (false, VERBOSE_FLAG, 0, "SDP fixing is disabled.");
			}
		}
		else
			logger.logINGwMsg (false, VERBOSE_FLAG, 0, "SDP fixing is disabled.");

	}

  const char* lUseContactFlag = getenv("COPY_CONTACT_TO_REQ_URI");

  if(lUseContactFlag != NULL)
  {
    if((strncmp (lUseContactFlag, "true", 4) == 0))
    {
      mIsUseContactForReqUri = true;
    }
  }

  // TRANSPORT to be used
  string transpStr;

  if(-1 == INGwIfrPrParamRepository::getInstance().getValue(ingwSIP_TransportType, 
                                                              transpStr))
  {
    const char *lTransTypeStr = getenv ("SIP_TRANSPORT_TYPE");

    if (lTransTypeStr != NULL)
    {
      if (strncmp (lTransTypeStr, "UDP", 3) == 0)
        m_IsTransportUdp = true;
    }
  }
  else
  {
    if(strncmp (transpStr.c_str(), "UDP", 3) == 0)
				m_IsTransportUdp = true;
	}

  logger.logINGwMsg(false, ALWAYS_FLAG, 0,
                   "SIP Transport type : <%s>",
                   ( m_IsTransportUdp == true ? "UDP" : "TCP" ));

   LogINGwTrace(false, 0, "OUT initialize");
} 

void INGwSpSipProviderConfig::_parseFwProxy(string fwProxy){

	logger.logMsg(TRACE_FLAG, 0, "Parsing Firewall Proxy [%s]", 
			fwProxy.c_str());

	StrVector ipPort;
	string ipDelimiter("|");
	INGwAlgTokenizer(fwProxy, ipDelimiter, back_inserter(ipPort));

	if((ipPort.size() > 2) || (ipPort.size() == 0))
	{
		logger.logMsg(ALWAYS_FLAG, 0, "Invalid endPoint [%s]", 
				fwProxy.c_str());
		mFwProxyInfo = NULL;
		return;
	}

	string dummyIP("0.0.0.0");

	if( dummyIP == ipPort[0]){

		logger.logMsg(ALWAYS_FLAG, 0, "Dummy endPoint [%s]. IP Validation "
				"failed. [%s]", fwProxy.c_str(), ipPort[0].c_str());
		mFwProxyInfo = NULL;
		return;
	}

	int ip = inet_addr(ipPort[0].c_str());

	if(ip == -1)
	{
		logger.logMsg(ALWAYS_FLAG, 0, "Invalid endPoint [%s]. IP Validation "
				"failed. [%s]", fwProxy.c_str(), ipPort[0].c_str());
		mFwProxyInfo = NULL;
		return;
	}

	int port = 5060;

	if(ipPort.size() == 2)
	{
		port = atoi(ipPort[1].c_str());
	}

	if((port < 0) || (port > 65535))
	{
		logger.logMsg(ALWAYS_FLAG, 0, "Invalid endPoint [%s]. Port Validation "
				"failed. [%s]", 
				fwProxy.c_str(), ipPort[1].c_str());
		mFwProxyInfo = NULL;
		return;
	}

	mFwProxyInfo = new GwInfo;
	mFwProxyInfo->mpHost = ipPort[0].c_str();
	mFwProxyInfo->miPort = port;
	mFwProxyInfo->m_failureCount = 0;
	mFwProxyInfo->m_failureTime = 0;

}

const GwInfo* INGwSpSipProviderConfig::getFirewallProxyInfo(){

	return mFwProxyInfo;
}

bool INGwSpSipProviderConfig::isSdpParsed()
{ return mbIsSdpParsed; }
void INGwSpSipProviderConfig::setSdpParsed(bool aParsed)
{ mbIsSdpParsed = aParsed; }

bool INGwSpSipProviderConfig::dropExtraneousCalls()
{ return mbDropExtraneousCalls; }
void INGwSpSipProviderConfig::setDropExtraneousCalls(bool aDrop)
{ mbDropExtraneousCalls = aDrop; }

INGwSpSipProviderConfig::SdpType INGwSpSipProviderConfig::getOutgoingSdpType()
{ return meSdpType; }
void INGwSpSipProviderConfig::setOutgoingSdpType(INGwSpSipProviderConfig::SdpType aType)
{ meSdpType = aType; }

INGwSpSipProviderConfig::FromHdrTreatment INGwSpSipProviderConfig::getFromHdrTreatment()
{ return meFromHdrTreatment; }
void INGwSpSipProviderConfig::setFromHdrTreatment(INGwSpSipProviderConfig::FromHdrTreatment aTreatment)
{ meFromHdrTreatment = aTreatment; }

INGwSpSipProviderConfig::ToHdrTreatment INGwSpSipProviderConfig::getToHdrTreatment()
{ return meToHdrTreatment; }
void INGwSpSipProviderConfig::setToHdrTreatment(INGwSpSipProviderConfig::ToHdrTreatment aTreatment)
{ meToHdrTreatment = aTreatment; }

INGwSpSipProviderConfig::ContactHdrTreatment
   INGwSpSipProviderConfig::getContactHdrTreatment()
     { return meContactHdrTreatment; }
void INGwSpSipProviderConfig::setContactHdrTreatment
  (INGwSpSipProviderConfig::ContactHdrTreatment aTreatment)
     { meContactHdrTreatment = aTreatment; }

int INGwSpSipProviderConfig::getInitialRespTimer()
{ return miInitialRespTimer; }
void INGwSpSipProviderConfig::setInitialRespTimer(int aTimer)
{ 
   miInitialRespTimer = aTimer; 

   logger.logMsg(ALWAYS_FLAG, 0, "InitRespTimer [%d] loaded", 
                 miInitialRespTimer);
}

INGwSpSipProviderConfig::HdrParamTreatment
INGwSpSipProviderConfig::getToHdrParamTreatment()
{ return m_toHdrParamTreatment; }
void INGwSpSipProviderConfig::setToHdrParamTreatment(INGwSpSipProviderConfig::HdrParamTreatment aTreatment)
{ m_toHdrParamTreatment = aTreatment; }

INGwSpSipProviderConfig::HdrParamTreatment
INGwSpSipProviderConfig::getFromHdrParamTreatment()
{ return m_fmHdrParamTreatment; }
void INGwSpSipProviderConfig::setFromHdrParamTreatment(INGwSpSipProviderConfig::HdrParamTreatment aTreatment)
{ m_fmHdrParamTreatment = aTreatment; }

INGwSpSipProviderConfig::HdrParamTreatment
INGwSpSipProviderConfig::getReqLineParamTreatment()
{ return m_reqLineParamTreatment; }
void INGwSpSipProviderConfig::setReqLineParamTreatment(INGwSpSipProviderConfig::HdrParamTreatment aTreatment)
{ m_reqLineParamTreatment = aTreatment; }

void INGwSpSipProviderConfig::setRtpTunneling(bool aTunnel)
{ mbRtpTunneling = aTunnel; }
bool INGwSpSipProviderConfig::getRtpTunneling()
{ return mbRtpTunneling; }

int  INGwSpSipProviderConfig::getIvrRespTimeout()
{
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "INGwSpSipProviderConfig::getIvrRespTimeout: Returning <%d>", miIvrRespTimeout);
  return miIvrRespTimeout;
}
void INGwSpSipProviderConfig::setIvrRespTimeout(int aTimer)
{
   if(aTimer < 30000)
   {
      aTimer = 30000;
   }

   miIvrRespTimeout = aTimer;
   logger.logINGwMsg(false, ALWAYS_FLAG, 0, 
                   "INGwSpSipProviderConfig::setIvrRespTimeout: Setting [%d]", 
                   miIvrRespTimeout);
}

int  INGwSpSipProviderConfig::getStackDebugLevel()
{
  return miStackDebugLevel;
}
void INGwSpSipProviderConfig::setStackDebugLevel(int aLevel)
{
  miStackDebugLevel = aLevel;
  logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE, "INGwSpSipProviderConfig::setStackDebugLevel: <%d>", miStackDebugLevel);

  if(aLevel == 0)
  {
     sipMsgStream->setLoggable(false);
  }
  else
  {
     sipMsgStream->setLoggable(true);
  }
}

int
INGwSpSipProviderConfig::getMinSeValue() {

  return miMinSeHdrValue;
}

int
INGwSpSipProviderConfig::getMaxSeValue() {

  return miMaxSeHdrValue;
}

char*
INGwSpSipProviderConfig::getDummyUserForFromHdr() {

  return mpDummyUserForFromHdr;
}

/*---------------------------------------------------------
 |
 | Function: markOutboundGwFailed
 |
 | 
 |---------------------------------------------------------
 */
void INGwSpSipProviderConfig::markOutboundGwFailed(int psxId) {
  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN markOutboundGwFailed");

  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, "failed psxId:<%d>", psxId);

  pthread_mutex_lock(&m_activePsxLock);

  if(psxId != m_activePsx) {
     int l_activePsx = m_activePsx;
     pthread_mutex_unlock(&m_activePsxLock);
     
     // Non Active Psx failure will be used once the rating 
     // (non active failure counts) for the Psx is introduced. 
     // This will allow to make a intelligent switch to the 
     // next active Psx once the current Psx fails.
     // The switch will be based on the rating.

     logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
       "Psx failure for non-active psx, ignored. active_psx:<%d>", l_activePsx);
     logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT markOutboundGwFailed");
     return;
  }
  else {
    bool l_psxFail = false;
    // Determine from leaky bucket that active
    // psx has to be changed or not.
    
    // Get the GwInfo Struct for failed psx
    GwInfo* l_gwInfo = m_obGwList[psxId];

    // Increment failure count
    l_gwInfo->m_failureCount += 1;

    // get time of day
    struct timeval l_failTime;
    gettimeofday(&l_failTime, 0);

    // find the time elapsed since last bucket
    unsigned long l_deltaFailTime = 
                  l_failTime.tv_sec - l_gwInfo->m_failureTime;

    // if time < max_time && count == max_failure
    // mark the psx as failed and move over to the 
    // next psx
    if(l_deltaFailTime > INGwSpSipProviderConfig::m_outGwLeakTime) {
      // The leak time has expired.
      // Reset the failure/time information
      
      l_gwInfo->m_failureCount = 1;
  
      logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE,
        "FAIL_TIME:<%u> - BASE_TIME:<%u> greater than LEAK_TIME:<%u>",
        l_failTime.tv_sec, l_gwInfo->m_failureTime, INGwSpSipProviderConfig::m_outGwLeakTime);

      l_gwInfo->m_failureTime = l_failTime.tv_sec;
    }
    else {
      if(l_gwInfo->m_failureCount >= INGwSpSipProviderConfig::m_outGwFailureLeakCount) {
        l_psxFail = true;

        logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE,
          "FAILURE_COUNT:<%u>, LEAK_COUNT:<%u>, DELTA_TIME:<%u>, LEAK_TIME:<%u> \
          PSX FAILURE CONDITIONS MET",
          l_gwInfo->m_failureCount, INGwSpSipProviderConfig::m_outGwFailureLeakCount,
          l_deltaFailTime, INGwSpSipProviderConfig::m_outGwLeakTime);

        // Reset the structures.
        l_gwInfo->m_failureCount = 0;
        l_gwInfo->m_failureTime = l_failTime.tv_sec;
      }
      else {
        logger.logINGwMsg(false, ALWAYS_FLAG, imERR_NONE,
          "FAILURE_COUNT:<%u> less than LEAK_COUNT:<%u>",
          l_gwInfo->m_failureCount, INGwSpSipProviderConfig::m_outGwFailureLeakCount);
      }
    }

    if(l_psxFail) {
      if(miNumOfGws == m_activePsx) {
        m_activePsx = 0;
      }
      else {
        ++m_activePsx;
      }
      updatePsxFailStats(psxId, m_activePsx, l_deltaFailTime);
    }
  }

  pthread_mutex_unlock(&m_activePsxLock);

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT markOutboundGwFailed");
}

/*---------------------------------------------------------
 |
 | Function: initObGwList
 |
 | 
 |---------------------------------------------------------
 */

int INGwSpSipProviderConfig::initObGwList() 
{
   logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN initObGwList");

   int l_noOfObGw = 0;

   string l_psxList;
#ifndef sipOUTBOUND_GATEWAY_LIST
#define sipOUTBOUND_GATEWAY_LIST		"sipOUTBOUND_GATEWAY_LIST"
#endif
   //INGwIfrPrParamRepository::getInstance().getValue(sipOUTBOUND_GATEWAY_LIST, 
   //                                          l_psxList);

   if(l_psxList.empty()) 
   {
      logger.logINGwMsg(false, TRACE_FLAG, 0, "Null sipOUTBOUND_GATEWAY_LIST");
      logger.logINGwMsg(false, TRACE_FLAG, 0, "OUT initObGwList");
      return l_noOfObGw;
   }

   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "Received outbound GwList [%s]", 
                   l_psxList.c_str());

   StrVector endPoints;
   string endPointDelimiter(",");

   INGwAlgTokenizer(l_psxList, endPointDelimiter, back_inserter(endPoints));

   struct timeval l_failTime;
   gettimeofday(&l_failTime, 0);

   for(StrVectorIt eIt = endPoints.begin(); eIt != endPoints.end(); eIt++)
   {
      string &currEndPoint = (*eIt);

      logger.logMsg(TRACE_FLAG, 0, "Parsing endpoint [%s]", 
                    currEndPoint.c_str());

      StrVector ipPort;
      string ipDelimiter("|");
      INGwAlgTokenizer(currEndPoint, ipDelimiter, back_inserter(ipPort));

      if((ipPort.size() > 2) || (ipPort.size() == 0))
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Invalid endPoint [%s]", 
                       currEndPoint.c_str());
         continue;
      }

      /** BPUsa07421 : 
			// Mriganka - This has been commented for Siemens Parlay Demo
			// For 3GPP compliant(FQDN instead of only IP address), this has been commented
			int ip = inet_addr(ipPort[0].c_str());

      if(ip == -1)
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Invalid endPoint [%s]. IP Validation "
                                       "failed. [%s]", 
                       currEndPoint.c_str(), ipPort[0].c_str());
         continue;
      }
			**/

      int port = 5060;

      if(ipPort.size() == 2)
      {
         port = atoi(ipPort[1].c_str());
      }

      if((port < 0) || (port > 65535))
      {
         logger.logMsg(ALWAYS_FLAG, 0, "Invalid endPoint [%s]. Port Validation "
                                       "failed. [%s]", 
                       currEndPoint.c_str(), ipPort[1].c_str());
         continue;
      }

      GwInfo* l_gwInfo = new GwInfo;
      l_gwInfo->mpHost = ipPort[0].c_str();
      l_gwInfo->miPort = port;
      l_gwInfo->m_failureCount = 0;
      l_gwInfo->m_failureTime = l_failTime.tv_sec;
      m_obGwList.push_back(l_gwInfo);
   }

   VecGwInfo::iterator l_st = m_obGwList.begin();
   VecGwInfo::iterator l_en = m_obGwList.end();
   VecGwInfo::iterator l_cur;
   int l_count = 1;
   logger.logINGwMsg(false, VERBOSE_FLAG, 0, "*** CONFIG OB_GW IP, PORT ***");

   for(l_cur = l_st; l_cur != l_en; ++l_cur, ++l_count) 
   {
      logger.logINGwMsg(false, ERROR_FLAG, 0, "<%d>. OB_GW IP:<%s> PORT:<%d>", 
                      l_count, ((*l_cur)->mpHost).c_str(), (*l_cur)->miPort);

      //cout << "OB_GW IP:<" << ((*l_cur)->mpHost).c_str() << ">" << "PORT:<" 
      //     << (*l_cur)->miPort << ">" << endl;
   }

   logger.logINGwMsg(false, ALWAYS_FLAG, 0, "*** CONFIG OB_GW IP, PORT END ***");

   l_noOfObGw = m_obGwList.size() - 1;

   if(0 <= l_noOfObGw) 
   {
      m_activePsx = 0;
   }

   logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT initObGwList");
   return l_noOfObGw;
}

/*---------------------------------------------------------
 |
 | Function: getOutboundGw
 |
 | 
 |---------------------------------------------------------
 */
void              
INGwSpSipProviderConfig::getOutboundGw(int& port, 
  char** ip, 
  int& psxId) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN getOutboundGw");

  if(-1 == psxId) {
    //return the current Gw 
    psxId = m_activePsx;
  }
  else if(psxId < miNumOfGws) {
    //Non weighed logic of selecting the next psx
    psxId += 1;
  }
  else if(psxId == miNumOfGws) {
    /*******************************
    //Wrap around, return the 1st Gw
    psxId = 0;
    *******************************/
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE,
      "All PSX's tried. No more left");
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getOutboundGw");
    return;
  }
  else {
    logger.logINGwMsg(false, ERROR_FLAG, imERR_NONE, 
      "psxId passed is Invalid:<%d>, cannor process", psxId);
    logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getOutboundGw");
    return;
  }

  GwInfo* l_gwInfo = m_obGwList[psxId];
  *ip = (char*)(l_gwInfo->mpHost).c_str();
  port = l_gwInfo->miPort;

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT getOutboundGw");
}

/*---------------------------------------------------------
 |
 | Function: updatePsxFailStats
 |
 | 
 |---------------------------------------------------------
 */
void              
INGwSpSipProviderConfig::updatePsxFailStats(int failedPsx, 
  int newPsx, unsigned long deltaFailTime) {

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "IN updatePsxFailStats");
  logger.logINGwMsg(false, VERBOSE_FLAG, imERR_NONE, 
    "FAILED_PSX:<%d>, NEW_ACTIVE_PSX:<%d>", failedPsx, newPsx);

  ++m_psxFailCounter;
  GwInfo* l_failGwInfo = m_obGwList[failedPsx];
  GwInfo* l_newGwInfo  = m_obGwList[newPsx];

  char l_buf[250];
  sprintf(l_buf, " OB_GW_FAIL:<%s>, Time from last fail:<%u> sec, \
    Total Fails since start:<%u>, NEW_OB_GW:<%s>", 
    (l_failGwInfo->mpHost).c_str(), deltaFailTime, m_psxFailCounter, 
    (l_newGwInfo->mpHost).c_str());

  if(INGwSpSipProviderConfig::m_psxAlarmInterval < deltaFailTime) {
    INGwIfrMgrAlarmMgr::getInstance().logAlarm(INGwIfrMgrAlarmMgr::CONFIGURATION, 
                                          __FILE__, __LINE__, 101, "SIP", 0, 
                                          l_buf);
  }
  else {
    logger.logINGwMsg(false, ERROR_FLAG, 101, l_buf);
  }

  logger.logINGwMsg(false, TRACE_FLAG, imERR_NONE, "OUT updatePsxFailStats");
}

bool INGwSpSipProviderConfig::getDontRetrans()
{
  return mbDontRetrans;
}

void INGwSpSipProviderConfig::setDontRetrans(bool aDontRetrans)
{
  mbDontRetrans = aDontRetrans;
}

int INGwSpSipProviderConfig::getNoAnsTimerReInv()
{
  return miNoAnsTimerReInv;
}

void INGwSpSipProviderConfig::setNoAnsTimerReInv(int aTimer)
{
  miNoAnsTimerReInv = aTimer;
}

int INGwSpSipProviderConfig::getNoAnsTimer()
{
  return miNoAnsTimer;
}

void INGwSpSipProviderConfig::setNoAnsTimer(int aTimer)
{
  miNoAnsTimer = aTimer;
}

int INGwSpSipProviderConfig::getByeAlsoXfer()
{
  return mByeAlsoXfer;
}

void INGwSpSipProviderConfig::setByeAlsoXfer(bool aXfer)
{
  mByeAlsoXfer = aXfer;
}

int
INGwSpSipProviderConfig::getObGwNum() {
  return miNumOfGws;

}

const string& INGwSpSipProviderConfig::getRedirGwInfo()
{
  return mCallRedirGwInfo;
}

bool INGwSpSipProviderConfig::getSipRouteUseRedirect()
{
  return mSipRouteUseRedirect;
}

void INGwSpSipProviderConfig::setSipRouteUseRedirect(bool aValue)
{
  mSipRouteUseRedirect = aValue;
}

bool INGwSpSipProviderConfig::isHoldMsg()
{
   return m_holdSipMsg;
}

void INGwSpSipProviderConfig::setHoldMsgFlag(bool status)
{
   m_holdSipMsg = status;
}

void INGwSpSipProviderConfig::setRedirectContactURI(
                                INGwSpSipProviderConfig::RedirectContactURI uritype)
{
   meRedirContactURI = uritype;
}

INGwSpSipProviderConfig::RedirectContactURI 
INGwSpSipProviderConfig::getRedirectContactURI()
{
   return meRedirContactURI;
}

void INGwSpSipProviderConfig::setVxmlReqUriTreatment(VxmlReqUriTreatment inData)
{
   if(inData == REQ_URI_VXML_INPARAM)
   {
      LogAlways(0, "VXML: Param based ReqUri treatment configured.");
   }
   else if(inData == REQ_URI_VXML_INUSER)
   {
      LogAlways(0, "VXML: User part based ReqUri treatment configured.");
   }
   else
   {
      LogError(0, "VXML: Some junk in ReqURI treatment correcting.");
      inData = REQ_URI_VXML_INPARAM;
      LogAlways(0, "VXML: Param based ReqUri treatment configured.");
   }

   m_vxmlReqUriTreatment = inData;
}

INGwSpSipProviderConfig::VxmlReqUriTreatment 
INGwSpSipProviderConfig::getVxmlReqUriTreatment()
{
   return m_vxmlReqUriTreatment;
}

bool INGwSpSipProviderConfig::isVxmlUrlSlashReplacement()
{
   return m_vxmlUrlSlashReplacement;
}

void INGwSpSipProviderConfig::setVxmlUrlSlashReplacement(bool inData)
{
   m_vxmlUrlSlashReplacement = inData;
}

int INGwSpSipProviderConfig::isConvedia()
{
   return (int)m_isConvedia;
}

bool
INGwSpSipProviderConfig::isSdpSerializationReqd ()
{
  return mbIsSdpSerializationReqd;
}

void
INGwSpSipProviderConfig::setPrivacyProtocol (int aiProtocol)
{
  if (aiProtocol == SIP_RFC3323)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy Protocol being used is rfc3323");

    miPrivacyProtocol = aiProtocol;
  }
  else if (aiProtocol == SIP_RPID)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy Protocol being used is based on remote-party-id");
  
    miPrivacyProtocol = aiProtocol;
  }
  else
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy Protocol being defaulted to rfc3323");

    miPrivacyProtocol = SIP_RFC3323;
  }
}

int
INGwSpSipProviderConfig::getPrivacyProtocol ()
{
  return miPrivacyProtocol;
}

void
INGwSpSipProviderConfig::setPrivacyMask (int aiMask)
{
  miPrivacyMask = 0;
  if (aiMask == 0)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy Mask is NULL. No Privacy being provided.");
    return;
  }

  if ((aiMask & SIP_PRIVACY_NONE) == SIP_PRIVACY_NONE)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy = None being Passed Through.");
    miPrivacyMask |= SIP_PRIVACY_NONE;
  }

  if ((aiMask & SIP_PRIVACY_SESSION) == SIP_PRIVACY_SESSION)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy = SESSION being Passed Through.");
    miPrivacyMask |= SIP_PRIVACY_SESSION;
  }

  if ((aiMask & SIP_PRIVACY_HEADER) == SIP_PRIVACY_HEADER)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy = HEADER being Passed Through.");
    miPrivacyMask |= SIP_PRIVACY_HEADER;
  }

  if ((aiMask & SIP_PRIVACY_USER) == SIP_PRIVACY_USER)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy = USER being Passed Through.");
    miPrivacyMask |= SIP_PRIVACY_USER;
  }

  if ((aiMask & SIP_PRIVACY_ID) == SIP_PRIVACY_ID)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy = ID being Passed Through.");
    miPrivacyMask |= SIP_PRIVACY_ID;
  }

  if ((aiMask & SIP_PRIVACY_CRITICAL) == SIP_PRIVACY_CRITICAL)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy = CRITICAL being Passed Through.");
    miPrivacyMask |= SIP_PRIVACY_CRITICAL;
  }
}

void
INGwSpSipProviderConfig::setDefaultPrivacy (int aiMask)
{
  miDefaultPrivacy = 0;
  if (aiMask == 0)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy Mask is NULL. No Default Privacy being provided.");
    return;
  }

  if ((aiMask & SIP_PRIVACY_NONE) == SIP_PRIVACY_NONE)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy = None being set as Default Privacy.");
    miDefaultPrivacy |= SIP_PRIVACY_NONE;
  }

  if ((aiMask & SIP_PRIVACY_SESSION) == SIP_PRIVACY_SESSION)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy = SESSION being set as Default Privacy.");
    miDefaultPrivacy |= SIP_PRIVACY_SESSION;
  }

  if ((aiMask & SIP_PRIVACY_HEADER) == SIP_PRIVACY_HEADER)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy = HEADER being set as Default Privacy.");
    miDefaultPrivacy |= SIP_PRIVACY_HEADER;
  }

  if ((aiMask & SIP_PRIVACY_USER) == SIP_PRIVACY_USER)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy = USER being set as Default Privacy.");
    miDefaultPrivacy |= SIP_PRIVACY_USER;
  }

  if ((aiMask & SIP_PRIVACY_ID) == SIP_PRIVACY_ID)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy = ID being set as Default Privacy.");
    miDefaultPrivacy |= SIP_PRIVACY_ID;
  }

  if ((aiMask & SIP_PRIVACY_CRITICAL) == SIP_PRIVACY_CRITICAL)
  {
    logger.logINGwMsg (false, ALWAYS_FLAG, 0,
      "Privacy = CRITICAL being set as Default Privacy.");
    miDefaultPrivacy |= SIP_PRIVACY_CRITICAL;
  }
}


int
INGwSpSipProviderConfig::getPrivacyMask ()
{
  return miPrivacyMask;
}

int
INGwSpSipProviderConfig::getDefaultPrivacy ()
{
  return miDefaultPrivacy;
}



std::set<int> &
INGwSpSipProviderConfig::getProcessedInformationalHeaders ()
{
  return mProcessedInformationalHeaders;
}
    
std::set<std::string> &
INGwSpSipProviderConfig::getUnprocessedInformationalHeaders ()
{
  return mUnprocessedInformationalHeaders;
}

void
INGwSpSipProviderConfig::parseInformationalHeaders (const char *apcList)
{
  string lstrHdrList (apcList);

  logger.logINGwMsg (false, ALWAYS_FLAG, 0,
    "Parsing Informational Headers <%s>", lstrHdrList.c_str());

  mProcessedInformationalHeaders.clear();
  mUnprocessedInformationalHeaders.clear();


  char a = ',', b = '\n';
  replace<string::iterator, char>(lstrHdrList.begin(), lstrHdrList.end(), a, b);

  // Tokenize all the strings and place them in a vector.
  strstream hdrstrstream;
  hdrstrstream << lstrHdrList;
  istream_iterator<string> streamiter(hdrstrstream);
  vector<string> hdrVec;
  copy(streamiter, istream_iterator<string>(), back_inserter(hdrVec));

  // For each header, get the sip supoprted header from the HSS stack API.

  for(vector<string>::iterator veciter = hdrVec.begin();
      veciter != hdrVec.end(); veciter++)
  {
    logger.logINGwMsg(false, VERBOSE_FLAG, 0, 
      "INGwSpSipProviderConfig::parseInformationalHeaders: Found token <%s>", 
      (*veciter).c_str());

    if(!(*veciter).empty()) // Ignore the token if it is empty
    {
      string hdrcol = *veciter + ":";
      en_HeaderType hdrType = SipHdrTypeUnknown;
      SipError siperror;
      sip_getTypeFromName((char*)hdrcol.c_str(), &hdrType, &siperror);

      // This header may be known, but unless its parsing is enabled, the
      // header will be stored in unknown header list.  In this case, treat
      // it as unknown header.
      if(SipHdrTypeUnknown != hdrType)
      {
        SipHdrTypeList& lHdrsTobeDecodedList = 
								INGwSpStackConfigMgr::getInstance()->getStackDecodeHeaderTypes();
        if(lHdrsTobeDecodedList.enable[hdrType] != SipSuccess)
          hdrType = SipHdrTypeUnknown;
      }

      if(SipHdrTypeUnknown == hdrType)
      {
        // Store in the unknown vector list.  First convert the entire header
        // into small case, so that there will not be any case-sensitive
        // comparison problems.
        string& currstr = *veciter;
        mUnprocessedInformationalHeaders.insert(*veciter);
        logger.logINGwMsg(false, VERBOSE_FLAG, 0, "INGwSpSipProviderConfig::parseInformationalHeaders: Unknown header <%s>", (*veciter).c_str());
      }
      else
      {
        if(mProcessedInformationalHeaders.find(hdrType) == 
                        mProcessedInformationalHeaders.end())
          mProcessedInformationalHeaders.insert(hdrType);
        logger.logINGwMsg(false, VERBOSE_FLAG, 0, "INGwSpSipProviderConfig::parseInformationalHeaders: Known header <%s>", (*veciter).c_str());
      }
    }
  } // end of for

  return;
}

// Mriganka - BPInd08129
bool INGwSpSipProviderConfig::offerSdpInIvrEnabled()
{
	return miSetOfferSdpInIvr; 
}

// Mriganka
bool INGwSpSipProviderConfig::sendEmptyInvite(){
	return mbSendEmptyInvite;
}

const char * INGwSpSipProviderConfig::getAreaAppender()
{
   return _areaAppender;
}
bool INGwSpSipProviderConfig::isSendBilling()
{
   return _sendBilling;
}

bool INGwSpSipProviderConfig::isSendReason()
{
   return _sendReason;
}

bool INGwSpSipProviderConfig::isSendPhoneContext()
{
   return _sendPhoneContext;
}

bool INGwSpSipProviderConfig::isSendBody()
{
   return _sendBody;
}

bool INGwSpSipProviderConfig::isSendContact()
{
   return _sendContact;
}

bool INGwSpSipProviderConfig::isSendAsserted()
{
   return _sendAsserted;
}

bool INGwSpSipProviderConfig::isSupportEarlyMedia()
{
   return _supportEarlyMedia;
}

bool INGwSpSipProviderConfig::isSendFromParams()
{
   return _sendFromParam;
}

bool INGwSpSipProviderConfig::isSendToParams()
{
   return _sendToParam;
}

bool INGwSpSipProviderConfig::isSendReqURIParams()
{
   return _sendReqURIParam;
}

bool INGwSpSipProviderConfig::isSendOLI()
{
   return _sendOLI;
}

bool INGwSpSipProviderConfig::isSendContactAccess()
{
   return _sendContactAccess;
}

bool INGwSpSipProviderConfig::isSendContactParams()
{
   return _sendContactParams;
}

bool INGwSpSipProviderConfig::isProcessIncomingVia()
{
   return _processIncomingVia;
}

const RSI_NSP_SIP::INGwSpSipHeaderPolicy & INGwSpSipProviderConfig::getSipHeaderPolicy()
{
   return _headerPolicy;
}

const RSI_NSP_SIP::INGwSpSipHeaderDefaultData & 
                                      INGwSpSipProviderConfig::getSipHeaderDefault()
{
   return _headerDefault;
}

using namespace RSI_NSP_SIP;

void INGwSpSipProviderConfig::_loadHeaderDefaults()
{
#if 0
//PANKAJ Not loading any policy now
//As we may not need it now
   INGwIfrPrParamRepository &repo = INGwIfrPrParamRepository::getInstance();
   string val;

   {
      INGwSpSipHeaderDefault &hdr = _headerDefault.getToHeaderDefault();

      repo.getValue(sipHP_ToHdrDispEnv, val);
      hdr.setDisplay(val.c_str(), val.size());

      repo.getValue(sipHP_ToHdrPrmEnv, val);
      hdr.setParam(val.c_str(), val.size());

      INGwSpSipAddressDefault &addr = hdr.getAddress();

      repo.getValue(sipHP_ToHdrAddrUsrEnv, val);
      addr.setUser(val.c_str(), val.size());

      repo.getValue(sipHP_ToHdrAddrPrmEnv, val);
      addr.setParam(val.c_str(), val.size());
   }

   {
      INGwSpSipHeaderDefault &hdr = _headerDefault.getFromHeaderDefault();

      repo.getValue(sipHP_FromHdrDispEnv, val);
      hdr.setDisplay(val.c_str(), val.size());

      repo.getValue(sipHP_FromHdrPrmEnv, val);
      hdr.setParam(val.c_str(), val.size());

      INGwSpSipAddressDefault &addr = hdr.getAddress();

      repo.getValue(sipHP_FromHdrAddrUsrEnv, val);
      addr.setUser(val.c_str(), val.size());

      repo.getValue(sipHP_FromHdrAddrPrmEnv, val);
      addr.setParam(val.c_str(), val.size());
   }

   {
      INGwSpSipHeaderDefault &hdr = _headerDefault.getContactHeaderDefault();

      repo.getValue(sipHP_ContactHdrDispEnv, val);
      hdr.setDisplay(val.c_str(), val.size());

      repo.getValue(sipHP_ContactHdrPrmEnv, val);
      hdr.setParam(val.c_str(), val.size());

      INGwSpSipAddressDefault &addr = hdr.getAddress();

      repo.getValue(sipHP_ContactHdrAddrUsrEnv, val);
      addr.setUser(val.c_str(), val.size());

      repo.getValue(sipHP_ContactHdrAddrPrmEnv, val);
      addr.setParam(val.c_str(), val.size());
   }

   {
      INGwSpSipAddressDefault &addr = _headerDefault.getReqUriDefault();

      repo.getValue(sipHP_ReqUriAddrUsrEnv, val);
      addr.setUser(val.c_str(), val.size());

      repo.getValue(sipHP_ReqUriAddrPrmEnv, val);
      addr.setParam(val.c_str(), val.size());
   }
#endif
}

void INGwSpSipProviderConfig::_loadHeaderPolicy()
{
#if 0
//PANKAJ Not loading any policy now
//As we may not need it now

   INGwIfrPrParamRepository &repo = INGwIfrPrParamRepository::getInstance();
   string val;

   {
      INGwSpSipHeaderTreatment &hdr = _headerPolicy.getToHeaderTreatment();
      INGwSpSipAddressTreatment &addr = hdr.getAddressTreatment();

      repo.getValue(sipHP_ToHdrDispOwner, val);
      hdr.setDisplayOwner(_getHPOwner(val));
      repo.getValue(sipHP_ToHdrDispTreatment, val);
      hdr.setDisplayTreatment(_getHPDisplay(val));
      repo.getValue(sipHP_ToHdrDispRoleChange, val);
      hdr.setDisplayRoleChangeChar(*(val.c_str()));

      repo.getValue(sipHP_ToHdrAddrProtOwner, val);
      addr.setProtocolOwner(_getHPOwner(val));
      repo.getValue(sipHP_ToHdrAddrProtTreatment, val);
      addr.setProtocolTreatment(_getHPProtocol(val));

      repo.getValue(sipHP_ToHdrAddrUsrOwner, val);
      addr.setUserPartOwner(_getHPOwner(val));
      repo.getValue(sipHP_ToHdrAddrUsrTreatment, val);
      addr.setUserPartTreatment(_getHPUser(val));
      repo.getValue(sipHP_ToHdrAddrUsrRoleChange, val);
      addr.setUserPartRoleChangeChar(*(val.c_str()));

      repo.getValue(sipHP_ToHdrAddrHstOwner, val);
      addr.setHostPartOwner(_getHPOwner(val));
      repo.getValue(sipHP_ToHdrAddrHstTreatment, val);
      addr.setHostPartTreatment(_getHPHost(val));

      repo.getValue(sipHP_ToHdrAddrPrmOwner, val);
      addr.setParamOwner(_getHPOwner(val));
      repo.getValue(sipHP_ToHdrAddrPrmTreatment, val);
      addr.setParamTreatment(_getHPParam(val));
      repo.getValue(sipHP_ToHdrAddrPrmSleeTreatment, val);
      addr.setParamSleeInputTreatment(_getHPSleeParam(val));

      repo.getValue(sipHP_ToHdrPrmOwner, val);
      hdr.setHeaderParamOwner(_getHPOwner(val));
      repo.getValue(sipHP_ToHdrPrmTreatment, val);
      hdr.setHeaderParamTreatment(_getHPParam(val));
      repo.getValue(sipHP_ToHdrPrmSleeTreatment, val);
      hdr.setParamSleeInputTreatment(_getHPSleeParam(val));
   }

   {
      INGwSpSipHeaderTreatment &hdr = _headerPolicy.getFromHeaderTreatment();
      INGwSpSipAddressTreatment &addr = hdr.getAddressTreatment();

      repo.getValue(sipHP_FromHdrDispOwner, val);
      hdr.setDisplayOwner(_getHPOwner(val));
      repo.getValue(sipHP_FromHdrDispTreatment, val);
      hdr.setDisplayTreatment(_getHPDisplay(val));
      repo.getValue(sipHP_FromHdrDispRoleChange, val);
      hdr.setDisplayRoleChangeChar(*(val.c_str()));

      repo.getValue(sipHP_FromHdrAddrProtOwner, val);
      addr.setProtocolOwner(_getHPOwner(val));
      repo.getValue(sipHP_FromHdrAddrProtTreatment, val);
      addr.setProtocolTreatment(_getHPProtocol(val));

      repo.getValue(sipHP_FromHdrAddrUsrOwner, val);
      addr.setUserPartOwner(_getHPOwner(val));
      repo.getValue(sipHP_FromHdrAddrUsrTreatment, val);
      addr.setUserPartTreatment(_getHPUser(val));
      repo.getValue(sipHP_FromHdrAddrUsrRoleChange, val);
      addr.setUserPartRoleChangeChar(*(val.c_str()));

      repo.getValue(sipHP_FromHdrAddrHstOwner, val);
      addr.setHostPartOwner(_getHPOwner(val));
      repo.getValue(sipHP_FromHdrAddrHstTreatment, val);
      addr.setHostPartTreatment(_getHPHost(val));

      repo.getValue(sipHP_FromHdrAddrPrmOwner, val);
      addr.setParamOwner(_getHPOwner(val));
      repo.getValue(sipHP_FromHdrAddrPrmTreatment, val);
      addr.setParamTreatment(_getHPParam(val));
      repo.getValue(sipHP_FromHdrAddrPrmSleeTreatment, val);
      addr.setParamSleeInputTreatment(_getHPSleeParam(val));

      repo.getValue(sipHP_FromHdrPrmOwner, val);
      hdr.setHeaderParamOwner(_getHPOwner(val));
      repo.getValue(sipHP_FromHdrPrmTreatment, val);
      hdr.setHeaderParamTreatment(_getHPParam(val));
      repo.getValue(sipHP_FromHdrPrmSleeTreatment, val);
      hdr.setParamSleeInputTreatment(_getHPSleeParam(val));
   }

   {
      INGwSpSipHeaderTreatment &hdr = _headerPolicy.getContactHeaderTreatment();
      INGwSpSipAddressTreatment &addr = hdr.getAddressTreatment();

      repo.getValue(sipHP_ContactHdrDispOwner, val);
      hdr.setDisplayOwner(_getHPOwner(val));
      repo.getValue(sipHP_ContactHdrDispTreatment, val);
      hdr.setDisplayTreatment(_getHPDisplay(val));
      repo.getValue(sipHP_ContactHdrDispRoleChange, val);
      hdr.setDisplayRoleChangeChar(*(val.c_str()));

      repo.getValue(sipHP_ContactHdrAddrProtOwner, val);
      addr.setProtocolOwner(_getHPOwner(val));
      repo.getValue(sipHP_ContactHdrAddrProtTreatment, val);
      addr.setProtocolTreatment(_getHPProtocol(val));

      repo.getValue(sipHP_ContactHdrAddrUsrOwner, val);
      addr.setUserPartOwner(_getHPContactUserOwner(val));
      repo.getValue(sipHP_ContactHdrAddrUsrTreatment, val);
      addr.setUserPartTreatment(_getHPUser(val));
      repo.getValue(sipHP_ContactHdrAddrUsrRoleChange, val);
      addr.setUserPartRoleChangeChar(*(val.c_str()));

      repo.getValue(sipHP_ContactHdrAddrHstOwner, val);
      addr.setHostPartOwner(_getHPOwner(val));
      repo.getValue(sipHP_ContactHdrAddrHstTreatment, val);
      addr.setHostPartTreatment(_getHPHost(val));

      repo.getValue(sipHP_ContactHdrAddrPrmOwner, val);
      addr.setParamOwner(_getHPOwner(val));
      repo.getValue(sipHP_ContactHdrAddrPrmTreatment, val);
      addr.setParamTreatment(_getHPParam(val));
      repo.getValue(sipHP_ContactHdrAddrPrmSleeTreatment, val);
      addr.setParamSleeInputTreatment(_getHPSleeParam(val));

      repo.getValue(sipHP_ContactHdrPrmOwner, val);
      hdr.setHeaderParamOwner(_getHPOwner(val));
      repo.getValue(sipHP_ContactHdrPrmTreatment, val);
      hdr.setHeaderParamTreatment(_getHPParam(val));
      repo.getValue(sipHP_ContactHdrPrmSleeTreatment, val);
      hdr.setParamSleeInputTreatment(_getHPSleeParam(val));
   }

   {
      INGwSpSipAddressTreatment &addr = _headerPolicy.getReqUriTreatment();

      repo.getValue(sipHP_ReqUriAddrProtOwner, val);
      addr.setProtocolOwner(_getHPOwner(val));
      repo.getValue(sipHP_ReqUriAddrProtTreatment, val);
      addr.setProtocolTreatment(_getHPProtocol(val));

      repo.getValue(sipHP_ReqUriAddrUsrOwner, val);
      addr.setUserPartOwner(_getHPOwner(val));
      repo.getValue(sipHP_ReqUriAddrUsrTreatment, val);
      addr.setUserPartTreatment(_getHPUser(val));
      repo.getValue(sipHP_ReqUriAddrUsrRoleChange, val);
      addr.setUserPartRoleChangeChar(*(val.c_str()));

      repo.getValue(sipHP_ReqUriAddrHstOwner, val);
      addr.setHostPartOwner(_getHPOwner(val));
      repo.getValue(sipHP_ReqUriAddrHstTreatment, val);
      addr.setHostPartTreatment(_getHPHost(val));

      repo.getValue(sipHP_ReqUriAddrPrmOwner, val);
      addr.setParamOwner(_getHPOwner(val));
      repo.getValue(sipHP_ReqUriAddrPrmTreatment, val);
      addr.setParamTreatment(_getHPParam(val));
      repo.getValue(sipHP_ReqUriAddrPrmSleeTreatment, val);
      addr.setParamSleeInputTreatment(_getHPSleeParam(val));
   }
#endif
}

int INGwSpSipProviderConfig::getSelfPort()
{
   return _selfPort;
}

const char * INGwSpSipProviderConfig::getSelfIp()
{
	INGwIfrPrParamRepository::getInstance().getValue(ingwFLOATING_IP_ADDR, _selfIP);
	return _selfIP.c_str();
}

int INGwSpSipProviderConfig::getCopyRRFlag(){
	return miCopyOrigRR;
}

// BPInd08347
bool INGwSpSipProviderConfig::updateIvrStatusOnTxnFailure()
{
	return miUpdateIvrStat; 
}

// BPUsa07885 : [
const char* INGwSpSipProviderConfig::getSRVDomainName()
{
	if( _srvDomainName.empty())
		return NULL;
	
	return _srvDomainName.c_str();
}

const unsigned long INGwSpSipProviderConfig::getSRVTTLValue()
{
	return _srvTTLValue;
}

bool INGwSpSipProviderConfig::isSRVLookupEnabled()
{
	if( _srvDomainName.empty())
		return false;
	
	return true;
}
// : ]

// BPUsa07888 : [
bool INGwSpSipProviderConfig::isSIPTParsingEnabled()
{
	return _parseSIPTBody;
}
// : ]

//	BPUsa07893 : [
bool INGwSpSipProviderConfig::isOutgoingMessageScreeningEnabled()
{
	return (_screenSipMessages & SCREEN_OUTGOING);
}

bool INGwSpSipProviderConfig::isIncomingMessageScreeningEnabled()
{
	return (_screenSipMessages & SCREEN_INCOMING);
}

void INGwSpSipProviderConfig::configureMessageScreening( unsigned int valuemask)
{
	_screenSipMessages = valuemask; 

	if( _screenSipMessages )
	{
		if (_screenSipMessages & SCREEN_OUTGOING)
			logger.logINGwMsg (false, ALWAYS_FLAG, 0, "Screening outgoing SIP messages is enabled.");

		if (_screenSipMessages & SCREEN_INCOMING)
			logger.logINGwMsg (false, ALWAYS_FLAG, 0, "Screening incoming SIP messages is enabled.");
	} 
	else
		logger.logINGwMsg (false, ALWAYS_FLAG, 0, "Screening SIP messages is disabled.");
}


bool INGwSpSipProviderConfig::isSDPFixEnabled()
{
	return _sdpFixEnabled ;
}

bool INGwSpSipProviderConfig::isUseContactForReqUri()
{
	return mIsUseContactForReqUri;
}

bool INGwSpSipProviderConfig::isTransportUdp()
{
  return m_IsTransportUdp;
}


