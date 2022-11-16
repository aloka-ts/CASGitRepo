package com.genband.isup.enumdata;

/**
 * Enum for suspend/resume indicators
 * @author vgoel
 *
 */
public enum SusResIndEnum {

	/**
	 * 0-ISDN subscriber initiated
	 * 1-network initiated
	 */
	ISDN_SUBSCRIBER_INITIATED(0), NETWORK_INITIATED(1);
	
	private int code;

	private SusResIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static SusResIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return ISDN_SUBSCRIBER_INITIATED; }
			case 1: { return NETWORK_INITIATED; }
			default: { return null; }
		}
	}
}
