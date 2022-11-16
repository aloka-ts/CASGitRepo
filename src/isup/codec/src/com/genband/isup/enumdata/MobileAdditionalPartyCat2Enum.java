package com.genband.isup.enumdata;

/**
 * Enum for Mobile Additional Party's Category 2
 * @author rarya
 *
 */

public enum MobileAdditionalPartyCat2Enum {
	
	/**
	 * 0-Spare
	 * 1-Mobile (Analog)
	 * 2-Mobile (N/J-TACS)
	 * 3-Mobile (PDC 800 MHz)
	 * 4-Mobile (PDC 1.5GHz)
	 * 5-Mobile (N-STAR satellite)
	 * 6-Mobile (cdmaOne 800MHz)
	 * 7-Mobile (Iridium satellite)
	 * 8-Mobile (IMT-2000)
	 * 9-Mobile (NTT network depended PHS)
	 */
	
	MOB_SPARE_2(0), MOB_ANALOG(1), MOB_NJ_TACS(2), MOB_PDC_800MHz(3), 
	MOB_PDC_1_5GHz(4), MOB_N_STAR_SAT(5), MOB_CDMA_ONE_800MHz(6),
	MOB_IRIDIUM_SAT(7), MOB_IMT_2000(8), MOB_NTT_NW (9);
	
	private int code;

	private MobileAdditionalPartyCat2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static MobileAdditionalPartyCat2Enum fromInt(int num) {
		switch (num) {
			case 0: { return MOB_SPARE_2; 		}
			case 1: { return MOB_ANALOG; 		}
			case 2: { return MOB_NJ_TACS; 		}	
			case 3: { return MOB_PDC_800MHz; 	}
			case 4: { return MOB_PDC_1_5GHz; 	}
			case 5: { return MOB_N_STAR_SAT; 	}
			case 6: { return MOB_CDMA_ONE_800MHz; 	}
			case 7: { return MOB_IRIDIUM_SAT; 	}
			case 8: { return MOB_IMT_2000; 		}
			case 9: { return MOB_NTT_NW; 		}
			default: { return null; 		}
		}
	}
}

