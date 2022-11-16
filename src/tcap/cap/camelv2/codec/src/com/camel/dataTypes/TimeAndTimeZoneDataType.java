package com.camel.dataTypes;

import org.apache.log4j.Logger;

import com.camel.exceptions.InvalidInputException;
import com.camel.util.NonAsnArg;

/**
 * This class have methods for decoding of 
 * TimeAndTimeZone parameter.
 * @author nkumar
 *
 */
public class TimeAndTimeZoneDataType {
	
	
	/** This parameter contains the time that the gsmSSF was triggered,
	 * It will be in format of yyyymmddhhmmss.
	 */
	String time ;
	/**
	 * This parameter contains the the time zone that the invoking gsmSSF
	 *	resides in.
	 */
	String timeZone ;
	
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	private static Logger logger = Logger.getLogger(TimeAndTimeZoneDataType.class);
	
	public static TimeAndTimeZoneDataType deocdeTimeAndTimeZone(byte[] val) throws InvalidInputException{
		logger.info("deocdeTimeAndTimeZone:Enter");
		if(val == null){
			logger.error("decodeAdrsSignal: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		if(val.length != 8){
			logger.error("decodeAdrsSignal: InvalidInputException(val length must be 8)");
			throw new InvalidInputException("val length must be 8");
		}
		TimeAndTimeZoneDataType timeAndTimeZoneDataType = new TimeAndTimeZoneDataType();
		byte[] timeInfoOnly = new byte[7];
		//only copying the info of time
		for(int i =0 ; i< val.length -1 ; i++)
			timeInfoOnly[i] = val[i] ;
		
		timeAndTimeZoneDataType.time = NonAsnArg.TbcdStringDecoder(timeInfoOnly, 0);
		logger.debug("deocdeTimeAndTimeZone: time value:" + timeAndTimeZoneDataType.time);
		
		
		int firstQuarter = (int)(val[7] & 0x7) ;
		System.out.println("first:" + firstQuarter);
		int secondQuarter = (int)((val[7] >> 4) & 0xF) ;
		System.out.println("second:" + secondQuarter);
		int sign = (int)((val[7] >> 3) & 0x1) ;
		System.out.println("sign:" + sign);
		int sum = firstQuarter*10 + secondQuarter ;
		System.out.println("sum:" + sum);
		int hrs = sum/4 ;
		System.out.println("hrs:" + hrs);
		int mins = (sum%4) * 15 ;
		System.out.println("min:" + mins);
		timeAndTimeZoneDataType.timeZone = "GMT" + ((sign == 0) ? "+" : "-" ) + (hrs == 0 ? "00" : hrs) + ":" + ((mins == 0)? "00" : mins ) ;
		logger.info("deocdeTimeAndTimeZone:Exit");
		return timeAndTimeZoneDataType ;
	}
	
	
}
