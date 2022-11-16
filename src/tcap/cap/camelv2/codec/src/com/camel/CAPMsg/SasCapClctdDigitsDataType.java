package com.camel.CAPMsg;

import asnGenerated.ErrorTreatment;

/**
 * This class have parameters for Collected digits 
 * used in the Prompt and Collect User Information.
 * @author nkumar
 *
 */
public class SasCapClctdDigitsDataType {

	/** This parameter specifies the minimum number of valid digits to be collected.Default value is 1.Range min = 1L, max = 16L  */
	public Integer minimumNbOfDigits = 1;
	/** This parameter specifies the maximum number of valid digits to be collected.Default value is 5. Range min = 1L, max = 16L  */
	public Integer maximumNbOfDigits = 5;
	/** This parameter indicates the digit string used to signal the end of input. It is an optional field.Range min = 1L, max = 2L  */
	public String endOfReplyDigit ;
	/** This parameter indicates the cancel digit string that may be entered by the user to request a retry. It is an optional field
	 * Range min = 1L, max = 2L  
	 * */
	public String cancelDigit ;
	/** This parameter indicates the start digit string that indicates the start of the valid digits to be collected It is an optional field
	 * Range min = 1L, max = 2L  
	 **/
	public String startDigit ;
	/** If this parameter is present, then the first digit shall be received by the gsmSRF before first-digit timer
	 *	expiration. It is an optional field. Range min = 1L, max = 127L */
	public Integer firstDigitTimeOut ;
	/**If this parameter is present, then any subsequent valid or invalid digit shall be received by the gsmSRF
		* before the inter-digit timer expires.. It is an optional field.Range min = 1L, max = 127L */
	public Integer interDigitTimeOut ;
	/** This parameter defines what specific action shall be taken by the gsmSRF in the event of error conditions
	 *	occurring..Default value is stdErrorAndInfo */
	public ErrorTreatment.EnumType errorTreatment = null ;
	/** If this parameter is TRUE, then the announcement shall interrupted after the first valid or invalid digit is
	 *	received by the gsmSRF..Default value is true. */
	public boolean interruptableAnnInd = true ;
	/** If this parameter is FALSE, then all valid or invalid digits shall be entered by DTMF.Default value is false. */
	public Boolean voiceInformation = null ;
	/** If this parameter is FALSE, then no voice back information shall be given by the gsmSRF.Default value is false. */
	public Boolean voiceBack = null ;
	
	public boolean isEndOfReplyDigitPresent(){
		return endOfReplyDigit != null ;
	}
	
	public boolean isCancelDigitPresent(){
		return cancelDigit != null ;
	}
	
	public boolean isStartDigitPresent(){
		return startDigit != null ;
	}
	
	public boolean isFirstDigitTimeOutPresent(){
		return firstDigitTimeOut != null ;
	}
	
	public boolean isInterDigitTimeOutPresent(){
		return interDigitTimeOut != null ;
	}
	public boolean isMaximumNbOfDigitsPresent(){
		return maximumNbOfDigits != null ;
	}
}
