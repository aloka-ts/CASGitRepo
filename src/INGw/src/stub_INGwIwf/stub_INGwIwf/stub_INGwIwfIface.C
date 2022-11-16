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
//     File:     INGwIwfIface.C
//
//     Desc:     <Description of file>
//
//     Author           Date          Description
//    ---------------------------------------------------------
//     Rajeev Arya     23/11/07     Initial Creation
//********************************************************************
#include <INGwInfraUtil/INGwIfrUtlLogger.h>
BPLOG("INGwIwf");

#include <INGwIwf/INGwIwfIface.h>

/**
* Constructor
*/
INGwIwfIface::INGwIwfIface():INGwIwfBaseIface(INGwIwfBaseIface::IWF)
{
}

/**
* Destructor
*/
INGwIwfIface::~INGwIwfIface()
{
}

void
INGwIwfIface::setTcapIface(INGwIwfBaseIface *p_iface)
{
	//m_tcapIface = static_cast<INGwTcapIface*>(p_iface);
}

void
INGwIwfIface::setSipIface(INGwIwfBaseIface *p_iface)
{
	m_sipIface = static_cast<INGwSpSipIface*>(p_iface);
}

/**
* Description : This method will be called on receiving INFO message with SSN 
*						    and  OPC. SIP provider will maintain map of SIP called and SAS 
*							  Info (FIP, Port, Session ID, Contact etc) and will pass the call
*							  ID to IWF. IWF will further pass this information to Load 
*								distributor to update its mapping info. 
*
* @param <p_selfPointCode> - OPC
* @param <p_selfSsn> 	     - SSN associated with OPC
* @param <p_sasCallId> 	   - Call ID received in SIP INVITE
*
* @return <bool> 					 - true | false
*
*/
int
INGwIwfIface::processSasInfo(g_TransitObj &p_transitObj)
{

	std::string opcXmlStr = std::string(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"
						"<tcap>\n"
							"<configuration>\n"
								"<local-user-address sub-system-number=\"241\">\n"
									"<signaling-point-code cluster=\"111\" member=\"5\" zone=\"1\"/>\n"
								"</local-user-address>\n"
							"</configuration>\n"
						"</tcap>\n"
						);
 char * buff = new char[opcXmlStr.length() +1]; 
 strncpy(buff, opcXmlStr.c_str(), opcXmlStr.length());
 buff[opcXmlStr.length()] = '\0';
 p_transitObj.m_buf = (unsigned char*)buff;
 p_transitObj.m_bufLen = opcXmlStr.length();
 p_transitObj.m_causeCode = G_ALREADY_REG;

	return 0;
//	return (m_tcapIface->processSasInfo(p_transitObj));
}

/**
* Description : This method will be called on receiving INVITE message to get 
*								list of registered OPC-SSN List. IWF will further call 
*								getOpcSsnList(0 from  load distributor. SIP provider should 
*								then form XML and send it in 200 OK.
*
* @return <List<OpcSsnList>> - List of OPC/SSn
*/
void
INGwIwfIface::getOpcSsnList(g_TransitObj &p_transitObj)
{
	std::string opcXmlStr = std::string(
					"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"
						"<tcap>\n"
							"<configuration>\n"
								"<local-user-address sub-system-number=\"241\">\n"
									"<signaling-point-code cluster=\"111\" member=\"5\" zone=\"1\"/>\n"
								"</local-user-address>\n"
							"</configuration>\n"
						"</tcap>\n"
						);
 char * buff = new char[opcXmlStr.length() +1]; 
 strncpy(buff, opcXmlStr.c_str(), opcXmlStr.length());
 buff[opcXmlStr.length()] = '\0';
 p_transitObj.m_buf = (unsigned char*)buff;
 p_transitObj.m_bufLen = opcXmlStr.length();
 p_transitObj.m_causeCode = G_SUCCESS;

//	return (m_tcapIface->getOpcSsnList(p_transitObj));
}

/**
* Description : This method will be called on receiving NOTIFY message which 
*								shall be used for sending TCAP message. SIP provider will 
*								first decode XML parser and form an object and pass it IWF to 
*								process it.
*
* @param <p_callInfoInfoObj> - 
* @param <p_causeCode> 	     - Return CauseCode
*
* @return <bool>  - true | false
*/
int
INGwIwfIface::processOutboundMsg(g_TransitObj &p_transitObj)
{
  p_transitObj.m_causeCode = G_SUCCESS;
	return 0;
//	return (m_tcapIface->processOutboundCall(p_transitObj));
}

int
INGwIwfIface::deregisterSas(g_TransitObj &p_transitObj)
{
	return 0;
//	return (m_tcapIface->deregisterSas(p_transitObj));
}

/**
* Description : This method will be called by TCAP provider after consolidating 
* 							Dialogue and Components received for any new call. 
*								Note: Dialogue if first received followed by component from 
*								stack.
*
* @param <p_callInfoObj> - OPC
*
* @return <bool> 					 - true | false
*/
int
INGwIwfIface::processInboundMsg(g_TransitObj  &p_transitObj)
{

std::string inputFromSasUser;
inputFromSasUser += "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n";
inputFromSasUser += "<tcap>\n";
inputFromSasUser += " <dialogue-req-event>\n";
inputFromSasUser += "   <begin-req-event dialogue-id=\"1233333333\" quality-of-service=\"0\">\n";
inputFromSasUser += "     <component-req-event>\n";
inputFromSasUser += "       <invoke-req-event class-type=\"CLASS_1\" dialogue-id=\"39467\" invoke-id=\"1\" last-component=\"true\" last-invoke-event=\"true\">\n";
inputFromSasUser += "         <operation operation-type=\"OPERATIONTYPE_LOCAL\">\n";
inputFromSasUser += "           <operation-code>00</operation-code>\n";
inputFromSasUser += "         </operation>\n";
inputFromSasUser += "         <parameters parameter-identifier=\"PARAMETERTYPE_SET\">\n";
inputFromSasUser += "           <parameter>3015800104820703100810325486830703102143658719</parameter>\n";
inputFromSasUser += "         </parameters>\n";
inputFromSasUser += "       </invoke-req-event>\n";
inputFromSasUser += "     </component-req-event>\n";
inputFromSasUser += "     <destination-address national-use=\"true\" routing-indicator=\"ROUTING_SUBSYSTEM\">\n";
inputFromSasUser += "       <sub-system-address sub-system-number=\"230\">\n";
inputFromSasUser += "         <signaling-point-code cluster=\"111\" member=\"4\" zone=\"1\"/>\n";
inputFromSasUser += "       </sub-system-address>\n";
inputFromSasUser += "     </destination-address>\n";
inputFromSasUser += "     <originating-address national-use=\"true\" routing-indicator=\"ROUTING_SUBSYSTEM\">\n";
inputFromSasUser += "      <sub-system-address sub-system-number=\"240\">\n";
inputFromSasUser += "       <signaling-point-code cluster=\"111\" member=\"5\" zone=\"1\"/>\n";
inputFromSasUser += "      </sub-system-address>\n";
inputFromSasUser += "     </originating-address>\n";
inputFromSasUser += "   </begin-req-event>\n";
inputFromSasUser += " </dialogue-req-event>\n";
inputFromSasUser += "</tcap>\n";

  char * buff = new char[inputFromSasUser.length() +1];
  strncpy(buff, inputFromSasUser.c_str(), inputFromSasUser.length());
  buff[inputFromSasUser.length()] = '\0';
  p_transitObj.m_buf = (unsigned char*)buff;
  p_transitObj.m_bufLen = inputFromSasUser.length();
  p_transitObj.m_causeCode = G_SUCCESS;
	int retVal =  (m_sipIface->processInboundMsg (p_transitObj));
  delete [] buff;
  buff = NULL;
  return retVal;
}


int
INGwIwfIface::sendSasAppResp(g_TransitObj &p_transitObj)
{
	return (m_sipIface->sendSasAppResp(p_transitObj));
}
