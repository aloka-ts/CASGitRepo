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
//     File:     NetworkMutex
//
//     Desc:    This interface represents the network level mutex object. 
//							Mutex can be acquired or released by invoking methods of this interface.  
//
//     Author       Date         Description
//    ---------------------------------------------------------
//     Ashish Kabra  27/09/07     Initial Creation
//
//*********************************************************************


package com.baypackets.ase.control;

/**
 * This interface represents the network level mutex object. 
 * Mutex can be acquired or released by invoking methods of this interface.
 * @author Ashish kabra
 */

public interface NetworkMutex {

	/** 
	 * This method will try to connect other members in 
	 * cluster on particular port. It throws  NetworkMutexException If it is 
	 * unable to create ServerSocket.
	 * @throws NetworkMutexException
	 */ 
	void acquireLock() throws NetworkMutexException;

	/** 
	 * This mehod is used to release Network mutex. 
	 * Network mutex can released by closing server socket
	 */
	void releaseLock();
}

