/**
 * Filename:	InterOperatorIdentifierImpl.java
 * Created On:	12-Oct-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;
import com.baypackets.ase.ra.ro.InterOperatorIdentifier;

public class InterOperatorIdentifierImpl implements InterOperatorIdentifier , Serializable {

	private com.condor.chargingcommon.InterOprtrIdtfr _ioId;

	public InterOperatorIdentifierImpl(String origIOI, String termIOI) {
		this._ioId = new com.condor.chargingcommon.InterOprtrIdtfr();
		this._ioId.setOrigIOI(origIOI);
		this._ioId.setTerminatingIOI(termIOI);
	}

	public InterOperatorIdentifierImpl(com.condor.chargingcommon.InterOprtrIdtfr ioId) {
		this._ioId = ioId;
	}

	public String getOriginatingIOI() {
		return this._ioId.getOrigIOI();
	}

	public String getTerminatingIOI() {
		return this._ioId.getTerminatingIOI();
	}

	public void setOriginatingIOI(String origIOI) {
		this._ioId.setOrigIOI(origIOI);
	}

	public void setTerminatingIOI(String termIOI) {
		this._ioId.setTerminatingIOI(termIOI);
	}

	public com.condor.chargingcommon.InterOprtrIdtfr getStackImpl() {
		return this._ioId;
	}
}

