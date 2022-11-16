package com.camel.dataTypes;

import org.apache.log4j.Logger;

import com.camel.enumData.AdrsPrsntRestdEnum;
import com.camel.enumData.IntNtwrkNumEnum;
import com.camel.enumData.NatureOfAdrsEnum;
import com.camel.enumData.NumINcomplteEnum;
import com.camel.enumData.NumPlanEnum;
import com.camel.enumData.ScreeningIndEnum;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.Util;

/**
 * This class have parameters for location number. 
 * @author nkumar
 *
 */
public class LocationNum extends AdrsSignalDataType{
	
	/**
	 * @see
	 */
	NatureOfAdrsEnum natureOfAdrs; 
	/**
	 * @see NumPlanEnum
	 */
	NumPlanEnum numPlan; 
	/**
	 * @see AdrsPrsntRestdEnum
	 */
	AdrsPrsntRestdEnum adrsPresntRestd; 
	/**
	 * @see ScreeningIndEnum
	 */
	ScreeningIndEnum screening ; 
	/**
	 * @see IntNtwrkNumEnum
	 */
	IntNtwrkNumEnum intNtwrkNumEnum ;
	
	private static Logger logger = Logger.getLogger(LocationNum.class);	 
	
	/**
	 * This function will encode the location number. locNum is mandatory parameter.
	 * Other parameters will be assigned default values if not provided.
	 * NatureOfAdrsEnum:spare, NumPlanEnum:spare,adrsPresntRestdEnum:Spare,
	 * screeningEnum:"user provided, not verified" IntNtwrkNumEnum:routing to internal network number allowed
	 * @param locNum
	 * @param natureOfAdrs
	 * @param numberingPlan
	 * @param adrsPresntRestd
	 * @param screening
	 * @param intNtwrkNumEnum
	 * @return encoded data of LocationNum
	 * @throws InvalidInputException
	 */
	public static byte[] encodeLocationNum(String locNum, NatureOfAdrsEnum natureOfAdrs, NumPlanEnum numberingPlan, 
												AdrsPrsntRestdEnum adrsPresntRestd, ScreeningIndEnum screening , IntNtwrkNumEnum intNtwrkNumEnum) throws InvalidInputException{
		
		logger.info("encodeLocationNum:Enter");
		NumINcomplteEnum intNtwrkNum ;
		if(intNtwrkNumEnum == null){
			intNtwrkNum = NumINcomplteEnum.fromInt(0);
		}
		intNtwrkNum = NumINcomplteEnum.fromInt(intNtwrkNumEnum.getCode());
		byte[] data = CallingPartyNum.encodeCalgParty(locNum, natureOfAdrs, numberingPlan, adrsPresntRestd, screening, intNtwrkNum);
		logger.info("encodeLocationNum:Exit");
		return data ;
		
	}
	
	public NatureOfAdrsEnum getNatureOfAdrs() {
		return natureOfAdrs;
	}

	public NumPlanEnum getNumPlan() {
		return numPlan;
	}

	public AdrsPrsntRestdEnum getAdrsPresntRestd() {
		return adrsPresntRestd;
	}

	public ScreeningIndEnum getScreening() {
		return screening;
	}

	public IntNtwrkNumEnum getIntNtwrkNumEnum() {
		return intNtwrkNumEnum;
	}

	
	public void setNatureOfAdrs(NatureOfAdrsEnum natureOfAdrs) {
		this.natureOfAdrs = natureOfAdrs;
	}

	public void setNumPlan(NumPlanEnum numPlan) {
		this.numPlan = numPlan;
	}

	public void setAdrsPresntRestd(AdrsPrsntRestdEnum adrsPresntRestd) {
		this.adrsPresntRestd = adrsPresntRestd;
	}

	public void setScreening(ScreeningIndEnum screening) {
		this.screening = screening;
	}

	public void setIntNtwrkNumEnum(IntNtwrkNumEnum intNtwrkNumEnum) {
		this.intNtwrkNumEnum = intNtwrkNumEnum;
	}

	public static LocationNum decodeLocationNum(byte[] data) throws InvalidInputException{
		logger.info("decodeLocationNum:Enter");
		if(logger.isDebugEnabled())
		logger.debug("decodeLocationNum: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeLocationNum: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		LocationNum locNum = new LocationNum();
		int parity = (data[0] >> 7) & 0x1;
		int natureOfAdrs = data[0] & 0x7f ;
		locNum.natureOfAdrs = NatureOfAdrsEnum.fromInt(natureOfAdrs);
		if(data.length > 1){
			int intNtwrkNum = (data[1] >> 7) & 0x1;
			locNum.intNtwrkNumEnum = IntNtwrkNumEnum.fromInt(intNtwrkNum);
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			locNum.numPlan = NumPlanEnum.fromInt(numberingPlan);
			int adrsPresntRestd = (data[1] >> 2) & 0x3 ;
			locNum.adrsPresntRestd = AdrsPrsntRestdEnum.fromInt(adrsPresntRestd);
			int screening = data[1] &0x3 ;
			locNum.screening = ScreeningIndEnum.fromInt(screening);
		}
		if(data.length > 2){
			locNum.addrSignal = AdrsSignalDataType.decodeAdrsSignal(data, 2 , parity);
		}
		
		logger.debug("decodeLocationNum:Output<--"+ locNum.toString());
		logger.info("decodeLocationNum:Exit");
		return locNum ;
	}
	
public String toString(){
		
		String locnum = "addrSignal:"+ addrSignal + " ,natureOfAdrs:"+ natureOfAdrs + " ,intNtwrkNumEnum:" + intNtwrkNumEnum +
									
									" ,numPlan:" + numPlan + " ,adrsPresntRestd:" + adrsPresntRestd + " ,screening:" + screening ;
		return locnum ;
	}

}
