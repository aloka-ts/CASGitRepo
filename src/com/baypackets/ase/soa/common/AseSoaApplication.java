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
//      File:   AseSoaApplication.java
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
import java.net.URISyntaxException;
import java.util.*;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import com.baypackets.ase.soa.iface.SoaContext;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.soa.SoaContextImpl;


import org.apache.log4j.Logger;

public class AseSoaApplication {

	private String applicationName; 
	private String mainClassName;
	private String mainMethod;
	private HashMap<URI, Class> m_listenerInterfaceUri;
	private HashMap<Class, Class> m_listenerInterfaceImpl;
	private HashMap<URI, String> m_ifaceNameUri;
	private HashMap<String, String> m_ifaceImplName;

	private static Logger m_logger = Logger.getLogger(AseSoaApplication.class);

	public AseSoaApplication() {
		 m_listenerInterfaceUri = new HashMap();
		 m_listenerInterfaceImpl = new HashMap();
		 m_ifaceNameUri = new HashMap();
		 m_ifaceImplName = new HashMap();

	}

	public String getApplicationName() {
		return this.applicationName;
	}

	public String getMainClassName() {
		return this.mainClassName;
	}

	public String getMainMethod() {
		return this.mainMethod;
	}

	public String getListenerImpl(String listenerInterface)	{
		return m_ifaceImplName.get(listenerInterface);
	}

	public HashMap<URI, Class> getListenerUriApi() {
		return this.m_listenerInterfaceUri;
	}

	public HashMap<Class, Class> getListenerImplApi() {
		return this.m_listenerInterfaceImpl;
	}

	public void setApplicationName(String name) {
		this.applicationName = name;
	}

	public void setMainClassName(String className) {
		this.mainClassName = className;
	}

	public void setMainMethod(String name) {
		this.mainMethod = name;
	}

	public void addListener(String listenerInterface, String listenerImpl, String listenerUri) {
			if(m_logger.isDebugEnabled())
			m_logger.debug("Inside AseSoaApplication : listenerInterface = "+listenerInterface+" listenerImpl = "+listenerImpl+" listenerUri= "+listenerUri);	
		try {
			if( (listenerInterface != null) && (listenerUri != null) && (listenerImpl != null) ) {
				URI uri = new URI(listenerUri);
			
				m_ifaceNameUri.put(uri, listenerInterface);
				m_ifaceImplName.put(listenerInterface, listenerImpl);
			}
		}catch(Exception e) {
				if(m_logger.isDebugEnabled())
				m_logger.debug(e.getMessage(),e);
		}
	}

	public void initializeListener(DeployableObject deployable) {
		try {
			if(! m_ifaceNameUri.isEmpty()) {
				Iterator itor = m_ifaceNameUri.entrySet().iterator();
				while(itor.hasNext()) {
					Map.Entry entry = (Map.Entry) itor.next();
					Class ifaceClazz = Class.forName((String) entry.getValue(), true, deployable.getClassLoader());
					this.m_listenerInterfaceUri.put((URI) entry.getKey(), ifaceClazz);
				}
			}
			if(! m_ifaceImplName.isEmpty()) {
				Iterator itor = m_ifaceImplName.entrySet().iterator();
				while(itor.hasNext()) {
					Map.Entry entry = (Map.Entry) itor.next();
					Class ifaceClazz = Class.forName((String) entry.getKey(), true, deployable.getClassLoader());
					Class implClazz = Class.forName((String) entry.getValue(), true, deployable.getClassLoader());
					this.m_listenerInterfaceImpl.put(implClazz, ifaceClazz);
				}
			}
		}catch(Exception e) {
			if(m_logger.isDebugEnabled())
			m_logger.debug(e.getMessage(), e);
		}
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("AseSoaApplication [Name=");
		buffer.append(applicationName);
		buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		return buffer.toString();
	}

}
	 
	



