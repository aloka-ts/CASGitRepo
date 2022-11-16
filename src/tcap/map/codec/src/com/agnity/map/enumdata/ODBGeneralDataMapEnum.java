/****                                                              
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.            
 *                                                                 
 * This is proprietary source code of Agnity, Inc.                 
 *                                                                 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.                   
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.                                      
 ****/

package com.agnity.map.enumdata;

public enum ODBGeneralDataMapEnum {
	/**
	 * Index of the ODB General data in the bit string
	 */
	
	ALL_OG_CALLS_BARRED (0),
	INTL_OG_CALLS_BARRED(1),  
	INTL_OG_CALLS_NOT_TO_HPLMN_CNTRY_BARRED (2),
	INTERZONAL_OG_CALLS_BARRED(6),
	INTERZONAL_OG_CALLS_NOT_TO_HPLMN_CNTRY_Barred(7), 
	INTERZONAL_OG_CALLS_AND_INTL_OG_CALLS_NOT_To_HPLMN_CNTRY_BARRED(8), 
	PREMIUM_RATE_INFORMATION_OG_CALLS_BARRED (3),
	PREMIUM_RATE_ENTERTAINMENT_OG_CALLS_BARRED (4), 
	SS_ACCESS_BARRED (5), 
	ALL_ECT_BARRED (9),
	CHARGEABLE_ECT_BARRED (10),
	INTL_ECT_BARRED (11),
	INTERZONAL_ECT_BARRED (12),
	DBLY_CHARGEABLE_ECT_BARRED (13),
	MULTIPLE_ECT_BARRED (14),
	ALL_PACKET_ORIENTED_SERVICES_BARRED (15),
	ROAMER_ACCESS_TO_HPLMN_AP_BARRED (16),
	ROAMER_ACCESS_TO_VPLMN_AP_BARRED (17),
	ROAMING_OUTSIDE_PLMN_OG_CALLS_BARRED (18),
	ALL_IC_CALLS_BARRED (19),
	RAOMING_OUTSIDE_PLMN_IC_CALLS_BARRED (20),
	ROAMING_OUTSIDE_PLMN_CNTRY_IC_CALLS_BARRED (21),
	ROAMING_OUTSIDE_PLMN_BARRED (22),
	ROAMING_OUTSIDE_PLMN_COUNTRY_BARRED (23),
	REGISTRATION_ALL_CF_BARRED (24),
	REGISTRAION_CF_NOT_TO_HPLMN_BARRED (25),
	REGISTRATION_INTERZONAL_CF_BARRED (26),
	REGISTRAION_INTERZONAL_CF_NOT_TO_HPLMN_BARRED (27),
	REGISTRATION_INTERNATIONAL_CF_BARRED  (28);
	
	int code;
	
	private ODBGeneralDataMapEnum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static ODBGeneralDataMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return ALL_OG_CALLS_BARRED ;
			case 1: return INTL_OG_CALLS_BARRED;
			case 2: return INTL_OG_CALLS_NOT_TO_HPLMN_CNTRY_BARRED ;
			case 6: return INTERZONAL_OG_CALLS_BARRED;
			case 7: return INTERZONAL_OG_CALLS_NOT_TO_HPLMN_CNTRY_Barred;
			case 8: return INTERZONAL_OG_CALLS_AND_INTL_OG_CALLS_NOT_To_HPLMN_CNTRY_BARRED;
			case 3: return PREMIUM_RATE_INFORMATION_OG_CALLS_BARRED ;
			case 4: return PREMIUM_RATE_ENTERTAINMENT_OG_CALLS_BARRED ;
			case 5: return SS_ACCESS_BARRED ;
			case 9: return ALL_ECT_BARRED ;
			case 10: return CHARGEABLE_ECT_BARRED ;
			case 11: return INTL_ECT_BARRED ;
			case 12: return INTERZONAL_ECT_BARRED ;
			case 13: return DBLY_CHARGEABLE_ECT_BARRED ;
			case 14: return MULTIPLE_ECT_BARRED ;
			case 15: return ALL_PACKET_ORIENTED_SERVICES_BARRED ;
			case 16: return ROAMER_ACCESS_TO_HPLMN_AP_BARRED ;
			case 17: return ROAMER_ACCESS_TO_VPLMN_AP_BARRED ;
			case 18: return ROAMING_OUTSIDE_PLMN_OG_CALLS_BARRED ;
			case 19: return ALL_IC_CALLS_BARRED ;
			case 20: return RAOMING_OUTSIDE_PLMN_IC_CALLS_BARRED ;
			case 21: return ROAMING_OUTSIDE_PLMN_CNTRY_IC_CALLS_BARRED ;
			case 22: return ROAMING_OUTSIDE_PLMN_BARRED ;
			case 23: return ROAMING_OUTSIDE_PLMN_COUNTRY_BARRED ;
			case 24: return REGISTRATION_ALL_CF_BARRED ;
			case 25: return REGISTRAION_CF_NOT_TO_HPLMN_BARRED ;
			case 26: return REGISTRATION_INTERZONAL_CF_BARRED ;
			case 27: return REGISTRAION_INTERZONAL_CF_NOT_TO_HPLMN_BARRED ;
			case 28: return REGISTRATION_INTERNATIONAL_CF_BARRED  ;
			default: return null;
		}
	}
}
