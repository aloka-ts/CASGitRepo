/**
 * Filename:	CustomAVP.java
 * Created On:	13-Oct-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This interfce allows application to define its own custom AVP if the application 
 *  wants to add an AVP which contains the application defined values.
 *
 */

public abstract class CustomAVP {

	public static final short DATA_TYPE_GROUPED			= 0;
	public static final short DATA_TYPE_DOUBLE			= 1;
	public static final short DATA_TYPE_FLOAT			= 2;
	public static final short DATA_TYPE_INT				= 3;
	public static final short DATA_TYPE_LONG			= 4;
	public static final short DATA_TYPE_STRING			= 5;
	public static final short DATA_TYPE_DIAMIDENT		= 6;
	public static final short DATA_TYPE_ADDRESS			= 7;
	public static final short DATA_TYPE_IPFILTERRULE	= 8;

	private int _code;
	private byte _flag;
	private Object _data;
	private short _type = DATA_TYPE_GROUPED;
	private int _vendorId;

	/**
	 * This creates an instance of CustomAVP.
	 */

	public CustomAVP(int code, byte flag, Object data, int vendorId) {
		this._code = code;
		this._flag = flag;
		this._data = data;
		this._vendorId = vendorId;

		if(this._data != null) {
			if(data instanceof Double) {
				this._type = DATA_TYPE_DOUBLE;
			} else if(data instanceof Float) {
				this._type = DATA_TYPE_FLOAT;
			} else if(data instanceof Integer) {
				this._type = DATA_TYPE_INT;
			} else if(data instanceof Long) {
				this._type = DATA_TYPE_LONG;
			} else if(data instanceof String) {
				this._type = DATA_TYPE_STRING;
			} else if(data instanceof DiamIdent) {
				this._type = DATA_TYPE_DIAMIDENT;
			} else if(data instanceof Address) {
				this._type = DATA_TYPE_ADDRESS;
			} else if(data instanceof IPFilterRule) {
				this._type = DATA_TYPE_IPFILTERRULE;
			} else {
				throw new IllegalArgumentException("Custom AVP data type [" +
								Object.class + "] not supported");
			}
		}
	}

	/**
	 * This method returns the application defined AVP code.
	 *
	 * @return int object containing application defined AVP code.
	 */

	public int getAVPCode() {
		return this._code;
	}

	/**
	 * This method returns the application defined AVP flag.
	 *
	 * @return byte object containing application defined AVP flag.
	 */

	public byte getAVPFlag() {
		return this._flag;
	}

	/**
	 * This method returns the application defined AVP Type.
	 *
	 * @return short object containing application defined AVP Type..
	 */

	public short getAVPType() {
		return this._type;
	}

	/**
	 * This method returns the application defined AVP data.
	 *
	 * @return Object object containing the application defined AVP data.
	 */

	public Object getAVPData() {
		return this._data;
	}

	/**
	 * This method returns the vendor Id.
	 *
	 * @return int object containing Vendor-Id.
	 */

	public int getVendorId() {
		return this._vendorId;
	}

	/**
	 * This method defines if this application defined  custom AVP is a single AVP
	 * or contains other application defined AVP as it's fileds.
	 *
	 * @return boolean - 'true' if custom AVP is grouped 
	 					 'false' is custom AVP is single.	
	 */
	 
	public abstract boolean isGrouped();
}
