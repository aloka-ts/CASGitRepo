//*********************************************************************
//   GENBAND, Inc. Confidential and Proprietary
//
// This work contains valuable confidential and proprietary 
// information.
// Disclosure, use or reproduction without the written authorization of
// GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
// is protected by the laws of the United States and other countries.
// If publication of the work should occur the following notice shall 
// apply:
// 
// "Copyright 2007 GENBAND, Inc.  All rights reserved."
//*********************************************************************

//**********************************************************************
//
//     File:     NetworkMutexException.java
//
//     Desc:     This class contains NetworkMutexException class
//
//
//     Author       Date         Description
//    ---------------------------------------------------------
//     Ashish Kabra  27/09/07     Initial Creation
//
//*********************************************************************

/**
 * This class is used for throwing exception wherever lock acquiring 
 * process is unsuccessful.	
 * @author Ashish kabra
 */
package com.baypackets.ase.control;

public class NetworkMutexException extends Exception { 

	/** 
	 * default constructor
	 */
	public NetworkMutexException() {
	}

	/** 
	 * This constructor takes string as input
	 * @param desc description of exception.
	 */
	public NetworkMutexException(String desc) {
		super(desc);
	}

}
	
