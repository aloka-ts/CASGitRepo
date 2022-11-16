package com.baypackets.ase.ra.ro.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.ra.ro.*;
import com.baypackets.ase.resource.ResourceEvent;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.spi.resource.ResourceAdaptor;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CliInterface;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.measurement.MeasurementCounter;
import com.baypackets.ase.spi.replication.ReplicationEvent;

public class RoResourceAdaptorImpl
	implements RoResourceAdaptor, ResourceAdaptor, CommandHandler, Constants {
	private static Logger logger = Logger.getLogger(RoResourceAdaptorImpl.class);

	private static ResourceContext context;
	private static RoStackInterface stackInterface;
	private short role = ResourceAdaptor.ROLE_ACTIVE;

	private boolean dfnUp = false;
	//private List downDFNs = new LinkedList(2);
	//private Map downPeers = new Hashtable();
	private boolean canSendMessage = true;
	private boolean peerDfnUp = true;


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

	public RoResourceAdaptorImpl() {
		super();
	}

	/////////////////////////////// ResourceAdaptor methods begin ////////////////////////////////

	public void init(ResourceContext context) throws ResourceException {
		this.context = context;		
		((RoResourceFactoryImpl)this.context.getResourceFactory()).init(this.context);
		
		// Get configuration role
		this.role = context.getCurrentRole();

		logger.debug("The system is " + (this.role == ResourceAdaptor.ROLE_ACTIVE ? "active" : "standby"));
		
		// Load Ro stack interface
		logger.debug("init(): load stack interface.");

		try {
			this.stackInterface = RoStackInterfaceFactory.getInstance().loadStackInterface(context, this);
		} catch(Throwable t) {
			logger.error("init(): Ro stack loading", t);
			throw new ResourceException(t.toString());
		}

		// Initialize Ro stack interface
		logger.debug("init(): initialize stack interface.");
		try {
			this.stackInterface.init(context);
		} catch (Exception ex) {
			logger.error("Ro stack initialization", ex);
			throw new ResourceException(ex);
		}

		logger.debug("Initialize measurement counters.");

		MeasurementManager measurementMgr = context.getMeasurementManager();

		this.ccrEventCnt = measurementMgr.getMeasurementCounter(CCR_EVENT_COUNTER);
		this.ccrSessionCnt = measurementMgr.getMeasurementCounter(CCR_SESSION_COUNTER);

		this.ccrDirectDebitCnt = measurementMgr.getMeasurementCounter(CCR_DIRECT_DEBIT_COUNTER);
		this.ccrAccountRefundCnt = measurementMgr.getMeasurementCounter(CCR_ACCOUNT_REFUND_COUNTER);
		this.ccrBalanceCheckCnt = measurementMgr.getMeasurementCounter(CCR_BALANCE_CHECK_COUNTER);
		this.ccrPriceEnquiryCnt = measurementMgr.getMeasurementCounter(CCR_PRICE_ENQUIRY_COUNTER);
		this.ccrFirstInteroCnt = measurementMgr.getMeasurementCounter(CCR_FIRST_INTEROGATION_COUNTER);
		this.ccrInterimInteroCnt = measurementMgr.getMeasurementCounter(CCR_INTERIM_INTEROGATION_COUNTER);
		this.ccrFinalInteroCnt = measurementMgr.getMeasurementCounter(CCR_FINAL_INTEROGATION_COUNTER);

		this.ccaEvent1xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_1XXX_COUNTER);
		this.ccaEvent2xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_2XXX_COUNTER);
		this.ccaEvent3xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_3XXX_COUNTER);
		this.ccaEvent4xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_4XXX_COUNTER);
		this.ccaEvent5xxxCnt = measurementMgr.getMeasurementCounter(CCA_EVENT_5XXX_COUNTER);

		this.ccaSession1xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_1XXX_COUNTER);
		this.ccaSession2xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_2XXX_COUNTER);
		this.ccaSession3xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_3XXX_COUNTER);
		this.ccaSession4xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_4XXX_COUNTER);
		this.ccaSession5xxxCnt = measurementMgr.getMeasurementCounter(CCA_SESSION_5XXX_COUNTER);

		this.ccrSendErrorCnt = measurementMgr.getMeasurementCounter(CCR_SEND_ERROR);
	}

	public void start() throws ResourceException {
		if (this.role != ResourceAdaptor.ROLE_ACTIVE) {
			logger.error("I am standby... doing nothing");
			return;
		}

		logger.debug("Start stack interface...");
		try {
			this.stackInterface.start();
			this.dfnUp = true; // TODO - what about with 1 DFN setup? Or when only single DFN is up in 2 DFN setup?
		} catch (Exception ex) {
			logger.error("Ro stack interface start() failed: ", ex);
			return;
		}

		logger.debug("Stack interface is started.");
	}

	public void stop() throws ResourceException 
	{
		try {
			if(this.dfnUp == true ) {	
			logger.debug(" stoping stack " );
			this.stackInterface.stop();
			}
		} catch (Exception ex) {
			logger.error("Ro stack interface stop() failed: ", ex);
			throw new ResourceException(ex);
		}

		logger.info("Stack Interface successfully stopped");
	}

	public void configurationChanged(String arg0, Object arg1) throws ResourceException {
	}

	public void roleChanged(String clusterId, String subsystemId, short role) {
		if(logger.isDebugEnabled()) {
			logger.debug("roleChanged(): role is changed to " + role);
		}

		short preRole = this.role;
		this.role = role;
		if(preRole != ROLE_ACTIVE && role == ROLE_ACTIVE) {
			try {
				this.start();
			} catch (Exception e) {
				logger.error("roleChanged(): ", e);
			}
		}
	}

	public void sendMessage(SasMessage message) throws IOException {
		if(logger.isDebugEnabled()) {
			logger.debug("Sending message: " + message);
		}

		try {
			if(message instanceof RoRequest) {
				if(this.canSendMessage) {
					
					if (((RoRequest) message).getSession() != null) {
						replicate((RoRequest) message);
					}
					this.stackInterface.handleRequest((RoRequest)message);
					this.updateRequestCounter(((RoRequest)message).getType());
				} else {
					logger.debug("CDF is disconnected... cannot send request");
					this.ccrSendErrorCnt.increment();
				}
			} else if (message instanceof RoResponse) {
				logger.error("No response should be sent by Ro applications");
				throw new IllegalArgumentException("No response can be sent");
			} else {
				logger.error("Message dropped: not an Ro message.");
				throw new IllegalArgumentException("Not an Ro message");
			}
		} catch (RoStackException ex) {
			this.ccrSendErrorCnt.increment();
			logger.error("sendMessage() failed ", ex);			
			throw new IllegalArgumentException(ex.getMessage());
		}
	}

	public void processed(SasMessage arg0) {
	}

	public void failed(SasMessage arg0, AseInvocationFailedException arg1) {
		logger.error("Message failed", arg1);
	}

	/////////////////////////////// ResourceAdaptor methods end //////////////////////////////////

	/////////////////////////////// RoResourceAdaptor methods begin //////////////////////////////

	public void deliverResponse(RoResponse response) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("deliverResponse(): type = " + response.getType() +
										", resultCode = " + response.getResultCode());
		}

		if( ( ((CreditControlMessage)response).getCCRequestType() == Constants.CCRT_EVENT_REQUEST) || 
				( ((CreditControlMessage)response).getCCRequestType() == Constants.CCRT_TERMINATION_REQUEST ))
		{
			if(logger.isDebugEnabled()) {
			logger.debug(" setting RoSession state to inactive ");
			}
			((RoSession)response.getSession()).setRoState(RoSession.RO_INACTIVE);
		}

		// Update Received Answer Counter
		this.updateAnswerCounter(	((CreditControlMessage)response).getCCRequestType(),
								response.getResultCode());
		try {
			this.context.deliverMessage(response, true);
		} catch(Throwable t) {
			logger.error("Delivering response to application", t);
		}
	}

	public void deliverEvent(RoResourceEvent event) throws ResourceException {
		if(logger.isDebugEnabled()) {
			logger.debug("deliverEvent(): event [" + event + "]");
		}
		boolean deliverUpward = true;
		if(event.getApplicationSession() == null )
		{
			logger.debug("app session is null");
			deliverUpward = false;
		}
			logger.debug("app session is not null");
		
		if(event.getType().equals(RoResourceEvent.RO_TX_TIMEROUT_EVENT)) {
			((RoSession)(event.getMessage()).getSession()).setRoState(RoSession.RO_INACTIVE);
		} else if(event.getType().equals(RoResourceEvent.RO_INTERIM_TIMEOUT_EVENT)) {
		} else if(event.getType().equals(RoResourceEvent.RO_NOTIFY_DISCONNECT_PEER_REQUEST)) {
		} else if(event.getType().equals(RoResourceEvent.RO_DISCONNECT_PEER_RESPONSE)) {
		} else if(event.getType().equals(RoResourceEvent.RO_NOTIFY_DFN_DOWN)) {
		} else if(event.getType().equals(RoResourceEvent.RO_NOTIFY_PEER_DOWN)) {
		} else if(event.getType().equals(RoResourceEvent.RO_NOTIFY_PEER_UP)) {
		} else if(event.getType().equals(RoResourceEvent.RO_NOTIFY_UNEXPECTED_MESSAGE)) {
		} else if(event.getType().equals(RoResourceEvent.RO_HB_HID_ERROR)) {
			((RoSession)(event.getMessage()).getSession()).setRoState(RoSession.RO_INACTIVE);
		} else if(event.getType().equals(RoResourceEvent.RO_ETE_ID_ERROR)) {
			((RoSession)(event.getMessage()).getSession()).setRoState(RoSession.RO_INACTIVE);
		} else if(event.getType().equals(RoResourceEvent.RO_SESSION_STR_ERRROR)) {
			((RoSession)(event.getMessage()).getSession()).setRoState(RoSession.RO_INACTIVE);
		} else if(event.getType().equals(RoResourceEvent.RO_AAA_XML_VALIDATION_ERROR)) {
			((RoSession)(event.getMessage()).getSession()).setRoState(RoSession.RO_INACTIVE);
		} else if(event.getType().equals(RoResourceEvent.RO_AAA_INVALID_AVP_VALUE)) {
			((RoSession)(event.getMessage()).getSession()).setRoState(RoSession.RO_INACTIVE);
		} else if(event.getType().equals(RoResourceEvent.RO_UNKNOWN_REQ_TYPE)) {
		} else if(event.getType().equals(RoResourceEvent.RO_UNEXPECTED_REQUESTED_ACTION)) {
		} else if(event.getType().equals(RoResourceEvent.RO_REDIRECT_NOTIFICATION)) {
		} else if(event.getType().equals(RoResourceEvent.RO_GRANT_ENDUSER_SERVICE)) {
		} else if(event.getType().equals(RoResourceEvent.RO_TERMINATE_ENDUSER_SERVICE)) {
		} else {
		}




		if(event.getType().equals("RF_NOTIFY_DISCONNECT_PEER_REQUEST"))
		{
			this.canSendMessage= false;
		}
		else if (event.getType().equals("RF_DISCONNECT_PEER_RESPONSE"))
		{
			this.canSendMessage= false;
		}

		else if (event.getType().equals("RF_NOTIFY_DFN_DOWN"))
		{
			this.dfnUp = false;
		}

		else if(event.getType().equals("RO_NOTIFY_DFN_UP"))
		{
			logger.debug("notify dfn up event received.");
			if(this.dfnUp = false)
			{
				logger.debug(" starting stack:");
				try{
					this.start();
				} catch ( Exception e){
					logger.error("dfn up:", e);
				}
			}
		}

		else if (event.getType().equals("RF_NOTIFY_PEER_UP"))
		{
			this.peerDfnUp = true;
		}

		else if (event.getType().equals("RF_NOTIFY_PEER_DOWN"))
		{
			this.peerDfnUp = false;
		}

		else if (event.getType().equals("RF_NOTIFY_UNEXPECTED_MESSAGE"))
		{
		}

		else if (event.getType().equals("RF_NOTIFY_FAILOVER"))
		{
		}

		else
			;

		if(deliverUpward == true){
			if (this.context != null) {
				logger.debug("deliverEvent():call context.");
				this.context.deliverEvent(event, true);
			}
		}
		
	}

	/////////////////////////////// RoResourceAdaptor methods end ////////////////////////////////

	///////////////////////////////// CommandHandler methods begin ///////////////////////////////

	public String getUsage(String command) {
		return new String("Not supported yet");
	}

	public String execute(String command, String[] args, InputStream in, OutputStream out)
		throws CommandFailedException {
		return new String("Not supported yet");
	}

	///////////////////////////////// CommandHandler methods end /////////////////////////////////

	public static ResourceContext getResourceContext() {
		return RoResourceAdaptorImpl.context;
	}

	public static RoStackInterface getStackInterface() {
		return RoResourceAdaptorImpl.stackInterface;
	}
	
	public void replicate( RoRequest request )
	{
		ReplicationEvent event = new ReplicationEvent(request.getSession() , "RESOURCE");
		((RoSession)request.getSession()).sendReplicationEvent(event);
	}

	private void updateRequestCounter(int reqType) {
		switch(reqType) {
			case RO_FIRST_INTERROGATION:
				this.ccrFirstInteroCnt.increment();
				this.ccrSessionCnt.increment();
			break;
			case RO_INTERMEDIATE_INTERROGATION:
				this.ccrInterimInteroCnt.increment();
				this.ccrSessionCnt.increment();
			break;
			case RO_FINAL_INTERROGATION:
				this.ccrFinalInteroCnt.increment();
				this.ccrSessionCnt.increment();
			break;
			case RO_DIRECT_DEBITING:
				this.ccrDirectDebitCnt.increment();
				this.ccrEventCnt.increment();
			break;
			case RO_REFUND_ACCOUNT:
				this.ccrAccountRefundCnt.increment();
				this.ccrEventCnt.increment();
			break;
			case RO_CHECK_BALANCE:
				this.ccrBalanceCheckCnt.increment();
				this.ccrEventCnt.increment();
			break;
			case RO_PRICE_ENQUERY:
				this.ccrPriceEnquiryCnt.increment();
				this.ccrEventCnt.increment();
			break;
			default:
				logger.error("updateRequestCounter: invalid request type");
		}
	}

	private void updateAnswerCounter(short type, long resultCode) {
		switch(type) {

			case CCRT_EVENT_REQUEST:
				switch((int)(resultCode/1000)) {
					case 1:
						this.ccaEvent1xxxCnt.increment();
					break;
					case 2:
						this.ccaEvent2xxxCnt.increment();
					break;
					case 3:
						this.ccaEvent3xxxCnt.increment();
					break;
					case 4:
						this.ccaEvent4xxxCnt.increment();
					break;
					case 5:
						this.ccaEvent5xxxCnt.increment();
					break;
					default:
						logger.error("updateAnswerCounter: invalid event result code");
				}
			break;

			case CCRT_INITIAL_REQUEST:
			case CCRT_UPDATE_REQUEST:
			case CCRT_TERMINATION_REQUEST:
				switch((int)(resultCode/1000)) {
					case 1:
						this.ccaSession1xxxCnt.increment();
					break;
					case 2:
						this.ccaSession2xxxCnt.increment();
					break;
					case 3:
						this.ccaSession3xxxCnt.increment();
					break;
					case 4:
						this.ccaSession4xxxCnt.increment();
					break;
					case 5:
						this.ccaSession5xxxCnt.increment();
					break;
					default:
						logger.error("updateAnswerCounter: invalid session result code");
				}
			break;

			default:
				logger.error("updateAnswerCounter: invalid type");
		}
	}
}
