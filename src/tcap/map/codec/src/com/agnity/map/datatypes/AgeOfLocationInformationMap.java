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

public class AgeOfLocationInformationMap {
	private Integer ageOfLoc;
	
	public AgeOfLocationInformationMap(){
	}
	
	public AgeOfLocationInformationMap(Integer ageOfLoc) {
		this.ageOfLoc = ageOfLoc;
	}
	
	public void setAgeOfLocation(Integer ageOfLoc){
		this.ageOfLoc = ageOfLoc;
	}
	
	public Integer getAgeOfLocation() {
		return this.ageOfLoc;
	}
	
	public String toString() {
		StringBuilder state = new StringBuilder();
		state.append("Age Of Location : "+this.ageOfLoc);
		return state.toString();
	}

}
