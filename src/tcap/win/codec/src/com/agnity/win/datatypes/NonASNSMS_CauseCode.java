package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.SMS_CauseCode;
import com.agnity.win.enumdata.SMSCauseCodeEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNSMS_CauseCode
 * as per definition given in TIA-826-A, section 6.5.2.125.
 * @author Supriya Jain
 */

public class NonASNSMS_CauseCode {
	private static Logger logger = Logger
			.getLogger(NonASNSMS_CauseCode.class);

	/**
	 * @see SMSCauseCodeEnum
	 */
	SMSCauseCodeEnum smsCauseCode;

	public SMSCauseCodeEnum getSMSCauseCode() {
		return smsCauseCode;
	}

	public void setSMSCauseCode(SMSCauseCodeEnum smsCauseCode) {
		this.smsCauseCode = smsCauseCode;
	}

	/**
	 * This function will encode NonASNSMS_CauseCode as per specification
	 * TIA-826-A section 6.5.2.125
	 * @param SMSCauseCodeEnum : SMSCauseCode value
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeSMSCauseCode(
			SMSCauseCodeEnum smsCauseCodeEnum)
			throws InvalidInputException {
		logger.info("encodeSMSCauseCode");
		byte[] myParms = new byte[1];

		if (smsCauseCodeEnum == null) {
			logger.info("encodeSMSCauseCode Enters: Invalid Value of Type Of NonASNSMS_CauseCode");
			throw new InvalidInputException("Type Of NonASNSMS_CauseCode is null");
		}
		int smsCauseCodeVal = smsCauseCodeEnum.getCode();

		// encoding
		myParms[0] = (byte) (smsCauseCodeVal & 0xFF);
		if (logger.isDebugEnabled())
			logger.debug("encodeSMSCauseCode: Encoded : "
					+ Util.formatBytes(myParms));
		logger.info("encodeSMSCauseCode Exits");
		return myParms;
	}
	
	
	/**
	 * This function will encode NonASN SMSCauseCode to ASN SMSCauseCode object
	 * @param nonASNSMSCauseCode
	 * @return SMS_CauseCode
	 * @throws InvalidInputException
	 */
	public static SMS_CauseCode encodeSMSCauseCode(NonASNSMS_CauseCode nonASNSMSCauseCode)
			throws InvalidInputException {
		
		logger.info("Before encodeSMSCauseCode : nonASN to ASN");
		SMS_CauseCode sms_CauseCode = new SMS_CauseCode();
		sms_CauseCode.setValue(encodeSMSCauseCode(nonASNSMSCauseCode.getSMSCauseCode()));
		logger.info("After encodeSMSCauseCode : nonASN to ASN");
		return sms_CauseCode;
	}

	/**
	 * This function will decode NonASNSMS_CauseCode as per specification
	 * TIA-826-A section 6.5.2.125
	 * @param data  bytes to be decoded
	 * @return object of NonASNSMS_CauseCode
	 * @throws InvalidInputException
	 */
	public static NonASNSMS_CauseCode decodeSMSCauseCode(byte[] data)
			throws InvalidInputException {

		if (logger.isDebugEnabled())
			logger.debug("decodeSMSCauseCode: Input--> data:"
					+ Util.formatBytes(data));
		// check to see if there is no byte of data
		if (data == null || data.length == 0) {
			logger.error("decodeSMSCauseCode: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNSMS_CauseCode smsCauseCodeData = new NonASNSMS_CauseCode();
		smsCauseCodeData.setSMSCauseCode(SMSCauseCodeEnum
				.fromInt(data[0] & 0xff));

		if (logger.isDebugEnabled())
			logger.debug("decodeSMSCauseCode: Output<--"
					+ smsCauseCodeData.toString());
		logger.info("decodeSMSCauseCode Exits");
		return smsCauseCodeData;
	}

	public String toString() {

		String obj = "NonASNSMS_CauseCode  :" + smsCauseCode;
		return obj;
	}

}
