/*
 * AseEngine.java
 *
 * Created on August 6, 2004, 4:51 PM
 */
package com.baypackets.ase.container;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.sip.SipServletMessage;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseBaseConnector;
import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.AseEventListener;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.logging.LoggingCriteria;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.container.sip.AseSipAppCompositionHandler;
import com.baypackets.ase.latency.AseLatencyData;
import com.baypackets.ase.sipconnector.AseSipServletMessage;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasMessageCallback;
import com.baypackets.ase.spi.container.SasMessageProcessor;
import com.baypackets.ase.spi.util.WorkManager;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.PrintInfoHandler;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.threadpool.ThreadPool;
import com.baypackets.ase.util.threadpool.WorkHandler;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;


/**
 * This class represents a Servlet engine.  It provides a root level
 * container for deploying virtual hosts each of which is represented 
 * by an instance of the AseHost class.
 *
 * @author  Zoltan Medveczky
 */
public final class AseEngine extends AseBaseContainer implements WorkHandler, MComponent, ThreadOwner, SasMessageProcessor {

	private static Logger _logger = Logger.getLogger(AseEngine.class);
	private static StringManager _strings = StringManager.getInstance(AseEngine.class.getPackage());
	private static final long serialVersionUID = -3814634264647849723L;
	private ThreadPool threadPool = null;

	private boolean priorityCallEnabled = false;

    private String ingwMessageQueue = null;
	private String nsepIngwPriority = null;
	/**
	 * Default constructor
	 */
	public AseEngine(){
		this(Constants.NAME_ENGINE);
	}


	/**
	 * Assigns the specified name to this AseEngine.
	 *
	 * @param name  The unique name to associate with this object
	 * @throws IllegalArgumentException if the given name is null
	 */
	public AseEngine(String name) {
		super(name);
	}


	/**
	 * Sets this engine's parent.  Since this is a root level container, this
	 * method will always throw an IllegalArgumentException. 
	 *
	 */
	public void setParent(AseContainer parent) {
		throw new IllegalArgumentException(_strings.getString("AseEngine.noParent", getName()));
	}    


	/**
	 * Adds the specified child to this AseEngine which must be an instance of
	 * the AseHost class.
	 *
	 * @param child  Must be an instance of AseHost
	 * @thorws IllegalArgumentException if the given child is not an AseHost.
	 * @see com.baypackets.ase.container.AseHost
	 */
	public void addChild(AseContainer child) {
		if (!(child instanceof AseHost)) {
			throw new IllegalArgumentException(_strings.getString("AseEngine.invalidChild", getName()));
		}
		super.addChild(child);
	}

	/**
	 * Indicates whether handling of priority call is enabled or not.
	 */
	public boolean isCallPriorityEnabled() {
		return this.priorityCallEnabled;
	}


	/**
	 * Processes the given Servlet request and response objects.
	 *
	 * @param request  An object encapsulating a Servlet request.
	 * @param response  An object encapsulating a Servlet response.
	 */
	public void processMessage(SasMessage message) throws AseInvocationFailedException, ServletException {		                                                                                                                                                                                    
		// Enable or disable logging for the current thread based on
		// any criteria specified for SIP messages.
		if (message instanceof SipServletMessage) {
			LoggingCriteria.getInstance().check((SipServletMessage)message);
		}

		AseHost host = (AseHost)this.findChild(Constants.NAME_HOST);

		if(host != null){
			host.processMessage(message);
		} else {
			_logger.error("Unable to get the host handle");
		}
	}


	/**
	 * Processes the given event.
	 *
	 */
	public void handleEvent(EventObject event, AseEventListener listener) {    	
		if (event == null || listener == null){
			_logger.error("Event or the listener object is NULL: " + event);
			return;
		}

		listener.handleEvent(event);
	}


	/**
	 * Processes the given message.
	 *
	 */
	public void handleMessage(AseMessage message){
		if (message == null){
			_logger.error("NULL Message object received");
			return;
		}

		if(message.getStatus() == AseMessage.LOOPBACK_SYNC) {
			if (_logger.isDebugEnabled()) {

			_logger.debug("Processing loopback (sync) message");
			}
			try {
				this.execute(message);
			} catch(Exception e) {
				_logger.error(e.getMessage(), e);
			}
		} else {
			if (_logger.isDebugEnabled()) {

			_logger.debug("Processing network message");
			}
			int queueIndex = threadPool.submit(message.getWorkQueue(), message,message.isPriorityMessage());
			/*if (message.getMessageType() == AseMessage.MESSAGE){
				if(AseSipServletMessage.class.isInstance(message.getMessage())){
					((AseSipServletMessage)message.getMessage()).setWorkQueue(queueIndex);
					if(_logger.isEnabledFor(Level.INFO)){
						_logger.info("Sip Servlet Message thus setting work queue index:" + queueIndex);
					}
				}
			}*/
		}
	}


	/**
	 * Threadpool callback method.
	 */
	public void execute(Object obj){
		if (obj == null || !(obj instanceof AseMessage)){
			_logger.error("Queued object is NULL or not an instance of AseMessage: " + obj);
			return;
		}

		AseMessage message = (AseMessage)obj;

		try {

			if(message.getMessage() instanceof AseSipServletMessage 
					&& !((message.getStatus() == AseMessage.LOOPBACK_SYNC) || (message.getStatus() == AseMessage.LOOPBACK_ASYNC))){
				AseLatencyData.noteLatencyData( (AseSipServletMessage)message.getMessage(),
						AseLatencyData.ComponentTimes.QUEUE, false );
			}

			if(message.getStatus() == AseMessage.LOOPBACK_ASYNC) {
				if (_logger.isDebugEnabled()) {

				_logger.debug("Processing loopback (async) message");
				}
				// It can only be MESSAGE type
				SasMessage sasMsg = message.getMessage();
				SasMessageCallback source = sasMsg.getMessageContext().getMessageCallback();

				// Add this message into thread specific data
				AseThreadData.add(sasMsg);

				// Now invoke PSIL to process this loopback message
				source.processed(sasMsg);
			} else {
				switch (message.getMessageType()) {
				case AseMessage.MESSAGE:
					SasMessage sasMsg = message.getMessage();
					if(AseSipServletMessage.class.isInstance(sasMsg)){
						((AseSipServletMessage)sasMsg).setWorkQueue(message.getWorkQueue());
						if(_logger.isEnabledFor(Level.INFO)){
							_logger.info("Sip Servlet Message thus setting work queue index:" + message.getWorkQueue());
						}
					}
					SasMessageCallback source = sasMsg.getMessageContext().getMessageCallback();
					try {
						this.processMessage(sasMsg);
						if(source != null){
							source.processed(sasMsg);
						}
					} catch(AseInvocationFailedException aife) {
						_logger.error(aife.getMessage(), aife);
						if(source != null){
							source.failed(sasMsg, aife);
						}
					}
					break;

				case AseMessage.EVENT:
					this.handleEvent(message.getEvent(), message.getListener());

					// Thread specific data to be processed on all connectors
					Iterator iter = this.getConnectors();
					while(iter.hasNext()) {
						((AseBaseConnector)iter.next()).messageProcessed();
					}

					break;

				default:
					_logger.error("Unknown message type :"+message.getMessageType());
				} // switch
			}
		} catch(Exception e) {
			_logger.error(e.getMessage(), e);
		}
	}

	public void registerConnector(AseBaseConnector connector) {
		super.registerConnector(connector);

		if(connector.getProtocol().startsWith(AseStrings.SIP)) {
			AseSipAppCompositionHandler.getInstance().setSipConnector(connector);
			AseSipAppCompositionHandler.getInstance().setContainer(this);
		}
	}

	/**
	 *
	 */
	public void initialize() throws Exception {
		ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

		int count = Constants.DEFAULT_ENGINE_WORKERS;
		int minPercentageCommonThreads = 100;
		boolean singleQueue = Boolean.valueOf(config.getValue(Constants.PROP_MT_CONTAINER_SINGLE_QUEUE)).booleanValue();

		String strCount = config.getValue(Constants.PROP_MT_CONTAINER_THREAD_POOL_SIZE);
		count = Integer.parseInt(strCount);

		String strPer = config.getValue(Constants.PROP_MT_MONITOR_MIN_PERCENT_THREADS_REQD);

		if (strPer != null) {
			minPercentageCommonThreads = Integer.parseInt(strPer);
		}
		
		ingwMessageQueue = (String)config.getValue(Constants.INGW_MSG_QUEUE);
		nsepIngwPriority = 	(String)config.getValue(Constants.NSEP_INGW_PRIORITY);

		if (_logger.isDebugEnabled()) {
			_logger.debug("Messages from INGw will come in " + ingwMessageQueue + " queue");
			_logger.debug("All call prioirty support with INGw messages coming in priority queue " + nsepIngwPriority);
		}

		// Added for NSEP(Priority) call support
		String priorityCall = config.getValue(Constants.NSEP_CALL_PRIORITY_SUPPORTED);
		if(priorityCall != null){
			if(1 == (int)Integer.parseInt(priorityCall)) {
				this.priorityCallEnabled = true;
				if(_logger.isDebugEnabled()) {
					_logger.debug("Call Priority feature is enabled :");
				}
			}
		}
		
		if(priorityCall != null && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
			if(1 == (int)Integer.parseInt(priorityCall)) {
				AseUtils.setCallPrioritySupport(1);
				if(_logger.isDebugEnabled()) {
					_logger.debug("Call Priority feature is enabled with INGw sending the messages in priority queue:");
				}
			} 
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("initialize(): Creating thread pool with " + count + " worker threads.");

			if (singleQueue) {
				_logger.debug("initialize(): Allocating a single queue for all threads in the pool.");
			} else {
				_logger.debug("initialize(): Allocating one queue per thread in the pool.");
			}
		}

		this.threadPool = new ThreadPool(count, singleQueue, "Worker", this, this, minPercentageCommonThreads);
		ThreadMonitor tm = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);
		this.threadPool.setThreadMonitor(tm);

		//new Thread( AseTimerClock.getInstance() ).start();
		AseTimerClock.getInstance().start();

		if (singleQueue) {
			PrintInfoHandler.instance().registerExternalCategory(Constants.CTG_ID_MSG_QUEUE, Constants.CTG_NAME_MSG_QUEUE, "", this.threadPool.getQueue().getElements());
		}
	} 

	/**
	 *
	 */
	public void start() {
		this.threadPool.start();
	}


	/**
	 *
	 */
	public void stop() {
		this.threadPool.shutdown();
		AseTimerClock.getInstance().shutdown();
	}


	/**
	 *
	 */
	public void changeState(MComponentState state) throws UnableToChangeStateException {			
		try {
			if (state.getValue() == MComponentState.LOADED){
				this.initialize();
			}
			if (state.getValue() == MComponentState.RUNNING){
				this.start();
			}
			if (state.getValue() == MComponentState.STOPPED){
				this.stop();
			}
		} catch(Exception e){
			throw new UnableToChangeStateException(e.getMessage());
		}
	}


	/**
	 *
	 */
	public void updateConfiguration(Pair[] configData, OperationType opType) throws UnableToUpdateConfigException {		
		//No op		
	}


	/**
	 *
	 */
	public int threadExpired(MonitoredThread thread) {
		if (_logger.isEnabledFor(Level.INFO)){
			_logger.info(thread.getName() + " expired");
		}

		// Calling ThreadPool's method as logic for min percentage of common
		// thread required, lies there
		return threadPool.threadExpired(thread);
	}

	public ArrayList getContainerQueues()  {
		return this.threadPool.getQueueList();
	}



	public WorkManager getWorkManager(){
		return this.threadPool;
	}
}
