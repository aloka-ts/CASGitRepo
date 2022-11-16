package com.agnity.win.enumdata;

public enum BreakEnum {

	/* BREAK 
	 * DecimalValue   Meaning 
	 *         0       NoBreak. Ignore digits received before the end of the announcement for purposes of controlling
					   the announcement.
	 *         1       BreakIn (default). Allow digits received before or during an announcement to cut the announcement off.   
	 */
	NO_BREAK(0), BREAK_IN(
			1);

	private int code;

	private BreakEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static BreakEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return NO_BREAK;
		}
		case 1: {
			return BREAK_IN;
		}
		default: {
			return null;
		}
		}
	}

}
