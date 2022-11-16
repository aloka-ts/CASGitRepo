package com.baypackets.ase.sysapps.registrar.common;

import java.util.HashMap;


/** Singleton */

/** This class is a singleton class and it stores the initial parameters and values as obtained from ServletConfig.
*/

public class Configuration  
{
   
 	private static HashMap  m_configList;
	private static Configuration m_instance = null;

	private Configuration() {
		m_configList = new HashMap();
	}

	public static synchronized Configuration getInstance() {

		if (m_instance == null ) {
			m_instance = new Configuration();
		}
		return m_instance;
	}

/* It gets the parameter value of the partcular parameter name
*/

	public String getParamValue(String paramName) {
		return ((String)m_configList.get(paramName));
		
	}

/* It sets the parameter value of the parameter name
*/

	public void setParamValue(String paramName,String paramValue) {
		m_configList.put(paramName,paramValue);
	}
		

}

