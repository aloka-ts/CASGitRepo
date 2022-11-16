/*
 * Created on Nov 15, 2004
 *
 */
package com.baypackets.ase.control;

/**
 * @author Ravi
 */
public interface PeerStateChangeSource {

	public void handlePeerStateChangeException(PeerStateChangeListener listener,
									PeerStateChangeEvent event, 
									PeerStateChangeException ex);
}
