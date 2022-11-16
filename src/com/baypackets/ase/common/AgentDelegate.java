/*
 * AgentDelegate.java
 *
 * Created on July 18, 2005
 *
 */
package com.baypackets.ase.common;

import RSIEmsTypes.ConfigurationDetail;

import com.agnity.oems.agent.messagebus.meascounters.OemsAgent;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.UnableToModifyConfigurationParamsException;
import com.baypackets.ase.util.threadpool.ThreadPool;
import com.baypackets.ase.util.threadpool.WorkHandler;
import com.baypackets.ase.util.threadpool.Queue;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.emsagent.EmsAgent;
import com.baypackets.emsliteagent.EmsLiteAgent;
import com.baypackets.emsliteagent.EmsLiteConfigurationDetail;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;

import org.apache.log4j.Logger;

/**
 * This class provides a wrapper for the EmsAgent object.  A singleton instance
 * of this class is available in the Registry bound under the name defined
 * by Constants.NAME_AGENT_DELEGATE.  Each of the methods defined in this class
 * will perform a NULL check on the return value of BaseContext.getAgent().  If
 * the platform is running without EMS, that call will always return NULL in 
 * which case the AgentDelegate method that was invoked will simply return.  
 * If the EmsAgent was returned by the call to BaseContext.getAgent() (i.e. if 
 * the platform is running with EMS), then the appropriate method on the 
 * EmsAgent class will be delegated to.
 *
 * @see com.baypackets.ase.common.Registry
 * @see com.baypackets.ase.util.Constants
 * @see com.baypackets.emsagent.EmsAgent
 * @see com.baypackets.bayprocessor.slee.common.BaseContext
 */
public final class AgentDelegate  implements WorkHandler, ThreadOwner, MComponent {

	private static Logger _logger = Logger.getLogger(AgentDelegate.class);
	private ThreadPool threadPool = null;
	private static final int poolSize = 1;
	OemsAgent oemsAgent=null;


	/**
	 * Returns "true" if the platform is running with EMS or returns "false"
	 * otherwise.
	 */
	public boolean isEmsEnabled() {
		return BaseContext.getAgent() != null;
	}
	public boolean isEmslEnabled() {
		return BaseContext.getEmslagent() != null;
	}

	/**
	 * Method from MComponent Interface.
	 */
	public void changeState(MComponentState state) throws UnableToChangeStateException  {
		try {
			if(_logger.isInfoEnabled()) {
				_logger.info("Entering changeState(): state = "+state.getValue());
			}
			if(state.getValue() == MComponentState.LOADED) {
				this.initialize();
			} else if(state.getValue() == MComponentState.RUNNING){
				this.start();
			} else if(state.getValue() == MComponentState.STOPPED){
				this.stop();
			}
		} catch(Exception exp) {
			_logger.error(exp.getMessage(), exp);
		}
	}

	public void updateConfiguration(Pair[] configData, OperationType opType)
		throws UnableToUpdateConfigException {
		// No op
	}

	private void initialize() {
		if(_logger.isInfoEnabled()) {
			_logger.info(" Creating AgentDelegation ThreadPool");
		}
		try {
			ConfigRepository configRepository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			String tmpQSize = (String)configRepository.getValue(Constants.DELEGATION_QUEUE_SIZE);
			this.threadPool = new ThreadPool(poolSize, true, "AgentDelegation",this, this, 100);
			ThreadMonitor tm = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);
			this.threadPool.setThreadMonitor(tm);
			if(tmpQSize != null) {
				this.threadPool.setMaxQueueSize(Integer.parseInt(tmpQSize));
			}
			this.threadPool.setPolicy(Queue.POLICY_DROP_LAST);
			
			//this.threadPool.start();
		} catch(Exception e) {
			_logger.error(e.getMessage(), e);
		}
	}

	private void start() {
		if(_logger.isDebugEnabled()) {
			_logger.debug("Starting AgentDelegation ThreadPool and getting oemsAgent");
		}
		
		oemsAgent=(OemsAgent)Registry.lookup(Constants.OEMS_AGENT);
		
		if(_logger.isDebugEnabled()) {
			_logger.debug("OemsAgent is  " + oemsAgent);
		}
		this.threadPool.start();
	}

	private void stop() {
		this.threadPool.shutdown();
	}
		
	/**
	 * Delegates to EmsAgent.modifyCfgParam() if the platform is currently
	 * running with EMS or simply returns otherwise.
	 *
	 * @see com.baypackets.emsagent.EmsAgent#modifyCfgParam(ConfigurationDetail)
	 */
	public void modifyCfgParam(ConfigurationDetail detail) throws UnableToModifyConfigurationParamsException {
		boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger.debug("modifyCfgParam() called on AgentDelegate class...");
		}
		
		if (BaseContext.getAgent() != null) {
			if (loggerEnabled) {
				_logger.debug("modifyCfgParam(): Platform is running w/ EMS, so delegating call to EmsAgent class...");
			}
			AgentDelegationMessage message = new AgentDelegationMessage(AgentDelegationMessage.MSG_TYPE_MODIFY_CFG, detail);
			this.threadPool.submit(message);

		} else if (loggerEnabled) {
			_logger.debug("modifyCfgParam(): Platform is NOT running w/ EMS, so taking no action.");
		}
	}

	/**
	 * Delegates to EmsLiteAgent.modifyCfgParam() if the platform is currently
	 * running with EMSLite or simply returns otherwise.
	 *
	 * @see com.baypackets.emsliteagent.EmsLiteAgent#modifyCfgParam(ConfigurationDetail)
	 */
	public void modifyCfgParam(EmsLiteConfigurationDetail detail) throws UnableToModifyConfigurationParamsException {
		boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger.debug("modifyCfgParam() called on AgentDelegate class...");
		}
		
		if (BaseContext.getEmslagent() != null) {
			if (loggerEnabled) {
				_logger.debug("modifyCfgParam(): Platform is running w/ EMS, so delegating call to EmsAgent class...");
			}
			AgentDelegationMessage message = new AgentDelegationMessage(AgentDelegationMessage.MSG_TYPE_MODIFY_CFG, detail);
			this.threadPool.submit(message);

		} else if (loggerEnabled) {
			_logger.debug("modifyCfgParam(): Platform is NOT running w/ EMS, so taking no action.");
		}
	}

	/**
	 * Delegates to EmsAgent.reportCallHistoryInfo() if the platform is currently
	 * running with EMS or simply returns otherwise.
	 * Delegates to EmsLiteAgent.reportCallHistoryInfo() if the platform is currently
	 * running with EMSLite or simply returns otherwise.
	 * @see com.baypackets.emsagent.EmsAgent#reportCallHistoryInfo(String,String,int,int,int)
	 *  @see com.baypackets.emsliteagent.EmsAgent#reportCallHistoryInfo(String,String,int,int,int)
	 */
	public void reportCallHistoryInfo(String callId, String message, int constraintId, int isTestCall, int callState,String caller,String called) {
    boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger.debug("reportCallHistoryInfo(5) called on AgentDelegate class...");
		}

		if (BaseContext.getAgent() != null) {
			if (loggerEnabled) {
				_logger.debug("reportCallHistoryInfo(): Platform is running w/ EMS, so delegating call to EmsAgent class...");
			}
			AgentDelegationMessage agentMessage = new AgentDelegationMessage(AgentDelegationMessage.MSG_TYPE_REPORT_CALL,
																		callId,
																		message,
																		constraintId,
																		isTestCall,
																		callState,caller,called);
			this.threadPool.submit(agentMessage);
		}else if(BaseContext.getEmslagent() != null){
			if (loggerEnabled) {
				_logger.debug("reportCallHistoryInfo(): Platform is running w/ wEMS, so delegating call to wEMS Agent ");
			}
			AgentDelegationMessage agentMessage = new AgentDelegationMessage(AgentDelegationMessage.MSG_TYPE_REPORT_CALL,
																		callId,
																		message,
																		constraintId,
																		isTestCall,
																		callState,caller,called);
			this.threadPool.submit(agentMessage);
		}else if(oemsAgent != null){
			if (loggerEnabled) {
				_logger.debug("reportCallHistoryInfo(): Platform is running w/ wEMS, so delegating call to Oems Agent ");
			}
			AgentDelegationMessage agentMessage = new AgentDelegationMessage(AgentDelegationMessage.MSG_TYPE_REPORT_CALL,
																		callId,
																		message,
																		constraintId,
																		isTestCall,
																		callState,caller,called);
			this.threadPool.submit(agentMessage);
		}else if (loggerEnabled) {
			_logger.debug("reportCallHistoryInfo(): Platform is NOT running w/ EMS, so taking no action.");
		}
	}


	/**
	 * Delegates to EmsAgent.reportCallHistoryInfo() if the platform is currently
	 * running with EMS or simply returns otherwise.
	 *
	 * @see com.baypackets.emsagent.EmsAgent#reportCallHistoryInfo(String,String,int,int)
	 */
	public void reportCallHistoryInfo(String callId, String message, int isTestCall, int callState) {
		boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger.debug("reportCallHistoryInfo() called on AgentDelegate class...");
		}

		if (BaseContext.getAgent() != null) {
			if (loggerEnabled) {
				_logger.debug("reportCallHistoryInfo(4): Platform is running w/ EMS, so delegating call to EmsAgent class...");
			}
			AgentDelegationMessage agentMessage = new AgentDelegationMessage(AgentDelegationMessage.MSG_TYPE_REPORT_CALL,
																		callId,
																		message,
																		isTestCall,
																		callState);
			this.threadPool.submit(agentMessage);
		}else if(BaseContext.getEmslagent() != null){
			if (loggerEnabled) {
				_logger.debug("reportCallHistoryInfo(4): Platform is running w/ wEMS, so delegating call to wEMS Agentclass...");
			}
			AgentDelegationMessage agentMessage = new AgentDelegationMessage(AgentDelegationMessage.MSG_TYPE_REPORT_CALL,
																		callId,
																		message,
																		isTestCall,
																		callState);
			this.threadPool.submit(agentMessage);
		}else if(oemsAgent != null){
			if (loggerEnabled) {
				_logger.debug("reportCallHistoryInfo(): Platform is running w/ wEMS, so delegating call to Oems Agent ");
			}
			AgentDelegationMessage agentMessage = new AgentDelegationMessage(AgentDelegationMessage.MSG_TYPE_REPORT_CALL,
																		callId,
																		message,
																		isTestCall,
																		callState);
			this.threadPool.submit(agentMessage);
		}else if (loggerEnabled) {
			_logger.debug("reportCallHistoryInfo(): Platform is NOT running w/ EMS, so taking no action.");
		}
	}
	

	/**
	 * Delegates to EmsAgent.notifyLink() if the platform is currently 
	 * running with EMS or simply returns otherwise.
	 */
	public void notifyLink(int host, RSIEmsTypes.ConnectionState state) {
		boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger.debug("notifyLink() called on AgentDelegate class...");
		}

		if (BaseContext.getAgent() != null) {
			if (loggerEnabled) {
				_logger.debug("notifyLink(): Platform is running w/ EMS, so delegating call to EmsAgent class...");
			}
			AgentDelegationMessage message = new AgentDelegationMessage(AgentDelegationMessage.MSG_TYPE_NOTIFY_LINK,
																		host,
																		state);
			this.threadPool.submit(message);
		} else if (loggerEnabled) {
			_logger.debug("notifyLink(): Platform is NOT running w/ EMS, so taking no action.");
		}
	}

	
	/**
	 * Delegates to EmsAgent.getSubnetMask() if the platform is currently
	 * running with EMS or simply returns otherwise.
	 *
	 * @see com.baypackets.emsagent.EmsAgent#notifyLan(String)
	 */
	public int getSubnetMask(String hostName) {
		boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger.debug("getSubnetMask() called on AgentDelegate class...");
		}

		if (BaseContext.getAgent() != null) {
			if (loggerEnabled) {
				_logger.debug("getSubnetMask(): Platform is running w/ EMS, so delegating call to EmsAgent class...");
			}
			return BaseContext.getAgent().getSubnetMask(hostName);
		} else if (loggerEnabled) {
			_logger.debug("getSubnetMask(): Platform is NOT running w/ EMS, so taking no action.");
		}
		return 0;
	}


	/**
	 *
	 */
	public void notifyLan(int ipAddress, int subnetMask, RSIEmsTypes.ConnectionState state) {
		boolean loggerEnabled = _logger.isDebugEnabled();

		if (loggerEnabled) {
			_logger.debug("notifyLan() called on AgentDelegate class...");
		}

		if (BaseContext.getAgent() != null) {
			if (loggerEnabled) {
				_logger.debug("notifyLan(): Platform is running w/ EMS, so delegating call to EmsAgent class...");
			}
			
			AgentDelegationMessage message = new AgentDelegationMessage(AgentDelegationMessage.MSG_TYPE_NOTIFY_LINK,
																		ipAddress,
																		state);
			this.threadPool.submit(message);
		} else if (loggerEnabled) {
			_logger.debug("notifyLan(): Platform is NOT running w/ EMS, so taking no action.");
		}
	}

	/**
	 * Callback as thread owner.
	 */
	public int threadExpired(MonitoredThread thread) {
		if(_logger.isInfoEnabled()) {
			_logger.info(thread.getName() + " expired");
		}

		// Calling ThreadPool's method as logic for min percentage of common
		// thread required, lies there
		return threadPool.threadExpired(thread);
	}


	public void execute(Object obj) {

		if (obj == null || !(obj instanceof AgentDelegationMessage)){
			_logger.error("Queued object is NULL or not an instance of AgentDelegationMessage: Object = "+obj);
			return;
		}

		AgentDelegationMessage message = (AgentDelegationMessage)obj;
		EmsAgent agent = BaseContext.getAgent();
		EmsLiteAgent emslAgent = BaseContext.getEmslagent();
		if(agent == null && emslAgent == null && oemsAgent==null) {
			_logger.error("Returning: EmsAgent is NULL. Message Type = "+message.getMessageType());
			return;
		}
		if(_logger.isDebugEnabled()) {
			_logger.debug("Inside  execute(): Message Type = "+message.getMessageType());
		}

		switch(message.getMessageType()) {

			case AgentDelegationMessage.MSG_TYPE_MODIFY_CFG:
				try {
					if(agent != null){
						agent.modifyCfgParam(message.configDetail);
					}
					if(emslAgent != null){
						emslAgent.modifyCfgParam(message.emslConfigDetail);
					}
				} catch(Exception e) {
					_logger.error(e.getMessage(), e);
				}
				break;
				
			case AgentDelegationMessage.MSG_TYPE_REPORT_CALL:
				if(message.constraintId == -1) {
					if(_logger.isDebugEnabled()) {
					 	_logger.debug("Going to invoke reportCallHistoryInfo(4) on agent");
					}
					if(oemsAgent != null){
						oemsAgent.reportCallHistoryInfo(message.callId, message.message, message.isTestCall, message.callState);
					}
					if(emslAgent != null){
						emslAgent.reportCallHistoryInfo(message.callId, message.message, message.isTestCall, message.callState);
					}
				} else {
					if(_logger.isDebugEnabled()) {
					 	_logger.debug("Going to invoke reportCallHistoryInfo(5)");
					}
					if(oemsAgent != null){
						oemsAgent.reportCallHistoryInfo(message.callId, message.message, message.constraintId, message.isTestCall, message.callState,message.caller,message.called);
					}	
					if(emslAgent != null){
						emslAgent.reportCallHistoryInfo(message.callId, message.message, message.constraintId, message.isTestCall, message.callState);
					}
				}					
				break;

			case AgentDelegationMessage.MSG_TYPE_NOTIFY_LINK:
				agent.notifyLink(message.host, message.connState);
				break;

			case AgentDelegationMessage.MSG_TYPE_NOTIFY_LAN:
				agent.notifyLan(message.ipAddress, message.subnetMask, message.connState);
				break;

			default:
				_logger.error("Unknown Message in Agent Queue: Message Type = "+message.getMessageType());
				break;
		}
	}


	private class AgentDelegationMessage {

		static final int MSG_TYPE_MODIFY_CFG = 0;
		static final int MSG_TYPE_REPORT_CALL = 1;
		static final int MSG_TYPE_NOTIFY_LINK = 2;
		static final int MSG_TYPE_NOTIFY_LAN = 3;

		int messageType;
		int ipAddress;
		int subnetMask;
		RSIEmsTypes.ConnectionState connState;
		ConfigurationDetail configDetail;
		EmsLiteConfigurationDetail emslConfigDetail;
		String callId;
		String message;
		int constraintId = -1;
		int isTestCall;
		int callState;
		int host;
		String hostName;
		private String caller;
		private String called;

		AgentDelegationMessage(int messageType, ConfigurationDetail detail) {
			
			this.messageType = messageType;
			this.configDetail = detail;
		}
		
		AgentDelegationMessage(int messageType, EmsLiteConfigurationDetail detail) {
			
			this.messageType = messageType;
			this.emslConfigDetail = detail;
		}

		AgentDelegationMessage(int messageType, String callId, String message, int isTestCall, int callState) {
			
			this.messageType = messageType;
			this.callId = callId;
			this.message = message;
			this.isTestCall = isTestCall;
			this.callState = callState;
		}

		AgentDelegationMessage(int messageType,String callId, String message, int constraintId, int isTestCall, int callState,String caller,String called) {
			this(messageType,callId,message,isTestCall,callState);
			this.constraintId = constraintId;
			this.caller =caller;
			this.called=called;
		}

		AgentDelegationMessage(int messageType, int host, RSIEmsTypes.ConnectionState state) {

			this.messageType = messageType;
			this.host = host;
			this.connState = state;
		}

		AgentDelegationMessage(int messageType, String hostName) {
			
			this.messageType = messageType;
			this.hostName = hostName;
		}

		AgentDelegationMessage(int messageType, int ipAddress, int subnetMask, RSIEmsTypes.ConnectionState state) {
				
			this.messageType = messageType;
			this.ipAddress = ipAddress;
			this.subnetMask = subnetMask;
			this.connState = state;
		}

		public int getMessageType() {
			return this.messageType;
		}

		public String toString() {
			StringBuffer buffer =  new StringBuffer("AgentDelegationMessage:");
			buffer.append("[Type = ");
			buffer.append(this.messageType);
			buffer.append(AseStrings.SQUARE_BRACKET_CLOSE);
			return buffer.toString();
		}
	}

}


 
 
