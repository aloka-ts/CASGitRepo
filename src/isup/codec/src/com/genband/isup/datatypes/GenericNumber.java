package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.AddPrsntRestEnum;
import com.genband.isup.enumdata.NatureOfAddEnum;
import com.genband.isup.enumdata.NumIncmpltEnum;
import com.genband.isup.enumdata.NumPlanEnum;
import com.genband.isup.enumdata.NumQualifierIndEnum;
import com.genband.isup.enumdata.ScreeningIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * Used for encoding and decoding of GenericNumber
 * @author vgoel
 *
 */
public class GenericNumber extends CallingPartyNum {
	
	/**
	 * @see NumQualifierIndEnum
	 */
	NumQualifierIndEnum numQualifier ;

	public NumQualifierIndEnum getNumQualifier() {
		return numQualifier;
	}
	
	public void setNumQualifier(NumQualifierIndEnum numQualifier) {
		this.numQualifier = numQualifier;
	}

	private static Logger logger = Logger.getLogger(GenericNumber.class);	 

	/**
	 * This function will encode Generic Number.AddrsSignal is mandatory paramter.
	 * Other parameters will be assigned default values if not provided.
	 * numQualifierIndEnum:spare,NatureOfAdrsEnum:spare, NumPlanEnum:spare,adrsPresntRestdEnum:Spare,
	 * screeningEnum:"user provided, not verified"
	 * @param numQualifierIndEnum
	 * @param addrSignal
	 * @param natureOfAdrsEnum
	 * @param numberingPlanEnum
	 * @param adrsPresntRestdEnum
	 * @param screeningEnum
	 * @param numIncomplte
	 * @return encoded data of GenericNum
	 * @throws InvalidInputException
	 */
	public static byte[] encodeGenericNum(NumQualifierIndEnum numQualifierIndEnum,String addrSignal, NatureOfAddEnum natureOfAdrsEnum, 
			NumPlanEnum numberingPlanEnum, AddPrsntRestEnum adrsPresntRestdEnum, 
			ScreeningIndEnum screeningEnum , NumIncmpltEnum numIncomplte ) throws InvalidInputException{

		logger.info("encodeGenericNum:Enter");
		byte[] data = CallingPartyNum.encodeCalgParty(addrSignal, natureOfAdrsEnum, numberingPlanEnum, adrsPresntRestdEnum, screeningEnum, numIncomplte);

		int seqLen = data.length + 1;
		byte[] genericNum = new byte[seqLen] ;
		int numQualifierInd ;
		if(numQualifierIndEnum == null){
			//spare
			numQualifierInd = 11 ;
		}else{
			numQualifierInd = numQualifierIndEnum.getCode();
		}
		genericNum[0] = (byte)( numQualifierInd | 0x0);

		for(int k= 0 ; k < data.length ; k++){
			genericNum[k+1] = data[k];
		}
		if(logger.isDebugEnabled())
		logger.debug("encodeGenericNum:Encoded GenericNum: " + Util.formatBytes(genericNum));
		logger.info("encodeGenericNum:Exit");
		return genericNum ;
	}

	/**
	 * This function will decode Generic Number.
	 * @param data
	 * @return object of GenericNumDataType
	 * @throws InvalidInputException 
	 */
	public static GenericNumber decodeGenericNum(byte[] data) throws InvalidInputException{
		logger.info("decodeGenericNum:Enter");
		if(data == null){
			logger.error("InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		GenericNumber genericNum = new GenericNumber();
		int numQualifier = data[0] ;
		genericNum.numQualifier = NumQualifierIndEnum.fromInt(numQualifier);
		int parity = 0;
		if(data.length > 1) {
			parity = (data[1] >> 7) & 0x1 ;
			int natureOfAdrs = data[1] & 0x7f ;
			genericNum.natureOfAdrs = NatureOfAddEnum.fromInt(natureOfAdrs);			
		}
		if(data.length > 2){
			int numIncomplete = (data[2] >> 7) & 0x1;
			genericNum.numIncomplte = NumIncmpltEnum.fromInt(numIncomplete);
			int numberingPlan = (data[2] >> 4) & 0x7 ;
			genericNum.numPlan = NumPlanEnum.fromInt(numberingPlan);
			int adrsPresntRestd = (data[2] >> 2) & 0x3 ;
			genericNum.adrsPresntRestd = AddPrsntRestEnum.fromInt(adrsPresntRestd);
			int screening = data[2] &0x3 ;
			genericNum.screening = ScreeningIndEnum.fromInt(screening);
		}		
		if(data.length > 3){
			genericNum.addrSignal = AddressSignal.decodeAdrsSignal(data, 3 , parity);
		}
		logger.info("-----Decoded data-----");
		logger.debug(genericNum.toString());
		logger.info("decodeGenericNum:Exit");
		return genericNum ;

	}
	
public String toString(){
		
		String obj = "numQualifier:"+ numQualifier + super.toString();
		return obj ;
	}


}
