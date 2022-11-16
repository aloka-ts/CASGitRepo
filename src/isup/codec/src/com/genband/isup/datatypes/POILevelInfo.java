package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.OutsidePOIGradeInfoEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * Used for encoding and decoding of POI Level Info
 * @author vgoel
 *
 */

public class POILevelInfo {

	private static Logger logger = Logger.getLogger(POILevelInfo.class);	
	
	/**
	 * @see OutsidePOIGradeInfoEnum
	 */
	OutsidePOIGradeInfoEnum outsidePOIGradeInfoEnum_LSB ;

	/**
	 * @see OutsidePOIGradeInfoEnum
	 */
	OutsidePOIGradeInfoEnum outsidePOIGradeInfoEnum_MSB ;
	
	
	public OutsidePOIGradeInfoEnum getOutsidePOIGradeInfoEnum_LSB() {
		return outsidePOIGradeInfoEnum_LSB;
	}

	public void setOutsidePOIGradeInfoEnum_LSB(
			OutsidePOIGradeInfoEnum outsidePOIGradeInfoEnumLSB) {
		outsidePOIGradeInfoEnum_LSB = outsidePOIGradeInfoEnumLSB;
	}

	public OutsidePOIGradeInfoEnum getOutsidePOIGradeInfoEnum_MSB() {
		return outsidePOIGradeInfoEnum_MSB;
	}

	public void setOutsidePOIGradeInfoEnum_MSB(
			OutsidePOIGradeInfoEnum outsidePOIGradeInfoEnumMSB) {
		outsidePOIGradeInfoEnum_MSB = outsidePOIGradeInfoEnumMSB;
	}

	/**
	 * This function will encode POI Level Info.
	 * @param outsidePOIGradeInfoEnum_LSB
	 * @param outsidePOIGradeInfoEnum_MSB
	 * @return encoded data byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodePOILevelInfo(OutsidePOIGradeInfoEnum outsidePOIGradeInfoEnum_LSB, OutsidePOIGradeInfoEnum outsidePOIGradeInfoEnum_MSB) throws InvalidInputException {
		
		logger.info("encodePOILevelInfo:Enter");
		byte[] myParms = new byte[1];

		int poiGradeVal_LSB ;
		if(outsidePOIGradeInfoEnum_LSB == null){
			poiGradeVal_LSB = 0;
		}
		else
			poiGradeVal_LSB = outsidePOIGradeInfoEnum_LSB.getCode();
		
		int poiGradeVal_MSB ;
		if(outsidePOIGradeInfoEnum_MSB == null){
			poiGradeVal_MSB = 0;
		}
		else
			poiGradeVal_MSB = outsidePOIGradeInfoEnum_MSB.getCode();
		
		myParms[0] = (byte)(poiGradeVal_MSB << 4 | poiGradeVal_LSB);
		
		
		if(logger.isDebugEnabled())
			logger.debug("encodePOILevelInfo:Encoded POILevelInfo: "+ Util.formatBytes(myParms));
		
		logger.info("encodePOILevelInfo:Exit");
		return myParms;
	}
	
	/**
	 * This function will decode POI Level Info.
	 * @param data
	 * @return object of POILevelInfo
	 * @throws InvalidInputException
	 */
	public static POILevelInfo decodePOILevelInfo(byte[] data) throws InvalidInputException{
		logger.info("decodePOILevelInfo:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodePOILevelInfo: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodePOILevelInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		POILevelInfo poiLevelInfo = new POILevelInfo();
		poiLevelInfo.setOutsidePOIGradeInfoEnum_LSB(OutsidePOIGradeInfoEnum.fromInt(data[0] & 0x0f));
		poiLevelInfo.setOutsidePOIGradeInfoEnum_MSB(OutsidePOIGradeInfoEnum.fromInt(data[0] >> 4 & 0x0f));
		
		if(logger.isDebugEnabled())
			logger.debug("decodePOILevelInfo: Output<--" + poiLevelInfo.toString());
		logger.info("decodePOILevelInfo:Exit");
		return poiLevelInfo ;
	}
	
    public String toString(){
		
		String obj = "outsidePOIGradeInfoEnum_LSB:"+ outsidePOIGradeInfoEnum_LSB + "outsidePOIGradeInfoEnum_MSB:"+ outsidePOIGradeInfoEnum_MSB ;
		return obj ;
	}
}
