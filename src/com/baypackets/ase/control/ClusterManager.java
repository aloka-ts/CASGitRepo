/*
 * Created on Oct 26, 2004
 *
 */
package com.baypackets.ase.control;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import RSIEmsTypes.ConfigurationDetail;

import com.baypackets.ase.util.EvaluationVersion;
import com.agnity.redis.client.RedisWrapper;
import com.agnity.redis.exeption.RedisLettuceCommandTimeoutException;
import com.agnity.redis.exeption.RedisLettuceConnectionException;
import com.baypackets.ase.channel.AseSubsystem;
import com.baypackets.ase.common.AgentDelegate;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.loadbalancer.LoadBalancerException;
import com.baypackets.ase.loadbalancer.LoadBalancerFactory;
import com.baypackets.ase.loadbalancer.LoadBalancerInterface;
import com.baypackets.ase.sipconnector.AseSipConnector;
import com.baypackets.ase.spi.replication.ReplicationManager;
import com.baypackets.ase.startup.AseMain;
import com.baypackets.ase.startup.AsePubSubPropsListener;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.ase.util.AsePing;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseTcpClient;
import com.baypackets.ase.util.AseThreadMonitor;
import com.baypackets.ase.util.AseTraceService;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.common.RedisAlarmHandler;
import com.baypackets.ase.util.StackDumpLogger;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.ase.util.threadmonitor.AseMonitoredThread;
import com.baypackets.ase.util.threadmonitor.AseStandByStatusThreadMonitor;
import com.baypackets.ase.util.threadmonitor.AseThreadOwner;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.baytalk.imUtils;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.ThreadAlreadyRegisteredException;
import com.baypackets.bayprocessor.slee.common.ThreadNotRegisteredException;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToModifyConfigurationParamsException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
//import com.baypackets.bayprocessor.slee.sleeft.channel.ChannelProviderFactory;
//import com.baypackets.bayprocessor.slee.sleeft.channel.ControlChannelProvider;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThreadState;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;
import com.baypackets.emsliteagent.EmsLiteAgent;
import com.baypackets.emsliteagent.EmsLiteConfigurationDetail;


/**
 * @author Ravi
 */
public class ClusterManager implements PeerStateChangeListener, MComponent, PeerStateChangeSource, ThreadOwner, RoleChangeListener , AseThreadOwner{// ControlMessageListener

	private static final Logger logger = Logger.getLogger(ClusterManager.class);

	public static final String PROTOCOL_TCP = "TCP";
	public static final String PROTOCOL_UDP = "UDP";

	public static final int DEFAULT_MUTEX_PORT = 9090;
	public static final int DEFAULT_SM_PORT = 12000;
	public static final int DEFAULT_SM_RETRIES = 5;
	public static final int DEFAULT_SM_TIMEOUT = 3000;
	public static final boolean DEFAULT_FLOATING_IP_REQD = true;
	public static final int DEFAULT_PING_PORT = AsePing.DEFAULT_PORT;
	public static final String DEFAULT_PING_PROTOCOL = PROTOCOL_TCP; 

	public static final short SET_FIP = 1;
	public static final short TAKEOVER_FIP = 2;
	public static final short UNSET_FIP = 3;

	private static final int SYS_INFO_REPLY=2;
	private static final int SYS_INFO_QUERY=1;
	private ThreadMonitor _threadMonitor = null;
	// ReceptionStatus==0 means we have not sent the SYS_INFO message to it
	// ReceptionStatus==1 means we have not yet received the reply for the SYS_INFO message we sent  
	// ReceptionStatus==2 means we have received the reply and processed it "fully"
	// ReceptionStatus==3 means that this system has gone down while we were waiting for a reply from it

	private static final String SYS_INFO_NOT_SENT="0";
	private static final String SYS_INFO_REPLY__NOT_RECEIVED="1";
	private static final String SYS_INFO_REPLY_RECEIVED="2";
	private static final String SYS_INFO_SYSTEM_DOWN="3";
	private static final String NULL_FIP="0.0.0.0";

	private ControlManager controlMgr;
	//private ControlChannelProvider ccp;
	private AgentDelegate agent;
	
	private EmsLiteAgent emslAgent = null;
	private AseAlarmService alarmService;
	private ConfigRepository configRep;

	private int smPort;
	private int smRetries;
	private int smTimeout;
	private boolean floatingIpTakeoverReqd;
	private int refPingPort;
	private String refIp;
	private String sipLbFIP;
	private String standbyStatusCheckerTimeOut;
	private String standbyStatusCheckerRetries;
	private String refPingProtocol;

	private AseSubsystem self;
	private AsePartitionTable partitionTable;
	private AseRoleResolver roleResolver;
	private LoadBalancerInterface loadBalancer;
	private NetworkMutexImpl networkMutex;
	private int networkMutexPort;

	private ArrayList referenceIps = new ArrayList();

	//Listeners for RoleChange Notification
//	private RoleChangeListener[] listeners =
//		new RoleChangeListener[Constants.RCL_NUM_OF_LISTENERS];
	//used tree map as invocation needs to be done on priorty
	private Map<Short,List<RoleChangeListener>> listeners =
					new TreeMap<Short,List<RoleChangeListener>>();

	private boolean started = false;
	private boolean stopped = false;

	//private Object sysInfoWaitObj = new Object();
//	private DataChannelThread dcThread = null;

	private ArrayList history;

	private boolean receivedSysInfoReplies = false;

	private ReplicationManager replicationManager = null;

	private boolean bGenerateActiveRoleChange = true;
	private boolean bSwitchToActiveRole = false;
	private boolean bSwitchToStandbyRole = false;

	private String ipAddrFromConf=null; //JSR289.34

	private String peerIpAddress = null;
	
	private static int peerDownFlag; 
	AseStandByStatusThreadMonitor byStatusThreadMonitor = null;
	private static int maxRetries;
	private static int retries; 
	private static int standbyStatusCheckerTimeoutValue;
	private static final String THREAD_RUNNING = "running";
	private static final String THREAD_IDLE = "idle";
	private static int deltaFaileOverDetectionTime=0;
	private static long deltaFaileOverDetectionTimeStamp=0;
	RedisWrapper redisWrapper=null;

	private String peerSubsystemName;
	
	public static String CAS_INSTANCE_HEARTBEAT="CAS_INSTANCE_HEARTBEAT";
	public static  String ACTIVE_CAS_INSTANCES="ACTIVE_CAS_INSTANCES";

	
	public void setReplicationManager( ReplicationManager rManager)
	{
		replicationManager = rManager;
	}

	public void removeReplicationDestination( String hostId )
	{
		// @Siddharth
		// This is for the case when after conflixt resolution a subsystem 
		// releases its standby role to another with higher priority
		if ( logger.isDebugEnabled() ) { 
			logger.debug("Removing Replication destination : " + hostId );
		}
		replicationManager.removeReplicationDestination(hostId);
	}


	/*
	 * Implementation of the ControlMessageListener
	 */
//	public void handleControlMessage(PeerMessage msg) {
//		if(this.stopped) {
//			if (logger.isInfoEnabled()) {
//
//
//			logger.info("Subsystrem stopped. Returning.");
//			}
//			return;
//		}
//
//		if(logger.isInfoEnabled()){
//			logger.info("handleControlMessage called :");
//		}
//
//		try{
//			switch(msg.getType()){
//			case MessageTypes.SYS_INFO_MESSAGE:
//				this.handleSystemInfoMessage((SystemInfoMessage)msg);
//				break;
//			case MessageTypes.ROLE_SYNC_MESSAGE:
//				this.handleRoleSyncMessage((RoleSyncMessage)msg);
//				break;
//			default:
//				if(logger.isInfoEnabled()){
//					logger.info("Undefined message received :" + msg);
//				}
//			}
//		}catch(Exception e){
//			logger.error("hanlding control message", e);
//		}
//	}
//
//	protected void handleSystemInfoMessage(SystemInfoMessage msg) throws Exception{
//		if(this.stopped) {
//			if (logger.isInfoEnabled()) {
//
//
//			logger.info("Subsystrem stopped. Returning.");
//			}
//			return;
//		}
//
//		if(logger.isInfoEnabled()){
//			logger.info("handleSystemInfoMessage called." + msg+" Sequence No="+msg.getSequenceNo());
//		}
//		//AseSubsystem self = this.controlMgr.getSelfInfo();
//		AseSubsystem subsys = msg.getSubsystem();
//		String subsysId = subsys.getId() == null ? "" : subsys.getId();
//
//		//If the message received from self, simply return from here
//		if(self.getId().equals(subsys.getId())){
//			return;
//		}
//
//		//Update the peer subsystem information	
//		if(logger.isInfoEnabled()){
//			logger.info("Sending the Peer Ready event to all the listeners, ems_subsys_id of the sender = "+subsys.getEmsSubsystemId());
//		}
//		//generate a peer state change event to all the listed listeners.
//		PeerStateChangeEvent event = new PeerStateChangeEvent(subsys, PeerStateChangeEvent.PR_READY, "", this);
//		this.controlMgr.generatePeerChangeEvent(event);
//
//
//		//Call the notifyLink UP for this PEER if running with the EMS.
//		if(!subsysId.equals("")){
//			if(logger.isInfoEnabled()){
//				logger.info("Calling the notifyLink UP for Peer ::" + subsys.getId());
//			}
//			this.agent.notifyLink(subsys.getEmsSubsystemId(), RSIEmsTypes.ConnectionState.ConnectionState_UP);
//		}
//
//		//Send an alarm to notify the peer connection restored.
//		if(!subsysId.equals("")){
//			String alarmMsg = " [Connection restored with peer ::" + subsys.getId() + "]";
//			logger.error("CAS-ALARM: Peer Connection Restored; peer [" + subsys.getId() + "]");
//			try{
//				this.alarmService.sendAlarm(Constants.ALARM_PEER_CONNECTION_RESTORED,subsys.getEmsSubsystemId(), alarmMsg );	
//			}catch (Exception e) {
//				logger.error("Exception while Raising alarm ",e);
//			}
//			
//		}
//
//
//		//If the message sequenceNo != 1 return without doing anything
//		switch(msg.getSequenceNo()){
//		case SYS_INFO_REPLY:
//			if(logger.isInfoEnabled()){
//				logger.info("Update partition information for " + msg.getSubsystem().getId());
//			}
//
//			//update partition table
//			ArrayList peerPartitionInfos = AsePartitionTable
//			.readPartitionInfos(msg.getObjectInput(), msg.getNumPartitionInfos());
//			synchronized( this.partitionTable.synchronizationObj ) {
//				ArrayList conflictPartitionInfos = partitionTable.update(peerPartitionInfos);
//				if( null != conflictPartitionInfos ) { 
//					if ( conflictPartitionInfos.size() > 0 ) {
//						logger.error( "Conflict detected in SysInfo message. Initiating shutdown " ) ;
//						this.initiateShutdown();
//						return;
//					}
//				}
//			}
//			//Object syncObj = this.ccp.getSynchronizationObj(subsys.getHost() );
//			Object syncObj = this.ccp.getSynchronizationObj(getAddress(subsys) );
//			if( syncObj == null )
//			{
//				// Only God knows when this will happen
//				// It means that this subsystem does not have any entry in jgroups and still it is sending this message
//			}
//			synchronized(syncObj)
//			{
//				if(logger.isInfoEnabled()){ 
//					logger.info(" ReceptionStatus set to SYS_INFO_REPLY_RECEIVED for subsys : "+getAddress(subsys));
//				}
//				if(subsys.getSignalIp() == null )	{
//					this.ccp.setReceptionStatus( subsys.getHost() , SYS_INFO_REPLY_RECEIVED );
//				}else	{
//					this.ccp.setReceptionStatus( subsys.getSignalIp() , SYS_INFO_REPLY_RECEIVED );
//				}
//
//				syncObj.notify();
//			}
//			if(logger.isInfoEnabled()){
//				logger.info(this.partitionTable);
//			}
//
//
//			if(logger.isInfoEnabled()){
//				logger.info("Creating and sending PeerState Changed Event to all listeners ::" + msg.getSubsystem().getId());
//			}
//
//			//OUT of this switch/case block.
//			break;
//		case SYS_INFO_QUERY:
//			//Create a Reply message to send the SELF information
//			if(logger.isInfoEnabled()){
//				logger.info("Creating and sending reply to the sender ::" + msg.getSubsystem().getId());
//			}	
//
//			//Create the reply message...
//			MessageFactory factory = ccp.getMessageFactory();
//			SystemInfoMessage reply = (SystemInfoMessage)
//			factory.createMessage(MessageTypes.SYS_INFO_MESSAGE, PeerMessage.MESSAGE_OUT);
//
//			//Set the message information
//			reply.setSequenceNo((short)SYS_INFO_REPLY);
//			reply.setSubsystem(self);
//
//			//write partition infos
//			ArrayList partitionInfos = null;
//			boolean tryAgain = false;
//			do {
//				tryAgain = false;
//				synchronized( this.partitionTable.synchronizationObj )
//				{
//					partitionInfos = this.partitionTable.getPartitionInfos(self.getId());
//				}
//				for (int i = 0; i < partitionInfos.size(); i++) {
//					PartitionInfo info = (PartitionInfo)partitionInfos.get(i);
//					if (info.getFip().equals(NULL_FIP)) {
//						//if floating IP is not set yet, sleep 3 seconds and try again
//						if (logger.isInfoEnabled()) {
//							logger.info("Try to get partition info again");
//						}
//						tryAgain = true;
//						Thread.sleep(3000);
//						break;
//					}
//				}
//			} while (tryAgain);
//
//			reply.setNumPartitionInfos(partitionInfos.size());
//			AsePartitionTable.writePartitionInfos(reply.getObjectOutput(), partitionInfos);
//
//			//Reply to the sender...
//			ccp.send(msg.getSubsystem(), reply);
//
//			if(logger.isInfoEnabled()){
//				logger.info("Sent the reply System Info Message :" + reply);
//			}
//
//			//OUT of the switch/case now.....
//			break;
//		default:
//			//Unknown sequence number for message.
//			logger.error("Unknown sequence number for message.");
//		}
//	}

//	protected void handleRoleSyncMessage(RoleSyncMessage msg) throws Exception{
//		if(logger.isInfoEnabled()){
//			logger.info("handleRoleSyncMessage called." + msg.getSubsysId());
//		}
//
//		if (msg.getSubsysId().equals(this.self.getId())) {
//			//do nothing when receive self info
//			return;
//		}
//
//		//ignore RoleSync message if received before receiving SysInfo replies.
//		if (!receivedSysInfoReplies) {
//			if (logger.isDebugEnabled()) {
//
//
//			logger.debug( "RoleSync Received before replies for SysInfo message");
//			logger.debug(" Returning from handleRoleSyncMesg without doing role resolution");
//			}
//			return;
//		}	
//
//
//		//generate partition info
//		PartitionInfo peerInfo = new PartitionInfo( 
//				msg.getFloatingIp(),msg.getSubsysId(), msg.getRole());
//		if(logger.isInfoEnabled()){
//			logger.info("Get peer partition info: " + peerInfo);
//		}
//
//		if (msg.getErrorCode() == RoleSyncMessage.NO_ERROR) {
//			synchronized( this.partitionTable.synchronizationObj )
//			{
//				this.partitionTable.roleSyncUpdate(peerInfo);
//			}
//
//			logger.error("Partition table is updated: " +  this.partitionTable);
//
//			//If the PEER has taken a standby role, send out a Replicate Event.
//			AsePartition partition;
//			synchronized( this.partitionTable.synchronizationObj )
//			{
//				partition = this.partitionTable.getPartition(msg.getFloatingIp());
//			}
//			//logger.info(" msg.getRole()="+msg.getRole()+"  AseRoles.STANDBY="+AseRoles.STANDBY+" partition.inSamePartition(self.getId(), msg.getSubsysId())="+partition.inSamePartition(self.getId(), msg.getSubsysId())+" self.getId()="+self.getId()+" msg.getSubsysId()="+msg.getSubsysId() );
//			if((msg.getRole() == AseRoles.STANDBY) && 
//					partition.inSamePartition(self.getId(), msg.getSubsysId())) {
//				
//				String alarmMsg = " [ Standby system is now available ]";
//				if (logger.isInfoEnabled()) {
//
//
//				logger.info("Standby system is now available");
//				}
//				try{
//					this.alarmService.sendAlarm(Constants.ALARM_NO_STANDBY_FOUND_CLEARED, alarmMsg );	
//				}catch (Exception e) {
//					logger.error("Exception while Raising alarm ",e);
//				}
//				if (logger.isInfoEnabled()) {
//
//
//				logger.info(" generating replication event ");
//				}
//				this.generatePeerReadyReplicateEvent(partition, msg.getSubsysId(), false);
//			}				
//
//		} else if (msg.getErrorCode() == RoleSyncMessage.CONFLICT_ROLE) {
//			logger.error("FATAL ERROR : Conflict in Role Sync Message");
//			initiateShutdown();
//			return;
//		} else {
//			if(logger.isDebugEnabled()){
//				logger.debug("Unknow error code: " + peerInfo);
//			}			
//		}
//	}

	/* 
	 * Implementation of the PeerStateChangeEvent.
	 */
	public int handleEvent(PeerStateChangeEvent psce) {
		if(this.stopped) {
			if (logger.isInfoEnabled()) {


			logger.info("Subsystrem stopped. Returning.");
			}
			return 0;
		}

		//Get the subsystem object OUT of the EVENT.
		int eventId = psce.getEventId();

		if(logger.isInfoEnabled()){
			logger.info("PeerStateChangeEvent received for :" + psce + " :: " +eventId);
		}

		try{
			switch(eventId){
//			case PeerStateChangeEvent.PR_READY:
//				//this.ccp.setSubsystem(psce.getSubsystem());
//				break;
			case PeerStateChangeEvent.PR_UP_OPEN_CONN:
				this.handlePeerUpEvent(psce);
				break;
			case PeerStateChangeEvent.PR_DOWN_CLOSE_CONN:
				this.handlePeerDownEvent(psce);
				break;
			default:
				//No handling for the messages other than the above			
			}
		}catch(Exception e){
			logger.error("handling peer-state-change event", e);
		}
		return 0;
	}

	protected void handlePeerUpEvent(PeerStateChangeEvent psce) throws Exception{
		AseSubsystem subsys = psce.getSubsystem();
		int eventId = psce.getEventId();
		addHistory(new Date(System.currentTimeMillis()) + ": " + getAddress(subsys) + "is up\n");
		if(logger.isInfoEnabled()){
			logger.info("Received a PEER UP event for :" + getAddress(subsys) + ":::" + eventId);
		}
	}

	protected void handlePeerDownEvent(PeerStateChangeEvent psce) throws Exception{
		AseSubsystem subsys = psce.getSubsystem();
		String subsysId = (subsys.getId() == null) ? "" : subsys.getId();
		int eventId = psce.getEventId();
		addHistory(new Date(System.currentTimeMillis()) + ": " + getAddress(subsys) + "is down\n");

		if(logger.isInfoEnabled()){
			logger.info("Received a PEER DOWN event for :" + getAddress(subsys) + ":::" + eventId);
		}

		//Call the notifyLink DOWN for this PEER if running with the EMS.
//		if(!subsysId.equals("")){
//			if(logger.isInfoEnabled()){
//				logger.info("Calling the notifyLink DOWN for Peer ::" + subsys.getId());
//			}
//			this.agent.notifyLink(subsys.getEmsSubsystemId(), RSIEmsTypes.ConnectionState.ConnectionState_DOWN);
//			if (logger.isInfoEnabled()) {
//
//
//			logger.info("Successfully notifyLink DOWN to EMS Agent ");
//		}
//		}

		ArrayList partitions;
		String nodeDown = null;
		synchronized( this.partitionTable.synchronizationObj )
		{
			partitions = this.partitionTable.getPartitions(subsysId,AseRoles.ACTIVE);
		}
		if(partitions != null){
			nodeDown = "Active";
		}else{
			nodeDown = "StandBy";
		}
		
		//Send an alarm to notify the peer connection down.
		if(!subsysId.equals("")){
			String alarmMsg = " [Connection failed with peer ::" + subsys.getId() + "] :  "+nodeDown+" CAS down";
			logger.error("CAS-ALARM: Peer Connection Failed; peer [" + subsys.getId() + "]");
			try{
				this.alarmService.sendAlarm(Constants.ALARM_PEER_CONNECTION_FAILED,subsys.getEmsSubsystemId(), alarmMsg );
			}catch (Exception e) {
				logger.error("Exception while Raising alarm ",e);
			}
		}
		//Check if Reference IP address is pingable, if not then it is network isolation
		// so initiate a normal shutdown
		if(AsePing.ping(refIp) != true)	{
			if(logger.isInfoEnabled()){
				logger.info("Network Isolation Initiating shutdown");
			}

			initiateShutdown();
		} 	
		/**
		 *  Additions start: Rajendra
		 * Invoke releaseFIP(int, String) to relese FIP of ACTIVE subsystem
		 * which went down.
		 */

		
		if(partitions != null)	{
			if(logger.isInfoEnabled()){
				logger.info("An ACTIVE CAS went down so invoke releaseFIP() and UNSET FIP");
			}


			String fip = null;
			//Ashish Y this for Loop 
			for(int i =0; i<partitions.size(); i++)	{
				fip = ((AsePartition)partitions.get(i)).getFloatingIp();
			}
			try {
				this.loadBalancer.releaseFIP(subsys.getEmsSubsystemId(),fip);
			}
			catch( Exception ex)
			{
				logger.error(" Exception while releasing fip : "+fip+" on the subsysId : "+subsys.getEmsSubsystemId()+", Moving ahead.... ");
			}
			
			try {
				//* UNSET FIP on subsystem which went down
				setFloatingIP(fip, subsys, UNSET_FIP);
			}
			catch( Exception ex)
			{
				logger.error(" Exception while unsetting fip : "+fip+" on the subsysId : "+subsys.getHost()+", Moving ahead.... ");
			}
			
		}

                /*
		 * Else block is removed for JCOM Bug 22509 .Alarm 12129 will be raised in both the conditions i.e. anyof the subystem killed active/standby
		 */  
			String alarmMsg = " [No CAS Subsystem available on StandBy Role ]";
			logger.error("No CAS Subsystem available on StandBy Role");
			try{
				this.alarmService.sendAlarm(Constants.ALARM_NO_STANDBY_FOUND, alarmMsg );
			}catch (Exception e) {
				logger.error("Exception while Raising alarm ",e);
			}
			
	

		//Additions End: Rajendra

		// This operation doesn't require synchronization
		this.partitionTable.removeMember(subsysId);

		//notify any waiting thread for the sysInfo message
//		Object obj = this.ccp.getSynchronizationObj(getAddress(subsys));
//		synchronized( obj )
//		{
//			// Set receptionStatus of this AseSubsystem to "3" which means it has gone down
//			// so that we would not wait for it in waitForPeerStatus
//			//this.ccp.setReceptionStatus( subsys.getHost() , "3" );
//
//			if(subsys.getSignalIp() == null )	{
//				this.ccp.setReceptionStatus( subsys.getHost() , SYS_INFO_SYSTEM_DOWN );
//			}else	{
//				this.ccp.setReceptionStatus( subsys.getSignalIp() , SYS_INFO_SYSTEM_DOWN );
//			}
//			obj.notify();
//		}

		if (started) {
			//checking self role. If in any of partition it has standby role it means 
			//that self role is standby 
			partitions = null;
			synchronized( this.partitionTable.synchronizationObj ) {
				partitions = this.partitionTable.getPartitions(this.self.getId() 
						, AseRoles.STANDBY);
			}

			//member will try to acquire the lock only if it is standby member.
			if ( partitions != null ) { 
				//if self role is standby then try to acquire lock
			//	networkMutex.acquireLock();
				//member will again check whether it is still standby member 
				//To avoid race condition in case of two active CAS goes down at a same time
				ArrayList partitions1 = null;
				synchronized( this.partitionTable.synchronizationObj ) { 
					partitions1 = this.partitionTable.getPartitions(this.self.getId() 
							, AseRoles.STANDBY);
				}	

				if ( partitions1 == null ) { 
					if (logger.isDebugEnabled()) {


					logger.debug("After acquiring lock it has released standby role. ");
					logger.debug("So it is no more standby member , Releasing Lock " ) ;
					}
					//it means that it got more than one peer down event. Corresponding to 
					//first peer down it resolved its role as active. But now its no more 
					//standby so simply update the partition table.
					networkMutex.releaseLock();
					partitionTable.update(null);
					return;
				}	
				resolveRole(subsys);
			}else {
				logger.error("Checking for Active Role");	
				partitions = null;
				synchronized( this.partitionTable.synchronizationObj ) {
					partitions = this.partitionTable.getPartitions(this.self.getId() 
							, AseRoles.ACTIVE);
				}
				if (partitions != null){
					logger.error("It is Active");
					String fip = null;
					for(int i =0; i<partitions.size(); i++)	{
						fip = ((AsePartition)partitions.get(i)).getFloatingIp();
					}
					logger.error("FIP: " + fip);
					FIPChecker checker = new FIPChecker(fip);
			        checker.setThreadOwner(this);
			        checker.start();
				}
			}
		}

		
		logger.error("Partition Table after peer down " + partitionTable);
	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.agent.MComponent#changeState(com.baypackets.bayprocessor.agent.MComponentState)
	 */
	public void changeState(MComponentState state)
	throws UnableToChangeStateException {
		if (logger.isInfoEnabled()) {


		logger.info("changeState() called");
		}
		try {
			if(state.getValue() == MComponentState.LOADED){
				this.initialize();
			} else if(state.getValue() == MComponentState.RUNNING){
				this.start();
			} else if(state.getValue() == MComponentState.STOPPED){
				this.shutdown();
			}
		} catch(Exception e){
			throw new UnableToChangeStateException(e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.baypackets.bayprocessor.agent.MComponent#updateConfiguration(com.baypackets.bayprocessor.slee.common.Pair[], com.baypackets.bayprocessor.agent.OperationType)
	 */
	public void updateConfiguration(Pair[] arg0, OperationType arg1)
	throws UnableToUpdateConfigException {
	}


	public void registerRoleChangeListener(RoleChangeListener listener, short priority){
		//if(listener != null && !this.listeners.contains(listener)){
		//	this.listeners.add(priority, listener);
		//}
		//this.listeners[priority] = listener;
		
		if(logger.isDebugEnabled()){
			logger.debug("registerRoleChangeListener() called on priorty:"+priority+"  listener::"+listener);
		}
		List<RoleChangeListener> roleChangeListenerListForPriorty = this.listeners.get(priority);
		if(roleChangeListenerListForPriorty == null ){
			if(logger.isInfoEnabled()){
				logger.info("registerRoleChangeListener() list was null on priorty:"+priority);
			}
			roleChangeListenerListForPriorty = new ArrayList<RoleChangeListener>();
		}
		if(!roleChangeListenerListForPriorty.contains(listener)){
			if(logger.isInfoEnabled()){
				logger.info("registerRoleChangeListener() adding listener on priorty:"+priority);
			}
			roleChangeListenerListForPriorty.add(listener);
		}else{
			if(logger.isInfoEnabled()){
				logger.info("registerRoleChangeListener() listener already present on priorty:"+priority);
			}
		}
		this.listeners.put(priority, roleChangeListenerListForPriorty);
		if(logger.isDebugEnabled()){
			logger.debug("registerRoleChangeListener() Exit");
		}
	}

	public void unregisterRoleChangeListener(RoleChangeListener listener){
		// this.listeners.remove(listener);
//		for(int i = 0; i < this.listeners.length; ++i) {
//			if(this.listeners[i] == listener) {
//				this.listeners[i] = null;
//				break;
//			}
//		}
		
		
		
		if (logger.isDebugEnabled()) {
			logger.debug("unregisterRoleChangeListener() called on listener::" + listener);
		}
		Set<Entry<Short, List<RoleChangeListener>>> entrySet = listeners.entrySet();
		for (Entry<Short, List<RoleChangeListener>> entry : entrySet) {
			short priority = entry.getKey();
			List<RoleChangeListener> roleChangeListenerList = entry.getValue();
			
			if (roleChangeListenerList != null) {
				if (logger.isDebugEnabled()) {
					logger
						.debug("unregisterRoleChangeListener() roleChangeListenerList Found on priority::"
										+ priority);
				}
				boolean status = roleChangeListenerList.remove(listener);
				if (status) {
					if (logger.isDebugEnabled()) {
						logger
							.debug("unregisterRoleChangeListener() listener removed on priority::"
											+ priority);
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger
							.debug("unregisterRoleChangeListener() listener Not found on priority::"
											+ priority);
					}
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger
						.debug("unregisterRoleChangeListener() roleChangeListenerList NULL on priority::"
										+ priority);
				}
			}//@End if (roleChangeListenerList != null) 
		}//@End for Entryset

		if (logger.isDebugEnabled()) {
			logger.debug("unregisterRoleChangeListener() Exit");
		}
		
	}

	/**
	 * Get replication destination of this floating IP
	 */
	public String[] getReplicationDestinations(String fip) {

		AsePartition partition;
		synchronized( this.partitionTable.synchronizationObj )
		{
			partition = this.partitionTable.getPartition(fip);
		}
		if (partition != null) {
			return partition.getReplicationDestinations();
		}
		return null;
	}

	public void initialize() throws Exception{
		try {
			if (logger.isInfoEnabled()) {
				logger.info("initialize() called");
			}
			this.redisWrapper = (RedisWrapper) Registry.lookup(Constants.REDIS_WRAPPER);
			this.agent = (AgentDelegate)Registry.lookup(Constants.NAME_AGENT_DELEGATE);
			this.alarmService = (AseAlarmService)Registry.lookup(Constants.NAME_ALARM_SERVICE);
			this.configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			sipLbFIP = this.configRep.getValue(Constants.OID_LOADBALANCER_FIP);
			
			standbyStatusCheckerTimeOut = this.configRep.getValue(Constants.STANDBY_STATUS_CHECKER_TIMEOUT);
			standbyStatusCheckerRetries = this.configRep.getValue(Constants.STANDBY_STATUS_CHECKER_RETRIES);
			
			retries = Integer.parseInt(standbyStatusCheckerRetries);
			maxRetries=retries;
			
			standbyStatusCheckerTimeoutValue=Integer.parseInt(standbyStatusCheckerTimeOut);
			
			logger.error("initialize standbyStatusCheckerTimeOut val : " + standbyStatusCheckerTimeOut);
			logger.error("initialize standbyStatusCheckerRetries val : " + standbyStatusCheckerRetries);
			
			//Get the control manager and the control channel.
			this.controlMgr = (ControlManager) Registry.lookup(Constants.NAME_CONTROL_MGR);
			this.self = this.controlMgr.getSelfInfo();

			//initialize AsePartitionTable
			this.partitionTable = controlMgr.getPartitionTable();
			this.partitionTable.setClusterManager(this);
		
			emslAgent = BaseContext.getEmslagent();
			
			
			CAS_INSTANCE_HEARTBEAT=CAS_INSTANCE_HEARTBEAT+configRep.getValue(Constants.CAS_SITE_ID);
			ACTIVE_CAS_INSTANCES =ACTIVE_CAS_INSTANCES+configRep.getValue(Constants.CAS_SITE_ID);
			
			if(logger.isInfoEnabled()){
				logger.info("ACTIVE_CAS_INSTANCES structure name in redis as per site is "+ACTIVE_CAS_INSTANCES);
			}
			//In case of Non-FT mode, no need to initialize the channels.
			//So return from here
			if(self.getMode() == AseModes.NON_FT)
				return;

			if (EvaluationVersion.FLAG) {
				logger.error("FT/HA mode is not supported in evaluation version of CAS");
				throw new Exception("FT/HA mode is not supported!!!");
			}

			//Update the Current Role to Standby in the EMS
			if(logger.isInfoEnabled()){
				logger.info("Going to modify Current Role to STANDBY in the EMS");
			}
			if(emslAgent != null){
				EmsLiteConfigurationDetail configDetail = new EmsLiteConfigurationDetail(Constants.OID_CURRENT_ROLE, "Standby");
				this.agent.modifyCfgParam(configDetail);
			}else{
				ConfigurationDetail detail = new ConfigurationDetail(Constants.OID_CURRENT_ROLE, "Standby");
				this.agent.modifyCfgParam(detail);
			}
	
			ConfigRepository configDb = BaseContext.getConfigRepository();
			configDb.setValue(Constants.OID_CURRENT_ROLE,"Standby");

			//Get the control channel from the cluster manager
		//	this.ccp = this.controlMgr.getControlChannel();


			//Configure the cluster information.
			this.configureCluster();		                

			//Register with the control manager for the System Info Message
		//	this.controlMgr.registerMessageListener(MessageTypes.SYS_INFO_MESSAGE, this);

			//Register with the control manager for the RoleSyncMessage.
		//	this.controlMgr.registerMessageListener(MessageTypes.ROLE_SYNC_MESSAGE, this);

			//Register with the control manager for the PeerStateChange messages
			this.controlMgr.registerPeerStateChangeListener(this);

			//Create a new DataChannel thread.
			//this.dcThread = new DataChannelThread();

			this._threadMonitor = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);
	
			//intialize NetworkMutex 
			ArrayList peerList = new ArrayList();
			String peerIpString = this.configRep.getValue(Constants.OID_PEER_SUBSYS_IP);
			StringTokenizer st = new StringTokenizer(peerIpString , ",");
			while (st.hasMoreTokens()) { 
				peerList.add(AseUtils.getIPAddress(st.nextToken()));
			}
			peerList.add(self.getHost());
			
			networkMutex = new NetworkMutexImpl( networkMutexPort , 
					peerList , self.getHost());
			
			//Register with the TELNET Server for the System information.
			TelnetServer telnetServer = (TelnetServer) Registry.lookup(Constants.NAME_TELNET_SERVER);

		}catch (Exception e) {
			logger.error("Initializing ClusterManager", e);
			throw e;
		}
		logger.info("leaving initialize()");
	}

	public void start() throws Exception{
		try {
			if (logger.isInfoEnabled()) {


			logger.info("start() called");
			}
			//JSR289.34
			AseSipConnector connector = (AseSipConnector)Registry.lookup("SIP.Connector");
			this.ipAddrFromConf = connector.getIPAddress();
			
			redisWrapper =  (com.agnity.redis.client.RedisWrapper) Registry.lookup(Constants.REDIS_WRAPPER);
			
			//end
			
			if (logger.isInfoEnabled()) {


			logger.info("IP Address read from configuration : "+this.ipAddrFromConf);
			}
			if(this.self.getMode() == AseModes.NON_FT){
				String partitionId = this.ipAddrFromConf;
				partitionId = adjustFIPFormat(partitionId);
				PartitionInfo partition = new PartitionInfo(partitionId, partitionId, AseRoles.ACTIVE);
				this.generateRoleChangeEvent(partition);
				return;
			}

			//Start the DataChannel thread
		//	this.dcThread.start();

			// will try to acquire NetworkLock only if reference IP is pingable 
			//otherwise it will initiate shutdown

			if( true != AsePing.ping(refIp) ) {
				if(logger.isInfoEnabled()){
					logger.info("Network Isolation so Initiating shutdown");
				}
				initiateShutdown();
				//no further processing
				return;
			}

			if(logger.isInfoEnabled()){
				logger.info(" Calling acquireLock() method from cluster manager " ) ;
			}
			networkMutex.acquireLock();

			//Publish the system information to the all the group members.
		//	this.publishSystemInfo();

			// Mark that you have come out of waitForPeerStatus, that is,
			// you have received replies for the SystemInfo messages you had sent
			synchronized(this)
			{
				receivedSysInfoReplies = true;
			}

			// Clear stale entries from the laodbalancer IP table
			this.clearLB_IPTable(); //commenting for now for replication test

			//Resolve the role.
			this.resolveRole();
			
			boolean isIPv4 = (self.getHost().contains(AseStrings.COLON)) ? false : true;
			int subnetMask = this.agent.getSubnetMask(self.getHost());
			
			//Call the notifyLan if we are running with the EMS.
			if(isIPv4){
				int ip = imUtils.ipToInt(self.getHost());
				this.agent.notifyLan(ip, subnetMask, RSIEmsTypes.ConnectionState.ConnectionState_UP);
			}else{
				//Since IPv6 address cannot be converted into an integer , any integer value is passed
				//e.g. as an argument
				this.agent.notifyLan( 1234, subnetMask, RSIEmsTypes.ConnectionState.ConnectionState_UP);
			}
			this.started = true;
			
			//register self with role change listener
			//Add self with priorty same as host as it needs to 
			//be done before replication and other activity.
			registerRoleChangeListener(this, Constants.RCL_HOST_PRIORITY);
			
			String valPeerLookup = (String)configRep.getValue(Constants.PEER_IP_LOOKUP_THROUGH_JMX);
			logger.error("peer.ip.lookup.through.jmx....value : " + valPeerLookup);
			if (valPeerLookup != null && !valPeerLookup.trim().isEmpty()) {
				if(valPeerLookup.equalsIgnoreCase(AseStrings.TRUE_SMALL)){

					try {

						peerIpAddress = AseUtils.getIPAddress( configRep.getValue(Constants.DUAL_LAN_PEER_SIGNAL_IP));
					} catch (Exception exp) {
						if (logger.isDebugEnabled()) {
							logger.debug("DUAL_LAN_SIGNAL_IP not specified");
						}

					}
					if ((peerIpAddress == null) || (peerIpAddress.length() == 0)) {
						peerIpAddress = AseUtils.getIPAddress(configRep.getValue(Constants.OID_PEER_SUBSYS_IP));
					}

					// This is a StandbyStatusChecker thread monitor thread.
					byStatusThreadMonitor = new AseStandByStatusThreadMonitor();
					byStatusThreadMonitor.initialize();
					
					//This thread is used to check JMX connection of its peer.
					//If any connection issue occurred, this will call peer down
					
					peerSubsystemName=(String) configRep.getValue(Constants.PEER_SUBSYSTEM_NAME);
					StandbyStatusChecker checker = new StandbyStatusChecker(peerIpAddress,(String) configRep.getValue(Constants.PROP_PEER_JMX_PORT),peerSubsystemName);
					checker.setStandByThreadOwner(this);
					// this indicates that this is first thread.
					checker.setNotFirst(0);
					checker.start();
				}
			}
		} catch (Exception e) {
			logger.error("Starting ClusterManager", e);
			throw e;
		}
		if (logger.isInfoEnabled()) {


		logger.info("Leaving start()");
		}
	}


	private void clearLB_IPTable() throws LoadBalancerException , Exception
	{

		String peerIP =  this.configRep.getValue(Constants.OID_PEER_SUBSYS_IP);
		String selfIP =  this.configRep.getValue(Constants.OID_BIND_ADDRESS);
		String currentFIPValue =  this.configRep.getValue(Constants.OID_SIP_FLOATING_IP);
		
		String selfAddr=AseUtils.getIPAddress(selfIP);
		String currentFIP=  AseUtils.getIPAddress(currentFIPValue);

		/**
		 *  if current fip and self ip are nt same or current ip and peerip are not same (means on same machine) then only go for unsetting FIP on self 
		 */
		if (peerIP != null && !peerIP.equals(selfIP)
				&& !currentFIPValue.equals(selfIP)
				&& !selfAddr.equals(currentFIP)) {
			
		currentFIPValue = adjustFIPFormat(currentFIPValue);
		String value = this.configRep.getValue(Constants.OID_LOADBALANCER_FIP);
		// Here (in the following if condition) code dealing with IPv6 IP has to be put it
		// @Siddharth
		if( !currentFIPValue.equals(NULL_FIP))// )
		{
			// UNSET this FIP from self, in case the FIP is not set this step won't cause any problem
			this.changeFloatingIP(false, self.getHost(), currentFIPValue);
			// Update the OID only when LB is there
			if (  !(value == null || value.equals("") || value.equals(NULL_FIP)) ) 
			{
				if(emslAgent != null){
					EmsLiteConfigurationDetail configDetail = new EmsLiteConfigurationDetail(Constants.OID_SIP_CONNECTOR_IP_ADDRESS, NULL_FIP);
					this.agent.modifyCfgParam(configDetail);
				}else{
				ConfigurationDetail detail = new ConfigurationDetail(Constants.OID_SIP_CONNECTOR_IP_ADDRESS, NULL_FIP);
				this.agent.modifyCfgParam(detail);
			}
		  }
		}else{
			logger.error(" no need to change FIP as it is eeithe rnull ");
		}
		// getIP table from the LaodBalancer
		String fipDetails = loadBalancer.getFIPDetails();
		
		if (logger.isDebugEnabled()) {
		logger.debug(" fipDetails got = "+fipDetails);
		}
	//	if(fipDetails != null)
//		{
//			Iterator itr = fillFipDetails( fipDetails );
//
//
//			HashMap connectedMemberTimestamp = this.ccp.getConnectedMembersTimeStamp();
//			if (logger.isDebugEnabled()) {
//
//
//			logger.debug(" connectedMemberTimestamp = "+connectedMemberTimestamp);
//			}
//			while( itr.hasNext() )
//			{
//				Map.Entry e = (Map.Entry) itr.next();
//				String fip = (String) e.getKey();
//				ArrayList lb_array = (ArrayList)e.getValue();
//				int emsSubsysId, result;
//				try {
//					emsSubsysId = Integer.parseInt( (String)lb_array.get(0) );
//				}
//				catch( Exception ex)
//				{
//					// If ems_id got from LB is non-integer then just continue
//					// we can't do anything else
//					continue;
//				}
//
//				if( emsSubsysId != 0 && anyProblem( (String)lb_array.get(0) , (String)lb_array.get(1), connectedMemberTimestamp ) )
//				{
//					// Send Unset and Deactivate FIP command for this ems_subsys_id
//					// Get timestamp and ipaddress of this emsSubsysId
//					ArrayList arr = (ArrayList) connectedMemberTimestamp.get( (String)lb_array.get(0) );
//					String hostIp;
//					if ( arr != null )
//					{
//						hostIp = (String) arr.get(1); 
//					}
//					else
//						hostIp = fip;
//					logger.error(" Unsetting and Releasing fip for emsSubsysId="+emsSubsysId+" hostIp="+hostIp+" fip="+fip);
//					loadBalancer.releaseAndFreeFIP( emsSubsysId , fip , (String)lb_array.get(1));
//					//loadBalancer.releaseFIP( emsSubsysId , fip );
//					result = this.changeFloatingIP(false, hostIp, fip);
//					if(result==-1)
//					{
//						logger.error(" ERROR Unable to unset the  FIP : "+fip+" on host : "+hostIp);
//						// Still continuing as nothing can be done other than raising an error 
//					}
//				}
//			}
//		}
		}else{
			
			self.setMode(AseModes.HA);
				
			if (logger.isDebugEnabled()) {
				logger.debug(" peer ip is same as selfip or fip is same as selfip so not removing FIP ");
				}
		}
	}

	public void shutdown() throws Exception{

		if(logger.isInfoEnabled()){
			logger.info("Entering the shutdown method on the cluster manager");
		}

		// Set shutdown flagged
		synchronized(this) {
			this.stopped = true;
		}

		//Return in case of Non-FT mode.
		if(this.self.getMode() == AseModes.NON_FT) {
			return;
		}
		if(self.getMode() != AseModes.NON_FT){
			ArrayList partitions;
			//Not Stopping thread : could be the condition when this method calls first and then monitor thread run after wait.
			//this.byStatusThreadMonitor.stop();
			synchronized( this.partitionTable.synchronizationObj )
			{
				partitions = this.partitionTable.getPartitions(this.self.getId(), AseRoles.ACTIVE);
				if(partitions != null)	{
					for (int i = 0; i < partitions.size(); i++) {
						AsePartition partition = (AsePartition)partitions.get(i);
						// There will be only one partition returned

						if( partition == null ) {
							break;
						}
						// Unset FIP in SystemMonitor
						if(logger.isInfoEnabled()){
							logger.info("Unset Floating IP (in SM) " + partition.getFloatingIp() );
						}	
						setFloatingIP(partition.getFloatingIp(), self, UNSET_FIP);
						//Check if Reference IP address is pingable, if pingable not a network isolation
						// so attempt to connect to LB 
						if(AsePing.ping(refIp) == true) {
							if(logger.isInfoEnabled()){
								logger.info("No Network Isolation");
								logger.info("Release Floating IP (in LB) " + partition.getFloatingIp() );
							}
							this.loadBalancer.releaseFIP(partition.getFloatingIp());

							String value = this.configRep.getValue(Constants.OID_LOADBALANCER_FIP);
							if (  !(value == null || value.equals("") || value.equals(NULL_FIP)) ) {
								if(emslAgent != null){
									EmsLiteConfigurationDetail configDetail = new EmsLiteConfigurationDetail(Constants.OID_SIP_CONNECTOR_IP_ADDRESS, 
											NULL_FIP);
									this.agent.modifyCfgParam(configDetail);
								}else{
								ConfigurationDetail detail = null;
								detail = new ConfigurationDetail(Constants.OID_SIP_CONNECTOR_IP_ADDRESS, 
										NULL_FIP);
								this.agent.modifyCfgParam(detail);
							}
						}
						}
					}//for 
				}
			}// sync
		}
	}

	protected void resolveRole(){
		resolveRole(null);
	}

	/**
	 * Decide if it is needed to takeover this peer's role
	 * @param peer 
	 *
	 * This method will be called whenever we want to resolve the role.
	 * The clients for this method would be 
	 * 1. initialize() method.
	 * The initialize() will call this method, when we want to resolve the 
	 * role for the first time (or) server startup.
	 * 
	 * 2. handleEvent() of the PeerStateChange Listener.
	 */
	protected void resolveRole(AseSubsystem downPeer){
		try{
			if(logger.isInfoEnabled()){
				logger.info("Start resolving role...");
			}

			ArrayList roles = null;

			boolean shallIKillMyself = false;
			synchronized( this.partitionTable.synchronizationObj )
			{
				synchronized(this) 
				{
					if(this.stopped) 
					{
						if (logger.isInfoEnabled()) {


						logger.info("Subsystem stopped. Returning from resolveRole");
						}
						return ;
					}
				}
				
//				String selfInstanceId = 	(String)configRep.getValue(Constants.SELF_CAS_INSTANCE_ID);
//				if (logger.isDebugEnabled()) {
//					logger.debug("CAS domain names : " + selfInstanceId);
//				}
//				
//
//				if ( downPeer != null ) { 
//					roles = ((SimpleRoleResolver)this.roleResolver).decideActiveRole(downPeer);
//					
//					// when downPeer is not null, then self CAS should be ACTIVE, so storing this information in Redis 
//					
//					redisWrapper.getHashOperations().addInHashes("CAS_INSTANCE_HEARTBEAT", selfInstanceId, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyymmddhhmmss")));
//					
//					redisWrapper.getSetOperations().addInSet("ACTIVE_CAS_INSTANCES", selfInstanceId);
//					
//				} else { 
//					roles = this.roleResolver.resolveRole(downPeer);
//					
//					if(redisWrapper.getSetOperations().getLengthOfSet("ACTIVE_CAS_INSTANCES") >= 1){ // active CAS is present
//						String activeId = null;
//						
//						Set<String> activeCAS = redisWrapper.getSetOperations().getAllMemberFrmSet("ACTIVE_CAS_INSTANCES");
//						Iterator value = activeCAS.iterator(); 
//						while (value.hasNext()) { 
//				           activeId =  (String)value.next(); 
//				        }
//					
//						String activeCASTime = redisWrapper.getHashOperations().getHashValue("CAS_INSTANCE_HEARTBEAT", activeId);
//						
//						//compare it with current time
//						
//					}else{
//						
//						redisWrapper.getHashOperations().addInHashes("CAS_INSTANCE_HEARTBEAT", selfInstanceId, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyymmddhhmmss")));
//						
//						redisWrapper.getSetOperations().addInSet("ACTIVE_CAS_INSTANCES", selfInstanceId);
//						
//					}
//				}
				
				if ( downPeer != null ) { 
					roles = ((SimpleRoleResolver)this.roleResolver).decideActiveRole(downPeer);
				} else { 
					roles = this.roleResolver.resolveRole(downPeer);
				}

				//if((roles == null) ||(roles.size() == 0))	
				if(roles == null)	{
					if (logger.isInfoEnabled()) {


					logger.info(" ROLES IS NULL  ");
					}
					return;
				}	
				if(roles.size() == 0)	{
					if (logger.isInfoEnabled()) {


					logger.info(" ROLES SIZE IS ZERO  ");
					}
				}

				//start of code for invoking generateRoleChangeEvent() method
//				if(ccp.getConnectedSubsystems().size() == 1)	{
//					if (logger.isInfoEnabled()) {
//
//
//					logger.info(" Only 1 Subsystem is connected:  " );
//					}
//					//No need to check for these 2 flags in Non-FT case
//					switchToRole(roles,downPeer);
//					networkMutex.releaseLock();
//					fireRoleChangeEvent(downPeer);	
//				}else	{
					fireRoleChangeEvent(downPeer);
					switchToRole(roles,downPeer);
					networkMutex.releaseLock();
				//}

				logger.error("Partition table after role resolving: " + this.partitionTable);

				/**
				 * end of code for invoking generateRoleChangeEvent() method
				 */
				shallIKillMyself = false;
				ArrayList roleList = this.partitionTable.getPartitionInfos( this.self.getId() );
				if( roleList.size() == 0 )
				{
					// Man... I don't have any role in any partition... theres no point in my
					// being alive
					logger.error(" I DON'T HAVE ANY ROLE IN THE PARTITION, SHUTTING SELF DOWN ");
					logger.error(" partitionTable : "+partitionTable);
					shallIKillMyself = true;
				}

				if(shallIKillMyself)
					initiateShutdown();
			}

		}catch(Exception e){
			logger.error("Exception in resolveRol ",e);
		}
	}

	private boolean setFip(AsePartition partition, AseSubsystem downPeer)	throws Exception	{

		int result = -1;
		short op = 0;
		if (logger.isInfoEnabled()) {


		logger.info("Entered setFip()");
		}
		String fip = partition.getFloatingIp();
		synchronized(this) {
			if(this.stopped) {
				if (logger.isInfoEnabled()) {


				logger.info("Subsystem stopped. Returning.");
				}
				return false;
			}

			op = TAKEOVER_FIP;//( downPeer == null? SET_FIP : TAKEOVER_FIP );

			if(logger.isInfoEnabled()){
				logger.info((op == SET_FIP? "Set " : "Takeover ") + fip + " ... ");
			}		

			if( op == SET_FIP )
			{
				if (logger.isInfoEnabled()) {


				logger.info(" Activating FIP : "+fip);
				}
				try
				{
					loadBalancer.activateFIP( this.self.getEmsSubsystemId() , fip );
				}
				catch( Exception ex)
				{
					logger.error(" Error in activating FIP : "+fip);
					return false;
				}

			}

			result = setFloatingIP(fip, downPeer, op);
			String value = this.configRep.getValue(Constants.OID_LOADBALANCER_FIP);
			logger.error(" self.getMode()="+self.getMode()+" AseModes.FT_ONE_PLUS_ONE="+AseModes.FT_ONE_PLUS_ONE);
			
//			if ((self.getMode() == AseModes.FT_ONE_PLUS_ONE) && (value == null || value.equals("") || value.equals(NULL_FIP)) ) 
//			{
//				// In case of 1+1 setup without LoadBalancer, there might be a possibility that
//				// the FIP is set on the other "dead" CAS instance. If the other CAS instance is not dead, no extra step is required
//				
//				//@saneja:bug11318: changed check = 2 as if value is 1 
//				//that means no peer is connecetd and 1 is showing for self
//				if(this.ccp.getNumberOfConnectedMembers()==2)
//				{
//					String hostIp = AseUtils.getIPAddress(this.configRep.getValue(Constants.OID_PEER_SUBSYS_IP));
//					// Validate Peer IP ???
//					this.changeFloatingIP(false, hostIp, fip);
//				}
//
//			}
		} // synchronized

		if (result == -1) {
			logger.error("Unable to " + (op == SET_FIP? "set " : "takeover ") + fip);
			if (logger.isInfoEnabled()) {


			logger.info("Exiting setFip()");
			}
			return false;
		}
		if (logger.isInfoEnabled()) {


		logger.info("Exiting setFip()");
		}
		return true;

	}




	/**
	 * This method generates and sends ROLE change event to various listeners
	 *
	 */
	private void fireRoleChangeEvent(AseSubsystem downPeer)	throws Exception {
		if (logger.isInfoEnabled()) {


		logger.info(" Inside fireRoleChangeEvent() method  ");
		}
		ArrayList partitionInfos = this.partitionTable.getPartitionInfos(self.getId());
		for(int i=0;i<partitionInfos.size();i++)	{
			PartitionInfo partitionInfo = (PartitionInfo)partitionInfos.get(i);
			if (logger.isInfoEnabled()) {


			logger.info(" partitionInfo:  " +partitionInfo);
			}
			short role = partitionInfo.getRole();
			if (logger.isInfoEnabled()) {

			
			logger.info(" role:  " +role);
			}
			AsePartition partition = null;
			if((AseRoles.ACTIVE == role)&&(bSwitchToActiveRole || bGenerateActiveRoleChange))	{
				partition = this.partitionTable.getPartition(partitionInfo.getFip(),
						self.getId(),
						AseRoles.ACTIVE);
				if (logger.isInfoEnabled()) {


				logger.info(" partition with ACTIVE role:  " +partition);
				}
			//	if(generateRoleChangeEvent(partition,AseRoles.ACTIVE))	{
				
				if(downPeer==null){
					
					downPeer = new AseSubsystem(
							peerSubsystemName);
					String peerIpAddress = AseUtils.getIPAddress(configRep.getValue(Constants.OID_PEER_SUBSYS_IP));
					downPeer.setHost(peerIpAddress);
					
					logger.info(" Peer is :  " +downPeer);
				}
					logger.info(" Invoke setFip():  ");
					setFip(partition,downPeer);
					
					generateRoleChangeEventForActiveRole(partition,partitionInfo,AseRoles.ACTIVE);
			//	} 
			}else if((AseRoles.STANDBY == role)&&(bSwitchToStandbyRole))	{
				partition = this.partitionTable.getPartition(partitionInfo.getFip(),
						self.getId(),
						AseRoles.STANDBY);
				
				if(generateRoleChangeEvent(partition,AseRoles.STANDBY))	{
					generateRoleChangeEventForStandbyRole(partition,partitionInfo,AseRoles.STANDBY);
				}
			}

		}	

	}

	private void switchToRole(ArrayList roles,AseSubsystem downPeer)	throws Exception	{
		if(logger.isInfoEnabled()){
			logger.info("Inside switchToRole():");
		}
		AsePartition partition;
		for ( int i = 0; i < roles.size(); i++) {
			PartitionInfo role = (PartitionInfo)roles.get(i);
			if(logger.isInfoEnabled()){
				logger.info("Switch to " + role); 
			}
			switch (role.getRole()) {
			case AseRoles.ACTIVE:
				partition = this.partitionTable
				.getPartition(role.getFip(), self.getId(), AseRoles.ACTIVE);
				this.switchToActiveRole(partition, downPeer);
				break;
			case AseRoles.STANDBY_TO_ACTIVE:
				partition = this.partitionTable.getPartition(role.getFip(), self.getId()
						, AseRoles.ACTIVE);
				//just a precaution check for null value
				if ( null != partition ) {
					if (logger.isDebugEnabled()) {


					logger.debug("setting standby role to null");
					}
					partition.setMember(null , AseRoles.STANDBY) ;
				}
				this.switchToActiveRole(partition, downPeer);
				ArrayList partitions = this.partitionTable.getPartitions(self.getId(),AseRoles.STANDBY);
				if(partitions != null) {
					for(int j =0; j<partitions.size(); j++)	{
						if (logger.isDebugEnabled()) {


						logger.debug("setting standby role to null for "+(AsePartition)partitions.get(j));
						
}
						((AsePartition)partitions.get(j)).setMember(null , AseRoles.STANDBY);
					}
				}
				break;
			case AseRoles.STANDBY:
				synchronized( this.partitionTable.synchronizationObj )
				{
					this.switchToStandbyRole(this.partitionTable
							.getPartition(role.getFip()));
				}
				break;
			case AseRoles.RELEASE_STANDBY:
				releaseStandbyRole(role);
				break;
			case AseRoles.RELEASE_ACTIVE:
				releaseActiveRole(role);
				break;
			}
		}


	}

	public void releaseActiveRole(PartitionInfo partitionInfo) throws Exception {
		//simply shut down the subsystem
		//initiateShutdown();
	}

	public void releaseStandbyRole(PartitionInfo partitionInfo) throws Exception {
		if(logger.isInfoEnabled()){
			logger.info("Release standby role for " + partitionInfo.getFip());
		}		

		this.sendRoleMessage(partitionInfo, (short)2, null);
	}


	/**
	 * This method communicate with the SystemManager to takeover the
	 * Floating IP  
	 */
	protected int setFloatingIP(String fip, AseSubsystem subsystem, short op) throws Exception{

		
		String selfIP =  this.configRep.getValue(Constants.OID_BIND_ADDRESS);
		String selfAddr=AseUtils.getIPAddress(selfIP);
		
		if(logger.isInfoEnabled()){
			logger.info("Set/unset Floating IP .. for self ." +selfAddr);
		}
		

		if (!AseModes.isFtMode(this.self.getMode())
				&& AseModes.FT_N_PLUS_LITE != this.self.getMode()
				&& AseModes.FT_N_PLUS != this.self.getMode()
				&& AseModes.HA != this.self.getMode()) {
			
			if(logger.isInfoEnabled()){
				logger.info("Not setting  Floating IP as not in FT mode ...");
			}

			return 0;
		}

		int result = -1;
		if (!selfAddr.equals(fip)) {
			
			switch (op) {
			case SET_FIP:
				//if(logger.isInfoEnabled()){
				logger.error("Set the Floating IP :" + fip + " for self at" 
						+ (self.getHost() == null? "" : self.getHost()));
				//}
				result = this.changeFloatingIP(true, self.getHost(), fip);
				break;
			case TAKEOVER_FIP:
				if (subsystem == null) {
					throw new Exception("Unable to takeover floating IP: subsystem is null");
				}
				
					// if(logger.isInfoEnabled()){
					logger.error("Remove the Floating IP :"
							+ fip
							+ " from :"
							+ (subsystem.getHost() == null ? "" : subsystem
									.getHost()));
					// }
					result = changeFloatingIP(false, subsystem.getHost(), fip);
					if (result == -1) {
						if (logger.isInfoEnabled()) {
	
							logger.info("FAILED TO UNSET FIP. Can be due to cable pullout so continue");
						}
						// break;
					}
				
				//if(logger.isInfoEnabled()){
				logger.error("Set the Floating IP :" + fip + " for self at" 
						+ (self.getHost() == null? "" : self.getHost()));
				//}
				result = changeFloatingIP(true, self.getHost(), fip);
				break;
				
			case UNSET_FIP:
				if (subsystem == null) {
					logger.error("Unable to remove floating IP: subsystem is null");
				}
				//if(logger.isInfoEnabled()){
				logger.error("Remove the Floating IP :" + fip + " from :" 
						+ (subsystem.getHost() == null? "" : subsystem.getHost()));
				//}		
				result = changeFloatingIP(false, subsystem.getHost(), fip);
				break;
			}
		}else{
			logger.error("Self ip is same as fip so no need to set/unset");
			return 0;
		}
		return result;

	}

	private boolean checkFIP(String fip){
		//String ifConfig = "ifconfig -a";
		boolean result = false;
//		InputStream lsOut = null;
//		InputStreamReader r = null;
//		BufferedReader in = null;
		if(logger.isInfoEnabled()){
			logger.info("Checking Floating IP ..." + fip);
		}
		try {
			
			Enumeration<NetworkInterface> enu = NetworkInterface.getNetworkInterfaces();
			while(enu.hasMoreElements()){
				NetworkInterface nI = enu.nextElement();
				Enumeration<InetAddress> enuInet = nI.getInetAddresses();
				while(enuInet.hasMoreElements()){
					if (fip.equals(((InetAddress)enuInet.nextElement()).getHostAddress())){
						if(logger.isInfoEnabled()){
							logger.info("Found Floating IP ...");
	            		}
						result = true;
	            		break;
					}
				}
			}
			
//			Process ifconfigProcess = Runtime.getRuntime().exec(ifConfig);
//			lsOut = ifconfigProcess.getInputStream();
//            r = new InputStreamReader(lsOut);
//            in = new BufferedReader(r);
//            String str = in.readLine();
//            String prevStr = null;
//            while (str != null){
//        		if(logger.isInfoEnabled()){
//        			logger.info("Checking Floating IP in " + str);
//        		}
//            	if ((prevStr != null && prevStr.contains("UP")) && str.contains(fip)){
//            		logger.error("Found Floating IP ...");
//            		result = true;
//            		break;
//            	}
//            	prevStr = str; 
//            	str = in.readLine();
//            }
            
		} catch (Exception e) {
			logger.error("Error in Checking FIP", e);
			//This is done in case any issue appears while checking the FIP
			//Active CAS should not kill itself
			result = true;
		}
//		finally{
//			try{
//				if (in != null)
//					in.close();
//				if (r != null)
//					r.close();
//				if (lsOut != null)
//					lsOut.close();
//			}catch (IOException e) {
//				logger.error("Error in Checking FIP", e);
//			}
//		}
		return result;
		
	}
	/**
	 * This method opens a TCP connection with the system monitor and
	 * sends a SET or UNSET message to set the Floating IP on the machine.
	 */
	protected int changeFloatingIP(boolean setFlag, String host, String floatingIP_orig) {

		if(logger.isInfoEnabled()){
			logger.info(" FIP Received => "+floatingIP_orig);
		}
		// Removing '[' and ']' from the ipv6 address if they are there
		String floatingIP = null;
		int start = floatingIP_orig.lastIndexOf(AseStrings.SQUARE_BRACKET_CHAR_OPEN);
		int end = floatingIP_orig.lastIndexOf(AseStrings.SQUARE_BRACKET_CHAR_CLOSE);
		if( start != -1 && end != -1)
		{
			floatingIP = floatingIP_orig.substring( start+1, end);
		}
		else
			floatingIP = floatingIP_orig;
	
		
	//JSR 289.34 
	String[] mulFloatingIP = floatingIP.split(AseStrings.COMMA);
	List<String> floatingPointsList = Arrays.asList(mulFloatingIP);
	boolean check = false; 
	if(logger.isInfoEnabled()){
		logger.info(" List of FIP's.. => "+floatingPointsList);
	}
	for(String fPoints : floatingPointsList){

		if(logger.isInfoEnabled()){
			logger.info(" FIP to be sent => "+fPoints);
		}
		
		fPoints=AseUtils.getIPAddress(fPoints);
		
		if(logger.isInfoEnabled()){
			logger.info(" FIP IPAddress to be sent is  => "+fPoints);
		}
		if(setFlag)
		{
			boolean fipAlreadyPresnt = AsePing.ping(fPoints, (short)DEFAULT_SM_PORT, 100);
	
			if(logger.isInfoEnabled()){
				logger.info("fipAlreadyPresnt:"+fipAlreadyPresnt);
			}
	
			if(fipAlreadyPresnt) {
				changeFloatingIP(false, host, floatingIP);
			}
		}
		String command  = (setFlag ? "SET " : "UNSET ") + fPoints;

		logger.error("Sending the following to System Monitor on host[" + host +
				"] and port[" + this.smPort + "]");
		logger.error("Command ::: " + command);

		for(int i=0; i<this.smRetries;i++){
			int value = 0;
			//Create a TcpClient object...
			AseTcpClient client = new AseTcpClient(host, (short)this.smPort, this.smTimeout);

			try{
				//Connect to the System Monitor
				value = client.connect();

				//Check the return value.
				if(value != 0){
					logger.error("Not able to connect to the System Monitor at host:" + host);
					continue;
				}

				//Send the command to the system monitor.
				value = client.send(command);
				if(value != 0){
					logger.error("Not able to send message to the system monitor at" + host);
					continue;
				}

				//Check whether the command went through successfully or NOT
				String reply = client.receive();
				logger.error("Reply received from System Monitor: " + reply);

				//If we have received a SUCCESS reply, continue, else retry.
				if(reply != null && reply.length() > 1 && reply.charAt(0) == '1'){
					logger.error("Successfully executed System Monitor command...");
					check=true;
					
					//@saneja:bug11318 added break as operation is repeated again inspite being succesful
					break;
				}
			} catch(Exception exp) {
				logger.error("Sending command to System Monitor", exp);
			} finally {
				try {
					client.disconnect();	
				} catch(Exception exp) {
					logger.error("Disconnecting System Monitor", exp);
				}
			}
		}

	}
	
		//@saneja:bug11318 corrected if condition
		if (check) {
			return 0;
		} else {
			logger.warn("No. of retries exceeded for changeFloatingIP. So returning a failure.");
			return -1;
		}
}





/**
 * This method will be called initially to publish the system information
 * like (IP, PORT, SUBSYS_ID) to other peers.
 */
//protected void publishSystemInfo() throws Exception{
//	try{
//		//Get self inforation.
//		String selfId = this.self.getId();
//		if(logger.isInfoEnabled()){
//			logger.info("Entering the publishSystemInfo method for :" + selfId );
//		}
//
//		if(!this.isNegotiationReqd()){
//			if(logger.isInfoEnabled()){
//				logger.info("Check FT Mode returned FALSE, So not doing publishInfo.");
//			}	
//			return;
//		}
//
//		//Create the message object
//		MessageFactory factory = ccp.getMessageFactory();
//		SystemInfoMessage msg = (SystemInfoMessage) 
//		factory.createMessage(MessageTypes.SYS_INFO_MESSAGE, PeerMessage.MESSAGE_OUT);
//
//		//Set the message values.
//		msg.setSequenceNo((short)SYS_INFO_QUERY);
//		msg.setSubsystem(self);
//
//		//write partition infos
//		ArrayList partitionInfos;
//		synchronized( this.partitionTable.synchronizationObj )
//		{
//			partitionInfos = this.partitionTable.getPartitionInfos(selfId);
//		}
//		msg.setNumPartitionInfos(partitionInfos.size());
//		AsePartitionTable.writePartitionInfos(msg.getObjectOutput(), partitionInfos);
//
//		//Send this message to all in the Network.
//		ccp.send(msg);
//
//		this.waitForPeerStatus();
//	}catch(Exception e){
//		logger.error("Publishing SYS_INFO", e);
//		throw e;
//	}		
//}

private String getAddress(AseSubsystem subsys)	{
	String address;
	if(subsys.getSignalIp() == null)        {
		address = subsys.getHost();
	}else	{
		address = subsys.getSignalIp();
	}
	return address;

}

public void setRoleResolvedToActive(boolean active)	{
	bSwitchToActiveRole = active;
}		

public void setRoleResolvedToStandby(boolean standby)	{
	bSwitchToStandbyRole = standby;
}		
/**
 * 
 * @param cluster
 * @throws Exception
 */
//public void waitForPeerStatus(){
//
//	if(logger.isInfoEnabled()){
//		logger.info("Entering the waitForPeerStatus");
//	}
//	boolean status = true;
//	// It will hold sync. object for each AseSubsystem
//	Object obj = null;
//	String address;
//
//	do{
//		status = true;
//		Iterator iterator = this.ccp.getAllSubsystems();
//		for(;iterator.hasNext();){
//			AseSubsystem subsys = (AseSubsystem)iterator.next();
//			if(subsys.getSignalIp() == null)        {
//				address = subsys.getHost();
//			}
//			else    {
//				address = subsys.getSignalIp();
//			}
//
//			String subsysId = subsys.getId() == null ? "" : subsys.getId(); 
//
//			//
//			if(logger.isInfoEnabled()){
//				logger.info("Checking subsystem ::::" + subsys.getId() + "::" + address);
//			}
//
//			//In case of SELF, just ignore it and continue
//			if(subsysId.equals(self.getId())){
//				continue;
//			}
//
//			//boolean condition1 = subsysId.equals(""); // It means we have not received any reply for this subsystem
//			boolean condition2 = false;
//			obj = this.ccp.getSynchronizationObj( address );
//			if( obj != null )
//			{
//				synchronized( obj )
//				{
//					if(logger.isInfoEnabled()){
//						logger.info("receptionStatus :" + this.ccp.getReceptionStatus(address) +" host: "+address);
//					}
//					if( this.ccp.getReceptionStatus(address).equals(SYS_INFO_REPLY__NOT_RECEIVED) ){
//						// ReceptionStatus==0 means we have not sent the SYS_INFO message to it
//						// ReceptionStatus==1 means we have not yet received the reply for the SYS_INFO message we sent  
//						// ReceptionStatus==2 means we have received the reply and processed it "fully"
//						// ReceptionStatus==3 means that this system has gone down while we were waiting for a reply from it
//						condition2 = true;
//					}
//				}
//			}
//			else
//			{
//				logger.error(" Sync Object found to be null, So not waiting for it ");
//			}
//			if( condition2 )
//			{
//				if(logger.isInfoEnabled()){
//					//logger.info("Waiting for reply from host :" + subsys.getHost());
//					logger.info("Waiting for reply from host :" + address);
//				}
//				status = false;
//				break;
//			}
//		}
//
//		//In case of any of the subsystem is yet to reply, wait here.
//		//We will get notified, when we receive a reply from a group member.
//		//Also we will get notified, when a peerDown was detected. 
//		if(!status){
//			try{
//				synchronized( obj )
//				{
//					obj.wait();
//				}
//			}catch(InterruptedException e){
//				logger.error("Waiting on sysInfo object", e);
//			}
//		}
//	}while(!status);
//
//	if(logger.isInfoEnabled()){
//		logger.info("Out of the waitForPeerStatus");
//	}
//	return;
//} 



private boolean generateRoleChangeEvent(AsePartition partition, short role)	{
//	boolean bGenerateRoleChangeEvent = false;

//	PartitionInfo partitionInfo = partition.getPartitionInfo(self.getId());
////	Vector subSystems = ccp.getConnectedSubsystems();
//	logger.error("Number of connected Subsystems: " + subSystems.size());
//	logger.error("Connected Subsystems: " + subSystems);
//
//	if(subSystems.size() == 1)	{
//		logger.error("Only one subsystem is connected: ");
//		bGenerateRoleChangeEvent = true;
//	}else if(subSystems.size() > 1)	{
//		logger.error("Multiple subsystems are connected: ");
//		if (  !(sipLbFIP == null || sipLbFIP.equals("") || sipLbFIP.equals(NULL_FIP)) ) {
//			//Logic for handling multiple subsystems with external SIP LB
//			logger.error("External SIP LB is PRESENT: ");
//			//Any partition can have max 1 ACTIVE, with possibility of second role as 'null' or STANDBY
//			if(partition.getMember(AseRoles.ACTIVE) != null)	{ //have 1 ACTIVE member
//				if((partition.getMember(AseRoles.STANDBY)!= null)//have 1 STANDBY member
//						||(partition.getMember(AseRoles.NONE)== null))	{//OR have 1 member with role 'null'
//					bGenerateRoleChangeEvent = true;	
//				}
//			}
//			//Any partition can have max 1 STANDBY
//			//Any other scenario is wrong and should return false
//		}else	{
//			//This is case of 1+1 FT without external SIP LB
//			//check if there is a member in this partition which has
//			//not assumed either ACTIVE or STANDBY ROLE
//			if((partition.getMember(AseRoles.ACTIVE)== null)
//					&& (partition.getMember(AseRoles.STANDBY)== null))	{
//				bGenerateRoleChangeEvent = false;
//			}else	{
//				//Since all memebers in this partition have resolved there ROLE
//				//so generate Role Change Event
//				bGenerateRoleChangeEvent = true;
//			}
//		}	
//
//	}
		boolean bGenerateRoleChangeEvent = true;
		if (logger.isInfoEnabled()) {

			logger.info("generateRoleChangeEvent() returns: "
					+ bGenerateRoleChangeEvent);
		}
		return bGenerateRoleChangeEvent;



}


private void generateRoleChangeEventForActiveRole(AsePartition partition,
		PartitionInfo partitionInfo,short role)	throws Exception {

	

	//For the all the subsystem with secondary, create a PR_READY_START_REPL
	String[] members = partition.getMembers(AseRoles.STANDBY);
	for(int i=0;i<members.length;i++){
		this.generatePeerReadyReplicateEvent(partition, members[i]);
	}
	//doing it second time here
	partition.setMember(self.getId(),AseRoles.ACTIVE);

	this.generateRoleChangeEvent(partitionInfo);

	//Update the current role to active in the EMS
	if(logger.isInfoEnabled()){
		logger.info("Going to modify Current Role to ACTIVE in the EMS");
	}
	ConfigurationDetail detail = null;
	EmsLiteConfigurationDetail configDetail = null;
	if(emslAgent != null){
		configDetail = new EmsLiteConfigurationDetail(Constants.OID_CURRENT_ROLE, "Active");
		this.agent.modifyCfgParam(configDetail);
	}else{
		detail = new ConfigurationDetail(Constants.OID_CURRENT_ROLE, "Active");
		this.agent.modifyCfgParam(detail);
	}
	ConfigRepository configDb = BaseContext.getConfigRepository();
	configDb.setValue(Constants.OID_CURRENT_ROLE,"Active");
	
	String fip = configDb.getValue(Constants.OID_SIP_CONNECTOR_IP_ADDRESS);// No IP Address lookup required at here.
	
	fip = setFip(fip);
	if(emslAgent != null){
		configDetail = new EmsLiteConfigurationDetail(Constants.OID_SIP_CONNECTOR_IP_ADDRESS, fip);
		this.agent.modifyCfgParam(configDetail);
	}else{
	detail = new ConfigurationDetail(Constants.OID_SIP_CONNECTOR_IP_ADDRESS, fip);
	this.agent.modifyCfgParam(detail);
	}
	
	if(logger.isInfoEnabled()){
		logger.info("***** Subsystem took the ACTIVE ROLE for :" + fip + "*****");
	}
	bGenerateActiveRoleChange = false;

}

private void generateRoleChangeEventForStandbyRole(AsePartition partition,
		PartitionInfo partitionInfo,short role)	throws Exception {
	String fip = partition.getFloatingIp();
	this.generateRoleChangeEvent(partitionInfo);
	//Update the Current Role to Standby in the EMS
	if(logger.isInfoEnabled()){
		logger.info("Going to modify Current Role to STANDBY in the EMS");
	}
	ConfigurationDetail detail = null;
	EmsLiteConfigurationDetail configDetail = null; 
	if(emslAgent != null){
		configDetail = new EmsLiteConfigurationDetail(Constants.OID_CURRENT_ROLE, "Standby");
		this.agent.modifyCfgParam(configDetail);
	}else{
		 detail = new ConfigurationDetail(Constants.OID_CURRENT_ROLE, "Standby");
		this.agent.modifyCfgParam(detail);
	}
	
	ConfigRepository configDb = BaseContext.getConfigRepository();
	configDb.setValue(Constants.OID_CURRENT_ROLE,"Standby");

	// The following steps in the if block should be performed only if LB is
	// there
	String value = this.configRep.getValue(Constants.OID_LOADBALANCER_FIP);
	if (  !(value == null || value.equals("") || value.equals(NULL_FIP)) ) {
		if(emslAgent != null){
			configDetail = new EmsLiteConfigurationDetail(Constants.OID_SIP_CONNECTOR_IP_ADDRESS, NULL_FIP);
			this.agent.modifyCfgParam(configDetail);
		}else{
		detail = new ConfigurationDetail(Constants.OID_SIP_CONNECTOR_IP_ADDRESS, NULL_FIP);
		this.agent.modifyCfgParam(detail);
	}
	}

	if(logger.isInfoEnabled()){
		logger.info("***** Subsystem took the STANDBY ROLE for :" + partition.getFloatingIp() + "*****");
	}

}

/**
 * This method will be called to become ACTIVE in the cluster.
 * This method will send a ROLE = ACTIVE message to all the members in the cluster
 *   
 */
protected boolean switchToActiveRole(AsePartition partition, AseSubsystem downPeer) throws Exception{
	if(logger.isInfoEnabled()){
		logger.info("Switching to ACTIVE role...");
	}		

	// Start activation process
	partition.setMember(self.getId(),AseRoles.ACTIVATING);

	String fip = partition.getFloatingIp();

	partition.setFloatingIp(fip);
	
	PartitionInfo partitionInfo = partition.getPartitionInfo(self.getId());

	//Send the role message out to all the members in the cluster.
//	this.sendRoleMessage(partitionInfo, (short)2, null);

	//Now set role as ACTIVE.added rajendra here from down
	partition.setMember(self.getId(),AseRoles.ACTIVE);
	
		if (logger.isDebugEnabled()) {
			logger.debug("subscribe channel for standby to get notified role ."
					+ AseMain.standbyNotiChannelName);
		}
		try {
			
			if (self.getMode() != AseModes.NON_FT
					|| self.getMode() != AseModes.HA) {
				
				redisWrapper.getPubSubOperations().subscribe(
						new AsePubSubPropsListener(
								AseMain.standbyNotiChannelName, redisWrapper),
						AseMain.standbyNotiChannelName);
				RedisAlarmHandler.redisIsAccessible(self.getId());
			}

		} catch (RedisLettuceConnectionException e) {
			logger.error("exception while writing headrtbeat in redis " + e);

			RedisAlarmHandler.redisNotAccessible(self.getId());
		} catch (RedisLettuceCommandTimeoutException e) {
			logger.error("exception while writing headrtbeat in redis " + e);
			RedisAlarmHandler.redisNotAccessible(self.getId());
		} catch (Exception e) {
			logger.error("Exception thrown " + e);
		}
		

	return true;
}

/**
 * This method will be called to become STANDBY in a cluster.
 * This method will send a ROLE = STANDBY message to all the members in the cluster.
 * @param floatingIp
 */
	protected void switchToStandbyRole(AsePartition partition) throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("Switching to STANBY role ...");
		}
		try {

			if (logger.isInfoEnabled()) {
				logger.info("publish standby role on channel "
						+ AseMain.standbyNotiChannelName);
			}

			if (self.getMode() != AseModes.NON_FT
					|| self.getMode() != AseModes.HA) {
			redisWrapper.getPubSubOperations().publishMessge(
					AseMain.standbyNotiChannelName, self.getId() + "_" + "UP");
			RedisAlarmHandler.redisIsAccessible(self.getId());
			}

		} catch (RedisLettuceConnectionException e) {
			logger.error("exception while writing headrtbeat in redis " + e);

			RedisAlarmHandler.redisNotAccessible(self.getId());
		} catch (RedisLettuceCommandTimeoutException e) {
			logger.error("exception while writing headrtbeat in redis " + e);
			RedisAlarmHandler.redisNotAccessible(self.getId());
		} catch (Exception e) {
			logger.error("could not publish standby role to active peer ", e);
		}
		
		/**
		 * will unsubscribe from hereR
		 */
		//redisWrapper.getPubSubOperations().unsubscribe(AseMain.standbyNotiChannelName);
	//this.sendRoleMessage(partitionInfo, (short)2, null);
}

protected void sendRoleMessage(PartitionInfo partitionInfo, short seq, String[] members) throws Exception{
//	sendRoleMessage(RoleSyncMessage.NO_ERROR, partitionInfo, seq, members);
	
}

/**
 * This method will send out a deciding message to the cluster.
 * @param cluster
 */
//protected void sendRoleMessage(short errorCode, PartitionInfo partitionInfo, short seq, String[] members) throws Exception{
//	if(logger.isInfoEnabled()){
//		logger.info("sendRoleMessageToCluster called for: " + partitionInfo);
//	}
//
//	//Create the message object
//	MessageFactory factory = ccp.getMessageFactory();
//	RoleSyncMessage msg = (RoleSyncMessage) 
//	factory.createMessage(MessageTypes.ROLE_SYNC_MESSAGE, PeerMessage.MESSAGE_OUT);
//
//	//Set the message values.
//	msg.setErrorCode(errorCode);
//	msg.setFloatingIp(partitionInfo.getFip());
//	msg.setSequenceNo(seq);
//
//	msg.setSubsysId(partitionInfo.getSubsysId());
//	msg.setRole(partitionInfo.getRole());
//
//	//Now send the message
//	if(logger.isDebugEnabled()){
//		logger.debug("Sending the Role sync message ::: " + msg);
//	}
//	ccp.send(msg);
//}

/**
 * This method is used to generate a RoleChangedEvent to all the listeners
 * registered with it.
 * @param floatingIp
 */
protected void generateRoleChangeEvent(PartitionInfo partitionInfo){
//	for(int i=0; i < this.listeners.length; i++){
//		if(this.listeners[i] != null) {
//			if(logger.isDebugEnabled()){
//				logger.debug("generateRoleChangeEvent(): invoking listener at index: " + i);
//			}
//			try {
//				listeners[i].roleChanged(partitionInfo.getFip(), partitionInfo);
//			} catch(Exception exp) {
//				logger.error("Error in listener.roleChanged()", exp);
//			}
//		}
//	}	
	
	if(logger.isDebugEnabled()){
		logger.debug("generateRoleChangeEvent() Called");
	}
	//used treemap to iterate in sort order and avoided use of Constants.RCL_NUM_OF_LISTENERS to make it more generic
	Set<Short> priortySet = listeners.keySet();
	for(short priorty:priortySet){
		if(logger.isDebugEnabled()){
			logger.debug("generateRoleChangeEvent() Generating on priority::"+priorty);
		}
		List<RoleChangeListener> roleChangeListenerList = listeners.get(priorty);
		
		if(roleChangeListenerList != null) {
			if(logger.isDebugEnabled()){
				logger.debug("generateRoleChangeEvent(): listeners found at priority: " + priorty);
			}
			for(RoleChangeListener roleChangeListener: roleChangeListenerList){
				
				try {
					roleChangeListener.roleChanged(partitionInfo.getFip(), partitionInfo);
					if(logger.isDebugEnabled()){
						logger.debug("generateRoleChangeEvent(): event generated for listener:"+roleChangeListener);
					}
				} catch(Exception exp) {
					logger.error("Error in listener.roleChanged()", exp);
				}
			}//@End for on RoleChangeListenerList
			
			
		}else{
			if(logger.isDebugEnabled()){
				logger.debug("generateRoleChangeEvent(): listeners NULL at priority: " + priorty);
			}
		}//@End if(roleChangeListenerList != null)
	}//@End for loop on priorty
	
	if(logger.isDebugEnabled()){
		logger.debug("generateRoleChangeEvent() Exit");
	}
}

/**
 * This method parses the configuration Cluster XML 
 * and creates the objects of AseCluster
 * whenever this system is part of a cluster. 
 */
protected void configureCluster() throws Exception{
	if (logger.isInfoEnabled()) {


	logger.info("configuring cluster");
	}
	boolean isRoleNegotiationReqd = this.isNegotiationReqd();

	//get the networkmutex port
	this.networkMutexPort = DEFAULT_MUTEX_PORT;
	try{
		String value = this.configRep.getValue(Constants.PROP_MUTEX_PORT);
		value = (value == null) ? "" : value;
		this.networkMutexPort = Integer.parseInt(value);
	}catch(NumberFormatException nfe){
		if(logger.isDebugEnabled()){
			logger.debug("NetworkMutex port not defined.So using the default PORT"); 
			logger.debug(" default port is :" + this.networkMutexPort);
		}
	}

	//get the system monitor port
	this.smPort = DEFAULT_SM_PORT;
	try{
		String value = this.configRep.getValue(Constants.PROP_SM_PORT);
		value = (value == null) ? "" : value;
		this.smPort = Integer.parseInt(value);
	}catch(NumberFormatException nfe){
		if(logger.isDebugEnabled()){
			logger.debug("The System Monitor port not defined. So using the default PORT :" + this.smPort);
		}
	}

	//get the system monitor retries.
	this.smRetries = DEFAULT_SM_RETRIES;
	try{
		String value = this.configRep.getValue(Constants.PROP_SM_RETRIES);
		value = (value == null) ? "" : value;
		this.smRetries = Integer.parseInt(value);
	}catch(NumberFormatException nfe){
		if(logger.isDebugEnabled()){
			logger.debug("The System Monitor retries not defined. So using the default value :" + this.smRetries);
		}
	}

	//get the system monitor connection timeout.
	this.smTimeout = DEFAULT_SM_TIMEOUT;
	try{
		String value = this.configRep.getValue(Constants.PROP_SM_TIMEOUT);
		value = (value == null) ? "" : value;
		this.smTimeout = Integer.parseInt(value);
	}catch(NumberFormatException nfe){
		if(logger.isDebugEnabled()){
			logger.debug("The System Monitor timeout not defined. So using the default value (msecs) :" + this.smTimeout);
		}
	}

	//Check whether floating IP takeover required or not.
	this.floatingIpTakeoverReqd = DEFAULT_FLOATING_IP_REQD;
	try{
		String value = this.configRep.getValue(Constants.PROP_FLOATING_IP_REQD);
		value = (value == null) ? "" : value;
		this.floatingIpTakeoverReqd = (Short.parseShort(value) != 0);
	}catch(NumberFormatException nfe){
		if(logger.isDebugEnabled()){
			logger.debug("The Floating IP takeover Required flag is not available. So using the default value :" + this.floatingIpTakeoverReqd);
		}
	}

	//Get the PING Protocol
	this.refPingProtocol = this.configRep.getValue(Constants.PROP_PING_PROTOCOL);
	this.refPingProtocol = (this.refPingProtocol == null) ? "" : this.refPingProtocol;
	if(!(this.refPingProtocol.equalsIgnoreCase(PROTOCOL_TCP) || 
			this.refPingProtocol.equalsIgnoreCase(PROTOCOL_UDP))){

		this.refPingProtocol = DEFAULT_PING_PROTOCOL;
		if(logger.isDebugEnabled()){
			logger.debug("The reference PING protocol not defined. So using the default protocol :" + this.refPingProtocol);
		}
	}

	//get the PORT number for PINGing the reference IP(s).
	this.refPingPort = DEFAULT_PING_PORT;
	try{
		String value = this.configRep.getValue(Constants.PROP_PING_PORT);
		value = (value == null) ? "" : value;
		this.refPingPort = Integer.parseInt(value);
	}catch(NumberFormatException nfe){
		if(logger.isDebugEnabled()){
			logger.debug("The TCP ping port not defined. So using the default :" + this.refPingPort);
		}
	}

	String sipFIP = AseUtils.getIPAddressList(this.configRep.getValue(Constants.OID_SIP_FLOATING_IP), false);
	sipFIP = (sipFIP == null) ? "" : sipFIP.trim();
	sipFIP = adjustFIPFormat(sipFIP);

	String httpFIP = AseUtils.getIPAddressList(this.configRep.getValue(Constants.OID_HTTP_FLOATING_IP), false);
	httpFIP = (httpFIP == null) ? "" : httpFIP.trim();
	httpFIP = adjustFIPFormat(httpFIP);

	String peerIp = AseUtils.getIPAddress(this.configRep.getValue(Constants.OID_PEER_SUBSYS_IP));
	peerIp = (peerIp == null) ? "" : peerIp.trim();
	
	int numberOfFIPs = sipFIP.split(AseStrings.COMMA).length;

	refIp = this.configRep.getValue(Constants.OID_REFERENCE_IP);
	refIp = (refIp == null) ? "" : refIp.trim();
	refIp = adjustFIPFormat(refIp);

	//Validate the SIP floating IP
	if(sipFIP.trim().equals(""))
		throw new Exception("SIP IP Cannot be NULL");

	//Validate the HTTP floating IP
	Object tomcatServer = Registry.lookup(Constants.NAME_WEB_CONTAINER);
	if(tomcatServer != null && httpFIP.equals(""))
		throw new Exception("HTTP IP Cannot be NULL");

	//Validate the Peer IP.
	if(isRoleNegotiationReqd && peerIp.trim().equals(""))
		throw new Exception("Peer Subsystem IP Cannot be NULL");

	//Validate the reference IP.
	if(isRoleNegotiationReqd && this.refIp.equals("")){
		throw new Exception("Reference IP should not be NULL");
	}

	//initialize LoadBalncerInterface
	this.loadBalancer = LoadBalancerFactory.getInstance()
	.getLoadBalancerInterface(self.getEmsSubsystemId());

	this.loadBalancer.initialize();
	this.loadBalancer.setPartitionTable(this.partitionTable);
	//if BayPackets' load balnacer is not used and floating IP is available
	//create a partition and set self as its active member

	//initialize AseRoleResolver
	this.roleResolver = RoleResolverFactory.getInstance().getRoleResolver();
	this.roleResolver.setPartitionTable(this.partitionTable);
	this.partitionTable.setNoOfFips(numberOfFIPs);
	this.roleResolver.initialize();
	//Setting partitionTable later was causing the problem as total number of
	//active subsystem is set in intialize functionof roleResolver , but at 
	//this time parttionTable is null. So setting partitionTable before 
	//initializing
	//this.roleResolver.setPartitionTable(this.partitionTable);
	if (logger.isInfoEnabled()) {


	logger.info("leaving configuring cluster");
}
}

public LoadBalancerInterface getLoadBalancer()
{
	return loadBalancer;
}

private boolean anyProblem( String ems_subsys_id, String lb_timeStamp, HashMap connectedMemberTimestamp )
{
	try
	{
		ArrayList arr = (ArrayList) connectedMemberTimestamp.get( ems_subsys_id );
		String joiningTime=null;
		if( arr==null )
		{
			// This member is not yet connected still an FIP is being shown against it
			return true;
		}
		else
			joiningTime = (String) arr.get(0);

		if( Long.decode(joiningTime).longValue() > ( (Long.decode(lb_timeStamp)).longValue()*1000 ) )

		{
			return true;
		}
		return false;
	}
	catch( NumberFormatException ex )
	{
		return true;
		// This exception has literally no chance of coming because the time stamp being non-number
		// will be caught at other levels
		// If still it comes we canot deactivate the FIP so we return TRUE
	}
}

private Iterator fillFipDetails( String FIPTableDump )
{
	HashMap tobeFilled = new HashMap();
	StringTokenizer tokens = new StringTokenizer(FIPTableDump.trim(), "|,=");

	String subSysId=null, timeStamp=null, fip=null;
	while( tokens.hasMoreTokens() )
	{
		String str = tokens.nextToken();
		if( str.equals("SUBID") )
		{
			subSysId = tokens.nextToken();	
		}
		if( str.equals("TMSTAMP") )
		{
			timeStamp = tokens.nextToken();
		}
		if( str.equals("IPADDR") )
		{
			fip = tokens.nextToken();
		}
		if( timeStamp != null && subSysId != null && fip != null )
		{
			ArrayList arr = new ArrayList(2);
			arr.add( subSysId );
			arr.add( timeStamp );
			tobeFilled.put( fip , arr );
			subSysId = null;
			timeStamp = null;
			fip = null;
		}
	}
	if (logger.isDebugEnabled()) {
	logger.debug(" FIPDetails hashmap = "+tobeFilled);
	}
	return tobeFilled.entrySet().iterator();

}

protected void initiateShutdown(){
	logger.error("Initiating shutdown.....");

	try{
		this.shutdown();
	}catch(Exception e){
		logger.error("Initiating shutdown", e);
	}finally{
		logger.error("Calling System.exit");
		System.exit(1);
	}
}

private boolean isNegotiationReqd(){

	//Check whether the control manager is NULL
	if(this.controlMgr == null){
		if(logger.isInfoEnabled()){
			logger.info("Control Manager is NULL.  So returning FALSE for Negotiation with Peer");
		}	
		return false;
	}

	//If the mode FT or HA, then do the role resolution.
	if(AseModes.isFtMode(self.getMode())){
		if(logger.isInfoEnabled()){
			logger.info("Mode is FT. So returning TRUE for Negotiation with Peer");
		}	
		return true;
	}

	if(self.getMode() == AseModes.HA){
		if(logger.isInfoEnabled()){
			logger.info("Mode is HA So returning TRUE for Negotiation with Peer");
		}	
		return true;
	}

	if(logger.isInfoEnabled()){
		logger.info("Mode is Not FT. So returning FALSE for Negotiation with Peer");
	}

	return false;
}

private void addHistory(String event) {
	if (this.history == null) {
		this.history = new ArrayList();
	}
	this.history.add(event);
}

private void clearHistory() {
	this.history = null;
}

public static final String SHOW = "show";
public static final String CLEAR = "clear";

/* (non-Javadoc)
 * @see com.baypackets.ase.control.PeerStateChangeSource#handlePeerStateChangeException(com.baypackets.ase.control.PeerStateChangeException)
 */
public void handlePeerStateChangeException(PeerStateChangeListener listener, PeerStateChangeEvent event, PeerStateChangeException ex) {

	if(ex.getErrorCode() == PeerStateChangeException.DATA_CHANNEL_NOT_CONNECTED){
		logger.error("Datachannel not connected :" + event);
		AseSubsystem subsys = event.getSubsystem();
		String fip = event.getEventData();
		AsePartition partition;
		synchronized( this.partitionTable.synchronizationObj )
		{
			partition = this.partitionTable.getPartition(fip);
		}

		//Add this destination to the cluster thread.
	//	this.dcThread.addDestination(subsys.getId(), partition);
	}
}

/**
 * Overloaded form to disable bulk replication
 * @param partition
 * @param subsysId
 * @param doReplication
 */
public void generatePeerReadyReplicateEvent(AsePartition partition, String subsysId, boolean doReplication){
	if(logger.isInfoEnabled()){
		logger.info("generatePeerReadyReplicateEvent for " + partition.getFloatingIp());
	}

	//If mode is Not FT, do not generate any replication event
	if(!AseModes.isFtMode(self.getMode())){
		if(logger.isInfoEnabled()){
			logger.info("Self is not in FT Mode, So not generating any Replication Event:" + partition);
		}
		return;	
	}

	if(partition.getRole(self.getId()) != AseRoles.ACTIVE){
		if(logger.isInfoEnabled()){
			logger.info("Self is not active in partition: " + partition.getFloatingIp());
		}
		return;
	}
	if(partition.isMember(subsysId, AseRoles.STANDBY)){
		if(logger.isInfoEnabled()){
			logger.info("Peer is a replication destination for the partition :" + partition.getFloatingIp());
		}

//		AseSubsystem subsys = this.ccp.getSubsystem(subsysId);
//		PeerStateChangeEvent event = new PeerStateChangeEvent(subsys, 
//				PeerStateChangeEvent.PR_READY_START_REPL, 
//				partition.getFloatingIp(), this);
//		event.setDoReplication(doReplication);
//		this.controlMgr.generatePeerChangeEvent(event);
	}
}



public void generatePeerReadyReplicateEvent(AsePartition partition, String subsysId){
	if(logger.isInfoEnabled()){
		logger.info("generatePeerReadyReplicateEvent for " + partition.getFloatingIp());
	}

	//If mode is Not FT, do not generate any replication event
	if(!AseModes.isFtMode(self.getMode())){
		if(logger.isInfoEnabled()){
			logger.info("Self is not in FT Mode, So not generating any Replication Event:" + partition);
		}
		return;	
	}

	if(partition.getRole(self.getId()) != AseRoles.ACTIVE){
		if(logger.isInfoEnabled()){
			logger.info("Self is not active in partition: " + partition.getFloatingIp());
		}
		return;
	}
	if(partition.isMember(subsysId, AseRoles.STANDBY)){
		if(logger.isInfoEnabled()){
			logger.info("Peer is a replication destination for the partition :" + partition.getFloatingIp());
		}

//		AseSubsystem subsys = this.ccp.getSubsystem(subsysId);
//		PeerStateChangeEvent event = new PeerStateChangeEvent(subsys, 
//				PeerStateChangeEvent.PR_READY_START_REPL, 
//				partition.getFloatingIp(), this);
//		this.controlMgr.generatePeerChangeEvent(event);
	}
}

// As ThreadOwner
public int threadExpired(MonitoredThread thread) {
	logger.error(thread.getName() + " expired");

	// Print the stack trace
	StackDumpLogger.logStackTraces();

	return ThreadOwner.SYSTEM_RESTART;
}

// If the fip is of ipv6 format and is not enclosed in square brackets
// then this method encloses the fip in those brackets
public static String adjustFIPFormat( String fip )
{
	/*if( fip.lastIndexOf(":") == -1 )
		return fip;
	if( !fip.startsWith("[") && !fip.endsWith("]") )
		return "["+fip+"]";
	return fip;*/
	String[] temp_Fip = fip.split(AseStrings.COMMA);
	StringBuffer sb = new StringBuffer();
	for(int i=0;i<temp_Fip.length;i++){
		fip = temp_Fip[i].trim();
		if( fip.lastIndexOf(":") == -1 ){
			sb.append(fip);
		}else if( !fip.startsWith(AseStrings.SQUARE_BRACKET_OPEN) && !fip.endsWith(AseStrings.SQUARE_BRACKET_CLOSE) ){
			sb.append(AseStrings.SQUARE_BRACKET_OPEN+fip+AseStrings.SQUARE_BRACKET_CLOSE);
		}else{
			sb.append(fip);
		}
		if(i+1 != temp_Fip.length){
			sb.append(AseStrings.COMMA);
		}

	}

	return sb.toString();
}

/*
 * This method returns delta time for detection of failover calculated based on StatndByStatusChecker timeout and retries values.
 */
public static int getDeltaFailoverDetectionTime(){
	return deltaFaileOverDetectionTime;
}

/*
 * This method returns failover detection timestamp.
 */
public static long getDeltaFailoverDetectionTimeStamp(){
	return deltaFaileOverDetectionTimeStamp;
}


/* Method added to set the IPv6 Address in the correct 
 * form by removing the square brackets of the IP Address
 */

private String setFip(String origFip){
	String fip = null;
	int start = origFip.lastIndexOf(AseStrings.SQUARE_BRACKET_CHAR_OPEN);
	int end = origFip.lastIndexOf(AseStrings.SQUARE_BRACKET_CHAR_CLOSE);
	if( start != -1 && end != -1){
		fip = origFip.substring( start+1, end);
	}else{
		fip = origFip;
	}
	return fip;
}
/*public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("Please provide file name and try again. ");
			return;
		}
		try {
			AsePartitionTable partitionTable = new AsePartitionTable("self");
			SimpleRoleResolver roleResolver = 
				(SimpleRoleResolver)RoleResolverFactory.getInstance().getRoleResolver();
			roleResolver.setPartitionTable(partitionTable);
			BufferedReader reader = new BufferedReader(new FileReader(args[0]));

			String line = reader.readLine();			
			while (line != null) {
				System.out.println(line);

				if (line.startsWith("#")) {
					line = reader.readLine();
					continue;
				}

				StringTokenizer tokens = new StringTokenizer(line, ", \t");

				if (!tokens.hasMoreTokens()) {
					line = reader.readLine();
					continue;
				}

				String opName = tokens.nextToken();

				ArrayList values = new ArrayList();
				while (tokens.hasMoreTokens()) {
					values.add(tokens.nextToken());
				}

				if (opName.equals("setTotalSubsystems") && values.size() >= 1) {
					roleResolver.setTotalSubsystems(Integer.parseInt((String)values.get(0)));
				} else if (opName.equals("setLastStandby") && values.size() >= 1) {
					roleResolver.setLastStandby(((String)values.get(0))
							.equals("true") ? true : false);
				} else if (opName.equals("update") && values.size() >= 3) {
					String str = (String)values.get(2);
					short role = -1;
					if (str.equals("active")) {
						role = AseRoles.ACTIVE;
					} else if (str.equals("standby")) {
						role = AseRoles.STANDBY;
					} else if (str.equals("release_standby")) {
						role = AseRoles.RELEASE_STANDBY;
					}
					PartitionInfo info = new PartitionInfo((String)values.get(1), 
							(String)values.get(0), role);
					if (partitionTable.update(info) != null) {
						System.out.println("Update failed: " + info);
					}

				} else if (opName.equals("resolve")) {
					roleResolver.resolveRole();
				} else if (opName.equals("peerUp") && values.size() >= 1) {
				} else if (opName.equals("peerDown") && values.size() >= 1) {
					partitionTable.removeMember((String)values.get(0));
					roleResolver.resolveRole();
				} else if (opName.equals("addSubsystem") && values.size() >= 1) {
					roleResolver.addSubsystem((String)values.get(0));
				} else if (opName.equals("removeSubsystem") && values.size() >= 1) {
					roleResolver.removeSubsystem((String)values.get(0));
				}

				System.out.println(partitionTable.toString());

				line = reader.readLine();
			}
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}

	}*/
	
private class FIPChecker extends MonitoredThread {

	private ThreadOwner _threadOwner = null;
	private String fips = null;
	private int counter = 0;
	private ConfigRepository config = (ConfigRepository) Registry
			.lookup(Constants.NAME_CONFIG_REPOSITORY);

	public FIPChecker(String fip) {
		super("FIPChecker", AseThreadMonitor.getThreadTimeoutTime(),
				(AseTraceService) Registry
						.lookup(Constants.NAME_TRACE_SERVICE));
		this.fips = fip;
	}

	public void run() {
		// Register thread with thread monitor
		try {
			// Set thread state to idle before registering
			this.setThreadState(MonitoredThreadState.Idle);

			_threadMonitor.registerThread(this);
		} catch (ThreadAlreadyRegisteredException exp) {
			logger.error(
					"This thread is already registered with Thread Monitor",
					exp);
		}

		try {
			while (true) {
				String str = config.getValue(Constants.SPLIT_BRAIN_FIP_PLUMB_RETRIES);
				if (counter > Integer.valueOf(str).intValue()) {
					logger.error("FIP is still plumbed after " + counter +  " retries, thus doing nothing");
					return;
				}

				try {
					if (logger.isInfoEnabled()) {
						logger.info("FIPChecker.run(): Checking whether FIP is plumbed or not");
					}
					// Update time in thread monitor
					this.updateTimeStamp();

					// Set thread state to running before calling send
					this.setThreadState(MonitoredThreadState.Running);
					counter++;
					
					String[] fipList = this.fips.split(AseStrings.COMMA);
					for (int i=0; i < fipList.length; i++) {
						String fip = fipList[i];
						if (!checkFIP(fip)){
							logger.error(" FIP ::  "+ fip + "not set on self thus shutdown , retry=" + counter);
							initiateShutdown();
						}
					}
					
					Thread.sleep(1000);
				} catch (Exception e) {
					logger.error(e.toString(), e);
				}
			}
		} finally {
			// Unregister thread with thread monitor
			try {
				_threadMonitor.unregisterThread(this);
			} catch (ThreadNotRegisteredException exp) {
				logger.error(
						"This thread is not registered with Thread Monitor",
						exp);
			}
		}
	}

	// ////////////// ThreadMonitor methods for MessageSender start
	// ///////////////////////

	public void setThreadOwner(ThreadOwner threadOwner) {
		_threadOwner = threadOwner;
	}

	public ThreadOwner getThreadOwner() {
		return _threadOwner;
	}

	// ////////////// ThreadMonitor methods for MessageSender end
	// /////////////////////////
}

	private static Class jmxmpConnectorClass = null;
	static {
		// load class
		try {

			jmxmpConnectorClass = Class
					.forName("javax.management.remote.jmxmp.JMXMPConnector");

			logger.debug("The Jmxmpconnector class loaded is "
					+ jmxmpConnectorClass);

		} catch (ClassNotFoundException e) {
			logger.error("The JMXMPConnector class not found");

		}
	}
	
	private class StandbyStatusChecker extends AseMonitoredThread {

		private AseThreadOwner _threadOwner = null;
		private String ip = null;
		private int port = 0;
		private ObjectName stdMBeanName = null;
		private MBeanServerConnection mbsc = null;
		private JMXConnector jmxc = null;
		private boolean stoppedForStandByStatusChecker = false;
		private int notFirst = 0;
		
		String peerSubystemNm=null;

		public StandbyStatusChecker(String ip, String port,String peerSubsystemName) {
			//Timeout in millisec. This is a timeout time for this thread. 
			super("StandbyStatusChecker", standbyStatusCheckerTimeoutValue);

			logger.error("StandbyStatusChecker constructor called..." + standbyStatusCheckerTimeoutValue + "IP is "+ip +"  port "+ port);
			this.ip = ip;
			this.port = Integer.parseInt(port);
			peerSubystemNm=peerSubsystemName;
		}

		public void run() {
			// Register thread with thread monitor
			try {	
				// Set thread state to idle before registering
				this.setThreadState(THREAD_IDLE);

				//registering this thread
				
				
				byStatusThreadMonitor.registerStandByCheckerThread(this);
			} catch (Exception exp) { 
				logger.error(
						"This thread is already registered with Thread Monitor",
						exp);
			}
			try {
				try {
					// connect and acquire bean

					int i = 0;
					while (true) {
						try {

							this.updateTimeStamp();
							//Initially peerDownFlag will be 0 for first thread.Once first thread acquired connection
							// then this peerDownFlag will be set to 1.
							// If first thread stuck ( sig cable pull out ) then need to call connectAndAcquireBean again
							// for the second thread.It can stuck here as well so making thread state running. 
							if(peerDownFlag == 1){
								this.setThreadState(THREAD_RUNNING);
							}
							connectAndAcquireBean();

							//this flag will be 1 if first thread successfully acquired bean 
							peerDownFlag = 1;
							
							// Set deltaFaileOverDetectionTime as 0 as Mbean acquired again 
							deltaFaileOverDetectionTime=0;
									
							// if second thread successfully acquired the connection then incrementing retries 
							// to its actual value
							if(this.getNotFirst() == 1){
								retries ++;
							}

							break;

						} catch (ConnectException e) {
							i++;
							if (i == 1) {
								logger.error("ConnectException : retrying.."
										+ e.getMessage());
							}
							// sleep 30 secs before next retry
							try {
								Thread.sleep(30000);
							} catch (Exception e1) {
								logger.error("Exception in sleep : retrying.."
										+ e1.getMessage());
							}
						}
					}
				} catch (MalformedObjectNameException e) {
					logger.error("MalformedObjectNameException:",e);
				} catch (SecurityException e) {
					logger.error("SecurityException:",e);
				} catch (IllegalArgumentException e) {
					logger.error("IllegalArgumentException:",e);
				} catch (IOException e) {
					logger.error("IOException:",e);
				} catch (NoSuchMethodException e) {
					logger.error("NoSuchMethodException:",e);
				} catch (InstantiationException e) {
					logger.error("InstantiationException:",e);
				} catch (IllegalAccessException e) {
					logger.error("IllegalAccessException:",e);
				} catch (InvocationTargetException e) {
					logger.error("InvocationTargetException:",e);
				}
				Integer statusInt = 0;

				// invoke method on bean
				// stoppedForStandByStatusChecker : check for successfully stop thread
				while (!stoppedForStandByStatusChecker){
					// making state running so that this will be eligible for monitoring
					this.setThreadState(THREAD_RUNNING);
					try{
						statusInt = (Integer) mbsc.invoke(stdMBeanName, "status", null,null);
					}catch(IOException e){
						String exceptionMessage = "local class name incompatible with stream class name";
						if(e.getMessage().contains(exceptionMessage)){
							statusInt = 1;
							logger.error("Ignoring IOException "+ e.getMessage());
							}
						
						else{
							throw e;
						}
					}

					if (statusInt != 1) {
						logger.error("Broken Connection " + new Date());
						break;
					}

					// Update time in thread monitor
					this.updateTimeStamp();

					//making it idle to overcome the possiblity of sleeping thread more then the specify time.
					this.setThreadState(THREAD_IDLE);
					Thread.sleep(200);

				}
			}catch (EOFException eof) {
				// if check : for the first stuck thread ( which will timeout as per tcp socket timeout )
				// that should not go in catch. Changing stoppedForStandByStatusChecker in expired method.
				if(!stoppedForStandByStatusChecker){

					//In case of actual kill / shutdown, updating timestamp so that monitor thread 
					// should not mark it expired.
					this.updateTimeStamp();
					// In case of actual kill / shutdown, we dont want monitor thread to call thread expired 
					byStatusThreadMonitor.unregisterStandByCheckerThread(this);
					logger.error("EOFException Occuured : CAS is not running :: "+ new Date() + eof.getMessage());

					Set<String> activeInstances=null;
					try {
						activeInstances = redisWrapper.getSetOperations()
								.getAllMemberFrmSet(ClusterManager.ACTIVE_CAS_INSTANCES);
					 RedisAlarmHandler.redisIsAccessible(self.getId());

					} catch (RedisLettuceConnectionException e) {
						logger.error("exception while writing headrtbeat in redis "
								+ e);

						RedisAlarmHandler.redisNotAccessible(self.getId());
					} catch (RedisLettuceCommandTimeoutException e) {
						logger.error("exception while writing headrtbeat in redis "
								+ e);
						RedisAlarmHandler.redisNotAccessible(self.getId());
					} catch (Exception e) {
						logger.error("Expetion thrown "+e);
					}
					
					if (activeInstances != null
							&& activeInstances.contains(peerSubystemNm)) {

                        logger.error("ACTIVE CAS is down:: invoke peerdown ");
						
						
					}else{
						
						 logger.error("Standby CAS is down:: invoke peerdown ");
					}
					
					String peerIpAddress = AseUtils.getIPAddress(configRep.getValue(Constants.OID_PEER_SUBSYS_IP));

					logger.error("Peer CAS down IP "+ peerIpAddress);

					
					
					AseSubsystem subsystem = new AseSubsystem(
							peerSubystemNm);
					subsystem.setHost(peerIpAddress);
					deltaFaileOverDetectionTimeStamp = System
							.currentTimeMillis();
					controlMgr.peerDown(subsystem);

				}
			}catch (IOException iox) {
				if(!stoppedForStandByStatusChecker){
					this.updateTimeStamp();
					byStatusThreadMonitor.unregisterStandByCheckerThread(this);
					logger.error("IOException Occuured : CAS is not running :: " + new Date() + iox.getMessage());

					
					Set<String> activeInstances=null;
					try {
						activeInstances = redisWrapper.getSetOperations()
								.getAllMemberFrmSet(ClusterManager.ACTIVE_CAS_INSTANCES);
						RedisAlarmHandler.redisIsAccessible(self.getId());

					} catch (RedisLettuceConnectionException e) {
						logger.error("exception while writing headrtbeat in redis " + e);

						RedisAlarmHandler.redisNotAccessible(self.getId());
					} catch (RedisLettuceCommandTimeoutException e) {
						logger.error("exception while writing headrtbeat in redis " + e);
						RedisAlarmHandler.redisNotAccessible(self.getId());
					} catch (Exception e) {
						logger.error("Exception thrown "+e);
					}
					
					if (activeInstances != null
							&& activeInstances.contains(peerSubystemNm)) {

                        logger.error("ACTIVE CAS is down:: invoke peerdown ");
						
						
					}else{
						
						 logger.error("Standby CAS is down:: invoke peerdown ");
					}
					
					String peerIpAddress = AseUtils.getIPAddress(configRep.getValue(Constants.OID_PEER_SUBSYS_IP));

					logger.error("Peer CAS down IP "+ peerIpAddress);

					
					
					AseSubsystem subsystem = new AseSubsystem(
							peerSubystemNm);
					subsystem.setHost(peerIpAddress);
					deltaFaileOverDetectionTimeStamp = System
							.currentTimeMillis();
					controlMgr.peerDown(subsystem);
				}
			} catch (InterruptedException ie) {
				this.updateTimeStamp();
				byStatusThreadMonitor.unregisterStandByCheckerThread(this);
				logger.error( "Exception occured "+ ie.getMessage());
			}catch (Exception e) {
				this.updateTimeStamp();
				byStatusThreadMonitor.unregisterStandByCheckerThread(this);
				logger.error("Exception occured "	+  new Date() + e.getMessage());
			} finally {

				if (jmxc != null) {
					try {
						jmxc.close();
					} catch (IOException iox) {
						logger.error("Unable to close JMX connection"	+ iox.getMessage());
					}
				}

				try {
					logger.error("StandbyStatusChecker finally block....");

					// doing for safety.
					byStatusThreadMonitor.unregisterStandByCheckerThread(this);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Unable to unregister Thread"	+ e.getMessage());
				}
			}
		}

		private void connectAndAcquireBean() throws IOException,
		SecurityException, NoSuchMethodException, IllegalArgumentException,
		InstantiationException, IllegalAccessException,
		InvocationTargetException, MalformedObjectNameException {

			JMXServiceURL url = new JMXServiceURL("jmxmp", ip, port);
			Class[] paramTypes = { JMXServiceURL.class };
			Constructor cons = jmxmpConnectorClass.getConstructor(paramTypes);

			Object[] args = { url };
			Object theObject = cons.newInstance(args);
			jmxc = (JMXConnector) theObject;
			this.updateTimeStamp();
			jmxc.connect();
			this.updateTimeStamp();
			logger.debug("JMXConnector ========== > " + jmxc);

			mbsc = jmxc.getMBeanServerConnection();
			logger.debug("MBeanServerConnection========== > "
					+ mbsc);
			// fetch domain
			String domain = mbsc.getDefaultDomain();

			stdMBeanName = new ObjectName(
					domain
					+ ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");

		}

		@Override
		public AseThreadOwner getStandByThreadOwner() {
			// TODO Auto-generated method stub
			return _threadOwner;
		}

		public void setStandByThreadOwner(AseThreadOwner aseThreadOwner) {
			// TODO Auto-generated method stub
			_threadOwner = aseThreadOwner;
		}

		public void setNotFirst(int notFirst) {
			this.notFirst = notFirst;
		}

		public int getNotFirst() {
			return notFirst;
		}
	}
	/**
	 * implements rolechanges listener on self to update OID's in middlw of role change events.
	 */
	@Override
	public void roleChanged(String clusterId, PartitionInfo partitionInfo) {
		if (logger.isDebugEnabled()) {
			logger.debug("roleChanged(): CLusterManager Subsystem role in cluster has been changed ");
		}
		short role = partitionInfo.getRole();

		if (logger.isDebugEnabled()) {
			logger.debug("roleChanged(): Subsystem role in cluster has been changed to: "
					+ AseRoles.getString(role));
		}

		if (role == AseRoles.ACTIVE) {
			ConfigurationDetail detail = new ConfigurationDetail(
					Constants.OID_CURRENT_ROLE, "Active");
			try {
				this.agent.modifyCfgParam(detail);
			} catch (UnableToModifyConfigurationParamsException e) {
				logger.error("error updating Active Role with agent::"+e.getMessage());
				logger.warn(e);
			}
			ConfigRepository configDb = BaseContext.getConfigRepository();
			configDb.setValue(Constants.OID_CURRENT_ROLE, "Active");
		}else if(role == AseRoles.STANDBY){
			ConfigurationDetail detail = new ConfigurationDetail(Constants.OID_CURRENT_ROLE, "Standby");
			try {
				this.agent.modifyCfgParam(detail);
			} catch (UnableToModifyConfigurationParamsException e) {
				logger.error("error updating Standby Role with agent::"+e.getMessage());
				logger.warn(e);
			}
			ConfigRepository configDb = BaseContext.getConfigRepository();
			configDb.setValue(Constants.OID_CURRENT_ROLE,"Standby");
		}
		logger.error("roleChanged(): Updated current role in OID ");
		
		
	}
	
	@Override
	public int standByThreadExpired(AseMonitoredThread monitoredThread) {
		logger.error("Inside StandbyStatusChecker Thread Exp...");
		
		StandbyStatusChecker checker = (StandbyStatusChecker)monitoredThread;
		// we want to stop this thread and we are about to create new thread or call peer down as per retries.
		// Once expired thread timed out then it will not go into catch block of StandbyStatusChecker Class.
		checker.stoppedForStandByStatusChecker = true;
		
		//Stack dump is not required here.As this is for StandByStatus checker only.
		//StackDumpLogger.logStackTraces();
		
		// if retires=0 then call peer down.
		// Not assuming this value should be negative any time.
		if(retries != 0){
			
			logger.error("Inside StandbyStatusChecker Thread Exp..creating new thread");
			StandbyStatusChecker checkerNew = new StandbyStatusChecker(peerIpAddress,(String) configRep.getValue(Constants.PROP_PEER_JMX_PORT),(String) configRep.getValue(Constants.PEER_SUBSYSTEM_NAME));
			checkerNew.setStandByThreadOwner(this);
			checkerNew.stoppedForStandByStatusChecker=false;
			
			// This is not first thread.This check is required so that if this thread successfully 
			// acquire bean again then need to increment retires count
			checkerNew.setNotFirst(1);
			// decrementing as we are re-trying 
			retries --;
			deltaFaileOverDetectionTime+=standbyStatusCheckerTimeoutValue;
			checkerNew.start();
			
		}else{
			logger.error("Inside StandbyStatusChecker Thread Exp : CAS is not running :: "+ new Date());

			AseSubsystem subsystem = null ;
//			//subsystem.setSignalIp(selfIpAddress);
//			Iterator iterator =  ChannelProviderFactory.getInstance().getControlChannelProvider().getAllSubsystems();
//			while(iterator.hasNext()){
//				subsystem = (AseSubsystem) iterator.next();
//				if(peerIpAddress.equals(subsystem.getSignalIp())){
//					logger.error("Subsys found::"+subsystem);
//					break;
//				}//end if
//				subsystem=null;					
//			}//end while
			if(subsystem!=null){
				deltaFaileOverDetectionTime=(maxRetries+1)*standbyStatusCheckerTimeoutValue;
				deltaFaileOverDetectionTimeStamp=System.currentTimeMillis();
				controlMgr.peerDown(subsystem);
			}	
		}
		// return will not impact anything.This is for future consideration.
		return 0;
	}
	
	/**
	 * This method is used toa ss standby to partition when standby resolves roleor comes up
	 * @param subsystemId
	 */
	public void standbySubsystemUp(String messageUp) {

		
		
		String peerSubName=(String) configRep.getValue(Constants.PEER_SUBSYSTEM_NAME);
		
		String expectedmsg=peerSubName+"_UP";
		
		logger.error("Inside standbySubsystemUp for "+ peerSubName +" Expected msg is "+ expectedmsg +" Msg Received is "+messageUp); 
		
		if (messageUp != null && messageUp.equalsIgnoreCase(expectedmsg)) {

			logger.error("Inside standbySubsystemUp standby is up ");

			ArrayList partitions = null;
			String nodeDown = null;
			synchronized (this.partitionTable.synchronizationObj) {
				partitions = this.partitionTable.getPartitions(self.getId(),
						AseRoles.ACTIVE);

				for (int i = 0; i < partitions.size(); i++) {
					AsePartition partition = (AsePartition) partitions.get(i);
					if (!partition.hasMember(AseRoles.STANDBY)) {

						logger.error("current partiton donot have standby member add it  ");
						partition.setMember(peerSubName, AseRoles.STANDBY);

						if (logger.isInfoEnabled()) {
							logger.info("STANDBY added to parition is  "
									+ peerSubName);
						}
						
						//Send an alarm to notify the peer connection restored.
						if(!peerSubName.equals("")){
							String alarmMsg = " [Connection restored with peer ::" + peerSubName + "]";
							logger.error("CAS-ALARM: Peer Connection Restored; peer [" +peerSubName+ "]");
							try{
								this.alarmService.sendAlarm(Constants.ALARM_PEER_CONNECTION_RESTORED, alarmMsg );	
							}catch (Exception e) {
								logger.error("Exception while Raising alarm ",e);
							}
							
						}
					}
				}
			}

			logger.error("add standby status checker for checking standby health ");
			// This is a StandbyStatusChecker thread monitor thread.
			byStatusThreadMonitor = new AseStandByStatusThreadMonitor();
			byStatusThreadMonitor.initialize();

			// This thread is used to check JMX connection of its peer.
			// If any connection issue occurred, this will call peer down
			StandbyStatusChecker checker = new StandbyStatusChecker(
					peerIpAddress,
					(String) configRep.getValue(Constants.PROP_PEER_JMX_PORT),
					peerSubName);
			checker.setStandByThreadOwner(this);
			// this indicates that this is first thread.
			checker.setNotFirst(0);
			checker.start();
		}
	}
	
	
	/**
	 * This method is called by SystemInfoHandler of Control manager to show updated partition table
	 * this is needed in case Active CAS not aware of standby is down 
	 */
	public void updatePartition(){
		
		if (logger.isInfoEnabled()) {
			logger.info("updatePartition() ");
		}
		ArrayList partitions=null;
		JMXConnector jmxc = null;
		
		
		String peerSubName=(String) configRep.getValue(Constants.PEER_SUBSYSTEM_NAME);
		try{
			
			String jmxPort=(String) configRep.getValue(Constants.PROP_PEER_JMX_PORT);
			JMXServiceURL url = new JMXServiceURL("jmxmp", peerIpAddress, Integer.parseInt(jmxPort));
			Class[] paramTypes = { JMXServiceURL.class };
			Constructor cons = jmxmpConnectorClass.getConstructor(paramTypes);

			Object[] args = { url };
			Object theObject = cons.newInstance(args);
			jmxc = (JMXConnector) theObject;
		
			jmxc.connect();
			
			
			synchronized (this.partitionTable.synchronizationObj) {
				 partitions = this.partitionTable.getPartitions(self.getId(),
						AseRoles.ACTIVE);
			if(partitions!=null){	
				for (int i = 0; i < partitions.size(); i++) {
					AsePartition partition = (AsePartition) partitions.get(i);
					if (!partition.hasMember(AseRoles.STANDBY)) {
						partition.setMember(peerSubName, AseRoles.STANDBY);

						if (logger.isInfoEnabled()) {
							logger.info("STANDBY updated to partition is  "
									+ peerSubName);
						}
					}
				}
			}
			}

		}catch (Exception e) {
			logger.error("standby not available Exception" + e.getMessage());
			
			synchronized (this.partitionTable.synchronizationObj) {
				 partitions = this.partitionTable.getPartitions(self.getId(),
						AseRoles.ACTIVE);
				if(partitions!=null){		
				for (int i = 0; i < partitions.size(); i++) {
					AsePartition partition = (AsePartition) partitions.get(i);
					if (partition.hasMember(AseRoles.STANDBY)) {
						this.partitionTable.removeMember(peerSubsystemName);

						if (logger.isInfoEnabled()) {
							logger.info("STANDBY unset from  parition is  "
									+ peerSubName);
						}
					}
				}
			}
			}
		}
		if (jmxc != null) {
			try {
				jmxc.close();
			} catch (IOException iox) {
				logger.error("Unable to close JMX connection"	+ iox.getMessage());
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("updatePartition() leaving with "+partitionTable);
		}
	
	}
	
}
