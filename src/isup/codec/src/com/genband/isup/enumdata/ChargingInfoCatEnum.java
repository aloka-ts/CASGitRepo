package com.genband.isup.enumdata;

/**
 * Enum for Charging Information Category
 * @author vgoel
 *
 */

public enum ChargingInfoCatEnum {

	/**
	 *  0-KDD - international automatic subscriber dialing payphone (network specific)
	 *  1-international automatic subscriber dialing payphone
	 *  2-flexible charging
	 *  3-application charging rate transfer
	 *  4-NTT network connection type PHS
	 *  5-charge calculation information
	 *  6-network-specific information
	 *  129-spare
	 *  254-charging rate transfer
	 */
	KDD(0), INT_AUTO_SUBS_PAYPHONE(1), FLEXIBLE_CHARGING(2), APP_CHARGING_RATE_TRFR(3), NTT_NW_CONN_PHS(4), CHARGE_CALC_INFO(5), NW_SPECIFIC_INFO(6),
	SPARE(129), CHARGING_RATE_TRFR(254);
	 
	private int code;

	private ChargingInfoCatEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ChargingInfoCatEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	KDD	; }
		case 1: { return 	INT_AUTO_SUBS_PAYPHONE	; }
		case 2: { return 	FLEXIBLE_CHARGING	; }
		case 3: { return 	APP_CHARGING_RATE_TRFR	; }
		case 4: { return 	NTT_NW_CONN_PHS	; }
		case 5: { return 	CHARGE_CALC_INFO	; }
		case 6: { return 	NW_SPECIFIC_INFO	; }
		case 129: { return 	SPARE	; }
		case 254: { return 	CHARGING_RATE_TRFR	; }
		default: { return null; }
		}
	}
}
