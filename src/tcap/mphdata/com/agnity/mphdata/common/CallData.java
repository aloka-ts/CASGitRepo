/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.agnity.mphdata.common;

import java.io.Serializable;
import java.util.HashMap;

public class CallData implements Serializable {
	/**
	 * 
	 */
	private static final long					serialVersionUID	= -8950386471372036222L;
	public static final transient String		CALL_DATA			= "CALL-DATA";
	private HashMap<String, Object>				persitableData		= new HashMap<String, Object>();

	private transient HashMap<String, Object>	nonpersitableData	= new HashMap<String, Object>();

	public Object getPersistableData(String key) {
		Object val = persitableData.get(key);
		if (val == null) {
			val = nonpersitableData.get(key);
		}
		return val;
	}

	public void setPersistableData(String key, Object value) {
		persitableData.put(key, value);
	}

	public Object getNonpersistableData(String key) {
		
		if(nonpersitableData == null) {
			nonpersitableData = new HashMap<String, Object>();
		}
		
		Object val = nonpersitableData.get(key);
		if (val == null) {
			val = persitableData.get(key);
		}
		return val;
	}

	public void setNonpersistableData(String key, Object value) {
		
		if(nonpersitableData == null) {
			nonpersitableData = new HashMap<String, Object>();
		}
		
		nonpersitableData.put(key, value);
	}

	public HashMap<String, Object> getPersitableData() {
		return persitableData;
	}

	public void removePersistableData(String key) {
		persitableData.remove(key);
	}

	public Object getData(String key) {
		Object result = getNonpersistableData(key);

		if (result == null) {
			result = getPersistableData(key);
		}

		return result;
	}

}
