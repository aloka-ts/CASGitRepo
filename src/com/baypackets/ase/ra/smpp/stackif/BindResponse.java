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
//      File:   BindResponse.java
//
//		Desc:	This interface defines a bind response. This is the base class for 
//				all the SMPP responses to be used in BIND/OUTBIND/UNBIND operations.
//				All the SMPP responses to be used in BIND/OUTBIND/UNBIND operation
//				must extend this. This class contains get methods to get various
//				fields of a these responses. This interface is not visible to application 
//				and used by SMPP RA.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar				24/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.stackif;

import com.baypackets.ase.ra.smpp.SmppResourceException;
import com.baypackets.ase.ra.smpp.SmppResponse;

public interface BindResponse extends SmppResponse {

	/**
	 *	This method returns the system_id parameter of smpp response.This parameter
	 *	is used to identify an SMSC at the bind time.This contains the system-id of
	 *	the SMSC.
	 *
	 *	@return String syatem_id parameter of BindResponse.
	 *
	 *	@throws SmppResourceException -Incase no value is set for this parameter in 
	 *			response or there is any problem inretreiving this value.
	 */
	public String getSystemId() throws SmppResourceException;

	/**
	 *	This method returns the sc_interface_version parameter of smpp bind response.
	 *	This parameter is used to identify an SMSC at the bind time.This contains
	 *	the system-id of the SMSC.
  	 *
	 *	@return sc_interface_version parameter of smpp bind response.
	 *
	 *	@throws SmppResourceException -Incase no value is set for this parameter in 
	 *			response or there is any problem inretreiving this value.
	 */
	public byte getScInterfaceVersion() throws SmppResourceException;


}
