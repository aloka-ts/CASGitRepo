package com.agnity.win.enumdata;

public enum PositionRequestEnum {

	/*
	 * PositionRequest Decimal Value Meaning 0 SENDER CANNOT SUPPORT POSITION
	 * REQUEST OPERATIONS 1 SENDER CAN SUPPORT POSITION REQUEST OPERATIONS
	 */
	SENDER_CANNOT_SUPPORT_POS_REQUEST_OPERATIONS(0), SENDER_CAN_SUPPORT_POS_REQUEST_OPERATIONS(
			1);

	private int code;

	private PositionRequestEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static PositionRequestEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SENDER_CANNOT_SUPPORT_POS_REQUEST_OPERATIONS;
		}
		case 1: {
			return SENDER_CAN_SUPPORT_POS_REQUEST_OPERATIONS;
		}
		default: {
			return null;
		}
		}
	}

}
