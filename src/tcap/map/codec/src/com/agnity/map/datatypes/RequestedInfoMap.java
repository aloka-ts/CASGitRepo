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

import java.io.Serializable;

import com.agnity.map.enumdata.DomainTypeMapEnum;

public class RequestedInfoMap {
	
	private DomainTypeMapEnum domainType;
	private RequestedNodesMap requestedNodes;
	
	
	public RequestedInfoMap() {
	}
	
	public RequestedInfoMap(DomainTypeMapEnum domainType, 
			RequestedNodesMap reqNodes) {

		this.domainType = domainType;
		this.requestedNodes = reqNodes;
		
	}
	
	public void setDomainType(DomainTypeMapEnum domainType){
		this.domainType = domainType;
	}
	
	public DomainTypeMapEnum getDomainType() {
		return this.domainType;
	}
	
	public RequestedNodesMap getRequestedNodes() {
		return this.requestedNodes;
	}
	
	public void setRequestedNodes(RequestedNodesMap reqNodes) {
		this.requestedNodes = reqNodes;
	}
}
