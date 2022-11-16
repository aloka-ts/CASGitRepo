package com.baypackets.ase.ra.diameter.gy.stackif;

import org.apache.log4j.Logger;

import com.traffix.openblox.diameter.coding.DiameterAnswer;
import com.traffix.openblox.diameter.coding.DiameterRequest;
import com.traffix.openblox.diameter.gy.generated.event.MessageACA;
import com.traffix.openblox.diameter.gy.generated.event.MessageACR;
import com.traffix.openblox.diameter.gy.generated.event.MessageCCA;
import com.traffix.openblox.diameter.gy.generated.event.MessageCCR;
import com.traffix.openblox.diameter.gy.generated.event.MessageRAR;
import com.traffix.openblox.diameter.gy.generated.session.SessionListenerGyClient;
import com.traffix.openblox.diameter.gy.generated.session.SessionGyClient;
import com.traffix.openblox.diameter.session.DiameterSession;

public class SessionListenerGyClientImpl extends SessionListenerGyClient{
	//static class SessionListenerShClientImpl extends SessionListenerShClient{
	static final Logger logger = Logger.getLogger(SessionListenerGyClientImpl.class.getName());
	private GyStackInterfaceImpl clientInterface;

	public SessionListenerGyClientImpl(GyStackInterfaceImpl shClientApplication) {
		logger.debug("Inside constructor of SessionListenerShClientImpl with "+shClientApplication);
		this.clientInterface=shClientApplication;
	}

	@Override
	protected void receivedErrorMessage(DiameterSession session,
			DiameterRequest pendingRequest, DiameterAnswer answer) {
		logger.error("received Error message" + answer);
		clientInterface.receivedErrorMessage(session, pendingRequest, answer);
	}

	@Override
	protected void timeoutExpired(DiameterSession session,
			DiameterRequest pendingRequest) {
		logger.debug("Timeout expired for request " + pendingRequest);
		clientInterface.timeoutExpired(session, pendingRequest);
	}

	protected void requestTimeoutExpired(DiameterSession session,
			DiameterRequest pendingRequest) {
		logger.debug("Timeout request " + pendingRequest);
		clientInterface.requestTimeoutExpired(session, pendingRequest);
	}

	@Override
	protected void handleIncomingACA(SessionGyClient session, MessageACR request, 
			MessageACA answer) {
		logger.debug("Received ACA message for  " + request);
		// TODO Auto-generated method stub
	}

	@Override
	protected void handleIncomingASR(SessionGyClient arg0,
			com.traffix.openblox.diameter.gy.generated.event.MessageASR arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void handleIncomingCCA(SessionGyClient session, MessageCCR request,
			MessageCCA answer) {
		logger.debug("Received CCA message for  " + request);
		clientInterface.handleIncomingCCA(session, request, answer);	
	}

	@Override
	protected void handleIncomingRAR(SessionGyClient session, MessageRAR request) {
		logger.debug("Received RAR message for  " + request);
		// TODO Auto-generated method stub
	}

}