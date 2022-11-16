package com.agnity.map.enumdata;

/**
 * @author sanjay
 *
 */

public enum MatchTypeMapEnum {

	INHIBITING(0), 
	ENABLING(1);
	
	private int code;

	private MatchTypeMapEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static MatchTypeMapEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	INHIBITING	; }
		case 1: { return 	ENABLING	; }

		default: { return null; }
		}
	}
}
