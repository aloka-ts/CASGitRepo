package com.baypackets.ase.ra.diameter.gy.stackif;

import org.apache.log4j.Logger;

import com.traffix.openblox.diameter.gy.generated.session.SessionListenerFactoryGyClient;
import com.traffix.openblox.diameter.gy.generated.session.SessionListenerGyClient;



public class SessionListenerFactoryGyClientImpl extends SessionListenerFactoryGyClient {
	static final Logger logger = Logger.getLogger(SessionListenerFactoryGyClientImpl.class.getName());

	private SessionListenerGyClient listener;

	public SessionListenerFactoryGyClientImpl(GyStackInterfaceImpl shClientApplication) {
		logger.debug("Creating SessionListenerFactoryGyClientImpl");
		listener = new SessionListenerGyClientImpl(shClientApplication);
	}

	@Override
	public SessionListenerGyClient newInstance() {
		logger.debug("Creating vew instance of SessionListenerShClient");
		return listener;
	}
}