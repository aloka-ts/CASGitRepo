package com.genband.inap.enumdata;


/**
 * Enum for Mobile Additional Party's Category 2
 * @author vgoel
 *
 */

public enum MobileAdtnlPartyCat2Enum {

	/**
	 * 0-spare
	 * 1-mobile (analog)
	 * 2-mobile (N/J-TACS)
	 * 3-mobile (PDC 800MHz)
	 * 4-mobile (PDC 1.5GHz)
	 * 5-Mobile (N-STAR satellite)
	 * 6-Mobile (cdmaOne 800MHz)
	 * 7-Mobile (Iridium satellite)
	 * 8-Mobile (IMT-2000)
	 * 9-Mobile (NTT network depended PHS)
	 */
	SPARE(0), ANALOG(1), NJ_TACS(2), PDC_800(3), PDC_1_5(4), NSTAR_SATELLITE(5), CDMA_ONE_800(6), IRIDIUM_SATELLITE(7), IMT_2000(8), NTT_PHS(9);
	
	private int code;

	private MobileAdtnlPartyCat2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static MobileAdtnlPartyCat2Enum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return ANALOG; }
			case 2: { return NJ_TACS; }
			case 3: { return PDC_800; }
			case 4: { return PDC_1_5; }
			case 5: { return NSTAR_SATELLITE; }
			case 6: { return CDMA_ONE_800; }
			case 7: { return IRIDIUM_SATELLITE; }
			case 8: { return IMT_2000; }
			case 9: { return NTT_PHS; }
			default: { return null; }
		}
	}
}
