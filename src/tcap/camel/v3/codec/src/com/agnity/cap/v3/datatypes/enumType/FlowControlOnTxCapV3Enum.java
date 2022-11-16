package com.agnity.cap.v3.datatypes.enumType;

public enum FlowControlOnTxCapV3Enum {
	/**
	 * 0-Not required to send data with flow control mechanism
	 * 1-Required to send data with flow control mechanism
	 */
	NOT_REQUIRED(0), REQUIRED(1);
	
	private int code;

	private FlowControlOnTxCapV3Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static FlowControlOnTxCapV3Enum getValue(int num) {
		switch (num) {
		case 0: { return 	NOT_REQUIRED	; }
		case 1: { return 	REQUIRED	; }
		default: { return null; }
		}
	}

}
