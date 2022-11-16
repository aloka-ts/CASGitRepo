package com.genband.inap.datatypes;

import java.io.Serializable;
import com.genband.inap.enumdata.CarrierInfoSubordinateEnum;

/**
 * Carrier Information Subordinate Class
 * It will contain suboridnate type, length and its value.
 * Out of 3 subordinate type, only one will be non-null and others will be null.
 * @author vgoel
 *
 */
public class CarrierInfoSubordinate implements Serializable
{
	/**
	 * @see CarrierInfoSubordinateEnum
	 */
	CarrierInfoSubordinateEnum carrierInfoSubordinateEnum ;
	
	/**
	 * Length of Carrier Information Subordinate Field
	 */
	int carrierInfoSubOrdinateLength ;

	/**
	 * @see POILevelInfo
	 */
	POILevelInfo poiLevelInfo ;
	
	/**
	 * @see POIChargeAreaInfo
	 */
	POIChargeAreaInfo poiChargeAreaInfo ;
	
	/**
	 * @see CarrierIdentificationCode
	 */
	CarrierIdentificationCode carrierIdentificationCode ;
	
	
	public CarrierInfoSubordinateEnum getCarrierInfoSubordinateEnum() {
		return carrierInfoSubordinateEnum;
	}

	public void setCarrierInfoSubordinateEnum(
			CarrierInfoSubordinateEnum carrierInfoSubordinateEnum) {
		this.carrierInfoSubordinateEnum = carrierInfoSubordinateEnum;
	}

	public POILevelInfo getPoiLevelInfo() {
		return poiLevelInfo;
	}

	public void setPoiLevelInfo(POILevelInfo poiLevelInfo) {
		this.poiLevelInfo = poiLevelInfo;
	}

	public POIChargeAreaInfo getPoiChargeAreaInfo() {
		return poiChargeAreaInfo;
	}

	public void setPoiChargeAreaInfo(POIChargeAreaInfo poiChargeAreaInfo) {
		this.poiChargeAreaInfo = poiChargeAreaInfo;
	}

	public CarrierIdentificationCode getCarrierIdentificationCode() {
		return carrierIdentificationCode;
	}

	public void setCarrierIdentificationCode(
			CarrierIdentificationCode carrierIdentificationCode) {
		this.carrierIdentificationCode = carrierIdentificationCode;
	}
	
	@Override
	public String toString() {
		String obj = "carrierInfoSubordinateEnum:"+ carrierInfoSubordinateEnum + ", carrierInfoSubOrdinateLength:"+ carrierInfoSubOrdinateLength
		+ ", poiLevelInfo:" + poiLevelInfo + ", poiChargeAreaInfo:" + poiChargeAreaInfo + ", carrierIdentificationCode:" + carrierIdentificationCode ;
		return obj ;
	}
	
}
