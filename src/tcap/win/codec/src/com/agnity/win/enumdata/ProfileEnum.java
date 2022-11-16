package com.agnity.win.enumdata;

public enum ProfileEnum {

	/* Profile 
	 * DecimalValue   Meaning 
	 *         0       SYSTEM CANNOT SUPPORT IS41C profile PARAMETERS.
	 *         1       SYSTEM CAN SUPPORT IS41C profile PARAMETERS
	 */
	SYSTEM_CANNOT_SUPPORT_IS41C_PROFILE_PARAMS(0), SYSTEM_CAN_SUPPORT_IS41C_PROFILE_PARAMS(
			1);

	private int code;

	private ProfileEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static ProfileEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SYSTEM_CANNOT_SUPPORT_IS41C_PROFILE_PARAMS;
		}
		case 1: {
			return SYSTEM_CAN_SUPPORT_IS41C_PROFILE_PARAMS;
		}
		default: {
			return null;
		}
		}
	}

}
