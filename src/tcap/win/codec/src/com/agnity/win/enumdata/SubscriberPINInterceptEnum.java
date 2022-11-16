package com.agnity.win.enumdata;

public enum SubscriberPINInterceptEnum {

	/* ANNOUNCEMENTS 
	 * DecimalValue   Meaning 
	 *         0       SYSTEM CANNOT SUPPORT LOCAL SPINI OPERATION
	 *         1       SYSTEM CAN SUPPORT LOCAL SPINI OPERATION
	 */
	SYSTEM_CANNOT_SUPPORT_LOCAL_SPINI_OP(0), SYSTEM_CAN_SUPPORT_LOCAL_SPINI_OP(
			1);

	private int code;

	private SubscriberPINInterceptEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static SubscriberPINInterceptEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SYSTEM_CANNOT_SUPPORT_LOCAL_SPINI_OP;
		}
		case 1: {
			return SYSTEM_CAN_SUPPORT_LOCAL_SPINI_OP;
		}
		default: {
			return null;
		}
		}
	}

}
