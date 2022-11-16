/**
 * Filename:	ApplicationServerInformationImpl.java
 * Created On:	12-Oct-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.io.Serializable;

import com.baypackets.ase.ra.ro.ApplicationServerInformation;
import com.baypackets.ase.ra.ro.util.UnmodifiableIterator;

public class ApplicationServerInformationImpl implements ApplicationServerInformation , Serializable {

	private com.condor.chargingcommon.AppServerInfo _asInfo;
	private List _appCdPtyAddresses;
	private boolean _readonly = false;

	public ApplicationServerInformationImpl(String appServer, List appProvidedCdPtyAddresses) {
		this._asInfo = new com.condor.chargingcommon.AppServerInfo();
		this._asInfo.setAppServer(appServer);

		if(appProvidedCdPtyAddresses != null) { 
			if(appProvidedCdPtyAddresses.size() > 1) {
				throw new IllegalStateException(
					"Stack supports only one Application-Provided-Called-Party-Address");
			} else if(appProvidedCdPtyAddresses.size() == 1) {
				this._appCdPtyAddresses = appProvidedCdPtyAddresses;
			}
		}
	}

	public ApplicationServerInformationImpl(com.condor.chargingcommon.AppServerInfo asInfo) {
		this._asInfo = asInfo;

		String addr = this._asInfo.getAppProvidedCldPrtyAdrs();
		if(addr != null) {
			this._appCdPtyAddresses = new LinkedList();
			this._appCdPtyAddresses.add(addr);
		}

		this._readonly = true;
	}

	public String getApplicationServer() {
		return this._asInfo.getAppServer();
	}

	public void setApplicationServer(String appServer) {
		this.checkReadOnly();

		this._asInfo.setAppServer(appServer);
	}

	public Iterator getApplicationProvidedCalledPartyAddresses() {
		if(this._appCdPtyAddresses != null) {
			return new UnmodifiableIterator(this._appCdPtyAddresses.iterator());
		} else {
			return null;
		}
	}

	public void addApplicationProvidedCalledPartyAddress(String addr) {
		this.checkReadOnly();

		if(this._appCdPtyAddresses == null) {
			this._appCdPtyAddresses = new LinkedList();
			this._appCdPtyAddresses.add(addr);
		} else if(this._appCdPtyAddresses.size() == 0) {
			this._appCdPtyAddresses.add(addr);
		} else {
			throw new IllegalStateException(
				"Stack supports only one Application-Provided-Called-Party-Address");
		}
	}

	public boolean removeApplicationProvidedCalledPartyAddress(String addr) {
		this.checkReadOnly();

		if(this._appCdPtyAddresses != null) {
			return this._appCdPtyAddresses.remove(addr);
		} else {
			return false;
		}
	}

	public com.condor.chargingcommon.AppServerInfo getStackImpl() {
		if(this._readonly) {
			return this._asInfo;
		}

		this._readonly = true;

		if(this._appCdPtyAddresses != null && this._appCdPtyAddresses.size() == 1) {
			this._asInfo.setAppProvidedCldPrtyAdrs((String)this._appCdPtyAddresses.get(0));
		}

		return this._asInfo;
	}

	private void checkReadOnly() {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message field");
		}
	}
}

