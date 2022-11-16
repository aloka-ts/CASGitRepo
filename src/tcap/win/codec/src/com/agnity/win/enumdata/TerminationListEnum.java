package com.agnity.win.enumdata;

public enum TerminationListEnum {

	/* TerminationList 
	 * DecimalValue   Meaning 
	 *         0       SYSTEM CANNOT SUPPORT TERMINATION LIST PARAMETER
	 *         1       SYSTEM CAN SUPPORT TERMINATION LIST PARAMETER
	 */
	SYSTEM_CANNOT_SUPPORT_TERMINATIONLIST_PARAM(0), SYSTEM_CAN_SUPPORT_TERMINATIONLIST_PARAM(
			1);

	private int code;

	private TerminationListEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static TerminationListEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SYSTEM_CANNOT_SUPPORT_TERMINATIONLIST_PARAM;
		}
		case 1: {
			return SYSTEM_CAN_SUPPORT_TERMINATIONLIST_PARAM;
		}
		default: {
			return null;
		}
		}
	}

}
