package com.genband.inap.datatypes;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.genband.inap.enumdata.AdtnlPartyCat1Enum;
import com.genband.inap.enumdata.AdtnlPartyCatNameEnum;
import com.genband.inap.enumdata.MobileAdtnlPartyCat1Enum;
import com.genband.inap.enumdata.MobileAdtnlPartyCat2Enum;
import com.genband.inap.enumdata.MobileAdtnlPartyCat3Enum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;


/**
 * Used for encoding and decoding of TtcAdtnalPartyCategory
 * @author vgoel
 *
 */
public class TtcAdtnalPartyCategory { 
	
	/**
	 * @see AdtnlPartyCatNameEnum
	 */
	AdtnlPartyCatNameEnum adtnlPartyCatNameEnum;
	
	/**
	 * @see AdtnlPartyCat1Enum
	 */
	AdtnlPartyCat1Enum adtnlPartyCat1Enum;
	
	/**
	 * @see MobileAdtnlPartyCat1Enum
	 */
	MobileAdtnlPartyCat1Enum mobileAdtnlPartyCat1Enum;
	
	/**
	 * @see MobileAdtnlPartyCat2Enum
	 */
	MobileAdtnlPartyCat2Enum mobileAdtnlPartyCat2Enum;
	
	/**
	 * @see MobileAdtnlPartyCat3Enum
	 */
	MobileAdtnlPartyCat3Enum mobileAdtnlPartyCat3Enum;
	


	private static Logger logger = Logger.getLogger(TtcAdtnalPartyCategory.class);	 
	 
	
	public AdtnlPartyCatNameEnum getAdtnlPartyCatNameEnum() {
		return adtnlPartyCatNameEnum;
	}

	public void setAdtnlPartyCatNameEnum(AdtnlPartyCatNameEnum adtnlPartyCatNameEnum) {
		this.adtnlPartyCatNameEnum = adtnlPartyCatNameEnum;
	}

	public AdtnlPartyCat1Enum getAdtnlPartyCat1Enum() {
		return adtnlPartyCat1Enum;
	}

	public void setAdtnlPartyCat1Enum(AdtnlPartyCat1Enum adtnlPartyCat1Enum) {
		this.adtnlPartyCat1Enum = adtnlPartyCat1Enum;
	}

	public MobileAdtnlPartyCat1Enum getMobileAdtnlPartyCat1Enum() {
		return mobileAdtnlPartyCat1Enum;
	}

	public void setMobileAdtnlPartyCat1Enum(
			MobileAdtnlPartyCat1Enum mobileAdtnlPartyCat1Enum) {
		this.mobileAdtnlPartyCat1Enum = mobileAdtnlPartyCat1Enum;
	}

	public MobileAdtnlPartyCat2Enum getMobileAdtnlPartyCat2Enum() {
		return mobileAdtnlPartyCat2Enum;
	}

	public void setMobileAdtnlPartyCat2Enum(
			MobileAdtnlPartyCat2Enum mobileAdtnlPartyCat2Enum) {
		this.mobileAdtnlPartyCat2Enum = mobileAdtnlPartyCat2Enum;
	}

	public MobileAdtnlPartyCat3Enum getMobileAdtnlPartyCat3Enum() {
		return mobileAdtnlPartyCat3Enum;
	}

	public void setMobileAdtnlPartyCat3Enum(
			MobileAdtnlPartyCat3Enum mobileAdtnlPartyCat3Enum) {
		this.mobileAdtnlPartyCat3Enum = mobileAdtnlPartyCat3Enum;
	}

	
	
	/**
	 * This function will encode the TTC Additional Party's Category.
	 * For encoding, list of this class will be given as input. Every object of this list will contain additional party category name and
	 * its corresponding value (other values will be null).
	 * @param ttcAdtnalPartyCategories
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeTtcAdtnlPartyCat(LinkedList<TtcAdtnalPartyCategory> ttcAdtnalPartyCategories) throws InvalidInputException {
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcAdtnlPartyCat:Enter");
		}
		if(ttcAdtnalPartyCategories == null){
			logger.error("encodeTtcAdtnlPartyCat: InvalidInputException(ttcAdtnalPartyCategories is null)");
			throw new InvalidInputException("ttcAdtnalPartyCategories is null");
		}
		
		int i = 0;
		byte[] myParams = new byte[ttcAdtnalPartyCategories.size()*2];
		for(TtcAdtnalPartyCategory ttcAPC : ttcAdtnalPartyCategories)
		{
			if(ttcAPC.adtnlPartyCatNameEnum.getCode() == 254){
				myParams[i++] = (byte)(254);
				myParams[i++] = (byte)(ttcAPC.adtnlPartyCat1Enum.getCode());
			}
			if(ttcAPC.adtnlPartyCatNameEnum.getCode() == 253){
				myParams[i++] = (byte)(253);
				myParams[i++] = (byte)(ttcAPC.mobileAdtnlPartyCat1Enum.getCode());
			}
			if(ttcAPC.adtnlPartyCatNameEnum.getCode() == 252){
				myParams[i++] = (byte)(252);
				myParams[i++] = (byte)(ttcAPC.mobileAdtnlPartyCat2Enum.getCode());
			}
			if(ttcAPC.adtnlPartyCatNameEnum.getCode() == 251){
				myParams[i++] = (byte)(251);
				myParams[i++] = (byte)(ttcAPC.mobileAdtnlPartyCat3Enum.getCode());
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("encodeTtcAdtnlPartyCat:Encoded Ttc charge info delay: " + Util.formatBytes(myParams));
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcAdtnlPartyCat:Exit");
		}
		return myParams;
	}
	
	/**
	 * This function will decode the TTC Additional Party's Category.
	 * After decoding, list of this class will be returned as output. Every object of this list will contain additional party category name and
	 * its corresponding value (other values will be null).
	 * @param data
	 * @return list of decode object
	 * @throws InvalidInputException
	 */
	public static LinkedList<TtcAdtnalPartyCategory> decodeTtcAdtnlPartyCat(byte[] data) throws InvalidInputException
	{
		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcAdtnlPartyCat:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcAdtnlPartyCat: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeTtcAdtnlPartyCat: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		LinkedList<TtcAdtnalPartyCategory> out = new LinkedList<TtcAdtnalPartyCategory>();
		for(int i=0; i<data.length; i++){
			if(data[i] == (byte)254){
				TtcAdtnalPartyCategory ttc = new TtcAdtnalPartyCategory();
				ttc.adtnlPartyCatNameEnum = AdtnlPartyCatNameEnum.PSTN_CATEGORY_1;
				ttc.adtnlPartyCat1Enum = AdtnlPartyCat1Enum.fromInt(data[++i] & 0xFF);
				out.add(ttc);
			}
			else if(data[i] == (byte)251){
				TtcAdtnalPartyCategory ttc = new TtcAdtnalPartyCategory();
				ttc.adtnlPartyCatNameEnum = AdtnlPartyCatNameEnum.MOBILE_CATEGORY_3;
				ttc.mobileAdtnlPartyCat3Enum = MobileAdtnlPartyCat3Enum.fromInt(data[++i] & 0xFF);
				out.add(ttc);
			}
			else if(data[i] == (byte)252){
				TtcAdtnalPartyCategory ttc = new TtcAdtnalPartyCategory();
				ttc.adtnlPartyCatNameEnum = AdtnlPartyCatNameEnum.MOBILE_CATEGORY_2;
				ttc.mobileAdtnlPartyCat2Enum = MobileAdtnlPartyCat2Enum.fromInt(data[++i] & 0xFF);
				out.add(ttc);
			}
			else if(data[i] == (byte)253){
				TtcAdtnalPartyCategory ttc = new TtcAdtnalPartyCategory();
				ttc.adtnlPartyCatNameEnum = AdtnlPartyCatNameEnum.MOBILE_CATEGORY_1;
				ttc.mobileAdtnlPartyCat1Enum = MobileAdtnlPartyCat1Enum.fromInt(data[++i] & 0xFF);
				out.add(ttc);
			}
		}
		
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcAdtnlPartyCat: Output<--" + out);
		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcAdtnlPartyCat:Exit");
		}
		return out ;
	}


	public String toString(){
		
		String obj = "adtnlPartyCatNameEnum:"+ adtnlPartyCatNameEnum + " adtnlPartyCat1Enum,:" + adtnlPartyCat1Enum + " mobileAdtnlPartyCat1Enum,:" + mobileAdtnlPartyCat1Enum
		 + " mobileAdtnlPartyCat2Enum,:" + mobileAdtnlPartyCat2Enum + " mobileAdtnlPartyCat3Enum,:" + mobileAdtnlPartyCat3Enum;
		return obj ;
	}

}

