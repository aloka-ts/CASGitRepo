/**
 * 
 */
package com.baypackets.ase.ra.telnetssh.session;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.telnetssh.LsResourceAdaptorImpl;
import com.baypackets.ase.ra.telnetssh.message.LsRequest;
import com.baypackets.ase.ra.telnetssh.message.LsRequestImpl;
import com.baypackets.ase.ra.telnetssh.utils.Constants;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.spi.resource.AbstractSession;

/**
 * The Class LsResourceSession.
 * Implements AbstractSession
 * Defines resource session
 * to which LsMessages are attached
 * LsResourceSession attaches itself to 
 * SipApplication Session
 *
 * @author saneja
 */
public class LsResourceSession extends AbstractSession implements Constants{
	private static final long serialVersionUID = -85964543333637L;
	/** The Constant logger. */
	private static final Logger logger=Logger.getLogger(LsResourceSession.class);

	/** The Constant LS_ACTIVE. */
	public static final short LS_ACTIVE = 0;	

	/** The Constant LS_INACTIVE. */
	public static final short LS_INACTIVE = 1;	

	/** The is ready for replication. */
	private boolean isReadyForReplication = true;

	/** The request. */
	private LsRequest request = null;

	/** The _session state. */
	private int _sessionState = LS_ACTIVE;

	/**Check for if lsresourceadaptor is set */
	private boolean isRAset = false; 

	/**
	 * Instantiates a new ls resource session.
	 */
	public LsResourceSession() {
	}

	/**
	 * Instantiates a new ls resource session.
	 *
	 * @param id the id
	 */
	public LsResourceSession(String id) {
		super(id);
	}

	/**
	 * Gets the session state.
	 *
	 * @return the session state
	 */
	public int getSessionState() {
		return this._sessionState;
	}

	/**
	 * Sets the session state.
	 *
	 * @param state the new session state
	 */
	public void setSessionState(int state) {
		this._sessionState = state;
		this.setModified(true);
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.resource.ResourceSession#createMessage(int)
	 */
	@Override
	public Message createMessage(int type) throws ResourceException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("entering LsResourceSession createMessage()");
		throw new ResourceException("API not supported. Use LsResourceFactory.createRequest(appSession,lsId,lsCommand)");
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractProtocolSession#getProtocol()
	 */
	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	/**
	 * Sets the request.
	 *
	 * @param request the new request
	 */
	public void setRequest(LsRequest request) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside addRequest of LsResourceSession");
		this.request=request;
		this.setModified(true);
	}

	// Replicable Interface methods

	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractProtocolSession#isReadyForReplication()
	 */
	public boolean isReadyForReplication() {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Entering LsResourceSession isReadyForReplication()");
		return isReadyForReplication;
	}

	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractProtocolSession#replicationCompleted()
	 */
	public void replicationCompleted() {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Entering LsResourceSession replicationCompleted()");
		replicationCompleted(false);

	}
	
	public void replicationCompleted(boolean noReplication) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Entering LsResourceSession replicationCompleted()"+noReplication);
		
		if(!noReplication){
			((LsRequestImpl) request).setReplicated(true);
		}
		
		super.replicationCompleted(noReplication);
		
		if(isDebugEnabled) {
			logger.debug("Leaving replicationCompleted()"+noReplication);
		}
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractProtocolSession#activate(com.baypackets.ase.spi.replication.ReplicationSet)
	 */
	public void activate(ReplicationSet parent) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Entering LsResourceSession activate()");
		//Activating parent class
		super.activate(parent);
		
		//check ra state
		try {
			LsResourceAdaptorImpl lsRAImpl=(LsResourceAdaptorImpl) LsResourceAdaptorImpl.getInstance();
			synchronized (lsRAImpl) {
				if(!lsRAImpl.isRaUp()){
					wait();
				}
			}
			if(getSessionState()==LS_ACTIVE){

				if(!isRAset){
					partialActivate(parent);
				}
				this.request.send();
			}
		} catch (Exception e) {
			logger.error("Error re-sending reuest on FT Lsid:"+request.getLsId(),e);
		}
	}


	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractProtocolSession#partialActivate(com.baypackets.ase.spi.replication.ReplicationSet)
	 */
	public void partialActivate(ReplicationSet parent) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Entering LsResourceSession partialActivate()");
		if(this.request instanceof LsRequestImpl){
			try {
				((LsRequestImpl)(this.request)).setLsResourceAdaptor(LsResourceAdaptorImpl.getInstance());
				isRAset=true;
			} catch (Exception e) {
				logger.error("Error in partialACtivate setting LsResourceAdaptor instance",e);
			}
		}
		super.partialActivate(parent);
		if(isDebugEnabled)
			logger.debug("Leaving LsResourceSession partialActivate()");
	}


	//Serialization method

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractProtocolSession#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Entering LsResourceSession writeExternal()");
		out.writeInt(this._sessionState);
		out.writeObject(this.request);
		super.writeExternal(out);
		if(isDebugEnabled)
			logger.debug("Leaving LsResourceSession writeExternal()");
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.spi.container.AbstractProtocolSession#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Entering LsResourceSession readExternal()");
		this._sessionState = (int)in.readInt();
		this.request = (LsRequest)in.readObject();
		super.readExternal(in);
		
		
		
		if(isDebugEnabled)
			logger.debug("Leaving LsResourceSession readExternal()");
	}
	
	public void invalidate () {
		if(logger.isDebugEnabled())
		logger.debug("Invalidating LsResourceSession");
		
		if(this.request!=null && request.getStatus()==LsRequest.REQUEST_PENDING && !request.isMustExecute()){
			try {
				request.cancel();
			} catch (ResourceException e) {
				logger.error("Resouce Exception in invalidate():"+e.getMessage(),e);
			}
		}
		super.invalidate();

		if(logger.isDebugEnabled())
			logger.debug("Invalidated LsResourceSession");
	}

}
