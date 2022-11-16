package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.SMS_BillingID ;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNSMS_BillingID
 * as per definition given in TIA-826-A, section 6.5.2.ho.
 *  @author Supriya Jain
 */
public class NonASNSMS_BillingID  {
	private static Logger logger = Logger.getLogger(NonASNSMS_BillingID .class);

	int marketID;
	short mcNumber;
	int idNo;

	/**
	 * This function will encode SMS_BillingID  as per specification TIA-826-A
	 * section 6.5.2.ho
	 * @param MarketID ,MCNumber,idNo
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeSMS_BillingID (int marketID,
			short mcNumber, int idNo)
			throws InvalidInputException {
		logger.info("encodeSMS_BillingID Enters");
		byte[] param = new byte[6];

		// MarketID represents first 2 octets received
		param[0] = (byte) (marketID >> 8 & 0x00ff);
		param[1] = (byte) (marketID & 0x00ff);
		// MCNumber represents 3rd octet received
		param[2] = (byte) (mcNumber & 0x00ff);
		// idNo represents next 3 octets received
		param[3] = (byte) (idNo >> 16 & 0x00ff);
		param[4] = (byte) (idNo >> 8 & 0x00ff);
		param[5] = (byte) (idNo & 0x00ff);
		if (logger.isDebugEnabled())
			logger.debug("encodeSMS_BillingID : Encoded : "
					+ Util.formatBytes(param));
		logger.info("encodeSMS_BillingID Exits ");
		return param;
	}
	
	/**
	 * This function will encode Non ASN SMS_BillingID  to ASN SMS_BillingID  object
	 * @param NonASNSMS_BillingID 
	 * @return SMS_BillingID 
	 * @throws InvalidInputException
	 */
	public static SMS_BillingID  encodeSMS_BillingID (NonASNSMS_BillingID  nonASNSMS_BillingID )
			throws InvalidInputException {
		
		logger.info("Before encodeSMS_BillingID  : nonASN to ASN");
		SMS_BillingID  billingID = new SMS_BillingID ();
		billingID.setValue(encodeSMS_BillingID (nonASNSMS_BillingID .marketID,nonASNSMS_BillingID .mcNumber,
				nonASNSMS_BillingID .idNo));
		logger.info("After encodeSMS_BillingID  : nonASN to ASN");
		return billingID;
	}
	
	
	/**
	 * This function will decode SMS BillingID as per specification TIA-826-A
	 * section 6.5.2.ho
	 * @param data
	 * @return object of SMS_BillingID DataType
	 * @throws InvalidInputException
	 */
	public static NonASNSMS_BillingID  decodeSMS_BillingID (byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeSMS_BillingID : Input--> data:"
					+ Util.formatBytes(data));
		if (data == null || data.length == 0 || data.length <6) {
			logger.error("decodeSMS_BillingID : InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNSMS_BillingID  nonASNSMS_BillingID  = new NonASNSMS_BillingID ();
		// cocatenating 2 octets to represent MarketID
		nonASNSMS_BillingID .marketID = ((data[0] & 0xff) << 8) | (data[1] & 0xff);
		nonASNSMS_BillingID .mcNumber = data[2];
		// cocatenating 3 octets to represent id number
		nonASNSMS_BillingID .idNo = ((data[3] & 0xff) << 16)
				| ((data[4] & 0xff) << 8) | (data[5] & 0xff);
		if (logger.isDebugEnabled())
			logger.debug("decodeSMS_BillingID : Output<--"
					+ nonASNSMS_BillingID .toString());
		logger.info("decodeSMS_BillingID ");
		return nonASNSMS_BillingID ;
	}

	public int getMarketID() {
		return marketID;
	}

	public void setMarketID(int marketID) {
		this.marketID = marketID;
	}

	public short getMcNo() {
		return mcNumber;
	}

	public void setMcNo(short mcNumber) {
		this.mcNumber = mcNumber;
	}

	public int getIdNo() {
		return idNo;
	}

	public void setIdNo(int idNo) {
		this.idNo = idNo;
	}


	public String toString() {
		String obj = "MarketID:" + marketID
				+ " ,MCNumber:" +mcNumber + ", idNo:"
				+ idNo ;
		return obj;
	}

}
