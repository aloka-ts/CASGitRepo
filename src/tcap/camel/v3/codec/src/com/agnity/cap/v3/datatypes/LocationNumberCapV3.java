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
import com.agnity.cap.v3.datatypes.enumType.NatureOfAddressIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.NumberingOfPlanIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.OddEvenIndicatorCapV3Enum;
import com.agnity.cap.v3.datatypes.enumType.ScreeningIndicatorCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;
/**
 *  ref - T-REC-Q.763-3.30
 * @author rnarayan
 * LocationNumber non-asn decoding & encoding 
 */
public class LocationNumberCapV3{

	private OddEvenIndicatorCapV3Enum oddEvenIndicator;
	private NatureOfAddressIndicatorCapV3Enum natureOfAddressIndicator;
	private INNIndicatorCapV3Enum innIndicator;
	private NumberingOfPlanIndicatorCapV3Enum numberingPlanIndicator;
	private AddressPresentationRestricatedIndicatorCapV2Enum addPreResIndicator;
	private ScreeningIndicatorCapV3Enum screeningIndicator;
	private String locationNumber;	
	private static Logger logger = Logger.getLogger(LocationNumberCapV3.class);
	
	
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
     * @return AddressPresentationRestricatedIndicatorCapV2Enum
     */
	public AddressPresentationRestricatedIndicatorCapV2Enum getAddPreResIndicator() {
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
    * @return decode hex string of location number
    */
	public String getLocationNumber() {
		return locationNumber;
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
     * @param locationNumber
     */
	public void setLocationNumber(String locationNumber) {
		this.locationNumber = locationNumber;
	}


    /**
     * decode LocationNumber byte array into non-asn LocationNumberCapV2 object 
     * @param byte array of LocationNumber
     * @return LocationNumberCapV2 object 
     */
	public static LocationNumberCapV3 decode(byte[] bytes)  throws InvalidInputException {
		LocationNumberCapV3 locNum = null;
			locNum = new LocationNumberCapV3();
			locNum.oddEvenIndicator = OddEvenIndicatorCapV3Enum.getValue((byte) (bytes[0]>>7));
			locNum.natureOfAddressIndicator = NatureOfAddressIndicatorCapV3Enum.getValue((byte) (bytes[0]&0x7F));
			locNum.innIndicator = INNIndicatorCapV3Enum.getValue((byte) (bytes[1]>>7));
			locNum.numberingPlanIndicator = NumberingOfPlanIndicatorCapV3Enum.getValue((byte) ((bytes[1]&0x7F)>>4));
			locNum.addPreResIndicator = AddressPresentationRestricatedIndicatorCapV2Enum.getValue((byte) ((bytes[1]&0x0F)>>2));
			locNum.screeningIndicator = ScreeningIndicatorCapV3Enum.getValue((byte) ((bytes[1]&0x03)));
			locNum.locationNumber = CapFunctions.decodeNumber(bytes,2);
			
			if(logger.isDebugEnabled()){
				logger.debug("LocationNumber non-asn decoded successfully.");
			}
			
		return locNum;
	}
	
	/**
	 * encode LocationNumberCapV2 object into byte array of locationNumber
	 * @param LocationNumberCapV3 object with non-asn values
	 * @return byte array of locationNumber
	 */
	public static byte[] encode(LocationNumberCapV3 locNumCap)  throws InvalidInputException{
		 byte[] bytes = null;
			int oddEvenInd = locNumCap.oddEvenIndicator.getCode();
	        int natOfAddInd = locNumCap.natureOfAddressIndicator.getCode();
	        int innInd = locNumCap.innIndicator.getCode();
	        int numOfPlanInd = locNumCap.numberingPlanIndicator.getCode();
	        int addPreResInd = locNumCap.addPreResIndicator.getCode();
	        int scrInd = locNumCap.screeningIndicator.getCode();
	        String locNum = locNumCap.getLocationNumber();
	        
	        byte b0 = (byte)((oddEvenInd<<7)+natOfAddInd);
	        byte b1 = (byte)((((((innInd<<3)+numOfPlanInd)<<2)+addPreResInd)<<2)+scrInd); 
	        
	        locNum = CapFunctions.encodenNumber(locNum);
	        byte[] locNumBytes = CapFunctions.hexStringToByteArray(locNum);
		    int totalByteLength = locNumBytes.length+2;
		    bytes = new byte[totalByteLength];
		    bytes[0]=b0;
		    bytes[1]=b1;
		    for(int i=0;i<(totalByteLength-2);i++){
		    	bytes[i+2] = locNumBytes[i];
		    }
		    
		    if(logger.isDebugEnabled()){
		    	logger.debug("locationNumber byteArray encoded successfully.");
		    	logger.debug("locationNumber byteArray encoded length::"+bytes.length);
		    }
			
		return bytes;
		
	}
	
	@Override
	public String toString() {
		String bl = "\n";
		return "OddEvenIndicatorodd "+this.oddEvenIndicator+bl+
		       "NatureOfAddressIndicator "+this.natureOfAddressIndicator+bl+
		       "INNIndicator "+ this.innIndicator+bl+
		       "NumberingOfPlanIndicator "+this.numberingPlanIndicator+bl+
		       "AddressPresentationRestricatedIndicator "+this.addPreResIndicator+bl+
		       "ScreeningIndicator "+this.screeningIndicator+bl+
		       "locationNumber "+this.locationNumber;
	}
	
	/*public static void main(String[] args) {
		LocationNumberCapV2 loc = new LocationNumberCapV2();
		loc.setOddEvenIndicator(OddEvenIndicatorCapV2Enum.EVEN);
		loc.setAddPreResIndicator(AddressPresentationRestricatedIndicatorCapV2Enum.PRESENTATION_ALLOWED);
		loc.setInnIndicator(INNIndicatorCapV2Enum.INN_ALLOWED);
		loc.setNumberingPlanIndicator(NumberingOfPlanIndicatorCapV2Enum.NUMBERING_PLAN_ISDN);
		loc.setNatureOfAddressIndicator(NatureOfAddressIndicatorCapV2Enum.INTERNATIONAL);
		loc.setScreeningIndicator(ScreeningIndicatorCapV2Enum.RESERVED_0);
		loc.setLocationNumber("0123456789");
		byte[] bytes = encode(loc);
		byte[] expectedBytes = CapV2Functions.hexStringToByteArray("04101032547698");
		System.out.println(Arrays.equals(bytes, expectedBytes));
		loc = LocationNumberCapV2.decode(expectedBytes);
		System.out.println(loc.toString());
	}*/

}
