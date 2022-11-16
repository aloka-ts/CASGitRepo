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
import com.agnity.ain.enumdata.CarrierFormatNatEnum;
import com.agnity.ain.enumdata.CarrierFormatSelectionEnum;
import com.agnity.ain.exceptions.InvalidInputException;
import com.agnity.ain.util.Util;

/**
 * @author nishantsharma
 *
 */
public class CarrierFormat extends AddressSignal
{
	private static Logger logger = Logger.getLogger(CarrierFormat.class);
	private CarrierFormatSelectionEnum carrierFormatSelectionEnum;
	private CarrierFormatNatEnum carrierFormatNatEnum;
	/**
	 * This method encode Carrier Fromat parameters
	 * This parameter is used in InoAnalyzed and InfoCollected operations
	 * @return a byte array
	 * @throws InvalidInputException
	 */
	public  byte[] encodeCarrierFormat() throws InvalidInputException{
		if (logger.isInfoEnabled())
		{
			logger.info("encodeCarrierFormat:Enter");
		}
		int i=0;
		int carrierSelction;
		int natOfCarrier;
		if(this.carrierFormatSelectionEnum == null){

			if (logger.isInfoEnabled()){
				logger.info("encodeCarrierFormat:Assining default value spare of natureOfnum");
			}
			carrierSelction = 0;
		}else{
			carrierSelction = this.carrierFormatSelectionEnum.getCode();
		}
		if(this.carrierFormatNatEnum == null){
			if (logger.isInfoEnabled()){
				logger.info("encodeCarrierFormat:Assining default value spare of natureOfnum");
			}
			natOfCarrier = 0;
		}
		else{
			natOfCarrier = this.carrierFormatNatEnum.getCode();
		}
		byte[] bcdDigits= AddressSignal.encodeAdrsSignal(this.addrSignal);
		//calculate the length of AINDIgits parameters 
		int seqLength = 2 + bcdDigits.length;
		byte[] data=new byte[seqLength];
		//encoding for carrierSelction 
		data[i++]=(byte) carrierSelction;

		data[i++] =(byte) (natOfCarrier << 4 | 4);
		// encoding start for digits
		for (int j = 0; j < bcdDigits.length; j++){
			data[i++] = bcdDigits[j];
		}
		if(logger.isDebugEnabled()){
			logger.debug("encodeCarrierFormat:Encoded data: "+ Util.formatBytes(data));
		}
		if (logger.isInfoEnabled()){
			logger.info("encodeCarrierFormat:Exit");
		}
		return data;
	}
	/**
	 * This method decode Carrier Format parameters
	 * @param data as a byte array
	 * @return CarrierFormat object
	 * @throws InvalidInputException
	 */
	public  CarrierFormat decodeCarrierFormat(byte[] data)throws InvalidInputException{
		if (logger.isInfoEnabled()){
			logger.info("decodeCarrierFormat:Enter");
		}
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		int parity = (data[0] >> 7) & 0x1;
		this.carrierFormatSelectionEnum=CarrierFormatSelectionEnum.fromInt(data[0] & 0xFF);
		if(data.length >=1)	{ 
			int carrierNature = (data[1] >> 4) & 0xF ;
			this.carrierFormatNatEnum = CarrierFormatNatEnum.fromInt(carrierNature);	
		}
		this.addrSignal=AddressSignal.decodeAdrsSignal(data, 2, parity);
		return this;
	}
	public CarrierFormatSelectionEnum getCarrierFormatSelectionEnum() {
		return carrierFormatSelectionEnum;
	}
	public void setCarrierFormatSelectionEnum(
			CarrierFormatSelectionEnum carrierFormatSelectionEnum) {
		this.carrierFormatSelectionEnum = carrierFormatSelectionEnum;
	}
	public CarrierFormatNatEnum getCarrierFormatNatEnum() {
		return carrierFormatNatEnum;
	}
	public void setCarrierFormatNatEnum(CarrierFormatNatEnum carrierFormatNatEnum) {
		this.carrierFormatNatEnum = carrierFormatNatEnum;
	}

}
