package com.baypackets.ase.ra.diameter.rf.stackif;


import org.apache.log4j.Logger;

import com.traffix.openblox.diameter.rf.generated.session.SessionListenerFactoryRfClient;
import com.traffix.openblox.diameter.rf.generated.session.SessionListenerRfClient;



public class SessionListenerFactoryRfClientImpl extends SessionListenerFactoryRfClient {
	static final Logger logger = Logger.getLogger(SessionListenerFactoryRfClientImpl.class.getName());

	private SessionListenerRfClient listener;

	public SessionListenerFactoryRfClientImpl(RfStackInterfaceImpl shClientApplication) {
		logger.debug("Creating SessionListenerFactoryShClientImpl");
		listener = new SessionListenerRfClientImpl(shClientApplication);
	}

	@Override
	public SessionListenerRfClient newInstance() {
		logger.debug("Creating vew instance of SessionListenerShClient");
		return listener;
	}
}