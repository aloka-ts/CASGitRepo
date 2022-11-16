/*
 * Created on Mar 18, 2005
 *
 */
package com.baypackets.ase.control;

import java.io.Serializable;

/**
 * @author Dana
 * <p>This class provides one partition information for one subsystem.
 * </p>
 */
public class PartitionInfo implements Serializable {
	
	private static final long serialVersionUID = 5079898438354L;
	public static final int NO_INDEX = -1;
	
	private String subsysId;
	private String fip;
	private short role;
	
	public PartitionInfo(String fip, String subsysId, short role) {
		this.subsysId = subsysId;
		this.fip = fip;
		this.role = role;
	}
	
	public String getSubsysId() {
		return this.subsysId;
	}
	
	public String getFip() {
		return this.fip;
	}
	
	public short getRole() {
		return this.role;
	}

	public void setRole(short role) {
		this.role = role;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer(this.fip + ", ");
		buf.append(this.subsysId + ", ");
		switch (this.role) {
		case AseRoles.ACTIVE:
			buf.append("ACTIVE");
			break;
		case AseRoles.STANDBY:
			buf.append("STANDBY");
			break;
		case AseRoles.RELEASE_STANDBY:
			buf.append("RELEASE_STANDBY");
			break;
		case AseRoles.STANDBY_TO_ACTIVE:
			buf.append("STANDBY_TO_ACTIVE");
			break;
		case AseRoles.NONE:
			buf.append("NONE");
			break;
		}
		return buf.toString();
	}
}
