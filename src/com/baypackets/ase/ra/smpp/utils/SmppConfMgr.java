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
//      File:   SmppConfMgr.java
//
//      Desc:   This class defines Smpp configuration manager which manages
//				Smsc session with all the SMSCc configured in smpp-config.xml
//				It binds smsc session with these SMSCs during initialization
//				of the SMPP RA,unbinds on stopping SMPP RA,binds with a 
//				particular SMSC on receiving outbind request from that SMSC.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              01/02/08        Initial Creation
//
/***********************************************************************************/
package com.baypackets.ase.ra.smpp.utils;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import java.lang.Exception;
import com.baypackets.ase.ra.smpp.stackif.*;
import com.baypackets.ase.ra.smpp.stackif.Constants;
import com.baypackets.ase.ra.smpp.Address;
import com.baypackets.ase.ra.smpp.AddressRange;
import com.baypackets.ase.ra.smpp.stackif.AddressRangeImpl;
import com.baypackets.ase.ra.smpp.stackif.BindRequest;
import com.baypackets.ase.ra.smpp.SmppRequest;
import com.baypackets.ase.ra.smpp.SmppResponse;
import com.baypackets.ase.ra.smpp.SmppResourceEvent;
import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptor;
import com.baypackets.ase.ra.smpp.impl.SmppResourceAdaptorImpl;
import com.baypackets.ase.spi.container.SasMessage;


public class SmppConfMgr{
	private static Logger logger = Logger.getLogger(SmppConfMgr.class);
	private List m_smscList; 
	private SmppResourceAdaptor ra;
	public static int sequenceNumber=1;
	public static List<SmscSession> currentSmcsDownList= new ArrayList<>();

	public SmppConfMgr(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside constructor");
		}
		m_smscList=new ArrayList();
	}
	
	/** 
	 *	This method returns the list of all the SMSC server configured
	 *	with SMPP RA in smpp-config.xml.
	 *
	 *	@return List -list of all the SMSCs configured with the RA. 
	 */
	public List getSmscList() {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSmscList()");
		}
		 return m_smscList ;
	}

	/**
	 *	This method returns the instance  of <code>SmscSession</code>
	 *	which is at the last index in the list of all the SmscSession
	 *	available with the <code>SmppConfMge</code>
	 *
	 *	@return </code>SmscSession</code> at last index of smsc list.
	 */
	 public SmscSession getLastSmsc(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getLastSmsc()");
		}
		return (SmscSession)m_smscList.get(m_smscList.size()-1);
	 }
	 
	/** 
	 *	This method sets the list of SMSC servers configured
	 *	with SMPP RA in smpp-config.xml.
	 *
	 *	@param list -List of all the SMSCs configured with the RA. 
	 */
	public void setSmscList( List list) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside setSmscList()");
		}
		m_smscList = list ; 
	}

	/** 
	 *	This method adds a SMSC server to the smsc servers list.
	 *
     * @param smsc -SMSC server to be added to the list. 
     */
	public void addSmsc(SmscSession smsc) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside addSmsc()");
		}
		//for ( int i=0;i<m_smscList.size() ; i++ ) { 
			/* logic to check if smscSession for this ip and add range already added.
			if (((SmscSession)m_smscList.get(i)).getIpAddr().compareToIgnoreCase(smsc.getIpAddr()) == 0 ){
				
				m_logger.error ( " Server Already exist in List ") ;
				return ;
			} */
		//}	
		/*if ( smsc.getIpAddr() != null ){
			m_smscList.add(smsc);
		}else{ 
			logger.debug("IP address is null , So rejecting it  " ) ;	
		}*/
		m_smscList.add(smsc);
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving addSmsc()");
		}
	}

	/**
	*  This method sends the SMPP RA specific alarms.
	*  
	*  @param code - alarm code associated with the alarm.
	*  @param msg  - alarm message associated with the alarm.
	*/
	private void sendSmppAlarm(int alarmCode,String msg,SmscSession smscSession){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside sendSmppAlarm()");
		}
		SmppResourceAdaptorImpl.getInstance().sendSmppAlarm(alarmCode,msg);
		if(alarmCode==com.baypackets.ase.util.Constants.ALARM_SMSC_DOWN) {
			if(logger.isDebugEnabled()) {
				logger.debug("Delivering SMSC down event to application");
			}
			SmppResourceEvent event = new SmppResourceEvent(
										SmppResourceAdaptorImpl.getInstance(), 
										SmppResourceEvent.SMSC_DOWN_EVENT,
										smscSession,
										null);
			try{
				SmppResourceAdaptorImpl.getInstance().deliverEvent(event);
			}catch(Exception ex){
				logger.error("Exception in delivering event to application",ex);
			}
		}else if(alarmCode==com.baypackets.ase.util.Constants.ALARM_SMSC_UP) {
			if(logger.isDebugEnabled()) {
				logger.debug("Delivering SMSC up event to application");
			}
			SmppResourceEvent event = new SmppResourceEvent(
										SmppResourceAdaptorImpl.getInstance(), 
										SmppResourceEvent.SMSC_UP_EVENT,
										smscSession,
										null);
			try{
				SmppResourceAdaptorImpl.getInstance().deliverEvent(event);
			}catch(Exception ex){
				logger.error("Exception in delivering event to application",ex);
			}
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving sendSmppAlarm()");
		}
	}

	/**
	 *	This method is used during SMPP RA initialization.This method
	 *	binds different SMSCs configures in smpp-config.xml.For 
	 *	every SMSC in smsclist this method creates a new object of 
	 *	<code>stackListener=new</code> and passes it to the stack as 
	 *	a paramter.This <code>stackListener=new</code> will be used as
	 *	a call back listener for all the messages coming into this
	 *	<code>SmscSession</code>.
	 *	
	 *	@param ra <code>SmppResourceAdaptor</code> object
	 */
	 /*
	public void createSmscConnection(SmppResourceAdaptor ra) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside createSmscConnection()");
		}
		String bindMode;
		BindRequest bindRequest;
		org.smpp.pdu.BindRequest stackObj;
		org.smpp.pdu.BindResponse stkBindRes=null;
		StackListener stackListener;
		int reqType=-1;
		this.ra=ra;

		for(int i=0;i<m_smscList.size();i++){
			SmscSession smscSession = (SmscSession)m_smscList.get(i);
			stackListener=new StackListener(smscSession,ra);
			smscSession.setStackListener(stackListener);
			org.smpp.TCPIPConnection connection = new org.smpp.TCPIPConnection(smscSession.getIpAddr(),smscSession.getPort());
			org.smpp.Session session = new org.smpp.Session(connection);
			smscSession.setStackObj(session);
			bindMode=smscSession.getMode();
			if(bindMode.compareToIgnoreCase(Constants.BIND_TX)==0){
				if(logger.isDebugEnabled()) {
					logger.debug("Creating BindTransmitter request");
				}
				reqType=Constants.BIND_TRANSMITTER_REQ;
				smscSession.setTransmitter();
				bindRequest = new BindTransmitter();
				stackObj=((BindTransmitter)bindRequest).getStackObject();
			}else if(bindMode.compareToIgnoreCase(Constants.BIND_RX)==0){
				if(logger.isDebugEnabled()) {
					logger.debug("Creating BindReceiver request");
				}
				reqType=Constants.BIND_RECEIVER_REQ;
				smscSession.setReceiver();
				bindRequest = new BindReceiver();
				stackObj=((BindReceiver)bindRequest).getStackObject();
			}else if(bindMode.compareToIgnoreCase(Constants.BIND_TRX)==0){
				if(logger.isDebugEnabled()) {
					logger.debug("Creating BindTransceiver request");
				}
				reqType=Constants.BIND_TRANSCEIVER_REQ;
				smscSession.setReceiver();
				smscSession.setTransmitter();
				bindRequest = new BindTransceiver();
				stackObj=((BindTransceiver)bindRequest).getStackObject();
			}else{
				if(logger.isDebugEnabled()) {
					logger.debug("Creating default BindTransceiver request");
				}
				reqType=Constants.BIND_TRANSCEIVER_REQ;
				smscSession.setReceiver();
				smscSession.setTransmitter();
				bindRequest = new BindTransceiver();
				stackObj=((BindTransceiver)bindRequest).getStackObject();
			}	
			try{
				bindRequest.setSystemId(smscSession.getSystemId());
				bindRequest.setPassword(smscSession.getPassword());
				bindRequest.setSystemType(smscSession.getSystemType());
				logger.debug("Setting interface version ");
				bindRequest.setInterfaceVersion(smscSession.getInterfaceVersion());
				logger.debug("setting sequence number");
				bindRequest.setSequenceNumber(sequenceNumber);
				// bindRequest.setSequenceNumber(ra.getNextSeqNumber());
				//session.bind(bindRequest.getStackObject(),stackListener);
				if(logger.isDebugEnabled()) {
					logger.debug("Going to bind with the SMSC");
				}
				logger.debug("Bind request is "+stackObj.toString());
				logger.debug("Stack Listener is "+stackListener);
				try{
					stkBindRes=session.bind(stackObj,stackListener);
					SmppResourceAdaptorImpl.getInstance().updateRequestCounter(
												reqType,false);
				}catch(Exception ex){
					logger.error("Exception in creating SMSC connection",ex);
					smscSession.setBound(false);
					String alarmMsg=" - Could not connect with SMSC.Exception in making connection."
											+smscSession.getName();
					sendSmppAlarm(com.baypackets.ase.util.Constants.ALARM_SMSC_DOWN,
									alarmMsg,
									smscSession);
					sequenceNumber++;
					continue;
				}
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
				sequenceNumber++;
				if(stkBindRes.getCommandStatus()==Constants.ESME_ROK){
					if(logger.isDebugEnabled()) {
						logger.debug("Bind response is successful");
					}
					smscSession.setBound(true);
					String alarmMsg=" - Successfully connected with SMSC "
											+smscSession.getName();
					sendSmppAlarm(com.baypackets.ase.util.Constants.ALARM_SMSC_UP,
									alarmMsg,
									smscSession);
				}else{
					if(logger.isDebugEnabled()) {
						logger.debug("Bind response is not successful");
					}
					smscSession.setBound(false);
					String alarmMsg=" - Could not connect with SMSC.Unsuccessful response received."
											+smscSession.getName();
					sendSmppAlarm(com.baypackets.ase.util.Constants.ALARM_SMSC_DOWN,
									alarmMsg,
									smscSession);
				}
			}catch(Exception ex){
				logger.error("Creating SMSC connection", ex);
				smscSession.setBound(false);
				String alarmMsg=" - Could not connect with SMSC.Exception in making connection."
										+smscSession.getName();
				sendSmppAlarm(com.baypackets.ase.util.Constants.ALARM_SMSC_DOWN,
								alarmMsg,
								smscSession);
			}
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving createSmscConnection()");
		}
	}
	*/
	/**
	 *	This method is used during SMPP RA stoping.This method
	 *	calls unbinds all the previously bindes <code>SmscSession</code>
	 *	This method gets the underlying stack's <code>Session</code>
	 *	object and cals unbind metod on the same.Stack sends an
	 *	Unbind request to the SMSC and passed the Unbind Response to this
	 *	class.
	 */
	 /*
	public void stopSmscConnection(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside stopSmscConnection()");
		}
		org.smpp.pdu.UnbindResp unbindResp=null;
		SmscSession smscSession;
		org.smpp.Session session;

		for(int i=0;i<m_smscList.size();i++){
			smscSession = (SmscSession)m_smscList.get(i);
			if(logger.isDebugEnabled()) {
				logger.debug("Going to unbind session "+smscSession);
			}
			session = smscSession.getStackObject();
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
				logger.error("Exception in unbind"+ex);	
			}
			if((unbindResp!=null) && (unbindResp.getCommandStatus()==Constants.ESME_ROK)){
				smscSession.setBound(false);
			}
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving stopSmscConnection()");
		}
	}
	*/
	/*
	public BindRequest fillReques(BindRequest request,SmscSession smscSession){
		AddressRange range;
		request.setSystemId(smscSession.getSystemId());
		request.setPassword(smscSession.getPassword());
		request.setSystemType(smscSession.getSystemType());
		request.setInterfaceVersion(smscSession.getInterfaceVersion());
		ArrayList rangeList = smscSession.getAddressRange();
		// TODO what in case of multi address range
		if(rangeList.size()>1){
			range=rangeList.get(0);
		}else {
			range=rangeList.get(0);
		}
		request.setAddressRange(range.getTon(),range.getNpi(),range.getRange());
	} */

	/**
	 *	This method retuns all the <code>SmscSession</code> which can 
	 *	be used to send this <code>SasMessage</code>.This method calls 
	 *	getmatchingSmsc() method which checks, out of all the binded SMSCs,
	 *	which all SMSCs serves the address-range this message falls in 
	 *	and returns the list of all the <code>SmscSession</code>
	 *	corresponoding to these SMSCS.(one messages can fall in the 
	 *	address-range of more than one SMSC).
	 *
	 *	@param message -<code>SasMessage</code> to be sent to stack.
	 *
	 *	@return <code>ArrayList</code> -List of all the <code>SmscSession</code>
	 *									which can be used to send this message.
	 */
	public ArrayList getSmscSession(SasMessage message) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getSmscSession()");
		}
		Address address=null;
		ArrayList smscList= new ArrayList();
		SmscSession smscSession;
		if(message instanceof SmppRequest){
			try{
				address = ((SmppRequest)message).getDestinationAddr();
			}catch(Exception ex){
				logger.error("problem in reading dest address",ex);
			}
		}else{
			// TODO address = ((SmppResponse)message).getDestinationAddr();
		}	
		ArrayList tmpList = getMatchingSmsc(address);
		if(logger.isDebugEnabled()) {
			logger.debug("No of matched smsc sessions "+tmpList.size());
		}
		 for(int i=0;i<tmpList.size();i++){
			smscSession = (SmscSession)tmpList.get(i);
			if(smscSession.isBound() && smscSession.isTransmitter() && StringUtils.equalsIgnoreCase(smscSession.getCurrentStatus(), Constants.STATUS_ACTIVE)){
				if(logger.isDebugEnabled()) {
					logger.debug("Adding Smsc Session "+smscSession);
				}
				smscList.add(smscSession);
			}
			
			if(!smscSession.isBound() || StringUtils.equalsIgnoreCase(smscSession.getCurrentStatus(), Constants.STATUS_DOWN)) {
				logger.debug("Found a SMSC not in active state so checking if already Polling is active or not:- "+ smscSession);
				
				if(!currentSmcsDownList.contains(smscSession)) {
					currentSmcsDownList.add(smscSession);
					logger.debug("Found a SMSC not in active state so trying to bind with it again :- "+ smscSession);
					SMScPollingConnection smScPollingConnection= new SMScPollingConnection();
					smScPollingConnection.setSmscSession(smscSession);
					Thread smscSessionPolling= new Thread(smScPollingConnection,"SMScPollingConnection");
					smscSessionPolling.start();	
				}

				
			}
		 }
		if(logger.isDebugEnabled()) {
			logger.debug("No of bounded smsc sessions "+smscList.size());
		}
		 return smscList;
	}

	/**
	 *	This method retuns all the <code>SmscSession</code> which can 
	 *	be used to send this <code>SasMessage</code>.This method checks, out
	 *  of all the binded SMSCs,which all SMSCs serves the address-range
	 *  this message falls in and returns the list of all the <code>SmscSession</code>
	 *  corresponoding to these SMSCS.(one messages can fall in the address-range of
	 *  more than one SMSC).This method matches the 'type of number',
	 *	'numbering plan indicator' and 'range' of Smpp messsge to be sent 
	 *	with 'type of number','numbering plan indicator' and 'range' of 
	 *	all the SMSCs configuired respectively and returns the list of all
	 *	the matched SMSCs.There can be multiple SMSC which matches with the 
	 *	address of the SasMessage.
	 *
	 *	@param addr -Destination <code>Address</code> paramter of the Smpp
	 *				message to be sent to the SMSC.
	 *
	 *	@return <code>ArrayList</code> -List of all the <code>SmscSession</code>
	 *									which can be used to send this message.
	 */
	public ArrayList getMatchingSmsc(Address addr){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getMatchingSmsc(Address)");
		}
		ArrayList smscList = new ArrayList();
		ArrayList rangeList=null;
		Address address=null;
		int destTon=-1;
		int destNpi=-1;
		String destAddr=null;
		
		address=addr;
		destTon=address.getTon();
		destNpi=address.getNpi();
		destAddr=address.getRange();
		logger.debug("request address is "+destTon+" "+destNpi+" "+destAddr);

		for(int i=0;i<m_smscList.size();i++){
			logger.debug("inside for for count "+i);
			SmscSession smscSession = (SmscSession)m_smscList.get(i);
			logger.debug("smsc is "+smscSession);
			rangeList=smscSession.getAddressRange();
			logger.debug("address list size is "+rangeList.size());
			for(int j=0;j<rangeList.size();j++){
				AddressRange addrRange = (AddressRange)rangeList.get(j); 
				logger.debug("address range object is "+addrRange);
				if(logger.isDebugEnabled()) {
					logger.debug("Going to match with "+addrRange.getTon()+" "
								+addrRange.getNpi()+" "+addrRange.getRange());
				}

				if(addrRange.getTon()!=destTon){
					if(logger.isDebugEnabled()) {
						logger.debug("'type of number' did not match");
					}
					continue;
				}else if(addrRange.getNpi()!=destNpi){
					if(logger.isDebugEnabled()) {
						logger.debug("'numbering plan indicator' did not match");
					}
					continue;
				}else {
					Pattern p = ((AddressRangeImpl)addrRange).getPattern();
					logger.debug("pattern is "+p);
					Matcher m = p.matcher(destAddr);
					logger.debug("matcher is "+m);
					boolean matched = m.matches();
					logger.debug("matched flag is " +matched);
					if(!matched){
						if(logger.isDebugEnabled()) {
							logger.debug("'range'did not match");
						}
						continue;
					}
				}
				logger.debug("adding to the list " +smscSession);
				smscList.add(smscSession);	
				logger.debug("added to the list ");
				break;
			} // for rangeList ends here
		} // for smsc list ends here
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving getMatchingSmsc() with no of SMSCs matched = "+smscList.size());
		}
		return smscList;
	}
}
