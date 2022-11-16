package com.camel.dataTypes;

import org.apache.log4j.Logger;

import com.camel.enumData.CauseValEnum;
import com.camel.enumData.CodingStndEnum;
import com.camel.enumData.LocationEnum;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.Util;

/**
 * This class have parameters for Cause. 
 * @author nkumar
 *
 */
public class CauseDataType {

	/**
	 * @see LocationEnum
	 */
	LocationEnum locEnum ;

	/**
	 * @see CodingStndEnum
	 */
	CodingStndEnum codingStndEnum ;
	/**
	 * @see CauseValEnum
	 */
	CauseValEnum causeValEnum ;

	private static Logger logger = Logger.getLogger(CauseDataType.class);	 

	public LocationEnum getLocEnum() {
		return locEnum;
	}
	public CodingStndEnum getCodingStndEnum() {
		return codingStndEnum;
	}
	public CauseValEnum getCauseValEnum() {
		return causeValEnum;
	}
	
	
	public void setLocEnum(LocationEnum locEnum) {
		this.locEnum = locEnum;
	}
	public void setCodingStndEnum(CodingStndEnum codingStndEnum) {
		this.codingStndEnum = codingStndEnum;
	}
	public void setCauseValEnum(CauseValEnum causeValEnum) {
		this.causeValEnum = causeValEnum;
	}
	/**
	 * This function will encode Cause. causeValEnum is mandatory parameter.
	 * Other parameters will be assigned default values if not provided.
	 * locationEnum:spare, CodingStndEnum:ITU-T standardized coding
	 * @param locationEnum
	 * @param codingStndEnum
	 * @param causeValEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeCauseVal(LocationEnum locationEnum, CodingStndEnum codingStndEnum , CauseValEnum causeValEnum) throws InvalidInputException{
		logger.info("encodeCauseVal:Enter");
		byte[] myParms = new byte[2];

		int location;
		if(locationEnum == null){
			// Assigning value Spare
			location = 6;
		}else {
			location = locationEnum.getCode();
		}
		int codingStnd ;
		if(codingStndEnum == null){
			// Assigning value ITU-T standardized coding
			codingStnd = 0 ;
		}else {
			codingStnd = codingStndEnum.getCode();
		}
		int causeVal ;
		if(causeValEnum == null){
			logger.error("encodeCauseVal: InvalidInputException(causeValEnum is null)");
			throw new InvalidInputException("causeValEnum is null");
		}
		causeVal = causeValEnum.getCode();
		
		myParms[0] = (byte) ((1 << 7) | (codingStnd << 5) | location);
		myParms[1] = (byte) ((1 << 7) | causeVal);

		if(logger.isDebugEnabled())
		logger.debug("encodeCauseVal:Encoded Cause: "+ Util.formatBytes(myParms));
		
		logger.info("encodeCauseVal:Exit");
		return myParms;
	}
	
	/**
	 * This function will decode Cause.
	 * @param data
	 * @return object of CauseDataType
	 * @throws InvalidInputException
	 */
	public static CauseDataType decodeCauseVal(byte[] data) throws InvalidInputException{
		logger.info("decodeCauseVal:Enter");
		if(logger.isDebugEnabled())
		logger.debug("decodeCauseVal: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeCauseVal: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		CauseDataType causeData = new CauseDataType();
		int codingStd = (data[0] >> 5) & 0x3;
		causeData.codingStndEnum = CodingStndEnum.fromInt(codingStd);
		int location = (data[0] ) & 0xF;
		causeData.locEnum = LocationEnum.fromInt(location);
		if(data.length > 1){
			int causeVal = data[1] & 0x7F ;
			causeData.causeValEnum = CauseValEnum.fromInt(causeVal);
		}
		
		logger.debug("decodeCauseVal: Output<--" + causeData.toString());
		logger.info("decodeCauseVal:Exit");
		return causeData ;
	}
	
    public String toString(){
		
		String obj = "locEnum:"+ locEnum + " ,codingStndEnum:"+ codingStndEnum + " causeValEnum:" + causeValEnum ;
		return obj ;
	}
}
