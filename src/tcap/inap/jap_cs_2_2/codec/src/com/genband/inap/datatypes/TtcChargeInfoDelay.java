package com.genband.inap.datatypes;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.genband.inap.enumdata.ChargeInfoDelayEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;


/**
 * Used for encoding and decoding of TtcChargeInfoDelay
 * @author vgoel
 *
 */
public class TtcChargeInfoDelay { 
	
	LinkedList<ChargeInfoDelayEnum> chargeInfoDelay;
	
	public LinkedList<ChargeInfoDelayEnum> getChargeInfoDelay() {
		return chargeInfoDelay;
	}

	public void setChargeInfoDelay(LinkedList<ChargeInfoDelayEnum> chargeInfoDelay) {
		this.chargeInfoDelay = chargeInfoDelay;
	}

	private static Logger logger = Logger.getLogger(TtcChargeInfoDelay.class);	 
	 
	
	/**
	 * This function will encode the TTC Charge Information Delay.
	 * @param chargeInfoDelay
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeTtcChargeInfoDelay(LinkedList<ChargeInfoDelayEnum> chargeInfoDelay) throws InvalidInputException {
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcChargeInfoDelay:Enter");
		}
		if(chargeInfoDelay == null){
			logger.error("encodeTtcChargeInfoDelay: InvalidInputException(chargeInfoDelay is null)");
			throw new InvalidInputException("chargeInfoDelay is null");
		}
		
		int i = 0;
		byte[] myParams = new byte[chargeInfoDelay.size()];
		for(ChargeInfoDelayEnum cid : chargeInfoDelay){
			myParams[i++] = (byte) (cid.getCode());
		}
		
		if(logger.isDebugEnabled())
			logger.debug("encodeTtcChargeInfoDelay:Encoded Ttc charge info delay: " + Util.formatBytes(myParams));
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcChargeInfoDelay:Exit");
		}
		return myParams;
	}
	
	/**
	 * This function will decode TTC Charge Information Delay.
	 * @param data
	 * @return decode object
	 * @throws InvalidInputException
	 */
	public static TtcChargeInfoDelay decodeTtcChargeInfoDelay(byte[] data) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcChargeInfoDelay:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcChargeInfoDelay: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeTtcChargeInfoDelay: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		TtcChargeInfoDelay ttcCID = new TtcChargeInfoDelay();
		ttcCID.chargeInfoDelay = new LinkedList<ChargeInfoDelayEnum>();
		for(int i=0; i<data.length; i++){
			ttcCID.chargeInfoDelay.add(ChargeInfoDelayEnum.fromInt(data[i] & 0xFF));
		}	
		
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcChargeInfoDelay: Output<--" + ttcCID.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcChargeInfoDelay:Exit");
		}
		return ttcCID ;
	}


	public String toString(){
		
		String obj = "chargeInfoDelay:"+ chargeInfoDelay ;
		return obj ;
	}

}

