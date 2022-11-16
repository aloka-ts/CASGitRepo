package com.baypackets.ase.ra.radius.stackif;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.radius.RadiusMessage;
import com.baypackets.ase.ra.radius.RadiusResponse;
import com.baypackets.ase.ra.radius.impl.RadiusResourceAdaptorFactory;
import com.baypackets.ase.ra.radius.impl.RadiusSession;
import com.baypackets.ase.spi.container.SasMessageContext;

public abstract class RadiusAbstractResponse extends RadiusMessage implements RadiusResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(RadiusAbstractResponse.class);
	private static int count = 0;
	private int id;
	private boolean isReplicated = false ;
	

	public RadiusAbstractResponse(int type) {		
		super(type);
		if(logger.isDebugEnabled())
		logger.debug("Inside RadiusAbstractResponse constructor ");
	}

	public RadiusAbstractResponse(RadiusSession session){
		super();
		setProtocolSession(session);
	}

	
	
	public int getId() {
		if (this.id == -1) {
			this.id = generateId();
		}
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private static int generateId() {
		if (count >= Integer.MAX_VALUE) {
			count = 0;
		}
		return count++;

	}

	public void isAlreadyReplicated(boolean isReplicated) {
		this.isReplicated = isReplicated;
	}

	public void send() throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("RoAbstractResponse send() called.");
		}
		SasMessageContext context = this.getMessageContext();
		if(context != null){
			context.sendMessage(this);
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Send to RF resource adaptor directly.");
			}
			try {
				RadiusResourceAdaptorFactory.getResourceAdaptor().sendMessage(this);
			} catch (Exception e) {
				logger.error("send(): " ,e);
				throw new IOException(e.getMessage());
			}
		}
	}

	
}
