//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************
                                                                                                                             
																															 //***********************************************************************************
//
//      File:   SoaMeasurementUtil.java
//
//      Desc:   This concrete class represents measurement counters used in SOA framework .
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Somesh Kr. Srivastava           27/12/07        Initial Creation
//
//***********************************************************************************
																															                                                                                                                              
package com.baypackets.ase.soa;

import com.baypackets.ase.measurement.AseCounter;
import com.baypackets.ase.measurement.AseMeasurementManager;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;

import org.apache.log4j.Logger;


/**
* This class represents measurement counters used in SOA framework.
 *
 * @author Somesh Kr. Srivastava
 */
public class SoaMeasurementUtil {

	private static Logger m_logger = Logger.getLogger(SoaMeasurementUtil.class);
	
	private static final MeasurementManager m_SoaMeasMgr = AseMeasurementManager.instance().getSoaMeasurementManager();
	
	public static final String NAME_INCOMING_INVOCATIONS = "Total Number of Incoming Invocations";
	public static final String NAME_OUTGOING_INVOCATIONS = "Total Number of Outgoing Invocations";
	public static final String NAME_INCOMING_CALLBACKS = "Total Number of Incoming Callbacks";
	public static final String NAME_OUTGOING_CALLBACKS = "Total Number of Outgoing Callbacks";
	public static final String NAME_INCOMING_INVOCATIONS_FAILED = "Total Number of Incoming Invocations Failed";
	public static final String NAME_OUTGOING_INVOCATIONS_FAILED = "Total Number of Outgoing Invocations Failed";
	public static final String NAME_INCOMING_CALLBACKS_FAILED = "Total Number of Incoming Callbacks Failed";
	public static final String NAME_OUTGOING_CALLBACKS_FAILED = "Total Number of Outgoing Callbacks Failed";
	public static final String NAME_TOTAL_FINDSERVICE_INVOKE = "Total Number of Findservice Method Invoked";
	public static final String NAME_TOTAL_FINDLISTENER_INVOKE = "Total Number of Findlistener Method Invoked";
	
	public static AseCounter m_counterIncomingInvocations;
	public static AseCounter m_counterOutgoingInvocations;
	public static AseCounter m_counterIncomingCallbacks;
	public static AseCounter m_counterOutgoingCallbacks;
	public static AseCounter m_counterIncomingInvocationsFailed;
	public static AseCounter m_counterOutgoingInvocationsFailed;
	public static AseCounter m_counterIncomingCallbacksFailed;
	public static AseCounter m_counterOutgoingCallbacksFailed;
	public static AseCounter m_counterTotalFindServiceInvoke;
	public static AseCounter m_counterTotalFindListenerInvoke;

	public static void initSoaCounters() {
		if(m_logger.isDebugEnabled())
			m_logger.debug("initializing counters");
		m_counterIncomingInvocations = (AseCounter)m_SoaMeasMgr.getMeasurementCounter(NAME_INCOMING_INVOCATIONS);
		m_counterOutgoingInvocations = (AseCounter)m_SoaMeasMgr.getMeasurementCounter(NAME_OUTGOING_INVOCATIONS);
		m_counterIncomingCallbacks = (AseCounter)m_SoaMeasMgr.getMeasurementCounter(NAME_INCOMING_CALLBACKS);
		m_counterOutgoingCallbacks = (AseCounter)m_SoaMeasMgr.getMeasurementCounter(NAME_OUTGOING_CALLBACKS);
		m_counterIncomingInvocationsFailed = (AseCounter)m_SoaMeasMgr.getMeasurementCounter(NAME_INCOMING_INVOCATIONS_FAILED);
		m_counterOutgoingInvocationsFailed = (AseCounter)m_SoaMeasMgr.getMeasurementCounter(NAME_OUTGOING_INVOCATIONS_FAILED);
		m_counterIncomingCallbacksFailed = (AseCounter)m_SoaMeasMgr.getMeasurementCounter(NAME_INCOMING_CALLBACKS_FAILED);
		m_counterOutgoingCallbacksFailed = (AseCounter)m_SoaMeasMgr.getMeasurementCounter(NAME_OUTGOING_CALLBACKS_FAILED);
		m_counterTotalFindServiceInvoke = (AseCounter)m_SoaMeasMgr.getMeasurementCounter(NAME_TOTAL_FINDSERVICE_INVOKE);
		m_counterTotalFindListenerInvoke = (AseCounter)m_SoaMeasMgr.getMeasurementCounter(NAME_TOTAL_FINDLISTENER_INVOKE);

	}

	public static void incrementIncomingInvocations() {
		m_counterIncomingInvocations.change(1);
	}

	public static void incrementOutgoingInvocations() {
		m_counterOutgoingInvocations.change(1);
	}

	public static void incrementIncomingCallbacks() {
		m_counterIncomingCallbacks.change(1);
	}

	public static void incrementOutgoingCallbacks() {
		m_counterOutgoingCallbacks.change(1);
	}

	public static void incrementIncomingInvocationsFailed() {
		m_counterIncomingInvocationsFailed.change(1);
	}

	public static void incrementOutgoingInvocationsFailed() {
		m_counterOutgoingInvocationsFailed.change(1);
	}

	public static void incrementIncomingCallbacksFailed() {
		m_counterIncomingCallbacksFailed.change(1);
	}

	public static void incrementOutgoingCallbacksFailed() {
		m_counterOutgoingCallbacksFailed.change(1);
	}

	public static void incrementTotalFindServiceInvoke() {
		m_counterTotalFindServiceInvoke.change(1);
	}

	public static void incrementTotalFindListenerInvoke() {
		m_counterTotalFindListenerInvoke.change(1);
	}

		

}
	
		
