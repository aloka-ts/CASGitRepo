package com.genband.isup.enumdata;

/**
 * Enum for Feature Code
 * @author pgandhi
 *
 */

public enum FeatureCodeEnum {
	
	/**
	 * 123-SPARE (QUICK_LINE - SBTM)
	 * 125-Request for redialing 110/119
	 * 126-Request for on-network connection termination indication
	 * 127-Directory assistance service connected call
	 * 253-Rerouting request
	 * 254-VPN	 
	 */
	QUICK_LINE(123),REDIALING_REQUEST(125),CONN_TERM_IND(126),DIR_ASS_SVC_CONN_CALL(127),REROUTING_REQUEST(253),VPN(254);
	
	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	private FeatureCodeEnum(int c) {
		code = c;
	}
	
	public static FeatureCodeEnum fromInt(int num) {
		switch (num) {
			case 123: { return QUICK_LINE; }
			case 125: { return REDIALING_REQUEST; }
			case 126: { return CONN_TERM_IND; }
			case 127: { return DIR_ASS_SVC_CONN_CALL; }
			case 253: { return REROUTING_REQUEST; }
			case 254: { return VPN; }
			default: { return null; }
		}
	}

}