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
//      File:   Cancel.java
//
//		Desc:	This class represents replace request to replace an SMPP request sent
//				earlier to the SMSC. Whenever application wants to replace an SMPP 
//				request sent earlier to SMSC it calls replace () on that request.
//				SMPP RA creates a ReplaceSM request object with the same message-ID
//				as send back by SMSC in the response to earlier sent SMPP request
//				(Submit/Data) and sends it to SMSC.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
/***********************************************************************************/


package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.smpp.Address;
import com.baypackets.ase.ra.smpp.stackif.AddressImpl;
import com.baypackets.ase.ra.smpp.SmppResourceException;

public class CancelSM extends AbstractSmppRequest {

	private static Logger logger = Logger.getLogger(CancelSM.class);

	/**
	*	This is the unique sequence number for a particular smpp request. The smpp response 
	*	corresponding to this request must have the same sequence number. This is to be used
	*	as a key by resource adaptor to put outstanding smpp requests into a MAP.
	*/
	private int seqNumber = 0;

	/**
	 *	This attribute contains the stack object which is to be used 
	 *	for all the set/get method
	 *
	 */
	private org.smpp.pdu.CancelSM stackObj;

	private SmscSession smscSession;

	public CancelSM() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside CancelSM()");
		}
		setType(Constants.CANCEL_SM_REQ);
		stackObj = new org.smpp.pdu.CancelSM();
	}

	public int getCommandLength(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandLength()");
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

	public String getServiceType(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getServiceType()");
		}
		return this.stackObj.getServiceType();
	}

	public String getMessageId(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getMessageId()");
		}
		return this.stackObj.getMessageId();
	}

	public int getSourceAddrTon(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSourceAddrTon()");
		}
		return this.stackObj.getSourceAddr().getTon();
	}

	public int getSourceAddrNpi(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSourceAddrNpi()");
		}
		return this.stackObj.getSourceAddr().getNpi();
	}

	public Address getSourceAddr() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSourceAddr()");
		}
		try{
			return new AddressImpl(this.stackObj.getSourceAddr());
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	public int getDestAddrTon(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDestAddrTon()");
		}
		return this.stackObj.getDestAddr().getTon();
	}

	public int getDestAddrNpi(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDestAddrNpi()");
		}
		return this.stackObj.getDestAddr().getNpi();
	}

	public Address getDestinationAddr() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDestinationAddr()");
		}
		try{
			return new AddressImpl(this.stackObj.getDestAddr());
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}
	/*
	public SmscSession getSmscSession(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSmscSession()");
		}
		return this.smscSession;
	}*/

	public void setSequenceNumber(int seqNum) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSequenceNumber()");
		}
		try{
			this.stackObj.setSequenceNumber(seqNum);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	public void setServiceType(String servType) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setServiceType(String)");
		}
		try{
			this.stackObj.setServiceType(servType);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	public void setMessageId(String messageId) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setMessageId(String)");
		}
		try{
			this.stackObj.setMessageId(messageId);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	public void setSourceAddr(Address address){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSourceAddr(Address)");
		}
		this.stackObj.setSourceAddr(((AddressImpl)address).getStackObject());
	}
				    
	public void setDestAddr(Address address){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setDestAddr(Address)");
		}
		this.stackObj.setDestAddr(((AddressImpl)address).getStackObject());
	}
	/*
	public void setSmscSession(SmscSession smscSession){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSmscSession()");
		}
		this.smscSession=smscSession;
	}*/

	public org.smpp.pdu.CancelSM getStackObject() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObject()");
		}
		return this.stackObj;
	}

}
