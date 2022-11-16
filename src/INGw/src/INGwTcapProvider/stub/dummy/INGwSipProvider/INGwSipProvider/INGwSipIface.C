#include <INGwSipProvider/INGwSipIface.h>

#include <string>
using namespace std;

INGwSipIface::INGwSipIface():INGwIwfBaseIface(INGwIwfBaseIface::SIP)
{
}

INGwSipIface::~INGwSipIface()
{
}

int
INGwSipIface::processInboundMsg(g_TransitObj	&p_transitObj)
{
	int retVal;
	if (NULL != p_transitObj.m_buf) {
		printf("Send NOTIFY...with SDP to SAS ip [%s]\n", p_transitObj.m_sasIp.c_str());
		printf ("XML Len[%d]\n%s\n", p_transitObj.m_bufLen, p_transitObj.m_buf);
	}
	else {
		printf ("SHOULD BE SENDING NOTIFY BUT m_buf IS NULL\n");
	}
	return retVal;
}

int
INGwSipIface::sendSasAppResp(g_TransitObj &p_transitObj)
{
	int retVal;

	printf ("\n\n");

	if (NULL != p_transitObj.m_buf) {
		printf("Send INFO...with SDP to SAS ip [%s]\n", p_transitObj.m_sasIp.c_str());
		printf ("XML Len[%d]\n%s\n", p_transitObj.m_bufLen, p_transitObj.m_buf);
	}
	else {
		printf ("SHOULD BE SENDING INFO BUT m_buf IS NULL\n");
	}
	return retVal;
}

