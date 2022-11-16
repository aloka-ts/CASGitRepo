package com.genband.isup.datatypes;

import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.ChgRateInfoCatEnum;
import com.genband.isup.enumdata.UnitRateIndEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * Used for Charging Information when charging info type is 11111110
 * @author vgoel
 *
 */
public class ChgRateTrfrChargingInfo {

	private static Logger logger = Logger.getLogger(ChgRateTrfrChargingInfo.class);
	private static int ASCII_VALUE = 48;
	
	/**
	 * @see UnitRateIndEnum
	 */
	UnitRateIndEnum unitRateIndEnum ;
	
	/**
	 * @see ChargingInfo
	 */
	ChgRateInfoCatEnum chgRateInfoCatEnum ;

	/**
	 * Initial Lump Call Rate
	 */
	int initialCallRate ;

	/**
	 * List of charging periods i.e. first charging period, second charging period etc. 
	 */
	LinkedList<Float> chargingPeriods ;

	
	public int getInitialCallRate() {
		return initialCallRate;
	}

	public void setInitialCallRate(int initialCallRate) {
		this.initialCallRate = initialCallRate;
	}
	
	public LinkedList<Float> getChargingPeriods() {
		return chargingPeriods;
	}

	public void setChargingPeriods(LinkedList<Float> chargingPeriods) {
		this.chargingPeriods = chargingPeriods;
	}

	public UnitRateIndEnum getUnitRateIndEnum() {
		return unitRateIndEnum;
	}

	public void setUnitRateIndEnum(UnitRateIndEnum unitRateIndEnum) {
		this.unitRateIndEnum = unitRateIndEnum;
	}

	public ChgRateInfoCatEnum getChgRateInfoCatEnum() {
		return chgRateInfoCatEnum;
	}

	public void setChgRateInfoCatEnum(ChgRateInfoCatEnum chgRateInfoCatEnum) {
		this.chgRateInfoCatEnum = chgRateInfoCatEnum;
	}

	/**
	 * This function will encode Charge Rate Transfer charging information.
	 * @param chgRateTrfrChargingInfo
	 * @return byte[]
	 * @throws InvalidInputException
	 */
	public static byte[] encodeChgRateTrfrChargingInfo(ChgRateTrfrChargingInfo chgRateTrfrChargingInfo) throws InvalidInputException
	{
		logger.info("eencodeChgRateTrfrChargingInfo:Enter");
			
		LinkedList<Byte> outList = new LinkedList<Byte>();
		
		if(chgRateTrfrChargingInfo.unitRateIndEnum == UnitRateIndEnum.NO_INDICATION) {
			outList.add((byte)chgRateTrfrChargingInfo.unitRateIndEnum.getCode());
			outList.add((byte)(ChgRateInfoCatEnum.NO_RATE_INFO.getCode() | (1<<7)));
		}
		else {
			outList.add((byte)chgRateTrfrChargingInfo.unitRateIndEnum.getCode());
			outList.add((byte)chgRateTrfrChargingInfo.chgRateInfoCatEnum.getCode());
			
			int len;
			if(chgRateTrfrChargingInfo.chargingPeriods != null)
				len = 2 + chgRateTrfrChargingInfo.chargingPeriods.size()*3;		// 2 octets for initial call rate, 3 for each charging periods
			else
				len = 2;
			outList.add((byte)len);
			outList.add((byte)(chgRateTrfrChargingInfo.initialCallRate/10 + ASCII_VALUE));
			outList.add((byte)(chgRateTrfrChargingInfo.initialCallRate%10 + ASCII_VALUE));
			if(chgRateTrfrChargingInfo.chargingPeriods != null)
				for(int i=0; i<chgRateTrfrChargingInfo.chargingPeriods.size(); i++) {
					Float chargingPeriod = (float)(chgRateTrfrChargingInfo.chargingPeriods.get(i)*2);
					int A = chargingPeriod.intValue()/100;
					int B = (chargingPeriod.intValue()-A*100)/10;
					int C = chargingPeriod.intValue()-A*100-B*10;
					outList.add((byte)(A + ASCII_VALUE));
					outList.add((byte)(B + ASCII_VALUE));
					outList.add((byte)(C + ASCII_VALUE));
				}
		}
				
		byte[] data = new byte[outList.size()];
		for(int i=0; i<outList.size(); i++)
			data[i] = outList.get(i);		
		
		if(logger.isDebugEnabled())
			logger.debug("encodeChgRateTrfrChargingInfo:Encoded Charge Rate Transfer Charging Info: " + Util.formatBytes(data));
		logger.info("encodeChgRateTrfrChargingInfo:Exit");
		
		return data;
	}
	
	/**
	 * This function will decode Charge Rate Transfer charging information.
	 * @param data
	 * @return ChgRateTrfrChargingInfo
	 * @throws InvalidInputException
	 */
	public static ChgRateTrfrChargingInfo decodeChgRateTrfrChargingInfo(byte[] data) throws InvalidInputException
	{		
		logger.info("decodeChgRateTrfrChargingInfo:Enter");
		if(logger.isDebugEnabled())
			logger.debug("decodeChgRateTrfrChargingInfo: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeChgRateTrfrChargingInfo: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		ChgRateTrfrChargingInfo chargingInfo = new ChgRateTrfrChargingInfo();
		chargingInfo.setUnitRateIndEnum(UnitRateIndEnum.fromInt(data[0] & 0xff));
		if(chargingInfo.getUnitRateIndEnum() == UnitRateIndEnum.NO_INDICATION)
			chargingInfo.setChgRateInfoCatEnum(ChgRateInfoCatEnum.NO_RATE_INFO);
		else {
			chargingInfo.setChgRateInfoCatEnum(ChgRateInfoCatEnum.fromInt(data[1] & 0x7f));
			
			int len = data[2] & 0xff;
			chargingInfo.setInitialCallRate((data[3]-ASCII_VALUE)*10 + (data[4]-ASCII_VALUE));
			LinkedList<Float> chgPeriods = new LinkedList<Float>();
			for(int i=2+3; i<len+3; i++) {		//2 octets for initial lump call rate, 3 octets for URI, category, length
				chgPeriods.add((float)((data[i++]-ASCII_VALUE)*100 + (data[i++]-ASCII_VALUE)*10 + (data[i]-ASCII_VALUE))/2);
			}
			chargingInfo.setChargingPeriods(chgPeriods);
		}
		
		if(logger.isDebugEnabled())
			logger.debug("decodeChgRateTrfrChargingInfo: Output<--" + chargingInfo.toString());
		logger.info("decodeChgRateTrfrChargingInfo:Exit");
		
		return chargingInfo ;
	}
	
	
	public String toString(){
		
		String obj = "unitRateIndEnum:"+ unitRateIndEnum + ", chgRateInfoCatEnum:" + chgRateInfoCatEnum + ", initialCallRate:" + initialCallRate
		+ ", chargingPeriods:" + chargingPeriods;
		return obj ;
	}
	
}
