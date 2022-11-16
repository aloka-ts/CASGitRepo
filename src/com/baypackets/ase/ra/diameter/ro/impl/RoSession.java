package com.baypackets.ase.ra.diameter.ro.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseChainInfo;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.ra.diameter.ro.RoRequest;
import com.baypackets.ase.ra.diameter.ro.RoResourceFactory;
import com.baypackets.ase.ra.diameter.ro.stackif.RoStackInterfaceImpl;
import com.baypackets.ase.ra.diameter.ro.utils.Constants;
import com.baypackets.ase.ra.diameter.ro.utils.statistic.RoCCRStatsCollector;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.spi.resource.AbstractSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

import fr.marben.diameter.DiameterSession;

@DefaultSerializer(ExternalizableSerializer.class)
public class RoSession extends AbstractSession implements Constants {

	private static Logger logger = Logger.getLogger(RoSession.class);

	public static final short RO_PENDING = 0;	
	public static final short RO_ACTIVE = 1;	
	public static final short RO_INACTIVE = 2;	
	private DiameterSession clientStackSession;
	private DiameterSession serverStackSession;
	private int _handle = -1;
	private int _shState = RO_PENDING;
	private ArrayList requests = new ArrayList(1);
	private HashMap <Integer,ArrayList<Long>> timestamps;
	private boolean isReadyForReplication = true;

	private ResourceContext context;

	private String clientStackSessionId;
	private String serverStackSessionId;

	public RoSession() {
	}
	
	public RoSession(String id) {
		super(id);
	}
	
	/**
	 * @return the stackObj
	 */
	public DiameterSession getClientStackSession() {
		return clientStackSession;
	}

	/**
	 * @param stackObj the stackObj to set
	 */
	protected void setClientStackSession(DiameterSession clientStackSession) {
		this.clientStackSession = clientStackSession;
	}

	public DiameterSession getServerStackSession() {
		return serverStackSession;
	}

	public void setServerStackSession(DiameterSession serverStackSession) {
		this.serverStackSession = serverStackSession;
	}

	public synchronized int getNextHandle() {
		return ++this._handle;
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
		return (Message)RoResourceAdaptorImpl.getResourceContext().getMessageFactory().createRequest(this, type);
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
	public void addRequest(RoRequest request) {
		logger.debug("Inside addRequest of RoSession");
		if(request!= null && this.requests.indexOf(request)== -1) {
		logger.debug("adding the request to list " + request);
		this.requests.add(request);
		}
	}

	public void removeRequest(RoRequest request) {
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
		// Not Supported for RO Session 
		
		  logger.debug("Entering RoSession partialActivate()");
		  super.partialActivate(parent);
    }

    public boolean isReadyForReplication() {
        logger.debug("Entering RoSession isReadyForReplication()");
        return isReadyForReplication;
    }

    public void replicationCompleted() {
        logger.debug("Entering RoSession replicationCompleted()");
     // Not Supported for RO Session 
        logger.debug("Leaving RoSession replicationCompleted()");
	}

/*	public void writeExternal(ObjectOutput out) throws IOException {
		logger.debug("Entering RoSession writeExternal()");
		out.writeInt(this._handle);
		out.writeInt(this._shState);
		out.writeObject(this.requests);
		logger.debug("Leaving RoSession writeExternal()");
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        logger.debug("Entering RoSession readExternal()");
        this._handle = (int)in.readInt();
        this._shState = (int)in.readInt();
        this.requests = (ArrayList)in.readObject();
        logger.debug("Leaving RoSession readExternal()");
    }
*/
    
    public void invalidate () {
		if(logger.isDebugEnabled())
			logger.debug("Inside invalidate()  Ro resource session");
		if(clientStackSession!=null){
			if(logger.isDebugEnabled())
				logger.debug("Releasing Ro Stack Client session");
			clientStackSession.delete();//.release();
		}
		if(this.requests!=null && requests.size()!=0){
			for(Object obj:requests){
				if(RoResourceAdaptorImpl.getStackInterface()!=null)
					((RoStackInterfaceImpl)RoResourceAdaptorImpl.getStackInterface()).removeRequestFromMap(obj);
			}
		}
		if(serverStackSession!=null){
			if(logger.isDebugEnabled())
				logger.debug("Releasing Ro Stack Server session and remove mapping in RoResourceAdaptorImpl");
			RoResourceAdaptorImpl.removeRoSession(serverStackSession.getSessionId());			
			serverStackSession.delete();//release();
		}
		if(timestamps!=null){
			RoCCRStatsCollector.addTimeStampMap(timestamps);
		}
		super.invalidate();
		if(logger.isDebugEnabled())
			logger.debug("Exiting invalidate() Ro resource session");
    }
  	
    /**
  	 * Updates the specified attribute
           */
       @Override
          public void setAttribute(String name, Object attribute){
        	  super.setAttribute(name, attribute);
        	  
        	  if(logger.isDebugEnabled())
      			logger.debug("setAttribute name "+ name +" value "+ attribute);
  		// If state is invalid throw an exception
  		     attributes.put(name, attribute);
  	    }
     
       /**
   	 * Method for adding message time stamp
   	 */
	public void addTimestamp(int messageType, long timestamp) {
		if (timestamps == null) {
			timestamps = new HashMap<Integer, ArrayList<Long>>();
		}

		Integer key = new Integer(messageType);
		ArrayList<Long> sessionTimestamps = null;
		if (timestamps.containsKey(key)) {
			sessionTimestamps = (ArrayList<Long>)timestamps.get(key);
		} else {
			sessionTimestamps = new ArrayList<Long>();
			timestamps.put(key, sessionTimestamps);
		}
		sessionTimestamps.add(new Long(timestamp));
	}
	/**
	 * Method for getting message time stamp
	 */	
	public Long getTimestamp(int messageType, int msgIndex) {
		if (timestamps == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No timestamp added in this RoSession.");
			}
			return null;
		}
		
		ArrayList<Long> sessionTimestamps = (ArrayList<Long>)timestamps.get(new Integer(messageType));
		if (sessionTimestamps == null || sessionTimestamps.size() <= msgIndex) {
			if (logger.isDebugEnabled()) {
				logger.debug("No timestamp found for messageType " + messageType +
						" messageIndex " + msgIndex);
			}
			return null;
		}
		return sessionTimestamps.get(msgIndex);
	}

	public void setClientStackSessionId(String sessionId) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled()) {
	         logger.debug("Entering RoSession setClientStackSessionId() "+sessionId);
	     }
		clientStackSessionId=sessionId;
	}
	
	public String getClientStackSessionId() {
		if (logger.isDebugEnabled()) {
	         logger.debug("Entering RoSession getClientStackSessionId() "+clientStackSessionId);
	     }
		return clientStackSessionId;
	}


	
	public String getServerStackSessionId() {
		 if (logger.isDebugEnabled()) {
	         logger.debug("Entering RoSession getServerStackSessionId() "+serverStackSessionId);
	     }
		return serverStackSessionId;
	}

	public void setServerStackSessionId(String serverStackSessionId) {
		  if (logger.isDebugEnabled()) {
		         logger.debug("Entering RoSession setServerStackSessionId() "+serverStackSessionId);
		     }
		     
		this.serverStackSessionId = serverStackSessionId;
	}
	
	
	 public void writeExternal(ObjectOutput out) throws IOException {
	     if (logger.isDebugEnabled()) {
	         logger.debug("Entering RoSession writeExternal()");
	     }
	     
	     if(serverStackSessionId!=null){
	    	 out.writeUTF(serverStackSessionId);
	     }
        super.writeExternal(out);
        
		
        if (logger.isDebugEnabled()) {
            logger.debug("Leaving RoSession writeExternal()");
        }
    }


    public void readExternal(ObjectInput in)  throws IOException, ClassNotFoundException{
         if (logger.isDebugEnabled()) {
             logger.debug("Entering RoSession readExternal()");
         }

         this.serverStackSessionId= in.readUTF();
         
         if (logger.isDebugEnabled()) {
             logger.debug("Entering RoSession readExternal() "+ serverStackSessionId + "app session id is "+this.appSessionId);
         }
         
         RoResourceAdaptorImpl.addRoSession(this.serverStackSessionId, this);
         
        

         if (logger.isDebugEnabled()) {
             logger.debug("Appsession is "+ appSession);
         }
         
		super.readExternal(in);
		
		if (appSession == null) {
			AseHost host = (AseHost) Registry
					.lookup(com.baypackets.ase.util.Constants.NAME_HOST);
			appSession = (AseApplicationSession) host
					.getApplicationSession(this.appSessionId);
			if (logger.isDebugEnabled())
				logger.debug("setting appession " + appSession);
		} else {
			if (logger.isDebugEnabled())
				logger.debug(" appSession is" + appSession);
		}
        	
		if (logger.isDebugEnabled()) {
			logger.debug("Leaving RoSession readExternal()");
		}
    }

	
}

