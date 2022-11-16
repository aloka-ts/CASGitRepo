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
//      File:   QuerySM.java
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

public class QuerySM extends AbstractSmppRequest {

	private static Logger logger = Logger.getLogger(QuerySM.class);
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
	private org.smpp.pdu.QuerySM stackObj;

	private SmscSession smscSession;

	public QuerySM() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside QuerySM()");
		}
		setType(Constants.QUERY_SM_REQ);
		stackObj = new org.smpp.pdu.QuerySM();
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
		} catch(Exception ex){
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
			logger.debug("Inside setSequenceNumber(int)");
		}
		try{
			this.stackObj.setSequenceNumber(seqNum);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("leaving setSequenceNumber(int)");
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
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setMessageId(String)");
		}
	}

	public void setSourceAddr(Address address){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSourceAddr(Address)");
		}
		this.stackObj.setSourceAddr(((AddressImpl)address).getStackObject());
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setSourceAddr(Address)");
		}
	}
	/*	
	public void setSmscSession(SmscSession smscSession){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSmscSession()");
		}
		this.smscSession=smscSession;	
	}*/

	public org.smpp.pdu.QuerySM getStackObject() {
		return this.stackObj;
	}

}
