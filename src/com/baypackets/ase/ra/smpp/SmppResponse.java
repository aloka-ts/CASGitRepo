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
//      File:   SmppResponse.java
//
//		Desc:	This interface defines an SMPP response. This is the base class
//				for all the SMPP responses and all the SMPP responses must extend
//				this. This class contains get methods to get various fields of an
//				SMPP response. This interface describes in details all the APIs 
//				available to application.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar				24/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp;

import com.baypackets.ase.resource.Response;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.ra.smpp.stackif.QuerySMResp;

public interface SmppResponse extends Response {

	/**
	*  	This method returns the command length of SMPP request.
	*
	*  	@return command_length parameter
	*
	*	@throws SmppResourceException -If problem in getting this paramter.
	*/
	public int getCommandLength() throws SmppResourceException ;

	/**
	*  	This method returns the command id associated with this SMPP request.
	*
	*	@return command_id parameter
	*
	*	@throws SmppResourceException -If problem in getting this paramter.
	*/
	public int getCommandId() throws SmppResourceException ;

	/**
	*  	This method returns the Command status of SMPP message. For SMPP requests this 
	*  	should be null.
	*
	*  	@return command_status paramemeter
	*
	*	@throws SmppResourceException -If problem in getting this paramter.
	*/
	public int getCommandStatus() throws SmppResourceException ;

	/**
	* 	This method returns the sequence number associated with the SMPP response.
	*	This sequence number should be the same as mentioned in the SMPP request
	*	sent earlier to the SMSC.
	*
	* 	@return sequence_number parameter
	*
	*	@throws SmppResourceException -If problem in getting this paramter.
	*/
	public int getSequenceNumber() throws SmppResourceException ;

	/**
	* 	This method returns the message id associated with the response of this request.
	*	This message is assigned by SMSC, and can be used at a later stage to query,
	*	replace, or cancel the message sent earlier.
	*  
	* 	@return message_id parameter associated with the response.
	*
	*	@throws SmppResourceException -If problem in getting this paramter.
	*/
	public String getMessageId() throws SmppResourceException ;

	/**
	*  This method returns list of all the optional parameters associated with the
	*  SMPP response.
	*
	*  @return list of all the associated optional parameter.
	*
	*  @throws SmppResourceException -If problem in getting this paramter or this 
	*							operation is not allowed on this kind of response.
	*/
	public int[] getOptParamNames() throws SmppResourceException ;

	/**
	*  This method returns the value of a optional parameter.The key is the integer
	*  value of this optional parameter as defiled in the SMPP specification version
	*  3.4
	*
	*  @param key It is the Integer value corrosponding to a specific Parameter Name 
	*          	as defined in SMPP specification verion 3.4.
	*  
	*  @throws SmppExceptionException -if key provied does not map to any parameter name
	*							or this operation is not allowed on this kind of request.
	*/
	public int getOptParam(int key) throws SmppResourceException ;

	/**
	 *	This method returns the QuerySmResp object if reponse is an instance
	 *	of QuesrySMResp else returns null.This class defines query response received
	 *	from SMSC.Using this class object, application can get various fields associated 
	 *	with QuerySm reponse.
	 *
	 *	@return object of <code>QuerySMResp</code> response.
	 *
	 *  @throws SmppResourceException -If problem in getting this object or this 
	 *							operation is not allowed on this kind of response.
	 */
	public QuerySMResp getQueryResponse() throws SmppResourceException ;

	/**
	* 	This method returns the "dpf_result" associated with the response.The 
	*	"dpf_result" parameter is used in "data_sm_resp" to indicate if 
	*	delivery pending flag(DPF) was set for a delivery failure of a short
	*	message sent earlier in the Data request.This is available only with 
	*	dataSM reqeust.
	*  
	* 	@return "dpf_result" parameter associated with the response.
	*
	*	@throws SmppResourceException -If problem in getting this paramter.
	*/
	public byte getDpfResult() throws SmppResourceException ;
}
