package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;


/**
 * Used for encoding and decoding of POIChargeAreaInfo
 * @author vgoel
 *
 */
public class POIChargeAreaInfo {

	private static Logger logger = Logger.getLogger(POIChargeAreaInfo.class);	 
	
	/**
	 * Charge Area Information
	 */
	String chargeAreaInfo;
	
	public String getChargeAreaInfo() {
		return chargeAreaInfo;
	}

	public void setChargeAreaInfo(String chargeAreaInfo) {
		this.chargeAreaInfo = chargeAreaInfo;
	}

	/**
	 * This function will encode Charge Area Info.
	 * @param chargeAreaInfo
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodePOIChargeAreaInfo(String chargeAreaInfo) throws InvalidInputException {
		
		logger.info("encodePOIChargeAreaInfo:Enter");
		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(chargeAreaInfo);
		int seqLength = 1 + bcdDigits.length;

		int i = 0;
		byte[] myParms = new byte[seqLength];
				
		// If even no. then set 8th bit 0 otherwise 1
		if (chargeAreaInfo.length() % 2 == 0) {
			myParms[i++] = (byte) (0 << 7);
		} else {
			myParms[i++] = (byte) (1 << 7);
		}
		
		for (int j = 0; j < bcdDigits.length; j++) {
			myParms[i++] = bcdDigits[j];
		}
		
		if(logger.isDebugEnabled())
			logger.debug("encodePOIChargeAreaInfo:Charge Area Info: "+ Util.formatBytes(myParms));
		logger.info("encodePOIChargeAreaInfo:Exit");
		return myParms;
	}
	
	/**
	 * This function will decode Charge Area Info.
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static POIChargeAreaInfo decodePOIChargeAreaInfo(byte[] data) throws InvalidInputException {
		
		logger.info("decodePOIChargeAreaInfo:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodePOIChargeAreaInfo: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodePOIChargeAreaInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		POIChargeAreaInfo poiCharge = new POIChargeAreaInfo();
		int parity = (data[0] >> 7) & 0x1;
		if(data.length > 1){
			poiCharge.chargeAreaInfo = AddressSignal.decodeAdrsSignal(data, 1 , parity);			
		}	
		if(logger.isDebugEnabled())
			logger.debug("decodePOIChargeAreaInfo: Output<--" + poiCharge.toString());
		logger.info("decodePOIChargeAreaInfo:Exit");
		return poiCharge ;
	}
	
	public String toString(){
		
		String obj = "chargeAreaInfo:"+ chargeAreaInfo ;
		return obj ;
	}
}
