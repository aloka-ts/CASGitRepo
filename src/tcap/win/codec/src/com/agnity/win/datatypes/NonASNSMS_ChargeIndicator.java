package com.agnity.win.datatypes;

import org.apache.log4j.Logger;
import com.agnity.win.asngenerated.SMS_ChargeIndicator;
import com.agnity.win.enumdata.SMSChargeIndicatorEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNSMS_ChargeIndicator
 * as per definition given in TIA-826-A, section 6.5.2.126.
 * @author Supriya Jain
 */

public class NonASNSMS_ChargeIndicator {
	private static Logger logger = Logger
			.getLogger(NonASNSMS_ChargeIndicator.class);

	/**
	 * @see SMSChargeIndicatorEnum
	 */
	SMSChargeIndicatorEnum smsChargeIndicator;

	public SMSChargeIndicatorEnum getSMSChargeIndicator() {
		return smsChargeIndicator;
	}

	public void setSMSChargeIndicator(SMSChargeIndicatorEnum smsChargeIndicator) {
		this.smsChargeIndicator = smsChargeIndicator;
	}

	/**
	 * This function will encode NonASNSMS_ChargeIndicator as per specification
	 * TIA-826-A section 6.5.2.126
	 * @param SMSChargeIndicatorEnum : SMSChargeIndicator value
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeSMSChargeIndicator(
			SMSChargeIndicatorEnum smsChargeIndicatorEnum)
			throws InvalidInputException {
		logger.info("encodeSMSChargeIndicator");
		byte[] myParms = new byte[1];

		if (smsChargeIndicatorEnum == null) {
			logger.info("encodeSMSChargeIndicator Enters: Invalid Value of Type Of NonASNSMS_ChargeIndicator");
			throw new InvalidInputException("Type Of NonASNSMS_ChargeIndicator is null");
		}
		int smsChargeIndicatorVal = smsChargeIndicatorEnum.getCode();

		// encoding
		myParms[0] = (byte) (smsChargeIndicatorVal & 0xFF);
		if (logger.isDebugEnabled())
			logger.debug("encodeSMSChargeIndicator: Encoded : "
					+ Util.formatBytes(myParms));
		logger.info("encodeSMSChargeIndicator Exits");
		return myParms;
	}
	
	
	/**
	 * This function will encode NonASN SMSChargeIndicator to ASN SMSChargeIndicator object
	 * @param nonASNSMSChargeIndicator
	 * @return SMS_CauseCode
	 * @throws InvalidInputException
	 */
	public static SMS_ChargeIndicator encodeSMSChargeIndicator(NonASNSMS_ChargeIndicator nonASNSMSChargeIndicator)
			throws InvalidInputException {
		
		logger.info("Before encodeSMSChargeIndicator : nonASN to ASN");
		SMS_ChargeIndicator sms_ChargeIndicator = new SMS_ChargeIndicator();
		sms_ChargeIndicator.setValue(encodeSMSChargeIndicator(nonASNSMSChargeIndicator.getSMSChargeIndicator()));
		logger.info("After encodeSMSChargeIndicator : nonASN to ASN");
		return sms_ChargeIndicator;
	}

	/**
	 * This function will decode NonASNSMS_ChargeIndicator as per specification
	 * TIA-826-A section 6.5.2.126
	 * @param data  bytes to be decoded
	 * @return object of NonASNSMS_ChargeIndicator
	 * @throws InvalidInputException
	 */
	public static NonASNSMS_ChargeIndicator decodeSMSChargeIndicator(byte[] data)
			throws InvalidInputException {

		if (logger.isDebugEnabled())
			logger.debug("decodeSMSChargeIndicator: Input--> data:"
					+ Util.formatBytes(data));
		// check to see if there is no byte of data
		if (data == null || data.length == 0) {
			logger.error("decodeSMSChargeIndicator: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNSMS_ChargeIndicator smsChargeIndicatorData = new NonASNSMS_ChargeIndicator();
		smsChargeIndicatorData.setSMSChargeIndicator(SMSChargeIndicatorEnum
				.fromInt(data[0] & 0xff));

		if (logger.isDebugEnabled())
			logger.debug("decodeSMSChargeIndicator: Output<--"
					+ smsChargeIndicatorData.toString());
		logger.info("decodeSMSChargeIndicator Exits");
		return smsChargeIndicatorData;
	}

	public String toString() {

		String obj = "NonASNSMS_ChargeIndicator  :" + smsChargeIndicator;
		return obj;
	}

}
