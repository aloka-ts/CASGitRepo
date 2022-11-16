/*
 * Created on Aug 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.container.sip;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TimerListener;
import javax.servlet.sip.URI;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseEvent;
import com.baypackets.ase.common.AseEventListener;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseProtocolSession;
import com.baypackets.ase.container.AseThreadData;
import com.baypackets.ase.container.AseTimerClock;
import com.baypackets.ase.container.AseWrapper;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.sbb.SBBFactory;
import com.baypackets.ase.sipconnector.AseSipDiagnosticsLogger;
import com.baypackets.ase.sipconnector.AseSipURIImpl;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;

/**
 * @author Ravi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class SipApplicationSessionImpl extends AseApplicationSession implements SipApplicationSession, AseEventListener {
	
	private static Logger logger = Logger.getLogger(SipApplicationSessionImpl.class);
	private static final long serialVersionUID = -3814634264647849793L;
	private ArrayList timers = new ArrayList();
	private int timerCounter = 0;
	private ArrayList cancelledList = new ArrayList();	//list of cancelled timers
	
	private transient boolean isExpired = false;

	private boolean invalidateWhenReady=true;

	private boolean isValid = true;
	
	//BUG6765 (LIVE SBB UPGRADE)
	private AseWrapper sbbWrapper = null;
	private SBBFactory sbbFactory = null;
	
	public AseWrapper getSBBWrapper() {
		return sbbWrapper;
	}

	public void setSBBWrapper(AseWrapper wrapper) {
		this.sbbWrapper = wrapper;
	}

	public SBBFactory getSbbFactory() {
		return sbbFactory;
	}

	public void setSbbFactory(SBBFactory sbbFactory) {
		this.sbbFactory = sbbFactory;
	}	

	public SipApplicationSessionImpl(AseContext ctx){

		super(ctx);	
		this.objectCreated();


       	if (logger.isDebugEnabled()) {
           	logger.debug("Creating SipApplicationSessionImpl with ctx()");
       	}
       	
       	//Bug 6040
       	if(ctx != null && ctx.isServletMapPresent() == true) {
           	if (logger.isDebugEnabled()) {
               	logger.debug("Application is JSR116 complaint. So, it must itself invalidate the sessions");
           	}
       		invalidateWhenReady = false;
       	}
       	
       	//BUG6765 (LIVE SBB UPGRADE)
       	if(ctx.getUsesSBB() == true) {
       		sbbFactory = (SBBFactory) ctx.getAttribute(Constants.SBB_FACTORY);
       		sbbWrapper = (AseWrapper) ctx.findChild(Constants.SBB_SERVLET_NAME);
       	}
	}
	
	/**JSR 289.42
	 * This constructor creates SipApplication session with the sessionID.It is
	 * primarly used for targetting request using SipapplicationKeyAnnotation.
	 */
	public SipApplicationSessionImpl(AseContext ctx,String sessionId){

		super(ctx,sessionId);	
		this.objectCreated();


       	if (logger.isDebugEnabled()) {
           	logger.debug("Creating SipApplicationSessionImpl with ctx() and id:"+sessionId);
       	}
       	
       	//Bug 6040
       	if(ctx != null && ctx.isServletMapPresent() == true) {
           	if (logger.isDebugEnabled()) {
               	logger.debug("Application is JSR116 complaint. So, it must itself invalidate the sessions");
           	}
       		invalidateWhenReady = false;
       	}
       	
       	//BUG6765 (LIVE SBB UPGRADE)
       	if(ctx.getUsesSBB() == true) {
       		sbbFactory = (SBBFactory) ctx.getAttribute(Constants.SBB_FACTORY);
       		sbbWrapper = (AseWrapper) ctx.findChild(Constants.SBB_SERVLET_NAME);
       	}
	}
//JSR 289.42
	public SipApplicationSessionImpl() {

		super();

       	if (logger.isDebugEnabled()) {
           	logger.debug("Creating SipApplicationSessionImpl without ctx()");
       	}
	}

	public void activate(ReplicationSet parent) {
		if (logger.isDebugEnabled()) {
              	logger.debug("Entering SipApplicationSessionImpl activate()");
        	}
		
		super.activate(parent);

        	if (logger.isDebugEnabled()) {
              	logger.debug("Leaving SipApplicationSessionImpl activate()");
        	}
	}

	public void writeExternal(ObjectOutput out) throws IOException {
        	if (logger.isDebugEnabled()) {
            		logger.debug("Entering SipApplicationSessionImpl writeExternal()");
        	}
        
        	out.writeInt(this.timerCounter);
        	out.writeBoolean(this.invalidateWhenReady);
        	//This is not needed as this is resulting in diplicate AseWrapper
        	//object at standby. This is reason why encapsulated SBB Servlet
        	//doesn't have the state which is expected from a servlet object.
        	//For instance , the object doesn't have the Servlet Context
        	//out.writeObject(this.sbbWrapper);
        	
        	super.writeExternal(out);
		
        	if (logger.isDebugEnabled()) {
            	logger.debug("Leaving SipApplicationSessionImpl writeExternal()");
        	}
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
        	if (logger.isDebugEnabled()) {
            		logger.debug("Entering SipApplicationSessionImpl readExternal()");
        	}

        	this.timerCounter = in.readInt();
        	this.invalidateWhenReady =in.readBoolean();
        	//This is not needed as this is resulting in diplicate AseWrapper
        	//object at standby. This is reason why encapsulated SBB Servlet
        	//doesn't have the state which is expected from a servlet object.
        	//For instance , the object doesn't have the Servlet Context
        	//this.sbbWrapper = (AseWrapper) in.readObject();
        
        	super.readExternal(in);
        	
        	//We will get the Ase Wrapper with the SBB servlet object which is properly
        	//initialized at standby
        	if (this.ctx != null){
            	if (logger.isDebugEnabled()) {
                	logger.debug("Context is de-serialized");
            	}
        		this.sbbWrapper = (AseWrapper) this.ctx.findChild(Constants.SBB_SERVLET_NAME);
            	this.sbbFactory = (SBBFactory) ctx.getAttribute(Constants.SBB_FACTORY);
        		if (logger.isDebugEnabled()) {
                	logger.debug("SBB Wrapper re-incarnated" + this.sbbWrapper != null ? "DONE":"NOT DONE");
            	}
        	}
        		
        	
        	if (logger.isDebugEnabled()) {
            	logger.debug("Leaving SipApplicationSessionImpl readExternal()");
        	}
	}

	/** This method writes the number of timers in the cancelled timers List and the replicableId of each timer.
	*/

	public void writeIncremental(ObjectOutput out, int replicationType) throws IOException
	{
	
        	if (logger.isDebugEnabled()) {
            		logger.debug("Entering SipApplicationSessionImpl writeIncremental()");
        	}

        	
		int sizeCancelledList = cancelledList.size();
		out.writeInt(sizeCancelledList);
		
		for(int counter=0;counter<sizeCancelledList;counter++)
		{
			ServletTimerImpl servletTimerImpl = (ServletTimerImpl)cancelledList.get(counter);
			String replicableId = servletTimerImpl.getReplicableId();

			out.writeObject(replicableId);
		}
		super.writeIncremental(out, replicationType);
		
	}
	/** This method reads the number of timers in the cancelled timers list and the replicableId of each timer.
	*   It then removes those timers from the timers list whose replicableId has been read in this method
	*/

	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException
	{
		if (logger.isDebugEnabled()) {
    		logger.debug("Entering SipApplicationSessionImpl readIncremental()");
	}
		
		
		int sizeCancelledList = in.readInt();
		
		for(int counter=0;counter<sizeCancelledList;counter++)
		{
			String replicableId = (String)in.readObject();
			int sizeTimers = timers.size();
			for(int i=0;i<sizeTimers;i++)
			{
				if((((ServletTimerImpl)timers.get(i)).getReplicableId()).equals(replicableId))
				{
					timers.remove(i);
					break;
				}
				continue;
			}
		}
		super.readIncremental(in);
		
		

	}

	public void replicationCompleted() {
		replicationCompleted(false);
	}
	
	public void replicationCompleted(boolean noReplication)	{
		cancelledList.clear();
		super.replicationCompleted(noReplication);
	}

			
		
   /*
	public void writeIncremental(ObjectOutput out) {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering SipApplicationSessionImpl writeIncremental()");
        }

		try {
			out.writeObject(timers);
			super.writeIncremental(out);
		}
		catch (Exception e) {
            logger.error("Exception in writeObject()" + e.toString(), e );
		}

        if (logger.isDebugEnabled()) {
            logger.debug("Leaving SipApplicationSessionImpl writeIncremental()");
        }
	}

	public void readIncremental(ObjectInput in) {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering SipApplicationSessionImpl readIncremental()");
        }

		try {
			timers = (ArrayList)in.readObject();
			super.readIncremental(in);
		}
		catch (Exception e) {
            logger.error("Exception in readIncremental()" + e.toString(), e);
		}

        if (logger.isDebugEnabled()) {
            logger.debug("Leaving SipApplicationSessionImpl readIncremental()");
        }
	} */
       

	/**
	 * Implmented from SipApplicationSession interface
	 */
	public void encodeURI(URI uri) {
		this.checkValid();
		
		if(!(uri instanceof SipURI)){
			throw new IllegalArgumentException("Donot know how to encode Non SIP URI");
		}
		
		//Check if the URI is Immutable.
		if(uri instanceof AseSipURIImpl){		
			AseSipURIImpl uriImpl = (AseSipURIImpl)uri;
			if(uriImpl.isImmutable()){
				throw new IllegalStateException("The SIP URI is immutable");
			}
		}
		
		//Add the session ID into the URI as a parameter
		((SipURI)uri).setParameter(ENCODE_KEY, this.getId());
	}

	/**
	 * @return a Collection of timer objects(javax.servlet.sip.ServletTimer)
	 */
	public Collection getTimers() {
		this.checkValid();
		return this.timers;
	}
	
        
	/**
	 * Invaildates this session. 
	 * This method will be explicitely called from the application 
	 * or it will be called when the session timesout. 
	 */
	public void invalidate(){

     		boolean changeLock = AseThreadData.setIcLock(this.getIc());

            try {
                // Check the state of this session
                this.checkValid();
		
				AseSipDiagnosticsLogger diag = AseSipDiagnosticsLogger.getInstance();
				if (diag.isAppInvalidationLoggingEnabled()) {
		    		if(!this.isExpired()) {
						// Session is being invalidated by application
						diag.log("App invalidated AppSession: " + this.getId());
					}
				}

                // Set the state to invalidating.
                if (this.getState() == Constants.STATE_VALID){
                    this.setState(Constants.STATE_INVALIDATING);
                }
		
                // Cancel all the associated timers with this session.
                for (int i=0; i<this.timers.size();i++){
                	if(logger.isDebugEnabled()){
                		logger.debug("Cancelling the timer at index :" +i);
                	}
                    ServletTimerImpl timer = (ServletTimerImpl) this.timers.get(i);
                    timer.cancel0();
                }
                this.timers.clear();
		
                // Call invalidate on the super class.
                super.invalidate();
                this.isValid =false;
            } catch (Exception e) {
                logger.error("invalidate(): ", e);
            } finally {
				AseThreadData.resetIcLock(this.getIc(), changeLock);
            }
	}
	
        
	/**
	 * This method is called from the TimerService when the timer expires.
	 * This method enques the message into the container's work queue.
	 * @param timer that expired.
	 */
	void timeout(ServletTimerImpl timer){
		if(logger.isDebugEnabled()){
			logger.debug("Timeout called for timer :" +timer);
		}
		AseMessage message = timer.getMessage();
		
		// Reeta fixed for Protocol handler timer timeout handling in same inap thread
		if(this.getAttribute(Constants.TCAP_SESSION_ATTRIBUTE)!=null){
			
			if(logger.isDebugEnabled()){
				logger.debug("Timer is Tcap Service Timer : setInapMessage(true)");
			}
			message.setInapMessage(true);
		}
		
		//Remove the timer from the list if it is expired
		if(timer.isExpired()){
			this.timers.remove(timer);
			if(timer.getReplicableId() != null){
				this.removeReplicable(timer.getReplicableId());
			}
		}
		
		//Enqueue the message into the workers's pool.
		this.enqueMessage(message);
	}
	
	/**
	 * This method will be called from the ServletTimer implementation, 
	 * whenever a createTimer method is called by the application. 
	 * ServletTimer implementation calls this method to register the created timer.
	 * @param timer that needs to be added.
	 */
	void addTimer(ServletTimerImpl timer){
		if(timer != null){
			this.timers.add(timer);
		}
		if(timer.getReplicableId() == null){
			timer.setReplicableId(""+ (++this.timerCounter));
			this.setModified(true);
			this.setReplicable(timer);
		}
	}
	
	void cancelTimer(ServletTimerImpl timer){
		if(timer != null){
			timer.cancel0();
			this.timers.remove(timer);
			this.cancelledList.add(timer);
		}
		if(timer.getReplicableId() != null){
			this.removeReplicable(timer.getReplicableId());
		}
	}
	
	/**
	 * This method is called from the AppSession 
	 * to notify the registered timer listener that the timer has expired.
	 * This method runs in the context of a worker thread.
	 * @param timer that expired
	 */
	private void invokeTimerListener(ServletTimerImpl timer){
		
		//The timer is already cancelled, 
		//so do not deliver this message
		if(timer.isCancelled()){
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("Timer cancelled for appSession" + this.getAppSessionId() + ". So ignoring the timer event." );
			}
			return;
		}
		
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("invoking the timer listeners for sip timer expiry :" + this.getAppSessionId() );
		}
		//Get the list of listeners and invoke the timeout on each of them.
		Iterator listeners = this.getContext().getListeners(TimerListener.class).iterator();
		for(; listeners != null && listeners.hasNext();){
			TimerListener listener = (TimerListener)listeners.next();

			// If listener class is same as servlet class and not yet initialized then initialize it
			if(listener instanceof SipServlet) {
				try {
                                       if(logger.isEnabledFor(Level.INFO)){ 
                                        logger.info("call initServlet :" );
                                         } 
					this.getContext().initServlet(((SipServlet)listener).getServletName());
				} catch(ServletException exp) {
					logger.error("invokeTimerListener: initializing servlet", exp);
				}
			}else{
                              if(logger.isEnabledFor(Level.INFO)){
                                 logger.info("Not instance of AseWrapper"+listener );
                             } 
                         } 

			try {
				listener.timeout(timer); 
			} catch (Throwable th) {
				logger.error(th.getMessage(), th);
			}
		}
		if(timer.getPersistent())	{
                
        	if (logger.isDebugEnabled()) {
            		logger.debug("Invoked all timer listeners.  Emitting a TIMER_EXIRY event for FT purposes...");
        	}
        
        	this.getIc().handleReplicationEvent(new ReplicationEvent(this, ReplicationEvent.TIMER_EXPIRY));
		}
	}

	/**
	 * The worker thread calls this method whenever it dequeues the event 
	 * for this app-session.
	 */
	public void handleEvent(EventObject eventObject) {
		AseEvent event = (AseEvent)eventObject;
		boolean changeLock = AseThreadData.setIcLock(this.getIc());

		try {
			super.handleEvent(event);
			int type = event.getType();
			if(event.getType() == Constants.EVENT_SIP_TIMER) {
				this.invokeTimerListener((ServletTimerImpl)event.getData());
			}
		} finally {
			AseThreadData.resetIcLock(this.getIc(), changeLock);
		}	
	}
	
	/**
	 * This method notifies all the SipApplication Listeners that the 
	 * appsession is created
	 */
	public void objectCreated() {
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("objectCreated called on Application Session :"+this.getAppSessionId());
		}
		Iterator iterator = this.getContext().getListeners(SipApplicationSessionListener.class).iterator(); 
		for(;iterator != null && iterator.hasNext();){
			SipApplicationSessionListener listener = (SipApplicationSessionListener) iterator.next(); 
			try  {
				listener.sessionCreated(new SipApplicationSessionEvent(this)); 
			} catch(Throwable th ) {
				logger.error(th.getMessage(), th);
			}
		}
	}

	/**
	 * This method notifies all the SipApplication Listeners that the 
	 * appsession is destroyed
	 */
	public void objectDestroyed() {
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("objectDestroyed called on Application Session :"+this.getAppSessionId());
		}
		Iterator iterator = this.getContext().getListeners(SipApplicationSessionListener.class).iterator(); 
		for(;iterator != null && iterator.hasNext();){
			SipApplicationSessionListener listener = (SipApplicationSessionListener) iterator.next(); 
			try  {
				listener.sessionDestroyed(new SipApplicationSessionEvent(this)); 
			} catch (Throwable th) {
				logger.error(th.getMessage(), th);
			}
		}
	}

	/**
	 * Tells if this ApplicationSession expired of was invalidated.
	 */
	public boolean isExpired() {
		return this.isExpired;
	}

	/**
	 * This method notifies all the SipApplication Listeners that the 
	 * appsession expired
	 */
	public void objectExpired() {
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("objectExpired called on Application Session :"+this.getAppSessionId());
		}
		if(this.getState() == Constants.STATE_INVALID || 
			this.getState() == Constants.STATE_DESTROYED){
			logger.info("object already invalidated");
			return;
		}

		this.isExpired = true;

		Iterator iterator = this.getContext().getListeners(SipApplicationSessionListener.class).iterator(); 
		for(;iterator != null && iterator.hasNext();){
			SipApplicationSessionListener listener = (SipApplicationSessionListener) iterator.next(); 
			try {
				listener.sessionExpired(new SipApplicationSessionEvent(this)); 
			} catch (Throwable th) {
				logger.error(th.getMessage(), th);
			}
		}

		if(timerValue>0)
		{
			AseTimerClock.getInstance().add(this);		
			if(logger.isEnabledFor(Level.INFO))
			{
				logger.info(" Application Session Timer incremented by "+timerValue+" seconds");
			}
		}
		else
		{
			//This check is added to prevent invalidation of App Session twice as on session expired
			//call back service might invalidate the session
			if (this.getState() != Constants.STATE_INVALID && this.getState() != Constants.STATE_DESTROYED){
				if(logger.isEnabledFor(Level.INFO))
				{
					logger.info(" Application Session is not invalidated by service");
				}
				this.setState(Constants.STATE_INVALIDATING);
			}
		}		
		    //If none of the listener's extended the lifetime of this session,
		    //Invalidate this session.
		    if(this.getState() == Constants.STATE_INVALIDATING){
			    this.invalidate();
			    AseMeasurementUtil.counterTotalTimeoutAppSessions.increment();
		}
	}
	
	
	 public void objectReadyToInvalidate(){
		
		 if(logger.isEnabledFor(Level.INFO)){
				logger.info("objectReadyToInvalidate called on Application Session :"+this.getAppSessionId());
			}
			if(this.getState() == Constants.STATE_INVALID || 
				this.getState() == Constants.STATE_DESTROYED){
				logger.info("object already invalidated");
				return;
			}

			//this.isExpired = true;
			// The container will invalidate this Appsession upon completion of this callback 
			// unless the application calls SipApplicationSessionEvent.getApplicationSession().setInvalidateWhenReady(false)
			timerValue=0;
			Iterator iterator = this.getContext().getListeners(SipApplicationSessionListener.class).iterator(); 
			for(;iterator != null && iterator.hasNext();){
				SipApplicationSessionListener listener = (SipApplicationSessionListener) iterator.next(); 
				try {
					listener.sessionReadyToInvalidate(new SipApplicationSessionEvent(this)); 
				} catch (Throwable th) {
					logger.error(th.getMessage(), th);
				}
			}

			if(timerValue>0)
			{
				AseTimerClock.getInstance().add(this);		
				if(logger.isEnabledFor(Level.INFO))
				{
					logger.info(" Application Session Timer incremented by "+timerValue+" seconds");
				}
			}
			else
			{
				this.setState(Constants.STATE_INVALIDATING);
			}		
			// If application has called SipApplicationSession.setInvalidateWhenReady(false)
			// then only container will not invalidate the session.
			    if(this.getState()==Constants.STATE_INVALIDATING && getInvalidateWhenReady()){
				    this.invalidate();
				    AseMeasurementUtil.counterTotalTimeoutAppSessions.increment();
			}
	}

	 /*
	  * @see javax.servlet.sip.SipApplicationSession#encodeURL(java.net.URL)
	  * Adds a get parameter to the URL like this:
	  * http://agnity/sas -> http://agnity/sas?sas-sessionid=APP-SESSION-ID
	  * http://agnity/sas?something=1 -> http://agnity/sas?something=1&sas-sessionid=APP-SESSION-ID
	  */
	 public URL encodeURL(URL url){
		 this.checkValid();
		 String urlStr = url.toExternalForm();
		 URL retEncodeURL = null;
		 try{
			 if(!url.getProtocol().equalsIgnoreCase(AseStrings.PROTOCOL_HTTP)){
				 throw new IllegalArgumentException(" Not a HTTP URL ");
			 }
			 if(urlStr.contains(AseStrings.QUESTION_MARK)){
				 retEncodeURL =  new URL(url + AseStrings.AMPERSAND +ENCODE_KEY+AseStrings.EQUALS+this.getId());
			 }else{
				 retEncodeURL =  new URL(url + AseStrings.QUESTION_MARK +ENCODE_KEY+AseStrings.EQUALS+this.getId());
			 }
		 } catch (Exception e) {
			 logger.error("Exception in encodeURL " + e.getMessage());
		 }
		 return retEncodeURL; 
	 }

	public String getApplicationName() {
		return super.getApplicationName();
	}

	public long getExpirationTime() {
		int timeout = getTimeout();
		
		if(isExpired()) {
			return Long.MIN_VALUE;
		}
		if(timeout <=0) {
			return 0;
		} else {
			return getCreationTime() + timeout*60*1000;
		}
	}

	public boolean getInvalidateWhenReady() {
		if(!isValid())
			throw new IllegalStateException("Session Already Invalidated");
		return invalidateWhenReady;
	}

	public Object getSession(String id, Protocol protocol) {

		this.checkValid();
		if(id == null || protocol == null)
			throw new NullPointerException("Null Arguments not allowed");
		Object obj = null;
		AseProtocolSession session = this.getSession(id);
		if(session != null) {
			if(session.getProtocol().equals(protocol.toString()))
			{
				obj = (Object)session;
			}
		}
		return obj;
	}

	public SipSession getSipSession(String id) {

		if(id == null)
			throw new NullPointerException("Null Argument not allowed");
		
		SipSession user_session = null;
		Iterator<SipSession>  sessionItr = this.getSessions(AseStrings.SIP);
		
		while(sessionItr.hasNext()){
			user_session = sessionItr.next();
			if(user_session.getId().equals(id)) {
				return user_session;
			}
		}
		
		return null;
	}

	public ServletTimer getTimer(String id) {
		if(id == null) {
			if(logger.isDebugEnabled()){
				logger.debug("Id passed is null. Returning");
			}
			return null;
		}


		Iterator itr = timers.iterator();
		ServletTimerImpl timer = null;
		while(itr.hasNext()) {
			timer = (ServletTimerImpl)itr.next();
			if(timer.getId().equals(id)) {
				return timer;
			}
		}
		return null;
	}

	public boolean isReadyToInvalidate() {
		
		if(!isValid())
			throw new IllegalStateException("Session Already Invalidated");
		boolean ready = true;
		Iterator<SipSession>  sessionItr = this.getSessions("SIP");
		
		while(sessionItr.hasNext()){
			if(!sessionItr.next().isReadyToInvalidate())
				ready= false;
		}
		
		Collection<ServletTimer> coll =this.getTimers();
		
		Iterator< ServletTimer> itr = coll.iterator();
		
		while(itr.hasNext()){
			if(itr.next().getTimeRemaining()>0){
				ready= false;
			}
			
		}
		return ready;
	}

	public boolean isValid() {
		return isValid;
	}

	public void setInvalidateWhenReady(boolean invalidateWhenReady) {
		if(!isValid())
			throw new IllegalStateException("Session Already Invalidated");
		this.invalidateWhenReady = invalidateWhenReady;
		
	}
}
