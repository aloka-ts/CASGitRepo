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
//      File:   SmppResourceFactoryImpl.java
//
//		Desc:	This interface defines Resource adaptor factory interface for SMPP. It 
//				provides various API to be used by application to perform various 
//				operations e.g. creating request etc.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              30/01/08        Initial Creation
//
/***********************************************************************************/


package com.baypackets.ase.ra.smpp.impl;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.container.ResourceContextImpl;

import org.apache.log4j.Logger;

import com.baypackets.ase.resource.ResourceSession;
import com.baypackets.ase.resource.Request;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.ra.smpp.SmppRequest;
import com.baypackets.ase.ra.smpp.SmppResourceFactory;
import com.baypackets.ase.ra.smpp.Address;
import com.baypackets.ase.ra.smpp.AddressRange;
import com.baypackets.ase.ra.smpp.stackif.AddressRangeImpl;
import com.baypackets.ase.ra.smpp.CallBackNumber;
import com.baypackets.ase.ra.smpp.WrongMultipleDestException;
import com.baypackets.ase.ra.smpp.stackif.*;
//import com.baypackets.ase.spi.resource.ResourceFactory;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.SessionFactory;
//import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;

public class SmppResourceFactoryImpl implements SmppResourceFactory {

	private static Logger logger =	Logger.getLogger(SmppResourceFactoryImpl.class);

	private SmppMessageFactory msgFactory;
	private ResourceContext context;
	private SessionFactory  sessionFactory ;//(SessionFactory) context.getSessionFactory();






	public void init(ResourceContext context) {
		logger.debug("init() is called.");
		this.context = context;
		if (context != null) {
			logger.debug("init(): get message factory.");
			this.msgFactory = SmppMessageFactoryImpl.getInstance();
			if (this.msgFactory == null) {
				logger.error("init(): null message factory.");
			}
			logger.debug("init(): get session factory from context.");
			this.sessionFactory = (SessionFactory)context.getSessionFactory();
			if (this.sessionFactory== null) {
				logger.error("init(): null session factory.");
			}
		} else {
			logger.error("init(): null context received");
		}
	}


//	private SmppResourceFactoryImpl(){
//
//	}
//
//	public static SmppResourceFactoryImpl getSmppResourceFactory(){
//		SmppResourceFactoryImpl smppResourceFactoryImpl = new SmppResourceFactoryImpl();
//		return smppResourceFactoryImpl;
//	}


	public ResourceSession createSession(SipApplicationSession appSession) throws ResourceException {
		if(appSession == null) {
			throw new IllegalArgumentException("Application Session cannot be NULL ");
		}
		SasProtocolSession session = sessionFactory.createSession();
		((SasApplicationSession)appSession).addProtocolSession(session);
		return (ResourceSession)session;
	}

	// create session for incoming request
	public ResourceSession createSession() throws ResourceException {

		SasProtocolSession session = sessionFactory.createSession();

		return (ResourceSession)session;
	}



	public Request createRequest(int type) throws ResourceException {
		//return this.createRequest(null, type);
		// TODO
		throw new ResourceException(" not allowed for SMPP requests");
	}

	/*
	public SmppRequest createRequest(SipApplicationSession appSession, int type) throws ResourceException {
		SasProtocolSession session = null;
		if (appSession != null) {
			session = sessionFactory.createSession();
			((SasApplicationSession)appSession).addProtocolSession(session);
		}
		SasMessage message = msgFactory.createRequest(session, type);
		message.setInitial(true);
		return (SmppRequest)message;
	}
	*/

	/**
	*  This method is used by application to create a Smpp Request,when it wants
	*  to associate the request with a pre-existing application session.It takes
	*  'source type of number','source numbering plan indicator','source address',
	*  'destination type of number', 'destination numbering plan indicator',
	*  'destination address' and 'short messages' as parameters.Whenever 
	*  application wants to send short message to one particular destination,
	*  application should use this method to create <code>/SmppRequest</code>
	*  and should call send() on this request.
	*  
	*  @param appSession <code>SipApplicationSession</code> to be associated with
	*						new request object.
	*  @param sourceTon -'type of numer' for source address.
	*  @param sourceNpi -'Numbering plan indicator' for source address.
	*  @param sourceAddr -'address' of source SME.
	*  @param destTon -'type of numer' for destination address.
	*  @param destNpi -'Numbering plan indicator' for destination address.
	*  @param destAddr -'address' of destination SME.
	*  @param message -'message' to be sent to destination SME.
	*
	*  @return <code>SmppRequest</code> object.
	*
	*  @throws ResourceException -if problem in creating <code>SmppRequest</code>
	*/
	public SmppRequest createRequest(SipApplicationSession appSession,
										byte sourceTon,
										byte sourceNpi,
										String sourceAddr,
										byte destTon,
										byte destNpi,
										String destAddr,
										String message) throws ResourceException {
			Address sourceAddress = this.createAddress(sourceTon,sourceNpi,sourceAddr);
			Address destAddress = this.createAddress(destTon,destNpi,destAddr);
			SmppSession session = (SmppSession)this.createSession(appSession);
			SasMessage request = (SasMessage)msgFactory.createRequest(session,
																		sourceAddress,
																		destAddress);	
			request.setInitial(true);
			((SmppRequest)request).setShortMessage(message);
			return (SmppRequest)request;
	}

	/**
	*  This method is used by application to create a Smpp Request, when it wants
	*  to associate the request with a pre-existing application session.It takes
	*  'source address', 'destination address' and 'short messages' as parameters.
	*  Whenever application wants to send short message to one particular destination
	*  address, application should use this method to create <code>/SmppRequest</code>
	*  and call send() on this request.
	*  
	*  @param appSession <code>SipApplicationSession</code> to be associated with
	*						new request object.
	*  @param sourceAddr <code>Address</code> object associated with the 
	*                      originating SME.
	*  @param destAddr <code>Address</code> object associated with the 
	*                      destination SME.
	*
	*  @return <code>SmppRequest</code> object.
	*
	*  @throws ResourceException -if problem in creating <code>SmppRequest</code>
	*/
	public SmppRequest createRequest(SipApplicationSession appSession,
										Address sourceAddr,
										Address destAddr,
										String message) throws ResourceException {
			SmppSession session = (SmppSession)this.createSession(appSession);
			SasMessage request = (SasMessage)msgFactory.createRequest(session,sourceAddr,destAddr);	
			request.setInitial(false);
			((SmppRequest)request).setShortMessage(message);
			return (SmppRequest)request;
	}





	//create request method for incoming request.









	public SmppRequest createRequest( Address sourceAddr,
									 Address destAddr,
									 String message) throws ResourceException {
		SmppSession session = (SmppSession)this.createSession();
		SasMessage request = (SasMessage)msgFactory.createRequest(session,sourceAddr,destAddr);
		request.setInitial(false);
		((SmppRequest)request).setShortMessage(message);
		return (SmppRequest)request;
	}

	/**
	*  This method is used by application to create a Smpp Request, when it wants
	*  to associate the request with a pre-existing application session.It takes 
	*  'source address', 'destination address' list and 'short messages' as 
	*  parameters.Whenever application wants to send short message to more than 
	*  one destination addressees,application should use this method to create
	*  an <code>/SmppRequest</code> and call send() on this request.
	*  
	*  @param appSession <code>SipApplicationSession</code> to be associated with
	*						new request object.
	*  @param sourceAddr <code>Address</code> object associated with the 
	*                      originating SME.
	*  @param destAddr Array of <code>Address</code> objects associated with the 
	*                      destination SME, where application wants to send the SMS.
	*
	*  @return <code>SmppRequest</code> object.
	*
	*  @throws ResourceException -if problem in creating <code>SmppRequest</code>
	*  @throws WrongMultipleDestException -If all the address in destination address 
	*                                      array does not belong to same address-range
	*                                      of any SMSC which is configured with SMPP RA.
	*/
	public SmppRequest createRequest(SipApplicationSession appSession,
										Address sourceAddr,
										Address[] destAddr, 
										String message) 
										throws ResourceException,
										WrongMultipleDestException{
			SmppSession session = (SmppSession)this.createSession(appSession);
			SasMessage request = (SasMessage)msgFactory.createRequest(session,sourceAddr,destAddr);	
			request.setInitial(true);
			((SmppRequest)request).setShortMessage(message);
			return (SmppRequest)request;
	}



	/**
	 * 	This method is used by application to create a Smpp Request,when it wants
	 *	to associate the request with a pre-existing application session.It takes
	 *	'source type of number','source numbering plan indicator','source address',
	 *	'destination type of number', 'destination numbering plan indicator',
	 *	'destination address' and 'data' as parameters.Whenever 
	 *	application wants to send data (byte[])to one particular destination,
	 *	application should use this method to create <code>/SmppRequest</code>
	 *	and should call send() on this request.
	 *	
	 *	@param appSession <code>SipApplicationSession</code> to be associated with
	 * 						new request object.
	 * 	@param sourceTon -'type of numer' for source address.
	 *	@param sourceNpi -'Numbering plan indicator' for source address.
	 *	@param sourceAddr -'address' of source SME.
	 * 	@param destTon -'type of numer' for destination address.
	 *	@param destNpi -'Numbering plan indicator' for destination address.
	 *	@param destAddr -'address' of destination SME.
	 *	@param data -'data' to be sent to destination SME.
	 *
	 *	@return <code>SmppRequest</code> object.
	 *
	 *	@throws ResourceException -if problem in creating <code>SmppRequest</code>
	 */
	public SmppRequest createRequest(SipApplicationSession appSession,
										byte sourceTon,
										byte sourceNpi,
										String sourceAddr,
										byte destTon,
										byte destNpi,
										String destAddr,
										byte[] data)throws ResourceException {
			Address sourceAddress = this.createAddress(sourceTon,sourceNpi,sourceAddr);
			Address destAddress = this.createAddress(destTon,destNpi,destAddr);
			SmppSession session = (SmppSession)this.createSession(appSession);
			SasMessage request = (SasMessage)msgFactory.createRequest(session,
																		sourceAddress,
																		destAddress);	
			request.setInitial(true);
			short msgPaylod = 0x0424;
			((SmppRequest)request).addOptionalParameter(msgPaylod,data);
			return (SmppRequest)request;
	}

	//Created by Prashant
	public SmppRequest createRequest(
									 byte sourceTon,
									 byte sourceNpi,
									 String sourceAddr,
									 byte destTon,
									 byte destNpi,
									 String destAddr,
									 byte[] data)throws ResourceException {
		Address sourceAddress = this.createAddress(sourceTon,sourceNpi,sourceAddr);
		Address destAddress = this.createAddress(destTon,destNpi,destAddr);
		SmppSession session =   (SmppSession) sessionFactory.createSession();
		SasMessage request = (SasMessage)msgFactory.createRequest(session,
				sourceAddress,
				destAddress);
		request.setInitial(true);
		short msgPaylod = 0x0424;
		((SmppRequest)request).addOptionalParameter(msgPaylod,data);
		return (SmppRequest)request;
	}

	/**
	 *	This method is used by application to create a Smpp Request, when it wants
	 *	to associate the request with a pre-existing application session.It takes
	 *	'source address', 'destination address' and 'data' as parameters.Whenever 
	 *	application wants to send data(byte[]) to one particular destination
	 *	address, application should use this method to create <code>/SmppRequest</code>
	 *	and call send() on this request.
	 *	
	 *	@param appSession <code>SipApplicationSession</code> to be associated with
	 *						new request object.
	 *	@param sourceAddr <code>Address</code> object associated with the 
	 *						originating SME.
	 *	@param destAddr <code>Address</code> object associated with the 
	 *						destination SME.
	 *
	 *	@return <code>SmppRequest</code> object.
	 *
	 *	@throws ResourceException -if problem in creating <code>SmppRequest</code>
	 */

	public SmppRequest createRequest(SipApplicationSession appSession,
										Address sourceAddr,
										Address destAddr,
										byte[] data)throws ResourceException {
			SmppSession session = (SmppSession)this.createSession(appSession);
			SasMessage request = (SasMessage)msgFactory.createRequest(session,sourceAddr,destAddr);	
			request.setInitial(true);
			short msgPaylod = 0x0424;
			((SmppRequest)request).addOptionalParameter(msgPaylod,data);
			return (SmppRequest)request;
	}

	/**
	 *	This method is used by application to create a Smpp Request, when it wants
	 *	to associate the request with a pre-existing application session.It takes 
	 *	'source address', 'destination address' list and 'data(byte[])' as 
	 *	parameters.Whenever application wants to send data (byte[]) to more than 
	 *	one destination addressees,application should use this method to create
	 *	an <code>/SmppRequest</code> and call send() on this request.
	 *	
	 *	@param appSession <code>SipApplicationSession</code> to be associated with
	 						new request object.
	 *	@param sourceAddr <code>Address</code> object associated with the 
	 *						originating SME.
	 *	@param destAddr Array of <code>Address</code> objects associated with the 
	 *						destination SME, where application wants to send the SMS.
	 *
	 *	@return <code>SmppRequest</code> object.
	 *
	 *	@throws ResourceException -if problem in creating <code>SmppRequest</code>
	 *	@throws WrongMultipleDestException -If all the address in destination address 
	 *										array does not belong to same address-range
	 *										of any SMSC which is configured with SMPP RA.
	 */
	public SmppRequest createRequest(SipApplicationSession appSession,
										Address sourceAddr,
										Address[] destAddr, 
										byte[] data)
										throws ResourceException,
										WrongMultipleDestException {
			SmppSession session = (SmppSession)this.createSession(appSession);
			SasMessage request = (SasMessage)msgFactory.createRequest(session,sourceAddr,destAddr);	
			request.setInitial(true);
			short msgPaylod = 0x0424;
			((SmppRequest)request).addOptionalParameter(msgPaylod,data);
			return (SmppRequest)request;
	}

	/**
	*  This method can be used by application to create an Address object for a
	*  particular 'type of number', 'numbering plan indicator, and 'address'.SMPP
	*  RA factory takes all these values as parameter, creates a new Address object
	*  and returns it to application.
	*  
	*  @param ton -'type of number' associated with <code>Address</code> parameter.
	*  @param npi -'numbering plan indicator' associated with </code>Address</code> parameter.
	*  @param range -'range' associated with <code>Address</code> parameter.
	*
	*  @return <code>Address</code> object.
	*
	*  @throws ResourceException -if problem in creating <code>Address</code>
	*/
	public Address createAddress(byte ton, byte npi, String Addr) throws ResourceException{
		return new AddressImpl(ton,npi,Addr);
	}

	/**
	*  This method can be used by application to create an AddressRange object for a
	*  particular 'type of number', 'numbering plan indicator, and 'range'.SMPP
	*  RA factory takes all these values as parameter, creates a new AddressRange object
	*  and returns it to application.
	*  
	*  @param ton -'type of number' associated with <code>AddressRange</code> parameter.
	*  @param npi -'numbering plan indicator' associated with </code>AddressRange</code> parameter.
	*  @param range -'range' associated with <code>AddressRange</code> parameter.
	*
	*  @return <code>AddressRange</code> object.
	*
	*  @throws ResourceException -if problem in creating <code>Address</code>
	*/
	public AddressRange createAddressRange(byte ton, byte npi, String range) throws ResourceException{
		return new AddressRangeImpl(ton,npi,range);
	}

	/**
	*  This method is used by application to create a Smpp 'data Request',when it 
	*  wants to associate the request with a pre-existing application session.It
	*  takes 'source type of number','source numbering plan indicator','source address',
	*  'destination type of number', 'destination numbering plan indicator',
	*  'destination address' and 'short messages' as parameters.Whenever application
	*  wants to send short message to one particular destination using 'Data Request',
	*  this method can be used by application.
	*  
	*  @param appSession <code>SipApplicationSession</code> to be associated with
	*						new request object.
	*  @param sourceTon -'type of numer' for source address.
	*  @param sourceNpi -'Numbering plan indicator' for source address.
	*  @param sourceAddr -'address' of source SME.
	*  @param destTon -'type of numer' for destination address.
	*  @param destNpi -'Numbering plan indicator' for destination address.
	*  @param destAddr -'address' of destination SME.
	*  @param message -'message' to be sent to destination SME.
	*
	*  @return <code>SmppRequest</code> object.
	*
	*  @throws ResourceException -if problem in creating <code>SmppRequest</code>
	*/
	public SmppRequest createDataRequest(SipApplicationSession appSession,
											byte sourceTon,
											byte sourceNpi, 
											String sourceAddr, 
											byte destTon, 
											byte destNpi,
											String destAddr, 
											String message) throws ResourceException {
			if(logger.isDebugEnabled()){
				logger.debug("Inside createDataRequest().");
			}
			Address sourceAddress = this.createAddress(sourceTon,sourceNpi,sourceAddr);
			Address destAddress = this.createAddress(destTon,destNpi,destAddr);
			SmppSession session = (SmppSession)this.createSession(appSession);
			SasMessage request = (SasMessage)msgFactory.createDataRequest(session,sourceAddress,destAddress);	
			request.setInitial(true);
			byte[] data = message.getBytes();
			short paramName = Constants.OPT_PAR_MSG_PAYLOAD;
			((SmppRequest)request).addOptionalParameter(paramName,data);
			if(logger.isDebugEnabled()){
				logger.debug("Leaving createDataRequest().");
			}
			return (SmppRequest)request;
	}
	
	/**
	*  This method is used by application to create a Smpp 'Data Request', when it
	*  wants to associate the request with a pre-existing application session.It
	*  takes 'source address','destination address' and 'short messages' as 
	*  parameters.Whenever application wants to send short message to one 
	*  particular destination address using 'Data Request', this method can be
	*  used by application.
	*
	*  @param appSession <code>SipApplicationSession</code> to be associated with
	*						new request object.
	*  @param sourceAddr <code>Address</code> object associated with the 
	*                      originating SME.
	*  @param destAddr <code>Address</code> object associated with the 
	*                      destination SME.
	*
	*  @return <code>SmppRequest</code> object.
	*
	*  @throws ResourceException -if problem in creating <code>SmppRequest</code>
	*/
	public SmppRequest createDataRequest(SipApplicationSession appSession,
										Address sourceAddr,
										Address destAddr, 
										String message) throws ResourceException {
			if(logger.isDebugEnabled()){
				logger.debug("Inside createDataRequest(appSession,sourceAddr,destAddr,message).");
			}
			SmppSession session = (SmppSession)this.createSession(appSession);
			SasMessage request = (SasMessage)msgFactory.createDataRequest(session,sourceAddr,destAddr);	
			request.setInitial(true);
			byte[] data = message.getBytes();
			short paramName = Constants.OPT_PAR_MSG_PAYLOAD;
			((SmppRequest)request).addOptionalParameter(paramName,data);
			return (SmppRequest)request;
	}

	/**
	 *	This method is used by application to create a Smpp 'data Request',when it 
	 *	wants to associate the request with a pre-existing application session.It
	 *	takes 'source type of number','source numbering plan indicator','source address',
	 *	'destination type of number', 'destination numbering plan indicator',
	 *	'destination address' and 'data (byte[])' as parameters.Whenever application
	 *	wants to send data (byte[]) to one particular destination using 'Data Request',
	 *	this method can be used by application.
	 *	
	 *	@param appSession <code>SipApplicationSession</code> to be associated with
	 						new request object.
	 * 	@param sourceTon -'type of numer' for source address.
	 *	@param sourceNpi -'Numbering plan indicator' for source address.
	 *	@param sourceAddr -'address' of source SME.
	 * 	@param destTon -'type of numer' for destination address.
	 *	@param destNpi -'Numbering plan indicator' for destination address.
	 *	@param destAddr -'address' of destination SME.
	 *	@param data -'data(byte[])' to be sent to destination SME.
	 *
	 *	@return <code>SmppRequest</code> object.
	 *
	 *	@throws ResourceException -if problem in creating <code>SmppRequest</code>
	*/
	public SmppRequest createDataRequest(SipApplicationSession appSession,
											byte sourceTon,
											byte sourceNpi, 
											String sourceAddr, 
											byte destTon, 
											byte destNpi,
											String destAddr, 
											byte[] data)throws ResourceException {
			if(logger.isDebugEnabled()){
				logger.debug("Inside createDataRequest for data[]");
			}
			Address sourceAddress = this.createAddress(sourceTon,sourceNpi,sourceAddr);
			Address destAddress = this.createAddress(destTon,destNpi,destAddr);
			SmppSession session = (SmppSession)this.createSession(appSession);
			SasMessage request = (SasMessage)msgFactory.createDataRequest(session,sourceAddress,destAddress);	
			request.setInitial(true);
			short msgPaylod = Constants.OPT_PAR_MSG_PAYLOAD;
			((SmppRequest)request).addOptionalParameter(msgPaylod,data);
			if(logger.isDebugEnabled()){
				logger.debug("Leaving createDataRequest for data[]");
			}
			return (SmppRequest)request;
	}

	/**
	 *	This method is used by application to create a Smpp 'Data Request', when it
	 *	wants to associate the request with a pre-existing application session.It
	 *	takes 'source address','destination address' and 'data (byte[])' as 
	 *	parameters.Whenever application wants to send data (byte[]) to one 
	 *	particular destination address using 'Data Request', this method can be
	 *	used by application.
	 *
	 *	@param appSession <code>SipApplicationSession</code> to be associated with
	 						new request object.
	 *	@param sourceAddr <code>Address</code> object associated with the 
	 *						originating SME.
	 *	@param destAddr <code>Address</code> object associated with the 
	 *						destination SME.
	 *	@param data -'data(byte[])' to be sent to destination SME.
	 *
	 *	@return <code>SmppRequest</code> object.
	 *
	 *	@throws ResourceException -if problem in creating <code>SmppRequest</code>
	 */
	public SmppRequest createDataRequest(SipApplicationSession appSession,
										Address sourceAddr,
										Address destAddr, 
										byte[] data)throws ResourceException {
			SmppSession session = (SmppSession)this.createSession(appSession);
			SasMessage request = (SasMessage)msgFactory.createDataRequest(session,sourceAddr,destAddr);	
			request.setInitial(true);
			short msgPaylod = 0x0424;
			((SmppRequest)request).addOptionalParameter(msgPaylod,data);
			return (SmppRequest)request;
	}


	/**
	*  This method is used by application to register the range of SMEs is serves,
	*  with the container.Any Incoming SMPP request from network which falls in
	*  this range, will be routed to this applicaition by the container.
	*
	*  @param range <code>AddressRange</code> which this application will serve.
	*  @param appName -name of the application.
	*
	*  @return boolean - true if registration was successful, else false.
	*/
	public boolean registerEsme(AddressRange range,String appName) {
		return SmppResourceAdaptorImpl.registerAppRange(range,appName);	
	}

	/**
	 *  This method can be used by application to create an CallBackNumber object 
	 *  for a particular 'callback_num', 'callback_num_pres_ind', and 
	 *  'callback_num_atag'.SMPP RA factory takes all these values as parameter, 
	 *  creates a new Address object and returns it to application.
	 *  
	 *  @param callbackNum -'callback_num' associated with <code>CallBackNumber</code> 
	 *                      parameter.
	 *  @param presInd -'callback_num_pres_ind' associated with 
	 *                  </code>CallBackNumber</code> parameter.
	 *  @param aTag -'callback_num_atag' associated with <code>CallBackNumber</code>
	 *              parameter.
	 *  @return <code>CallBackNumber</code> object.
	 *
	 *  @throws ResourceException -if problem in creating <code>CallBackNumber</code>
	 */
	 public CallBackNumber createCallBackNumber(String callbackNum, 
	 											byte presInd, 
												String aTag) 
												throws ResourceException {

	 	return new CallBackNumberImpl(callbackNum,presInd,aTag);
	}

}
