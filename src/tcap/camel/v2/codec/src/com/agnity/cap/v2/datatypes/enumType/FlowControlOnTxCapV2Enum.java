package com.agnity.cap.v2.datatypes.enumType;

public enum FlowControlOnTxCapV2Enum {
	/**
	 * 0-Not required to send data with flow control mechanism
	 * 1-Required to send data with flow control mechanism
	 */
	NOT_REQUIRED(0), REQUIRED(1);
	
	private int code;

	private FlowControlOnTxCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static FlowControlOnTxCapV2Enum getValue(int num) {
		switch (num) {
		case 0: { return 	NOT_REQUIRED	; }
		case 1: { return 	REQUIRED	; }
		default: { return null; }
		}
	}

}
