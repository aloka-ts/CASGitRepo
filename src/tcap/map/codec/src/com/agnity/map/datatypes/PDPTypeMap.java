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

public class PDPTypeMap {
	private String pdpType;
	
	public PDPTypeMap(){
	}
	
	public PDPTypeMap(String pdpType) {
		this.pdpType = pdpType;
	}
	
	public void setPDPType(String pdpType){
		this.pdpType = pdpType;
	}
	
	public String getPDPType() {
		return this.pdpType;
	}
	
	public String toString() {
		return pdpType;
	}

}
