/**
 * Filename:	CCAMultipleServicesCreditControlImpl.java
 * Created On:	16-Oct-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;

import com.baypackets.ase.ra.ro.CCAMultipleServicesCreditControl;
import com.baypackets.ase.ra.ro.GrantedServiceUnit;
import com.baypackets.ase.ra.ro.FinalUnitIndication;

public class CCAMultipleServicesCreditControlImpl
	implements CCAMultipleServicesCreditControl , Serializable {

	private com.condor.ro.rocommon.RoCCAMulServices _stackObj;
	private GrantedServiceUnitImpl _grantedServUnit;
	private FinalUnitIndicationImpl _fui;

	public CCAMultipleServicesCreditControlImpl(
				com.condor.ro.rocommon.RoCCAMulServices stackObj) {

		this._stackObj = stackObj;

		if(this._stackObj.getIsGrantServUnit()) {
			this._grantedServUnit = new GrantedServiceUnitImpl(
							this._stackObj.getAAAGrantedServiceUnit());
		}

		if(this._stackObj.getIsFinalUnitIndic()) {
			this._fui = new FinalUnitIndicationImpl(this._stackObj.getFinalUnitIndic());
		}
	}

	public GrantedServiceUnit getGrantedServiceUnit() {
		return this._grantedServUnit;
	}

	public long getRatingGroup() {
		if(this._stackObj.getIsRatingGroup()) {
			return this._stackObj.getRatingGRoup();
		} else {
			return -1;
		}
	}

	public long getValidityTime() {
		if(this._stackObj.getIsValidityTime()) {
			return this._stackObj.getValidityTime();
		} else {
			return -1;
		}
	}

	public long getResultCode() {
		if(this._stackObj.getIsResultCode()) {
			return this._stackObj.getResultCode();
		} else {
			return -1;
		}
	}

	public FinalUnitIndication getFinalUnitIndication() {
		return this._fui;
	}

	public long getTimeQuotaThreshold() {
		if(this._stackObj.getIsTimeQuotaThresHold()) {
			return this._stackObj.getTimeQuotaThresHold();
		} else {
			return -1;
		}
	}

	public long getVolumeQuotaThreshold() {
		if(this._stackObj.getIsVolumeQuotaThresHold()) {
			return this._stackObj.getVolumeQuotaThresHold();
		} else {
			return -1;
		}
	}

	public long getQuotaHoldingTime() {
		if(this._stackObj.getIsQuotaHoldingTime()) {
			return this._stackObj.getQuotaHoldingTime();
		} else {
			return -1;
		}
	}

	public long getQuotaConsumptionTime() {
		if(this._stackObj.getIsQuotaConsumptionTime()) {
			return this._stackObj.getQuotaConsumptionTime();
		} else {
			return -1;
		}
	}

	public short getTriggerType() {
		if((new Byte(this._stackObj.getNoOfTriggerType())).intValue() > 0) {
			return (short)this._stackObj.getTriggerType(0);
		} else {
			return -1;
		}
	}
}

