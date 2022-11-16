package com.baypackets.ase.ra.ro.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.ro.Constants;
import com.baypackets.ase.ra.ro.RoRequest;
import com.baypackets.ase.ra.ro.stackif.CondorStackInterface;
import com.baypackets.ase.ra.ro.stackif.CreditControlRequestImpl;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.spi.resource.AbstractSession;
import com.baypackets.ase.spi.resource.MessageFactory;

public class RoSession extends AbstractSession
{
	private static Logger logger = Logger.getLogger(RoSession.class);

	public static final short RO_PENDING = 0;
	public static final short RO_ACTIVE = 1;
	public static final short RO_INACTIVE = 2;

	private MessageFactory mf;
	private int roState = RO_PENDING;
	private int _handle = -1;
	private boolean isReadyForReplication = true;
	private ArrayList requests = new ArrayList(1);

	public RoSession() {
		this.mf = RoResourceAdaptorImpl.getResourceContext().getMessageFactory();
	}
	
	public RoSession(String id) 
	{
		super(id);
		logger.debug("Inside the constructor of RoSession(String)");
		
	}
	
	public int getHandle() {
		return this._handle;
	}

	public void setHandle(int handle) {
		this._handle = handle;
	}

	public int getRoState() {
		return this.roState;
	}

	public void setRoState(int state) {
		this.roState = state;
	}

	public String getProtocol() {
		return Constants.PROTOCOL;
	}

	public void addRequest(RoRequest request ){
		logger.debug("inside addRequest of RoSession");
		if(request != null && this.requests.indexOf(request) == -1) {
			logger.debug("adding the request to list " + request);
			this.requests.add(request);
		}
	}

	public Message createMessage(int type) throws ResourceException {
		return (Message)mf.createRequest(this, type);
	}
	
	///////////// Replicable Interface method ////////////////////

	// This method adds the request attached with this session into
	// the hashMap of stackInterfacelayer.

	public void partialActivate(ReplicationSet parent) {	
		for(int i=0;i<this.requests.size();i++){
			((CondorStackInterface)RoResourceAdaptorImpl.getStackInterface()).addRequestToMap(this._handle , (RoRequest)this.requests.get(i));
		}
	}
 
	public boolean isReadyForReplication() {
		logger.debug("Entering RoSession isReadyForReplication()");
		return isReadyForReplication;
	}

	public void replicationCompleted() {
		logger.debug("Entering RoSession replicationCompleted()");
		replicationCompleted(false);
		logger.debug("Leaving RoSession replicationCompleted()");
	}
	
	
	public void replicationCompleted(boolean noReplication) {
        if (logger.isDebugEnabled()) { 
 	 	 	 	 logger.debug("Entering RoSession replicationCompleted()"+noReplication);
		}
		if (!noReplication) {
			Iterator itr = this.requests.iterator();
			while(itr.hasNext()) {
				RoRequest request = (RoRequest)itr.next();
				((CreditControlRequestImpl)request).isAlreadyReplicated(true);
			}
		}
        if (logger.isDebugEnabled()) { 
 	 	 	 	 logger.debug("Leaving RoSession replicationCompleted()");
		}
	}
	
	public void writeExternal(ObjectOutput out) throws IOException {
		logger.debug("Entering RoSession writeExternal()");
		out.writeInt(this._handle);
		out.writeInt(this.roState);
		out.writeObject(this.requests);
		logger.debug("Leaving RoSession writeExternal()");
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		logger.debug("Entering RoSession readExternal()");
		this._handle = (int)in.readInt();
		this.roState = (int)in.readInt();
		this.requests = (ArrayList)in.readObject();
		logger.debug("Leaving RoSession readExternal()");
	}
}
