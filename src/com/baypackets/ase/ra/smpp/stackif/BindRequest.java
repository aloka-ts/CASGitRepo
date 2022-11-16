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
//      File:   BindRequest.java
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

import com.baypackets.ase.ra.smpp.AddressRange;
import com.baypackets.ase.ra.smpp.SmppRequest;
import com.baypackets.ase.ra.smpp.SmppResourceException;

public interface BindRequest extends SmppRequest {

	/**
	 *	This method returns the system_id parameter of smpp request.This parameter
	 *	is used to identify an ESME or an SMSC at the bind time.
	 *
	 *	@return system_id parameter associated with the SMPP bind request.
	 */
	public String getSystemId();

	/**
	 *	This method returns the password parameter.This password 
	 *	parameter is used by SMSC to authenticate the identity of a binding
	 *	ESME.
	 *
	 *	@return password parameter associated with the SMPP bind request.
	 */
	public String getPassword();

	/**
	 * This method returns the system_type parameter.This parameter is used
	 *	to categorize the type of ESME that is binding to the SMSC.
	 *
	 *	@return system_type parameter associated with the SMPP bind request.
	 */
	public String getSystemType();

	/**
	 *	This method returns the interface_version parameter.This parameter
	 *	is used to indicate the version of the SMPP protocol.
	 *
	 *	@return interface_version parameter associated with the SMPP bind request.
	 */
	public byte getInterfaceVersion();

	/**
	 *	This method returns the type of number of address parameter associated
	 *	with this request.
	 *
	 *	@return addr_ton parameter associated with the SMPP bind request.
	 */
	public byte getAddressTon();

	/**
	 *	This method returns the numbering plan indicator of address parameter
	 *	associated with this request.
	 *
	 *	@return addr_npi parameter associated with the SMPP request.
	 */
	public byte getAddressNpi();

	/**
	 *	This method returns the address_range paramenter.This parameter
	 *	is used in the bind_receiver and bind_transceiver command to specify
	 *	a set of SME address serviced by the ESME client.
	 *
	 *	@return address_range parameter associated with the Bind Request.
	 */
	public AddressRange getAddressRange();

	/**
	 * This method returns true if SMSC Session established by this bind request
	 *	can be used to send smpp requests.
	 *
	 * @return true if can send SMPP request else false.
	 */
	public boolean isTransmitter();

	/**
	 *	This method returns true if SMSC session established by this bind request 
	 *	can be used to receive smpp requests.
	 *
	 *	@return true is session can be used to receive SMPP requests.
	 */
	public boolean isReceiver();

	/**
	 *	This method sets 'system_id' parameter of a bind request.This parameter 
	 *	identifies the ESME system requesting to bind with the SMSC.
	 *
	 *	@param id -'system_id' of ESME, who wants to bind with SMSC.
	 *
	 *	@Throws SmppResourceException -incase there is any problem in 
	 *	setting the parameter.
	 */
	public void setSystemId(String id) throws SmppResourceException ;

	/**
	 *	This method sets 'password' parameter of a bind request.This parameter 
	 *	is used by SMSC to authenticate the ESME.
	 *
	 *	@param pass -'password' of ESME, who wants to bind with SMSC.
	 *
	 *	@Throws SmppResourceException -incase there is any problem in 
	 *	setting the parameter.
	 */
	public void setPassword(String pass) throws SmppResourceException ;

	/**
	 *	This method sets 'system_type' parameter of a bind request.This parameter 
	 *	identifies the type of ESME system requesting to bind with the SMSC.
	 *
	 *	@param type -'sytem_type' of ESME, who wants to bind with SMSC.
	 *
	 *	@Throws SmppResourceException -incase there is any problem in 
	 *	setting the parameter.
	 */
	public void setSystemType(String type) throws SmppResourceException ;

	/**
	 *	This method sets 'interface_version' parameter of a bind request.This 
	 *	parameter indicates the version of the SMPP protocol supported by the
	 *	SMSC.
	 *
	 *	@param ver -'interface_version' of ESME, who wants to bind with SMSC.
	 *
	 *	@Throws SmppResourceException -incase there is any problem in 
	 *	setting the parameter.
	 */
	public void setInterfaceVersion(int ver) throws SmppResourceException ;

	/**
	 *	This method sets 'address_range' parameter of a bind request.This 
	 *	parameter indicates the range of SME addressess ESME serves.set 
	 *	to null if not known.
	 *
	 *	@param ton -'type of number' of the ESME address.set to null if not
	 *				know.
	 *  @param npi -'numbering plan indicator' of the ESME address.Set to 
	 *				null if not known.
	 *  @param range -'range' of this ESME.Set to null if not known.
*
	 *	@Throws SmppResourceException -incase there is any problem in 
	 *	setting the parameter.
	 */
	public void setAddressRange(int ton, int npi,String range) throws SmppResourceException ;

	/**
	 *	This method sets 'sequence_number' parameter of a bind request.This 
	 *	parameters should be set to an unique value.The bind response corrosponding
	 *	to this bind request should echo the same sequence number.
	 *
	 *	@param seqNum -'sequence_number' of this Bind Request.
	 *
	 *	@Throws SmppResourceException -incase there is any problem in 
	 *	setting the parameter.
	 */
	public void setSequenceNumber(int seqNum) throws SmppResourceException ;

}
