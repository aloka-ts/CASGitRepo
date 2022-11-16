/**
 * Filename:	GroupedCustomAVP.java
 * Created On:	13-Oct-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This interface allows the application to define grouped custom AVP,
 * Grouped custom AVP can contains other AVP as their fields.
 *
 */
 	
public class GroupedCustomAVP extends CustomAVP implements CustomAVPMap {

	private CustomAVPMapImpl _avpMap;

	/**
	 * This method returns a new instance of GroupedCustomAVP.
	 *
	 */
	public GroupedCustomAVP(int code, byte flag, int vendorId) {
		super(code, flag, null, vendorId);
		this._avpMap = new CustomAVPMapImpl();
	}

	/**
	 * This method returns an int array containing all the AVP codes associated 
	 * with the grouped AVP.
	 *
	 * @return int[] - int array containing all the AVP codes.
	 */

	public int[] getCustomAVPCodes() {
		return this._avpMap.getCustomAVPCodes();
	}

	/**
	 * This method returns the CustomAVP for a particular code.
	 *
	 */
	public CustomAVP getCustomAVP(int code) {
		return this._avpMap.getCustomAVP(code);
	}

	/**
	 * This method sets the CustomAVP for a particular code in grouped custom AVP.
	 *
	 */
	public CustomAVP setCustomAVP(int code, CustomAVP value) {
		return this._avpMap.setCustomAVP(code, value);
	}

	/**
	 * This method removes the  CustomAVP for a particular code from grouped custom AVP.
	 *
	 */
	public CustomAVP removeCustomAVP(int code) {
		return this._avpMap.removeCustomAVP(code);
	}

	/**
	 * This method returns if the AVP is grouped or not?
	 *
	 * @return boolean - true 
	 */

	public boolean isGrouped() {
		return true;
	}
}
