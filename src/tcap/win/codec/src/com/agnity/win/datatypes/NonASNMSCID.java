package com.agnity.win.datatypes;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.MSCID;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for MSCID
 * as per definition given in TIA-EIA-41-D, section 6.5.2.82.
 *  @author Supriya Jain
 */
public class NonASNMSCID implements Serializable{  

	private static Logger logger = Logger.getLogger(NonASNMSCID.class);
	short marketID;
	int switchNo;

	/**
	 * This function will encode MSCID as per specification TIA-EIA-41-D section
	 * 6.5.2.82
	 * 
	 * @param MarketID
	 *            ,SwitchNo
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeMSCID(short marketID, int switchNo)
			throws InvalidInputException {
		logger.info("encodeMSCID");
		byte[] param = new byte[3];
		// MarketID represents first 2 octets received
		param[0] = (byte) (marketID >> 8 & 0x00ff);
		param[1] = (byte) (marketID & 0x00ff);
		// SwitchNo represents 3rd octet received
		param[2] = (byte) (switchNo & 0x00ff);
		if (logger.isDebugEnabled())
			logger.debug("encodeMSCID: Encoded : " + Util.formatBytes(param));
		logger.info("encodeMSCID");
		return param;
	}

	/**
	 * This function will encode NonASN MSCID to ASN MSCID object
	 * @param nonASNMSCID
	 * @return MSCID
	 * @throws InvalidInputException
	 */
	public static MSCID encodeMSCID(NonASNMSCID nonASNMSCID)
			throws InvalidInputException {
		
		logger.info("Before encodeMSCID : nonASN to ASN");
		MSCID mscID = new MSCID();
		mscID.setValue(encodeMSCID(nonASNMSCID.getMarketID(),nonASNMSCID.getSwitchNo()));
		logger.info("After encodeMSCID : nonASN to ASN");
		return mscID;
	}
	
	/**
	 * This function will decode MSCID ID. as per definition given in
	 * TIA-EIA-41-D, section 6.5.2.82.
	 * 
	 * @param data
	 * @return object of MSCID DataType
	 * @throws InvalidInputException
	 */
	public static NonASNMSCID decodeMSCID(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeMSCID: Input--> data:" + Util.formatBytes(data));
		
		if (data == null || data.length == 0) {
			logger.error("decodeMSCID: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNMSCID nonASNMSCID = new NonASNMSCID();
		// cocatenating 2 octets to represent MarketID
		nonASNMSCID.marketID = (short) (((data[0] & 0xff)) << 8 | (data[1] & 0xff));
		nonASNMSCID.switchNo = data[2];
		if (logger.isDebugEnabled())
			logger.debug("decodeMSCID: Output<--" + nonASNMSCID.toString());
		logger.info("decodeMSCID");
		return nonASNMSCID;
	}

	public short getMarketID() {
		return marketID;
	}

	public void setMarketID(short marketID) {
		this.marketID = marketID;
	}

	public int getSwitchNo() {
		return switchNo;
	}

	public void setSwitchNo(int switchNo) {
		this.switchNo = switchNo;
	}

	public String toString() {
		String obj = "marketID:" + marketID + " ,switchNo:" + switchNo;
		return obj;
	}

}
