package com.genband.inap.datatypes;

import org.apache.log4j.Logger;

import com.genband.inap.enumdata.InfoDiscriminationIndiEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.util.Util;


/**
 * Used for encoding and decoding of TtcChargeAreaInfo
 * @author vgoel
 *
 */
public class TtcChargeAreaInfo extends AddressSignal { 
	
	/**
	 * @see InfoDiscriminationIndiEnum
	 */
	InfoDiscriminationIndiEnum infoDiscriminationIndiEnum; 
	
	
	private static Logger logger = Logger.getLogger(TtcChargeAreaInfo.class);	 
	 
	
	/**
	 * This function will encode the TTC Charge Area Info. AddrsSignal is mandatory parameter.
	 * Other parameters will be assigned default values if not provided.
	 * @param chargeAreaInfo
	 * @param infoDiscriminationIndiEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeTtcChargeAreaInfo(String chargeAreaInfo, InfoDiscriminationIndiEnum infoDiscriminationIndiEnum) throws InvalidInputException {
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcChargeAreaInfo:Enter");
		}
		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(chargeAreaInfo);
		int seqLength = 1 + bcdDigits.length;
		int i = 0;
		byte[] myParms = new byte[seqLength];
		
		int infoDiscIndi;
		if(infoDiscriminationIndiEnum == null){
			// Assigning default value -Spare
			infoDiscIndi = 2;
		}else {
			infoDiscIndi = infoDiscriminationIndiEnum.getCode();
		}		
		
		// If even no. of Address signal then set 8th bit 0 otherwise 1
		if (chargeAreaInfo.length() % 2 == 0) {
			myParms[i++] = (byte) ((0 << 7) | infoDiscIndi);
		} else {
			myParms[i++] = (byte) ((1 << 7) | infoDiscIndi);
		}
		
		for (int j = 0; j < bcdDigits.length; j++) {
			myParms[i++] = bcdDigits[j];
		}
		if(logger.isDebugEnabled())
			logger.debug("encodeTtcChargeAreaInfo:Encoded Ttc charge area info: " + Util.formatBytes(myParms));
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcChargeAreaInfo:Exit");
		}
		return myParms;
	}
	
	/**
	 * This function will decode TTC Charge Area Info.
	 * @param data
	 * @return object of TtcChargeAreaInfo
	 * @throws InvalidInputException
	 */
	public static TtcChargeAreaInfo decodeTtcChargeAreaInfo(byte[] data) throws InvalidInputException{
		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcChargeAreaInfo:Enter");
		}
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcChargeAreaInfo: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeTtcChargeAreaInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		TtcChargeAreaInfo ttcCAI = new TtcChargeAreaInfo();
		
		int parity = (data[0] >> 7) & 0x1;
		int infoDiscInd = data[0] & 0x7f ;
		ttcCAI.infoDiscriminationIndiEnum = InfoDiscriminationIndiEnum.fromInt(infoDiscInd);
		
		if(data.length >1){
			ttcCAI.addrSignal = AddressSignal.decodeAdrsSignal(data, 1 , parity);
		}		
		
		if(logger.isDebugEnabled())
			logger.debug("decodeTtcChargeAreaInfo: Output<--" + ttcCAI.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcChargeAreaInfo:Exit");
		}
		return ttcCAI ;
	}
	


	public InfoDiscriminationIndiEnum getInfoDiscriminationIndiEnum() {
		return infoDiscriminationIndiEnum;
	}

	public void setInfoDiscriminationIndiEnum(
			InfoDiscriminationIndiEnum infoDiscriminationIndiEnum) {
		this.infoDiscriminationIndiEnum = infoDiscriminationIndiEnum;
	}

	public String toString(){
		
		String obj = "addrSignal:"+ addrSignal + " ,infoDiscriminationIndiEnum:"+ infoDiscriminationIndiEnum ;
		return obj ;
	}

}
