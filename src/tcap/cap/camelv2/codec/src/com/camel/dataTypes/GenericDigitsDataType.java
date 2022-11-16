package com.camel.dataTypes;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.camel.enumData.EncodingSchemeEnum;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.Util;

/**
 * This class have parameters for Generic Digits.
 * @author vgoel
 *
 */
public class GenericDigitsDataType {

	private static Logger logger = Logger.getLogger(GenericDigitsDataType.class);
	static {
		 PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	 }
	 
	public EncodingSchemeEnum encodingSchemeEnum;
	public String digits;
		
	
	/**
	 * This function will encode Generic Digits. "Type Of Digits" is irrelevant to the CAP
	 * @param encodingSchemeEnum
	 * @param digits
	 * @return encoded data of GenericDigits
	 * @throws InvalidInputException
	 */
	public static byte[] encodeGenericDigits(EncodingSchemeEnum encodingSchemeEnum, String digits) throws InvalidInputException{

		logger.info("encodeGenericDigits:Enter");
		
		int size = ((digits.length() + 1)/2) + 1;
		byte[] genericDigits = new byte[size] ;
		
		genericDigits[0] = (byte)(encodingSchemeEnum.getCode()<<5);
		//TODO encoding scheme bcd is implemented only
		byte[] encodedDigits = AdrsSignalDataType.encodeAdrsSignal(digits);

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
	public static GenericDigitsDataType decodeGenericDigits(byte[] data) throws InvalidInputException{
		logger.info("decodeGenericDigits:Enter");
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		GenericDigitsDataType genericDigits = new GenericDigitsDataType();
		
		genericDigits.encodingSchemeEnum = EncodingSchemeEnum.fromInt(data[0]>>5 & 0x7);
		//TODO decoding for other type of encoding scheme 
		if(genericDigits.encodingSchemeEnum.getCode() == 0)
			genericDigits.digits = AdrsSignalDataType.decodeAdrsSignal(data, 1, 0);
		else if(genericDigits.encodingSchemeEnum.getCode() == 1)
			genericDigits.digits = AdrsSignalDataType.decodeAdrsSignal(data, 1, 1);
		
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
	
	public String toString(){
		String obj = "encodingSchemeEnum:"+ encodingSchemeEnum + " ,digits:"+ digits;
		return obj ;
	}

	
}