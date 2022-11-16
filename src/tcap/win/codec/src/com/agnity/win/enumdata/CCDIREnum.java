package com.agnity.win.enumdata;

public enum CCDIREnum {

	/*
	 * CCDIR Decimal Value Meaning 0 SENDER CANNOT SUPPORT CALL CONTROL
	 * DIRECTIVE OPERATIONS 1 SENDER CAN SUPPORT CALL CONTROL DIRECTIVE
	 * OPERATIONS
	 */
	SENDER_CANNOT_SUPPORT_CCD_OPERATIONS(0), SENDER_CAN_SUPPORT_CCD_OPERATIONS(
			1);

	private int code;

	private CCDIREnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static CCDIREnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SENDER_CANNOT_SUPPORT_CCD_OPERATIONS;
		}
		case 1: {
			return SENDER_CAN_SUPPORT_CCD_OPERATIONS;
		}
		default: {
			return null;
		}
		}
	}

}
