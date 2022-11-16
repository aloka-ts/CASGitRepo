package com.agnity.win.enumdata;

public enum CircuitSwitchedDataEnum {

	/*
	 * CircuitSwitchedData Decimal Value Meaning 0 SENDER CANNOT SUPPORT WIN
	 * TRIGGER BASED CS DATASERVICES 1 SENDER CAN SUPPORT WIN TRIGGER BASED CS
	 * DATASERVICES
	 */
	SENDER_CANNOT_SUPPORT_WIN_CS_DATASERVICES(0), SENDER_CAN_SUPPORT_WIN_CS_DATASERVICES(
			1);

	private int code;

	private CircuitSwitchedDataEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static CircuitSwitchedDataEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SENDER_CANNOT_SUPPORT_WIN_CS_DATASERVICES;
		}
		case 1: {
			return SENDER_CAN_SUPPORT_WIN_CS_DATASERVICES;
		}
		default: {
			return null;
		}
		}
	}

}
