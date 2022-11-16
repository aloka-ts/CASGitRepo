#include <INGwSipProvider/INGwSipProvider.h>

INGwSipProvider* INGwSipProvider::m_selfPtr = NULL;

INGwSipProvider::INGwSipProvider():INGwIwfBaseProvider(1)
{
}

INGwSipProvider::~INGwSipProvider()
{
}

INGwSipProvider&
INGwSipProvider::getInstance()
{
	if (m_selfPtr == NULL) {
		m_selfPtr = new INGwSipProvider();
	}
	return *m_selfPtr;
}

int 
INGwSipProvider::changeState(INGwIwfBaseProvider::ProviderStateType p_state)
{
	int retVal =0;
	return retVal;
}

int 
INGwSipProvider::startUp(void)
{
	int retVal =0;
	return retVal;
}

void 
INGwSipProvider::getStatistics(std::ostrstream &output, int tabCount)
{
}


