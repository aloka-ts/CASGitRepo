package com.baypackets.ase.ra.diameter.rf.stackif;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.ra.diameter.rf.RfAccountingRequest;
import com.baypackets.ase.ra.diameter.rf.RfMessage;
import com.baypackets.ase.ra.diameter.rf.RfRequest;
import com.baypackets.ase.ra.diameter.rf.RfResourceAdaptor;
import com.baypackets.ase.ra.diameter.rf.RfResourceEvent;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.baypackets.ase.ra.diameter.rf.RfResourceFactory;
import com.baypackets.ase.ra.diameter.rf.RfResponse;
import com.baypackets.ase.ra.diameter.rf.RfStackInterface;
import com.baypackets.ase.ra.diameter.rf.enums.AccountingRecordTypeEnum;
import com.baypackets.ase.ra.diameter.rf.impl.RfResourceAdaptorFactory;
import com.baypackets.ase.ra.diameter.rf.impl.RfSession;
import com.baypackets.ase.ra.diameter.rf.utils.Constants;
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
import com.traffix.openblox.diameter.base.generated.event.MessageCEA;
import com.traffix.openblox.diameter.base.generated.event.MessageCER;
import com.traffix.openblox.diameter.base.generated.event.MessageDPA;
import com.traffix.openblox.diameter.base.generated.event.MessageDPR;
import com.traffix.openblox.diameter.coding.DiameterAnswer;
import com.traffix.openblox.diameter.coding.DiameterRequest;
import com.traffix.openblox.diameter.enums.PcbState;
import com.traffix.openblox.diameter.rf.generated.RfStandardLoader;
import com.traffix.openblox.diameter.rf.generated.event.MessageACA;
import com.traffix.openblox.diameter.rf.generated.event.MessageACR;
import com.traffix.openblox.diameter.rf.generated.event.MessageASR;
import com.traffix.openblox.diameter.rf.generated.session.SessionRfClient;
import com.traffix.openblox.diameter.session.DiameterSession;
import com.traffix.openblox.diameter.transport.CapabilitiesExchangeListener;
import com.traffix.openblox.diameter.transport.DiameterMetaData;
import com.traffix.openblox.diameter.transport.DisconnectPeerListener;
import com.traffix.openblox.diameter.utils.DiameterApplicationId;

public class RfStackInterfaceImpl implements RfStackInterface,DisconnectPeerListener, CapabilitiesExchangeListener,PeerStateListener,Constants {

	private static Logger logger = Logger.getLogger(RfStackInterfaceImpl.class);
	private static RfResourceFactory rfFactory;
	private static TimerService timerService;
	private static long timeout = 30;
	private static int handleCount = 0;

	public Stack stack;
	public Stack serverStack;
	public SessionFactory sessionFactory;
	public static SessionRfClient stackSession=null;
	public Peer serverPeer;
	public static String serverRealm;
	public static String serverHost;
	String clientConfigFile;
	private int threadNumber=5;
	public static final DiameterApplicationId applicationId =
		new DiameterApplicationId(Standard.Rf.applicationId, ApplicationType.Auth);
	private RfStackServerInterfaceImpl serverInterface;

	private RfResourceAdaptor ra;
	private Map requests;
	private AseAlarmService alarmService;

	private MeasurementCounter acrEventCnt;		//Accounting Request Counter Event Based Charging
	private MeasurementCounter acrSessionCnt;	//Accounting Request Counter Session Based Charging
	private MeasurementCounter acaEvent1xxxCnt;	//Accounting Answer Counter Event Based Charging
	private MeasurementCounter acaEvent2xxxCnt;
	private MeasurementCounter acaEvent3xxxCnt;
	private MeasurementCounter acaEvent4xxxCnt;
	private MeasurementCounter acaEvent5xxxCnt;		
	private MeasurementCounter acaSession1xxxCnt;	//Accounting Answer Counter Session Based Charging
	private MeasurementCounter acaSession2xxxCnt;
	private MeasurementCounter acaSession3xxxCnt;
	private MeasurementCounter acaSession4xxxCnt;
	private MeasurementCounter acaSession5xxxCnt;

	public RfStackInterfaceImpl(RfResourceAdaptor ra) {
		this.ra = ra;
		this.requests = new Hashtable(64*1024); // initialize with 64 K entries
		serverInterface = new RfStackServerInterfaceImpl(ra);
	}

	public void init(ResourceContext context) throws RfResourceException {
		this.alarmService = (AseAlarmService) context.getAlarmService();
		RfStackInterfaceImpl.rfFactory = (RfResourceFactory)context.getResourceFactory();
		RfStackInterfaceImpl.timerService = (TimerService)context.getTimerService();
		logger.debug("Initialize measurement counters.");
		MeasurementManager measurementMgr = context.getMeasurementManager();
		this.acrEventCnt = measurementMgr.getMeasurementCounter(Constants.ACR_EVENT_COUNTER_OUT);
		this.acrSessionCnt = measurementMgr.getMeasurementCounter(Constants.ACR_SESSION_COUNTER_OUT);
		this.acaEvent1xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_1xxx_COUNTER_IN);
		this.acaEvent2xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_2xxx_COUNTER_IN);
		this.acaEvent3xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_3xxx_COUNTER_IN);
		this.acaEvent4xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_4xxx_COUNTER_IN);
		this.acaEvent5xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_5xxx_COUNTER_IN);
		this.acaSession1xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_1xxx_COUNTER_IN);
		this.acaSession2xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_2xxx_COUNTER_IN);
		this.acaSession3xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_3xxx_COUNTER_IN);
		this.acaSession4xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_4xxx_COUNTER_IN);
		this.acaSession5xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_5xxx_COUNTER_IN);

		this.clientConfigFile = (String)context.getConfigProperty("ase.home") + File.separator + 
		"conf" + File.separator + "rfClient.cfg";
		logger.debug("Use [" + this.clientConfigFile + "]");
		init(clientConfigFile);
		serverInterface.init(context);
	}

	public void init(String cfgFile) throws RfResourceException {
		try {
			rfFactory = RfResourceAdaptorFactory.getResourceFactory();
			this.clientConfigFile = cfgFile;
			Configuration configuration = (Configuration) MutableConfigurationImpl.createConfiguration(
					cfgFile, ConfigurationType.Xml);

			logger.debug("creating transport stack");
			stack = new TransportStack();

			sessionFactory = stack.init(configuration);
			RfStandardLoader.load(stack, new SessionListenerFactoryRfClientImpl(this));

			logger.debug("stack loaded successfully");

		} catch (Exception ex) {

			logger.error("RfResourceFactory.init() failed.");
			logger.error(ex.getMessage(), ex);
			throw new RfResourceException("RfResourceFactory.init() failed.");
		}

	}

	public void start() throws RfResourceException {
		logger.debug("Inside RfStackInterfaceImpl start()");

		try {

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

				stackSession = (SessionRfClient) sessionFactory.getNewSession(Standard.Rf);
				stackSession.setPerformFailover(true);
			}
			serverInterface.start();

		} catch (Exception ex) {

			logger.error("RfResourceFactory.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new RfResourceException("RfResourceFactory.stop() failed.");
		}
	}

	public void stop() throws RfResourceException {
		logger.info("stop(): closing Diameter stack...");

		try {

			logger.debug("start(): initialize Condor stack.");
			StackState state = stack.stop(5, TimeUnit.SECONDS);

			if (state != StackState.Stopped){
				logger.debug("State is " + state + ", Stack failed to stop " + stack);
				throw new RfResourceException("RfResourceFactory.stop() failed.");
			}

			serverInterface.stop();

		} catch (Exception ex) {

			logger.error("RfResourceFactory.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new RfResourceException("RfResourceFactory.init() failed.");
		}

	}

	public void handleRequest(RfRequest request) throws RfResourceException {		
		logger.debug("handleRequest(RfRequest) called ");
		if (request == null) {
			logger.error("handleRequest(): null request. returning");
			return;
		}
		int handle = -1;
		RfSession session = (RfSession)request.getSession();
		try {
			handle = RfStackInterfaceImpl.getNextHandle();
			session.setHandle(handle);
			((RfAccountingRequestImpl)request).incrementRetryCounter();
			logger.debug("request map size before adding is :[ "+ this.requests.size()+"]"); 
			this.requests.put(((RfAccountingRequestImpl)request).getStackObj(),request);
			
			//Set worker queue for message
			((SipApplicationSessionImpl)session.getApplicationSession()).getIc().setWorkQueue(((RfAbstractRequest)request).getWorkQueue());
			RfStackInterfaceImpl.stackSession.sendACR(((RfAccountingRequestImpl)request).getStackObj(), 5, TimeUnit.SECONDS);
			AccountingRecordTypeEnum type = ((RfAccountingRequest)request).getEnumAccountingRecordType();
			if(type.equals(AccountingRecordTypeEnum.EVENT_RECORD)){
				logger.debug("sent EVENT_RECORD request");
				this.acrEventCnt.increment();
			}else if(type.equals(AccountingRecordTypeEnum.START_RECORD)){
				logger.debug("sent START_RECORD request");
				this.acrSessionCnt.increment();
			}else if(type.equals(AccountingRecordTypeEnum.INTERIM_RECORD)){
				logger.debug("sent INTERIM_RECORD request");
				this.acrSessionCnt.increment();
			}else if(type.equals(AccountingRecordTypeEnum.STOP_RECORD)){
				logger.debug("sent STOP_RECORD request");
				this.acrSessionCnt.increment();
			}else {
				logger.error("Wrong/Unkown request type.");
				throw new ResourceException("Wrong/Unkown request type.");
			}
		} catch (ValidationException ex) {
			logger.error("ValidationException in sending Rf Request.",ex);
			this.requests.remove(((RfAccountingRequestImpl)request).getStackObj());
			RfResourceEvent resourceEvent = new RfResourceEvent(request, 
					RfResourceEvent.REQUEST_FAIL_EVENT, request.getApplicationSession());
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering RfResourceEvent"+ex);
				throw new RfResourceException(ex);
			}
		}catch(Exception ex){
			logger.error("handleRequest() failed: " + ex);
			logger.error(ex.getMessage(), ex);
			throw new RfResourceException(ex);
		}
	}

	public void handleResponse(RfResponse response) throws RfResourceException {
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
			
			RfResourceEvent resourceEvent = new RfResourceEvent(response, 
					RfResourceEvent.RESPONSE_FAIL_EVENT, response.getApplicationSession());
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering RfResourceEvent :: ",e);
				throw new RfResourceException(e);
			}

		}
	}

	//TODO
	public void handleIncomingASR(SessionRfClient session, MessageASR request) {
		logger.debug("Inside handleIncomingASR with " + request);
		//		try {
		//			this.pnrCnt.increment();
		// No need to save notification requests as incase of timeout, remote
		// party will resend it.
		//			ShPushNotificationRequestImpl notificationReq = new ShPushNotificationRequestImpl(request);
		//			response.setProtocolSession(containerReq.getProtocolSession());
		//			logger.debug("devilering ShProfileUpdateResponse");
		//			this.ra.deliverResponse(response);
		//			logger.debug("delivered ShProfileUpdateResponse successfully");

		// only for testing
		//			MessagePushNotificationAnswer answer = request.createAnswer(ResultCode.SUCCESS);
		//			/** adding mandatory avps of answer **/
		//			//Adding Auth-Session-State
		//			answer.addAuthSessionState(request.getEnumAuthSessionState());
		//			//Adding Vendor-Specific-Application-Id grouped avp
		//			AvpVendorSpecificApplicationId gAvpVSI =answer.addGroupedVendorSpecificApplicationId();
		//			gAvpVSI.addAuthApplicationId(request.getGroupedVendorSpecificApplicationId().getAuthApplicationId());
		//			gAvpVSI.addVendorId(request.getGroupedVendorSpecificApplicationId().getVendorId());
		//			logger.debug("Sending " + answer);
		//			session.sendPushNotificationAnswer(answer);
		//		}
		//		catch (ValidationException ex) {
		//			logger.error("Failed to handle " + request, ex);
		//		}
	}

	public void handleIncomingACA(SessionRfClient session,MessageACR request, MessageACA answer) {
		logger.debug("Inside handleIncomingACA with " + answer);
		try {
			logger.debug("removing request from map");
			RfAccountingRequestImpl containerReq = (RfAccountingRequestImpl) this.requests.remove(request);
			if (containerReq==null) {
				logger.error("corrosponding requst is not found. returnning");
				return;
			}
			AccountingRecordTypeEnum type = ((RfAccountingRequest)containerReq).getEnumAccountingRecordType();
			if(type.equals(AccountingRecordTypeEnum.EVENT_RECORD)){
				logger.debug("received EVENT_RECORD request");
				this.updateResponseCounter(EVENT, (int) answer.getResultCode());
			}else if(type.equals(AccountingRecordTypeEnum.START_RECORD)){
				logger.debug("received START_RECORD request");
				this.updateResponseCounter(SESSION, (int) answer.getResultCode());
			}else if(type.equals(AccountingRecordTypeEnum.INTERIM_RECORD)){
				logger.debug("received INTERIM_RECORD request");
				this.updateResponseCounter(SESSION, (int) answer.getResultCode());
			}else if(type.equals(AccountingRecordTypeEnum.STOP_RECORD)){
				logger.debug("received STOP_RECORD request");
				this.updateResponseCounter(SESSION, (int) answer.getResultCode());
			}else {
				logger.error("Wrong/Unkown type request received.");
				throw new ResourceException("Wrong/Unkown request type.");
			}
			RfAccountingResponseImpl response = new RfAccountingResponseImpl(answer);
			response.setProtocolSession(containerReq.getProtocolSession());
			logger.debug("devilering RfAccountingResponse");
			this.ra.deliverResponse(response);
			logger.debug("delivered RfAccountingResponse successfully");
		} catch (Exception ex) {
			logger.error("handleIncomingACA() failed: " + ex);
			logger.error(ex.getMessage(), ex);
		}
	}

	// TODO we may also send ResourceEvent for this.
	public void receivedErrorMessage(DiameterSession session,
			DiameterRequest pendingRequest, DiameterAnswer answer) {
		logger.debug("Inside receivedErrorMessage with " + answer);
		try {
			logger.debug("removing request from map");
			RfAccountingRequestImpl containerReq = (RfAccountingRequestImpl) this.requests.remove(pendingRequest);
			if (containerReq==null) {
				logger.error("corrosponding requst is not found. returnning");
				return;
			}
			AccountingRecordTypeEnum type = ((RfAccountingRequest)containerReq).getEnumAccountingRecordType();
			if(type.equals(AccountingRecordTypeEnum.EVENT_RECORD)){
				logger.debug("received EVENT_RECORD request");
				this.updateResponseCounter(EVENT, (int) ((MessageACA)answer).getResultCode());
			}else if(type.equals(AccountingRecordTypeEnum.START_RECORD)){
				logger.debug("received START_RECORD request");
				this.updateResponseCounter(SESSION, (int) ((MessageACA)answer).getResultCode());
			}else if(type.equals(AccountingRecordTypeEnum.INTERIM_RECORD)){
				logger.debug("received INTERIM_RECORD request");
				this.updateResponseCounter(SESSION, (int) ((MessageACA)answer).getResultCode());
			}else if(type.equals(AccountingRecordTypeEnum.STOP_RECORD)){
				logger.debug("received STOP_RECORD request");
				this.updateResponseCounter(SESSION, (int) ((MessageACA)answer).getResultCode());
			}else {
				logger.error("Wrong/Unkown type request received.");
				throw new ResourceException("Wrong/Unkown request type.");
			}
			RfResponse response=null;
			response = new RfAccountingResponseImpl((MessageACA)answer);	
			((RfMessage) response).setProtocolSession(containerReq.getProtocolSession());
			logger.debug("devilering receivedErrorMessage event");
			//this.ra.deliverResponse(response);
			RfResourceEvent resourceEvent = new RfResourceEvent(this, 
					RfResourceEvent.ERROR_MSG_RECEIVED, response.getApplicationSession());
			resourceEvent.setMessage((RfMessage) response);
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering RfResourceEvent"+e);
			}
			logger.debug("devilered receivedErrorMessage event");
		} catch (Exception ex) {
			logger.error("receivedErrorMessage() failed: " + ex);
			//logger.error(ex.getMessage(), ex);
		}
	}


	public void timeoutExpired(DiameterSession session,
			DiameterRequest pendingRequest) {
		logger.debug("Inside TimeoutExpired for request " + pendingRequest);
		RfAccountingRequestImpl containerReq = (RfAccountingRequestImpl) this.requests.get(pendingRequest);
		if(containerReq==null){
			logger.debug("Request already handled");
			return;
		}
		if(containerReq.getRetryCounter()< MAX_RETYR_COUNTER){
			try{
				containerReq.incrementRetryCounter();
				containerReq.setReTransmitted(true);
				((SessionRfClient)session).sendACR((MessageACR)pendingRequest, 5, TimeUnit.SECONDS);
			}catch(Exception e){
				logger.debug("Exception in sending retransmit req ",e);
			}
		}else{
			logger.debug("re-transmissions exceeded. Devivering request fail event");
			this.requests.remove(((RfAccountingRequestImpl)containerReq).getStackObj());
			RfResourceEvent resourceEvent = new RfResourceEvent(containerReq, 
					RfResourceEvent.REQUEST_FAIL_EVENT, containerReq.getApplicationSession());
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering RfResourceEvent",e);
			}
		}
	}

	public void requestTimeoutExpired(DiameterSession session,
			DiameterRequest pendingRequest) {
		logger.debug("Inside requestTimeoutExpired for request " + pendingRequest);
		RfAccountingRequestImpl containerReq = (RfAccountingRequestImpl) this.requests.get(pendingRequest);
		if(containerReq.getRetryCounter()< MAX_RETYR_COUNTER){
			try{
				containerReq.incrementRetryCounter();
				containerReq.setReTransmitted(true);
				((SessionRfClient)session).sendACR((MessageACR)pendingRequest, 5, TimeUnit.SECONDS);
			}catch(Exception e){
				logger.debug("Exception in sending retransmit req "+e);
			}
		}else{
			this.requests.remove(((RfAccountingRequestImpl)containerReq).getStackObj());
			RfResourceEvent resourceEvent = new RfResourceEvent(containerReq, 
					RfResourceEvent.REQUEST_FAIL_EVENT, containerReq.getApplicationSession());
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering RfResourceEvent"+e);
			}
		}
	}

	// mathod to update response counter
	private void updateResponseCounter(int type, int resultCode) 
	{
		switch (type) 
		{
		case EVENT:
			if (resultCode >= 1000 && resultCode < 2000) 
			{
				this.acaEvent1xxxCnt.increment();
			} 
			else if (resultCode >= 2000 && resultCode < 3000) 
			{
				this.acaEvent2xxxCnt.increment();
			}
			else if (resultCode >= 3000 && resultCode < 4000) 
			{
				this.acaEvent3xxxCnt.increment();
			}
			else if (resultCode >= 4000 && resultCode < 5000) 
			{
				this.acaEvent4xxxCnt.increment();
			}
			else if (resultCode >= 5000 && resultCode < 6000) 
			{
				this.acaEvent5xxxCnt.increment();
			}
			break;
		case SESSION:
			if (resultCode >= 1000 && resultCode < 2000) 
			{
				this.acaSession1xxxCnt.increment();
			} 
			else if (resultCode >= 2000 && resultCode < 3000) 
			{
				this.acaSession2xxxCnt.increment();
			}
			else if (resultCode >= 3000 && resultCode < 4000) 
			{
				this.acaSession3xxxCnt.increment();
			} 
			else if (resultCode >= 4000 && resultCode < 5000) 
			{
				this.acaSession4xxxCnt.increment();
			} 
			else if (resultCode >= 5000 && resultCode < 6000) 
			{
				this.acaSession5xxxCnt.increment();
			}
			break;
		}
	}

	///////////////////////////////////////////////////////////


	public static synchronized int getNextHandle() {
		return RfStackInterfaceImpl.handleCount++;
	}

	public void addRequestToMap(int handle, RfRequest request) {
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
			RfResourceEvent event = new RfResourceEvent(this, 
					RfResourceEvent.RF_DISCONNECT_PEER_RESPONSE, 
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
			RfResourceEvent event = new RfResourceEvent(this, 
					RfResourceEvent.RF_NOTIFY_DISCONNECT_PEER_REQUEST, 
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
			RfResourceEvent event = new RfResourceEvent(this, 
					RfResourceEvent.RF_NOTIFY_PEER_UP,
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
	// PeerStateListener inteface implementation starts /////
	/////////////////////////////////////////////////////////
	
	@Override
	public void stateChanged(Enum rfEvent, Enum oldState, Enum newState) {


		if (newState == PcbState.DOWN) {
			logger.error(" ************** " + serverPeer + " is DOWN *************** ");
			try {
				RfResourceEvent event = new RfResourceEvent(this, 
						RfResourceEvent.RF_NOTIFY_PEER_DOWN, 
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
				RfResourceEvent event = new RfResourceEvent(this, 
						RfResourceEvent.RF_NOTIFY_PEER_UP, 
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

