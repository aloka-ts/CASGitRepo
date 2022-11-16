
/*
 * Created on Sep 8, 2004
 *
 */
package com.baypackets.ase.measurement;

import com.agnity.oems.agent.messagebus.OEMSServiceStarter;
import com.agnity.oems.agent.messagebus.enumeration.ComponentType;
import com.agnity.oems.agent.messagebus.meascounters.MeasMgr;
import com.agnity.oems.agent.messagebus.meascounters.MeasurementListener;
import com.agnity.oems.agent.messagebus.meascounters.OemsAgent;
import com.agnity.oems.agent.messagebus.meascounters.threshold.ThresholdMgr;
import com.agnity.oems.agent.messagebus.utils.OemsUtils;
import com.baypackets.bayprocessor.slee.meascounters.SleeMeasurementCounter;
import com.baypackets.ase.common.AseBackgroundProcessor;
import com.baypackets.ase.common.BackgroundProcessListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AppClassLoader;
import com.baypackets.ase.container.AseEngine;
import com.baypackets.ase.soa.SoaMeasurementUtil;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseThreadMonitor;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.*;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.emsagent.EmsAgent;
import com.baypackets.emsliteagent.EmsLiteAgent;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

public class AseMeasurementManager implements CommandHandler, MComponent, BackgroundProcessListener  {
	
	private static Logger logger = Logger.getLogger(AseMeasurementManager.class);

	private static AseMeasurementManager mgr = null;
	public static AseMeasurementManager instance(){
		if(mgr == null){
			mgr =  (AseMeasurementManager) Registry.lookup(Constants.NAME_MEASUREMENT_MGR);
		}
		return mgr;
	}
	private static final String MEASUREMENT_CONFIG = "com/baypackets/ase/measurement/measurement-config.xml";
	private static final String THRESHOLD_CONFIG = "com/baypackets/ase/measurement/threshold-config.xml";
	private static final String NSEP_MEASUREMENT_CONFIG = "com/baypackets/ase/measurement/nsep-measurement-config.xml";
	private static final String SOA_MEASUREMENT_CONFIG = "com/baypackets/ase/soa/soa-measurement-config.xml";

	public static final String DEFAULT_SERVICE_NAME = "CAS";
	public static final String DEFAULT_NSEP_SERVICE_NAME = "NSEP";
	public static final String DEFAULT_SOA_SERVICE_NAME = "SOA";    
	public static final int DEFAULT_SERVICE_ID = 0;
	
	public static final int TYPE_MEASUREMENT_COUNTER = 1;
	public static final int TYPE_THRESHOLD_COUNTER = 2;

	//Application specific measurement counter changes
	private ArrayList<ApplicationList> serviceNames =  new ArrayList<ApplicationList>();
	public static int STATUS_ACTIVE = 1;
	public static int STATUS_INACTIVE = 0;
	public static boolean counterEnableFlag = false;

	public static String msetAndThresXmlResposneFromEMS = null;

	public static String transectionIdOfGetConfigRequest = null;
	
	public static String componentIdOfGetConfigResponse= null;

	private ArrayList<CounterSet> counterSets =  new ArrayList<CounterSet>();

	//This Map contains KV pairs as EntityName -> {CounterName -> CounterObject} which will be passed to MeasMgr for processing
	private Map<String, HashMap<String, SleeMeasurementCounter>> sleeMeasurementCounterMap = new HashMap<String, HashMap<String, SleeMeasurementCounter>>();

	private long lastClearTime;
	private ServiceMeasurementManager defaultMeasurementManager = new ServiceMeasurementManager(DEFAULT_SERVICE_NAME);
	private ServiceMeasurementManager NSEPMeasurementManager ;
	private ServiceMeasurementManager SoaMeasurementManager ;   

	private ThresholdMgr mfwThresholdMgr = null;
	private MeasMgr measMgr = null;
	private AseMeasurementLogger fileLogger = null;
	private boolean writeToFile = false;
	private AseUsageService usageService = null;
	private int dumpCount = 0;
	private int headerDumpFreq;

	//For Application Specific Measurement Counters
	private int minDefaultAccIntervalForSets;
	private ConfigRepository m_configRepository;

	private CounterSet getCounterSet(String serviceName, int type){
		CounterSet temp = new CounterSet();
		temp.serviceName = serviceName;

		temp.type = type;
		int index =this.counterSets.indexOf(temp);
		return index == -1 ? null : (CounterSet) this.counterSets.get(index);
	}

	public AseCounter getCounter(String serviceName, String name, int type){
		logger.info("counter =>serviceName :"+ serviceName + " name:"+ name + " type:"+ type );
		CounterSet counterSet = this.getCounterSet(serviceName, type);
		if(counterSet == null) {
			return null;
		}
		logger.info("counter set :"+ counterSet);
		return (counterSet == null) ? null : counterSet.getCounter(name);	
	}
	
	public synchronized AseCounter getCounter(String serviceName, String name, int type, boolean create){
		AseCounter counter = this.getCounter(serviceName, name, type);
		if(counter == null && create){
			counter = this.createCounter(serviceName, name, type);
		}
		return counter;
	}
	
	public synchronized AseCounter createCounter(String serviceName, String name, int type){
		CounterSet counterSet = this.getCounterSet(serviceName, type);
		if(counterSet == null){
			counterSet = new CounterSet();
			counterSet.serviceName = serviceName;
			counterSet.type = type;
			counterSet.lastCleared = System.currentTimeMillis();
			this.counterSets.add(counterSet);
		}
		
		AseCounter counter = new AseCounter(name);
		counter.setServiceName(serviceName);
		counterSet.addCounter(counter);

		//Add the counter in counterNameMap also so that it can be fetched in MeasMgr
		addSleeCounterMappingForCounter(counter);
		return counter;
	}
	
	public AseCounter getCounter(String name){
		return this.getCounter(DEFAULT_SERVICE_NAME, name, TYPE_MEASUREMENT_COUNTER, true);
	}

	public Iterator getCounterNames(String serviceName, int type){
		CounterSet counterSet = this.getCounterSet(serviceName, type);
		if(counterSet == null){
			return null;
		}
		return counterSet.counters.keySet().iterator();
	}	
	
	public void increment(AseCounter counter, int value) {
		try {
			switch (counter.getType()) {
				case AseCounter.TYPE_EVENT:
//					No need to add work to the queue for increment. Fixed as a part of Level3 Performance
					if (logger.isDebugEnabled()) {
						logger.debug("Calling the MfwInterface.incrementEventParam  :" + counter.getServiceName() + ", " + counter.getName() + ", 0 , " + value);
					}
					counterEnableFlag = this.measMgr.incrementEventParam(counter.getServiceName(), counter.getName(), value);
					if(counterEnableFlag)
					counter.count.addAndGet(value);
					break;
				case AseCounter.TYPE_THRESHOLD:
					if (logger.isDebugEnabled()) {
						logger.debug("Calling the ThresholdValue  :" + value + " for :" + counter.getName());
					}
					counter.count.addAndGet(value);
					this.mfwThresholdMgr.incrementThresholdValue(counter.getName(), value);
					break;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
			
	public void decrement(AseCounter counter, int value){
		try{
			switch(counter.getType()){
				case AseCounter.TYPE_EVENT:
//					No need to add work to the queue for decrement. Fixed as a part of Level3 Performance
					if(logger.isDebugEnabled()){
						logger.debug("Calling the MfwInterface.decrementEventParam  :" + counter.getServiceName() + ", " +counter.getName() + ", 0 , " + value);
					}
					counterEnableFlag = this.measMgr.decrementEventParam(counter.getServiceName(), counter.getName(), value);
					if(counterEnableFlag)
					counter.count.addAndGet(-1 * value);
					break;
				case AseCounter.TYPE_THRESHOLD:
					if(logger.isDebugEnabled()){
						logger.debug("Calling the decrementThresholdValue  :" + value +" for :"+ counter.getName());
					}
					counter.count.addAndGet(-1 * value);
					this.mfwThresholdMgr.decrementThresholdValue(counter.getName(), value);
					break;
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
//	public void setCount(AseCounter counter, int value){
//		try{
//			if(counter.getType() == AseCounter.TYPE_EVENT) {
//				if(logger.isDebugEnabled()){
//					logger.debug("Calling the MfwInterface.setEventParam  :" + counter.getServiceName() + ", " + counter.getName() + ", 0 , " + value);
//				}
//				this.measMgr.setEventParam(counter.getServiceName(), counter.getName(), counter.getName(), value);
//			}
//		}catch(Exception e){
//			logger.error(e.getMessage(), e);
//		}
//	}
	
	public void initialize() throws Exception{
	
		logger.info("ASE MEASUREMENT MANAGER initialize()");
		
		//Initialize the telnet server commands...
		TelnetServer telnetServer = (TelnetServer)Registry.lookup(Constants.NAME_TELNET_SERVER);
		if(telnetServer != null){
			telnetServer.registerHandler(Constants.CMD_GETCOUNT, this);
			telnetServer.registerHandler(Constants.CMD_CLEARCOUNT, this);
		}
		
		//Get the configuration properties...
		ConfigRepository configRep = null;
		configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
    	String strWriteToFile = configRep.getValue(Constants.PROP_LOG_MEASUREMENTS_TO_FILE);
    	this.writeToFile = (strWriteToFile != null && strWriteToFile.trim().equals("1"));
    	if(logger.isInfoEnabled()){
    		logger.info("Write Measurement Data to file flag :" + this.writeToFile);
    	}
    	
		//Get the managerAgent...
		EmsAgent agent = BaseContext.getAgent();
		
		OemsAgent oemsAgent  = (OemsAgent) Registry.lookup(Constants.OEMS_AGENT);// new OemsAgent();
		//Registry.bind(Constants.OEMS_AGENT, oemsAgent);
		
		
		EmsLiteAgent emslAgent = BaseContext.getEmslagent();
		
		//Initialize the Usage Service Impl
		if(logger.isInfoEnabled()){
			logger.info("Instantiating the Usage Service object....");
		}
		this.usageService = new AseUsageService();
		BaseContext.setUsageService(this.usageService);
		if(logger.isInfoEnabled()){
			logger.info("Initialized the Usage Service.....");
		}	
		ThreadMonitor threadMonitor = (ThreadMonitor)Registry.lookup(
											Constants.NAME_THREAD_MONITOR);

		//ThresholdManager Initialization
		this.mfwThresholdMgr = ThresholdMgr.getInstance();
		this.mfwThresholdMgr.setEmsliteAgent(emslAgent);
		mfwThresholdMgr.initialize(oemsAgent, configRep, threadMonitor);
		this.mfwThresholdMgr.setThreadTimeout(AseThreadMonitor.getThreadTimeoutTime());
  		if(logger.isInfoEnabled()){
			logger.info("ThresholdMgr Initialization is Successful.");
  		}
	
		//Meas-Mgr initialization
  		//This is introduced becuase in measurement , usage param value is average of different scanned values
  		//during regular time interval. Initially it is average of all scanned values from begining
  		//now making this as a configuration paramter that will determine how many scanned values should be 
  		//taken in account to find average value.
  		String scanCountForUsageParams = configRep.getValue(Constants.PROP_USAGE_PARAMS_AVG_VALUE_SCAN_COUNT);
  		scanCountForUsageParams = (scanCountForUsageParams == null) ? "" : scanCountForUsageParams.trim();
  		MeasMgr.initialize(BaseContext.getTraceService(), oemsAgent, threadMonitor,scanCountForUsageParams);
  		this.measMgr = MeasMgr.instance();
		this.measMgr.setThreadTimeout(AseThreadMonitor.getThreadTimeoutTime());
	    this.measMgr.setEmsliteAgent(emslAgent);
		if(logger.isInfoEnabled()){
			logger.info("MeasMgr Initialization is Successful.");
		}
		
		OEMSServiceStarter oemsAgentInstence = (OEMSServiceStarter)Registry.lookup(Constants.OEMS_AGENT_WRAPPER);
		
		ComponentType componentType = oemsAgentInstence.getInstance().oemsInitDTO.getComponentType();
		String selfInstanceId = oemsAgentInstence.getInstance().oemsInitDTO.getSelfInstanceId();
		String siteId = OemsUtils.getSiteId(selfInstanceId);
		String instanceId = OemsUtils.getInstanceId(selfInstanceId);
		if (logger.isInfoEnabled()) {
			logger.info("componentType :" + componentType + " selfInstanceId:" + selfInstanceId + " instanceId:"
					+ instanceId);
		}
		String measurementConfTransId = oemsAgent.getMeasurementMgrConfig(siteId, instanceId, componentType.toString(),
				"CAS");
		MeasurementThread measurementThread = new MeasurementThread(this.measMgr, this.mfwThresholdMgr,
				this.defaultMeasurementManager, MEASUREMENT_CONFIG, THRESHOLD_CONFIG, this.getClass().getClassLoader(),
				configRep, sleeMeasurementCounterMap, minDefaultAccIntervalForSets, writeToFile, fileLogger,
				m_configRepository, measurementConfTransId);
		if (logger.isInfoEnabled()) {
			logger.info("MeasurementThread starting");
		}
		Thread loadMsetConfifThread = new Thread(measurementThread);
		loadMsetConfifThread.start();
		
		
	}
	
	public void sendRegisterRequestForPerformanceStats(){
		
		if(logger.isInfoEnabled()){
			logger.info("send performance stats reg request ....");
		}
		OemsAgent oemsAgent=(OemsAgent) Registry.lookup(Constants.OEMS_AGENT);
		oemsAgent.sendRegisterPerStats();
	}
	
	public void start() throws Exception{
	}
	
	public void shutdown() throws Exception {
		this.mfwThresholdMgr.shutdown();
		this.measMgr.shutdown();
		if(this.fileLogger != null){
			this.fileLogger.shutdown();
		}
	}

	private void initNSEPMeasurementCounters() {
		if(logger.isDebugEnabled()) {
			logger.debug("Initializing Measurement Counters for Priority calls");
		}	
		NSEPMeasurementManager = new ServiceMeasurementManager(DEFAULT_NSEP_SERVICE_NAME);
		this.initMeasurementCounters(DEFAULT_NSEP_SERVICE_NAME, 
										NSEP_MEASUREMENT_CONFIG, 
										this.getClass().getClassLoader());
		AseMeasurementUtil.initNSEPCounters();
		logger.debug("complete Measurement Counters for Priority calls");
	}

	
	public  void initNSEPMeasurementCountersNew() {
		initNSEPMeasurementCounters();
	}
	
	private void initSoaMeasurementCounters() {
		if(logger.isDebugEnabled()) {
			logger.debug("Initiaizing Measurement Counters for Soa service");
		}
		SoaMeasurementManager = new ServiceMeasurementManager(DEFAULT_SOA_SERVICE_NAME);
		this.initMeasurementCounters(DEFAULT_SOA_SERVICE_NAME,
										SOA_MEASUREMENT_CONFIG,
										this.getClass().getClassLoader());
		SoaMeasurementUtil.initSoaCounters();
	}

	public  void initSoaMeasurementCountersNew() {
		initSoaMeasurementCounters();
	}
	public void initMeasurementCounters(String serviceName, String configFile, ClassLoader loader) {
		InputStream stream = null;
		try {
			if (configFile == null) {
				return;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Initializing Measurement Counters from Config file: " + configFile);
				logger.debug("Service name: " + serviceName);
			}
			if (loader == null) {
				return;
			}

			//Bug 8529 Issue in deployment of more than one resource 
			if (loader instanceof AppClassLoader) {
				if (logger.isDebugEnabled()) logger.debug("Find Resource of AppClassloader called for " + configFile);
				URL url = ((AppClassLoader) loader).findResource(configFile);
				if (logger.isDebugEnabled())
					logger.debug("Find Resource of AppClassloader called successfully URL is:" + url);
				stream = url.openStream();
			} else {
				stream = loader.getResourceAsStream(configFile);
			}
			DocumentBuilderFactory factory = null;
			factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(stream);

			//Iterate through the Measurement Counters and initialize them.
			Element rootEl = doc.getDocumentElement();
			NodeList list = rootEl.getElementsByTagName("MeasurementCounter");
			for (int i = 0; i < list.getLength(); i++) {
				Element el = (Element) list.item(i);

				//Skip if the parent is not measurement manager
				if (!el.getParentNode().getNodeName().equals("MeasurementMgr")) {
					continue;
				}

				String counterName = el.getAttribute("id");
				String type = el.getAttribute("mode");
				String oid = el.getAttribute("oid");
				String perfOid = el.getAttribute("perfOid");

				AseCounter counter = this.getCounter(serviceName, counterName, TYPE_MEASUREMENT_COUNTER, true);
				//Set the type for this counter.
				if (type.equals("event")) {
					counter.setType(AseCounter.TYPE_EVENT);
				} else if (type.equals("usage")) {
					counter.setType(AseCounter.TYPE_USAGE);
				} else {
					counter.setType(AseCounter.TYPE_UNKNOWN);
				}

				counter.setServiceId(DEFAULT_SERVICE_ID);
				counter.setOid(oid);
				counter.setPerfOid(perfOid);
				if (perfOid != null && !perfOid.trim().equals("")) {
					this.usageService.addUsageParam(counter);
				}
			}

			//Initialize the measurement framework using this XML file.
			if (logger.isDebugEnabled()) {
				logger.debug("loading Measurement Data: Service Name = " + serviceName + ", Config file = " + configFile);
			}
			this.measMgr.loadMeasDataFromServiceDeploymentDescriptor(serviceName, doc, loader);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		
	}

	/**
	 * Initializes the application specific measurement
	 * counters by parsing the counter xml file and
	 * storing them as AseCounters in the Counter Map
	 * 
	 * @param serviceName - application whose counters are to be initialized
	 * @param configFile - path of measurement-config.xml
	 */
	public void initAppMeasurementCounters(String serviceName, String configFile, ClassLoader loader) {
		InputStream  stream =  null;
		try{
			if((configFile == null) || (configFile.trim().equals(""))) {
				logger.error("Configuration file name not found.");
				return;
			}

			if(logger.isDebugEnabled()) {

				logger.debug("Initializinource( AppMeasurement Counters for Service name: "+serviceName);
				logger.debug("from Config file: "+configFile);
			}

			//bug 8939,code added to make loading of measurement counters through class loader
			if(logger.isDebugEnabled())logger.debug("Find Resource of AppClassloader called for "+configFile);
			URL url=((AppClassLoader)loader).findResource(configFile); 
			if(logger.isDebugEnabled())logger.debug("Find Resource of AppClassloader called successfully URL is:"+url);
			stream =url.openStream();

			DocumentBuilderFactory factory = null;
			factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(stream);

			NodeList msrMgrNodelist = doc.getElementsByTagName("MeasurementMgr");

			if (null == msrMgrNodelist) {
				logger.error("<MeasurementMgr> element not found in Configuration xml file");
				return;
			}

			int numMsrMgrNodes = msrMgrNodelist.getLength();
			if (numMsrMgrNodes != 1) {
				logger.error("Invalid number of <MeasurementMgr> elements in Configuration xml file");
				return;
			}

			Element msrMgrNode = (Element) msrMgrNodelist.item( 0 );
			NodeList msrMgrDataNodeList = msrMgrNode.getChildNodes();
			if (null==msrMgrDataNodeList) {
				logger.error("<MeasurementMgr> element does not have any child elements");
				return;
			}
			
			int numMsrMgrDataNodes = msrMgrDataNodeList.getLength();
			boolean setProcessed = false;
			for (int i=0;  i<numMsrMgrDataNodes; i++) {
				Node msrMgrDataNode = msrMgrDataNodeList.item( i );
				if (Node.ELEMENT_NODE != msrMgrDataNode.getNodeType()) {
					continue;
				}
				Element  elem = (Element) msrMgrDataNode;
				if (elem.getTagName().equalsIgnoreCase("MeasurementSet")) {
					if(setProcessed == false) {
						setProcessed = true;
						String id = elem.getAttribute("id");
						String accumInterval = elem.getAttribute("accumulationInterval");
						String version = elem.getAttribute("version");
						if (id==null || accumInterval==null || version==null  ||
								id.equals("") || accumInterval.equals("") || version.equals("")) {

							logger.error("Attributes missing for <MeasurementSet> element");
							CounterSet appCounterSet = this.getCounterSet(serviceName,TYPE_MEASUREMENT_COUNTER);

							if((appCounterSet == null) || (counterSets.indexOf(appCounterSet) == -1)) {
								return;
							}
							else { 
								counterSets.remove(counterSets.indexOf(appCounterSet));
							}
							return;
						}
					}
				}
				else if (elem.getTagName().equalsIgnoreCase("MeasurementCounter")) {
					String counterName = elem.getAttribute("id");
					String type = elem.getAttribute("mode");
					String oid = elem.getAttribute("oid");
					String refType = elem.getAttribute("refType");
					if (null==counterName 			|| 
							null==refType      		|| 
							null==oid          		||
							null==type         		||
							counterName.equals("")  ||
							refType.equals("") 		||
							oid.equals("")     		||
							type.equals("")) {
						logger.error("Attributes missing for <MeasurementCounter> element");
						return;
					}
					if (!refType.equals("name")) {
						logger.error("Invalid refType "+refType+" for counter " + counterName);
						return;
					}
					if (!type.equals("event")) {
						logger.error("Invalid counter mode "+type+" for counter " + counterName);
						return ;
					}
					AseCounter counter = this.getCounter(serviceName, counterName, TYPE_MEASUREMENT_COUNTER, true);
					//Set the type for this counter.
					if(type.equals("event")){
						counter.setType(AseCounter.TYPE_EVENT);					
					}
					counter.setOid(oid);
				}  else {
					logger.error("Unsupported element "+elem.getTagName());
				}
			}   

			if(this.writeToFile) {
				this.fileLogger.addAppCounterToFormatter(serviceName);
			}
			//Here loader is used to load the class mentioned as supplier attribute 
			//in the xml for only event type counter.
			this.measMgr.loadMeasDataFromServiceDeploymentDescriptor(serviceName, doc, loader);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally{
			if(stream != null){
				try{
					stream.close();
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	public void initThresholdCounters(String serviceName, String configFile, ClassLoader loader) {
		if(logger.isDebugEnabled()) {
			logger.debug("Init initThresholdCounters serviceName:"+ serviceName + " configFile:"+ configFile + " loader:"+ loader);
		}
			InputStream  stream =  null;
		try{
			if(configFile == null)
				return;
			
			if(loader == null)
				return;
			//Get the xml document parsed.
			stream = loader.getResourceAsStream(configFile);
			DocumentBuilderFactory factory = null;
			factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(stream);

			//Iterate through the Measurement Counters and initialize them.
			Element rootEl = doc.getDocumentElement();
			
			//Iterate through the Threshold Counters and initialize them.
			NodeList list = rootEl.getElementsByTagName("ThresholdCounter");
			for(int i=0;i<list.getLength();i++){
				Element el = (Element)list.item(i);
				
				String counterName = el.getAttribute("oid");
				AseCounter counter = this.getCounter(serviceName, counterName, TYPE_THRESHOLD_COUNTER, true);
				counter.setType(AseCounter.TYPE_THRESHOLD);
			}
					
			//Initialize the threshold counters using the XML file.
			this.mfwThresholdMgr.populateThresCounterListFromSDD(doc);
		}catch(Exception e){
			logger.error("Exception occured");
			logger.error(e.getMessage(), e);
		}finally{
			if(stream != null){
				try{
					stream.close();
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	//---- Telnet interface started ----//
	public String execute(String command, String[] args, InputStream is, OutputStream os)
		throws CommandFailedException {
		
		command = (command == null) ? "" : command;	
		StringBuffer msg = new StringBuffer();
		String serviceName;
		
		if(command.equals(Constants.CMD_GETCOUNT)) {
			if(args.length == 0 || args[0] == null) {
				//
				// Print all the counters
				//

				msg.append("Values of the counters at ");
				msg.append(new Date());
				msg.append("\r\nCounters last cleared at ");
				msg.append(new Date(this.lastClearTime));

				for (Object counterSet : this.counterSets) {
					if (((CounterSet)counterSet).type == TYPE_MEASUREMENT_COUNTER) {
						for (Object counter : ((CounterSet)counterSet).counters.values()) {
							msg.append(counter.toString());
						}
					}
				}
			} else {
				//
				// Print service specific counters
				//

				serviceName = args[0];
				CounterSet counterSet = this.getCounterSet(serviceName, TYPE_MEASUREMENT_COUNTER);
				if(counterSet == null){
					msg.append("No counters available for " + serviceName);
				} else {
				
					msg.append("Values of the counters at ");
					msg.append(new Date());
					msg.append("\r\nCounters last cleared at ");
					msg.append(new Date(counterSet.lastCleared));

					for (Object counter : counterSet.counters.values()) {
						msg.append(counter.toString());
					}
				}
			}
		} else if (command.equals(Constants.CMD_CLEARCOUNT)){
			if(args.length == 0 || args[0] == null) {
				//
				// Clear all the counters
				//
				this.lastClearTime = System.currentTimeMillis();
				for (Object counterSet : this.counterSets) {
					if (((CounterSet)counterSet).type == TYPE_MEASUREMENT_COUNTER) {
						for (Object counter : ((CounterSet)counterSet).counters.values()) {
							((AseCounter)counter).setCount(0);
						}
					}
				}
				msg.append("Reset all the counters.");
			} else {
				// Clear service specific counters
				serviceName = args[0];
				CounterSet counterSet = this.getCounterSet(serviceName, TYPE_MEASUREMENT_COUNTER);
				if(counterSet == null){
					msg.append("No counters available for " + serviceName);
				} else {
					counterSet.lastCleared = System.currentTimeMillis();
					for (Object counter : counterSet.counters.values()) {
						((AseCounter)counter).setCount(0);
					}
					msg.append("Reset all the " + serviceName + " counters.");
				}
			}
		}
		return msg.toString();
	}

	public String getUsage(String command) {
		StringBuffer buffer = new StringBuffer();
		if(command.equals(Constants.CMD_GETCOUNT)){
			buffer.append("Prints all the counters in the system");
			buffer.append("\r\nUsage :");
			buffer.append(command);
			buffer.append(" <Service Name>");
		}else if(command.equals(Constants.CMD_CLEARCOUNT)){
			buffer.append("Resets all the event counters in the system");
			buffer.append("\r\nUsage :");
			buffer.append(command);
			buffer.append(" <Service Name>");
		}
		return buffer.toString();
	}
	//	--- Telnet Server interface ended ---- //
	
	/**
	 * Implemented from the MComponent inteface and called by the EMS 
	 * management application to set this object's running state.
	 *
	 */
	public void changeState(MComponentState state)
			throws UnableToChangeStateException {
		try {
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("Change state called on Measurement Manager :::" + state.getValue());
			}
			if(state.getValue() == MComponentState.LOADED){
				this.initialize();
			} else if(state.getValue() == MComponentState.RUNNING){
				this.start();
			} else if(state.getValue() == MComponentState.STOPPED){
				this.shutdown();
			}
		} catch(Exception e){
			throw new UnableToChangeStateException(e.getMessage());
		}
	}
	
	public void process(long currentTime)  {

		Iterator counterName = this.getCounterNames(DEFAULT_SERVICE_NAME, TYPE_MEASUREMENT_COUNTER);
		StringBuffer valueBuff = new StringBuffer("CONT: ");

		if((dumpCount == 0) || (dumpCount >= this.headerDumpFreq))  {

			StringBuffer headerBuff = new StringBuffer("CONT: ");
			while(counterName.hasNext())  {

				headerBuff.append(counterName.next().toString());
				headerBuff.append(AseStrings.COMMA);
			}

			String tmp = headerBuff.substring(0, headerBuff.lastIndexOf(AseStrings.COMMA));
			logger.error(tmp);
			dumpCount = 1;
		}

		Iterator counterList = this.getCounterSet(DEFAULT_SERVICE_NAME, TYPE_MEASUREMENT_COUNTER).counters.values().iterator();     
		while(counterList.hasNext())  {
			AseCounter counter = (AseCounter)counterList.next();
			if(counter != null) {
				valueBuff.append(counter.getCount());
				valueBuff.append(AseStrings.COMMA);
			} else {
				if(logger.isDebugEnabled())logger.debug("Counter is null");
			}
		}

		String str = valueBuff.substring(0, valueBuff.lastIndexOf(AseStrings.COMMA));
		logger.error(str);
		dumpCount+=1;
		
		printAppcounters();
	}

	public void printAppcounters() {
		Iterator temp = this.serviceNames.listIterator();
		Iterator counterName = null;
		Iterator counterList = null;
		String serviceName =null;

		while(temp.hasNext()) {
			ApplicationList appTemp = (ApplicationList)temp.next();
			if(appTemp.status == STATUS_ACTIVE)	{
				serviceName = appTemp.serviceName;
				counterName = this.getCounterNames(serviceName,TYPE_MEASUREMENT_COUNTER);

				if(counterName != null) {
					StringBuffer valueBuff = new StringBuffer("APPLICATION CONT for "+serviceName + ": ");

					if((appTemp.dumpCount == 0) || (appTemp.dumpCount >= this.headerDumpFreq))  {

						StringBuffer headerBuff = new StringBuffer("APPLICATION CONT for "+serviceName + ": ");

						while(counterName.hasNext())  {
							headerBuff.append(counterName.next().toString());
							headerBuff.append(AseStrings.COMMA);
						}
						String tmp = headerBuff.substring(0, headerBuff.lastIndexOf(AseStrings.COMMA));
						logger.error(tmp);
						appTemp.dumpCount = 1;
					}

					counterList = this.getCounterSet(serviceName, TYPE_MEASUREMENT_COUNTER).counters.values().iterator();     
					while(counterList.hasNext())  {
						AseCounter counter = (AseCounter)counterList.next();
						if(counter != null) {
							valueBuff.append(counter.getCount());
							valueBuff.append(AseStrings.COMMA);
						} else {
							logger.debug("Counter is null");
						}
					}

					String str = valueBuff.substring(0, valueBuff.lastIndexOf(AseStrings.COMMA));
					logger.error(str);
					appTemp.dumpCount+=1;
				}
			}
		}
	}

	public void registerForBackgroundProcessNew() {
		registerForBackgroundProcess();
	}
	private void registerForBackgroundProcess() {
		ConfigRepository config = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);	
		try {
			this.headerDumpFreq = (int)Integer.parseInt(config.getValue(Constants.FREQ_HEADER_DUMP));
		} catch (Exception e) {
			this.headerDumpFreq = 1000;
		}
		long dumpPeriod;
		try {
			String str = config.getValue(Constants.DUR_COUNTER);
			if (str != null)  {
				dumpPeriod = (long)Long.parseLong(str);
			} else { 
				logger.error("Unable to Register Measurement Manager to BKG Processor");
				return;
			}
		} catch (Exception e) {
			logger.error("Unable to Register Measurement Manager");
			return;
		}

		
		try  {
			AseBackgroundProcessor processor = (AseBackgroundProcessor) Registry.lookup(Constants.BKG_PROCESSOR);
			processor.registerBackgroundListener(this, dumpPeriod);
		} catch (Exception e)  {
			logger.error(e.getMessage(), e);
		}
	}
    
	/**
	 * Implemented from the MComponent interface and called by the EMS 
	 * management application to update this object's configuration.
	 *
	 */
	public void updateConfiguration(Pair[] configData, OperationType opType) throws UnableToUpdateConfigException {
		// No op.
	}

	public MeasurementManager getDefaultMeasurementManager() {
		return defaultMeasurementManager;
	}

	public MeasurementManager getNSEPMeasurementManager() {
		return NSEPMeasurementManager;
	}

	public MeasurementManager getSoaMeasurementManager() {
		return SoaMeasurementManager;
	}
	
	public static class CounterSet{
		private int type;
		private String serviceName;
		private long lastCleared;
		private Map counters = new TreeMap();
		
		public AseCounter getCounter(String name){
			return (AseCounter)this.counters.get(name);
		}
		
		public void addCounter(AseCounter counter){
			if(counter != null){
				this.counters.put(counter.getName(), counter);
			}
		}
		
		public boolean equals(Object other){
			if(!(other instanceof CounterSet)){
				return false;
			}
			CounterSet temp = (CounterSet) other;
			
			if(this.type != temp.type)
				return false;
			
			if(this.serviceName == null || temp.serviceName == null)
				return false;
		
			if(!this.serviceName.equals(temp.serviceName))
				return false;
			
			return true;
		}
		
		public int hashCode(){
			int hash = -1;
			if(this.serviceName != null){
				hash = this.serviceName.hashCode(); 
			}
			hash *= this.type;
			return hash;
		} 
	}
	
	/**
	 * To remove the counters specific to a certain service
	 * @param serviceName
	 */
	public void removeAppMeasCounters(String serviceName){

		if(logger.isDebugEnabled()) {
			logger.debug("Removing Counters for Service name: "+serviceName);
		}
		CounterSet appCounterSet = this.getCounterSet(serviceName,TYPE_MEASUREMENT_COUNTER);

		if((appCounterSet == null) || (counterSets.indexOf(appCounterSet) == -1)) {
			logger.error("Intended Counter set not found in the list");
			return;
		}
		else { 
			this.measMgr.removeMeasData(serviceName);
			counterSets.remove(counterSets.indexOf(appCounterSet));
			removeSleeCounterMappingForService(serviceName);
		}
		if(logger.isDebugEnabled()) {
			logger.debug("Removed Counters for Service name: "+serviceName);
		}
	}
	
	/**
	 *  Class used to define a list used to 
	 *  store the service that uses measurement
	 *  counters 
	 */
	public static class ApplicationList {
		private String serviceName;
		private String measurement_config_path;
		private int status;
		private int dumpCount;
	}
	
	
	public void addServiceName(String serviceName, String path,int status) {
			ApplicationList list = null;
			
			if(serviceName != null && path != null) {
				list = new ApplicationList();
				list.serviceName = serviceName;
				list.measurement_config_path = path;
				list.status = status;
				serviceNames.add(list);
			}
		}
		
		public void removeServiceName(String serviceName) {
			if(logger.isDebugEnabled()) {
				logger.debug("Removing service "+ serviceName);
			}
			Iterator temp = this.serviceNames.listIterator();
			int index=-1;
			ApplicationList appTemp = null;
			
			while(temp.hasNext()) {
				appTemp = (ApplicationList)temp.next();
				index++;
				if(appTemp.serviceName.equals(serviceName)) {
					this.serviceNames.remove(index);
					return;
				}
			}
		}

		public String getMsrConfigPath(String serviceName, int status) {
			Iterator temp = this.serviceNames.listIterator();
			String path = null;
			ApplicationList appTemp = null;

			while(temp.hasNext()) {
				appTemp = (ApplicationList)temp.next();
				if((appTemp.status == status) && (appTemp.serviceName.equals(serviceName)))	
					path = appTemp.measurement_config_path;
			}
			return path;
		}
		
		public void setAppStatusActive(String serviceName) {
			Iterator temp = this.serviceNames.listIterator();
			int index=-1;
			ApplicationList appTemp = null;

			while(temp.hasNext()) {
				appTemp = (ApplicationList)temp.next();
				index++;
				if((appTemp.status == STATUS_INACTIVE) && (appTemp.serviceName.equals(serviceName)))	{
					appTemp.status = STATUS_ACTIVE;
					this.serviceNames.set(index, appTemp);
				}
			}
		}
		
		public void setAppStatusInactive(String serviceName) {
			Iterator temp = this.serviceNames.listIterator();
			int index=-1;
			ApplicationList appTemp = null;

			while(temp.hasNext()) {
				appTemp = (ApplicationList)temp.next();
				index++;
				if((appTemp.status == STATUS_ACTIVE) && (appTemp.serviceName.equals(serviceName)))	{
					appTemp.status = STATUS_INACTIVE;
					this.serviceNames.set(index, appTemp);
				}
			}
		}

	private void addSleeCounterMappingForCounter(AseCounter aseCounter){
		String serviceName = aseCounter.getServiceName();
		String counterName = aseCounter.getName();

		if (sleeMeasurementCounterMap.containsKey(serviceName)) {
			Map<String, SleeMeasurementCounter> entityCountersMapping = sleeMeasurementCounterMap.get(serviceName);
			entityCountersMapping.put(counterName, aseCounter);
		} else {
			HashMap<String, SleeMeasurementCounter> entityCountersMapping = new HashMap<String, SleeMeasurementCounter>();
			entityCountersMapping.put(counterName, aseCounter);
			sleeMeasurementCounterMap.put(serviceName, entityCountersMapping);
		}
	}

	private void removeSleeCounterMappingForService(String serviceName){
		if (sleeMeasurementCounterMap.containsKey(serviceName)) {
			sleeMeasurementCounterMap.remove(serviceName);
		} else {
			logger.error("Unable to find the service name key in sleeMeasurementCounterMap for serviceName: "+ serviceName);
		}
	}

	public void updateClearTime(long lastClearTime) {
		this.lastClearTime =lastClearTime;
		
	}
}
