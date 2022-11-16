package com.agnity.win.enumdata;

public enum TerminationTreatmentEnum {


	/* TerminationTreatment
	 * DecimalValue   Meaning 
	 *         0       NOT_USED
	 *         1       MS TERMINATION
	 *         2       TERMINATION TO VOICEMAIL BOX FOR MSG STORAGE
	 *         3       TERMINATION TO VOICEMAIL BOX FOR MSG RETRIEVAL
	 *         4       DIALOGUE TERMINATION
	 */
	
	NOT_USED(0), MS_TERMINATION(1), VOICEMAIL_STORAGE(2), VOICEMAIL_RETRIEVAL(
			3), DIALOGUE_TERMINATION(4);

	private int code;

	private TerminationTreatmentEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static TerminationTreatmentEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return NOT_USED;
		}
		case 1: {
			return MS_TERMINATION;
		}
		case 2: {
			return VOICEMAIL_STORAGE;
		}
		case 3: {
			return VOICEMAIL_RETRIEVAL;
		}
		case 4: {
			return DIALOGUE_TERMINATION;
		}
		default: {
			return null;
		}
		}
	}

}
