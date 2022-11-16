package com.genband.isup.enumdata;

/**
 * Enum for Additional Party's Category name
 * @author rarya
 *
 */

public enum AdditionalPartyCatNameEnum {
	
	/**
	 * 251-Mobile additional partyt's category 3
	 * 252-Mobile additional party's category 2
	 * 253-Mobile additional party's category 1
	 * 254-PSTN additional part's category 1
	 */
	
	MOB_ADD_PARTY_CAT_3(251), MOB_ADD_PARTY_CAT_2(252), MOB_ADD_PARTY_CAT_1(253), 
	PSTN_ADD_PARTY_CAT_1(254);
	
	private int code;

	private AdditionalPartyCatNameEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static AdditionalPartyCatNameEnum fromInt(int num) {
		switch (num) {
			case 251: { return MOB_ADD_PARTY_CAT_3; }
			case 252: { return MOB_ADD_PARTY_CAT_2; }
			case 253: { return MOB_ADD_PARTY_CAT_1; }
			case 254: { return PSTN_ADD_PARTY_CAT_1; }
			default: { return null; }
		}
	}
}

