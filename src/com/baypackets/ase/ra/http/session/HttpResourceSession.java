package com.baypackets.ase.ra.http.session;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.http.message.HttpRequest;
import com.baypackets.ase.ra.http.message.HttpRequestImpl;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.resource.AbstractSession;
import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

@DefaultSerializer(ExternalizableSerializer.class)
public class HttpResourceSession extends AbstractSession{
	private static final long serialVersionUID = 2365843333637L;
	int sessionStatus;
	private HttpRequest request=null;
	
	/** The Constant logger. */
    private static Logger logger = Logger.getLogger(HttpResourceSession.class);
    

    /** The Constant HTTP_ACTIVE. */
	public static final short HTTP_ACTIVE = 0;	

	/** The Constant HTTP_INACTIVE. */
	public static final short HTTP_INACTIVE = 1;	
	
	/** The _session state. */
	private int _sessionState = HTTP_ACTIVE;
	

	public HttpResourceSession(String id) {
		super(id);
	}

	/**
	 * Added for Serialization
	 */
	public HttpResourceSession() {
	}

	@Override
	public String getProtocol() {
		// TODO Auto-generated method stub
		return "HTTP-RESOURCE";
	}

	@Override
	public Message createMessage(int arg0) throws ResourceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRequest(HttpRequestImpl request) {
		boolean isDebugEnabled=logger.isDebugEnabled();
		if(isDebugEnabled)
			logger.debug("Inside addRequest of HttpResourceSession");
		this.request=request;

	}
	
	/**
	 * Sets the session state.
	 *
	 * @param state the new session state
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
