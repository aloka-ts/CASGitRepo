package com.camel.dataTypes;

import org.apache.log4j.Logger;

import com.camel.enumData.NatureOfAdrsEnum;
import com.camel.enumData.NatureOfAdrsStringEnum;
import com.camel.enumData.NumPlanEnum;
import com.camel.enumData.NumPlan_AdrsStringEnum;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.NonAsnArg;
import com.camel.util.Util;

public class AdrsStringDataType {

	String adrs ;
	/**
	 * @see NatureOfAdrsEnum
	 */
	NatureOfAdrsStringEnum natureOfAdrs ; 
	/**
	 * @see NumPlanEnum
	 */
	NumPlan_AdrsStringEnum numPlan ;

	public String getMscAdrs() {
		return adrs;
	}

	public NatureOfAdrsStringEnum getNatureOfAdrs() {
		return natureOfAdrs;
	}

	public NumPlan_AdrsStringEnum getNumPlan() {
		return numPlan;
	}
	

	public String getAdrs() {
		return adrs;
	}

	public void setAdrs(String adrs) {
		this.adrs = adrs;
	}

	public void setNatureOfAdrs(NatureOfAdrsStringEnum natureOfAdrs) {
		this.natureOfAdrs = natureOfAdrs;
	}

	public void setNumPlan(NumPlan_AdrsStringEnum numPlan) {
		this.numPlan = numPlan;
	}


	private static Logger logger = Logger.getLogger(AdrsStringDataType.class);	

	/**
	 * This function will encode address string.Adrs is mandatory parameter.
	 * Other parameters will be assigned default values if not provided.
	 * natureOfNumber: unknown, numberingPlanEnum: unknown
	 * @param adrs
	 * @param natureOfNumber
	 * @param numberingPlanEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeAdrsString(String adrs, NatureOfAdrsStringEnum natureOfNumber, NumPlan_AdrsStringEnum numberingPlanEnum) throws InvalidInputException{

		logger.info("encodeAdrsString:Enter");
		if(adrs == null || adrs.equalsIgnoreCase(" ")){
			logger.error("encodeAdrsString: InvalidInputException(mscAdrs is null or blank)");
			throw new InvalidInputException("mscAdrs is null or blank");
		}
		byte[] bcdDigits = NonAsnArg.tbcdStringEncoder(adrs);
		int seqLength = 1 + bcdDigits.length;
		int natureOfAdrs;
		if(natureOfNumber == null){
			// Unknown
			natureOfAdrs = 0;
		}else {
			natureOfAdrs = natureOfNumber.getCode();
		}
		int numberingPlan ;
		if(numberingPlanEnum == null){
			// Unknown
			numberingPlan = 0 ;
		}else {
			numberingPlan = numberingPlanEnum.getCode();
		}

		int i = 0;
		byte[] myParms = new byte[seqLength];

		myParms[i++] = (byte) ((1 << 7) | (natureOfAdrs << 4) | numberingPlan);

		for (int j = 0; j < bcdDigits.length; j++) {
			myParms[i++] = bcdDigits[j];
		}
		if(logger.isDebugEnabled())
		logger.debug("encodeAdrsString:Encoded Adrs String : " + Util.formatBytes(myParms));
		
		logger.info("encodeAdrsString:Exit");
		return myParms;

	}

	/**
	 * This function will decode address string.
	 * @param data
	 * @return object of AdrsStringDataType
	 * @throws InvalidInputException
	 */
	public static AdrsStringDataType decodeAdrsString(byte[] data) throws InvalidInputException{
		logger.info("decodeAdrsString:Enter");
		if(logger.isDebugEnabled())
		logger.debug("decodeCaldParty: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeCaldParty: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		AdrsStringDataType msc = new AdrsStringDataType();
		int natureOfAdrs = (data[0] >> 4) & 0x7 ;
		msc.natureOfAdrs = NatureOfAdrsStringEnum.fromInt(natureOfAdrs);
		int numPlan = data[0] & 0xF ;
		msc.numPlan = NumPlan_AdrsStringEnum.fromInt(numPlan);
		msc.adrs = NonAsnArg.TbcdStringDecoder(data, 1);
		logger.info("decodeAdrsString:Output<-- :" + msc.toString());
		logger.debug(msc.toString());
		logger.info("decodeAdrsString:Exit");
		return msc;
	}

	public String toString(){

		String obj = "Adrs:"+ adrs + " ,natureOfAdrs:"+ natureOfAdrs + " ,numPlan:" + numPlan ;
		return obj ;
	}

}
