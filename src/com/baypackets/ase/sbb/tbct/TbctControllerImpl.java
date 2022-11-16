/*
 * TbctControllerImpl.java
 * 
 * Created on Aug 17, 2005
 */
package com.baypackets.ase.sbb.tbct;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipSession;

import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.TbctController;
import com.baypackets.ase.sbb.ConnectException;
import com.baypackets.ase.sbb.DisconnectException;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.SBBEventListener;
import com.baypackets.ase.sbb.impl.SBBImpl;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;
import com.baypackets.ase.sbb.util.Constants;

import com.baypackets.ase.sbb.b2b.OneWayDialoutHandler;
import com.baypackets.ase.sbb.b2b.DisconnectHandler;
import com.baypackets.ase.sbb.b2b.OneWayDisconnectHandler;

/**
 * This class implements interfaces <code>TbctController</code> and
 * <code>MessageFilter</code>. It is used to connect an existing call
 * leg to an address (both terminating on same gateway) using TBCT.
 * 
 * @author BayPackets
 */
public class TbctControllerImpl extends SBBImpl implements TbctController{

	private static Logger m_logger = Logger.getLogger(TbctControllerImpl.class);
	private static final long serialVersionUID = -912924397411478251L;
	private boolean m_failureNotified = false;

	/**
	 * Constructor.
	 */
	public TbctControllerImpl() {
		super();
	}
	
	/**
	 * This method is invoked to connect an existing call leg to an address.
	 * This method requires that A-party of this call is already connected.
	 * <p>
	 * 
	 * <pre>
	 * This method sends out an INVITE to specified B-party address. When it
	 * receives the 2xx final response for the INVITE, it sends ACK for the
	 * same and B-party call leg is established.
	 *
	 * It then send a REFER request to gateway to interconnect both A-party
	 * and B-party call legs and get itself out of the call path. All incoming
	 * NOTIFY requests are responded with 200 OK.
	 *
	 * If everything goes well, it notifies the application that TBCT is
	 * successful. If TBCT did not go through, application is notified that
	 * TBCT is failed.
	 * </pre>
	 * 
	 * @param sessionA - The <code>SipSession</code> object corresponding to
	 * established call leg with A-party.
	 * @param addressB - The address of the B-party to dial out to.
	 *
	 * @throws IllegalArgumentException if sessionA or addressB is null.
	 * @throws IllegalStateException if sessionA is not in CONFIRMED state
	 * @throws ConnectException if an error occurs while performing TBCT.
	 */

	public void connect(SipSession sessionA, Address addressB)
		throws ConnectException, IllegalStateException, IllegalArgumentException {
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("connect(SipSession, Address): enter");

		//
		// Verify arguments
		//
		if(sessionA == null) {
			m_logger.error("connect: Argument sessionA is null");
			throw new IllegalArgumentException("Argument sessionA is null");
		}

		if(addressB == null) {
			m_logger.error("connect: Argument addressB is null");
			throw new IllegalArgumentException("Argument addressB is null");
		}

		//
		// Verify states
		//
		int stateA = ((Integer)sessionA.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
		if(stateA != Constants.STATE_CONFIRMED) {
			m_logger.error("sessionA is not in CONFIRMED state");
			throw new IllegalStateException("sessionA is not in CONFIRMED state");
		}

		// Add partyA
		this.addA(sessionA);

		//
		// Dialout to B-party
		//
		OneWayDialoutHandler bPtyConnHandler = new OneWayDialoutHandler(addressB);
		//bPtyConnHandler.setReInviteType(OneWayDialoutHandler.NO_REINVITE);
		this.addSBBOperation(bPtyConnHandler);
		bPtyConnHandler.setOperationContext(this);
		try {
			bPtyConnHandler.start();
		} catch(ProcessMessageException pme) {
			m_logger.error("connect: Starting dialout to B", pme);
			throw new ConnectException(pme.getMessage());
		}
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("connect(SipSession, Address): exit");
	}

	public void transfer(SipSession sessionA, SipSession sessionB)
		throws ConnectException, IllegalStateException, IllegalArgumentException {
		if(m_logger.isDebugEnabled()) 
			m_logger.debug("transfer(SipSession, SipSession): enter");

		//
		// Verify arguments
		//
		if(sessionA == null) {
			m_logger.error("transfer: Argument sessionA is null");
			throw new IllegalArgumentException("Argument sessionA is null");
		}
		if(sessionB == null) {
			m_logger.error("transfer: Argument sessionB is null");
			throw new IllegalArgumentException("Argument sessionB is null");
		}

		//
		// Verify states
		//
		int stateA = ((Integer)sessionA.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
		if(stateA != Constants.STATE_CONFIRMED) {
			m_logger.error("sessionA is not in CONFIRMED state");
			throw new IllegalStateException("sessionA is not in CONFIRMED state");
		}

		int stateB = ((Integer)sessionB.getAttribute(Constants.ATTRIBUTE_DIALOG_STATE)).intValue();
		if(stateB != Constants.STATE_CONFIRMED) {
			m_logger.error("sessionB is not in CONFIRMED state");
			throw new IllegalStateException("sessionB is not in CONFIRMED state");
		}

		// Add partyA
		this.addA(sessionA);
		
		// Add partyB
		this.addB(sessionB);

		//Now do the transfer....
		this.transfer();
	}

	private void transfer(){
		TbctHandler handler = new TbctHandler();
		this.addSBBOperation(handler);
		handler.setOperationContext(this);
		try {
			handler.start();
		} catch(ProcessMessageException pme) {
			m_logger.error("fireEvent: Starting TBCT", pme);
		}
	}

	/**
	 * This Method would be called by the SBB operations (Connect, Dialout, Mute, Resync)
	 * to fire an event to the SBB Event listeners registered with SBB.
	 * @param event - event to be fired.
	 * @return identifier indicating whether to continue with default processing or not.
	 */
	public int fireEvent(SBBEvent event) {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("fireEvent: entering with event : " + event.getEventId());
		}

		if(event.getEventId().equals(SBBEvent.EVENT_CONNECTED)) {
			//
			// B-party connected, now start TBCT part
			//
			this.transfer();

			// Calling handler need not do anything
			if(m_logger.isDebugEnabled()) 
				m_logger.debug("fireEvent: exit");
			return SBBEventListener.NOOP;
		}

		return super.fireEvent(event);
	}
}
