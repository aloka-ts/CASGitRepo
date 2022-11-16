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

package com.baypackets.ase.util.consumer;

import org.apache.log4j.Logger;

import com.agnity.oems.agent.messagebus.OEMSServiceStarter;
import com.agnity.oems.agent.messagebus.dto.OemsMeasurementFetchResponseDTO;
import com.agnity.oems.agent.messagebus.listener.OEMSObserver;
import com.agnity.oems.agent.messagebus.response.OemsMeasurementFetchResponse;
import com.agnity.oems.agent.messagebus.utils.OemsJsonUtils;
import com.agnity.oems.agent.messagebus.utils.OemsUtils;
import com.baypackets.ase.measurement.AseMeasurementManager;

/**
 * This class to use to consume the records from topics
 * 
 * @author Madhukar
 *
 * @param <T>
 */
public class OemsMessageConsumer<T> implements OEMSObserver<T> {

	private static Logger logger = Logger.getLogger(OemsMessageConsumer.class);

	@Override
	public void consumeRecords(T value, String topic) {

		/**
		 * code for handling response of MSET/Threashold config got from EMS
		 */
		if (topic.endsWith("oemsFRMSET")) {
			logger.info("Inside the MSET response :" + value);

			OemsMeasurementFetchResponse oemsMeasurementFetchResponse = null;
			try {
				oemsMeasurementFetchResponse = OemsJsonUtils.jsonToObject(value.toString(),
						OemsMeasurementFetchResponse.class);
			} catch (Exception e) {
				logger.error("exception occured while parsing  " + value);
			}
			String measurementSetInfo = oemsMeasurementFetchResponse.getResult().getMeasurementSetInfo();
			OemsMeasurementFetchResponseDTO oemsMeasurementFetchResponseDTO = oemsMeasurementFetchResponse.getResult();
			String selfInstanceId = OEMSServiceStarter.oemsInitDTO.getSelfInstanceId();
			String siteId = OemsUtils.getSiteId(selfInstanceId);
			String componentId = OemsUtils.getInstanceId(selfInstanceId);
			// if siyteId and component ID matches
			if (siteId.equals(oemsMeasurementFetchResponseDTO.getSiteId())
					&& componentId.equals(oemsMeasurementFetchResponseDTO.getComponentId())) {
				if (logger.isInfoEnabled()) {
					logger.info("setting xml response for componentId: " + componentId);
				}
				AseMeasurementManager.msetAndThresXmlResposneFromEMS = measurementSetInfo;
				AseMeasurementManager.transectionIdOfGetConfigRequest = oemsMeasurementFetchResponseDTO
						.getTransactionId();

			}
			if (logger.isInfoEnabled()) {
				logger.info("values are set from kafka");
			}

		}

	}

}
