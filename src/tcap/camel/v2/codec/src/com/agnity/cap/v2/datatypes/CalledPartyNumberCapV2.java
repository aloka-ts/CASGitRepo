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
package com.agnity.cap.v2.datatypes;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.cap.v2.datatypes.enumType.AddressPresentationRestricatedIndicatorCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.INNIndicatorCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.NIIndicatorCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.NatureOfAddressIndicatorCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.NumberingOfPlanIndicatorCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.OddEvenIndicatorCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.ScreeningIndicatorCapV2Enum;
import com.agnity.cap.v2.exceptions.InvalidInputException;
import com.agnity.cap.v2.util.CapFunctions;
/**
 * ref - T-REC-Q.763-3.9
 * @author rnarayan
 * CalledPartyNumber non-asn decoding encoding
 */
public class CalledPartyNumberCapV2{

	private OddEvenIndicatorCapV2Enum oddEvenIndicator;
	private NatureOfAddressIndicatorCapV2Enum natureOfAddressIndicator;
	private INNIndicatorCapV2Enum innIndicator;
	private NumberingOfPlanIndicatorCapV2Enum numberingPlanIndicator;
	private String calledPartyNumber;
	private static Logger logger = Logger.getLogger(CalledPartyNumberCapV2.class);
	

	/**
	 * (odd=1 , even=0)
	 * @return OddEvenIndicatorCapV2Enum 
	 */
	public OddEvenIndicatorCapV2Enum getOddEvenIndicator() {
		return oddEvenIndicator;
	}


    /**
     * 
     * @return NatureOfAddressIndicatorCapV2Enum
     */
	public NatureOfAddressIndicatorCapV2Enum getNatureOfAddressIndicator() {
		return natureOfAddressIndicator;
	}


    /**
     * 
     * @return INNIndicatorCapV2Enum
     */
	public INNIndicatorCapV2Enum getInnIndicator() {
		return innIndicator;
	}


    /**
     * 
     * @return NumberingOfPlanIndicatorCapV2Enum
     */
	public NumberingOfPlanIndicatorCapV2Enum getNumberingPlanIndicator() {
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
	public void setOddEvenIndicator(OddEvenIndicatorCapV2Enum oddEvenIndicator) {
		this.oddEvenIndicator = oddEvenIndicator;
	}


    /**
     * 
     * @param natureOfAddressIndicator
     */
	public void setNatureOfAddressIndicator(
			NatureOfAddressIndicatorCapV2Enum natureOfAddressIndicator) {
		this.natureOfAddressIndicator = natureOfAddressIndicator;
	}


    /**
     * 
     * @param innIndicator
     */
	public void setInnIndicator(INNIndicatorCapV2Enum innIndicator) {
		this.innIndicator = innIndicator;
	}


    /**
     * 
     * @param numberingPlanIndicator
     */
	public void setNumberingPlanIndicator(
			NumberingOfPlanIndicatorCapV2Enum numberingPlanIndicator) {
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
	public static CalledPartyNumberCapV2 decode(byte[] bytes) throws InvalidInputException {
		CalledPartyNumberCapV2 cpn = null;
			cpn = new CalledPartyNumberCapV2();
			cpn.oddEvenIndicator = OddEvenIndicatorCapV2Enum.getValue((byte) (bytes[0]>>7)&0x01);
			cpn.natureOfAddressIndicator = NatureOfAddressIndicatorCapV2Enum.getValue((byte) (bytes[0]&0x7F));
			cpn.innIndicator = INNIndicatorCapV2Enum.getValue((byte) (bytes[1]>>7)&0x01);
			cpn.numberingPlanIndicator = NumberingOfPlanIndicatorCapV2Enum.getValue((byte)((bytes[1]>>4)&0x7));
			cpn.calledPartyNumber = AddressSignalCapV2.decode(bytes,2);
			
			if(logger.isDebugEnabled()){
				logger.debug("CalledPartyNumber decoded succesfully");
			}
		return cpn;
	}
	
	
	/**
	 * Return encoded byte array for Called Party number 
	 * @param  CalledPartyNumberCapV2 object with non-asn parameters
	 * @return encoded byte array
	 */
	public static byte[] encode(CalledPartyNumberCapV2 cpn) throws InvalidInputException{
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
			byte[] calledPartyNumBytes  = AddressSignalCapV2.encode(calledPartyNumber);
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
