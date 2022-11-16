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
//      File:   AbstractBindRequest.java
//
//		Desc:	This interface defines a bind request. This is the base class for all
//		the SMPP requests to be used in BIND/OUTBIND/UNBIND operations. All the SMPP
//		requests to be used in BIND/OUTBIND/UNBIND operation   must extend this. This
//		class contains get methods to get various fields of a these request. This 
//		interface is not visible to application and used by SMPP RA to bind to SMSC.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar				23/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.smpp.AddressRange;
import com.baypackets.ase.ra.smpp.SmppResourceException;
import com.baypackets.ase.resource.Response;
import com.baypackets.ase.resource.ResourceException;

public abstract class AbstractBindRequest extends AbstractSmppRequest implements BindRequest {

	private static Logger logger = Logger.getLogger(AbstractBindRequest.class);

	private org.smpp.pdu.BindRequest stackObj;

	public AbstractBindRequest(){
	}
	/**
	 *	This method returns the system_id parameter of smpp request. This parameter
	 *	is used to identify an ESME or an SMSC at the bind time.
	 *
	 *	@return system_id parameter associated with the SMPP bind request.
	 */
	public String getSystemId() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSystemId() ");
		}
		return this.stackObj.getSystemId();
	}

	/**
	 *	This method returns the password parameter. This password 
	 *	parameter is used by SMSC to authenticate the identity of a binding
	 *	ESME.
	 *
	 *	@return password parameter associated with the SMPP bind request.
	 */
	public String getPassword() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getPassword() ");
		}
		return this.stackObj.getPassword();
	}

	/**
	 * This method returns the system_type parameter. This parameter is used
	 *	to categorize the type of ESME that is binding to the SMSC.
	 *
	 *	@return system_type parameter associated with the SMPP bind request.
	 */
	public String getSystemType() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSystemType()");
		}
		return this.stackObj.getSystemType();
	}

	/**
	 *	This method returns the interface_version parameter. This parameter
	 *	is used to indicate the version of the SMPP protocol.
	 *
	 *	@return interface_version parameter associated with the SMPP bind request.
	 */
	public byte getInterfaceVersion() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getInterfaceVersion()");
		}
		return this.stackObj.getInterfaceVersion();
	}

	/**
	 *	This method returns the type of number of address parameter associated
	 *	with this request.
	 *
	 *	@return addr_ton parameter associated with the SMPP bind request.
	 */
	public byte getAddressTon() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getAddressTon()");
		}
		return this.stackObj.getAddressRange().getTon();
	}

	/**
	 *	This method returns the numbering plan indicator of address parameter
	 *	associated with this request.
	 *
	 *	@return addr_npi parameter associated with the SMPP request.
	 */
	public byte getAddressNpi() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getAddressNpi()");
		}
		return this.stackObj.getAddressRange().getNpi();
	}

	/**
	 *	This method returns the address_range paramenter. This parameter
	 *	is used in the bind_receiver and bind_transceiver command to specify
	 *	a set of SME address serviced by the ESME client.
	 *
	 *	@return address_range parameter associated with the Bind Request.
	 */
	public AddressRange getAddressRange() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getAddressRange() ");
		}
		org.smpp.pdu.AddressRange stkRange = this.stackObj.getAddressRange();
		AddressRange addRange = new AddressRangeImpl(stkRange);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving getAddressRange()");
		}
		return addRange;
	}

	public void setSystemId(String id) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSystemId()");
		}
		try{
		this.stackObj.setSystemId(id);	
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setSystemId()");
		}
	}

	public void setPassword(String pass) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setPassword()");
		}
		try{
		this.stackObj.setPassword(pass);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setPassword()");
		}
	}

	public void setSystemType(String type) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSystemType()");
		}
		try{
		this.stackObj.setSystemType(type);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setSystemType()");
		}
	}

	public void setInterfaceVersion(int ver) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setInterfaceVersion()");
		}
		try{
		this.stackObj.setInterfaceVersion((byte)ver);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setInterfaceVersion()");
		}
	}

	public void setAddressRange(int ton, int npi,String range) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setAddressRange()");
		}
		try{
		this.stackObj.setAddressRange((byte)ton,(byte)npi,range);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setAddressRange()");
		}
	}

	public void setSequenceNumber(int seqNum) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSequenceNumber()");
		}
		try{
		this.stackObj.setSequenceNumber(seqNum);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("leaving setSequenceNumber()");
		}
	}

	protected void setStackObj(org.smpp.pdu.BindRequest stackObj) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setStackObj()");
		}
		this.stackObj=stackObj;
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setStackObj()");
		}
	}

	public org.smpp.pdu.BindRequest getStackObject(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObject()");
		}
		return this.stackObj;
	}

	public Response createResponse(int type) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside createResponse()");
		}
		throw new SmppResourceException("Response creation not allowed for binding request");
	}

}
