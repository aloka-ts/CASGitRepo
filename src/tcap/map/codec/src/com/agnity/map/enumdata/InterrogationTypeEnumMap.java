package com.agnity.map.enumdata;

/**
 * @author sanjay
 *
 */

public enum InterrogationTypeEnumMap {
	BASIC_CALL(0), FORWARDING(1);
	 
	private int code;

	private InterrogationTypeEnumMap(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static InterrogationTypeEnumMap fromInt(int num) {
		switch (num) {
		case 0: { return 	BASIC_CALL	; }
		case 1: { return 	FORWARDING	; }
		default: { return null; }
		}
	}
}
