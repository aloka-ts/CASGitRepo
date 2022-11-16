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
//      File:   AddressRange.java
//
//      Desc:   This interface defines a AddressRange Parameter of an SMPP message. 
//              Application can use this interface to set various fields of an 
//				AddressRange parameter.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
/***********************************************************************************/


package com.baypackets.ase.ra.smpp;

public interface AddressRange {

	/**
	*	This method can be used by application to associate 'type of number' 
	*	with AddressRange parameter.
	* 
	*	@param ton -'type of number' to be associate to <code>SmppRequest</code>.
	*/
	public void setTon(int ton);

	/**
	*	This method can be used by application to associate 'type of number' 
	*	with AddressRange parameter.
	* 
	*	@param npi -'numbering plan indicator' to be associate to 
	*				<code>SmppRequest</code>.
	*/
	public void setNpi(int npi);

	/**
	*	This method can be used by application to associate 'type of number' 
	*	with AddressRange parameter.
	* 
	*	@param range -'range' to be associate to <code>SmppRequest</code>.
	*/
	public void setRange(String range);

	/**
	* This method can be used by application to get 'type of number' associated
	* with AddressRange parameter.
	* 
	*  @return int -'type of number' associated with the AddressRange object.
	*/
	public int getTon();

	/**
	* This method can be used by application to get 'type of number' associated
	* with AddressRange parameter.
	* 
	*  @return int -'numbering plan indicator' associated with the AddressRange object.
	*/
	public int getNpi();

	/**
	* This method can be used by application to get 'type of number' associated
	* with AddressRange parameter.
	* 
	*  @return int -'range' associated with the AddressRange object.
	*/
	public String getRange();
}
