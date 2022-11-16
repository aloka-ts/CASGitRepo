package com.baypackets.ase.spi.replication;

public interface ReplicationListener {
	public int handleReplicationEvent(ReplicationEvent event);
}
