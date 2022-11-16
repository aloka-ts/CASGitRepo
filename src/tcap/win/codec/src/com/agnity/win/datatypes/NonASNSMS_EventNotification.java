package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.SMS_EventNotification;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for SMS_EventNotification
 *  @author Supriya Jain
 */
public class NonASNSMS_EventNotification {
	private static Logger logger = Logger
			.getLogger(NonASNSMS_EventNotification.class);

	// Notification Request Status for MS Originated Short Msg Accepted By Network:: represents 1st bit of 1st octet.
	byte mo_n_notification;
	// Notification Request Status for MS Originated Short Msg Delivery Attempt Towards Destination:: represents 2nd bit of 1st octet.
	byte mo_DA_notification;
	// Notification Request Status for Successful MS Originated Short Msg Delivery Towards Destination::represents 3rd bit of 1st octet.
	byte mo_SD_notification;
	//  Notification Request Status for UnSuccessful MS Originated Short Msg Delivery Towards Destination:: represents 4th bit of 1st octet.
	byte mo_UD_notification;
	//  Notification Request Status for MS Originated Short Msg purged by MC Event::represents 5th bit of 1st octet.
	byte mo_P_notification;
	
	// Notification Request Status for MS Terminated Short Msg Accepted By Network:: represents 1st bit of 2nd octet.
	byte mt_N_notification;
	// Notification Request Status for MS Terminated Short Msg Delivery Attempt Towards Destination:: represents 2nd bit of 2nd octet.
	byte mt_DA_notification;
	// Notification Request Status for Successful MS Terminated Short Msg Delivery Towards Destination::represents 3rd bit of 2nd octet.
	byte mt_SD_notification;
	//  Notification Request Status for UnSuccessful MS Terminated Short Msg Delivery Towards Destination:: represents 4th bit of 2nd octet.
	byte mt_UD_notification;
	//  Notification Request Status for MS Terminated Short Msg purged by MC Event::represents 5th bit of 2nd octet.
	byte mt_P_notification;
	

	/**
	 * This function will encode NonASNSMS_EventNotification
	 * @param bit values
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeSMS_EventNotification(byte mo_n_notification, byte mo_DA_notification,
			byte mo_SD_notification, byte mo_UD_notification, byte mo_P_notification, byte mt_N_notification, byte mt_DA_notification,
			byte mt_SD_notification, byte mt_UD_notification, byte mt_P_notification) throws InvalidInputException {
		if(logger.isInfoEnabled())
	      logger.info("encodeSMS_EventNotification Enters");
		byte[] param = new byte[2];
		
		// octet 1
		param[0] = (byte) ((mo_n_notification & 0x01) | ((mo_DA_notification & 0x01) << 1)
				| ((mo_SD_notification & 0x01) << 2) | ((mo_UD_notification & 0x01) << 3)| ((mo_P_notification & 0x01) << 4));
		// octet 2
		param[1] = (byte) ((mt_N_notification & 0x01) | ((mt_DA_notification & 0x01) << 1)
				| ((mt_SD_notification & 0x01) << 2) | ((mt_UD_notification & 0x01) << 3)| ((mt_P_notification & 0x01) << 4));

		if (logger.isDebugEnabled())
			logger.debug("encodeSMS_EventNotification: Encoded : "
					+ Util.formatBytes(param));
		if(logger.isInfoEnabled())
		logger.info("encodeSMS_EventNotification Exits");
		return param;
	}
	
	/**
	 * This function will encode NonASN SMS_EventNotification to ASN SMS_EventNotification object
	 * @param nonASNSMS_EventNotification 
	 * @return SMS_EventNotification
	 * @throws InvalidInputException
	 */
	public static SMS_EventNotification encodeSMS_EventNotification(NonASNSMS_EventNotification nonASNSMSEventNotification )
			throws InvalidInputException {
		if(logger.isInfoEnabled())
		logger.info("Before encodeSMS_EventNotification : nonASN to ASN");
		SMS_EventNotification sms_EventNotification = new SMS_EventNotification();
		sms_EventNotification.setValue(encodeSMS_EventNotification(nonASNSMSEventNotification.getMo_n_notification(),
				nonASNSMSEventNotification.getMo_DA_notification(),nonASNSMSEventNotification.getMo_SD_notification(),nonASNSMSEventNotification.getMo_UD_notification(),	
				nonASNSMSEventNotification.getMo_P_notification(),nonASNSMSEventNotification.getMt_N_notification(),
				nonASNSMSEventNotification.getMt_DA_notification(),nonASNSMSEventNotification.getMt_SD_notification(),	
				nonASNSMSEventNotification.getMt_UD_notification(),nonASNSMSEventNotification.getMt_P_notification()));
		if(logger.isInfoEnabled())
		logger.info("After encodeSMS_EventNotification : nonASN to ASN");
		return sms_EventNotification;
	}
	
	
	
	/**
	 * This function will decode SMS_EventNotification
	 * @param data
	 * @return object of NonASNSMS_EventNotification
	 * @throws InvalidInputException
	 */
	public static NonASNSMS_EventNotification decodeSMS_EventNotification(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeSMS_EventNotification: Input--> data:"
					+ Util.formatBytes(data));
		if (data == null || data.length==0) {
			logger.error("decodeSMS_EventNotification: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNSMS_EventNotification smsEventNotification = new NonASNSMS_EventNotification();
		smsEventNotification.mo_n_notification = (byte) (data[0] & 0x1);
		smsEventNotification.mo_DA_notification = (byte) (data[0] >> 1 & 0x1);
		smsEventNotification.mo_SD_notification = (byte) (data[0] >> 2 & 0x1);
		smsEventNotification.mo_UD_notification = (byte) (data[0] >> 3 & 0x1);
		smsEventNotification.mo_P_notification = (byte) (data[0] >> 4 & 0x1);
		smsEventNotification.mt_N_notification = (byte) (data[1] & 0x1);
		smsEventNotification.mt_DA_notification = (byte) (data[1] >> 1 & 0x1);
		smsEventNotification.mt_SD_notification = (byte) (data[1] >> 2 & 0x1);
		smsEventNotification.mt_UD_notification = (byte) (data[1] >> 3 & 0x1);
		smsEventNotification.mt_P_notification = (byte) (data[1] >> 4 & 0x1);

		if (logger.isDebugEnabled())
			logger.debug("decodeSMS_EventNotification: Output<--"
					+ smsEventNotification.toString());
		if(logger.isInfoEnabled())
		logger.info("decodeSMS_EventNotification exits");
		return smsEventNotification;
	}

	public byte getMo_n_notification() {
		return mo_n_notification;
	}

	public void setMo_n_notification(byte mo_n_notification) {
		this.mo_n_notification = mo_n_notification;
	}

	public byte getMo_DA_notification() {
		return mo_DA_notification;
	}

	public void setMo_DA_notification(byte mo_DA_notification) {
		this.mo_DA_notification = mo_DA_notification;
	}

	public byte getMo_SD_notification() {
		return mo_SD_notification;
	}

	public void setMo_SD_notification(byte mo_SD_notification) {
		this.mo_SD_notification = mo_SD_notification;
	}

	public byte getMo_UD_notification() {
		return mo_UD_notification;
	}

	public void setMo_UD_notification(byte mo_UD_notification) {
		this.mo_UD_notification = mo_UD_notification;
	}

	public byte getMo_P_notification() {
		return mo_P_notification;
	}

	public void setMo_P_notification(byte mo_P_notification) {
		this.mo_P_notification = mo_P_notification;
	}

	public byte getMt_N_notification() {
		return mt_N_notification;
	}

	public void setMt_N_notification(byte mt_N_notification) {
		this.mt_N_notification = mt_N_notification;
	}

	public byte getMt_DA_notification() {
		return mt_DA_notification;
	}

	public void setMt_DA_notification(byte mt_DA_notification) {
		this.mt_DA_notification = mt_DA_notification;
	}

	public byte getMt_SD_notification() {
		return mt_SD_notification;
	}

	public void setMt_SD_notification(byte mt_SD_notification) {
		this.mt_SD_notification = mt_SD_notification;
	}

	public byte getMt_UD_notification() {
		return mt_UD_notification;
	}

	public void setMt_UD_notification(byte mt_UD_notification) {
		this.mt_UD_notification = mt_UD_notification;
	}

	public byte getMt_P_notification() {
		return mt_P_notification;
	}

	public void setMt_P_notification(byte mt_P_notification) {
		this.mt_P_notification = mt_P_notification;
	}

	@Override
	public String toString() {
		return "NonASNSMS_EventNotification [mo_n_notification="
				+ mo_n_notification + ", mo_DA_notification="
				+ mo_DA_notification + ", mo_SD_notification="
				+ mo_SD_notification + ", mo_UD_notification="
				+ mo_UD_notification + ", mo_P_notification="
				+ mo_P_notification + ", mt_N_notification="
				+ mt_N_notification + ", mt_DA_notification="
				+ mt_DA_notification + ", mt_SD_notification="
				+ mt_SD_notification + ", mt_UD_notification="
				+ mt_UD_notification + ", mt_P_notification="
				+ mt_P_notification + "]";
	}


}
