package com.genband.isup.enumdata;

/**
 * Enum for Charging Information Indicator
 * @author vgoel
 *
 */

public enum ChargingInfoIndEnum {

	/**
	 *  0-spare
	 *  2-charging pulse interval information (LMNC indication)
	 */
	SPARE(0), CHARGING_PULSE_INTERVAL_INFO_LMNC(2);
	 
	private int code;

	private ChargingInfoIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ChargingInfoIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	SPARE	; }
		case 2: { return 	CHARGING_PULSE_INTERVAL_INFO_LMNC	; }
		default: { return null; }
		}
	}
}
