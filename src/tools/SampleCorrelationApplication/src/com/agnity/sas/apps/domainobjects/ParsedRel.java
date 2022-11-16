package com.agnity.sas.apps.domainobjects;

import com.genband.isup.datatypes.Cause;

public class ParsedRel {

	private Cause cause;
	private byte[] causeBytes;

	public ParsedRel(Cause cause,byte[] causeBytes) {
		this.setCause(cause);
		this.setCauseBytes(causeBytes);

	}


	/**
	 * @param cause the cause to set
	 */
	public void setCause(Cause cause) {
		this.cause = cause;
	}


	/**
	 * @return the cause
	 */
	public Cause getCause() {
		return cause;
	}


	/**
	 * @param causeBytes the causeBytes to set
	 */
	public void setCauseBytes(byte[] causeBytes) {
		this.causeBytes = causeBytes;
	}


	/**
	 * @return the causeBytes
	 */
	public byte[] getCauseBytes() {
		return causeBytes;
	}

}
