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
import org.bn.types.NullObject;

import asnGenerated.v3.BackwardServiceInteractionInd;
import asnGenerated.v3.BothwayThroughConnectionInd;
import asnGenerated.v3.ConnectedNumberTreatmentInd;
import asnGenerated.v3.ForwardServiceInteractionInd;
import asnGenerated.v3.ServiceInteractionIndicatorsTwo;

import com.agnity.cap.v3.datatypes.enumType.CwTreatmentIndCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.EctTreatmentIndCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.HoldTreatmentIndCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;

/**
 * ref-3GPP TS 29.078 V4.9.0
 * @author rnarayan
 * 
ServiceInteractionIndicatorsTwo             ::= SEQUENCE { 
  forwardServiceInteractionInd    [0] ForwardServiceInteractionInd    OPTIONAL, 
  -- applicable to operations InitialDP, Connect and ContinueWithArgument. 
  backwardServiceInteractionInd    [1] BackwardServiceInteractionInd    OPTIONAL, 
  -- applicable to operations Connect and ContinueWithArgument. 
  bothwayThroughConnectionInd     [2] BothwayThroughConnectionInd     OPTIONAL, 
  -- applicable to ConnectToResource and EstablishTemporaryConnection 
  connectedNumberTreatmentInd     [4] ConnectedNumberTreatmentInd     OPTIONAL, 
  -- applicable to Connect and ContinueWithArgument 
  nonCUGCall              [13] NULL                OPTIONAL, 
  -- applicable to Connect and ContinueWithArgument 
  -- indicates that no parameters for CUG shall be used for the call (i.e. the call shall  
  -- be a non-CUG call).  
  -- If not present, it indicates one of three things: 
  --  a) continue with modified CUG information (when one or more of either CUG Interlock C
  --     and Outgoing Access Indicator are present), or 
  --  b) continue with original CUG information (when neither CUG Interlock Code or Outgoin
  --     Access Indicator are present), i.e. no IN impact. 
  --  c) continue with the original non-CUG call. 
  holdTreatmentIndicator        [50] OCTET STRING (SIZE(1))       OPTIONAL, 
  -- applicable to InitialDP, Connect and ContinueWithArgument 
  -- acceptHoldRequest  'xxxx xx01'B 
  -- rejectHoldRequest  'xxxx xx10'B 
  -- if absent from Connect or ContinueWithArgument, 
  -- then CAMEL service does not affect call hold treatment 
  cwTreatmentIndicator        [51] OCTET STRING (SIZE(1))       OPTIONAL, 
  -- applicable to InitialDP, Connect and ContinueWithArgument 
  -- acceptCw 'xxxx xx01'B 
  -- rejectCw 'xxxx xx10'B 
  -- if absent from Connect or ContinueWithArgument, 
  -- then CAMEL service does not affect call waiting treatment 
  ectTreatmentIndicator        [52] OCTET STRING (SIZE(1))       OPTIONAL, 
  -- applicable to InitialDP, Connect and ContinueWithArgument 
  -- acceptEctRequest 'xxxx xx01'B 
  -- rejectEctRequest 'xxxx xx10'B 
  -- if absent from Connect or ContinueWithArgument, 
  -- then CAMEL service does not affect explicit call transfer treatment 
  ... 
  } 
 */
public class ServiceInteractionIndTwoCapV3 {

	private ForwardServiceInteractionInd forwardSerInd;
	private BackwardServiceInteractionInd backwardSerInd;
	private BothwayThroughConnectionInd bothwayInd;
	private ConnectedNumberTreatmentInd connInd;
	private HoldTreatmentIndCapV3Enum holdtreatIndEnum;
	private CwTreatmentIndCapV3Enum cwTreatIndEnum;
	private EctTreatmentIndCapV3Enum ectTreatIndEnum;
	
	private static Logger logger = Logger.getLogger(ServiceInteractionIndTwoCapV3.class); 
	
	
	/**
	 * 
	 * @return ForwardServiceInteractionInd object
	 */
	public ForwardServiceInteractionInd getForwardSerInd() {
		return forwardSerInd;
	}
    
	/**
	 * set ForwardServiceInteractionInd object
	 * @param forwardSerInd
	 */
	public void setForwardSerInd(ForwardServiceInteractionInd forwardSerInd) {
		this.forwardSerInd = forwardSerInd;
	}

	/**
	 * 
	 * @return BackwardServiceInteractionInd object
	 */
	public BackwardServiceInteractionInd getBackwardSerInd() {
		return backwardSerInd;
	}

	/**
	 * set BackwardServiceInteractionInd object
	 * @param backwardSerInd
	 */
	public void setBackwardSerInd(BackwardServiceInteractionInd backwardSerInd) {
		this.backwardSerInd = backwardSerInd;
	}

	/**
	 * 
	 * @return BothwayThroughConnectionInd Object
	 */
	public BothwayThroughConnectionInd getBothwayInd() {
		return bothwayInd;
	}

	/**
	 * set BothwayThroughConnectionInd Object
	 * @param bothwayInd
	 */
	public void setBothwayInd(BothwayThroughConnectionInd bothwayInd) {
		this.bothwayInd = bothwayInd;
	}

	/**
	 * 
	 * @return ConnectedNumberTreatmentInd object
	 */
	public ConnectedNumberTreatmentInd getConnInd() {
		return connInd;
	}

	/**
	 * set ConnectedNumberTreatmentInd object
	 * @param connInd
	 */
	public void setConnInd(ConnectedNumberTreatmentInd connInd) {
		this.connInd = connInd;
	}

	/**
	 * 
	 * @return HoldTreatmentIndCapV3Enum
	 */
	public HoldTreatmentIndCapV3Enum getHoldtreatIndEnum() {
		return holdtreatIndEnum;
	}

	/**
	 * set HoldTreatmentIndCapV3Enum
	 * @param holdtreatIndEnum
	 */
	public void setHoldtreatIndEnum(HoldTreatmentIndCapV3Enum holdtreatIndEnum) {
		this.holdtreatIndEnum = holdtreatIndEnum;
	}

	/**
	 * 
	 * @return CwTreatmentIndCapV3Enum
	 */
	public CwTreatmentIndCapV3Enum getCwTreatIndEnum() {
		return cwTreatIndEnum;
	}

	/**
	 * set CwTreatmentIndCapV3Enum
	 * @param cwTreatIndEnum
	 */
	public void setCwTreatIndEnum(CwTreatmentIndCapV3Enum cwTreatIndEnum) {
		this.cwTreatIndEnum = cwTreatIndEnum;
	}

	/**
	 * 
	 * @return EctTreatmentIndCapV3Enum
	 */
	public EctTreatmentIndCapV3Enum getEctTreatIndEnum() {
		return ectTreatIndEnum;
	}

	/**
	 * set EctTreatmentIndCapV3Enum
	 * @param ectTreatIndEnum
	 */
	public void setEctTreatIndEnum(EctTreatmentIndCapV3Enum ectTreatIndEnum) {
		this.ectTreatIndEnum = ectTreatIndEnum;
	}

	
	/**
	 * decode ServiceInteractionIndicatorsTwo non asn parameters(HoldTreatmentInd,CwTreatmentInd,
	 * EctTreatmentInd) and set into ServiceInteractionIndTwoCapV3
	 * @param ServiceInteractionIndicatorsTwo object
	 * @return ServiceInteractionIndTwoCapV3 object
	 */
	private static ServiceInteractionIndTwoCapV3 decode(ServiceInteractionIndicatorsTwo siiTwo) throws InvalidInputException{
		ServiceInteractionIndTwoCapV3 serIntIndTwo = null;
			serIntIndTwo = new ServiceInteractionIndTwoCapV3();
			serIntIndTwo.forwardSerInd = siiTwo.getForwardServiceInteractionInd();
			serIntIndTwo.backwardSerInd = siiTwo.getBackwardServiceInteractionInd();
			serIntIndTwo.bothwayInd = siiTwo.getBothwayThroughConnectionInd();
			serIntIndTwo.connInd = siiTwo.getConnectedNumberTreatmentInd();
			
			//non-asn decoding
			serIntIndTwo.holdtreatIndEnum = HoldTreatmentIndCapV3Enum.getValue(siiTwo.getHoldTreatmentIndicator()[0]);
			serIntIndTwo.cwTreatIndEnum = CwTreatmentIndCapV3Enum.getValue(siiTwo.getCwTreatmentIndicator()[0]);		
		    serIntIndTwo.ectTreatIndEnum =  EctTreatmentIndCapV3Enum.getValue(siiTwo.getEctTreatmentIndicator()[0]);
		    if(logger.isDebugEnabled()){
		    	logger.debug("ServiceInteractionIndTwoCap decoded successfuly");
		    }
		return serIntIndTwo;
			
	}
	
	/**
	 * This encode method encode HoldTreatmentInd, CwTreatmentInd, EctTreatmentInd and set all parameters 
	 * of  ServiceInteractionIndTwoCapV3 in to new object of ServiceInteractionIndicatorsTwo.
	 * @param ServiceInteractionIndTwoCapV3 Object
	 * @return new object of ServiceInteractionIndicatorsTwo with all encoded values
	 */
	private static ServiceInteractionIndicatorsTwo encode(ServiceInteractionIndTwoCapV3 serIntIndTwo) throws InvalidInputException{
		return encode(serIntIndTwo,null);
	}
	
	/**
	 * This encode method encode HoldTreatmentInd, CwTreatmentInd, EctTreatmentInd and set only these three 
	 * parameters in to provided ServiceInteractionIndicatorsTwo object its not create new object of ServiceInteractionIndicatorsTwo
	 * in case siiTwo param not null. If siiTwo param null, set all parameters 
	 * of  ServiceInteractionIndTwoCapV3 in to new object of ServiceInteractionIndicatorsTwo.
	 * @param ServiceInteractionIndTwoCapV3 serIntIndTwo
	 * @param ServiceInteractionIndicatorsTwo siiTwo
	 * @return ServiceInteractionIndicatorsTwo
	 */
	private static ServiceInteractionIndicatorsTwo encode(ServiceInteractionIndTwoCapV3 serIntIndTwo,ServiceInteractionIndicatorsTwo siiTwo) throws InvalidInputException{
		ServiceInteractionIndicatorsTwo siIndTwo = null;
			if(siiTwo!=null){
				siIndTwo= siiTwo;
			}else{
				siIndTwo = new ServiceInteractionIndicatorsTwo();
				siIndTwo.setForwardServiceInteractionInd(serIntIndTwo.forwardSerInd);
				siIndTwo.setBackwardServiceInteractionInd(serIntIndTwo.backwardSerInd);
				siIndTwo.setBothwayThroughConnectionInd(serIntIndTwo.bothwayInd);
				siIndTwo.setConnectedNumberTreatmentInd(serIntIndTwo.connInd);
			}
			//non-asn encoding
			byte[] holdTreatInd = new byte[]{(byte)serIntIndTwo.holdtreatIndEnum.getCode()};
			byte[] cwTreatInd = new byte[]{(byte) serIntIndTwo.cwTreatIndEnum.getCode()};
			byte[] ectTreatInd = new byte[]{(byte)serIntIndTwo.ectTreatIndEnum.getCode()};
			
			siIndTwo.setHoldTreatmentIndicator(holdTreatInd);
			siIndTwo.setCwTreatmentIndicator(cwTreatInd);
			siIndTwo.setEctTreatmentIndicator(ectTreatInd);
			
			if(logger.isDebugEnabled()){
				logger.debug("ServiceInteractionIndicatorsTwo encoded successfuly");
			}
		
		return siIndTwo;	
	}
	
/*	public static void main(String[] args) {
		ServiceInteractionIndicatorsTwo si = new ServiceInteractionIndicatorsTwo();
		si.setHoldTreatmentIndicator(CapV2Functions.hexStringToByteArray("01"));
		si.setCwTreatmentIndicator(CapV2Functions.hexStringToByteArray("02"));
		si.setEctTreatmentIndicator(CapV2Functions.hexStringToByteArray("01"));
		
		ServiceInteractionIndTwoCapV3 siCap = ServiceInteractionIndTwoCapV3.decode(si);
		ServiceInteractionIndicatorsTwo si2 = ServiceInteractionIndTwoCapV3.encode(siCap);
		
		System.out.println(Arrays.equals(si.getHoldTreatmentIndicator(), si2.getHoldTreatmentIndicator()));
		System.out.println(Arrays.equals(si.getCwTreatmentIndicator(), si2.getCwTreatmentIndicator()));
		System.out.println(Arrays.equals(si.getEctTreatmentIndicator(), si2.getEctTreatmentIndicator()));
	}*/
}
