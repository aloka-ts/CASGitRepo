package com.camel.enumData.bearerCapabilty;
/**
 * This enum represents Multiple frame establishment support in data link.
 * @author vgoel
 */
public enum MultipleFrameEnum {
	
	/**
	 * 0-Multiple frame establishment not supported
	 * 1-Multiple frame establishment supported
	 */
	NOT_SUPPORTED(0), SUPPORTED(1);
	
	private int code;

	private MultipleFrameEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static MultipleFrameEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NOT_SUPPORTED	; }
		case 1: { return 	SUPPORTED	; }
		default: { return null; }
		}
	}
}
