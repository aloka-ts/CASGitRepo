package com.agnity.camelv2.enumdata;

/**
 * This enum represent Number qualifier indicator.
 * 
 * @author nkumar
 *
 */
public enum NumQualifierIndEnum {

	/**
	 * 0-reserved (dialed digits) (national use)
	 * 1-additional called number (national use)
	 * 2-reserved (supplemental user provided calling number -failed network screening) (national use)
	 * 3-reserved (supplemental user provided calling number - not screened) (national use)
	 * 4-reserved (redirecting terminating number) (national use)
	 * 5-additional connected number
	 * 6-additional calling party number
	 * 7-reserved for additional original called number
	 * 8-reserved for additional redirecting number
	 * 9-reserved for additional redirection number
	 * 10-reserved (used in 1992 version)
	 * 11->127-spare
	 * 128->254-reserved for national use
	 * 255-reserved for expansion
	 */
	ADD_CALLED_NO(1), ADD_CONNECTED_NO(5), ADD_CALLING_NO(6), SPARE(11) ;
		
	private int code;

	private NumQualifierIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NumQualifierIndEnum fromInt(int num) {
		switch (num) {
			case 1: { return ADD_CALLED_NO; }
			case 5: { return ADD_CONNECTED_NO; }
			case 6: { return ADD_CALLING_NO; }
			case 11: { return SPARE; }
			default: { return null; }
		}
	}
}
