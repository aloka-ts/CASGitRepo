/**
 * Filename:	ServiceInformationImpl.java
 * Created On:	12-Oct-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;
import com.baypackets.ase.ra.ro.ServiceInformation;
import com.baypackets.ase.ra.ro.IMSInformation;

public class ServiceInformationImpl implements ServiceInformation , Serializable {

	private com.condor.chargingcommon.ServiceInfo _servInfo;
	private IMSInformationImpl _imsInfo;
	private boolean _readonly = false;

	public ServiceInformationImpl(IMSInformation imsInfo) {
		this._imsInfo = (IMSInformationImpl)imsInfo;
	}

	public ServiceInformationImpl(com.condor.chargingcommon.ServiceInfo servInfo) {
		this._servInfo = servInfo;
		if(this._servInfo.getIsImsInfoPresent()) {
			this._imsInfo = new IMSInformationImpl(this._servInfo.getImsInfo());
		}

		this._readonly = true;
	}

	public IMSInformation getIMSInformation() {
		return this._imsInfo;
	}

	public void setIMSInformation(IMSInformation imsInfo) {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message field.");
		}

		this._imsInfo = (IMSInformationImpl)imsInfo;
	}

	public com.condor.chargingcommon.ServiceInfo getStackImpl() {

		if(this._servInfo != null) {
			return this._servInfo;
		}

		this._readonly = true;

		this._servInfo = new com.condor.chargingcommon.ServiceInfo();

		if(this._imsInfo != null) {
			this._servInfo.setImsInfo(this._imsInfo.getStackImpl());
			this._servInfo.setIsImsInfoPresent(true);
		} else {
			this._servInfo.setIsImsInfoPresent(false);
		}

		return this._servInfo;
	}
}

