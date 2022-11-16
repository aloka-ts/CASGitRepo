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
//      File:   SoaContextImpl.java
//
//      Desc:   This file extends abstract class com.baypackets.ase.soa.iface.SoaContext 
//				It also provides additional methods used by other SAS components
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Somesh Kr. Srivastava           18/12/07        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.soa;

import com.baypackets.ase.soa.iface.SoaContext; 
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import com.baypackets.ase.soa.SoaMeasurementUtil;
import com.baypackets.ase.soa.codegenerator.proxy.BaseProxy;
import com.baypackets.ase.soa.codegenerator.CodeGenerator;
import com.baypackets.ase.soa.exceptions.SoaException;
import com.baypackets.ase.soa.common.WebServiceDataObject;
import com.baypackets.ase.soa.common.AseSoaService;
import com.baypackets.ase.soa.common.AseSoaApplication;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.sipconnector.AseSipConnector;

import com.baypackets.ase.util.Constants;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class extends abstract class com.baypackets.ase.soa.iface.SoaContext. It also 
 * provides additional methods used by other SAS components.
 *
 * @author Somesh Kr. Srivastava
 */
public class SoaContextImpl extends SoaContext {

	private String m_name;
	private Object m_deployableObj = null;
	private Map<URI, Class> m_serviceInterfaces = new HashMap();
	private Map<String, String> m_parameters = new HashMap();
	private Map<Object, Object> m_allowSelfRegistrationMap = new HashMap();
	private Map<URI, Object> m_services = new Hashtable();
	private Map<URI, Object> m_listeners = new Hashtable();
	private Map<Class, Object> m_listenerProxies = new HashMap();
	private WebServiceDataObject m_webServiceDataObj = null;
	private AseSoaApplication m_soaApplication = null;
	
	private static Logger m_logger = Logger.getLogger(SoaContextImpl.class);
	private ConfigRepository config;	
	
	/**
	 * Constructs the SoaContextImpl object with specified name
	 *
	 * @param p_name  Uniquely identifies this SoaContextImpl
	 * @param p_parameters Configuration parameters 
	 */
	public SoaContextImpl(String p_name, Map<String,String> p_parameters) {
		this.m_name = p_name;
		try {
			if(p_parameters != null) {
				Iterator iterator = p_parameters.entrySet().iterator(); 
				while(iterator.hasNext()) {
		 			Map.Entry entry = (Map.Entry) iterator.next();   
		 			m_parameters.put((String)entry.getKey(), (String)entry.getValue());
		 		}
			}
		}catch(UnsupportedOperationException e1) {
			m_logger.error(e1.getMessage(), e1);
		}catch(ClassCastException e2) {
			m_logger.error(e2.getMessage(), e2);
		}catch(NullPointerException e3) {
			m_logger.error(e3.getMessage(), e3);
		}catch(IllegalArgumentException e4) {
			m_logger.error(e4.getMessage(), e4);
		}
	}

	/**
	 * This method retrieves SoaContext from SoaFrameworkContext
	 *
	 * @param p_name name of the deployed service
	 * @returns SoaContext object associated with this name in SoaFrameworkContext.
	 */
	/*public static SoaContext getSoaContext(String p_name) {
		 SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);	 
		 return soaFw.getSoaContext(p_name);
	}*/

	/**
	 * This method find URI in m_serviceInterface for the given Class.
	 *
	 * @param p_class service Interface class
	 * @return URI of the service interface class
	 */
	public URI getServiceURI(Class p_servInfClass) {
		URI uri = null;
		if(m_serviceInterfaces.containsValue(p_servInfClass)) {
			Iterator iterator = m_serviceInterfaces.entrySet().iterator();

			while(iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				if(entry.getValue() == p_servInfClass) {
					uri = (URI) entry.getKey();
				}
			}
		}
		return uri;
	}

	public void setServiceURI(URI p_uri, Class p_servInfClass) {
		if( (p_uri == null) || (p_servInfClass == null) ) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug(" key=" +p_uri+ "  value=" +p_servInfClass);
			}
			throw new IllegalArgumentException("either key of value is null");
		}
		if(m_serviceInterfaces.containsKey(p_uri)) {
			if(m_logger.isDebugEnabled()) {
            	m_logger.debug("given uri is=" +p_uri);
            }
            throw new IllegalArgumentException("given uri is already present");
        }	
		synchronized(m_serviceInterfaces) {
			m_serviceInterfaces.put(p_uri, p_servInfClass);
		}
		if(m_logger.isDebugEnabled()) {
        	m_logger.debug("service interface class " +p_servInfClass+ " added with the URI " +p_uri);
        } 
	}


	/**
	 * Using this method a service can register its implementation object corresponding to given URI
	 * 
	 * @param p_uri URI of the service implementation object
	 * @param p_object the service implementation object
	 * @throws IllegalStateException if selfRegistration map has no entry for given URI
	 * @throws IllegalStateException if entry of implementation is given in soa.xml discriptor file.
	 */
	public void registerService(URI p_uri, Object p_object) throws IllegalStateException, IllegalArgumentException {
		
		if( !(m_allowSelfRegistrationMap.containsKey(p_uri)) ) {
			throw new IllegalStateException("selfRegistration map has no entry corresponding to given URI");
		}
		Boolean bl = (Boolean) m_allowSelfRegistrationMap.get(p_uri);
		if( ! bl.booleanValue()) {
			throw new IllegalStateException("implementation object has already been initialized as it was specified in DD");
		}

		Class serv_interface = m_serviceInterfaces.get(p_uri);
		if(serv_interface == null) {
			throw new IllegalArgumentException("wrong URI");
		}
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("TESTING: class of the argument is "+p_object.getClass());
			m_logger.debug("TESTING: interface is = "+serv_interface);
		}
		

		//if(p_object.getClass() == serv_interface.getClass()) {
		if( serv_interface.isInstance(p_object) ) {
			SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT); 
			ServiceMap servMap = soaFw.getServiceMap();
			Object serv_proxy = servMap.getService(p_uri);
			
			if(serv_proxy == null) {
				throw new IllegalArgumentException("wrong URI");
			}else {
				((BaseProxy) serv_proxy).setImpl(p_object);
				m_allowSelfRegistrationMap.put(p_uri, false);
				servMap.addImplObject(serv_interface.getName(), p_object);
				try {
					servMap.activateService(p_uri);
					if(m_logger.isDebugEnabled())
					m_logger.debug("registration successfull");
				} catch(SoaException e) {
					m_logger.error("Unable to activate Service",e);
				}
				
			}
		}else {
			throw new IllegalArgumentException("Object is not an instatnce of interface");
		}
	}

	/**
	 * Using this method a service can find desired listener object by giving the URI
	 * and listener interface class.
	 *
	 * @param p_uri uri of the listener object
	 * @param p_class listener interface class
	 */
	public synchronized Object findListener(URI p_uri, Class p_class) {
		SoaMeasurementUtil.incrementTotalFindListenerInvoke();
		if(m_listeners.containsKey(p_uri)) {
			return m_listeners.get(p_uri);
		}
		
		Object clientListProxy = m_listenerProxies.get(p_class);
		if(clientListProxy == null) {
			m_logger.error("No SOA proxy found for listener class: " + p_class.getName());
			throw new IllegalArgumentException("Listener class proxy not found");
		}
		
		Object proxy = null;
		
		try {
			proxy = ((BaseProxy)clientListProxy).clone();
			((BaseProxy) proxy).setURI(p_uri.toString());
		} catch(Exception e) {
			m_logger.error("Unable to clone",e);
		}
		
		m_listeners.put(p_uri, proxy);
		return proxy;
	}


	/**
	 * Searches m_listenerInterfaceMap and returns the URI matching the given class,
	 * if found one. Otherwise, it returns null.
	 * 
	 * @param p_class listener interface class
	 * @return URI of the listener interface class if found, null otherwise
	 */
	public URI getListenerURI(Class p_class) {
		URI uri = null;   
		Map<URI, Class> map = this.m_webServiceDataObj.getApplication().getListenerUriApi();
		if(map.containsValue(p_class)) {
		Iterator iterator = map.entrySet().iterator(); 
			while(iterator.hasNext()) {
		 		Map.Entry entry = (Map.Entry) iterator.next();   
		 		if(entry.getValue() == p_class) {    
		 			uri = (URI) entry.getKey();   
		 		}
		 	}
		 }
		 return uri;
	}

	/**
	 * An application can find the desired service object by specifying the service URI and 
	 * interface class.
	 *
	 * @param p_uri service URI
	 * @param p_class service interface class
	 */
	public synchronized Object findService(URI p_uri, Class p_class) {
		SoaMeasurementUtil.incrementTotalFindServiceInvoke();
		SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);             
		if(p_uri == null) {
			throw new IllegalArgumentException("URI is NULL");
		}	
		ServiceMap servMap = soaFw.getServiceMap();
		Object servProxy = servMap.findService(p_uri);
		if(servProxy == null) {
			return null;
		}
		Object sCliProxy = m_services.get(p_uri);
		if(sCliProxy != null) {
			if( ((BaseProxy) sCliProxy).getImpl() == servProxy) {
				return sCliProxy;
			}
		}
		CodeGenerator codGen = soaFw.getCodeGenerator();
		try	{
			sCliProxy = codGen.generateClientProxy(p_class,p_uri);
		}catch(Exception exp)	{
			throw new IllegalArgumentException(exp.getMessage());
		}
		if(sCliProxy == null) {
			throw new IllegalArgumentException("Code Generator Error");
		}
		((BaseProxy) sCliProxy).setImpl(servProxy);
		m_services.put(p_uri, sCliProxy);
		return sCliProxy;
	}

	/**
	 * @return unique name as String associated with the SoaContextImpl object
	 */
	public String getName() {
		return this.m_name;
	}

	/**
	 * @return iterator over the set of keys present in m_parameters map.
	 */
	public Iterator<String> getParameters() {
		return m_parameters.keySet().iterator();
	}

	/**
	 * This method returns the value of the parameter of the given name as String
	 * 
	 * @param p_name name of the parameter
	 * @return value of the parameter String 
	 */
	public String getParameterValue(String p_name) {
		return (String) m_parameters.get(p_name);
	}

	/**
	 * This method returns the current role of the container by getting it as
	 * value of Constants.OID_CURRENT_ROLE from ConfigRepository. The returned value is 
	 * ROLE_ACTIVE or ROLE_STANDBY.
	 *
	 * return ROLE_ACTIVE or ROLE_STANDBY int
	 */
	public int getContainerRole() {
		//this.config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		//int role = Integer.parseInt(this.config.getValue(Constants.OID_CURRENT_ROLE));
		AseSipConnector connector = (AseSipConnector)Registry.lookup("SIP.Connector");
		Short role = connector.getRole();

		if(role == AseRoles.ACTIVE ) {
			return ROLE_ACTIVE;
		}else {
			return ROLE_STANDBY;
		}
	}

	/**
	 * This method retrieves value of service proxy object from Service Map and 
	 * then returns the value of associated implementation object
	 *
	 * @param p_URI URI of the service proxy object
	 * @return implementation object associated with the service proxy object
	 */
	public Object getServiceImpl(URI p_uri) {
		 SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);   
		 ServiceMap servMap = soaFw.getServiceMap(); 
		 Object servProxy = servMap.findService(p_uri); 
		 
		 if(servProxy == null) {  
		 	//throw new SoaException("service not found");
			return null;
		 }

		 return ((BaseProxy) servProxy).getImpl();
	}

	/**
	 * This method returns iterator over set of key-value pair in m_serviceInterfaces.
	 *
	 * @return iterator over set<key, value> of the map of interface classes exposed by SOA 
	 * service and URI as key
	 */
	public Iterator<Map.Entry<URI, Class>> getServices() {
		return m_serviceInterfaces.entrySet().iterator();
	}

	/**
	 * This method returns iterator over Set of key-value pairs in Map<URI, Class> contained
	 * by associated AseSoaApplication.
	 *
	 * @return iterator over set<key, value> of the map of interface classes exposed by SOA
	 * application with URI as key
	 */
	public Iterator<Map.Entry<URI, Class>> getListeners() {
		Map<URI, Class> map = this.m_webServiceDataObj.getApplication().getListenerUriApi();
		return map.entrySet().iterator();
	}

	/**
	 * Using this method a generated ServiceProxy object is registered with SoaContextImpl.
	 *
	 * @param p_uri URI of the ServiceProxy object
	 * @param p_serviceProxy Object, ServiceProxy object
	 * @param P_allowSelfRegistration boolean flag indicating whether a service can 
	 * register its own implementation object or not.
	 * @param p_isUpgrade boolean flag indicating whether the ServiceProxy Object 
	 * added or upgraded in ServiceMap.
	 */
	public void registerServiceProxy(URI p_uri, Object p_serviceProxy, 
								boolean p_allowSelfRegistration, boolean p_isUpgrade){

		SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);  
		ServiceMap servMap = soaFw.getServiceMap(); 

		if(!p_isUpgrade) {
			try {
				servMap.addService(p_uri, p_serviceProxy);
				if(m_logger.isDebugEnabled())
				m_logger.debug("service proxy object added in serviceMap" +p_serviceProxy);
			} catch(SoaException e) {
				m_logger.error("Unable to add Service proxy object",e);
			}
		}else {
			servMap.upgradeService(p_uri, p_serviceProxy);
			if(m_logger.isDebugEnabled())
			m_logger.debug("Upgradation done");
		}
		if(!p_allowSelfRegistration) {
			try {
				servMap.activateService(p_uri);
				if(m_logger.isDebugEnabled())
				m_logger.debug("Service activated");
			} catch(SoaException e) {
				m_logger.error("Unable to activate Service",e);
			}
		}

		m_allowSelfRegistrationMap.put(p_uri, p_allowSelfRegistration);
	}

	/**
	 * Using this method a generated ListenerProxy object is registered with SoaContextImpl.
	 * 
	 * @param  p_uri URI of the ListenerProxy object
	 * @param p_listenerProxy ListenerProxy Object
	 * @param p_isUpgrade boolean flag indicating whether the ListenerProxy Object should be 
	 * added or upgraded in ServiceMap.
	 */
	public void registerListenerProxy(URI p_uri, Object p_listenerProxy, boolean p_isUpgrade) {
			
		SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);  
		ListenerRegistry listenerReg = soaFw.getListenerRegistry();
		
		
		if(!p_isUpgrade) {
			try {
				listenerReg.addListener(p_uri, p_listenerProxy);
				if(m_logger.isDebugEnabled())
				m_logger.debug("Registration done-> added in listener registry");
			} catch(SoaException e) {
				m_logger.error(e.getMessage(), e);
			}
		}else {
			listenerReg.upgradeListener(p_uri, p_listenerProxy);
			if(m_logger.isDebugEnabled())
			m_logger.debug("Registration done-> upgraded");
		}
	}

	/**
	 * This method adds a Class-Object pair of ListenerClientProxy into m_listenerProxies map
	 * @param p_class Class 
	 * @param p_listenerClientProxy ListenerClientProxy Object
	 */
	public void addListenerClientProxy(Class p_class, Object p_listenerClientProxy) {
		this.m_listenerProxies.put(p_class, p_listenerClientProxy);

	}

	public void setWebServiceDataObject(WebServiceDataObject obj) {
		this.m_webServiceDataObj = obj;
	}

	public WebServiceDataObject getWebServiceDetail() {
		return this.m_webServiceDataObj;
	}

	public Iterator<AseSoaService> getSoaServices() {
		return this.m_webServiceDataObj.getServices();
	}

	public AseSoaApplication getSoaApplication() {
		//return this.m_soaApplication;
		return this.m_webServiceDataObj.getApplication();
	}

	public void setSoaApplication(AseSoaApplication app) {
		this.m_soaApplication = app;
	}

	public AseSoaService getSoaService(String name) {
		return this.m_webServiceDataObj.getService(name);
	}

	public void setDeployableObject(Object m_obj) {
		if(m_deployableObj == null) {
		if(m_logger.isDebugEnabled())
			m_logger.debug("Setting AbstractDeployableObject on SoaContext");
		}
		m_deployableObj = m_obj;
	}

	public Object getDeployableObject() {
		if(m_deployableObj == null) {
			if(m_logger.isDebugEnabled())
			m_logger.debug("AbstractDeployableObject is not set yet");
		}
		return m_deployableObj;
	}

	//for UT
	/* public static void main(String arg[]) {
		URI uri = null;
        URI uri1 = null;
        try {
			uri = new URI("http://somesh.com");
			uri1 = new URI("http://asdfadf.com");
		} catch(URISyntaxException ee) {
		    m_logger.debug(ee.getMessage(), ee);
		}

		Object lproxy =new Object();
		SoaContextImpl sc = new SoaContextImpl("somesh", null);
		//sc.registerListenerProxy(uri, lproxy, false);
		//sc.registerListenerProxy(uri, new Object(), true);
		//sc.registerServiceProxy(uri, new Object(), false, true);
		test test = new testImpl();
		sc.m_serviceInterface.put(uri,test);
		sc.servMap.put(uri, test);
	} */


}
