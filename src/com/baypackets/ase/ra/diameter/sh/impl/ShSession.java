package com.baypackets.ase.ra.diameter.sh.impl;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.ra.diameter.sh.ShRequest;
import com.baypackets.ase.ra.diameter.sh.stackif.ShStackServerInterfaceImpl;
import com.baypackets.ase.ra.diameter.sh.utils.Constants;
import com.baypackets.ase.ra.diameter.sh.utils.statistic.ShUDRStatsCollector;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.spi.resource.AbstractSession;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import fr.marben.diameter.DiameterSession;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;

@DefaultSerializer(ExternalizableSerializer.class)
public class ShSession extends AbstractSession implements Constants {

	private static final Logger logger = Logger.getLogger(ShSession.class);

	public static final short SH_PENDING = 0;
	public static final short SH_ACTIVE = 1;
	public static final short SH_INACTIVE = 2;
	private DiameterSession clientStackSession;
	private DiameterSession serverStackSession;
	private int _handle = -1;
	private int _shState = SH_PENDING;
	private final ArrayList requests = new ArrayList(1);
	private HashMap <Integer,ArrayList<Long>> timestamps;
	private final boolean isReadyForReplication = true;

	private ResourceContext context;

	private String clientStackSessionId;
	private String serverStackSessionId;

	public ShSession() {
	}

	public ShSession(String id) {
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
		return (Message)ShResourceAdaptorImpl.getResourceContext().getMessageFactory().createRequest(this, type);
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
	public void addRequest(ShRequest request) {
		logger.debug("Inside addRequest of ShSession");
		if(request!= null && this.requests.indexOf(request)== -1) {
			logger.debug("adding the request to list " + request);
			this.requests.add(request);
		}
	}

	public void removeRequest(ShRequest request) {
		logger.debug("Inside removeRequest of ShSession");
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

		logger.debug("Entering ShSession partialActivate()");
		super.partialActivate(parent);
	}

	public boolean isReadyForReplication() {
		logger.debug("Entering ShSession isReadyForReplication()");
		return isReadyForReplication;
	}

	public void replicationCompleted() {
		logger.debug("Entering ShSession replicationCompleted()");
		// Not Supported for RO Session
		logger.debug("Leaving ShSession replicationCompleted()");
	}

/*	public void writeExternal(ObjectOutput out) throws IOException {
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
				if(ShResourceAdaptorImpl.getStackInterface()!=null)
					((ShStackServerInterfaceImpl)ShResourceAdaptorImpl.getStackInterface()).removeRequestFromMap(obj);
			}
		}
		if(serverStackSession!=null){
			if(logger.isDebugEnabled())
				logger.debug("Releasing Ro Stack Server session and remove mapping in ShResourceAdaptorImpl");
			ShResourceAdaptorImpl.removeShSession(serverStackSession.getSessionId());
			serverStackSession.delete();//release();
		}
		if(timestamps!=null){
			ShUDRStatsCollector.addTimeStampMap(timestamps);
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
			sessionTimestamps = timestamps.get(key);
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
				logger.debug("No timestamp added in this ShSession.");
			}
			return null;
		}

		ArrayList<Long> sessionTimestamps = timestamps.get(new Integer(messageType));
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
			logger.debug("Entering ShSession setClientStackSessionId() "+sessionId);
		}
		clientStackSessionId=sessionId;
	}

	public String getClientStackSessionId() {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering ShSession getClientStackSessionId() "+clientStackSessionId);
		}
		return clientStackSessionId;
	}



	public String getServerStackSessionId() {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering ShSession getServerStackSessionId() "+serverStackSessionId);
		}
		return serverStackSessionId;
	}

	public void setServerStackSessionId(String serverStackSessionId) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering ShSession setServerStackSessionId() "+serverStackSessionId);
		}

		this.serverStackSessionId = serverStackSessionId;
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering ShSession writeExternal()");
		}

		if(serverStackSessionId!=null){
			out.writeUTF(serverStackSessionId);
		}
		super.writeExternal(out);


		if (logger.isDebugEnabled()) {
			logger.debug("Leaving ShSession writeExternal()");
		}
	}


	public void readExternal(ObjectInput in)  throws IOException, ClassNotFoundException{
		if (logger.isDebugEnabled()) {
			logger.debug("Entering ShSession readExternal()");
		}

		this.serverStackSessionId= in.readUTF();

		if (logger.isDebugEnabled()) {
			logger.debug("Entering ShSession readExternal() "+ serverStackSessionId + "app session id is "+this.appSessionId);
		}

		ShResourceAdaptorImpl.addShSession(this.serverStackSessionId, this);



		if (logger.isDebugEnabled()) {
			logger.debug("Appsession is "+ appSession);
		}

		super.readExternal(in);

		if (appSession == null) {
			AseHost host = (AseHost) Registry
					.lookup(com.baypackets.ase.util.Constants.NAME_HOST);
			appSession = host
					.getApplicationSession(this.appSessionId);
			if (logger.isDebugEnabled())
				logger.debug("setting appession " + appSession);
		} else {
			if (logger.isDebugEnabled())
				logger.debug(" appSession is" + appSession);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Leaving ShSession readExternal()");
		}
	}


}

