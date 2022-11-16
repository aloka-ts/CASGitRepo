package com.genband.tcap.provider;

import java.io.IOException;
import java.util.List;
import java.util.TooManyListenersException;

import jain.InvalidListenerConfigException;

import jain.InvalidAddressException;
import jain.ListenerAlreadyRegisteredException;
import jain.ListenerNotRegisteredException;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.tcap.JainTcapListener;
import jain.protocol.ss7.tcap.JainTcapProvider;

/**
 * Extension to the JainTcapProvider interface.
 * 
 * Provides the following extensions to the JainTcapProvider interface.
 * <li> Allows creation of a new TcapSession object </li>
 * <li> Lookup for an existing TcapSession object </li>
 * 
 * The TcapSession allows the application to store the state information.
 * The container can persist this state information and make it available to 
 * the application even in case of the fail-over to a remote peer.
 */
public interface TcapProvider extends JainTcapProvider {
	
	/**
	 * Returns the TcapFactory associated with this listener. 
	 * Returns NULL if this listener object is not registered with the provider yet. 
	 * @param listener The JainTCAPListener.
	 * @return The TCAP Factory Object associated with the listener
	 */
	public TcapFactory getTcapFactory(JainTcapListener listener);
	
	/**
	 * Returns the TcapSession associated with the given Dialogue ID
	 * @param dialogueId - Dialogue Identifier.
	 * @return TcapSession object.
	 */
	public TcapSession getTcapSession(int dialogueId);
	
	public void addJainTcapListener(JainTcapListener listener, List<SccpUserAddress> sccpUserAddress, List<String> serviceKey)
	throws TooManyListenersException,
	       ListenerAlreadyRegisteredException, InvalidAddressException, InvalidListenerConfigException, IOException ;
	
	 public void removeJainTcapListener(JainTcapListener listener, List<String> serviceKey) throws ListenerNotRegisteredException, IOException ;
	 
	 public void tcapListenerActivated(JainTcapListener listener)  ;
	 
	 public void tcapListenerDeActivated(JainTcapListener listener) ;
}
