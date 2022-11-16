package com.baypackets.ase.spi.replication;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.log4j.Logger;

public class SelectiveReplicationContext extends ReplicationContextImpl {
	private static final long serialVersionUID = -34518498432439439L;
	private static Logger logger = Logger.getLogger(SelectiveReplicationContext.class);
	
	private ReplicationEvent replEvent = new ReplicationEvent(this, ReplicationEvent.REPLICABLE_CHANGED);
	
	private boolean disableCreate = false;
	private boolean useRepId = false;
	
	public SelectiveReplicationContext(){
	}
	
	public SelectiveReplicationContext(String id) {
		super(id);
		this.init();
	}
	
	protected void init(){
		setSelectiveReplication(true);
		super.replicationMgr.addReplicationContext(this);
	}

	public void cleanup() {
		super.clear();
		super.replicationMgr.removeReplicationContext(this);
		super.cleanedUp = true;
	}

	public void clear(){
		Collection replicables = super.getAllReplicables();
		if(replicables == null || replicables.size() == 0)
			return;

		Replicable[] temp = new Replicable[replicables.size()];
		replicables.toArray(temp);
		
		super.clear();
		this.replicate(temp, replEvent);
	}

	public void setReplicable(Replicable replicable) {
		if(logger.isDebugEnabled()){
			logger.debug("setReplicable:");
		}
		super.setReplicable(replicable);
         replicate(replicable, replEvent);
	}
	
	public void setReplicable(Replicable replicable, boolean replicate) {
		if(logger.isDebugEnabled()){
			logger.debug("setReplicable: isReplicate::"+replicate);
		}
		super.setReplicable(replicable);
        if(replicate){
        	replicate(replicable, replEvent);
        }
	}

	public void removeReplicable(String replicableId) {
		if(logger.isDebugEnabled()){
			logger.debug("removeReplicable(replicableid)::");
		}
		Replicable replicable = super.getReplicable(replicableId);
		if(replicable == null){
			if(logger.isDebugEnabled()){
				logger.debug("removeReplicable: Replicable not found for ID :" + replicableId);
			}
			return;
		}
	   ReplicationEvent replEvent = new ReplicationEvent(this, ReplicationEvent.CLEAN_UP);
		super.removeReplicable(replicableId);
		this.replicate(new Replicable[]{replicable}, replEvent);
	}
	
	public void replicate(Replicable replicable, ReplicationEvent event) {
		if(logger.isDebugEnabled()){
			logger.debug("replicate(replicable,event)");
		}
		if(replicable == null){
			throw new IllegalArgumentException("Replicable object cannot be NULL.");
		}
		this.replicate(new Replicable[]{replicable}, event);
	}
	
	public void replicate(Replicable[] replicables, ReplicationEvent event){
		if(logger.isDebugEnabled()){
			logger.debug("replicate(replicable[],event)");
		}
		event = (event == null) ? this.replEvent : event;
		String eventId = event.getEventId();
		eventId = (eventId == null) ? "" : eventId;
		
		if(replicables == null || replicables.length == 0){
			throw new IllegalArgumentException("Replicable object list cannot be NULL/Empty.");
		}
		
		if(isCleanedUp()){
			if(logger.isDebugEnabled()){
				logger.debug("This replication context is already cleaned up. So not replicating for:" + eventId );
			}
			return;
		}
		
		if(!isDistributable()){
			if(logger.isDebugEnabled()){
				logger.debug("This replication context is not distributable. So not replicating for :" +  eventId);
			}
			return;
		}
	
		if(!isActive()){
			if(logger.isDebugEnabled()){
                                logger.debug("This replication context is not active. So not replicating for :"+eventId);
			}
			return;
		}
		
		ArrayList<String> replicableIds = new ArrayList<String>();
		for(int i=0; i<replicables.length;i++){
			if(replicables[i] == null)
				continue;
			
			replicableIds.add(replicables[i].getReplicableId());
		}
		
		if(replicableIds.size() == 0){
			if(logger.isDebugEnabled()){
				logger.debug("No valid replicables found in the list. So not replicating :"+ eventId);
			}
			return;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("TO REPLICATE :" + replicableIds + ", EVENT_ID :" + eventId);
		}
		
		String[] temp = new String[replicableIds.size()];
		replicableIds.toArray(temp); 

		event.setReplicationContextId(getId());
		super.replicationMgr.replicate(this, event, temp,disableCreate,useRepId);
	}
	
	/**
	 * @param disableCreate the disableCreate to set
	 */
	public void setDisableCreate(boolean disableCreate) {
		this.disableCreate = disableCreate;
	}

	/**
	 * @return the disableCreate
	 */
	public boolean isDisableCreate() {
		return disableCreate;
	}

	/**
	 * @param useRepId the useRepId to set
	 */
	public void setUseRepId(boolean useRepId) {
		this.useRepId = useRepId;
	}

	/**
	 * @return the useRepId
	 */
	public boolean isUseRepId() {
		return useRepId;
	}
	
	
	
}
