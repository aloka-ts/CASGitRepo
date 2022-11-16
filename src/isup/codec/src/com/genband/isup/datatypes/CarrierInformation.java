package com.genband.isup.datatypes;

import java.util.Arrays;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import com.genband.isup.enumdata.CarrierInfoNameEnum;
import com.genband.isup.enumdata.CarrierInfoSubordinateEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

/**
 * Carrier Information Class used in TTCCit
 * @author vgoel
 *
 */
public class CarrierInformation
{
	private static Logger logger = Logger.getLogger(CarrierInformation.class);	
	
	/**
	 * @see CarrierInfoNameEnum
	 */
	  CarrierInfoNameEnum carrierInfoNameEnum;
	
	/**
	 * Length of Carrier Information Field
	 */
	int carrierInfoLength ;

	/**
	 * Multiple Carrier Information Subordinate fields
	 */
	 LinkedList<CarrierInfoSubordinate> carrierInfoSubordinate ;
	
	public CarrierInfoNameEnum getCarrierInfoNameEnum() {
		return carrierInfoNameEnum;
	}

	public void setCarrierInfoNameEnum(CarrierInfoNameEnum carrierInfoNameEnum) {
		this.carrierInfoNameEnum = carrierInfoNameEnum;
	}

	public int getCarrierInfoLength() {
		return carrierInfoLength;
	}

	public void setCarrierInfoLength(int carrierInfoLength) {
		this.carrierInfoLength = carrierInfoLength;
	}

	public LinkedList<CarrierInfoSubordinate> getCarrierInfoSubordinate() {
		return carrierInfoSubordinate;
	}

	public void setCarrierInfoSubordinate(
			LinkedList<CarrierInfoSubordinate> carrierInfoSubordinate) {
		this.carrierInfoSubordinate = carrierInfoSubordinate;
	}
	
	/**
	 * This function will decode Carrier Identification.
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static CarrierInformation decodeCarrierInformationTransfer(byte[] data) throws InvalidInputException {
		
		logger.info("decodeCarrierInformationTransfer");
		if(logger.isDebugEnabled())
			logger.debug("decodeCarrierIdentificationTransfer: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeCarrierIdentificationTransfer: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		// Check Carrier Information Name
		CarrierInformation carrierInfo = new CarrierInformation();
		carrierInfo.carrierInfoSubordinate = new LinkedList<CarrierInfoSubordinate>();
		
		int carrierInfoLen =0;
		carrierInfo.carrierInfoNameEnum = CarrierInfoNameEnum.fromInt(data[1]&0xFF);
		carrierInfoLen = (data[2]&0xFF);
		
		if(carrierInfo.carrierInfoNameEnum == CarrierInfoNameEnum.OLEC ||
				carrierInfo.carrierInfoNameEnum == CarrierInfoNameEnum.TLEC) {
			int cicFrom = 5;
			int cicTo = cicFrom + data[4]&0xFF;
			byte[] cicByte = Arrays.copyOfRange(data, cicFrom, cicTo);
			CarrierIdentificationCode cic = CarrierIdentificationCode.decodeCarrierIdentCode(cicByte);
			
			CarrierInfoSubordinate sub = new CarrierInfoSubordinate();
			sub.setCarrierIdentificationCode(cic);
			sub.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.CARRIER_IDENT_CODE);
			sub.setCarrierInfoSubOrdinateLength(cicByte.length);
			carrierInfo.carrierInfoSubordinate.add(sub);
			
			if(data.length <= cicTo) {
				return carrierInfo;
			}
			
			int poiFrom = cicTo + 2;
			int poiTo = poiFrom + data[poiFrom-1]&0xFF;
			byte[] poiByte = Arrays.copyOfRange(data, poiFrom, poiTo);
			
			// Check for the code before calling decode. 
			// POI-Charge Area Information = 0xFD
			// POI-Level Information = 0xFC
			int poiId = poiFrom-2;
			int poiIdVal = data[poiId] &0xFF;
			if(CarrierInfoSubordinateEnum.fromInt((data[poiFrom-2]&0xFF)) == CarrierInfoSubordinateEnum.POI_LEVEL_INFO)
			{
				POILevelInfo poi = POILevelInfo.decodePOILevelInfo(poiByte);
				
				CarrierInfoSubordinate poiLevelSub = new CarrierInfoSubordinate();
				poiLevelSub.setPoiLevelInfo(poi);
				poiLevelSub.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.POI_LEVEL_INFO);
				carrierInfo.carrierInfoSubordinate.add(poiLevelSub);
			}
			else if (CarrierInfoSubordinateEnum.fromInt((data[poiFrom-2]&0xFF)) == CarrierInfoSubordinateEnum.POI_CHARGE_AREA_INFO)
			{
				POIChargeAreaInfo poiChrgInf = POIChargeAreaInfo.decodePOIChargeAreaInfo(poiByte);
				
				CarrierInfoSubordinate poiChrgInfSub = new CarrierInfoSubordinate();
				poiChrgInfSub.setPoiChargeAreaInfo(poiChrgInf);
				poiChrgInfSub.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.POI_CHARGE_AREA_INFO);
				carrierInfo.carrierInfoSubordinate.add(poiChrgInfSub);
			}

		}
		else if(carrierInfo.carrierInfoNameEnum == CarrierInfoNameEnum.CHOSEN_INTER_EXCHANGE ||
				carrierInfo.carrierInfoNameEnum == CarrierInfoNameEnum.TRANSIT) {
			int cicFrom = 5;
			int cicTo = cicFrom + data[4]&0xFF;
			byte[] cicByte = Arrays.copyOfRange(data, cicFrom, cicTo);
			CarrierIdentificationCode cic = CarrierIdentificationCode.decodeCarrierIdentCode(cicByte);
			
			CarrierInfoSubordinate sub = new CarrierInfoSubordinate();
			sub.setCarrierIdentificationCode(cic);
			sub.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.CARRIER_IDENT_CODE);
			sub.setCarrierInfoSubOrdinateLength(cicByte.length);
			carrierInfo.carrierInfoSubordinate.add(sub);
			
			
			int poiChargeFrom = cicTo + 2;
			int poiChargeTo = poiChargeFrom + data[poiChargeFrom-1]&0xFF;
			byte[] poiChargeByte = Arrays.copyOfRange(data, poiChargeFrom, poiChargeTo);
			POIChargeAreaInfo poiCharge = POIChargeAreaInfo.decodePOIChargeAreaInfo(poiChargeByte);
			
			CarrierInfoSubordinate poiChargeSub = new CarrierInfoSubordinate();
			poiChargeSub.setPoiChargeAreaInfo(poiCharge);
			poiChargeSub.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.POI_CHARGE_AREA_INFO);
			carrierInfo.carrierInfoSubordinate.add(poiChargeSub);
			
			
			int poiFrom = poiChargeTo + 2;
			int poiTo = poiFrom + data[poiFrom-1]&0xFF;
			byte[] poiByte = Arrays.copyOfRange(data, poiFrom, poiTo);
			POILevelInfo poi = POILevelInfo.decodePOILevelInfo(poiByte);
			
			CarrierInfoSubordinate poiLevelSub = new CarrierInfoSubordinate();
			poiLevelSub.setPoiLevelInfo(poi);
			poiLevelSub.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.POI_LEVEL_INFO);
			carrierInfo.carrierInfoSubordinate.add(poiLevelSub);
		}
		else if(carrierInfo.carrierInfoNameEnum == CarrierInfoNameEnum.DONOR_SCP ||
				carrierInfo.carrierInfoNameEnum == CarrierInfoNameEnum.RECEPIENT_SCP ||
				carrierInfo.carrierInfoNameEnum == CarrierInfoNameEnum.SCP) {
			int cicFrom = 5;
			int cicTo = data[4]&0xFF;
			byte[] cicByte = Arrays.copyOfRange(data, cicFrom, cicTo);
			CarrierIdentificationCode cic = CarrierIdentificationCode.decodeCarrierIdentCode(cicByte);
			
			CarrierInfoSubordinate sub = new CarrierInfoSubordinate();
			sub.setCarrierIdentificationCode(cic);
			sub.setCarrierInfoSubordinateEnum(CarrierInfoSubordinateEnum.CARRIER_IDENT_CODE);
			sub.setCarrierInfoSubOrdinateLength(cicByte.length);
			carrierInfo.carrierInfoSubordinate.add(sub);
		}
		else {
			throw new InvalidInputException("Unknown Carrier Information Name");
		}
		return carrierInfo;
	}

	@Override
	public String toString() {
		String obj = "carrierInfoNameEnum:"+ carrierInfoNameEnum + ", carrierInfoLength:"+ carrierInfoLength + ", carrierInfoSubordinate:" + carrierInfoSubordinate ;
		return obj ;
	}

}
