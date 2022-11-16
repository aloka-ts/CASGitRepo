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

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.cap.v3.datatypes.enumType.AddressPresentationRestricatedIndicatorCapV2Enum;
import com.agnity.cap.v3.datatypes.enumType.NIIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NatureOfAddressIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NumberingOfPlanIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.OddEvenIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.ScreeningIndicatorCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;
/**
 * ref - T-REC-Q.763-3.10
 * @author rnarayan
 * CallingPartyNumber non-asn decoding & encoding
 */
public class CallingPartyNumberCapV3{

	
	private OddEvenIndicatorCapV3Enum oddEvenIndicator;
	private NatureOfAddressIndicatorCapV3Enum natureOfAddressIndicator;
	private NIIndicatorCapV3Enum niIndicator;
	private NumberingOfPlanIndicatorCapV3Enum numberingPlanIndicator;
	private AddressPresentationRestricatedIndicatorCapV2Enum addPreResIndicator;
	private ScreeningIndicatorCapV3Enum screeningIndicator;
	private String callingPartyNumber;	
	private static Logger logger = Logger.getLogger(CallingPartyNumberCapV3.class);
	
	
	/**
	 * 
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
     * @return NIIndicatorCapV2Enum
     */
	public NIIndicatorCapV3Enum getNiIndicator() {
		return niIndicator;
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
     * @return AddressPresentationRestricatedIndicatorCapV2Enum
     */
	public AddressPresentationRestricatedIndicatorCapV2Enum getAddressPresentationRestricatedIndicator() {
		return addPreResIndicator;
	}

    /**
     * 
     * @return ScreeningIndicatorCapV2Enum
     */
	public ScreeningIndicatorCapV3Enum getScreeningIndicator() {
		return screeningIndicator;
	}

    /**
     * 
     * @return decoded hex string of calling party number
     */
	public String getCallingPartyNumber() {
		return callingPartyNumber;
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
	public void setNatureOfAddressIndicator(NatureOfAddressIndicatorCapV3Enum natureOfAddressIndicator) {
		this.natureOfAddressIndicator = natureOfAddressIndicator;
	}

    /**
     * 
     * @param niIndicator
     */
	public void setNiIndicator(NIIndicatorCapV3Enum niIndicator) {
		this.niIndicator = niIndicator;
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
     * @param addPreResIndicator
     */
	public void setAddPreResIndicator(
			AddressPresentationRestricatedIndicatorCapV2Enum addPreResIndicator) {
		this.addPreResIndicator = addPreResIndicator;
	}

    /**
     * 
     * @param screeningIndicator
     */
	public void setScreeningIndicator(ScreeningIndicatorCapV3Enum screeningIndicator) {
		this.screeningIndicator = screeningIndicator;
	}

    /**
     * 
     * @param hex string of callingPartyNumber
     */
	public void setCallingPartyNumber(String callingPartyNumber) {
		this.callingPartyNumber = callingPartyNumber;
	}

    /**
     * Decode calling party number byte array into CallingPartyNumberCapV2
     * object with non-asn parameters. 
     * @param byte array of CallingPartyNumber
     * @return CallingPartyNumberCapV2 object
     */
	public static CallingPartyNumberCapV3 decode(byte[] bytes)  throws InvalidInputException {
		CallingPartyNumberCapV3 cpn = null;
			cpn = new CallingPartyNumberCapV3();
			cpn.oddEvenIndicator = OddEvenIndicatorCapV3Enum.getValue((byte)(bytes[0]>>7)&0x01);
			cpn.natureOfAddressIndicator = NatureOfAddressIndicatorCapV3Enum.getValue((byte)(bytes[0]&0x7F));
			cpn.niIndicator = NIIndicatorCapV3Enum.getValue((byte)((bytes[1]>>7)&0x01));
			cpn.numberingPlanIndicator = NumberingOfPlanIndicatorCapV3Enum.getValue((byte) (((bytes[1]&0x7F)>>4)&0x0f));
			cpn.addPreResIndicator = AddressPresentationRestricatedIndicatorCapV2Enum.getValue((byte) (((bytes[1]&0x0F)>>2)&0x11));
			cpn.screeningIndicator = ScreeningIndicatorCapV3Enum.getValue((byte) ((bytes[1]&0x03)));
			cpn.callingPartyNumber = AddressSignalCapV3.decode(bytes,2);
			
			if(logger.isDebugEnabled()){
				logger.debug("CallingPartyNumber non-asn decoded successfully.");
			}
			
		return cpn;
	}
	
	/**
	 * 
	 * @param byte array of CallingPartyNumber
	 * @return decode hex string of calling party number
	 */
	//now Address Signal have decode method 
	/*private String decodeCallingNumber(byte[] bytes){
		 StringBuilder number = new StringBuilder();
		    int length = bytes.length;
			for(int i=2;i<length;i++){
			    number.append(Integer.toHexString((bytes[i]&0x0f)));
			    if((((bytes[i]>>4)&0x0F)==0) && (i==(length-1))){
				     // log here for break 0	
				      break;	
				    }
			 	number.append((Integer.toHexString((bytes[i]>>4)&0x0F)));
			}
			return number.toString();
	}*/
	
	/**
	 * encode CallingPartyNumberCapV2 non-asn parameters into CallingPartyNumber 
	 * byte array 
	 * @param CallingPartyNumberCapV3 Object with non-asn parameters
	 * @return byte array of CallingPartyNumber
	 */
	public static byte[] encode(CallingPartyNumberCapV3 cpn )  throws InvalidInputException{
		 byte[] bytes = null;

			int oddEvenInd = cpn.oddEvenIndicator.getCode();
			int natureOfAddInd = cpn.natureOfAddressIndicator.getCode();
			int niInd = cpn.niIndicator.getCode();
			int numPlanInd = cpn.numberingPlanIndicator.getCode();
			int addPreResInd = cpn.addPreResIndicator.getCode();
			int screeningInd = cpn.screeningIndicator.getCode();
			String callingPartyNumber = cpn.callingPartyNumber;
			
			byte b0 = (byte)((oddEvenInd<<7)+natureOfAddInd);
		    byte b1 = (byte)((((((niInd<<3)+numPlanInd)<<2)+addPreResInd)<<2)
		    		           +screeningInd);
		   // ((((0<<3)+1)<<2+0)<<2)+3;
		    //In case of an odd number of address signals, the filler code 0000 is 
			//inserted after the last address signal. 
			/*if(callingPartyNumber.length()%2!=0){
				callingPartyNumber = callingPartyNumber+"0";
			}*/
		    //callingPartyNumber = CapV2Functions.encodenNumber(callingPartyNumber);
		   // byte[] callingPartyNumBytes = CapV2Functions.hexStringToByteArray(callingPartyNumber);
		    byte[] callingPartyNumBytes = AddressSignalCapV3.encode(callingPartyNumber);
		    int totalByteLength = callingPartyNumBytes.length+2;
		    bytes = new byte[totalByteLength];
		    bytes[0]=b0;
		    bytes[1]=b1;
		    for(int i=0;i<(totalByteLength-2);i++){
		    	bytes[i+2] = callingPartyNumBytes[i];
		    }
		    
		    if(logger.isDebugEnabled()){
		    	logger.debug("CallingPartyNumber byte array encoded successfully");
		    	logger.debug("CallingPartyNumber encoded byte array length::"+bytes.length);
		    }
			
		
		return bytes;
	}
	
	@Override
	public String toString() {
		String bl = "\n";
		return "OddEvenIndicator "+this.oddEvenIndicator+bl+
		       "NatureOfAddressIndicator "+this.natureOfAddressIndicator+bl+
		       "NIIndicator "+this.niIndicator+bl+
		       "NumberingOfPlanIndicator "+this.numberingPlanIndicator+bl+
		       "AddressPresentationRestricatedIndicator "+this.addPreResIndicator+bl+
		       "ScreeningIndicator "+this.screeningIndicator+bl+
		       "callingPartyNumber "+this.callingPartyNumber;
	}
	
	
/*	public static void main(String[] args) {
		CallingPartyNumberCapV2 cpn = new CallingPartyNumberCapV2();
		cpn.setOddEvenIndicator(OddEvenIndicatorCapV2Enum.EVEN);
		cpn.setNiIndicator(NIIndicatorCapV2Enum.COMPLETE);
		cpn.setNatureOfAddressIndicator(NatureOfAddressIndicatorCapV2Enum.INTERNATIONAL);
		cpn.setNumberingPlanIndicator(NumberingOfPlanIndicatorCapV2Enum.NUMBERING_PLAN_ISDN);
		cpn.setAddPreResIndicator(AddressPresentationRestricatedIndicatorCapV2Enum.PRESENTATION_ALLOWED);
		cpn.setScreeningIndicator(ScreeningIndicatorCapV2Enum.RESERVED_0);
		cpn.setCallingPartyNumber("9199991114441");
		byte[] bytes =encode(cpn);
		System.out.println("0410199999114144");
		
		byte[] expectedBytes = CapV2Functions.hexStringToByteArray("041019999911414401");
		boolean b = Arrays.equals(bytes,expectedBytes );
		System.out.println("result::"+ b);
		cpn = CallingPartyNumberCapV2.decode(expectedBytes);
		System.out.println(cpn.toString());
	}*/
}
