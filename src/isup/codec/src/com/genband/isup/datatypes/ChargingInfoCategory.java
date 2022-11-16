package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.ChargingInfoCatEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * This class have parameters for Charging Information Category. 
 * @author vgoel
 *
 */
public class ChargingInfoCategory {

	/**
	 * @see ChargingInfoCatEnum
	 */
	ChargingInfoCatEnum chargingInfoCatEnum;
	
	private static Logger logger = Logger.getLogger(ChargingInfoCategory.class);

	public ChargingInfoCatEnum getChargingInfoCatEnum() {
		return chargingInfoCatEnum;
	}

	public void setChargingInfoCatEnum(ChargingInfoCatEnum chargingInfoCatEnum) {
		this.chargingInfoCatEnum = chargingInfoCatEnum;
	}
	
	
	/**
	 * This function will encode charge info category
	 * @param chargingInfoCatEnum
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeChargingInfoCat(ChargingInfoCatEnum chargingInfoCatEnum) throws InvalidInputException
	{	
		logger.info("encodeChargingInfoCat:Enter");
		
		byte[] data = new byte[1];		
		int chargingInfoCat;
		if(chargingInfoCatEnum == null)
			chargingInfoCat = 129; //spare	
		else
			chargingInfoCat = chargingInfoCatEnum.getCode();		
				
		data[0] = (byte) (chargingInfoCat & 0xFF);
		
		if(logger.isDebugEnabled())
			logger.debug("encodeChargingInfoCat:Encoded Charging Info Category: " + Util.formatBytes(data));
		logger.info("encodeChargingInfoCat:Exit");
		
		return data;
	}
	
	
	/**
	 * This function will charge info category
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static ChargingInfoCategory decodeChargingInfoCat(byte[] data) throws InvalidInputException
	{
		logger.info("decodeChargingInfoCat:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeChargingInfoCat: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeChargingInfoCat: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		ChargingInfoCategory chargingInfoCat = new ChargingInfoCategory();
		
		chargingInfoCat.chargingInfoCatEnum = ChargingInfoCatEnum.fromInt(data[0] & 0xFF);
		
		if(logger.isDebugEnabled())
			logger.debug("decodeChargingInfoCat: Output<--" + chargingInfoCat.toString());
		logger.info("decodeChargingInfoCat:Exit");
		
		return chargingInfoCat ;
	}
	
	public String toString(){
		
		String obj = "chargingInfoCatEnum:"+ chargingInfoCatEnum;
		return obj ;
	}	
}
