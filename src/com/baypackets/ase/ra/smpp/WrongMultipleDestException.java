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
//      File:   WrongMultipleDestException.java
//
//      Desc:   This class defines SMPP specific exception. In case of multiple 
//				destinations, if all the addresses do not fall in the address range
//				of any of the SMSCs configured, or fall in the address range of 
//				two or more than two SMSCs, this exception is thrown
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
//***********************************************************************************/

package com.baypackets.ase.ra.smpp;

import com.baypackets.ase.resource.ResourceException;


public class WrongMultipleDestException extends ResourceException {

	/**
	 *  This method creates a new <code>WrongMultipleDestException(</code>
	 *  object.
	 *
	 */
	public WrongMultipleDestException() {
		super();
	}

	/**
	 *  This method creates a new <code>WrongMultipleDestException(</code>
	 *  object.
	 *
	 *  @param message  -Exception message to be set.
	 */
	public WrongMultipleDestException(String message) {
		super(message);
	}

	/**
	 *  This method creates a new <code>WrongMultipleDestException(</code>
	 *  object.
	 *
	 *  @param message  -Exception message to be set.
	 *  @param cause    -Object of <code>Throwable</code> which defines 
	 *                  the cause of the Exception.
	 */
	public WrongMultipleDestException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 *  This method creates a new <code>WrongMultipleDestException(</code>
	 *  object.
	 *
	 *  @param cause    -Object of <code>Throwable</code> which defines 
	 *                  the cause of the Exception.
	 */
	public WrongMultipleDestException(Throwable cause) {
		super(cause);
	}
}

