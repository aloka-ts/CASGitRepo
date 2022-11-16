package com.genband.isup.enumdata;

/**
 * Enum for Continuity Check Indicator
 * @author vgoel
 *
 */

public enum ContCheckIndEnum {

	/**
	 * 0-continuity check not required
	 * 1-continuity check required on this circuit
	 * 2-continuity check performed on a previous circuit
	 * 3-spare
	 */
	CONTINUITY_NOT_REQUIRED(0), CONTINUITY_REQUIRED(1), CONTINUITY_PREVIOUS(2), SPARE(3);

	private int code;

	private ContCheckIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ContCheckIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return CONTINUITY_NOT_REQUIRED; }
			case 1: { return CONTINUITY_REQUIRED; }
			case 2: { return CONTINUITY_PREVIOUS; }
			case 3: { return SPARE; }
			default: { return null; }
		}
	}
}
