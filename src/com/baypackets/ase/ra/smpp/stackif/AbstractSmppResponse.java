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
//      File:   AbstractSmppResponse.java
//
//		Desc:	This class defines an SMPP response. This class implements SmppResponse
//				interface. This is the base class for all the SMPP responses and all the
//				SMPP responses must extend this. This class contains get methods to get
//				various fields of an SMPP response.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar				24/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;
import com.baypackets.ase.ra.smpp.SmppResponse;
import com.baypackets.ase.ra.smpp.SmppRequest;
import com.baypackets.ase.ra.smpp.SmppResourceException;
import com.baypackets.ase.ra.smpp.stackif.Constants;
import com.baypackets.ase.ra.smpp.impl.SmppSession;
import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptorImpl;

import com.baypackets.ase.dispatcher.Destination;

import com.baypackets.ase.spi.resource.AbstractMessage;
import com.baypackets.ase.spi.resource.SessionFactory;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.resource.ResourceException;


import com.baypackets.ase.resource.Request;

public class AbstractSmppResponse extends AbstractMessage implements SmppResponse {

	private static Logger logger = Logger.getLogger(AbstractSmppResponse.class);

	private SmppRequest m_request;
	private SmppSession m_session;
	private Destination m_destination=  null;
	private int m_type=-1;

	/**
	*  This method returns the command length of SMPP request.
	*
	*  @return command_length parameter
	*/
	public int getCommandLength() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandLength()");
		}
		throw new SmppResourceException("This operation is not allowed on this response");
	}

	/**
	*  This method returns the command id associated with this SMPP request.
	*
	*  @return command_id parameter
	*/
	public int getCommandId() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandId()");
		}
		throw new SmppResourceException("This operation is not allowed on this response");
	}

	/**
	*  This method returns the Command status of SMPP message. For SMPP requests this 
	*  should be null.
	*
	*  @return command_status paramemeter
	*/
	public int getCommandStatus() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandStatus()");
		}
		throw new SmppResourceException("This operation is not allowed on this response");
	}

	/**
	* This method returns the sequence number associated with the SMPP request.
	*
	* @return sequence_number parameter
	*/
	public int getSequenceNumber() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSequenceNumber()");
		}
		throw new SmppResourceException("This operation is not allowed on this response");
	}

	/**
	*  This method returns the message id associated with the response of this request.
	*  
	*/
	public String getMessageId() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getMessageId()");
		}
		throw new SmppResourceException("This operation is not allowed on this response");
	}

	/**
	*  This method returns list of all the optional parameters associated with the SMPP request.
	*
	*/
	public int[] getOptParamNames() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getOptParamNames()");
		}
		throw new SmppResourceException("This operation is not allowed on this response");
	}

	/**
	*  This method returns the value of a optional parameter.The key is the integer value 
	*  of this optional parameter as defiled in the SMPP specification version 3.4
	*
	*  @param key It is the Integer value corrosponding to a specific Parameter Name 
	*          as defined in SMPP specification verion 3.4.
	*  
	*  @throws SmppExceptionException if key provied does not map to any parameter name.
	*/
	public int getOptParam(int key) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getOptParam()");
		}
		throw new SmppResourceException("This operation is not allowed on this response");
	}

	/**
	 *	This method returns the object of QuerySmResp class if reponse if instance
	 *	of QuesrySMResp else returns null.This class defines query response received
	 *	from SMSC.Using this class object, application can get various fields associated 
	 *	with QuerySm reponse.
	 *
	 *	@return QuerySMResp QuerySM response.
	 */
	public QuerySMResp getQueryResponse() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getQueryResponse()");
		}
		throw new SmppResourceException("This operation is not allowed on this response");
	}

	/**
	 *   This method returns the "dpf_result" associated with the response.The 
	 *   "dpf_result" parameter is used in "data_sm_resp" to indicate if 
	 *   delivery pending flag(DPF) was set for a delivery failure of a short
	 *   message sent earlier in the Data request.This is available only with 
	 *   dataSM reqeust.
	 *  
	 *   @return "dpf_result" parameter associated with the response.
	 *
	 *   @throws SmppResourceException -If problem in getting this paramter.
	 */
	public byte getDpfResult() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDpfResult()");
		}
		throw new SmppResourceException("This operation is not allowed on this response");
	
	}

	public void setType(int type){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setType()");
		}
		this.m_type=type;
	}
	
	public void setProtocolSession(SmppSession session){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setProtocolSession(SmppSession) with "+session);
		}
		this.m_session=session;
	}

	public void setRequest(SmppRequest request){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setRequest(SmppRequest)");
		}
		this.m_request=request;
	}

	public Request getRequest(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getRequest()");
		}
		return this.m_request;
	}

	public String getMethod(){
		if(logger.isDebugEnabled()) {
			logger.debug("inside getMethod()");
		}
		// TODO
		// return Constants.METHOD;
		return null;
	}
	public SasProtocolSession getProtocolSession(boolean create) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getProtocolSession(boolean)");
		}
		if( (this.m_session == null) && create) {
			try {
				SessionFactory sf =
					SmppResourceAdaptorImpl.getResourceContext().getSessionFactory();
					this.m_session = (SmppSession)sf.createSession();
			} catch(ResourceException re) {
				//logger.error("Creating protocol session", re);
			}
		}
		return this.m_session;
	}   

	public SasProtocolSession getProtocolSession() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getProtocolSession() "+this.m_session);
		}
		return this.m_session;
	}

	public boolean isSecure() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside isSecure()");
		}
		return false;
	}  

	public String getProtocol() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getProtocol()");
		}
		return Constants.PROTOCOL;
	}

	public void setDestination(Object destination){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setDestination()");
		}
		if(m_destination==null)
			m_destination= new Destination();
		this.m_destination = (Destination)destination;
	}

	public Object getDestination(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDestination()");
		}
		return this.m_destination;
	}
}
