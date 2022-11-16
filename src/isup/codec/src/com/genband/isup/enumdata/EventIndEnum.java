package com.genband.isup.enumdata;

/**
 * Enum for Event Indicator
 * @author vgoel
 *
 */
public enum EventIndEnum {

	/**
	 *  0-spare
	 *  1-alerting
	 *  2-progress
	 *  3-in-band information
	 *  4-call forwarded on busy (national use)
	 *  5-call forwarded on no reply (national use)
	 *  6-call forwarded unconditional (national use)
	 */
	
	SPARE(0), ALERTING(1), PROGRESS(2), INBAND_INFO(3), CALL_FORWARD_BUSY(4), CALL_FORWARD_REPLY(5), CALL_FORWARD_UNCONDITIONAL(6);
	 
	private int code;

	private EventIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static EventIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	SPARE	; }
		case 1: { return 	ALERTING	; }
		case 2: { return 	PROGRESS	; }
		case 3: { return 	INBAND_INFO	; }
		case 4: { return 	CALL_FORWARD_BUSY	; }
		case 5: { return 	CALL_FORWARD_REPLY	; }
		case 6: { return 	CALL_FORWARD_UNCONDITIONAL	; }
		default: { return null; }
		}
	}
}
