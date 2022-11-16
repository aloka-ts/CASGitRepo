package com.baypackets.ase.ra.diameter.gy.stackif;

import java.io.File;
import java.net.SocketAddress;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.gy.GyResourceException;
import com.baypackets.ase.ra.diameter.gy.enums.CCRequestTypeEnum;
import com.baypackets.ase.ra.diameter.gy.impl.GySession;
import com.baypackets.ase.ra.diameter.gy.utils.Constants;
import com.baypackets.ase.ra.diameter.gy.CreditControlAnswer;
import com.baypackets.ase.ra.diameter.gy.GyRequest;
import com.baypackets.ase.ra.diameter.gy.GyResourceEvent;
import com.baypackets.ase.ra.diameter.gy.GyResourceFactory;
import com.baypackets.ase.ra.diameter.gy.GyResponse;
import com.baypackets.ase.ra.diameter.gy.GyResourceAdaptor;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.util.TimerService;
import com.baypackets.ase.util.AseAlarmService;
import com.traffix.openblox.core.enums.ApplicationType;
import com.traffix.openblox.core.enums.Standard;
import com.traffix.openblox.core.exceptions.ValidationException;
import com.traffix.openblox.core.fsm.stack.StackState;
import com.traffix.openblox.core.session.SessionFactory;
import com.traffix.openblox.core.transport.Peer;
import com.traffix.openblox.core.transport.PeerStateListener;
import com.traffix.openblox.core.transport.PeerTable;
import com.traffix.openblox.core.transport.PeerTableListener;
import com.traffix.openblox.core.transport.Stack;
import com.traffix.openblox.core.transport.TransportStack;
import com.traffix.openblox.core.utils.configuration.Configuration;
import com.traffix.openblox.core.utils.configuration.ConfigurationType;
import com.traffix.openblox.core.utils.configuration.MutableConfigurationImpl;
import com.traffix.openblox.diameter.coding.DiameterAnswer;
import com.traffix.openblox.diameter.coding.DiameterRequest;
import com.traffix.openblox.diameter.enums.PcbState;
import com.traffix.openblox.diameter.gy.generated.event.MessageASA;
import com.traffix.openblox.diameter.gy.generated.GyStandardLoader;
import com.traffix.openblox.diameter.gy.generated.enums.EnumCCRequestType;
import com.traffix.openblox.diameter.gy.generated.enums.EnumRequestedAction;
import com.traffix.openblox.diameter.gy.generated.event.MessageCCR;
import com.traffix.openblox.diameter.gy.generated.session.SessionGyServer;
import com.traffix.openblox.diameter.session.DiameterSession;
import com.traffix.openblox.diameter.utils.DiameterApplicationId;


// Referenced classes of package com.baypackets.ase.ra.diameter.sh.stackif:
//            SessionListenerFactoryShServerImpl, ShProfileUpdateRequestImpl, ShUserDataRequestImpl

public class GyStackServerInterfaceImpl implements Constants {

	private static Logger logger = Logger.getLogger(GyStackServerInterfaceImpl.class);
	private static GyResourceFactory raFactory;
	private static TimerService timerService;
	private static long timeout = 30L;
	private int handleCount;

	public Stack serverStack;
	public SessionFactory sessionFactory;
	public static SessionGyServer stackSession=null;
	public Peer serverPeer;
	public static String serverRealm;
	public static String serverHost;
	private String serverConfFile;
	public static final DiameterApplicationId applicationId;
	private GyResourceAdaptor ra;
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

	static 
	{
		applicationId = new DiameterApplicationId(Standard.Gy.applicationId, ApplicationType.Auth);
	}

	static class ServerPeerStateListenerImpl implements PeerStateListener
	{
		public void stateChanged(Enum oldState, Enum newState)
		{
			if(newState == PcbState.DOWN)
				GyStackServerInterfaceImpl.logger.info((new StringBuilder()).append(" ************** ").append(peer).append(" is DOWN *************** ").toString());
			else
				if(newState == PcbState.OKAY ||newState == PcbState.REOPEN){
					GyStackServerInterfaceImpl.logger.info((new StringBuilder()).append(" ************** ").append(peer).append(" is OKAY ************** ").toString());
			}			
		}
		
		// added in 3.2.8 version
		public void stateChanged(Enum oldState, Enum newState, Enum event)
		{
			if(newState == PcbState.DOWN)
				GyStackServerInterfaceImpl.logger.info((new StringBuilder()).append(" ************** ").append(peer).append(" is DOWN *************** ").toString());
			else
				if(newState == PcbState.OKAY ||newState == PcbState.REOPEN){
					GyStackServerInterfaceImpl.logger.info((new StringBuilder()).append(" ************** ").append(peer).append(" is OKAY ************** ").toString());
			}			
		}

		public void originStateIdChanged(long oldValue, long newValue)
		{
			GyStackServerInterfaceImpl.logger.info((new StringBuilder()).append("Origin-State-Id of ").append(peer).append(" changed from ").append(oldValue).append(" to ").append(newValue).toString());
		}

		private final Peer peer;

		public ServerPeerStateListenerImpl(Peer peer)
		{
			this.peer = peer;
		}
	}

	static class ServerPeerTableListenerImpl implements PeerTableListener
	{

		public void peerAccepted(Peer peer)
		{
			GyStackServerInterfaceImpl.logger.info((new StringBuilder()).append("Accepted ").append(peer).toString());
			peer.addPeerStateListener(new ServerPeerStateListenerImpl(peer));
		}

		public void peerRemoved(Peer peer)
		{
			GyStackServerInterfaceImpl.logger.info((new StringBuilder()).append("Removed ").append(peer).toString());
		}

		public void connectionRejected(SocketAddress remoteSocketAddress)
		{
			GyStackServerInterfaceImpl.logger.info((new StringBuilder()).append("Rejected ").append(remoteSocketAddress).toString());
		}

		ServerPeerTableListenerImpl()
		{
		}
	}



	public GyStackServerInterfaceImpl(GyResourceAdaptor ra)
	{
		handleCount = 0;
		this.ra = ra;
		outgoingRequests = new Hashtable(0x10000);
	}

	public void init(ResourceContext context) throws GyResourceException
	{
		alarmService = (AseAlarmService)context.getAlarmService();
		raFactory = (GyResourceFactory)context.getResourceFactory();
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

		serverConfFile = (new StringBuilder()).append(context.getConfigProperty("ase.home")).append(File.separator).append("conf").append(File.separator).append("gyServer.cfg").toString();

		logger.debug((new StringBuilder()).append("Use [").append(serverConfFile).append("]").toString());
		init(serverConfFile);

	}

	public void init(String cfgFile) throws GyResourceException
	{
		try
		{
			Configuration configuration1 = (Configuration)MutableConfigurationImpl.createConfiguration(cfgFile, ConfigurationType.Xml);
			logger.debug("creating server transport stack");
			serverStack = new TransportStack();

			sessionFactory = serverStack.init(configuration1);

			GyStandardLoader.load(serverStack, new SessionListenerFactoryGyServerImpl(this));
			logger.debug("serverStack loaded successfully");
		}
		catch(Exception ex)
		{
			logger.error("GyResourceFactory.init() failed.");
			logger.error(ex.getMessage(), ex);
			throw new GyResourceException("GyResourceFactory.init() failed.");
		}
	}

	public void start()	throws GyResourceException
	{
		logger.debug("Inside GyStackInterfaceImpl start()");

		try
		{
			PeerTable peerTable1 = serverStack.getPeerTable();
			peerTable1.addPeerTableListener(new ServerPeerTableListenerImpl());
			StackState state1 = serverStack.start();
		}
		catch(Exception ex)
		{
			logger.error("GyResourceFactory.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new GyResourceException("GyResourceFactory.stop() failed.");
		}

		try {
			stackSession = (SessionGyServer)sessionFactory.getNewSession(Standard.Gy);
		} catch (ValidationException e) {
			logger.error("Exception in craeating stack session ", e);
		}

		stackSession.setPerformFailover(true);
	}

	public void stop() throws GyResourceException
	{
		logger.info("stop(): closing Diameter stack...");

		try
		{
			StackState state = serverStack.stop(5L, TimeUnit.SECONDS);
			if(state != StackState.Stopped)
			{
				logger.debug((new StringBuilder()).append("State is ").append(state).append(", Stack failed to stop ").append(serverStack).toString());
				throw new GyResourceException("GyResourceFactory.stop() failed.");
			}
			logger.info("stop(): Diameter stack is closed");
		}
		catch(Exception ex)
		{
			logger.error("GyResourceFactory.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new GyResourceException("GyResourceFactory.init() failed.");
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

	public void addRequestToMap(int handle, GyRequest request)
	{
		logger.debug((new StringBuilder()).append("request map size before adding is :[ ").append(outgoingRequests.size()).append("]").toString());
		outgoingRequests.put(new Integer(handle), request);
		logger.debug((new StringBuilder()).append("request map size after adding is :[ ").append(outgoingRequests.size()).append("]").toString());
	}

	public void receivedErrorMessageServerMode(DiameterSession diametersession, DiameterRequest diameterrequest, DiameterAnswer diameteranswer)
	{
		//TODO
	}

	public void timeoutExpiredServerMode(DiameterSession diametersession, DiameterRequest diameterrequest)
	{
		//TODO
	}

	public void handleRequest(GyRequest request) throws GyResourceException {		

		logger.debug("handleRequest(GyRequest) called ");
	}

	public void handleResponse(GyResponse response) throws GyResourceException {

		boolean sentSuccessfully = true;

		if (response == null) {
			logger.error("handleResponse(): null response.");
			return;
		}

		try {

			if (response instanceof GyAbstractResponse) {

				CCRequestTypeEnum recordType = ((CreditControlAnswer)response).getEnumCCRequestType();
				long resultCode = ((CreditControlAnswer)response).getResultCode();

				switch (recordType){

				case EVENT_REQUEST:
					this.updateResponseCounter(EVENT, (int) resultCode);
					break;
				case INITIAL_REQUEST:
					this.updateResponseCounter(SESSION, (int) resultCode);
					break;
				case UPDATE_REQUEST:
					this.updateResponseCounter(SESSION, (int) resultCode);
					break;
				case TERMINATION_REQUEST:
					this.updateResponseCounter(SESSION, (int) resultCode);
					break;
				default:
					logger.error("Wrong/Unkown type request received.");
					throw new ResourceException("Wrong/Unkown response type.");

				}

				GyStackServerInterfaceImpl.stackSession.sendCCA(((CreditControlAnswerImpl)response).getStackObj());	
				GyRequest request=(GyRequest)((CreditControlAnswerImpl)response).getRequest();
				GySession GySession=(GySession)((CreditControlRequestImpl)request).getProtocolSession();
				GySession.removeRequest(request);
				logger.debug("CreditControlAnswer sent successfully");
			} 			
		} catch (ValidationException ex) {

			logger.error("ValidationException in sending Ro response ",ex);
			sentSuccessfully=false;

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
	
	public void incrementRequestedActionCounter(MessageCCR request) throws GyResourceException, ValidationException 
	{
		logger.debug("Inside incrementRequestedActionCounter()");
		EnumRequestedAction type=request.getEnumRequestedAction();
			if(type.equals(EnumRequestedAction.DIRECT_DEBITING)){
				this.ccrDirectDebitCnt.increment();
				logger.debug("Requested Action Direct Debiting incrementing Counter");
			}else if(type.equals(EnumRequestedAction.REFUND_ACCOUNT)){
				this.ccrAccountRefundCnt.increment();
				logger.debug("Requested Action Account Refund incrementing Counter");
			}else if(type.equals(EnumRequestedAction.CHECK_BALANCE)){
				this.ccrBalanceCheckCnt.increment();
				logger.debug("Requested Action Balance Check incrementing Counter");
			}else if(type.equals(EnumRequestedAction.PRICE_ENQUIRY)){
				this.ccrPriceEnquiryCnt.increment();
				logger.debug("Requested Action Price Enquiry incrementing Counter");
			}else {
				logger.error("Wrong/Unkown requested Action type.");
				
			}
			logger.debug("incrementRequestedActionCounter() exit");
		}
	
	public void handleIncomingCCR(SessionGyServer serverSession, MessageCCR stackReq)
	{
		logger.debug("Inside handleIncomingCCR..");

		try
		{
			int type = -1;

			logger.debug("sssssssssssssssss");
			EnumCCRequestType recordType = stackReq.getEnumCCRequestType();

			logger.debug("tttttttttttt");
			switch (recordType){

			case EVENT_REQUEST:
				logger.debug("11111111");
				this.ccrEventCnt.increment();
				this.incrementRequestedActionCounter(stackReq);
				type = EVENT_REQUEST;
				break;
			case INITIAL_REQUEST:
				logger.debug("222222");
				this.ccrFirstInteroCnt.increment();
				this.ccrSessionCnt.increment();
				type = INITIAL_REQUEST;
				break;
			case UPDATE_REQUEST:
				logger.debug("33333333");
				this.ccrInterimInteroCnt.increment();
				this.ccrSessionCnt.increment();
				type = UPDATE_REQUEST;
				break;
			case TERMINATION_REQUEST:
				logger.debug("44444444444");
				this.ccrFinalInteroCnt.increment();
				this.ccrSessionCnt.increment();
				type = TERMINATION_REQUEST;
				break;
			default:
				logger.error("Wrong/Unkown type request received.");
				throw new ResourceException("Wrong/Unkown response type.");
			}

			CreditControlRequestImpl request = new CreditControlRequestImpl(type);
			request.setStackObj(stackReq);
			ra.deliverRequest(request);

		}
		catch(Exception ex)
		{
			logger.error("handleIncomingUserDataRequest() failed: ",ex);
		}
	}

	public void handleIncomingASA(SessionGyServer serverSession,
			MessageASA answer) {
		// TODO Auto-generated method stub

	}
}
