/*
 * AseStackInterfaceLayer.java
 *
 * Created on Aug 17, 2004
 */
package com.baypackets.ase.sipconnector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.sip.Rel100Exception;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipErrorEvent;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;

import org.apache.log4j.Logger;

import com.baypackets.ase.channel.AseSubsystem;
import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.AseEvent;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.AseLockException;
import com.baypackets.ase.common.logging.LoggingCriteria;
import com.baypackets.ase.common.logging.debug.SelectiveMessageLogger;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseEngine;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.control.AseModes;
import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.control.ControlManager;
import com.baypackets.ase.latency.AseLatencyData;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.monitor.AseComponentMonitorManager;
import com.baypackets.ase.monitor.CallStatsProcessor;
import com.baypackets.ase.sipconnector.headers.AseSipDefaultHeader;
import com.baypackets.ase.sipconnector.headers.AseSipDiversionHeader;
import com.baypackets.ase.sipconnector.headers.AseSipHeaderFactory;
import com.baypackets.ase.sipconnector.headers.AseSipHistoryInfoHeader;
import com.baypackets.ase.sipconnector.headers.AseSipPAssociatedURIHeader;
import com.baypackets.ase.sipconnector.headers.AseSipServiceRouteHeader;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.CallTraceService;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipClientTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipServerTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipStateMachineDefinitions;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransaction;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransactionManager;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransactionManager.DsAlreadyShuttingDownException;
import com.dynamicsoft.DsLibs.DsSipLlApi.DsSipTransportLayer;
import com.dynamicsoft.DsLibs.DsSipObject.DsByteString;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipAckMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipCancelMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipConstants;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipMessage;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRequest;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipResponse;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipRetryAfterHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTimestampHeader;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipTransportType;
import com.dynamicsoft.DsLibs.DsSipObject.DsSipURL;
import com.dynamicsoft.DsLibs.DsSipObject.DsTelURL;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipParserListenerException;
import com.dynamicsoft.DsLibs.DsSipParser.DsSipUrlHeaderParser;
import com.dynamicsoft.DsLibs.DsUtil.DsBindingInfo;
import com.dynamicsoft.DsLibs.DsUtil.DsConfigManager;
import com.dynamicsoft.DsLibs.DsUtil.DsException;
import com.dynamicsoft.DsLibs.DsUtil.DsMessageLoggingInterface;
import com.dynamicsoft.DsLibs.DsUtil.DsSSLContext;
import com.dynamicsoft.DsLibs.DsUtil.DsSSLException;


/**
 * The <code>AseStackInterfaceLayer</code> class architecturally resides
 * between the connector and SIP stack. It encapsulates stack specific
 * initialization, configuration, runtime object creation, send message and
 * callback methods. It also handles various exceptions thrown by stack.
 *
 * Main methods provided by this class handle SIP requests and responses
 * coming from container or stack (network) side. After preliminary checks, it
 * determines where (which object) and how (which method) to pass the SIP
 * messages on to other side.
 *
 * @author Neeraj Jain
 */

public class AseStackInterfaceLayer
implements AseStackInterface {
	
	
	 private static final String CORRELATION_ID_ATTRIBUTE="P-Correlation-ID";
	 private String DIALOGUE_ID = Constants.DIALOGUE_ID ;
	 //FT Handling Update: Need the SIL object at App Session
	 // for replication of INVITE Client Transaction
	 private static AseStackInterfaceLayer _instance;
	 
	 private String ingwMessageQueue = null;
	 
	 private String nsepIngwPriority = null;
	 
	 public static Set<String> prepaidPatterns = new HashSet<String>();
	 
	 private static String prepaidCallPatternStr = null;
	 
	 private static String prepaidTrafficDist = null;
	 
	 private static Pattern prepaidPattern = null;
	 
	 Multipart multiPartContent = new MimeMultipart();
	 
	 private int releaseCause = 42;
	 
	 byte[] rel_isup = {(byte)0x0c, (byte)0x02, (byte)0x00, (byte)0x02, (byte)0x83, (byte)0xbf};
	 
	 private static volatile boolean isSoftShutdownEnabled = false;
	 
	 private static boolean isMultihomingEnabled = false;
	 
	 CallStatsProcessor callStatsProcessorObj = CallStatsProcessor.getInstance();
	 
	// AseComponentMonitorManager aseCompMonManager=AseComponentMonitorManager.getInstance();
	

	/**
	 * Constructor. Creates basic stack objects and sets private attributes.
	 *
	 * @param connector SIP connector reference
	 *
	 * @param dialogMgr dialog manager
	 *
	 * @param subManager subscription manager
	 * @param factory SIP connector factory
	 *
	 * @param defaultHandler SIP message default handler
	 */
	AseStackInterfaceLayer(AseSipConnector	connector,
			AseDialogManager dialogMgr,
			AseSipSubscriptionManager subManager,
			AseConnectorSipFactory factory) {

		if (m_l.isDebugEnabled()) {
			m_l.debug("AseStackInterfaceLayer(AseSipConnector, AseDialogManager, AseSipSubscriptionManager, AseConnectorSipFactory):enter");
		}

		m_connector			= connector;
		m_dialogMgr			= dialogMgr;
		m_subscriptionManager = subManager;
		m_factory			= factory;
		m_configRepository	= (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		m_engine			= (AseEngine)Registry.lookup(Constants.NAME_ENGINE);

		if (m_l.isDebugEnabled()) {
			m_l.debug("AseStackInterfaceLayer(AseDialogManager, AseConnectorSipFactory):exit");
		}
	}
	
	//FT Handling Update: Need the SIL object at App Session
	// for replication of INVITE Client Transaction
	
	public static AseStackInterfaceLayer getInstance(){
		return _instance;
	}
	
	public AseConnectorSipFactory getM_factory() {
		return m_factory;
	}

	
	public void setM_factory(AseConnectorSipFactory mFactory) {
		m_factory = mFactory;
	}

	private void constructStack() {
		//Start : Configuration Base Random Port Generation for OutBound Messages 
		String randomPortFlagValStr = (String)m_configRepository.getValue(Constants.GENERATE_RANDOM_PORT_FOR_MESSAGES_OUT);
		if(randomPortFlagValStr != null && !randomPortFlagValStr.equals(AseStrings.BLANK_STRING)){
			if(randomPortFlagValStr.trim().equalsIgnoreCase(AseStrings.TRUE_SMALL) || randomPortFlagValStr.trim().equalsIgnoreCase(AseStrings.FALSE_SMALL)){
				boolean randomPortFlagVal = Boolean.parseBoolean(randomPortFlagValStr);
				DsConfigManager.randomPortFlagVal = randomPortFlagVal ;
			}
		}
		//End : Configuration Base Random Port Generation for OutBound Messages 
		
		String dataiQueueLoggingStr = (String)m_configRepository.getValue(Constants.DATAI_QUEUE_LOGGING);
		String dataiQueueLoggingPeriod = (String)m_configRepository.getValue(Constants.DATAI_QUEUE_LOGGING_PERIOD);
		if(dataiQueueLoggingStr != null && !dataiQueueLoggingStr.equals(AseStrings.BLANK_STRING)){
			if(dataiQueueLoggingStr.trim().equalsIgnoreCase(AseStrings.TRUE_SMALL) || dataiQueueLoggingStr.trim().equalsIgnoreCase(AseStrings.FALSE_SMALL)){
				boolean dataiQueueLogging = Boolean.parseBoolean(dataiQueueLoggingStr);
				DsConfigManager.printDataIQueue = dataiQueueLogging ;
				if(!dataiQueueLoggingPeriod.equals(AseStrings.ZERO)){
					DsConfigManager.printDataIQueueDuration = Integer.parseInt(dataiQueueLoggingPeriod) ;
				}
			}
		}
		// start : setting stack variable for callback queues
		String ctCallbackQueueLoggingStr = (String)m_configRepository.getValue(Constants.CT_CALLBACK_QUEUE_LOGGING);
		String ctCallbackQueueLoggingPeriod = (String)m_configRepository.getValue(Constants.CT_CALLBACK_QUEUE_LOGGING_PERIOD);
		if(ctCallbackQueueLoggingStr != null && !ctCallbackQueueLoggingStr.equals(AseStrings.BLANK_STRING)){
			if(ctCallbackQueueLoggingStr.trim().equalsIgnoreCase(AseStrings.TRUE_SMALL) || ctCallbackQueueLoggingStr.trim().equalsIgnoreCase(AseStrings.FALSE_SMALL)){
				boolean ctCallbackQueueLogging = Boolean.parseBoolean(ctCallbackQueueLoggingStr);
				DsConfigManager.printCtCallbackQueue = ctCallbackQueueLogging ;
				if(!ctCallbackQueueLoggingPeriod.equals(AseStrings.ZERO)){
					DsConfigManager.printCtCallbackQueueDuration = Integer.parseInt(ctCallbackQueueLoggingPeriod) ;
				}
			}
		}
		// end : setting stack variable for callback queues
		
		// Check if TLS is enabled
		String DATA_IN_QNAME = "DATAI";
		int normalQSize = 2000;
		int priorityQSize = 4000;
		String tlsStr = (String)m_configRepository.getValue(Constants.PROP_SIP_TLS_ENABLED);
		if(tlsStr != null) {
			m_enableTLS = new Boolean(tlsStr).booleanValue();
		}
		DsConfigManager.setPrioritySupport(m_engine.isCallPriorityEnabled());
		String dscpStr = (String)m_configRepository.getValue(Constants.NSEP_DSCP);
		int dscp = 0;
		try {
			dscp = new Integer(dscpStr).intValue();
		} catch(Exception ex) { 
		}
		DsConfigManager.setDSCP(dscp*4);
		String normalSize = (String)m_configRepository.getValue(Constants.NORMAL_Q_SIZE);
		String prioritySize = (String)m_configRepository.getValue(Constants.PRIORITY_Q_SIZE);
		try	{
			normalQSize = new Integer(normalSize).intValue();
			priorityQSize = new Integer(prioritySize).intValue();
		}catch(Exception e)	{
			//Use default values if numberformat exception
		}

		// Stack objects creation
		if (m_l.isInfoEnabled()) {
			m_l.info("Going to create transport layer and transport manager");
		}
		if(m_enableTLS) {
			if (m_l.isInfoEnabled()) {
				m_l.info("TLS is enabled");
			}
			// Get Keystore, Keystore password  truststore from config
			String keyStore = (String)m_configRepository.getValue(Constants.OID_SIP_KEYSTORE_PATH);
			String trustStore = (String)m_configRepository.getValue(Constants.OID_SIP_TRUSTSTORE_PATH);
			m_keyStorePassword = (String)m_configRepository.getValue(Constants.OID_SIP_KEYSTORE_PASSWORD);

			try {
				//BpInd17903
				System.setProperty("com.dynamicsoft.DsLibs.DsUtil.trustStore",m_keyStorePassword);
				m_sslContext = new DsSSLContext(keyStore, m_keyStorePassword, trustStore);

				m_transportLayer = new DsSipTransportLayer(	DsBindingInfo.LOCAL_PORT_UNSPECIFIED,
						DsBindingInfo.BINDING_TRANSPORT_UNSPECIFIED,
						null,
						m_sslContext);

			} catch(DsSSLException exp) {
				m_l.error("SIL constructor: ", exp);
			} catch(DsException exp) {
				m_l.error("SIL constructor: ", exp);
			} catch(Exception exp) { // IOException
				m_l.error("SIL constructor: ", exp);
			}
		} else {
			if (m_l.isInfoEnabled()) {
				m_l.info("TLS is disabled");
			}
			m_transportLayer = new DsSipTransportLayer();
		}

		try {
			// Instantiate transaction manager without any request listener
			m_transactionMgr	= new DsSipTransactionManager(m_transportLayer, null);
		} catch (DsException ex) {
			// Transaction manager already exists
			m_l.error("Stack transport manager constructor", ex);
		}
		if (m_l.isDebugEnabled()) {
			m_l.debug("Setting DATA_IN_Q Normal Queue threshold size: "+normalQSize);
		}
		DsConfigManager.setMaxSize(DATA_IN_QNAME,normalQSize);
		if(m_engine.isCallPriorityEnabled())	{
			if (m_l.isDebugEnabled()) {
				m_l.debug("Setting DATA_IN_Q Priority Queue threshold size: "+priorityQSize);
			}
			DsConfigManager.setPriorityMaxSize(DATA_IN_QNAME,priorityQSize);
		}
	}

	/**
	 * Initializes underlying SIP stack to bring it into ready state for
	 * listening to messages from network.
	 *
	 * @param container associated container object reference
	 */
	public void initialize(AseContainer container) {
		if (m_l.isDebugEnabled()) {
			m_l.debug("initialize(AseContainer):enter");
		}

		constructStack();

		m_container = container;

		// --
		// Stack objects creation and initialization
		// --

		// Register SIP stack listeners for the methods supported
		// not to be registered for ACK and CANCEL methods
		try {
			String methods[] = AseSipConstants.getSupportedMethods();
			for (int i = 0; i < methods.length; i++) {
				if( (!methods[i].equals(AseStrings.CANCEL)) &&
						(!methods[i].equals(AseStrings.ACK)) ) {
					if(m_l.isInfoEnabled())
						m_l.info("Registering request handler for " + methods[i]);
					m_transactionMgr.setRequestInterface(
							new AseSipRequestListener(this, m_factory),
							methods[i]);
				}
			}
		} catch (DsException ex) {
			m_l.error("SIL initialization: Registering request interface", ex);
		}

		if (m_l.isInfoEnabled()) {
			m_l.info("Registering stray message interface");
		}
		m_transactionMgr.setStrayMessageInterface(
				new AseSipStrayMessageListener(this, m_factory));

		// Register server transaction listener object factory
		if (m_l.isInfoEnabled()) {
			m_l.info("Registering transaction interface factory");
		}
		AseSipTransactionInterfaceFactory tiFactory =
			new AseSipTransactionInterfaceFactory(this, m_factory);
		m_transactionMgr.setTransactionInterfaceFactory(tiFactory);

		// Register transaction object factory
		if (m_l.isInfoEnabled()) {
			m_l.info("Registering transaction factory");
		}
		m_transactionMgr.setTransactionFactory(new AseSipTransactionFactory(tiFactory));

		//Registering the headers and setting the header factory;
		AseSipHistoryInfoHeader.m_headerID = AseSipHeaderFactory.registerHeader(AseSipHistoryInfoHeader.m_token, null, false, DsSipUrlHeaderParser.getInstance());
		AseSipServiceRouteHeader.m_headerID = AseSipHeaderFactory.registerHeader(AseSipServiceRouteHeader.m_token, null, false, DsSipUrlHeaderParser.getInstance());
		AseSipPAssociatedURIHeader.m_headerID = AseSipHeaderFactory.registerHeader(AseSipPAssociatedURIHeader.m_token, null, false, DsSipUrlHeaderParser.getInstance());
		AseSipDiversionHeader.m_headerID = AseSipHeaderFactory.registerHeader(AseSipDiversionHeader.m_token, null, false, DsSipUrlHeaderParser.getInstance());
		AseSipDefaultHeader.m_headerID = AseSipHeaderFactory.registerHeader(AseSipDefaultHeader.m_token, null, false, DsSipUrlHeaderParser.getInstance());
		DsSipHeader.setHeaderFactory(new AseSipHeaderFactory());

		// Enable MFR handling
		DsSipMessage.setNewKeyPolicy(false);
		DsConfigManager.handleMultipleFinalResponses(true);
		// Configure stack in compatibility mode - doing it in 'ase_no_ems' now
		//System.setProperty("com.dynamicsoft.DsLibs.DsSipLlApi.x200Terminated", AseStrings.TRUE_SMALL);

		// Comfigure timer TU1 for the same
		DsConfigManager.setTimerValue(DsSipConstants.TU1, 32000); // 32 seconds
		DsConfigManager.setTimerValue(DsSipConstants.TU2, 32000);

		// ATT bug fix - BPInd12945: starts
		int clientTnTimer = 300; // 5 mins
		String value = (String)m_configRepository.getValue(
				Constants.PROP_SIP_CLIENT_TXN_TIMEOUT);
		if(null != value && !value.isEmpty()) {
			try {
				clientTnTimer = Integer.parseInt(value);
			} catch(java.lang.NumberFormatException exp) {
				m_l.error("Taking value of clientTnTimer = " + clientTnTimer, exp);
			}
		}

		if(m_l.isDebugEnabled()) {
			m_l.debug("Setting client transaction timeout to (secs): " + clientTnTimer);
		}
		
		boolean removeTransport=false;
		 value = (String)m_configRepository.getValue(
				Constants.PROP_REMOVE_TRANSPORT_PARAM);
		if(null != value && !value.isEmpty()) {
			try {
				removeTransport = Boolean.parseBoolean(value);
			} catch(java.lang.Exception exp) {
				m_l.error("Taking value of PROP_REMOVE_TRANSPORT_PARAM = " + removeTransport, exp);
			}
		}
		
		if(m_l.isDebugEnabled()) {
			m_l.debug("Setting removeTransport to  " + removeTransport);
		}
		DsConfigManager.setRemoveTransport(removeTransport);
		
		DsConfigManager.setTimerValue(DsSipConstants.clientTn, clientTnTimer*1000);
		DsConfigManager.setTimerValue(DsSipConstants.serverTn, clientTnTimer*1000); // BPInd12953
		// ATT bug fix - BPInd12945: ends

		///////////////timerA//////////////////////
		// client.invite.request.retransmit.interval
		int TimerA = -1; // milliseconds
		value = (String)m_configRepository.getValue(
				Constants.TIMER_A);
		if(null != value && !value.isEmpty()) {
			try {
				TimerA = Integer.parseInt(value.trim());
			} catch(java.lang.NumberFormatException exp) {
				m_l.error("Taking value of TimerA = " + TimerA, exp);
			}
		}

		if(TimerA >= 0) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Setting invite request retransmit interval to TimerA (millisecs): " + TimerA);
			}
			DsConfigManager.setTimerValue(DsSipConstants.TimerA, TimerA);
		}

		////////////////TimerE//////////////////////////////
		// non invite request retransmit interval 
		int TimerE = -1; // milliseconds
		value = (String)m_configRepository.getValue(Constants.TIMER_E);
		if(null != value && !value.isEmpty()) {
			try {
				TimerE = Integer.parseInt(value.trim());
			} catch(java.lang.NumberFormatException exp) {
				m_l.error("Taking value of TimerE = " + TimerE, exp);
			}
		}

		if(TimerE >= 0) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Setting non invite request retransmit interval to TimerE (millisecs): " + TimerE);
			}
			DsConfigManager.setTimerValue(DsSipConstants.TimerE, TimerE);
		}

		/////////////////TimerD///////////////////
		// wait timer for response retransmit(time between first 3xx-6xx and removeTransaction) client
		int TimerD = -1; // milliseconds
		value = (String)m_configRepository.getValue(Constants.TIMER_D);
		if(null != value && !value.isEmpty()) {
			try {
				TimerD = Integer.parseInt(value.trim());
			} catch(java.lang.NumberFormatException exp) {
				m_l.error("Taking value of TimerD = " + TimerD, exp);
			}
		}

		if(TimerD >= 0) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Setting wait time for response retransmit(client) to TimerD (millisecs): " + TimerD);
			}
			DsConfigManager.setTimerValue(DsSipConstants.TimerD, TimerD);
		}

		/////////////////TimerB///////////////////////
		//max retry count for invite request(client)
		byte TimerB = -1; 
		value = (String)m_configRepository.getValue(
				Constants.TIMER_B);
		if(null != value && !value.isEmpty()) {
			try {
				TimerB = Byte.valueOf(value.trim());
			} catch(java.lang.NumberFormatException exp) {
				m_l.error("Taking value of TimerB = " + TimerB, exp);
			}
		}

		if(TimerB >= 0) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Setting INVITE_CLIENT_TRANS_RETRY to TimerB (byte): " + TimerB);
			}
			DsConfigManager.setRetryCount(DsSipConstants.INVITE_CLIENT_TRANS, TimerB);
		}

		//////////////////TimerF//////////////////////////
		//max retry count for non invite request(client)
		byte TimerF = -1; 
		value = (String)m_configRepository.getValue(Constants.TIMER_F);
		if(null != value && !value.isEmpty()) {
			try {
				TimerF = Byte.valueOf(value.trim());
			} catch(java.lang.NumberFormatException exp) {
				m_l.error("Taking value of TimerF = " + TimerF, exp);
			}
		}

		if(TimerF >= 0) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Setting_CLIENT_TRANS_RETRY to TimerF (byte): " + TimerF);
			}
			DsConfigManager.setRetryCount(DsSipConstants.CLIENT_TRANS, TimerF);
		}

		///////////////timerG//////////////////////
		// invite response retransmit interval(server) 
		int TimerG = -1; // milliseconds
		value = (String)m_configRepository.getValue(
				Constants.TIMER_G);
		if(null != value && !value.isEmpty()) {
			try {
				TimerG = Integer.parseInt(value.trim());
			} catch(java.lang.NumberFormatException exp) {
				m_l.error("Taking value of TimerG = " + TimerG, exp);
			}
		}

		if(TimerG >= 0) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Setting invite response retransmit interval(server) to TimerG (millisecs): " + TimerG);
			}
			DsConfigManager.setTimerValue(DsSipConstants.TimerG, TimerG);
		}


		/////////////////TimerH///////////////////////
		//server.ack.retransmit.wait
		byte TimerH = -1; 
		value = (String)m_configRepository.getValue(
				Constants.TIMER_H);
		if(null != value && !value.isEmpty()) {
			try {
				TimerH = Byte.valueOf(value.trim());
			} catch(java.lang.NumberFormatException exp) {
				m_l.error("Taking value of TimerH = " + TimerH, exp);
			}
		}

		if(TimerH >= 0) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Setting INVITE_CLIENT_TRANS_RETRY to TimerH (byte): " + TimerH);
			}
			DsConfigManager.setRetryCount(DsSipConstants.INVITE_SERVER_TRANS, TimerH);
		}


		/////////////////TimerI///////////////////
		// wait time for ACK retransmit in case of invite server transaction
		int TimerI = -1; // milliseconds
		value = (String)m_configRepository.getValue(Constants.TIMER_I);
		if(null != value && !value.isEmpty()) {
			try {
				TimerI = Integer.parseInt(value.trim());
			} catch(java.lang.NumberFormatException exp) {
				m_l.error("Taking value of TimerI = " + TimerI, exp);
			}
		}

		if(TimerI >= 0) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Setting wait time for ACK retransmit(invite server) to TimerI (millisecs): " + TimerI);
			}
			DsConfigManager.setTimerValue(DsSipConstants.TimerI, TimerI);
		}


		///////////////timerJ//////////////////////
		// wait time for non invite request retransmit(server trans)
		int TimerJ = -1; // milliseconds
		value = (String)m_configRepository.getValue(
				Constants.TIMER_J);
		if(null != value && !value.isEmpty()) {
			try {
				TimerJ = Integer.parseInt(value.trim());
			} catch(java.lang.NumberFormatException exp) {
				m_l.error("Taking value of TimerJ = " + TimerJ, exp);
			}
		}

		if(TimerJ >= 0) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Setting wait time for non invite request retransmit(server trans) to TimerJ (millisecs): " + TimerJ);
			}
			DsConfigManager.setTimerValue(DsSipConstants.TimerJ, TimerJ);
		}

		/////////////////TimerK///////////////////
		// wait time for response retransmit in non invite client transaction
		int TimerK = -1; // milliseconds
		value = (String)m_configRepository.getValue(Constants.TIMER_K);
		if(null != value&&!value.isEmpty()) {
			try {
				TimerK = Integer.parseInt(value.trim());
			} catch(java.lang.NumberFormatException exp) {
				m_l.error("Taking value of TimerK = " + TimerK, exp);
			}
		}

		if(TimerK >= 0) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Setting wait time for response retransmit(non invite client) to TimerK (millisecs): " + TimerK);
			}
			DsConfigManager.setTimerValue(DsSipConstants.TimerK, TimerK);
		}


		/////////////////////////////////////////////////////////////////	
		// BPUsa07933 changes begin
		value = (String)m_configRepository.getValue(Constants.OID_SIP_RETRY_AFTER_DELAY);
		if(null != value && !value.isEmpty()) {
			try {
				m_retryAfterDelay = Integer.parseInt(value.trim());
			} catch(java.lang.NumberFormatException ex) {
				m_l.error("Taking value of SIP RETRY AFTER DELAY: ", ex);
			}
		}
		if(m_l.isInfoEnabled()) {
			m_l.info("Use [" + m_retryAfterDelay + "] as Retry-After delay");
		}
		// BPUsa07933 changes end

		// ATT bug fix - BPInd12953: starts
		int serverTnTimer = 300; // 5 mins
		value = (String)m_configRepository.getValue(
				Constants.PROP_SIP_SERVER_TXN_TIMEOUT);
		if(null != value &&!value.isEmpty()) {
			try {
				serverTnTimer = Integer.parseInt(value.trim());
			} catch(java.lang.NumberFormatException exp) {
				m_l.error("Taking value of serverTnTimer = " + serverTnTimer, exp);
			}
		}

		if(m_l.isDebugEnabled()) {
			m_l.debug("Setting server transaction timeout to (secs): " + serverTnTimer);
		}
		DsConfigManager.setTimerValue(DsSipConstants.serverTn, serverTnTimer*1000);
		// ATT bug fix - BPInd12953: ends

		String propVal;

		// Configure max simultaneous TCP connections allowed
		propVal = (String)m_configRepository.getValue(
				Constants.PROP_SIP_TCP_MAX_CONNECTIONS);
		if(propVal != null&& !propVal.isEmpty()) {
			int maxConn = Integer.parseInt(propVal);
			if(m_l.isDebugEnabled()) {
				m_l.debug("Max simultaneous TCP connections allowed are : " + maxConn);
			}
			m_transportLayer.setMaxConnections(maxConn);
		}

		// Configure incoming TCP connection timeout
		propVal = (String)m_configRepository.getValue(
				Constants.PROP_SIP_TCP_INCOMING_CONNECTION_TIMEOUT);
		if(propVal != null&& !propVal.isEmpty()) {
			int icConnTimeout = Integer.parseInt(propVal);
			if(m_l.isDebugEnabled()) {
				m_l.debug("Incoming TCP connection timeout (in secs) : " + icConnTimeout);
			}
			m_transportLayer.setIncomingConnectionTimeout(icConnTimeout);
		}

		// Configure outgoing TCP connection timeout
		propVal = (String)m_configRepository.getValue(
				Constants.PROP_SIP_TCP_OUTGOING_CONNECTION_TIMEOUT);
		if(propVal != null&& !propVal.isEmpty()) {
			int ogConnTimeout = Integer.parseInt(propVal);
			if(m_l.isDebugEnabled()) {
				m_l.debug("Outgoing TCP connection timeout (in secs) : " + ogConnTimeout);
			}
			m_transportLayer.setOutgoingConnectionTimeout(ogConnTimeout);
		}

		//BpInd 17838 Configure initial notify flag
		String notifyFlag = (String) m_configRepository.getValue(Constants.PROP_SIP_INITIAL_NOTIFY_FLAG);

		if(notifyFlag!=null && notifyFlag.equals(AseStrings.TRUE_SMALL))
		{
			if(m_l.isDebugEnabled()) {
				m_l.debug("notifyFLag : " + notifyFlag);
			}
			Constants.NOTIFY_FLAG=true;
		}

		m_ocm = m_connector.getOverloadManager();

		// Initializing OverloadControlManager for prioprity calls
		if(m_engine.isCallPriorityEnabled()) {
			m_nsepOcm = m_connector.getOverloadManager(true);
		}
		if(m_enableTLS) {
			// Cipher suites
			String[] ciphers = m_sslContext.getSupportedCipherSuites();
			m_sslContext.setEnabledCipherSuites(ciphers);
			ciphers = m_sslContext.getEnabledCipherSuites();
			for(int i=0; i < ciphers.length; ++i) {
				if (m_l.isDebugEnabled()) {
					m_l.debug("Enabled cipher-suite: " + ciphers[i]);
				}
			}
		}
		 //FT Handling Update: Need the SIL object at App Session
		 // for replication of INVITE Client Transaction
		this._instance = this;
		
		//INGW Communication queue
		ingwMessageQueue = (String)m_configRepository.getValue(Constants.INGW_MSG_QUEUE);
		nsepIngwPriority = 	(String)m_configRepository.getValue(Constants.NSEP_INGW_PRIORITY);
		
		if (m_l.isDebugEnabled()) {
			m_l.debug("Messages from INGw will come in " + ingwMessageQueue + " queue");
			m_l.debug("All call prioirty support with INGw messages coming in priority queue " + nsepIngwPriority);
		}
		
		prepaidTrafficDist = (String)m_configRepository.getValue(Constants.PREPAID_TRAFFIC_DISTRIBUTION);
		prepaidTrafficDist = prepaidTrafficDist.toLowerCase();
		if (prepaidTrafficDist.equals(AseStrings.TRUE_SMALL)){
			String prepaidCallPattern = (String)m_configRepository.getValue(Constants.PREPAID_CALL_PATTERNS);
			String[] patterns = prepaidCallPattern.split(AseStrings.COMMA);
			if (m_l.isDebugEnabled()) {
				m_l.debug("Prepaid Patterns Configured " + patterns);
			}
			//This is done to avoid duplicates
			for (String pattern: patterns){
				prepaidPatterns.add(pattern);
			}
			//prepaidCallPatternStr = prepaidCallPatternStr + "(\\s*):(\\s*)" + pattern;
			createPattern();
			if (m_l.isDebugEnabled()) {
				m_l.debug("Prepaid Call Pattern Configured " + prepaidCallPatternStr);
			}
		}
		//Register with the TELNET Server for the System information.
		TelnetServer telnetServer = (TelnetServer) Registry.lookup(Constants.NAME_TELNET_SERVER);
		if(telnetServer != null){
			CommandHandler handler = new PrepaidPatternHandler(); 
			telnetServer.registerHandler(Constants.ADD_PREPAID_PATTERN, handler);
			telnetServer.registerHandler(Constants.REMOVE_PREPAID_PATTERN, handler);
			telnetServer.registerHandler(Constants.GET_PREPAID_PATTERN, handler);
		}
		
		if (m_l.isDebugEnabled()) {
			m_l.debug("initialize(AseContainer):exit");
		}
	}

	/**
	 * Starts listening to messages from network. Any messages arriving after
	 * this method call will be processed.
	 */
	public void start() {
		if (m_l.isDebugEnabled()) {
			m_l.debug("start():enter");
		}

		String value = (String)m_configRepository.getValue(
				Constants.OID_SIP_OVERLOAD_RESPONSE_CODE);
		if(null != value) {
			int rejectionCode = Integer.parseInt(value);
			if((rejectionCode > 399) && (rejectionCode < 700)) {
				m_overloadRejectionCode = rejectionCode;
			}
			else {
				if(m_l.isInfoEnabled())
					m_l.info("Invalid [" + rejectionCode + "] as response for overload based rejection. Using the default value");
			}
		}
		if(m_l.isInfoEnabled()) {
			m_l.info("Use [" + m_overloadRejectionCode + "] as response for overload based rejection");
		}
		
		//adding isup body part and setting in mp
		String relCause = m_configRepository.getValue(Constants.ISUP_REL_OVERLOAD);
		
		try{
			if (relCause != null){
				releaseCause = Integer.parseInt(relCause);
			}
		}catch (Exception e) {
			m_l.error("SIL start: Release cause for Overload rejection response not defined properly ", e);
		}
		
		if(m_l.isInfoEnabled()) {
			m_l.info("Use [" + releaseCause + "] as ISUP release cause for overload based rejection");
		}
		byte relCauseByte = (byte) ((1 << 7) | releaseCause);
		rel_isup[((rel_isup.length)-1)]= relCauseByte;
		BodyPart isupBodyPart = new MimeBodyPart();
		javax.mail.util.ByteArrayDataSource dataFile = new javax.mail.util.ByteArrayDataSource(rel_isup, AseStrings.ISUP_CONTENT_TYPE);
		try {
			isupBodyPart.setDataHandler(new DataHandler(dataFile));
			isupBodyPart.setHeader(AseStrings.HDR_CONTENT_TYPE, AseStrings.ISUP_CONTENT_TYPE_VER);
			multiPartContent.addBodyPart(isupBodyPart);
		} catch (MessagingException e) {
			m_l.error("Error creating isupbody in multipart",e);
		}
		
		// Register Route Fix interface
		if (m_l.isInfoEnabled()) {
			m_l.info("Registering Route Fix interface");
		}
		if(m_connector.getRecordRouteURI() != null) {
			String uri = m_connector.getRecordRouteURI().toString();
			try {
				m_transactionMgr.setRouteFixInterface(
						new AseSipRouteFixImpl(new DsSipURL(uri)));
			} catch(DsSipParserException exp) {
				m_l.error("Setting Route Fix interface", exp);
			}
		}

		String fip =  AseUtils.getIPAddressList(m_configRepository.getValue(Constants.OID_SIP_CONNECTOR_IP_ADDRESS), false);
		
		boolean ipV6 = false;
		if(fip == null || fip.contains(AseStrings.COLON)){
			ipV6 = true;
		}
		
		if(fip != null) {
			String[] ipAdds = fip.split(AseStrings.COMMA);
			int ipLen = ipAdds.length;
			if(ipLen > 1){
				isMultihomingEnabled = true;
			}
		}
		
		if (m_l.isDebugEnabled()) {
			m_l.debug("Multihoming Enabled :: " + isMultihomingEnabled);
		}
		// This is done to handle the binding of socket on IPv6 Address
		//The kernel validates the uniqueness of IPv6 FIP on the link 
		//by sending Duplicate Address Detection (DAD) messages which
		//may take some time.So, the sleep to cover that time.
		String value1 = m_configRepository.getValue(Constants.PROP_IPv6_DUPLICATION_DETECTION);
		if(ipV6 && value1!=null){
			try {
				int timeout = Integer.parseInt(value1);
				if(timeout<0) {
					timeout=3000;
				}
				Thread.sleep(timeout);
			} catch (Exception e) {
				m_l.error("Illegal Address Type ",e);
			}
		}
		// Start listening
		// We listen to the FIP - call to getIPAddress and
		// host IP - retrieve from the COnfig Repository (BPUsa07229)
		ListenPoint lp = null;

		List<String>  ipAdressList =  m_connector.getIPAddressList();
		List<Integer> portAdressList = m_connector.getPortList();

		try {
			if(ipAdressList != null){

				controlMgr = (ControlManager)Registry.lookup(Constants.NAME_CONTROL_MGR);
				subSystem = controlMgr.getSelfInfo();
				// Supporting multihomed host :  adding multiple listeners
				for(int i=0;i<ipAdressList.size();i++){

					if(subSystem.getMode() == AseModes.FT_N_PLUS_K && ipAdressList.get(i).equals("0.0.0.0")){
						//do not open listen point for 0.0.0.0 on n+k setup 
						if (m_l.isInfoEnabled()) {
							m_l.info("Mode is N+K so not opening listen point for 0.0.0.0");
						}
					}else{
						lp = new ListenPoint(ipAdressList.get(i), portAdressList.get(i), DsSipTransportType.UDP);
						m_transportLayer.listenPort(portAdressList.get(i),
								DsSipTransportType.UDP,
								InetAddress.getByName(ipAdressList.get(i)));
						m_listenPoints.add(lp);
						if(m_l.isInfoEnabled()) {
							m_l.info("SIP Listener added for : " + lp.toString());
						}

						lp = new ListenPoint(ipAdressList.get(i), portAdressList.get(i), DsSipTransportType.TCP);
						m_transportLayer.listenPort(portAdressList.get(i),
								DsSipTransportType.TCP,
								InetAddress.getByName(ipAdressList.get(i)));
						m_listenPoints.add(lp);
						if(m_l.isInfoEnabled()) {
							m_l.info("SIP Listener added for : " + lp.toString());
						}
					}

				}
			}

			// Adding TLS listen point
			if(m_enableTLS) {
				lp = new ListenPoint(m_connector.getIPAddress(), m_connector.getTlsPort(), DsSipTransportType.TLS);
				m_transportLayer.listenPort(m_connector.getTlsPort(),
						DsSipTransportType.TLS,
						InetAddress.getByName(m_connector.getIPAddress()));
				m_listenPoints.add(lp);
				if(m_l.isInfoEnabled()) {
					m_l.info("SIP Listener added for : " + lp.toString());
				}
			}
			
			if(m_configRepository.getValue(Constants.LISTEN_ON_PHYSICAL_IP).equalsIgnoreCase("true")){
				if(m_l.isInfoEnabled()) {
					m_l.info("Opening listen point on physical IP...");
				}
			String bindAddress;
			if(m_configRepository.getValue(Constants.DUAL_LAN_SIGNAL_IP) == null) {
				bindAddress = AseUtils.getIPAddress(m_configRepository.getValue(Constants.OID_BIND_ADDRESS));
			}else	{
				bindAddress = AseUtils.getIPAddress(m_configRepository.getValue(Constants.DUAL_LAN_SIGNAL_IP));
			}
			if (null == bindAddress) {
				m_l.error("Bind Address not specified");
			} else {
				lp = new ListenPoint(bindAddress, m_connector.getPort(), DsSipTransportType.UDP);
				m_transportLayer.listenPort(m_connector.getPort(),
						DsSipTransportType.UDP,
						InetAddress.getByName(bindAddress));
				m_listenPoints.add(lp);
				if(m_l.isInfoEnabled()) {
					m_l.info("SIP Listener added for : " + lp.toString());
				}

				// Adding TCP listen point
				lp = new ListenPoint(bindAddress, m_connector.getPort(), DsSipTransportType.TCP);
				m_transportLayer.listenPort(m_connector.getPort(),
						DsSipTransportType.TCP,
						InetAddress.getByName(bindAddress));
				m_listenPoints.add(lp);
				if(m_l.isInfoEnabled()) {
					m_l.info("SIP Listener added for : " + lp.toString());
				}

				// Adding TLS listen point
				if(m_enableTLS) {
					lp = new ListenPoint(bindAddress, m_connector.getTlsPort(), DsSipTransportType.TLS);
					m_transportLayer.listenPort(m_connector.getTlsPort(),
							DsSipTransportType.TLS,
							InetAddress.getByName(bindAddress));
					m_listenPoints.add(lp);
					if(m_l.isInfoEnabled()) {
						m_l.info("SIP Listener added for : " + lp.toString());
					}
				}
			}
		 }
		} catch(UnknownHostException ex) {
			// Log error
			m_l.error("SIL start: Adding SIP Listener for : " + m_connector.getIPAddress() +
					" " + ", port no. : " + m_connector.getPort(), ex);
		} catch(DsException ex) {
			// Log error
			m_l.error("SIL start: Adding SIP Listener for : " + m_connector.getIPAddress() +
					" " + ", port no. : " + m_connector.getPort(), ex);
		} catch(IOException ex) {
			// Log error
			m_l.error("SIL start: Adding SIP Listener for : " + m_connector.getIPAddress() +
					" " + ", port no. : " + m_connector.getPort(), ex);
		}

		if (m_l.isDebugEnabled()) {
			m_l.debug("start():exit");
		}
	}

	/**
	 * Stops listening to messages from network. Any messages arriving after
	 * this method call will get discarded.
	 */
	public void shutdown() {
		if (m_l.isDebugEnabled()) {
			m_l.debug("shutdown():enter..!");
		}

		try {
			// Remove all listen ports
			for(int i=0; i < m_listenPoints.size(); i++) {
				ListenPoint lp = (ListenPoint)m_listenPoints.get(i);
				m_transportLayer.removeListenPort(lp.getPort(),
						lp.getProtocol(),
						InetAddress.getByName(lp.getAddress()),
						0);
				if(m_l.isInfoEnabled()) {
					m_l.info("SIP Listener removed for : " + lp.toString());
				}
			}

			m_transactionMgr.shutdownReject(0, 0);
		} catch(DsAlreadyShuttingDownException ex) {
			m_l.error("Transaction manager already shutting down.");
		} catch(UnknownHostException ex) {
			// Log error
			m_l.error("SIL shutdown", ex);
		}

		if (m_l.isDebugEnabled()) {
			m_l.debug("shutdown():exit");
		}
	}
	
	/*
	 * Call received from Connector to enable softshutdown flag
	 * so as to reject incoming requests received after softshutdown
	 */
	public void softShutdown() {
		m_l.debug("softShutdown():enter..!");
		
		isSoftShutdownEnabled = true;
	}



	/**
	 * This method is called by stack interface request listener method in
	 * <code>AseSipRequestListener</code> or
	 * <code>AseSipServerTxnListener</code> for further processing of a SIP
	 * request received from stack (network). After processing, the request
	 * is handed over to either container or default handler.
	 *
	 * Following logic is applied first to determine type of request:
	 *
	 * - If dialog-id in request does not have a From tag, then it is an
	 *   erroneous request message.
	 *
	 * - If dialog-id in request does not have a To tag, then it may be an
	 *   initial request.
	 *
	 * - If dialog-id in request has a To tag, then it is a subsequent
	 *   request.
	 *
	 * If it is an initial request, it hands over the request to container for
	 * further processing. If it is stray or erroneous request, pass it to
	 * default handler.
	 *
	 * If the request is a CANCEL request, it is given to associated
	 * <code>AseSipSession</code> object, which updates its FSM and returns
	 * a code. Sip session may also throw an exception, if the request is not
	 * conforming.
	 * @see AseSipSession#recvRequest(AseSipServletRequest)
	 *
	 * Depending on the returned code from
	 * <code>AseSipSession.recvRequest()</code>, the request is sent to
	 * container or default handler for further processing.
	 *
	 * @param request SIP request object received from network
	 */
	public void handleRequest(AseSipServletRequest request) {
		if (m_l.isDebugEnabled()) {
			m_l.debug("handleRequest(AseSipServletRequest):enter");
		}
		DsSipRequest dsReq = request.getDsRequest();


		request.setAppChaining(false);
		
		AseLatencyData.noteLatencyData(request, AseLatencyData.ComponentTimes.STACK, true);
		selectiveLogger.logIncomingRequest(request , m_dialogMgr.getSession(request));
				
		//Response time measurement

		if(request.getMessagePriority()) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("handleRequest() IN: Received Priority request from network");
			}
//			m_nsepOcm.measureResponseTime(AseSipOverloadManager.MESSAGE_IN, request);
		} else {
	//		m_ocm.measureResponseTime(AseSipOverloadManager.MESSAGE_IN, request);
		}
		
		//Start: Writing the code to find out the sipSession early for tracing purpose
		AseSipSession session = null;
		AseSipTransaction txn = (AseSipTransaction)request.getServerTxn();
		if (txn != null){
			session = txn.getSipSession();
		}
		if (session == null){
			session = m_dialogMgr.getSession(request);
		}
		if (session != null){
			request.setAseSipSession(session);
		}
		//End: Writing the code to find out the sipSession early for tracing purpose
		// Dump the body of the incoming request to the call trace log
		traceMessage(request, true);

		//Increment the counter for this incoming request
		AseMeasurementUtil.incrementRequestIn(dsReq.getMethodID());
		
		if(callStatsProcessorObj.isTrafficMonitoringEnabled()){
			if((request.getMethod().equals(AseStrings.BYE)) || 
					(request.getMethod().equals(AseStrings.CANCEL) ) ){
				callStatsProcessorObj.reportInProgressCall(false,(AseApplicationSession)request.getApplicationSession());
			}
		}

		//If Call Priority is enabled for the purpose of INGw messages then don't consider it for  
		//NSEP call processing
		if(m_engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL)) && AseNsepMessageHandler.getMessagePriority(request)) {
			AseMeasurementUtil.incrementPriorityRequestIn(dsReq.getMethodID());
			AseMeasurementUtil.incrementPriorityMessageCount();
		}

		// Enable or disable logging for the current thread based on
		// any criteria specified for SIP messages.
		LoggingCriteria.getInstance().check(request);

		//Fix for LEV-1889 . Condition added to report CallHoldTime only when 
		// BYE is received from the Orig Party.
		if(callStatsProcessorObj.isTrafficMonitoringEnabled()){
			if(request.getMethod().equals(AseStrings.BYE) 
					&& session != null && session.getOrigRequest()!=null && session.getOrigRequest().isIncoming()){
				callStatsProcessorObj.reportCallHoldTime(false,(AseApplicationSession)request.getApplicationSession());
			}
		}
		
		AseSipDialogId dialogId = request.getDialogId();
		if(m_l.isInfoEnabled())
			m_l.info(request.getMethod() + " request with dialog id = " +
					dialogId.toString() + ", call id = " + request.getCallId());

		// Verify presence of From tag in dialog id
		if(!dialogId.hasFromTag()) {
			m_l.error("From tag not present in request [call id = " +
					request.getCallId() + "], passing it to default handler");
			m_defaultHandler.handleErrorRequest(request);

			if (m_l.isDebugEnabled()) {
				m_l.debug("handleRequest(AseSipServletRequest):exit");
			}
			return;
		}

		//AseSipSession session = null;
		//AseSipTransaction txn = (AseSipTransaction)request.getServerTxn();

		// If this is ACK or CANCEL belonging to default proxy operation,
		// proxy it.
		if ((txn != null) && (txn.isDefaultProxy())) {
			if (dsReq.getMethodID() == DsSipConstants.ACK) {
				if (m_l.isDebugEnabled())
					m_l.debug("ACK received for default proxy [call id = " +
							request.getCallId() + "]. NOOP");

				// ACK for 2xx will not come here but on stray message iface
				// Eat up all other ACKs.
			}
			else if (request.getDsRequest().getMethodID() ==
				DsSipConstants.CANCEL) {
				if (m_l.isDebugEnabled())
					m_l.debug("CANCEL received for default proxy " +
							"[call id = " +
							request.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE);

				// Cancel client transaction
				try {
					request.getClientTxn().cancel(null);
				}
				catch(DsException exp) {
					m_l.error("Sending CANCEL " + "[call id = " +
							request.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
				}
				catch(java.io.IOException exp) {
					m_l.error("Sending CANCEL " + "[call id = " +
							request.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
				}

				// Increment the counter for this outgoing request
				// for this CANCEL
				AseMeasurementUtil.incrementRequestOut(DsSipConstants.CANCEL);
			}

			if (m_l.isDebugEnabled()) {
				m_l.debug("handleRequest(AseSipServletRequest):exit");
			}
			return;
		} // if(txn.isDefaultProxy())

		// If the request has come from stack's stray message interface,
		// txn will be null.
		if ((txn == null)){

			// Check for retransmitted ACK
			if (dsReq.getMethodID() == DsSipConstants.ACK) {
				// Find session from dialog manager
				session = m_dialogMgr.getSession(request);

				if (session != null) {
					request.setAseSipSession(session);
				}
				else {
					// Stray ACK, proxy on connection, if not destined for SAS

					String uriHost = null;

					// Remove top Route header, if this indicates SAS
					AseSipRouteHeaderHandler.stripTopSelfRoute(request);

					// Check Request URI
					try {
						if(request.getDsRequest().getRequestURIHost() != null) {
							uriHost = request.getDsRequest().getRequestURIHost().toString();
						} else {
							m_l.error("Request URI host is NULL");
						}
					}
					catch(DsSipParserException exp) {
						m_l.error("Getting Request-URI: " + "[call id = " +
								request.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE,exp);
					}

					if ((uriHost != null) &&
							(AseSipConnector.isMatchingAddress(uriHost))) {
						// it matched an address, eat up ACK
						if (m_l.isDebugEnabled()) {
							m_l.debug("Request URI matched, eating up stray ACK");
							m_l.debug("handleRequest(AseSipServletRequest):exit");
						}
						return;
					}

					// Validate Max-Forwards header value
					try {
						AseSipMaxForwardsHeaderHandler.validateMaxForwards(request);
					} catch(javax.servlet.sip.TooManyHopsException exp) {
						if (m_l.isDebugEnabled()) {
							m_l.debug("ACK Max-Forwards exhausted. Discarding.");
							m_l.debug("handleRequest(AseSipServletRequest):exit");
						}
						return;
					}

					if (m_l.isDebugEnabled()) {
						m_l.debug("Proxying stray ACK");
					}

					// Clear binding info
					dsReq.setBindingInfo(new DsBindingInfo());

					try {
						DsSipTransactionManager.getConnection(dsReq).
						send(dsReq);

						if(m_sipMsgLogger != null) {

							m_sipMsgLogger.logRequest(
									DsMessageLoggingInterface.REASON_NO_HANDLER,
									DsMessageLoggingInterface.DIRECTION_OUT,
									dsReq);
						}
					}
					catch(Exception exp) {
						m_l.error("Proxying stray ACK: " + "[call id = " +
								request.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
					}

					if (m_l.isDebugEnabled()) {
						m_l.debug("handleRequest(AseSipServletRequest):exit");
					}
					return;
				}
			}
			else {
				// pass it to default handler and return.
				m_l.error("No session found. Passing request " +
						"[call id = " +
						request.getCallId() + "] to default handler");
				//m_defaultHandler.handleErrorRequest(request);
				m_defaultHandler.handleStrayRequest(request);

				if (m_l.isDebugEnabled()) {
					m_l.debug("handleRequest(AseSipServletRequest):exit");
				}
				return;
			}
		}
		else {
			session = txn.getSipSession();
			if(request.getDsRequest().getMethodID() == DsSipConstants.CANCEL) {
				// If it is CANCEL request, then its 200 OK would have already
				// been sent by stack. Increment the counter here
				AseMeasurementUtil.incrementResponseOut(200);
				if(m_engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL)) && AseNsepMessageHandler.getMessagePriority(request)) {
					AseMeasurementUtil.incrementPriorityMessageCount();
				}
			}
		}

		// Check if dialog id has To tag
		if(!dialogId.hasToTag()) {
			if (m_l.isDebugEnabled()) {
				m_l.debug("handleRequest(): dialogId does not have 'to' tag");
			}

			// See if session exists for this request (as in case of CANCEL)
			if((session == null)
					&& (dsReq.getMethodID() != DsSipConstants.CANCEL)
					&& (dsReq.getMethodID() != DsSipConstants.ACK) ) {
				// Dialog is not established
				boolean generateServerError = false;
				//parse initial request for presence of ets namespace in RPH
				boolean priorityMsg =  false;
				
				
					
				if(m_engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
					priorityMsg = AseNsepMessageHandler.getMessagePriority(request);
				}
				
				/*
				 * All the incoming requests received during softstop
				 * are rejected from here
				 */
				if(isSoftShutdownEnabled){
					m_l.error("System has gone fo SoftShutdown:  Rejecting New Request" );
					rejectIncomingRequest(request, priorityMsg);
					return;		
				}
				if(callStatsProcessorObj.isTrafficMonitoringEnabled()){
					if(request.getMethod().equals(AseStrings.INVITE)) {
						callStatsProcessorObj.reportNewCall((AseApplicationSession)request.getApplicationSession());
					}
				}				
				
				// Increment the counter if priority Call

				// Check if SAS is in ACTIVE state
				if(this.m_connector.getRole() != AseRoles.ACTIVE && request.getHeader("WARMUP") == null ) {
					// the new call cannot be admitted due to overload.
					if(m_l.isInfoEnabled())
						m_l.info("Not in ACTIVE state. Rejecting the initial request [call id = " +
								request.getCallId() + "] with [" +
								m_overloadRejectionCode + "] response");
					generateServerError = true;
				}

				if(generateServerError == false) {

					String dlgId = null;
					//setting default as true so by default all call are allowed
					boolean allowCall = true;
					//As Messages coming from INC have been treated as priority messages for putting 
					//into the same queue. We need to apply the same congestion control logic  
					//to them irrespective of whether these are priority messages or not.
					
					if(m_engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
						
						allowCall = m_nsepOcm.isCallAllowed(request, priorityMsg);
						
//						if (allowCall) {
//							allowCall = aseCompMonManager.canAcceptNewCalls();
//						}
						if(priorityMsg && allowCall && m_nsepOcm.updatePriorityCongestion()) {
							AseMeasurementUtil.incrementCngnExemptedPriorityMessage(dsReq.getMethodID());
						}
					} else {
						
						/*
						 * Congestion control change started for INAP it need to
						 * check in dialog id map with app session
						 */
						AseHost host = (AseHost) m_engine
								.findChild(Constants.NAME_HOST);

						
						dlgId = (String)request.getHeader(Constants.TC_CORR_ID_HEADER);
				    	
						if(dlgId==null)
				    		dlgId = (String)request.getHeader(Constants.DIALOGUE_ID);

						//changed map type from string aseapplication session to string sipappsession to make it usable in service.
						SipApplicationSession appSession1 = null;

						if (m_l.isInfoEnabled())
							m_l.info("got the dialogid for tcap call as "
									+ dlgId);

						if (dlgId != null)
							appSession1 = host.getAppSessionMapForInapDlgId()
									.get(dlgId);
						
						/*
						 * For hand off scenario it need to check correlation id
						 * from user part of request uri in the map
						 */
						String corrId = (String) request.getAttribute(CORRELATION_ID_ATTRIBUTE);

//						if (appSession1 != null) {
//
//							if (m_l.isInfoEnabled())
//								m_l.info("got the appSession for tcap call");
//							m_ocm.setApplyMaxActiveCallCriteria(false);
//						}
//
//						
						if (m_l.isInfoEnabled())
							m_l.info("got the appSession for tcap call as::"+appSession1);
						
						if (m_l.isInfoEnabled())
							m_l.info("got the corrId for tcap/SIPT correlated call as "
									+ corrId);
//
//						if (corrId != null)
//							m_ocm.setApplyMaxActiveCallCriteria(false);
						
						//Apply OCMP if both appsesion and correlation is null.
						//added isInitial check for saftey so that counter is not increased 
						// for any stray request reaching this check due to imprepr handling in this block
						if(appSession1 == null && corrId == null){
							
							/**
							 * report new SS7 call  
							 */
							if(callStatsProcessorObj.isTrafficMonitoringEnabled()){
								if(request.getMethod().equals(AseStrings.NOTIFY)) {
									callStatsProcessorObj.reportNewCall(null);
								}
							}	
							
							allowCall = m_ocm.isCallAllowed(request);
							
//							if (allowCall) {
//                             allowCall = aseCompMonManager.canAcceptNewCalls();
//							}
						}
						

						/*
						 * Congestion control change ended
						 */
						
//						allowCall = m_ocm.isCallAllowed(request);
//						//This is done to fix the issue that if it is false then it will 
//						//remain false for ever.
//						m_ocm.setApplyMaxActiveCallCriteria(true);
					}
					if(allowCall) {
						// Overload Manager permits, so the dialog can be
						// initiated, pass request to container
						if(m_l.isInfoEnabled())
							m_l.info("Passing initial request [call id = " +
									request.getCallId() + "] to container");
						request.extractParamsFromRequestURI();
						request.setInitial();
						request.setInProgress();
						AseMessage aseMsg = new AseMessage(request,AseNsepMessageHandler.getMessagePriority(request));
						if (dlgId != null)
							aseMsg.setInapMessage(true);
						//START: Prepaid Traffic Distribution
						if (prepaidTrafficDist.equals(AseStrings.TRUE_SMALL)){
							boolean prepaidCall = this.isPrepaidCall(request);
							if (prepaidCall){
								if(m_l.isInfoEnabled())
									m_l.info("It is a prepaid Call");
								aseMsg.setPrepaidMessage(true);
							}
						}
						//END: Prepaid Traffic Distribution
						
						m_connector.sendToContainer(aseMsg);
					} else {
						// the new call cannot be admitted due to overload.
						if(m_l.isInfoEnabled())
							m_l.info("Overloaded. Rejecting the initial request [call id = " +
									request.getCallId() + "] with [" +
									m_overloadRejectionCode + "] response");
						generateServerError = true;
					}
				}

				if(generateServerError == true) {
					DsSipServerTransaction servertxn = request.getServerTxn();
					DsSipResponse errResp = new DsSipResponse(
							priorityMsg ? m_nsepOcmRejectionCode : m_overloadRejectionCode,
									dsReq,
									null,
									null);
					errResp.addHeader(new DsSipRetryAfterHeader(m_retryAfterDelay));
					try {
						if(priorityMsg) {
							errResp.setMessagePriority(true);
							errResp.addHeaders(dsReq.getHeaders(new DsByteString(Constants.RPH)));
							AseMeasurementUtil.incrementPriorityMessageCount();
							AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
							if(request.getMethod().equals(AseStrings.INVITE)) {
								AseMeasurementUtil.incrementPriorityRejectedInvites();
							}
						}
						String contentType = request.getContentType();
						if(null != contentType && contentType.startsWith(AseStrings.SDP_MULTIPART)){
							ByteArrayOutputStream bos = new ByteArrayOutputStream();
							try {
								multiPartContent.writeTo(bos);
								errResp.setBody(bos.toByteArray(), new DsByteString(multiPartContent.getContentType()));
								AseSipServletResponse response = new AseSipServletResponse(m_connector,errResp);
								response.addHeader("Reason", "Q.850;cause="+releaseCause);
							} catch (IOException exp) {
								m_l.error(exp.getMessage(), exp);
							}catch (MessagingException exp) {
								m_l.error(exp.getMessage(), exp);
							}
							
						}else{
							AseSipServletResponse response = new AseSipServletResponse(m_connector,errResp);
							response.addHeader("Reason", "Q.850;cause="+releaseCause);
						}
						servertxn.sendResponse(errResp);
						AseMeasurementUtil.incrementResponseOut(priorityMsg ? 
								m_nsepOcmRejectionCode : m_overloadRejectionCode);
						AseMeasurementUtil.counterRejectedRequests.increment();
					} catch(DsException exp) {
						m_l.error("sending response [" +
								m_overloadRejectionCode + AseStrings.SQUARE_BRACKET_CLOSE + errResp, exp);
					} catch(IOException exp) {
						m_l.error("sending response [" +
								m_overloadRejectionCode + AseStrings.SQUARE_BRACKET_CLOSE + errResp, exp);
					}
				}

				if (m_l.isDebugEnabled()) {
					m_l.debug("handleRequest(AseSipServletRequest):exit");
				}
				return;
			} else if(dsReq.getMethodID() == DsSipConstants.ACK) {
				if(m_l.isDebugEnabled()) {
					m_l.debug("Received ACK for non-2xx response to initial INVITE. Eating it up.");
					m_l.debug("handleRequest(AseSipServletRequest):exit");
				}
				return;
			}
		} else {
			// For ACKs, session would be retrived from dialog-manager
			if(dsReq.getMethodID() == DsSipConstants.ACK) {
				session = m_dialogMgr.getSession(request);
			}
		}

		// Dialog is already established (subsequent request)
		// First look for session in transaction, if not found then check
		// with dialog manager
		if(session == null) {
			session = m_dialogMgr.getSession(request);

			if (session == null) {
				// Session not found. If this is a NOTIFY, we may find the
				// session in the subscription manager. Take a look

				if (dsReq.getMethodID() == DsSipConstants.NOTIFY) {
					if (null == (session =
						m_subscriptionManager.getSession(request))) {
						// Session not found, its an stray request,
						// pass it to default handler
						if(m_l.isInfoEnabled()) {
							m_l.info("No associated session found with NOTIFY dialog id [" +
									dialogId.toString() +
							"] passing request to default handler");
						}
						m_defaultHandler.handleStrayRequest(request);
						if(m_l.isDebugEnabled()) {
							m_l.debug("handleRequest(AseSipServletRequest):exit");
						}
						return;
					}
					else {
						if(m_l.isDebugEnabled()) {
							m_l.debug("Found session in subscription manager");
						}
					}

					if (m_l.isDebugEnabled())
						m_l.debug("Session [id = " + session.getId()
								+ "] found, associate it with transaction");
					txn.setSipSession(session);
				}
				else {
					// Session not found, its an stray request,
					// pass it to default handler
					if(m_l.isInfoEnabled())
						m_l.info("No associated session found with dialog id [" +
								dialogId.toString() +
						"] passing request to default handler");
					m_defaultHandler.handleStrayRequest(request);

					if(m_l.isDebugEnabled()) {
						m_l.debug("handleRequest(AseSipServletRequest):exit");
					}
					return;
				}
			}
			else {
				// Session found. Associate it with request and transaction
				if(m_l.isDebugEnabled())
					m_l.debug("Session [id = " + session.getId()
							+ "] found, associate it with transaction");
				txn.setSipSession(session);
			}
		}

		// Associate session with request
		request.setAseSipSession(session);

		// Handing over request to AseSipSession and processing it further
		// depending on the return code.
		if(m_l.isInfoEnabled())
			m_l.info("Passing request to session");

		AseSipSession lockedSession = session;

		try {
			lockedSession.acquire();
		} 
		catch(AseLockException exp) {
			m_l.fatal("SESSION LOCK ACQUIRE FAILURE: " + "Session [id = " 
					+ session.getId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
			if(m_l.isDebugEnabled()) {
				m_l.debug("handleRequest(AseSipServletRequest):exit");
			}
			return;
		}

		try {

			// If NOTIFY
			if (dsReq.getMethodID() == DsSipConstants.NOTIFY) {
				if (!session.isMatchingSession(request)) {
					// This is NOT a matching session
					// Derive from this session to create multiple dialog
					if (m_l.isDebugEnabled())
						m_l.debug("Create multiple dialog for NOTIFY " +
								"[call id = " +
								request.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE);

					// Use original session for derivation (just like that)
					session = m_factory.createSession(lockedSession);
					
					//for tcap calls Active Sip session count will not be mantained
					if(request.getHeader(Constants.DIALOGUE_ID)!=null){
						AseMeasurementUtil.counterActiveSIPSessions.decrement();
					}
					
					// Set session into request & txn
					txn.setSipSession(session);
					request.setAseSipSession(session);
				}
			}

			try {
				int i = session.recvRequest(request);
				// Increment the counter if priority Call
				AseApplicationSession appSession =
					(AseApplicationSession)request.getApplicationSession();
				switch (i) {
				case AseSipSession.NOOP:
					if(m_l.isDebugEnabled()) {
						m_l.debug("SIP session return code: NOOP");
					}

					// Do not send this request to container. No more
					// processing required, cleanup resources associated
					// what cleanup to do ??
					break;

				case AseSipSession.CONTINUE:
					if(m_l.isDebugEnabled()) {
						m_l.debug("SIP session return code: CONTINUE");
					}

					// Pass this request to container
					if (m_l.isInfoEnabled()) {
						m_l.info("Send subsequent request to container");
					}
					m_connector.sendToContainer(new AseMessage(request,request.getMessagePriority()));
					break;

				case AseSipSession.CANCEL_REQUEST:
					if(m_l.isDebugEnabled()) {
						m_l.debug("SIP session return code: CANCEL_REQUEST");
					}

					// Cancel original INVITE, send 487
					if (m_l.isInfoEnabled()) {
						m_l.info("Going to send 487 for orig INVITE");
					}
					try {
						DsSipRequest dsRequest = request.getDsRequest();
						DsSipResponse dsRes = new DsSipResponse(487, dsRequest, null, null);

						// Adding RPH Header if INVITE is priority 
						if(AseNsepMessageHandler.getMessagePriority(request)) {
							AseMeasurementUtil.incrementPriorityMessageCount();
							AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
							dsRes.addHeaders(dsRequest.getHeaders(new DsByteString(Constants.RPH)));
						}
						((DsSipServerTransaction)txn).sendResponse(dsRes);

						//Increment the counter for this outgoing response
						AseMeasurementUtil.incrementResponseOut(487);
					} catch(DsException exp) {
						m_l.error("sending 487: " + "[call id = " +
								request.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
					} catch(IOException exp) {
						m_l.error("sending 487: " + "[call id = " +
								request.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
					}

					// Pass this request to container
					if (m_l.isInfoEnabled()) {
						m_l.info("Send subsequent request to container");
					}
					//ase_msg.setWorkQueue(appSession.getIc().getWorkQueue());

					m_connector.sendToContainer(new AseMessage(request,request.getMessagePriority()));
					break;

				case AseSipSession.OPTIONS_RESPONSE:
					if(m_l.isDebugEnabled()) {
						m_l.debug("SIP session return code: OPTIONS_RESPONSE");
					}

					if (m_l.isInfoEnabled()) {
						m_l.info("Going to send 200 for OPTIONS");
					}
					try {
						DsSipRequest dsRequest = request.getDsRequest();
						DsSipResponse dsRes = new DsSipResponse(200, dsRequest, null, null);

						// Adding RPH Header if INVITE is priority 
						if(AseNsepMessageHandler.getMessagePriority(request)) {
							dsRes.addHeaders(dsRequest.getHeaders(new DsByteString(Constants.RPH)));
							AseMeasurementUtil.incrementPriorityMessageCount();
						}
						((DsSipServerTransaction)txn).sendResponse(dsRes);

						//Increment the counter for this outgoing response
						AseMeasurementUtil.incrementResponseOut(200);
					} catch(DsException exp) {
						m_l.error("sending 200: " + "[call id = " +
								request.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
					} catch(IOException exp) {
						m_l.error("sending 200: " + "[call id = "+ request.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
					}
					break;

				case AseSipSession.TOO_MANY_HOPS:
					if(m_l.isDebugEnabled()) {
						m_l.debug("SIP session return code: TOO_MANY_HOPS");
					}

					// Too many hops, send 483
					if (m_l.isInfoEnabled()) {
						m_l.info("Going to send 483");
					}
					try {
						DsSipRequest dsRequest = request.getDsRequest();
						DsSipResponse dsRes = new DsSipResponse(483, dsRequest, null, null);

						// Adding RPH Header if INVITE is priority 
						if(AseNsepMessageHandler.getMessagePriority(request)) {
							AseMeasurementUtil.incrementPriorityMessageCount();
							AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
							dsRes.addHeaders(dsRequest.getHeaders(new DsByteString(Constants.RPH)));
						}
						((DsSipServerTransaction)txn).sendResponse(dsRes);
						//Increment the counter for this outgoing response
						AseMeasurementUtil.incrementResponseOut(483);
					} catch(DsException exp) {
						m_l.error("sending 483: " + "[call id = "+ request.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
					} catch(IOException exp) {
						m_l.error("sending 483: " + "[call id = "+ request.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
					}
					break;

				default:
					m_l.error("session.recvRequest() return code [" + i +
							" for call id [" + request.getCallId() +
					"] is not handled");
				}
			} catch(AseStrayMessageException exp) {
				m_l.error("passing request [call id = " + request.getCallId() +
						"] to default handler", exp);
				m_defaultHandler.handleStrayRequest(request);
			} catch(AseCannotCancelException exp) {
				m_l.error("call id = " + request.getCallId(), exp);
			} catch(AseOutOfSequenceException exp) {
				m_l.error("call id = " + request.getCallId(), exp);

				if(dsReq.getMethodID() != DsSipConstants.ACK) {
					if (m_l.isInfoEnabled()) {
						m_l.info("Sending 400 for request received out of sequence");
					}

					String body =
						new String(request.getMethod() + " received out of sequence");
					DsSipRequest dsRequest = request.getDsRequest();
					DsSipResponse dsResp = new DsSipResponse(	400,
							dsRequest,
							body.getBytes(),
							new DsByteString("text/html"));
					try {
						if(AseNsepMessageHandler.getMessagePriority(request)) {
							dsResp.addHeaders(dsRequest.getHeaders(new DsByteString(Constants.RPH)));
							AseMeasurementUtil.incrementPriorityMessageCount();
							if(dsReq.getMethodID() == DsSipConstants.INVITE) {
								AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
							}
						}
						((DsSipServerTransaction)txn).sendResponse(dsResp);
						//Increment the counter for this outgoing response
						AseMeasurementUtil.incrementResponseOut(dsResp.getStatusCode());
					} catch(DsException exp1) {
						m_l.error("sending 400" +"  for call id = " + request.getCallId(), exp1);
					} catch(IOException exp1) {
						m_l.error("sending 400" +"  for call id = " + request.getCallId(), exp1);
					}
				}
			} catch(IllegalStateException exp) {
				m_l.error("call id = " + request.getCallId(), exp);

				if(dsReq.getMethodID() != DsSipConstants.ACK) {
					if (m_l.isInfoEnabled()) {
						m_l.info("Sending 481 for request received in illegal session state");
					}

					String body =
						new String(request.getMethod() + " received in illegal dialog state");
					DsSipRequest dsRequest = request.getDsRequest();
					DsSipResponse dsResp = new DsSipResponse(	481,
							request.getDsRequest(),
							body.getBytes(),
							new DsByteString("text/html"));
					try {
						if(AseNsepMessageHandler.getMessagePriority(request)) {
							dsResp.addHeaders(dsRequest.getHeaders(new DsByteString(Constants.RPH)));
							AseMeasurementUtil.incrementPriorityMessageCount();
							if(dsReq.getMethodID() == DsSipConstants.INVITE) {
								AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
							}
						}
						((DsSipServerTransaction)txn).sendResponse(dsResp);
						//Increment the counter for this outgoing response
						AseMeasurementUtil.incrementResponseOut(dsResp.getStatusCode());
					} catch(DsException exp1) {
						m_l.error("sending 481" +"  for call id = " + request.getCallId(), exp1);
					} catch(IOException exp1) {
						m_l.error("sending 481" +"  for call id = " + request.getCallId(), exp1);
					}
				}
			} catch(AseSessionInvalidException exp) {
				m_l.error("call id = " + request.getCallId(), exp);

				if(dsReq.getMethodID() != DsSipConstants.ACK) {
					if (m_l.isInfoEnabled()) {
						m_l.info("Sending 481 for request received for an invalid session");
					}

					String body =
						new String(request.getMethod() + " received for invalid dialog");
					DsSipRequest dsRequest = request.getDsRequest();
					DsSipResponse dsResp = new DsSipResponse(	481,
							request.getDsRequest(),
							body.getBytes(),
							new DsByteString("text/html"));

					try {
						if(AseNsepMessageHandler.getMessagePriority(request)) {
							dsResp.addHeaders(dsRequest.getHeaders(new DsByteString(Constants.RPH)));
							AseMeasurementUtil.incrementPriorityMessageCount();
							if(dsReq.getMethodID() == DsSipConstants.INVITE) {
								AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
							}
						}
						((DsSipServerTransaction)txn).sendResponse(dsResp);
						//Increment the counter for this outgoing response
						AseMeasurementUtil.incrementResponseOut(dsResp.getStatusCode());
					} catch(DsException exp1) {
						m_l.error("sending 481" +"  for call id = " + request.getCallId(), exp1);
					} catch(IOException exp1) {
						m_l.error("sending 481" +"  for call id = " + request.getCallId(), exp1);
					}
				}
			} catch(AseDialogInvalidException exp) {
				m_l.error("call id = " + request.getCallId(), exp);

				if(dsReq.getMethodID() != DsSipConstants.ACK) {
					if (m_l.isInfoEnabled()) {
						m_l.info("Sending 481 for request received in invalid dialog state");
					}

					String body =
						new String(request.getMethod() + " received in invalid dialog state");
					DsSipResponse dsResp = new DsSipResponse(	481,
							request.getDsRequest(),
							body.getBytes(),
							new DsByteString("text/html"));
					try {
						((DsSipServerTransaction)txn).sendResponse(dsResp);

						//Increment the response OUT counter
						AseMeasurementUtil.incrementResponseOut(dsResp.getStatusCode());
						if(request.getMessagePriority()) {
							AseMeasurementUtil.incrementPriorityMessageCount();
							if(dsReq.getMethodID() == DsSipConstants.INVITE) {
								AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
							}
						}
					} catch(DsException exp1) {
						m_l.error("sending 481" +"  for call id = " + request.getCallId(), exp1);
					} catch(IOException exp1) {
						m_l.error("sending 481" +"  for call id = " + request.getCallId(), exp1);
					}
				}
			}catch(AseSubscriptionInvalidException exp) {
				m_l.error("call id = " + request.getCallId(), exp);

				if(dsReq.getMethodID() != DsSipConstants.ACK) {
					if (m_l.isInfoEnabled()) {
						m_l.info("Sending 481 as subscription does not exist");
					}

					String body = new String("Subscription does not exist");
					DsSipResponse dsResp = new DsSipResponse(	481,
							request.getDsRequest(),
							body.getBytes(),
							new DsByteString("text/html"));
					try {
						((DsSipServerTransaction)txn).sendResponse(dsResp);
						//Increment the response OUT counter
						AseMeasurementUtil.incrementResponseOut(dsResp.getStatusCode());
						if(request.getMessagePriority()) {
							AseMeasurementUtil.incrementPriorityMessageCount();
							if(dsReq.getMethodID() == DsSipConstants.INVITE) {
								AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
							}
						}
					} catch(DsException exp1) {
						m_l.error("sending 481" +"  for call id = " + request.getCallId(), exp1);
					} catch(IOException exp1) {
						m_l.error("sending 481" +"  for call id = " + request.getCallId(), exp1);
					}
				}
			}
		}finally{
			try {
				lockedSession.release();
			} catch(AseLockException exp) {
				m_l.fatal("SESSION LOCK RELEASE FAILURE", exp);
			}
		}
		if(m_l.isDebugEnabled()) {
			m_l.debug("handleRequest(AseSipServletRequest):exit");
		}
		return;
	}

	/*
	 * This method is used to reject incoming SIP calls received when
	 * the system is in a softstop state. All the calls received after 
	 * soft stop state are rejected with a 503 response
	 */
	private void rejectIncomingRequest(AseSipServletRequest request , boolean priorityMsg ){

		DsSipServerTransaction servertxn = request.getServerTxn();
		DsSipRequest dsReq = request.getDsRequest();
		DsSipResponse errResp = new DsSipResponse(503,dsReq,null,null);
		try {
			if(priorityMsg) {
				errResp.setMessagePriority(true);
				errResp.addHeaders(dsReq.getHeaders(new DsByteString(Constants.RPH)));
				AseMeasurementUtil.incrementPriorityMessageCount();
				AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
				if(request.getMethod().equals(AseStrings.INVITE)) {
					AseMeasurementUtil.incrementPriorityRejectedInvites();
				}
			}
			String contentType = request.getContentType();
			if(null != contentType && contentType.startsWith(AseStrings.SDP_MULTIPART)){
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				try {
					multiPartContent.writeTo(bos);
					errResp.setBody(bos.toByteArray(), new DsByteString(multiPartContent.getContentType()));
					AseSipServletResponse response = new AseSipServletResponse(m_connector,errResp);
					response.addHeader("Reason", "Call received during SoftShutdown. Hence Rejected");
				} catch (IOException exp) {
					m_l.error(exp.getMessage(), exp);
				}catch (MessagingException exp) {
					m_l.error(exp.getMessage(), exp);
				}
				
			}else{
				AseSipServletResponse response = new AseSipServletResponse(m_connector,errResp);
				response.addHeader("Reason", "Call received during SoftShutdown. Hence Rejected");
			}
			servertxn.sendResponse(errResp);
			AseMeasurementUtil.incrementResponseOut(503);
			AseMeasurementUtil.counterRejectedRequests.increment();
		} catch(DsException exp) {
			m_l.error("sending response [" +
					m_overloadRejectionCode + AseStrings.SQUARE_BRACKET_CLOSE + errResp, exp);
		} catch(IOException exp) {
			m_l.error("sending response [" +
					m_overloadRejectionCode + AseStrings.SQUARE_BRACKET_CLOSE + errResp, exp);
		}
	
	}

	/**
	 * Called by "handleRequest", "handleResponse", "sendRequest" and
	 * "sendResponse" methods to log the incoming and outgoing SIP messages.
	 */
	private void traceMessage(AseSipServletMessage message, boolean incoming) {
		if(m_l.isDebugEnabled()) {
			m_l.debug("traceMessage() called");
		}

		CallTraceService traceService = (CallTraceService)Registry.lookup(Constants.CALL_TRACE_SERVICE);

		if ((!traceService.isContainerTracingEnabled()) || !traceService.matchesCriteria(message)) {
			return;
		}

		if(m_l.isDebugEnabled()) {
			m_l.debug("tracing SIP message...");
		}

		String logMsg = null;

		if (message instanceof SipServletRequest) {
			if (incoming) {
				logMsg = _strings.getString("AseStackInterfaceLayer.traceIncomingRequest", message.toString());
			} else {
				logMsg = _strings.getString("AseStackInterfaceLayer.traceOutgoingRequest", message.toString());
			}
		} else if (message instanceof SipServletResponse) {
			if (incoming) {
				logMsg = _strings.getString("AseStackInterfaceLayer.traceIncomingResponse", message.toString());
			} else {
				logMsg = _strings.getString("AseStackInterfaceLayer.traceOutgoingResponse", message.toString());
			}
		}

		traceService.trace(message, logMsg);
	}



	/**
	 * This method is called by stack interface response callback method in
	 * <code>AseSipClientTnxListener</code> for further processing of a SIP
	 * response received from stack (network) or simulated client side
	 * timeout 408 responses. After processing, the response is handed over
	 * to either container or default handler.
	 *
	 * - First retrieve the associated <code>AseSipSession</code> object from
	 *   the response.
	 *
	 * - If no session is associated with this response, pass this response
	 *   to default handler.
	 *
	 * - Process any pending CANCEL with client transaction
	 * - Pass this response to <code>AseSipSession</code>.
	 *   @see AseSipSession#recvResponse(AseSipServletResponse)
	 *
	 * Depending on return code or exception from method call
	 * <code>AseSipSession.recvResponse()</code>, ack and/or send this response to
	 * container or default handler, or do nothing.
	 *
	 * @param response SIP response object received from network
	 */
	public void handleResponse(AseSipServletResponse response) {
		if(m_l.isDebugEnabled())	{
			m_l.debug("handleResponse(AseSipServletResponse):enter");
		}

		selectiveLogger.logResponse(DsMessageLoggingInterface.DIRECTION_IN, response);
		

		//Log in error responses like 400,417,420
		if(response.getStatus() == 400
				||response.getStatus() == 417
				||response.getStatus() == 420)	{
			m_l.error(" Error condition Response: " + response);
			m_l.error(" Error condition Request: " + response.getRequest());
		}

		response.setAppChaining(false);
		AseLatencyData.noteLatencyData(response, AseLatencyData.ComponentTimes.STACK, true);

		AseMeasurementUtil.incrementResponseIn(response.getStatus());
		//Response time measurement
		boolean priorityMsg = false;
		if(m_engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
			priorityMsg = AseNsepMessageHandler.getMessagePriority(response);
		}
		if(priorityMsg) {
	//		m_nsepOcm.measureResponseTime(AseSipOverloadManager.MESSAGE_IN, response);
			AseMeasurementUtil.incrementPriorityMessageCount();
		} else {
	//		m_ocm.measureResponseTime(AseSipOverloadManager.MESSAGE_IN, response);
		}
		if((response.getMessagePriority()) 
				&& (response.getStatus() >200) 
				&& (response.getDsResponse().getMethodID() == DsSipConstants.INVITE)) {
			AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
			if(m_l.isDebugEnabled()) {
				m_l.debug("Received "+response.getStatus()+" response for "+response.getRequest().getMethod());
			}
		}

		// Dump the body of the incoming response to the call trace log
		traceMessage(response, true);

		// Enable or disable logging for the current thread based on any
		// criteria currently set on SIP messages.
		LoggingCriteria.getInstance().check(response);

		AseSipDialogId dialogId = response.getDialogId();

		if (m_l.isInfoEnabled())
			m_l.info(response.getMethod() + " response with dialogId = " +
					dialogId.toString() + ", call id = " +
					response.getCallId());

		AseSipTransaction astTxn = (AseSipTransaction)response.getClientTxn();
		DsSipResponse dsRes = response.getDsResponse();

		// If response is for default proxy operation, proxy it.
		if ((astTxn != null) && astTxn.isDefaultProxy()) {
			if (m_l.isDebugEnabled())
				m_l.debug("Response received for default proxy [call id = " +
						response.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE);

			// If it is a non-2xx final response to INVITE, acknowledge it.
			if ((dsRes.getResponseClass() > 2) &&
					(dsRes.getMethodID() == DsSipConstants.INVITE)) {
				try {

					//BpInd 18330
					DsSipAckMessage ackMessage = new DsSipAckMessage(dsRes,null,null);
					DsSipRequest request = ((AseSipServletRequest)response.getRequest()).getDsRequest();
					if(m_l.isDebugEnabled()) {
						m_l.debug("the error intial request is: "+request);
					}
					if(request.getHeader( new DsByteString(AseStrings.HDR_ACCEPT_CONTACT))!=null)
					{
						if(m_l.isDebugEnabled()) {
							m_l.debug("Header Accept present");
						}
						ackMessage.addHeader(request.getHeader( new DsByteString(AseStrings.HDR_ACCEPT_CONTACT)),false);
					}

					if(request.getHeader( new DsByteString(AseStrings.HDR_REJECT_CONTACT ))!=null)
					{
						ackMessage.addHeader(request.getHeader( new DsByteString(AseStrings.HDR_REJECT_CONTACT)),false);
					}

					if(request.getHeader( new DsByteString(AseStrings.HDR_REQUEST_DISPOSITION))!=null)
					{
						ackMessage.addHeader(request.getHeader( new DsByteString(AseStrings.HDR_REQUEST_DISPOSITION)),false);
					}


					((DsSipClientTransaction)astTxn).ack(ackMessage);
				} 
				catch(DsException exp) {
					m_l.error("Sending ACK" + " response with dialogId = " +
							dialogId.toString() + ", call id = " +
							response.getCallId(), exp);
				} 
				catch(java.io.IOException exp) {
					m_l.error("Sending ACK" + " response with dialogId = " +
							dialogId.toString() + ", call id = " +
							response.getCallId(), exp);
				}
			}

			// Remove top Via header, if it matches to SAS
			AseSipViaHeaderHandler.removeTopViaHeader(response);

			// Clear binding info from response
			dsRes.setBindingInfo(new DsBindingInfo());

			// Set this response in transaction
			astTxn.setAseSipResponse(response);

			// Proxy forward this response
			try {
				astTxn.getAseSipRequest().getServerTxn().sendResponse(dsRes);
			} 
			catch(DsException exp) {
				m_l.error("Proxying response" +" [call id = " 
						+ response.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
			} 
			catch(java.io.IOException exp) {
				m_l.error("Proxying response" +" [call id = " 
						+ response.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
			}

			// Increment the counter for this outgoing response for
			// this Server Txn.
			AseMeasurementUtil.incrementResponseOut(response.getStatus());
			if(dsRes.getMessagePriority()) {
				AseMeasurementUtil.incrementPriorityMessageCount();
				// if its non-2XX final response for INVITE increment counter for unsuccessful session;
				if ((dsRes.getResponseClass() > 2) &&
						(dsRes.getMethodID() == DsSipConstants.INVITE)) {
					if(m_l.isDebugEnabled()) {
						m_l.debug("Received "+dsRes.getStatusCode()+" response for INVITE");
					}
					AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
				}
			}

			if(m_l.isDebugEnabled()) {
				m_l.debug("handleResponse(AseSipServletResponse):exit");
			}
			return;
		} // if( (astTxn != null) && astTxn.isDefaultProxy())

		AseSipSession session = response.getAseSipSession();

		if (session == null) {
			// Find session for response from dialog manager
			if (response.getDsResponse().getResponseClass() == 2) {
				// It's a retransmission of 2xx response for a proxy
				// operation. Find session from dialog manager
				if (response.getRequest() != null) {
					session = m_dialogMgr.
					getSession((AseSipServletRequest)response.
							getRequest());
				}
			}

			if (session != null) {
				// If session found, associate with 2xx and proceed
				if(m_l.isDebugEnabled())
					m_l.debug("2xx retransmission received [call id = " +
							response.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE);
				response.setAseSipSession(session);
			}
			else {
				// response is stray message
				m_l.error("SIP session not present, sending response [call id = " +
						response.getCallId() + "] to default handler");
				m_defaultHandler.handleStrayResponse(response);

				if(m_l.isDebugEnabled()) {
					m_l.debug("handleResponse(AseSipServletResponse):exit");
				}
				return;
			}
		}

		// Process any pending CANCEL with client transaction
		if ((astTxn != null) &&
				(dsRes.getMethodID() == DsSipConstants.INVITE)) {
			if(m_l.isDebugEnabled()) {
				m_l.debug("Going to process pending CANCEL, if any");
			}
			astTxn.processPendingCancel(response.getStatus());
		}

		// Handing over response to AseSipSession and processing it further
		// depending on the return code.
		if(m_l.isInfoEnabled())
			m_l.info("SIP session [id = " + session.getId() +
			"] found, sending response to it");

		boolean ackResponse		= false;
		boolean sendToContainer	= false;

		// IC will be same, still storing this session ref for release().
		AseSipSession lockedSession = session;

		try {
			session.acquire();
		} 
		catch(AseLockException exp) {
			m_l.fatal("SESSION LOCK ACQUIRE FAILURE", exp);
			if(m_l.isDebugEnabled()) {
				m_l.debug("handleResponse(AseSipServletResponse):exit");
			}
			return;
		}

		try{
			//
			// Addition for MFR handling starts
			// If this response can create a dialog then check if the response
			// matches the session we have found. If it does then all is good.
			// If not then try to find a matching session in the dialog manager
			// If not found then check if this response can create multiple dialogs
			// if it can then clone the session object
			//

			if (true == response.canCreateDialog() &&
					null != response.getDsResponse().getToTag()) {
				if (m_l.isDebugEnabled())
					m_l.debug("Dialog creating response");
				
				if(callStatsProcessorObj.isTrafficMonitoringEnabled()){
					if((response.getStatus() > 100 && response.getStatus() < 200) 
							&& DsSipConstants.INVITE == response.getDsResponse().getMethodID()){
						callStatsProcessorObj.reportInProgressCall(true,(AseApplicationSession)response.getApplicationSession());
					}
				}
				
				if(callStatsProcessorObj.isTrafficMonitoringEnabled()){
					if((response.getStatus() ==200) 
						&& (response.getDsResponse().getMethodID() == DsSipConstants.INVITE)){
					callStatsProcessorObj.reportCallHoldTime(true,(AseApplicationSession)response.getApplicationSession());
					
					}
				}

				if (!session.isMatchingSession(response)) {
					if (m_l.isDebugEnabled())
						m_l.debug("Response and Session do not match. " +
						"Search for session in the dialog manager");

					session = m_dialogMgr.
					getSession((AseSipServletRequest)response.getRequest());

					if (session == null) {
						// Matching session not found in dialog map
						if(m_l.isDebugEnabled()) {
							m_l.debug("Session not found in dialog manager. ");
						}

						if (response.canCreateMultipleDialogs()){
							// Derive from this session to create multiple dialog
							if(m_l.isDebugEnabled())
								m_l.debug("Create multiple dialog. [call id = " +
										response.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE);

							// Use original session for derivation (just like that)
							session = m_factory.createSession(lockedSession);

							// Set session into txn
							astTxn.setSipSession(session);

							// Set session into response
							response.setAseSipSession(session);
							session.resetDialogParameters(response);
						}
						else {
							// Response can't create multiple dialog, return here.
							if (m_l.isDebugEnabled()) {
								m_l.debug("Cannot create multiple dialog. " + "[call id = "
												+ response.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE);
								m_l.debug("handleResponse(AseSipServletResponse):exit");
							}
							return;
						}
					} 
					else {
						if (m_l.isDebugEnabled())
							m_l.debug("Session found in dialog manager " +
									"[call id = " +
									response.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE);
						response.setAseSipSession(session);
					}
				}
				else {
					if (m_l.isDebugEnabled())
						m_l.debug("Response and session match.");
				}
			}
			else {
				if (m_l.isDebugEnabled())
					m_l.debug("Response does not create a dialog");
			}

			//
			// Addition for MFR handling ends
			//

			try {
				//FT Handling strategy Update: Replication will be done for the provisional
				//responses as well, so need to replicate the client transaction on 
				//standby SAS only in case of INVITE request
				if(astTxn instanceof AseSipClientTransactionIImpl) {
					session.setTxn((DsSipClientTransaction)astTxn);
				}
				int i = session.recvResponse(response);
				switch(i) {
				case AseSipSession.NOOP:

					
					if(m_l.isDebugEnabled())
						m_l.debug("Doing nothing for response, call id = " +
								response.getCallId());
					// Do not send this response to container. No more
					// processing required, cleanup resources associated
					// What cleanup to do ??
					break;

				case AseSipSession.CONTINUE:
					// Pass this response to container
					sendToContainer = true;
					break;

				case AseSipSession.ACK_RESPONSE:
					// Send ACK for the response
					ackResponse = true;
					// Send the response to container
					sendToContainer = true;
					break;
				// UAT-792 : race Condition where ACK for $XX response is not
				// going.
				// This was happening because of the fact that SIP Session was
				// terminated.
				// When session is terminated InviteMsgHandler got
				// AseSipMessageHandlerException
				// and on its handling it sends NOOP.
				// There are other cases as well where we can get NOOP:
				// 1) When we don't find any outstanding request.
				// 2) When we need to proxy the response.
				case AseSipSession.ACK_RESPONSE_ONLY:
					// Send ACK for the response
					ackResponse = true;
					break;
					
				default:
					m_l.error("session.recvResponse() return code [" + i +
							"], call id [" + response.getCallId() +
							"], session id [" + session.getId() +
					"] is not handled");
				}
			} catch(AseStrayMessageException exp) {
				m_l.error("passing response [method = " + response.getMethod()
						+ ", call id = " + response.getCallId() +
						"] to default handler", exp);
				m_defaultHandler.handleStrayResponse(response);
			} catch(AseSessionInvalidException exp) {
				m_l.error("response [method = " + response.getMethod()
						+ ", call id = " + response.getCallId() +
						"] received for invalid session", exp);

				// Acknowledge if this is final response to INVITE
				//bug# BPInd09232
				if(DsSipConstants.INVITE == response.getDsResponse().getMethodID()
						&& (response.getStatus() >= 200)) {
					ackResponse = true;
				}
			} catch(AseDialogInvalidException exp) {
				m_l.error("response [method = " + response.getMethod()
						+ ", call id = " + response.getCallId() +
						"] received in invalid dialog state", exp);

				// Acknowledge if this is final response to INVITE
				//bug# BPInd09232
				if(DsSipConstants.INVITE == response.getDsResponse().getMethodID()
						&& (response.getStatus() >= 200)) {
					ackResponse = true;
				}
			} catch(IllegalStateException exp) {
				m_l.error("response [method = " + response.getMethod()
						+ ", call id = " + response.getCallId() +
						"] received in illegal state", exp);

				// Acknowledge if this is final response to INVITE
				//bug# BPInd09232
				if(DsSipConstants.INVITE == response.getDsResponse().getMethodID()
						&& (response.getStatus() >= 200)) {
					ackResponse = true;
				}
			} catch (Rel100Exception exp) {
				m_l.error("response [method = " + response.getMethod()
						+ ", call id = " + response.getCallId() +
						"] is an unexpected response", exp);
			} catch (AseSubscriptionInvalidException e) {
				m_l.error("response [method = " + response.getMethod()
						+ ", call id = " + response.getCallId() +
						"] with illegal subscription information", e);
			}

			// Acknowledge response, if required
			if(ackResponse) {
				if(m_l.isDebugEnabled())
					m_l.debug("Going to send ACK for response, call id = " +
							response.getCallId());
				try {
					//BpInd 18330
					DsSipAckMessage ackMessage = new DsSipAckMessage(dsRes,null,null);
					DsSipRequest request = ((AseSipServletRequest)response.getRequest()).getDsRequest();
					if(m_l.isDebugEnabled()) {
						m_l.debug("the error intial request is: "+request);
					}
					if(request.getHeader( new DsByteString("Accept-Contact"))!=null)
					{
						if(m_l.isDebugEnabled()) {
							m_l.debug("Header Accept present");
						}
						ackMessage.addHeader(request.getHeader( new DsByteString("Accept-Contact")),false);
					}

					if(request.getHeader( new DsByteString("Reject-Contact"))!=null)
					{
						ackMessage.addHeader(request.getHeader( new DsByteString("Reject-Contact")),false);
					}

					if(request.getHeader( new DsByteString("Request-Disposition"))!=null)
					{
						ackMessage.addHeader(request.getHeader( new DsByteString("Request-Disposition")),false);
					}

					response.getClientTxn().ack(ackMessage);
				} catch(DsException exp) {
					// If this is 408 transaction, just do the info level logging
					if(response.getStatus() == 408) {
						if(m_l.isInfoEnabled())
							m_l.info("ACK could not be sent for 408 response, call id = " + response.getCallId());
					} else {
						m_l.error("sending ack, call id = " +
								response.getCallId(), exp);
					}
				} catch(UnknownHostException exp) {
					m_l.error("should never come here, call id = " +
							response.getCallId(), exp);
				} catch(IOException exp) {
					m_l.error("should never come here, call id = " +
							response.getCallId(), exp);
				}
			}

			// Send to container, if required
			if(sendToContainer) {
				if(m_l.isDebugEnabled()) {
					m_l.debug("Sending response [call id = " +
						response.getCallId() + "] to container");
				}
				AseMessage aseMsg = new AseMessage(response,response.getMessagePriority());
				AseApplicationSession appSession =
					(AseApplicationSession)response.getApplicationSession();
				//aseMsg.setWorkQueue(appSession.getIc().getWorkQueue());
				m_connector.sendToContainer(aseMsg);
			}

		} finally{
			try {
				lockedSession.release();
			} catch(AseLockException exp) {
				m_l.fatal("SESSION LOCK RELEASE FAILURE", exp);
			}
		}
		if(m_l.isDebugEnabled()) {
			m_l.debug("handleResponse(AseSipServletResponse):exit");
		}
		return;
	}

	/**
	 * This method is called by stack server transaction timeout listener method in
	 * <code>AseSipServerTransactionListener</code> for further processing of a SIP
	 * ACK timeout or transaction timeout notification received from stack. It creates
	 * a new <code>AseEvent</code> object with:
	 * - <code>SipErrorEvent</code> object with associated request and response in case
	 *   of ACK timeout, or
	 * - associated request object in case of transaction timeout.
	 *
	 * It passes this <code>AseEvent</code> to associated session. It then sends error
	 * event to container, if asked by session to do so.
	 *
	 * @param transaction SIP server transaction which timed out
	 */
	public void handleTimeout(	AseSipTransaction	transaction) {
		if(m_l.isDebugEnabled()) {
			m_l.debug("handleTimeout(AseSipTransaction):enter");
		}

		AseSipServletRequest request = transaction.getAseSipRequest();
		AseSipServletResponse response = transaction.getAseSipResponse();

		// If default proxy transaction, do nothing
		if(transaction.isDefaultProxy()) {
			if(m_l.isInfoEnabled())
				m_l.info("Timeout for default proxied call id = " +
						request.getCallId());

			if(m_l.isDebugEnabled()) {
				m_l.debug("handleTimeout(AseSipTransaction):exit");
			}
			return;
		}

		AseSipSession session = request.getAseSipSession();
		if(session == null) {
			if(m_l.isInfoEnabled())
				m_l.info("Timeout for call id = " + request.getCallId());

			if(m_l.isDebugEnabled()) {
				m_l.debug("handleTimeout(AseSipTransaction):exit");
			}
			return;
		}

		AseEvent aseEvt = null;

		// Check if it is an ACK timeout and server transaction timeout
		//Or Check is applied as we are now not replicating whole response rather only
		//its response class. The case 0r check will hit when FT happens after 200 OK
		
		//TODO: Not doing it rite now and will do when FT testing happens
		if( (response != null) && (response.getStatus() >= 200)) {
			// increment the ACT timedout counter
			AseMeasurementUtil.counterAckTimedout.increment();


			// ACK timeout. Create AseEvent object with new SipErrorEvent.
			m_l.error("ACK timed out for call id = " + request.getCallId());

			if(m_l.isDebugEnabled()) {
				m_l.debug("Creating AseEvent with SipErrorEvent");
			}
			aseEvt = new AseEvent(	session,
					Constants.EVENT_SIP_ACK_ERROR,
					new SipErrorEvent(request, response));
		} else {
			// Server transaction timeout. Create AseEvent object with request.
			m_l.error("Server transaction timed out for call id = " +
					request.getCallId() +
					", request method = " + request.getMethod()); 

			if(m_l.isDebugEnabled()) {
				m_l.debug("Creating AseEvent with request");
			}
			aseEvt = new AseEvent(	session,
					Constants.EVENT_SERVER_TXN_TIMEOUT,
					request);
		}

		try {
			session.acquire();
		} catch(AseLockException exp) {
			m_l.fatal("SESSION LOCK ACQUIRE FAILURE", exp);
			if(m_l.isDebugEnabled()) {
				m_l.debug("handleTimeout(AseSipTransaction):exit");
			}
			return;
		}

		// Sending AseMessage to session
		if(m_l.isDebugEnabled()) {
			m_l.debug("Sending AseEvent to session");
		}
		try {
			int i = session.recvEvent(aseEvt);
			switch(i) {
			case AseSipSession.NOOP:
				if(m_l.isDebugEnabled()) {
					m_l.debug("SIP session return code: NOOP");
				}

				// Do not send this event to container. No more
				// processing required, cleanup resources associated
				// what cleanup to do ??
				break;

			case AseSipSession.CONTINUE:
				if(m_l.isDebugEnabled()) {
					m_l.debug("SIP session return code: CONTINUE");
				}

				// Pass this event to container
				if (m_l.isInfoEnabled()) {
					m_l.info("Send timeout event to container");
				}
				AseMessage aseMsg = new AseMessage(aseEvt, session);
				m_connector.sendToContainer(aseMsg);
				break;
			}
		} catch(AseSessionInvalidException exp) {
			m_l.error("Invalid session for call id = " + request.getCallId() +
					", request method = " + request.getMethod() +
					", session id = " + session.getId());
		} catch (AseDialogInvalidException exp) {
			m_l.error("Invalid session for call id = " + request.getCallId() +
					", request method = " + request.getMethod() +
					", session id = " + session.getId());
		} finally {

			try {
				session.release();
			} catch(AseLockException exp) {
				m_l.fatal("SESSION LOCK RELEASE FAILURE", exp);
			}
		}
		if(m_l.isDebugEnabled()) {
			m_l.debug("handleTimeout(AseSipTransaction):exit");
		}
	}

	/**
	 * This method is called by <code>AseSipConnector</code> for processing of
	 * a SIP request received from container. After processing, the request
	 * is handed over to stack for transmission on to the network.
	 * ACK and CANCEL requests are handle differently in the way that no new
	 * transaction is created for them but they are sent on existing
	 * associated transaction.
	 *
	 * - Creates <code>AseSipClientTransactionImpl</code> object for
	 *   non-INVITE and <code>AseSipClientTransactionIImpl</code> object for
	 *   INVITE requests.
	 *
	 * - Associate transaction with request and session with transaction
	 *
	 * - Notify session by calling <code>AseSipSession.requestPreSend()</code>
	 *
	 * - Start transaction
	 *
	 * - Notify session by calling <code>AseSipSession.requestPostSend()</code>
	 *
	 * @param request SIP request object passed by SIP connector
	 */
	public void sendRequest(AseSipServletRequest request) {
		if(m_l.isDebugEnabled()) {
			m_l.debug("sendRequest(AseSipServletRequest):enter...");
		}

		DsSipClientTransaction dsTxn = request.getClientTxn();
		AseSipTransaction txn = (AseSipTransaction)dsTxn;
		DsSipRequest dsReq = request.getDsRequest();
		AseSipSession session = request.getAseSipSession();
		selectiveLogger.logOutgoingRequest(request );
		InetAddress addr=null;
		
		SipURI outboundURI = (SipURI)request.getApplicationSession().getAttribute(Constants.IF_FOR_RECEIVING_ORIG_REQUEST);
		try {
			if(isMultihomingEnabled && outboundURI!=null ){//&& !session.isOutboundURISet()){
				addr = InetAddress.getByName(outboundURI.getHost());
				int port = outboundURI.getPort();
				if(port == -1){
					int index = m_connector.getIPAddressList().indexOf(addr.getHostAddress());
					port = m_connector.getPortList().get(index);
				}
				dsReq.getBindingInfo().setLocalAddress(addr);
				dsReq.getBindingInfo().setlocalViaPort(port);
				session.setOutboundURIFlag(true);
				
				if(m_l.isDebugEnabled()){
					m_l.debug("Setting outbound Address as " + addr + " and port as " + port);
				}
				
				/**
				 * update contact hdr
				 */
				String dsDisplayName = AseStrings.BLANK_STRING;
				if(request.getFrom().toString().indexOf(AseStrings.AT)!=-1) {
					
					dsDisplayName = request.getFrom().toString().substring(request.getFrom().toString().indexOf(AseStrings.COLON)+1,request.getFrom().toString().indexOf(AseStrings.AT));
	
					if (dsDisplayName == AseStrings.BLANK_STRING)
					{
						dsDisplayName = AseStrings.UNKNOWN_DISPLAY_NAME;
					}
				}
				com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader contact = null;

				DsSipContactHeader contactHdr = (DsSipContactHeader)dsReq.getContactHeader();

				DsByteString contactStr =null;
				if (!AseStrings.BLANK_STRING.equals(dsDisplayName)) {
					 contactStr = new DsByteString("sip:"
							+ dsDisplayName + AseStrings.AT
							+ addr.getHostAddress() + AseStrings.COLON + port);
				}else{
					 contactStr = new DsByteString("sip:"
							+ addr.getHostAddress() + AseStrings.COLON + port);
				}

				if(contactHdr!=null &&((DsSipURL)(contactHdr.getURI())).getParameters()!= null){
					contactStr = new DsByteString(contactStr.toString()+ ((DsSipURL)(contactHdr.getURI())).getParameters().toString()) ;
				}
				if(m_l.isDebugEnabled()) {
					m_l.debug("Update Contact Str  :: " + contactStr);
				}
				DsSipURL contactURL = new DsSipURL(contactStr);  
				contact = new com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader(contactURL);
				dsReq.updateHeader(contact);	
			}
		}catch (DsException e) {
			m_l.error("Error in updating contact header "+e.getMessage());
		} catch (UnknownHostException e1) {
			m_l.error("Error is setting outboubd URI " + e1.getMessage(),e1);
			e1.printStackTrace();
		}		
		
		if(callStatsProcessorObj.isTrafficMonitoringEnabled()){
			if(request.getMethod().equals(AseStrings.INVITE)) {
				callStatsProcessorObj.reportNewCall((AseApplicationSession)request.getApplicationSession());
			}
		}

		if(callStatsProcessorObj.isTrafficMonitoringEnabled()){
			if((request.getMethod().equals(AseStrings.BYE)) || 
					(request.getMethod().equals(AseStrings.CANCEL) ) ){
				callStatsProcessorObj.reportInProgressCall(false,(AseApplicationSession)request.getApplicationSession());
			}
		}
		
		//Fix for LEV-1889 . Condition added to report CallHoldTime only when 
		// BYE is send to the Orig Party.
		if(callStatsProcessorObj.isTrafficMonitoringEnabled()){
			if(request.getMethod().equals(AseStrings.BYE)
					&& session != null && session.getOrigRequest()!=null && session.getOrigRequest().isIncoming()){
				callStatsProcessorObj.reportCallHoldTime(false,(AseApplicationSession)request.getApplicationSession());
			}
		}
		
		//Set priority flag into outgoing message
		dsReq.setMessagePriority(request.getMessagePriority());

		if(m_l.isInfoEnabled())
			m_l.info(request.getMethod() + " request with session id = " +
					session.getId() + ", call id = " + request.getCallId());

		try {
			session.acquire();
		} catch(AseLockException exp) {
			m_l.fatal("SESSION LOCK ACQUIRE FAILURE", exp);
			if(m_l.isDebugEnabled()) {
				m_l.debug("sendRequest(AseSipServletRequest):exit");
			}
			return;
		}

		try{
			boolean isProxy = false;
			if(session.getRole() == AseSipSession.ROLE_PROXY) {
				if(m_l.isInfoEnabled())
					m_l.info("Proxy session id = " + session.getId()
							+ ", call id = " + request.getCallId());

				isProxy = true;
				dsReq.setBindingInfo(new DsBindingInfo());
			} // role = proxy


			//JSR 289.34 
			try {
				SipURI uri = ((AseSipSession)request.getSession()).getOutboundInterface();
				AseProxyImpl proxyImpl=null;
				proxyImpl = ((AseProxyImpl)request.getProxy(false));
				String hostIP = null;
				if(uri != null){
					hostIP = uri.getHost();
				}

				boolean check = false;
				//check outbound for proxies : setting local binding adress in binding info
				if(proxyImpl != null){
					AseProxyBranch branch = ((AseProxyImpl)request.getProxy()).getBranches(request.getRequestURI());
					if(branch != null){
						if(branch.getOutboundInterface() != null){
							dsReq.getBindingInfo().setLocalAddress(InetAddress.getByName(branch.getOutboundInterface().getHost()));
							check=true;
						}else if(proxyImpl.getProxyOutboundInterface() != null){
							dsReq.getBindingInfo().setLocalAddress(InetAddress.getByName(proxyImpl.getProxyOutboundInterface().getHost()));
							check=true;
						}
					}
				}
				if(uri != null && check == false){
					addr=InetAddress.getByName(uri.getHost());
					dsReq.getBindingInfo().setLocalAddress(addr);
					dsReq.getBindingInfo().setlocalViaPort(uri.getPort());
				}
				//updating contact header for Non Proxy Application
				if(uri != null && session.getRole() != AseSipSession.ROLE_PROXY){
					try {
						String dsDisplayName = AseStrings.BLANK_STRING;
						dsDisplayName = request.getFrom().toString().substring(request.getFrom().toString().indexOf(AseStrings.COLON)+1,request.getFrom().toString().indexOf(AseStrings.AT));

						if (dsDisplayName == AseStrings.BLANK_STRING)
						{
							dsDisplayName = AseStrings.UNKNOWN_DISPLAY_NAME;
						}
						com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader contact = null;

						DsSipContactHeader contactHdr = (DsSipContactHeader)dsReq.getContactHeader();

						DsByteString contactStr = new DsByteString("sip:" + dsDisplayName +
								AseStrings.AT + adjustIPFormat(hostIP) + AseStrings.COLON + uri.getPort());

						if(((DsSipURL)(contactHdr.getURI())).getParameters()!= null){
							contactStr = new DsByteString(contactStr.toString()+ ((DsSipURL)(contactHdr.getURI())).getParameters().toString()) ;
						}
						if(m_l.isDebugEnabled()) {
							m_l.debug("Check Contact Str  :: " + contactStr);
						}
						DsSipURL contactURL = new DsSipURL(contactStr);  
						contact = new com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader(contactURL);
						dsReq.updateHeader(contact);	
					} catch (Exception e) {
						m_l.error("Error in updating contact header "+e.getMessage());
					}
				}

			}catch (Exception e) {
				m_l.error("Error in setting binding info "+e.getMessage());
			}

			// ACK handling
			if(dsReq.getMethodID() == DsSipConstants.ACK) {
				// Notify session for state updation
				if (m_l.isInfoEnabled()) {
					m_l.info("Notifying session before sending ACK");
				}
				session.requestPreSend(request);

				//Response time measurement
				if(request.getMessagePriority()) {
					//m_nsepOcm.measureResponseTime(AseSipOverloadManager.MESSAGE_OUT, request);
				} else {
					//m_ocm.measureResponseTime(AseSipOverloadManager.MESSAGE_OUT, request);
				}

				if(isProxy && (dsTxn == null)) {
					if(m_l.isDebugEnabled()) {
						m_l.debug("Proxying 2xx ACK");
					}
					try {
						DsSipTransactionManager.getConnection(dsReq).send(dsReq);
						if(m_sipMsgLogger != null) {

							m_sipMsgLogger.logRequest(
									DsMessageLoggingInterface.REASON_REGULAR,
									DsMessageLoggingInterface.DIRECTION_OUT,
									dsReq);
						}

						//Increment the counter for this outgoing request for this ACK
						AseMeasurementUtil.incrementRequestOut(DsSipConstants.ACK);
						if(request.getMessagePriority()) {
							AseMeasurementUtil.incrementPriorityMessageCount();
						}
					} catch(Exception exp) {
						m_l.error("proxying ACK retransmission for " + " request with session id = " +
								session.getId() + ", call id = " + request.getCallId(), exp);
					}
				} else {
					try {
						if(dsTxn == null) {
							AseSipTransaction servTxn = (AseSipTransaction)request.getServerTxn();
							if(servTxn != null) {
								dsTxn = servTxn.getAseSipRequest().getClientTxn();
							}
						}

						if(dsTxn != null) {
							if(m_l.isDebugEnabled()) {
								m_l.debug("Sending ACK on transaction");
							}
							dsTxn.ack((DsSipAckMessage)dsReq);
						} else {
							// Add Via first
							AseSipViaHeaderHandler.addViaHeader(
									request,
									new DsByteString(m_connector.getIPAddress()),
									m_connector.getPort(),
									DsSipTransportType.UDP,
									false);
							if(m_l.isDebugEnabled()) {
								m_l.debug("Sending ACK on connection");
							}
							DsSipTransactionManager.getConnection(dsReq).send(dsReq);
							if(m_sipMsgLogger != null) {

								m_sipMsgLogger.logRequest(
										DsMessageLoggingInterface.REASON_REGULAR,
										DsMessageLoggingInterface.DIRECTION_OUT,
										dsReq);
							}
						}

						//Increment the counter for this outgoing request for this ACK
						AseMeasurementUtil.incrementRequestOut(DsSipConstants.ACK);
						if(m_engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL)) && AseNsepMessageHandler.getMessagePriority(request)) {
							AseMeasurementUtil.incrementPriorityMessageCount();
						}
					} catch(IOException exp) {
						m_l.error("sending ACK", exp);
					} catch(DsException exp) {
						m_l.error("sending ACK", exp);
					}
				}

				//Dump the body of the outgoing request to the call trace log
				traceMessage(request, false);

				// Notify session for state updation
				if (m_l.isInfoEnabled()) {
					m_l.info("Notifying session after sending request");
				}
				session.requestPostSend(request);

				if(m_l.isDebugEnabled()) {
					m_l.debug("sendRequest(AseSipServletRequest):exit");
				}
				return;
			}

			// CANCEL handling
			if(dsReq.getMethodID() == DsSipConstants.CANCEL) {
				// Notify session for state updation
				if (m_l.isInfoEnabled()) {
					m_l.info("Notifying session before sending request");
				}
				session.requestPreSend(request);

				//Response time measurement
				if(request.getMessagePriority()) {
				//	m_nsepOcm.measureResponseTime(AseSipOverloadManager.MESSAGE_OUT, request);
				} else {
				//	m_ocm.measureResponseTime(AseSipOverloadManager.MESSAGE_OUT, request);
				}

				// Check if the transaction is in cancellable state, if not set
				// this CANCEL request pending with this transaction
				if(txn.isCancellable()) {
					try {
						dsTxn.cancel((DsSipCancelMessage)dsReq);
						//Increment the counter for this outgoing request for this CANCEL
						AseMeasurementUtil.incrementRequestOut(DsSipConstants.CANCEL);
						if(m_engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL)) && AseNsepMessageHandler.getMessagePriority(request)) {
							AseMeasurementUtil.incrementPriorityMessageCount();
						}
					} catch(IOException exp) {
						m_l.error("sending CANCEL" +" for call id = " + request.getCallId(), exp);
					} catch(DsException exp) {
						m_l.error("sending CANCEL" +" for call id = " + request.getCallId(), exp);
					}
				} else {
					if (m_l.isInfoEnabled()) {
						m_l.info("Setting pending CANCEL");
					}
					txn.setCancelPending(request);
				}

				//Dump the body of the outgoing request to the call trace log
				traceMessage(request, false);


				// Notify session for state updation
				if (m_l.isInfoEnabled()) {
					m_l.info("Notifying session after sending request");
				}
				session.requestPostSend(request);

				if(m_l.isDebugEnabled()) {
					m_l.debug("sendRequest(AseSipServletRequest):exit");
				}
				return;
			}

			// Create client transaction object
			if(dsReq.getMethodID() == DsSipConstants.INVITE) {
				if(m_l.isDebugEnabled()) {
					m_l.debug("INVITE request received from container, creating AseSipClientTransactionIImpl");
				}
				try {
					txn = new AseSipClientTransactionIImpl(
							dsReq,
							AseSipClientTransportInfo.instance(),
							new AseSipClientTransactionListener(this, m_factory));
					//new AseSipClientTransactionListener(this, m_factory),
					//new DsSipTransactionParams());
				} catch(DsException exp) {
					m_l.error("creating INVITE client txn, call id = " +
							request.getCallId(), exp);
				}
			} else {
				if(m_l.isDebugEnabled()) {
					m_l.debug("non-INVITE request received from container, creating AseSipClientTransactionImpl");
				}
				try {
					txn = new AseSipClientTransactionImpl(
							dsReq,
							AseSipClientTransportInfo.instance(),
							new AseSipClientTransactionListener(this, m_factory));
					//new AseSipClientTransactionListener(this, m_factory),
					//new DsSipTransactionParams());
				} catch(DsException exp) {
					m_l.error("creating non-INVITE client txn, call id = " +
							request.getCallId(), exp);
				}
			}

			if(isProxy) {
				// set client transaction proxy mode to true
				
				if (m_l.isInfoEnabled()) {
					m_l.info("set client transaction proxy mode to true");
				}
				((DsSipTransaction)txn).setProxyServerMode(true);
				
				if(request.getServerTxn()!=null)
				 ((DsSipTransaction)request.getServerTxn()).setProxyServerMode(true);
				
				//m_transactionMgr.setProxyServerMode(true);
			}else{
				
				if (m_l.isInfoEnabled()) {
					m_l.info("set client transaction proxy mode to false");
				}
                ((DsSipTransaction)txn).setProxyServerMode(false);
				
				//((DsSipTransaction)request.getServerTxn()).setProxyServerMode(false);
				//m_transactionMgr.setProxyServerMode(false);
			}

			// Set client transaction into request
			request.setClientTxn((DsSipClientTransaction)txn);

			// Set SIP session into transaction
			txn.setSipSession(session);

			// Set request into transaction
			txn.setAseSipRequest(request);

			// Notify session for state updation
			if (m_l.isInfoEnabled()) {
				m_l.info("Notifying session before sending request");
			}
			session.requestPreSend(request);

			//Response time measurement
			if(request.getMessagePriority()) {
	//			m_nsepOcm.measureResponseTime(AseSipOverloadManager.MESSAGE_OUT, request);
			} else {
	//			m_ocm.measureResponseTime(AseSipOverloadManager.MESSAGE_OUT, request);
			}

			// Start the transaction
			AseSipServletResponse failResp = null;
			if (m_l.isInfoEnabled()) {
				m_l.info("Starting client transaction");
			}
			try {
				((DsSipClientTransaction)txn).start();
				//Increment the counter for this outgoing request for this Client Txn
				AseMeasurementUtil.incrementRequestOut(dsReq.getMethodID());
				if(m_engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL)) && AseNsepMessageHandler.getMessagePriority(request)) {
					AseMeasurementUtil.incrementPriorityMessageCount();
				}

				// Added for NSEP(Priority) call 
				if(request.getMessagePriority()) {
					AseMeasurementUtil.incrementPriorityRequestOut(dsReq.getMethodID());
				}
				//FT Handling strategy Update: Replication will be done for the provisional
				//responses as well, so need to replicate the client transaction on 
				//standby SAS only in case on INVITE request
				if(txn instanceof AseSipClientTransactionIImpl) {
					session.setTxn((DsSipClientTransaction)txn);
				}

			} catch(Exception exp) {
				m_l.error("starting client transaction, call id = " +
						request.getCallId(), exp);

				// In case of proxy, failure in sending request is treated as
				// receiving 503 (Service Unavailable) response
				if(isProxy) {
					try {
						failResp = (AseSipServletResponse)request.createResponse(503);
					} catch(Exception exp1) {
						m_l.error("creating 503 response, call id = " +
								request.getCallId(), exp1);
					}
				} else {
					try {
						failResp = (AseSipServletResponse)request.createResponse(500);
					} catch(Exception exp1) {
						m_l.error("creating 500 response, call id = " +
								request.getCallId(), exp1);
					}
				}
			}

			//Dump the body of the outgoing request to the call trace log
			traceMessage(request, false);


			// Notify session for state updation
			if (m_l.isInfoEnabled()) {
				m_l.info("Notifying session after sending request");
			}
			session.requestPostSend(request);
			// Sending response to container after release lock, so that handle
			// response locking is not migled.
			if(failResp != null) {
				if (m_l.isInfoEnabled()) {
					m_l.info("Sending error to SIL.handleResponse()");
				}
				// send error to container
				handleResponse(failResp);
			}

		}finally {
			try {
				session.release();
			} catch(AseLockException exp) {
				m_l.fatal("SESSION LOCK RELEASE FAILURE", exp);
			}
		}

		if(m_l.isDebugEnabled()) {
			m_l.debug("sendRequest(AseSipServletRequest):exit");
		}
	}

	/**
	 * This method is called by <code>AseSipConnector</code> for processing of
	 * a SIP response received from container. After processing, the response
	 * is handed over to stack for transmission on to network.
	 *
	 * - Notify associated <code>AseSipSession</code> before sending response
	 *
	 * - Send response by invoking method on transaction
	 *
	 * - Notify associated <code>AseSipSession</code> after sending response
	 *
	 * @param response SIP response object passed by SIP connector
	 */
	public void sendResponse(AseSipServletResponse response) {
		if(m_l.isDebugEnabled()) {
			m_l.debug("sendResponse(AseSipServletResponse):enter");
		}

		DsSipResponse dsResp = response.getDsResponse();
		//Set priority flag into outgoing message
		dsResp.setMessagePriority(response.getMessagePriority());
		//Also set priority flag in corresponding DsSipRequest object
		AseSipServletRequest sipReq = (AseSipServletRequest)response.getRequest();
		DsSipRequest dsReq = sipReq.getDsRequest();
		dsReq.setMessagePriority(response.getMessagePriority());
		AseSipSession session = response.getAseSipSession();
		selectiveLogger.logResponse(DsMessageLoggingInterface.DIRECTION_OUT, response);
		
		long currTime = System.currentTimeMillis();
		response.setTimestamp(currTime);
		
		InetAddress addr=null;
		SipURI outboundURI = (SipURI)response.getApplicationSession().getAttribute(Constants.IF_FOR_RECEIVING_ORIG_REQUEST);
		try {
			if(isMultihomingEnabled && outboundURI!=null && (response.getStatus()<299 || response.getStatus()>399)){//&&!session.isOutboundURISet()){
				addr=InetAddress.getByName(outboundURI.getHost());
				int port = outboundURI.getPort();
				if(port == -1){
					int index = m_connector.getIPAddressList().indexOf(addr.getHostAddress());
					port = m_connector.getPortList().get(index);
				}
				dsResp.getBindingInfo().setLocalAddress(addr);
				dsResp.getBindingInfo().setlocalViaPort(port);
				session.setOutboundURIFlag(true);
				if(m_l.isDebugEnabled()){
					m_l.debug("Setting outbound Address as " + addr + " and port as " + port);
				}
				
				/**
				 * update contact hdr
				 */
				String dsDisplayName = AseStrings.BLANK_STRING;
				if(response.getFrom().toString().indexOf(AseStrings.AT)!=-1) {
					dsDisplayName = response.getFrom().toString().substring(response.getFrom().toString().indexOf(AseStrings.COLON)+1,response.getFrom().toString().indexOf(AseStrings.AT));
	
					if (dsDisplayName == AseStrings.BLANK_STRING)
					{
						dsDisplayName = AseStrings.UNKNOWN_DISPLAY_NAME;
					}
				}
				com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader contact = null;

				DsSipContactHeader contactHdr = (DsSipContactHeader)dsResp.getContactHeader();

				DsByteString contactStr =null;
				if (!AseStrings.BLANK_STRING.equals(dsDisplayName)) {
					 contactStr = new DsByteString("sip:"
							+ dsDisplayName + AseStrings.AT
							+ addr.getHostAddress() + AseStrings.COLON + port);
				}else{
					 contactStr = new DsByteString("sip:"
							+ addr.getHostAddress() + AseStrings.COLON + port);
				}

				if (contactHdr != null &&contactHdr.getURI() instanceof DsSipURL) {
					if (((DsSipURL) (contactHdr.getURI())).getParameters() != null) {
						contactStr = new DsByteString(contactStr.toString()
								+ ((DsSipURL) (contactHdr.getURI()))
										.getParameters().toString());
					}
					if (m_l.isDebugEnabled()) {
						m_l.debug("Update Contact Str  :: " + contactStr);
					}
					DsSipURL contactURL = new DsSipURL(contactStr);
					contact = new com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader(contactURL);
				} else if (contactHdr != null &&contactHdr.getURI() instanceof DsTelURL) {
//					if (contactHdr != null
//							&& ((DsTelURL) (contactHdr.getURI()))
//									.getParameters() != null) {
//						contactStr = new DsByteString(contactStr.toString()
//								+ ((DsTelURL) (contactHdr.getURI()))
//										.getParameters().toString());
//					}
					if (m_l.isDebugEnabled()) {
						m_l.debug("Update Contact Str  :: " + contactStr);
					}
					DsTelURL contactURL = new DsTelURL(contactStr);
					contact = new com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader(contactURL);
				}else{
					DsSipURL contactURL = new DsSipURL(contactStr);
					contact = new com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader(contactURL);
				}
				
				
				dsResp.updateHeader(contact);	
			}
		} catch (UnknownHostException e1) {
			m_l.error("Error is setting outboubd URI " + e1.getMessage(),e1);
			e1.printStackTrace();
		}catch (DsException e) {
			m_l.error("Error in updating contact header "+e.getMessage());
		}

		// Following is done to add any delay in sending 100 response (ISC Reqmnt)
		if(dsResp.getStatusCode() == 100) {
			// Now set delay in response's Timestamp header
			DsSipTimestampHeader tsh = null;
			try {
				tsh = (DsSipTimestampHeader)dsResp.getHeaderValidate(DsSipResponse.TIMESTAMP);
			} catch(DsSipParserException exp) {
				m_l.error("Parsing Timestamp header", exp);
			} catch(DsSipParserListenerException exp) {
				m_l.error("Parsing Timestamp header", exp);
			}
			if(tsh != null) {
				long reqTime = ((AseSipServletRequest)response.getBaseRequest()).getTimestamp();
				float delay;
				if(reqTime > 0) {
					delay = currTime - reqTime;
					delay /= 1000.0; // convert delay to seconds
					if(delay > 0.0) {
						tsh.setDelay(delay);
					}
				}
			}
		}

		if(m_l.isInfoEnabled())
			m_l.info(response.getMethod() + " response with session id = " +
					session.getId() + ", call id = " + response.getCallId());

		try {
			session.acquire();
		} catch(AseLockException exp) {
			m_l.fatal("SESSION LOCK ACQUIRE FAILURE", exp);
			if(m_l.isDebugEnabled()) {
				m_l.debug("sendResponse(AseSipServletResponse):exit");
			}
			return;
		}

		try {
			boolean isProxy = false;
			if(session.getRole() == AseSipSession.ROLE_PROXY) {
				if(m_l.isInfoEnabled())
					m_l.info("Proxy session id = " + session.getId()
							+ ", call id = " + response.getCallId());

				isProxy = true;

				// For responses to subsequent requests, remove top Via
				if(!response.getRequest().isInitial()) {
					// Remove top Via header, if it matches to SAS
					AseSipViaHeaderHandler.removeTopViaHeader(response);

					// If no more Via, drop it and return
					if(null == dsResp.getViaHeaders()) {
						if (m_l.isInfoEnabled()) {
							m_l.info("No more Via header for proxy response");
						}
						if(m_l.isDebugEnabled()) {
							m_l.debug("sendResponse(AseSipServletResponse):exit");
						}
						return;
					}
				} // if(!response.getRequest().isInitial())

				// Clear binding info
				if(m_l.isDebugEnabled()) {
					m_l.debug("Proxy: clearing binding info for outgoing response");
				}
				dsResp.setBindingInfo(new DsBindingInfo());
			} // if proxy

			//ipv6 Start 
			try {
				DsSipResponse dsRes = response.getDsResponse();
				SipURI uri = ((AseSipSession)response.getSession()).getOutboundInterface();
				String hostIP = null;
				if(uri != null){
					hostIP = uri.getHost();
				}
				//updating contact header for Non Proxy Application
				if((uri != null && session.getRole() != AseSipSession.ROLE_PROXY) ||(uri != null && isProxy && response.getStatus()== 100) ){
					String dsDisplayName = AseStrings.BLANK_STRING;
					dsDisplayName = response.getFrom().toString().substring(response.getFrom().toString().indexOf(AseStrings.COLON)+1,response.getFrom().toString().indexOf(AseStrings.AT));

					if (dsDisplayName == AseStrings.BLANK_STRING)
					{
						dsDisplayName = "unknown";
					}
					com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader contact = null;
					DsSipContactHeader contactHdr = (DsSipContactHeader)dsRes.getContactHeader();

					DsByteString contactStr = new DsByteString("sip:" + dsDisplayName +
							AseStrings.AT + adjustIPFormat(hostIP) + AseStrings.COLON + uri.getPort());

					if(((DsSipURL)(contactHdr.getURI())).getParameters()!= null){
						contactStr = new DsByteString(contactStr.toString()+ ((DsSipURL)(contactHdr.getURI())).getParameters().toString()) ;
					}
					DsSipURL contactURL = new DsSipURL(contactStr);  
					contact = new com.dynamicsoft.DsLibs.DsSipObject.DsSipContactHeader(contactURL);
					dsRes.updateHeader(contact);	
				}
			} catch (Exception e) {
				m_l.error("Error in updating contact header "+e.getMessage());
			}


			//end

			//
			// Notify session for state updation
			if (m_l.isInfoEnabled()) {
				m_l.info("Notifying session before sending response");
			}
			session.responsePreSend(response);

			//Response time measurement
			if(response.getMessagePriority()) {
			//	m_nsepOcm.measureResponseTime(AseSipOverloadManager.MESSAGE_OUT, response);
			} else {	
			//	m_ocm.measureResponseTime(AseSipOverloadManager.MESSAGE_OUT, response);
			}

			// Send response to stack
			if (m_l.isInfoEnabled()) {
				m_l.info("Sending response to stack");
			}

			DsSipServerTransaction dsTxn =
				((AseSipServletRequest)response.getRequest()).getServerTxn();
			if( isProxy
					&& ((dsTxn == null)
							|| (dsTxn.getState() == DsSipStateMachineDefinitions.DS_TERMINATED))
							&& (dsResp.getResponseClass() == 2)
							&& (dsResp.getMethodID() == DsSipConstants.INVITE)) {
				// 2xx (INVITE) retransmission or MFR for a proxy operation
				try {
					DsSipTransactionManager.getConnection(dsResp).send(dsResp);
					if(m_sipMsgLogger != null) {

						m_sipMsgLogger.logResponse(
								DsMessageLoggingInterface.REASON_REGULAR,
								DsMessageLoggingInterface.DIRECTION_OUT,
								dsResp);

					}
				} catch(Exception exp) {
					m_l.error("proxying 2xx (INVITE) retransmission or MFR"
							+"[call id = " + response.getCallId() + AseStrings.SQUARE_BRACKET_CLOSE, exp);
				}
			} else {
				try {
					dsTxn.sendResponse(dsResp);

					//Increment the counter for this outgoing response for this Server Txn.
					AseMeasurementUtil.incrementResponseOut(response.getStatus());
					if(m_engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL)) && AseNsepMessageHandler.getMessagePriority(response)) {
						AseMeasurementUtil.incrementPriorityMessageCount();
					}
					if((response.getMessagePriority()) 
							&& (dsResp.getResponseClass() > 2) 
							&& (dsResp.getMethodID() == DsSipConstants.INVITE)) {
						AseMeasurementUtil.incrementUnsuccessfulPrioritySessions();
					}
				} catch(IOException exp) {
					m_l.error("sending " + response.getMethod() +
							" response with session id = " +
							session.getId() + ", call id = " +
							response.getCallId() + " to stack", exp);
				} catch(DsException exp) {
					m_l.error("sending " + response.getMethod() +
							" response with session id = " +
							session.getId() + ", call id = " +
							response.getCallId() + " to stack", exp);
				}

				((AseSipTransaction)dsTxn).setAseSipResponse(response);
				session.setTransResponse(response);
			}

			AseSipServletRequest request = (AseSipServletRequest)response.getRequest();
			if((199 < response.getStatus()) &&
					(true == request.isInProgress()) ){
				if(m_engine.isCallPriorityEnabled()) {
					//m_connector.getOverloadManager(true).decrementInPrgsCalls(request.getInitialPriorityStatus());
				} else {
					//m_connector.getOverloadManager().decrementInPrgsCalls();
				}
				request.resetInProgress();
			}

			// Dump the body of the outgoing response to the call trace log
			traceMessage(response, false);

			// Notify session for state updation
			if (m_l.isInfoEnabled()) {
				m_l.info("Notifying session after sending response");
			}
			session.responsePostSend(response);
		} finally {
			try {
				session.release();
			} catch(AseLockException exp) {
				m_l.fatal("SESSION LOCK RELEASE FAILURE", exp);
			}
		}
		if(m_l.isDebugEnabled()) {
			m_l.debug("sendResponse(AseSipServletResponse):exit");
		}
	}

	private String adjustIPFormat(String ip){
		if( ip.lastIndexOf(AseStrings.COLON) == -1 )
			return ip;
		if( !ip.startsWith(AseStrings.SQUARE_BRACKET_OPEN) && !ip.endsWith(AseStrings.SQUARE_BRACKET_CLOSE) )
			return AseStrings.SQUARE_BRACKET_OPEN+ip+AseStrings.SQUARE_BRACKET_CLOSE;
		return ip;
	}

	/**
	 * Sets reference to default handler.
	 * @param defaultHandler reference to default handler
	 */
	public void setDefaultHandler(AseSipDefaultHandler defaultHandler) {
		m_defaultHandler = defaultHandler;
	}

	/**
	 * Set message loggin interface in SIL.
	 */
	public static void setMessageLoggingInterface(AseMessageLoggingInterface msgLogger) {
		m_sipMsgLogger = msgLogger;
	}

	private boolean isPrepaidCall(AseSipServletRequest request){
		//Pattern p = Pattern.compile(prepaidCallPatternStr);
		
		SipURI sipReqUri = null;
		String sipReqUser = null;
		URI reqUri = request.getRequestURI();
		if(reqUri != null && reqUri.isSipURI()){
			sipReqUri = (SipURI) reqUri;
			sipReqUser = sipReqUri.getUser();
			if (sipReqUser != null){
				Matcher m = prepaidPattern.matcher(sipReqUser);
				return m.matches();
			}else {
				m_l.error("SIP URI without SIP Request User");
				return false;
			}
		}else{
			m_l.error("TEL URI not supported");
			return false;
		}
		//\n(dialogue-id)(\\s*):
	}
	/////////////////////////// private attributes //////////////////////////// 

	private SelectiveMessageLogger  selectiveLogger     = SelectiveMessageLogger.getInstance();

	private AseEngine 				m_engine 			= null;

	private AseSipConnector			m_connector			= null;

	private AseContainer			m_container			= null;

	private AseDialogManager		m_dialogMgr 		= null;

	private AseSipSubscriptionManager m_subscriptionManager = null;

	private AseConnectorSipFactory	m_factory			= null;

	private AseSipDefaultHandler	m_defaultHandler	= null;

	private DsSipTransactionManager	m_transactionMgr	= null;

	private DsSipTransportLayer		m_transportLayer	= null;

	private ConfigRepository		m_configRepository	= null;

	private static AseMessageLoggingInterface m_sipMsgLogger	= null;

	private int 					m_overloadRejectionCode	= 503;

	private int 					m_retryAfterDelay	= 120;

	private AseSipOverloadManager	m_ocm				= null;

	private ArrayList				m_listenPoints		= new ArrayList();

	private boolean					m_enableTLS			= false;

	private DsSSLContext			m_sslContext		= null;

	private String					m_keyStorePassword	= null;

	private AseSipOverloadManager	m_nsepOcm			= null;

	private int 					m_nsepOcmRejectionCode = 503;

	private AseSubsystem subSystem;

	private ControlManager controlMgr;

	private static Logger m_l = Logger.getLogger(
			AseStackInterfaceLayer.class.getName());




	private static StringManager _strings = StringManager.getInstance(AseStackInterfaceLayer.class.getPackage());


	private class ListenPoint {
		ListenPoint(String addr, int port, int protocol) {
			m_lpPort = port;
			m_lpProtocol = protocol;
			m_lpAddr = addr;
		}

		public int getPort() {
			return m_lpPort;
		}

		public int getProtocol() {
			return m_lpProtocol;
		}

		public String getAddress() {
			return m_lpAddr;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer("LISTEN-POINT = ");
			sb.append(m_lpAddr).append(AseStrings.COLON).append(m_lpPort).append(AseStrings.COMMA);
			switch(m_lpProtocol) {
			case DsSipTransportType.UDP:
				sb.append("UDP");
				break;

			case DsSipTransportType.TCP:
				sb.append("TCP");
				break;

			case DsSipTransportType.TLS:
				sb.append("TLS");
				break;

			default:
				sb.append("INVALID");
				break;

			}

			return sb.toString();
		}

		private int m_lpPort;
		private int m_lpProtocol;
		private String m_lpAddr;
	} // class ListenPoint
	
	
	private synchronized static String addPrepaidPattern(String[] args){
		String[] patterns = args[0].split(AseStrings.COMMA);
		if(m_l.isDebugEnabled()) {
			m_l.debug("Prepaid Patterns needs to be added " + patterns);
		}
		//This is done to avoid duplicates
		for (String pattern: patterns){
			prepaidPatterns.add(pattern);
		}
		createPattern();
		if(m_l.isDebugEnabled()) {
			m_l.debug("Prepaid Call Pattern Re-Configured " + prepaidCallPatternStr);
		}

		return "Pattern(s) added successfully";
	}
	private static void createPattern(){
		Object [] uniquePatterns = prepaidPatterns.toArray();
		prepaidCallPatternStr = new String();
		for (int i = 0; i < uniquePatterns.length; i++){
			if (i == 0){
				prepaidCallPatternStr = prepaidCallPatternStr + "(" + uniquePatterns[i] + "|";
			}else if (i == (uniquePatterns.length - 1)){
				prepaidCallPatternStr = prepaidCallPatternStr + uniquePatterns[i] + ").*";
			}else{
				prepaidCallPatternStr = prepaidCallPatternStr + uniquePatterns[i] + "|";
			}
		}
		prepaidPattern = Pattern.compile(prepaidCallPatternStr);
	}
	private synchronized static String removePrepaidPattern(String[] args){
		String[] patterns = args[0].split(AseStrings.COMMA);
		if(m_l.isDebugEnabled()) {
			m_l.debug("Prepaid Patterns needs to be removed " + patterns);
		}
		//This is done to avoid duplicates
		for (String pattern: patterns){
			prepaidPatterns.remove(pattern);
		}
		createPattern();
		return "Pattern(s) removed successfully";
	}
	private synchronized static String getPrepaidPattern(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\r\nPrepaid Pattern :");
		buffer.append(prepaidCallPatternStr);
		return buffer.toString();
	}
	class PrepaidPatternHandler implements CommandHandler{
		
		public String execute(
			String command,
			String[] args,
			InputStream in,
			OutputStream out)
			throws CommandFailedException {
			
			if (command.equals(Constants.ADD_PREPAID_PATTERN)) {
				return addPrepaidPattern(args);
			} else if (command.equals(Constants.REMOVE_PREPAID_PATTERN)) {
				return removePrepaidPattern(args);
			}else if (command.equals(Constants.GET_PREPAID_PATTERN)) {
				return getPrepaidPattern();
			}
			 return null;	
		}

		public String getUsage(String command) {
			if (command.equals(Constants.ADD_PREPAID_PATTERN)) {
				return "Adds the prepaid pattern to the provisioned patterns. Takes input as comma separated patterns";	
			} else if (command.equals(Constants.REMOVE_PREPAID_PATTERN)) {
				return "Removes the prepaid pattern from the provisioned patterns. Takes input as comma separated patterns";
			}else if (command.equals(Constants.GET_PREPAID_PATTERN)) {
				return "Prints currently configured prepaid patterns";
			}
			return null;
		}
	}

	
}
