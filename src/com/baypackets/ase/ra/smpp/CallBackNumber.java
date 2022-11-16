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
//      File:   CallBackNumber.java
//
//      Desc:   This interface defines a CallBackNumber Parameter of an SMPP message. 
//				Application can use this interface to make CallBackNumber parameter, 
//				which is to be used in creating SMPP requests.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp;

public interface CallBackNumber {
	
	/**
	 * This method can be used by application to get 'callback_num' associated
	 * with CallBackNumber parameter.
	 * 
	 *	@return String -'callback_num' associated with the CallBackNumber object.
	 */
	public String getCallbackNum();

	/**
	 * 	This method can be used by application to get 'callback_num_pres_ind' 
	 *	associated with CallBackNumber parameter.
	 * 
	 *	@return byte -'callback_num_pres_ind' associated with the CallBackNumber
	 *					object.
	 */

	public byte getPresInd();

	/**
	 * 	This method can be used by application to get 'callback_num_atag' 
	 *	associated with CallBackNumber parameter.
	 * 
	 *	@return String -'callback_num_atag' associated with the CallBackNumber object.
	 */
	public String getAtag();
}
