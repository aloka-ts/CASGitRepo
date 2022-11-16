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
//      File:   StandbyReplicator.java

//		This Runnable is always running after started. It listens to in-coming 
//		replicatin messages and takes actions base on the message action type.
//
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Dana/Prashant Kumar              25/04/08        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.replication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.agnity.redis.client.RedisWrapper;
import com.baypackets.ase.channel.AseSubsystem;
import com.baypackets.ase.channel.ChannelClosedException;
import com.baypackets.ase.channel.ChannelNotConnectedException;
import com.baypackets.ase.channel.PeerMessage;
import com.baypackets.ase.channel.TimeoutException;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.container.AseIc;
import com.baypackets.ase.control.ControlManager;
import com.baypackets.ase.control.MessageTypes;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.router.acm.AppDataMessage;
import com.baypackets.ase.serializer.kryo.KryoPoolManager;
import com.baypackets.ase.servicemgmt.SasServiceManager;
import com.baypackets.ase.spi.replication.ReplicationContextImpl;
import com.baypackets.ase.spi.util.Work;
import com.baypackets.ase.spi.util.WorkListener;
import com.baypackets.ase.util.AseThreadMonitor;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.PrintInfoHandler;
import com.baypackets.ase.util.threadpool.ThreadPool;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ThreadAlreadyRegisteredException;
import com.baypackets.bayprocessor.slee.common.ThreadNotRegisteredException;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThreadState;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

public class StandbyReplicator extends MonitoredThread {

	private static Logger logger = Logger.getLogger(StandbyReplicator.class);

	private static final short DEFAULT_THREADPOOL_SIZE = 2;
	private static final short DEFAULT_MIN_ACTIVE_THREADS = 50; // percentage
	
	private static String peerId="";

	private boolean m_sr_stopped = false;
	private ThreadOwner m_sr_threadOwner = null;
	private ThreadPool m_sr_threadPool = null;

	private RedisWrapper redisWrapper = null;
	private AseHost host = (AseHost) Registry.lookup(Constants.NAME_HOST);
	private ThreadMonitor threadMonitor = (ThreadMonitor) Registry
			.lookup(Constants.NAME_THREAD_MONITOR);

	ReplicationContextStoreManager replStoreManager = null;

	public ReplicationContextStoreManager getReplStoreManager() {
		return replStoreManager;
	}

	public void setReplStoreManager(
			ReplicationContextStoreManager replStoreManager) {
		this.replStoreManager = replStoreManager;
	}

	private static final boolean isKryoSerializer = BaseContext
			.getConfigRepository()
			.getValue(Constants.IS_KRYO_SERIALIZER_ACTIVATED).equals("1");

	ControlManager controlMgr = (ControlManager) Registry
			.lookup(Constants.NAME_CONTROL_MGR);
	AseSubsystem subSystem = null;

	public StandbyReplicator(ThreadOwner thOwner, int threadId) {
		super("StandbyReplicator", AseThreadMonitor.getThreadTimeoutTime(),
				BaseContext.getTraceService());

		int tpSize = DEFAULT_THREADPOOL_SIZE;

		// Read the threadpool size property
		try {
			String numThreads = BaseContext.getConfigRepository().getValue(
					Constants.PROP_MT_STANDBY_REPLICATOR_THREAD_POOL_SIZE);
			if (numThreads != null) {
				tpSize = Integer.parseInt(numThreads.trim());
				tpSize = AseUtils.getTheNextProbablePrimeIfComposite(tpSize,
						Constants.PROP_MT_STANDBY_REPLICATOR_THREAD_POOL_SIZE);
			}
		} catch (Exception exp) {
			logger.error("Error in getting threadPool size", exp);
		}
		// set name and dersilaizer name::
		String threadName = "StandByThread";
		String deserializerName = threadName + "-" + threadId + "-Deserializer";

		logger.error("Using custom serializer : " + isKryoSerializer);

		// Instantiate the thread pool
		try {
			m_sr_threadPool = new ThreadPool(tpSize, false, deserializerName,
					null, // WorkHandler is optional
					thOwner, DEFAULT_MIN_ACTIVE_THREADS);
			this.m_sr_threadPool.setThreadMonitor(threadMonitor);
		} catch (Exception ex) {
			logger.error("Error in creating threadPool", ex);
		}

		m_sr_threadOwner = thOwner;

		subSystem = controlMgr.getSelfInfo();

		if (logger.isDebugEnabled()) {
			logger.debug("substemid is " + host.getSubsystemId());
		}

	}

	public StandbyReplicator(ThreadOwner thOwner) {
		this(thOwner, 0);
	}

	public ThreadPool getStandByThreadPool() {

		return m_sr_threadPool;
	}

	public void start() {
		m_sr_threadPool.start();
		super.start();
	}

	public void run() {
		if (logger.isInfoEnabled())
			logger.info("Standby Replicator Listener is started.");

		// Register thread with thread monitor
		try {
			// Set thread state to idle before registering
			this.setThreadState(MonitoredThreadState.Idle);

			threadMonitor.registerThread(this);
		} catch (ThreadAlreadyRegisteredException exp) {
			logger.error(
					"This thread is already registered with Thread Monitor",
					exp);
		}

		// Wait till all applications are deployed
		SasServiceManager ssm = (SasServiceManager) Registry
				.lookup("SasServiceManager");
		if (logger.isInfoEnabled())
			logger.info("Waiting for ServManager to notify ssm");
		synchronized (ssm) {
			while (!ssm.isStartupComplete()) {
				try {
					ssm.wait();
				} catch (Exception ex) {
					logger.error(
							"While waiting for notification from ServManager thread",
							ex);
					logger.error("No point in moving forward..... returning");
					return;
				}
			}
		}

		if (logger.isInfoEnabled())
			logger.info("Wait ended. Has been notified by ServManager thread ");

		ReplicationMessage msg = null;
		StandbyReplicationItem repl = null;

		try {
			
		//	while (!this.m_sr_stopped) {
				try {
					// Set thread state to idle before blocking on readLine
				//	this.setThreadState(MonitoredThreadState.Idle);


					//Set<String> replicablecallIds = redisWrapper.getSetOperations().getAllMemberFrmSet(peerId+ ReplicationManagerImpl.REPL_CALLIDS);
					List<String> replicablecallIds=redisWrapper.getListOperations().getListAll(
									peerId+ ReplicationManagerImpl.REPL_CALLIDS);
					
					if (logger.isInfoEnabled())
						logger.info("getListAll() find replicable callIds for peer "+peerId);

					if (replicablecallIds != null && !replicablecallIds.isEmpty()) {
						
						if (logger.isInfoEnabled())
							logger.info("replicable callIds set  found is  ... "+replicablecallIds);

//						ListIterator<String> callidsItr = replicablecallIds
//								.listIterator();
						
				        Iterator<String> callidsItr = replicablecallIds.iterator();
								
						while (callidsItr.hasNext()) {

							if (logger.isDebugEnabled()) {
								logger.debug("Replication data stored in " + peerId+ ReplicationManagerImpl.REPL_CALLIDS + " is found" );
							}

							String callId = (String) callidsItr.next();
//							redisWrapper.getListOperations().removeFromList(peerId + ReplicationManagerImpl.REPL_CALLIDS,0L
//									callId);
///                        
							//self has read it so remove
							
							redisWrapper.getListOperations().removeFromList(
									peerId + ReplicationManagerImpl.REPL_CALLIDS, 0L,
									callId);
							/**
							 * add context  to self on active role so that peer when takes active role gets these messages if they are yet active
							 */
//							 redisWrapper.getListOperations().pushInList(
//										ReplicationManagerImpl.REPL_CALLID_SET_NAME
//												, callId,"LEFT");
							 
							 if (logger.isDebugEnabled()) {
									logger.debug("pushInList repl data for self  -> "+ ReplicationManagerImpl.REPL_CALLID_SET_NAME);
								}

							if (logger.isDebugEnabled()) {
								logger.debug("CallID/context-id found is" + callId);
							}

							/**
							 * commenting below two lines of code as not saving callids against context id even for sip calls
							 * direct context/replids are saved in abovve repl callids list for sip/ss7 calls respectively
							 */
							String ctxid = null;

							String repl_prefix=peerId+"_";
							List<String> replMessages = null;
//							if (ctxid != null) {
//	
//								redisWrapper.getValueOperations().removeKey(callId);
//							} else { // when replid is used for tcap
										// calls callid=dialogid and
										// below list contains data
										// agianst repl ids
								// now changed sip also callids not sued only context-ids are saved and data against them is saved so now tcap and sip are same
	
								if (logger.isDebugEnabled()) {
									logger.debug("Check if data available for this "
											+ callId +" in "+ repl_prefix+callId);
								}
	
								replMessages = redisWrapper.getListOperations()
										.getListAll(repl_prefix+callId);
	
								if (logger.isDebugEnabled()) {
									logger.debug("check if ReplMessages for REPL_CALLID  "
											+ callId + " are found  "
											+ replMessages.isEmpty());
								}
	
								if (replMessages != null && !replMessages.isEmpty()) {
									ctxid = callId;
								}
	
								if (logger.isDebugEnabled()) {
									logger.debug("Context ID   is " + ctxid);
								}
					//	}

							if (ctxid != null) {

								if (replMessages == null) {
									replMessages = redisWrapper.getListOperations()
											.getListAll(repl_prefix+ctxid);
								}

								if (logger.isDebugEnabled()) {
									logger.debug("get Replication messages list for  context id " +ctxid);
								}

								boolean activateCtxt = false;

								if (replMessages != null) {
									
									redisWrapper.getListOperations().removeAllElements(repl_prefix+ctxid);
									
									if (logger.isDebugEnabled()) {
										logger.debug("Found No.of Replication messages -> "+ replMessages.size());
									}

									for (int i = 0; i < replMessages
											.size(); i++) {
										String replMsg = replMessages.get(i);
										
										/**
										 * add mesaage to self on active role so that peer when takes active role gets these messages if they are yet active
										 */
										if (logger.isDebugEnabled()) {
											logger.debug("pushInList repl data for self  -> "+ ReplicationManagerImpl.REPL_MSG_LIST_PREFIX);
										}
//										redisWrapper.getListOperations().pushInList(
//												ReplicationManagerImpl.REPL_MSG_LIST_PREFIX
//														+ ctxid, replMsg, "RIGHT");
										
										byte[] data =Base64.getDecoder().decode(replMsg);
										

//										redisWrapper.getListOperations()
//												.removeFromList(
//														ctxid, 0L,
//														replMsg);

										if (logger.isDebugEnabled()) {
											logger.debug("packet received for context id is  "
													+ data);
										}

										PeerMessage peerMessage = replStoreManager
												.getMessageFactory()
												.createMessage(
														MessageTypes.REPLICATION_MESSAGE,
														PeerMessage.MESSAGE_IN);
										peerMessage.setPacket(data);
										

									//	if (peerMessage instanceof ReplicationMessage) {

											if (logger.isDebugEnabled()) {
												logger.debug("Replication message is received: "
														+ peerMessage);
											}
											msg = (ReplicationMessage) peerMessage;

											short action=msg.getAction();

										if (i == (replMessages.size() - 1)
												&& action != ReplicationMessage.CLEANUP) {
	
											if (logger.isDebugEnabled()) {
												logger.debug("msg.setActivate(true)");
											}
												msg.setActivate(true);
										}else{
											if (msg.getSequenceNo() == 4) {
												if (logger.isDebugEnabled()) {
													logger.debug("enable activate context: for msg seq "
															+ msg.getSequenceNo());
												}
												msg.setActivate(true);
											}
										}
										//	msg.setContextId(ctxid);

//										} else if (peerMessage instanceof AppDataMessage) {
//
//											if (logger.isDebugEnabled()) {
//												logger.debug("AppData message is received: "
//														+ peerMessage);
//											}
//											AppDataMessage datamsg = (AppDataMessage) peerMessage;
//											deliverAppDataMessage(datamsg);
//											continue;
//
//										}
											
											// Update time in thread monitor
											this.updateTimeStamp();

											// Set thread state to running before
											// blocking on dequeue
											this.setThreadState(MonitoredThreadState.Running);
											repl = new StandbyReplicationItem(msg);

											String repId = msg.getRepId();
											if (logger.isDebugEnabled()) {
												logger.debug("Replication message is received: repId::"
														+ repId +" Context id is "+ msg.getContextId());
											}
											if (repId == null || repId.isEmpty()) {
												if (logger.isDebugEnabled()) {
													logger.debug("Replication message repId not present");
												}
												m_sr_threadPool.submit(msg
														.getContextId().hashCode(),
														repl);
											} else {
												if (logger.isDebugEnabled()) {
													logger.debug("Replication message is received: repId available");
												}
												m_sr_threadPool.submit(
														repId.hashCode(), repl);
											}

									}

									
								}
							}
						}
					}else{
						
//						try{
//						Thread.currentThread().sleep(10000);
//						if (logger.isInfoEnabled())
//							logger.info("wakeup and check available repl data... ");
//						}catch(Exception e){
//							e.printStackTrace();
//						}
						
//						m_sr_stopped=true;					
						if (logger.isDebugEnabled()) {
							logger.debug("Stopping standby replicator as no nore replicable id available");
						}
					}

				} catch (ChannelClosedException ex) {
					logger.error("Data channel is closed.", ex);
					// break;
				} catch (ChannelNotConnectedException ex) {
					logger.error("Data channel is not connected.", ex);
					// break;
				} catch (TimeoutException ex) {
					logger.error(ex.getMessage(), ex);
					// continue;
				} catch (Throwable t) {
					logger.error("" + t, t);
					// continue;
				}
			//} // while
		}catch (Throwable t) {
			logger.error("" + t, t);
			// continue;
		} finally {
			// Unregister thread with thread monitor
			try {
				threadMonitor.unregisterThread(this);
			} catch (ThreadNotRegisteredException exp) {
				logger.error(
						"This thread is not registered with Thread Monitor",
						exp);
			}
		}
		if (logger.isInfoEnabled())
			logger.info("Replication Listener shuting down...");
	}

	/**
	 * This method is used to deliver app data message
	 * 
	 * @param datamsg
	 */
	private void deliverAppDataMessage(AppDataMessage datamsg) {

		// DataChannelProvider
		// datachannelprovider=(DataChannelProvider)this.replStoreManager.getChannelManager();
		// List<AppDataMessageListener> listeners=
		// datachannelprovider.getAppDataMsgListeners();
		//
		// if (logger.isInfoEnabled())
		// logger.info("deliverAppDataMessage...listener available are "+listeners
		// + "  on datachannel provider "+datachannelprovider);
		//
		// for(int i=0; listeners != null && i<listeners.size();i++){
		// AppDataMessageListener listener = (AppDataMessageListener)
		// listeners.get(i);
		// listener.handleDataMessage(datamsg);
		// }

	}

	public void shutdown() {
		this.m_sr_stopped = true;
		this.m_sr_threadPool.shutdown();
	}

	public ThreadOwner getThreadOwner() {
		return m_sr_threadOwner;
	}

	private class StandbyReplicationItem implements Work {

		private ReplicationMessage m_sri_msg;

		public StandbyReplicationItem(ReplicationMessage p_msg) {
			m_sri_msg = p_msg;
		}

		public int getTimeout() {
			return 100;
		}

		public WorkListener getWorkListener() {
			return null;
		}

		public void execute() {
			boolean ctxtAvailable = false;
			ReplicationContextImpl ctxt = null;
			try {
				ctxt = (ReplicationContextImpl) replStoreManager
						.findReplicationContextById(m_sri_msg.getContextId());

				ctxtAvailable = (ctxt != null);
				switch (m_sri_msg.getAction()) {
				case ReplicationMessage.CREATE:
					if (logger.isDebugEnabled())
						logger.debug("Going to create the replication context....");

					if (isKryoSerializer) {
						Kryo kryo = KryoPoolManager.borrow();
						Input ip = new Input(
								(ObjectInputStream) m_sri_msg.getObjectInput());
						ctxt = (ReplicationContextImpl) kryo
								.readClassAndObject(ip);
						KryoPoolManager.release(kryo);
					} else {
						ctxt = (ReplicationContextImpl) m_sri_msg
								.getObjectInput().readObject();
					}

					host.addIc((AseIc) ctxt);
					((ReplicationManagerImpl) m_sr_threadOwner)
							.setReplicationInfo(m_sri_msg, ctxt);

					// If this ReplicationContext contains objects belonging to
					// applications that are NOT deployed on this host, notify
					// the peer that sent us the context to stop sending them.
					if (!((ReplicationManagerImpl) m_sr_threadOwner)
							.isCompatible(ctxt)) {
						((ReplicationManagerImpl) m_sr_threadOwner)
								.notifyPeer(ctxt);
						break;
					}
					if (logger.isDebugEnabled())
						logger.debug("so caching it to be activated later.");
					replStoreManager.createReplicationContext(ctxt);
					ctxt.partialActivate();
					// Increment the measurement counters at the standby side
					if (!ctxtAvailable) {
						AseMeasurementUtil.counterReplicated.increment();
						AseMeasurementUtil.counterBeingReplicated.increment();
					}
					
					if (m_sri_msg.isActivate()) {
						
						if (logger.isDebugEnabled())
							logger.debug("Going to activate context....after reading msg sequence "+ m_sri_msg.getSequenceNo());
						ctxt.activate();

						PrintInfoHandler.instance().addValue(
								Constants.CTG_ID_ACTIVATED, ctxt.getId());
					}
					break;

				case ReplicationMessage.REPLICATE:
					if (logger.isDebugEnabled())
						logger.debug("Going to parse incremental replication message....");
					if (ctxtAvailable) {
						ctxt.readIncremental(m_sri_msg.getObjectInput());

						((ReplicationManagerImpl) m_sr_threadOwner)
								.setReplicationInfo(m_sri_msg, ctxt);
						ctxt.partialActivate();

						if (m_sri_msg.isActivate()) {
							
							if (logger.isDebugEnabled())
								logger.debug("Going to activate context....after reading msg sequence "+ m_sri_msg.getSequenceNo());
							ctxt.activate();

							PrintInfoHandler.instance().addValue(
									Constants.CTG_ID_ACTIVATED, ctxt.getId());
						}
					} else {
						logger.error("Unable to get context (incremental):"
								+ m_sri_msg.getContextId());
					}
					break;

				case ReplicationMessage.REINIT:
					if (logger.isDebugEnabled())
						logger.debug("Going to re-initialize the replication context using the message....");
					if (ctxtAvailable) {
						ctxt.readExternal(m_sri_msg.getObjectInput());
						((ReplicationManagerImpl) m_sr_threadOwner)
								.setReplicationInfo(m_sri_msg, ctxt);
						ctxt.partialActivate();
					} else {
						logger.error("Unable to get context for read external:"
								+ m_sri_msg.getContextId());
					}
					break;

				case ReplicationMessage.CLEANUP:
					if (logger.isDebugEnabled())
						logger.debug("Will remove the replication context if found one....");
					if (ctxtAvailable) {
						replStoreManager.removeReplicationContext(ctxt);
						ctxt.cleanup();

						// Update the measurement counters on the standby side
						AseMeasurementUtil.counterCleanedUp.increment();
						AseMeasurementUtil.counterBeingReplicated.decrement();
					} else {
						if (logger.isInfoEnabled())
							logger.info("Unable to get context (cleanup):"
									+ m_sri_msg.getContextId());
					}
					break;
				}// switch
			} catch (IOException e) {
				logger.error(e.getMessage(), e);

				// Remove the replication context from the cache.
				if (m_sri_msg != null) {
					if (ctxtAvailable) {
						replStoreManager.removeReplicationContext(ctxt);
					}
					AseMeasurementUtil.counterDeserializationFail.increment();
					AseMeasurementUtil.thresholdDeserializationFail.increment();
				}
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage(), e);

				// Remove the replication context from the cache.
				if (m_sri_msg != null) {
					if (ctxtAvailable) {
						replStoreManager.removeReplicationContext(ctxt);
					}
					AseMeasurementUtil.counterDeserializationFail.increment();
					AseMeasurementUtil.thresholdDeserializationFail.increment();
				}
			} catch (Throwable t) {
				logger.error("" + t, t);
			} finally {
				// cleanup previous message object
				if (m_sri_msg != null) {
					try {
						((ReplicationMessageFactory) replStoreManager
								.getMessageFactory()).releaseMessage(m_sri_msg);
						// m_sri_msg.reset();
					} catch (Exception ex) {
						logger.error("Message cleanup", ex);
					}
				}
			}
		} // execute()
	} // StandbyReplicationItem ends

	public void setRedisWrapper(RedisWrapper redisWrapper) {
		this.redisWrapper = redisWrapper;
		logger.debug("RedisWrapper is  " + this.redisWrapper);
	}

	public void setPeerId(String peerId) {
		this.peerId=peerId;
		
	}
} // StandbyReplicator ends

