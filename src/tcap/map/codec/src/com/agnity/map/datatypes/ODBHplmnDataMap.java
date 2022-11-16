/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/

package com.agnity.map.datatypes;

import java.util.Arrays;
import java.util.BitSet;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.ODBHplmnDataMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

/**
 * Class to hold Operator Determined Barring (ODB) HPLMN Data
 * 
 * @author sanjay
 *
 */

public class ODBHplmnDataMap {
	public static final int MAX_SIZE = 32;

    private static Logger logger = Logger.getLogger(SpecificCSIWithdrawMap.class);
    
    /**
     * BitSet to hold CAMEL Subscription Information (CSI) 
     */
    private BitSet odbBitSet = new BitSet(MAX_SIZE);
    
    public void setODBHplmData(ODBHplmnDataMapEnum odb, boolean value) {
    	if(logger.isDebugEnabled()) {
    		logger.debug("Setting "+odb+" to "+value);
    	}
    	
    	odbBitSet.set(odb.getCode(), value);
    }
    
    public boolean getODBHplmData(ODBHplmnDataMapEnum odb) {
    	boolean odbValue = odbBitSet.get(odb.getCode());
    	if(logger.isDebugEnabled()){
    		logger.debug("ODB Value for "+odb+" is "+odbValue);
    	}
    	return odbValue;
    }
    
    /**
     * Method to encode getODBHplmData object to byte array
     * @param odbObj
     * @return
     */
    
    public static byte[] encode(ODBHplmnDataMap odbobj) throws InvalidInputException{
    	if(odbobj == null){
			logger.error("data to encode is null");
			throw new InvalidInputException("data to decode is null");
		}

    	byte[] data = toByteArr(odbobj.odbBitSet);
		
		if(logger.isDebugEnabled()){
			logger.debug("encoded byte array is "+Util.formatBytes(data));
		}
		    	
		return data;
    }
    
    /**
     * Method to encode a byte array into ODBHplmnDataMap object
     * @param data
     * @return
     */
    
    public static ODBHplmnDataMap decode(byte[] odbData) throws InvalidInputException{
    	
    	if(odbData == null) {
			logger.error("data to decode is null");
			throw new InvalidInputException("data to decode is null");
		}
    	int max_size = MAX_SIZE/8;
    	
		if(odbData.length > max_size) {
			logger.error("Invalid data length "+odbData.length+" expected max length is "+max_size +" bytes");
			throw new InvalidInputException("Invalid data length "+odbData.length+" expected max length is"+ max_size +" bytes");
		}
		
		if(logger.isDebugEnabled()){
            logger.debug("odbData array length "+ odbData.length);
		}

    	ODBHplmnDataMap odbObj = new ODBHplmnDataMap();
    	odbObj.odbBitSet = fromByteArray(odbData);
    	
    	return odbObj;
    }

    /**
     * This Method is used for converting BitSet to byte array
     * 
     * @return bytes as a byte array
     * 
     * @param BitSet object
     */
    public static byte[] toByteArr(BitSet bits) {
    		int numBytes = (int) Math.ceil((float)(bits.length()+7)/8);
    		if(numBytes == 0) numBytes++;
            byte[] bytes = new byte[numBytes];
            for (int i=0; i<bits.length(); i++) {
                    if (bits.get(i)) {
                            bytes[bytes.length-i/8-1] |= 0x80>>(i%8);
                    }
            }
            return bytes;
    }

        
    /**
     * This Method is used for converting byte array to BitSet
     * 
     * @param bytes as a byte array
     * 
     * @return BitSet object
     */
    public static BitSet fromByteArray(byte[] bytes) {
        BitSet bits = new BitSet();
        for (int i = 0; i < bytes.length * 8; i++) {
            if ((bytes[bytes.length - i / 8 - 1] & (0x80 >> (i % 8))) > 0) {
                bits.set(i);
            }
        }
        return bits;
    }
    
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("Hplmn specific barring type 1 = ").append(getODBHplmData(ODBHplmnDataMapEnum.PLMN_SPECIFIC_BARRING_TYPE_1)).append("\n");
    	sb.append("hplmn specific barring type 2 = ").append(getODBHplmData(ODBHplmnDataMapEnum.PLMN_SPECIFIC_BARRING_TYPE_2)).append("\n");
    	sb.append("hplmn specific barring type 3 = ").append(getODBHplmData(ODBHplmnDataMapEnum.PLMN_SPECIFIC_BARRING_TYPE_3)).append("\n");
    	sb.append("hplmn specific barring type 4 = ").append(getODBHplmData(ODBHplmnDataMapEnum.PLMN_SPECIFIC_BARRING_TYPE_4)).append("\n");
    	
    	return sb.toString();
    	
    }
}
