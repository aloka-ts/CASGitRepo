package com.agnity.camelv2.datatypes;

import org.apache.log4j.Logger;

import com.agnity.camelv2.exceptions.InvalidInputException;
import com.agnity.camelv2.util.NonAsnArg;
import com.agnity.camelv2.util.Util;

/**
 * This class have parameters for Local Area
 * Identification fixed length.
 * @author nkumar
 *
 */
public class LAIFixedLenDataType {

	String mobileCountryCode ;

	String mobileNetworkCode ;

	String locationAreaCode ;

	private static Logger logger = Logger.getLogger(LAIFixedLenDataType.class);

	/**
	 * This function will encode Local Area Identification fixed length.
	 * @param mobileCountryCode
	 * @param mobileNetworkCode
	 * @param locationAreaCode
	 * @return encoded data of LAIFixedLenDataType
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeLAI(String mobileCountryCode, String mobileNetworkCode, String locationAreaCode) throws InvalidInputException{

		logger.info("encodeLAI:Enter");
		if(mobileCountryCode == null || mobileCountryCode.equals(" ")){
			logger.error("encodeLAI: InvalidInputException(mobileCountryCode is null or blank)");
			throw new InvalidInputException("mobileCountryCode is null or blank");
		}
		if(mobileNetworkCode == null || mobileNetworkCode.equals(" ")){
			logger.error("encodeLAI: InvalidInputException(mobileNetworkCode is null or blank)");
			throw new InvalidInputException("mobileNetworkCode is null or blank");
		}
		if(locationAreaCode == null || locationAreaCode.equals(" ")){
			logger.error("encodeLAI: InvalidInputException(locationAreaCode is null or blank)");
			throw new InvalidInputException("locationAreaCode is null or blank");
		}

		byte[] mobileCC = NonAsnArg.tbcdStringEncoder(mobileCountryCode);
		byte[] mobileNC = NonAsnArg.tbcdStringEncoder(mobileNetworkCode);
		int len = 3 + (locationAreaCode.length()+1)/2 ;
		byte[] lai = new byte[len];

		for(int k= 0; k < mobileCC.length; k++){
			lai[k] = mobileCC[k];
		}
		if(mobileNetworkCode.length() >2){	
			logger.debug("encodeLAI: network code length >2");
			logger.debug("encodeLAI:mobileNC[1]"+ Util.conversiontoBinary(mobileNC[1]));
			lai[1] = (byte)((mobileNC[1] << 4) | (lai[1] & 0xF));
			logger.debug("encodeLAI:lai[1]"+ Util.conversiontoBinary(lai[1]));
		}

		lai[2] = mobileNC[0];
		byte[] locAreaCode = NonAsnArg.tbcdStringEncoder(locationAreaCode);

		int j = 3; 
		for(int k= 0; k < locAreaCode.length; k++){
			lai[j++] = locAreaCode[k];
		}
		if(logger.isDebugEnabled())
		logger.debug("encodeLAI:Encoded Lai: " + Util.formatBytes(lai));
		
		logger.info("encodeLAI:Exit");
		return lai ;

	}
	/**
	 * This function will decode LAI fixed length.
	 * @param data
	 * @return object of LAIFixedLenDataType
	 * @throws InvalidInputException
	 */
	public static LAIFixedLenDataType decodeLAI(byte[] data) throws InvalidInputException{
		logger.info("decodeLAI:Enter");
		if(logger.isDebugEnabled())
		logger.debug("decodeLAI: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeLAI: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		String lai = NonAsnArg.TbcdStringDecoder(data, 0);
		logger.debug("decodeLAI: lai: "+ lai);
		String mobileCC = lai.substring(0, 3);
		String mobileNC = (lai.substring(4, 6).concat(lai.substring(3, 4))).trim();
		String localAreaCode = lai.substring(6);
		LAIFixedLenDataType laiData = new LAIFixedLenDataType();
		laiData.mobileCountryCode = mobileCC ;
		laiData.mobileNetworkCode = mobileNC ;
		laiData.locationAreaCode = localAreaCode ;
		logger.debug("decodeLAI: Output<--" + laiData.toString());
		logger.info("decodeLAI:Exit");
		return laiData ;
	}

	public String getMobileCountryCode() {
		return mobileCountryCode;
	}

	public String getMobileNetworkCode() {
		return mobileNetworkCode;
	}

	public String getLocationAreaCode() {
		return locationAreaCode;
	}

	public void setMobileCountryCode(String mobileCountryCode) {
		this.mobileCountryCode = mobileCountryCode;
	}
	public void setMobileNetworkCode(String mobileNetworkCode) {
		this.mobileNetworkCode = mobileNetworkCode;
	}
	public void setLocationAreaCode(String locationAreaCode) {
		this.locationAreaCode = locationAreaCode;
	}
	public String toString(){

		String obj = "mobileCountryCode:"+ mobileCountryCode + " ,mobileNetworkCode:"+ mobileNetworkCode + " ,locationAreaCode:" + locationAreaCode ;

		return obj ;
	}
}
