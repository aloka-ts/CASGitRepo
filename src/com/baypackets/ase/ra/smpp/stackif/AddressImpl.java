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
//      File:   AddressImpl.java
//
//		Desc:	This class defines a Address Parameter of an SMPP message. 
//				Application can use this class to make Address parameter, 
//				which is to be used in creating SMPP requests.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar				24/01/08        Initial Creation
//
/***********************************************************************************/
  
 package com.baypackets.ase.ra.smpp.stackif;
 

import org.apache.log4j.Logger;
import com.baypackets.ase.ra.smpp.Address;
import com.baypackets.ase.resource.ResourceException;

public class AddressImpl implements Address {
	
	private static Logger logger = Logger.getLogger(AddressImpl.class);

	private	org.smpp.pdu.Address stackObj;

	public AddressImpl(org.smpp.pdu.Address stackObj) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside AddressImpl(org.smpp.pdu.Address) ");
		}
		try{
			this.stackObj=stackObj;
		}catch(Exception ex){
			throw new ResourceException(ex);
		}
	}

	public AddressImpl(int ton, int npi, String range) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside AddressImpl(int,int,String) ");
		}
		try{
			stackObj=new org.smpp.pdu.Address((byte)ton,(byte)npi,range);
		}catch(Exception ex){
			throw new ResourceException(ex);
		}
	}

	public int getTon(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getTon()");
		}
		return this.stackObj.getTon();
	}

	public int getNpi(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getNpi()");
		}
		return this.stackObj.getNpi();
	}

	public String getRange(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getRange()");
		}
		return this.stackObj.getAddress();
	}

	public org.smpp.pdu.Address getStackObject(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObject()");
		}
		return this.stackObj;
	}
}
