/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
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

import org.apache.log4j.Logger;

import com.agnity.cap.v3.datatypes.enumType.CallCompletionTreatmentIndCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.ConferenceTreatmentIndCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;

/**
 * ref- 3GPP TS 29.078 V4.9.0 (2009-09) 
 * @author rnarayan
 *BackwardServiceInteractionInd     ::= SEQUENCE { 
  conferenceTreatmentIndicator    [1] OCTET STRING (SIZE(1))  OPTIONAL, 
  -- acceptConferenceRequest  'xxxx xx01'B 
  -- rejectConferenceRequest  'xxxx xx10'B 
  -- if absent from Connect or ContinueWithArgument, 
  -- then CAMEL service does not affect conference treatement 
  callCompletionTreatmentIndicator  [2] OCTET STRING (SIZE(1))  OPTIONAL, 
  -- acceptCallCompletionServiceRequest  'xxxx xx01'B, 
  -- rejectCallCompletionServiceRequest  'xxxx xx10'B 
  -- if absent from Connect or ContinueWithArgument, 
  -- then CAMEL service does not affect call completion treatment 
  ... 
  } 
 */
public class BackwardServiceInteractionIndCapV3 {

	private ConferenceTreatmentIndCapV3Enum conTreatInd;
	private CallCompletionTreatmentIndCapV3Enum callComptreatInd;
	private static Logger logger = Logger.getLogger(BackwardServiceInteractionIndCapV3.class);
	
	
	/**
	 * 
	 * @return ConferenceTreatmentIndCapV3Enum
	 */
	public ConferenceTreatmentIndCapV3Enum getConTreatInd() {
		return conTreatInd;
	}

	/**
	 * set ConferenceTreatmentIndCapV3Enum
	 * @param conTreatInd
	 */
	public void setConTreatInd(ConferenceTreatmentIndCapV3Enum conTreatInd) {
		this.conTreatInd = conTreatInd;
	}

	/**
	 * 
	 * @return CallCompletionTreatmentIndCapV3Enum
	 */
	public CallCompletionTreatmentIndCapV3Enum getCallComptreatInd() {
		return callComptreatInd;
	}

	/**
	 * set CallCompletionTreatmentIndCapV3Enum
	 * @param callComptreatInd
	 */
	public void setCallComptreatInd(CallCompletionTreatmentIndCapV3Enum callComptreatInd) {
		this.callComptreatInd = callComptreatInd;
	}

	/**
	 * decode BackwardServiceInteractionInd non asn parameters
	 * @param BackwardServiceInteractionInd byte array
	 * @return BackwardServiceInteractionIndCapV3 object
	 */
	public static BackwardServiceInteractionIndCapV3 decode(byte[] bytes)  throws InvalidInputException{
		
		if(logger.isDebugEnabled()){
			logger.debug("length of byte array is "+bytes.length);
		}
		BackwardServiceInteractionIndCapV3 bsi = null;
			bsi = new BackwardServiceInteractionIndCapV3();
			bsi.conTreatInd = ConferenceTreatmentIndCapV3Enum.getValue(bytes[0]);
			bsi.callComptreatInd = CallCompletionTreatmentIndCapV3Enum.getValue(bytes[1]);
			if(logger.isDebugEnabled()){
				logger.debug("BackwardServiceInteractionInd decoaded successfuly");
			}

		return bsi;
	}
	
	/**
	 * encode BackwardServiceInteractionIndCapV3 parameters into BackwardServiceInteractionInd
	 * byte array
	 * @param BackwardServiceInteractionIndCapV3 bsi
	 * @return BackwardServiceInteractionInd byte array
	 */
	public static byte[] encode(BackwardServiceInteractionIndCapV3 bsi)  throws InvalidInputException{
		byte[] bytes=null;
	         byte b1 = (byte)(bsi.conTreatInd==null?0:bsi.conTreatInd.getCode());	
	         byte b2 = (byte)(bsi.callComptreatInd==null?0:bsi.callComptreatInd.getCode());
	         bytes = new byte[]{b1,b2};
	         if(logger.isDebugEnabled()){
					logger.debug("BackwardServiceInteractionInd encoded successfuly");
				}
		return bytes;
	}
	
/*	public static void main(String[] args) {
		String hexString = "0201";
		byte[] bytes = CapV2Functions.hexStringToByteArray(hexString);
		BackwardServiceInteractionIndCapV3 bsi = BackwardServiceInteractionIndCapV3.decode(bytes);
		byte[] bytesAfterEncode = BackwardServiceInteractionIndCapV3.encode(bsi);
		System.out.println(Arrays.equals(bytes, bytesAfterEncode));
	}*/
	
}
