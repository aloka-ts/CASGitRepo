package com.genband.jain.protocol.ss7.tcap;

import jain.ASNParsingException;
import jain.CriticalityTypeException;
import jain.InvalidAddressException;
import jain.InvalidListenerConfigException;
import jain.ListenerAlreadyRegisteredException;
import jain.ListenerNotRegisteredException;
import jain.MandatoryParamMissingException;
import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.ParameterOutOfRangeException;
import jain.ParseError;
import jain.ParseError.PARSE_ERROR_TYPE;
import jain.protocol.ss7.IdNotAvailableException;
import jain.protocol.ss7.JainSS7Factory;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.SignalingPointCode;
import jain.protocol.ss7.SubSystemAddress;
import jain.protocol.ss7.UserAddressEmptyException;
import jain.protocol.ss7.sccp.SccpConstants;
import jain.protocol.ss7.sccp.StateIndEvent;
import jain.protocol.ss7.sccp.StateReqEvent;
import jain.protocol.ss7.sccp.management.NPCStateIndEvent;
import jain.protocol.ss7.sccp.management.NStateIndEvent;
import jain.protocol.ss7.sccp.management.NStateReqEvent;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.GTIndicator0001;
import jain.protocol.ss7.tcap.GTIndicator0010;
import jain.protocol.ss7.tcap.GTIndicator0011;
import jain.protocol.ss7.tcap.GTIndicator0100;
import jain.protocol.ss7.tcap.GlobalTitle;
import jain.protocol.ss7.tcap.InvalidUserAddressException;
import jain.protocol.ss7.tcap.JainTcapListener;
import jain.protocol.ss7.tcap.JainTcapStack;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.TcapErrorEvent;
import jain.protocol.ss7.tcap.TcapUserAddress;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.LocalCancelIndEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.RejectIndEvent;
import jain.protocol.ss7.tcap.dialogue.BeginIndEvent;
import jain.protocol.ss7.tcap.dialogue.EndIndEvent;
import jain.protocol.ss7.tcap.dialogue.UnidirectionalIndEvent;

import org.yaml.snakeyaml.Yaml;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.baypackets.ase.util.AseUtils;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TooManyListenersException;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipApplicationSessionActivationListener;
import javax.servlet.sip.SipApplicationSessionEvent;
import javax.servlet.sip.SipApplicationSessionListener;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TimerService;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bn.exceptions.EnumParamOutOfRangeException;

import com.baypackets.ase.common.AseEventListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AppClassLoader;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.RoleChangeListener;
import com.baypackets.ase.deployer.TcapSessionCount;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.resource.Message;
import com.baypackets.ase.resource.MessageHandler;
import com.baypackets.ase.resource.ResourceEvent;
import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.resource.ResourceListener;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.spi.replication.appDataRep.AppDataReplicator;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.ase.util.AseTUIService;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.ReplicationUtilityListener;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.ParameterName;
import com.genband.jain.protocol.ss7.tcap.router.TcapNextAppInfo;
import com.genband.jain.protocol.ss7.tcap.router.TcapRoutingController;
import com.genband.jain.protocol.ss7.tcap.router.TcapRoutingControllerImpl;
import com.genband.tcap.parser.SignalingPointStatus;
import com.genband.tcap.parser.TcapContentReaderException;
import com.genband.tcap.parser.TcapContentWriterException;
import com.genband.tcap.parser.TcapParser;
import com.genband.tcap.parser.TcapType;
import com.genband.tcap.parser.Util;
import com.genband.tcap.provider.TcapFactory;
import com.genband.tcap.provider.TcapListener;
import com.genband.tcap.provider.TcapProvider;
import com.genband.tcap.provider.TcapSession;

import org.yaml.snakeyaml.Yaml;

public class JainTcapProviderImpl extends javax.servlet.sip.SipServlet 
implements javax.servlet.Servlet, TcapProvider, SipApplicationSessionListener, 
AseEventListener, RoleChangeListener,MessageHandler, ResourceListener, 
SipApplicationSessionActivationListener,ReplicationUtilityListener, CommandHandler
{
	private static final long serialVersionUID = -4629937833502893124L;
	private static Properties tcapProvProperty = new Properties();
	private static Properties printTcapInfoStatusFile;
	boolean simulator = false;
	private String loopBackEnabled = null;
	private String loopBackScenario = null;
	private String ftTesting = null;

	private byte[] contentInviteResp = null;
	private byte[] inviteSccpMsg = {(byte)0x35, (byte)0x0e, (byte)0x0d, (byte)0x04, (byte)0x0b, (byte)0x06, (byte)0x01, (byte)0x03, (byte)0x01, (byte)0x08, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x79, (byte)0x07, (byte)0x92};
	private String rsnSSA = null;

	//Start: Changes for loopback
	private String ingwIP = null;
	private short ingwPort = 0;
	private TcapListener defaultListener;
	//end: Changes for loopback

	//replicator to replicate appsession
	private AppDataReplicator		appDataReplicator;

	private AseHost host = (AseHost) Registry.lookup(Constants.NAME_HOST);

	/**
	 * below set of variables are used to measure perf of doNotify
	 */
	private static AtomicLong	sInitialRequestCount	= new AtomicLong(0);
	private static AtomicLong	sTotalTimeFirstReqResp	= new AtomicLong(0);
	private static int sAvgTimeCalcCount = 0;
	private final String AVG_TIME_CALC_COUNT ="AVG_TIME_CALC_COUNT";
	private static final String AVG_RESPONSE_TIME = "AVG_RESPONSE_TIME";
	private static final String CMD_SET_AVG_COUNT = "set-call-cnt-for-tcap-avg-resp";
	private static final String CMD_SET_AVG_RES_THRES = "set-tcap-avg-resp-thres";
	private static final String X_TCAP_VARIANT = "X-Tcap-Variant";
	private static long sAvgResponseTime = 0;
	private static AtomicBoolean alarmSentForAvgResponseTime = new AtomicBoolean(false);
	private static AseAlarmService aseAlarmService = null;
	private static Object lock = new Object();
	/**
	 * Timeout value after which SAS should restart HB if not restored after FT
	 * value in seconds
	 */
	private int ftHbRestartTime =60;
	private final String FT_HB_RESTART_TIME ="FT_HB_RESTART_TIME";

	private static boolean isSS7MsgInfoEnabled = false;

	// CO- Changes 
	boolean ignoreProtocolCheck = true;


	private TcapAppRegistry tcapAppRegistry = TcapAppRegistry.getInstance();
	private TcapRoutingController tcapRoutingController = TcapRoutingControllerImpl
			.getInstance();
	final String SccpUserAddressAttr = "SccpUserAddressAttr";
	final String ORIG_SccpUserAddressAttr = "ORIG_SccpUserAddressAttr";
	final String ServiceKey = "ServiceKey";
	final String ListenerApp = "ListenerApp";
	final String AppName = "AppName";
	final String APPLICATION_SESSION = "ApplicationSession";
	// final String NOTIFY_SESSION = "NotifySession";
	static final String DIALOG_ID = "Dialogue-Id";

	protected static final transient String TCAP_DLG_ID_APPSESSION_MAP = "Tcap_DlgID_AppSessionMap";

	private Map<Integer, LinkedList<ComponentReqEvent> > currentComps = new ConcurrentHashMap <Integer, LinkedList<ComponentReqEvent> >();

	/* TODO ... store these against tpg for FT */
	private Map<String, NPCStateIndEvent> nPCStateStatusMap = new ConcurrentHashMap <String, NPCStateIndEvent>();
	private Map<String, NStateIndEvent> nStateStatusMap = new ConcurrentHashMap <String, NStateIndEvent>();

	public SipFactory factory = null;
	//private SipURI src = null;^M
	private Map<Integer, Integer> InvocationMap = new ConcurrentHashMap<Integer, Integer>();

	/*@reeta added
	 * Added this map for correlation of tgcap messages in ANSI e.g in WIN
	 * multiple Begin-end( dialogues ) will take place in single call.So in WIN
	 * billingid will be used as correlation id b/w diffrent dialogues. for other
	 * protocols with multiple dialogues in single call this value may be diffrent
	 */
	private Map<Integer, Integer> tcCorrIdMap = new ConcurrentHashMap<Integer, Integer>();

	public final String TC_CORR_ID_HEADER="TC-Corr-id";

	private static int nodeId = 1;

	private static final String MRS_RELAY="MRS_RELAY";



	// Constant for generating base value for start range
	private int MAX_GEN_DLG_IDS = 1000000;
	// offset for dialog id start range vale
	private static int offsetDialogueStart=0;
	// total number of dialog id to be generated

	private static int numDialogeIds = 1000000;
	private static int UserDialogueStart ;
	private static int MaxDialougues;
	Integer DialogueId = UserDialogueStart;


	static private Logger logger = Logger.getLogger(JainTcapProviderImpl.class
			.getName());
	static private JainTcapProviderImpl jtpi;

	// private Pattern p = null;
	TcapSessionReplicator replicator = null;
	private INGatewayManagerImpl ingwManager = null;

	// added for RSN message handling
	//public RSNHandler rsnHandler = null;
	private final String RSNSTATUS = "RSN_STATUS";

	// added for ACTIVITY_TEST Time
	private final String AT_TIME = "AT_TIME";
	private final String RSN_SSA = "RSN_SSA";
	//
	private final String BEGIN_REQ = "BEGIN_REQ";

	final static int PROTOCOL_NONE = 0;
	final static int PROTOCOL_ANSI = 1;
	final static int PROTOCOL_ITU = 2;
	final static String ITU_STR="ITU";
	final static String ANSI_STR="ANSI";
	
	// this flag corresponds to tcap.sccp.ansi = 0/1
	// this is a scenario of coexistence of ANSI and ITUT (in USA). If this 
	// flag is set to 1 then some local handling is performed. By default it 
	// is turned off. 
	private int isProtocolSccpAnsi = 0;
	
	// In case of GT, whether to use digits being received in SCCP called party 
	// or to use called party from message itself for checking triggering application. 
	// If set as 0 then use called party address.
	private int isSCCPDigitsToBeUsed = 0;

	private int protocol = PROTOCOL_NONE;
	
	private int listenerCount = 0;

	public static String PRINT_TCAP_INFO_STATUS_FILE = null;

	public Set<String> congestedPointCodes = new HashSet<String>();
	private String appRoutingEnabled;
	private Deployer appDeployer;

	public static final char hexcodes[] = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private final static String ENABLE_SS7_SIG_INFO = "ENABLE_SS7_SIG_INFO";
	
	// Triggering Criteria Rule file read for static rules. 
	private TcapTriggeringCriteriaRule triggeringCriteriaRule = null;
	private List<String> opsCodes = null;

	public  static String formatBytes(byte data[]) {
		if(logger.isDebugEnabled()){
			logger.debug("format bytes:"+ data.length);
		}
		char output[] = new char[5 * (data.length)];
		int top = 0;

		for (int i = 0; i < data.length; i++) {
			output[top++] = '0';
			output[top++] = 'x' ;
			output[top++] = hexcodes[(data[i] >> 4) & 0xf];
			output[top++] = hexcodes[data[i] & 0xf];
			output[top++] = ' ';
		}

		return (new String(output).trim());
	}

	/*
	 * Calculate average time taken by PH in giving response to initial request
	 */
	private static void calculateAvgTime(long startTimeMilliSec, int dialogueId, String appName) {
		try {
			long timeTaken = System.currentTimeMillis()	- startTimeMilliSec;
			if(timeTaken >= sAvgResponseTime) {
				logger.error("timeTaken by call for processing BEGIN : " + timeTaken);
			}
			long totalTimeTaken = sTotalTimeFirstReqResp.addAndGet(timeTaken);
			long totalCount = sInitialRequestCount.incrementAndGet();
			long avgResponseTime = totalTimeTaken / totalCount;
			if (totalCount % sAvgTimeCalcCount == 0) {

				if(avgResponseTime >= sAvgResponseTime) {

					if(alarmSentForAvgResponseTime.compareAndSet(false, true)) {
						logger.error("Average Tcap Resp time " + avgResponseTime + " Milis. Threshold reached, raising alarm");
						aseAlarmService.sendAlarm(2019, "Average Tcap Resp time reached threshold " + 
								avgResponseTime + " Milis");	//raise alarm
					}

				}else if(avgResponseTime < sAvgResponseTime) {

					if(alarmSentForAvgResponseTime.compareAndSet(true, false)) {
						logger.error("Average Tcap Resp " + avgResponseTime + " Milis. Falling threshold reached, clearing alarm");
						aseAlarmService.sendAlarm(2018 , "Falling threshold reached " + avgResponseTime + " Mills"); // raise clearing alarm
					}

				}
				logger.error("Current stats :: currentRequestCount : " + sInitialRequestCount.get() +
						", currentTotalResponseTime : " + sTotalTimeFirstReqResp.get() +
						", thresholdCount : " + sAvgTimeCalcCount +
						", thresholdTime : " + sAvgResponseTime + 
						", Average Tcap doNotify Response time [milli-sec] : " + avgResponseTime +
						", DialogueId : " + dialogueId +
						",AppName : " + appName);
				if(logger.isDebugEnabled()) {
					logger.debug("resetting the requestCount and totalReqResponseTime counters..");
				}
				sTotalTimeFirstReqResp.set(0);
				sInitialRequestCount.set(0);
			}


			/*if(avgResponseTime >= sAvgResponseTime) {

				if(alarmSentForAvgResponseTime.compareAndSet(false, true)) {
					logger.error("Average Tcap Resp time threshold reached, so raising alarm");
					logger.error("Current stats :: currentRequestCount : " + sInitialRequestCount.get() +
							", currentTotalResponseTime : " + sTotalTimeFirstReqResp.get() +
							", thresholdCount : " + sAvgTimeCalcCount +
							", thresholdTime : " + sAvgResponseTime + 
							", Average Tcap doNotify Response time [milli-sec] : " + avgResponseTime);
					aseAlarmService.sendAlarm(2019, "Average Tcap Resp time reached threshold " + 
							avgResponseTime + " Milis");	//raise alarm
				}

			}else if(avgResponseTime < sAvgResponseTime) {

				if(alarmSentForAvgResponseTime.compareAndSet(true, false)) {
					logger.error("Falling threshold reached, so raising clearing alarm");
					logger.error("Current stats :: currentRequestCount : " + sInitialRequestCount.get() +
							", currentTotalResponseTime : " + sTotalTimeFirstReqResp.get() +
							", thresholdCount : " + sAvgTimeCalcCount +
							", thresholdTime : " + sAvgResponseTime + 
							", Average Tcap doNotify Response time [milli-sec] : " + avgResponseTime);
					aseAlarmService.sendAlarm(2018 , "Falling threshold reached " + avgResponseTime + " Mills"); // raise clearing alarm
				}

			}*/
			if(avgResponseTime >= sAvgResponseTime) {
				logger.error("currReqCnt : " + sInitialRequestCount.get() +
						", currTotalRespTm : " + sTotalTimeFirstReqResp.get() +
						", thresCnt : " + sAvgTimeCalcCount +
						", thresTm : " + sAvgResponseTime + 
						", Average Tcap doNotify Response time [milli-sec] : " + avgResponseTime +
						", DialogueId : " + dialogueId +
						",AppName : "+ appName);
			}
		} catch (Exception ex) {
			logger.error("Error in caluclating avg time. " + ex.getMessage());
			if (logger.isInfoEnabled()) {
				logger.error("Error in average time calculation", ex);
			}
		}
	}


	public void init() throws ServletParseException, ServletException
	{
		if(logger.isInfoEnabled()){
			logger.info("init called @@@...");
		}
		jtpi = this;
		JainSS7Factory fact = JainSS7Factory.getInstance();
		fact.setProvider(this);
		
		DeployerFactory deployerFactory = (DeployerFactory) Registry.lookup(DeployerFactory.class.getName());
		appDeployer = deployerFactory.getDeployer(DeployableObject.TYPE_SAS_APPLICATION);
		factory = (SipFactory)getServletContext().getAttribute(SipServlet.SIP_FACTORY);
		aseAlarmService = (AseAlarmService)Registry.lookup(Constants.NAME_ALARM_SERVICE);

		TelnetServer telnetServer = (TelnetServer) Registry.lookup(Constants.NAME_TELNET_SERVER);
		telnetServer.registerHandler(CMD_SET_AVG_COUNT, this);
		telnetServer.registerHandler(CMD_SET_AVG_RES_THRES, this);

		super.init();

		appDataReplicator = new AppDataReplicator();
		replicator = new TcapSessionReplicator(this);
		replicator.init();

		ingwManager = new INGatewayManagerImpl();
		ingwManager.init();
		Iterator<INGateway> gws = ingwManager.getAllINGateways();
		if (!gws.hasNext())
		{
			throw new ServletException("No IN Gateways Configured.");
		}   

		//saneja@bug 10406 creating default listener for error scenarios
		defaultListener = new DefaultInapListener(this);
		replicator.addListener(defaultListener);
		//]

		loadPrintTcapInfoFile();

		// simulator mode
		try {
			if(logger.isInfoEnabled()){
				logger.info("Loading the JainTcapProvider properties");
			}
			String ase_Home = System.getProperty("ase.home");
			if(logger.isInfoEnabled()){
				logger.info("ASE_HOME:" + ase_Home);
			}


			logger.info("starting load  properties file" + Constants.ASE_HOME + "/"
					+ Constants.FILE_CAS_STARUP_PROPERTIES);

			String filepath = Constants.ASE_HOME + "/"
					+ Constants.FILE_CAS_STARUP_PROPERTIES;
			InputStream in = null;
			try {
				in = Files.newInputStream(Paths.get(filepath));
			} catch (Exception e) {
				logger.error("error while readinf file");
			}

			try{
				Yaml yaml = new Yaml();
				TreeMap<String, String> oemsAgentProperties = yaml.loadAs(in, TreeMap.class);

				appRoutingEnabled = oemsAgentProperties.get("app.routing.enabled");

				logger.info("appRoutingEnabled is "+appRoutingEnabled);

			} finally{
				in.close();
			}

			String path = "conf/tcapProvider.properties";
			if(logger.isInfoEnabled()){
				logger.info("Path for tcapProvider.properties:" + path);
			}
			String filePath = ase_Home.concat("/").concat(path);
			if(logger.isInfoEnabled()){
				logger.info("Absolute tcapProvider.properties path:" + filePath);
			}

			tcapProvProperty.load(new FileInputStream(filePath));

			String actAsSimulator = tcapProvProperty.getProperty("actAsIncSimulator");	
			if("true".equals(actAsSimulator)) {
				simulator = true; 
			}

			if(simulator == true) {				

				String protocolvariant = tcapProvProperty.getProperty("protocolvariant");
				String localPc = tcapProvProperty.getProperty("localpc");
				String localSsn = tcapProvProperty.getProperty("localssn");

				if(protocolvariant == null || localPc == null || localSsn == null) {
					logger.error("local SUA details Missing!! It is Mandatory in simulator mode.");
					throw new Exception("local SUA details Missing/Incomplete in configuration file.");
				}

				SignalingPointCode localSpc = null;
				SccpUserAddress localAddr = new SccpUserAddress(new Object());

				String[] tmp = localPc.split("-");
				if (tmp.length == 3) {
					try {
						localSpc = new SignalingPointCode(Integer.parseInt(tmp[2]),
								Integer.parseInt(tmp[1]), Integer.parseInt(tmp[0]));
						localAddr = new SccpUserAddress(new SubSystemAddress(localSpc, Short.parseShort(localSsn)));
						localAddr.setProtocolVariant(Integer.parseInt(protocolvariant));
					} catch (Exception e) {
						logger.error("Invalid SUA details!" + e);
					}
				}

				NStateReqEvent event = new NStateReqEvent(new Object());
				event.setAffectedUser(localAddr);
				event.setUserStatus(1);

				byte[] encode = null;
				try {
					encode = TcapParser.encodeSCCPMgmtMsg(event, 11);
				} catch (MandatoryParameterNotSetException e) {
					logger.error("MandatoryParameterNotSetException encoding invite sccp message",e);
				} catch (ParameterNotSetException e) {
					logger.error("ParameterNotSetException encoding invite sccp message",e);
				}

				if(encode == null){
					if(logger.isDebugEnabled())
						logger.debug("Encoded msg null returning default msg");
					contentInviteResp = inviteSccpMsg;
				}
				//replace the protocol variant
				inviteSccpMsg[8] = encode[10];
				//replace the point code
				inviteSccpMsg[10] = encode[14];
				inviteSccpMsg[11] = encode[15];
				inviteSccpMsg[12] = encode[16];
				inviteSccpMsg[13] = encode[17];
				//replace the SSN
				inviteSccpMsg[15] = encode[12];

				if(logger.isDebugEnabled())
					logger.debug("Got byte array for INVIte::"+com.genband.tcap.parser.Util.formatBytes(inviteSccpMsg));
				contentInviteResp = inviteSccpMsg;
			}
		} catch (Exception ex) {
			logger.error("Error reading properties file. Not acting as simulator.");
			if(logger.isDebugEnabled()){
				logger.debug("got exception::",ex);
			}
		}

		//added for RSN handling
		//rsnHandler = new RSNHandler();
		//rsnHandler.init(getServletContext(), ingwManager, replicator);

		//AT handling
		ConfigRepository configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

		int protVersion = Integer.parseInt(configRep.getValue(Constants.TCAP_PROTOCOL_VERSION));
		if (protVersion == PROTOCOL_ANSI){
			protocol = PROTOCOL_ANSI;
		} else if (protVersion == PROTOCOL_ITU){
			protocol = PROTOCOL_ITU;
		} else{
			protocol = PROTOCOL_NONE;
		}
		if(logger.isDebugEnabled()){
			logger.debug( "protocol :" + protocol);
		}
		
		// check if SCCP protocol version is ANSI (required in co-existence)
		
		 if(configRep.getValue(Constants.TCAP_SCCP_ANSI)!=null)
		     isProtocolSccpAnsi = Integer.parseInt(configRep.getValue(Constants.TCAP_SCCP_ANSI));

		TcapParser.setIsProtocolSccpAnsi(isProtocolSccpAnsi);

		logger.debug("value of tcap.sccp.ansi in ase.yml is isProtocolSccpAnsi: " + isProtocolSccpAnsi);
	
		// check for SCCPDigits to be used or not.
		if(configRep.getValue(Constants.USE_GT_DIGIT_FOR_TRIGGERING)!=null)
			isSCCPDigitsToBeUsed = Integer.parseInt(configRep.getValue(Constants.USE_GT_DIGIT_FOR_TRIGGERING));

		logger.debug("value of 	ss7.usegtdigits in ase.yml is isSCCPDigitsToBeused: " + isSCCPDigitsToBeUsed);
		

		ftTesting = configRep.getValue(Constants.TCAP_FT_TESTING);
		if(logger.isDebugEnabled()){
			logger.debug( "FT Testing :" + ftTesting);
		}
		String value = configRep.getValue(AT_TIME);
		rsnSSA = configRep.getValue(RSN_SSA);
		if(logger.isDebugEnabled()){
			logger.debug( "AT time:" + value);
		}
		if(value != null){
			String[] hhmm = value.split(":");
			if(hhmm.length == 2){
				int hh = Integer.parseInt(hhmm[0]);
				int mm = Integer.parseInt(hhmm[1]);
				long miliSecond = (long)(hh*60*60*1000 + mm*60*1000);
				if(logger.isDebugEnabled()){
					logger.debug("AT time miliSecond:" + miliSecond + "date format:" + new Date(miliSecond));
				}
				Calendar c = Calendar.getInstance();
				long currentMiliSec = (c.get(Calendar.HOUR_OF_DAY)*60*60*1000) + (c.get(Calendar.MINUTE)*60*1000);
				if(logger.isDebugEnabled()){
					logger.debug( "currentMiliSec  miliSecond:" + currentMiliSec + "date format:" + new Date(currentMiliSec));
				}
				long timeDiff = miliSecond - currentMiliSec ;
				if(logger.isDebugEnabled()){
					logger.debug("timeDiff  miliSecond:" + timeDiff + "date format:" + new Date(timeDiff));
				}
				if(timeDiff < 0) {
					timeDiff = (24*60*60*1000 - currentMiliSec ) + miliSecond ;
					if(logger.isDebugEnabled()){
						logger.debug("negative timeDiff  miliSecond:" + timeDiff + "date format:" + new Date(timeDiff));
					}
				}
				SipApplicationSession appSession = factory.createApplicationSession() ;
				String val = String.valueOf(timeDiff);
				appSession.setAttribute("AT", val);
				if(logger.isDebugEnabled()){
					logger.debug("AT timer val  set in appsession:" + val);
				}
				TimerService timerService = ((TimerService)getServletContext().getAttribute(SipServlet.TIMER_SERVICE));
				timerService.createTimer(appSession, timeDiff, false, new ATHandler());
			}else{
				logger.error("AT can not be started because given format is not in the expected format-hh:mm");
			}

		}

		//Property to enable/disable Loobpack
		loopBackEnabled = configRep.getValue(Constants.TCAP_LOOPBACK_ENABLED);
		loopBackScenario = configRep.getValue(Constants.TCAP_LOOPBACK_SCENARIO);


		// Register with the cluster manager...
		ClusterManager clusterMgr = (ClusterManager)Registry.lookup(Constants.NAME_CLUSTER_MGR);            
		clusterMgr.registerRoleChangeListener(this, Constants.RCL_JAINTCAPAPP_PRIORITY);


		String timeoutTostartHB = configRep.getValue(FT_HB_RESTART_TIME);
		if(timeoutTostartHB !=null && timeoutTostartHB.matches("[1-9][0-9]*")){
			ftHbRestartTime = Integer.parseInt(timeoutTostartHB);
		}

		String avgTimeCalcCount = configRep.getValue(AVG_TIME_CALC_COUNT);
		if(avgTimeCalcCount !=null && avgTimeCalcCount.matches("[1-9][0-9]*")){
			sAvgTimeCalcCount = Integer.parseInt(avgTimeCalcCount);
		}

		String avgResponseTime = configRep.getValue(AVG_RESPONSE_TIME);
		if(avgResponseTime != null && !avgResponseTime.isEmpty()) {
			sAvgResponseTime = Long.parseLong(avgResponseTime);
		}

		String strEmsSubsysId = configRep.getValue(ParameterName.SUBSYSTEM_ID);
		int emsSubsysId=1;

		try{
			emsSubsysId = Integer.parseInt(strEmsSubsysId);
			
			if(logger.isDebugEnabled()){
				logger.debug("Subsystem_Id is -->"+ emsSubsysId);
			}
		}catch(Exception e){
			emsSubsysId=1;
			logger.error("Exception in parsing emsSubsysId",e);
		}

		String rangeMultiplier = configRep.getValue(Constants.PROP_INC_TCAP_SDLG_MULTIPLIER);

		try{
			MAX_GEN_DLG_IDS=Integer.parseInt(rangeMultiplier);
		}catch(Exception e){
			MAX_GEN_DLG_IDS = 1000000;
			logger.error("Exception in parsing"+Constants.PROP_INC_TCAP_SDLG_MULTIPLIER,e);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("MAX_GEN_DLG_IDS:----> "+MAX_GEN_DLG_IDS);
		}

		String strNumSDLGTotal = configRep.getValue(Constants.PROP_INC_TCAP_SDLG_TOTAL_LIMIT);

		try{
			numDialogeIds=Integer.parseInt(strNumSDLGTotal);
		}catch(Exception e){
			numDialogeIds=1000000;
			logger.error("Exception in parsing numDialogIds",e);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("numDialogeIds:----> "+numDialogeIds);
		}

		String strTcapSDLGOffset = configRep.getValue(Constants.PROP_INC_TCAP_SDLG_OFFSET);
		try{
			offsetDialogueStart=Integer.parseInt(strTcapSDLGOffset);
		}catch(Exception e){
			offsetDialogueStart=0;
			logger.error("Exception in parsing numDialogIds",e);
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("numDialogeIds:----> "+numDialogeIds);
		}

		if(offsetDialogueStart+numDialogeIds>MAX_GEN_DLG_IDS){
			numDialogeIds=MAX_GEN_DLG_IDS;
			offsetDialogueStart=0;
			logger.error("offsetDialogueStart+numDialogeIds > MAX_GEN_DLG_IDS so using default");
		}


		nodeId=emsSubsysId%100;

		UserDialogueStart = nodeId * MAX_GEN_DLG_IDS+offsetDialogueStart;
		MaxDialougues = nodeId * MAX_GEN_DLG_IDS+offsetDialogueStart+numDialogeIds-1;
		DialogueId = UserDialogueStart;
	
		
		if(logger.isDebugEnabled()){
			logger.debug("DialogId generation range is: "+UserDialogueStart +"-"+ MaxDialougues +" with nodeId "+nodeId);
		}
		
		TcapJndiObjectFactory.bind(this);
		TcapProviderGateway.init(ingwManager);

		if (TcapSessionReplicator.isSASActive())
		{
			logger.warn("SAS Active @@@");
			//moved initailze HB to common
			initializeHB(gws);
		}
		
		if (checkV2EnabledOrNot()) {
			// Read triggering criteria from file
			String filepath = Constants.ASE_HOME + "/"
					+ Constants.TRIGGERING_RULE_FILE;
			if (logger.isDebugEnabled()) {
				logger.debug("Triggering Rule file path- " + filepath);
			}

			InputStream in = null;
			try {
				in = Files.newInputStream(Paths.get(filepath));
				Yaml yaml = new Yaml();
				triggeringCriteriaRule = yaml.loadAs(in,
						TcapTriggeringCriteriaRule.class);
				if (logger.isDebugEnabled()) {
					logger.info("triggeringCriteriaRule:"
							+ triggeringCriteriaRule);
				}
				
				// read contents from Triggering criteria rule
				String opCodesString = triggeringCriteriaRule.getOpCodes();
				if (logger.isDebugEnabled()) {
					logger.info("opCodesString from trigerring rule file:" + opCodesString);
				}
				
				// parse the content based on opcodeString
				opsCodes = AseUtils.splictStringToList(opCodesString, ",");
			} catch (IOException e) {
				logger.error("Triggering Rule file parse exception occured- "
						+ filepath);
			} finally {
				logger.info("close the stream");
				try {
					in.close();
				} catch (Exception e) {
					logger.error("error while closing input steam for trigering rule yml");
				}
			}
		}

		if(logger.isInfoEnabled()){
			logger.info("init successfully @@@");
		}
		//p = Pattern.compile("(INVITE|INFO|NOTIFY)");
	}

	private void initializeHB(Iterator<INGateway> gws) throws ServletParseException   {
		if(logger.isInfoEnabled()){
			logger.info("Enter initializeHB");
		}
		for (; gws.hasNext();) {
			/* IN gateways */
			INGateway gw = gws.next();
			//Start: Changes for loopback
			if (ingwIP == null)
				ingwIP = gw.getHost();
			if (ingwPort == 0)
				ingwPort = gw.getPort();
			//End: Changes for loopback

			logger.warn("Calling TcapProviderGateway.createTcapProviderGateway @@@");
			TcapProviderGateway tpg = TcapProviderGateway.createTcapProviderGateway(this, gw);
			try {
				logger.info("tpg inside JainTacpProviderImpl:"+ tpg + " contentInviteResp :"+ contentInviteResp );
				if (simulator != true) {
					tpg.connect();
				} else {
					if (contentInviteResp != null)
						tpg.setAddress(contentInviteResp);
					tpg.setConnected(true);
				}
			} catch (Exception ioe) {
				logger.error("Failed to connect to gateway : " + gw, ioe);
			}

			break; //FIXME: am i required??
		}//@End for gws

		if(logger.isInfoEnabled()){
			logger.info("Leave initializeHB");
		}
	}


	public void destroy()
	{
		TelnetServer telnetServer = (TelnetServer) Registry.lookup(Constants.NAME_TELNET_SERVER);
		telnetServer.unregisterHandler(CMD_SET_AVG_COUNT, this);
		telnetServer.unregisterHandler(CMD_SET_AVG_RES_THRES, this);
		List<TcapProviderGateway> tpgList = TcapProviderGateway.getAllInstances();
		Iterator<TcapProviderGateway> iter = tpgList.iterator();
		while (iter.hasNext())
		{
			/* IN gateways */
			iter.next().disconnect(false);
		}
		TcapJndiObjectFactory.unbind();
		ingwManager.close();
		replicator.cleanup();
		super.destroy();
	}

	public static JainTcapProviderImpl getImpl()
	{
		return jtpi;
	}

	public void disconnected(TcapProviderGateway tpg)
	{
		int startDialogue = DialogueId;
		int tryDialogue = ++startDialogue;
		boolean done = false;
		while (!done)
		{
			if (tryDialogue == startDialogue)
				done = true;
			else
			{
				if (InvocationMap.get(tryDialogue) != null)
				{
					TcapSession ts = replicator.getTcapSession(tryDialogue);

					if (ts != null)
					{
						SccpUserAddress sua = (SccpUserAddress)ts.getAttribute(SccpUserAddressAttr);

						SignalingPointCode spc = null;
						try
						{
							spc = sua.getSubSystemAddress().getSignalingPointCode();
						}

						catch (ParameterNotSetException pnse)
						{
							logger.error( "can't happen, already checked", pnse);
						}

						TcapProviderGateway suatpg = TcapProviderGateway.getByAddress(spc);
						if (suatpg == tpg)
						{
							/* release Dialogue */
							if(logger.isDebugEnabled()){
								logger.debug( "releasing dialogue id: " + tryDialogue + ", associated with INGw: " + tpg.INGwId());
							}
							try
							{
								//replicator.closeTcapSession(tryDialogue);
								ts.invalidate();
							}
							catch (IdNotAvailableException inae)
							{
								logger.error( "can't happen, already checked", inae);
							}
						}

					}
				}

				tryDialogue++;

				if (tryDialogue > MaxDialougues)
					tryDialogue = UserDialogueStart;
			}
		}
	}

	protected void doErrorResponse(SipServletResponse resp) throws IOException
	{
		TcapProviderGateway tpg = TcapProviderGateway.getTcapProviderGateway(resp);
		logger.error("doErrorResponse:\n"+ resp.toString());

		//response for duplicate notify so ignoring not invalidating
		if (resp.getStatus() == 499){
			if(logger.isDebugEnabled()){
				logger.debug( "doErrorResponse:aduplicate notify error reponse ignore");
			}
			return;
		}

		if(tpg == null){
			SipApplicationSession appSession = resp.getApplicationSession();
			if(logger.isDebugEnabled()){
				logger.debug( "doErrorResponse:TPG is null appSession obj:" + appSession);
			}
			//RSNStatus rsnStatus = (RSNStatus)appSession.getAttribute(RSNSTATUS);
			//if(logger.isDebugEnabled()){
			//	logger.debug("doErrorResponse: rsnStatus:" + rsnStatus);
			//}
			//if(rsnStatus != null){
			//	if(logger.isDebugEnabled()){
			//		logger.debug( "doErrorResponse: Setting rsnStatus UNREACHABLE");
			//	}
			//	rsnStatus.setStatus(Status.UNREACHABLE);
			//}
			//invalidate as rsn status null
			//appSession.invalidate();

		}else {
			tpg.doErrorResponse(resp);
		}
	}

	protected void doSuccessResponse(SipServletResponse resp) throws IOException
	{
		TcapProviderGateway tpg = TcapProviderGateway.getTcapProviderGateway(resp);
		if(logger.isDebugEnabled()){
			logger.debug("doSuccessResponse: "+ resp.toString());
		}

		//done to support loopback cleanup not a loopback call
		if ( resp.getMethod().equals("NOTIFY")    && !(loopBackEnabled.toLowerCase().equals("false")) ){
			resp.getApplicationSession().invalidate();
			return;
		}


		if (ftTesting.equalsIgnoreCase("Received_200Ok")){
			System.exit(0);
		}

		if(tpg == null) {
			logger.error("doSuccessResponse: TcapProviderGateway is null");
			SipApplicationSession appSession = resp.getApplicationSession();

			//invalidate if notify sesison and tpg is null. else follow notify specific handling
			if(appSession.isValid()   && !(resp.getMethod().equals("NOTIFY")) ){
				if(logger.isDebugEnabled()){
					logger.debug("Appsession is valid invalidiating");
				}
				appSession.invalidate();
			}
			//return here as appsession in now invalidated
			return;
		}else {
			if (!tpg.isConnected())
			{
				tpg.connected(resp);
				if (resp.getRequest().getMethod().equals("INVITE"))
				{
					SipServletRequest ssr = resp.createAck();
					if(logger.isDebugEnabled()){
						logger.debug("Sending ack:\n" + ssr);
					}
					ssr.send();
					byte [] content = (byte[])resp.getContent();
					if(content!= null && content.length > 2) {
						int length = content.length;
						byte [] tcapContent = new byte[length-2]; // remove the last 2 octects (\r\n)
						System.arraycopy(content, 0, tcapContent, 0, length-2);

						tpg.setAddress(tcapContent);
						logger.error("Starting HeartBeat between SAS and INC");
						tpg.StartHeartBeat();
					}
				}
			}
			tpg.activeConnection(resp);
		}
		//notify specific handling to invalidate appsesison on 200 ok
		if (resp.getRequest().getMethod().equals("NOTIFY")) {
			if (ftTesting.equalsIgnoreCase("After_Invalidate_On_Received_200Ok")) {
				System.exit(0);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Check if appsession valid for invalidation \n");
			}

			try {
				SipApplicationSession appSession = resp.getApplicationSession();

				//added to ensure appsesison is invalidated after last 200ok
				Object pendingNotify = appSession.getAttribute("PENDING_NOTIFY");
				Integer pendingNotifyInt = null;
				if (pendingNotify != null) {
					pendingNotifyInt = (Integer) pendingNotify;
					pendingNotifyInt--;
				}
				if (pendingNotifyInt == null || pendingNotifyInt < 0) {
					pendingNotifyInt = 0;
				}

				appSession.setAttribute("PENDING_NOTIFY", pendingNotifyInt);

				//				resp.getApplicationSession().setInvalidateWhenReady(false);
				//				resp.getSession().invalidate();
				//				resp.getApplicationSession().setInvalidateWhenReady(true);

				if (appSession.isValid()
						&& "true".equalsIgnoreCase((String) (appSession
								.getAttribute("CLOSED"))) && (pendingNotifyInt == 0)) {
					if (logger.isDebugEnabled()) {
						logger.debug("invalidate appSession on do success reponse");
					}

					appSession.invalidate();

				}
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.debug("Exception in invalidating session:\n", e);
				}
				logger.warn("appsession invalidation failed due to exception");
			}
		}

	}

	protected void doOptions(SipServletRequest req) throws IOException
	{
		TcapProviderGateway tpg = TcapProviderGateway.getTcapProviderGateway(req);
		SipServletResponse resp = req.createResponse(200, "OK");
		resp.send();
		tpg.activeConnection(req);
	}

	//Start: changes for loopback
	private SipURI source() throws ServletParseException {
		String fromAddress = null;
		try
		{
			fromAddress = "sip:TcapListener@" + InetAddress.getLocalHost().getCanonicalHostName();
		}
		catch (Exception e)
		{
			logger.error("cannot find Inet address for host.");
			if(logger.isDebugEnabled()){
				logger.debug("Got exception:",e);
			}
		}
		SipURI sourceUri = (SipURI)factory.createURI(fromAddress);
		sourceUri.setTransportParam("udp");
		return sourceUri;
	}

	private SipURI destination() throws ServletParseException{
		SipURI destinationUri = factory.createSipURI("TcapProvider", ingwIP);
		destinationUri.setPort(ingwPort);
		destinationUri.setTransportParam("udp");
		return destinationUri;
	}
	//end: changes for loopback
	protected void doNotify(SipServletRequest req) throws IOException
	{
		//Increment Number of Tcap notify received counter
		AseMeasurementUtil.counterTcapNotifyReceived.increment();
		//Set start time for performance analysis
		long startTimeMilliSec = 0;
		boolean isBegin =false;
		if (sAvgTimeCalcCount > 0) {
			startTimeMilliSec = System.currentTimeMillis();
		}

		DialogueIndEvent diEvent = null;
		int dialogueId = -1;
		//save refernce id of notify session for future use of sipsesison
		//req.getApplicationSession().setAttribute(NOTIFY_SESSION, req.getSession().getId());
		long timeTakenToParse = 0;
		try
		{

			if(logger.isDebugEnabled()){
				logger.debug( "doNotify: " + req.toString());
			}

			TcapType content = null ;

			byte [] tcapContent = (byte[])req.getContent();			 
			int length = tcapContent.length;
			byte [] contentInByte = new byte[length-2]; // remove the last 2 octects (\r\n)
			System.arraycopy(tcapContent, 0, contentInByte, 0, length-2);

			long startTimeToParseInMills = System.currentTimeMillis();
			content = TcapParser.parse(contentInByte, this);

			timeTakenToParse = System.currentTimeMillis() - startTimeToParseInMills;
			if(timeTakenToParse >= sAvgResponseTime) {
				logger.error("Total time spent in parseAndPrepare dialogues and components : " + timeTakenToParse);
			}

			if(logger.isDebugEnabled()){
				logger.debug("Parsed Object: "+ content.toString());
			}
			diEvent = content.getDialogueIndEvent();
			dialogueId = diEvent.getDialogueId();
			SccpUserAddress origSua = null;
			//DialogueReqEvent diReqEvent = content.getDialogueReqEvent();
			if(diEvent!=null && diEvent.getPrimitiveType() == TcapConstants.PRIMITIVE_BEGIN){
				isBegin = true;
				origSua = ((BeginIndEvent)diEvent).getOriginatingAddress();
				String spc = origSua.getSubSystemAddress().getSignalingPointCode().toString();
				if (congestedPointCodes.contains(spc)){
					logger.error("Due to Congestion, rejecting the NOTIFY message");
					SipServletResponse resp = req.createResponse(500);
					if(logger.isInfoEnabled()){
						logger.info("Sending response:\n" + resp);
					}
					resp.send();

					TcapProviderGateway tpg = TcapProviderGateway.getTcapProviderGateway(req);
					if(tpg != null){
						tpg.activeConnection(req);
					}

					return;
				}
			}
			//code added-vidhu
			String operationCode="";
			if(content.getComponentIndEvent().size()==1)
			{
				if (content.getComponentIndEvent().get(0).getPrimitiveType() == TcapConstants.PRIMITIVE_INVOKE ){
					InvokeIndEvent iie = (InvokeIndEvent) content.getComponentIndEvent().get(0);
					Operation operation = iie.getOperation();
					byte[] opCode = operation.getOperationCode();
					operationCode = Util.formatBytes(opCode);
				}
			}
			if(logger.isDebugEnabled()){
				logger.debug("val of opecode " + operationCode);
			}

			SipURI sipUri = (SipURI)req.getFrom().getURI();
			if(logger.isDebugEnabled()){
				logger.debug("doNotify:sipURI from: "+ sipUri.toString());
			}

			if(diEvent != null && diEvent.getPrimitiveType() == TcapConstants.PRIMITIVE_UNIDIRECTIONAL && operationCode.equalsIgnoreCase("0x02")){
				if(logger.isDebugEnabled()){
					logger.debug("doNotify:Calling handleRSA");
				}
				//rsnHandler.handleRSA(diEvent, req);
				//SipServletResponse resp = req.createResponse(200, "OK");
				//if(logger.isInfoEnabled()){
				//	logger.info("Sending response:\n" + resp);
				//}
				//resp.send();
			}
			else{
				if(diEvent != null && diEvent.getPrimitiveType() == TcapConstants.PRIMITIVE_UNIDIRECTIONAL && operationCode.equalsIgnoreCase("0x01")){
					if(logger.isDebugEnabled()){
						logger.debug("doNotify:Calling handleRSN");
					}
					//boolean result = rsnHandler.handleRSN(content, req);
					//if (result){
					//	SipServletResponse resp = req.createResponse(200, "OK");
					//	if(logger.isDebugEnabled()){
					//		logger.debug("Sending response:\n" + resp);
					//	}
					//	resp.send();
					//}
				}else {
					if (loopBackEnabled.toLowerCase().equals("false")){
						if(logger.isDebugEnabled()){
							logger.debug("doNotify:Calling receivedTcap");
						}
						boolean result = receivedTcap(content, req);
						if (result){
							SipServletResponse resp = req.createResponse(200, "OK");
							if(logger.isDebugEnabled()){
								logger.debug("Sending response:\n" + resp);
							}
							resp.send();

							//replicate appSession once to  ensure it is available after FT for normal cleanup
							if(req.getAttribute(BEGIN_REQ) != null   && 
									!("true".equalsIgnoreCase((String) (req.getApplicationSession().getAttribute("CLOSED") ) ) ) ) {
								appDataReplicator.doReplicate(req.getApplicationSession());
							}
						}

					}else {
						//Start: Changes for loopback
						if(logger.isDebugEnabled()){
							logger.debug("doNotify: Load Testing-Executing hardcoded flow");
						}
						//						SccpUserAddress sua = diEvent.getDestinationAddress();
						//						SignalingPointCode spc = sua.getSubSystemAddress().getSignalingPointCode();	
						//						TcapProviderGateway tpg = TcapProviderGateway.getByAddress(spc);   

						if (loopBackScenario != null && loopBackScenario.toUpperCase().equals("IDP-ENC")){
							SipServletRequest ssr = factory.createRequest(factory.createApplicationSession(), "NOTIFY", source(), destination());
							ssr.setRequestURI(destination());

							if(ssr != null) {
								ssr.addHeader("Event", "tcap-event");
								ssr.addHeader("Subscription-State", "active");

								ssr.addHeader(JainTcapProviderImpl.DIALOG_ID, req.getHeader(JainTcapProviderImpl.DIALOG_ID));

								int id = diEvent.getDialogueId();

								if(operationCode.equalsIgnoreCase("0x00")) {
									if(logger.isDebugEnabled()){
										logger.debug("doNotify: Load Testing-Executing hardcoded flow: Received IDP, sending CONN");
									}
									byte[] baos = {(byte)0x01, (byte)0x06, (byte)0x0C, (byte)0x02, (byte)(id >>> 24), (byte)(id >>> 16), (byte)(id >>> 8), (byte)id, 
											(byte)0x1D, (byte)0x1E, (byte)0x02, (byte)0x23, (byte)0x00, (byte)0x22, (byte)0x01, (byte)0x1E, 
											(byte)0x01, (byte)0x1F, (byte)0x01, (byte)0x2E, (byte)0x20, (byte)0x02, (byte)0x21, (byte)0x10, 
											(byte)0x30, (byte)0x0E, (byte)0x80, (byte)0x07, (byte)0xAF, (byte)0x05, (byte)0xA0, (byte)0x03, 
											(byte)0x80, (byte)0x01, (byte)0x01, (byte)0xA1, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x01,
											(byte)0x1D, (byte)0x2B, (byte)0x02, (byte)0x23, (byte)0x01, (byte)0x22, (byte)0x01, (byte)0x1E, 
											(byte)0x01, (byte)0x1F, (byte)0x01, (byte)0x19, (byte)0x20, (byte)0x02, (byte)0x21, (byte)0x1D,
											(byte)0x30, (byte)0x1B, (byte)0x30, (byte)0x19, (byte)0x80, (byte)0x0F, (byte)0xAF, (byte)0x0D, 
											(byte)0xA0, (byte)0x0B, (byte)0xA0, (byte)0x09, (byte)0x0A, (byte)0x01, (byte)0x00, (byte)0x0A,
											(byte)0x01, (byte)0x02, (byte)0x0A, (byte)0x01, (byte)0x03, (byte)0x81, (byte)0x01, (byte)0x01,
											(byte)0xA2, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x02, (byte)0x1D, (byte)0x51, (byte)0x02,
											(byte)0x23, (byte)0x02, (byte)0x22, (byte)0x01, (byte)0x1E, (byte)0x01, (byte)0x1F, (byte)0x01, 
											(byte)0x17, (byte)0x20, (byte)0x02, (byte)0x21, (byte)0x43, (byte)0x30, (byte)0x41, (byte)0xA0,
											(byte)0x3F, (byte)0x30, (byte)0x06, (byte)0x80, (byte)0x01, (byte)0x07, (byte)0x81, (byte)0x01, 
											(byte)0x01, (byte)0x30, (byte)0x06, (byte)0x80, (byte)0x01, (byte)0x05, (byte)0x81, (byte)0x01, 
											(byte)0x00, (byte)0x30, (byte)0x0B, (byte)0x80, (byte)0x01, (byte)0x06, (byte)0x81, (byte)0x01, 
											(byte)0x00, (byte)0xBE, (byte)0x03, (byte)0x81, (byte)0x01, (byte)0x0A, (byte)0x30, (byte)0x0B,
											(byte)0x80, (byte)0x01, (byte)0x09, (byte)0x81, (byte)0x01, (byte)0x01, (byte)0xA2, (byte)0x03, 
											(byte)0x80, (byte)0x01, (byte)0x01, (byte)0x30, (byte)0x0B, (byte)0x80, (byte)0x01, (byte)0x09,
											(byte)0x81, (byte)0x01, (byte)0x01, (byte)0xA2, (byte)0x03, (byte)0x80, (byte)0x01, (byte)0x02, 
											(byte)0x30, (byte)0x06, (byte)0x80, (byte)0x01, (byte)0x0A, (byte)0x81, (byte)0x01, (byte)0x01, 
											(byte)0x1D, (byte)0x3D, (byte)0x02, (byte)0x23, (byte)0x03, (byte)0x22, (byte)0x01, (byte)0x1E, 
											(byte)0x01, (byte)0x1F, (byte)0x01, (byte)0x14, (byte)0x20, (byte)0x02, (byte)0x21, (byte)0x2F, 
											(byte)0x30, (byte)0x2D, (byte)0xA0, (byte)0x09, (byte)0x04, (byte)0x07, (byte)0x02, (byte)0x10, 
											(byte)0x04, (byte)0x10, (byte)0x00, (byte)0x01, (byte)0x50, (byte)0xAA, (byte)0x18, (byte)0x30,
											(byte)0x16, (byte)0x02, (byte)0x01, (byte)0xFF, (byte)0x0A, (byte)0x01, (byte)0x00, (byte)0xA1, 
											(byte)0x0E, (byte)0x30, (byte)0x0C, (byte)0xA1, (byte)0x0A, (byte)0x80, (byte)0x08, (byte)0x02, 
											(byte)0xFA, (byte)0x05, (byte)0xFE, (byte)0x03, (byte)0x00, (byte)0x02, (byte)0x31, (byte)0xAF, 
											(byte)0x06, (byte)0x87, (byte)0x01, (byte)0x00, (byte)0x8B, (byte)0x01, (byte)0x00, (byte)13, (byte)10};

									this.addSeqDlgParameter(ssr,1,id);
									ssr.addHeader("TC-Seq", "1");	
									ssr.setContent(baos, "application/tcap");
									ssr.send();
								} else if(operationCode.equalsIgnoreCase("0x18")) {
									if(logger.isDebugEnabled()){
										logger.debug("doNotify: Load Testing-Executing hardcoded flow: Received ERB, sending TC-END");
									}
									//dialog type dialog id tag dialog id
									byte[] baos = {(byte)0x01, (byte)0x06 , (byte)0x0D, (byte)0x02, (byte)(id >>> 24), (byte)(id >>> 16), (byte)(id >>> 8), (byte)id, (byte)13, (byte)10};

									this.addSeqDlgParameter(ssr,2,id);
									ssr.addHeader("TC-Seq", "2");	
									ssr.setContent(baos, "application/tcap");
									ssr.send();
									req.getApplicationSession().setAttribute("CLOSED","true");
								}			
							}
							SipServletResponse resp = req.createResponse(200, "OK");
							if(logger.isDebugEnabled()){
								logger.debug("Sending response:\n" + resp);
							}
							resp.send();
						}else if (loopBackScenario != null && loopBackScenario.toUpperCase().equals("REL")){
							SipServletRequest ssr = factory.createRequest(factory.createApplicationSession(), "NOTIFY", source(), destination());
							ssr.setRequestURI(destination());

							if(ssr != null) {
								ssr.addHeader("Event", "tcap-event");
								ssr.addHeader("Subscription-State", "active");

								ssr.addHeader(JainTcapProviderImpl.DIALOG_ID, req.getHeader(JainTcapProviderImpl.DIALOG_ID));

								int id = diEvent.getDialogueId();

								if(operationCode.equalsIgnoreCase("0x00")) {
									if(logger.isDebugEnabled()){
										logger.debug("doNotify: Load Testing-Executing hardcoded flow: Received IDP, sending REL");
									}
									byte[] baos = {(byte)0x01, (byte)0x06, (byte)0x0D, (byte)0x02, (byte)(id >>> 24), (byte)(id >>> 16), 
											(byte)(id >>> 8), (byte)id, (byte)0x1D, (byte)0x12, (byte)0x02, (byte)0x23, (byte)0x00, 
											(byte)0x22, (byte)0x01, (byte)0x1E, (byte)0x01, (byte)0x1F, (byte)0x01, (byte)0x16, 
											(byte)0x20, (byte)0x02, (byte)0x21, (byte)0x04, (byte)0x04, (byte)0x02, (byte)0x83, 
											(byte)0xA9, (byte)13, (byte)10};
									this.addSeqDlgParameter(ssr,1,id);
									ssr.addHeader("TC-Seq", "1");	
									ssr.setContent(baos, "application/tcap");
									ssr.send();
									req.getApplicationSession().setAttribute("CLOSED","true");
								}
							}
							SipServletResponse resp = req.createResponse(200, "OK");
							if(logger.isDebugEnabled()){
								logger.debug("Sending response:\n" + resp);
							}
							resp.send();
						}
					}
				}
			}
			//end: Changes for loopback

			//invalidate validate if service marked closed and no pending notidfies
			Object pendingNotify =  req.getApplicationSession().getAttribute("PENDING_NOTIFY");
			if (req.getApplicationSession().isValid()
					&& "true".equalsIgnoreCase((String) (req.getApplicationSession()
							.getAttribute("CLOSED"))) && (pendingNotify == null ||  ( (Integer)pendingNotify <= 0) )   ) {
				if (logger.isDebugEnabled()) {
					logger.debug("invalidate appSession on doNOtify");
				}
				req.getApplicationSession().invalidate();
			}
		}


		catch (MandatoryParameterNotSetException e)
		{
			//synchronized (rsnHandler.processingRSN) {
			//	rsnHandler.processingRSN = false;	
			//}
			tcapMessageFailed(diEvent, true);
			SipServletResponse resp = req.createResponse(400, "Invalid TCAP content");
			if(logger.isDebugEnabled()){
				logger.debug(e.getMessage(), e);
				logger.debug("MandatoryParameterNotSetException sending:\n" + resp);
			}
			logger.error("Total time spent in parseAndPrepare dialogues and components : " + timeTakenToParse);
			resp.send();
			//invalidate AS
			req.getApplicationSession().invalidate();
		}
		catch (TcapContentReaderException e )
		{
			//synchronized (rsnHandler.processingRSN) {
			//	rsnHandler.processingRSN = false;	
			//}
			tcapMessageFailed(diEvent, true);
			SipServletResponse resp = req.createResponse(400, "Invalid TCAP content");
			if(logger.isDebugEnabled()){
				logger.debug(e.getMessage(), e);
				logger.debug("TcapContentReaderException sending:\n" + resp);
			}
			logger.error("Total time spent in parseAndPrepare dialogues and components : " + timeTakenToParse);
			resp.send();
			req.getApplicationSession().invalidate();
		}
		catch (ListenerNotRegisteredException e)
		{	
			if(!req.isCommitted()){
				if (logger.isDebugEnabled()) {
					logger.debug("Response pending for request send error response.");
				}
				SipServletResponse resp = req.createResponse(481, "No Sccp User For This Service");
				if (logger.isDebugEnabled()) {
					logger.debug(e.getMessage(), e);
					logger.debug("ListenerNotRegisteredException sending:\n" + resp);
				}
				logger.error("Total time spent in parseAndPrepare dialogues and components : " + timeTakenToParse);
				resp.send();
				req.getApplicationSession().invalidate();
			}else{
				if (logger.isDebugEnabled()) {
					logger.debug("request is already commited");
				}
			}
		}
		catch (Exception e) {
			//The exception block is added to appropriately update the flag for RSN processing to know
			//the current processing state of RSN message.
			logger.error(e.getMessage(), e);
			//synchronized (rsnHandler.processingRSN) {
			//	rsnHandler.processingRSN = false;	
			//}
			if (e instanceof IOException){
				throw (IOException)e;
			}

			SipServletResponse resp = req.createResponse(400, e.getMessage());
			logger.error("Exception occured sending:\n" + resp);
			logger.error("Total time spent in parseAndPrepare dialogues and components : " + timeTakenToParse);
			resp.send();

			req.getApplicationSession().invalidate();
		}
		TcapProviderGateway tpg = TcapProviderGateway.getTcapProviderGateway(req);
		if(tpg != null){
			tpg.activeConnection(req);
		}

		if (isBegin && sAvgTimeCalcCount > 0) {
			TcapSession ts = replicator.getTcapSession(dialogueId);
			String appName = null;
			if(ts != null && ts.getAttribute(AppName) != null) {
				appName =  (String) ts.getAttribute(AppName); 
			}
			calculateAvgTime(startTimeMilliSec, dialogueId, appName);
		}

		if (ftTesting.equalsIgnoreCase("After_Sending_200Ok")){
			System.exit(0);
		}

	}

	protected void doInfo(SipServletRequest req) throws IOException
	{
		try
		{
			if(logger.isDebugEnabled()){
				logger.debug("doInfo: "+ req.toString());
			}

			byte [] content = (byte[])req.getContent();			 
			int length = content.length;
			byte [] tcapContent = new byte[length-2]; // remove the last 2 octects (\r\n)
			System.arraycopy(content, 0, tcapContent, 0, length-2);

			TcapType msg = TcapParser.parseSCCPMgmtMsg(tcapContent, this);

			if(msg.getStateIndEvent() == null) {
				TcapProviderGateway tpg = TcapProviderGateway.getTcapProviderGateway(req);
				tpg.setAddress(tcapContent);

				SipServletResponse resp = req.createResponse(200, "OK");
				if(logger.isDebugEnabled()){
					logger.debug("Sending response:" + resp);
				}
				resp.send();
			} else {
				if (msg.getStateIndEvent() instanceof NPCStateIndEvent){
					NPCStateIndEvent npc = (NPCStateIndEvent) msg.getStateIndEvent();
					if(logger.isDebugEnabled()){
						logger.debug("NPCStateIndEvent Parsed");
					}
					if (npc.getSignalingPointStatus() == SignalingPointStatus.SP_CONGESTED){
						boolean added = congestedPointCodes.add(npc.getAffectedDpc().toString());
						if (added)
							logger.error("Point Code is congested");
						else
							logger.error("Point Code is already congested");
					}else if (npc.getSignalingPointStatus() == SignalingPointStatus.SP_DECONGESTED){
						boolean removed = congestedPointCodes.remove(npc.getAffectedDpc().toString());
						if (removed)
							logger.error("Point Code is de-congested");
						else
							logger.error("Point Code was not congested before, though de-congested");
					}
				}
				/* content is valid, send OK */
				boolean result = receivedTcap(msg, req);
				if (result){
					SipServletResponse resp = req.createResponse(200, "OK");
					if(logger.isDebugEnabled()){
						logger.debug("Sending response:\n" + resp);
					}
					resp.send();
				}
			}
		}

		catch (TcapContentReaderException e)
		{
			SipServletResponse resp = req.createResponse(400, "Invalid TCAP content");
			if(logger.isDebugEnabled()){
				logger.debug( e.getMessage(), e);
				logger.debug("TcapContentReaderException sending:\n" + resp);
			}
			resp.send();
		}

		catch (MandatoryParameterNotSetException e)
		{
			SipServletResponse resp = req.createResponse(400, "Invalid TCAP content");
			if(logger.isDebugEnabled()){
				logger.debug( e.getMessage(), e);
				logger.debug("MandatoryParameterNotSetException sending:\n" + resp);
			}
			resp.send();
		}

		catch (ParameterNotSetException e)
		{
			SipServletResponse resp = req.createResponse(400, "Invalid TCAP content");
			if(logger.isDebugEnabled()){
				logger.debug(e.getMessage(), e);
				logger.debug( "ParameterNotSetException sending:\n" + resp);
			}
			resp.send();
		}
		catch (ListenerNotRegisteredException e)
		{
			SipServletResponse resp = req.createResponse(481, "No Sccp User For This Service");
			if(logger.isDebugEnabled()){
				logger.debug( e.getMessage(), e);
				logger.debug("ListenerNotRegisteredException sending:\n" + resp);
			}
			resp.send();
		} 
		TcapProviderGateway tpg = TcapProviderGateway.getTcapProviderGateway(req);
		tpg.activeConnection(req);
	}

	public void sendTcapContent(DialogueReqEvent dre, List<ComponentReqEvent> components) throws TcapContentWriterException, IOException, TcapContentReaderException, MandatoryParameterNotSetException
	{
		if(logger.isDebugEnabled()){
			logger.debug("Inside sendTcapContent");
		}
		SccpUserAddress sua = null;
		Integer dlgId =getTCCorrelationId(dre.getDialogueId());

		int tcCorrId=-1;
		if(dlgId==null){
			dlgId=dre.getDialogueId();
		}else
			tcCorrId=dlgId;

		TcapSession ts = replicator.getTcapSession(dlgId);

		boolean tsCreated = false;
		if(logger.isDebugEnabled()){
			logger.debug("Inside got ts form replicator as::"+ts);
		}

		if (ts == null ||ts.getAttribute(SccpUserAddressAttr)==null)
		{
			try
			{
				sua = dre.getOriginatingAddress();
			}
			catch (ParameterNotSetException pnse)
			{
				throw new MandatoryParameterNotSetException("no originating address in DialogueReqEvent on non-established dialogue.", pnse);
			}
		}
		else
		{
			sua = (SccpUserAddress)ts.getAttribute(SccpUserAddressAttr);
		}

		if(logger.isDebugEnabled()){
			logger.debug("Inside got sua from tc/dre as::"+sua);
		}

		SignalingPointCode spc;
		try
		{
			spc = sua.getSubSystemAddress().getSignalingPointCode();
		}

		catch (ParameterNotSetException pnse)
		{
			throw new MandatoryParameterNotSetException("no SubSystemAddress in SccpUserAddress address for DialogueReqEvent.", pnse);
		}
		if(logger.isDebugEnabled()){
			logger.debug("Inside got spc form sua::"+spc);
		}

		TcapProviderGateway tpg = TcapProviderGateway.getByAddress(spc);
		if(tpg == null || !tpg.isConnected()){
			if(logger.isDebugEnabled()){
				logger.debug("tpg is::["+tpg+"]");
				logger.debug("tpg list is:::"+TcapProviderGateway.printDebugInfo(null,true));
			}
			throw new IOException("Not connected to the IN Gateway");
		}
		if (ts == null && protocol == PROTOCOL_ITU) {
			Iterator<ComponentReqEvent> iComponent = components.iterator();
			while (iComponent.hasNext())
			{
				ComponentReqEvent ciet = (ComponentReqEvent)iComponent.next();		// to be analysed
				InvokeReqEvent receivedInvoke = (InvokeReqEvent)ciet; 	// to be analysed
				Operation opr = receivedInvoke.getOperation();
				byte[] opCode = opr.getOperationCode();
				String opCodeStr = formatBytes(opCode);
				if(opCodeStr.equalsIgnoreCase("0x00")){
					byte[] parms ;
					try{
						parms = receivedInvoke.getParameters().getParameter();
					}catch (ParameterNotSetException pnse)
					{
						throw new MandatoryParameterNotSetException("No param set in Idp.");
					}
					//service can be in one or 2 bytes so modified
					int serviceKey = -1;
					if ((parms[2] & 0xff) == 128) {
						if (parms[3] == 1) {
							serviceKey = parms[4];
						} else if (parms[3] == 2) {
							serviceKey = ((parms[4] & 0xff) << 8);
							serviceKey = (serviceKey | (parms[5] & 0xff));
						}
					} 					

					String srvKey = new Integer(serviceKey).toString();
					//ignore the state as service triigers this flow
					JainTcapListener jtl = tcapAppRegistry.getListenerForSrvKey(srvKey,true);
					//for ITU T standard sk is not reqd for service registartion so adding sua support
					//done to support SUA
					//XXX think for multiple suas
					if(jtl== null){
						//default as first listener for sua
						//UAT-840 Changes
						//jtl = tcapAppRegistry.getListenerForSUA(sua.toString()).get(0);
						jtl = tcapAppRegistry.getListenerForSUA(sua.getString()).get(0);
					}
					ts = replicator.getTcapSession(dre.getDialogueId(), jtl);
					ts.setAttribute(SccpUserAddressAttr, sua);
					ts.setAttribute(ListenerApp, jtl);


					if(tpg.getSasId() == null) {
						tpg.setSasId(((TcapListener)jtl).getInviteSessionId());
					}
					break ;
				}						
			}
		} else if (ts == null && protocol == PROTOCOL_ANSI) {
			if (logger.isDebugEnabled()) {
				logger.debug("Outgoing message ts null and protocol ANSI");
			}
			if (dre.getPrimitiveType() == TcapConstants.PRIMITIVE_BEGIN
					|| dre.getPrimitiveType() == TcapConstants.PRIMITIVE_UNIDIRECTIONAL) {
				if (logger.isDebugEnabled()) {
					logger.debug("BEIN or unidirectional");
				}
				JainTcapListener jtl = tcapAppRegistry.getListenerForSUA(
						sua.toString()).get(0);

				ts = replicator.getTcapSession(dre.getDialogueId(), jtl);
				tsCreated = true;
				ts.setAttribute(SccpUserAddressAttr, sua);
				ts.setAttribute(ListenerApp, jtl);

				if(ts!=null && tcCorrId>0)
					ts.setTcCorrelationId(tcCorrId);

				if (tpg.getSasId() == null) {
					tpg.setSasId(((TcapListener) jtl).getInviteSessionId());
				}
			}
		}// @End if ts == null
		if(logger.isDebugEnabled()){
			logger.debug("sendTcapContent from " + sua);
		}

		/**
		 * Added for WIN  protocol updating dialogue id in outgoing BEGIN for example CCDIR
		 */
		if(ts!=null){
			TcapSessionImpl tsi=(TcapSessionImpl)ts;
			tsi.setDialogueId(dre.getDialogueId());
		}

		boolean relay=false;
		if(ts.getAttribute(MRS_RELAY)!=null){
			relay=true;
		}

		tpg.sendTcapContent(dre, components,this,relay);

		if (dre.endsDialogue())
		{
			try
			{
				//This is done when Service sends the Connect in TC_END for handoff scenario, attribute would be set by
				//service in this case. Attribute check is added as Connect can be sent in TC_END in other cases like 
				//when SN wants to remove itself from the call path
				//removed ts== null|| check as ts cannot be null
				if ( ts != null && ts.getAttribute(Constants.FOR_HANDOFF) == null){
					//Closing Tcap Session here as tcap session needed in case INVITE doesn't come and timer gets expired

					ts.invalidate();

				}else{
					if(logger.isDebugEnabled()){
						logger.debug( "Listener have send the TC-END for handoff scenario with Connect, " +
								"thus not invalidating the App and Tcap Session ");
					}
					//call counter will decrement on tcapsession invalidate after handoff
				}


			}
			catch (IdNotAvailableException inae)
			{
				logger.error("Don't have dialogue " + dre.getDialogueId(), inae);
			}
		}
	}

	public void sendStateInformation(StateReqEvent sre) throws MandatoryParameterNotSetException,
	IOException
	{
		try
		{
			NStateReqEvent nsre = (NStateReqEvent)sre;
			SccpUserAddress sua = nsre.getAffectedUser();
			TcapProviderGateway tpg = TcapProviderGateway.getByAddress(sua.getSubSystemAddress().getSignalingPointCode());
			if(tpg == null || !tpg.isConnected()){
				throw new IOException("Not connected to the IN Gateway::"+sua.getSubSystemAddress().getSignalingPointCode());
			}

			//Bug 9312 (set SUA status to out of service even before sending INFO to INC)
			if(nsre.getUserStatus() == SccpConstants.USER_OUT_OF_SERVICE) {
				List<SccpUserAddress> list = tpg.getSSAList();
				for(SccpUserAddress addr : list) {
					//UAT-840 Changes -  We are not relying on GT
					if(addr.getString().equals(nsre.getAffectedUser().getString())) {
						addr.setSuaStatus(nsre.getUserStatus());
					}
				}
			}

			if(!simulator ) {				
				tpg.sendStateInformation(sre);
			}
		}

		catch (ParameterNotSetException pnse)
		{
			throw new MandatoryParameterNotSetException("Need a subsytem address in sccp address", pnse);
		}
	}

	/**
	 *  Returns a unique Dialogue Id to initiate a dialogue with another TCAP user.
	 *
	 * @return the new Dialogue Id returned by the underlying TCAP layer
	 * @exception  IdNotAvailableException  if a new Dialogue Id is not available
	 */
	public int getNewDialogueId() throws IdNotAvailableException
	{
		int id;
		synchronized(InvocationMap)
		{
			int startDialogue = DialogueId;
			int tryDialogue = ++DialogueId;
			while (true)
			{
				if (tryDialogue == startDialogue)
					throw new IdNotAvailableException("dialogue are all used");

				if (tryDialogue > MaxDialougues) {
					logger.error("Max limit reached for dialogid resetiing to  "
							+ UserDialogueStart);

					tryDialogue = UserDialogueStart;
				}
				if (InvocationMap.get(tryDialogue) == null)
					break;

				tryDialogue++;

				if (tryDialogue > MaxDialougues)
					tryDialogue = UserDialogueStart;
			}

			DialogueId = tryDialogue;
			InvocationMap.put(DialogueId, 0);
			id = DialogueId;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("getNewDialogueId Returning "+ id );
		}
		return id;
	}

	/**
	 * Release the dialogue Id back to the system. <br>
	 * <b>Note:</b> In some SS7 stacks the TCAP Dialogue Id is automatically
	 * released following the end of a TCAP transaction. In such instances the
	 * implementation of this method may be left null.
	 *
	 * @param  dialogueId  the dialogue Id supplied to the method
	 * @exception  IdNotAvailableException  if a new Dialogue Id is not available
	 */
	public void releaseDialogueId(int dialogueId) throws IdNotAvailableException
	{
		synchronized(InvocationMap)
		{
			InvocationMap.remove(dialogueId);
		}
	}

	/**
	 * Returns a unique Invoke Id for identifying invoke requests within the
	 * dialogue identified by the supplied Dialogue Id. Each dialogue between two
	 * <CODE>JainTcapListeners</CODE> is identified by a unique Dialogue Id. Note
	 * that the returned Invoke Id will be unique for a particular Dialogue Id.
	 *
	 * @param  dialogueId the dialogue Id supplied to the method
	 * @return an unused unique Invoke Id
	 * @exception  IdNotAvailableException  if an invoke Id is not available to the
	 * specified dialogue Id
	 */
	public int getNewInvokeId(int dialogueId)
	{
		int id;
		synchronized(InvocationMap){
			// throws IdNotAvailableException;
			Integer invokeId = InvocationMap.get(DialogueId);
			invokeId = invokeId + 1;
			InvocationMap.put(DialogueId, invokeId);
			id = invokeId;
		}
		return id;
	}

	/**
	 * Releases the unique Invoke Id, allowing it to be reused within the dialogue
	 * identified by the supplied Dialogue Id.
	 *
	 * @param  invokeId    the invoke Id to be released
	 * @param  dialogueId  the dialogue Id from which to release the dialogue Id
	 * @exception  IdNotAvailableException  if an invoke Id is not available to the
	 * specified dialogue Id
	 */
	public void releaseInvokeId(int invokeId, int dialogueId)
	{
		// throws IdNotAvailableException;
		/* TODO: do i really have to track these? */
	}

	public void setTCCorrelationId(int dialogueId,int correlationId){
		tcCorrIdMap.put(dialogueId, correlationId);
	}

	/**
	 * 
	 * @param dialogueId
	 * @return
	 */
	public Integer getTCCorrelationId(int dialogueId){
		return tcCorrIdMap.get(dialogueId);
	}

	/**
	 * 
	 * @param dialogueId
	 * @return
	 */
	public Integer removeTCCorrelationId(int dialogueId){
		return tcCorrIdMap.remove(dialogueId);
	}


	/**
	 * Sends a Component Request primitive into the TCAP layer of the SS7 protocol
	 * stack.
	 *
	 * @param  event the new component event supplied to the method
	 * @exception  MandatoryParameterNotSetException  thrown if all of the mandatory
	 * parameters required by this JainTcapProviderImpl, to send the Component
	 * Request are not set. <BR>
	 * Note that different implementations of this JainTcapProvider interface
	 * will mandate that different parameters must be set for each <CODE>
	 * ComponentReqEvent</CODE> . It is recommended that the message detail
	 * returned in the <CODE>MandatoryParameterNotSetException</CODE> should
	 * be a <CODE>String</CODE> of the form: <br>
	 * <CENTER><B>"Parameter: <parameterName> not set"</B></CENTER>
	 */
	public void sendComponentReqEvent(ComponentReqEvent event) throws MandatoryParameterNotSetException
	{
		incrementTcapCounters(event);
		storeComponentRequest(event);
	}

	/**
	 * Sends a Dialogue Request primitive into the TCAP layer of the SS7 protocol
	 * stack. This will trigger the transmission to the destination node of the
	 * Dialogue request primitive along with any associated Component request
	 * primitives that have previously been passed to this JainTcapProviderImpl.
	 * Since the same JainTcapProviderImpl will be used to handle a particular
	 * transaction, Dialogue Request Events with the same Originating Transaction
	 * Id must be sent to the same JainTcapProviderImpl.
	 *
	 * @param  event the new dialogue event supplied to the method
	 * @exception  MandatoryParameterNotSetException  thrown if all of the mandatory
	 * parameters required by this JainTcapProviderImpl to send the Dialogue
	 * Request are not set. <p>
	 *
	 * <b>Note to developers</b> :- different implementations of this
	 * JainTcapProvider interface will mandate that different parameters must
	 * be set for each <CODE>DialogueReqEvent</CODE> . It is recommended that
	 * the detail message returned in the <CODE>
	 * MandatoryParameterNotSetException</CODE> should be a <CODE>String
	 * </CODE>of the form: <P>
	 * <CENTER><B>"Parameter: <parameterName> not set"</B></CENTER>
	 */
	public void sendDialogueReqEvent(DialogueReqEvent event) throws MandatoryParameterNotSetException,
	IOException
	{
		try
		{
			TcapSession tcapSession = getTcapSession(event.getDialogueId());
			if(tcapSession!=null && isSS7MsgInfoEnabled){
				tcapSession.updateReqEventPrintInfo(null,getCurrentComponentsFor(event), event.getPrimitiveType());

			}
			incrementTcapCounters(event);
			sendTcapContent(event, getCurrentComponentsFor(event));
		}

		catch (TcapContentWriterException tcwe)
		{
			logger.error( "TcapContentWriterException encoutered during sendDialogueReqEvent.", tcwe);
			throw new IOException("TcapContentWriterException encoutered during sendDialogueReqEvent. " + tcwe.getMessage());
		}
		catch (TcapContentReaderException tcwe)
		{
			logger.error("TcapContentReaderException encoutered during sendDialogueReqEvent.", tcwe);
			throw new IOException("TcapContentReaderException encoutered during sendDialogueReqEvent. " + tcwe.getMessage());
		}

		finally
		{
			clearComponentRequest(event);
		}
	}

	public void sendStateReqEvent(StateReqEvent event) throws MandatoryParameterNotSetException,
	IOException
	{
		sendStateInformation(event);
	}

	/**
	 * Adds a <a href="JainTcapListener.html">JainTcapListener</a> to the list of
	 * registered Event Listeners of this JainTcapProviderImpl.
	 *
	 * @param  listener the feature to be added to the JainTcapListener attribute
	 * @param  userAddress the feature to be added to the JainTcapListener attribute
	 * @exception  TooManyListenersException thrown if a limit is
	 * placed on the allowable number of registered JainTcapListeners, and
	 * this limit is exceeded.
	 * @exception  ListenerAlreadyRegisteredException thrown if the listener
	 * listener supplied is already registered
	 * @exception  InvalidUserAddressException thrown if the user address
	 * supplied is not a valid address
	 *
	 * @deprecated As of JAIN TCAP version 1.1. This method is replaced by the
	 * {@link #addJainTcapListener(JainTcapListener, SccpUserAddress)} method.
	 */
	public void addJainTcapListener(JainTcapListener listener, TcapUserAddress userAddress)
	{
		logger.error("This API is Deprecated!! Instead use addJainTcapListener(JainTcapListener listener, List<SccpUserAddress> sccpUserAddress, List<String> serviceKey)");
	}

	/**
	 * Adds a <a href="JainTcapListener.html">JainTcapListener</a> to the list of
	 * registered Event Listeners of this JainTcapProviderImpl.
	 *
	 * @param  listener the feature to be added to the JainTcapListener attribute
	 * @param  userAddress the feature to be added to the JainTcapListener attribute
	 * @exception  TooManyListenersException thrown if a limit is
	 * placed on the allowable number of registered JainTcapListeners, and
	 * this limit is exceeded.
	 * @exception  ListenerAlreadyRegisteredException thrown if the listener
	 * listener supplied is already registered
	 * @exception  InvalidAddressException thrown if the user address
	 * supplied is not a valid address
	 */
	public void addJainTcapListener(JainTcapListener listener, SccpUserAddress userAddress) throws ListenerAlreadyRegisteredException,
	InvalidAddressException, IOException
	{
		logger.error("This API has been made Deprecated!! Instead use addJainTcapListener(JainTcapListener listener, List<SccpUserAddress> sccpUserAddress, List<String> serviceKey)");
	}

	/**
	 * Adds a <a href="JainTcapListener.html">JainTcapListener</a> to the list of
	 * registered Event Listeners of this JainTcapProviderImpl.
	 *
	 * @param  listener the feature to be added to the JainTcapListener attribute
	 * @param  userAddress the feature to be added to the JainTcapListener attribute
	 * @param  serviceKey the feature to be added to the JainTcapListener attribute
	 * @param  applicationName the feature to be added to the JainTcapListener attribute
	 * @exception  TooManyListenersException thrown if a limit is
	 * placed on the allowable number of registered JainTcapListeners, and
	 * this limit is exceeded.
	 * @exception  ListenerAlreadyRegisteredException thrown if the listener
	 * listener supplied is already registered
	 * @exception  InvalidAddressException thrown if the user address
	 * supplied is not a valid address
	 */
	public void addJainTcapListener(JainTcapListener listener, List<SccpUserAddress> sccpUserAddress, List<String> serviceKey)
			throws TooManyListenersException, ListenerAlreadyRegisteredException, InvalidAddressException, InvalidListenerConfigException, IOException {

		if(listener == null) {
			throw new InvalidListenerConfigException("JainTcapListener cannot be NULL.");
		}

		String applicationName = ((TcapListener)listener).getName();
		String appVersion = ((TcapListener)listener).getVersion();

		//BUG: 9936 TCAP FT Changes in App Class Loader
		AppClassLoader tcapProviderLoader = (AppClassLoader)host.getTcapProviderCL();
		List<ClassLoader> tcapLoaderSiblings = tcapProviderLoader.getSiblings();
		if(logger.isDebugEnabled()){
			logger.debug("tcapProviderLoader: " + tcapProviderLoader);
		}

		tcapLoaderSiblings.add(listener.getClass().getClassLoader());
		if(logger.isDebugEnabled()){
			logger.debug("Listener " + ((TcapListener)listener).getName() + " Loader: " + listener.getClass().getClassLoader() +" Listener Class is "+listener.getClass().getName());
		}

		boolean upgrade = false;

		// Validation ANSI or ITU 
		//Rather than deciding the protocol on the basis of whether service key is present for the 
		//first listener or not. It is being decided that it should be picked from the configuration
		//in order to avoid scenarios like service key provisioning has been missed out and first 
		//listener (VPN) has been deployed. 
		//		if(listenerCount == 0) {
		//			if(serviceKey == null || serviceKey.size() == 0) {
		//				protocol = PROTOCOL_ANSI;
		//			} else {
		//				protocol = PROTOCOL_ITU;
		//			}
		//		} else {

		// CO - Changes 
		if (protocol == PROTOCOL_ITU
				&& (serviceKey == null || serviceKey.size() == 0)) {
			throw new InvalidListenerConfigException(
					"Service Key MUST be present.");
		} else if ((protocol == PROTOCOL_ANSI
				&& (serviceKey != null && serviceKey.size() > 0)) && !ignoreProtocolCheck) {
			throw new InvalidListenerConfigException(
					"Service Key MUST NOT be present.");
		}
		//		}

		// Validation for presence of SUA
		if(sccpUserAddress == null || sccpUserAddress.size() == 0)
			throw new InvalidAddressException("No SccpUserAddress provided.");

		// Check for upgrade
		String version = tcapAppRegistry.getActiveVersionForAppName(applicationName);
		if(version == null) {
			if(logger.isDebugEnabled()){
				logger.debug("No application deployed yet with name : " + applicationName);
			}
			tcapAppRegistry.addActiveVersionForAppName(applicationName, appVersion);			
		} else {
			if(logger.isDebugEnabled()){
				logger.debug("Upgrade of application with name : " + applicationName);
			}
			tcapAppRegistry.addActiveVersionForAppName(applicationName, appVersion);	
			upgrade = true;
		}

		// Listener mapping with application name
		//ignore as we need to validate alrdy regiterd listeners
		JainTcapListener jtl = tcapAppRegistry.getListenerForAppName(applicationName, true);
		if (upgrade == true || jtl == null )
		{
			if(logger.isDebugEnabled()){
				logger.debug("Listener mapped to application name : " + applicationName);
			}
			tcapAppRegistry.addListenerForAppName(applicationName, listener);
		}
		else{
			logger.error("Some Listener Already Registered with Application Name : " + applicationName);
			throw new ListenerAlreadyRegisteredException();
		}

		// Listener mapping with service key
		if(protocol == PROTOCOL_ITU || ignoreProtocolCheck) {
			if(serviceKey != null){
				for(int i = 0; i < serviceKey.size(); i++){
					String srvKey = serviceKey.get(i);
					//ignore as we need to validate alrdy regiterd listeners
					JainTcapListener jtl1 = tcapAppRegistry.getListenerForSrvKey(srvKey, true);
					if (upgrade == true || jtl1 == null)
					{
						if(logger.isDebugEnabled()){
							logger.debug("Listener mapped to Service Key : " + srvKey);
						}
						tcapAppRegistry.addListenerForSrvKey(srvKey, listener);
					}
					else {
						logger.error("Some Listener Already Registered for Service Key : " + srvKey);
						throw new ListenerAlreadyRegisteredException();
					}
				}
			}
		}

		// Listener mapping with SUA
		for(int i = 0; i < sccpUserAddress.size(); i++){
			SccpUserAddress userAddress = sccpUserAddress.get(i);


			//UAT-840
			//List<JainTcapListener>  listenerList = tcapAppRegistry.getListenerForSUA(userAddress.toString());
			List<JainTcapListener>  listenerList = tcapAppRegistry.getListenerForSUA(userAddress.getString());

			if(logger.isDebugEnabled()){
				logger.debug("listenerList mapped to userAddress : " + userAddress + " is : " + listenerList);
			}
			if (listenerList == null)
			{
				if(logger.isDebugEnabled()){
					logger.debug("Listener mapped to SUA - " + userAddress.toString());
				}
				List<JainTcapListener> list = new ArrayList<JainTcapListener>();
				list.add(listener);
				//UAT-840 Changes
				//tcapAppRegistry.addListenerForSUA(userAddress.toString(), list);
				tcapAppRegistry.addListenerForSUA(userAddress.getString(), list);
			}
			else {
				if(logger.isDebugEnabled()){
					logger.debug("Changes done for ANSI");
				}
				/*if(upgrade == false && protocol == PROTOCOL_ANSI) {
					logger.error("Some Listener Already Registered for SUA");
					throw new ListenerAlreadyRegisteredException();
				}*/
				if(logger.isDebugEnabled()){
					logger.debug("Some Listener already registered for same SUA, so checking its existing Listener or new Listener getting registered..");
				}
				//throw new ListenerAlreadyRegisteredException();
				String classListener = listener.getClass().getName();
				boolean listenerFound = false ;
				int index = -1;
				for (JainTcapListener listener1 :listenerList){
					if(listener1.getClass().getName().equalsIgnoreCase(classListener)){
						listenerFound = true ;
						if(upgrade == true) {
							index = listenerList.indexOf(listener1);
						} else {														// upgrade = false & protocol = ITU	
							index = listenerList.indexOf(listener1);
							logger.warn("This Listener getting  Registered for this SUA for the mutilple times .. so just updating its instance");
							//throw new ListenerAlreadyRegisteredException();
						}
					}
				}

				if(index != -1) {
					listenerList.remove(index);
					listenerList.add(listener);
				}

				if(!listenerFound) {
					listenerList.add(listener);
					//UAT-840 Changes
					//tcapAppRegistry.addListenerForSUA(userAddress.toString(), listenerList);
					tcapAppRegistry.addListenerForSUA(userAddress.getString(), listenerList);
				}

			}

			logger.log(Level.ERROR, "replicator.isSASActive():" + TcapSessionReplicator.isSASActive());
			if (TcapSessionReplicator.isSASActive())
			{

				TcapProviderGateway tpg = null;
				try
				{
					tpg = TcapProviderGateway.getByAddress(userAddress.getSubSystemAddress().getSignalingPointCode());
				}
				catch (ParameterNotSetException e)
				{
					throw new InvalidAddressException("User address improperly formatted or no GTT support");
				}

				if (tpg != null && tpg.isConnected()){
					List<SccpUserAddress> suaList = tpg.getSSAList();
					int status = SccpConstants.USER_OUT_OF_SERVICE;
					try {
						for (SccpUserAddress sua : suaList) {
//							if(sua.getProtocolVariant() == userAddress.getProtocolVariant() &&
//									sua.getSubSystemAddress().getSubSystemNumber() == userAddress.getSubSystemAddress().getSubSystemNumber() &&
//									sua.getSubSystemAddress().getSignalingPointCode().toString().equals(userAddress.getSubSystemAddress().getSignalingPointCode().toString())) {
							if(sua.getString().equals(userAddress.getString())){
								status = sua.getSuaStatus();
								if(logger.isDebugEnabled()){
									logger.debug("Got SUA Status - " +status);
								}
							}
						}
					}catch(Exception e){
						logger.error("Exception checking the SUA status" + e);
					}
//					} catch(MandatoryParameterNotSetException ex) {
//						logger.error("An exception occured while checking the SUA status" + ex);
//					} catch (ParameterNotSetException ex) {
//						logger.error("An exception occured while checking the SUA status" + ex);
//					}

					if(status == SccpConstants.USER_IN_SERVICE)  {
						if(logger.isDebugEnabled()){
							logger.debug("User Address already registered with INC. Calling  SccpUserAddressInService - " );
						}
						SccpUserAddressInService(userAddress, listener);	// inform the listener application about In-Service status of user address
					} else {
						if(logger.isDebugEnabled()){
							logger.debug("Calling  registerUserAddress - " );
						}
						registerUserAddress(userAddress);
					}
				} else {
					if(logger.isDebugEnabled()){
						logger.debug( "SCCP User Address Out Of Service  " );
					}
					// inform the listener application about Out-Of-Service status of user address
					if (TcapSessionReplicator.isSASActive())
					{
						/*
						 * SignalingPointCode spc = null;
						 * try
						 * {
						 * spc = userAddress.getSubSystemAddress().getSignalingPointCode();
						 * }
						 * catch (ParameterNotSetException pnse)
						 * {
						 * }
						 */
						NStateIndEvent nsie = new NStateIndEvent(this, userAddress, SccpConstants.USER_OUT_OF_SERVICE);
						listener.processStateIndEvent(nsie);
					}
				}
			}

		}

		try{
			replicator.addListener(listener);
			listenerCount++;
			if(logger.isDebugEnabled()){
				logger.debug("Number of Listeners = " + listenerCount);
			}
		}catch(Exception e){
			logger.error("Exception adding this listener for Replication. Needs to be handled...");
			if(logger.isDebugEnabled()){
				logger.debug(e.getMessage(), e);
			}
		}		
	}

	/**
	 * Request to the gateway to make this local subsystem address usable.
	 */
	public void registerUserAddress(SccpUserAddress userAddress) throws InvalidAddressException,
	IOException
	{
		//UAT-840 Changes
		//List<JainTcapListener> jtl = tcapAppRegistry.getListenerForSUA(userAddress.toString());
		List<JainTcapListener> jtl = tcapAppRegistry.getListenerForSUA(userAddress.getString());
		if (jtl != null)
		{
			if (TcapSessionReplicator.isSASActive())
			{
				if(simulator != true) {				
					try
					{
						NStateReqEvent sreq = new NStateReqEvent(this, userAddress, SccpConstants.USER_IN_SERVICE);
						sendStateReqEvent(sreq);
					}

					catch (ParameterNotSetException e)
					{
						throw new InvalidAddressException("User address improperly formatted");
					}
				}

				Collection<NPCStateIndEvent> smap = nPCStateStatusMap.values();
				Iterator<NPCStateIndEvent> sIter = smap.iterator();
				while (sIter.hasNext())
				{
					NPCStateIndEvent npcEvent = sIter.next();
					for(JainTcapListener listener : jtl)
						listener.processStateIndEvent(npcEvent);
				}

				Collection<NStateIndEvent> smap1 = nStateStatusMap.values();
				Iterator<NStateIndEvent> sIter1 = smap1.iterator();
				while (sIter1.hasNext())
				{
					NStateIndEvent nstateEvent = sIter1.next();
					for(JainTcapListener listener : jtl)
						listener.processStateIndEvent(nstateEvent);
				}
			}
		}else {
			try{
				if (userAddress.getSubSystemAddress().getSubSystemNumber() == Integer.valueOf(rsnSSA).intValue()&& TcapSessionReplicator.isSASActive())
				{
					if(simulator != true) {				
						NStateReqEvent sreq = new NStateReqEvent(this, userAddress, SccpConstants.USER_IN_SERVICE);
						sendStateReqEvent(sreq);
					}
				}
			}catch (MandatoryParameterNotSetException e) {
				throw new InvalidAddressException("Mandatory Parameter Subsystem number not set" + e);
			}catch (ParameterNotSetException e) {
				throw new InvalidAddressException("User address improperly formatted" + e);
			}
		}
	}


	/**
	 * Indication from a gateway that a local subsystem address is now usable.
	 */
	public void SccpUserAddressInService(SccpUserAddress userAddress, JainTcapListener jtl)
	{

		if (jtl != null)
		{
			if (TcapSessionReplicator.isSASActive())
			{
				/*
				 * SignalingPointCode spc = null;
				 * try
				 * {
				 * spc = userAddress.getSubSystemAddress().getSignalingPointCode();
				 * }
				 * catch (ParameterNotSetException pnse)
				 * {
				 * }
				 */
				NStateIndEvent nsie = new NStateIndEvent(this, userAddress, SccpConstants.USER_IN_SERVICE);
				jtl.processStateIndEvent(nsie);
			}
		}
	}


	/**
	 * Indication from a gatewat that a local subsystem address is now unusable.
	 */
	public void SccpUserAddressOutOfService(SccpUserAddress userAddress)
	{
		//UAT-840 Changes
		//List<JainTcapListener> jtl = tcapAppRegistry.getListenerForSUA(userAddress.toString());
		List<JainTcapListener> jtl = tcapAppRegistry.getListenerForSUA(userAddress.getString());
		if (jtl != null)
		{
			if (TcapSessionReplicator.isSASActive())
			{
				/*
				 * SignalingPointCode spc = null;
				 * try
				 * {
				 * spc = userAddress.getSubSystemAddress().getSignalingPointCode();
				 * }
				 * catch (ParameterNotSetException pnse)
				 * {
				 * }
				 */
				NStateIndEvent nsie = new NStateIndEvent(this, userAddress, SccpConstants.USER_OUT_OF_SERVICE);
				for(JainTcapListener listener : jtl)
					listener.processStateIndEvent(nsie);
			}
		}
	}

	/**
	 * Removes a <a href="JainTcapListener.html">JainTcapListener</a> from the
	 * list of registered JainTcapListeners of this JainTcapProviderImpl.
	 *
	 * @param  listener the listener to be removed from this provider
	 * @exception  ListenerNotRegisteredException thrown if there is no such
	 * listener registered with this provider
	 */
	public void removeJainTcapListener(JainTcapListener listener) throws ListenerNotRegisteredException,
	IOException
	{
		logger.error("This API is Deprecated!! Instead use removeJainTcapListener(JainTcapListener listener, List<String> serviceKey)");
	}

	/**
	 * Removes a <a href="JainTcapListener.html">JainTcapListener</a> from the
	 * list of registered JainTcapListeners of this JainTcapProviderImpl in the SUA map.
	 *
	 * @param  listener the listener to be removed from this provider
	 * @exception  ListenerNotRegisteredException thrown if there is no such
	 * listener registered with this provider
	 */
	public void removeJainTcapListenerForSUA(JainTcapListener listener) throws ListenerNotRegisteredException,
	IOException
	{
		try
		{
			SccpUserAddress[] addrs = listener.getUserAddressList();

			int size = 0;
			while (size < addrs.length)
			{
				//UAT-840 Changes
				//List<JainTcapListener> jtl = tcapAppRegistry.getListenerForSUA(addrs[size].toString());
				List<JainTcapListener> jtl = tcapAppRegistry.getListenerForSUA(addrs[size].getString());
				if(logger.isDebugEnabled()){
					logger.debug("getListenerForSUA : " + addrs[size].getString() + " is : " + jtl + " size : " + jtl.size());
				}
				if (jtl != null)
				{
					if(jtl.contains(listener)){
						jtl.remove(listener);
					}
					if(jtl.size()== 0) {
						//UAT-840 Changes
						//tcapAppRegistry.removeListenerForSUA(addrs[size].toString());
						tcapAppRegistry.removeListenerForSUA(addrs[size].getString());

						//send INFO for de-registration only if no listener for his SUA
						if (TcapSessionReplicator.isSASActive())
						{
							if(logger.isDebugEnabled()){
								logger.debug("sending info for address : " + addrs[size].getString());
							}

							NStateReqEvent sreq = new NStateReqEvent(this, addrs[size], SccpConstants.USER_OUT_OF_SERVICE);
							sendStateReqEvent(sreq);
						}
					}
					else {
						//UAT-840 Changes
						//tcapAppRegistry.addListenerForSUA(addrs[size].toString(), jtl);
						tcapAppRegistry.addListenerForSUA(addrs[size].getString(), jtl);
					}
				}

				size++;
			}
			replicator.removeListener(listener);
			listenerCount--;
			if(logger.isDebugEnabled()){
				logger.debug("Number of Listeners = " + listenerCount);
			}
		}

		catch (UserAddressEmptyException e)
		{
			throw new ListenerNotRegisteredException("User address list is empty");
		}

		catch (ParameterNotSetException e)
		{
			throw new ListenerNotRegisteredException("User address improperly formatted");
		}

	}

	/**
	 * Removes a <a href="JainTcapListener.html">JainTcapListener</a> from the
	 * list of registered JainTcapListeners of this JainTcapProviderImpl.
	 *
	 * @param  listener the listener to be removed from this provider
	 * @param  serviceKey the listener to be removed from this provider for service key
	 * @param  listener the listener to be removed from this provider for applicationName
	 * @exception  ListenerNotRegisteredException thrown if there is no such
	 * listener registered with this provider
	 * @throws IOException 
	 */
	public void removeJainTcapListener(JainTcapListener listener,
			List<String> serviceKey)
					throws ListenerNotRegisteredException, IOException {

		if(listener == null) {
			throw new ListenerNotRegisteredException("JainTcapListener cannot be NULL.");
		}

		String applicationName = ((TcapListener)listener).getName();
		String appVersion = ((TcapListener)listener).getVersion();


		String versionActiveForApp = tcapAppRegistry.getActiveVersionForAppName(applicationName);
		if(versionActiveForApp != null && versionActiveForApp.equals(appVersion)) {		

			if(logger.isDebugEnabled()){
				logger.debug("Removing Version for application name:" + applicationName);
			}
			tcapAppRegistry.removeActiveVersionForAppName(applicationName) ;

			if(logger.isDebugEnabled()){
				logger.debug("Removing listener for application name:" + applicationName);
			}
			tcapAppRegistry.removeListenerForAppName(applicationName) ;

			if(serviceKey != null ){//&& protocol == PROTOCOL_ITU) {
				for(int i =0 ; i< serviceKey.size(); i++){
					if(logger.isDebugEnabled()){
						logger.debug("Removing listener for service key:" + serviceKey.get(i));
					}
					tcapAppRegistry.removeListenerForSrvKey(serviceKey.get(i));
				}
			}

			removeJainTcapListenerForSUA(listener);

		} else {
			replicator.removeListener(listener);
			listenerCount--;
			if(logger.isDebugEnabled()){
				logger.debug("Number of Listeners = " + listenerCount);
			}
		}
	}

	/**
	 * Returns the JainTcapStackImpl that this JainTcapProviderImpl is attached to.
	 *
	 * @return the attached JainTcapStack.
	 * @deprecated As of JAIN TCAP v1.1. This class is no longer needed as a result
	 * of the addition of the {@link jain.protocol.ss7.tcap.JainTcapProvider#getStack} class.
	 * The reason for deprecating this method is that the provider is attached implicitly.
	 */
	public JainTcapStack getAttachedStack()
	{
		return null;
	}

	/**
	 * Returns the JainTcapStackImpl that this JainTcapProviderImpl is attached to.
	 *
	 * @return the attached JainTcapStack.
	 * @since version 1.1
	 */
	public JainTcapStack getStack()
	{
		return null;
	}

	/**
	 * @deprecated    As of JAIN TCAP v1.1. No replacement, the JainTcapProvider is
	 * attached implicitly within the <a href = "JainTcapStack.html#createProvider()">createProvider</a>
	 * method call in the <code>JainTcapStack</code> Interface.
	 */
	public boolean isAttached()
	{
		return true;
	}

	/**
	 * This is the Genband replacement for the above primitive.
	 */
	public boolean isAttached(SignalingPointCode localSpc)
	{
		TcapProviderGateway tpg = TcapProviderGateway.getByAddress(localSpc);
		return tpg.isConnected();
	}


	public void tcapMessageFailed(TcapSession ts, boolean notifyApp){
		int dialogueId = -1;
		dialogueId = (ts != null) ? ts.getDialogueId() : dialogueId;

		if(dialogueId == -1){
			logger.error("Not able to get the Dialogue ID to generate TcapError:" + ts);
			return;
		}		

		logger.error("Gen TcapErrorEvent dlg:" + dialogueId);
		if(ts != null) {
			JainTcapListener jtl = (JainTcapListener)ts.getAttribute(ListenerApp);
			if(jtl != null){
				if(notifyApp){
					jtl.processTcapError(new TcapErrorEvent(this, ts));
				}
				try{
					//replicator.closeTcapSession(dialogueId);
					ts.invalidate();
				}catch (IdNotAvailableException inae) {
					logger.error( "Don't have dialogue " + dialogueId, inae);
				}
			}else{
				logger.error("Not able to find the listener for failed Message:" + dialogueId);
			}
		} else {
			logger.error("Tcap Session not available for Dialog ID:" + dialogueId);
		}
	}

	public void tcapMessageFailed(DialogueReqEvent dre, boolean notifyApp){
		int dialogueId = -1;
		try{
			dialogueId = (dre != null) ? dre.getDialogueId() : dialogueId;
		}catch(MandatoryParameterNotSetException mpe){}

		if(dialogueId == -1){
			logger.error("Not able to get the Dialogue ID to generate TcapError:" + dre);
			return;
		}		

		logger.error("Gen TcapErrorEvent dlg:" + dialogueId);
		TcapSession ts = replicator.getTcapSession(dialogueId);
		if(ts != null) {
			JainTcapListener jtl = (JainTcapListener)ts.getAttribute(ListenerApp);
			if(jtl != null){
				if(notifyApp){
					jtl.processTcapError(new TcapErrorEvent(this, ts));
				}
				try{
					//replicator.closeTcapSession(dialogueId);
					ts.invalidate();
				}catch (IdNotAvailableException inae) {
					logger.error( "Don't have dialogue " + dialogueId, inae);
				}
			}else{
				logger.error("Not able to find the listener for failed Message:" + dialogueId);
			}
		} else {
			logger.error("Tcap Session not available for Dialog ID:" + dialogueId);
		}
	}
	public void tcapMessageFailed(DialogueIndEvent die, boolean notifyApp){
		int dialogueId = -1;
		try{
			dialogueId = (die != null) ? die.getDialogueId() : dialogueId;
		}catch(MandatoryParameterNotSetException mpe){}

		if(dialogueId == -1){
			logger.error("Not able to get the Dialogue ID to generate TcapError:" + die);
			return;
		}		

		logger.error("Gen TcapErrorEvent dlg:" + dialogueId);
		TcapSession ts = replicator.getTcapSession(dialogueId);
		if(ts != null) {
			JainTcapListener jtl = (JainTcapListener)ts.getAttribute(ListenerApp);
			if(jtl != null){
				if(notifyApp){
					jtl.processTcapError(new TcapErrorEvent(this, ts));
				}
				try{
					//replicator.closeTcapSession(dialogueId);
					ts.invalidate();
				}catch (IdNotAvailableException inae) {
					logger.error( "Don't have dialogue " + dialogueId, inae);
				}
			}else{
				logger.error("Not able to find the listener for failed Message:" + dialogueId);
			}
		} else {
			logger.error("Tcap Session not available for Dialog ID:" + dialogueId);
		}
	}

	public boolean receivedTcap(TcapType tt, SipServletRequest req) 
			throws MandatoryParameterNotSetException,TcapContentReaderException, 
			ListenerNotRegisteredException
	{
		DialogueIndEvent die = tt.getDialogueIndEvent();
		int dlgId=-1;
		
		// CO - Check if request contains X-Protocol header. Possible vlaues ITU or ANSI
		int rxProtocol = protocol; // initialize with same as configured on
		String rxProtocolHdr = (String)req.getHeader(X_TCAP_VARIANT);
		
		if(StringUtils.isNotBlank(rxProtocolHdr)){

			if (StringUtils.containsIgnoreCase(rxProtocolHdr, ITU_STR)) {
				rxProtocol = PROTOCOL_ITU;
			} else if (StringUtils.containsIgnoreCase(rxProtocolHdr, ANSI_STR)) {
				rxProtocol = PROTOCOL_ANSI;
			}
			
			if(logger.isDebugEnabled()){
				logger.debug((String)req.getHeader("Dialogue-id") + "X-Tcap-Variant: header rxed, protocol:" + ((rxProtocol==1)?"ANSI":"ITU"));
			}
		}else{
			if(logger.isDebugEnabled()){
			logger.debug((String)req.getHeader("Dialogue-id") + "X-Tcap_variant header not received in incoming Notify");
			}
		}
		
		if(logger.isDebugEnabled()){
			logger.debug((die != null)?die.getDialogueId():"Null" + ": rx dialogue:" + rxProtocol) ;
		}
		
		if (die != null)
		{
			String tcCorrId =(String)req.getHeader(TC_CORR_ID_HEADER);

			if (tcCorrId == null) {
				dlgId = die.getDialogueId();
			} else {
				dlgId = Integer.parseInt(tcCorrId);
				if (getTCCorrelationId(die.getDialogueId()) == null)
					setTCCorrelationId(die.getDialogueId(), dlgId);
			}

			TcapSession existingTs = replicator.getTcapSession(dlgId);
			JainTcapListener jtl=null;
			jtl = getUser(tt, req, rxProtocol);

			TcapSession ts = replicator.getTcapSession(dlgId);

			if(ts!=null && tcCorrId!=null){
				ts.setTcCorrelationId(dlgId);
			}

			if(ts!=null && isSS7MsgInfoEnabled){
				ts.updateIndEventPrintInfo(req,tt.getComponentIndEvent(),die.getPrimitiveType());
			}

			List<String> seqIds = (List<String>)ts.getAttribute("TC-Seq");

			if (seqIds == null){
				seqIds = new ArrayList<String>();

				if(protocol==PROTOCOL_ANSI || rxProtocol==PROTOCOL_ANSI){
					seqIds.add(die.getDialogueId()+"-"+(String)req.getHeader("TC-Seq"));
				}else
					seqIds.add((String)req.getHeader("TC-Seq"));

				ts.setAttribute("TC-Seq", seqIds);
			} else if ((protocol == PROTOCOL_ITU || rxProtocol==PROTOCOL_ITU)
					&& seqIds.contains((String) req.getHeader("TC-Seq"))
					|| (protocol == PROTOCOL_ANSI && seqIds.contains(die
							.getDialogueId()
							+ "-"
							+ (String) req.getHeader("TC-Seq")))) {
				logger.error("Duplicate NOTIFY Received");
				SipServletResponse resp = req.createResponse(499);
				resp.setAttribute("TC-Seq", req.getHeader("TC-Seq"));
				resp.addHeader("TC-Seq",req.getHeader("TC-Seq"));
				if(logger.isDebugEnabled()){
					logger.debug("Sending response:\n" + resp);
				}
				try{
					resp.send();
				}catch (IOException e) {
					logger.error( "Exception in sending the response " , e);
				}

				return false;
			} else{
				if(protocol==PROTOCOL_ANSI || rxProtocol==PROTOCOL_ANSI){
					seqIds.add(die.getDialogueId()+"-"+(String)req.getHeader("TC-Seq"));
				}else
					seqIds.add((String)req.getHeader("TC-Seq"));
			}
			long startTimeToProcess = System.currentTimeMillis();
			try
			{	
				ts.acquire();
				if(die.getPrimitiveType() == TcapConstants.PRIMITIVE_END){
					if(logger.isDebugEnabled()){
						logger.debug("Dlg rcvd is End" );
					}
					EndIndEvent endIndEvent = (EndIndEvent)die;
					List<ComponentIndEvent> components = tt.getComponentIndEvent();
					if(components.size()> 0){
						if(logger.isDebugEnabled()){
							logger.debug("Dlg rcvd is End.setComponentsPresent true" );
						}
						endIndEvent.setComponentsPresent(true);
					}
					else{
						if(logger.isDebugEnabled()){
							logger.debug("Dlg rcvd is End.setComponentsPresent false" );
						}
						endIndEvent.setComponentsPresent(false);
					}

					jtl.processDialogueIndEvent(endIndEvent);
					incrementTcapCounters(endIndEvent);

				}else {
					if(logger.isDebugEnabled()){
						logger.debug("Processing dlg for listner" );
					}
					jtl.processDialogueIndEvent(die);
					incrementTcapCounters(die);
				}

				List<ComponentIndEvent> components = tt.getComponentIndEvent();
				Iterator<ComponentIndEvent> iComponent = components.iterator();
				while (iComponent.hasNext())
				{
					ComponentIndEvent ciet = (ComponentIndEvent)iComponent.next();
					//this check is introduced because there can be the case 
					//when we recive multiple components in dialogue and 
					//call has been cleaned on first one
					//eg: EN+ERB_OANS Now on ENC parse fail service will clean the call
					//or ERB_ODSIC+ENC and service cleans call on disconnect. 
					//We should invoke default listener for such cases
					if(ts.isClosed()){
						//invoking default listener.
						//this method just does necessary logging for component
						logger.warn(die.getDialogueId()+":: Pending components but " +
								"call terminated; dialog primitive::"+die.getPrimitiveType());
						defaultListener.processComponentIndEvent(ciet);
					}else{
						jtl.processComponentIndEvent(ciet);
					}

					incrementTcapCounters(ciet);
				}

				//will be true for abort and end dialogue
				if (die.endsDialogue()
						|| (die.getPrimitiveType() == TcapConstants.PRIMITIVE_UNIDIRECTIONAL && existingTs == null)) {
					try {
						// invalidate ts if service has not set handoff flag.
						// this is done to support explicit control of ts by
						// service
						if (ts.getAttribute(Constants.FOR_HANDOFF) == null) {
							ts.invalidate();
						}
					} catch (IdNotAvailableException inae) {
						logger.error( "Don't have dialogue " + die.getDialogueId());
						if(logger.isDebugEnabled()){
							logger.debug( "Don't have dialogue " + die.getDialogueId(), inae);
						}
					}

				}
				if(logger.isDebugEnabled()){
					logger.debug("Calling replicate" );
				}
				if (ftTesting.equalsIgnoreCase("Before_Replication")){
					System.exit(0);
				}
				replicate(dlgId) ;//die.getDialogueId());
				if (ftTesting.equalsIgnoreCase("After_Replication")){
					System.exit(0);
				}

			}finally{
				long timeSpentInProcessing = System.currentTimeMillis() - startTimeToProcess;
				if(timeSpentInProcessing >= sAvgResponseTime){
					logger.error("Total time spent in processing dialogues and components indication events : " + timeSpentInProcessing + " for dialogueId : " + die.getDialogueId());
				}
				//Always release lock before rtuen
				ts.release();
			}
		}//@end die not null

		StateIndEvent stateIndEvent = tt.getStateIndEvent();
		if (stateIndEvent != null)
		{
			if (stateIndEvent instanceof NPCStateIndEvent)
			{
				NPCStateIndEvent npcsie = (NPCStateIndEvent)stateIndEvent;
				if(logger.isDebugEnabled()){
					logger.debug("Received State information for " + npcsie.getAffectedDpc().toString() + " for opc " + npcsie.getOwnPointCode() + " is now " + npcsie.getSignalingPointStatus());
				}
				if (npcsie.getSignalingPointStatus() != SccpConstants.DESTINATION_INACCESSIBLE)
				{
					logger.error("Putting the event in nPCStateStatusMap");
					nPCStateStatusMap.put(npcsie.getOwnPointCode().toString() + npcsie.getAffectedDpc().toString(), npcsie);
				}

				else
				{
					logger.error("removing the event in nPCStateStatusMap");
					nPCStateStatusMap.remove(npcsie.getOwnPointCode().toString() + npcsie.getAffectedDpc().toString());
				}
			}

			else if (stateIndEvent instanceof NStateIndEvent)
			{
				NStateIndEvent nsie = (NStateIndEvent)stateIndEvent;
				if (logger.isInfoEnabled()) {
					logger.log(Level.INFO, "Received State information for " + nsie.getAffectedUser().toString() + " is now " + nsie.getUserStatus());
				}
				if (nsie.getUserStatus() != SccpConstants.USER_OUT_OF_SERVICE)
				{
					logger.error("put the event in nStateStatusMap");
					nStateStatusMap.put(nsie.getAffectedUser().toString(), nsie);
				}

				else
				{
					logger.error("removing the event in nStateStatusMap");
					nStateStatusMap.remove(nsie.getAffectedUser().toString());
				}

				// Update SUA status
				TcapProviderGateway tpg = TcapProviderGateway.getTcapProviderGateway(req);
				if(tpg != null) {
					List<SccpUserAddress> list = tpg.getSSAList();
					for(SccpUserAddress sua : list) {
						
						if(sua.toString().equals(nsie.getAffectedUser().toString())) {
							if (logger.isInfoEnabled()) {
								logger.log(Level.INFO, "SUA From SSA List" + sua.toString() + " incoming SUA  " + nsie.getAffectedUser().toString());
							}
							if (logger.isInfoEnabled()) {
								logger.log(Level.INFO, "found SUA in list-->Set SUA status 1");
							}
							sua.setSuaStatus(nsie.getUserStatus());
						}

					}
				}
			}

			//Broadcasting to All registered listeners as sua don't come in INFO only Point-Code comes of SUA.
			Collection<List<JainTcapListener>> jtls = tcapAppRegistry.getListenersForAllSUA();
			Iterator<List<JainTcapListener>> jtlsIterator = jtls.iterator();
			while(jtlsIterator.hasNext())
			{
				List<JainTcapListener> list = jtlsIterator.next();
				for(JainTcapListener jtl : list)
					jtl.processStateIndEvent(stateIndEvent);
			}
		}

		LocalCancelIndEvent localCancelIndEvent = tt.getLocalCancelIndEvent();
		if (localCancelIndEvent != null)
		{
			TcapSession ts = replicator.getTcapSession(dlgId); //localCancelIndEvent.getDialogueId());
			JainTcapListener jtl = (JainTcapListener)ts.getAttribute(ListenerApp);
			if(jtl != null)
				jtl.processComponentIndEvent(localCancelIndEvent);
			else{
				throw new ListenerNotRegisteredException("Could not find listener");
			}

			replicate(dlgId);//localCancelIndEvent.getDialogueId());
		}

		RejectIndEvent rejectIndEvent = tt.getRejectIndEvent();
		if (rejectIndEvent != null)
		{
			TcapSession ts = replicator.getTcapSession(dlgId); //rejectIndEvent.getDialogueId());
			JainTcapListener jtl = (JainTcapListener)ts.getAttribute(ListenerApp);
			if(jtl != null){
				jtl.processComponentIndEvent(rejectIndEvent);
				incrementTcapCounters(rejectIndEvent);
			}else {
				throw new ListenerNotRegisteredException("Could not find listener");
			}

			replicate(dlgId);//rejectIndEvent.getDialogueId());
		}
		return true;
	}


	public TcapFactory getTcapFactory(JainTcapListener listener)
	{
		return replicator.getTcapFactory(listener);
	}

	public TcapSession getTcapSession(int dialogueId)
	{
		Integer tcCorrId= getTCCorrelationId(dialogueId);

		if(tcCorrId!=null)
			dialogueId=tcCorrId.intValue();

		return replicator.getTcapSession(dialogueId);
	}

	public TcapSession getTcapSession(int dialogueId, JainTcapListener jtl)
	{
		return replicator.getTcapSession(dialogueId, jtl);
	}

	public void replicate(int dialogueId)
	{
		replicator.replicate(dialogueId, null);
	}

	private JainTcapListener getUser(TcapType tcapType, SipServletRequest req, int rxProtocol)
			throws MandatoryParameterNotSetException, ListenerNotRegisteredException {
		JainTcapListener jtl = null;
		if (rxProtocol == PROTOCOL_ANSI) {
			logger.info("Inside the ANSI protocol");
			if(checkV2EnabledOrNot()) {
//				logger.info("app routing enabled");
//				String filepath = Constants.ASE_HOME + "/" + Constants.TRIGGERING_RULE_FILE;
//				if (logger.isDebugEnabled()) {
//					logger.debug("Triggering Rule file path- " + filepath);
//				}
//				TcapTriggeringCriteriaRule triggeringCriteriaRule = null;
//				InputStream in =null;
//				try {
//					in = Files.newInputStream(Paths.get(filepath));
//					Yaml yaml = new Yaml();
//					triggeringCriteriaRule = yaml.loadAs(in, TcapTriggeringCriteriaRule.class);
//					if (logger.isDebugEnabled()) {
//						logger.info("triggeringCriteriaRule:" + triggeringCriteriaRule);
//					}
//
//				
//
//				if(triggeringCriteriaRule != null) {
//					logger.info("app routing   enabled and proper trigger file");
//					jtl = getUserForAnsiV2(tcapType, req, triggeringCriteriaRule);
//				}else {
//					logger.info("app routing   enabled but not proper file ");
//					jtl = getUserForAnsi(tcapType, req);
//				}
//				} catch (IOException e) {
//					logger.error("Triggering Rule file parse exception occured- " + filepath);
//				}finally{
//					logger.info("close the stream");
//					try{
//					in.close();
//					}catch(IOException e){
//						logger.error("error while closing input steam for trigering rule yml");
//					}
//				}
				if(triggeringCriteriaRule != null) {
					logger.info("app routing   enabled and proper trigger file");
					jtl = getUserForAnsiV2(tcapType, req, triggeringCriteriaRule);
				}else {
					logger.info("app routing   enabled but not proper file ");
					jtl = getUserForAnsi(tcapType, req);
				}
			}else {
				logger.info("app routing not  enabled");
				jtl = getUserForAnsi(tcapType, req);
			}


		} else if (rxProtocol == PROTOCOL_ITU) {
			jtl = getUserForItu(tcapType, req);
		}
		return jtl;
	}

	/*private synchronized JainTcapListener getUserForItu(TcapType tcapType,
			SipServletRequest req) throws MandatoryParameterNotSetException,
			ListenerNotRegisteredException {*/
	private JainTcapListener getUserForItu(TcapType tcapType,
			SipServletRequest req) throws MandatoryParameterNotSetException,
	ListenerNotRegisteredException {
		long startGetUserForItut = System.currentTimeMillis();
		DialogueIndEvent die = tcapType.getDialogueIndEvent();
		SccpUserAddress sua = null;
		SccpUserAddress origsua = null;
		int dialogueId = die.getDialogueId();
		TcapSession ts = replicator.getTcapSession(dialogueId);
		int invokeId = 1;
		if (logger.isDebugEnabled()) {
			logger.debug("tcapSession : " + ts);
		}
		JainTcapListener jtl = null;
		boolean srvKeyPresent = true;
		// String srvKey = null;
		TcapNextAppInfo nextAppInfo = null;
		String nextApp = null;
		ParseError parseError = null;
		boolean isInitialMessagePresent = false;
		if (ts == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("no TcapSession found for DialodueId - "
						+ die.getDialogueId());
			}
			try {
				sua = die.getDestinationAddress();
				if (die.getPrimitiveType() == TcapConstants.PRIMITIVE_BEGIN) {
					origsua = ((BeginIndEvent) die).getOriginatingAddress();
					req.setAttribute(BEGIN_REQ, "true");
				} else if (die.getPrimitiveType() == TcapConstants.PRIMITIVE_UNIDIRECTIONAL) {
					origsua = ((UnidirectionalIndEvent) die)
							.getOriginatingAddress();
					req.setAttribute(BEGIN_REQ, "true");
				}

				if (logger.isDebugEnabled()) {
					logger.debug(dialogueId + ":: Got Orig sua ::" + sua);
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Message destined for SUA - " + sua);
				}
				List<ComponentIndEvent> components = tcapType
						.getComponentIndEvent();
				Iterator<ComponentIndEvent> iComponent = components.iterator();
				while (iComponent.hasNext()) {
					ComponentIndEvent ciet = (ComponentIndEvent) iComponent
							.next();
					// if not invoke
					if (ciet.getPrimitiveType() != TcapConstants.PRIMITIVE_INVOKE) {
						if (logger.isDebugEnabled()) {
							logger.debug(dialogueId
									+ ":: Not a Invoke component move to next");
						}
						continue;
					}
					InvokeIndEvent receivedInvoke = (InvokeIndEvent) ciet;
					Operation opr = receivedInvoke.getOperation();
					invokeId = receivedInvoke.getInvokeId();
					byte[] opCode = opr.getOperationCode();
					String opCodeStr = formatBytes(opCode);
					
					if (opCodeStr.equalsIgnoreCase("0x00")) {
						// set initail messag efound tot true
						isInitialMessagePresent = true;

						byte[] parms = receivedInvoke.getParameters()
								.getParameter();
						if (logger.isDebugEnabled()) {
							logger.debug("Calling tcapRoutingController utility with params:"
									+ parms[0]
											+ " "
											+ parms[1]
													+ " "
													+ parms[2] + " " + parms[3]);
						}

						try {
							nextAppInfo = tcapRoutingController
									.getNextAppListener(parms,origsua);
						} catch (ASNParsingException e) {
							if (logger.isDebugEnabled()) {
								logger.debug(
										"Got ASNParsingException in getNextAppListener()",
										e);
							}
							parseError = new ParseError(
									PARSE_ERROR_TYPE.ASN_PARSE_FAILURE,
									invokeId, e);
						} catch (EnumParamOutOfRangeException e) {
							if (logger.isDebugEnabled()) {
								logger.debug(
										"Got EnumParamOutOfRangeException in getNextAppListener()",
										e);
							}
							parseError = new ParseError(
									PARSE_ERROR_TYPE.ENUM_PARAM_OUT_OF_RANGE,
									invokeId, e);
						} catch (MandatoryParamMissingException e) {
							if (logger.isDebugEnabled()) {
								logger.debug(
										"Got MandatoryParamMissingException in getNextAppListener()",
										e);
							}
							parseError = new ParseError(
									PARSE_ERROR_TYPE.MANDATORY_PARAM_MISSING,
									invokeId, e);
						} catch (ParameterOutOfRangeException e) {
							if (logger.isDebugEnabled()) {
								logger.debug(
										"Got ParameterOutOfRangeException in getNextAppListener()",
										e);
							}
							parseError = new ParseError(
									PARSE_ERROR_TYPE.PARAM_OUT_OF_RANGE,
									invokeId, e);
						} catch (CriticalityTypeException e) {
							if (logger.isDebugEnabled()) {
								logger.debug(
										"Got CriticalityTypeException in getNextAppListener()",
										e);
							}
							parseError = new ParseError(
									PARSE_ERROR_TYPE.CRITICALITY_TYPE,
									invokeId, e);
						}

						if (logger.isDebugEnabled()) {
							logger.debug("listener found.");
						}
						break;
					}else{
						// check in static rule based on opcode 
						if(checkV2EnabledOrNot()) {
							if(triggeringCriteriaRule != null) {
								if (opsCodes.contains(opCodeStr)) {
									isInitialMessagePresent = true;
									String ssnFromTcapRequest = String.valueOf(sua.getSubSystemAddress().getSubSystemNumber());
									String serviceKey = null;
									String conditionString = ssnFromTcapRequest + ":" + opCodeStr + ":" + serviceKey;
									String nextAppId = getNextAppId(triggeringCriteriaRule, conditionString, null);
									nextAppInfo = new TcapNextAppInfo(nextAppId, false);
									
									if(logger.isDebugEnabled()){
										logger.debug("ITUT - Searching Next App based for App:" +
												conditionString + " : App Id:" + nextAppId);
									}
								}
							}else{
								logger.debug("ITUT- Triggering Criteria Rule MUST be enabled for MAP: opcode:" + opCodeStr);
							}
						}else{
							logger.debug("ITUT- appRoutingEnabled need to enabled for MAP:" + appRoutingEnabled);
						}
					}
				}// end while for component

				if (nextAppInfo != null) {
					srvKeyPresent = nextAppInfo.isServiceKey();
					nextApp = nextAppInfo.getNextAppInfo();
					if (srvKeyPresent) {
						jtl = tcapAppRegistry.getListenerForSrvKey(nextApp,
								false);
					} else {
						jtl = tcapAppRegistry.getListenerForAppName(nextApp,
								false);
					}
					if (logger.isDebugEnabled()) {
						logger.debug("Listener name:" + jtl);
					}
				} else if (isInitialMessagePresent && parseError == null) {
					// nextappInfo is null ; initail meesage found and parse
					// error is null
					// means it was full id case
					if (logger.isDebugEnabled()) {
						logger.debug("Got appname as null in getNextAppListener() db look up");
					}
					parseError = new ParseError(
							PARSE_ERROR_TYPE.NUMBER_NOT_PROVISIONED, invokeId,
							null);
				}else if (!isInitialMessagePresent) {
					if (logger.isDebugEnabled()) {
						logger.debug(dialogueId
								+ "::Inital message not found for listener");
					}
					parseError = new ParseError(
							PARSE_ERROR_TYPE.TS_NULL_NO_INITAL_MESSAGE,
							invokeId, null);
				}// end if next app info

				if (jtl != null) {
					
					List<JainTcapListener> jtlList = null;

					// check if SUA has Global Title.
					String modifiedSUA = null;
					if(sua.isGlobalTitlePresent()){
						modifiedSUA = "Protocol Variant: " + sua.getProtocolVariant() + ", " 
								+ ((sua.isSubSystemAddressPresent() == true)?sua.getSubSystemAddress().toString():"");
					}else{
						modifiedSUA = sua.toString();
					}
					
					if(logger.isDebugEnabled()){
						logger.debug(dialogueId + " Modified SUA string:[" + modifiedSUA + "] isGTTPresent:" 
								+ sua.isGlobalTitlePresent());
					}
					// if sccp layer is ANSI and TCAP is ITUT then conver the protocol to ITU before 
					// searching SUA 				
					if(isProtocolSccpAnsi == 1){
						// Application register as protocol variant as 1 as for ITUT (cap, map, inap) 
						// where as INC will send variant as 2 coz SCCP layer is ANSI and TCAP Layer 
						// is ITUT. So repalce string with protocol with 1 in sua rxed from INC before
						// checking for registered SUA. 
						// Sua.toString looks like: Protocol Variant: 1, SSN:241, SPC:0-90-0
						modifiedSUA = StringUtils.replace(modifiedSUA, "t: 2", "t: 1");
						
						if(logger.isDebugEnabled()){
							logger.debug(dialogueId + "sua to String:" + sua.toString() + ": Modified:" + modifiedSUA);
						}
						// Check SUA registered for chosen listener
						jtlList = tcapAppRegistry
								.getListenerForSUA(modifiedSUA);
						
						logger.debug(dialogueId + "isProtocolSccpAnsi: " 
								+ isProtocolSccpAnsi +"jtlList for sua: " +modifiedSUA + " is "  + jtlList);
					}else{
						// Check SUA registered for chosen listener
						jtlList = tcapAppRegistry
								//.getListenerForSUA(sua.toString());
								.getListenerForSUA(modifiedSUA);
						logger.debug(dialogueId + "isProtocolSccpAnsi: " 
								+ isProtocolSccpAnsi +"jtlList for sua: " +modifiedSUA + " is "  + jtlList);
					}
					if (jtlList == null || !jtlList.contains(jtl)) {
						if (logger.isDebugEnabled()) {
							logger.debug("Listeners NOT registered for SUA : "
									+ modifiedSUA  + " && sua is:" + sua);
						}
						throw new ListenerNotRegisteredException(
								"Listener not registered for received SUA::"
										+ sua);
					}

					// Check SUA status
					TcapProviderGateway tpg = TcapProviderGateway
							.getTcapProviderGateway(req);
					if (tpg != null) {
						LinkedList<SccpUserAddress> ssaList = tpg.getSSAList();

						for (SccpUserAddress ssa : ssaList) {
							//if (ssa.toString().equals(sua.toString())) {
							if (ssa.toString().equals(modifiedSUA)) {
								if (logger.isDebugEnabled()) {
									logger.debug("SUA : " + ssa + " status  : "
											+ ssa.getSuaStatus());
								}
								if (ssa.getSuaStatus() != SccpConstants.USER_IN_SERVICE) {
									throw new ListenerNotRegisteredException(
											"SUA is NOT in-service.");
								}
								break;
							}
						}
					}
					if (logger.isDebugEnabled()) {
						logger.debug("creating tcap session");
					}

					ts = createTcapSession(die.getDialogueId(), jtl, req, sua,
							origsua);

					// For serialization at the time of partialActivate will
					// help to find jtl
					TcapListener listener = (TcapListener) jtl;
					TcapSessionCount.getInstance().addTcapDialog(
							listener.getName() + "_" + listener.getVersion(),
							String.valueOf(die.getDialogueId()));
					if (nextAppInfo.isServiceKey()) {
						if (logger.isDebugEnabled()) {
							logger.debug("tcapsession setting attribute ServiceKey");
						}
						ts.setAttribute(ServiceKey,
								nextAppInfo.getNextAppInfo());
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("tcapsession setting attribute appname");
						}
						ts.setAttribute(AppName, nextAppInfo.getNextAppInfo());
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("no listener for Message destined for SUA - "
								+ sua + " and Service Key - " + nextApp);
					}
				}

			} catch (ParameterNotSetException pnse) {
				if (logger.isDebugEnabled()) {
					logger.debug("Got ParameterNotSetException in getUSer",
							pnse);
				}
				throw new MandatoryParameterNotSetException(
						"MandatoryParamNotsetExceprion", pnse);
			} catch (ListenerNotRegisteredException e) {
				if (logger.isDebugEnabled()) {
					logger.debug(
							"Got ListenerNotRegisteredException in getUSer for SUA status",
							e);
				} 
				parseError = new ParseError(
						PARSE_ERROR_TYPE.LISTENER_NOT_REGISTERED_SUA, invokeId,
						e);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("find listener from tcapSession");
			}
			jtl = (JainTcapListener) ts.getAttribute(ListenerApp);
		}// end if ts = null/not null

		if (jtl == null & req.getMethod().equals("NOTIFY")) {
			// jtl not found form meesaegedue to erro or some other issue
			// use default listener for notify
			if (parseError == null) {
				if (srvKeyPresent) {
					parseError = new ParseError(
							PARSE_ERROR_TYPE.LISTENER_NOT_REGISTERED_SK,
							invokeId, null);

				} else {
					parseError = new ParseError(
							PARSE_ERROR_TYPE.LISTENER_NOT_REGISTERED_APP,
							invokeId, null);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Parse Error set default listener "
						+ defaultListener);
			}
			jtl = defaultListener;
			// for out of sequnce continue message sua will be null;
			// so we cannot send out tcap content and have to return with error
			// response
			if (sua == null) {
				logger.warn(dialogueId
						+ " out of dialog message sua not found; dialog primitive::"
						+ die.getPrimitiveType());
				// log the dialog and component types for debugging and futre
				// refernces
				List<ComponentIndEvent> components = tcapType
						.getComponentIndEvent();
				Iterator<ComponentIndEvent> iComponent = components.iterator();
				while (iComponent.hasNext()) {
					// invoking default listener.
					// this method just does necessary logging for component
					defaultListener.processComponentIndEvent(iComponent.next());
					incrementTcapCounters(iComponent.next());
				}
				// throw exception to send 4xx for notify
				throw new ListenerNotRegisteredException(
						"Listener is not registered for req::"
								+ req.getMethod());
			} else {
				ts = createTcapSession(dialogueId, jtl, req, sua, origsua);
				// setting error scenario specific attribute
				ts.setAttribute(ParseError.class.getName(), parseError);
			}

		} else if (jtl == null) {
			throw new ListenerNotRegisteredException(
					"Listener is not registered for req::" + req.getMethod());
		}

		long timeTakenInGetUserForItut = System.currentTimeMillis() - startGetUserForItut;

		if(timeTakenInGetUserForItut >= sAvgResponseTime) {
			logger.error("Time spent in gettingUserForItut : " + timeTakenInGetUserForItut + " for dialougueId : " + dialogueId);
		}

		return jtl;
	}

	private synchronized JainTcapListener getUserForAnsi(TcapType tcapType,
			SipServletRequest req) throws MandatoryParameterNotSetException,
	ListenerNotRegisteredException {

		if(logger.isDebugEnabled()){
			logger.debug("Inside getUserForAnsi");
		}

		DialogueIndEvent die = tcapType.getDialogueIndEvent();
		SccpUserAddress sua = null;
		SccpUserAddress origsua = null;
		GlobalTitle gtt = null;
		TcapNextAppInfo nextAppInfo = null;
		String nextApp = null;

		String dlgId =(String)req.getHeader(TC_CORR_ID_HEADER);

		int tcCorrId=-1;

		int dialogueId=-1;
		if(dlgId==null){
			dialogueId=die.getDialogueId();
		}else{
			dialogueId= Integer.parseInt(dlgId);
			tcCorrId=dialogueId;
		}

		JainTcapListener jtl =null;
		TcapSession ts = replicator.getTcapSession(dialogueId);
		int invokeId = 1;
		if(logger.isDebugEnabled()){
			logger.debug("tcapSession : " + ts);
		}
		boolean isInitialMessagePresent = false;
		if (ts == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("no TcapSession found for DialodueId - " + dialogueId);
			}
			try {
				sua = die.getDestinationAddress();
				if (die.getPrimitiveType() == TcapConstants.PRIMITIVE_BEGIN) {
					origsua = ((BeginIndEvent) die).getOriginatingAddress();
					req.setAttribute(BEGIN_REQ, "true");
				} else if (die.getPrimitiveType() == TcapConstants.PRIMITIVE_UNIDIRECTIONAL) {
					origsua = ((UnidirectionalIndEvent) die)
							.getOriginatingAddress();
					req.setAttribute(BEGIN_REQ, "true");
				}

				if (logger.isDebugEnabled()) {
					logger.debug(dialogueId + ":: Got Destin sua ::" + sua);
					logger.debug(dialogueId + ":: Got Orig sua ::" + origsua);
				}

				if (logger.isDebugEnabled()) {
					logger.debug("OrigSUA destined for SUA - " + origsua);
				}

				List<ComponentIndEvent> components = tcapType
						.getComponentIndEvent();
				Iterator<ComponentIndEvent> iComponent = components.iterator();
				byte[] parms = null;
				String opCodeStr = "";
				while (iComponent.hasNext()) {
					ComponentIndEvent ciet = (ComponentIndEvent) iComponent
							.next();
					// if not invoke
					if (ciet.getPrimitiveType() != TcapConstants.PRIMITIVE_INVOKE) {
						if (logger.isDebugEnabled()) {
							logger.debug(dialogueId
									+ ":: For AIN : Not a Invoke component move to next");
						}
						continue;
					}
					InvokeIndEvent receivedInvoke = (InvokeIndEvent) ciet;
					Operation opr = receivedInvoke.getOperation();
					invokeId = receivedInvoke.getInvokeId();
					byte[] opCode = opr.getOperationCode();
					opCodeStr = formatBytes(opCode);

					// condition for Info Ana (0x64 0x03) and Termi Attempt (0x64 0x05)
					if (opCodeStr.equalsIgnoreCase("0x64 0x05") || opCodeStr.equalsIgnoreCase("0x64 0x03")) { 
						// set initail messag efound tot true
						isInitialMessagePresent = true;

						parms = receivedInvoke.getParameters()
								.getParameter();
						if (logger.isDebugEnabled()) {
							logger.debug("Calling tcapRoutingController utility with params for ain :"
									+ parms[0]
											+ " "
											+ parms[1]
													+ " "
													+ parms[2] + " " + parms[3]);
						}


						break;
					}// end if for opcode 0x64 0x05, 0x64 0x03
				}// end while for component

				// Cehck if GT is present alogn with address signal. 
				// There could be a case to identify application to be triggered 
				// based on address signal received. 
				if(origsua.isGlobalTitlePresent()){
					gtt = origsua.getGlobalTitle();

					// check if address signal is present. 
					if(gtt.getAddressInformation() != null){

						if(logger.isDebugEnabled()){
							logger.debug(dialogueId + ":: Address signal received: " + 
									formatBytes(gtt.getAddressInformation()));
						}

						String receivedDigits = convertDigitsToString(dialogueId, gtt.getAddressInformation());

						if(StringUtils.isNotBlank(receivedDigits)){
							// Call App Router
							if(parms != null){
								try {
									if(logger.isDebugEnabled()){
										logger.debug(dialogueId + ":: calling getNextAppListener - 4 args: " +opCodeStr );
									}
									nextAppInfo = tcapRoutingController.getNextAppListener(parms, opCodeStr, receivedDigits, sua);
								} catch (Exception e) {
									logger.error("Exceotion occured "+e);
								} 
							}else{
								if(logger.isDebugEnabled()){
									logger.debug(dialogueId + ":: calling getNextAppListener - 2 args: " );
								}
								nextAppInfo = tcapRoutingController.getNextAppListener(receivedDigits, sua);
							}


							if(nextAppInfo != null){
								nextApp = nextAppInfo.getNextAppInfo();
								// fetch listner based on application id
								jtl = tcapAppRegistry.getListenerForAppName(nextApp, false);

								if(logger.isDebugEnabled()){
									logger.debug(dialogueId + ":: fetched JTL for appName:" + nextApp + ", jtl returned:" + jtl );
								}
							}
						}
					}
				}
				// get jtl based on protocol currently supported
				// if protocol type is ANSI no need to parse SK and directly
				// route on SUA

				if(jtl == null){
					if(logger.isDebugEnabled()){
						logger.debug(dialogueId + ":: Since JTL returned fron GT is NULL, tryign to fetch based on SUA");
					}
					jtl = getListenerForSua(sua);
				}
				if (logger.isDebugEnabled()) {
					logger.debug(dialogueId + ":: Got JTL::" + jtl);
				}

				if (jtl != null) {
					
					boolean active=isAppActive(((TcapListener)jtl));
					
					if(!active){
						throw new ListenerNotRegisteredException(
								"Listener is not registered for req::" + req.getMethod());
					}
					// Check SUA status
					TcapProviderGateway tpg = TcapProviderGateway
							.getTcapProviderGateway(req);
					if (tpg != null) {
						LinkedList<SccpUserAddress> ssaList = tpg.getSSAList();

						for (SccpUserAddress ssa : ssaList) {
							if (ssa.toString().equals(sua.toString())) {
								if (logger.isDebugEnabled()) {
									logger.debug("SUA : " + ssa + " status  : "
											+ ssa.getSuaStatus());
								}
								if (ssa.getSuaStatus() != SccpConstants.USER_IN_SERVICE) {
									throw new ListenerNotRegisteredException(
											"SUA is NOT in-service.");
								}
								break;
							}
						}
					}

					if (logger.isDebugEnabled()) {
						logger.debug("creating tcap session");
					}

					ts = createTcapSession(dialogueId, jtl, req, sua,
							origsua);

					// For serialization at the time of partialActivate will
					// help to find jtl
					TcapListener listener = (TcapListener) jtl;
					TcapSessionCount.getInstance().addTcapDialog(
							listener.getName() + "_" + listener.getVersion(),
							String.valueOf(dialogueId));

					if (nextAppInfo != null) {

						if (nextAppInfo.isServiceKey()) {
							if (logger.isDebugEnabled()) {
								logger.debug("tcapsession setting attribute ServiceKey");
							}
							ts.setAttribute(ServiceKey,
									nextAppInfo.getNextAppInfo());
						} else {
							if (logger.isDebugEnabled()) {
								logger.debug("tcapsession setting attribute appname");
							}
							ts.setAttribute(AppName,
									nextAppInfo.getNextAppInfo());
						}
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("setAppname as "+listener.getName());
						}
						ts.setAttribute(AppName, listener.getName());
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("no listener for Message destined for SUA - "
								+ sua );
					}
				}
			}catch (ParameterNotSetException pnse) {
				if (logger.isDebugEnabled()) {
					logger.debug("Got ParameterNotSetException in getUSer",
							pnse);
				}
				throw new MandatoryParameterNotSetException(
						"MandatoryParamNotsetExceprion", pnse);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("find listener from tcapSession");
			}

			if(logger.isDebugEnabled()){
				logger.debug("Update DialogueId in Existing TcapSession " );
			}
			TcapSessionImpl tsi=(TcapSessionImpl)ts;
			tsi.setDialogueId(dialogueId);

			jtl = (JainTcapListener) ts.getAttribute(ListenerApp);
		}// end if ts = null/not null

		if (jtl == null) {
			throw new ListenerNotRegisteredException(
					"Listener is not registered for req::" + req.getMethod());
		}

		return jtl;
	}


	/**
	 * @param tcapType
	 * @param req
	 * @return
	 * @throws MandatoryParameterNotSetException
	 * @throws ListenerNotRegisteredException
	 */
	private synchronized JainTcapListener getUserForAnsiV2(TcapType tcapType, SipServletRequest req,
			TcapTriggeringCriteriaRule triggeringCriteriaRule)
					throws MandatoryParameterNotSetException, ListenerNotRegisteredException {

		if (logger.isDebugEnabled()) {
			logger.debug("Inside getUserForAnsiV2 new Logic");
		}

		DialogueIndEvent die = tcapType.getDialogueIndEvent();
		SccpUserAddress sua = null;
		SccpUserAddress origsua = null;
		GlobalTitle gtt = null;
		TcapNextAppInfo nextAppInfo = null;
		String nextApp = null;
		int pointCode = 0;

		String dlgId = (String) req.getHeader(TC_CORR_ID_HEADER);

		int tcCorrId = -1;

		int dialogueId = -1;
		if (dlgId == null) {
			dialogueId = die.getDialogueId();
		} else {
			dialogueId = Integer.parseInt(dlgId);
			tcCorrId = dialogueId;
		}

		JainTcapListener jtl = null;
		TcapSession ts = replicator.getTcapSession(dialogueId);
		int invokeId = 1;
		if (logger.isDebugEnabled()) {
			logger.debug("tcapSession : " + ts);
		}
		boolean isInitialMessagePresent = false;
		if (ts == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("no TcapSession found for DialodueId - " + dialogueId);
			}
			try {
				sua = die.getDestinationAddress();
				if (die.getPrimitiveType() == TcapConstants.PRIMITIVE_BEGIN) {
					origsua = ((BeginIndEvent) die).getOriginatingAddress();
					req.setAttribute(BEGIN_REQ, "true");
				} else if (die.getPrimitiveType() == TcapConstants.PRIMITIVE_UNIDIRECTIONAL) {
					origsua = ((UnidirectionalIndEvent) die).getOriginatingAddress();
					req.setAttribute(BEGIN_REQ, "true");
				}
				if (logger.isDebugEnabled()) {
					logger.debug(dialogueId + ":: Got Destin sua ::" + sua);
					logger.debug(dialogueId + ":: Got Orig sua ::" + origsua);
				}

				// code to get point code
				try {
					SignalingPointCode signalingPointCode = origsua.getSubSystemAddress().getSignalingPointCode();
					int zone = signalingPointCode.getZone();
					int cluster = signalingPointCode.getCluster();
					int member = signalingPointCode.getMember();
					if (logger.isInfoEnabled()) {
						logger.info(dialogueId + " :: [PH] Origin zone= " + zone + " cluster=" + cluster + " member="
								+ member);
					}

					String zcmFormat = zone + "-" + cluster + "-" + member;
					String pcBitStr = lPad(Integer.toBinaryString(zone), 8) + lPad(Integer.toBinaryString(cluster), 8)
					+ lPad(Integer.toBinaryString(member), 8);
					if (logger.isInfoEnabled()) {
						logger.info(dialogueId + " :: [PH] pcBitStr =" + pcBitStr);
					}
					pointCode = Integer.parseInt(pcBitStr, 2);
					if (logger.isInfoEnabled()) {
						logger.info("pointcode = " + pointCode);
					}

				} catch (Exception e) {

					logger.info("excpetion  occured while getting point code ");
				}

				List<ComponentIndEvent> components = tcapType.getComponentIndEvent();
				Iterator<ComponentIndEvent> iComponent = components.iterator();
				byte[] parms = null;
				String opCodeStr = "";

				while (iComponent.hasNext()) {
					if (logger.isDebugEnabled()) {
						logger.debug("In side iComponent.hasNext() ");
					}
					ComponentIndEvent ciet = (ComponentIndEvent) iComponent.next();
					// if not invoke
					if (ciet.getPrimitiveType() != TcapConstants.PRIMITIVE_INVOKE) {
						if (logger.isDebugEnabled()) {
							logger.debug(dialogueId + ":: For AIN : Not a Invoke component move to next");
						}
						continue;
					}
					InvokeIndEvent receivedInvoke = (InvokeIndEvent) ciet;
					Operation opr = receivedInvoke.getOperation();
					invokeId = receivedInvoke.getInvokeId();
					byte[] opCode = opr.getOperationCode();
					opCodeStr = formatBytes(opCode);

//					String opCodesString = triggeringCriteriaRule.getOpCodes();
//					if (logger.isDebugEnabled()) {
//						logger.info("opCodesString from trigerring rule file:" + opCodesString);
//					}
					//List<String> opsCodes = AseUtils.splictStringToList(opCodesString, ",");

					// check if opcode matches in yaml file
					if (opsCodes.contains(opCodeStr)) {

						isInitialMessagePresent = true;

						parms = receivedInvoke.getParameters().getParameter();
						if (logger.isDebugEnabled()) {
							logger.debug("Calling tcapRoutingController utility with params for ain :" + parms[0] + " "
									+ parms[1] + " " + parms[2] + " " + parms[3]);
						}

						break;
					} // end if for opcode 0x64 0x05, 0x64 0x03
				} // end while for component

				/**
				 * Logic to get ssn, opcode and serviceKey Condtion String is like
				 * ssn:opCode:serviecKey
				 */
				String ssnFromTcapRequest = String.valueOf(sua.getSubSystemAddress().getSubSystemNumber());
				String opCode = null;
				String serviceKey = null;
				String appSelectionCriteriaCondition = null;
				Integer translationType = null;
				
				if (opCodeStr != null) {
					opCode = opCodeStr;
				}
				
				if(origsua.isGlobalTitlePresent()){
					gtt = origsua.getGlobalTitle();
					translationType = fetchTransaltionType(gtt);
				}

				String conditionString = ssnFromTcapRequest + ":" + opCode + ":" + serviceKey;

				String nextAppId = getNextAppId(triggeringCriteriaRule, conditionString, translationType);
				/**
				 * If not null then return no need to go for DB call else do as usual
				 */
				if (logger.isDebugEnabled()) {
					logger.debug("nextAppId:" + nextAppId);
				}

				// First search in static rule file (triggering-rule.yml) if it contains SSN:Opcode:SK tupple
				// then trigger application based on app id defined in file. else go to next triggering rule 
				// criteria based on ade. 
				// same content in triggering-rule.yml is as below
				// opCodes: "0x64 0x05,0x64 0x05,0x64 0x02,0x64 0x17,0x64 0x03"
				// triggeringRules:
				//		- ssn: "249"
				//		  opsCode: "0x69 0x03"
				//        appId: "116"
				//
				if (nextAppId != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("rule matched for  app id: " + nextAppId + " .So no db/ADE call will be done ");
					}
				} else {
					// first fetch calledparty from SCCP digits in case of GTT. else continue to extract from 
					// incoming message
					String receivedDigits = null;
					if(origsua.isGlobalTitlePresent()){
						gtt = origsua.getGlobalTitle();

						// check if address signal is present. 
						if(gtt.getAddressInformation() != null){

							if(logger.isDebugEnabled()){
								logger.debug(dialogueId + ":: Address signal received: " + 
										formatBytes(gtt.getAddressInformation()));
							}

							if(isSCCPDigitsToBeUsed != 0){
								receivedDigits = convertDigitsToString(dialogueId, gtt.getAddressInformation());
							}else{
								if(logger.isDebugEnabled()){
									logger.debug(dialogueId + 
									" not using GT address as ss7.usegtdigits in ase.yml is 0 or not defined");
								}
							}
						}
					}

					// check for param (must be present)
					if(parms != null){
						try {
							if(logger.isDebugEnabled()){
								logger.debug(dialogueId + ":: calling getNextAppListener - 4 args: " +opCodeStr );
							}
							nextAppInfo = tcapRoutingController.getNextAppListenerV2(parms, opCodeStr, receivedDigits,
												ssnFromTcapRequest, PROTOCOL_ANSI, pointCode, serviceKey, sua, dialogueId);
						} catch (Exception e) {
										logger.error("Exceotion occured "+e);
						} 
					}else{
							if(logger.isDebugEnabled()){
										logger.debug(dialogueId + ":: calling getNextAppListener - 2 args: " );
							}
							nextAppInfo = tcapRoutingController.getNextAppListener(receivedDigits, sua);
					}

					if(nextAppInfo != null){
						nextAppId = nextAppInfo.getNextAppInfo();
								
						if(logger.isDebugEnabled()){
									logger.debug(dialogueId + " Next Application ID found as:"+ nextAppId);
						}
					}else{
						logger.debug(dialogueId + "NextApp not found from DB, TrigeeringCriteria");
					}
				}

				if (nextAppId != null) {
					jtl = tcapAppRegistry.getListenerForAppName(nextAppId, false);

					if (logger.isDebugEnabled()) {
						logger.debug(dialogueId + ":: fetched JTL for appName:" + nextAppId + ", jtl returned:" + jtl);
					}
				}
				// get jtl based on protocol currently supported
				// if protocol type is ANSI no need to parse SK and directly
				// route on SUA

				/**
				 * |ProtocolHandlerServlet need to check
				 */
				if (jtl == null) {
					if (logger.isDebugEnabled()) {
						logger.debug(
								dialogueId + ":: Since JTL returned fron GT is NULL, tryign to fetch based on SUA");
					}
					jtl = getListenerForSua(sua);
				}
				if (logger.isDebugEnabled()) {
					logger.debug(dialogueId + ":: Got JTL::" + jtl);
				}

				if (jtl != null) {
					
              boolean active=isAppActive(((TcapListener)jtl));
					
					if(!active){
						throw new ListenerNotRegisteredException(
								"Listener is not registered for req::" + req.getMethod());
					}
					// Check SUA status
					TcapProviderGateway tpg = TcapProviderGateway.getTcapProviderGateway(req);
					if (tpg != null) {
						LinkedList<SccpUserAddress> ssaList = tpg.getSSAList();

						for (SccpUserAddress ssa : ssaList) {
							if (ssa.toString().equals(sua.toString())) {
								if (logger.isDebugEnabled()) {
									logger.debug("SUA : " + ssa + " status  : " + ssa.getSuaStatus());
								}
								if (ssa.getSuaStatus() != SccpConstants.USER_IN_SERVICE) {
									throw new ListenerNotRegisteredException("SUA is NOT in-service.");
								}
								break;
							}
						}
					}

					if (logger.isDebugEnabled()) {
						logger.debug("creating tcap session");
					}

					ts = createTcapSession(dialogueId, jtl, req, sua, origsua);

					// For serialization at the time of partialActivate will
					// help to find jtl
					TcapListener listener = (TcapListener) jtl;
					TcapSessionCount.getInstance().addTcapDialog(listener.getName() + "_" + listener.getVersion(),
							String.valueOf(dialogueId));

					if (nextAppInfo != null) {

						if (nextAppInfo.isServiceKey()) {
							if (logger.isDebugEnabled()) {
								logger.debug("tcapsession setting attribute ServiceKey");
							}
							ts.setAttribute(ServiceKey, nextAppInfo.getNextAppInfo());
						} else {
							if (logger.isDebugEnabled()) {
								logger.debug("tcapsession setting attribute appname");
							}
							ts.setAttribute(AppName, nextAppInfo.getNextAppInfo());
						}
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("setAppname as " + listener.getName());
						}
						ts.setAttribute(AppName, listener.getName());
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("no listener for Message destined for SUA - " + sua);
					}
				}
			} catch (ParameterNotSetException pnse) {
				if (logger.isDebugEnabled()) {
					logger.debug("Got ParameterNotSetException in getUSer", pnse);
				}
				throw new MandatoryParameterNotSetException("MandatoryParamNotsetExceprion", pnse);
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("find listener from tcapSession");
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Update DialogueId in Existing TcapSession ");
			}
			TcapSessionImpl tsi = (TcapSessionImpl) ts;
			tsi.setDialogueId(dialogueId);

			jtl = (JainTcapListener) ts.getAttribute(ListenerApp);
		} // end if ts = null/not null

		if (jtl == null) {
			throw new ListenerNotRegisteredException("Listener is not registered for req::" + req.getMethod());
		}

		return jtl;
	}

	/**
	 * Get next app Id based on trigregging rule file
	 * 
	 * @param triggeringCriteriaRule
	 * @param conditionString
	 * @return
	 */
	private static String getNextAppId(TcapTriggeringCriteriaRule triggeringCriteriaRule, String conditionString,
			Integer translationType) {
		if (logger.isDebugEnabled()) {
			logger.debug("In getNextAppId conditionString to check nextAppListener: ie ssn:opsCode:serviceKey: "
					+ conditionString + ", translationType:" + translationType);
		}
		String nextAppId = null;
		LinkedHashSet<TcapTriggeringRule> triggeringRules = triggeringCriteriaRule.getTriggeringRules();
		// TODO Auto-generated method stub
		for (TcapTriggeringRule tRule : triggeringRules) {
			String rule = tRule.getSsn() + ":" + tRule.getOpsCode() + ":" + tRule.getServiceKey();

			if (rule.equals(conditionString)) {
				// check for Translation type if both incoming and defined 
				// values are not null
				if(translationType != null && tRule.getTt(1) != null){
					if(!translationType.equals(tRule.getTt(1))){
						if(logger.isDebugEnabled()){
							logger.debug("getNextAppId: rule matched: " + rule 
									+ " however TT did not match. rx TT:" + translationType 
									+ ", rule tt:" + tRule.getTt());
						}
						continue;
					}
				}
				nextAppId = tRule.getAppId();
				if (nextAppId != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("Matched rule criteria returning nextAppId:" + nextAppId);
					}
					break;
				}
			}
		}
		return nextAppId;
	}

	/**
	 * This method appends 0 as leading digits to input.
	 * 
	 * @param input
	 * @param resultSize
	 * @return
	 */
	private static String lPad(String input, int resultSize) {
		if (input == null) {
			return input;
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < (resultSize - input.length()); i++) {
			result.append("0");
		}
		result.append(input);
		return result.toString();
	}

	JainTcapListener getListenerForSua(SccpUserAddress sua) {
		JainTcapListener jtl =null;
		List<JainTcapListener> list = tcapAppRegistry.getListenerForSUA(sua.getString());
		if (logger.isDebugEnabled()) {
			logger.debug(sua.toString() + ":: ANSI listener list is::" + list);
		}
		if (list != null)
			jtl = list.get(0);
		return jtl;
	}

	private TcapSession createTcapSession(int dialogueId, JainTcapListener jtl,
			SipServletRequest req, SccpUserAddress sua, SccpUserAddress origsua) {
		if(logger.isDebugEnabled()){
			logger.debug(dialogueId+"::Inside create Tcapsesison for initial request");
		}

		TcapSession ts = replicator.getTcapSession(dialogueId, jtl);
		if(logger.isDebugEnabled()){
			logger.debug( dialogueId+ "::created tcap session: " + ts );
		}
		//setting attributess in TS
		ts.setAttribute(SccpUserAddressAttr, sua);
		ts.setAttribute(ORIG_SccpUserAddressAttr, origsua);
		ts.setAttribute(ListenerApp, jtl);

		SipApplicationSession appSession = req.getApplicationSession();
		ts.setAttribute(APPLICATION_SESSION, appSession.getId());

		//setting cdr context in ts
		ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);             
		String sysappEnable = (String)config.getValue(Constants.PROP_SYSAPP_ENABLE);	
		if(sysappEnable != null && sysappEnable.trim().contains("cdr")) {
			ts.setAttribute(Constants.CDR_KEY, req.getSession().getAttribute(Constants.CDR_KEY_FOR_TCAP));
			if(logger.isDebugEnabled()){
				logger.debug(dialogueId+ "::Attribute CDR set to [" + ts.getAttribute(Constants.CDR_KEY) + "]");
			}
		}
		return ts;
	}

	private LinkedList<ComponentReqEvent> getCurrentComponentsFor(DialogueReqEvent dre) throws MandatoryParameterNotSetException
	{
		return currentComps.get(dre.getDialogueId());
	}

	private void clearComponentRequest(DialogueReqEvent dre) throws MandatoryParameterNotSetException
	{
		currentComps.remove(dre.getDialogueId());
	}

	private void storeComponentRequest(ComponentReqEvent cre) throws MandatoryParameterNotSetException
	{
		LinkedList<ComponentReqEvent> comps = currentComps.get(cre.getDialogueId());
		if (comps == null)
		{
			comps = new LinkedList<ComponentReqEvent>();
			currentComps.put(cre.getDialogueId(), comps);
		}

		comps.add(cre);
	}

	public void sessionCreated(SipApplicationSessionEvent ev)
	{
		TcapProviderGateway.sessionCreated(ev);
	}

	public void sessionDestroyed(SipApplicationSessionEvent ev)
	{
		TcapProviderGateway.sessionDestroyed(ev);
	}

	public void sessionExpired(SipApplicationSessionEvent ev)
	{
		String at = (String)ev.getApplicationSession().getAttribute("AT");
		if(logger.isDebugEnabled()){
			logger.debug( "session Expired (milliseconds):" + at);
		}
		SignalingPointCode origSpc = (SignalingPointCode)ev.getApplicationSession().getAttribute("ORIGSPC");
		if(at != null){
			Long atTimer = Long.valueOf(at);
			atTimer = ((atTimer/1000)/60);
			if(logger.isDebugEnabled()){
				logger.debug( "session Expired (minutes):" + atTimer);
			}
			ev.getApplicationSession().setExpires(atTimer.intValue());
		}else if(origSpc != null){
			ev.getApplicationSession().setExpires(5);
		}else {
			TcapProviderGateway.sessionExpired(ev);
		}
	}	

	//added for RSN message handling
	//added for ACTIVITY_TEST Time

	public void sessionReadyToInvalidate(SipApplicationSessionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(EventObject event) {
		ATHandler ath = new ATHandler();
		ath.triggerActivityTest();		
	}

	public TcapSessionReplicator getReplicator()
	{
		return  this.replicator;
	}

	private void addSeqDlgParameter(SipServletRequest ssr, int seq, int dialogueId){
		SipURI requestURI = (SipURI) ssr.getRequestURI();
		String tcSeq = null;
		if (seq < 10)
			tcSeq = "00" + String.valueOf(seq);
		else if (seq >= 10 && seq < 100)
			tcSeq = "0" + String.valueOf(seq);
		else
			tcSeq = String.valueOf(seq);
		String seqdlg = "seq-dlg";
		requestURI.setParameter(seqdlg, tcSeq + "-" + new Integer(dialogueId).toString());
		ssr.setRequestURI(requestURI);
	}

	@Override
	public void tcapListenerActivated(JainTcapListener listener) {
		if(logger.isDebugEnabled()){
			logger.debug("listener is now activated ");
		}
		tcapAppRegistry.setTcapListenerState(listener,true);

	}

	@Override
	public void tcapListenerDeActivated(JainTcapListener listener) {
		if(logger.isDebugEnabled()){
			logger.debug("listener is now de_Activated ");
		}
		tcapAppRegistry.setTcapListenerState(listener,false);

	}

	/**
	 * added to support start of HB in case of failure
	 */
	@Override
	public void roleChanged(String paramString, PartitionInfo pInfo) {

		short role = pInfo.getRole();

		if (logger.isDebugEnabled()) {
			logger.debug("roleChanged(): Subsystem role in cluster has been changed to: " + AseRoles.getString(role));
		}

		if (role == AseRoles.ACTIVE) {
			if (logger.isDebugEnabled()) {
				logger.debug("roleChanged(): Role changed to active Check HB");
			}

			//start timer to check HB
			Timer sasActivatedTimer = new Timer();

			sasActivatedTimer.schedule(new SasActivationTimerTask(), ftHbRestartTime*1000);

			if (logger.isDebugEnabled()) {
				logger.debug("Timer started");
			}
		}

	}

	/**
	 * This method will compare established HB session with all ingw list 
	 *  and start HB on pending INGws; 
	 *  written to support MULTIPLE ingw in future
	 */
	public void startHB() {
		if (logger.isDebugEnabled()) {
			logger.debug("Inside startHB");
		}

		List<TcapProviderGateway> tpgInstances = TcapProviderGateway.getAllInstances();
		Iterator<INGateway> gws = ingwManager.getAllINGateways();

		List<String> IngwIdList = new ArrayList<String>();

		List<INGateway> pendingGatewayList = new ArrayList<INGateway>();

		String ingwId = null;

		for(TcapProviderGateway tpg : tpgInstances){
			ingwId= tpg.INGwId();
			INGateway ingw = ingwManager.getINGateway(ingwId);
			if(ingw == null){
				//disconnect TPGS not matching to current INGW list
				if (logger.isDebugEnabled()) {
					logger.debug("Disconnect tpg for ID::"+ingwId);
				}
				tpg.disconnect();
			}else{
				IngwIdList.add(ingwId.intern());
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Got IngwIdList as::"+IngwIdList);
		}


		while(gws.hasNext()){
			INGateway gw = gws.next();
			ingwId = gw.getId().intern();
			if(IngwIdList.contains(ingwId)){
				if (logger.isDebugEnabled()) {
					logger.debug("HB activated for INGW::"+ingwId);
				}
			}else{
				if (logger.isDebugEnabled()) {
					logger.debug("HB Not activated INGW::"+ingwId);
				}
				pendingGatewayList.add(gw);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Got pendingGatewayList as::"+pendingGatewayList);
			logger.debug("Start HB for Pending GW");
		}

		try {
			initializeHB(pendingGatewayList.iterator());
		} catch (ServletParseException e) {
			logger.error("Error starting HB");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Leave startHB");
		}

	}

	@Override
	public void handleEvent(ResourceEvent event) throws ResourceException {
		SipApplicationSession appSession = event.getApplicationSession();
		JainTcapListener jtl = getJtlFromAppSession(appSession);
		if(ResourceListener.class.isInstance(jtl)){
			((ResourceListener)jtl).handleEvent(event);
		}
	}

	@Override
	public void handleMessage(Message message) throws ResourceException {
		SipApplicationSession appSession = message.getApplicationSession();
		JainTcapListener jtl = getJtlFromAppSession(appSession);
		if(MessageHandler.class.isInstance(jtl)){
			((MessageHandler)jtl).handleMessage(message);
		}
	}

	private JainTcapListener getJtlFromAppSession(SipApplicationSession appSession) {
		if (logger.isDebugEnabled()) {
			logger.debug("getJtlFromAppSession()");
		}
		JainTcapListener jtl = null;
		TcapSession ts = null;
		if (appSession.getAttribute("Tcap-Session") != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("TS present");
			}
			ts = getTcapSession(((Integer) appSession.getAttribute("Tcap-Session")));

		}
		if (logger.isDebugEnabled()) {
			logger.debug("getJtlFromAppSession() ts::"+ts);
		}

		if (ts != null) {
			jtl = (JainTcapListener) ts.getAttribute(ListenerApp);
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getJtlFromAppSession() jtl::"+jtl);
		}
		return jtl;

	}

	public int getProtocol(){
		return protocol;
	}

	@Override
	public void init(ServletContext arg0) {
		if (logger.isDebugEnabled()) {
			logger.debug("ServletContext init(ServletContext) do nothing");
		}

	}

	static public void warmup() {
		try {
			logger.error("Starting Tcap Warmup..");
			JainTcapProviderImpl.getImpl().warmup(true);
			logger.error("Leaving Tcap Warmup..");
		} catch (Exception e) {
			logger.error("Tcap Warmup exception occured.." + e);
		}finally{
			logger.error("Tcap Warmup Completed..");
		}
	}


	private void warmup(boolean b) throws MandatoryParameterNotSetException, TcapContentReaderException, IOException {

		Properties prop = new Properties();
		String fileName = System.getProperty("ase.home")+"/conf/msgbuffer.dat";
		InputStream is = new FileInputStream(fileName);
		prop.load(is);
		//reading properties
		String messageString = prop.getProperty("warmup.tcapmessages.hexMessageString");

		if(messageString != null ){

			String messages[] = messageString.split(",");
			for (int i = 0; i < messages.length; i++) {
				logger.error("Processing warmup message :: "+ (i+1));
				byte [] contentInByte = hexStringToByteArray(messages[i]);

				TcapType tcapType = TcapParser.parse(contentInByte, this);
				int dialogueId= tcapType.getDialogueIndEvent().getDialogueId();
				JainTcapListener objJainTcapListener =TcapAppRegistry.getInstance().getListenerForAppName(Integer.toString(dialogueId), true);
				if(objJainTcapListener ==null){
					logger.error("JTL not found for dialogueID::"+dialogueId);
					continue;
				}
				TcapSession ts = replicator.getTcapSession(dialogueId, objJainTcapListener);
				if(logger.isDebugEnabled()){
					logger.debug(dialogueId+"::created tcap session: " + ts );
				}
				ts.setAttribute(ListenerApp, objJainTcapListener);

				SipApplicationSession appSession = factory.createApplicationSession();
				ts.setAttribute(APPLICATION_SESSION, appSession.getId());
				ts.setAttribute("WARMUP", dialogueId);
				appSession.setAttribute(JainTcapProviderImpl.DIALOG_ID, Integer.toString(dialogueId));

				objJainTcapListener.processDialogueIndEvent(tcapType.getDialogueIndEvent());


				List<ComponentIndEvent> components = tcapType.getComponentIndEvent();
				Iterator<ComponentIndEvent> iComponent = components.iterator();
				while (iComponent.hasNext())
				{
					ComponentIndEvent ciet = (ComponentIndEvent)iComponent.next();
					objJainTcapListener.processComponentIndEvent(ciet);
				}
			}
		}
	}

	private static byte[] hexStringToByteArray(String s) {  
		int len = s.length();    
		byte[] data = new byte[len / 2];     
		for (int i = 0; i < len; i += 2) {        
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)  + Character.digit(s.charAt(i+1), 16));    
		}    
		return data;
	}

	@Override
	public void sessionDidActivate(SipApplicationSessionEvent arg0) {
		TcapSession ts = null;
		SipApplicationSession appsession = arg0.getApplicationSession();
		String dlgId = (String) appsession.getAttribute(JainTcapProviderImpl.DIALOG_ID);

		if (dlgId != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(dlgId
						+ " App sessionDidActivate: appsesion with dlg id");
			}
			ts = JainTcapProviderImpl.getImpl().getTcapSession(
					Integer.parseInt(dlgId));
			if (ts == null) {
				appsession.invalidate();
			}
		}



	}

	@Override
	public void sessionWillPassivate(SipApplicationSessionEvent arg0) {

		if (logger.isDebugEnabled()) {
			logger.debug("App sessionWillPassivate: "
					+ arg0.getApplicationSession().getId());
		}

	}

	@Override
	public void appSessionCleaned(String dialogId) {
		//XXX: this method is added for future use.
		/*int dlgId = Integer.parseInt(dialogId);
		TcapSession ts = getTcapSession(dlgId);
		if(ts!=null){
			if (logger.isDebugEnabled()) {
				logger.debug("Cleaning TS for dialogID as AS is getting cleaned:: "+dialogId);
			}
			ts.cleanup();
		}*/
	}

	private void loadPrintTcapInfoFile(){

		PRINT_TCAP_INFO_STATUS_FILE = Constants.ASE_HOME + File.separator
				+ "sysapps" + File.separator + "print-tcap-info.properties";

		if(logger.isInfoEnabled()){
			logger.info("Loading print-tcap-info status file : " + PRINT_TCAP_INFO_STATUS_FILE);
		}

		printTcapInfoStatusFile =  new Properties();

		FileInputStream inPropStream = null;
		try {

			File statusFile =new File(
					PRINT_TCAP_INFO_STATUS_FILE);

			if(!statusFile.exists())
				statusFile.createNewFile();

			inPropStream = new FileInputStream(statusFile);
			try {
				printTcapInfoStatusFile.load(inPropStream);
			} catch (FileNotFoundException e) {
				logger.error("print-tcap-info status file not found " + e.getMessage(),e);
			} catch (IOException e) {
				logger.error("IOException while loading print-tcap-info status file" + e.getMessage(),e);
			}

			String currentState = printTcapInfoStatusFile.getProperty(ENABLE_SS7_SIG_INFO);

			if (currentState != null && !currentState.isEmpty() && currentState.equals("1")) {
				isSS7MsgInfoEnabled = true;
				if (logger.isDebugEnabled()) {
					logger.debug("SS7MsgInfoEnabled : " + isSS7MsgInfoEnabled);
				}
			}else if(currentState == null){
				printTcapInfoStatusFile.setProperty(ENABLE_SS7_SIG_INFO, "0");
				storeProperty();
			}

		}catch(Exception e){
			logger.error("Exception while loading print-tcap-info status file" + e.getMessage(),e);
		} finally{
			try {
				inPropStream.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}

	}

	private void storeProperty() {

		FileOutputStream os = null;
		try {
			File statusFile =new File(
					PRINT_TCAP_INFO_STATUS_FILE);

			if(!statusFile.exists())
				statusFile.createNewFile();

			os = new FileOutputStream(statusFile);
		} 
		catch (FileNotFoundException e1) {
			logger.error("print-tcap-info status file not found " + e1.getMessage(),e1);
		}catch (IOException e1) {
			logger.error("IOException while loading print-tcap-info status file" + e1.getMessage(),e1);
		} 
		try {
			if (logger.isDebugEnabled()) {
				logger.debug(" Storing the state in property file "
						+ PRINT_TCAP_INFO_STATUS_FILE);
			}
			printTcapInfoStatusFile.store(os, null);
		} catch (IOException e1) {
			logger.error(e1);
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}

	}

	public void enableSS7MsgInfo(boolean enableSS7MsgInfo) {
		if (logger.isDebugEnabled()) {
			logger.debug("SS7MsgInfo is set as  "+ enableSS7MsgInfo);
		}
		isSS7MsgInfoEnabled = enableSS7MsgInfo;
		if(enableSS7MsgInfo){
			printTcapInfoStatusFile.setProperty(ENABLE_SS7_SIG_INFO,"1");
		}else{
			printTcapInfoStatusFile.setProperty(ENABLE_SS7_SIG_INFO,"0");
		}
		storeProperty();
	}

	public boolean isSS7MsgInfoEnabled(){
		return isSS7MsgInfoEnabled;
	}

	/**
	 * Method converts byte arrary to string of received digits. 
	 * It then converts each digit string to Hex string. 
	 * Final string it then converted to BCD format. 
	 * For example: received digits are in decimal 0x08, 0x40, (byte) 0x99, 0x69, 0x33 then it is converted to Hex
	 * 08->08, 40->28, 99->63, 69->45, 33->21 (0828634521) converted to BCD -> 8082365412
	 * @param data
	 * @return
	 */
	private String convertDigitsToString(int dialogueId, byte data[]){ 
		String value = "";
		String retVal  = "";
		String hexStr = "";
		for(int i = 0;i<data.length;i++)
		{
			value = value+ Integer.toString((data[i] & 0xff) + 0x100, 16).substring(1);
		}

		for (int i =0, j=1; i < value.length() && j < value.length(); ++i){
			retVal += value.charAt(j);
			retVal += value.charAt(j-1);

			j += 2;
		}


		if(logger.isDebugEnabled()){
			logger.debug(dialogueId + ":: ByteString: " + value + ", ReturnedString:" + retVal);
		}

		return retVal;
	}

	@Override
	public String execute(String command, String[] args, InputStream in,
			OutputStream out) throws CommandFailedException {
		StringBuffer buffer = new StringBuffer();

		if(CMD_SET_AVG_COUNT.equals(command)) {
			if(args.length == 1) {
				sAvgTimeCalcCount = Integer.parseInt(args[0]);
				buffer.append("Average calculate count gets updated to " + sAvgTimeCalcCount + " successfully");
				if(logger.isDebugEnabled()) {
					logger.debug("Average calculate count gets updated to : " + sAvgTimeCalcCount + " successfully");
				}
			}else {
				return getUsage(command);
			}
		}else if(CMD_SET_AVG_RES_THRES.equals(command)) {
			if(args.length == 1) {
				sAvgResponseTime = Integer.parseInt(args[0]);
				buffer.append("Average reponse time threshold gets updated to " + sAvgResponseTime + " successfully");
				if(logger.isDebugEnabled()) {
					logger.debug("Average reponse time threshold gets updated to " + sAvgResponseTime + " successfully");
				}
			}else {
				return getUsage(command);
			}
		}

		sInitialRequestCount.set(0);
		sTotalTimeFirstReqResp.set(0);

		logger.error("Current stats :: currentRequestCount : " + sInitialRequestCount.get() +
				", currentTotalResponseTime : " + sTotalTimeFirstReqResp.get() +
				", thresholdCount : " + sAvgTimeCalcCount +
				", thresholdTime : " + sAvgResponseTime);

		return buffer.toString();
	}

	@Override
	public String getUsage(String command) {
		if(CMD_SET_AVG_COUNT.equals(command)) {
			return "Usage: set-call-cnt-for-avg-resp <count>";
		}else if(CMD_SET_AVG_RES_THRES.equals(command)) {
			return "Usage: set-tcap-avg-resp-thres <count>";
		}
		return null;
	}

	private static void incrementTcapCounters(EventObject eventObject) {

		ConfigRepository configRepository = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String value = configRepository.getValue(Constants.PROP_DUMP_TCAP_COUNTERS);
		boolean dumpTcapCounters = (value != null && value.equals("1")) ? true : false; 
		if(!dumpTcapCounters) {
			return;
		}
		int primitiveType = -1;
		boolean isTransmitted = false;
		if(eventObject instanceof DialogueReqEvent) {
			isTransmitted = true;
			primitiveType = ((DialogueReqEvent) eventObject).getPrimitiveType();
		}else if(eventObject instanceof ComponentReqEvent) {
			isTransmitted = true;
			primitiveType = ((ComponentReqEvent) eventObject).getPrimitiveType();
		}else if(eventObject instanceof DialogueIndEvent) {
			primitiveType = ((DialogueIndEvent) eventObject).getPrimitiveType();
		}else if(eventObject instanceof ComponentIndEvent) {
			primitiveType = ((ComponentIndEvent) eventObject).getPrimitiveType();
		}
		if(logger.isDebugEnabled()) {
			logger.debug("going to increment tcap counter for primitiveType : " + primitiveType + " isTransmitted : " + isTransmitted);
		}

		switch(primitiveType) {

		case TcapConstants.PRIMITIVE_BEGIN:{
			if(isTransmitted)
				AseMeasurementUtil.counterTotalBeginsOut.increment();
			else
				AseMeasurementUtil.counterTotalBeginsIn.increment();
			break;
		}

		case TcapConstants.PRIMITIVE_CONTINUE:{
			if(isTransmitted)
				AseMeasurementUtil.counterTotalContinueOut.increment();
			else
				AseMeasurementUtil.counterTotalContinueIn.increment();
			break;
		}

		case TcapConstants.PRIMITIVE_END:{
			if(isTransmitted)
				AseMeasurementUtil.counterTotalEndOut.increment();
			else
				AseMeasurementUtil.counterTotalEndIn.increment();
			break;
		}

		case TcapConstants.PRIMITIVE_USER_ABORT:{
			if(isTransmitted)
				AseMeasurementUtil.counterTotalUAbortOut.increment();
			else
				AseMeasurementUtil.counterTotalUAbortIn.increment();
			break;
		}

		case TcapConstants.PRIMITIVE_NOTICE:{
			if(isTransmitted)
				AseMeasurementUtil.counterTotalNoticeOut.increment();
			else
				AseMeasurementUtil.counterTotalNoticeIn.increment();
			break;
		}

		case TcapConstants.PRIMITIVE_INVOKE:{
			if(isTransmitted)
				AseMeasurementUtil.counterTotalInvokesOut.increment();
			else
				AseMeasurementUtil.counterTotalInvokesIn.increment();
			break;
		}

		case TcapConstants.PRIMITIVE_RESULT:{
			if(isTransmitted)
				AseMeasurementUtil.counterTotalRetResultOut.increment();
			else
				AseMeasurementUtil.counterTotalRetResultIn.increment();
			break;
		}

		case TcapConstants.PRIMITIVE_ERROR:{
			if(isTransmitted)
				AseMeasurementUtil.counterTotalErrorOut.increment();
			else
				AseMeasurementUtil.counterTotalErrorIn.increment();
			break;
		}

		case TcapConstants.PRIMITIVE_REJECT:{
			if(isTransmitted)
				AseMeasurementUtil.counterTotalRejectOut.increment();
			else
				AseMeasurementUtil.counterTotalRejectIn.increment();
			break;
		}

		}
	}


	private boolean checkV2EnabledOrNot() {
		logger.info("starting load  properties file" + Constants.ASE_HOME + "/"
				+ Constants.FILE_CAS_STARUP_PROPERTIES);
		if (logger.isDebugEnabled()) {
			logger.debug("checkV2EnabledOrNot : ");
		}

		logger.info("appRoutingEnabled:" + appRoutingEnabled);
		if (appRoutingEnabled != null) {
			if (appRoutingEnabled.equals("true")) {
				return true;
			} else {
				return false;
			}
		}

		return false;
	}
	
	/**
	 * check if the application is active
	 * @param appName
	 * @return
	 */
	private boolean isAppActive(TcapListener jtl){

		String jtlname=jtl.getName();
		String appdisName=jtl.getDisplayName();
		logger.info("isAppActive App Name "+ jtlname +" App Display name "+appdisName);
		boolean active=false;	
		Iterator it=null;
		DeployableObject app =null;
		try {
			    it = appDeployer.findByName(jtlname);
			    app = it.hasNext() ? (DeployableObject) it.next()
					: null;

			if (app == null && appdisName != null) {
				it = appDeployer.findByName(appdisName);
				app = it.hasNext() ? (DeployableObject) it.next() : null;
			}
			if (app != null) {

				if (app.getState() == DeployableObject.STATE_ACTIVE) {
					active = true;
				} else {
					active = false;
				}
			}
		} catch (Exception e) {		
			
			try{
				if (app == null && appdisName != null) {
					it = appDeployer.findByName(appdisName);
					app = it.hasNext() ? (DeployableObject) it.next() : null;
				}
				if (app != null) {

					if (app.getState() == DeployableObject.STATE_ACTIVE) {
						active = true;
					} else {
						active = false;
					}
				}
			} catch (Exception ee) {
			}
		}
		logger.info("isAppActive retun "+active);
		return active;
	}
	
	/**
	 * Method is used to fetch Transaltion Type from received Global Title
	 * @param gtt
	 * @return
	 * @throws ParameterNotSetException 
	 */
	private int fetchTransaltionType(GlobalTitle gtt) {
		Integer retVal = null;
		int gtIndType=0;
		
		if(gtt == null){
			return retVal;
		}
		
		try{
			switch(gtt.getGTIndicator()){
			case jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0001:
				retVal = ((GTIndicator0001) gtt).getTranslationType() & 0xFF;
				gtIndType=1;
				break;
			case jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0010:
				retVal = ((GTIndicator0010) gtt).getTranslationType() & 0xFF;
				gtIndType=2;
				break;
			case jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0011:
				retVal = ((GTIndicator0011) gtt).getTranslationType() & 0xFF;
				gtIndType=3;
				break;
			case jain.protocol.ss7.tcap.TcapConstants.GTINDICATOR_0100:
				retVal = ((GTIndicator0100) gtt).getTranslationType() & 0xFF;
				gtIndType=4;
				break;
				default:
			}
		}catch(Exception exp){
			logger.error("fetchTransaltionType Unsupported dialogue ID:"+gtt.getGTIndicator());
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("GT Indicator Type:" + gtIndType + ", Translation Type:" + retVal);
		}
		return retVal;
	}

	private Deployer applicationDeployer = null;
}

