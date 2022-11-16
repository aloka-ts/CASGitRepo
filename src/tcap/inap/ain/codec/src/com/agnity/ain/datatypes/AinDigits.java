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
import com.agnity.ain.enumdata.CalgNatOfNumEnum;
import com.agnity.ain.enumdata.CalledNatOfNumEnum;
import com.agnity.ain.enumdata.ChargeNumEnum;
import com.agnity.ain.enumdata.ClgPrsntRestIndEnum;
import com.agnity.ain.enumdata.NumPlanEnum;
import com.agnity.ain.enumdata.ScreeningIndEnum;
import com.agnity.ain.exceptions.InvalidInputException;
import com.agnity.ain.util.Constant;
import com.agnity.ain.util.Util;

/**
 * @author nishantsharma
 *
 */
public class AinDigits extends AddressSignal
{
	private static Logger logger = Logger.getLogger(AinDigits.class);
	private CalgNatOfNumEnum calgNatOfNumEnum;
	private CalledNatOfNumEnum calledNatOfNumEnum;
	private ChargeNumEnum chargeNumEnum;
	private NumPlanEnum numPlanEnum;
	private ClgPrsntRestIndEnum clgPrsntRestIndEnum; 
	private ScreeningIndEnum screeningIndEnum;
	private String addrSignal;
	private int spare;
	
	public static AinDigits getInstance()
	{
		return new AinDigits();
	}
	/**

	 * This function will encode AinDigits.It encodes the 
	  
	 * called and calling parameter like charge number ,Calling party ID 
	  
	 * and Called Party ID etc.

	 * @return encoded data of AinDigits

	 * @throws InvalidInputException

	 */
	public  byte[] encodeAinDigits() throws InvalidInputException
	{
		if (logger.isInfoEnabled())
		{
			logger.info("encodeAinDigits:Enter");
		}
		int i=0;
		int natureOfnum;
		int clgPrsntRest=0;
		int screeningInd=0;
		int numPlan;
		if(this.calgNatOfNumEnum == null)
		{
			if (logger.isInfoEnabled()) 
			{
				logger.info("encodeAinDigits:Assining default value spare of natureOfnum");
			}
			natureOfnum = this.calledNatOfNumEnum.getCode();
		}
		else 
		{
			natureOfnum = this.calgNatOfNumEnum.getCode();
		}

		byte[] bcdDigits= AddressSignal.encodeAdrsSignal(this.addrSignal);
		//calculate the length of AINDIgits parameters 
		int seqLength = 2 + bcdDigits.length;
		byte[] data=new byte[seqLength];
		//Nature of Number for calling parameters
		if(this.calgNatOfNumEnum!=null)
		{
			if (this.addrSignal.length() % 2 == 0) 
			{
				data[i++] = (byte) ((0 << 7) | natureOfnum);
			}
			else 
			{
				data[i++] = (byte) ((1 << 7) | natureOfnum);
			}
		}
		//Nature of Number for called parameters
		else
		{
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
		if(this.calgNatOfNumEnum==null && this.calledNatOfNumEnum==null)
		{
			if (logger.isInfoEnabled()) 
			{
				logger.info("encodeAinDigits:Assining default value spare of clgPrsntRest");
			}
			clgPrsntRest =0;
			screeningInd =0;
		}
		else if(this.calgNatOfNumEnum!=null)
		{
			if(this.clgPrsntRestIndEnum!=null)
			{
				clgPrsntRest=this.clgPrsntRestIndEnum.getCode();
			}
			else
			{
				clgPrsntRest=0;
			}
			if(this.screeningIndEnum!=null)
			{
				screeningInd=this.screeningIndEnum.getCode();
			}
			else
			{
				screeningInd=0;
			}
		}
		if(this.numPlanEnum!=null)
		{
			numPlan=this.numPlanEnum.getCode();
		}
		else
		{
			numPlan=0;
		}
		data[i++] =(byte) (0 << 7 | numPlan << 4 | clgPrsntRest << 2 |  screeningInd);
		// encoding start for digits
		for (int j = 0; j < bcdDigits.length; j++) 
		{
			data[i++] = bcdDigits[j];
		}
		if(logger.isDebugEnabled())
		{
			logger.debug("encodeAinDigits:Encoded data: "+ Util.formatBytes(data));
		}
		if (logger.isInfoEnabled()) 
		{
			logger.info("encodeAinDigits:Exit");
		}
		return data;
	}
	/**

	 * This function will decode AinDigits..

	 * @param data
	 
	 * @param lata boolean flag for checking called parameter lata.Becuase it takes maximum 3 digits 

	 * @return object of AinDigits DataType

	 * @throws InvalidInputException 

	 */
	public  AinDigits decodeAinDigits(byte[] data,String ainClgCldType)throws InvalidInputException
	{
		if (logger.isInfoEnabled()) 
		{
			logger.info("decodeAinDigits:Enter");
		}
		if(data == null)
		{
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		int parity = (data[0] >> 7) & 0x1;
		if(ainClgCldType==Constant.CALLING)
		{
			this.calgNatOfNumEnum=CalgNatOfNumEnum.fromInt(data[0] & 0x7F);
		}
		else if(ainClgCldType==Constant.CALLED)
		{
			this.calledNatOfNumEnum=CalledNatOfNumEnum.fromInt(data[0] & 0x7F);
		}
		if(data.length >=1)
		{ 
			this.spare=(data[1] >> 7) & 0x3;
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			this.numPlanEnum = NumPlanEnum.fromInt(numberingPlan);
			int clgPrsntRest = (data[1] >> 2) & 0x3 ;
			this.clgPrsntRestIndEnum = ClgPrsntRestIndEnum.fromInt(clgPrsntRest);
			int screening=data[1] & 0x3;
			this.screeningIndEnum=ScreeningIndEnum.fromInt(screening);

		}
		this.addrSignal=AddressSignal.decodeAdrsSignal(data, 2, parity);
		//lata maximum digits should be three 
		/*if(true==lata)
		{
			String lataDigits=AddressSignal.decodeAdrsSignal(data, 2, parity);
			this.addrSignal=lataDigits.substring(0,3);
		}*/
		return this;
	}

	public String toString()
	{
		String obj = "addrSignal:"+ addrSignal+",clgPrsntRestIndEnum:" +clgPrsntRestIndEnum+",screeningIndEnum:"+ screeningIndEnum+" ,chargeNumEnum:"+chargeNumEnum+" ,calgNatOfNumEnum:"+calgNatOfNumEnum+" ,calledNatOfNumEnum:"+calledNatOfNumEnum+ ",numPlan:" + numPlanEnum ;
		return obj ;
	}
	public int getSpare() {
		return spare;
	}
	public void setSpare(int spare) {
		this.spare = spare;
	}
	public CalgNatOfNumEnum getCalgNatOfNumEnum() {
		return calgNatOfNumEnum;
	}
	public void setCalgNatOfNumEnum(CalgNatOfNumEnum calgNatOfNumEnum) {
		this.calgNatOfNumEnum = calgNatOfNumEnum;
	}
	public CalledNatOfNumEnum getCalledNatOfNumEnum() {
		return calledNatOfNumEnum;
	}
	public void setCalledNatOfNumEnum(CalledNatOfNumEnum calledNatOfNumEnum) {
		this.calledNatOfNumEnum = calledNatOfNumEnum;
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
