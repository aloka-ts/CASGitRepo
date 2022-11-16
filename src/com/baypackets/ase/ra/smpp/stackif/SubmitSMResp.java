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
//      File:   SmppRequest.java
//
//		Desc:	SMSC sends this response in response to the SubmitSM request sent 
//				earlier by application to SMSC. Basically this response class is
//				a wrapper on underlying stack's org.smpp.pdu.SubmitSMResp class.
//				SMPP RA receives stack response object from stack and creates its
//				own wrapper response object which it then delivers to application. 
//				All of the application set/get operations are executed on this class
//				object are intern are executed on underlying stack class object.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar				24/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;
import com.baypackets.ase.ra.smpp.SmppResourceException;
import com.baypackets.ase.ra.smpp.server.receiver.SMSCSession;
import com.baypackets.ase.ra.smpp.server.receiver.SmppPDUProcessor;

public class SubmitSMResp extends AbstractSmppResponse {
	private static Logger logger = Logger.getLogger(SubmitSMResp.class);

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
	private org.smpp.pdu.SubmitSMResp stackObj;
	private SmscSession smscSession;
	private transient SMSCSession smscSessionResponse;
	private transient SmppPDUProcessor smppPDUProcessor;
	
	public SMSCSession getSmscSessionResponse() {
		return smscSessionResponse;
	}

	public void setSmscSessionResponse(SMSCSession smscSessionResponse) {
		this.smscSessionResponse = smscSessionResponse;
	}
	public SmppPDUProcessor getSmppPDUProcessor() {
		return smppPDUProcessor;
	}

	public void setSmppPDUProcessor(SmppPDUProcessor smppPDUProcessor) {
		this.smppPDUProcessor = smppPDUProcessor;
	}

	public SubmitSMResp() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside SubmitSMResp()");
		}
		setType(Constants.SUBMIT_SM_RES);
		stackObj = new org.smpp.pdu.SubmitSMResp();
	}

	public SubmitSMResp(org.smpp.pdu.SubmitSMResp stackObj) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside SubmitSMResp(org.smpp.pdu.SubmitSMResp)");
		}
		setType(Constants.SUBMIT_SM_RES);
		this.stackObj=stackObj;
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


	public org.smpp.pdu.SubmitSMResp getStackObj(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObj()");
		}
		return this.stackObj;
	}

}
