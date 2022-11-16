/**
 * Filename:	MessageBodyImpl.java
 * Created On:	12-Oct-2006
 */

package com.baypackets.ase.ra.ro.stackif;

import java.io.Serializable;
import com.baypackets.ase.ra.ro.MessageBody;

public class MessageBodyImpl implements MessageBody , Serializable {
	private com.condor.chargingcommon.MessageBody _msgBody;

	public MessageBodyImpl(String type, String length, String disp, short originator) {
		this._msgBody = new com.condor.chargingcommon.MessageBody();

		this._msgBody.setContentType(type);
		this._msgBody.setContentLen(length);
		this._msgBody.setContentDisp(disp);
		this._msgBody.setOrignator(originator);
	}

	public MessageBodyImpl(com.condor.chargingcommon.MessageBody mb) {
		this._msgBody = mb;
	}

	public String getContentType() {
		return this._msgBody.getContentType();
	}

	public String getContentLength() {
		return this._msgBody.getContentLen();
	}

	public String getContentDisposition() {
		return this._msgBody.getContentDisp();
	}

	public short getOriginator() {
		if(this._msgBody.getIsOrignatorPresent()) {
			return (short)this._msgBody.getOrignator();
		} else {
			return -1;
		}
	}

	public void setContentType(String contentType) {
		this._msgBody.setContentType(contentType);
	}

	public void setContentLength(String contentLength) {
		this._msgBody.setContentLen(contentLength);
	}

	public void setContentDisposition(String contentDisposition) {
		this._msgBody.setContentDisp(contentDisposition);
	}

	public void setOriginator(short orig) {
		this._msgBody.setOrignator(orig);
	}

	public com.condor.chargingcommon.MessageBody getStackImpl() {
		return this._msgBody;
	}
}

