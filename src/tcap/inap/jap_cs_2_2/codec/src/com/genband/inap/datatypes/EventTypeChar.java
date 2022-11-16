package com.genband.inap.datatypes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;

import com.genband.inap.asngenerated.TTCEventTypeCharging;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;

/**
 * Used for encoding and decoding of EventTypeCharging
 * @author vgoel
 *
 */
public class EventTypeChar
{
	/**
	 * @see TTCEventTypeCharging
	 */
	TTCEventTypeCharging ttcEventTypeCharging;

	private static Logger logger = Logger.getLogger(EventTypeChar.class);	
	
	
	public TTCEventTypeCharging getTtcEventTypeCharging() {
		return ttcEventTypeCharging;
	}

	public void setTtcEventTypeCharging(TTCEventTypeCharging ttcEventTypeCharging) {
		this.ttcEventTypeCharging = ttcEventTypeCharging;
	}
	
	/**
	 * This function will encode EventTypeCharging.
	 * @param ttcEventTypeCharging
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] encodeEventTypeCharging(TTCEventTypeCharging ttcEventTypeCharging ) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("encodeEventTypeCharging:Enter");
		}
		if(ttcEventTypeCharging == null){
			logger.error("InvalidInputException(ttcEventTypeCharging is null)");
			throw new InvalidInputException("ttcEventTypeCharging is null");
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IEncoder<TTCEventTypeCharging> encoder = CoderFactory.getInstance().newEncoder("BER");
		encoder.encode((TTCEventTypeCharging)ttcEventTypeCharging, outputStream);		
						
		if(logger.isDebugEnabled())
			logger.debug("encodeEventTypeCharging:Encoded EventTypeCharging: " + Util.formatBytes(outputStream.toByteArray()));
		if (logger.isInfoEnabled()) {
			logger.info("encodeEventTypeCharging:Exit");
		}
		return outputStream.toByteArray() ;
	}

	/**
	 * This function will decode EventTypeCharging.
	 * @param data
	 * @return TTCEventTypeCharging
	 * @throws Exception
	 */
	public static TTCEventTypeCharging decodeEventTypeCharging(byte[] data) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeEventTypeCharging:Enter");
		}
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		InputStream ins = new ByteArrayInputStream(data);
		IDecoder decoder;
		decoder = CoderFactory.getInstance().newDecoder("BER");
		TTCEventTypeCharging etcB = (TTCEventTypeCharging)decoder.decode(ins, TTCEventTypeCharging.class);;
		
		
		if(logger.isDebugEnabled())
			logger.debug("decodeEventTypeCharging: Output<--" + etcB);
		if (logger.isInfoEnabled()) {
			logger.info("decodeEventTypeCharging:Exit");
		}
		return etcB ;

	}

}
