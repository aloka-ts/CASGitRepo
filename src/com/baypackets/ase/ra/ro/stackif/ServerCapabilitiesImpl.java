/**
 * Filename:	ServerCapabilitiesImpl.java
 * Created On:	12-Oct-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.io.Serializable;

import com.baypackets.ase.ra.ro.ServerCapabilities;
import com.baypackets.ase.ra.ro.util.UnmodifiableIterator;
import com.baypackets.ase.ra.ro.CustomAVP;
import com.baypackets.ase.ra.ro.CustomAVPMapImpl;

public class ServerCapabilitiesImpl implements ServerCapabilities , Serializable {

	private com.condor.chargingcommon.ServerCapability _servCaps;
	private List _mandCaps;
	private List _optCaps;
	private List _serverNames;
	private CustomAVPMapImpl _avpMap;
	private boolean _readonly;

	public ServerCapabilitiesImpl() {
	}

	public ServerCapabilitiesImpl(com.condor.chargingcommon.ServerCapability sc) {
		this._servCaps = sc;

		int num = 0;

		// Copy Mandatory Capabilities
		num = (new Byte(sc.getNoOfMandatoryCapabilitys())).intValue();
		if(num > 0) {
			this._mandCaps = new LinkedList();
			for(int idx = 0; idx < num; ++idx) {
				this._mandCaps.add(new Integer(sc.getMandatoryCapability(idx)));
			}
		}

		// Copy Optional Capabilities
		num = (new Byte(sc.getNoOfOptionalCapabilitys())).intValue();
		if(num > 0) {
			this._optCaps = new LinkedList();
			for(int idx = 0; idx < num; ++idx) {
				this._optCaps.add(new Integer(sc.getOptionalCapability(idx)));
			}
		}

		// Copy Server Names
		num = (new Byte(sc.getNoOfServerName())).intValue();
		if(num > 0) {
			this._serverNames = new LinkedList();
			for(int idx = 0; idx < num; ++idx) {
				this._serverNames.add(sc.getServerName(idx));
			}
		}

		// Copy custom AVPs : TODO - not supported by stack

		this._readonly = true;
	}

	// Mandatory Capabilities
	public Iterator getMandatoryCapabilities() {
		if(this._mandCaps != null) {
			return new UnmodifiableIterator(this._mandCaps.iterator());
		} else {
			return null;
		}
	}

	public void addMandatoryCapability(int mandCap) {
		this.checkReadOnly();

		if(this._mandCaps == null) {
			this._mandCaps = new LinkedList();
		}

		this._mandCaps.add(new Integer(mandCap));
	}

	public boolean removeMandatoryCapability(int mandCap) {
		this.checkReadOnly();

		if(this._mandCaps != null) {
			return this._mandCaps.remove(new Integer(mandCap));
		} else {
			return false;
		}
	}

	// Optional Capabilities
	public Iterator getOptionalCapabilities() {
		if(this._optCaps != null) {
			return new UnmodifiableIterator(this._optCaps.iterator());
		} else {
			return null;
		}
	}

	public void addOptionalCapability(int optCap) {
		this.checkReadOnly();

		if(this._optCaps == null) {
			this._optCaps = new LinkedList();
		}

		this._optCaps.add(new Integer(optCap));
	}

	public boolean removeOptionalCapability(int optCap) {
		this.checkReadOnly();

		if(this._optCaps != null) {
			return this._optCaps.remove(new Integer(optCap));
		} else {
			return false;
		}
	}

	// Server Names
	public Iterator getServerNames() {
		if(this._serverNames != null) {
			return new UnmodifiableIterator(this._serverNames.iterator());
		} else {
			return null;
		}
	}

	public void addServerName(String servName) {

		this.checkReadOnly();

		if(this._serverNames == null) {
			this._serverNames = new LinkedList();
		}

		this._serverNames.add(servName);
	}

	public boolean removeServerName(String servName) {
		this.checkReadOnly();

		if(this._serverNames != null) {
			return this._serverNames.remove(servName);
		} else {
			return false;
		}
	}

	public int[] getCustomAVPCodes() {
		if(this._avpMap != null) {
			return this._avpMap.getCustomAVPCodes();
		} else {
			return null;
		}
	}

	public CustomAVP getCustomAVP(int code) {
		if(this._avpMap != null) {
			return this._avpMap.getCustomAVP(code);
		} else {
			return null;
		}
	}

	public CustomAVP setCustomAVP(int code, CustomAVP value) {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message");
		}

		if(this._avpMap == null) {
			this._avpMap = new CustomAVPMapImpl();
		}

		return this._avpMap.setCustomAVP(code, value);
	}

	public CustomAVP removeCustomAVP(int code) {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message");
		}

		if(this._avpMap != null) {
			return this._avpMap.removeCustomAVP(code);
		} else {
			return null;
		}
	}

	public com.condor.chargingcommon.ServerCapability getStackImpl() {
		if(this._servCaps != null) {
			return this._servCaps;
		}

		this._readonly = true;

		this._servCaps = new com.condor.chargingcommon.ServerCapability();

		// Copy Mandatory Capabilities
		if(this._mandCaps != null) {
			this._servCaps.initNoOfMandatoryCapabilitys((byte)this._mandCaps.size());

			Iterator iter = this._mandCaps.iterator();
			int index = 0;
			while(iter.hasNext()) {
				int mc = ((Integer)iter.next()).intValue();
				this._servCaps.setMandatoryCapability(mc, index++);
			}
		}

		// Copy Optional Capabilities
		if(this._optCaps != null) {
			this._servCaps.initNoOfOptionalCapabilitys((byte)this._optCaps.size());

			Iterator iter = this._optCaps.iterator();
			int index = 0;
			while(iter.hasNext()) {
				int oc = ((Integer)iter.next()).intValue();
				this._servCaps.setOptionalCapability(oc, index++);
			}
		}

		// Copy Server Names
		if(this._serverNames != null) {
			this._servCaps.initNoOfServerName((byte)this._serverNames.size());

			Iterator iter = this._serverNames.iterator();
			int index = 0;
			while(iter.hasNext()) {
				String sn = (String)iter.next();
				this._servCaps.setServerName(sn, index++);
			}
		}

		return this._servCaps;
	}

	private void checkReadOnly() {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message field");
		}
	}
}

