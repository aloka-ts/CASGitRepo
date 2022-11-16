/**
 * Filename:	ProxyInfo.java
 * Created On:	30-Sept-2006
 */

package com.baypackets.ase.ra.ro;

/**
 * This class defines the Proxy-Info AVP that is part of an
 * credit control request.
 *
 * Application can use it's methods to fill various fields of Proxy-Info AVP.
 *
 * @author Neeraj Jain
 *
 */

public interface ProxyInfo
{
	/**
	 * This method returns the Proxy-Host AVP associated with the credit
	 * control request.
	 * 
	 * @return <code>DiamIdent</code> object containing Proxy-Host AVP.
	 */

	public DiamIdent getProxyHost();

	/**
	 * This method returns the Proxy-State AVP associated with the credit
	 * control request.
	 * 
	 * @return byte[] object containing Proxy-State AVP.
	 */

	public byte[] getProxyState();

	/**
	 * This method associates the Proxy-Host AVP to a credit control request.
	 *
	 * @param host - <code>DiamIdent</code> object containing Proxy-Host AVP to be set.
	 */

	public void setProxyHost(DiamIdent host);

	/**
	 * This method associates the Proxy-State AVP to a credit control request.
	 *
	 * @param state - byte[] object containing Proxy-Host AVP to be set.
	 */

	public void setProxyState(byte[] state);
}
