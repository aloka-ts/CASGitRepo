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
package com.agnity.cap.v2.datatypes.enumType;

/**
 * 
 * @author rnarayan
 *
 */
public enum LegTypeCapV2Enum {

	LEG_1(1) , LEG_2(2);
	
	private int code;
	private LegTypeCapV2Enum(int code) {
		// TODO Auto-generated constructor stub
	   this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static LegTypeCapV2Enum getValue(int tag){
		 switch (tag) {
		case 1: return LEG_1;
		case 2: return LEG_2;
		default:return null;
		}
	}
}
