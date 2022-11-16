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
//      File:   SmppResourceAdaptorImpl.java
//
//      Desc:   This interface defines an SMPP request. This is the base class for all
//              the SMPP request and all the SMPP request must extend this. This class
//              contains set methods to set various field of an SMPP request and get
//              methods to get various fields of an SMPP request. This interface describes
//              in details all the API available to application.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Prashant Kumar              26/01/08        Initial Creation
//
/***********************************************************************************/
package com.baypackets.ase.ra.smpp.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.apache.log4j.Logger;

import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.ra.smpp.*;
import com.baypackets.ase.ra.smpp.server.receiver.DeliveryInfoSender;
import com.baypackets.ase.ra.smpp.server.receiver.PDUProcessorGroup;
import com.baypackets.ase.ra.smpp.server.receiver.SMSCListener;
import com.baypackets.ase.ra.smpp.server.receiver.SMSCListenerImpl;
import com.baypackets.ase.ra.smpp.server.receiver.SMSCSession;
import com.baypackets.ase.ra.smpp.server.receiver.ShortMessageStore;
import com.baypackets.ase.ra.smpp.server.receiver.SmppPDUProcessor;
import com.baypackets.ase.ra.smpp.server.receiver.SmppPDUProcessorFactory;
import com.baypackets.ase.ra.smpp.server.receiver.util.Attribute;
import com.baypackets.ase.ra.smpp.server.receiver.util.Record;
import com.baypackets.ase.ra.smpp.server.receiver.util.Table;
import com.baypackets.ase.ra.smpp.stackif.*;
import com.baypackets.ase.ra.smpp.utils.*;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseIc;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.ResourceAdaptor;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.internalservices.AlarmService;
	// TODO - only spi package should be used
import org.smpp.debug.*;
import org.smpp.SmppObject;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang3.StringUtils;

import com.baypackets.ase.ra.smpp.stackif.RangeToAppName;
import org.yaml.snakeyaml.constructor.Constructor;

public class SmppResourceAdaptorImpl
	implements SmppResourceAdaptor, ResourceAdaptor, CommandHandler{

	private static Logger logger = Logger.getLogger(SmppResourceAdaptorImpl.class);
	private static ResourceContext context;
	private static SmppResourceFactory smppFactory;
	private SmppConfMgr m_smppConfMgr;
	private SmscPollingThread m_poolingThread;
	private static File configFile;
	
	private static File xmlfile;
	private static File RuleYamlFile;
	private static String configfilelocation;
	private static String RulesConfigYamlFile;
	private static String measurementConfigfile;
	private short role = ResourceAdaptor.ROLE_STANDBY;
	private boolean canSendMessage = true;
	private int seqNumber=0;
	private Map requestMap;
	private Map rgstdRequestMap;
	private static Hashtable rangeMap;
	private static SmppResourceAdaptorImpl smppResourceAdaptor;
	private AseHost aseHost;
	private AlarmService alarmService;
	
	private RuleConfig ruleConfig;

	/**
	* The debug file object.
	*/
	static Debug debug = new FileDebug("","");

	/**
	* The event file object.
	*/
	static Event event = new FileEvent("","");

	// Measurement counter for bind operation
	private MeasurementCounter bindTxReqCnt;
	private MeasurementCounter bindTxResCnt;
	private MeasurementCounter bindRxReqCnt;
	private MeasurementCounter bindRxResCnt;
	private MeasurementCounter bindTRxReqCnt;
	private MeasurementCounter bindTRxResCnt;
	private MeasurementCounter outbindReqCnt;
	private MeasurementCounter unbindReqCnt;
	private MeasurementCounter unbindResCnt;
	private MeasurementCounter enquireReqCnt;
	private MeasurementCounter enquireResCnt;
	private MeasurementCounter genNackReqCnt;

	// SMPP mesage specific measurement couters
	private MeasurementCounter smppReqOutCnt;
	private MeasurementCounter smppResOutCnt;
	private MeasurementCounter smppReqInCnt;
	private MeasurementCounter smppResInCnt;
	private MeasurementCounter submitReqCnt;
	private MeasurementCounter submitResCnt;
	private MeasurementCounter submitMultiReqCnt;
	private MeasurementCounter submitMultiResCnt;
	private MeasurementCounter DataReqCnt;
	private MeasurementCounter DataResCnt;
	private MeasurementCounter DeliverReqCnt;
	private MeasurementCounter DeliverResCnt;
	private MeasurementCounter queryReqCnt;
	private MeasurementCounter queryResCnt;
	private MeasurementCounter replaceReqCnt;
	private MeasurementCounter replaceResCnt;
	private MeasurementCounter cancelReqCnt;
	private MeasurementCounter cancelResCnt;
	private MeasurementCounter alertReqCnt;
	// error counters
	private MeasurementCounter smppReqSendErrCnt;
	private MeasurementCounter smppResSendErrCnt;

	private SMSCListener smscListener;
	private SmppPDUProcessorFactory factory;
	private PDUProcessorGroup processors ;
	private ShortMessageStore messageStore;
	private DeliveryInfoSender deliveryInfoSender;
	private Table users;



	public SmppResourceAdaptorImpl(){
		logger.debug("creating SmppResourceAdaptor object.");
		smppResourceAdaptor=this;
	}

	/**
	 *	This method returns the instance of smppResourceAdaptor.
	 *
	 *	@return SmppResourceAdaptorImpl object.
	 */
	public static SmppResourceAdaptorImpl getInstance()	{
		if(smppResourceAdaptor==null){
			smppResourceAdaptor=new SmppResourceAdaptorImpl();
		}
		return smppResourceAdaptor;
	}

	/////////////////////////////// ResourceAdaptor methods begin ////////////////////////////////

	/**
	 *	This method can be used to initialize SMPP RA.This method initializes variouos 
	 *	maps,load the smpp-config.xml file,creates connection with all the SMSCs configured
	 *	in smpp-config.xml.
	 *
	 *	@param context -<code>ResourceContext</code> for this RA.
	 *
	 *	@throws ResourceException -Incase problem occured during initializaton.
	 */
	public void init(ResourceContext context) throws ResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Initializing Smpp resource adaptor.");
		}
		this.context = context;		
		this.smppFactory=(SmppResourceFactoryImpl)this.context.getResourceFactory();
		((SmppResourceFactoryImpl)this.smppFactory).init(this.context);
		
		// Get configuration role
		this.role = context.getCurrentRole();
		logger.debug("The system is " + (this.role == ResourceAdaptor.ROLE_ACTIVE ? "active" : "standby"));

		// Initialize alarm service 
		this.alarmService = context.getAlarmService();

		// initializing Measurement counters 
		logger.debug("Initializing measurement counters");

		MeasurementManager measurementMgr = context.getMeasurementManager();
	
		bindTxReqCnt = measurementMgr.getMeasurementCounter(Constants.BIND_TX_REQ_COUNTER);
		bindTxResCnt = measurementMgr.getMeasurementCounter(Constants.BIND_TX_RES_COUNTER);
		bindRxReqCnt = measurementMgr.getMeasurementCounter(Constants.BIND_RX_REQ_COUNTER);
		bindRxResCnt = measurementMgr.getMeasurementCounter(Constants.BIND_RX_RES_COUNTER);
		bindTRxReqCnt = measurementMgr.getMeasurementCounter(Constants.BIND_TRX_REQ_COUNTER);
		bindTRxResCnt = measurementMgr.getMeasurementCounter(Constants.BIND_TRX_RES_COUNTER);
		outbindReqCnt = measurementMgr.getMeasurementCounter(Constants.OUTBIND_REQ_COUNTER);
		unbindReqCnt = measurementMgr.getMeasurementCounter(Constants.UNBIND_REQ_COUNTER);
		unbindResCnt = measurementMgr.getMeasurementCounter(Constants.UNBIND_RES_COUNTER);
		enquireReqCnt = measurementMgr.getMeasurementCounter(Constants.ENQUIRELINK_REQ_COUNTER);
		enquireResCnt = measurementMgr.getMeasurementCounter(Constants.ENQUIRELINK_RES_COUNTER);
		genNackReqCnt = measurementMgr.getMeasurementCounter(Constants.GENERIC_NACK_REQ_COUNTER);
	
		smppReqOutCnt = measurementMgr.getMeasurementCounter(Constants.SMPP_REQ_OUT_COUNTER);
		smppResOutCnt = measurementMgr.getMeasurementCounter(Constants.SMPP_RES_OUT_COUNTER);
		smppReqInCnt = measurementMgr.getMeasurementCounter(Constants.SMPP_REQ_IN_COUNTER);
		smppResInCnt = measurementMgr.getMeasurementCounter(Constants.SMPP_RES_IN_COUNTER);
		submitReqCnt = measurementMgr.getMeasurementCounter(Constants.SUBMIT_REQ_COUNTER);
		submitResCnt = measurementMgr.getMeasurementCounter(Constants.SUBMIT_RES_COUNTER);
		submitMultiReqCnt = measurementMgr.getMeasurementCounter(Constants.SUBMITMULTI_REQ_COUNTER);
		submitMultiResCnt = measurementMgr.getMeasurementCounter(Constants.SUBMITMULTI_RES_COUNTER);
		DataReqCnt = measurementMgr.getMeasurementCounter(Constants.DATA_REQ_COUNTER);
		DataResCnt = measurementMgr.getMeasurementCounter(Constants.DATA_RES_COUNTER);
		DeliverReqCnt = measurementMgr.getMeasurementCounter(Constants.DELIVER_REQ_COUNTER);
		DeliverResCnt = measurementMgr.getMeasurementCounter(Constants.DELIVER_RES_COUNTER);
		queryReqCnt = measurementMgr.getMeasurementCounter(Constants.QUERY_REQ_COUNTER);
		queryResCnt = measurementMgr.getMeasurementCounter(Constants.QUERY_RES_COUNTER);
		replaceReqCnt = measurementMgr.getMeasurementCounter(Constants.REPLACE_REQ_COUNTER);
		replaceResCnt = measurementMgr.getMeasurementCounter(Constants.REPLACE_RES_COUNTER);
		cancelReqCnt = measurementMgr.getMeasurementCounter(Constants.CANCEL_REQ_COUNTER);
		cancelResCnt = measurementMgr.getMeasurementCounter(Constants.CANCEL_RES_COUNTER);
		alertReqCnt = measurementMgr.getMeasurementCounter(Constants.ALERT_NOTIFICATION_REQ_COUNTER);
		smppReqSendErrCnt = measurementMgr.getMeasurementCounter(Constants.SMPP_REQ_SEND_ERROR);
		smppResSendErrCnt = measurementMgr.getMeasurementCounter(Constants.SMPP_RES_SEND_ERROR);
		if(logger.isDebugEnabled()){
			logger.debug("Initialize measurement counters.");
		}
		// initialize stack logger
		
		aseHost=(AseHost) Registry.lookup(com.baypackets.ase.util.Constants.NAME_HOST);
		this.requestMap = new Hashtable(65063);
		this.rgstdRequestMap = new Hashtable(65063);
		this.rangeMap = new Hashtable(24);


		configfilelocation= (String)context.getConfigProperty("ase.home") +File.separator + "conf"
							+ File.separator + "smpp-config.xml";
   
		configFile= new File(configfilelocation); 
      

		loadConfigParam (configFile);


		RulesConfigYamlFile  = (String)context.getConfigProperty("ase.home") + File.separator + "conf"
							+ File.separator + "SmppServerAppRules.yaml";

		RuleYamlFile  = new File(RulesConfigYamlFile);
     


		ruleConfig = YamlObjectMapper.getSmppServerConfig(RuleYamlFile);

		
		if(logger.isDebugEnabled()){
			logger.debug("Smpp Server configurations read are :- "+ ruleConfig);
		}
		 //start port for receiver //get ip of server,potrt ect start smpp reciever thread then start register smse
			if(logger.isDebugEnabled()){
				logger.debug("Initilizing Smpp reciever through SmppResource");
			}
			
	     smscListener= new SMSCListenerImpl(ruleConfig.getPortNumber(), true);
	     smscListener.setAcceptTimeout(ruleConfig.getReceiverTimeout());
	     processors= new PDUProcessorGroup();
	     messageStore = new ShortMessageStore();
	     deliveryInfoSender =new DeliveryInfoSender();
	     users=ruleConfig.getUsers();
	     factory = new SmppPDUProcessorFactory(processors, messageStore, deliveryInfoSender, users,(SmppResourceFactoryImpl)smppFactory,this,ruleConfig.getSendEnquireLink());
	     smscListener.setPDUProcessorFactory(factory);
		
   	if(logger.isDebugEnabled()){
		logger.debug("Reading smppAppRules an registerEsme ");
		logger.debug("Rules list"+ ruleConfig.getRules());
	}
		List<Rule> listOfrule = ruleConfig.getRules();

		for(Rule rule : listOfrule){

			AddressRangeImpl range = new AddressRangeImpl(rule.getTon() , rule.getNpi(), rule.getRange());
			String appName = rule.getAppName();
			smppFactory.registerEsme(range,appName);
		}
		//createSmscConnection();
	
		if(logger.isDebugEnabled()){
			logger.debug("Initialized Smpp resource adaptor.");
		}
	}

	/**
	 *	This method can be used to start SMPP RA.
	 *
	 *	@throws ResourceException -Incase problem occured during initializaton.
	 */
	public void start() throws ResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside start().");
		}
		if(this.role!=ResourceAdaptor.ROLE_ACTIVE){
			logger.error("Standby SAS.Doing nothing");
			return;
		}
		if(logger.isDebugEnabled()){
			logger.debug("starting reciever smpp");
		}
		
		
		try {
			logger.debug("Starting delveryInfo sender");
			deliveryInfoSender.start();
			logger.debug("Starting smsc listener sender");
			smscListener.start();
		} catch (IOException e) {
			logger.error("error while running smpp server :- "+ e);
		}
		
		
		if(ruleConfig.getDisableClient()!=null &&Boolean.valueOf(ruleConfig.getDisableClient())) {
			if(logger.isDebugEnabled()){
				logger.debug("Disable client is enabled so not starting client");
			}
		}else {
			if(logger.isDebugEnabled()){
				logger.debug("Creating connections with SMSCs.");
			}
			startPollingThread();
			if(logger.isDebugEnabled()){
				logger.debug("starting telnet adaptor.");
			}
			TelnetAdaptor telnetAdaptor = new TelnetAdaptor(m_smppConfMgr);
			telnetAdaptor.start();	
		}
		//createSmscConnection();
		
		if(logger.isDebugEnabled()){
			logger.debug("Leaving start().");
		}
	}

	/**
	 *	This method can be used to stop SMPP RA.This method unbinds the connections
	 *	with all the SMSCs,it made connection previously.
	 *
	 *	@throws ResourceException -Incase problem occured during initializaton.
	 */
	public void stop() throws ResourceException {
		if(logger.isDebugEnabled()){
			logger.debug("Inside stop().");
		}
		stopPollingThread();
		//unbindSmscConnection();
		if(logger.isDebugEnabled()){
			logger.debug("Leaving stop().");
		}
	}

	/**
	 *	This method sends the SMPP RA specific alarms.
	 *	
	 *	@param code - alarm code associated with the alarm.
	 *	@param msg 	- alarm message associated with the alarm.
	 */
	public void sendSmppAlarm(int code,String msg){
		if(logger.isDebugEnabled()){
			logger.debug("Inside sendSmppAlarm().");
		}
		try{
			this.alarmService.sendAlarm(code,msg);
		}catch(Exception ex){
			logger.error("Exception in sending alarm ",ex);
		}
		if(logger.isDebugEnabled()){
			logger.debug("Leaving sendSmppAlarm().");
		}
	}

	/**
	 *	This method can be used to load various fileds mentioned in smpp-config.xml.
	 *	This method parses the smpp-config.xml file, reads it different fields,creates 
	 *	different class objects required,fill the fields values into class objects.
	 *
	 *	@param file -<code>smpp-config.xml</code> file,which is the configuration file
	 *					for SMPP RA.
	 */

	private void loadConfigParam(File file) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside loadConfigParam(File)");
		}
		try{
			logger.debug("instantiate SmppConfMgr");
			m_smppConfMgr = new SmppConfMgr();
			Digester digester = new Digester();
			SmppRuleSet ruleSet = new SmppRuleSet(m_smppConfMgr);
			ruleSet.addRuleInstances(digester);
			digester.parse(file);
		} catch (Exception ex) {
			logger.error("Error occured during Parsing config file",ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving loadConfigParam(File)");
		}
	}

	/**
	 *	This method is used to bind with different SMSCs configured in smpp-config.xml
	 *	This method calls createSmscConnection() method on <code>SmppConfMgr</code> 	
	 *	which binds all the SMSCs.
	 *
	 */
	 /*
	private void createSmscConnection(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside SmppResourceAdaptorImpl createSmscConnection()");
		}
		m_smppConfMgr.createSmscConnection(this);		
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving SmppResourceAdaptorImpl createSmscConnection(){");
		}
	}
	*/

	private void startPollingThread(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside startPollingThread()");
		}
		m_poolingThread=new SmscPollingThread();
		m_poolingThread.setSmscList(m_smppConfMgr.getSmscList());
		m_poolingThread.startPolling();
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving startPollingThread()");
		}
	}

	private void stopPollingThread(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside stopPollingThread()");
		}
		m_poolingThread.stopPolling();
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving stoptPollingThread()");
		}

	}

	/**
	 *	This method is used to unbind with different SMSCs binded earlier by RA.This
	 *	method calls stopSmscConnection() method on <code>SmppConfMgr</code> 	
	 *	which unbinds all the SMSCs previously binded.
	 *
	 */
	 /*
	private void unbindSmscConnection(){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside SmppResourceAdaptorImpl stopSmscConnection()");
		}
		m_smppConfMgr.stopSmscConnection();		
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving SmppResourceAdaptorImpl stopSmscConnection(){");
		}
	}
	*/

	public void configurationChanged(String arg0, Object arg1) throws ResourceException {
	}

	public void roleChanged(String clusterId, String subsystemId, short role) {
		if(logger.isDebugEnabled()) {
			logger.debug("roleChanged(): role is changed to " + role);
		}
		short preRole=this.role;
		this.role=role;
		if(preRole!=ResourceAdaptor.ROLE_ACTIVE && 
			role==ResourceAdaptor.ROLE_ACTIVE){
			try{
				this.start();
			}catch(Exception ex){
				logger.error("Exception in roleChanged.",ex);
			}
		}
	}

	/**
	 *	This method is used to send Smpp Messages to stack.Application calls
	 *	send() Smpp message which inturn calls this method.
	 *
	 *	@param message -<code>SasMessage</code> to be sent to stack.
	 *
	 *	@throws IOException -If an IO Exception occurs during sending.
	 */
	public void sendMessage(SasMessage message) throws IOException {
		if(logger.isDebugEnabled()) {
			logger.debug("Sending message: " + message);
		}
		try{
			if(message instanceof SmppRequest) {
				this.handleRequest((SmppRequest)message);
				//this.updateRequestCounter(((SmppRequest)message).getType());
			//	replicate((SmppSession)((SmppRequest)message).getSession(),"RESOURCE");
			} else if (message instanceof SmppResponse) {
				this.handleResponse((SmppResponse)message);
				//this.updateResponseCounter(((SmppResponse)message).getType());
				//replicate((SmppSession)((SmppResponse)message).getSession(),"CLEAN_UP");
			} else {
				logger.error("Message dropped: not an Smpp message.");
				throw new IllegalArgumentException("Not an Smpp message");
			}
		} catch (ResourceException ex) {
			if(message instanceof SmppRequest) {
				this.smppReqSendErrCnt.increment();
			}else if (message instanceof SmppResponse) {
				this.smppResSendErrCnt.increment();
			}
			logger.error("sendMessage() failed ", ex);			
			throw new IllegalArgumentException(ex.getMessage());
		}
	}

	/**
	 *	This method is used to get the <code>SmscSession</code> which is 
	 *	to be used to send this <code>SasMessage</code>.This method calls 
	 *	getSmscSession method on <code>SmppConfMgr</code>,which checks, out
	 *	of all the binded SMSCs,which all SMSCs serves the address-range
	 *	this message falls in and returns the list of all the <code>SmscSession</code>
	 *	corresponoding to these SMSCS.(one messages can fall in the address-range of
	 *	more than one SMSC).Now this method randonly choose one </code>SmscSession</code>
	 *	and returns.
	 *
	 *	@param message -<code>SasMessage</code> to be sent to stack.
	 *
	 *	@return <code>SmscSession</code> which can be used to send this message.
	 *
	 *	@throws ResourceException -If any Exception occurs during sending.
	 */
	private SmscSession getSmsc(SasMessage message) throws ResourceException {
		Random rand;
		SmscSession smscSession = null;
		SmscSession smscSecondarySession = null;
		boolean primaryInilialized= false;
		boolean secondaryInilialized = false;
       	ArrayList smscList = m_smppConfMgr.getSmscSession(message);
		if(logger.isDebugEnabled()) {
			logger.debug("Number of SMSCs =" +smscList.size());
			logger.debug("SMSC:- "+ smscList.toString());
		}
		if(smscList==null || smscList.size()==0){
			logger.error("None of the SMSCs is in binded or transmitter state.");
			throw new ResourceException("None of the SMSCs is in binded or transmiter state");
		}
		// iterate list for ACTIVE and Primary first then it not there then iterate for secondary and active
		try{
			String selectionMode = "FIRST_AVAILABLE";
			
			switch(selectionMode) {
			
			case "FIRST_AVAILABLE": {
				
				for(int i=0;i<smscList.size();i++) {
					SmscSession tmpSession= (SmscSession)smscList.get(i);
					if(tmpSession.isBound() && tmpSession.isPrimary() && StringUtils.equalsIgnoreCase(tmpSession.getCurrentStatus(), Constants.STATUS_ACTIVE)
							&& !primaryInilialized) {
				    logger.debug("Primary smsc picked with value:-"+ tmpSession);
						smscSession=tmpSession;
						primaryInilialized=true;
					}else if(tmpSession.isBound() && StringUtils.equalsIgnoreCase(tmpSession.getCurrentStatus(), Constants.STATUS_ACTIVE)
							&& !secondaryInilialized) {
						 logger.debug("Secondary smsc inilialized:-"+ tmpSession);
						 smscSecondarySession = tmpSession;
						 secondaryInilialized=false;
					}
					
			    }
				
				
				
				if(smscSession !=null) {
                logger.debug("Returning primary smscSession :- "+ smscSession.toString());
					return smscSession;
				}else if (smscSecondarySession!=null) {
					 logger.debug("Returning secondary smscSession :- " + smscSecondarySession.toString());
					return smscSecondarySession;
				}else {
					 logger.debug("Found neither primary nor secondary so returning random Session ");
					rand = new Random();
					int index=rand.nextInt(smscList.size());
					smscSession = (SmscSession)smscList.get(index);
					return smscSession;
				}				
		
			}
			default:
                logger.error("Error setting selection mode " + selectionMode );
                throw new ResourceException("Error setting selection mode " + selectionMode);
		}
				
		}catch(Exception ex){
			logger.error("problem in getting SMSC ",ex);
			throw new ResourceException(ex.getMessage());
		}
	}

	/**
	 *	This method checks the state of an <code>SmscSession</code>.If session is
	 *	not in bound state, throws Exception.
	 *
	 *	@param session <code>SmscSession</code> whose state is to be checked.
	 *
	 *	@throws ResourceException -If <code>SmscSession</code> is not in bound state.
	 */
	private void checkSmscSession(SmscSession session) throws ResourceException {
		SmscSession smscSession=session;
		if(!smscSession.isBound() || StringUtils.equalsIgnoreCase(smscSession.getCurrentStatus(),Constants.STATUS_DOWN)) {
			logger.error("SMSC "+smscSession.getName()+" is not bound... cannot send message:- " + smscSession.toString());
			smscSession.setCurrentStatus(Constants.STATUS_DOWN);
       
		throw new ResourceException("SMSC "+smscSession.getName() +" is not bound... cannot send message");
		}

	}

	/**
	 *	This method is called by sendMessage() method to handle any 
	 *	<code>SmppRequest</code> coming from application to be sent
	 *	to stack.This method gets a unique sequence number,sets in into
	 *	the request,gets <code>SmscSession</code> for this request(which 
	 *	is to be used to sent this request),sets the <code>SmscSession</code>
	 *	into request (further messages can get <code>SmscSession</code> from 
	 *	this request),and send this message to stack using <code>SmscSession</code>.
	 *
	 *	@param request <code>SmppRequest</code> to be sent.
	 *
	 *	@throws ResourceException -If problem in sending <code>SmscRequest</code>.
	 */
	private void handleRequest(SmppRequest request) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside handleRequest()");
		}
		/*if (request == null) {
			logger.error("handleRequest(): null request");
			throw new Exception("Null request argument passed");
		}*/
		
		SmscSession smscSession=null;
		int requestType=0;
		int seqNum = this.getNextSeqNumber();
		if(request instanceof SubmitSM){
			if(logger.isDebugEnabled()) {
				logger.debug("handling SubmitSM");
			}
			seqNum=((SubmitSM)request).getSequenceNumber();
			((SubmitSM)request).setRegisteredDelivery((byte) 1);
			smscSession=getSmsc((SasMessage)request);
			checkSmscSession(smscSession);
			
			((SubmitSM)request).setSmscSession(smscSession);
			this.requestMap.put(new Integer(seqNum), request);
			requestType=Constants.SUBMIT_SM_REQ;
		} else if(request instanceof DataSM){
			if(logger.isDebugEnabled()) {
				logger.debug("handling DataSM");
			}
			seqNum=((DataSM)request).getSequenceNumber();
			smscSession=getSmsc((SasMessage)request);
			checkSmscSession(smscSession);
			((DataSM)request).setSmscSession(smscSession);
			this.requestMap.put(new Integer(seqNum), request);
			requestType=Constants.DATA_SM_REQ;
		} else if(request instanceof SubmitMultiSM){
			if(logger.isDebugEnabled()) {
				logger.debug("handling SubmitMultiSM");
			}
			seqNum=((SubmitMultiSM)request).getSequenceNumber();
			smscSession=((SubmitMultiSM)request).getSmscSession();
			checkSmscSession(smscSession);
			this.requestMap.put(new Integer(seqNum), request);
			requestType=Constants.SUBMIT_SM_MULTI_REQ;
		} else if(request instanceof CancelSM){
			if(logger.isDebugEnabled()) {
				logger.debug("handling CancelSM");
			}
			smscSession=((CancelSM)request).getSmscSession();
			checkSmscSession(smscSession);
			((CancelSM)request).setSequenceNumber(seqNum);
			this.requestMap.put(new Integer(seqNum), request);
			requestType=Constants.CANCEL_SM_REQ;
		} else if(request instanceof QuerySM){
			if(logger.isDebugEnabled()) {
				logger.debug("handling QuerySM");
			}
			smscSession=((QuerySM)request).getSmscSession();
			checkSmscSession(smscSession);
			((QuerySM)request).setSequenceNumber(seqNum);
			this.requestMap.put(new Integer(seqNum), request);
			requestType=Constants.QUERY_SM_REQ;
		} else if(request instanceof ReplaceSM){
			if(logger.isDebugEnabled()) {
				logger.debug("handling ReplaceSM");
			}
			smscSession=((ReplaceSM)request).getSmscSession();
			checkSmscSession(smscSession);
			((ReplaceSM)request).setSequenceNumber(seqNum);
			this.requestMap.put(new Integer(seqNum), request);
			requestType=Constants.REPLACE_SM_REQ;
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Sequence number is "+seqNum);
		}
		if(smscSession!=null){
			try{
				smscSession.handleRequest(request);
				this.updateRequestCounter(requestType,false);
			}catch(Exception ex){
				logger.error("Problem in sending smpp request ");
				throw new ResourceException("Problem in sending smpp request",ex);
			}
		} else{
			throw new ResourceException("Could not find any SMSC for ths request");
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving handleRequest() with map size "+this.requestMap.size());
		}
	}

	/**
	 *	This method is called by sendMessage() method to handle any 
	 *	<code>SmppResponse</code> coming from application to be sent
	 *	to stack.This method gets <code>SmscSession</code> for this response,
	 *	sets the <code> and send this message to stack using 
	 *	this <code>SmscSession</code>.
	 *
	 *	@param response <code>SmppResponse</code> to be sent.
	 *
	 *	@throws ResourceException -If problem in sending <code>SmscResponse</code>.
	 */
	private void handleResponse(SmppResponse response) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside handleResponse()");
		}
		SmscSession smscSession=null;
		SMSCSession smscSessionResponse=null;
		SmppPDUProcessor smppPDUProcessor=null;
		int responseType=-1;
		if(response instanceof DataSMResp){
			smscSession=((DataSMResp)response).getSmscSession();	
			responseType=Constants.DATA_SM_RES;
		} else if(response instanceof DeliverSMResp){
			smscSession=((DeliverSMResp)response).getSmscSession();	
			responseType=Constants.DELIVER_SM_RES;
		}else if(response instanceof SubmitSMResp) {
			smscSessionResponse=((SubmitSMResp)response).getSmscSessionResponse();	
			smppPDUProcessor= ((SubmitSMResp)response).getSmppPDUProcessor();
			responseType=Constants.SUBMIT_SM_RES;
		}
		try{
			if(response instanceof SubmitSMResp) {
				smppPDUProcessor.serverResponse(((SubmitSMResp)response).getStackObj());
				
			}else {
				smscSession.handleResponse(response);
			}
			this.updateResponseCounter(responseType,false);
		}catch(Exception ex){
			logger.error("Problem in sending smpp response ");
			throw new ResourceException("Problem in sending smpp response",ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving handleResponse()");
		}
	}

	public void processed(SasMessage arg0) {
	}

	public void failed(SasMessage arg0, AseInvocationFailedException arg1) {
		logger.error("Message failed", arg1);
	}

	/////////////////////////////// ResourceAdaptor methods end //////////////////////////////////

	/////////////////////////////// SmppResourceAdaptor methods begin //////////////////////////////

	/**
	*  This method is used to deliver any <code>SmppRequest</code> coming from the
	*  SMSC to application.Underlying stack calls handleEvent(ServerPDUEvent) on 
	*  StackListener class which is a callback listener registered with stack for 
	*  a <code>SmscSession</code>.This listener calls this method which inturns 
	*  deliver the request to application.
	*
	*  @param resp - <code>SmppRequest</code> object to be delivered to application.
	*
	*  @throws ResourceException -If problem in delivering request to application.
	*/
	public void deliverRequest(SmppRequest request) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside deliverRequest()");
		}
		Address address=null;
		String appName;
		AseContext aseContext=null;
		AseApplicationSession appSession=null;
		String protocol=Constants.PROTOCOL;
		AseIc ic=null;
		SmppSession smppSession;
		boolean isRgstdReq=false;
		String messageId=null;
		int sequenceNo=-1;
		if(request instanceof DataSM){
			if(logger.isDebugEnabled()) {
				logger.debug("DataSM request received");
			}
			if(((DataSM)request).hasReceiptedMessageId()){
				// If recepted message id present
				if(logger.isDebugEnabled()) {
					logger.debug("DeliverSM receipted request received");
				}
				messageId=((DataSM)request).getReceiptedMessageId();
				if(messageId!=null){
					isRgstdReq=true;
				}	
			}else{
				// if message id is in short message
				if(logger.isDebugEnabled()) {
					logger.debug("Searching receipt in short message");
				}
				short paramName=Constants.OPT_PAR_MSG_PAYLOAD;
				byte[] data=((DataSM)request).getOptParam(paramName);
				String msg= new String(data);
				if(msg!=null){
					if(logger.isDebugEnabled()) {
						logger.debug("Short message is "+msg);
					}
					messageId=findMessageId(msg);
					if(messageId!=null){
						isRgstdReq=true;
					}
				}
			}
			if(isRgstdReq){
				SmppRequest savedRequest =  getRgstdRequestFromMAp(messageId);//(SmppRequest)this.rgstdRequestMap.get(messageId);
				logger.debug("SMPP Req is "+savedRequest);
				appSession=(AseApplicationSession)savedRequest.getApplicationSession();
				smppSession=(SmppSession)savedRequest.getSession();
				((AbstractSmppRequest)request).setProtocolSession(smppSession);
				sequenceNo=savedRequest.getSequenceNumber();
				if(logger.isDebugEnabled()) {
					logger.debug("Application session is "+appSession);
				}
				if(logger.isDebugEnabled()) {
					logger.debug("Smpp session is "+smppSession);
				}
				// no need to replicate dataSM
				//replicate((SmppSession)((SmppRequest)request).getSession(),"RESOURCE");
			}else{
				address=((DataSM)request).getDestinationAddr();
			}
		}
		else if (request instanceof SubmitSM) {
			if(logger.isDebugEnabled()) {
				logger.debug("SubmitSm request received");
			}
				// if message id is in short message
				if(logger.isDebugEnabled()) {
					logger.debug("Searching receipt in short message");
				}
				String msg=((SubmitSM)request).getShortMessage();
				if(msg!=null){
					if(logger.isDebugEnabled()) {
						logger.debug("Short message is "+msg);
					}
					messageId=findMessageId(msg);
					isRgstdReq=false;
				
			}
			if(isRgstdReq){
				SmppRequest savedRequest =getRgstdRequestFromMAp(messageId);
				appSession=(AseApplicationSession)savedRequest.getApplicationSession();
				logger.debug("SMPP Req is "+savedRequest);
				smppSession=(SmppSession)savedRequest.getSession();
				((AbstractSmppRequest)request).setProtocolSession(smppSession);
				sequenceNo=savedRequest.getSequenceNumber();
				//smppSession.associateMessageId(messageId,sequenceNo);
				if(logger.isDebugEnabled()) {
					logger.debug("Application session is "+appSession);
				}
				if(logger.isDebugEnabled()) {
					logger.debug("Smpp session in SubmitSM is "+smppSession);
				}
				// no need to replicate DeliverSM
				//replicate((SmppSession)((SmppRequest)request).getSession(),"RESOURCE");
			}else{
				logger.debug("getting address in submitSM ");
				address=((SubmitSM)request).getDestinationAddr();
			}
		}


		else if(request instanceof DeliverSM){
			if(logger.isDebugEnabled()) {
				logger.debug("DeliverSM request received");
			}
			if(((DeliverSM)request).hasReceiptedMessageId()){
				// If recepted message id present
				if(logger.isDebugEnabled()) {
					logger.debug("DeliverSM receipted request received");
				}
				messageId=((DeliverSM)request).getReceiptedMessageId();
				if(messageId!=null){
					isRgstdReq=true;
				}	
			}else{
				// if message id is in short message
				if(logger.isDebugEnabled()) {
					logger.debug("Searching receipt in short message");
				}
				String msg=((DeliverSM)request).getShortMessage();
				if(msg!=null){
					if(logger.isDebugEnabled()) {
						logger.debug("Short message is "+msg);
					}
					messageId=findMessageId(msg);
					isRgstdReq=true;
				}
			}
			if(isRgstdReq){
				SmppRequest savedRequest = getRgstdRequestFromMAp(messageId);
				appSession=(AseApplicationSession)savedRequest.getApplicationSession();
				logger.debug("SMPP Req is "+savedRequest);
				smppSession=(SmppSession)savedRequest.getSession();
				((AbstractSmppRequest)request).setProtocolSession(smppSession);
				sequenceNo=savedRequest.getSequenceNumber();
				//smppSession.associateMessageId(messageId,sequenceNo);
				if(logger.isDebugEnabled()) {
					logger.debug("Application session is "+appSession);
				}
				if(logger.isDebugEnabled()) {
					logger.debug("Smpp session is "+smppSession);
				}
				// no need to replicate DeliverSM
				//replicate((SmppSession)((SmppRequest)request).getSession(),"RESOURCE");
			}else{
				address=((DeliverSM)request).getDestinationAddr();
			}
		}else if(request instanceof AlertNotification){
			if(logger.isDebugEnabled()) {
				logger.debug("AlertNotification request received");
			}
			// this is done beacuse stack does not provide any direct methos to
			//	get the destination address from AlertNotification request.
			address=readStackRequest(request);
		}else{
			logger.error("Unknown request type received from stack");
			throw new ResourceException("Unknown request type received from stack");
		}


		if(!isRgstdReq){
			if(address==null){
				throw new ResourceException("Destinaiton address not specified in request");
			}
			try{
				appName=getDestinationApp(address);
				if(logger.isDebugEnabled()) {
					logger.debug("application name matched is "+appName);
				}
				DeployerFactory deployerFactory = (DeployerFactory)Registry.lookup(
								com.baypackets.ase.util.Constants.NAME_DEPLOYER_FACTORY);
				//Deployer deployer = deployerFactory.getDeployer(
				//							DeployableObject.TYPE_SERVLET_APP);
			
				Iterator depItr = deployerFactory.getAllDeployer();
				while(depItr.hasNext()){
					Deployer deployer = (Deployer)depItr.next();
					Iterator itr=deployer.findByName(appName);
					while(itr.hasNext()){
						aseContext=(AseContext)itr.next();
						if(logger.isDebugEnabled()) {
							logger.debug("element is "+aseContext);
						}
						if(aseContext.getState()==DeployableObject.STATE_ACTIVE){
							break;
						}
					}
				}
				if(logger.isDebugEnabled()) {
					logger.debug("AseContext found is "+aseContext);
				}
				if(aseContext==null){
					throw new ResourceException("No AseContext found for applicaiton");
				}
				appSession=aseContext.createApplicationSession(protocol,ic,"");	
				smppSession=(SmppSession)smppFactory.createSession(appSession);
				((AbstractSmppRequest)request).setProtocolSession(smppSession);
				smppSession.addRequest(request);
			}catch(Exception ex){
				logger.error("Exception in getting app session ",ex);
				throw new ResourceException(ex);
			}
		} // if(!isRgstdReq) ends
		/* if(isRgstdReq){
			//smppSession.addRegisteredRequest(request);
		}else{
			smppSession.addRequest(request);
		}*/

		try {
			// TODO replicate((SmppSession)((SmppRequest)request).getSession(),"RESOURCE");
			this.context.deliverMessage(request, true);
		} catch(Throwable t) {
			logger.error("problem in delivering request to application", t);
		}
		
		if(logger.isDebugEnabled()) {
			logger.debug("responding to the incoming request()");
		}
		try{
			if(request instanceof DataSM ){
				((DataSM)request).respond();
				//this.rgstdRequestMap.remove(((DataSM)request).getReceiptedMessageId());
			}else if(request instanceof DeliverSM){
				((DeliverSM)request).respond();
				//this.rgstdRequestMap.remove(((DeliverSM)request).getReceiptedMessageId());
			}else if(request instanceof SubmitSM) {
				logger.debug("Inside SubmitSM response going outside query");
				SMSCSession smscSessionResponse= ((SubmitSM) request).getSmscSessionResponse();
				SmppPDUProcessor smppPDUProcessor= ((SubmitSM) request).getSmppPDUProcessor();
				((SubmitSM)request).response(smscSessionResponse,smppPDUProcessor);
			}
			// removing registerd request from registered map.This is done after 
			// sending response to Registered request as if we remove it before 
			// sending response and response is not been sent to SMSC then SMSC 
			// will re-send the deliver request.In this case we wont find the 
			// cossponding SmppRequest in the map.
			if(isRgstdReq){
				if(logger.isDebugEnabled()) {
					logger.debug("removing req from registerd map." + messageId);
				}
				removeRgstdRequestFromMap(messageId);
				//this.rgstdRequestMap.remove(messageId);
			}
		}catch(Exception ex){
			logger.error("Exception in responding to incoming request",ex);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving deliverRequest()");
		}
	}

	/**
	*  This method is used to deliver any <code>SmppResponse</code> coming from the
	*  SMSC to application.Underlying stack calls handleEvent(ServerPDUEvent) on 
	*  StackListener class which is a callback listener registered with stack for 
	*  a <code>SmscSession</code>.This listener calls this method which inturns 
	*  deliver the response to application.
	*
	*  @param resp - <code>SmppResponse</code> object to be delivered to application.
	*
	*  @throws ResourceException -If problem in delivering response to application.
	*/
	public void deliverResponse(SmppResponse response) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside deliverResponse() "+response.getSequenceNumber());
		}
		try{	
			SmppRequest request;
			SmppSession smppSession;
			request=(SmppRequest)requestMap.remove(new Integer(response.getSequenceNumber()));	
			if(request==null){
				logger.error("No corrosponding request found");
				// TODO   should throw exception or not
			}

			if(response instanceof SubmitSMResp){
				((SubmitSMResp)response).setRequest(request);
				logger.debug("SubmitSM response in SmppResourceAdapterImpl " +(SubmitSMResp)response);
				((SubmitSM)request).setMessageId(response.getMessageId());
				if((((SubmitSM)request).getRegisteredDelivery())==1){
					if(logger.isDebugEnabled()) {
						logger.debug("Adding SubmitSM to registered req map "+request);
					}
					addRgstdRequestToMap(response.getMessageId(),request);
					smppSession=(SmppSession)request.getSession();
					smppSession.associateMessageId(response.getMessageId(),
													request.getSequenceNumber());
					replicate((SmppSession)((SmppRequest)request).getSession(),"RESOURCE");
				}
			}else if(response instanceof SubmitMultiSMResp){
				((SubmitMultiSMResp)response).setRequest(request);
				((SubmitMultiSM)request).setMessageId(response.getMessageId());
				if((((SubmitMultiSM)request).getRegisteredDelivery())==1){
					if(logger.isDebugEnabled()) {
						logger.debug("Adding SubmitMultiSM to registered req map "+request);
					}
					addRgstdRequestToMap(response.getMessageId(),request);
					smppSession=(SmppSession)request.getSession();
					smppSession.associateMessageId(response.getMessageId(),
													request.getSequenceNumber());
					replicate((SmppSession)((SmppRequest)request).getSession(),"RESOURCE");
				}
			}else if(response instanceof DataSMResp){
				((DataSMResp)response).setRequest(request);
				((DataSM)request).setMessageId(response.getMessageId());
				if((((DataSM)request).getRegisteredDelivery())==1){
					if(logger.isDebugEnabled()) {
						logger.debug("Adding DataSM to registered req map "+request);
					}
					addRgstdRequestToMap(response.getMessageId(),request);
					smppSession=(SmppSession)request.getSession();
					smppSession.associateMessageId(response.getMessageId(),
													request.getSequenceNumber());
					replicate((SmppSession)((SmppRequest)request).getSession(),"RESOURCE");
				}
			}else if(response instanceof CancelSMResp){
				((CancelSMResp)response).setRequest(request);
				//((CancelSM)request).setMessageId(response.getMessageId());
			}else if(response instanceof QuerySMResp){
				((QuerySMResp)response).setRequest(request);
				((QuerySM)request).setMessageId(response.getMessageId());
			}else if(response instanceof ReplaceSMResp){
				((ReplaceSMResp)response).setRequest(request);
				//((ReplaceSM)request).setMessageId(response.getMessageId());
			}
			if(logger.isDebugEnabled()) {
				logger.debug("setting protocol session into response");
			}
			((AbstractSmppResponse)response).setProtocolSession(
					(SmppSession)((AbstractSmppRequest)request).getProtocolSession());
		}catch(Throwable t){
			logger.error("Exception in reading response ", t);
		}

		try {
			if(logger.isDebugEnabled()) {
				logger.debug("delivering response to context");
			}
			this.context.deliverMessage(response, true);
			//replicate((SmppSession)((SmppResponse)response).getSession(),"CLEAN_UP");
		} catch(Throwable t) {
			logger.error("Exception in Delivering response to application", t);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving deliverResponse()");
		}
	}

	/**
	 *	This method searches the corrosponding SmppRequest for which this 
	 *	delivery receipt is being sent by SMSC.After getting 
	public void deliverReceiptedRequest(SmppRequest request){


	}

	/**
	*  This method is used to deliver any <code>SmppResourceEvent</code> to the 
	*  application.This can be used by SMPP RA or StackListener to notify 
	*  application about any event.
	*
	*  @param event - <code>SmppResourceEvent</code> object to be delivered to application.
	*
	*  @throws ResourceException -If problem in delivering event to application.
	*/
	public void deliverEvent(SmppResourceEvent event) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside deliverEvent()");
		}
		boolean deliverUpward=true;
		AseContext aseContext=null;
		AseApplicationSession appSession=null;
		AseIc ic=null;

		if(deliverUpward == true && this.context != null){
			this.context.deliverEvent(event, true);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving deliverEvent()");
		}
	}

	/**
	 *	This method is used to save the <code>AddressRange</code> of a
	 *	application.Any Incoming SMPP request from network which falls in
	 *  this range, will be routed to this applicaition by the SMPP RA.
	 *
	 *	@param range <code>AddressRange</code> which this application will serve.
	 *  @param appName -name of the application.
	 *
	 *  @return boolean - true if successfully added to map otherwise false.
	 */
	public static boolean registerAppRange(AddressRange range,String appName) {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside registerAppRange() " +range +" appname "+ appName);
		}
		boolean addedSuccessfully=true;

		try{
			rangeMap.put(range,appName);
		}

		catch(Exception ex){
			logger.error("Problem in adding application range",ex);
			addedSuccessfully=false;
		}


		if(logger.isDebugEnabled()) {
			logger.debug("Leaving registerAppRange() with flag "+addedSuccessfully);
		}
		return addedSuccessfully;
	}

	/**
	 *	This method returns the name of the application where any incoming request from
	 *	network is to be routed.RA maches the <code>Address</code> of incoming message
	 *	with the <code>AddressRange</code> of all the applications,which they serve
	 *	and choose the matched application.Then the incoming message  will be routed 
	 *	to this applicaition by the SMPP RA.
	 *
	 *	@param address -destination <code>Address</code> parameter of incoming 
	 *					Smpp message.
	 *
	 *  @return String -name of the application where this message is to be routed.
	 */
	public String getDestinationApp(Address address) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("Inside getDestinationApp()");
		}
		int destTon=address.getTon();
		int destNpi=address.getNpi();
		AddressRange addrRange=null;

		String destAddr=address.getRange();
		String appName=null;

		Enumeration rangeEnum = rangeMap.keys();
		while(rangeEnum.hasMoreElements()){
			addrRange = (AddressRange)rangeEnum.nextElement();
		
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
				break;
			}
		} // while ends
		if(addrRange!=null){
			appName = (String)rangeMap.get(addrRange);
			if(appName==null){
				logger.error("No application found for incoming request");
				throw new ResourceException("No application found for incoming request");
			}
		}else {
			throw new ResourceException("address range is null in map");
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Leaving getDestinationApp() with app name "+appName);
		}
		return appName;
	}

	/**
	 *	This method searches the message-Id provided in short message 
	 *	field of any deliver receipt request.
	 *
	 *	@param message-Short-message which contains the id.
	 *
	 *	@return message-id associated with the delivery receipt request.
	 */
	private String findMessageId(String message){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside findMessageId()");
		}
		String msgId=null;
		String[] array=message.split(" ");
		for(int i=0;i<array.length;i++){
			String token=array[i];
			token=token.trim();
			String[] innerArray=token.split(":");
			String key=innerArray[0];
			if(key.equalsIgnoreCase("id")){
				msgId=innerArray[1];
				break;
			}
		}
		if(logger.isDebugEnabled()) {
			logger.debug("returning message id "+msgId);
		}
		return msgId;
	}

	/**
	 *	This method is used to read the <code>Address</code> fields of 
	 *	<code>AlertNotification</code> request because stack does not provide 
	 *	any API to read this filed.
	 *
	 *	@param request -<code>SmppRequest</code> whose <code>Address</code> 
	 *					field is to be parsed.
	 *
	 *	@return <code>Address</code> filed of <code>SmppRequest</code>.
	*/
	private Address readStackRequest(SmppRequest request){
		if(logger.isDebugEnabled()) {
			logger.debug("Inside readStackRequest()");
		}

		String stringForm;
		if(request instanceof AlertNotification){
			stringForm=((AlertNotification)request).getHexDump();
			if(logger.isDebugEnabled()) {
				logger.debug("request raed is "+stringForm);
			}
		}	
		// TODO
		return null;
	}

	/////////////////////////////// SmppResourceAdaptor methods end ////////////////////////////////


	///////////////////////////////// CommandHandler methods begin ///////////////////////////////

	public String getUsage(String command) {
		return new String("Not supported yet");
	}

	public String execute(String command, String[] args, InputStream in, OutputStream out)
		throws CommandFailedException {
		return new String("Not supported yet");
	}

	///////////////////////////////// CommandHandler methods end /////////////////////////////////

	/**
	 *	This method returns the refernce of ResourceContext for this RA.
	 *
	 *	@return ResourceContext for this SMPP RA.
	 */
	public static ResourceContext getResourceContext() {
		return SmppResourceAdaptorImpl.context;
	}

	/**
	 *	This method is used to get the reference of <code>SmppConfMgr</code>
	 *	which is responsible for the management of all the <code>SmscSession</code>
	 *	
	 *	@return reference of <code>SmppConfMgr</code>
	 */
	public SmppConfMgr getConfigMgr(){
		if(this.m_smppConfMgr==null){
			logger.debug("config manager is null");
		}else {
			logger.debug("config manager is not null");
		}
		return this.m_smppConfMgr;
	}

	/**
	 *	This method replicates the <code>SmppSession</code> To peer SAS.
	 *
	 *	@param session <code>SmppSession</code> object to be replicated.
	 *	@param action <code>String</code> replication action to be performed
	 *					on peer SAS.
	 *
	 */
	public void replicate(SmppSession session,String action) {
		if(logger.isDebugEnabled()){
			logger.debug("Goind to perform "+action+" action on SmscSession "+session);
		}
		ReplicationEvent event = new ReplicationEvent(session,action);
		((SmppSession)session).sendReplicationEvent(event);
		if(logger.isDebugEnabled()){
			logger.debug("Leaving replcated");
		}
	}

	/**
	 *	This method generates an unique integer,which is to be used as sequence 
	 *	number for a <code>SmppRequest</code>.
	 *
	 *	@return int -unique integer.
	 */
	public synchronized int getNextSeqNumber(){
		return this.seqNumber++;
	}

	/**
	 *	This method is used to remove a <code>SmppRequest</code> from SMPP
	 *	RA's request map.This is being called from <code>SmppSession</code>
	 *	cleanup() method to remove any <code>SmppRequest</code> associated 
	 *	with any <code>SmppSession</code> before cleaning up the 
	 *	<code>SmppSession</code>.
	 *
	 *	@param request -<code>SmppRequest</code> which is to be removed from map.
	 *	
	 */
	public void removeRequestFromMap(SmppRequest request){
		if(logger.isDebugEnabled()){
			logger.debug("Inside removeRequestFromMap() with request "+request);
		}
		int seqNum=-1;
		try{
			seqNum=request.getSequenceNumber();
		}catch(Exception ex){
			logger.error("Exception in getting sequence number",ex);
		}
		if(seqNum!=-1){
			if(logger.isDebugEnabled()){
				logger.debug("removing request with sequence number "+seqNum);
			}
			this.requestMap.remove(new Integer(seqNum));
		}
		if(logger.isDebugEnabled()){
			logger.debug("Leaving removeRequestFromMap()");
		}
	}

	/**
	 *	This method is used to add a <code>SmppRequest</code> into a map.
	 *	so that any incoming <code>SmppResponse</code> can be mapped to 
	 *	its corrosponding <code>SmppRequest</code>.
	 *
	 *	@param seqNum -sequence number of the <code>SmppRequest</code>, to 
	 *					be used as the key for addind into the map.
	 *	@param request -<code>SmppRequest</code> which is to be added to map.
	 *	
	 */
	public void addRequestToMap(int seqNum, SmppRequest request){
		if(logger.isDebugEnabled()){
			logger.debug("Adding into request map with seq number "+seqNum);
		}
		this.requestMap.put(new Integer(seqNum),request);
	}

	/**
	 *	This method is used to remove a <code>SmppRequest</code> from SMPP
	 *	RA's registered request map.This is being called from 
	 *	<code>SmppSession</code> cleanup() method to remove any 
	 *	<code>SmppRequest</code> associated with any 
	 *	<code>SmppSession</code> before cleaning up the SmppSession.
	 *
	 *	@param request -<code>SmppRequest</code> which is to be removed from map.
	 *	
	 */
	public void removeRgstdRequestFromMap(SmppRequest request){
		if(logger.isDebugEnabled()){
			logger.debug("Inside removeRgstdRequestFromMap() with request "+request);
		}
		String messageId=null;
		try{
			messageId=request.getMessageId();
		}catch(Exception ex){
			logger.error("Exception in getting message id ",ex);
		}
		if(messageId!=null){
			if(logger.isDebugEnabled()){
				logger.debug("removing request with message id "+messageId);
			}
			this.rgstdRequestMap.remove(messageId);
		}
		if(logger.isDebugEnabled()){
			logger.debug("Leaving removeRgstdRequestFromMap()");
		}
	}
	
	/**
	 * Remove registered map for messageid
	 * @param messageId
	 */
	public void removeRgstdRequestFromMap(String messageId){
		if(logger.isDebugEnabled()){
			logger.debug("Inside removeRgstdRequestFromMap() with messageId "+messageId);
		}
		if(messageId!=null){
			if(logger.isDebugEnabled()){
				logger.debug("removing request with message id "+messageId);
			}
			this.rgstdRequestMap.remove(messageId);
		}
		if(logger.isDebugEnabled()){
			logger.debug("Leaving removeRgstdRequestFromMap()");
		}
	}
	
	
	/**
	 * This method is used to get request using message id
	 * @param messageId
	 * @return
	 */
	public SmppRequest getRgstdRequestFromMAp(String messageId){
		if(logger.isDebugEnabled()){
			logger.debug("Entering getRgstdRequestFromMAp for messageId"+messageId);
		}
		SmppRequest smppRequest= (SmppRequest)this.rgstdRequestMap.get(messageId);
		
		if(logger.isDebugEnabled()){
			logger.debug("Leaving getRgstdRequestFromMAp with "+smppRequest);
		}
		return smppRequest;
	}


	/**
	 *	This method is used to add a <code>SmppRequest</code> into a map 
	 *	which has registered delivery value set.So that any incoming 
	 *	delivery Receipet request <code>DataSM</code> or <code>DeliverSM</code>
	 *	can be mapped to its corrosponding <code>SmppRequest</code>.
	 *
	 *	@param messageId -Mesage id return by SMSC in SmppResponce for this 
	 *						request to be used as the key for addind into 
	 *						the map.
	 *	@param request -<code>SmppRequest</code> which is to be added to map.
	 *	
	 */
	public void addRgstdRequestToMap(String messageId, SmppRequest request){
		if(logger.isDebugEnabled()){
			logger.debug("Adding into registered request map with id"+messageId);
		}
		this.rgstdRequestMap.put(messageId,request);
	}

	public void updateRequestCounter(int reqType,boolean isIncoming) {
		if(logger.isDebugEnabled()){
			logger.debug("Inside updateRequestCounter().");
		}
		if(isIncoming){
			this.smppReqInCnt.increment();
		}else{
			this.smppReqOutCnt.increment();
		}
		switch(reqType){
			case Constants.BIND_TRANSMITTER_REQ:
				logger.debug("Incrementing bind transmitter counter");
				this.bindTxReqCnt.increment();
				break;
			case Constants.BIND_RECEIVER_REQ:
				logger.debug("Incrementing bind Receiver counter");
				this.bindRxReqCnt.increment();
				break;
			case Constants.BIND_TRANSCEIVER_REQ:
				logger.debug("Incrementing bind transceiver counter");
				this.bindTRxReqCnt.increment();
				break;
			case Constants.OUTBIND_REQ:
				logger.debug("Incrementing outbind request counter");
				this.outbindReqCnt.increment();
				break;
			case Constants.UNBIND_REQ:
				logger.debug("Incrementing unbind request counter");
				this.unbindReqCnt.increment();
				break;
			case Constants.ENQUIRE_LINK_REQ:
				logger.debug("Incrementing enquire link request counter");
				this.enquireReqCnt.increment();
				break;
			case Constants.ALERT_NOTIFICATION:
				logger.debug("Incrementing alert notification request counter");
				this.alertReqCnt.increment();
				break;
			case Constants.GENERIC_NACK:
				logger.debug("Incrementing generic request counter");
				this.genNackReqCnt.increment();
				break;
			case Constants.SUBMIT_SM_REQ:
				logger.debug("Incrementing submit request counter");
				this.submitReqCnt.increment();
				break;
			case Constants.SUBMIT_SM_MULTI_REQ:
				logger.debug("Incrementing submit multi request counter");
				this.submitMultiReqCnt.increment();
				break;
			case Constants.DATA_SM_REQ:
				logger.debug("Incrementing data request counter");
				this.DataReqCnt.increment();
				break;
			case Constants.DELIVER_SM_REQ:
				logger.debug("Incrementing deliver request counter");
				this.DeliverReqCnt.increment();
				break;
			case Constants.QUERY_SM_REQ:
				logger.debug("Incrementing query request counter");
				this.queryReqCnt.increment();
				break;
			case Constants.CANCEL_SM_REQ:
				logger.debug("Incrementing cancel request counter");
				this.cancelReqCnt.increment();
				break;
			case Constants.REPLACE_SM_REQ:
				logger.debug("Incrementing replace request counter");
				this.replaceReqCnt.increment();
				break;
		}
		if(logger.isDebugEnabled()){
			logger.debug("Leaving updateRequestCounter().");
		}
	}

	public void updateResponseCounter(int resType,boolean isIncoming) {
		if(logger.isDebugEnabled()){
			logger.debug("Inside updateResponseCounter().");
		}
		if(isIncoming){
			this.smppResInCnt.increment();
		}else {
			this.smppResOutCnt.increment();
		}
		switch(resType){
			case Constants.BIND_TRANSMITTER_RES:
				logger.debug("Incrementing bind transmitter response counter.");
				this.bindTxResCnt.increment();
				break;
			case Constants.BIND_RECEIVER_RES:
				logger.debug("Incrementing bind receiver response counter.");
				this.bindRxResCnt.increment();
				break;
			case Constants.BIND_TRANSCEIVER_RES:
				logger.debug("Incrementing bind transceiver response counter.");
				this.bindTRxResCnt.increment();
				break;
			case Constants.UNBIND_RES:
				logger.debug("Incrementing unbind response counter.");
				this.unbindResCnt.increment();
				break;
			case Constants.ENQUIRE_LINK_RES:
				logger.debug("Incrementing response response counter.");
				this.enquireResCnt.increment();
				break;
			case Constants.SUBMIT_SM_RES:
				logger.debug("Incrementing submit response counter.");
				this.submitResCnt.increment();
				break;
			case Constants.SUBMIT_SM_MULTI_RES:
				logger.debug("Incrementing submit multi response counter.");
				this.submitMultiResCnt.increment();
				break;
			case Constants.DATA_SM_RES:
				logger.debug("Incrementing data response counter.");
				this.DataResCnt.increment();
				break;
			case Constants.DELIVER_SM_RES:
				logger.debug("Incrementing deliver response counter.");
				this.DeliverResCnt.increment();
				break;
			case Constants.QUERY_SM_RES:
				logger.debug("Incrementing query response counter.");
				this.queryResCnt.increment();
				break;
			case Constants.CANCEL_SM_RES:
				logger.debug("Incrementing cancel response counter.");
				this.cancelResCnt.increment();
				break;
			case Constants.REPLACE_SM_RES:
				logger.debug("Incrementing replace response counter.");
				this.replaceResCnt.increment();
				break;
		}
		if(logger.isDebugEnabled()){
			logger.debug("Leaving updateResponseCounter().");
		}
	}

	@Override
	public void registerApp(DeployableObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterApp(DeployableObject arg0) {
		// TODO Auto-generated method stub
		
	}
}
