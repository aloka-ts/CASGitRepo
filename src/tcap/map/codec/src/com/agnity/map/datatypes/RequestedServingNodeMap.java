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

import com.agnity.map.enumdata.RequestedNodesMapEnum;
import com.agnity.map.enumdata.RequestedServingNodeMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

/**
 * 
 * @author sanjay
 *
 */
public class RequestedServingNodeMap {
	public static final int MAX_REQUESTED_NODES_LENGTH = 8;

	private static Logger logger = Logger.getLogger(RequestedNodesMap.class);

	/**
	 * BitSet to hold requested node information at specific bit index as
	 * specified by RequestedNodesMapEnum
	 */
	private BitSet servingNode = new BitSet(MAX_REQUESTED_NODES_LENGTH);
	
    /**
     * Method to encode RequestedNodes object to byte array
     * 
     * @return a byte array
     * @throws InvalidInputException 
     */
    public static byte[] encode(RequestedServingNodeMap reqobj) throws InvalidInputException{
     	if(reqobj == null){
    		logger.error("object to encode is null");
    		throw new InvalidInputException("Objet to encode is null");
    	}
    	
	byte[] data = toByteArr(reqobj.servingNode);
    
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
    public void enableRequestedServingNodeAtIndex(RequestedServingNodeMapEnum index){
		if(logger.isDebugEnabled()){
			logger.debug("Setting Requested Serving Node At Index = "+index);
		}
        servingNode.set(index.getCode());
    }

    /**
     *  This Method is for disabling the bit on specified index
     *  
     * @param index
     */
    public void disableRequestedServingNodeAtIndex(RequestedServingNodeMapEnum index){
		if(logger.isDebugEnabled()){
			logger.debug("Disabling Requested Serving Node At Index = "+index);
		}
		servingNode.clear(index.getCode());
    }

    /**
     *  This Method is for checking the bit is set or not 
     *  on specified index
     *  
     * @param index
     */
    public boolean getRequestedServingNodeAtIndex(RequestedServingNodeMapEnum index){
        return servingNode.get(index.getCode());
    }
    
    /**
     * Method to decode requested serving node byte array into 
     * RequestedServingNodeMap object  
     * 
     * @return a RequestedServingNodeMap decoded object
     * 
     * @param a byte array as requested Serving Node byte array
     */
    public static RequestedServingNodeMap decode(byte[] data)
    throws InvalidInputException{
      	
    	if(data == null){
    		logger.error("data to decode is null");
    		throw new InvalidInputException("data to decode is null");
    	}
    	
    	int max_size = MAX_REQUESTED_NODES_LENGTH/8;
    	if(data.length > max_size){
    		String msg = "Invalid data size "+data.length+", expected max size is "+max_size;
    		logger.error(msg);
    		throw new InvalidInputException(msg);
    	}
    	
        if(logger.isDebugEnabled()){
            logger.debug("data to decode "+Util.formatBytes(data));
        }
        
        RequestedServingNodeMap retObj = new RequestedServingNodeMap();
        retObj.servingNode = fromByteArray(data);
        
        if(logger.isDebugEnabled()){
        	logger.debug("Following serving nodes are enabled");
        	for(int i=0; i<retObj.servingNode.size(); ++i) {
        		if(retObj.servingNode.get(i)){
        			logger.debug("Node set = "+RequestedServingNodeMapEnum.getValue(i));
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
