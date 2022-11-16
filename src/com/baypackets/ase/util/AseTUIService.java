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
package com.baypackets.ase.util;

import org.apache.log4j.Logger;

import com.agnity.oems.agent.messagebus.OEMSServiceStarter;
import com.baypackets.ase.common.Registry;

public class AseTUIService {
	private static Logger logger = Logger.getLogger(AseTUIService.class);

	private  static AseTUIService instance;
	
	public  static boolean tuiEnabled = false;

	private AseTUIService() {
	}

	public static AseTUIService getTUIInstence() {
		if (logger.isDebugEnabled()) {
			logger.info("Initinlizing AseTUIService");
		}
		if (instance == null) {
			synchronized (AseTUIService.class) {
				if (instance == null) {
					instance = new AseTUIService();
				}
			}
		}
		return instance;
	}

	
	/**
	 * send tui data
	 * @param tuiMessage
	 * 
	 * "{\"contextVariable\":\"request\",\"requests\":[{\"api\":\"digitPatternAllowed\",\"operation\":\"CREATE\",\"data\":{\"pattern\":{\"accountNumber\":\"1345\",\"patternName\":\"test\",\"patternType\":\"trt\",\"patternDescription\":\"desc\"},\"accountCodes\":[{\"accountNumber\":\"1345\",\"accountCode\":\"8523654\"},{\"accountNumber\":\"1345\",\"accountCode\":\"987452\"}]}}]}"
	 */
	public void sendTUIData(String tuiMessage) {
		logger.info("Send tui data to SPS:" + tuiMessage);

		if (tuiEnabled) {
			OEMSServiceStarter oemsAgentInstence = (OEMSServiceStarter) Registry.lookup(Constants.OEMS_AGENT_WRAPPER);
			oemsAgentInstence.sendTUIData(tuiMessage);
		} else {
			logger.info("TUI is not enabled");
		}

	}
}