package com.genband.isup.enumdata.bearercapability;
/**
 * This enum represents Network Independent Clock on Reception.
 * @author vgoel
 */
public enum NicOnRxEnum {
	
	/**
	 * 0-Can not accept data with network independent clock
	 * 1-Can accept data with network independent clock
	 */
	CAN_NOT_ACCEPT(0), CAN_ACCEPT(1);
	
	private int code;

	private NicOnRxEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NicOnRxEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	CAN_NOT_ACCEPT	; }
		case 1: { return 	CAN_ACCEPT	; }
		default: { return null; }
		}
	}
}
