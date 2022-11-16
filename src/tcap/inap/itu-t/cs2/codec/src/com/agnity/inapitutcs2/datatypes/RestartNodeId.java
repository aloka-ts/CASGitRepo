package com.agnity.inapitutcs2.datatypes;

import java.io.Serializable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.agnity.inapitutcs2.operations.InapOperationsCoding;

import jain.MandatoryParameterNotSetException;
import jain.protocol.ss7.SignalingPointCode;

/**
 * Used for encoding and decoding of Restart Node Id
 * @author Mriganka
 *
 */
public class RestartNodeId implements Serializable{
	
	private int ssn;
	
	private SignalingPointCode signalingPointCode;
	
	private byte[] signalingPointCodeBytes;

	private static Logger logger = Logger.getLogger(RestartNodeId.class);
	
	public int getSsn() {
		return ssn;
	}

	public void setSsn(int ssn) {
		this.ssn = ssn;
	}

	public SignalingPointCode getSignalingPointCode() {
		return signalingPointCode;
	}

	public void setSignalingPointCode(SignalingPointCode signalingPointCode) {
		this.signalingPointCode = signalingPointCode;
	}
	public byte[] getSignalingPointCodeBytes() {
		return signalingPointCodeBytes;
	}

	public void setSignalingPointCodeBytes(byte[] signalingPointCodeBytes) {
		this.signalingPointCodeBytes = signalingPointCodeBytes;
	}
	public boolean equals(Object obj){
		RestartNodeId restartNodeId = (RestartNodeId) obj;
		try {
			if (this.ssn == restartNodeId.getSsn() && restartNodeId.getSignalingPointCode().getCluster() == this.getSignalingPointCode().getCluster()
					&& restartNodeId.getSignalingPointCode().getMember() == this.getSignalingPointCode().getMember() 
					&& restartNodeId.getSignalingPointCode().getZone() == this.getSignalingPointCode().getZone()){
				return true;
			}
		} catch (MandatoryParameterNotSetException e) {
			logger.log(Level.ERROR, e);
		}
		return false;
		
	}
	
}
