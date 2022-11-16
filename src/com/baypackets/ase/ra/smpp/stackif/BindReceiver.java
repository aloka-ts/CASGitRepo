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
//      File:   BindTransmitter.java
//
//		Desc:	Whenever RA wants to establish a session with SMSC as a receiver
//				(it wants to receive smpp messaes from SMSC), it creates the object
//				of this class and sends it to SMSC. Basically this class is a wrapper
//				over underlying stack\222s BindReceiver class. All of the set/get
//				operations are preformed on this underlying stack object.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar				23/01/08        Initial Creation
//
/***********************************************************************************/
package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;

public class BindReceiver extends AbstractBindRequest {

	private static Logger logger = Logger.getLogger(BindReceiver.class);

	private boolean canTransmit=false;
	private boolean canReceive=true;

	public BindReceiver(){
		org.smpp.pdu.BindReceiver stackObj = new org.smpp.pdu.BindReceiver();
		setStackObj(stackObj);
	}

	/**
	* This method returns true if SMSC Session established by this bind request
	*  can be used to send smpp requests.
	*
	* @return true if can send SMPP request else false.
	*/
	public boolean isTransmitter(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside isTransmitter()");
		}
		return canTransmit;
	}

	/**
	*  This method returns true if SMSC session established by this bind request 
	*  can be used to receive smpp requests.
	*
	*  @return true is session can be used to receive SMPP requests.
	*/
	public boolean isReceiver(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside isReceiver()");
		}
		return canReceive;
	}

}
