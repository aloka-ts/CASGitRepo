package com.agnity.inapitutcs2.enumdata.bearercapability;
/**
 * This enum represents Mode of operation.
 * @author Mriganka
 */
public enum OperationModeEnum {
	
	/**
	 * 0-Bit transparent mode of operation
	 * 1-Protocol sensitive mode of operation
	 */
	BIT_TRANSPARENT(0), PROTOCOL_SENSITIVE(1);
	
	private int code;

	private OperationModeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static OperationModeEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	BIT_TRANSPARENT	; }
		case 1: { return 	PROTOCOL_SENSITIVE	; }
		default: { return null; }
		}
	}
}
