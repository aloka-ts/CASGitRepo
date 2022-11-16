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
//     File:     INGwSpSipContext.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************

#ifndef INGW_SP_SIP_CONTEXT_H_
#define INGW_SP_SIP_CONTEXT_H_
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
#include <netinet/in.h>
#include <INGwSipProvider/INGwSpSipCommon.h>
#include <INGwSipProvider/INGwSpSipIncludes.h>

#define MAX_CALLID_LEN     256
class INGwSpSipConnection;

// INGwSipTranspInfo is used by the network listener thread to pass the peer host
// information to the provider.  It places this structure as a context data in
// the INGwWorkUnit structure.  The sip stack interface layer is responsible for
// destroying it.
//struct INGwSipTranspInfo
struct INGwSipTranspInfo
{
  public:
    char             pTranspAddr[INET_ADDRSTRLEN];
    int              mPort;
    Sdf_ty_protocol  mTranspType;
    int              mSockfd;
};

// This structure is stored as application context data in the HSS call object,
// to reference the INGwSpSipConnection from the HSS call object.  This is the only
// way to access the provider's connection from the hss call object.
struct INGwConnectionContext
{
  public:

    INGwConnectionContext() :
      mSipConnection(0), 
      mUniqId(-1)      , mbUseGivenCallid(false)
    {}

    INGwSpSipConnection *mSipConnection;
    char             mCallId[MAX_CALLID_LEN];

    // This member is only used by the callid generator function to create
    // unique call-ids across all connections in a call.
    int              mUniqId;
    // Setting this will cause the generator function to simply replicate the
    // supplied callid, instead of creating a new unique callid.
    bool             mbUseGivenCallid;
};

// This structure is the base for all timer types in sip provider.  The workunit
// which will carry the timer expiry message will contain a reference to this
// base class.  Based on the type, the correct object can be got from there.
struct INGwSipTimerType
{
  public:
   virtual ~INGwSipTimerType(){}
   typedef enum
   {
     STACK_TIMER                = 34567,
     NOANSWER_TIMER             = 78934,
     INITRESP_TIMER             = 82347,
     TRANSCOMPL_TIMER           = 24588,
     REMOTE_RETRANS_PURGE_TIMER = 29348,
     SESSION_TIMER              = 76543,
     SIPIVR_TIMER               = 44533,
		 EP_HB_TIMER                = 98989
     
   } TimerType;

   TimerType mType;
   virtual void dummy(void) = 0;
};

// This structure is passed to the SendCallToPeer method, from where it gets
// passed as a context data in timer related callbacks.  Using this, the
// provider retrieves the HSS call leg (from the call id), then the
// INGwSpSipConnection associated with that HSS leg, and calls the timeout callback
// on the connection.
// This is also used for the no answer timer, where the timer is actually
// fired from the timer adapter directly and not through the stack.

struct INGwSipTimerContext : public INGwSipTimerType
{
  public:
    ~INGwSipTimerContext(){}
    INGwSipMethodType mMethodType;
    char         mCallId[MAX_CALLID_LEN];
    void         dummy(void){}
    short        mConnId;
};

// This structure is used to pass on configuration related information to the
// worker threads.
struct INGwSipConfigContext
{
  std::string mOid;
  std::string mVal;
};

#endif // INGW_SP_SIP_CONTEXT_H_
