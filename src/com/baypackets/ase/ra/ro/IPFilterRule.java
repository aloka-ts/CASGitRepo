/**
 * Filename:	IPFilterRule.java
 * Created On:	03-Oct-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This class represents data type <code>DiameterIdentity</code> as per Diameter RFC 3588.
 *
 * @author Neeraj Jain
 */


public final class IPFilterRule
{
	private byte[] _bytes;

	public IPFilterRule(byte[] bytes) {
		this._bytes = bytes;
	}

	public byte[] get() {
		return this._bytes;
	}

	public void set(byte[] bytes) {
		this._bytes = bytes;
	}

	public boolean equals(byte[] other) {
		if(this._bytes == other) {
			return true;
		} else if((this._bytes != null) && (other != null)) {
			if(this._bytes.length != other.length) {
				return false;
			}

			for(int i=0; i < this._bytes.length; ++i) {
				if(this._bytes[i] != other[i]) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	public String toString() {
		return new String(this._bytes);
	}
}
