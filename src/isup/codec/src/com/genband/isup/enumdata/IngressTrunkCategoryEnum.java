package com.genband.isup.enumdata;

/**
 * Enum for Ingress Trunk Category Enum which is party of DPC Info parameters
 * @author rarya
 *
 */
public enum IngressTrunkCategoryEnum {

	/**
	 * Values as per 1405 SBTM RA 
	 * 0-UNKNOWN
	 * 1-GS
	 * 2-IGS
	 * 3-KDD
	 * 4-PHS_DP
	 * 5-GC
	 * 6-JR
	 * 7-MX
	 * 8-INTS
	 * 9-N61J
	 * 10-LS
	 */
	UNKNOWN(0), GS(1), IGS(2), KDD(3), PHS_DP(4), GC(5), JR(6), MX(7),
	INTS(8), N61J(9), LS(10);
	
	private int code;

	private IngressTrunkCategoryEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static IngressTrunkCategoryEnum fromInt(int num) {
		switch (num) {
			case 0: { return UNKNOWN; }
			case 1: { return GS; 			}
			case 2: { return IGS; 		}
			case 3: { return KDD; 	  }
			case 4: { return PHS_DP;  }
			case 5: { return GC; 			}
			case 6: { return JR; 			}
			case 7: { return MX; 			}
			case 8: { return INTS; 		}
			case 9: { return N61J; 		}
			case 10: { return LS; 		}
			default: { return null; 	}
		}
	}
}
