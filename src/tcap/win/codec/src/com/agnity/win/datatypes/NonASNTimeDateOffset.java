package com.agnity.win.datatypes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.apache.log4j.Logger;

import com.agnity.win.asngenerated.TimeDateOffset;
import com.agnity.win.exceptions.InvalidInputException;
import com.agnity.win.util.Util;

/*
 * This class provides encode and decode methods for NonASNTimeDateOffset
 *  @author Supriya Jain
 */
public class NonASNTimeDateOffset {
	private static Logger logger = Logger.getLogger(NonASNTimeDateOffset.class);

	int timeDateOffsetVal;

	/**
	 * This function will encode TimeDateOffset 
	 * @param timeDateOffsetVal
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeTimeDateOffset(int timeDateOffsetVal)
			throws InvalidInputException {
		logger.info("encodeTimeDateOffset");
		if (logger.isDebugEnabled())
			logger.debug("Input timeDateOffsetVal: "+timeDateOffsetVal);
		byte[] param = new byte[2];

		// timeDateOffsetVal represents first 2 octets received
		param[0] = (byte) (timeDateOffsetVal >> 8 & 0x00ff);
		param[1] = (byte) (timeDateOffsetVal & 0x00ff);

		if (logger.isDebugEnabled())
			logger.debug("encodeTimeDateOffset: Encoded : "
					+ Util.formatBytes(param));
		logger.info("encodeTimeDateOffset");
		return param;
	}
	
	/**
	 * This function will encode Non ASN TimeDateOffset to ASN TimeDateOffset object
	 * @param NonASNTimeDateOffset
	 * @return TimeDateOffset
	 * @throws InvalidInputException
	 */
	public static TimeDateOffset encodeTimeDateOffset(NonASNTimeDateOffset nonASNTimeDateOffset)
			throws InvalidInputException {
		
		logger.info("Before encodeTimeDateOffset : nonASN to ASN");
		TimeDateOffset TimeDateOffset = new TimeDateOffset();
		TimeDateOffset.setValue(encodeTimeDateOffset(nonASNTimeDateOffset.gettimeDateOffsetVal()));
		logger.info("After encodeTimeDateOffset : nonASN to ASN");
		return TimeDateOffset;
	}
	
	
	/**
	 * This function will decode TimeDateOffset
	 * @param data
	 * @return object of TimeDateOffsetsDataType
	 * @throws InvalidInputException
	 */
	public static NonASNTimeDateOffset decodeTimeDateOffset(byte[] data)
			throws InvalidInputException {
		if (logger.isDebugEnabled())
			logger.debug("decodeTimeDateOffset: Input--> data:"
					+ Util.formatBytes(data));
		if (data == null || data.length == 0) {
			logger.error("decodeTimeDateOffset: InvalidInputException(Input data(bytes) not present or null)");
			throw new InvalidInputException("Input data(bytes) not present or null");
		}
		NonASNTimeDateOffset timeDateOffset = new NonASNTimeDateOffset();
		
	/*	if(((data[0]>>7)& 0x01) ==1)
		{
			timeDateOffset.timeDateOffsetVal = - (((byte) ~data[0])  << 8) | ((byte) ((~data[1])+1)) ;
		}
		else
		{*/
			/*timeDateOffset.timeDateOffsetVal = ( (data[0]& 0x00ff ) << 8) | data[1] ;
			System.out.println("( (data[0] : "+data[0]);
			System.out.println("(data[0]& 0x00ff ) : "+(data[0]& 0x00ff ));
			System.out.println("( (data[0]& 0x00ff ) << 8) | data[1]  : "+(( (data[0]& 0x00ff ) << 8) | data[1]));
			System.out.println("( (data[0]& 0x00ff ) << 8) : "+ ( (data[0]& 0x00ff ) << 8));*/
		//}
		
		// cocatenating 2 octets to represent timeDateOffset
		
		
		timeDateOffset.timeDateOffsetVal =(int)( ((data[0])<<8) | (data[1]) );

		if (logger.isDebugEnabled())
			logger.debug("decodeTimeDateOffset: Output<--"
					+ timeDateOffset.toString());
		logger.info("decodeTimeDateOffset");
		return timeDateOffset;
	}

	public int gettimeDateOffsetVal() {
		return timeDateOffsetVal;
	}

	public void settimeDateOffsetVal(int timeDateOffsetVal) {
		this.timeDateOffsetVal = timeDateOffsetVal;

	}

	public String toString() {
		String obj =  " timeDateOffsetVal: "+ timeDateOffsetVal ;
		return obj;
	}

}
