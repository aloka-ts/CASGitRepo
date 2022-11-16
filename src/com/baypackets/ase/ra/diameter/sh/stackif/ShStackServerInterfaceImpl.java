package com.baypackets.ase.ra.diameter.sh.stackif;

import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.ra.diameter.sh.*;
import com.baypackets.ase.ra.diameter.sh.impl.ShMessageFactoryImpl;
import com.baypackets.ase.ra.diameter.sh.impl.ShResourceAdaptorFactory;
import com.baypackets.ase.ra.diameter.sh.impl.ShSession;
import com.baypackets.ase.ra.diameter.sh.utils.AvpCodes;
import com.baypackets.ase.ra.diameter.sh.utils.Constants;
import com.baypackets.ase.ra.diameter.sh.utils.ResultCodes;
import com.baypackets.ase.ra.diameter.sh.utils.ShStackConfig;
import com.baypackets.ase.ra.diameter.sh.utils.statistic.ShStatsManager;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.util.TimerService;
import com.baypackets.ase.util.AseAlarmService;
import fr.marben.diameter.*;
import fr.marben.diameter._3gpp.sh.DiameterShMessageFactory;
import fr.marben.diameter._3gpp.sh.DiameterShProvider;
import fr.marben.diameter._3gpp.sh.DiameterShR14Listener;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;

public class ShStackServerInterfaceImpl  extends Thread  implements ShStackInterface,DiameterNotificationListener,
		DiameterStackManager, DiameterProviderManager, DiameterShR14Listener, DiameterCapabilityExchangeListener,Constants {

	private static Logger logger = Logger.getLogger(ShStackServerInterfaceImpl.class);
	private static ShResourceFactory raFactory;
	private static TimerService timerService;
	private static long timeout = 30;
	private int handleCount = 0;
	int loop_delay = 1000;
	public static DiameterStack stack;
	private static boolean stackStarted;
	private static Timer monitorTimer;
	public DiameterPeer serverPeer;
	public static String serverContextId;
	String configFile;
	private ShStackClientInterfaceImpl clientInterface;
	private ShResourceAdaptor ra;
	protected static Map<String,Object> requests;
	public static DiameterShProvider shProvider;
	private DiameterRoute serverRoute;
	private String remotePeerRoutes;
	boolean isAlive = true;
	boolean isRunning = false;
	boolean routeAdded = false;
	private DiameterShMessageFactory shFactory;
	private Properties stackProperties;
	private DiameterRoute route;
	public static  String localhost;
	private String originRealm=null;// for creating listeneing point
	private List<String> destRealm=null;// for creating route
	private String listeningPoints;
	private boolean isclientMode=true;
	private String extendedDictionary=null;
	private long authApplicationId=4;
	private AseAlarmService alarmService;
	private ShStatsManager statsManager;
	private MeasurementCounter udrCnt;
	private MeasurementCounter purCnt;
	private MeasurementCounter snrCnt;
	private MeasurementCounter pnrCnt;
	private MeasurementCounter uda1xxxCnt;
	private MeasurementCounter uda2xxxCnt;
	private MeasurementCounter uda3xxxCnt;
	private MeasurementCounter uda4xxxCnt;
	private MeasurementCounter uda5xxxCnt;
	private MeasurementCounter pua1xxxCnt;
	private MeasurementCounter pua2xxxCnt;
	private MeasurementCounter pua3xxxCnt;
	private MeasurementCounter pua4xxxCnt;
	private MeasurementCounter pua5xxxCnt;
	private MeasurementCounter sna1xxxCnt;
	private MeasurementCounter sna2xxxCnt;
	private MeasurementCounter sna3xxxCnt;
	private MeasurementCounter sna4xxxCnt;
	private MeasurementCounter sna5xxxCnt;
	private MeasurementCounter pna1xxxCnt;
	private MeasurementCounter pna2xxxCnt;
	private MeasurementCounter pna3xxxCnt;
	private MeasurementCounter pna4xxxCnt;
	private MeasurementCounter pna5xxxCnt;
	private MeasurementCounter udrSendErrorCnt;
	private MeasurementCounter purSendErrorCnt;
	private MeasurementCounter snrSendErrorCnt;
	private MeasurementCounter pnrSendErrorCnt;

	public ShStackServerInterfaceImpl(ShResourceAdaptor ra) {
		this.ra = ra;		
		this.requests = new Hashtable(64*1024); // initialize with 64 K entries
		clientInterface = new ShStackClientInterfaceImpl(ra);
	}

	public void init(ResourceContext context) throws ShResourceException {
		alarmService = (AseAlarmService)context.getAlarmService();
		raFactory = (ShResourceFactory)context.getResourceFactory();
		timerService = context.getTimerService();
		logger.debug("Initialize measurement counters.");
		MeasurementManager measurementMgr = context.getMeasurementManager();
		udrCnt = measurementMgr.getMeasurementCounter(Constants.UDR_COUNTER_OUT);
		purCnt = measurementMgr.getMeasurementCounter(Constants.PUR_COUNTER_OUT);
		snrCnt = measurementMgr.getMeasurementCounter(Constants.SNR_COUNTER_OUT);
		pnrCnt = measurementMgr.getMeasurementCounter(Constants.PNR_COUNTER_OUT);
		uda1xxxCnt = measurementMgr.getMeasurementCounter(Constants.UDA_1xxx_COUNTER_IN);
		uda2xxxCnt = measurementMgr.getMeasurementCounter(Constants.UDA_2xxx_COUNTER_IN);
		uda3xxxCnt = measurementMgr.getMeasurementCounter(Constants.UDA_3xxx_COUNTER_IN);
		uda4xxxCnt = measurementMgr.getMeasurementCounter(Constants.UDA_4xxx_COUNTER_IN);
		uda5xxxCnt = measurementMgr.getMeasurementCounter(Constants.UDA_5xxx_COUNTER_IN);
		pua1xxxCnt = measurementMgr.getMeasurementCounter(Constants.PUA_1xxx_COUNTER_IN);
		pua2xxxCnt = measurementMgr.getMeasurementCounter(Constants.PUA_2xxx_COUNTER_IN);
		pua3xxxCnt = measurementMgr.getMeasurementCounter(Constants.PUA_3xxx_COUNTER_IN);
		pua4xxxCnt = measurementMgr.getMeasurementCounter(Constants.PUA_4xxx_COUNTER_IN);
		pua5xxxCnt = measurementMgr.getMeasurementCounter(Constants.PUA_5xxx_COUNTER_IN);
		sna1xxxCnt = measurementMgr.getMeasurementCounter(Constants.SNA_1xxx_COUNTER_IN);
		sna2xxxCnt = measurementMgr.getMeasurementCounter(Constants.SNA_2xxx_COUNTER_IN);
		sna3xxxCnt = measurementMgr.getMeasurementCounter(Constants.SNA_3xxx_COUNTER_IN);
		sna4xxxCnt = measurementMgr.getMeasurementCounter(Constants.SNA_4xxx_COUNTER_IN);
		sna5xxxCnt = measurementMgr.getMeasurementCounter(Constants.SNA_5xxx_COUNTER_IN);
		pna1xxxCnt = measurementMgr.getMeasurementCounter(Constants.PNA_1xxx_COUNTER_OUT);
		pna2xxxCnt = measurementMgr.getMeasurementCounter(Constants.PNA_2xxx_COUNTER_OUT);
		pna3xxxCnt = measurementMgr.getMeasurementCounter(Constants.PNA_3xxx_COUNTER_OUT);
		pna4xxxCnt = measurementMgr.getMeasurementCounter(Constants.PNA_4xxx_COUNTER_OUT);
		pna5xxxCnt = measurementMgr.getMeasurementCounter(Constants.PNA_5xxx_COUNTER_OUT);
		udrSendErrorCnt = measurementMgr.getMeasurementCounter(Constants.UDR_ERROR_COUNTER_OUT);
		snrSendErrorCnt = measurementMgr.getMeasurementCounter(Constants.SNR_ERROR_COUNTER_OUT);
		pnrSendErrorCnt = measurementMgr.getMeasurementCounter(Constants.PNR_ERROR_COUNTER_OUT);
		purSendErrorCnt = measurementMgr.getMeasurementCounter(Constants.PUR_ERROR_COUNTER_OUT);

		this.configFile = context.getConfigProperty("ase.home") + File.separator +
				"conf" + File.separator + "diameter_sh.yml";
		this.extendedDictionary = (String)context.getConfigProperty("ase.home") + File.separator +
				"conf" + File.separator + "diameter_sh_ext_dictionary.xml";
		logger.debug("Use [" + this.configFile + "] and extended dictionary is exits [" + this.extendedDictionary + "]");
		statsManager=new ShStatsManager(measurementMgr);
		init(configFile);
		clientInterface.init(context);
	}

	public void init(String cfgFile) throws ShResourceException {
		try {
			ShStackServerInterfaceImpl.raFactory = ShResourceAdaptorFactory.getResourceFactory();
			this.configFile = cfgFile;
			logger.debug("creating transport stack");
			ShStackConfig.loadconfiguration(this.configFile,this.extendedDictionary);
			originRealm=ShStackConfig.getOriginRealm();
			destRealm=ShStackConfig.getDestRealm();
			localhost=ShStackConfig.getLocalFQDN();
			isclientMode=ShStackConfig.isClientModeEnabled();
			serverContextId= ShStackConfig.getServiceContextId();//"roContext@agnity.com";
			stackProperties= new Properties();
			/**
			 * To enable stack management the property ENABLE_STACKMANAGEMENT has to
			 * be set to true
			 */
			stackProperties.setProperty("fr.marben.diameter.ENABLE_STACKMANAGEMENT",
					"true");
		} catch (Exception ex) {
			logger.error("ShResourceFactory.init() failed.");
			logger.error(ex.getMessage(), ex);
			throw new ShResourceException("ShResourceFactory.init() failed.");
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
	class DiameterShR14ServerShutdown extends Thread {
		private ShStackServerInterfaceImpl server = null;

		public void run() {

			System.err.println("DiameterRoR14ServerShutdown hook called");
			if (server.getDiameterStack() != null) {
				System.err.println("Stopping the stack...");
				server.shutdown();
				server.getDiameterStack() .stop();
			}
		}

		DiameterShR14ServerShutdown(ShStackServerInterfaceImpl srv) {
			this.server = srv;
		}
	}


	@SuppressWarnings("unchecked")
	public void run() {

		logger.info("Inside run of ShStackInterface ...with lopp delay "+loop_delay);
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
				stack.deleteDiameterProvider(shProvider);
				shProvider = null;
			}

			{
				logger.info("Deleting all Listening points...");
				Iterator<DiameterListeningPoint> it;
				do {
					it = stack.getDiameterListeningPoints();
					if (it.hasNext()) {
						DiameterListeningPoint lp = it.next();
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


	public void startStack() throws ShResourceException {
		logger.debug("Inside ShStackServerInterfaceImpl start()");
		try{
			logger.debug("createDiameterStack  with realm "+originRealm +" FQDN " +localhost);

			DiameterFactory factory =DiameterFactory.getInstance();
			stack = factory.createDiameterStack(originRealm,
					localhost, stackProperties);

			logger.info("Server starting");

			logger.debug("initializing transport stack");

			/**
			 * Register shutdown hook
			 */
			ShStackServerInterfaceImpl.DiameterShR14ServerShutdown sh = new ShStackServerInterfaceImpl.DiameterShR14ServerShutdown(this);
			Runtime.getRuntime().addShutdownHook(sh);

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
			throw new ShResourceException("startStack failed.");
		}
	}

	public void stopStack() throws ShResourceException {
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
			stack.stop();
			isAlive=false;
			isRunning=false;
			if (isclientMode) {
				logger.debug("Client mode is enabled hence stop client interface as well ");
				clientInterface.stopStack();
			}
		} catch (Exception ex) {
			logger.error("ShResourceFactory.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new ShResourceException("ShResourceFactory.init() failed.");
		}

	}

	public void incrementRequestedActionCounter(ShRequest request) throws ShResourceException
	{
		if(request instanceof ShUserDataRequest){
			this.udrCnt.increment();
			logger.debug("Requested UDR");
		}else if(request instanceof ShProfileUpdateRequest){
			this.purCnt.increment();
			logger.debug("Requested PUR");
		}else if(request instanceof ShPushNotificationRequest){
			this.pnrCnt.increment();
			logger.debug("Requested PNR");
		}else if(request instanceof ShSubscribeNotificationRequest){
			this.snrCnt.increment();
			logger.debug("Requested SNR");
		}else {
			logger.error("Wrong/Unkown request");
		}
	}
		public void handleRequest(ShRequest request) throws ShResourceException {		
		logger.debug("handleRequest(ShRequest) called ");
		
		if (request == null) {
			logger.error("handleRequest(): null request.");
			return;
		}
		
		int handle = -1;
		
		ShSession session = (ShSession)request.getSession();

			try {
				logger.debug("request map size before adding is :[ "+ this.requests.size()+"]");

				//Set worker queue for message
				((SipApplicationSessionImpl)session.getApplicationSession()).getIc().setWorkQueue(((ShAbstractRequest)request).getWorkQueue());
				if (request instanceof ShUserDataRequestImpl) {

					((ShUserDataRequestImpl)request).incrementRetryCounter();
					this.requests.put(((ShUserDataRequestImpl)request).getStackObj().getSessionIdAVPValue(),request);
					logger.debug("ShUserDataRequest set successfully for handle: "+handle);
					this.udrCnt.increment();
					
//					DiameterMessage updatedReq=addDestinationHostAndRouteRecordAVPS(((ShUserDataRequestImpl) request).getStackObj());
//					((ShUserDataRequestImpl) request).setStackObj(updatedReq);
					
					if (ShStackConfig.isStateless()) {
						if(logger.isDebugEnabled()){
							logger.debug( "send UDR message On stateless shProvider ");
						}
						ShStackClientInterfaceImpl.shProvider
								.sendMessage(((ShUserDataRequestImpl) request)
										.getStackObj());

					} else {
						if(logger.isDebugEnabled()){
							logger.debug( "send UDR message On client stack session");
						}
						session.getClientStackSession().sendMessage(
								((ShUserDataRequestImpl) request).getStackObj());
					}
			}else if (request instanceof ShProfileUpdateRequestImpl) {

					((ShProfileUpdateRequestImpl)request).incrementRetryCounter();
					this.requests.put(((ShProfileUpdateRequestImpl)request).getStackObj().getSessionIdAVPValue(),request);
					session.getClientStackSession().sendMessage(((ShProfileUpdateRequestImpl) request)
							.getStackObj());
					logger.debug("ShProfileUpdateRequestImpl rsent successfully for handle: "+handle);
					this.purCnt.increment();
					session.getClientStackSession().sendMessage(
							((ShProfileUpdateRequestImpl) request).getStackObj());
				} else if (request instanceof ShSubscribeNotificationRequestImpl) {

					((ShSubscribeNotificationRequestImpl)request).incrementRetryCounter();
					this.requests.put(((ShSubscribeNotificationRequestImpl)request).getStackObj().getSessionIdAVPValue(),request);
					session.getClientStackSession().sendMessage(((ShSubscribeNotificationRequestImpl) request)
							.getStackObj());
					logger.debug("shSubscribeNotification sent successfully for handle: "+ handle  );
					this.snrCnt.increment();
					session.getClientStackSession().sendMessage(
							((ShSubscribeNotificationRequestImpl) request).getStackObj());
				}
			} catch (DiameterException ex) {
				logger.error("ValidationException in sending Sh Request.",ex);
				if (request instanceof ShUserDataRequestImpl)
				{
					removeRequestFromMap(((ShUserDataRequestImpl)request).getStackObj());
					this.udrSendErrorCnt.increment();
				}
				else if (request instanceof ShProfileUpdateRequestImpl) {
					removeRequestFromMap(((ShProfileUpdateRequestImpl)request).getStackObj());
					this.purSendErrorCnt.increment();
				}
				else if (request instanceof ShSubscribeNotificationRequestImpl){
					removeRequestFromMap(((ShSubscribeNotificationRequestImpl)request).getStackObj());
					this.snrSendErrorCnt.increment();
				}
				ShResourceEvent resourceEvent = new ShResourceEvent(request,
						ShResourceEvent.REQUEST_FAIL_EVENT, request.getApplicationSession());
				resourceEvent.setMessage((ShMessage)request);
				try {
					this.ra.deliverEvent(resourceEvent);
				} catch (ResourceException e) {
					logger.error("Exception in delivering ShResourceEvent"+ex);
					throw new ShResourceException(ex);
				}
			}catch(Exception ex){
				logger.error("handleRequest() failed: " + ex);
				logger.error(ex.getMessage(), ex);
				throw new ShResourceException(ex);
			}
		}

	@Override
	public void handleResponse(ShResponse response) throws ShResourceException {
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
			if (response instanceof ShUserDataResponseImpl)
			{
				this.udrSendErrorCnt.increment();
			}
			else if (response instanceof ShProfileUpdateRequestImpl) {
				this.purSendErrorCnt.increment();
			}
			else if (response instanceof ShSubscribeNotificationRequestImpl){
				this.snrSendErrorCnt.increment();
			}
			ShResourceEvent resourceEvent = new ShResourceEvent(response,
					ShResourceEvent.RESPONSE_FAIL_EVENT, response.getApplicationSession());
			resourceEvent.setMessage((ShMessage)response);
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering ShResourceEvent :: ",e);
				throw new ShResourceException(e);
			}

		}
	}

	// TODO we may also send ResourceEvent for this.
	public void receivedErrorMessage(DiameterSession session,
									 DiameterMessage pendingRequest, DiameterMessage answer) {
		logger.debug("Inside receivedErrorMessage with " + answer);
		try {
			logger.debug("removing request from map");
			ShRequest containerReq = (ShRequest) this.requests.remove(pendingRequest.getSessionIdAVPValue());

			if (containerReq==null) {
				logger.error("corresponding request is not found. returning");
				return;
			}
			ShResponse response= new ShUserDataResponseImpl(answer);
		
			if (containerReq instanceof ShUserDataRequestImpl)
			{
				logger.debug("received UDR request");
				this.updateResponseCounter(UDR, (int) answer.getResultCodeAVP());
				((ShMessage) response).setProtocolSession(((ShUserDataRequestImpl)containerReq).getProtocolSession());
			}
			else if (containerReq instanceof ShProfileUpdateRequestImpl) {
				logger.debug("received UDR request");
				this.updateResponseCounter(PUR, (int) answer.getResultCodeAVP());
				((ShMessage) response).setProtocolSession(((ShProfileUpdateRequestImpl)containerReq).getProtocolSession());
			}
			else if (containerReq instanceof ShSubscribeNotificationRequestImpl){
				logger.debug("received UDR request");
				this.updateResponseCounter(SNR, (int) answer.getResultCodeAVP());
				((ShMessage) response).setProtocolSession(((ShSubscribeNotificationRequestImpl)containerReq).getProtocolSession());
			}
			else {
				logger.error("Wrong/Unkown type request received.");
				throw new ResourceException("Wrong/Unkown request type.");
			}

			logger.debug("delivering receivedErrorMessage event");
			//this.ra.deliverResponse(response);
			ShResourceEvent resourceEvent = new ShResourceEvent(this,
					ShResourceEvent.ERROR_MSG_RECEIVED, response.getApplicationSession());
			resourceEvent.setMessage((ShMessage) response);
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering ShResourceEvent"+e);
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
		ShUserDataRequestImpl containerReq = (ShUserDataRequestImpl) this.requests.get(pendingRequest.getSessionIdAVPValue());
		if(containerReq==null){
			logger.debug("Request already handled");
			return;
		}
		if(containerReq.getRetryCounter()< MAX_RETYR_COUNTER){
			try{
				containerReq.incrementRetryCounter();
				containerReq.setReTransmitted(true);
				session.sendMessage(pendingRequest);
			}catch(Exception e){
				logger.debug("Exception in sending retransmit req ",e);
			}
		}else{
			logger.debug("re-transmissions exceeded. Devivering timeout event");
			removeRequestFromMap(((ShUserDataRequestImpl)containerReq).getStackObj().getSessionIdAVPValue());
			ShResourceEvent resourceEvent = new ShResourceEvent(containerReq,
					ShResourceEvent.TIMEOUT_EVENT, containerReq.getApplicationSession());
			resourceEvent.setMessage(containerReq);
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering ShResourceEvent",e);
			}
		}
	}

	public void requestTimeoutExpired(DiameterSession session,
									  DiameterMessage pendingRequest) {
		logger.debug("Inside requestTimeoutExpired for request " + pendingRequest);
		ShUserDataRequestImpl containerReq = (ShUserDataRequestImpl) this.requests.get(pendingRequest.getSessionIdAVPValue());
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
			removeRequestFromMap(((ShUserDataRequestImpl)containerReq).getStackObj());
			ShResourceEvent resourceEvent = new ShResourceEvent(containerReq,
					ShResourceEvent.TIMEOUT_EVENT, containerReq.getApplicationSession());
			resourceEvent.setMessage(containerReq);
			try {
				this.ra.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering ShResourceEvent"+e);
			}
		}
	}

		private void updateResponseCounter(int type, int resultCode) {
		switch (type) {
		case UDA:
			if (resultCode >= 1000 && resultCode < 2000) {
				this.uda1xxxCnt.increment();
			} else if (resultCode >= 2000 && resultCode < 3000) {
				this.uda2xxxCnt.increment();
			} else if (resultCode >= 3000 && resultCode < 4000) {
				this.uda3xxxCnt.increment();
			} else if (resultCode >= 4000 && resultCode < 5000) {
				this.uda4xxxCnt.increment();
			} else if (resultCode >= 5000 && resultCode < 6000) {
				this.uda5xxxCnt.increment();
			}
			break;
		case PUA:
			if (resultCode >= 1000 && resultCode < 2000) {
				this.pua1xxxCnt.increment();
			} else if (resultCode >= 2000 && resultCode < 3000) {
				this.pua2xxxCnt.increment();
			} else if (resultCode >= 3000 && resultCode < 4000) {
				this.pua3xxxCnt.increment();
			} else if (resultCode >= 4000 && resultCode < 5000) {
				this.pua4xxxCnt.increment();
			} else if (resultCode >= 5000 && resultCode < 6000) {
				this.pua5xxxCnt.increment();
			}
			break;
		case SNA:
			if (resultCode >= 1000 && resultCode < 2000) {
				this.sna1xxxCnt.increment();
			} else if (resultCode >= 2000 && resultCode < 3000) {
				this.sna2xxxCnt.increment();
			} else if (resultCode >= 3000 && resultCode < 4000) {
				this.sna3xxxCnt.increment();
			} else if (resultCode >= 4000 && resultCode < 5000) {
				this.sna4xxxCnt.increment();
			} else if (resultCode >= 5000 && resultCode < 6000) {
				this.sna5xxxCnt.increment();
			}
			break;
		case PNA:
			if (resultCode >= 1000 && resultCode < 2000) {
				this.pna1xxxCnt.increment();
			} else if (resultCode >= 2000 && resultCode < 3000) {
				this.pna2xxxCnt.increment();
			} else if (resultCode >= 3000 && resultCode < 4000) {
				this.pna3xxxCnt.increment();
			} else if (resultCode >= 4000 && resultCode < 5000) {
				this.pna4xxxCnt.increment();
			} else if (resultCode >= 5000 && resultCode < 6000) {
				this.pna5xxxCnt.increment();
			}
			break;
		}
	}

	public void originStateIdChanged(long oldValue, long newValue) {
		logger.error("Origin-State-Id of " + serverPeer+
				" changed from " + oldValue + " to " + newValue);
	}

	public void removeRequestFromMap(Object request) {
		if(logger.isDebugEnabled())
			logger.debug("Removing request from map"+request);
		if (request instanceof ShUserDataRequestImpl)
		{
			this.requests.remove(((ShUserDataRequestImpl)request).getStackObj().getSessionIdAVPValue());
		}
		else if (request instanceof ShProfileUpdateRequestImpl) {
			this.requests.remove(((ShProfileUpdateRequestImpl)request).getStackObj().getSessionIdAVPValue());
		}
		else if (request instanceof ShSubscribeNotificationRequestImpl){
			this.requests.remove(((ShSubscribeNotificationRequestImpl)request).getStackObj().getSessionIdAVPValue());
		}
	}

	/**
	 *
	 * This class will be used to monitor shClient stack.<br>
	 * This timer task will periodically check stack state:<br>
	 * <ol>
	 * <li> Stack in not started successfully then it will try to restart stack.</li>
	 * 		OR
	 * <li> If server peer is not connected then it will try to reconnect peer.</li></ol>
	 * @author Amit Baxi
	 *
	 */
	private class ShStackMonitorTask extends TimerTask {

		Logger logger= Logger.getLogger(ShStackServerInterfaceImpl.ShStackMonitorTask.class);

		DiameterStack stack;
		int count=1;
		ShStackMonitorTask(DiameterStack stack2){
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
				}
			} catch (Exception e) {
				logger.error("Exception inside run()"+e.getMessage(),e);
			}
		}
	}

	public void ClientCreateSessionAndSendRequest() {
		logger.info("ClientCreateSessionAndSendRequest");
		try {

			/* Create a User Data Request */

			/* Create Supported-Features AVP */
			List<DiameterAVP>[] lSupportedFeatures = new ArrayList[1];
			Long lVendorId = new Long(123L);
			Long lFeatureListID = new Long(123L);
			Long lFeatureList = new Long(123L);
			lSupportedFeatures[0] = shFactory.createSupportedFeaturesAVP(
					lVendorId, lFeatureListID, lFeatureList);

			/* Create User-Identity AVP */
			String lPublicIdentity = new String("UTF8StringPublic-Identity");
			String lMSISDN = "MSISDN";
			List<DiameterAVP> lUserIdentity = shFactory.createUserIdentityAVP(
					lPublicIdentity, lMSISDN);

			String lWildcardedPSI = new String(" UTF8StringWildcarded-PSI");
			String lWildcardedIMPU = new String(" UTF8StringWildcarded-IMPU");
			String lServerName = new String(" UTF8StringServer-Name");
			String[] lServiceIndication = new String[] { " OctetStringService-Indication" };
			String[] lDataReference = new String[] { "RepositoryData" };
			String[] lIdentitySet = new String[] { "ALL_IDENTITIES" };
			String lRequestedDomain = new String("CS-Domain");
			String lCurrentLocation = new String(
					"DoNotNeedInitiateActiveLocationRetrieval");
			String[] lDSAITag = new String[] { " OctetStringDSAI-Tag" };
			String lSessionPriority = new String("PRIORITY_0");
			Long lRequestedNodes = new Long(123L);
			String lServingNodeIndication = new String(
					"ONLY_SERVING_NODES_REQUIRED");
			String lPrepagingSupported = new String("PREPAGING_NOT_SUPPORTED");

			DiameterMessage request = shFactory.createUserDataRequest(destRealm.get(0),
					lSupportedFeatures,
					lUserIdentity,
					lWildcardedPSI,
					lWildcardedIMPU,
					lServerName,
					lServiceIndication,
					lDataReference,
					lIdentitySet,
					lRequestedDomain,
					lCurrentLocation,
					lDSAITag,
					lSessionPriority,
					lRequestedNodes,
					lServingNodeIndication,
					lPrepagingSupported);

			String WildcardedPSI = "Wildcarded-PSI";
			DiameterAVP avp = stack.getDiameterMessageFactory().createOctetStringAVP("Wildcarded-PSI", "3GPP", WildcardedPSI);
			request.add(avp);

			String DSAITag = "DSAI-Tag";
			avp = stack.getDiameterMessageFactory().createOctetStringAVP("DSAI-Tag", "3GPP", DSAITag);
			request.add(avp);

			int SendDataIndic = 1;
			avp = stack.getDiameterMessageFactory().createInteger32AVP("Send-Data-Indication", "3GPP", SendDataIndic);
			request.add(avp);

			logger.info(" UDR message created ");

			/*
			 * 3. Create a new session (with null as argument to let the stack
			 * allocate the session-ID). Send the message, and forget about the
			 * session until response notification.
			 */
			//		if (stopTime == 0) {
			DiameterSession session = shProvider
					.createClientDiameterAcctSession(null);

			logger.info("Sending UDR : " + request.toString());
			session.sendMessage(request);

			/*
			 * 4. Display and update statistics.
			 */


		} catch (DiameterException e) {
			logger.error("Exception caught in "
					+ "ClientCreateSessionAndSendRequest: " + e.toString());
		}
	}

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



	///////////////////////////////////////////////////////////

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
					ShResourceEvent event = new ShResourceEvent(this,
							ShResourceEvent.SH_NOTIFY_PEER_DOWN,
							null);
					event.setData(serverRoute.getIpAddress());
					ra.deliverEvent(event);
					logger.debug("delivered peer down event");
				}
				catch(Exception ex) {
					logger.error(ex.toString(),ex);
				}
			}
			else if (state.equals(DiameterRoute.RouteState.available)){
				logger.error(" ************** " + serverPeer + " is OKAY ************** ");
				try {
					ShResourceEvent event = new ShResourceEvent(this,
							ShResourceEvent.SH_NOTIFY_PEER_UP,
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
				ShResourceEvent event = new ShResourceEvent(this,
						ShResourceEvent.SH_NOTIFY_PEER_UP,
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

				logger.info("Stack::Idle  create shProvider");
				/**
				 * Add our application's own AVP and command codes in addition
				 * to the Ro Release13 dictionary which is already preloaded in
				 * the Diameter stack.
				 */
				shFactory = stack
						.getDiameterShMessageFactory(DiameterStack.RELEASE14);

//				if (ShStackConfig.getExtendedDictionary() != null) {
//
//					logger.info("Stack::Idle extended grammer with dictionary provided");
//
//					stack.extendGrammar(ShStackConfig.getExtendedDictionary());
//				} else {
				stack.extendGrammar(shFactory.getShDictionary());
				//}

				ShMessageFactoryImpl.setDiameterShMsgFactory(shFactory);

				/**
				 * Instantiate one provider with the application name matching
				 * the one configured in the xml dictionary, and attach to a
				 * listener (for example this) so that it receives all incoming
				 * messages.
				 */
				stackProperties = new Properties();
				stackProperties.setProperty("jarFileName", "mjds3gppsh.jar");
				stackProperties.setProperty("interfaceName", "sh");
				stackProperties.setProperty("vendorId", "3gpp");
				stackProperties.setProperty("className", "DiameterShProvider");

				logger.info("Stack::Idle shProvider create stateful  provider");
				stackProperties.setProperty("fsmName", "RFC_SRV");

				DiameterProvider tmpProvider = stack.createDiameterProvider(
						"Sh", stackProperties);

				if (tmpProvider instanceof DiameterShProvider) {
					shProvider = (DiameterShProvider) tmpProvider;
				} else {
					// error to be managed
				}

				if (ShStackConfig.getExtendedDictionary() != null) {

					logger.info("Stack::Idle extended provider grammer with dictionary provided");

					shProvider.extendGrammar(ShStackConfig.getExtendedDictionary());
				}

				shProvider.setDiameterShListener(this);

				logger.info("Stack::Idle shProvider created is " + shProvider);

				/**
				 * Indicate that this example implements a provider Manager, so
				 * Stack management call-backs will be called for this provider.
				 *
				 */
				shProvider.setDiameterProviderManager(this);
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
		shProvider.eventInit();
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
			shProvider.eventStart();
		}
	}

	public void Initializing(int aDate, int aStatus, int lastStackEvent) {
		logger.info("Stack::Initializing(" + aDate + "," + aStatus + ","
				+ lastStackEvent + ")");

		/**
		 * Create a listening point for incoming connections in server mode
		 */
		if ((lastStackEvent == DiameterStack.EVENTINIT)) {

			listeningPoints = ShStackConfig.getListeningPoints();
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

	public DiameterShMessageFactory getDiameterShMsgFactory(){
		return stack.getDiameterShMessageFactory(DiameterStack.RELEASE13);
	}

	public DiameterShMessageFactory getDiameterClientIfoMsgFactory(){

		if(logger.isDebugEnabled()){
			logger.debug("getDiameterClientIfoMsgFactory ..");
		}
		return clientInterface.getDiameterShMsgFactory();
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
				&& (lastProviderEvent == DiameterShProvider.EVENTSTART)) {
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
				remotePeerRoutes = ShStackConfig.getRemotePeerRoutes();

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
								stack.createDiameterRoute("Sh", destRlm,
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

	boolean validateMandatoryAVPs(DiameterMessage request) {
	if (logger.isDebugEnabled()) {
			logger.debug("validateMandatoryAVPs ");
		}
	/*
		boolean valid = true;
		if (request.getAVP(AvpCodes.Auth_Application_id) == null) {
			logger.error("missing Mandatory Auth_Application_id");
			return false;
		}*/
		if (logger.isDebugEnabled()) {
			logger.debug("validateMandatoryAVPs leaving "+true);
		}
		return true;
	}

	private boolean validateAuthAppId(DiameterMessage request) {
		if (logger.isDebugEnabled()) {
			logger.debug("validateAuthAppId ");
		}
	/*	boolean valid = true;
		ArrayList<DiameterAVP> avplist = request.getAVP(AvpCodes.Auth_Application_id);

		DiameterAVP avp = avplist.get(0);

		DiameterUnsigned32AVP authid = (DiameterUnsigned32AVP) avp;
		if (logger.isDebugEnabled()) {
			logger.debug("Auth_Application_id " + authid.getValue());
		}
		if (authid.getValue() != authApplicationId) {
			return false;
		}
*/
		if (logger.isDebugEnabled()) {
			logger.debug("validateAuthAppId leaving "+true);
		}
		return true;
	}


	public void reAuthAnswerReceived(String arg0, DiameterMessageEvent arg1) {

		if(logger.isDebugEnabled()){
			logger.debug("reAuthAnswerReceived ..");
		}

	}


	public void reAuthRequestReceived(String arg0, String arg1, String arg2, DiameterMessageEvent arg3) {
		// TODO Auto-generated method stub

		if(logger.isDebugEnabled()){
			logger.debug("reAuthRequestReceived ..");
		}

	}

	@Override
	public DiameterMessage updateCapabilityExchange(DiameterMessage msg, DiameterPeer peer) {
		DiameterAVP avp = stack.getDiameterMessageFactory()
				.createUnsigned32AVP(
						"Auth-Application-Id",
						"base",16777217);
		msg.add(avp);

		if(logger.isDebugEnabled()){
			logger.debug("updateCapabilityExchange ..with Auth-Application-Id");
		}
		return  msg;
	}

	@Override
	public boolean isUnknownPeerAuthorized(DiameterMessage incomingUDR) {
	return true;
	}

	@Override
	public void userDataRequestReceived(String destinationRealm,
										List<DiameterAVP>[] supportedFeatures,
										List<DiameterAVP> userIdentity,
										String wildcardedPSI,
										String wildcardedIMPU,
										String serverName,
										String[] serviceIndication,
										String[] dataReference,
										String[] identitySet,
										String requestedDomain,
										String currentLocation,
										String[] tag,
										String sessionPriority,
										Long requestedNodes,
										String servingNodeIndication,
										String prepagingSupported,
										DiameterMessageEvent msgEvent) {

		try {
		DiameterMessage request = msgEvent.getDiameterMessage();
		logger.info("Received UDR:" + request.toString());
		logger.info("UserDataRequestReceived Session id " +request.getSessionIdAVPValue());

			String resultCode = "DIAMETER_SUCCESS";

			/* Create Supported-Features AVP */
			List<DiameterAVP>[] supportedFeaturesAns = new ArrayList[1];
			Long lVendorId = new Long(123L);
			Long lFeatureListID = new Long(123L);
			Long lFeatureList = new Long(123L);

			supportedFeaturesAns[0] = shFactory.createSupportedFeaturesAVP(
					lVendorId, lFeatureListID, lFeatureList);


			String wildcardedPSIAns = new String(" UTF8StringWildcarded-PSI");
			String wildcardedIMPUAns = new String(" UTF8StringWildcarded-IMPU");
			String userDataAns = new String(" OctetStringUser-Data");
			DiameterMessage diameterMessage = msgEvent.getDiameterMessage();

		DiameterSession session = msgEvent.getDiameterSession();
		/* Otherwise, the session is new on the server side */
		if (session == null) {

			logger.info("create New Session as incoming session is null ");
			session = shProvider.createServerDiameterSession(request);
		}

			if (!validateMandatoryAVPs(request)) {

				DiameterMessage answer = shFactory.createUserDataAnswer(
						ResultCodes.getReturnCode(5005), supportedFeaturesAns,
						wildcardedPSIAns,
						wildcardedIMPUAns,
						userDataAns);
				answer.setSessionIdAVPValue(diameterMessage.getSessionIdAVPValue());
				session.sendMessage(answer);

				logger.error("missing mandatory avp send error code 5005 ");
				return;
			}

			if(!validateAuthAppId(request)){
				DiameterMessage answer = shFactory.createUserDataAnswer(
						ResultCodes.getReturnCode(3007), supportedFeaturesAns,
						wildcardedPSIAns,
						wildcardedIMPUAns,
						userDataAns);
				answer.setSessionIdAVPValue(diameterMessage.getSessionIdAVPValue());
				session.sendMessage(answer);

				logger.error("missing mandatory avp send error code 3007 ");
				return;
			}

			String nextHop = msgEvent.getSenderOriginHost();
			clientInterface.handleIncomingShUserDataRequest(session, request,nextHop);

			/*
			 * Delete the session when we know it is no longer needed on the
			 * server side
			 */
/*

			if ((CCRequestType.equals(DiameterShMessageFactory.TERMINATION_REQUEST))
					|| (CCRequestType.equals(DiameterShMessageFactory.EVENT_REQUEST))) {
				//	session.delete();
				session = null;
			}
*/
		} catch (DiameterException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void userDataAnswerReceived(String s, List<DiameterAVP>[] lists, String s1, String s2, String s3, DiameterMessageEvent diameterMessageEvent) {
		logger.error("Server don't Receive UDA");
	}

	@Override
	public void profileUpdateRequestReceived(String s, List<DiameterAVP>[] lists, List<DiameterAVP> list, String s1, String s2, String[] strings, String s3, DiameterMessageEvent diameterMessageEvent) {
//todo
	}

	@Override
	public void profileUpdateAnswerReceived(String s, String s1, String s2, List<DiameterAVP> list, DiameterMessageEvent diameterMessageEvent) {
		logger.error("Server don't Receive PUA : ");
	}

	@Override
	public void subscribeNotificationsRequestReceived(String s, List<DiameterAVP>[] lists, List<DiameterAVP> list, String s1, String s2, String[] strings, String s3, String s4, String s5, String[] strings1, String[] strings2, String s6, String[] strings3, String s7, DiameterMessageEvent diameterMessageEvent) {
//todo
	}

	@Override
	public void subscribeNotificationsAnswerReceived(String s, String s1, String s2, List<DiameterAVP>[] lists, String s3, String s4, DiameterMessageEvent diameterMessageEvent) {
		logger.error("Server don't Receive SNA : ");
	}

	@Override
	public void pushNotificationRequestReceived(String s, String s1, List<DiameterAVP>[] lists, List<DiameterAVP> list, String s2, String s3, String s4, DiameterMessageEvent diameterMessageEvent) {
//todo
	}

	@Override
	public void pushNotificationAnswerReceived(String s, List<DiameterAVP>[] lists, DiameterMessageEvent diameterMessageEvent) {
		logger.error("Server don't Receive PNA : ");
	}
	
	
//	private DiameterMessage addDestinationHostAndRouteRecordAVPS(
//			DiameterMessage request) {
//
//		if (logger.isDebugEnabled()) {
//			logger.debug(" addDestinationHostAndRouteRecordAVPS ");
//		}
//		DiameterAVP avp=null;
//		if (ShStackConfig.getLocalClientFQDN() != null) {
//			avp = clientInterface
//					.getDiameterStack()
//					.getDiameterMessageFactory()
//					.createOctetStringAVP("Route-Record", "base",
//							ShStackConfig.getLocalClientFQDN());
//
//			if (logger.isDebugEnabled()) {
//				logger.debug(" addDestinationHostAndRouteRecordAVPS  add Route-Record"
//						+ ShStackConfig.getLocalClientFQDN());
//			}
//		}
//		request.add(avp);
//
//		String fqdn = null;
//		List<DiameterRoute> routes = clientInterface.getRoutes();
//		if (routes != null && !routes.isEmpty()) {
//			DiameterRoute route = routes.get(0);
//			fqdn = route.getRemotePeerFQDN();
//		}
//		
//		if (logger.isDebugEnabled()) {
//			logger.debug(" addDestinationHostAndRouteRecordAVPS  add Destination-Host "+fqdn);
//		}
//		if (fqdn != null) {
//			avp = clientInterface.getDiameterStack()
//					.getDiameterMessageFactory()
//					.createOctetStringAVP("Destination-Host", "base", fqdn);
//		}
//		request.add(avp);
//		return request;
//	}
}

// receivedErrorMessage() remaining
// timeoutExpired() remaining
// requestTimeoutExpired() remaining