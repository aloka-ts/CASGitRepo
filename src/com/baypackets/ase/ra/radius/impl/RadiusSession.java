package com.baypackets.ase.ra.radius.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.baypackets.ase.ra.radius.RadiusRequest;
import com.baypackets.ase.ra.radius.utils.Constants;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.spi.resource.AbstractSession;
import com.baypackets.ase.spi.resource.ResourceContext;

public class RadiusSession extends AbstractSession implements Constants {

	private static Logger logger = Logger.getLogger(RadiusSession.class);

	public static final short RADIUS_PENDING = 0;	
	public static final short RADIUS_ACTIVE = 1;	
	public static final short RADIUS_INACTIVE = 2;	
	
	private int _handle = -1;
	private int _radiusState = RADIUS_PENDING;
	@SuppressWarnings("unchecked")
	private ArrayList requests = new ArrayList(1);
	private boolean isReadyForReplication = true;

	private ResourceContext context;
	
	public RadiusSession() {
	}
	
	public RadiusSession(String id) {
		super(id);
	}
	
	public int getHandle() {
		return this._handle;
	}

	public void setHandle(int handle) {
		this._handle = handle;
	}

	public int getRadiusState() {
		return this._radiusState;
	}

	public void setRadiusState(int state) {
		this._radiusState = state;
	}
	
	public String getProtocol() {
		return PROTOCOL;
	}

	public Message createMessage(int type) throws ResourceException {
		return null;
	}

	/**
	 * This Method returns the Resource Context associated with the session
	 *
	 * @return <code>ResourceContext</code> object associated 
	 */
	
	public ResourceContext getResourceContext() {
		if (logger.isDebugEnabled()) {
			logger.debug("getResourceContext() called.");
		}
		return this.context;
	}
	
	/**
	 * This Method associates the resource context with this session
	 *
	 * @param context - resource context to be associated with this session
	 */	
	
	public void setResourceContext(ResourceContext context) 
	{
		if (logger.isDebugEnabled()) {
			logger.debug("setResourceContext() called.");
		}
		this.context = context;
	}
	@SuppressWarnings("unchecked")
	public void addRequest(RadiusRequest request) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside addRequest of RadiusSession");
		}
		if(request!= null && this.requests.indexOf(request)== -1) {
			if (logger.isDebugEnabled()) {
				logger.debug("adding the request to list " + request);
			}
			this.requests.add(request);
		}
	}

	public void removeRequest(RadiusRequest request) {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside removeRequest of RadiusSession");
		}
		if (request != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Removing the request from list " + request);
			}
			this.requests.remove(request);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Request is null");
			}
		}
	}

	///////////// Replicable Interface method ////////////////////

	// This method adds the request attached with this session into
	// the hashMap of stackInterfacelayer.
	
	public void partialActivate(ReplicationSet parent) {
	//Client mode is synchronous  so not required
//		for(int i=0;i<this.requests.size();i++){
//			((RadiusStackClientInterfaceImpl)RadiusResourceAdaptorImpl.getStackClientInterface()).addRequestToMap(this._handle , (RadiusRequest)this.requests.get(i));
//        }
		
    }

    public boolean isReadyForReplication() {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Entering Radius isReadyForReplication()");
    	}
        return isReadyForReplication;
    }

    public void replicationCompleted() {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Entering RoSession replicationCompleted()");
    	}
        if (logger.isDebugEnabled()) {
        	logger.debug("Leaving RoSession replicationCompleted()");
        }
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering RoSession writeExternal()");
		}
		out.writeInt(this._handle);
		out.writeInt(this._radiusState);
		out.writeObject(this.requests);
		if (logger.isDebugEnabled()) {
			logger.debug("Leaving RoSession writeExternal()");
		}
    }

    @SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    	if (logger.isDebugEnabled()) {
    		logger.debug("Entering RadiusSession readExternal()");
    	}
        this._handle = (int)in.readInt();
        this._radiusState = (int)in.readInt();
        this.requests = (ArrayList)in.readObject();
        if (logger.isDebugEnabled()) {
        	logger.debug("Leaving RadiusSession readExternal()");
        }
    }

}
