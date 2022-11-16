
package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;
import com.baypackets.ase.ra.ro.RequestedServiceUnit;

import com.condor.ro.rocommon.RoReqServiceUnit;

public class RequestedServiceUnitImpl implements RequestedServiceUnit , Serializable {

	private RoReqServiceUnit _stackReqServUnit;

	public RequestedServiceUnitImpl(	long ccTime,
										long totalOctets,
										long inputOctets,
										long outputOctets,
										long servUnits) {
		this._stackReqServUnit = new RoReqServiceUnit();
		this.setCCTime(ccTime);
		this.setCCTotalOctets(totalOctets);
		this.setCCInputOctets(inputOctets);
		this.setCCOutputOctets(outputOctets);
		this.setCCServiceSpecificUnits(servUnits);
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
		if(time >= 0) {
			this._stackReqServUnit.setCCTime((int)time);
			this._stackReqServUnit.setIsCCTimePresent(true);
		} else {
			this._stackReqServUnit.setIsCCTimePresent(false);
		}
	}

	public void setCCTotalOctets(long octets) {
		if(octets >= 0) {
			this._stackReqServUnit.setCCTotalOctets(octets);
			this._stackReqServUnit.setIsCCTOctetsPresent(true);
		} else {
			this._stackReqServUnit.setIsCCTOctetsPresent(false);
		}
	}

	public void setCCInputOctets(long octets) {
		if(octets >= 0) {
			this._stackReqServUnit.setCCInputOctets(octets);
			this._stackReqServUnit.setIsCCInputOctetsPresent(true);
		} else {
			this._stackReqServUnit.setIsCCInputOctetsPresent(false);
		}
	}

	public void setCCOutputOctets(long octets) {
		if(octets >= 0) {
			this._stackReqServUnit.setCCOutputOctets(octets);
			this._stackReqServUnit.setIsCCOutputOctetsPresent(true);
		} else {
			this._stackReqServUnit.setIsCCOutputOctetsPresent(false);
		}
	}

	public void setCCServiceSpecificUnits(long units) {
		if(units >= 0) {
			this._stackReqServUnit.setCCServSpecUnits(units);
			this._stackReqServUnit.setIsCCServSpecUnitsPresent(true);
		} else {
			this._stackReqServUnit.setIsCCServSpecUnitsPresent(false);
		}
	}

	RoReqServiceUnit getStackImpl() {
		return this._stackReqServUnit;
	}
}
