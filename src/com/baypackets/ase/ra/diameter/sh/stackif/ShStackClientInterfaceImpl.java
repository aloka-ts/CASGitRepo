package com.baypackets.ase.ra.diameter.sh.stackif;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.ra.diameter.sh.*;
import com.baypackets.ase.ra.diameter.sh.impl.ShMessageFactoryImpl;
import com.baypackets.ase.ra.diameter.sh.impl.ShSession;
import com.baypackets.ase.ra.diameter.sh.utils.Constants;
import com.baypackets.ase.ra.diameter.sh.utils.ShStackConfig;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.spi.util.TimerService;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.ase.util.TelnetServer;

import fr.marben.diameter.*;
import fr.marben.diameter.DiameterRoute.RouteState;
import fr.marben.diameter._3gpp.sh.DiameterShMessageFactory;
import fr.marben.diameter._3gpp.sh.DiameterShProvider;
import fr.marben.diameter._3gpp.sh.DiameterShR14Listener;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class ShStackClientInterfaceImpl extends Thread implements
		DiameterNotificationListener, DiameterStackManager, DiameterProviderManager, DiameterShR14Listener,
		DiameterCapabilityExchangeListener, com.baypackets.ase.ra.diameter.sh.utils.Constants,CommandHandler {

	//private static final String SH_CLIENT_INFO = "sh-client-info";
	private static final String SH_STACK_STATS = "sh-stack-stats";
	private static final String SH_APP_STATS = "sh-app-stats";
	private static final String SH_PEER_STATS = "sh-peer-stats";
	private static final String SH_ROUTE_INFO = "sh-route-info";
	private static Logger logger = Logger.getLogger(ShStackClientInterfaceImpl.class);
	private static ShResourceFactory raFactory;
	private static TimerService timerService;
	private static long timeout = 30L;
	private int handleCount;
	public DiameterPeer serverPeer;
	public static String serverHost;
	private String configFile;
	private ShResourceAdaptor shResourceAdaptor;
	private Map outgoingRequests;
	private AseAlarmService alarmService;
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

	private String localClientRealm;
	private List destRealm;
	private String localhost;
	private String remotePeerRoutes;
	public static String serverContextId;
	private Properties stackProperties;
	private DiameterStack stack;
	private DiameterShMessageFactory shFactory;
	public static DiameterShProvider shProvider;
	private boolean stackStarted;
	boolean isAlive = true;
	boolean isRunning = false;

	boolean routeAdded = false;
	private long loop_delay = 1000;

	private DiameterRoute serverRoute;
	private String extendedDictionary;
	private Boolean isMutihomingEnabled;
	private static List<DiameterRoute> routesList=new ArrayList<DiameterRoute>();
	private static List<DiameterPeer> peersList=new ArrayList<DiameterPeer>();
	TelnetServer telnetServer;


	@Override
	public void processEvent(DiameterEvent diamEvent) {

		logger.info("processEvent called " + diamEvent);

		if(diamEvent instanceof DiameterRouteStateChangeEvent){

			logger.info("processEvent  DiameterRouteStateChangeEvent"); 
			DiameterRouteStateChangeEvent stateEvent=(DiameterRouteStateChangeEvent)diamEvent;
			String state=stateEvent.getDiameterRoute().getRouteState();
			
			for (int i = 0; i <= (routesList.size() - 1); i++) {
				DiameterRoute route = routesList.get(i);
				logger.info("processEvent  update route state ");
				if (stateEvent.getDiameterRoute().getRemotePeerFQDN()
						.equals(route.getRemotePeerFQDN())) {
					route.setRouteState(DiameterRoute.RouteState.valueOf(state));
				}

			}
			if (state.equals(DiameterRoute.RouteState.busy)||state.equals(DiameterRoute.RouteState.infinite_loop)){
				logger.error(" ************** " + serverPeer + " is DOWN *************** ");
				try {
					ShResourceEvent event = new ShResourceEvent(this,
							ShResourceEvent.SH_NOTIFY_PEER_DOWN,
							null);
					event.setData(serverRoute.getIpAddress());
					shResourceAdaptor.deliverEvent(event);
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
					shResourceAdaptor.deliverEvent(event);
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
				shResourceAdaptor.deliverEvent(event);
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
					"Event received DiameterRealmStateChangeEvent: " + realmEvent.toString());

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
		} else if(diamEvent instanceof DiameterRouteAddedEvent){
			logger.info("Event received DiameterRouteAddedEvent: " + diamEvent.toString());
//			DiameterRouteAddedEvent event=(DiameterRouteAddedEvent)diamEvent;
//			DiameterRoute route=event.getDiameterRoute();
//			routesList.add(route);		
		}else if(diamEvent instanceof DiameterRouteDeletedEvent){
			logger.info("Event received  DiameterRouteDeletedEvent: " + diamEvent.toString());
			DiameterRouteDeletedEvent event=(DiameterRouteDeletedEvent)diamEvent;
			DiameterRoute route=event.getDiameterRoute();
			routesList.remove(route);	
		}else{
			logger.info("Event received: " + diamEvent.toString());
		}
	}

	/**
	 * SendHook is an abstract method that must be implemented, even if the
	 * option is not set in the provider property list (the routine will never
	 * be called).
	 */
	@Override
	public boolean sendHook(fr.marben.diameter.DiameterSession arg0,
							DiameterMessage arg1) {
		return true;
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
	public boolean isUnknownPeerAuthorized(DiameterMessage incomingSHRequest) {
		return true;
	}

	@Override
	public String getAlternateRoute(String s, String s1, String s2) {
		return null;
	}

	@Override
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
			logger.error(
					"Notification PeerStateChange Transport"+ peerStateChange.getTransportType()+ " FQDN "
							+ peerStateChange.getFQDN() + " URI "
							+ peerStateChange.getURI() + " State "
							+ peerStateChange.getState());
			logger.error(
					"Notification Number of routes are "+ routesList.size());
			
			
			for (int i = 0; i <= (routesList.size() - 1); i++) {
				DiameterRoute route = routesList.get(i);
				logger.error(
						"Peer FDQN is"+peerStateChange.getFQDN()+" and route FQDN is "+route.getRemotePeerFQDN());
				if (peerStateChange.getFQDN().equals(route.getRemotePeerFQDN())) {
					if (peerStateChange.getState().equals("OPEN")) {
						logger.info("processEvent  update route state  to Available for " +route.getRemotePeerFQDN());
						route.setRouteState(DiameterRoute.RouteState.available);
						peersList.add(peerStateChange.getPeer());
						
						logger.info("processEvent peer List updated is  " +peersList);
//					} else {
//						logger.info("processEvent  update route state  to Not available");
//						route.setRouteState(DiameterRoute.RouteState.busy);
//					}
				}
//					else {
//					logger.info("processEvent  update route state  to Not available");
//					route.setRouteState(DiameterRoute.RouteState.busy);
			}

			}

		} else {
			logger.info(
					"Notification " + notification.toString());
		}
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
				 * to the SH RELEASE14 dictionary which is already preloaded in
				 * the Diameter stack.
				 */
				shFactory = stack
						.getDiameterShMessageFactory(DiameterStack.RELEASE14);

				stack.extendGrammar(shFactory.getShDictionary());
		
		    	ShMessageFactoryImpl.setDiameterShMsgFactory(shFactory);
				ShMessageFactoryImpl.setDiameterShClientStack(stack);

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


				if (ShStackConfig.isStateless() == true) {
					logger.info("Stack::Idle shProvider createe stateless  provider");

					stackProperties.setProperty("fsmName", "RFC_CL_STATELESS");// RFC_CL_LESS
					stackProperties.setProperty("storeStatelessData", "true");
				} else {
					logger.info("Stack::Idle shProvider createe staefull provider");
					stackProperties.setProperty("fsmName", "RFC_CL_LESS");// RFC_CL_LESS
				}

//			    stackProperties.setProperty("fr.marben.diameter.ENABLE_STACKMANAGEMENT",
//						"true");
				DiameterProvider tmpProvider = stack.createDiameterProvider(
						"Sh", stackProperties);

				if (tmpProvider instanceof DiameterShProvider) {
					shProvider = (DiameterShProvider) tmpProvider;
				} else {
					logger.error("tmpProvider instanceof DiameterShProvider is false");
				}

				if (ShStackConfig.getExtendedDictionary() != null) {

					logger.info("Stack::Idle extended grammer with dictionary provided");

					shProvider.extendGrammar(ShStackConfig.getExtendedDictionary());
				}

				logger.info("Stack::Idle shProvider created is " + shProvider);

				shProvider.setDiameterShListener(this);

				/**
				 * Indicate that this example implements a provider Manager, so
				 * Stack management call-backs will be called for this provider.
				 *
				 */
				shProvider.setDiameterProviderManager(this);
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
		} catch (ShResourceException e) {
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
		synchronized (this) {
			isRunning=true;

			logger.info("Stack::Running( stackStarted and running");
			stackStarted = true;
		}
	}

	@Override
	public void Waiting(int aDate, int aStatus, int lastStackEvent) {
		logger.info("Stack::Waiting(" + aDate + "," + aStatus
				+ "," + lastStackEvent + ")");
		stack.eventClose();

	}

	@Override
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
	@Override
	public void Running(int aDate, int aStatus, int lastStackEvent,
						int lastProviderEvent) {
		logger.info("Provider::Running(" + aDate + "," + aStatus
				+ "," + lastStackEvent + "," + lastProviderEvent + ")");

		/**
		 * Wait for the provider to be in running state to create route to new
		 * peers, in order to have our application present in UDR/UDA exchange.
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

			remotePeerRoutes = ShStackConfig.getRemotePeerRoutes();

			logger.info("Provider::Running(createDiameterRoute with remotePeerRoutes "+ remotePeerRoutes);


			if (remotePeerRoutes != null && !remotePeerRoutes.isEmpty()) {

				StringTokenizer st = new StringTokenizer(remotePeerRoutes, ",");
				// "m=1;aaa://servera.traffix.com:3868,m=2;aaa://serverb.traffix.com:3869"

				//"priority=1;defaultrealm;aaa://seagull.agnity.com:3868;transport=tcp,
				//priority=1;defaultrealm;aaa://seagull2.agnity.com:3869;transport=tcp,
				//priority=1;marbenrealm;aaa://cas0001.agnity.com:4002;transport=tcp"
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

							boolean mutihomingEnabled=false;
							if(realmAndURI.hasMoreTokens()){
								String transp=realmAndURI.nextToken();

								if (transp.indexOf("transport=") != -1) {

									String[] transportStr = transp.split("=");
									transport = transportStr[1];

									if ("sctp".equals(transport)
											&& isMutihomingEnabled == true) {
										mutihomingEnabled = true;
										logger.info("transport set is  "
												+ transport +" and mutihoming is also enabled");
									}
									
								}
							}
					
							if (mutihomingEnabled) {

								Properties options = new Properties();
								if (ShStackConfig.getLocalClientSctpIpList() != null
										&& !ShStackConfig
												.getLocalClientSctpIpList()
												.isEmpty()) {

									if(logger.isDebugEnabled()){
										logger.debug("SCTP_CLT_ADDRESS_LIST is -->"+ShStackConfig.getLocalClientSctpIpList());
									}
									options.setProperty(
											"fr.marben.diameter.SCTP_CLT_ADDRESS_LIST",
											ShStackConfig
													.getLocalClientSctpIpList());
								}
								if (!ShStackConfig.isInitiateConnection()) {

									logger.info("set INITIATE_CONNECTION false");
									options.setProperty(
											"fr.marben.diameter.INITIATE_CONNECTION",
											"false");
								}

								destHostURI = destHostURI + ";transport="
										+ transport;
								logger.info("createDiameterRoute with multihoming with destRealm  and URI as "
										+ destRlm
										+ " uri "
										+ destHostURI
										+ " priority "
										+ priority
										+ "SCTP_CLT_ADDRESS_LIST is "
										+ ShStackConfig.getLocalClientSctpIpList());
//								stack.createDiameterRoute("Sh", destRlm,
//										destHostURI, priority,options);
								DiameterRoute routecreated=stack.createDiameterRoute("sh", destRlm,
										destHostURI, priority,DiameterRoute.LocalAction.LOCAL,DiameterRoute.RouteTo.SERVER,options);
								routesList.add(routecreated);
							} else {

								destHostURI = destHostURI + ";transport="
										+ transport;
								logger.info("createDiameterRoute with destRealm  and URI as "
										+ destRlm
										+ " uri "
										+ destHostURI
										+ " priority " + priority);
								DiameterRoute routecreated=stack.createDiameterRoute("Sh", destRlm,
										destHostURI, priority);
								routecreated.setRouteState(RouteState.busy);
								routesList.add(routecreated);
							}
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


	@Override
	public void userDataRequestReceived(String s, List<DiameterAVP>[] lists, List<DiameterAVP> list, String s1, String s2, String s3, String[] strings, String[] strings1, String[] strings2, String s4, String s5, String[] strings3, String s6, Long aLong, String s7, String s8, DiameterMessageEvent diameterMessageEvent) {
// no need to implement. This is Client
	}

	@Override
	public void userDataAnswerReceived(String resultCode,
									   List<DiameterAVP>[] supportedFeatures,
									   String wildcardedPSI,
									   String wildcardedIMPU,
									   String ShUserData,
									   DiameterMessageEvent UDAEvent) {
		/* Display the UDA received */
		DiameterMessage uda = UDAEvent.getDiameterMessage();
		if(logger.isDebugEnabled()){
		logger.debug("userDataAnswerReceived with " + uda.getSessionIdAVPValue());
		}
		
		//logger.error("userDataAnswerReceived with " + uda.getSessionIdAVPValue());
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("removing request from map");
				logger.debug("request map size  :[ "
						+ ShStackServerInterfaceImpl.requests.size() + "]");
			}
			ShUserDataRequestImpl containerReq = (ShUserDataRequestImpl) ShStackServerInterfaceImpl.requests.remove(uda.getSessionIdAVPValue());
			if (containerReq==null) {
				logger.error("corrosponding requst is not found. returnning");
				return;
			}
			ShUserDataResponseImpl response = new ShUserDataResponseImpl
					(UDA,containerReq);
			response.setUserData(ShUserData);
			if (logger.isDebugEnabled()) {
			logger.info("Received ShUserData : " + ShUserData);
			}

			/***
			//Sh message type
			public static final int UDR = 1;
			public static final int UDA = 2;
			public static final int PUR = 3;
			public static final int PUA = 4;
			public static final int SNR = 5;
			public static final int SNA = 6;
			public static final int PNR = 7;
			public static final int PNA = 8;*/
			response.setStackObj(uda);
			response.setProtocolSession(containerReq.getProtocolSession());
			if (logger.isDebugEnabled()) {
			logger.debug("devilering UDA");
			}
			this.shResourceAdaptor.deliverResponse(response);
			UDAEvent.getDiameterSession().delete();
			if (logger.isDebugEnabled()) {
			logger.debug("delivered UDA successfully");
			}
			if (logger.isDebugEnabled()) {
			logger.debug("update response counter");
			}
			int result = ShResultCode.getReturnCode(resultCode, false);
			if (result != -1) {
				updateResponseCounter(2, result);
			}
		} catch (Exception ex) {
			logger.error("handleIncomingUDA() failed: " + ex);
			logger.error(ex.getMessage(), ex);
		}
		if(logger.isDebugEnabled()){
		logger.info("Delivered UDA : " + uda.toString());
		}
		//logger.error("userDataAnswerReceived Delivered with " + uda.getSessionIdAVPValue());
	}


	@Override
	public void profileUpdateRequestReceived(String s, List<DiameterAVP>[] lists, List<DiameterAVP> list, String s1, String s2, String[] strings, String s3, DiameterMessageEvent diameterMessageEvent) {
// no need to implement. This is Client
	}

	@Override
	public void profileUpdateAnswerReceived(String s, String s1, String s2, List<DiameterAVP> list, DiameterMessageEvent diameterMessageEvent) {

	}

	@Override
	public void subscribeNotificationsRequestReceived(String s, List<DiameterAVP>[] lists, List<DiameterAVP> list, String s1, String s2, String[] strings, String s3, String s4, String s5, String[] strings1, String[] strings2, String s6, String[] strings3, String s7, DiameterMessageEvent diameterMessageEvent) {
// no need to implement. This is Client
	}

	@Override
	public void subscribeNotificationsAnswerReceived(String s, String s1, String s2, List<DiameterAVP>[] lists, String s3, String s4, DiameterMessageEvent diameterMessageEvent) {

	}

	@Override
	public void pushNotificationRequestReceived(String s, String s1, List<DiameterAVP>[] lists, List<DiameterAVP> list, String s2, String s3, String s4, DiameterMessageEvent diameterMessageEvent) {
// no need to implement. This is Client
	}

	@Override
	public void pushNotificationAnswerReceived(String s, List<DiameterAVP>[] lists, DiameterMessageEvent diameterMessageEvent) {

	}


	public ShStackClientInterfaceImpl(ShResourceAdaptor ra) {
		handleCount = 0;
		this.shResourceAdaptor = ra;
		outgoingRequests = new Hashtable(0x10000);
	}

	public void init(ResourceContext context) throws ShResourceException {
		alarmService = (AseAlarmService) context.getAlarmService();
		raFactory = (ShResourceFactory) context.getResourceFactory();
		timerService = context.getTimerService();
		logger.debug("Initialize measurement counters.");
		MeasurementManager measurementMgr = context.getMeasurementManager();
		udrCnt = measurementMgr.getMeasurementCounter(Constants.UDR_COUNTER_IN);
		purCnt = measurementMgr.getMeasurementCounter(Constants.PUR_COUNTER_IN);
		snrCnt = measurementMgr.getMeasurementCounter(Constants.SNR_COUNTER_IN);
		pnrCnt = measurementMgr.getMeasurementCounter(Constants.PNR_COUNTER_IN);
		uda1xxxCnt = measurementMgr.getMeasurementCounter(Constants.UDA_1xxx_COUNTER_OUT);
		uda2xxxCnt = measurementMgr.getMeasurementCounter(Constants.UDA_2xxx_COUNTER_OUT);
		uda3xxxCnt = measurementMgr.getMeasurementCounter(Constants.UDA_3xxx_COUNTER_OUT);
		uda4xxxCnt = measurementMgr.getMeasurementCounter(Constants.UDA_4xxx_COUNTER_OUT);
		uda5xxxCnt = measurementMgr.getMeasurementCounter(Constants.UDA_5xxx_COUNTER_OUT);
		pua1xxxCnt = measurementMgr.getMeasurementCounter(Constants.PUA_1xxx_COUNTER_OUT);
		pua2xxxCnt = measurementMgr.getMeasurementCounter(Constants.PUA_2xxx_COUNTER_OUT);
		pua3xxxCnt = measurementMgr.getMeasurementCounter(Constants.PUA_3xxx_COUNTER_OUT);
		pua4xxxCnt = measurementMgr.getMeasurementCounter(Constants.PUA_4xxx_COUNTER_OUT);
		pua5xxxCnt = measurementMgr.getMeasurementCounter(Constants.PUA_5xxx_COUNTER_OUT);
		sna1xxxCnt = measurementMgr.getMeasurementCounter(Constants.SNA_1xxx_COUNTER_OUT);
		sna2xxxCnt = measurementMgr.getMeasurementCounter(Constants.SNA_2xxx_COUNTER_OUT);
		sna3xxxCnt = measurementMgr.getMeasurementCounter(Constants.SNA_3xxx_COUNTER_OUT);
		sna4xxxCnt = measurementMgr.getMeasurementCounter(Constants.SNA_4xxx_COUNTER_OUT);
		sna5xxxCnt = measurementMgr.getMeasurementCounter(Constants.SNA_5xxx_COUNTER_OUT);
		pna1xxxCnt = measurementMgr.getMeasurementCounter(Constants.PNA_1xxx_COUNTER_IN);
		pna2xxxCnt = measurementMgr.getMeasurementCounter(Constants.PNA_2xxx_COUNTER_IN);
		pna3xxxCnt = measurementMgr.getMeasurementCounter(Constants.PNA_3xxx_COUNTER_IN);
		pna4xxxCnt = measurementMgr.getMeasurementCounter(Constants.PNA_4xxx_COUNTER_IN);
		pna5xxxCnt = measurementMgr.getMeasurementCounter(Constants.PNA_5xxx_COUNTER_IN);

		this.configFile = (String) context.getConfigProperty("ase.home") + File.separator +
				"conf" + File.separator + "diameter_sh.yml";
		this.extendedDictionary = (String) context.getConfigProperty("ase.home") + File.separator +
				"conf" + File.separator + "diameter_sh_ext_dictionary.xml";
		logger.debug((new StringBuilder()).append("Use [").append(configFile).append("]").toString());
		init(configFile);
	}

	public void init(String cfgFile) throws ShResourceException {
		try {
			this.configFile = cfgFile;

			logger.debug("creating transport stack");

			ShStackConfig.loadconfiguration(this.configFile, this.extendedDictionary);
			localClientRealm = ShStackConfig.getLocalClientRealm();
			destRealm = ShStackConfig.getDestRealm();
			localhost = ShStackConfig.getLocalClientFQDN();
			remotePeerRoutes = ShStackConfig.getRemotePeerRoutes();
			isMutihomingEnabled =ShStackConfig.isMutihomingEnabled();

			serverContextId = ShStackConfig.getServiceContextId();

			stackProperties = new Properties();
			/**
			 * To enable stack management the property ENABLE_STACKMANAGEMENT has to
			 * be set to true
			 */
			stackProperties.setProperty("fr.marben.diameter.ENABLE_STACKMANAGEMENT", "true");
			stackProperties.setProperty("ENABLE_RB_ROUTE_ALGORITHM", "true");
		} catch (Exception ex) {
			logger.error("ShResourceFactory.init() failed.");
			logger.error(ex.getMessage(), ex);
			throw new ShResourceException("ShResourceFactory.init() failed.");
		}
	}

	public DiameterStack getDiameterStack() {

		if (logger.isDebugEnabled()) {
			logger.debug("getDiameterShMsgFactory return stack.." + stack);
		}
		return stack;
	}

	// Example shutdown hook class
	class DiameterShR14ClientShutdown extends Thread {
		private ShStackClientInterfaceImpl client = null;

		public void run() {

			System.err.println("DiameterShR14ClientShutdown hook called");
			if (client.getDiameterStack() != null) {
				System.err.println("Stopping the stack...");
				client.shutdown();
				client.getDiameterStack().stop();
			}
		}

		DiameterShR14ClientShutdown(ShStackClientInterfaceImpl client) {
			this.client = client;
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
			logger.error(" xeption " + e);
		}
	}

	public void startStack() throws ShResourceException {
		logger.debug("Inside ShStackClientInterfaceImpl start()");

		try {
			logger.debug("createDiameterStack  with realm " + localClientRealm
					+ " FQDN " + localhost);

			if (stackProperties == null) {
				stackProperties = new Properties();
			}

			if (isMutihomingEnabled) {
				stackProperties.setProperty(
						"fr.marben.diameter.ENABLE_SCTP_MULTIHOMING", "true");
			}
			
			DiameterFactory factory = DiameterFactory.getInstance();
			stack = factory.createDiameterStack(localClientRealm, localhost,
					stackProperties);

			logger.info("Client starting");

			logger.debug("initializing transport stack");

			/**
			 * Register shutdown hook
			 */
			ShStackClientInterfaceImpl.DiameterShR14ClientShutdown sh = new ShStackClientInterfaceImpl.DiameterShR14ClientShutdown(
					this);
			Runtime.getRuntime().addShutdownHook(sh);

			/**
			 * Set this class as an implementation of a diameter stack manager
			 */
			stack.setDiameterStackManager(this);

			/**
			 * Register our application as a DiameterNotificationListener,so
			 * that we receive all the notifications from the Diameter stack.
			 */
			stack.setDiameterNotificationListener(this);
			stack.setDiameterCapabilityExchangeListener(this);

			logger.debug("stack loaded successfully" + stack);
			logger.debug("clientStack loaded successfully");
			this.start();
			
			telnetServer = (TelnetServer) Registry
					.lookup(com.baypackets.ase.util.Constants.NAME_TELNET_SERVER);
			
//			if(logger.isDebugEnabled()){
//				logger.debug("registerHandler command ."+SH_CLIENT_INFO);
//			}
		//	telnetServer.registerHandler(SH_CLIENT_INFO, this, false);
			telnetServer.registerHandler(SH_ROUTE_INFO, this, false);
			telnetServer.registerHandler(SH_STACK_STATS, this, false);
			telnetServer.registerHandler(SH_APP_STATS, this, false);
			telnetServer.registerHandler(SH_PEER_STATS, this, false);
			
		} catch (Exception ex) {
			logger.error("startStack.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new ShResourceException("startStack failed.");
		}
	}

	public void stopStack() throws ShResourceException {
		logger.info("stop(): closing Diameter stack...");

		try {

			logger.debug("stop(): initialize Marben stack.");

			//	StackState state =
			stack.stop();//stop(5, TimeUnit.SECONDS);
			isAlive = false;
			isRunning = false;
		
		} catch (Exception ex) {
			logger.error("RoResourceFactory.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new ShResourceException("RoResourceFactory.init() failed.");
		}finally{	
			telnetServer.unregisterHandler(SH_ROUTE_INFO, this);
			telnetServer.unregisterHandler(SH_STACK_STATS, this);
			telnetServer.unregisterHandler(SH_APP_STATS, this);
			telnetServer.unregisterHandler(SH_PEER_STATS, this);
		}
	}

	@SuppressWarnings("unchecked")
	public void run() {

		logger.info("Inside run of ShStackInterface ...with lopp delay " + loop_delay);
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
				logger.error("InterruptedException  " + e);
			}
		}

		try {
			/* Final cleanup before exit */

			{
				logger.info("Removing the provider...");
				stack.deleteDiameterProvider(shProvider);
				shProvider = null;
			}
			Iterator it;
			do {
				it = stack.getDiameterRoutes();
				if (it.hasNext()) {
					DiameterRoute route = (DiameterRoute) it.next();
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

	private void updateResponseCounter(int type, int resultCode) {
		switch (type) {
			case 3: // '\003'
			case 5: // '\005'
			case 7: // '\007'
			default:
				break;

			case 2: // '\002'
				if (resultCode >= 1000 && resultCode < 2000) {
					uda1xxxCnt.increment();
					break;
				}
				if (resultCode >= 2000 && resultCode < 3000) {
					uda2xxxCnt.increment();
					break;
				}
				if (resultCode >= 3000 && resultCode < 4000) {
					uda3xxxCnt.increment();
					break;
				}
				if (resultCode >= 4000 && resultCode < 5000) {
					uda4xxxCnt.increment();
					break;
				}
				if (resultCode >= 5000 && resultCode < 6000)
					uda5xxxCnt.increment();
				break;

			case 4: // '\004'
				if (resultCode >= 1000 && resultCode < 2000) {
					pua1xxxCnt.increment();
					break;
				}
				if (resultCode >= 2000 && resultCode < 3000) {
					pua2xxxCnt.increment();
					break;
				}
				if (resultCode >= 3000 && resultCode < 4000) {
					pua3xxxCnt.increment();
					break;
				}
				if (resultCode >= 4000 && resultCode < 5000) {
					pua4xxxCnt.increment();
					break;
				}
				if (resultCode >= 5000 && resultCode < 6000)
					pua5xxxCnt.increment();
				break;

			case 6: // '\006'
				if (resultCode >= 1000 && resultCode < 2000) {
					sna1xxxCnt.increment();
					break;
				}
				if (resultCode >= 2000 && resultCode < 3000) {
					sna2xxxCnt.increment();
					break;
				}
				if (resultCode >= 3000 && resultCode < 4000) {
					sna3xxxCnt.increment();
					break;
				}
				if (resultCode >= 4000 && resultCode < 5000) {
					sna4xxxCnt.increment();
					break;
				}
				if (resultCode >= 5000 && resultCode < 6000)
					sna5xxxCnt.increment();
				break;

			case 8: // '\b'
				if (resultCode >= 1000 && resultCode < 2000) {
					pna1xxxCnt.increment();
					break;
				}
				if (resultCode >= 2000 && resultCode < 3000) {
					pna2xxxCnt.increment();
					break;
				}
				if (resultCode >= 3000 && resultCode < 4000) {
					pna3xxxCnt.increment();
					break;
				}
				if (resultCode >= 4000 && resultCode < 5000) {
					pna4xxxCnt.increment();
					break;
				}
				if (resultCode >= 5000 && resultCode < 6000)
					pna5xxxCnt.increment();
				break;
		}
	}

/*	public void handleIncomingProfileUpdateRequest(SessionShServer serverSession, MessageProfileUpdateRequest stackReq)
		{
			logger.debug("Inside handleIncomingProfileUpdateRequest");
			purCnt.increment();
			try
			{
				ShProfileUpdateRequestImpl request = new ShProfileUpdateRequestImpl(Constants.PUR);
				request.setStackObj(stackReq);
				ra.deliverRequest(request);
			
			}
			catch(Exception ex)
			{
				logger.error("handleIncomingShUserDataRequest() failed: ",ex);
			}
		}

		public void handleIncomingSubscribeNotificationsRequest(SessionShServer sessionshserver,
				MessageSubscribeNotificationsRequest stackReq)
		{
			logger.debug("Inside handleIncomingSubscribeNotificationsRequest:");
			snrCnt.increment();
			try
			{
				ShSubscribeNotificationRequestImpl request = new ShSubscribeNotificationRequestImpl(Constants.SNR);
				request.setStackObj(stackReq);
				ra.deliverRequest(request);

			}
			catch(Exception ex)
			{
				logger.error("handleIncomingShUserDataRequest() failed: ",ex);
			}
		}
*/
	public void handleIncomingShUserDataRequest(DiameterSession serverSession, DiameterMessage stackReq, String nextHop)
	{
		logger.debug("Inside handleIncomingShUserDataRequest.." + serverSession.getSessionId());

		try
		{
            int type=UDR;
			ShUserDataRequestImpl request = new ShUserDataRequestImpl(type);
			request.setStatelessSenderOriginHost(nextHop);
			request.setStackObj(stackReq);
			request.setServerStackSession(serverSession);
			shResourceAdaptor.deliverRequest(request);

		}
		catch(Exception ex)
		{
			logger.error("handleIncomingUDR() failed: ",ex);
		}
	}


	public void handleRequest(ShRequest request) throws ShResourceException {

		logger.debug("handleRequest(ShRequest) called . Nothing to do. Client should not receive it");
	}

	public void handleResponse(ShResponse response) throws ShResourceException {

		boolean sentSuccessfully = true;
		long handleTimestamp = System.currentTimeMillis();
		if (response == null) {
			logger.error("handleResponse(): null response.");
			return;
		}


		try {

			if (response instanceof ShUserDataResponse) {
				ShUserDataRequestImpl request = (ShUserDataRequestImpl) response.getRequest();
				if (logger.isDebugEnabled()) {
					logger.debug("ShUserDataResponse incoming udr server session id is " + request.getSessionId());
				}
				ShSession shsession = (ShSession) request.getProtocolSession();
				long timestamp = request.getTimestamp();
				// Added processing ts in ShSession
				if (shsession != null) {
					shsession.addTimestamp(request.getType(), handleTimestamp - timestamp);
				}

			request.getServerStackSession().sendMessage(
						((ShUserDataResponseImpl) response).getStackObj());
				logger.debug("ShUserDataAnswer sent successfully sesion deleted");
				request.getServerStackSession().delete();
				/*
				 * Session no longer needed
				 */
			} else if (response instanceof ShProfileUpdateResponse) {
				ShProfileUpdateRequestImpl request = (ShProfileUpdateRequestImpl) ((ShProfileUpdateResponseImpl) response).getRequest();
				if (logger.isDebugEnabled()) {
					logger.debug("ShUserDataResponse incoming udr server session id is " + request.getSessionId());
				}
				ShSession shsession = (ShSession) request.getProtocolSession();
				long timestamp = request.getTimestamp();
				// Added processing ts in ShSession
				if (shsession != null) {
					shsession.addTimestamp(request.getType(), handleTimestamp - timestamp);
				}
				/*DiameterMessage answer = shFactory.createShUserDataAnswer(
						ResultCodes.getReturnCode(5005), supportedFeaturesAns,
						wildcardedPSIAns,
						wildcardedIMPUAns,
						ShUserDataAns);
				answer.setSessionIdAVPValue(diameterMessage.getSessionIdAVPValue());
*/
				request.getServerStackSession().sendMessage(
						((ShProfileUpdateResponseImpl) response).getStackObj());
				logger.debug("ShUserDataAnswer sent successfully sesion deleted");
				request.getServerStackSession().delete();
				/*
				 * Session no longer needed
				 */
			} else if (response instanceof ShSubscribeNotificationResponse) {
				/*ShStackClientInterfaceImpl.stackSession.sendSubscribeNotificationsAnswer(((ShSubscribeNotificationResponseImpl)response).getStackObj());
				logger.debug("ShSubscribeNotificationResponse sent successfully");
				this.updateResponseCounter(SNA, (int)response.getResultCode());
				ShRequest request=(ShRequest)((ShSubscribeNotificationResponseImpl)response).getRequest();
				ShSession shsession=(ShSession)((ShSubscribeNotificationRequestImpl)request).getProtocolSession();
				if(shsession!=null)
					shsession.removeRequest(request);
*/

				ShSubscribeNotificationRequestImpl request = (ShSubscribeNotificationRequestImpl) ((ShSubscribeNotificationResponseImpl) response).getRequest();
				if (logger.isDebugEnabled()) {
					logger.debug("ShUserDataResponse incoming udr server session id is " + request.getSessionId());
				}
				ShSession shsession = (ShSession) request.getProtocolSession();
				long timestamp = request.getTimestamp();
				// Added processing ts in ShSession
				if (shsession != null) {
					shsession.addTimestamp(request.getType(), handleTimestamp - timestamp);
				}
				request.getServerStackSession().sendMessage(
						((ShSubscribeNotificationResponseImpl) response).getStackObj());
				logger.debug("ShUserDataAnswer sent successfully sesion deleted");
				request.getServerStackSession().delete();
				/*
				 * Session no longer needed
				 */
			}

		} catch (Exception ex) {
			logger.error("handleResponse() failed: ", ex);
			sentSuccessfully = false;
		}

		if (!sentSuccessfully) {
			ShResourceEvent resourceEvent = new ShResourceEvent(response,
					ShResourceEvent.RESPONSE_FAIL_EVENT, response.getApplicationSession());
			try {
				this.shResourceAdaptor.deliverEvent(resourceEvent);
			} catch (ResourceException e) {
				logger.error("Exception in delivering ShResourceEvent :: ", e);
				throw new ShResourceException(e);
			}

		}
	}

	public DiameterShMessageFactory getDiameterShMsgFactory(){

		if(logger.isDebugEnabled()){
			logger.debug("getDiameterRoMsgFactory return .." +shFactory);
		}
		return shFactory;
	}
	
	
	@Override
	public DiameterMessage updateCapabilityExchange(DiameterMessage msg, DiameterPeer peer) {
		

		if(logger.isDebugEnabled()){
			logger.debug("updateCapabilityExchange .");
		}
//		if(ShStackConfig.isMutihomingEnabled() && 
//				(ShStackConfig.getLocalClientSecSctpFqdn()!=null 
//				&& !ShStackConfig.getLocalClientSecSctpFqdn().isEmpty())){
//			
//			if(logger.isDebugEnabled()){
//				logger.debug("updateCapabilityExchange ..add secondary sctp host ip");
//			}
//		DiameterAVP avp = stack.getDiameterMessageFactory()
//				.createOctetStringAVP(
//						"Host-IP-Address",
//						"base",ShStackConfig.getLocalClientSecSctpFqdn());
//		msg.add(avp);
//		}

		return  msg;
	}
	
	
	private String printStackStats(){
		if (logger.isDebugEnabled()) {
			logger.debug("printStats() called...");
		}
		StringBuffer buffer=new StringBuffer();
		buffer.append("Number of Routes created: ");
		buffer.append(stack.getMBean().getNbRoutes()).append("\n");;
//		buffer.append("Number of Listen points created: ");
//		buffer.append(stack.getMBean().getNbListeningPoints()).append("\n");;
		buffer.append("Number of Action Sessions: ");
		buffer.append(stack.getMBean().getNbActiveSessions()).append("\n");;
		buffer.append("Number of messages in SCTP Queue: ");
		buffer.append(stack.getMBean().getNbMsgInSCTPInQueue()).append("\n");
		buffer.append("Number of messages in Upstream Queue: ");
		buffer.append(stack.getMBean().getNbMsgInUpStreamQueue()).append("\n");
		buffer.append("Number of Transactions Attempts: ");
		buffer.append(stack.getMBean().getNbTransactionAttempts()).append("\n");;
		buffer.append("Number of Active Peers: ");
		buffer.append(stack.getMBean().getNbActivePeers()).append("\n");;
		buffer.append("Number of Connections Created: ");
		buffer.append(stack.getMBean().getNbConnectionsCreated()).append("\n");	
		buffer.append("Number of Requests rejected due to license:");
		buffer.append(stack.getMBean().getNbRejectedRequestsDiscardedDueToLicense()).append("\n");;
		buffer.append("Number of Transactions: ");
		buffer.append(stack.getMBean().getNbTransactions()).append("\n");;
		buffer.append("Number of Messages in TCP Queue: ");
		buffer.append(stack.getMBean().getNbMsgInTCPInQueue()).append("\n");;
		buffer.append("Number of Current Connections: ");
		buffer.append(stack.getMBean().getNbCurrentConnections()).append("\n");;
		buffer.append("App ThreadPool Active Count: ");
		buffer.append(stack.getMBean().getAppThreadPoolActiveCount()).append("\n");;
		buffer.append("Transport Out ThreadPool Active Count : ");
		buffer.append(stack.getMBean().getTransportOutThreadPoolActiveCount()).append("\n");;
		buffer.append("DownStream ThreadPool Active Count: ");
		buffer.append(stack.getMBean().getDownStreamThreadPoolActiveCount()).append("\n");;
		buffer.append("Transport Out ThreadPool Queue Size: ");
		buffer.append(stack.getMBean().getTransportOutThreadPoolQueueSize()).append("\n");;
		return buffer.toString();
	}

	
	private String printProviderStats(){
		if (logger.isDebugEnabled()) {
			logger.debug("printStats() called...");
		}
		StringBuffer buffer=new StringBuffer();
		buffer.append("Number of Routes created: ");
		buffer.append(stack.getMBean().getNbRoutes()).append("\n");;
		buffer.append("Number of Session Created: ");
		buffer.append(shProvider.getMBean().getNbSessionsCreated()).append("\n");;
		buffer.append("Number of Requests Received: ");
		buffer.append(shProvider.getMBean().getNbRequestRcvd()).append("\n");;
		buffer.append("Number of Response Sent: ");
		buffer.append(shProvider.getMBean().getNbResponsesSent()).append("\n");;
		buffer.append("Number of Requests Sent: ");
		buffer.append(shProvider.getMBean().getNbRequestSent()).append("\n");;
		buffer.append("Number of Response Received: ");
		buffer.append(shProvider.getMBean().getNbResponsesRcvd()).append("\n");;
		buffer.append("Average Response Receive Time: ");
		buffer.append(shProvider.getMBean().getAvgRespRecvTime()).append("\n");;
		buffer.append("Average Response Process Time: ");
		buffer.append(shProvider.getMBean().getAvgRespProcessTime()).append("\n");
		buffer.append("Number of Current Session : ");
		buffer.append(shProvider.getMBean().getNbCurrentSessions()).append("\n");
		buffer.append("Number of Dropped Messages: ");
		buffer.append(shProvider.getMBean().getNbDroppedMessages()).append("\n");;
		buffer.append("Number of Dropped Messages Forwarded : ");
		buffer.append(shProvider.getMBean().getNbDroppedMessagesForwarded()).append("\n");;
		buffer.append("Number of Dropped Messages Sent : ");
		buffer.append(shProvider.getMBean().getNbDroppedMessagesSent()).append("\n");	
		buffer.append("Number of Messages in Applictaion IN Queue:");
		buffer.append(shProvider.getMBean().getNbMsgInAppInQueue()).append("\n");;
		buffer.append("Number of Messages in Applictaion OUT Queue: ");
		buffer.append(shProvider.getMBean().getNbMsgInAppOutQueue()).append("\n");;
		buffer.append("Number of Timeouts: ");
		buffer.append(shProvider.getMBean().getNbTimeOuts()).append("\n");;
		buffer.append("Number of Retransmissions: ");
		buffer.append(shProvider.getMBean().getNbRetransmission()).append("\n");;
		buffer.append("Number of Failed Messages Sending: ");
		buffer.append(shProvider.getMBean().getNbFailedMessagesSending()).append("\n");;
		return buffer.toString();
	}

	
	private String printPeerStats(){
		if (logger.isDebugEnabled()) {
			logger.debug("printPeerStats() called...");
		}
		StringBuffer buffer=new StringBuffer();
		DiameterPeer route=peersList.get(0);
		if(route!=null){
		
		buffer.append("Peer State is : ");
		buffer.append(route.getMBean().getPeerState()).append("\n");;
		buffer.append("Peer Realm: ");
		buffer.append(route.getMBean().getPeerRealm()).append("\n");;
		buffer.append("Number of Requests Sent: ");
		buffer.append(route.getMBean().getPeerNbRequestsSent()).append("\n");;
		buffer.append("Number of Responses Received: ");
		buffer.append(route.getMBean().getPeerNbResponsesRcvd()).append("\n");;
		buffer.append("Number of Requests Received : ");
		buffer.append(route.getMBean().getPeerNbRequestsRcvd()).append("\n");	
		buffer.append("Number of Responses Sent :");
		buffer.append(route.getMBean().getPeerNbResponsesSent()).append("\n");;
		buffer.append("Number of Messages in Peer In Queue: ");
		buffer.append(route.getMBean().getNbMsgInPeerInQueue()).append("\n");;
		buffer.append("Number of Messages in Peer Out Queue: ");
		buffer.append(route.getMBean().getNbMsgInPeerOutQueue()).append("\n");;
		buffer.append("Number of Messages in Peer Message In Queue: ");
		buffer.append(route.getMBean().getNbMsgInPeerMessageInQueue()).append("\n");;
		buffer.append("Number of Messages in Peer Pending Request Queue: ");
		buffer.append(route.getMBean().getNbMsgPeerPendingReqQueue()).append("\n");;
		buffer.append("Number Of Transport Send Failure: ");
		buffer.append(route.getMBean().getNbTransportSendFailure()).append("\n");
		buffer.append("Number of Transport Send Success : ");
		buffer.append(route.getMBean().getNbTransportSendSuccess()).append("\n");
		buffer.append("Peer Message Counter: ");
		buffer.append(route.getMBean().getPeerMessagesCounter()).append("\n");;
		buffer.append("Peer Busy Counter : ");
		buffer.append(route.getMBean().getPeerBusyCounter()).append("\n");;
		buffer.append("Peer Infinite loop counter : ");
		buffer.append(route.getMBean().getPeerInfiniteLoopCounter()).append("\n");;
		buffer.append("Peer Protocol State : ");
		buffer.append(route.getMBean().getPeerProtocolState()).append("\n");;
		buffer.append("Peer Watchdog FSM State: ");
		buffer.append(route.getMBean().getPeerWatchdogFSMState()).append("\n");;
		}else{
			buffer.append("Could not find any Peer in route list !!!");
		}
		return buffer.toString();
	}
	public String execute(String cmd, String[] args, InputStream in,
			OutputStream out) throws CommandFailedException {
		if (logger.isDebugEnabled()) {
			logger.debug("execute() called..." + cmd);
		}
		try {
			if (cmd.equals(SH_STACK_STATS)) {
				return printStackStats();
			} else if (cmd.equals(SH_ROUTE_INFO)) {
				return getRouteDetails();
			}else if (cmd.equals(SH_APP_STATS)) {
				return printProviderStats();
			}else if (cmd.equals(SH_PEER_STATS)) {
				return printPeerStats();
			}else{
				return getUsage(cmd);
			}
			
		} catch (Exception e) {
			logger.error("execute"+e.toString(), e);

			return e.getMessage();
		} 

	}

//	public String execute(short cmd, String command, String[] args,
//			InputStream is, OutputStream os) throws CommandFailedException {
//
//		if(logger.isDebugEnabled()){
//			logger.debug("execute command "+ command);
//		}
//		try {
//			if (command.equals(SH_CLIENT_INFO)) {
//				return printStats();
//			} else if (command.equals(SH_ROUTE_INFO)) {
//				return getRouteDetails();
//			}else{
//				return getUsage(command);
//			}
//			
//		} catch (Exception e) {
//			logger.error("execute"+e.toString(), e);
//
//			return e.getMessage();
//		}
//	}

	@Override
	public String getUsage(String command) {
		// TODO Auto-generated method stub
		return "Supported Commands are : "+ SH_STACK_STATS+" , "+ SH_ROUTE_INFO + " and " + SH_APP_STATS+" !!!";
	}
	
	   private String getRouteDetails(){
		
		   StringBuffer buffer=new StringBuffer();
		   if(!routesList.isEmpty()){
		for (int i = 0; i <= (routesList.size() - 1); i++) {
			DiameterRoute route = routesList.get(i);
			buffer.append("Diameter Route "+(i+1 ) +" :--->\n");	
			buffer.append("Realm : "+ route.getRealm()+" , ");
			buffer.append("FQDN : "+ route.getRemotePeerFQDN()+" , ");
			buffer.append("URI : "+ route.getRemotePeerURI()+" , ");
			buffer.append("Metric : "+ route.getMetric()+" , ");
			if(route.getRouteState().equals(DiameterRoute.RouteState.available.name())){
			 buffer.append("State : Available\n");
			}else{
				 buffer.append("State : Not available\n");
			}
		}
		   }else{
			   buffer.append("No Routes found !!");
		   }
		return buffer.toString();
	}
	   
	   public static List<DiameterRoute> getRoutes(){
		  return routesList;
	   }

}

// getNextHandle() not required
// addRequestToMap() not required