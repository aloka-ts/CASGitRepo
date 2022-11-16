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
//      File:   AlertNotification.java
//
//      Desc:   This interface defines an SMPP request. This is the base class for all
//		Desc:	Whenever application wants to send an SMPP message (SMS) using data_sm
//				request and calls createDataRequest() on RA, the object of this class 
//				is created by SMPP RA internally. Basically this class is a wrapper 
//				over stacks DataSM class, which is to be sent to SMSC for submit 
//				operation. All of the application set/get operations are executed on
//				this class object which intern are executed on underlying stack class 
//				object.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
/***********************************************************************************/


package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;
import com.baypackets.ase.ra.smpp.Address;
import com.baypackets.ase.ra.smpp.SmppResponse;
import com.baypackets.ase.ra.smpp.SmppResourceException;
import com.baypackets.ase.ra.smpp.impl.SmppSession;
import org.smpp.util.ByteBuffer;

import java.util.Iterator;
import java.util.ArrayList;

public class AlertNotification extends AbstractSmppRequest {

	private static Logger logger = Logger.getLogger(AlertNotification.class);
	/**
	*	This is the unique sequence number for a particular smpp request.The smpp response 
	*	corresponding to this request must have the same sequence number.This is to be used
	*	as a key by resource adaptor to put outstanding smpp requests into a MAP.
	*/
	private int seqNumber = 0;

	/**
	 *	This attribute contains the stack object which is to be used 
	 *	for all the set/get method
	 *
	 */
	private org.smpp.pdu.AlertNotification stackObj;
	private SmppSession m_session;
	private SmscSession smscSession;

	
	/*public AlertNotification(SmppSession session) {
		stackObj = new org.smpp.pdu.DataSM();
		this.m_session=session;
	}*/

	public AlertNotification(org.smpp.pdu.AlertNotification stackObj) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside AlertNotification(org.smpp.pdu.AlertNotification)");
		}
		this.stackObj=stackObj;
	}

	public int getCommandLength(){
		if(logger.isDebugEnabled()) {
			logger.debug("InsidegetCommandLength() ");
		}
		return this.stackObj.getCommandLength();
	}
	
	public int getCommandId(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandId()");
		}
		return this.stackObj.getCommandId();
	}

	public int getCommandStatus(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandStatus()");
		}
		return this.stackObj.getCommandStatus();
	}

	public int getSequenceNumber(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSequenceNumber()");
		}
		return this.stackObj.getSequenceNumber();
	}
	
	public ByteBuffer getBody(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getBody()");
		}
		return this.stackObj.getBody();
	}
	
	public String getHexDump(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getHexDump()");
		}
		return this.getBody().getHexDump();
	}
	
	/*
	public int getSourceAddrTon(){
		return this.stackObj.getSourceAddr().getTon();
	}

	public int getSourceAddrNpi(){
		return this.stackObj.getSourceAddr().getNpi();
	}

	public Address getSourceAddr(){
		return new AddressImpl(this.stackObj.getSourceAddr());
	}

	public int getDestAddrTon(){
		return this.stackObj.getDestAddr().getTon();
	}

	public int getDestAddrNpi(){
		return this.stackObj.getDestAddr().getNpi();
	}
	public Address getDestinationAddr(){
		return new AddressImpl(this.stackObj.getDestAddr());
	}
*/

	/*	
	public Iterator getOptParamNames() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getOptParamNames()");
		}
		// stack has no support for this request.
	}

	public byte[] getOptParam(short paramName) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getOptParam(int)");
		}
		// stack has no support for this request.
	}

	public int getOptIntParam(short paramName) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getOptintParam(int)");
		}
		// stack has no support for this request.
	}

	public void addOptionalParameter(short paramName,byte[] paramValue){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addOptionalParameter()");
		}
		// stack has no support for this request.
	}

	public void addOptionalintParameter(short paramName, int paramValue) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addOptionalParameter()");
		}
		// stack has no support for this request.
	}
	*/

	/**
	 *	This method returns the <code>SmscSession</code> associated with
	 *	this request.
	 *
	 *	@return <code>SmscSession</code> associated with this request.
	 */
	public SmscSession getSmscSession(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSmscSession()");
		}
		return this.smscSession;
	}

	/**
	 *	This method associates a <code>SmscSession</code> with this 
	 *	request.
	 *
	 *	@param smscSession -<code>SmscSession</code> to be associated with 
	 *						this request.
	 */
	public void setSmscSession(SmscSession smscSession){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSmscSession()");
		}
		this.smscSession=smscSession;
	}

	/**
	 *	This method returns the underlying stack object associated with 
	 *	this request.
	 *
	 *	@return org.smpp.pdu.AlertNotification -underlying stack object.
	 */
	public org.smpp.pdu.AlertNotification getStackObject() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObject()");
		}
		return this.stackObj;
	}
}
