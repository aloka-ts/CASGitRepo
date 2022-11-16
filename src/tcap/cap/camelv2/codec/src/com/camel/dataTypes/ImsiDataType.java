package com.camel.dataTypes;

import org.apache.log4j.Logger;

import com.camel.exceptions.InvalidInputException;

/**
 * This class contains parameter for IMSI.
 * @author nkumar
 *
 */
public class ImsiDataType extends LAIFixedLenDataType {
	
	
	private static Logger logger = Logger.getLogger(ImsiDataType.class);
	/**
	 * This function will encode IMSI.
	 * @param mobileCountryCode
	 * @param mobileNetworkCode
	 * @param msin
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeImsi(String mobileCountryCode, String mobileNetworkCode, String msin) throws InvalidInputException{
			
		logger.info("encodeImsi:Enter");
		logger.info("encodeImsi:Exit");
		 return encodeLAI(mobileCountryCode, mobileNetworkCode, msin);
	}
	
	/**
	 * This function will deccode IMSI.
	 * @param data
	 * @return object of ImsiDataType
	 * @throws InvalidInputException
	 */
	public static ImsiDataType decodeImsi(byte[] data) throws InvalidInputException{
		logger.info("decodeImsi:Enter"); 
		LAIFixedLenDataType laiData = decodeLAI(data);
		 ImsiDataType imsiDataType = new ImsiDataType();
		 imsiDataType.locationAreaCode = laiData.locationAreaCode ;
		 imsiDataType.mobileCountryCode = laiData.mobileCountryCode ;
		 imsiDataType.mobileNetworkCode = laiData.mobileNetworkCode ;
		 logger.info("decodeImsi:Exit");
		 return imsiDataType ;
	}

}
