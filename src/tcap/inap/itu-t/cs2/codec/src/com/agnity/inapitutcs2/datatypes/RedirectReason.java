package com.agnity.inapitutcs2.datatypes;

import org.apache.log4j.Logger;

import com.agnity.inapitutcs2.enumdata.RedirectionReasonEnum;
import com.agnity.inapitutcs2.exceptions.InvalidInputException;
import com.agnity.inapitutcs2.util.Util;

/**
 * Used for encoding and decoding of RedirectReason
 * @author Mriganka
 *
 */

public class RedirectReason {

	/**
	 * @see RedirectionReasonEnum
	 */
	RedirectionReasonEnum redirectionReasonEnum ;

	private static Logger logger = Logger.getLogger(RedirectReason.class);	 

	public RedirectionReasonEnum getRedirectionReasonEnum() {
		return redirectionReasonEnum;
	}
	
	
	public void setRedirectionReasonEnum(RedirectionReasonEnum redirectionReasonEnum) {
		this.redirectionReasonEnum = redirectionReasonEnum;
	}

	
	/**
	 * This function will encode Redirection Reason.
	 * @param redirectionReasonEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeRedirectionReason(RedirectionReasonEnum redirectionReasonEnum) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("encodeRedirectionReason:Enter");
		}
		byte[] myParms = new byte[1];

		int redirectionReasonVal ;
		if(redirectionReasonEnum == null){
			logger.error("encodeRedirectionReason: InvalidInputException(reasonEnum is null)");
			throw new InvalidInputException("reasonEnum is null");
		}
		redirectionReasonVal = redirectionReasonEnum.getCode();
		
		myParms[0] = (byte) (redirectionReasonVal);

		if(logger.isDebugEnabled())
			logger.debug("encodeRedirectionReason:Encoded RedirectionReason: "+ Util.formatBytes(myParms));
		if (logger.isInfoEnabled()) {
			logger.info("encodeRedirectionReason:Exit");
		}
		return myParms;
	}
	
	/**
	 * This function will decode Redirection Reason.
	 * @param data
	 * @return object of Redirection Reason
	 * @throws InvalidInputException
	 */
	public static RedirectReason decodeRedirectionReason(byte[] data) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("decodeRedirectionReason:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeRedirectionReason: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeRedirectionReason: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		RedirectReason reasonData = new RedirectReason();		
		reasonData.setRedirectionReasonEnum(RedirectionReasonEnum.fromInt(data[0]));
		
		if(logger.isDebugEnabled())
			logger.debug("decodeRedirectionReason: Output<--" + reasonData.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeRedirectionReason:Exit");
		}
		return reasonData ;
	}
	
    public String toString(){
		
		String obj = "redirectionReasonEnum:"+ redirectionReasonEnum ;
		return obj ;
	}
}
