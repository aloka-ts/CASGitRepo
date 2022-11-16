/*
 * SBBFactory.java
 * 
 * Created on Jun 17, 2005
 */
package com.baypackets.ase.sbb;

import javax.servlet.sip.SipApplicationSession;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

/**
 * The SBBFactory class provides a factory for obtaining service building block
 * objects.
 *
 *<p>
 * An instance of the SBB Factory can be obtained using the instance() method 
 * defined in this class.
 *
 * The instance() method creates an object of the class specified using the system
 * property "SBBFactory.class".
 * 
 * The class specified using this property should extend the SBBFactory class and 
 * should have a public default Constructor.
 * 
 * @see com.baypackets.ase.sbb.SBB
 * @author BayPackets
 */
public abstract class SBBFactory {
	
	/**
	 * This method creates and returns an implementation of the SBB
	 * interface as specified by the given "type" parameter.
	 *
	 * @param type  The fully qualified class name of the SBB interface to return
	 * (ex. com.baypackets.ase.sbb.b2b.B2bSessionController).
	 * @param name  The unique name that will be assigned to the returned SBB.
	 * @param appSession  App session to which the returned SBB will be associated
	 * @param ctx  The ServletContext to which the returned SBB will be associated
	 * @return  An implementation of the SBB interface.  The caller will be
	 * required to cast the returned object to the type specified by the given
	 * "type" parameter.
	 * @throws IllegalArgumentException  If the given "type" parameter refers to
	 * an unknown SBB type or if an SBB with the specified name is already 
	 * associated with the given app session.
	 */
	public abstract SBB getSBB(String type, String name, SipApplicationSession appSession, ServletContext ctx)
		throws IllegalArgumentException;
}
