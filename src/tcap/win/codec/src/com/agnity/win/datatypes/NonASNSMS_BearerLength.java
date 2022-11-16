package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.SMS_BearerLength;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for SMS_BearerLength
 * as per definition given in TIA-826-A, section 6.5.2.hn.
 *  @author Supriya Jain
 */
public class NonASNSMS_BearerLength {  

	private static Logger logger = Logger.getLogger(NonASNSMS_BearerLength.class);
	int sms_bearer_length;

	/**
	 * This function will encode SMS_BearerLength as per specification TIA-826-A section
	 * 6.5.2.hn
	 * @param sms_bearer_length
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeSMS_BearerLength(int sms_bearer_length)
			throws InvalidInputException {
		logger.info("encodeSMS_BearerLength Enter");
		byte[] param = new byte[3];
		// sms_bearer_length represents first 2 octets received
		param[0] = (byte) (sms_bearer_length >> 8 & 0x00ff);
		param[1] = (byte) (sms_bearer_length & 0x00ff);
		if (logger.isDebugEnabled())
			logger.debug("encodeSMS_BearerLength: Encoded : " + Util.formatBytes(param));
		logger.info("Exit encodeSMS_BearerLength");
		return param;
	}

	/**
	 * This function will encode NonASN SMS_BearerLength to ASN SMS_BearerLength object
	 * @param nonASNSMS_BearerLength
	 * @return SMS_BearerLength
	 * @throws InvalidInputException
	 */
	public static SMS_BearerLength encodeSMS_BearerLength(NonASNSMS_BearerLength nonASNSMS_BearerLength)
			throws InvalidInputException {
		
		logger.info("Before encodeSMS_BearerLength : nonASN to ASN");
		SMS_BearerLength SMS_BearerLength = new SMS_BearerLength();
		SMS_BearerLength.setValue(encodeSMS_BearerLength(nonASNSMS_BearerLength.getsms_bearer_length()));
		logger.info("After encodeSMS_BearerLength : nonASN to ASN");
		return SMS_BearerLength;
	}
	
	/**
	 * This function will decode SMS_BearerLengthID. as per definition given in
	 * TIA-826-A, section 6.5.2.hn.
	 * @param data
	 * @return object of SMS_BearerLength DataType
	 * @throws InvalidInputException
	 */
	public static NonASNSMS_BearerLength decodeSMS_BearerLength(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeSMS_BearerLength Enter: Input--> data:" + Util.formatBytes(data));
		
		if (data == null || data.length == 0) {
			logger.error("decodeSMS_BearerLength: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNSMS_BearerLength nonASNSMS_BearerLength = new NonASNSMS_BearerLength();
		// cocatenating 2 octets to represent sms_bearer_length
		nonASNSMS_BearerLength.sms_bearer_length = ((data[0] & 0xff)) << 8 | (data[1] & 0xff);

		if (logger.isDebugEnabled())
			logger.debug("decodeSMS_BearerLength: Output<--" + nonASNSMS_BearerLength.toString());
		logger.info(" Exit decodeSMS_BearerLength");
		return nonASNSMS_BearerLength;
	}


	public int getsms_bearer_length() {
		return sms_bearer_length;
	}

	public void setsms_bearer_length(int sms_bearer_length) {
		this.sms_bearer_length = sms_bearer_length;
	}

	public String toString() {
		String obj = "sms_bearer_length:" + sms_bearer_length;
		return obj;
	}

}
