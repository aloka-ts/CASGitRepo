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
//      File:   SmppRequest.java
//
//      Desc:   This interface defines an SMPP request. This is the base class for all
//              the SMPP request and all the SMPP request must extend this. This class
//              contains set methods to set various field of an SMPP request and get
//              methods to get various fields of an SMPP request. This interface describes
//              in details all the API available to application.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
/***********************************************************************************/



package com.baypackets.ase.ra.smpp;

import java.io.IOException;
import java.util.Iterator;
import com.baypackets.ase.resource.Request;

public interface SmppRequest extends Request { 

	/**
	*	This is the unique sequence number for a particular smpp request. The smpp response 
	*	corresponding to this request must have the same sequence number. This is to be used
	*	as a key by resource adaptor to put outstanding smpp requests into a MAP.
	*/
	 public int seqNumber = 0;

	/**
	 *	This method returns the command length of SMPP request.
	 *
	 *	@return command_length parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter.
	 */
 	public int getCommandLength() throws SmppResourceException ;
	
	/**
	 *	This method returns the command id associated with this SMPP request.
	 *
	 *	@return command_id parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter.
	 */
	public int getCommandId() throws SmppResourceException ;

	/**
	 *	This method returns the Command status of SMPP message. For SMPP requests this 
	 *	should be null.
	 *
	 *	@return command_status paramemeter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter.
	 */
	public int getCommandStatus() throws SmppResourceException ;

	/**
	 * This method returns the sequence number associated with the SMPP request.
	 *
	 * @return sequence_number parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter.
	 */
	public int getSequenceNumber() throws SmppResourceException ;

	/**
	 *	This method returns the "message mode" in which this SMPP request
	 *	is to be sent.Following message modes are supported by SMPP 
	 *	RA:
	 *
	 *	Store and forward mode - byte value=3
	 *	Datagram mode - byte value=1
	 *	Transaction mode - byte value=2
	 *	Defaul (Transaction) mode -	byte value=1
	 *
	 *	@return message mode set into request.
	 *	
	 *	@throws SmppResourceException If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public byte getMessageMode() throws SmppResourceException ;

	/**
	 * This method returns the service type associated with the SMPP request.
	 *
	 * @return service_type parameter.
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public String getServiceType() throws SmppResourceException ;

	/**
	 *	This method returns the type of number associated with the source address.
	 *
	 * 	@return source_addr_ton parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public int getSourceAddrTon() throws SmppResourceException ;

	/**
	 *	This method returns the numbering plan indicator associated with the source 
	 *	address.
	 *
	 *	@return source_addr_npi parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public int getSourceAddrNpi() throws SmppResourceException ;

	/***
	 *	This method returns the source address associated with the SMPP request.
	 *
	 *	@return source_addr parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public Address getSourceAddr() throws SmppResourceException ;

	/**
	 *	This method returns the destination address associated with the SMPP request.
	 *
	 *	@return destination_addr parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public Address getDestinationAddr() throws SmppResourceException ;

	/**
	 *	This method returns the type of number associated with the destination address
	 *
	 *	@return dest_addr_ton parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public int getDestAddrTon() throws SmppResourceException ;

	/**
	 *	This method returns the numbering plan indicator associated with the 
	 *	destination address
	 *
	 *	@return dest_addr_npi parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public int getDestAddrNpi() throws SmppResourceException ;

	/**
	 *	This method returns the esm_class field of the SMPP request.The esm_class
	 *	attribute is used to indicate the special message attribute associated with
	 *	the short message.
	 *
	 *	@return esm_class parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public byte getEsmClass() throws SmppResourceException ;

	/**
	 *	This method returns the protocol id associated with the SMPP request.
	 *
	 *	@return protocol_id parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public int getProtocolId() throws SmppResourceException ;

	/**
	 *	This method returns the priority flag associated with the SMPP request.
	 *
	 *	@return priority_flag parameter 
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public int getPriorityFlag() throws SmppResourceException ;

	/**
	 *	This method returns the scheduled delivery time associated with the SMPP request.
	 *
	 *	@return schedule_delivery_time parameter
	 *	This should be in ASCII with following format:
	 *	'YYMMDDhhmmsstnnp' where
	 *
	 * 	yy		last two digits of the year (00-99)
	 * 	MM		month (01-12)
	 *	DD		day (01-31)
	 *	hh		hour (00-23)
	 *	mm		minute (00-59)
	 *	ss		second (00-59)
	 *	t		tenths of second (0-9)
	 *	nn		Time difference in quarter hours between local time (as expressed in 
	 *			the first 13 octets) and UTC (Universal Time Constant) time (00-48).
	 * 	'p'-"+" Local time is in quarter hours advanced in relation to UTC time.
	 * 		"-" Local time is in quarter hours retarded in relation to UTC time.
	 * 	"R"		Local time is relative to the current SMSC time.
	 *	
	 *	Where responses are reported by the SMSC the local time of the SMSC will
	 *	be given and the format will be "YYMMDDhhmmss" with the same definitaions
	 *	as above.
	 *
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public String getScheduledDeliveryTime() throws SmppResourceException ;

	/**
	 *	This method returns the validity period associated with the SMPP request
	 *
	 *	@return validity_period parameter
	 *	This should be in ASCII with following format:
	 *	'YYMMDDhhmmsstnnp' where
	 *
	 * 	yy		last two digits of the year (00-99)
	 * 	MM		month (01-12)
	 *	DD		day (01-31)
	 *	hh		hour (00-23)
	 *	mm		minute (00-59)
	 *	ss		second (00-59)
	 *	t		tenths of second (0-9)
	 *	nn		Time difference in quarter hours between local time (as expressed in 
	 *			the first 13 octets) and UTC (Universal Time Constant) time (00-48).
	 * 	'p'-"+" Local time is in quarter hours advanced in relation to UTC time.
	 * 		"-" Local time is in quarter hours retarded in relation to UTC time.
	 * 	"R"		Local time is relative to the current SMSC time.
	 *	
	 *	Where responses are reported by the SMSC the local time of the SMSC will
	 *	be given and the format will be "YYMMDDhhmmss" with the same definitaions
	 *	as above.
	 *
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public String getValidityPeriod() throws SmppResourceException ;

	/**
	 *	This method returns the registered_delivery parameter associated with the SMPP request
	 *
	 *	@return registered_delivery parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public byte getRegisteredDelivery() throws SmppResourceException ;

	/**
	 *	This method returns the data coding scheme associated with the SMPP request
	 *
	 *	@return data_coding parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public byte getDataCoding() throws SmppResourceException ;

	/**
	 *	This method returns the short message associated with the SMPP request
	 *
	 *	@return short_message parameter 
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public String getShortMessage() throws SmppResourceException ;


	/**
	 *	This method returns the list of call back numbers associated with the request.
	 *
	 *	@return callback_num paramter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public CallBackNumber getCallBackNum() throws SmppResourceException ;

	/**
	 *	This method returns the list of alphanumeric tag associated with call back
	 *	numbers present in a request.This returns the same number of objects as 
	 *	returned by getCalllBackNum() method.The order of occurrence determines the
	 *	particular call_num_atag which corresponds to a particular callback_num.
	 *
	 *	@return callback_num_atag parameter
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	//public byte[] getCallBackNumAtag() throws SmppResourceException ;

	/**
	 *	This method returns the list of call back numbers present indicator.This
	 *	defines the call number presentation and screening .This returns the same
	 *	number of objects as returned by getCalllBackNum() method.The order of 
	 *	occurrence determines the particular call_num_pre_ind which corresponds 
	 *	to a particular callback_num
	 *
	 *	@return callback_num_pres_ind
	 *	
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	//public byte getCallBackNumPreInd() throws SmppResourceException ;

	/**
	 *	This method returns an iterator on all of the optional parameters 
	 *	associated with the SMPP request.
	 *
	 *	@return Iterator on all of the associated optional parameter.
	 *
	 *	@throws SmppResourceException -If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public Iterator getOptParamNames() throws SmppResourceException ;

	/**
	 *	This method returns the value of a optional parameter whose value has 
	 *	been defined as octate string in SMPP specification version 3.4.The 
	 *	key is the integer value of this optional parameter as defiled in 
	 *	the SMPP specification version	3.4
	 *
	 *	@param key It is the Integer value corrosponding to a specific Parameter Name 
	 *			as defined in SMPP specification verion 3.4.
	 *	
	 *	@throws SmppExceptionException -if key provied does not map to any parameter name
	 			or this operation is not allowed on this kind of request.
	 */
	public byte[] getOptParam(short key) throws SmppResourceException ;

	/**
	 *	This method returns the value of a optional parameter whose value has 
	 *	been defined as short or byte in SMPP specification version 3.4.The 
	 *	key is the integer value of this optional parameter as defiled in 
	 *	the SMPP specification version	3.4
	 *
	 *	@param key It is the Integer value corrosponding to a specific Parameter Name 
	 *			as defined in SMPP specification verion 3.4.
	 *	
	 *	@throws SmppExceptionException -if key provied does not map to any parameter name
	 			or this operation is not allowed on this kind of request.
	 */
	public int getOptIntParam(short key) throws SmppResourceException ;

	/**
	 *	This method returns the message id associated with the response of this request.
	 *	
	 *	@return message_id associated with this request.
	 *
	 *	@throws SmppResourceException If problem in getting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public String getMessageId() throws SmppResourceException ;

	/**
	 *	This method cancels the SMPP request sent earlier to SMSC.
	 *
	 *	@throws SmppExceptionException -If this operation is not allowed on this
	 *			kind of request.
	 *	@throws IOException -If IO exception in sending request.
	 */	
	public void cancel() throws SmppResourceException ,IOException;

	/**
	 *	This method replaces the SMPP request sent earlier to SMSC
	 *
	 *	@throws SmppExceptionException -If this operation is not allowed on this
	 *			kind of request.
	 *			IOException -If IO exception in sending request.
	 */
	public void replace(String message) throws SmppResourceException ,IOException;

	/**
	 *	This method queries the status of the SMPP request sent earlier to SMSC
	 *
	 *	@throws SmppExceptionException -If this operation is not allowed on this
	 *			kind of request.
	 *			IOException -If IO exception in sending request.
	 */
	public void query() throws SmppResourceException,IOException ;

	/**
	 *	This method sets the "message mode" in which this SMPP request
	 *	is to be sent.Following message modes are supported by SMPP 
	 *	RA:
	 *
	 *	Store and forward mode - byte value=3
	 *	Datagram mode - byte value=1
	 *	Transaction mode - byte value=2
	 *	Defaul (Transaction) mode -	byte value=1
	 *
	 *	@param mode message mode to be set into request.
	 *	
	 *	@throws SmppResourceException If problem in setting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public void setMessageMode(byte mode) throws SmppResourceException ;

	/**
	 *	This method sets the 'service_type' parameter of an SMPP request.This 
	 *	attribute can be used to indicate the SMS Application service associated
	 *	with the message.
	 *
	 *	@param priority priopity_flag value to be set into request.
	 *	
	 *	@throws SmppResourceException If problem in setting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public void setServiceType(String type) throws SmppResourceException ;

	/**
	 *	This method sets the priority_flag parameter of an SMPP request.This 
	 *	attribute allows the originating SME to assign a priority level to short
	 *	message.
	 *
	 *	@param priority priopity_flag value to be set into request.
	 *	
	 *	@throws SmppResourceException If problem in setting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public void setPriorityFlag(int priority) throws SmppResourceException ;

	/**
	 *	This method sets the validity_period parameter of an SMPP request.This
	 *	parameter specifies the validity of an SMPP request i.e.the SMSC expiration
	 *	time, after which the message should be discarded if not delivered to the
	 *	destinatation.
	 *
	 *	@param validity -validity_period value to be set into request.
	 *	This should be in ASCII with following format:
	 *	'YYMMDDhhmmsstnnp' where
	 *
	 * 	yy		last two digits of the year (00-99)
	 * 	MM		month (01-12)
	 *	DD		day (01-31)
	 *	hh		hour (00-23)
	 *	mm		minute (00-59)
	 *	ss		second (00-59)
	 *	t		tenths of second (0-9)
	 *	nn		Time difference in quarter hours between local time (as expressed in 
	 *			the first 13 octets) and UTC (Universal Time Constant) time (00-48).
	 * 	'p'-"+" Local time is in quarter hours advanced in relation to UTC time.
	 * 		"-" Local time is in quarter hours retarded in relation to UTC time.
	 * 	"R"		Local time is relative to the current SMSC time.
	 *	
	 *	
	 *	@throws SmppResourceException If problem in setting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public void setValidity(String validity) throws SmppResourceException ;
	
	/**
	 *	This method sets the registered_delivery parameter of an SMPP 
	 *	request.This parameter is used to request an SMSC delivery 
	 *	receipt and/or SME originated acknowledgement.
	 *
	 *	@param value -registered_delivery flag value to be set.
	 *	
	 *	@throws SmppResourceException If problem in setting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	//public void setRegisteredDelivery(byte value) throws SmppResourceException ;
	
	/**
	 *	This method sets the 'data_coding' parameter of an SMPP request.This 
	 *	parameter is used to request an SMSC delivery receipt and/or SME 
	 *	originated acknowledgement.
	 *
	 *	@param value -'data_coding' value to be set.
	 *	
	 *	@throws SmppResourceException If problem in setting this paramter or
	 *			this operation is not allowed on this kind of Smpp request.
	 */
	public void setDataCoding(byte value) throws SmppResourceException ;
	
	/**
	 *	This method sets the scheduled_delivery_time parameter of an SMPP 
	 *	request.This parameter specifies the scheduled time at which the 
	 *	message delivery should be first attempted.
	 *
	 *	@param scheduleTime schedule_time parameter value to be set into request.
	 *	This should be in ASCII with following format:
	 *	'YYMMDDhhmmsstnnp' where
	 *
	 * 	yy		last two digits of the year (00-99)
	 * 	MM		month (01-12)
	 *	DD		day (01-31)
	 *	hh		hour (00-23)
	 *	mm		minute (00-59)
	 *	ss		second (00-59)
	 *	t		tenths of second (0-9)
	 *	nn		Time difference in quarter hours between local time (as expressed in 
	 *			the first 13 octets) and UTC (Universal Time Constant) time (00-48).
	 * 	'p'-"+" Local time is in quarter hours advanced in relation to UTC time.
	 * 		"-" Local time is in quarter hours retarded in relation to UTC time.
	 * 	"R"		Local time is relative to the current SMSC time.
	 *	
	 *	@throws SmppResourceException If problem in setting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public void setScheduledDeliveryTime(String scheduleTime) throws SmppResourceException ;
	
	/**
	 *	This method sets the short_message parameter of an SMPP request.
	 *
	 *	@param message short_message parameter value to be set into request.
	 *	
	 *	@throws SmppResourceException If problem in setting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public void setShortMessage(String message) throws SmppResourceException ;

	/**
	 *	This method adds an optional parameter whose value has been defined as
	 *	octate String (in SMPP specifpication version 3.4, to an SMPP) to an
	 *	request.ParamName is the short value of this optional parameter as
	 *	defiled in the SMPP specifpication version 3.4.
	 *
	 *	@param paramName -optional parameter name to be add to request.
	 *	@param paramValue -optional  parameter value to be set into request.
	 *	
	 *	@throws SmppResourceException If problem in setting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public void addOptionalParameter(short paramName, byte[] paramValue) 
										throws SmppResourceException ;

	/**
	 *	This method adds an optional parameter whose value has been defined as
	 *	short or byte (provide as int) in SMPP specifpication version 3.4, 
	 *	to an SMPP to an request.ParamName is the short value of this optional
	 *	parameter as defiled in the SMPP specifpication version 3.4.
	 *
	 *	@param paramName -optional parameter name to be add to request.
	 *	@param paramValue -optional  parameter value to be set into request.
	 *	
	 *	@throws SmppResourceException If problem in setting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public void addOptionalIntParameter(short paramName, int paramValue) 
										throws SmppResourceException ;

	/**
	 *	This method adds a call back numbers to a Smpp request.
	 *
	 *	@param callbackNum -callback_num paramter value to be set into request.
	 *	@param aTag -callback_num_atag paramter value to be set into request.
	 *	@param preInd -callback_num_pres_ind paramter value to be set into request.
	 *	
	 *	@throws SmppResourceException If problem in setting this paramter or
	 *	parameter is not allowed to be set by applicaiton.
	 */
	//public void setCallBackNum(String callbackNum, String aTag, byte preInd) 
	
	public void setCallBackNum(CallBackNumber callbackNum) throws SmppResourceException ;

	/**
	 *	This method sets the set_dpf parameter of an SMPP request.SMSC should respond
	 *	to such a request with a "alert_notification" when SMSC detects that the
	 *	destination MS has become available.
	 *
	 *	@param value set_dpf parameter value to be set into request.
	 *	
	 *	@throws SmppResourceException If problem in setting this paramter or
	 *			this operation is not allowed on this kind of request.
	 */
	public void setSetDpf(byte value) throws SmppResourceException;
}
