/*******************************************************************************
 *   Copyright (c) 2011 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.genband.isup.datatypes;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.OriginalRedirectionReasonEnum;
import com.genband.isup.enumdata.RedirectingIndicatorEnum;
import com.genband.isup.enumdata.RedirectingReasonEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

//This class defined Redirection Information as defined in 3.45. 
//Octet format
//       8	7	6	5	4	3	2	1
//Octet-1	H	G	F	E	D	C	B	A
//Octet-2	P	O	N	M	L	K	J	I

// This contains follwoing fields 
// CBA - Redirecting Indicator
// D - Spare
// HGFE - Original Redirection reason
// KJI - Redirection Counter 
// L - reserved for Nation Use
// PONM - redirecting reason 

public class RedirectionInformation implements Serializable{

	/**
	 * @see RedirectingIndicator
	 */
	RedirectingIndicatorEnum redirectingIndEnum;

	/**
	 * @see OriginalRedirectionReason
	 */
	OriginalRedirectionReasonEnum originalRedirectingReasonEnum;

	/**
	 * @see RedirectingReason
	 */
	RedirectingReasonEnum redirectingReasonEnum;

	/**
	 * Redirection Counter
	 */
	int redirectionCounter;


	private static Logger logger = Logger.getLogger(RedirectionInformation.class);

	/**
	 * This function will encode redirection information. 
	 * @param redirectingIndEnum
	 * @param originalRedirectingReasonEnum
	 * @param redirectingReasonEnum
	 * @param redirectionCounter
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeRedirectionInformation(RedirectingIndicatorEnum redirectingIndEnum, 
			OriginalRedirectionReasonEnum origRedirectionReasonEnum, 
			RedirectingReasonEnum redirectingReasonEnum,
			int redirectionCounter) throws InvalidInputException {	
		logger.info("encodeRedirectionInformation:Enter");

		if(redirectingIndEnum == null){
			logger.error("encodeRedirectionInformation: InvalidInputException(RedirectingIndicatorEnum is null )");
			throw new InvalidInputException("RedirectingIndicatorEnum is null");
		}
		if(origRedirectionReasonEnum == null){
			logger.error("encodeRedirectionInformation: InvalidInputException(OriginalRedirectionReasonEnum is null )");
			throw new InvalidInputException("OriginalRedirectionReasonEnum is null");
		}
		if(redirectingReasonEnum == null){
			logger.error("encodeRedirectionInformation: InvalidInputException(RedirectingReasonEnum is null )");
			throw new InvalidInputException("RedirectingReasonEnum is null");
		}

		if (redirectionCounter < 1 || redirectionCounter > 5) {
			logger.error("encodeRedirectionInformation: RedirectionCounter shoud be between 1 and 5, entered:"
					+redirectionCounter);
			throw new InvalidInputException("RedirectionCounter is invalid");
		}
		byte[] data = new byte[2];		
		int redirectingInd        = redirectingIndEnum.getCode();
		int origRedirectingReason = origRedirectionReasonEnum.getCode();
		int redirectingReason     = redirectingReasonEnum.getCode();
		int spare                 = 0;

		data[0] = (byte) (origRedirectingReason << 4  | spare << 3 | redirectingInd);
		data[1] = (byte) (redirectingReason << 4 | spare << 3 | redirectionCounter);

		if(logger.isDebugEnabled())
			logger.debug("encodeRedirectionInformation:Encoded encodeRedirectionInformation: " + Util.formatBytes(data));
		logger.info("encodeRedirectionInformation:Exit");

		return data;
	}

	/**
	 * This function will decode redirection information
	 * @param data
	 * @return RedirectionInformation
	 * @throws InvalidInputException 
	 */
	public static RedirectionInformation decodeRedirectionInformation(byte[] data) throws InvalidInputException
	{
		logger.info("decodeRedirectionInformation: Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeRedirectionInformation: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeRedirectionInformation: InvalidInputException(Input data is null)");
			throw new InvalidInputException("Input data is null");
		}

		RedirectionInformation redirectionInfo = new RedirectionInformation();

		redirectionInfo.redirectingIndEnum            = RedirectingIndicatorEnum.fromInt(data[0] & 0x7);
		redirectionInfo.originalRedirectingReasonEnum = OriginalRedirectionReasonEnum.fromInt((data[0] >> 4) & 0xf);
		redirectionInfo.redirectionCounter            = (data[1] & 0x7);
		redirectionInfo.redirectingReasonEnum         = RedirectingReasonEnum.fromInt((data[1] >> 4 ) & 0xf);


		if(logger.isDebugEnabled())
			logger.debug("decodeRedirectionInformation: Output<--" + redirectionInfo.toString());
		logger.info("decodeRedirectionInformation:Exit");

		return redirectionInfo ;
	}

	public RedirectingIndicatorEnum getRedirectingIndEnum() {
		return redirectingIndEnum;
	}

	public void setRedirectingIndEnum(RedirectingIndicatorEnum redirectingIndEnum) {
		this.redirectingIndEnum = redirectingIndEnum;
	}

	public OriginalRedirectionReasonEnum getOriginalRedirectingReasonEnum() {
		return originalRedirectingReasonEnum;
	}

	public void setOriginalRedirectingReasonEnum(
			OriginalRedirectionReasonEnum originalRedirectingReasonEnum) {
		this.originalRedirectingReasonEnum = originalRedirectingReasonEnum;
	}

	public RedirectingReasonEnum getRedirectingReasonEnum() {
		return redirectingReasonEnum;
	}

	public void setRedirectingReasonEnum(RedirectingReasonEnum redirectingReasonEnum) {
		this.redirectingReasonEnum = redirectingReasonEnum;
	}

	public int getRedirectionCounter() {
		return redirectionCounter;
	}

	public boolean setRedirectionCounter(int redirectionCounter) {

		if (redirectionCounter < 1 || redirectionCounter > 5) {
			logger.error("setRedirectionCounter: Invalid Value of redirection counter entered: )" + redirectionCounter);
			return false;
		}

		this.redirectionCounter = redirectionCounter;
		return true;
	}

	public String toString(){

		String obj = "redirectingIndEnum:"+ redirectingIndEnum + " ,originalRedirectingReasonEnum:" + 
				originalRedirectingReasonEnum + ",redirectingReasonEnum:" + redirectingReasonEnum +
				" ,redirectionCounter: "+ redirectionCounter;
		return obj ;
	}

}
