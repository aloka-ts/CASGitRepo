package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.EventIndEnum;
import com.genband.isup.enumdata.EventPrsntRestIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * This class have parameters for Event Information. 
 * @author vgoel
 *
 */
public class EventInfo {

	/**
	 * @see EventIndEnum
	 */
	EventIndEnum eventIndEnum;
	
	/**
	 * @see EventPrsntRestIndEnum
	 */
	EventPrsntRestIndEnum eventPrsntRestIndEnum;

	private static Logger logger = Logger.getLogger(EventInfo.class);
	
	public EventIndEnum getEventIndEnum() {
		return eventIndEnum;
	}

	public void setEventIndEnum(EventIndEnum eventIndEnum) {
		this.eventIndEnum = eventIndEnum;
	}

	public EventPrsntRestIndEnum getEventPrsntRestIndEnum() {
		return eventPrsntRestIndEnum;
	}

	public void setEventPrsntRestIndEnum(EventPrsntRestIndEnum eventPrsntRestIndEnum) {
		this.eventPrsntRestIndEnum = eventPrsntRestIndEnum;
	}
	
	
	/**
	 * This function will encode event info
	 * @param eventIndEnum
	 * @param eventPrsntRestIndEnum
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeEventInfo(EventIndEnum eventIndEnum, EventPrsntRestIndEnum eventPrsntRestIndEnum) throws InvalidInputException
	{	
		logger.info("encodeEventInfo:Enter");

		if(eventPrsntRestIndEnum == null){
			logger.error("encodeEventInfo:Enter: InvalidInputException(eventPrsntRestIndEnum is null )");
			throw new InvalidInputException("eventPrsntRestIndEnum is null");
		}
		
		byte[] data = new byte[1];		
		int eventPrsntRestInd = eventPrsntRestIndEnum.getCode();
		int eventInd;
		if(eventIndEnum == null)
			eventInd = 3; //spare	
		else
			eventInd = eventIndEnum.getCode();		
				
		data[0] = (byte) (eventPrsntRestInd << 7 | eventInd);
		
		if(logger.isDebugEnabled())
			logger.debug("encodeEventInfo:Encoded Event Info: " + Util.formatBytes(data));
		logger.info("encodeEventInfod:Exit");
		
		return data;
	}
	
	
	/**
	 * This function will decode event info
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static EventInfo decodeEventInfo(byte[] data) throws InvalidInputException
	{
		logger.info("decodeEventInfo:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeEventInfo: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeEventInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		EventInfo eventInfo = new EventInfo();
		
		eventInfo.eventIndEnum = EventIndEnum.fromInt(data[0] & 0x7F);
		eventInfo.eventPrsntRestIndEnum = EventPrsntRestIndEnum.fromInt((data[0] >> 7 ) & 0x1);
		
		if(logger.isDebugEnabled())
			logger.debug("decodeEventInfo: Output<--" + eventInfo.toString());
		logger.info("decodeEventInfo:Exit");
		
		return eventInfo ;
	}
	
	public String toString(){
		
		String obj = "eventIndEnum:"+ eventIndEnum + " ,eventPrsntRestIndEnum:" + eventPrsntRestIndEnum;
		return obj ;
	}	
}
