package com.genband.tcap.parser;

import jain.protocol.ss7.SignalingPointCode;

public class CongestionMessage {
	
	int signalingPointStatus;
	
	SignalingPointCode affectedPointCode;
	
	int protocolVariant;

	public int getProtocolVariant() {
		return protocolVariant;
	}

	public void setProtocolVariant(int protocolVariant) {
		this.protocolVariant = protocolVariant;
	}

	public SignalingPointCode getAffectedPointCode() {
		return affectedPointCode;
	}

	public void setAffectedPointCode(SignalingPointCode affectedPointCode) {
		this.affectedPointCode = affectedPointCode;
	}

	public int getSignalingPointStatus() {
		return signalingPointStatus;
	}

	public void setSignalingPointStatus(int signalingPointStatus) {
		this.signalingPointStatus = signalingPointStatus;
	}
}