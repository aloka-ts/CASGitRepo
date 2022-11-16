package com.genband.isup.enumdata;

/**
 * Enum for Echo Control Device Indicator
 * @author vgoel
 *
 */

public enum EchoContDeviceIndEnum {

	/**
	 * 0-outgoing echo control device not included
	 * 1-outgoing echo control device included
	 */
	DEVICE_NOT_INCLUDED(0), DEVICE_INCLUDED(1);

	private int code;

	private EchoContDeviceIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static EchoContDeviceIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return DEVICE_NOT_INCLUDED; }
			case 1: { return DEVICE_INCLUDED; }
			default: { return null; }
		}
	}
}
