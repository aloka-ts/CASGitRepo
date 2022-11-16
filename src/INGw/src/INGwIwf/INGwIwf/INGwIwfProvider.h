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
//     File:     INGwIwfProvider.h
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#ifndef _INGW_IWF_PROVIDER_H_
#define _INGW_IWF_PROVIDER_H_

#include <INGwIwf/INGwIwfBaseProvider.h>

class INGwIwfProvider : public virtual INGwIwfBaseProvider
{
	public:

		INGwIwfProvider();

		~INGwIwfProvider(); 

		static INGwIwfProvider&
		getInstance();

		int 
		changeState(INGwIwfBaseProvider::ProviderStateType p_state);

		int 
		startUp(void);

		void 
		getStatistics(std::ostrstream &output, int tabCount);

		inline INGwIwfBaseIface* getInterface() {
			return m_iwfIface;
		}

		inline int getProviderType() {
			return m_providerType;
		}


	protected:


	private:
		
		static INGwIwfProvider*	m_selfPtr;

		INGwIwfIface*						m_iwfIface;

		INGwIwfProvider(const INGwIwfProvider& p_conSelf);
		INGwIwfProvider& operator=(const INGwIwfProvider& p_conSelf);

};

#endif
