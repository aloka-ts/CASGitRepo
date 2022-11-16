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
//     File:     INGwSpSipIface.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Pankaj bathwal     07/12/07     Initial Creation
//********************************************************************
#ifndef INGW_SP_SIP_IFACE_H_
#define INGW_SP_SIP_IFACE_H_

#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>
#include <INGwIwf/INGwIwfBaseIface.h>

#include <string>
using namespace std;

class INGwIwfIface;

class INGwSpSipIface: public INGwIwfBaseIface
{
	public:

    typedef enum
		{
			SAS_REQUEST_INVALID,
			SAS_REQUEST_SERVER_REGISTER_INIT,     // When Invite is recv
			SAS_REQUEST_SERVER_REGISTER_COMPLETE, // When ACK is recv
			SAS_REQUEST_SERVER_DEREGISTER,        // When Bye is recv
			SAS_REQUEST_APP_MGMT                  // When Info is recv
		}t_INGwSipIfaceRequestType;

		INGwSpSipIface();

		~INGwSpSipIface();


		//
		// Following methods shall be used by SIP Provider
		//

		/*
	  * This method will be called by SIP provider after recveiving 
	  * INVITE, BYE or INFO message. g_TransitObj will 
	  * have XML body along with orignating SAS address
		*/
		int
		processSasMgmtRequest(t_INGwSipIfaceRequestType p_RequestType, g_TransitObj  &p_transitObj);

		/*
	  * This method will be called by SIP provider after recveiving 
	  * Notify message. g_TransitObj will 
	  * have XML body along with orignating SAS address
		*/
		int
		processOutboundMsg(g_TransitObj  &p_transitObj); 

		/*
	  * This method will be called by SIP provider after recveiving 
	  * failure response for Notify message. g_TransitObj will 
	  * have Tcap related tranx information
		*/
		void
		processSasErrResp(g_TransitObj  &p_transitObj); 

		//
		// Method to be used by IWF provider
		//

		/*
	  * This method will be called by IWF provider after consolidating 
	  * Dialogue and Components received for any call. g_TransitObj will 
	  * have encoded XML along with destination SAS address
		*/
		int
		processInboundMsg(g_TransitObj  &p_transitObj); 

		int
		processReplayInboundMsg(g_TransitObj  &p_transitObj); 

		/*
	  * This method will be called by IWF provider for transmitting
	  * management message IN_SERVICE, OUT_OF_SERVICE, ALLOWED, PROHIBITED. 
		* g_TransitObj will have encoded XML along with destination SAS address
		*/
		int
		sendSasAppResp(g_TransitObj  &p_transitObj); 

		inline void setIwfIface(INGwIwfIface* p_IwfIface)
		{
			 m_IwfIface = p_IwfIface;
		}

		inline INGwIwfIface * getIwfIface(void)
		{
			 return m_IwfIface;
		}

#ifdef INGW_LOOPBACK_SAS
    int
    initLoopbackDlgInfo(int p_loDlg, int p_hiDlg);
#endif

	private:

		int
		handleSasServerRegistrationInit(g_TransitObj  &p_transitObj);

		int
		handleSasServerRegistrationComplete(g_TransitObj  &p_transitObj);

		int
		handleSasServerDeregistration(g_TransitObj  &p_transitObj);

		int
		handleSasAppRegistration(g_TransitObj  &p_transitObj);

		int
		handleSasAppDeregistration(g_TransitObj  &p_transitObj);

		int
		handleSasAppMgmt(g_TransitObj  &p_transitObj);

		INGwIwfIface* m_IwfIface;

};

#endif //INGW_SP_SIP_IFACE_H_

