package com.agnity.camelv2.datatypes;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.agnity.camelv2.enumdata.RedirectingIndEnum;
import com.agnity.camelv2.enumdata.RedirectionReasonEnum;
import com.agnity.camelv2.exceptions.InvalidInputException;
import com.agnity.camelv2.util.Util;

/**
 * This class have parameters for Redirection information.
 * @author nkumar
 *
 */
public class RedirectionInfoDataType {

	/**
	 * @see RedirectingIndEnum
	 */
	RedirectingIndEnum redirectInd ;
	/**
	 * @see RedirectionReasonEnum
	 */
	RedirectionReasonEnum redirectReason ;
	/**
	 * @see RedirectionReasonEnum
	 */
	RedirectionReasonEnum origRedirectReason ;
	/**
	 * Redirection counter. Number of redirections the call has undergone expressed
	 *	as a binary number between 1 and 5.
	 */
	int redirectCounter ;

	private static Logger logger = Logger.getLogger(RedirectionInfoDataType.class);	 
	static {
		PropertyConfigurator.configure("config" + System.getProperty("file.separator") + "log4j.properties");
	}

	/**
	 * This function will encode Redirection information.
	 * @param redirectingIndEnum
	 * @param reasonEnum
	 * @param origReasonEnum
	 * @param redirectCounter
	 * @return encoded data of RedirectionInfo
	 * @throws InvalidInputException
	 */
	public static byte[] encodeRedirectionInfo(RedirectingIndEnum redirectingIndEnum , RedirectionReasonEnum reasonEnum, RedirectionReasonEnum origReasonEnum, int redirectCounter) throws InvalidInputException{

		logger.info("encodeRedirectionInfo:Enter");
		byte[] data = new byte[2];
		if(redirectingIndEnum == null){
			logger.error("encodeRedirectionInfo: InvalidInputException(redirectingIndEnum is nul)");
			throw new InvalidInputException("redirectingIndEnum is null");
		}
		if(origReasonEnum == null){
			logger.error("encodeRedirectionInfo: InvalidInputException(origReasonEnum is nul)");
			throw new InvalidInputException("origReasonEnum is null");
		}

		int redirectingInd = redirectingIndEnum.getCode();
		int reason ;
		if(reasonEnum == null){
			reason = 0 ;
		}else{
			reason = reasonEnum.getCode();		 
		}
		if(redirectCounter >=5 || redirectCounter <= 1){
			logger.error("encodeRedirectionInfo: InvalidInputException(redirectCounter should be between 1 and 5)");
			throw new InvalidInputException("redirectCounter should be between 1 and 5");
		}

		int origReason = origReasonEnum.getCode();
		data[0] = (byte)((origReason << 4) | redirectingInd );
		data[1] = (byte)((reason << 4) | redirectCounter );
		if(logger.isDebugEnabled())
		logger.debug("encodeRedirectionInfo:Encoded RedirectINfo: " + Util.formatBytes(data));
		logger.info("encodeRedirectionInfo:Exit");
		return data ;
	}

	public static RedirectionInfoDataType decodeRedirectionInfo(byte[] data) throws InvalidInputException{
		logger.info("decodeRedirectionInfo:Enter");
		if(data == null){
			logger.error("decodeRedirectionInfo: InvalidInputException(data is nul)");
			throw new InvalidInputException("data is null"); 
		}
		RedirectionInfoDataType redrnInfo = new RedirectionInfoDataType();

		int origReason = ((data[0] >> 4) & 0xF);
		redrnInfo.origRedirectReason = RedirectionReasonEnum.fromInt(origReason);
		int redirectInd = (data[0] & 0x7);
		redrnInfo.redirectInd = RedirectingIndEnum.fromInt(redirectInd);

		if(data.length > 1){
			int reason = ((data[1] >> 4) & 0xF);
			redrnInfo.redirectReason = RedirectionReasonEnum.fromInt(reason);
			redrnInfo.redirectCounter = (data[1] & 0x7);
		}
		logger.info("decodeRedirectionInfo:Output<--"+ redrnInfo.toString());
		logger.info("decodeRedirectionInfo:Exit");
		return redrnInfo ;
	}

	public RedirectingIndEnum getRedirectInd() {
		return redirectInd;
	}

	public RedirectionReasonEnum getRedirectReason() {
		return redirectReason;
	}

	public RedirectionReasonEnum getOrigRedirectReason() {
		return origRedirectReason;
	}

	public int getRedirectCounter() {
		return redirectCounter;
	}

	public void setRedirectInd(RedirectingIndEnum redirectInd) {
		this.redirectInd = redirectInd;
	}

	public void setRedirectReason(RedirectionReasonEnum redirectReason) {
		this.redirectReason = redirectReason;
	}

	public void setOrigRedirectReason(RedirectionReasonEnum origRedirectReason) {
		this.origRedirectReason = origRedirectReason;
	}

	public void setRedirectCounter(int redirectCounter) {
		this.redirectCounter = redirectCounter;
	}

	public String toString(){

		String obj = "redirectInd:"+ redirectInd + " ,redirectReason:"+ redirectReason + " ,origRedirectReason:" + origRedirectReason +

		" ,redirectCounter:" + redirectCounter ;
		return obj ;
	}
}
