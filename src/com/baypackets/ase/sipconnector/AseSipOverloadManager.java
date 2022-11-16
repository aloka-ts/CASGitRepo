/*
 * Created on Oct 11, 2004
 *
 * @author Vishal Sharma
 */
package com.baypackets.ase.sipconnector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseProtocolSession;
import com.baypackets.ase.ocm.OlfOverloadEvent;
import com.baypackets.ase.ocm.OverloadControlManager;
import com.baypackets.ase.ocm.SyncOverloadListener;
import com.baypackets.ase.ocm.TimeMeasurement;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.ocm.OverloadEvent;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

/**
 * <h1>Overload Control Algorithm</h1>
 * <p> Parameters used in the algorithm:
 *		<ul>
 *		<li><code>confMaxInPrgsCalls</code></li>
 *		<li><code>currMaxInPrgsCalls</code></li>
 *		<li<code>currInPrgsCalls</code></li>
 *		<li><code>maxNormalOlf</code></li>
 *		<li><code>lastOlf<code></li>
 *		</ul>
 * </p>
 *
 * <p>Algorithm:
 * <code>
 * 	if lastOlf or currInPrgsCalls changed
 *		calculate currMaxInPrgsCalls using the formular:
 *			currMaxInPrgsCalls = (maxNormalOlf/lastOlf)*currInPrgsCalls
 *			// it means currMaxInPrgsCalls decreases when lastOlf increases, if
 *			// currInPrgsCalls is not changed. It also means currMaxInPrgsCalls
 *			// increases when currInPrgsCalls increases while lastOlf is not changed.
 *	if currMaxInPrgsCalls <= 0
 *		currMaxInPrgsCalls = 1
 *	if currMaxInPrgsCalls > confMaxInPrgsCalls
 *		currMaxInPrgsCalls = confMaxInPrgsCalls
 *	when new call is coming
 *		if currInPrgsCalls > currMaxInPrgsCalls
 * 			drop the call
 * </code>
 * </p>
 */

/**
 * This class handles call admission based on inputs from the Overload Control
 * Manager. It adjusts the maximum allowed in-progress calls based on the system
 * overload factor, from an initial (and maximum) configured value. Stack
 * Interface Layer uses the services of this class to determine whether to allow
 * a new call. This decision is based on both the current in-progress count as
 * well as the containment of various factors within their maximum specified
 * limits. If there is no configured value of maximum number of in-progress
 * calls, all calls would be allowed.
 */
public class AseSipOverloadManager extends SyncOverloadListener implements
		MComponent {
	/**
	 * Constructor.
	 * 
	 */
	AseSipOverloadManager() {
		m_isInitialized = false;
	}

	/*
	 * Congestion control change
	 */
	private boolean applyMaxActiveCallCriteria = true;
	private int congLevelOneActiveCalls;
	private int congLevelTwoActiveCalls;
	private int congLevelThreeActiveCalls;
	private static int allowedConfActiveCalls = 0;
	private static int allowedActiveCalls = 0;

	private static int rejectedConfActiveCalls = 0;
	private static int rejectedActiveCalls = 0;

	private static BitSet cpuContentionAlarm = new BitSet(2);
	private static BitSet memoryContentionAlarm = new BitSet(2);
	private static BitSet activeCallsContentionAlarm = new BitSet(2);
	private static BitSet networkTransactionsAlarm = new BitSet(1);
	private static BitSet aggregatedTransactionsAlarm = new BitSet(1);
	private static BitSet newCallsPerSecAlarm = new BitSet(1);
	private boolean falseL3AlarmRaised;
	
	

	public boolean isApplyMaxActiveCallCriteria() {
		return applyMaxActiveCallCriteria;
	}

	public void setApplyMaxActiveCallCriteria(boolean applyMaxActiveCallCriteria) {
		this.applyMaxActiveCallCriteria = applyMaxActiveCallCriteria;
	}
	
	//change ends here

	/**
	 * Initialization method
	 * 
	 * @param confMaxInPrgsCalls
	 *            configured maximum allowed in-progress calls.
	 * @param maxNormalOlf
	 *            configured maximum normal OLF.
	 */
	void initialize(boolean priority) {
		this.m_priorityInstance = priority;
		ocmMgr = (OverloadControlManager) Registry
				.lookup(Constants.NAME_OC_MANAGER);

		// parameter id of response time commented  for Congestion control
	//	ocmId = ocmMgr.getParameterId(OverloadControlManager.RESPONSE_TIME);
		if (true != m_isInitialized) {
			String strValue = null;
			ConfigRepository configRepository = (ConfigRepository) Registry
					.lookup(Constants.NAME_CONFIG_REPOSITORY);

			// Added for NSEP(priority) call handling
			if (priority) {
				strValue = (String) configRepository
						.getValue(Constants.NSEP_MAX_SIP_IN_PROGRESS_CALLS);
				if (null != strValue)
					m_confMaxInPrgsCalls = Integer.parseInt(strValue);
			} else {
				strValue = (String) configRepository
						.getValue(Constants.OID_MAX_SIP_IN_PROGRESS_CALLS);

				if (null != strValue)
					m_confMaxInPrgsCalls = Integer.parseInt(strValue);
			}

			/*
			 * Congestion control change
			 */

			strValue = (String) configRepository
					.getValue(Constants.OID_CONTENTION_LEVEL_ONE_ACTIVE_CALLS);
			
			if (null != strValue)
				congLevelOneActiveCalls = Integer.parseInt(strValue);
			
			strValue = (String) configRepository
					.getValue(Constants.OID_CONTENTION_LEVEL_TWO_ACTIVE_CALLS);
			
			if (null != strValue)
				congLevelTwoActiveCalls = Integer.parseInt(strValue);
			
			strValue = (String) configRepository
					.getValue(Constants.OID_CONTENTION_LEVEL_THREE_ACTIVE_CALLS);
			
				if (null != strValue) 
			  congLevelThreeActiveCalls= Integer.parseInt(strValue);
			
			
			
			strValue = (String) configRepository
			.getValue(Constants.OID_CONTENTION_ALLOWED_ACTIVE_CALLS);
			if (null != strValue)
				allowedConfActiveCalls = Integer.parseInt(strValue);

			strValue = (String) configRepository
			.getValue(Constants.OID_CONTENTION_REJECTED_ACTIVE_CALLS);
			if (null != strValue)
				rejectedConfActiveCalls = Integer.parseInt(strValue);

			// Congestion control Change  Ended 

			strValue = (String) configRepository
					.getValue(Constants.OID_MAX_NORMAL_OLF);
			if (null != strValue)
				m_maxNormalOlf = Float.parseFloat(strValue);

			strValue = (String) configRepository
					.getValue(Constants.OID_OVERLOAD_RATIO);
			if (null != strValue)
				overloadRatio = Float.parseFloat(strValue);

			strValue = (String) configRepository
					.getValue(Constants.OID_ENABLE_OCM_PROFILING);
			if (null != strValue)
				profilingEnabled = strValue.equals("1") ? true : false;

			strValue = (String) configRepository
					.getValue(Constants.OID_OCM_METHOD);
			if (null != strValue)
				ocmMethod = Integer.parseInt(strValue);

			strValue = (String) configRepository
					.getValue(Constants.OID_OCM_CONTROL_INTERVAL);
			if (null != strValue)
				confNumSamples = Integer.parseInt(strValue);

			strValue = (String) configRepository
					.getValue(Constants.OID_OCM_PERCENTAGE_TARGET);
			if (null != strValue)
				confPct = Double.parseDouble(strValue);

			strValue = (String) configRepository
					.getValue(Constants.OID_OCM_SMOOTH_FACTOR);
			if (null != strValue)
				confSmoothFactor = Double.parseDouble(strValue);

			strValue = (String) configRepository
					.getValue(Constants.OID_THRESHOLD_INC_FACTOR);
			if (null != strValue)
				confIncFactor = Double.parseDouble(strValue);

			strValue = (String) configRepository
					.getValue(Constants.OID_THRESHOLD_DEC_FACTOR);
			if (null != strValue)
				confDecFactor = Double.parseDouble(strValue);

			strValue = (String) configRepository
					.getValue(Constants.OID_PERCENTAGE_CALL_DROP);
			if (strValue != null) {
				setPercentageCallDrop(Integer.parseInt(strValue));
			}

			strValue = (String) configRepository
					.getValue(Constants.OCM_ALARM_HYSTERESIS);
			if (strValue != null) {
				ocmAlarmHysteresis = Float.parseFloat(strValue);
			}

			m_currMaxInPrgsCalls = m_confMaxInPrgsCalls;
			normalTimeRatios = new double[confNumSamples];
			tokenCount = bucketSize;
			ocmMgr.addOverloadListener(this, 0f, priority);

			if (m_l.isInfoEnabled()) {
				if (overloadControlEnabled) {
					m_l.info(parametersToString());
				} else {
					m_l.info("Overload control disabled");
				}
			}

			if (profilingEnabled) {
				if (overloadControlEnabled) {
					logMeasurement(parametersToString());
				} else {
					logMeasurement("Overload control disabled");
				}
			}

			// Register with the TELNET Server for the System information.
			TelnetServer telnetServer = (TelnetServer) Registry
					.lookup(Constants.NAME_TELNET_SERVER);
			if ((telnetServer != null) && (!this.m_priorityInstance)) {
				CallGappingHandler handler = new CallGappingHandler();
				telnetServer.registerHandler(Constants.CMD_CALL_DROPPING,
						handler);
			}

			m_isInitialized = true;
			
			networkTransactionsAlarm.clear(0);
			aggregatedTransactionsAlarm.clear(0);
			
			if (m_l.isInfoEnabled()){
				m_l
						.info("networkTransactionsAlarm..........."+networkTransactionsAlarm.get(0));
			}
			if (m_l.isInfoEnabled()){
				m_l
						.info("aggregatedTransactionsAlarm................."+aggregatedTransactionsAlarm.get(0));
				
			}
		} else {
			m_l.error("AseSipOverloadManager already initialized");
		}
	}

	/**
	 * Adjusts the Current-Max-In-Progress calls (CurrMaxInPrgsCalls) allowed
	 * based on the current Overload Factor (OLF). The intention is to keep the
	 * OLF constrained within MaxNormalOlf. In case the OLF exceeds this value,
	 * CurrMaxInPrgsCalls is reduced progressively to such value as to bring the
	 * OLF back to MaxNormalOlf. This adjustment in CurrMaxInPrgsCalls is sought
	 * to be made in such a fashion as would - a) rapidly bring the OLF within
	 * MaxNormalOlf limit, and b) avoid excessive oscillations of
	 * CurrMaxInPrgsCalls when OLF values change from below MaxNormalOlf to
	 * above MaxNormalOlf values. For the objective (b) above, the last value of
	 * CurrMaxInPrgsCalls is preserved in the sub-MaxNormalOlf range when
	 * crossing over takes place from over-MaxNormalOlf to sub-MaxNormalOlf
	 * range. The value of CurrInPrgsCalls is increased, however, in case
	 * CurrMaxInPrgsCalls is reached while OLF remains sub-MaxNormalOlf. The new
	 * CurrMaxInPrgsCalls value when the OLF value is over-MaxNormalOlf is
	 * <code>(MaxNormalOlf/OLF)*CurrInPrgsCalls</code>. The calculation of OLF
	 * over CurrInPrgsCalls ensures that the covergence to MaxNormalOlf is
	 * rapid. The increase in CurrMaxInPrgsCalls upon CurrInPrgsCalls hitting
	 * the barrier in sub-MaxNormalOlf range is done in
	 * <codeoisCallAllowed</code>isCallAllowed</code> method.
	 * 
	 * @param event
	 *            OLF overload event.
	 */
	public void olfChanged(OverloadEvent event) {
		if (m_l.isDebugEnabled())
			m_l.debug("olfChanged(OverloadEvent) called.");

		if (!(event instanceof OlfOverloadEvent)) {
			m_l
					.error("Not an OlfOverloadEvent. Returning without doing anything.");

			return;
		}

		parameterStatus = event.getParameterStatus();

		// there is no overload control.
		if (!overloadControlEnabled) {
			if (m_l.isInfoEnabled())
				m_l
						.info("No Overload Control. Returning without doing anything.");

			return;
		}

		// acquiring lock ..
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException ex) {
			m_l.error("Error in acquiring lock. Exiting.", ex);
			return;
		}

		try {
			m_lastOlf = ((OlfOverloadEvent) event).getOlf();

			if (ocmMethod == ACCEPTANCE_RATE) {
				adjustAcceptanceRate();
			} else {
				adjustInPrgsCounter(m_lastOlf);
			}
		} finally {
			// releasing lock ..
			try {
				lock.unlock();
			} catch (Error ex) {
				m_l.error("Error in releasing lock.", ex);
			}
		}
		reportOverloadCondition();
	}

	private void adjustInPrgsCounter(float olf) {
		// no need to adjust CurrMaxInPrgsCalls if the current OLF is in
		// sub-MaxNormalOlf range
		if ((m_maxNormalOlf >= olf)) {
			if (m_l.isInfoEnabled())
				m_l.info("OLF is in sub-MaxNormalOlf range [" + olf
						+ "]. Not changing in-progress calls' threshold.");
		}

		// else ..
		// calculate the new CurrMaxInPrgsCalls in case the olf is over
		// MaxNormalOlf.
		else {
			m_currMaxInPrgsCalls = (int) ((m_maxNormalOlf / olf)
					* m_currMaxInPrgsCalls / confDecFactor);
			// m_currMaxInPrgsCalls =
			// (int)((m_maxNormalOlf/olf)*m_currInPrgsCalls);

			if (m_l.isInfoEnabled())
				m_l.info("Higher than normal OLF [" + olf
						+ "]. Reducing in-progress calls' threshold.");
		}

		// check whether the change has resulted in CurrMaxInPrgsCalls outside
		// the range (1, ConfMaxInPrgsCalls) due to rounding off.
		// [Not keeping 0 as CurrMaxInPrgsCalls value as this would result in
		// problems when scaling up.]
		if (0 >= m_currMaxInPrgsCalls)
			m_currMaxInPrgsCalls = 1;
		else if (m_confMaxInPrgsCalls < m_currMaxInPrgsCalls)
			m_currMaxInPrgsCalls = m_confMaxInPrgsCalls;

		if (m_l.isInfoEnabled())
			m_l.info("Current Max In-Progress count = " + m_currMaxInPrgsCalls);
	}

	/**
	 * Returns <code>true</code> is the current call can be admitted; <code>
	 * false</code>
	 * otherwise. The decision to allow a new call is based on two factors, viz.
	 * 1) there should not no outstanding Maximum Limit Crossed events, and 2)
	 * CurrInPrgsCalls is less than CurrMaxInPrgsCalls. In case condition (2) is
	 * not met while last OLF was sub-MaxNormalOlf, CurrMaxInPrgsCalls would be
	 * increased in proportion of the Max Normal value to the last OLF value.
	 * The increase would be <code>(MaxNormalOlf/OLF)*CurrInPrgsCalls</code>. If
	 * the new CurrMaxInPrgsCalls is more than CurrInPrgsCalls, call is allowed.
	 * If the call can be allowed, CurrInPrgsCalls is incremented by 1.
	 * 
	 * @return <code>true</code> if this new call is allowed; <code>false</code>
	 *         otherwise.
	 */
	boolean isCallAllowed(SasMessage message) {
		boolean allowed = false;
		if (m_l.isDebugEnabled())
			m_l.debug("isCallAllowed() called.");

		// if manual call gapping enabled, other overload controls will give
		// their way
//		if (this.pctCallDrops > 0) {
//			return isAllowedByPercentage();
//		}
//
//		// if no overload control; allow all calls.
//		if (!overloadControlEnabled) {
//			if (m_l.isInfoEnabled()) {
//				m_l
//						.info("Overload control is disabled. All calls are allowed.");
//				m_l.info("Current In-Progress Calls: " + m_currInPrgsCalls);
//			}
//			++m_currInPrgsCalls;
//			return true;
//		}
//
//		if (m_l.isInfoEnabled()) {
//			m_l.info("Current In-Progress count: " + m_currInPrgsCalls
//					+ ", Current Max: " + m_currMaxInPrgsCalls);
//		}
//
//		// reject the call if there is any outstanding MLC event// Max limit
//		// Crossed
//		if (0 < parameterStatus.cardinality()) {
//			if (m_l.isInfoEnabled()) {
//				m_l.info("MLC outstanding - call rejected.");
//			}
//			nOverMaxCalls++;
//			// Priority call will not come here so not adding that alarm.
//			synchronized (syncObj) {
//				if (!callGappingStarted) {
//					callGappingStarted = true;
//					this.reportCallGapping(true, this
//							.getOverloadReasons(parameterStatus));
//				}
//			}
//			return false;
//		}
		
		/*
		 *  Congestion control change started
		 */
		
		if (isApplyMaxActiveCallCriteria()) {
			
			
			if(networkTransactionsAlarm.get(0) ){
			
				m_l.error("Network Transactions limit has been reached so not allowing any calls ");
				
				return false;
			}
			
			if(aggregatedTransactionsAlarm.get(0) ){
				
				m_l.error("Aggregated Transactions limit has been reached so not allowing any calls " );
			
				return false;
			}
			
           if(newCallsPerSecAlarm.get(0) ){
				
				m_l.error("Calls Per Second limit has been reached so not allowing any calls " );
			
				return false;
			}

			// isActiveCallsLimitReached();
			String congResource="";
				if(cpuContentionAlarm.get(2) || memoryContentionAlarm.get(2) || activeCallsContentionAlarm.get(2) ){
					
					
					if(cpuContentionAlarm.get(2))
						congResource="CPU";
					if(memoryContentionAlarm.get(2))
						congResource="MEMORY";
					if(activeCallsContentionAlarm.get(2))
						congResource="ACTIVE CALLS";
					
				
					m_l.error("Level Three Alarm has been raised for Congestion resource "+ congResource+" so not allowing any calls " );
				
					return false;
				}

			if (cpuContentionAlarm.get(1) || memoryContentionAlarm.get(1)
					|| activeCallsContentionAlarm.get(1)) {

				if (cpuContentionAlarm.get(1))
					congResource = "CPU";
				if (memoryContentionAlarm.get(1))
					congResource = "MEMORY";
				if (activeCallsContentionAlarm.get(1))
					congResource = "ACTIVE CALLS";

				//if (m_l.isInfoEnabled()) {
					m_l
							.error("Level Two Alarm has been raised for Congestion resource "
									+ congResource);
				//}

				if (isAllowedByConfig()) {

                                 if (m_l.isInfoEnabled()) { 
						m_l.info("Call is allowed by config  "
								+ allowedActiveCalls + " configured value "
								+ allowedConfActiveCalls);
                                 } 
					++m_currInPrgsCalls;

					isActiveCallsLimitReached();
					return true;
				} else
					updateRejectedCallsCounter();
				return false;
			}

			++m_currInPrgsCalls;
			isActiveCallsLimitReached();
			return true;

		} else {
			// ++m_currInPrgsCalls;
			return true;
		}
		  
		  // will not reach below

//		if (ocmMethod == ACCEPTANCE_RATE) {
//			if (isAllowedByRate(false)) {
//				++m_currInPrgsCalls;
//				nAllowedCalls++;
//				allowed = true;
//			} else {
//				nDroppedCalls++;
//			}
//		} else {
//			if (this.isAllowedByCounter(false)) {
//				++m_currInPrgsCalls;
//				nAllowedCalls++;
//				allowed = true;
//			} else {
//				nDroppedCalls++;
//			}
//		}
//		synchronized (syncObj) {
//			if (allowed && callGappingStarted) {
//				callGappingStarted = false;
//				reportCallGapping(false, null);
//			}
//		}
//
//		allowed = allowed || ocmMgr.matchesWhiteListEntry(message, false);
//		return allowed;
	}

	/**
	 * This overloaded method shall be invoked only if Call priority is enabled.
	 * If call is normal(priority boolean is false) . it shall check whether
	 * Call is allowed or not. if the call is allowed it invokes the
	 * isCallAllowed() method of normal call listener and depending upon
	 * response update the counters. Priority call will be handled by this
	 * listener only. for more details see isCallAllowed().
	 */

	boolean isCallAllowed(SasMessage message, boolean priority) {
		boolean allowed = false;
		if (m_l.isDebugEnabled())
			m_l.debug("isCallAllowed(boolean) called.");

		// if manual call gapping enabled, other overload controls will give
		// their way
		// we are not providing manual call gapping feature for NSEP call so
		// commenting
		/*
		 * if (this.pctCallDrops > 0) { return isAllowedByPercentage(); }
		 */

		// if no overload control; allow all calls.
		if (!overloadControlEnabled) {
			if (m_l.isInfoEnabled()) {
				m_l
						.info("Overload control is disabled. All calls are allowed.");
				m_l.info("Current In-Progress Calls: " + m_currInPrgsCalls);
			}
			++m_currInPrgsCalls;
			if ((!priority) && (this.m_priorityInstance)) {
				return this.m_normalCallListener.isCallAllowed(message);
			} else {
				return true;
			}
		}

		if (m_l.isInfoEnabled()) {
			if (this.m_priorityInstance) {
				m_l.info("Current Priority In-Progress count: "
						+ m_currInPrgsCalls + ", Current Max: "
						+ m_currMaxInPrgsCalls);
			} else {
				m_l.info("Current In-Progress count.: " + m_currInPrgsCalls
						+ ", Current Max: " + m_currMaxInPrgsCalls);
			}
		}

		if (priority) {
			OverloadControlManager.OverloadReason reason = new OverloadControlManager.OverloadReason();
			if (ocmMgr.isPriorityOverload(reason)) {
				if (m_l.isInfoEnabled()) {
					m_l
							.info("MLC outstanding - call rejected: Priority = true");
				}
				if (!priorityCallGappingStarted) {
					synchronized (syncObj) {
						priorityCallGappingStarted = true;
					}
					this.reportPriorityCallGapping(true, reason.toString());
				}
				nOverMaxCalls++;
				return false;
			}

			++m_currInPrgsCalls;
			nAllowedCalls++;
			if (priorityCallGappingStarted) {
				synchronized (syncObj) {
					priorityCallGappingStarted = false;
				}
				reportPriorityCallGapping(false, null);
			}
			return true;

			/*
			 * if(ocmMethod != ACCEPTANCE_RATE) {
			 * if((m_normalCallListener.m_currInPrgsCalls
			 * *1.0f/m_currInPrgsCalls*1.0f) > 0.1) { ++m_currInPrgsCalls;
			 * nAllowedCalls++; return true; } else if(m_currInPrgsCalls <
			 * m_currMaxInPrgsCalls) { ++m_currInPrgsCalls; nAllowedCalls++;
			 * return true; } }
			 */
		} else if (0 < parameterStatus.cardinality()) {
			// reject the call if there is any outstanding MLC event
			if (m_l.isInfoEnabled()) {
				m_l.info("MLC outstanding - call rejected: Priority = "
						+ priority);
			}
			synchronized (syncObj) {
				if (!callGappingStarted) {
					callGappingStarted = true;
					this.reportCallGapping(true, this
							.getOverloadReasons(parameterStatus));
				}
			}
			nOverMaxCalls++;
			return false;
		}

		if (ocmMethod == ACCEPTANCE_RATE) {
			if (this.isAllowedByRate(priority)) {
				if ((priority)
						|| (this.m_normalCallListener.isCallAllowed(message))) {
					allowed = true;
				}
			} else {
				nDroppedCalls++;
			}
		} else {
			if (this.isAllowedByCounter(priority)) {
				if ((priority)
						|| (this.m_normalCallListener.isCallAllowed(message))) {
					allowed = true;
				}
			} else {
				nDroppedCalls++;
			}
		}
		if (allowed) {
			++m_currInPrgsCalls;
			nAllowedCalls++;
			if (priority) {
				if (priorityCallGappingStarted) {
					synchronized (syncObj) {
						priorityCallGappingStarted = false;
					}
					this.reportPriorityCallGapping(false, null);
				}
			} else {
				synchronized (syncObj) {
					if (callGappingStarted) {
						callGappingStarted = false;
						this.reportCallGapping(false, null);
					}
				}
			}

		}
		allowed = allowed || ocmMgr.matchesWhiteListEntry(message, true);
		return allowed;
	}

	public boolean updatePriorityCongestion() {
		// if(this.m_priorityInstance) {
		// return false;
		// }

		if (0 < parameterStatus.cardinality()) {
			return true;
		}

		boolean increase = false;

		if (ocmMethod == ACCEPTANCE_RATE) {
			long curtime = System.currentTimeMillis();
			long delay = curtime - lastTime;
			try {
				lock.lockInterruptibly();
			} catch (InterruptedException ex) {
				m_l.error("Error in acquiring lock", ex);
				return false;
			}

			try {
				double tmptokenCount = tokenCount;
				if (delay >= confMinDelay) {
					double nTokens = delay / (1000 * 1.0 / curRate);
					tmptokenCount = tokenCount + nTokens;
				}

				if (tmptokenCount < 1) {
					increase = true;
				}
			} finally {
				try {
					lock.unlock();
				} catch (Error er) {
					m_l.error("Error in releasing lock.", er);
				}
			}

		} else {
			if (m_currInPrgsCalls >= m_currMaxInPrgsCalls) {
				increase = true;
			}
		}
		if (!increase && this.m_priorityInstance) {
			increase = this.m_normalCallListener.updatePriorityCongestion();
		}
		return increase;
	}

	private boolean isAllowedByCounter(boolean priority) {
		boolean allowed = false;

		// acquiring lock ..
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException ex) {
			m_l.error("Error in acquiring lock. Not allowing the call.", ex);
			return false;
		}

		try {
			// if CurrInPrgsCalls >= CurrMaxInPrgsCalls, but last OLF is
			// sub-MaxNormal, increment the CurrMaxInPrgsCalls
			if ((m_currInPrgsCalls >= m_currMaxInPrgsCalls)
					&& (m_maxNormalOlf > m_lastOlf)) {

				// m_currMaxInPrgsCalls =
				// (int)((m_maxNormalOlf/m_lastOlf)*m_currInPrgsCalls);
				m_currMaxInPrgsCalls += (int) ((m_maxNormalOlf / m_lastOlf) * confIncFactor);
				if (m_confMaxInPrgsCalls < m_currMaxInPrgsCalls)
					m_currMaxInPrgsCalls = m_confMaxInPrgsCalls;

				if (m_l.isInfoEnabled())
					m_l
							.info("Sub-MaxNormal OLF; changing in-progress calls' threshold to "
									+ m_currMaxInPrgsCalls);
			}

			boolean tmpPriority = priority && this.m_priorityInstance;
			if (m_currInPrgsCalls >= m_currMaxInPrgsCalls) {
				if (tmpPriority) {
					if (!priorityCallGappingStarted) {
						synchronized (syncObj) {
							priorityCallGappingStarted = true;
						}
						this.reportPriorityCallGapping(true,
								"Max in-progress INVITEs exceeded");
					}
				} else {
					synchronized (syncObj) {
						if (!callGappingStarted) {
							callGappingStarted = true;
							this.reportCallGapping(true,
									"Max in-progress INVITEs exceeded");
					  	}
					}
				}

				if (m_l.isInfoEnabled()) {
					if (this.m_priorityInstance) {
						m_l.info("Overload - call rejected: Priority = "
								+ priority);
					} else {
						m_l.info("Overload - call rejected:");
					}
				}
			} else {
				allowed = true;
			}
			this.reportInviteLimit(tmpPriority);
		} finally {
			// releasing lock ..
			try {
				lock.unlock();
			} catch (Error ex) {
				m_l.error("Error in releasing lock.", ex);
			}
		}
		return allowed;
	}
	
	/**
	 * Congestion control change
	 * increments ACTIVE Call which means when app session is activated after FT
	 * increment it
	 */

	public void incrementActiveCall() {

		if (m_l.isDebugEnabled())
			m_l.debug("incrementActiveCall() called.");
		incrementInPrgsCalls();
	}
	
	/**
	 * Increments CurrInPrgsCalls. This method is called when a final response
	 * is sent to a dialog-initiating request.
	 */
	void incrementInPrgsCalls() {
		if (m_l.isDebugEnabled())
			m_l.debug("incrementInPrgsCalls() called.");

		// acquiring lock ..
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException ex) {
			m_l.error("Error in acquiring lock. Not allowing the call.", ex);
			return;
		}

		try {
			++m_currInPrgsCalls;
			isActiveCallsLimitReached();
		} finally {
			// releasing lock ..
			try {
				lock.unlock();
			} catch (Error ex) {
				m_l.error("Error in releasing lock.", ex);
			}
		}		
		if (m_l.isInfoEnabled())
			m_l.info("Current In-Progress Call Count: " + m_currInPrgsCalls);
	}
	
	
	/**
	 * Congestion control change
	 * Decrements ACTIVE Call which means when app session is invalidated
	 * decrement it
	 */

	public void decrementActiveCall() {

		if (m_l.isDebugEnabled())
			m_l.debug("decrementActiveCall() called.");
		decrementInPrgsCalls();
	}

	/**
	 * Decrements CurrInPrgsCalls. This method is called when a final response
	 * is sent to a dialog-initiating request.
	 */
	void decrementInPrgsCalls() {
		if (m_l.isDebugEnabled())
			m_l.debug("decrementInPrgsCalls() called.");

		// acquiring lock ..
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException ex) {
			m_l.error("Error in acquiring lock. Not allowing the call.", ex);
			return;
		}

		try {
		//	if(m_currInPrgsCalls >= 0 ) // to take care of decrement caused by app sessions invalidation by sources other than call
			 --m_currInPrgsCalls;
			/*
			 * Congestion control change
			 */
			isActiveCallsLimitCleared();
		} finally {
			// releasing lock ..
			try {
				lock.unlock();
			} catch (Error ex) {
				m_l.error("Error in releasing lock.", ex);
			}
		}		
		if (m_l.isInfoEnabled())
			m_l.info("Current In-Progress Call Count: " + m_currInPrgsCalls);
	}

	/**
	 * 
	 * Decrements CurrInPrgsCalls. This method is called only if Call Priority
	 * is enabled. and when a final response is sent to a dialog-initiating
	 * request.
	 */
	void decrementInPrgsCalls(boolean priority) {
		if (m_l.isDebugEnabled())
			m_l.debug("decrementInPrgsCalls(boolean) called.");

		// acquiring lock ..
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException ex) {
			m_l.error("Error in acquiring lock. Not allowing the call.", ex);
			return;
		}
		// if call is normal than decrement the counter on normal listener also.
		if (!priority) {
			this.m_normalCallListener.decrementInPrgsCalls();
		}

		try {
			--m_currInPrgsCalls;
			/*
			 * Congestion control change
			 */
			isActiveCallsLimitCleared();
		} finally {
			// releasing lock ..
			try {
				lock.unlock();
			} catch (Error ex) {
				m_l.error("Error in releasing lock.", ex);
			}
		}
		
		 
		if (m_l.isInfoEnabled())
			m_l.info("Current In-Progress Call Count: " + m_currInPrgsCalls);
	}
	
	/**
	 * Congestion control change 
	 * This method is used for checking if the calls is allowed by configuration
	 * when second level alarm has been generated :Congestion control change
	 */
	public boolean isAllowedByConfig(){
	
                  	
		// acquiring lock ..
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException ex) {
			m_l.error("Error in acquiring lock. Not allowing the call.", ex);
			return false;
		}
		try {
			if(allowedActiveCalls == allowedConfActiveCalls ){
				//rejectedActiveCalls=0;
                              if (m_l.isInfoEnabled()) { 
                                                m_l.info("isAllowedByConfig()" +false );
                                        } 
				return false;
			}
			++allowedActiveCalls;
		} finally {
			// releasing lock ..
			try {
				lock.unlock();
			} catch (Error ex) {
				m_l.error("Error in releasing lock.", ex);
			}
		}
                  if (m_l.isInfoEnabled()) {
                                                m_l.info("isAllowedByConfig()" +true );
                                        } 
		return true;
	}
	
	/**
	 * Congestion control change 
	 * This method is used to update rejected calls
	 * count after allowed call counter has been incremented to its max value
	 */
	public void updateRejectedCallsCounter() {

		if (m_l.isInfoEnabled()) {

			m_l.info("updateRejectedCallsCounter() rejectedActiveCalls "
					+ rejectedActiveCalls + " allowedActiveCalls "
					+ allowedActiveCalls);
		}
		// acquiring lock ..
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException ex) {
			m_l.error("Error in acquiring lock. Not allowing the call.", ex);
			return;
		}
		try {
			if (rejectedActiveCalls == rejectedConfActiveCalls) {
				allowedActiveCalls = 0;
				rejectedActiveCalls = 0;
			}
			++rejectedActiveCalls;
		} finally {
			// releasing lock ..
			try {
				lock.unlock();
			} catch (Error ex) {
				m_l.error("Error in releasing lock.", ex);
			}
		}
	}
  
  /**
   * Congestion control change
   * This method is used to check if Active Calls has cleared the limit or not
   */
	private void isActiveCallsLimitCleared() {
		if (m_l.isInfoEnabled()) {
			m_l
					.info(" isActiveCallsLimitCleared() currentInPrgs "
							+ m_currInPrgsCalls + "  "
							+ congLevelOneActiveCalls + " "
							+ congLevelTwoActiveCalls + " "
							+ congLevelThreeActiveCalls);
		}
		if (m_currInPrgsCalls <= (1 - ocmAlarmHysteresis)
				* congLevelOneActiveCalls)
			maxActiveCallsLimitCleared(ocmMgr
					.getParameterId(OverloadControlManager.CONTENTION_LEVEL_ONE_ACTIVE_CALLS));
		else if (m_currInPrgsCalls <= (1 - ocmAlarmHysteresis)
				* congLevelTwoActiveCalls)
			maxActiveCallsLimitCleared(ocmMgr
					.getParameterId(OverloadControlManager.CONTENTION_LEVEL_TWO_ACTIVE_CALLS));
		else if (m_currInPrgsCalls <= (1 - ocmAlarmHysteresis)
				* congLevelThreeActiveCalls)
			maxActiveCallsLimitCleared(ocmMgr
					.getParameterId(OverloadControlManager.CONTENTION_LEVEL_THREE_ACTIVE_CALLS));
	}

	  /**
	   *  Congestion control change
	   *  This method is used to check if Active Calls has Reached the limit or not 
	   */
	private void isActiveCallsLimitReached() {

		if (m_l.isInfoEnabled()) {
			m_l
					.info(" isActiveCallsLimitReached() currentInPrgs "
							+ m_currInPrgsCalls + "  "
							+ congLevelOneActiveCalls + " "
							+ congLevelTwoActiveCalls + " "
							+ congLevelThreeActiveCalls);
		}
		if (m_currInPrgsCalls >= congLevelThreeActiveCalls
				&& !activeCallsContentionAlarm.get(2)) {
			maxActiveCallsLimitReached(ocmMgr
					.getParameterId(OverloadControlManager.CONTENTION_LEVEL_THREE_ACTIVE_CALLS));
		} else if (m_currInPrgsCalls >= congLevelTwoActiveCalls
				&& !activeCallsContentionAlarm.get(1)) {
			maxActiveCallsLimitReached(ocmMgr
					.getParameterId(OverloadControlManager.CONTENTION_LEVEL_TWO_ACTIVE_CALLS));
		} else if (m_currInPrgsCalls >= congLevelOneActiveCalls
				&& !activeCallsContentionAlarm.get(0)) {
			maxActiveCallsLimitReached(ocmMgr
					.getParameterId(OverloadControlManager.CONTENTION_LEVEL_ONE_ACTIVE_CALLS));
		}
	}
	  
	/**
	 * Congestion control change 
	 * This method is used to raise Max Active Calls Limit Reached alarm
	 */
	public void maxActiveCallsLimitReached(int paramId) {

		// int alarmId = 0;
		String desc = "";
		if (m_l.isInfoEnabled()) {

			m_l.info("maxActiveCallsLimitReached called for paramId " + paramId);

		}
		if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_THREE_ACTIVE_CALLS)) {

			if (!activeCallsContentionAlarm.get(2)) {
				desc = "Contention level three for active calls reached";
				activeCallsContentionAlarm.set(2);
				this
						.raiseAlarmToEms(
								Constants.ALARM_CONTENTION_LEVEL_THREE_ACTIVE_CALLS_REACHED,
								desc);
			}
		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_TWO_ACTIVE_CALLS)) {

			if (!activeCallsContentionAlarm.get(1)) {
				desc = "Contention level two for active calls reached";
				this
						.raiseAlarmToEms(
								Constants.ALARM_CONTENTION_LEVEL_TWO_ACTIVE_CALLS_REACHED,
								desc);
				activeCallsContentionAlarm.set(1);
			}

			if (activeCallsContentionAlarm.get(2)) {

				desc = "contention level three for active calls cleared";
				this
						.raiseAlarmToEms(
								Constants.ALARM_CONTENTION_LEVEL_THREE_ACTIVE_CALLS_CLEARED,
								desc);
				activeCallsContentionAlarm.clear(2);
			}

		}
		if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_ONE_ACTIVE_CALLS)) {

			if (!activeCallsContentionAlarm.get(0)) {
				desc = "Contention level one for active calls reached";
				activeCallsContentionAlarm.set(0);
				this
						.raiseAlarmToEms(
								Constants.ALARM_CONTENTION_LEVEL_ONE_ACTIVE_CALLS_REACHED,
								desc);
			}

			if (activeCallsContentionAlarm.get(1)) {

				desc = "Contention level two for active calls cleared";
				this
						.raiseAlarmToEms(
								Constants.ALARM_CONTENTION_LEVEL_TWO_ACTIVE_CALLS_CLEARED,
								desc);
				activeCallsContentionAlarm.clear(1);
			}

			if (activeCallsContentionAlarm.get(2)) {
				desc = "Contention level three for active calls cleared";
				this
						.raiseAlarmToEms(
								Constants.ALARM_CONTENTION_LEVEL_THREE_ACTIVE_CALLS_CLEARED,
								desc);
				activeCallsContentionAlarm.clear(2);
			}

		}
	}

	/**
	 * Congestion control change 
	 * This method is used to clear Max Active Calls Limit Reached alarm
	 */
	public void maxActiveCallsLimitCleared(int paramId) {

		if (m_l.isInfoEnabled()) {

			m_l.info("maxActiveCallsLimitCleared called for paramId " + paramId);

		}
		String desc = "";
		int alarmId = 0;
		if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_ONE_ACTIVE_CALLS)) {

			if (activeCallsContentionAlarm.get(0)) {
				desc = "Contention level one for active calls cleared";
				this
						.raiseAlarmToEms(
								Constants.ALARM_CONTENTION_LEVEL_ONE_ACTIVE_CALLS_CLEARED,
								desc);
				activeCallsContentionAlarm.clear(0);
			}

			if (activeCallsContentionAlarm.get(2)) {
				desc = "Contention level three for active calls cleared";
				alarmId = Constants.ALARM_CONTENTION_LEVEL_THREE_ACTIVE_CALLS_CLEARED;
				activeCallsContentionAlarm.clear(2);
				this.raiseAlarmToEms(alarmId, desc);
			}

			if (activeCallsContentionAlarm.get(1)) {
				desc = "Contention level two for active calls cleared ";
				this
						.raiseAlarmToEms(
								Constants.ALARM_CONTENTION_LEVEL_TWO_ACTIVE_CALLS_CLEARED,
								desc);
				activeCallsContentionAlarm.clear(1);
			}

		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_TWO_ACTIVE_CALLS)) {

			if (activeCallsContentionAlarm.get(1)) {
				desc = "Contention level two for active calls cleared";

				this
						.raiseAlarmToEms(
								Constants.ALARM_CONTENTION_LEVEL_TWO_ACTIVE_CALLS_CLEARED,
								desc);
				activeCallsContentionAlarm.clear(1);
			}

			if (activeCallsContentionAlarm.get(2)) {
				desc = "Contention level three for active calls cleared";
				alarmId = Constants.ALARM_CONTENTION_LEVEL_THREE_ACTIVE_CALLS_CLEARED;
				activeCallsContentionAlarm.clear(2);
				this.raiseAlarmToEms(alarmId, desc);
			}

		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_THREE_ACTIVE_CALLS)) {

			if (activeCallsContentionAlarm.get(2)) {
				alarmId = Constants.ALARM_CONTENTION_LEVEL_THREE_ACTIVE_CALLS_CLEARED;
				activeCallsContentionAlarm.clear(2);
				desc = "Contention level three for active calls cleared";
				this.raiseAlarmToEms(alarmId, desc);
			}
		}

		// }
	}

   /**
    * (non-Javadoc)
    * @see com.baypackets.ase.ocm.SyncOverloadListener#maxLimitReached(com.baypackets.ase.spi.ocm.OverloadEvent)
    * updated for Congestion control change
    */
	public void maxLimitReached(OverloadEvent event) {
		super.maxLimitReached(event);
		int alarmId = 0;
		String desc="";
		int paramId = event.getOverloadParameter() != null ? event
				.getOverloadParameter().getId() : -1;

		
	//	if (m_l.isInfoEnabled()) {

			m_l.error("maxLimitReached " + paramId +" Value : "+event
					.getOverloadParameter().getValue());

	//	}
		
//		if (paramId == ocmMgr
//				.getParameterId(OverloadControlManager.PROTOCOL_SESSION_COUNT)) {
//
//			alarmId = this.m_priorityInstance ? Constants.NSEP_MAX_SESSION_REACHED
//					: Constants.ALARM_MAX_SESSION_REACHED;
//			if (this.m_priorityInstance) {
//				m_l
//						.error("SAS-ALARM: Max Priority Protocol Session Limit Reached");
//			} else {
//				m_l.error("SAS-ALARM: Max Protocol Session Limit Reached");
//			}
//		} else if (paramId == ocmMgr
//				.getParameterId(OverloadControlManager.APP_SESSION_COUNT)) {
//
//			alarmId = this.m_priorityInstance ? Constants.NSEP_MAX_APPSESSION_REACHED
//					: Constants.ALARM_MAX_APPSESSION_REACHED;
//			if (this.m_priorityInstance) {
//				m_l.error("SAS-ALARM: Max Priority App Session Limit Reached");
//			} else {
//				m_l.error("SAS-ALARM: Max App Session Limit Reached");
//			}
//		} else 
//			
		if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_ONE_CPU_USAGE)) {

			desc="Contention level one for cpu reached";
			alarmId = Constants.ALARM_CONTENTION_LEVEL_ONE_CPU_REACHED;
			
			cpuContentionAlarm.set(0);

		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_TWO_CPU_USAGE)) {

			alarmId = Constants.ALARM_CONTENTION_LEVEL_TWO_CPU_REACHED;
			
			desc="Contention level two for cpu reached";
			cpuContentionAlarm.set(1);
			
		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_THREE_CPU_USAGE)) {
			
			desc="Contention level three for cpu reached";
			alarmId = Constants.ALARM_CONTENTION_LEVEL_THREE_CPU_REACHED;
			cpuContentionAlarm.set(2);

		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_ONE_MEMORY_USAGE)) {

			desc="Contention level one for memory reached";
			alarmId = Constants.ALARM_CONTENTION_LEVEL_ONE_MEMORY_REACHED;
			memoryContentionAlarm.set(0);

		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_TWO_MEMORY_USAGE)) {

			desc="Contention level two for memory reached";
			
			alarmId = Constants.ALARM_CONTENTION_LEVEL_TWO_MEMORY_REACHED;
			memoryContentionAlarm.set(1);

		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_THREE_MEMORY_USAGE)) {
			
			if (!memoryContentionAlarm.get(0) && !memoryContentionAlarm.get(1)&& !memoryContentionAlarm.get(2)) {
				
				if (m_l.isDebugEnabled()) {
					m_l.debug("Max Limit Reached : id = " + paramId+ " Value : "+event
							.getOverloadParameter().getValue() +" is ignored as no level1 or level2 alarm yet raised .it may be a false alaram..");
				}
				
				falseL3AlarmRaised=true;
				memoryContentionAlarm.set(2);
				return;
			}
	
			// below not removed was just added and commented for testing
			
//			String filename = "heapDumpOnL3Alarm.bin";
//			String filePath=File.separator+"usr"+File.separator+"tmp"+File.separator+filename;
//			
//			try {
//				ManagementFactory.getDiagnosticMXBean().dumpHeap(filePath, true);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			falseL3AlarmRaised=false;
			memoryContentionAlarm.set(2);

			desc="Contention level three for memory reached";
			alarmId = Constants.ALARM_CONTENTION_LEVEL_THREE_MEMORY_REACHED;
			

		}
		
		if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.NETWORK_TRANSACTIONS_PER_SECOND)) {

//			desc="Network Transactions Limit Reached";
//			alarmId = Constants.ALARM_NETWORK_TRANSACTIONS_PER_SECOND_REACHED;
			
			networkTransactionsAlarm.set(0);
			return ;// alarm will be raised by performance counters fetched by ems for this

		}
		

		if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.AGGREGATED_TRANSACTIONS_PER_SECOND)) {

//			desc="Network Transactions Limit Reached";
//			alarmId = Constants.ALARM_AGGREGATED_NETWORK_TRANSACTIONS_PER_SECOND_REACHED;
			
			aggregatedTransactionsAlarm.set(0);
			return ;// alarm will be raised by performance counters fetched by ems for thi

		}
		
		if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.NEW_CALLS_PER_SECOND)) {
			
			newCallsPerSecAlarm.set(0);
			return ;

		}
		   
		   
		this.raiseAlarmToEms(alarmId,desc);
	}

	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.ocm.SyncOverloadListener#maxLimitCleared(com.baypackets.ase.spi.ocm.OverloadEvent)
	 * updated for Congestion control change
	 */
	public void maxLimitCleared(OverloadEvent event) {
		super.maxLimitCleared(event);
		int paramId = event.getOverloadParameter() != null ? event
				.getOverloadParameter().getId() : -1;
		int alarmId = 0;
		String desc ="";
		
		if (m_l.isDebugEnabled()) {
			m_l.debug("Max Limit Cleared: id = " + paramId+ " Value : "+event
					.getOverloadParameter().getValue());
		}

		if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_ONE_CPU_USAGE) && cpuContentionAlarm.get(0)) {

			alarmId = Constants.ALARM_CONTENTION_LEVEL_ONE_CPU_CLEARED;
			desc="Contention level one for cpu cleared";
			cpuContentionAlarm.clear(0);		

		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_TWO_CPU_USAGE)&& cpuContentionAlarm.get(1)) {

			alarmId = Constants.ALARM_CONTENTION_LEVEL_TWO_CPU_CLEARED;
			desc ="Contention level two for cpu cleared";
			cpuContentionAlarm.clear(1);
//			allowedActiveCalls=0;
//			rejectedActiveCalls=0;

		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_THREE_CPU_USAGE)&& cpuContentionAlarm.get(2)) {

			desc="Contention level three for cpu cleared";
			alarmId = Constants.ALARM_CONTENTION_LEVEL_THREE_CPU_CLEARED;
			cpuContentionAlarm.clear(2);
					

		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_ONE_MEMORY_USAGE)&& memoryContentionAlarm.get(0)) {

			desc="Contention level one for memory cleared";
			alarmId = Constants.ALARM_CONTENTION_LEVEL_ONE_MEMORY_CLEARED;
			memoryContentionAlarm.clear(0);

		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_TWO_MEMORY_USAGE)&& memoryContentionAlarm.get(1)) {

			alarmId = Constants.ALARM_CONTENTION_LEVEL_TWO_MEMORY_CLEARED;
			desc="Contention level two for memory cleared";
			memoryContentionAlarm.clear(1);
//			allowedActiveCalls=0;
//			rejectedActiveCalls=0;

		} else if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.CONTENTION_LEVEL_THREE_MEMORY_USAGE)&& memoryContentionAlarm.get(2)&&!falseL3AlarmRaised) {

			desc="Contention level three for memory cleared";
			alarmId = Constants.ALARM_CONTENTION_LEVEL_THREE_MEMORY_CLEARED;
			memoryContentionAlarm.clear(2);

		} 
		
		if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.NETWORK_TRANSACTIONS_PER_SECOND)) {

//			desc="Network Transactions Limit Cleared";
//			alarmId = Constants.ALARM_NETWORK_TRANSACTIONS_PER_SECOND_CLEARED;
			
			networkTransactionsAlarm.clear(0);
			return ;// alarm will be raised by performance counters fetched by ems for this

		}

		if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.AGGREGATED_TRANSACTIONS_PER_SECOND)) {

//			desc="Aggregated Transactions Limit Cleared";
//			alarmId = Constants.ALARM_AGGREGATED_NETWORK_TRANSACTIONS_PER_SECOND_CLEARED;
			
			aggregatedTransactionsAlarm.clear(0);
			return; // alarm will be raised by performance counters fetched by ems for this

		}
		
		
		if (paramId == ocmMgr
				.getParameterId(OverloadControlManager.NEW_CALLS_PER_SECOND)) {

//			desc="Aggregated Transactions Limit Cleared";
//			alarmId = Constants.ALARM_AGGREGATED_NETWORK_TRANSACTIONS_PER_SECOND_CLEARED;
			
			newCallsPerSecAlarm.clear(0);
			return; // alarm will be raised by performance counters fetched by ems for this

		}

		this.raiseAlarmToEms(alarmId,desc);
	}

	/**
	 * Check whether the system is in overload condition. If
	 * <code>m_currMaxInPrgsCalls</code> is crossed over for the first time, an
	 * alarm for system overload reached is sent to EMS agent. If <code>
	 * m_currInPrgsCalls</code>
	 * is within a lower threashold which is calculated by
	 * <code>m_currMaxInPrgsCalls*ratio</code>, an alarm for system overload
	 * cleared is sent only if an alarm for system overload reached is sent
	 * before.
	 * 
	 */
	private void reportOverloadCondition() {
		if (m_lastOlf >= m_maxNormalOlf * (1 + overloadRatio)) {
			if (!alarmed) {
				alarmed = true;
				// report system overload reached
				try {
					int tmp = this.m_priorityInstance ? Constants.NSEP_OVERLOAD_REACHED
							: OVERLOAD_REACHED;
					if (this.m_priorityInstance) {
						m_l.error("SAS-ALARM: Priority Overload Reached");
					} else {
						m_l.error("SAS-ALARM: Overload Reached");
					}
					this.raiseAlarmToEms(tmp);
				} catch (Exception ex) {
					m_l.error(ex);
				}
			}
		} else if (m_lastOlf >= m_maxNormalOlf * (1 - overloadRatio)) {
			// do nothing
		} else {
			if (alarmed) {
				alarmed = false;
				// report system overload cleared
				try {
					int tmp = this.m_priorityInstance ? Constants.NSEP_OVERLOAD_CLEARED
							: OVERLOAD_CLEARED;
					if (this.m_priorityInstance) {
						m_l.error("SAS-ALARM: Priority Overload Cleared");
					} else {
						m_l.error("SAS-ALARM: Overload Cleared");
					}
					this.raiseAlarmToEms(tmp);
				} catch (Exception ex) {
					m_l.error(ex);
				}
			}
		}

	}

	private void reportCallGapping(boolean status, String reason) {
		if (status) {
			if (this.m_priorityInstance) {
				m_l
						.error("SAS-ALARM: Call Gapping Started.; Reason: "
								+ reason);
			} else {
				// Only to distinguish for debugging
				m_l.error("SAS-ALARM: Call Gapping Started; Reason: " + reason);
			}
			this.raiseAlarmToEms(Constants.ALARM_CALL_GAPPING_STARTED);
		} else {
			if (this.m_priorityInstance) {
				m_l.error("SAS-ALARM: Call Gapping Ended.");
			} else {
				// Only to distinguish for debugging
				m_l.error("SAS-ALARM: Call Gapping Ended");
			}
			this.raiseAlarmToEms(Constants.ALARM_CALL_GAPPING_ENDED);
		}
	}

	private void reportPriorityCallGapping(boolean status, String reason) {
		if (status) {
			m_l.error("SAS-ALARM: Priority Call Gapping Started; Reason: "
					+ reason);
			this.raiseAlarmToEms(Constants.NSEP_CALL_GAPPING_STARTED);
		} else {
			m_l.error("SAS-ALARM: Priority Call Gapping Ended");
			this.raiseAlarmToEms(Constants.NSEP_CALL_GAPPING_ENDED);
		}

	}

       private void raiseAlarmToEms(int alarmId,String desc) {
            try {
                BaseContext.getAlarmService().sendAlarm(alarmId, desc); 
                m_l.error("Alarm " + alarmId + " is reported "+ desc);
           
            } catch (Exception ex) {
                    m_l.error(ex);
            }
    } 

	private void raiseAlarmToEms(int alarmId) {
		try {
			BaseContext.getAlarmService().sendAlarm(alarmId, "");
			if (m_l.isInfoEnabled()) {
				m_l.info("Alarm " + alarmId + " is reported");
			}
		} catch (Exception ex) {
			m_l.error(ex);
		}
	}

	private void reportInviteLimit(boolean priority) {
		if (m_currInPrgsCalls >= m_confMaxInPrgsCalls) {
			if (!invitesCountAlarmed) {
				invitesCountAlarmed = true;
				int alarmId = priority ? Constants.NSEP_MAX_INVITES_REACHED
						: Constants.ALARM_MAX_INVITES_REACHED;
				if (priority) {
					m_l
							.error("SAS-ALARM: Max Priority In-progress INVITEs Reached");
				} else {
					m_l.error("SAS-ALARM: Max In-progress INVITEs Reached");
				}
				this.raiseAlarmToEms(alarmId);
			}
		} else if (m_currInPrgsCalls < m_confMaxInPrgsCalls
				* (1 - ocmAlarmHysteresis)) {
			invitesCountAlarmed = false;
		}
	}

	/**
	 * This method is called by AseStackInterfaceLayer to measure application
	 * session wide response time and report the latest value to
	 * OverloadControlManager
	 * 
	 * @param stage
	 *            indicates MESSAGE_IN or MESSAGE_OUT
	 * @param msg
	 *            an object implements TimeMeasurement interface
	 */
	public void measureResponseTime(int stage, TimeMeasurement msg) {
		//
		// Setting timestamp before OCM enable check since same timestamp will
		// be used
		// to set delay for generating 100 Trying responses. (ISC Reqmnt)
		//
		
		// Congestion Control handling
	//	return;
		
		msg.setTimestamp(System.currentTimeMillis());

		if (!profilingEnabled && !overloadControlEnabled) {
			return;
		}

		if (m_l.isDebugEnabled()) {
			m_l.debug("Response time measurement start...");
		}

		if (stage == MESSAGE_OUT) {
			SasProtocolSession protocolSession = null;
			if (msg instanceof AseSipServletRequest) {
				protocolSession = ((AseSipServletRequest) msg)
						.getProtocolSession();
			} else if (msg instanceof AseSipServletResponse) {
				protocolSession = ((AseSipServletResponse) msg)
						.getProtocolSession();
			}
			if (protocolSession == null) {

				if (m_l.isDebugEnabled()) {
					m_l.debug("ProtocolSession is not found");
				}

				return;
			}

			((AseProtocolSession) protocolSession).setTimestamp(msg);

			AseApplicationSession appSession = (AseApplicationSession) protocolSession
					.getApplicationSession();
			if (appSession == null) {

				if (m_l.isDebugEnabled()) {
					m_l.debug("ApplicationlSession is not found");
				}

				return;
			}

			Double responseTime = appSession.getResponseTimeRatio();
			if (responseTime == null) {

				if (m_l.isDebugEnabled()) {
					m_l.debug("Response time is not found");
				}

				return;
			}

			StringBuffer logMsg = new StringBuffer("\n"
					+ formater.format(responseTime));
			if (ocmMethod == ACCEPTANCE_RATE) {
				logMsg.append("\t" + formater.format(curRate));
			} else {
				logMsg.append("\t" + m_currMaxInPrgsCalls);
			}
			logMsg.append("\t" + formater.format(m_lastOlf));
			logMsg.append("\t" + m_currInPrgsCalls);
			logMsg.append("\t" + nAllowedCalls);
			logMsg.append("\t" + nDroppedCalls);
			logMsg.append("\t" + nOverMaxCalls);
			/*
			 * if (nSamples == confNumSamples) { logMsg.append("\t" +
			 * (long)nDroppedCalls/nAllowedCalls); logMsg.append("\t" +
			 * (long)(nDroppedCalls + nOverMaxCalls)/nAllowedCalls);
			 * nAllowedCalls = 0; nDroppedCalls = 0; nOverMaxCalls = 0; }
			 */
			logMeasurement(logMsg.toString());

			if (!overloadControlEnabled) {
				return;
			}

			try {
				lock.lockInterruptibly();
			} catch (InterruptedException ex) {
				m_l.error("Error in acquiring lock");
				return;
			}

			try {
				Float updatedTimeRatio = null;

				if (nSamples < confNumSamples) {
					// save response time sample
					normalTimeRatios[nSamples++] = responseTime.doubleValue();

					if (m_l.isDebugEnabled()) {
						m_l.debug("AppSession " + appSession.getAppSessionId()
								+ " response time = " + responseTime);
					}

				}
				// after nSamples, calculate the latest smoothed response time
				if (nSamples == confNumSamples) {

					if (m_l.isDebugEnabled()) {
						m_l.debug("Update threshold...");
					}

					Arrays.sort(normalTimeRatios);
					int index = (int) ((normalTimeRatios.length - 1) * confPct);
					double curTimeRatio = normalTimeRatios[index];
					smoothTimeRatio = curTimeRatio * confSmoothFactor
							+ smoothTimeRatio * (1 - confSmoothFactor);
					updatedTimeRatio = new Float(smoothTimeRatio);
					if (m_l.isDebugEnabled()) {
						m_l.debug("curTimeDiff=" + curTimeRatio
								+ ", smoothTimeDiff=" + smoothTimeRatio);
					}

					nSamples = 0;
				}

				if (updatedTimeRatio != null) {
					ocmMgr.update(ocmId, updatedTimeRatio.floatValue(),
							this.m_priorityInstance);
					ocmMgr.checkOlf(this.m_priorityInstance);
				}

			} finally {
				try {
					lock.unlock();
				} catch (Error er) {
					m_l.error("Error in releasing lock.", er);
				}
			}
		}

		if (m_l.isDebugEnabled()) {
			m_l.debug("Response time measurement is done");
		}

	}

	private static double confMinDelay = 0;
	private int bucketSize = 10;
	private double maxRate = 5000;
	private double minRate = 0.05;
	private double curRate = 10;
	private long lastTime;
	private double tokenCount;

	public void adjustAcceptanceRate() {
		if (m_lastOlf == 0)
			return;

		try {
			lock.lockInterruptibly();
		} catch (InterruptedException ex) {
			m_l.error("Error in acquiring lock", ex);
			return;
		}

		try {
			if (m_lastOlf < m_maxNormalOlf) {
				// increase acceptance rate
				curRate += (m_maxNormalOlf / m_lastOlf) * confIncFactor;
				if (curRate > maxRate) {
					curRate = maxRate;
				}

				if (m_l.isInfoEnabled()) {
					m_l.info("Acceptance rate is increased to " + curRate);
				}

			} else {
				// decrease acceptance rate
				curRate = (m_maxNormalOlf / m_lastOlf) * curRate
						/ confDecFactor;
				if (curRate < minRate) {
					curRate = minRate;
				}
			}
		} finally {
			try {
				lock.unlock();
			} catch (Error er) {
				m_l.error("Error in releasing lock.", er);
			}
		}
	}

	public boolean isAllowedByRate(boolean priority) {
		long curtime = System.currentTimeMillis();
		long delay = curtime - lastTime;
		boolean allowed = false;

		try {
			lock.lockInterruptibly();
		} catch (InterruptedException ex) {
			m_l.error("Error in acquiring lock", ex);
			return false;
		}

		try {
			if (delay >= confMinDelay) {
				double nTokens = delay / (1000 * 1.0 / curRate);
				tokenCount += nTokens;
				if (tokenCount > bucketSize)
					tokenCount = bucketSize;
				lastTime = curtime;
			}

			if (tokenCount >= 1) {
				tokenCount--;
				// ++m_currInPrgsCalls;
				// nAllowedCalls++;
				allowed = true;
			} else {
				boolean tmpPriority = priority && this.m_priorityInstance;
				if (m_l.isInfoEnabled()) {
					m_l
							.info("Overload - call rejected by current rate: Priority = "
									+ tmpPriority);
				}
				// nDroppedCalls++;
				if (tmpPriority) {
					if (!priorityCallGappingStarted) {
						synchronized (syncObj) {
							priorityCallGappingStarted = true;
						}
						this.reportCallGapping(tmpPriority,
								"Current Rate Criterion");
					}
				} else {
					synchronized (syncObj) {
						if (!callGappingStarted) {
							callGappingStarted = true;
							this.reportCallGapping(tmpPriority,
									"Current Rate Criterion");
						}
					}
				}
			}
		} finally {
			try {
				lock.unlock();
			} catch (Error er) {
				m_l.error("Error in releasing lock.", er);
			}
		}
		return allowed;
	}

	public boolean isAllowedByPercentage() {
		double random = Math.random();
		if (random <= this.pctCallDrops) {
			if (m_l.isInfoEnabled()) {
				m_l.info("Call dropped by " + this.pctCallDrops * 100 + " %");
			}
			nDroppedCalls++;
			synchronized (syncObj) {
				if (!callGappingStarted) {
					callGappingStarted = true;
					this.reportCallGapping(false,
							"Call Drop Percentage Criterion");
				}
			}
			return false;
		} else {
			++m_currInPrgsCalls;
			nAllowedCalls++;
			synchronized (syncObj) {
				if (callGappingStarted) {
					callGappingStarted = false;
					this.reportCallGapping(false, null);
				}
			}
			return true;
		}
	}

	/**
	 * This method must be invoked only if Call Priority is enabled and Listener
	 * is for Priority calls
	 */
	public void setNormalCallListener(AseSipOverloadManager list) {
		if (this.m_priorityInstance) {
			this.m_normalCallListener = list;
		}
	}

	/**
	 * Changes the Component State to the state indicated by the argument
	 * passed. The states are changed according to the priority values.
	 **/
	public void changeState(MComponentState componentState)
			throws UnableToChangeStateException {
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
			if (name.equals(Constants.OID_MAX_SIP_IN_PROGRESS_CALLS)) {
				if (value != null) {
					setMaxInPrgsCalls(Integer.parseInt(value));
				}
			} else if (name.equals(Constants.OID_MAX_NORMAL_OLF)) {
				if (value != null) {
					setMaxNormalOlf(Float.parseFloat(value));
				}
			} else if (name.equals(Constants.OID_PERCENTAGE_CALL_DROP)) {
				if (value != null) {
					setPercentageCallDrop(Integer.parseInt(value));
				}
			}
			
			     if (name.equals(Constants.OID_CONTENTION_LEVEL_ONE_ACTIVE_CALLS)){
					if (value != null) 
						congLevelOneActiveCalls =Integer.parseInt(value);
						
					}
					
		            if (name.equals(Constants.OID_CONTENTION_LEVEL_TWO_ACTIVE_CALLS)){
					if (value != null) 
						congLevelTwoActiveCalls = Integer.parseInt(value);
						
					}
					
		            if (name.equals(Constants.OID_CONTENTION_LEVEL_THREE_ACTIVE_CALLS)){
					if (value != null) 
						congLevelThreeActiveCalls =Integer.parseInt(value);
						
					}

			if (m_l.isEnabledFor(Level.INFO)) {
				m_l.info(name + " is set to " + value);
			}
		}
	}

	private void setMaxInPrgsCalls(int max) {
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException ex) {
			m_l.error("Error in acquiring lock. Not allowing the call.", ex);
			return;
		}

		try {
			m_confMaxInPrgsCalls = max;
		} finally {
			try {
				lock.unlock();
			} catch (Error ex) {
				m_l.error("Error in releasing lock.", ex);
			}
		}
	}

	private synchronized void setMaxNormalOlf(float max) {
		try {
			lock.lockInterruptibly();
		} catch (InterruptedException ex) {
			m_l.error("Error in acquiring lock. Not allowing the call.");
			m_l.error(ex.getMessage(), ex);
			return;
		}

		m_maxNormalOlf = max;

		try {
			lock.unlock();
		} catch (Error ex) {
			m_l.error("Error in releasing lock.");
			m_l.error(ex.getMessage(), ex);
		}
	}

	private void setPercentageCallDrop(int pctCallDrops) {
		this.pctCallDrops = pctCallDrops / 100.0;
	}

	private void logMeasurement(String logMsg) {
		try {
			FileWriter writer = new FileWriter(perfFilename, true);
			writer.write(logMsg.toString());
			writer.flush();
			writer.close();
		} catch (IOException ex) {
			m_l.error(ex);
		}
	}

	private String parametersToString() {
		StringBuffer buf = new StringBuffer("\nOverload Parameters:\n");
		buf.append("Max in-progress calls = " + m_confMaxInPrgsCalls + "\n");
		buf.append("Sampling interval = " + confNumSamples + "\n");
		buf.append("Target percentage = " + confPct + "\n");
		buf.append("Smooth factor = " + confSmoothFactor + "\n");
		buf.append("Increase factor = " + confIncFactor
				+ ", Decrease factor = " + confDecFactor + "\n");
		buf.append("Max Normal OLF = " + m_maxNormalOlf + "\n");
		buf.append("\n");
		return buf.toString();
	}

	private String getOverloadReasons(BitSet ps) {
		StringBuffer buff = new StringBuffer();

//		for (int idx = 0; idx < ps.length(); ++idx) {
//			if (ps.get(idx)) {
//				if (idx == OverloadControlManager.CPU_USAGE_ID) {
//					buff.append("CPU usage exceeded; ");
//				} else if (idx == OverloadControlManager.PROTOCOL_SESSION_COUNT_ID) {
//					buff.append("Protocol-session count exceeded; ");
//				} else if (idx == OverloadControlManager.APP_SESSION_COUNT_ID) {
//					buff.append("App-session count exceeded; ");
//				} else if (idx == OverloadControlManager.RESPONSE_TIME_ID) {
//					buff.append("Response time exceeded; ");
//				} else if (idx == OverloadControlManager.MEMORY_USAGE_ID) {
//					buff.append("Memory usage exceeded; ");
//				}
//			}
//		}

		return buff.toString();
	}

	class CallGappingHandler implements CommandHandler {
		public String execute(String command, String[] args, InputStream in,
				OutputStream out) throws CommandFailedException {

			if (args.length != 1) {
				return getUsage(command);
			}

			try {
				setPercentageCallDrop(Integer.parseInt(args[0]));
			} catch (NumberFormatException nfe) {
				return "Invalid argument: " + args[0] + " [Not A Number] ";
			}

			return pctCallDrops * 100 + "% calls will be dropped.\n";
		}

		public String getUsage(String command) {
			return "Usage: " + command + " <percentage>";
		}
	}

	// -- data members --
	/**
	 * Overload control manager
	 */

	private AseSipOverloadManager m_normalCallListener = null;
	private OverloadControlManager ocmMgr;
	private int ocmId;
	private boolean m_priorityInstance;
	private boolean invitesCountAlarmed = false;
	private boolean callGappingAlarmed = false;
	private static boolean callGappingStarted = false;
	private static boolean priorityCallGappingStarted = false;
	private static Object syncObj = new Object();

	private float ocmAlarmHysteresis = (float) 0.10;

	/**
	 * Configurable percentage value of call drpping
	 */
	private double pctCallDrops = 0;

	/**
	 * Configured Maximum Allowed In-Progress Calls.
	 */
	private int m_confMaxInPrgsCalls = 0;

	/**
	 * Current Maximum Allowed In-Progress Calls.
	 */
	private int m_currMaxInPrgsCalls = 0;

	/**
	 * Used to calculate low threshold of in progress calls
	 */
	private float overloadRatio = (float) 0.25;

	/**
	 * Flag if system overload is in alarming condition
	 */
	private boolean alarmed = false;

	/**
	 * Current In-Progress Calls' count.
	 */
	private int m_currInPrgsCalls = 0;

	/**
	 * Maximum "normal" OLF. The idea is to contain the OLF for the system
	 * within this value.
	 */
	private float m_maxNormalOlf = (float) 0.5;

	/**
	 * Last value of OLF. This value is used to determine the increase in Max
	 * Current In Progress Calls, in case the Current In Progress calls hit the
	 * barrer while OLF is below Max Normal value.
	 */
	private float m_lastOlf = (float) 0.0;

	/**
	 * Variables for response time based overload control
	 */
	private int confNumSamples = 10;
	private double confPct = 0.9;
	private double confSmoothFactor = 0.7;
	private double confIncFactor = 2.0;
	private double confDecFactor = 1.1;

	private double[] normalTimeRatios;
	private int nSamples = 0;
	private double smoothTimeRatio;

	private int nAllowedCalls = 0;
	private int nDroppedCalls = 0;
	private int nOverMaxCalls = 0;

	private boolean profilingEnabled = true;
	private int ocmMethod = IN_PRGS_COUNTER;

	private static final int IN_PRGS_COUNTER = 0;
	private static final int ACCEPTANCE_RATE = 1;

	/**
	 * Reentrant Lock for adjusting the Current Max In Progress calls'
	 * threshold.
	 */
	private ReentrantLock lock = new ReentrantLock();

	/**
	 * Initialization flag
	 */
	private boolean m_isInitialized;

	// logger instance for the class
	private static Logger m_l = Logger.getLogger(AseSipOverloadManager.class
			.getName());

	/**
	 * Alarm IDs
	 */
	private static final int OVERLOAD_REACHED = Constants.ALARM_OVERLOAD_REACHED;
	private static final int OVERLOAD_CLEARED = Constants.ALARM_OVERLOAD_CLEARED;

	/**
	 * Transaction stage
	 */
	public static final int MESSAGE_IN = 0;
	public static final int MESSAGE_OUT = 1;

	// private static String perfFilename = Constants.ASE_HOME + File.separator
	// + Constants.FILE_LOG_DIR + File.separator + "ocm.log";
	// Commented by NJADAUN
	private static String perfFilename = Constants.FILE_LOG_DIR
			+ File.separator + "ocm.log";

	private static NumberFormat formater = NumberFormat.getInstance();
	static {
		formater.setMaximumFractionDigits(3);
	}
}
