package com.camel.enumData;

/**
 * This enum represent Numbering Plan indiactor.
 * 
 * @author nkumar
 *
 */


public enum NumPlanEnum {

	/**
	 * 0-spare
	 * 1-ISDN (Telephony) numbering plan (Recommendation E.164)
	 * 2-spare
	 * 3-Data numbering plan (Recommendation X.121) (national use)
	 * 4-Telex numbering plan (Recommendation F.69) (national use)
	 * 5- Private Numbering Plan
	 * 6-reserved for national use
	 * 7-spare
	 */
	SPARE(0), ISDN_NP(1), DATA_NP(3), TELEX_NP(4), PRIVATE_NP(5);

	private int code;

	private NumPlanEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NumPlanEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return ISDN_NP; }
			case 3: { return DATA_NP; }
			case 4: { return TELEX_NP; }
			case 5: { return PRIVATE_NP; }
			default: { return null; }
		}
	}

}
