package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.ServingCellID ;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for ServingCellID 
 * as per definition given in TIA-EIA-41-D, section 6.5.2.117.
 *  @author Supriya Jain
 */
public class NonASNServingCellID {  

	private static Logger logger = Logger.getLogger(NonASNServingCellID.class);
	int servingCellID;

	/**
	 * This function will encode ServingCellID  as per specification TIA-EIA-41-D section
	 * 6.5.2.117
	 * @param ServingCellID
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeServingCellID( int servingCellID)
			throws InvalidInputException {
		logger.info("encodeServingCellID");
		byte[] param = new byte[2];
		// servingCellID represents first 2 octets received
		param[0] = (byte) (servingCellID >> 8 & 0x00ff);
		param[1] = (byte) (servingCellID & 0x00ff);
		if (logger.isDebugEnabled())
			logger.debug("encodeServingCellID: Encoded : " + Util.formatBytes(param));
		logger.info("encodeServingCellID");
		return param;
	}

	/**
	 * This function will encode NonASN ServingCellID to ASN ServingCellID  object
	 * @param NonASNServingCellID
	 * @return ServingCellID 
	 * @throws InvalidInputException
	 */
	public static ServingCellID  encodeServingCellID(NonASNServingCellID nonASNServingCellID)
			throws InvalidInputException {
		
		logger.info("Before encodeServingCellID : nonASN to ASN");
		ServingCellID  servingCellID  = new ServingCellID ();
		servingCellID.setValue(encodeServingCellID(nonASNServingCellID.getServingCellID()));
		logger.info("After encodeServingCellID : nonASN to ASN");
		return servingCellID ;
	}
	
	/**
	 * This function will decode ServingCellID parameter. as per definition given in
	 * TIA-EIA-41-D, section 6.5.2.117.
	 * @param data
	 * @return object of ServingCellID  DataType
	 * @throws InvalidInputException
	 */
	public static NonASNServingCellID decodeServingCellID (byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeServingCellID : Input--> data:" + Util.formatBytes(data));
		
		if (data == null || data.length == 0) {
			logger.error("decodeServingCellID : InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNServingCellID nonASNServingCellID = new NonASNServingCellID();
		// cocatenating 2 octets to represent servingCellID
		nonASNServingCellID.servingCellID = (short) (((data[0] & 0xff)) << 8 | (data[1] & 0xff));
		if (logger.isDebugEnabled())
			logger.debug("decodeServingCellID : Output<--" + nonASNServingCellID.toString());
		logger.info("decodeServingCellID ");
		return nonASNServingCellID;
	}

	public int getServingCellID() {
		return servingCellID;
	}

	public void setServingCellID(int servingCellID) {
		this.servingCellID = servingCellID;
	}


	public String toString() {
		String obj = "servingCellID :" + servingCellID ;
		return obj;
	}

}
