package com.camel.CAPMsg;

import com.camel.dataTypes.GenericDigitsDataType;

/**
 * This class have parameters of VariablePart
 * used in PC and PA. This is of choice type. So set the desired parameter only.
 * @author nkumar
 *
 */
public class SasCapVariablePartDataType {

	/** This is the field used in VariablePart parameter of MessageID.*/
	public Integer value ;
	
	/** This is the field used in VariablePart parameter of MessageID. Format must be HHMM */
	public String time ;
	
	/** This is the field used in VariablePart parameter of MessageID. Format must be YYYYMMDD*/
	public String date ;
	
	/** This is the field used in VariablePart parameter of MessageID.*/
	public String price ;
	
	/** This is the field used in VariablePart parameter of MessageID. */
	public GenericDigitsDataType number ;
	
	public boolean isValuePresent(){
		return value != null ;
	}
	
	public boolean isTimePresent(){
		return time != null ;
	}
	
	public boolean isDatePresent(){
		return date != null ;
	}
	
	public boolean isPricePresent(){
		return price != null ;
	}
	
	public boolean isNumberPresent(){
		return number != null ;
	}
}
