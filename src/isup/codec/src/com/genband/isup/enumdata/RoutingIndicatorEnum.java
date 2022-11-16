package com.genband.isup.enumdata;

/**
 * Enum for RoutingIndicator
 * @author vgoel
 *
 */

public enum RoutingIndicatorEnum {
	
	/**
	 * 0-Routing based on GT
	 * 1-Routing based on PC & SSN
	 */
	
	ROUTING_GT(0), ROUTING_PC_SSN(1);
	
	private int code;

	private RoutingIndicatorEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static RoutingIndicatorEnum fromInt(int num) {
		switch (num) {
			case 0: { return ROUTING_GT; }
			case 1: { return ROUTING_PC_SSN; }
			default: { return null; }
		}
	}
}
