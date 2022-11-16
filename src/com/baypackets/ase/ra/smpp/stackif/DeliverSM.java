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
//      File:   DeliverSM.java
//
//		Desc:	Whenever SMPP RA receives a deliver request from stack to be delivered
//				to application, the object of this class is created by SMPP RA 
//				internally. Basically this class is a wrapper over stacks DeliverSM 
//				class, which is to be delivered to application. All of the application 
//				set/get operations are executed on this class object which intern are 
//				executed on underlying stack class object.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
/***********************************************************************************/

package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import com.baypackets.ase.ra.smpp.Address;
import com.baypackets.ase.ra.smpp.SmppResponse;

import org.smpp.pdu.ValueNotSetException;
import org.smpp.util.ByteBuffer;
import com.baypackets.ase.ra.smpp.SmppResourceException;

import com.baypackets.ase.ra.smpp.CallBackNumber;
import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptor;
import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptorImpl;
import com.baypackets.ase.ra.smpp.impl.SmppSession;
import com.baypackets.ase.spi.container.SasMessage;

public class DeliverSM extends AbstractSmppRequest {

	private static Logger logger = Logger.getLogger(DeliverSM.class);
	/**
	*	This is the unique sequence number for a particular smpp request. The smpp response 
	*	corresponding to this request must have the same sequence number. This is to be used
	*	as a key by resource adaptor to put outstanding smpp requests into a MAP.
	*/
	//private int seqNumber = 0;

	/**
	 *	This attribute contains the stack object which is to be used 
	 *	for all the set/get method
	 *
	 */
	private org.smpp.pdu.DeliverSM stackObj;
	private transient SmscSession smscSession;
	private CallBackNumber callbackNum=null;

	public DeliverSM() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside DeliverSM()");
		}
		setType(Constants.DELIVER_SM_REQ);
		stackObj = new org.smpp.pdu.DeliverSM();
	}

	public DeliverSM(org.smpp.pdu.DeliverSM stackObj) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside DeliverSM(org.smpp.pdu.DeliverSM)");
		}
		setType(Constants.DELIVER_SM_REQ);
		this.stackObj=stackObj;
	}

	public int getCommandLength() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandLength()");
		}
		return this.stackObj.getCommandLength();
	}
	
	public int getCommandId() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandId()");
		}
		return this.stackObj.getCommandId();
	}

	public int getCommandStatus() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCommandStatus()");
		}
		return this.stackObj.getCommandStatus();
	}

	public int getSequenceNumber() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSequenceNumber()");
		}
		return this.stackObj.getSequenceNumber();
	}

	public String getServiceType() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getServiceType()");
		}
		return this.stackObj.getServiceType();
	}

	public int getSourceAddrTon() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSourceAddrTon()");
		}
		return this.stackObj.getSourceAddr().getTon();
	}

	public int getSourceAddrNpi() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSourceAddrNpi()");
		}
		return this.stackObj.getSourceAddr().getNpi();
	}

	public Address getSourceAddr() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSourceAddr()");
		}
		try{
			return new AddressImpl(this.stackObj.getSourceAddr());
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	public int getDestAddrTon() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDestAddrTon()");
		}
		return this.stackObj.getDestAddr().getTon();
	}

	public int getDestAddrNpi() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDestAddrNpi()");
		}
		return this.stackObj.getDestAddr().getNpi();
	}

	public Address getDestinationAddr() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDestinationAddr()");
		}
		try{
			return new AddressImpl(this.stackObj.getDestAddr());
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	public byte getEsmClass() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getEsmClass()");
		}
		return this.stackObj.getEsmClass();
	}

	public int getProtocolId() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getProtocolId()");
		}
		return this.stackObj.getProtocolId();
	}

	public int getPriorityFlag() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getPriorityFlag()");
		}
		return this.stackObj.getPriorityFlag();
	}

	public byte getRegisteredDelivery() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getRegisteredDelivery()");
		}
		return this.stackObj.getRegisteredDelivery();
	}

	public byte getDataCoding() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDataCoding()");
		}
		return this.stackObj.getDataCoding();
	}

	public String getShortMessage() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getShortMessage()");
		}
		return this.stackObj.getShortMessage();
	}

	public CallBackNumber getCallBackNum() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCallBackNum()");
		}
		String cbNum=null;
		byte preInd=-1;
		String atag=null;
		try{
			if(this.callbackNum==null){
				cbNum =this.stackObj.callbackNum().getHexDump();
				//byte preInd=this.stackObj.getCallbackNumPresInd();
				//String atag=this.stackObj.getCallbackNumAtag().getHexDump();
				callbackNum=new CallBackNumberImpl(cbNum,preInd,atag);
			}
			return callbackNum;
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	/*
	public byte[] getCallBackNumAtag() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCallBackNumAtag()");
		}
		try{
			return null; 
			//return this.stackObj.getCallbackNumAtag();
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	public byte getCallBackNumPreInd() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCallBackNumPreInd()");
		}
		try{
			return 0; 
		//return this.stackObj.getCallbackNumPresInd();
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}
	*/

	public Iterator getOptParamNames() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getOptParamNames()");
		}
		short[] keys;
		ArrayList nameList = new ArrayList();
		org.smpp.pdu.DeliverSM stackObj=this.getStackObject();
				
			 
			if(stackObj.hasDestinationPort()){
				nameList.add(new Short(Constants.OPT_PAR_DST_PORT));
			}
			 
			if(stackObj.hasDestSubaddress()){
				nameList.add(new Short(Constants.OPT_PAR_DEST_SUBADDR));
			}
			 
			if(stackObj.hasItsSessionInfo()){
				nameList.add(new Short(Constants.OPT_PAR_ITS_SESSION_INFO));
			}
			 
			if(stackObj.hasLanguageIndicator()){
				nameList.add(new Short(Constants.OPT_PAR_LANG_IND));
			}
			 
			if(stackObj.hasMessagePayload()){
				nameList.add(new Short(Constants.OPT_PAR_MSG_PAYLOAD));
			}
			 
			if(stackObj.hasMessageState()){
				nameList.add(new Short(Constants.OPT_PAR_MSG_STATE));
			}
			 
			if(stackObj.hasNetworkErrorCode()){
				nameList.add(new Short(Constants.OPT_PAR_NW_ERR_CODE));
			}
			 
			if(stackObj.hasPayloadType()){
				nameList.add(new Short(Constants.OPT_PAR_PAYLOAD_TYPE));
			}
			 
			if(stackObj.hasPrivacyIndicator()){
				nameList.add(new Short(Constants.OPT_PAR_PRIV_IND));
			}
			 
			if(stackObj.hasSarMsgRefNum()){
				nameList.add(new Short(Constants.OPT_PAR_SAR_MSG_REF_NUM));
			}
			 
			if(stackObj.hasSarSegmentSeqnum()){
				nameList.add(new Short(Constants.OPT_PAR_SAR_SEG_SNUM));
			}
			 
			if(stackObj.hasSarTotalSegments()){
				nameList.add(new Short(Constants.OPT_PAR_SAR_TOT_SEG));
			}
			 
			if(stackObj.hasSourcePort()){
				nameList.add(new Short(Constants.OPT_PAR_SRC_PORT));
			}
			 
			if(stackObj.hasSourceSubaddress()){
				nameList.add(new Short(Constants.OPT_PAR_SRC_SUBADDR));
			}
			 
			if(stackObj.hasUserMessageReference()){
				nameList.add(new Short(Constants.OPT_PAR_USER_MSG_REF));
			}
			 
			if(stackObj.hasUserResponseCode()){
				nameList.add(new Short(Constants.OPT_PAR_USER_RESP_CODE));
			}
			return nameList.iterator();
	}

	public byte[] getOptParam(short paramName) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getOptParam(int)");
		}
		int key=paramName;
		byte[] val=null;
		short shortVal;
		String strVal;
		org.smpp.pdu.DeliverSM stackObj=this.getStackObject();
			
		try{
		switch(key) {

		case Constants.OPT_PAR_DST_PORT :
			if(stackObj.hasDestinationPort()){
				shortVal = stackObj.getDestinationPort();
				strVal=Short.toString(shortVal);
				val=strVal.getBytes();
			}
			break;
		case Constants.OPT_PAR_DEST_SUBADDR :
			if(stackObj.hasDestSubaddress()){
				val = stackObj.getDestSubaddress().getBuffer();
			}
			break;
		case Constants.OPT_PAR_ITS_SESSION_INFO :
			if(stackObj.hasItsSessionInfo()){
				shortVal = stackObj.getItsSessionInfo();
				strVal=Short.toString(shortVal);
				val=strVal.getBytes();
			}
			break;
		case Constants.OPT_PAR_LANG_IND :
			if(stackObj.hasLanguageIndicator()){
				val[0] = stackObj.getLanguageIndicator();
			}
			break;
		case Constants.OPT_PAR_MSG_PAYLOAD :
			if(stackObj.hasMessagePayload()){
				val= stackObj.getMessagePayload().getBuffer();
			}
			break;
		case Constants.OPT_PAR_MSG_STATE :
			if(stackObj.hasMessageState()){
				val[0] = stackObj.getMessageState();
			}
			break;
		case Constants.OPT_PAR_NW_ERR_CODE :
			if(stackObj.hasNetworkErrorCode()){
				val= stackObj.getNetworkErrorCode().getBuffer();
			}
			break;
		case Constants.OPT_PAR_PAYLOAD_TYPE :
			if(stackObj.hasPayloadType()){
				val[0] = stackObj.getPayloadType();
			}
			break;
		case Constants.OPT_PAR_PRIV_IND :
			if(stackObj.hasPrivacyIndicator()){
				val[0] = stackObj.getPrivacyIndicator();
			}
			break;
		case Constants.OPT_PAR_SAR_MSG_REF_NUM :
			if(stackObj.hasSarMsgRefNum()){
				shortVal = stackObj.getSarMsgRefNum();
				strVal=Short.toString(shortVal);
				val=strVal.getBytes();
			}
			break;
		case Constants.OPT_PAR_SAR_SEG_SNUM :
			if(stackObj.hasSarSegmentSeqnum()){
				shortVal= stackObj.getSarSegmentSeqnum();
				strVal=Short.toString(shortVal);
				val=strVal.getBytes();
			}
			break;
		case Constants.OPT_PAR_SAR_TOT_SEG :
			if(stackObj.hasSarTotalSegments()){
				shortVal= stackObj.getSarTotalSegments();
				strVal=Short.toString(shortVal);
				val=strVal.getBytes();
			}
			break;
		case Constants.OPT_PAR_SRC_PORT :
			if(stackObj.hasSourcePort()){
				shortVal= stackObj.getSourcePort();
				strVal=Short.toString(shortVal);
				val=strVal.getBytes();
			}
			break;
		case Constants.OPT_PAR_SRC_SUBADDR :
			if(stackObj.hasSourceSubaddress()){
				val= stackObj.getSourceSubaddress().getBuffer();
			}
			break;
		case Constants.OPT_PAR_USER_MSG_REF :
			if(stackObj.hasUserMessageReference()){
				shortVal= stackObj.getUserMessageReference() ;
				strVal=Short.toString(shortVal);
				val=strVal.getBytes();
			}
			break;
		case Constants.OPT_PAR_USER_RESP_CODE :
			if(stackObj.hasUserResponseCode()){
				val[0] = stackObj.getUserResponseCode() ;
			}
			break;
		default :
			throw new SmppResourceException("This optional parameter is not supported");
		}
		}catch(SmppResourceException ex){
			logger.error("Problem in getting optional parameter ",ex);
			throw new SmppResourceException("This optional parameter is not supported");
		}catch(Exception ex){
			logger.error("Problem in getting parameter value from stack ",ex);
			throw new SmppResourceException("Problem in getting parameter value from stack.");
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving getOptParam(int)");
		}
		return val;
	}


	public int getOptIntParam(short paramName) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getOptintParam(int)");
		}
		boolean flag=true;
		short key=paramName;
		int intVal=-1;
		String strVal;
				
		switch(key) {

		//case Constants.OPT_PAR_ALERT_ON_MSG_DELIVERY :
			//if(stackObj.hasAlertOnMsgDelivery()){
				//val= stackObj.getAlertOnMsgDelivery();
			//}
			//break;
		case Constants.OPT_PAR_DST_PORT :
			break;
		case Constants.OPT_PAR_ITS_SESSION_INFO :
			break;
		case Constants.OPT_PAR_LANG_IND :
			break;
		case Constants.OPT_PAR_MSG_STATE :
			break;
		case Constants.OPT_PAR_PAYLOAD_TYPE :
			break;
		case Constants.OPT_PAR_PRIV_IND :
			break;
		case Constants.OPT_PAR_SAR_MSG_REF_NUM :
			break;
		case Constants.OPT_PAR_SAR_SEG_SNUM :
			break;
		case Constants.OPT_PAR_SAR_TOT_SEG :
			break;
		case Constants.OPT_PAR_SRC_PORT :
			break;
		case Constants.OPT_PAR_USER_MSG_REF :
			break;
		case Constants.OPT_PAR_USER_RESP_CODE :
			break;
		default :
			flag=false;
			throw new SmppResourceException("This optional parameter is not supported");

		}
		if(flag==true){
			strVal=new String(getOptParam(key));
			intVal=Integer.parseInt(strVal);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("LeavingLeaving  getOptintParam(int)");
		}
		return intVal;
	}

	public void addOptionalParameter(short paramName,byte[] paramValue) 
										throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addOptionalParameter()");
		}
		int key = paramName;
		org.smpp.util.ByteBuffer value = new org.smpp.util.ByteBuffer(paramValue);
		org.smpp.pdu.DeliverSM stackObj=this.getStackObject();
		try{
		switch(key) {
		
			case Constants.OPT_PAR_DEST_SUBADDR :
				stackObj.setDestSubaddress(value);
				break;
		case Constants.OPT_PAR_MSG_PAYLOAD :
				stackObj.setMessagePayload(value);
			break;
		case Constants.OPT_PAR_NW_ERR_CODE :
				stackObj.setNetworkErrorCode(value);
			break;
		case Constants.OPT_PAR_SRC_SUBADDR :
				stackObj.setSourceSubaddress(value);
			break;
		default :
			throw new SmppResourceException("This optional parameter is not supported");
		}
		}catch(SmppResourceException ex){
			logger.error("Problem in adding optional parameter ",ex);
			throw new SmppResourceException("This optional parameter is not supported");
		}catch(Exception ex){
			logger.error("Problem in adding parameter on stack object ",ex);
			throw new SmppResourceException("Problem in adding parameter on stack object.");
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving addOptionalParameter()");
		}
	}

	public void addOptionalIntParameter(short paramName, int paramValue) 
										throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addOptionalIntParameter()");
		}
		int key = paramName;
		int value = paramValue;
		org.smpp.pdu.DeliverSM stackObj=this.getStackObject();
	
		try{
		switch(key) {

		case Constants.OPT_PAR_DST_PORT :
				stackObj.setDestinationPort((short)value);
			break;
		case Constants.OPT_PAR_ITS_SESSION_INFO :
				stackObj.setItsSessionInfo((short)value);
			break;
		case Constants.OPT_PAR_LANG_IND :
				stackObj.setLanguageIndicator((byte)value);
			break;
		case Constants.OPT_PAR_MSG_STATE :
				stackObj.setMessageState((byte)value);
			break;
		case Constants.OPT_PAR_PAYLOAD_TYPE :
				stackObj.setPayloadType((byte)value);
			break;
		case Constants.OPT_PAR_PRIV_IND :
				stackObj.setPrivacyIndicator((byte)value);
			break;
		case Constants.OPT_PAR_SAR_MSG_REF_NUM :
				stackObj.setSarMsgRefNum((short)value);
			break;
		case Constants.OPT_PAR_SAR_SEG_SNUM :
				stackObj.setSarSegmentSeqnum((short)value);
			break;
		case Constants.OPT_PAR_SAR_TOT_SEG :
				stackObj.setSarTotalSegments((short)value);
			break;
		case Constants.OPT_PAR_SRC_PORT :
				stackObj.setSourcePort((short)value);
			break;
		case Constants.OPT_PAR_USER_MSG_REF :
				stackObj.setUserMessageReference((short)value) ;
			break;
		case Constants.OPT_PAR_USER_RESP_CODE :
				stackObj.setUserResponseCode((byte)value) ;
			break;
		default :
			throw new SmppResourceException("This optional parameter is not supported");
		}
		}catch(SmppResourceException ex){
			logger.error("Problem in adding optional parameter ",ex);
			throw new SmppResourceException("This optional parameter is not supported");
		}catch(Exception ex){
			logger.error("Problem in adding parameter on stack object ",ex);
			throw new SmppResourceException("Problem in adding parameter on stack object.");
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving addOptionalIntParameter()");
		}
	}

	public String getMessageId(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getMessageId()");
		}
		String messageId=null;
		try {
			messageId= this.stackObj.getReceiptedMessageId();
		} catch (ValueNotSetException e) {
			e.printStackTrace();
		}
		return messageId;
	}

	public String getReceiptedMessageId() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getReceiptedMessageId()");
		}
		try{
			return this.stackObj.getReceiptedMessageId();
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	public int getUserMessageReference() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getUserMessageReference()");
		}
		try{
			return this.stackObj.getUserMessageReference();
		}catch(Exception ex){
			logger.error("Exception in getUserMessageReference()",ex);
			throw new SmppResourceException(ex);
		}
	}

	public SmscSession getSmscSession(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSmscSession()");
		}
		return this.smscSession;
	}

	public boolean hasReceiptedMessageId() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside hasReceiptedMessageId()");
		}
		return this.stackObj.hasReceiptedMessageId();
	}

	public void setSequenceNumber(int seqNum) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSequenceNumber(int)");
		}
		try{
			this.stackObj.setSequenceNumber(seqNum);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	public void setServiceType(String type) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setPriorityFlag()");
		}
		try {
			this.stackObj.setServiceType(type);
		}catch(Exception ex){
			throw new SmppResourceException(ex.getMessage());
		}
	}

	public void setSourceAddr(Address address){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSourceAddr(Address)");
		}
		this.stackObj.setSourceAddr(((AddressImpl)address).getStackObject());
	}
				    
	public void setDestAddr(Address address){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setDestAddr(Address)");
		}
		this.stackObj.setDestAddr(((AddressImpl)address).getStackObject());
	}

	public void setPriorityFlag(int priority) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setPriorityFlag(int)");
		}
		this.stackObj.setPriorityFlag((byte)priority);
	}

	public void setRegisteredDelivery(byte value) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setRegisteredDelivery()");
		}
		this.stackObj.setRegisteredDelivery(value);
	}

	public void setDataCoding(byte value) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setDataCoding()");
		}
		this.stackObj.setDataCoding(value);
	}
	
	public void setShortMessage(String message) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setShortMessage(String)");
		}
		try{
			this.stackObj.setShortMessage(message);
		}catch(Exception ex){

		}
	}

	// PKUMAR



	public void setCallBackNum(CallBackNumber cbNum) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setCallBackNum()");
		}
		this.callbackNum=cbNum;     
		this.stackObj.setCallbackNum(new ByteBuffer(cbNum.getCallbackNum().getBytes()));
		//this.stackObj.setCallbackNumAtag(new ByteBuffer(cbNum.getAtag().getBytes()));
		//this.stackObj.setCallbackNumPresInd(cbNum.getPresInd());
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setCallBackNum()");
		}
	}

	public void setUserMessageReference(int value){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setUserMessageReference()");
		}
		this.stackObj.setUserMessageReference((byte)value);
	}

	public SmppResponse createResponse() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside createResponse()");
		}
		try{
		DeliverSMResp response = new DeliverSMResp();
		response.setSequenceNumber(this.getSequenceNumber());
		response.setSmscSession(this.smscSession);
		response.setMessageId(getMessageId());
		return (SmppResponse)response;
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	/**
	 *  This method creates an object of <code>DeliverSMResp</code> corrosponding
	 *  to this request object and calls sendMessage() of 
	 *  <code>SmppResourceAdaptorImpl</code> to send this to stack which
	 *  inturn sends it to SMSC.
	 *
	 *  @throws SmppResourceException -If any problem occured in creating
	 *                              and sending this message.
	 *  @throws IOException -If an IO Exception occured.
	 */
	public void respond() throws SmppResourceException,IOException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside respond()");
		}
		DeliverSMResp response = (DeliverSMResp)createResponse();
		response.setSmscSession(this.smscSession);
		response.setProtocolSession((SmppSession)getProtocolSession());
		response.getMessageId();
		SmppResourceAdaptorImpl.getInstance().sendMessage((SasMessage)response);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving respond()");
		}
	}

	public void setSmscSession(SmscSession smscSession){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSmscSession()");
		}
		this.smscSession=smscSession;
	}

/*
	public void setCallBackNum(String callbackNum, String aTag, byte preInd){
		this.stackObj.setCallbackNum(callbackNum);
		this.stackObj.setCallbackNumAtag(aTag);
		this.stackObj.setCallbackNumPresInd(preInd);
	}
*/
	public org.smpp.pdu.DeliverSM getStackObject() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObject()");
		}
		return this.stackObj;
	}

}
