package com.agnity.win.datatypes;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.MobileIdentificationNumber;
import com.agnity.win.asngenerated.MINType;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for Mobile Identification Number.
 * MobileIdentificationNumber (MIN) is a 10-digits representation of MS's MIN 
 * coded in BCD format. 
 */
public class NonAsnMobileIdNum extends NonASNAddressSignal implements Serializable{
	
	private static Logger logger = Logger.getLogger(NonAsnMobileIdNum.class);
	
	/*
	 * Mobile Identification Number
	 */
	String mobileIdNum;
	
	/**
	 * This function will decode MIN as per specification TIA-EIA-41-D
	 * section 
	 * @param data
	 * @return decoded Digits
	 * @throws InvalidInputException 
	 */
	public static NonAsnMobileIdNum decodeMin(byte[] data) throws InvalidInputException {
		if(logger.isDebugEnabled())
			logger.debug("decodeMin: Input--> data:" + Util.formatBytes(data));

		if(data == null){
			logger.error("decodeMin: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		
		NonAsnMobileIdNum min = new NonAsnMobileIdNum();
		
		if(data.length > 0){
			//parity is zero because number of digits is even for MIN i.e. 10....
			//int parity = data.length%2;
			int parity =0;
			min.mobileIdNum = NonASNAddressSignal.decodeAdrsSignal(data, 0, parity);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("decodeMin: Mobile Identification Number:" + min);
		
		return min;
	}
	
	/**
	 * This function will encode MIN as per specification TIA-EIA-41-D
	 * section 6.5.2.63
	 * @param min   - Mobile Identification Number
	 * @return byte[] of encoded MIN
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeMin(String min) throws InvalidInputException{
		if(logger.isDebugEnabled())
			logger.info("encodeMin: Mobile Identification Number:" + min);
		
		if(min.length() != 10){
			logger.error("encodeMin: InvalidInputException(MIN should be on 10 digits)");
			throw new InvalidInputException("MIN length validation failed, Should be of 10 digits");
		}
		
		byte[] myParams = NonASNAddressSignal.encodeAdrsSignal(min);
		
		if(logger.isDebugEnabled())
			logger.debug("encodeMin: Output--> data:" + Util.formatBytes(myParams));
		
		return myParams;
	}

	/**
	 * This function will encode NonASN MobileIdNum to ASN MobileIdNum object
	 * @param nonASNMobileIdNum
	 * @return MobileIdNum
	 * @throws InvalidInputException
	 */
	public static MobileIdentificationNumber encodeMin(NonAsnMobileIdNum nonASNMobileIdNum)
			throws InvalidInputException {
		
		logger.info("Before encodeMobileIdNum : nonASN to ASN");
		MobileIdentificationNumber mobileIdentificationNumber = new MobileIdentificationNumber();
		MINType mintyp = new MINType();
		mintyp.setValue(encodeMin(nonASNMobileIdNum.getAddrSignal()));
		mobileIdentificationNumber.setValue(mintyp);
		logger.info("After encodeMobileIdNum : nonASN to ASN");
		return mobileIdentificationNumber;
	}
	
	public String getMobileIdNum() {
		return mobileIdNum;
	}

	public void setMobileIdNum(String mobileIdNum) {
		this.mobileIdNum = mobileIdNum;
	}
	
	public String toString() {
		String obj = "MobileIdentificationNum: " + mobileIdNum;	
		return obj;	
	}
}
