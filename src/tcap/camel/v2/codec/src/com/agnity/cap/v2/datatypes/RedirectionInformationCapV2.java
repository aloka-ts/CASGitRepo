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
package com.agnity.cap.v2.datatypes;

import java.util.Arrays;

import com.agnity.cap.v2.datatypes.enumType.OriginalRedirectionReasonCapv2Enum;
import com.agnity.cap.v2.datatypes.enumType.RedirectingIndicatorCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.RedirectingReasonCapV2Enum;
import com.agnity.cap.v2.exceptions.InvalidInputException;


public class RedirectionInformationCapV2 {
	
	
	private RedirectingIndicatorCapV2Enum redirectingIndicator;
	private OriginalRedirectionReasonCapv2Enum originalRedirectionReason;
	private RedirectingReasonCapV2Enum redirectingReason;
	
	
	public OriginalRedirectionReasonCapv2Enum getOriginalRedirectionReason() {
		return originalRedirectionReason;
	}
	public void setOriginalRedirectionReason(
			OriginalRedirectionReasonCapv2Enum originalRedirectionReason) {
		this.originalRedirectionReason = originalRedirectionReason;
	}
	public RedirectingIndicatorCapV2Enum getRedirectingIndicator() {
		return redirectingIndicator;
	}
	public void setRedirectingIndicator(
			RedirectingIndicatorCapV2Enum redirectingIndicator) {
		this.redirectingIndicator = redirectingIndicator;
	}
	public RedirectingReasonCapV2Enum getRedirectingReason() {
		return redirectingReason;
	}
	public void setRedirectingReason(RedirectingReasonCapV2Enum redirectingReason) {
		this.redirectingReason = redirectingReason;
	}
	
	
	
	public static RedirectionInformationCapV2 decode(byte[] bytes) throws InvalidInputException {
		RedirectionInformationCapV2 redirecton = new RedirectionInformationCapV2();
		redirecton.redirectingIndicator = RedirectingIndicatorCapV2Enum.getValue((byte) (bytes[0])&0x07);
		redirecton.originalRedirectionReason = OriginalRedirectionReasonCapv2Enum.getValue((byte) (bytes[0])&0xf0);
		redirecton.redirectingReason=RedirectingReasonCapV2Enum.getValue((byte) (bytes[1])&0xf0);
		return redirecton;
	}


	public static byte[] encode(RedirectionInformationCapV2 redirecton) throws InvalidInputException
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

		RedirectionInformationCapV2 decode= new RedirectionInformationCapV2();
		RedirectionInformationCapV2 decodeAG= new RedirectionInformationCapV2();
        
		decode.setOriginalRedirectionReason(OriginalRedirectionReasonCapv2Enum.no_reply);
		decode.setRedirectingIndicator(RedirectingIndicatorCapV2Enum.call_diverted);
		decode.setRedirectingReason(RedirectingReasonCapV2Enum.deflection_during_alerting);
		
		byte[] Values = encode(decode);
  
		//byte[] Values = new byte[]{(byte)0x36,(byte)0x40};

		decode=RedirectionInformationCapV2.decode(Values);

		System.out.println("redirectingindicator :" +decode.redirectingIndicator);
		System.out.println("originalredirectionreason :" +decode.originalRedirectionReason);
		System.out.println("redirectingreason :" +decode.redirectingReason);
		

		byte[] Values2 =RedirectionInformationCapV2.encode(decode);
		
		decodeAG=RedirectionInformationCapV2.decode(Values2);	

		System.out.println("redirectingindicator :" +decodeAG.redirectingIndicator);
		System.out.println("originalredirectionreason :" +decodeAG.originalRedirectionReason);
		System.out.println("redirectingreason :" +decodeAG.redirectingReason);
		
       System.out.println(Arrays.equals(Values, Values2));
	}
}

	

