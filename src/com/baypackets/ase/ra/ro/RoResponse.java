/**
 * Filename:	RoResponse.java
 * Created On:	30-Sept-2006
 */
package com.baypackets.ase.ra.ro;

import com.baypackets.ase.resource.Response;

/**
 * <code>RoResponse</code> interface represents an Ro response to
 * applications. It specifies operations which can be performed
 * by applications an Ro response.
 *
 * @author Neeraj Jain
 */
public interface RoResponse extends Response, RoMessage
{
	/**
	 * This method is used to access Result-Code AVP of an Ro answer.
	 *
	 * @return Result-Code AVP value
	 */
	public long getResultCode();
}
