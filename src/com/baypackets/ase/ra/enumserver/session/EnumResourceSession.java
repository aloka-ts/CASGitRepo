package com.baypackets.ase.ra.enumserver.session;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.enumserver.message.EnumRequest;
import com.baypackets.ase.ra.enumserver.message.EnumRequestImpl;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.AbstractSession;

public class EnumResourceSession extends AbstractSession {
	
	private static final long serialVersionUID = 2365843333637L;
	
	int sessionStatus;
	private EnumRequest request = null;

	/** The Constant logger. */
	private static Logger logger = Logger.getLogger(EnumResourceSession.class);

	/** The Constant Enum_ACTIVE. */
	public static final short ENUM_ACTIVE = 0;

	/** The Constant Enum_INACTIVE. */
	public static final short ENUM_INACTIVE = 1;

	/** The _session state. */
	private int _sessionState = ENUM_ACTIVE;

	public EnumResourceSession(String id) {
		super(id);
	}

	/**
	 * Added for Serialization
	 */
	public EnumResourceSession() {
	}

	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return "ENUM";
	}

	@Override
	public Message createMessage(int arg0) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRequest(EnumRequestImpl request) {
		boolean isDebugEnabled = logger.isDebugEnabled();
		if (isDebugEnabled)
			logger.debug("Inside setRequest of EnumResourceSession");
		this.request = request;

	}
	
	 /**
  	 * Updates the specified attribute
           */
       @Override
          public void setAttribute(String name, Object attribute){
        	  super.setAttribute(name, attribute);
        	  
        	  if(logger.isDebugEnabled())
      			logger.debug("setAttribute name "+ name +" value "+ attribute);
  		// If state is invalid throw an exception
  		     attributes.put(name, attribute);
  	    }

	/**
	 * Sets the session state.
	 *
	 * @param state
	 *            the new session state
	 */
	public void setSessionState(int state) {
		this._sessionState = state;
	}

	/**
	 * Gets the session state.
	 *
	 * @return the session state
	 */
	public int getSessionState() {
		return this._sessionState;
	}

}
