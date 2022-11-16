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
//     File:     INGwSpSipProvider.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal   03/12/07     Initial Creation
//********************************************************************
#ifndef INGW_SP_SIP_PROVIDER_H_
#define INGW_SP_SIP_PROVIDER_H_

#include <INGwSipProvider/INGwSpSipCall.h>
#include <INGwSipProvider/INGwSpThreadSpecificSipData.h>

#include <INGwSipProvider/INGwSpSipIncludes.h>
#include <INGwSipProvider/INGwSpSipCallController.h>
#include <INGwSipProvider/INGwSpSipIface.h>

#include <INGwIwf/INGwIwfBaseProvider.h>

extern Sdf_st_initData *pGlbProfile;

class INGwSpSipProvider : public virtual INGwIwfBaseProvider
{
  public:

	  // Memebers for base class INGwIwfBaseProvider

    /**
    * Method for notifying state of Provider.
    */
    virtual int
    changeState(INGwIwfBaseProvider::ProviderStateType p_state);

    /**
	  * Method is called on receivng startUp from EMS Agent
		*/
		virtual int
		startUp(void);

		/**
		* Method to fetch statistics
		*/
		virtual void
		getStatistics(std::ostrstream &output, int tabCount);

		/**
		* Method to get Interface Object associated with Provider
		*/
		virtual INGwIwfBaseIface* getInterface()
		{
			return m_SipIface;
		}

		inline INGwSpSipCallController* getCallController()
		{
			return m_SipCallController;
		}


    INGwSpSipProvider();
    ~INGwSpSipProvider();

    INGwSpSipConnection* getNewConnection(INGwSpSipCall& arCall);

    INGwSpSipCall* getNewCall(const std::string& arconCallId, bool abGenCallId);
    bool getNewCallsStatus(void);

    static INGwSpSipProvider& getInstance();

    static bool generateCallid(char *outCallid, int aiThreadIdx=0);

    int configure(const char* apcOID, const char* apcValue, ConfigOpType aeOpType);

    INGwSpThreadSpecificSipData& getThreadSpecificSipData();

    void handleBkupInfo(const std::string& arCallId, const char* apcMsg, short asLength);

		int initInboundMsgConn(INGwSpSipConnection* p_SipConn);

    void updateWorkerThreadConfig(char* apcOID, char* apcValue);

    inline pthread_key_t* getThreadSpecificKey(void) { return &mThreadSpecificKey; }
    inline pthread_key_t& getKey(void) { return mThreadSpecificKey;}

		std::string toLogSipStats();

  private:

    pthread_key_t               mThreadSpecificKey;

    INGwSpThreadSpecificSipData *mThdSpecData;

    static INGwSpSipProvider *mSipProvider;

    int mCurrentMaxInviteInProgressCount;
    int mConfigMaxInviteInProgressCount;

    int setTSD();

    static const char* mpcOIDsOfInterest[];

		INGwSpSipCallController* m_SipCallController;
		INGwSpSipIface* m_SipIface;
		INGwIwfBaseProvider::ProviderStateType meProviderState;

		void setSipStatParamIndex();

  public :

    static int miStatParamId_SipMsgRecvd;
    static int miStatParamId_SipMsgSent;
    static int miStatParamId_NumInvRecvd;
    static int miStatParamId_NumInvRejected;
    static int miStatParamId_NumNotifyRecvd;
    static int miStatParamId_NumNotifyRejected;
    static int miStatParamId_NumNotifySent;
    static int miStatParamId_NumNotifySentRejected;
    static int miStatParamId_NumInfoRecvd;
    static int miStatParamId_NumInfoRejected;
    static int miStatParamId_NumInfoSent;
    static int miStatParamId_NumInfoSentRejected;
    static int miStatParamId_NumOptRecvd;
    static int miStatParamId_NumHBFailures;


}; // end of INGwSpSipProvider

#endif
