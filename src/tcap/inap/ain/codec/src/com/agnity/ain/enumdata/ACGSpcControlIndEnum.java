/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
/**
 * 
 */
package com.agnity.ain.enumdata;

/**
 * @author nishantsharma
 *
 */
public enum ACGSpcControlIndEnum {
	/**
	 * 0->  No SPC Overload controls encountered
	 * 1->  SPC Overload controls encountered
	 */
	NO_SPC_OVERLOAD_CTRL(0),SPC_OVERLOAD_CTRL(1);
	private int code;
	private ACGSpcControlIndEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}

	public static ACGSpcControlIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return NO_SPC_OVERLOAD_CTRL; }
		case 1: { return SPC_OVERLOAD_CTRL; }
		default: { return null; }
		}
	}
}
