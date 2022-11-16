package com.agnity.win.enumdata;

/**
 * This class defines the Numbering Plan based on the definition provided in
 * TIA-EIA-41-D, section 6.5.3.2
 * 
 * @author Rajeev Arya
 */
public enum NumPlanEnum {

	/**
	 * Definition as per TIA/EIA-41-D, section 6.5.3.2 0-spare 1-ISDN
	 * (Telephony) numbering plan (Recommendation E.164) 2-Telephony Numbering
	 * (E.164, E.163) 3-Data numbering (Recommendation X.121) (national use)
	 * 4-Telex numbering plan (Recommendation F.69) (national use) 5- Maritime
	 * Mobile Numbering 6-Land Mobile Numbering (E.212) 7-Private Numbering Plan
	 * (Service Provider defined) 13-ANSI SS7 Point Code (PC) and Subsystem
	 * Number (SSN) 14-Internet Protocol (IP) Address 15- Reserved
	 */
	SPARE(0), ISDN_NP(1), TEL_NP(2), DATA_NP(3), TELEX_NP(4), MARITIME_MOB_NP(5), LAND_MOB_NP(
			6), PRIVATE_NP(7), ANSI_SS7_PC(13);

	private int code;

	private NumPlanEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static NumPlanEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SPARE;
		}
		case 1: {
			return ISDN_NP;
		}
		case 2: {
			return TEL_NP;
		}
		case 3: {
			return DATA_NP;
		}
		case 4: {
			return TELEX_NP;
		}
		case 5: {
			return MARITIME_MOB_NP;
		}
		case 6: {
			return LAND_MOB_NP;
		}
		case 7: {
			return PRIVATE_NP;
		}
		case 13: {
			return ANSI_SS7_PC;
		}
		default: {
			return null;
		}
		}
	}

}
