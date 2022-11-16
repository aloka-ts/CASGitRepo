/*
 * SBB.java
 * 
 * Created on Jun 17, 2005
 */
package com.baypackets.ase.sbb;

import javax.servlet.ServletContext;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipApplicationSession;

/**
 * The SBB (Service Building Block) interface is implemented by all service building blocks.  
 * A service building block is a utility class used by a SIP Servlet application to 
 * handle the signalling involved in controlling a specific type of call.
 * These calls can include back-to-back user agent sessions, IVR interactions, 
 * conference calls, and more. 
 * 
 * @author BayPackets
 */
public interface SBB {
	
	/**
	 * If this behavioral attribute is set to "true", redirect responses
	 * received for a dialog being established by this SBB object are 
	 * processed recursively.  Otherwise, only the party specified in the first
	 * redirect response is invited.  If a subsequent redirect response is
	 * received for that invite, an error event is dispatched to the application.
	 * 
	 * @see #setAttribute(String, Object)
	 */
	public static final String RECURSIVE_REDIRECT = "RECURSIVE_REDIRECT";
		
	/**
	 * Behavioral attribute to specify whether or not to use RTP_TUNNELLING
	 * @see #setAttribute(String, Object)
	 */
	public static final String RTP_TUNNELLING = "RTP_TUNNELLING";
	
	/**
	 * Response code which needs to be relay.
	 * value "default" will be used to send the same response which is received
	 * value other than "default" will be used to send the provided response code.
	 */
	public static final String RTP_TUNNELLING_18X_CODE = "RTP_TUNNELLING_18X_CODE";
	
	/**
	 * If set true 2xx response will be sent to A party along with 18x response
	 * if media server is connected in early media mode
	 */
	public static final String RELAY_2XX_IN_EARLY_MEDIA = "RELAY_2XX_IN_EARLY_MEDIA";
	
	/**
	 * A party can be put on HOLD by modifying any of the following attributes in
	 * the SDP message sending it to this UA.
	 * <pre>
	 * 1. Setting the IP address in the SDP message to "0.0.0.0"
	 * 2. Setting the a=INACTIVE in the SDP message.
	 * 3. Using both the approaches mentioned above.
	 * </pre>
	 * 
	 * Setting the value of "1" or "2" or "3" for this attribute would cause the SBB to use
	 * the respective approach defined above.The value specified should be an integer.  
	 */
	public static final String HOLD_TYPE = "HOLD_TYPE";
	
	/**
	 * A party can be put on HOLD and resumed from HOLD, 
	 * by sending the SDP message using the INVITE method or the UPDATE method.
	 * 
	 * Setting the value of "INVITE" or "UPDATE" causes the SBB to use the
	 * respective method for the HOLD/RESYNC operation.
	 */
	public static final String HOLD_METHOD = "HOLD_METHOD";
	
	/**
	 * The behavioural attribute in SBB specifying whether to start the 
	 * HOLD, RESYNC operations by sending out the re-INVITE or UPDATE to the
	 * endpoint A or to the endpoint B.
	 */
	public static final String DIRECTION = "DIRECTION".intern();
	
	/**
	 * Direction attribute value specifying that the operation should be
	 * started by first sending out the message to endpoint A and then to B.
	 * Its value should be specified as e.g SBB.DIRECTION_A_TO_B for attribute
	 * SBB.DIRECTION
	 */
	public static final String DIRECTION_A_TO_B = "A_TO_B".intern();
	
	/**
	 * Direction attribute value specifying that the operation should be
	 * started by first sending out the message to endpoint B and then to A.
	 * Its value should be specified as e.g SBB.DIRECTION_B_TO_A for attribute
	 * SBB.DIRECTION
	 */
	public static final String DIRECTION_B_TO_A = "B_TO_A".intern();
	
	/**
	 * Behavioural attribute to specify whether to support early media or not.
	 * If this attribute is set to "true", then the SBB may generate a 
	 * EVENT_EARLY_MEDIA to the application before the EVENT_CONNECTED is 
	 * generated.
	 */
	public static final String EARLY_MEDIA = "EARLY_MEDIA";


	/**
	 * Returns the name of this SBB.  
	 * @return Name of this SBB object.
	 */
	public String getName();

	/**
	 * Sets the name of this SBB. 
	 * The application writer must ensure that this name is unique across 
         * the SBBs available in the Application Session. 
	 */
	public void setName(String name);
	
	/**
	 * This method sets a behavioral attribute on this object.  The value of a
	 * particular attribute determines how this object will behave when 
	 * handling a specific aspect of the call being managed by this object.  
	 * The behavioral attribute names and values are enumerated as public static 
	 * constants of this interface while those that are specific to a particular
	 * type of call are enumerated in the corresponding SBB interface.
	 * For example, to enable RTP tunneling to propagate a
	 * callee's ring tone to the caller during setup of a back-to-back 
	 * user agent session, set the following attribute:
	 * <p>
	 * <pre>
	 * <code>
	 * 	SBB sbb = ...;
	 * 	sbb.setAttribute(RTP_TUNNELING, new Integer(1));
	 * </code>
	 * </pre>
	 * </p>     
	 * 
	 * @throws IllegalArgumentException if the attribute specified is not 
	 * supported by this object.
	 * @throws IllegalStateException if this object was previously invalidated.
	 */
	public void setAttribute(String name, Object value) throws IllegalArgumentException, IllegalStateException;
	
	/**
	 * This method returns the value of the specified behavioral attribute.
	 * 
	 * @return The value of the specified attribute or NULL if the attribute
	 * is not currently set. 
	 * @throws IllegalStateException if this object was previously invalidated.
	 */
	public Object getAttribute(String name) throws IllegalStateException;
		
	/**
	 * This method returns the names of the behavioral attributes that are 
	 * currently set on this object.
	 * 
	 * @return The names of the set attributes or an empty array if no
	 * attributes are currently set. 
	 */
	public String[] getAttributeNames();
		
	/**
	 * This method returns the names of the behavioral attributes that are
	 * supported but not necessarily set on this object.
	 * 
	 * @return The names of this object's supported attributes or an empty
	 * array if none are applicable.
	 */
	public String[] getSupportedAttributes();
	
	/**
	 * This method is invoked to invalidate this SBB object.  Any SipSessions
	 * referenced by this object will be invalidated and also released from this SBB.
	 * Any subsequent operation or mutator method invoked on this object 
	 * will result in an IllegalStateException being thrown.
	 * 
	 * @throws IllegalStateException if this object was already invalidated.
	 */
	public void invalidate() throws IllegalStateException;
	
	/**
	 * Returns whether are not this SBB is in valid state.
	 * @return True if not invalidated else false.
	 */
	public boolean isValid();
			

	/**
	 * Removes the SipSession representing the Party A endpoint from this
	 * SBB object.
	 * 
	 * @throws IllegalStateException if there currently is no Party A endpoint
	 * attached or if this object was explicitly invalidated or if the SBB is 
       * currently handling an operation.
	 */
	public SipSession removeA();

	/**
	 * Removes the SipSession representing the Party B endpoint from this
	 * SBB object.
	 * 
	 * @throws IllegalStateException if there currently is no Party B endpoint
	 * attached or if this object was explicitly invalidated or if the SBB is
       * currently handling an operation.
	 */
	public SipSession removeB();

	/**
	 * Attaches the specified call party as the Party A endpoint.
	 * 
	 * @param session - Represents the endpoint to attach.
	 * @throws IllegalStateException if there is still a Party A attached or
	 * if this object was invalidated.
	 */
	public void addA(SipSession session);

	/**
	 * Attaches the specified call party as the Party B endpoint.
	 * 
	 * @param session - Represents the endpoint to attach.
	 * @throws IllegalStateException if there is still a Party B attached or
	 * if this object was invalidated.
	 */
	public void addB(SipSession session);

	/**
	 * Gets the SIP Session object associated with the Party A endpoint.
	 *  
	 * @return The Current Sip Session that is used as Party A. 
	 * NULL if no parties are associated with Party A. 
	 */
	public SipSession getA();

	/**
	 * Gets the SIP Session object associated with the Party B endpoint.
	 *  
	 * @return The Current Sip Session that is used as Party B. 
	 * NULL if no parties are associated with Party B. 
	 */
	public SipSession getB();
	
	/**
	 * This method returns the SipSession objects contained by this SBB.
	 * Each SipSession represents a dialog in the call being managed by this
	 * SBB. If no dialogs are currently established, an empty array is
	 * returned.
	 * 
	 * @return An array of SipSession objects referenced by this object
	 * or an empty array if none are referenced.
	 */
	public SipSession[] getSessions();
		
	/**
	 * This method associates a MessageFilter callback with this object.
	 * The modifier will be invoked to perform mutations on all SIP messages 
	 * being sent to the network by this object.
	 * <p>
	 * <pre>
	 * Example:
	 * <code>
	 * 	// Obtain instance of SBB...
	 * 	SBB sbb = ...;
	 * 
	 * 	// Create a callback object that will add the header "foo=bar" 
	 * 	// to all SIP messages sent to the network.
	 * 	MessageFilter filter = new MesssageFilter() {
	 * 		public void doFilter(SipServletMessage message) {
	 * 			message.addHeader("foo", "bar");
	 * 		}
	 * 	};
	 * 
	 * 	// Associate the MessageFileter with the SBB...
	 * 	sbb.setMessageFilter(filter);
	 * 
	 * </code>
	 * </pre>
	 * </p>
	 * 
	 * @param filter - The callback to invoke whenever a message is to be
	 * sent to the network.  If null, any existing modifier will be 
	 * disassociated with this object.
	 * @throws IllegalStateException if this object was previously explicitly
	 * invalidated.
	 */
	public void setMessageFilter(MessageFilter filter) throws IllegalStateException;


	/**
	 * This method associates a IncomingMessageListener callback with this object.
	 * Upon receipt of new Incoming message, SBB will send a notification to the Application.
	 */
	public void setIncomingMessageListener(IncomingMessageListener listener);
	
	/**
	 *This method returns any IncomingMessageListener allback currently associated
	 *with this object.
	 *
	 *@return The IncomingMessageListener associated with this object or NULL if
	 * none is currently set.
	 */
	 
	public IncomingMessageListener getIncomingMessageListener();
			
	/**
	 * This method returns any MessageModifier callback currently associated
	 * with this object.
	 * 
	 * @return The MessageFilter associated with this object or NULL if
	 * none is currently set.
	 */
	public MessageFilter getMessageFilter();
	
	/**
	 * Associates the event listener with this SBB.
	 * @param listener - Listener to be associated.
	 * @throws IllegalStateException if this object is already invalidated.
	 */
	public void setEventListener(SBBEventListener listener) throws IllegalStateException;
	
	/**
	 * Returns the Event Listener associated with this SBB.
	 * @return The Event Listener associated with this SBB.
	 */
	public SBBEventListener getEventListener();


	/**
     * Associates the servlet context with this SBB.
     * @param ctx - Application's servlet context.
     */
	public void setServletContext(ServletContext ctx);



	/**
     * Returns the servlet context associated with this SBB.
     * @return The servlet context associated with this SBB.
     */
    public ServletContext getServletContext();


    /**
     * Returns the application session associated with this SBB.
     * @return The application session associated with this SBB.
     */
    public SipApplicationSession  getApplicationSession() ;

    /**
     * Sets the application session associated with this SBB.
     */
    public void setApplicationSession(SipApplicationSession session);
    
    /**
     * Returns the callback object specified by the name.
     * @param name Fully qualified class name of the SBBCallback interface
     * @return The Callback object or NULL if the SBB 
     * 	is not in a state to return the specified callback object.
     */
    public SBBCallback getCallback(String name);
}
