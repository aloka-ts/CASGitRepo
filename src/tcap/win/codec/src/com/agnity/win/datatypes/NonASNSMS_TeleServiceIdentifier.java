package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.SMS_TeleserviceIdentifier;
import com.agnity.win.enumdata.SMSTeleServiceIdEnum;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for SMS_TeleServiceIdentifier
 * as per definition given in TIA-EIA-41-D, section 6.5.2.137.
 * @author Supriya Jain
 */

public class NonASNSMS_TeleServiceIdentifier {
	private static Logger logger = Logger
			.getLogger(NonASNSMS_TeleServiceIdentifier.class);

	/**
	 * @see SMSTeleServiceIdEnum
	 */
	SMSTeleServiceIdEnum smsTeleServiceId;

	public SMSTeleServiceIdEnum getSMSTeleServiceIdentifier() {
		return smsTeleServiceId;
	}

	public void setSMSTeleServiceIdentifier(SMSTeleServiceIdEnum smsTeleServiceId) {
		this.smsTeleServiceId = smsTeleServiceId;
	}

	/**
	 * This function will encode NonASNSMS_TeleServiceIdentifier as per specification
	 * TIA-EIA-41-D section 6.5.2.137
	 * @param SMSTeleServiceIdEnum: teleservice for which the SMS message applies.
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeSMSTeleServiceIdentifier(
			SMSTeleServiceIdEnum smsTeleService)
			throws InvalidInputException {
		logger.info("encodeSMSTeleServiceIdentifier Enters");
		byte[] myParms = new byte[2];

		int smsTeleServiceIdVal;
		if (smsTeleService == null) {
			logger.info("encodeSMSTeleServiceIdentifier: Invalid Value of Type Of NonASNSMS_TeleServiceIdentifier");
			throw new InvalidInputException("Type Of NonASNSMS_TeleServiceIdentifier is null");
		}
		smsTeleServiceIdVal = smsTeleService.getCode();

		// encoding
		
		myParms[0] = (byte) (smsTeleServiceIdVal >> 8 & 0x00ff);
		myParms[1] = (byte) (smsTeleServiceIdVal & 0x00ff);
		if (logger.isDebugEnabled())
			logger.debug("encodeSMSTeleServiceIdentifier: Encoded : "
					+ Util.formatBytes(myParms));
		logger.info("encodeSMSTeleServiceIdentifier Exits");
		return myParms;
	}

	/**
	 * This function will encode NonASNSMS_TeleServiceIdentifier to ASN SMS_TeleServiceIdentifier object
	 * @param nonASNSMS_TeleServiceIdentifier
	 * @return SMS_TeleserviceIdentifier
	 * @throws InvalidInputException
	 */
	public static SMS_TeleserviceIdentifier encodeSMSTeleServiceIdentifier(NonASNSMS_TeleServiceIdentifier nonASNSMS_TeleServiceIdentifier)
			throws InvalidInputException {
		
		logger.info("Before encodeSMSTeleServiceIdentifier : nonASN to ASN");
		SMS_TeleserviceIdentifier sms_TeleserviceIdentifier = new SMS_TeleserviceIdentifier();
		sms_TeleserviceIdentifier.setValue(encodeSMSTeleServiceIdentifier(nonASNSMS_TeleServiceIdentifier.getSMSTeleServiceIdentifier()));
		logger.info("After encodeSMSTeleServiceIdentifier : nonASN to ASN");
		return sms_TeleserviceIdentifier;
	}
	
	
	
	/**
	 * This function will decode NonASNSMS_TeleServiceIdentifier as per specification
	 * TIA-EIA-41-D section 6.5.2.137
	 * @param data  bytes to be decoded
	 * @return object of NonASNSMS_TeleServiceIdentifier
	 * @throws InvalidInputException
	 */
	public static NonASNSMS_TeleServiceIdentifier decodeSMSTeleServiceIdentifier(byte[] data)
			throws InvalidInputException {

		if (logger.isDebugEnabled())
			logger.debug("decodeSMSTeleServiceIdentifier: Input--> data:"
					+ Util.formatBytes(data));
		// check to see if there is no byte of data
		if (data == null || data.length == 0 ) {
			logger.error("decodeSMSTeleServiceIdentifier: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNSMS_TeleServiceIdentifier smsTeleServiceIdData = new NonASNSMS_TeleServiceIdentifier();
		int smsTeleServiceIdVal= ((data[0] & 0xff)) << 8 | (data[1] & 0xff);
		smsTeleServiceIdData.setSMSTeleServiceIdentifier(SMSTeleServiceIdEnum
				.fromInt(smsTeleServiceIdVal));

		if (logger.isDebugEnabled())
			logger.debug("decodeSMSTeleServiceIdentifier: Output<--"
					+ smsTeleServiceIdData.toString());
		logger.info("decodeSMSTeleServiceIdentifier");
		return smsTeleServiceIdData;
	}

	public String toString() {

		String obj = "SMS_TeleServiceIdentifier  :" + smsTeleServiceId;
		return obj;
	}

}
