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
//      File:   AseRemoteService.java
//
//      Desc:   This class contains the details of the remote service.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Suresh Kr. Jangir               17/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.provisioner;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;

import com.baypackets.ase.util.AseStrings;


public class AseRemoteService {

	private String name;
	private String version;
	private URI wsdlURL;
	private List<URI> serviceUris  = new ArrayList<URI>();

	public AseRemoteService() {
	}

	public AseRemoteService(String name, String version, URI uri) {
		this.name = name;
		this.version = version;
		this.wsdlURL = uri;
	}

	public String getServiceName() {
		return this.name;
	}

	public String getVersion() {
		return this.version;
	}

	public URI getWsdlUri() {
		return this.wsdlURL;
	}

	public List<URI> getServiceUris() {
		return this.serviceUris;
	}

	public void setServiceName(String name) {
		this.name = name;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setWsdlUri(URI uri) {
		this.wsdlURL = uri;
	}

	public void addServiceUri(URI uri) {
		this.serviceUris.add(uri);
	}

	public void clearServiceUris() {
		this.serviceUris.clear();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Remote Service [Name=");
		buffer.append(this.name);
		buffer.append(", Version=");
		buffer.append(this.version);
		buffer.append(", WSDL URL=");
		buffer.append(this.wsdlURL);
		buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		return buffer.toString();
	}
}

