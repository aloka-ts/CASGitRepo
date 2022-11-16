
package com.baypackets.ase.ra.ro.stackif;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.io.Serializable;

import com.baypackets.ase.ra.ro.util.UnmodifiableIterator;
import com.baypackets.ase.ra.ro.*;

import com.condor.ro.rocommon.*;

public class CCRMultipleServicesCreditControlImpl
	implements CCRMultipleServicesCreditControl , Serializable {

	private RoCCRMulServices _stackMulServices;
	private RequestedServiceUnit _reqServUnit;
	private List _usedServUnitList;
	private List _ttList;

	public CCRMultipleServicesCreditControlImpl() {
		this._stackMulServices = new RoCCRMulServices();
		this._usedServUnitList = new LinkedList();
		this._ttList = new LinkedList();
	}

	// Requested Service Unit
	public RequestedServiceUnit getRequestedServiceUnit() {
		return this._reqServUnit;
	}

	public void setRequestedServiceUnit(RequestedServiceUnit rsu) {
		this._reqServUnit = rsu;

		if(rsu != null) {
			this._stackMulServices.setAAAReqServiceUnit(
					(RoReqServiceUnit)((RequestedServiceUnitImpl)rsu).getStackImpl());
			this._stackMulServices.setIsReqServUnit(true);
		} else {
			this._stackMulServices.setIsReqServUnit(false);
		}
	}

	// Used Service Units
	public Iterator getUsedServiceUnits() {
		return new UnmodifiableIterator(this._usedServUnitList.iterator());
	}

	public void addUsedServiceUnit(UsedServiceUnit usu) {
		if(usu != null) {
			this._stackMulServices.setAAAUsedServiceUnit(
						(RoUsedServiceUnit)((UsedServiceUnitImpl)usu).getStackImpl(),
						this._usedServUnitList.size());
			this._usedServUnitList.add(usu);
			Integer size = new Integer(this._usedServUnitList.size());
			this._stackMulServices.setNoOfUsedServUnit(size.byteValue());
		} else {
			throw new IllegalArgumentException("Argument passed is null");
		}
	}

	public void removeUsedServiceUnit(UsedServiceUnit usu) {
		throw new IllegalStateException("Not supported by stack");
	}

	// Rating Group
	public long getRatingGroup() {
		if(this._stackMulServices.getIsRatingGroup()) {
			return this._stackMulServices.getRatingGroup();
		} else {
			return -1;
		}
	}

	public void setRatingGroup(long ratingGroup) {
		if(ratingGroup >= 0) {
			this._stackMulServices.setRatingGroup((int)ratingGroup);
			this._stackMulServices.setIsRatingGroup(true);
		} else {
			throw new IllegalArgumentException("Argument passed is invalid");
		}
	}

	// Reporting Reason
	public short getReportingReason() {
		if(this._stackMulServices.getIsRptReason()) {
			return (short)this._stackMulServices.getReportReason();
		} else {
			return -1;
		}
	}

	public void setReportingReason(short reportingReason) {
		if(reportingReason >= 0) {
			this._stackMulServices.setReportReason(reportingReason);
			this._stackMulServices.setIsRptReason(true);
		} else {
			throw new IllegalArgumentException("Argument passed is invalid");
		}
	}

	// Trigger Type
	public Iterator getTriggerTypes() {
		return new UnmodifiableIterator(this._ttList.iterator());
	}

	public void addTriggerType(short triggerType) {
		if(triggerType >= 0) {
			this._stackMulServices.setTriggerType(triggerType, this._ttList.size());
			this._ttList.add(new Short(triggerType));
			Integer size = new Integer(this._ttList.size());
			this._stackMulServices.setNoOfTriggerType(size.byteValue());
		} else {
			throw new IllegalArgumentException("Argument passed is invalid");
		}
	}

	public void removeTriggerType(short triggerType) {
		throw new IllegalStateException("Not supported by stack");
	}

	RoCCRMulServices getStackImpl() {
		return this._stackMulServices;
	}
}
