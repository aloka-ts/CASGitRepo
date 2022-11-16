/**
 * Filename:	DiamIdent.java
 * Created On:	03-Oct-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This class represents data type <code>DiameterIdentity</code> as per Diameter RFC 3588.
 * It contains Fully Qualified Domain Name in form of an <code>String</code>.
 *
 * @author Neeraj Jain
 */

public final class DiamIdent
{
	private String _fqdn;

	/**
	 * This method creates a new instance of data type <code>DiamIdent</code>.
	 *
	 */

	public DiamIdent(String fqdn) {
		this._fqdn = fqdn;
	}

	/**
	 * This method returns the Fully Qualified Domain Name in form of an <code>String</code>.
	 *
 	 * @return <code>String</code> object containing Fully Qualified Domain Name.
	 */

	public String get() {
		return this._fqdn;
	}

	/**
	 * This method sets the Fully Qualified Domain Name in form of an <code>String</code>.
	 *
	 * param fqdn - <code>String</code> object containing Fully Qualified Domain Name.
	 *
	 */

	public void set(String fqdn) {
		this._fqdn = fqdn;
	}

	/**
	 * This method compares a given Fully Qualified Domain Name with the one that is 
	 * already set.
	 *
	 * @return <code>boolean</code> - 'true' if both are same.
	 * 								- 'false' if both are not same.
	 *
	 */

	public boolean equals(String other) {
		if(this._fqdn == other) {
			return true;
		} else if(this._fqdn != null) {
			return this._fqdn.equals(other);
		}

		return false;
	}

	/**
	 * This method returns the hashcode value of the <code>DiamIdent</code> object.
	 *
	 */

	public int hashCode() {
		if(this._fqdn != null) {
			return this._fqdn.hashCode();
		} else {
			return 0;
		}
	}
	
	/**
	 * This method returns the Fully Qualified Domain Name which is in <code>DiamIdent</code>
	 * format in <code>String</code> format.
	 *
 	 */

	public String toString() {
		return this._fqdn;
	}
}
