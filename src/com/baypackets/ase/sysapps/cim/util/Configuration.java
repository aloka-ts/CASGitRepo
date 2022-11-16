/****

  Copyright (c) 2013 Agnity, Inc. All rights reserved.

  This is proprietary source code of Agnity, Inc. 
  Agnity, Inc. retains all intellectual property rights associated 
  with this source code. Use is subject to license terms.

  This source code contains trade secrets owned by Agnity, Inc.
  Confidentiality of this computer program must be maintained at 
  all times, unless explicitly authorized by Agnity, Inc.

 ****/
package com.baypackets.ase.sysapps.cim.util;

import java.util.HashMap;

public class Configuration  
{
   
 	private static HashMap<String, String>  m_configList;
	private static Configuration m_instance = null;
	
	private Configuration() {
		m_configList = new HashMap<String, String>();
	}

	public static synchronized Configuration getInstance() {

		if (m_instance == null ) {
			m_instance = new Configuration();
		}
		return m_instance;
	}

/* 
 * It gets the parameter value of the partcular parameter name
 */

	public String getParamValue(String paramName) {
		return m_configList.get(paramName);
		
	}

/* 
 * It sets the parameter value of the parameter name
 */
	public void setParamValue(String paramName,String paramValue) {
		m_configList.put(paramName,paramValue);
	}
	
}