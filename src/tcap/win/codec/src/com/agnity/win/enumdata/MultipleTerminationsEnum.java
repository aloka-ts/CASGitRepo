package com.agnity.win.enumdata;

public enum MultipleTerminationsEnum {

	/* ANNOUNCEMENTS 
	 * DecimalValue   Meaning 
	 *         0       SYSTEM CANNOT ACCEPT TERMINATION
	 *         1 - 15  SYSTEM SUPPORTS THE NUMBER OF CALL LEGS INDICATED
	 */
	SYSTEM_CANNOT_ACCEPT_TERMINATION(0), SYSTEM_SUPPORTS_1_CALLLEG(1), SYSTEM_SUPPORTS_2_CALLLEG(2),SYSTEM_SUPPORTS_3_CALLLEG(3),
	SYSTEM_SUPPORTS_4_CALLLEG(4),SYSTEM_SUPPORTS_5_CALLLEG(5),SYSTEM_SUPPORTS_6_CALLLEG(6),SYSTEM_SUPPORTS_7_CALLLEG(7),SYSTEM_SUPPORTS_8_CALLLEG(8),
	SYSTEM_SUPPORTS_9_CALLLEG(9),SYSTEM_SUPPORTS_10_CALLLEG(10),SYSTEM_SUPPORTS_11_CALLLEG(11),SYSTEM_SUPPORTS_12_CALLLEG(12),SYSTEM_SUPPORTS_13_CALLLEG(13),
	SYSTEM_SUPPORTS_14_CALLLEG(14),SYSTEM_SUPPORTS_15_CALLLEG(15),;

	private int code;

	private MultipleTerminationsEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static MultipleTerminationsEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SYSTEM_CANNOT_ACCEPT_TERMINATION;
		}
		case 1: {
			return SYSTEM_SUPPORTS_1_CALLLEG;
		}
		case 2: {
			return SYSTEM_SUPPORTS_2_CALLLEG;
		}
		case 3: {
			return SYSTEM_SUPPORTS_3_CALLLEG;
		}
		case 4: {
			return SYSTEM_SUPPORTS_4_CALLLEG;
		}
		case 5: {
			return SYSTEM_SUPPORTS_5_CALLLEG;
		}
		case 6: {
			return SYSTEM_SUPPORTS_6_CALLLEG;
		}
		case 7: {
			return SYSTEM_SUPPORTS_7_CALLLEG;
		}
		case 8: {
			return SYSTEM_SUPPORTS_8_CALLLEG;
		}
		case 9: {
			return SYSTEM_SUPPORTS_9_CALLLEG;
		}
		case 10: {
			return SYSTEM_SUPPORTS_10_CALLLEG;
		}
		case 11: {
			return SYSTEM_SUPPORTS_11_CALLLEG;
		}
		case 12: {
			return SYSTEM_SUPPORTS_12_CALLLEG;
		}
		case 13: {
			return SYSTEM_SUPPORTS_13_CALLLEG;
		}
		case 14: {
			return SYSTEM_SUPPORTS_14_CALLLEG;
		}
		case 15: {
			return SYSTEM_SUPPORTS_15_CALLLEG;
		}
		default: {
			return null;
		}
		}
	}
	
}
