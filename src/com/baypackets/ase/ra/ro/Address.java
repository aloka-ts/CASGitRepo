/**
 * Filename:	Address.java
 * Created On:	12-Oct-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This class represents data type <code>Address</code> as per Diameter RFC 3588.
 *
 * @author Neeraj Jain
 */

public class Address {
	private String _addr;

	/**
	 * creates a new instance of data type <code>Address</code>.
	 *
	 * @param type - type of the Address as per Diameter RFC 3588.
	 * 		  value- value of the Address as per Diameter RFC 3588.
	 */

	public Address(byte[] type, byte[] value) {
		this._addr = new String(type) + new String(value);
	}

	public Address(String addr) {
		this._addr = addr;
	}

	public String get() {
		return this._addr;
	}

	public void set(String addr) {
		this._addr = addr;
	}
}

