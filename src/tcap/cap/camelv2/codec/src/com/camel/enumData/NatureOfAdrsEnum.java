package com.camel.enumData;


/**
 * 
 * This enum indicates nature of address 
 * indiactor.
 * 
 * @author nkumar
 */

public enum NatureOfAdrsEnum {

	/**
	 * 0-spare
	 * 1-subscriber number (national use)
	 * 2-unknown (national use)
	 * 3-national (significant) number (national use)
	 * 4-international number
	 * 5-network-specific number (national use) (For called party only)
	 * 6->111- spare
	 * 112->126-reserved for national use
	 * 127-spare
	 */
	SPARE(0), SUBS_NO(1), UNKNOWN(2), NATIONAL_NO(3), INTER_NO(4), NETWORK_NO(5);

	private int code;

	private NatureOfAdrsEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NatureOfAdrsEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return SUBS_NO; }
			case 2: { return UNKNOWN; }
			case 3: { return NATIONAL_NO; }
			case 4: { return INTER_NO; }
			case 5: { return NETWORK_NO; }
			default: { return null; }
		}
	}

}
