package com.baypackets.ase.spi.container;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.util.*;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.io.KryoObjectInput;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.Registry;
//import com.baypackets.ase.container.AseApplicationSession;
//import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.spi.replication.Replicable;
import com.baypackets.ase.spi.replication.ReplicableMap;
import com.baypackets.ase.spi.replication.Replicables;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.replication.ReplicationListener;
import com.baypackets.ase.spi.replication.ReplicationSet;

@DefaultSerializer(ExternalizableSerializer.class)
public abstract class AbstractProtocolSession 	
	implements SasProtocolSession, Replicable, ReplicationSet, Cloneable {
	
	private static Logger logger = Logger.getLogger(AbstractProtocolSession.class);
	private static final String ATTRIBUTE_MAP = "SESSION_ATTRIBUTE_MAP".intern();
	 private static final long serialVersionUID = -318979579424184L;
	private String id;
	private String appId;
	private ClassLoader classLoader;
	private long creationTime;
	private long lastAccessedTime;
	private String handler = null;
	protected int index = 0;
	
	protected int state;
	protected Map attributes;
	protected Replicables replicables = new Replicables();
	
	protected SasApplicationSession appSession = null;
	protected String appSessionId = null;
        private boolean   m_isReadyForReplication = true;
        private boolean   m_isModified = false;
        private boolean   m_isNew = true;
	private boolean mFirstReplicationCompleted=false;
    
    public AbstractProtocolSession() {
    	//this(UIDGenerator.getInstance().get128BitUuid());
		this(TimeBasedUUIDGenerator.getUUID());
    }

	/**
	  * Constructor for Protocol Session. 
	  * @param id - Unique identifier for this session
	  */
	public AbstractProtocolSession(String id) {
		 this.id = id;
		 this.state = VALID;
		 this.creationTime = System.currentTimeMillis();
		 this.lastAccessedTime = System.currentTimeMillis();
		 this.attributes = new ReplicableMap(ATTRIBUTE_MAP, true);
		 this.setReplicable((Replicable)this.attributes);
		 this.replicables.setReplicableId("PROTOCOL_SESSION_REPLICABLES");
	}
	
	public Object clone() throws CloneNotSupportedException {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering AbstractProtocolSession clone()");
        }

          AbstractProtocolSession clonedSession = (AbstractProtocolSession)(super.clone());
		  clonedSession.handler = new String(this.handler);
		  clonedSession.attributes = new ReplicableMap(ATTRIBUTE_MAP, true);
		  /*
		   * Each cloned SipSession should have its own copy of Replicables object.
		   * Otherwise clonedSession and Base Session will keep referring to the same
		   * object and replicables of each of them will mix up. 
		   */
		  clonedSession.replicables = new Replicables();
		  clonedSession.setReplicable((Replicable)clonedSession.attributes);
		  clonedSession.m_isNew = true;
		  clonedSession.mFirstReplicationCompleted = false;

		   Iterator it = attributes.entrySet().iterator();
         for(;it.hasNext();){
            Map.Entry entry = (Map.Entry)it.next();
            clonedSession.setAttribute((String)entry.getKey(), entry.getValue());
         } 
          
        if (logger.isDebugEnabled()) {
            logger.debug("Leaving AbstractProtocolSession clone()");
        }
	 
		  return clonedSession;
	 }
	 
    public String getReplicableId() {
        return this.id;
    }
  
    public void setReplicableId(String id){
		this.id = id;
    }

    public boolean isReadyForReplication() {
        return m_isReadyForReplication;
    }

    public void setReadyForReplication(boolean ready) {
        m_isReadyForReplication = ready;
    }


	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		this.state = in.readInt();
		this.lastAccessedTime = in.readLong();
		this.handler =(String) in.readObject();
		
		if (in instanceof AseObjectInputStream) {
			((AseObjectInputStream)in).setClassLoader(this.classLoader);
	
			this.replicables.readIncremental(in);
		
			((AseObjectInputStream)in).setClassLoader(null);
		} else {
			this.replicables.readIncremental(in);
		}
	}

	public void writeIncremental(ObjectOutput out, int replicationType) throws IOException {
		out.writeInt(this.state);
		out.writeLong(this.lastAccessedTime);
		out.writeObject(handler);
        this.replicables.writeIncremental(out, replicationType);
	}

	public boolean isModified(){
		return this.m_isModified || this.replicables.isModified();	
	}
	
	public boolean isNew(){
		return this.m_isNew;
	}

   public void setModified(boolean reqd){
		this.m_isModified = reqd;	
	}
 
   public void replicationCompleted(){
		if (logger.isDebugEnabled())
			logger.debug(getId()+"::AbstractProtocolSession: replicationCompleted(): Setting m_isNew = false");
		replicationCompleted(false);
	}
   
	public void replicationCompleted(boolean noReplication) {
		if (logger.isDebugEnabled())
			logger.debug(getId()
					+ "::AbstractProtocolSession: replicationCompleted(): Setting m_isNew = false NoRep is::"+noReplication);
		if (!noReplication) {
			this.m_isModified = false;
			this.m_isNew = false;
		}
		this.replicables.replicationCompleted(noReplication);
	}

    public void activate(ReplicationSet parent) {
		if(logger.isDebugEnabled())
		logger.debug("Entering AbstractProtocolSession activate()");
		//marking valid so counter is decremnted on invalidate
		this.state=VALID;
		
        if (null != parent) {
        	//Activate the Child objects...
        	this.replicables.activate(this);

        } else {
			logger.error("activate() failed as ReplicationSet is NULL");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Leaving AbstractProtocolSession activate()");
        }
    }

	public void partialActivate(ReplicationSet parent) {
		// parent is IC
		if(logger.isDebugEnabled())
			logger.debug("Entering AbstractProtocolSession partialActivate()");
        if (null != parent) {
            appSession = (SasApplicationSession)parent.getReplicable(this.appSessionId);
            appSession.addProtocolSession(this);
        } else {
			logger.error("partialActivate() failed as ReplicationSet is NULL");
        }
	}
     public void writeExternal(ObjectOutput out) throws IOException {
		 if(logger.isDebugEnabled()) {
			 logger.debug("Entering AbstractProtocolSession writeExternal()");
		 }

		 if (logger.isDebugEnabled()){
			 logger.debug("setFirstReplicationCompleted(true); ");
		 }
		 this.setFirstReplicationCompleted(true);
 	
		out.writeObject(this.appId);
 		out.writeObject(this.appSessionId);
 		out.writeObject(this.id);
		out.writeLong(this.creationTime);

		//******************** Replication*******************

		out.writeInt(this.state);
		out.writeLong(this.lastAccessedTime);
		out.writeObject(handler);

		out.writeObject(this.replicables);
		if(logger.isDebugEnabled())
		logger.debug("Leaving AbstractProtocolSession writeExternal()");
    }

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if(logger.isDebugEnabled())
		logger.debug("Entering AbstractProtocolSession readExternal()");

		this.appId = (String)in.readObject();
		this.appSessionId = (String)(in.readObject());
		this.id = (String)(in.readObject());
		this.creationTime = in.readLong();
        
		if(logger.isDebugEnabled())
			logger.debug("Entering AbstractProtocolSession readExternal() id "+id +" appid "+appId +" appSessionId "+appSessionId);
		
		// Get the class loader of the app that this session is associated with.
		if (this.classLoader == null) {
			AseContainer host = (AseContainer)Registry.lookup(Constants.NAME_HOST);
			SasApplication app = (SasApplication)host.findChild(this.appId);
			this.classLoader = app.getClassLoader(); 
		}

		//*************Replication modification***************

		this.state = in.readInt();
		this.lastAccessedTime = in.readLong();
		this.handler =(String) in.readObject();

		if (in instanceof AseObjectInputStream) {
			((AseObjectInputStream)in).setClassLoader(this.classLoader);
			this.replicables = (Replicables)in.readObject();
			((AseObjectInputStream)in).setClassLoader(null);
		} else if(in instanceof KryoObjectInput) {
			ClassLoader previousCl = ((KryoObjectInput)in).getKryoObject().getClassLoader();
			((KryoObjectInput)in).getKryoObject().setClassLoader(this.classLoader);
			this.replicables = (Replicables)in.readObject();
			((KryoObjectInput)in).getKryoObject().setClassLoader(previousCl);
		} else{
			this.replicables = (Replicables)in.readObject();
		}

		//*****************************************************
	
		//Restore the Attributes MAP and the Chain Info
		this.attributes = (ReplicableMap) this.getReplicable(ATTRIBUTE_MAP);
        	
		if(logger.isDebugEnabled()) {
			logger.debug("Protocol Session Replicables :::" + this.replicables);
			logger.debug("Protocol Session attribute map :::" + this.attributes); 
		}
        if(logger.isDebugEnabled())	
		logger.debug("Leaving AbstractProtocolSession readExternal()");
	}

	/**
	 * gets the replication listener and calls handleReplicationEvent on it.
	 * @param event - ReplicationEvent to be send.
	 */
	public void sendReplicationEvent(ReplicationEvent event) {
		if( this.getReplicationListener()  != null ){
			ArrayList list = new ArrayList(1);
			list.add(((SasApplicationSession)this.getApplicationSession()).getApplication().getId());
			event.setAppNames(list);
			this.getReplicationListener().handleReplicationEvent(event);
		}
	}

	/**
	 * Returns the Application Session associated with this protocol Session
	 * @return
	 */
	public SipApplicationSession getApplicationSession() {
		return appSession;
	}

	/**
	 * Sets the application session for this protocol session.
	 * This method will be called from the AseHost while associating the appsession
	 * @param session
	 */
	public void setApplicationSession(SipApplicationSession session, int index) {
		this.appSession = (SasApplicationSession)session;
		this.appSessionId = ((SasApplicationSession)session).getAppSessionId();
		this.appId = this.appSession.getApplication().getId();
		this.index = index;
		this.setModified(true);
	}

	public ReplicationListener getReplicationListener() {
		ReplicationListener listener = this.appSession != null ? this.appSession.getReplicationListener() : null;
		return listener;
	}

	/**
	 * Returns the name of the handler for the requests on this session
	 * @return
	 */
	public String getHandler() {
		return handler;
	}
	
	/**
	 * Sets the name of the handler for this session
	 * @param string
	 */
	public void setHandler(String string) throws javax.servlet.ServletException {
		handler = string;
		this.setModified(true);
	}
	
	/**
	 * Returns the attribute object
	 * @param name - name of the attribute
	 * @return Attribute object specified by this name.
	 */
	public Object getAttribute(String name) {
		if (INVALID == state) {
  			throw new java.lang.IllegalStateException("Invalid State");
		}
	 	
		//Get the attribute from the map
   		Object value = null;
		synchronized (this.attributes) {
			value = this.attributes.get(name);
        }

   		return value;
	  	
	}
	
	
	public Iterator getAttributeNamesIterator(){
		return this.attributes.keySet().iterator();
	}
	
	/**
	 * Returns the enumeration of attribute names
	 * @return
	 */
	final public Enumeration getAttributeNames (){
		if (INVALID == state) {
  			throw new java.lang.IllegalStateException("Invalid State");
		}

		Enumeration enumr = null;
		synchronized (this.attributes) {
			enumr = new Enumerator(this.attributes.keySet().iterator());
		}
		return enumr;
	}
	
	/**
	 * Returns the creation time for this session
	 * @return
	 */
	final public long getCreationTime(){
		return this.creationTime;
	}
	
	/**
	 * Returns the ID for this session
	 * @return
	 */
	final public String getId(){
		return this.id;
	}

	 /**
	  * Sets the id for this session
	  */
	 final public void setId(String pId) {
		  this.id = pId;
		this.setModified(true);
	 }
	 
	/**
	 * Returns the last accessed time for the session
	 * @return
	 */
	final public long getLastAccessedTime(){
		return this.lastAccessedTime;
	}
	
	/**
	 * Sub classes call this method to update the last update time.
	 * This method also updates the associated application sessions last accessed time.
	 */
	final protected void accessed (){
		this.lastAccessedTime = System.currentTimeMillis();
		if(appSession != null){
			appSession.setLastAccessedTime(this.lastAccessedTime);
		}
	}
	
	/**
	 * Returns the state of this session
	 * @return
	 */
	public final int getProtocolSessionState() {
		return state;
	}

	/**
	 * Sets the state for this session.
	 * @param i
	 */
	public final void setProtocolSessionState(int i) {
		state = i;
		this.setModified(true);
	}
	
	/**
	 * Base class implementation of the cleanup. 
	 * Sub-classes need to over-ride this method for complete cleanup. 
	 */
	public void cleanup(){
		this.state = DESTROYED;
	}
	
	/**
	 * Handles the incoming message. This method will be called from the AseHost 
	 * for handling the requests. If the protocol specific sessions want
	 * to do something specific, needs to over-ride this method and call super.handleRequest();
	 * @param request
	 * @param response
	 * @throws AseInvocationFailedException
	 */
	public void handleMessage(SasMessage message) 
					throws AseInvocationFailedException, ServletException {
		
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("HandleRequest called on protocol session");
		}

		//Check whether the app session is already associated or not.
		if(this.appSession == null){
			throw new AseInvocationFailedException("Unable to get the application session");
		}
		
		//Pass the request to the AppContext
		SasMessageProcessor ctx = (SasMessageProcessor)this.appSession.getApplication();
		ctx.processMessage(message);
	}
		
	/**
	 * Invalidates this session.
	 */
	public void invalidate (){
		this.state = INVALID;
	}
	
 	/**
	 * Removes the specified attribute
    */
	public void removeAttribute(String name){
		// If state is INVALID throw an exception
		if (INVALID == state) {
			  logger.error("removeAttribute. Throwing Exception. " +
							   "Session state = INVALID " + id);
			  throw new java.lang.IllegalStateException("Invalid Session");
		}
	
		// Now do all the work.
		synchronized (attributes) {
			// Remove the attribute
			attributes.remove(name);
		}
	}

        
        /**
	 * Updates the specified attribute
         */
        public void setAttribute(String name, Object attribute){
		// If state is invalid throw an exception
		if (INVALID == state) {
			  logger.error("setAttribute. Throwing Exception. Session " +
							   "state = INVALID :" + this.id);
			  throw new java.lang.IllegalStateException("Invalid Session");
		}
		  
        if (logger.isDebugEnabled()) {
            logger.debug("setAttr(): Setting an attribute on the session with name: " + name);
        }
                
		//Check whether the attribute is Serializable, 
		//if not check whether the app is distributable
		//if it is distributable, then throw exception
        if(!(attribute instanceof Serializable)) {
		  if (this.appSession != null && this.appSession.getApplication().isDistributable()){
			  logger.error("setAttribute. Distributable App. But Attribute not serializable, throwing exception: ");
			  throw new java.lang.IllegalStateException("This is a distributable application. So the attribute should be Serializable");
		  }                  
		}
	}
        	
	/**
	 * Returns the protocol used by this session.
	 * @return
	 */
	public abstract String getProtocol();
        
	public static class SessionAttributeHolder implements Externalizable{
	  public  String id;
		
      public SessionAttributeHolder(){
      }

	   public void readExternal(ObjectInput in)
		   throws IOException, ClassNotFoundException {
		   this.id = (String)in.readObject();
	   }

	   public void writeExternal(ObjectOutput out) throws IOException {
         if (logger.isDebugEnabled()) {
             logger.debug("Inside SessionAttributeHolder writeExternal()");
         }
		   out.writeObject(this.id);
	   }
	}

	public SasApplication getApplication(){
		SasApplication ctx = this.appSession != null ? this.appSession.getApplication() : null;
		return ctx;
	}
	
	////////////ReplicableCollection Interface Implementation starts //////////////
	public void clear() {
		this.replicables.clear();
	}

	public Collection getAllReplicables() {
		return this.replicables.getAllReplicables();
	}

	public Replicable getReplicable(String id) {
		return this.replicables.getReplicable(id);
	}

	public void removeReplicable(String id) {
		this.replicables.removeReplicable(id);
	}

	public void setReplicable(Replicable replicable) {
		this.replicables.setReplicable(replicable);
	}
	////////////ReplicableCollection Interface Implementation ends //////////////

	protected SasProtocolSession getProtocolSession(String id){
		SasProtocolSession protoSession = null;
		Iterator it = id != null && this.appSession != null ? this.appSession.getSessions() : null;
		for(;it!=null && it.hasNext();){
			SasProtocolSession temp = (SasProtocolSession) it.next();
			if(temp.getId().equals(id)){
				protoSession = temp;
				break;
			}
		}
		return protoSession;
	}
	
	@Override
	public boolean isFirstReplicationCompleted() {
		return mFirstReplicationCompleted;
	}

	@Override
	public void setFirstReplicationCompleted(boolean isFirstReplicationCompleted) {
		mFirstReplicationCompleted = isFirstReplicationCompleted;
	}

}
