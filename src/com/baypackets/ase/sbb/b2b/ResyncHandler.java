/*
 * @(#)ResyncHandler.java	1.0 11 July 2005
 *
 */

package com.baypackets.ase.sbb.b2b;

import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.Address;
import javax.servlet.sip.Rel100Exception;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;


import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.baypackets.ase.sbb.SBB;
import com.baypackets.ase.sbb.SBBEvent;
import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.util.Constants;
import com.baypackets.ase.sbb.util.SBBResponseUtil;
import com.baypackets.ase.sbb.impl.BasicSBBOperation;


/**
 * Implementation of the disconnect handler.
 * This class is responsible for handling disconnect operation. 
 * Disconnect operation handles the signalling level details 
 * for disconnecting a  party from a connected call. 
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class ResyncHandler extends OneWayUnmuteHandler {

	private static final long serialVersionUID = 7484327033147L;
	/** Logger element */
    private static Logger logger = Logger.getLogger(ResyncHandler.class.getName());

	boolean isSuccess = false;	

	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public ResyncHandler() {
		super();
	}

	/**
     * This method will be invoked to start disconnect operation.
     * This would be done, when the application invokes an operation on the SBB.
     * (OR) when the SBB Servlet receives an message from network,
     * but there are no handlers available to handle it.
     */
	public void start() throws ProcessMessageException {
		if (logger.isDebugEnabled())
			logger.debug("<SBB>entered start() ");
		
		setUnmuteParty(PARTY_A);
		setOperType(OPER_RESYNC);
		if (logger.isInfoEnabled())
			logger.info("<SBB> Deligating to OneWayUnmuteHandler");
		// deligate to start, to fire reInvite  
		super.start();
		if (logger.isDebugEnabled())
			logger.debug("<SBB>exited start() ");
    }



	public void handleRequest(SipServletRequest request) {
		logger.error("<SBB> Error: Received a request on ResyncHandler. ");
	}

	/**
     * This method handles all response from party issued a reINVITE/Update request 
     *
     * @response - Response .
     */
	public void handleResponse(SipServletResponse response) {
		if (logger.isDebugEnabled())
			logger.debug("<SBB> entered handleResponse with following response :: "+response);

		if (getMuteParty() == PARTY_A) {
			// handler response to reinvite request to A
			isSuccess  = super.handleResponse(response,false);
			if (isSuccess) {
				if (logger.isInfoEnabled())
					logger.info("<SBB> A-party unmuted successfully, going to unmute party-B");
				// OneWayDisconnect operation for Party-B 
				setUnmuteParty(PARTY_B);
				try {
					// initiate reInvite/Update to B
					super.start();
					return;
				}
				catch(ProcessMessageException exp) {
					logger.error(exp.getMessage(),exp);
				}
			}
		}
		// this will response to reInvite/update request to party-B
		if ( isSuccess && getMuteParty() == PARTY_B) {
			handleResponse(response,true);
		}
		if (logger.isDebugEnabled())
			logger.debug("<SBB> exited handleResponse");
    }

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
		this.isSuccess = in.readBoolean();
	}


	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
		out.writeBoolean(this.isSuccess);
	}
}
