/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
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
package com.agnity.ain.exceptions;

/**
 * This exception will be thrown when operation not allowed.
 * @author Mriganka
 *
 */
public class OperationNotAllowedException extends Exception {
	/**
	 * default serial version Id
	 */	private static final long serialVersionUID = 1L;
	public OperationNotAllowedException(){}
	public OperationNotAllowedException(String msg){
		super(msg);
	}
}
