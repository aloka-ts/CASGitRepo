/*
 * Created on Jun 17, 2006
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.baypackets.ase.sysapps.registrar.common;

/**
 * @author rajendra
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author Kameswara Rao
 *
 */

public class Subscription
{
	//data members
	/** the unique identifier for a subscription */
	private String m_subID;

	/** the URI of the address of record for the subscription */
	private String m_subURI;

	/** contact address of the subscriber */
	private String m_subContactAddress;

	/** the time at which the subscription will expires */
	private int m_expiryTime;



	//Methods ,getters and setters

	public String getSubscriptionID()
	{
		return m_subID;
	}

	public void setSubscriptionID(String id)
	{
		m_subID = id;
	}



	public String getSubscriptionURI()
	{
		return m_subURI;
	}
	public void setSubscriptionURI(String uri)
	{
		m_subURI=uri;	
	}


	public String getSubscriptionContactAddress()
	{
		return m_subContactAddress;
	}

	public void setSubscriptionContactAddress(String contactAddress)
	{
		m_subContactAddress = contactAddress;
	}


	public int getExpiryTime()
	{
		return m_expiryTime;
	}

	public void setExpiryTime(int expire)
	{
		m_expiryTime = expire;
	}

}
