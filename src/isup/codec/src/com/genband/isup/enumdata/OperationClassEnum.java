package com.genband.isup.enumdata;

/**
 * Enum for Operation Class
 * @author vgoel
 *
 */

public enum OperationClassEnum {

	/**
	 * 0-class 1 (no report)
	 * 1-spare
	 * 3-class 4 (reported only when successful)
	 */
	CLASS_1(0), SPARE(1), CLASS_4(3);
	
	private int code;

	private OperationClassEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static OperationClassEnum fromInt(int num) {
		switch (num) {
			case 0: { return CLASS_1; }
			case 2: { return SPARE; }
			case 3: { return CLASS_4; }
			default: { return null; }
		}
	}
}
