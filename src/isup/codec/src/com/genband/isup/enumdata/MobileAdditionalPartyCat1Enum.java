package com.genband.isup.enumdata;

/**
 * Enum for Mobile Additional Party's Category 1
 * @author rarya
 *
 */

public enum MobileAdditionalPartyCat1Enum {
	
	/**
	 * 0-Spare
	 * 1-Mobile (automobile and portable phone service)
	 * 2-Mobile (Maritime telephone Service)
	 * 3-Mobile (in-flight telephone service)
	 * 4-Mobile (Pager)
	 * 5-PHS (PHS Service)
	 */
	
	MOB_SPARE(0), MOB_AUTO_AND_PORTABLE_PHONE_SRV(1), MOB_MARITIME_TEL_SRV(2), 
	MOB_INFLIGHT_TEL_SRV(3), MOB_PAGER(4), PHS_SRV(5);
	
	private int code;

	private MobileAdditionalPartyCat1Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static MobileAdditionalPartyCat1Enum fromInt(int num) {
		switch (num) {
			case 0: { return MOB_SPARE; }
			case 1: { return MOB_AUTO_AND_PORTABLE_PHONE_SRV; }
			case 2: { return MOB_MARITIME_TEL_SRV; }
			case 3: { return MOB_INFLIGHT_TEL_SRV; }
			case 4: { return MOB_PAGER; }
			case 5: { return PHS_SRV; }
			default: { return null; }
		}
	}
}

