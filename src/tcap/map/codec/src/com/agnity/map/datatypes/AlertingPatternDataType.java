package com.agnity.map.datatypes;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.TypeOfAlertCatgEnum;
import com.agnity.map.enumdata.TypeOfAlertLevelEnum;
import com.agnity.map.enumdata.TypeOfPatrnEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

public class AlertingPatternDataType {
	
	/**
	 * @see TypeOfAlertCatgEnum
	 */
	TypeOfAlertCatgEnum typeOfAlertCatgEnum ;
	
	/**
	 * @see TypeOfAlertLevelEnum
	 */
	TypeOfAlertLevelEnum typeOfAlertLevelEnum ;
	
	/**
	 * @see TypeOfPatrnEnum
	 */
	TypeOfPatrnEnum typeOfPatrnEnum ;
	
	private static Logger logger = Logger.getLogger(AlertingPatternDataType.class);	 
	
	/**
	 * This function will encode Alerting Pattern.
	 * @param typeOfAlertCatgEnum
	 * @param typeOfAlertLevelEnum
	 * @param typeOfPatrnEnum
	 * @return encoded data of AlertingPttrn
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeAlertingPttrn(TypeOfPatrnEnum typeOfPatrnEnum, TypeOfAlertCatgEnum typeOfAlertCatgEnum,
			TypeOfAlertLevelEnum typeOfAlertLevelEnum) throws InvalidInputException{
		
		logger.info("encodeAlertingPttrn:Enter");
		if(typeOfPatrnEnum == null){
			logger.error("encodeAlertingPttrn: InvalidInputException(typeOfPatrnEnum is null )");
			throw new InvalidInputException("typeOfPatrnEnum is null or blank");
		}
		if(typeOfAlertLevelEnum == null && typeOfAlertCatgEnum == null){
			logger.error("encodeAlertingPttrn: InvalidInputException(typeOfAlertCatgEnum and typeOfAlertLevelEnum are null )");
			throw new InvalidInputException("typeOfAlertCatgEnum and typeOfAlertLevelEnum are null");
		}
		if(typeOfPatrnEnum.getCode() == 0 && typeOfAlertLevelEnum == null){
			logger.error("encodeAlertingPttrn: InvalidInputException(typeOfAlertCatgEnum and typeOfAlertLevelEnum are null )");
			throw new InvalidInputException("typeOfAlertLevelEnum cant be null because pattern is level");
		}
		if(typeOfPatrnEnum.getCode() == 1 && typeOfAlertCatgEnum == null){
			logger.error("encodeAlertingPttrn: InvalidInputException(typeOfAlertCatgEnum and typeOfAlertLevelEnum are null )");
			throw new InvalidInputException("typeOfAlertCatgEnum cant be null because pattern is category");
		}
		byte[] data = new byte[1];
		int typeOfPatrn = typeOfPatrnEnum.getCode();
		int typeOfCatg = typeOfAlertCatgEnum.getCode();
		int typeOfLevel = typeOfAlertLevelEnum.getCode();
		if(typeOfCatg == 4  && typeOfPatrn == 1){
			data[0] = (byte)( 0x8 | 0x0);
		} else{
			data[0] = (byte)((typeOfPatrn << 2) | (typeOfPatrn==0?typeOfLevel:typeOfCatg));
		}
		if(logger.isDebugEnabled())
		logger.debug("encodeAlertingPttrn:Encoded Alerting pAtrn: " + Util.formatBytes(data));
		
		logger.info("encodeAlertingPttrn:Exit");
		return data ;
		
	}
	
	public static AlertingPatternDataType decodeAlertingPttrn(byte[] data) throws InvalidInputException{
		
		logger.info("decodeAlertingPttrn:Enter");
		if(logger.isDebugEnabled())
		logger.debug("decodeAlertingPttrn: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeAlertingPttrn: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		AlertingPatternDataType alertData = new AlertingPatternDataType();
		
		int typeOfPatrn = (data[0] >> 2) & 0x3 ;
		if(typeOfPatrn == 2){
			alertData.typeOfPatrnEnum = TypeOfPatrnEnum.fromInt(typeOfPatrn-1);
		}else {
			alertData.typeOfPatrnEnum = TypeOfPatrnEnum.fromInt(typeOfPatrn);
		}
		int typeOflevelOrCatg = data[0] & 0x3 ;
		if(typeOfPatrn == 0){
			alertData.typeOfAlertLevelEnum = TypeOfAlertLevelEnum.fromInt(typeOflevelOrCatg);
		}
		else if(typeOfPatrn == 1){
			alertData.typeOfAlertCatgEnum = TypeOfAlertCatgEnum.fromInt(typeOflevelOrCatg);
		}
		else if(typeOfPatrn == 2){
			alertData.typeOfAlertCatgEnum = TypeOfAlertCatgEnum.fromInt(4);
		}
		logger.debug("decodeAlertingPttrn: Output<--" + alertData.toString());
		logger.info("decodeAlertingPttrn:Exit");
		return alertData ;
	}
	
	public TypeOfAlertCatgEnum getTypeOfAlertCatgEnum() {
		return typeOfAlertCatgEnum;
	}

	public TypeOfAlertLevelEnum getTypeOfAlertLevelEnum() {
		return typeOfAlertLevelEnum;
	}

	public TypeOfPatrnEnum getTypeOfPatrnEnum() {
		return typeOfPatrnEnum;
	}
	
	
	public void setTypeOfAlertCatgEnum(TypeOfAlertCatgEnum typeOfAlertCatgEnum) {
		this.typeOfAlertCatgEnum = typeOfAlertCatgEnum;
	}

	public void setTypeOfAlertLevelEnum(TypeOfAlertLevelEnum typeOfAlertLevelEnum) {
		this.typeOfAlertLevelEnum = typeOfAlertLevelEnum;
	}

	public void setTypeOfPatrnEnum(TypeOfPatrnEnum typeOfPatrnEnum) {
		this.typeOfPatrnEnum = typeOfPatrnEnum;
	}

	public String toString(){

		String obj = "typeOfAlertCatgEnum:"+ typeOfAlertCatgEnum + " ,typeOfAlertLevelEnum:"+ typeOfAlertLevelEnum + " ,typeOfPatrnEnum:" + typeOfPatrnEnum ;

		return obj ;
	}

}
