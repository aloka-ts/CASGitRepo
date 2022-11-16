package com.baypackets.ase.ra.diameter.gy.stackif;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.gy.CreditControlRequest;
import com.baypackets.ase.ra.diameter.gy.GyMessage;
import com.baypackets.ase.ra.diameter.gy.GyRequest;
import com.baypackets.ase.ra.diameter.gy.GyResourceAdaptor;
import com.baypackets.ase.ra.diameter.gy.GyResourceEvent;
import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.GyResourceFactory;
import com.baypackets.ase.ra.diameter.gy.GyResponse;
import com.baypackets.ase.ra.diameter.gy.GyStackInterface;
import com.baypackets.ase.ra.diameter.gy.enums.CCRequestTypeEnum;
import com.baypackets.ase.ra.diameter.gy.enums.RequestedActionEnum;
import com.baypackets.ase.ra.diameter.gy.impl.GyResourceAdaptorFactory;
import com.baypackets.ase.ra.diameter.gy.impl.GySession;
import com.baypackets.ase.ra.diameter.gy.utils.Constants;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.util.TimerService;
import com.baypackets.ase.util.AseAlarmService;
import com.traffix.openblox.core.enums.ApplicationType;
import com.traffix.openblox.core.enums.Standard;
import com.traffix.openblox.core.enums.StartMode;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.core.fsm.stack.StackState;
import com.traffix.openblox.core.session.SessionFactory;
import com.traffix.openblox.core.transport.Peer;
import com.traffix.openblox.core.transport.PeerStateListener;
import com.traffix.openblox.core.transport.PeerTable;
import com.traffix.openblox.core.transport.Stack;
import com.traffix.openblox.core.transport.TransportStack;
import com.traffix.openblox.core.utils.configuration.Configuration;
import com.traffix.openblox.core.utils.configuration.ConfigurationType;
import com.traffix.openblox.core.utils.configuration.MutableConfigurationImpl;
import com.traffix.openblox.diameter.base.generated.event.MessageASA;
import com.traffix.openblox.diameter.base.generated.event.MessageCEA;
import com.traffix.openblox.diameter.base.generated.event.MessageCER;
import com.traffix.openblox.diameter.base.generated.event.MessageDPA;
import com.traffix.openblox.diameter.base.generated.event.MessageDPR;
import com.traffix.openblox.diameter.coding.DiameterAnswer;
import com.traffix.openblox.diameter.coding.DiameterRequest;
import com.traffix.openblox.diameter.enums.PcbState;
import com.traffix.openblox.diameter.gy.generated.GyStandardLoader;
import com.traffix.openblox.diameter.gy.generated.enums.EnumRequestedAction;
import com.traffix.openblox.diameter.gy.generated.event.MessageCCA;
import com.traffix.openblox.diameter.gy.generated.event.MessageCCR;
import com.traffix.openblox.diameter.gy.generated.session.SessionGyClient;
import com.traffix.openblox.diameter.session.DiameterSession;
import com.traffix.openblox.diameter.transport.CapabilitiesExchangeListener;
import com.traffix.openblox.diameter.transport.DiameterMetaData;
import com.traffix.openblox.diameter.transport.DisconnectPeerListener;
import com.traffix.openblox.diameter.utils.DiameterApplicationId;

public class GyStackInterfaceImpl implements GyStackInterface,DisconnectPeerListener, CapabilitiesExchangeListener,PeerStateListener,Constants {

	private static Logger logger = Logger.getLogger(GyStackInterfaceImpl.class);
	private static GyResourceFactory raFactory;
	private static TimerService timerService;
	private static long timeout = 30;
	private static int handleCount = 0;

	public Stack stack;
	public Stack serverStack;
	public SessionFactory sessionFactory;
	public static SessionGyClient stackSession=null;
	public Peer serverPeer;
	public static String serverRealm;
	public static String serverHost;
	String configFile;
	private int threadNumber=5;
	public static final DiameterApplicationId applicationId =
		new DiameterApplicationId(Standard.Gy.applicationId, ApplicationType.Auth);
	private GyStackServerInterfaceImpl serverInterface;

	private GyResourceAdaptor ra;
	private Map requests;
	private AseAlarmService alarmService;

	private MeasurementCounter ccrEventCnt;         //Accounting Request Counter Event Based Charging
	private MeasurementCounter ccrSessionCnt;       //Accounting Request Counter Session Based Charging

	private MeasurementCounter ccrDirectDebitCnt;
	private MeasurementCounter ccrAccountRefundCnt;
	private MeasurementCounter ccrBalanceCheckCnt;
	private MeasurementCounter ccrPriceEnquiryCnt;
	
	private MeasurementCounter ccrFirstInteroCnt;
	private MeasurementCounter ccrInterimInteroCnt;
	private MeasurementCounter ccrFinalInteroCnt;

	private MeasurementCounter ccaEvent1xxxCnt;     //Accounting Answer Counter Event Based Charging
	private MeasurementCounter ccaEvent2xxxCnt;
	private MeasurementCounter ccaEvent3xxxCnt;
	private MeasurementCounter ccaEvent4xxxCnt;
	private MeasurementCounter ccaEvent5xxxCnt;

	private MeasurementCounter ccaSession1xxxCnt;   //Accounting Answer Counter Session Based Charging
	private MeasurementCounter ccaSession2xxxCnt;
	private MeasurementCounter ccaSession3xxxCnt;
	private MeasurementCounter ccaSession4xxxCnt;
	private MeasurementCounter ccaSession5xxxCnt;

	private MeasurementCounter ccrSendErrorCnt;

	public GyStackInterfaceImpl(GyResourceAdaptor ra) {
		this.ra = ra;
		this.requests = new Hashtable(64*1024); // initialize with 64 K entries 
		serverInterface = new GyStackServerInterfaceImpl(ra);
	}

	public void init(ResourceContext context) throws GyResourceException {
		this.alarmService = (AseAlarmService) context.getAlarmService();
		GyStackInterfaceImpl.raFactory = (GyResourceFactory)context.getResourceFactory();
		GyStackInterfaceImpl.timerService = (TimerService)context.getTimerService();
		logger.debug("Initialize measurement counters.");

		MeasurementManager measurementMgr = context.getMeasurementManager();

		this.ccrEventCnt = measurementMgr.getMeasurementCounter(CCR_EVENT_COUNTER_OUT);
		this.ccrSessionCnt = measurementMgr.getMeasurementCounter(CCR_SESSION_COUNTER_OUT);

		this.ccrDirectDebitCnt = measurementMgr.getMeasurementCounter(CCR_DIRECT_DEBIT_COUNTER_OUT);
		this.ccrAccountRefundCnt = measurementMgr.getMeasurementCounter(CCR_ACCOUNT_REFUND_COUNTER_OUT);
		this.ccrBalanceCheckCnt = measurementMgr.getMeasurementCounter(CCR_BALANCE_CHECK_COUNTER_OUT);
		this.ccrPriceEnquiryCnt = measurementMgr.getMeasurementCounter(CCR_PRICE_ENQUIRY_COUNTER_OUT);
		this.ccrFirstInteroCnt = measurementMgr.getMeasurementCounter(CCR_FIRST_INTEROGATION_COUNTER_OUT);
		this.ccrInterimInteroCnt = measurementMgr.getMeasurementCounter(CCR_INTERIM_INTEROGATION_COUNTER_OUT);
		this.ccrFinalInteroCnt = measurementMgr.getMeasurementCounter(CCR_FINAL_INTEROGATION_COUNTER_OUT);

		this.ccaEvent1xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_1XXX_COUNTER_IN);
		this.ccaEvent2xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_2XXX_COUNTER_IN);
		this.ccaEvent3xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_3XXX_COUNTER_IN);
		this.ccaEvent4xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_4XXX_COUNTER_IN);
		this.ccaEvent5xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_5XXX_COUNTER_IN);

		this.ccaSession1xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_1XXX_COUNTER_IN);
		this.ccaSession2xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_2XXX_COUNTER_IN);
		this.ccaSession3xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_3XXX_COUNTER_IN);
		this.ccaSession4xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_4XXX_COUNTER_IN);
		this.ccaSession5xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_5XXX_COUNTER_IN);

		this.ccrSendErrorCnt = measurementMgr.getMeasurementCounter(CCR_SEND_ERROR);

		this.configFile = (String)context.getConfigProperty("ase.home") + File.separator + 
		"conf" + File.separator + "gyClient.cfg";
		logger.debug("Use [" + this.configFile + "]");
		init(configFile);
		serverInterface.init(context);
	}

	public void init(String cfgFile) throws GyResourceException {
		try {
			GyStackInterfaceImpl.raFactory = GyResourceAdaptorFactory.getResourceFactory();
			this.configFile = cfgFile;
			Configuration configuration = (Configuration) MutableConfigurationImpl.createConfiguration(
					cfgFile, ConfigurationType.Xml);
			logger.debug("creating transport stack");
			stack = new TransportStack();
			logger.debug("initializing transport stack");
			sessionFactory = stack.init(configuration);
			GyStandardLoader.load(stack, new SessionListenerFactoryGyClientImpl(this));
			logger.debug("stack loaded successfully");
		} catch (Exception ex) {
			logger.error("GyResourceFactory.init() failed.");
			logger.error(ex.getMessage(), ex);
			throw new GyResourceException("GyResourceFactory.init() failed.");
		}

	}

	public void start() throws GyResourceException {
		logger.debug("Inside GyStackInterfaceImpl start()");
		try{
			// Set the serverPeer to be the first peer from the peer table
			// and wait until it's in state OKAY
			PeerTable peerTable = stack.getPeerTable();
			serverPeer = peerTable.getPeers().iterator().next();
			serverPeer.setDisconnectListener(this);
			serverPeer.setHandshakeListener(this);
			serverPeer.addPeerStateListener(this);
			StackState state = stack.start(StartMode.ANY_PEER, 5, TimeUnit.SECONDS);
			if (state != StackState.Working){
				logger.debug("State is " + state + ", Failed to start " + stack);
			} 
			else {
				logger.debug("stack started successfully");

				// Set the serverHost serverRealm parameters in order to be used as values
				// for destination Host and destination Realm in created requests
				DiameterMetaData metaData = (DiameterMetaData) serverPeer.getMetaData();
				serverHost = metaData.getURI().getHost();
				serverRealm = metaData.getRealm();

				stackSession = (SessionGyClient) sessionFactory.getNewSession(Standard.Gy);
				stackSession.setPerformFailover(true);
			}
			serverInterface.start();
		} catch (Exception ex) {
			logger.error("GyResourceFactory.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new GyResourceException("GyResourceFactory.stop() failed.");
		}
	}

	public void stop() throws GyResourceException {
		logger.info("stop(): closing Diameter stack...");
		try{
			logger.debug("start(): initialize Condor stack.");
			StackState state = stack.stop(5, TimeUnit.SECONDS);
			if (state != StackState.Stopped){
				logger.debug("State is " + state + ", Stack failed to stop " + stack);
				throw new GyResourceException("GyResourceFactory.stop() failed.");
			}

			serverInterface.stop();
		} catch (Exception ex) {
			logger.error("GyResourceFactory.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new GyResourceException("GyResourceFactory.init() failed.");
		}

	}
public void incrementRequestedActionCounter(CreditControlRequest request) throws GyResourceException
{
	RequestedActionEnum type=request.getEnumRequestedAction();
		if(type.equals(RequestedActionEnum.DIRECT_DEBITING)){
			this.ccrDirectDebitCnt.increment();
			logger.debug("Requested Action Direct Debiting incrementing Counter");
		}else if(type.equals(RequestedActionEnum.REFUND_ACCOUNT)){
			this.ccrAccountRefundCnt.increment();
			logger.debug("Requested Action Account Refund incrementing Counter");
		}else if(type.equals(RequestedActionEnum.CHECK_BALANCE)){
			this.ccrBalanceCheckCnt.increment();
			logger.debug("Requested Action Balance Check incrementing Counter");
		}else if(type.equals(RequestedActionEnum.PRICE_ENQUIRY)){
			this.ccrPriceEnquiryCnt.increment();
			logger.debug("Requested Action Price Enquiry incrementing Counter");
		}else {
			logger.error("Wrong/Unkown requested Action type.");
			
		}
	}
	
	public void handleRequest(GyRequest request) throws GyResourceException {		
		logger.debug("handleRequest(GyRequest) called ");
		if (request == null) {
			logger.error("handleRequest(): null request. returning");
			return;
		}
		int handle = -1;
		GySession session = (GySession)request.getSession();
		try {
			handle = GyStackInterfaceImpl.getNextHandle();
			session.setHandle(handle);
			((CreditControlRequestImpl)request).incrementRetryCounter();
			logger.debug("request map size before adding is :[ "+ this.requests.size()+"]"); 
			this.requests.put(((CreditControlRequestImpl)request).getStackObj(),request);
			GyStackInterfaceImpl.stackSession.sendCCR(((CreditControlRequestImpl)request).getStackObj(), 5, TimeUnit.SECONDS);
			CCRequestTypeEnum type = ((CreditControlRequest)request).getEnumCCRequestType();
			if(type.equals(CCRequestTypeEnum.EVENT_REQUEST)){
				logger.debug("sent EVENT_REQUEST request");
				this.incrementRequestedActionCounter((CreditControlRequest) request);
				this.ccrEventCnt.increment();
			}else if(type.equals(CCRequestTypeEnum.INITIAL_REQUEST)){
				logger.debug("sent INITIAL_REQUEST request");
				this.ccrFirstInteroCnt.increment();
				this.ccrSessionCnt.increment();
			}else if(type.equals(CCRequestTypeEnum.UPDATE_REQUEST)){
				logger.debug("sent UPDATE_REQUEST request");
				this.ccrInterimInteroCnt.increment();
				this.ccrSessionCnt.increment();
			}else if(type.equals(CCRequestTypeEnum.TERMINATION_REQUEST)){
				logger.debug("sent TERMINATION_REQUEST request");
				this.ccrFinalInteroCnt.increment();
				this.ccrSessionCnt.increment();
			}else {
				logger.error("Wrong/Unkown request type.");
				throw new ResourceException("Wrong/Unkown request type.");
			}
		} catch (ValidationException ex) {
			logger.error("ValidationException in sending Ro Request.",ex);
			this.requests.remove(((CreditControlRequestImpl)request).getStackObj());
			GyResourceEvent resourceEvent = new GyResourceEvent(request, 
					GyResourceEvent.REQUEST_FAIL_EVENT, request.getApplicationSession());
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering GyResourceEvent"+ex);
				throw new GyResourceException(ex);
			}
		}catch(Exception ex){
			logger.error("handleRequest() failed: " + ex);
			logger.error(ex.getMessage(), ex);
			throw new GyResourceException(ex);
		}
	}

	public void handleResponse(GyResponse response) throws GyResourceException {
		logger.debug("Entering handleResponse()");
		
		boolean sentSuccessfully = true;

		if (response == null) {
			logger.error("handleResponse(): null response. returning");
			return;
		}
		try {
			
				logger.debug("Passing on to server interface");
				serverInterface.handleResponse(response);					

		} catch (Exception ex) {
			
			logger.error("handleResponse() failed: " ,ex);
			sentSuccessfully=false;
		}

		if(!sentSuccessfully) {
			
			GyResourceEvent resourceEvent = new GyResourceEvent(response, 
					GyResourceEvent.RESPONSE_FAIL_EVENT, response.getApplicationSession());
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering GyResourceEvent :: ",e);
				throw new GyResourceException(e);
			}

		}
	}

	//TODO
	public void handleIncomingASR(SessionGyClient session, MessageASA request) {
		logger.debug("Inside handleIncomingASR with " + request);
	}

	public void handleIncomingCCA(SessionGyClient session, MessageCCR request, MessageCCA answer) {
		logger.debug("Inside handleIncomingCCA with " + answer);
		try {
			logger.debug("removing request from map");
			CreditControlRequestImpl containerReq = (CreditControlRequestImpl) this.requests.remove(request);
			if (containerReq==null) {
				logger.error("corrosponding requst is not found. returnning");
				return;
			}
			CCRequestTypeEnum type = ((CreditControlRequest)containerReq).getEnumCCRequestType();
			if(type.equals(CCRequestTypeEnum.EVENT_REQUEST)){
				logger.debug("received EVENT_RECORD request");
				this.updateResponseCounter(EVENT, (int) answer.getResultCode());
			}else if(type.equals(CCRequestTypeEnum.INITIAL_REQUEST)){
				logger.debug("received START_RECORD request");
				this.updateResponseCounter(SESSION, (int) answer.getResultCode());
			}else if(type.equals(CCRequestTypeEnum.UPDATE_REQUEST)){
				logger.debug("received INTERIM_RECORD request");
				this.updateResponseCounter(SESSION, (int) answer.getResultCode());
			}else if(type.equals(CCRequestTypeEnum.TERMINATION_REQUEST)){
				logger.debug("received STOP_RECORD request");
				this.updateResponseCounter(SESSION, (int) answer.getResultCode());
			}else {
				logger.error("Wrong/Unkown type request received.");
				throw new ResourceException("Wrong/Unkown request type.");
			}
			CreditControlAnswerImpl response = new CreditControlAnswerImpl(answer);
			response.setProtocolSession(containerReq.getProtocolSession());
			logger.debug("devilering CCA");
			this.ra.deliverResponse(response);
			logger.debug("delivered CCA successfully");
		} catch (Exception ex) {
			logger.error("handleIncomingCCA() failed: " + ex);
			logger.error(ex.getMessage(), ex);
		}
	}

	// TODO we may also send ResourceEvent for this.
	public void receivedErrorMessage(DiameterSession session,
			DiameterRequest pendingRequest, DiameterAnswer answer) {
		logger.debug("Inside receivedErrorMessage with " + answer);
		try {
			logger.debug("removing request from map");
			CreditControlRequestImpl containerReq = (CreditControlRequestImpl) this.requests.remove(pendingRequest);
			if (containerReq==null) {
				logger.error("corrosponding requst is not found. returnning");
				return;
			}
			CCRequestTypeEnum type = ((CreditControlRequest)containerReq).getEnumCCRequestType();
			if(type.equals(CCRequestTypeEnum.EVENT_REQUEST)){
				logger.debug("received EVENT_RECORD request");
				this.updateResponseCounter(EVENT, (int) ((MessageCCA)answer).getResultCode());
			}else if(type.equals(CCRequestTypeEnum.INITIAL_REQUEST)){
				logger.debug("received START_RECORD request");
				this.updateResponseCounter(SESSION, (int) ((MessageCCA)answer).getResultCode());
			}else if(type.equals(CCRequestTypeEnum.UPDATE_REQUEST)){
				logger.debug("received INTERIM_RECORD request");
				this.updateResponseCounter(SESSION, (int) ((MessageCCA)answer).getResultCode());
			}else if(type.equals(CCRequestTypeEnum.TERMINATION_REQUEST)){
				logger.debug("received STOP_RECORD request");
				this.updateResponseCounter(SESSION, (int) ((MessageCCA)answer).getResultCode());
			}else {
				logger.error("Wrong/Unkown type request received.");
				throw new ResourceException("Wrong/Unkown request type.");
			}
			
			GyResponse response= new CreditControlAnswerImpl((MessageCCA)answer);	
			((GyMessage) response).setProtocolSession(containerReq.getProtocolSession());
			logger.debug("devilering receivedErrorMessage event");
			//this.ra.deliverResponse(response);
			GyResourceEvent resourceEvent = new GyResourceEvent(this, 
					GyResourceEvent.ERROR_MSG_RECEIVED, response.getApplicationSession());
			resourceEvent.setMessage((GyMessage) response);
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering GyResourceEvent"+e);
			}
			logger.debug("devilered receivedErrorMessage event");
		} catch (Exception ex) {
			logger.error("receivedErrorMessage() failed: " + ex);
			logger.error(ex.getMessage(), ex);
		}
	}


	public void timeoutExpired(DiameterSession session,
			DiameterRequest pendingRequest) {
		logger.debug("Inside TimeoutExpired for request " + pendingRequest);
		CreditControlRequestImpl containerReq = (CreditControlRequestImpl) this.requests.get(pendingRequest);
		if(containerReq==null){
			logger.debug("Request already handled");
			return;
		}
		if(containerReq.getRetryCounter()< MAX_RETYR_COUNTER){
			try{
				containerReq.incrementRetryCounter();
				containerReq.setReTransmitted(true);
				((SessionGyClient)session).sendCCR((MessageCCR)pendingRequest, 5, TimeUnit.SECONDS);
			}catch(Exception e){
				logger.debug("Exception in sending retransmit req ",e);
			}
		}else{
			logger.debug("re-transmissions exceeded. Devivering request fail event");
			this.requests.remove(((CreditControlRequestImpl)containerReq).getStackObj());
			GyResourceEvent resourceEvent = new GyResourceEvent(containerReq, 
					GyResourceEvent.REQUEST_FAIL_EVENT, containerReq.getApplicationSession());
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering GyResourceEvent",e);
			}
		}
	}

	public void requestTimeoutExpired(DiameterSession session,
			DiameterRequest pendingRequest) {
		logger.debug("Inside requestTimeoutExpired for request " + pendingRequest);
		CreditControlRequestImpl containerReq = (CreditControlRequestImpl) this.requests.get(pendingRequest);
		if(containerReq.getRetryCounter()< MAX_RETYR_COUNTER){
			try{
				containerReq.incrementRetryCounter();
				containerReq.setReTransmitted(true);
				((SessionGyClient)session).sendCCR((MessageCCR)pendingRequest, 5, TimeUnit.SECONDS);
			}catch(Exception e){
				logger.debug("Exception in sending retransmit req "+e);
			}
		}else{
			this.requests.remove(((CreditControlRequestImpl)containerReq).getStackObj());
			GyResourceEvent resourceEvent = new GyResourceEvent(containerReq, 
					GyResourceEvent.REQUEST_FAIL_EVENT, containerReq.getApplicationSession());
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering GyResourceEvent"+e);
			}
		}
	}

	// Method to update response counter
	private void updateResponseCounter(int type, int resultCode) 
	{
		switch (type) 
		{
		case EVENT:
			if (resultCode >= 1000 && resultCode < 2000) 
			{
				this.ccaEvent1xxxCnt.increment();
			} 
			else if (resultCode >= 2000 && resultCode < 3000) 
			{
				this.ccaEvent2xxxCnt.increment();
			}
			else if (resultCode >= 3000 && resultCode < 4000) 
			{
				this.ccaEvent3xxxCnt.increment();
			}
			else if (resultCode >= 4000 && resultCode < 5000) 
			{
				this.ccaEvent4xxxCnt.increment();
			}
			else if (resultCode >= 5000 && resultCode < 6000) 
			{
				this.ccaEvent5xxxCnt.increment();
			}
			break;
		case SESSION:
			if (resultCode >= 1000 && resultCode < 2000) 
			{
				this.ccaSession1xxxCnt.increment();
			} 
			else if (resultCode >= 2000 && resultCode < 3000) 
			{
				this.ccaSession2xxxCnt.increment();
			}
			else if (resultCode >= 3000 && resultCode < 4000) 
			{
				this.ccaSession3xxxCnt.increment();
			} 
			else if (resultCode >= 4000 && resultCode < 5000) 
			{
				this.ccaSession4xxxCnt.increment();
			} 
			else if (resultCode >= 5000 && resultCode < 6000) 
			{
				this.ccaSession5xxxCnt.increment();
			}
			break;
		}
	}

	///////////////////////////////////////////////////////////


	public static synchronized int getNextHandle() {
		return GyStackInterfaceImpl.handleCount++;
	}

	public void addRequestToMap(int handle, GyRequest request) {
		logger.debug("request map size before adding is :[ "+ this.requests.size()+"]"); 
		this.requests.put(new Integer(handle) , request);
		logger.debug("request map size after adding is :[ "+ this.requests.size()+"]"); 
	}

	//////////////////////////////////////////////////////////
	////////// PeerDisconnectListener Interface methods. /////
	//////////////////////////////////////////////////////////

	public void dpaReceived(MessageDPA dpa, Peer peer) {
		logger.debug("Inside Inside dpaReceived");
		try {
			GyResourceEvent event = new GyResourceEvent(this, 
					GyResourceEvent.GY_DISCONNECT_PEER_RESPONSE, 
					null);
			event.setData(peer.getHost());
			ra.deliverEvent(event);
			logger.debug("delivered dpaReceived event");
		} 
		catch(Exception ex) {
			logger.error(ex.toString(),ex);
		}
	}

	//@Override
	public void dprReceived(MessageDPR dpr, Peer peer) {
		logger.debug("Inside Inside dprReceived");
		try {
			GyResourceEvent event = new GyResourceEvent(this, 
					GyResourceEvent.GY_NOTIFY_DISCONNECT_PEER_REQUEST, 
					null);
			event.setData(peer.getHost());
			ra.deliverEvent(event);
			logger.debug("delivered dprReceived event");
		} 
		catch(Exception ex) {
			logger.error(ex.toString(),ex);
		}
	}

	//@Override
	public void previewDPA(MessageDPA arg0, Peer arg1) {
		logger.debug("Inside previewDPA");
	}

	//@Override
	public void previewDPR(MessageDPR arg0, Peer arg1) {
		logger.debug("Inside previewDPR");
	}

	//////////////////////////////////////////////////////////////
	////////// CapabilityExchangeListener Interface methods. /////
	//////////////////////////////////////////////////////////////

	@Override
	public void ceaReceived(MessageCEA message, Peer peer) {
		logger.debug("Inside ceaReceived");
		try {
			GyResourceEvent event = new GyResourceEvent(this, 
					GyResourceEvent.GY_NOTIFY_PEER_UP,
					null);
			event.setData(peer.getHost());
			ra.deliverEvent(event);
		} 
		catch(Exception ex) {
			logger.error(ex.toString(),ex);
		}
		logger.debug("delivered ceaReceived event");
	}

	@Override
	public void cerReceived(MessageCER arg0, Peer arg1) {
		logger.debug("Inside cerReceived");
	}

	@Override
	public void previewCEA(MessageCEA arg0, Peer arg1) {
		logger.debug("Inside previewCEA");
	}

	@Override
	public void previewCER(MessageCER arg0, Peer arg1) {
		logger.debug("Inside previewCER");
	}

	/////////////////////////////////////////////////////////
	// PeerStateListener interface implementation starts /////
	/////////////////////////////////////////////////////////

	public void stateChanged(Enum oldState, Enum newState) {

		if (newState == PcbState.DOWN) {
			logger.error(" ************** " + serverPeer + " is DOWN *************** ");
			try {
				GyResourceEvent event = new GyResourceEvent(this, 
						GyResourceEvent.GY_NOTIFY_PEER_DOWN, 
						null);
				event.setData(serverPeer.getHost());
				ra.deliverEvent(event);
				logger.debug("delivered peer down event");
			} 
			catch(Exception ex) {
				logger.error(ex.toString(),ex);
			}
		}
		else if (newState == PcbState.OKAY) {
			logger.error(" ************** " + serverPeer + " is OKAY ************** ");
			try {
				GyResourceEvent event = new GyResourceEvent(this, 
						GyResourceEvent.GY_NOTIFY_PEER_UP, 
						null);
				event.setData(serverPeer.getHost());
				ra.deliverEvent(event);
				logger.debug("delivered peer up event");
			} 
			catch(Exception ex) {
				logger.error(ex.toString(),ex);
			}
		}
	}
	
	// added in 3.2.8 version
	public void stateChanged(Enum oldState, Enum newState,Enum event1) {

		if (newState == PcbState.DOWN) {
			logger.error(" ************** " + serverPeer + " is DOWN *************** ");
			try {
				GyResourceEvent event = new GyResourceEvent(this, 
						GyResourceEvent.GY_NOTIFY_PEER_DOWN, 
						null);
				event.setData(serverPeer.getHost());
				ra.deliverEvent(event);
				logger.debug("delivered peer down event");
			} 
			catch(Exception ex) {
				logger.error(ex.toString(),ex);
			}
		}
		else if (newState == PcbState.OKAY) {
			logger.error(" ************** " + serverPeer + " is OKAY ************** ");
			try {
				GyResourceEvent event = new GyResourceEvent(this, 
						GyResourceEvent.GY_NOTIFY_PEER_UP, 
						null);
				event.setData(serverPeer.getHost());
				ra.deliverEvent(event);
				logger.debug("delivered peer up event");
			} 
			catch(Exception ex) {
				logger.error(ex.toString(),ex);
			}
		}
	}

	public void originStateIdChanged(long oldValue, long newValue) {
		logger.error("Origin-State-Id of " + serverPeer+
				" changed from " + oldValue + " to " + newValue);
	}
}
