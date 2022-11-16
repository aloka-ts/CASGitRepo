package com.genband.isup.datatypes;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.ChargeCollectionMethodEnum;
import com.genband.isup.enumdata.ChargeRateIndEnum;
import com.genband.isup.enumdata.ChargedPartyTypeEnum;
import com.genband.isup.enumdata.OperationClassEnum;
import com.genband.isup.enumdata.OperationTypeEnum;
import com.genband.isup.enumdata.SignalElementTypeEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * Used for Charging Information when charging info type is 00000011
 * @author vgoel
 *
 */
public class AppRateTrfrChargingInfo {

	private static Logger logger = Logger.getLogger(AppRateTrfrChargingInfo.class);
	
	/**
	 * @see SignalElementTypeEnum
	 */
	SignalElementTypeEnum signalElementTypeEnum ;
	
	/**
	 * It may be 0 (0 means invoke id is not present)
	 */
	int invokeId ;
	
	/**
	 * @see OperationClassEnum
	 * It may be null
	 */
	OperationClassEnum operationClassEnum ;
	
	/**
	 * @see OperationTypeEnum
	 * It may be null
	 */
	OperationTypeEnum operationTypeEnum ;
	
	/**
	 * @see ChargedPartyTypeEnum
	 * It may be null
	 */
	ChargedPartyTypeEnum chargedPartyTypeEnum ;
	
	/**
	 * @see ChargeCollectionMethodEnum
	 * It may be null
	 */
	ChargeCollectionMethodEnum chargeCollectionMethodEnum ;
	
	/**
	 * @see ChargeRateIndEnum
	 */
	ChargeRateIndEnum chargeRateIndEnum ;
	
	/**
	 * Charge/Rate Info (Reserved for future). It may be null
	 */
	byte[] chargeRateInfo ;
	

	public SignalElementTypeEnum getSignalElementTypeEnum() {
		return signalElementTypeEnum;
	}

	public void setSignalElementTypeEnum(SignalElementTypeEnum signalElementTypeEnum) {
		this.signalElementTypeEnum = signalElementTypeEnum;
	}

	public int getInvokeId() {
		return invokeId;
	}

	public void setInvokeId(int invokeId) {
		this.invokeId = invokeId;
	}

	public OperationClassEnum getOperationClassEnum() {
		return operationClassEnum;
	}

	public void setOperationClassEnum(OperationClassEnum operationClassEnum) {
		this.operationClassEnum = operationClassEnum;
	}

	public OperationTypeEnum getOperationTypeEnum() {
		return operationTypeEnum;
	}

	public void setOperationTypeEnum(OperationTypeEnum operationTypeEnum) {
		this.operationTypeEnum = operationTypeEnum;
	}

	public ChargedPartyTypeEnum getChargedPartyTypeEnum() {
		return chargedPartyTypeEnum;
	}

	public void setChargedPartyTypeEnum(ChargedPartyTypeEnum chargedPartyTypeEnum) {
		this.chargedPartyTypeEnum = chargedPartyTypeEnum;
	}

	public ChargeCollectionMethodEnum getChargeCollectionMethodEnum() {
		return chargeCollectionMethodEnum;
	}

	public void setChargeCollectionMethodEnum(
			ChargeCollectionMethodEnum chargeCollectionMethodEnum) {
		this.chargeCollectionMethodEnum = chargeCollectionMethodEnum;
	}

	public ChargeRateIndEnum getChargeRateIndEnum() {
		return chargeRateIndEnum;
	}

	public void setChargeRateIndEnum(ChargeRateIndEnum chargeRateIndEnum) {
		this.chargeRateIndEnum = chargeRateIndEnum;
	}

	public byte[] getChargeRateInfo() {
		return chargeRateInfo;
	}

	public void setChargeRateInfo(byte[] chargeRateInfo) {
		this.chargeRateInfo = chargeRateInfo;
	}
	
	
	/**
	 * This function will encode Application Rate Transfer Charging Information. 
	 * @param chargingInfo
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeAppRateTrfrChargingInfo(AppRateTrfrChargingInfo chargingInfo) throws InvalidInputException
	{
		logger.info("encodeAppRateTrfrChargingInfo:Enter");
				
		LinkedList<Byte> outList = new LinkedList<Byte>();
		
		int signalElementType;
		if(chargingInfo.signalElementTypeEnum == null)
			signalElementType = 0; //spare	
		else
			signalElementType = chargingInfo.signalElementTypeEnum.getCode();
		
		byte byte1 = (byte)(signalElementType & 0x07);
		if(chargingInfo.invokeId != 0) {	//ext1 is 0
			byte byte1a = (byte)(chargingInfo.invokeId & 0x0f);
			if(chargingInfo.operationClassEnum != null || chargingInfo.operationTypeEnum != null) {		//ext1a is 0
				byte byte1b = (byte)(chargingInfo.operationClassEnum.getCode()<<5 | chargingInfo.operationTypeEnum.getCode());
				if(chargingInfo.chargedPartyTypeEnum != null | chargingInfo.chargeCollectionMethodEnum != null){		//ext1b is 0
					byte byte1c = (byte)(chargingInfo.chargedPartyTypeEnum.getCode()<<4 | chargingInfo.chargeCollectionMethodEnum.getCode());
					byte1c = (byte)(byte1c | 0x80);		//ext1c is always 1		
					outList.addFirst(byte1c);
				}
				else
					byte1b = (byte)(byte1b | 0x80);		//ext1b is 1
				outList.addFirst(byte1b);
			}
			else
				byte1a = (byte)(byte1a | 0x80);		//ext1a is 1
			outList.addFirst(byte1a);
		}
		else								//ext1 is 1
			byte1 = (byte)(byte1 | 0x80);
		
		outList.addFirst(byte1);		
		outList.add((byte)(chargingInfo.chargeRateIndEnum.getCode() & 0xff));
		if(chargingInfo.chargeRateInfo != null){
			for(int i=0; i<chargingInfo.chargeRateInfo.length; i++)
				outList.add(chargingInfo.chargeRateInfo[i]);
		}
				
		byte[] data = new byte[outList.size()];
		for(int i=0; i<outList.size(); i++)
			data[i] = outList.get(i);
		
		if(logger.isDebugEnabled())
			logger.debug("encodeAppRateTrfrChargingInfo:Encoded App Rate Trfr Charging Info: " + Util.formatBytes(data));
		logger.info("encodeAppRateTrfrChargingInfo:Exit");
		
		return data;
	}
	
	/**
	 * This function will decode Application Rate Transfer Charging Information.
	 * @param data
	 * @return AppRateTrfrChargingInfo
	 * @throws InvalidInputException
	 */
	public static AppRateTrfrChargingInfo decodeAppRateTrfrChargingInfo(byte[] data) throws InvalidInputException
	{		
		logger.info("decodeAppRateTrfrChargingInfo:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeAppRateTrfrChargingInfo: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeAppRateTrfrChargingInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		int index = 0;
		AppRateTrfrChargingInfo chargingInfo  = new AppRateTrfrChargingInfo();
		chargingInfo.setSignalElementTypeEnum(SignalElementTypeEnum.fromInt(data[index] & 0x07));
		if((data[index++]>>7 & 0x01) == 0) {		//ext1 is 0
			chargingInfo.setInvokeId(data[index] & 0x0f);
			if((data[index++]>>7 & 0x01) == 0) {		//ext1a is 0
				chargingInfo.setOperationTypeEnum(OperationTypeEnum.fromInt(data[index] & 0x1f));
				chargingInfo.setOperationClassEnum(OperationClassEnum.fromInt(data[index] >> 5 & 0x03));
				if((data[index++]>>7 & 0x01) == 0) {		//ext1b is 0
					chargingInfo.setChargeCollectionMethodEnum(ChargeCollectionMethodEnum.fromInt(data[index] & 0x0f));
					chargingInfo.setChargedPartyTypeEnum(ChargedPartyTypeEnum.fromInt(data[index] >> 4 & 0x07));
					index++;
				}
			}
		}
		chargingInfo.setChargeRateIndEnum(ChargeRateIndEnum.fromInt(data[index] & 0xff));
		
		index++;
		byte[] chargerateInfo = new byte[data.length-index];		
		for(int i=0; i<chargerateInfo.length; i++, index++){
			chargerateInfo[i] = data[index];			
		}
		chargingInfo.setChargeRateInfo(chargerateInfo);		

		if(logger.isDebugEnabled())
			logger.debug("decodeAppRateTrfrChargingInfo: Output<--" + chargingInfo.toString());
		logger.info("decodeAppRateTrfrChargingInfo:Exit");
		
		return chargingInfo ;
	}
	
	
	public String toString(){
		
		String obj = "operationClassEnum:"+ operationClassEnum + ", operationTypeEnum:"+ operationTypeEnum + ", chargedPartyTypeEnum:"+ chargedPartyTypeEnum
		 + ", chargeCollectionMethodEnum:"+ chargeCollectionMethodEnum + ", signalElementTypeEnum:"+ signalElementTypeEnum + ", invokeId:"+ invokeId
		 + ", chargeRateIndEnum:"+ chargeRateIndEnum + ", chargeRateInfo:"+ Util.formatBytes(chargeRateInfo);
		return obj ;
	}
	
}
