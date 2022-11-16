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
package com.agnity.ain.enumdata;
/**
 * Enum for Transit Carrier Indicator
 * @author Mriganka
 *
 */

public enum TransitCarrierIndEnum {
	/**
	 * 0-no transmission
	 * 1-forward direction
	 * 2-backward direction
	 * 3-bi-direction
	 */
	NO_TRANSMISSION(0), FORWARD_DIRECTION(1), BACKWARD_DIRECTION(2), BI_DIRECTION(3);
	private int code;
	private TransitCarrierIndEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static TransitCarrierIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return NO_TRANSMISSION; }		case 1: { return FORWARD_DIRECTION; }
		case 2: { return BACKWARD_DIRECTION; }
		case 3: { return BI_DIRECTION; }
		default: { return null; }
		}
	}
}
