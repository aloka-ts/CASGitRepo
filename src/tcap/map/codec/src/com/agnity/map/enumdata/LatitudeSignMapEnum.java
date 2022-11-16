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
 * Latitude Sign
 * 0 North
 * 1 South 
 * 
 * defined in Q.763 (1999). 3.88.1
 *
 */
public enum LatitudeSignMapEnum {
	NORTH(0), SOUTH(1);
	
	int code;
	
	private LatitudeSignMapEnum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static LatitudeSignMapEnum getValue(int tag){
		switch(tag) {
			case 0: return NORTH;
			case 1: return SOUTH;
			default: return null;
		}
	}

}
