package com.camel.enumData;

/**
 * This enum represent the Redirecting Reason.
 * indicator.
 * @author nkumar
 *
 */
public enum RedirectionReasonEnum {


	/**
	 * 0-unknown/not available
	 * 1-user busy
	 * 2-no reply
	 * 3-unconditional
	 * 4-deflection during alerting
	 * 5-deflection immediate response
	 * 6-mobile subscriber not reachable
	 * 7->15-spare
	 */
	UNKNOWN(0), USER_BUSY(1), NO_REPLY(2), UNCONDITIONAL(3), DEFLECTION_ALERT(4),
		
	DEFLECTION_RESPONSE(5), NOT_REACHABLE(6), SPARE(7);
	
	private int code;

	private RedirectionReasonEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static RedirectionReasonEnum fromInt(int num) {
		switch (num) {
			case 0: { return UNKNOWN; }
			case 1: { return USER_BUSY; }
			case 2: { return NO_REPLY; }
			case 3: { return UNCONDITIONAL; }
			case 4: { return DEFLECTION_ALERT; }
			case 5: { return DEFLECTION_RESPONSE; }
			case 6: { return NOT_REACHABLE; }
			case 7: { return SPARE; }
			default: { return null; }
		}
	}
}
