/**
 * Filename:	SingleCustomAVP.java
 * Created On:	13-Oct-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This interface allows the application to define its own AVP.
 * Single custom AVP does not contain other single AVP as their fileds
 * but these can be used as a field in a grouped custom avp.
 */

public class SingleCustomAVP extends CustomAVP {

	/**
	 * constructor: creates a  new instance of SingleCustomAVP.
	 * @param code - code of custom AVP.
	 * 		  flag - flag of custom AVP.
 	 * 		  data - data of custom AVP.
	 * 		  vendorId - vendorId of custom AVP.
	 */ 

	public SingleCustomAVP(int code, byte flag, Object data, int vendorId) {
		super(code, flag, data, vendorId);
	}

	/**
	 * This method defines whether the user defined custom AVP is grouped
	 * (contains other AVP as its fields) or not. 
	 *
	 * @return boolean - false
	 */

	public boolean isGrouped() {
		return false;
	}
}
