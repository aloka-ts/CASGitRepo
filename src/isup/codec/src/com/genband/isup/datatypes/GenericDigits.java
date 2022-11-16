package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.DigitCatEnum;
import com.genband.isup.enumdata.EncodingSchemeEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * Used for encoding and decoding of GenericDigits
 * @author vgoel
 *
 */
public class GenericDigits {

	private static Logger logger = Logger.getLogger(GenericDigits.class);
	 
	public EncodingSchemeEnum encodingSchemeEnum;
	public DigitCatEnum digitCatEnum;
	public String digits;
		
	
	/**
	 * This function will encode Generic Digits. "Type Of Digits" is irrelevant to the CAP
	 * @param encodingSchemeEnum
	 * @param digits
	 * @return encoded data of GenericDigits
	 * @throws InvalidInputException
	 */
	public static byte[] encodeGenericDigits(EncodingSchemeEnum encodingSchemeEnum, DigitCatEnum digitCatEnum, String digits) throws InvalidInputException{

		logger.info("encodeGenericDigits:Enter");
		
		int size = ((digits.length() + 1)/2) + 1;
		byte[] genericDigits = new byte[size] ;
		
		genericDigits[0] = (byte)(encodingSchemeEnum.getCode()<<5 | digitCatEnum.getCode());
		//TODO encoding scheme bcd is implemented only
		byte[] encodedDigits = AddressSignal.encodeAdrsSignal(digits);

		for(int k= 0 ; k < encodedDigits.length ; k++){
			genericDigits[k+1] = encodedDigits[k];
		}
		if(logger.isDebugEnabled())
			logger.debug("encodeGenericDigits:Encoded data: "+ Util.formatBytes(genericDigits));
		logger.info("encodeGenericDigits:Exit");
		
		return genericDigits ;
	}

	/**
	 * This function will decode Generic Digits.
	 * @param data
	 * @return object of GenericDigitsDataType
	 * @throws InvalidInputException 
	 */
	public static GenericDigits decodeGenericDigits(byte[] data) throws InvalidInputException{
		logger.info("decodeGenericDigits:Enter");
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		GenericDigits genericDigits = new GenericDigits();
		
		genericDigits.encodingSchemeEnum = EncodingSchemeEnum.fromInt(data[0]>>5 & 0x7);
		//TODO decoding for other type of encoding scheme 
		if(genericDigits.encodingSchemeEnum.getCode() == 0)
			genericDigits.digits = AddressSignal.decodeAdrsSignal(data, 1, 0);
		else if(genericDigits.encodingSchemeEnum.getCode() == 1)
			genericDigits.digits = AddressSignal.decodeAdrsSignal(data, 1, 1);
		
		genericDigits.digitCatEnum = DigitCatEnum.fromInt(data[0] & 0x1F);
		
		logger.debug("decodeGenericDigits:Output: "+ genericDigits);
		logger.info("decodeGenericDigits:Exit");
		return genericDigits ;

	}
	
	public EncodingSchemeEnum getEncodingSchemeEnum() {
		return encodingSchemeEnum;
	}

	public void setEncodingSchemeEnum(EncodingSchemeEnum encodingSchemeEnum) {
		this.encodingSchemeEnum = encodingSchemeEnum;
	}

	public String getDigits() {
		return digits;
	}

	public void setDigits(String digits) {
		this.digits = digits;
	}
	
	public DigitCatEnum getDigitCatEnum() {
		return digitCatEnum;
	}

	public void setDigitCatEnum(DigitCatEnum digitCatEnum) {
		this.digitCatEnum = digitCatEnum;
	}
	
	public String toString(){
		String obj = "encodingSchemeEnum:"+ encodingSchemeEnum + " ,digits:"+ digits + ", digitCatEnum:" + digitCatEnum;
		return obj ;
	}

	
}