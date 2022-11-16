/**
 * Filename:	TimeStampsImpl.java
 * Created On:	12-Oct-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;
import com.baypackets.ase.ra.ro.TimeStamps;

public class TimeStampsImpl implements TimeStamps , Serializable {

	private com.condor.chargingcommon.TimeStamp _timeStamps;
	private boolean _readonly = false;

	public TimeStampsImpl(String reqTS, String resTS) {
		this._timeStamps = new com.condor.chargingcommon.TimeStamp();
		this._timeStamps.setSipReqTimeStamp(reqTS);
		this._timeStamps.setSipResTimeStamp(resTS);
	}

	public TimeStampsImpl(com.condor.chargingcommon.TimeStamp ts) {
		this._timeStamps = ts;
		this._readonly = true;
	}

	public String getSIPRequestTimestamp() {
		return this._timeStamps.getSipReqTimeStamp();
	}

	public String getSIPResponseTimestamp() {
		return this._timeStamps.getSipResTimeStamp();
	}

	public void setSIPRequestTimestamp(String timestamp) {
		this.checkReadOnly();

		this._timeStamps.setSipReqTimeStamp(timestamp);
	}

	public void setSIPResponseTimestamp(String timestamp) {
		this.checkReadOnly();

		this._timeStamps.setSipResTimeStamp(timestamp);
	}

	public com.condor.chargingcommon.TimeStamp getStackImpl() {
		return this._timeStamps;
	}

	private void checkReadOnly() {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message field");
		}
	}
}

