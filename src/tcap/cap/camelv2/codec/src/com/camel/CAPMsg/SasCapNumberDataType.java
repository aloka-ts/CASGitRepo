package com.camel.CAPMsg;
/**
 * This class have parameters of Number used in VariablePart
 * of PC or PA.All parameters are mandatory.
 */

import com.camel.enumData.EncodingSchemeEnum;
import com.camel.enumData.TypeOfDigitsEnum;

public class SasCapNumberDataType {

	/** This is the field used to encode number in VariablePart parameter of MessageID.*/
	public EncodingSchemeEnum encodingSchemeEnum ;
	
	/** This is the field used to encode number in VariablePart parameter of MessageID.*/
	public TypeOfDigitsEnum typeOfDigitsEnum ;
	
	/** This is the field used to encode number in VariablePart parameter of MessageID.*/
	public String digits ;
	
	public boolean isEncodingSchemeEnumPresent(){
		return encodingSchemeEnum != null ;
	}
	
	public boolean isTypeOfDigitsEnumPresent(){
		return typeOfDigitsEnum != null ;
	}
	
	public boolean isDigitsPresent(){
		return digits != null ;
	}
}
