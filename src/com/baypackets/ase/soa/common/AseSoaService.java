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
//      File:   AseSoaService.java
//
//      Desc:   This class contains the details specific to one service element in a Soa Service. 
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Suresh Kr. Jangir               17/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.common;

import java.net.URI;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseStrings;



public class AseSoaService {

	private String serviceName; 
	private boolean isAnnotated;
	private String implClassName;
	private String serviceApi;
	private String remoteServiceUri;
	private String servicePath;
	private String notificationApi;
	private List m_ports = new ArrayList();
	private Logger m_logger = Logger.getLogger(AseSoaService.class);

	public AseSoaService() {
		
	}

	public String getServiceName() {
		return this.serviceName;
	}

	public String getImplClassName() {
		return this.implClassName;
	}

	public String getServiceApi() {
		return this.serviceApi;
	}

	public String getServicePath() {
		return this.servicePath;
	}

	public String getNotificationApi() {
		return this.notificationApi;
	}

	public boolean isAnnotated() {
		return this.isAnnotated;
	}

	public void setServiceName(String name) {
		this.serviceName = name;
	}

	public void setImplClassName(String implClassName) {
		this.implClassName = implClassName;
	}

	public void setServiceApi(String name) {
		this.serviceApi = name;
	}

	public void setRemoteServiceUri(String uri)	{
		remoteServiceUri = uri;
	}

	public String getRemoteServiceUri()	{
		return remoteServiceUri;
	}

	public void setServicePath(String path) {
		this.servicePath = path;
	}

	public void setNotificationApi(String api) {
		this.notificationApi = api;
	}

	public void setAnnotated(boolean ann) {
		this.isAnnotated = ann;
	}

	public String getServiceUri() {
		return ((ServicePort)this.m_ports.get(0)).getLocationUri();
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("AseSoaService [");
		buffer.append("Name=");
		buffer.append(serviceName);
		buffer.append(", Service API=");
		buffer.append(serviceApi);
		buffer.append(", Notification API=");
		buffer.append(notificationApi);
		buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		return buffer.toString();
	}

	public void addPort(String name, String binding, String uri) {
		ServicePort port = new ServicePort();
		port.setPortDetails(name,binding,uri);
		m_ports.add(port);
	}

	public List getPorts() {
		return m_ports;
	}

	public ServicePort getServicePort(String name) {
		ServicePort port = null;
		Iterator it = m_ports.iterator();
		while(it.hasNext()) {
			ServicePort tmpPort = (ServicePort)it.next();
			if(name.equals(tmpPort.getName())) {
				port = tmpPort;
				break;
			}
		}
		return port;
	}

	class ServicePort {
		String name;
		String binding;
		String locationUri;

		public ServicePort() {
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getBinding() {
			return this.binding;
		}

		public void setBinding(String binding) {
			this.binding = binding;
		}

		public String getLocationUri() {
			return this.locationUri;
		}

		public void setLocationUri(String uri) {
			this.locationUri = uri;
		}

		public void setPortDetails(String name, String binding, String uri) {
			this.name = name;
			this.binding = binding;
			this.locationUri = uri;
		}

	}

}
	 
	



