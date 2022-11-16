package com.baypackets.ase.deployer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;
import java.util.jar.JarInputStream;
import java.util.jar.JarEntry;

import javax.servlet.sip.ar.SipApplicationRouter;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.Lifecycle;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.container.AppClassLoader;
import com.baypackets.ase.startup.AseClassLoader;
import com.baypackets.ase.container.AseHost;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.WebContainer;
import com.baypackets.ase.container.WebContainerState;
import com.baypackets.ase.container.exceptions.ActivationFailedException;
import com.baypackets.ase.container.exceptions.UpgradeFailedException;
import com.baypackets.ase.container.exceptions.DeactivationFailedException;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.container.exceptions.UndeploymentFailedException;
import com.baypackets.ase.container.exceptions.RedeploymentFailedException;
import com.baypackets.ase.dispatcher.RulesRepository;
import com.baypackets.ase.spi.container.SasApplication;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.spi.replication.ReplicationManager;
import com.baypackets.ase.spi.resource.ResourceContext;
import com.baypackets.ase.container.ResourceContextImpl;
import com.baypackets.ase.spi.deployer.DeploymentListener;
import com.baypackets.ase.util.AseAlarmService;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.FileUtils;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.soa.iface.SoaContext;
import com.baypackets.ase.soa.SoaContextImpl;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import com.baypackets.ase.sbbdeployment.SbbContext;
import com.baypackets.ase.sbbdeployment.SbbDeployer;
import com.baypackets.ase.sipconnector.AseSipConnector;
import com.baypackets.ase.measurement.AseMeasurementManager;
import com.baypackets.ase.router.AseSipApplicationRouterManager;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

public abstract class DeployerImpl implements Deployer, Lifecycle {

    private static StringManager _strings = StringManager.getInstance(AseHost.class.getPackage());
    private static Logger logger = Logger.getLogger(DeployerImpl.class);

    private static String ARCHIVE_DIR_NAME = "archives".intern();
    protected AseHost host = null;
    private DDHandlerFactory ddHandlerFactory = new DDHandlerFactoryImpl();
    private HashMap listeners = new HashMap();
    protected DeployableObjectDAO dataAccessObject = null;
    private ArrayList deployables = new ArrayList<AbstractDeployableObject>();
    protected boolean started = false;
    protected boolean nsaUpgrade = false;
    Set<String> allowedOptionsApplicationNames=null;
    private static boolean optionsAppStarted=false;
   private static  boolean optionsApplicationRaisedAlarm=false;
   private static  boolean optionsApplicationClearingAlarm=false;
    
    //private static String SERVICE="Service".intern();
    private static String TROUBLE_SUB_SYS="+ServiceSubsysName+";
    private static String PLUS="+";
    /*private static String SERVICE_DEPLOYED="is deployed on Server";
    private static String SERVICE_STARTED="is started on Server";
    private static String SERVICE_ACTIVATED="is activated on Server";
    private static String SERVICE_DEACTIVATED="is deactivated on Server";
    private static String SERVICE_STOP="is stopped on Server";
    private static String SERVICE_UNDEPLOYED="is undeployed from Server";*/

    public DeployerImpl() {
        super();
    }

    public void initialize() throws Exception
    {
        if(logger.isDebugEnabled()) {
            logger.debug("Initialize called on Deployer :" + this.getType());
        }

        
        ConfigRepository m_configRepository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
        nsaUpgrade = Boolean.parseBoolean(m_configRepository.getValue(Constants.PROP_NSA_UPGRADE));
       if(this.allowedOptionsApplicationNames ==null ) {
    	   this.allowedOptionsApplicationNames= new HashSet<String>();
           
           String allowedApps = m_configRepository.getValue(Constants.APP_ROUTER_OPTIONS_HANDLING_APPS);
   		if(StringUtils.isNotBlank(allowedApps)) {
   			if(allowedApps.contains(AseStrings.COMMA)) {
   				StringTokenizer tokenizer = new StringTokenizer(allowedApps, AseStrings.COMMA);
   				while(tokenizer.hasMoreTokens()) {
   					allowedOptionsApplicationNames.add(tokenizer.nextToken());
   				}
   			}else {
   				allowedOptionsApplicationNames.add(allowedApps);
   			}
   		}
       }
        
        this.host = (AseHost) Registry.lookup(Constants.NAME_HOST);
        if(this.getDAOClassName() != null)
        {
            Class daoClazz = Class.forName(this.getDAOClassName());
            this.dataAccessObject = (DeployableObjectDAO) daoClazz.newInstance();
            this.dataAccessObject.setDeployer(this);
        }
        if(this.dataAccessObject != null)
        {
            ArrayList temp = this.dataAccessObject.load(this.getType());

                Iterator iterator = temp.iterator();
            for (;iterator.hasNext();) {
                    AbstractDeployableObject deployable = (AbstractDeployableObject)iterator.next();

                if (logger.isDebugEnabled()) {
                    logger.debug("Loaded the following deplyable from datastore: " + deployable.getId());
                }

                try {
                            String deployedBy = deployable.getDeployedBy();
                        if(deployable.getExpectedState() == DeployableObject.STATE_UNINSTALLED ||
                            (deployable.getExpectedState() == DeployableObject.STATE_UPGRADE) ||
                        (deployedBy != null && (deployedBy.equals(Deployer.CLIENT_EMS)))) {
                        if (logger.isInfoEnabled()) {
                            logger.info("Removing deployable ==> "+deployable.getId());
                        }
                        this.dataAccessObject.remove(deployable);
                    } else {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Adding deployable ==> " + deployable);
                          
                        }
                            this.deployables.add(deployable);
                            String []app= deployable.getId().split("_");
                            if(allowedOptionsApplicationNames.contains(app[0])){
                            	DeployerImpl.optionsAppStarted=true;
                            	DeployerImpl.optionsApplicationRaisedAlarm=true;
                            }
                        }
                        
                        
                } catch (Exception e) {
                    logger.error("Error occurred while removing AseContext object", e);
                   }
            }
            
            
         
            
            if(!optionsAppStarted && !optionsApplicationRaisedAlarm) {
            	// raise alarm 
            	if(logger.isDebugEnabled()) {
            		logger.debug("No options applicable application deployed raising alarm");
            	}
            	optionsApplicationRaisedAlarm=true;
            	optionsApplicationClearingAlarm=true;
            	this.reportAlarm(Constants.ALARM_OPTIONS_APPLICATION_NOT_DEPLOYED, "No Options handling application are yet active to handle options.");
            }else if(optionsAppStarted && optionsApplicationClearingAlarm ) {
            	if(logger.isDebugEnabled()) {
            		logger.debug("Options applicable application deployed raising clearing alarm");
            	}
            	optionsApplicationClearingAlarm=false;
            	this.reportAlarm(Constants.ALARM_OPTIONS_APPLICATION_DEPLOYED, " Options can be handled by the active applications now.");
            }
            
        }
    }

    public boolean isRunning() {
        return true;
    }

    public void start() throws StartupFailedException {
        if(logger.isDebugEnabled()){
            logger.debug("Start called on Deployer :" + this.getType());
        }
        Iterator iterator = this.findAll();
        for(;iterator.hasNext();){
            try{
                AbstractDeployableObject deployable = (AbstractDeployableObject) iterator.next();
                deployable.setState(DeployableObject.STATE_UNINSTALLED);

                this.checkExpectedState(deployable.getId(), false);
            }catch(Exception e){
                logger.error(e.getMessage(), e);
            }
        }

        validateUpgradation();

        /* - This is not needed the checkExpectedState will end up calling this interface method.
        // Notify app-router if available
        SipApplicationRouter ar = AseSipApplicationRouterManager.getRouterInstance();
        if (ar != null) {
            List apps = this.getAppNames();
            apps.remove(Constants.NAME_SOAP_SERVER_AXIS);
            if (!apps.isEmpty()) {
                ar.applicationDeployed((List<String>) apps);
            }
        }
        */

        this.deployables.clear();
        this.started = true;
    }

    public void stop() throws ShutdownFailedException {
    }

    public synchronized DeployableObject  deploy(String name, String version, int priority,
                            String contextPath, InputStream stream, String deployedBy)
                            throws DeploymentFailedException {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering deploy() with 6 arg: name, version, priority, contextPath, stream, deployedBy");
        }
        AbstractDeployableObject deployable =  null;
        try{
            byte[] binary = this.getByteArray(stream);
            deployable = this.preDeploy(deployable, name, version, priority, binary);
            deployable.setContextPath(contextPath);

            this.deploy(deployable, binary, false, deployedBy);
        }catch(Exception e){
        	if(logger.isDebugEnabled()) {
        		logger.debug("deploy()", e);
        	}
            throw new DeploymentFailedException(e.getMessage(), e);
        }

        return deployable;
    }

    //This method has been added for the SAS IDE support for Redeployment of an application
    public synchronized DeployableObject  redeploy(String name, String version, int priority,String contextPath, InputStream stream, String deployedBy) throws RedeploymentFailedException {

        AbstractDeployableObject deployable =  null;
        try {
            byte[] binary = this.getByteArray(stream);
            deployable = this.preDeploy(deployable, name, version, priority, binary);
            AbstractDeployableObject temp = (AbstractDeployableObject)this.host.findChild(deployable.getId());

            if(temp!=null) {
                temp.setExpectedState(DeployableObject.STATE_UNINSTALLED);
                            this.checkExpectedState(temp.getId(), false);

                if(temp.getExpectedState() == DeployableObject.STATE_UNINSTALLED ||
                    (deployedBy != null && deployedBy.equals(Deployer.CLIENT_IDE))) {
                                        this.dataAccessObject.remove(temp);
                    }

                try {
                    DeployableObject tempObject = (DeployableObject)this.host.findChild(deployable.getId());

                    this.host.removeChild(temp);
                    if(this.dataAccessObject != null) {
                        this.dataAccessObject.remove(temp);
                    }
                } catch(Exception e) {
                    logger.error("Removing child from host", e);
                }
            }

            this.deploy(deployable, binary, false, deployedBy);
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("redeploy()", e);
			}
			throw new RedeploymentFailedException(e);
		}

        return deployable;
    }

    protected synchronized DeployableObject deploy(AbstractDeployableObject deployable,
                                        byte[] binary, boolean upgrade, String deployedBy)
                                        throws DeploymentFailedException {

        if (logger.isDebugEnabled()) {
            logger.debug("Entering deploy() with 4 arg: AbstractDeployableObject, byte[], boolean, String ");
        }
        boolean success = false;
        try{
            //If the Stream is NULL, the DDs might not have been parsed.
            //So parse the DDs first.
            //Otherwise store the archive file.
            if(binary == null){
                binary = this.getByteArray(deployable.getArchive().openStream());
                this.preDeploy(deployable, null, null, -1, binary);
            }

            //Check the ID of the Deployable Objects.
            if(deployable.getId() == null){
                throw new DeploymentFailedException("Not able to get the name for this Deployable Object.");
            }

            //Check whether there are any other object with the same ID.
            DeployableObject temp = (DeployableObject)this.host.findChild(deployable.getId());
            if(temp != null){
                throw new DeploymentFailedException("Object with the same ID already exists." + deployable.getId());
            }

            //Check if not upgrade, if there is an object with the same name.
            if(!upgrade){
                AseContainer[] children = this.host.findChildren();
                if(children != null) {
                    for(int i=0; i<children.length  ; i++) {
	                    DeployableObject ctx = (DeployableObject)children[i];
	                    if(ctx.getObjectName().equals(deployable.getObjectName())) {
	                    	throw new DeploymentFailedException("Object with the same name: " + deployable.getObjectName() + " already exists.");
	                    }
	                }
                }
                Iterator itr = this.findByName(deployable.getDeploymentName());
                if(itr.hasNext()){
                    throw new DeploymentFailedException("Object with the same deployment name: " + deployable.getDeploymentName() + " already exists.");
                }
            }

            //If the binary is NOT NULL and not that of SOAP Server, store it.
            if ((binary != null) && (deployable.getType() != DeployableObject.TYPE_SOAP_SERVER)
            					 && (deployable.getType() != DeployableObject.TYPE_SBB)) {
                this.storeArchive(deployable, new ByteArrayInputStream(binary));
            }

            //Now deploy and add it to the host.
            deployable.deploy();
            deployable.setDeployedBy(deployedBy);
            this.host.addChild(deployable);

            //If the Data Access Object(DAO) is not NULL, persist it.
            if(this.dataAccessObject != null){
                this.dataAccessObject.persist(deployable);
            }
            
            // BUG-9936 TCAP FT Changes in App Class Loader
            if ( deployable.getDeploymentName().equalsIgnoreCase(Constants.TCAP_PROVIDER_APP_NAME)) {
            	if(logger.isDebugEnabled()) {
            		logger.debug("Class Loader for tcap-provider = " + deployable.getClassLoader());
            	}
            	((AppClassLoader)deployable.getClassLoader()).setAppName(Constants.TCAP_PROVIDER_APP_NAME);
            	this.host.setTcapProviderCL(deployable.getClassLoader());
            }
            
            // BUG-6765 [LIVE SBB UPGRADE]
            if ( deployable.getType() == DeployableObject.TYPE_SBB ) {
            	if(logger.isDebugEnabled()) {
            		logger.debug("Class Loader for SBB = " + deployable.getClassLoader());
            	}
            	this.host.setLatestSbbCL(deployable.getClassLoader());
            	if(logger.isDebugEnabled()) {
            		logger.debug("SBB version " + deployable.getVersion() + " is successfully deployed.");
            	}
            }
            
            success = true;
        }catch(Exception e){
        	if(logger.isDebugEnabled()) {
        		logger.debug("deploy()", e);
        	}
            throw new DeploymentFailedException(e);
        }finally{
            this.notifyListeners(deployable, DeployableObject.STATE_UNINSTALLED, deployable.getState(), success);
        }
        return deployable;
    }

    public DeployableObject deploy(InputStream stream, String deployedBy)
                throws DeploymentFailedException {

        if (logger.isDebugEnabled()) {
            logger.debug("Entering deploy method with 2 arg: InputStream, deployerdBy");
        }

        return this.deploy(null, null, -1, null, stream, deployedBy);
    }

    public synchronized DeployableObject upgrade(String appName, String version,
        int priority, InputStream stream) throws UpgradeFailedException {
        this.checkState();
        AbstractDeployableObject deployable =  null;
        try{
            byte[] binary = this.getByteArray(stream);
            deployable = this.preDeploy(deployable, appName, version, priority, binary);

            //Validate the Upgrade process.
            Iterator it = this.find(this.getType(), deployable.getDeploymentName(), null);
            AbstractDeployableObject prevDeployable = it.hasNext() ? (AbstractDeployableObject) it.next() : null;

            if (prevDeployable == null) {
                throw new UpgradeFailedException(_strings.getString("AseHost.appNotFound", appName));
            }
            if (deployable.getVersion().equals(prevDeployable.getVersion())) {
                throw new UpgradeFailedException(_strings.getString("AseHost.duplicateAppVersions", appName, version));
            }
            if (! deployable.getObjectName().equals(prevDeployable.getObjectName())) {
                throw new UpgradeFailedException(_strings.getString("AseHost.invalidObjectName"));
            }
            //Now deploy the new version of the application.
            deployable.setContextPath(prevDeployable.getContextPath());
            this.deploy(deployable, binary, true, prevDeployable.getDeployedBy());

            prevDeployable.setNewDeployableObject(deployable);
            deployable.setOldDeployableObject(prevDeployable);

            if (prevDeployable.getState() == DeployableObject.STATE_READY) {
                this.start(deployable);
            }

            // Handle app upgrade case...
            if (prevDeployable.getState() == DeployableObject.STATE_ACTIVE) {
                RulesRepository repository = (RulesRepository)Registry.lookup(Constants.RULES_REPOSITORY);

                try {
                    repository.lock();

                    repository.removeRulesForApp(prevDeployable.getName());
                    this.start(deployable);
                    this.activate(deployable);
                    repository.unlock();
                } catch (Exception e) {
                    logger.error(e.toString(), e);

                    //Removed the automatic rollback from here.
                    //So that the user has to manually take care of application UPGRADE failure.
                    throw new UpgradeFailedException(e.toString(), e);
                }finally{
                    repository.unlock();
                }
            }

            prevDeployable.setExpectedState(DeployableObject.STATE_UNINSTALLED);
            prevDeployable.setState(DeployableObject.STATE_UPGRADE);
            if(prevDeployable instanceof AseContext){
                if((((AseContext)prevDeployable).getAppSessionCount() == 0)
                		&& TcapSessionCount.getInstance().getDialogueCount(prevDeployable.getObjectName()+"_"+prevDeployable.getVersion()) == 0) {
                    this.checkExpectedState(prevDeployable.getId(), false);
                } else {
                	if(logger.isDebugEnabled()) {
                		logger.debug("App Session Count is not 0 : Not removing");
                	}
                }
            }
            //BUG-6765 [LIVE SBB UPGRADE]
            if(prevDeployable instanceof SbbContext){
            	if(logger.isDebugEnabled()) {
            		logger.debug("Removing the already deployed version of SBB");
            	}
            	this.checkExpectedState(prevDeployable.getId(), false);
            }
            
            if ( deployable.getType() != DeployableObject.TYPE_SBB ) {
            	persistUpgrade(prevDeployable);
            }
            
        }catch(Exception e){
        	if(logger.isDebugEnabled()) {
        		logger.debug("upgrade()", e);
        	}
            throw new UpgradeFailedException(e.getMessage(), e);
        }
        return deployable;
    }

    protected void persistUpgrade(AbstractDeployableObject deployable)
    {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering persistUpgrade(AbstractDeployableObject)");
        }

        boolean success = false;
                short prevState = deployable.getState();
        deployable.setUpgradeState(true);
                try{
                        if(this.dataAccessObject != null){
                                this.dataAccessObject.persist(deployable);
                        }
                        success = true;
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("persistUpgrade()", e);
			}
		} finally {
			this.notifyListeners(deployable, prevState, deployable.getState(), success);
		}
    }

    public DeployableObject upgrade(InputStream stream)
            throws UpgradeFailedException {
        return this.upgrade(null, null, -1, stream);
    }

    public DeployableObject undeploy(String id) throws UndeploymentFailedException {
        this.checkState();
        if (logger.isDebugEnabled()) {
            logger.debug("Going to undeploy the application: " + id);
        }
        AbstractDeployableObject deployable = (AbstractDeployableObject)this.findById(id);
        if (deployable == null) {
            throw new UndeploymentFailedException(_strings.getString("AseHost.undeployFailed", id));
        }
        this.undeploy(deployable);

        if (logger.isInfoEnabled()) {
            logger.info("Application was undeployed successfully.");
        }

        return deployable;
    }

    public synchronized void undeploy(AbstractDeployableObject deployable) throws UndeploymentFailedException {

        boolean success = false;
        short prevState = deployable.getState();
        if(!(prevState == DeployableObject.STATE_ERROR ||
                prevState == DeployableObject.STATE_INSTALLED ||
                prevState == DeployableObject.STATE_STOPPING)){
            throw new IllegalStateException("Object cannot be undeployed from State :" + deployable.getStatusString());
        }
		if(deployable instanceof AseContext){
			Iterator itr = ((AseContext)deployable).getResourceNames();
			if(itr.hasNext()){
				DeployerFactory factory = (DeployerFactory)Registry.lookup(DeployerFactory.class.getName());
				Deployer deployer = factory.getDeployer(DeployableObject.TYPE_RESOURCE);
				String resourceName;
				Iterator it;
				ResourceContext resourceCtx; 
				while(itr.hasNext()){
					resourceName = (String)itr.next();	
					it = deployer.findByName(resourceName);
					resourceCtx = it.hasNext() ? (ResourceContext)it.next() : null;
					if(resourceCtx!=null){
						if(logger.isDebugEnabled()){
							logger.debug("unregistering app with the resource ");
						}
						((ResourceContextImpl)resourceCtx).unregisterApp((AseContext)deployable);
					}else{
						logger.error("No resource context found for resource "+resourceName);
					}
				} //while ends
        	}
			
			//BUG-6765 [LIVE SBB UPGRADE]
			if(((AseContext) deployable).getUsesSBB() == true) {
				DeployerFactory factory = (DeployerFactory)Registry.lookup(DeployerFactory.class.getName());
				SbbDeployer sbbdeployer = (SbbDeployer) factory.getDeployer(DeployableObject.TYPE_SBB);
				
				sbbdeployer.unregisterApp((AseContext)deployable);
			}
		}
        try{
            if(prevState == DeployableObject.STATE_STOPPING){
                deployable.setExpectedState(DeployableObject.STATE_UNINSTALLED);
            }else{
                deployable.undeploy();
            }

            this.host.removeChild(deployable);
            if(this.dataAccessObject != null){
                this.dataAccessObject.remove(deployable);
            }
            success = true;
        }catch(Exception e){
        	if(logger.isDebugEnabled()) {
        		logger.debug("undeploy()", e);
        	}
            throw new UndeploymentFailedException(e);
        }finally{
        	//Removing replication Context when service is undeployed
        	    	 	 
        	ReplicationManager m_replicationMgr = (ReplicationManager) Registry
        	        						.lookup(Constants.NAME_REPLICATION_MGR);
        	m_replicationMgr.removeContextsForAppId(deployable.getId());
            this.notifyListeners(deployable, prevState, deployable.getState(), success);
        }
        
        /* *************************
    	 *  Called for the case when the application has defined its 
    	 *  own measurement counters. 
    	 *  The name and the measurement config path corresponding to 
    	 *  this serviceName is removed from a list in case of undeploy()  
    	 */
        AseMeasurementManager.instance().removeServiceName(deployable.getId());
        
        /* **************************/
    }

    public DeployableObject start(String id) throws StartupFailedException {
        this.checkState();

        if (logger.isInfoEnabled()) {
            logger.info("Going to start the application" + id);
        }

        AbstractDeployableObject deployable = (AbstractDeployableObject)this.findById(id);
        if (deployable == null) {
            throw new StartupFailedException(_strings.getString("AseHost.startFailed", id));
        }

        this.start(deployable);

        if (logger.isInfoEnabled()) {
            logger.info("Application was started successfully.");
        }

        return deployable;
    }

    public synchronized void start(AbstractDeployableObject deployable) throws StartupFailedException {
        boolean success = false;
        short prevState = -1;
        if((deployable.getType() != DeployableObject.TYPE_PURE_SOA) && (deployable.getType() != DeployableObject.TYPE_SIMPLE_SOA_APP)) {
            prevState = deployable.getState();
        if(!(prevState == DeployableObject.STATE_INSTALLED ||
                prevState == DeployableObject.STATE_STOPPING)){
            throw new IllegalStateException("Object cannot be started from State :" + deployable.getStatusString());
        }
        }
        try{
            deployable.start();
            if(this.dataAccessObject != null){
                this.dataAccessObject.persist(deployable);
            }
            success = true;
        }catch(Exception e){
        	if(logger.isDebugEnabled()) {
        		logger.debug("start()", e);
        	}
            throw new StartupFailedException(e);
        }finally{
            if(prevState != -1)
            this.notifyListeners(deployable, prevState, deployable.getState(), success);
        }
    }

    public DeployableObject stop(String id, boolean immediate) throws ShutdownFailedException {
        this.checkState();

        if (logger.isInfoEnabled()) {
            logger.info("Going to stop the application" + id);
        }

        AbstractDeployableObject deployable = (AbstractDeployableObject)this.findById(id);
        if (deployable == null) {
            throw new ShutdownFailedException(_strings.getString("AseHost.stopFailed", id));
        }

        this.stop(deployable, immediate);

        if (logger.isInfoEnabled()) {
            logger.info("Application was stoped successfully.");
        }
        return deployable;
    }

    public synchronized void stop(AbstractDeployableObject deployable, boolean immediate) throws ShutdownFailedException {
        boolean success = false;
        short prevState = deployable.getState();
        if(!(prevState == DeployableObject.STATE_READY ||
                prevState == DeployableObject.STATE_STOPPING || prevState == DeployableObject.STATE_ERROR)){
            throw new IllegalStateException("Object cannot be stoped from State :" + deployable.getStatusString());
        }
        try{
            deployable.stop(immediate);
            if(this.dataAccessObject != null){
                this.dataAccessObject.persist(deployable);
            }
            success = true;
        }catch(Exception e){
        	if(logger.isDebugEnabled()) {
        		logger.debug("stop()", e);
        	}
            throw new ShutdownFailedException(e);
        }finally{
            this.notifyListeners(deployable, prevState, deployable.getState(), success);
        }
    }

    public DeployableObject activate(String id) throws ActivationFailedException {
    	this.checkState();

    	if (logger.isInfoEnabled()) {
    		logger.info("Going to activate the application " + id);
    	}
    	AbstractDeployableObject deployable = (AbstractDeployableObject)this.findById(id);
    	if (deployable == null) {
    		throw new ActivationFailedException(_strings.getString("AseHost.startFailed", id));
    	}

    	this.activate(deployable);

    	logger.error("Application ["+id+"] was activated successfully.");

    	return deployable;
    }




    public synchronized void activate(AbstractDeployableObject deployable) throws ActivationFailedException {
    	
    	
    	/* *************************
    	 *  Called for the case when the application has defined its 
    	 *  own measurement counters. 
    	 *  The counters are getting initialized and stored in the MAP
    	 *  with their status set to active. 
    	 */
    	
    	String path = AseMeasurementManager.instance().getMsrConfigPath(deployable.getId(),AseMeasurementManager.STATUS_INACTIVE);
		if( path != null ) {
			AseMeasurementManager.instance().setAppStatusActive(deployable.getId());
			//bug 8939,arguement added in initAppMeasurementCounters
			AseMeasurementManager.instance().initAppMeasurementCounters(deployable.getId(), path,deployable.getClassLoader());
		}
		
		/* ***************************/
		
		
        boolean success = false;
        short prevState = deployable.getState();

        if(!(prevState == DeployableObject.STATE_READY)){
            throw new IllegalStateException("Object cannot be activated from State :" + deployable.getStatusString());
        }

        if(deployable instanceof AseContext){
            AseSipConnector connector = (AseSipConnector)Registry.lookup("SIP.Connector");
            ((AseContext)deployable).setAttribute(Constants.PROP_IP_ADDRESS , connector.getIPAddress() ) ;
            ((AseContext)deployable).setAttribute(Constants.PROP_OUTBOUND_INTERFACES , connector.getOutboundInterfaces() ) ; //JSR289.34       
            ((AseContext)deployable).setAttribute(Constants.PROP_ROLE , connector.getRole() ) ;
			Iterator itr = ((AseContext)deployable).getResourceNames();
			if(itr.hasNext()){
				DeployerFactory factory = (DeployerFactory)Registry.lookup(DeployerFactory.class.getName());
				Deployer deployer = factory.getDeployer(DeployableObject.TYPE_RESOURCE);
				String resourceName;
				Iterator it;
				ResourceContext resourceCtx; 
				while(itr.hasNext()){
					resourceName = (String)itr.next();	
					it = deployer.findByName(resourceName);
					resourceCtx = it.hasNext() ? (ResourceContext)it.next() : null;
					if(resourceCtx!=null){
						if(logger.isDebugEnabled()){
							logger.debug("registering app with the resource ");
						}
						((ResourceContextImpl)resourceCtx).registerApp((AseContext)deployable);
					}else{
						logger.error("No resource context found for resource "+resourceName);
					}
				} //while ends
        	}			

			//BUG-6765 [LIVE SBB UPGRADE]
			if(((AseContext) deployable).getUsesSBB() == true) {
				DeployerFactory factory = (DeployerFactory)Registry.lookup(DeployerFactory.class.getName());
				SbbDeployer sbbdeployer = (SbbDeployer) factory.getDeployer(DeployableObject.TYPE_SBB);
				
				sbbdeployer.registerApp((AseContext)deployable);	
			}
		}

        try{
            
        	deployable.activate();

            if (! Constants.NAME_SOAP_SERVER_AXIS.equals(deployable.getObjectName())) {
                if ((deployable.getType() == DeployableObject.TYPE_SERVLET_APP) ||
                    (deployable.getType() == DeployableObject.TYPE_SOA_SERVLET) ||
                    (deployable.getType() == DeployableObject.TYPE_SYSAPP && !((AseContext)deployable).isSysUtil())) {
                    // Notify app-router if available
                   
                    SipApplicationRouter sysAr = AseSipApplicationRouterManager.getSysAppRouter();
                    if (sysAr != null) {
                        List<String> list = new ArrayList<String>();
                        list.add(deployable.getObjectName());
                        sysAr.applicationDeployed(list);
                    }
                }
            }

            if(this.dataAccessObject != null){
                this.dataAccessObject.persist(deployable);
            }

            success = true;
        }catch(Exception e){
        	if(logger.isDebugEnabled()) {
        		logger.debug("activate()", e);
        	}
            throw new ActivationFailedException(e);
        }finally{
            this.notifyListeners(deployable, prevState, deployable.getState(), success);
        }
    }

    public DeployableObject deactivate(String id) throws DeactivationFailedException {
    	this.checkState();

    	if (logger.isInfoEnabled()) {
    		logger.info("Going to deactivate the application " + id);
    	}

    	AbstractDeployableObject deployable = (AbstractDeployableObject)this.findById(id);
    	if (deployable == null) {
    		throw new DeactivationFailedException(_strings.getString("AseHost.stopFailed", id));
    	}

    	this.deactivate(deployable);

    	if (! Constants.NAME_SOAP_SERVER_AXIS.equals(deployable.getObjectName())) {
    		if ((deployable.getType() == DeployableObject.TYPE_SERVLET_APP) ||
    				(deployable.getType() == DeployableObject.TYPE_SOA_SERVLET) ||
    				(deployable.getType() == DeployableObject.TYPE_SYSAPP)) {
    			// Notify app-router if available
    			SipApplicationRouter ar = AseSipApplicationRouterManager.getSysAppRouter();
    			if (ar != null) {
    				List<String> list = new ArrayList<String>();
    				list.add(deployable.getObjectName());
    				ar.applicationUndeployed(list);
    			}
    		}
    	}

    	logger.error("Application ["+id+"] was deactivated successfully.");

    	return deployable;

    }

    public synchronized void deactivate(AbstractDeployableObject deployable) throws DeactivationFailedException {
    	
    	boolean success = false;
        short prevState = deployable.getState();
        if(!(prevState == DeployableObject.STATE_ACTIVE || prevState == DeployableObject.STATE_UPGRADE)){
            throw new IllegalStateException("Object cannot be deactivated from State :" + deployable.getStatusString());
        }
        try{
            deployable.deactivate();
            if(this.dataAccessObject != null){
                this.dataAccessObject.persist(deployable);
            }
            success = true;
        }catch(Exception e){
        	if(logger.isDebugEnabled()) {
        		logger.debug("deactivate()", e);
        	}
            throw new DeactivationFailedException(e);
        }finally{
            this.notifyListeners(deployable, prevState, deployable.getState(), success);
        }
        
        /* *************************
    	 *  Called for the case when the application has defined its 
    	 *  own measurement counters. 
    	 *  The counters are being removed from the Counter Map when the
    	 *  application is stopped but the information about the service 
    	 *  is still maintained in a list with the status set to inactive. 
    	 */
    	String path = AseMeasurementManager.instance().getMsrConfigPath(deployable.getId(), AseMeasurementManager.STATUS_ACTIVE);
		if( path != null ) {
			AseMeasurementManager.instance().setAppStatusInactive(deployable.getId());
			AseMeasurementManager.instance().removeAppMeasCounters(deployable.getId());
		}
		
		/* ************************/
    }

    public DeployableObject findById(String id) {
        if(id == null)
            return null;

        DeployableObject deployable = null;
        if(this.started){
            if(this.host != null ) {
                deployable = (DeployableObject)this.host.findChild(id);
            }
            if(deployable == null) {
                SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
                if(soaFw != null) {
                    SoaContext soaContext = (SoaContext)soaFw.getSoaContext(this.getName(id));
                    deployable = (AbstractDeployableObject)soaContext.getDeployableObject();
                }
            }
        }else{
            for(int i=0; i<this.deployables.size();i++){
                DeployableObject temp = (DeployableObject) this.deployables.get(i);
                if(temp.getId().equals(id)){
                    deployable = temp;
                    break;
                }
            }
        }

        return deployable;
    }
    
    public DeployableObject findByNameAndVersion(String name, String version) {
        Iterator it = this.find(this.getType(), name, version);
        return (it != null && it.hasNext()) ? (DeployableObject) it.next() : null;
    }

    public Iterator findByName(String name) {
        return this.find(this.getType(), name, null);
    }

    public Iterator findAll() {
        return this.find(this.getType(), null, null);
    }

    public void registerStateChangeListener(String id,
            DeploymentListener listener) {
        ArrayList list = (ArrayList)listeners.get(id);
        list = (list == null) ? new ArrayList() : list;
        if(list.indexOf(listener) == -1){
            list.add(listener);
        }
        this.listeners.put(id, list);
    }

    public void unregisterStateChangeListener(String id,
            DeploymentListener listener) {
        ArrayList list = (ArrayList)listeners.get(id);
        if(list != null && list.indexOf(listener) != -1){
            list.remove(listener);
        }
    }

    public void checkExpectedState(String id, boolean force) throws Exception{

        if(logger.isDebugEnabled()){
            logger.debug("Check Expected State called for: " + id);
        }

        AbstractDeployableObject deployable = (AbstractDeployableObject)this.findById(id);
        short expectedState =  deployable.getExpectedState();

        if(logger.isDebugEnabled()) {
            logger.debug("Expected state: " + expectedState +
                        "; Current state = " +  deployable.getState());
        }

        switch(expectedState){
            case DeployableObject.STATE_INSTALLED:
                if(deployable.getState() == DeployableObject.STATE_UNINSTALLED){
                    this.deploy(deployable, null, false, deployable.getDeployedBy());
                }
                if(deployable.getState() == DeployableObject.STATE_ACTIVE){
                    this.deactivate(deployable);
                }
                if(deployable.getState() == DeployableObject.STATE_READY){
                    this.stop(deployable, force);
                }
                break;
            case DeployableObject.STATE_READY:
                if(deployable.getState() == DeployableObject.STATE_UNINSTALLED){
                    this.deploy(deployable, null, false, deployable.getDeployedBy());
                }
                if(deployable.getState() == DeployableObject.STATE_INSTALLED ||
                    deployable.getState() == DeployableObject.STATE_STOPPING){
                    this.start(deployable);
                }
                if(deployable.getState() == DeployableObject.STATE_ACTIVE){
                    this.deactivate(deployable);
                }
                break;
            case DeployableObject.STATE_ACTIVE:
                if(deployable.getState() == DeployableObject.STATE_UNINSTALLED){
                    this.deploy(deployable, null, true, deployable.getDeployedBy());
                }
                if(deployable.getState() == DeployableObject.STATE_INSTALLED ||
                    deployable.getState() == DeployableObject.STATE_STOPPING){
                    this.start(deployable);
                }
                if(deployable.getState() == DeployableObject.STATE_READY){
                    this.activate(deployable);
                }
                break;
            case DeployableObject.STATE_UNINSTALLED:
                if(deployable.getState() == DeployableObject.STATE_ACTIVE ||
                    deployable.getState() == DeployableObject.STATE_UPGRADE){
                    this.deactivate(deployable);
                }
                if(deployable.getState() == DeployableObject.STATE_READY){
                    this.stop(deployable, force);
                }
                if(deployable.getState() == DeployableObject.STATE_INSTALLED){
                    this.undeploy(deployable);
                }
                break;
            case DeployableObject.STATE_UPGRADE:
                if(deployable.getState() == DeployableObject.STATE_ACTIVE){
                    this.deactivate(deployable);
                }
                if(deployable.getState() == DeployableObject.STATE_READY){
                    this.stop(deployable, force);
                }
                if(deployable.getState() == DeployableObject.STATE_INSTALLED){
                    this.undeploy(deployable);
                }
                break;
        }
    }

    public void setExpectedState(String id, short state) {
        AbstractDeployableObject deployable = (AbstractDeployableObject)this.findById(id);
        if(deployable !=  null){
            deployable.setExpectedState(state);
        }
    }

    public void notifyListeners(DeployableObject deployable, short prevState, short requestedState, boolean completed){
        if(deployable == null || deployable.getId() == null)
            return;
        
        if(logger.isDebugEnabled()){
    		logger.debug("Enter in notifyListner . Is operation completed :: "+ completed);
    	}
        
        String alarmMessage =null;
        String deploymentname =deployable.getDeploymentName();
        
        if(deployable.getType()==DeployableObject.TYPE_RESOURCE){
        	if(logger.isDebugEnabled()){
        		logger.debug("Enter in notifyListner for assigning ID for resources. resource name is :: "+ deploymentname);
        	}
        	
        	if(deployable instanceof AbstractDeployableObject){
        		
        		if(deploymentname.equalsIgnoreCase(Constants.HTTP_RA_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.HTTP_RA_ID+PLUS+"For Service ::"+Constants.HTTP_RA_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.HTTP_IF_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.HTTP_IF_ID+PLUS+"For Service ::"+Constants.HTTP_IF_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.HTTP_FULL_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.HTTP_FULL_ID+PLUS+"For Service ::"+Constants.HTTP_FULL_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.RO_RA_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.RO_RA_ID+PLUS+"For Service ::"+Constants.RO_RA_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.RO_IF_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.RO_IF_ID+PLUS+"For Service ::"+Constants.RO_IF_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.RO_FULL_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.RO_FULL_ID+PLUS+"For Service ::"+Constants.RO_FULL_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.RF_RA_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.RF_RA_ID+PLUS+"For Service ::"+Constants.RF_RA_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.RF_IF_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.RF_IF_ID+PLUS+"For Service ::"+Constants.RF_IF_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.RF_FULL_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.RF_FULL_ID+PLUS+"For Service ::"+Constants.RF_FULL_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.SH_RA_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.SH_RA_ID+PLUS+"For Service ::"+Constants.SH_RA_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.SH_IF_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.SH_IF_ID+PLUS+"For Service ::"+Constants.SH_IF_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.SH_FULL_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.SH_FULL_ID+PLUS+"For Service ::"+Constants.SH_FULL_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.TELNETSSH_RA_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.TELNETSSH_RA_ID+PLUS+"For Service ::"+Constants.TELNETSSH_RA_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.TELNETSSH_IF_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.TELNETSSH_IF_ID+PLUS+"For Service ::"+Constants.TELNETSSH_IF_NAME;
        		}
        		else if(deploymentname.equalsIgnoreCase(Constants.TELNETSSH_FULL_NAME)){
        			alarmMessage =TROUBLE_SUB_SYS+Constants.TELNETSSH_FULL_ID+PLUS+"For Service ::"+Constants.TELNETSSH_FULL_NAME;
        		}
        	}

        }
        else{
        	if(deploymentname.equalsIgnoreCase(Constants.ATT_BLIZZARD_NAME)){
    			alarmMessage =TROUBLE_SUB_SYS+Constants.ATT_BLIZZARD_ID+PLUS+"For Service ::"+Constants.ATT_BLIZZARD;
    		}
        	else {
        		alarmMessage=TROUBLE_SUB_SYS+deployable.getId()+PLUS;
        	}
        }
        
        if(!completed){
        	alarmMessage=null;
        	if(prevState==DeployableObject.STATE_UNINSTALLED){
        		alarmMessage=_strings.getString("AseHost.deployFailedAlarm", deployable.getDeploymentName());
        		this.reportAlarm(Constants.ALARM_SERVICE_OPERATION_FAILED,alarmMessage);
        	}
        	else if(prevState==DeployableObject.STATE_INSTALLED && requestedState==DeployableObject.STATE_READY){
        		alarmMessage=_strings.getString("AseHost.startupFailedAlarm", deployable.getDeploymentName());
        		this.reportAlarm(Constants.ALARM_SERVICE_OPERATION_FAILED,alarmMessage);
        	}
        	else if(prevState==DeployableObject.STATE_READY && requestedState==DeployableObject.STATE_ERROR){
        		alarmMessage=_strings.getString("AseHost.activationFailedAlarm", deployable.getDeploymentName());
        		this.reportAlarm(Constants.ALARM_SERVICE_OPERATION_FAILED,alarmMessage);
        	}
        }else if((completed && ((deployable.getType()==DeployableObject.TYPE_SERVLET_APP)||deployable.getType()==DeployableObject.TYPE_RESOURCE))){
        	
        	if(logger.isDebugEnabled()){
            	logger.debug("Previous state of Service + " + deployable.getId() + " is  ::  " + prevState +" and  requestedState of Service is ::  " +requestedState);
            }
        	
        	if(prevState==DeployableObject.STATE_UNINSTALLED && requestedState==DeployableObject.STATE_INSTALLED){
        		this.reportAlarm(Constants.ALARM_SERVICE_DEPLOY, alarmMessage,completed);
        	}
        	else if(prevState==DeployableObject.STATE_INSTALLED && requestedState==DeployableObject.STATE_READY){ 
        		this.reportAlarm(Constants.ALARM_SERVICE_START, alarmMessage,completed);
        	}
        	else if(prevState==DeployableObject.STATE_READY && requestedState==DeployableObject.STATE_ACTIVE){
        		this.reportAlarm(Constants.ALARM_SERVICE_ACTIVE, alarmMessage,completed);
        	}
        	else if(prevState==DeployableObject.STATE_READY && requestedState==DeployableObject.STATE_INSTALLED){
        		this.reportAlarm(Constants.ALARM_SERVICE_STOP, alarmMessage,completed);
        	}
        	else if(prevState==DeployableObject.STATE_ACTIVE && requestedState==DeployableObject.STATE_READY){
        		this.reportAlarm(Constants.ALARM_SERVICE_DEACTIVE, alarmMessage,completed);
        	}
        	else if(prevState==DeployableObject.STATE_INSTALLED && requestedState==DeployableObject.STATE_UNINSTALLED){
        		this.reportAlarm(Constants.ALARM_SERVICE_UNDEPLOY, alarmMessage,completed);
        	}
        }
        
        ArrayList list = (ArrayList)listeners.get(deployable.getId());
        if(list == null)
            return;
        for(int i=0;i<list.size();i++){
            DeploymentListener listener = (DeploymentListener) list.get(i);
            if(listener == null)
                continue;
            if(completed){
                listener.stateChangeCompleted(deployable, prevState, requestedState);
            }else{
                listener.stateChangeFailed(deployable, prevState, requestedState);
            }
        }
    }

    public DDHandlerFactory getDDHandlerFactory(){
        return null;
    }

    public void processDescriptor(DeploymentDescriptor dd, AbstractDeployableObject deployable) throws Exception{
        if(dd == null || deployable == null)
            return;

        if(logger.isDebugEnabled()){
            logger.debug("Going to process descriptor type :" + dd.getTypeString());
        }

        DDHandler handler = this.ddHandlerFactory.getDDHandler(dd.getType());
        if(handler != null){
            handler.setDeployer(this);
            handler.parse(dd, deployable);
        }
    }

    public ArrayList getDDs(AbstractDeployableObject deployable, String[] names) throws DeploymentFailedException{
        ArrayList list = new ArrayList();
        boolean cas = false;
        for(int i=0; names != null && i <names.length;i++){
            if(names[i] == null)
                continue;
            logger.debug("Class DeployerImpl in getDDs" + names[i]);
            DeploymentDescriptor dd = this.getDD(deployable, names[i]);
            if(dd != null) {
            	
            	if(dd.getType() == 6){
            		logger.debug("Class DeployerImpl : " + dd.getType());
            		cas = true;
            	}

            	if(cas){
            		logger.debug("Class DeployerImpl inside cas if : " + dd.getType());
            		if(dd.getType() == 3){
            			logger.debug("Class DeployerImpl before continueee : " + dd.getType());
            			continue;
            		}
            	}
            	logger.debug("Class DeployerImpl before add " + dd.getType());
                list.add(dd);
            }
        }
        logger.debug("Class DeployerImpl getDDs list " + list);
        return list;
    }

    private DeploymentDescriptor getDD(AbstractDeployableObject deployable, String name) throws DeploymentFailedException{

        DeploymentDescriptor dd = null;
        InputStream ddStream = null;
        try{
            if(logger.isDebugEnabled()) {
                logger.debug("Unpacked dir = " + deployable.getUnpackedDir() + " name = " + name);
            }
            File descriptor = new File(deployable.getUnpackedDir(), name);
            if(descriptor.exists()){
                ddStream = new FileInputStream(descriptor);
            } else {
                if(logger.isDebugEnabled()) {
                    logger.debug("Descriptor "+name+" does not exists");
                }
            }

            if(ddStream != null){
                if(logger.isDebugEnabled()) {
                    logger.debug("Creating new instance of DeploymentDescriptor: Name = "+name);
                }
                dd = new DeploymentDescriptor();
                dd.setType(name);
                dd.setStream(ddStream);
            }
        }catch(Exception e){
        	if(logger.isDebugEnabled()) {
        		logger.debug("getDD()", e);
        	}
            throw new DeploymentFailedException(e.getMessage(), e);
        }

        return dd;
   }

    protected void parseDeployableObject(AbstractDeployableObject deployable) throws Exception{
        String[] ddNames = this.getDDNames();
        Iterator it = this.getDDs(deployable, ddNames).iterator();
        for(;it != null && it.hasNext(); ){
            DeploymentDescriptor dd = (DeploymentDescriptor)it.next();
            this.processDescriptor(dd, deployable);
        }
    }

    protected void storeArchive(AbstractDeployableObject deployable, InputStream stream) throws Exception {

        if(stream == null){
            if(logger.isDebugEnabled()) {
                logger.debug("Inside storeArchive(): stream is NULL: returning");
            }
            return;
        }

        //Create an archive directory if it does not exist.
        File archiveDir = new File(this.getDeployDirectory(), ARCHIVE_DIR_NAME);
        if (!archiveDir.exists()) {
            if(logger.isDebugEnabled()) {
                logger.debug("Creating Archives directory");
            }
            archiveDir.mkdirs();
        }

        //Get the archive file name.
        String fileName = deployable.getId();
        if(this.isAppDeployer() || this.isSoaDeployer()) {
            fileName += ".sar";
        } else {
            fileName += ".jar";
        }
        //Copy the archive file into the "archives" sub-directory
        File archive = new File(archiveDir, fileName);
        OutputStream os = new BufferedOutputStream(new FileOutputStream(archive));

        byte[] bytes = new byte[1000];
        int bytesRead = stream.read(bytes, 0, bytes.length);

        while (bytesRead > 0) {
            os.write(bytes, 0, bytesRead);
            bytesRead = stream.read(bytes, 0, bytes.length);
        }
        os.close();

        //Set the Archive file URL into the application.
        deployable.setArchive(archive.toURL());
    }

    protected void setClassLoader(AbstractDeployableObject deployable, InputStream stream) throws Exception {

        //Set the deployable's deploy time.
        deployable.setDeployTime(System.currentTimeMillis());
        
        //Create a temp directory if it does not exist.
        File unpackedDir = null;
        if(deployable.getType() == DeployableObject.TYPE_SOAP_SERVER) {
            unpackedDir = new File(Constants.ASE_HOME,"soapserver/"+Constants.NAME_SOAP_SERVER_AXIS);
        } else {
            unpackedDir = new File(Constants.ASE_HOME, "tmp/unpacked_" + deployable.getDeployTime());
        }
        if (!unpackedDir.exists()) {
            unpackedDir.mkdirs();
        }

        InputStream tempStream = (stream != null) ? stream : deployable.getArchive().openStream();
        ZipInputStream zipStream = new ZipInputStream(tempStream);
        boolean extracted = FileUtils.extract(zipStream, unpackedDir);

        //Add the ROOT directory to the Class Loader's ClassPath
        AppClassLoader classLoader = null;
        if(logger.isDebugEnabled()) {
        	logger.debug("Getting class loader.");
        }
        WebContainer webContainer = (WebContainer)Registry.lookup(Constants.NAME_WEB_CONTAINER);
        if ((webContainer!=null) && (webContainer.getState() == WebContainerState.RUNNING)) {
        	if(logger.isDebugEnabled()) {
        		logger.debug("Create AppClassLoader as child of Web container class loader.");
        	}
            classLoader = new AppClassLoader(webContainer.getClassLoader());
        }else {
        	if(logger.isDebugEnabled()) {
        		logger.debug("Create AppClassLoader as child of Ase container class loader.");
        	}
            classLoader = new AppClassLoader(deployable.getClass().getClassLoader());
        }
        
        //SANEJA@BUG 7056 Getting ASELoader for adding replication classes[
        AseClassLoader aseLoader=null;
        ClassLoader hostLoader=null;
        if(this.getType() == DeployableObject.TYPE_RESOURCE){
        	//|| this.getType() == DeployableObject.TYPE_SBB
        	hostLoader=this.host.getClass().getClassLoader();
        	if(hostLoader instanceof AseClassLoader)
        		aseLoader=(AseClassLoader)hostLoader;
        }
        if(logger.isDebugEnabled())
    		logger.debug("Identified ASE/host loader is:::"+aseLoader);
        //] closed SANEJA@BUG 7056
                
        if(logger.isDebugEnabled()){
        		logger.debug("Identified deployment loader is:::"+classLoader);
        }
         
		File appJarsUrl = new File(Constants.ASE_HOME, "appjars/"
				+ deployable.getDeploymentName());

		if (logger.isDebugEnabled())
			logger.debug("Load application specific jars:::"
					+ appJarsUrl.getPath() + " if This path exists  "
					+ appJarsUrl.exists() + " For deployable : "+deployable.getDeploymentName());

		if (appJarsUrl.exists()) {
			
			File[] appjars = appJarsUrl.listFiles();
			for (File appjar : appjars) {
				
				 if(logger.isDebugEnabled()){
		        		logger.debug("Adding app jars URL to ClassLoader:::"+appjar.toURL());
		                 }
				     if (appjar.getName().endsWith(".jar")){	
                                       classLoader.addRepository(appjar.toURL());
				     }	
			}
		}
        
        URL url = unpackedDir.toURL();
        if(logger.isDebugEnabled()){
            logger.debug("Adding URL to ClassLoader :"  + url );
        }
        classLoader.addRepository(url);
        //SANEJA@BUG 7056 Adding base URL of unpacked jar to classpath for RA [
        if(aseLoader!=null)
        	aseLoader.addRepository(url);
        //]closed SANEJA@BUG 7056

        //Add the Classes directory to the Class Loader's Class Path
        String classesDir = "classes";
        if(this.isAppDeployer() || this.isSoaDeployer()) {
            classesDir = "WEB-INF/" + classesDir;
        }
        url = new File(unpackedDir, classesDir).toURL();
        if(logger.isDebugEnabled()){
            logger.debug("Adding URL to ClassLoader :"  + url );
        }

        //Add all the jar files in the Lib directory to the Class Loader's Class Path
        String libDir = "lib";
        if(this.isAppDeployer() || this.isSoaDeployer()) {
            libDir = "WEB-INF/" + libDir;
        }

        File lib = new File(unpackedDir, libDir);

        // Adding the SOAP Server Jars in Parent CL to make them accessible for all the apps.
        if(deployable.getType() == DeployableObject.TYPE_SOAP_SERVER) {
            ClassLoader sasCL = deployable.getClass().getClassLoader();
            this.addInAxisCL(sasCL,url,lib);
            if ((webContainer!=null) && (webContainer.getState() == WebContainerState.RUNNING)) {
            	if(logger.isDebugEnabled()) {
            		logger.debug("Get Web container class loader.");
            	}
                ClassLoader webCL = webContainer.getClassLoader();
                this.addInAxisCL(webCL,url,lib);
            }
            //PG: Change
            host.setAseCL(sasCL);

        } else {
            if(logger.isDebugEnabled()){
                logger.debug("Adding the Jars and Classes in the ClassLoader");
            }
            classLoader.addRepository(url);
            //SANEJA@BUG 7056 7056 Adding classes folder of unpacked jar in ase loader--done for replication in RA[
            if(aseLoader!=null)
            	aseLoader.addRepository(url);
            //]closed SANEJA@BUG 7056
            
            //bug 8939,adding path till "WEB-INF", so that MeasurementConfig.xml can be loaded 
            String WebInfDir = "WEB-INF";
            if (isAppDeployer()) {
              url = new File(unpackedDir, WebInfDir).toURL();
              classLoader.addRepository(url);
              if (logger.isDebugEnabled()) {
                logger.debug("Adding Web-Inf URL to ClassLoader :" + url);
              }
            }
            
        if (lib.exists()) {
            File[] jars = lib.listFiles();

            for (int i = 0; i < jars.length; i++) {
                if(logger.isDebugEnabled()){
                    logger.debug("Adding URL to ClassLoader :"  + jars[i].toURL());
                }
                classLoader.addRepository(jars[i].toURL());
              //SANEJA@BUG 7056 7056 Adding libs of unpacked jar--done for resolving depndencies at replication in RA[
                if(aseLoader!=null)
                	aseLoader.addRepository(jars[i].toURL());
              //]closed SANEJA@BUG 7056
            }
        }
        }
        deployable.setUnpackedDir(unpackedDir);
        deployable.setClassLoader(classLoader);

        //Close the Stream if this Stream is opened from the archive file.
        if(stream == null){
            zipStream.close();
        }

    }

    private void addInAxisCL(ClassLoader cl, URL url, File lib) {
        if(logger.isDebugEnabled()){
            logger.debug("Adding the Jars and Classes in the Parent ClassLoader CL="+cl);
        }
        try {
            if(cl instanceof AseClassLoader) {
                AseClassLoader aseClassLoader = (AseClassLoader)cl;
                aseClassLoader.addRepository(url);
                if (lib.exists()) {
                    File[] jars = lib.listFiles();
                    for (int i = 0; i < jars.length; i++) {
                        if(logger.isDebugEnabled()){
                            logger.debug("Adding URL to ClassLoader :"  + jars[i].toURL());
                        }
                        aseClassLoader.addRepository(jars[i].toURL());
                    }
                }
            } else {
                logger.error("SOAP Server Jars cant be added in the Class Loaders CLASSPATH");
                logger.error("Parent in not instance of AseClassLoader");
            }
        }catch(Exception e) {
        	if(logger.isDebugEnabled()) {
        		logger.debug(e.getMessage(), e);
        	}
        }
    }

    public Iterator find(short type, String name, String version){
        if (logger.isDebugEnabled()) {
            logger.debug("Inside find method envoking getDeployables : type = " +type + "; name = " + name +"; version = " + version);
        }

        ArrayList list = new ArrayList();
        List children = this.getDeployables();
        for(int i=0; children != null && i<children.size();i++){
            DeployableObject ctx = (DeployableObject) children.get(i);
            if (ctx.getState() != SasApplication.STATE_UNINSTALLED) {
                if ( (type < 0)
                || (ctx.getType() == type)
                || ((ctx.getType() == DeployableObject.TYPE_PURE_SOA) && (type == DeployableObject.TYPE_SOA_SERVLET))
                || ((ctx.getType() == DeployableObject.TYPE_SIMPLE_SOA_APP) && (type == DeployableObject.TYPE_SOA_SERVLET))
                ) {
                    if ((name == null || name.equals(ctx.getDeploymentName())) &&
                    (version == null || version.equals(ctx.getVersion()))) {
                list.add(ctx);
                        if (logger.isDebugEnabled()) {
                            logger.debug("DeployableObject being added in the list is "+ ctx);
                        }
                    }
                }
            }
        } //for

        return list.iterator();
    }
    
    public List getAppNames() {
        ArrayList list = new ArrayList();
        List children = this.getDeployables();
        if(logger.isDebugEnabled()) {
        	logger.debug("inside getAppNames: list from getDeployables is = "+children.toString());
        }

        for(int i = 0; children != null && i < children.size(); i++){
            DeployableObject ctx = (DeployableObject) children.get(i);
            if(ctx.getState() != SasApplication.STATE_UNINSTALLED &&
                ((ctx.getType() == DeployableObject.TYPE_SAS_APPLICATION) ||
                (ctx.getType() == DeployableObject.TYPE_SERVLET_APP) ||
                (ctx.getType() == DeployableObject.TYPE_SOA_SERVLET) ||
                (ctx.getType() == DeployableObject.TYPE_SYSAPP))) {
                list.add(ctx.getObjectName());
            }
        }
        
        if(logger.isDebugEnabled()) {
	        logger.debug("inside getAppNames: list returned is = "+list.toString());
	        logger.debug("empty check: "+ list.isEmpty());
        }
        return list;
    }

    private List getDeployables(){
        if(!this.started) {
            return this.deployables;
        }

        AseContainer[] children = this.host.findChildren();
        List depList = new ArrayList();
        if(children != null) {
            for(int i=0; i<children.length  ; i++) {
                depList.add(children[i]);
            }
        }
        SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
        if(soaFw.isSoaSupportEnabled()) {
            Iterator it = soaFw.listSoaContexts();
            while( (it != null) && (it.hasNext()) ) {
                Map.Entry entry = (Map.Entry)it.next();
                SoaContext soaContext = (SoaContextImpl)entry.getValue();
                AbstractDeployableObject abso = (AbstractDeployableObject)soaContext.getDeployableObject();
                if((abso != null) &&(!(abso instanceof AseContext))) {
                    depList.add(abso);
                    if (logger.isDebugEnabled()) {
                        logger.debug("AbstractDeployableObject " + abso +" for pure SOA service is added");
                    }
                }
            }
        }

        return depList;
    }

    public AbstractDeployableObject preDeploy(AbstractDeployableObject deployable,
                                    String name, String version, int priority,
                                    byte[] binary)  throws DeploymentFailedException {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering preDeploy(5 args) with name = " + name);
        }

        try {
            deployable = (deployable == null) ? this.createDeployableObject() : deployable;
            deployable.setDeployer(this);
            if(name != null)	// deployable might be containing the DeploymentName already
            	deployable.setDeploymentName(name);
            deployable.setVersion(version);
            deployable.setPriority(priority);
            if( (deployable.getType() != DeployableObject.TYPE_PURE_SOA) && (deployable.getType() != DeployableObject.TYPE_SIMPLE_SOA_APP) ) {
                deployable.setType(this.getType());
            }
            
            this.setClassLoader(deployable, new ByteArrayInputStream(binary));
            this.parseDeployableObject(deployable);
            //JSR 289.42
	        if(deployable.getType() != DeployableObject.TYPE_SOAP_SERVER && 
	        		deployable.getType() != DeployableObject.TYPE_RESOURCE && 
	        		deployable.getType() != DeployableObject.TYPE_SBB) {
                boolean ea=false;
                if(deployable instanceof AseContext) {
                            ea=((AseContext)deployable).isEnableAnnotation();
                }
            	//call process annotation method if it is enabled
            	if(ea==true){
            		new AseAnnotationProcessor((AseContext)deployable).processAnnotation();
            	}
            	if (ea==false && !deployable
            			.isValidDescriptorAvailable()) {
            		throw new DeploymentFailedException(
            				"Application archive must contain a sip-app and/or web-app deployment descriptor " +
            		"file or annotations must be enabled in sas.xml file");
            	}
            }
            if(deployable.getObjectName() == null){
                deployable.setObjectName(deployable.getDeploymentName());
            }       
            if(deployable.getPriority() < 0){
                deployable.setPriority(DEFAULT_PRIORITY);
            }
            
            if(deployable.isXmlApp()){
            	if(logger.isDebugEnabled()){
            		logger.debug("Xml related Application deployement");
            	}
            	
            	processXmlFile(deployable, new ByteArrayInputStream(binary));
            }
        }catch(Exception e){
        	if(logger.isDebugEnabled()) {
        		logger.debug("In preDeploy()", e);
        	}
            throw new DeploymentFailedException(e);
        }
        return deployable;
    }

    protected byte[] getByteArray(InputStream stream) throws DeploymentFailedException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if(stream == null)
            return null;
        try {
            byte[] bytes = new byte[1024];
            for(;stream.available()>0;){
                int bytesRead = stream.read(bytes, 0, bytes.length);
                bos.write(bytes, 0, bytesRead);
            }
            stream.close();
        }catch(java.io.IOException exp) {
            logger.error("Reading bytes", exp);
            throw new DeploymentFailedException("Read error!");
        }

        return bos.toByteArray();
    }

    private boolean isAppDeployer(){
         return (this.getType() == DeployableObject.TYPE_SERVLET_APP ||
                this.getType() == DeployableObject.TYPE_SYSAPP ||
                this.getType() == DeployableObject.TYPE_SOAP_SERVER);
    }

    private boolean isSoaDeployer() {
        return (this.getType() == DeployableObject.TYPE_PURE_SOA ||
                this.getType() == DeployableObject.TYPE_SOA_SERVLET ||
                this.getType() == DeployableObject.TYPE_SIMPLE_SOA_APP);
    }

    protected void checkState(){
        if(!this.started){
            throw new IllegalStateException("The deployer is not properly initialized");
        }
    }

    public abstract AbstractDeployableObject createDeployableObject();

    public abstract String[] getDDNames();

    public abstract String getDAOClassName();
    public void validateUpgradation()
    {
        RulesRepository repository = (RulesRepository)Registry.lookup(Constants.RULES_REPOSITORY);
        try {
            int size = deployables.size();
            for(int i =0 ;i<size;i++) {
                AbstractDeployableObject deployable = (AbstractDeployableObject)deployables.get(i);
                Iterator it = this.findByName(deployable.getDeploymentName());
                if(it.hasNext()) {
                    DeployableObject object = (DeployableObject)it.next();
                }

            }
        } catch(Exception e) {
            logger.error("In validateUpgradation()", e);
        } finally {
            repository.unlock();
        }

    }

    protected boolean isSasXmlExist(byte[] binary) {
        boolean flag = false;
        try {
            InputStream stream = new ByteArrayInputStream(binary);
            JarInputStream jarStream = new JarInputStream(stream);
            while(jarStream.available() == 1) {
                JarEntry jarEntry = jarStream.getNextJarEntry();
                if(jarEntry != null && jarEntry.toString().contains(DeploymentDescriptor.STR_SAS_DD)) {
                    flag = true;
                }
            }
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return flag;
    }
    
    //cas
    protected boolean isCasXmlExist(byte[] binary) {
        boolean flag = false;
        try {
            InputStream stream = new ByteArrayInputStream(binary);
            JarInputStream jarStream = new JarInputStream(stream);
            while(jarStream.available() == 1) {
                JarEntry jarEntry = jarStream.getNextJarEntry();
                if(jarEntry != null && jarEntry.toString().contains(DeploymentDescriptor.STR_CAS_DD)) {
                    flag = true;
                }
            }
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return flag;
    }

    protected short findType(String name, byte[] binary) {
        short returnValue = 0;
        int type = 0;
        try {
            InputStream stream = new ByteArrayInputStream(binary);
            JarInputStream jarStream = new JarInputStream(stream);
            while(jarStream.available() == 1) {
                JarEntry jarEntry = jarStream.getNextJarEntry();
                if(jarEntry != null && jarEntry.toString().contains(DeploymentDescriptor.STR_SOA_DD)) {
                    type = type+1;
                } else if(jarEntry != null && jarEntry.toString().contains(DeploymentDescriptor.STR_SIP_DD)) {
                    type = type+2;
                } else if(jarEntry != null && jarEntry.toString().contains(DeploymentDescriptor.STR_WEB_DD)) {
                    type = type+4;
                }
            }
        } catch(Exception e) {
            logger.error(e.getMessage(),e);
        }
        switch(type) {
            case 1:
                returnValue = DeployableObject.TYPE_PURE_SOA;
                break;
            case 2:
            case 4:
            case 6:
                returnValue = DeployableObject.TYPE_SERVLET_APP;
                break;
            case 3:
            case 5:
            case 7:
                returnValue = DeployableObject.TYPE_SOA_SERVLET;
                break;
            default:
                logger.error("Unknown Type = "+type);
        }

        return returnValue;
    }

    protected short findType(String id) {
        DeployableObject deployable = null;
        if(this.started){
        	if(logger.isDebugEnabled()) {
        		logger.debug("findType...............Finding service with id..."+ id);
        	}
            if(this.host != null ) {
                deployable = (DeployableObject)this.host.findChild(id);
            }
            if(deployable == null) {
                SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
                if(soaFw != null) {
                    SoaContext soaContext = (SoaContext)soaFw.getSoaContext(this.getName(id));
                    deployable = (AbstractDeployableObject)soaContext.getDeployableObject();
                }
            }
        }else{
        	if(logger.isDebugEnabled()) {
        		logger.debug("findType...............Finding service with id..."+ id);
        	}
            for(int i=0; i<this.deployables.size();i++){
                DeployableObject temp = (DeployableObject) this.deployables.get(i);
                if(temp.getId().equals(id)){
                    deployable = temp;
                    break;
                }
            }
        }
        return deployable.getType();
    }

    protected String getName(String id) {
        String temp = id.substring(0,id.lastIndexOf(AseStrings.UNDERSCORE));
        String name = temp.substring(0, temp.lastIndexOf(AseStrings.UNDERSCORE));
        return name;
    }

    protected short findTypeByName(String name) {
        DeployableObject deployable = null;
        if(this.started){
            AseContainer[] children = this.host.findChildren();
            for(int i=0; i<children.length ; i++) {
                DeployableObject obj = (DeployableObject) children[i];
                if(obj.getDeploymentName().equals(name)){
                     deployable = obj;
                     break;
                }
            }

            if(deployable == null) {
                SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
                if(soaFw != null) {
                    SoaContext soaContext = (SoaContext)soaFw.getSoaContextByDepName(name);
                    if(soaContext != null) {
                        deployable = (AbstractDeployableObject)soaContext.getDeployableObject();
                    }
                }
            }

        }else{
            for(int i=0; i<this.deployables.size();i++){
                DeployableObject temp = (DeployableObject) this.deployables.get(i);
                if(temp.getDeploymentName().equals(name)){
                    deployable = temp;
                    break;
                }
            }
        }
        if(deployable != null)
            return deployable.getType();
        else
            return -1;
    }

    private void validation()
    {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering validation()");
        }

        try {
            int size = deployables.size();
            for(int i =0 ;i<size;i++) {
                AbstractDeployableObject deployable = (AbstractDeployableObject)deployables.get(i);
                Iterator it = this.findByName(deployable.getDeploymentName());
                if(it.hasNext()) {
                    DeployableObject object = (DeployableObject)it.next();
                    if(Double.parseDouble(deployable.getVersion())>(Double.parseDouble(object.getVersion()))) {
                        deployables.remove(object);
                    }
                    if(Double.parseDouble(deployable.getVersion())<(Double.parseDouble(object.getVersion()))) {
                        deployables.remove(deployable);
                    }
                }
            }
        } catch(Exception e) {
            logger.error("In validation()", e);
        }
    }
    
    private void reportAlarm(int alarmCode,String alarmMessage){
    	if(alarmMessage!=null){
    		if (logger.isDebugEnabled()) {
        		logger.debug("Reporting alarm for failed service operation to EMS/wEMS agent...");
        	}
    		try {
    			AseAlarmService service = (AseAlarmService)Registry.lookup(Constants.NAME_ALARM_SERVICE); 

    			// Sending alarm code with trouble subsystem id as dummy for wEMS
    			service.sendAlarm(alarmCode,12,alarmMessage);
    			
    			if (logger.isDebugEnabled()) {
    				logger.debug("Successfully reported alarm to EMS/wEMS.");
    			}
    		} catch (Exception e) {
    			String msg = "Error occurred while reporting failed service deployment alarm to EMS: " + e.getMessage();
    			logger.error(msg, e);
    		}
    	}

    } 
    private void reportAlarm(int alarmCode,String alarmMessage,boolean completed){
    	if(alarmMessage!=null){
    		if (logger.isDebugEnabled()) {
        		logger.debug("Reporting alarm for failed service operation to EMS agent...");
        	}
    		try {
    			AseAlarmService service = (AseAlarmService)Registry.lookup(Constants.NAME_ALARM_SERVICE); 

    			service.sendAlarm(alarmCode,alarmMessage);
    			
    			if (logger.isDebugEnabled()) {
    				logger.debug("Successfully reported alarm to EMS");
    			}
    		} catch (Exception e) {
    			String msg = "Error occurred while reporting failed service deployment alarm to EMS: " + e.getMessage();
    			logger.error(msg, e);
    		}
    	}

    } 
    
    private void processXmlFile(AbstractDeployableObject deployable, InputStream stream) throws Exception{
    	File xmlFilePath = new File(deployable.getUnpackedDir(), "WEB-INF/");
    	File[] xmlTarFiles = FileUtils.findFiles(xmlFilePath, Pattern.compile(".*.tar"), false);
    	
    	if(xmlTarFiles != null && xmlTarFiles.length > 0){
    		String deploymentName = deployable.getDeploymentName();
    		for(File xmlTarFile : xmlTarFiles){
    			if(logger.isDebugEnabled()){
            		logger.debug("XmlTarFile found for deployement : " + deploymentName + " is : " + xmlTarFile);
            	}
    			
            	File destDir = new File(Constants.ASE_HOME, "conf/xmlparser");
            	if(!destDir.exists()){
            		if(logger.isDebugEnabled()){
            			logger.debug("Destinition Directory : " + destDir.getName() + " dosn't exist so creating one");
            		}
            		destDir.mkdir();
            	}
            	
            	String tarFileName = xmlTarFile.getName();
            	int dot = tarFileName.lastIndexOf('.');
            	String serviceName = (dot == -1) ? tarFileName : tarFileName.substring(0, dot);
            	
            	File appNameDir = new File(Constants.ASE_HOME, "conf/xmlparser/" + serviceName);
            	if(!appNameDir.exists()){
            		if(logger.isDebugEnabled()){
            			logger.debug("AppName Directory : " + appNameDir.getName() + " doesn't exist so creating it.");
            		}
            		appNameDir.mkdir();
            	}
            	
            	File appVersionDir = new File(Constants.ASE_HOME, "conf/xmlparser/" + serviceName + "/" + deployable.getFullVersion());
            	if(!appVersionDir.exists()){
            		if(logger.isDebugEnabled()){
            			logger.debug("AppVersion Directory : " + appVersionDir.getName() + " doesn't exist so creating it.");
            		}
            		appVersionDir.mkdir();
            		
            		FileUtils.copy(xmlTarFile , appVersionDir);
                	
                	String unpackedDir = appVersionDir.getAbsolutePath() + File.separator + xmlTarFile.getName();
                	logger.debug("xmlTarFile name : " + xmlTarFile.getName());
                	File tarFile = new File(unpackedDir);
                	if(logger.isDebugEnabled()){
                		logger.debug("Going to extract the file : " + tarFile.getAbsolutePath() + " at destDir : " + appVersionDir.getAbsolutePath());
                	}
                	URI tarFileUri = tarFile.toURI();
                	TarArchiveInputStream tarStream = new TarArchiveInputStream(tarFileUri.toURL().openStream());
                	boolean extracted = FileUtils.decompress(tarStream, appVersionDir);
                	if(logger.isDebugEnabled()){
                		logger.debug("extracted : " + extracted);
                	}
                	if(extracted){
                		FileUtils.delete(tarFile);
                	}
            	}
            	
    		}
    	}else{
    		if(logger.isDebugEnabled()){
    			logger.debug(deployable.getDeploymentName() + ".tar not found");
    		}
    		throw new DeactivationFailedException();
    	}
    }
    
}
