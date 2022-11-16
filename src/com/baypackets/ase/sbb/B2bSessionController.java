/*
 * B2bSessionController.java
 * 
 * Created on Jun 17, 2005
 */
package com.baypackets.ase.sbb;

import java.util.List;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.Address;
import javax.servlet.sip.URI;

/**
 * The back-to-back session controller interface (B2bSessionController) defines an 
 * object that is used to establish and control a back-to-back user agent session.
 * 
 * @author BayPackets
 */
public interface B2bSessionController extends SBB {	
	
	/**
	 * This method is invoked to establish a back-to-back user agent session
	 * between two network endpoints. This method requires that the other party 
	 * of this call is already connected and has been added to the SBB. 
	 * <p>
	 * 
	 * <pre>
	 * This method sends out an INVITE to the address specified.
	 * When it receives a provisional response or 2xx final response from the above address,
	 * it sends out a re-INVITE to the other party that is already connected.
	 * When it receives the 2xx final response for the re-INVITE from the other party,
	 * It sends ACK to both the parties involved and sets up the Call.
	 * If everything goes well, it notifies the application that call is CONNECTED else FAILED.
	 * </pre>
	 * 
	 * @param address - The address of the endpoint to dial out to.
	 * @throws ConnectException if an error occurs while dialing out to 
	 * the specified party.
	 * @throws IllegalStateException if this B2bSessionController object is
	 * currently managing an existing session between two endpoints or if
	 * it was explicitly invalidated or if the other party is NULL or not connected yet.
	 * @throws IllegalArgumentException if the given Address is null.
	 */
	public void dialOut(Address from, Address address) throws ConnectException, IllegalStateException, IllegalArgumentException;
			
	/**
	 * This overloaded version of the "dialOut" method handles all aspects of
	 * connecting the two specified parties.
	 * <p>
	 * 
	 * <pre>
	 * In this case, the SBB does the following,
	 * a. Sends OUT an INVITE message to Party A.
	 * b. When it receives a 2xx response with SDP, it sends INVITE to Party B.
	 * c. When it receives 200 from Party B, the SBB sends an ACK to Party A with Party B's SDP.
	 * d. It also sends ACK to Party B.
	 * e. Notifies the application with a CONNECTED event.
	 * f. In case of any failure, the SBB handles the SIP transactions and notifies
	 *    application with a CONNECT_FAILED event. 
	 * </pre>
	 * 
	 * @param from this address will be set in from header of INVITE requests that 
	 * controller sends to partyA and partyB. 
	 * @param partyA - The address of Party A.	 
	 * @param partyB - The address of Party B.
	 * @param autoResponseMode This flag should set true if PartyB is an automata(IVR, etc)
	 * that will answer the call immediately.Default value is false. 
	 * @throws ConnectException if an error occurs while connecting the two
	 * parties.
	 * @throws IllegalStateException if this object is already managing an
	 * existing back-to-back session or if it was explicitly invalidated.
	 * @throws IllegalArgumentException if one of the given Address objects
	 * is null.
	 */
	public void dialOut(Address from, Address partyA, Address partyB, boolean autoResponseMode ) throws ConnectException, IllegalStateException, IllegalArgumentException;
		
	/**
	 * This method connects the originating endpoint and the terminating endpoint (and its address object),
	 * that were specified by the given request, into a back-to-back user agent session.
	 * 
       * <p>
	 * When an initial INVITE request is received by a Servlet from Party A, the Servlet then 
	 * creates an instance of a B2bSessionController object and invokes this method passing it 
       * the given request object and the address of the terminating endpoint, Party B.  
	 * 
	 * <p>
	 * The B2BSessionController takes care of the subsequent SIP messages between
	 * both parties and notifies the application whether the call is CONNECTED or FAILED.
	 * 
	 * If any MessageModifier object is associated with this object, it will be 
	 * invoked by this method to perform any header and/or body manipulations 
	 * to the outbound request before it is sent to the terminating endpoint.
	 * <p>
	 * The following example demonstrates the usage of this method in a 
	 * Servlet:
	 * <pre>
	 * <code>
	 * 	// The Servlet's "doRequest" method...
	 * 	public void doRequest(SipServletRequest request) {
	 * 		// Create the Address of the terminating endpoint to connect.
	 * 		SipFactory factory = (SipFactory)this.getServletContext().getAttribute(SIP_FACTORY);
	 * 		Address address = factory.createAddress(...);
	 * 
	 * 		// Create a B2bSessionController object and connect the two 
	 * 		// endpoints... 
	 * 		B2bSessionController b2b = SBBFactory.getInstance().getB2bSessionController();
	 * 		b2b.connect(request, address);
	 * 		...
	 * 	}
	 * </code>
	 * </pre>
	 * </p>
	 * 
	 * @param request - A SIP INVITE request specifying the originating
	 * network endpoint to connect.
	 * @param address - The address of the terminating endpoint to connect.
	 * If this is null, the request URI in the given request object is used
	 * as the terminating address.
	 * @throws ConnectException if an error occurs while connecting the two 
	 * endpoints.
	 * @throws IllegalStateException if there are any endpoints still 
	 * being managed by this object or if this object was explicitly invalidated.
	 * @throws IllegalArgumentException if the given Address parameter is
	 * null or the specified request is not an INVITE request.
	 */
	public void connect(SipServletRequest request, Address address) throws ConnectException, IllegalStateException, IllegalArgumentException;			

	/**
	 * This overloaded version of the "connect" method takes an additional 
	 * "from" Address argument which will be used as the "from" address of the 
	 * outgoing SIP INVITE request generated by this method.
	 */
	public void connect(SipServletRequest request, Address address, Address from) throws ConnectException, IllegalArgumentException;

	
	public void connectParallel(SipServletRequest request,List<URI> termPartyList, Address from)  throws ConnectException, IllegalStateException, IllegalArgumentException;
	/**
	 * This method disconnects the Party A endpoint from the back-to-back
	 * user agent session.  This will initiate a BYE transaction with 
	 * Party A.  Typically, all the endpoints should be put on hold 
	 * before this method is invoked by calling "hold()".
	 * <br/>
	 * 
	 * @throws DisconnectException if an error occurs while disconnecting the
	 * specified endpoint.
	 * @throws IllegalStateException if no Party A is currently connected or
	 * if this object was explicitly invalidated.
	 */
	public void disconnectA() throws DisconnectException, IllegalStateException;
			
	/**
	 * This method disconnects the Party B endpoint from the back-to-back
	 * session.  This will initiate a BYE transaction with Party B.  
	 * Typically, all the endpoints should be put on hold 
	 * before this method is invoked by calling "hold()".
	 * 
	 * @throws DisconnectException if an error occurs while disconnecting the
	 * specified endpoint.
	 * @throws IllegalStateException if no Party B is currently connected or
	 * if this object was explicitly invalidated.
	 */
	public void disconnectB() throws DisconnectException, IllegalStateException;


    /**
     * This method disconnects both endpoints from the back-to-back
     * session.  This will initiate a BYE transaction with both parties.
     * Typically, all endpoints should be put on hold
     * before this method is invoked by calling "hold()".
     *
     * @throws DisconnectException if an error occurs while disconnecting the
     * specified endpoint.
     * @throws IllegalStateException if no party is currently connected or
     * if this object was explicitly invalidated.
     */
    public void disconnect() throws DisconnectException, IllegalStateException;
	
	/**
     * This method puts the Party A and Party B endpoints of the SBB on HOLD. 
     * Calling this method sends a re-INVITE or UPDATE request (as specified 
     * by the "HOLD_METHOD" attribute)to both the parties involved 
     * with an SDP specifying that no further
     * media should be sent or received. Both the parties can be
     * subsequently resumed from HOLD by calling "resync".  This method would
     * typically be called before disconnecting an endpoint from
     * this back-to-back user agent session.
     *
     * @throws IllegalStateException if either of the party is not currently connected or
     * if this object was explicitly invalidated.
     * This method puts all the parties handled by this SBB on HOLD.
     */
    public void hold() throws IllegalStateException;

	/**
     * This method exchanges the SDP messages between the parties involved in the SBB,
     * using the method defined by the behavioral attribute "HOLD_METHOD".
     *
     * If the partie are already on HOLD, this operation would resume them from HOLD.
     *
     * @throws IllegalStateException if this object was already invalidated
     * or if no call parties are currently being managed by this object.
     */
    public void resync() throws IllegalStateException;

    /**
     * This method cancels the ongoing CONNECT operation.
     * If the application has called any of the connect() methods, and it has not
     * yet received any event (CONNECTED or CONNECT_FAILED), then calling this method
     * would cancel the current CONNECT operation.
     * 
     * If there are no connect operation is in-progresss, this method would simply return.
     */
    public void cancelConnect();
    
    /**
     * This method cancels the ongoing DIALOUT operation.
     * If the application has called any of the dialOut methods, and it has not
     * yet received any event (CONNECTED or CONNECT_FAILED), then calling this method
     * would cancel the current DIALOUT operation.
     *
     * If there are no dialOut operation is in-progresss, this method would simply return.
     */
    public void cancelDialout();
    
	/**
	 * Sets the NO-ANSWER timeout in Seconds for the endpoint.
	 * If this value is not specified explicitly, default value 60 Second will be used. 
	 * It represent the maximum time SBB will wait for final response,
	 * before sending CANCEL to the endpoint. 
	 * This value should be set before invoking dialOut() method.
	 * @param timeout Timeout for NO-ANSWER in Seconds.
	 */
	public void setNoAnswerTimeout(int timeout);

	/**
  	 * Gets the current timeout value set.
	 * @return Timeout value in seconds.
	 */
					
	public int getNoAnswerTimeout();
	
}
