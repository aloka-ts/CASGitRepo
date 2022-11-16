/*
 * Created on Oct 27, 2004
 *
 */
package com.baypackets.ase.control;


/**
 * @author Ravi
 * 
 */
public interface AsePartition {
	public static final short CREATE = 0;
	public static final short ACTIVE = 1;
	public static final short INACTIVE = 2;
	public static final short DESTROY = 3;
	public static final short ACTIVE_READY = 4;
	public static final short INACTIVE_READY = 5;
	
	/**
	 * Get partition state
	 * @return
	 */
	public short getState();
	
	/**
	 * 
	 * @param fip
	 */
	public void setFloatingIp(String fip);
	
	/**
	 * 
	 * @return
	 */
	public String getFloatingIp();
	
	/**
	 * Return true if this partition has any member
	 * @return
	 */
	public boolean hasMember();
	
	/**
	 * Return true if this partition has a member with the role
	 * @param role
	 * @return
	 */
	public boolean hasMember(short role);
	
	/**
	 * Return true if this partition has a member with the subsysId
	 * @param subsysId
	 * @return
	 */
	public boolean isMember(String subsysId);
	
	/**
	 * Return true if this partition has a member with the subsysId and the role
	 * @param subsysId
	 * @param role
	 * @return
	 */
	public boolean isMember(String subsysId, short role);
	
	/**
	 * Set the subsystem as a member with the role
	 * @param subsysId
	 * @param role
	 */
	public void setMember(String subsysId, short role);
	
	/**
	 * Get all the members with the role in this partition
	 * @param role
	 * @return
	 */
	public String[] getMembers(short role);
	
	/**
	 * Return true if both subsystems are in this partition
	 * @param subsysId1
	 * @param subsysId2
	 * @return
	 */
	public boolean inSamePartition(String subsysId1, String subsysId2);
	
	/**
	 * return the role of subsystem
	 * @param subsysId
	 * @return
	 */
	public short getRole(String subsysId);
	
	/**
	 * Return PartitionInfo for the subsystem
	 * @param subsysId
	 * @return
	 */
	public PartitionInfo getPartitionInfo(String subsysId);
	
	/**
	 * Get replication destinations for this partition
	 * @return
	 */
	public String[] getReplicationDestinations();

	/**
	 * Get the member in the partition having the specified role
	 */

	public String getMember(short role);

}
