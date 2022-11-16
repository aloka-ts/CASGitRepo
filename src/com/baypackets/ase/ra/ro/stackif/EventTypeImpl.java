/**
 * Filename:	EventTypeImpl.java
 * Created On:	12-Oct-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;
import com.baypackets.ase.ra.ro.EventType;

public class EventTypeImpl implements EventType , Serializable {

	private com.condor.chargingcommon.EventType _evtType;
	private boolean _readonly = false;

	public EventTypeImpl(String sipMethod, String event, int expires) {
		this._evtType = new com.condor.chargingcommon.EventType();
		this._evtType.setSipMethod(sipMethod);
		this._evtType.setEvent(event);
		if(expires >= 0) {
			this._evtType.setExpires(expires);
		}
	}

	public EventTypeImpl(com.condor.chargingcommon.EventType evtType) {
		this._evtType = evtType;
		this._readonly = false;
	}

	public String getSIPMethod() {
		return this._evtType.getSipMethod();
	}

	public String getEvent() {
		return this._evtType.getEvent();
	}

	public int getExpires() {
		if(this._evtType.getIsExpiresPresent()) {
			return this._evtType.getExpires();
		} else {
			return -1;
		}
	}

	public void setSIPMethod(String method) {
		this.checkReadOnly();

		this._evtType.setSipMethod(method);
	}

	public void setEvent(String event) {
		this.checkReadOnly();

		this._evtType.setEvent(event);
	}

	public void setExpires(int expires) {
		this.checkReadOnly();

		if(expires >= 0) {
			this._evtType.setExpires(expires);
		}
	}

	public com.condor.chargingcommon.EventType getStackImpl() {
		return this._evtType;
	}

	private void checkReadOnly() {
		if(this._readonly) {
			throw new IllegalStateException("Cannot modify this message field");
		}
	}
}

