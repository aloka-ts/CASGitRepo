#ifndef _INGW_SIP_PROVIDER_H_
#define _INGW_SIP_PROVIDER_H_

#include <INGwIwf/INGwIwfBaseProvider.h>

class INGwSipProvider : public virtual INGwIwfBaseProvider
{
	public:

		INGwSipProvider();

		~INGwSipProvider(); 

		static INGwSipProvider&
		getInstance();

		int 
		changeState(INGwIwfBaseProvider::ProviderStateType p_state);

		int 
		startUp(void);

		void 
		getStatistics(std::ostrstream &output, int tabCount);

		inline INGwIwfBaseIface* getInterface() {
			return m_sipIface;
		}

		inline int getProviderType() {
			return m_providerType;
		}

	protected:


	private:
		
		static INGwSipProvider*	m_selfPtr;

		INGwSipIface*						m_sipIface;

		INGwSipProvider(const INGwSipProvider& p_conSelf);
		INGwSipProvider& operator=(const INGwSipProvider& p_conSelf);

};

#endif
