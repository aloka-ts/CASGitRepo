
package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;
import com.baypackets.ase.ra.ro.RedirectServer;

public final class RedirectServerImpl implements RedirectServer, Serializable {
	private short _addrType = -1;
	private String _address;

	public RedirectServerImpl(short addrType, String address) {
		this._addrType = addrType;
		this._address = address;
	}

	public short getRedirectServerAddressType() {
		return this._addrType;
	}

	public String getRedirectServerAddress() {
		return this._address;
	}

	public void setRedirectServerAddressType(short addrType) {
		this._addrType = addrType;
	}

	public void setRedirectServerAddress(String addr) {
		this._address = addr;
	}
}
