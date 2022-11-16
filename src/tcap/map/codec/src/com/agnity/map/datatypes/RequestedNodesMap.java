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

import com.agnity.map.enumdata.RequestedNodesMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

import org.apache.log4j.Logger;


/**
 * @author Sanjay
 *
 */

public class RequestedNodesMap {

    public static final int MAX_REQUESTED_NODES_LENGTH = 8;

    private static Logger logger = Logger.getLogger(RequestedNodesMap.class);

    /**
     * BitSet to hold requested node information at specific bit index as
     * specified by RequestedNodesMapEnum
     */
    private BitSet requestedNodeBitSet = new BitSet(MAX_REQUESTED_NODES_LENGTH);

    /**
     * Method to encode RequestedNodes object to byte array
     * 
     * @return a byte array
     */
    public  static byte[] encode(RequestedNodesMap reqobj) throws InvalidInputException {
    	if(reqobj == null){
    		logger.error("object to encode is null");
    		throw new InvalidInputException("Objet to encode is null");
    	}
    	
	byte[] data = toByteArr(reqobj.requestedNodeBitSet);
    
	if(logger.isDebugEnabled()){
		logger.debug("encode byte array is "+Util.formatBytes(data));
	}
        return data;
    }

    /**
     *  This Method is for enabling the bit on specified index
     *  
     * @param index
     */
    public void enableRequestedNodeAtIndex(RequestedNodesMapEnum index){
		if(logger.isDebugEnabled()){
			logger.debug("Setting Requested Node At Index = "+index);
		}
        requestedNodeBitSet.set(index.getCode());
    }

    /**
     *  This Method is for disabling the bit on specified index
     *  
     * @param index
     */
    public void disableRequestedNodeAtIndex(RequestedNodesMapEnum index){
		if(logger.isDebugEnabled()){
			logger.debug("Disabling Requested Node At Index = "+index);
		}
		requestedNodeBitSet.clear(index.getCode());
    }

    /**
     *  This Method is for checking the bit is set or not 
     *  on specified index
     *  
     * @param index
     */
    public boolean getRequestedNodeAtIndex(RequestedNodesMapEnum index){
        return requestedNodeBitSet.get(index.getCode());
    }

    /**
     * Method to decode requested node byte array into 
     * RequestedNodesMap object  
     * 
     * @return a RequestedNodesMap decoded object
     * 
     * @param a byte array as requestedNodeByte
     * @throws InvalidInputException 
     */
    public static RequestedNodesMap decode(byte[] requestedNodeByte) 
    		throws InvalidInputException{
    	
    	if(requestedNodeByte == null){
    		logger.error("data to decode is null");
    		throw new InvalidInputException("data to decode is null");
    	}
    	
    	int max_size = MAX_REQUESTED_NODES_LENGTH/8;
    	if(requestedNodeByte.length > max_size){
    		String msg = "Invalid data size "+requestedNodeByte.length+", expected max size is "+max_size;
    		logger.error(msg);
    		throw new InvalidInputException(msg);
    	}
    	
        if(logger.isDebugEnabled()){
            logger.debug("data to decode "+Util.formatBytes(requestedNodeByte));
        }
        
        RequestedNodesMap retObj = new RequestedNodesMap();
        retObj.requestedNodeBitSet = fromByteArray(requestedNodeByte);
        
        if(logger.isDebugEnabled()){
        	logger.debug("Following nodes are enabled");
        	for(int i=0; i<retObj.requestedNodeBitSet.size(); ++i) {
        		if(retObj.requestedNodeBitSet.get(i)){
        			logger.debug("Node set = "+RequestedNodesMapEnum.getValue(i));
        		}
        	}
        }

        return retObj;
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
            if ((bytes[i / 8 ] & (1 << (i % 8))) > 0) {
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
        int arrayLength = (int)Math.ceil(bits.length()/8);
        byte[] bytes = new byte[arrayLength];
        for (int i=0; i<bits.length(); i++) {
            if (bits.get(i)) {
                bytes[i] |= 1<<(i%8);
            }
        }
        return bytes;
    }

}

