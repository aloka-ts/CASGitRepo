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

import com.agnity.map.enumdata.RequestedServingNodeMapEnum;
import com.agnity.map.enumdata.SpecificCSIWithdrawMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

/**
 * 
 * @author sanjay
 *
 */
public class SpecificCSIWithdrawMap {
	public static final int MAX_SIZE = 32;

    private static Logger logger = Logger.getLogger(SpecificCSIWithdrawMap.class);

    /**
     * BitSet to hold CAMEL Subscription Information (CSI) 
     */
    private BitSet csiBitSet = new BitSet(MAX_SIZE);
    
    public void setSpecificCSI(SpecificCSIWithdrawMapEnum csi, boolean value) {
    	if(logger.isDebugEnabled()) {
    		logger.debug("Setting "+csi+" to "+value);
    	}
    	
    	csiBitSet.set(csi.getCode(), value);
    }
    
    public boolean getSpecificCSI(SpecificCSIWithdrawMapEnum csi) {
    	boolean csiValue = csiBitSet.get(csi.getCode());
    	if(logger.isDebugEnabled()){
    		logger.debug("CSI Value for "+csi+" is "+csiValue);
    	}
    	return csiValue;
    }
    
    /**
     * Method to encode SpecificCSIWithdrawMap object to byte array
     * @param csiObj
     * @return
     * @throws InvalidInputException 
     */
    
    public static byte[] encode(SpecificCSIWithdrawMap csiObj) throws InvalidInputException {
     	if(csiObj == null){
    		logger.error("object to encode is null");
    		throw new InvalidInputException("Objet to encode is null");
    	}
    	
	byte[] data = toByteArr(csiObj.csiBitSet);
    
	if(logger.isDebugEnabled()){
		logger.debug("encode byte array is "+Util.formatBytes(data));
	}

		
    	return data;
    	
    }
    
    /**
     * Method to encode a byte array into SpecificCSIWithdrawMap object
     * @param data
     * @return
     */
    
    public static SpecificCSIWithdrawMap decode(byte[] data) throws InvalidInputException {
       	if(data == null){
    		logger.error("data to decode is null");
    		throw new InvalidInputException("data to decode is null");
    	}
    	
    	int max_size = MAX_SIZE/8;
    	if(data.length > max_size){
    		String msg = "Invalid data size "+data.length+", expected max size is "+max_size;
    		logger.error(msg);
    		throw new InvalidInputException(msg);
    	}
    	
        if(logger.isDebugEnabled()){
            logger.debug("data to decode "+Util.formatBytes(data));
        }
        
    	SpecificCSIWithdrawMap csiObj = new SpecificCSIWithdrawMap();
    	csiObj.csiBitSet = fromByteArray(data);
    	
    	return csiObj;
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


}
