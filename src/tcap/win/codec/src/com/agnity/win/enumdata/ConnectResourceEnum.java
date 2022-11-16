package com.agnity.win.enumdata;

public enum ConnectResourceEnum {

	/*
	 * ConnectResource Decimal Value Meaning 0 SENDER CANNOT SUPPORT CONNECT
	 * RESOURCE,DISCONNECT RESOURCE,CONNECTION FAILURE REPORT, RESET TIMER
	 * OPERATIONS 1 SENDER CAN SUPPORT CONNECT RESOURCE,DISCONNECT
	 * RESOURCE,CONNECTION FAILURE REPORT, RESET TIMER OPERATIONS
	 */
	SENDER_CANT_SUPPORT_OPERATIONS(0), SENDER_CAN_SUPPORT_OPERATIONS(1);

	private int code;

	private ConnectResourceEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static ConnectResourceEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SENDER_CANT_SUPPORT_OPERATIONS;
		}
		case 1: {
			return SENDER_CAN_SUPPORT_OPERATIONS;
		}
		default: {
			return null;
		}
		}
	}

}
