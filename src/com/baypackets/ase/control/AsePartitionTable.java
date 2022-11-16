/*
 * Created on Mar 15, 2005
 *
 */
package com.baypackets.ase.control;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.baypackets.ase.util.AseStrings;

/**
 * @author Dana
 * <p>This class provides a conceptual table and each row of the table is 
 * an AsePartition object. The size of the table is decided by number of 
 * active subsystem in a cluster, which is initialized with installation 
 * configuration and increased/decreased by adding/removing subsystems.
 * </p>
 */
public class AsePartitionTable {
	private static final Logger logger = Logger.getLogger(AsePartitionTable.class);
	
	private int maxPartitions = 1;
	private ArrayList partitions = new ArrayList();
	private String selfId = null;
	
	private PartitionFactory factory = PartitionFactory.getInstance();

	public Object synchronizationObj = new Object();

	private ClusterManager clusterMgr = null;
	
	private static int numberOfFIPs = 1;
	
	public AsePartitionTable(String selfId) {
		this.selfId = selfId;
	}

	public void setClusterManager( ClusterManager clstrMgr )
	{
		clusterMgr = clstrMgr;
	}

	/**
	 * Return infos for the partitions that has the specified subsystem 
	 * as either active member or standby member.
	 * @param subsysId
	 * @return
	 */
	public ArrayList getPartitionInfos(String subsysId) {
		ArrayList partitionInfos = new ArrayList();
		for (int i = 0; i < this.partitions.size(); i++) {
			AsePartition partition = (AsePartition)this.partitions.get(i);
			short role = partition.getRole(subsysId);
			if (role == AseRoles.UNKNOWN) {
				continue;
			}
			PartitionInfo info = 
				new PartitionInfo(partition.getFloatingIp(), subsysId, role);
			partitionInfos.add(info);
		}
		return partitionInfos;
	}
	
	public PartitionInfo getPartitionInfo(String fip, String subsysId) {
		return getPartition(fip).getPartitionInfo(subsysId);
	}


	
	/**
	 * Return conflict peer partition infos if any update fails
	 * @param peerPartitionInfos
	 * @return ArrayList of PartitionInfo
	 */
	public synchronized ArrayList update(ArrayList peerPartitionInfos) {
		if (peerPartitionInfos == null) {
			if (logger.isDebugEnabled()) {

			logger.debug(" peerPartitionInfos is null " ) ;
			}
			return null;
		}
		ArrayList failed = null;
		for (int i = 0; i < peerPartitionInfos.size(); i++) {
			PartitionInfo info = (PartitionInfo)peerPartitionInfos.get(i);
			PartitionInfo failedInfo = update(info);
			if (failedInfo != null) {
				if (failed == null) {
					failed = new ArrayList();
				}
				failed.add(failedInfo);
			}
		}
		return failed;
	}

	/**
	 * Return conflict peer partition info if update fails
	 * @param info 
	 * @return PartitionInfo
	 */
	private synchronized PartitionInfo update(PartitionInfo info)  {
		AsePartition partition = getPartition(info.getFip());
		if (partition == null) {
			partition = getEmptyPartition();
			if (partition == null) {
				if (logger.isInfoEnabled()) {

				logger.info(" No empty partition is available. Now try to get partition with null fip."); 
				}
				partition = getPartiallyEmptyPartition();	
				//logger.error("Unable to update partition of " + info.getFip());
				partition.setFloatingIp(info.getFip());
			} else {
				partition.setFloatingIp(info.getFip());
			}
		} else {
			//It means that partition already exist so check whether 
			//partition has member with same role
			if ( partition.hasMember(info.getRole()) ) {
				logger.error("FATAL ERROR : conflict in update " ) ;
				return info;
			}
		}
		//update the partition
		short role = info.getRole();
		if( role == AseRoles.RELEASE_STANDBY ) {
			//Ashish don know wat is happening
			// Handles the case:    2 (X and Y) (there can be any number of them) Active 
			// and one STANDBY caters to them all
			// One Active (say X) goes down, the Standby becomes Active. The new Active now sends a 
			// RELEASE_STANDBY due to which removeReplicationDestination() has to be called 
			// on the Active, that is, Y
			if( partition.isMember( selfId , AseRoles.ACTIVE ) )
			{
				if (logger.isInfoEnabled()) {

				logger.info( " Received RELEASE_STANDBY from "+info.getSubsysId() +" removing Replication Destination "+ partition.getMember(AseRoles.STANDBY));
				}
				clusterMgr.removeReplicationDestination(partition.getMember(AseRoles.STANDBY) );
			}
		}
		String subsysId = info.getSubsysId();
		partition.setMember(subsysId, role);
		if (logger.isInfoEnabled()) {


		logger.info(" update() exiting ");	
		}
		return null;
	}

  /**
   *	Return conflict peer partition info if update fails in case of RoleSync 
	 *	Message
   *	@param info 
   *	@return PartitionInfo
   */
  public synchronized PartitionInfo roleSyncUpdate(PartitionInfo info)  {
		if ( logger.isDebugEnabled() ) {	
			logger.debug("inside roleSyncUpdate() method " ) ;
		}
		//Already unset FIP in case of active goes down. So If trying to get partition
		//correspond to partition will return null. So first have to take care of 
		//partition in which Standby is not null but incase of Active slot role is 
		//NONE.
		String subsysId = info.getSubsysId();
		short role = info.getRole();
		if ( AseRoles.STANDBY != role ) {
			ArrayList list = getPartitions(subsysId , AseRoles.STANDBY);
			if ( null != list ) { 
				for ( int i=0; i<list.size() ; i++ ) { 
					AsePartition partition = (AsePartition)list.get(i);
					//No more standby 
					if ( logger.isInfoEnabled() ) {
						logger.info(" Removing replication destination " + subsysId );
						logger.info ("for partition " + partition );
					}
					clusterMgr.removeReplicationDestination(subsysId );
					if ( partition.hasMember(AseRoles.NONE) ) { 
						partitions.remove(partition);
					} else { 
						partition.setMember(subsysId , AseRoles.NONE);
					}
				}
			}
		}
    AsePartition partition = getPartition(info.getFip());
    if (partition == null) {
      partition = getEmptyPartition();
      if (partition == null) {
				if (logger.isInfoEnabled()) {


				logger.info(" No empty partition is available. Now try to get partition with null fip.");
				}
      	partition = getPartiallyEmptyPartition();
        //logger.error("Unable to update partition of " + info.getFip());
      	partition.setFloatingIp(info.getFip());
      } else {
        partition.setFloatingIp(info.getFip());
      }
    } else {
      //It means that partition already exist so check whether 
      //partition has member with same role
      if ( partition.hasMember(info.getRole()) ) {
        logger.error("FATAL ERROR : conflict in roleSyncUpdate " ) ;
        return info;
      }
    }
    //update the partition
    if( role == AseRoles.RELEASE_STANDBY )
    {
      //Ashish don know wat is happening
      // Handles the case:    2 (X and Y) (there can be any number of them) Active 
      // and one STANDBY caters to them all
      // One Active (say X) goes down, the Standby becomes Active. The new Active now sends a 
      // RELEASE_STANDBY due to which removeReplicationDestination() has to be called 
      // on the Active, that is, Y
      if( partition.isMember( selfId , AseRoles.ACTIVE ) )	
      {
        if (logger.isInfoEnabled()) {


		logger.info( " Received RELEASE_STANDBY from "+info.getSubsysId() +" removing Replication Destination "+ partition.getMember(AseRoles.STANDBY));
		}
        clusterMgr.removeReplicationDestination(partition.getMember(AseRoles.STANDBY) );
      }
    }
    partition.setMember(subsysId, role);
	if (logger.isInfoEnabled()) {


    logger.info(" roleSyncUpdate()  exiting");
}
    return null;
  }

	private AsePartition getPartiallyEmptyPartition()
	{
		// A partially empty partition is the one having a member but no valid fip (that is it has 0.0.0.0 as fip
		for (int i = 0; i < this.partitions.size(); i++) {
			AsePartition partition = (AsePartition)this.partitions.get(i);
			if (partition.getFloatingIp().equals("0.0.0.0")) {
				return partition;
			}
		}
		return null;
	}
	
	public AsePartition getPartition(String fip) {
		for (int i = 0; i < this.partitions.size(); i++) {
			AsePartition partition = (AsePartition)this.partitions.get(i);
			if (partition.getFloatingIp().equals(fip)) {
				return partition;
			}
		}
		return null;
	}

	public ArrayList getPartition() { 
		return this.partitions;
	}
	
	public AsePartition getPartition(int index) {
		return (AsePartition)this.partitions.get(index);
	}
	
	public AsePartition getPartition(String fip, String subsysId, short role) {
		for (int i = 0; i < this.partitions.size(); i++) {
			AsePartition partition = (AsePartition)this.partitions.get(i);
			if (partition.getFloatingIp().equals(fip) && 
					partition.isMember(subsysId, role)) {
				return partition;
			}
		}
		return null;
	}
	
	/**
	 * Get all the partitions that have subsysId as a member with the role
	 * @param subsysId
	 * @param role
	 * @return ArrayList of AsePartition
	 */
	public ArrayList getPartitions(String subsysId, short role) {
		ArrayList partitions = null;
		for (int i = 0; i < this.partitions.size(); i++) {
			AsePartition partition = (AsePartition)this.partitions.get(i);
			if (partition.isMember(subsysId, role)) { 
				if (partitions == null) {
					partitions = new ArrayList();
				}
				partitions.add(partition);
			}
		}
		return partitions;
		
	}
	
	/**
	 * Return true if two subsys in the same partition
	 * @param subsysId1
	 * @param subsysId2
	 * @return
	 */
	public boolean inSamePartition(String subsysId1, String subsysId2) {
		for (int i = 0; i < this.partitions.size(); i++) {
			AsePartition partition = (AsePartition)partitions.get(i);
			if (partition.isMember(subsysId1) && partition.isMember(subsysId2)) {
				return true;
			}
		}
		return false;                                                                                                                                                                                                                                                                                                                                                         
	}
	
	/**
	 * Remove the subsys from all partitions. Reduce partitions, if number of
	 * partitions exceeds maxPartitions
	 * @param subsysId
	 */
	public void removeMember(String subsysId) {
		for (int i = 0; i < this.partitions.size(); i++) {
			AsePartition partition = (AsePartition)partitions.get(i);
			if (partition.isMember(subsysId)) {
				//remove partition if the number of partitions is decreased
				if (partition.isMember(subsysId, AseRoles.ACTIVE) && 
						this.partitions.size() > this.maxPartitions ) {
						this.partitions.remove(i);
				} else {
					//remove the subsystem from the partition
					partition.setMember(subsysId, AseRoles.NONE);
					if (logger.isInfoEnabled()) {


					logger.info(" removeMember() ");
					}
				}
			}
		}		
	}

	
	/**
	 * Return true if this table has any active member
	 * @return boolean
	 */
	public boolean hasActiveMembers() {
		for (int i = 0; i < this.partitions.size(); i++) {
			AsePartition partition = (AsePartition)partitions.get(i);
			if (partition.hasMember(AseRoles.ACTIVE)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return true if this table has any standby member
	 * @return
	 */
	public boolean hasStandbyMembers() {
		for (int i = 0; i < this.partitions.size(); i++) {
			AsePartition partition = (AsePartition)partitions.get(i);
			if (partition.hasMember(AseRoles.STANDBY)) {
				return true;
			}
		}
		return false;
	}
	
	public int size() {
		return partitions.size();
	}
	
	/**
	 * Set max partition size. Increase number of partitions, if current size is 
	 * less than the max size.
	 * @param maxPartitions
	 */
	public void setMaxPartitions(int maxPartitions) {
		this.maxPartitions = maxPartitions;
		if (this.partitions.size() < this.maxPartitions) {
			//increase partitions
			for (int i = this.partitions.size(); i < this.maxPartitions; i++) {
				this.partitions.add(this.factory.createPartition());
				if (logger.isInfoEnabled()) {


				logger.info(" Added partition, now table size is : " + this.partitions.size() ) ;
				}
			}
		}
	}

	public int getMaxPartitions() { 
		return this.maxPartitions;
	}
	
	public void setNoOfFips(int noOfFips){
		numberOfFIPs = noOfFips;
	}
	/**
	 * Find a partition that is not used yet
	 * @return
	 */
	private AsePartition getEmptyPartition() {
		for (int i = 0; i < this.partitions.size(); i++) {
			AsePartition partition = (AsePartition)this.partitions.get(i);
			if (partition.getState() == AsePartition.CREATE) {
				return partition;
			}
		}
		return null;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer("\n\n** Partition Table **\n");
		if(numberOfFIPs == 1 ){
			buf.append("Index\tFloating IP\tActive Member\t\tStandby Member\n");
		}else{
			buf.append("Index\tFloating IPs"+ repeatTabs() +"Active Member\t\tStandby Member\n");
		}
		for (int i = 0; i < this.partitions.size(); i++) {
			AsePartition partition = (AsePartition)this.partitions.get(i);
			buf.append(i + "\t");
			buf.append(partition + "\n");
		}		
		return buf.toString();
	}
	
	private String repeatTabs() {
		StringBuffer tabs = new StringBuffer();
		for(int i=0; i<= numberOfFIPs; i++){
			tabs.append(AseStrings.TAB);
		}
		return tabs.toString();
	}

	/**
	 * Write PartitionInfos into ObjectOutput
	 * @param out
	 * @param partitionInfos
	 * @throws Exception
	 */
	public static void writePartitionInfos(ObjectOutput out, ArrayList partitionInfos) 
			throws Exception {
		if (partitionInfos == null || partitionInfos.size() == 0) {
			if (logger.isInfoEnabled()) {
				logger.info("Nothing to write.");
			}
			return;			
		}
		for (int i = 0; i < partitionInfos.size(); i++) {
			out.writeObject(partitionInfos.get(i));
		}
	}
	
	/**
	 * Read PartitionInfos from ObjectInput into ArrayList
	 * @param in
	 * @param nPartitionInfos
	 * @return ArrayList of PartitionInfo
	 * @throws Exception
	 */
	public static ArrayList readPartitionInfos(ObjectInput in, int nPartitionInfos) 
			throws Exception {
		if (nPartitionInfos <= 0) {
			if (logger.isInfoEnabled()) {
				logger.info("Nothing to read.");
			}
			return null;
		}
		ArrayList partitionInfos = new ArrayList(nPartitionInfos);
		for (int i = 0 ; i < nPartitionInfos; i++) {
			PartitionInfo partitionInfo = (PartitionInfo)in.readObject();
			partitionInfos.add(partitionInfo);
		}
		return partitionInfos;
	}

	/**
	 * Returns the number of machines in th epartition Table
	 */
	public int getActiveMemberCount()	
	{
		int count = 0;
		for (int i = 0; i < this.partitions.size(); i++) {
			AsePartition partition = (AsePartition)this.partitions.get(i);
			if( partition.hasMember(AseRoles.ACTIVE) )
				count++;	
		}
		if (logger.isInfoEnabled()) {
			logger.info(" Partition size is : "+this.partitions.size()+" No of Active members in the Partition Table : "+count);
		}
									
		return count;
					
	}
	/*

	public ArrayList getMembers()
	{
		ArrayList members = new ArrayList();
		boolean hasGotStandBy = false;
		for (int i = 0; i < this.partitions.size(); i++) {
		AsePartition partition = (AsePartition)this.partitions.get(i);
		if( partition.hasMember(AseRoles.ACTIVE) )
			members.add( partition.getActiveMemberId() );
		if( !hasGotStandBy && partition.hasMember(AseRoles.STANDBY) )
		{
			hasGotStandBy = true;
			members.add( partition.getStandbyMemberId() );
		}

		}//for

	}*/
}
