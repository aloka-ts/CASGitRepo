package com.agnity.cap.v3.datatypes.enumType;

public enum OperationModeCapV3Enum {
	/**
	 * 0-Bit transparent mode of operation
	 * 1-Protocol sensitive mode of operation
	 */
	BIT_TRANSPARENT(0), PROTOCOL_SENSITIVE(1);
	
	private int code;

	private OperationModeCapV3Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static OperationModeCapV3Enum getValue(int num) {
		switch (num) {
		case 0: { return 	BIT_TRANSPARENT	; }
		case 1: { return 	PROTOCOL_SENSITIVE	; }
		default: { return null; }
		}
	}

}
