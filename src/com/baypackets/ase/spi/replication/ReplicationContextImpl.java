package com.baypackets.ase.spi.replication;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.channel.ReplicationContext;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class ReplicationContextImpl extends Replicables implements ReplicationContext, ReplicationListener {
	private static final long serialVersionUID = -345182098432439439L;
	private static Logger logger = Logger.getLogger(ReplicationContextImpl.class);
	transient private String id;
	transient protected boolean active = false;
	transient protected boolean cleanedUp = false;
	transient private boolean distributable = true;
	
	transient protected Object info;
	transient private String clusterId;
	transient private String subsystemId;
	
	transient protected ReplicationManager replicationMgr;
	
	public ReplicationContextImpl() {
		replicationMgr = (ReplicationManager)Registry.lookup(Constants.NAME_REPLICATION_MGR);
	}

	public ReplicationContextImpl(String id) {
		this.id = id;
		super.setReplicableId(id);
		replicationMgr = (ReplicationManager)Registry.lookup(Constants.NAME_REPLICATION_MGR);
	}

	public String getId() {
		return this.id;
	}

	public String getClusterId() {
		return this.clusterId;
	}

	public void partialActivate() {
		if(logger.isDebugEnabled())
		logger.debug("Entering ReplicationContextImpl partialActivate()");
		//active=true;
		super.partialActivate(this);
		if(logger.isDebugEnabled())
		logger.debug("Leaving ReplicationContextImpl partialActivate()");
	}
	public void activate() {
		super.activate(this);
		this.active = true;
	}

	public void cleanup() {
		// Clear Replicables
		if (this.active) {
			if(logger.isDebugEnabled()) {
				logger.debug("Creating the CLEANUP message for the IC :" + this.id);
			}
        	
			// Generates ReplicationMessage.CLEANUP packet and replicates 
			replicationMgr.replicate(this, new ReplicationEvent(this, ReplicationEvent.CLEAN_UP));
		} else {
			if(logger.isDebugEnabled()) {
				logger.debug("Not Creating the CLEANUP message for this IC :" + this.id
					+". Since it is not ACTIVE");
			}
		}
        
		this.cleanedUp = true;
	}

	public boolean isCleanedUp() {
		return this.cleanedUp;
	}

	public Collection getAppInfo() {
		return null;
	}
	
	public boolean hasApplication(String id){
		return false;
	}

	public String getSubsystemId() {
		return this.subsystemId;
	}

	public Object getReplicationInfo() {
		return this.info;
	}

	public void setReplicationInfo(Object info) {
		this.info = info;
	}

	public int handleReplicationEvent(ReplicationEvent event) {
   		if(logger.isEnabledFor(Level.INFO)){
			logger.info("handleReplicationEvent :" + event.getEventId() + " called for :" + this);
   		}
   
   		//If the IC is already cleanedUp, no need to send this event, just ignore it.
		if(this.cleanedUp){
			if(logger.isInfoEnabled()){
				logger.info("Received a replication event :" + event.getEventId() + 
						 " after the cleanup of IC :" + this.id +". So ignoring it....");
			}
			return 0;
		}
		
		//Here we will check if the IC is replicable or not.
		//In case, the IC is not distributable, all the replication events (except CLEAN_UP) will be discarded here.
		//The cleanup event is allowed here because, 
		//if there were any replication happened prior to switching this IC to non-distributable,
		//then we need to take care of cleaning it up on the other side.
		if(!this.distributable && !event.getEventId().equals(ReplicationEvent.CLEAN_UP)){
			if(logger.isInfoEnabled()){
				logger.info("Distributable flag is FALSE for this IC :" + this.id + 
						 ". So ignoring this replication event :" + event.getEventId());
			}
			return 0;
		}		
   		
   		//do the replication
   		event.setReplicationContextId(this.id);

   		replicationMgr.replicate(this, event);
   		
		if(logger.isEnabledFor(Level.INFO)) {
			logger.info("Replication event :" + event.getEventId() + " completed for :" + this);
		}
   		
		return 0;
	}

	public boolean isActive() {
//		AseSipConnector connector = (AseSipConnector)Registry.lookup("SIP.Connector");
//		return connector.getRole() == 1;
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isDistributable() {
		return distributable;
	}

	public void setDistributable(boolean distributable) {
		this.distributable = distributable;
	}
	
	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		this.distributable = in.readBoolean();
		super.readIncremental(in);
	}

	public void writeIncremental(ObjectOutput out, int replicationType) throws IOException {
   		if(logger.isEnabledFor(Level.INFO)){
			logger.info("writeIncremental() :" + " called for ReplicationContextImpl");
   		}
		out.writeBoolean(distributable);
		super.writeIncremental(out, replicationType);
	}
	
	public void writeIncremental(ObjectOutput out, String[] replicableIds) throws IOException {
        out.writeBoolean(distributable);
		super.writeIncremental(out, replicableIds);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.id = (String) in.readObject();
		this.distributable = in.readBoolean();
		super.readExternal(in);
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(id);
		out.writeBoolean(distributable);
		super.writeExternal(out);
	}

	public void setClusterId(String clusterId) {
		this.clusterId = clusterId;
	}

	public void setSubsystemId(String subsystemId) {
		this.subsystemId = subsystemId;
	}
}
