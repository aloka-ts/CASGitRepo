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
package com.agnity.cap.v3.datatypes.enumType;

public enum LayerIdentificationCapV3Enum {


	/**
	 *  1-Layer 1
	 *  2-Layer 2 
	 *  3-Layer 3 
	 */
	LAYER_1(1), LAYER_2(2), LAYER_3(3);
	
	private int code;
	private LayerIdentificationCapV3Enum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static  LayerIdentificationCapV3Enum getValue(int tag){
		switch (tag) {
		case 1: return LAYER_1;
		case 2: return LAYER_2;
		case 3: return LAYER_3;
		default: return null;
		}
	}
	
}
