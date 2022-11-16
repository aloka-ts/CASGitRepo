/*
 * Created on Oct 25, 2004
 *
 */
package com.baypackets.ase.spi.replication;

import java.util.Collection;

/**
 */
public interface ReplicationSet {
	
	public void setReplicable(Replicable replicable);
	
	public void removeReplicable(String id);
	
	public Replicable getReplicable(String id);
	
	public Collection getAllReplicables();
	
	public void clear();
}
