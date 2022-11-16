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
//      File:   UnbindResp.java
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
public class UnbindResp extends AbstractSmppResponse {

	private static Logger logger = Logger.getLogger(UnbindResp.class);
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
	private org.smpp.pdu.UnbindResp stackObj;

	public UnbindResp() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside UnbindResp()");
		}
		stackObj = new org.smpp.pdu.UnbindResp();
	}

	public UnbindResp(org.smpp.pdu.UnbindResp stackObj){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside UnbindResp(org.smpp.pdu.UnbindResp)");
		}
		this.stackObj=stackObj;
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

	public void setSequenceNumber(int seqNum){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSequenceNumber()");
		}
		this.stackObj.setSequenceNumber(seqNum);
	}
	
	public org.smpp.pdu.UnbindResp getStackObj(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObj()");
		}
		return this.stackObj;
	}
}
