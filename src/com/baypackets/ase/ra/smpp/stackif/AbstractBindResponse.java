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
//      File:   AbstractBindResponse.java
//
//		Desc:	This class implements BindResponse interface and defines a bind 
//				response. This is the base class for all the SMPP responses to be 
//				used in BIND/OUTBIND/UNBIND operations. All the SMPP responses to 
//				be used in BIND/OUTBIND/UNBIND operation   must extend this class.
//				This class contains get methods to get various fields of a these 
//				responses. This interface is not visible to application and used 
//				by SMPP RA.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar				24/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;
import com.baypackets.ase.ra.smpp.SmppResourceException;

public abstract class AbstractBindResponse extends AbstractSmppResponse implements BindResponse {

	private static Logger logger = Logger.getLogger(AbstractBindResponse.class);

	private org.smpp.pdu.BindResponse stackObj;


	/**
	 *	This method returns the system_id parameter of smpp request. This parameter
	 *	is used to identify an ESME or an SMSC at the bind time.
	 *
	 *	@return system_id parameter associated with the SMPP bind request.
	 */
	public String getSystemId() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSystemId() ");
		}
		try{
		return this.stackObj.getSystemId();
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	/**
	 *	This method returns the sc_interface_version parameter. This parameter
	 *	is used to indicate the version of the SMPP protocol supported by SMSC.
	 *
	 *	@return sc_interface_version parameter associated with the SMPP bind request.
	 */
	public byte getScInterfaceVersion()  throws SmppResourceException{
		if(logger.isDebugEnabled()) {
			logger.debug("leaving getScInterfaceVersion()");
		}
		try{
		return this.stackObj.getScInterfaceVersion();
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	public void setStackObject(org.smpp.pdu.BindResponse stkObj){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setStackObject()");
		}
		this.stackObj=stkObj;
	}

	public org.smpp.pdu.BindResponse getStackObject(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObject()");
		}
		return this.stackObj;
	}
}
