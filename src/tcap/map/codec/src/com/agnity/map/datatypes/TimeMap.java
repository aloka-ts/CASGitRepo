package com.agnity.map.datatypes;

import org.apache.log4j.Logger;

import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.MapFunctions;

/**
 * Time is a four octet fixed size value with
 * 
 * Refer ETSI TS 129 002 V9.4.0
 * 
 * Time ::= OCTET STRING (SIZE (4))
 * 
 * @author sanjay
 *
 */

public class TimeMap {
	private Integer time;
	
	private static Logger logger = Logger.getLogger(TimeMap.class);
	
	public TimeMap() {
		this.time = 0;
	}
	
	public TimeMap(Integer time){
		this.time = time;
	}
	
	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}


	public static TimeMap decode(byte[] data) throws InvalidInputException {
		if(data == null) {
			throw new InvalidInputException("data to encode is null");
		}
		if(data.length != 4) {
			throw new InvalidInputException("byte array is of invalid length, "
					+ "expected size is 4, provide is "+data.length);
		}
		
		Integer time = Integer.parseInt(MapFunctions.decodeNumber(data, 0));
				
		if(logger.isDebugEnabled()){
			logger.debug("decoded time = "+time);
		}
		return new TimeMap(time);
	}
	
	public static byte[] encode(TimeMap obj) throws InvalidInputException {
		if(logger.isDebugEnabled()){
			logger.debug("object to encode = "+obj);
		}
		Integer time = obj.getTime();  
		byte[] encdata = MapFunctions.hexStringToByteArray(MapFunctions.encodenNumber(time.toString()));
		
		return encdata;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TimeMap [time=" + time + "]";
	}
	
	

	
}
