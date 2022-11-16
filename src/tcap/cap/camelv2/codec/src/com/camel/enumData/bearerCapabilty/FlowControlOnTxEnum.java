package com.camel.enumData.bearerCapabilty;
/**
 * This enum represents Flow control on transmission.
 * @author vgoel
 */
public enum FlowControlOnTxEnum {
	
	/**
	 * 0-Not required to send data with flow control mechanism
	 * 1-Required to send data with flow control mechanism
	 */
	NOT_REQUIRED(0), REQUIRED(1);
	
	private int code;

	private FlowControlOnTxEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static FlowControlOnTxEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NOT_REQUIRED	; }
		case 1: { return 	REQUIRED	; }
		default: { return null; }
		}
	}
}
