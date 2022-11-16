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
//      File:   SmppResourceFactory.java
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


package com.baypackets.ase.ra.smpp;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.resource.DefaultResourceFactory;
import com.baypackets.ase.resource.ResourceException;

public interface SmppResourceFactory extends DefaultResourceFactory {

	/**
	 * 	This method is used by application to create a Smpp Request,when it wants
	 *	to associate the request with a pre-existing application session.It takes
	 *	'source type of number','source numbering plan indicator','source address',
	 *	'destination type of number', 'destination numbering plan indicator',
	 *	'destination address' and 'short messages' as parameters.Whenever 
	 *	application wants to send short message to one particular destination,
	 *	application should use this method to create <code>/SmppRequest</code>
	 *	and should call send() on this request
	 *
	 *	This method setes the default message mode in the request.To set 
	 *	any other message mode use setMessageMode() API present in 
	 *	<code>SmppRequest</code>.
	 *	
	 *	@param appSession <code>SipApplicationSession</code> to be associated with
	 						new request object.
	 * 	@param sourceTon -'type of numer' for source address.
	 *	@param sourceNpi -'Numbering plan indicator' for source address.
	 *	@param sourceAddr -'address' of source SME.
	 * 	@param destTon -'type of numer' for destination address.
	 *	@param destNpi -'Numbering plan indicator' for destination address.
	 *	@param destAddr -'address' of destination SME.
	 *	@param message -'message' to be sent to destination SME.
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
									 String message)throws ResourceException;

	/**
	 *	This method is used by application to create a Smpp Request,when it wants
	 *	to associate the request with a pre-existing application session.It takes
	 *	'source address','destination address' and 'short messages' as 
	 *	parameters.Whenever application wants to send short message to one 
	 *	particular destination address, application should use this method 
	 *	to create <code>/SmppRequest</code>	and call send() on this request
	 *
	 *	This method setes the default message mode in the request.To set 
	 *	any other message mode use setMessageMode() API present in 
	 *	<code>SmppRequest</code>.
	 *	
	 *	@param appSession <code>SipApplicationSession</code> to be associated with
	 						new request object.
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
										String message)throws ResourceException;

	/**
	 *	This method is used by application to create a Smpp Request, when it wants
	 *	to associate the request with a pre-existing application session.It takes 
	 *	'source address', 'destination address' list and 'short messages' as 
	 *	parameters.Whenever application wants to send short message to more than 
	 *	one destination addressees,application should use this method to create
	 *	an <code>/SmppRequest</code> and call send() on this request
	 *
	 *	This method setes the default message mode in the request.To set 
	 *	any other message mode use setMessageMode() API present in 
	 *	<code>SmppRequest</code>.
	 *	
	 *	@param appSession <code>SipApplicationSession</code> to be associated with
	 *						new request object.
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
										String message)
										throws ResourceException,
										WrongMultipleDestException;

	/**
	 * 	This method is used by application to create a Smpp Request,when it wants
	 *	to associate the request with a pre-existing application session.It takes
	 *	'source type of number','source numbering plan indicator','source address',
	 *	'destination type of number', 'destination numbering plan indicator',
	 *	'destination address' and 'data' as parameters.Whenever 
	 *	application wants to send data (byte[])to one particular destination,
	 *	application should use this method to create <code>/SmppRequest</code>
	 *	and should call send() on this request
	 *
	 *	This method setes the default message mode in the request.To set 
	 *	any other message mode use setMessageMode() API present in 
	 *	<code>SmppRequest</code>.
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
										byte[] data)throws ResourceException;

	/**
	 *	This method is used by application to create a Smpp Request, when it wants
	 *	to associate the request with a pre-existing application session.It takes
	 *	'source address', 'destination address' and 'data' as parameters.Whenever 
	 *	application wants to send data(byte[]) to one particular destination
	 *	address, application should use this method to create <code>/SmppRequest</code>
	 *	and call send() on this request
	 *	
	 *	This method setes the default message mode in the request.To set 
	 *	any other message mode use setMessageMode() API present in 
	 *	<code>SmppRequest</code>.
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
										byte[] data)throws ResourceException;

	/**
	 *	This method is used by application to create a Smpp Request, when it wants
	 *	to associate the request with a pre-existing application session.It takes 
	 *	'source address', 'destination address' list and 'data(byte[])' as 
	 *	parameters.Whenever application wants to send data (byte[]) to more than 
	 *	one destination addressees,application should use this method to create
	 *	an <code>/SmppRequest</code> and call send() on this request
	 *	
	 *	This method setes the default message mode in the request.To set 
	 *	any other message mode use setMessageMode() API present in 
	 *	<code>SmppRequest</code>.
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
										byte[] data) throws ResourceException,  WrongMultipleDestException;

	/**
	 *	This method can be used by application to create an Address object for a
	 *	particular 'type of number', 'numbering plan indicator, and 'address'.SMPP
	 *	RA factory takes all these values as parameter, creates a new Address object
	 *	and returns it to application.
	 *	
	 *  @param ton -'type of number' associated with <code>Address</code> parameter.
	 *  @param npi -'numbering plan indicator' associated with </code>Address</code> parameter.
	 *  @param range -'range' associated with <code>Address</code> parameter.
	 *
	 *	@return <code>Address</code> object.
	 *
	 *	@throws ResourceException -if problem in creating <code>Address</code>
	 */
	public Address createAddress(byte ton, byte npi, String Addr) 
													throws ResourceException;

	/**
	 *	This method can be used by application to create an <code>AddressRange</code> object for a
	 *	particular 'type of number', 'numbering plan indicator, and 'address-range'.SMPP
	 *	RA factory takes all these values as parameter, creates a new AddressRange object
	 *	and returns it to application.
	 *	
	 *  @param ton -'type of number' associated with <code>AddressRange</code> parameter.
	 *  @param npi -'numbering plan indicator' associated with </code>AddressRange</code> parameter.
	 *  @param range -'range' associated with <code>AddressRange</code> parameter.
	 *
	 *	@return <code>AddressRange</code> object.
	 *
	 *	@throws ResourceException -if problem in creating <code>Address</code>
	 */
	public AddressRange createAddressRange(byte ton, byte npi, String range) 
													throws ResourceException;

	/**
	 *	This method is used by application to create a Smpp 'data Request',when it 
	 *	wants to associate the request with a pre-existing application session.It
	 *	takes 'source type of number','source numbering plan indicator','source address',
	 *	'destination type of number', 'destination numbering plan indicator',
	 *	'destination address' and 'short messages' as parameters.Whenever application
	 *	wants to send short message to one particular destination using 'Data Request',
	 *	this method can be used by application
	 *	
	 *	This method setes the default message mode in the request.To set 
	 *	any other message mode use setMessageMode() API present in 
	 *	<code>SmppRequest</code>.
	 *	
	 *	@param appSession <code>SipApplicationSession</code> to be associated with
	 						new request object.
	 * 	@param sourceTon -'type of numer' for source address.
	 *	@param sourceNpi -'Numbering plan indicator' for source address.
	 *	@param sourceAddr -'address' of source SME.
	 * 	@param destTon -'type of numer' for destination address.
	 *	@param destNpi -'Numbering plan indicator' for destination address.
	 *	@param destAddr -'address' of destination SME.
	 *	@param message -'message' to be sent to destination SME.
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
											String message)throws ResourceException;
	/**
	 *	This method is used by application to create a Smpp 'Data Request', when it
	 *	wants to associate the request with a pre-existing application session.It
	 *	takes 'source address','destination address' and 'short messages' as 
	 *	parameters.Whenever application wants to send short message to one 
	 *	particular destination address using 'Data Request', this method can be
	 *	used by application
	 *	
	 *	This method setes the default message mode in the request.To set 
	 *	any other message mode use setMessageMode() API present in 
	 *	<code>SmppRequest</code>.
	 *
	 *	@param appSession <code>SipApplicationSession</code> to be associated with
	 						new request object.
	 *	@param sourceAddr <code>Address</code> object associated with the 
	 *						originating SME.
	 *	@param destAddr <code>Address</code> object associated with the 
	 *						destination SME.
	 *	@param message -'short message' to be sent to destination SME.
	 *
	 *	@return <code>SmppRequest</code> object.
	 *
	 *	@throws ResourceException -if problem in creating <code>SmppRequest</code>
	 */
	public SmppRequest createDataRequest(SipApplicationSession appSession,
										Address sourceAddr,
										Address destAddr, 
										String message)throws ResourceException;

	/**
	 *	This method is used by application to create a Smpp 'data Request',when it 
	 *	wants to associate the request with a pre-existing application session.It
	 *	takes 'source type of number','source numbering plan indicator','source address',
	 *	'destination type of number', 'destination numbering plan indicator',
	 *	'destination address' and 'data (byte[])' as parameters.Whenever application
	 *	wants to send data (byte[]) to one particular destination using 'Data Request',
	 *	this method can be used by application
	 *	
	 *	This method setes the default message mode in the request.To set 
	 *	any other message mode use setMessageMode() API present in 
	 *	<code>SmppRequest</code>.
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
											byte[] data)throws ResourceException;
	/**
	 *	This method is used by application to create a Smpp 'Data Request', when it
	 *	wants to associate the request with a pre-existing application session.It
	 *	takes 'source address','destination address' and 'data (byte[])' as 
	 *	parameters.Whenever application wants to send data (byte[]) to one 
	 *	particular destination address using 'Data Request', this method can be
	 *	used by application
	 *	
	 *	This method setes the default message mode in the request.To set 
	 *	any other message mode use setMessageMode() API present in 
	 *	<code>SmppRequest</code>.
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
										byte[] data)throws ResourceException;


	/**
	 * 	This method is used by application to register the range of SMEs is serves,
	 * 	with the container.Any Incoming SMPP request from network which falls in
	 * 	this range, will be routed to this applicaition by the container.
	 *
	 * 	@param range <code>AddressRange</code> which this application will serve.
	 *	@param appName -name of the application.
	 *
	 * 	@return boolean - true if registration was successful, else false.
	 */

	public boolean registerEsme(AddressRange range,String appName);
	 
	/**
	 *	This method can be used by application to create an CallBackNumber object 
	 *	for a particular 'callback_num', 'callback_num_pres_ind', and 
	 *	'callback_num_atag'.SMPP RA factory takes all these values as parameter, 
	 *	creates a new Address object and returns it to application.
	 *	
	 *  @param callbackNum -'callback_num' associated with <code>CallBackNumber</code> 
	 *						parameter.
	 *  @param presInd -'callback_num_pres_ind' associated with 
	 *					</code>CallBackNumber</code> parameter.
	 *  @param aTag -'callback_num_atag' associated with <code>CallBackNumber</code>
	 *				parameter.
	 *	@return <code>CallBackNumber</code> object.
	 *
	 *	@throws ResourceException -if problem in creating <code>CallBackNumber</code>
	 */
	public CallBackNumber createCallBackNumber(String callbackNum, byte presInd, String aTag)
											throws ResourceException ;

}
