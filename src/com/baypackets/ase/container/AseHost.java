/*
 * AseHost.java
 *
 * Created on August 6, 2004, 4:51 PM
 */
package com.baypackets.ase.container;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseBackgroundProcessor;
import com.baypackets.ase.common.AseBaseConnector;
import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.AseMessage;
import com.baypackets.ase.common.BackgroundProcessListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.container.MonitoredIP.MonitorActionType;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.control.ControlManager;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.RoleChangeListener;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.dispatcher.Destination;
import com.baypackets.ase.dispatcher.Dispatcher;
import com.baypackets.ase.dispatcher.DispatcherImpl;
import com.baypackets.ase.measurement.AseMeasurementUtil;
import com.baypackets.ase.ocm.OverloadControlManager;
import com.baypackets.ase.sipconnector.AseNsepMessageHandler;
import com.baypackets.ase.sipconnector.AseSipConstants;
import com.baypackets.ase.sipconnector.AseSipServletMessage;
import com.baypackets.ase.sipconnector.AseSipServletRequest;
import com.baypackets.ase.sipconnector.AseSipSession;
import com.baypackets.ase.spi.container.SasApplicationSession;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasMessageProcessor;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.util.AsePing;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseThreadMonitor;
import com.baypackets.ase.util.AseTraceService;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.CallTraceService;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.LogDirChecker;
import com.baypackets.ase.util.PrintInfoHandler;
import com.baypackets.ase.util.StackDumpLogger;
import com.baypackets.ase.util.StringManager;
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
import com.baypackets.emsagent.EmsAgent;
import com.baypackets.emsagent.SoftShutdownInterface;

/**
 * An instance of this class provides a container for deploying and managing
 * Servlet applications.
 *
 * @see com.baypackets.ase.container.AseContext
 */
public class AseHost extends AseBaseContainer
    implements MComponent,  RoleChangeListener, ThreadOwner, SasMessageProcessor , BackgroundProcessListener,SoftShutdownInterface{//AppSyncMessageListener
	private static final long serialVersionUID = -3814634264647849724L;
    private static Logger _logger = Logger.getLogger(AseHost.class);
    private static StringManager _strings = StringManager.getInstance(AseHost.class.getPackage());
    private static long INVOCATION_ID_COUNTER = 0;
    private static File HOST_DIR = new File(Constants.ASE_HOME, Constants.FILE_HOST_DIR);

    private Map appSessionMap;
    private Map icMap;
    //private short _clusterRole = AseRoles.UNKNOWN;
    private Short _clusterRole = AseRoles.UNKNOWN;
    private String _subsysId;
    private ControlManager _controlMgr;
   // private VersionManager _versionMgr;
    private ClusterManager _clusterMgr;
    private EmsAgent _emsAgent;
    private Map _senders = new ConcurrentHashMap();
    private int repCtxtIdCounter = 0;

    private Dispatcher dispatcher = null;

    private OverloadControlManager m_ocm = null;
    private int m_ocmId;
    private AseEngine m_engine = null;

    private ThreadMonitor _threadMonitor = null;

    private ClassLoader latestSbbCL;
    
    private ClassLoader tcapProviderCL;
    
	private ClassLoader aseCL;
	
	private LogDirChecker dirChecker = null;
    
  //added for Inap call Sbtm
	//changed map type from string aseapplication session to string sipappsession to make it usable in service.
    private ConcurrentHashMap<String, SipApplicationSession> appSessionMapForInapDlgId ;
    private String DIALOGUE_ID = Constants.DIALOGUE_ID ;
    
    //Sumit@SBTM[
//    private static final String CORRELATION_ID_ATTRIBUTE="P-Correlation-ID";
//    private static final String TCAP_SESSION_ATTRIBUTE="Tcap-Session";
    //]Sumit@SBTM

    ConfigRepository configRepositary = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
	
    /**
     * Default constructor
     */
    public AseHost(){
        this(Constants.NAME_HOST);

        //
    	// Decide on the hash table size depending max active calls + 30000 ( on safe side )
        //

    	String maxActiveCalls = (String)configRepositary.getValue(Constants.OID_CONTENTION_LEVEL_THREE_ACTIVE_CALLS);
    	if(maxActiveCalls == null || maxActiveCalls.trim().isEmpty()){
    		maxActiveCalls="55000";
    	}
    	int maxActiveCallsInt = Integer.parseInt(maxActiveCalls) + 30000;

    	int tcapTabSize = (int) (maxActiveCallsInt*1);
    	if (_logger.isInfoEnabled()) {
    		_logger.info("In AseHost(): tabSize = " + maxActiveCallsInt + ", tcapTabSize = " + tcapTabSize);

    	}
    	appSessionMap = new ConcurrentHashMap(maxActiveCallsInt);
        appSessionMapForInapDlgId = new ConcurrentHashMap<String, SipApplicationSession>(tcapTabSize);
    	icMap = new ConcurrentHashMap(maxActiveCallsInt);
    	
    	String appDeployDir = BaseContext.getConfigRepository().getValue(
				Constants.PROP_APP_DEPLOY_DIR);
		
		if (appDeployDir != null && !appDeployDir.isEmpty()) {
			HOST_DIR = new File(appDeployDir);
		}
    }

    /**
     * Assigns the specified name to this AseHost instance.
     *
     * @param name  The unique name to associate with this host
     * @throws IllegalArgumentException if the given name is null
     */
    public AseHost(String name) {
        super(name);
    }


    /**
     * Performs initialization.
     */
    public void initialize() throws Exception {
        if (_logger.isEnabledFor(Level.INFO)) {
            _logger.info("initialize(): Initializing AseHost: " + this.getName());
        }

        m_engine = (AseEngine)Registry.lookup(Constants.NAME_ENGINE);
        m_engine.addChild(this);

        m_ocm = (OverloadControlManager) Registry.lookup(Constants.NAME_OC_MANAGER);
        m_ocmId = m_ocm.getParameterId(OverloadControlManager.APP_SESSION_COUNT);

        // Thread monitor stuff
        _threadMonitor = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);

        // Register with the ClusterManager so that we will be notified
        // whenever our role in the cluster has changed.
        _clusterMgr = (ClusterManager)Registry.lookup(Constants.NAME_CLUSTER_MGR);
        _clusterMgr.registerRoleChangeListener(this, Constants.RCL_HOST_PRIORITY);

        // Register with the VersionManager so that we can receive
        // AppSyncMessages from our cluster peers when an app is deployed.
//        _versionMgr = (VersionManager)Registry.lookup(Constants.NAME_VERSION_MGR);
//        _versionMgr.registerAppSyncMsgListener(this);

        _controlMgr = (ControlManager)Registry.lookup(Constants.NAME_CONTROL_MGR);
        
      //Register this object with EMSAgent so that a notification is received 
       //to check whether shutdown is allowed after Softshutdown command is given
        _emsAgent = BaseContext.getAgent();
        
        if(_emsAgent!=null){
        	_emsAgent.registerSoftShutdownInterface(this);
        }
        
        dispatcher = new DispatcherImpl();

        // Register the print handlers for the ICs
        PrintInfoHandler.instance().registerExternalCategory(Constants.CTG_ID_ACTIVE_CALLS, Constants.CTG_NAME_ACTIVE_CALLS, "", this.icMap);

        dirChecker = LogDirChecker.getInstance();
    // Registering for background Processes
    this.registerForBackgroundProcess();
        if (_logger.isEnabledFor(Level.INFO)) {
            _logger.info("initialize(): Successfully initialized AseHost.");
        }
    }


    /**
     * Sets this AseHost's parent.
     *
     * @param parent  Must be an instance of AseEngine or null.
     * @throws IllegalArgumentException if the given parent argument is not an
     * instance of AseEnine or null.
     * @see com.baypackets.ase.container.AseEngine
     */
    public void setParent(AseContainer parent) {
        if (!(parent instanceof AseEngine || parent == null)) {
            throw new IllegalArgumentException(_strings.getString("AseHost.invalidParent", getName()));
        }
        super.setParent(parent);
    }


    /**
     * Adds the specified child to this AseHost.
     *
     * @param child  Must be an instance of AseContext.
     * @thorws IllegalArgumentException if the given child is not an instance
     * AseContext.
     * @see com.baypackets.ase.container.AseContext
     */
    public void addChild(AseContainer child) {
        if (!(child instanceof DeployableObject)) {
            throw new IllegalArgumentException(_strings.getString("AseHost.invalidChild", getName()));
        }
    if (_logger.isDebugEnabled()) {
            _logger.debug("Adding child (" + child.getName() + ") to host: " + this.getName());
        }
        super.addChild(child);
    }


    /**
     * Returns the ID of the subsystem where this AseHost object resides.
     */
    public String getSubsystemId() {
        return _subsysId;
    }

    /**
     * Processes the given Servlet request and/or response objects.
     *
     * @param request  An object encapsulating a servlet request.
     * @param response  An object encapsulating a servlet response.
     */
    public void processMessage(SasMessage message)
                    throws AseInvocationFailedException, ServletException {

        SasProtocolSession protocolSession = null;
        AseApplicationSession appSession = null;
        AseIc invocationContext = null;
        Destination destination = null;

        //      if(_logger.isEnabledFor(Level.INFO)){
        //          _logger.info("Processing the mesasge :"+message.getDestination()+"*********\n");
        //          }

        try{
            //Check either one of the request or response is NOT NULL
            if(message == null){
                _logger.error("Message should be Not NULL , Message =" + message);
                throw new AseInvocationFailedException(_strings.getString("AseHost.nullRequestResponse"));
            }

            //Get the protocol Session. Do not create session here if not already present.
            protocolSession = message.getProtocolSession();

            //Validate the protocol and application sessions for RESPONSE/subsequent REQUEST
            if(!message.isInitial() && protocolSession != null){

                //Get the application session object...
                //Check whether the app session is NOT NULL...
                appSession = (AseApplicationSession)protocolSession.getApplicationSession();
                if(appSession == null){
                    _logger.error("Application session is null for message");
                    throw new AseInvocationFailedException(_strings.getString("AseHost.nullApplicationSession"));
                }

                //Call the handleMessage on the protocolSession
                if(_logger.isEnabledFor(Level.INFO)){
                    _logger.info("Processing the mesasge for :"+protocolSession);
                }

                //Get the handler from the protocol Session and associate with this message.
                message.setHandler(protocolSession.getHandler());
                protocolSession.handleMessage(message);
                return;
            }
            
            //added null check on dlg id to allow SIP/SIPT calls --- saneja
            //Added for SS7 call Handling
            SipApplicationSession appSession1=null;
            AseSipServletRequest  aseMessage = (AseSipServletRequest)message;
            
			 //@reeta changed for WIN getting billingid will be used instaed dialogue id 
            // and will work as dialogue id for win
            
            String dlgId =(String)aseMessage.getHeader(Constants.TC_CORR_ID_HEADER);
    		
    		if (dlgId == null)
    			 dlgId = (String)aseMessage.getHeader(Constants.DIALOGUE_ID);
            
            if(dlgId!=null)
            	appSession1 = appSessionMapForInapDlgId.get(dlgId);
            if(appSession1 != null){
			if (_logger.isDebugEnabled()) {

            	 _logger.debug(dlgId+"::got the appSession for tcap call");
			}
            	Iterator it = appSession1.getSessions();
            	SasProtocolSession protoSession = null ;
            	while(it.hasNext()){
            		 protoSession = (SasProtocolSession)it.next();
            		 if (protoSession.getProtocol().equals(AseStrings.SIP)){
            			 break;
            		 }
                         protoSession = null;
            	}
            	if(protoSession == null){
                    _logger.warn(dlgId+"::protoSession is null for message");
                    //since sip session is not found new sip session will be create by approuter for notify.
				} else {
					//Call the handleMessage on the protocolSession
					if (_logger.isEnabledFor(Level.INFO)) {
						_logger.info("Processing the mesasge for :" + protoSession);
					}
					if (protoSession.getProtocolSessionState() == 0
									|| protoSession.getProtocolSessionState() == 2) {
						protoSession.setProtocolSessionState(1);
						if (_logger.isInfoEnabled()) {

						_logger.info("set the state of protocol session :");
					}
					}
					if (_logger.isEnabledFor(Level.INFO)) {
						_logger.info("set the state of protocol session :"
										+ protoSession.getProtocolSessionState());
					//Get the handler from the protocol Session and associate with this message.
					_logger.info("set the state of protocol session handler:"
									+ protoSession.getHandler());
					}

					
					aseMessage.setAseSipSession((AseSipSession) protoSession);
					aseMessage.setInitial(false);
					if (_logger.isEnabledFor(Level.INFO)) {

					_logger.info("set initial message false @@@@:" + protoSession.getHandler());
					}
					message.setHandler(protoSession.getHandler());
					protoSession.handleMessage(message);
					return;
                }//end if proto session
                
            }//end if appsession

            //Process the initial request
            if (_logger.isEnabledFor(Level.INFO)){
                _logger.info("Processing the initial request ....");
            }

            //decode the URI to get the application session
            appSession = this.decodeAppSession(message);

            if(appSession != null){
                //Found the application session using the encoded URI,
                //Use it for invoking application also skip the regular rule matching.
                if(_logger.isInfoEnabled()){
                    _logger.info("Got the appSession using the encoded URI. So going to route this request to specified app.");
                }

                //If the request is a Loopback, then rule matching would have been done already.
                //So get the destination from the last matched rule.
                if(message.isLoopback()){
                    invocationContext = this.getLoopbackIC(message);

                    if(invocationContext == null){
                        throw new AseInvocationFailedException("Unable to get the Invocation context for looped back request");
                    }

                    // Bpind 17365 destination = invocationContext.getLastDestination();
                    destination = (Destination )message.getDestination();
                    if(_logger.isInfoEnabled()){
                    	_logger.info("Host:the destination loop back is==>"+destination);
                    }
                }else{   //JSR289.36
                	//Get the destination for this invocation 
                	if (message instanceof AseSipServletRequest)
                		((AseSipServletRequest)message).setTargeted();
                	destination = new Destination();
                	destination.setAppName(appSession.getApplicationName());
                	if(_logger.isInfoEnabled())
                		_logger.info("Request targeted for Application Name: "+ destination.getApplicationName());
                	destination = dispatcher.getDestination(message, destination, this);
                	AseContext ctx = appSession.getContext();
                	if(ctx != null) {
                		if(null == destination || destination.getApplicationName() != ctx.getId()) {
                			throw new AseInvocationFailedException("Application Router could not find the application name");
                		}
                	} else {
                		throw new AseInvocationFailedException("Not able to find the application context");
                	}
                }
            }else{
                //The URI is not encoded (OR) the encoded Application Session timed out...
                //So now check whether it is a looped back request or a direct request from N/W
                if(message.isLoopback()){

                    //Request is a looped back request
                    //So skip the rule matching, use the last matched rule that we did before sending this request out.
                    if (_logger.isEnabledFor(Level.INFO)) {

                    _logger.info("Request is loopback. Will use the last matched rule here...");
				}
                    //Get the IC id from the request and get the IC using that
                    invocationContext = this.getLoopbackIC(message);

                    if(invocationContext == null){
                        throw new AseInvocationFailedException("Unable to get Invocation context for Loopback request");
                    }

                     //BpInd 17365 destination = invocationContext.getLastDestination();
                     destination = (Destination )message.getDestination();

                if(_logger.isInfoEnabled()){
                    _logger.info("AHost:the destination loop back is==>"+destination);
                }
                }else{

                    //Request is not loopback, it came from outside, so do a Rule matching and findout
                    //the application to be triggered...
			if (_logger.isEnabledFor(Level.INFO)) {

                    _logger.info("Request is not loopback. Going to do a Rule Matching to find a matching application...");
				}
					destination = this.doRuleMatching(message, destination);
					if(destination == null){
						if(_logger.isInfoEnabled()){
							_logger.info("Got a PROCESSING_OVER from dispatcher, so completing processing for this request...");
						}
						return;
					} else if (destination.getStatus() == Dispatcher.EXTERNAL_ROUTE) {
						if (message.getProtocol().startsWith(AseStrings.SIP)) {
							int reqSrc=((AseSipServletRequest)message).getSource();
							if(reqSrc ==AseSipConstants.SRC_NETWORK){
								AseMeasurementUtil.counterAppNotFound.increment();
								//saneja@bug11987 [
								//decrease active call count here
								//active call decrement happens on appsession invalidation;
								//since appsesion is not created in this case decrementing count here.
								//TODO:Puneet is this required for NSEP?I also see increasing this 
								//param during assoicaite appsesion called after this..so is this too ealry to decremnt?? 
								if(_logger.isDebugEnabled()){
			                        _logger.debug("APP_NOT_FOUND,Decrease OCMP param id:"+m_ocmId+ " for callid"+aseMessage.getCallId());
			                    }
								m_ocm.decrease(m_ocmId);
								//]closed saneja@bug11987
							}
							
							if(_logger.isInfoEnabled()){
								_logger.info("Got EXTERNAL_ROUTE, on initial external request proxy to SIP connector");
							}
							Iterator i = getConnectors();
							while (i.hasNext()) {
								AseBaseConnector connector = (AseBaseConnector)i.next();
								if (connector.getProtocol().startsWith(AseStrings.SIP)) {
									AseMessage msg = null;
									boolean pMsg = false;
									if(AseUtils.getCallPrioritySupport() == 1) {
										pMsg = AseNsepMessageHandler.getMessagePriority((AseSipServletRequest)message);
									}
									msg =  new AseMessage(message, pMsg);
									connector.handleMessage(msg);
									//Fix for LEV-2089
									//When no rule is configured for a request in Application Router
									//and External Route is set, then SipSession is created and Active
									//SipSession count is incremented but since appsession is not created
									//the active session count is not decremented
									
									AseMeasurementUtil.counterActiveSIPSessions.decrement();
								}
							}
						}
						// If not a SIP call, or no connector found then same behavior as PROCESSING_OVER
						return;
					}
                }
            }
            
            //saneja Refactored for BUG 7084[
            //saneja@BUG 6794 changes:::: Tcapsession Fetching[
            AseSipServletRequest aseRequest = (AseSipServletRequest) message;
            String corrId=(String) aseRequest.getAttribute(Constants.CORRELATION_ID_ATTRIBUTE);
            Object corrObject=null;
            boolean isAppSession=false;
            if(corrId!=null){
            	if(_logger.isDebugEnabled()){
            		_logger.debug("Correlated, checking correltaed Session for corrid::["+corrId+"]");
            	}
            	AseContext aseContext = (AseContext) this.findChild(destination.getApplicationName());
                if (aseContext == null){
                    _logger.error("Not able to find the application context for :" + destination.getApplicationName());
                    throw new AseInvocationFailedException(_strings.getString("AseHost.noAppContext", destination.getApplicationName()));
                }
                if(_logger.isDebugEnabled())
                	_logger.debug("Checking in AppContext :[" + aseContext+"]");
                String correlationId=corrId;
                Map<String,Object> corrMap=(Map<String,Object>) aseContext.getAttribute(Constants.CORRELATION_MAP_ATTRIBUTE);
                corrObject=corrMap.get(correlationId);
                if(corrObject==null){
                	if (message.getProtocol().startsWith(AseStrings.SIP)) {
						int reqSrc=((AseSipServletRequest)message).getSource();
						//saneja@bug11987 [
						//decrease active call count here
						//This is case where correlation call but correlation object not found so clearing counters
						if(reqSrc ==AseSipConstants.SRC_NETWORK){
							AseMeasurementUtil.counterAppNotFound.increment();
							if(_logger.isDebugEnabled()){
		                        _logger.debug("Correlated Object Not found,Decrease OCMP param id:"+m_ocmId+ " for callid"+aseMessage.getCallId());
		                    }
							m_ocm.decrease(m_ocmId);
							
						}//]closed saneja@bug11987
						if(_logger.isInfoEnabled()){
							_logger.info("Unable to find Correlated Session for SIPT request");
						}
						Iterator i = getConnectors();
						while (i.hasNext()) {
							AseBaseConnector connector = (AseBaseConnector)i.next();
							if (connector.getProtocol().startsWith(AseStrings.SIP)) {
								AseMessage msg = null;
								boolean pMsg = false;
								if(AseUtils.getCallPrioritySupport() == 1) {
									pMsg = AseNsepMessageHandler.getMessagePriority((AseSipServletRequest)message);
								}
								msg =  new AseMessage(message, pMsg);
								connector.handleMessage(msg);
								_logger.error("Sending 503 for Correlation Id : " +correlationId);
							}
						}
						// If no connector found then same behavior as PROCESSING_OVER	
						return;
					}else{
						if(_logger.isInfoEnabled())
							_logger.info("corrObject  Not found for correlated request, CorrId::["+corrId+
                			"]  AppName:::["+destination.getApplicationName()+"] Protocol:::["+message.getProtocol()+"]");
						return;
					}
                }
                if(_logger.isDebugEnabled())
                	_logger.debug("corrObject found Continue correlation");
                //checking if appSession then do targetting saneja@bug 7084[
                if(corrObject instanceof AseApplicationSession){
                	if(_logger.isDebugEnabled())
                		_logger.debug("SIPT-Correlattion, Found Appsession");
                	appSession=(AseApplicationSession) corrObject;
                	//saneja@bug11987 [
					//decrease active call count here
					//In case of SIP-SIP correlation active call count is incrmented twice.
                	//one for orignal INVITE request and second for correlated INVITE request.
                	//But decremented only once at appsession invalidation.
                	//so decrementing the counter here
                	if(_logger.isDebugEnabled()){
                        _logger.debug("SIP_T correlation,Decrease OCMP param id:"+m_ocmId+ " for callid"+aseMessage.getCallId());
                    }
                	m_ocm.decrease(m_ocmId);
					//]closed saneja@bug11987
                	isAppSession=true;
                }
                //]closed saneja@bug 7084
            }
            //]saneja@BUG 6794 changes SessionTargetting closed
            //]closed refactoring bug 7084

            //Create the protocol session, if it is not yet created.
            protocolSession = (protocolSession == null) ? message.getProtocolSession(true) : protocolSession;

            //Process the Sessionless Messages.....
            if(protocolSession == null){

                if(_logger.isDebugEnabled()){
                    _logger.debug("Handling the Sessionless Message...");
                }

                //Doing a NULL check on the Application Context
                AseContext ctx = (AseContext) this.findChild(destination.getApplicationName());
                if (ctx == null){
                    _logger.error("Not able to find the application for :" + destination);
                    throw new AseInvocationFailedException(_strings.getString("AseHost.noAppContext", destination.getApplicationName()));
                }

                //Process the Message....
                message.setHandler(destination.getServletName());
                ctx.processMessage(message);
                if(_logger.isDebugEnabled()){
                    _logger.debug("Completed processing the Sessionless Message...");
                }
                return;
            }
            
            //done to target NOTIFY after FT to same appsession
            if(appSession1!=null){
            	if(_logger.isDebugEnabled()){
            		_logger.debug(dlgId+"Reuse old appsession");
            	}
            	appSession = (AseApplicationSession)appSession1;
            }
            
            if(appSession == null){
                //Associate the application with this request.
                //This method will create a new invocation context, if it is NULL...
                //Create an application Session and associate with this IC.
                if(_logger.isDebugEnabled())
                {
                    _logger.debug("Creating a new app session,new ic...");
                }
                appSession = this.associateAppSession(message, invocationContext, destination);
                //Setting OrigLeg Call ID in Application Session.
                appSession.setAttribute(Constants.ORIG_CALL_ID, aseMessage.getCallId());
                if (AseSipServletMessage.class.isInstance(message)){
                	Object matches = ((AseSipServletMessage)message).getAttribute(Constants.MATCHES_CALL_CRITERIA);
                	if (matches != null){
                		appSession.setAttribute(Constants.MATCHES_CALL_CRITERIA, matches);
                		if (matches instanceof Boolean){
                			if(((Boolean)matches).booleanValue()){
                				if(_logger.isDebugEnabled()){                     
                                	_logger.debug("Setting TRACE_KEY in app session...");
                                }
                				appSession.setAttribute(Constants.TRACE_KEY, aseMessage.getCallId());
                			}
                		}
                	}
                	ArrayList list = (ArrayList) ((AseSipServletMessage)message).getAttribute(Constants.MATCHING_CONSTRAINT);
                	if (list != null){
                		appSession.setAttribute(Constants.MATCHING_CONSTRAINT, list);
                	}
                		
                }
                if(_logger.isDebugEnabled()){                     
                	_logger.debug("AseHost:appsession ,destination==>"+((Destination)appSession.getDestination()));
                }
            }
            
          //added for inap sbtm
            if(dlgId != null){
			if (_logger.isEnabledFor(Level.DEBUG)) {

            	_logger.debug("dlgId is not null : "+ dlgId);
			}
            	if(appSessionMapForInapDlgId.get(dlgId) == null){
            		appSessionMapForInapDlgId.put(dlgId, appSession);
            		appSession.setAttribute(DIALOGUE_ID, dlgId);
				if (_logger.isEnabledFor(Level.DEBUG)) {

            		_logger.debug("putting in the appSessionMapForInapDlgId appsession:" + appSession);
            	}
            	}
            	
            }

            //saneja Refactored for BUG 7084[
            //saneja@BUG 6794 changes:::: Tcapsession adding[
            if(corrObject!=null && !isAppSession){
            	if(_logger.isDebugEnabled()){
            		_logger.debug("Tcap Session found adding same to appSession");
            	}
            	appSession.setAttribute(Constants.TCAP_SESSION_ATTRIBUTE, corrObject);
            }
            //]saneja@BUG 6794 changes SessionTargetting closed]
            //]closed refactoring bug 7084
            
            //Now associate this protocol session with the application session.
            protocolSession = this.associateProtocolSession(message, appSession);

            //Call handle request on the protocol session
            if(_logger.isEnabledFor(Level.INFO)){
                _logger.info("Calling handlRequest for initial request on Protocol Session :" + protocolSession);
            }

            //Set the handler in the request object.
            //And call the handleMessage on the protocol Session.
            message.setHandler(destination.getServletName());
            protocolSession.handleMessage(message);
        }catch(ServletException se){
            _logger.error(""+se, se);
            throw new AseInvocationFailedException("" + se);
        }catch(AseInvocationFailedException ase){
            _logger.error(""+ase, ase);
            throw ase;
        }catch(Throwable t){
            //Just printing the stack trace (if any other exception occurs)
            _logger.error(""+t, t);
            throw new AseInvocationFailedException("" + t);
        }finally{
            //Log a message before exiting.
            if (_logger.isEnabledFor(Level.INFO)){
                _logger.info("Completed the processing of request/response for :" + protocolSession);
            }
        }
    }

    
    
    void checkForTcapRequest(AseSipServletRequest  request){
    	if (_logger.isEnabledFor(Level.INFO)){
            _logger.info("Checking request for Tcap");
        }
    	String dlgId = (String)request.getHeader(Constants.TC_CORR_ID_HEADER);
    	if(dlgId==null)
    		dlgId = (String)request.getHeader(Constants.DIALOGUE_ID);
    	
    	SipApplicationSession appSession= request.getApplicationSession();
    	_logger.debug("checkForTcapRequest() dlgId is : "+ dlgId  + " && Appsession is::"+appSession);
    	 if(dlgId != null && appSession !=null){
         	_logger.debug("checkForTcapRequest() dlgId is not null : "+ dlgId);
         	if(appSessionMapForInapDlgId.get(dlgId) == null){
         		appSessionMapForInapDlgId.put(dlgId,appSession);
         		appSession.setAttribute(DIALOGUE_ID, dlgId);
         		_logger.debug("checkForTcapRequest() putting in the appSessionMapForInapDlgId appsession:" + appSession);
         	}
         	
         }
    	
    }
    private AseIc getLoopbackIC(SasMessage message){
        AseIc invocationContext = null;
        SasMessage loopbackSource = message.getLoopbackSourceMessage();
        SipApplicationSessionImpl appSession =  (loopbackSource != null ) ? (SipApplicationSessionImpl)loopbackSource.getApplicationSession() : null;
        invocationContext = appSession != null ? appSession.getIc() : null;
        return invocationContext;
    }

    public AseApplicationSession decodeAppSession(SasMessage request) throws AseInvocationFailedException{
        AseApplicationSession appSession = null;

        String appSessionId = request.decode();
        appSession = (appSessionId != null) ? (AseApplicationSession) this.appSessionMap.get(appSessionId) : null;

        if(appSessionId == null){
            if(_logger.isInfoEnabled()){
                _logger.info("No encoded URI. So will follow the normal route...");
            }
        }else if(appSession == null){
            if(_logger.isInfoEnabled()){
                _logger.info("Unable to get the specified application session:" +  appSessionId +" It might be invalidated. Will follow the normal route....");
            }
        }else{
            if(_logger.isInfoEnabled()){
                _logger.info("Found the specified application session :" +appSessionId +" Will associate this request with the specified app...");
            }
        }
        return appSession;
    }

    Destination doRuleMatching(SasMessage request, Destination destination) throws AseInvocationFailedException{

        //Get the dispatcher and do a null check on it
        if(this.dispatcher == null){
            _logger.error("request.getDispatcher() == NULL");
            throw new AseInvocationFailedException(_strings.getString("AseHost.nullDispatcher"));
        }

        if(destination == null){
            //Create a destination object for passing to the Dispatcher.
            destination = new Destination();

            //Set the invocation id for this destination
            //This invocation Id will be used for detecting the LOOP by the dispatcher
            //This invocation ID will be same for all the loopback requests
            long invocationId = ++INVOCATION_ID_COUNTER;
            destination.setInvocationId(invocationId);
            if(_logger.isEnabledFor(Level.INFO)){
                _logger.info("Invocation ID for this invocation :"+invocationId);
            }
        }

        // Call the dispatcher to find the destination.
        destination = dispatcher.getDestination(request, destination, this);
        if (_logger.isEnabledFor(Level.INFO)){
            _logger.info("Dispatcher return status :" + destination.getStatus() + "," + destination);
        }

        //For now thre will be no PROCESSING_OVER
        //In the next phase, we may have to do something specific
        //for the processing over.
        if (destination.getStatus() == Dispatcher.PROCESSING_OVER){
            if (_logger.isEnabledFor(Level.INFO)){
                _logger.info("Dispatcher returned a PROCESSING_OVER. So not doing anything :" + destination);
            }
            return null;
        }

        // Throw exception, if the dispatcher does not return any destination
        if(destination.getStatus() == Dispatcher.NO_DESTINATION_FOUND)
        {
            traceRequest(request);

            if( !(request instanceof AseSipServletMessage)
            || ((AseSipServletMessage)request).getSource() == AseSipConstants.SRC_NETWORK) {
                //Increment the counter for the no application triggered.
                AseMeasurementUtil.counterAppNotFound.increment();
            }

            if(_logger.isDebugEnabled()) {
                _logger.debug("Dispatcher returned a NO_DESTINATION_FOUND. So throwing an exception :" + destination);
            }
            throw new AseInvocationFailedException(_strings.getString("AseHost.noDestination"), AseMessage.NO_DESTINATION_FOUND);
        }

        // Throw an exception, if the dispatcher has found a loop.
        if(destination.getStatus() == Dispatcher.LOOP_DETECTED){
            _logger.error("Dispatcher returned a LOOP_DETECTED. So throwing an exception :" + destination);
            throw new AseInvocationFailedException(_strings.getString("AseHost.loopDetected"), AseMessage.LOOP_DETECTED);
        }

        return destination;
    }

    private AseApplicationSession associateAppSession(SasMessage request, AseIc invocationContext, Destination destination)
                                                            throws AseInvocationFailedException{

        //Get the application context for the found application.
        if(_logger.isEnabledFor(Level.INFO)){
            _logger.info("Finding the deployed application for :"+destination);
        }
        AseContext ctx = (AseContext) this.findChild(destination.getApplicationName());

        //Doing a NULL check on the Application Context
        if (ctx == null){
            _logger.error("Not able to find the application for :" + destination);
            throw new AseInvocationFailedException(_strings.getString("AseHost.noAppContext", destination.getApplicationName()));
        }

        // Create the appsession and associate the IC, appSession, lastDestination
        if(_logger.isEnabledFor(Level.INFO)){
            _logger.info("Associating app context, app session , IC for :" + destination);
        }

        //Get the protocol session and do a NULL check...
        SasProtocolSession protocolSession = request.getProtocolSession();
        if (protocolSession == null){
            _logger.error("Not able to find the protocolSession");
            throw new AseInvocationFailedException(_strings.getString("AseHost.nullProtocolSession"));
        }

        //JSR 289.42
        String sessionId=createSesIdIfReqd(ctx,request);
      //Create the application session and associate with the IC.
        AseApplicationSession appSession = ctx.createApplicationSession(protocolSession.getProtocol(), invocationContext,sessionId);
        //JSR 289.42
        appSession.setInitialPriorityStatus(request.getMessagePriority());
        appSession.setPriorityStatus(request.getMessagePriority());

		ConfigRepository cr = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		
		String ingwMessageQueue = (String)cr.getValue(Constants.INGW_MSG_QUEUE);
		String nsepIngwPriority = (String)cr.getValue(Constants.NSEP_INGW_PRIORITY);
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("Messages from INGw will come in " + ingwMessageQueue + " queue");
			_logger.debug("All call prioirty support with INGw messages coming in priority queue " + nsepIngwPriority);
		}

        if (m_engine.isCallPriorityEnabled() && (!ingwMessageQueue.equals(Constants.INGW_MSG_PRIORITY_QUEUE) || nsepIngwPriority.equals(AseStrings.TRUE_SMALL)) && ((AseSipServletMessage)request).getMessagePriority()) {
            m_ocm.increaseNSEP(m_ocmId);
        } else {
            m_ocm.increase(m_ocmId);
        }

        if(invocationContext == null){
            //BpInd17365
            if(request.getDestination()==null)
                request.setDestination(destination);
            //appSession.getIc().setLastDestination(destination);
		if (_logger.isDebugEnabled()) {

            _logger.debug(" INVOCATION CONTEXT IS NULL ");
		}
            // PRASHANT
            // setting queue number in invocation context
            int queuenumber = request.getWorkQueue();

            if (_logger.isDebugEnabled()) {
                _logger.debug("Setting queue number to " + queuenumber + " in IC.");
            }
            appSession.getIc().setWorkQueue( queuenumber);
        }
        
        if(request.isInitial() && ((AseStrings.TRUE_CAPS).equals(((AseSipServletRequest)request).getAttribute("SELECTIVE MESSAGE LOGGING")))){
        	appSession.setAttribute("SELECTIVE MESSAGE LOGGING", AseStrings.TRUE_CAPS);
        }else{
        	appSession.setAttribute("SELECTIVE MESSAGE LOGGING", AseStrings.FALSE_CAPS);
        }
        
        appSession.setDestination(destination);
        return appSession;
    }

    
    /**
     * JSR 289.42
     * This  method creates sessionId from method defined with @SipApplicationKey 
     * annotation in the application
     * @param ctx
     * @param sasMessage
     * @return
     */
	private String createSesIdIfReqd(AseContext ctx, SasMessage sasMessage) {
		
		final String UNDERSCORE="_";
		StringBuilder sessionId = null;
		AseAnnotationInfo aseAnnotationInfo = ctx.getApplicationKeyAnnoInfo();
		if (aseAnnotationInfo != null) {
			try {
				Class<?> clazz = aseAnnotationInfo.getAnnotatedClass();
				Method sipapplicationKeyMethod = clazz.getMethod(ctx
						.getApplicationKeyAnnoInfo().getMethodName(),
						SipServletRequest.class);
				AseSipServletRequest sipRequest = null;
				if (sasMessage instanceof AseSipServletRequest) {
					sipRequest = (AseSipServletRequest) sasMessage;
				} else {
					return "";
				}

				String id = (String) sipapplicationKeyMethod.invoke(null,
						sipRequest);
				ConfigRepository cr = (ConfigRepository) Registry
				.lookup(Constants.NAME_CONFIG_REPOSITORY);

				String ipAddressOfContainer = AseUtils.getIPAddress(cr.getValue(Constants.OID_BIND_ADDRESS));
				
				// session id generated is combination of
				// "Application Name+Version+idreturnedfromannotated method+ipAddressofthecontainer"
				// to make it unique among all the containers
				sessionId = new StringBuilder(ctx.getObjectName());
				sessionId.append(UNDERSCORE).append(ctx.getVersion()).append(
						UNDERSCORE).append(id).append(UNDERSCORE).append(
						ipAddressOfContainer);
				return sessionId.toString();
			
			} catch (SecurityException e) {
				 _logger.error(" SecurityException in method createSesIdIfReqd ",e);
			} catch (NoSuchMethodException e) {
				 _logger.error(" Invoked method not found in method createSesIdIfReqd ",e);
			} catch (IllegalArgumentException e) {
				 _logger.error("Illegal Argument Exception in method createSesIdIfReqd ",e);
			} catch (IllegalAccessException e) {
				 _logger.error("Illegal Access Exception in method createSesIdIfReqd",e); 
			} catch (InvocationTargetException e) {
				_logger.error("InvocationTargetException Exception in method createSesIdIfReqd",e); 
			}

		}
		return null;
	}

	private SasProtocolSession associateProtocolSession(SasMessage request, AseApplicationSession appSession)
                        throws AseInvocationFailedException{

        if(_logger.isInfoEnabled()){
            _logger.info("Associate protocol Session Called :" + appSession.contextName );
        }

        //Get the protocol session and do a NULL check...
        SasProtocolSession protocolSession = request.getProtocolSession();
        if (protocolSession == null){
            _logger.error("Not able to find the protocolSession");
            throw new AseInvocationFailedException(_strings.getString("AseHost.nullProtocolSession"));
        }

        //Associate the Protocol Session with the application Session
        if(_logger.isInfoEnabled()){
            _logger.info("Associating the application Session with the protocol Session");
        }
        appSession.addProtocolSession(protocolSession);

        return protocolSession;
    }


    /**
     * Called by the "invoke" method to log a warning message to the EMS call
     * trace console indicating that the given request did not trigger any
     * applications.
     */
    private void traceRequest(SasMessage request) {
        if (!(request instanceof SipServletMessage)) {
            return;
        }

        CallTraceService traceService = (CallTraceService)Registry.lookup(Constants.CALL_TRACE_SERVICE);

        if (!traceService.isEnabled()) {
            return;
        }

        if (traceService.matchesCriteria((SipServletMessage)request)) {
            String message = _strings.getString("AseHost.noDestFoundForRequest", request);
            traceService.trace((SipServletMessage)request, message);
        }
    }
    /**
     * This method is overridden from the AseBaseContainer class. It performs
     * the actions required to move this AseHost into a "running" state.
     */
    public synchronized void start() throws StartupFailedException {
        if (_logger.isEnabledFor(Level.INFO)) {
            _logger.info(_strings.getString("AseHost.start", getName()));
        }

        if(dirChecker.getLogDirCheckerThreadInterval() > 0){
        	dirChecker.start();
        }
        
        if (isRunning()) {
            return;
        }

        setRunning(true);
        if (_logger.isEnabledFor(Level.INFO)) {
            _logger.info(_strings.getString("AseHost.started", getName()));
        }
    }


    /**
     *
     */
    public void stop() throws ShutdownFailedException {
        try {
            this.destroyChildren();
        } catch(Exception exp) {
            _logger.error("Destroying children", exp);
        }
    }


    /**
     * Called by the "stop" method to destroy all application instances
     * on this host.
     */
    private void destroyChildren() throws Exception {
        AseContainer[] children = this.findChildren();

        if (children == null || children.length == 0) {
            return;
        }

        if (_logger.isEnabledFor(Level.INFO)) {
            _logger.info("Going to destroy applications for host: " + this.getName());
        }

        for (int i = 0; i < children.length; i++) {
            if(children[i] instanceof AseContext){
                AseContext context = (AseContext)children[i];
                context.destroyServlets();
            }
        }
    }


   public Iterator findByName(String name){
        ArrayList list = new ArrayList();
        AseContainer[] children = this.findChildren();
        for(int i=0; children != null && i<children.length;i++){
            AbstractDeployableObject ctx = (AbstractDeployableObject) children[i];
            if(ctx.getState() != DeployableObject.STATE_UNINSTALLED &&
                (name == null || name.equals(ctx.getName()))){
                list.add(ctx);
            }
        }

        return list.iterator();
    }

   public Iterator findByNamePrefix(String name){
        ArrayList list = new ArrayList();
        AseContainer[] children = this.findChildren();
        for(int i=0; children != null && i<children.length;i++){
            if(children[i] instanceof AseContext){
                AbstractDeployableObject ctx = (AbstractDeployableObject) children[i];
                if(ctx.getState() == DeployableObject.STATE_ACTIVE 
                		&& ctx.getObjectName().equals(name)){
                    list.add(ctx);
                }
            }
        }

        return list.iterator();
    }

   public Iterator findContextByNamePrefix(String name){
       ArrayList list = new ArrayList();
       AseContainer[] children = this.findChildren();
       for(int i=0; children != null && i<children.length;i++){
           if(children[i] instanceof AseContext){
               AbstractDeployableObject ctx = (AbstractDeployableObject) children[i];
               if (_logger.isEnabledFor(Level.INFO)) {
                   _logger.info("findContextByNamePrefix() context is..." + ctx + " State :"+ctx.getState() +" name "+ctx.getObjectName());
               }
               if(ctx.getState() != DeployableObject.STATE_UNINSTALLED
               		&& ctx.getObjectName().equals(name)){
                   list.add(ctx);
               }
           }
       }

       return list.iterator();
   } 
   
     public Iterator findAll(){
        return this.findByName(null);
    }

  
    public File getHostDir(){
        return HOST_DIR;
    }

    //added @Nitin-sbtm
    public ConcurrentHashMap<String, SipApplicationSession> getAppSessionMapForInapDlgId() {
		return appSessionMapForInapDlgId;
	}
    
    
    /**
     * Invoked by the "deploy" and "undeploy" methods to notify our cluster
     * peers of the app that was just deployed, upgraded or undeployed on this
     * host.  This is done by broadcasting AppSyncMessages to each peer at
     * regular intervals until an ACK is received from the ACTIVE node(s) in
     * the cluster.
     */
    public void startPing(DeployableObject app, boolean deployed) throws Exception {
        if (_logger.isEnabledFor(Level.INFO)) {
            _logger.info("startPing() called...");
        }

        synchronized (_clusterRole) {
        	if (_clusterRole == AseRoles.ACTIVE) {
        		if (_logger.isEnabledFor(Level.INFO)) {
        			_logger.info("startPing(): Current cluster role is ACTIVE, so nothing to do.");
        		}
        		return;
        	}
        }

        if (_logger.isEnabledFor(Level.INFO)) {
            _logger.info("startPing(): Current cluster role is STANDBY, so starting broadcast of AppSyncMessages to peers.");
        }

//        AppSyncMessage message = new AppSyncMessage(AppSyncMessage.MESSAGE_OUT);
//        message.setApplicationId(app.getId());
//
//        if (_subsysId == null) {
//            if (_logger.isEnabledFor(Level.INFO)) {
//                _logger.info("startPing():  Our subsystem ID is null, so obtaining subsystem ID from ControlManager.");
//            }
//            _subsysId = _controlMgr.getSelfInfo().getId();
//        }
//
//        message.setSenderId(_subsysId);
//        message.setMsgType(deployed ? AppSyncMessage.APP_DEPLOYED : AppSyncMessage.APP_NOT_DEPLOYED);
//
//        // Start a thread to send the AppSyncMessages at regular intervals.
//        MessageSender sender = new MessageSender(message);
//        sender.setThreadOwner(this);
//        _senders.put(app.getId(), sender);
//        sender.start();
    }

    private void printAppInfo(AseContext context)  {

        StringBuffer buffer = new StringBuffer();
        context.appendApplicationInfo(buffer);
        _logger.error(buffer.toString());
    }

    public void addApplicationSession(AseApplicationSession appSession){
		if (_logger.isInfoEnabled()) {
            _logger.info("Inside addApplicationSession with "+appSession);
        }
		if (_logger.isInfoEnabled()) {
            _logger.info("AppSession map size before adding is "+appSessionMap.size());
        }
        this.appSessionMap.put(appSession.getAppSessionId(), appSession);
		if (_logger.isInfoEnabled()) {
            _logger.info("AppSession map size after adding is "+appSessionMap.size());
        }
    }

    public void removeApplicationSession(String id){
		if (_logger.isInfoEnabled()) {
            _logger.info("Inside removeApplicationSession with "+id);
        }
		if (_logger.isInfoEnabled()) {
            _logger.info("AppSession map size before removing is "+appSessionMap.size());
        }
        this.appSessionMap.remove(id);
		if (_logger.isInfoEnabled()) {
            _logger.info("AppSession map size after removing is "+appSessionMap.size());
        }
    }

    public SasApplicationSession getApplicationSession(String id){
        return (SasApplicationSession) this.appSessionMap.get(id);
    }

    public void addIc(AseIc ic){
        if(ic != null){
		if (_logger.isInfoEnabled()) {
			_logger.info("AseIc map size before adding " +icMap.size());
		}
            this.icMap.put(ic.getId(), ic);
            if (_logger.isInfoEnabled()) {
                _logger.info("AseIc map size after adding " +icMap.size());
            }
        } else {
            _logger.error("addIc(): IC passed is null");
        }
    }

    public AseIc getIc(String id){
        return (AseIc)this.icMap.get(id);
    }

    public AseIc removeIc(String id){
		if (_logger.isInfoEnabled()) {
			_logger.info("AseIc map size before removing " +icMap.size());
		}
        AseIc ic = (AseIc)this.icMap.remove(id);
		if (_logger.isInfoEnabled()) {
			_logger.info("AseIc map size after removing " +icMap.size());
		}
        if (ic == null) {
            if (_logger.isInfoEnabled()) {
                _logger.info("AseIc is not found for" + id);
            }
        }
        return ic;
    }

	public void dumpICsWithoutActiveSipDialog(Logger p_logger) {
		synchronized(this.icMap) {
			p_logger.log (Level.OFF, "Going to dump calls without active dialog from total = " + this.icMap.size());
			Iterator iter = this.icMap.values().iterator();
			while (iter.hasNext()) {
				AseIc ic = (AseIc)iter.next();
				if (!ic.containsActiveSipDialog()) {
					p_logger.log (Level.OFF, "Call with no active dialogs: " + ic.getDiagnosticInfo());
				}
			}
		}
	}

    /**
     * This method is implemented from the MComponent interface and is called
     * by the EMS management application to update the state of this component.
     */
    public void changeState(MComponentState state) throws UnableToChangeStateException {
        try {
            if (state.getValue() == MComponentState.LOADED) {
                this.initialize();
            }
            if (state.getValue() == MComponentState.RUNNING){
                this.start();
            }
            if(state.getValue() == MComponentState.STOPPED){
                this.stop();
            }
    } catch(StartupFailedException ex)  {
        throw new UnableToChangeStateException(ex.getMessage());
        } catch(Exception e){
            _logger.error("changeState: ", e);
            throw new UnableToChangeStateException(e.getMessage());
        }
    }


    /**
     * This method is implemented from the MComponent interface and is called
     * by the EMS management application to update the configuration of this
     * component.
     */
    public void updateConfiguration(Pair[] configData, OperationType opType)
        throws UnableToUpdateConfigException {

        AseContainer[] children = this.findChildren();
        for(int i=0; children != null && i<children.length;i++){
            if(children[i] == null)
                continue;

            for(int j=0; configData != null && j<configData.length;j++){
                if(configData[j] == null)
                    continue;

                String name = (String)configData[j].getFirst();
                String value = (String)configData[j].getSecond();
                if(children[i] instanceof ResourceContextImpl){
                    ((ResourceContextImpl)children[i]).configurationChanged(name, value);
                }
            }
        }

    }


    /**
     * Implemented from AppSyncMessageListener and called by the VersionManager
     * class to process all incoming AppSyncMessages sent from our cluster
     * peers.
     *
     * @see com.baypackets.ase.control.VersionManager
     */
//    public void handleMessage(AppSyncMessage message) {
//        if (_logger.isEnabledFor(Level.INFO)) {
//            _logger.info("handleMessage(): Received AppSyncMessage from: " + message.getSenderId());
//        }
//
//        try {
//        	synchronized (_clusterRole) {
//        		if (_clusterRole == AseRoles.ACTIVE) {
//        			if (_logger.isEnabledFor(Level.INFO)) {
//        				_logger.info("handleMessage(): Our current cluster role is ACTIVE, so nothing to do.");
//        			}
//        			return;
//        		}
//        	}
//
//            if (message.getMsgType() != AppSyncMessage.ACK) {
//                if (_logger.isEnabledFor(Level.INFO)) {
//                    _logger.info("handleMessage(): Received message was not an ACK, so ignoring it.");
//                }
//                return;
//            }
//
//            String id = message.getApplicationId();
//
//            if (_logger.isEnabledFor(Level.INFO)) {
//                _logger.info("handleMessage(): Stopping message sender thread for application \"" + id );
//            }
//
//            MessageSender sender = (MessageSender)_senders.get(id);
//
//            if (sender != null) {
//                sender.kill();
//                _senders.remove(id);
//
//                if (_logger.isEnabledFor(Level.INFO)) {
//                    _logger.info("handleMessage(): Successfully stopped message sender thread.");
//                }
//            }
//        } catch (Exception e) {
//            _logger.error(e.toString(), e);
//            throw new RuntimeException(e.toString());
//        }
//    }


    /**
     * Implemented from RoleChangeListener and called by the ClusterManager
     * whenever our role in the cluster has changed (ex. from active to
     * standby).
     *
     * @see com.baypackets.ase.control.ClusterManager
     */
    public void roleChanged(String clusterId, PartitionInfo partitionInfo) {
        if (_logger.isEnabledFor(Level.INFO)) {
            _logger.info("roleChanged():  Changing cluster role to: " + partitionInfo.getRole());
            _logger.info("roleChanged():  Assigned subsystem ID is: " + partitionInfo.getSubsysId());
        }
        _subsysId = partitionInfo.getSubsysId();
        synchronized (_clusterRole) {
        	_clusterRole = partitionInfo.getRole();	
		}
		if (_clusterRole == AseRoles.ACTIVE){
			
			ConfigRepository config = (ConfigRepository) Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			String fipMonFreq = config.getValue(Constants.FIP_MONITOR_INTERVAL);
			String managedRefIP = config.getValue(Constants.MANAGED_REF_IP);
			String managedRefIPRetries = config.getValue(Constants.MANAGED_REF_IP_PING_RETRY);
			String managedRefIPFreq = config.getValue(Constants.MANAGED_REF_IP_PING_INTERVAL);
			String managedRefIPPingTimeout = config.getValue(Constants.MANAGED_REF_IP_PING_TIMEOUT);
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("fipMonFreq::<" + fipMonFreq + ">   managedRefIP::<" + managedRefIP
								+ ">  managedRefIPRetries::<" + managedRefIPRetries
								+ ">  managedRefIPFreq::<" + managedRefIPFreq 
								+ ">  managedRefIPPingTimeout::<"+managedRefIPPingTimeout+">");
			}
			
			
			List<MonitoredIP> ipList = new ArrayList<MonitoredIP>();
			
			String fips = partitionInfo.getFip();
			String[] fipList = fips.split(AseStrings.COMMA);
			for (int i=0; i < fipList.length; i++) {
				MonitoredIP fip = new MonitoredIP(fipList[i], MonitorActionType.PLUMB);
				fip.setRetries(1);
				if(fipMonFreq!=null && !fipMonFreq.trim().isEmpty()){
					try {
						fip.setFrequency(Long.parseLong(fipMonFreq));
					} catch (Exception e) {
						_logger.warn("FIP frequency not set properly::<"+fipMonFreq+">");
					}
				}
				ipList.add(fip);
			}
			
			MonitoredIP manageNwRefIp =null;
			if (managedRefIP != null) {
				manageNwRefIp = new MonitoredIP(managedRefIP,
								MonitorActionType.PING);
				if (managedRefIPFreq != null && !managedRefIPFreq.trim().isEmpty()) {
					try {
						manageNwRefIp.setFrequency(Long.parseLong(managedRefIPFreq));
					} catch (Exception e) {
						_logger.warn("managed IP Ping frequency not set properly::<"+managedRefIPFreq+">");
					}
				}//end if frquency
				
				if (managedRefIPRetries != null && !managedRefIPRetries.trim().isEmpty()) {
					try {
						manageNwRefIp.setRetries(Integer.parseInt(managedRefIPRetries));
					} catch (Exception e) {
						_logger.error("managed IP Ping retry not set properly::<"+managedRefIPRetries+">");
					}
				}//end if frquency
				
				if (managedRefIPPingTimeout != null && !managedRefIPPingTimeout.trim().isEmpty()) {
					try {
						manageNwRefIp.setTimeout(Integer.parseInt(managedRefIPPingTimeout));
					} catch (Exception e) {
						_logger.error("managed IP Ping retry not set properly::<"+managedRefIPPingTimeout+">");
					}
				}//end if frquency
			}else{
				_logger.warn("Managed N/w ref iP is null");
			}
			
			if(manageNwRefIp!=null){
				ipList.add(manageNwRefIp);
			}
			
			if (_logger.isDebugEnabled()) {
				_logger.debug("Ip list size::<"+ipList.size()+">");
			}
			
			if(_logger.isInfoEnabled())
				_logger.info("Role is active thus initiating the IP checker thread");
			try {
				AseIPChecker checker = new AseIPChecker(ipList);
	        checker.setThreadOwner(checker);
	        checker.start();
			} catch (Exception e) {
				_logger.info("Error starting Monitor Thread",e);
			}
		}
    }

	private class AseIPChecker extends MonitoredThread implements ThreadOwner{

		private ThreadOwner _threadOwner = null;
		private List<MonitoredIP> ipList = null;
				
		public int threadExpired(MonitoredThread thread) {
			_logger.error(thread.getName() + " expired");

			// Print the stack trace
			this.setThreadState(MonitoredThreadState.Expired);
			StackDumpLogger.logStackTraces();
			return ThreadOwner.CONTINUE;
		}

		public AseIPChecker(List<MonitoredIP> ipList) {
			super("AseIPChecker [AseHost] ", AseThreadMonitor.getThreadTimeoutTime(),
					(AseTraceService) Registry
							.lookup(Constants.NAME_TRACE_SERVICE));
			this.ipList = ipList;
			
		}

		public void run() {
			// Register thread with thread monitor
			try {
				// Set thread state to idle before registering
				this.setThreadState(MonitoredThreadState.Idle);
				this.setThreadPriority(5);

				_threadMonitor.registerThread(this);
			} catch (ThreadAlreadyRegisteredException exp) {
				_logger.error(
						"This thread is already registered with Thread Monitor",
						exp);
			}

			long sleepInterval =1;
			try {
				sleepInterval = getSleepInterval();
			} catch (Exception e1) {
				_logger.error("Error getting sleep interval using deafult as 1",e1);
			}
			
			try {
				while (true) {
					try {
						if (_logger.isInfoEnabled()) {
							_logger.info("AseIPChecker[AseHost].run(): Checking IPs ");
						}
						// Update time in thread monitor
						this.updateTimeStamp();

						// Set thread state to running before calling send
						this.setThreadState(MonitoredThreadState.Running);
						
						for(MonitoredIP ip:ipList){
							if(_logger.isDebugEnabled()){
								_logger.debug(ip);
							}
							if( !(ip.incrementAndCheckTicks(sleepInterval) ) ){
								if(_logger.isDebugEnabled()){
									_logger.debug("ip not eligible in current run");
								}
								continue;
							}
							switch(ip.getActionType()){
								case PLUMB:{
									checkPlumb(ip);
									break;
								}
								case PING:{
									checkPing(ip);
									break;
								}
							}//@end switch
							
						}//@end for
						
					Thread.sleep(sleepInterval * 1000);
					if(this.getThreadState() == MonitoredThreadState.Expired){
						_logger.error("AseIPChecker Thread Expired. Register Thread again with ThreadMonitor");
						try {
							_threadMonitor.registerThread(this);
						} catch(ThreadAlreadyRegisteredException exp) {
							_logger.error("This thread is already registered with Thread Monitor", exp);
						}
					}
					} catch (Exception e) {
						_logger.error(e.toString(), e);
					}//@end inner try catch
				}//@end while
			} finally {
				// Unregister thread with thread monitor
				_logger.error("INside finally for ip checker got exception");
				try {
					_threadMonitor.unregisterThread(this);
				} catch (ThreadNotRegisteredException exp) {
					_logger.error(
							"This thread is not registered with Thread Monitor",
							exp);
				}
			}//@end outer try catch finally
		}
		
		private void checkPing(MonitoredIP ip) {
			
			if (!(AsePing.ping(ip.getIpAddress(),AsePing.DEFAULT_PORT,ip.getTimeout()))  ) {
				ip.incrementCounter();
				if(ip.getCounter() >= ip.getRetries()){
					_logger.error(" Ping failed retries exceeded::"+ip);
					initateShutdown();
				}//@end if retries exceeded
			}else{
				ip.resetCounter();
			}//@end if ping success
		}
		
		private void checkPlumb(MonitoredIP ip) {
			if (!AseUtils.checkFIP(ip.getIpAddress()) ) {
				ip.incrementCounter();
				if(ip.getCounter() >= ip.getRetries()){
					_logger.error(" Plumb check retries exceeded::"+ip);
					initateShutdown();
				}//@end if retries exceeded
			}else{
				ip.resetCounter();
			}//@end if plumb success
			
		}
		
		private void initateShutdown() {
			if (_logger.isDebugEnabled()) {
				_logger
					.debug("initiateShutdown on IP ping/plumb failure");
			}
			ClusterManager clusterMgr = (ClusterManager) Registry.lookup(Constants.NAME_CLUSTER_MGR);
			try{
				clusterMgr.shutdown();
			}catch(Exception e){
				_logger.error("Error Initiating shutdown", e);
			}finally{
				_logger.error("Calling System.exit");
				System.exit(1);
			}//@end try catch finally.
		}
		
		private long getSleepInterval() {
			long[] intervalArr = new long[ipList.size()];
			int cnt = 0;
			for(MonitoredIP ip:ipList){
			    intervalArr[cnt] = ip.getFrequency();
			    ++cnt;
			}
			
			return AseUtils.gcf(intervalArr);
		}
		
		// ////////////// ThreadMonitor methods for AseIpChecker start
		// ///////////////////////
		public void setThreadOwner(ThreadOwner threadOwner) {
			_threadOwner = threadOwner;
		}

		public ThreadOwner getThreadOwner() {
			return _threadOwner;
		}
		
		// ////////////// ThreadMonitor methods for AseIpChecker end
		// /////////////////////////

	}//@end ASEIPChecker
	
    // As ThreadOwner
    public int threadExpired(MonitoredThread thread) {
        _logger.error(thread.getName() + " expired");

        // Print the stack trace
        StackDumpLogger.logStackTraces();

        return ThreadOwner.SYSTEM_RESTART;
    }


   /**
    * This method is added for periodic dump of all Applications deployed
    */

    public void process(long currenttime)  {
    Iterator iter = this.findAll();
    while(iter.hasNext())  {
        Object temp = iter.next();
		if(temp instanceof AseContext) {
        	AseContext context = (AseContext)temp;
        	StringBuffer buffer = new StringBuffer();
        	context.appendApplicationInfo(buffer);
        	_logger.error(buffer.toString());
		}
    }
    }


    /**
     * This is required at active SAS inctance to generate ReplicationContextId.
     */
    public synchronized int generateRepCtxtId() {
        return ++repCtxtIdCounter;
    }

    /**
     * This is requied at standby SAS to keep ReplicationContextId in sync in case of
     * failover.
     */
    public synchronized void adjustRepCtxtId(String currId) {
        try {
            int id = Integer.parseInt(currId);
            if(id > repCtxtIdCounter) {
                repCtxtIdCounter = id;
            }
        } catch(NumberFormatException nfe) {
            _logger.error("Parsing replication context id", nfe);
        }
    }

    private void registerForBackgroundProcess()  {
        ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
        long dumpPeriod;
        try {
              String str = config.getValue(Constants.DUR_APP_INFO);
              if (str != null)  {
                  dumpPeriod = (long)Long.parseLong(str);
              } else {
                        _logger.error("Unable to Register App Info to BKG Processor ");
                         return;
              }
        } catch (Exception e) {
                   _logger.error("Unable to Register App Info to BKG Processor");
                   return;
        }

        try  {
                AseBackgroundProcessor processor = (AseBackgroundProcessor)Registry.lookup(Constants.BKG_PROCESSOR);
                 processor.registerBackgroundListener(this, dumpPeriod);
        } catch (Exception e)  {
               _logger.error(e.getMessage(), e);
        }
    }

    /**
     *
     */
//    private class MessageSender extends MonitoredThread {
//
//
//        private boolean stopped = false;
//        private AppSyncMessage message;
//        private ThreadOwner _threadOwner = null;
//        
//        private int counter = 0;
//        private ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
//
//        public MessageSender(AppSyncMessage message) {
//            super("MessageSender", AseThreadMonitor.getThreadTimeoutTime(),
//                                            (AseTraceService)Registry.lookup(Constants.NAME_TRACE_SERVICE));
//            this.message = message;
//        }
//
//        public void run() {
//            // Register thread with thread monitor
//            try {
//                // Set thread state to idle before registering
//                this.setThreadState(MonitoredThreadState.Idle);
//
//                _threadMonitor.registerThread(this);
//            } catch(ThreadAlreadyRegisteredException exp) {
//                _logger.error("This thread is already registered with Thread Monitor", exp);
//            }
//
//            try {
//                while (!stopped) {
//                	//This check is added to avoid following race condition:
//                	//Standby SAS sends AppSyncRole message for each deployed 
//                	//sysapps and applications. Suppose It gets the ACK message 
//                	//for each one of them except for one application and other 
//                	//SAS has gone down. As it has not got the ACK message, it 
//                	//retransmits the sync message after every one second. Now, 
//                	//other SAS comes up and acquired the role of StandBy. 
//                	//Since Standby doesnot respond to the sync message therefore 
//                	//active (which was standby earlier) never got the ACK message 
//                	//and continued retransmitting the sync message after every second 
//                	//resulting in high utilization of memory and ultimately resulting in memory leak.
//                	synchronized (_clusterRole) {
//                		if (_clusterRole == AseRoles.ACTIVE){
//                    		if (_logger.isEnabledFor(Level.INFO)) {
//                    			_logger.info("MessageSender(): Current cluster role is ACTIVE, so stopping retransmissions of AppSync Message.");
//                    		}
//                			return;
//                		}
//                	}
//                	String str = config.getValue(Constants.APP_SYNC_RETRIES);
//                	if (counter > Integer.valueOf(str).intValue()){
//                		_logger.error("Retries Exceeded than defined" + counter);
//                		return;
//                	}
//                		
//                	try {
//                        if (_logger.isEnabledFor(Level.INFO)) {
//                            _logger.info("MessageSender.run(): Broadcasting AppSyncMessage to peers for application, \"" + message.getApplicationId() + "\"");
//                        }
//                        // Update time in thread monitor
//                        this.updateTimeStamp();
//
//                        // Set thread state to running before calling send
//                        this.setThreadState(MonitoredThreadState.Running);
//                        counter++;
//                        _versionMgr.sendAppSyncMessage(message);
//                        Thread.sleep(1000);
//                    } catch (Exception e) {
//                        _logger.error(e.toString(), e);
//                    }
//                }
//            } finally {
//                // Unregister thread with thread monitor
//                try {
//                    _threadMonitor.unregisterThread(this);
//                } catch(ThreadNotRegisteredException exp) {
//                    _logger.error("This thread is not registered with Thread Monitor", exp);
//                }
//            }
//        }
//
//        public void kill() {
//            this.stopped = true;
//        }
//
//        //////////////// ThreadMonitor methods for MessageSender start ///////////////////////
//
//        public void setThreadOwner(ThreadOwner threadOwner) {
//            _threadOwner = threadOwner;
//        }
//
//        public ThreadOwner getThreadOwner() {
//            return _threadOwner;
//        }
//
//        //////////////// ThreadMonitor methods for MessageSender end /////////////////////////
//    }

    //BUG-6765 [LIVE SBB UPGRADE]
    public ClassLoader getLatestSbbCL() {
    	return latestSbbCL;
    }


    public void setLatestSbbCL(ClassLoader cl) {
    	this.latestSbbCL = cl;
    }
    
    public ClassLoader getAseCL() {
    	return aseCL;
    }

    public void setAseCL(ClassLoader cl) {
    	this.aseCL = cl;
    }
    
    public ClassLoader getTcapProviderCL() {
		return tcapProviderCL;
	}

	public void setTcapProviderCL(ClassLoader tcapProviderCL) {
		this.tcapProviderCL = tcapProviderCL;
	}

	@Override
	/*
	 * EMS Agent invokes this method to determine if it can continue
	 with shutdown process as part of soft shutdown call.
	 This method ,particularly, checks whether the Applications sessions 
	 of all the active SIP calls are invalidated before giving a call for shutdown
	 */
	
	public boolean isShutdownAllowed() throws Exception{
		
		if(_logger.isDebugEnabled()){
			_logger.debug("Inside AseHost : isshutDownAllowed()");
		}
		Object appSessionArray[] = appSessionMap.values().toArray();
		boolean isShutdownAllowed = true;
		
		for(int i=0;i<appSessionArray.length;i++){
			AseApplicationSession appSession = (AseApplicationSession) appSessionArray[i];
			if(_logger.isDebugEnabled()){
				_logger.debug("Delpoyed App Name is :" + appSession.getContext().getObjectName());
			}
			short appType = appSession.getContext().getType();
			if(appType==  DeployableObject.TYPE_SYSAPP){
				if(_logger.isDebugEnabled()){
					_logger.debug("App Type is SysApp Type");
				}
			}else{
				if(_logger.isDebugEnabled()){
					_logger.debug("Active Call present "+appSession+ "Shutdown not allowed");
				}
				isShutdownAllowed=false;
				break;
			}
				

		}
		if(_logger.isDebugEnabled()){
			_logger.debug("AseHost: ShutDown Allowed :" + isShutdownAllowed);
		}
		
		return isShutdownAllowed;
	}
    

	
}
