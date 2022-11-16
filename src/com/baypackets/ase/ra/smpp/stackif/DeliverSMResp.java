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
//      File:   DeliverSMResp.java
//
//
//		Desc:	SMPP RA sends this response, in response to the DeliverSM request 
//				received from SMSC. Basically this response class is a wrapper on
//				underlying stack's org.smpp.pdu.DeliverSMResp class. SMPP RA receives
//				DeliverSM request, it creates a new object of DeliverSMResp class and
//				sends it to SMSC.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar				23/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;
import org.smpp.pdu.WrongLengthOfStringException;

import com.baypackets.ase.ra.smpp.SmppResourceException;

public class DeliverSMResp extends AbstractSmppResponse {

	private static Logger logger = Logger.getLogger(DeliverSMResp.class);
	/**
	*	This is the unique sequence number for a particular smpp request. The smpp response 
	*	corresponding to this request must have the same sequence number. This is to be used
	*   as a key by resource adaptor to put outstanding smpp requests into a MAP.
	*/ 
	private int seqNumber = 0;

	/**
	*  This attribute contains the stack object which is to be used 
	*  for all the set/get method
	*
	*/
	private org.smpp.pdu.DeliverSMResp stackObj;
	private SmscSession smscSession;

	public DeliverSMResp() {
		stackObj = new org.smpp.pdu.DeliverSMResp();
		setType(Constants.DELIVER_SM_RES);
	}

	public DeliverSMResp(org.smpp.pdu.DeliverSMResp stackObj){
		this.stackObj=stackObj;
		setType(Constants.DELIVER_SM_RES);
	}

	public int getCommandLength() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandLength()");
		}
		return this.stackObj.getCommandLength();
	}

	public int getCommandId() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandId()");
		}
		return this.stackObj.getCommandId();
	}

	public int getCommandStatus() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandStatus()");
		}
		return this.stackObj.getCommandStatus();
	}

	public int getSequenceNumber() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSequenceNumber()");
		}
		return this.stackObj.getSequenceNumber();
	}

	public String getMessageId() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getMessageId()");
		}
		return this.stackObj.getMessageId();
	}

	public SmscSession getSmscSession(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSmscSession()");
		}
		return this.smscSession;
	}

	public void setSmscSession(SmscSession smscSession){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSmscSession()");
		}
		this.smscSession=smscSession;
	}
	
	public void setMessageId(String messageId) throws WrongLengthOfStringException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setMessageId()");
		}
		 this.stackObj.setMessageId(messageId);
	}

	public void setSequenceNumber(int seqNum){
		this.stackObj.setSequenceNumber(seqNum);
	}

	public org.smpp.pdu.DeliverSMResp getStackObject(){
		return this.stackObj;
	}

}
