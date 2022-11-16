package com.baypackets.ase.tomcat;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.ha.jmx.ClusterJmxHelper;
import org.apache.catalina.ha.tcp.SimpleTcpCluster;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.util.Constants;

public class ConvergedMcastServiceImpl extends SimpleTcpCluster{
	private static Logger logger = Logger.getLogger(ConvergedMcastServiceImpl.class);

	public ConvergedMcastServiceImpl() {
		super();
	}
	 
	/**
     * This method is overridden to register ConvergedMcastServiceImpl with JMX Service 
     * 
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    @Override
    protected void startInternal() throws LifecycleException {
    	logger.info("Cluster is about to start");
        try {
            checkDefaults();
            registerClusterValve();
            channel.addMembershipListener(this);
            channel.addChannelListener(this);
            channel.start(getChannelStartOptions());
            if (getClusterDeployer() != null) getClusterDeployer().start();
            ClusterJmxHelper.getRegistry().loadMetadata(this.getClass().getResourceAsStream("mbeans-descriptors.xml"));
            ClusterJmxHelper.registerDefaultCluster(this);
            // Notify our interested LifecycleListeners
        } catch (Exception x) {
            logger.error("Unable to start cluster.", x);
            throw new LifecycleException(x);
        }
        
        setState(LifecycleState.STARTING);
    }
	/**
	 * Method will get called if replication enables and one of the member in the cluster shutdowns
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void memberDisappeared(Member disappearedMember) {
		if (logger.isDebugEnabled()) 
		logger.debug("Entering memberDisappeared method of ConvergedMcastServiceImpl... "+disappearedMember);
		super.memberDisappeared(disappearedMember);
		//if replication enables then assign application session to the converged object and add converged object inside the 
		//application session.
		if(EmbeddedTomcat.convergedSessionRepEnable){
			
			Map sessionsMap =  ConvergedDeltaManagerImpl.getInstance().getAllSessions();
			Collection sessionsValues =  sessionsMap.values();
			Iterator itr = sessionsValues.iterator();
			while(itr.hasNext()){
				Object objSession= itr.next();
				String applSessionId =  ((ConvergedDeltaHttpSessionImpl)objSession).appSessionId;
				AseHost host = (AseHost)Registry.lookup(Constants.NAME_HOST);
				if(applSessionId!=null)
				{
					AseApplicationSession sipApplicationSession= (AseApplicationSession)host.getApplicationSession(applSessionId);
					if(sipApplicationSession != null){
						((ConvergedDeltaHttpSessionImpl)objSession).appSession = sipApplicationSession;
						sipApplicationSession.addProtocolSession(((ConvergedDeltaHttpSessionImpl)objSession));
					}
				}
			}
		}
		if (logger.isDebugEnabled()) 
		logger.debug("Leaving memberDisappeared method of ConvergedMcastServiceImpl...");
	}
	
}
