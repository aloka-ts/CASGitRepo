/*
 * Created on Mar 18, 2005
 *
 */
package com.baypackets.ase.control;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseStrings;

/**
 * @author dana
 * <p>This class maintains information for one partition. FIP is the identifier 
 * of a partition. Each partition has state, floating IP, active member and
 * standby member.
 * </p>
 */
public class SimplePartition implements AsePartition {
	private static final Logger logger = Logger.getLogger(SimplePartition.class);
	
	private short state = CREATE;
	private String fip = "0.0.0.0";
	private String activeMemberId;
	private String standbyMemberId;
	
	private ArrayList destinations = new ArrayList();

	public short getState() {
		return this.state;
	}
	
	public void setFloatingIp(String fip) {
	
		if (logger.isDebugEnabled()) {
		logger.debug(" setting floating ip to : "+fip+ "in the partition : "+this);
		}
		this.fip = fip;
	}
	
	public String getFloatingIp() {
		return this.fip;
	}
	
	public boolean hasFloatingIp() {
		if (this.fip != null && !this.fip.equals(AseStrings.BLANK_STRING) && !this.fip.equals("0.0.0.0")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Return true if this partition has any member
	 */
	public boolean hasMember() {
		if (this.activeMemberId != null || this.standbyMemberId != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * Return true if this partition has a member with the role
	 */
	public boolean hasMember(short role) {
		switch (role) {
		case AseRoles.ACTIVE:
			return this.activeMemberId != null ? true : false;
		case AseRoles.STANDBY:
			return this.standbyMemberId != null ? true : false;
		}
		return false;				
		
	}
	
	/**
	 * Return true if this partition has a member with the subsysId
	 */
	public boolean isMember(String subsysId) {
		if ((activeMemberId != null && activeMemberId.equals(subsysId)) || 
				(standbyMemberId != null && standbyMemberId.equals(subsysId))) {
			return true;
		}
		return false;
	}
	
	/**
	 * Return true if this partition has a member with the subsysId and the role
	 */
	public boolean isMember(String subsysId, short role) {
		switch (role) {
		case AseRoles.ACTIVE:
			return (this.activeMemberId != null) && this.activeMemberId.equals(subsysId);
		case AseRoles.STANDBY:
			return (this.standbyMemberId != null) && this.standbyMemberId.equals(subsysId);
		}
		return false;				
	}
	
	/**
	 * Set subsystem as a member with the role
	 */
	public void setMember(String subsysId, short role) {
		switch (role) {
		
		case AseRoles.ACTIVATING:
		case AseRoles.ACTIVE:
			this.activeMemberId = subsysId;
			this.state = AsePartition.ACTIVE;
			break;
		case AseRoles.STANDBY:
			this.standbyMemberId = subsysId;
			if (this.state == CREATE) {
				this.state = AsePartition.INACTIVE;
			}
			break;
		case AseRoles.STANDBY_TO_ACTIVE:
			this.activeMemberId = subsysId;
			this.standbyMemberId = null;
			break;
		case AseRoles.RELEASE_STANDBY:
			if ((this.standbyMemberId != null) && this.standbyMemberId.equals(subsysId)) {
				this.standbyMemberId = null;
			}
			break;
		case AseRoles.NONE: //subsystem takes no role in this partition
			if ((this.activeMemberId != null) && this.activeMemberId.equals(subsysId)) {
				this.activeMemberId = null;
				this.state = AsePartition.INACTIVE;
			} else if ((this.standbyMemberId != null) && this.standbyMemberId.equals(subsysId)) {
				this.standbyMemberId = null;
			}
			break;
		}
		if (!hasMember()) {
			this.fip = "0.0.0.0";
			this.state = CREATE;
		}
	}
	
	/**
	 * Get all the members with the role
	 */
	public String[] getMembers(short role) {
		String[] members = new String[0];
		switch (role) {
		case AseRoles.ACTIVE:
			if (this.activeMemberId != null) {
				members =  new String[] {this.activeMemberId};
			}
			break;
		case AseRoles.STANDBY:
			if (this.standbyMemberId != null) {
				members = new String[] {this.standbyMemberId};
			}
			break;
		}
		return members;		
	}

	/**
	 * Get the member in the partition with the specified role
	 */
	// There will be only one member with this role in the partition. The above method 
	// also serves the same purpose but I think that it returns a list of members just 
	// to be compatible with some future N+K (K>1) enhancements
	public String getMember(short role) {
		switch (role) {
		case AseRoles.ACTIVE:
			return activeMemberId;
		case AseRoles.STANDBY:
			return standbyMemberId;
		}
		return null;		
	}
	
	/**
	 * Return true if two subsystems in this partition
	 */
	public boolean inSamePartition(String subsysId1, String subsysId2) {
		if (isMember(subsysId1) && isMember(subsysId2)) {
			return true;
		}
		return false;
	}
	
	/**
	 * return the role of subsystem
	 */
	public short getRole(String subsysId) {
		if ((activeMemberId != null) && activeMemberId.equals(subsysId)) {
			return AseRoles.ACTIVE;
		} else if ((standbyMemberId != null) && standbyMemberId.equals(subsysId)) {
			return AseRoles.STANDBY;
		}
		return AseRoles.UNKNOWN;
	}
	
	/**
	 * Return PartitionInfo for the subsystem
	 */
	public PartitionInfo getPartitionInfo(String subsysId) {
		return new PartitionInfo(this.fip, subsysId, getRole(subsysId));
	}
	
	/**
	 * Get replication destinations for this partition
	 */
	public String[] getReplicationDestinations() {
		if (this.standbyMemberId != null) {
			return new String[] {this.standbyMemberId};
		}
		return null;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.fip + "\t");
		buf.append(this.activeMemberId + "\t\t");
		buf.append(this.standbyMemberId);
		return buf.toString();
	}

	/*public String getActiveMemberId()
	{
		return activeMemberId;
	}

	public String getStandbyMemberId()
	{
		return standbyMemberId;
	}*/
}
