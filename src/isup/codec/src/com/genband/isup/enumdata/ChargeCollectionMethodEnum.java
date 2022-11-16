package com.genband.isup.enumdata;

/**
 * Enum for Charge Collection Method
 * @author vgoel
 *
 */

public enum ChargeCollectionMethodEnum {

	/**
	 * 0-Bill to subscriber (normal)
	 * 1-spare
	 */
	BILL_SUBSCRIBER(0), SPARE(1);
	
	private int code;

	private ChargeCollectionMethodEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ChargeCollectionMethodEnum fromInt(int num) {
		switch (num) {
			case 0: { return BILL_SUBSCRIBER; }
			case 1: { return SPARE; }
			default: { return null; }
		}
	}
}
