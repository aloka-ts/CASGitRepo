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
//      File:   Smsc.java
//
//      Desc:   This interface defines an Smsc Simulator.This class contains
//				set ans get methods for name,IP,port,mode and range of an 
//				SMSC. 
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp;

import java.util.ArrayList;

public interface Smsc {

	/**
	 * 	This method returns the 'name' associated with SMSC.
	 *
	 *	@return -'name' associated with SMSC.
	 */
	public String getName();

	/**
	 * 	This method returns the 'IP addres' of the SMSC.
	 *
	 *	@return -'IP Address' associated with SMSC.
	 */
	public String getIpAddr();

	/**
	 * 	This method returns the 'port' associated with SMSC.
	 *
	 *	@return -'port' associated with SMSC.
	 */
	public int getPort();

	/**
	 * 	This method returns the 'mode' associated with SMSC.
	 *
	 *	@return -'mode' associated with SMSC.
	 */
	public String getMode();

	/**
	 * 	This method returns the 'address-range' associated with SMSC.
	 *
	 *	@return - list of 'address-range' associated with SMSC.
	 */
	public ArrayList getAddressRange();
	
	/**
	 *	This method returns the name,ipaddress,port,mode,and range of 
	 *	this SMSC in String form.
	 */
	public String toString();
}

