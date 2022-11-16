package com.baypackets.ase.ra.radius;

public interface RadiusAccessRequest  extends RadiusRequest {


	/**
	 * Passphrase Authentication Protocol
	 */
	public static final String AUTH_PAP = "pap";
	
	/**
	 * Challenged Handshake Authentication Protocol
	 */
	public static final String AUTH_CHAP = "chap";
	
	/**
	 * Sets the User-Name attribute of this Access-Request.
	 * @param userName user name to set
	 */
	public void setUserName(String userName);
	
	/**
	 * Sets the plain-text user password.
	 * @param userPassword user password to set
	 */
	public void setUserPassword(String userPassword);
	
	/**
	 * Retrieves the plain-text user password.
	 * Returns null for CHAP - use verifyPassword().
	 * @see #verifyPassword(String)
	 * @return user password
	 */
	public String getUserPassword() ;
	
	/**
	 * Retrieves the user name from the User-Name attribute.
	 * @return user name
	 */
	public String getUserName() ;
	
	
	/**
	 * Returns the protocol used for encrypting the passphrase.
	 * @return AUTH_PAP or AUTH_CHAP
	 */
	public String getAuthProtocol() ;
	
	/**
	 * Selects the protocol to use for encrypting the passphrase when
	 * encoding this Radius packet.
	 * @param authProtocol AUTH_PAP or AUTH_CHAP
	 */
	public void setAuthProtocol(String authProtocol) ;
		
	
	/**
	 * Verifies that the passed plain-text password matches the password
	 * (hash) send with this Access-Request packet. Works with both PAP
	 * and CHAP.
	 * @param plaintext
	 * @return true if the password is valid, false otherwise
	 */
	public boolean verifyPassword(String plaintext) throws RadiusResourceException;

	/**
     *  Create an answer with a given type code.
     */
	RadiusAccessAnswer createAnswer(int type) throws RadiusResourceException;
	
	
}
