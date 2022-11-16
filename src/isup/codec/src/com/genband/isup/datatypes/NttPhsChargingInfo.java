package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.SignalElementTypeEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * Used for Charging Information when charging info type is 00000100
 * @author vgoel
 *
 */
public class NttPhsChargingInfo {

	private static Logger logger = Logger.getLogger(NttPhsChargingInfo.class);
	
	/**
	 * @see SignalElementTypeEnum
	 */
	SignalElementTypeEnum signalElementTypeEnum	;
	
	String calledAreaInformation ;

	public SignalElementTypeEnum getSignalElementTypeEnum() {
		return signalElementTypeEnum;
	}

	public void setSignalElementTypeEnum(SignalElementTypeEnum signalElementTypeEnum) {
		this.signalElementTypeEnum = signalElementTypeEnum;
	}

	public String getCalledAreaInformation() {
		return calledAreaInformation;
	}

	public void setCalledAreaInformation(String calledAreaInformation) {
		this.calledAreaInformation = calledAreaInformation;
	}
	
	
	/**
	 * This function will encode NTT network connection type PHS charging information.
	 * @param nttPhsChargingInfo
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeNttPhsChargingInfo(NttPhsChargingInfo nttPhsChargingInfo) throws InvalidInputException
	{
		logger.info("encodeNttPhsChargingInfo:Enter");
		
		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(nttPhsChargingInfo.calledAreaInformation);
		byte[] data = new byte[bcdDigits.length + 2];
		
		int signalElementType;
		if(nttPhsChargingInfo.signalElementTypeEnum == null)
			signalElementType = 0; //spare	
		else
			signalElementType = nttPhsChargingInfo.signalElementTypeEnum.getCode();
		
		data[0] = (byte)(signalElementType & 0x03);
		if(nttPhsChargingInfo.calledAreaInformation.length() % 2 == 0)
			data[1] = (byte)(0 << 7);
		else if(nttPhsChargingInfo.calledAreaInformation.length() % 2 == 1)
			data[1] = (byte)(1 << 7);
		
		for (int j=0, i=2; j < bcdDigits.length; j++, i++) {
			data[i] = bcdDigits[j];
		}
		
		if(logger.isDebugEnabled())
			logger.debug("encodeNttPhsChargingInfo:Encoded NTT PHS Charging Info: " + Util.formatBytes(data));
		logger.info("encodeNttPhsChargingInfo:Exit");
		
		return data;
	}
	
	/**
	 * This function will decode NTT network connection type PHS charging information. 
	 * @param data
	 * @return NttPhsChargingInfo
	 * @throws InvalidInputException
	 */
	public static NttPhsChargingInfo decodeNttPhsChargingInfo(byte[] data) throws InvalidInputException
	{		
		logger.info("decodeNttPhsChargingInfo:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeNttPhsChargingInfo: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeNttPhsChargingInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		NttPhsChargingInfo chargingInfo = new NttPhsChargingInfo();
		chargingInfo.setSignalElementTypeEnum(SignalElementTypeEnum.fromInt(data[0] & 0x03));
		
		if(data.length > 2){
			chargingInfo.setCalledAreaInformation(AddressSignal.decodeAdrsSignal(data, 2 , (data[1] >> 7) & 0x1));			
		}
		
		if(logger.isDebugEnabled())
			logger.debug("decodeNttPhsChargingInfo: Output<--" + chargingInfo.toString());
		logger.info("decodeNttPhsChargingInfo:Exit");
		
		return chargingInfo ;
	}
	
	
	public String toString(){
		
		String obj = "signalElementTypeEnum:"+ signalElementTypeEnum + ", calledAreaInformation:" + calledAreaInformation;
		return obj ;
	}
}
