/*
 * Created on Aug 30, 2004
 *
 */
package com.baypackets.ase.container;

import com.baypackets.ase.common.AseBaseConnector;

/**
 * @author Ravi
 */
public interface AseProtocolAdapter {

	public AseBaseConnector getConnector();
	
	public Object createFactory(AseContext context);
	
	public AseApplicationSession createApplicationSession(AseContext context);
}
