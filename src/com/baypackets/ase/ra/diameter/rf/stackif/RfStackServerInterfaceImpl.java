package com.baypackets.ase.ra.diameter.rf.stackif;

import java.io.File;
import java.net.SocketAddress;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.baypackets.ase.ra.diameter.rf.RfAccountingResponse;
import com.baypackets.ase.ra.diameter.rf.RfRequest;
import com.baypackets.ase.ra.diameter.rf.RfResourceAdaptor;
import com.baypackets.ase.ra.diameter.rf.RfResourceEvent;
import com.baypackets.ase.ra.diameter.rf.RfResourceException;
import com.baypackets.ase.ra.diameter.rf.RfResourceFactory;
import com.baypackets.ase.ra.diameter.rf.RfResponse;
import com.baypackets.ase.ra.diameter.rf.enums.AccountingRecordTypeEnum;
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
import com.traffix.openblox.diameter.rf.generated.RfStandardLoader;
import com.traffix.openblox.diameter.rf.generated.enums.EnumAccountingRecordType;
import com.traffix.openblox.diameter.rf.generated.event.MessageACR;
import com.traffix.openblox.diameter.rf.generated.event.MessageASA;
import com.traffix.openblox.diameter.rf.generated.session.SessionRfServer;
import com.traffix.openblox.diameter.session.DiameterSession;
import com.traffix.openblox.diameter.utils.DiameterApplicationId;

// Referenced classes of package com.baypackets.ase.ra.diameter.sh.stackif:
//            SessionListenerFactoryShServerImpl, ShProfileUpdateRequestImpl, ShUserDataRequestImpl

public class RfStackServerInterfaceImpl implements Constants {

	private static Logger logger = Logger.getLogger(RfStackServerInterfaceImpl.class);
	private static RfResourceFactory raFactory;
	private static TimerService timerService;
	private static long timeout = 30L;
	private int handleCount;

	public Stack serverStack;
	public SessionFactory sessionFactory;
	public static SessionRfServer stackSession=null;
	public Peer serverPeer;
	public static String serverRealm;
	public static String serverHost;
	private String serverConfFile;
	public static final DiameterApplicationId applicationId;
	private RfResourceAdaptor ra;
	private Map outgoingRequests;
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

	static 
	{
		applicationId = new DiameterApplicationId(Standard.Rf.applicationId, ApplicationType.Auth);
	}

	static class ServerPeerStateListenerImpl implements PeerStateListener
	{

		public void stateChanged(Enum rfEvent, Enum oldState, Enum newState)
		{
			if(newState == PcbState.DOWN)
				RfStackServerInterfaceImpl.logger.info((new StringBuilder()).append(" ************** ").append(peer).append(" is DOWN *************** ").toString());
			else
				if(newState == PcbState.OKAY ||newState == PcbState.REOPEN)
					RfStackServerInterfaceImpl.logger.info((new StringBuilder()).append(" ************** ").append(peer).append(" is OKAY ************** ").toString());
		}

		public void originStateIdChanged(long oldValue, long newValue)
		{
			RfStackServerInterfaceImpl.logger.info((new StringBuilder()).append("Origin-State-Id of ").append(peer).append(" changed from ").append(oldValue).append(" to ").append(newValue).toString());
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
			RfStackServerInterfaceImpl.logger.info((new StringBuilder()).append("Accepted ").append(peer).toString());
			peer.addPeerStateListener(new ServerPeerStateListenerImpl(peer));
		}

		public void peerRemoved(Peer peer)
		{
			RfStackServerInterfaceImpl.logger.info((new StringBuilder()).append("Removed ").append(peer).toString());
		}

		public void connectionRejected(SocketAddress remoteSocketAddress)
		{
			RfStackServerInterfaceImpl.logger.info((new StringBuilder()).append("Rejected ").append(remoteSocketAddress).toString());
		}

		ServerPeerTableListenerImpl()
		{
		}
	}



	public RfStackServerInterfaceImpl(RfResourceAdaptor ra)
	{
		handleCount = 0;
		this.ra = ra;
		outgoingRequests = new Hashtable(0x10000);
	}

	public void init(ResourceContext context) throws RfResourceException
	{
		alarmService = (AseAlarmService)context.getAlarmService();
		raFactory = (RfResourceFactory)context.getResourceFactory();
		timerService = context.getTimerService();

		logger.debug("Initialize measurement counters.");
		MeasurementManager measurementMgr = context.getMeasurementManager();
		this.acrEventCnt = measurementMgr.getMeasurementCounter(Constants.ACR_EVENT_COUNTER_IN);
		this.acrSessionCnt = measurementMgr.getMeasurementCounter(Constants.ACR_SESSION_COUNTER_IN);
		this.acaEvent1xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_1xxx_COUNTER_OUT);
		this.acaEvent2xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_2xxx_COUNTER_OUT);
		this.acaEvent3xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_3xxx_COUNTER_OUT);
		this.acaEvent4xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_4xxx_COUNTER_OUT);
		this.acaEvent5xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_EVENT_5xxx_COUNTER_OUT);
		this.acaSession1xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_1xxx_COUNTER_OUT);
		this.acaSession2xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_2xxx_COUNTER_OUT);
		this.acaSession3xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_3xxx_COUNTER_OUT);
		this.acaSession4xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_4xxx_COUNTER_OUT);
		this.acaSession5xxxCnt = measurementMgr.getMeasurementCounter(Constants.ACA_SESSION_5xxx_COUNTER_OUT);

		serverConfFile = (new StringBuilder()).append(context.getConfigProperty("ase.home")).append(File.separator).append("conf").append(File.separator).append("rfServer.cfg").toString();

		logger.debug((new StringBuilder()).append("Use [").append(serverConfFile).append("]").toString());
		init(serverConfFile);

	}

	public void init(String cfgFile) throws RfResourceException
	{
		try
		{
			Configuration configuration1 = (Configuration)MutableConfigurationImpl.createConfiguration(cfgFile, ConfigurationType.Xml);
			logger.debug("creating server transport stack");
			serverStack = new TransportStack();

			sessionFactory = serverStack.init(configuration1);

			RfStandardLoader.load(serverStack, new SessionListenerFactoryRfServerImpl(this));
			logger.debug("serverStack loaded successfully");
		}
		catch(Exception ex)
		{
			logger.error("RfResourceFactory.init() failed.");
			logger.error(ex.getMessage(), ex);
			throw new RfResourceException("RfResourceFactory.init() failed.");
		}
	}

	public void start()	throws RfResourceException
	{
		logger.debug("Inside RfStackInterfaceImpl start()");

		try
		{
			PeerTable peerTable1 = serverStack.getPeerTable();
			peerTable1.addPeerTableListener(new ServerPeerTableListenerImpl());
			StackState state1 = serverStack.start();
		}
		catch(Exception ex)
		{
			logger.error("RfResourceFactory.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new RfResourceException("RfResourceFactory.stop() failed.");
		}

		try {
			stackSession = (SessionRfServer)sessionFactory.getNewSession(Standard.Rf);
		} catch (ValidationException e) {
			logger.error("Exception in craeating stack session ", e);
		}

		stackSession.setPerformFailover(true);
	}

	public void stop() throws RfResourceException
	{
		logger.info("stop(): closing Diameter stack...");

		try
		{
			StackState state = serverStack.stop(5L, TimeUnit.SECONDS);
			if(state != StackState.Stopped)
			{
				logger.debug((new StringBuilder()).append("State is ").append(state).append(", Stack failed to stop ").append(serverStack).toString());
				throw new RfResourceException("RfResourceFactory.stop() failed.");
			}
			logger.info("stop(): Diameter stack is closed");
		}
		catch(Exception ex)
		{
			logger.error("RfResourceFactory.start() failed.");
			logger.error(ex.getMessage(), ex);
			throw new RfResourceException("RfResourceFactory.init() failed.");
		}
	}

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


	private synchronized int getNextHandle()
	{
		return handleCount++;
	}

	public void addRequestToMap(int handle, RfRequest request)
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

	public void handleRequest(RfRequest request) throws RfResourceException {		

		logger.debug("handleRequest(RfRequest) called ");
	}

	public void handleResponse(RfResponse response) throws RfResourceException {

		boolean sentSuccessfully = true;

		if (response == null) {
			logger.error("handleResponse(): null response.");
			return;
		}

		try {

			if (response instanceof RfAbstractResponse) {

				AccountingRecordTypeEnum recordType = ((RfAccountingResponse)response).getEnumAccountingRecordType();
				long resultCode = ((RfAccountingResponse)response).getResultCode();

				int type = -1;

			switch (recordType){

				case EVENT_RECORD:
					type = EVENT_RECORD;
					this.updateResponseCounter(EVENT, (int) resultCode);
					break;
				case START_RECORD:
					type = START_RECORD;
					this.updateResponseCounter(SESSION, (int) resultCode);
					break;
				case INTERIM_RECORD:
					type = INTERIM_RECORD;
					this.updateResponseCounter(SESSION, (int) resultCode);
					break;
				case STOP_RECORD:
					type = STOP_RECORD;
					this.updateResponseCounter(SESSION, (int) resultCode);
					break;
				default:
					logger.error("Wrong/Unkown type request received.");
					throw new ResourceException("Wrong/Unkown request type.");

				}

				RfStackServerInterfaceImpl.stackSession.sendACA(((RfAccountingResponseImpl)response).getStackObj());
				RfRequest request=(RfRequest)((RfAccountingResponseImpl)response).getRequest();
				RfSession rfsession=(RfSession)((RfAccountingRequestImpl)request).getProtocolSession();
				if(rfsession!=null)
					rfsession.removeRequest(request);
				logger.debug("RfAccountingResponse sent successfully");
			} 			
		} catch (ValidationException ex) {

			logger.error("ValidationException in sending Rf response ",ex);
			sentSuccessfully=false;

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

	public void handleIncomingACR(SessionRfServer serverSession, MessageACR stackReq)
	{
		logger.debug("Inside handleIncomingACR");

		try
		{
			int type = -1;

			EnumAccountingRecordType recordType = stackReq.getEnumAccountingRecordType();

			switch (recordType){

			case EVENT_RECORD:
				type = EVENT_RECORD;
				this.acrEventCnt.increment();
				break;
			case START_RECORD:
				type = START_RECORD;
				this.acrSessionCnt.increment();
				break;
			case INTERIM_RECORD:
				type = INTERIM_RECORD;
				this.acrSessionCnt.increment();
				break;
			case STOP_RECORD:
				type = STOP_RECORD;
				this.acrSessionCnt.increment();

			}

			RfAccountingRequestImpl request = new RfAccountingRequestImpl(type);
			request.setStackObj(stackReq);
			ra.deliverRequest(request);
			
		}
		catch(Exception ex)
		{
			logger.error("handleIncomingUserDataRequest() failed: ",ex);
		}
	}

	public void handleIncomingASA(SessionRfServer serverSession,
			MessageASA answer) {
		// TODO Auto-generated method stub

	}
}
