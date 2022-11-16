package com.baypackets.ase.ra.diameter.rf.stackif;

import org.apache.log4j.Logger;

import com.traffix.openblox.diameter.coding.DiameterAnswer;
import com.traffix.openblox.diameter.coding.DiameterRequest;
import com.traffix.openblox.diameter.rf.generated.event.MessageACA;
import com.traffix.openblox.diameter.rf.generated.event.MessageACR;
import com.traffix.openblox.diameter.rf.generated.event.MessageASR;
import com.traffix.openblox.diameter.rf.generated.session.SessionListenerRfClient;
import com.traffix.openblox.diameter.rf.generated.session.SessionRfClient;
import com.traffix.openblox.diameter.session.DiameterSession;

public class SessionListenerRfClientImpl extends SessionListenerRfClient{
	//static class SessionListenerShClientImpl extends SessionListenerShClient{
	static final Logger logger = Logger.getLogger(SessionListenerRfClientImpl.class.getName());
	private RfStackInterfaceImpl clientInterface;

	public SessionListenerRfClientImpl(RfStackInterfaceImpl shClientApplication) {
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
	protected void handleIncomingACA(SessionRfClient session, MessageACR request,
			MessageACA answer) {
		logger.debug("Received ACA message for  " + request);
		clientInterface.handleIncomingACA(session, request, answer);	
	}

	@Override
	protected void handleIncomingASR(SessionRfClient session, MessageASR request) {
		logger.debug("Received ACR request");
		clientInterface.handleIncomingASR(session, request);
	}
}