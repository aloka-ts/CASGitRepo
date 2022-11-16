package com.camel.CAPMsg;

/**
 * This enum represents the all api's in CAPSbb
 * exposed to Application.
 * @author nkumar
 *
 */
public enum SasCapApiEnum {

	UPDATE(0),
	CONNECT(1),
	PLAY(2),
	PLAYANDCOLLECT(3),
	DISCONNECT_IVR(4),
	RELEASE_CALL(5),
	APPLY_CHARGING(6),
	CONNECT_IVR(7),
	FURNISH_CHARGING(8),
	TC_END(9);
	
	private int code;

	private SasCapApiEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
}
