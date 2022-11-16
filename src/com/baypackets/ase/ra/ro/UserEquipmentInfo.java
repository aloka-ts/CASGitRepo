/**
 * FileName:	UserEquipmentInfo.java
 *
 */

package com.baypackets.ase.ra.ro;

/**
 * This class defines the User-Equipment-Info AVP that is part of a 
 * credit control request.
 *
 * Application can use its various methods to fill different fields of 
 * User-Equipment-Info AVP.
 */

public class UserEquipmentInfo {
	private short _ueInfoType;

	private byte[] _ueInfoValue;

	/**
	 * Constructor: Creates a new instance of UserEquipmentInfo.
 	 * @param 	ueiType	- User-Equipment-Info-Type AVP to be set.
	 * 			ueiValue- User-Equipment-Info-Value AVP to be set.
	 *
	 */

	public UserEquipmentInfo(short ueiType, byte[] ueiValue) {
		this._ueInfoType = ueiType;
		this._ueInfoValue = ueiValue;
	}

	/**
	 * This method returns the User-Equipment-Info-Type AVP that is part of
	 * User-Equipment-Info AVP in a CCR.
	 *
	 * @return <code>short</code> object containing User-Equipment-Info AVP.
	 */

	public short getUserEquipmentInfoType() {
		return this._ueInfoType;
	}

	/**
	 * This method returns the User-Equipment-Info-Value AVP that is part of
	 * User-Equipment-Info AVP in a CCR.
	 *
	 * @return byte array object containing User-Equipment-Info-Value AVP.
	 */

	public byte[] getUserEquipmentInfoValue() {
		return this._ueInfoValue;
	}

	/**
	 * This method sets the User-Equipment-Info-Type AVP into a User-Equipment-Info 
	 * AVP that is part of a CCR.
	 *
	 * @param ueInfoType - User-Equipment-Info-Type AVP to be set.
	 */

	public void setUserEquipmentInfoType(short ueInfoType) {
		this._ueInfoType = ueInfoType;
	}

	/**
	 * This method sets the User-Equipment-Info-Value AVP into a User-Equipment-Info 
	 * AVP that is part of a CCR.
	 *
	 * @param ueInfoValue - User-Equipment-Info-Value AVP to be set.
	 */

	public void setUserEquipmentInfoValue(byte[] ueInfoValue) {
		this._ueInfoValue = ueInfoValue;
	}
}

