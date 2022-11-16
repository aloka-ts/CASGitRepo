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
package com.agnity.map.datatypes;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.AddressPresentationRestricatedIndicatorMapEnum;
import com.agnity.map.enumdata.INNIndicatorMapEnum;
import com.agnity.map.enumdata.NatureOfAddressIndicatorMapEnum;
import com.agnity.map.enumdata.NumberingOfPlanIndicatorMapEnum;
import com.agnity.map.enumdata.OddEvenIndicatorMapEnum;
import com.agnity.map.enumdata.ScreeningIndicatorMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.MapFunctions;
import com.agnity.map.util.Util;

/**
 *  ref - T-REC-Q.763-3.30
 * LocationNumber non-asn decoding & encoding 
 */
public class LocationNumberMap{

	private OddEvenIndicatorMapEnum oddEvenIndicator;
	private NatureOfAddressIndicatorMapEnum natureOfAddressIndicator;
	private INNIndicatorMapEnum innIndicator;
	private NumberingOfPlanIndicatorMapEnum numberingPlanIndicator;
	private AddressPresentationRestricatedIndicatorMapEnum addPreResIndicator;
	private ScreeningIndicatorMapEnum screeningIndicator;
	private String locationNumber;	
	private static Logger logger = Logger.getLogger(LocationNumberMap.class);
	
	
	/**
	 * 
	 * @return OddEvenIndicatorMapEnum
	 */
	public OddEvenIndicatorMapEnum getOddEvenIndicator() {
		return oddEvenIndicator;
	}


    /**
     *  
     * @return NatureOfAddressIndicatorMapEnum
     */
	public NatureOfAddressIndicatorMapEnum getNatureOfAddressIndicator() {
		return natureOfAddressIndicator;
	}


    /**
     * 
     * @return INNIndicatorMapEnum
     */
	public INNIndicatorMapEnum getInnIndicator() {
		return innIndicator;
	}


    /**
     * 
     * @return NumberingOfPlanIndicatorMapEnum
     */
	public NumberingOfPlanIndicatorMapEnum getNumberingPlanIndicator() {
		return numberingPlanIndicator;
	}


    /**
     * 
     * @return AddressPresentationRestricatedIndicatorMapEnum
     */
	public AddressPresentationRestricatedIndicatorMapEnum getAddPreResIndicator() {
		return addPreResIndicator;
	}


    /**
     * 
     * @return ScreeningIndicatorMapEnum
     */
	public ScreeningIndicatorMapEnum getScreeningIndicator() {
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
	public void setOddEvenIndicator(OddEvenIndicatorMapEnum oddEvenIndicator) {
		this.oddEvenIndicator = oddEvenIndicator;
	}

    /**
     * 
     * @param natureOfAddressIndicator
     */
	public void setNatureOfAddressIndicator(
			NatureOfAddressIndicatorMapEnum natureOfAddressIndicator) {
		this.natureOfAddressIndicator = natureOfAddressIndicator;
	}

    /**
     * 
     * @param innIndicator
     */
	public void setInnIndicator(INNIndicatorMapEnum innIndicator) {
		this.innIndicator = innIndicator;
	}

    /**
     * 
     * @param numberingPlanIndicator
     */
	public void setNumberingPlanIndicator(
			NumberingOfPlanIndicatorMapEnum numberingPlanIndicator) {
		this.numberingPlanIndicator = numberingPlanIndicator;
	}

    /**
     * 
     * @param addPreResIndicator
     */
	public void setAddPreResIndicator(
			AddressPresentationRestricatedIndicatorMapEnum addPreResIndicator) {
		this.addPreResIndicator = addPreResIndicator;
	}


    /**
     * 
     * @param screeningIndicator
     */
	public void setScreeningIndicator(ScreeningIndicatorMapEnum screeningIndicator) {
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
     * decode LocationNumber byte array into non-asn LocationNumberMap object 
     * @param byte array of LocationNumber
     * @return LocationNumberMap object 
     */
	public static LocationNumberMap decode(byte[] bytes)  throws InvalidInputException {
		if(logger.isDebugEnabled()){
			logger.debug("decoding = "+Util.formatBytes(bytes));
			System.out.println("decoding = "+Util.formatBytes(bytes));
		}
		LocationNumberMap locNum = null;
			locNum = new LocationNumberMap();
			locNum.oddEvenIndicator = OddEvenIndicatorMapEnum.getValue((bytes[0]&0x80)>>7);
			locNum.natureOfAddressIndicator = NatureOfAddressIndicatorMapEnum.getValue((bytes[0]&0x7F));
			locNum.innIndicator = INNIndicatorMapEnum.getValue((bytes[1]>>7));
			locNum.numberingPlanIndicator = NumberingOfPlanIndicatorMapEnum.getValue(((bytes[1]&0x7F)>>4));
			locNum.addPreResIndicator = AddressPresentationRestricatedIndicatorMapEnum.getValue((byte) ((bytes[1]&0x0F)>>2));
			locNum.screeningIndicator = ScreeningIndicatorMapEnum.getValue((byte) ((bytes[1]&0x03)));
			locNum.locationNumber = MapFunctions.decodeNumber(bytes,2);
			
			if(logger.isDebugEnabled()){
				logger.debug("LocationNumber non-asn decoded successfully.");
			}
			
		return locNum;
	}
	
	/**
	 * encode LocationNumberMap object into byte array of locationNumber
	 * @param LocationNumberMap object with non-asn values
	 * @return byte array of locationNumber
	 */
	public static byte[] encode(LocationNumberMap locNumMap)  throws InvalidInputException{
		 byte[] bytes = null;
			int oddEvenInd = locNumMap.oddEvenIndicator.getCode();
	        int natOfAddInd = locNumMap.natureOfAddressIndicator.getCode();
	        int innInd = locNumMap.innIndicator.getCode();
	        int numOfPlanInd = locNumMap.numberingPlanIndicator.getCode();
	        int addPreResInd = locNumMap.addPreResIndicator.getCode();
	        int scrInd = locNumMap.screeningIndicator.getCode();
	        String locNum = locNumMap.getLocationNumber();
	        
	        byte b0 = (byte)((oddEvenInd<<7)+natOfAddInd);
	        byte b1 = (byte)((((((innInd<<3)+numOfPlanInd)<<2)+addPreResInd)<<2)+scrInd); 
	        
	        locNum = MapFunctions.encodenNumber(locNum);
	        byte[] locNumBytes = MapFunctions.hexStringToByteArray(locNum);
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


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LocationNumberMap [oddEvenIndicator=" + oddEvenIndicator
				+ ", natureOfAddressIndicator=" + natureOfAddressIndicator
				+ ", innIndicator=" + innIndicator
				+ ", numberingPlanIndicator=" + numberingPlanIndicator
				+ ", addPreResIndicator=" + addPreResIndicator
				+ ", screeningIndicator=" + screeningIndicator
				+ ", locationNumber=" + locationNumber + "]";
	}
	

}
