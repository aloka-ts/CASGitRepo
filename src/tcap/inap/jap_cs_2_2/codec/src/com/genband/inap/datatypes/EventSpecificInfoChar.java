package com.genband.inap.datatypes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;

import com.genband.inap.asngenerated.TTCEventSpecificInformationCharging;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;

/**
 * Used for encoding and decoding of EventSpecificInformationCharging
 * @author vgoel
 *
 */
public class EventSpecificInfoChar
{
	/**
	 * @see TTCEventSpecificInformationCharging
	 */
	TTCEventSpecificInformationCharging ttcEventSpecificInformationCharging;

	private static Logger logger = Logger.getLogger(EventSpecificInfoChar.class);	
	
	public TTCEventSpecificInformationCharging getTtcEventSpecificInformationCharging() {
		return ttcEventSpecificInformationCharging;
	}

	public void setTtcEventSpecificInformationCharging(
			TTCEventSpecificInformationCharging ttcEventSpecificInformationCharging) {
		this.ttcEventSpecificInformationCharging = ttcEventSpecificInformationCharging;
	}

	/**
	 * This function will encode EventSpecificInformationCharging.
	 * @param ttcEventSpecificInformationCharging
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] encodeEventSpecificInfoCharging(TTCEventSpecificInformationCharging ttcEventSpecificInformationCharging ) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("encodeEventSpecificInfoCharging:Enter");
		}
		if(ttcEventSpecificInformationCharging == null){
			logger.error("InvalidInputException(ttcEventSpecificInformationCharging is null)");
			throw new InvalidInputException("ttcEventSpecificInformationCharging is null");
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IEncoder<TTCEventSpecificInformationCharging> encoder = CoderFactory.getInstance().newEncoder("BER");
		encoder.encode((TTCEventSpecificInformationCharging)ttcEventSpecificInformationCharging, outputStream);		
						
		if(logger.isDebugEnabled())
			logger.debug("encodeEventSpecificInfoCharging:Encoded EventSpecificInfoCharging: " + Util.formatBytes(outputStream.toByteArray()));
		if (logger.isInfoEnabled()) {
			logger.info("encodeEventSpecificInfoCharging:Exit");
		}
		return outputStream.toByteArray() ;
	}

	/**
	 * This function will decode EventSpecificInformationCharging.
	 * @param data
	 * @return TTCEventSpecificInformationCharging
	 * @throws Exception
	 */
	public static TTCEventSpecificInformationCharging decodeEventSpecificInfoCharging(byte[] data) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeEventSpecificInfoCharging:Enter");
		}
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		InputStream ins = new ByteArrayInputStream(data);
		IDecoder decoder;
		decoder = CoderFactory.getInstance().newDecoder("BER");
		TTCEventSpecificInformationCharging esicB = (TTCEventSpecificInformationCharging)decoder.decode(ins, TTCEventSpecificInformationCharging.class);;
		
		
		if(logger.isDebugEnabled())
			logger.debug("decodeEventSpecificInfoCharging: Output<--" + esicB);
		if (logger.isInfoEnabled()) {
			logger.info("decodeEventSpecificInfoCharging:Exit");
		}
		return esicB ;

	}

}
