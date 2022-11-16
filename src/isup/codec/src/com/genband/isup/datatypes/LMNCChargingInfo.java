package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.ChargingInfoIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * Used for LMNC Indication in Charging Information (When charging info type is 00000001)
 * @author vgoel
 *
 */
public class LMNCChargingInfo {

	private static Logger logger = Logger.getLogger(LMNCChargingInfo.class);
	
	ChargingInfoIndEnum chargingInfoIndEnum;
	
	int chargingData;
	
	public ChargingInfoIndEnum getChargingInfoIndEnum() {
		return chargingInfoIndEnum;
	}

	public void setChargingInfoIndEnum(ChargingInfoIndEnum chargingInfoIndEnum) {
		this.chargingInfoIndEnum = chargingInfoIndEnum;
	}

	public int getChargingData() {
		return chargingData;
	}

	public void setChargingData(int chargingData) {
		this.chargingData = chargingData;
	}
	

	/**
	 * This function will encode charging information based on LMNC indication
	 * @param chargingInfo
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeLMNCChgInfo(LMNCChargingInfo chargingInfo) throws InvalidInputException
	{
		logger.info("encodeLMNCChgInfo:Enter");
		
		byte[] data = null;				
		int charginfInfoInd = chargingInfo.chargingInfoIndEnum.getCode();

		if(charginfInfoInd == 2){		//if charging info indicator is 0010 (LMNC)
			data = new byte[3];
			data[0] = (byte) (charginfInfoInd);
			
			int chargingPulseInt = chargingInfo.chargingData*2;
			int L = chargingPulseInt/100;
			int M = (chargingPulseInt-L*100)/10;
			int N = chargingPulseInt-L*100-M*10;
			int C = 0;
			int n = 0;
			while (true){					
				C = 10*n-1-L-M-N;
				if(C >= 0)
					break;
				n++;
			}
			
			data[1] = (byte) (N << 4 | M);
			data[2] = (byte) (L << 4 | C);
		}
		else{
			data = new byte[1];
			data[0] = (byte) (charginfInfoInd);
		}

		
		if(logger.isDebugEnabled())
			logger.debug("encodeLMNCChgInfo:Encoded LMNC Charging Info: " + Util.formatBytes(data));
		logger.info("encodeLMNCChgInfo:Exit");
		
		return data;
	}
	
	/**
	 * This function will decode charging information based on LMNC indication
	 * @param data
	 * @return LMNCChargingInfo
	 * @throws InvalidInputException
	 */
	public static LMNCChargingInfo decodeLMNCChgInfo(byte[] data) throws InvalidInputException
	{		
		logger.info("decodeLMNCChgInfo:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeLMNCChgInfo: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeLMNCChgInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		LMNCChargingInfo chargingInfo = new LMNCChargingInfo();
		chargingInfo.setChargingInfoIndEnum(ChargingInfoIndEnum.fromInt(data[0] & 0x0F));			
		if(chargingInfo.getChargingInfoIndEnum() == ChargingInfoIndEnum.CHARGING_PULSE_INTERVAL_INFO_LMNC){		//LMNC
			int M = data[1] & 0x0F;
			int N = (data[1] >> 4) & 0x0F;
			//int C = data[2] & 0x0F;
			int L= (data[2] >> 4) & 0x0F;
			
			int chargingDataVal = (L*100+M*10+N)/2;
			chargingInfo.setChargingData(chargingDataVal);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("decodeLMNCChgInfo: Output<--" + chargingInfo.toString());
		logger.info("decodeLMNCChgInfo:Exit");
		
		return chargingInfo ;
	}
	
	public String toString(){
		
		String obj = "chargingInfoIndEnum:"+ chargingInfoIndEnum + " ,chargingData:" + chargingData;
		return obj ;
	}
}
