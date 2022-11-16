package com.baypackets.ase.spi.replication;

import com.agnity.redis.client.RedisWrapper;

public interface ReplicationManager {

	public void replicate(ReplicationContextImpl ctxt, ReplicationEvent event);
	public void removeReplicationDestination( String peerId);

	//Added for supporting the selective replication
	public void replicate(ReplicationContextImpl ctxt, ReplicationEvent event, String[] replicableIds);
	public void addReplicationContext(ReplicationContextImpl ctxt);
	public void removeReplicationContext(ReplicationContextImpl ctxt);
	
	/**
	 * Overloaded method with disable create flag and use repId flad
	 * this is done to avoid creation of replication context if out of sequencing
	 * happens on datachannel counter
	 * and distruibute tcap load on differnet channels/hreads during replication
	 */
	public void replicate(ReplicationContextImpl ctxt, ReplicationEvent event,
					String[] replicableIds, boolean disableCreate, boolean useRepId);
	
	
	/**
	 * Method used on standby SAS. This method removes all replication contexts for \
	 * an application if it is deactivated from the SAS
	 */
	public void removeContextsForAppId(String applicationId);
	
	public RedisWrapper getRedisWrapper();
}
