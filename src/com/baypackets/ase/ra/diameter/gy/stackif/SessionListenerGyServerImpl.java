package com.baypackets.ase.ra.diameter.gy.stackif;

import org.apache.log4j.Logger;

import com.traffix.openblox.diameter.coding.DiameterAnswer;
import com.traffix.openblox.diameter.coding.DiameterRequest;
import com.traffix.openblox.diameter.gy.generated.event.MessageCCR;
import com.traffix.openblox.diameter.gy.generated.event.MessageRAA;
import com.traffix.openblox.diameter.gy.generated.event.MessageRAR;
import com.traffix.openblox.diameter.gy.generated.session.SessionListenerGyServer;
import com.traffix.openblox.diameter.gy.generated.session.SessionGyServer;
import com.traffix.openblox.diameter.session.DiameterSession;

public class SessionListenerGyServerImpl extends SessionListenerGyServer
{

	static final Logger logger = Logger.getLogger(SessionListenerGyServerImpl.class);
	private GyStackServerInterfaceImpl serverInterface;

	public SessionListenerGyServerImpl(GyStackServerInterfaceImpl roServerApplication)
	{
		logger.debug((new StringBuilder()).append("Inside constructor of SessionListenerGyServerImpl with ").append(roServerApplication).toString());
		serverInterface = roServerApplication;
	}

		protected void receivedErrorMessage(DiameterSession session, DiameterRequest pendingRequest, DiameterAnswer answer)
	{
		logger.debug("Inside receivedErrorMessage with " +answer);
		serverInterface.receivedErrorMessageServerMode(session, pendingRequest, answer);
	}

	protected void timeoutExpired(DiameterSession session, DiameterRequest pendingRequest)
	{
		logger.debug("Inside timeoutExpired with "+pendingRequest);
		serverInterface.timeoutExpiredServerMode(session, pendingRequest);
	}

	@Override
	protected void handleIncomingCCR(SessionGyServer serverSession, MessageCCR request) {
		logger.debug(" handleIncomingCCR "+request);
		serverInterface.handleIncomingCCR(serverSession, request);
		
	}

	@Override
	protected void handleIncomingRAA(SessionGyServer serverSession, MessageRAR pendingRequest,
			MessageRAA answer) {
		logger.debug(" handleIncomingRAA "+answer);
	}

	@Override
	protected void handleIncomingACR(SessionGyServer serverSession,
			com.traffix.openblox.diameter.gy.generated.event.MessageACR request) {
		// TODO Auto-generated method stub
		logger.debug("Inside handleIncomingACR "+request);
	}

	@Override
	protected void handleIncomingASA(SessionGyServer serverSession,
			com.traffix.openblox.diameter.gy.generated.event.MessageASR pendingRequest,
			com.traffix.openblox.diameter.gy.generated.event.MessageASA answer) {
		// TODO Auto-generated method stub
		logger.debug("Inside handleIncomingASA "+answer);
	}

}
