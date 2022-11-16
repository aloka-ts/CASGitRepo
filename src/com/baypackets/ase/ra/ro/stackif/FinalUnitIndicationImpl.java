/**
 * Filename:	FinalUnitIndicationImpl.java
 * Created On:	16-Oct-2006
 */
package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;

import com.baypackets.ase.ra.ro.FinalUnitIndication;
import com.baypackets.ase.ra.ro.IPFilterRule;
import com.baypackets.ase.ra.ro.RedirectServer;

public class FinalUnitIndicationImpl implements FinalUnitIndication , Serializable {

	private com.condor.ro.rocommon.RoFinalUnitIndic _stackFUI;
	private RedirectServerImpl _redServ;

	public FinalUnitIndicationImpl(com.condor.ro.rocommon.RoFinalUnitIndic stackObj) {
		this._stackFUI = stackObj;
		if(this._stackFUI.getIsRedirectServer()) {
			this._redServ = new RedirectServerImpl(
										(short)this._stackFUI.getRedirectAddrType(),
										this._stackFUI.getRedirectServerAddress());
		}
	}

	public short getFinalUnitAction() {
		return (short)this._stackFUI.getFinalUnitAction();
	}

	public IPFilterRule getRestrictedFilterRule() {
		if((new Byte(this._stackFUI.getNoOfFilterId())).shortValue() > 0) {
			return new IPFilterRule(this._stackFUI.getRestrictFilterRule(0).getBytes());
		} else {
			return null;
		}
	}

	public RedirectServer getRedirectServer() {
		return this._redServ;
	}
}
