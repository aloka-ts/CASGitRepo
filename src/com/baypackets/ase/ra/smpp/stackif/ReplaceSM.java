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
//      File:   ReplaceSM.java
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

public class ReplaceSM extends AbstractSmppRequest {

	private static Logger logger = Logger.getLogger(ReplaceSM.class);
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
	private org.smpp.pdu.ReplaceSM stackObj;
	
	private SmscSession smscSession;

	public ReplaceSM() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside ReplaceSM()");
		}
		setType(Constants.REPLACE_SM_REQ);
		stackObj = new org.smpp.pdu.ReplaceSM();
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
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	public String getScheduledDeliveryTime(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getScheduledDeliveryTime()");
		}
		return this.stackObj.getScheduleDeliveryTime();
	}

	public String getValidityPeriod(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getValidityPeriod()");
		}
		//return Integer.parseInt(this.stackObj.getValidityPeriod());
		return this.stackObj.getValidityPeriod();
	}

	public byte getRegisteredDelivery(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getRegisteredDelivery()");
		}
		return this.stackObj.getRegisteredDelivery();
	}

	public String getShortMessage() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getShortMessage()");
		}
		return this.stackObj.getShortMessage();
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
			logger.debug("Leaving setSequenceNumber(int)");
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
			logger.debug("leaving setMessageId(String)");
		}
	}

	public void setSourceAddr(Address address){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSourceAddr(Address)");
		}
		this.stackObj.setSourceAddr(((AddressImpl)address).getStackObject());
	}

	public void setScheduledDeliveryTime(String scheduleTime) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setScheduledDeliveryTime()");
		}
		try{
			this.stackObj.setScheduleDeliveryTime(scheduleTime);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setScheduledDeliveryTime()");
		}
	}
	
	public void setValidity(String validity) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setValidity()");
		}
		try{
			Integer vali = new Integer(validity);
			this.stackObj.setValidityPeriod(vali.toString());
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("leaving setValidity()");
		}
	}
	
	public void setRegisteredDelivery(byte value){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setRegisteredDelivery()");
		}
		// TODO what is the value of byte in this 
		this.stackObj.setRegisteredDelivery(value);
	}
	
	public void setShortMessage(String message) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setShortMessage(String)");
		}
		try{
			this.stackObj.setShortMessage(message);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("leaving setShortMessage(String)");
		}
	}
	/*
	public void setSmscSession(SmscSession smscSession){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSmscSession()");
		}
		this.smscSession=smscSession;
	}*/

	public org.smpp.pdu.ReplaceSM getStackObject() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObject()");
		}
		return this.stackObj;
	}

}
