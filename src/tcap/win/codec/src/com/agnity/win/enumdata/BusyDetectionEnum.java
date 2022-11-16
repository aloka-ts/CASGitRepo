package com.agnity.win.enumdata;

public enum BusyDetectionEnum {

	/* BusyDetection 
	 * DecimalValue   Meaning 
	 *         0       SYSTEM CANNOT DETECT BUSY CONDITION
	 *         1       SYSTEM CAN DETECT BUSY CONDITION
	 */
	SYSTEM_CANNOT_DETECT_BUSY_CONDITION(0), SYSTEM_CAN_DETECT_BUSY_CONDITION(
			1);

	private int code;

	private BusyDetectionEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static BusyDetectionEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SYSTEM_CANNOT_DETECT_BUSY_CONDITION;
		}
		case 1: {
			return SYSTEM_CAN_DETECT_BUSY_CONDITION;
		}
		default: {
			return null;
		}
		}
	}

}
