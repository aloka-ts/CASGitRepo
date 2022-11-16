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

import com.agnity.cap.v3.datatypes.enumType.CallDiversionTreatmentIndCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.CallingPartyRestrictionIndCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.ConferenceTreatmentIndCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;

/**
 * ref- 3GPP TS 29.078 V4.9.0 (2009-09) 
 * @author rnarayan
 * ForwardServiceInteractionInd             ::= SEQUENCE { 
  conferenceTreatmentIndicator  [1] OCTET STRING (SIZE(1))      OPTIONAL, 
  -- acceptConferenceRequest  'xxxx xx01'B 
  -- rejectConferenceRequest  'xxxx xx10'B 
  -- if absent from Connect or ContinueWithArgument, 
  -- then CAMEL service does not affect conference treatment 
  callDiversionTreatmentIndicator [2] OCTET STRING (SIZE(1))    OPTIONAL, 
  -- callDiversionAllowed   'xxxx xx01'B 
  -- callDiversionNotAllowed  'xxxx xx10'B 
  -- if absent from Connect or ContinueWithArgument, 
  -- then CAMEL service does not affect call diversion treatment 
  callingPartyRestrictionIndicator [4] OCTET STRING (SIZE(1))   OPTIONAL, 
  -- noINImpact        'xxxx xx01'B 
  -- presentationRestricted  'xxxx xx10'B 
  -- if absent from Connect or ContinueWithArgument, 
  -- then CAMEL service does not affect calling party restriction treatment 
... 
  } 
 */
public class ForwardServiceInteractionIndCapV3 {

	
	private ConferenceTreatmentIndCapV3Enum conTreatInd;
	private CallDiversionTreatmentIndCapV3Enum callDivTreatInd;
	private CallingPartyRestrictionIndCapV3Enum callingPartyResInd;
	private static Logger logger = Logger.getLogger(ForwardServiceInteractionIndCapV3.class);
	
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
	 * @return CallDiversionTreatmentIndCapV3Enum
	 */
	public CallDiversionTreatmentIndCapV3Enum getCallDivTreatInd() {
		return callDivTreatInd;
	}

	/**
	 * set CallDiversionTreatmentIndCapV3Enum
	 * @param callDivTreatInd
	 */
	public void setCallDivTreatInd(CallDiversionTreatmentIndCapV3Enum callDivTreatInd) {
		this.callDivTreatInd = callDivTreatInd;
	}

	/**
	 * 
	 * @return CallingPartyRestrictionIndCapV3Enum
	 */
	public CallingPartyRestrictionIndCapV3Enum getCallingPartyResInd() {
		return callingPartyResInd;
	}

	/**
	 * set CallingPartyRestrictionIndCapV3Enum
	 * @param callingPartyResInd
	 */
	public void setCallingPartyResInd(
			CallingPartyRestrictionIndCapV3Enum callingPartyResInd) {
		this.callingPartyResInd = callingPartyResInd;
	}

	/**
	 * decode ForwardServiceInteractionInd non asn parameters
	 * @param ForwardServiceInteractionInd bytes
	 * @return ForwardServiceInteractionIndCapV3 object 
	 */
	public static ForwardServiceInteractionIndCapV3 decode(byte[] bytes)  throws InvalidInputException{
		if(logger.isDebugEnabled()){
			logger.debug("length of byte array is "+bytes.length);
		}
		ForwardServiceInteractionIndCapV3 forSerInt=null;
			forSerInt = new ForwardServiceInteractionIndCapV3();
			forSerInt.conTreatInd = ConferenceTreatmentIndCapV3Enum.getValue(bytes[0]);
			forSerInt.callDivTreatInd = CallDiversionTreatmentIndCapV3Enum.getValue(bytes[1]);
			forSerInt.callingPartyResInd = CallingPartyRestrictionIndCapV3Enum.getValue(bytes[2]);
			if (logger.isDebugEnabled()) {
				logger.debug("ForwardServiceInteractionInd succesfuly decoded");
			}

		return forSerInt;
	}
	
	/**
	 * encode ForwardServiceInteractionIndCapV3 non asn parameters into byte
	 * array of ForwardServiceInteractionInd
	 * @param ForwardServiceInteractionIndCapV3 forSerInt
	 * @return byte array of ForwardServiceInteractionInd
	 */
	public static byte[] encode(ForwardServiceInteractionIndCapV3 forSerInt) throws InvalidInputException {
		byte[] bytes=null;
		   	byte b0 = (byte)(forSerInt.conTreatInd==null?0:forSerInt.conTreatInd.getCode());
		   	byte b1 = (byte)(forSerInt.callDivTreatInd==null?0:forSerInt.callDivTreatInd.getCode());
		   	byte b2 = (byte) (forSerInt.callingPartyResInd==null?0:forSerInt.callingPartyResInd.getCode());
		   	bytes = new byte[]{b0,b1,b2};
		   	if(logger.isDebugEnabled()){
		   		logger.debug("ForwardServiceInteractionInd successfuly encoded & bytes length:"+bytes.length);
		   	}
		return bytes;
	}
	
/*	public static void main(String[] args) {
		String hexString = "010202";
		byte[] bytes = CapV2Functions.hexStringToByteArray(hexString);
		ForwardServiceInteractionIndCapV3 fsi = ForwardServiceInteractionIndCapV3.decode(bytes);
		byte[] bytesAfterEncode = ForwardServiceInteractionIndCapV3.encode(fsi);
		System.out.println(Arrays.equals(bytes, bytesAfterEncode));
	}*/
	
}
