package com.agnity.inapitutcs2.datatypes;

import java.io.Serializable;
import java.util.LinkedList;

import com.agnity.inapitutcs2.enumdata.CarrierInfoNameEnum;

/**
 * Carrier Information Class used in TTCCit
 * @author Mriganka
 *
 */
public class CarrierInformation implements Serializable
{
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

	@Override
	public String toString() {
		String obj = "carrierInfoNameEnum:"+ carrierInfoNameEnum + ", carrierInfoLength:"+ carrierInfoLength + ", carrierInfoSubordinate:" + carrierInfoSubordinate ;
		return obj ;
	}

}
