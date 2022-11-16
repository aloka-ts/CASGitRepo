/*
 * Created on Aug 14, 2004
 */

package com.baypackets.ase.sipconnector;


import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.Vector;

import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipURI;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.channel.AseSubsystem;
import com.baypackets.ase.common.AseBackgroundProcessor;
import com.baypackets.ase.common.AseBaseConnector;
import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.BackgroundProcessListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.logging.debug.SelectiveMessageLoggingManager;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseEngine;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.control.AseModes;
import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.control.ControlManager;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.RoleChangeListener;
import com.baypackets.ase.latency.AseLatencyData;
import com.baypackets.ase.latency.AseLatencyLogger;
import com.baypackets.ase.monitor.CallStatsProcessor;
import com.baypackets.ase.sbb.OutboundGateway;
import com.baypackets.ase.sbb.OutboundGatewaySelector;
import com.baypackets.ase.security.SasSecurityManager;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasMessageCallback;
import com.baypackets.ase.spi.container.SasMessageContext;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.util.AsePing;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.threadpool.Queue;
import com.baypackets.ase.util.threadpool.ThreadPool;
import com.baypackets.ase.util.threadpool.ThreadPoolException;
import com.baypackets.ase.util.threadpool.WorkHandler;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;
import com.dynamicsoft.DsLibs.DsUtil.DsWorkQueue;

/**
 * The <code>AseSipConnector</code> is SIP protocol specific implementation of
 * connector. It handles SIP messages received from container or SIP network
 * and performs SIP specific functionality.
 *
 * @author Neeraj Jain
 */
public class AseSipConnector extends AseBaseConnector implements  RoleChangeListener, WorkHandler, ThreadOwner, SasMessageContext, SasMessageCallback ,BackgroundProcessListener {


	public static final int DEFAULT_THREADPOOL_SIZE = 1;


	 private String ingwMessageQueue = null;
	 
	 private String nsepIngwPriority = null;
	 
	 private String casDomainNames = "";

	/**
	 * Constructor. Constructs dialog manager, connector SIP factory, stack
	 * interface layer and SIP default handler objects. Gets the root container
	 * from the registry and calls another constructor.
	 *
	 */
	public AseSipConnector() {

		this((AseContainer)Registry.lookup(Constants.NAME_ENGINE));
		if (m_l.isDebugEnabled()) {
		m_l.debug( "AseSipConnector():enter");
		m_l.debug( "AseSipConnector():exit");
	}
	}

	/**
	 * Constructor. Constructs dialog manager, connector SIP factory, stack
	 * interface layer and SIP default handler objects. Configures IP address and
	 * port no.
	 * 
	 * @param container root container
	 *
	 */
	public AseSipConnector(AseContainer container) {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "AseSipConnector(AseContainer):enter");
		}

		m_configRepository    = (ConfigRepository)Registry.lookup(
				Constants.NAME_CONFIG_REPOSITORY);
		m_container			  = container;
		m_dialogMgr			  = new AseDialogManager();
		m_subscriptionManager = new AseSipSubscriptionManager();
		m_factory			  = new AseConnectorSipFactory(this, 
				new AseDefaultSipCallIdGenerator(AseSipConnector.getHostAddress(), 0));
		m_b2buaHelper 	  = new AseB2buaHelperImpl(m_factory);
		m_sil				  = new AseStackInterfaceLayer(this,
				m_dialogMgr,
				m_subscriptionManager,
				m_factory);
		m_defaultHandler	  = new AseSipDefaultHandler(this, m_sil);
		m_defaultHandler.initialize();
		m_sil.setDefaultHandler(m_defaultHandler);
		m_psil				  = new AsePseudoStackInterfaceLayer(this,
				m_subscriptionManager,
				m_factory);
		//Initialize the pending messages list.
		//This list will be used to hold all the messages received before the
		//SIP Connector is started. 
		m_pendingMessages	  = new ArrayList();
		m_started		  	  = false;

		ingwMessageQueue = (String)m_configRepository.getValue(Constants.INGW_MSG_QUEUE);
		nsepIngwPriority = 	(String)m_configRepository.getValue(Constants.NSEP_INGW_PRIORITY);
		
		String pTimeOutVal = (String)m_configRepository.getValue(Constants.PROXY_TIMEOUT_VALUE);
		
		if(pTimeOutVal != null && !pTimeOutVal.isEmpty()){
			if (m_l.isDebugEnabled()) {
				m_l.debug( "AseSipConnector(AseContainer): pTimeOutVal "+pTimeOutVal);
			}
			m_proxyTimeout = Integer.parseInt(pTimeOutVal);
		}
		
		String proxyTimeOutEnabled = (String)m_configRepository.getValue(Constants.PROXY_TIMEOUT_ENABLE);
		
		if(proxyTimeOutEnabled != null && !proxyTimeOutEnabled.isEmpty()){
			if (m_l.isDebugEnabled()) {
				m_l.debug( "AseSipConnector(AseContainer): "+proxyTimeOutEnabled);
			}
			m_proxyTimeoutEnabled = Integer.parseInt(proxyTimeOutEnabled);
		}
		
		Registry.bind(Constants.DIALOG_MGR, m_dialogMgr);

		if (m_l.isDebugEnabled()) {
		m_l.debug( "AseSipConnector(AseContainer):exit");
	}
	}

	/**
	 * Changes TMN state of connector and calls state change methods on
	 * underlying objects, if available.
	 *
	 * @param componentState state to which connector has to transit. It can be
	 *                       LOADED, RUNNING or STOPPED.
	 */
	public void changeState(MComponentState componentState) {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "SipConnector : changeState(MComponentState):enter");
		}

		if(componentState.getValue() == MComponentState.LOADED) {
			if(m_l.isInfoEnabled()) { 
				m_l.info("Change state to LOADED - Going to initialize self"); 
			}

			initialize(m_container);

			doBasicConfiguration();
			doOtherConfiguration();


			if(null != System.getProperties().getProperty(AseSipConstants.ASE_MODE)) {
				m_factory.setCallIdGenerator(new AseSimSipCallIdGenerator(
						AseSipConnector.getHostAddress(), 0));
			}
		} 
		else if(componentState.getValue() == MComponentState.RUNNING) 
		{
			// Donot start the connector here and wait for the role notification
			if(m_l.isInfoEnabled()) { 
				m_l.info("Change state to RUNNING - No need to do start connector here"); 
			}
			if (m_l.isDebugEnabled()) {
			m_l.debug("Starting SIP Diagnostic Logger");
			}
			AseSipDiagnosticsLogger.getInstance().initialize();
			if (m_l.isDebugEnabled()) {
			m_l.debug("Starting SIP Latency  Logger");
			}
			AseLatencyLogger.getInstance().initialize();
			if (m_l.isDebugEnabled()) {
			m_l.debug("Starting SIP Selective Logger");
			}
			SelectiveMessageLoggingManager.getInstance().initialize();
			if (m_l.isDebugEnabled()) {
				m_l.debug("Starting Call Stats Processer and registering it");
			}
			CallStatsProcessor.getInstance().initialize();
			Registry.bind(Constants.PROP_CALL_STATS_PROCESSOR, CallStatsProcessor.getInstance());
			
		}
		else if(componentState.getValue() == MComponentState.SOFT_STOP) {
			if(m_l.isInfoEnabled()) {
				m_l.info("Change state to SOFT STOP ");
			}
			softStop();
		}
		else if(componentState.getValue() == MComponentState.STOPPED) {
			if(m_l.isInfoEnabled()) {
				m_l.info("Change state to STOPPED - Going to stop self");
			}
			stop();
		}

		if (m_l.isDebugEnabled()) {
		m_l.debug( "changeState(MComponentState):exit");
	}
	}

	private void registerForBackgroundProcess()  {
		ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		try {
			headerDumpFreq = (int)Integer.parseInt(config.getValue(Constants.FREQ_HEADER_DUMP));
		} catch(Exception e) {
			this.headerDumpFreq = 1000;
		}
		long dumpPeriod;
		try {
			String str = config.getValue(Constants.DUR_QUEUE);
			if (str != null)  {
				dumpPeriod = (long)Long.parseLong(str);
			} else { 
				m_l.error("Unable to Register Sip Connector to BKG Processor");
				return;
			}
		} catch (Exception e) {
			m_l.error("Unable to Register Sip Connector to BKG Processor");
			return;
		}

		try  {
			AseBackgroundProcessor processor = (AseBackgroundProcessor) Registry.lookup(Constants.BKG_PROCESSOR);
			processor.registerBackgroundListener(this, dumpPeriod);
		} catch (Exception e)  {
			m_l.error(e.getMessage(), e);
		}
	}



	public void process(long currentTime)  {
		AseEngine engine = (AseEngine) Registry.lookup(Constants.NAME_ENGINE);
		HashMap stkQ = DsWorkQueue.getQueueTable();
		ArrayList connectorQ = this.m_threadPool.getQueueList();
		ArrayList contQ = engine.getContainerQueues();

		if(dumpCount%headerDumpFreq == 0) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("QUEU:");
			//buffer.append("CT_CB_Q"); // not printing from here due to logic change in stack.
			//buffer.append(AseStrings.COMMA);		// now printing in seperate thread.
			buffer.append("SR_CB_Q");
			buffer.append(AseStrings.COMMA);
			//buffer.append("DT_I_Q");  // not printing from here due to logic change in stack.
			//buffer.append(AseStrings.COMMA);		// now printing in seperate thread.
			buffer.append("TR_Q");
			buffer.append(AseStrings.COMMA);

			// Adding connector Queue : presently its only one
			for(int i=0 ; i<connectorQ.size() ; i++)  {
				buffer.append("SIP_Q"+i);
				buffer.append(AseStrings.COMMA);
			}
			for(int i=0 ; i<contQ.size() ; i++) { 
				buffer.append("Q"+i);
				buffer.append(AseStrings.COMMA);
			}
			String hdr = buffer.substring(0,buffer.lastIndexOf(AseStrings.COMMA));
			m_l.error(hdr);
		}

		int ct_callback = 0;
		int st_callback = 0;
		int timer = 0;
		int data = 0;

		Iterator iter = stkQ.keySet().iterator();
		while(iter.hasNext()) {
			Object obj = iter.next();
			DsWorkQueue que = (DsWorkQueue)stkQ.get(obj);
			if(obj.toString().equals(DsWorkQueue.CLIENT_CALLBACK_QNAME)) {
				ct_callback = que.getSize();
			} else if (obj.toString().equals(DsWorkQueue.SERVER_CALLBACK_QNAME))  {
				st_callback = que.getSize();
			} else if (obj.toString().equals(DsWorkQueue.DATA_IN_QNAME))  {
				data = que.getSize();
			} else if (obj.toString().equals(DsWorkQueue.TIMER_QNAME))  {
				timer = que.getSize();
			}
		}
		StringBuffer buff = new StringBuffer();

		// Adding stack Queue
		buff.append("QUEU:");
		//buff.append(ct_callback);
		//buff.append(AseStrings.COMMA);
		buff.append(st_callback);
		buff.append(AseStrings.COMMA);
		//buff.append(data);
		//buff.append(AseStrings.COMMA);
		buff.append(timer);
		buff.append(AseStrings.COMMA);

		// Adding connector Queue : presently its only one
		for(int i=0 ; i<connectorQ.size() ; i++)  {
			Queue queue = (Queue) connectorQ.get(i);
			if(queue != null)  {
				buff.append(queue.size());
				buff.append(AseStrings.COMMA);
			} else {
				m_l.error("QUEUE is null");
			}
		}

		// Adding Containor Queues
		for(int i=0 ; i<contQ.size() ; i++)  {
			Queue queue = (Queue) contQ.get(i);
			if(queue != null)  {
				buff.append(queue.size());
				buff.append(AseStrings.COMMA);
			} else {
				m_l.error("QUEUE is null");
			}
		}

		String val  = buff.substring(0,buff.lastIndexOf(AseStrings.COMMA));
		m_l.error(val);
		
		//Check added for Non FT installation
//		if(AseModes.isFtMode(this.m_mode)){
//			//printing datachannel queue size
//			StringBuffer buffDataChannel = new StringBuffer();
//			buffDataChannel.append("DATA_CHANNEL QUEU :");
//			StringBuffer buffDataChannelPostRep = new StringBuffer();
//			buffDataChannelPostRep.append("DATA_CHANNEL REP-QUEU :");
//
//			ChannelProviderFactory channelProviderFactoryObj = ChannelProviderFactory.getInstance();
//			DataChannelPool dataChannelPoolObj= (DataChannelPool)channelProviderFactoryObj.getDataChannelProvider();
//			ArrayList mChannelList = dataChannelPoolObj.getChannelList();
//			ChannelProvider provider;
//			for(int i=0;i<mChannelList.size();i++){
//				provider = (ChannelProvider)mChannelList.get(i);
//				JChannel jChannel ;
//				if(provider instanceof JGroupsChannelProvider){
//					jChannel = ((JGroupsChannelProvider)provider).getJChannel();
//					//buffDataChannel.append(jChannel.getNumMessages());
//					buffDataChannel.append(jChannel.getReceivedMessages() + jChannel.getSentMessages());
//					buffDataChannel.append(AseStrings.COMMA);
//
//					int size = ((JGroupsChannelProvider)provider).getDataQueuePostRepSize();
//					buffDataChannelPostRep.append(size);
//					buffDataChannelPostRep.append(AseStrings.COMMA);
//
//					m_l.error("JChannel["+i+"] - Received Bytes : " + jChannel.getReceivedBytes() + " : Received Messages : " +jChannel.getReceivedMessages()+" : Sent Bytes : " + jChannel.getSentBytes() +" : Sent Messages : "+jChannel.getSentMessages());
//
//				}
//			}
//
//			//		String dataQueueSize  = buffDataChannel.substring(0,buffDataChannel.lastIndexOf(","));
//			//		m_l.error(dataQueueSize);
//			m_l.error(buffDataChannel);
//			//		String dataQueueSizePostRep  = buffDataChannelPostRep.substring(0,buffDataChannelPostRep.lastIndexOf(","));
//			//		m_l.error(dataQueueSizePostRep);
//			m_l.error(buffDataChannelPostRep);
//
//
//			StringBuffer buffStandByRepThread = new StringBuffer();
//			buffStandByRepThread.append("STAND_BY_REP QUEU :");
//			ArrayList stanyByRepThreadList = dataChannelPoolObj.getStandByRepThreadList();
//			for(int i=0;i<stanyByRepThreadList.size();i++){
//
//				StandbyReplicator  standbyReplicator= (StandbyReplicator)stanyByRepThreadList.get(i);
//				ThreadPool threadPoolStandBy = standbyReplicator.getStandByThreadPool();
//				ArrayList arrayListQueueList = threadPoolStandBy.getQueueList();
//
//				for(int j=0 ; j<arrayListQueueList.size() ; j++)  {
//					Queue queue = (Queue) arrayListQueueList.get(j);
//					if(queue != null)  {
//						buffStandByRepThread.append(queue.size());
//						buffStandByRepThread.append(AseStrings.COMMA);
//					} else {
//						m_l.error("QUEUE is null");
//					}
//				}
//			}
//			//		String dataStandByQueueSize  = buffStandByRepThread.substring(0,buffStandByRepThread.lastIndexOf(","));
//			//		m_l.error(dataStandByQueueSize);
//			m_l.error(buffStandByRepThread);
//
//			StringBuffer buffControlChannel = new StringBuffer();
//			buffControlChannel.append("CTRL_CHANNEL QUEU :");
//			ControlChannelProvider cntrlChannelPoolObj= (ControlChannelProvider)channelProviderFactoryObj.getControlChannelProvider();
//			if(cntrlChannelPoolObj instanceof JGroupsControlChannelProvider){
//				JChannel jChannel = ((JGroupsControlChannelProvider)cntrlChannelPoolObj).getJChannel();
//				//buffControlChannel.append(jChannel.getNumMessages());
//				buffControlChannel.append(jChannel.getReceivedMessages()  + jChannel.getSentMessages());
//				buffControlChannel.append(AseStrings.COMMA);
//
//			}
//			//		String ctrlQueueSize  = buffControlChannel.substring(0,buffControlChannel.lastIndexOf(","));
//			//		m_l.error(ctrlQueueSize);
//			m_l.error(buffControlChannel);
//
//		}
		dumpCount+=1;
	}


	public void roleChanged(String clusterId, PartitionInfo partitionInfo) {
		if( (AseRoles.ACTIVATING != this.m_role)
				&& (AseRoles.ACTIVE != this.m_role) ) {	
			if( (AseRoles.ACTIVATING == partitionInfo.getRole())
					|| (AseRoles.ACTIVE == partitionInfo.getRole()) ) {	
				if(m_l.isInfoEnabled())
					m_l.info("Role changes to ACTIVATING - going to do self start");

				String fips = partitionInfo.getFip();
				String [] fipsArray = fips.split(AseStrings.COMMA);
				m_ipAddress = fipsArray[0];
				m_l.info("m_ipAddress=== : " + fipsArray[0]);
				m_addressList.add(m_ipAddress);
				
				//start for n+k multihome
				controlMgr = (ControlManager)Registry.lookup(Constants.NAME_CONTROL_MGR);
				subSystem = controlMgr.getSelfInfo();
				
				if(subSystem.getMode() == AseModes.FT_N_PLUS_K ){
					m_l.info(" FIP adding in m_ipAddressList for n+k .");

					String portStr = (String)m_configRepository.getValue(
							Constants.OID_SIP_CONNECTOR_PORT);
					String [] portsArray = portStr.split(AseStrings.COMMA);


					for (int i=0; i < fipsArray.length; i++) {
						if(fipsArray[i] != null) {
							String str = ClusterManager.adjustFIPFormat(fipsArray[i].trim());
							m_ipAddressList.add(str);
						}
					}	

					for (int i=0; i < portsArray.length; i++) {
						if(portsArray[i] != null) {
							try{
								m_portList.add(new Integer( Integer.parseInt(portsArray[i].trim()) ) );
							} catch(NumberFormatException e) {							
								if(m_l.isInfoEnabled()) {
									m_l.info("Invalid Port. Read Next.");
								}							
							}			 
						}
					}
					
					if(m_ipAddressList.size() > m_portList.size()) {
						for(int i=m_portList.size(); i<m_ipAddressList.size(); i++)
							m_portList.add(new Integer(5060));
					}

					for(int i=0; i<m_ipAddressList.size(); i++) {
						SipURI uri = m_factory.createSipURI("sas", m_ipAddressList.get(i));
						uri.setPort((m_portList.get(i)).intValue());
						m_OutboundInterfaceList.add(uri);

					}	
				}
				//end
				
				String additionalPort = (String)m_configRepository.getValue(Constants.ADDITIONAL_SIP_LISTENER_PORT);
			
				if (additionalPort != null && !additionalPort.isEmpty()) {
					String [] ports = additionalPort.split(AseStrings.COMMA);
					for (String port : ports) {
						m_ipAddressList.add(m_ipAddress);
						m_portList.add(Integer.parseInt(port.trim()));
					}
				}
				
				
				setRRUri();
				setPathUri();
				m_factory.initialize();
				this.start();
			}
		}

		this.m_role = partitionInfo.getRole();
		try {
			DeployerFactory deployerFactoryImpl = (DeployerFactory)Registry.lookup(DeployerFactory.class.getName());
			Deployer appDeployer = deployerFactoryImpl.getDeployer(DeployableObject.TYPE_SAS_APPLICATION);
			Iterator deployedApps = appDeployer.findAll();
			while(deployedApps.hasNext()) {
				Object obj = deployedApps.next();
				if ( obj instanceof AseContext) {
					AseContext deployableObject = (AseContext)obj;
					deployableObject.setAttribute(Constants.PROP_ROLE , (Short)this.m_role );
					deployableObject.setAttribute(Constants.PROP_IP_ADDRESS , m_ipAddress ) ;
				}
			}
		} catch ( Exception e ) {
			m_l.error("Unable to get Deployed Applications", e);
		}

	}

	/**
	 * Updates configuration of connector at runtime.
	 *
	 * @param configData configuration parameter name value pair
	 *
	 * @param opType type of operation - ADD, MODIFY or DELETE
	 */
	public void updateConfiguration(	Pair[]			configData,
			OperationType	opType) {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "updateConfiguration(Pair[], OperationType):enter");
		}

		for(int i = 0; i < configData.length; i++) {
			if(m_l.isInfoEnabled())
				m_l.info("param name [" + configData[0].getFirst()
						+ "], param value [" + configData[0].getSecond()
						+ "], operation type [" + opType + AseStrings.SQUARE_BRACKET_CLOSE);
			// TBD - updateConfiguration(Pair[], OperationType)
		}

		if (m_l.isDebugEnabled()) {
		m_l.debug( "updateConfiguration(Pair[], OperationType):exit");
	}
	}

	/**
	 * Accepts the messages from container and submits them into thread pool
	 * for further processing.
	 *
	 * @param message message to be processed
	 */
	public void handleMessage(AseMessage message) throws IOException {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "handleMessage(AseMessage):enter");
		}

		if(message == null) {
			m_l.error("NULL Message object received");

			if (m_l.isDebugEnabled()) {
				m_l.debug( "handleMessage(AseMessage):exit");
			}
			return;
		}

		//If the SIP Connector is not yet started,
		//Then queue this message to be processed after starting it.
		if(!this.m_started){
			synchronized(this){
				if(!this.m_started){
					if(m_l.isDebugEnabled()){
						if (m_l.isDebugEnabled()) {
						m_l.debug("The SIP Connector is not yet started. " +
						"So queuing this request for processing later");
					}
					}
					this.m_pendingMessages.add(message);
					return;
				}
			}
		}

		if(message.getStatus() == AseMessage.LOOPBACK_SYNC) {
			// Loopback message, send to PSIL
			if(m_l.isInfoEnabled())
				m_l.info("Passing message type ["
						+ message.getMessageType() + "] to PSIL");
			if(message.getMessage() instanceof AseSipServletRequest) {
				m_psil.sendRequest((AseSipServletRequest)message.getMessage());
			} else if(message.getMessage() instanceof AseSipServletResponse) {
				m_psil.sendResponse((AseSipServletResponse)message.getMessage());
			} else {
				m_l.error("Invalid message type");
			}
		} else {
			// Outgoing message, send to SIL

			/*
			 * bug 8240 the below code will add P-Preferred header and remove
			 * P-Asserted-Service header from the request.when the next hop is
			 * not a trusted domain.
			 * 
			 * If trusted nodes property is empty in ase.properties 
			 * then all the request will add
			 * P-Preferred-Service and remove P-Asserted-Service
			 */
			if (message.getMessage() instanceof AseSipServletRequest) {
				AseSipServletRequest reqt = (AseSipServletRequest) message
						.getMessage();
				String pAssertedServiceHeader = reqt
						.getHeader(Constants.P_ASSERTED_SERVICE);
				if (pAssertedServiceHeader != null) {
					boolean needToAddPPreferredService = true;
					String nextHopHost = null;
					try {
						SipURI HostHeaderURI = null;
						Address routeAddressHeader = reqt
								.getAddressHeader(Constants.ROUTE);
						if (routeAddressHeader != null) {
							HostHeaderURI = (SipURI) routeAddressHeader.getURI();
						}
						else {
							HostHeaderURI = (SipURI) reqt.getRequestURI();
									
						}																	
						
						if (HostHeaderURI.isSipURI()) {
							
							nextHopHost = HostHeaderURI.getHost();		
						

						 
						m_l.debug("Next Hop Host : " + nextHopHost);
						if (trustedNodes != null && !(trustedNodes.isEmpty())) {
							String someTrustedNodes[] = trustedNodes.split(",");
							for (String trustedNode : someTrustedNodes) {								
								if ((trustedNode != null && nextHopHost != null)
										&& ((trustedNode.trim())
												.equalsIgnoreCase(nextHopHost))) {
									needToAddPPreferredService = false;
									break;
								}
							}

						}
						}
					} catch (ServletParseException ex) {
						m_l.debug("Not a valid Sip uri : " + nextHopHost);
						m_l.info(ex);
						needToAddPPreferredService = true;
					}
					if (needToAddPPreferredService) {
						m_l
								.debug("Since request uri is not a valid domain..Hence Chainging P-Asserted-Service header to P-Preferred-Service header");
						reqt.addHeader(Constants.P_PREFERRED_SERVICE,
								pAssertedServiceHeader);
						reqt.removeHeader(Constants.P_ASSERTED_SERVICE);
					}				
				}
			}
			// end of bug 8240


			// If initial request, check for outbound proxy/gateway
			if(message.getMessage() instanceof AseSipServletRequest) {
				// Push out bound proxy ROUTE header if required and not forbidden
				// by application
				AseSipServletRequest req = (AseSipServletRequest)message.getMessage();
				// Earlier SAS was sending only initial requests INVITE to the out-bound
				// gateway and subsequent requests like ACK, BYE were sent directing 
				// to the terminating party. Now all the requests are sent to the out-bound
				// gateway.
				//if(req.isInitial() && 
				if((null == req.getAttribute(Constants.DISABLE_OUTBOUND_PROXY) && null == req.getSession().getAttribute(Constants.DISABLE_OUTBOUND_PROXY)) &&
						m_gwSelector.processingActive()) {
					OutboundGateway obgw = m_gwSelector.select();
					if (obgw == null) {
						throw new IOException("No outbound gateway available.");
					} else {
						InetAddress ip = obgw.getHost();
						String ipStr = ip.getHostAddress();
						if(m_l.isDebugEnabled()) {
							m_l.debug("Pushing outbound proxy route on initial request: " + 
									ipStr + ":" + obgw.getPort());
						}
						req.pushRoute(ipStr, obgw.getPort(), null);
					}
				}
			}

			if(m_l.isInfoEnabled())
				m_l.info("Submitting message type ["
						+ message.getMessageType() + "] into thread pool");

			if(message.getWorkQueue() >= 0) {
				m_threadPool.submit(message.getWorkQueue(), message,message.isPriorityMessage());
			} else {
				m_threadPool.submit(message,message.isPriorityMessage());
			}
		}
		if (m_l.isDebugEnabled()) {
		m_l.debug( "handleMessage(AseMessage):exit");
		}
	}

	/**
	 * This method processes thread specific pending messages.
	 */
	public void messageProcessed() {
		m_psil.processMessages();
	}

	/**
	 * Returns protocol string.
	 *
	 * @return returns "SIP/2.0"
	 */
	public java.lang.String getProtocol() {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "getProtocol() called");
		}
		return "SIP/2.0";
	}

	/**
	 * Returns associated connector SIP factory
	 *
	 * @return returns connector factory object
	 */
	public java.lang.Object getFactory() {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "getFactory() called");
		}
		return m_factory;
	}

	public AseB2buaHelperImpl getB2bHelper() {
		return this.m_b2buaHelper;
	}

	/**
	 * Returns associated SIP Overload Manager.
	 *
	 * @return SIP overload manager.
	 */
	public AseSipOverloadManager getOverloadManager() {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "getOverloadManager() called");
		}
		return m_overloadManager;
	}

	/**
	 * Returns associated SIP Overload Manager for Priority calls.
	 *
	 * @return SIP overload manager.
	 */
	public AseSipOverloadManager getOverloadManager(boolean priority)
	{
		if(m_engine.isCallPriorityEnabled() && priority && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
			if(m_l.isDebugEnabled())
				m_l.debug( "Priority Call: getOverloadManager(boolean) called");
			return m_nsepOverloadManager;
		} else {
			if(m_l.isDebugEnabled())
				m_l.debug( "getOverloadManager(boolean) called");
			return this.getOverloadManager();
		}
	}

	public void dumpLongDurationDialogs(int p_duration, Logger p_logger) {
		m_dialogMgr.dumpLongDurationDialogs(p_duration, p_logger);
	}

	/**
	 * This method is notification from container about termination of given
	 * SIP session. It removes the session from dialog manager.
	 *
	 * @param session protocol session to be removed
	 */
	public void removeSession(SasProtocolSession session) {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "removeSession(AseProtocolSession):enter");
		}

		if( session instanceof AseSipSession) {
			m_dialogMgr.removeSession((AseSipSession)session);

		} else {
			// Log warning
			if(m_l.isEnabledFor(Level.WARN))
				m_l.warn("SIP connector received non-SIP session with id = " +
						session.getId());
		}

		if (m_l.isDebugEnabled()) {
		m_l.debug( "removeSession(AseProtocolSession):exit");
	}
	}

	/**
	 * This method is notification from container about creation of given
	 * SIP session. It adds the session to dialog manager.
	 *
	 * @param session protocol session to be added
	 */
	public void addSession(SasProtocolSession session) {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "addSession(AseProtocolSession):enter");
		}

		if( session instanceof AseSipSession) {
			m_dialogMgr.addSession((AseSipSession)session);
		} else {
			// Log warning
			if(m_l.isEnabledFor(Level.WARN))
				m_l.warn("SIP connector received non-SIP session with id" +
						session.getId());
		}

		if (m_l.isDebugEnabled()) {
		m_l.debug( "addSession(AseProtocolSession):exit");
	}
	}

	/**
	 * A new subscription is added to the subscription manager along
	 * with the corresponsing session
	 */
	void addSubscription(AseSipSubscription sub, AseSipSession session) {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "addSubscription:enter");
		}
		m_subscriptionManager.addSubscription(sub, session);
		if (m_l.isDebugEnabled()) {
		m_l.debug( "addSubscription:exit");
	}
	}

	/**
	 * A subscription is removed from the subscription manager
	 */
	void removeSubscription(AseSipSubscription sub, AseSipSession session) {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "removeSubscription:called");
		}
		m_subscriptionManager.removeSubscription(sub, session);
	}

	/**
	 * Return the session matching the subscription
	 */
	AseSipSession getSession(AseSipServletRequest request) {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "getSession:called");
		}
		return m_subscriptionManager.getSession(request);
	}

	/**
	 * Find a subscription which matches the input subscription
	 */
	AseSipSubscription getMatchingSubscription(AseSipSubscription orig) {
		if (m_l.isDebugEnabled()) {
			m_l.debug("getMatchingSubscription:enter");
			m_l.debug("getMatchingSubscription:exit");
		}
		return m_subscriptionManager.getMatchingSubscription(orig);
	}

	void sendToContainer(AseMessage message) {
		if (m_l.isDebugEnabled()) {
		m_l.debug("Entering sendToContainer");
		}

		int queueNumber = 0;

		if(message.getStatus() == AseMessage.LOOPBACK_ASYNC
				|| message.getStatus() == AseMessage.LOOPBACK_SYNC) {
			// In case of loopback message
			AseApplicationSession prevAppSession;
			if(message.getMessage() instanceof AseSipServletRequest) {
				prevAppSession = (AseApplicationSession)((AseSipServletRequest)message.getMessage()).
				getPrevSession().getApplicationSession();
			} else {
				prevAppSession = (AseApplicationSession)((AseSipServletResponse)message.getMessage()).
				getPrevSession().getApplicationSession();
			}

			queueNumber = prevAppSession.getIc().getWorkQueue();
			// Set queue number form previous message if equals to -1
			if(queueNumber ==-1){
				queueNumber=message.getMessage().getWorkQueue();
				prevAppSession.getIc().setWorkQueue(queueNumber);
			}
			
		} else if(message.getMessageType() == AseMessage.MESSAGE) {
			// In case of SasMessage
			if( ((AseSipServletMessage)message.getMessage()).isInitial() ){
				//queueNumber = ((AseSipServletMessage)message.getMessage()).getCallId().hashCode();
				//changed for INAP call sbtm
				queueNumber = ((AseSipServletMessage)message.getMessage()).getWorkQueue();
				//UAT-1435 The INAP call was failed in Main lab
				//This is required so that AseSipServletMessage mark the message as 
				//INAP
				if (((AseSipServletMessage)message.getMessage()).isAssistSipRequest()){
					message.setInapMessage(true);
				}
				//THis is added to identify the sunsequent responses and requests
				//while queing into the thread pool. This is necessiated because 
				//INAP traffic is identified through the presence of dialogue id
				//and in the NOTIGY we have dialogue id not in the response.
				message.setInitial(true);
				if (m_l.isDebugEnabled()) {
				m_l.debug("Entering sendToContainer nitin:" + queueNumber);
				}
			} else {

				queueNumber = ((AseApplicationSession)((AseSipServletMessage)message.getMessage()).
						getApplicationSession()).getIc().getWorkQueue();
			}
		} else if(message.getMessageType() == AseMessage.EVENT) {
			// In case of event object
			AseApplicationSession appSession= ((AseApplicationSession)((AseSipSession)message.
					getListener()).getApplicationSession());
			queueNumber = appSession.getIc().getWorkQueue();
		}
		// This is done so that messages going to thread pool from here only eligible
		//for worker thread distribution.
		//message.setDistributable(true);
		message.setWorkQueue(queueNumber);

		if(message.getMessage() instanceof AseSipServletMessage){
			AseLatencyData.noteLatencyData( (AseSipServletMessage)message.getMessage(),
					AseLatencyData.ComponentTimes.CONNECTOR, false );
		}
		m_container.handleMessage(message);

		if (m_l.isDebugEnabled()) {
		m_l.debug("Leaving sendToContainer");
	}
	}


	/**
	 * Creates SIP protocol connector specific dispatcher object
	 * thread pool and initializes super class. Also configures IP
	 * address and listen port. Creates an instance of SIP Overload Manager.
	 *
	 * @param container reference to container instance
	 */
	protected void initialize(AseContainer	container) {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "initialize(AseContainer):enter");
		}

		trustedNodes = (String) m_configRepository.getValue(Constants.PROP_SIP_TRUSTED_NODES);
		if(m_l.isInfoEnabled())
			m_l.info("Going to initialize super class");
		super.initialize(container);
		AsePing.initialize();// Initializing AsePing Variables 
		// Create thread pool
		String tnStr = (String)m_configRepository.getValue(
				Constants.PROP_MT_CONNECTOR_THREAD_POOL_SIZE);
		int threadNum = DEFAULT_THREADPOOL_SIZE; // default value
		if(tnStr != null) {
			if(m_l.isInfoEnabled())
				m_l.info("Number of SIP conn threads are : " + tnStr);
			try {
				threadNum = Integer.parseInt(tnStr);
			} catch(NumberFormatException exp) {
				if(m_l.isEnabledFor(Level.ERROR))
					m_l.error("SIPCONN_THREADNUM param value", exp);
			}
		}

		if(m_l.isInfoEnabled())
			m_l.info("Creating thread pool with [" + threadNum + "] threads");

		String oidStr = (String)m_configRepository.getValue(
				Constants.PROP_MT_MONITOR_MIN_PERCENT_THREADS_REQD);
		int minPercentageCommonThreads = 100;
		if(oidStr != null) {
			minPercentageCommonThreads = Integer.parseInt(oidStr);
		}

		String timeStr = (String)m_configRepository.getValue(
				Constants.OID_LATENCY_LOGGING_TIME);
		if(timeStr!=null){
			AseLatencyLogger.getInstance().setLatencyLoggingTime(Long.parseLong(timeStr));
		}

		try {
			m_threadPool = new ThreadPool(threadNum, false,"SIPTH", this, this, minPercentageCommonThreads);
			ThreadMonitor tm = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);
			this.m_threadPool.setThreadMonitor(tm);
		} catch (ThreadPoolException ex) {
			// Log error
			m_l.error("Thread pool creation failed", ex);
		}

		//Create an instance of SIP Overload Manager
		m_overloadManager = new AseSipOverloadManager();
		//Initailze the SIP Overload Manager
		m_overloadManager.initialize(false);

		// Create an instance of SIP Overload Manager for NSEP(Priority) calls
		m_engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
		if(m_engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL))) {
			m_nsepOverloadManager = new AseSipOverloadManager();
			//Initializing the NSEP SIP Overload Manager
			m_nsepOverloadManager.initialize(true);
			m_nsepOverloadManager.setNormalCallListener(m_overloadManager);
		}

		m_gwSelector = (OutboundGatewaySelector)Registry.lookup(Constants.NAME_OUTBOUND_GATEWAY_MANAGER);

		// Initialize SIL
		if(m_l.isInfoEnabled())
			m_l.info("Going to initialize SIL");
		m_sil.initialize(container);
		m_l.info("Going to initialize PSIL");
		m_psil.initialize(container);

		// Instantiate AseSipConstants
		AseSipConstants.getInstance();

		// Register ourselves with the root container
		container.registerConnector(this); 

		// Register itself as the role change listner
		// start will be called once the role is changed to ACTIVE
		ClusterManager clusterMgr = (ClusterManager)Registry.lookup(
				Constants.NAME_CLUSTER_MGR);
		clusterMgr.registerRoleChangeListener(this, Constants.RCL_SIPCONN_PRIORITY);

		// Instantiate the authentication handlers and register them 
		// with the SAS SecurityManager.
		this.m_digestAuthHandler = new AseSipDigestAuthenticationHandler();
		SasSecurityManager.registedAuthenticationHandler(this.m_digestAuthHandler);
		this.m_basicAuthHandler = new AseSipBasicAuthenticationHandler();
		SasSecurityManager.registedAuthenticationHandler(this.m_basicAuthHandler);
		this.m_paiAuthHandler = new AseSipAssertedIdentityAuthHandler();
		SasSecurityManager.registedAuthenticationHandler(this.m_paiAuthHandler);

		// Set SIP timer reference in transaction state table classes
		/*AsePseudoSipServerTxnStateTable.setSipTimer(m_sipTimer);		//NJADAUN
		AsePseudoSipClientTxnStateTable.setSipTimer(m_sipTimer);*/

		ingwMessageQueue = (String)m_configRepository.getValue(Constants.INGW_MSG_QUEUE);
		nsepIngwPriority = 	(String)m_configRepository.getValue(Constants.NSEP_INGW_PRIORITY);
		
		if(m_l.isDebugEnabled()){
			m_l.debug("Messages from INGw will come in " + ingwMessageQueue + " queue");
			m_l.debug("All call prioirty support with INGw messages coming in priority queue " + nsepIngwPriority);
		}
		String modeStr = m_configRepository.getValue(Constants.OID_SUBSYS_MODE);
		
		if(m_l.isDebugEnabled()){
			m_l.debug("bind dialog manager in registery " +m_dialogMgr);
		}
		Registry.bind(Constants.DIALOG_MGR, m_dialogMgr);
		
		modeStr = (modeStr == null) ? "" : modeStr.trim();
		try{
			m_mode = Short.parseShort(modeStr);
		}catch(NumberFormatException nfe){}
		// Register for Background process 
		this.registerForBackgroundProcess();

		if (m_l.isDebugEnabled()) {
		m_l.debug( "initialize(AseContainer):exit");
	}
	}

	/**
	 * Starts super class, thread pool and SIL. Also registers SIP Overload
	 * Manager with OCM.
	 */
	protected void start() {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "start():enter");
		}

		super.start();
		if (m_l.isDebugEnabled()) {
		m_l.debug("starting thread pool");
		}
		m_threadPool.start();

		// This should be called in the end of this method
		if (m_l.isDebugEnabled()) {
		m_l.debug("starting SIL");
		}
		m_sil.start();
		if (m_l.isDebugEnabled()) {
		m_l.debug("starting PSIL");
		}
		m_psil.start();

		//Set the started flag.
		synchronized(this){
			this.m_started = true;
		}

		//Now process all the pending requests.....
		if(m_l.isDebugEnabled()){
			m_l.debug("Processing the Pending Messages....");
		}
		Iterator it = this.m_pendingMessages.iterator();
		for(;it.hasNext();){
			try{
				AseMessage message = (AseMessage) it.next();
				this.handleMessage(message);
				it.remove();
			}catch(Exception e){
				m_l.error(e.getMessage(), e);
			}
		}
		if(m_l.isDebugEnabled()){
			m_l.debug("Completed Processing the Pending Messages.");
		}

		if (m_l.isDebugEnabled()) {
		m_l.debug( "start():exit");
		}
	}

	/**
	 * Stops SIL, thread pool and super class.
	 */
	protected void stop() {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "stop():enter");
		m_l.debug("stopping SIL");
		}
		m_sil.shutdown();
		
		if (m_l.isDebugEnabled()) {
		m_l.debug("stopping PSIL");
		}
		m_psil.shutdown();

		if (m_l.isDebugEnabled()) {
		m_l.debug("stopping thread pool");
		}
		m_threadPool.shutdown();

		super.stop();

		if (m_l.isDebugEnabled()) {
		m_l.debug( "stop():exit");
	}
	}

	/*
	 * Invoked when a call for softstop is received from EMS
	 */
	protected void softStop() {
		if (m_l.isDebugEnabled()) 
			m_l.debug( "softstop():enter");
		m_sil.softShutdown();
		
	}
	/**
	 * Returns the IP address of the host
	 */
	static String getHostAddress() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			return addr.getHostAddress();
		}
		catch(Exception e) {
			if (m_l.isDebugEnabled()) {
			m_l.debug("Exception: Unable to get the host address ", e);
		}
		}
		return null;
	}

	/**
	 * Accessor method for SIP connector IP address.
	 *
	 * @return SIP connector IP address in textual format
	 */
	public String getIPAddress() {
		return m_ipAddress;
	}

	/**
	 * Accessor method for SIP connector port.
	 *
	 * @return SIP connector port no.
	 */
	public int getPort() {
		return m_port;
	}

	public String getIpv6Address() {
		return m_ipv6Address;
	}

	public int getIpv6Port() {
		return m_ipv6port;
	}

	/**
	 * Accessor method for SIP connector TLS port.
	 *
	 * @return SIP connector TLS port no.
	 */
	public int getTlsPort() {
		return m_tlsPort;
	}

	/**
	 * Accessor method for list of IPs read from configuration
	 * 
	 * @return list of IPs
	 */
	public List<String> getIPAddressList() {
		return m_ipAddressList;
	}

	/**
	 * Accessor method for list of ports read from configuration
	 * 
	 * @return list of ports
	 */
	public List<Integer> getPortList() {
		return m_portList;
	}

	public void setChangedIPAddressList(List<String> ipAddr){
		for(int i =0 ;i<ipAddr.size();i++){
			String floatingIP_orig  =(String) ipAddr.get(i);
			int start = floatingIP_orig.lastIndexOf('[');
			int end = floatingIP_orig.lastIndexOf(']');
			if( start != -1 && end != -1){
				String floatingIP = floatingIP_orig.substring( start+1, end);
				m_ipChangedAddressList.add(floatingIP);
			}else{
				m_ipChangedAddressList.add(floatingIP_orig);
			}
		}
	}
	
	public List<String> getChangedIPAddressList(){
		return m_ipChangedAddressList;	
	}
	
	/**
	 * Accessor method for proxy timeout as in JSR 289
	 *
	 * @return proxy timeout in millisecs used for sequential searching
	 */
	public int getProxyTimeout() {
		return m_proxyTimeout;
	}

	/**
	 * Flag to enable/disble proxy timeout timer
	 *
	 * @return proxy timeout required
	 */
	
	public int getProxyTimeoutEnabled() {
		return m_proxyTimeoutEnabled;
	}

	/**
	 * Accessor method to retrieve list of configured IP addresses.
	 *
	 * @return list of configured IP addresses.
	 */
	public List getAddressList() {
		return m_addressList;
	}

	/**
	 * Accessor method for record route URI.
	 *
	 * @return record route URI to be used for proxying
	 */
	public SipURI getRecordRouteURI() {
		return m_recordRouteURI;
	}

	public SipURI getPathURI(){
		return m_pathURI;
	}
	
	/**
	 * Accessor method for record route SIPS URI.
	 *
	 * @return record route SIPS URI to be used for proxying
	 */
	public SipURI getRecordRouteSecureURI() {
		return m_recordRouteSURI;
	}

	/**
	 * Accessor method to get reference to SIP timer object.
	 *
	 * @return SIP connector timer
	 */
	public Timer getSipTimer() {
		return m_sipTimer;
	}

	/**
	 * Accessor method for SIP connector outbound interfaces.
	 *
	 * @return list of outbound interfaces
	 */
	public List<SipURI> getOutboundInterfaces() {
		return m_OutboundInterfaceList;
	}

	/**
	 * This method checks given address with configured ones.
	 * @param address address in form of quadratic IP address or FQDN
	 * @return true if address matches, false if it does not match
	 */
	public static boolean isMatchingAddress(String address) {
		// compare URI host with configure SAS assdresses
		Iterator iter = m_addressList.iterator();
		while(iter.hasNext()) {
			if(address.equalsIgnoreCase((String)iter.next())){
				// match found, return true
				return true;
			}
		}
		return false;
	}

	private void doBasicConfiguration() {
		if(m_l.isInfoEnabled()) {
			m_l.info("doBasicConfiguration():entry...");
		}

		try {
			Enumeration<NetworkInterface> nets = 
				NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets)) {
				Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
				for (InetAddress inetAddress : Collections.list(inetAddresses)) {
					m_addressList.add(inetAddress.getCanonicalHostName());
					m_NetworkInterfaceList.add(inetAddress.getHostAddress() );	//JSR289.34	            	
				}
			}
			if (m_l.isDebugEnabled()) {
				m_l.debug("FQDN value added is : " + m_addressList);
			}
		} catch (SocketException e) {
			m_l.fatal("Getting FQDN of localhost");
		}
		
		try {
			casDomainNames = 	(String)m_configRepository.getValue(Constants.CAS_DOMAIN_NAMES);
			if (m_l.isDebugEnabled()) {
				m_l.debug("CAS domain names : " + casDomainNames);
			}
			int domainLen=0;
			if(casDomainNames != null && !casDomainNames.isEmpty()){
				String[] casDomainName = casDomainNames.split(AseStrings.COMMA);
				domainLen = casDomainName.length;
				for (int i=0; i < domainLen; i++) {
					if(casDomainName[i] != null){
						if (m_l.isDebugEnabled()) {
							m_l.debug("CAS domain names added in address list : " + casDomainName[i].trim());
						}
						m_addressList.add(casDomainName[i].trim());
					}
				}
			}
		} catch (Exception e) {
			m_l.error("Exception in setting domain list for CAS");
		}
		
		

		String ipAddressStr = (String)m_configRepository.getValue(
				Constants.OID_SIP_CONNECTOR_IP_ADDRESS);

		//JSR289.34 starts
		int ipLen=0;
		if(ipAddressStr != null) {
			String[] ipAdds = ipAddressStr.split(AseStrings.COMMA);
			ipLen = ipAdds.length;
			for (int i=0; i < ipLen; i++) {
				if(ipAdds[i] != null) {
					String ipAddress=AseUtils.getIPAddress(ipAdds[i].trim());
					String str = ClusterManager.adjustFIPFormat(ipAddress);
					m_ipAddressList.add(str);
				} else {
					m_ipAddressList.add(null);
				}
			}		

			if(m_ipAddressList.size() > 0)
				m_ipAddress = m_ipAddressList.get(0);
		}
		//JSR289.34 ends
		
		setChangedIPAddressList(m_ipAddressList);
		
		if(m_l.isDebugEnabled())	{
			m_l.debug( "doBasicConfiguration: DEFAULT SIP CONNECTOR IP ADDRESS: " + m_ipAddress);
		}

		if(m_ipAddress == null) {
			if(m_l.isInfoEnabled())
				m_l.info("No self IP configured, taking local host IP address");
			InetAddress addr = null;
			try {
				addr = InetAddress.getLocalHost();
			} catch(UnknownHostException exp) {
				m_l.fatal("Getting IP address of this host", exp);
				if (m_l.isDebugEnabled()) {
					m_l.debug( "start():exit");
				}
				return;
			}
			m_ipAddress = addr.getHostAddress();
		}

		// this block of code adds addresses(FQDN) defined in etc/hosts to addresslist
		// address list is used by the container to match if incoming sip call's
		// route header matches container's own ip or FQDn
		try {
			// Get all network interfaces defined for this system
			Enumeration<NetworkInterface> nets = NetworkInterface
			.getNetworkInterfaces();
			while (nets.hasMoreElements()) {
				NetworkInterface netint = nets.nextElement();
				// get inet address for network interfaces
				Enumeration<InetAddress> inetAddresses = netint
				.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					// add all inet addresses's fully qualified domain name on
					// address list
					InetAddress inetAddress = inetAddresses.nextElement();
					m_addressList.add(inetAddress.getCanonicalHostName());
				}
			}
			if (m_l.isDebugEnabled()) {
				m_l.debug("FQDN value added is : " + m_addressList);
			}
		} catch (SocketException e) {
			m_l.fatal("Getting FQDN of localhost");
		}
		// Get listen port no. from config
		String portStr = (String)m_configRepository.getValue(
				Constants.OID_SIP_CONNECTOR_PORT);

		//JSR289.34 starts
		int portLen=0;
		if (portStr != null) {
			String[] portList = portStr.split(AseStrings.COMMA);
			portLen = portList.length;
			for (int i=0; i < portLen; i++) {
				if(portList[i] != null) {
					try{
						m_portList.add(new Integer( Integer.parseInt(portList[i].trim()) ) );
					} catch(NumberFormatException e) {							
						if(m_l.isInfoEnabled()) {
							m_l.info("Invalid Port. Read Next.");
						}							
					}			 
				} else {
					m_portList.add(new Integer(5060));
				}
			}

			if(m_portList.size() < 1) {
				m_portList.add(new Integer(5060));
			}

			try{
				m_port = (m_portList.get(0)).intValue();
			}catch(Exception ex){
				m_l.error(" Exception on getting port " + ex);
			}

		}
		//JSR289.34 ends

		for(int i =0 ;i<m_ipAddressList.size();i++){
			if( m_ipAddressList.get(i).startsWith(AseStrings.SQUARE_BRACKET_OPEN)){
				m_ipv6Address = m_ipAddressList.get(i);
				m_ipv6port =  m_portList.get(i);
				break;
			}
		}

		for(int i =0 ;i<m_ipAddressList.size();i++){
			if( !m_ipAddressList.get(i).startsWith(AseStrings.SQUARE_BRACKET_OPEN)){
				m_ipAddress = m_ipAddressList.get(i);
				m_port =  m_portList.get(i);
				break;
			}
		}


		if(m_l.isInfoEnabled()) {
			m_l.info("SIP connector default IP : " + m_ipAddress + ", Port : " + m_port);
			m_l.info( "SIP connector default IPV6 ADDRESS: " + m_ipv6Address+ " : port :"+m_ipv6port);
		}

		// Get TLS listen port no. from config
		portStr = (String)m_configRepository.getValue(
				Constants.OID_SIP_CONNECTOR_TLS_PORT);
		if(portStr != null) {
			m_tlsPort = Integer.parseInt(portStr);
		}

		if(m_l.isInfoEnabled()) {
			m_l.info("SIP connector TLS Port : " + m_tlsPort);
		}

		//JSR289.34
		if(m_ipAddressList.size() > m_portList.size()) {
			for(int i=m_portList.size(); i<m_ipAddressList.size(); i++)
				m_portList.add(new Integer(5060));
		}

		for(int i=0; i<m_ipAddressList.size(); i++) {
			if(m_ipAddressList.get(i) == null)
				continue;
			else {
				SipURI uri = m_factory.createSipURI("sas", m_ipAddressList.get(i));
				uri.setPort((m_portList.get(i)).intValue());
				m_OutboundInterfaceList.add(uri);
			}
		}			


		// SIP address is added in list of addresses
		if(m_ipAddressList != null){
			for(int i=0;i<m_ipAddressList.size();i++){
				m_addressList.add(m_ipAddressList.get(i).toString());
			}
		}
		//JSR289.34 ends

		// Also add local address of this machine
		String localAddress;
		if(m_configRepository.getValue(Constants.DUAL_LAN_SIGNAL_IP) != null)	{
			localAddress = AseUtils.getIPAddress(m_configRepository.getValue(Constants.DUAL_LAN_SIGNAL_IP));
		}else	{

			localAddress =  AseUtils.getIPAddress(m_configRepository.getValue(Constants.OID_BIND_ADDRESS));
		}
		if(localAddress != null && !localAddress.equals(m_ipAddress)) {
			m_l.info("SAS local IP : " + localAddress);
			m_addressList.add(localAddress);
		}

		setRRUri();
		setPathUri();
		
		/*try {
			handler.parse();
		} catch (Exception e) {
			e.printStackTrace();
		}*/

		if(m_l.isInfoEnabled()) {
			m_l.info("doBasicConfiguration():exit");
		}
	}

	private void setRRUri() {
		// Create and store Record-Route URI
		String rrUri = "sip:" + m_ipAddress + ":" + m_port + ";lr";
	//	+ AseSipConstants.RR_URI_PARAM;

		if(m_l.isInfoEnabled()) {
			m_l.info("Record-Route URI is : " + rrUri);
		}

		try {
			m_recordRouteURI = new AseSipURIImpl(rrUri);
		} catch(ServletParseException exp) {
			m_l.error("Creating record-route URI", exp);
		}

		// Create and store Record-Route SIPS URI
		rrUri = "sips:" + m_ipAddress + ":" + m_tlsPort + ";lr" ;//; AseSipConstants.RR_URI_PARAM;

		if(m_l.isInfoEnabled()) {
			m_l.info("Record-Route SIPS URI is : " + rrUri);
		}

		try {
			m_recordRouteSURI = new AseSipURIImpl(rrUri);
		} catch(ServletParseException exp) {
			m_l.error("Creating record-route SIPS URI", exp);
		}
	}
	
	private void setPathUri(){
		
		String pathUri = "sip:" + m_ipAddress + ":" + m_port + ";lr";
		
		if(m_l.isInfoEnabled()) {
			m_l.info("Path URI is : " + pathUri);
		}
		
		try {
			m_pathURI = new AseSipURIImpl(pathUri);
		} catch(ServletParseException exp) {
			m_l.error("Creating Path URI", exp);
		}
	}

	/**
	 * Callback as thread owner.
	 */
	public int threadExpired(MonitoredThread thread) {
		if(m_l.isInfoEnabled()) {
			m_l.info(thread.getName() + " expired");
		}

		// Calling ThreadPool's method as logic for min percentage of common
		// thread required, lies there
		return m_threadPool.threadExpired(thread);
	}

	public void execute(Object data) {
		if (m_l.isDebugEnabled()) {
		m_l.debug( "execute(AseMessage):enter");
		}

		AseMessage aseMsg = (AseMessage)data;
		int msgType = aseMsg.getMessageType();
		SasMessage sasMsg = aseMsg.getMessage();
		switch(msgType) {
		case AseMessage.MESSAGE :
			if(sasMsg instanceof AseSipServletRequest){
				AseSipServletRequest request =
					(AseSipServletRequest)sasMsg;

				if(aseMsg.getStatus() == AseMessage.PROCESSED) {
					if(m_l.isInfoEnabled())
						m_l.info("Handing over request [call id = " +
								request.getCallId() + "] to SIL");
					m_sil.sendRequest(request);
				} else {
					if(m_l.isInfoEnabled())
						m_l.info("Handing over request [call id = " +
								request.getCallId() + "] to default handler");
					m_defaultHandler.giveDefaultTreatment(request);
				}
			}else if(sasMsg instanceof AseSipServletResponse){
				AseSipServletResponse response =
					(AseSipServletResponse)sasMsg;

				if(m_l.isInfoEnabled())
					m_l.info("Handing over response [call id = " +
							response.getCallId() + "] to SIL");
				m_sil.sendResponse(response);
			}
			break;
		case AseMessage.EVENT :
			EventObject event = aseMsg.getEvent();

			if(m_l.isInfoEnabled())
				m_l.info("Event listener callback for event [" +
						event.toString() + AseStrings.SQUARE_BRACKET_CLOSE);
			aseMsg.getListener().handleEvent(event);
			break;
		default:
			// Log error message
			m_l.error("AseMessage received with unknown message type" + msgType);
		} // switch

		if (m_l.isDebugEnabled()) {
		m_l.debug( "execute(AseMessage):exit");
	}
	}

	private void doOtherConfiguration() {
		if(m_l.isInfoEnabled()) {
			m_l.info("doOtherConfiguration():entry");
		}

		// TBDNeeraj - get all configured IP addresses from config and
		// store in list of addresses - m_addressList
		// m_addressList.add(m_ipAddress);

		ConfigRepository configRepository = (ConfigRepository)Registry.lookup(
				Constants.NAME_CONFIG_REPOSITORY);

		//Set the copy policy with the default value.
		this.m_headerCopyPolicy = Constants.SIP_HEADER_DEEP_COPY_ALWAYS;

		//Get and Set the Header Copy Policy from the config property
		String strCopyPolicy = (String)configRepository.getValue(
				Constants.PROP_SIP_HEADER_COPY_POLICY);
		try{
			strCopyPolicy = (strCopyPolicy == null) ? "" : strCopyPolicy.trim();
			this.m_headerCopyPolicy = Integer.parseInt(strCopyPolicy);
		}catch(NumberFormatException nfe){}

		//Check for a valid value. If not valid, then use a Default.
		if(!(this.m_headerCopyPolicy == Constants.SIP_HEADER_DEEP_COPY_ALWAYS ||
				this.m_headerCopyPolicy == Constants.SIP_HEADER_SHALLOW_COPY_ALWAYS ||
				this.m_headerCopyPolicy == Constants.SIP_HEADER_USE_SHALLOW_COPYLIST)){
			m_l.warn("Got an Unknown value for the SIP Header Copy Ploicy." +
			"Will use the default value defined for it.");
			this.m_headerCopyPolicy = Constants.SIP_HEADER_DEEP_COPY_ALWAYS;
		}

		if(m_l.isDebugEnabled()){
			m_l.debug("The SIP Header Copy Policy value is :" + this.m_headerCopyPolicy);
		}

		//get the comma separated list of header names, that needs to be parsed using Shallow Copy Parser. 
		String strShallowCopyList = (String)configRepository.getValue(
				Constants.PROP_SIP_HEADER_SHALLOW_COPYLIST);
		strShallowCopyList = (strShallowCopyList == null) ? "" : strShallowCopyList.trim();
		StringTokenizer tokenizer = new StringTokenizer(strShallowCopyList, AseStrings.COMMA);
		for(;tokenizer.hasMoreTokens();){
			this.m_headerShallowCopyList.add(tokenizer.nextToken().trim());
		}

		if(m_l.isDebugEnabled()){
			m_l.debug("Header List for Shallow Copying :" + this.m_headerShallowCopyList);
		}


		if(m_l.isInfoEnabled()) {
			m_l.info("doOtherConfiguration():exit");
		}
	}

	public boolean checkConnectorIPType(){
		boolean checkIPv6 = false;
		boolean checkIPv4 = false;
		if(getIPAddressList().size() != 1){
			for(int i =0; i<getIPAddressList().size() ;i++){
				if(getIPAddressList().get(i).startsWith(AseStrings.SQUARE_BRACKET_OPEN)){
					checkIPv6 = true;
				}else{
					checkIPv4 = true;
				}
			}
		}
		return (checkIPv6 && checkIPv4) ;
	}

	public int getHeaderCopyPolicy(){
		return this.m_headerCopyPolicy;
	}

	public boolean isShallowCopyable(String header){
		return this.m_headerShallowCopyList.contains(header);
	}

	public short getRole() {
		return this.m_role;
	}


	////////////////////////////// private attributes /////////////////////////

	//private DebugHandler handler =  new DebugHandler();
	
	
	
	private int m_headerCopyPolicy = 0;

	private ArrayList m_headerShallowCopyList = new ArrayList();
	private ArrayList 				m_pendingMessages 	= null;

	private boolean					m_started 			= false;

	private ThreadPool				m_threadPool		= null;

	private AseStackInterfaceLayer	m_sil				= null;

	private AsePseudoStackInterfaceLayer m_psil			= null;

	private AseDialogManager		m_dialogMgr			= null;

	private AseSipSubscriptionManager m_subscriptionManager = null;

	private AseConnectorSipFactory	m_factory			= null;

	private AseB2buaHelperImpl 		m_b2buaHelper 		= null;

	private AseSipDefaultHandler	m_defaultHandler	= null;

	private AseContainer			m_container			= null;

	private String					m_ipAddress			= null;

	private String					m_ipv6Address		= null;

	private int						m_port				= 5060;

	private int						m_ipv6port			= 5060;

	private int						m_ipv4port	        =5060;

	private int						m_tlsPort			= 5061;

	private String					m_obIPAddress		= null;

	private int						m_obPort			= 5060;

	private AseSipOverloadManager	m_overloadManager	= null;

	private SipURI					m_recordRouteURI	= null;

	private SipURI					m_pathURI	        = null;
	
	private SipURI					m_recordRouteSURI	= null;

	private int						m_proxyTimeout = 30; // secs
	
	private int						m_proxyTimeoutEnabled = 1; // default enabled 

	private ConfigRepository		m_configRepository	= null;

	private AseSipDigestAuthenticationHandler m_digestAuthHandler = null;

	private AseSipBasicAuthenticationHandler m_basicAuthHandler = null;

	private AseSipAssertedIdentityAuthHandler m_paiAuthHandler = null;

	private short					m_role = AseRoles.UNKNOWN;
	
	private short 					m_mode = AseModes.NON_FT;

	private static List				m_addressList		= new Vector();

	private static List<String>		m_NetworkInterfaceList	= new Vector<String>(); //JSR289.34

	private static List<SipURI>		m_OutboundInterfaceList	= new Vector<SipURI>(); //JSR289.34

	private List<String>    		m_ipAddressList     	= new Vector<String>();	//JSR289.34

	private List<Integer>		    m_portList		        = new Vector<Integer>(); //JSR289.34
	
	private List<String>    		m_ipChangedAddressList  = new Vector<String>();	

	private static Timer			m_sipTimer			= new Timer();

	private AseSipOverloadManager m_nsepOverloadManager = null;

	private AseEngine 				m_engine			= null;

	private OutboundGatewaySelector m_gwSelector = null;
	
	private AseSubsystem subSystem;
	
	private ControlManager controlMgr;

	private static Logger m_l = Logger.getLogger(AseSipConnector.class.getName());

	private int dumpCount = 0;
	private int headerDumpFreq;
	private String trustedNodes = null;

	/////////////////////////////////// UT code ///////////////////////////////

	public static void main(String[] args) {
		System.getProperties().setProperty(	AseSipConstants.SIPCONN_THREADNUM,
		"1");
		if(m_l.isInfoEnabled())
			m_l.info("Creating container");
		AseEngine container = new AseEngine();

		if(m_l.isInfoEnabled())
			m_l.info("Creating sipconn");
		AseSipConnector sipconn = new AseSipConnector(container);

		if(m_l.isInfoEnabled())
			m_l.info("loading sipconn");
		sipconn.changeState(new MComponentState(MComponentState.LOADED));

		if(m_l.isInfoEnabled())
			m_l.info("running sipconn");
		sipconn.changeState(new MComponentState(MComponentState.RUNNING));

		if(m_l.isInfoEnabled())
			m_l.info("exiting from main()");
	}

	public void failed(SasMessage message, AseInvocationFailedException e) {
		if (e.getStatus() == AseMessage.NO_DESTINATION_FOUND ||
				e.getStatus() == AseMessage.LOOP_DETECTED) {                            
			AseMessage msg = new AseMessage(message);
			msg.setStatus(e.getStatus());
			try {
				this.handleMessage(msg);
			} catch (Exception ex) {
				m_l.error("Error handling failed message", ex);
			}
		}
		this.messageProcessed();
	}

	public void processed(SasMessage mesasge) {
		this.messageProcessed();
	}

	public String getId() {
		return "SIP/2.0";
	}

	public String getObjectName() {
		return "SIP";
	}

	public String getVersion() {
		return "2.0";
	}

	public SasMessageCallback getMessageCallback() {
		return this;
	}

	public void sendMessage(SasMessage message) throws IOException {
		//NOOP
		//This already handled differently in SIP Connector.
	}
}
