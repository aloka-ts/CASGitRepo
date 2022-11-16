package com.camel.enumData;

/**
 * THis enum represent Internal Network Number indicator (INN ind.)
 * @author nkumar
 *
 */
public enum IntNtwrkNumEnum {

	/**
	 * 0-routing to internal network number allowed
	 * 1-routing to internal network number not allowed
	 */
	ROUTING_ALLWD(0), ROUTING_NOT_ALLWD(1);
	
	private int code;

	private IntNtwrkNumEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static IntNtwrkNumEnum fromInt(int num) {
		switch (num) {
			case 0: { return ROUTING_ALLWD; }
			case 1: { return ROUTING_NOT_ALLWD; }
			default: { return null; }
		}
	}
}
