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
//     File:     INGwSpMsgInviteStateContext.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_MSG_INVITE_STATE_CONTEXT_H_
#define INGW_SP_MSG_INVITE_STATE_CONTEXT_H_

#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipIncludes.h>

#if 0
#include <INGwSipMsgHandler/INGwSpMsgResolver.h>
#endif

#include <string>

struct INGwSpMsgInviteStateContext
{
   public:

      INGwSpMsgInviteStateContext();
      ~INGwSpMsgInviteStateContext();

      void reset();
      std::string toLog() const;

			// Overloaded '=' operator used during SipConnection Cloning
			INGwSpMsgInviteStateContext& operator = ( const INGwSpMsgInviteStateContext& fromObject) ;
			// Copy constructor to complement the '=' operator overloading
			INGwSpMsgInviteStateContext( const INGwSpMsgInviteStateContext& fromObject) ;

	 public:

      //UNUSED Will remove them later after reconfirmation.
      int  miDirection;
      int  miRequestName;
      int  miMode;           // B2B(pass through) or local mode
      int  miLocalEvent;     // Event to be generatd in local mode (hold,resync)
      bool mbIsReqCancelled; // Whether this request has been cancelled

   public:

      int  miLatestRespCode;
      bool m_isRpr;                  //PRACK handling.

      FailureCause  miFailureCause;
      LastOperation mLastop;

      bool isFailedByePending;
      bool isCancelPending;

      bool      mbIsincoming;
      hrtime_t  callSetupStartTime;

      std::string    mstrIvrUri;

   public:

      //Timer related. 

      unsigned int mNoAnsTimerid;
      unsigned int mInitRespTimerid;

     
      unsigned int connectNoAnsTimer;
      //BpConnection::ConnectionMode connectMode;
      int connectMode;

   public:

      //For 3xx response handling.

      bool      isContactSearching;
      INGwSipEPInfo* contactinfolist;
      int       currentContactSearchIndex;
      int       contactlistLength;

			int       mOutboundGwIndex;

      //PANKAJ 
			//SRVGwInfo *outboundGwList;
			unsigned short outboundGwListLength;

   public:

      // Redirection query related context

      INGwSipMethodType mCallbackHandlerType;
      Sdf_st_callObject* redirCallObj;
      bool mbIsRedirectionQuery;

   public:

      // Call redirection related context

      std::string mRedirTarget;
      bool mbIsCallRedirection;

      bool mAlertFlag;

      // BPInd11560 - STARTS.
      bool mOtBoGwFailed;
      // BPInd11560 - ENDS.
}; //INGW_SP_MSG_INVITE_STATE_CONTEXT_H_

#endif
