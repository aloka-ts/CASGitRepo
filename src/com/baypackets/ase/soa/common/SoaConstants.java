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
//      File:   SoaConstants.java
//
//      Desc:   This file defines constants  to be used for SOA framework support coding. 
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  18/12/07        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.soa.common;

public class SoaConstants	{

	public static final int OPERATION_DEPLOY = 1; 	
	public static final int OPERATION_PROVISION = 2; 	
	public static final int OPERATION_UPGRADE = 3; 	
	public static final int OPERATION_UPDATE = 4; 	
	public static final int WS_TYPE_SERVICE = 1; 	
	public static final int WS_TYPE_APP = 2; 	
	public static final String PROXY_SERVICE = "svc_proxy"; 	
	public static final String PROXY_REMOTE_SERVICE = "remote_svc_proxy"; 	
	public static final String  PROXY_REMOTE_LISTENER = "remote_lsnr_proxy"; 	
	public static final String  PROXY_LOCAL_LISTENER = "local_lsnr_proxy"; 	
	public static final String  PROXY_CLIENT_LISTENER = "client_lsnr_proxy";
	public static final String NAME_SOAP_SERVER = "soapserver";
	public static final String SOAP_SERVER_AXIS = "axis";
	public static final String GENERATE_SOAP_SERVICE_INTERFACE = "service_interface";
	public static final String GENERATE_SOAP_STUB = "stub";
	public static final String GENERATE_SOAP_SKELETON = "skeleton";
	public static final String GENERATE_SOAP_BOTH = "both";
	public static final String NAME_PROXY_FILE_SUFFIX = "Proxy.java";
	public static final String NAME_CLIENT_PROXY_FILE_SUFFIX = "ClientProxy.java";
	public static final String NAME_LOCAL_PROXY_FILE_SUFFIX = "LocalProxy.java";
	public static final String NAME_REMOTE_PROXY_FILE_NAME_SUFFIX = "RemoteProxy.java";
	public static final String JAVA_SOURCE_DIR_NAME = "src";
	public static final String JAVA_CLASS_DIR_NAME = "WEB-INF/classes";
    public static final String WSDL_DIR_NAME = "WEB-INF/wsdl";
	public static final String NAME_SOA_DD = "soa.xml";
	public static final String NAME_CUSTOM_PKG_STUB_SKEL = "com.baypackets.ase.soa.";
	public static final String PROP_PROVISION_REMOTE_SERVICE = "provision.remote.service.enable";
	public static final String NAME_SOA_CONTEXT = "com.baypackets.ase.soa.iface.SoaContext";
	public static final String SOAP_SERVER_AXIS_VERSION = "2.0";	
} 	
