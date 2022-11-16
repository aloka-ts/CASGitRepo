package com.agnity.win.datatypes;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.LocationAreaID ;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for LocationAreaID 
 * as per definition given in TIA-EIA-41-D, section 6.5.2.77.
 *  @author Supriya Jain
 */
public class NonASNLocationAreaID {  

	private static Logger logger = Logger.getLogger(NonASNLocationAreaID.class);
	int locationAreaID;

	/**
	 * This function will encode LocationAreaID  as per specification TIA-EIA-41-D section
	 * 6.5.2.77
	 * @param LocationAreaID
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeLocationAreaID( int locationAreaID)
			throws InvalidInputException {
		logger.info("encodeLocationAreaID");
		byte[] param = new byte[2];
		// locationAreaID represents first 2 octets received
		param[0] = (byte) (locationAreaID >> 8 & 0x00ff);
		param[1] = (byte) (locationAreaID & 0x00ff);
		if (logger.isDebugEnabled())
			logger.debug("encodeLocationAreaID: Encoded : " + Util.formatBytes(param));
		logger.info("encodeLocationAreaID");
		return param;
	}

	/**
	 * This function will encode NonASN LocationAreaID to ASN LocationAreaID  object
	 * @param NonASNLocationAreaID
	 * @return LocationAreaID 
	 * @throws InvalidInputException
	 */
	public static LocationAreaID  encodeLocationAreaID(NonASNLocationAreaID NonASNLocationAreaID)
			throws InvalidInputException {
		
		logger.info("Before encodeLocationAreaID : nonASN to ASN");
		LocationAreaID  LocationAreaID  = new LocationAreaID ();
		LocationAreaID .setValue(encodeLocationAreaID(NonASNLocationAreaID.getLocationAreaID()));
		logger.info("After encodeLocationAreaID : nonASN to ASN");
		return LocationAreaID ;
	}
	
	/**
	 * This function will decode LocationAreaID parameter. as per definition given in
	 * TIA-EIA-41-D, section 6.5.2.77.
	 * @param data
	 * @return object of LocationAreaID  DataType
	 * @throws InvalidInputException
	 */
	public static NonASNLocationAreaID decodeLocationAreaID (byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeLocationAreaID : Input--> data:" + Util.formatBytes(data));
		
		if (data == null || data.length == 0) {
			logger.error("decodeLocationAreaID : InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNLocationAreaID NonASNLocationAreaID = new NonASNLocationAreaID();
		// cocatenating 2 octets to represent locationAreaID
		NonASNLocationAreaID.locationAreaID = (short) (((data[0] & 0xff)) << 8 | (data[1] & 0xff));
		if (logger.isDebugEnabled())
			logger.debug("decodeLocationAreaID : Output<--" + NonASNLocationAreaID.toString());
		logger.info("decodeLocationAreaID ");
		return NonASNLocationAreaID;
	}

	public int getLocationAreaID() {
		return locationAreaID;
	}

	public void setLocationAreaID(int locationAreaID) {
		this.locationAreaID = locationAreaID;
	}


	public String toString() {
		String obj = "locationAreaID :" + locationAreaID ;
		return obj;
	}

}
