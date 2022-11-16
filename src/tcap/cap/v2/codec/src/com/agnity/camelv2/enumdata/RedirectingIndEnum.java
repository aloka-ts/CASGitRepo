package com.agnity.camelv2.enumdata;

/**
 * This enum represent the Redirecting Indicator.
 * indicator.
 * @author nkumar
 *
 */
public enum RedirectingIndEnum {

	/**
	 * 0-no redirection (national use)
	 * 1-call rerouted (national use)
	 * 2-call rerouted, all redirection information presentation restricted (national use)
	 * 3-call diverted
	 * 4-call diverted, all redirection information presentation restricted
	 * 5-call rerouted, redirection number presentation restricted (national use)
	 * 6-call diversion, redirection number presentation restricted (national use)
	 * 7-spare
	 */
	NO_REDIRECTION(0), CALL_REROUTED(1), CALL_REROUTED_REDRN_INF_RESTD(2), CALL_DIVERTED(3), CALL_DIVERTED_REDRN_INF_RESTD(4),
		
	CALL_REROUTED_REDRN_NO_RESTD(5), CALL_DIVERTED_REDRN_NO_RESTD(6), SPARE(7);
	
	private int code;

	private RedirectingIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static RedirectingIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return NO_REDIRECTION; }
			case 1: { return CALL_REROUTED; }
			case 2: { return CALL_REROUTED_REDRN_INF_RESTD; }
			case 3: { return CALL_DIVERTED; }
			case 4: { return CALL_DIVERTED_REDRN_INF_RESTD; }
			case 5: { return CALL_REROUTED_REDRN_NO_RESTD; }
			case 6: { return CALL_DIVERTED_REDRN_NO_RESTD; }
			case 7: { return SPARE; }
			default: { return null; }
		}
	}
}
