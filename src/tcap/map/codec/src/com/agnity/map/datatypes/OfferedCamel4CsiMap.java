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

import java.util.BitSet;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.OfferedCamel4CsiMapEnum;
import com.agnity.map.enumdata.SupportedCamelPhasesMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

/**
 * 
 * @author sanjay
 *
 */
public class OfferedCamel4CsiMap {
	public static final int MAX_SIZE = 16;

    private static Logger logger = Logger.getLogger(OfferedCamel4CsiMap.class);

    /**
     * BitSet to hold offered CAMEL Subscription Information (CSI) 
     */
    private BitSet offeredCsiBitSet = new BitSet(MAX_SIZE);
    
    public void setOfferedCSI(OfferedCamel4CsiMapEnum csi, boolean value) {
    	if(logger.isDebugEnabled()) {
    		logger.debug("Setting "+csi+" to "+value);
    	}
    	
    	offeredCsiBitSet.set(csi.getCode(), value);
    }
    
    public boolean getOfferedCSI(OfferedCamel4CsiMapEnum csi) {
    	boolean csiValue = offeredCsiBitSet.get(csi.getCode());
    	if(logger.isDebugEnabled()){
    		logger.debug("CSI Value for "+csi+" is "+csiValue);
    	}
    	return csiValue;
    }
    
    /**
     * Method to encode OfferedCamel4CsiMap object to byte array
     * @param csiObj
     * @return
     * @throws InvalidInputException 
     */
    
    public static byte[] encode(OfferedCamel4CsiMap csiObj) throws InvalidInputException {
      	if(csiObj==null) {
    		logger.error("Object to encode is null");
    		throw new InvalidInputException("Object to encode is null");
    	}
    	
    	if(logger.isDebugEnabled()){
    		logger.debug("Encoding the object = "+csiObj);
    	}

    	byte[] encdata = toByteArr(csiObj.offeredCsiBitSet); 

    	if (logger.isDebugEnabled()){
    		logger.debug("Encoded data = "+encdata);
    	}
    	return encdata;	
    }
    
    /**
     * Method to decode a byte array into OfferedCamel4CsiMap object
     * @param data
     * @return
     * @throws InvalidInputException 
     */
    
    public static OfferedCamel4CsiMap decode(byte[] data) throws InvalidInputException {
       	if(data == null){
    		logger.equals("data to encode is null");
    		throw new InvalidInputException("data to encode is null");
    	}
    	
    	if(logger.isDebugEnabled()){ 
    		logger.debug("Decoding the data = "+Util.formatBytes(data));
    	}

    	byte[] bindata = new byte[]{data[0]};
    	
    	OfferedCamel4CsiMap csiObj = new OfferedCamel4CsiMap();
    	csiObj.offeredCsiBitSet = fromByteArray(bindata);
    	
    	if(logger.isDebugEnabled()){ 
    		logger.debug("Object is = "+ csiObj);
    	}
    	
    	
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
			System.out.println("bit size " +bits.length());

    		int numBytes = (int) Math.ceil((float)bits.length()/8);
    		if(numBytes == 0) numBytes++;
    		System.out.println("num byte  "+numBytes);
            byte[] bytes = new byte[numBytes];
            for (int i=0; i<bits.length(); i++) {
                    if (bits.get(i)) {
                            bytes[bytes.length-i/8-1] |= 0x80>>(i%8);
                    }
            }
            return bytes;
    }

    
    
    public String toString(){
    	StringBuilder sb = new StringBuilder();
    	sb.append("\n");
    	sb.append("D_CSI = ").append(getOfferedCSI(OfferedCamel4CsiMapEnum.D_CSI)).append("\n");
    	sb.append("MG_CSI = ").append(getOfferedCSI(OfferedCamel4CsiMapEnum.MG_CSI)).append("\n");
    	sb.append("MT_SMS_CSI = ").append(getOfferedCSI(OfferedCamel4CsiMapEnum.MT_SMS_CSI)).append("\n");
    	sb.append("O_CSI = ").append(getOfferedCSI(OfferedCamel4CsiMapEnum.O_CSI)).append("\n");
    	sb.append("PSI_ENHANCEMENTS = ").append(getOfferedCSI(OfferedCamel4CsiMapEnum.PSI_ENHANCEMENTS)).append("\n");
    	sb.append("T_CSI = ").append(getOfferedCSI(OfferedCamel4CsiMapEnum.T_CSI)).append("\n");
    	sb.append("VT_CSI = ").append(getOfferedCSI(OfferedCamel4CsiMapEnum.VT_CSI)).append("\n");
    	
    	
    	return sb.toString();
    }
	
}
