
/*
 * Created on Oct 30, 2004
 *
 */
package com.baypackets.ase.sbb.impl;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipSession;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.MessageFilter;
import com.baypackets.ase.sbb.IncomingMessageListener;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBCallback;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.mediaserver.MediaServerInfoHandler;
import com.baypackets.ase.sbb.util.Constants;


/**
 */

@DefaultSerializer(ExternalizableSerializer.class)
public abstract class SBBImpl implements SBB, SBBOperationContext, Externalizable{
	
	private static final long serialVersionUID = -3322448118251L;
	/* Name of SBB */	
	private String name;

	/* Application's servlet context */
	private transient ServletContext servletContext;
	
	private String partyAId;
	private String partyBId;

	/*SIP session of party-A*/	
	private transient SipSession partyA = null;

	/*SIP session of party-B*/	
	private transient SipSession partyB = null;

	/* This hashTable will store all attributes */	
	private Hashtable attributes =  new Hashtable();

	/* making public for alc Listener for SBB callbacks */
	public transient SBBEventListener sbbEventListener = null;
	
	/* List of operations */
	private Vector operations = new Vector();
	
	private Hashtable callbacks = new Hashtable();

	/* SBB validity flag */
	boolean isSBBValid = true;

	/* Application Session */
	public transient SipApplicationSession appSession = null;

	/*Servlet timer map with [timerId,TimerInfo]*/
	private HashMap servletTimerMap; 
	
	/* Message Filter for this SBB */
	MessageFilter msgFilter = null;

	IncomingMessageListener inMessListener = null;

	/** Logger element */
    private static Logger logger = Logger.getLogger(SBBImpl.class.getName());


	/**
     * Returns the Name of this SBB.
     * The name would be unique across the SBBs available in the Application Session
     * @return Name of this SBB object.
     */
	public String getName() {
		return name;
	}

	public void setName(String name){
		this.name = name;
	}


	/**
     * This method sets a behavioral attribute on this object.  The value of a
     * particular attribute determines how this object will behave when
     * handling a specific aspect of the call being managed by this object.
     * The behavioral attribute names and values are enumerated as public static
     * constants of this interface while those that are specific to particular
     * type of call are enumerated in the corresponding SBB interface.
     * For example, to enable RTP tunneling for the purpose of propagating a
     * callee's ring tone to the caller during the setup of a back-to-back
     * user agent session, the following attribute would be set:
     * <p>
     * <pre>
     * <code>
     *  SBB sbb = ...;
     *  sbb.setAttribute(RTP_TUNNELING, new Boolean(true));
     * </code>
     * </pre>
     * </p>    
     *
     * @throws IllegalArgumentException if the attribute specified is not
     * supported by this object.
     * @throws IllegalStateException if this object was previously invalidated.
     */
	public void setAttribute(String name, Object value) throws IllegalArgumentException, 
											IllegalStateException  {

		if (name == null || value == null) {
			logger.error("<SBB> Either name or value is illegal");
			throw new IllegalArgumentException();
		}
		
	
		if (!(name.equalsIgnoreCase(RECURSIVE_REDIRECT) ||
						name.equalsIgnoreCase(RTP_TUNNELLING) ||
						name.equalsIgnoreCase(HOLD_TYPE) ||
						name.equalsIgnoreCase(HOLD_METHOD) ||
						name.equalsIgnoreCase(DIRECTION) ||
						name.equalsIgnoreCase(EARLY_MEDIA) ||
						name.equalsIgnoreCase(RTP_TUNNELLING_18X_CODE) ||
						name.equalsIgnoreCase(RELAY_2XX_IN_EARLY_MEDIA))) {

			throw new IllegalArgumentException();	
		}
		
		if (! isValid()) {
			throw new IllegalStateException("SBB is not valid");
		}

		attributes.put(name,value);
	}



	/**
     * This method returns the value of the specified behavioral attribute.
     *
     * @return The value of the specified attribute or NULL if the attribute
     * is not currently set.
     * @throws IllegalStateException if this object was previously invalidated.
     */
	public Object getAttribute(String name) throws IllegalStateException {

		if (!isValid()) {
			logger.error("SBB is not valid");
			throw new IllegalStateException("SBB is not valid");
		}
		return(attributes.get(name));	
	}


	/**
     * This method returns the names of the behavioral attributes that are
     * currently set on this object.
     *
     * @return The names of the set attributes or an empty array if no
     * attributes are currently set.
     */
	public String[] getAttributeNames() {
		String[] nameList = new String[attributes.size()];
		Set keySet = attributes.keySet();
		Iterator itr = keySet.iterator();
		int i=0;
		while(itr.hasNext()) {
			nameList[i] = (String)itr.next();
		}
		return nameList;
	}
	
	

	/**
     * This method returns the names of the behavioral attributes that are
     * supported but not necessarily set on this object.
     *
     * @return The names of this object's supported attributes or an empty
     * array if none are applicable.
     */

	public String[] getSupportedAttributes() {
		int supportedAttrCount = 4;
		int i=0;
		String[] supportedAttributes = new String[supportedAttrCount];
		supportedAttributes[i++] = RECURSIVE_REDIRECT;
		supportedAttributes[i++] = RTP_TUNNELLING;
		supportedAttributes[i++] = HOLD_TYPE;
		supportedAttributes[i++] = HOLD_METHOD;
		return supportedAttributes;
	}

	
	/**
     * This method is invoked to invalidate this SBB object.  Any SipSessions
     * referenced by this object will be invalidated and also released from this SBB.
     * Any subsequent operation or mutator method invoked on this object
     * will result in an IllegalStateException being thrown.
     *
     * @throws IllegalStateException if this object was already invalidated.
     */
	public void invalidate() throws IllegalStateException {

		if(logger.isDebugEnabled())
			logger.debug("<SBB> Entered invalidate");

		isSBBValid = false;
		if(partyA != null){
			partyA.removeAttribute(SBBOperationContext.ATTRIBUTE_SBB);
		}
		if(partyB != null){
			partyB.removeAttribute(SBBOperationContext.ATTRIBUTE_SBB);
		}
		partyA = null;
		partyB= null;

		if(logger.isDebugEnabled())
			logger.debug("<SBB> Exited invalidate");
	}
	
	/**
     * Returns whether are not this SBB is in valid state.
     * @return true if not invalidated else false.
     */
	public boolean isValid() {
		return isSBBValid;
	}


		
	/**
     * Removes the SipSession representing the party A endpoint from this
     * SBB object.
     *
     * @throws IllegalStateException if there currently is no party A endpoint
     * attached or if this object was explicitly invalidated.
     */

	public SipSession removeA() {
	 //System.out.println("remove A : "+partyA +" From SBB :" + this.name );	
		if (logger.isDebugEnabled()) {
			logger.debug("removeA() called on SBB with name: " + this.getName() +" A party is "+partyA);
		}
                if (! isValid() || partyA == null) {
			throw new IllegalStateException("Invalid SBB or A-party not present");
		}
		SipSession tempSession = partyA;
		partyA.removeAttribute(SBBOperationContext.ATTRIBUTE_SBB);
		partyA = null;
		return tempSession;
	}


	/**
     * Removes the SipSession representing the party B endpoint from this
     * SBB object.
     *
     * @throws IllegalStateException if there currently is no party B endpoint
     * attached or if this object was explicitly invalidated.
     */
	public SipSession removeB() {
		
		boolean loggerEnabled = logger.isDebugEnabled();

		if (loggerEnabled) {
			logger.debug("removeB() called on SBB with name: " + this.getName()+" B party is "+partyB);
		}
	 //System.out.println("remove B : "+partyB +" From SBB :" + this.name );	
               if (!isValid() || partyB == null) {
            		throw new IllegalStateException("Invalid SBB or B-party not present");
        	}
		SipSession tempSession = partyB;
		partyB.removeAttribute(SBBOperationContext.ATTRIBUTE_SBB);
		// Setting the partyBId to null so that partyB SipSession should be null on Replication
		partyBId = null;
		partyB = null;
		return tempSession;
	}


	/**
     * Attaches the specified call party as the "party A" endpoint.
     *
     * @param session - Represents the endpoint to attach.
     * @throws IllegalStateException if there is still a party A attached or
     * if this object was invalidated.
     */
    public void addA(SipSession session) {
                //System.out.println("Add A : "+session +" To SBB :" + this.name ); 
		if (partyA != null) {
			throw  new IllegalStateException("A-Party already associated");
		}
		/*if (session.getAttribute(SBBOperationContext.ATTRIBUTE_SBB) != null) {
			throw  new IllegalArgumentException("SBB is already assosiated with party");
		}*/
		partyA = session;
		partyAId = session.getId();
		
		//  Associate SBB with party-A 
		partyA.setAttribute(SBBOperationContext.ATTRIBUTE_SBB,name);
			
		try {	
			//TODO remove this hardcoding of "SBBServlet"
			partyA.setHandler("SBBServlet");
			SBBSessionActivationListener sbbSessionActvationListenet 
						= new SBBSessionActivationListener();
			session.setAttribute(Constants.ATTR_SESSION_ACT_LISTENER,sbbSessionActvationListenet);
			//session.setAttribute(Constants.ATTR_SESSION_ACT_LISTENER,"SBBServlet");
		}
		catch(ServletException se) {
            logger.error("Not able to set handler",se);
            throw new IllegalStateException(se.getMessage());
        }
		
	}

		
	/**
     * Attaches the specified call party as the "party B" endpoint.
     *
     * @param session - Represents the endpoint to attach.
     * @throws IllegalStateException if there is still a party B attached or
     * if this object was invalidated.
     */
	public void addB(SipSession session) {
         //  System.out.println("Add B : "+session +" To SBB :" + this.name ); 
		if (partyB != null) {
           throw  new IllegalStateException("B-Party already associated");
        }

        /*if (session.getAttribute(SBBOperationContext.ATTRIBUTE_SBB) != null) {
            throw  new IllegalArgumentException("SBB is already assosiated with party-B");
        }*/
        partyB = session;
		partyBId = session.getId();
        partyB.setAttribute(SBBOperationContext.ATTRIBUTE_SBB,name);
		
		try {
			//TODO remove this hardcoding of "SBBServlet"
			partyB.setHandler("SBBServlet");
			SBBSessionActivationListener sbbSessionActvationListener
                       = new SBBSessionActivationListener();
            session.setAttribute(Constants.ATTR_SESSION_ACT_LISTENER,sbbSessionActvationListener);
			//session.setAttribute(Constants.ATTR_SESSION_ACT_LISTENER,"SBBServlet");

		}
		catch(ServletException se) {
			logger.error("Not able to set handler",se);
			throw new IllegalStateException(se.getMessage());
		}
		
	}



	/**
     * Gets the SIP Session object associated with the "Party A" end point
     *
     * @return the Current Sip Session that is used as Party A.
     * NULL if no parties is associated for party A.
     */
	public SipSession getA()  {
		return partyA;
	}


	/**
     * Gets the SIP Session object associated with the "Party B" end point
     *
     * @return the Current Sip Session that is used as Party B.
     * NULL if no parties is associated for party B.
     */
	public SipSession getB() {
		return partyB;
	}

	
	/**
     * This method returns the SipSession objects contained by this SBB.
     * Each SipSession represents a dialog in the call being managed by this
     * SBB.  If no dialogs are currently established, an empty array is
     * returned.
     *
     * @return An array of SipSession objects referenced by this object
     * or an emtpy array if none are referenced.
     */
	public SipSession[] getSessions() {
		int i=0;
		SipSession sessionList[] = new SipSession[2];
		if (partyA != null)  {
			sessionList[i++] = partyA;	
		}
		
		if (partyB != null) {
			sessionList[i] = partyB;
		}
		return sessionList;
	}


	/**
     * This method associates a MessageFilter callback with this object.
     * The folter will be invoked to perform mutations on all SIP message
     * being sent to the network by this object.
     * <p>
     * <pre>
     * Example:
     * <code>
     *  // Obtain instance of SBB...
     *  SBB sbb = ...;
     *
     *  // Create a callback object that will add the header "foo=bar"
     *  // to all SIP messages sent to the network.
     *  MessageFilter filter = new MesssageFilter() {
     *      public void doFilter(SipServletMessage message) {
     *          message.addHeader("foo", "bar");
     *      }
     *  };
     *
     *  // Associate the MessageModifier with the SBB...
     *  sbb.setMessageFilter(filter);
     *
     * </code>
     * </pre>
     * </p>
     *
     * @param modifier - The callback to invoke whenever a message is to be
     * sent to the network.  If null, any existing modifier will be
     * disassociated with this object.
     * @throws IllegalStateException if this object was previously explicitly
     * invalidated.
     */
	public void setMessageFilter(MessageFilter filter) throws IllegalStateException {
		msgFilter = filter;
	}

	/**
     * This method returns any MessageFilter callback currently associated
     * with this object.
     *
     * @return - The MessageModifier associated with this object or NULL if
     * none is currently set.
     */
	public MessageFilter getMessageFilter() {
		return msgFilter;
	}

	public void setIncomingMessageListener(IncomingMessageListener inMessL) {
		inMessListener = inMessL;
	}

	public IncomingMessageListener getIncomingMessageListener() {
		return inMessListener;
	}

	/**
     * Associates the event listener with this SBB.
     * @param listener - Listener to be associated.
     * @throws IllegalStateException if this object is already invalidated.
     */
	public void setEventListener(SBBEventListener listener) throws IllegalStateException {
		this.sbbEventListener = listener;

		if(logger.isDebugEnabled())
    		logger.debug("<SBB>setEventListener "+listener);
		
		String listenerNme=(String)this.appSession.getAttribute(this.getName() + com.baypackets.ase.sbb.util.Constants.SBB_LISTENER_CLASS);
		
		if (this.sbbEventListener != null && listenerNme==null) {
			// Store the name of the listener class in the app session so that it
			// may be reconstructed during session activation on the cluster peer.
			this.appSession.setAttribute(this.getName() + Constants.SBB_LISTENER_CLASS, 
						this.sbbEventListener.getClass().getName());
		}
	}

	
    /**
     * Returns the Event Listener associated with this SBB.
     * @return the Event Listener associated with this SBB.
     */
	public SBBEventListener getEventListener() {
		return sbbEventListener;
	}


	/**
     * This method is invoked from the SipSessionActivationListener.
     * The listener would get the SBB from the application Session
     * and call the activate on the same.
     *
     * <p>
     * This method woud do the following.
     * <pre>
     * a. Get the Sip Session ID
     * b. If the Id equals the partyA's id, then call addA().
     * c. If the Id equals the partyB's id, then call addB().
     * </p>
     * @param session
     */
    public void activate(SipSession session)  {
    	if(logger.isDebugEnabled())
    		logger.debug("<SBB> Entered activate(SipSession) with "+session);
			boolean loggerEnabled = logger.isDebugEnabled();

			if (loggerEnabled) {
				logger.debug("activate() called on SBB with name: " + this.getName());
			}
			
			if (session.getId().equalsIgnoreCase(partyAId)) {
				partyA = session;
				
				if(logger.isDebugEnabled()){
		    		logger.debug("<SBB> found partyA session  "+partyAId);
				}
			}
			if (session.getId().equalsIgnoreCase(partyBId)) {
				partyB = session;
				if(logger.isDebugEnabled()){
		    		logger.debug("<SBB> found partyB session  "+partyBId);
				}
			}

			if (this.getServletContext() == null) {
				if (loggerEnabled) {
					logger.debug("activate(): Associating ServletContext with SBB: " + this.getName());
				}
				this.setServletContext((ServletContext)session.getApplicationSession().getAttribute(com.baypackets.ase.util.Constants.ATTRIBUTE_SERVLET_CONTEXT));
				if (this.getServletContext() == null) {
					logger.error("activate(): Unable to get ServletContext to associate with SBB!");
				}
			}

			if (this.sbbEventListener != null) {
				if (loggerEnabled) {
					logger.debug("activate(): Activating registered event listener...");
				}
				this.sbbEventListener.activate(this);
			}	
		}

	/**
     * This method return the SipApplication session associated with 
	 * the SBB.
     * @return  SipApplicationSession
     */

	public SipApplicationSession getApplicationSession() {
		return appSession;
	}


    /**
     * This Method would be called by the SBB operations (Connect, Dialout, Mute, Resync)
     * to fire an event to the SBB Event listeners registered with SBB.
     * @param event - event to be fired.
     * @return identifier indicating whether to continue with default processing or not.
     */
    public int fireEvent(SBBEvent event) {
    	if(logger.isDebugEnabled())
    		logger.debug("<SBB> firing event "+event +" on SBB "+sbbEventListener);

		if (sbbEventListener == null) {
			return SBBEventListener.CONTINUE;
		}
		return (sbbEventListener.handleEvent(this, event));		
	}
    
    
	/**
     * Returns the SBB Object associated with this operation.
     * @return the SBB Object.
     */
    public SBB getSBB() {
		return this;	
	}


    /**
     * Adds this SBB operation to the list of available SBB operations.
     * @param operation Operation to be handled by the SBB.
     */
    public void addSBBOperation(SBBOperation operation) {
    	if(logger.isDebugEnabled())
    		logger.debug("<SBB> addSBBOperation called with "+operation);
		operations.add(operation);
		operation.setOperationContext(this);
	}


    /**
     * Remove this SBB operation to the list of available SBB operations.
     * @param operation Operation to be handled by the SBB.
     */
    public void removeSBBOperation(SBBOperation operation) {
    	if(logger.isDebugEnabled())
    		logger.debug("<SBB> removeSBBOperation called with "+operation);
        operations.remove(operation);
    }


	/**
     * The implementation of this method may do the following.
     * <pre>
     * a. Iterate over the list all the available operations.
     * b. Checks whether the operation is already completed or not.
     * c. If completed, continue to the next operation.
     * d. Returns the SBBOperation object that returns TRUE for the isMatching method.
     * e. If none of the Operation is matching, and the message is a request other than ACK or CANCEL,
     * f. Creates an instance of type NetworkMessageHandler and returns it.
     * g. Otherwise returns NULL.
     * </pre>
     *
     * @param message The incoming message (REQUEST or RESPONSE)
     * @return the handler for handling this message. NULL if not able to get one.
     */
    public SBBOperation getMatchingSBBOperation(SipServletMessage message) {
		
		SBBOperation matchingOper =  null;
		if(logger.isDebugEnabled())
			logger.debug("entered getMatchingSBBOperation");
		Iterator itr = operations.iterator();
		while(itr.hasNext()) {
			if(logger.isDebugEnabled())
				logger.debug("ArrayList operations has element");
			SBBOperation oper = (SBBOperation)itr.next();
			if(logger.isDebugEnabled())
				logger.debug("Value of oper.isCompleted(): " + oper.isCompleted());
			/*if(oper instanceof MediaServerInfoHandler){
				if(((MediaServerInfoHandler)oper).getOperation()< 5){
					logger.debug(" Value of the operation passed");
					((MediaServerInfoHandler)oper).setCompleted(true);
				}
			}*/
			if (! oper.isCompleted()) {
				if (oper.isMatching(message)) {
					logger.debug("oper matched with message");
					matchingOper =  oper;
					break;
				}
			}
		}
		if(logger.isDebugEnabled())
			logger.debug("exited getMatchingSBBOperation");	
		return matchingOper;
	}

	
	/**
     * Associates the ServletContect with this SBB.
     * @param ctx - Application's servlet context.
     */
	public void setServletContext(ServletContext ctx) {
		servletContext = ctx;
	}	

    /**
     * Returns an associated ServletContect with this SBB.
     * @return ctx - Application's servlet context.
     */

	public ServletContext getServletContext() {
		return servletContext;
	}


	public void writeExternal0(ObjectOutput out) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("writeExternal0() called on SBB with name: " + this.name+" partyAId : "+partyAId +" partyBId : "+partyBId);
		}
		try{
			boolean isPartyAValid = this.partyAId != null;
			boolean isPartyBValid = this.partyBId != null;
	
			out.writeUTF(this.name);
			// write party-A
			out.writeBoolean(isPartyAValid);
			if(isPartyAValid) {
				out.writeUTF(this.partyAId);
			}
			//write party-B
			out.writeBoolean(isPartyBValid);
			if(isPartyBValid) {
				out.writeUTF(this.partyBId);
			}
			out.writeObject(this.attributes);
			if(this.operations != null)	{
				if(logger.isDebugEnabled())
					logger.debug(" In writeExternal0() this.operations: " + this.operations);
			}	else	{
				if(logger.isDebugEnabled())
					logger.debug(" In writeExternal0() this.operations is null");
			}
			out.writeObject(this.operations);
			out.writeObject(this.callbacks);
			out.writeBoolean(this.isSBBValid);
			out.writeObject(this.msgFilter);
			out.writeObject(this.inMessListener);
		}catch(Exception e){
			logger.error("Exception in writeExternal0()....." +e);
		}
	}
	
	public void writeExternal(ObjectOutput out) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("writeExternal() called on SBB with name: " + this.name);
		}
		try{
			this.writeExternal0(out);
			//out.writeObject(this.sbbEventListener);
			if (logger.isDebugEnabled()) {
				logger.debug("writeExternal() completed on SBB with name: " + this.name);
			}
		}catch(Exception e){
			logger.error("Exception in writeExternal()....." +e);
		}
	}
	
	public void readExternal0(ObjectInput in) throws IOException,ClassNotFoundException {
		if (logger.isDebugEnabled()) {
			logger.debug("readExternal0() called on SBB object... ");
		}		
		this.name = (String)in.readUTF();
		// read party-A
		boolean isPartyAValid = in.readBoolean();
		if(isPartyAValid) {
			this.partyAId = (String)in.readUTF();
		}
		// read party-B
		boolean isPartyBValid = in.readBoolean();
		if(isPartyBValid) {
			this.partyBId = (String)in.readUTF();
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("readExternal0() ... partyAId "+partyAId +" partyBId "+partyBId);
		}	
		this.attributes = (Hashtable)in.readObject();
		this.operations = (Vector)in.readObject();
		this.callbacks = (Hashtable)in.readObject();
		this.isSBBValid = in.readBoolean();
		this.msgFilter = (MessageFilter)in.readObject();
		this.inMessListener = (IncomingMessageListener)in.readObject();

		if (operations != null) {
			Iterator iterator = operations.iterator();

			while (iterator.hasNext()) {
				SBBOperation op = (SBBOperation)iterator.next();
				op.setOperationContext(this);
			}
		}else	{
			if(logger.isDebugEnabled())
				logger.debug("readExternal0(): operations is NULL");
		}
	}
	
	public void readExternal(ObjectInput in) throws IOException,ClassNotFoundException {
		if (logger.isDebugEnabled()) {
			logger.debug("readExternal() called on SBB object...");
		}
		this.readExternal0(in);
		//this.sbbEventListener = (SBBEventListener)in.readObject();
		if (logger.isDebugEnabled()) {
			logger.debug("readExternal(): Completed de-serialization of SBB with name: " + this.name);
		}
	}


    /**
     * Returns the application session assosiated with this SBB.
     * @return application session assosiated with this SBB.
     */
    public SipApplicationSession  getApplicationSessin()  {
		return appSession;
	}


    /**
     * Sets the application session assosiated with this SBB.
     */
    public void setApplicationSession(SipApplicationSession session) {
		appSession = session;
	}

    public SBBCallback getCallback(String name) {
    	return (SBBCallback)this.callbacks.get(name);
	}

	public void registerCallback(String name, SBBCallback callback) {
		if(name != null && callback != null){
			this.callbacks.put(name, callback);
		}	
	}

	public void unregisterCallback(String name) {
		if(name != null){
			this.callbacks.remove(name);
		}
	}
	
	public void cancel(Class clazz){
		Iterator it = this.operations.iterator();
		for(;it.hasNext();){
			SBBOperation operation = (SBBOperation)it.next();
			if(clazz.isInstance(operation)){
					operation.cancel();
			}
		}
	}
	
	public Vector getOperations(){
		return operations;
	}
	
	public void setServletTimer(String key,Object value){
		if(this.servletTimerMap==null){
			this.servletTimerMap = new HashMap();
			if(logger.isDebugEnabled()){
				logger.debug("servletTimerMap is null, creating new servlet timer map");
			}
		}
		this.servletTimerMap.put(key, value);
	}
	
	public Object getServletTimer(String key){
		return this.servletTimerMap.remove(key);
	}

	/**
	 This method is added to handle Msml media server case , when stop operation is performed on
	 * a msml media operation , only a single event is received from ms i.e. dialog exit , so the instaed of stopped 
	 * the pay complete/play collect complete is generated to application on stop success and one of operation element
	 * keeps lying in sbb which causes issue on next media operation on this leg . then instaed of play/collect success
	 * the stop operation event is generated .in MSCML for stop peration 2 events are generated oone is stopped and on is completion
	 * of original ms operation so the both operations elements gets removed from operations list
	 * @param removeExistingOpers
	 */
	public boolean stopMediaOperation(boolean removeExistingOpers) {

		if (!operations.isEmpty()) {

			if (removeExistingOpers) {
				if (logger.isDebugEnabled()) {
					logger.debug("stopMediaOperation remove all existing media operations on this dialog for msml media server");
				}
			//commenting this code  because we are not adding stop operation for msml media server  now
			//operations.removeAllElements();
			}
			return true;
		}else{
			if (logger.isDebugEnabled()) {
				logger.debug("stopMediaOperation.. no operation avaiable to be stopped ");
			}
			return false;
		}
		
	}
	
	/**
	 * This method returns String representation of instance of this class
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SBB  : ");
		sb.append(name);
		sb.append(" Party A : ");
		sb.append(partyA);
		sb.append(" Party B : ");
		sb.append(partyB);
		return sb.toString();
	}
	
}
