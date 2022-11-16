package com.agnity.inapitutcs2.enumdata.bearercapability;
/**
 * This enum represents Network Independent Clock on Transmission.
 * @author Mriganka
 */
public enum NicOnTxEnum {
	
	/**
	 * 0-Not required to send data with network independent clock
	 * 1-Required to send data with network independent clock
	 */
	NOT_REQUIRED(0), REQUIRED(1);
	
	private int code;

	private NicOnTxEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NicOnTxEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NOT_REQUIRED	; }
		case 1: { return 	REQUIRED	; }
		default: { return null; }
		}
	}
}
