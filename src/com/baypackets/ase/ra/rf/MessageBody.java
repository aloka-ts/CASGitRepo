package com.baypackets.ase.ra.rf;

import java.io.Serializable;
import com.baypackets.ase.ra.rf.impl.*;

import org.apache.log4j.Logger;

/**
 * This class defines the Message-Body AVP that is part of an accounting request
 * according to 3GPP TS 32.299 V6.5.0
 *
 * Application can use it's methods to fill various fields of Message-Body AVP.
 *
 * @author Prashant Kumar
 *
 */

public class MessageBody implements Serializable {

	private Logger logger = Logger.getLogger(MessageBody.class);

	private com.condor.chargingcommon.MessageBody m_messageBody = null;

	/**
	 * This method returns a new instance of MessageBody
	 *
	 */
	public MessageBody()
	{
		if(logger.isDebugEnabled())
			logger.debug("Inside the constructor of MessageBody.");
		m_messageBody = new com.condor.chargingcommon.MessageBody();
	}

	/**
	 * This method is used by application to get the Content-Disposition AVP
	 *
	 * @return Strign object containing Content-Disposition AVP
	 */	
	public String getContentDisp()
	{
		if(logger.isDebugEnabled())
			logger.debug("getContentDisp() called.");
		return m_messageBody.getContentDisp();
	}

	/**
	 * This method is used by application to set Content-Disposition AVP
	 * 
	 * @param des - Content-Disposition AVP to be set
	 */
	public void setContentDisp( String des)
	{
		if(logger.isDebugEnabled())
			logger.debug("setContentDisp() called.");
		m_messageBody.setContentDisp(des);
	}

	/**
	 * This method is used by application to get Content-Length AVP
	 *
	 * @return string object containing Content-Length AVP
	 */
	public String getContentLen()
	{
		if(logger.isDebugEnabled())
			logger.debug("getContentLen() called.");
		return m_messageBody.getContentLen();
	}

	/**
	 * This method is used by application to set Content-Length AVP
	 *
	 * @param length - Content-Type AVP
	 */
	public void setContentLen( String length )
	{
		if(logger.isDebugEnabled())
			logger.debug("setContentLen() called.");
		m_messageBody.setContentLen(length);
	}

	/**
	 * This method is used by application to get Content-Type AVP
	 *
	 * @return String object containing Content-Type AVP
	 */
	public String getContentType()
	{
		if(logger.isDebugEnabled())
			logger.debug("getContentType() called.");
		return m_messageBody.getContentType();
	}
	
	/**
	 * This method is used by application to set Content-Type AVP
	 *
	 * @param type - Content-Type AVP to be set 
	 */
	public void setContentType( String type)
	{
		if(logger.isDebugEnabled())
			logger.debug("setContentType() called.");
		m_messageBody.setContentType(type);
	}

	/**
 	 * This method returns a boolean showing the presence of Originator AVP
	 *
	 * @return boolean - true if Originator AVP is present
	 *   				 false if Originator AVP is not present
	 */
	public boolean getIsOrignatorPresent()
	{
		if(logger.isDebugEnabled())
			logger.debug("getIsOrignatorPresent() called.");
		return m_messageBody.getIsOrignatorPresent();
	}

	/**
	 * This method is used by application to get the Originator AVP
	 *
	 * @return Originator AVP
	 */
	public int getOrignator()
	{
		if(logger.isDebugEnabled())
			logger.debug("getOrignator() called.");
		return m_messageBody.getOrignator();
	}

	/**
	 * This method is used by application to set the Originator AVP
	 * 
	 * @param orignator - Originator AVP to be set
	 */
	public void setOrignator( int orignator)
	{
		if(logger.isDebugEnabled())
			logger.debug("setOrignator() called.");
		m_messageBody.setOrignator(orignator);
	}
	public com.condor.chargingcommon.MessageBody getStackObject()
	{
		if(logger.isDebugEnabled())
			logger.debug("getStackObject() called.");
		return m_messageBody;
	}
}	
