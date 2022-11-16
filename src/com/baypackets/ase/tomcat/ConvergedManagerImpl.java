package com.baypackets.ase.tomcat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Session;
import org.apache.catalina.ha.session.DeltaManager;
import org.apache.catalina.ha.session.DeltaRequest;
import org.apache.catalina.ha.session.DeltaSession;
import org.apache.catalina.tribes.io.ReplicationStream;
import org.apache.log4j.Logger;

public class ConvergedManagerImpl extends DeltaManager {

	private static Logger logger = Logger.getLogger(ConvergedManagerImpl.class);
	private static ConvergedManagerImpl convergedManager = null;

	public ConvergedManagerImpl() {
		super();
		convergedManager = this;
	}
	
	public static ConvergedManagerImpl getInstance() {
		if (logger.isDebugEnabled()) 
		logger.debug("Inside getInstance() method of ConvergedManagerImpl) ");
		return convergedManager;
	}
	

	@Override
	protected DeltaSession getNewDeltaSession() {
		if (logger.isDebugEnabled()) 
		logger.debug("Inside getNewDeltaSession of ConvergedManagerImpl...");
		ConvergedHttpSessionImpl httpSession = new ConvergedHttpSessionImpl(this);
		return httpSession;
	}

/*
 * This method is overridden to avoid null pointer exception when converged Session replication is not enabled and cluster is not created  
 * @see org.apache.catalina.ha.session.DeltaManager#createSession(java.lang.String)
 */
	   @Override
	    public Session createSession(String sessionId) {		   
		   return createSession(sessionId, EmbeddedTomcat.convergedSessionRepEnable);
	    }
	
	
	/*
	 * @see org.apache.catalina.cluster.session.DeltaManager# deserializeDeltaRequest(org.apache.catalina.cluster.session.DeltaSession, byte[])
	 * Overriding in order to read ConvergedHttpSessionImpl 
	 */
	
	@Override
	protected DeltaRequest deserializeDeltaRequest(DeltaSession session, byte[] data)
	throws ClassNotFoundException, IOException {
		if (logger.isDebugEnabled()) 
		logger.debug("Entering loadDeltaRequest method in ConvergedManagerImpl..");
		 try {
	            session.lock();
	            ReplicationStream ois = getReplicationStream(data);
		if(EmbeddedTomcat.convergedSessionRepEnable){
			((ConvergedHttpSessionImpl)session).readExternal(ois);
		}
		session.getDeltaRequest().readExternal(ois);
		ois.close();
		return session.getDeltaRequest();
	        }finally {
	            session.unlock();
	        }
	}
	
	/*
	 * @see org.apache.catalina.cluster.session.DeltaManager#unloadDeltaRequest(org.apache.catalina.cluster.session.DeltaRequest)
	 * Overriding in order to write ConvergedHttpSessionImpl 
	 */
	
	//@Override
	protected byte[] serializeDeltaRequest(DeltaSession session, DeltaRequest deltaRequest) throws IOException {
		if (logger.isDebugEnabled()) 
		logger.debug("Entering unloadDeltaRequest method in ConvergedManagerImpl..");
		   try {
	            session.lock();
	            String sessionId = deltaRequest.getSessionId();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		ConvergedHttpSessionImpl convergedHttpSessionImpl = (ConvergedHttpSessionImpl) findSession(sessionId);
		if(EmbeddedTomcat.convergedSessionRepEnable){
			convergedHttpSessionImpl.writeExternal(oos);
		}
		deltaRequest.writeExternal(oos);
		return bos.toByteArray();
	        }finally {
	            session.unlock();
	        }
	}
	

	 /**
     * Overriding this method from tomcat.
     * If converged session replication flag is disabled then getting exception while stopping tomcat
     * This is because in that case we don't have have cluster configuration.
     * So, we have applied null check before calling "getCluster().removeManager(getName(),this)"; 
     * There are some member variable which is not visible here, so ignoring because we are stopping and on restart,
     * they will initialize it again. 
     */
	@Override
	public void stopInternal() throws LifecycleException {
		logger.debug("Entering stopInternal() method of ConvergedManagerImpl..");	
		setState(LifecycleState.STOPPING);		
		Session[] sessions = findSessions();
		for (int i = 0; i < sessions.length; i++) {
			DeltaSession session = (DeltaSession)sessions[i];
			if (!session.isValid())
				continue;
			try {
				session.expire(true, isExpireSessionsOnShutdown());
			} catch (Throwable t) {
				;
			} 
		}
		if(getCluster() != null){
			getCluster().removeManager(this);
		}
		 logger.debug("Exitting stopInternal() method of ConvergedManagerImpl..");
	}
	
	@SuppressWarnings("unchecked")
	public Map getAllSessions(){		
		return sessions;
	}
	
	 /**
	  *If converged session replication flag is disabled then getting exception while starting tomcat
     * This is because in that case we don't have have cluster configuration. 
     * If cluster is null then startInternal() of DeltaManager simply returns without changing state,
     * it does not set LifeCycle state as STARTING so we get exception so we overridden this method and set LifeCycleState as STARTING 
     * even cluster is null.
     * @exception LifecycleException if this component detects a fatal error
     *  that prevents this component from being used
     */
    @Override
    protected synchronized void startInternal() throws LifecycleException {
    	super.startInternal();
    	if (cluster == null) {
    		logger.error(sm.getString("deltaManager.noCluster", getName()));
    		setState(LifecycleState.STARTING);
    		return;
    	}       
    }
}
