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
//      File:   CallBackNumberImpl.java
//
//      Desc:   This class implements  CallBackNumber interface which defines 
//				'Callback_num' Parameter of an SMPP message. 
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              14/02/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;
import java.io.Serializable;
import com.baypackets.ase.ra.smpp.CallBackNumber;

public class CallBackNumberImpl implements CallBackNumber,Serializable {

	private static Logger logger = Logger.getLogger(CallBackNumberImpl.class);
	private String callbackNum;
	private byte presInd;
	private String aTag;

	public CallBackNumberImpl(String num, byte preInd, String aTag) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside CallBackNumberImpl(String,byte,String) ");
		}
		this.callbackNum=num;
		this.presInd=preInd;
		this.aTag=aTag;
	}

	/**
	 * This method can be used by application to get 'callback_num' associated
	 * with CallBackNumberImpl parameter.
	 * 
	 *	@return String -'callback_num' associated with the CallBackNumberImpl object.
	 */
	public String getCallbackNum(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCallbackNum()");
		}
		return this.callbackNum;
	}

	/**
	 * 	This method can be used by application to get 'callback_num_pres_ind' 
	 *	associated with CallBackNumberImpl parameter.
	 * 
	 *	@return byte -'callback_num_pres_ind' associated with the CallBackNumberImpl
	 *					object.
	 */
	public byte getPresInd(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getPresInd()");
		}
		return this.presInd;
	}
	/**
	 * 	This method can be used by application to get 'callback_num_atag' 
	 *	associated with CallBackNumberImpl parameter.
	 * 
	 *	@return byte -'callback_num_atag' associated with the CallBackNumberImpl object.
	 */
	public String getAtag(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getAtag()");
		}
		return this.aTag;
	}

}
