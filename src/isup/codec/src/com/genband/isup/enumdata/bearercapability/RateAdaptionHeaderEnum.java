package com.genband.isup.enumdata.bearercapability;
/**
 * This enum represents Rate adaption header/no header.
 * @author vgoel
 */
public enum RateAdaptionHeaderEnum {
	
	/**
	 * 0-Rate adaption header not included
	 * 1-Rate adaption header included
	 */
	NOT_INCLUDED(0), INCLUDED(1);
	
	private int code;

	private RateAdaptionHeaderEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static RateAdaptionHeaderEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NOT_INCLUDED	; }
		case 1: { return 	INCLUDED	; }
		default: { return null; }
		}
	}
}
