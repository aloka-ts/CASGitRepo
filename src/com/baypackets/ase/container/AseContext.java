/*--------------------------------------------------------*
 * AseContext.java
 *
 * Created on August 6, 2004, 4:51 PM
 *
 * BPUsa06758 : added invalidateSessions() method to invalidate all
 * currently active sessions upon undeployment of application - Zoltan M.
 *
 * BPUsa06791_1 : 11/2/2004 : modified invalidateSessions() method to prevent
 * a ConcurrentModificationException from being thrown. - Zoltan M.
 *
 * BPUsa07045 : Added an attribute to return the immutable
 *              List of supported extensions. NK 12/20/04
 *
 * BPUsa07069 : Added "validateArchive()" method to validate the contents of
 *              the application archive being deployed. - Zoltan M. 12/20/04
 *
 *-------------------------------------------------------*/

package com.baypackets.ase.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipServletMessage;
import javax.servlet.sip.SipSessionsUtil;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jndi.ActiveMQInitialContextFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.baypackets.ase.cdr.CDRContext;
import com.baypackets.ase.cdr.CDRContextWrapper;
import com.baypackets.ase.cdr.CDRWriter;
import com.baypackets.ase.common.AseBackgroundProcessor;
import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.BackgroundProcessListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.container.exceptions.ActivationFailedException;
import com.baypackets.ase.container.exceptions.AseInvocationFailedException;
import com.baypackets.ase.container.exceptions.DeactivationFailedException;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.container.exceptions.UndeploymentFailedException;
import com.baypackets.ase.container.sip.SipApplicationSessionImpl;
import com.baypackets.ase.control.AseModes;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.deployer.TcapSessionCount;
import com.baypackets.ase.dispatcher.Rule;
import com.baypackets.ase.dispatcher.RulesRepository;
import com.baypackets.ase.measurement.AseMeasurementManager;
import com.baypackets.ase.ocm.TimeMeasurementRule;
import com.baypackets.ase.replication.Policy;
import com.baypackets.ase.replication.PolicyManager;
import com.baypackets.ase.resource.MessageHandler;
import com.baypackets.ase.sbb.SBBFactory;
import com.baypackets.ase.security.ResourceCollection;
import com.baypackets.ase.security.SasPolicy;
import com.baypackets.ase.security.SasPrincipal;
import com.baypackets.ase.security.SasSecurityManager;
import com.baypackets.ase.security.SecurityConstraint;
import com.baypackets.ase.security.SipServletPermission;
import com.baypackets.ase.spi.container.SasApplication;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasProtocolSession;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.spi.measurement.MeasurementManager;
import com.baypackets.ase.spi.ocm.OverloadListener;
import com.baypackets.ase.spi.ocm.OverloadManager;
import com.baypackets.ase.spi.replication.ReplicationManager;
import com.baypackets.ase.startup.ServerConfigUtil;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.CallTraceService;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.DOMUtils;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.workmanager.WorkManagerImpl;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;


/**
 * An instance of this class provides a container for deploying and
 * managing a Servlet application.
 */
public class AseContext extends AbstractDeployableObject implements ServletContext, Serializable, SasApplication, BackgroundProcessListener {

	private static final long serialVersionUID = -3146342646478497221L;
    // Error conditions
    public static final short NO_ERROR = 0;
    public static final short DEPLOY_FAILED = 1;
    public static final short START_FAILED = 2;
    public static final short ACTIVATE_FAILED = 3;

    private static Logger _logger = Logger.getLogger(AseContext.class);
    private static Logger _servletLogger = Logger.getLogger("ServletLogger");
    private static StringManager _strings = StringManager.getInstance(AseContext.class.getPackage());

    private transient int _appSessionTimeout = -1 ;
    private transient Map _attributes = new ConcurrentHashMap();
    private transient Map _initParams = new ConcurrentHashMap();
    private transient List _listeners = new ArrayList();
    private transient List m_resourceNames = new ArrayList();
    private transient Map _sessionMap = new ConcurrentHashMap();
    private transient Map _sessionMapFacade = Collections.unmodifiableMap(_sessionMap);
    private transient Map _protocolSessionMap = new ConcurrentHashMap();
    private transient ServletContext _facade = createServletContext();
    private transient String _defaultHandlerName;
    private transient HashMap _defaultHandlers = new HashMap();
    private transient String _displayName;
    private transient String _appName;
    private transient String _description;
    private transient String _largeIcon;
    private transient String _smallIcon;
    private transient List _wmList = new ArrayList();
    private transient List _resRefList = new ArrayList();
    private transient String mainServlet = null;
    private transient boolean servletMapPresent = false;
    private short _errorStatus;
    private static short m_mode = AseModes.NON_FT; 
    //JSR 289.42
    //This property saves the value of enable-annotation tag defined in sas.xml
    private boolean isEnableAnnotation=true;  
    
    private boolean isEnableLibAnnotation=true;
    //This property saves the information about class and method name in which SipApplicationKey annotation is defined. 
    private AseAnnotationInfo applicationKeyAnnoInfo;
    //JSR 289.42
    
    private boolean sysUtil = false;
    public boolean isSysUtil() {
    	return sysUtil;
    }

    public void setSysUtil(boolean sysUtil) {
    	this.sysUtil = sysUtil;
    }

   private boolean usesSBB = false;
   private SBBFactory sbbFactory = null;
   private ClassLoader lastSBBFacLoaded = null;
   private static Boolean lock = new Boolean(true);

   private SasSecurityManager _securityManager = new SasSecurityManager();
    
  //SipSessionsUtil
    SipSessionsUtil sipSessionsUtil = new SipSessionsUtilImpl(this);

	//for initialContext
	private Hashtable environment = null;
	private Context ctx=null;
	
	// JMS JNDI Properties file 
    private Properties props=null;
    private List<String> queueList = new ArrayList<String>();
    private List<String> topicList = new ArrayList<String>();
    private List<String> factoryList = new ArrayList<String>();
  		
    //BPUsa06771 == Need to create a new sipFactory for each SIP application deployed.
    //This factories object will have all the protocol specific factory objects
    //identified using the PROTOCOL_NAME as the key;
    private transient HashMap factories;

    private int _searchTimeout;
    private Collection _rules = new ArrayList();
    private Collection _policies;

	private boolean httpFlag;
	// BPUsa07541 :
	private CDRContextWrapper _cdrContextWrapper;

    private static transient List _supp_ext = new ArrayList();
    private static transient List _supp_ext_Facade =
      Collections.unmodifiableList (_supp_ext);

    //BUG 7070
    private int notifyCount = 0;
    private long maxNotifyPerSec = -1;

    public int getNotifyCount() {
    	return notifyCount;
    }

    public void incrementNotifyCount() {
    	notifyCount++;
    }

    public long getMaxNotifyPerSec() {
    	return maxNotifyPerSec;
    }
  //Sumit@SBTM hand off assist changes [
	private transient Map<Integer,Object> _corrIdTcapSessionMap;
//	protected static final transient String CORRELATION_MAP_ATTRIBUTE="Correlation-Map";
//	private static final transient String CORRELATION_MAP_CAPACITY_OID=Constants.OID_CORRELATION_MAP_CAPACITY;
//	private static final transient String CORRELATION_MAP_CONCURRENCY_OID=Constants.OID_CORRELATION_MAP_CONCURRENCY;
	//] Sumit@SBTM
	
	//Nitin@sbtm
    protected static final transient String TCAP_DLG_ID_APPSESSION_MAP="Tcap_DlgID_AppSessionMap";
    // ]Nitin
	
	// Used to just one time load of the supported extn and
    // other such system wide properties.
    static
    {
    	// Getting installation mode 
    	ConfigRepository configRepository    = (ConfigRepository)Registry.lookup(
				Constants.NAME_CONFIG_REPOSITORY);
    	String modeStr = configRepository.getValue(Constants.OID_SUBSYS_MODE);
		modeStr = (modeStr == null) ? "" : modeStr.trim();
		try{
			m_mode = Short.parseShort(modeStr);
		}catch(NumberFormatException nfe){}
		
      NodeList nl =
        ServerConfigUtil.instance().getNodeList("Supported-Extensions");
      if (nl.getLength() > 0)
      {
        Element element = (Element) nl.item(0);
        Element child   = DOMUtils.getFirstChildElement (element);
        while (child != null)
        {
          String name = child.getTagName ();
          String value = DOMUtils.getChildCharacterData (child);
          if (name.equals ("Ext"))
            {
			if((AseUtils.getCallPrioritySupport() == 0) &&
				(value.equalsIgnoreCase("resource-priority")))	{
				//Don't add resource-priority to list of supported extensions
			}else	{
	     		 _supp_ext.add (value);
			}
            }
          child = DOMUtils.getNextSiblingElement (child);
        }
      }
    }

    /**
     * Assigns the specified name to this AseContext object.
     *
     * @param name  Uniquely identifies this AseContext
     * @throws IllegalArgumentException if the given name is null
     */
    public AseContext(String name) {    	
        super(name);
        
        // Bug 6389
        ConfigRepository m_configRepository    = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
	    String annot = (String) m_configRepository.getValue(Constants.PROP_ASE_ANNOTAIONS_ENABLE);
	    if(annot != null && ! "".equals(annot)) {
	    	if("false".equalsIgnoreCase(annot.trim())) {
	    		isEnableAnnotation = false;
	            if (_logger.isDebugEnabled()) {
	                _logger.debug("Annotation Processing enabled for application classes." );
	            }	    		
	    	}
	    }
	    String libAnnot = (String) m_configRepository.getValue(Constants.PROP_ASE_LIB_ANNOTAIONS_ENABLE);
	    if(libAnnot != null && ! "".equals(libAnnot)) {
	    	if("false".equalsIgnoreCase(libAnnot.trim())) {
	    		isEnableLibAnnotation = false;
	            if (_logger.isDebugEnabled()) {
	                _logger.debug("Annotation Processing enabled for application libraries." );
	            }	
	    	}
	    }

	    try {
	    	String str = m_configRepository.getValue(Constants.NOTIFY_GEN_RATE);
	    	if (str != null)  {
	    		maxNotifyPerSec = (long)Long.parseLong(str);
			if (_logger.isDebugEnabled()) {

	    			_logger.debug("NOTIFY limit set to " + maxNotifyPerSec + " per second.");
			}
	    	} else {
	    		_logger.error("No limit on NOTIFY creation!");
	    		maxNotifyPerSec = -1;
	    		return;
	    	}
	    } catch (Exception e) {
	    	_logger.error("No limit could be set on NOTIFY creation!");
	    	maxNotifyPerSec = -1;
	    	return;
	    }
	    //Sumit@SBTM[
	    int initialCapacity=Integer.parseInt(m_configRepository.getValue(Constants.OID_CORRELATION_MAP_CAPACITY));
	    int concurrencyLevel=Integer.parseInt(m_configRepository.getValue(Constants.OID_CORRELATION_MAP_CONCURRENCY));
	    _corrIdTcapSessionMap = new ConcurrentHashMap<Integer,Object>(initialCapacity,0.75f,concurrencyLevel);
	    //]sumit@SBTM
	    
    }

    public AseContext(){
    	super();
    	
        // Bug 6389
        ConfigRepository m_configRepository    = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
	    String annot = (String) m_configRepository.getValue(Constants.PROP_ASE_ANNOTAIONS_ENABLE);
	    if(annot != null && ! "".equals(annot)) {
	    	if("false".equalsIgnoreCase(annot.trim())) {
	    		isEnableAnnotation = false;
	            if (_logger.isDebugEnabled()) {
	                _logger.debug("Annotation Processing disabled for application classes." );
	            }	
	    	}
	    }
	    String libAnnot = (String) m_configRepository.getValue(Constants.PROP_ASE_ANNOTAIONS_ENABLE);
	    if(libAnnot != null && ! "".equals(libAnnot)) {
	    	if("false".equalsIgnoreCase(libAnnot.trim())) {
	    		isEnableLibAnnotation = false;
	            if (_logger.isDebugEnabled()) {
	                _logger.debug("Annotation Processing disabled for application libraries." );
	            }
	    	}
	    }	    

	    try {
	    	String str = m_configRepository.getValue(Constants.NOTIFY_GEN_RATE);
	    	if (str != null)  {
	    		maxNotifyPerSec = (long)Long.parseLong(str);
			if (_logger.isDebugEnabled()) {

		    		_logger.debug("NOTIFY limit set to " + maxNotifyPerSec + " per second.");
			}
	    	} else {
	    		_logger.error("No limit on NOTIFY creation!");
	    		maxNotifyPerSec = -1;
	    		return;
	    	}
	    } catch (Exception e) {
	    	_logger.error("No limit could be set on NOTIFY creation!");
	    	maxNotifyPerSec = -1;
	    	return;
	    }
	    //Sumit@SBTM[
	    int initialCapacity=Integer.parseInt(m_configRepository.getValue(Constants.OID_CORRELATION_MAP_CAPACITY));
	    int concurrencyLevel=Integer.parseInt(m_configRepository.getValue(Constants.OID_CORRELATION_MAP_CONCURRENCY));
	    _corrIdTcapSessionMap = new ConcurrentHashMap<Integer,Object>(initialCapacity,0.75f,concurrencyLevel);
	    //]sumit@SBTM
    }
    
    public boolean getUsesSBB() {
    	return usesSBB;
    }

    public void setUsesSBB(boolean usesSBB) {
    	this.usesSBB = usesSBB;
    }


    /**
     * Sets this AseContext's parent.  Since a context is deployed to a
     * virtual host, it's parent must be an instance of the AseHost class.
     *
     * @param parent  Must be an instance of AseHost or null
     * @throws IllegalArgumentException if the given parent argument is not an
     * instance of AseHost or null.
     * @see com.baypackets.ase.container.AseHost
     */
    public void setParent(AseContainer parent) {
        if (!(parent instanceof AseHost || parent == null)) {
            throw new IllegalArgumentException(_strings.getString("AseContext.invalidParent", getName()));
        }
        super.setParent(parent);
    }


    /**
     * Adds the specified child to this AseContext object which must be an
     * instance of the AseWrapper class.
     *
     * @param child  Must be an instance of AseWrapper
     * @thorws IllegalArgumentException if the given child is not an
     * AseWrapper.
     * @see com.baypackets.ase.container.AseWrapper
     */
    public void addChild(AseContainer child) {
        if (!(child instanceof AseWrapper)) {
            throw new IllegalArgumentException(_strings.getString("AseContext.invalidChild", getName()));
        }
        super.addChild(child);
    }


    /**
     * Processes the given Servlet request and/or response objects.
     *
     * @param message  An object encapsulating a Servlet request.
     * @param response  An object encapsulating a Servlet response.
     */
    public void processMessage(SasMessage message) throws AseInvocationFailedException, ServletException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("invoke() called on AseContext: " + this.getName());
        }

        // Can't service any new requests if we're not currently running.
        if (message.isInitial() && !isRunning()) {
            throw new AseInvocationFailedException(_strings.getString("AseContext.notRunning", getName()));
        }

		//****************************************************************************
		// Need to place this code some where else BUG FIX BPUsa07425
		if(message.isInitial())
		{
			try
			{
				DeployerFactory deployerFactoryImpl = (DeployerFactory)Registry.lookup(DeployerFactory.class.getName());
				Deployer appDeployer = (Deployer)deployerFactoryImpl.getDeployer(1);
				Iterator initiallydeployed = appDeployer.findAll();
				while(initiallydeployed.hasNext())
				{
					Object temp = initiallydeployed.next();
					if(temp instanceof AseContext) {
						AseContext deployableObject = (AseContext)temp;
						if (_logger.isInfoEnabled()) {

						_logger.info("APPLICATION ==== >"+deployableObject.getId());
						}
						if(deployableObject.getUpgradeState()&&(deployableObject.getAppSessionCount()==0)
							&& TcapSessionCount.getInstance().getDialogueCount(deployableObject.getObjectName()+"_"+deployableObject.getVersion()) == 0)
						{
							if (_logger.isInfoEnabled()) {

							_logger.info("UPGRADE STATE IS TRUE AND ACTIVE APPLICATION SESSIONS ARE 0");
							}
							AseHost host = (AseHost) Registry.lookup(Constants.NAME_HOST);
							host.startPing(this, false);
							deployableObject.deactivate();
							deployableObject.stop(false);
							deployableObject.undeploy();
							if (_logger.isInfoEnabled()) {

							_logger.info("Application has been undeployed:"+deployableObject.getName());
						}
					}
				}
				}

			}
			catch(Exception e)
			{
				_logger.error("Exception ",e);
				}
		}

		//*********************************************************************************
		AseWrapper wrapper = null;
        String handler = message.getHandler();
        if(handler == null){
        	throw new AseInvocationFailedException("Not able to get the name of the handler to be invoked.");
        }
        
        // Get the Servlet wrapper object specified in the session.
        if(usesSBB && Constants.SBB_SERVLET_NAME.equals(handler)) {
        	wrapper = ((SipApplicationSessionImpl) message.getApplicationSession()).getSBBWrapper();
        }  else {
        	wrapper = (AseWrapper)this.findChild(handler);
        }
        if (wrapper == null){
            throw new AseInvocationFailedException(_strings.getString("AseContext.noWrapper", handler));
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("Incoming message received for Servlet \"" + wrapper.getName() + "\"");
        }	

        // Log the incoming request or response to the call trace console.
        traceMessage(message, wrapper.getName());

        // Authenticate the caller if required.
        if (message != null && _securityManager != null) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Authenticating caller...");
            }

            boolean authenticated = false;

            try {
                authenticated = _securityManager.authenticate(message, wrapper.getName());
            } catch (Exception e) {
                _logger.error(e.toString(), e);
                throw new AseInvocationFailedException(e.toString());
            }

            if (!authenticated) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("The caller was not authenticated.  Returning...");
                }
                return;
            } else if (_logger.isDebugEnabled()) {
                _logger.debug("The caller was successfully authenticated.");
            }
        } else if (_logger.isDebugEnabled()) {
            _logger.debug("No authentication required for caller.");
        }

        // Get any auth info associated with this caller.
        Subject subject = message != null ? message.getSubject() : null;

        // Authorize the caller if required.
        if (subject != null) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Checking if caller is in role to access requested resource...");
            }

            try {
                // Execute the Servlet in the context of the
                // caller's principal(s).
                PrivilegedExceptionAction action = new AseContext.PriviledgedActionImpl(wrapper, message);
                Subject.doAsPrivileged(subject, action, null);
            } catch (Exception e) {
                if (e instanceof AseInvocationFailedException) {
                    throw (AseInvocationFailedException)e;
                }
                if (e instanceof ServletException) {
                    throw (ServletException)e;
                }
                String msg = "Error occurred while invoking the Servlet: " + e.toString();
                _logger.error(msg, e);
                throw new AseInvocationFailedException(msg);
            }
        } else {
            if (_logger.isDebugEnabled()) {
                _logger.debug("No authorization of caller is required.  Invoking Servlet...");
            }
            wrapper.processMessage(message);
        }
    }


    /**
     * Called by the "invoke" method to log the given SIP Servlet request or
     * response object to the call trace console in EMS.
     */
    private void traceMessage(SasMessage msg, String servletName) {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Logging the incoming message to the call trace console...");
        }

        CallTraceService traceService = (CallTraceService)Registry.lookup(Constants.CALL_TRACE_SERVICE);

        if (!traceService.isContainerTracingEnabled()) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Call tracing is currently disabled.");
            }
            return;
        }

        if (msg instanceof SipServletMessage) {
            if (traceService.matchesCriteria((SipServletMessage)msg)) {
                Object[] args = {this.getName(), servletName, msg};
                String message = _strings.getString("AseContext.traceRequest", args);
                traceService.trace((SipServletMessage)msg, message);
            } else if (_logger.isDebugEnabled()) {
                _logger.debug("Incoming request does not match the current call tracing criteria.");
            }
        } else if (msg instanceof SipServletMessage) {
            if (traceService.matchesCriteria((SipServletMessage)msg)) {
                Object[] args = {this.getName(), servletName, msg};
                String message = _strings.getString("AseContext.traceResponse", args);
                traceService.trace((SipServletMessage)msg, message);
            } else if (_logger.isDebugEnabled()) {
                _logger.debug("Incoming response does not match the current call tracing criteria.");
            }
        }
    }


    /**
     * Returns the name of the Servlet that is the default handler for
     * the application.
     */
    public String getDefaultHandlerName() {
        return _defaultHandlerName;
    }


    /**
     * Sets the name of the application's default handler.
     */
    public void setDefaultHandlerName(String name) {
        _defaultHandlerName = name;
    }
    
    
//    /**
//     * Returns the name of the Application 
//     * the application.
//     */
//    public String getAppName() {
//        return _appName;
//    }
//
//
//    /**
//     * Sets the name of the application
//     */
//    public void setAppName(String name) {
//        _appName = name;
//    }
//    

    
    /**
     * Returns the name of the Servlet that is the default handler for
     * the application.
     */
    public String getDefaultHandlerName(String name) {
        return (String)this._defaultHandlers.get(name);
    }


    /**
     * Sets the name of the application's default handler.
     */
    public void setDefaultHandlerName(String name, String handler) {
        this._defaultHandlers.put(name, handler);
    }

    /**
     * Returns "true" if this AseContext was successfully started by EMS.
     */
    public boolean isReady() {
        return this.getState() == SasApplication.STATE_READY;
    }

    public boolean isRunning(){
		return (this.getState() == SasApplication.STATE_ACTIVE)||(this.getUpgradeState());
    }

    /**
     * Returns the error status of the application being managed by this
     * AseContext object.  The possible return values from this method are
     * enumerated by this class's public static constants, "NO_ERROR",
     * "DEPLOY_ERROR", and "START_ERROR".
     */
    public short getErrorStatus() {
        return _errorStatus;
    }


    /**
     * Sets the error status of the application.
     */
    public void setErrorStatus(short errorStatus) {
        _errorStatus = errorStatus;
    }

    /**
     * Returns the set of triggering rules for this application.
     *
     * @return  A Collection of RuleObjects.
     * @see com.baypackets.ase.dispatcher.RuleObject
     */
    public Collection getTriggeringRules() {
        return _rules;
    }

    /**
     * Returns the set of replication policies for this application.
     *
     * @return  A Collection of Policy objects.
     * @see com.baypackets.ase.replication.Policy
     */
    public Collection getPolicies() {
        return _policies;
    }


    /**
     * Sets this application's replication policies.
     */
    public void setPolicies(Collection policies) {
        _policies = policies;
    }

    /**
     * Returns the number of active app sessions currently associated with
     * this application.
     */
    public int getAppSessionCount() {
        return _sessionMap != null ? _sessionMap.size() : 0;
    }


    /**
     * Returns "true" if this application is a web app or returns
     * "false" otherwise.
     */
    public boolean isWebApp() {
        return findFile("web.xml") != null;
    }


    /**
     * Returns the SasSecurityManager that handles the authentication and
     * authorization of all requests dispatched to this application.
     */
    public SasSecurityManager getSecurityManager() {
        return _securityManager;
    }


    /**
     * Sets the SasSecurityManager on this AseContext object which will
     * handle the authentication and authorization of all requests dispatched
     * to this application.
     */
    public void setSecurityManager(SasSecurityManager manager) {
        _securityManager = manager;
    }

 	// BPUsa07541 : [
	/**
	 * Returns the CDRContext associated with this object.
	 */
	public CDRContext getCDRContext(String sipSessionId) {
		
		if (_cdrContextWrapper == null) {
			_cdrContextWrapper = (CDRContextWrapper)Registry.lookup(Constants.DEFAULT_CDR_CONTEXT_WRAPPER);
		}
		int index = ( ( Math.abs(sipSessionId.hashCode()) )
						% this._cdrContextWrapper.getMaxCDRWriters()  );
		return _cdrContextWrapper.getCDRContext(index);
	}

	/**
	 * Associates the specified CDRContext with this object.
	 */
	public void setCDRContextWrapper(CDRContextWrapper wrapper) {
		_cdrContextWrapper = wrapper;
	}
	// ]

	public void start() throws StartupFailedException {
		super.start();
		try{
			//commented as start doesn't means service is active
//			((AseHost)this.getParent()).startPing(this,true);
			updateOcmListeners(true);
		}catch(Exception e){
			throw new StartupFailedException(e);
		}
	}


    /**
     * This method is overridden from the AseBaseContainer class. It performs
     * the actions required to move this AseContext into a "running" state.
     */
    public void activate() throws ActivationFailedException {
        if (_logger.isDebugEnabled()) {
            _logger.debug(_strings.getString("AseContext.start", getName()));
        }

        try {
            addTriggeringRules();
            addReplicationPolicies();
            notifyContextListeners(true);
            initServlets();
            this.startWebApp();
           	super.activate();
          //added as start is before activationa nd cause nullpointer exceptions
           	((AseHost)this.getParent()).startPing(this,true);
        } catch (Exception e) {
		    this.setState(SasApplication.STATE_ERROR);
		    this.setErrorStatus(ACTIVATE_FAILED);
            _logger.error(e.toString(), e);
            throw new ActivationFailedException(e.toString());
        }

        if (_logger.isInfoEnabled()) {
            _logger.info(_strings.getString("AseContext.started", getName()));
            _logger.info("Application Information :" + this.getDisplayInfo());
        }
    }

    public void stop(boolean immediate) throws ShutdownFailedException {
    	if(!immediate && (this.getAppSessionCount() > 0)){
    		this.setState(DeployableObject.STATE_STOPPING);
    		super.setExpectedState(DeployableObject.STATE_INSTALLED);
    	}else{
		try{
			updateOcmListeners(false);
			((AseHost)this.getParent()).startPing(this,false);
		}catch(Exception e){
			throw new ShutdownFailedException(e);
		}
    		super.stop(immediate);
    	}
    }


    /**
     * This method is overridden from the AseBaseContainer class.  It performs
     * the actions required to shutdown this AseContext.
     */
    public void deactivate() throws DeactivationFailedException {
        if (_logger.isDebugEnabled()) {
            _logger.debug(_strings.getString("AseContext.stop", getName()));
        }
        try {
        	//added as deactivate is called before stop 
        	((AseHost)this.getParent()).startPing(this,false);
            removeTriggeringRules();
            removeReplicationPolicies();
            notifyContextListeners(false);
            this.stopWebApp();

			//stopping WorkManager
			if(!(_wmList.isEmpty())) {
				if (_logger.isDebugEnabled()) {

				_logger.debug("stopping all the WorkMangager lookedup by application");
				}
		    	for(Iterator i = _wmList.iterator(); i.hasNext(); ) {
		    		WorkManagerImpl wmImpl = (WorkManagerImpl)i.next();
		    		wmImpl.stop();
		    	}
			}

	    	super.deactivate();
        } catch (Exception e) {
        	this.setState(SasApplication.STATE_ERROR);
            _logger.error(e.toString(), e);
            throw new DeactivationFailedException(e.toString());
        }finally{
        	//clean all replicated dat for APP
        	
        	/* Removing ReplicationContext here results in context being removed on service
        	 * deactivation and not created again when service is activated. So, when replication
        	 * is done, this results in Unable to get Context error.
        	*/
        	/*try {
				ReplicationManager m_replicationMgr = (ReplicationManager) Registry
						.lookup(Constants.NAME_REPLICATION_MGR);
				if(AseModes.isFtMode(m_mode))
					m_replicationMgr.removeContextsForAppId(this.getId());
				else{
					if (_logger.isDebugEnabled()) {
			            _logger.debug("NON FT Modenot doing anything");
			        }
				}
			} finally {

			}*/
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug(_strings.getString("AseContext.stopped", getName()));
        }
    }


    /**
     * Deploys the application being managed by this AseContext.
     *
     * @throws DeploymentFailedException if an error occurs while deploying
     * the application.
     */
    public synchronized void deploy() throws DeploymentFailedException {
        if (_logger.isDebugEnabled()) {
            _logger.debug(_strings.getString("AseContext.deploy", getName()));
        }

        try {
        	//Adding a LifeCycleListener
        	AppLifeCycleListener.instance().add(this);
        	
            initialize();
            readTriggeringRules();

   //some changes for BPUsa08018
            boolean flag=this.addProtectionDomain();
            Boolean securityFlag =new Boolean(flag);
            setAttribute(Constants.ASE_SIP_SECURITY, securityFlag);

			//workmanager and JMS
			if(!(_resRefList.isEmpty())) {
				bind();
			}

            super.deploy();
        } catch (Exception e) {
            this.setState(SasApplication.STATE_ERROR);
	    this.setErrorStatus(DEPLOY_FAILED);
            _logger.error(e.toString(), e);

            if (e instanceof DeploymentFailedException) {
                throw (DeploymentFailedException)e;
            }
            throw new DeploymentFailedException(e.toString());
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug(_strings.getString("AseContext.deployed", getName()));
        }
    }

    /**
     * Called by the "deploy" method to add this application's protection
     * domain to the SAS security policy.
     */
    private boolean addProtectionDomain() throws Exception {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Adding application's protection domain to the SAS security policy.");
        }

        SasSecurityManager manager = this.getSecurityManager();

        if (manager == null) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("No security constraints defined for this application.");
            }
            return false;
        }

        Collection constraints = manager.getSecurityConstraints();

        if (constraints == null || constraints.isEmpty()) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("No security constraints defined for this application.");
            }
            return false;
        }

        SasPolicy policy = (SasPolicy)Registry.lookup(Constants.NAME_SAS_SECURITY_POLICY);

        if (policy == null) {
            String msg = "Unable to add protection domain for application.  SasPolicy object was not found in the Registry!";
            _logger.error(msg);
            throw new DeploymentFailedException(msg);
        }

        Iterator iterator = constraints.iterator();

        while (iterator.hasNext()) {
            SecurityConstraint constraint = (SecurityConstraint)iterator.next();
            Collection roles = constraint.getRoles();

            if (roles == null || roles.isEmpty()) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("No roles defined in this security constraint.");
                }
                continue;
            }

            ResourceCollection resources = constraint.getResourceCollection();
            Collection servletNames = resources != null ? resources.getServletNames() : null;
            Collection methods = resources != null ? resources.getMethods() : null;

            if (servletNames == null || servletNames.isEmpty() || methods == null || methods.isEmpty()) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("Invalid security constraint:: " + constraint.toString());
                }
                continue;
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("Creating security roles for protection domain...");
            }

            SasPrincipal[] principals = new SasPrincipal[roles.size()];

            Iterator rolesIter = roles.iterator();

            for (int i = 0; rolesIter.hasNext(); i++) {
                String roleName = (String)rolesIter.next();

                if (_logger.isDebugEnabled()) {
                    _logger.debug("Processing role-name: " + roleName);
                }

                principals[i] = new SasPrincipal(roleName);
            }

            SipServletPermission[] permissions = new SipServletPermission[servletNames.size() * methods.size()];

            Iterator servletNameIter = servletNames.iterator();

            for (int i = 0; servletNameIter.hasNext(); i++) {
                String servletName = (String)servletNameIter.next();

                if (_logger.isDebugEnabled()) {
                    _logger.debug("Creating permissions for Servlet: " + servletName);
                }

                Iterator methodIter = methods.iterator();

                while (methodIter.hasNext()) {
                    String method = (String)methodIter.next();

                    if (_logger.isDebugEnabled()) {
                        _logger.debug("Creating permission for method: " + method);
                    }

                    permissions[i] = new SipServletPermission(this.getName(), servletName, method);
                }

                policy.addPermissions(principals, permissions);
            }
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("Successfully added protection domain to the security policy.");
        }
return true;
    }


    /**
     * Undeploys the application by performing all required cleanup.
     *
     * @throws UndeploymentFailedException if an error occurs while
     * un-deploying the application.
     */
    public synchronized void undeploy() throws UndeploymentFailedException {
        if (_logger.isDebugEnabled()) {
            _logger.debug(_strings.getString("AseContext.undeploy", getName()));
        }

        try {
        	this.invalidateSessions();
        	this.destroyServlets();
            this.removeProtectionDomain();
			((AseHost)this.getParent()).removeChild(this);

			//workmanager
			if(!(_resRefList.isEmpty())) {
				unbind();
			}
			super.undeploy();
        } catch (Exception e) {
            this.setState(SasApplication.STATE_ERROR);
            _logger.error(e.toString(), e);
            throw new UndeploymentFailedException(e.toString());
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug(_strings.getString("AseContext.undeployed", getName()));
        }
    }


    /**
     * Invoked by "undeploy" to remove the application's protection domain
     * from the SAS security policy.
     */
    private void removeProtectionDomain() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Removing application's protection domain from security policy.");
        }

        SasPolicy policy = (SasPolicy)Registry.lookup(Constants.NAME_SAS_SECURITY_POLICY);

        if (policy == null) {
            _logger.error("No security policy bound to Registry.");
        } else {
            policy.removePermissions(this.getName());

            if (_logger.isDebugEnabled()) {
                _logger.debug("Successfully removed protection domain for application.");
            }
        }
    }


    /**
     * Called by the "undeploy" method to invalidate all currently active app
     * sessions.
     */
    private void invalidateSessions() throws Exception {
        if (_sessionMap != null && !_sessionMap.isEmpty()) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("Invalidating all app sessions...");
            }

            // Make a shallow copy of the set of app sessions to prevent
            // a ConcurrentModificationException from being thrown.
            Collection sessions = new ArrayList(_sessionMap.values());
            Iterator iterator = sessions.iterator();

            while (iterator.hasNext()) {
                AseApplicationSession session = (AseApplicationSession)iterator.next();
                session.invalidate();
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("Successfully invalidated " + sessions.size() + " app sessions");
            }
        } else if (_logger.isDebugEnabled()) {
            _logger.debug("No app sessions to invalidate");
        }
    }

    /**
     * Called by the "deploy" method to instantiate all internal objects and
     * create the work directory if necessary.
     */
    public void initialize() throws Exception {

        //For now, all apps will use the same CDRContext implementation.
        this.setCDRContextWrapper((CDRContextWrapper)Registry.lookup(Constants.DEFAULT_CDR_CONTEXT_WRAPPER));

		try{
			String strTimeOut = BaseContext.getConfigRepository().getValue(Constants.PROP_SIP_DEFAULT_APP_SESSION_TIMEOUT);
                        if(_appSessionTimeout <= 0){
				if(strTimeOut != null) {
					int appSessionTimeout = Integer.parseInt(strTimeOut);
					if(appSessionTimeout > 0){
						this._appSessionTimeout = appSessionTimeout;
					}else{
						if (_logger.isDebugEnabled()) {

							_logger.debug("Setting the default value (5 mins) for the application session ");
						}
						this._appSessionTimeout = Constants.DEFAULT_SESSION_TIMEOUT;
					}
				}else{
					if (_logger.isDebugEnabled()) {

						_logger.debug("Setting the default value (5 mins) for the application session ");
					}
					 this._appSessionTimeout = Constants.DEFAULT_SESSION_TIMEOUT;
				}
			}
		} catch(NumberFormatException e) {
			_logger.error("Reading app session-timeout", e);
		}

		//BPUsa06771 -- Initialize the factories map.
		this.factories = new HashMap();
		
	    // Registering for background Processes
	    this.registerForBackgroundProcess();
    }

    /**
     * Returns the specified file from the application's "WEB-INF" directory or
     * returns null if it cannot be found.
     */
    private File findFile(String fileName) {
        File webInf = new File(this.getUnpackedDir(), "WEB-INF");

        if (!webInf.exists()) {
            return null;
        }

        File[] files = webInf.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && files[i].getName().equalsIgnoreCase(fileName)) {
                return files[i];
            }
        }
        return null;
    }

    public void addTriggeringRule(Rule rule){
    	if(rule == null)
    		return;

    	if(rule.getName() == null){
    		rule.setName(this.getId() + "_" + rule.getServletName());
    	}

    	rule.setAppName(this.getId());
    	this._rules.add(rule);
    }

    /**
     * Called by the "deploy" method to read the application's triggering rules
     * from the deployment descriptor.
     */
    private void readTriggeringRules() throws Exception {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Reading application's triggering rules from the sip.xml file...");
        }

        RulesRepository repository = (RulesRepository)Registry.lookup(Constants.RULES_REPOSITORY);

        // Find the deployment descriptor.
        File descriptor = findFile("sip.xml");
        if (descriptor == null) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("No triggering rules to read.  Application has no sip.xml file.");


            }
            return;
        }

        // Generate the triggering rule objects and store them in a local list.
        // They will be added to the RulesRepository when the app is started.
        InputStream stream = new FileInputStream(descriptor);
        Collection rules = repository.generateRules(stream, this.getName());
        Iterator ruleIt = rules.iterator();
        for(;ruleIt.hasNext();){
        	Rule rule = (Rule) ruleIt.next();
        	this.addTriggeringRule(rule);
        }
        stream.close();
    }


    /**
     * Invoked by the "start" method to add this application's triggering rules
     * to the RulesRepository.
     */
    private void addTriggeringRules() throws Exception {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Adding application's triggering rules to the RulesRepository...");
        }

        RulesRepository repository = (RulesRepository)Registry.lookup(Constants.RULES_REPOSITORY);

        // Check if the rules were already added for this application.
        if (repository.hasRules(this.getName())) {
            return;
        }

        Collection rules = this.getTriggeringRules();

        if (rules == null || rules.isEmpty()) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("No triggering rules to add to RulesRepository.");
            }
            return;
        }

        repository.addRules(this.getName(), this.getPriority(), rules);
    }


    /**
     * Invoked by the "start" method to add this application's replication
     * policies to the PolicyManager.
     */
    private void addReplicationPolicies() throws Exception {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Adding replication policies to the PolicyManager...");
        }

        PolicyManager manager = (PolicyManager)Registry.lookup(Constants.NAME_POLICY_MANAGER);

        if (manager == null) {
            _logger.warn("PolicyManager was not found in the Registry!");
            return;
        }

        Collection policies = getPolicies();

        if (policies == null || policies.isEmpty()) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("No policies to add to PolicyManager.");
            }
            return;
        }

        Iterator iterator = policies.iterator();
        while (iterator.hasNext()) {
            manager.addPolicy((Policy)iterator.next());
        }
    }


    /**
     * Removes the application's triggering rules from the RulesRepository.
     */
    private void removeTriggeringRules() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Removing application's triggering rules from the RulesRepository");
        }
        RulesRepository repository = (RulesRepository)Registry.lookup(Constants.RULES_REPOSITORY);
        repository.removeRulesForApp(this.getName());
    }


    /**
     * Called by the "stop" method to remove any of the application's
     * replication policies from the PolicyManager.
     */
    private void removeReplicationPolicies() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Removing application's replication policies from the PolicyManager");
        }
        PolicyManager manager = (PolicyManager)Registry.lookup(Constants.NAME_POLICY_MANAGER);

        if (manager == null) {
            _logger.warn("PolicyManager was not found in the Registry");
            return;
        }
        manager.removePoliciesForApp(this.getName());
    }

    private void updateOcmListeners(boolean add){
	OverloadManager ocm = (OverloadManager)Registry.lookup(Constants.NAME_OC_MANAGER);
        Iterator listeners = getListeners(OverloadListener.class).iterator();
	for(;ocm !=null && listeners.hasNext();){
		OverloadListener listener = (OverloadListener) listeners.next();
		if(listener == null )
			continue;
		if(add)
			ocm.addOverloadListener(listener);
		else
			ocm.removeOverloadListener(listener);
	}
    }

    /**
     * Notifies all registered ServletContextListeners that the ServletContext
     * object has been created or destroyed based on the given boolean value.
     */
    private void notifyContextListeners(boolean create) throws Exception {
        if (_logger.isDebugEnabled()) {
            if (create) {
                _logger.debug("Notifying all ServletContextListeners of the ServletContext's creation");
            } else {
                _logger.debug("Notifying all ServletContextListeners of the ServletContext's destruction");
            }
        }

        ServletContextEvent event = null;

        Iterator listeners = getListeners(ServletContextListener.class).iterator();

        if (listeners != null) {
            while (listeners.hasNext()) {
                ServletContextListener listener = (ServletContextListener)listeners.next();

                if (event == null) {
                    event = new ServletContextEvent(_facade);
                }
                if (create) {
                    listener.contextInitialized(event);
                } else {
                    listener.contextDestroyed(event);
                }
            }
        }
    }


    /**
     * Initializes all "load-on-startup" Servlets.
     */
    private void initServlets() throws Exception {
        if (_logger.isDebugEnabled()) {
            _logger.debug(_strings.getString("AseContext.loadOnStartup", getName()));
        }

        AseContainer[] children = findChildren();

        if (children == null || children.length == 0) {
            return;
        }

        Map map = new TreeMap();

        // Insert each child AseWrapper object into a TreeMap keyed by their
        // "load-on-startup" priority.
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof AseWrapper) {
                AseWrapper wrapper = (AseWrapper)children[i];

                Integer priority = wrapper.getLoadOnStartup();

//                if(wrapper.getMessageHandler()!=null){
//                	//intiialize it 
//                	
//                	if (_logger.isDebugEnabled()) {
//            			_logger.debug("message handler is set on this wrapper so will load this: too " +wrapper.getName());
//            		}
//                	priority=1;
//                }else
                	
                 if (priority == null) {
                    continue;  // ignore all non "load-on-startup" wrappers
                }

                Collection wrappers = (Collection)map.get(priority);
                if (wrappers == null) {
                    map.put(priority, wrappers = new ArrayList(1));
                }
                wrappers.add(wrapper);
            }
        }

        // Do an in-order traversal of the tree and invoke each wrapper's
        // "initServlet" method.
        Iterator iterator = map.values().iterator();
        while (iterator.hasNext()) {
            Iterator wrappers = ((Collection)iterator.next()).iterator();
            while (wrappers.hasNext()) {
                AseWrapper wrapper = (AseWrapper)wrappers.next();
                wrapper.initServlet();
                wrapper.injectResourcesInServlet();
            }
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug(_strings.getString("AseContext.initializedServlets", getName()));
        }
    }

	public void destroyServlets() throws Exception {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Destroy Servlets called on the Context :" + this.getName());
		}

		AseContainer[] children = findChildren();
		for(int i=0; children != null && i< children.length; i++){
			AseWrapper wrapper = (AseWrapper) children[i];
			if(wrapper == null)
				continue;
			wrapper.destroyServlet();
		}

		if (_logger.isDebugEnabled()) {
			_logger.debug("Completed the destroying the Servlets.");
		}
	}

    /**
     * Creates a ServletContext object that will be passed to the Servlet
     * instances when they are intialized.  The implementation returned by
     * this method is a facade for this object.
     */
    private ServletContext createServletContext() {
        try {
            final AseContext context = this;

            return (ServletContext)Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class[] {ServletContext.class},
                    new InvocationHandler() {
                        public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                                return method.invoke(context, args);
                        }
                    });
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new RuntimeException(e.toString());
        }
    }


    /**
     * Returns the ServletContext object that will be used by the Servlets
     * in the application.  The implementation returned by this method is
     * a facade for this AseContext object.
     */
    public ServletContext getServletContext() {
        return _facade;
    }


    /**
     * Adds an application event listener to this AseContext object.  This
     * method will typically be called during the deployment process when
     * the application's descriptor file(s) are being parsed.
     */
    public void addListener(EventListener listener) {
    	if(listener!=null){
    		synchronized (_listeners) {
    			for(Object listererObj:_listeners){
    				if(listererObj.getClass().getName().equals(listener.getClass().getName())){
    					if(_logger.isInfoEnabled()){
    						_logger.info("Listener already added so not adding again:"+listener.getClass().getName());
    					}
				 	return;
    				}
    			}
    			_listeners.add(listener);
    		}
    	}
    }

    public void addListener(Class listenerClass){

    	if(listenerClass == null){
    		throw new IllegalArgumentException("Listener Class Cannot be NULL");
    	}

    	if(listenerClass.isAssignableFrom(EventListener.class)){
    		throw new IllegalArgumentException("Listener Class should implement the java.util.EventListener interface" );
    	}

    	AseWrapper wrapper = this.getWrapper(listenerClass);
    	if(wrapper != null){
    		this.addListener((EventListener) wrapper.getWrappedObject());
    		return;
    	}

    	EventListener listener=null;
		try {
			listener = (EventListener)listenerClass.newInstance();
    	this.addListener(listener);
		} catch (InstantiationException e) {
           _logger.error("Exception in addListener(().....",e);
			
			} catch (IllegalAccessException e) {
			_logger.error("Exception in addListener(().....",e);
		}
    	
    }

    private AseWrapper getWrapper(Class clazz){
    	AseWrapper wrapper = null;

    	AseContainer[]  wrappers = this.findChildren();
    	for(int i=0; wrappers != null && i<wrappers.length;i++){
    		AseWrapper temp = (AseWrapper) wrappers[i];
    		if(temp == null || temp.getWrappedObject() == null)
    			continue;
    		if(clazz.equals(temp.getWrappedObject().getClass())){
    			wrapper = temp;
    			break;
    		}
    	}
    	return wrapper;
    }

	/**
	 * Check type of class passed to it and if it is <code>SipServlet</code> type,
	 * then initialize it.
	 */
    public void initServlet(String name)
		throws ServletException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("AseContext.initServlet(String) called for " + getName() + " , name = " + name);
        }
		AseWrapper wrapper = (AseWrapper)this.findChild(name);
		if(wrapper != null) {
			wrapper.checkInitialized();
			wrapper.injectResourcesInServlet();
		}
	}

    /**
     * Returns the list of all application event listeners registered with
     * this object.
     */
    public List getListeners() {
        return _listeners;
    }


    /**
     * Returns an iterator over all application event listeners of
     * the specified type or returns null if none are found.
     */
    public List getListeners(Class type) {
        List listeners = new ArrayList();

        Iterator iterator = _listeners.iterator();

        while (iterator.hasNext()) {
            Object listener = iterator.next();

            if (type.isAssignableFrom(listener.getClass())) {
                listeners.add(listener);
            }
        }

        return listeners;
    }


    public SipApplicationSession createApplicationSession(String protocol,  String sessionId){
		return this.createApplicationSession(protocol,null,sessionId);
	}

    /**
     * Creates a session object that encapsulates the state and protocol
     * sessions of a call flow.
     * @param sessionId 
     */
    public AseApplicationSession createApplicationSession(String protocol, AseIc ic, String sessionId){

        // Create the app session and insert it into the session map.
        // This will be made accessible to Servlets through the ServletContext
    	//JSR 289.42
    	AseApplicationSession appSession=null;
    	boolean isAppSessionExists=false;
		if (sessionId == null || sessionId.equals("")) {
			//create new application session
			appSession = new SipApplicationSessionImpl(this);
			if (_logger.isInfoEnabled()) {

			_logger.info("Session id is null so generating new session with id: "+appSession.getAppSessionId());
			}
		} else {
			//check if sessionId already exists in sessionMap
			appSession = (AseApplicationSession) _sessionMap.get(sessionId);
			if (appSession == null) {
				// if appSesion is still null create new session with this id.
				appSession = new SipApplicationSessionImpl(this, sessionId);
				if (_logger.isInfoEnabled()) {

				_logger
						.info("Session id is not null so creating new session with this id: "
								+ appSession.getAppSessionId());
				}
			} else {
				isAppSessionExists=true;
				if (_logger.isInfoEnabled()) {

				_logger
						.info("Session id is not null and app session with this id exists in session map,returning old session with id: "
								+ appSession.getAppSessionId());
			}
		}
		}
		//JSR 289.42
		if (!isAppSessionExists) {
			if (_logger.isInfoEnabled()) {
				_logger.info(" Adding appSession(" + appSession
						+ ") to AseTimerClock");
			}
			AseTimerClock.getInstance().add(appSession);

			// Add it to the local map
			if (_logger.isInfoEnabled()) {
				_logger.info("Adding the Application Session to Context Map:"
						+ appSession.getAppSessionId());
			}
			_sessionMap.put(appSession.getAppSessionId(), appSession);
			AseHost host = (AseHost) this.getParent();

			if (null == ic) {
				ic = new AseIc();
				ic.setActive(true);
				// ic.assumeId();
				host.addIc(ic);
			}

			ic.addApplicationSession(appSession);

		}
		return appSession;
    }

	

	/**
	 * Called from AseApplicationSession at the time of activation.
	 */

	public void addApplicationSession(AseApplicationSession appSession) {
        if(_logger.isDebugEnabled())
        	_logger.debug("Adding the Application Session to the Host Maps :" + appSession.getId());
		_sessionMap.put(appSession.getId(), appSession);
		AseHost host = (AseHost)this.getParent();
		host.addApplicationSession(appSession);
	}


    /**
     * Returns a factory object for the protocol specified.
     */
    public Object getFactory(String protocol){
		Object factory = this.factories.get(protocol);

		if(factory == null) {
			synchronized(this) {
				AseProtocolAdapter adapter = this.getProtocolAdapter(protocol);
				if(adapter == null || factory != null) {
					return factory;
				}

				factory = adapter.createFactory(this);
				this.factories.put(protocol, factory);
			}
		}

		return factory;
    }


    /**
     * Remove the app session from the session map
     * Invoked from the appsession invalidate() method
     */
    public void removeApplicationSession(AseApplicationSession appSession) {
		if(_logger.isDebugEnabled()) {
			_logger.debug("Removing appSession from the Map");
		}
       _sessionMap.remove(appSession.getAppSessionId());

	   //Also remove it from the map at the host level
 	  AseHost host = (AseHost)this.getParent();
	  host.removeApplicationSession(appSession.getAppSessionId());
    }

    /*
    public SasProtocolSession getProtocolSession(String id){
    	return (SasProtocolSession)this._protocolSessionMap.get(id);
    } */

    public void addProtocolSession(SasProtocolSession session){
    	if(session != null){
			if(_logger.isDebugEnabled()) {
				_logger.debug("Adding protocol session to the Map");
			}
    		this._protocolSessionMap.put(session.getId(), session);
    	}
  	}

    public void removeProtocolSession(SasProtocolSession session){
    	if(session != null){
			if(_logger.isDebugEnabled()) {
				_logger.debug("Removing protocol session to the Map");
			}
    		this._protocolSessionMap.remove(session.getId());
    	}
    }

    public AseProtocolSession getProtocolSession(String id){
		if(_logger.isDebugEnabled()) {
			_logger.debug("Getting protocol session for id "+id);
		}
    	return (AseProtocolSession)this._protocolSessionMap.get(id);
    }

    /**
     * Sets the duration for the application session timeout.  This value is
     * specified in the application's deployment descriptor file through the
     * "session-timeout" element.
     */
    public void setAppSessionTimeout(int timeout) {
        _appSessionTimeout = timeout;
    }


    /**
     * Returns the duration of the application session timeout.
     */
    public int getAppSessionTimeout() {
		return _appSessionTimeout;
    }

    /**
     * Sets the value of an initialization parameter.  The init parameters are
     * specified in the deployment descriptor through each "context-param"
     * element.
     */
    public void setInitParam(Object name, Object value) {
        if(_logger.isDebugEnabled()){
            _logger.debug("Adding init-param :" + name + ", " + value + ", " + this.getId() + ", " + this);
        }
        _initParams.put(name, value);
    }

    /**
     * Sets this AseContext's "displayName" attribute.  This value is
     * specified in the application's deployment descriptor through the
     * "display-name" element.
     */
    public void setDisplayName(String displayName) {
        _displayName = displayName;
    }

    /**
     * Sets the application's description.  This is specified by the
     * "description" element in the deployment descriptor.
     */
    public void setDescription(String description) {
        _description = description;
    }


    /**
     * Return the application's description.
     */
    public String getDescription() {
        return _description;
    }


    /**
     * Returns the configured sequential search timeout value used by proxies
     * as defined in the application's deployment descriptor file.
     */
    public int getSequentialSearchTimeout() {
        return _searchTimeout;
    }


    /**
     *
     */
    public void setSequentialSearchTimeout(int searchTimeout) {
        _searchTimeout = searchTimeout;
    }


    /**
     *
     * @param icon  The path to a GIF or JPEG image file
     */
    public void setLargeIcon(String icon) {
        _largeIcon = icon;
    }


    /**
     *
     */
    public String getLargeIcon() {
        return _largeIcon;
    }


    /**
     *
     * @param icon   The path to a GIF or JPEG image file
     */
    public void setSmallIcon(String icon) {
        _smallIcon = icon;
    }


    /**
     *
     */
    public String getSmallIcon() {
        return _smallIcon;
    }

    /**
     *
     * @param servletName - name of main servlet in app
     */
    public void setMainServlet(String servletName) {
        mainServlet = servletName;
    }


    /**
     *
     */
    public String getMainServlet() {
        return mainServlet;
    }
    /**
    *
    * @param mapPresent - if servlet-mapping is present in DD or not
    */
   public void setServletMapPresent(boolean mapPresent) {
	   servletMapPresent = mapPresent;
   }


   /**
    *
    */
   public boolean isServletMapPresent() {
       return servletMapPresent;
   }

    /**
     * Implemented from ServletContext.
     */
    public void removeAttribute(String name) {
        Object value = _attributes.remove(name);

        if (value == null) {
            return;
        }

        ServletContextAttributeEvent event = null;

        // Notify any registered ServletContextAttributeListeners that an
        // attribute has been removed.
        Iterator listeners = getListeners(ServletContextAttributeListener.class).iterator();
        if (listeners != null) {
            while (listeners.hasNext()) {
                ServletContextAttributeListener listener = (ServletContextAttributeListener)listeners.next();

                if (event == null) {
                    event = new ServletContextAttributeEvent(getServletContext(), name, value);
                }
                listener.attributeRemoved(event);
            }
        }
    }


    /**
     * Implemented from ServletContext.
     */
    public void setAttribute(String name, Object value) {
        Object oldValue = _attributes.put(name, value);
		if (_logger.isDebugEnabled()) {

		_logger.debug("set Attribute Name " + name + "  Value " + value.toString() ) ;
		}
        ServletContextAttributeEvent event = null;

        // Notify any registered ServletContextAttributeListeners that an
        // attribute has been set.
        Iterator listeners = getListeners(ServletContextAttributeListener.class).iterator();
        if (listeners != null) {
            while (listeners.hasNext()) {
                ServletContextAttributeListener listener = (ServletContextAttributeListener)listeners.next();

                if (event == null) {
                    event = new ServletContextAttributeEvent(getServletContext(), name, oldValue);
                }
                if (oldValue != null) {
                    listener.attributeReplaced(event);
                } else {
                    listener.attributeAdded(event);
                }
            }
        }
    }


    /**
     * Implemented from ServletContext.
     */
    public Object getAttribute(String key) {

    	
    	// Return the AseAlarmService object if the key is for the AseAlarmService
    	if (key != null && key.equals(Constants.NAME_ALARM_SERVICE)){
            return Registry.lookup(key);
    	}

    	// Return the CallTraceService object if the key is for the callTraceService
    	if (key != null && key.equals(Constants.NAME_CALL_TRACE_SERVICE)){
            return Registry.lookup(key);
    	}

    	// Return the timer service object if the key is for the timer service
    	if (key != null && key.equals(Constants.NAME_TIMER_SERVICE)){
            return Registry.lookup(key);
    	}
    	
    	if (key != null && key.equals(Constants.NAME_SHARED_TOKEN_POOL)){
            return Registry.lookup(key);
    	}
    	
    	if (key != null && key.equals(Constants.NAME_APP_CHAIN_MGR)){
            return Registry.lookup(key);
    	}
        
        if (key != null && key.equals(Constants.NAME_CONTEXT_CLASS_LOADER)){
            return  this.getClassLoader();
        }  

    	// Return the SIP factory object
    	if (key != null && key.equals(Constants.NAME_SIP_FACTORY)){
	    return this.getFactory(Constants.PROTOCOL_SIP);
    	}

        // Return a Map containing all SipApplicationSessions keyed by their ID
        if (key != null && key.equals(Constants.ASE_APPSESSION_MAP)) {
            return _sessionMapFacade;
        }
        
      //Return the SipSessionsUtil implementation...
        if (key != null && key.equals(SipSessionsUtil.class.getName())) {
            return sipSessionsUtil;
        }

        // Immutable List of suppored extensions
        if (key != null && key.equals("javax.servlet.sip.supported")) {
            return _supp_ext_Facade;
        }
        
        // Retrieve the SINGLETON SBBFactoryImpl instance
        if (key != null && key.equals(Constants.SBB_FACTORY)) {
        	try {
			if (_logger.isDebugEnabled()) {

        		_logger.debug("Inside getAttribute for SBBFactory");
			}
        		ClassLoader cl = ((AseHost) this.getParent()).getLatestSbbCL();
			if (_logger.isDebugEnabled()) {

        		_logger.debug("CL inside AseContext = " +cl);
			}
        		if(sbbFactory == null || ! cl.equals(lastSBBFacLoaded)) {
        			synchronized (lock){
        				Class clazz = cl.loadClass(Constants.SBB_FACTORY_CLASS);
        				sbbFactory = (SBBFactory) clazz.newInstance();
        				lastSBBFacLoaded = cl;
        			}
        		}
        		return sbbFactory;
        	} catch(Exception e) {
        		_logger.error("Exception while obtaining instance of SBBFactoryImpl in AseContext" + e);
        		return null;
        	}
        }        

		// BPUsa07552: [
		if (Constants.NAME_MEDIA_SERVER_SELECTOR.equals(key)) {
			return Registry.lookup(Constants.NAME_MEDIA_SERVER_MANAGER);
		}
		// ]

        // If request is for outbound gateway selector, provide reference
        // to outbound gateway manager.
        if (Constants.NAME_OUTBOUND_GATEWAY_SELECTOR.equals(key)) {
            return Registry.lookup(Constants.NAME_OUTBOUND_GATEWAY_MANAGER);
        }
        
        //Sumit@SBTM[
        //return correlation Map
        if (key != null && key.equals(Constants.CORRELATION_MAP_ATTRIBUTE)){
            return _corrIdTcapSessionMap;
    	}
        //]Sumit@SBTM
        
      //Nitin@SBTM[
        //return correlation Map
        if (key != null && key.equals(TCAP_DLG_ID_APPSESSION_MAP)){
        	AseHost host = (AseHost) Registry.lookup(Constants.NAME_HOST);
        	return host.getAppSessionMapForInapDlgId();
    	}
        //]Nitin@SBTM

		//return Overload Manager 
		if(OverloadManager.class.getName().equals(key)){
			return Registry.lookup(Constants.NAME_OC_MANAGER);
		}
	
		if (key != null && key.equals(MeasurementManager.INSTANCE)) {
			return AseMeasurementManager.instance()
					.getDefaultMeasurementManager();
		}
		
		if (key != null && key.equals(Constants.PROP_CALL_STATS_PROCESSOR)) {
		  return Registry.lookup(Constants.PROP_CALL_STATS_PROCESSOR);
		}
		
		
		 if (key != null && key.equals(Constants.NAME_ASE_COMP_MON_MGR)){
	            return Registry.lookup(key);
	    	}
	        

        Object value = _attributes.get(key);

        //In case of special attributes, get it using the Special Attribute Holder interface
        if(value != null && value instanceof SpecialAttributeHolder){
        	value = ((SpecialAttributeHolder)value).get(this, key);
        }

        return value;
    }


    /*-----------------------------------------------------*
     * Implemented from ServletContext.
     * We do not add the system wide strings upfront
     * because this will be a needless overhead. So we
     * construct the enumaration on demand. Hopefully this
     * will not be called too often.
     */
    public java.util.Enumeration getAttributeNames() {
      Vector v = new Vector (_attributes.keySet());
      v.add (Constants.NAME_TIMER_SERVICE);
      v.add (Constants.NAME_SIP_FACTORY);
      v.add (Constants.NAME_MEDIA_SERVER_SELECTOR);  // BPUsa07552
      v.add(Constants.NAME_OUTBOUND_GATEWAY_SELECTOR);
      v.add (Constants.ASE_APPSESSION_MAP);
      v.add ("javax.servlet.sip.supported");
      return v.elements();
    }


    /**
     * Implemented from ServletContext.
     */
    public ServletContext getContext(String name) {
        // Trim any leading or trailing "/" characters
        name = name.replaceAll("\\A/*|/*\\z", "");

        AseHost host = (AseHost)getParent();

        if (host != null) {
            AseContext context = (AseContext)host.findChild(name);

            if (context != null) {
                return context.getServletContext();
            }
        }
        return null;
    }


    /**
     * Implemented from ServletContext.
     */
    public String getInitParameter(String name) {
        if(_logger.isDebugEnabled()){
            _logger.debug("Get init param :::" + name + " " + this.getId()  + "," + this);
        }

        return (String)_initParams.get(name);
    }


    /**
     * Implemented from ServletContext.
     */
    public java.util.Enumeration getInitParameterNames() {
        return Collections.enumeration(_initParams.keySet());
    }


    /**
     * Implemented from ServletContext.
     */
    public int getMajorVersion() {
        return 2;
    }


    /**
     * Implemented from ServletContext.
     */
    public int getMinorVersion() {
        return 4;
    }


    /**
     * Implemented from ServletContext.
     */
    public String getMimeType(String fileName) {
        // return null for now
        return null;
    }


    /**
     * Implemented from Servletcontext.
     */
    public RequestDispatcher getNamedDispatcher(String name) {
        final AseWrapper wrapper = (AseWrapper)findChild(name);

        if (wrapper == null) {
            return null;
        }

        return new RequestDispatcher() {
            public void forward(ServletRequest request, ServletResponse response) throws
                ServletException, IOException {
                    wrapper.invokeServlet(request, response);
            }
            public void include(ServletRequest request, ServletResponse response) throws
				ServletException, IOException {
                    wrapper.invokeServlet(request, response);
            }
        };
    }


    /**
     * Implemented from ServletContext.
     */
    public String getRealPath(String path) {
        // Trim any leading or trailing "/" characters
        path = path.replaceAll("\\A/*|/*\\z", "");
        return new File(this.getUnpackedDir(), path).getAbsolutePath();
    }


    /**
     * Implemented from ServletContext.
     */
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;  // return null as per spec JSR 116
    }


    /**
     * Implemented from ServletContext.
     */
    public URL getResource(String name) throws java.net.MalformedURLException {
        // Trim any leading or trailing "/" characters
        name = name.replaceAll("\\A/*|/*\\z", "");
        return this.getClassLoader().getResource(name);
    }


    /**
     * Implemented from ServletContext.
     */
    public InputStream getResourceAsStream(String name) {
        // Trim any leading or trailing "/" characters
        name = name.replaceAll("\\A/*|/*\\z", "");
        return this.getClassLoader().getResourceAsStream(name);
    }


    /**
     * Implemented from ServletContext.
     */
    public Set getResourcePaths(String path) {
        File file = new File(this.getUnpackedDir(), path);

        if (!file.exists()) {
            return null;
        }

        if (file.isFile()) {
            return new HashSet(1);
        }

        File[] files = file.listFiles();

        Set paths = new HashSet(files.length);

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
               paths.add("/" + files[i].getName());
            }
        }

        return paths;
    }


    /**
     * Implemented from ServletContext.
     */
    public String getServerInfo() {
        // WIP
        return null;
    }


    /**
     * Implemented from ServletContext.
     * @deprecated
     */
    public javax.servlet.Servlet getServlet(String name) throws javax.servlet.ServletException {
        // This method is deprecated, so return null.
        return null;
    }


    /**
     * Implemented from ServletContext.
     */
    public String getServletContextName() {
        return _displayName;
    }


    /**
     * Implemented from ServletContext.
     * @deprecated
     */
    public java.util.Enumeration getServletNames() {
        // This method is deprecated, so return null.
        return null;
    }


    /**
     * Implemented from ServletContext.
     * @deprecated
     */
    public java.util.Enumeration getServlets() {
        // This method is deprecated, so return null.
        return null;
    }


    /**
     * Implemented from ServletContext.
     */
    public void log(String msg) {
        _servletLogger.log(Level.INFO, msg);
    }


    /**
     * Implemented from ServletContext.
     * @deprecated
     */
    public void log(Exception exception, String msg) {
        _servletLogger.log(Level.ERROR, msg, exception);
    }


    /**
     * Implemented from ServletContext.
     */
    public void log(String msg, Throwable throwable) {
        _servletLogger.log(Level.ERROR, msg, throwable);
    }

    /**
     * Variables and methods for response time based overload control
     */
    private ArrayList timeMeasurementRules = new ArrayList();

    public ArrayList getTimeMeasurementRules() {
    	return timeMeasurementRules;
    }

    public void addTimeMeasurementRule(TimeMeasurementRule rule){
    	this.timeMeasurementRules.add(rule);
    }
    /**
     * in the context of the caller's principal(s).
     */
    private class PriviledgedActionImpl implements PrivilegedExceptionAction {

        private AseWrapper wrapper;
        private SasMessage message;

        public PriviledgedActionImpl(AseWrapper wrapper, SasMessage message) {
            this.wrapper = wrapper;
            this.message = message;
        }

        public Object run() throws Exception {
            // Construct the Permission to be checked.
            SipServletPermission permission = new SipServletPermission(getName(), wrapper.getName(), message.getMethod());

            if (_logger.isDebugEnabled()) {
                _logger.debug("Checking if caller has been granted the permission: " + permission.toString());
            }

            AccessController.checkPermission(permission);

            if (_logger.isDebugEnabled()) {
                _logger.debug("Caller was granted the permission.  Invoking Servlet...");
            }

            wrapper.processMessage(message);

            return null;
        }

    }

    public String getDisplayInfo(){
    	return this.appendApplicationInfo(null).toString();
    }

    public StringBuffer appendApplicationInfo (StringBuffer buffer){
		buffer = (buffer == null) ? new StringBuffer() : buffer;

		super.appendApplicationInfo(buffer);

		//Append the Display Name
		buffer.append("\r\nDisplayName = ");
		buffer.append(this._displayName);

		//Append the description
		if(this._description != null){
			buffer.append("\r\nDescription = ");
			buffer.append(this._description);
		}

		//Append the session timeout for this application
		buffer.append("\r\nApplication Session Timeout (mins) = ");
		buffer.append(this._appSessionTimeout);

		//Currently ACTIVE Application Sessions.
		buffer.append("\r\nNumber of ACTIVE Application Sessions = ");
		buffer.append(this.getAppSessionCount());

		if(this._securityManager != null){
			this._securityManager.toString(buffer);
		}

		// Append any CDR specific info...
		if(this._cdrContextWrapper!=null){
			for(CDRContext context: this._cdrContextWrapper.getCDRContexts()){
				//CDRContext context = this.getCDRContext[j];
		if (context != null) {
			CDRWriter[] writers = context.getWriters();
			if (writers != null) {
				for (int i = 0; i < writers.length; i++) {
					CDRWriter writer = writers[i];

					Object[] params = new Object[2];
					params[0] = writer.getCDRLocation();
					params[1] = writer.isWritable() ? _strings.getString("AseContext.cdrLocationUp") : _strings.getString("AseContext.cdrLocationDown");

					if (writer.isPrimary()) {
						buffer.append(_strings.getString("AseContext.primaryCDRLocation", params));
					} else {
						buffer.append(_strings.getString("AseContext.secondaryCDRLocation", params));
					}
				}
			}
		}
			}
		}
		return buffer;
    }

	/**
     * Invoked by the "start" method to deploy the specified application into
     * the web container.
     */
    private void startWebApp() throws ActivationFailedException {
        WebContainer webContainer = (WebContainer)Registry.lookup(Constants.NAME_WEB_CONTAINER);

        try {
            if (!this.isWebApp()){
               _logger.warn("Application is not a WEB app, so not deploying this web app");
               return;
            }
            if(this.getContextPath() == null || this.getContextPath().trim().equals("")){
               _logger.warn("Application does not have a Context Path,  so using the application name ");
               this.setContextPath(this.getObjectName());
            }

            if (_logger.isDebugEnabled()) {
               _logger.debug("Context Path of the application :" + getContextPath());
            }

			httpFlag = true;
			if (this.getOldDeployableObject() == null) {
                webContainer.deploy(getContextPath(), this);
			} else {
            	if (!webContainer.isDeployed(getContextPath())) {
                	webContainer.deploy(getContextPath(), this);
            	} else{
					webContainer.upgrade(getContextPath(), this);
				}
			}
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new ActivationFailedException(e.toString());
        }
    }

   /**
     * Invoked by the "stop" method to undeploy the specified application from
     * the web container.
     */
    private void stopWebApp() throws DeactivationFailedException {
        WebContainer webContainer = (WebContainer)Registry.lookup(Constants.NAME_WEB_CONTAINER);

        try {
            if ((getContextPath() != null) && webContainer.isDeployed(getContextPath())) {
				if (httpFlag == true) {
					AbstractDeployableObject newDeployable = this.getNewDeployableObject();
					if (newDeployable != null) {
						if (!newDeployable.getContextPath().equals(getContextPath())) {
                			webContainer.undeploy(getContextPath());
						}
					} else {
                		webContainer.undeploy(getContextPath());
					}
            	}
			}
        } catch (Exception e) {
            _logger.error(e.toString(), e);
            throw new DeactivationFailedException(e.toString());
        }
    }

    public void addDefaultHandler(String resourceName, String handlerName) {

    	//If the Default Handler for this resource is already set,
    	//then simply return from here.
    	if(this.getDefaultHandlerName(resourceName) != null)
    		return;

    	AseWrapper wrapper = (AseWrapper)this.findChild(handlerName);
    	if(wrapper == null){
    		throw new IllegalArgumentException("The handler name specified is not valid.");
    	}

    	if(!(wrapper.getWrappedObject() instanceof MessageHandler)){
    		throw new IllegalArgumentException("The handler does not implement the interface :" + MessageHandler.class.getName());
    	}

    	this._defaultHandlers.put(resourceName, handlerName);
    }


   /**
    * this makes a list of workManagerImpl object
	* this is called by WorkManagerFactory.java
	* @param p_workManager
	*/
	public void addWmToList(WorkManagerImpl p_workManager) {

		_wmList.add(p_workManager);
	}

   /**
    * this makes a list of as many ResourceReference object
	* as the resource-ref's are given by the application in sip.xml
	* @param p_resRef
	*/
	public void addResourceReferenceList(ResourceReference p_resRef) {

		_resRefList.add(p_resRef);
	}

	/**
	 * This method is added for periodic dump of all Applications deployed
	 */

	public void process(long currenttime)  {
		notifyCount = 0;
	}

	private void registerForBackgroundProcess()  {
		try  {
			AseBackgroundProcessor processor = (AseBackgroundProcessor)Registry.lookup(Constants.BKG_PROCESSOR);
			processor.registerBackgroundListener(this, 1); // rate of NOTIFY generation is given per second
		} catch (Exception e)  {
			_logger.error(e.getMessage(), e);
		}
	}

	private boolean initializeContext() throws NamingException {

		try {
		    environment = new Hashtable();
			ConfigRepository m_configRepository    = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		    String initialcontextfactory=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_CONTEXT_FACTORY);
			if (_logger.isInfoEnabled()) {
    
		    _logger.info("INITIAL_CONTEXT_FACTORY===> "+initialcontextfactory);
			}
		    String providerurl=(String)m_configRepository.getValue(Constants.OID_JNDI_JDBC_PROVIDER_URL);
			if (_logger.isInfoEnabled()) {

		    _logger.info("PROVIDER_URL======> "+providerurl);
			}
		    environment.put(Context.INITIAL_CONTEXT_FACTORY,initialcontextfactory);
		    environment.put(Context.PROVIDER_URL,providerurl);
		    ctx=new InitialContext(environment);
		    if (_logger.isInfoEnabled()) {
		    _logger.info("Initial context has been initialized to: "+ctx);
			}
		    return true;

		}catch(NamingException e) {
			_logger.error(e);
		    throw e;
		}catch(Exception ee) {
			_logger.error(ee);
		    return false;
	    }
	}

	
	private boolean initializeJMSObjects() throws NamingException {

		try {
			
			ConfigRepository m_configRepository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

			Queue queue = null;
			Topic topic = null;
			String connFactory = null;
		    ActiveMQConnectionFactory connectionFactory = null; 
		    Hashtable env = null;
			
			String properties_path = (String)m_configRepository.getValue(Constants.PROP_JNDI_JMS_PROPERTIES);
			InputStream input = new FileInputStream(properties_path);
			props = new OrderedProperties();
			props.load(input);
			if (_logger.isDebugEnabled()) {

			_logger.debug("Properties file loaded from " + properties_path);
			}		
			Factory jms_factory = new Factory();

			connFactory = props.getProperty("connectionFactoryNames");
			StringTokenizer temp = new StringTokenizer(connFactory,",");
			List connectionFactoryNames = new ArrayList();
			while(temp.hasMoreTokens()) {
				connectionFactoryNames.add(temp.nextToken().trim());
			}
		
			Enumeration keyset = props.propertyNames();
			while(keyset.hasMoreElements()) {
				String key = (String)keyset.nextElement();
				
				if(key.equals("java.naming.provider.url")) {
					if(connectionFactoryNames.size() == 1) {
						if(factoryList.contains(connFactory)) {
							env = new Hashtable();
							env.put(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
							env.put(Context.PROVIDER_URL, props.getProperty(key));
							connectionFactory = jms_factory.getConnectionFactory(connFactory, env);
							ctx.rebind(connFactory, connectionFactory);
							if (_logger.isDebugEnabled()) {

							_logger.debug(connFactory + " Factory bound successfully to the context");
						}
					}
					}
					else {
						_logger.error("Only one connection factory allowed for property "+ key + " ,Returning");
						return false;
					}
				}
				else if(key.startsWith("queue.")) {
					key = key.substring(6);
					if(queueList.contains(key)) {
						queue = jms_factory.getQueue(key);
						ctx.rebind(key,queue);
						if (_logger.isDebugEnabled()) {

						_logger.debug(key + " Queue bound successfully to the context");
					}
				}
				}
				else if(key.startsWith("topic.")) {
					key = key.substring(6);
					if(topicList.contains(key)) {
						topic = jms_factory.getTopic(key);
						ctx.rebind(key,topic);
						if (_logger.isDebugEnabled()) {

						_logger.debug(key + " Topic bound successfully to the context");
					}
				}
				}
				else if(key.startsWith("connection.")) {
					String factory_name = null;
					StringTokenizer st = new StringTokenizer(key,".");
					String value = props.getProperty(key);

					while(st.hasMoreTokens()) {
						String garbage = st.nextToken();   // contains String "connection." - so just discard it
						factory_name = st.nextToken();
						if(factoryList.contains(factory_name) && connectionFactoryNames.contains(factory_name)) {
							String property = st.nextToken();
							if(property.equals("brokerURL")) {
								env = new Hashtable();
								env.put(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
								env.put(Context.PROVIDER_URL, value);
								connectionFactory = jms_factory.getConnectionFactory(factory_name, env);
								ctx.rebind(factory_name, connectionFactory);
								if (_logger.isDebugEnabled()) {

								_logger.debug(factory_name + " Factory bound successfully to the context");
							}
							}
							else if(property.equals("alwaysSyncSend")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setAlwaysSyncSend(Boolean.parseBoolean(value));
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("clientID")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setClientID(value);
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("clientIDPrefix")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setClientIDPrefix(value);
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("closeTimeout")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setCloseTimeout(Integer.parseInt(value));
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("copyMessageOnSend")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setCopyMessageOnSend(Boolean.parseBoolean(value));
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("disableTimeStampsByDefault")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setDisableTimeStampsByDefault(Boolean.parseBoolean(value));
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("dispatchAsync")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setDispatchAsync(Boolean.parseBoolean(value));
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("exclusiveConsumer")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setExclusiveConsumer(Boolean.parseBoolean(value));
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("objectMessageSerializationDeferred")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setObjectMessageSerializationDefered(Boolean.parseBoolean(value));
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("password")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setPassword(value);
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("producerWindowSize")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setProducerWindowSize(Integer.parseInt(value));
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("statsEnabled")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setStatsEnabled(Boolean.parseBoolean(value));
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("useCompression")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setUseCompression(Boolean.parseBoolean(value));
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("useRetroactiveConsumer")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setUseRetroactiveConsumer(Boolean.parseBoolean(value));
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("userName")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setUserName(value);
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else if(property.equals("warnAboutUnstartedConnectionTimeout")) {
								if(ctx.lookup(factory_name) != null) {
									ActiveMQConnectionFactory tempFactory = (ActiveMQConnectionFactory)ctx.lookup(factory_name);
									tempFactory.setWarnAboutUnstartedConnectionTimeout(Long.parseLong(value));
									ctx.rebind(factory_name, tempFactory);
								}
							}
							else
								_logger.error("Incorrect property "+property+" defined for connection " + factory_name);
						}
						else {
							break;
						}
					}
				}
			}

			return true;
		}catch(NamingException e) {
			_logger.error(e);
			throw e;
		}catch(Exception ee) {
			_logger.error(ee);
			return false;
		}
	}


	/**
	 * This method is called at the time of deploy
	 * it calls the bind() on initialContext if bindName
	 * is properly given
	 */
	private void bind() {
		if (_logger.isInfoEnabled()) {
		
		_logger.info("Initializing Context");
		}
		try {
			initializeContext();
		} catch (NamingException nameExp) {
			_logger.error("Error in initializing context" + nameExp);
		}

		for(Iterator i = _resRefList.iterator(); i.hasNext(); ) {
			ResourceReference resRef = (ResourceReference)i.next();
			String bindName = resRef.getResourceRefName();
			String type = resRef.getResourceType();
			if (_logger.isDebugEnabled()) {

			_logger.debug("BindName is = " +bindName + "  and type is = " + type);
			}
			if((bindName != null)||(bindName != "")) {
				if(type.equals("commonj.work.WorkManager")) {
					if (_logger.isDebugEnabled()) {

					_logger.debug("binding WorkManager");
					}
					String appId = this.getName();

					WorkManagerImpl bindObject = new WorkManagerImpl(bindName, appId);
					if (_logger.isDebugEnabled()) {

					_logger.debug("binding WorkManagerobjecti = " + bindObject + " with bindName = " + bindName);
					}
					try {
						ctx.rebind(bindName, bindObject);
						if (_logger.isDebugEnabled()) {

						_logger.debug("WorkManager successfully bound");
						_logger.debug("hashcode for WorkManager object bound = " + bindObject.hashCode());
						}
					}catch(NamingException e) {
						_logger.error(e);
					}
				}
				else if(type.equals("javax.jms.ConnectionFactory")) {
					factoryList.add(bindName);
				}
				else if(type.equals("javax.jms.Queue")) {
					queueList.add(bindName);
				}
				else if(type.equals("javax.jms.Topic")) {
					topicList.add(bindName);
				}
			}
		}
		
		if(!factoryList.isEmpty())
		{
			try {
				Boolean success = initializeJMSObjects();
				if(success)
					if (_logger.isDebugEnabled()) {

					_logger.debug("JMS Objects successfully bound");
					}
				else
					_logger.error("Error in binding JMS Objects");
			}catch(NamingException e) {
				_logger.error(e);
			}
		}
	}
	

	/**
	 * This method is called at the time of undeploy
	 * it calls the unbind() on initialContext if bindName
	 * is found
	 */
	private void unbind() {
		if (_logger.isDebugEnabled()) {

		_logger.debug("Unbind called on Initial Context");
		}
		try{
		
			for(Iterator i = _resRefList.iterator(); i.hasNext(); ) {
				ResourceReference resRef = (ResourceReference)i.next();
				String bindName = resRef.getResourceRefName();
				String type = resRef.getResourceType();

				if(type.equals("commonj.work.WorkManager")) {
					ctx.unbind(bindName);
					if (_logger.isInfoEnabled()) {

					_logger.info("WorkManager is successfully unbounded to "+bindName);
				}
				}
				else if(type.equals("javax.jms.ConnectionFactory")) {
					ctx.unbind(bindName);
					if (_logger.isInfoEnabled()) {

					_logger.info("ConnectionFactory is successfully unbounded to "+bindName);
				}
				}
				else if(type.equals("javax.jms.Queue")) {
					ctx.unbind(bindName);
					if (_logger.isInfoEnabled()) {

					_logger.info("Queue is successfully unbounded to "+bindName);
				}
				}
				else if(type.equals("javax.jms.Topic")) {
					ctx.unbind(bindName);
					if (_logger.isInfoEnabled()) {

					_logger.info("Topic is successfully unbounded to "+bindName);
				}
		}
		}
			return;

		}catch(NamingException e) {
			_logger.error(e.toString(), e);
			return;
		}
	}


	/**
	 *	This method adds a resource name to a list in AseContext.This 
	 *	list contains all the resources name the application corrosponding
	 *	to this AseContext specified in its sas.xml
	 *
	 *	@param name -Name of the resource to be added in the list.
	 */
	public void addResourceName(String name){
		if(_logger.isDebugEnabled()){
			_logger.debug("Adding resource name "+name);
		}
		m_resourceNames.add(name);

	}

	/**
	 *	This method returns an iterator on the which list contains 
	 *	all the resources name the application corrosponding
	 *	to this AseContext specified in its sas.xml
	 *
	 *	@return -Iterator on the name of the resources.
	 */
	public Iterator getResourceNames(){
		if(_logger.isDebugEnabled()){
			_logger.debug("Inside getResourceName()");
		}
		return m_resourceNames.iterator();
	}

	//JSR 289.42


	public AseAnnotationInfo getApplicationKeyAnnoInfo() {
		return applicationKeyAnnoInfo;
	}

	public void setApplicationKeyAnnoInfo(AseAnnotationInfo applicationKeyAnnoInfo) {
		this.applicationKeyAnnoInfo = applicationKeyAnnoInfo;
	}

	//JSR 289.42
	public boolean isEnableAnnotation() {
		return isEnableAnnotation;
	}

	public void setEnableAnnotation(boolean isEnableAnnotation) {
		this.isEnableAnnotation = isEnableAnnotation;
	}

	public boolean isEnableLibAnnotation() {
		return isEnableLibAnnotation;
	}

	public void setEnableLibAnnotation(boolean isEnableLibAnnotation) {
		this.isEnableLibAnnotation = isEnableLibAnnotation;
	}

	@Override
	public Dynamic addFilter(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dynamic addFilter(String arg0, Filter arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Dynamic addFilter(String arg0, Class<? extends Filter> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void addListener(String arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Servlet arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0,
			Class<? extends Servlet> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> arg0)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> arg0)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> arg0)
			throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void declareRoles(String... arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEffectiveMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEffectiveMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilterRegistration getFilterRegistration(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServletRegistration getServletRegistration(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setInitParameter(String name, String value) {
		  if(_logger.isDebugEnabled()){
	            _logger.debug("Adding init-param :" + name + ", " + value + ", " + this.getId() + ", " + this);
	        }
	        _initParams.put(name, value);
		return false;
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0)
			throws IllegalStateException, IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}
}

class OrderedProperties extends Properties {

	public OrderedProperties() {
		super ();

		_names = new Vector();
	}

	public Enumeration propertyNames() {
		return _names.elements();
	}

	public Object put(Object key, Object value) {
		if (_names.contains(key)) {
			_names.remove(key);
		}

		_names.add(key);

		return super .put(key, value);
	}

	public Object remove(Object key) {
		_names.remove(key);

		return super .remove(key);
	}

	private Vector _names;

}
class Factory extends ActiveMQInitialContextFactory {
	
	ActiveMQConnectionFactory factory;
	Queue queue;
	Topic topic;
	
	public ActiveMQConnectionFactory getConnectionFactory(String name,Hashtable env) {
		try {
		factory = createConnectionFactory(name,env);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return factory;
	}

	public Queue getQueue(String queue_name){
		queue = createQueue(queue_name);
		return queue;
	}

	public Topic getTopic(String topic_name) {
		topic = createTopic(topic_name);
		return topic;
	}
}
