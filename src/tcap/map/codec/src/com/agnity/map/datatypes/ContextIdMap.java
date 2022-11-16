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

public class ContextIdMap {
	private Integer contextId;
	
	public ContextIdMap(){
	}
	
	public ContextIdMap(Integer contextId) {
		this.contextId = contextId;
	}
	
	public void setContextId(Integer contextId){
		this.contextId = contextId;
	}
	
	public Integer getContextId() {
		return this.contextId;
	}
	
	public String toString() {
		StringBuilder state = new StringBuilder();
		state.append("ContextId : "+this.contextId);
		return state.toString();
	}

}
