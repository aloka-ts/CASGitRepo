package com.genband.isup.enumdata;

/**
 * Enum for Signaling Elemnt Type
 * @author vgoel
 *
 */

public enum SignalElementTypeEnum {

	/**
	 * 0-spare
	 * 2-Invoke, activation:The operation that should be executed
	 * 3-success response
	 */
	SPARE(0), INVOKE_ACTIVATION(2), SUCCESS(3);
	
	private int code;

	private SignalElementTypeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static SignalElementTypeEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 2: { return INVOKE_ACTIVATION; }
			case 3: { return SUCCESS; }
			default: { return null; }
		}
	}
}
