package com.agnity.win.enumdata;

public enum RemoteUserInteractionEnum {

	/* ANNOUNCEMENTS 
	 * DecimalValue   Meaning 
	 *         0       System Not Capable of Interacting with user
	 *         1       System Capable of Interacting with user
	 */

	SYSTEM_CANNOT_INTERACT_WITH_USER(0), SYSTEM_CAN_INTERACT_WITH_USER(
			1);

	private int code;

	private RemoteUserInteractionEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static RemoteUserInteractionEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SYSTEM_CANNOT_INTERACT_WITH_USER;
		}
		case 1: {
			return SYSTEM_CAN_INTERACT_WITH_USER;
		}
		default: {
			return null;
		}
		}
	}

}
