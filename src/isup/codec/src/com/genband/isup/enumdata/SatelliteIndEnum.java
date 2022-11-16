package com.genband.isup.enumdata;

/**
 * Enum for Satellite Indicator
 * @author vgoel
 *
 */

public enum SatelliteIndEnum {

	/**
	 * 0-no satellite circuit in the connection
	 * 1-one satellite circuit in the connection
	 * 2-two satellite circuit in the connection
	 * 3-spare
	 */
	NO_SATELLITE(0), ONE_SATELLITE(1), TWO_SATELLITE(2), SPARE(3);

	private int code;

	private SatelliteIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static SatelliteIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return NO_SATELLITE; }
			case 1: { return ONE_SATELLITE; }
			case 2: { return TWO_SATELLITE; }
			case 3: { return SPARE; }
			default: { return null; }
		}
	}
}
