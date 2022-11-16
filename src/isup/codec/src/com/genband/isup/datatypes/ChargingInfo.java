package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.ChargingInfoCatEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * This class have parameters for Charging Information.
 * Exactly one of the fields will be not null based on ChargingInfoCategory. 
 * @author vgoel
 *
 */
public class ChargingInfo {
	
	/**
	 * Charging Info Category is 00000001
	 * @see LMNCChargingInfo
	 */
	LMNCChargingInfo lmncChargingInfo ;
	
	/**
	 * Charging Info Category is 00000011
	 * @see AppRateTrfrChargingInfo
	 */
	AppRateTrfrChargingInfo appRateTrfrChargingInfo ;
	
	/**
	 * Charging Info Category is 00000100
	 * @see NttPhsChargingInfo
	 */
	NttPhsChargingInfo nttPhsChargingInfo ;
	
	/**
	 * Charging Info Category is 1111110
	 * @see ChgRateTrfrChargingInfo
	 */
	ChgRateTrfrChargingInfo chgRateTrfrChargingInfo ;
	
	/**
	 * Charging Info Category is 00000010
	 * @see FlexibleChgChargingInfo
	 */
	FlexibleChgChargingInfo flexibleChgChargingInfo ;
	
	
	public FlexibleChgChargingInfo getFlexibleChgChargingInfo() {
		return flexibleChgChargingInfo;
	}

	public void setFlexibleChgChargingInfo(
			FlexibleChgChargingInfo flexibleChgChargingInfo) {
		this.flexibleChgChargingInfo = flexibleChgChargingInfo;
	}

	public ChgRateTrfrChargingInfo getChgRateTrfrChargingInfo() {
		return chgRateTrfrChargingInfo;
	}

	public void setChgRateTrfrChargingInfo(
			ChgRateTrfrChargingInfo chgRateTrfrChargingInfo) {
		this.chgRateTrfrChargingInfo = chgRateTrfrChargingInfo;
	}

	public NttPhsChargingInfo getNttPhsChargingInfo() {
		return nttPhsChargingInfo;
	}

	public void setNttPhsChargingInfo(NttPhsChargingInfo nttPhsChargingInfo) {
		this.nttPhsChargingInfo = nttPhsChargingInfo;
	}

	public AppRateTrfrChargingInfo getAppRateTrfrChargingInfo() {
		return appRateTrfrChargingInfo;
	}

	public void setAppRateTrfrChargingInfo(
			AppRateTrfrChargingInfo appRateTrfrChargingInfo) {
		this.appRateTrfrChargingInfo = appRateTrfrChargingInfo;
	}

	public LMNCChargingInfo getLmncChargingInfo() {
		return lmncChargingInfo;
	}

	public void setLmncChargingInfo(LMNCChargingInfo lmncChargingInfo) {
		this.lmncChargingInfo = lmncChargingInfo;
	}

	private static Logger logger = Logger.getLogger(ChargingInfo.class);


	/**
	 * This function will encode charging information.
	 * Exactly one field of chargingInfo input wil be non-null, others will be null (based on chargingInfoCategory) 
	 * @param chargingInfo
	 * @param chargingInfoCatEnum
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeChargingInfo(ChargingInfo chargingInfo, ChargingInfoCatEnum chargingInfoCatEnum) throws InvalidInputException
	{
		logger.info("encodeChargingInfo:Enter");
		
		byte[] data = null;				
		int chargingInfoCat;
		if(chargingInfoCatEnum == null){
			logger.error("encodeChargingInfo: InvalidInputException(chargingInfoCatEnum is null)");
			throw new InvalidInputException("chargingInfoCatEnum is null");
		}
		chargingInfoCat = chargingInfoCatEnum.getCode();
			
		if(chargingInfoCat == 1){		//if charging info type param equals 00000001
			data = LMNCChargingInfo.encodeLMNCChgInfo(chargingInfo.lmncChargingInfo);
		}
		else if(chargingInfoCat == 3){		//if charging info type param equals 00000011
			data = AppRateTrfrChargingInfo.encodeAppRateTrfrChargingInfo(chargingInfo.appRateTrfrChargingInfo);
		}
		else if(chargingInfoCat == 4){		//if charging info type param equals 00000100
			data = NttPhsChargingInfo.encodeNttPhsChargingInfo(chargingInfo.nttPhsChargingInfo);
		}
		else if(chargingInfoCat == 254){		//if charging info type param equals 11111110
			data = ChgRateTrfrChargingInfo.encodeChgRateTrfrChargingInfo(chargingInfo.chgRateTrfrChargingInfo);
		}
		else if(chargingInfoCat == 2){		//if charging info type param equals 00000010
			data = FlexibleChgChargingInfo.encodeFlexibleChargingInfo(chargingInfo.flexibleChgChargingInfo);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("encodeChargingInfo:Encoded Charging Info: " + Util.formatBytes(data));
		logger.info("encodeChargingInfo:Exit");
		
		return data;
	}
	
	/**
	 * This function will decode charging information
	 * @param data
	 * @param chargingInfoCatEnum
	 * @return ChargingInfo
	 * @throws InvalidInputException
	 */
	public static ChargingInfo decodeChargingInfo(byte[] data, ChargingInfoCatEnum chargingInfoCatEnum) throws InvalidInputException
	{		
		logger.info("decodeChargingInfo:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeChargingInfo: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeChargingInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		int chargingInfoCat;
		if(chargingInfoCatEnum == null){
			logger.error("decodeChargingInfo: InvalidInputException(chargingInfoCatEnum is null)");
			throw new InvalidInputException("chargingInfoCatEnum is null");
		}
		chargingInfoCat = chargingInfoCatEnum.getCode();
		
		ChargingInfo chargingInfo  = new ChargingInfo();;
		if(chargingInfoCat == 1){		//if charging info param equals 00000001
			chargingInfo.setLmncChargingInfo(LMNCChargingInfo.decodeLMNCChgInfo(data));
		}
		else if(chargingInfoCat == 3){		//if charging info type param equals 00000011
			chargingInfo.setAppRateTrfrChargingInfo(AppRateTrfrChargingInfo.decodeAppRateTrfrChargingInfo(data));
		}
		else if(chargingInfoCat == 4){		//if charging info type param equals 00000100
			chargingInfo.setNttPhsChargingInfo(NttPhsChargingInfo.decodeNttPhsChargingInfo(data));
		}
		else if(chargingInfoCat == 254){		//if charging info type param equals 11111110
			chargingInfo.setChgRateTrfrChargingInfo(ChgRateTrfrChargingInfo.decodeChgRateTrfrChargingInfo(data));
		}
		else if(chargingInfoCat == 2){		//if charging info type param equals 00000010
			chargingInfo.setFlexibleChgChargingInfo(FlexibleChgChargingInfo.decodeFlexibleChargingInfo(data));
		}
		
		if(logger.isDebugEnabled())
			logger.debug("decodeChargingInfo: Output<--" + chargingInfo.toString());
		logger.info("decodeChargingInfo:Exit");
		
		return chargingInfo ;
	}
	
	
	public String toString(){
		
		String obj = "lmncChargingInfo:"+ lmncChargingInfo + ", AppRateTrfrChargingInfo:" + appRateTrfrChargingInfo + ", nttPhsChargingInfo:" + nttPhsChargingInfo
		+ ", chgRateTrfrChargingInfo:" + chgRateTrfrChargingInfo + ", flexibleChgChargingInfo:" + flexibleChgChargingInfo;
		return obj ;
	}	
}

