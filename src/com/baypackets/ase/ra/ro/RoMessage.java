/**
 * Filename:	RoMessage.java
 * Created On:	30-Sept-2006
 */

package com.baypackets.ase.ra.ro;

import java.util.List;

import com.baypackets.ase.resource.Message;

/**
 * <code>RoMessage</code> interface represents an Ro message to
 * applications. It specifies common operations which can be performed
 * by applications.
 *
 * @author Neeraj Jain
 */
public interface RoMessage extends Message
{
	/**
	 * This method is used by application to access Diameter Session-Id AVP
	 * of Ro request and answer.
	 *
	 * @return String object containing Session-Id AVP
	 */
	public String getSessionId();

	/**
	 * This method is used by application to access Diameter Origin-Host AVP
	 * of Ro request and answer.
	 *
	 * @return <code>DiamIdent</code> object containing Origin-Host AVP
	 */
	public DiamIdent getOriginHost();

	/**
	 * This method is used by application to access Diameter Origin-Realm AVP
	 * of Ro request and answer.
	 *
	 * @return <code>DiamIdent</code> object containing Origin-Realm AVP
	 */
	public DiamIdent getOriginRealm();

	/**
	 * This method is used by application to access Diameter Proxy-Info AVP
	 * of Ro request and answer. This method returns a reference to internal
	 * list of these AVPs, hence modifying this list will alter the content
	 * of actual message also.
	 *
	 * @return List of <code>ProxyInfo</code> objects containing multiple
	 *         Proxy-Info AVPs
	 *         <code>null</code> if no Proxy-Info AVP is present.
	 */
	public List getProxyInfoList();

	/**
	 * This method is used by application to access Diameter Route-Record AVP
	 * of Ro request and answer. This method returns a reference to internal
	 * list of these AVPs, hence modifying this list will alter the content
	 * of actual message also.
	 *
	 * @return List of <code>DiamIdent</code> objects containing multiple
	 *         Route-Record AVPs
	 *         <code>null</code> if no Route-Record AVP is present.
	 */
	public List getRouteRecordList();

	/**
	 * This method adds a Proxy-Info AVP value into the message.
	 *
	 * @param proxyInfo Proxy-Info AVP to be added
	 *
	 * @throws IllegalStateException if the message is incoming or already sent
	 */
	public void addProxyInfo(ProxyInfo proxyInfo);

	/**
	 * This method adds a Route-Record AVP value into the message.
	 *
	 * @param routeRecord Route-Record AVP to be added
	 *
	 * @throws IllegalStateException if the message is incoming or already sent
	 */
	public void addRouteRecord(DiamIdent routeRecord);
}
