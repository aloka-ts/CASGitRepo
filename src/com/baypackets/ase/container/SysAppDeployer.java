/*
 * SysAppDeployer.java
 *
 * Created on July 3, 2005, 6:43 PM
 */
package com.baypackets.ase.container;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.sip.ar.SipApplicationRouter;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.RoleChangeListener;
import com.baypackets.ase.deployer.SysAppInfo;
import com.baypackets.ase.servicemgmt.ComponentDeploymentStatus;
import com.baypackets.ase.spi.container.SasApplication;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.spi.util.CommandFailedException;
import com.baypackets.ase.spi.util.CommandHandler;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.TelnetServer;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.InitializationFailedException;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;

import com.baypackets.ase.router.AseSipApplicationRouterManager;

/**
 * This component manages the lifecycle of system applications.
 *
 * @see com.baypackets.ase.container.SysAppInfo
 * @author  BayPackets
 */
public class SysAppDeployer implements MComponent, RoleChangeListener, CommandHandler {

    private static String SHOW_SYS_APPS = "sys-app-info";
    private static String APP_ROUTER_RELOAD = "app-router-reload";
    private static Logger _logger = Logger.getLogger(SysAppDeployer.class);
    private static StringManager _strings = StringManager.getInstance(SysAppDeployer.class.getPackage());

    private Collection _sysApps;
    private Deployer _deployer;
    private ConfigRepository _configRepository;
    private boolean _started = false;
    private short _role = AseRoles.UNKNOWN;

    private ComponentDeploymentStatus deploymentStatus = null;

    /**
     * This method displays the info on all system applications to the
         * telnet console when the user enters the "sys-app-info" command.
     * For each app, the following information is displayed:
     *  <ul>
     *      <li> The URI of the application archive file.
     *      <li> The app's unique name, if it is deployed.
     *      <li> The current state of the app (i.e. NOT DEPLOYED,
     *      DEPLOYED BUT NOT RUNNING, RUNNING)
     *  </ul>
     */
    public String execute(String cmd, String[] args, InputStream in, OutputStream out) throws CommandFailedException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("execute() called: Displaying info on all system apps to the telnet console...");
        }

    if (cmd.equals(APP_ROUTER_RELOAD)) {
        appRouterReload();
        return "";
    }

        if (_sysApps == null || _sysApps.isEmpty()) {
            return _strings.getString("SysAppDeployer.noSystemApps");
        }

        StringBuffer buffer = new StringBuffer(_strings.getString("SysAppDeployer.systemAppList"));

        Iterator iterator = _sysApps.iterator();

        while (iterator.hasNext()) {
            SysAppInfo appInfo = (SysAppInfo)iterator.next();
            DeployableObject app = appInfo.getId() != null ? _deployer.findById(appInfo.getId()) : null;

            Object[] params = new Object[5];
            if (appInfo.getName() == null) {
        params[0] = "";
            } else {
        params[0] = appInfo.getName();
        }

            params[1] = appInfo.getArchive();

            params[4] = 0;
            if (app == null) {
                params[2] = "";
                params[3] = _strings.getString("SysAppDeployer.notDeployed");
            } else {
                AseContext ctx = (AseContext)app;
                params[2] = app.getObjectName();
                if (app.getState() == SasApplication.STATE_ACTIVE) {
                    params[3] = _strings.getString("SysAppDeployer.running");
                    params[4] = ctx.getAppSessionCount();
                } else {
                    params[3] = _strings.getString("SysAppDeployer.deployedNotRunning");
                }
            }

            buffer.append(_strings.getString("SysAppDeployer.appInfo", params));

        if (!iterator.hasNext()) {
        buffer.append(AseStrings.NEWLINE);
        }
        }

        return buffer.toString();
    }

    private void appRouterReload() {
        SipApplicationRouter sysAr = AseSipApplicationRouterManager.getSysAppRouter();
        if (sysAr != null) {
            List apps = _deployer.getAppNames();
            sysAr.applicationDeployed((List<String>) apps);
        }
    }


    /**
     * Returns the usage statement for the "sys-app-info" telnet command.
     */
    public String getUsage(String cmd) {
       if(cmd.equals(SHOW_SYS_APPS))
    		return _strings.getString("SysAppDeployer.usage");
    	else 
    		return _strings.getString("AppRouterReload.usage");
 
    }


    /**
     * This method is invoked to update the state of this component.
         * If the value of the given "state" parameter is LOADED, then
     * this object's "initialize" method will be called.
     */
    public void changeState(MComponentState state) throws UnableToChangeStateException {
        if (_logger.isDebugEnabled()) {
            _logger.debug("changeState(): Setting component state to: " + state);
        }

        try {
            if (state.getValue() == MComponentState.LOADED) {
                this.initialize();
            } else if (state.getValue() == MComponentState.RUNNING) {
                this.start();
            }
        } catch (Exception e) {
            _logger.error(e.getMessage(), e);
            throw new UnableToChangeStateException(e.getMessage());
        }
    }


    /**
     * This method initializes this object by performing the following
     * actions:
     *  <ul>
     *      <li> Invokes the SysAppInfoDAO object to obtain the meta data
     *      on all system applications from the backing store.
     *      <li> Obtains a handle to the Deployer component which it will use
     *      to deploy, un-deploy, start and stop the system apps.
     *      <li> Register with the TelnetServer so that this component can be
         *          managed from a telnet console.
         *          <li> Register with the ClusterManager so that this component will
         *          be notified whenever the system's cluster role has changed.
     *  </ul>
     *
     * @see com.baypackets.ase.container.Deployer
     * @see com.baypackets.ase.container.SysAppInfoDAO
         * @see com.baypackets.ase.control.ClusterManager
         * @see com.baypackets.ase.util.TelnetServer
     */
    public void initialize() throws InitializationFailedException {
        boolean loggerEnabled = _logger.isDebugEnabled();

        if (loggerEnabled) {
            _logger.debug("initialize() called...");
        }

        try {
            // PK ADDED FOR SYSAPP DEPLOYMENT ENABLER BugId 17770
            _configRepository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

            // Register with the TelnetServer and ClusterManager...
            TelnetServer telnetServer = (TelnetServer)Registry.lookup(Constants.NAME_TELNET_SERVER);
            telnetServer.registerHandler(SHOW_SYS_APPS, this, false);
            telnetServer.registerHandler(APP_ROUTER_RELOAD, this, false);
            ClusterManager clusterManager = (ClusterManager)Registry.lookup(Constants.NAME_CLUSTER_MGR);
            clusterManager.registerRoleChangeListener(this,Constants.RCL_SYSAPPS_PRIORITY);

            DeployerFactory deployFactory = (DeployerFactory)Registry.lookup(DeployerFactory.class.getName());
            this._deployer = deployFactory.getDeployer(DeployableObject.TYPE_SYSAPP);

                        // Get the meta data on all system apps from the backing store...
                        _sysApps = SysAppInfoDAOFactory.getInstance().getSysAppInfoDAO().getSysAppInfoList();

                        if (loggerEnabled) {
                            if (_sysApps == null || _sysApps.isEmpty()) {
                                _logger.debug("initialize(): No system applications are currently provisioned with the platform.");
                            } else {
                                _logger.debug("initialize(): The following system apps are provisioned with the platform...");

                                Iterator iterator = _sysApps.iterator();

                                while (iterator.hasNext()) {
                                    _logger.debug("initialize(): System App:: " + iterator.next());
                                }
                            }
                        }
        } catch (Exception e) {
                        String msg = "Error occurred during SysAppDeployer intialization: " + e.getMessage();
            _logger.error(msg, e);
            throw new InitializationFailedException(msg);
        }
    }

    /**
     * Called to start this deployer
     */
    public void start() {
        if (_logger.isDebugEnabled()) {
            _logger.debug("Starting SysAppDeployer...");
        }

        this._started = true;

        this.activateSysApps(this._role);
    }


    /**
     * Called to update this component's configuration.
     */
    public void updateConfiguration(Pair[] pairs, OperationType arg1) throws UnableToUpdateConfigException {
        // No Op
    }


    /**
     * This callback method is invoked by the ClusterManager to notify this
     * object when it's role in the cluster has changed.  When invoked, this
         * method will iterate through the list of provisioned system apps
         * and, based on the new cluster role, will either deploy, un-deploy,
         * start or stop each system app based on what is specified in their
         * configuration.
     */

    public void roleChanged(String clusterId, PartitionInfo pInfo ) {

        String subsysId = pInfo.getSubsysId();
        this._role = pInfo.getRole();

        if (_logger.isDebugEnabled()) {
            _logger.debug("roleChanged(): Cluster role has changed to: " + AseRoles.getString(this._role));
        }

        // Activate system apps if it is already started
        if (this._started) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("roleChanged(): Activating system apps");
            }
            this.activateSysApps(this._role);
        } else {
            if (_logger.isDebugEnabled()) {
                _logger.debug("roleChanged(): Not activating system apps as deployer is not yet started");
            }
        }
    }

    private void activateSysApps(short role) {

        boolean loggerEnabled = _logger.isDebugEnabled();
        String sysappEnable = (String)_configRepository.getValue(Constants.PROP_SYSAPP_ENABLE);

        if(sysappEnable == null || sysappEnable.trim().equals(AseStrings.BLANK_STRING)) {
            _logger.error("roleChanged(): sysapp deploy flag is false so not deploying any sysapp.");
            return;
        }

        try {
            if (_sysApps == null || _sysApps.isEmpty()) {
        if (loggerEnabled) {
            _logger.debug("roleChanged(): No system apps are currently provisioned.  Returning...");
                                }
                return;
            }

        StringTokenizer tokenizer = new StringTokenizer(sysappEnable, ",");
        ArrayList sysapps = new ArrayList();
            for (;tokenizer.hasMoreTokens();) {
        String sysapp = tokenizer.nextToken();
        sysapps.add(sysapp);
        }

            Iterator iterator = _sysApps.iterator();
            while (iterator.hasNext()) {
                SysAppInfo appInfo = (SysAppInfo)iterator.next();
        if (sysapps.contains(appInfo.getName())) {
            Short deployOnRole = appInfo.getDeployOnRole();
            Short startOnRole = appInfo.getStartOnRole();

            // Check if app should be deployed or un-deployed at this role.
            if ( deployOnRole != null && deployOnRole.shortValue() == role) {
                appInfo.setExpectedState(SasApplication.STATE_INSTALLED);
            }

            // Check if app should be started or stopped at this role...
            if (startOnRole != null && startOnRole.shortValue() == role) {
                appInfo.setExpectedState(SasApplication.STATE_ACTIVE);
            }

                    //If there is no specific requirement to deploy/start at a particular role...
            //Start the application by default.
            if(deployOnRole == null && startOnRole == null){
                appInfo.setExpectedState(SasApplication.STATE_ACTIVE);
            }

            if (loggerEnabled) {
            _logger.debug("roleChanged(): Changing the application Status::: " + appInfo);
            }
            ((com.baypackets.ase.deployer.SysAppDeployer)_deployer).changeState(appInfo);
        }
            }// while
            ((com.baypackets.ase.deployer.SysAppDeployer)_deployer).setDeploymentStatus(deploymentStatus);
            if (((com.baypackets.ase.deployer.SysAppDeployer)_deployer).me_started){
                synchronized (deploymentStatus) {
                	deploymentStatus.setSysAppsDeployed(true);
			if (loggerEnabled) {

                	_logger.debug("Sys App deployed" + deploymentStatus);
			}
                	if (deploymentStatus.isSbbDeployed()){
				if (loggerEnabled) {

                		_logger.debug("Notifying Ems Adaptor");
				}
                		deploymentStatus.notify();
                	}
    			}
            }
            	
            	
            
        } catch (Exception e) {
            _logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }
    public ComponentDeploymentStatus getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(ComponentDeploymentStatus deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}
}
