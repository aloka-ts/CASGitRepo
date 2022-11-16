package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.SusResIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * This class have parameters for Suspend, Resume Indicators. 
 * @author vgoel
 *
 */
public class SusResIndicators {

	/**
	 * @see SusResIndicators
	 */
	SusResIndEnum susResIndEnum;
	
	private static Logger logger = Logger.getLogger(SusResIndicators.class);

	public SusResIndEnum getSusResIndicators() {
		return susResIndEnum;
	}

	public void setSusResIndEnum(SusResIndEnum susResIndEnum) {
		this.susResIndEnum = susResIndEnum;
	}
	
	/**
	 * This function will encode suspend/resume indicators
	 * @param susResIndEnum
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeSusResInd(SusResIndEnum susResIndEnum) throws InvalidInputException
	{	
		logger.info("encodeSusResInd:Enter");

		if(susResIndEnum == null){
			logger.error("encodeSusResInd:Enter: InvalidInputException(susResIndEnum is null )");
			throw new InvalidInputException("susResIndEnum is null");
		}
		
		byte[] data = new byte[1];		
		int susResInd = susResIndEnum.getCode();
				
		data[0] = (byte) susResInd;
		
		if(logger.isDebugEnabled())
			logger.debug("encodeSusResInd:Encoded Sus/Res indicators: " + Util.formatBytes(data));
		logger.info("encodeSusResInd:Exit");
		
		return data;
	}
	
	
	/**
	 * This function will decode suspend/resume indicators
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static SusResIndicators decodeSusResInd(byte[] data) throws InvalidInputException
	{
		logger.info("decodeSusResInd:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeSusResInd: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeSusResInd: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		SusResIndicators susResInd = new SusResIndicators();
		
		susResInd.susResIndEnum = SusResIndEnum.fromInt(data[0] & 0x01);
		
		if(logger.isDebugEnabled())
			logger.debug("decodeSusResInd: Output<--" + susResInd.toString());
		logger.info("decodeSusResInd:Exit");
		
		return susResInd ;
	}
	
	public String toString(){
		
		String obj = "susResIndEnum:"+ susResIndEnum;
		return obj ;
	}	
}
