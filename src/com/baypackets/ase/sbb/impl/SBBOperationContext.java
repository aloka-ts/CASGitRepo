/*
 * Created on Oct 30, 2004
 *
 */
package com.baypackets.ase.sbb.impl;

import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipSession;

import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBCallback;
import com.baypackets.ase.sbb.SBBEvent;

/**
 */
public interface SBBOperationContext {
	
	/**
	 * The Attribute name used in the SIP Session for storing the SBB object.
	 */
	public static final String ATTRIBUTE_SBB = "SBB".intern();

	/**
     * The Attribute name for storing invite's response..
     */
    public static final String ATTRIBUTE_INV_RESP = "OK".intern();

	
	/**
	 * The attribute name used in the SIP Session for stroring the SDP from the 
	 * corresponding end-point.
	 */
	public static final String ATTRIBUTE_SDP = "SDP".intern();

	/**
     * The attribute name used in the SIP Session for stroring the content type of stored SDP.
     */
    public static final String ATTRIBUTE_SDP_CONTENT_TYPE = "SDP_CONTENT_TYPE".intern();

	/**
     * The attribute name used in the SIP Session for stroring the prack.
     */
	public static final String ORIG_PRACK_FROM_PARTY_A ="PRACK_FROM_A";
	
	
	/**
     * The attribute name used for storing the SDP last exchanged with Party-A
     * by Application.
     * This is introduced for handling the race conditions of UAT-745
     */
	public static final String ATTRIBUTE_SDP_PARTY_B ="ATTRIBUTE_SDP_PARTY_B";

	public static final String ATTRIBUTE_SDP_PARTY_B_CONTENT_TYPE ="ATTRIBUTE_SDP_PARTY_B_CONTENT_TYPE";

	
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
	 * </pre>
	 * @param session
	 */
	public void activate(SipSession session);

	/**
	 * This Method would be called by the SBB operations (Connect, Dialout, Mute, Resync)
	 * to fire an event to the SBB Event listeners registered with SBB.
	 * @param event - event to be fired.
	 * @return identifier indicating whether to continue with default processing or not.
	 */
	public int fireEvent(SBBEvent event);
	
	/**
	 * Returns the SBB Object associated with this operation.
	 * @return the SBB Object.
	 */
	public SBB getSBB();
	
	/**
	 * Adds this SBB operation to the list of available SBB operations.
	 * @param operation Operation to be handled by the SBB.
	 */
	public void addSBBOperation(SBBOperation operation);
	
	/**
	 * Removes this SBB operation from the list of available SBB operations.
	 * @param operation Operation to be removed from the SBB's OP list.
	 */
	public void removeSBBOperation(SBBOperation operation);
	
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
	public SBBOperation getMatchingSBBOperation(SipServletMessage message);
	
	public void registerCallback(String name, SBBCallback callback);
	
	public void unregisterCallback(String name);
}
