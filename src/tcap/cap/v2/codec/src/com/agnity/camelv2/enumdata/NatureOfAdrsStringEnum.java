package com.agnity.camelv2.enumdata;

/**
 * 
 * This enum indicates nature of address 
 * indiactor for MSC.
 * 
 * @author nkumar
 */
public enum NatureOfAdrsStringEnum {
	
	/**
	 * 0-unknown
	 * 1-international number                                  
	 * 2-national (significant) number (national use)          unknown (national use)
	 * 3-network-specific number
	 * 4-subscriber number 
	 * 5-reserved
	 * 6->abbreviated number
	 * 7,8-reserved for extension
	 */
	UNKNOWN(0), INTER_NO(1),NATIONAL_NO(2), NETWORK_NO(3), SUBS_NO(4), ABBERIVATED_NO(6), ;

	private int code;

	private NatureOfAdrsStringEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NatureOfAdrsStringEnum fromInt(int num) {
		switch (num) {
			case 0: { return UNKNOWN; }
			case 1: { return INTER_NO; }
			case 2: { return NATIONAL_NO; }
			case 3: { return NETWORK_NO; }
			case 4: { return SUBS_NO; }
			case 6: { return ABBERIVATED_NO; }
			default: { return null; }
		}
	}

}
