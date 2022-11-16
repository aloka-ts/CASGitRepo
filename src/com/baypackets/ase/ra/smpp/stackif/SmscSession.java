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

package com.baypackets.ase.ra.smpp.stackif;

import java.util.ArrayList;

import javax.mail.Session;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.resource.ResourceException;

import org.apache.log4j.Logger;
import org.smpp.TCPIPConnection;
import org.smpp.TimeoutException;
import org.smpp.WrongSessionStateException;
import org.smpp.SmppObject;
import org.smpp.debug.Debug;
import org.smpp.debug.Event;
import org.smpp.debug.FileDebug;
import org.smpp.debug.FileEvent;
import org.smpp.pdu.PDUException;

import com.baypackets.ase.ra.smpp.Smsc;
import com.baypackets.ase.ra.smpp.AddressRange;
import com.baypackets.ase.ra.smpp.SmppRequest;
import com.baypackets.ase.ra.smpp.SmppResponse;
import com.baypackets.ase.ra.smpp.stackif.Constants;
import com.baypackets.ase.ra.smpp.utils.SmppConfMgr;
import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptorImpl;
import com.baypackets.ase.ra.smpp.SmppResourceEvent;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;


public class SmscSession implements Smsc {
	
	private static Logger logger = Logger.getLogger(SmscSession.class);


	/** The connection is closed (or not opened yet) and session isn't bound. */
	public static final int STATE_CLOSED = org.smpp.Session.STATE_CLOSED;

	/** The connection is opened, but the session isn't bound. */
	public static final int STATE_OPENED = org.smpp.Session.STATE_OPENED; 
	
	public static ConfigRepository configRepositery = (ConfigRepository) Registry.lookup(com.baypackets.ase.util.Constants.NAME_CONFIG_REPOSITORY);
	
	static final String dbgDir =configRepositery.getValue(com.baypackets.ase.util.Constants.OID_LOGS_DIR);//"/LOGS/CAS";

	/**
	 * The debug object.
	 * @see FileDebug
	 */
	static Debug debug = new FileDebug(dbgDir, "test.dbg");

	/**
	 * The event object.
	 * @see FileEvent
	 */
	static Event event = new FileEvent(dbgDir, "test.evt");

    private  StackListener stackListener;
	private org.smpp.TCPIPConnection connection;
	// SMSC server parameters
	private String m_name;
	private String m_systemId;
	private String m_password;
	private String m_mode;
	private String m_timeout;
	private String m_retries;
	private String m_protocol;
	private String m_systemType;
	
	private String m_serviceType;
	
	private String m_priority_flag;
	
	
	public String getPriority_flag() {
		return m_priority_flag;
	}

	public void setPriority_flag(String m_priority_flag) {
		logger.debug("Setting priority flag :- " + m_priority_flag);
		this.m_priority_flag = m_priority_flag;
	}

	public String getService_Type() {
		return m_serviceType;
	}

	public void setServiceType(String serviceType) {
		logger.debug("Setting service type :- " + serviceType);
		this.m_serviceType = serviceType;
	}

	public void setSystemType(String m_systemType) {
		this.m_systemType = m_systemType;
	}

	// ip address of the SMSC
	private String m_ipAddress;
	// port of the SMSC
	private int m_port;
	
	private String m_selectionMode;
	
	private boolean m_isPrimary;

	// floating ip of SAS
	private String m_sasFIP;
	// SAS port for making connection with SMSC
	private int m_sasPort;

	// list of address ranges supported by this SMSC
	private ArrayList addrRangelist = new ArrayList();

	private boolean bound=false;
	private boolean preState=false;
	private boolean isTransmitter=false;
	private boolean isReceiver=false;
    
	private String currentStatus;
	public String getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

	// stack session object
	private org.smpp.Session stackObj;

    public SmscSession(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside SmscSession()");
		}
		//connection = new org.smpp.TCPIPConnection(
		//						this.getIpAddr(), this.getPort());
		//stackObj = new org.smpp.Session(connection);
		//stackObj.enableStateChecking();
        stackListener = new StackListener(this);
    }

	public void setStackListener(StackListener listener){
		this.stackListener=listener;	
	}
	
	public StackListener getStackListener(){
		return this.stackListener;
	}

	public boolean isTransmitter() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside isTransmitter with value "+this.isTransmitter);
			logger.debug("opbject is "+this);
		}
		return isTransmitter;
	}

	public boolean isReceiver() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside isReceiver with value "+this.isReceiver);
		}
		return isReceiver;
	}
	
	public void setTransmitter() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setTransmitter " +this);
		}
		isTransmitter=true;
	}

	public void setReceiver() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setReceiver");
		}
		isReceiver=true;
	}
	
	public void setName(String name) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setName(String) with "+name);
		}
		m_name=name;
	}

	public void setSystemId(String id) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSystemId(String) with "+id);
		}
		m_systemId=id;;
	}

	public void setPassword(String pass) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setPassword(String) with "+pass);
		}
		m_password=pass;
	}

	public void setIpAddr(String ip) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setIpAddr(String) with "+ip);
		}
		m_ipAddress=ip;
	}

	public void setPort(int port) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setPort() with "+port);
		}
		m_port=port;
	}

	public void setMode(String mode) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setMode() with "+mode);
		}
		m_mode=mode;
	}

	public void setSasFIP(String ip) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSasFIP() with "+ip);
		}
		m_sasFIP=ip;
	}

	public void setSasPort(int port) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSasPort() with "+port);
		}
		m_sasPort=port;
	}

	public String toString(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside toString()");
		}
		StringBuffer msg = new StringBuffer();
		msg.append(this.getName());
		msg.append("	");
		msg.append(this.getIpAddr());
		msg.append("	");
		msg.append(this.getPort());
		msg.append("	");
		msg.append(this.getMode());
		msg.append("	");
		msg.append(this.getSelectionMode());
		msg.append("	");
		msg.append(this.isPrimary());
		msg.append("	");
		if(isBound()){
			msg.append("Connected");
			msg.append("	");
		}else{
			msg.append("Disconnected");
			msg.append("	");
		}
		ArrayList list = this.getAddressRange();
		for(int i=0;i<list.size();i++){
			AddressRange range = (AddressRange)list.get(i);
			msg.append(range.getRange());
			msg.append(AseStrings.COMMA);
		}
		msg.append("\n");
		return msg.toString();
	}


	public void setTimeout(String timeout) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setTimeout() with "+timeout);
		}
		m_timeout=timeout;
	}

	public void setRetries(String retries) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setRetries() with "+retries);
		}
		m_retries=retries;
	}

	public void setProtocol(String protocol) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setProtocol() with "+protocol);
		}
		m_protocol=protocol;
	}

	public void addAddressRange(AddressRange addrRange) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addAddressRange() with "+addrRange);
		}
		addrRangelist.add(addrRange);
	}

	public void setStackObj(org.smpp.Session stkObj){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setStackObj() "+stkObj);
		}
		this.stackObj=stkObj;
	}

	public void setBound(boolean bound){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setBound(boolean) called with new state "+bound +
							" and previous state "+this.bound);
		}
		this.preState=this.bound;
		this.bound=bound;
	}

	public boolean isBound(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside isBound(), returnin "+bound);
		}
		return this.bound;
	}

	public String getName() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getName(), returning "+m_name);
		}
		return m_name;
	}

	public String getSystemId() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSystemId(), returning "+m_systemId);
		}
		return m_systemId;
	}

	public String getPassword() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getPassword(),returning "+m_password);
		}
		return m_password;
	}

	public String getIpAddr() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getIpAddr(),returning "+m_ipAddress);
		}
		return m_ipAddress;
	}

	public int getPort() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getPort(),returning "+m_port);
		}
		return m_port;
	}

	public String getMode() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getMode(),returning "+m_mode);
		}
		return m_mode;
	}

	
	public String getSystemType() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSystemType(),returning ");
		}
		// TODO what to return 
		return m_systemType;
	}

	public int getInterfaceVersion() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getInterfaceVersion()");
		}
		 //return Integer.parseInt(Constants.INTERFACE_VERSION);
		 int version = Integer.parseInt(Constants.INTERFACE_VERSION);
		 //int version = 3;
		 logger.debug("Leaving getInterfaceVersion() with " +version);
		 return version;
	}

	public ArrayList getAddressRange() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getAddressRange()");
		}
		return addrRangelist;
	}

	public String getSasFIP() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSasFIP,returning "+m_sasFIP);
		}
		return m_sasFIP;
	}

	public int getSasPort() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSasPort,returnin "+m_sasPort);
		}
		return m_sasPort;
	}

	public String getSelectionMode() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSelectionMode "+m_selectionMode);
		}
		return m_selectionMode;
	}

	public void setSelectionMode(String selectionMode) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSelectionMode() with "+selectionMode);
		}
        try {
        	if(selectionMode!=null) {
        		
        		switch(selectionMode) {

	            case "FIRST_AVAILABLE":
	            	logger.debug("Selection Mode is set to FIRST_AVAILABLE");
	                this.m_selectionMode = selectionMode;
	            break;
	            default:
	                logger.error("Error setting selection mode " + selectionMode );
	            break;

	            }
        	}
	          
	            
	        } catch (Exception e) {
	            logger.error("Error setting selection mode " + selectionMode + " " + e, e);
	        }

        this.m_selectionMode= "FIRST_AVAILABLE";
	}

	public boolean isPrimary() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside isPrimary "+m_isPrimary);
		}
		return m_isPrimary;
	}

	public void setIsPrimary(boolean isPrimary) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setIsPrimary() with "+isPrimary);
		}
		this.m_isPrimary = isPrimary;
	}

	public org.smpp.Session getStackObject(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getStackObject().Returning "+this.stackObj);
		}
		return this.stackObj;
	}

	/**
	*  This method sends the SMPP RA specific alarms.
	*  
	*  @param code - alarm code associated with the alarm.
	*  @param msg  - alarm message associated with the alarm.
	*/
	private void sendSmppAlarm(int code,String msg){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside sendSmppAlarm()");
		}
		// sending alarm only if the state has changed.
		if(this.preState!=this.bound){
			if(logger.isDebugEnabled()) {
				logger.debug("sending smpp alarm with alarm code "+code);
			}
			SmppResourceAdaptorImpl.getInstance().sendSmppAlarm(code,msg);
			if(code==com.baypackets.ase.util.Constants.ALARM_SMSC_DOWN){
				if(logger.isDebugEnabled()) {
					logger.debug("Delivering SMSC down event to application");
				}
				SmppResourceEvent event = new SmppResourceEvent(
											SmppResourceAdaptorImpl.getInstance(), 
											SmppResourceEvent.SMSC_DOWN_EVENT,
											this,
											null);
				try{
					SmppResourceAdaptorImpl.getInstance().deliverEvent(event);
				}catch(Exception ex){
					logger.error("Exception in delivering event to application",ex);
				}
			}else if(code==com.baypackets.ase.util.Constants.ALARM_SMSC_UP){
				if(logger.isDebugEnabled()) {
					logger.debug("Delivering SMSC up event to application");
				}
				SmppResourceEvent event = new SmppResourceEvent(
											SmppResourceAdaptorImpl.getInstance(), 
											SmppResourceEvent.SMSC_UP_EVENT,
											this,
											null);
				try{
					SmppResourceAdaptorImpl.getInstance().deliverEvent(event);
				}catch(Exception ex){
					logger.error("Exception in delivering event to application",ex);
				}
			}
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving sendSmppAlarm()");
		}
	}

	public void handleRequest(SmppRequest request) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside handleRequest(SmppRequest)");
		}
		if(!isTransmitter()){
			String errMsg="Smsc is not binded as transmitter.";
			logger.error(errMsg);
			throw new ResourceException(errMsg);
		}
		
		
		try{
		if(request instanceof SubmitSM ){
			if(logger.isDebugEnabled()) {
				logger.debug("Handling SubmitSM request");
				logger.debug("Adding priority flag and service type in submit sm request");
			}
			
			((SubmitSM)request).setPriorityFlag(Integer.valueOf(getPriority_flag()));
			((SubmitSM)request).setServiceType(getService_Type());
			
			this.stackObj.submit(((SubmitSM)request).getStackObject());
		}else if(request instanceof SubmitMultiSM ){
			if(logger.isDebugEnabled()) {
				logger.debug("Handling SubmitMultiSM request");
			}
			this.stackObj.submitMulti(((SubmitMultiSM)request).getStackObject());
		}else if(request instanceof DataSM){
			if(logger.isDebugEnabled()) {
				logger.debug("Handling DataSM request");
			}
			this.stackObj.data(((DataSM)request).getStackObject());
		}else if(request instanceof DeliverSM){
			if(logger.isDebugEnabled()) {
				logger.debug("Handling DeliverSM request");
			}
			this.stackObj.deliver(((DeliverSM)request).getStackObject());
		}else if(request instanceof QuerySM){
			if(logger.isDebugEnabled()) {
				logger.debug("Handling QuerySM request");
			}
			this.stackObj.query(((QuerySM)request).getStackObject());
		}else if(request instanceof ReplaceSM){
			if(logger.isDebugEnabled()) {
				logger.debug("Handling ReplaceSM request");
			}
			this.stackObj.replace(((ReplaceSM)request).getStackObject());
		}else if(request instanceof CancelSM){
			if(logger.isDebugEnabled()) {
				logger.debug("Handling CancelSM request");
			}
			this.stackObj.cancel(((CancelSM)request).getStackObject());
		}
		} catch(Exception ex){
			throw new ResourceException("problem in sending request",ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving handleRequest(SmppRequest)");
		}
	}

	public void handleResponse(SmppResponse response) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside handleResponse(SmppResponse)");
		}
		/*
		if(!isTransmitter()){
			String errMsg="Smsc is not binded as transmitter.";
			logger.error(errMsg);
			throw new ResourceException(errMsg);
		}*/
		if(response instanceof DataSMResp){
			if(logger.isDebugEnabled()) {
				logger.debug("Handling DataSMResp Response");
			}
			try{
				this.stackObj.respond(((DataSMResp)response).getStackObject());
			}catch(Exception ex){
				throw new ResourceException("problem in sending data response ",ex);
			}
		} else if(response instanceof DeliverSMResp){
			if(logger.isDebugEnabled()) {
				logger.debug("Handling DeliverSMResp Response");
			}
			try{
				this.stackObj.respond(((DeliverSMResp)response).getStackObject());
			}catch(Exception ex){
				throw new ResourceException("problem in sending deliver response ",ex);
			}
		}
		else if(response instanceof SubmitSMResp){
			if(logger.isDebugEnabled()) {
				logger.debug("Handling SubmitSMResp Response");
			}
			try{
				this.stackObj.respond(((SubmitSMResp)response).getStackObj());
			}catch(Exception ex){
				throw new ResourceException("problem in sending SubmitSMResp response ",ex);
			}
		} 
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving handleResponse(SmppResponse)");
		}
	}

	public void makeSmscConnection(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside makeSmscConnection()");
		}
		
		debug.activate();
		event.activate();
		SmppObject.setDebug(debug);
		SmppObject.setEvent(event);
		
		int state;
		org.smpp.Session session;

		if(connection==null){
			if(logger.isDebugEnabled()) {
				logger.debug("connection object is null.");
			}
			state=1;
		}else{
			if(logger.isDebugEnabled()) {
				logger.debug("Getting session state");
			}
			session = stackObj;	
			state = session.getState();
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Session state is "+state);
		}
		switch(state){
			case SmscSession.STATE_CLOSED:
				openConnection();
				bindConnection();
				break;
			case SmscSession.STATE_OPENED:
				bindConnection();
				break;
			default :
				checkConnection();
				break;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving makeSmscConnection()");
		}
	}

	/**
	 * 	This method opens a connection with the SMSC for 
	 *	communication.
	 *
	 */
	public void openConnection(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside openSmscConnection()");
		}
		// This port is set to zero so that underlying stack
		// can bind on any availbale port.But in case in future
		// stack has to be binded on a particular port,then 
		// this variable can set to that port.
		int sasPort=0;

		try{
			this.connection = new TCPIPConnection(new Socket(InetAddress.getByName(this.getIpAddr()), this.getPort(), InetAddress.getByName(this.getSasFIP()), sasPort));
		}catch(Exception ex){
			logger.error("Exception in making connection. ",ex);	
		}
		if(logger.isDebugEnabled()) {
			logger.debug("creating stack object.<"+connection+">");
		}
		stackObj = new org.smpp.Session(connection);
		stackObj.enableStateChecking();
		try{
			stackObj.getConnection().open();
		}catch(Exception ex){
			logger.error("Exception occured during open connection. ",ex);	
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving openSmscConnection()");
		}
	}

	/**
	 *	This method is used during SMPP RA stoping.This method
	 *	calls unbinds the previously bindes <code>SmscSession</code>
	 *	This method gets the underlying stack's <code>Session</code>
	 *	object and calls unbind method on the same.Stack sends an
	 *	Unbind request to the SMSC and passed the Unbind Response to this
	 *	class.
	 */
	public void closeConnection(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside closeConnection() of "+this);
		}
		org.smpp.pdu.UnbindResp unbindResp=null;
		org.smpp.Session session;

		session = this.getStackObject();
		try{
			unbindResp = session.unbind();
			SmppResourceAdaptorImpl.getInstance().updateRequestCounter(
											Constants.UNBIND_REQ,false);
			SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
											Constants.UNBIND_RES,true);
			if(logger.isDebugEnabled()) {
				logger.debug("Unbind response received is "+unbindResp);
			}
		}catch(Exception ex){
			logger.error("Exception in unbind",ex);	
		}
		if((unbindResp!=null) && (unbindResp.getCommandStatus()==Constants.ESME_ROK)){
			this.setBound(false);
			this.setCurrentStatus(Constants.STATUS_DOWN);
		}
		// setting connection to null;
		this.connection=null;
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving closeConnection()");
		}
	}

	/**
	 * 	This method checks a connection with the SMSC.To do 
	 *	this it sends a <code>EnquireLink</code> request
	 *	to SMSC.
	 *
	 */
	public void checkConnection(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside checkSmscConnection()");
		}
		org.smpp.pdu.EnquireLinkResp enquireResp=null;
		org.smpp.Session session;

		session = this.getStackObject();
		try{
			enquireResp = session.enquireLink();
			SmppResourceAdaptorImpl.getInstance().updateRequestCounter(
											Constants.ENQUIRE_LINK_REQ,false);
		}catch(Exception ex){
			logger.error("Exception in enquire link ",ex);	
			this.setCurrentStatus(Constants.STATUS_DOWN);
			logger.error("Session state is :- "+ this.stackObj.getState());
			this.connection=null;
			this.setBound(false);			
		}
		if((enquireResp!=null) && (enquireResp.getCommandStatus()==Constants.ESME_ROK)){
			if(logger.isDebugEnabled()) {
				logger.debug("Session is in binded state. Setting State Active");
			}
			this.setCurrentStatus(Constants.STATUS_ACTIVE);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving checkSmscConnection()");
		}
	
	}

	/**
	 * 	This method binds a coonection with the SMSC.To do 
	 *	this it sends a <code>BindRequest</code> request
	 *	to SMSC and depending upon the response it sets the 
	 *	state of the <code>SmscSession</code>.
	 *
	 */
	public void bindConnection(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside bindConnection()");
		}
		BindRequest bindRequest;
		org.smpp.pdu.BindRequest stackObj;
		org.smpp.pdu.BindResponse stkBindRes=null;
		org.smpp.Session session=this.stackObj;
		int sequenceNumber=SmppConfMgr.sequenceNumber;
		int reqType=-1;

		String bindMode=this.m_mode;
		if(bindMode.compareToIgnoreCase(Constants.BIND_TX)==0){
			if(logger.isDebugEnabled()) {
				logger.debug("Creating BindTransmitter request");
			}
			reqType=Constants.BIND_TRANSMITTER_REQ;
			isTransmitter=true;
			bindRequest = new BindTransmitter();
			stackObj=((BindTransmitter)bindRequest).getStackObject();
		}else if(bindMode.compareToIgnoreCase(Constants.BIND_RX)==0){
			if(logger.isDebugEnabled()) {
				logger.debug("Creating BindReceiver request");
			}
			reqType=Constants.BIND_RECEIVER_REQ;
			isReceiver=true;
			bindRequest = new BindReceiver();
			stackObj=((BindReceiver)bindRequest).getStackObject();
		}else if(bindMode.compareToIgnoreCase(Constants.BIND_TRX)==0){
			if(logger.isDebugEnabled()) {
				logger.debug("Creating BindTransceiver request");
			}
			reqType=Constants.BIND_TRANSCEIVER_REQ;
			isTransmitter=true;
			isReceiver=true;
			bindRequest = new BindTransceiver();
			stackObj=((BindTransceiver)bindRequest).getStackObject();
		}else{
			if(logger.isDebugEnabled()) {
				logger.debug("Creating default BindTransceiver request");
			}
			SmppResourceAdaptorImpl.getInstance().updateRequestCounter(
										Constants.BIND_TRANSCEIVER_REQ,false);
			isTransmitter=true;
			isReceiver=true;
			bindRequest = new BindTransceiver();
			stackObj=((BindTransceiver)bindRequest).getStackObject();
		}
		try{
			bindRequest.setSystemId(this.getSystemId());
			bindRequest.setPassword(this.getPassword());
			bindRequest.setSystemType(this.getSystemType());
			logger.debug("Setting interface version ");
			bindRequest.setInterfaceVersion(this.getInterfaceVersion());
			logger.debug("setting sequence number");
			bindRequest.setSequenceNumber(sequenceNumber);
			if(logger.isDebugEnabled()) {
				logger.debug("Going to bind with the SMSC");
			}
			SmppResourceAdaptorImpl.getInstance().updateRequestCounter(
										reqType,false);
			stkBindRes=session.bind(stackObj,stackListener);
			SmppConfMgr.sequenceNumber++;
			if(stkBindRes==null){
				if(logger.isDebugEnabled()) {
					logger.debug("SmscSession timedout.");
				}
				this.setBound(false);
				String alarmMsg=" - Cound not connect with SMSC.SmscSession timeout."+
																	this.getName();
				sendSmppAlarm(com.baypackets.ase.util.Constants.ALARM_SMSC_DOWN,
								alarmMsg);
			}else{
				if(stkBindRes instanceof org.smpp.pdu.BindReceiverResp){
					SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
					Constants.BIND_RECEIVER_RES,true);
				}else if(stkBindRes instanceof org.smpp.pdu.BindTransmitterResp){
					SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
					Constants.BIND_TRANSMITTER_RES,true);
				}else if(stkBindRes instanceof org.smpp.pdu.BindTranscieverResp){
					SmppResourceAdaptorImpl.getInstance().updateResponseCounter(
					Constants.BIND_TRANSCEIVER_RES,true);
				}
				if(stkBindRes.getCommandStatus()==Constants.ESME_ROK){
					if(logger.isDebugEnabled()) {
						logger.debug("Bind response is successful setting Status Active");
					}
					this.setCurrentStatus(Constants.STATUS_ACTIVE);
					this.setBound(true);
					String alarmMsg=" - Successfully connected with SMSC."+
														this.getName();
					sendSmppAlarm(com.baypackets.ase.util.Constants.ALARM_SMSC_UP,
								alarmMsg);
				}else{
					if(logger.isDebugEnabled()) {
						logger.debug("Bind response is not successful setting status down");
					}
					this.setBound(false);
					this.setCurrentStatus(Constants.STATUS_DOWN);
					String alarmMsg=" - Cound not connect with SMSC.Unsuccessful response received."+
																		this.getName();
					sendSmppAlarm(com.baypackets.ase.util.Constants.ALARM_SMSC_DOWN,
									alarmMsg);
					return;
				}
			}
		}catch(Exception ex){
			logger.error("Exception in creating SMSC connection", ex);
			this.setBound(false);
			this.setCurrentStatus(Constants.STATUS_DOWN);
			String alarmMsg=" - Cound not connect with SMSC.Exception in making connection."
													+this.getName();
			sendSmppAlarm(com.baypackets.ase.util.Constants.ALARM_SMSC_DOWN,
							alarmMsg);
			return;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving bindConnection()");
		}
	}
}
