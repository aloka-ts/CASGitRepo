package com.baypackets.ase.spi.replication;

import java.util.Iterator;

import org.apache.log4j.Logger;

public class SelectiveRepContextActivator implements Runnable{
	private static final Logger logger = Logger.getLogger(SelectiveRepContextActivator.class);
	private Replicables replicables = null;
	private ReplicationSet parentReplicationSet = null;
	
	public SelectiveRepContextActivator( Replicables replicables, ReplicationSet parent) {
		this.replicables = replicables;
		this.parentReplicationSet = parent;
	}
	
	
	@Override
	public void run() {
		logger.error("Starting SelectiveRepContextActivator run method..");
		Iterator iter = replicables.getAllReplicables().iterator();
		while(iter.hasNext()) {
			Replicable rep = (Replicable)iter.next();
				rep.activate(parentReplicationSet);
		}
		logger.error("Leaving SelectiveRepContextActivator run method..");
	}
	
	
	
}