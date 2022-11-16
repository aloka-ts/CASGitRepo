
package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;
import com.baypackets.ase.ra.ro.SubscriptionId;

import com.condor.ro.rocommon.RoSubscriptionId;

public class SubscriptionIdImpl implements SubscriptionId , Serializable {
	private RoSubscriptionId _stackSubsId;

	public SubscriptionIdImpl(short subsIdType, String subsIdData) {
		this._stackSubsId = new RoSubscriptionId();
		this._stackSubsId.setSubscriptionIdType(subsIdType);
		this._stackSubsId.setSubscriptionIdData(subsIdData);
	}

	public short getSubscriptionIdType() {
		return (short)this._stackSubsId.getSubscriptionIdType();
	}

	public String getSubscriptionIdData() {
		return this._stackSubsId.getSubscriptionIdData();
	}

	public void setSubscriptionIdType(short subsIdType) {
		this._stackSubsId.setSubscriptionIdType(subsIdType);
	}

	public void setSubscriptionIdData(String subsIdData) {
		this._stackSubsId.setSubscriptionIdData(subsIdData);
	}

	public RoSubscriptionId getStackImpl() {
		return this._stackSubsId;
	}
}

