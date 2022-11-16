package com.agnity.win.enumdata;

public enum ClassEnum {

	/*
	 * Class (octet 1) Bits H G F E D C B A Value Meaning 0 0 0 0 0 Concurrent.
	 * Play announcements concurrently with any call routing. 0 0 0 1 1
	 * Sequential. Play all announcements before any call termination or
	 * routing.
	 * 
	 * 
	 * 0 0 1 0 2 ? ? through 0 1 1 1 7 Reserved. Treat the same as value 0 1 0 0
	 * 0 8 ? ? through 1 1 1 1 15
	 */

	CONCURRENT(0), SEQUENTIAL(1);

	private int code;

	private ClassEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static ClassEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return CONCURRENT;
		}
		case 1: {
			return SEQUENTIAL;
		}
		default: {
			return null;
		}
		}
	}

}
