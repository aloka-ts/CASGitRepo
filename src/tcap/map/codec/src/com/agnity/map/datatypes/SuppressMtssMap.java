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
import java.util.BitSet;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.SuppressMtssMapEnum;
import com.agnity.map.enumdata.UnusedBitsMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

/**
 * 
 * @author sanjay
 *
 */
public class SuppressMtssMap {
	private UnusedBitsMapEnum unusedBits = UnusedBitsMapEnum.NO_UNUSED_BIT;

	/**
	 * @return the unusedBits
	 */
	public UnusedBitsMapEnum getUnusedBits() {
		return unusedBits;
	}


	/**
	 * @param unusedBits the unusedBits to set
	 */
	public void setUnusedBits(UnusedBitsMapEnum unusedBits) {
		this.unusedBits = unusedBits;
	}


	private static int MAX_BIT_STRING_SIZE = 16;
		
	BitSet suppressBitSet = new BitSet(MAX_BIT_STRING_SIZE); 
		
	private static Logger logger = Logger.getLogger(SuppressMtssMap.class);

	/**
	 * Set a specific MTSS to the value specified
	 * @param value
	 */
	
	public void setSuppressMtss(SuppressMtssMapEnum mtss, boolean value) {
		if(logger.isDebugEnabled()){
			logger.debug("Setting "+mtss+" to "+value);
		}
		suppressBitSet.set(mtss.getCode(), value);
	}

	
	public boolean getSuppressMtss(SuppressMtssMapEnum mtss){
    	boolean mtssVal = suppressBitSet.get(mtss.getCode());
    	if(logger.isDebugEnabled()){
    		logger.debug("MTSS Value for "+mtss+" is "+mtssVal);
    	}
    	return mtssVal;
	}
	
	 /**
     * Method to encode SuppressMtssMap object to byte array
     * @param suppMtssObj
     * @return
     */
    
    public static byte[] encode(SuppressMtssMap suppMtssObj) throws InvalidInputException{
    	if(suppMtssObj==null) {
    		logger.error("Object to encode is null");
    		throw new InvalidInputException("Object to encode is null");
    	}
    	//byte b0 = (byte)suppMtssObj.unusedBits.getcode();
    	
    	byte[] data = toByteArr(suppMtssObj.suppressBitSet);
		
		if(logger.isDebugEnabled()){
			logger.debug("encoded byte array is "+Util.formatBytes(data));
		}
		
		//byte[] retData = new byte[data.length + 1];
		//retData[0] = b0;
		//for(int i=0; i<data.length;++i) {
		//	retData[i+1] = data[i];
		//}
		
		System.out.println("encoding suppress mtss = "+Util.formatBytes(data));

		
    	return data;
    	
    }
    
    /**
     * Method to encode a byte array into SuppressMtssMap object
     * @param data
     * @return
     */
    
    public static SuppressMtssMap decode(byte[] data) throws InvalidInputException{
    	if(data == null) {
			logger.error("data to decode is null");
			throw new InvalidInputException("data to decode is null");
		}
    	int max_size = MAX_BIT_STRING_SIZE/8;
    	
		if(data.length > max_size) {
			logger.error("Invalid data length "+data.length+" expected max length is "+max_size +" bytes");
			throw new InvalidInputException("Invalid data length "+data.length+" expected max length is"+ max_size +" bytes");
		}
		
		if(logger.isDebugEnabled()){
            logger.debug("data array length "+ data.length);
		}

		SuppressMtssMap suppMtssObj = new SuppressMtssMap();
		suppMtssObj.suppressBitSet = fromByteArray(data);
    	
    	return suppMtssObj;
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
                            bytes[i/8] |= 0x80>>(i%8);
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
          if ((bytes[i / 8 ] & (0x80 >> (i % 8))) > 0) {
              bits.set(i);
          }
      }
      return bits;
  }
  
  

}
