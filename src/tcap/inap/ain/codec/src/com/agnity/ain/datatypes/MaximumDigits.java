/*******************************************************************************
* Copyright (c) 2020 Agnity, Inc. All rights reserved.
*
* This is proprietary source code of Agnity, Inc.
*
* Agnity, Inc. retains all intellectual property rights associated
* with this source code. Use is subject to license terms.
*
* This source code contains trade secrets owned by Agnity, Inc.
* Confidentiality of this computer program must be maintained at
* all times, unless explicitly authorized by Agnity, Inc.
*******************************************************************************/
package com.agnity.ain.datatypes;

import org.apache.log4j.Logger;

import com.agnity.ain.exceptions.InvalidInputException;

/**
 * this class is use for encoding and decoding 
 * the MaximumDigits as the standard GR1129.
 * @author stiwari
 *
 */
/**
		*MaximumDigits ::= INTEGER (0..255)
		*		-- range: 0--32 is "fixed number of digits" (e.g., 5 means collect 5 digits)
		*		-- 33-252 is spare
		*		-- 253 is "normal number of digits"
		*		-- 254 is "any number of digits"
		*		-- 255 is spare
		**/
public class MaximumDigits {
	
	private static Logger logger = Logger.getLogger(MaximumDigits.class);
	private int maxdigiInfo;
	

	/**
	 * encode MaximumDigits as the document GR1129 standard.
	 * @param maximumDigit
	 * @throws InvalidInputException
	 */
	
	public static Integer encodeMaximumDigits(int maximumDigit) throws InvalidInputException{
		

		if(logger.isDebugEnabled())
		{
			logger.debug("Enter: encodeMaximumDigits --> int:" +maximumDigit);
		}
		
		if(maximumDigit >= 0 || maximumDigit <=255){
			
			if(logger.isDebugEnabled())
			{
				logger.debug(" encodeMaximumDigits validated" +maximumDigit);
			}
		}
		else{
			
				logger.error(" encodeMaximumDigits validation failed" +maximumDigit);
				logger.error(" MaximumDigits range should be 0 to 255 ");
				throw new InvalidInputException("MaximumDigits range should be 0 to 255");
		}
		if(maximumDigit >= 0 && maximumDigit <=32){
			if(logger.isDebugEnabled())
			{
				logger.debug(" Maximum Digits is fixed no. of digits " +maximumDigit);
			}
		}else if(maximumDigit >= 33 && maximumDigit <=252){
			if(logger.isDebugEnabled())
			{
				logger.debug(" Maximum Digits is SPARE " +maximumDigit);
			}
		}else if(maximumDigit == 253){
			if(logger.isDebugEnabled())
			{
				logger.debug(" Maximum Digits is normal number of digits " +maximumDigit);
			}
		}else if(maximumDigit == 254){
			if(logger.isDebugEnabled())
			{
				logger.debug(" Maximum Digits is any number of digits " +maximumDigit);
			}
		}else{
			if(logger.isDebugEnabled())
			{
				logger.debug(" Maximum Digits is is spare " +maximumDigit);
			}
		}
		
		return maximumDigit;
	}
	
	/**
	 * method use for decoding MaximumDigits
	 * @param maximumDigit
	 * @return
	 * @throws InvalidInputException
	 */
	public static int decodeMaximumDigits(int maximumDigit) throws InvalidInputException{
		int diggitInfo =0;
		if(logger.isDebugEnabled())
		{
			logger.debug("Enter: decodeMaximumDigits --> int:" +maximumDigit);
		}
		if(maximumDigit >= 0 && maximumDigit <=32){
			if(logger.isDebugEnabled())
			{
				logger.debug(maximumDigit+" is fixed number of digits");
			}
			diggitInfo =1;
		}else if((maximumDigit >= 33 && maximumDigit <=252) || maximumDigit == 255){
			if(logger.isDebugEnabled())
			{
				logger.debug(maximumDigit+" is spare");
			}
			
			diggitInfo =2;
			
		}else if(maximumDigit == 253 ){
			if(logger.isDebugEnabled())
			{
				logger.debug(maximumDigit+" is normal number of digits");
			}
			diggitInfo =3;
		}else if(maximumDigit == 254){
			if(logger.isDebugEnabled())
			{
				logger.debug(maximumDigit+" is any number of digits");
			}
			diggitInfo =4;
		}else{
			logger.error(" decodeMaximumDigits validation failed");
			throw new InvalidInputException("MaximumDigits range should be 0 to 255");
		}
		
		return diggitInfo;
	}
	
		public int getMaxdigiInfo() {
			return maxdigiInfo;
		}
	
		public void setMaxdigiInfo(int maxdigiInfo) {
			this.maxdigiInfo = maxdigiInfo;
		}

}
