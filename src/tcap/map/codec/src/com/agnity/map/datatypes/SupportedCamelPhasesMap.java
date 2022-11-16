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

import com.agnity.map.enumdata.SupportedCamelPhasesMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

public class SupportedCamelPhasesMap {
	public static final int MAX_SIZE = 16;

    private static Logger logger = Logger.getLogger(SupportedCamelPhasesMap.class);
    
    /**
     * BitSet to hold Supported Camel Phases 
     */
    private BitSet phasesBitSet;// = new BitSet(MAX_SIZE);
    
    public SupportedCamelPhasesMap() {
    	phasesBitSet = new BitSet(16);
	}
    
    public void setCamelPhase(SupportedCamelPhasesMapEnum phase, boolean value) {
    	if(logger.isDebugEnabled()) {
    		logger.debug("Setting "+phase+" to "+value);
    	}
    	
    	phasesBitSet.set(phase.getCode(), value);
    }
    
    public boolean getCamelPhase(SupportedCamelPhasesMapEnum phase) {
    	boolean phaseValue = phasesBitSet.get(phase.getCode());
    	if(logger.isDebugEnabled()){
    		logger.debug("ODB Value for "+phase+" is "+phaseValue);
    	}
    	return phaseValue;
    }
    
    /**
     * Method to encode SupportedCamelPhasesMap object to byte array
     * @param odbObj
     * @return
     */
    
    public static byte[] encode(SupportedCamelPhasesMap phaseObj) 
    		throws InvalidInputException{
    	if(phaseObj==null) {
    		logger.error("Object to encode is null");
    		throw new InvalidInputException("Object to encode is null");
    	}
    	
    	if(logger.isDebugEnabled()){
    		logger.debug("Encoding the object = "+phaseObj);
    	}

    	byte[] encdata = toByteArr(phaseObj.phasesBitSet); 

    	if (logger.isDebugEnabled()){
    		logger.debug("Encoded data = "+encdata);
    	}
    	return encdata;
    }
    
    /**
     * Method to encode a byte array into SupportedCamelPhasesMap object
     * @param data
     * @return
     */
    
    public static SupportedCamelPhasesMap decode(byte[] data) throws InvalidInputException{
    	if(data == null){
    		logger.equals("data to encode is null");
    		throw new InvalidInputException("data to encode is null");
    	}
    	
    	if(logger.isDebugEnabled()){ 
    		logger.debug("Decoding the data = "+Util.formatBytes(data));
    	}

    	byte[] bindata = new byte[]{data[0]};
    	
    	SupportedCamelPhasesMap phaseObj = new SupportedCamelPhasesMap();
    	phaseObj.phasesBitSet = fromByteArray(bindata);
    	
    	if(logger.isDebugEnabled()){ 
    		logger.debug("Object is = "+ phaseObj);
    	}
    	
    	return phaseObj;
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

    		int numBytes = (int) Math.ceil((float)bits.length()/8);
    		if(numBytes == 0) numBytes++;
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
    	sb.append("Phase 1 = ").append(getCamelPhase(SupportedCamelPhasesMapEnum.PHASE_1)).append("\n");
    	sb.append("Phase 2 = ").append(getCamelPhase(SupportedCamelPhasesMapEnum.PHASE_2)).append("\n");
    	sb.append("Phase 3 = ").append(getCamelPhase(SupportedCamelPhasesMapEnum.PHASE_3)).append("\n");
    	sb.append("Phase 4 = ").append(getCamelPhase(SupportedCamelPhasesMapEnum.PHASE_4)).append("\n");
    	
    	return sb.toString();
    }

}
