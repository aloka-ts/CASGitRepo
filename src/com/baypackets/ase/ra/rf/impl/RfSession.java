package com.baypackets.ase.ra.rf.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.rf.Constants;
import com.baypackets.ase.ra.rf.RfRequest;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.spi.resource.AbstractSession;
import com.baypackets.ase.spi.resource.MessageFactory;
import com.baypackets.ase.spi.resource.ResourceContext;

/** 
 * This class defines the protocol session in which all the processing 
 * of Rf Resource adapter happens. All the requests , responses and events 
 * are processed inside this session.
 *
 * @author Neeraj Jadaun

 */
public class RfSession extends AbstractSession implements Constants 
{
	private static Logger logger = Logger.getLogger(RfSession.class);
	private ResourceContext context;

	private int state = PENDING;		//Pending State:0, Active State:1, Inactive State:-1
	private int handle =0;
	private ArrayList requests = new ArrayList(1);
	private MessageFactory mf;

	/**
 	 * This method creates a new instance of RfSession
	 *
	 */	
	public RfSession() 
	{
		if(logger.isDebugEnabled())
			logger.debug("Inside the constructor of RfSession");
	}

	/**
	 * This method creates a new instance of RfSession with specified id
	 *
	 */	
	public RfSession(String id) 
	{
		super(id);
		if(logger.isDebugEnabled())
			logger.debug("Inside the constructor of RfSession(String)");
	}

	public void setHandle(int x)
	{
		if(logger.isDebugEnabled())
			logger.debug("setHandle() called.");
		this.handle = x;
	}
	public int getHandle()
	{
		if(logger.isDebugEnabled())
			logger.debug("setHandle() called.");
		return this.handle;
	}

	/**
	 * This method sets the RfSession state
	 * INACTIVE = -1
	 * ACTIVE = 1  
	 * PENDING = 0
	 */
	public void setRfSessionState(int x)
	{
		if(logger.isDebugEnabled())
			logger.debug("setRfSessionState() called.");
		this.state = x;
	}

	/**
	 * This method returns the RfSession state
	 *
	 * @return RfSession state
	 *INACTIVE = -1
	 * ACTIVE = 1
	 * PENDING = 0
	 */
	public int getRfSessionState()
	{
		if(logger.isDebugEnabled())
			logger.debug("setRfSessionState() called.");
		return this.state;
	}

	/** This method returns the RfSession id 
	 *
	 * @return String object containing the RfSession id
	 */
	public String getSessionId()
	{
		if(logger.isDebugEnabled())
			logger.debug("getSessionId() called.");
		return getId();
	}

	/**
	 * This method returns the protocol associated with this resource
	 *
	 * @return String object containing protocol associated with this resource
	 */
	public String getProtocol() 
	{
		if(logger.isDebugEnabled())
			logger.debug("getProtocol() called.");
		return PROTOCOL;
	}
	
	/**
	 * This method creates a new Message depending on the type passed in the parameter
	 * type can be 
	 * EVENT = 1
	 * SESSION = 2
	 *
	 * @return a newly created message 
	 */
	public Message createMessage(int type) throws ResourceException 
	{
		if(logger.isDebugEnabled())
			logger.debug("createMessage() called.");
		//return (Message)RfResourceAdaptorImpl.getResourceContext().getMessageFactory().createRequest(this, type);
		return (Message)this.getResourceContext().getMessageFactory().createRequest(this, type);
		//return (Message)mf.createRequest(this, type);	
	}

	/**
	 * This Method returns the Resource Context associated with the session
	 *
	 * @return <code>ResourceContext</code> object associated 
	 */
	
	public ResourceContext getResourceContext() 
	{
		if(logger.isDebugEnabled())
			logger.debug("getResourceContext() called.");
		return this.context;
	}
	
	/**
	 * This Method associates the resource context with this session
	 *
	 * @param context - resource context to be associated with this session
	 */	
	
	public void setResourceContext(ResourceContext context) 
	{
		if(logger.isDebugEnabled())
			logger.debug("setResourceContext() called.");
		this.context = context;
	}
		
	public void addRequest(RfRequest request ) {
		if(logger.isDebugEnabled())
			logger.debug("inside addRequest of RfSession");
		if(request != null && this.requests.indexOf(request) == -1 ) {
			this.requests.add(request);
		}
	}

	///////////// Replicable Interface method ////////////////////

	// This method adds the request attached with this session into 
	// the hashMap of stackInterfacelayer.
	 

	public void partialActivate(ReplicationSet parent) {
		 for(int i=0;i<this.requests.size();i++){
			((CondorStackInterface)RfResourceAdaptorImpl.getStackInterface()).addRequestToMap(this.handle , (RfRequest)this.requests.get(i));
		}
	}

	public boolean isReadyForReplication() {
		return isReadyForReplication;
	}
	
	public void replicationCompleted() {
		replicationCompleted(false);
	}
	
	public void replicationCompleted(boolean noReplication) {
        if (logger.isDebugEnabled()) { 
 	 	 	 	 logger.debug("Entering RfSession replicationCompleted()"+noReplication);
		}
		if (!noReplication) {
			Iterator itr = this.requests.iterator();
			while(itr.hasNext()) {
				RfRequest request = (RfRequest)itr.next();
				((RfAccountingRequest)request).isAlreadyReplicated(true);
			}
		}
        if (logger.isDebugEnabled()) { 
 	 	 	 	 logger.debug("Leaving RfSession replicationCompleted()");
		}
	}
	
	
	

	public void writeExternal(ObjectOutput out) throws IOException {
		if(logger.isDebugEnabled())
			logger.debug("Entering RfSession writeExternal()");
		out.writeInt(this.state);	
		out.writeInt(this.handle);
		out.writeObject(this.requests);
		if(logger.isDebugEnabled())
			logger.debug("Leaving RfSession writeExternal()");	
	}

	public void readExternal(ObjectInput in ) throws IOException , ClassNotFoundException {
		if(logger.isDebugEnabled())
			logger.debug("Entering RfSession readExternal()");
		this.state = (int)in.readInt();
		this.handle = (int)in.readInt();
		this.requests = (ArrayList)in.readObject();
		if(logger.isDebugEnabled())
			logger.debug("Leaving RfSession readExternal()");
	}
	
	private boolean isReadyForReplication = true;

}
