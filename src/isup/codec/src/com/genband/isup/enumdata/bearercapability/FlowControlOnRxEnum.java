package com.genband.isup.enumdata.bearercapability;
/**
 * This enum represents Flow control on Reception.
 * @author vgoel
 */
public enum FlowControlOnRxEnum {
	
	/**
	 * 0-Can not accept data with flow control mechanism
	 * 1-Can accept data with flow control mechanism
	 */
	CAN_NOT_ACCEPT(0), CAN_ACCEPT(1);
	
	private int code;

	private FlowControlOnRxEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static FlowControlOnRxEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	CAN_NOT_ACCEPT	; }
		case 1: { return 	CAN_ACCEPT	; }
		default: { return null; }
		}
	}
}
