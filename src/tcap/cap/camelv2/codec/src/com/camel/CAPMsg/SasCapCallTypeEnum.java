package com.camel.CAPMsg;

/**
 * This enum represents the type of call either Mobile originating 
 * or mobile terminating.
 * @author nkumar
 *
 */
public enum SasCapCallTypeEnum {

	MOBILE_ORIGIN(0),
	MOBILE_TERM(1);
	
	
	private int code ;

	private SasCapCallTypeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
}
