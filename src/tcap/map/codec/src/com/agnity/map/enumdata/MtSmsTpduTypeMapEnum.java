package com.agnity.map.enumdata;

/**
 * @author sanjay
 *
 */

public enum MtSmsTpduTypeMapEnum {
	SMS_DELIVER(0),
	SMS_SUBMIT_REPORT(1),
	SMS_STATUS_REPORT(2);
	
	private int code;

	private MtSmsTpduTypeMapEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static MtSmsTpduTypeMapEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	SMS_DELIVER	; }
		case 1: { return 	SMS_SUBMIT_REPORT	; }
		case 2: { return 	SMS_STATUS_REPORT	; }
		default: { return null; }
		}
	}
}
