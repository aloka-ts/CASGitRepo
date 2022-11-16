package com.agnity.inapitutcs2.datatypes;

import org.apache.log4j.Logger;

import com.agnity.inapitutcs2.enumdata.ReasonEnum;
import com.agnity.inapitutcs2.exceptions.InvalidInputException;
import com.agnity.inapitutcs2.util.Util;

/**
 * Used for encoding and decoding of Reason
 * @author Mriganka
 *
 */

public class Reason {

	/**
	 * @see ReasonEnum
	 */
	ReasonEnum reasonEnum ;

	private static Logger logger = Logger.getLogger(Reason.class);	 

	public ReasonEnum getReasonEnum() {
		return reasonEnum;
	}
	
	
	public void setReasonEnum(ReasonEnum reasonEnum) {
		this.reasonEnum = reasonEnum;
	}

	
	/**
	 * This function will encode Reason.
	 * @param reasonEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeReason(ReasonEnum reasonEnum) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("encodeReason:Enter");
		}
		byte[] myParms = new byte[1];

		int reasonVal ;
		if(reasonEnum == null){
			logger.error("encodeReason: InvalidInputException(reasonEnum is null)");
			throw new InvalidInputException("reasonEnum is null");
		}
		reasonVal = reasonEnum.getCode();
		
		myParms[0] = (byte) (reasonVal & 0xFF);

		if(logger.isDebugEnabled())
			logger.debug("encodeReason:Encoded Reason: "+ Util.formatBytes(myParms));
		if (logger.isInfoEnabled()) {
			logger.info("encodeReason:Exit");
		}
		return myParms;
	}
	
	/**
	 * This function will decode Reason.
	 * @param data
	 * @return object of Reason
	 * @throws InvalidInputException
	 */
	public static Reason decodeReason(byte[] data) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("decodeReason:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeReason: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeReason: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		Reason reasonData = new Reason();
		reasonData.setReasonEnum(ReasonEnum.fromInt(data[0] & 0xff));
		
		if(logger.isDebugEnabled())
			logger.debug("decodeReason: Output<--" + reasonData.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeReason:Exit");
		}
		return reasonData ;
	}
	
    public String toString(){
		
		String obj = "reasonEnum:"+ reasonEnum ;
		return obj ;
	}
}
