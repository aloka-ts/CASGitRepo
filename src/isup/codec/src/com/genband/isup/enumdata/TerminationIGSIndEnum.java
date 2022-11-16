package com.genband.isup.enumdata;

/**
 * Enum for Event Indicator
 * @author vgoel
 *
 */
public enum TerminationIGSIndEnum {

	/**
	 *  0-IGS
	 *  1-GC
	 */
	
	IGS(0), GC(1);
	 
	private int code;

	private TerminationIGSIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static TerminationIGSIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	IGS	; }
		case 1: { return 	GC	; }
		default: { return null; }
		}
	}
}
