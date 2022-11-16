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
//      File:   SoaContext.java
//
//      Desc:   This file is an interface exposed to SOA applications/services. There is one instance of
//				SoaContext implementation associated with each SOA application/service. This interface
//				provides various APIs using which a service or listener objects can be retrieved in order to
//				invoke operation on them.
//				In addition, this interface provides operation to retrieve configuration parameters specified
//				in soa.xml file, service/application name, service/listener public URIs and current role of 
//				container.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Somesh Kr. Srivastava           17/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.iface;

import java.net.URI;
import java.util.Iterator;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.common.Registry;


/**
 * This interface is exposed to SOA applications/services. There is one instance of SoaContext
 * implementation associated with each SOA application/service. Using APIs of this interface service
 * or listener objects can be retrieved in order to invoke operation on them.
 * In addition, this interface provides operation to retrieve configuration parameters specified
 * in soa.xml file, service/application name, service/listener public URIs and current role of 
 * container.
 *
 * @author Somesh Kr. Srivastava
 */
public abstract class  SoaContext {

	/**
	 * This represents 'Active' role of container.
	 */
	public static final int ROLE_ACTIVE = 0;

	/**
	 * This represents 'Standby role of container.
	 */
	public static final int ROLE_STANDBY = 1;


	/**
	 * This method is available to service/application so that it can get the reference to 
	 * associated SoaContext object. This is useful in case of non-servlet service/application
	 * where there is no ServletContext is available.
	 *
	 * @param name String representing the name of service/application
	 * @return SoaContext
	 */
	public static SoaContext getSoaContext(String p_name)	{
		 SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);	 
		 return soaFw.getSoaContext(p_name);
	}

	/**
	 * This method is used to retrieve URI corresponding to the service interface class
	 * specified in DD.
	 *
	 * @param class service interface class in DD
	 * @return URI
	 */
	public abstract URI getServiceURI(Class name);

	/**
	 * A service can register its service implementation object corresponding to given URI
	 * by using this method. This method is useful when the implementation object was not
	 * specified in DD and instantiated by service itself.
	 *
	 * @param URI
	 * @param Object service implementation object
	 */
	public abstract void registerService(URI uri, Object obj);

	/**
	 * A service can find the desired listener object by giving the URI and listener interface
	 * class. The listener invocation can then be performed on this listener object.
	 *
	 * @param URI 
	 * @param class listener interface class
	 * @return listener object
	 */
	public abstract Object findListener(URI uri, Class obj);

	/**
	 * An application can retrieve the URI corresponding to the given listener interface.
	 *
	 * @param class listener interface class
	 * @return URI
	 */
	public abstract URI getListenerURI(Class className);

	/**
	 * An application can find the desired service object by specifying the service URI and
	 * interface class
	 *
	 * @param URI service uri
	 * @param Class interface class
	 * @return service object
	 */
	public abstract Object findService(URI uri, Class className);

	/**
	 * A service can retrieve its unique name using this method.
	 *
	 * @return String representing the name of the service.
	 */
	public abstract String getName();

	/**
	 * Names of all the parameters specified in SOA DD can be accessed using this method.
	 *
	 * @return iterator<String> of all the parameters in SOA DD.
	 */
	public abstract Iterator<String> getParameters();

	/**
	 * This method returns the value of configuration parameter wiht the specified name.
	 *
	 * @param String name of the parameter
	 * @return value of the parameter(String)
	 */
	public abstract String getParameterValue(String name);

	/**
	 * This method returns the current role (Active/Standby) of the container.
	 *
	 * @return 0 or 1 resp. active or standby.
	 */
	public abstract int getContainerRole();

	/**
	 * This method can be used by service to access its implementation object for the given service URI.
	 * This method would be useful when the service implementation class was specified in DD, hence container
	 * instantiated it and later same needs to be referenced by service itself.
	 *
	 * @param URI service uri
	 * @return implementaion object of the service
	 */
	public abstract Object getServiceImpl(URI uri);

	/**
	 *
	 */
	public abstract void setDeployableObject(Object p_obj);

	/**
	 *
	 */
	public abstract Object getDeployableObject();

}

