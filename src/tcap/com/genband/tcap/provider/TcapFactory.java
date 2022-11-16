package com.genband.tcap.provider;

import jain.protocol.ss7.IdNotAvailableException;
import jain.protocol.ss7.tcap.JainTcapListener;

import java.util.Collection;

import javax.servlet.sip.SipApplicationSession;
/**
 * Factory interface for creating and getting the TcapSession objects.
 *
 * The TcapFactory can be obtained from the provider object.  
 *
 */
public interface TcapFactory {
	
	/**
	 * Creates a new TcapSession object and returns it.
	 * @return a new TcapSession object.
	 * @throws IdNotAvailableException - if an Dialogue ID cannot be reserved 
	 * for this session.
	 */
	public TcapSession createTcapSession(JainTcapListener jtl,SipApplicationSession appSession) throws IdNotAvailableException;
	
	/**
	 * Returns an iterator containing all the active TcapSessions created 
	 * by this factory object.
	 * @return iterator of the TcapSession objects.
	 */
	public Collection getAllTcapSessions();
}
