package com.baypackets.ase.ra.diameter.ro.stackif;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.ra.diameter.ro.CreditControlRequest;
import com.baypackets.ase.ra.diameter.ro.RoMessage;
import com.baypackets.ase.ra.diameter.ro.RoRequest;
import com.baypackets.ase.ra.diameter.ro.RoResourceAdaptor;
import com.baypackets.ase.ra.diameter.ro.RoResourceEvent;
import com.baypackets.ase.ra.diameter.ro.RoResourceException;
import com.baypackets.ase.ra.diameter.ro.RoResourceFactory;
import com.baypackets.ase.ra.diameter.ro.RoResponse;
import com.baypackets.ase.ra.diameter.ro.RoStackInterface;
import com.baypackets.ase.ra.diameter.ro.enums.CCRequestTypeEnum;
import com.baypackets.ase.ra.diameter.ro.enums.RequestedActionEnum;
import com.baypackets.ase.ra.diameter.ro.impl.RoMessageFactoryImpl;
import com.baypackets.ase.ra.diameter.ro.impl.RoResourceAdaptorFactory;
import com.baypackets.ase.ra.diameter.ro.impl.RoSession;
import com.baypackets.ase.ra.diameter.ro.utils.AvpCodes;
import com.baypackets.ase.ra.diameter.ro.utils.Constants;
import com.baypackets.ase.ra.diameter.ro.utils.ResultCodes;
import com.baypackets.ase.ra.diameter.ro.utils.RoStackConfig;
import com.baypackets.ase.ra.diameter.ro.utils.statistic.RoStatsManager;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.util.TimerService;
import com.baypackets.ase.util.AseAlarmService;

import fr.marben.diameter.DiameterAVP;
import fr.marben.diameter.DiameterCapabilityExchangeListener;
import fr.marben.diameter.DiameterErrorEvent;
import fr.marben.diameter.DiameterEvent;
import fr.marben.diameter.DiameterException;
import fr.marben.diameter.DiameterFactory;
import fr.marben.diameter.DiameterListeningPoint;
import fr.marben.diameter.DiameterMessage;
import fr.marben.diameter.DiameterMessageEvent;
import fr.marben.diameter.DiameterNotification;
import fr.marben.diameter.DiameterNotificationListener;
import fr.marben.diameter.DiameterPeer;
import fr.marben.diameter.DiameterPeerCapabilitiesEvent;
import fr.marben.diameter.DiameterPeerStateChange;
import fr.marben.diameter.DiameterProvider;
import fr.marben.diameter.DiameterProviderManager;
import fr.marben.diameter.DiameterRealmStateChangeEvent;
import fr.marben.diameter.DiameterRoute;
import fr.marben.diameter.DiameterRoute.RouteState;
import fr.marben.diameter.DiameterRouteStateChangeEvent;
import fr.marben.diameter.DiameterSession;
import fr.marben.diameter.DiameterStack;
import fr.marben.diameter.DiameterStackManager;
import fr.marben.diameter.DiameterUnsigned32AVP;
import fr.marben.diameter._3gpp.ro.DiameterRoMessageFactory;
import fr.marben.diameter._3gpp.ro.DiameterRoProvider;
import fr.marben.diameter._3gpp.ro.DiameterRoR14Listener;

public class RoStackInterfaceImpl extends Thread implements RoStackInterface,
                                                            DiameterNotificationListener,DiameterStackManager, 
                                                            DiameterProviderManager,DiameterRoR14Listener,DiameterCapabilityExchangeListener,Constants{

	private static Logger logger = Logger.getLogger(RoStackInterfaceImpl.class);
	private static RoResourceFactory raFactory;
	private static TimerService timerService;
	private static long timeout = 30;
	private static int handleCount = 0;

	int loop_delay = 1000;

	public static DiameterStack stack;
	private static boolean stackStarted;
	private static Timer monitorTimer;

	public DiameterPeer serverPeer;
	public static String serverContextId;
	String configFile;
//	public static final DiameterApplicationId applicationId =
//		new DiameterApplicationId(Standard.Ro.applicationId, ApplicationType.Auth);
	private RoStackClientInterfaceImpl clientInterface;

	private RoResourceAdaptor ra;
	protected static Map<String,Object> requests;
	private AseAlarmService alarmService;
	private RoStatsManager statsManager;
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
	public static  DiameterRoProvider roProvider;
	private DiameterRoute serverRoute;
	private String remotePeerRoutes;
	
//	int stopTime = 0;
	boolean isAlive = true;
	boolean isRunning = false;
	
	boolean routeAdded = false;
	private DiameterRoMessageFactory roFactory;
	private Properties stackProperties;
	//private DiameterRoProvider provider;
	// String localURI=null;
	private DiameterRoute route;
	public static  String localhost;
	
	private String originRealm=null;// for creating listeneing point
	private List<String> destRealm=null;// for creating route
	
	private String listeningPoints;
	//private String remotePeerRoutes;
	private boolean isclientMode=true;
	private String extendedDictionary=null;
	
	private long authApplicationId=4;

	public RoStackInterfaceImpl(RoResourceAdaptor ra) {
		this.ra = ra;
		this.requests = new ConcurrentHashMap(64*1024); // initialize with 64 K entries 
		clientInterface = new RoStackClientInterfaceImpl(ra);
	}

	public void init(ResourceContext context) throws RoResourceException {
		this.alarmService = (AseAlarmService) context.getAlarmService();
		RoStackInterfaceImpl.raFactory = (RoResourceFactory)context.getResourceFactory();
		RoStackInterfaceImpl.timerService = (TimerService)context.getTimerService();
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
		"conf" + File.separator + "diameter_ro.yml";
		
		this.extendedDictionary = (String)context.getConfigProperty("ase.home") + File.separator + 
				"conf" + File.separator + "diameter_ro_ext_dictionary.xml";
		
		logger.debug("Use [" + this.configFile + "] and extended dictionary is exits [" + this.extendedDictionary + "]");
		statsManager=new RoStatsManager(measurementMgr);
		init(configFile);
		clientInterface.init(context);
	}

	public void init(String cfgFile) throws RoResourceException {
		try {
			RoStackInterfaceImpl.raFactory = RoResourceAdaptorFactory.getResourceFactory();
			this.configFile = cfgFile;

			logger.debug("creating transport stack");
			
			RoStackConfig.loadconfiguration(this.configFile,this.extendedDictionary);
			originRealm=RoStackConfig.getOriginRealm();
			destRealm=RoStackConfig.getDestRealm();
			localhost=RoStackConfig.getLocalFQDN();
			isclientMode=RoStackConfig.isClientModeEnabled();
//			
//			  originRealm="serverRealm";
//			  destRealm="clientRealm";
//			 localhost="cas00fip.agnity.com";
//			 localURI="aaa://"+localhost+":3868";
			
			serverContextId=RoStackConfig.getServiceContextId();//"roContext@agnity.com";
//			
			 
			stackProperties= new Properties();
			/**
			 * To enable stack management the property ENABLE_STACKMANAGEMENT has to
			 * be set to true
			 */
			stackProperties.setProperty("fr.marben.diameter.ENABLE_STACKMANAGEMENT",
					"true");
			
//			logger.debug("createDiameterStack  with realm "+originRealm +" FQDN " +localhost);
//			
//			DiameterFactory factory =DiameterFactory.getInstance();
//			stack = factory.createDiameterStack(originRealm,
//					localhost, stackProperties);
//			
//				logger.info("Server starting");
//				
//
//			logger.debug("initializing transport stack");
//			
//			/**
//			 * Register shutdown hook
//			 */
//			DiameterRoR14ServerShutdown ro = new DiameterRoR14ServerShutdown(this);
//			Runtime.getRuntime().addShutdownHook(ro);
//			
//			/**
//			 * Set this class as an implementation of a diameter stack manager
//			 */
//			stack.setDiameterStackManager(this);
//
//			/**
//			 * Register our application as a DiameterNotificationListener,so that we
//			 * receive all the notifications from the Diameter stack.
//			 */
//			stack.setDiameterNotificationListener(this);
//			
//			logger.debug("stack loaded successfully" +stack);
		} catch (Exception ex) {
			logger.error("RoResourceFactory.init() failed.");
			logger.error(ex.getMessage(), ex);
			throw new RoResourceException("RoResourceFactory.init() failed.");
		}

	}
	
	public void shutdown() {
		logger.debug("shutdown()");
		stack.eventImmediateStop("DIAMETER_UNABLE_TO_COMPLY",
				"Perform shutdown");
		try {
			sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			logger.error(" xeption "+e);
		}
	}
	
	// Example shutdown hook class
	class DiameterRoR14ServerShutdown extends Thread {
		private RoStackInterfaceImpl server = null;

		public void run() {

			System.err.println("DiameterRoR14ServerShutdown hook called");
			if (server.getDiameterStack() != null) {
				System.err.println("Stopping the stack...");
				server.shutdown();
				server.getDiameterStack() .stop();
			}
		}

		DiameterRoR14ServerShutdown(RoStackInterfaceImpl srv) {
			this.server = srv;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public void run() {
		
		logger.info("Inside run of RoStackInterface ...with lopp delay "+loop_delay);
		long startTime = System.currentTimeMillis();

		/**
		 * In server mode, infinite loop
		 */
		boolean bIsAlive = true;
		boolean bIsRunning = true;
		while (bIsAlive) {
			synchronized (this) {
				bIsAlive = isAlive;
				bIsRunning = isRunning;
			}

			/*
			 * Sleep for the delay specified in the -l option, or 1 millisecond
			 * if no -l option is set.
			 */
			try {
				sleep(loop_delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		try {
			/* Final cleanup before exit */

			{
				logger.info("Removing the provider...");
				stack.deleteDiameterProvider(roProvider);
				roProvider = null;
			}

			{
				logger.info("Deleting all Listening points...");
				Iterator<DiameterListeningPoint> it;
				do {
					it = stack.getDiameterListeningPoints();
					if (it.hasNext()) {
						DiameterListeningPoint lp = (DiameterListeningPoint) it
								.next();
						stack.deleteDiameterListeningPoint(lp);
						lp = null;
					}
				} while (it.hasNext());
			}
			logger.info("Stopping the stack...");
			stack.stop();

		} catch (DiameterException e) {
			logger.error("Exception caught: " + e);
		}
	}
	
	
	public void startStack() throws RoResourceException {
		logger.debug("Inside RoStackInterfaceImpl start()");
		try{
					
//			monitorTimer=new Timer();
//			monitorTimer.scheduleAtFixedRate(new RoStackMonitorTask(stack), 10000, 10000);	
			
           logger.debug("createDiameterStack  with realm "+originRealm +" FQDN " +localhost);
			
			DiameterFactory factory =DiameterFactory.getInstance();
			stack = factory.createDiameterStack(originRealm,
					localhost, stackProperties);
			
				logger.info("Server starting");

			logger.debug("initializing transport stack");
			
			/**
			 * Register shutdown hook
			 */
			DiameterRoR14ServerShutdown ro = new DiameterRoR14ServerShutdown(this);
			Runtime.getRuntime().addShutdownHook(ro);
			
			/**
			 * Set this class as an implementation of a diameter stack manager
			 */
			stack.setDiameterStackManager(this);

			/**
			 * Register our application as a DiameterNotificationListener,so that we
			 * receive all the notifications from the Diameter stack.
			 */
		
			stack.setDiameterNotificationListener(this);
			stack.setDiameterCapabilityExchangeListener(this);
			
			logger.debug("stack loaded successfully" +stack);
			this.start();
			
			if (isclientMode) {

				logger.debug("Client mode is enabled hence start client interface as well ");
				clientInterface.startStack();
			}
			statsManager.startManager();
		} catch (Exception ex) {
			logger.error("startStack failed.");
			logger.error(ex.getMessage(), ex);
			throw new RoResourceException("startStack failed.");
		}
	}

	public void stopStack() throws RoResourceException {
		logger.info("stop(): closing Diameter stack...");
		statsManager.stopManager();
		try{
			logger.debug("stop(): initialize Marben stack.");
			if(monitorTimer!=null){
				if(logger.isDebugEnabled()){
					logger.debug("Canceling RoPeerMonitorTask for peer monitoring");
				}
				monitorTimer.cancel();
			}
			//	StackState state = 
		    stack.stop();//stop(5, TimeUnit.SECONDS);
		    isAlive=false;
		    isRunning=false;
			if (isclientMode) {
				logger.debug("Client mode is enabled hence stop client interface as well ");
				clientInterface.stopStack();
			}
		} catch (Exception ex) {
			logger.error("RoResourceFactory.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new RoResourceException("RoResourceFactory.init() failed.");
		}

	}
public void incrementRequestedActionCounter(CreditControlRequest request) throws RoResourceException
{
	RequestedActionEnum type=request.getEnumRequestedAction();
		if(RequestedActionEnum.DIRECT_DEBITING.equals(type)){
			this.ccrDirectDebitCnt.increment();
			logger.debug("Requested Action Direct Debiting incrementing Counter");
		}else if(RequestedActionEnum.REFUND_ACCOUNT.equals(type)){
			this.ccrAccountRefundCnt.increment();
			logger.debug("Requested Action Account Refund incrementing Counter");
		}else if(RequestedActionEnum.CHECK_BALANCE.equals(type)){
			this.ccrBalanceCheckCnt.increment();
			logger.debug("Requested Action Balance Check incrementing Counter");
		}else if(RequestedActionEnum.PRICE_ENQUIRY.equals(type)){
			this.ccrPriceEnquiryCnt.increment();
			logger.debug("Requested Action Price Enquiry incrementing Counter");
		}else {
			logger.error("Wrong/Unkown requested Action type.");
			
		}
	}
	
    /**
     * For outgoing Credit Control request
     */
	public void handleRequest(RoRequest request) throws RoResourceException {		
		logger.debug("handleRequest(RoRequest) called ");
		if (request == null) {
			logger.error("handleRequest(): null request. returning");
			return;
		}
		//int handle = -1;
		RoSession session = (RoSession)request.getSession();
		try {
			//handle = RoStackInterfaceImpl.getNextHandle();
			//session.setHandle(handle);
			((CreditControlRequestImpl)request).incrementRetryCounter();
			logger.debug("request map size before adding is :[ "+ this.requests.size()+"]"); 
			this.requests.put(((CreditControlRequestImpl)request).getStackObj().getSessionIdAVPValue(),request);
			
			logger.debug("request map size after adding is :[ "+ this.requests.size()+"]");
			
			//Set worker queue for message
			((SipApplicationSessionImpl)session.getApplicationSession()).getIc().setWorkQueue(((RoAbstractRequest)request).getWorkQueue());
			
			
			if (RoStackConfig.isStateless()) {
				if(logger.isDebugEnabled()){
					logger.debug( "send CCR message On stateless roProvider ");
				}
				RoStackClientInterfaceImpl.roProvider
				.sendMessage(((CreditControlRequestImpl) request)
						.getStackObj());
				
			} else {
				if(logger.isDebugEnabled()){
					logger.debug( "send CCR message On client stack session");
				}
				session.getClientStackSession().sendMessage(
						((CreditControlRequestImpl) request).getStackObj());// sendCCR(((CreditControlRequestImpl)request).getStackObj(),
																			// 5,
																			// TimeUnit.SECONDS);
			}
			
			long type = ((CreditControlRequest)request).getCCRequestType();
			if(type==CCRequestTypeEnum.getCode(CCRequestTypeEnum.EVENT_REQUEST)){
				logger.debug("sent EVENT_REQUEST request");
				this.incrementRequestedActionCounter((CreditControlRequest) request);
				this.ccrEventCnt.increment();
			}else if(type==CCRequestTypeEnum.getCode(CCRequestTypeEnum.INITIAL_REQUEST)){
				logger.debug("sent INITIAL_REQUEST request");
				this.ccrFirstInteroCnt.increment();
				this.ccrSessionCnt.increment();
			}else if(type==CCRequestTypeEnum.getCode(CCRequestTypeEnum.UPDATE_REQUEST)){
				logger.debug("sent UPDATE_REQUEST request");
				this.ccrInterimInteroCnt.increment();
				this.ccrSessionCnt.increment();
			}else if(type==CCRequestTypeEnum.getCode(CCRequestTypeEnum.TERMINATION_REQUEST)){
				logger.debug("sent TERMINATION_REQUEST request");
				this.ccrFinalInteroCnt.increment();
				this.ccrSessionCnt.increment();
			}else {
				logger.error("Wrong/Unkown request type.");
				throw new ResourceException("Wrong/Unkown request type.");
			}
		} catch (DiameterException ex) {
			logger.error("ValidationException in sending Ro Request.",ex);
			removeRequestFromMap(((CreditControlRequestImpl)request).getStackObj());
			RoResourceEvent resourceEvent = new RoResourceEvent(request, 
					RoResourceEvent.REQUEST_FAIL_EVENT, request.getApplicationSession());
			resourceEvent.setMessage((RoMessage)request);
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering RoResourceEvent"+ex);
				throw new RoResourceException(ex);
			}
		}catch(Exception ex){
			logger.error("handleRequest() failed: " + ex);
			logger.error(ex.getMessage(), ex);
			throw new RoResourceException(ex);
		}
	}

	/**
     * For outgoing Credit Control response
     */
	public void handleResponse(RoResponse response) throws RoResourceException {
		logger.debug("Entering handleResponse()");
		
		boolean sentSuccessfully = true;

		if (response == null) {
			logger.error("handleResponse(): null response. returning");
			return;
		}
		try {
			
				logger.debug("Passing on to server interface");
				clientInterface.handleResponse(response);					

		} catch (Exception ex) {
			
			logger.error("handleResponse() failed: " ,ex);
			sentSuccessfully=false;
		}

		if(!sentSuccessfully) {
			
			RoResourceEvent resourceEvent = new RoResourceEvent(response, 
					RoResourceEvent.RESPONSE_FAIL_EVENT, response.getApplicationSession());
			resourceEvent.setMessage((RoMessage)response);
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering RoResourceEvent :: ",e);
				throw new RoResourceException(e);
			}

		}
	}

	// TODO we may also send ResourceEvent for this.
	public void receivedErrorMessage(DiameterSession session,
			DiameterMessage pendingRequest, DiameterMessage answer) {
		logger.debug("Inside receivedErrorMessage with " + answer);
		try {
			logger.debug("removing request from map");
			CreditControlRequestImpl containerReq = (CreditControlRequestImpl) this.requests.remove(pendingRequest.getSessionIdAVPValue());
			if (containerReq==null) {
				logger.error("corrosponding requst is not found. returnning");
				return;
			}
			CCRequestTypeEnum type = ((CreditControlRequest)containerReq).getEnumCCRequestType();
			if(type.equals(CCRequestTypeEnum.EVENT_REQUEST)){
				logger.debug("received EVENT_RECORD request");
				this.updateResponseCounter(EVENT, (int) answer.getResultCodeAVP());
			}else if(type.equals(CCRequestTypeEnum.INITIAL_REQUEST)){
				logger.debug("received START_RECORD request");
				this.updateResponseCounter(SESSION, (int)answer.getResultCodeAVP());
			}else if(type.equals(CCRequestTypeEnum.UPDATE_REQUEST)){
				logger.debug("received INTERIM_RECORD request");
				this.updateResponseCounter(SESSION, (int) answer.getResultCodeAVP());
			}else if(type.equals(CCRequestTypeEnum.TERMINATION_REQUEST)){
				logger.debug("received STOP_RECORD request");
				this.updateResponseCounter(SESSION, (int) answer.getResultCodeAVP());
			}else {
				logger.error("Wrong/Unkown type request received.");
				throw new ResourceException("Wrong/Unkown request type.");
			}
			
			RoResponse response= new CreditControlAnswerImpl(answer);	
			((RoMessage) response).setProtocolSession(containerReq.getProtocolSession());
			logger.debug("devilering receivedErrorMessage event");
			//this.ra.deliverResponse(response);
			RoResourceEvent resourceEvent = new RoResourceEvent(this, 
					RoResourceEvent.ERROR_MSG_RECEIVED, response.getApplicationSession());
			resourceEvent.setMessage((RoMessage) response);
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering RoResourceEvent"+e);
			}
			logger.debug("devilered receivedErrorMessage event");
		} catch (Exception ex) {
			logger.error("receivedErrorMessage() failed: " + ex);
			logger.error(ex.getMessage(), ex);
		}
	}


	public void timeoutExpired(DiameterSession session,
			DiameterMessage pendingRequest) {
		logger.debug("Inside TimeoutExpired for request " + pendingRequest);
		CreditControlRequestImpl containerReq = (CreditControlRequestImpl) this.requests.get(pendingRequest.getSessionIdAVPValue());
		if(containerReq==null){
			logger.debug("Request already handled");
			return;
		}
		if(containerReq.getRetryCounter()< MAX_RETYR_COUNTER){
			try{
				containerReq.incrementRetryCounter();
				containerReq.setReTransmitted(true);
				session.sendMessage(pendingRequest);
			//	((SessionRoClient)session).sendCCR((MessageCCR)pendingRequest, 5, TimeUnit.SECONDS);
			}catch(Exception e){
				logger.debug("Exception in sending retransmit req ",e);
			}
		}else{
			logger.debug("re-transmissions exceeded. Devivering timeout event");
			removeRequestFromMap(((CreditControlRequestImpl)containerReq).getStackObj().getSessionIdAVPValue());
			RoResourceEvent resourceEvent = new RoResourceEvent(containerReq, 
					RoResourceEvent.TIMEOUT_EVENT, containerReq.getApplicationSession());
			resourceEvent.setMessage(containerReq);
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering RoResourceEvent",e);
			}
		}
	}

	public void requestTimeoutExpired(DiameterSession session,
			DiameterMessage pendingRequest) {
		logger.debug("Inside requestTimeoutExpired for request " + pendingRequest);
		CreditControlRequestImpl containerReq = (CreditControlRequestImpl) this.requests.get(pendingRequest.getSessionIdAVPValue());
		if(containerReq.getRetryCounter()< MAX_RETYR_COUNTER){
			try{
				containerReq.incrementRetryCounter();
				containerReq.setReTransmitted(true);
				session.sendMessage(pendingRequest);
			//	((SessionRoClient)session).sendCCR((MessageCCR)pendingRequest, 5, TimeUnit.SECONDS);
			}catch(Exception e){
				logger.debug("Exception in sending retransmit req "+e);
			}
		}else{
			removeRequestFromMap(((CreditControlRequestImpl)containerReq).getStackObj());
			RoResourceEvent resourceEvent = new RoResourceEvent(containerReq, 
					RoResourceEvent.TIMEOUT_EVENT, containerReq.getApplicationSession());
			resourceEvent.setMessage(containerReq);
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering RoResourceEvent"+e);
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

	 

	//////////////////////////////////////////////////////////
	////////// PeerDisconnectListener Interface methods. /////
	//////////////////////////////////////////////////////////

//	public void dpaReceived(MessageDPA dpa, Peer peer) {
//		logger.debug("Inside Inside dpaReceived");
//		try {
//			RoResourceEvent event = new RoResourceEvent(this, 
//					RoResourceEvent.RO_DISCONNECT_PEER_RESPONSE, 
//					null);
//			event.setData(peer.getHost());
//			ra.deliverEvent(event);
//			logger.debug("delivered dpaReceived event");
//		} 
//		catch(Exception ex) {
//			logger.error(ex.toString(),ex);
//		}
//	}
//
//	//@Override
//	public void dprReceived(MessageDPR dpr, Peer peer) {
//		logger.debug("Inside Inside dprReceived");
//		try {
//			RoResourceEvent event = new RoResourceEvent(this, 
//					RoResourceEvent.RO_NOTIFY_DISCONNECT_PEER_REQUEST, 
//					null);
//			event.setData(peer.getHost());
//			ra.deliverEvent(event);
//			logger.debug("delivered dprReceived event");
//		} 
//		catch(Exception ex) {
//			logger.error(ex.toString(),ex);
//		}
//	}
//
//	//@Override
//	public void previewDPA(MessageDPA arg0, Peer arg1) {
//		logger.debug("Inside previewDPA");
//	}
//
//	//@Override
//	public void previewDPR(MessageDPR arg0, Peer arg1) {
//		logger.debug("Inside previewDPR");
//	}

	//////////////////////////////////////////////////////////////
	////////// CapabilityExchangeListener Interface methods. /////
	//////////////////////////////////////////////////////////////

//	@Override
//	public void ceaReceived(MessageCEA message, Peer peer) {
//		logger.debug("Inside ceaReceived");
//		try {
//			RoResourceEvent event = new RoResourceEvent(this, 
//					RoResourceEvent.RO_NOTIFY_PEER_UP,
//					null);
//			event.setData(peer.getHost());
//			ra.deliverEvent(event);
//		} 
//		catch(Exception ex) {
//			logger.error(ex.toString(),ex);
//		}
//		logger.debug("delivered ceaReceived event");
//	}
//
//	@Override
//	public void cerReceived(MessageCER arg0, Peer arg1) {
//		logger.debug("Inside cerReceived");
//	}
//
//	@Override
//	public void previewCEA(MessageCEA arg0, Peer arg1) {
//		logger.debug("Inside previewCEA");
//	}
//
//	@Override
//	public void previewCER(MessageCER arg0, Peer arg1) {
//		logger.debug("Inside previewCER");
//	}

	/////////////////////////////////////////////////////////
	// PeerStateListener interface implementation starts /////
	/////////////////////////////////////////////////////////

//	public void stateChanged(Enum roEvent, Enum oldState, Enum newState) {
//
//		if (newState == PcbState.DOWN) {
//			logger.error(" ************** " + serverPeer + " is DOWN *************** ");
//			try {
//				RoResourceEvent event = new RoResourceEvent(this, 
//						RoResourceEvent.RO_NOTIFY_PEER_DOWN, 
//						null);
//				event.setData(serverPeer.getHost());
//				ra.deliverEvent(event);
//				logger.debug("delivered peer down event");
//			} 
//			catch(Exception ex) {
//				logger.error(ex.toString(),ex);
//			}
//		}
//		else if (newState == PcbState.OKAY) {
//			logger.error(" ************** " + serverPeer + " is OKAY ************** ");
//			try {
//				RoResourceEvent event = new RoResourceEvent(this, 
//						RoResourceEvent.RO_NOTIFY_PEER_UP, 
//						null);
//				event.setData(serverPeer.getHost());
//				ra.deliverEvent(event);
//				logger.debug("delivered peer up event");
//			} 
//			catch(Exception ex) {
//				logger.error(ex.toString(),ex);
//			}
//		}
//	}

	public void originStateIdChanged(long oldValue, long newValue) {
		logger.error("Origin-State-Id of " + serverPeer+
				" changed from " + oldValue + " to " + newValue);
	}

	public void removeRequestFromMap(Object request) {
		if(logger.isDebugEnabled())
			logger.debug("Removing request from map"+request);
		this.requests.remove(((CreditControlRequestImpl)request).getStackObj().getSessionIdAVPValue());
	}
	
	public CreditControlRequestImpl getInitialOrEventRequest(String sesionId) {
		if(logger.isDebugEnabled())
			logger.debug("getPrevCCRFromMap from map"+sesionId +" Map "+this.requests);
		return (CreditControlRequestImpl) this.requests.get(sesionId);
	}
	
	
	/**
	 * 
	 * This class will be used to monitor roClient stack.<br>
	 * This timer task will periodically check stack state:<br>
 	 * <ol> 
 	 * <li> Stack in not started successfully then it will try to restart stack.</li>
 	 * 		OR
 	 * <li> If server peer is not connected then it will try to reconnect peer.</li></ol>
	 * @author Amit Baxi
	 *
	 */
	private class RoStackMonitorTask extends TimerTask {
		
		Logger logger= Logger.getLogger(RoStackMonitorTask.class);
		
		DiameterStack stack;
		int count=1;
		RoStackMonitorTask(DiameterStack stack2){
			this.stack=stack2;
		}
		@Override
		public void run()  {
			if(logger.isDebugEnabled()){
				logger.debug("Inside run() method.");
			}
			try {
				if(stackStarted){
					
					if(count==1){
					ClientCreateSessionAndSendRequest();
					count=0;
					}
//					// Stack started so check for peers
//					DiameterPeer serverpeer=(DiameterPeer) stack.getDiameterPeers().next();
//					if(serverpeer.getPeerState()!=DiameterPeer.Open.name()){
//						if(logger.isDebugEnabled()){
//							logger.debug("PeerState is not OPEN disconnecting server peer....");
//						}
//						stack.deleteDiameterRoute(serverRoute);//rserverpeer..disconnect(5, TimeUnit.SECONDS);
//						if(logger.isDebugEnabled()){
//							logger.debug("Connecting server peer....");
//						}
//						//serverpeer..connect();
//					}
//					else{
//						if(logger.isDebugEnabled()){
//							logger.debug("Server peer is in OPEN state so not doing anything.");
//						}
//					}
//				}else{
//					// Stack not started properly in start() method so will restart.
//					if(logger.isDebugEnabled()){
//						logger.debug("Going to restart stack");
//					}
//					stack.stop();
				//	StackState state = stack..start();
//					if (state == StackState.Working) {
//						if(logger.isDebugEnabled()){
//							logger.debug("Stack restarted successfully" + stack);
//						}
//						stackStarted=true;
//					}else{
//						logger.error("State is " + state + ", Failed to start " + stack);
//					}
				}
			} catch (Exception e) {
				logger.error("Exception inside run()"+e.getMessage(),e);
			}
//			if(logger.isDebugEnabled()){
//				logger.debug("Exitting run() method.");
//			}
		}
	}

	public void ClientCreateSessionAndSendRequest() {
		logger.info("ClientCreateSessionAndSendRequest");
		try {

			/* 2. Create a Credit-Control Request (CCR) */
			/*
			 * Send an EVENT_REQUEST to launch a Stateless Session: Here we
			 * request a "Check Balance" for instance.
			 */
			DiameterMessage request = roFactory.createCreditControlRequest(
					originRealm, "exampleContext@domain",
					DiameterRoMessageFactory.EVENT_REQUEST, (long) 0);

			// Add Mandatory Requested-Action AVP
			DiameterAVP avp = stack.getDiameterMessageFactory()
					.createInteger32AVP(
							"Requested-Action",
							"base",
							stack.findEnumCode("base", "Requested-Action",
									DiameterRoMessageFactory.CHECK_BALANCE));

			request.add(avp);

			// ***********
			// Adding New AVP's

			int LCSClientType = 1;
			avp = stack.getDiameterMessageFactory().createInteger32AVP(
					"LCS-Client-Type", "3GPP", LCSClientType);
			request.add(avp);

			String PoCGroupName = "PoC-Group-Name";
			avp = stack.getDiameterMessageFactory().createOctetStringAVP(
					"PoC-Group-Name", "3GPP", PoCGroupName);
			request.add(avp);

			String GPPSGSNMCCMNC = "3GPP-SGSN-MCC-MNC";
			avp = stack.getDiameterMessageFactory().createOctetStringAVP(
					"3GPP-SGSN-MCC-MNC", "3GPP", GPPSGSNMCCMNC);
			request.add(avp);

			int LocationEstimateType = 0;
			avp = stack.getDiameterMessageFactory().createInteger32AVP(
					"Location-Estimate-Type", "3GPP", LocationEstimateType);
			request.add(avp);

			// ***********

			/* Send an INITIAL_REQUEST to launch a Stateful Session: */
			/*
			 * DiameterMessage request = roFactory.createCreditControlRequest
			 * (destRealm, 4, "exampleContext@domain",
			 * DiameterRoMessageFactory.INITIAL_REQUEST, 0);
			 */

			/*
			 * If several accurate routes (here, toward Rf applications) are
			 * configured, the route through which the message will be conveyed
			 * to the Accounting Server will be selected according to the values
			 * of the routing AVPs contained in the Message passed to the
			 * sendMessage() utility.
			 * 
			 * Here the createCreditControlRequest(...) method requets as first
			 * argument the Destination-Realm AVP. If several accurate routes
			 * towards the same Realm exist and are available, the stack will
			 * automatically select the route to use based on Metric values
			 * comparison. However, in such a case you can select by yourself
			 * the Peer to send the message to by specifying the value of the
			 * Destination-Host AVP and appending this AVP to the message to be
			 * sent:
			 */
			// Select the route towards a particular Peer:
			// request.setDestinationHostAVPValue("remotePeerNamefromCEA");
			// You may also change the Realm to select another route:
			// request.setDestinationRealmAVPValue("remoteRealmNamefromCEA");

			/*
			 * 3. Create a new session (with null as argument to let the stack
			 * allocate the session-ID). Send the message, and forget about the
			 * session until response notification.
			 */
	//		if (stopTime == 0) {
				DiameterSession session = roProvider
						.createClientDiameterAcctSession(null);

				logger.info("Sending CCR : " + request.toString());
				session.sendMessage(request);
				
			//}

			/*
			 * Session deletion will be dealt with in the callback receiving
			 * Accounting Answers. ( accountingAnswerReceived )
			 */

			/*
			 * 4. Display and update statistics.
			 */
			

		} catch (DiameterException e) {
			logger.error("Exception caught in "
					+ "ClientCreateSessionAndSendRequest: " + e.toString());
		}
	}
//	/* Create a client session and send a new request. */
//	public void ClientCreateSessionAndSendRequest() {
//		logger.info("ClientCreateSessionAndSendRequest");
//		try {
//
//			
//			/* the request is build based on message factory, for example: */
//			DiameterMessage request = roFactory.createCreditControlRequest(
//					"cas00fip.agnity.com", "exampleContext@domain",
//					DiameterRoMessageFactory.INITIAL_REQUEST, (long) 1);
//
//			// Add Mandatory Requested-Action AVP
//			DiameterAVP avp = stack.getDiameterMessageFactory()
//					.createInteger32AVP(
//							"Requested-Action",
//							"base",
//							stack.findEnumCode("base", "Requested-Action",
//									DiameterRoMessageFactory.CHECK_BALANCE));
//
//			request.add(avp);
//
//			// Adding New AVP's
//
//			int LCSClientType = 1;
//			avp = stack.getDiameterMessageFactory().createInteger32AVP(
//					"LCS-Client-Type", "3GPP", LCSClientType);
//			request.add(avp);
//
//			String PoCGroupName = "PoC-Group-Name";
//			avp = stack.getDiameterMessageFactory().createOctetStringAVP(
//					"PoC-Group-Name", "3GPP", PoCGroupName);
//			request.add(avp);
//
//			String GPPSGSNMCCMNC = "3GPP-SGSN-MCC-MNC";
//			avp = stack.getDiameterMessageFactory().createOctetStringAVP(
//					"3GPP-SGSN-MCC-MNC", "3GPP", GPPSGSNMCCMNC);
//			request.add(avp);
//
//			int LocationEstimateType = 0;
//			avp = stack.getDiameterMessageFactory().createInteger32AVP(
//					"Location-Estimate-Type", "3GPP", LocationEstimateType);
//			request.add(avp);
//			
//			/* DO NOT create a DiameterSession object, but:
//			 * - get a Session-Id AVP value from DiameterProvider if it is the first message of
//			 *   the session (for subsequent messages, it has to be stored/re-used by the upper 
//			 *   application)
//			 * - get the End-to-End identifier from DiameterStack
//			 * - set these Session-Id AVP and End-to-End identifier to the request
//			 */
//		
//		//	if (stopTime == 0) {
//				
//				/* Get the next session-Id value from the stack, store it member variable so 
//				   that it can be used for sending subsequent requests
//				*/
//				String sessionId = roProvider.getNextSessionIdValue();
//				
//				// Set session-Id value in the request
//				request.setSessionIdAVPValue(sessionId);
//				
//				// Set End-To-End Id in the request
//				
//				request.setEndtoEndId(stack.getNextEndToEndId());
//				
//				// Send the request 
//				logger.info("Sending CCR : " + request.toString());
//				roProvider.sendMessage(route,request);
//				
//		
//			//}
//
//		} catch (DiameterException e) {
//			logger.error("Exception caught in "
//					+ "ClientCreateSessionAndSendRequest: " + e.toString());
//		}
//	}


	@Override
	public String getAlternateRoute(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This method is called each time a remote peer tries to connect with us,
	 * but is not configured in the routing table as a known destination peer
	 * for at least one route.
	 * 
	 * In this case, we have here the opportunity to accept or refuse the
	 * incoming connection based on the content of the received CER message. The
	 * incoming CER received from the remote peer is given here as an input
	 * argument and we can check for some AVPs inside to validate if this peer
	 * is acceptable.
	 * 
	 * In this example, we just Display the remote peer name (by lookup up the
	 * Origin-Host AVP in the CER), and accept it systematically by returning
	 * 'true'.
	 */
	public boolean isUnknownPeerAuthorized(DiameterMessage incomingCER) {
		boolean status = false;
		logger.info(
				"isUnknownPeerAuthorized() is " + "invoked for remote peer: "
						+ incomingCER.getOriginHostAVPValue() +" destRealm "+ destRealm);
		if(destRealm==null){
			return true;
		}
		if (!isclientMode) {
			if (destRealm != null
					&& destRealm.contains(incomingCER.getOriginRealmAVPValue())) {
				status = true;
			} else {
				logger.info("isUnknownPeerAuthorized()"
						+ "return false unknown realm: "
						+ incomingCER.getOriginRealmAVPValue()+"  dest realm in config does not match orig host of client");

			}
		} else {
			status = true;
		}
		
		logger.info(
				"isUnknownPeerAuthorized() is " + "invoked for remote peer: "
						+ incomingCER.getOriginHostAVPValue() +" remote peer is authorized "+ status);
		return status;
	}

	@Override
	public void processEvent(DiameterEvent diamEvent) {
		// TODO Auto-generated method stub
		
		logger.info("processEvent called " + diamEvent);
		
		if(diamEvent instanceof DiameterRouteStateChangeEvent){
			
			DiameterRouteStateChangeEvent stateEvent=(DiameterRouteStateChangeEvent)diamEvent;
			String state=stateEvent.getDiameterRoute().getRouteState();
		if (state.equals(DiameterRoute.RouteState.busy)||state.equals(DiameterRoute.RouteState.infinite_loop)){
			logger.error(" ************** " + serverPeer + " is DOWN *************** ");
			try {
				RoResourceEvent event = new RoResourceEvent(this, 
						RoResourceEvent.RO_NOTIFY_PEER_DOWN, 
						null);
				event.setData(serverRoute.getIpAddress());
				ra.deliverEvent(event);
				logger.debug("delivered peer down event");
			} 
			catch(Exception ex) {
				logger.error(ex.toString(),ex);
			}
		}
		else if (state.equals(RouteState.available)){
			logger.error(" ************** " + serverPeer + " is OKAY ************** ");
			try {
				RoResourceEvent event = new RoResourceEvent(this, 
						RoResourceEvent.RO_NOTIFY_PEER_UP, 
						null);
				event.setData(serverRoute.getIpAddress());//serverPeer.getHost()
				ra.deliverEvent(event);
				logger.debug("delivered peer up event");
			} 
			catch(Exception ex) {
				logger.error(ex.toString(),ex);
			}
		}
		}else if(diamEvent instanceof DiameterPeerCapabilitiesEvent){
			
			logger.debug("Inside ceaReceived");
			try {
				RoResourceEvent event = new RoResourceEvent(this, 
						RoResourceEvent.RO_NOTIFY_PEER_UP,
						null);
				event.setData(serverPeer.getFQDN());
				ra.deliverEvent(event);
			} 
			catch(Exception ex) {
				logger.error(ex.toString(),ex);
		}
		}if (diamEvent instanceof DiameterMessageEvent) {

			logger.info("Message Event receivd " + diamEvent);

		} else if (diamEvent instanceof DiameterRealmStateChangeEvent) {

			DiameterRealmStateChangeEvent realmEvent = (DiameterRealmStateChangeEvent) diamEvent;

			/*
			 * Set the boolean class variable to tell the client it can send
			 * traffic now.
			 */

			//isRemoteRealmAvailable = realmEvent.isRealmAvailable();

			logger.info(
					"Event received: " + realmEvent.toString());

		}//Manage DiameterErrorEvent
		else if (diamEvent instanceof DiameterErrorEvent) {
			DiameterErrorEvent errorEvent = (DiameterErrorEvent) diamEvent;
			logger.info("Event received: " + errorEvent.toString());
			// To retrieve Session-id from DiameterErrorEvent
			String sessionId = errorEvent.getSessionId();
			// To retrieve DiameterMessage from DiameterErrorEvent
			DiameterMessage messsage = errorEvent.getDiameterMessage();
			// To retrieve Error Cause from DiameterErrorEvent
			int errorCause = errorEvent.getErrorCause();
		} else {
			logger.info("Event received: " + diamEvent.toString());
		}
	}

	@Override
	public boolean sendHook(fr.marben.diameter.DiameterSession arg0,
			DiameterMessage arg1) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void Closing(int aDate, int aStatus, int lastStackEvent) {
		logger.info("Stack::Closing(" + aDate + "," + aStatus
				+ "," + lastStackEvent + ")");
		/**
		 * Stopping the diameter stack.
		 * 
		 * stack.eventInit() can also be called instead to reinitialize the
		 * diameterstack.
		 */
		synchronized (this) {
			isAlive = false;
		}
		logger.info("Perform eventDie() from closing state");
		stack.eventDie();
	}

	@Override
	public void GracefulStopping(int aDate, int aStatus, int lastStackEvent) {
		// TODO Auto-generated method stub
		
		logger.info("Stack::GracefulStopping(" + aDate + ","
				+ aStatus + "," + lastStackEvent + ")");
		/**
		 * After 1 second in client mode switch to immediate stopping.
		 */
//		if (stopTime == 0) {
//			stopTime = aDate;
//		}
//		if ((aDate - stopTime) > 10000) {
			stack.eventImmediateStop("DIAMETER_UNABLE_TO_DELIVER",
					"Stack is stopping");
	//	}
	}

	
	/**
	 * Diameter Stack management callbacks that have to be implemented. These
	 * callbacks implements stack level call-backs
	 * 
	 */
	public void Idle(int aDate, int aStatus, int lastStackEvent) {
		// TODO Auto-generated method stub
		logger.info("Stack::Idle(" + aDate + "," + aStatus
				+ "," + lastStackEvent );

		// prevent re-execution when coming back from closing state
		if (lastStackEvent != DiameterStack.EVENTDIE) {
			try {
				
				logger.info("Stack::Idle  create RoProvider");
				/**
				 * Add our application's own AVP and command codes in addition
				 * to the Ro Release13 dictionary which is already preloaded in
				 * the Diameter stack.
				 */
				roFactory = stack
						.getDiameterRoMessageFactory(DiameterStack.RELEASE14);

//				if (RoStackConfig.getExtendedDictionary() != null) {
//					
//					logger.info("Stack::Idle extended grammer with dictionary provided");
//					
//					stack.extendGrammar(RoStackConfig.getExtendedDictionary());
//				} else {
					stack.extendGrammar(roFactory.getRoDictionary());
				//}

				RoMessageFactoryImpl.setDiameterRoMsgFactory(roFactory);

				/**
				 * Instantiate one provider with the application name matching
				 * the one configured in the xml dictionary, and attach to a
				 * listener (for example this) so that it receives all incoming
				 * messages.
				 */
				stackProperties = new Properties();
				stackProperties.setProperty("jarFileName", "mjds3gppro.jar");
				stackProperties.setProperty("interfaceName", "ro");
				stackProperties.setProperty("vendorId", "3gpp");
				stackProperties.setProperty("className", "DiameterRoProvider");
				
				if (RoStackConfig.isStateless() == true) {
					logger.info("Stack::Idle RoProvider createe stateless  provider");
					stackProperties.setProperty("fsmName", "RFC_SRV_STATELESS");// RFC_SRV
				} else {
					logger.info("Stack::Idle RoProvider createe stateful  provider");
					stackProperties.setProperty("fsmName", "RFC_SRV");
				}

				DiameterProvider tmpProvider = stack.createDiameterProvider(
						"Ro", stackProperties);

				if (tmpProvider instanceof DiameterRoProvider) {
					roProvider = (DiameterRoProvider) tmpProvider;
				} else {
					// error to be managed
				}
				
                  if (RoStackConfig.getExtendedDictionary() != null) {
					
					logger.info("Stack::Idle extended provider grammer with dictionary provided");
					
					roProvider.extendGrammar(RoStackConfig.getExtendedDictionary());
				}

				roProvider.setDiameterRoListener(this);

				logger.info("Stack::Idle RoProvider created is " + roProvider);
				
				/**
				 * Indicate that this example implements a provider Manager, so
				 * Stack management call-backs will be called for this provider.
				 * 
				 */
				roProvider.setDiameterProviderManager(this);
			} catch (DiameterException e) {
				logger.error("Exception caught: " + e);
			}

			/**
			 * Go to Initializing state.
			 */
			
			logger.info("Stack::Idle(going to call eventInit)");
			stack.eventInit();
		} else {
			// EVENTDIE was called
			stack.stop();
		}
		stack.eventInit();
	}

	@Override
	public void ImmediateStopping(int arg0, int arg1, int arg2) {
		logger.info("ImmediateStopping Diameter stack...");
		clientInterface.stop();
	}

	@Override
	/**
	 * Diameter Stack management callbacks that have to be implemented. These
	 * callbacks implements Provider level call-backs
	 * 
	 */
	public void Idle(int aDate, int aStatus, int lastStackEvent,
			int lastProviderEvent) {
		logger.info("Provider::Idle(" + aDate + "," + aStatus
				+ "," + lastStackEvent + "," + lastProviderEvent + ")");

		/**
		 * provider.eventInit() can be called here to change state of provider.
		 * This can be used to change locally the state of provider without
		 * changing th state of the stack.
		 * 
		 */
		roProvider.eventInit();
	}

	public void Initializing(int aDate, int aStatus, int lastStackEvent,
			int lastProviderEvent) {
		logger.info("Provider::Initializing(" + aDate + ","
				+ aStatus + "," + lastStackEvent + "," + lastProviderEvent
				+ ")");

		/**
		 * provider.eventStart() can be called here to change state of provider.
		 * This can be used to change locally the state of provider without
		 * changing th state of the stack.
		 * 
		 */
		if ((lastProviderEvent == DiameterStack.EVENTINIT)) {
		   roProvider.eventStart();
		}
	}
	
	public void Initializing(int aDate, int aStatus, int lastStackEvent) {
		logger.info("Stack::Initializing(" + aDate + "," + aStatus + ","
				+ lastStackEvent + ")");

		/**
		 * Create a listening point for incoming connections in server mode
		 */
		if ((lastStackEvent == DiameterStack.EVENTINIT)) {

			listeningPoints = RoStackConfig.getListeningPoints();
			if (listeningPoints.length() > 0) {

				logger.info("Creating listening point from  " + listeningPoints);
				try {

					StringTokenizer st = new StringTokenizer(listeningPoints,
							",");
					while (st.hasMoreTokens()) {
						String localURI = st.nextToken();
						logger.info("Creating listening point for local URI "
								+ localURI);
						stack.createDiameterListeningPoint(localURI);
					}

				} catch (DiameterException e) {
					logger.error("Unable to create listening point.---->" +e);
				}
			}
		}
		/**
		 * Switch to running state.
		 */
		logger.info("Stack::Initializing( calling eventStart()");
		stack.eventStart();
	}


	public void Running(int aDate, int aStatus, int lastStackEvent) {
		logger.info("Stack::Running(" + aDate + "," + aStatus
				+ "," + lastStackEvent + ")");
		/**
		 * Stop after nbMsgMax request or answers
		 */
//		if ((nbAnswers >= nbMsgMax) || (nbRequests >= nbMsgMax)) {
//			stopTime = aDate; // stop the time of stop.3
//			synchronized (this) {
//				isRunning = false;
//			}
//		     try {
//		         /* Final cleanup before exit */
//		             logger.info(Level.INFO,"Deleting all Diameter routes...");
//		             /* We use a do/while loop because the iterator cannot be
//		                re-used once deleteDiameterRoute() has been called.
//		                Otherwise, a ConcurrentModification exception is
//		                thrown. */
//		             Iterator it;
//		             do {
//		                 it = stack.getDiameterRoutes();
//		                 if (it.hasNext()) {
//		                     DiameterRoute route = (DiameterRoute)it.next();
//		                     stack.deleteDiameterRoute(route);
//		                     route = null;
//		                 }
//		             } while (it.hasNext());
//
//		     } catch (DiameterException e) {
//		    	 tracer.println(Level.INFO,"ERROR: Exception caught: " +
//		                 e.toString());
//		     }
//			stack.eventGracefulStop("DIAMETER_TOO_BUSY", "Stack is stopping");
//		} else {
			synchronized (this) {
				isRunning=true;
				
				logger.info("Stack::Running( stackStarted and running");
				stackStarted = true;
			}
		//}
	}

	@Override
	public void Waiting(int aDate, int aStatus, int lastStackEvent) {
		logger.info("Stack::Waiting(" + aDate + "," + aStatus
				+ "," + lastStackEvent + ")");
		stack.eventClose();
		
	}
	
	
	public DiameterStack getDiameterStack(){
		return stack;
	}
	
	public DiameterStack getClientInterfaceDiameterStack(){
		if(logger.isDebugEnabled()){
			logger.debug("getClientInterfaceDiameterStack ..");
		}
		return clientInterface.getDiameterStack();
		
	}
	
	public DiameterRoMessageFactory getDiameterRoMsgFactory(){
		return stack.getDiameterRoMessageFactory(DiameterStack.RELEASE13);
	}

	public DiameterRoMessageFactory getDiameterClientIfoMsgFactory(){
		
		if(logger.isDebugEnabled()){
			logger.debug("getDiameterClientIfoMsgFactory ..");
		}
		return clientInterface.getDiameterRoMsgFactory();
	}
	
	public void GracefulStopping(int aDate, int aStatus, int lastStackEvent,
			int lastProviderEvent) {
		logger.info("Provider::GracefulStopping(" + aDate + ","
				+ aStatus + "," + lastStackEvent + "," + lastProviderEvent
				+ ")");

		/**
		 * provider.eventImmediateStop() can be called here to change state of
		 * provider. This can be used to change the state of provider to
		 * Immediate Stopping state.
		 * 
		 */
	}

	
	@Override
	public void ImmediateStopping(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	public void Running(int aDate, int aStatus, int lastStackEvent,
			int lastProviderEvent) {
	logger.info("Provider::Running(" + aDate + "," + aStatus
				+ "," + lastStackEvent + "," + lastProviderEvent + ")");

		/**
		 * Wait for the provider to be in running state to create route to new
		 * peers, in order to have our application present in CER/CEA exchange.
		 * 
		 */
		if ((lastStackEvent == DiameterStack.EVENTSTART)
				&& (lastProviderEvent == DiameterRoProvider.EVENTSTART)) {
			/**
			 * Routes are provided with the command-line and stored using
			 * iterator. Thus, we have to scan the iterator and configure the
			 * corresponding route objects in the Diameter Stack.
			 * 
			 * Route configuration is not mandatory for server process, if
			 * isUnknownPeerAuthorized() callback is implemented in the listener
			 * (see the isUnknownPeerAuthorized() routine for details).
			 */
   if(!isclientMode){
			remotePeerRoutes = RoStackConfig.getRemotePeerRoutes();

			logger.info("Provider::Running(createDiameterRoute with remotePeerRoutes "+ remotePeerRoutes);
			
		
			if (remotePeerRoutes != null && !remotePeerRoutes.isEmpty()) {
				
				StringTokenizer st = new StringTokenizer(remotePeerRoutes, ",");
				// "m=1;aaa://servera.traffix.com:3868,m=2;aaa://serverb.traffix.com:3869"

				try {

					while (st.hasMoreTokens()) {

						String route = st.nextToken();

						logger.info("Provider::Running(createDiameterRoute) with route "
										+ route);
						StringTokenizer realmAndURI = new StringTokenizer(route, ";");

						while (realmAndURI.hasMoreTokens()) {

							String priorityStr=realmAndURI.nextToken();
							String[] priorityVal=priorityStr.split("=");
							int priority= Integer.parseInt(priorityVal[1]);
							String destRlm = realmAndURI.nextToken();
							String destHostURI = realmAndURI.nextToken();
                            String transport="tcp";
							
							if(realmAndURI.hasMoreTokens()){
						     String transp=realmAndURI.nextToken();
						     
						     if(transp.indexOf("transport=")!=-1){
							
						    	 String[] transportStr=transp.split("=");
						    	 transport=transportStr[1];
						    	 
						    	 logger.info("transport set is  "
											+ transport);
							}
							}

						     destHostURI=destHostURI+";transport="+transport;
							logger.info("Provider::Running(createDiameterRoute with destRealm  and URI as "
											+ destRlm
											+ " uri "
											+ destHostURI
											+ " priority " + priority +"  DiameterRoute.LocalAction.LOCAL,DiameterRoute.RouteTo.CLIENT  fr.marben.diameter.INITIATE_CONNECTION--> false");
							
							Properties p=new Properties();
							p.setProperty("fr.marben.diameter.INITIATE_CONNECTION", "false");
							stack.createDiameterRoute("Ro", destRlm,
									destHostURI, priority,DiameterRoute.LocalAction.LOCAL,DiameterRoute.RouteTo.CLIENT,p);
						}
					}

					routeAdded = true;
				} catch (DiameterException e) {
					logger.error("ERROR: Exception caught: " + e.toString());
					e.printStackTrace();
				}
			}
		}
		}
	}

	@Override
	public void Waiting(int arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * ProcessNotification method shared by both client and server code. As a
	 * DiameterNotificationListener, it is mandatory to implement this method
	 * (inherited from the DiameterNotificationListener interface).
	 * 
	 * This method receives some asynchronous events from the Diameter stack,
	 * including network errors, status changes, and so on.
	 * 
	 * The same comment as processEvent about synchronous processing is
	 * applicable to this method (see previous method).
	 */
	public void processNotification(DiameterNotification notification) {
		if (notification instanceof DiameterPeerStateChange) {

			DiameterPeerStateChange peerStateChange = (DiameterPeerStateChange) notification;
			logger.info(
					"Notification PeerStateChange FQDN "
							+ peerStateChange.getFQDN() + " URI "
							+ peerStateChange.getURI() + " State "
							+ peerStateChange.getState());
			
			if(peerStateChange.getState().equals("OPEN")){
				logger.info(
						"Notification peer is connected  ");
			}else if(peerStateChange.getState().equals("CLOSED")){
				logger.info(
						"Notification peer is dsconnected  ");
			}

		} else {
			logger.info(
					"Notification " + notification.toString());
		}
	}

	/**
	 * Process incoming CCA (client callback).
	 * 
	 * @see fr.marben.diameter._3gpp.ro.DiameterRoListener#
	 *      creditControlAnswerReceived(java.lang.String, int, java.lang.String,
	 *      int, fr.marben.diameter.DiameterMessageEvent)
	 */
	public void creditControlAnswerReceived(String resultCode,
			String CCRequestType, Long requestNumber,
			DiameterMessageEvent msgEvent) {
		
		
		logger.error("Server donot Receive CCA : ");
	}


	@Override
	public void creditControlRequestReceived(java.lang.String destinationRealm,
            java.lang.String serviceContextId,
            java.lang.String CCRequestType,
            java.lang.Long cCRequestNumber,
            DiameterMessageEvent msgEvent) {
		
   try{		
		DiameterMessage request = msgEvent.getDiameterMessage();
		logger.info("Received CCR:" + request.toString());

		/*
		 * Get existing Session object if it exists. (here we continue
		 * computing an ongoing stateful session)
		 */
		
		logger.info("creditControlRequestReceived Session id " +request.getSessionIdAVPValue()); 
		
		DiameterSession session = msgEvent.getDiameterSession();//roProvider.createServerDiameterSession(request);//
		
		/* Otherwise, the session is new on the server side */
		if (session == null) {
			
			logger.info("create New Session as incoming session is null "); 
			session = roProvider.createServerDiameterSession(request);
		}
		
		String nextHop = msgEvent.getSenderOriginHost();
		
			if (!validateMandatoryAVPs(request)) {

				DiameterMessage answer = roFactory.createCreditControlAnswer(
						ResultCodes.getReturnCode(5005), request);

				sendErrorCode(answer,session,nextHop);
				
				logger.error("missing mandatory avp send error code 5005 ");
				return;
			}
			
			if(!validateAuthAppId(request)){
				DiameterMessage answer = roFactory.createCreditControlAnswer(
						ResultCodes.getReturnCode(3007), request);

				sendErrorCode(answer,session,nextHop);
				
				logger.error("missing mandatory avp send error code 3007 "); 
				return;
			}

//		DiameterMessage answer = roFactory.createCreditControlAnswer(
//				"DIAMETER_SUCCESS", request);
////			
//		/* Send the message through the session */
//		logger.info("Sending CCA afrer sleeping for 1 sec: " + answer.toString());
//		try {
//			Thread.currentThread().sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		session.sendMessage(answer);
	//	answer = null;

		clientInterface.handleIncomingCCR(session, request,nextHop);
		
		/*
		 * Delete the session when we know it is no longer needed on the
		 * server side
		 */

		if ((CCRequestType.equals(DiameterRoMessageFactory.TERMINATION_REQUEST))
				|| (CCRequestType.equals(DiameterRoMessageFactory.EVENT_REQUEST))) {
		//	session.delete();
			session = null;
		}
//		/* Display and update statistics. */
//		synchronized (this) {
//			nbAnswers++;
//		}
//		if ((nbAnswers % 100) == 0) {
//			tracer.println(Level.INFO, nbAnswers + " CCA sent.");
//		}

	} catch (DiameterException e) {
		logger.error("Exception caught in "
				+ "creditControlRequestReceived: " + e.toString());
	}
	
		
		
	}
	
	
	/**
	 * This metod is called to send an CCA error code
	 * @param answer
	 * @param session
	 */
	   public void sendErrorCode(DiameterMessage answer,DiameterSession session,String nextHop){
		   if (RoStackConfig.isStateless()) {
				if (logger.isDebugEnabled()) {
					logger.debug("send response on ro provider on sender orin host");
				}
				try {
					RoStackInterfaceImpl.roProvider.sendMessage(nextHop ,answer);
				} catch (DiameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//request.getStatelessSenderOriginHost(),

			} else {
				
				if (logger.isDebugEnabled()) {
					logger.debug("send response on ro provider on Session");
				}
				try {
					session.sendMessage(
								answer);
				} catch (DiameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				logger.debug("CreditControlAnswer sent successfully sesion deleted");
				session.delete();
			}
	   }
	
	
	boolean validateMandatoryAVPs(DiameterMessage request) {

		if (logger.isDebugEnabled()) {
			logger.debug("validateMandatoryAVPs ");
		}
		boolean valid = true;
		if (request.getAVP(AvpCodes.Auth_Application_id) == null) {
			logger.error("missing Mandatory Auth_Application_id");
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("validateMandatoryAVPs leaving "+valid);
		}
		return valid;
	}
	
	private boolean validateAuthAppId(DiameterMessage request) {


		if (logger.isDebugEnabled()) {
			logger.debug("validateAuthAppId ");
		}
		boolean valid = true;
		ArrayList<DiameterAVP> avplist = request
				.getAVP(AvpCodes.Auth_Application_id);

		DiameterAVP avp = avplist.get(0);

		DiameterUnsigned32AVP authid = (DiameterUnsigned32AVP) avp;
		if (logger.isDebugEnabled()) {
			logger.debug("Auth_Application_id " + authid.getValue());
		}
		if (authid.getValue() != authApplicationId) {
			return false;
		}

		return valid;

	}

	@Override
	public void reAuthAnswerReceived(String arg0, DiameterMessageEvent arg1) {

		if(logger.isDebugEnabled()){
			logger.debug("reAuthAnswerReceived ..");
		}
		
	}

	@Override
	public void reAuthRequestReceived(String arg0, String arg1, String arg2,
			DiameterMessageEvent arg3) {
		// TODO Auto-generated method stub
		
		if(logger.isDebugEnabled()){
			logger.debug("reAuthRequestReceived ..");
		}
		
	}

	@Override
	public DiameterMessage updateCapabilityExchange(DiameterMessage msg,
			DiameterPeer peer) {
		// TODO Auto-generated method stub
		
	//	ArrayList<DiameterAVP> securityId = msg.getAVP(AvpCodes.Inband_security_id);//"InbandSecurityID");
		
		DiameterAVP avp = stack.getDiameterMessageFactory()
				.createUnsigned32AVP(
						"Auth-Application-Id",
						"base",4);
		msg.add(avp);
		
		if(logger.isDebugEnabled()){
			logger.debug("updateCapabilityExchange ..with Auth-Application-Id");
		}
		return  msg;
	}

}

