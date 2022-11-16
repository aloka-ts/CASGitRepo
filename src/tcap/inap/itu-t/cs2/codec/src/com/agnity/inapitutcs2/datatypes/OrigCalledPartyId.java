/****
Copyright (c) 2015 Agnity, Inc. All rights reserved.


This is proprietary source code of Agnity, Inc.


Agnity, Inc. retains all intellectual property rights associated 
with this source code. Use is subject to license terms.

This source code contains trade secrets owned by Agnity, Inc.
Confidentiality of this computer program must be maintained at
 all times, unless explicitly authorized by Agnity, Inc.
 ****/
package com.agnity.inapitutcs2.datatypes;

/**
 * @author rarya
 *This class is used for encoding original called party ID.
 */
import org.apache.log4j.Logger;

import com.agnity.inapitutcs2.enumdata.AddPrsntRestEnum;
import com.agnity.inapitutcs2.enumdata.NatureOfAddEnum;
import com.agnity.inapitutcs2.enumdata.NumPlanEnum;
import com.agnity.inapitutcs2.exceptions.InvalidInputException;
import com.agnity.inapitutcs2.util.Util;

public class OrigCalledPartyId extends AddressSignal {

	private static Logger logger = Logger.getLogger(OrigCalledPartyId.class);	 

	/**
	 * @see NatureOfAddEnum
	 */
	NatureOfAddEnum natureOfAdrs ; 
	/**
	 * @see NumPlanEnum
	 */
	NumPlanEnum numPlan ;
	/**
	 * @see AddPrsntRestEnum
	 */
	AddPrsntRestEnum addPrsntRest;


	/**
	 * This function will encode Original called party Id. AddrsSignal is mandatory parameter.
	 * Other parameters will be assigned default values if not provided.
	 * NatureOfAdrsEnum: national,NumPlanEnum: ISDN,AddPrsntrest:allowed 
	 * @param addrSignal
	 * @param natureOfNumberEnum
	 * @param numberingPlanEnum
	 * @param intNtwrkNumEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeOrigCalledPartyId(String addrSignal, NatureOfAddEnum natureOfNumberEnum, NumPlanEnum numberingPlanEnum, AddPrsntRestEnum addPrsntRest) throws InvalidInputException {
		if (logger.isInfoEnabled()) {
			logger.info("encodeOrigCalledPartyId: Enter");
		}
		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(addrSignal);
		int seqLength = 2 + bcdDigits.length;

		int i = 0;
		byte[] myParms = new byte[seqLength];

		int natureOfAdrs;
		if(natureOfNumberEnum == null){
			if (logger.isInfoEnabled()) {
				logger.info("encodeOrigCalledPartyId:Assigning default value national for natureOfNumberEnum");
			}
			natureOfAdrs = 3;
		}else {
			natureOfAdrs = natureOfNumberEnum.getCode();
		}
		
		int numberingPlan ;
		if(numberingPlanEnum == null){
			if (logger.isInfoEnabled()) {
				logger.info("encodeOrigCalledPartyId: Assigning default value ISDN of numberingPlan");
			}
			numberingPlan = 1 ;
		}else {
			numberingPlan = numberingPlanEnum.getCode();
		}
		
		int addressPresentationInd ;
		if(addPrsntRest == null){
			if (logger.isInfoEnabled()) {
				logger.info("encodeOrigCalledPartyId:Assigning default value allowed to addPrsntRest");
			}
			addressPresentationInd = 0 ;
		}else {
			addressPresentationInd = addPrsntRest.getCode();
		}
		
		// If even no. then set 8th bit 0 otherwise 1
		if (addrSignal.length() % 2 == 0) {
			myParms[i++] = (byte) ((0 << 7) | natureOfAdrs);
		} else {
			myParms[i++] = (byte) ((1 << 7) | natureOfAdrs);
		}
		// Setting 2nd byte for numbering plan and Address presentation Indicator
		//myParms[i++] = (byte) ( (numberingPlan << 7) | (addressPresentationInd << 4));
		myParms[i++] = (byte) ( (numberingPlan << 4) | (addressPresentationInd << 4));

		for (int j = 0; j < bcdDigits.length; j++) {
			myParms[i++] = bcdDigits[j];
		}
		if(logger.isDebugEnabled())
			logger.debug("encodeOrigCalledPartyId::Encoded OrigCalledPartyId: "+ Util.formatBytes(myParms));
		if (logger.isInfoEnabled()) {
			logger.info("encodeOrigCalledPartyId: Exit");
		}
		return myParms;
	}

	/**
	 * This function will decode Called Party Number.
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static OrigCalledPartyId decodeOrigCalledPartyId(byte[] data) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("decodeOrigCalledPartyId: Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeOrigCalledPartyId: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeOrigCalledPartyId: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		OrigCalledPartyId origCldPtyId = new OrigCalledPartyId();
		int parity = (data[0] >> 7) & 0x1;
		int natureOfAdrs = data[0] & 0x7f ;
		origCldPtyId.natureOfAdrs = NatureOfAddEnum.fromInt(natureOfAdrs);
		
		if(data.length >= 1){
			int addPrsntInd = (data[1] >> 2) & 0x3;
			origCldPtyId.addPrsntRest = AddPrsntRestEnum.fromInt(addPrsntInd);
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			origCldPtyId.numPlan = NumPlanEnum.fromInt(numberingPlan);
		}
		if(data.length > 2){
			origCldPtyId.addrSignal = AddressSignal.decodeAdrsSignal(data, 2 , parity);			
		}	
		if(logger.isDebugEnabled())
			logger.debug("decodeOrigCalledPartyId: Output<--" + origCldPtyId.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeOrigCalledPartyId: Exit");
		}
		return origCldPtyId ;
	}

	public String toString(){

		String obj = "addrSignal:"+ addrSignal + " ,natureOfAdrs:"+ natureOfAdrs + " ,addrPrsntInd:" + addPrsntRest + " ,numPlan:" + numPlan ;
		return obj ;
	}

	public NatureOfAddEnum getNatureOfAddressInd() {
		return natureOfAdrs;
	}

	public void setNatureOfAddressInd(NatureOfAddEnum natureOfAdrs) {
		this.natureOfAdrs = natureOfAdrs;
	}

	public NumPlanEnum getNumberingPlanInd() {
		return numPlan;
	}

	public void setNumberingPlanInd(NumPlanEnum numPlan) {
		this.numPlan = numPlan;
	}

	public AddPrsntRestEnum getAddressPresentationRestrictedInd() {
		return addPrsntRest;
	}

	public void setAddressPresentationRestrictedInd(AddPrsntRestEnum addPrsntRest) {
		this.addPrsntRest = addPrsntRest;
	}
	
}
