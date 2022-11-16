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

public enum PresentationMethodOfProtocolProfileCapV3Enum {
	
	/*
	 * Presentation method of protocol profile (octet 3)
		Bits
		2 1
		0 1 High layer protocol profile (without specification of attributes)
		All other values are reserved.
		NOTE 4 – Currently, "Presentation method of protocol profile" has only a single value, i.e. a "profile
		value" is used to indicate a service to be supported by high layer protocols as required. Necessity of other
		presentation methods, e.g. service indications in the forum of layer-by-layer indication of protocols to be
		used in high layers, is left for further study.
	 */
	
	HIGH_LAYER_PROTOCOL_PROFILE(1);
	
	private int code;
	
	private PresentationMethodOfProtocolProfileCapV3Enum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static PresentationMethodOfProtocolProfileCapV3Enum getValue(int tag){
		switch (tag) {
		case 1: return HIGH_LAYER_PROTOCOL_PROFILE;
		default:return null;
		}
	}
	
}
