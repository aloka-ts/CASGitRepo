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
 * SM capability (MT SMS pt to pt capability) 
 * 0 Mobile station does not support mobile terminated point to point SMS
 * 1 Mobile station supports mobile terminated point to point SMS
 *  
 * @author sanjay
 *
 */
public enum SmCapabilityMapEnum {
	MT_P2P_SMS_NOT_SUPPORTED(0),
	MT_P2P_SMS_SUPPORTED(1);
	
	private int code;
	public int getCode() {
		return this.code;
	}
	
	private SmCapabilityMapEnum(int code) {
		this.code = code;
	}
	
	public static SmCapabilityMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return MT_P2P_SMS_NOT_SUPPORTED;
			case 1: return MT_P2P_SMS_SUPPORTED;
			default: return null;
		}
	}
}
