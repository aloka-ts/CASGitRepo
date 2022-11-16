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

import com.agnity.cap.v3.datatypes.enumType.AddressPresentationRestricatedIndicatorCapV2Enum;
import com.agnity.cap.v3.datatypes.enumType.INNIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NIIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NatureOfAddressIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NumberingOfPlanIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.OddEvenIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.ScreeningIndicatorCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;
/**
 * ref - T-REC-Q.763-3.9
 * @author rnarayan
 * CalledPartyNumber non-asn decoding encoding
 */
public class CalledPartyNumberCapV3{

	private OddEvenIndicatorCapV3Enum oddEvenIndicator;
	private NatureOfAddressIndicatorCapV3Enum natureOfAddressIndicator;
	private INNIndicatorCapV3Enum innIndicator;
	private NumberingOfPlanIndicatorCapV3Enum numberingPlanIndicator;
	private String calledPartyNumber;
	private static Logger logger = Logger.getLogger(CalledPartyNumberCapV3.class);
	

	/**
	 * (odd=1 , even=0)
	 * @return OddEvenIndicatorCapV2Enum 
	 */
	public OddEvenIndicatorCapV3Enum getOddEvenIndicator() {
		return oddEvenIndicator;
	}


    /**
     * 
     * @return NatureOfAddressIndicatorCapV2Enum
     */
	public NatureOfAddressIndicatorCapV3Enum getNatureOfAddressIndicator() {
		return natureOfAddressIndicator;
	}


    /**
     * 
     * @return INNIndicatorCapV2Enum
     */
	public INNIndicatorCapV3Enum getInnIndicator() {
		return innIndicator;
	}


    /**
     * 
     * @return NumberingOfPlanIndicatorCapV2Enum
     */
	public NumberingOfPlanIndicatorCapV3Enum getNumberingPlanIndicator() {
		return numberingPlanIndicator;
	}


    /**
     * 
     * @return hex String of decoded called party number
     */
	public String getCalledPartyNumber() {
		return calledPartyNumber;
	}

	

    /**
     * 
     * @param oddEvenIndicator
     */
	public void setOddEvenIndicator(OddEvenIndicatorCapV3Enum oddEvenIndicator) {
		this.oddEvenIndicator = oddEvenIndicator;
	}


    /**
     * 
     * @param natureOfAddressIndicator
     */
	public void setNatureOfAddressIndicator(
			NatureOfAddressIndicatorCapV3Enum natureOfAddressIndicator) {
		this.natureOfAddressIndicator = natureOfAddressIndicator;
	}


    /**
     * 
     * @param innIndicator
     */
	public void setInnIndicator(INNIndicatorCapV3Enum innIndicator) {
		this.innIndicator = innIndicator;
	}


    /**
     * 
     * @param numberingPlanIndicator
     */
	public void setNumberingPlanIndicator(
			NumberingOfPlanIndicatorCapV3Enum numberingPlanIndicator) {
		this.numberingPlanIndicator = numberingPlanIndicator;
	}


    /**
     * 
     * @param calledPartyNumber in hex string 
     */
	public void setCalledPartyNumber(String calledPartyNumber) {
		this.calledPartyNumber = calledPartyNumber;
	}


    /**
     * Return CalledPartyNumberCapV2 object contain all non- asn parameters 
     * @param byte array of CalledPartyNumber
     * @return CalledPartyNumberCapV2 decoded object
     */
	public static CalledPartyNumberCapV3 decode(byte[] bytes) throws InvalidInputException {
		CalledPartyNumberCapV3 cpn = null;
			cpn = new CalledPartyNumberCapV3();
			cpn.oddEvenIndicator = OddEvenIndicatorCapV3Enum.getValue((byte) (bytes[0]>>7)&0x01);
			cpn.natureOfAddressIndicator = NatureOfAddressIndicatorCapV3Enum.getValue((byte) (bytes[0]&0x7F));
			cpn.innIndicator = INNIndicatorCapV3Enum.getValue((byte) (bytes[1]>>7)&0x01);
			cpn.numberingPlanIndicator = NumberingOfPlanIndicatorCapV3Enum.getValue((byte)((bytes[1]>>4)&0x7));
			cpn.calledPartyNumber = AddressSignalCapV3.decode(bytes,2);
			
			if(logger.isDebugEnabled()){
				logger.debug("CalledPartyNumber decoded succesfully");
			}
		return cpn;
	}
	
	
	/**
	 * Return encoded byte array for Called Party number 
	 * @param  CalledPartyNumberCapV3 object with non-asn parameters
	 * @return encoded byte array
	 */
	public static byte[] encode(CalledPartyNumberCapV3 cpn) throws InvalidInputException{
		 byte[] bytes = null;
			int oddEvenInd = cpn.oddEvenIndicator.getCode();
			int natureOfAddInd = cpn.natureOfAddressIndicator.getCode();
			int innInd = cpn.innIndicator.getCode();
			int numPlanInd = cpn.numberingPlanIndicator.getCode();
			String calledPartyNumber = cpn.calledPartyNumber;
			
			byte b0 = (byte)((oddEvenInd<<7)+natureOfAddInd);
			byte b1 = (byte)(((innInd<<3)+numPlanInd)<<4);	
			
			//In case of an odd number of address signals, the filler code 0000 is 
			//inserted after the last address signal. 
			/*if(calledPartyNumber.length()%2!=0){
				calledPartyNumber = calledPartyNumber+"0";
			}*/
			//calledPartyNumber = CapV2Functions.encodenNumber(calledPartyNumber);
		  //  byte[] calledPartyNumBytes = CapV2Functions.hexStringToByteArray(calledPartyNumber);
			byte[] calledPartyNumBytes  = AddressSignalCapV3.encode(calledPartyNumber);
		    int totalByteLength = calledPartyNumBytes.length+2;
		    bytes = new byte[totalByteLength];
		    bytes[0]=b0;
		    bytes[1]=b1;
		    for(int i=0;i<(totalByteLength-2);i++){
		    	bytes[i+2] = calledPartyNumBytes[i];
		    }
		    
		    if(logger.isDebugEnabled()){
		    	logger.debug("CalledPartyNumber encoded successfully");
		    	logger.debug("CalledPartyNumber encoded byte array length::"+bytes.length);
		    }
			
		return bytes;
	}
	
	@Override
	public String toString() {
		String bl = "\n";
		return "OddEvenIndicator "+this.oddEvenIndicator+bl+
		       "NatureOfAddressIndicator "+this.natureOfAddressIndicator+bl+
		       "INNIndicator "+this.innIndicator+bl+
		       "NumberingOfPlanIndicator "+this.numberingPlanIndicator+bl+
		       "calledPartyNumber "+this.calledPartyNumber
		              ;
	}
	

/* 	public static void main(String[] args) {
 		CalledPartyNumberCapV2 cpn = new CalledPartyNumberCapV2();
 		cpn.setCalledPartyNumber("911432200075F");
 		cpn.setOddEvenIndicator(OddEvenIndicatorCapV2Enum.ODD);
 		cpn.setNatureOfAddressIndicator(NatureOfAddressIndicatorCapV2Enum.INTERNATIONAL);
 		cpn.setInnIndicator(INNIndicatorCapV2Enum.INN_ALLOWED);
 		cpn.setNumberingPlanIndicator(NumberingOfPlanIndicatorCapV2Enum.NUMBERING_PLAN_ISDN);
		byte[] bytes =encode(cpn);
		System.out.println("84101941230200570f");
		byte[] expectedBytes = CapV2Functions.hexStringToByteArray("84101941230200570f");
		boolean b = Arrays.equals(bytes,expectedBytes );
		System.out.println("result::"+ b);
		cpn = CalledPartyNumberCapV2.decode(expectedBytes);
		System.out.println(cpn.toString());
   	}*/
}
