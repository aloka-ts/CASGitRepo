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


package com.agnity.map.enumdata;

/**
 * 
 * @author sanjay
 *
 */

public enum RequestedNodesMapEnum {
	MME(0), SGSN(1);
	
	private int code;
	
	private RequestedNodesMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static RequestedNodesMapEnum getValue(int tag) {
		switch(tag){
			case 0: return MME;
			case 1: return SGSN;
			default: return null;
		}
	}

}
