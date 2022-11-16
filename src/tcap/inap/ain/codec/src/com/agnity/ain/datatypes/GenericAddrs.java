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

import com.agnity.ain.datatypes.AddressSignal;
import com.agnity.ain.enumdata.AddPrsntRestEnum;
import com.agnity.ain.enumdata.NatureOfAddCallingEnum;
import com.agnity.ain.enumdata.NumPlanEnum;
import com.agnity.ain.enumdata.TestIndEnum;
import com.agnity.ain.enumdata.TypeOfAddrsEnum;
import com.agnity.ain.exceptions.InvalidInputException;

/**
 * Used for encoding and decoding of GenericAddrs
 * 
 * @author nishantsharma
 *
 */
public class GenericAddrs extends AddressSignal
{
	private static Logger logger = Logger.getLogger(GenericAddrs.class);	 

	private TypeOfAddrsEnum typeOfAddrsEnum;
	private NatureOfAddCallingEnum natureOfAddCallingEnum;
	private NumPlanEnum numPlanEnum;
	private TestIndEnum testIndEnum;
	private AddPrsntRestEnum addPrsntRestEnum;
	private int reserved;

	/**

	 * This function will encode Generic Address.

	 * @param typeOfAddrsEnum

	 * @param natureOfAddCallingEnum

	 * @param numPlanEnum

	 * @param testIndEnum

	 * @param addPrsntRestEnum

	 * @return encoded data of GenericAddrs

	 * @throws InvalidInputException

	 */
	public static byte[] encodeGenericAddrs(TypeOfAddrsEnum typeOfAddrsEnum,NatureOfAddCallingEnum natureOfAddCallingEnum,NumPlanEnum numPlanEnum,TestIndEnum testIndEnum,AddPrsntRestEnum addPrsntRestEnum,String addrSignal) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("encodeGenericAddrs:Enter");
		}
		int i=0;
		int natureOfAdrs;
		if(natureOfAddCallingEnum == null){
			if (logger.isInfoEnabled()){
				logger.info("encodeGenericAddrs:Assining default value spare of natureOfAdrs");
			}
			natureOfAdrs = 0;
		}
		else{
			natureOfAdrs = natureOfAddCallingEnum.getCode();
		}
		byte[] bcdDigits= AddressSignal.encodeAdrsSignal(addrSignal);
		int seqLength = 3 + bcdDigits.length;
		byte[] data=new byte[seqLength];
		if(typeOfAddrsEnum==TypeOfAddrsEnum.DAILED_NUM){
			data[i]=(byte)0x00;
		}else if(typeOfAddrsEnum==TypeOfAddrsEnum.DESTINATION_NO){
			data[i]=(byte)0x01;
		}
		else if(typeOfAddrsEnum==TypeOfAddrsEnum.SUP_USR_PRO_CALLING_ADDR_failed){
			data[i]=(byte)0x02;
		}
		
		if (addrSignal.length() % 2 == 0){
			data[++i] = (byte) ((0 << 7) | natureOfAdrs);
		}
		else{
			data[++i] = (byte) ((1 << 7) | natureOfAdrs);
		}
		
		//0x00 for reserved 2 bits ,It will be further decided. 

		data[++i]=(byte)(testIndEnum.getCode() << 7 | numPlanEnum.getCode() << 4 | addPrsntRestEnum.getCode() << 2 | 0x00);
		for (int j = 0; j < bcdDigits.length; j++){
			data[++i] = bcdDigits[j];
		}
		if (logger.isInfoEnabled()){
			logger.info("encodeGenericAddrs:Exit");
		}
		return data;
	}
	/**

	 * This function will decode Generic Address.

	 * @param data

	 * @return object of GenericAddrs DataType

	 * @throws InvalidInputException 

	 */
	public static GenericAddrs decodeGenericAddrs(byte[] data)throws InvalidInputException{
		if (logger.isInfoEnabled()){
			logger.info("decodeGenericAddrs:Enter");
		}
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		GenericAddrs genericAddrs=new GenericAddrs();
		genericAddrs.typeOfAddrsEnum=TypeOfAddrsEnum.fromInt(data[0]);
		int natureOfAdrs = data[1] & 0x7f ;
		genericAddrs.natureOfAddCallingEnum=NatureOfAddCallingEnum.fromInt(natureOfAdrs);
		if(data.length >=1){
			int testIndEnum = (data[2] >> 7) & 0x1;
			genericAddrs.testIndEnum = TestIndEnum.fromInt(testIndEnum);
			int numberingPlan = (data[2] >> 4) & 0x7 ;
			genericAddrs.numPlanEnum = NumPlanEnum.fromInt(numberingPlan);
			int adrsPresntRestd = (data[2] >> 2) & 0x3 ;
			genericAddrs.addPrsntRestEnum = AddPrsntRestEnum.fromInt(adrsPresntRestd);
			genericAddrs.reserved=data[2] & 0x0;
		}
		if(genericAddrs.typeOfAddrsEnum.getCode()==0){
			genericAddrs.addrSignal=AddressSignal.decodeAdrsSignal(data, 1, 0);
			
		}
		else if(genericAddrs.typeOfAddrsEnum.getCode()==1){
			genericAddrs.addrSignal=AddressSignal.decodeAdrsSignal(data, 1, 1);
		}
		return genericAddrs;
	}
	
	public TypeOfAddrsEnum getTypeOfAddrsEnum() {
		return typeOfAddrsEnum;
	}
	
	public void setTypeOfAddrsEnum(TypeOfAddrsEnum typeOfAddrsEnum) {
		this.typeOfAddrsEnum = typeOfAddrsEnum;
	}
	
	public NatureOfAddCallingEnum getNatureOfAddCallingEnum() {
		return natureOfAddCallingEnum;
	}
	
	public void setNatureOfAddCallingEnum(
			NatureOfAddCallingEnum natureOfAddCallingEnum) {
		this.natureOfAddCallingEnum = natureOfAddCallingEnum;
	}
	
	public NumPlanEnum getNumPlanEnum() {
		return numPlanEnum;
	}
	
	public void setNumPlanEnum(NumPlanEnum numPlanEnum) {
		this.numPlanEnum = numPlanEnum;
	}
	
	public TestIndEnum getTestIndEnum() {
		return testIndEnum;
	}
	
	public void setTestIndEnum(TestIndEnum testIndEnum) {
		this.testIndEnum = testIndEnum;
	}

}
