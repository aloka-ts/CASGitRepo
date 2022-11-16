package com.camel.enumData;

/**
 * 
 * This enum indicates Numbering Plan 
 * indiactor for MSC.
 * 
 * @author nkumar
 */
public enum NumPlan_AdrsStringEnum {
	
	/**
	 * 0-unknown
	 * 1-ISDN (Telephony) numbering plan (Recommendation E.164)
	 * 2-spare
	 * 3-Data numbering plan (Recommendation X.121) (national use)
	 * 4-Telex numbering plan (Recommendation F.69) (national use)
	 * 5-spare 
	 * 6- land mobile numbering plan
	 * 7-spare
	 * 8-national numbering plan
	 * 9-Private Numbering Plan
	 * 10->15-reserved
	 */
	UNKNOWN(0), ISDN_NP(1), SPARE(2), DATA_NP(3), TELEX_NP(4), LAND_MOBILE_NP(6), NATIONAL_NP(8), PRIVATE_NP(9);

	private int code;

	private NumPlan_AdrsStringEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NumPlan_AdrsStringEnum fromInt(int num) {
		switch (num) {
			case 0: { return UNKNOWN; }
			case 1: { return ISDN_NP; }
			case 2: { return SPARE; }
			case 3: { return DATA_NP; }
			case 4: { return TELEX_NP; }
			case 6: { return LAND_MOBILE_NP; }
			case 7: { return NATIONAL_NP; }
			case 9: { return PRIVATE_NP; }
			default: { return null; }
		}
	}

}
