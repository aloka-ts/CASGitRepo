package com.agnity.win.datatypes;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.BillingID;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNActionCode
 * as per definition given in TIA-EIA-41-D, section 6.5.2.16.
 *  @author Supriya Jain
 */
public class NonASNBillingID implements Serializable{
	private static Logger logger = Logger.getLogger(NonASNBillingID.class);

	short originatingMarketID;
	short originatingSwitchNo;
	int idNo;
	short segmentCounter;

	/**
	 * This function will encode BillingID as per specification TIA-EIA-41-D
	 * section 6.5.2.16
	 * 
	 * @param originatingMarketID
	 *            ,originatingSwitchNo,idNo,segmentCounter
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeBillingID(short originatingMarketID,
			short originatingSwitchNo, int idNo, short segmentCounter)
			throws InvalidInputException {
		logger.info("encodeBillingID");
		byte[] param = new byte[7];

		// originatingMarketID represents first 2 octets received
		param[0] = (byte) (originatingMarketID >> 8 & 0x00ff);
		param[1] = (byte) (originatingMarketID & 0x00ff);
		// originatingSwitchNo represents 3rd octet received
		param[2] = (byte) (originatingSwitchNo & 0x00ff);
		// idNo represents next 3 octets received
		param[3] = (byte) (idNo >> 16 & 0x00ff);
		param[4] = (byte) (idNo >> 8 & 0x00ff);
		param[5] = (byte) (idNo & 0x00ff);
		// segmentCounter represents last i.e. 7th octet received
		param[6] = (byte) (segmentCounter & 0x00ff);
		if (logger.isDebugEnabled())
			logger.debug("encodeBillingID: Encoded : "
					+ Util.formatBytes(param));
		logger.info("encodeBillingID");
		return param;
	}
	
	/**
	 * This function will encode Non ASN BillingID to ASN BillingID object
	 * @param nonASNBillingID
	 * @return BillingID
	 * @throws InvalidInputException
	 */
	public static BillingID encodeBillingID(NonASNBillingID nonASNBillingID)
			throws InvalidInputException {
		
		logger.info("Before encodeBillingID : nonASN to ASN");
		BillingID billingID = new BillingID();
		billingID.setValue(encodeBillingID(nonASNBillingID.originatingMarketID,nonASNBillingID.originatingSwitchNo,
				nonASNBillingID.idNo,nonASNBillingID.segmentCounter));
		logger.info("After encodeBillingID : nonASN to ASN");
		return billingID;
	}
	
	
	/**
	 * This function will decode Billing ID.as per specification TIA-EIA-41-D
	 * section 6.5.2.16
	 * 
	 * @param data
	 * @return object of BillingIDsDataType
	 * @throws InvalidInputException
	 */
	public static NonASNBillingID decodeBillingID(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeBillingID: Input--> data:"
					+ Util.formatBytes(data));
		if (data == null || data.length == 0) {
			logger.error("decodeBillingID: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNBillingID nonASNBillingID = new NonASNBillingID();
		// cocatenating 2 octets to represent originatingMarketID
		System.out.println("data[0] & 0xff00  " + (data[0] & 0xff00));
		nonASNBillingID.originatingMarketID = (short) (((data[0] & 0xff) << 8) | (data[1] & 0xff));
		nonASNBillingID.originatingSwitchNo = data[2];
		// cocatenating 3 octets to represent id number
		nonASNBillingID.idNo = ((data[3] & 0xff) << 16)
				| ((data[4] & 0xff) << 8) | (data[5] & 0xff);
		nonASNBillingID.segmentCounter = data[6];
		if (logger.isDebugEnabled())
			logger.debug("decodeBillingID: Output<--"
					+ nonASNBillingID.toString());
		logger.info("decodeBillingID");
		return nonASNBillingID;
	}

	public short getOriginatingMarketID() {
		return originatingMarketID;
	}

	public void setOriginatingMarketID(short originatingMarketID) {
		this.originatingMarketID = originatingMarketID;
	}

	public short getOriginatingSwitchNo() {
		return originatingSwitchNo;
	}

	public void setOriginatingSwitchNo(short originatingSwitchNo) {
		this.originatingSwitchNo = originatingSwitchNo;
	}

	public int getIdNo() {
		return idNo;
	}

	public void setIdNo(int idNo) {
		this.idNo = idNo;
	}

	public short getSegmentCounter() {
		return segmentCounter;
	}

	public void setSegmentCounter(short segmentCounter) {
		this.segmentCounter = segmentCounter;
	}

	public String toString() {
		String obj = "originatingMarketID:" + originatingMarketID
				+ " ,originatingSwitchNo:" + originatingSwitchNo + ", idNo:"
				+ idNo + ", segmentCounter:" + segmentCounter;
		return obj;
	}

}
