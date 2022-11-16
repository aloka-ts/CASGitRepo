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
//      File:   Address.java
//
//      Desc:   This interface defines a Address Parameter of an SMPP message. 
//				Application can use this interface to make Address parameter, 
//				which is to be used in creating SMPP requests.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
***********************************************************************************/

package com.baypackets.ase.ra.smpp;

public interface Address {
	
	/**
	 * This method can be used by application to create an Address parameter.
	 * 
	 *	@param ton -'type of number' associated with Address parameter.
	 *	@param npi -'numbering plan indicator' associated with Address parameter.
	 *	@param range -'range' associated with Address parameter.
	 *
	 */
	// TODO how to provide it to app. prob in compilation.
	//public Address(int ton, int npi, String range);

	/**
	 * This method can be used by application to get 'type of number' associated
	 * with Address parameter.
	 * 
	 *	@return int -'type of number' associated with the Address object.
	 */
	public int getTon();

	/**
	 * 	This method can be used by application to get 'numbering plan indicator' 
	 *	associated with Address parameter.
	 * 
	 *	@return int -'numbering plan indiacator' associated with the Address object.
	 */
	public int getNpi();

	/**
	 * 	This method can be used by application to get 'range' associated
	 * 	with Address parameter.
	 * 
	 *	@return String -'range' associated with the Address object.
	 */
	public String getRange();
}
