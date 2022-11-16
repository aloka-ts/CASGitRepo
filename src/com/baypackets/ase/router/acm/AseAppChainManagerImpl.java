/*******************************************************************************
 *   Copyright (c) 2017 Agnity, Inc. All rights reserved.
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
/**
 * 
 */

package com.baypackets.ase.router.acm;

import jain.protocol.ss7.tcap.JainTcapListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.ar.SipApplicationRouter;

import org.apache.log4j.Logger;

import com.agnity.mphdata.common.CallData;
import com.agnity.mphdata.common.Event;
import com.agnity.mphdata.common.PhoneNumber;
import com.agnity.mphdata.common.Protocol;
import com.agnity.ph.common.PhConstants;
import com.agnity.ph.common.ProtocolRouter;
import com.agnity.ph.common.ServiceInterface;
import com.agnity.ph.common.enums.CallDataAttribute;
import com.baypackets.ase.channel.AseSubsystem;
import com.baypackets.ase.channel.PeerMessage;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseApplicationSession;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.control.ControlManager;
import com.baypackets.ase.replication.AppDataMessageListener;
import com.baypackets.ase.router.AseSipApplicationRouterManager;
import com.baypackets.ase.router.customize.servicenode.SnApplicationRouter;
import com.baypackets.ase.router.customize.servicenode.SnApplicationRouterConfigData;
import com.baypackets.ase.spi.replication.appDataRep.AppDataReplicator;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.InitializationFailedException;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.genband.jain.protocol.ss7.tcap.TcapReplicationContext;
import com.genband.jain.protocol.ss7.tcap.TcapSessionImpl;
import com.genband.jain.protocol.ss7.tcap.TcapSessionReplicator;
import com.genband.tcap.provider.TcapSession;

/**
 * @author reeta
 *
 */
public class AseAppChainManagerImpl implements MComponent,AppDataMessageListener, AseAppChainManager {

	private static Logger _logger = Logger
			.getLogger(AseAppChainManagerImpl.class);
	private boolean enabled=true;

	final String ListenerApp = "ListenerApp";

	private ConcurrentHashMap<String ,Stack<String>> currentServicesStackMap = null;
	private ConcurrentHashMap<String,Set<String>> alltriggredServicesMap = null;

	private Map<String, ServiceInterface> serviceMap = null;
	private Map<String, String> servletNameMap = null;

	private Map<String, JainTcapListener> tcapListenerMap = null;
	private HashMap<String, AseContext> aseCtxtMap;
	private AseHost host;
	private ControlManager manager;
	private AseSubsystem subSystem;
	//DataChannelProvider dataChannelProvider;
	
	private static final String SERVICE_ID="SERVICE_ID";
	private static final String TCAP_LISTENER ="ListenerApp";

	/**
	 * This method is used to update and oid configuration
	 */
	@Override
	public void updateConfiguration(Pair[] arg0, OperationType arg1)
			throws UnableToUpdateConfigException {
	}

	/**
	 * This method is invoked by the EMS to update the state of this component.
	 * If the value of the given "state" parameter is LOADED, the meta data on
	 * all provisioned media servers will be read from the backing store.
	 */
	public void changeState(MComponentState state)
			throws UnableToChangeStateException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("changeState() called.  Setting component state to: "
					+ state);
		}

		try {
			if (state.getValue() == MComponentState.LOADED) {
				this.initialize();
			}
		} catch (Exception e) {
			String msg = "Error occurred while setting component state: "
					+ e.getMessage();
			_logger.error(msg, e);
			throw new UnableToChangeStateException(msg);
		}
	}

	/**
	 * This method initializes this object's state using the parameters
	 * specified in the ConfigRepository singleton. It internally calls the
	 * "initialize(Properties)" method.
	 * 
	 * @see com.baypackets.slee.common.ConfigRepository
	 * @see #initialize(Properties)
	 */
	public void initialize() throws InitializationFailedException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("initialize(): Initializing component state from ConfigRepository...");
		}

		ConfigRepository config =BaseContext.getConfigRepository();
		

		String sysappEnable = (String) config
				.getValue(Constants.PROP_SYSAPP_ENABLE);

		if (sysappEnable == null
				|| !sysappEnable.trim().contains(Constants.SYSAPP_APP_CHAIN_MANAGER)) {
			_logger.info("roleChanged(): sysapp deploy properties does not contain 'appchainmanager'.");
			enabled = false;
			return;
		}

		if (enabled) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("appchain manager is enabled creating maps .......");
			}
			serviceMap = new HashMap<String, ServiceInterface>();

			currentServicesStackMap = new ConcurrentHashMap<String,Stack<String>>();
			alltriggredServicesMap = new ConcurrentHashMap<String,Set<String>>();

			servletNameMap = new HashMap<String, String>();
			tcapListenerMap = new HashMap<String,JainTcapListener>();
			
			aseCtxtMap= new HashMap<String,AseContext>();
			
			this.host = (AseHost) Registry.lookup(Constants.NAME_HOST);
			
			this.manager = (ControlManager)Registry.lookup(Constants.NAME_CONTROL_MGR);
//	        manager.registerMessageListener(MessageTypes.APP_CHAIN_DATA_SYNC_MESSAGE, this);
			
		//    dataChannelProvider=ChannelProviderFactory.getInstance().getDataChannelProvider();
		    
//		    if (_logger.isDebugEnabled()) {
//				_logger.debug("Data channel provider is ......." +dataChannelProvider);
//			}
//        	if(dataChannelProvider != null){
//        		dataChannelProvider.registerAppDataMsgListener(this);
//        	}
	        subSystem = manager.getSelfInfo();
		}
	}

	/**
	 * This method is used to add new service to this app chain manager
	 */
	@Override
	public void addService(String serviceId, ServiceInterface serviceImpl,
			String servletName, JainTcapListener tcapListener,	ServletContext ac) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("addService(): serviceId :" + serviceId
					+ " ServiceImple: " + serviceImpl);
		}
		if (enabled) {
			serviceMap.put(serviceId, serviceImpl);
			servletNameMap.put(serviceId, servletName);
			tcapListenerMap.put(serviceId, tcapListener);

			Iterator contexts = this.host.findContextByNamePrefix(serviceId);

			if (_logger.isDebugEnabled()) {
				_logger.debug("addService(): contexts for serviceid  :"
						+ serviceId + " : " + contexts);
			}

			while (contexts.hasNext()) {
				AseContext actxt = (AseContext) contexts.next();

				if (_logger.isDebugEnabled()) {
					_logger.debug("addService(): serviceId :" + serviceId
							+ " Asecontext is: " + actxt);
				}
				aseCtxtMap.put(serviceId, actxt);
				break;
			}
		}else{
			if (_logger.isDebugEnabled()) {
				_logger.debug("addService():  app chain manager is not enabled so not adding service");
			}
		}
	

	}

	/**
	 * This method is used to get next interested service
	 * @throws Throwable 
	 */
	@Override
	public String getNextInterestedService(String callId,String currentSvcId,
			String prevSvcId, Map<String, Object> adressesMap,
			Event eventObject, String originInfo, Protocol protocol) throws Exception {

		if (_logger.isDebugEnabled()) {
			_logger.debug("getNextInterestedService(): callId "+ callId +" currentSvcId :"
					+ currentSvcId + " prevSvcId: " + prevSvcId
					+ " :adressesMap " + adressesMap + " eventObject: "
					+ eventObject + " OrigInfo: " + originInfo);
		}

		PhoneNumber originatingNumber = (PhoneNumber)adressesMap.get(CALLING_NUM);
		PhoneNumber terminatingNumber = (PhoneNumber)adressesMap.get(MODIFIED_DIALLED_NUMBER);

		SipApplicationRouter  sar=AseSipApplicationRouterManager.getSysAppRouter();

		String nextsvcId=null;
		
		if (sar != null && sar instanceof SnApplicationRouter) {

			try {
				Set<String>  triggeredServices=getAllTriggeredServices(callId);
				if(triggeredServices==null){
					triggeredServices = new HashSet<String>();
					triggeredServices.add(currentSvcId);
				}
				 
				if (_logger.isDebugEnabled()) {
					_logger.debug("getNextInterestedService(): currently triggered services are  :" + triggeredServices);
				}
				SnApplicationRouter snar = (SnApplicationRouter) sar;
				nextsvcId = snar.findApplicationForNormalRequest(
						terminatingNumber.getAddress(),
						originatingNumber.getAddress(), originInfo,triggeredServices);
				
				if(_logger.isDebugEnabled()){
					_logger.debug("Received nextServiceId : " + nextsvcId);
				}
				//Added <skList-to-invoke-defaultApp> in AppRouterConfig.xml, Default app
				//would be trigger for ServiceKey defined in this list.
				//If Service Key does'nt match null will be returned as nextServiceId.
				if(nextsvcId!=null && !nextsvcId.isEmpty()){
					
					SnApplicationRouterConfigData configData=SnApplicationRouter.getConfigData();
					
					if(nextsvcId.equals(configData.getDefaultApp()) /*&& configData.isUseServiceKeyOnNoMatch()*/ && protocol != null &&
						!protocol.equals(Protocol.SIP)){
						_logger.debug("Received originInfo : " + originInfo);
						if(originInfo.contains("|")){
							String[] originArray = originInfo.split(Pattern.quote("|"));
							if(!configData.getDefaultAllowedServiceKeyList().contains(originArray[1])){
								nextsvcId = null;
							}else{
								if(_logger.isDebugEnabled()){
									_logger.debug("Service key matches with the configured one, so Triggering defaault app");
								}
							}
						}
					}
					if(nextsvcId != null)
						updateTriggeredServices(callId, currentSvcId, nextsvcId);
				}else{
					nextsvcId=null;
				}
			} catch (Throwable e) {
				e.printStackTrace();
				_logger.error("Throw exception could not find application...."
						+ e.getMessage());

			}
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("getNextInterestedService(): returning nextsvcId :" + nextsvcId);
			}
			return nextsvcId;
		}

		int svcId = 0;
		if (currentSvcId != null) {
			svcId = Integer.parseInt(currentSvcId);
		}

		switch (svcId) {

		case 6: // origapp
			nextsvcId = "1"; //vpn
			break;
		case 1: // vpn
			nextsvcId = "10";
			break;
		case 10: // lnp
			nextsvcId = "2"; // atf
			break;
		case 2:	
			//if (prevSvcId != null && prevSvcId.equals("10")) {
				nextsvcId = null;
			//}

			break;
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("getNextInterestedService(): returning nextsvcId :" + nextsvcId);
		}
		return nextsvcId;
	}

	/**
	 * This method is used to invoke service chaining 
	 */
	@Override
	public void invokeServiceChaining(String currentSvcId, String nextSvcId,
			Map<String, Object> adressesMap, Event eventObject,
			TcapSession tcapSession, SipApplicationSession appSession,
			boolean remainInPath) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("invokeServiceChaining(): currentSvcId :"
					+ currentSvcId + " nextSvcId: " + nextSvcId
					+ " :adressesMap " + adressesMap + " eventObject: "
					+ eventObject + " tcapSession: " + tcapSession
					+ " appSession " + appSession + "remainInPath " +remainInPath);
		}
	
		CallData callData = null;
		String callId=null;

		if (tcapSession != null) {
			callData = (CallData) tcapSession.getAttribute(CallData.CALL_DATA);
			callId = ""+tcapSession.getDialogueId();
		} else if (appSession != null) {
			callData = (CallData) appSession.getAttribute(CallData.CALL_DATA);
			callId =(String)callData.get(CallDataAttribute.P_ORIG_LEG_CALL_ID);

		}
		callData.set(CallDataAttribute.P_DESTINATION_NUMBER,adressesMap.get(MODIFIED_DIALLED_NUMBER));
		

		if (_logger.isDebugEnabled()) {
			_logger.debug("invokeServiceChaining(): callID or Dialogue id is " +callId);
		}
		
		if (!remainInPath) {
			removeService(callId,currentSvcId);
		}
		
		Stack<String> currentServicesStack=pushServiceOnStack(callId, nextSvcId);//currentServicesStack.push(nextSvcId);
		
		Set<String> allTrigServices=updateTriggeredServices(callId, currentSvcId, nextSvcId);
		
		sendUpdatedDataToPeer(callId,currentServicesStack,allTrigServices);

		try {
			
//			if(tcapSession!=null){
//				appSession=getAppSession(tcapSession);
//			}
	
			if (appSession != null) {
				String servletHanlder = servletNameMap.get(nextSvcId);
				Iterator<?> it = appSession.getSessions("SIP");

				while (it.hasNext()) {
					SipSession sipSession = (SipSession) it.next();
					sipSession.setHandler(servletHanlder);
				}
				
				if (_logger.isDebugEnabled()) {
					_logger.debug("update AppSession context and add app session in next service context");
				}
				
				((AseApplicationSession)appSession).setContext(aseCtxtMap.get(nextSvcId));
				aseCtxtMap.get(nextSvcId).addApplicationSession((AseApplicationSession)appSession);
			}

			if (tcapSession != null) {
				
				if (_logger.isDebugEnabled()) {
					_logger.debug("update TcapSessionReplicationContext as  next service context "+ nextSvcId);
				}
				JainTcapListener tcapLtnr = tcapListenerMap
						.get(nextSvcId);
				tcapSession.setAttribute("ListenerApp", tcapLtnr);
				
				TcapReplicationContext trc=TcapSessionReplicator.getTcapReplicationContext(tcapLtnr);
				
				if (_logger.isDebugEnabled()) {
					_logger.debug("update TcapSessionReplicationContext as next service context "+ trc);
				}
				
				((TcapSessionImpl)tcapSession).setReplicationContext(trc);
				trc.setReplicable(((TcapSessionImpl)tcapSession));
			//	trc.setReplicableId(((TcapSessionImpl)tcapSession).getReplicableId());
				((TcapSessionImpl)tcapSession).setFirstReplicationCompleted(false);

			}
			Event serviceEvent = null;
			if (eventObject != null) {
				serviceEvent = (Event) eventObject;
			}
			
			ServiceInterface serviceObj = (ServiceInterface) serviceMap
					.get(nextSvcId);

			ProtocolRouter.getInstance().execute(serviceEvent, callData, serviceObj);

		} catch (Exception e) {
			e.printStackTrace();
           _logger.error(" exception thrown while calling invokeServiceChaining "+e);
		}

	}
	
	
	/**
     * This method returns the sip application session using reference save in
     * tcap session. It returns Tcap Notify AppSession.
     *
     * @param tcapSession represents an instance of TcapSession
     * @return an instance of SipApplicationSession
     */
    public static SipApplicationSession getAppSession(TcapSession tcapSession) {
        String appSessionId = (String) tcapSession.getAttribute(PhConstants.APPLICATION_SESSION_ID);
        
		if (_logger.isDebugEnabled()) {
			_logger.debug("getAppSession():  for" +appSessionId);
		}
        return tcapSession.getAppSession(appSessionId);
    }

	/**
	 * This method is used to get removed from path
	 */
	@Override
	public void serviceComplete(String currentServiceId, boolean notifyPrevSvc,
			Event eventObj, SipApplicationSession appSession,
			TcapSession tcapSession) throws Exception {
		if (_logger.isDebugEnabled()) {
			_logger.debug("serviceComplete(): currentServiceId: "
					+ currentServiceId + " NotifyPrevSvc " + notifyPrevSvc
					+ " Event " + eventObj);
		}
		
		CallData callData = null;
		
		if(appSession!=null && !appSession.isValid()){
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("serviceComplete():  this call app session is already invalidated");
			}
			return;
		}
		
		String callId=null;
		if (tcapSession != null) {
			callData = (CallData) tcapSession.getAttribute(CallData.CALL_DATA);
			callId = ""+tcapSession.getDialogueId();
		} else if (appSession != null) {
			callData = (CallData) appSession.getAttribute(CallData.CALL_DATA);
			callId =(String)callData.get(CallDataAttribute.P_ORIG_LEG_CALL_ID);

		}
		removeService(callId,currentServiceId);

		if (eventObj != null) {
			
			String serviceId = getTopService(callId);
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("serviceComplete(): prev serv id is : " + serviceId);
			}

			if (notifyPrevSvc && serviceId != null) {
				
				if (_logger.isDebugEnabled()) {
					_logger.debug("serviceComplete(): notify prev serv id is : " + serviceId);
				}

				if (appSession != null) {
					String servletHanlder = servletNameMap.get(serviceId);
					Iterator<?> it = appSession.getSessions("SIP");

					while (it.hasNext()) {
						SipSession sipSession = (SipSession) it.next();
						sipSession.setHandler(servletHanlder);
					}
					
					((AseApplicationSession)appSession).setContext(aseCtxtMap.get(serviceId));
				}

				if (tcapSession != null) {
					JainTcapListener tcapLtnr = tcapListenerMap.get(serviceId);
					tcapSession.setAttribute(TCAP_LISTENER, tcapLtnr);
					tcapSession.setAttribute(SERVICE_ID, serviceId);

				}

				ServiceInterface serviceObj = serviceMap.get(serviceId);
				
				appSession.setAttribute(SERVICE_ID, serviceId);
				callData.set(CallDataAttribute.SERVICE_ID, serviceId);
				ProtocolRouter.getInstance().execute(eventObj, callData, serviceObj);
				
				if (_logger.isDebugEnabled()) {
					_logger.debug("serviceComplete(): Replicate app sesison on peer with updated context for : " + serviceId);
				}
				 AppDataReplicator appDataRep = new AppDataReplicator();
				 appDataRep.doReplicate(appSession);
				 
			} else {

				if (_logger.isDebugEnabled()) {
					_logger.debug("serviceComplete(): not notifying prev service");
				}
			}

		}

	}

	/**
	 * This method is used to remove current service from current services stack
	 * 
	 */
	private void removeService(String callId,String serviceId) {
	
		if (_logger.isDebugEnabled()) {
			_logger.debug("removeService(): CallId is "+ callId +" serviceId: " + serviceId);
		}
		
		Stack<String> currentServicesStack = currentServicesStackMap.get(callId);
		if (currentServicesStack != null) {	
			currentServicesStack.remove(serviceId);
			
			if (currentServicesStack.isEmpty()) {

				if (_logger.isDebugEnabled()) {
					_logger.debug("removeService(): CallId is "
							+ callId
							+ " current services stack is empty remove it for this callid  also remove alltriggeredServices map");
				}
				currentServicesStackMap.remove(callId);
				alltriggredServicesMap.remove(callId);

				sendUpdatedDataToPeer(callId, null, null);
			} else {

				Set<String> allTrigServices = getAllTriggeredServices(callId);

				sendUpdatedDataToPeer(callId, currentServicesStack,
						allTrigServices);
			}
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("removeService(): CallId is "+ callId +" current services stack is "+currentServicesStack);
			}
		}
		

	}

	/**
	 * This method is used to get list of all the triggered services for a call id
	 */
	public Set<String> getAllTriggeredServices(String callId) {

		Set<String> trigServices=alltriggredServicesMap.get(callId);
		if (_logger.isDebugEnabled()) {
			_logger.debug("getAllTriggeredServices(): callId :"+callId +" Triggred services : " + trigServices);
		}
		return trigServices;
	}
	
	
	/**
	 * This method is used to updated triggred services list
	 * @param callId
	 * @param currentSvcId
	 * @param nextSvcId
	 */
	private Set<String> updateTriggeredServices(String callId,
			String currentSvcId, String nextSvcId) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("updateTriggeredServices(): serviceId:" + nextSvcId);
		}
		Set<String> alltriggredServices = alltriggredServicesMap.get(callId);

		if (alltriggredServices == null) {
			alltriggredServices = new HashSet<String>();
		}

		if (!alltriggredServices.contains(currentSvcId)) {
			alltriggredServices.add(currentSvcId);
		}
		alltriggredServices.add(nextSvcId);

		alltriggredServicesMap.put(callId, alltriggredServices);

		if (_logger.isDebugEnabled()) {
			_logger.debug("updateTriggeredServices(): triggered services list is :"
					+ alltriggredServices);
		}
		return alltriggredServices;
	}
	
	/**
	 * This method is used to push current service on stack
	 * @param callId
	 * @param serviceId
	 */
	private Stack<String> pushServiceOnStack(String callId,String serviceId){
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("pushServiceOnStack(): callId :"+ callId + " serviceId "+ serviceId);
		}
		
		Stack<String> currentServicesStack=currentServicesStackMap.get(callId);
		
		if(currentServicesStack==null){
			currentServicesStack =new Stack<String>();
			currentServicesStackMap.put(callId, currentServicesStack);
		}
		currentServicesStack.push(serviceId);
		return currentServicesStack;
		
	}

	/**
	 * This method is used to get latest service from chain stack
	 * @return
	 */
	private String getTopService(String callId) {
		
		String topService=null;
		if (_logger.isDebugEnabled()) {
			_logger.debug("getTopService():");
		}
		Stack<String> currentServicesStack=currentServicesStackMap.get(callId);
		
		if(currentServicesStack!=null && !currentServicesStack.isEmpty()){
			topService= (String) currentServicesStack.peek();
		}
		else{
			return null;
		}
		return topService;
	}

	/**
	 * This method is used to get serviceInterface for a specific service id
	 */
	@Override
	public ServiceInterface getServiceInterface(String serviceId) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("getServiceInterface(): return " + serviceMap.get(serviceId));
		}
		return serviceMap.get(serviceId);
	}
	
	/**
	 * This method is used to get servletname for a specific service id
	 */
	@Override
	public String getServletName(String serviceId) {

		if (_logger.isDebugEnabled()) {
			_logger.debug("getServletName(): return " + servletNameMap.get(serviceId));
		}
		return servletNameMap.get(serviceId);
	}

//	@Override
//	public void handleControlMessage(PeerMessage msg) {
//		 if (msg instanceof AppDataMessage) {
//	            
//	               handleMessage((AppDataMessage)msg);
//	            }
//	        }

	private void handleMessage(AppDataMessage message) {
		
		if (message.getMsgType() == AppDataMessage.ACK) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("handleMessage(): Incoming message was an ACK, so ignoring it.");
			}
			return;
		}
		
		if (message.getMsgType() == AppDataMessage.APP_CHAIN_MAP_UPDATED) {
			
			 String callId = message.getCallId();
			 
			 if (_logger.isDebugEnabled()) {
					_logger.debug("handleMessage(): update map for callid : "
							+ callId);
				}
			
			/**
			 * update current services stack for callid
			 */
			String currentChainedServices = message.getCurrentChainedServices();
			String allTriggered = message.getAllTriggredServices();
			
			Stack<String> currentStack=null;
			Set<String> triggeredList=null;
			
			if (currentChainedServices != null && allTriggered != null) {
				
				String[] currentchained = currentChainedServices.split(",");

				currentStack = new Stack<String>();

				for (int i = currentchained.length - 1; i >= 0; i--) {
					currentStack.add(currentchained[i]);
				}

				String[] alltrigg = allTriggered.split(",");

				triggeredList = new HashSet<String>(Arrays.asList(alltrigg));

			}
			
			  /**
             * Update all triggredlist map and current services stack
             */
			this.currentServicesStackMap.put(callId, currentStack);
			this.alltriggredServicesMap.put(callId, triggeredList);
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("handleMessage(): currentServicesStackMap: "
						+ currentStack +" alltriggredServicesMap "+alltriggredServicesMap +" callId "+callId);
			}

			// Send an acknowledgement.
			try {
				
				if (_logger.isDebugEnabled()) {
					_logger.debug("handleMessage(): Sending ACK to subsystem: "
							+ message);
				}
				AppDataMessage ack = new AppDataMessage(
						AppDataMessage.MESSAGE_OUT);
				ack.setSenderId(subSystem.getId());
				ack.setMsgType(AppDataMessage.ACK);
				this.sendAppChainDataSyncMessage(ack);
			} catch (Exception e) {
				_logger.error(
						"Error occured while sending AppSync-ACK message", e);
			}
		}
		
	}
	
	
	/**
	 * this method is calld to send updatd chain data to peer 
	 * @param callId
	 * @param currentServicesStack
	 * @param allTriggredServices
	 */
	private void sendUpdatedDataToPeer(String callId,Stack<String> currentServicesStack,Set<String> allTriggredServices){
		
		try {
			
			if(_logger.isDebugEnabled()){
    			_logger.debug("sendUpdatedDataToPeer........."+callId+" currentServicesStack: "+ currentServicesStack+" allTriggredServices "+ allTriggredServices);
    		}
			
			AppDataMessage ack = new AppDataMessage(
					AppDataMessage.MESSAGE_OUT);
			ack.setSenderId(subSystem.getId());
			ack.setMsgType(AppDataMessage.APP_CHAIN_MAP_UPDATED);
			ack.setCallId(callId);

			if (currentServicesStack != null && allTriggredServices != null) {
				
				Iterator<String> services = currentServicesStack.iterator();

				String serviceStack = "";
				while(services.hasNext()){
					serviceStack = serviceStack+ ","+services.next();
				}
				if (serviceStack.startsWith(",")) {
					serviceStack = serviceStack.substring(1,
							(serviceStack.length()));
				}
				
				System.out.println("Currents service stack is ........"+serviceStack);

				String allTrigServices = "";
				for (String s : allTriggredServices) {
					allTrigServices = allTrigServices+  ","+ s;
				}
				

				if (allTrigServices.startsWith(",")) {
					allTrigServices = allTrigServices.substring(1,
							(allTrigServices.length()));
				}
				
				if(_logger.isDebugEnabled()){
	    			_logger.debug("sendUpdatedDataToPeer........."+callId+" currentServicesStack: "+ serviceStack);
	    		}
				
				if(_logger.isDebugEnabled()){
	    			_logger.debug("sendUpdatedDataToPeer........."+callId+" allTrigServices: "+ allTrigServices);
	    		}
				
				ack.setCurrentChainedServices(serviceStack);

				ack.setAllTriggredServices(allTrigServices);
					
			}
			
			this.sendAppChainDataSyncMessage(ack);
		} catch (Exception e) {
			_logger.error(
					"Error occured while sending udate app chain data  message", e);
		}
		
		
	}

	/**
	 * This method is used to send app chain data to peer
	 * @param message
	 */
	private void sendAppChainDataSyncMessage(AppDataMessage message) {
		 try {
			 
			 if(_logger.isDebugEnabled()){
	    			_logger.debug("sendAppChainDataSyncMessage........."+message);
	    		}
	        //	ControlChannelProvider channel = this.manager.getControlChannel();
	        	
//	        	if(dataChannelProvider != null){
//	        		dataChannelProvider.send(message);
//	        	}else{
//	        		if(_logger.isDebugEnabled()){
//	        			_logger.debug("Not sending the APP Sync Message since the data Channel is NOT AVAILABLE");
//	        		}
//	        	}
	        } catch (Exception e) {
	            _logger.error(e.toString(), e);
	            throw new RuntimeException(e.toString());
	        }
	}

	@Override
	public void handleDataMessage(PeerMessage msg) {
		 if (msg instanceof AppDataMessage) {
	            
             handleMessage((AppDataMessage)msg);
          }
		
	}
		
	@Override
	public boolean removeTriggeredServices(String currentServiceId,
			SipApplicationSession sipAppSession, TcapSession tcapSession) {
		if(_logger.isDebugEnabled()) {
			_logger.debug("inside removeTriggeredServices with currentServiceId : " + currentServiceId);
		}
		boolean isRemoved = false;
		try {
			CallData callData = null;
			String callId = null;
			
			if(tcapSession != null) {
				callData = (CallData) tcapSession.getAttribute(CallData.CALL_DATA);
				callId = tcapSession.getDialogueId() + "";
			}else if(sipAppSession != null) {
				callData = (CallData) sipAppSession.getAttribute(CallData.CALL_DATA);
				callId = (String) callData.get(CallDataAttribute.P_ORIG_LEG_CALL_ID);
			}
			
			Set<String> triggeredServices = alltriggredServicesMap.get(callId);
			if(_logger.isDebugEnabled()) {
				_logger.debug("triggeredServices corresponding to callId : " + callId + " are : " + triggeredServices);
			}
			
			isRemoved = alltriggredServicesMap.remove(callId) != null ? true : false;
			currentServicesStackMap.remove(callId);
		}catch (Exception ex) {
			_logger.error("exception occured while removeTriggeredServices", ex);
		}
		
		return isRemoved;
	}
	}


