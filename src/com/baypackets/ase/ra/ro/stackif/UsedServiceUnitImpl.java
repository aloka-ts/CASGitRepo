
package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;
import com.baypackets.ase.ra.ro.RequestedServiceUnit;
import com.baypackets.ase.ra.ro.UsedServiceUnit;

import com.condor.ro.rocommon.*;

public final class UsedServiceUnitImpl implements UsedServiceUnit , Serializable {
	private RoUsedServiceUnit _stackUsedServUnit;
	private RoReqServiceUnit _stackReqServUnit;

	public UsedServiceUnitImpl(	short reportReason,
								short tariffChangeUsage,
								long ccTime,
								long totalOctets,
								long inputOctets,
								long outputOctets,
								long servUnits) {
		this._stackUsedServUnit = new RoUsedServiceUnit();
		this._stackReqServUnit = new RoReqServiceUnit();
		this._stackUsedServUnit.setAAAReqServiceUnit(this._stackReqServUnit);

		this.setReportingReason(reportReason);
		this.setTariffChangeUsage(tariffChangeUsage);
		this.setCCTime(ccTime);
		this.setCCTotalOctets(totalOctets);
		this.setCCInputOctets(inputOctets);
		this.setCCOutputOctets(outputOctets);
		this.setCCServiceSpecificUnits(servUnits);
	}

	public short getReportingReason() {
		if(this._stackUsedServUnit.getIsRprtRsnPresent()) {
			return (short)this._stackUsedServUnit.getReportReason();
		} else {
			return -1;
		}
	}

	public short getTariffChangeUsage() {
		if(this._stackUsedServUnit.getIsTarifChangeUsage()) {
			return (short)this._stackUsedServUnit.getTariffChangeUsage();
		} else {
			return -1;
		}
	}

	public void setReportingReason(short reportingReason) {
		if(reportingReason >= 0) {
			this._stackUsedServUnit.setReportReason(reportingReason);
			this._stackUsedServUnit.setIsRprtRsnPresent(true);
		} else {
			this._stackUsedServUnit.setIsRprtRsnPresent(false);
		}
	}

	public void setTariffChangeUsage(short tariffChangeUsage) {
		if(tariffChangeUsage >= 0) {
			this._stackUsedServUnit.setTariffChangeUsage(tariffChangeUsage);
			this._stackUsedServUnit.setIsTarifChangeUsage(true);
		} else {
			this._stackUsedServUnit.setIsTarifChangeUsage(false);
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

	RoUsedServiceUnit getStackImpl() {
		return this._stackUsedServUnit;
	}
}
