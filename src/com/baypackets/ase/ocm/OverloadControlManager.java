/*
 * Created on Oct 8, 2004
 *
 */
package com.baypackets.ase.ocm;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.baypackets.ase.common.AseBackgroundProcessor;
import com.baypackets.ase.common.BackgroundProcessListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseEngine;
import com.baypackets.ase.dispatcher.Rule;
import com.baypackets.ase.dispatcher.RulesRepository;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.ocm.OverloadEvent;
import com.baypackets.ase.spi.ocm.OverloadListener;
import com.baypackets.ase.spi.ocm.OverloadManager;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.EvaluationVersion;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

/**
 * @author Dana
 *         <p>
 *         This class monitors system overload status using predefined overload
 *         control parameters. Each parameter has its maximum value and wieght.
 *         Overload factor is calculate with the formular: OLF = p1/p1max*w1 +
 *         p2/p2max*w2 + ... + pn/pnmax*wn. It generates overload events and
 *         sends to event listeners. There are basically two type of events. One
 *         is for alarming/clearing max limit reached, and another for notifing
 *         OLF changed.
 *         </p>
 */
public class OverloadControlManager implements MComponent,
		BackgroundProcessListener, OverloadManager {

	// TODO
	// OID entry in ase.properties
	// server-ocm.xml entry
	public static final int CLEARED = 0;
	public static final int ALARMED = 1;

	/*
	 * Congestion control change
	 */
	public static final int LEVEL1_ALARMED = 2;
	public static final int LEVEL2_ALARMED = 3;
	public static final int LEVEL3_ALARMED = 4;
	//	

	// supported overload parameters
	public static final String CPU_USAGE = "CPU Usage";
	public static final String PROTOCOL_SESSION_COUNT = "Protocol Session Count";
	public static final String APP_SESSION_COUNT = "Application Session Count";
	// public static final String RESPONSE_TIME = "Response Time";
	public static final String MEMORY_USAGE = "Memory Usage";

	/*
	 * Congestion control change
	 */
	public static final String CONTENTION_LEVEL_ONE_MEMORY_USAGE = "Contention Level One for Memory";
	public static final String CONTENTION_LEVEL_TWO_MEMORY_USAGE = "Contention Level two for Memory";
	public static final String CONTENTION_LEVEL_THREE_MEMORY_USAGE = "Contention Level three for Memory";
	public static final String CONTENTION_LEVEL_ONE_CPU_USAGE = "Contention Level One for CPU";
	public static final String CONTENTION_LEVEL_TWO_CPU_USAGE = "Contention Level two for CPU";
	public static final String CONTENTION_LEVEL_THREE_CPU_USAGE = "Contention Level three for CPU";
	public static final String CONTENTION_LEVEL_ONE_ACTIVE_CALLS = "Contention Level One for Max Active Calls";
	public static final String CONTENTION_LEVEL_TWO_ACTIVE_CALLS = "Contention Level two for Max Active Calls";
	public static final String CONTENTION_LEVEL_THREE_ACTIVE_CALLS = "Contention Level three for Max Active Calls";
	
	public static final String NETWORK_TRANSACTIONS_PER_SECOND = "Average Network Transactions Per Second";
	public static final String AGGREGATED_TRANSACTIONS_PER_SECOND = "Average Aggregated Transactions Per Second";
	
	public static final String NEW_CALLS_PER_SECOND = "New Calls Per second";
	
	// Congestion control change ended

	public static int CPU_USAGE_ID;
	// public static int PROTOCOL_SESSION_COUNT_ID;
	public static int APP_SESSION_COUNT_ID;
	// public static int RESPONSE_TIME_ID;
	public static int MEMORY_USAGE_ID;

	/*
	 * Congestion control change
	 */
	public static int CONTENTION_LEVEL_ONE_MEMORY_USAGE_ID;
	public static int CONTENTION_LEVEL_TWO_MEMORY_USAGE_ID;
	public static int CONTENTION_LEVEL_THREE_MEMORY_USAGE_ID;
	public static int CONTENTION_LEVEL_ONE_CPU_USAGE_ID;
	public static int CONTENTION_LEVEL_TWO_CPU_USAGE_ID;
	public static int CONTENTION_LEVEL_THREE_CPU_USAGE_ID;
	public static int CONTENTION_LEVEL_ONE_ACTIVE_CALLS_ID;
	public static int CONTENTION_LEVEL_TWO_ACTIVE_CALLS_ID;
	public static int CONTENTION_LEVEL_THREE_ACTIVE_CALLS_ID;
	// Congestion control change ended
	

	public static int NETWORK_TRANSACTIONS_PER_SECOND_ID;
	public static int NEW_CALLS_PER_SECOND_ID;
	public static int AGGREGATED_TRANSACTIONS_PER_SECOND_ID;
	
	public static HashMap<String, OverloadParameter.Type> PARAM_NAME_TYPE_MAP = new HashMap<String, OverloadParameter.Type>();

	private static String ingwMessageQueue = null;
    private static String nsepIngwPriority = null;
    
    private static int memoryIgnoreCounter = 0;
    private static int confMemCounter = 0;
    
    private static ConfigRepository config = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
	static {
		PARAM_NAME_TYPE_MAP.put(CPU_USAGE, OverloadParameter.Type.CPU_USAGE);
		PARAM_NAME_TYPE_MAP.put(MEMORY_USAGE,
				OverloadParameter.Type.MEMORY_USAGE);
		/*
		 * Congestion control change
		 */
		PARAM_NAME_TYPE_MAP.put(CONTENTION_LEVEL_ONE_MEMORY_USAGE,
				OverloadParameter.Type.CONTENTION_LEVEL_ONE_MEMORY_USAGE);
		PARAM_NAME_TYPE_MAP.put(CONTENTION_LEVEL_TWO_MEMORY_USAGE,
				OverloadParameter.Type.CONTENTION_LEVEL_TWO_MEMORY_USAGE);
		PARAM_NAME_TYPE_MAP.put(CONTENTION_LEVEL_THREE_MEMORY_USAGE,
				OverloadParameter.Type.CONTENTION_LEVEL_THREE_MEMORY_USAGE);
		PARAM_NAME_TYPE_MAP.put(CONTENTION_LEVEL_ONE_CPU_USAGE,
				OverloadParameter.Type.CONTENTION_LEVEL_ONE_CPU_USAGE);
		PARAM_NAME_TYPE_MAP.put(CONTENTION_LEVEL_TWO_CPU_USAGE,
				OverloadParameter.Type.CONTENTION_LEVEL_TWO_CPU_USAGE);
		PARAM_NAME_TYPE_MAP.put(CONTENTION_LEVEL_THREE_CPU_USAGE,
				OverloadParameter.Type.CONTENTION_LEVEL_THREE_CPU_USAGE);
		PARAM_NAME_TYPE_MAP.put(CONTENTION_LEVEL_ONE_ACTIVE_CALLS,
				OverloadParameter.Type.CONTENTION_LEVEL_ONE_ACTIVE_CALLS);
		PARAM_NAME_TYPE_MAP.put(CONTENTION_LEVEL_TWO_ACTIVE_CALLS,
				OverloadParameter.Type.CONTENTION_LEVEL_TWO_ACTIVE_CALLS);
		PARAM_NAME_TYPE_MAP.put(CONTENTION_LEVEL_THREE_ACTIVE_CALLS,
				OverloadParameter.Type.CONTENTION_LEVEL_THREE_ACTIVE_CALLS);
		PARAM_NAME_TYPE_MAP.put(APP_SESSION_COUNT,
				OverloadParameter.Type.APPLICATION_SESSION_COUNT);
		confMemCounter = Integer.parseInt(config.getValue(Constants.IGNORE_MEMORY_ALARM));
		// Congestion control change ended
		
		PARAM_NAME_TYPE_MAP.put(NETWORK_TRANSACTIONS_PER_SECOND,
				OverloadParameter.Type.NETWORK_TRANSACTIONS_PER_SECOND);
		PARAM_NAME_TYPE_MAP.put(AGGREGATED_TRANSACTIONS_PER_SECOND,
				OverloadParameter.Type.AGGREGATED_TRANSACTIONS_PER_SECOND);
		PARAM_NAME_TYPE_MAP.put(NEW_CALLS_PER_SECOND,
				OverloadParameter.Type.NEW_CALLS_PER_SECOND);
	}

	private int dumpCount = 0;
	private int dumpHeader = 0;
	private long ocmDumpDuration;
	private int headerDumpFreq;
	private long ocmDumpFreq;

	private float maxOverallCPU;
	private float maxOverallMem;

	private static Logger logger = Logger
			.getLogger(OverloadControlManager.class);
	private static String propertyfile = com.baypackets.ase.util.Constants.ASE_HOME
			+ File.separator + "conf" + File.separator + "server-ocm.xml";

	private boolean enabled;
	private long scanInterval;
	private OverloadParameter[] parameters; // Init at loading time. Parameter
											// id is used as index
	//private int[] controlStatus; // Init at loading time. Parameter id is used
									// as index
	private BitSet[] controlStatuses;
	private List<OverloadListener> listeners;
	private Map olfListeners; // OLF level name is usaed as key

	private boolean cpuScanEnabled = true;

	// Added variables for NSEP(Priority) Call handling
	private static String nsepPropertyFile = Constants.ASE_HOME
			+ File.separator + "conf" + File.separator + "nsep-server-ocm.xml";
	private OverloadParameter[] nsepParameters;
	private int[] nsepControlStatus;
	private List nsepListeners;
	private Map nsepOlfListeners;
	private AseEngine engine = null;

	private ArrayList<Rule> ocmWhiteList = new ArrayList<Rule>();
	private ArrayList<Rule> nsepWhiteList = new ArrayList<Rule>();
	private float ocmAlarmHysteresis;

	public void init(String filename) throws Exception {
		listeners = new ArrayList<OverloadListener>();
		olfListeners = new HashMap();

		// Load configuration from xml file.
		loadConfig(filename, false);
		// Load configuration from nsep-server-ocm.xml for NSEP calls
		engine = (AseEngine) Registry.lookup(Constants.NAME_ENGINE);
		// Load configuration from EMS.
//		ConfigRepository config = (ConfigRepository) Registry
//				.lookup(Constants.NAME_CONFIG_REPOSITORY);
		ingwMessageQueue = (String)config.getValue(Constants.INGW_MSG_QUEUE);
		nsepIngwPriority = 	(String)config.getValue(Constants.NSEP_INGW_PRIORITY);
		if (logger.isDebugEnabled()) {
			logger.debug("Messages from INGw will come in " + ingwMessageQueue + " queue");
			logger.debug("All call prioirty support with INGw messages coming in priority queue " + nsepIngwPriority);
		}

		//Check is not done here as there are lot of places where nsep counters are getting incremented
		//So to prevent regression the check is not applied here as at other places
		if (engine.isCallPriorityEnabled()) {
			if (logger.isDebugEnabled()) {
				logger
						.debug("Call priority feature is enabled: initializing OCM");
			}
			nsepListeners = new ArrayList();
			nsepOlfListeners = new HashMap();
			this.loadConfig(nsepPropertyFile, true);
		}

		// Initialize statis IDs
		CPU_USAGE_ID = this.getParameterId(CPU_USAGE);
		// PROTOCOL_SESSION_COUNT_ID =
		// this.getParameterId(PROTOCOL_SESSION_COUNT);
		APP_SESSION_COUNT_ID = this.getParameterId(APP_SESSION_COUNT);
		// RESPONSE_TIME_ID = this.getParameterId(RESPONSE_TIME);
		MEMORY_USAGE_ID = this.getParameterId(MEMORY_USAGE);

		/*
		 * Congestion control change
		 */

		CONTENTION_LEVEL_ONE_MEMORY_USAGE_ID = this
				.getParameterId(CONTENTION_LEVEL_ONE_MEMORY_USAGE);
		CONTENTION_LEVEL_TWO_MEMORY_USAGE_ID = this
				.getParameterId(CONTENTION_LEVEL_TWO_MEMORY_USAGE);
		CONTENTION_LEVEL_THREE_MEMORY_USAGE_ID = this
				.getParameterId(CONTENTION_LEVEL_THREE_MEMORY_USAGE);
		CONTENTION_LEVEL_ONE_CPU_USAGE_ID = this
				.getParameterId(CONTENTION_LEVEL_ONE_CPU_USAGE);
		CONTENTION_LEVEL_TWO_CPU_USAGE_ID = this
				.getParameterId(CONTENTION_LEVEL_TWO_CPU_USAGE);
		CONTENTION_LEVEL_THREE_CPU_USAGE_ID = this
				.getParameterId(CONTENTION_LEVEL_THREE_CPU_USAGE);
		CONTENTION_LEVEL_ONE_ACTIVE_CALLS_ID = this
				.getParameterId(CONTENTION_LEVEL_ONE_ACTIVE_CALLS);
		CONTENTION_LEVEL_TWO_ACTIVE_CALLS_ID = this
				.getParameterId(CONTENTION_LEVEL_TWO_ACTIVE_CALLS);
		CONTENTION_LEVEL_THREE_ACTIVE_CALLS_ID = this
				.getParameterId(CONTENTION_LEVEL_THREE_ACTIVE_CALLS);
		// 
		
		NETWORK_TRANSACTIONS_PER_SECOND_ID = this.getParameterId(NETWORK_TRANSACTIONS_PER_SECOND);
		AGGREGATED_TRANSACTIONS_PER_SECOND_ID = this.getParameterId(AGGREGATED_TRANSACTIONS_PER_SECOND);
		NEW_CALLS_PER_SECOND_ID = this.getParameterId(NEW_CALLS_PER_SECOND);

		String value = null;
		try {
			// value = config.getValue(Constants.OID_NORMAL_MAX_CPU);
			// if (value != null) {
			// setLimit(CPU_USAGE, Float.parseFloat(value));
			// }
			//	        
			// value = config.getValue(Constants.OID_MAX_PROTOCOL_SESSIONS);
			// if (value != null) {
			// setLimit(PROTOCOL_SESSION_COUNT, Integer.parseInt(value));
			// }
			//	    	
			// value = config.getValue(Constants.OID_MAX_APPLICATION_SESSIONS);
			// if (value != null) {
			// setLimit(APP_SESSION_COUNT, Integer.parseInt(value));
			// }

			if (EvaluationVersion.FLAG) {
				logger
						.info("OCM is not supported in evaluation version of SAS");
				this.enabled = false;
			} else {
				value = config.getValue(Constants.OID_ENABLE_OCM);
				if (value != null) {
					this.enabled = value.equals("1") ? true : false;
				}
			}

			value = config.getValue(Constants.OID_CPU_SCAN_INTERVAL_IN_SECS);
			if (value != null) {
				setScanInterval(Long.parseLong(value));
			}
			
			if (logger.isDebugEnabled()) {
				logger
						.debug("set max limits if configured in OIDS in property file");
			}

			// value = config.getValue(Constants.OID_NORMAL_MAX_MEMORY);
			// if (value != null) {
			// this.setLimit(MEMORY_USAGE, Integer.parseInt(value));
			//				
			// }
			/*
			 * Congestion control change
			 */
			value = config
					.getValue(Constants.OID_CONTENTION_LEVEL_ONE_CPU_USAGE);
			if (value != null) {
				this.setLimit(CONTENTION_LEVEL_ONE_CPU_USAGE, Float
						.parseFloat(value));

			}

			value = config
					.getValue(Constants.OID_CONTENTION_LEVEL_TWO_CPU_USAGE);
			if (value != null) {
				this.setLimit(CONTENTION_LEVEL_TWO_CPU_USAGE, Float
						.parseFloat(value));

			}

			value = config
					.getValue(Constants.OID_CONTENTION_LEVEL_THREE_CPU_USAGE);
			if (value != null) {
				this.setLimit(CONTENTION_LEVEL_THREE_CPU_USAGE, Float
						.parseFloat(value));

			}

			value = config
					.getValue(Constants.OID_CONTENTION_LEVEL_ONE_MEMORY_USAGE);
			if (value != null) {
				this.setLimit(CONTENTION_LEVEL_ONE_MEMORY_USAGE, Integer
						.parseInt(value));

			}

			value = config
					.getValue(Constants.OID_CONTENTION_LEVEL_TWO_MEMORY_USAGE);
			if (value != null) {
				this.setLimit(CONTENTION_LEVEL_TWO_MEMORY_USAGE, Integer
						.parseInt(value));

			}

			value = config
					.getValue(Constants.OID_CONTENTION_LEVEL_THREE_MEMORY_USAGE);
			if (value != null) {
				this.setLimit(CONTENTION_LEVEL_THREE_MEMORY_USAGE, Integer
						.parseInt(value));

			}
			
			value = config
					.getValue(Constants.OID_NETWORK_TRANSACTIONS_PER_SECOND);
			
			if (value != null) {
				this.setLimit(NETWORK_TRANSACTIONS_PER_SECOND, Float
						.parseFloat(value));

			}
			

			value = config
					.getValue(Constants.OID_AGGREGATED_TRANSACTIONS_PER_SECOND);
			if (value != null) {
				this.setLimit(AGGREGATED_TRANSACTIONS_PER_SECOND, Float
						.parseFloat(value));
			}
			
			
			value = config
					.getValue(Constants.OID_NEW_CALLS_PER_SECOND);
			if (value != null) {
				this.setLimit(NEW_CALLS_PER_SECOND, Float
						.parseFloat(value));
			}


			// Congestion control change Ended

			// Added for NSEP (priority) call handling only if enabled
			// if(engine.isCallPriorityEnabled()) {
			//
			// value = config.getValue(Constants.NSEP_MAX_CPU);
			// if(value != null) {
			// this.setNSEPLimit(CPU_USAGE, Float.parseFloat(value));
			// this.maxOverallCPU = Float.parseFloat(value)*1.1f;
			// logger.error("Max CPU = " + this.maxOverallCPU);
			// if(this.maxOverallCPU > 90.0f) {
			// this.maxOverallCPU = 90.0f;
			// }
			// }
			//
			// value = config.getValue(Constants.NSEP_MAX_PROTOCOL_SESSIONS);
			// if(value != null) {
			// this.setNSEPLimit(PROTOCOL_SESSION_COUNT,
			// Integer.parseInt(value));
			// }
			//
			// value = config.getValue(Constants.NSEP_MAX_APPLICATION_SESSIONS);
			// if(value != null) {
			// this.setNSEPLimit(APP_SESSION_COUNT, Integer.parseInt(value));
			// }
			//			
			// value = config.getValue(Constants.NSEP_MAX_MEMORY);
			// if (value != null) {
			// this.setNSEPLimit(MEMORY_USAGE, Integer.parseInt(value));
			// this.maxOverallMem = Integer.parseInt(value)*1.1f;
			// logger.error("Max Memory = " + this.maxOverallMem);
			// if(this.maxOverallMem > 90.0f) {
			// this.maxOverallMem = 90.0f;
			// }
			// }
			// }
			String strValue = (String) config
					.getValue(Constants.OCM_ALARM_HYSTERESIS);
			if (strValue != null) {
				ocmAlarmHysteresis = Float.parseFloat(strValue);
			}
			// parameters[PROTOCOL_SESSION_COUNT_ID].setHysteresisFactor(ocmAlarmHysteresis);
			// parameters[APP_SESSION_COUNT_ID].setHysteresisFactor(ocmAlarmHysteresis);
			// if(engine.isCallPriorityEnabled()) {
			// nsepParameters[PROTOCOL_SESSION_COUNT_ID].setHysteresisFactor(ocmAlarmHysteresis);
			// nsepParameters[APP_SESSION_COUNT_ID].setHysteresisFactor(ocmAlarmHysteresis);
			// }
			// }

		} catch (Exception ex) {
			logger.error(ex);
		}

		// registering for Backgroung processes
		this.registerForBackgroundProcess();

		if (logger.isEnabledFor(Level.INFO)) {
			logger.info(this.toString());
		}
	}

	/**
	 * When OCM is enabled/disabled, it also enables/disables CPU thread and
	 * sends out control sate change event
	 * 
	 * @param enabled
	 */
	public synchronized void setEnabled(boolean enabled) {
		if (EvaluationVersion.FLAG) {
			logger.info("OCM is not supported in evaluation version of SAS");
			return;
		}

		this.enabled = enabled;
		if (cpuScanEnabled) {
			CPU.getInstance().setEnable(enabled);
		}
		fireControlStateChanged();
	}

	public synchronized boolean isEnabled() {
		return enabled;
	}

	public synchronized long getScanInterval() {
		return scanInterval;
	}

	public synchronized void setScanInterval(long scanInterval) {
		this.scanInterval = scanInterval * 1000;
	}

	public void setWeight(int id, float weight) {
		parameters[id].setWeight(weight);
	}

	public synchronized void setLimit(int id, float max) {
		
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("\n OCM: setLimit  " + max +" for parameter ID "+ id);
		}
		parameters[id].setLimit(max);
	}

	public synchronized void setLimit(String name, float max) {
		int id = getParameterId(name);
		
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("\n OCM: setLimit  " + max +" for parameter name  "+name +" ID "+ id);
		}
		parameters[id].setLimit(max);
	}

	public synchronized void setNSEPLimit(int id, float max) {
		nsepParameters[id].setLimit(max);
	}

	public synchronized void setNSEPLimit(String name, float max) {
		int id = getNSEPParameterId(name);
		nsepParameters[id].setLimit(max);
	}

	/**
	 * Registers overload listener for maximum-limit-reached/cleared event
	 * 
	 * @param listener
	 *            Implements OverloadListener interface
	 */
	public void addOverloadListener(OverloadListener listener) {
		this.addOverloadListener(listener, false);
	}

	/**
	 * Registers overload listener for OLF-threshod-passed event as well as
	 * maximum-limit-reached/cleared event. The registered threshod value is
	 * used as trigger for generating event
	 * 
	 * @param listener
	 *            Implements OverloadListener interface
	 * @param threshod
	 *            A trigger for generating OLF-threshod-passed event
	 */
	public void addOverloadListener(OverloadListener listener, float threshod) {
		this.addOverloadListener(listener, threshod, false);
	}

	public void addOverloadListener(OverloadListener listener,
			boolean isPriority) {
		if (isPriority) {
			nsepListeners.add(listener);
		} else {
			listeners.add(listener);
		}
		// Sync inital control state
		listener.controlStateChanged(enabled);
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("\n OCM: add listener " + listener);
		}
	}

	/**
	 * Registers overload listener for OLF-threshod-passed event as well as
	 * maximum-limit-reached/cleared event. The registered threshod value is
	 * used as trigger for generating event
	 * 
	 * @param listener
	 *            Implements OverloadListener interface
	 * @param threshod
	 *            A trigger for generating OLF-threshod-passed event
	 */
	public void addOverloadListener(OverloadListener listener, float threshod,
			boolean isPriority) {
		if (isPriority) {
			nsepListeners.add(listener);
		} else {
			listeners.add(listener);
		}
		// Sync inital control state
		listener.controlStateChanged(enabled);

		Float key = new Float(threshod);
		ArrayList listeners = null;
		if (isPriority) {
			if (nsepOlfListeners.containsKey(key)) {
				listeners = (ArrayList) nsepOlfListeners.get(key);
				listeners.add(listener);
			} else {
				listeners = new ArrayList();
				listeners.add(listener);
				nsepOlfListeners.put(key, listeners);
			}
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("\n NSEP OCM: add Priority listener " + listener
						+ " for " + key);
			}
		} else {
			if (olfListeners.containsKey(key)) {
				listeners = (ArrayList) olfListeners.get(key);
				listeners.add(listener);
			} else {
				listeners = new ArrayList();
				listeners.add(listener);
				olfListeners.put(key, listeners);
			}
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("\n OCM: add listener " + listener + " for " + key);
			}
		}
	}

	/**
	 * Deregisters overload listener from maximum-limit-reached/cleared event
	 * 
	 * @param listener
	 *            An OverloadListener object
	 */
	public void removeOverloadListener(OverloadListener listener) {
		listeners.remove(listener);
		nsepListeners.remove(listener);
	}

	/**
	 * Deregisters overload listener from OLF-threshod-passed event as well as
	 * maximum-limit-reached/cleared event.
	 * 
	 * @param listener
	 *            An OverloadListener object
	 * @param threshod
	 *            OLF threshod
	 */
	public void removeOverloadListener(OverloadListener listener, float threshod) {
		listeners.remove(listener);
		nsepListeners.remove(listener);

		Float key = new Float(threshod);
		Object obj = olfListeners.get(key);
		if (obj != null) {
			((ArrayList) obj).remove(listener);
		}
		obj = nsepOlfListeners.get(key);
		if (obj != null) {
			((ArrayList) obj).remove(listener);
		}
	}

	/**
	 * Finds parameter ID for a predefind parameter name
	 * 
	 * @param name
	 *            Predifined parameter name
	 * @return Parameter ID
	 */
	public int getParameterId(String name) {
		for (int i = 0; i < parameters.length; i++) {
			if ((parameters[i] != null) && parameters[i].getName().equals(name)) {
				return parameters[i].getId();
			}
		}
		return -1;
	}

	public int getParameterId(OverloadParameter.Type type) {
		for (int i = 0; type != null && i < parameters.length; i++) {
			OverloadParameter.Type temp = parameters[i] != null ? parameters[i]
					.getType() : null;
			if (temp != null && temp.equals(type)) {
				return parameters[i].getId();
			}
		}
		return -1;
	}

	public OverloadParameter getParameter(OverloadParameter.Type type) {
		int id = getParameterId(type);
		return (id >= 0 && id < parameters.length) ? parameters[id] : null;
	}

	/**
	 * This method is called by parameter owner to report a value increase. If
	 * no increased amount presents, 1 is assumed.
	 * 
	 * @param id
	 *            Parameter ID
	 */
	public void increase(int id) {
		increase(id, false);
	}
	
	/**
	 * This method is called by parameter owner to report a value increase. If
	 * no increased amount presents, 1 is assumed.
	 * 
	 * @param id Parameter ID
	 * @param incremnentSipOverLaodCtr incremnts ctr in AseSipOverload used for appsession cnt
	 */
	public void increase(int id, boolean incremnentSipOverLaodCtr) {
		if (id == -1) // Congestion control enhancement for taking care of
						// getParameterID(APP_SSESSION_COUNT) and
						// getParameterID(PROTOCOL_SSESSION_COUNT)
			return;
		increase(id, 1, incremnentSipOverLaodCtr );
	}

	/**
	 * @see increase(int id)
	 */
	public synchronized void increase(int id, int amount,boolean incremnentSipOverLaodCtr) {
		parameters[id].increase(amount);
		if (engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
			this.increaseNSEP(id, amount);
		}
		
		/*
		 * Congestion control change
		 */
		if (incremnentSipOverLaodCtr && id == APP_SESSION_COUNT_ID) {
			
			for (OverloadListener oll : listeners) {
				oll.incrementActiveCall();
			}

			return;
		}
		
		int value = (int) parameters[id].getValue();
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("OCM: parameter(" + id + ") is increased to " + value);
		}
		if (!enabled) {
			return;
		}
		if (!isLimitReached(id)) {
			return;
		}
		fireLimitReached(id, value);
	}

	/**
	 * @see increase(int id)
	 */
	public void increase(int id, float amount) {
		parameters[id].increase(amount);
		float value = parameters[id].getValue();
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("OCM: parameter(" + id + ") is increased to " + value);
		}
		if (!enabled) {
			return;
		}
		if (!isLimitReached(id)) {
			return;
		}
		fireLimitReached(id, value);
	}

	/**
	 * This method is called by parameter owner to report a value decrease. If
	 * no decreased amount presents, 1 is assumed.
	 * 
	 * @param id
	 *            Parameter ID
	 */
	public void decrease(int id) {
		if (id == -1)
			return;
		decrease(id, 1);
	}

	/**
	 * @see decrease(int id)
	 */
	public synchronized void decrease(int id, int amount) {

		/*
		 * Congestion control change
		 */
		if (id == APP_SESSION_COUNT_ID) {
			
			for (OverloadListener oll : listeners) {
				oll.decrementActiveCall();
			}

			return;
		}

		parameters[id].decrease(amount);
		if (engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
			this.decreaseNSEP(id, amount);
		}

		int value = (int) parameters[id].getValue();
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("OCM: parameter(" + id + ") is decreased to " + value);
		}
		if (!enabled) {
			return;
		}
		if (!isLimitCleared(id)) {
			return;
		}
		fireLimitCleared(id, value);
	}

	/**
	 * @see decrease(int id)
	 */
	public void decrease(int id, float amount) {

		/*
		 * Congestion control change
		 */
		if (id == APP_SESSION_COUNT_ID) {
			for (OverloadListener oll : listeners) {
				oll.decrementActiveCall();
			}

			return;
		}
		parameters[id].decrease(amount);
		float value = parameters[id].getValue();
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("OCM: parameter(" + id + ") is decreased to " + value);
		}
		if (!enabled) {
			return;
		}
		if (!isLimitCleared(id)) {
			return;
		}
		fireLimitCleared(id, value);
	}

	/**
	 * This method is called by parameter owner to report a new value
	 * 
	 * @param id
	 *            Parameter ID
	 * @param value
	 *            Current value
	 */
	public synchronized void update(int id, float value) {
		parameters[id].setValue(value);
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("OCM: parameter(" + id + ") is changed to " + value);
		}
		if (engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
			nsepParameters[id].setValue(value);
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("NSEP OCM: parameter(" + id + ") is changed to "
						+ value);
			}
		}
		if (!enabled) {
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info(" OCM is not enabled so not checking limit");
			}
			return;
		}
		this.checkLimit(id, value);
		if (engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
			this.checkNSEPLimit(id, value);
		}
	}

	public synchronized void update(int id, float value, boolean priority) {
		if (engine.isCallPriorityEnabled() && priority && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
			nsepParameters[id].setValue(value);
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("NSEP OCM: parameter(" + id + ") is changed to "
						+ value);
			}
			this.checkNSEPLimit(id, value);
		} else {
			parameters[id].setValue(value);
			if (logger.isEnabledFor(Level.INFO)) {
				logger
						.info("OCM: parameter(" + id + ") is changed to "
								+ value);
			}
			this.checkLimit(id, value);
		}
	}

	private void checkNSEPLimit(int id, float value) {

		if (isNSEPLimitReached(id)) {
			fireNSEPLimitReached(id, value);
		}
		if (isNSEPLimitCleared(id)) {
			fireNSEPLimitCleared(id, value);
		}
	}

	private void checkLimit(int id, float value) {

		if (isLimitReached(id)) {
			fireLimitReached(id, value);
		}

		if (isLimitCleared(id)) {
			fireLimitCleared(id, value);
		}
	}

	/**
	 * Calculates OLF using the formular OLF = p1/p1max*w1 + p2/p2max*w2 + ... +
	 * pn/pnmax*wn. Checks OLF against each registered threshod. If any threshod
	 * is passed, a OLF-threshod-passed event is sent to listeners registered
	 * for that threshod.
	 */
	public synchronized void checkOlf() {
		if (!enabled) {
			return;
		}
		this.checkNormalOlf();
		// Checking OLF for Priority (NSEP) Calls
		if (engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
			this.checkNSEPOlf();
		}

	}

	public synchronized void checkOlf(boolean priority) {
		if (!enabled) {
			return;
		}
		if (engine.isCallPriorityEnabled() && priority && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
			this.checkNSEPOlf();
		} else {
			this.checkNormalOlf();
		}

	}

	private void checkNormalOlf() {
		float olf = calculateOlf();
		for (Iterator i = olfListeners.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			if (olf >= ((Float) entry.getKey()).floatValue()) {
				fireOlfChanged(olf, (ArrayList) entry.getValue());
			}
		}
	}

	private void checkNSEPOlf() {
		float nsepOlf = this.calculateNSEPOlf();
		for (Iterator i = nsepOlfListeners.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			if (nsepOlf >= ((Float) entry.getKey()).floatValue()) {
				fireNSEPOlfChanged(nsepOlf, (ArrayList) entry.getValue());
			}
		}
	}

	/**
	 * Calculates OLF using the formular OLF = p1/p1max*w1 + p2/p2max*w2 + ... +
	 * pn/pnmax*wn.
	 * 
	 * @return OLF
	 */
	private float calculateOlf() {
		float olf = 0f;
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] != null) {
				olf += parameters[i].getOlf();
			}
		}
		return olf;
	}

	public boolean isPriorityOverload(OverloadReason reason) {
		// if(nsepParameters[CPU_USAGE_ID].getValue() > this.maxOverallCPU) {
		// reason.setReason("Overall CPU Crunch");
		// return true;
		// }
		//
		// if(nsepParameters[MEMORY_USAGE_ID].getValue() > this.maxOverallMem) {
		// reason.setReason("Overall Memory Crunch");
		// return true;
		// }
		//
		// if(nsepControlStatus[APP_SESSION_COUNT_ID] == ALARMED) {
		// reason.setReason("App-session count reached overall limit");
		// return true;
		// } else if(nsepControlStatus[PROTOCOL_SESSION_COUNT_ID] == ALARMED) {
		// reason.setReason("Protocol-session count reached overall limit");
		// return true;
		// } else if(nsepControlStatus[RESPONSE_TIME_ID] == ALARMED) {
		// reason.setReason("Response time exceeded overall limit");
		// return true;
		// }
		//
		// if(nsepControlStatus[CPU_USAGE_ID] == ALARMED) {
		// if(nsepParameters[APP_SESSION_COUNT_ID].getValue() <= 0) {
		// return false;
		// } else {
		// reason.setReason("Overall CPU Crunch.");
		// return (parameters[APP_SESSION_COUNT_ID].getValue()/
		// nsepParameters[APP_SESSION_COUNT_ID].getValue()) < 0.1;
		// }
		// } else if(nsepControlStatus[MEMORY_USAGE_ID] == ALARMED) {
		// if(nsepParameters[APP_SESSION_COUNT_ID].getValue() <= 0) {
		// return false;
		// } else {
		// reason.setReason("Overall Memory Crunch.");
		// return (parameters[APP_SESSION_COUNT_ID].getValue()/
		// nsepParameters[APP_SESSION_COUNT_ID].getValue()) < 0.1;
		// }
		// }

		return false;
	}

	/**
	 * This method has been Updated for Congestion control change Returns false
	 * always as we have been checking Limit Reached on CPU and Memory ID. but
	 * actually checking is on their different level of congestion controls e.g
	 * if current value of cpu usage is equal to or larger than first level
	 * congestion control . then if first level alarm has not been raised then
	 * this alarm will be raised
	 * 
	 * @param id
	 *            Parameter ID
	 * @return A boolean value
	 */
	public boolean isLimitReached(int id) {
		// if((id == CPU_USAGE_ID) || (id == MEMORY_USAGE_ID)) {
		float uf = 1.0f;
		// if(engine.isCallPriorityEnabled()
		// && nsepParameters[APP_SESSION_COUNT_ID].getValue() != 0) {
		// uf = parameters[APP_SESSION_COUNT_ID].getValue()/
		// nsepParameters[APP_SESSION_COUNT_ID].getValue();
		// }
		// if (parameters[id].isLimitReached(uf)
		// && (controlStatus[id] == CLEARED)) {
		// controlStatus[id] = ALARMED;
		// return true;
		// }
		
		if (logger.isEnabledFor(Level.INFO)) {
		logger.info(" Checking isLimitReached ID " + id + " Value "
				+ parameters[id].getValue() + "Control Status "
				+ controlStatuses[id]);
	}
		
		if (id == NETWORK_TRANSACTIONS_PER_SECOND_ID|| id == AGGREGATED_TRANSACTIONS_PER_SECOND_ID || id== NEW_CALLS_PER_SECOND_ID) {

			 if (parameters[id].getValue()>parameters[id].getMaxLimit() && (!controlStatuses[id].get(0))) {
				 controlStatuses[id].set(0);
				 
				 if (logger.isEnabledFor(Level.INFO)) {
						logger.info("LimitReached For " + id + " Value "
								+ parameters[id].getValue() + "Control Status " + controlStatuses[id].get(0));
					}

			    return true;
			 }
		}

		/**
		 * handling for MEMORY_USAGE
		 */

		if (id == MEMORY_USAGE_ID) {
			
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info(" Checking isLimitReached ID " + id + " Value "
						+ parameters[id].getValue() + "Control Status "
						+ "Level 1 " + controlStatuses[id].get(0) + "Level 2 " + controlStatuses[id].get(1)
						+ "Level 3 " + controlStatuses[id].get(2));
			}

			if (parameters[id].getValue() >= parameters[CONTENTION_LEVEL_THREE_MEMORY_USAGE_ID]
					.getMaxLimit()

			) {

				if (!controlStatuses[id].get(2)) {
					if (memoryIgnoreCounter < confMemCounter){
						memoryIgnoreCounter++;	
						logger.error("Ignoring Memory Level 3 for current(conf) " + memoryIgnoreCounter + "(" + confMemCounter + ")");
						return false;
					}
					controlStatuses[id].set(2);
					fireLimitReached(CONTENTION_LEVEL_THREE_MEMORY_USAGE_ID,
							parameters[CONTENTION_LEVEL_THREE_MEMORY_USAGE_ID]
									.getValue());
				}


				return false;
			}

			if (parameters[id].getValue() >= parameters[CONTENTION_LEVEL_TWO_MEMORY_USAGE_ID]
					.getMaxLimit()) {

				if (controlStatuses[id].get(2)) {
					fireLimitCleared(CONTENTION_LEVEL_THREE_MEMORY_USAGE_ID,
							parameters[CONTENTION_LEVEL_TWO_MEMORY_USAGE_ID]
									.getValue());

				}

				if (!controlStatuses[id].get(1)) {
					if (memoryIgnoreCounter < confMemCounter){
						memoryIgnoreCounter++;	
						logger.error("Ignoring Memory Level 2 for current(conf) " + memoryIgnoreCounter + "(" + confMemCounter + ")");
						return false;
					}
					controlStatuses[id].set(1);
					fireLimitReached(CONTENTION_LEVEL_TWO_MEMORY_USAGE_ID,
							parameters[CONTENTION_LEVEL_TWO_MEMORY_USAGE_ID]
									.getValue());
				}

				return false;
			}

			if (parameters[id].getValue() >= parameters[CONTENTION_LEVEL_ONE_MEMORY_USAGE_ID]
					.getMaxLimit()// isLimitReached()
			) {

				if (controlStatuses[id].get(2)) {
					fireLimitCleared(CONTENTION_LEVEL_THREE_MEMORY_USAGE_ID,
							parameters[CONTENTION_LEVEL_ONE_MEMORY_USAGE_ID]
									.getValue());

				}

				if (controlStatuses[id].get(1)) {
					fireLimitCleared(CONTENTION_LEVEL_TWO_MEMORY_USAGE_ID,
							parameters[CONTENTION_LEVEL_ONE_MEMORY_USAGE_ID]
									.getValue());

				}

				if (!controlStatuses[id].get(0)) {
					if (memoryIgnoreCounter < confMemCounter){
						memoryIgnoreCounter++;	
						logger.error("Ignoring Memory Level 1 for current(conf) " + memoryIgnoreCounter + "(" + confMemCounter + ")");
						return false;
					}
					controlStatuses[id].set(0);
					fireLimitReached(CONTENTION_LEVEL_ONE_MEMORY_USAGE_ID,
							parameters[CONTENTION_LEVEL_ONE_MEMORY_USAGE_ID]
									.getValue());
				}

				return false;
			}

		}
		// Ends MEMORY_USAGE LIMIT

		/**
		 * handling for CPU_USAGE
		 */

		if (id == CPU_USAGE_ID) {
			
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info(" Checking isLimitReached ID " + id + " Value "
						+ parameters[id].getValue() + "Control Status "
						+ "Level 1 " + controlStatuses[id].get(0) + "Level 2 " + controlStatuses[id].get(1)
						+ "Level 3 " + controlStatuses[id].get(2));
			}

			if (parameters[id].getValue() >= parameters[CONTENTION_LEVEL_THREE_CPU_USAGE_ID]
					.getMaxLimit()) {

				if (!controlStatuses[id].get(2)){
					controlStatuses[id].set(2);
					fireLimitReached(CONTENTION_LEVEL_THREE_CPU_USAGE_ID,
							parameters[CONTENTION_LEVEL_THREE_CPU_USAGE_ID]
									.getValue());
					
				}

				return false;
			}

			if (parameters[id].getValue() >= parameters[CONTENTION_LEVEL_TWO_CPU_USAGE_ID]
					.getMaxLimit()) {

				if (controlStatuses[id].get(2)) {
					fireLimitCleared(CONTENTION_LEVEL_THREE_CPU_USAGE_ID,
							parameters[CONTENTION_LEVEL_TWO_CPU_USAGE_ID]
									.getValue());

				}
				
				if (!controlStatuses[id].get(1)) {
					fireLimitReached(CONTENTION_LEVEL_TWO_CPU_USAGE_ID,
							parameters[CONTENTION_LEVEL_TWO_CPU_USAGE_ID]
									.getValue());
					controlStatuses[id].set(1);
				}

				return false;
			}

			if (parameters[id].getValue() >= parameters[CONTENTION_LEVEL_ONE_CPU_USAGE_ID]
					.getMaxLimit()) {

				if (controlStatuses[id].get(2)) {
					fireLimitCleared(CONTENTION_LEVEL_THREE_CPU_USAGE_ID,
							parameters[CONTENTION_LEVEL_ONE_CPU_USAGE_ID]
									.getValue());

				}

				if (controlStatuses[id].get(1)) {
					fireLimitCleared(CONTENTION_LEVEL_TWO_CPU_USAGE_ID,
							parameters[CONTENTION_LEVEL_ONE_CPU_USAGE_ID]
									.getValue());

				}
				
				if (!controlStatuses[id].get(0)) {
					fireLimitReached(CONTENTION_LEVEL_ONE_CPU_USAGE_ID,
							parameters[CONTENTION_LEVEL_ONE_CPU_USAGE_ID]
									.getValue());
					controlStatuses[id].set(0);
				}

				return false;
			}

		}
		
		// Ends CPU_USAGE LIMIT

		return false;
	}

	/**
	 * This method has been Updated for Congestion control change Returns false
	 * always as we have been checking Limit cleared on CPU and Memory ID. but
	 * actually checking is on their different level of congestion controls e.g
	 * if current value of cpu usage is equal to or lesser than first level
	 * congestion control . then if first level limit reached alarm has been
	 * raised then this clearing alarm will be raised
	 * 
	 * @param id
	 *            Parameter ID
	 * @return A boolean value
	 */
	public boolean isLimitCleared(int id) {
		float uf = 1.0f;
		// if(engine.isCallPriorityEnabled()
		// && nsepParameters[APP_SESSION_COUNT_ID].getValue() != 0) {
		// uf = parameters[APP_SESSION_COUNT_ID].getValue()/
		// nsepParameters[APP_SESSION_COUNT_ID].getValue();
		// }
		// if (!parameters[id].isLimitReached(uf)
		// && (controlStatus[id] == ALARMED)) {
		// controlStatus[id] = CLEARED;
		// return true;
		// }
		
		if (logger.isEnabledFor(Level.INFO)) {
		logger.info(" Checking isLimitcleared ID " + id + " Value "
				+ parameters[id].getValue() + "Control Status "
				+ controlStatuses[id]);
	}

		if (id == NETWORK_TRANSACTIONS_PER_SECOND_ID|| id == AGGREGATED_TRANSACTIONS_PER_SECOND_ID || id == NEW_CALLS_PER_SECOND_ID){

			if (parameters[id].getValue() <= getClearingValue(id) &&(controlStatuses[id].get(0))) {
				 controlStatuses[id].clear(0);
				 
				 if (logger.isEnabledFor(Level.INFO)) {
						logger.info(" Limitcleared For ID " + id + " Value "
								+ parameters[id].getValue());
					}
			    return true;
			 }
		}
		
		/**
		 * handling for MEMORY_USAGE
		 */

		if (id == MEMORY_USAGE_ID) {
			
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info(" Checking isLimitcleared ID " + id + " Value "
						+ parameters[id].getValue() + "Control Status "
						+ "Level 1 " + controlStatuses[id].get(0) + "Level 2 " + controlStatuses[id].get(1)
						+ "Level 3 " + controlStatuses[id].get(2));
			}
			
			if (parameters[id].getValue() <= getClearingValue(CONTENTION_LEVEL_ONE_MEMORY_USAGE_ID)) {

				if (controlStatuses[id].get(0)) {
					fireLimitCleared(CONTENTION_LEVEL_ONE_MEMORY_USAGE_ID,
							parameters[CONTENTION_LEVEL_ONE_MEMORY_USAGE_ID]
									.getValue());
					controlStatuses[id].clear(0);
				}
				

				if (controlStatuses[id].get(2)) {
					fireLimitCleared(CONTENTION_LEVEL_THREE_MEMORY_USAGE_ID,
							parameters[CONTENTION_LEVEL_ONE_MEMORY_USAGE_ID]
									.getValue());
					controlStatuses[id].clear(2);

				}

				if (controlStatuses[id].get(1)) {
					fireLimitCleared(CONTENTION_LEVEL_TWO_MEMORY_USAGE_ID,
							parameters[CONTENTION_LEVEL_ONE_MEMORY_USAGE_ID]
									.getValue());
					controlStatuses[id].clear(1);

				}

				return false;
			}

			if (parameters[id].getValue() <= getClearingValue(CONTENTION_LEVEL_TWO_MEMORY_USAGE_ID)) {

				if (controlStatuses[id].get(1)) {
					fireLimitCleared(CONTENTION_LEVEL_TWO_MEMORY_USAGE_ID,
							parameters[CONTENTION_LEVEL_TWO_MEMORY_USAGE_ID]
									.getValue());
					controlStatuses[id].clear(1);

				}


				if (controlStatuses[id].get(2)) {
					fireLimitCleared(CONTENTION_LEVEL_THREE_MEMORY_USAGE_ID,
							parameters[CONTENTION_LEVEL_TWO_MEMORY_USAGE_ID]
									.getValue());
					controlStatuses[id].clear(2);

				}


				return false;
			}

			if (parameters[id].getValue() <= getClearingValue(CONTENTION_LEVEL_THREE_MEMORY_USAGE_ID)) {

				if (controlStatuses[id].get(2)) {
					fireLimitCleared(CONTENTION_LEVEL_THREE_MEMORY_USAGE_ID,
							parameters[CONTENTION_LEVEL_THREE_MEMORY_USAGE_ID]
									.getValue());
					controlStatuses[id].clear(2);

				}

				return false;
			}
			// Ends MEMORY_USAGE LIMIT
		}
		/**
		 * handling for CPU_USAGE
		 */

		if (id == CPU_USAGE_ID) {
			
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info(" Checking isLimitcleared ID " + id + " Value "
						+ parameters[id].getValue() + "Control Status "
						+ "Level 1 " + controlStatuses[id].get(0) + "Level 2 " + controlStatuses[id].get(1)
						+ "Level 3 " + controlStatuses[id].get(2));
			}
			
			if (parameters[id].getValue() <= getClearingValue(CONTENTION_LEVEL_ONE_CPU_USAGE_ID)) {

				if (controlStatuses[id].get(0)) {
					fireLimitCleared(CONTENTION_LEVEL_ONE_CPU_USAGE_ID,
							parameters[CONTENTION_LEVEL_ONE_CPU_USAGE_ID]
									.getValue());
					controlStatuses[id].clear(0);
				}
				

				if (controlStatuses[id].get(2)) {
					fireLimitCleared(CONTENTION_LEVEL_THREE_CPU_USAGE_ID,
							parameters[CONTENTION_LEVEL_ONE_CPU_USAGE_ID]
									.getValue());
					controlStatuses[id].clear(2);
				}

				if (controlStatuses[id].get(1)) {
					fireLimitCleared(CONTENTION_LEVEL_TWO_CPU_USAGE_ID,
							parameters[CONTENTION_LEVEL_ONE_CPU_USAGE_ID]
									.getValue());
					controlStatuses[id].clear(1);
				}
				

				return false;
			}

			if (parameters[id].getValue() <= getClearingValue(CONTENTION_LEVEL_TWO_CPU_USAGE_ID)) {

				if (controlStatuses[id].get(2)) {
					fireLimitCleared(CONTENTION_LEVEL_THREE_CPU_USAGE_ID,
							parameters[CONTENTION_LEVEL_TWO_CPU_USAGE_ID]
									.getValue());
					controlStatuses[id].clear(2);

				}

				if (controlStatuses[id].get(1)) {
					fireLimitCleared(CONTENTION_LEVEL_TWO_CPU_USAGE_ID,
							parameters[CONTENTION_LEVEL_TWO_CPU_USAGE_ID]
									.getValue());
					controlStatuses[id].clear(1);
				}
				

				return false;
			}

			if (parameters[id].getValue() <= getClearingValue(CONTENTION_LEVEL_THREE_CPU_USAGE_ID)) {

				if (controlStatuses[id].get(2)) {
					fireLimitCleared(CONTENTION_LEVEL_THREE_CPU_USAGE_ID,
							parameters[CONTENTION_LEVEL_THREE_CPU_USAGE_ID]
									.getValue());
					controlStatuses[id].clear(2);
				}

				return false;
			}

		}
		// Ends CPU_USAGE LIMIT

		return false;
	}

	/**
	 * Congestion control change
	 * 
	 * @param id
	 * @return
	 */
	private float getClearingValue(int id) {

		return (1 - this.ocmAlarmHysteresis) * parameters[id].getMaxLimit();
	}

	/**
	 * updated Congestion control change Creates a BitSet that presents the
	 * current overload status. Parameter ID is used as bits position. If a bit
	 * is set, it means that paramert is alarmed.
	 * 
	 * @return A BitSet value
	 */

	// 
	private BitSet getParameterStatus() {
		BitSet parameterStatus = new BitSet();
		
		for (int i = 0; i < controlStatuses.length; i++) {
			if (controlStatuses[i].get(0) || controlStatuses[i].get(1)
					|| controlStatuses[i].get(2)) {
				parameterStatus.set(i);
			}
		}
		
//		for (int i = 0; i < controlStatus.length; i++) {
//			if (controlStatus[i] == ALARMED) {
//				parameterStatus.set(i);
//			}
//
//			if ((controlStatus[i] == LEVEL1_ALARMED)
//					|| (controlStatus[i] == LEVEL2_ALARMED)
//					|| (controlStatus[i] == LEVEL3_ALARMED)) {
//				parameterStatus.set(i);
//			}
//		}
		return parameterStatus;
	}

	private void fireLimitReached(int id, int value) {
		OverloadParameter param = (id >= 0 && id < parameters.length) ? parameters[id]
				: null;
		OverloadEvent event = new CountOverloadEvent(this, param,
				getParameterStatus(), value);
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("OCM: parameter(id=" + id + ") is reach the limit");
			logger.info(this.toString());
		}
		fireLimitReached(event);
	}

	private void fireLimitReached(int id, float value) {
		OverloadParameter param = (id >= 0 && id < parameters.length) ? parameters[id]
				: null;
		OverloadEvent event = new PercentageOverloadEvent(this, param,
				getParameterStatus(), value);
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("OCM: parameter(id=" + id + ") is reach the limit");
			logger.info(this.toString());
		}
		fireLimitReached(event);
	}

	private void fireLimitReached(OverloadEvent event) {
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			((OverloadListener) i.next()).maxLimitReached(event);
		}
	}

	private void fireLimitCleared(int id, int value) {
		OverloadParameter param = (id >= 0 && id < parameters.length) ? parameters[id]
				: null;
		OverloadEvent event = new CountOverloadEvent(this, param,
				getParameterStatus(), value);
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("OCM: parameter(id=" + id + ") is back to Value = "
					+ value);
			logger.info(this.toString());
		}
		fireLimitCleared(event);
	}

	private void fireLimitCleared(int id, float value) {
		OverloadParameter param = (id >= 0 && id < parameters.length) ? parameters[id]
				: null;
		OverloadEvent event = new PercentageOverloadEvent(this, param,
				getParameterStatus(), value);
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("OCM: parameter(id=" + id + ") is back to Value = "
					+ value);
			logger.info(this.toString());
		}
		fireLimitCleared(event);
	}

	private void fireLimitCleared(OverloadEvent event) {
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			((OverloadListener) i.next()).maxLimitCleared(event);
		}
	}

	private void fireOlfChanged(float olf, ArrayList listeners) {
		OlfOverloadEvent event = new OlfOverloadEvent(this, null,
				getParameterStatus(), olf);
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("OCM: OLF threshold is passed");
			logger.info(this.toString());
		}

		for (Iterator i = listeners.iterator(); i.hasNext();) {
			((OverloadListener) i.next()).olfChanged(event);
		}
	}

	private void fireControlStateChanged() {
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("OCM: Control state is changed to " + this.enabled);
		}
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			((OverloadListener) i.next()).controlStateChanged(this.enabled);
		}
	}

	/*************** start NSEP specific methods ************************/
	/**
	 * Creates a BitSet that presents the current overload status. Parameter ID
	 * is used as bits position. If a bit is set, it means that paramert is
	 * alarmed.
	 * 
	 * @return A BitSet value
	 */
	private BitSet getNSEPParameterStatus() {
		BitSet parameterStatus = new BitSet();
		for (int i = 0; i < nsepControlStatus.length; i++) {
			if (nsepControlStatus[i] == ALARMED) {
				parameterStatus.set(i);
			}
		}
		return parameterStatus;
	}

	private void fireNSEPLimitReached(int id, int value) {
		OverloadParameter param = (id >= 0 && id < nsepParameters.length) ? nsepParameters[id]
				: null;
		OverloadEvent event = new CountOverloadEvent(this, param,
				getNSEPParameterStatus(), value);
		if (logger.isEnabledFor(Level.INFO)) {
			logger
					.info("NSEP OCM: parameter(id=" + id
							+ ") is reach the limit");
		}
		fireNSEPLimitReached(event);
	}

	private void fireNSEPLimitReached(int id, float value) {
		OverloadParameter param = (id >= 0 && id < nsepParameters.length) ? nsepParameters[id]
				: null;
		OverloadEvent event = new PercentageOverloadEvent(this, param,
				getNSEPParameterStatus(), value);
		if (logger.isEnabledFor(Level.INFO)) {
			logger
					.info("NSEP OCM: parameter(id=" + id
							+ ") is reach the limit");
		}
		fireNSEPLimitReached(event);
	}

	private void fireNSEPLimitReached(OverloadEvent event) {
		for (Iterator i = nsepListeners.iterator(); i.hasNext();) {
			((OverloadListener) i.next()).maxLimitReached(event);
		}
	}

	private void fireNSEPLimitCleared(int id, int value) {
		OverloadParameter param = (id >= 0 && id < nsepParameters.length) ? nsepParameters[id]
				: null;
		OverloadEvent event = new CountOverloadEvent(this, param,
				getNSEPParameterStatus(), value);
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("NSEP OCM: parameter(id=" + id
					+ ") is back to Value = " + value);
		}
		fireNSEPLimitCleared(event);
	}

	private void fireNSEPLimitCleared(int id, float value) {
		OverloadParameter param = (id >= 0 && id < nsepParameters.length) ? nsepParameters[id]
				: null;
		OverloadEvent event = new PercentageOverloadEvent(this, param,
				getNSEPParameterStatus(), value);
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("NSEP OCM: parameter(id=" + id
					+ ") is back to Value = " + value);
		}
		fireNSEPLimitCleared(event);
	}

	private void fireNSEPLimitCleared(OverloadEvent event) {
		for (Iterator i = nsepListeners.iterator(); i.hasNext();) {
			((OverloadListener) i.next()).maxLimitCleared(event);
		}
	}

	public int getNSEPParameterId(String name) {
		for (int i = 0; i < nsepParameters.length; i++) {
			if ((nsepParameters[i] != null)
					&& nsepParameters[i].getName().equals(name)) {
				return nsepParameters[i].getId();
			}
		}
		return -1;
	}

	/**
	 * This method is called by parameter owner to report a value increase for
	 * NSEP call. If no increased amount presents, 1 is assumed.
	 * 
	 * @param id
	 *            Parameter ID
	 */
	public void increaseNSEP(int id) {
		
		increaseNSEP(id, false);
	}
	
	/**
	 * This method is called by parameter owner to report a value increase for
	 * NSEP call. If no increased amount presents, 1 is assumed.
	 * 
	 * @param id   Parameter ID
	 * @param incremnentSipOverLaodCtr incremnts ctr in AseSipOverload used for appsession cnt
	 */
	public void increaseNSEP(int id,  boolean incremnentSipOverLaodCtr) {
		if (id == -1)
			return;
		increaseNSEP(id, 1, incremnentSipOverLaodCtr);
	}

	/**
	 * @see increaseNSEP(int id)
	 */
	public synchronized void increaseNSEP(int id, int amount,boolean incremnentSipOverLaodCtr) {
		nsepParameters[id].increase(amount);
		int value = (int) nsepParameters[id].getValue();
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("NSEP OCM: parameter(" + id + ") is increased to "
					+ value);
		}
		if (!enabled) {
			return;
		}
		if (!isNSEPLimitReached(id)) {
			return;
		}
		fireNSEPLimitReached(id, value);
	}

	/**
	 * @see increaseNSEP(int id)
	 */
	public synchronized void increaseNSEP(int id, float amount) {
		nsepParameters[id].increase(amount);
		float value = nsepParameters[id].getValue();
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("NSEP OCM: parameter(" + id + ") is increased to "
					+ value);
		}
		if (!enabled) {
			return;
		}
		if (!isNSEPLimitReached(id)) {
			return;
		}
		fireNSEPLimitReached(id, value);
	}

	/**
	 * This method is called by NSEP parameter owner to report a value decrease.
	 * If no decreased amount presents, 1 is assumed.
	 * 
	 * @param id
	 *            Parameter ID
	 */
	public void decreaseNSEP(int id) {
		if (id == -1)
			return;
		decreaseNSEP(id, 1);
	}

	/**
	 * @see decreaseNSEP(int id)
	 */
	public synchronized void decreaseNSEP(int id, int amount) {
		nsepParameters[id].decrease(amount);
		int value = (int) nsepParameters[id].getValue();
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("NSEP OCM: parameter(" + id + ") is decreased to "
					+ value);
		}
		if (!enabled) {
			return;
		}
		if (!isNSEPLimitCleared(id)) {
			return;
		}
		fireNSEPLimitCleared(id, value);
	}

	/**
	 * @see decreaseNSEP(int id)
	 */
	public synchronized void decreaseNSEP(int id, float amount) {
		nsepParameters[id].decrease(amount);
		float value = nsepParameters[id].getValue();
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("NSEP OCM: parameter(" + id + ") is decreased to "
					+ value);
		}
		if (!enabled) {
			return;
		}
		if (!isNSEPLimitCleared(id)) {
			return;
		}
		fireNSEPLimitCleared(id, value);
	}

	/**
	 * Tells if the given parameter value will exceed limits.
	 */
	public boolean doesExceedLimit(int id, float value) {
		if (id == -1)
			return false;
		return parameters[id].doesExceedLimit(value);
	}

	/**
	 * Tells if the given parameter value will exceed NSEP limits.
	 */
	public boolean doesExceedNSEPLimit(int id, float value) {
		if (id == -1)
			return false;
		return nsepParameters[id].doesExceedLimit(value);
	}

	/**
	 * Returns true only if the indicated parameter is in cleared status and its
	 * current value is equal or large than its maximum. Otherwise, returns
	 * false. Its status is then set to alarmed
	 * 
	 * @param id
	 *            Parameter ID
	 * @return A boolean value
	 */
	private boolean isNSEPLimitReached(int id) {
		if (nsepParameters[id].isLimitReached()
				&& (nsepControlStatus[id] == CLEARED)) {
			nsepControlStatus[id] = ALARMED;
			return true;
		}
		return false;
	}

	/**
	 * Returns true only if the indicated parameter is in alarmed status and its
	 * current value is less than its maximum. Otherwise, returns false. Its
	 * status is then set to cleared
	 * 
	 * @param id
	 *            Parameter ID
	 * @return A boolean value
	 */
	private boolean isNSEPLimitCleared(int id) {
		if (nsepParameters[id].isLimitCleared()
				&& (nsepControlStatus[id] == ALARMED)) {
			nsepControlStatus[id] = CLEARED;
			return true;
		}
		return false;
	}

	/**
	 * Calculates OLF using the formular OLF = p1/p1max*w1 + p2/p2max*w2 + ... +
	 * pn/pnmax*wn.
	 * 
	 * @return OLF
	 */
	private float calculateNSEPOlf() {
		float olf = 0f;
		for (int i = 0; i < nsepParameters.length; i++) {
			if (nsepParameters[i] != null) {
				olf += nsepParameters[i].getOlf();
			}
		}
		return olf;
	}

	private void fireNSEPOlfChanged(float olf, ArrayList listeners) {
		OlfOverloadEvent event = new OlfOverloadEvent(this, null,
				getNSEPParameterStatus(), olf);
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("NSEP OCM: OLF threshod is passed for Priority calls");
		}
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			((OverloadListener) i.next()).olfChanged(event);
		}
	}

	/*************** ends NSEP specific methods ************************/

	/**
	 * Changes the Component State to the state indicated by the argument
	 * passed. The states are changed according to the priority values.
	 **/
	public void changeState(MComponentState componentState)
			throws UnableToChangeStateException {
		try {
			if (componentState.getValue() == MComponentState.LOADED) {
				this.init(propertyfile);
			} else if (componentState.getValue() == MComponentState.RUNNING) {
				if (enabled && cpuScanEnabled) {
					CPU.getInstance().setEnable(enabled);
				}
			} else if (componentState.getValue() == MComponentState.STOPPED) {
				if (cpuScanEnabled && CPU.isInitialized()) {
					CPU.getInstance().setEnable(false);
				}
			}
		} catch (Exception e) {
			throw new UnableToChangeStateException(e.getMessage());
		}
	}

	/**
	 * Updates the configuration parameters of the component as specified in the
	 * Pair array
	 **/
	public void updateConfiguration(Pair[] configData, OperationType opType)
			throws UnableToUpdateConfigException {
		for (int i = 0; i < configData.length; i++) {
			// Extract the parameter name and value.
			String name = (String) configData[i].getFirst();
			String value = (String) configData[i].getSecond();
			if (name.equals(Constants.OID_ENABLE_OCM)) {
				if (value != null) {
					setEnabled(value.equals("1") ? true : false);
				}
			} else if (name.equals(Constants.OID_NORMAL_MAX_CPU)) {
				if (value != null) {
					setLimit(CPU_USAGE, Float.parseFloat(value));
				}
			}
			// else if (name.equals(Constants.OID_MAX_PROTOCOL_SESSIONS)) {
			// if (value != null) {
			// setLimit(PROTOCOL_SESSION_COUNT, Integer.parseInt(value));
			// }
			// } else if (name.equals(Constants.OID_MAX_APPLICATION_SESSIONS)) {
			// if (value != null) {
			// setLimit(APP_SESSION_COUNT, Integer.parseInt(value));
			// }
			// }
			else if (name.equals(Constants.OID_CPU_SCAN_INTERVAL_IN_SECS)) {
				if (value != null) {
					setScanInterval(Long.parseLong(value));
				}
			} else if (name.equals(Constants.NSEP_MAX_CPU)) {
				if (value != null) {
					setNSEPLimit(CPU_USAGE, Float.parseFloat(value));
					this.maxOverallCPU = Float.parseFloat(value) * 1.1f;
					logger.error("Max CPU = " + this.maxOverallCPU);
					if (this.maxOverallCPU > 90.0f) {
						this.maxOverallCPU = 90.0f;
					}
				}
			}

			/*
			 * Congestion control change
			 */
			if (name.equals(Constants.OID_CONTENTION_LEVEL_ONE_CPU_USAGE)) {
				if (value != null)
					this.setLimit(CONTENTION_LEVEL_ONE_CPU_USAGE, Float
							.parseFloat(value));

			}

			if (name.equals(Constants.OID_CONTENTION_LEVEL_TWO_CPU_USAGE)) {
				if (value != null)
					this.setLimit(CONTENTION_LEVEL_TWO_CPU_USAGE, Float
							.parseFloat(value));

			}

			if (name.equals(Constants.OID_CONTENTION_LEVEL_THREE_CPU_USAGE)) {
				if (value != null)
					this.setLimit(CONTENTION_LEVEL_THREE_MEMORY_USAGE, Float
							.parseFloat(value));

			}

			if (name.equals(Constants.OID_CONTENTION_LEVEL_ONE_MEMORY_USAGE)) {
				if (value != null)
					this.setLimit(CONTENTION_LEVEL_ONE_MEMORY_USAGE, Integer
							.parseInt(value));

			}

			if (name.equals(Constants.OID_CONTENTION_LEVEL_TWO_MEMORY_USAGE)) {
				if (value != null)
					this.setLimit(CONTENTION_LEVEL_TWO_MEMORY_USAGE, Integer
							.parseInt(value));

			}

			if (name.equals(Constants.OID_CONTENTION_LEVEL_THREE_MEMORY_USAGE)) {
				if (value != null) {
					this.setLimit(CONTENTION_LEVEL_THREE_MEMORY_USAGE, Integer
							.parseInt(value));

				}
				//
				// else if (name.equals(Constants.NSEP_MAX_PROTOCOL_SESSIONS)) {
				// if (value != null) {
				// setNSEPLimit(PROTOCOL_SESSION_COUNT,
				// Integer.parseInt(value));
				// }
				// } else if
				// (name.equals(Constants.NSEP_MAX_APPLICATION_SESSIONS)) {
				// if (value != null) {
				// setNSEPLimit(APP_SESSION_COUNT, Integer.parseInt(value));
				// }
				// }

				if (name.equals(Constants.NSEP_MAX_MEMORY)) {
					if (value != null) {
						setNSEPLimit(MEMORY_USAGE, Integer.parseInt(value));
						this.maxOverallMem = Integer.parseInt(value) * 1.1f;
						logger.error("Max Memory = " + this.maxOverallMem);
						if (this.maxOverallMem > 90.0f) {
							this.maxOverallMem = 90.0f;
						}
					}
				}

			}

			if (logger.isEnabledFor(Level.INFO)) {
				logger.info(this.toString());
			}
		}
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("OCM: \n");
		buf.append("OCM enabled: " + enabled + "\n");
		buf.append("CPU scan interval: " + scanInterval + "\n");
		buf.append("Control parameters:\n");
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] != null) {
				buf.append("\t" + parameters[i].toString() + "\n");
			}
		}
		buf.append("Control status:\n");
		for (int i = 0; i < controlStatuses.length; i++) {
			if (parameters[i] != null) {
				if (controlStatuses[i].get(2)){
					buf.append("\t" + parameters[i].getName() + ": "
							+ "LEVEL3_ALARMED" + "\n");
				}else if(controlStatuses[i].get(1)){
					buf.append("\t" + parameters[i].getName() + ": "
							+ "LEVEL2_ALARMED" + "\n");
				}else if(controlStatuses[i].get(1)){
					buf.append("\t" + parameters[i].getName() + ": "
							+ "LEVEL1_ALARMED" + "\n");
				}else{
					buf.append("\t" + parameters[i].getName() + ": "
							+ "NOT_ALARMED" + "\n");
				}
			}
		}
		
//		for (int i = 0; i < controlStatus.length; i++) {
//			if (parameters[i] != null) {
//				buf.append("\t" + parameters[i].getName() + ": "
//						+ controlStatus[i] + "\n");
//			}
//		}
		if (engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
			buf.append("NSEP Control status:\n");
			for (int i = 0; i < nsepControlStatus.length; i++) {
				if (nsepParameters[i] != null) {
					buf.append("\t" + nsepParameters[i].getName() + ": "
							+ nsepControlStatus[i] + "\n");
				}
			}
		}
		return buf.toString();
	}

	/**
	 * Load configuration from server-ocm.xml
	 * 
	 * @param filename
	 *            XML file name
	 */
	public void loadConfig(String filename, boolean priority) {
		if (logger.isDebugEnabled()) {
			logger.debug("loading configuration from " + filename);
		}
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			SAXParser saxParser = factory.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();
			xmlReader.setContentHandler(new ConfigHandler(priority));
			xmlReader.parse((new File(filename)).toString());
		} catch (Exception ex) {
			logger.error("OCM: " + ex, ex);
		}
	}

	/**
	 * @author Dana
	 * 
	 *         This class provides document handling methods for XML parser
	 */
	private class ConfigHandler extends DefaultHandler {
		private TreeMap params = new TreeMap();
		private boolean isPriorityInstance;

		public ConfigHandler(boolean priority) {
			this.isPriorityInstance = priority;
		}

		public void startDocument() throws SAXException {
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("OCM: loading ...");
			}
		}

		public void startElement(String namespaceURI, String localName,
				String qName, Attributes atts) throws SAXException {
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info(localName + ":");
			}
			if (localName.equals("parameter")) {
				Integer id = null;
				String name = null;
				float weight = 0;
				String type = null;
				String max = null;
				for (int i = 0; i < atts.getLength(); i++) {
					if (atts.getLocalName(i).equals("id")) {
						id = new Integer(atts.getValue(i));
					} else if (atts.getLocalName(i).equals("name")) {
						name = atts.getValue(i);
					} else if (atts.getLocalName(i).equals("type")) {
						type = atts.getValue(i);
					} else if (atts.getLocalName(i).equals("weight")) {
						weight = Float.parseFloat(atts.getValue(i));
					} else if (atts.getLocalName(i).equals("max")) {
						max = atts.getValue(i);
					}
				}
				OverloadParameter param = null;
				if (name.equals("Response Time")) {
					param = new ResponseTime(id.intValue(), name,
							OverloadParameter.Type.RESPONSE_TIME);
				} else {
					OverloadParameter.Type paramType = PARAM_NAME_TYPE_MAP
							.get(name);
					param = new OverloadParameter(id.intValue(), name,
							paramType);
					if (type.equals("int")) {
						param.setLimit(Integer.parseInt(max));
					} else if (type.equals("float")) {
						param.setLimit(Float.parseFloat(max));
						;
					}
				}
				param.setWeight(weight);
				param.setValue(0);
				params.put(id, param);
			} else if (localName.equals("interval")) {
				scanInterval = Long.parseLong(atts.getValue(0)) * 1000;
			} else if (localName.equals("enable")) {
				enabled = atts.getValue(0).equals(AseStrings.TRUE_SMALL) ? true : false;
			} else if (localName.equals("sip-white-list")) {

				ArrayList list = isPriorityInstance ? nsepWhiteList
						: ocmWhiteList;
				String configFileName = isPriorityInstance ? nsepPropertyFile
						: propertyfile;
				String appName = isPriorityInstance ? "nsepOcm" : "ocm";

				if (logger.isDebugEnabled()) {
					logger.debug("Parsing the OCM Config file for White List:"
							+ configFileName);
				}

				// Parse the rules from the white list and generate rules.
				try {
					FileInputStream ins = new FileInputStream(configFileName);
					RulesRepository repository = (RulesRepository) Registry
							.lookup(Constants.RULES_REPOSITORY);
					Collection rules = repository.generateRules(ins, appName);
					list.addAll((Collection<Rule>) rules);

					ins.close();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}

				if (logger.isDebugEnabled()) {
					logger.debug("Parsed the OCM Config file for White List:"
							+ list.size());
				}

			}
			if (logger.isEnabledFor(Level.INFO)) {
				if (atts != null) {
					for (int i = 0; i < atts.getLength(); i++) {
						logger.info(" " + atts.getLocalName(i) + "="
								+ atts.getValue(i));
					}
				}
				logger.info("\n");
			}
		}

		public void endDocument() throws SAXException {
			// Since id may not be continuous, we need to find an array length
			// that is equal to the largest id
			int length = params.size();
			if (((Integer) params.lastKey()).intValue() > length) {
				length = ((Integer) params.lastKey()).intValue();
			}
			if (this.isPriorityInstance) {
				nsepParameters = new OverloadParameter[length];
				for (Iterator i = params.entrySet().iterator(); i.hasNext();) {
					Map.Entry entry = (Map.Entry) i.next();
					int index = ((Integer) entry.getKey()).intValue();
					nsepParameters[index] = (OverloadParameter) entry
							.getValue();
				}
				// Initialize control status for each NSEP paramerter
				nsepControlStatus = new int[nsepParameters.length];
			} else {
				parameters = new OverloadParameter[length];
				for (Iterator i = params.entrySet().iterator(); i.hasNext();) {
					Map.Entry entry = (Map.Entry) i.next();
					int index = ((Integer) entry.getKey()).intValue();
					parameters[index] = (OverloadParameter) entry.getValue();
				}
				// Initialize control status for each paramerter
//				controlStatus = new int[parameters.length];
				controlStatuses = new BitSet[parameters.length];
				for (int i = 0; i < controlStatuses.length ; i++){
					controlStatuses[i] = new BitSet(2);
				}
			}

		}
	}

	private void registerForBackgroundProcess() {
		if (this.isEnabled()) {
//			ConfigRepository config = (ConfigRepository) Registry
//					.lookup(Constants.NAME_CONFIG_REPOSITORY);
			try {
				this.ocmDumpDuration = (long) Long.parseLong(config
						.getValue(Constants.DUR_OCM_INFO));
			} catch (Exception e) {
				this.ocmDumpDuration = 0;
			}
			try {
				this.headerDumpFreq = (int) Integer.parseInt(config
						.getValue(Constants.FREQ_HEADER_DUMP));
			} catch (Exception e) {
				this.headerDumpFreq = 1000;
			}
			long scanInterval = this.getScanInterval();
			if (this.ocmDumpDuration * 1000 < scanInterval) {
				this.ocmDumpFreq = 1;
			} else {
				this.ocmDumpFreq = (this.ocmDumpDuration * 1000)
						/ this.getScanInterval();
			}
			try {
				AseBackgroundProcessor processor = (AseBackgroundProcessor) Registry
						.lookup(Constants.BKG_PROCESSOR);
				processor.registerBackgroundListener(this, scanInterval / 1000);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			if (logger.isEnabledFor(Level.INFO)) {
				logger
						.info("OCM is not enabled: Not registering OCM for Backgrond peocess");
			}
		}
	}

	public void process(long currentTime) {
		
		logger.debug("OverLoadControlManager: Inside process()");
		
		CPU.getInstance().monitorLoad();
		
		logger.debug("Called monitorLoad()");
		
		if (this.ocmDumpDuration != 0) {
			if (dumpCount % this.ocmDumpFreq == 0) {
				logger.debug(" Doing Scanning for CPU and MEMORY");
				if (dumpHeader % headerDumpFreq == 0) {
					logger.error("OCMP: CPU,MEMORY");
				}
				CPU _instance = CPU.getInstance();
				float cpu = _instance.getCurrentCPUUsage();
				float memory = _instance.getCurrentMemoryUsage();
				
				logger.error("OCMP:" + cpu + "," + memory +" Max Memory:  "+_instance.getMaxMemory()+" Used Heap "+ _instance.getHeapMemoryUsed());
				dumpHeader++;
			}

			dumpCount++;
		}
	}

	public static class OverloadReason {
		String m_reason = null;

		public void setReason(String reason) {
			m_reason = reason;
		}

		public String toString() {
			return m_reason;
		}
	}

	public boolean matchesWhiteListEntry(SasMessage message, boolean priority) {
		if (message == null)
			return false;

		boolean matches = false;
		ArrayList<Rule> rules = priority ? nsepWhiteList : ocmWhiteList;
		for (Rule rule : rules) {
			String[] orderedInputStrings = rule.getInputData(message);
			ArrayList orderedInputParameterData = rule
					.getInputParameterData(message);
			if (orderedInputStrings == null
					&& orderedInputParameterData == null)
				continue;
			if (rule.evaluate(orderedInputStrings, orderedInputParameterData)) {
				matches = true;
				break;
			}
		}
		return matches;
	}
}
