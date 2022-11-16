/*
 * Created on Aug 30, 2004
 *
 */
package com.baypackets.ase.container.sip;

import javax.servlet.sip.Address;
import javax.servlet.sip.AuthInfo;
import javax.servlet.sip.Parameterable;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;

import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.security.SasAuthInfoImpl;
import com.baypackets.ase.sipconnector.AseConnectorSipFactory;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.ocm.OverloadControlManager;
import com.baypackets.ase.common.Registry;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
/**
 * @author Ravi
 */
public class SipFactoryImpl implements SipFactory {
	
	private AseConnectorSipFactory connectorFactory = null;
	private AseContext context = null;
	private OverloadControlManager m_ocm = null;
	private int ocmId; 
	
	public SipFactoryImpl(AseConnectorSipFactory connectorFactory , AseContext context){
		this.connectorFactory = connectorFactory;
		this.context = context;
		m_ocm = (OverloadControlManager)Registry.lookup(Constants.NAME_OC_MANAGER);
		ocmId = m_ocm.getParameterId(OverloadControlManager.APP_SESSION_COUNT);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipFactory#createURI(java.lang.String)
	 */
	public URI createURI(String uri) throws ServletParseException {
		return connectorFactory.createURI(uri);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipFactory#createSipURI(java.lang.String, java.lang.String)
	 */
	public SipURI createSipURI(String user, String host) {
		return connectorFactory.createSipURI(user, host);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipFactory#createAddress(java.lang.String)
	 */
	public Address createAddress(String sipAddress) throws ServletParseException {
		return connectorFactory.createAddress(sipAddress);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipFactory#createAddress(javax.servlet.sip.URI)
	 */
	public Address createAddress(URI uri) {
		return connectorFactory.createAddress(uri);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipFactory#createAddress(javax.servlet.sip.URI, java.lang.String)
	 */
	public Address createAddress(URI uri, String displayName) {
		return connectorFactory.createAddress(uri, displayName);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipFactory#createRequest(javax.servlet.sip.SipApplicationSession, java.lang.String, javax.servlet.sip.Address, javax.servlet.sip.Address)
	 */
	public SipServletRequest createRequest(
		SipApplicationSession appSession,
		String method,
		Address from,
		Address to) {
		return connectorFactory.createRequest(appSession, method, from , to);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipFactory#createRequest(javax.servlet.sip.SipApplicationSession, java.lang.String, javax.servlet.sip.URI, javax.servlet.sip.URI)
	 */
	public SipServletRequest createRequest(
		SipApplicationSession appSession,
		String method,
		URI from,
		URI to) {
		return connectorFactory.createRequest(appSession, method, from , to);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipFactory#createRequest(javax.servlet.sip.SipApplicationSession, java.lang.String, java.lang.String, java.lang.String)
	 */
	public SipServletRequest createRequest(
		SipApplicationSession appSession,
		String method,
		String from,
		String to)
		throws ServletParseException {
		return connectorFactory.createRequest(appSession, method, from , to);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipFactory#createRequest(javax.servlet.sip.SipServletRequest, boolean)
	 */
	public SipServletRequest createRequest(
		SipServletRequest origRequest,
		boolean sameCallId) {
		return connectorFactory.createRequest(origRequest, sameCallId);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.sip.SipFactory#createApplicationSession()
	 */
	public SipApplicationSession createApplicationSession() {
		// App session created using factory is treated as normal appSession
		this.m_ocm.increase(ocmId);
		//JSR 289.42
		return (SipApplicationSession)context.createApplicationSession(Constants.PROTOCOL_SIP, null,null);
	}

	public SipApplicationSession createApplicationSessionByKey(String sessionKey) {
		// TODO Auto-generated method stub
		this.m_ocm.increase(ocmId);
		
		final String UNDERSCORE="_";
		StringBuilder sessionId = null;
		ConfigRepository cr = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

		String ipAddressOfContainer = AseUtils.getIPAddress(cr.getValue(Constants.OID_BIND_ADDRESS));
		// session id generated is combination of
		// "Application Name+Version+idreturnedfromannotated method+ipAddressofthecontainer"
		// to make it unique among all the containers
		sessionId = new StringBuilder(context.getObjectName());
		sessionId.append(UNDERSCORE).append(context.getVersion()).append(
				UNDERSCORE).append(sessionKey).append(UNDERSCORE).append(
				ipAddressOfContainer);
		
		return (SipApplicationSession)context.createApplicationSession(Constants.PROTOCOL_SIP, null,sessionId.toString());

	}

	//Bug Id: 5638
	public AuthInfo createAuthInfo() {
		// TODO Auto-generated method stub
		return new SasAuthInfoImpl();
	}

        public Parameterable createParameterable(String value)
                        throws ServletParseException {
                return connectorFactory.createParameterable(value);
        }
}
