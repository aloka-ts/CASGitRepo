//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************


//***********************************************************************************
//
//      File:   SoapServerFactory.java
//
//      Desc:   This file acts as a factory for creating and returning an appropriate SoapServer
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh				18/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.common;

import org.apache.log4j.Logger;

public class SoapServerFactory	{
	private static Logger m_logger = Logger.getLogger(SoapServerFactory.class);

	private static SoapServer m_axisSoapServer = new AxisSoapServer();

	public static SoapServer getSoapServer(String soapServerName) {
		if(soapServerName.equalsIgnoreCase(SoaConstants.SOAP_SERVER_AXIS)) {
			return m_axisSoapServer;
		}

		m_logger.error("No soap server found with name: " + soapServerName);
		return null;
	}
}
