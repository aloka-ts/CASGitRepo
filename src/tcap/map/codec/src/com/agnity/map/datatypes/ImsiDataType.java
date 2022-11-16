package com.agnity.map.datatypes;

import org.apache.log4j.Logger;

import com.agnity.map.exceptions.InvalidInputException;

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
	 * This method encodes ImsiDataType object to byte array
	 * @return byte array
	 * @throws InvalidInputException
	 */
	
	public byte[] encode() throws InvalidInputException {
		return ImsiDataType.encodeImsi(this.mobileCountryCode, this.mobileNetworkCode,
				this.locationAreaCode);
	}
	
	/**
	 * This function will decode IMSI.
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
	
	/**
	 * Set the MSIN for IMSI
	 * @param msin
	 */

	public void setMsin(String msin) {
		this.locationAreaCode = msin; // Internally we'll use LA for msin
	}
}
