/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/

package com.agnity.map.datatypes;

import org.apache.log4j.Logger;

import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.NonAsnArg;

/**
 *  
 * @author sanjay
 * 
 * IMEI ::= TBCD-STRING (SIZE (8))
 * Refers to International Mobile Station Equipment Identity
 * and Software Version Number (SVN) defined in TS 3GPP TS 23.003 [17].
 * If the SVN is not present the last octet shall contain the
 * digit 0 and a filler.
 * If present the SVN shall be included in the last octet.
 * 
 *
 */
public class ImeiMap {

	

	private String imei;
	private static Logger logger = Logger.getLogger(ImeiMap.class);

	/**
	 * 
	 * @return International Mobile Station Equipment Identify String
	 */
	public String getImei() {
		return imei;
	}
	
	/**
	 * Set IMEI 
	 * @param id
	 */
	public void setImei(String id) {
		this.imei = id;
	}
	
	/**
	 * decode ImeiMap bytes array in non-asn object of
	 *  ImeiMap
	 * @param bytes
	 * @return ImeiMap object
	 */
	public static ImeiMap decode(byte[] bytes) throws InvalidInputException{
		if(logger.isDebugEnabled()){
			logger.debug("In decode, ImeiMap bytes array length::"+bytes.length);
		}
		
		if( bytes.length != 8) {
			throw new InvalidInputException(
					"Invalid input length, expected length is 8 octets, recieved "+bytes.length+" octets");
		}
		
		ImeiMap imeiObj = new ImeiMap();
		imeiObj.imei = NonAsnArg.tbcdStringDecoder(bytes, 0);
		
		return imeiObj;
	}
	
	/**
	 * 
	 * @param imeiObj
	 * @return
	 * @throws InvalidInputException
	 */
	public static byte[] encode(ImeiMap imeiObj) throws InvalidInputException {
		if(imeiObj == null) {
			throw new InvalidInputException("Error encoding IMEI Object, null value received");
		}

		if (imeiObj.getImei().length() < 15 || imeiObj.getImei().length() > 16)
            throw new InvalidInputException("Error encoding IMEI object, imei be 15 or 16");
		
		byte[] bytes = null;
		bytes = NonAsnArg.tbcdStringEncoder(imeiObj.imei);
		
		return bytes;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ImeiMap [imei=" + imei + "]";
	}
}

