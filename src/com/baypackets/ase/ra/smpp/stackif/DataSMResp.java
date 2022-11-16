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
//      File:   DataSMResp.java
//
//		Desc:	SMSC sends this response in response to the DataSM2 request sent 
//				earlier by application to SMSC. Basically this response class is 
//				a wrapper on underlying stack's org.smpp.pdu.DataResp class. SMPP
//				RA receives stack response object from stack and creates its own 
//				wrapper response object which it then delivers to application.All
//				of the application set/get operations are executed on this class 
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

public class DataSMResp extends AbstractSmppResponse {

	private static Logger logger = Logger.getLogger(DataSMResp.class);
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
	private org.smpp.pdu.DataSMResp stackObj;
	private SmscSession smscSession;

	public DataSMResp() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside DataSMResp()");
		}
		setType(Constants.DATA_SM_RES);
		stackObj = new org.smpp.pdu.DataSMResp();
	}

	public DataSMResp(org.smpp.pdu.DataSMResp stackObj){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside DataSMResp(org.smpp.pdu.DataSMResp)");
		}
		setType(Constants.DATA_SM_RES);
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

	public byte getDpfResult() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDpfResult()");
		}
		try{
			return this.stackObj.getDpfResult();	
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
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

	public void setSequenceNumber(int seqNum){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSequenceNumber(int)");
		}
		this.stackObj.setSequenceNumber(seqNum);
	}
	
	public org.smpp.pdu.DataSMResp getStackObject(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObj()");
		}
		return this.stackObj;
	}
}
