/*******************************************************************************
 *   Copyright (c) 2011 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/

package com.baypackets.ase.measurement;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import com.agnity.oems.agent.messagebus.meascounters.MeasMgr;
import com.agnity.oems.agent.messagebus.meascounters.MeasurementListener;
import com.agnity.oems.agent.messagebus.meascounters.threshold.ThresholdMgr;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseEngine;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.InitializationFailedException;
import com.baypackets.bayprocessor.slee.meascounters.SleeMeasurementCounter;

/**
 * TO handle the reposne got from EMS
 * 
 * @author Madhukar
 *
 */
public class MeasurementThread implements Runnable {

	private static Logger logger = Logger.getLogger(MeasurementThread.class);
	private MeasMgr measMgr;
	private ThresholdMgr mfwThresholdMgr;

	private ServiceMeasurementManager defaultMeasurementManager;
	private String MEASUREMENT_CONFIG;
	private String THRESHOLD_CONFIG;
	private ClassLoader classLoader;
	private ConfigRepository configRep;

	private Map<String, HashMap<String, SleeMeasurementCounter>> sleeMeasurementCounterMap;
	private int minDefaultAccIntervalForSets;

	private boolean writeToFile;

	private AseMeasurementLogger fileLogger;

	private ConfigRepository m_configRepository;
	private String measurementConfTransId;

	public MeasurementThread(MeasMgr measMgr, ThresholdMgr mfwThresholdMgr,
			ServiceMeasurementManager defaultMeasurementManager, String MEASUREMENT_CONFIG, String THRESHOLD_CONFIG,
			ClassLoader classLoader, ConfigRepository configRep,
			Map<String, HashMap<String, SleeMeasurementCounter>> sleeMeasurementCounterMap,
			int minDefaultAccIntervalForSets, boolean writeToFile, AseMeasurementLogger fileLogger,
			ConfigRepository m_configRepository, String measurementConfTransId

	) {
		logger.info("INIT MeasurementThread  measMgr" + measMgr + "fwThresholdMgr: " + mfwThresholdMgr
				+ " defaultMeasurementManager:" + defaultMeasurementManager + " MEASUREMENT_CONFIG:"
				+ MEASUREMENT_CONFIG + " THRESHOLD_CONFIG: " + THRESHOLD_CONFIG + " classLoader:" + classLoader
				+ " configRep:" + configRep + " sleeMeasurementCounterMap:" + sleeMeasurementCounterMap
				+ " minDefaultAccIntervalForSets:" + minDefaultAccIntervalForSets + " writeToFile:" + writeToFile
				+ " fileLogger:" + fileLogger + " m_configRepository:" + m_configRepository + " measurementConfTransId:"
				+ measurementConfTransId);
		this.measMgr = measMgr;
		this.mfwThresholdMgr = mfwThresholdMgr;
		this.defaultMeasurementManager = defaultMeasurementManager;
		this.MEASUREMENT_CONFIG = MEASUREMENT_CONFIG;
		this.THRESHOLD_CONFIG = THRESHOLD_CONFIG;
		this.classLoader = classLoader;
		this.configRep = configRep;
		this.sleeMeasurementCounterMap = sleeMeasurementCounterMap;
		this.minDefaultAccIntervalForSets = minDefaultAccIntervalForSets;
		this.writeToFile = writeToFile;
		this.fileLogger = fileLogger;
		this.m_configRepository = m_configRepository;
		this.measurementConfTransId = measurementConfTransId;

	}

	public MeasurementThread() {
		super();
	}

	@Override
	public void run() {

		
		logger.info("starting load properties file" + Constants.ASE_HOME + "/"
				+ Constants.FILE_CAS_STARUP_PROPERTIES);

		String filepath = Constants.ASE_HOME + "/"
				+ Constants.FILE_CAS_STARUP_PROPERTIES;
		
		Integer counter = 2;
		Long sleepTime = 1000l;
		try {
			InputStream in = Files.newInputStream(Paths.get(filepath));
			Yaml yaml = new Yaml();
			TreeMap<String, String> oemsAgentProperties = yaml.loadAs(in, TreeMap.class);

			 counter = Integer.parseInt(oemsAgentProperties.get("mset.wait.counter"));
			 sleepTime = Long.parseLong(oemsAgentProperties.get("mset.sleep.time.ms"));
			if (logger.isDebugEnabled()) {
				logger.debug(
						"MeasurementThread started to check if response of fetch measurement got of not for ASE meas manager");

			}
		} catch (Exception e) {
			logger.error("exception occured while reading mset properties");
		}
		
        logger.info("counter :"+ counter + " sleepTime:"+ sleepTime );
		int countTest = 0;
		while (AseMeasurementManager.msetAndThresXmlResposneFromEMS == null && countTest < counter) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			countTest = countTest + 1;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("After  " + countTest*sleepTime + "  mili second got msetAndThresXmlResposneFromEMS :"
					+ AseMeasurementManager.msetAndThresXmlResposneFromEMS);

		}
		String transectionIdOfGetConfigRequest = AseMeasurementManager.transectionIdOfGetConfigRequest;
		if (logger.isDebugEnabled()) {
			logger.debug("transection id got in response =" + transectionIdOfGetConfigRequest
					+ " transectionId in request " + measurementConfTransId);
		}
		if (transectionIdOfGetConfigRequest != null && AseMeasurementManager.msetAndThresXmlResposneFromEMS != null
				&& !AseMeasurementManager.msetAndThresXmlResposneFromEMS.equals("")
				&& transectionIdOfGetConfigRequest.equals(measurementConfTransId)) {
			this.measMgr.loadEmsCfgInitData(AseMeasurementManager.msetAndThresXmlResposneFromEMS);
			try {
				this.mfwThresholdMgr.configureFromEMS(AseMeasurementManager.msetAndThresXmlResposneFromEMS);
			} catch (InitializationFailedException e) {
				logger.error("errorcoccured while configuring mset from EMS");
			}
		}
		// resetting values
		AseMeasurementManager.transectionIdOfGetConfigRequest = null;
		AseMeasurementManager.msetAndThresXmlResposneFromEMS = null;
		try {
			this.defaultMeasurementManager.initialize(MEASUREMENT_CONFIG, THRESHOLD_CONFIG, classLoader);
		} catch (Exception e1) {

			logger.error("errorc occured while configuring mset for default ");
		}
		// Initializing SOA specific counters
		SoaFrameworkContext soaFw = (SoaFrameworkContext) Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
		if (soaFw.isSoaSupportEnabled()) {
			AseMeasurementManager.instance().initSoaMeasurementCountersNew();
		}

		// Initializing NSEP specific counters
		AseEngine engine = (AseEngine) Registry.lookup(Constants.NAME_ENGINE);

		String ingwMessageQueue = (String) configRep.getValue(Constants.INGW_MSG_QUEUE);
		String nsepIngwPriority = (String) configRep.getValue(Constants.NSEP_INGW_PRIORITY);

		if (logger.isDebugEnabled()) {
			logger.debug("Messages from INGw will come in " + ingwMessageQueue + " queue");
			logger.debug("All call prioirty support with INGw messages coming in priority queue " + nsepIngwPriority);
		}

		if (engine.isCallPriorityEnabled()
				&& (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals("true"))) {
			AseMeasurementManager.instance().initNSEPMeasurementCountersNew();
		}
		// Initialize local counters
		AseMeasurementUtil.initLocalCounters();
		AseMeasurementUtil.initDefaultCounters();

		if (this.writeToFile) {
			// Create and register a Measurement listener.
			fileLogger = new AseMeasurementLogger();
			try {
				fileLogger.initialize();
			} catch (Exception e) {
				logger.error("error occured while processing file logger");
			}
			this.measMgr.registerListener((MeasurementListener) fileLogger);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Call sendRegisterRequestForPerformanceStats on AseMeasurementManager");
		}
		AseMeasurementManager.instance().sendRegisterRequestForPerformanceStats();

		// Registering for Background process
		AseMeasurementManager.instance().registerForBackgroundProcessNew();
		AseMeasurementManager.instance().updateClearTime(System.currentTimeMillis());

		/*
		 * Get the minimum accumulation interval for Application specific Measurement
		 * Sets from Config Repository
		 */
		m_configRepository = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		try {
			minDefaultAccIntervalForSets = Integer
					.parseInt(m_configRepository.getValue(Constants.APP_COUNTERS_MIN_ACC_INTERVAL));
		} catch (NumberFormatException e) {
			minDefaultAccIntervalForSets = 300;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Setting minimum default acc. interval for Application specific " + "Measurement Counters to "
					+ minDefaultAccIntervalForSets + " secs.");
		}

		this.measMgr.setMinDefaultAccIntervalForSets(minDefaultAccIntervalForSets);
		this.measMgr.loadSleeMeasurementCounterMap(sleeMeasurementCounterMap);

	}

}
