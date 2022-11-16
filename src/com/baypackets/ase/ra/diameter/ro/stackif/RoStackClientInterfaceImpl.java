package com.baypackets.ase.ra.diameter.ro.stackif;

import java.io.File;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.common.exception.ValidationException;
import com.baypackets.ase.ra.diameter.ro.RoResourceException;
import com.baypackets.ase.ra.diameter.ro.enums.CCRequestTypeEnum;
import com.baypackets.ase.ra.diameter.ro.enums.RequestedActionEnum;
import com.baypackets.ase.ra.diameter.ro.impl.RoMessageFactoryImpl;
import com.baypackets.ase.ra.diameter.ro.impl.RoResourceAdaptorFactory;
import com.baypackets.ase.ra.diameter.ro.impl.RoSession;
import com.baypackets.ase.ra.diameter.ro.utils.AvpCodes;
import com.baypackets.ase.ra.diameter.ro.utils.Constants;
import com.baypackets.ase.ra.diameter.ro.utils.ResultCodes;
import com.baypackets.ase.ra.diameter.ro.utils.RoStackConfig;
import com.baypackets.ase.ra.diameter.ro.CreditControlAnswer;
import com.baypackets.ase.ra.diameter.ro.CreditControlRequest;
import com.baypackets.ase.ra.diameter.ro.RoMessage;
import com.baypackets.ase.ra.diameter.ro.RoRequest;
import com.baypackets.ase.ra.diameter.ro.RoResourceEvent;
import com.baypackets.ase.ra.diameter.ro.RoResourceFactory;
import com.baypackets.ase.ra.diameter.ro.RoResponse;
import com.baypackets.ase.ra.diameter.ro.RoResourceAdaptor;
import com.baypackets.ase.ra.diameter.ro.RoStackInterface;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.util.TimerService;
import com.baypackets.ase.util.AseAlarmService;

import fr.marben.diameter.*;
import fr.marben.diameter.DiameterRoute.RouteState;
//import com.traffix.openblox.core.enums.ApplicationType;
//import com.traffix.openblox.core.enums.Standard;
//import com.traffix.openblox.core.exceptions.ValidationException;
//import com.traffix.openblox.core.fsm.stack.StackState;
//import com.traffix.openblox.core.session.SessionFactory;
//import com.traffix.openblox.core.transport.Peer;
//import com.traffix.openblox.core.transport.PeerStateListener;
//import com.traffix.openblox.core.transport.PeerTable;
//import com.traffix.openblox.core.transport.PeerTableListener;
//import com.traffix.openblox.core.transport.Stack;
//import com.traffix.openblox.core.transport.TransportStack;
//import com.traffix.openblox.core.utils.configuration.Configuration;
//import com.traffix.openblox.core.utils.configuration.ConfigurationType;
//import com.traffix.openblox.core.utils.configuration.MutableConfigurationImpl;
//import com.traffix.openblox.diameter.coding.DiameterAnswer;
//import com.traffix.openblox.diameter.coding.DiameterRequest;
//import com.traffix.openblox.diameter.enums.PcbState;
//import com.traffix.openblox.diameter.rf.generated.event.MessageASA;
//import com.traffix.openblox.diameter.ro.generated.RoStandardLoader;
//import com.traffix.openblox.diameter.ro.generated.enums.EnumCCRequestType;
//import com.traffix.openblox.diameter.ro.generated.enums.EnumRequestedAction;
//import com.traffix.openblox.diameter.ro.generated.event.MessageCCR;
//import com.traffix.openblox.diameter.ro.generated.session.SessionRoServer;
//import com.traffix.openblox.diameter.session.DiameterSession;
//import com.traffix.openblox.diameter.utils.DiameterApplicationId;
import fr.marben.diameter._3gpp.ro.DiameterRoMessageFactory;
import fr.marben.diameter._3gpp.ro.DiameterRoProvider;
import fr.marben.diameter._3gpp.ro.DiameterRoR14Listener;


// Referenced classes of package com.baypackets.ase.ra.diameter.sh.stackif:
//            SessionListenerFactoryShServerImpl, ShProfileUpdateRequestImpl, ShUserDataRequestImpl

public class RoStackClientInterfaceImpl extends Thread implements
DiameterNotificationListener,DiameterStackManager, DiameterProviderManager,DiameterRoR14Listener,Constants {

	private static Logger logger = Logger.getLogger(RoStackClientInterfaceImpl.class);
	private static RoResourceFactory raFactory;
	private static TimerService timerService;
	private static long timeout = 30L;
	private int handleCount;

	public DiameterPeer serverPeer;
	//public static String serverRealm;
	public static String serverHost;
	private String configFile;
	//public static final DiameterApplicationId applicationId;
	private RoResourceAdaptor ra;
	private Map outgoingRequests;
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
	private String localClientRealm;
	private List destRealm;
	private String localhost;
	private String remotePeerRoutes;
	public static String serverContextId;
	private Properties stackProperties;
	private DiameterStack stack;
	private DiameterRoMessageFactory roFactory;
	public static DiameterRoProvider roProvider;
	private boolean stackStarted;
	boolean isAlive = true;
	boolean isRunning = false;
	
	boolean routeAdded = false;
	private long loop_delay=1000;

	private DiameterRoute serverRoute;
	private String extendedDictionary;

	public RoStackClientInterfaceImpl(RoResourceAdaptor ra)
	{
		handleCount = 0;
		this.ra = ra;
		outgoingRequests = new Hashtable(0x10000);
	}

	public void init(ResourceContext context) throws RoResourceException
	{
		alarmService = (AseAlarmService)context.getAlarmService();
		raFactory = (RoResourceFactory)context.getResourceFactory();
		timerService = context.getTimerService();

		logger.debug("Initialize measurement counters.");
		MeasurementManager measurementMgr = context.getMeasurementManager();

		this.ccrEventCnt = measurementMgr.getMeasurementCounter(CCR_EVENT_COUNTER_IN);
		this.ccrSessionCnt = measurementMgr.getMeasurementCounter(CCR_SESSION_COUNTER_IN);

		this.ccrDirectDebitCnt = measurementMgr.getMeasurementCounter(CCR_DIRECT_DEBIT_COUNTER_IN);
		this.ccrAccountRefundCnt = measurementMgr.getMeasurementCounter(CCR_ACCOUNT_REFUND_COUNTER_IN);
		this.ccrBalanceCheckCnt = measurementMgr.getMeasurementCounter(CCR_BALANCE_CHECK_COUNTER_IN);
		this.ccrPriceEnquiryCnt = measurementMgr.getMeasurementCounter(CCR_PRICE_ENQUIRY_COUNTER_IN);
		this.ccrFirstInteroCnt = measurementMgr.getMeasurementCounter(CCR_FIRST_INTEROGATION_COUNTER_IN);
		this.ccrInterimInteroCnt = measurementMgr.getMeasurementCounter(CCR_INTERIM_INTEROGATION_COUNTER_IN);
		this.ccrFinalInteroCnt = measurementMgr.getMeasurementCounter(CCR_FINAL_INTEROGATION_COUNTER_IN);

		this.ccaEvent1xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_1XXX_COUNTER_OUT);
		this.ccaEvent2xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_2XXX_COUNTER_OUT);
		this.ccaEvent3xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_3XXX_COUNTER_OUT);
		this.ccaEvent4xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_4XXX_COUNTER_OUT);
		this.ccaEvent5xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_5XXX_COUNTER_OUT);

		this.ccaSession1xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_1XXX_COUNTER_OUT);
		this.ccaSession2xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_2XXX_COUNTER_OUT);
		this.ccaSession3xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_3XXX_COUNTER_OUT);
		this.ccaSession4xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_4XXX_COUNTER_OUT);
		this.ccaSession5xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_5XXX_COUNTER_OUT);

		this.ccrSendErrorCnt = measurementMgr.getMeasurementCounter(CCR_SEND_ERROR);

		
		this.configFile = (String)context.getConfigProperty("ase.home") + File.separator + 
				"conf" + File.separator + "diameter_ro.yml";
		this.extendedDictionary = (String)context.getConfigProperty("ase.home") + File.separator + 
				"conf" + File.separator + "diameter_ro_ext_dictionary.xml";
		logger.debug((new StringBuilder()).append("Use [").append(configFile).append("]").toString());
		init(configFile);

	}

	public void init(String cfgFile) throws RoResourceException
	{
		try
		{
			this.configFile = cfgFile;

			logger.debug("creating transport stack");
			
			RoStackConfig.loadconfiguration(this.configFile,this.extendedDictionary);
			localClientRealm=RoStackConfig.getLocalClientRealm();
			destRealm=RoStackConfig.getDestRealm();
			localhost=RoStackConfig.getLocalClientFQDN();
			remotePeerRoutes=RoStackConfig.getRemotePeerRoutes();

			serverContextId=RoStackConfig.getServiceContextId();//"roContext@agnity.com";			
			 
			stackProperties= new Properties();
			/**
			 * To enable stack management the property ENABLE_STACKMANAGEMENT has to
			 * be set to true
			 */
			stackProperties.setProperty("fr.marben.diameter.ENABLE_STACKMANAGEMENT",
					"true");
			 stackProperties.setProperty("ENABLE_RB_ROUTE_ALGORITHM","true");
			
//			logger.debug("createDiameterStack  with realm "+localClientRealm +" FQDN " +localhost);
//			
//			DiameterFactory factory =DiameterFactory.getInstance();
//			stack = factory.createDiameterStack(localClientRealm,
//					localhost, stackProperties);
//			
//				logger.info("Client starting");
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
//			logger.debug("serverStack loaded successfully");
		}
		catch(Exception ex)
		{
			logger.error("RoResourceFactory.init() failed.");
			logger.error(ex.getMessage(), ex);
			throw new RoResourceException("RoResourceFactory.init() failed.");
		}
	}
	
	// Example shutdown hook class
	class DiameterRoR14ServerShutdown extends Thread {
		private RoStackClientInterfaceImpl server = null;

		public void run() {

			System.err.println("DiameterRoR14ServerShutdown hook called");
			if (server.getDiameterStack() != null) {
				System.err.println("Stopping the stack...");
				server.shutdown();
				server.getDiameterStack() .stop();
			}
		}

		DiameterRoR14ServerShutdown(RoStackClientInterfaceImpl srv) {
			this.server = srv;
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

	public void startStack() throws RoResourceException {
		logger.debug("Inside RoStackInterfaceImpl start()");

		try {
			logger.debug("createDiameterStack  with realm " + localClientRealm
					+ " FQDN " + localhost);

			DiameterFactory factory = DiameterFactory.getInstance();
			stack = factory.createDiameterStack(localClientRealm, localhost,
					stackProperties);

			logger.info("Client starting");

			logger.debug("initializing transport stack");

			/**
			 * Register shutdown hook
			 */
			DiameterRoR14ServerShutdown ro = new DiameterRoR14ServerShutdown(
					this);
			Runtime.getRuntime().addShutdownHook(ro);

			/**
			 * Set this class as an implementation of a diameter stack manager
			 */
			stack.setDiameterStackManager(this);

			/**
			 * Register our application as a DiameterNotificationListener,so
			 * that we receive all the notifications from the Diameter stack.
			 */
			stack.setDiameterNotificationListener(this);

			logger.debug("stack loaded successfully" + stack);
			logger.debug("serverStack loaded successfully");
			this.start();
		} catch (Exception ex) {
			logger.error("startStack.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new RoResourceException("startStack failed.");
		}
	}

	public void stopStack() throws RoResourceException
	{
		logger.info("stop(): closing Diameter stack...");

		try
		{
			
			logger.debug("stop(): initialize Marben stack.");
			
			//	StackState state = 
		    stack.stop();//stop(5, TimeUnit.SECONDS);
		    isAlive=false;
		    isRunning=false;
		}

		catch(Exception ex)
		{
			logger.error("RoResourceFactory.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new RoResourceException("RoResourceFactory.init() failed.");
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
				logger.error("InterruptedException  "+e);
			}
		}

		try {
			/* Final cleanup before exit */

			{
				logger.info("Removing the provider...");
				stack.deleteDiameterProvider(roProvider);
				roProvider = null;
			}
			  Iterator it;
            do {
            it = stack.getDiameterRoutes();
            if (it.hasNext()) {
                DiameterRoute route = (DiameterRoute)it.next();
                stack.deleteDiameterRoute(route);
                route = null;
            }
        } while (it.hasNext());
			{
				logger.info("Deleting all routes...");
		
			}
			logger.info("Stopping the stack...");
			stack.stop();

		} catch (DiameterException e) {
			logger.error("Exception caught: " + e);
		}
	}
	

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



	private synchronized int getNextHandle()
	{
		return handleCount++;
	}

	public void addRequestToMap(int handle, RoRequest request)
	{
		logger.debug((new StringBuilder()).append("request map size before adding is :[ ").append(outgoingRequests.size()).append("]").toString());
		outgoingRequests.put(new Integer(handle), request);
		logger.debug((new StringBuilder()).append("request map size after adding is :[ ").append(outgoingRequests.size()).append("]").toString());
	}

//	public void receivedErrorMessageServerMode(DiameterSession diametersession, DiameterRequest diameterrequest, DiameterAnswer diameteranswer)
//	{
//		//TODO
//	}
//
//	public void timeoutExpiredServerMode(DiameterSession diametersession, DiameterRequest diameterrequest)
//	{
//		//TODO
//	}

	public void handleRequest(RoRequest request) throws RoResourceException {		

		logger.debug("handleRequest(RoRequest) called ");
	}

	public void handleResponse(RoResponse response) throws RoResourceException {

		boolean sentSuccessfully = true;
		long handleTimestamp=System.currentTimeMillis();
		if (response == null) {
			logger.error("handleResponse(): null response.");
			return;
		}

		try {

			if (response instanceof RoAbstractResponse) {

				CCRequestTypeEnum recordType = ((CreditControlAnswer)response).getEnumCCRequestType();
				String resultCode = ((CreditControlAnswer)response).getResultCode();
				
				logger.debug("handleResponse with resultcode "+ resultCode);
				
				switch (recordType){

//				case EVENT_REQUEST:
//					this.updateResponseCounter(EVENT, (int) resultCode);
//					break;
//				case INITIAL_REQUEST:
//					this.updateResponseCounter(SESSION, (int) resultCode);
//					break;
//				case UPDATE_REQUEST:
//					this.updateResponseCounter(SESSION, (int) resultCode);
//					break;
//				case TERMINATION_REQUEST:
//					this.updateResponseCounter(SESSION, (int) resultCode);
//					break;
//				default:
//					logger.error("Wrong/Unkown type request received.");
//					throw new ResourceException("Wrong/Unkown response type.");

				}

				CreditControlRequestImpl request=(CreditControlRequestImpl)((CreditControlAnswerImpl)response).getRequest();
				
				if(logger.isDebugEnabled()){
				logger.debug("CreditControlAnswer incoming CCR server session id is "+ request.getSessionId());
				}
				RoSession rosession=(RoSession)request.getProtocolSession();
				long timestamp=request.getTimestamp();
				// Added processing ts in RoSession
				if(rosession!=null){
					rosession.addTimestamp(request.getType(), handleTimestamp-timestamp);
				rosession.removeRequest(request);
				}
				
				if (RoStackConfig.isStateless()) {
					if (logger.isDebugEnabled()) {
						logger.debug("send response on ro provider on Sender host  "
								+ request.getStatelessSenderOriginHost());
					}
					RoStackInterfaceImpl.roProvider.sendMessage(request.getStatelessSenderOriginHost(),((CreditControlAnswerImpl) response).getStackObj());//request.getStatelessSenderOriginHost(),

				} else {
					request.getServerStackSession().sendMessage(
							((CreditControlAnswerImpl) response).getStackObj());
					logger.debug("CreditControlAnswer sent successfully sesion deleted");
					request.getServerStackSession().delete();
				}
				/*
				 * Session no longer needed
				 */
				
				
			} 			
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
	
	public void incrementRequestedActionCounter(DiameterMessage request) throws RoResourceException, ValidationException 
	{
		logger.debug("Inside incrementRequestedActionCounter()");
		ArrayList<DiameterAVP> avpList=request.getAVP(AvpCodes.Requested_Action);
		DiameterInteger32AVP avp=(DiameterInteger32AVP)avpList.get(0);
		int type=avp.getValue();//.getName();
		//.getEnumRequestedAction();
			if(type==RequestedActionEnum.getCode(RequestedActionEnum.DIRECT_DEBITING)){
				this.ccrDirectDebitCnt.increment();
				logger.debug("Requested Action Direct Debiting incrementing Counter");
			}else if(type==RequestedActionEnum.getCode(RequestedActionEnum.REFUND_ACCOUNT)){
				this.ccrAccountRefundCnt.increment();
				logger.debug("Requested Action Account Refund incrementing Counter");
			}else if(type==RequestedActionEnum.getCode(RequestedActionEnum.CHECK_BALANCE)){
				this.ccrBalanceCheckCnt.increment();
				logger.debug("Requested Action Balance Check incrementing Counter");
			}else if(type==RequestedActionEnum.getCode(RequestedActionEnum.PRICE_ENQUIRY)){
				this.ccrPriceEnquiryCnt.increment();
				logger.debug("Requested Action Price Enquiry incrementing Counter");
			}else {
				logger.error("Wrong/Unkown requested Action type.");
				
			}
			logger.debug("incrementRequestedActionCounter() exit");
		}
	
	public void handleIncomingCCR(DiameterSession serverSession, DiameterMessage stackReq, String nextHop)
	{
		logger.debug("Inside handleIncomingCCR.." + serverSession.getSessionId());

		try
		{
			int type = -1;

			ArrayList<DiameterAVP> recordType = stackReq.getAVP(AvpCodes.CC_Request_Type);
			DiameterInteger32AVP avp=(DiameterInteger32AVP)recordType.get(0);
			type=avp.getValue();
		
			switch (type){
             case EVENT_REQUEST:
				logger.debug("EVENT_REQUEST");
				this.ccrEventCnt.increment();
				this.incrementRequestedActionCounter(stackReq);
				type = EVENT_REQUEST;
				break;
			case INITIAL_REQUEST:
				logger.debug("INITIAL_REQUEST");
				this.ccrFirstInteroCnt.increment();
				this.ccrSessionCnt.increment();
				type = INITIAL_REQUEST;
				break;
			case UPDATE_REQUEST:
				logger.debug("UPDATE_REQUEST");
				this.ccrInterimInteroCnt.increment();
				this.ccrSessionCnt.increment();
				type = UPDATE_REQUEST;
				break;
			case TERMINATION_REQUEST:
				logger.debug("TERMINATION_REQUEST");
				this.ccrFinalInteroCnt.increment();
				this.ccrSessionCnt.increment();
				type = TERMINATION_REQUEST;
				break;
			default:
				logger.error("Wrong/Unkown type request received.");
				throw new ResourceException("Wrong/Unkown response type.");
			}

			CreditControlRequestImpl request = new CreditControlRequestImpl(type);
			request.setStatelessSenderOriginHost(nextHop);
			request.setStackObj(stackReq);
			request.setServerStackSession(serverSession);
			ra.deliverRequest(request);

		}
		catch(Exception ex)
		{
			logger.error("handleIncomingCCR() failed: ",ex);
		}
	}

	public void handleIncomingASA(DiameterSession serverSession,
			DiameterMessage answer) {
		// TODO Auto-generated method stub

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
						+ incomingCER.getOriginHostAVPValue());
		if (destRealm!=null && destRealm.contains(incomingCER.getOriginRealmAVPValue())) {
			status = true;
		} else {
			logger.info(
					"isUnknownPeerAuthorized()"
							+ "return false unknown realm: "
							+ incomingCER.getOriginRealmAVPValue());

		}
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
				 * to the Ro RELEASE14 dictionary which is already preloaded in
				 * the Diameter stack.
				 */
				roFactory = stack
						.getDiameterRoMessageFactory(DiameterStack.RELEASE14);

//                if (RoStackConfig.getExtendedDictionary() != null) {
//					
//					logger.info("Stack::Idle extended grammer with dictionary provided");
//					
//					stack.extendGrammar(RoStackConfig.getExtendedDictionary());
	//			} else {
					stack.extendGrammar(roFactory.getRoDictionary());
	//			}

				
				RoMessageFactoryImpl.setDiameterRoMsgFactory(roFactory);
				RoMessageFactoryImpl.setDiameterRoClientStack(stack);

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
					
					stackProperties.setProperty("fsmName", "RFC_CL_STATELESS");// RFC_CL_LESS
					stackProperties.setProperty("storeStatelessData", "true");
				} else {
					logger.info("Stack::Idle RoProvider createe staefull provider");
					stackProperties.setProperty("fsmName", "RFC_CL_LESS");// RFC_CL_LESS
				}
				
//			    stackProperties.setProperty("fr.marben.diameter.ENABLE_STACKMANAGEMENT",
//						"true");
				DiameterProvider tmpProvider = stack.createDiameterProvider(
						"Ro", stackProperties);

				if (tmpProvider instanceof DiameterRoProvider) {
					roProvider = (DiameterRoProvider) tmpProvider;
				} else {
					// error to be managed
				}
				
              if (RoStackConfig.getExtendedDictionary() != null) {
					
					logger.info("Stack::Idle extended grammer with dictionary provided");
					
					roProvider.extendGrammar(RoStackConfig.getExtendedDictionary());
				}
				
				logger.info("Stack::Idle RoProvider created is " + roProvider);

				roProvider.setDiameterRoListener(this);

				/**
				 * Indicate that this example implements a provider Manager, so
				 * Stack management call-backs will be called for this provider.
				 * 
				 */
				roProvider.setDiameterProviderManager(this);
			} catch (DiameterException e) {
				logger.error("Exception caught: " + e);
				e.printStackTrace();
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
		try {
			this.stopStack();
		} catch (RoResourceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		if(logger.isDebugEnabled()){
			logger.debug("getDiameterRoMsgFactory return stack.." +stack);
		}
		return stack;
	}
	
	public DiameterRoMessageFactory getDiameterRoMsgFactory(){
		
		if(logger.isDebugEnabled()){
			logger.debug("getDiameterRoMsgFactory return .." +roFactory);
		}
		return roFactory;
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
												+ " priority " + priority);
								stack.createDiameterRoute("Ro", destRlm,
										destHostURI, priority);
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
		DiameterMessage answer = msgEvent.getDiameterMessage();
		
		
		logger.debug("creditControlAnswerReceived with " + answer);
		try {
			logger.debug("removing request from map");
			
			logger.debug("request map size  :[ "+ RoStackInterfaceImpl.requests.size()+"]");
			CreditControlRequestImpl containerReq = (CreditControlRequestImpl) RoStackInterfaceImpl.requests.remove(answer.getSessionIdAVPValue());
			if (containerReq==null) {
				logger.error("corrosponding requst is not found. returnning");
				return;
			}
			CCRequestTypeEnum type = ((CreditControlRequest)containerReq).getEnumCCRequestType();
			int responseType=0;
			if(type.equals(CCRequestTypeEnum.EVENT_REQUEST)){
				logger.debug("received EVENT_RECORD request");
				this.updateResponseCounter(EVENT, (int) answer.getResultCodeAVP());
				responseType=EVENT_REQUEST;
			}else if(type.equals(CCRequestTypeEnum.INITIAL_REQUEST)){
				logger.debug("received START_RECORD request");
				this.updateResponseCounter(SESSION, (int) answer.getResultCodeAVP());
				responseType=INITIAL_REQUEST;
			}else if(type.equals(CCRequestTypeEnum.UPDATE_REQUEST)){
				logger.debug("received INTERIM_RECORD request");
				this.updateResponseCounter(SESSION, (int) answer.getResultCodeAVP());
				responseType=UPDATE_REQUEST;
			}else if(type.equals(CCRequestTypeEnum.TERMINATION_REQUEST)){
				logger.debug("received STOP_RECORD request");
				this.updateResponseCounter(SESSION, (int) answer.getResultCodeAVP());
				responseType=TERMINATION_REQUEST;
			}else {
				logger.error("Wrong/Unkown type request received.");
				throw new ResourceException("Wrong/Unkown request type.");
			}
			CreditControlAnswerImpl response = new CreditControlAnswerImpl(responseType,containerReq);
			response.setStackObj(answer);
			response.setProtocolSession(containerReq.getProtocolSession());
			logger.debug("devilering CCA");
			this.ra.deliverResponse(response);
			
			
			if ((CCRequestType
					.equals(DiameterRoMessageFactory.TERMINATION_REQUEST))
					|| (CCRequestType
							.equals(DiameterRoMessageFactory.EVENT_REQUEST))) {
				if (RoStackConfig.isStateless()) {
					roProvider.removeStatelessData(answer
							.getSessionIdAVPValue());
				} else {
					msgEvent.getDiameterSession().delete();
				}
			} 
			
			logger.debug("delivered CCA successfully");
		} catch (Exception ex) {
			logger.error("handleIncomingCCA() failed: " + ex);
			logger.error(ex.getMessage(), ex);
		}

		logger.error("Received CCA : " + answer.toString());
	}


	@Override
	public void creditControlRequestReceived(java.lang.String destinationRealm,
            java.lang.String serviceContextId,
            java.lang.String CCRequestType,
            java.lang.Long cCRequestNumber,
            DiameterMessageEvent msgEvent) {
		
		// Client does not receive CreditControlRequest message
		
	}

	@Override
	public void reAuthAnswerReceived(String arg0, DiameterMessageEvent arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reAuthRequestReceived(String arg0, String arg1, String arg2,
			DiameterMessageEvent arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAlternateRoute(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}


}


