/**
 * SpecialReplicationActivator is special class 
 * which will activate selected Application contexts \
 * on priorty in case of FT.
 * Special Contexts are like Tcap HB appsesion contexts  
 * This is done as in case of normal FT these appsession may activate 
 * later causing some calls to fail.
 * Priorty of SpecialReplicationActivator is decided by Replication manger impl which instatiates this class.
 * @author saneja
 * @date 20 March 2013
 */
package com.baypackets.ase.replication;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;

import com.baypackets.ase.channel.ReplicationContext;
import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.RoleChangeListener;


public class SpecialReplicationActivator implements RoleChangeListener{
	private static final Logger logger = Logger.getLogger(SpecialReplicationActivator.class);
	private static SpecialReplicationActivator specialReplicationActiator;
	private SpecialReplicationActivator(){
		specialCtxtSet =new CopyOnWriteArraySet<ReplicationContext>();
	}
	
	private Set<ReplicationContext> specialCtxtSet;
	private boolean activated;
	
	public boolean isActivated() {
		return activated;
	}
	/**
	 * Gets the single instance of SpecialReplicationActiator.
	 *
	 * @return single instance of SpecialReplicationActiator
	 * 
	 */
	public static SpecialReplicationActivator getInstance(){
		if (specialReplicationActiator == null) {
			synchronized (SpecialReplicationActivator.class) {
				if (specialReplicationActiator == null) {
					specialReplicationActiator = new SpecialReplicationActivator();
				}//end inner if
			}//end synch
		}//end getInstance
		return specialReplicationActiator;
	}
	@Override
	public void roleChanged(String clusterId, PartitionInfo partitionInfo) {
		if (logger.isDebugEnabled()) {
			logger.debug("roleChanged(): SpecialReplicationActivator Subsystem role in cluster has been changed ");
		}
		short role = partitionInfo.getRole();

		if (logger.isDebugEnabled()) {
			logger.debug("roleChanged(): Subsystem role in cluster has been changed to: "
					+ AseRoles.getString(role));
		}

		if (role == AseRoles.ACTIVE) {
			activateContexts();
		}
		logger.error("roleChanged():Activated Special contexts ");
		
	}
	
	public void activateContexts() {
		if (logger.isDebugEnabled()) {
			logger.debug("activate special contexts ");
		}
		activated=true;
		for(ReplicationContext rc :specialCtxtSet ){
			try{
				rc.activate();
			}catch (Exception e) {
				logger.error("activateContexts(): Error actvating context::"+rc,e);
			}
		}
		
	}
	public void addReplicationContext(ReplicationContext replicationContext){
		specialCtxtSet.add(replicationContext);
	}
	
	public void removeReplicationContext(ReplicationContext replicationContext){
		specialCtxtSet.remove(replicationContext);
	}
}
