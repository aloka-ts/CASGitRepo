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
//     File:     INGwIwfBaseProvider.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_IWF_BASE_PROVIDER_H_
#define _INGW_IWF_BASE_PROVIDER_H_

#include <INGwInfraUtil/INGwIfrUtlConfigurable.h>
#include <INGwIwf/INGwIwfIface.h>

#include <strstream>

const int PROVIDER_TYPE_IWF = 1;
const int PROVIDER_TYPE_SIP = 2;
const int PROVIDER_TYPE_TCAP = 3;

class INGwIwfBaseProvider : public virtual INGwIfrUtlConfigurable
{
	public:

		/**
		* Possible state of any provider
		*/
 		enum ProviderStateType {
			PROVIDER_STATE_STOPPED = 0,
      PROVIDER_STATE_LOADED  = 1,
      PROVIDER_STATE_RUNNING = 2
		};

		INGwIwfBaseProvider(int p_providerType) {
			m_providerType = p_providerType;
		}

		virtual ~INGwIwfBaseProvider() { }

		/**
		* Method for notifying state of Provider.
		*/
		virtual int 
		changeState(INGwIwfBaseProvider::ProviderStateType p_state) = 0;

		/**
		* Method is called on receivng startUp from EMS Agent
		*/
		virtual int 
		startUp(void) = 0;

		/**
		* Method to fetch statistics
		*/
		virtual void 
		getStatistics(std::ostrstream &output, int tabCount) = 0;

		/**
		* Method to get Interface Object associated with Provider
		*/
		virtual INGwIwfBaseIface*
		getInterface() = 0;

		/**
		* INLINE Methods
		*/
		inline int getProviderType() {
			return m_providerType;
		}

	protected:

		int                         m_providerType;

	private:

		INGwIwfBaseProvider(const INGwIwfBaseProvider& p_conSelf);
		INGwIwfBaseProvider& operator=(const INGwIwfBaseProvider& p_conSelf);

};

#endif
