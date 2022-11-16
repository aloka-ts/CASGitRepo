/**
 * Filename:	GrantedServiceUnitImpl.java
 * Created On:	16-Oct-2006
 */
package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;
import com.baypackets.ase.ra.ro.GrantedServiceUnit;

import com.condor.ro.rocommon.RoGrantedServiceUnit;
import com.condor.ro.rocommon.RoReqServiceUnit;

public class GrantedServiceUnitImpl implements GrantedServiceUnit , Serializable {

	private RoGrantedServiceUnit _stackGrantedServUnit;
	private RoReqServiceUnit _stackReqServUnit;

	public GrantedServiceUnitImpl(RoGrantedServiceUnit stackObj) {
		this._stackGrantedServUnit = stackObj;
		this._stackReqServUnit = this._stackGrantedServUnit.getAAAReqServiceUnit();
	}

	public long getTariffTimeChange() {
		if(this._stackGrantedServUnit.getIsTariffTmeChange()) {
			return this._stackGrantedServUnit.getTariffTimeChange();
		} else {
			return -1;
		}
	}

	public long getCCTime() {
		if(this._stackReqServUnit.getIsCCTimePresent()) {
			return this._stackReqServUnit.getCCTime();
		} else {
			return -1;
		}
	}

	public long getCCTotalOctets() {
		if(this._stackReqServUnit.getIsCCTOctetsPresent()) {
			return this._stackReqServUnit.getCCTotalOctets();
		} else {
			return -1;
		}
	}

	public long getCCInputOctets() {
		if(this._stackReqServUnit.getIsCCInputOctetsPresent()) {
			return this._stackReqServUnit.getCCInputOctets();
		} else {
			return -1;
		}
	}

	public long getCCOutputOctets() {
		if(this._stackReqServUnit.getIsCCOutputOctetsPresent()) {
			return this._stackReqServUnit.getCCOutputOctets();
		} else {
			return -1;
		}
	}

	public long getCCServiceSpecificUnits() {
		if(this._stackReqServUnit.getIsCCServSpecUnitsPresent()) {
			return this._stackReqServUnit.getCCServSpecUnits();
		} else {
			return -1;
		}
	}

	public void setCCTime(long time) {
		throw new IllegalStateException("Cannot modify incoming response field");
	}

	public void setCCTotalOctets(long octets) {
		throw new IllegalStateException("Cannot modify incoming response field");
	}

	public void setCCInputOctets(long octets) {
		throw new IllegalStateException("Cannot modify incoming response field");
	}

	public void setCCOutputOctets(long octets) {
		throw new IllegalStateException("Cannot modify incoming response field");
	}

	public void setCCServiceSpecificUnits(long units) {
		throw new IllegalStateException("Cannot modify incoming response field");
	}
}
