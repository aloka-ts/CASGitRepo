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
//      File:   DataSM.java
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
import com.baypackets.ase.ra.smpp.impl.SmppSession;
import org.smpp.util.ByteBuffer;

public class OutBind extends AbstractSmppRequest {

	private static Logger logger = Logger.getLogger(OutBind.class);
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
	private org.smpp.pdu.Outbind stackObj;
	private SmppSession m_session;

	public OutBind(org.smpp.pdu.Outbind stackObj) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside OutBind(org.smpp.pdu.Outbind)");
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

	public org.smpp.pdu.Outbind getStackObject() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObject()");
		}
		return this.stackObj;
	}

	public String getSystemId(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSystemId()");
		}
		return this.stackObj.getSystemId();
	}

	public String getPassword(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getPassword()");
		}
		return this.stackObj.getPassword();
	}
}
