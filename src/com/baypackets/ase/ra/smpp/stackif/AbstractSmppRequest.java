/*******************************************************************************
 *   Copyright (c) Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/


/***********************************************************************************
//
//      File:   AbstractSmppRequest.java
//
//      Desc:   This class implements SmppRequest interface abstractly. All type of
//              Smpp Requests extends this class. Those methds which are common to
//              all type of Smpp Requests are implemented in this class and rest are
//              declared as abstract and to be implemented by the subclass. Basically
//              this class is a wrapper over stacks org.smpp.pdu.request class. All of
//              the application set/get operations are executed on this class object
//              which intern are executed on underlying stack class object.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
/***********************************************************************************/



package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.Iterator;
import com.baypackets.ase.ra.smpp.SmppRequest;
import com.baypackets.ase.ra.smpp.SmppResponse;
import com.baypackets.ase.ra.smpp.SmppResourceException;
import com.baypackets.ase.ra.smpp.Address;
import com.baypackets.ase.ra.smpp.stackif.Constants;
import com.baypackets.ase.ra.smpp.CallBackNumber;
import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptorImpl;
import com.baypackets.ase.ra.smpp.impl.SmppSession;

import com.baypackets.ase.dispatcher.Destination;

import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.resource.AbstractMessage;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.resource.Response;
import com.baypackets.ase.resource.ResourceException;

public class AbstractSmppRequest extends AbstractMessage implements SmppRequest {
	
	private static Logger logger = Logger.getLogger(AbstractSmppRequest.class);

	/**
	*	This is the unique sequence number for a particular smpp request.The smpp 
	*	response corresponding to this request must have the same sequence number
	*	.This is to be used	as a key by resource adaptor to put outstanding smpp 
	*	requests into a MAP.
	*/
	private transient SmppSession m_session;
	private transient Destination m_destination=null;
	private transient SmscSession smscSession;
	private int m_type=-1;

	public AbstractSmppRequest(){
	}

	public AbstractSmppRequest(SmppSession session){
		this.m_session=session;
	}

	public int getCommandLength() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandLength()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");
	}
	
	public int getCommandId() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandId()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public int getCommandStatus() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandStatus()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public int getSequenceNumber() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSequenceNumber()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public byte getMessageMode() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getmessageMode()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");
	}

	public String getServiceType() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getServiceType()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");
	}

	public int getSourceAddrTon() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSourceAddrTon()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public int getSourceAddrNpi() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSourceAddrNpi()");
		}

		throw new SmppResourceException("This operation is not allowed on this request");
	}

	public Address getSourceAddr() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSourceAddr()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public Address getDestinationAddr() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDestinationAddr()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public int getDestAddrTon() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDestAddrTon()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public int getDestAddrNpi() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDestAddrNpi()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public byte getEsmClass() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getEsmClass()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public int getProtocolId() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getProtocolId()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public int getPriorityFlag() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getPriorityFlag()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public String getScheduledDeliveryTime() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getScheduledDeliveryTime()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public String getValidityPeriod() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getValidityPeriod()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public byte getRegisteredDelivery() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getRegisteredDelivery()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public byte getDataCoding() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDataCoding()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}
	
	public String getShortMessage() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getShortMessage()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}
	
	public CallBackNumber getCallBackNum() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCallBackNum()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	/*
	public byte[] getCallBackNumAtag() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCallBackNumAtag()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public byte getCallBackNumPreInd() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCallBackNumPreInd()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}
	*/
	public Iterator getOptParamNames() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getOptParamNames()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public byte[] getOptParam(short key) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getOptParam()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public int getOptIntParam(short key) throws SmppResourceException{
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getOptintParam()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public String getMessageId() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getMessageId()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	/**
	*  This method returns the <code>SmscSession</code> associated with
	*  this request.
	*
	*  @return <code>SmscSession</code> associated with this request.
	*/
	public SmscSession getSmscSession(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSmscSession()");
		}
		return this.smscSession;
	}

	/**
	 *  This method cancels the SMPP request sent earlier to SMSC.This method is 
	 *	to be overridden by all the classes extending this class.If any subclass
	 *	does not overwrite this method and control comes to this method then, an
	 *	exception is thrown.
	 *
	 *  @throws SmppExceptionException -If this operation is not allowed on this
	 *          kind of request.
	 *  @throws IOException -If IO exception in sending request.
	 */
	public void cancel() throws SmppResourceException,IOException{
		if(logger.isDebugEnabled()) {
			logger.debug("Inside cancel()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	/**
	 *  This method replaces the SMPP request sent earlier to SMSC.This method is 
	 *	to be overridden by all the classes extending this class.If any subclass
	 *	does not overwrite this method and control comes to this method then, an
	 *	exception is thrown.
	 *
	 *  @throws SmppExceptionException -If this operation is not allowed on this
	 *          kind of request.
	 *  @throws IOException -If IO exception in sending request.
	 */
	public void replace(String message) throws SmppResourceException,IOException{
		if(logger.isDebugEnabled()) {
			logger.debug("Inside replace()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	/**
	 *  This method queries the status of an SMPP request sent earlier to SMSC.This 
	 *	method isto be overridden by all the classes extending this class.If any 
	 *	subclass does not overwrite this method and control comes to this method 
	 *	then, an exception is thrown.
	 *
	 *  @throws SmppExceptionException -If this operation is not allowed on this
	 *          kind of request.
	 *  @throws IOException -If IO exception in sending request.
	 */
	public void query() throws SmppResourceException,IOException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside query()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public void setMessageMode(byte mode) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setmessageMode()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");
	}

	public void setServiceType(String type) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setServiceType()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");
	}

	public void setPriorityFlag(int priority) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setPriorityFlag()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public void setValidity(String validity) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setValidity()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}
	
	public void setRegisteredDelivery(byte value) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setRegisteredDelivery()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");
	}
	
	public void setDataCoding(byte value) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setDataCoding()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");
	}
	
	public void setScheduledDeliveryTime(String scheduleTime) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setScheduledDeliveryTime()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public void setShortMessage(String message)  throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setShortMessage()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public void addOptionalParameter(short paramName, byte[] paramValue) 
								throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addOptionalParameter()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}

	public void addOptionalIntParameter(short paramName, int paramValue)
								throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addOptionalIntParameter()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");


	}

	public void setCallBackNum(CallBackNumber cbNum) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setCallBackNum()");
		}
		throw new SmppResourceException("This operation is not allowed on this request");

	}
	
	/**
	*  This method associates a <code>SmscSession</code> with this 
	*  request.
	*
	*  @param smscSession -<code>SmscSession</code> to be associated with 
	*                      this request.
	*/
	public void setSmscSession(SmscSession smscSession){
		if(logger.isDebugEnabled()) {
				logger.debug("Inside setSmscSession()");
		}
		this.smscSession=smscSession;
	}  

	public void setProtocolSession(SmppSession session){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setProtocolSession(SmppSession) with "+session);
		}
		this.m_session=session;
	}

	public String getMethod(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getMethod()");
		}
		// TODO
		// return Constants.METHOD;
		return null;
	}

	public SasProtocolSession getProtocolSession(boolean create) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getProtocolSession(boolean)");
		}
		if( (this.m_session == null) && create) {
			try {
				SessionFactory sf =
				SmppResourceAdaptorImpl.getResourceContext().getSessionFactory();
				this.m_session = (SmppSession)sf.createSession();
			} catch(ResourceException ex) {
				//logger.error("Creating protocol session", ex);
			}  
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving getProtocolSession(boolean) with "+m_session);
		}
		return this.m_session;
	}

	public SasProtocolSession getProtocolSession() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getProtocolSession() with "+m_session);
		}
		return this.m_session; 
	}

	public boolean isSecure() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside isSecure()");
		}
		return false;
	}

	public String getProtocol() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getProtocol()");
		}
		return Constants.PROTOCOL;
	}

	public void setDestination(Object destination){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setDestination()");
		}
		if(m_destination==null)
			m_destination= new Destination();
		this.m_destination = (Destination)destination;
	}

	public Object getDestination(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDestination()");
		}
		return this.m_destination;
	}

	public Response createResponse(int type) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside createResponse(int)");
		}
		throw new SmppResourceException("Response creation not allowed with type");
	}

	public SmppResponse createResponse() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside createResponse()");
		}
		throw new SmppResourceException("response creation not allowed for this request");
	}

	public void setSetDpf(byte value) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSetDpf()");
		}
		throw new SmppResourceException("setting delivery pending flag not"+ 
										"allowed for this request");
	}

	public void setType(int type){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setType()");
		}
		this.m_type=type;
	}
}
