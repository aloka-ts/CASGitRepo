package com.genband.inap.enumdata;

/**
 * Enum for Charge Information DelayEnum
 * @author vgoel
 *
 */

public enum ChargeInfoDelayEnum {

	/**
	 *  0-Spare
	 *  1->128-reserved for national use 
	 *  253-charging rate transfer 
	 *  254-terminating charge area information 
	 */
	SPARE(0), NATIONAL_USE(1), CHARGING_RATE_TRANSFER(253), TERMINATING_CHARGE_AREA_INFO(254);
	 
	private int code;

	private ChargeInfoDelayEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ChargeInfoDelayEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	SPARE	; }
		case 1: { return 	NATIONAL_USE	; }
		case 253: { return 	CHARGING_RATE_TRANSFER	; }
		case 254: { return 	TERMINATING_CHARGE_AREA_INFO	; }
		default: { return null; }
		}
	}
}
