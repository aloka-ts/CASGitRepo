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

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.TypeOfShapeMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.enumdata.LSASignificantIdMapEnum;

/**
 * 
 * @author sanjay
 *
 */

public class LSAIdentityMap {
	/*
	 * Octets are coded according to TS 3GPP TS 23.003 [17]

	 * The LSA ID consists of 24 bits, numbered from 0 to 23, with bit 0 being the LSB. 
	 * Bit 0 indicates whether the LSA is a PLMN significant number or a universal LSA. 
	 * If the bit is set to 0 the LSA is a PLMN significant number; 
	 * if it is set to 1 it is a universal LSA
	 * 
	 * MSB                                            LSB
     *  ________________________________________________
     * |    23 Bits                            | 1 bit  |
     * |_______________________________________|________|
     * <----------------- LSA ID ----------------------->
	 *  
	 */
    private LSASignificantIdMapEnum significantNumber;
	private byte[] lsaId;
	private static Logger logger = Logger.getLogger(LSAIdentityMap.class);
	
	/**
	 * 
	 * @return Significant Number, LSB of the 24 bit long LSA ID
	 */
	public LSASignificantIdMapEnum getSignificantNumber() {
        return this.significantNumber;
    }
	
	/**
	 * 
	 * @return Localized Service Area Id
	 */
	public byte[] getLSAId() {
		return this.lsaId;
	}
	
	public static LSAIdentityMap decode(byte[] bytes) throws InvalidInputException{
		
		if(logger.isDebugEnabled()){
			logger.debug("In decode, LSAIdentityMap bytes array length::"+bytes.length);
		}
		
		LSAIdentityMap lsaId = new LSAIdentityMap();
		lsaId.significantNumber = LSASignificantIdMapEnum.getValue(bytes[0] & 0x01);

		lsaId.lsaId = bytes.clone();
		
		if(logger.isDebugEnabled()){
			logger.debug("LSAIdentityMap non asn succesfully decoded.");
		}
		return lsaId;
	}
	
	public static byte[] encode(LSAIdentityMap lsaId) throws InvalidInputException{        
        byte[] bytes = lsaId.getLSAId().clone();
        if(logger.isDebugEnabled()){
            logger.debug("LSAIdentityMap non asn encoded successfully. byte array length:"+bytes.length);
        }
        return bytes;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LSAIdentityMap [significantNumber=" + significantNumber
				+ ", lsaId=" + Arrays.toString(lsaId) + "]";
	}

}
