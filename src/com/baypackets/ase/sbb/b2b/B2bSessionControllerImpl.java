/*
 * B2bSessionController.java
 * 
 * Created on July 7, 2005
 */
package com.baypackets.ase.sbb.b2b;


import java.util.List;

import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.URI;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.B2bSessionController;
import com.baypackets.ase.sbb.ConnectException;
import com.baypackets.ase.sbb.DisconnectException;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.impl.SBBImpl;
import com.baypackets.ase.sbb.util.Constants;
	

/**
 * This interface defines an object that is used to establish and controll a 
 * back-to-back user agent session.
 * 
 * @author Baypackets
 */
public class  B2bSessionControllerImpl  extends SBBImpl implements  B2bSessionController {	

	private static final long serialVersionUID = 51848884324397411L;
	/** Logger element */
    private static Logger logger = Logger.getLogger(B2bSessionControllerImpl.class.getName());

	private int noAnswerTimeout = 60;

   /**
	 * This method is invoked to establish a back-to-back user agent session
	 * between two network endpoints. This method requires that other party 
	 * of this call should be already connected and should have been added to the SBB
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
	 * @param partyA  - party-A session.
	 * @throws ConnectException if an error occurs while dialing out to 
	 * the specified party.
	 * @throws IllegalStateException if this B2bSessionController object is
	 * currently managing an existing session between two endpoints or if
	 * it was explicitly invalidated or if the other party is NULL or not connected yet.
	 * @throws IllegalArgumentException if the given Address is null.
	 */
	public void dialOut(Address from, Address address) throws ConnectException, 
						IllegalStateException, IllegalArgumentException {
        if (!isValid() ) {
            throw new IllegalStateException(" SBB is not valid");
        }

		if (logger.isDebugEnabled())
			logger.debug("<SBB>entered dialOut(Address)");

		if (address == null) {
			logger.error("<SBB> Supplied argument is null");
			throw new IllegalArgumentException("Supplied argument is null");	
		}


        OneWayDialoutHandler  handler  = new OneWayDialoutHandler(from, address);
		handler.setNoAnswerTimeout(this.noAnswerTimeout);

        // setting connect as one of the current operations in SBB
        addSBBOperation(handler);

        // start the connect operation handling
		if (logger.isDebugEnabled())
			logger.debug("<SBB>Starting dialout operation");
        try {

            handler.start();
        }
        catch(ProcessMessageException pme) {
            logger.error(pme.getMessage(),pme);
            throw new ConnectException(pme.getMessage());
        }  
		if (logger.isDebugEnabled())
			logger.debug("exited dialOut(Address)");

	}
			
	/**
	 * This overloaded version of the "dialOut" method handles all aspects of
	 * connecting the two specified parties.
	 * <p>
	 * 
	 * <pre>
	 * In this case, the SBB does the following,
	 * a. Sends OUT an INVITE message to party A.
	 * c. When it receives 200 from party B, the SBB sends an ACK to party A with B's SDP.
	 * d. It also sends ACK to party B.
	 * e. Notifies the application with a CONNECTED event.
	 * f. In case of any failure, the SBB handles the SIP transactions and notifies
	 *    application with a CONNECT_FAILED event. 
	 * </pre>
	 * 
	 * @param from this address will be set in from header of INVITE requests that
   * controller sends to partyA and partyB.
	 * @param partyA - The address of party A.
	 * @param partyB - The address of party B.
	 * @param autoResponseMode This flag should set true if PartyB is an automata
   * that will answer the call immediately.Default value is false.
	 * @throws ConnectException if an error occurs while connecting the two
	 * parties.
	 * @throws IllegalStateException if this object is already managing an
	 * existing back-to-back session or if it was explicitly invalidated.
	 * @throws IllegalArgumentException if one of the given Address objects
	 * is null.
	 */
	public void dialOut(Address from, Address partyA, Address partyB, boolean autoResponseMode ) throws ConnectException, 
					IllegalStateException, IllegalArgumentException {

        if (!isValid() ) {
            throw new IllegalStateException(" SBB is not valid");
        }

		if (logger.isDebugEnabled())
			logger.debug("<SBB>entered dialOut(Address,Address) with autoResponseMode flag : " + autoResponseMode );

		 if (partyA  == null || partyB == null) {
            logger.error("<SBB> Supplied argument is null");
            throw new IllegalArgumentException("Supplied argument is null");
        }


        DialoutHandler  handler  = new DialoutHandler(from, partyA,partyB, autoResponseMode);
		handler.setNoAnswerTimeout(this.noAnswerTimeout);

        // setting dialout as one of the current operations in SBB
        addSBBOperation(handler);

        // start the connect operation handling
		if (logger.isDebugEnabled())
			logger.debug("<SBB>Starting dialout operation");
        try {

            handler.start();
        }
        catch(ProcessMessageException pme) {
            logger.error(pme.getMessage(),pme);
            throw new ConnectException(pme.getMessage());
        } 
		if (logger.isDebugEnabled())
			logger.debug("exited dialOut(Address)");

	}
	
	
	public void connect(SipServletRequest request, Address bPartyAddress) throws ConnectException, IllegalArgumentException, IllegalStateException {
		this.connect(request, bPartyAddress, null);
	}
	
	/**
	 * This method connects the originating and terminating endpoints 
	 * specified by the given request and address objects into a back-to-back
	 * user agent session.
	 * <br/>
	 * 
	 * When an initial INVITE request is received by a Servlet from "party A",
	 * The Servlet then creates an instance of a B2bSessionController object and invokes this method 
	 * passing it the given request object and the address of the terminating
	 * endpoint, "party B".  
	 * 
	 * <p>
	 * The B2BSessionController takes care of the subsequent SIP messages between
	 * both the parties and notifies the application whether the call is CONNECTED or FAILED.
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
	 * 		// Create the Address of the terminiating endpoint to connect.
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
	 * being managed this object or if this object was explicitly invalidated.
	 * @throws IllegalArgumentException if the given Address parameter is
	 * null or the specified request is not an INVITE request.
	 */
	public void connect(SipServletRequest request, Address bPartyAddress, Address from) throws ConnectException, IllegalStateException, IllegalArgumentException	 {
		if (logger.isDebugEnabled())
			logger.debug("<SBB>entered connect(SipServletRequest,Address)");



        if (!isValid() ) {
            throw new IllegalStateException(" SBB is not valid");
        }


		if (request == null || bPartyAddress  == null) {
            logger.error("<SBB> Supplied argument is null");
            throw new IllegalArgumentException("Supplied argument is null");
        }


		// Check if request is INVITE
		if (! request.getMethod().equalsIgnoreCase(Constants.METHOD_INVITE)) {
			logger.error("Illegal argument: Non Invite request");
            throw new IllegalArgumentException("Illegal argument: Non Invite request");
		}
	
		// Check if supplied invite request is initial
		if (! request.isInitial()) {
            logger.error("Illegal argument: Non Initial request");
            throw new IllegalArgumentException("Illegal argument: Non Initial request");
        }

		ConnectHandler connectHandler = null;

		if (from == null) {
			connectHandler = new EarlyMediaConnectHandler(request,bPartyAddress);	
		} else {
			connectHandler = new EarlyMediaConnectHandler(request, bPartyAddress, from);
		}
		// setting connect as one of the current operations in SBB
		addSBBOperation(connectHandler);
		
		// start the connect operation handling
		if (logger.isDebugEnabled())
			logger.debug("<SBB>Starting connect operation");
		try {	
			connectHandler.start();
		}
		catch(ProcessMessageException pme) {
			logger.error(pme.getMessage(),pme);
			throw new ConnectException(pme.getMessage());
		}	
		if (logger.isDebugEnabled())
			logger.debug("exited connect(SipServletRequest,Address)");
	}		
	
	
	public void connectParallel(SipServletRequest request,List<URI> termPartyList, Address from)  throws ConnectException, IllegalStateException, IllegalArgumentException {
		
		if (logger.isDebugEnabled())
			logger.debug("<SBB>entered connectParallel(SipServletRequest,Address)");

        if (!isValid() ) {
            throw new IllegalStateException(" SBB is not valid");
        }


		if (request == null || termPartyList  == null || termPartyList.isEmpty()) {
            logger.error("<SBB> Supplied argument is null");
            throw new IllegalArgumentException("Supplied argument is null");
        }
		
		// Check if supplied invite request is initial
		if (! request.isInitial()) {
			logger.error("Illegal argument: Non Initial request");
		    throw new IllegalArgumentException("Illegal argument: Non Initial request");
		}
		
		ConnectHandler connectHandler = null;

		if (from == null) {
			connectHandler = new ConnectParallelHandler(request, termPartyList);
		} else {
			connectHandler = new ConnectParallelHandler(request, termPartyList, from);
		}
		// setting connect as one of the current operations in SBB
		addSBBOperation(connectHandler);
		
		// start the connect operation handling
		if (logger.isDebugEnabled())
			logger.debug("<SBB>Starting connect operation");
		try {	
			connectHandler.start();
		}
		catch(ProcessMessageException pme) {
			logger.error(pme.getMessage(),pme);
			throw new ConnectException(pme.getMessage());
		}	
		if (logger.isDebugEnabled())
			logger.debug("exited connect(SipServletRequest,Address)");

		
	}

	/**
	 * This method disconnects the "party A" endpoint from the back-to-back
	 * user agent session.  This will initiate a BYE transaction with 
	 * "party A".  Typically, all the endpoints should be put on mute 
	 * before this method is invoked by calling "mute()".
	 * <br/>
	 * 
	 * @throws DisconnectException if an error occurs while disconnecting the
	 * specified endpoint.
	 * @throws IllegalStateException if no "party A" is currently connected or
	 * if this object was explicitly invalidated.
	 */
	public void disconnectA() throws DisconnectException, IllegalStateException {
		if (logger.isDebugEnabled())
			logger.debug("Entered disconnectA");

		if (getA() == null ) {
			logger.error("Called disconnect method on disconnected party");
			throw new IllegalStateException("A party is not conncted ");
		}
		OneWayDisconnectHandler handler = new OneWayDisconnectHandler();
		
		// set party A
		handler.setDisconnectParty(OneWayDisconnectHandler.PARTY_A);
		
        // setting disconnect as one of the current operations in SBB
        addSBBOperation(handler);

        // start the disconnect operation handling
		if (logger.isDebugEnabled())
			logger.debug("<SBB>Starting disconnect operation");
		try {
        	handler.start();
		}
		catch(ProcessMessageException pme) {
            logger.error(pme.getMessage(),pme);
            throw new DisconnectException(pme.getMessage());
        }
		if (logger.isInfoEnabled())
			logger.info("<SBB> Disconnect (A) operation started");
		if (logger.isDebugEnabled())
			logger.debug("exited disconnect()");
	}


			
	/**
	 * This method disconnects the "party B" endpoint from the back-to-back
	 * session.  This will initiate a BYE transaction with "party B".  
	 * Typically, all the endpoints should be put on mute 
	 * before this method is invoked by calling "mute()".
	 * 
	 * @throws DisconnectException if an error occurs while disconnecting the
	 * specified endpoint.
	 * @throws IllegalStateException if no "party B" is currently connected or
	 * if this object was explicitly invalidated.
	 */
	public void disconnectB() throws DisconnectException, IllegalStateException {

		if (logger.isDebugEnabled())
			logger.debug("Entered disconnectB()");
		
        if (getB() == null) {
            logger.error("Called disconnect method on disconnected party");
            throw new IllegalStateException("B party is not conncted ");
        }
        OneWayDisconnectHandler oneWayDisconnectHandler = new OneWayDisconnectHandler();

        // set party B
        oneWayDisconnectHandler.setDisconnectParty(OneWayDisconnectHandler.PARTY_B);

        // setting disconnect as one of the current operations in SBB
        addSBBOperation(oneWayDisconnectHandler);

        // start the disconnect operation handling
		if (logger.isDebugEnabled())
			logger.debug("<SBB>Starting disconnect operation");
		try {
        	oneWayDisconnectHandler.start();
		}
		catch(ProcessMessageException pme) {
            logger.error(pme.getMessage(),pme);
            throw new DisconnectException(pme.getMessage());
        }
		if (logger.isInfoEnabled())
			logger.info("<SBB> Disconnect (B) operation started");
		if (logger.isDebugEnabled())
			logger.debug("exited disconnectB()");

	}



    /**
     * This method disconnects the both endpoint from the back-to-back
     * session.  This will initiate a BYE transaction with both parties.
     * Typically, all the endpoints should be put on mute
     * before this method is invoked by calling "mute()".
     *
     * @throws DisconnectException if an error occurs while disconnecting the
     * specified endpoint.
     * @throws IllegalStateException if no party is currently connected or
     * if this object was explicitly invalidated.
     */
	public void disconnect() throws DisconnectException, IllegalStateException {


		if (getA() == null) {
            logger.error("Invalid call to disconnect method as no A-Party is assocated with SBB");
            throw new IllegalStateException("A party is not associated with SBB");
        }
		
		if (getB() == null) {
            logger.error("Invalid call to disconnect method as no B-Party is assocated with SBB");
            throw new IllegalStateException("B party is not associated with SBB");
        }

		DisconnectHandler handler = new DisconnectHandler();		
		// setting disconnect as one of the current operations in SBB
        addSBBOperation(handler);

		try {
			handler.start();
		}
		catch(ProcessMessageException pme)	{
			logger.error("<SBB> Exeption in start ",pme);
			throw new DisconnectException(pme.getMessage());
		}
	}

	/**
     * This method exchanges the SDP messages between the parties involved in the SBB,
     * using the method defined by the behavioural attribute "MUTE_METHOD".
     *
     * If any of the party is already muted, this operation would un-mute that party.
     *
     * @throws IllegalStateException if this object was already invalidated
     * or if no call parties are currently being managed by this object.
     */
    public void resync() throws IllegalStateException {
		
		HoldHandler handler = new HoldHandler();
		handler.setOperation(HoldHandler.OPERATION_RESYNC);
        addSBBOperation(handler);

        // start the resync  operation handling
		if (logger.isDebugEnabled())
			logger.debug("<SBB>Starting resync  operation");
        try {
            handler.start();
        }
        catch(ProcessMessageException pme) {
            logger.error(pme.getMessage(),pme);
            throw new IllegalStateException(pme.getMessage());
        }
		if (logger.isDebugEnabled())
			logger.debug("exited resync operation");
    }

    /**
     * This method puts all the parties handled by this SBB on HOLD.
     *
     * @throws IllegalStateException if this object was already invalidated
     * or if no call parties are currently being managed by this object.
     */
    public void hold() throws IllegalStateException {
       	HoldHandler handler = new HoldHandler();
       	handler.setOperation(HoldHandler.OPERATION_HOLD);
    	addSBBOperation(handler);

        // start the mutA  operation handling
		if (logger.isDebugEnabled())
			logger.debug("<SBB>Starting hold operation");
        try {
            handler.start();
        }
        catch(ProcessMessageException pme) {
            logger.error(pme.getMessage(),pme);
            throw new IllegalStateException(pme.getMessage());
        }
		if (logger.isDebugEnabled())
			logger.debug("exited hold operation)");
    }
	
    public void cancelConnect() {
		super.cancel(ConnectHandler.class);
	}

	public void cancelDialout() {
		super.cancel(OneWayDialoutHandler.class);
		super.cancel(DialoutHandler.class);
	}

	public void setNoAnswerTimeout(int timeout) {
		this.noAnswerTimeout = timeout;
	}

	public int getNoAnswerTimeout() {
		return this.noAnswerTimeout;
	}
}
