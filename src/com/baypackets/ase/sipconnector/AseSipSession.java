/*
 * AseSipSession.java
 *
 * Created on August 20, 2004
 */

package com.baypackets.ase.sipconnector;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.B2buaHelper;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipErrorEvent;
import javax.servlet.sip.SipErrorListener;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipSessionActivationListener;
import javax.servlet.sip.SipSessionAttributeListener;
import javax.servlet.sip.SipSessionBindingEvent;
import javax.servlet.sip.SipSessionBindingListener;
import javax.servlet.sip.SipSessionEvent;
import javax.servlet.sip.SipSessionListener;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TelURL;
import javax.servlet.sip.URI;
import javax.servlet.sip.ar.SipApplicationRoutingRegion;

import com.esotericsoftware.kryo.DefaultSerializer;
import com.esotericsoftware.kryo.serializers.ExternalizableSerializer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.cdr.CDRImpl;
import com.baypackets.ase.common.AseBaseConnector;
import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.AseEvent;
import com.baypackets.ase.common.AseEventListener;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.AseLockException;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseBaseRequest;
import com.baypackets.ase.container.AseBaseResponse;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseEngine;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.container.AseIc;
import com.baypackets.ase.container.AseProtocolSession;
import com.baypackets.ase.container.AseThreadData;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.container.sip.AseSipAppCompositionHandler;
import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.dispatcher.Destination;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.ocm.OverloadControlManager;
import com.baypackets.ase.replication.ReplicationManagerImpl;
import com.baypackets.ase.sbb.CDR;
import com.baypackets.ase.spi.replication.ReplicableList;
import com.baypackets.ase.spi.replication.ReplicableMap;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.replication.ReplicationManager;
import com.baypackets.ase.spi.replication.ReplicationSet;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.EvaluationVersion;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransaction;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipFromHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderInterface;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeaderList;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRouteHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipToHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTransportType;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipViaHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsURI;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;


/**
 * This is the SIP protocol specific implementation of the AseProtocolSession
 * object. This will mantain the dialog state for the associated SIP dialog.
 * The SIP dialog state is maintained by <code>AseSipSessionStateImpl</code> class.  
 */

@DefaultSerializer(ExternalizableSerializer.class)
public class AseSipSession extends AseProtocolSession
		implements SipSession, AseEventListener, Cloneable,
		AseSipSessionState, AseSipInvitationHandlerInterface,
		AseSipSubscriptionHandlerInterface,
		AseSip100RelHandlerInterface,
		AseSipReplicationHandlerInterface {

	private static final long serialVersionUID = 3848858488843L;
	// Need to replicate Client Transaction on Standby SAS
	private DsSipClientTransaction txn = null;
	
	public DsSipClientTransaction getTxn() {
		return txn;
	}

	public void setTxn(DsSipClientTransaction txn) {
		this.txn = txn;
	}
	
	private static ConfigRepository m_configRepository	= (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

	private static String transReplication = (String) m_configRepository.getValue(Constants.TRANS_REPLICATION);
	
	String privateIps=    m_configRepository.getValue(Constants.OID_MULTIHOME_PVT_IP_LIST);
	
	private static String writeDefaultCDR = (String) m_configRepository.getValue(Constants.WRITE_DEFAULT_CDR);
	
	private static short sipSignalingInfoListSize=12;
	
	static{
		try{
			String listSize = (String) m_configRepository.getValue(Constants.SIP_SIGNALING_INFO_LIST_MAXSIZE);
			sipSignalingInfoListSize=Short.valueOf(listSize);
		}catch(Exception e){
			sipSignalingInfoListSize=12;
		}
	}
	
	private transient boolean transReplicated = false;
	
	private boolean readTransaction = false;
	
	//private transient boolean rprHandlerReplicated = false;
	private boolean readRprHandler = false;
	
	private transient boolean invHandlerReplicated = false;
	private boolean readInvHandler = false;
	
	private transient boolean b2bHandlerReplicated = false;
	private boolean readB2bHandler = false;
	
	private static final String MESSAGE_MAP = "MESSAGE_MAP".intern();
	
	private int responseClass = 0;
	
	
	private ReplicationManager replicationMgr;
	
	private boolean isOutstanding = false;
	
	private int initialRequestMessageId = 0;
	
	private transient boolean isActivatedOnFt = false; 
	
	private static AseHost host = (AseHost)Registry.lookup(Constants.NAME_HOST);
	
	public int getInitialRequestMessageId() {
		return initialRequestMessageId;
	}

	public void setInitialRequestMessageId(int initialRequestMessageId) {
		this.initialRequestMessageId = initialRequestMessageId;
	}

	
	/**
	 * Constructor. Takes the AseSipConnector as a paramater.
	 * Generates a unique sessionid. Initializes all the parameters.
	 */
	AseSipSession(AseSipConnector connector) {
		super("AseSipSession_"+host.getSubsystemId()+allocateId());
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering Constructor AseSipSession host.getSubsystemId() : " + host.getSubsystemId());

		if (EvaluationVersion.FLAG) {
			if (AseMeasurementUtil.counterActiveSIPSessions.getCount() >=
				Constants.EVAL_VERSION_MAX_SIP_SESSION) {
				m_logger.error("SAS evaluation version active SIP sessions limit exceeded.");
				throw new IllegalStateException("Max active SIP session limit exceeded!!!");
			}
		}
		
		logId = " AseSipSession Id = [" + getId() + AseStrings.SQUARE_BRACKET_CLOSE;
		m_logId = " AseSipSession Id = [" + getId() + AseStrings.SQUARE_BRACKET_CLOSE;
		logCallId = " Call Id = [UNDEFINED]";

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering Constructor" + getLogId());

		m_sipConnector = connector;
		m_engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
		
		//Create a subscription handler....
		ReplicableList subscriptionList = new ReplicableList(SUBSCRIPTION_LIST);
		this.setReplicable(subscriptionList);
		m_subscriptionHandler.setSubscriptionList(subscriptionList);

		//Set the parent session for the 100rel handler... 
		m_100RelHandler.setParentSession(this);

		// Create and setup the session state object
		m_sessionState = new AseSipSessionStateImpl();
		m_sessionState.setParentSession(this);
		m_sessionState.setReplicableId(SESSION_STATE);
		this.setReplicable(m_sessionState);


		// Register the two handlers with the session state as
		// respective dialog reference managers
		m_sessionState.registerDialogReferenceManager(m_invitationHandler);
		m_sessionState.registerDialogReferenceManager(m_subscriptionHandler);

		//Increment the counters for the SIP Sessions
		AseMeasurementUtil.counterActiveSIPSessions.increment();
		AseMeasurementUtil.counterTotalSIPSessions.increment();
		AseMeasurementUtil.thresholdSIPSession.increment();

		//Increment for the Overload control
		// Commented - moved to all the places from where this constructor is called
		// need to identify between normal/NSEP session. info not available here
		// ocmManager.increase(ocmId);

		//Set the b2b handler for this session;
		m_b2bHandler = new AseB2bSessionHandler();

		m_sach = AseSipAppCompositionHandler.getInstance();
		
		replicationMgr = (ReplicationManager)Registry.lookup(Constants.NAME_REPLICATION_MGR);

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving Constructor" + getLogId());
	}

	public AseSipSession() {
		super("Replicated");

		m_engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
		m_sach = AseSipAppCompositionHandler.getInstance();
		
		//FT Handling strategy Update: Replication will be done for the provisional
		//responses as well, so need to replicate the list for pending messages corresponding
		//to INVITE request. There by AseB2bSessionHandler will get replicated so no need
		//to create a new instance when sip session object is deserialised
		//Set the b2b handler for this session;
		//TODO: Commenting again as for transient calls we need not to hold the calls
		m_b2bHandler = new AseB2bSessionHandler();
	}

	public void partialActivate(ReplicationSet parent) {
		if (m_logger.isDebugEnabled()) m_logger.debug("Entering AseSipSession partialActivate().");
		super.partialActivate(parent);
		if (appSession != null){
			if (m_logger.isDebugEnabled())
				m_logger.debug("Initial Request Message Id " + this.initialRequestMessageId);
			ReplicableMap repMap = (ReplicableMap) (((AseApplicationSession) appSession).getReplicable(MESSAGE_MAP));
			this.m_origRequest = (AseSipServletRequest) repMap.get(this.initialRequestMessageId);
			//TODO: This is done to recreate the invitation handler rather serializing it
			if(isOutstanding){
				if (m_invitationHandler != null && m_origRequest != null){
					m_invitationHandler.addOutstandingRequest(m_origRequest);
					if (m_logger.isDebugEnabled())
						m_logger.debug("Request is also added to the outstanding list ");
				}
			}
		}

		if (m_logger.isDebugEnabled()) m_logger.debug("Leaving AseSipSession partialActivate().");
	}

	public void activate(ReplicationSet parent) {
		if (EvaluationVersion.FLAG) {
			if (AseMeasurementUtil.counterActiveSIPSessions.getCount() >=
				Constants.EVAL_VERSION_MAX_SIP_SESSION) {
				m_logger.error("SAS evaluation version active SIP sessions limit exceeded.");
				throw new IllegalStateException("Max active SIP session limit exceeded!!!");
			}
		}


		logId = " AseSipSession Id = [" + getId() + AseStrings.SQUARE_BRACKET_CLOSE;
		m_logId = " AseSipSession Id = [" + getId() + AseStrings.SQUARE_BRACKET_CLOSE;
		logCallId = " Call Id = [UNDEFINED]";

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering activate" + getLogId());

		if(null == m_sipConnector) {
			// Now get reference to m_sipConnector
			AseBaseConnector connector = null;
			Iterator iterator = m_engine.getConnectors();
			for(;iterator != null && iterator.hasNext();){
				connector = (AseBaseConnector) iterator.next();
				if(connector.getProtocol().equals(Constants.PROTOCOL_SIP_2_0)){
					m_sipConnector = (AseSipConnector)connector;
					break;
				}
			}
		}

		//FT Handling strategy Update: Replication will be done for the provisional
		//responses as well, so need to replicate the client transaction on 
		//standby SAS only in case on INVITE request
		//THis has been moved to the AseSipServletRequest Activation as there we need to have
		//transaction for sending the cancel after failover from Standby SAS
		/*if(this.getTxn() instanceof AseSipClientTransactionIImpl) {
			m_logger.debug("Client Transaction instance of AseSipClientTransactionIImpl");
			
			try {
				m_logger.debug("Starting Client Transaction");
				this.getTxn().startAfterFailover();
				//DsSipTransactionManager.addTransaction(this.getTxn(),true,true); // client and useVia				
			}catch (Exception e) {
				m_logger.error("Exception in Starting the client transaction." + getLogId(), e);
			}

			// Need to recreate the AseSipClientTransportInfo.instance() as this is the transient 
			// attribute of the client transaction.
			// Need to recreate the AseSipClientTransactionListener as this is also transient attribute 

			((AseSipClientTransactionIImpl)this.getTxn()).setClientTransportInfo(AseSipClientTransportInfo.instance());
			((AseSipClientTransactionIImpl) this.getTxn())
					.setClientInterface(new AseSipClientTransactionListener(
							AseStackInterfaceLayer.getInstance(),
							AseStackInterfaceLayer.getInstance().getM_factory()));
			//This is done to ensure that Service should get this attribute in case of client transaction 
			//i.e., when invite is sent. Now in case of FT Service needs to send the CANCEL.
			//Before this change service was getting the request from attribute in the app session
			//that object doesn't have the client transaction. The same thing is being done for INVITE
			//coming in.
			((AseSipServletRequest)this.getAttribute(Constants.ORIG_REQUEST)).setClientTxn(this.getTxn());
			this.m_origRequest.setClientTxn(this.getTxn());
			m_logger.debug("Client Transaction Reincarnated and Started");
		}*/
		super.activate(parent);

		m_sach = AseSipAppCompositionHandler.getInstance();
		
		//FT Handling strategy Update: Replication will be done for the provisional
		//responses as well, so need to add the session to the INVITE dialog to handle
		//the PRACK received at standby node 
		m_sach.activateSession(this);
		
		if (null == m_sipConnector) {
			m_logger.error("SIP connector ref is NULL inside " +
					"activate" + getLogId());
			m_logger.error("Some strange problem, shouldn't happen " +
					"- recovery not possible" + getLogId());
			return ;
		}
		
		if(AseProtocolSession.VALID == state && !isActivatedOnFt){
			if(appSession==null){
				AseHost host = (AseHost)Registry.lookup(Constants.NAME_HOST);
				appSession = (AseApplicationSession)host.getApplicationSession(this.appSessionId);
			}
			isActivatedOnFt = true;
			if (appSession != null && appSession.isValid()&&appSession.getAttribute(Constants.DIALOGUE_ID) ==null) {
				((AseApplicationSession) appSession)
						.incrementActivatedSipSessions();

				// Increment the counters for the SIP Sessions
				AseMeasurementUtil.counterActiveSIPSessions.increment();
				
			} else {
				if(appSession==null){
					m_logger.error("Appsesion is null for Sipsession::"
							+ this.getId() + "  Appsess ID::" + this.appSessionId);
				}else if(m_logger.isDebugEnabled()){
					m_logger.debug("Tcap Sipsession::"
							+ this.getId() + "  Appsess ID::" + this.appSessionId);
				}
						
			}
			if(appSession.isValid()){ //application may invalidate app session on activating sipsession above so this chk is added
			  AseMeasurementUtil.counterTotalSIPSessions.increment();
			  AseMeasurementUtil.thresholdSIPSession.increment();
			}
		}
		//Increment for the Overload control
		if(((AseApplicationSession)this.getApplicationSession()).getInitialPriorityStatus()) {
			ocmManager.increaseNSEP(ocmId);
		} else {
			ocmManager.increase(ocmId);
		}

		// Notify SipSessionActivationListeners of this session's activation
		genActivationEvent();        

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving activate" + getLogId());
	}


	public void readIncremental(ObjectInput in) throws IOException, ClassNotFoundException {
		//This is commented as whole object is now replicated
		//this.m_invitationHandler.setInvitationCount(in.readInt());
		
		int replicationType = in.readInt();
		
		if(replicationType == ReplicationEvent.TYPE_REGULAR) {
		
			this.m_subscriptionHandler.setFirstRefer(in.readBoolean());
			// This is done to avoid null pointer exception as zero length
			// string was added at write side.
			//this.linkedSessionId = in.readUTF();
			this.linkedSessionId = in.readUTF();
			if(linkedSessionId.length()==0){
				this.linkedSessionId=null;
			}
			//FT Handling Strategy Update: Replicating the cseq handler entry in order
			//to support the SAS Failover after sending or receiving provisional responses.
	
			readRprHandler = in.readBoolean();
			
			if(transReplication.equals(AseStrings.TRUE_SMALL) && readRprHandler){
				if(m_logger.isDebugEnabled())
					m_logger.debug("Reading RPR Handler");
				this.m_rprHandlers = (HashMap)in.readObject();
			}
			
			if (this.m_rprHandlers != null) {
				m_100RelHandler.m_rprHandlers.clear();
				Set<Entry<Long, AseSip100RelHandler.TransactionRPRHandler>> entrySet = this.m_rprHandlers
						.entrySet();
				for (Map.Entry<Long, AseSip100RelHandler.TransactionRPRHandler> entry : entrySet) {
					if (m_100RelHandler.m_rprHandlers.get(entry.getKey()) == null) {
						m_100RelHandler.m_rprHandlers.put(entry.getKey(),
								entry.getValue());
						m_100RelHandler
								.setLocalRSeq(((AseSip100RelHandler.TransactionRPRHandler) entry
										.getValue()).rSeq + 1000);
					}
				}
			}
			//To replicate success requests in order to handle case of sending ACk after 
			//failover happens
			//TODO: This is now being recreated
			/*this.readInvHandler = in.readBoolean();
			if(transReplication.equals(AseStrings.TRUE_SMALL) && readInvHandler){
				if(m_logger.isDebugEnabled())
					m_logger.debug("Reading Invitation Handler");
				this.m_invitationHandler = (AseSipInvitationHandler) in.readObject();
			}*/
	
			//To retrieve pending messages corresponding to UAC and UAS
			/*this.readB2bHandler = in.readBoolean();
			if(transReplication.equals(AseStrings.TRUE_SMALL) && readB2bHandler){
				if(m_logger.isDebugEnabled())
					m_logger.debug("Reading B2bSession Handler");
				this.m_b2bHandler = (AseB2bSessionHandler) in.readObject();
			}*/
			//THis was done as part of reduction of Replication data
			//this.m_transResponse = (AseSipServletResponse) in.readObject();
			
			//Need to check with Application Team, do we have to replicate
			//this again as there might be some attributes added
			//this.m_origRequest = (AseSipServletRequest) in.readObject();
			
			this.readTransaction = in.readBoolean();
			if(transReplication.equals(AseStrings.TRUE_SMALL) && readTransaction){
				if(m_logger.isDebugEnabled())
					m_logger.debug("Reading Client Transaction");
				this.txn = (DsSipClientTransaction)in.readObject();
			}else {
				if (this.txn != null){
					AseSipTransaction aseTxn = (AseSipTransaction) txn;
					boolean cancelable = in.readBoolean(); 
					if(cancelable)
						aseTxn.setCancellable();
				}
			}
			// Added try catch to support upgrade from SAS7.5.11.56 to SAS7.5.11.57 
			// To be removed in future releases.
			try{	 
				this.m_sipSignalingInfoList=(AseEvictingQueue<AseSipMessageInfo>) in.readObject();
				this.responseClass = in.readInt();
			}catch(OptionalDataException optionalDataException){
				this.responseClass = in.readInt();
			}catch(Exception e){
				m_logger.error("Error in readIncremental() exception occured: "+e.getMessage(),e);
			}
			

			isOutstanding = in.readBoolean();
			
			this.initialRequestMessageId = in.readInt();
			
			//This is done to ignore the replication of original request rather 
			//getting the request reference from the message map of the application session
		}
		
		super.readIncremental(in);
		
		if(m_sessionState == null) {
			m_sessionState = (AseSipSessionStateImpl)this.getReplicable(SESSION_STATE);
			if(m_sessionState != null) {
				m_sessionState.setParentSession(this);
				m_sessionState.registerDialogReferenceManager(m_invitationHandler);
				m_sessionState.registerDialogReferenceManager(m_subscriptionHandler);
			} else {
				m_logger.error("readIncremental(): m_sessionState is null");
			}
		}

		// If dialog is not already added into Dialog Manager, add it if
		// dialog is established

		if(m_sessionState != null) {
			if(m_isAddedIntoDMgr == false) {
				if((m_sessionState.getSessionState() == AseSipSessionState.STATE_EARLY)
						|| (m_sessionState.getSessionState() == AseSipSessionState.STATE_CONFIRMED)) {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Adding dialogs to dialog mgr " +
								getLogId());

					m_sipConnector.addSession(this);
					m_isAddedIntoDMgr = true;
				}
			} else {
				if((m_sessionState.getSessionState() == AseSipSessionState.STATE_INITIAL)
						|| (m_sessionState.getSessionState() == AseSipSessionState.STATE_TERMINATED)) {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Removing dialogs from dialog mgr " +
								getLogId());

					m_sipConnector.removeSession(this);
					m_isAddedIntoDMgr = false;
				}
			}
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving readIncremental" + getLogId());

	}

	public void writeIncremental(ObjectOutput out, int replicationType) {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering writeIncremental()" +
					getLogId());

		try {			
			// Bug 13141
			out.writeInt(replicationType);
				
			if(replicationType == ReplicationEvent.TYPE_REGULAR) {
			
				//This is removed as whole object is now replicated
				//out.writeInt(this.m_invitationHandler.getInvitationCount());
				out.writeBoolean(this.m_subscriptionHandler.isFirstRefer());
				// This is done to avoid null pointer exception at read side as if we write 
				//  null value. This value will not be null only in case if B2BHeler is used.
				if(this.linkedSessionId==null){
					out.writeUTF(AseStrings.BLANK_STRING);
				}else{
					out.writeUTF(this.linkedSessionId);
				}
				//FT Handling Strategy Update: Replicating the cseq handler entry in order
				//to support the SAS Failover after sending or receiving provisional responses.
				
				if (transReplication.equals(AseStrings.TRUE_SMALL) && this.m_rprHandlers != null && this.m_rprHandlers.size() > 0){
					//rprHandlerReplicated = true;
					readRprHandler = true;
					out.writeBoolean(readRprHandler);
					out.writeObject(this.m_rprHandlers);
				}else{
					readRprHandler = false;
					out.writeBoolean(readRprHandler);
				}
				
				//this.m_rprHandlers = null;
				//To replicate success requests in order to handle case of sending ACk after 
				//failover happens
				/*if(transReplication.equals(AseStrings.TRUE_SMALL) && !invHandlerReplicated){
					invHandlerReplicated = true;
					readInvHandler = true;
					out.writeBoolean(readInvHandler);
					out.writeObject(m_invitationHandler);
				}else{
					readInvHandler = false;
					out.writeBoolean(readInvHandler);
				}*/
				
				//To retrieve pending messages corresponding to UAC and UAS
				/*if(transReplication.equals(AseStrings.TRUE_SMALL) && !b2bHandlerReplicated){
					b2bHandlerReplicated = true;
					readB2bHandler = true;
					out.writeBoolean(readB2bHandler);
					out.writeObject(m_b2bHandler);
				}else{
					readB2bHandler = false;
					out.writeBoolean(readB2bHandler);
				}*/
				
				//out.writeObject(m_transResponse);
				
				//Need to check with Application Team, do we have to replicate
				//this again as there might be some attributes added
				//out.writeObject(m_origRequest);
				
				if(transReplication.equals(AseStrings.TRUE_SMALL) && !transReplicated && txn != null){
					transReplicated = true;
					readTransaction = true;	
					out.writeBoolean(readTransaction);
					out.writeObject(txn);
					if(m_logger.isDebugEnabled())
						m_logger.debug("Replicating Client Transaction");
				}else{
					readTransaction = false;	
					out.writeBoolean(readTransaction);
					//UAT-779, In order to make transaction cancellable we need to replicate the
					//Cancellable property of the transaction as well second time
					if (txn != null){
						AseSipTransaction aseTxn = (AseSipTransaction) txn;
						out.writeBoolean(aseTxn.isCancellable());
					}
				}
				if (m_transResponse != null)
					responseClass = m_transResponse.getResponseClass();
				
				out.writeObject(m_sipSignalingInfoList);
				out.writeInt(responseClass);
				
				if(m_invitationHandler.isRequestOutstanding(m_origRequest.getDsRequest().getCSeqNumber())){
					if(m_logger.isDebugEnabled())
						m_logger.debug("Outstanding request");
					isOutstanding = true;
				}else{
					isOutstanding = false;
				}
				out.writeBoolean(isOutstanding);
				out.writeInt(initialRequestMessageId);
			}
			
			super.writeIncremental(out, replicationType);
		}
		catch (Exception e) {
			m_logger.error("Exception in writeIncremental. " + getLogId(), e);
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving writeIncremental()" + getLogId());
	}


	public void writeExternal(ObjectOutput out) throws IOException{
		if(m_logger.isDebugEnabled())
			m_logger.debug("Entering writeExternal()" +getLogId());

		// Notify SipSessionActivationListeners before serializing
		
		try {
			
			if(m_logger.isDebugEnabled())
				m_logger.debug("Entering writeExternal() write callid and contextid " +this.getCallId()+" contextid is " +((AseApplicationSession) this
						.getApplicationSession()).getIc().getId());
			
//			if (ReplicationManagerImpl.isreplicationEnabled) {
//				replicationMgr
//						.getRedisWrapper()
//						.getValueOperations()
//						.setValue(
//								this.getCallId(),
//								((AseApplicationSession) this
//										.getApplicationSession()).getIc()
//										.getId());
//
//				replicationMgr
//						.getRedisWrapper()
//						.getListOperations()
//						.pushInList(host.getSubsystemId() + ReplicationManagerImpl.REPL_CALLIDS,
//								this.getCallId(), "LEFT");
//			}
		} catch (Exception e) {
			m_logger.error(
					"Exception in writeExternal writing to redis wrapper . "
							+ getLogId(), e);
		}
		
		if (m_logger.isDebugEnabled()){
			m_logger.debug("setFirstReplicationCompleted(true); ");
		}
		this.setFirstReplicationCompleted(true);
 
		genPassivationEvent();

		out.writeInt(role);
		
		out.writeObject(m_sipSignalingInfoList);
		
		out.writeObject(routerStateInfo);
		
		out.writeObject(routingRegion);
		// This is done to avoid null pointer exception at read side as if we write 
		//  null value. This value will not be null only in case if B2BHeler is used.
		if(this.linkedSessionId==null){
			out.writeUTF(AseStrings.BLANK_STRING);
		}else{
			out.writeUTF(this.linkedSessionId);
		}
		out.writeBoolean(this.invalidateWhenReady);
		
		//FT Handling strategy Update: Replication will be done for the provisional
		//responses as well, so need to replicate the client transaction on 
		//standby SAS only in case of INVITE request,the outstanding requests
		//and server transaction for sending the response for the INVITE request
		//received.
		//Removing the check whether transaction is replicated or not because of the following
		//Race Condition in case of bulk replication:
		//If Satndby SAS restarts more than once during the call processing then Optional 
		//Data exception will come because transReplicated will be true with first bulk replication
		//Now when second bulk replication will happen then transaction will not be replicated
		//and standby will not have transaction as it was restarted.
		
		if(transReplication.equals(AseStrings.TRUE_SMALL) && txn != null){
			transReplicated = true;
			readTransaction = true;
			out.writeBoolean(readTransaction);
			out.writeObject(txn);
			if(m_logger.isDebugEnabled())
				m_logger.debug("Replicating Client Transaction");
		}else{
			readTransaction = false;
			out.writeBoolean(readTransaction);
			//Here, it is just precautionary as in situations there is a possibility that
			//readIncremental is called before readExternal
			//UAT-779, In order to make transaction cancellable we need to replicate the
			//Cancellable property of the transaction as well second time
			if (txn != null){
				AseSipTransaction aseTxn = (AseSipTransaction) txn;
				out.writeBoolean(aseTxn.isCancellable());
			}
		}

		if (m_transResponse != null)
			responseClass = m_transResponse.getResponseClass();
		
		out.writeInt(responseClass);
		
		if(m_origRequest != null && m_invitationHandler.isRequestOutstanding(m_origRequest.getDsRequest().getCSeqNumber())){
			if(m_logger.isDebugEnabled())
				m_logger.debug("Outstanding request");
			isOutstanding = true;
		}else{
			isOutstanding = false;
		}
		out.writeBoolean(isOutstanding);	
		
		out.writeInt(initialRequestMessageId);
		
		super.writeExternal(out);

		if(m_logger.isDebugEnabled())
			m_logger.debug("Leaving writeExternal()" + getLogId());
	}

	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException{
		m_logger.debug("Entering readExternal");
		role = in.readInt();
		
		Object object = in.readObject();
		
		if(object instanceof AseEvictingQueue){
			this.m_sipSignalingInfoList=(AseEvictingQueue<AseSipMessageInfo>) object;
			routerStateInfo = (Serializable)in.readObject();
		}else{
			routerStateInfo = (Serializable)object;
		}
		
		routingRegion = (SipApplicationRoutingRegion)in.readObject();
		// This is done to avoid null pointer exception as zero length
		// string was added at write side.
		//this.linkedSessionId = in.readUTF();
		
		this.linkedSessionId = in.readUTF();
		if(linkedSessionId.length()==0){
			this.linkedSessionId=null;
		}
		isFirstToInvalidate = false;
		this.invalidateWhenReady = in.readBoolean();
		
		//FT Handling strategy Update: Replication will be done for the provisional
		//responses as well, so need to replicate the client transaction on 
		//standby SAS only in case of INVITE request,the outstanding requests
		//and server transaction for sending the response for the INVITE request
		//received.
		
			
		this.readTransaction = in.readBoolean();
		if(transReplication.equals(AseStrings.TRUE_SMALL) && readTransaction){
			if(m_logger.isDebugEnabled())
				m_logger.debug("Reading Client Transaction");
			this.txn = (DsSipClientTransaction)in.readObject();
		}else{
			if (this.txn != null){
				AseSipTransaction aseTxn = (AseSipTransaction) txn;
				boolean cancelable = in.readBoolean(); 
				if(cancelable)
					aseTxn.setCancellable();
			}
		}
		
		if (this.txn != null){
			((AseSipTransaction)this.txn).setSipSession(this);
		}
		
		//This is used in the ACK timeout or server transaction timeout
		this.responseClass = in.readInt();
		this.isOutstanding = in.readBoolean();
		
		//This is done to handle the stack overflow error as sip session and sip response
		//references made transient in server transaction
		if (this.m_invitationHandler != null){
			for (Object obj : this.m_invitationHandler.m_outstandingRequests){
				AseSipServletRequest request = (AseSipServletRequest) obj;
				if( request.getServerTxn() != null){
					((AseSipTransaction) request.getServerTxn()).setSipSession(this);
					//((AseSipTransaction) request.getServerTxn()).setAseSipResponse(this.m_transResponse);
					if (AseSipServerTransactionIImpl.class.isInstance(request.getServerTxn())
							&& this.responseClass > 0)
					((AseSipServerTransactionIImpl) request.getServerTxn()).setResponseClass(this.responseClass);
				}
			}
		}
		
		initialRequestMessageId = in.readInt();
		
		super.readExternal(in);
		
		ReplicableList subscriptionList = (ReplicableList)this.getReplicable(SUBSCRIPTION_LIST); 
		m_subscriptionHandler.setSubscriptionList(subscriptionList);
		if(null == m_sipConnector) {
			logId = " AseSipSession Id = [" + getId() + AseStrings.SQUARE_BRACKET_CLOSE;
			m_logId = " AseSipSession Id = [" + getId() +AseStrings.SQUARE_BRACKET_CLOSE;
			logCallId = " Call Id = [UNDEFINED]";

			// Now get reference to m_sipConnector
			AseBaseConnector connector = null;
			AseEngine engine =
				(AseEngine)Registry.lookup(Constants.NAME_ENGINE);
			Iterator iterator = engine.getConnectors();
			for(;iterator != null && iterator.hasNext();){
				connector = (AseBaseConnector) iterator.next();
				if(connector.getProtocol().equals(Constants.PROTOCOL_SIP_2_0)){
					m_sipConnector = (AseSipConnector)connector;
					break;
				}
			}
		}

		m_100RelHandler.setParentSession(this);

		m_sessionState = (AseSipSessionStateImpl)this.getReplicable(SESSION_STATE);
		if(m_sessionState != null) {
			m_sessionState.setParentSession(this);
			m_sessionState.registerDialogReferenceManager(m_invitationHandler);
			m_sessionState.registerDialogReferenceManager(m_subscriptionHandler);
		}

		// If dialog is not already added into Dialog Manager, add it if
		// dialog is established
		//Check Ashish 	
		if(m_sessionState !=  null ) {
			if(m_isAddedIntoDMgr == false) {
				if((m_sessionState.getSessionState() == AseSipSessionState.STATE_EARLY)
						|| (m_sessionState.getSessionState() == AseSipSessionState.STATE_CONFIRMED)) {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Adding dialogs to dialog mgr " +
								getLogId());

					m_sipConnector.addSession(this);
					m_isAddedIntoDMgr = true;
				}
			} else {
				if((m_sessionState.getSessionState() == AseSipSessionState.STATE_INITIAL)
						|| (m_sessionState.getSessionState() == AseSipSessionState.STATE_TERMINATED)) {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Removing dialogs from dialog mgr " +
								getLogId());

					m_sipConnector.removeSession(this);
					m_isAddedIntoDMgr = false;
				}
			}
		} else {
			m_logger.error("readExternal(): m_sessionState is null");
		}
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("AseSipSession data de-seriliased");
			m_logger.debug("Leaving readExternal" + getLogId());
		}
	}



	/**
	 * Implememtation of the Cloneable interface
	 */
	public Object clone() throws CloneNotSupportedException {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering clone." + getLogId());

		if (EvaluationVersion.FLAG) {
			if (AseMeasurementUtil.counterActiveSIPSessions.getCount() >=
				Constants.EVAL_VERSION_MAX_SIP_SESSION) {
				m_logger.error("SAS evaluation version active SIP sessions limit exceeded.");
				throw new IllegalStateException("Max active SIP session limit exceeded!!!");
			}
		}

		// Clone the AseSipSession object
		AseSipSession clonedSession = (AseSipSession)super.clone();
		clonedSession.setId("AseSipSession_"+host.getSubsystemId()+allocateId());

		clonedSession.logId = " AseSipSession Id = [" + clonedSession.getId() + AseStrings.SQUARE_BRACKET_CLOSE;
		clonedSession.m_logId = " AseSipSession Id = [" + clonedSession.getId() + AseStrings.SQUARE_BRACKET_CLOSE;
		clonedSession.logCallId = " Call Id = [UNDEFINED]";

		if (m_logger.isDebugEnabled())
			m_logger.debug("Cloned Session. " + clonedSession.getLogId());

		// Clone the contained AseSipSessionStateImpl object
		clonedSession.m_sessionState = 
			(AseSipSessionStateImpl)(this.m_sessionState.clone());
		clonedSession.m_sessionState.setParentSession(clonedSession);

		// Clone the contained replication handler
		clonedSession.m_replicationHandler =
			(AseSipReplicationHandler)(this.m_replicationHandler.clone());
		clonedSession.m_replicationHandler.setSipSession(clonedSession);

		clonedSession.m_sipConnector = m_sipConnector;

		// Create and setup the support handlers
		clonedSession.m_invitationHandler = new AseSipInvitationHandler();
		clonedSession.m_subscriptionHandler = new AseSipSubscriptionHandler();
		ReplicableList subscriptionList = new ReplicableList(SUBSCRIPTION_LIST);
		clonedSession.setReplicable(subscriptionList);
		clonedSession.m_subscriptionHandler.setSubscriptionList(subscriptionList);

		clonedSession.m_100RelHandler = new AseSip100RelHandler();
		clonedSession.m_100RelHandler.setParentSession(clonedSession);

		// Register the two handlers with the session state as
		// respective dialog reference managers
		clonedSession.m_sessionState.
		registerDialogReferenceManager(clonedSession.m_invitationHandler);
		clonedSession.m_sessionState.
		registerDialogReferenceManager(clonedSession.
				m_subscriptionHandler);

		//Copy the origRequest 
		clonedSession.m_origRequest = m_origRequest;

		clonedSession.m_sessionState.setReplicableId(SESSION_STATE);
		clonedSession.setReplicable(clonedSession.m_sessionState);

		// Add new session into corresponding app session
		((AseApplicationSession)getApplicationSession()).addProtocolSession(
				clonedSession);
		if (getApplicationSession().getAttribute(Constants.DIALOGUE_ID) == null) {
			AseMeasurementUtil.counterActiveSIPSessions.increment();

		}
		AseMeasurementUtil.counterTotalSIPSessions.increment();
		AseMeasurementUtil.thresholdSIPSession.increment();

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving clone." + getLogId());

		return clonedSession;
	}

	void resetDialogParameters(AseSipServletResponse response) {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession resetDialogParameters" +
					getLogId());

		m_sessionState.setSessionState(AseSipSessionState.STATE_INITIAL);	//NJADAUN

		if (role != ROLE_PROXY) {
			AseSipServletRequest req =
				(AseSipServletRequest)(response.getRequest());
			addOutstandingRequest(req);
		}

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession resetDialogParameters" +
					getLogId());
		}
	}

	/**
	 * Matches the incoming response to the SIP session. This is used while
	 * processing the multiple provisional/final dialog establishing response
	 * This function returns TRUE if the session is supposed to handle the
	 * response otherwise returns FALSE
	 */
	boolean isMatchingSession(AseSipServletResponse response) {
		if (AseSipSessionState.STATE_INITIAL ==
			m_sessionState.getSessionState()) {
			return true;
		} else if (AseSipSessionState.STATE_EARLY ==
			m_sessionState.getSessionState() ||
			AseSipSessionState.STATE_CONFIRMED ==
				m_sessionState.getSessionState()) {

			AseSipDialogId dialogId = response.getDialogId();
			if ((dialogId.equals(m_sessionState.getUpstreamDialogId())) ||
					(dialogId.equals(m_sessionState.getDownstreamDialogId())) ) {
				return true;
			}
			else {
				return false;
			}
		} else {
			if (null != (m_sessionState.getUpstreamDialogId())) { 
				AseSipDialogId dialogId = response.getDialogId();
				if( (dialogId.equals(m_sessionState.getUpstreamDialogId())) ||
						(dialogId.equals(m_sessionState.getDownstreamDialogId())) ) {
					return true;
				}
				else
					return false;
			}
			else
				return false;
		}
	}


	/**
	 * Matches the incoming response to the SIP session. This is used while
	 * processing the multiple provisional/final dialog establishing responses
	 * This function returns TRUE if the session is supposed to handle the
	 * response otherwise returns FALSE
	 */
	public boolean isMatchingSession(AseSipServletMessage message) {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering isMatchingSession(AseSipServletMessage)");

		boolean ret = false;

		if (AseSipSessionState.STATE_INITIAL == getSessionState() ||
				message.getDialogId().equals(getUpstreamDialogId()) ||
				message.getDialogId().equals(getDownstreamDialogId()))
			ret = true;

		if (m_logger.isDebugEnabled()) {
			if (!ret)
				m_logger.debug("Session did not match!!!");
			m_logger.debug("Leaving isMatchingSession(AseSipServletMessage)");
		}

		return ret;
	}

	/********************************************************************
	 * javax.servlet.sip.SipSession interface methods
	 ********************************************************************/

	/**
	 * invalidate this sip session. This called by the AseApplicationSession
	 * invalidate method or directly by the application
	 * Call genAttributeRemovedEvent for each attribute in the attributes map
	 * Call genValueUnboundEvent for each attribute in the attributes map
	 * Call genDestroyedEvent
	 * Call base class invalidate
	 */
	public void invalidate() {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.invalidate." + logId);
			m_logger.debug(logCallId);
		}

		boolean changeLock = AseThreadData.setIcLock(this);

		try{

			// If state is not VALID throw an exception
			if (AseProtocolSession.VALID != state) {
				m_logger.error("invalidate. Throwing Exception. Session " +
						"state = " +state +" "+ logId);
				throw new IllegalStateException("Session state not VALID");
			}

			AseSipDiagnosticsLogger diag = AseSipDiagnosticsLogger.getInstance();
			if (diag.isAppInvalidationLoggingEnabled()) {
				if (!((SipApplicationSessionImpl)this.appSession).isExpired()) {
					if (((SipApplicationSessionImpl)this.appSession).getState() == Constants.STATE_VALID) {
						// It means that only SipSession is being invalidated, record this
						diag.log("App invalidated SipSession Only: " + this.getId());
					} else {
						diag.log("App invalidated SipSession: " + this.getId());
					}
				}
			}

			// BPUsa07541 : [
			// Write any CDR object associated with this session to the backing 
			// store if it has not yet been written.
			CDR cdr = (CDR)this.getAttribute(Constants.CDR_KEY);
			//making default cdr writing configuration based
			boolean writeCDR = false;
			if (m_configRepository != null){
				if (writeDefaultCDR.toLowerCase().equals(AseStrings.TRUE_SMALL))
					writeCDR = true;
			}
			
			if (cdr != null && cdr.getWriteCount() == 0 && writeCDR) {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("invalidate(): CDR object associated with this session was never written, so writing it to the backing store now.");
				}
				try {
					cdr.write();
				} catch (Exception e) {
					String msg = "Error occurred while writing CDR object to backing store: " + e.getMessage();
					m_logger.error(msg, e);
					throw new RuntimeException(msg);
				}
			}
			//Implementing the same thing for TCAP calls as well
			CDR tcapCDR = (CDR)super.getAttribute(Constants.CDR_KEY_FOR_TCAP);
			if (tcapCDR != null && tcapCDR.getWriteCount() == 0 && writeCDR) {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("invalidate(): CDR object associated with this session was never written, so writing it to the backing store now.");
				}
				try {
					tcapCDR.write();
				} catch (Exception e) {
					String msg = "Error occurred while writing CDR object to backing store: " + e.getMessage();
					m_logger.error(msg, e);
					throw new RuntimeException(msg);
				}
			}
			// ]

			//BPInd18155
			// Removing the invalidated SipSession from replicables.
		if (m_logger.isDebugEnabled())	m_logger.debug("Removing the replicable "+this.getReplicableId()+" from context");
			((AseApplicationSession)this.appSession).getIc().removeReplicable(this.getReplicableId());


			Iterator iter = super.getAttributeNamesIterator();
			while (iter.hasNext()) {
				String attrName = (String)(iter.next());
				genAttributeRemovedEvent(attrName);
				genValueUnboundEvent(attrName, super.getAttribute(attrName));
			}

			// Generate a session destroyed event
			genDestroyedEvent();

			// Removing the subscription if present
			ReplicableList list = m_subscriptionHandler.getSubscriptionList();
			AseSipSubscription matchRefSub = null;
			Iterator itor = list.iterator();
			while(itor.hasNext()) {
				AseSipSubscription sub = (AseSipSubscription)itor.next();
				if (null != sub.getReferencedId()) {
					matchRefSub = AseSipSubscription.createReferencedSubscription(sub);
				}
				this.m_sipConnector.removeSubscription(sub, this);
				if(matchRefSub != null) {
					this.m_sipConnector.removeSubscription(matchRefSub, this);
				}
			}

			//Decrement for the Overload control
			if(((AseApplicationSession)this.getApplicationSession()).getInitialPriorityStatus()) {
				ocmManager.decreaseNSEP(ocmId);
			} else {
				ocmManager.decrease(ocmId);
			}
			
			if(isActivatedOnFt ){
				((AseApplicationSession)appSession).decrementActivatedSipSessions();
			}
			//Decrement the counter for the active SIP session if it is not tcap.
			if (appSession.getAttribute(Constants.DIALOGUE_ID) ==null) {
				AseMeasurementUtil.counterActiveSIPSessions.decrement();
			}

			// Set the fact that a replication is required
			this.setModified(true);

			// Call base class
			super.invalidate();

			Iterator<SipSession>  sessionItr = (Iterator<SipSession>) this.getApplicationSession().getSessions("SIP");
			boolean appSessionReadyToInvalidate =true;

			while(sessionItr.hasNext()){

				if(sessionItr.next().isValid())
					appSessionReadyToInvalidate =false;

			}
			if (m_logger.isDebugEnabled()) m_logger.debug("Leaving AseSipSession.invalidate  : " + appSessionReadyToInvalidate);
			if(appSessionReadyToInvalidate == true && 
					!(((SipApplicationSessionImpl)this.getApplicationSession()).getState()== Constants.STATE_INVALIDATING)){
				
				if(this.getApplicationSession().getInvalidateWhenReady()){
					((SipApplicationSessionImpl)(this.getApplicationSession())).objectReadyToInvalidate();
				}
			}



		} finally {		  
			AseThreadData.resetIcLock(this, changeLock);
		}

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.invalidate" + logId);
		}
	}

	/**
	 * Remove an attribute from the attributes map.
	 * Generate attributeRemovedEvent and valueUnboundEvent
	 */
	public void removeAttribute(String name) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.removeAttribute. " +
					"Attribute Name = [" + name + AseStrings.SQUARE_BRACKET_CLOSE + logId);
		}

		// Now do all the work.
		Object value = super.getAttribute(name); 
		super.removeAttribute(name);

		// If something is actually removed, try and generate events
		if (null != value) {
			genAttributeRemovedEvent(name);
			genValueUnboundEvent(name, value);
		}

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.removeAttribute. " +
					"Attribute Name = [" + name + AseStrings.SQUARE_BRACKET_CLOSE + logId);
		}
	}

	/**
	 * Add an attribute to the map
	 * Generate attributeReplaced or attributeAdded event.
	 * Generate valueBound event.
	 */
	public void setAttribute(String name, Object attribute) {
		// BPUsa07541 : [
		if (Constants.CDR_KEY.equals(name)) {
			m_logger.error("Cannot set value for session attribute: " + name);
			//throw new IllegalArgumentException("Cannot set value for session attribute: " + name);
		}
		// ]

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.setAttribute. " +
					"Attribute Name = [" + name + AseStrings.SQUARE_BRACKET_CLOSE + logId);
		}

		if (name == null)	 {
			// no-op with INFO logging
			m_logger.info("Attribute name can't be null");
			return;
		}
		else if (attribute == null) {
			// no-op with error logging
			m_logger.info("Attribute value can't be null");	     //BPInd13388 (log level was initially set to error)
			return;	
		}	



		// Now do all the work.
		Object value = super.getAttribute(name); 
		super.setAttribute(name, attribute);

		// Generate appropriate events
		if(null != value) {
			genAttributeReplacedEvent(name);
			genValueUnboundEvent(name, attribute);
		} else {
			genAttributeAddedEvent(name);
		}

		genValueBoundEvent(name, attribute);

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.setAttribute. " +
					"Attribute Name = [" + name + AseStrings.SQUARE_BRACKET_CLOSE + logId);
		}
	}

	/**
	 * Method called by the application to create a subsequent request
	 * Call getFactory on the connector and call the createRequest method
	 * on the connector
	 */
	public SipServletRequest createRequest(String method) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.createRequest. " +
					"Method = [" + method + AseStrings.SQUARE_BRACKET_CLOSE + logId);
			m_logger.debug(logCallId);
		}

		((AseApplicationSession)this.appSession).getIc().activate();

		// If state is invalid, throw an exception
		if (AseProtocolSession.VALID != state) {
			m_logger.error("createRequest. Throwing Exception. Session " +
					"state = INVALID" +
					logId);
			throw new java.lang.IllegalStateException("Invalid Session");
		}

		// If role is proxy, throw an exception
		if (role == ROLE_PROXY) {
			m_logger.error("createRequest. Throwing Exception. Session role = ROLE_PROXY." + logId);
			throw new java.lang.IllegalStateException("Proxy Session");
		}

		AseConnectorSipFactory sipFactory =
			(AseConnectorSipFactory)(m_sipConnector.getFactory());
		AseSipServletRequest request = sipFactory.createRequest(this, method);

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.createRequest. " +
					"Method = [" + method + AseStrings.SQUARE_BRACKET_CLOSE + logId);
		}

		return request;
	}


	/**
	 * @param name - The name of the attribute whose value is to be returned.
	 * @return  The value of the specified attribute.
	 */
	public Object getAttribute(String name) {
		Object value = super.getAttribute(name);

		// BPUsa07541 : [
		if (Constants.CDR_KEY.equals(name) && value == null) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("getAttribute(): No CDR object is currently associated with this session, so creating a new CDR...");
			}
			super.setAttribute(name, value = this.getCDR());
		} else if (Constants.CDR_KEY_FOR_TCAP.equals(name) && value == null) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("getAttribute(): [TCAP] No CDR object is currently associated with this session, so creating a new CDR...");
			}
			super.setAttribute(name, value = this.getCDRforTCAP());
		}
		// ]

		return value;
	}


	/**
	 * Called by the "getAttribute" method to get the CDR object
	 * to be put into this session's attribute map.
	 */
	private CDR getCDR() {
		// Get the CDRContext of the app that this session is associated with...
		AseApplicationSession appSession = (AseApplicationSession)this.getApplicationSession();
		AseContext app = appSession.getContext();
		CDR cdr =app.getCDRContext(this.getId()).createCDR();
		// Populate the CDR with the initial values...
		cdr.set(CDR.DEFAULT_CDR,CDR.DEFAULT_CDR);
		cdr.set(CDR.CORRELATION_ID, appSession.getAttribute(Constants.CORRELATION_ID));
		cdr.set(CDR.SESSION_ID, this.getId());
			
		Address localParty=this.getLocalParty()!=null?this.getLocalParty():m_localParty;
		Address remoteParty=this.getRemoteParty()!=null?this.getRemoteParty():m_remoteParty;
		
		
		if (localParty != null && remoteParty != null) {
			if (this.getRole() == ROLE_UAC) {
				if(localParty.getURI().isSipURI()){
					cdr.set(CDR.ORIGINATING_NUMBER,
							((SipURI) localParty.getURI()).getUser());
				}else{
					cdr.set(CDR.ORIGINATING_NUMBER,
							((TelURL) localParty.getURI()).getPhoneNumber());
				}
				
				if(remoteParty.getURI().isSipURI()){
					cdr.set(CDR.TERMINATING_NUMBER,
							((SipURI) remoteParty.getURI()).getUser());
				}else{
					cdr.set(CDR.TERMINATING_NUMBER,
							((TelURL) remoteParty.getURI()).getPhoneNumber());
				}

			} else {
				
				if(remoteParty.getURI().isSipURI()){
					cdr.set(CDR.ORIGINATING_NUMBER,
							((SipURI) remoteParty.getURI()).getUser());
				}else{
					cdr.set(CDR.ORIGINATING_NUMBER,
							((TelURL) remoteParty.getURI()).getPhoneNumber());
				}
				
				if(localParty.getURI().isSipURI()){
					cdr.set(CDR.TERMINATING_NUMBER,
							((SipURI) localParty.getURI()).getUser());
				}else{
					cdr.set(CDR.TERMINATING_NUMBER,
							((TelURL) localParty.getURI()).getPhoneNumber());
				}
				
			}

			if(remoteParty.getURI().isSipURI()){
				cdr.set(CDR.BILL_TO_NUMBER,
						((SipURI) remoteParty.getURI()).getUser());
			}else{
				cdr.set(CDR.BILL_TO_NUMBER,
						((TelURL) remoteParty.getURI()).getPhoneNumber());
			}
			
		}
	
		cdr.set(CDR.CALL_START_TIMESTAMP, String.valueOf(this.getCreationTime()));
		
		
		
		// Special case for handling the default CDR implementation:
		// We associate the host and app name with the CDR here so that the 
		// CDRContext can be looked up and re-associated with the CDR after 
		// it has been replicated.
		// We also set a flag indicating if the CDR is associated with a
		// distributable app or not.  If it is distributable, only Serializable
		// attribute values will be allowed to be set on it.
		if (cdr instanceof CDRImpl) {
			((CDRImpl)cdr).setHostName(app.getParent().getName());
			((CDRImpl)cdr).setAppName(app.getName());
			((CDRImpl)cdr).setDistributable(app.isDistributable());
		}

		return cdr;
	}
	
	/**
	 * Called by the "getAttribute" method to get the CDR object
	 * to be put into this session's attribute map.
	 */
	private CDR getCDRforTCAP() {
		// Get the CDRContext of the app that this session is associated with...
		AseApplicationSession appSession = (AseApplicationSession)this.getApplicationSession();
		AseContext app = appSession.getContext();
		CDR cdr = app.getCDRContext(this.getId()).createCDR();

		// Populate the CDR with the initial values...
		//Marking it as default CDR
		cdr.set(CDR.DEFAULT_CDR,CDR.DEFAULT_CDR);
		cdr.set(CDR.CORRELATION_ID, appSession.getAttribute(Constants.CORRELATION_ID));
		cdr.set(CDR.SESSION_ID, this.getId());
///		cdr.set(CDR.ORIGINATING_NUMBER, ((SipURI)m_localParty.getURI()).getUser()); /coomenting for axtel it is coming as null for tcap
//		cdr.set(CDR.TERMINATING_NUMBER, ((SipURI)m_remoteParty.getURI()).getUser());
		cdr.set(CDR.CALL_START_TIMESTAMP, String.valueOf(this.getCreationTime()));
//		cdr.set(CDR.BILL_TO_NUMBER, ((SipURI)m_localParty.getURI()).getUser());

		// Special case for handling the default CDR implementation:
		// We associate the host and app name with the CDR here so that the 
		// CDRContext can be looked up and re-associated with the CDR after 
		// it has been replicated.
		// We also set a flag indicating if the CDR is associated with a
		// distributable app or not.  If it is distributable, only Serializable
		// attribute values will be allowed to be set on it.
		if (cdr instanceof CDRImpl) {
			((CDRImpl)cdr).setHostName(app.getParent().getName());
			((CDRImpl)cdr).setAppName(app.getName());
			((CDRImpl)cdr).setDistributable(app.isDistributable());
		}

		return cdr;
	}


	/********************************************************************
	 * AseProtocolSession methods
	 ********************************************************************/

	/**
	 * Called by the AseApplicationSession cleanup method
	 * Call the sipconnector removeSession method
	 * Cleanup any references
	 * Call super cleanup
	 */
	public void cleanup() {

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.cleanup. " + logId);
		}

		// If invalidation has not happened first invalidate
		if(((AseApplicationSession)this.appSession).getIc().isActive()) {
			if (AseProtocolSession.VALID == state) {
				invalidate();
			}
		}

		// First to get a lock on the IC object. All the processing happens
		// Inside the lock
		try {
			this.acquire();
		}
		catch (AseLockException e) {
			m_logger.error("cleanup. Failed to acquire a lock" +	logId);
			return;
		}

		try{
			if(m_sessionState != null) {
				m_sipConnector.removeSession(this);
			}
			m_outstandingRequests.clear();
			m_outstandingRequests = null;
			m_invite2xxRequests.clear();
			m_invite2xxRequests = null;
			m_inviteNon2xxRequests.clear();
			m_inviteNon2xxRequests = null;
			m_cancelledRequests.clear();
			m_cancelledRequests = null;

			// Call base class
			super.cleanup();

		}finally {		  
			try {
				this.release();
			} catch (AseLockException e) {
				m_logger.error("cleanup. Failed to release a lock" +	logId, e);
			}
		}
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.cleanup. " + logId);
		}
	}

	/**
	 * Called by the AseHost or the application to set the handler for this
	 * session object
	 */
	public void setHandler(String name)
	throws javax.servlet.ServletException {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.setHandler. " +
					"Servlet Name = [" + name + AseStrings.SQUARE_BRACKET_CLOSE + logId);
		}

		// We need to find if the servlet is a valid servlet.
		// Get help from the AseContext
//		AseContainer childContainer =  ((AseApplicationSession)appSession).getContext().findChild(name);
//		if (null == childContainer) {
//			m_logger.error("setHandler. Throwing Exception. Servlet does " +
//					"not belong " +
//					"to this application" + logId);
//			String err = "Servlet does not belong to this application";
//			throw new javax.servlet.ServletException(err);
//		}

		// All is good call base class
		try {
			this.acquire();
		}
		catch (AseLockException e) {
			m_logger.error("setHandler. Failed to acquire a lock" +	logId);
			return;
		}
		try{

			super.setHandler(name);
		}finally{
			try {
				this.release();
			}catch (AseLockException e) {
				m_logger.error("setHandler. Failed to release a lock" +	logId);
				return;
			}
		}
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.setHandler. " +
					"Servlet Name = [" + name + AseStrings.SQUARE_BRACKET_CLOSE + logId);
		}
	}

	/**
	 * handleInitialRequest
	 * Invoked from handleRequest for initial requests
	 */
	public void handleInitialRequest(AseSipServletRequest request)
	throws AseInvocationFailedException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession.handleInitialRequest" +
					getLogId());

		// Retrieve the underlying DsSipRequest
		DsSipRequest dsRequest = request.getDsRequest();
		
		//FT Handling strategy Update: Replication will be done for the provisional
		//responses as well, so in order to activate requests on the stand by server
		//storing the requests in the application session. This is done to replicate 
		//the server transaction and its associated connection object.
		//Server transaction will be present in the INVITE request coming from the 
		//network while client transaction gets created at SIL while sending the 
		//INVITE request to the network
		//This is removed from here as we are ultimately adding the request to the 
		///MESSAGE_MAP during serialization of request through storeMessagAttr method 
		//request.addToApplicationSession();


		if (m_logger.isDebugEnabled())
			m_logger.debug("Method Id = [" +
					dsRequest.getMethodID() + AseStrings.SQUARE_BRACKET_CLOSE + getLogId());

		// Create a copy of the original request and save it.
		// This is required if the application turns out to be a PROXY
		AseConnectorSipFactory sipFactory =
			(AseConnectorSipFactory)(m_sipConnector.getFactory());

		m_origRequest = request;
		
		if (m_logger.isDebugEnabled())
			m_logger.debug("m_origRequest received is "+ m_origRequest + "\n index of conteact bracket "+m_origRequest.toString().indexOf("Contact: <"));
		
		
		request = (AseSipServletRequest)(sipFactory.
				createRequest(m_origRequest));
		
		if(m_origRequest.toString().indexOf("Contact: <")==-1){
			request.setContactBrackets(false);
		}else{
			request.setContactBrackets(true);
		}
		

		// Strip the top ROUTE header if it points to us
		AseSipRouteHeaderHandler.stripTopSelfRoute(request);
		
		//This is done to ensure that Service should get this attribute in case of client transaction 
		//i.e., when invite is sent. Now in case of FT Service needs to send the CANCEL.
		//Before this change service was getting the request from attribute in the app session
		//that object doesn't have the client transaction. The same thing is being done for INVITE
		//coming in.
		this.setAttribute(Constants.ORIG_REQUEST, request);
		
			if(request.getRequestURI().isSipURI()){
		  	    SipURI requestUri=(SipURI) request.getRequestURI();
			    
				if (privateIps != null && privateIps.contains(requestUri.getHost())) {
		
					if (m_logger.isDebugEnabled()) {
						m_logger.debug("Request received on private interface !!"
								+ getLogId());
					}
					this.getApplicationSession().setAttribute(
							Constants.RECEIVED_PRIVATE_IF, Boolean.TRUE);
					
				}else{
					
					if (m_logger.isDebugEnabled()) {
						m_logger.debug("Request not received on private interface !!"
								+ getLogId());
					}
				}
				
				this.getApplicationSession().setAttribute(Constants.IF_FOR_RECEIVING_ORIG_REQUEST, requestUri);
			}
			
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Inside handleInitialRequest..!"
						+ getLogId());
			}
	
		// Get the appropriate message handler and let it have a go at
		// the message
		AseSipMessageHandler msgHandler =
			AseSipMessageHandlerFactory.getHandler(dsRequest.getMethodID(),
					this);

		int ret = 0;
		try {
			if(AseUtils.getCallPrioritySupport() == 1)	{
				nsepMessageHandler.handleInitialRequest(request, this);
			}	
			ret = msgHandler.handleInitialRequest(request, this);
		}
		catch (AseSipMessageHandlerException e) {
			m_logger.error("Message handler Exception: " + getLogId(), e);
			throw new AseInvocationFailedException(e.toString());
		}
		  m_localParty = request.getTo();
		  m_remoteParty = request.getFrom();

		// If return value is NOOP
		if (true == msgHandler.isRetNoop(ret)) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Message Handler return NOOP" + getLogId());
				m_logger.debug("Leaving AseSipSession.handleInitialRequest" +
						getLogId());
			}
			return;
		}

		// If return value is STATE_UPDATE
		if (true == msgHandler.isRetStateUpdate(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler return STATE_UPDATE" +
						getLogId());

			try {
				m_sessionState.handleInitialRequest(request);
			}
			catch (AseSipSessionStateException e) {
				m_logger.error("Session state Exception", e);
				throw new AseInvocationFailedException(e.toString());
			}
		}

		// If return value is CONTINUE
		if (true == msgHandler.isRetContinue(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler return CONTINUE" +
						getLogId());
			
			//Added to Capture Call Id in print-info for initial NOTIFY request
			if(request.getMethod().equals(AseStrings.NOTIFY) && m_sessionState.getCallId() == null){
				m_sessionState.setCallId(request.getCallId());
			}

			sendRequestToServlet(request);
		}

		// If return value is PROXY
		if (true == msgHandler.isRetProxy(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler return PROXY" +
						getLogId());

			// If the servlet has already responded to this request we
			// do not PROXY it
			if (true == request.isResponded()) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Application sent final response " +
							"for this request. Not proxying it." +
							getLogId());
			}
			else {
				// Proxy the request
				proxyRequest(request);
			}
		}

		// Send the request to the replication handler
		m_replicationHandler.handleInitialRequest(request, this);

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession.handleInitialRequest" +
					getLogId());
	}


	/**
	 * Invoked from handleRequest for subsequent requests
	 */
	public void handleSubsequentRequest(AseSipServletRequest request)
	throws AseInvocationFailedException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession.handleSubsequentRequest" +
					getLogId());

		// Retrieve the underlying DsSipRequest
		DsSipRequest dsRequest = request.getDsRequest();

		if (m_logger.isDebugEnabled())
			m_logger.debug("Method Id = [" +
					dsRequest.getMethodID() + AseStrings.SQUARE_BRACKET_CLOSE + getLogId());

		// Add a Record Route Header. This will get added if the session role
		// is PROXY and proxy is record-routed.
		if(m_sessionState.isRecordRouted()) {
			//JSR 289.34
			SipURI uri = ((AseSipSession)request.getSession()).getOutboundInterface();
			if(uri!=null){
				AseSipRecordRouteHeaderHandler.
				addRecordRoute(request, uri, this);

			}else {
				AseSipRecordRouteHeaderHandler.
				addRecordRoute(request, getRecordRouteURI(), this);
			}
		}

		// Get the appropriate message handler and let it have a go at
		// the message
		AseSipMessageHandler msgHandler =
			AseSipMessageHandlerFactory.getHandler(dsRequest.getMethodID(),
					this);

		int ret = 0;
		try {
			if(AseUtils.getCallPrioritySupport() == 1)      {
				nsepMessageHandler.handleSubsequentRequest(request, this);
			}

			ret = msgHandler.handleSubsequentRequest(request, this);
		}
		catch (AseSipMessageHandlerException e) {
			m_logger.error("Message handler Exception", e);
			throw new AseInvocationFailedException(e.toString());
		}

		// If return value is NOOP
		if (true == msgHandler.isRetNoop(ret)) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Message Handler return NOOP" + getLogId());
				m_logger.debug("Leaving AseSipSession." +
						"handleSubsequentRequest" +
						getLogId());
			}
			return;
		}

		if (true == msgHandler.isRetStateUpdate(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler return STATE UPDATE" +
						getLogId());

			try {
				m_sessionState.handleSubsequentRequest(request);
			}
			catch (AseSipSessionStateException e) {
				m_logger.error("Session state exception", e);
				throw new AseInvocationFailedException(e.toString());
			}
		}

		if (true == msgHandler.isRetContinue(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler return CONTINUE" +
						getLogId());

			// Send the request to the servlet
			sendRequestToServlet(request);
		}

		if (true == msgHandler.isRetProxy(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler return PROXY" + getLogId());

			// If the servlet has already responded to this request we
			// do not PROXY it
			if (true == request.isResponded()) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Application sent final response " +
							"for this request. Not proxying it." +
							getLogId());
			}
			else {
				// Proxy the request
				proxyRequest(request);
			}
		}

		// Send the request to the replication handler
		m_replicationHandler.handleSubsequentRequest(request, this);

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession.handleSubsequentRequest" +
					getLogId());
	}

	/**
	 * Invoked by the AseHost when a request is to be delivered to the
	 * application
	 * If request is initial then connector has checked that this is a dialog
	 * creating request
	 */

	public void handleRequest(AseBaseRequest request,
			AseBaseResponse response)
	throws AseInvocationFailedException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession.handleRequest" + logId);

		try{
			if(m_sipConnector.checkConnectorIPType()){
				AseSipServletRequest req = (AseSipServletRequest)request;
				AseAddressImpl addressImpl = (AseAddressImpl)req.getTo();
				AseSipURIImpl aseSipURIImpl = (AseSipURIImpl)addressImpl.getURI();

				InetAddress inetAddress = InetAddress.getByName(aseSipURIImpl.getHost());

				if(inetAddress instanceof Inet6Address){
					if(req.getSession0().getOutboundInterface() == null){
						if (m_logger.isDebugEnabled()) {
							m_logger.debug("In handleRequest method of AseSipSession : Before setting outbound for IPv6");
						}
						req.getSession0().setOutboundInterface(InetAddress.getByName(m_sipConnector.getIpv6Address()));
					}
				}
			}
		}catch (Exception e) {
			m_logger.error("Exception in handleRequest method of AseSipSession" + e );
		}


		// First to get a lock on the IC object. All the processing happens
		// Inside the lock
		boolean isLockedAlready=((AseApplicationSession)appSession).getIc().isAcquiredByCurrentThread();
		if(!isLockedAlready){
			try {
				this.acquire();	
			}
			catch (AseLockException e) {
				m_logger.error("handleEvent. Failed to acquire Lock" +
						e.toString() + logId);
				return;
			}
		}
		AseThreadData.setCurrentIc(((AseApplicationSession)appSession).getIc());

		try{
			// Set the last accessed time
			accessed();

			// Typecast the request for further processing
			AseSipServletRequest sipRequest = (AseSipServletRequest)request;

			// Depending on the type of request send it to the appropriate
			// helper method

			//B2BUA Helper - Check for the pending message
			m_b2bHandler.receiveRequest((AseSipServletRequest)request);

			if (sipRequest.isInitial())
				handleInitialRequest(sipRequest);
			else
				handleSubsequentRequest(sipRequest);

		}catch (AseInvocationFailedException exp)	{
			//Don't log error message here
			throw new AseInvocationFailedException(exp.toString());
		}catch (Exception e) {
			m_logger.error("Exception handling the request", e);
			throw new AseInvocationFailedException(e.toString(), e);
		} finally {

			// Release the IC lock
			try {
				AseThreadData.setCurrentIc(null);
				if(!isLockedAlready){
					this.release();
				}
			}catch (AseLockException le) {
				m_logger.error("Exception releasing the lock", le);
			}
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession.handleRequest" + logId);
	}

	/**
	 * Sends the request to the servlet
	 * Catches appropriate exceptions
	 * If TooManyHopsException is thrown generates and sends an appropriate
	 * response
	 */
	private void sendRequestToServlet(AseSipServletRequest request)
	throws AseInvocationFailedException {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession.sendRequestToServlet" +
					getLogId());

		try {
			super.handleRequest(request, null);
		}
		catch (javax.servlet.sip.TooManyHopsException e) {
			m_logger.error("TooManyHopsException" + logId, e);
			setRole(ROLE_UAS);

			// create and send a response
			try {
				AseConnectorSipFactory sipFactory =
					(AseConnectorSipFactory)(m_sipConnector.getFactory());
				AseSipServletResponse resp =
					sipFactory.createResponse(request, 483, null);
				sendResponse(resp);
			}
			catch(AseSipSessionException exp) {
				//Don't log any error message here
			}
			catch (Exception e1) {
				m_logger.error("Exception sending 483 response" + logId, e1);
			}
		}
		catch (Exception e) {
			m_logger.error("Exception invoking handleRequest", e);
			throw new AseInvocationFailedException(e.toString());
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession.sendRequestToServlet" +
					getLogId());
	}

	/**
	 * Sends the response to the servlet
	 * Catches appropriate exceptions
	 */
	private void sendResponseToServlet(AseSipServletResponse response)
	throws AseInvocationFailedException {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession.sendResponseToServlet" +
					getLogId());

		try {
			super.handleResponse(null, response);
		}
		catch (Exception e) {
			m_logger.error("Exception invoking handleResponse", e);
			throw new AseInvocationFailedException(e.toString());
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession.sendResponseToServlet" +
					getLogId());
	}

	/**
	 * PROXY the request
	 */
	private void proxyRequest(AseSipServletRequest request)
	throws AseInvocationFailedException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession.proxyRequest" +
					getLogId());

		AseSipSession session = (AseSipSession)request.getSession();
		try {
			session.sendRequest(request);
		}
		catch (AseSipSessionException exp)	{
			//Don't log error here
			throw new AseInvocationFailedException(exp.toString());
		}
		catch (Exception e) {
			m_logger.error("Exception in sendRequest", e);
			throw new AseInvocationFailedException(e.toString());
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession.proxyRequest" +
					getLogId());
	}

	/**
	 * PROXY the response
	 */
	private void proxyResponse(AseSipServletResponse response)
	throws AseInvocationFailedException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession.proxyResponse" +
					getLogId());

		AseSipSession session = (AseSipSession)response.getSession();
		try {
			session.sendResponse(response);
		}
		catch (AseSipSessionException exp) {
			//Don't log any error message here
			throw new AseInvocationFailedException(exp.toString());
		}
		catch (Exception e) {
			m_logger.error("Exception in sendResponse", e);
			throw new AseInvocationFailedException(e.toString());
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession.proxyResponse" +
					getLogId());
	}

	/**
	 * Invoked by the AseHost when a response is to be delivered to the
	 * application
	 */
	public void handleResponse(AseBaseRequest request,
			AseBaseResponse response)
	throws AseInvocationFailedException, ServletException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession handleResponse" + 
					getLogId());

		AseSipServletResponse sipResponse = (AseSipServletResponse)response;
		DsSipResponse dsResponse = sipResponse.getDsResponse();
		if (m_logger.isDebugEnabled()) {
			m_logger.debug(getLogId() + " " + "CSeq Method = [" +
					dsResponse.getCSeqType() + "] Status Code = [" +
					dsResponse.getStatusCode() + AseStrings.SQUARE_BRACKET_CLOSE);
		}

		// First to get a lock on the IC object. All the processing happens
		// Inside the lock
		boolean isLockedAlready=((AseApplicationSession)appSession).getIc().isAcquiredByCurrentThread();
		if(!isLockedAlready){
			try {
				this.acquire();	
			}
			catch (AseLockException e) {
				m_logger.error("handleEvent. Failed to acquire Lock" +
						e.toString() + logId);
				return;
			}
		}
		AseThreadData.setCurrentIc(((AseApplicationSession)appSession).getIc());
		

		try{

			// change the last accessed time
			accessed();

			// BPUsa07541: [
						// If we receive a response for an INVITE, use it's status code as the
						// default value for the CALL_COMPLETION_STATUS_CODE attribute for any
						// CDR object associated with this session.
						CDR cdr = (CDR)this.getAttribute(Constants.CDR_KEY);
						if (cdr != null) {
							if(cdr.get(CDR.CALL_COMPLETION_STATUS_CODE)==null && (getRole()==ROLE_UAC||getRole()==ROLE_PROXY))
								cdr.set(CDR.CALL_COMPLETION_STATUS_CODE, new Integer(dsResponse.getStatusCode()));		
							if (this.getState()==State.TERMINATED) {
								Object callEndTimestamp = cdr.get(CDR.CALL_END_TIMESTAMP);
								Object callStartTimestamp = cdr.get(CDR.CALL_START_TIMESTAMP);
								Object callDuration = cdr.get(CDR.CALL_DURATION_MSECS);

								if (callEndTimestamp == null) {
									cdr.set(CDR.CALL_END_TIMESTAMP, callEndTimestamp = new Long(System.currentTimeMillis()));
								}
								if (callDuration == null && callStartTimestamp != null) {
									cdr.set(CDR.CALL_DURATION_MSECS, new Long(Long.parseLong(callEndTimestamp.toString()) - Long.parseLong(callStartTimestamp.toString())));
								}
							}
						}
						// ]
						
			// Retrieve the appropriate message handler and see what it thinks
			AseSipMessageHandler msgHandler =
				AseSipMessageHandlerFactory.getHandler(dsResponse.getMethodID(),
						this);

			int ret = 0;
			try {
				if(AseUtils.getCallPrioritySupport() == 1)      {
					nsepMessageHandler.handleResponse(sipResponse, this);
				}
				ret = msgHandler.handleResponse(sipResponse, this);
				if (m_logger.isDebugEnabled()) m_logger.debug("Return value = " + ret);
			}
			catch (AseSipMessageHandlerException e) {
				m_logger.error("Message handler exception", e);
				throw new AseInvocationFailedException(e.toString());
			}

			//B2BUA Helper - Check for the pending message
			m_b2bHandler.receiveResponse((AseSipServletResponse)response);

			// If return value is NOOP
			if (true == msgHandler.isRetNoop(ret)) {

				if (m_logger.isDebugEnabled()) {
					m_logger.debug("Message Handler return NOOP" + getLogId());
					m_logger.debug("Leaving AseSipSession.handleResponse" +
							getLogId());
				}
				return;
			}

			if (true == msgHandler.isRetStateUpdate(ret)) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Message handler return STATE_UPDATE");

				try {
					m_sessionState.handleResponse(sipResponse);
				}
				catch (AseSipSessionStateException e) {
					m_logger.error("Session state exception", e);
					throw new AseInvocationFailedException(e.toString());
				}
			}

			try {
				// If response is CONTINUE
				if (true == msgHandler.isRetContinue(ret)) {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Message handler returned CONTINUE");

					// Send the response to the servlet
					sendResponseToServlet(sipResponse);
				}

				// If response is PROXY
				if (true == msgHandler.isRetProxy(ret)) {
					if (m_logger.isDebugEnabled())
						m_logger.debug("Message handler returned PROXY");

					// Proxy the response
					proxyResponse(sipResponse);
				}
			}
			catch (AseInvocationFailedException exp)	{
				throw exp;
			}
			catch (Exception e) {
				m_logger.error("Exception handling the response", e);
				throw new AseInvocationFailedException(e.toString());
			}
			// Generate replication event for non-INVITE Final response
			m_replicationHandler.handleResponse(sipResponse, this);
		}finally{
			// Release the lock
			try {
				AseThreadData.setCurrentIc(null);
				if(!isLockedAlready){
					this.release();
				}
			}
			catch (AseLockException le) {
				m_logger.error("Failed to release Lock", le);
			}
		}
		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession handleResponse" +
					getLogId());

		/*
		 *   	//Counter Decrement for UAC to check pending transaction
		 */
		--counter;
		if(invalidateWhenReady == true && isReadyToInvalidate()== true)
			objectReadyToInvalidate(); 

	}

	/**
	 * Return the protocol type i.e. "SIP" as per the specifications
	 */
	public String getProtocol() {
		return "SIP";
	}

	/********************************************************************
	 * Method from the AseEventListener Interface
	 *******************************************************************/

	/**
	 * Invoked by the AseHost when an event is received.
	 */
	public void handleEvent(EventObject eventObject) {
		AseEvent event = (AseEvent)eventObject;
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.handleEvent" + logId);
		}

		// First to get a lock on the IC object. All the processing happens
		// Inside the lock
		boolean isLockedAlready=((AseApplicationSession)appSession).getIc().isAcquiredByCurrentThread();
		if(!isLockedAlready){
			try {
				this.acquire();	
			}
			catch (AseLockException e) {
				m_logger.error("handleEvent. Failed to acquire Lock" +
						e.toString() + logId);
				return;
			}
		}
		AseThreadData.setCurrentIc(((AseApplicationSession)appSession).getIc());
		
		try{
			// If the state of this session is not VALID then throw exception
			if (AseProtocolSession.VALID != state) {
				m_logger.error("handleEvent. Session state not VALID" +
						logId);
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("Leaving AseSipSession.handleEvent" + logId);
				}
				return;
			}

			// Change the last accessed time. This is a method on the base
			// class
			accessed();

			// Check the event type. We should get either of
			// EVENT_SIP_ACK_ERROR or EVENT_SIP_PRACK_ERROR events.
			if (Constants.EVENT_SIP_ACK_ERROR == event.getType()) {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug(logId + logCallId + "ACK timeout Event received");
				}
				genAckTimeoutEvent((SipErrorEvent)(event.getData()));
			}
			else if (Constants.EVENT_SIP_PRACK_ERROR == event.getType()) {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug(logId + logCallId +
					"PRACK timeout Event received");
				}
				genPrackTimeoutEvent((SipErrorEvent)(event.getData()));
			}
		}finally{		
			try {
				AseThreadData.setCurrentIc(null);
				if(!isLockedAlready){
					this.release();
				}
			}
			catch (AseLockException e) {
				m_logger.error("handleEvent. Failed to release Lock" +
						e.toString() + logId);
				return;
			}
		}
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.handleEvent" + logId);
		}
	}


	/**
	 * Invoked by the AseSipServletRequest send method
	 * Do some state processing and send the request to the connector
	 * for disptach over the network
	 */

	void sendRequest(AseSipServletRequest request)
	throws AseSipSessionException, IOException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession sendRequest" + getLogId());
		((AseApplicationSession)this.appSession).getIc().activate() ; 

		// Retrieve the DS request object
		DsSipRequest dsRequest = request.getDsRequest();

		if (m_logger.isDebugEnabled())
			m_logger.debug("Method Id = [" +
					dsRequest.getMethodID() + AseStrings.SQUARE_BRACKET_CLOSE);

		// First to get a lock on the IC object. All the processing happens
		// Inside the lock
		try {
			this.acquire();
		}
		catch (AseLockException e) {
			m_logger.error("sendRequest. Failed to acquire Lock" +
					e.toString() + logId);
			throw new AseSipSessionException("System Failure Exception");
		}

		accessed();
		//BpInd 17365
		/*	Destination destination=((AseApplicationSession)this.getApplicationSession()).getDestination();
			if (m_logger.isDebugEnabled())
				m_logger.debug("the destination is being set==>"+destination);
		  request.setDestination((Destination)destination.clone());*/


		// Depending on the type send it to the appropriate helper method
		try {
			if (request.isInitial())
				sendInitialRequest(request);
			else
				sendSubsequentRequest(request);

			this.updatePrintInfo(request,System.currentTimeMillis(), "outgoing");

			// As the request has been successfully sent to the connector,
			// isCommitted flag has to be set to true
			request.setCommitted();

			//B2BUA Helper - Check for the pending message
			m_b2bHandler.sendRequest(request);

			//Counter Increment for UAC to check pending transaction
			++counter;
		}
		catch (AseSipSessionException exp)	{
			throw exp;
		}
		catch (IOException ioe)	{
			throw ioe;
		} catch (Exception e) {
			m_logger.error("Exception handling the request", e);
			throw new AseSipSessionException(e.toString());
		}finally {
			// Release the IC lock
			try {
				this.release();
			}
			catch (AseLockException le) {
				m_logger.error("Exception releasing the lock", le);
			}
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession sendRequest" + getLogId());
	}


	void sendInitialRequest(AseSipServletRequest request)
	throws AseSipSessionException, IOException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession sendInitialRequest" +
					getLogId() +" isProxyRequest "+ request.isContactHasBrackets());

		//IPv6

		if(m_sipConnector.checkConnectorIPType()){
			if(request.getRequestURI().isSipURI() &&!matchRequestURI(request)){
				throw new IllegalArgumentException("Cannot send IPv4 request to IPv6 interface and vice-versa");
			}
		}else{
			if(request.getRequestURI().isSipURI() && !matchDefaultRequestURI(request)){
				throw new IllegalArgumentException("Cannot send IPv4 request to IPv6 interface and vice-versa");
			}
		}
		//BpInd 17365
		Destination destination=((AseApplicationSession)this.getApplicationSession()).getDestination();
		if (m_logger.isDebugEnabled())
			m_logger.debug("the destination is being set==>"+destination);
		request.setDestination((Destination)destination.clone());

		// set the role of this session if not already set
		if (ROLE_UNDEFINED == getRole()) {
			setRole(ROLE_UAC);
			//m_origRequest = request;
		}
		
		m_origRequest = request;

		//This is done to ensure that Service should get this attribute in case of client transaction 
		//i.e., when invite is sent. Now in case of FT Service needs to send the CANCEL.
		//Before this change service was getting the request from attribute in the app session
		//that object doesn't have the client transaction. The same thing is being done for INVITE
		//coming in.
		this.setAttribute(Constants.ORIG_REQUEST, request);
		
		// Get the appropriate message handler and let it have a go at
		// the message
		DsSipRequest dsRequest = request.getDsRequest();

		//somesh
		DsSipContactHeader contactHdr = (DsSipContactHeader)dsRequest.getContactHeader();
		String method =((dsRequest.getMethod()).toString()).trim();
		int transport = DsSipTransportType.UDP;
		DsSipRouteHeader routeHdr = null;
		
		if(request.isContactHasBrackets()){
			if (m_logger.isDebugEnabled())
				m_logger.debug("The Request is proxy request check for incoming contact hdr " );
				if (m_logger.isDebugEnabled())
					m_logger.debug("set addbrackets  true default is true donot set");
			} else {
				contactHdr.setAddBrackets(false);
				if (m_logger.isDebugEnabled())
					m_logger.debug("set addbrackets false ");
			}
		//}
		if(!method.equalsIgnoreCase(AseSipConstants.STR_REGISTER)&& (contactHdr != null)) {
			try {
				routeHdr = (DsSipRouteHeader)dsRequest.getHeaderValidate(DsSipConstants.ROUTE);
			}catch (DsSipParserException e) {
				m_logger.error(e.toString());
			}catch (DsSipParserListenerException ee) {
				m_logger.error(ee.toString());
			}

			if(routeHdr != null) {
				DsURI routeUri = routeHdr.getURI();
				if( (routeUri.isSipURL()) && (((DsSipURL)routeUri).hasTransport()) ) {
					transport = ((DsSipURL)routeUri).getTransportParam();
				}
			}else {
				DsURI reqUri = dsRequest.getURI();
				if( reqUri.isSipURL() && ( ((DsSipURL)reqUri).hasTransport() ) ) {
					transport = ((DsSipURL)reqUri).getTransportParam();
				}
			}
		//	if (!(((DsSipURL)contactHdr.getURI())).hasTransport()) {
//				((DsSipURL) (contactHdr.getURI())).setTransportParam(transport);
			dsRequest.updateHeader(contactHdr, false);
				
//				if (m_logger.isDebugEnabled())
//					m_logger.debug("not updating contact as transport is not there==>");
//			}else{
//				
//				if (m_logger.isDebugEnabled())
//					m_logger.debug("not updating contact as transport is alreday there==>");
//			}
		}



		AseSipMessageHandler msgHandler =
			AseSipMessageHandlerFactory.getHandler(dsRequest.getMethodID(),
					this);

		int ret = 0;
		try {
			if(AseUtils.getCallPrioritySupport() == 1)      {
				nsepMessageHandler.sendInitialRequest(request, this);
			}
			ret = msgHandler.sendInitialRequest(request, this);
		}
		catch (AseSipMessageHandlerException e) {
			m_logger.error("Message handler Exception", e);
			throw new AseSipSessionException(e.toString());
		}

		  m_localParty = request.getFrom();
		  m_remoteParty = request.getTo();
		
		// If return value is NOOP
		if (true == msgHandler.isRetNoop(ret)) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Message Handler return NOOP" + getLogId());
				m_logger.debug("Leaving AseSipSession sendInitialRequest" +
						getLogId());
			}
			return;
		}

		if (true == msgHandler.isRetStateUpdate(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler requested State Update");

			try {
				m_sessionState.sendInitialRequest(request);
			}
			catch (AseSipSessionStateException e) {
				m_logger.error("Session state exception", e);
				throw new AseSipSessionException(e.toString());
			}
		}

		//Added to Capture Call Id in print-info for outgoing NOTIFY request
		
		if(request.getMethod().equals(AseStrings.NOTIFY) && m_sessionState.getCallId() == null){
			m_sessionState.setCallId(request.getCallId());
		}
		// Send request to the connector
		sendToConnector(request);

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession sendInitialRequest" +
					getLogId());
	}

	void sendSubsequentRequest(AseSipServletRequest request)
	throws AseSipSessionException, IOException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession sendSubsequentRequest" +
					getLogId());

		// Get the appropriate message handler and let it have a go at
		// the message
		DsSipRequest dsRequest = request.getDsRequest();
		AseSipMessageHandler msgHandler =
			AseSipMessageHandlerFactory.getHandler(dsRequest.getMethodID(),
					this);

		int ret = 0;
		try {
			if(AseUtils.getCallPrioritySupport() == 1)      {
				nsepMessageHandler.sendSubsequentRequest(request, this);
			}
			ret = msgHandler.sendSubsequentRequest(request, this);
		}
		catch (AseSipMessageHandlerException e) {
			m_logger.error("Message handler Exception", e);
			throw new AseSipSessionException(e.toString());
		}

		// If return value is NOOP
		if (true == msgHandler.isRetNoop(ret)) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Message Handler return NOOP" + getLogId());
				m_logger.debug("Leaving AseSipSession sendSubsequentRequest" +
						getLogId());
			}
			return;
		}

		if (true == msgHandler.isRetStateUpdate(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler return STATE_UPDATE" +
						getLogId());

			try {
				m_sessionState.sendSubsequentRequest(request);
			}
			catch (AseSipSessionStateException e) {
				m_logger.error("Session state exception", e);
				throw new AseSipSessionException(e.toString());
			}
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Message Handler return CONTINUE" + getLogId());

		// Strip top route header if it points to us
		AseSipRouteHeaderHandler.stripTopSelfRoute(request, this);

		// Send request to the connector
		sendToConnector(request);

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession sendSubsequentRequest" +
					getLogId());
	}

	/**
	 * Sets the default handler in the initial outgoing request
	 */
	void updateDefaultHandler() throws AseSipSessionException {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession updateDefaultHandler" +
					getLogId());

		if (null == getHandler()) {
			try {
				String defaultHandler =
					((AseApplicationSession)appSession).
					getContext().getDefaultHandlerName();
				setHandler(defaultHandler);
			}
			catch (javax.servlet.ServletException e) {
				m_logger.error("Exception setting default handler", e);
				throw new
				AseSipSessionException("Failed to set default " +
						"handler. " + e.toString());
			}  
		}
	}

	/**
	 * Invoked by the AseSipServletResponse send method
	 * Do some state processing and send the response to the connector
	 * for disptach over the network
	 */

	void sendResponse(AseSipServletResponse response)
	throws AseSipSessionException, IOException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession.sendResponse" + getLogId());
		((AseApplicationSession)this.appSession).getIc().activate();

		// First to get a lock on the IC object. All the processing happens
		// Inside the lock
		try {
			this.acquire();
		}
		catch (AseLockException e) {
			m_logger.error("Failed to acquire Lock", e);
			throw new AseSipSessionException("System Failure Exception");
		}

		try {
			

			// Retrieve the underlying Ds response object
			DsSipResponse dsResponse = response.getDsResponse();
			if (m_logger.isDebugEnabled()) {
				m_logger.debug(logId + logCallId + "CSeq Method = [" +
						dsResponse.getCSeqType() +
						"] Response Class = [" +
						dsResponse.getResponseClass() +
						"] Status Code = [" +
						dsResponse.getStatusCode() + AseStrings.SQUARE_BRACKET_CLOSE);
			}

			//somesh
			int status = response.getStatus();
			DsSipViaHeader viaHdr = null;
			DsSipContactHeader contactHdr = null;
			try {
				contactHdr = (DsSipContactHeader)dsResponse.getContactHeaderValidate();
			} catch (DsSipParserException e ) {
				m_logger.error(e.toString());
			}catch (DsSipParserListenerException ee) {
				m_logger.error(ee.toString());
			}

			if( !( (response.SC_MULTIPLE_CHOICES < status) && (status < response.SC_ALTERNATIVE_SERVICE) ) 
					&& ((response.getRequest()).isInitial()) 
					&& (contactHdr != null) ) {
				try {
					viaHdr = (DsSipViaHeader)dsResponse.getHeaderValidate(DsSipConstants.VIA);
				}catch (DsSipParserException e) {
					m_logger.error(e.toString());
				}catch (DsSipParserListenerException ee) {
					m_logger.error(ee.toString());
				}

				if(viaHdr != null) {
					int transport = viaHdr.getTransport();
//					((DsSipURL)(contactHdr.getURI())).setTransportParam(transport);
					dsResponse.updateHeader(contactHdr, false);
				}
			}





			// Change the last accessed time.
			accessed();

			// Retrieve the appropriate message handler and see what it thinks
			AseSipMessageHandler msgHandler =
				AseSipMessageHandlerFactory.getHandler(dsResponse.getMethodID(),
						this);

			int ret = 0;
			try {
				if(AseUtils.getCallPrioritySupport() == 1)      {
					nsepMessageHandler.sendResponse(response, this);
				}
				ret = msgHandler.sendResponse(response, this);
			}
			catch (AseSipMessageHandlerException e) {
				m_logger.error("Message handler exception", e);
				throw new AseSipSessionException(e.toString());
			}

			// isCommitted has to set to true as this reponse is being sent upstream
			// even in case of NOOP, isCommitted has to set true
			response.setCommitted();

			//B2BUA Helper - Check for the pending message
			if(response.isFinalResponse()){
				((AseSipServletRequest)response.getRequest()).setCommitted();
			}
			m_b2bHandler.sendResponse(response);


			// If return value is NOOP
			if (true == msgHandler.isRetNoop(ret)) {

				if (m_logger.isDebugEnabled()) {
					m_logger.debug("Message Handler return NOOP" + getLogId());
					m_logger.debug("Leaving AseSipSession.sendResponse" +
							getLogId());
				}

				return;
			}

			if (true == msgHandler.isRetStateUpdate(ret)) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Message Handler return STATE_UPDATE" +
							getLogId());

				try {
					m_sessionState.sendResponse(response);
				}
				catch (AseSipSessionStateException e) {
					m_logger.error("Session state exception", e);
					throw new AseSipSessionException(e.toString());
				}
			}

			// Return value is CONTINUE
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler return CONTINUE" + getLogId());

			// BPUsa07541: [
			// If this a 200 response for a BYE request, then the call is terminated
			// and we can update any CDR object associated with this session.
			CDR cdr = (CDR)this.getAttribute(Constants.CDR_KEY);
			if (cdr != null) {
				// In case of UAS mode set call completion code as first 
				// response for invite by application for originating leg 
				if(cdr.get(CDR.CALL_COMPLETION_STATUS_CODE)==null && getRole()!=ROLE_UAC && getRole()!=ROLE_PROXY)
					cdr.set(CDR.CALL_COMPLETION_STATUS_CODE, new Integer(status));
				
				if(this.getState()==State.TERMINATED) {
					Object callEndTimestamp = cdr.get(CDR.CALL_END_TIMESTAMP);
					Object callStartTimestamp = cdr.get(CDR.CALL_START_TIMESTAMP);
					Object callDuration = cdr.get(CDR.CALL_DURATION_MSECS);

					if (callEndTimestamp == null) {
						cdr.set(CDR.CALL_END_TIMESTAMP, callEndTimestamp = new Long(System.currentTimeMillis()));
					}
					if (callDuration == null && callStartTimestamp != null) {
						cdr.set(CDR.CALL_DURATION_MSECS, new Long(Long.parseLong(callEndTimestamp.toString()) - Long.parseLong(callStartTimestamp.toString())));
					}
				}
			}
			// ]


			// Send the response to the connector
			sendToConnector(response);
			this.updatePrintInfo(response, System.currentTimeMillis(), "outgoing");

		} finally{
			// Release the lock and get out of here
			try {
				this.release();
			}
			catch (AseLockException le) {
				m_logger.error("Failed to release Lock", le);
			}
		}
		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession.sendResponse" + getLogId());

		if(invalidateWhenReady == true && isReadyToInvalidate()== true)
			objectReadyToInvalidate();
	}
	/**
	 * Send the request to the connector
	 */
	void sendToConnector(AseSipServletRequest request) throws IOException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession.sendToConnector" +
					getLogId());

		m_sach.sendRequest(request);

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession.sendToConnector" +
					getLogId());
	}

	/**
	 * Send the response to the connector
	 */
	void sendToConnector(AseSipServletResponse response) throws IOException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession.sendToConnector" +
					getLogId());

		m_sach.sendResponse(response);

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession.sendToConnector" +
					getLogId());
	}

	/**
	 * Invoked by the SIL when a new request is received from the stack.
	 * This will be called for subsequent requests.
	 * For initial requests there is no session object at the SIL level
	 */

	public int recvRequest(AseSipServletRequest request) 
	throws AseStrayMessageException, AseCannotCancelException,
	AseOutOfSequenceException, AseSessionInvalidException,
	IllegalStateException, AseDialogInvalidException,
	AseSubscriptionInvalidException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession.recvRequest" + getLogId());

		this.updatePrintInfo(request, System.currentTimeMillis(), "incoming");

		((AseApplicationSession)this.appSession).getIc().activate();

		//FT Handling strategy Update: Replication will be done for the provisional
		//responses as well, so in order to activate requests on the stand by server
		//storing the requests in the application session. This is done to replicate 
		//the server transaction and its associated connection object.
		//This is done to make sure the message map maintained in the Application Session
		//is in sync with the outstanding requests we have in the invitation handler
		//This is removed from here as we are ultimately adding the request to the 
		///MESSAGE_MAP during serialization of request through storeMessagAttr method 
		//request.addToApplicationSession();
		
		// Retrieve the DS request object
		DsSipRequest dsRequest = request.getDsRequest();

		if (m_logger.isDebugEnabled())
			m_logger.debug("Method Id = [" +
					dsRequest.getMethodID() + AseStrings.SQUARE_BRACKET_CLOSE);

		accessed();

		// Get the appropriate message handler and see what it has to say
		AseSipMessageHandler msgHandler =
			AseSipMessageHandlerFactory.getHandler(dsRequest.getMethodID(),
					this);

		int ret = 0;
		try {
			if(AseUtils.getCallPrioritySupport() == 1)      {
				nsepMessageHandler.recvRequest(request, this);
			}
			ret = msgHandler.recvRequest(request, this);
		}
		catch (AseSipMessageHandlerException e) {
			m_logger.error("Message handler exception", e);
			if (m_logger.isDebugEnabled())
				m_logger.debug("Return NOOP. " +
						"Leaving AseSipSession.recvRequest" +
						getLogId());
			return NOOP;
		}

		// NOOP return value
		if (true == msgHandler.isRetNoop(ret)) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Message Handler returned NOOP" + getLogId());
				m_logger.debug("Leaving AseSipSession.recvRequest" +
						getLogId());
			}
			return NOOP;
		}

		if (true == msgHandler.isRetStateUpdate(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler returned STATE_UPDATE" +
						getLogId());

			try {
				m_sessionState.recvRequest(request);
			}
			catch (AseSipSessionStateException e) {
				m_logger.error("Session state exception: " + getLogId(), e);

			}
		}

		// We can have either PROXY or CONTINUE not both
		if (true == msgHandler.isRetProxy(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler returned PROXY. " +
						"Proxying the request" +
						getLogId());

			try {
				proxyRequest(request);
			}
			catch (AseInvocationFailedException exp)	{
				// Don't log error here
			}
			catch (Exception e) {
				m_logger.error("proxyRequest Exception: " + getLogId(), e);
			}

			if (m_logger.isDebugEnabled())
				m_logger.debug("Return NOOP. " +
						"Leaving AseSipSession.recvRequest" +
						getLogId());

			return NOOP;
		}

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Message Handler returned CONTINUE" +
					getLogId());
			m_logger.debug("Return CONTINUE. " +
					"Leaving AseSipSession.recvRequest" +
					getLogId());
		}

		return CONTINUE;
	}

	/**
	 * Invoked by the SIL when a new response is received from the stack
	 */
	int recvResponse(AseSipServletResponse response)
	throws AseStrayMessageException, AseSessionInvalidException,
	IllegalStateException, Rel100Exception,
	AseDialogInvalidException, AseSubscriptionInvalidException {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession recvResponse" +
					getLogId());
		this.updatePrintInfo(response, System.currentTimeMillis(), "incoming");


		// As the response is received, isCommitted flag has to be set to true in case its a UAC
		if( role != ROLE_PROXY )
			response.setCommitted();
		((AseApplicationSession)this.appSession).getIc().activate();

		// Retrieve the underlying Ds response object
		DsSipResponse dsResponse = response.getDsResponse();
		if (m_logger.isDebugEnabled()) {
			m_logger.debug(getLogId() + " " + "CSeq Method = [" +
					dsResponse.getCSeqType() +
					"] Response Class = [" +
					dsResponse.getResponseClass() +
					"] Status Code = [" +
					dsResponse.getStatusCode() + AseStrings.SQUARE_BRACKET_CLOSE);
		}

		// Change the last accessed time.
		accessed();

		// Retrieve the appropriate message handler and see what it thinks
		AseSipMessageHandler msgHandler =
			AseSipMessageHandlerFactory.getHandler(dsResponse.getMethodID(),
					this);

		int ret = 0;
		try {
			if(AseUtils.getCallPrioritySupport() == 1)      {
				nsepMessageHandler.recvResponse(response, this);
			}
			ret = msgHandler.recvResponse(response, this);
		}
		catch (AseSipMessageHandlerException e) {
			m_logger.error("Message handler exception: " + getLogId(), e);
			if (m_logger.isDebugEnabled())
				m_logger.debug("Return NOOP. " +
						"Leaving AseSipSession.recvResponse" +
						getLogId());

			if (e.isSessionTermOrInvalid() && DsSipConstants.INVITE == response.getDsResponse().getMethodID()
					&& response.getStatus() >= 200) {
				m_logger.error("Race Condition when received final response "+ response.getStatus() +" while dialog is terminated");
				return ACK_RESPONSE_ONLY;
			} else {
				return NOOP;	
			}
			
		}
		

		// If return value is NOOP
		if (true == msgHandler.isRetNoop(ret)) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Message Handler return NOOP" + getLogId());
				m_logger.debug("Return NOOP. " +
						"Leaving AseSipSession.recvResponse" +
						getLogId());
			}
			return NOOP;
		}

		if (true == msgHandler.isRetStateUpdate(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler return STATE_UPDATE" +
						getLogId());

			try {
				m_sessionState.recvResponse(response);
			}
			catch (AseSipSessionStateException e) {
				m_logger.error("Session state exception: " + getLogId(), e);
				if (m_logger.isDebugEnabled())
					m_logger.debug("Return NOOP. " +
							"Leaving AseSipSession.recvResponse" +
							getLogId());

				return NOOP;
			}
		}

		// FT handling strategy update: Replication will be done after receiving
		// 200 OK final response for INVITE and also for in progress calls only 
		// in case of 100rel coming in either REQUIRE header or SUPPOPRTED 
		// header of the INVITE request
		
		m_replicationHandler.recvResponse(response, this);
		
		// Return can be either of PROXY or CONTINUE
		if (true == msgHandler.isRetProxy(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler return PROXY. " +
						"Proxy the response. " + getLogId());

			try {
				proxyResponse(response);
			}
			catch (AseInvocationFailedException exp)	{
				// Don't log error here
			}
			catch (Exception e) {
				m_logger.error("proxyResponse exception: " + getLogId(), e);
			}

			if (m_logger.isDebugEnabled())
				m_logger.debug("Return NOOP. " +
						"Leaving AseSipSession.recvResponse" +
						getLogId());

			return NOOP;
		}
		
		if (true == msgHandler.isRetContinue(ret)) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Message Handler return CONTINUE" + getLogId());

			if (m_logger.isDebugEnabled())
				m_logger.debug("Return CONTINUE. " +
						"Leaving AseSipSession.recvResponse" +
						getLogId());
			return CONTINUE;
		}



		if (m_logger.isDebugEnabled())
			m_logger.debug("Message Handler return NOOP" + getLogId());

		if (m_logger.isDebugEnabled())
			m_logger.debug("Return NOOP. " +
					"Leaving AseSipSession.recvResponse" +
					getLogId());

		return NOOP;
	}

	/**
	 * Invoked by the SIL on ACK and PRACK timeouts.
	 * SIL generates SipErroEvent's, wraps them in AseEvent
	 * and calls this method
	 * We do not handle PRACK timeouts for now
	 */
	int recvEvent(AseEvent event)
	throws IllegalStateException, AseSessionInvalidException,
	AseDialogInvalidException{
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.recvEvent" + logId);
		}

		// If the state of this session is not VALID then throw exception
		if (AseProtocolSession.VALID != state) {
			m_logger.error("recvEvent. Throwing Exception. Session state " +
					"not VALID" + logId);
			throw new AseSessionInvalidException("Invalid Session");
		}

		// If dialog state is DLG_TERMINATED return NOOP
		// Allow timeout events for proxy sessions in TERMINATED state too
		if( (AseSipSessionState.STATE_TERMINATED == this.getSessionState())
				&& (role != ROLE_PROXY) ) {
			m_logger.error("recvEvent. Throwing Exception. DIalog state " +
					"DLG_TERMINATED" + logId);
			throw new AseDialogInvalidException("Invalid Dialog State");
		}

		// Check the event type. We should get either of
		// EVENT_SIP_ACK_ERROR or EVENT_SIP_PRACK_ERROR events.
		if (Constants.EVENT_SIP_ACK_ERROR == event.getType()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug(logId + logCallId +
				"ACK timeout Event received");
			}

			// For proxy no request is stored to be matched,
			// return CONTINUE
			if(role == ROLE_PROXY) {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("Return CONTINUE" + logId);
					m_logger.debug("Leaving AseSipSession.recvEvent" +
							logId);
				}
				return CONTINUE;
			}

			// Retrieve the SipErrorEvent from here
			SipErrorEvent sipEvent = (SipErrorEvent)(event.getData());
			// Retrieve the request object from the SipErrorEvent
			AseSipServletRequest sipRequest =
				(AseSipServletRequest)(sipEvent.getRequest());

			DsSipRequest dsRequest = sipRequest.getDsRequest();
			long cSeqNum = dsRequest.getCSeqNumber();

			// See if this is an ACK timeout for a 2XX response
			if (null != this.removeSuccessRequest(cSeqNum)) {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("ACK timeout for 2XX response. " +
							"Return CONTINUE" + getLogId()); 
					m_logger.debug("Leaving AseSipSession.recvEvent" +
							getLogId());
				}
				return CONTINUE;
			}

			// See if this is an ACK timeout for a non-2XX response
			if (null != this.removeFailureRequest(cSeqNum)) {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("ACK timeout for non-2XX response. " +
							"Return NOOP" + getLogId()); 
					m_logger.debug("Leaving AseSipSession.recvEvent" +
							getLogId());
				}
				return NOOP;
			}

			// If I am here there is no match
			m_logger.error("recvEvent. Stray ACK timeout event" + getLogId());
			if (m_logger.isDebugEnabled())
				m_logger.debug("Return NOOP. Leaving AseSipSession.recvEvent" +
						getLogId());
			return NOOP;
		}
		else if (Constants.EVENT_SIP_PRACK_ERROR == event.getType()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug(logId + logCallId +
				"PRACK timeout Event received. Return NOOP");
				m_logger.debug("Leaving AseSipSession.recvEvent" +
						logId);
			}
			return NOOP;
		}
		else if (Constants.EVENT_SERVER_TXN_TIMEOUT == event.getType()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug(logId + logCallId +
				"Server Tx timeout Event received.");
			}

			// For proxy no request is stored to be matched,
			// return CONTINUE
			if(role == ROLE_PROXY) {
				if (m_logger.isDebugEnabled()) {
					m_logger.debug("Return CONTINUE" + logId);
					m_logger.debug("Leaving AseSipSession.recvEvent" +
							logId);
				}
				return CONTINUE;
			}

			// Retrieve the AseSipServletRequest from here
			AseSipServletRequest sipRequest =
				(AseSipServletRequest)(event.getData());
			DsSipRequest dsRequest = sipRequest.getDsRequest();
			long cSeqNum = dsRequest.getCSeqNumber();

			Iterator iter = null;

			// Look for a match in the m_outstandingRequests
			iter = m_outstandingRequests.iterator();
			while (iter.hasNext()) {
				AseSipServletRequest sipReq =
					(AseSipServletRequest)(iter.next());
				if (sipReq.getDsRequest().getCSeqNumber() == cSeqNum) {
					iter.remove();
					m_sessionState.setSessionState(
							AseSipSessionState.STATE_TERMINATED); //NJADAUN
					if (m_logger.isDebugEnabled()) {
						m_logger.debug("Found matching request" +
								"Return NOOP" + logId);
						m_logger.debug("Leaving AseSipSession.recvEvent" +
								logId);
					}
					return NOOP;
				}
			}

			m_logger.error("recvEvent Server Xaction timeout. Stray Request");
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Return NOOP" + logId);
				m_logger.debug("Leaving AseSipSession.recvEvent" +
						logId);
			}
			return NOOP;
		}

		m_logger.error(logId + logCallId +
		"Unknown SipErrorEvent received. Return NOOP");
		return NOOP;
	}


	/**
	 * Invoked by SIL just before a request is sent on the network.
	 * Currently NOOP
	 */
	void requestPreSend(AseSipServletRequest request) {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering requestPreSend" + getLogId());

		DsSipRequest dsRequest = request.getDsRequest();
		AseSipMessageHandler msgHandler =
			AseSipMessageHandlerFactory.getHandler(dsRequest.getMethodID(),
					this);

		try {
			if(AseUtils.getCallPrioritySupport() == 1) {
				nsepMessageHandler.requestPreSend(request, this);
			}
			msgHandler.requestPreSend(request, this);
		} catch (AseSipMessageHandlerException e) {
			m_logger.error("Message handler Exception" + getLogId(), e);
		}

		m_replicationHandler.requestPreSend(request, this);

		if (m_logger.isDebugEnabled()) {
			m_logger.debug(getLogId() + logCallId + " Method Id = [" +
					dsRequest.getMethodID() + AseStrings.SQUARE_BRACKET_CLOSE);
			m_logger.debug("Leaving requestPreSend" + getLogId());
		}
	}

	/**
	 * Invoked by SIL just after a request is sent on the network.
	 * Currently NOOP
	 */
	void requestPostSend(AseSipServletRequest request) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.requestPostSend" + logId);
		}		

		DsSipRequest dsRequest = request.getDsRequest();
		AseSipMessageHandler msgHandler =
			AseSipMessageHandlerFactory.getHandler(dsRequest.getMethodID(),
					this);

		try {
			if(AseUtils.getCallPrioritySupport() == 1) {
				nsepMessageHandler.requestPostSend(request, this);
			}
			msgHandler.requestPostSend(request, this);
		} catch (AseSipMessageHandlerException e) {
			m_logger.error("Message handler Exception" + getLogId(), e);
		}

		m_replicationHandler.requestPostSend(request, this);

		if (m_logger.isDebugEnabled()) {
			m_logger.debug(logId + logCallId + " Method Id = [" +
					dsRequest.getMethodID() + AseStrings.SQUARE_BRACKET_CLOSE);
			m_logger.debug("Leaving AseSipSession.requestPostSend" + logId);
		}		
	}

	/**
	 * Invoked by SIL just before a response is sent on the network.
	 * The session can tweak this message is desired.
	 * The session definitely adds a tag to the TO header if required
	 */
	void responsePreSend(AseSipServletResponse response) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.responsePreSend" + logId);
		}

		// Get the DsSipResponse from the Ase response
		DsSipResponse dsResponse = response.getDsResponse();
		// Retrieve the statusCode and the response class for the response
		int responseClass = dsResponse.getResponseClass();
		int statusCode = dsResponse.getStatusCode();

		if (m_logger.isDebugEnabled()) {
			m_logger.debug(logId + logCallId + "CSeq Method = [" +
					dsResponse.getCSeqType() +
					"] Response Class = [" + responseClass +
					"] Status Code = [" + statusCode + AseStrings.SQUARE_BRACKET_CLOSE);
		}

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.responsePreSend" + logId);
		}
	}

	/**
	 * Invoked by SIL just after a response is sent on the network.
	 * NOOP for now
	 */
	void responsePostSend(AseSipServletResponse response) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.responsePostSend" + logId);
		}

		// Get the DsSipResponse from the Ase response
		DsSipResponse dsResponse = response.getDsResponse();

		// Retrieve the statusCode and the response class for the response
		int responseClass = dsResponse.getResponseClass();
		int statusCode = dsResponse.getStatusCode();

		if (m_logger.isDebugEnabled()) {
			m_logger.debug(logId + logCallId + "CSeq Method = [" +
					dsResponse.getCSeqType() + "] Response Class = [" +
					responseClass +
					"] Status Code = [" + statusCode + AseStrings.SQUARE_BRACKET_CLOSE);
		}

		// Generate the replication event for completion of transaction
		m_replicationHandler.responsePostSend(response, this);

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.responsePostSend" + logId);
		}
	}


	private boolean matchRequestURI(AseSipServletRequest request){

		URI reqURI = request.getRequestURI();
		String requestURI = ((AseSipURIImpl)reqURI).getHost();

		try {

			InetAddress reqURIAddress = InetAddress.getByName(requestURI);
			InetAddress outboundAddress  = null;
			boolean isSessionOutbound = false;
			boolean isReqUriIpv4 = (reqURIAddress instanceof Inet4Address) ? true : false;

			if(this.getOutboundInterface()!=null){
				outboundAddress =  InetAddress.getByName(this.getOutboundInterface().getHost());
				isSessionOutbound = true;
			}

			AseProxyImpl proxyImpl = (AseProxyImpl)request.getProxy(false);
			if(proxyImpl!=null){
				AseProxyBranch branch = ((AseProxyImpl)request.getProxy()).getBranches(reqURI);
				SipURI proxyOutboundURI = proxyImpl.getProxyOutboundInterface();
				SipURI branchOutboundURI = null;

				if(branch != null){
					branchOutboundURI = branch.getOutboundInterface();
				}

				if(branchOutboundURI != null){
					InetAddress branchAddress = InetAddress.getByName(branchOutboundURI.getHost());
					return ((branchAddress instanceof Inet6Address && !isReqUriIpv4 )
							|| (branchAddress instanceof Inet4Address && isReqUriIpv4 ));
				}else if(proxyOutboundURI!=null){
					InetAddress proxyAddress = InetAddress.getByName(proxyOutboundURI.getHost());
					return ((proxyAddress instanceof Inet6Address && !isReqUriIpv4 )
							|| (proxyAddress instanceof Inet4Address && isReqUriIpv4 ));
				}else if(!isSessionOutbound){ 
					return (reqURIAddress instanceof Inet4Address);
				}else{
					return((!isReqUriIpv4 && (outboundAddress instanceof Inet6Address))
							|| (isReqUriIpv4 && (outboundAddress instanceof Inet4Address)));
				}
			}else if(!isSessionOutbound){
				return (reqURIAddress instanceof Inet4Address);
			}else{ 
				return((!isReqUriIpv4 && (outboundAddress instanceof Inet6Address))
						|| (isReqUriIpv4 && (outboundAddress instanceof Inet4Address)));
			}
		}catch (Exception e) {
			m_logger.error("Exception :" + e);
		}
		return true;
	}
	
	private boolean matchDefaultRequestURI(AseSipServletRequest request){
		URI reqURI = request.getRequestURI();
		
		String requestURI = null;
		if (reqURI instanceof SipURI) {
			
			requestURI = ((AseSipURIImpl) reqURI).getHost();
		} else if (reqURI instanceof TelURL) {
			
			if (!((TelURL) reqURI).isGlobal()) {
				requestURI = ((TelURL) reqURI).getPhoneContext();
			}

		}
		 requestURI = ((AseSipURIImpl)reqURI).getHost();
		try {
			InetAddress reqURIAddress =  InetAddress.getByName(requestURI);
			if(m_sipConnector.getIpv6Address()== null){
				return (reqURIAddress instanceof Inet4Address);
			}else{
				return (reqURIAddress instanceof Inet6Address);
			}
		}catch(UnknownHostException e) {
			m_logger.error("Unknown Host :" + requestURI );
		}
		return true;
	}


	/**
	 * Acquires a lock on the AseIc object
	 */
	void acquire() throws AseLockException {
		if (appSession != null) {
			AseIc icObject = ((AseApplicationSession) appSession).getIc();
			if (null == icObject)
				throw new AseLockException("Null AseIc in AseSipSession");

			icObject.acquire();
		}
	}

	/**
	 * Releases a lock on the AseIc object
	 */
	void release() throws AseLockException {
		if(appSession!=null){
		AseIc icObject = ((AseApplicationSession)appSession).getIc();
		if (null == icObject)
			throw new AseLockException("Null AseIc in AseSipSession");

		icObject.release();
		}
	}

	/**
	 * Dispatches a SipSessionEvent to all registered 
	 * SipSessionActivationListeners to notify them of this SipSession's
	 * activation.
	 */
	private void genActivationEvent() {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Notifying all SipSessionActivationListeners of the SipSession's activation.");
		}

		SipSessionEvent event = null;

		Iterator names = this.getAttributeNamesIterator();

		if (names != null) {        
			while (names.hasNext()) {
				Object obj = this.getAttribute((String)names.next());                        

				if (obj instanceof SipSessionActivationListener) {
					if (event == null) {
						event = new SipSessionEvent(this);
					}
					try { 
						((SipSessionActivationListener)obj).sessionDidActivate(event);
					} catch (Throwable th) {
						m_logger.error(th.getMessage(), th);
					}
				}
			}
		}

		if (event == null) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("No SipSessionActivationListeners are currently registered with SipSession.");
			}
		} else if (m_logger.isDebugEnabled()) {
			m_logger.debug("Successfully notified all SipSessionActivationListeners");
		}
	}


	/**
	 * Dispatches a SipSessionEvent to all registered 
	 * SipSessionActivationListeners to notify them of this SipSession's
	 * passivation.
	 */
	private void genPassivationEvent() {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Notifying all SipSessionActivationListeners of the SipSession's passivation.");
		}

		SipSessionEvent event = null;

		Iterator names = this.getAttributeNamesIterator();

		if (names != null) {        
			while (names.hasNext()) {
				Object obj = this.getAttribute((String)names.next());                        

				if (obj instanceof SipSessionActivationListener) {
					if (event == null) {
						event = new SipSessionEvent(this);
					}
					try { 
						((SipSessionActivationListener)obj).sessionWillPassivate(event);
					} catch (Throwable th) {
						m_logger.error(th.getMessage(), th);
					}
				}
			}
		}

		if (event == null) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("No SipSessionActivationListeners are currently registered with SipSession.");
			}
		} else if (m_logger.isDebugEnabled()) {
			m_logger.debug("Successfully notified all SipSessionActivationListeners");
		}
	}

	/**
	 * Generate a SipSessionBindingEvent and invoke the attributeAdded
	 * callback on the SipSessionAttributeListeners.
	 * If no listener is registered this return false
	 */
	private boolean genAttributeAddedEvent(String attributeName) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.genAttributeAddedEvent" +
					logId);
		}

		// Check if the SipSessionAttributeListener is registered
		// If not then return false
		// If there are no listeners registered the getListeners method
		// will return null as opposed to an empty iterator
		if (null == appSession) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("No Application session found" +
						logId);
				m_logger.debug("Leaving AseSipSession.genAttributeAddedEvent" +
						logId);
			}
			return false;
		}

		Iterator listeners = ((AseApplicationSession)appSession).getContext().
		getListeners(SipSessionAttributeListener.class).iterator();
		if (null == listeners || !listeners.hasNext()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("SipSessionAttributeListener not registered" +
						logId);
				m_logger.debug("Leaving AseSipSession.genAttributeAddedEvent" +
						logId);
			}
			return false;
		}

		// Create a SipSessionBindingEvent and invoke listener's
		// attributeAdded callback
		SipSessionBindingEvent event =
			new SipSessionBindingEvent(this, attributeName);

		for ( ; listeners != null && listeners.hasNext(); ){
			SipSessionAttributeListener listener =
				(SipSessionAttributeListener)listeners.next();
			try {
				listener.attributeAdded(event);
			} catch (Throwable th) {
				m_logger.error(th.getMessage(), th);
			}
		}

		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.genAttributeAddedEvent" +
					logId);
		}

		return true;
	}

	/**
	 * Generate a SipSessionBindingEvent and invoke the attributeRemoved
	 * callback on the SipSessionAttributeListeners.
	 * If no listener is registered this return false
	 */
	private boolean genAttributeRemovedEvent(String attributeName) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.genAttributeRemovedEvent" +
					logId);
		}

		// Check if the SipSessionAttributeListener is registered
		// If not then return false
		// If there are no listeners registered the getListeners method
		// will return null as opposed to an empty iterator
		if (null == appSession) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("No Application session found" +
						logId);
				m_logger.debug("Leaving AseSipSession.genAttributeRemovedEvent" +
						logId);
			}
			return false;
		}
		Iterator listeners = ((AseApplicationSession)appSession).getContext().
		getListeners(SipSessionAttributeListener.class).iterator();
		if (null == listeners || !listeners.hasNext()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("SipSessionAttributeListener not registered" +
						logId);
				m_logger.debug("Leaving AseSipSession.genAttributeRemovedEvent" +
						logId);
			}
			return false;
		}

		// Create a SipSessionBindingEvent and invoke listener's
		// attributeRemoved callback
		SipSessionBindingEvent event =
			new SipSessionBindingEvent(this, attributeName);

		for ( ; listeners != null && listeners.hasNext(); ){
			SipSessionAttributeListener listener =
				(SipSessionAttributeListener)listeners.next();
			try {
				listener.attributeRemoved(event);
			} catch (Throwable th) {
				m_logger.error(th.getMessage(), th);
			}
		}
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.genAttributeRemovedEvent" +
					logId);
		}
		return true;
	}

	/**
	 * Generate a SipSessionBindingEvent and invoke the attributeReplaced
	 * callback on the SipSessionAttributeListeners.
	 * If no listener is registered this return false
	 */
	private boolean genAttributeReplacedEvent(String attributeName) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.genAttributeReplacedEvent" +
					logId);
		}

		// Check if the SipSessionAttributeListener is registered
		// If not then return false
		// If there are no listeners registered the getListeners method
		// will return null as opposed to an empty iterator
		if (null == appSession) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("No Application session found" +
						logId);
				m_logger.debug("Leaving AseSipSession.genAttribute" +
						"ReplacedEvent" +
						logId);
			}
			return false;
		}
		Iterator listeners = ((AseApplicationSession)appSession).getContext().
		getListeners(SipSessionAttributeListener.class).iterator();
		if (null == listeners || !listeners.hasNext()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("SipSessionAttributeListener not registered" +
						logId);
				m_logger.debug("Leaving AseSipSession.genAttributeReplacedEvent"
						+ logId);
			}
			return false;
		}

		// Create a SipSessionBindingEvent and invoke listener's
		// attributeReplaced callback
		SipSessionBindingEvent event =
			new SipSessionBindingEvent(this, attributeName);

		for ( ; listeners != null && listeners.hasNext(); ){
			SipSessionAttributeListener listener =
				(SipSessionAttributeListener)listeners.next();
			try {
				listener.attributeReplaced(event);
			} catch (Throwable th) {
				m_logger.error(th.getMessage(), th);
			}
		}
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.genAttributeReplacedEvent" +
					logId);
		}
		return true;
	}

	/**
	 * Generate a SipSessionBindingEvent and invoke the valueBound
	 * callback on the SipSessionBindingListeners.
	 * If no listener is registered this return false
	 */
	private boolean genValueBoundEvent(String attributeName, Object attributeObject) {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.genValueBoundEvent" + logId);
		}

		if(attributeObject instanceof SipSessionBindingListener) {
			SipSessionBindingEvent event = new SipSessionBindingEvent(this, attributeName);
			((SipSessionBindingListener)attributeObject).valueBound(event);
		}

		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.genValueBoundEvent" + logId);
		}

		return true;
	}

	/**
	 * Generate a SipSessionBindingEvent and invoke the valueUnbound
	 * callback on the SipSessionBindingListeners.
	 * If no listener is registered this return false
	 */
	private boolean genValueUnboundEvent(String attributeName, Object attributeObject) {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.genValueUnboundEvent" + logId);
		}

		if(attributeObject instanceof SipSessionBindingListener) {
			SipSessionBindingEvent event = new SipSessionBindingEvent(this, attributeName);
			((SipSessionBindingListener)attributeObject).valueUnbound(event);
		}

		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.genValueUnboundEvent" + logId);
		}

		return true;
	}

	public void setApplicationSession(SipApplicationSession session, int index) {
		super.setApplicationSession(session, index);

		//BPInd10505 == sessionCreated was not invoked, 
		//Application Session was not associated when this session object is constructed.
		//So generate this event, when it is associated with the Application Session.
		//We need to generate this event only on the ACTIVE side during initial creation.
		//If it is activated on the Standby, we don't need to generate this event,
		//instead the sessionDidActivate() should be generated 
		//which is already taken care in activate() method. 
		//So just taking care of ACTIVE side here.
		if(appSession != null && ((AseApplicationSession)appSession).getIc().isActive()){
			genCreationEvent();
		}

		//Bug 6040
		if(appSession != null && isFirstToInvalidate) {
			invalidateWhenReady = session.getInvalidateWhenReady();
			isFirstToInvalidate = false;
		}
	}

	/**
	 * Generate a SipSessionEvent and invoke the sessionCreated
	 * callback on the SipSessionListeners.
	 */
	private void genCreationEvent() {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.genCreationEvent" +
					logId);
		}

		// Check if the SipSessionListener is registered
		// If there are no listeners registered the getListeners method
		// will return null as opposed to an empty iterator
		if (null == appSession) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("No Application session found" +
						logId);
				m_logger.debug("Leaving AseSipSession.genCreationEvent" +
						logId);
			}
			return;
		}
		Iterator listeners = ((AseApplicationSession)appSession).getContext().
		getListeners(SipSessionListener.class).iterator();
		if (null == listeners || !listeners.hasNext()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("SipSessionListener not registered" +
						logId);
				m_logger.debug("Leaving AseSipSession.genCreationEvent"
						+ logId);
			}
			return;
		}

		// Create a SipSessionEvent and invoke listener's
		// sessionCreated callback
		SipSessionEvent event = new SipSessionEvent(this);

		for ( ; listeners != null && listeners.hasNext(); ){
			SipSessionListener listener =
				(SipSessionListener)listeners.next();
			try {
				listener.sessionCreated(event);
			} catch (Throwable th) {
				m_logger.error(th.getMessage(), th);
			}
		}
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.genCreationEvent" +
					logId);
		}
	}

	/**
	 * Generate a SipSessionEvent and invoke the sessionDestroyed
	 * callback on the SipSessionListeners.
	 */
	private void genDestroyedEvent() {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.genDestroyedEvent" +
					logId);
		}

		// Check if the SipSessionListener is registered
		// If there are no listeners registered the getListeners method
		// will return null as opposed to an empty iterator
		if (null == appSession) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("No Application session found" +
						logId);
				m_logger.debug("Leaving AseSipSession.genDestroyedEvent" +
						logId);
			}
			return;
		}
		Iterator listeners = ((AseApplicationSession)appSession).getContext().
		getListeners(SipSessionListener.class).iterator();
		if (null == listeners || !listeners.hasNext()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("SipSessionListener not registered" +
						logId);
				m_logger.debug("Leaving AseSipSession.genDestroyedEvent"
						+ logId);
			}
			return;
		}

		// Create a SipSessionEvent and invoke listener's
		// sessionDestroyed callback
		SipSessionEvent event = new SipSessionEvent(this);

		for ( ; listeners != null && listeners.hasNext(); ){
			SipSessionListener listener =
				(SipSessionListener)listeners.next();
			try {
				listener.sessionDestroyed(event);
			} catch (Throwable th) {
				m_logger.error(th.getMessage(), th);
			}
		}
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.genDestroyedEvent" +
					logId);
		}
	}

	/**
	 * Invoke the noAckReceived callback on the SipErrorListener passing
	 * in the received event
	 */
	private void genAckTimeoutEvent(SipErrorEvent event) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.genAckTimeoutEvent" +
					logId);
		}

		// Check if the SipErrorListener is registered
		// If there are no listeners registered the getListeners method
		// will return null as opposed to an empty iterator
		if (null == appSession) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("No Application session found" +
						logId);
				m_logger.debug("Leaving AseSipSession.genAckTimeoutEvent" +
						logId);
			}
			return;
		}
		Iterator listeners = ((AseApplicationSession)appSession).getContext().
		getListeners(SipErrorListener.class).iterator();
		if (null == listeners || !listeners.hasNext()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("SipErrorListener not registered" +
						logId);
				m_logger.debug("Leaving AseSipSession.genAckTimeoutEvent"
						+ logId);
			}
			return;
		}

		// Invoke the listeners noAckReceived callback
		for ( ; listeners != null && listeners.hasNext(); ){
			SipErrorListener listener = (SipErrorListener)listeners.next();
			try {	
				listener.noAckReceived(event);
			} catch (Throwable th) {
				m_logger.error(th.getMessage(), th);
			}
		}
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.genAckTimeoutEvent" +
					logId);
		}
	}

	/**
	 * Invoke the noPrackReceived callback on the SipErrorListener passing
	 * in the received event
	 */
	private void genPrackTimeoutEvent(SipErrorEvent event) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Entering AseSipSession.genPrackTimeoutEvent" +
					logId);
		}

		// Check if the SipErrorListener is registered
		// If there are no listeners registered the getListeners method
		// will return null as opposed to an empty iterator
		if (null == appSession) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("No Application session found" +
						logId);
				m_logger.debug("Leaving AseSipSession.genPrackTimeoutEvent" +
						logId);
			}
			return;
		}
		Iterator listeners = ((AseApplicationSession)appSession).getContext().
		getListeners(SipErrorListener.class).iterator();
		if (null == listeners || !listeners.hasNext()) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("SipErrorListener not registered" +
						logId);
				m_logger.debug("Leaving AseSipSession.genPrackTimeoutEvent"
						+ logId);
			}
			return;
		}

		// Invoke the listeners noPrackReceived callback
		for ( ; listeners != null && listeners.hasNext(); ){
			SipErrorListener listener =
				(SipErrorListener)listeners.next();
			try {
				listener.noPrackReceived(event);
			} catch (Throwable th) {
				m_logger.error(th.getMessage(), th);
			}
		}
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("Leaving AseSipSession.genPrackTimeoutEvent" +
					logId);
		}
	}

	public boolean isReadyForReplication() {
		return m_replicationHandler.isReadyForReplication();
	}

	/**
	 * Increment the count of outstanding requests
	 */
	public void incrementPrCount() {
		m_replicationHandler.incrementPrCount();
	}

	/**
	 * Decrement the count of outstanding requests
	 */
	public void decrementPrCount() {
		m_replicationHandler.decrementPrCount();
	}

	/**
	 * Reset the outstanding request count to 0
	 */
	void resetPrCount() {
		m_replicationHandler.resetPrCount();
	}

	/**
	 * Gets session role
	 */
	public int getRole() {
		return role;
	}

	/**
	 * Sets session role
	 */
	public void setRole(int newRole) {
		if(m_logger.isDebugEnabled())
			m_logger.debug("ROLE set to : " + newRole);

		role = newRole;
	}

	//
	// -- Additions for proxy start
	//

	public boolean isHandlingProxy() {
		return m_handlingProxyResponse;
	}

	/**
	 * This method check whether the passed SIP message has come from
	 * upstream or downstream.
	 *
	 * @param message SIP message whose direction is to be checked
	 *
	 * @return DIR_UPSTREAM/DIR_DOWNSTREAM
	 */
	public int checkDirection(AseSipServletMessage message) {
		if(m_logger.isDebugEnabled())
			m_logger.debug("checkDirection(AseSipServletMessage) called.");

		int direction;
		DsByteString dsFromTag = null;
		if(m_sessionState.getUpstreamDialogId() != null) {
			dsFromTag = m_sessionState.getUpstreamDialogId().getFromTag();
		} else {
			dsFromTag = m_origRequest.getDialogId().getFromTag();
		}

		if(message instanceof AseSipServletRequest) {
			// message is a SIP request
			if(DsByteString.equals(dsFromTag, message.getDsMessage().getFromTag())) {
				direction = DIR_UPSTREAM;
			} else {
				direction = DIR_DOWNSTREAM;
			}

			if(m_logger.isDebugEnabled())
				m_logger.debug("SIP request direction is " + direction);
		} else {
			// message is a SIP response
			if(DsByteString.equals(dsFromTag, message.getDsMessage().getFromTag())) {
				direction = DIR_DOWNSTREAM;
			} else {
				direction = DIR_UPSTREAM;
			}

			if(m_logger.isDebugEnabled())
				m_logger.debug("SIP response direction is " + direction);
		}

		return direction;
	}

	/**
	 * This method is used only by associated proxy object to invoke 
	 * application for response or request.
	 *
	 * @param request request for which servlet is to be invoked.
	 * This is null for response invocations.
	 * @param response response for which servlet is to be invoked. 
	 * This is null for request invocations.
	 */
	public void invokeServlet(AseSipServletRequest request,
			AseSipServletResponse response) {

		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering invokeServlet" + getLogId());

		if (request != null) {
			try {
				super.handleRequest(request, response);
			} 
			catch (Exception e) {
				m_logger.error("Exception invoking servlet: " 
						+"for call Id[" + request.getCallId()
						+AseStrings.SQUARE_BRACKET_CLOSE, e);
			}
		} 
		else {
			m_handlingProxyResponse = true;

			try {
				super.handleResponse(request, response);
			} 
			catch (Exception e) {
				m_logger.error("Exception invoking servlet: " 
						+"for call Id[" + request.getCallId()
						+AseStrings.SQUARE_BRACKET_CLOSE, e);
			}

			m_handlingProxyResponse = false;
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving invokeServlet" + getLogId());
	}

	public AseSipServletRequest getOrigRequest() {
		return m_origRequest;
	}

	public void setProxy(AseProxyImpl proxy) {
		m_proxy = proxy;
	}

	AseProxyImpl getProxy() {
		return m_proxy;
	}

	//
	// -- Additions for proxy end
	//

	transient private static Logger m_logger = Logger.getLogger(AseSipSession.class);

	transient private static OverloadControlManager ocmManager = 
		(OverloadControlManager)Registry.lookup(Constants.NAME_OC_MANAGER);

	transient private static int ocmId = 
		ocmManager.getParameterId(OverloadControlManager.PROTOCOL_SESSION_COUNT);

	//
	// -- Additions for proxy start
	//

	private transient AseSipServletRequest m_origRequest = null;
	
	private AseSipServletResponse m_transResponse = null;

	transient private boolean m_handlingProxyResponse = false;

	transient private AseProxyImpl m_proxy = null;

	transient private AseProxyServerTransactionTimer m_proxyTimer = null;

	transient private boolean m_supervised = false;

	transient private boolean m_recordRoute = false;

	transient private SipURI m_recordRouteURI = null;

	transient private boolean m_isAddedIntoDMgr = false; // used at standby only

	private static int m_proxyTimerInterval = 30000; // 30 secs

	public static final int DIR_UPSTREAM = 1;

	public static final int DIR_DOWNSTREAM = 2;

	//
	// -- Additions for proxy end
	//

	transient private AseSipAppCompositionHandler m_sach = null;
	transient private static AseNsepMessageHandler nsepMessageHandler = new AseNsepMessageHandler();

	/**
	 * Reference to the SIP connector
	 */
	transient private AseSipConnector m_sipConnector;

	/**
	 * List of all requests for which a response has not been sent.
	 * List contains AseSipServletRequest objects
	 */
	transient private List m_outstandingRequests = new ArrayList();

	/**
	 * List of all INVITE requests for which a 2XX final response has been
	 * received. Requests will remain in this map till an ACK is received
	 * List contains AseSipServletRequest objects
	 */
	transient private List m_invite2xxRequests = new ArrayList();

	/**
	 * List of all INVITE requests for which a non 2XX final response
	 * has been received. Requests will remain in this list till an ACK
	 * is received
	 * List contains AseSipServletRequest objects
	 */
	transient private List m_inviteNon2xxRequests = new ArrayList();

	/**
	 * List of all requests for which a CANCEL is received
	 * List contains AseSipServletRequest objects
	 */
	transient private List m_cancelledRequests = new ArrayList();

	/**
	 * Id string for logging purposes
	 */
	transient private String logId;
	transient private String m_logId;

	/**
	 * Call Id string for logging purposes
	 */
	transient private String logCallId;

	/**
	 * static counter t0 generate SIP session ids
	 * ID will be of the form AseSipSession_COUNTER
	 */
	private static AtomicInteger idCounter = new AtomicInteger(1); 
	private static Object idSyncObj = new Object();

	private static int allocateId() {
		return idCounter.incrementAndGet();
	}

	/**
	 * Whether there is an invitation in this dialog
	 */
	transient private boolean m_hasInvitation = false;

	/**
	 * Flag which indicates if the remoteTarget has been computed
	 */
	transient private boolean validRemoteTarget = false;

	/**
	 * RSEQ header value to be used during the lifetime of this dialog
	 */
	transient private long localRSeq = 0;

	/**
	 * The number of pending requests in this session
	 * This includes INVITE requests for which a final response has been sent
	 * but no ACK is received
	 */
	transient private int m_prCount = 0;

	/**
	 * Flag which indicates if this session needs to be replicated
	 * Session needs to be replicated if dialog state has changed
	 */
	transient private boolean m_isReplicationRequired = false;

	/**
	 * Reference to the AseSipInvitationHandler
	 */
	private AseSipInvitationHandler m_invitationHandler = new AseSipInvitationHandler();

	/**
	 * Reference to the AseSipSubscriptionHandler
	 */
	private AseSipSubscriptionHandler m_subscriptionHandler = new AseSipSubscriptionHandler();

	/**
	 * Reference to the AseSip100RelHandler
	 */
	private AseSip100RelHandler m_100RelHandler = new AseSip100RelHandler();

	/**
	 * Reference to the replication handler
	 */
	private AseSipReplicationHandler m_replicationHandler = new AseSipReplicationHandler(this);

	/**
	 * Reference to AseEngine
	 */
	transient private AseEngine m_engine = null;

	/**
	 * Reference to the session state
	 */
	private AseSipSessionStateImpl m_sessionState = null;

	private Serializable routerStateInfo = null;
	private SipApplicationRoutingRegion routingRegion = null;
	private URI subscriber_URI = null;

	/**
	 * Role of this session object. Can be one of ROLE_UNDEFINED,
	 * ROLE_UAS, ROLE_UAC or ROLE_PROXY
	 */
	private int role = ROLE_UNDEFINED;

	/**
	 * Role at construction time.
	 */
	public static final int ROLE_UNDEFINED = 0;

	/**
	 * Servlet is a UAS
	 */
	public static final int ROLE_UAS = 1;

	/**
	 * Servlet is a UAC
	 */
	public static final int ROLE_UAC = 2;

	/**
	 * Servlet is a proxy
	 */
	public static final int ROLE_PROXY = 3;

	/**
	 * Return value NOOP. Caller should not send message to container
	 */
	static final int NOOP = 100;

	/**
	 * Return value CONTINUE. Caller should send message to container
	 */
	static final int CONTINUE = 101;

	/**
	 * Return value CANCEL_REQUEST. Caller should cancel the INVITE request
	 * and send CANCEL message to the container
	 */
	static final int CANCEL_REQUEST = 102;

	/**
	 * Return value ACK_RESPONSE. Caller should ACK the received response
	 * and send the response to the container
	 */
	static final int ACK_RESPONSE = 103;

	/**
	 * Return value OPTIONS_RESPONSE.
	 * SIL should send a response to the OPTIONS request.
	 * SIL will not send the OPTIONS request to the container
	 */
	static final int OPTIONS_RESPONSE = 104;

	/**
	 * This is added for proxy support. It is returned for requests
	 * having Max-Forwards header value as 0 for proxy operation.
	 */
	static final int TOO_MANY_HOPS = 105;
	
	/**
	 * Return value ACK_RESPONSE_ONLY. Caller should only ACK the received response
	 * and does not send the response to the container
	 */
	static final int ACK_RESPONSE_ONLY = 106;

	/**
	 * Constant RSEQ string for comparison
	 */
	private static final DsByteString DS_RSEQ = new DsByteString("RSEQ");

	private static final String SESSION_STATE = "SIP_SESSION_STATE".intern();
	private static final String SUBSCRIPTION_LIST = "SIP_SUBSCRIPTION_LIST".intern();

	private String method = null;
	private int respCode = -1;
	private String direction = null;
	private String source = null;
	private long mesTimeStamp;
	private AseEvictingQueue<AseSipMessageInfo> m_sipSignalingInfoList=new AseEvictingQueue<AseSipMessageInfo>(sipSignalingInfoListSize);
	private int counter = 0;

	//FT Handling strategy Update: Replication will be done for the provisional
	//responses as well, so need to replicate the list for pending messages corresponding
	//to INVITE request. In order to do so need to replicate the b2bhandler thus made it
	//serializable
	
	private transient AseB2bSessionHandler m_b2bHandler;
	
	//FT Handling strategy Update: Need to replicate the rprTransactionHandler on the 
	//standby node in order to support the SAS failover after sending or receiving 
	//provisional responses.
	private Map<Long,AseSip100RelHandler.TransactionRPRHandler> m_rprHandlers = new HashMap<Long,AseSip100RelHandler.TransactionRPRHandler>();
	
	private String linkedSessionId = null;

	private boolean invalidateWhenReady=true;
	private boolean isFirstToInvalidate = true;
	private SipURI m_outboundInterface=null;
	
	private Address m_localParty = null;
	private Address m_remoteParty = null;
	private boolean isOutboundURISet = false;
	
	public void addRPRHandler(Long cseq, AseSip100RelHandler.TransactionRPRHandler transactionRPRHandler ){
		if (this.m_rprHandlers != null)
			this.m_rprHandlers.put(cseq,transactionRPRHandler);
	}
	private void updatePrintInfo(AseSipServletMessage p_mes, long timestamp, String direction) {
		m_logger.debug("updateprintinfo called ");
		this.method = p_mes.getMethod();
		boolean isSDP=(p_mes.getContentLength()>0)?true:false;
		AseSipMessageInfo messageInfo;
		if(p_mes instanceof SipServletResponse) {
			this.respCode = ( (SipServletResponse) p_mes).getStatus();
			messageInfo=new AseSipMessageInfo(respCode, direction, isSDP);
		}else {
			this.respCode = -1;
			messageInfo=new AseSipMessageInfo(this.method, direction, isSDP);
		}
		this.m_sipSignalingInfoList.add(messageInfo);
		this.mesTimeStamp = timestamp;
		this.direction = direction.trim();
		int src = p_mes.getSource();
		this.source = (src == AseSipConstants.SRC_NETWORK) ? "network" : ( (src == AseSipConstants.SRC_SERVLET) ? "servlet" : "ase");	
	}



	public String toString(){
		StringBuffer buffer = new StringBuffer();

		buffer.append("SipSession [ id=");
		buffer.append(this.getId());
		buffer.append(", State =");
		buffer.append(getProtocolSessionState());
		buffer.append(", Session State =");
		buffer.append(this.getSessionState());
		buffer.append(", CallId =");
		buffer.append(this.m_sessionState == null ? "NULL" : this.getCallId());
		buffer.append(", AppSessionId =");
		buffer.append(this.appSession == null ? "NULL" : this.appSession.getAppSessionId());
		buffer.append(", SIPSession created at =");
		buffer.append(this.getCreationTime());
		if(m_sipSignalingInfoList!=null){
			buffer.append(", Signaling Info = [");
			buffer.append(m_sipSignalingInfoList.toString());
			buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		}
		buffer.append(", The latest Message [");
		buffer.append(" Method =  ");
		buffer.append(this.method);
		if(this.respCode != -1) {
			buffer.append(", ResponseCode =  ");
			buffer.append(this.respCode);
		}
		buffer.append(AseStrings.COMMA);
		buffer.append(this.direction);
		buffer.append(", to/from the source =  ");
		buffer.append(this.source);
		buffer.append(", At the time  ");
		buffer.append(this.mesTimeStamp);
		buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		buffer.append(AseStrings.SPACE);
		buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
		return buffer.toString();
	}

	//
	// -- Additions for proxy start
	//

	/**
	 * This is a private class of <code>AseSipSessioni</code>. It extends
	 * <code>java.util.TimerTask</code> to represent a timer scheduled for an
	 * initial server transaction associated with a proxy operation.
	 */

	private class AseProxyServerTransactionTimer
	extends java.util.TimerTask {
		public void run() {
			AseSipServletResponse resp = null;

			AseConnectorSipFactory sipFactory =
				(AseConnectorSipFactory)(m_sipConnector.getFactory());

			// try and acquire a lock
			try {
				acquire();
			} catch (AseLockException e) {
				m_logger.error("AseProxyServerTransactionTimer.run(); Failed to acquire Lock" + logId, e);
			}

			try{
				// create response
				try {
					resp = sipFactory.createResponse(	m_origRequest,
							100,
							null);
				} catch(IllegalArgumentException exp) {
					m_logger.error("AseProxyServerTransactionTimer.run(); Sending 100 on timeout"
							+ logId, exp);
				} catch(IllegalStateException exp) {
					m_logger.error("AseProxyServerTransactionTimer.run(); Sending 100 on timeout"
							+ logId, exp);
				}

				// send response
				try {
					sendResponse(resp);
				} catch(AseSipSessionException exp) {
					//Don't log any error message here
				} catch (IOException ie) {
				}

				// create a new timer
				m_proxyTimer = new AseProxyServerTransactionTimer();
				m_sipConnector.getSipTimer().schedule(m_proxyTimer, m_proxyTimerInterval);

			} finally{
				// try and release the lock
				try {
					release();
				} catch (AseLockException e) {
					m_logger.error("AseProxyServerTransactionTimer.run(); Failed to release Lock."
							+ logId, e);
				}
			}
		}
	}

	//
	// -- Additions for proxy end
	//

	/**
	 * Invoked by the session state object when a dialog is created
	 */
	void dialogCreated(AseSipServletMessage message) {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession dialogCreated" +
					getLogId());

		// Update chaining info
		if(role == ROLE_PROXY) {
			getChainInfo().setChainableBothSides();
			getChainInfo().setChainingReqd(m_proxy.getRecordRoute());
		}

		if(message instanceof AseSipServletResponse) {
			getChainInfo().setChainedDownstream(
					((AseSipServletResponse)message).getPrevSession() != null);
		} else {
			getChainInfo().setChainedDownstream(
					((AseSipServletRequest)message).getPrevSession() != null);
		}

		// Add session to IC and/or dialog manager
		m_sach.addSession(this);

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession dialogCreated" +
					getLogId());
	}

	/**
	 * Invoked by the session state object when a dialog is terminated
	 */
	void dialogTerminated() {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering AseSipSession dialogTerminated" +
					getLogId());

		m_sipConnector.removeSession(this);

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving AseSipSession dialogTerminated" +
					getLogId());
	}

	public String getLogId() {
		return m_logId;
	}

	// Implementation of the AseSipInvitationHandler

	/**
	 * Mark that an INVITE is been sent/received
	 */
	public void setInvitation() {
		m_invitationHandler.setInvitation();
	}

	/**
	 * Unset the INVITE, typically by CANCEL or a 3XX-6XX response
	 */
	public void unsetInvitation() {
		m_invitationHandler.unsetInvitation();
	}

	/**
	 * Reset the invitation. Typically on receiving a BYE
	 */
	public void resetInvitation() {
		m_invitationHandler.resetInvitation();
	}

	public boolean isAckOutstanding() {
		return m_invitationHandler.isAckOutstanding();
	}

	public void addOutstandingRequest(AseSipServletRequest request) {
		m_invitationHandler.addOutstandingRequest(request);
	}

	public AseSipServletRequest removeOutstandingRequest(long cseq) {
		return m_invitationHandler.removeOutstandingRequest(cseq);
	}

	public boolean isRequestOutstanding(long cseq) {
		return m_invitationHandler.isRequestOutstanding(cseq);
	}

	public void addSuccessRequest(AseSipServletRequest request) {
		m_invitationHandler.addSuccessRequest(request);
	}

	public AseSipServletRequest removeSuccessRequest(long cseq) {
		return m_invitationHandler.removeSuccessRequest(cseq);
	}

	public void addFailureRequest(AseSipServletRequest request) {
		m_invitationHandler.addFailureRequest(request);
	}

	public AseSipServletRequest removeFailureRequest(long cseq) {
		return m_invitationHandler.removeFailureRequest(cseq);
	}

	public int getSessionState() {
		if(m_sessionState != null) {
			return m_sessionState.getSessionState();
		} else {
			return AseSipSessionState.STATE_INITIAL;
		}
	}

	public String getCallId() {
		return m_sessionState.getCallId();
	}

	public DsByteString getLocalTag() {
		return m_sessionState.getLocalTag();
	}

	public DsByteString getRemoteTag() {
		return m_sessionState.getRemoteTag();
	}

	public void setLocalTag(DsByteString tag) {
	}

	public void setRemoteTag(DsByteString tag) {
	}

	public DsSipFromHeader getFromHeader() {
		return m_sessionState.getFromHeader();
	}

	public DsSipToHeader getToHeader() {
		return m_sessionState.getToHeader();
	}

	public void setFromHeader(DsSipFromHeader header) {
	}

	public void setToHeader(DsSipToHeader header) {
	}

	public DsSipHeaderInterface getLocalTarget() {
		return m_sessionState.getLocalTarget();
	}

	public DsURI getRemoteTarget() {
		return m_sessionState.getRemoteTarget();
	}

	public long getLocalCSeqNumber() {
		return m_sessionState.getLocalCSeqNumber();
	}
	
	public long getLocalCSeqNumber(long incr) {
		return m_sessionState.getLocalCSeqNumber(incr);
	}

	public long getRemoteCSeqNumber() {
		return m_sessionState.getRemoteCSeqNumber();
	}

	public DsSipHeaderList getRouteSet() {
		return m_sessionState.getRouteSet();
	}

	public boolean isSecure() {
		return m_sessionState.isSecure();
	}

	public AseSipDialogId getUpstreamDialogId() {
		return m_sessionState.getUpstreamDialogId();
	}

	public AseSipDialogId getDownstreamDialogId() {
		return m_sessionState.getDownstreamDialogId();
	}

	public Address getLocalParty() {
		return m_sessionState.getLocalParty();
	}

	public void setLocalParty( Address lParty )
	{
		m_sessionState.setLocalParty(lParty);
	}

	public Address getRemoteParty() {
		return m_sessionState.getRemoteParty();
	}

	public void setRemoteParty( Address rParty )
	{
		m_sessionState.setRemoteParty(rParty);
	}

	public boolean isSupervised() {
		return m_sessionState.isSupervised();
	}

	public boolean isRecordRouted() {
		return m_sessionState.isRecordRouted();
	}

	public SipURI getRecordRouteURI() {
		return m_sessionState.getRecordRouteURI();
	}

	void sendToContainer(AseSipServletResponse response) {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering sendToContainer (response)");

		AseMessage msg = new AseMessage(response);
		AseIc icObject = ((AseApplicationSession)appSession).getIc();
		msg.setWorkQueue(icObject.getWorkQueue());
		m_engine.handleMessage(msg);

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving sendToContainer (response)");
	}

	void sendToContainer(AseEvent event, AseEventListener listener) {
		if (m_logger.isDebugEnabled())
			m_logger.debug("Entering sendToContainer (event)");

		AseMessage msg = new AseMessage(event, listener);
		AseIc icObject = ((AseApplicationSession)appSession).getIc();
		msg.setWorkQueue(icObject.getWorkQueue());
		m_engine.handleMessage(msg);

		if (m_logger.isDebugEnabled())
			m_logger.debug("Leaving sendToContainer (event)");
	}

	public int sendReliableResponse(AseSipServletResponse response)
	throws AseSipMessageHandlerException {
		return m_100RelHandler.sendReliableResponse(response);
	}

	public int recvReliableResponse(AseSipServletResponse response)
	throws AseSipMessageHandlerException {
		return m_100RelHandler.recvReliableResponse(response);
	}

	public int sendPrack(AseSipServletRequest request)
	throws AseSipMessageHandlerException {
		return m_100RelHandler.sendPrack(request);
	}

	public int recvPrack(AseSipServletRequest request)
	throws AseSipMessageHandlerException {
		return m_100RelHandler.recvPrack(request);
	}

	public void sendFinalResponse(AseSipServletResponse response)
	throws AseSipMessageHandlerException {
		m_100RelHandler.sendFinalResponse(response);
	}

	public void recvFinalResponse(AseSipServletResponse response)
	throws AseSipMessageHandlerException {
		m_100RelHandler.recvFinalResponse(response);
	}

	public void addSubscription(AseSipSubscription subscription) {
		m_subscriptionHandler.addSubscription(subscription);
	}

	public AseSipSubscription
	removeSubscription(AseSipSubscription subscription) {
		return m_subscriptionHandler.removeSubscription(subscription);
	}

	public boolean doesSubscriptionExist(AseSipSubscription subscription) {
		return m_subscriptionHandler.doesSubscriptionExist(subscription);
	}

	public AseSipSubscription
	getMatchingSubscription(AseSipSubscription subscription) {
		return m_subscriptionHandler.getMatchingSubscription(subscription);
	}

	public boolean isFirstRefer() {
		return m_subscriptionHandler.isFirstRefer();
	}

	public void firstReferSent() 
	{
		m_subscriptionHandler.firstReferSent();
	}

	AseSipConnector getConnector() {
		return m_sipConnector;
	}

	public void setRouterStateInfo(Serializable stateInfo) {
		routerStateInfo = stateInfo;
	}

	public Serializable getRouterStateInfo() {
		return routerStateInfo;
	}

	public void setRegion(SipApplicationRoutingRegion region) {
		routingRegion = region;
	}

	public SipApplicationRoutingRegion getRegion() {
		if(!isValid())
			throw new IllegalStateException("Session Already Invalidated");

		return routingRegion;
	}

	public void setSubscriberURI(String uri) {
		if(uri == null || uri.equals("") || uri.equals("null")){
			subscriber_URI = null ;
			return;
		}

		AseConnectorSipFactory sipFactory =
			(AseConnectorSipFactory)(m_sipConnector.getFactory());

		try {
			subscriber_URI =  sipFactory.createURI(uri);
		} catch (ServletParseException e) {

		}

	}

	public URI getSubscriberURI() {
		if(!isValid())
			throw new IllegalStateException("Session Already Invalidated");

		return subscriber_URI;
	}

	public boolean getInvalidateWhenReady() {
		if(!isValid())
			throw new IllegalStateException("Session Already Invalidated");
		return invalidateWhenReady;
	}

	public ServletContext getServletContext() {
		AseApplicationSession appSession = (AseApplicationSession)this.getApplicationSession();
		if(appSession == null){
			m_logger.error("Application session is null for this Session, so cannot obtain ServletContext");
			return null;
		}
		return appSession.getContext();
	}

	public boolean isReadyToInvalidate() {
		if(!isValid())
			throw new IllegalStateException("Session Already Invalidated");
		int sessionState = this.getSessionState();
		boolean returnFlag = false;
		if(sessionState == AseSipSessionState.STATE_TERMINATED) {
			returnFlag = true;
		}else if(sessionState == AseSipSessionState.STATE_CONFIRMED && this.getProxy()!= null && !this.getProxy().getRecordRoute()) {
			returnFlag = true;
		} 
		else if(m_sessionState.m_invalidateWhenReady == true && counter == 0){
			returnFlag = true;
		}
		return returnFlag;
	}

	public boolean isValid() {
		return AseProtocolSession.VALID == state;
	}

	public void setInvalidateWhenReady(boolean invalidateWhenReady) {
		if(!isValid())
			throw new IllegalStateException("Session Already Invalidated");
		this.invalidateWhenReady = invalidateWhenReady;
	}

	public void objectReadyToInvalidate(){

		if(m_logger.isEnabledFor(Level.INFO)){
			m_logger.info("objectReadyToInvalidate called on Sip Session :"+ getId());
		}
		if(!isValid()){
			m_logger.info("object already invalidated");
			return;
		}

		Iterator listeners = ((AseApplicationSession)appSession).getContext().getListeners(SipSessionListener.class).iterator();
		SipSessionListener listener =null;
		for(;listeners != null && listeners.hasNext();){
			listener = (SipSessionListener) listeners.next(); 
			try {
				listener.sessionReadyToInvalidate(new SipSessionEvent(this)); 
			} catch (Throwable th) {
				m_logger.error(th.getMessage(), th);
			}
		}
		if(invalidateWhenReady){
			this.invalidate();
		}
	}

	/* This method sets the outbound interface(InetSocketAddress addr) 
	 * specified by the application 
	 * JSR 289.34
	 */
	public void setOutboundInterface(InetSocketAddress addr) {

		if(addr == null)
			throw new NullPointerException("Socket Address is NULL.");

		// If state is not VALID throw an exception
		if (AseProtocolSession.VALID != state) {
			m_logger.error("Throwing Exception. Session " +
					"state = " +state);
			throw new IllegalStateException("Session state not VALID");
		}


		AseConnectorSipFactory sipFactory =
			(AseConnectorSipFactory)(m_sipConnector.getFactory());


		SipURI uri = sipFactory.createSipURI(AseStrings.SAS, addr.getAddress().getHostAddress());
		uri.setPort(addr.getPort());

		List<String> ipAddrNew = new ArrayList<String>();
		ipAddrNew = m_sipConnector.getChangedIPAddressList();
		int index=ipAddrNew.indexOf(addr.getAddress().getHostAddress());
		if(index < 0){		
			throw new IllegalArgumentException("Invalid Interface chosen");
		}else{
			if(m_sipConnector.getPortList().get(index) == addr.getPort()) {			
				m_outboundInterface = uri;

			}else{
				throw new IllegalArgumentException("Invalid Interface chosen.");
			}
		}
	}

	/* This method sets the outbound interface(InetAddress addr) 
	 * specified by the application 
	 * JSR 289.34
	 */
	public void setOutboundInterface(InetAddress addr) {
		if(addr == null)
			throw new NullPointerException("Inet Address is NULL.");

		// If state is not VALID throw an exception
		if (AseProtocolSession.VALID != state) {
			m_logger.error("Throwing Exception. Session " +
					"state = " +state);
			throw new IllegalStateException("Session state not VALID");
		}

		AseConnectorSipFactory sipFactory =
			(AseConnectorSipFactory)(m_sipConnector.getFactory());

		List<String> ipAddrNew = new ArrayList<String>();
		ipAddrNew = m_sipConnector.getChangedIPAddressList();
		int index=ipAddrNew.indexOf(addr.getHostAddress());
		if(index < 0)			
			throw new IllegalArgumentException("Invalid Interface chosen");
		else {
			SipURI uri = sipFactory.createSipURI("sas", addr.getHostAddress());
			uri.setPort(m_sipConnector.getPortList().get(index));
			m_outboundInterface = uri;

		}
	}


	public SipURI getOutboundInterface() {
		return m_outboundInterface;
	}
	
	public B2buaHelper getB2buaHelper() throws IllegalStateException{
		return this.m_sipConnector.getB2bHelper();
	}

	public AseB2bSessionHandler getB2bSessionHandler() {
		return this.m_b2bHandler;
	}

	public String getLinkedSessionId() {
		return linkedSessionId;
	}

	public void setLinkedSessionId(String linkedSessionId) {
		this.linkedSessionId = linkedSessionId;
	}

	public State getState() {

		if(!isValid()){
			throw new IllegalStateException("Session Already Invalidated");
		}
		int i = getSessionState();
		if(i==AseSipSessionState.STATE_INITIAL){
			return State.INITIAL;
		}else if(i==AseSipSessionState.STATE_EARLY){
			return State.EARLY;
		}else if(i==AseSipSessionState.STATE_CONFIRMED){
			return State.CONFIRMED;
		}else if(i==AseSipSessionState.STATE_TERMINATED){
			return State.TERMINATED;
		}
		return  null;	

	}

	public AseSipInvitationHandler getInvitationHandler(){
		return m_invitationHandler;
	}
	public int getSessState() {
		return state;
	}
	
	public void setTransResponse(AseSipServletResponse transResp){
		m_transResponse = transResp;
	}
	public AseSipServletResponse getTransResponse(){
		return m_transResponse;
	}
	
	public boolean isOutboundURISet(){
		return isOutboundURISet  ;
	}
			
	public void setOutboundURIFlag(boolean setOutboundURIFlag){
		if(m_logger.isDebugEnabled()){
			m_logger.debug("Set setOutboundURIFlag as : " + setOutboundURIFlag);
		}
		isOutboundURISet  = setOutboundURIFlag;
	}
}
