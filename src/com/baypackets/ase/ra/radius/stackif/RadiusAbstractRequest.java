package com.baypackets.ase.ra.radius.stackif;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.radius.RadiusMessage;
import com.baypackets.ase.ra.radius.RadiusRequest;
import com.baypackets.ase.ra.radius.RadiusResourceException;
import com.baypackets.ase.ra.radius.RadiusResponse;
import com.baypackets.ase.ra.radius.impl.RadiusResourceAdaptorFactory;
import com.baypackets.ase.ra.radius.impl.RadiusResourceAdaptorImpl;
import com.baypackets.ase.ra.radius.impl.RadiusSession;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.Response;


public abstract class RadiusAbstractRequest extends RadiusMessage implements RadiusRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1464458746547L;
	private static Logger logger = Logger.getLogger(RadiusAbstractRequest.class);
	private static int count = 0;
	private int id;
	private boolean isReplicated = false ;
	

	public RadiusAbstractRequest() {
		super();
	}

	public RadiusAbstractRequest(int type) {
		super(type);
		if(logger.isDebugEnabled())
			logger.debug("Inside RadiusAbstractRequest(int) constructor ");
	}

	public RadiusAbstractRequest(RadiusSession session){
		super();
		if (logger.isDebugEnabled()) {
			logger.debug("Inside RoAbstractRequest(RoSession) constructor");
		}
		setProtocolSession(session);
	}

	//	public Response createResponse(int type) throws ResourceException {
	//		logger.debug("createResponse(int type) is called.");
	//		//if (this.getResourceContext() != null) {
	//		if(RoResourceAdaptorImpl.getResourceContext() != null) {		
	//			//return (Response)this.getResourceContext().getMessageFactory().createResponse(this, type);
	//			return (Response)RoResourceAdaptorImpl.getResourceContext().getMessageFactory().createResponse(this, type);
	//		} else {
	//			logger.debug("Use default MessageFactory.");
	//			return (Response)RoResourceAdaptorFactory.getMessageFactory().createResponse(this, type);
	//		}
	//	}

	public Response createResponse() throws RadiusResourceException {
		if (logger.isDebugEnabled()) {
			logger.debug("createResponse() is called.");
		}
		try {
			//if (this.getResourceContext() != null ) {
			if(RadiusResourceAdaptorImpl.getResourceContext() != null) {
				//return (RoResponse)this.getResourceContext().getMessageFactory().createResponse(this, this.getType());
				return (RadiusResponse)RadiusResourceAdaptorImpl.getResourceContext().getMessageFactory().createResponse(this, this.getType());
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Use default MessageFactory.");
				}
				return (RadiusResponse)RadiusResourceAdaptorFactory.getMessageFactory().createResponse(this, this.getType());
			}
		} catch (Exception e) {
			throw new RadiusResourceException(e);
		}
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
	@Deprecated
	public void send() throws IOException{
		throw new IOException("Not valid for RadiusRequest");		
	};
	
//	void setStackObject( org.tinyradius.packet.RadiusPacket stkObj){
//		this.stackObj=stkObj;
//	}
//
//	org.tinyradius.packet.RadiusPacket getStackObject(){
//		return this.stackObj;
//	}	

}
