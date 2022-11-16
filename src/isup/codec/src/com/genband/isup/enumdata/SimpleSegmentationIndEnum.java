package com.genband.isup.enumdata;

/**
 * Enum for Simple segmentation indicator
 * @author vgoel
 *
 */
public enum SimpleSegmentationIndEnum {

	/**
	 *  0-no additional information will be sent
	 *  1-additional information will be sent in a segmentation message
	 */
	
	NO_INFORMATION(0), ADDITIONAL_INFORMATION(1);
	 
	private int code;

	private SimpleSegmentationIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static SimpleSegmentationIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NO_INFORMATION	; }
		case 1: { return 	ADDITIONAL_INFORMATION	; }
		default: { return null; }
		}
	}
}
