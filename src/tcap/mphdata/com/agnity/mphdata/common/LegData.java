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

public class LegData implements Serializable {

	/**
	 * 
	 */
	private static final long					serialVersionUID	= 558378882164660795L;

	private HashMap<String, Object>				persitableData		= new HashMap<String, Object>();

	private transient HashMap<String, Object>	nonpersitableData	= new HashMap<String, Object>();

	public Object getPersistableData(String key) {
		Object val = persitableData.get(key);
//		if (val == null) {
//			val = nonpersitableData.get(key);
//		}
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

	public void removePersistableData(String key) {
		persitableData.remove(key);
	}

	public void removeNonpersistableData(String key) {
		nonpersitableData.remove(key);
	}

	@Override
	public String toString() {
		return "LegData [persitableData=" + persitableData + ", nonpersitableData="
						+ nonpersitableData + "]";
	}
}
