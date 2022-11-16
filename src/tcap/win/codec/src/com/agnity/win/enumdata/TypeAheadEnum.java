package com.agnity.win.enumdata;

public enum TypeAheadEnum {

	/* TypeAhead 
	 * DecimalValue   Meaning 
	 *         0       No Type Ahead.Ignore digits received before the end of the announcement.
	 *         1       Buffer(Default).Allow digits to be received and
                       collected before the end of the announcement.    
	 */
	NO_TYPE_AHEAD(0), BUFFER(
			1);

	private int code;

	private TypeAheadEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static TypeAheadEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return NO_TYPE_AHEAD;
		}
		case 1: {
			return BUFFER;
		}
		default: {
			return null;
		}
		}
	}

}
