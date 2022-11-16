/**
 * Filename:	TrunkGroupIdImpl.java
 * Created On:	12-Oct-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;
import com.baypackets.ase.ra.ro.TrunkGroupId;

public class TrunkGroupIdImpl implements TrunkGroupId , Serializable {

	private com.condor.chargingcommon.TrunkGroupId _tgId;

	public TrunkGroupIdImpl(String itgId, String otgId) {
		this._tgId = new com.condor.chargingcommon.TrunkGroupId();
		this._tgId.setIncomingTGID(itgId);
		this._tgId.setOutgoingTGID(otgId);
	}

	public TrunkGroupIdImpl(com.condor.chargingcommon.TrunkGroupId tgId) {
		this._tgId = tgId;
	}

	public String getIncomingTrunkGroupId() {
		return this._tgId.getIncomingTGID();
	}

	public String getOutgoingTrunkGroupId() {
		return this._tgId.getOutgoingTGID();
	}

	public void setIncomingTrunkGroupId(String itgId) {
		this._tgId.setIncomingTGID(itgId);
	}
	
	public void setOutgoingTrunkGroupId(String otgId) {
		this._tgId.setOutgoingTGID(otgId);
	}

	public com.condor.chargingcommon.TrunkGroupId getStackImpl() {
		return this._tgId;
	}
}

