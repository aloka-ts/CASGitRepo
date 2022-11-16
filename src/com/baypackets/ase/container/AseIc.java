/*
 * Created on Aug 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.container;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;

import java.util.concurrent.TimeUnit;
//import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import com.baypackets.ase.util.TimeBasedUUIDGenerator;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

//import EDU.oswego.cs.dl.util.concurrent.ReentrantLock;


import com.baypackets.ase.channel.AppInfo;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.AseLockException;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.container.sip.AseSipApplicationChain;
import com.baypackets.ase.dispatcher.Destination;
import com.baypackets.ase.dispatcher.Dispatcher;
import com.baypackets.ase.dispatcher.DispatcherImpl;
import com.baypackets.ase.replication.ReplicationInfo;
import com.baypackets.ase.spi.replication.ReplicationContextImpl;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.util.UIDGenerator;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.sipconnector.AseSipSession;
import com.baypackets.ase.sipconnector.AseSipSessionState;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class AseIc extends ReplicationContextImpl {
	
   private static Logger logger = Logger.getLogger(AseIc.class);
   private static final long serialVersionUID= -33720368547708482L;
   private HashMap appSessions = new HashMap();
   transient private Destination lastDestination = null;
   transient private Collection appInfo = new ArrayList();
   
   transient private int workQueue= -1;
   transient private int noOfValidAppSessions = 0;
   transient private ReentrantLock icLock = new ReentrantLock();
   
   transient private AseHost host; 
   
   // Application composition chain list
   transient private int appChainCounter = 0;
   transient private HashMap<Integer, AseSipApplicationChain> appChains = new HashMap<Integer, AseSipApplicationChain>();
   transient private boolean appSessionInvalidate =false;

   
   public AseIc(){
	   //this(UIDGenerator.getInstance().get128BitUuid());
	   this(TimeBasedUUIDGenerator.getUUID());
   }

	public AseIc(String id) {
		super(id);
        this.host = (AseHost) Registry.lookup(Constants.NAME_HOST);
    }
   
   public void acquire() throws AseLockException {
      try {
        boolean acquired= icLock.tryLock(100L, TimeUnit.MILLISECONDS);//lockInterruptibly();
         
         if (logger.isInfoEnabled()) {
				logger.info("lock acquired on IC:" + acquired);
			}
      }
      catch (InterruptedException e) {
         throw new AseLockException(e.toString());
      }
   }
   
   public boolean isAcquired(){
	   return icLock.isLocked();
   }

   public boolean isAcquiredByCurrentThread(){
	   return icLock.isHeldByCurrentThread();
   }
   
   public void release() throws AseLockException {
	   try {    	  
	    	  if(icLock.isHeldByCurrentThread()){
	             icLock.unlock();
	    	  }else{
	    		  if (logger.isInfoEnabled()) {
	  				logger.info("lock not held by current thread no need to unlock:");
	  			}
	    	  }
	      }
      catch (Error e) {
         throw new AseLockException(e.toString());
      }
   }
	
	public int getNoOfValidAppSessions() {
		return noOfValidAppSessions;
	}

	public int getWorkQueue() {
		return workQueue;
	}

	public void setWorkQueue(int i) {
		workQueue = i;
	}
	
	//@saneja 13769, identifed deadlock as activate is called form icLock on messages received form n/w; 
	//on the other hand after ft activate is called without icLock and takes lock form insides
	// removed synchronized  and added lock on ic here also to avoid possibilities of deadlock	
	public void addApplicationSession(AseApplicationSession appSession) {

		try {
			icLock.lockInterruptibly();
		} catch (InterruptedException e) {
			logger.error(e);
		}
		try {
			if (this.appSessions.containsValue(appSession) == false) {

				if (this.appSessions.put(appSession.getId(), appSession) == null) {
				// Increment only first time
				this.noOfValidAppSessions++;
			}
			appSession.setIc(this);
			
			AseContext ctx = appSession.getContext();
                        
            if (ctx != null) {
					if (!ctx.isDistributable()) {
						if (logger.isInfoEnabled()) {
							logger.info("Appliaction "
									+ ctx.getName()
									+ "is not distributable, "
									+ "So turning OFF the distributable flag in this IC object :"
									+ this.getId());
                	}
                	this.setDistributable(false);
                } else {

                    AppInfo info = new AppInfo();
                    info.setApplicationId(ctx.getId());
						info.setHostName(((AseHost) ctx.getParent()).getName());
                    this.appInfo.add(info);
                }
            }                        

                    AppInfo info = new AppInfo();
            this.setReplicable(appSession);
        }
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("activate completed on IC:" + this);
			}
			try {
				icLock.unlock();
			} catch (Error e) {
				logger.error(e);
			}
		}
	}
	
	//@saneja 13769, identifed deadlock as activate is called form icLock on messages received form n/w; 
	//on the other hand after ft activate is called without icLock and takes lock form insides
	// removed synchronized and added lock on ic here also to avoid possibilities of deadlock	
	public void protocolSessionInvalidated(AseProtocolSession session) {
		if (session == null)
			return;

		try {
			icLock.lockInterruptibly();
		} catch (InterruptedException e) {
			logger.error(e);
		}
		try {

			int cid = session.getChainInfo().getChainId();
			AseSipApplicationChain chain = appChains.get(cid);
			if (chain != null) {
			boolean cleanedup = chain.cleanup();
				if (cleanedup)
				appChains.remove(chain.getId());
			} else {
			session.cleanup();
		}
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("activate completed on IC:" + this);
			}
			try {
				icLock.unlock();
			} catch (Error e) {
				logger.error(e);
			}
		}
	}

	//@saneja 13769, identifed deadlock as activate is called form icLock on messages received form n/w; 
	//on the other hand after ft activate is called without icLock and takes lock form insides
	// removed synchronized and added lock on ic here also to avoid possibilities of deadlock	
	public void appSessionInvalidated(AseApplicationSession appSession) {

		try {
			icLock.lockInterruptibly();
		} catch (InterruptedException e) {
			logger.error(e);
		}
		try {
			appSessionInvalidate=true;
		this.noOfValidAppSessions--;
		
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("appSession Invalidated called :"
						+ noOfValidAppSessions);
      }

	  // Remove this app-session from Replicable's list
	  this.removeReplicable(appSession.getReplicableId());

			if (this.noOfValidAppSessions == 0) {
			this.cleanup();
		}
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("appSessionInvalidated completed on IC:" + this);
			}
			try {
				icLock.unlock();
			} catch (Error e) {
				logger.error(e);
			}
		}
	}
	
	public void cleanup() {
		if (logger.isEnabledFor(Level.INFO)) {
	    	logger.info("Cleanup called on Invocation Context");
	    }

		super.cleanup();
        
		// Call cleanup on all appSessions.
		
		if (logger.isDebugEnabled()) {
			logger.debug("Num of app-sessions to cleanup is: "
					+ this.appSessions.size());
		}
		Iterator iter = this.appSessions.values().iterator();
		while (iter.hasNext()) {
			AseApplicationSession appSession = (AseApplicationSession) iter
					.next();
			appSession.cleanup();
		}

		this.appSessions.clear();
		
		// Removes this IC from the host.
		if (null == this.host.removeIc(this.getId())) {
			logger.error("Ctxt-id for IC not found:");
		}
		
	}
	
	public void setLastDestination(Destination destination) {
		lastDestination = destination;
	}
   
	public boolean checkLoopback(AseBaseRequest request)
	{
   		
   		if(logger.isInfoEnabled())
		{
   			logger.info("checkLoopback called for request...");
   		}
   		
   		boolean loopbackRequired = false;
		Destination destination;
   		
   		//Host is NULL, we cannot do any rule matching, so returning false.
   		if(this.host == null)
		{
   			return loopbackRequired;
   		}

		/** BpInd16651,handling encoded uri --author: Kameswara Rao */
   	
		try
		{
			AseApplicationSession appSession = host.decodeAppSession(request);
			if(appSession!=null)
			{
				if(logger.isInfoEnabled())
				{
					logger.info("AseIc:Got the appSession using the encoded URI. So going to route this request to specified app.");
				}
				//JSR289.36
				destination = (Destination)request.getDestination();
				if ( null == destination ) {
					destination = new Destination();
				}
				if (request instanceof AseSipServletRequest)
					((AseSipServletRequest)request).setTargeted();
				Dispatcher dispatcher = new DispatcherImpl();
				destination.setAppName(appSession.getApplicationName());
				if(logger.isInfoEnabled())
					logger.info("Request targeted for Application Name: "+ destination.getApplicationName());
				destination = dispatcher.getDestination(request, destination, this.host);
				AseContext ctx = appSession.getContext();
				if(ctx != null) {
					if(null == destination || destination.getApplicationName() != ctx.getId()) {
						throw new AseInvocationFailedException("Application Router could not find the application name");
					}
				} else {
					throw new AseInvocationFailedException("Not able to find the application context");
				}

				loopbackRequired = true;
				// Bpind 17365 this.setLastDestination(destination);
					request.setDestination(destination);

			}
			else
			{       /** end of BpInd16651 */
				//BpInd 17365
				destination = host.doRuleMatching(request,(Destination)request.getDestination() );
                if(destination != null && destination.getStatus() != Dispatcher.EXTERNAL_ROUTE)
				{
					if(logger.isInfoEnabled())
					{
						logger.info("Received a DESTINATION_FOUND. Will return TRUE for LOOPBACK");
					}
					loopbackRequired = true;
				//BpInd 17365 	this.setLastDestination(destination);
					request.setDestination(destination);
				}
				else
				{
					if(logger.isInfoEnabled())
					{
                        logger.info("Received a PROCESSING_OVER/EXTERNAL_ROUTE from Dispatcher. Will return FALSE for LOOPBACK");
					}
				}
  	 		
			}
			if (request instanceof AseSipServletRequest){
				host.checkForTcapRequest((AseSipServletRequest)request);
			}
		}
		catch(AseInvocationFailedException e)
		{
			if(logger.isDebugEnabled()) 
			{
				logger.debug("Returning FALSE for LOOPBACK as we received either NO_DESTINATION_FOUND or LOOP_DETECTED from dispatcher :"+e);
			}
		}

   		return loopbackRequired;
	
	}  

	/** 
	 * This method is used, if the standby machine whom an AseIc is replicated to,
	 *  wants to do some processing with this AseIc object prior to the activation 
	 * of it.
	 */

	public 	void partialActivate() {
		if (logger.isDebugEnabled()) {

		logger.debug("Entering AseIc partialActivate().");

		}
			super.partialActivate();
		if (logger.isDebugEnabled()) {

		logger.debug("Leaving AseIc partialActivate().");
		}
			
	}
	/**
	 * This method sets context itself active and also activates 
	 * each Replicables
	 */
	//@saneja 13769, identifed deadlock as activate is called form icLock on messages received form n/w; 
	//on the other hand after ft activate is called without icLock and takes lock form insides
	// removed synchronized
	public void activate() {
	  
	  // If this is alread activated, return from here
	  if(this.active) {
	  	if(logger.isDebugEnabled()){
			logger.debug("This is already activated. Returning.");
		}

	  	return;
	  }

	  try {
	     icLock.lockInterruptibly();
	  } catch (InterruptedException e) {
	     logger.error(e);
	  }
	  	//@saneja added to minimize exceptions on trace
		//though rare it is possible that DATI waits for activate lock and activator is activating.
		//To avoid duplicate actovation added below logic::
		// If this is alread activated, return from here
		if (this.active ) {
			if (logger.isDebugEnabled()) {
				logger.debug("second: This is already activated 2>. Sorry for waiting you on lock :).");
			}

			//release lock before returning
			try {
				icLock.unlock();
			} catch (Error e) {
				logger.error(e);
			}
			return;
		}
	  //]closed sanejs
	  
	  try {
		  //FT Handling Strategy Update: Firstly making AseIC active then its
		  //list of valid app sessions and sip session.
		  //As soon as app session gets activated it generates the callback to
		  //application without considering the fact that whether AseIC got
		  //activated or not.After getting the call back application can 
		  //create request on sip session, which in turn checks the AseIC's 
		  //state (active or not). In this scenario it may find that AseIC is not
		  //yet active rather in the process of becoming active. This may lead to
		  //contention between app call back thread and AseIc thread.
		  this.active = true;
		  super.activate();
		                   

		  // Set the sequence number to 0 so that clean up messages
		  // won't get replicated to the peer when it comes back up.
		  // Set the activated flag, so that we don't increment the
		  // counter for activated messages.
		  ((ReplicationInfo)this.info).setSequenceNo((short)0);
		  ((ReplicationInfo)this.info).setActivated(true);

		  //Increment the measurement counter
		  AseMeasurementUtil.counterActivated.increment();
	  } finally {
	  	  if(logger.isInfoEnabled()){
	  	  	logger.info("activate completed on IC:" + this);
	  	  }
		  try {
		     icLock.unlock();
		  } catch (Error e) {
		     logger.error(e);
		  }
	  }
	}
	
	/**
	 * Implemented from the Replication Listener Interface.
	 * This method provides the locking support 
	 * over the base class implementation
	 */
	public int handleReplicationEvent(ReplicationEvent event) {
		int retValue = 0;

		if(!this.isActive()) {
			return retValue;
		}

		//acquire the lock.
		try {
			icLock.lockInterruptibly();
		} catch (InterruptedException e) {
			logger.error(e);
		}
		
		try {
			retValue = super.handleReplicationEvent(event);
		} finally {
			if(logger.isInfoEnabled()){
				logger.info("handleReplicationEvent Completed for :"+ this);
			}
			try {
				icLock.unlock();
			} catch (Error e) {
				logger.error(e);
			}
		}
		return retValue;
	}

    /**
     * Implemented from ReplicationContext to return a Collection of 
     * AppInfo objects each of which contain meta data on an application 
     * whose replicable objects are contained by this AseIc.
     *
     * @see com.baypackets.ase.channel.AppInfo
     */
    public Collection getAppInfo() {
        return appInfo;
    }
		
    public boolean hasApplication(String id){
	if(id == null)
		return false;

	Iterator it = appInfo != null ?  appInfo.iterator() : null;
	for(;it !=null && it.hasNext();){
		AppInfo tmp = (AppInfo)it.next();
		if(tmp.getApplicationId() != null && id.equals(tmp.getApplicationId())){
			return true;
		}
	}
	
    	return false;
    }

	
	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		appInfo = (Collection)in.readObject();		
		appChainCounter = in.readInt();
		super.readIncremental(in);
	}

	public void writeIncremental(ObjectOutput out, int replicationType) throws IOException {
		out.writeObject(appInfo);
		out.writeInt(appChainCounter);
		super.writeIncremental(out, replicationType);
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		if(logger.isInfoEnabled()){
			logger.info("readExternal called on IC :" + this.getId());
		}
		workQueue = in.readInt();
		super.readExternal(in);
    }
	
	public void writeExternal(ObjectOutput out) throws IOException {
		if(logger.isInfoEnabled()){
			logger.info("writeExternal called on IC :" + this.getId());
		}
		
		out.writeInt(workQueue);
		super.writeExternal(out);
	}
	        
	////////////////// Application Composition methods start //////////////////

	public void addSession(AseProtocolSession session) {
		if (logger.isDebugEnabled()) {

		logger.debug("addSession(AseProtocolSession): enter");
		}
		int cid = session.getChainInfo().getChainId();
		AseSipApplicationChain chain = appChains.get(cid);
		if(chain == null){
			Iterator<AseSipApplicationChain> it = appChains.values().iterator();
			for(;it.hasNext();){
				AseSipApplicationChain tmp = it.next();
				if(tmp.isMatching(session)){
					chain = tmp;
					break;
				}
			}
		}
		if(chain == null){
			// This means session cannot be chained at downstream
			session.getChainInfo().setChainedDownstream(false);
			chain = new AseSipApplicationChain(++appChainCounter);
			appChains.put(chain.getId(), chain);
			
		}
		chain.addSession(session);
		if(logger.isDebugEnabled()) {
			logger.debug("Added " + session + " to app-chain with id : " + session.getChainInfo().getChainId());

		logger.debug("addSession(AseProtocolSession): exit");
	}
	}

	/**
	 * Uses only previous session stored in request to find next session in
	 * chain. This session is never null for requests arriving here.
	 */
	public AseProtocolSession getSession(AseBaseRequest request) {
		if (logger.isDebugEnabled()) {

		logger.debug("getSession(AseBaseRequest): enter");
		}
		int cid = request.getPrevSession().getChainInfo().getChainId();
		AseSipApplicationChain ac = (AseSipApplicationChain)appChains.get(cid);
		if(ac == null){
			logger.error("Chain id in request is out of range");
			if (logger.isDebugEnabled()) {

			logger.debug("getSession(AseBaseRequest): exit");
			}
			return null;
		}

		AseProtocolSession ps = null;
		ps = (AseProtocolSession)ac.getSession(request);

		if(ps != null) {
			if(logger.isDebugEnabled()) {
				logger.debug("Returning " + ps + " from app-chain with id : "
																	+ cid);
			}
		} else {
			if (logger.isDebugEnabled()) {

			logger.debug("Returning null");
		}
		}
		if (logger.isDebugEnabled()) {

		logger.debug("getSession(AseBaseRequest): exit");
		}
		return ps;
	}

	public void removeSession(AseProtocolSession session) {
		if (logger.isDebugEnabled()) {

		logger.debug("removeSession(AseProtocolSession): enter");
		}
		int cid = session.getChainInfo().getChainId();
		AseSipApplicationChain ac = (AseSipApplicationChain)appChains.get(cid);
		if(ac == null){
			logger.error("Chain id in session is out of range");
			if (logger.isDebugEnabled()) {

			logger.debug("removeSession(AseProtocolSession): exit");
			}
			return;
		}

		ac.removeSession(session);
		if(logger.isDebugEnabled()) {
			logger.debug("Removed " + session + " from chain with id : " + cid);


		logger.debug("removeSession(AseProtocolSession): exit");
		}
	}

	public AseProtocolSession getUpstreamEdge(AseProtocolSession session) {
		if(logger.isDebugEnabled()) {

		logger.debug("getUpstreamEdge(AseProtocolSession): enter");
		}
		AseProtocolSession rs = null;

		AseSipApplicationChain chain = null;
		Iterator<AseSipApplicationChain> it = appChains.values().iterator();
		for(;it.hasNext();){
			AseSipApplicationChain tmp = it.next();
			if(tmp.isMatching(session)){
				chain = tmp;
				break;
			}
		}
		
		if(chain == null){
			logger.error("No app-chain exists corresponding to session");
		} else {
			rs = chain.getUpstreamEdge();
			if(logger.isDebugEnabled())
				logger.debug("Returning session : " + rs);
		}
		if(logger.isDebugEnabled()) {

		logger.debug("getUpstreamEdge(AseProtocolSession): exit");
		}
		return rs;
	}

	public void activateSession(AseProtocolSession session) {
		if(logger.isDebugEnabled()) {

		logger.debug("activateSession(AseProtocolSession): enter");
		}
		int cid = session.getChainInfo().getChainId();
		AseSipApplicationChain chain = (AseSipApplicationChain)appChains.get(cid);
		if(chain == null){
			chain = new AseSipApplicationChain(cid);
			appChains.put(cid, chain);
		}
		chain.activateSession(session);

		if(logger.isDebugEnabled()) {
			logger.debug("Added " + session + " to app-chain with id : " + cid);
		

		logger.debug("activateSession(AseProtocolSession): exit");
	}
	}

	/////////////////// Application Composition methods end ///////////////////

	public boolean containsActiveSipDialog() {
		Iterator iter = this.appSessions.values().iterator();
		while(iter.hasNext()) {
			AseApplicationSession appSession = (AseApplicationSession)iter.next();
			if(appSession.getState() != Constants.STATE_VALID){
				continue;
			}

			Iterator protocolSessions = appSession.getSessions();
			if(protocolSessions == null)
				continue;
			while (protocolSessions.hasNext()) {
				Object sess = protocolSessions.next();
				if (sess instanceof AseSipSession) {
					int state = ((AseSipSession)sess).getSessionState();
					if (state == AseSipSessionState.STATE_CONFIRMED
					|| state == AseSipSessionState.STATE_EARLY) {
						return true;
					}
				}
			} // while - inner
		} // while - outer

		return false;
	}

	public String getDiagnosticInfo(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Call [Id =");
		buffer.append(this.getId());
		Iterator iter = this.appSessions.values().iterator();
		while(iter.hasNext()) {
			AseApplicationSession appSession = (AseApplicationSession)iter.next();
			if(appSession.getState() != Constants.STATE_VALID){
				if(logger.isDebugEnabled()){
					logger.debug("Application Session already invalidated:" + appSession.getAppSessionId());
				}
				continue;
			}
			
			buffer.append(AseStrings.SEMI_COLON);
			buffer.append(appSession.toString());
			Iterator protocolSessions = appSession.getSessions();
			if(protocolSessions == null)
				continue;
			buffer.append(" Protocol Sessions = {");
			for(;protocolSessions.hasNext();){
				buffer.append(AseStrings.COMMA);
				buffer.append(protocolSessions.next());
			}
			buffer.append(AseStrings.BRACES_CLOSE);
		}
		buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		return buffer.toString();
	}

	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("Call [Id =");
		buffer.append(this.getId());
		buffer.append(", Protocol Sessions = {");
		Iterator iter = this.appSessions.values().iterator();
		while(iter.hasNext()) {
			AseApplicationSession appSession = (AseApplicationSession)iter.next();
			buffer.append("Appsession::");
			buffer.append(appSession);
			if(appSession.getState() != Constants.STATE_VALID){
				if(logger.isDebugEnabled()){
					logger.debug("Application Session already invalidated:" + appSession.getAppSessionId());
				}
				continue;
			}
			
			Iterator protocolSessions = appSession.getSessions();
			if(protocolSessions == null)
				continue;
			for(;protocolSessions.hasNext();){
				buffer.append(AseStrings.COMMA);
				buffer.append(protocolSessions.next());
			}
		}
		buffer.append("}]");
		return buffer.toString();
	}

	public Destination getLastDestination() {
		return lastDestination;
	}
	
	/**
	 * This method reset AppSyncMessage receive status for all applications who are in AppInfo List for this AseIc.
	 */
	public void resetAllAppSyncForDeployReceived(){
		if(logger.isDebugEnabled())
			logger.debug("resetAllAppSyncForDeployReceived(): enter");
		if(this.appInfo!=null && appInfo.size()!=0)
		{
			for(Object info:appInfo){
				((AppInfo)info).setAppSyncForDeployReceived(false);
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("resetAllAppSyncForDeployReceived(): exit");
	}
	
	
	/**
	 * This method returns boolean indicating that AppSyncMessage for all application received or not
	 * @return
	 */
	public boolean isAllAppSyncForDeployReceived(){
		if(logger.isDebugEnabled())
			logger.debug("isAllAppSyncForDeployReceived(): enter for Ase IC:"+AseStrings.COLON+this.getId());
		boolean result=true;
		if(this.appInfo!=null && appInfo.size()!=0)
		{
			for(Object info:appInfo){
				result=result & ((AppInfo)info).isAppSyncForDeployReceived();
				if(result==false)
					break;
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("isAllAppSyncForDeployReceived() :exit for Ase IC:"+this.getId()+AseStrings.COLON+result);
		return result;
	}
	
	/**
	 * This method sets boolean indicating that AppSyncMessage for the application received or not
	 * @param applicationId Application for which value is to be set.
	 * @param value true/false for received status.
	 * @throws Exception If application name not found in AppInfo List for AseIc
	 */
	public void setAppSyncForDeployReceived(String applicationId,boolean value) throws Exception{
		if(this.appInfo!=null && appInfo.size()!=0 && applicationId!=null)
		{
			boolean isAppFound=false;
			for(Object info:appInfo){
				AppInfo appinfoObj = (AppInfo)info;
				if(applicationId.equals(appinfoObj.getApplicationId())){
					appinfoObj.setAppSyncForDeployReceived(value);
					isAppFound=true;
				}
			}
			if(!isAppFound)
				throw new Exception("Application not found in AppInfo List");
		}
	}
}
