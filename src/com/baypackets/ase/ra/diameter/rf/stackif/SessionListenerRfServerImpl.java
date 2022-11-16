package com.baypackets.ase.ra.diameter.rf.stackif;

import org.apache.log4j.Logger;

import com.traffix.openblox.diameter.coding.DiameterAnswer;
import com.traffix.openblox.diameter.coding.DiameterRequest;
import com.traffix.openblox.diameter.rf.generated.event.MessageACR;
import com.traffix.openblox.diameter.rf.generated.event.MessageASA;
import com.traffix.openblox.diameter.rf.generated.event.MessageASR;
import com.traffix.openblox.diameter.rf.generated.session.SessionListenerRfServer;
import com.traffix.openblox.diameter.rf.generated.session.SessionRfServer;
import com.traffix.openblox.diameter.session.DiameterSession;

public class SessionListenerRfServerImpl extends SessionListenerRfServer
{

	static final Logger logger = Logger.getLogger(com.baypackets.ase.ra.diameter.rf.stackif.SessionListenerRfServerImpl.class);
	private RfStackServerInterfaceImpl ServerInterface;

	public SessionListenerRfServerImpl(RfStackServerInterfaceImpl shServerApplication)
	{
		logger.debug((new StringBuilder()).append("Inside constructor of SessionListenerRfServerImpl with ").append(shServerApplication).toString());
		ServerInterface = shServerApplication;
	}

	@Override
	protected void handleIncomingACR(SessionRfServer serverSession, MessageACR request) {
		logger.debug(" handleIncomingACR "+request);
		ServerInterface.handleIncomingACR(serverSession, request);

	}

	@Override
	protected void handleIncomingASA(SessionRfServer serverSession, MessageASR pendingRequest,
			MessageASA answer) {
		logger.debug(" handleIncomingASA "+answer);
		ServerInterface.handleIncomingASA(serverSession, answer);

	}

	protected void receivedErrorMessage(DiameterSession session, DiameterRequest pendingRequest, DiameterAnswer answer)
	{
		logger.debug("Inside receivedErrorMessage with " +answer);
		ServerInterface.receivedErrorMessageServerMode(session, pendingRequest, answer);
	}

	protected void timeoutExpired(DiameterSession session, DiameterRequest pendingRequest)
	{
		logger.debug("Inside timeoutExpired with "+pendingRequest);
		ServerInterface.timeoutExpiredServerMode(session, pendingRequest);
	}

}
