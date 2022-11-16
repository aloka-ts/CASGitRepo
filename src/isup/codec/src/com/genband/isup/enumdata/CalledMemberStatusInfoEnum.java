package com.genband.isup.enumdata;

/**
 * Enum for Event Indicator
 * @author vgoel
 *
 */
public enum CalledMemberStatusInfoEnum {

	/**
	 *  0-ZERO
	 *  1-ONE
	 *  2-TWO
	 *  3-THREE
	 *  4-FOUR
	 *  5-FIVE
	 *  7-SEVEN
	 *  8-EIGHT 
	 */
	
	ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SEVEN(7), EIGHT(8);
	 
	private int code;

	private CalledMemberStatusInfoEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static CalledMemberStatusInfoEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	ZERO	; }
		case 1: { return 	ONE	    ; }
		case 2: { return 	TWO	    ; }
		case 3: { return 	THREE	; }
		case 4: { return 	FOUR	; }
		case 5: { return 	FIVE	; }
		case 7: { return 	SEVEN	; }
		case 8: { return    EIGHT   ; }
		default: { return null; }
		}
	}
}
