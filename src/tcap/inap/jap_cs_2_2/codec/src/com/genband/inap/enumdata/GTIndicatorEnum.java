package com.genband.inap.enumdata;

/**
 * Enum for GTIndicator
 * @author vgoel
 *
 */

public enum GTIndicatorEnum {
	
	/**
	 * 0-No global title Included
	 * 1-GT:Nature of Address Indicator Only
	 * 2-GT:Translation Type Only
	 * 3-GT:TransType, NumPlan & EncScheme
	 * 4-GT:TransType, NumPlan, EncScheme & NatOfAddress
	 * 5-Spare, International
	 * 8-Spare, National
	 * 15-Reserved for Extension
	 */
	
	NO_GT(0), GT_NATURE_ADDRESS(1), GT_TRANSLTAION_TYPE(2), GT_TRANSLATION_NUMPLAN_ENCSCHEME(3), GT_TRANSLATION_NUMPLAN_ENCSCHEME_NATOFADD(4),
	SPARE_INTERNATIONAL(5), SPARE_NATIONAL(8), RESRVED(15);
	
	private int code;

	private GTIndicatorEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static GTIndicatorEnum fromInt(int num) {
		switch (num) {
			case 0: { return NO_GT; }
			case 1: { return GT_NATURE_ADDRESS; }
			case 2: { return GT_TRANSLTAION_TYPE; }
			case 3: { return GT_TRANSLATION_NUMPLAN_ENCSCHEME; }
			case 4: { return GT_TRANSLATION_NUMPLAN_ENCSCHEME_NATOFADD; }
			case 5: { return SPARE_INTERNATIONAL; }
			case 8: { return SPARE_NATIONAL; }
			case 15: { return RESRVED; }
			default: { return null; }
		}
	}
}
