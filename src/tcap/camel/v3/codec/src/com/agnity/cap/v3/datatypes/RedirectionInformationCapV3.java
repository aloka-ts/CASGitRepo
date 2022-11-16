/****
 * Copyright (coffee) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/
package com.agnity.cap.v3.datatypes;

import java.util.Arrays;

import com.agnity.cap.v3.datatypes.enumType.OriginalRedirectionReasonCapv3Enum;
import com.agnity.cap.v3.datatypes.enumType.RedirectingIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.RedirectingReasonCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;


public class RedirectionInformationCapV3 {
	
	
	private RedirectingIndicatorCapV3Enum redirectingIndicator;
	private OriginalRedirectionReasonCapv3Enum originalRedirectionReason;
	private RedirectingReasonCapV3Enum redirectingReason;
	
	
	public OriginalRedirectionReasonCapv3Enum getOriginalRedirectionReason() {
		return originalRedirectionReason;
	}
	public void setOriginalRedirectionReason(
			OriginalRedirectionReasonCapv3Enum originalRedirectionReason) {
		this.originalRedirectionReason = originalRedirectionReason;
	}
	public RedirectingIndicatorCapV3Enum getRedirectingIndicator() {
		return redirectingIndicator;
	}
	public void setRedirectingIndicator(
			RedirectingIndicatorCapV3Enum redirectingIndicator) {
		this.redirectingIndicator = redirectingIndicator;
	}
	public RedirectingReasonCapV3Enum getRedirectingReason() {
		return redirectingReason;
	}
	public void setRedirectingReason(RedirectingReasonCapV3Enum redirectingReason) {
		this.redirectingReason = redirectingReason;
	}
	
	
	
	public static RedirectionInformationCapV3 decode(byte[] bytes) throws InvalidInputException {
		RedirectionInformationCapV3 redirecton = new RedirectionInformationCapV3();
		redirecton.redirectingIndicator = RedirectingIndicatorCapV3Enum.getValue((byte) (bytes[0])&0x07);
		redirecton.originalRedirectionReason = OriginalRedirectionReasonCapv3Enum.getValue((byte) (bytes[0])&0xf0);
		redirecton.redirectingReason=RedirectingReasonCapV3Enum.getValue((byte) (bytes[1])&0xf0);
		return redirecton;
	}


	public static byte[] encode(RedirectionInformationCapV3 redirecton) throws InvalidInputException
	{	
		int tag1=redirecton.redirectingIndicator.getCode();
		int tag2=redirecton.originalRedirectionReason.getCode();
		byte byte1 = (byte)((tag2<<4)+tag1);
		int tag3=redirecton.redirectingReason.getCode();
		byte byte2 = (byte)(tag3<<4);
				
		byte[] bytes = new byte[2];
		bytes[0]=byte1;
		bytes[1]=byte2;
		return bytes;
	}
	
	
	/******
	 ***
	 **
	For Unit Testing
	 *****
	 ****
	 ***
	 */

	public static void main(String[] args) throws InvalidInputException{

		RedirectionInformationCapV3 decode= new RedirectionInformationCapV3();
		RedirectionInformationCapV3 decodeAG= new RedirectionInformationCapV3();
        
		decode.setOriginalRedirectionReason(OriginalRedirectionReasonCapv3Enum.no_reply);
		decode.setRedirectingIndicator(RedirectingIndicatorCapV3Enum.call_diverted);
		decode.setRedirectingReason(RedirectingReasonCapV3Enum.deflection_during_alerting);
		
		byte[] Values = encode(decode);
  
		//byte[] Values = new byte[]{(byte)0x36,(byte)0x40};

		decode=RedirectionInformationCapV3.decode(Values);

		System.out.println("redirectingindicator :" +decode.redirectingIndicator);
		System.out.println("originalredirectionreason :" +decode.originalRedirectionReason);
		System.out.println("redirectingreason :" +decode.redirectingReason);
		

		byte[] Values2 =RedirectionInformationCapV3.encode(decode);
		
		decodeAG=RedirectionInformationCapV3.decode(Values2);	

		System.out.println("redirectingindicator :" +decodeAG.redirectingIndicator);
		System.out.println("originalredirectionreason :" +decodeAG.originalRedirectionReason);
		System.out.println("redirectingreason :" +decodeAG.redirectingReason);
		
       System.out.println(Arrays.equals(Values, Values2));
	}
}

	

