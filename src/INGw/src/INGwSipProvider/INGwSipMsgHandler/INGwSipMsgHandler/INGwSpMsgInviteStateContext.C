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
//     File:     INGwSpMsgInviteStateContext.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwSipMsgHandler");

#include <INGwSipMsgHandler/INGwSpMsgInviteStateContext.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipProviderConfig.h>

using namespace std;


INGwSpMsgInviteStateContext::INGwSpMsgInviteStateContext()
{
	contactinfolist = NULL;
	//outboundGwList = NULL;

  reset();
}

void INGwSpMsgInviteStateContext::reset()
{
  miDirection          = -1   ;
  miRequestName        = -1   ;
  miMode               = -1   ;
  miLocalEvent         = -1   ;
  miLatestRespCode     = -1   ;
  mbIsReqCancelled     = false;
  isFailedByePending   = false;
  isCancelPending      = false;
  m_isRpr              = false;

  mLastop              = LASTOP_UNKNOWN;
  miFailureCause       = FAILCAUSE_NONE ;

  mNoAnsTimerid        = 0;
  mInitRespTimerid     = 0;

  connectNoAnsTimer    = 0;
  //connectMode          = BpConnection::CONNECTION_MODE_UNKNOWN;
  connectMode          = 0;

	isContactSearching        = false;

	if (contactinfolist != NULL){
		LogINGwTrace( false, 0,  "contactinfolist deleted");
		delete [] contactinfolist;
	}
  contactinfolist           = NULL;

  currentContactSearchIndex = -1;
	contactlistLength         = -1;

//	if (outboundGwList != NULL){
//		LogINGwMsg( false, 0,  "outboundGwList deleted");
//		delete [] outboundGwList;
//	}
// outboundGwList           = NULL;

  outboundGwListLength         = -1;

  mOutboundGwIndex     = -1;

  mbIsincoming         = false;

  callSetupStartTime   = -1;

  mstrIvrUri           = "";

  // Redirection query related context
  mCallbackHandlerType = INGW_SIP_METHOD_TYPE_UNKNOWN;
  redirCallObj         = Sdf_co_null;
  mbIsRedirectionQuery = false;
  // Redirection query related context

  // Call redirection related context
  mRedirTarget.clear();
  mbIsCallRedirection = false;
  // Call redirection related context

  mAlertFlag = false;

  mOtBoGwFailed = false;
}

INGwSpMsgInviteStateContext::~INGwSpMsgInviteStateContext()
{
	reset();
}

std::string INGwSpMsgInviteStateContext::toLog() const
{
   std::string ret = "Invite Context\n";

   char local[1000];
   sprintf(local, "LastResp:[%d] RprFlag:[%d] FailureCause:[%d]\n"
                  "LastOp:[%d] ByePending:[%d] CancelPending:[%d]\n"
                  "IncomingFlag:[%d] startTime:[%lld]\n"
                  "NoAnsID:[%d]  InitRespID:[%d] \n"
                  "ConnectNoAnsTimer:[%d] ConnectMode:[%d]\n"
                  "ContactSearchFlag:[%d] ContactSearchIdx:[%d] ContactLen:[%d]"
                  "\nOutGWIdx:[%d] OutboundGwListLength:[%d]\n"
                  "Query MethodType:[%d] RedirQueryFlag:[%d]\n"
                  "RedirTarget:[%s] RedirFlag[%d]\n",
           miLatestRespCode, m_isRpr, miFailureCause, 
           mLastop, isFailedByePending, isCancelPending,
           mbIsincoming, callSetupStartTime,
           mNoAnsTimerid, mInitRespTimerid,
           connectNoAnsTimer, connectMode,
           isContactSearching, currentContactSearchIndex, contactlistLength,
           mOutboundGwIndex, outboundGwListLength,
           mCallbackHandlerType, mbIsRedirectionQuery, 
           mRedirTarget.c_str(), mbIsCallRedirection);

   ret += local;
   return ret;
}

// Overloaded '=' operator used during INGwSpSipConnection Cloning
INGwSpMsgInviteStateContext& INGwSpMsgInviteStateContext::operator = ( const INGwSpMsgInviteStateContext& fromObject) {

	reset();

	// Deep copy contactlist members:
	isContactSearching        = fromObject.isContactSearching;
	currentContactSearchIndex = fromObject.currentContactSearchIndex;
	contactlistLength         = fromObject.contactlistLength;

	if (contactlistLength > 0 ) {

		if (contactinfolist != NULL)
			delete [] contactinfolist;

		contactinfolist           = new INGwSipEPInfo[ contactlistLength ];

		for (int i = 0; i < contactlistLength; i++){
			contactinfolist[i] = fromObject.contactinfolist[i];
		}
	}

	// Deep copy outboundGwList members:
	outboundGwListLength         = fromObject.outboundGwListLength;
#if 0
//PANKAJ We won't support query service

	if ( (outboundGwListLength > 0 ) &&
		INGwSpSipProviderConfig::isSRVLookupEnabled() ) {

		if (outboundGwList != NULL)
			delete [] outboundGwList;

		outboundGwList           = new SRVGwInfo[ outboundGwListLength ];

		for (int i = 0; i < outboundGwListLength; i++){
			outboundGwList[i] = fromObject.outboundGwList[i];
		}
	}

#endif
	// Copy all OutBw Gw params
	mOutboundGwIndex     = fromObject.mOutboundGwIndex;

	// No Answer Timer and Connection mode
	mNoAnsTimerid        = fromObject.mNoAnsTimerid;
	connectNoAnsTimer    = fromObject.connectNoAnsTimer;
	connectMode          = fromObject.connectMode;

	// Call redirection related context
	mRedirTarget = fromObject.mRedirTarget;
	mbIsCallRedirection = fromObject.mbIsCallRedirection;
	mAlertFlag = fromObject.mAlertFlag;

	return *this;
}

// Copy constructor to complement the '=' operator overloading
INGwSpMsgInviteStateContext::INGwSpMsgInviteStateContext( const INGwSpMsgInviteStateContext& fromObject) {

	contactinfolist = NULL;
//	outboundGwList = NULL;

	reset();

	// Deep copy contactlist members:
	isContactSearching        = fromObject.isContactSearching;
	currentContactSearchIndex = fromObject.currentContactSearchIndex;
	contactlistLength         = fromObject.contactlistLength;

	if (contactlistLength > 0 ) {

		if (contactinfolist != NULL)
			delete [] contactinfolist;

		contactinfolist           = new INGwSipEPInfo[ contactlistLength ];

		for (int i = 0; i < contactlistLength; i++){
			contactinfolist[i] = fromObject.contactinfolist[i];
		}
	}

	outboundGwListLength         = fromObject.outboundGwListLength;

#if 0
//PANKAJ We won't support query service
	if ( (outboundGwListLength > 0 ) &&
		INGwSpSipProviderConfig::isSRVLookupEnabled() ) {

		if (outboundGwList != NULL)
			delete [] outboundGwList;

		outboundGwList           = new SRVGwInfo[ outboundGwListLength ];

		for (int i = 0; i < outboundGwListLength; i++){
			outboundGwList[i] = fromObject.outboundGwList[i];
		}
	}
#endif

	// Copy all OutBw Gw params
	mOutboundGwIndex     = fromObject.mOutboundGwIndex;

	// No Answer Timer and Connection mode
	mNoAnsTimerid        = fromObject.mNoAnsTimerid;
	connectNoAnsTimer    = fromObject.connectNoAnsTimer;
	connectMode          = fromObject.connectMode;

	// Call redirection related context
	mRedirTarget = fromObject.mRedirTarget;
	mbIsCallRedirection = fromObject.mbIsCallRedirection;
	mAlertFlag = fromObject.mAlertFlag;

}

