/*
 * Created on Oct 26, 2004
 *
 */
package com.baypackets.ase.replication;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * @author Ravi
 */
public class ReplicationInfo {
	private static Logger logger = Logger.getLogger(ReplicationInfo.class);
	private String clusterId;
	private short sequenceNo;
	private boolean activated = false;
	private boolean replicated = false;
	private long lastReplicationTime;
	private HashMap destinations= new HashMap(1);	

	public ReplicationInfo() {
		if (logger.isDebugEnabled())
			logger.debug("Creating new ReplicationInfo object");	
	}

	public String getClusterId() {
		return clusterId;
	}

	public void setClusterId(String id) {
		clusterId = id;
	}
	
	public short getSequenceNo(){
		return this.sequenceNo;
	}
	
	public void setSequenceNo(short seq){
		this.sequenceNo = seq;
	}

	public String[] getDestinations(){
	 	String[] dests = new String[this.destinations.size()];
		for(int i=0;i<this.destinations.size();i++){
			Iterator iterator = this.destinations.values().iterator();	
			while ( iterator.hasNext()) {
				ReplicationDestination dest = (ReplicationDestination)iterator.next();
				dests[i] = dest.name;
			}
		}
		return dests;
	}

	public void addDestination(String subsysId){
		ReplicationDestination subsys = new ReplicationDestination(subsysId);
			if(!this.destinations.containsValue(subsys)){
			this.destinations.put( subsysId, subsys );
			}
	}
	
	public void removeDestination(String subsysId){
			this.destinations.remove(subsysId);

	}
	
	public long getLastReplicatedOn(String dest){
		ReplicationDestination destination = this.getDestination(dest);
		return destination != null ? destination.lastReplicatedTime : 0;
	}
	
	public int getLastSequenceNo(String dest){
		ReplicationDestination destination = this.getDestination(dest);
		int retNum = destination != null ? destination.lastSeqNo : 0;
		if(logger.isDebugEnabled()) {
			logger.debug("getLastSequenceNo: dest <" + dest + "> seq-no<" + retNum + ">");
		}
		return retNum;
	}
	
	public void setLastReplicatedOn(String dest, long timestamp, short sequenceNo){
		if(logger.isDebugEnabled()) {
			logger.debug("setLastReplicatedOn: dest <" + dest + "> seq-no <" + sequenceNo + ">");
		}
		ReplicationDestination destination = this.getDestination(dest);
		if(destination == null){
			destination = new ReplicationDestination(dest);
			this.destinations.put(dest, destination);
		}
		destination.lastReplicatedTime = timestamp;
		destination.lastSeqNo = sequenceNo;
	}
	
	private ReplicationDestination getDestination(String dest){

		return (ReplicationDestination)this.destinations.get(dest);
	} 

	class ReplicationDestination{
		
		private String name;
		private long lastReplicatedTime;
		private short lastSeqNo;
		
		ReplicationDestination(String name){
			this.name = name;
		}
		
		public boolean equals(Object other){
			if(other == this)
				return true;
			if(!(other instanceof ReplicationDestination))
				return false;
			return this.name.equals(((ReplicationDestination)other).name);	
		}
	}

	public long getLastReplicationTime() {
		return lastReplicationTime;
	}

	public void setLastReplicationTime(long l) {
		lastReplicationTime = l;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setActivated(boolean b) {
		activated = b;
	}

	public boolean isReplicated() {
		return replicated;
	}

	public void setReplicated(boolean b) {
		replicated = b;
	}
}
