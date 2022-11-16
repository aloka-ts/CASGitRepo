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
package com.agnity.ain.datatypes;import org.apache.log4j.Logger;import com.agnity.ain.enumdata.CauseValEnum;import com.agnity.ain.enumdata.CodingStndEnum;import com.agnity.ain.enumdata.LocationEnum;import com.agnity.ain.exceptions.InvalidInputException;import com.agnity.ain.util.Util;

/**
 * Used for encoding and decoding of Cause
 * @author Mriganka
 *
 */
public class Cause {
	/**
	 * @see LocationEnum
	 */
	private LocationEnum locEnum ;
	/**
	 * @see CodingStndEnum
	 */
	private CodingStndEnum codingStndEnum ;
	/**
	 * @see CauseValEnum
	 */
	private CauseValEnum causeValEnum ;
	private static Logger logger = Logger.getLogger(Cause.class);	 

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
		if (logger.isInfoEnabled()) {
			logger.info("encodeCauseVal:Enter");
		}
		byte[] myParms = new byte[2];
		int location;
		if(locationEnum == null){
			// Assigning value Spare			location = 6;
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
		if (logger.isInfoEnabled()) {
			logger.info("encodeCauseVal:Exit");
		}
		return myParms;
	}
	
	/**
	 * This function will decode Cause.
	 * @param data
	 * @return object of CauseDataType
	 * @throws InvalidInputException
	 */
	public static Cause decodeCauseVal(byte[] data) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("decodeCauseVal:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeCauseVal: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeCauseVal: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		Cause causeData = new Cause();
		int codingStd = (data[0] >> 5) & 0x3;
		causeData.codingStndEnum = CodingStndEnum.fromInt(codingStd);
		int location = (data[0] ) & 0xF;
		causeData.locEnum = LocationEnum.fromInt(location);
		if(data.length > 1){
			int causeVal = data[1] & 0x7F ;
			causeData.causeValEnum = CauseValEnum.fromInt(causeVal);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("decodeCauseVal: Output<--" + causeData.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeCauseVal:Exit");
		}
		return causeData ;
	}
    public String toString(){
		String obj = "locEnum:"+ locEnum + " ,codingStndEnum:"+ codingStndEnum + " causeValEnum:" + causeValEnum ;
		return obj ;
	}
}
