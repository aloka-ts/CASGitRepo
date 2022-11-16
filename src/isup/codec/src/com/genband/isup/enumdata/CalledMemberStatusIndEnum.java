package com.genband.isup.enumdata;

/**
 * Enum for Event Indicator
 * @author vgoel
 *
 */
public enum CalledMemberStatusIndEnum {

	/**
	 *  0-Not Set
	 *  1-Set
	 */
	
	NOT_SET(0), SET(1);
	 
	private int code;

	private CalledMemberStatusIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static CalledMemberStatusIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NOT_SET	; }
		case 1: { return 	SET	; }
		default: { return null; }
		}
	}
}
