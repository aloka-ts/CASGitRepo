#ifndef _INGW_SIP_IFACE_H_
#define _INGW_SIP_IFACE_H_

#include <INGwIwf/INGwIwfBaseIface.h>
#include <INGwInfraUtil/INGwIfrUtlGlbInclude.h>

#include <string>
using namespace std;

class INGwSipIface: public INGwIwfBaseIface
{
	public:

		INGwSipIface();

		~INGwSipIface();

		int
		processSasInfo(g_TransitObj &p_transitObj);

		void
		getOpcSsnList(g_TransitObj &p_transitObj);

		int
		processOutboundMsg(g_TransitObj	&p_transitObj);

		//
		// Method to be used by TCAP provider
		//

		/*
	  * This method will be called by TCAP provider after consolidating 
	  * Dialogue and Components received for any new call. Note: Dialogue if 
	  * first received followed by component from stack.
		*/
		int
		processInboundMsg(g_TransitObj  &p_transitObj); 

		int
		sendSasAppResp(g_TransitObj &p_transitObj);

	private:

};

#endif

