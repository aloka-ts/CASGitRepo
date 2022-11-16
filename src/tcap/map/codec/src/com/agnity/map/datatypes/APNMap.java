/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/

package com.agnity.map.datatypes;

public class APNMap {
	private String apn;
	
	public APNMap(){
	}
	
	public APNMap(String apn) {
		this.apn = apn;
	}
	
	public void setAPN(String apn){
		this.apn = apn;
	}
	
	public String getAPN() {
		return this.apn;
	}
	
	public String toString() {
		return apn;
	}

}
