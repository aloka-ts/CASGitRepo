package com.baypackets.ase.ra.radius;

public interface RadiusAccountingRequest  extends RadiusRequest {
	/**
	 * Acct-Status-Type: Start
	 */
	public static final int ACCT_STATUS_TYPE_START = 1;

	/**
	 * Acct-Status-Type: Stop
	 */
	public static final int ACCT_STATUS_TYPE_STOP = 2;

	/**
	 * Acct-Status-Type: Interim Update/Alive
	 */
	public static final int ACCT_STATUS_TYPE_INTERIM_UPDATE = 3;

	/**
	 * Acct-Status-Type: Accounting-On
	 */
	public static final int ACCT_STATUS_TYPE_ACCOUNTING_ON = 7;

	/**
	 * Acct-Status-Type: Accounting-Off
	 */
	public static final int ACCT_STATUS_TYPE_ACCOUNTING_OFF = 8;
	
	/**
	 * Sets the User-Name attribute of this Accountnig-Request.
	 * @param userName user name to set
	 */
	public void setUserName(String userName) ;
	
	/**
	 * Retrieves the user name from the User-Name attribute.
	 * @return user name
	 */
	public String getUserName() throws RadiusResourceException;
	
	/**
	 * Sets the Acct-Status-Type attribute of this Accountnig-Request.
	 * @param acctStatusType ACCT_STATUS_TYPE_* to set
	 */
	public void setAcctStatusType(int acctStatusType) ;
	
	/**
	 * Retrieves the user name from the User-Name attribute.
	 * @return user name
	 */
	public int getAcctStatusType() throws RadiusResourceException;
	
	/**
     *  Create an answer with a given type code.
     */
	RadiusAccountingAnswer createAnswer(int type) throws RadiusResourceException;
		
}
