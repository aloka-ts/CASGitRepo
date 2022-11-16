package com.camel.CAPMsg;

import com.camel.enumData.CauseValEnum;
import com.camel.enumData.CodingStndEnum;
import com.camel.enumData.LocationEnum;

public interface SasCapDefaultValue {
	
	/** Default value of location information */
	LocationEnum LOCTION_USER = LocationEnum.USER ;
	
	/** Default value of Coding Standard */
	CodingStndEnum CODING_STD_ITU_T = CodingStndEnum.ITUT_STANDARDIZED_CODING ;
	
	/** Default value of Cause Value */
	CauseValEnum CAUSE_VAL_NORMAL_CLEAR = CauseValEnum.Normal_call_clearing ;
	
}
