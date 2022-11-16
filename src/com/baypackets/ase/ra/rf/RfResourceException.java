package com.baypackets.ase.ra.rf;

import org.apache.log4j.Logger;

import com.baypackets.ase.resource.ResourceException;
public class RfResourceException extends ResourceException 
{
	private static Logger logger = Logger.getLogger(RfResourceException.class);
	public RfResourceException() 
	{
		super();
		if (logger.isDebugEnabled()) {
			logger.debug("RfResourceException() called.");
		}
	}

	public RfResourceException(String message) 
	{
		super(message);
		if (logger.isDebugEnabled()) {
			logger.debug("RfResourceException(String) called.");
		}
	}

	public RfResourceException(String message, Throwable cause) 
	{
		super(message, cause);
		if (logger.isDebugEnabled()) {
			logger.debug("RfResourceException(String , Throwable) called.");
		}
	}

	public RfResourceException(Throwable cause) 
	{
		super(cause);
		if (logger.isDebugEnabled()) {
			logger.debug("RfResourceException(Throwable) called.");
		}
	}
}
