package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class defines the Event-Type AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of Event-Type AVP.
 *
 * @author Prashant Kumar
 *
 */

public class EventType implements Serializable {

	public static Logger logger = Logger.getLogger(EventType.class);

	public com.condor.chargingcommon.EventType m_eventType = null;

	/**
	 * This method creates a new instance of EventType
	 *
	 */
	public EventType()
	{
		m_eventType = new com.condor.chargingcommon.EventType();
	}

	/**
	 * This method is used by application to get Event AVP
	 *
	 * @return String object containing the Event AVP
	 */  
	public String getEvent()
	{
		return m_eventType.getEvent();
	}

	/**
	 * This method is used by application to set Event AVP
	 * 
	 * @param event - Event AVP to be set
	 */	 
	public void setEvent( String event )
	{
		m_eventType.setEvent(event);
	}

	/** 
	 * This method is used by application to get Expires AVP
	 *
	 * @return int object containing Expires AVP
	 */
	public int getExpires()
	{
		return m_eventType.getExpires();
	}

	/**
	 * This method is used by application to set Expires AVP
	 * 
	 * @param exp - Expires AVP to be set
	 */
	public void setExpires( int exp )
	{
		m_eventType.setExpires(exp);
	}
	
	/**
	 * This method returns a boolean associated with Expires AVP
	 * 
	 * @return boolean - true if Expire AVP is present 
	 * 					 false if Expires AVP not present
	 */
	public boolean getIsExpiresPresent()
	{
		return m_eventType.getIsExpiresPresent() ;
	}
	 
	/**
	 * This method is used by application to get SipMethod AVP
	 *
	 * @return String object containing the SipMethod AVP
	 */
	public String getSipMethod()
	{
		return m_eventType.getSipMethod();
	}
	
	/**
	 * This method is used by application to set SipMethod AVP
	 *
	 * @param method - The SipMethod AVP to b set
	 */
	public void setSipMethod( String method)
	{
		m_eventType.setSipMethod(method);
	}
	public com.condor.chargingcommon.EventType getStackObject()
	{
		return m_eventType;
	}
}
