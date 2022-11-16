package com.baypackets.ase.control;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import RSIEmsTypes.ConfigurationDetail;

import com.agnity.redis.client.RedisWrapper;
import com.agnity.redis.client.utility.RedisClientOptions;
import com.agnity.redis.connection.RedisConnectionInfo;
import com.agnity.redis.policy.RedisConnectionPolicy;
import com.baypackets.ase.channel.AseSubsystem;
import com.baypackets.ase.channel.ChannelManager;
//import com.baypackets.ase.channel.ChannelProviderFactory;
//import com.baypackets.ase.channel.ControlChannelProvider;
import com.baypackets.ase.channel.PeerMessage;
import com.baypackets.ase.common.AgentDelegate;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.AseThreadMonitor;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StackDumpLogger;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.ParameterName;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;
import com.baypackets.emsagent.EmsAgent;
import com.baypackets.emsliteagent.EmsLiteAgent;
import com.baypackets.emsliteagent.EmsLiteConfigurationDetail;

public class ControlManager implements ChannelManager, MComponent, ThreadOwner, PeerStateChangeSource
{
	private static final Logger logger = Logger.getLogger(ControlManager.class);
	private static final AseSubsystem ALL_SUBSYS = new AseSubsystem("ALL");
	public static final String PROP_CAS_VERSION = Constants.PROP_CAS_VERSION;

//	private ControlChannelProvider ccp;
	private AseSubsystem self;
	private AsePartitionTable partitionTable;
	private HashMap peerStateListeners = new HashMap();
	private HashMap controlMsgListeners = new HashMap();
	
	private ThreadMonitor threadMonitor = null;
//	private ReceiverThread receiverThread = null;
	private boolean stopFlag = false;
	// Holds system names
	private ArrayList systemIds = new ArrayList();

	private EmsAgent emsAgent = null;
	private EmsLiteAgent emslAgent = null;
	private AgentDelegate agent;
	
	AseRedisHearbeatUpdater hearbeatUpdater =null;

	private Map<String,Boolean> subsystemDown = new HashMap<String, Boolean>();
	/** 
	 * Pass the ChannelManager handle to Channel
	 * ccp = new JgroupsCCProvider (this);
	 * ccp.registerChannelManager (this);
	 * The ControlManager spawns a receiver thread
	 * that blocks on the channel's receive message
	 * on getting the message the control manager 
	 * then makes sense out of that message and then
	 * in turn sends out an PeerStateChangeEvent if
	 * required to any listeners. One such listener 
	 * could be the replication manager itself. 
	 * Besides this the ControlManager is also an 
	 * MComponent getting the configuration information
	 * like FT or HA etc. 
	 * */
	public ControlManager(){
	}

	/**
	 * Use this when interested only in state change of the
	 * named peer */
	public void registerPeerStateChangeListener 
		(PeerStateChangeListener pscl, AseSubsystem subsystem){
		
		if(pscl == null || subsystem == null)
			return;
		
		ArrayList list = (ArrayList)this.peerStateListeners.get(subsystem.getId());
		if(list == null){
			list = new ArrayList();
			this.peerStateListeners.put(subsystem.getId(),list);
		}
		if(!list.contains(pscl)){
			list.add(pscl);
		}
	}
	
	/**
	 * All state notifications are sent out to the registered 
	 * listener that are registered via this interface.
	 */
	public void registerPeerStateChangeListener 
		(PeerStateChangeListener pscl){
		this.registerPeerStateChangeListener(pscl, ALL_SUBSYS);
	}
	
//	public void registerMessageListener(short messageType, ControlMessageListener listener){
//		if(listener == null)
//			return;
//		
//		Short type = new Short(messageType);
//		ArrayList list = (ArrayList)this.controlMsgListeners.get(type);
//		if(list == null){
//			list = new ArrayList();
//			this.controlMsgListeners.put(type,list);
//		}
//		if(!list.contains(listener)){
//			list.add(listener);
//		}
//	}

	/**
	 * Can be invoked by the replication manager or other 
	 * such component which is using a different channel and
	 * faces some issue with using that channel. In this case
	 * the ControlManager should verify the state of subsystem
	 * and notify the caller via the listener mechanism of the
	 * action to take.
	 * */
	public void verifyPeerState (AseSubsystem subsystem)
	{
	}

	/**
	 * Invoked by the data channel owner indicating the fact
	 * that I am connected to the named subsystem, in case 
	 * the semantics is a group/cluster then the subsystem 
	 * reference can be null and control manager shall assume
	 * that the data channel is not "connected"
	 * */
	public void dataChannelConnected (AseSubsystem subsystem)
	{
	}

	public void dataChannelDisConnected (AseSubsystem subsystem)
	{
	}

	public void dataChannelReady (AseSubsystem subsystem)
	{
	}

	public void dataChannelNotReady (AseSubsystem subsystem)
	{
	}
	
//	public ControlChannelProvider getControlChannel(){
//		return this.ccp;
//	}
	
	public AseSubsystem getSelfInfo(){
		return self;
	}
	
	public AsePartitionTable getPartitionTable() {
		return partitionTable;
	}
	
	/* (non-Javadoc)
	 * @see com.baypackets.ase.replication.ChannelManager#messageDeliveryFailed(com.baypackets.ase.replication.PeerMessage, com.baypackets.ase.replication.AseSubsystem[])
	 */
	public void messageDeliveryFailed(
		PeerMessage msg,
		AseSubsystem[] subsystem) {
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.replication.ChannelManager#peerDown(com.baypackets.ase.replication.AseSubsystem)
	 */
	public void peerDown(AseSubsystem subsystem) {
		synchronized (this) {
			//Unknown subsystem. Simply return from here.
			String systemIP = subsystem.getSignalIp();
			if ((systemIP == null) || (systemIP != null && subsystemDown.get(systemIP)!=null && subsystemDown.get(systemIP).equals(Boolean.valueOf(false)))){ 
				logger.error("Peer Down " + systemIP);
				subsystemDown.put(systemIP, Boolean.valueOf(true));
				PeerStateChangeEvent event = new PeerStateChangeEvent(subsystem, PeerStateChangeEvent.PR_DOWN_CLOSE_CONN, "", this);
				this.generatePeerChangeEvent(event);
			}else{
				logger.error("Peer Already Down " + systemIP);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.baypackets.ase.replication.ChannelManager#peerUp(com.baypackets.ase.replication.AseSubsystem)
	 */
	public void peerUp(AseSubsystem subsystem) {
		subsystemDown.put(subsystem.getSignalIp(), Boolean.valueOf(false));
		logger.error("Peer Up " + subsystem.getSignalIp());
		PeerStateChangeEvent event = new PeerStateChangeEvent(subsystem, PeerStateChangeEvent.PR_UP_OPEN_CONN, "", this);
		this.generatePeerChangeEvent(event);
	}
	
	void generatePeerChangeEvent(PeerStateChangeEvent event){
		if(logger.isInfoEnabled()){
			logger.info("generatePeerChangeEvent for Subsystem:" +event.getSubsystem());
		}
		AseSubsystem subsystem = event.getSubsystem();
		String subsysId = subsystem.getId() == null ? "" : subsystem.getId();
		ArrayList specificList = (ArrayList)this.peerStateListeners.get(subsysId);
		ArrayList allList = (ArrayList)this.peerStateListeners.get(ALL_SUBSYS.getId());
		
		//Send to the listeners that are specific to this subsystems.
		for(int i=0; specificList != null && i<specificList.size();i++){
			PeerStateChangeListener listener = (PeerStateChangeListener) specificList.get(i);
			try{
				listener.handleEvent(event);
			}catch(PeerStateChangeException e){
				if(event.getSource() != null){
					((PeerStateChangeSource)event.getSource()).handlePeerStateChangeException(listener, event, e);	
				}		
			}
			
		}
		
		//Send to the listeners that are registered for all the subsystems.
		for(int i=0; allList != null && i<allList.size();i++){
			PeerStateChangeListener listener = (PeerStateChangeListener) allList.get(i);
			if(specificList != null && specificList.contains(listener))
				continue;
			try{
				listener.handleEvent(event);
			}catch(PeerStateChangeException e){
				if(event.getSource() != null){
					((PeerStateChangeSource)event.getSource()).handlePeerStateChangeException(listener, event, e);	
				}		
			}
		}
	}
	
//	protected void deliverControlMessage(PeerMessage message){
//		if(logger.isInfoEnabled()){
//			logger.info("Received control Message :"+message);
//		}
//		Short type = new Short(message.getType());
//		ArrayList listeners = (ArrayList)this.controlMsgListeners.get(type);
//		for(int i=0; listeners != null && i<listeners.size();i++){
//			ControlMessageListener listener = (ControlMessageListener) listeners.get(i);
//			listener.handleControlMessage(message);
//		}
//	}
	
	public void initialize() throws Exception{

		if (logger.isDebugEnabled()) {
			logger.debug("initialize control manager");
		}
		
		//createRedisWrapper();

		//Construct the self information
		this.agent = (AgentDelegate)Registry.lookup(Constants.NAME_AGENT_DELEGATE);
		ConfigRepository rep = BaseContext.getConfigRepository();
		this.self = new AseSubsystem(rep.getValue(Constants.OID_SUBSYSTEM_NAME));
		//this.self.setHost(rep.getValue(Constants.OID_BIND_ADDRESS));
		 this.self.setHost(AseUtils.getIPAddress(rep.getValue(Constants.OID_MANAGEMENT_ADDRESS)));
                this.self.setSignalIp(AseUtils.getIPAddress(rep.getValue(Constants.DUAL_LAN_SIGNAL_IP)));
                this.self.setConnected(true);
	
		boolean useSeperateVlan=false;
		String enableSepVlanFlag = rep.getValue(Constants.SEPERATE_NETWORK_REPL_FLAG);
		if(logger.isDebugEnabled()){
				logger.debug("Got seperate Network for replication flag as::<"+enableSepVlanFlag+">");
		}
		if(enableSepVlanFlag!=null && !(enableSepVlanFlag.trim().isEmpty()) ){
			try{
				useSeperateVlan = Boolean.parseBoolean(enableSepVlanFlag);
			}catch(Exception ex){
				logger.error("SEPERATE_NETWORK_REPL_FLAG is not boolean::<"+enableSepVlanFlag+">; using default as <false>");
				useSeperateVlan= false;
			}
		}
		this.self.setSeperateVlanEnabled(useSeperateVlan);
		if(useSeperateVlan){
			this.self.setSeperateVlanIp(rep.getValue(Constants.SEPERATE_NETWORK_REPL_SELFIP));
		}
		// @Siddharth: Fill the oids if the system is FT or N+K
		emsAgent = BaseContext.getAgent();
		if(logger.isDebugEnabled()){

		logger.debug(" self.getMode() = "+self.getMode());
		}
		//BugFixing BPInd16194

		
		String modeStr = rep.getValue(Constants.OID_SUBSYS_MODE);
		modeStr = (modeStr == null) ? "" : modeStr.trim();
		short mode = AseModes.NON_FT;
		try{
			mode = Short.parseShort(modeStr); 
		}catch(NumberFormatException nfe){}
		this.self.setMode(mode);

		String strVersion = rep.getValue(PROP_CAS_VERSION);
		strVersion = (strVersion == null) ? "" : strVersion;
		self.setVersion(strVersion);

		if((self.getMode() != AseModes.NON_FT)&&(emsAgent!=null))
		{
 			int []    llist = emsAgent.getBpSubSystem().getbayMgrAgentSession().getComponentsInSystem(0, RSIEmsTypes.ComponentSubType.Subsystem_RsiAse);

			String selfIp = AseUtils.getIPAddress(rep.getValue(Constants.OID_BIND_ADDRESS));
			String peerIps = "",  systemNames = ""; 
			if(logger.isDebugEnabled()){

			logger.debug("List got, length = "+llist.length);	
			}	
			for(int ii=0;ii<llist.length;ii++)
			{
			logger.debug("List eelement = "+llist[ii]);	
				ConfigurationDetail dtl = emsAgent.getBpSubSystem().getbayMgrAgentSession().getConfigParam( llist[ii] , Constants.OID_BIND_ADDRESS);
				
				
				if( !selfIp.equals(dtl.paramValue) )
					peerIps  = peerIps + dtl.paramValue + ",";
				
				dtl = emsAgent.getBpSubSystem().getbayMgrAgentSession().getConfigParam( llist[ii] , Constants.OID_SUBSYSTEM_NAME);
				if(logger.isDebugEnabled()){

				logger.debug(" paramValue = "+dtl.paramValue);
				}
				systemNames  = systemNames + dtl.paramValue + ",";
			}
			systemNames = systemNames.substring(0,systemNames.length()-1);
			peerIps = peerIps.substring(0,peerIps.length()-1);
			if(logger.isDebugEnabled()){

			logger.debug("Finally, peerIps = "+peerIps+"   systemNames = "+systemNames);
			}
			ConfigurationDetail detail = new ConfigurationDetail( Constants.OID_CLUSTER_MEMBERS , systemNames );
			this.agent.modifyCfgParam(detail);

			detail = new ConfigurationDetail( Constants.DUAL_LAN_PEER_SIGNAL_IP , peerIps );
			this.agent.modifyCfgParam(detail);
		
		}	

		//This if block is added for comaptibilty with emslite agent to update cluster members
		if((self.getMode() != AseModes.NON_FT)&&(emslAgent!=null))
		{
			Integer[]    llist = emslAgent.getComponentsInSystem();

			String selfIp = AseUtils.getIPAddress(rep.getValue(Constants.OID_BIND_ADDRESS));
			String peerIps = "",  systemNames = ""; 
			if(logger.isDebugEnabled()){
				logger.debug("List got, length = "+llist.length);	
			}
			String paramValue = null;
			for(int ii=0;ii<llist.length;ii++)
			{
				if(logger.isDebugEnabled()){
					logger.debug("List eelement = "+llist[ii]);	
				}	
				EmsLiteConfigurationDetail dtl = emslAgent.getConfigurationParam( llist[ii] , Constants.OID_BIND_ADDRESS);
				paramValue = dtl.getParamValue();
				if(logger.isDebugEnabled()){
					logger.debug(" selfIp = "+selfIp+"  paramValue = "+paramValue);
				}
				if( !selfIp.equals(paramValue) )
					peerIps  = peerIps + paramValue + ",";
				if(logger.isDebugEnabled()){
					logger.debug(" peerIps = "+peerIps);
				}
				dtl = emslAgent.getConfigurationParam( llist[ii] , Constants.OID_SUBSYSTEM_NAME);
				paramValue = dtl.getParamValue();
				if(logger.isDebugEnabled()){
					logger.debug(" paramValue = "+paramValue);
				}
				systemNames  = systemNames + paramValue + ",";
			}
			systemNames = systemNames.substring(0,systemNames.length()-1);
			peerIps = peerIps.substring(0,peerIps.length()-1);
			if(logger.isDebugEnabled()){
				logger.debug("Finally, peerIps = "+peerIps+"   systemNames = "+systemNames);
			}

			EmsLiteConfigurationDetail detail = new EmsLiteConfigurationDetail( Constants.OID_CLUSTER_MEMBERS , systemNames );
			this.agent.modifyCfgParam(detail);

			detail = new EmsLiteConfigurationDetail( Constants.DUAL_LAN_PEER_SIGNAL_IP , peerIps );
			this.agent.modifyCfgParam(detail);
		
		}	

		// Oids filled


		String strEmsSubsysId = rep.getValue(ParameterName.SUBSYSTEM_ID);
		int emsSubsysId = Integer.parseInt(strEmsSubsysId);
		this.self.setEmsSubsystemId(emsSubsysId);
		
		//initialize AsePartitionTable
		this.partitionTable = new AsePartitionTable(this.self.getId());
		
		//Register with the TELNET Server for the System information.
		TelnetServer telnetServer = (TelnetServer) Registry.lookup(Constants.NAME_TELNET_SERVER);
		if(telnetServer != null){
			CommandHandler handler = new SystemInfoHandler(); 
			telnetServer.registerHandler(Constants.CMD_INFO, handler);
		}
		
		//In case of Non-FT mode, no need to initialize
		//Control Channel and the Receiver threads. So return from here.
		if(self.getMode() == AseModes.NON_FT){
			return;
		}

		
		String members = rep.getValue(Constants.OID_CLUSTER_MEMBERS);	
		{
		// Block having code to break the OID-> OID_CLUSTER_MEMBERS into individual systemIds
			if(logger.isDebugEnabled()){
				logger.debug(" OID_CLUSTER_MEMBERS : "+members);
			}
			StringTokenizer tokens = new StringTokenizer( members , ",");
			while(tokens.hasMoreTokens())
			{
				systemIds.add( tokens.nextToken() );
			}
		}

		members = (members == null) ? "" : members;
		boolean isDesignatedPrimary = (self.getMode() == AseModes.NON_FT) ||
										members.startsWith(self.getId());
	    
		
		String heartbeatEnabled = BaseContext.getConfigRepository().getValue(
				Constants.PROP_HEARTBEAT_ENABLED);

		if ("1".equals(heartbeatEnabled)) {
			
			if(logger.isDebugEnabled()){
				logger.debug(" start heartbeat : "+heartbeatEnabled);
			}
			hearbeatUpdater = new AseRedisHearbeatUpdater(
					"AseRedisHearbeatUpdater",
					AseThreadMonitor.getThreadTimeoutTime(),
					BaseContext.getTraceService());
			hearbeatUpdater.setThreadOwner(this);
			hearbeatUpdater.start();
		}
		
	}

	
	public void start() throws Exception{
		
		if(this.self.getMode() == AseModes.NON_FT )
			return;
		
		//Start the receiver thread(s).
//		this.receiverThread.setDaemon(true);
//		this.receiverThread.start();
	}
	
	public void shutdown() throws Exception{
		
		if(this.self.getMode() == AseModes.NON_FT)
			return;
		
		//Set the stop flag for the receiver threads		
		this.stopFlag = true;
		
		hearbeatUpdater.stopIt();
		
		//Disconnect the Control channel provider.
	//	this.ccp.disconnect();
	}

	public void changeState(MComponentState state)
			throws UnableToChangeStateException {
		try {
			if(state.getValue() == MComponentState.LOADED){
				this.initialize();
			} else if(state.getValue() == MComponentState.RUNNING){
				this.start();
			} else if(state.getValue() == MComponentState.STOPPED){
				if (logger.isInfoEnabled()) {
					logger.info("State is changed to STOPPED");
				}
				this.shutdown();
			}
		} catch(Exception e){
			logger.error("Changing component state", e);
			throw new UnableToChangeStateException(e.getMessage());
		}
	}

	public void updateConfiguration(Pair[] arg0, OperationType arg1)
		throws UnableToUpdateConfigException {
	}
	
	/* (non-Javadoc)
	 * @see com.baypackets.ase.control.PeerStateChangeSource#handlePeerStateChangeException(com.baypackets.ase.control.PeerStateChangeException)
	 */
	public void handlePeerStateChangeException(PeerStateChangeListener listener, PeerStateChangeEvent event, PeerStateChangeException e) {
		if (logger.isInfoEnabled()) {
			logger.info("handlePeerStateChangeException is called");
		}
	}

	// As ThreadOwner
	public int threadExpired(MonitoredThread thread) {
		logger.error(thread.getName() + " expired");

		// Print the stack trace
		StackDumpLogger.logStackTraces();

		return ThreadOwner.SYSTEM_RESTART;
	}


//	private class ReceiverThread extends MonitoredThread {
//		private ThreadOwner threadOwner = null;
//
//		ReceiverThread(ThreadOwner thOwner) {
//			super("ControlReceiver", AseThreadMonitor.getThreadTimeoutTime(),
//												BaseContext.getTraceService());
//			threadOwner = thOwner;
//		}
//
//		public void run(){
//			// Register thread with thread monitor
//			try {
//				// Set thread state to idle before registering
//				this.setThreadState(MonitoredThreadState.Idle);
//
//				threadMonitor.registerThread(this);
//			} catch(ThreadAlreadyRegisteredException exp) {
//				logger.error("This thread is already registered with Thread Monitor", exp);
//			}
//
//			try {
//				for(;!stopFlag;){
//					this.setThreadState(MonitoredThreadState.Idle);
//
//					try{
//						//Receive will be a blocking method.
//						//So this thread will be blocked on receive anyway.
//						PeerMessage msg = ccp.receive();
//				
//						this.updateTimeStamp();
//						this.setThreadState(MonitoredThreadState.Running);
//
//						//Now deliver the control message to the listener - interested in
//						deliverControlMessage(msg);
//					}catch(Throwable t){
//						logger.error(t.getMessage(), t);
//					}
//				} // for
//			} finally {
//				// Unregister thread with thread monitor
//				try {
//					threadMonitor.unregisterThread(this);
//				} catch(ThreadNotRegisteredException exp) {
//					logger.error("This thread is not registered with Thread Monitor", exp);
//				}
//			}
//		}
//
//		public ThreadOwner getThreadOwner() {
//			return threadOwner;
//		}
//	}// ReceiverThread ends
	
	private String getSystemInformation(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("\r\nName :");
		buffer.append(self.getId());
		buffer.append("\r\nHost :");
		buffer.append(self.getHost());
		//Rajendra: Added for DUAL LAN support
		ConfigRepository rep = BaseContext.getConfigRepository();
		if(rep.getValue(Constants.DUAL_LAN_SIGNAL_IP)!= null)	{
			buffer.append("\r\nSignalIp :");
			buffer.append(self.getSignalIp());
		}
		buffer.append("\r\nVersion :");
		buffer.append(self.getVersion());
		buffer.append("\r\nMode :");
		switch(self.getMode()){
			case AseModes.FT_N_PLUS_K:
				buffer.append("FT (N+K)");
				break;
			case AseModes.FT_ONE_PLUS_ONE:
				buffer.append("FT (1+1)");
				break;
			case AseModes.HA:
				buffer.append("HA");
				break;
			case AseModes.NON_FT:
				buffer.append("Non FT");
				break;
			case AseModes.FT_N_PLUS_LITE:
				buffer.append("FT (N+LITE)");
				break;
			default:
				buffer.append("Unknown");
				break;
		}
		
		if(this.partitionTable.size() == 0) {
			return buffer.toString();
		}
		
//		ClusterManager clusterMgr = (ClusterManager) Registry.lookup(Constants.NAME_CLUSTER_MGR);
//		clusterMgr.updatePartition();
		
		//append partition table
		buffer.append(this.partitionTable.toString());
		
		return buffer.toString();
	}
	
	class SystemInfoHandler implements CommandHandler{
	
		public String execute(
			String command,
			String[] args,
			InputStream in,
			OutputStream out)
			throws CommandFailedException {
			
			return getSystemInformation();
		}

		public String getUsage(String command) {
			return "Prints the name, host, version and the cluster information.";
		}
	}

	@Override
	public void channelUp(String address,AseSubsystem subsystem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void channelDown(String address,AseSubsystem subsystem) {
		// TODO Auto-generated method stub
		
	}
	
//	private void createRedisWrapper() throws Exception{
//		
//        if (logger.isDebugEnabled()) {
//            logger.debug("createRedisWrapper from redis configuration: ");
//        }
//        
//        String filepath = Constants.ASE_HOME + "/" + Constants.FILE_CAS_STARUP_PROPERTIES;
//        Properties redisP= AseUtils.getProperties(filepath);
//        
//        java.security.Security.setProperty("networkaddress.cache.ttl",redisP.getProperty("networkaddress.cache.ttl", "0"));
//		java.security.Security.setProperty("networkaddress.cache.negative.ttl",redisP.getProperty("networkaddress.cache.negative.ttl", "0"));
//
//		RedisConnectionInfo connectionInfo1 = new RedisConnectionInfo("shard1" , RedisConnectionPolicy.FQDN_BASED);
//		connectionInfo1.setFQDN(redisP.getProperty("FQDN","redis-11896.rediscluster.agnity.com"));
//		connectionInfo1.setPort(11896);
//
//		connectionInfo1.setMaxPoolConnections(10);
//		connectionInfo1.setMaxPubSubClientConnections(10);
//		connectionInfo1.setMaxSerializeClientConnections(10);
//
//		RedisClientOptions clientOptions = new RedisClientOptions();
//		clientOptions.setCommandTimeOut(1);
//
//		connectionInfo1.setClientOptions(clientOptions);
//		RedisWrapper redisWrapper = RedisWrapper.getRedisWrapper(connectionInfo1);
//		Registry.bind(Constants.REDIS_WRAPPER, redisWrapper);
//		
//		 if (logger.isDebugEnabled()) {
//	            logger.debug("leaving createRedisWrapper with : "+ redisWrapper);
//	        }
//	}

}

