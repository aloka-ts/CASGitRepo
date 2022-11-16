/**
 * Filename:	SDPMediaComponentImpl.java
 * Created On:	12-Oct-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.io.Serializable;

import com.baypackets.ase.ra.ro.SDPMediaComponent;
import com.baypackets.ase.ra.ro.util.UnmodifiableIterator;

public class SDPMediaComponentImpl implements SDPMediaComponent , Serializable {

	private com.condor.chargingcommon.SdpMediaCmpnt _sdpMedComponent;
	private List _sdpMediaDescs;
	private boolean _readonly = false;

	public SDPMediaComponentImpl(	String sdpMediaName,
									short mediaInitFlag,
									String qos,
									String gprsChargingId,
									List sdpMediaDescs) {

		this._sdpMedComponent = new com.condor.chargingcommon.SdpMediaCmpnt();

		this._sdpMedComponent.setSdpMediaName(sdpMediaName);
		this._sdpMedComponent.setMediaInitFlag(mediaInitFlag);
		this._sdpMedComponent.setAuthorizedQOS(qos);
		this._sdpMedComponent.setGPRSChargingId(gprsChargingId);

		this._sdpMediaDescs = sdpMediaDescs;
	}

	public SDPMediaComponentImpl(com.condor.chargingcommon.SdpMediaCmpnt sdpMC) {
		this._sdpMedComponent = sdpMC;

		int num = (new Byte(sdpMC.getNoOfsdpMediaDesc())).intValue();
		if(num > 0) {
			this._sdpMediaDescs = new LinkedList();

			for(int idx = 0; idx < num; ++idx) {
				this._sdpMediaDescs.add(sdpMC.getSdpMediaDesc(idx));
			}
		}

		this._readonly = true;
	}

	// SDP Media Name
	public String getSDPMediaName() {
		return this._sdpMedComponent.getSdpMediaName();
	}

	public void setSDPMediaName(String sdpMediaName) {
		this.checkReadOnly();

		this._sdpMedComponent.setSdpMediaName(sdpMediaName);
	}

	// Media Initiator Flag
	public short getMediaInitiatorFlag() {
		return (short)this._sdpMedComponent.getMediaInitFlag();
	}

	public void setMediaInitiatorFlag(short mediaInitFlag) {
		this.checkReadOnly();

		this._sdpMedComponent.setMediaInitFlag(mediaInitFlag);
	}

	// Authorized QoS
	public String getAuthorizedQoS() {
		return this._sdpMedComponent.getAuthorizedQOS();
	}

	public void setAuthorizedQoS(String qos) {
		this.checkReadOnly();

		this._sdpMedComponent.setAuthorizedQOS(qos);
	}

	// GPRS Changing Id
	public String getGPRSChargingId() {
		return this._sdpMedComponent.getGPRSChargingId();
	}

	public void setGPRSChargingId(String gprsChargingId) {
		this.checkReadOnly();

		this._sdpMedComponent.setGPRSChargingId(gprsChargingId);
	}

	// SDP Media Descriptions
	public Iterator getSDPMediaDescriptions() {
		if(this._sdpMediaDescs != null) {
			return new UnmodifiableIterator(this._sdpMediaDescs.iterator());
		} else {
			return null;
		}
	}

	public void addSDPMediaDescription(String sdpMediaDesc) {
		this.checkReadOnly();

		if(this._sdpMediaDescs == null) {
			this._sdpMediaDescs = new LinkedList();
		}

		this._sdpMediaDescs.add(sdpMediaDesc);
	}

	public boolean removeSDPMediaDescription(String sdpMediaDesc) {
		this.checkReadOnly();

		if(this._sdpMediaDescs != null) {
			return this._sdpMediaDescs.remove(sdpMediaDesc);
		} else {
			return false;
		}
	}

	public com.condor.chargingcommon.SdpMediaCmpnt getStackImpl() {
		if(this._readonly) {
			// Should have already filled SDP Media Descriptions
			return this._sdpMedComponent;
		}

		this._readonly = true;

		// Fill SDP Media Descriptions
		if(this._sdpMediaDescs != null) {
			this._sdpMedComponent.initNoOfsdpMediaDesc((byte)this._sdpMediaDescs.size());
			Iterator iter = this._sdpMediaDescs.iterator();
			int index = 0;
			while(iter.hasNext()) {
				String sdpMD = (String)iter.next();
				this._sdpMedComponent.setSdpMediaDesc(sdpMD, index++);
			}
		}

		return this._sdpMedComponent;
	}

	private void checkReadOnly() {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message field");
		}
	}
}

