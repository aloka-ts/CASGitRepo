/*
 * Created on Feb 18, 2005
 *
 */
package com.baypackets.ase.security;

import com.baypackets.ase.spi.container.SasMessage;

/**
 * This interface defines an object that handles the protocol specific actions
 * involved during user authentication.
 *
 * @author Ravi
 */
public interface SasAuthenticationHandler {
	
	public static final String AUTH_METHOD_BASIC = "basic";
	public static final String AUTH_METHOD_DIGEST = "digest";
	
	public static final short RESP_UNAUTHORIZED = 401;
	public static final short RESP_PROXY_AUTH_REQD = 407;
	
	public static final Short SIP_DIGEST_AUTH_HANDLER = new Short((short)1);
	public static final Short SIP_BASIC_AUTH_HANDLER = new Short((short)2);
	public static final Short SIP_ASSERTED_ID_AUTH_HANDLER= new Short((short)3);
	
	public Short getHandlerType();
		
	public void sendRedirect(SasMessage message) throws SasSecurityException;
	
	public void sendChallenge(SasMessage message, int respCode, String realm) throws SasSecurityException;
	
	public void sendError(SasMessage message, int respCode) throws SasSecurityException;
        
        public void sendError(SasMessage message, int respCode, String msg) throws SasSecurityException;
	
	public SasAuthenticationInfo getCredentials(SasMessage message, String realm, String IdAssertSch, String IdAssertSupp) throws SasSecurityException;
	
	public boolean validateCredentials(SasMessage message, SasAuthenticationInfo authInfo) throws SasSecurityException;
}
