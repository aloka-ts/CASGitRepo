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
//      File:   SubmitMultiSM.java.java
//
//		Desc:	Whenever application wants to send an SMPP message (SMS) to 
//				more than one destinations and calls createRequest() on RA 
//				with destination address list as argument, the object of this
//				class is created by SMPP RA internally. Basically this class
//				is a wrapper over stacks SubmitMultiSm class, which is to be
//				sent to SMSC for submitting an SMS to be sent to multiple 
//				destinations. All of the application set/get operations are 
//				executed on this class object which intern are executed on 
//				underlying stack class object.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              23/01/08        Initial Creation
//
/***********************************************************************************/


package com.baypackets.ase.ra.smpp.stackif;

import org.apache.log4j.Logger;
import com.baypackets.ase.ra.smpp.Address;
import com.baypackets.ase.ra.smpp.SmppResponse;
import com.baypackets.ase.ra.smpp.impl.SmppSession;
import com.baypackets.ase.ra.smpp.stackif.AddressImpl;
import com.baypackets.ase.ra.smpp.SmppResourceException;
import org.smpp.util.ByteBuffer;

import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import com.baypackets.ase.ra.smpp.CallBackNumber;
import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptor;
import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptorImpl;
import com.baypackets.ase.spi.container.SasMessage;



public class SubmitMultiSM extends AbstractSmppRequest {
	private static Logger logger = Logger.getLogger(SubmitMultiSM.class);
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
	private org.smpp.pdu.SubmitMultiSM stackObj;
	private transient SmppSession m_session;
	private transient SmscSession smscSession;
	private transient String messageId;
	private CallBackNumber callbackNum=null;
	private byte messageMode;
	private boolean isStoreMode=false;
	private boolean isForwardMode=false;
	private boolean isDatagramMode=false;


	public SubmitMultiSM() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside SubmitMultiSM()");
		}
		setType(Constants.SUBMIT_SM_MULTI_REQ);
		stackObj = new org.smpp.pdu.SubmitMultiSM();
	}

	public SubmitMultiSM(SmppSession session) {
		super(session);
		if(logger.isDebugEnabled()) {
			logger.debug("Inside SubmitMultiSM(SmppSession)");
		}
		setType(Constants.SUBMIT_SM_MULTI_REQ);
		stackObj = new org.smpp.pdu.SubmitMultiSM();
		this.m_session=session;
	}

	public void addRequest(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addRequest()");
		}
		this.m_session.addRequest(this);
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

	public byte getMessageMode(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getMesasgeMode, returning "+this.messageMode);
		}
		return this.messageMode;
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

	public String getScheduledDeliveryTime() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getScheduledDeliveryTime()");
		}
		return this.stackObj.getScheduleDeliveryTime();
	}

	public String getValidityPeriod() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getValidityPeriod()");
		}
		//return Integer.parseInt(this.stackObj.getValidityPeriod());
		return this.stackObj.getValidityPeriod();
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
	    
	public String getShortMessage()  throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getShortMessage()");
		}
		String message = this.stackObj.getShortMessage();
		if(message==null){
			try{
				message = new String(this.stackObj.getMessagePayload().getBuffer());
			}catch(org.smpp.pdu.ValueNotSetException ex){
				//throw new SmppResourceException("No message set");
				logger.debug("No message set");
			}
		}
		return message;
	}

	public CallBackNumber getCallBackNum() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCallBackNum()");
		}
		try{
			if(this.callbackNum==null){
				String cbNum =this.stackObj.callbackNum().getHexDump();
				byte preInd=this.stackObj.getCallbackNumPresInd();
				String atag=this.stackObj.getCallbackNumAtag().getHexDump();
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
			return this.stackObj.getCallbackNumAtag().getBuffer();
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	public byte getCallBackNumPreInd() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getCallBackNumPreInd()");
		}
		try{
			return this.stackObj.getCallbackNumPresInd();
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
		org.smpp.pdu.SubmitMultiSM stackObj=this.getStackObject();
				
			//if(stackObj.hasAlertOnMsgDelivery()){
			//} 
			if(stackObj.hasDestAddrSubunit()){
				nameList.add(new Short(Constants.OPT_PAR_DST_ADDR_SUBUNIT));
			}
			if(stackObj.hasDestinationPort()){
				nameList.add(new Short(Constants.OPT_PAR_DST_PORT));
			}
			if(stackObj.hasDestSubaddress()){
				nameList.add(new Short(Constants.OPT_PAR_DEST_SUBADDR));
			}
			if(stackObj.hasDisplayTime()){
				nameList.add(new Short(Constants.OPT_PAR_DISPLAY_TIME));
			}
			if(stackObj.hasLanguageIndicator()){
				nameList.add(new Short(Constants.OPT_PAR_LANG_IND));
			}
			if(stackObj.hasMessagePayload()){
				nameList.add(new Short(Constants.OPT_PAR_MSG_PAYLOAD));
			}
			if(stackObj.hasMsMsgWaitFacilities()){
				nameList.add(new Short(Constants.OPT_PAR_MSG_WAIT));
			}
			if(stackObj.hasMsValidity()){
				nameList.add(new Short(Constants.OPT_PAR_MS_VALIDITY));
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
			if(stackObj.hasSmsSignal()){
				nameList.add(new Short(Constants.OPT_PAR_SMS_SIGNAL));
			}
			if(stackObj.hasSourceAddrSubunit()){
				nameList.add(new Short(Constants.OPT_PAR_SRC_ADDR_SUBUNIT));
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
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving getOptParamNames()");
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
		org.smpp.pdu.SubmitMultiSM stackObj=this.getStackObject();
	
		try{
		switch(key) {

		//case Constants.OPT_PAR_ALERT_ON_MSG_DELIVERY :
		//	if(stackObj.hasAlertOnMsgDelivery()){
		//		val= stackObj.getAlertOnMsgDelivery();
		//	}
		//	break;
		case Constants.OPT_PAR_DST_ADDR_SUBUNIT :
			if(stackObj.hasDestAddrSubunit()){
				val[0] = stackObj.getDestAddrSubunit();
			}
			break;
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
		case Constants.OPT_PAR_DISPLAY_TIME :
			if(stackObj.hasDisplayTime()){
				val[0] = stackObj.getDisplayTime();
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
		case Constants.OPT_PAR_MSG_WAIT :
			if(stackObj.hasMsMsgWaitFacilities()){
				val[0] = stackObj.getMsMsgWaitFacilities();
			}
			break;
		case Constants.OPT_PAR_MS_VALIDITY:
			if(stackObj.hasMsValidity()){
				val[0] = stackObj.getMsValidity();
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
		case Constants.OPT_PAR_SMS_SIGNAL :
			if(stackObj.hasSmsSignal()){
				shortVal= stackObj.getSmsSignal();
				strVal=Short.toString(shortVal);
				val=strVal.getBytes();
			}
			break;
		case Constants.OPT_PAR_SRC_ADDR_SUBUNIT :
			if(stackObj.hasSourceAddrSubunit()){
				val[0] = stackObj.getSourceAddrSubunit();
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
			logger.debug("Leaving getOptParam(int) with value "+val);
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
	
		case Constants.OPT_PAR_DST_ADDR_SUBUNIT :
			break;
		case Constants.OPT_PAR_DST_PORT :
			break;
		case Constants.OPT_PAR_DISPLAY_TIME :
			break;
		case Constants.OPT_PAR_LANG_IND :
			break;
		case Constants.OPT_PAR_MSG_WAIT :
			break;
		case Constants.OPT_PAR_MS_VALIDITY:
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
		case Constants.OPT_PAR_SMS_SIGNAL :
			break;
		case Constants.OPT_PAR_SRC_ADDR_SUBUNIT :
			break;
		case Constants.OPT_PAR_SRC_PORT :
			break;
		case Constants.OPT_PAR_USER_MSG_REF :
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
			logger.debug("Leaving getOptintParam(int)");
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
		org.smpp.pdu.SubmitMultiSM stackObj=this.getStackObject();
		
		try{
		switch(key) {
		
			case Constants.OPT_PAR_DEST_SUBADDR :
				stackObj.setDestSubaddress(value);
				break;
		case Constants.OPT_PAR_MSG_PAYLOAD :
				stackObj.setMessagePayload(value);
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
		org.smpp.pdu.SubmitMultiSM stackObj=this.getStackObject();
		try{
		switch(key) {

		case Constants.OPT_PAR_DST_ADDR_SUBUNIT :
				stackObj.setDestAddrSubunit((byte)value);
			break;
		case Constants.OPT_PAR_DST_PORT :
				stackObj.setDestinationPort((short)value);
			break;
		case Constants.OPT_PAR_DISPLAY_TIME :
				stackObj.setDisplayTime((byte)value);
			break;
		case Constants.OPT_PAR_LANG_IND :
				stackObj.setLanguageIndicator((byte)value);
			break;
		case Constants.OPT_PAR_MSG_WAIT :
				stackObj.setMsMsgWaitFacilities((byte)value);
			break;
		case Constants.OPT_PAR_MS_VALIDITY:
				stackObj.setMsValidity((byte)value);
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
		case Constants.OPT_PAR_SMS_SIGNAL :
				stackObj.setSmsSignal((short)value);
			break;
		case Constants.OPT_PAR_SRC_ADDR_SUBUNIT :
				stackObj.setSourceAddrSubunit((byte)value);
			break;
		case Constants.OPT_PAR_SRC_PORT :
				stackObj.setSourcePort((short)value);
			break;
		case Constants.OPT_PAR_USER_MSG_REF :
				stackObj.setUserMessageReference((short)value) ;
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

	public String getMessageId() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getMessageId()");
		}
		// TODO
		//throw new SmppResourceException("no message id for SubmitSM request");
		return this.messageId;
	}
	
	/*
	public SmscSession getSmscSession(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSmscSession()");
		}
		return this.smscSession;
	}
	*/
	public int getUserMessageReference() throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getUserMessageReference()");
		}
		try{
			return this.stackObj.getUserMessageReference();
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}

	/**
  	 *  This method cancels the SMPP request sent earlier to SMSC.
	 *
	 *  @throws SmppExceptionException -If this operation is not allowed on this
	 *          kind of request.
	 *  @throws IOException -If IO exception in sending request.
	 */
	 public void cancel() throws SmppResourceException,IOException {
		if(logger.isDebugEnabled()) {
				logger.debug("Inside cancel()");
		}
		CancelSM cancelReq = new CancelSM();
		cancelReq.setMessageId(this.messageId);
		cancelReq.setSmscSession(getSmscSession());
		cancelReq.setProtocolSession((SmppSession)getProtocolSession());
		SmppResourceAdaptorImpl.getInstance().sendMessage((SasMessage)cancelReq);
		if(logger.isDebugEnabled()) {
				logger.debug("Leaving cancel()");
		}
	}

	/**
	 *  This method replaces the SMPP request sent earlier to SMSC
	 *
	 *  @throws SmppExceptionException -If this operation is not allowed on this
	 *          kind of request.
	 *          IOException -If IO exception in sending request.
	 */
	public void replace(String message) throws SmppResourceException,IOException{
		if(logger.isDebugEnabled()) {
			logger.debug("Inside replace()");
		}
		ReplaceSM replaceReq = new ReplaceSM();
		replaceReq.setMessageId(this.messageId);
		replaceReq.setShortMessage(message);
		replaceReq.setSmscSession(getSmscSession());
		replaceReq.setProtocolSession((SmppSession)getProtocolSession());
		SmppResourceAdaptorImpl.getInstance().sendMessage((SasMessage)replaceReq);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving replace()");
		}
	}

	/**
	 *  This method queries the status of the SMPP request sent earlier to SMSC
	 *       
	 *  @throws SmppExceptionException -If this operation is not allowed on this
	 *          kind of request.
	 *          IOException -If IO exception in sending request.
	 */
	public void query() throws SmppResourceException,IOException{
		if(logger.isDebugEnabled()) {
			logger.debug("Inside query()");
		}
		QuerySM queryReq = new QuerySM();
		queryReq.setMessageId(this.messageId);
		queryReq.setSmscSession(getSmscSession());
		queryReq.setProtocolSession((SmppSession)getProtocolSession());
		SmppResourceAdaptorImpl.getInstance().sendMessage((SasMessage)queryReq);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving query()");
		}
	}
	
	public void setSequenceNumber(int seqNum) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSequenceNumber()");
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
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setSourceAddr(Address)");
		}
	}
				    
	public void setDestAddr(Address address){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setDestAddr(Address)");
		}
		// this.stackObj.setDestAddr(((AddressImpl)address).getStackObject());
	}
	
	public void setMessageMode(byte mode) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setMessageMode with "+mode); 
		}
		byte rgstdValue;
		this.messageMode=mode;
		isStoreMode=false;	
		isForwardMode=false;
		isDatagramMode=false;
		try{
			if(this.messageMode==Constants.SM_STORE_FORWARD_MODE){
				rgstdValue=1;
				setRegisteredDelivery(rgstdValue);	
				this.stackObj.setEsmClass((byte)Constants.SM_STORE_FORWARD_MODE);
				isStoreMode=true;	
			}else if(this.messageMode==Constants.SM_FORWARD_MODE){
				rgstdValue=0;
				setRegisteredDelivery(rgstdValue);	
				this.stackObj.setEsmClass((byte)Constants.SM_FORWARD_MODE);
				isForwardMode=true;
			}else if(this.messageMode==Constants.SM_ESM_DEFAULT) {
				rgstdValue=1;
				setRegisteredDelivery(rgstdValue);	
				setScheduledDeliveryTime(null);
				this.stackObj.setEsmClass((byte)Constants.SM_ESM_DEFAULT);
				isStoreMode=true;
			}else{
				rgstdValue=0;
				setRegisteredDelivery(rgstdValue);	
				setScheduledDeliveryTime(null);
				this.stackObj.setEsmClass((byte)Constants.SM_DATAGRAM_MODE);
				isDatagramMode=true;
			}
		}catch(Exception ex){
			String msg="Exception in setting message mode";
			logger.error(msg,ex);
			throw new SmppResourceException(msg);
		}
	}

	public void addDestAddress(Address address) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addDestAddress(Address)");
		}
		try{
			org.smpp.pdu.DestinationAddress destAddr = new org.smpp.pdu.DestinationAddress();
			destAddr.setAddress(((AddressImpl)address).getStackObject());
			this.stackObj.addDestAddress(destAddr);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving addDestAddress(Address)");
		}
	}

	public void setPriorityFlag(int priority) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setPriorityFlag(int)");
		}
		this.stackObj.setPriorityFlag((byte)priority);
	}

	public void setValidity(String validity) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setValidity(String)");
		}
		try{
			this.stackObj.setValidityPeriod(validity);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
	}
	
	public void setRegisteredDelivery(byte value) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setRegisteredDelivery()");
		}
		if(isDatagramMode==true || isForwardMode==true){
			String msg="This operation is not allowed in this message mode";
			throw new SmppResourceException(msg);
		}
		this.stackObj.setRegisteredDelivery(value);
	}
	
	public void setDataCoding(byte value) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setDataCoding()");
		}
		this.stackObj.setDataCoding(value);
	}
	
	public void setScheduledDeliveryTime(String scheduleTime) 
												throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setScheduledDeliveryTime()");
		}
		try{
			this.stackObj.setScheduleDeliveryTime(scheduleTime);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setScheduledDeliveryTime()");
		}
	}
	
	public void setShortMessage(String message) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setShortMessage()");
		}
		try{
			this.stackObj.setShortMessage(message);
		}catch(org.smpp.pdu.WrongLengthOfStringException ex){
			if(logger.isDebugEnabled()) {
				logger.debug("Message too long.Setting into message payload");
			}
			org.smpp.util.ByteBuffer value = new org.smpp.util.ByteBuffer(message.getBytes());
			stackObj.setMessagePayload(value);
		}catch(Exception ex){
			throw new SmppResourceException(ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving setShortMessage()");
		}
	}

	public void addOptionalParameter(int paramName, int paramValue) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addOptionalParameter()");
		}
		// TO DO create a new utility class to add
		// this.stackObj.
	}

	public void setCallBackNum(CallBackNumber cbNum) throws SmppResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setCallBackNum()");
		}
		this.callbackNum=cbNum;
		this.stackObj.setCallbackNum(new ByteBuffer(cbNum.getCallbackNum().getBytes()));
		this.stackObj.setCallbackNumAtag(new ByteBuffer(cbNum.getAtag().getBytes()));
		this.stackObj.setCallbackNumPresInd(cbNum.getPresInd());
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

	public void setMessageId(String id){
		this.messageId=id;
	}
	/*
	public void setSmscSession(SmscSession smscSession){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSmscSession()");
		}
		this.smscSession=smscSession;
	}*/

	public org.smpp.pdu.SubmitMultiSM getStackObject(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObject()");
		}
		return this.stackObj;
	}

//	public SmppResponse createResponse(){
//		throw new ResourceException("response creation not allowed for this request");
//	}

}
