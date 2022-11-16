package com.genband.inap.enumdata;


/**
 * Enum for Nature of address indicator used for called in number
 * @author vgoel
 *
 */
public enum TTCNatureOfAddEnum {
	
	/**
	 * 0-spare
	 * 1-subscriber number (national use)
	 * 2-unknown (national use)
	 * 3-national (significant) number (national use)
	 * 4-international number
	 * 5-reserved
	 * 6-network routing number with the national number format (national use)
	 * 7-network routing number with the network specific number format (national use)
	 * 8-reserved
	 * 9->111- spare
	 * 112->125-reserved for national use
	 * 126-network specific number
	 * 127-spare
	 */
	SPARE(0), SUBS_NO(1), UNKNOWN(2), NATIONAL_NO(3), INTER_NO(4), RESERVED(5), NW_ROUTING_NATIONAL_FORMAT(6),
	NW_ROUTING_NETWORK_FORMAT(7), NW_SPECIFIC_NUMBER(126);

	private int code;

	private TTCNatureOfAddEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static TTCNatureOfAddEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return SUBS_NO; }
			case 2: { return UNKNOWN; }
			case 3: { return NATIONAL_NO; }
			case 4: { return INTER_NO; }
			case 5: { return RESERVED; }
			case 6: { return NW_ROUTING_NATIONAL_FORMAT; }
			case 7: { return NW_ROUTING_NETWORK_FORMAT; }
			case 126: { return NW_SPECIFIC_NUMBER; }
			default: { return null; }
		}
	}
}
