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
 * Enum for Carrier Information Subordinate
 * @author Mriganka
 *
 */
public enum CarrierInfoSubordinateEnum {
	/**
	 * 0-spare
	 * 1-reserved for national use
	 * 252-POI level information
	 * 253-POI charge area information
	 * 254-Carrier Identification Code
	 */
	SPARE(0), RESERVED(1), POI_LEVEL_INFO(252), POI_CHARGE_AREA_INFO(253), CARRIER_IDENT_CODE(254);
	private int code;
	private CarrierInfoSubordinateEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static CarrierInfoSubordinateEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return RESERVED; }
			case 252: { return POI_LEVEL_INFO; }
			case 253: { return POI_CHARGE_AREA_INFO; }
			case 254: { return CARRIER_IDENT_CODE; }
			default: { return null; }
		}
	}
}
