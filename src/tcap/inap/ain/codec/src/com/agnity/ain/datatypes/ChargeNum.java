/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
/**
 * 
 */
package com.agnity.ain.datatypes;

import org.apache.log4j.Logger;

import com.agnity.ain.enumdata.ChargeNumEnum;
import com.agnity.ain.enumdata.ClgPrsntRestIndEnum;
import com.agnity.ain.enumdata.NumPlanEnum;
import com.agnity.ain.enumdata.ScreeningIndEnum;
import com.agnity.ain.exceptions.InvalidInputException;
import com.agnity.ain.util.Util;

/**
 * @author nishantsharma
 *
 */
public class ChargeNum extends AddressSignal{

	private static Logger logger = Logger.getLogger(AinDigits.class);
	private ChargeNumEnum chargeNumEnum;
	private NumPlanEnum numPlanEnum;
	private ClgPrsntRestIndEnum clgPrsntRestIndEnum; 
	private ScreeningIndEnum screeningIndEnum;
	private String addrSignal;
	private int spare;


	/**

	 * This function will encode ChargeNumber.
	 * @param chargeNumEnum
	 * @param numPlanEnum
	 * @param clgPrsntRestIndEnum
	 * @param screeningIndEnum
	 * @return encoded data of ChargeNumber
	 * @throws InvalidInputException
	 */
	public  byte[] encodeChargeNum() throws InvalidInputException{
		if (logger.isInfoEnabled()){
			logger.info("encodeAinDigits:Enter");
		}
		int i=0;
		int natureOfnum;
		int clgPrsntRest=4;
		int screeningInd=0;
		if(this.chargeNumEnum == null){
			if (logger.isInfoEnabled()){
				logger.info("encodeAinDigits:Assining default value spare of natureOfnum");
			}
			natureOfnum = 0;
		}
		else{
			natureOfnum = this.chargeNumEnum.getCode();
		}
		byte[] bcdDigits= AddressSignal.encodeAdrsSignal(this.addrSignal);
		//calculate the length of AINDIgits parameters 
		int seqLength = 2 + bcdDigits.length;
		byte[] data=new byte[seqLength];
		//Nature of Number for calling parameters
		if(this.chargeNumEnum!=null){
			if (this.addrSignal.length() % 2 == 0) 
			{
				data[i++] = (byte) ((0 << 7) | natureOfnum);
			}
			else 
			{
				data[i++] = (byte) ((1 << 7) | natureOfnum);
			}
		}
		//encoding start for numbering plan ,present indicator ,screening indicator and spare...
		if(this.chargeNumEnum==null){
			if (logger.isInfoEnabled()) 
			{
				logger.info("encodeAinDigits:Assining default value spare of clgPrsntRest");
			}
			clgPrsntRest =0;
			screeningInd =0;
		}
		else if(this.chargeNumEnum!=null){
			clgPrsntRest=this.clgPrsntRestIndEnum.getCode();
			screeningInd=this.screeningIndEnum.getCode();
			//data[i++] =(byte) (0 << 7 | this.numPlanEnum.getCode() << 4 | clgPrsntRest << 2 |  screeningInd);
		}
		data[i++] =(byte) (0 << 7 | this.numPlanEnum.getCode() << 4 | clgPrsntRest << 2 |  screeningInd);
		// encoding start for digits
		for (int j = 0; j < bcdDigits.length; j++){
			data[i++] = bcdDigits[j];
		}
		if(logger.isDebugEnabled()){
			logger.debug("encodeAinDigits:Encoded data: "+ Util.formatBytes(data));
		}
		if (logger.isInfoEnabled()){
			logger.info("encodeAinDigits:Exit");
		}
		return data;
	}
	/**

	 * This function will decode ChargeNumber..
	 * @param data
	 * @return object of AinDigits DataType
	 * @throws InvalidInputException 
	 */
	public  ChargeNum decodeChargeNum(byte[] data)throws InvalidInputException
	{
		if (logger.isInfoEnabled()){
			logger.info("decodeAinDigits:Enter");
		}
		
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		int parity = (data[0] >> 7) & 0x1;
		this.chargeNumEnum=ChargeNumEnum.fromInt(data[0] & 0x7F);
		if(data.length >=1){ 
			this.spare=(data[1] >> 7) & 0x3;
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			this.numPlanEnum = NumPlanEnum.fromInt(numberingPlan);
			int clgPrsntRest = (data[1] >> 2) & 0x3 ;
			this.clgPrsntRestIndEnum = ClgPrsntRestIndEnum.fromInt(clgPrsntRest);
			int screening=data[1] & 0x3;
			this.screeningIndEnum=ScreeningIndEnum.fromInt(screening);
		}
		this.addrSignal=AddressSignal.decodeAdrsSignal(data, 2, parity);
		return this;
	}

	public String toString(){
		String obj = "addrSignal:"+ addrSignal+",clgPrsntRestIndEnum:" +clgPrsntRestIndEnum+",screeningIndEnum:"+ screeningIndEnum+" ,chargeNumEnum:"+chargeNumEnum+" ,numPlan:" + numPlanEnum ;
		return obj ;
	}
	
	public int getSpare() {
		return spare;
	}
	public void setSpare(int spare) {
		this.spare = spare;
	}

	public ChargeNumEnum getChargeNumEnum() {
		return chargeNumEnum;
	}
	public void setChargeNumEnum(ChargeNumEnum chargeNumEnum) {
		this.chargeNumEnum = chargeNumEnum;
	}
	public NumPlanEnum getNumPlanEnum() {
		return numPlanEnum;
	}
	public void setNumPlanEnum(NumPlanEnum numPlanEnum) {
		this.numPlanEnum = numPlanEnum;
	}

	public  ClgPrsntRestIndEnum getClgPrsntRestIndEnum() {
		return clgPrsntRestIndEnum;
	}
	
	public  void setClgPrsntRestIndEnum(ClgPrsntRestIndEnum clgPrsntRestIndEnum) {
		this.clgPrsntRestIndEnum = clgPrsntRestIndEnum;
	}
	
	public  String getAddrSignal() {
		return addrSignal;
	}
	
	public  void setAddrSignal(String addrSignal) {
		this.addrSignal = addrSignal;
	}
	
	public ScreeningIndEnum getScreeningIndEnum() {
		return screeningIndEnum;
	}
	
	public void setScreeningIndEnum(ScreeningIndEnum screeningIndEnum) {
		this.screeningIndEnum = screeningIndEnum;
	}

}
