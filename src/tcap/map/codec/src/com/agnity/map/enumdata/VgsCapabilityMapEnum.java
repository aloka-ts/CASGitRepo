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
 * VGCS notification reception
 * 
 * An MS not supporting A/Gb mode shall set this bit to ‘0’.
 * An MS supporting A/Gb mode shall indicate the associated capability (see table):
 * 
 * 0 no VGCS capability or no notifications wanted
 * 1 VGCS capability and notifications wanted
 *  
 * @author sanjay
 *
 */
public enum VgsCapabilityMapEnum {
	VGS_CAP_OR_NOTF_NOT_WANTED(0),
	VGS_CAP_AND_NOTF_WANTED(1);
	
	private int code;
	public int getCode() {
		return this.code;
	}
	
	private VgsCapabilityMapEnum(int code) {
		this.code = code;
	}
	
	public static VgsCapabilityMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return VGS_CAP_OR_NOTF_NOT_WANTED;
			case 1: return VGS_CAP_AND_NOTF_WANTED;
			default: return null;
		}
	}

}
