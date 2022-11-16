package com.baypackets.ase.ra.diameter.gy.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;
import com.baypackets.ase.ra.diameter.gy.GyRequest;
import com.baypackets.ase.ra.diameter.gy.stackif.GyStackInterfaceImpl;
import com.baypackets.ase.ra.diameter.gy.utils.Constants;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.spi.resource.AbstractSession;
import com.baypackets.ase.spi.resource.ResourceContext;

public class GySession extends AbstractSession implements Constants {

	private static Logger logger = Logger.getLogger(GySession.class);

	public static final short RO_PENDING = 0;	
	public static final short RO_ACTIVE = 1;	
	public static final short RO_INACTIVE = 2;	
	private static GyResourceAdaptorImpl rfAdaptor;

	private int _handle = -1;
	private int _shState = RO_PENDING;
	private ArrayList requests = new ArrayList(1);
	private boolean isReadyForReplication = true;

	private ResourceContext context;
	
	public GySession() {
	}
	
	public GySession(String id) {
		super(id);
	}
	
	public int getHandle() {
		return this._handle;
	}

	public void setHandle(int handle) {
		this._handle = handle;
	}

	public int getRoState() {
		return this._shState;
	}

	public void setRoState(int state) {
		this._shState = state;
	}
	
	public String getProtocol() {
		return PROTOCOL;
	}

	public Message createMessage(int type) throws ResourceException {
		logger.debug("entering RoSession.createMessage()");
		//return (Message)this.context.getMessageFactory().createRequest(this, type);
		return (Message)GyResourceAdaptorImpl.getResourceContext().getMessageFactory().createRequest(this, type);
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
	public void addRequest(GyRequest request) {
		logger.debug("Inside addRequest of RoSession");
		if(request!= null && this.requests.indexOf(request)== -1) {
		logger.debug("adding the request to list " + request);
		this.requests.add(request);
		}
	}

	public void removeRequest(GyRequest request) {
		logger.debug("Inside removeRequest of RoSession");
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
			((GyStackInterfaceImpl)GyResourceAdaptorImpl.getStackInterface()).addRequestToMap(this._handle , (GyRequest)this.requests.get(i));
        }
    }

    public boolean isReadyForReplication() {
        logger.debug("Entering GySession isReadyForReplication()");
        return isReadyForReplication;
    }

    public void replicationCompleted() {
        logger.debug("Entering GySession replicationCompleted()");
        Iterator itr = this.requests.iterator();
        while(itr.hasNext()) {
            GyRequest request = (GyRequest)itr.next();
			// TODO un-comment request.isAlreadyReplicated(true);
        }
        logger.debug("Leaving GySession replicationCompleted()");
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		logger.debug("Entering GySession writeExternal()");
		out.writeInt(this._handle);
		out.writeInt(this._shState);
		out.writeObject(this.requests);
		logger.debug("Leaving GySession writeExternal()");
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        logger.debug("Entering GySession readExternal()");
        this._handle = (int)in.readInt();
        this._shState = (int)in.readInt();
        this.requests = (ArrayList)in.readObject();
        logger.debug("Leaving GySession readExternal()");
    }

}
