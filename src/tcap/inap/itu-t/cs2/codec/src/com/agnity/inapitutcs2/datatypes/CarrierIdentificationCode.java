package com.agnity.inapitutcs2.datatypes;

import java.io.Serializable;
import org.apache.log4j.Logger;

import com.agnity.inapitutcs2.exceptions.InvalidInputException;
import com.agnity.inapitutcs2.util.Util;


/**
 * Used for encoding and decoding of CarrierIdentificationCode
 * @author Mriganka
 *
 */
public class CarrierIdentificationCode implements Serializable {

	private static Logger logger = Logger.getLogger(CarrierIdentificationCode.class);	 
	
	/**
	 * Carrier identification Code
	 */
	String carrierIdentCode;
	
	public String getCarrierIdentCode() {
		return carrierIdentCode;
	}

	public void setCarrierIdentCode(String carrierIdentCode) {
		this.carrierIdentCode = carrierIdentCode;
	}

	/**
	 * This function will encode Carrier Identification Code.
	 * @param carrierIdentCode
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeCarrierIdentCode(String carrierIdentCode) throws InvalidInputException {
		if (logger.isInfoEnabled()) {
			logger.info("encodeCarrierIdentCode:Enter");
		}
		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(carrierIdentCode);
		int seqLength = 1 + bcdDigits.length;

		int i = 0;
		byte[] myParms = new byte[seqLength];
				
		// If even no. then set 8th bit 0 otherwise 1
		if (carrierIdentCode.length() % 2 == 0) {
			myParms[i++] = (byte) (0 << 7);
		} else {
			myParms[i++] = (byte) (1 << 7);
		}
		
		for (int j = 0; j < bcdDigits.length; j++) {
			myParms[i++] = bcdDigits[j];
		}
		
		if(logger.isDebugEnabled())
			logger.debug("encodeCarrierIdentCode:Encoded Carrier Identification Code: "+ Util.formatBytes(myParms));
		if (logger.isInfoEnabled()) {
			logger.info("encodeCarrierIdentCode:Exit");
		}
		return myParms;
	}
	
	/**
	 * This function will decode Carrier Identification Code.
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static CarrierIdentificationCode decodeCarrierIdentCode(byte[] data) throws InvalidInputException {
		if (logger.isInfoEnabled()) {
			logger.info("decodeCarrierIdentCode:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeCarrierIdentCode: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeCarrierIdentCode: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		CarrierIdentificationCode carrIdentCode = new CarrierIdentificationCode();
		int parity = (data[0] >> 7) & 0x1;
		if(data.length > 1){
			carrIdentCode.carrierIdentCode = AddressSignal.decodeAdrsSignal(data, 1 , parity);			
		}	
		if(logger.isDebugEnabled())
			logger.debug("decodeCarrierIdentCode: Output<--" + carrIdentCode.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeCarrierIdentCode:Exit");
		}
		return carrIdentCode ;
	}
	
	public String toString(){
		
		String obj = "carrierIdentCode:"+ carrierIdentCode ;
		return obj ;
	}
}
