package com.agnity.map.datatypes;

import java.util.Arrays;
import java.util.BitSet;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.AllowedServicesMapEnum;
import com.agnity.map.enumdata.OfferedCamel4CsiMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

public class AllowedServicesMap {
	public static final int MAX_SIZE = 8;

    private static Logger logger = Logger.getLogger(AllowedServicesMap.class);

    /**
     * BitSet to hold allowed services 
     */
    private BitSet srvsBitSet = new BitSet(MAX_SIZE);
    
    public void setAllowedService(AllowedServicesMapEnum srv, boolean value) {
    	if(logger.isDebugEnabled()) {
    		logger.debug("Setting "+srv+" to "+value);
    	}
    	
    	srvsBitSet.set(srv.getCode(), value);
    }
    
    public boolean getAllowedService(AllowedServicesMapEnum srv) {
    	boolean srvsValue = srvsBitSet.get(srv.getCode());
    	if(logger.isDebugEnabled()){
    		logger.debug("Value for service "+srv+" is "+srvsValue);
    	}
    	return srvsValue;
    }
    
    /**
     * Method to encode AllowedServicesMap object to byte array
     * @param csiObj
     * @return
     */
    
    public static byte[] encode(AllowedServicesMap srvsObj) throws InvalidInputException {
      	if(srvsObj==null) {
    		logger.error("Object to encode is null");
    		throw new InvalidInputException("Object to encode is null");
    	}

    	byte[] data = toByteArr(srvsObj.srvsBitSet);
    	
    	if(logger.isDebugEnabled()){
    		logger.debug("data encoded = "+Util.formatBytes(data));
    	}
    	
    	return data;
    	
    }
    
    /**
     * Method to decode a byte array into AllowedServicesMap object
     * @param data
     * @return
     */
    
    public static AllowedServicesMap decode(byte[] data) throws InvalidInputException {
     	if(data == null) {
			logger.error("data to decode is null");
			throw new InvalidInputException("data to decode is null");
		}
    	int max_size = MAX_SIZE/8;
    	
		if(data.length > max_size) {
			logger.error("Invalid data length "+data.length+" expected max length is "+max_size +" bytes");
			throw new InvalidInputException("Invalid data length "+data.length+" expected max length is"+ max_size +" bytes");
		}
		
        System.out.println("AllowedServicesMap array length "+ data.length);

		if(logger.isDebugEnabled()){
            logger.debug("AllowedServicesMap array length "+ data.length);
		}

		AllowedServicesMap srvsObj = new AllowedServicesMap();
		srvsObj.srvsBitSet = fromByteArray(data);
		
		return srvsObj;
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
    


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AllowedServicesMap [srvsBitSet=" + srvsBitSet + "]";
	}
    
    
}
