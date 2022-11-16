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
//      File:   SmppResourceException.java
//
//      Desc:   This class defines SMPP RA specific exceptions.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              01/02/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp;

import com.baypackets.ase.resource.ResourceException;


public class SmppResourceException extends ResourceException {
	
	/**
	 *	This method creates a new <code>SmppResourceException</code>
	 *	object.
	 *
	 */
	public SmppResourceException() {
		super();
	}

	/**
	 *	This method creates a new <code>SmppResourceException</code>
	 *	object.
	 *
	 *	@param message	-Exception message to be set.
	 */
	public SmppResourceException(String message) {
		super(message);
	}

	/**
	 *	This method creates a new <code>SmppResourceException</code>
	 *	object.
	 *
	 *	@param message	-Exception message to be set.
	 *	@param cause	-Object of <code>Throwable</code> which defines 
	 *					the cause of the Exception.
	 */
	public SmppResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 *	This method creates a new <code>SmppResourceException</code>
	 *	object.
	 *
	 *	@param cause	-Object of <code>Throwable</code> which defines 
	 *					the cause of the Exception.
	 */
	public SmppResourceException(Throwable cause) {
		super(cause);
	}
}

