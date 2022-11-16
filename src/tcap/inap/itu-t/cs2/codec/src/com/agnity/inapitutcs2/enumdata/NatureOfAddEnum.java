package com.agnity.inapitutcs2.enumdata;


/**
 * Enum for Nature of address indicator
 * @author Mriganka
 *
 */
public enum NatureOfAddEnum {
	
	/**
	 * 0-spare
	 * 1-subscriber number (national use)
	 * 2-unknown (national use)
	 * 3-national (significant) number (national use)
	 * 4-international number
	 * 5-network-specific number (national use) (For called party only)
	 * 6->national network route number 
	 * 112->125-reserved for national use
	 * 126 - AssistingSSPIPRoutingAddress (specific to SBTM)
	 * 127-spare
	 */
	SPARE(0), SUBS_NO(1), UNKNOWN(2), NATIONAL_NO(3), INTER_NO(4), NETWORK_NO(5),NATIONAL_NETWORK_ROUTE_NO(6), ASSIST_SSPIP_ROUTE_ADDR(126);

	private int code;

	private NatureOfAddEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NatureOfAddEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return SUBS_NO; }
			case 2: { return UNKNOWN; }
			case 3: { return NATIONAL_NO; }
			case 4: { return INTER_NO; }
			case 5: { return NETWORK_NO; }
			case 6: { return NATIONAL_NETWORK_ROUTE_NO;}
			case 126: { return ASSIST_SSPIP_ROUTE_ADDR; }
			default: { return null; }
		}
	}
}
