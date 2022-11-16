package com.agnity.simulator;

import jain.InvalidAddressException;
import jain.ListenerNotRegisteredException;
import jain.protocol.ss7.JainSS7Factory;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.SignalingPointCode;
import jain.protocol.ss7.SubSystemAddress;
import jain.protocol.ss7.UserAddressEmptyException;
import jain.protocol.ss7.UserAddressLimitException;
import jain.protocol.ss7.sccp.SccpConstants;
import jain.protocol.ss7.sccp.StateIndEvent;
import jain.protocol.ss7.sccp.management.NPCStateIndEvent;
import jain.protocol.ss7.sccp.management.NStateIndEvent;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.TcapErrorEvent;
import jain.protocol.ss7.tcap.TcapUserAddress;
import jain.protocol.ss7.tcap.TimeOutEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import org.apache.log4j.Logger;
import com.agnity.simulator.callflowadaptor.NodeManager;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.type.StartNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.SimulatorConfig;
import com.agnity.simulator.logger.SuiteLogger;
import com.agnity.simulator.statistics.Counters;
import com.agnity.simulator.tasks.CallStartTimerTask;
import com.agnity.simulator.tasks.InitializeTask;
import com.agnity.simulator.tasks.RsnTimerTask;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.tcap.parser.Util;
import com.genband.tcap.provider.TcapListener;
import com.genband.tcap.provider.TcapProvider;
import com.genband.tcap.provider.TcapSession;

import java.awt.*;
import java.awt.event.*;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;


@SuppressWarnings("deprecation")
public class InapIsupSimServlet extends SipServlet implements TcapListener {



	/**
	 * 
	 */
	private static final long serialVersionUID = -8634161986388055753L;

	private static Logger logger = Logger.getLogger(InapIsupSimServlet.class);

	private static InapIsupSimServlet instance;

	//
	private SimulatorConfig simConfig;
	private Timer timer;
	private Timer timeoutTimer;
	private TcapProvider tcapProvider;
	private SipFactory factory;
	private NodeManager nodeManager;
	
	//point codes
	private SignalingPointCode remoteSpc;
	private SccpUserAddress remoteAddr;
	//	private static SignalingPointCode localSpc;
	//	private static SccpUserAddress localAddr;
	private ArrayList<SignalingPointCode> localSpc;
	private ArrayList<SccpUserAddress> localAddr ;

	//call data structures
	private Map<Integer, SimCallProcessingBuffer> tcapCallData;
	private Map<String, SimCallProcessingBuffer> sipCallData;
	private Map<String, SimCallProcessingBuffer> appSessionIdCallData;
	private List<String> listSrvKey;
	private String flowType;
	
	private Iterator<String> fileNameIterator;
	private String currentFileName;
	private boolean flowInitialized;
	


	
	private String serviceKeys;
	private String inviteSessionId;
	private SipApplicationSession heartbeatAppSesion;

	//param if simulator running inINC mode
	private boolean isIncMode;	
	//param if simulator running in B2B mode
	public boolean isB2bMode;	
	//mark true when inc is connected(both when acting as INC or acting as Service)
	private boolean isIncConnecetd;
	//mark true when running test suite and not load
	private boolean isTestSuite;
	//param if support AT from JTP
	private boolean isActivityTestSupported;
	// to check it is for RSN/RSA
	private boolean isRSN_RSA;
	
	private final String  IN_GW_SESSION_ID= "ingw-sessionid";
	
	public Properties prop1;
		
	/*
	 * (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy(){

		if(logger.isInfoEnabled())
			logger.info("servlet destroy()-->Enter");

		super.destroy();

		try {
//			tcapProvider.removeJainTcapListener(this, listSrvKey, Constants.APP_NAME,Constants.APP_VERSION);
			tcapProvider.removeJainTcapListener(this, listSrvKey);
		} catch (ListenerNotRegisteredException e) {
			if(logger.isDebugEnabled())
				logger.debug("ListenerNotRegisteredException removing JTP",e);
		} catch (IOException e) {
			if(logger.isDebugEnabled())
				logger.debug("IOException removing JTP",e);
		}

		instance = null;
		simConfig=null;;
		timer=null;
		timeoutTimer =null;;
		tcapProvider = null;
		factory =null;
		nodeManager=null;

		localAddr= null; //Multiple SUA
		localSpc = null;//Multiple PC
		remoteAddr = null;
		remoteSpc=null;

		tcapCallData= null;
		sipCallData = null;
		appSessionIdCallData = null;
		listSrvKey = null;
		flowType = null;

		fileNameIterator=null;
		currentFileName= null;

		if(heartbeatAppSesion!=null)
			heartbeatAppSesion.invalidate();
		inviteSessionId= null;
		heartbeatAppSesion=null;

		isIncMode = false;	
		isB2bMode = false;
		isIncConnecetd = false;
		isTestSuite = false;
		setFlowInitialized(false);
		if(logger.isInfoEnabled())
			logger.info("servlet destroy()-->complete");
	}

	/**
	 * 1. Loads the simulator configuration
	 * 2. Register with TcapProvider
	 * 3. Creates call data structures
	 * 4. Parses Call flow and stores the same
	 * 5. Schedule timer for pure ISUP flows 
	 *  
	 * (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		if(logger.isInfoEnabled())
			logger.info("Inside InapIsupSimServlet init()");
		
		super.init(config);
		
		//Added for testing 
//		ServletContext context =config.getServletContext();
//		logger.info("context is"+context);
//		AseAlarmUtil alarmService =
//			(AseAlarmUtil)context.getAttribute(com.baypackets.ase.util.Constants.NAME_ALARM_SERVICE);
//		alarmService.raiseAlarm(1286,"farzi alarm" );
//		
//		logger.info("Inside InapIsupSimServlet init() raised alarm");
		
		
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet init()-->loading properties");
		//loading properties
		//		String[] paths={"simulatorconfiguration.xml"};
		//		ApplicationContext appContext=new ClassPathXmlApplicationContext(paths);
		//		simConfig=((SimulatorConfig) appContext.getBean("simulatorConfig"));

		//initializing variables::
		instance = this;
		localAddr= new ArrayList<SccpUserAddress>(); //Multiple SUA
		localSpc = new ArrayList<SignalingPointCode>(); //Multiple PC
		listSrvKey = new ArrayList<String>();
		isIncMode = false;	
		isB2bMode = false;
		isIncConnecetd = false;
		isTestSuite = false;
		setFlowInitialized(false);

		try{
			prop1 = new Properties();

			String fileName1 = System.getProperty("ase.home")+"/conf/simulator/simulator.properties";
			InputStream is1 = new FileInputStream(fileName1);
			prop1.load(is1);


			Properties prop2 = new Properties();

			String fileName2 = System.getProperty("ase.home")+"/conf/tcapProvider.properties";
			InputStream is2 = new FileInputStream(fileName2);
			prop2.load(is2);

			//reading properties
			simConfig = new SimulatorConfig();

			simConfig.setCpsIncrementFreq(Integer.parseInt(prop1.getProperty("cps.increase.freq")));
			simConfig.setCpsIncremntValue(Integer.parseInt(prop1.getProperty("cps.increase.value")));
			//simConfig.setInfoTcapMessage(prop1.getProperty("connector.inc.info.request"));
			simConfig.setInitialCps(Integer.parseInt(prop1.getProperty("cps.initail")));
			//simConfig.setInviteRespTcapMessage(prop1.getProperty("connector.inc.invite.response"));
			simConfig.setLocalPc(prop1.getProperty("simulator.inc.localpc"));
			simConfig.setLocalSsn(prop1.getProperty("simulator.inc.localssn"));
			simConfig.setMaxCps(Integer.parseInt(prop1.getProperty("cps.max")));
			simConfig.setRemotePc(prop1.getProperty("simulator.inc.remotepc"));
			simConfig.setRemoteSsn(prop1.getProperty("simulator.inc.remotessn"));
			serviceKeys = prop1.getProperty("simulator.inc.sk");
			
			if(!(serviceKeys.equals("")))
				simConfig.setServiceKey(serviceKeys.split(","));
			
			simConfig.setTotalCalls(Integer.parseInt(prop1.getProperty("calls.total")));
			simConfig.setProtocolVariant(Integer.parseInt(prop1.getProperty("simulator.inc.protocolvariant")));
			simConfig.setSdp(prop1.getProperty("simulator.nbs.sdp"));
			simConfig.setPublishingTime(Integer.parseInt(prop1.getProperty("simulator.sleep.publishing.going")));
			simConfig.setCallFlowFileNames(prop1.getProperty("callflow.file.name"));
			simConfig.setActivityTestTimeout(prop1.getProperty("tcap.activitytest.timeout"));
			simConfig.setRsnRsaTimeout(prop1.getProperty("tcap.rsnrsa.timeout"));
			simConfig.setTcapSessionTimeout(prop1.getProperty("tcap.session.timeout"));
			simConfig.setSendSccpConfigWithInfo(Boolean.parseBoolean(prop1.getProperty("simulator.inc.sccpconfimsg.info.enable")));
			simConfig.setDelaySccpConfigWithInfo(prop1.getProperty("simulator.inc.sccpconfimsg.info.delay"));
			simConfig.setActivityTestResponse(prop1.getProperty("simulator.activitytest.response"));
			//tcap app property
			simConfig.setEnableInc(Boolean.parseBoolean(prop2.getProperty("actAsIncSimulator")));
			simConfig.setEnableB2b(Boolean.parseBoolean(prop1.getProperty("inc.simulator.b2b.mode.on")));
			
			//uabortinformation object identifier and fixed byte array
			simConfig.setUabortInfoFixedPart((prop1.getProperty("simulator.uabort.info.fixed.part")));
			//acn version
			simConfig.setApplicationContextName((prop1.getProperty("simulator.begin.acn")));
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet init()-->properties loaded, register with TCAP application");

		}catch(FileNotFoundException e){
			logger.error("FileNotFoundException in properties loading",e);
			return;
		} catch (IOException e) {
			logger.error("IOException in properties loading",e);
			return;
		}
		
		
		//read local point code
		String localPc = simConfig.getLocalPc();
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet init()-->localpc::" + localPc);
		SignalingPointCode spcLocal = null;
		if (localPc != null) {
			String[] localPcList = localPc.split(",");
			for (int i=0; i < localPcList.length; i++) {
				spcLocal = null;
				String[] tmp = localPcList[i].split("-");
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet init()-->no of local spc configured::"+ tmp.length);
				if (tmp.length == 3) {
					try {
						spcLocal = new SignalingPointCode(Integer.parseInt(tmp[2]),
								Integer.parseInt(tmp[1]), Integer.parseInt(tmp[0]));
					} catch (Exception e) {
						logger.error("Exception creating local pc on::["+ tmp[i]+"]  exception message::"+e.getMessage());
					}
				}
				//creating default spc if local SPC is null for that iteration
				if (spcLocal != null) {
					localSpc.add(spcLocal);
				}
			}
		}
		if(localSpc.isEmpty()){
			throw new ServletException("Error::Local point codes list created list empty");
		}
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet init()-->no of local spc added::"+ localSpc.size());

		//read local SSN
		String localSsn = simConfig.getLocalSsn();
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet init()-->localSsn::" + localSsn);
		if (localSsn == null) {
			throw new ServletException("Error::local ssn is null");
		} else {
			String[] ssnList = localSsn.split(",");
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet init()-->no of ssn configured" + ssnList.length);

			if(ssnList.length != localSpc.size()){
				throw new ServletException("Error::Local SSN count and local SPC added is differnt");
			}
			for (int i=0; i < ssnList.length; i++) {
				try {
					localAddr.add(new SccpUserAddress(new SubSystemAddress(localSpc.get(i),
							Short.parseShort(ssnList[i]))));
					localAddr.get(i).setProtocolVariant(simConfig.getProtocolVariant());
				}catch (Exception e) {
					logger.error("Exception adding local Addr on::["+ ssnList[i]+"]  exception message::"+e.getMessage());
				}
			}
		}
		if(localAddr.isEmpty()){
			throw new ServletException("Error::Local SCCP user Addrs list is empty");
		}


		//reading remote Point code
		String remotePc = simConfig.getRemotePc();
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet init()-->remotepc::" + remotePc);
		remoteSpc = null;
		if (remotePc != null) {
			String[] tmp = remotePc.split("-");
			if (tmp.length == 3) {
				try {
					remoteSpc = new SignalingPointCode(
							Integer.parseInt(tmp[2]), Integer.parseInt(tmp[1]),
							Integer.parseInt(tmp[0]));
				} catch (Exception e) {
					logger.error("Exception creating remote pc on::["+ remotePc+"]  exception message::"+e.getMessage());
				}
			}
		}

		//Error if remote SPC is null
		if (remoteSpc == null) {
			throw new ServletException("Error::remote point SPC created is null");
		}


		//read rmote ssn
		String remoteSsn = simConfig.getRemoteSsn();
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet init()-->remoteSsn::" + remoteSsn);
		if (remoteSsn == null) {
			throw new ServletException("Error::remote ssn is null");
		} else {
			try {
				remoteAddr = new SccpUserAddress(new SubSystemAddress(
						remoteSpc, Short.parseShort(remoteSsn)));
				remoteAddr.setProtocolVariant(simConfig.getProtocolVariant());
			} catch (Exception e) {
				logger.error("Exception creating remote Addr on::["+ remoteSsn+"]  exception message::"+e.getMessage());
			}
		}
		if(remoteAddr == null){
			throw new ServletException("Error::Remote SCCP user Addrs created is null");
		}


		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet init()-->localAddr::" + localAddr + "   remoteAddr::" + remoteAddr);

		//getting Jain SS7 factory
		JainSS7Factory fact = JainSS7Factory.getInstance();

		try {
			//creating tcap provider form factory
			if(logger.isInfoEnabled())
				logger.info("InapIsupSimServlet init()-->obtaining JainTcapProvider");
			fact.setPathName("com.genband");
			tcapProvider = (TcapProvider) fact.createSS7Object("jain.protocol.ss7.tcap.JainTcapProviderImpl");
			if(logger.isInfoEnabled())
				logger.info("InapIsupSimServlet init()-->JainTcapProvider =" + tcapProvider);

			String[] serviceKey=simConfig.getServiceKey();

			//add jain tcap litener
			//			List<SccpUserAddress> list = new ArrayList<SccpUserAddress>();
			//			list.add(localAddr);
			if(serviceKey!=null)
			listSrvKey=Arrays.asList(serviceKey);
			
			if(logger.isInfoEnabled())
				logger.info("InapIsupSimServlet init()--> calling addJainTcapListener ");
			//			tcapProvider.addJainTcapListener(this, list, listSrvKey, "SimulatorApp");
//			tcapProvider.addJainTcapListener(this, localAddr, listSrvKey, Constants.APP_NAME,Constants.APP_VERSION);
			tcapProvider.addJainTcapListener(this, localAddr, listSrvKey);
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet init()--> for srvkey:"+ serviceKey+ "and one SSN registered...");

		} catch (Exception e) {
			logger.error("Exception in registration", e);
		}

		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet init()-->registration done, creating Call data maps");
		//create call data hash table
		tcapCallData = new ConcurrentHashMap<Integer, SimCallProcessingBuffer>();
		sipCallData =new ConcurrentHashMap<String, SimCallProcessingBuffer>();
		appSessionIdCallData = new ConcurrentHashMap<String, SimCallProcessingBuffer>(); 

		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet init()-->data structures created, Parsing XML");

		//initilizing sipfactory
		factory = (SipFactory) getServletContext().getAttribute(SIP_FACTORY);

		//creating file name list
		String fileNames = simConfig.getCallFlowFileNames();
		String[] fileNamesList=fileNames.split(",");
		if(fileNamesList.length > 1){
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet init()-->Test suite, disabling load");
			isTestSuite = true;
			simConfig.setTotalCalls(1);
		}
		List<String> filesList=Arrays.asList(fileNamesList);
		if(logger.isDebugEnabled())
			logger.debug("got fileNameList::"+filesList);
		fileNameIterator =filesList.iterator();


		//rsing the call flow
		if(logger.isDebugEnabled())
			logger.debug("initializing nodeManager");
		nodeManager = new NodeManager();

		isIncMode = simConfig.isEnableInc();
		if(isIncMode){
			if(logger.isDebugEnabled())
				logger.debug("Act as INC is true;");
		}else{
			if(logger.isDebugEnabled())
				logger.debug("Act as INC is false");
		}
		
		isB2bMode = simConfig.isEnableB2b();
		if(isB2bMode){
			if(logger.isDebugEnabled())
				logger.debug("Act as B2B is true;");
		}else{
			if(logger.isDebugEnabled())
				logger.debug("Act as B2B is false");
		}
		
		isActivityTestSupported = (!(isIncMode) && !(isTestSuite));
		
		isRSN_RSA = ((isIncMode)&&!(isTestSuite()));
		
		if(isRSN_RSA)
		{
		if(logger.isDebugEnabled())
		logger.debug(":::  starting RsnTimer");
		//start RsnTimer
			
		InapIsupSimServlet source = InapIsupSimServlet.getInstance();
		String waitTimeRsnRsaResult = source.getConfigData().getRsnRsaTimeout();
		long waitTime=Constants.DEFAULT_AT_TIMEOUT;
			if (waitTimeRsnRsaResult != null && waitTimeRsnRsaResult.matches("[0-9]+")) {
				waitTime = Long.parseLong(waitTimeRsnRsaResult);
				//getting timer
				Timer timer = source.getTimeoutTimer();
				if (timer == null) {
					if (logger.isInfoEnabled())
						logger.info("InapIsupSimServlet creating new timer");
					timer = new Timer();
					InapIsupSimServlet.getInstance().setTimeoutTimer(timer);
				}

				//getting ACtivityTest task
				RsnTimerTask timertaskRsn = new RsnTimerTask(timer);

				//scheduling task in failuure proof way
				try {
					timer.schedule(timertaskRsn, waitTime * 1000);
					//simCpb.setActivityTestTimerTask(timertaskAT);
				} catch (Exception e) {
					logger.error("Timer creation FAiled once...recreating::" + e.getMessage());
					timer = new Timer();
					source.setTimeoutTimer(timer);
					try {
						timer.schedule(timertaskRsn, waitTime * 1000);
						//simCpb.setActivityTestTimerTask(timertaskAT);
					} catch (Exception e1) {
						logger.error("Timer creation Failed again::" + e.getMessage());
						InapIsupSimServlet.getInstance().setTimeoutTimer(null);
					}
				}
			}
		}//eof if rsn_rsa
		
//		isRSN_RSA = ((isIncMode)&&!(isTestSuite()));
		
		SuiteLogger.getInstance().log("############# SIMULATOR STARTED AT "+new Date()+" #####################");
		//outside if else as for pureISUP flow actAsINC can be true so that 
		//unnecessary INVITE xchanges are not attempted by tcap app
		Thread initialThread=new Thread(new InitializeTask(this));
		initialThread.start();
	}

	public void initializeAndStartFlow() {
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet startFlow()-->start Initial Flow");

		if(fileNameIterator.hasNext()){
			currentFileName=fileNameIterator.next();
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet startInitialFlow()-->File present,Got File Name::"+currentFileName);
			Counters.getInstance().resetStats(false);
		}else{
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet startInitialFlow()-->no file present in list exit");
			Counters.getInstance().printStats();
			return;
		}

		nodeManager.parseCallFlow(currentFileName);
		if(isTestSuite){
			if(logger.isDebugEnabled())
				logger.debug("Waiting due to flow not initialized /parsed in testsuite");
			synchronized (fileNameIterator) {
				try {
					fileNameIterator.notifyAll();
				} catch (Throwable e) {
					logger.error("Throwable on messages waiting for flow in use", e);
				}
			}
		}

		//start flow if pure ISUP
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet startInitialFlow()-->Parse complete for initial flow, checking flow type");
		//getting start Node
		Node initailNode=nodeManager.getNextNode(null);
		
		if(initailNode == null  || !(initailNode instanceof StartNode)){
			logger.error("start node not present...invalid call flow xml. for file NAME::"+currentFileName);
			SuiteLogger.getInstance().log("FAILED-->FileName::["+currentFileName+"] Error in XML:Initial Node note found");
			return;
		}
		//field in InapIsupSimServlet to store protocol type as simulator now supports more than on protocol
		flowType=((StartNode)initailNode).getFlowType();
				
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet startInitialFlow()-->Got flow type for initial flow::"+flowType);
		synchronized (InitializeTask.class) {
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet startInitialFlow()-->Enter synch block on [InitializeTask.class]");
			if(isIncConnecetd){
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet startInitialFlow()-->INC connected, Flow type not checked; Flow started");
				startFlow();
			}else if(flowType!=null && flowType.equalsIgnoreCase(Constants.ISUP_FLOW_TYPE)){
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet init()-->INC not connected Pure ISUP flow;Flow started");
				startFlow();
			}else {
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet init()-->INC not connected and inap or win or hybrid flow;" +
					" Wait for Inc to connect");
				try {
					InitializeTask.class.wait();
				} catch (InterruptedException e) {
					logger.error("InterruptedException in wait",e);
				}
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet init()-->INC connected Recieved notify on wait;Flow started");
				startFlow();
			}//@End if INC connected
		}//@End synchronized
	}


	private void startFlow() {
		//start flow Timer
		//cant use timer service as its timer needs to be associated with app session
		timer= new Timer();
		TimerTask task= new CallStartTimerTask(simConfig, timer);
		//taking 10 second delay  on startup to allow other side to complete initialization if pending
		timer.scheduleAtFixedRate(task, 10000, Constants.TASK_FREQ);
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet startFlow()-->Timer scheduled");

	}

	///////////////////Jain tcap listener methods///////////////////////////////////////
	@Override
	public void addUserAddress(SccpUserAddress arg0)
	throws UserAddressLimitException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addUserAddress(TcapUserAddress arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public SccpUserAddress[] getUserAddressList()
	throws UserAddressEmptyException {
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet::getUserAddressList");
		int size =localAddr.size()+1;
		SccpUserAddress[] suas = new SccpUserAddress[size];
		localAddr.toArray(suas);
		suas[size-1] = remoteAddr ;
		return suas;
	}

	@Override
	public void processComponentIndEvent(ComponentIndEvent event) {
		if(logger.isInfoEnabled())
			logger.info("Enter InapIsupSimServlet processComponentIndEvent()");
		int dialogId = 0;
		//		SasCapMsgsToSend msgs = new SasCapMsgsToSend();
		SimCallProcessingBuffer buffer = null;
		try {	
			dialogId = event.getDialogueId();
			if(this.getFlowType().equalsIgnoreCase("win")){
				buffer = tcapCallData.get(Helper.getCallId());
				if((buffer!=null))
					buffer.setDialogId(dialogId);
				}
			else
				buffer = tcapCallData.get(dialogId);

			if(buffer == null){
				throw new Exception("Dilaogue Id:" + dialogId + ":: buffer is null");
			}
			//Calling helper for decoding the components and set the decoded params in the buffer.

			if(logger.isDebugEnabled())
				logger.debug("Dilaogue Id:" + dialogId + "::Calling helper processComponent");
			//will throw exception if anything wrong happens in decoding the components				

			Helper.processRcvdComponent(event, buffer);	

			if(logger.isDebugEnabled())
				logger.debug("Dilaogue Id:" + dialogId + "::Successfully update Buffer");	

		} catch (Exception e) {
			logger.error("Exception: " , e);
			Counters.getInstance().incrementFailedCalls();
			if(buffer != null){
				Helper.cleanUpResources(buffer,true);
			}
			SuiteLogger.getInstance().log("FAILED-->FileName::["+currentFileName+"] Exception in processComponentIndEvent()");
			if(isTestSuite()){
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet processComponentIndEvent()-->Test suite; attempt next flow");
				initializeAndStartFlow();
			}

		}			
		if(logger.isInfoEnabled())
			logger.info("Exit InapIsupSimServlet processComponentIndEvent()");

	}

	@Override
	public void processDialogueIndEvent(DialogueIndEvent event) {
		if(logger.isInfoEnabled())
			logger.info("Enter InapIsupSimServlet processDialogueIndEvent()");
		Integer dId = null;
		SimCallProcessingBuffer buffer = null;
		try {
			//get Dialog ID
			dId = event.getDialogueId();
			if(dId!=null)
			{
			if(logger.isDebugEnabled())
				logger.debug("getDialogueId:" + dId);
			//object of tcapCallData
			if(logger.isDebugEnabled())
				logger.debug("tcapCallData:" + tcapCallData);
			
			if(this.getFlowType().equalsIgnoreCase("win")){
				buffer = tcapCallData.get(Helper.getCallId());
				if((buffer!=null))
					buffer.setDialogId(dId);
				}
			else{
				//inap cpb
				buffer = tcapCallData.get(dId);
			}
			if(logger.isDebugEnabled())
				logger.debug("SimCallProcessBuffer:" + buffer);
			if(buffer == null){
				buffer = new SimCallProcessingBuffer();
				buffer.setDialogId(dId) ;
				buffer.setTcap(true);
				tcapCallData.put(dId, buffer);
				tcapCallData.put(Helper.getCallId(), buffer);
				if(logger.isDebugEnabled())
					logger.debug("New SampleAppCallProcessBuffer:" + buffer + "for dialogue id received from the event:"+ dId);
				if(isTestSuite && isFlowInitialized()){
					if(logger.isDebugEnabled())
						logger.debug("Waiting due to flow not initialized /parsed in testsuite");
					synchronized (fileNameIterator) {
						try {
							fileNameIterator.wait();
						} catch (InterruptedException e) {
							logger.error("InterruptedException on initial message wait on flow not inbitialized", e);
						}catch (Throwable e) {
							logger.error("Throwable on initial message wait on flow not initialized", e);
						}
					}
				}
			}
			}
			//testing code..not part of final release
//			TcapSession ts = tcapProvider.getTcapSession(dId);
//			Object appSession= ts.getAttribute("ApplicationSession");
//			if(appSession ==null){
//				if(logger.isDebugEnabled())
//					logger.debug("AppSession is null");
//			}else{
//				if(appSession instanceof SipApplicationSession){
//					if(logger.isDebugEnabled())
//						logger.debug("found appSession:::"+appSession);
//				}else{
//					if(logger.isDebugEnabled())
//						logger.debug("found Object:::"+appSession);
//				}
//			}
			if(dId==null)
				Helper.processRcvdDialogue(event,null);
			
			Helper.processRcvdDialogue(event,buffer);

		} catch (Exception e) {
			logger.error("Exception: " + e.getMessage(), e);
			Counters.getInstance().incrementFailedCalls();
			if(buffer != null){
				Helper.cleanUpResources(buffer, true);
			}
			SuiteLogger.getInstance().log("FAILED-->FileName::["+currentFileName+"] Exception in processDialogueIndEvent()");
			if(isTestSuite()){
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet processDialogueIndEvent()-->Test suite; attempt next flow");
				initializeAndStartFlow();
			}
		}
		if(logger.isInfoEnabled())
			logger.info("Exit InapIsupSimServlet processDialogueIndEvent()");

	}

	@Override
	public void processStateIndEvent(StateIndEvent sie){
		if(logger.isDebugEnabled())
			logger.debug("processStateIndEvent");
		if (sie instanceof NPCStateIndEvent) {
			if(logger.isDebugEnabled())
				logger.debug("processStateIndEvent NPCStateIndEvent");
			NPCStateIndEvent npcsie = (NPCStateIndEvent) sie;
			if(logger.isDebugEnabled())
				logger.debug("Received State information for "
						+ npcsie.getAffectedDpc().toString() + " for opc "
						+ npcsie.getOwnPointCode());

			if (npcsie.getSignalingPointStatus() != SccpConstants.DESTINATION_ACCESSIBLE) {
				if(logger.isDebugEnabled())
					logger.debug("DESTINATION_ACCESSIBLE");
			}
			if (npcsie.getSignalingPointStatus() != SccpConstants.DESTINATION_INACCESSIBLE) {
				if(logger.isDebugEnabled())
					logger.debug("DESTINATION_INACCESSIBLE");
			}
		} else if (sie instanceof NStateIndEvent) {
			NStateIndEvent nsie = (NStateIndEvent) sie;
			SccpUserAddress affectedUser = nsie.getAffectedUser();
			if(logger.isDebugEnabled())
//				logger.debug("Received NStateIndEvent State information for "
//						+ nsie.getAffectedUser().toString() + " for opc "
//						+ nsie.getOwnPointCode());
				
				logger.debug("Received NStateIndEvent State information for "
						+ nsie.getAffectedUser().toString() );
			if (nsie.getUserStatus() == SccpConstants.USER_OUT_OF_SERVICE) {
				if(logger.isDebugEnabled())
					logger.debug("USER_OUT_OF_SERVICE");
				for(SccpUserAddress addr :localAddr) {
					if (addr!= null && affectedUser.toString().equals(addr.toString())) {
						addr.setSuaStatus(SccpConstants.USER_OUT_OF_SERVICE);
						if(logger.isDebugEnabled())
							logger.debug("Local Address: " + addr + " Status: " + addr.getSuaStatus());

					}
				}
			}else if (nsie.getUserStatus() == SccpConstants.USER_IN_SERVICE) {
				if(logger.isDebugEnabled())
					logger.debug("USER_IN_SERVICE");
				for(SccpUserAddress addr :localAddr) {
					if (addr!= null && affectedUser.toString().equals(addr.toString())) {
						addr.setSuaStatus(SccpConstants.USER_IN_SERVICE);
						if(logger.isDebugEnabled())
							logger.debug("Local Address: " + addr + " Status: " + addr.getSuaStatus());
					}
				}

				synchronized (InitializeTask.class) {
					if(logger.isDebugEnabled())
						logger.debug("InapIsupSimServlet processStateIndEvent()-->" +
						"setting inc as connected and notify waiting threads");
					//set INC as connected
					isIncConnecetd=true;
					//check notify waiting threads
					InitializeTask.class.notifyAll();

				}//@end synchronized

			}else{
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet processStateIndEvent()-->Unknown user status::"+nsie.getUserStatus());
			}
		}//@End instance of stetIndevent
	}

	@Override
	public void processTcapError(TcapErrorEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet::processTcapError:" + event.getError());

	}

	@Override
	public void processTimeOutEvent(TimeOutEvent event) {
		if(logger.isDebugEnabled())
			logger.debug("InapIsupSimServlet::TimeOutEvent: " + Util.toString(event.getDialogueId())+
					"  Timer type::"+event.getTimerType());

		try {
			if( event.getTimerType() == 1  &&  isActivityTestSupported  ){
				Integer dlgId = event.getDialogueId();
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet::AT timer expiry: " + Util.toString(event.getDialogueId()));
				SimCallProcessingBuffer buffer = tcapCallData.get(dlgId);
				logger.debug("processTimeOutEvent:buffer"+ buffer);
				Helper.performActivityTest(this, buffer);
			}
			}catch(Exception e){
				logger.error("Exception occured in sending :" , e);
			}
//			if(isRSN_RSA)
//			{
//				try {
////						if(logger.isDebugEnabled())
////						logger.debug(Util.toString(simCpb.getDialogId()) + ":::  starting RsnTimer");
//					//start RsnTimer
//
//					String waitTimeRsnRsaResult = source.getConfigData().getRsnRsaTimeout();
//					long waitTime=Constants.DEFAULT_AT_TIMEOUT;
//					if(waitTimeRsnRsaResult != null){
//						waitTime = Long.parseLong(waitTimeRsnRsaResult);
//					}
//
//					//getting timer
//					Timer timer = source.getTimeoutTimer();
//					if(timer ==null ){
//						if(logger.isInfoEnabled())
//							logger.info("InapIsupSimServlet creating new timer");
//						timer = new Timer();
//						InapIsupSimServlet.getInstance().setTimeoutTimer(timer);
//					}
//
//					//getting ACtivityTest task
//					RsnTimerTask timertaskRsn = new RsnTimerTask(timer);
//
//					//scheduling task in failuure proof way
//					try{
//						timer.schedule(timertaskRsn ,waitTime*1000);
//						//simCpb.setActivityTestTimerTask(timertaskAT);
//					}catch(Exception e){
//						logger.error("Timer creation FAiled once...recreating::"+e.getMessage());
//						timer = new Timer();
//						source.setTimeoutTimer(timer);
//						try{
//							timer.schedule(timertaskRsn, waitTime*1000);
//							//simCpb.setActivityTestTimerTask(timertaskAT);
//						}catch (Exception e1) {
//							logger.error("Timer creation FAiled again::"+e.getMessage());
//							InapIsupSimServlet.getInstance().setTimeoutTimer(null);
//						}
//					}
//
//				} catch (ParameterNotSetException e) {
//					logger.error(Util.toString(simCpb.getDialogId()) + "::: ParameterNotSetException sending Rsn/Rsa component/continue dialog",e);
//				} catch (IOException e) {
//					logger.error(Util.toString(simCpb.getDialogId()) + "::: IOException excpetion sending Rsn/Rsa component/continue dialog",e);
//				}
//			}//eof if rsn

	}

	@Override
	public void removeUserAddress(SccpUserAddress arg0)
	throws InvalidAddressException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeUserAddress(TcapUserAddress arg0) {
		// TODO Auto-generated method stub

	}

/*	@Override
	public List<SccpUserAddress> getSUAList() {
		return localAddr;
	}
*/
	@Override
	public String getInviteSessionId() {
		return inviteSessionId;

	}
	
	@Override
	public String getName() {
		return Constants.APP_NAME;
	}

	@Override
	public String getVersion() {
		return Constants.APP_VERSION;
	}

	/////////////////////////////////////SIP messages///////////////////////////////////////

	/**
	 * this method handles sip invite messages
	 * logic:
	 * 1. Check if Invite to connect INC and sends 200 ok with SUA list for success case
	 * 
	 */
	public void doInvite(SipServletRequest request){
		if(logger.isInfoEnabled())
			logger.info("Inside InapIsupSimServlet doInvite()");
		//check if inc mode active
		URI reqUri=null;
		SipURI sipReqUri=null;
		String sipReqUser=null;
		reqUri=request.getRequestURI();
		if(reqUri!=null  && reqUri.isSipURI()){
			sipReqUri=(SipURI) reqUri;
			sipReqUser=sipReqUri.getUser().toLowerCase();
		}

		if(isIncMode && ( sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER1.toLowerCase()) ||	
				sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER2.toLowerCase()) ) ){
			//message to connect INC
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet doInvite()-->Invite to connect INC");
			inviteSessionId= request.getFrom().getURI().getParameter(IN_GW_SESSION_ID);
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet doInvite()-->Got invite Session ID::"+inviteSessionId);
			//storing heartbeat session
			heartbeatAppSesion = request.getApplicationSession();
			SipServletResponse inviteResponse = request.createResponse(SipServletResponse.SC_OK);
			//			String inviteRespCont=simConfig.getInviteRespTcapMessage();
			//			if(logger.isDebugEnabled())
			//				logger.debug("creating byteArray for::["+inviteRespCont+"]");
			//			byte[] responseContent= Util.hexStringToByteArray(inviteRespCont);

			byte[] responseContent = null;
			try {
				if(!simConfig.isSendSccpConfigWithInfo()){
					if(logger.isDebugEnabled())
						logger.debug("InapIsupSimServlet doInvite()-->sending 200Ok for invite with sccpconfig msg");
					responseContent= Helper.getInviteByteArray(getLocalAddrs().get(0));
					if(logger.isDebugEnabled())
						logger.debug("created SCCP msg byteArray for INvite response::["+Util.formatBytes(responseContent)+"]");
					inviteResponse.setContent(responseContent, Constants.TCAP_CONTENT_TYPE);
				}//@End:if send sccp msg with info
				inviteResponse.send();
			} catch (UnsupportedEncodingException e) {
				logger.error("InapIsupSimServlet doInvite()-->Exception setting content in invite response " +
						"contentType:::[" + Constants.TCAP_CONTENT_TYPE +
						"]  content:::["+Util.formatBytes(responseContent) + "]",e );
			} catch (IOException e) {
				logger.error("InapIsupSimServlet doInvite()-->Exception sending invite response",e );
			}
		}
		
		
		
	else if (isB2bMode) {
		if(logger.isDebugEnabled())
			logger.debug("Normal SIPT INVITE received to be send forward");
		
		if(logger.isDebugEnabled())
			logger.debug("Handling INVITE in b2b mode");
		String callId= request.getCallId();

		//check if correlation
		SimCallProcessingBuffer simCpb = sipCallData.get(callId);
		if(simCpb ==null){

				if(logger.isDebugEnabled())
					logger.debug("Normal SIPT INVITE received not a correlated calls new cpb created");
				simCpb = new SimCallProcessingBuffer();
//				if(isTestSuite && isFlowInitialized()){
//					if(logger.isDebugEnabled())
//						logger.debug("Waiting due to flow not initialized /parsed in testsuite");
//					synchronized (fileNameIterator) {
//						try {
//							fileNameIterator.wait();
//						} catch (InterruptedException e) {
//							logger.error("InterruptedException on initial message wait on flow not inbitialized", e);
//						}catch (Throwable e) {
//							logger.error("Throwable on initial message wait on flow not initialized", e);
//						}
//					}
//				}
		
			simCpb.setCallId(callId);
			
			simCpb.setSipAppSession(request.getApplicationSession());
			//simCpb.setOrigInviteRequestLeg1(request);	
			sipCallData.put(callId, simCpb);
			if(logger.isDebugEnabled())
				logger.debug("New SampleAppCallProcessBuffer:" + simCpb + "for callid id received from the request:"+ callId);

		}//@END creating new CPB
		Helper.callMessageHandler(request, simCpb);		
	} else{
			if(logger.isDebugEnabled())
				logger.debug("Normal SIPT INVITE received");
			String callId= request.getCallId();

			//check if correlation
			SimCallProcessingBuffer simCpb = sipCallData.get(callId);
			if(simCpb ==null){
				//check if correlated call
				if(logger.isDebugEnabled())
					logger.debug("Normal SIPT INVITE received checking coirreelation with tcap as callid->cpb not found");
				TcapSession tcapSession = (TcapSession) request.getApplicationSession().getAttribute(Constants.TCAP_SESSION_ATTRIBUTE);
				if(tcapSession!=null){
					int dlgId = tcapSession.getDialogueId();
					simCpb = tcapCallData.get(dlgId);
				}
				//still null
				if(simCpb == null){
					if(logger.isDebugEnabled())
						logger.debug("Normal SIPT INVITE received not a correlated calls new cpb created");
					simCpb = new SimCallProcessingBuffer();
					if(isTestSuite && isFlowInitialized()){
						if(logger.isDebugEnabled())
							logger.debug("Waiting due to flow not initialized /parsed in testsuite");
						synchronized (fileNameIterator) {
							try {
								fileNameIterator.wait();
							} catch (InterruptedException e) {
								logger.error("InterruptedException on initial message wait on flow not inbitialized", e);
							}catch (Throwable e) {
								logger.error("Throwable on initial message wait on flow not initialized", e);
							}
						}
					}
				}
				simCpb.setCallId(callId);
				sipCallData.put(callId, simCpb);
				if(logger.isDebugEnabled())
					logger.debug("New SampleAppCallProcessBuffer:" + simCpb + "for callid id received from the request:"+ callId);

			}//@END creating new CPB
			Helper.callMessageHandler(request, simCpb);

		}//@END normal SIPT call

	}

	public void doOptions(SipServletRequest request){
		if(logger.isInfoEnabled())
			logger.info("Inside InapIsupSimServlet doOptions()");

		//check if inc mode active
		URI reqUri=null;
		SipURI sipReqUri=null;
		String sipReqUser=null;
		reqUri=request.getRequestURI();
		if(reqUri!=null  && reqUri.isSipURI()){
			sipReqUri=(SipURI) reqUri;
			sipReqUser=sipReqUri.getUser().toLowerCase();
		}

		if(isIncMode && ( sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER1.toLowerCase()) ||	
				sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER2.toLowerCase()) ) ){
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet doOptions()-->Heartbeat Option to INC");
			SipServletResponse optionResponse = request.createResponse(SipServletResponse.SC_OK);
			try {
				optionResponse.send();
			} catch (IOException e) {
				logger.error("InapIsupSimServlet doOptions()-->Exception sending heartbeat response",e );
			}
		}else{
			//don othing as handlers not supported for options
		}


	}

	public void doInfo(SipServletRequest request){
		if(logger.isInfoEnabled())
			logger.info("Inside InapIsupSimServlet doInfo()");

		//check if inc mode active
		URI reqUri=null;
		SipURI sipReqUri=null;
		String sipReqUser=null;
		reqUri=request.getRequestURI();
		if(reqUri!=null  && reqUri.isSipURI()){
			sipReqUri=(SipURI) reqUri;
			sipReqUser=sipReqUri.getUser().toLowerCase();
		}

		if(isIncMode && ( sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER1.toLowerCase()) ||	
				sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER2.toLowerCase()) ) ){
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet doInfo()-->INFO with SUA list to INC");

			SipServletResponse infoResponse = request.createResponse(SipServletResponse.SC_OK);
			byte[] requestContent=null;
			try {
				infoResponse.send();
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet doInfo()-->Info response succesfully sent");
				SipServletRequest infoRequest = request.getSession().createRequest(Constants.INFO_MESSAGE);
				//				String infoReqCont=simConfig.getInfoTcapMessage();
				//				if(logger.isDebugEnabled())
				//					logger.debug("creating byteArray for::["+infoReqCont+"]");
				//				byte[] requestContent= Util.hexStringToByteArray(infoReqCont);

				requestContent = Helper.getInfoByteArray(getRemoteAddr(),getLocalAddrs().get(0),true);
				
//				requestContent = Helper.hexStringToByteArray("341d0b3301050b0601030108000000790792040b06010301080000007907920d0a");
				
				if(logger.isDebugEnabled())
					logger.debug("created SCCP msg byteArray for INfo request::["+Util.formatBytes(requestContent)+"]");
				infoRequest.setContent(requestContent, Constants.TCAP_CONTENT_TYPE);
				infoRequest.send();

				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet doInfo()-->Info request succesfully sent");

			} catch (UnsupportedEncodingException e) {
				logger.error("InapIsupSimServlet doInfo()-->Exception setting content in Info Message " +
						"contentType:::[" + Constants.TCAP_CONTENT_TYPE +
						"]  content:::["+Util.formatBytes(requestContent) + "]",e );
			} catch (IOException e) {
				logger.error("InapIsupSimServlet doInfo()-->Exception sending Info response or Inforequest",e );
			}
		}else if(isB2bMode){
			if(logger.isDebugEnabled())		
				logger.debug("Handling Info in b2b mode");
			SipApplicationSession appSession = request.getApplicationSession();
			SimCallProcessingBuffer simCpb = appSessionIdCallData.get(appSession.getId());
			if(simCpb==null)
			{
				logger.error("For Info message in B2BMode of appsession"+appSession+",call processing buffer is not foound");
				return;
			}
			
			Helper.callMessageHandler(request, simCpb);
		}else{
			if(logger.isDebugEnabled())
				logger.debug("Normal SIP/SIPT Info received");
			String callId= request.getCallId();
			SimCallProcessingBuffer simCpb = sipCallData.get(callId);
			if(simCpb ==null){
				logger.error("For non initail message doInfo on CallID:" +callId + "  CallProcessing buffer not found");
				return;
			}//@END creating new CPB
			Helper.callMessageHandler(request, simCpb);

		}
	}

	public void doAck(SipServletRequest request){
		if(logger.isInfoEnabled())
			logger.info("Inside InapIsupSimServlet doAck()");

		//check if inc mode active
		URI reqUri=null;
		SipURI sipReqUri=null;
		String sipReqUser=null;
		reqUri=request.getRequestURI();
		if(reqUri!=null  && reqUri.isSipURI()){
			sipReqUri=(SipURI) reqUri;
			sipReqUser=sipReqUri.getUser().toLowerCase();
		}

		if(isIncMode && ( sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER1.toLowerCase()) ||	
				sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER2.toLowerCase()) ) ){
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet doAck()-->Ack for Invite to INC");
			
			if(simConfig.isSendSccpConfigWithInfo()){
				String delay = simConfig.getDelaySccpConfigWithInfo();
				if( delay!=null){
					if(logger.isDebugEnabled())
						logger.debug("InapIsupSimServlet doAck()-->Dealy present value::"+delay);
					//adding delay
					long timeout = ( (Long.parseLong(delay)) * 1000L );
					try {
						Thread.sleep(timeout);
					} catch (InterruptedException e) {
						if(logger.isDebugEnabled())
							logger.debug("Error adding delay to info",e);
					}//@end try catch block
					
				}//@end delay check
				
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet doAck()-->sending info with sccpconfig msg");
				SipServletRequest infoRequest = request.getSession().createRequest(Constants.INFO_MESSAGE);
				
				byte[] requestContent=  null;
				
				requestContent =Helper.getInviteByteArray(getLocalAddrs().get(0));
				
				if(logger.isDebugEnabled())
					logger.debug("created SCCP config msg byteArray for INfo request::["+Util.formatBytes(requestContent)+"]");
				try {
					infoRequest.setContent(requestContent, Constants.TCAP_CONTENT_TYPE);
					infoRequest.send();
				} catch (UnsupportedEncodingException e) {
					logger.error("InapIsupSimServlet doAck()-->Exception setting content in Info Message " +
							"contentType:::[" + Constants.TCAP_CONTENT_TYPE +
							"]  content:::["+Util.formatBytes(requestContent) + "]",e );
				} catch (IOException e) {
					logger.error("InapIsupSimServlet doAck()-->Exception sending sccpconfig msg in  Inforequest",e );
				}

				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet doAck()-->Info request succesfully sent");
				
			}//@End:if send sccp msg with info
			
			
		}else if(isB2bMode){
			if(logger.isDebugEnabled())		
				logger.debug("Handling Ack in b2b mode");
			SipApplicationSession appSession = request.getApplicationSession();
			logger.debug("getting call buffer data from id = "+appSession.getId() );
			SimCallProcessingBuffer simCpb = appSessionIdCallData.get(appSession.getId());
			if(simCpb==null)
			{
				logger.error("For Ack message(B2BMode) of appsession"+appSession+",call processing buffer is not foound");
				return;
			}
			
			Helper.callMessageHandler(request, simCpb);			
			
		}else{
			if(logger.isDebugEnabled())
				logger.debug("Normal SIPT ACK received");
			String callId= request.getCallId();
			SimCallProcessingBuffer simCpb = sipCallData.get(callId);
			if(simCpb ==null){
				logger.error("For non initail message ACK on CallID:" +callId + "  CallProcessing buffer not found");
				return;
			}//@END creating new CPB
			Helper.callMessageHandler(request, simCpb);

		}//@END normal SIPT call


	}

	public void doSuccessResponse(SipServletResponse response) {
		if(logger.isInfoEnabled())
			logger.info("Inside InapIsupSimServlet doSuccessResponse()");
		//check if inc mode active
		URI reqUri=null;
		SipURI sipReqUri=null;
		String sipReqUser=null;
		reqUri=response.getRequest().getRequestURI();
		if(reqUri!=null  && reqUri.isSipURI()){
			sipReqUri=(SipURI) reqUri;
			sipReqUser=sipReqUri.getUser().toLowerCase();
		}
		if (response.getMethod().equalsIgnoreCase("BYE")){
			response.getSession().setInvalidateWhenReady(true);
		}
		if(isIncMode && response.getMethod().equalsIgnoreCase(Constants.INFO_MESSAGE) && 
				( sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER1.toLowerCase()) ||	
						sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER2.toLowerCase()) ) ){
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet doSuccessResponse()-->Succesreponse for INFO to INC  status [::"+response.getStatus()+"]");
			//setting isIncConnected true
			synchronized (InitializeTask.class) {
				if(logger.isDebugEnabled())
					logger.debug("InapIsupSimServlet doSuccessResponse()-->" +
					"setting inc as connected and notify waiting threads");
				//set INC as connected
				isIncConnecetd=true;
				//check notify waiting threads
				InitializeTask.class.notifyAll();

			}//@end synchronized
		}else if(isB2bMode){
			if(logger.isDebugEnabled())
				logger.debug("Handling 2XX in b2b mode");
			SipApplicationSession appSession = response.getApplicationSession();
			SimCallProcessingBuffer simCpb = appSessionIdCallData.get(appSession.getId());
			if(simCpb==null)
			{
				logger.error("For success response(2xx in B2BMode) message of INVITE of appsession"+appSession+",call processing buffer is not foound");
				return;
			}
			
			Helper.callMessageHandler(response, simCpb);
		}else{
		
			if(logger.isDebugEnabled())
				logger.debug("Normal SIPT succes resp received  status [::"+response.getStatus()+"]");
			//check if unhandled method message
			String method =response.getMethod();
			if(method.equals("OPTIONS") || method.equals("CANCEL") || method.equals("NOTIFY") ){
				if(logger.isDebugEnabled())
					logger.debug("Not invoking handler::success response for method::"+method);
				return;
			}
			//begin processing
			String callId= response.getCallId();
			SimCallProcessingBuffer simCpb = sipCallData.get(callId);
			if(simCpb ==null){
				logger.error("For non initail message SuccessResponse on CallID:" +callId + "  CallProcessing buffer not found");
				return;
			}//@END creating new CPB

			Helper.callMessageHandler(response, simCpb);

		}//@END normal SIPT call

	}

	public void doProvisionalResponse(SipServletResponse response) {
		if(logger.isInfoEnabled())
			logger.info("Inside InapIsupSimServlet doProvisionalResponse()");
		//check if inc mode active
		URI reqUri=null;
		SipURI sipReqUri=null;
		String sipReqUser=null;
		reqUri=response.getRequest().getRequestURI();
		if(reqUri!=null  && reqUri.isSipURI()){
			sipReqUri=(SipURI) reqUri;
			sipReqUser=sipReqUri.getUser().toLowerCase();
		}

		if(isIncMode && ( sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER1.toLowerCase()) ||	
				sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER2.toLowerCase()) ) ){
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet doProvisionalResponse()-->prov resp for Invite to INC  status[::"+response.getStatus()+"]");
		}else if(isB2bMode){
		
			if(logger.isDebugEnabled())
				logger.debug("Handling 1XX in b2b mode");
			SipApplicationSession appSession = response.getApplicationSession();
			SimCallProcessingBuffer simCpb = appSessionIdCallData.get(appSession.getId());
			if(simCpb==null)
			{
				logger.error("For provisional response(1xx in B2BMode) message of INVITE of appsession"+appSession+",call processing buffer is not foound");
				return;
			}
			
			Helper.callMessageHandler(response, simCpb);
		}else{
			if(logger.isDebugEnabled())
				logger.debug("Normal SIPT prov resp received status[::"+response.getStatus()+"]");
			String callId= response.getCallId();
			SimCallProcessingBuffer simCpb = sipCallData.get(callId);
			if(simCpb ==null){
				logger.error("For non initail message SuccessResponse on CallID:" +callId + "  CallProcessing buffer not found");
				return;
			}//@END creating new CPB
			Helper.callMessageHandler(response, simCpb);

		}//@END normal SIPT call


	}
	
	public void doRedirectResponse(SipServletResponse response) {
		if(logger.isInfoEnabled())
			logger.info("Inside InapIsupSimServlet doRedirectResponse()");
		//check if inc mode active
		URI reqUri=null;
		SipURI sipReqUri=null;
		String sipReqUser=null;
		reqUri=response.getRequest().getRequestURI();
		if(reqUri!=null  && reqUri.isSipURI()){
			sipReqUri=(SipURI) reqUri;
			sipReqUser=sipReqUri.getUser().toLowerCase();
		}

		if(isIncMode && ( sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER1.toLowerCase()) ||	
				sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER2.toLowerCase()) ) ){
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet doRedirectResponse()-->prov resp for Invite to INC  status[::"+response.getStatus()+"]");
		}else if(isB2bMode){
			if(logger.isDebugEnabled())
				logger.debug("Handling 3XX in b2b mode");
			SipApplicationSession appSession = response.getApplicationSession();
			SimCallProcessingBuffer simCpb = appSessionIdCallData.get(appSession.getId());
			if(simCpb==null)
			{
				logger.error("For redirect response(3xx in B2BMode) message of INVITE of appsession"+appSession+",call processing buffer is not foound");
				return;
			}
			
			Helper.callMessageHandler(response, simCpb);
		}else{
			if(logger.isDebugEnabled())
				logger.debug("Normal SIPT prov resp received status[::"+response.getStatus()+"]");
			String callId= response.getCallId();
			SimCallProcessingBuffer simCpb = sipCallData.get(callId);
			if(simCpb ==null){
				logger.error("For non initail message redirectResponse on CallID:" +callId + "  CallProcessing buffer not found");
				return;
			}//@END creating new CPB
			Helper.callMessageHandler(response, simCpb);

		}//@END normal SIPT call


	}

	public void doBye(SipServletRequest request){
		if(logger.isInfoEnabled())
			logger.info("Inside InapIsupSimServlet doBye()");

		//check if inc mode active
		URI reqUri=null;
		SipURI sipReqUri=null;
		String sipReqUser=null;
		reqUri=request.getRequestURI();
		if(reqUri!=null  && reqUri.isSipURI()){
			sipReqUri=(SipURI) reqUri;
			sipReqUser=sipReqUri.getUser().toLowerCase();
		}

		if(isIncMode && ( sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER1.toLowerCase()) ||	
				sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER2.toLowerCase()) ) ){
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet doBye()-->Bye to INC unknown behavior");
		}else if(isB2bMode){
			if(logger.isDebugEnabled())
				logger.debug("Handling Bye in b2b mode");
			SipApplicationSession appSession = request.getApplicationSession();
			SimCallProcessingBuffer simCpb = appSessionIdCallData.get(appSession.getId());
			if(simCpb==null)
			{
				logger.error("For Bye message(B2BMode) of appsession"+appSession+",call processing buffer is not foound");
				return;
			}
			
			Helper.callMessageHandler(request, simCpb);
			Helper.cleanUpResources(simCpb,true);
		}else{
			if(logger.isDebugEnabled())
				logger.debug("Normal SIPT Bye received");
			String callId= request.getCallId();
			SimCallProcessingBuffer simCpb = sipCallData.get(callId);
			if(simCpb ==null){
				logger.error("For non initail message Bye on CallID:" +callId + "  CallProcessing buffer not found");
				return;
			}//@END creating new CPB
			Helper.callMessageHandler(request, simCpb);
			Helper.cleanUpResources(simCpb,true);

		}//@END normal SIPT call


	}
	
	public void doCancel(SipServletRequest request){
		if(logger.isInfoEnabled())
			logger.info("Inside InapIsupSimServlet doCancel()");

		//check if inc mode active
		URI reqUri=null;
		SipURI sipReqUri=null;
		String sipReqUser=null;
		reqUri=request.getRequestURI();
		if(reqUri!=null  && reqUri.isSipURI()){
			sipReqUri=(SipURI) reqUri;
			sipReqUser=sipReqUri.getUser().toLowerCase();
		}

		if(isIncMode && ( sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER1.toLowerCase()) ||	
				sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER2.toLowerCase()) ) ){
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet doBye()-->Bye to INC unknown behavior");
		}else if(isB2bMode){
		
			if(logger.isDebugEnabled())
				logger.debug("Handling Cancel in b2b mode");
			SipApplicationSession appSession = request.getApplicationSession();
			SimCallProcessingBuffer simCpb = appSessionIdCallData.get(appSession.getId());
			if(simCpb==null)
			{
				logger.error("For Cancel message(B2BMode) of appsession"+appSession+",call processing buffer is not foound");
				return;
			}
			
			Helper.callMessageHandler(request, simCpb);
			Helper.cleanUpResources(simCpb,true);
		}else{
			if(logger.isDebugEnabled())
				logger.debug("Normal SIPT Cancel received");
			String callId= request.getCallId();
			SimCallProcessingBuffer simCpb = sipCallData.get(callId);
			if(simCpb ==null){
				logger.error("For non initail message Cancel on CallID:" +callId + "  CallProcessing buffer not found");
				return;
			}//@END creating new CPB
			Helper.callMessageHandler(request, simCpb);
			Helper.cleanUpResources(simCpb,true);

		}//@END normal SIPT call


	}

	public void doPrack(SipServletRequest request){
		if(logger.isInfoEnabled())
			logger.info("Inside InapIsupSimServlet doPrack()");

		//check if inc mode active
		URI reqUri=null;
		SipURI sipReqUri=null;
		String sipReqUser=null;
		reqUri=request.getRequestURI();
		if(reqUri!=null  && reqUri.isSipURI()){
			sipReqUri=(SipURI) reqUri;
			sipReqUser=sipReqUri.getUser().toLowerCase();
		}

		if(isIncMode && ( sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER1.toLowerCase()) ||	
				sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER2.toLowerCase()) ) ){
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet doPrack()-->PRACK for INC unknown Behavior");
		}else if(isB2bMode){
		
			if(logger.isDebugEnabled())
				logger.debug("Handling Prack in b2b mode");
			SipApplicationSession appSession = request.getApplicationSession();
			SimCallProcessingBuffer simCpb = appSessionIdCallData.get(appSession.getId());
			if(simCpb==null)
			{
				logger.error("For prack message(B2BMode) of appsession"+appSession+",call processing buffer is not foound");
				return;
			}
			
			Helper.callMessageHandler(request, simCpb);
		}else{
			if(logger.isDebugEnabled())
				logger.debug("Normal SIPT PRACK received");
			String callId= request.getCallId();
			SimCallProcessingBuffer simCpb = sipCallData.get(callId);
			if(simCpb ==null){
				logger.error("For non initail message PRACK on CallID:" +callId + "  CallProcessing buffer not found");
				return;
			}//@END creating new CPB
			Helper.callMessageHandler(request, simCpb);

		}//@END normal SIPT call


	}
	
	public void doUpdate(SipServletRequest request){
		if(logger.isInfoEnabled())
			logger.info("Inside InapIsupSimServlet doUpdate()");

		//check if inc mode active
		URI reqUri=null;
		SipURI sipReqUri=null;
		String sipReqUser=null;
		reqUri=request.getRequestURI();
		if(reqUri!=null  && reqUri.isSipURI()){
			sipReqUri=(SipURI) reqUri;
			sipReqUser=sipReqUri.getUser().toLowerCase();
		}

		if(isIncMode && ( sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER1.toLowerCase()) ||	
				sipReqUser.startsWith(Constants.INC_MESSAGE_IDENTIFIER2.toLowerCase()) ) ){
			if(logger.isDebugEnabled())
				logger.debug("InapIsupSimServlet doUpdate()-->Update for INC unknown Behavior");
		}else if(isB2bMode){
		
			if(logger.isDebugEnabled())
				logger.debug("Handling Update in b2b mode");
			SipApplicationSession appSession = request.getApplicationSession();
			SimCallProcessingBuffer simCpb = appSessionIdCallData.get(appSession.getId());
			if(simCpb==null)
			{
				logger.error("For update message(B2BMode) of appsession"+appSession+",call processing buffer is not foound");
				return;
			}
			
			Helper.callMessageHandler(request, simCpb);
		}else{
			if(logger.isDebugEnabled())
				logger.debug("Normal SIP/SIPT Update received");
			String callId= request.getCallId();
			SimCallProcessingBuffer simCpb = sipCallData.get(callId);
			if(simCpb ==null){
				logger.error("For non initail message doUpdate on CallID:" +callId + "  CallProcessing buffer not found");
				return;
			}//@END creating new CPB
			Helper.callMessageHandler(request, simCpb);

		}//@END normal SIPT call
	}
	
	public SccpUserAddress[] processRSNUniDirIndEvent(TcapSession tcapSession, DialogueIndEvent event){
		return null;	
	}


	/////////////////////////////Setters and getters/////////////////////////////////////////
	/**
	 * @return the tcapProvider
	 */
	public TcapProvider getTcapProvider() {
		return tcapProvider;
	}

	/**
	 * @return the factory
	 */
	public SipFactory getFactory() {
		return factory;
	}

	/**
	 * @return the nodeManager
	 */
	public NodeManager getNodeManager() {
		return nodeManager;
	}

	/**
	 * @return the tcapCallData
	 */
	public Map<Integer, SimCallProcessingBuffer> getTcapCallData() {
		return tcapCallData;
	}

	/**
	 * @return the sipCallData
	 */
	public Map<String, SimCallProcessingBuffer> getSipCallData() {
		return sipCallData;
	}

	/**
	 * @return the sipCallData
	 */
	public Map<String, SimCallProcessingBuffer> getAppSessionIdCallData() {
		return appSessionIdCallData;
	}

	/**
	 * @return the remoteAddr
	 */
	public SccpUserAddress getRemoteAddr() {
		return remoteAddr;
	}

	/**
	 * @return the localAddr
	 */
	public List<SccpUserAddress> getLocalAddrs() {
		return localAddr;
	}

	/**
	 * 
	 * @return configuration
	 */
	public SimulatorConfig getConfigData(){
		return simConfig;
	}

	/**
	 * @param timeoutTimer the timeoutTimer to set
	 */
	public void setTimeoutTimer(Timer timeoutTimer) {
		this.timeoutTimer = timeoutTimer;
	}

	/**
	 * @return the timeoutTimer
	 */
	public Timer getTimeoutTimer() {
		return timeoutTimer;
	}

	/**
	 * @return the timeoutTimer
	 */
	public String getCurrentFileName() {
		return currentFileName;
	}

	/**
	 * @return the isTestSuite
	 */
	public boolean isTestSuite() {
		return isTestSuite;
	}

	public static InapIsupSimServlet getInstance() {
		return instance;
	}

	/**
	 * @return the isActivityTestSupported
	 */
	public boolean isActivityTestSupported() {
		return isActivityTestSupported;
	}

	/**
	 * @param flowInitialized the flowInitialized to set
	 */
	public void setFlowInitialized(boolean flowInitialized) {
		this.flowInitialized = flowInitialized;
	}

	/**
	 * @return the flowInitialized
	 */
	public boolean isFlowInitialized() {
		return flowInitialized;
	}

	@Override
	public void processTcapSessionActivationEvent(TcapSession arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return the flowType
	 */
	public String getFlowType() {
		return flowType;
	}

	/**
	 * @param flowType the flowType to set
	 */
	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}
	
		@Override
		public String  getDisplayName(){
			return null;
		}

}
