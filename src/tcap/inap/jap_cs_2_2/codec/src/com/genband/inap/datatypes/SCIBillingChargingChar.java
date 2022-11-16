package com.genband.inap.datatypes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;

import com.genband.inap.asngenerated.TTCSCIBillingChargingCharacteristics;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;

/**
 * Used for encoding and decoding of SCIBillingChargingCharacteristics
 * @author vgoel
 *
 */
public class SCIBillingChargingChar
{
	/**
	 * @see TTCSCIBillingChargingCharacteristics
	 */
	TTCSCIBillingChargingCharacteristics ttcsciBillingChargingCharacteristics;

	private static Logger logger = Logger.getLogger(SCIBillingChargingChar.class);	
	
	
	public TTCSCIBillingChargingCharacteristics getTtcsciBillingChargingCharacteristics() {
		return ttcsciBillingChargingCharacteristics;
	}

	public void setTtcsciBillingChargingCharacteristics(
			TTCSCIBillingChargingCharacteristics ttcsciBillingChargingCharacteristics) {
		this.ttcsciBillingChargingCharacteristics = ttcsciBillingChargingCharacteristics;
	}
	
	/**
	 * This function will encode SciBillingCharacterstics.
	 * @param ttcsciBillingChargingCharacteristics
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] encodeSciBillingChar(TTCSCIBillingChargingCharacteristics ttcsciBillingChargingCharacteristics ) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("encodeSciBillingChar:Enter");
		}
		if(ttcsciBillingChargingCharacteristics == null){
			logger.error("InvalidInputException(ttcsciBillingChargingCharacteristics is null)");
			throw new InvalidInputException("ttcsciBillingChargingCharacteristics is null");
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		IEncoder<TTCSCIBillingChargingCharacteristics> encoder = CoderFactory.getInstance().newEncoder("BER");
		encoder.encode((TTCSCIBillingChargingCharacteristics)ttcsciBillingChargingCharacteristics, outputStream);		
						
		if(logger.isDebugEnabled())
			logger.debug("encodeSciBillingChar:Encoded SCIBillingChar: " + Util.formatBytes(outputStream.toByteArray()));
		if (logger.isInfoEnabled()) {
			logger.info("encodeSciBillingChar:Exit");
		}
		return outputStream.toByteArray() ;
	}

	/**
	 * This function will decode SciBillingCharacterstics.
	 * @param data
	 * @return TTCSCIBillingChargingCharacteristics
	 * @throws Exception
	 */
	public static TTCSCIBillingChargingCharacteristics decodeSciBillingChar(byte[] data) throws Exception
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeSciBillingChar:Enter");
		}
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		InputStream ins = new ByteArrayInputStream(data);
		IDecoder decoder;
		decoder = CoderFactory.getInstance().newDecoder("BER");
		TTCSCIBillingChargingCharacteristics sciB = (TTCSCIBillingChargingCharacteristics)decoder.decode(ins, TTCSCIBillingChargingCharacteristics.class);;
		
		
		if(logger.isDebugEnabled())
			logger.debug("decodeSciBillingChar: Output<--" + sciB);
		if (logger.isInfoEnabled()) {
			logger.info("decodeSciBillingChar:Exit");
		}
		return sciB ;

	}

}
