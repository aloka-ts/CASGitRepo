/*
 * Created on Nov 1, 2004
 *
 */
package com.baypackets.ase.replication;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.agnity.redis.client.RedisWrapper;
import com.agnity.redis.exeption.RedisLettuceCommandTimeoutException;
import com.agnity.redis.exeption.RedisLettuceConnectionException;
import com.baypackets.ase.channel.AppInfo;
import com.baypackets.ase.channel.AseSubsystem;
import com.baypackets.ase.channel.MessageParseException;
import com.baypackets.ase.channel.PeerMessage;
import com.baypackets.ase.channel.ReplicationContext;
import com.baypackets.ase.common.RedisAlarmHandler;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.container.AseIc;
import com.baypackets.ase.control.AseModes;
import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.control.ControlManager;
import com.baypackets.ase.control.MessageTypes;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.PeerStateChangeEvent;
import com.baypackets.ase.control.PeerStateChangeException;
import com.baypackets.ase.control.PeerStateChangeListener;
import com.baypackets.ase.control.RoleChangeListener;
//import com.baypackets.ase.control.VersionManager;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.serializer.kryo.KryoPoolManager;
import com.baypackets.ase.spi.replication.ReplicationContextImpl;
import com.baypackets.ase.spi.replication.ReplicationEvent;
import com.baypackets.ase.spi.replication.ReplicationManager;
import com.baypackets.ase.spi.replication.SelectiveReplicationContext;
import com.baypackets.ase.spi.util.Work;
import com.baypackets.ase.spi.util.WorkListener;
import com.baypackets.ase.util.AseThreadMonitor;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.PrintInfoHandler;
import com.baypackets.ase.util.StackDumpLogger;
import com.baypackets.ase.util.redis.RedisManager;
import com.baypackets.ase.util.threadpool.ThreadPool;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.ThreadAlreadyRegisteredException;
import com.baypackets.bayprocessor.slee.common.ThreadNotRegisteredException;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThreadState;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

/**
 * This class handles data replication in a way defined by the role of 
 * AseSubsystem in a cluster. If a AseSubsystem in ACTIVE role, it sends 
 * replication message to other systems. The action is trigged by activities
 * in the system. For a STANDBY AseSubsystem, it listens to in-coming replication
 * messages and takes action base on message action type. When a peer is down,
 * it stops to send replication message to that peer. And when a peer is up, it
 * resends all replicables.
 * 
 * @author Ravi, Dana
 */

public class ReplicationManagerImpl 
		implements ReplicationManager, MComponent, RoleChangeListener, PeerStateChangeListener, ThreadOwner{// AppSyncMessageListener,DataChannelSyncMessageListener{
	
	private static Logger logger = Logger.getLogger(ReplicationManagerImpl.class);
	
	private static final String CALL_ID_PREFIX = "SAS Call Id = ";

	private static final int RESET = 1;
	private static final int REMOVE = 2;
	
	private AseSubsystem subSystem;
//	private DataChannelProvider dataChannel;
	
	ReplicationContextStoreManager replStoreManager=null;
	private String clusterId;
	private Map destinationMap = new HashMap();
	private Map destinationSubsystems = new HashMap();
	private Map replicableSubsystems = new HashMap();
	
	private transient List<String> allowedSubsystemList = new ArrayList<String>();
	
	private short clusterRole = AseRoles.UNKNOWN;
	private String subsysId;
//	private VersionManager versionMgr;
        
	private List<BulkReplicator> bulkReplicatorList = new ArrayList<BulkReplicator>();
	//private StandbyReplicator replicationListener;
	
	private ControlManager controlMgr;
	private ClusterManager clusterMgr;
	private PolicyManager policyMgr;
	
	private AseHost host;
	
	public static final String REPL_CALLIDS="REPL_CALLIDS";

	private ThreadMonitor threadMonitor = null;
	
	RedisWrapper redisWrapper=null;
	
	RedisManager redisManager=null;
	
	protected static String REPL_MSG_LIST_PREFIX="";
	protected static String REPL_CALLID_SET_NAME="";

	private static final boolean isKryoSerializer = BaseContext.getConfigRepository().getValue(Constants.IS_KRYO_SERIALIZER_ACTIVATED).equals("1");
	
	public static  boolean isreplicationEnabled = true;
	
	public void init() throws Exception {		
		controlMgr = (ControlManager)Registry.lookup(Constants.NAME_CONTROL_MGR);
		subSystem = controlMgr.getSelfInfo();
		
		if (logger.isDebugEnabled())
			logger.debug("initializing subsystem mode is " +subSystem.getMode());
//		if(!AseModes.isFtMode(subSystem.getMode())){
//			if (logger.isDebugEnabled())fv
//				logger.debug("Not initializing the replication manager, since the Mode is Not FT");
//			return;
//		}
		
		if (AseModes.FT_N_PLUS == subSystem.getMode()
				|| AseModes.FT_N_PLUS_LITE == subSystem.getMode()) {
			if (logger.isDebugEnabled())
				logger.debug("initializing the redis client");

			redisWrapper = (RedisWrapper) Registry
					.lookup(Constants.REDIS_WRAPPER);// RedisWrapper.getRedisWrapper(connectionInfo1);

			replStoreManager = ReplicationContextStoreManager.getInstance();

			replStoreManager.setMessageFactory(new ReplicationMessageFactory());
		}
		
		
		String replEnabled=BaseContext.getConfigRepository().getValue(Constants.PROP_REPLICATION_ENABLED);
		
		if("0".equals(replEnabled)){
			isreplicationEnabled=false;
		}
		
	//	if (AseModes.FT_N_PLUS_K == subSystem.getMode()) {
			controlMgr.registerPeerStateChangeListener(this);
			clusterMgr = (ClusterManager) Registry
					.lookup(Constants.NAME_CLUSTER_MGR);

			// added as part to activate of early activations of some sessions
			clusterMgr.registerRoleChangeListener(
					SpecialReplicationActivator.getInstance(),
					Constants.RCL_SPECIAL_ACTIVATOR);

			clusterMgr.registerRoleChangeListener(this,
					Constants.RCL_REPLMGR_PRIORITY);
			clusterMgr.setReplicationManager(this);
			policyMgr = (PolicyManager) Registry
					.lookup(Constants.NAME_POLICY_MANAGER);
//			versionMgr = (VersionManager) Registry
//					.lookup(Constants.NAME_VERSION_MGR);
		//	versionMgr.registerAppSyncMsgListener(this);

		
        
		threadMonitor = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);

//		dataChannel = ChannelProviderFactory.getInstance().getDataChannelProvider();
//		// TESTPK dataChannel.connect();
//		dataChannel.setSubsystem(this.subSystem);
//		
//		
//		
//		//register data message synch listener
//		dataChannel.registerDataChannelSyncMsgListener(this);
////		
//		dataChannel.setMessageFactory(new ReplicationMessageFactory());
		clusterId = AseUtils.getIPAddressList(BaseContext.getConfigRepository().getValue(Constants.OID_SIP_FLOATING_IP), true);
		clusterId = ClusterManager.adjustFIPFormat(clusterId);
	//	}
		this.subsysId = subSystem.getId();
        
		//TESTPK this.replicationListener = new StandbyReplicator(this);
		
		this.host = (AseHost)Registry.lookup(Constants.NAME_HOST);
		
		redisManager = (RedisManager) Registry
				.lookup(Constants.NAME_REDIS_MANAGER);

		//Register the print handlers for Replicated replication contexts
	//	PrintInfoHandler.instance().registerExternalCategory(Constants.CTG_ID_REPL_CTXT, Constants.CTG_NAME_REPL_CTXT, "", dataChannel.getReplicationContextMap());
		
		//Register the print handler for ACTIVATED replication contexts
		PrintInfoHandler.instance().registerInternalCategory(Constants.CTG_ID_ACTIVATED, Constants.CTG_NAME_ACTIVATED, CALL_ID_PREFIX, 1000);
		

		logger.error("Using custom serializer : " + isKryoSerializer);

	}
	   
    /** Changes the Component State to the state indicated by the argument 
	passed. The states are changed according to the priority values. **/ 
	public void changeState(MComponentState state)
		throws UnableToChangeStateException {
        try {
            if (state.getValue() == MComponentState.LOADED) {
            	this.init();
            } else if (state.getValue() == MComponentState.RUNNING){
            	// do nothing
            } else if(state.getValue() == MComponentState.STOPPED){
            	if(AseModes.isFtMode(this.subSystem.getMode())){
                	//TESTPK this.replicationListener.shutdown();
	            //	this.dataChannel.disconnect();
            	}
            }
        } catch(Exception e){
			logger.error("Starting Replication Module", e);
            throw new UnableToChangeStateException(e.getMessage());
        }
	}

    /** Updates the configuration parameters of the component as 
	specified in the Pair array **/
	public void updateConfiguration(Pair[] arg0, OperationType arg1)
		throws UnableToUpdateConfigException {
	}

	/**
	 * Implements RoleChangeListener. If AseSubsystem becomes active,
	 * it actives each replication context.
	 */
	public void roleChanged(String clusterId, PartitionInfo partitionInfo) {
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("System role has been changed" +   " for cluster :" + clusterId);
		}
                
		this.clusterRole = partitionInfo.getRole();
		REPL_MSG_LIST_PREFIX=host.getSubsystemId()+"_";
		REPL_CALLID_SET_NAME=host.getSubsystemId() + REPL_CALLIDS;
		//this.subsysId = subsysId;

		if(logger.isDebugEnabled()) {
			logger.debug("Cluster Role = "+this.clusterRole);
		}
		
		/*
		 * clear any replication data for this host in case its there in redis server
		 */
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Check if there is any stale replication data stored for this host ->then remove it ");
			}
			 
//			Set<String> contextids = redisWrapper.getSetOperations()
//					.getAllMemberFrmSet(REPL_CALLID_SET_NAME);
//			redisWrapper.getSetOperations().deleteKey(
//					REPL_CALLID_SET_NAME);
			
			List<String> contextids = redisWrapper.getListOperations().getListAll(REPL_CALLID_SET_NAME);
			redisWrapper.getListOperations().removeAllElements(REPL_CALLID_SET_NAME);

			if (contextids != null) {

				if (logger.isDebugEnabled()) {
					logger.debug("Found No.of Replication contexts -> "
							+ contextids.size());
				}
				Iterator<String> contexts = contextids.iterator();
				while (contexts.hasNext()) {
					String ctxid = contexts.next();
					redisWrapper.getListOperations().removeAllElements(REPL_MSG_LIST_PREFIX+ctxid);
				}
			}else{
				if (logger.isDebugEnabled()) {
					logger.debug("No stale replication data found-> ");
				}
			}
			
		} catch (Exception e) {
			logger.error(" Exception thrown while deleting redis REPL_CALLIDS "+e );
		}
                
		//if((this.clusterRole != AseRoles.ACTIVATING)
		if(this.clusterRole != AseRoles.ACTIVE) {
			if(logger.isDebugEnabled()) {
				logger.debug("Cluster Role is not ACTIVE: returning not starting stanby replicator");
			}
			return;
		}
		
		// Set these counters to 0 as their meaning changes with role
		if(logger.isDebugEnabled()) {
			logger.debug("Setting the counter values to 0");
		}
		AseMeasurementUtil.counterCleanedUp.setCount(0);
		AseMeasurementUtil.counterBeingReplicated.setCount(0);
		AseMeasurementUtil.counterReplicated.setCount(0);
		
		Iterator iterator = replStoreManager.findReplicationContextByCluster(clusterId);
		if (iterator == null) {
			// Nothing need to do
			if(logger.isInfoEnabled()){
				logger.info("No replication contexts found. So not doing any activation.");
			}
		} else {
			
			if(logger.isInfoEnabled()){
				logger.info("will do  replication contexts activate on peer state change event.");
			}
			// Create and start Activator here
//			String threadNum = BaseContext.getConfigRepository().getValue(Constants.PROP_MT_ACTIVATOR_THREAD_POOL_SIZE);
//			if(!threadNum.equals("0")){
//				//new Activator(clusterId, this).start();
//				
//				StandbyReplicator sbr=new StandbyReplicator(this, 1);
//				sbr.setRedisWrapper(redisWrapper);
//        		sbr.setReplStoreManager(replStoreManager);
//        		sbr.start();
//			}
		}
		
		// 
		// Stop and dereference StandbyReplication and its threadpool
		//
		// Commenting this for now as this is executed at the time of start up itself
        // this.replicationListener.shutdown();
		// this.replicationListener = null;
	}

	/**
	 * Implements PeerStateChangeListener. When a peer is down, stops 
	 * replication to that peer. When a peer is up, resends all replicables
	 * to that peer.
	 */
	public int handleEvent (PeerStateChangeEvent psce) throws PeerStateChangeException {
		int peerState = psce.getEventId(); 
		String peerId = psce.getSubsystem().getId();
		
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Received Event: Peer " + peerId + "is "+psce +"clusterrole is "+clusterRole);
		}
		
		switch (peerState) {
		case PeerStateChangeEvent.PR_DOWN_CLOSE_CONN:
			if(logger.isEnabledFor(Level.INFO)){
				logger.info("PR_DOWN_CLOSE_CONN Event: Peer " + peerId + "");
			}
			
			if (AseModes.FT_N_PLUS_LITE == subSystem.getMode()
					&& isreplicationEnabled) {
				StandbyReplicator sbr = new StandbyReplicator(this);
				sbr.setPeerId(peerId);
				sbr.setRedisWrapper(redisWrapper);
				sbr.setReplStoreManager(replStoreManager);
				sbr.start();
				break;
			}
		
//		switch (peerState) {
//		case PeerStateChangeEvent.PR_READY:
//			if(logger.isEnabledFor(Level.INFO)){
//				logger.info("PR_READY Event: Peer " + peerId + " is ready");
//			}
//			this.dataChannel.setSubsystem(psce.getSubsystem());
//			break;
//		case PeerStateChangeEvent.PR_DOWN_CLOSE_CONN:
//			if(logger.isEnabledFor(Level.INFO)){
//				logger.info("PR_DOWN_CLOSE_CONN Event: Peer " + peerId + " is down");
//			}
//			//remove subsys from allowed list
//			allowedSubsystemList.remove(peerId);
//			
//			// Remove destination from map
//			if(null != destinationMap.remove(peerId)) {
//				// Valid for K=1 (in N+K) only
//				if (logger.isDebugEnabled())
//					logger.debug("Going to update the counters");
//				// long currentReplicated = AseMeasurementUtil.counterReplicated.getCount();
//				// AseMeasurementUtil.counterCleanedUp.setCount(currentReplicated);  BPInd18171
//				// We are not resetting the counters in order to capture the snapshot of down time.
//				AseMeasurementUtil.counterBeingReplicated.setCount(0);
//
//				Iterator clusters = replStoreManager.getReplicationContextMap().values().iterator();
//				while(clusters.hasNext()) {
//					ConcurrentHashMap ctxtMap = (ConcurrentHashMap)clusters.next();
//					Iterator contexts = ctxtMap.values().iterator();
//					while(contexts.hasNext()) {
//						ReplicationContextImpl ctxt = (ReplicationContextImpl)contexts.next();
//                		ReplicationInfo info = (ReplicationInfo)ctxt.getReplicationInfo();
//                		info.setReplicated(false);
//					}
//				}
//			}
//			removeReplicationDestination(peerId);
//			if(logger.isEnabledFor(Level.INFO)){
//				logger.info(" Peers in DestinationMap : "+destinationSubsystems);
//			}
//			return 1;
//		case PeerStateChangeEvent.PR_READY_START_REPL:
//			if(logger.isEnabledFor(Level.INFO)){
//				logger.info("PR_READY_START_REPL Event: Peer " + peerId + " is up");
//			}
//			// Add destination to the map
//			Destination destination = null;
//			if (destinationMap.containsKey(peerId)) {
//				destination = (Destination)destinationMap.get(peerId);
//			} else {
//				destination = new Destination(peerId);
//				destinationMap.put(peerId, destination);
//			}
//
//			//BPInd18171 As part of new strategy, We will reset the counter only when 
//			// Peer comes UP or takeover happens. 
//			//Peer came up so need to reset all the three counters.
//			if(logger.isDebugEnabled()) {
//				logger.debug("Peer UP Event: Resetting the counters");
//			}
//			AseMeasurementUtil.counterCleanedUp.setCount(0);
//			AseMeasurementUtil.counterBeingReplicated.setCount(0);
//			AseMeasurementUtil.counterReplicated.setCount(0);
//
//
//			AseSubsystem subsystem = dataChannel.getSubsystem(peerId);
//			destinationSubsystems.put(peerId, subsystem);	
//			if(logger.isEnabledFor(Level.INFO)){
//				logger.info(" Peers in DestinationMap : "+destinationSubsystems);
//			}
//			if (subsystem != null && subsystem.isConnected()) {
//				destination.setConnect(true);
//				destination.setStartTime(System.currentTimeMillis());
//				if(psce.isDoReplication()){
//					if(logger.isDebugEnabled()){
//						logger.debug("Got do replication as true: START REPLICATION");
//					}
//					startBulkReplication(psce.getSubsystem().getId(), null);
//				}else{
//					if(logger.isDebugEnabled()){
//						logger.debug("Got do replication as false: NO REPLICATION");
//					}
//				}
//				return 1;
//			} else {
//				if(logger.isEnabledFor(Level.INFO)){
//					logger.info("DataChannel is not available to peer " + peerId);
//				}
//				
//				throw new PeerStateChangeException(PeerStateChangeException.DATA_CHANNEL_NOT_CONNECTED , "DataChannel not connected yet");
//			}			
		}
//		if (logger.isInfoEnabled()) {
//			logger.info(destinationMapToString());
//		}
		
		if(logger.isEnabledFor(Level.INFO)){
			logger.info("Leaving");
		}
		return 0;
	}

	public void removeReplicationDestination(String peerId)
	{
		logger.error(" removeReplicationDestination called to remove: "+peerId);
		if(null != destinationSubsystems.remove(peerId)) {
			stopBulkReplication(peerId);
			logger.error(" Removed AseSubsystem: "+peerId+" from destinationSubsystems"); 
		}
	}

//	public DataChannelProvider getDataChannel() {
//		return dataChannel;
//	}
//	
	public RedisWrapper  getRedisWrapper() {
		return redisWrapper;
	}

        public void addReplicationContext(ReplicationContextImpl ctxt){
                if(ctxt == null)
                        throw new IllegalArgumentException("The Replication Context cannot be NULL");

		if(logger.isDebugEnabled()){
			logger.debug("addReplicationContext IN with:"+ctxt);
		}

                if(this.replStoreManager == null){
			if(logger.isDebugEnabled()){
				logger.debug("No DataChannel available. So not adding replication Context");
			}
			return;
		}
	
		//Set the replication info...	
		ReplicationInfo info = (ReplicationInfo)ctxt.getReplicationInfo();
                if(info == null) {
                	info = this.createReplicationInfo(this.clusterId);
                        ctxt.setReplicationInfo(info);
                        ctxt.setClusterId(info.getClusterId());
                        ctxt.setSubsystemId(this.subsysId);
                }

                replStoreManager.createReplicationContext(ctxt);
		if(logger.isDebugEnabled()){
			logger.debug("addReplicationContext OUT.");
		}
        }

        public void removeReplicationContext(ReplicationContextImpl ctxt){
                if(ctxt == null)
                        throw new IllegalArgumentException("The Replication Context cannot be NULL");
		if(logger.isDebugEnabled()){
			logger.debug("removeReplicationContext IN with:"+ctxt);
		}

		if(this.replStoreManager == null){
			if(logger.isDebugEnabled()){
				logger.debug("No DataChannel available. So not removing replication Context");
			}
			return;
		}
		
		replStoreManager.removeReplicationContext(ctxt);
		if(logger.isDebugEnabled()){
			logger.debug("removeReplicationContext OUT.");
		}
        }
	
	/**
	 * Generates a replication message and sends to all connected peer systems
	 * in the cluster. The ReplicationInfo of each ReplicationContext is also updated.
	 * @param ctxt ReplicationContext
	 * @param event ReplicationEvent
	 */
	public void replicate(ReplicationContextImpl ctxt, ReplicationEvent event) {
                replicate(ctxt,event,null);
        }

   public void replicate(ReplicationContextImpl ctxt, ReplicationEvent event, String[] replicableIds) {
		if(event.getEventId().equals(ReplicationEvent.BULK_REPLICATION)) {
			_bulkReplicate(ctxt, event);
		} else {
			_replicate(ctxt, event, replicableIds);
		}
	}
   
	/**
	 * Overloaded method with disable create flag
	 * this is done to avoid creation of replication context if out of sequencing
	 * happens on datachannel counter
	 */
	public void replicate(ReplicationContextImpl ctxt, ReplicationEvent event,
					String[] replicableIds, boolean disableCreate, boolean useRepId) {
		if (logger.isDebugEnabled()) {
			logger.debug("replicate(context,evenet,replicableIds[],disableCreate, useRepid)::"
							+" diableCreate::"+ disableCreate+" useRep::"+useRepId);
		}
		if (event.getEventId().equals(ReplicationEvent.BULK_REPLICATION)) {
			_bulkReplicate(ctxt, event);
		} else {
			_replicate(ctxt, event, replicableIds, disableCreate, useRepId);
		}
	}
   

	private void _replicate(ReplicationContextImpl ctxt, ReplicationEvent event, String[] replicableIds) {
		_replicate(ctxt, event, replicableIds, false, false);
		
	}
	
	
	/**
	 * Overloaded method with disable create flag
	 * this is done to avoid creation of replication context if out of sequencing 
	 * happens on datachannel counter
	 * @param ctxt
	 * @param event
	 * @param replicableIds
	 * @param disableCreate
	 */
	private void _replicate(ReplicationContextImpl ctxt, ReplicationEvent event,
					String[] replicableIds, boolean disableCreate, boolean useRepId) {

		
		if (logger.isDebugEnabled()) {
			logger.debug("_replicate(context,evenet,replicableIds[],disableCreate, useRepid)::"
							+" diableCreate::"+ disableCreate+" useRep::"+useRepId);
		}

		if(!isreplicationEnabled){
			if (logger.isInfoEnabled()) {
				logger
					.info("retrning replictaion is disabled");
			}
			return;
		}

		String eventId = event.getEventId();
		String ctxtId = ctxt.getId();
		ReplicationMessage msg = null;

		if (logger.isInfoEnabled()) {
			logger
				.info("Entering _replicate(ReplicationContextImpl, ReplicationEvent) for ctxt-id = "
								+ ctxtId + " and event-id = " + eventId + " subsystem mode is "+subSystem.getMode());
		}

		//If the mode is Not FT, return immediately....
		if ( (AseModes.FT_N_PLUS!=subSystem.getMode())&&AseModes.FT_N_PLUS_LITE!=subSystem.getMode()) {//!AseModes.isFtMode(subSystem.getMode())||
			if (logger.isInfoEnabled())
				logger.info("Replicate: non FT mode, So not doing any replication... returning.");
			//mark replication completed for replicables to manage memory better;
			if (replicableIds == null){
				ctxt.replicationCompleted(false);
			}else{
				ctxt.replicationCompleted(replicableIds,false);
			}
			return;
		}

		//If the policy Manager says, no Replication required, then don't do any replication.
		short policy = policyMgr.query(event);

		if (policy == Policy.NO_REPLICATION) {
			if (logger.isInfoEnabled())
				logger.info("Replicate: no action required from Policy Manager... returning");
			//mark replication completed for replicables to manage memory better;
			if (replicableIds == null){
				ctxt.replicationCompleted(false);
			}else{
				ctxt.replicationCompleted(replicableIds,false);
			}
			return;
		}

		try {
			//If info == null, Create the replicationInfo for the first time.
			ReplicationInfo info = (ReplicationInfo) ctxt.getReplicationInfo();
			if (info == null) {
				info = this.createReplicationInfo(this.clusterId);
				ctxt.setReplicationInfo(info);
				ctxt.setClusterId(info.getClusterId());
				ctxt.setSubsystemId(this.subsysId);
			}

			//Define some veriables that we will use it later
			short replicationSeq = (short) (info.getSequenceNo() + 1);
			long replicationTime = System.currentTimeMillis();

			//Get the name of all the destinations, for which we need to replicate.
			//Get their corresponding subsystem information...
			//AseSubsystem[] subsystems = this.getConnectedSubsystems(info.getClusterId());

			// Filter out those AseSubsystems who should NOT have 
			// this ReplicationContext replicated to.
			//subsystems = filterRecipients(subsystems, ctxt);
			AseSubsystem[] subsystems = filterRecipients(ctxt);

			// Check if this context already exists in data channel data store
			boolean alreadyInDataChannel = replStoreManager.findReplicationContextById(ctxtId) != null;

			// Just do a check here to whether or not if any of the destination is UP.
			if (subsystems == null || subsystems.length == 0) {
				if (!alreadyInDataChannel) {
					// This context does not already exists into data channel map, add it now
					if (ctxt.isReadyForReplication()) {
						// We are adding it to the DataChannel without sending it because,
						// if there is no destination UP right now, but it may come UP after
						// some time. So we need to replicate this at that time.
						// In case, the destination was DOWN for the whole lifetime of this
						// ctxt, it will be cleaned up by the CLEANUP event.
						if (logger.isDebugEnabled())
							logger.debug("Adding replication context to the DataChannel");
						replStoreManager.createReplicationContext(ctxt);
					}
				} else if (eventId.equals(ReplicationEvent.CLEAN_UP)) {
					//
					// CLEANUP Event
					//
					if (logger.isDebugEnabled())
						logger.debug("Removing the replication context from the DataChannel");
					replStoreManager.removeReplicationContext(ctxt);
				}
				if (eventId.equals(ReplicationEvent.CLEAN_UP)) {
					//Increment the counter for replication cleanup for the ACTIVE side

					//BPInd18171: Not Incrementing the counter. As we have changed the meaning
					// Now it represent thr No of CLEANUP event sent to the peer.
					//AseMeasurementUtil.counterCleanedUp.increment();

				}
				// If none of the PEERs are UP, then no need to create the packet and send it.
//				if (logger.isDebugEnabled())
//					logger
//						.debug("No destination is connected. So not doing any replication... returning");
				//mark replication completed for replicables to manage memory better;
//				if (replicableIds == null){
//					ctxt.replicationCompleted(true);
//				}else{
//					ctxt.replicationCompleted(replicableIds,true);
//				}
//				return;
			}

//			if (logger.isDebugEnabled()) {
//				for (int i = 0; i < subsystems.length; i++) {
//					logger.debug("Replicating to: " + subsystems[i].getId());
//				}
//			}

			// Identify the Replication action.
			if (logger.isDebugEnabled())
				logger.debug("Identifying the replication action....");
			short action;

			if (eventId.equals(ReplicationEvent.CLEAN_UP)) {
				//
				// CLEANUP Event
				//

				if (alreadyInDataChannel) {
					if (logger.isDebugEnabled())
						logger.debug("Removing the replication context from the DataChannel");
					replStoreManager.removeReplicationContext(ctxt);
				}

				//if it is a CLEANUP message, Check whether it is already replicated OR not.
				//If no, then we do not need to send it to the peers
				//If yes, then update the measurement counters
//				if (!info.isReplicated()) {
//					if (logger.isDebugEnabled())
//						logger
//							.debug("Not incrementing the CLEANUP counter as this object was never replicated");
//					return;
//				}

				//Increment the counter for replication cleanup for the ACTIVE side
				AseMeasurementUtil.counterCleanedUp.increment();
				AseMeasurementUtil.counterBeingReplicated.decrement();

				action = ReplicationMessage.CLEANUP;
			} else {
				action = ReplicationMessage.REPLICATE;

				// If info seq no. is 0, full replication is needed
				if (!(disableCreate)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Allowed CREATE");
					}

					if (info.getSequenceNo() == 0) {
						action = ReplicationMessage.CREATE;
						if (logger.isDebugEnabled()) {
							logger.debug("Sequence No is 0... setting action to CREATE");
						}
					} else {
						//If any of the connected host is OUT of sequence, then we need to replicate FULL,
						//So set the action = CREATE
//						for (int i = 0; action != ReplicationMessage.CREATE
//										&& i < subsystems.length; i++) {
//							if (info.getSequenceNo() > info
//								.getLastSequenceNo(subsystems[i].getId())) {
//								action = ReplicationMessage.CREATE;
//								if (logger.isDebugEnabled()) {
//									logger.debug("Seq No " + info.getSequenceNo()
//													+ "is greater than last seq no "
//													+ info.getLastSequenceNo(subsystems[i].getId())
//													+ "... setting action to CREATE");
//								}
//
//								break;
//							}
//						} // for
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Disabled CREATE");
					}
				}//@end if disablecreate

				// If this context is not already in store then it has to be full replication
				if (!alreadyInDataChannel) {
					if (logger.isDebugEnabled())
						logger.debug("Adding the replication context to the DataChannel");
					replStoreManager.createReplicationContext(ctxt);
					action = ReplicationMessage.CREATE;
				}
			}

			//Added for Selective Replication....
			action = (action == ReplicationMessage.CREATE && ctxt instanceof SelectiveReplicationContext) ? ReplicationMessage.REINIT : action;

			if (logger.isInfoEnabled()) {
				logger.info("The identified replication action is ::"
								+ ReplicationMessage.getActionString(action));
			}

			// Create a replication message object for sending out....
			msg = (ReplicationMessage) replStoreManager.getMessageFactory().createMessage(
							MessageTypes.REPLICATION_MESSAGE, PeerMessage.MESSAGE_OUT);

			// Set message header infomation
			if (logger.isDebugEnabled()) {
				logger.debug("Filling the header information for the message....");
			}
			msg.setPartitionId(info.getClusterId());
			msg.setAction(action);
			msg.setContextId(ctxtId);
			msg.setMode(policy);
			msg.setSequenceNo(replicationSeq);
			msg.setReplicationTime(System.currentTimeMillis());
			
			if(useRepId && replicableIds !=null && replicableIds.length > 0){
				msg.setRepId(replicableIds[0]);
			}

			// Timer Replication
			int type = ReplicationEvent.TYPE_REGULAR;

			if(eventId == ReplicationEvent.TIMER_CREATION 
					|| eventId == ReplicationEvent.TIMER_CANCELLATION
					|| eventId == ReplicationEvent.TIMER_EXPIRY) {

				type = ReplicationEvent.TYPE_TIMER;
			}

			if(event.isReinviteTranComplete() == true) {
				type = ReplicationEvent.TYPE_REINVITE;
			}

			// Construct the body of the message.
			if (action == ReplicationMessage.CREATE) {
				//During CREATE, serialize the complete object....
				if(isKryoSerializer) {
					Kryo kryo = KryoPoolManager.borrow();
					Output op = new Output((ObjectOutputStream) msg.getObjectOutput());
					kryo.writeClassAndObject(op, ctxt);
					op.flush();
					KryoPoolManager.release(kryo);
				}else{
					msg.getObjectOutput().writeObject(ctxt);
				}
			} else if (action == ReplicationMessage.REPLICATE) {
				//During REPLICATE, do an incremental replication.
//				ObjectOutputStream fileOp = new ObjectOutputStream(new FileOutputStream(
//						"/LOGS/CAS/ReplicatePacket.bin#" +
//								eventId + "#" + action + "#" +
//								type + "#" +
//								System.currentTimeMillis()));
				if (replicableIds == null) {
					ctxt.writeIncremental(msg.getObjectOutput(), type);
					//ctxt.writeIncremental(fileOp, type);
				} else {
					ctxt.writeIncremental(msg.getObjectOutput(), replicableIds);
					//ctxt.writeIncremental(fileOp, replicableIds);
				}
			//	fileOp.flush();
		//		fileOp.close();
			} else if (action == ReplicationMessage.REINIT) {
				ctxt.writeExternal(msg.getObjectOutput());
			}
			
			if (AseModes.FT_N_PLUS_LITE == subSystem.getMode() && isreplicationEnabled) {
				
				String replId = msg.getRepId();
				RedisReplicationTask task= new RedisReplicationTask(msg
						.getContextId().hashCode(), ctxtId, action, msg.getPacket(),replId, host.getSubsystemId());
				redisManager.enqueueTask(task);				
			} 

			// Callback method on context
			if (action != ReplicationMessage.CLEANUP) {
				if (replicableIds == null) {
					ctxt.replicationCompleted(false);
				} else {
					ctxt.replicationCompleted(replicableIds, false);
				}
			}

			//Now update the last replicated info...
			if (logger.isDebugEnabled())
				logger.debug("Now updating the replication info");
			info.setSequenceNo(msg.getSequenceNo());
			info.setLastReplicationTime(msg.getReplicationTime());
//			for (int i = 0; i < subsystems.length; i++) {
//				info.setLastReplicatedOn(subsystems[i].getId(), msg.getReplicationTime(),
//								msg.getSequenceNo());
//				if (logger.isDebugEnabled()) {
//					logger.debug("Replicated ctxt :" + ctxtId + " to " + subsystems[i].getId()
//									+ " at " + replicationTime + " with seqNo "
//									+ msg.getSequenceNo());
//				}
//			}

			//Increment the Measurement Counters for the ACTIVE side
			if (msg.getSequenceNo() == 1 && msg.getAction() == ReplicationMessage.CREATE) {
				synchronized (info) {
					if (!info.isReplicated()) {
						AseMeasurementUtil.counterReplicated.increment();
						AseMeasurementUtil.counterBeingReplicated.increment();
						info.setReplicated(true);
					}
				}
			}
		} catch (MessageParseException ex) {
			logger.error("ReplicationMessage parsing failed for ctxt-id = " + ctxtId, ex);

			//Increment counters for this Packet Serialization Failure
			AseMeasurementUtil.counterSerializationFail.increment();
			AseMeasurementUtil.thresholdSerializationFail.increment();
		} catch (IOException ex) {
			logger.error("Replication failed for ctxt-id = " + ctxtId, ex);

			//Increment counters for this Packet Serialization Failure
			AseMeasurementUtil.counterSerializationFail.increment();
			AseMeasurementUtil.thresholdSerializationFail.increment();
		} catch (Exception ex) {
			logger.error("Replication failed for ctxt-id = " + ctxtId, ex);
		} finally {
			// Close output stream now
			if (msg != null) {
				try {
					((ReplicationMessageFactory)replStoreManager.getMessageFactory()).releaseMessage(msg);
				} catch(Exception ex) {
					logger.error("Message cleanup", ex);
				}
			}
			if (logger.isInfoEnabled()) {
				logger.info("Completed replication for replication context = " + ctxtId);
			}
		}
	}
	
	
	
	
	/**
	 * Generates a replication message and sends to all connected peer systems
	 * in the cluster. The ReplicationInfo of each ReplicationContext is also updated.
	 * @param ctxt ReplicationContext
	 * @param event ReplicationEvent
	 */
	private void _bulkReplicate(ReplicationContextImpl ctxt, ReplicationEvent event) {
		String ctxtId = ctxt.getId();
		ReplicationMessage msg = null;

		if(logger.isInfoEnabled()) {
			logger.info("Entering _bulkReplicate(ReplicationContextImpl, ReplicationEvent) for ctxt-id = "
							+ ctxtId);
		}
		
		//If the policy Manager says, no Replication required, then don't do any replication.
		short policy = policyMgr.query(event);
		if (policy == Policy.NO_REPLICATION) {
			if (logger.isInfoEnabled())
				logger.info("Replicate: no action required from Policy Manager... returning");
			return;
		}

		String hostId = (String)event.getAttribute();
		try {
			//If info == null, Create the replicationInfo for the first time.
			ReplicationInfo info = (ReplicationInfo)ctxt.getReplicationInfo();

			//Define some veriables that we will use it later
			long replicationTime = System.currentTimeMillis();

			//Get corresponding subsystem information...
			AseSubsystem[] subsystems = new AseSubsystem[1];
			boolean firstReplication = info.getSequenceNo() == 0;
			boolean replicateToAll = true; // always true for K=1
			short action = (ctxt instanceof SelectiveReplicationContext) ?
                                ReplicationMessage.REINIT : ReplicationMessage.CREATE;

			short replicationSeq = (short)info.getSequenceNo();
			if(firstReplication) {
				// Replicate to all connected subsystems
				replicationSeq++;
			} 
			subsystems[0] = getSubsystem(hostId);

			// Filter out those AseSubsystems who should NOT have 
			// this ReplicationContext replicated to.
			subsystems = filterRecipients( ctxt);
            
			if((subsystems == null) || (subsystems.length == 0)) {
				// Replication for hosts denied by filter. Return.
				if (logger.isInfoEnabled())
					logger.info("All hosts filtered out. Returning.");
				return;
			}

			String[] hosts;
			hosts = new String[subsystems.length];
			for(int i=0; i < subsystems.length; i++) {
				hosts[i] = subsystems[i].getId();
			}

			// Create a replication message object for sending out....
			msg = (ReplicationMessage)replStoreManager.getMessageFactory().createMessage(
					MessageTypes.REPLICATION_MESSAGE, PeerMessage.MESSAGE_OUT);
			
			// Set message header infomation
			if (logger.isDebugEnabled())
				logger.debug("Filling the header information for the message....");
			msg.setPartitionId(info.getClusterId());
			msg.setAction(action);
			msg.setContextId(ctxtId);
			msg.setMode(policy);
			msg.setSequenceNo(replicationSeq);
			msg.setReplicationTime(replicationTime);
			msg.setDestinations(hosts);

			// Serialize the complete object....
			if(action == ReplicationMessage.CREATE){
				if(isKryoSerializer) {
					Kryo kryo = KryoPoolManager.borrow();
					Output op = new Output((ObjectOutputStream) msg.getObjectOutput());
					kryo.writeClassAndObject(op, ctxt);
					op.flush();
					KryoPoolManager.release(kryo);
				}else{
					msg.getObjectOutput().writeObject(ctxt);
				}
			}else if(action == ReplicationMessage.REINIT){
				ctxt.writeExternal(msg.getObjectOutput());
			}

			//Now send the message to all the found destinations.			
	//		dataChannel.send(subsystems, msg);
			
		//	redisWrapper.getListOperations().pushInListSerializedData("REPL_MSG",msg, "LEFT");
			
			// If this is the first time this context getting replicated, then make a call to
			// callback method replicationCompleted()
			if(replicateToAll) {
				if (logger.isDebugEnabled())
					logger.debug("Calling replicationCompleted()");
				ctxt.replicationCompleted(false);
				info.setSequenceNo(msg.getSequenceNo());
				info.setLastReplicationTime(msg.getReplicationTime());
			}

			//Now update the host specific last replicated info...
			if (logger.isDebugEnabled())
				logger.debug("Now updating the host specific replication info");
			for(int i=0; i < subsystems.length; i++) {
				info.setLastReplicatedOn(subsystems[i].getId(), msg.getReplicationTime(), msg.getSequenceNo());
				if(logger.isDebugEnabled()) {
					logger.debug("Replicated ctxt :" + ctxtId + " to " + subsystems[i].getId()
						+ " at " + replicationTime + " with seqNo " + msg.getSequenceNo());
				}
			}
			
			synchronized(info) {
				if(!info.isReplicated()) {
					if(replicateToAll) {
						AseMeasurementUtil.counterBeingReplicated.increment();
					}
					AseMeasurementUtil.counterReplicated.increment();
					info.setReplicated(true);
				}
			}
		} catch(MessageParseException ex) {
			logger.error("ReplicationMessage parsing failed for ctxt-id = " + ctxtId, ex);

			//Increment counters for this Packet Serialization Failure
			AseMeasurementUtil.counterSerializationFail.increment();
			AseMeasurementUtil.thresholdSerializationFail.increment();
		} catch(IOException ex){
			logger.error("Replication failed for ctxt-id = " + ctxtId, ex);

			//Increment counters for this Packet Serialization Failure
			AseMeasurementUtil.counterSerializationFail.increment();
			AseMeasurementUtil.thresholdSerializationFail.increment();
		} catch(Exception ex) {
			logger.error("Bulk replication failed for ctxt-id = " + ctxtId, ex);
		} finally {
			// Close output stream now
			if(msg != null) {
				try {
					((ReplicationMessageFactory)replStoreManager.getMessageFactory()).releaseMessage(msg);
				} catch(Exception ex) {
					logger.error("Message cleanup", ex);
				}
			}

			if(logger.isInfoEnabled()){
				logger.info("Completed bulk replication for replication context = " + ctxtId);
			}
		}
	}
	
        
        /**
         * This method is invoked to notify the ReplicationManager that it 
         * should allow objects belonging to the specified application and 
         * version to be replicated to the specified subsystem.
         */
        public void allowReplicationFor(String subsysId, String appName) {
            if (logger.isDebugEnabled()) {
                logger.debug("allowReplicationFor():  Allowing replication to subsystem, \"" + subsysId + "\" for application, \"" + appName + "\"");
            }
            
            Collection appNames = (Collection)this.replicableSubsystems.get(subsysId);
            
            if (appNames == null) {
                this.replicableSubsystems.put(subsysId, appNames = new HashSet());
            }
            appNames.add(appName);
        }
        
        
        /**
         * This method is invoked to notify the ReplicationManager that it
         * should NOT allow objects belonging to the specified app and 
         * version to be replicated the specified subsystem.
         */
        public void denyReplicationFor(String subsysId, String appName) {
            if (logger.isDebugEnabled()) {
                logger.debug("denyReplicationFor():  Denying replication to subsystem, \"" + subsysId + "\" for application, \"" + appName + "\"");
            }
            
            Collection appNames = (Collection)this.replicableSubsystems.get(subsysId);

            if (appNames != null) {
                appNames.remove(appName);
            }            
            
        }
                        
        
        /**
         * Called by the "replicate" method to filter out those AseSubsystems 
         * that have been excluded from having the specified ReplicationContext
         * sent to them.
         */
        //private AseSubsystem[] filterRecipients(AseSubsystem[] peers, ReplicationContext context) {
        private AseSubsystem[] filterRecipients( ReplicationContext context) {
            /*if (peers == null || peers.length == 0) {
                return peers;
            }*/

            if ( destinationSubsystems.size() == 0) {
            	return null;
            }
            
            Collection appInfo = (Collection)context.getAppInfo();

			AseSubsystem[] retArray = new AseSubsystem[destinationSubsystems.size()];
                    
			if (appInfo == null || appInfo.isEmpty()) {
					if (logger.isDebugEnabled())
						logger.debug("Returning with appInfo null/empty");
            		return (AseSubsystem[])destinationSubsystems.values().toArray(retArray);
			}

                        
            //Collection recipients = new HashSet(peers.length);
            Collection recipients = new HashSet( destinationSubsystems.size() );

			Iterator dIter = destinationSubsystems.values().iterator();

			while(dIter.hasNext()) {
                AseSubsystem peer = (AseSubsystem)dIter.next();
                
                if(! (allowedSubsystemList.contains(peer.getId()) ) ){
                	if(logger.isDebugEnabled()){
                		logger.debug("Data channel with peer is not up. Replication is not required.");
                	}
                	continue;
                }
                
                Collection appNames = (Collection)this.replicableSubsystems.get(peer.getId());
                
                if (appNames == null || appNames.isEmpty()) {
                    continue;
                }
                 
                Iterator iterator = appInfo.iterator();
                        
                while (iterator.hasNext()) {
                    AppInfo info = (AppInfo)iterator.next();
                    if (appNames.contains(info.getApplicationId())) {
                        recipients.add(peer);
                    }else{
                    	//@abaxi: If multiple application info added in AseIc then include in recipients if all are present in appNames
                    	recipients.remove(peer);
                    }
                }
            }
            
	    retArray = new AseSubsystem[recipients.size()];
            return (AseSubsystem[]) recipients.toArray(retArray);
        }
        
        
	private ReplicationInfo createReplicationInfo(String clusterId){
		if(logger.isInfoEnabled()){
			logger.info("Creating the replicationInfo for cluster :" + clusterId);
		}
		
		ReplicationInfo info = new ReplicationInfo();
		info.setClusterId(clusterId);
		info.setSequenceNo((short)0);
		
		return info;
	}
	
	public void setReplicationInfo(ReplicationMessage msg, ReplicationContextImpl ctxt){
			
		if(logger.isInfoEnabled()){
			logger.info("Entering the setReplicationInfo method...");
		}
		
		//Create the replication context if it is NULL
		ReplicationInfo info =  (ReplicationInfo)ctxt.getReplicationInfo();
		if(info == null){
			if(logger.isInfoEnabled()){
				logger.info("Replication INFO is NULL. Creating it...");
			}
			info = this.createReplicationInfo(msg.getPartitionId());
			ctxt.setReplicationInfo(info);
			ctxt.setClusterId(info.getClusterId());
			ctxt.setSubsystemId(this.subsysId);
		}
		
		//Upadte the last timestamp and the last sequence number
		if(logger.isInfoEnabled()){
			logger.info("Updating the replication information for :" + msg.getContextId());
		} 
		info.setClusterId(msg.getPartitionId());
		info.setSequenceNo(msg.getSequenceNo());
		String[] dests = msg.getDestinations();
		for(int i=0;i<dests.length;i++){
			info.setLastReplicatedOn(dests[i], msg.getReplicationTime(), msg.getSequenceNo());
		}
	}
	
	/**
	 * Get all known destinations in a cluster
	 * @return array of AseSubsystem Id
	 */
	private String[] getDestinations() {
		if(logger.isInfoEnabled()){
			logger.info("get destinations...\n");
		}
		
		String[] destinations = new String[destinationMap.size()];
		int k = 0;
		for (Iterator i = destinationMap.values().iterator(); i.hasNext();) {
			Destination destination = (Destination)i.next();
			
			if(logger.isInfoEnabled()){
				logger.info("\t" + destination.toString() + "\n");
			}
			
			destinations[k++] = destination.getSubsystemId(); 
		}
		return destinations;
	}
	
	private String destinationMapToString() {
		StringBuffer buf = new StringBuffer("\n** Destination Map **\n");
		for (Iterator i = destinationMap.values().iterator(); i.hasNext();) {
			Destination destination = (Destination)i.next();
			buf.append(destination.toString() + "\n");
		}
		return buf.toString();
	}
	
	/**
	 * Get all connected AseSubsystems in a cluster
	 * @param clusterId String name of a cluster
	 * @return array of AseSubsystems
	 */
	private AseSubsystem[] getConnectedSubsystems(String clusterId) {
		if(logger.isInfoEnabled()){
			logger.info("Entering getConnectedSubsystems for Cluster :" + clusterId);
		}
		String[] dests = this.clusterMgr.getReplicationDestinations(clusterId);
		ArrayList list = new ArrayList();
		for(int i=0; dests != null && i<dests.length;i++){
			Destination destination = (Destination) this.destinationMap.get(dests[i]);
			if(destination == null)
				continue;
			AseSubsystem subsys = destination.getSubsystem();
			if(subsys.isConnected()){
				list.add(subsys);
			}
		}
		
		if(logger.isInfoEnabled()){
			logger.info("Connected Subsystems :" + list);
		}
		
		AseSubsystem[] subsystems = new AseSubsystem[list.size()];
		subsystems = (AseSubsystem[]) list.toArray(subsystems);
		return subsystems;
	}
	
	/**
	 * Get AseSubsystem specified by subsystemId
	 * @param subsystemId String
	 * @return AseSubsystem or null if not found
	 */
	private AseSubsystem getSubsystem(String subsystemId) {
		if (destinationMap.containsKey(subsystemId)) {
			Destination destination = (Destination)destinationMap.get(subsystemId);
			return destination.getSubsystem();
		}
		return null;
	}
	
	/**
	 * Starts one-time thread
	 * @param hostId Destination host ID
	 */
	private void startBulkReplication(String hostId, String appId) {
		if(!AseModes.isFtMode(subSystem.getMode())) {
			if (logger.isDebugEnabled())
				logger.debug("System is not running in FT mode. So not started the BulkReplicator.");
			return;
		}
		BulkReplicator replicator = new BulkReplicator(hostId, this);
		replicator.applicationId = appId;
		this.bulkReplicatorList.add(replicator);
		replicator.start();
	}
	
	/**
	 * Stops one-time thread
	 * @param hostId Destination host ID
	 */
	private void stopBulkReplication(String hostId) {
		if (hostId == null)
			return;
		Iterator<BulkReplicator> it = this.bulkReplicatorList.iterator();
		for (; it.hasNext();) {
			BulkReplicator replicator = it.next();
			if (replicator != null && replicator.m_br_hostId.equals(hostId)) {
				replicator.shutdown();
				it.remove();
			}

		}
	}

	/**
	 * Implemented from AppSyncMessageListener and invoked by the VersionManager
	 * whenever one of our peers in the cluster sends us an AppSyncMessage.
	 * 
	 * @see com.baypackets.ase.control.VersionManager
	 */
//	public void handleMessage(com.baypackets.ase.control.AppSyncMessage message) {
//		if (logger.isDebugEnabled()) {
//			logger.debug("handleMessage(): Received AppSyncMessage from: "
//					+ message.getSenderId()+" for application:"+message.getApplicationId());
//		}
//
//		if (this.clusterRole != AseRoles.ACTIVE) {
//			if (logger.isDebugEnabled()) {
//				logger.debug("handleMessage(): Not in ACTIVE role, so ignoring message.");
//			}
//			return;
//		}
//
//		if (message.getMsgType() == AppSyncMessage.ACK) {
//			if (logger.isDebugEnabled()) {
//				logger.debug("handleMessage(): Incoming message was an ACK, so ignoring it.");
//			}
//			return;
//		}
//
//		if (logger.isDebugEnabled()) {
//			logger.debug("handleMessage(): Sending ACK to subsystem: "
//					+ message.getSenderId());
//		}
//
//		// Send an acknowledgement.
//		try {
//			AppSyncMessage ack = new AppSyncMessage(AppSyncMessage.MESSAGE_OUT);
//			ack.setApplicationId(message.getApplicationId());
//			ack.setSenderId(this.subsysId);
//			ack.setMsgType(AppSyncMessage.ACK);
//			this.versionMgr.sendAppSyncMessage(ack);
//		} catch (Exception e) {
//			logger.error("Error occured while sending AppSync-ACK message", e);
//		}
//
//		if (message.getMsgType() == AppSyncMessage.APP_DEPLOYED) {
//			this.allowReplicationFor(message.getSenderId(),
//					message.getApplicationId());
//			this.startBulkReplication(message.getSenderId(),
//					message.getApplicationId());
//		} else if (message.getMsgType() == AppSyncMessage.APP_NOT_DEPLOYED) {
//			this.denyReplicationFor(message.getSenderId(),
//					message.getApplicationId());
//			this.resetContextIdsForHostAndApp(clusterId,
//					message.getApplicationId(), RESET);
//		}
//	}

	public void removeContextsForAppId(String applicationId) {
		logger.error("Remove contexts for appid::"+applicationId);
		
		ConfigRepository config = (ConfigRepository) Registry
				.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String currRole = config.getValue(Constants.OID_CURRENT_ROLE);
		
		if ((currRole != null) && currRole.equalsIgnoreCase("Active")) {
			logger.error("Active SAS not removing replication Contexts form Self.::"+applicationId);
			return;
		}
		
		if (replStoreManager != null) {
			Iterator clusterIt = replStoreManager.findClusterIds();
			while (clusterIt.hasNext()) {
				Entry ent = (Entry) clusterIt.next();
				String hostId = (String) ent.getKey();
				this.resetContextIdsForHostAndApp(hostId, applicationId, REMOVE);
			}
		}

	}

	private void resetContextIdsForHostAndApp(String hostId,
			String applicationId, int action) {
		Thread resetThread = new Thread(new ResetCtxt(hostId, applicationId,
				action));
		resetThread.setName("ResetThread_" + hostId);
		resetThread.start();
	}

	/**
	 * Implemented from DataChannelSyncMessageListener and invoked by the
	 * DataChannelPool whenever one of our peers in the cluster sends us
	 * an DataChannelSyncMessage.
	 * 
	 * @see com.baypackets.ase.channel.DataChannelPool
	 */
//	public void handleMessage(DataChannelSyncMessage message) {
//		if (logger.isDebugEnabled()) {
//			logger.debug("handleMessage(): Received DataChannelSyncMessage from: " + message.getSubsysId());
//		}
//
//		if (this.clusterRole != AseRoles.ACTIVE) {
//			if (logger.isDebugEnabled()) {
//				logger.debug("handleMessage() dataSYnc: Not in ACTIVE role, so ignoring message.");
//			}
//			return;
//		}
//		
//		//enable replication for subsytem
//		if(!allowedSubsystemList.contains(message.getSubsysId())) {	// to support n+1 - saneja
//			allowedSubsystemList.add(message.getSubsysId());
//			startBulkReplication(message.getSubsysId(), null);
//		}
//	}
       
	// As ThreadOwner
	public int threadExpired(MonitoredThread thread) {
		logger.error(thread.getName() + " expired");

		// Print the stack trace
		StackDumpLogger.logStackTraces();

		return ThreadOwner.SYSTEM_RESTART;
	}

	private class Activator extends MonitoredThread {
		private static final short DEFAULT_THREADPOOL_SIZE = 4;
		private static final short DEFAULT_MIN_ACTIVE_THREADS = 50; // percentage

		private boolean m_a_stopped = false;
		private ThreadOwner m_a_threadOwner = null;
		private ThreadPool m_a_threadPool = null;

		private String m_a_clusterId;

		public Activator(String clusterId, ThreadOwner thOwner) {
			super("Activator", AseThreadMonitor.getThreadTimeoutTime(),
					BaseContext.getTraceService());

			m_a_clusterId = clusterId;
			int tpSize = DEFAULT_THREADPOOL_SIZE;

			// Read the threadpool size property
			try {
				String threadNum = BaseContext.getConfigRepository().getValue(
							Constants.PROP_MT_ACTIVATOR_THREAD_POOL_SIZE);
				if(threadNum != null) {
					tpSize = Integer.parseInt(threadNum.trim());
				}
			} catch(Exception exp) {
				logger.error( "Error in getting threadPool size", exp);
			}

			// Instantiate the thread pool
			try {
				m_a_threadPool = new ThreadPool(	tpSize,
													false,
													"Activator",
													null, // WorkHandler is optional
													thOwner,
													DEFAULT_MIN_ACTIVE_THREADS);
				this.m_a_threadPool.setThreadMonitor(threadMonitor);
			} catch (Exception ex) {
				logger.error( "Error in creating threadPool", ex);
			}

			m_a_threadOwner = thOwner;
		}

		public void start() {
			m_a_threadPool.start();
			super.start();
		}

		public void run() {
			if (logger.isInfoEnabled())
				logger.info("Activator thread is started.");

			// Register thread with thread monitor
			try {
				// Set thread state to idle before registering
				this.setThreadState(MonitoredThreadState.Idle);

				threadMonitor.registerThread(this);
			} catch(ThreadAlreadyRegisteredException exp) {
				logger.error("This thread is already registered with Thread Monitor", exp);
			}

			try {
				Iterator iterator = replStoreManager.findReplicationContextByCluster(m_a_clusterId);
				int count = 0;
				Thread currThread = Thread.currentThread();
				logger.error("Starting activation");

				while (iterator.hasNext()) {
					ReplicationContext ctxt = (ReplicationContext)iterator.next();
					m_a_threadPool.submit(ctxt.getId().hashCode(), new ActivationItem(ctxt));
				}

				while(!m_a_stopped && !m_a_threadPool.isEmpty()) {
					try {
						Thread.sleep(5000);
					} catch(Throwable thr) {
						logger.error("Sleeping", thr);
					}
				}

				if(!m_a_stopped) {
					// Remove collected contexts now
					try {
						iterator = replStoreManager.findClusterIds();
						while(iterator.hasNext()) {
							// Remove context for other cluster-ids as it is not a standby
							// for them anymore
							Map.Entry e = (Map.Entry) iterator.next();
                			String ith_clusterId = (String)e.getKey();
                			if( ith_clusterId == null || ith_clusterId.equals(m_a_clusterId) )
                				continue;
                			ConcurrentHashMap val = (ConcurrentHashMap) e.getValue();
							val.clear();
							iterator.remove();
						}//while
					} catch(Exception exp) {
						logger.error("Error removing the replication context", exp);
					}
				}

				logger.error("Finished activation");
			} catch(Throwable thr) {
				logger.error("Error in activation", thr);
			} finally {
				// Wait until all the items are processed
				while(!m_a_threadPool.isEmpty()) {
					try {
						sleep(10000);
					} catch(Throwable thr) {
						logger.error("Sleeping", thr);
					}
				}

				// Now shutdown Activator threadpool
				m_a_threadPool.shutdown();

				// Unregister thread with thread monitor
				try {
					threadMonitor.unregisterThread(this);
				} catch(ThreadNotRegisteredException exp) {
					logger.error("This thread is not registered with Thread Monitor", exp);
				}
			}
			if (logger.isInfoEnabled())
				logger.info("Activator thread shuting down...");
		}

		public void shutdown() {
			this.m_a_stopped = true;
			this.m_a_threadPool.shutdown();
		}

		public ThreadOwner getThreadOwner() {
			return m_a_threadOwner;
		}
	}// class Activator ends

	private class ActivationItem implements Work {
		private ReplicationContext m_ai_ctxt;

		public ActivationItem(ReplicationContext ctxt) {
			m_ai_ctxt = ctxt;
		}

		public int getTimeout() {
			return 100;
		}

		public WorkListener getWorkListener() {
			return null;
		}

		public void execute() {
			try {
				if(logger.isInfoEnabled()) {
					logger.info("Activating the replication context:" + m_ai_ctxt);
				}
                                
				// Now activate the replication context
				m_ai_ctxt.activate();
				
				// Add it to the PrintHandler so that this can be viewed using the
				// telnet interface
				PrintInfoHandler.instance().addValue(	Constants.CTG_ID_ACTIVATED,
														m_ai_ctxt.getId());
			} catch(Throwable e) {
				logger.error("Error activating the replication context: " + m_ai_ctxt, e);
			}
		}
	}// ActivationItem ends

        
				/* TODO PKUMAR	
					case ReplicationMessage.REINIT:
                                                logger.debug("Going to re-initialize the replication context using the message....");
                                                if(ctxtAvailable) {
                                                        ctxt.readExternal(m_sri_msg.getObjectInput());
                                                        setReplicationInfo(m_sri_msg, ctxt);
                                                        ctxt.partialActivate();
                                                } else {
                                                        logger.error("Unable to get context for read external:" + m_sri_msg.getContextId());
                                                }
                                                break;
				*/
                
        /**
         * Called by the StandbyReplicator thread to verify that all objects
         * contained in the given ReplicationContext belong to applications
         * that are deployed on this host.
         */
        public boolean isCompatible(ReplicationContext context) {
            if (logger.isDebugEnabled()) {
                logger.debug("Verifying that all objects contained in the received ReplicationContext belong to applications deployed on this host...");
            }
            
            Collection appInfo = context.getAppInfo();
            
            if (appInfo == null || appInfo.isEmpty()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("No application info included in the ReplicationContext, so assuming that the context is OK to accept.");                    
                }
                return true;
            }
            
            Iterator iterator = appInfo.iterator();
                
            while (iterator.hasNext()) {
                AppInfo info = (AppInfo)iterator.next();
                    
                if (logger.isDebugEnabled()) {
                    logger.debug("Checking if application, \"" + info.getApplicationId()  + "\" is installed on this server.");
                }
                    
                if (!isDeployed(info)) {
                    return false;
                }
                    
                if (logger.isDebugEnabled()) {
                    logger.debug("Application, \"" + info.getApplicationId()  + "\" is installed.");
                }
            }
            return true;
        }

        
        /**
         * Returns a value of "true" if the application described by the 
         * given AppInfo object is deployed on this host or returns
         * a value of "false" otherwise.
         */
        private boolean isDeployed(AppInfo info) {
            return host.findChild(info.getApplicationId()) != null;                                
        }
        
        
        /**
         * Called by the StandbyReplicator thread to notify the peer that sent
         * us the given ReplicationContext that it contained objects belonging 
         * to applications that are NOT deployed on this host.
         */
        public void notifyPeer(ReplicationContext context) {             
            Collection appInfo = context.getAppInfo();
            
            if (appInfo == null) {
                return;
            }
            
            Iterator iterator = appInfo.iterator();
            
            while (iterator.hasNext()) {
                AppInfo info = (AppInfo)iterator.next();
                
                if (!isDeployed(info)) {
                    try {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Notifying ACTIVE peer to stop sending us ReplicationContexts for application, \"" + info.getApplicationId() + "\"");
                        }
                        
//                        AppSyncMessage message = new AppSyncMessage(AppSyncMessage.MESSAGE_OUT);
//                        message.setApplicationId(info.getApplicationId());
//                        message.setSenderId(this.subsysId);
//                        message.setMsgType(AppSyncMessage.APP_NOT_DEPLOYED);
//                        this.versionMgr.sendAppSyncMessage(message);
                    } catch (Exception e) {
                        logger.error("Error occured while sending AppSyncMessage", e);
                    }
                }                
            }
        }
	
        
	/**
	 * This is a one-time Runnable. After it is started, it sends all replicables 
	 * that have last-replicated time earlier than its start time.
	 * 
	 * @author Dana
	 */
	private class BulkReplicator extends MonitoredThread {
		private static final short DEFAULT_THREADPOOL_SIZE = 2;
		private static final short DEFAULT_MIN_ACTIVE_THREADS = 50;

		private String m_br_hostId;
		private boolean m_br_stopped = false;
		private String applicationId;

		private ThreadOwner m_br_threadOwner = null;
		
		private long m_br_lastTime;
		private int m_br_rate;
		private int m_br_counter;

		private ThreadPool m_br_threadPool;
		
		public BulkReplicator(String hostId, ThreadOwner thOwner) {
			super("BulkReplicator-" + hostId, AseThreadMonitor.getThreadTimeoutTime(),
												BaseContext.getTraceService());
			this.m_br_hostId = hostId;
			m_br_threadOwner = thOwner;
			m_br_rate = policyMgr.getReplicationPerSec();

			int tpSize = DEFAULT_THREADPOOL_SIZE;

			// Read the threadpool size property
			try {
				String threadNum = BaseContext.getConfigRepository().getValue(
							Constants.PROP_MT_BULK_REPLICATOR_THREAD_POOL_SIZE);
				if(threadNum != null) {
					tpSize = Integer.parseInt(threadNum.trim());
				}
			} catch(Exception exp) {
				logger.error( "Error in getting threadPool size", exp);
			}

			// Instantiate the thread pool
			try {
				m_br_threadPool = new ThreadPool(	tpSize,
													false,
													"BulkReplicator-" + hostId,
													null, // WorkHandler is optional
													thOwner,
													DEFAULT_MIN_ACTIVE_THREADS);
				this.m_br_threadPool.setThreadMonitor(threadMonitor);
			} catch (Exception ex) {
				logger.error( "Error in creating threadPool", ex);
			}
		}
		
		public String getHostId() {
			return m_br_hostId;
		}

		public void start() {
			m_br_threadPool.start();
			super.start();
		}

		public void run() {
			if(logger.isEnabledFor(Level.INFO)) {
				logger.info("Start replication to host " + m_br_hostId);
			}
			
			// Ashish Register thread with thread monitor
			try {
				this.updateTimeStamp();
				this.setThreadState(MonitoredThreadState.Running);

				threadMonitor.registerThread(this);
			} catch(ThreadAlreadyRegisteredException exp) {
				logger.error("This thread is already registered with Thread Monitor", exp);
			}

			try {
				Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

				this.m_br_lastTime = System.currentTimeMillis();
			
				m_br_counter = m_br_rate;
				Iterator iterator = replStoreManager.findReplicationContextIdsByCluster(clusterId);
				if (iterator == null) {
					logger.error("No replicationContexts found for cluster " + clusterId);
					if(logger.isEnabledFor(Level.INFO)) {
						logger.info("Stopping without doing any replication");
					}
					return;			
				}
				
				logger.error("Starting bulk replication for cluster " + clusterId);
				
				String ctxtId =null;
				for (; !this.m_br_stopped && iterator.hasNext();) {
					try {// Update time in thread monitor
						this.updateTimeStamp();

						ctxtId= (String) iterator.next();

						ReplicationContextImpl ctxt = (ReplicationContextImpl) replStoreManager
							.findReplicationContextByClusterAndId(clusterId, ctxtId);
						if (ctxt == null) {
							if (logger.isDebugEnabled())
								logger
									.debug("Replication context removed by some other thread... ignoring");
							continue;
						}

						if (applicationId != null && !ctxt.hasApplication(applicationId)) {
							if (logger.isDebugEnabled()) {
								logger
									.debug("Ignoring -> Replication context does not belong to application :"
													+ applicationId);
							}
							continue;
						}

						// create and submit item in threadpool
						BulkReplicationItem bri = new BulkReplicationItem(this, ctxt);
						m_br_threadPool.submit(ctxtId.hashCode(), bri);
					} catch (Throwable thr) {
						logger.error("Error in bulk replictaing contextid::"+ctxtId, thr);
					}
				}
				
				//setting self as idle as thread is no longer processing and waiting for child thread to complete
				this.setThreadState(MonitoredThreadState.Idle);
				
				// Wait for all the threads to be processed
				while(!m_br_stopped && !m_br_threadPool.isEmpty()) {
					try {
						//updating ts as saftey mechanism
						this.updateTimeStamp();
						Thread.sleep(10000);
					} catch(Throwable thr) {
						logger.error("Sleeping", thr);
					}
				}
				// Destroy threadpool, if not already stopped
				if(!m_br_stopped) {
					logger.error("Destroying bulk replicator pool...");
					shutdown();
				}
			
				logger.error("Finished bulk replication for cluster " + clusterId);
			} catch(Throwable thr) {
				logger.error("Doing bulk replication for cluster " + clusterId, thr);
			}finally {
				// Unregister thread with thread monitor
				try {
					threadMonitor.unregisterThread(this);
				} catch(ThreadNotRegisteredException exp) {
					logger.error("This thread is not registered with Thread Monitor", exp);
				}

				if(logger.isInfoEnabled()) {
					if (this.m_br_stopped) {
						if (logger.isInfoEnabled())
							logger.info("Replication to host " + m_br_hostId + " is stopped");
					} else {
						if (logger.isInfoEnabled())
							logger.info("Replication to host " + m_br_hostId + " is completed");
					}
				}
			}
		}// run()

		private synchronized void attenuate(int msgCount) {
			m_br_counter -= msgCount;

			if (m_br_counter <= 0) {
				// calculate sleep interval
				long timeNow = System.currentTimeMillis();
				long interval = 1000 - (timeNow - m_br_lastTime);
				if(logger.isInfoEnabled()) {
					logger.info("Calculated sleep time (msecs) :::" + interval);
				}
					
				if (interval > 0) {
					try {
						Thread.sleep(interval);
					} catch (InterruptedException ex) {
						logger.error("Sleeping", ex);
					}
				}
					
				// reset the counter and the lastTime.
				m_br_counter = m_br_rate;
				m_br_lastTime = timeNow;
			}
		}

		public void shutdown() {
			this.m_br_stopped = true;
			this.m_br_threadPool.shutdown();
		}

		public ThreadOwner getThreadOwner() {
			return m_br_threadOwner;
		}

	}// BulkReplicator ends

	private class BulkReplicationItem implements Work {
		private BulkReplicator m_bri_bulkRep;
		private ReplicationContextImpl m_bri_ctxt;

		public BulkReplicationItem(BulkReplicator bulkRep, ReplicationContextImpl ctxt) {
			m_bri_bulkRep = bulkRep;
			m_bri_ctxt = ctxt;
		}

		public int getTimeout() {
			return 100;
		}

		public WorkListener getWorkListener() {
			return null;
		}

		public void execute() { 
			try {
				boolean allAppSyncReceived=true;
				
				if( AseIc.class.isInstance(m_bri_ctxt) ){
					if(logger.isDebugEnabled()){
						logger.debug("Acquiring AseIc lock before bulk replication");
					}
					AseIc ic=((AseIc)m_bri_ctxt);
					ic.acquire();
					if(m_bri_bulkRep.applicationId!=null && ! m_bri_bulkRep.applicationId.trim().isEmpty()){
						if(logger.isDebugEnabled()){
							logger.debug("Checking All App Sync for App deploy received");
						}
						ic.setAppSyncForDeployReceived(m_bri_bulkRep.applicationId, true);
						allAppSyncReceived=ic.isAllAppSyncForDeployReceived();
						if(allAppSyncReceived==true)
							ic.resetAllAppSyncForDeployReceived();// @abaxi Reset for other bulk replication if secondary went down again
					}
				}


				if(m_bri_ctxt.isCleanedUp()) {
					// Cleaned up by a call processing thread, no need to replicate
					if(logger.isDebugEnabled())
						logger.debug("Replication context cleaned up by some other thread... ignoring");
					return;
				}

				ReplicationInfo info = (ReplicationInfo)m_bri_ctxt.getReplicationInfo();
				if(!info.isReplicated() && allAppSyncReceived) {
					if(logger.isInfoEnabled()) {
						logger.info("Going to replicate :"+ m_bri_ctxt);
					}

					ReplicationEvent event = new ReplicationEvent(this, ReplicationEvent.BULK_REPLICATION);
					event.setAttribute(m_bri_bulkRep.getHostId());
					replicate(m_bri_ctxt, event);
					m_bri_bulkRep.attenuate(1);
				} 
				else if(!allAppSyncReceived){
					if(logger.isInfoEnabled()) {
						logger.info("All App Sync for App deploy not received for replication context. So not replicating...");
					}
				}
				else {
					if(logger.isInfoEnabled()) {
						logger.info("Replication Context already replicated by some other event. So not replicating...");
					}
				}
			} catch(Throwable thr) {
				logger.error("In execute()", thr);
			} finally {
				try {
					if( AseIc.class.isInstance(m_bri_ctxt) ){
						if(logger.isDebugEnabled()){
							logger.debug("Releasing AseIc lock after bulk replication");
						}
						((AseIc)m_bri_ctxt).release();
					}

				} catch(Exception exp) {
					logger.error("Releasing IC lock", exp);
				}
			}
		}// execute()
	}// BulkReplicationItem ends

	private class Destination {
		private String subsystemId;
		private AseSubsystem subsystem;
		private boolean connected;
		private long startTime;
		
		public Destination(String subsystemId) {
			this.subsystemId = subsystemId;
		}
		
		public String getSubsystemId() {
			return subsystemId;
		}
		
		public AseSubsystem getSubsystem() {
			return subsystem;
		}
		
		public boolean isConnected() {
			return connected;
		}
		
		public void setConnect(boolean connected) {
			this.connected = connected;
			if (connected) {
			//	subsystem = dataChannel.getSubsystem(subsystemId);
			} else {
				subsystem = null;
			}
		}
		
		public String toString() {
			StringBuffer buf = new StringBuffer(subsystemId + ": ");
			if (connected) {
				buf.append("connected.");
			} else {
				buf.append("not connected");
			}
            return buf.toString();
        }

        public long getStartTime() {
            return startTime;
        }

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}
	} // class Destination ends
	
	private class ResetCtxt implements Runnable{
		
		String hostId; 
		String applicationId; 
		int action;
		
		public ResetCtxt(String hostId, String applicationId, int action) {
			this.hostId= hostId;
			this.applicationId= applicationId;
			this.action=action;
		}
		
		@Override
		public void run() {
			logger.error("Starting Reset CTxt for application::"
					+ applicationId + " ACtion::" + action);

			Iterator iterator = replStoreManager
					.findReplicationContextIdsByCluster(hostId);
			if (iterator == null) {
				logger.error("No replicationContexts found for cluster "
						+ hostId);
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("Stopping without doing any replication");
				}
				return;
			}

			String ctxtId = null;
			while (iterator.hasNext()) {
				try {
					ctxtId = (String) iterator.next();
					ReplicationContextImpl ctxt = (ReplicationContextImpl) replStoreManager
							.findReplicationContextByClusterAndId(clusterId,
									ctxtId);
					if (ctxt == null) {
						if (logger.isDebugEnabled())
							logger.debug("Replication context removed by some other thread... ignoring");
						continue;
					}

					if (applicationId != null
							&& !ctxt.hasApplication(applicationId)) {
						if (logger.isDebugEnabled()) {
							logger.debug("Ignoring -> Replication context does not belong to application :"
									+ applicationId);
						}
						continue;
					}
					
					switch(action){
					case RESET:{
						resetSeqForctxt(ctxt);
						break;
					}
					case REMOVE: {
						removeCtxt(ctxt);
						break;
					}
						
					}
					
					
					
				} catch (Throwable t) {
					logger.error("Error Reseting::"+ctxtId,t);
				}// end throwable
			}
			logger.error("Leaving Reset CTxt for application::" + applicationId
					+ " ACtion::" + action);

		}//end run
		
		private void resetSeqForctxt(ReplicationContextImpl ctxt){
			if (ctxt != null) {
				ReplicationInfo info = (ReplicationInfo) ctxt
						.getReplicationInfo();
				if (info != null) {
					if(info.getSequenceNo()>0){
						AseMeasurementUtil.counterBeingReplicated.decrement();
					}
					info.setSequenceNo((short) 0);
				}
			}
		}
		
		private void removeCtxt(ReplicationContextImpl ctxt){
			if(ctxt!=null){
				replStoreManager.removeReplicationContext(ctxt);
				ctxt.cleanup();
				//Update the measurement counters on the standby side
				AseMeasurementUtil.counterBeingReplicated.decrement();
			}
		}
		
	}//end Class ResetCtx
	
	
	
}

