/**
 * Created on Mar 9, 2005
 */
package com.baypackets.ase.sipconnector;

import com.baypackets.ase.common.AseContainer;

/**
 * The <code>AseStackInterface</code> interface provides the methods which
 * are required to be implemented by implementations to interact with stack/
 * network.
 *
 * @author Neeraj Jain
 */
interface AseStackInterface {
	public void initialize(AseContainer container);

	public void start();

	public void shutdown();

	public void handleRequest(AseSipServletRequest request);

	public void handleResponse(AseSipServletResponse response);

	public void handleTimeout(AseSipTransaction transaction);

	public void sendRequest(AseSipServletRequest request);

	public void sendResponse(AseSipServletResponse response);
}
