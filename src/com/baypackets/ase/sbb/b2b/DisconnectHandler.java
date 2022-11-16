/*
 * @(#)DisconnectHandler.java	1.0 11 July 2005
 *
 */

package com.baypackets.ase.sbb.b2b;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;
import org.apache.log4j.Logger;

import com.baypackets.ase.sbb.ProcessMessageException;
import com.baypackets.ase.sbb.SBBEvent;


/**
 * Implementation of the disconnect handler.
 * This class is responsible for handling disconnect operation. 
 * Disconnect operation handles the signalling level details 
 * for disconnecting a  party from a connected call. 
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class DisconnectHandler extends OneWayDisconnectHandler {

	private static final long serialVersionUID = 38824334297033147L;
	/** Logger element */
    private static Logger logger = Logger.getLogger(DisconnectHandler.class.getName());
	
	/**
	 * Public Default Constructor used for Externalizing this Object
	 */
	public DisconnectHandler() {
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
			logger.debug("<SBB>entered start() "  +getOperationContext());
		
		//first disconnect Party-B then Party-A 
		setDisconnectParty(PARTY_B);
		if (logger.isInfoEnabled())
			logger.info("<SBB> Deligating to OneWayDisconnectHandler");
		super.start();
		if (logger.isDebugEnabled())
			logger.debug("<SBB>exited start() ");
    }



	public void handleRequest(SipServletRequest request) {
		logger.error("<SBB> Error: Received a request on DisconnectHandler. ");
	}

	/**
     * This method  handles all response from party issued a BYE request 
     *
     * @response - Response .
     */
	public void handleResponse(SipServletResponse response) {

		if (logger.isDebugEnabled())
			logger.debug("<SBB> entered handleResponse with following response :: "+response);
		if (getDisconnectParty() == PARTY_B) {
			super.handleResponse(response,false);
			if (logger.isInfoEnabled())
				logger.info("<SBB> Disconnecting party-B");
			// OneWayDisconnect operation for Party-B 
			setDisconnectParty(PARTY_A);
			try {
				super.start();
				//this is done to ensure that disconnected event should be fired 
				//after receiving 200 OK(BYE) response from Party-A
//				setCompleted(true);	
//				logger.info("<SBB> firing <DISCONNECT> event");
//	            fireEvent(SBBEvent.EVENT_DISCONNECTED,SBBEvent.REASON_CODE_SUCCESS,response);
				return;
			}
			catch(ProcessMessageException exp) {
				logger.error(exp.getMessage(),exp);
			}
		}
		// this will handle 200 OK for party-B
		if ( getDisconnectParty() == PARTY_A) {
			handleResponse(response,true);
		}
		if (logger.isDebugEnabled())
			logger.debug("<SBB> exited handleResponse");
    }

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		super.readExternal(in);
	}

	public void writeExternal(ObjectOutput out) throws IOException {
		super.writeExternal(out);
	}

}
