package com.baypackets.ase.ra.diameter.rf.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.rf.RfRequest;
import com.baypackets.ase.ra.diameter.rf.stackif.RfStackInterfaceImpl;
import com.baypackets.ase.ra.diameter.rf.utils.Constants;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.spi.resource.AbstractSession;
import com.baypackets.ase.spi.resource.ResourceContext;

public class RfSession extends AbstractSession implements Constants {

	private static Logger logger = Logger.getLogger(RfSession.class);

	public static final short RF_PENDING = 0;	
	public static final short RF_ACTIVE = 1;	
	public static final short RF_INACTIVE = 2;	
	private static RfResourceAdaptorImpl rfAdaptor;

	private int _handle = -1;
	private int _shState = RF_PENDING;
	private ArrayList requests = new ArrayList(1);
	private boolean isReadyForReplication = true;

	private ResourceContext context;
	
	public RfSession() {
	}
	
	public RfSession(String id) {
		super(id);
	}
	
	public int getHandle() {
		return this._handle;
	}

	public void setHandle(int handle) {
		this._handle = handle;
	}

	public int getShState() {
		return this._shState;
	}

	public void setShState(int state) {
		this._shState = state;
	}
	
	public String getProtocol() {
		return PROTOCOL;
	}

	public Message createMessage(int type) throws ResourceException {
		logger.debug("entering ShSession.createMessage()");
		//return (Message)this.context.getMessageFactory().createRequest(this, type);
		return (Message)RfResourceAdaptorImpl.getResourceContext().getMessageFactory().createRequest(this, type);
	}

	/**
	 * This Method returns the Resource Context associated with the session
	 *
	 * @return <code>ResourceContext</code> object associated 
	 */
	
	public ResourceContext getResourceContext() 
	{
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
		logger.debug("setResourceContext() called.");
		this.context = context;
	}
	public void addRequest(RfRequest request) {
		logger.debug("Inside addRequest of RfSession");
		if(request!= null && this.requests.indexOf(request)== -1) {
		logger.debug("adding the request to list " + request);
		this.requests.add(request);
		}
	}

	public void removeRequest(RfRequest request) {
		logger.debug("Inside removeRequest of RfSession");
		if (request != null) {
			logger.debug("Removing the request from list " + request);
			this.requests.remove(request);
		} else {
			logger.debug("Request is null");
		}
	}

	///////////// Replicable Interface method ////////////////////

	// This method adds the request attached with this session into
	// the hashMap of stackInterfacelayer.
	
	public void partialActivate(ReplicationSet parent) {
		for(int i=0;i<this.requests.size();i++){
			((RfStackInterfaceImpl)RfResourceAdaptorImpl.getStackInterface()).addRequestToMap(this._handle , (RfRequest)this.requests.get(i));
        }
    }

    public boolean isReadyForReplication() {
        logger.debug("Entering ShSession isReadyForReplication()");
        return isReadyForReplication;
    }

    public void replicationCompleted() {
        logger.debug("Entering ShSession replicationCompleted()");
        Iterator itr = this.requests.iterator();
        while(itr.hasNext()) {
            RfRequest request = (RfRequest)itr.next();
			// TODO un-comment request.isAlreadyReplicated(true);
        }
        logger.debug("Leaving ShSession replicationCompleted()");
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		logger.debug("Entering ShSession writeExternal()");
		out.writeInt(this._handle);
		out.writeInt(this._shState);
		out.writeObject(this.requests);
		logger.debug("Leaving ShSession writeExternal()");
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        logger.debug("Entering ShSession readExternal()");
        this._handle = (int)in.readInt();
        this._shState = (int)in.readInt();
        this.requests = (ArrayList)in.readObject();
        logger.debug("Leaving ShSession readExternal()");
    }

}
