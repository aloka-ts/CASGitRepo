/*
 * EMSAdaptor.java
 *
 * Created on August 6, 2004, 4:51 PM
 */
package com.baypackets.ase.servicemgmt;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ImMediateTypes.Reject;
import RSIEms.AgentSession;
import RSIEms.ServiceMgmtSession;
import RSIEmsTypes.ServiceDataStreamHolder;
import RSIEmsTypes.ServiceInfo;
import RSIEmsTypes.ServiceStateInfo;
import RSIEmsTypes.ServiceStates;
import ServiceMgmtModule.ServiceMgmtPOA;

import com.baypackets.ase.util.Constants;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.deployer.DeployerFactoryImpl;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeploymentListener;
import com.baypackets.ase.util.StringManager;
import com.baypackets.ase.util.threadpool.ThreadPool;
import com.baypackets.ase.util.threadpool.ThreadPoolException;
import com.baypackets.ase.util.threadpool.WorkHandler;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.threadmonitor.MonitoredThread;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadOwner;
import com.baypackets.bayprocessor.slee.threadmonitor.ThreadMonitor;
import com.baypackets.emsagent.EmsAgent;
import com.baypackets.emsagent.ServiceInstallException;
import com.baypackets.emsagent.ServiceInstallHandler;
import com.baypackets.emsagent.ServiceUninstallException;
import com.baypackets.emsagent.ServiceUpgradeException;
/**
 * An instance of this class provides an interface to deploy and manage
 * Servlet applications on the SAS from the EMS console.
 */
public class EMSAdaptor extends ServiceMgmtPOA implements ServiceInstallHandler, DeploymentListener , WorkHandler , ThreadOwner {

    private static Logger _logger = Logger.getLogger(EMSAdaptor.class);
    private static StringManager _strings = StringManager.getInstance(EMSAdaptor.class.getPackage());
    
    private Deployer applicationDeployer = null;
	private ServiceMgmtSession srvcMgmtSession;

	private EmsAgent emsAgent = BaseContext.getAgent();
	private AgentSession agentSession = null;

	private static int DEFAULT_THREADPOOL_SIZE = 1;

	private ThreadPool m_threadPool = null;

	private Map m_appsFromEms = null;

	// Flag to indicate completion of service management during startup
	private boolean m_isStartupComplete = false;

	private static final int EMS_INSTALL	= 0;
	private static final int EMS_UPGRADE	= 1;
	private static final int EMS_UNDEPLOY	= 2;
	private static final int EMS_ACTIVATE	= 3;
	private static final int EMS_DEACTIVATE	= 4;
	private static final int EMS_START		= 5;
	private static final int EMS_STOP		= 6;

	private int appsLoadedUptillNow = 0;
	private ComponentDeploymentStatus deploymentStatus = null;
	private String sysappEnable = null;

	/**
     * Default constructor
     */
    public EMSAdaptor(){

		int threadNum = DEFAULT_THREADPOOL_SIZE;

		if(_logger.isDebugEnabled())
            _logger.debug("Creating thread pool with [" + threadNum + "] threads, in EMSAdaptor");

        //String oidStr = (String)m_configRepository.getValue(Constants.PROP_MT_MONITOR_MIN_PERCENT_THREADS_REQD);
        int minPercentageCommonThreads = 100;

        /* For the time being it is commented , it has to be thought upon
		if(oidStr != null) {
        //    minPercentageCommonThreads = Integer.parseInt(oidStr);
		} */

        try {
            m_threadPool = new ThreadPool(threadNum, true,"ServiceMgr", this, this, minPercentageCommonThreads);
			ThreadMonitor tm = (ThreadMonitor)Registry.lookup(Constants.NAME_THREAD_MONITOR);
			this.m_threadPool.setThreadMonitor(tm);
        } catch (ThreadPoolException ex) {
            // Log error
            _logger.error("Thread pool creation failed", ex);
        }

		agentSession = emsAgent.getBpSubSystem().getbayMgrAgentSession();
    }

	boolean isStartupComplete() {
		return m_isStartupComplete;
	}

    /**
     * Invoked by the "start()" method to obtain the status on all deployed
     * applications from EMS.
     */
    private Map getAndProcessAppStatusFromEMS() {
		_logger.debug("Obtaining the status on all available services from EMS..."); 
        
        Map appStatusMap = null;
                
        try {
            if (srvcMgmtSession != null) {  // Check if we're running with EMS.

                // Get the info on all installed, ready, and active services.
				RSIEmsTypes.AvailableServiceInfoListHolder servInfoList =
										new RSIEmsTypes.AvailableServiceInfoListHolder();
               	emsAgent.getBpSubSystem().getbayMgrAgentSession().getAvailableServiceInfoNK(servInfoList);
														
				RSIEmsTypes.AvailableServiceInfo[] availableServices = servInfoList.value;
                
                // Construct a Map for looking up each application's status.
                appStatusMap = new HashMap(availableServices.length);

				for (int j = 0; j < availableServices.length; j++) {
					if(_logger.isDebugEnabled()) {
						_logger.debug("STARTUP: " +
						"Service Id = " + availableServices[j].ServiceId +
						", Service Ver = " + availableServices[j].ServiceVersion +
						", Service State = " + getString(availableServices[j].ServiceState));
					}

					if(availableServices[j].ServiceState == 
						ServiceStates.SRV_INSTALL_IN_PROGRESS
					|| availableServices[j].ServiceState ==
						ServiceStates.SRV_TO_BE_INSTALLED
					|| availableServices[j].ServiceState ==
						ServiceStates.SRV_INSTALLED
					|| availableServices[j].ServiceState ==
						ServiceStates.SRV_STOP_IN_PROGRESS
					|| availableServices[j].ServiceState ==
						ServiceStates.SRV_TO_BE_STOP
					|| availableServices[j].ServiceState ==
						ServiceStates.SRV_ERROR_IN_STOP
					|| availableServices[j].ServiceState ==
						ServiceStates.SRV_ERROR_IN_INSTALL) {
						// Install service
						if(_logger.isDebugEnabled())
							_logger.debug("STARTUP: Installing service");
						this.installService(availableServices[j].ServiceId,
											availableServices[j].ServiceVersion,
											availableServices[j].ArchiveLabel);

						appsLoadedUptillNow++;

					} else if(availableServices[j].ServiceState ==
								ServiceStates.SRV_UPGRADE_IN_PROGRESS
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_TO_BE_UPGRADED
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_UPGRADED
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_ERROR_IN_UPGRADE) {
						// Upgrade service
						if(_logger.isDebugEnabled())
							_logger.debug("STARTUP: Upgrading service");
						this.upgradeService(availableServices[j].ServiceId,
											availableServices[j].ServiceVersion,
											availableServices[j].ArchiveLabel);
						appsLoadedUptillNow++;
					} else if(availableServices[j].ServiceState ==
								ServiceStates.SRV_UNDEPLOY_IN_PROGRESS
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_TO_BE_UNDEPLOYED
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_UNDEPLOYED
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_RECOMMENDED_TO_BE_UPGRADED
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_RECOMMENDED_TO_BE_INSTALLED
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_RECOMMENDED_TO_BE_UNDEPLOYED
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_ERROR_IN_UNDEPLOY) {
						// Do nothing
						if(_logger.isDebugEnabled())
							_logger.debug("STARTUP: Do nothing");
					} else if(availableServices[j].ServiceState ==
								ServiceStates.SRV_READY
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_START_IN_PROGRESS
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_DEACTIVE_IN_PROGRESS
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_TO_BE_START
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_TO_BE_DEACTIVE
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_ERROR_IN_START
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_ERROR_IN_DEACTIVE) {
						// Install and start service
						if(_logger.isDebugEnabled())
							_logger.debug("STARTUP: Install and start service");
						this.installService(availableServices[j].ServiceId,
											availableServices[j].ServiceVersion,
											availableServices[j].ArchiveLabel);
						this.startService(availableServices[j].ServiceId,
											availableServices[j].ServiceVersion);
						appsLoadedUptillNow += 2;
					} else if(availableServices[j].ServiceState ==
								ServiceStates.SRV_ACTIVE
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_ACTIVE_IN_PROGRESS
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_TO_BE_ACTIVE
							|| availableServices[j].ServiceState ==
								ServiceStates.SRV_ERROR_IN_ACTIVE) {
						// Install start and activate service
						if(_logger.isDebugEnabled())
							_logger.debug("STARTUP: Install, start and activate service");
						this.installService(availableServices[j].ServiceId,
											availableServices[j].ServiceVersion,
											availableServices[j].ArchiveLabel);
						this.startService(availableServices[j].ServiceId,
											availableServices[j].ServiceVersion);
						this.activateService(availableServices[j].ServiceId,
											availableServices[j].ServiceVersion);
						appsLoadedUptillNow += 3;
					} else {
						_logger.error("ERROR/UNKNOWN state!");
					}
				}
            } else if(_logger.isDebugEnabled()) {
                _logger.debug("We are running in non-EMS mode.");
            }
        } catch (Exception e) {
            _logger.error("Error occured while obtaining services status from EMS", e);
        }
		if(_logger.isDebugEnabled())
			_logger.debug(" appsLoadedUptillNow="+appsLoadedUptillNow); 

		// If no app has to be loaded notify ReplicationListener thread
		if( appsLoadedUptillNow == 0 )
		{
			SasServiceManager ssm = (SasServiceManager) Registry.lookup("SasServiceManager");
			synchronized(ssm) {
				m_isStartupComplete = true;
				if(_logger.isDebugEnabled())
					_logger.debug("No app to load. Notifying replicationlistener thread to start ,  ssm="+ssm);
				ssm.notifyAll();
			}
		}

        return appStatusMap != null ? appStatusMap : new HashMap(0);
    }

	void start() {
		if(_logger.isDebugEnabled())
			_logger.debug("Receiving and processing services received from EMS");
		
//		synchronized(factory){
//			try {
//				factory.wait();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		getAndProcessAppStatusFromEMS();
		
		synchronized (deploymentStatus) {
			try {
				if(_logger.isDebugEnabled())
					_logger.debug("Sys App Enabled flag in EMS Adaptor " + sysappEnable);
				
				if (deploymentStatus.isSbbDeployed() && (sysappEnable == null || sysappEnable.trim().equals(""))){
					if(_logger.isDebugEnabled())
						_logger.debug("sbb deployed and sys app is not configured,going to deploy services");
				}else if(!deploymentStatus.isSbbDeployed() || !deploymentStatus.isSysAppsDeployed()){
					if(_logger.isDebugEnabled())
						_logger.debug("Waiting for sys apps and sbb to get deployed" + deploymentStatus);
					deploymentStatus.wait();
					if(_logger.isDebugEnabled())
						_logger.debug("Wait is over, going to deploy services ");
				}else {
					if(_logger.isDebugEnabled())
						_logger.debug("sys apps and sbb deployed, going to deploy services");
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				_logger.error(e.getMessage(), e);
			}
		}
		if(_logger.isDebugEnabled())
			_logger.debug("starting thread pool");
		m_threadPool.start();

	}
	

    
    /**
     *
     */
    public void stop() throws ShutdownFailedException {
        // No op
    }

    /**
     * This method is implemented from the ServiceInstallHandler interface to 
     * handle the installation of a service specified from the EMS console.
     *
     * @param fileLocation  The absolute path of the directory where the 
     * service archive file is located.
     * @param fileName  The name of the service archive file.
     * @param serviceId  The primary key EMS will use to identify the service
     * being installed.
     * @param version  The version number of the service being installed.
     */
    public void installService( int serviceId, String serviceVersion,
								String[] archiveLabelList)
		throws ServiceInstallException {
		if(_logger.isDebugEnabled())
			_logger.debug("installService() called...");

		if(_logger.isInfoEnabled()) {
			_logger.info("Installing new service with ID: " + serviceId +
							", version: " + serviceVersion +
							"\n archiveLabelList received: "+archiveLabelList);
		}

		if( archiveLabelList.length > 1 ) {
			_logger.error("Error condition as archiveLabelList has more than one labels");

			throw new ServiceInstallException("Package cannot have multiple services!");
		}

		MessageHolder mesgObj = new MessageHolder(	EMS_INSTALL,
													serviceId,
													serviceVersion,
													archiveLabelList[0]);
		if(_logger.isDebugEnabled())
			_logger.debug("Submitting message to the queue , in installService()");

		m_threadPool.submit( mesgObj );		
        if(_logger.isDebugEnabled())
			_logger.debug("Leaving installService() method...");
    }

    
    /**
     * This method is implemented from the ServiceInstallHandler interface to 
     * handle the upgrade of the service specified from the EMS console.
     *
     * @param fileLocation  The absolute path of the directory where the 
     * service archive file is located.
     * @param fileName  The name of the service archive file.
     * @param serviceId  The primary key EMS uses to identify the service
     * being upgraded.
     * @param version  The version number of the service being upgraded.
     */
    public void upgradeService( int serviceId, String serviceVersion,
								String [] archiveLabelList)
		throws ServiceUpgradeException {
		if(_logger.isDebugEnabled())
			_logger.debug("upgradeService() called...");
               
		if(_logger.isInfoEnabled()) {
			_logger.info("Upgrading service with ID: " + serviceId +
							", version: " + serviceVersion +
							"\n archiveLabelList received: " + archiveLabelList);
		} 

		if( archiveLabelList.length > 1 ) {
			_logger.error("Error condition as archiveLabelList has more than one labels");

			throw new ServiceUpgradeException("Package cannot have multiple services!");
		}

		MessageHolder mesgObj = new MessageHolder(	EMS_UPGRADE,
													serviceId,
													serviceVersion,
													archiveLabelList[0]);
		if(_logger.isDebugEnabled())
			_logger.debug("Submitting message to the queue , in upgradeService()");

		m_threadPool.submit( mesgObj );
		if(_logger.isDebugEnabled())	
			_logger.debug("Leaving upgradeService() method...");
    }
    

    /**
     * This method is implemented from the ServiceInstallHandler interface to
     * handle the uninstallation of the service specified from the EMS console.
     *
     * @param serviceId  The primary key EMS uses to identify the service
     * to un-install. 
     * @param version  The version number of the service to uninstall. 
     */
    public void uninstallService(int serviceId, String version)
		throws ServiceUninstallException {
		if(_logger.isDebugEnabled())
			_logger.debug("uninstallService() called...");
       
		if(_logger.isInfoEnabled()) {
			_logger.info("Uninstalling service with ID: " + serviceId +
							", version: " + version);
		}

		MessageHolder msgObj = new MessageHolder( EMS_UNDEPLOY, serviceId , version);
		if(_logger.isDebugEnabled())
			_logger.debug("Submitting message to the queue , in uninstallService()");
			
		m_threadPool.submit( msgObj );	
		if(_logger.isDebugEnabled())
			_logger.debug("Leaving uninstallService() method...");
    }

	/**
	 * Called by the EMS to activate the specified service on the SAS.
	 */
	public void activateService(int serviceId, String version) throws Reject {
		if(_logger.isDebugEnabled())
			_logger.debug("activateService() called...");
       
		if(_logger.isInfoEnabled()) {
			_logger.info("Activating service with ID: " + serviceId +
							", version: " + version);
		}

		MessageHolder msgObj = new MessageHolder( EMS_ACTIVATE, serviceId , version);
		if(_logger.isDebugEnabled())
			_logger.debug("Submitting message to the queue , in activateService()");

		m_threadPool.submit( msgObj );	
		if(_logger.isDebugEnabled())
			_logger.debug("Leaving activateService() method...");
	}

	/**
	 * Called by the EMS to deactivate the specified service on the SAS.
	 */
	public void deactivateService(int serviceId, String version) throws Reject {
		if(_logger.isDebugEnabled())
			_logger.debug("deactivateService() called...");
       
		if(_logger.isInfoEnabled()) {
			_logger.info("Deactivating service with ID: " + serviceId +
							", version: " + version);
		}

		MessageHolder msgObj = new MessageHolder( EMS_DEACTIVATE, serviceId , version);
		if(_logger.isDebugEnabled())
			_logger.debug("Submitting message to the queue , in deactivateService()");

		m_threadPool.submit( msgObj );	
		if(_logger.isDebugEnabled())
			_logger.debug("Leaving deactivateService() method...");
	}

	/**
	 * Called by the EMS to start the specified service on the SAS.
	 */
	public void startService(int serviceId, String version) throws Reject {
		if(_logger.isDebugEnabled())
			_logger.debug("startService() called...");
       
		if(_logger.isInfoEnabled()) {
			_logger.info("Starting service with ID: " + serviceId +
							", version: " + version);
		}

		MessageHolder msgObj = new MessageHolder( EMS_START, serviceId , version);
		if(_logger.isDebugEnabled())
			_logger.debug("Submitting message to the queue , in startService()");

		m_threadPool.submit( msgObj );	
		if(_logger.isDebugEnabled())
			_logger.debug("Leaving startService() method...");
	}

	/**
	 * Called by the EMS to stop the specified service on the SAS.
	 */
	public void stopService(int serviceId, String version) throws Reject {
		if(_logger.isDebugEnabled())
			_logger.debug("stopService() called...");
       
		if(_logger.isInfoEnabled()) {
			_logger.info("Stopping service with ID: " + serviceId +
							", version: " + version);
		}

		MessageHolder msgObj = new MessageHolder( EMS_STOP, serviceId , version);
		if(_logger.isDebugEnabled())
			_logger.debug("Submitting message to the queue , in stopService()");

		m_threadPool.submit( msgObj );	
		if(_logger.isDebugEnabled())	
			_logger.debug("Leaving stopService() method...");
	}

        /**
         * Called by the EMS to upgrade the specified service.
		 * OBSELETE - as per EMS team
         */
        public void upgradeService(int serviceId, String oldVersion, String newVersion)
			throws ImMediateTypes.Reject {
			_logger.error("upgradeService() Not supported!!!");

			throw new ImMediateTypes.Reject();//"Not supported");
        }        
        
        /**
         * Called by the EMS to obtain the current list of services deployed
         * on this host.
		 * OBSELETE - as per EMS team
         */
        public ServiceInfo[] getServicesList()
			throws ImMediateTypes.Reject {
			_logger.error("getServicesList() Not supported!!!");

			throw new ImMediateTypes.Reject();//"Not supported");
        }
        
        public void ping() {
			if(_logger.isDebugEnabled())
			_logger.debug("BayManager pinging Service Management Session.");
        }
        
        public void setAdmin(RSIEms.AdminManager adminManager, String str)
			throws ImMediateTypes.Reject {
			if(_logger.isDebugEnabled())
				_logger.debug("setAdmin(RSIEms.AdminManager, String) called...");

			BaseContext.getAgent().setAdmin(adminManager,str);
        }
        
        public void setSession(RSIEms.Session session)
			throws ImMediateTypes.Reject {
			if(_logger.isDebugEnabled())
				_logger.debug("setSession(RSIEms.Session) called...");
			
			try {
				// Get Service Management session from the Session Object
				srvcMgmtSession = RSIEms.ServiceMgmtSessionHelper.narrow(session);
				if(_logger.isDebugEnabled())
					_logger.debug("Waking up any waiting threads ...");

				synchronized(this) {
					this.notifyAll();
				}
			} catch (Exception e) {
				_logger.error("Unable to set Service Management session.", e);
			}
        }
	
	public Deployer getApplicationDeployer() {
		return applicationDeployer;
	}

	public ServiceMgmtSession getSrvcMgmtSession() {
		return srvcMgmtSession;
	}

	public void setApplicationDeployer(Deployer deployer) {
		this.applicationDeployer = deployer;
	}

	public void setSrvcMgmtSession(ServiceMgmtSession session) {
		srvcMgmtSession = session;
	}

	public void stateChangeCompleted(
		DeployableObject application, short prevState,
		short requestedState) {
		//this.updateStateInfoInEMS(application.getAppName(), application.getVersion(), prevState, requestedState, true, application.isUpgradeInProgress());
		//@TBD	
	}
	
	public void stateChangeFailed(
		DeployableObject application, short prevState,
		short requestedState) {
		//this.updateStateInfoInEMS(application.getAppName(), application.getVersion(), prevState, requestedState, false, application.isUpgradeInProgress());
		//@TBD
	}
	
	private void sendServiceStateToEMS(int id, String version, ServiceStates state) {
		if(_logger.isDebugEnabled()) {
			_logger.debug("sendServiceStateToEMS: Service Id = " + id +
				", Service Ver = " + version +
				", Service State = " + getString(state));
		}

		ServiceStateInfo info = new ServiceStateInfo();

		info.SubsytemId = emsAgent.getBpSubSystem().getsubSystemId();
		info.ServiceId = id;
		info.ServiceVersion = version;
		info.ServiceState = state;
		try {
			emsAgent.getBpSubSystem().getbayMgrAgentSession().updateServiceStateInfoNK(info);
			if(_logger.isDebugEnabled())
				_logger.debug("Successfully sent state to EMS");
		} catch( Exception ex ) {
			_logger.error("Exception updating service state to EMS, with serviceId=" +
							id + " version=" + version + " state=" + state,ex);
		}
	}

	/**********************************************************
	private void updateStateInfoInEMS(String name, String version, short prevState,
							short requestedState, boolean completed, boolean upgrade){
		try{
			if(_logger.isDebugEnabled()){	
				_logger.debug("updateStateInfoInEMS called for :" + name +", " + version +"," + prevState + "," + requestedState +"," + completed + "," + upgrade);
			}
			//Prepare an object for reporting the service install status to EMS.
			ServiceInfo info = new ServiceInfo();
			info.serviceID = Integer.parseInt(name);
			info.serviceVersion = Integer.parseInt(version);        
			switch(requestedState){
				case DeployableObject.STATE_INSTALLED:
					if(upgrade || prevState == DeployableObject.STATE_UNINSTALLED){
						if(_logger.isDebugEnabled()){
							_logger.debug("Calling updateServiceInfo with INSTALLED :" + completed);
						}	
						info.servState = completed ? SoftwareStatus.Installed : SoftwareStatus.ServiceState_Error;
						BaseContext.getAgent().updateServiceInfo(info);
					}else if(prevState == DeployableObject.STATE_READY && completed) {
						if(_logger.isDebugEnabled()){
							_logger.debug("Calling Service Stopped for " + name + "," + version);
						}
						this.srvcMgmtSession.serviceStopped(info.serviceID, ""+info.serviceVersion);
					}else if (prevState == DeployableObject.STATE_READY && !completed){
						if(_logger.isDebugEnabled()){
							_logger.debug("Calling Service Not Stopped for " + name + "," + version);
						}
						this.srvcMgmtSession.serviceNotStopped(info.serviceID, ""+info.serviceVersion);
					}
					break;
				case DeployableObject.STATE_READY:
					if(upgrade){	
						if(_logger.isDebugEnabled()){
							_logger.debug("Calling updateServiceInfo with READY :" + completed);
						}	
						info.servState = completed ? SoftwareStatus.ServiceState_Ready : SoftwareStatus.ServiceState_Error;
						BaseContext.getAgent().updateServiceInfo(info);
					}else if(prevState == DeployableObject.STATE_INSTALLED && completed){
						if(_logger.isDebugEnabled()){
							_logger.debug("Calling Service Started for " + name + "," + version);
						}
						this.srvcMgmtSession.serviceStarted(info.serviceID, ""+info.serviceVersion);
					}else if (prevState == DeployableObject.STATE_INSTALLED && !completed){
						if(_logger.isDebugEnabled()){
							_logger.debug("Calling Service Not Started for " + name + "," + version);
						}
						this.srvcMgmtSession.serviceNotStarted(info.serviceID, ""+info.serviceVersion);
					}else if (prevState == DeployableObject.STATE_ACTIVE && completed) {
						if(_logger.isDebugEnabled()){
							_logger.debug("Calling Service Deactivated for " + name + "," + version);
						}
						this.srvcMgmtSession.serviceDeactivated(info.serviceID, ""+info.serviceVersion);
					}else if (prevState == DeployableObject.STATE_ACTIVE && !completed) {
						if(_logger.isDebugEnabled()){
							_logger.debug("Calling Service Not Deactivated for " + name + "," + version);
						}
						this.srvcMgmtSession.serviceNotDeactivated(info.serviceID, ""+info.serviceVersion);
					}
					break;
				case DeployableObject.STATE_ACTIVE:
					if(upgrade){	
						if(_logger.isDebugEnabled()){
							_logger.debug("Calling updateServiceStateInfoNK with ACTIVE :" + completed);
						}	
						info.servState = completed ? SoftwareStatus.ServiceState_Active : SoftwareStatus.ServiceState_Error;
						BaseContext.getAgent().updateServiceInfo(info);
					}if(prevState == DeployableObject.STATE_READY && completed){
						if(_logger.isDebugEnabled()){
							_logger.debug("Calling Service Activated for " + name + "," + version);
						}
						this.srvcMgmtSession.serviceActivated(info.serviceID, ""+info.serviceVersion);
					}else if (prevState == DeployableObject.STATE_READY && !completed){
						if(_logger.isDebugEnabled()){
							_logger.debug("Calling Service Not Activated for " + name + "," + version);
						}
						this.srvcMgmtSession.serviceNotActivated(info.serviceID, ""+info.serviceVersion);
					}
					break;
				case DeployableObject.STATE_UNINSTALLED:
					if(_logger.isDebugEnabled()){
						_logger.debug("Calling updateServiceInfo with UNINSTALLED :" + completed);
					}	
					info.servState = completed ? SoftwareStatus.Deinstalled : SoftwareStatus.ServiceStateDeinstall_Error;
					BaseContext.getAgent().updateServiceInfo(info);
					break;
				default:
					throw new Exception("Unknown Service State");
			}
			if(_logger.isDebugEnabled()){	
				_logger.debug("updateStateInfoInEMS completed for :" + name +", " + version);
			}
		}catch(Exception e){
			_logger.error(e.getMessage(), e);
		}
	}
	******************************************************/

	/**
     * Callback as thread owner.
     */
    public int threadExpired(MonitoredThread thread) {
        if(_logger.isDebugEnabled()) {
            _logger.debug(thread.getName() + " expired");
        }

        // Calling ThreadPool's method as logic for min percentage of common
        // thread required, lies there
        return m_threadPool.threadExpired(thread);
    }

	public void execute(Object data) {
		if(_logger.isDebugEnabled())
			_logger.debug(" execute(EMSAdaptor):enter");
	
//		while(!factory.isStartUpComplete()){
//			//wait();
//		}

		MessageHolder msgHldr = (MessageHolder) data;
		int oprType = msgHldr.getOperation();
		String servIdInAsString;
		DeployableObject app = null;
		RSIEmsTypes.ServiceStates serviceState = ServiceStates.SRV_ERROR_IN_INSTALL;

		if(_logger.isDebugEnabled()) {	
			_logger.debug(" oprType="+oprType+" servId="+msgHldr.getServiceId()+" servVer="+msgHldr.getServiceVersion());
		}

		try {
			switch( oprType ) {
				case EMS_INSTALL: // service installation
						
					serviceState = ServiceStates.SRV_ERROR_IN_INSTALL;
					servIdInAsString = "" + msgHldr.getServiceId();

					try {
						ServiceDataStreamHolder serviceDataStream =
												new ServiceDataStreamHolder();
						org.omg.CORBA.StringHolder archiveName =
												new org.omg.CORBA.StringHolder();

						emsAgent.getBpSubSystem().getbayMgrAgentSession().getServiceElementNK(
														msgHldr.getServiceId(),
														msgHldr.getServiceVersion(),
														msgHldr.getArchiveLabel(),
														archiveName,
														serviceDataStream );
						
						byte fileData[] = serviceDataStream.value;
            			InputStream fis = new ByteArrayInputStream(fileData);	
						if(_logger.isDebugEnabled())
							_logger.debug(" Got stream frm EMS, going to deploy");
		           		app = this.applicationDeployer.deploy(	servIdInAsString,
													msgHldr.getServiceVersion(),
													-1,
													null,
													new BufferedInputStream(fis), Deployer.CLIENT_EMS);

						serviceState = ServiceStates.SRV_INSTALLED;
						if(_logger.isDebugEnabled())
							_logger.debug(" SERVICE STATES === > "+serviceState);



					} catch(Exception e) {
						_logger.error("Error occured while deploying service from EMS: ", e);
					}

					break;

				case EMS_UPGRADE: // service upgradation
					
					serviceState = ServiceStates.SRV_ERROR_IN_UPGRADE;	
					servIdInAsString = "" + msgHldr.getServiceId();


					try{
						ServiceDataStreamHolder serviceDataStream =  new ServiceDataStreamHolder();
						org.omg.CORBA.StringHolder archiveName = new org.omg.CORBA.StringHolder();
						

						emsAgent.getBpSubSystem().getbayMgrAgentSession().getServiceElementNK(
															msgHldr.getServiceId(),
															msgHldr.getServiceVersion(),
															msgHldr.getArchiveLabel(),
															archiveName,
															serviceDataStream );
						if(_logger.isDebugEnabled())
							_logger.debug(" IN THE EMS_UPGRADE ");
						byte fileData[] = serviceDataStream.value;
						InputStream fis = new ByteArrayInputStream(fileData);

						app = this.applicationDeployer.upgrade(	servIdInAsString,
																msgHldr.getServiceVersion(),
																-1,
																new BufferedInputStream(fis));

						
						serviceState = ServiceStates.SRV_UPGRADED;
						if(_logger.isDebugEnabled())
							_logger.debug(" SERVICE STATES === > "+serviceState);

						sendServiceStateToEMS(  msgHldr.getServiceId(),
												msgHldr.getServiceVersion(),
												serviceState);

						switch(app.getState()) {
							case DeployableObject.STATE_INSTALLED:
								serviceState = ServiceStates.SRV_INSTALLED;
								break;
							case DeployableObject.STATE_READY:
								serviceState = ServiceStates.SRV_READY;
								break;
							case DeployableObject.STATE_ACTIVE:
								serviceState = ServiceStates.SRV_ACTIVE;
								break;
							default:
								serviceState = ServiceStates.SRV_ERROR;
								break;
						}
					} catch (Exception e) {
						_logger.error("Error occured while upgrading service from EMS", e);
					}

					break;

				case EMS_UNDEPLOY: // service undeployment

					serviceState = ServiceStates.SRV_ERROR_IN_UNDEPLOY;
					servIdInAsString = "" + msgHldr.getServiceId();
					try {
						// Find the AseContext object by the specified EMS service ID.
						app = this.applicationDeployer.findByNameAndVersion(
														servIdInAsString,
														msgHldr.getServiceVersion() );

						if(app == null) {
							if(_logger.isEnabledFor(Level.WARN)) {
								_logger.warn("No service found with ID: " +
									msgHldr.getServiceId() + ", version: " +
									msgHldr.getServiceVersion());
								_logger.warn("Notifying EMS as Service UNDEPLOYED Successful");
							}
						} else {
							if (_logger.isDebugEnabled()) {
								_logger.debug("Undeploying service with ID: " +
												msgHldr.getServiceId() + ", version: " +
												msgHldr.getServiceVersion());
							}

							this.applicationDeployer.undeploy(app.getId());

							if (_logger.isDebugEnabled()) {
								_logger.debug("Successfully undeployed service with ID: " +
												msgHldr.getServiceId() + ", version: " +
												msgHldr.getServiceVersion());
							}
						}

						serviceState = ServiceStates.SRV_UNDEPLOYED;
					} catch (Exception e) {
						_logger.error("Error occured while undeploying service", e);
					}

					break;

				case EMS_ACTIVATE: // service activate

					serviceState = ServiceStates.SRV_ERROR_IN_ACTIVE;
					servIdInAsString = "" + msgHldr.getServiceId();
					try {
						// Find the AseContext object by the specified EMS service ID.
						app = this.applicationDeployer.findByNameAndVersion(
														servIdInAsString,
														msgHldr.getServiceVersion() );

						if(app == null) {
							if(_logger.isEnabledFor(Level.WARN)) {
								_logger.warn("No service found with ID: " +
									msgHldr.getServiceId() + ", version: " +
									msgHldr.getServiceVersion());
							}
						} else {
							if(_logger.isDebugEnabled()) {
								_logger.debug("Activating service with ID: " +
											msgHldr.getServiceId() + ", version: " +
											msgHldr.getServiceVersion());
							}

							this.applicationDeployer.activate(app.getId());

							if(_logger.isDebugEnabled()) {
								_logger.debug("Successfully activated service with ID: " +
												msgHldr.getServiceId() + ", version: " +
												msgHldr.getServiceVersion());
							}

							serviceState = ServiceStates.SRV_ACTIVE;
						}
					} catch (Exception e) {
						_logger.error("Error occured while activating service", e);
					}

					break;

				case EMS_DEACTIVATE: // service deactivate

					serviceState = ServiceStates.SRV_ERROR_IN_DEACTIVE;
					servIdInAsString = "" + msgHldr.getServiceId();
					try {
						// Find the AseContext object by the specified EMS service ID.
						app = this.applicationDeployer.findByNameAndVersion(
														servIdInAsString,
														msgHldr.getServiceVersion() );

						if(app == null) {
							if(_logger.isEnabledFor(Level.WARN)) {
								_logger.warn("No service found with ID: " +
									msgHldr.getServiceId() + ", version: " +
									msgHldr.getServiceVersion());
							}
						} else {
							if(_logger.isDebugEnabled()) {
								_logger.debug("Deactivating service with ID: " +
												msgHldr.getServiceId() + ", version: " +
												msgHldr.getServiceVersion());
							}

							this.applicationDeployer.deactivate(app.getId());

							if(_logger.isDebugEnabled()) {
								_logger.debug("Successfully deactivated service with ID: " +
												msgHldr.getServiceId() + ", version: " +
												msgHldr.getServiceVersion());
							}

							serviceState = ServiceStates.SRV_READY;
						}
					} catch (Exception e) {
						_logger.error("Error occured while deactivating service", e);
					}

					break;

				case EMS_START: // service start
					_logger.error("execute() :Start called on service........."+msgHldr.getServiceId());
					 
					DeployerFactoryImpl factory = (DeployerFactoryImpl)Registry.lookup(Constants.NAME_DEPLOYER_FACTORY);
					
					 while(!((DeployerFactoryImpl)Registry.lookup(Constants.NAME_DEPLOYER_FACTORY)).isStartUpComplete()){
					//wait();
				    }

					serviceState = ServiceStates.SRV_ERROR_IN_START;
					servIdInAsString = "" + msgHldr.getServiceId();
					try {
						// Find the AseContext object by the specified EMS service ID.
						app = this.applicationDeployer.findByNameAndVersion(
													servIdInAsString,
													msgHldr.getServiceVersion() );

						if(app == null) {
							if(_logger.isEnabledFor(Level.WARN)) {
								_logger.warn("No service found with ID: " +
									msgHldr.getServiceId() + ", version: " +
									msgHldr.getServiceVersion());
							}
						} else {
							if(_logger.isDebugEnabled()) {
								_logger.debug("Starting service with ID: " +
												msgHldr.getServiceId() + ", version: " +
												msgHldr.getServiceVersion());
							}

							this.applicationDeployer.start(app.getId());

							if(_logger.isDebugEnabled()) {
								_logger.debug("Successfully started service with ID: " +
												msgHldr.getServiceId() + ", version: " +
												msgHldr.getServiceVersion());
							}

							serviceState = ServiceStates.SRV_READY;
						}
					} catch (Exception e) {
						_logger.error("Error occured while starting service", e);
					}

					break;

				case EMS_STOP: // service stop

					serviceState = ServiceStates.SRV_ERROR_IN_STOP;
					servIdInAsString = "" + msgHldr.getServiceId();
					try {
						// Find the AseContext object by the specified EMS service ID.
						app = this.applicationDeployer.findByNameAndVersion(
														servIdInAsString,
														msgHldr.getServiceVersion() );

						if(app == null) {
							if(_logger.isEnabledFor(Level.WARN)) {
								_logger.warn("No service found with ID: " +
									msgHldr.getServiceId() + ", version: " +
									msgHldr.getServiceVersion());
							}
						} else {
							if(_logger.isDebugEnabled()) {
								_logger.debug("Stopping service with ID: " +
												msgHldr.getServiceId() + ", version: " +
												msgHldr.getServiceVersion());
							}

							this.applicationDeployer.stop(app.getId(), true);

							if(_logger.isDebugEnabled()) {
								_logger.debug("Successfully stopped service with ID: " +
												msgHldr.getServiceId() + ", version: " +
												msgHldr.getServiceVersion());
							}

							serviceState = ServiceStates.SRV_INSTALLED;
						}
					} catch (Exception e) {
						_logger.error("Error occured while stopping service", e);
					}

					break;

				default:
					_logger.error("Illegal operation type of <code>MessageHolder</code>");
			} // switch
		} catch(Throwable t) {
			_logger.error("Error in service management", t);
		} finally {
			if(!m_isStartupComplete) {
				// Notifying "replicationlistener thread" to start iff the system has just started up AND
				// all the applications have been installed
				appsLoadedUptillNow--;
				if(_logger.isDebugEnabled())
					_logger.debug(" appsLoadedUptillNow decremented to : "+appsLoadedUptillNow);
				if( appsLoadedUptillNow == 0 )
				{
					SasServiceManager ssm = (SasServiceManager) Registry.lookup("SasServiceManager");
					synchronized(ssm) {
						m_isStartupComplete = true;
						_logger.debug(" Notifying main/replicationlistener thread to start ,  ssm="+ssm);
						ssm.notifyAll();
					}
				}
			}

			// Report state to EMS
			if(_logger.isDebugEnabled())
				_logger.debug(" SERVICE STATES === > "+serviceState);
			sendServiceStateToEMS(  msgHldr.getServiceId(),
									msgHldr.getServiceVersion(),
									serviceState);
		}
		if(_logger.isDebugEnabled())
			_logger.debug(" execute(EMSAdaptor):exit");
	}

	private String getString(RSIEmsTypes.ServiceStates state) {
		if(state == ServiceStates.SRV_INSTALL_IN_PROGRESS) {
			return new String("SRV_INSTALL_IN_PROGRESS");
		} else if(state == ServiceStates.SRV_TO_BE_INSTALLED) {
			return new String("SRV_TO_BE_INSTALLED");
		} else if(state == ServiceStates.SRV_INSTALLED) {
			return new String("SRV_INSTALLED");
		} else if(state == ServiceStates.SRV_STOP_IN_PROGRESS) {
			return new String("SRV_STOP_IN_PROGRESS");
		} else if(state == ServiceStates.SRV_TO_BE_STOP) {
			return new String("SRV_TO_BE_STOP");
		} else if(state == ServiceStates.SRV_ERROR_IN_STOP) {
			return new String("SRV_ERROR_IN_STOP");
		} else if(state == ServiceStates.SRV_ERROR_IN_INSTALL) {
			return new String("SRV_ERROR_IN_INSTALL");
		} else if(state == ServiceStates.SRV_UPGRADE_IN_PROGRESS) {
			return new String("SRV_UPGRADE_IN_PROGRESS");
		} else if(state == ServiceStates.SRV_TO_BE_UPGRADED) {
			return new String("SRV_TO_BE_UPGRADED");
		} else if(state == ServiceStates.SRV_UPGRADED) {
			return new String("SRV_UPGRADED");
		} else if(state == ServiceStates.SRV_ERROR_IN_UPGRADE) {
			return new String("SRV_ERROR_IN_UPGRADE");
		} else if(state == ServiceStates.SRV_UNDEPLOY_IN_PROGRESS) {
			return new String("SRV_UNDEPLOY_IN_PROGRESS");
		} else if(state == ServiceStates.SRV_TO_BE_UNDEPLOYED) {
			return new String("SRV_TO_BE_UNDEPLOYED");
		} else if(state == ServiceStates.SRV_UNDEPLOYED) {
			return new String("SRV_UNDEPLOYED");
		} else if(state == ServiceStates.SRV_RECOMMENDED_TO_BE_UPGRADED) {
			return new String("SRV_RECOMMENDED_TO_BE_UPGRADED");
		} else if(state == ServiceStates.SRV_RECOMMENDED_TO_BE_INSTALLED) {
			return new String("SRV_RECOMMENDED_TO_BE_INSTALLED");
		} else if(state == ServiceStates.SRV_RECOMMENDED_TO_BE_UNDEPLOYED) {
			return new String("SRV_RECOMMENDED_TO_BE_UNDEPLOYED");
		} else if(state == ServiceStates.SRV_ERROR_IN_UNDEPLOY) {
			return new String("SRV_ERROR_IN_UNDEPLOY");
		} else if(state == ServiceStates.SRV_READY) {
			return new String("SRV_READY");
		} else if(state == ServiceStates.SRV_START_IN_PROGRESS) {
			return new String("SRV_START_IN_PROGRESS");
		} else if(state == ServiceStates.SRV_DEACTIVE_IN_PROGRESS) {
			return new String("SRV_DEACTIVE_IN_PROGRESS");
		} else if(state == ServiceStates.SRV_TO_BE_START) {
			return new String("SRV_TO_BE_START");
		} else if(state == ServiceStates.SRV_TO_BE_DEACTIVE) {
			return new String("SRV_TO_BE_DEACTIVE");
		} else if(state == ServiceStates.SRV_ERROR_IN_START) {
			return new String("SRV_ERROR_IN_START");
		} else if(state == ServiceStates.SRV_ERROR_IN_DEACTIVE) {
			return new String("SRV_ERROR_IN_DEACTIVE");
		} else if(state == ServiceStates.SRV_ACTIVE) {
			return new String("SRV_ACTIVE");
		} else if(state == ServiceStates.SRV_ACTIVE_IN_PROGRESS) {
			return new String("SRV_ACTIVE_IN_PROGRESS");
		} else if(state == ServiceStates.SRV_TO_BE_ACTIVE) {
			return new String("SRV_TO_BE_ACTIVE");
		} else if(state == ServiceStates.SRV_ERROR_IN_ACTIVE) {
			return new String("SRV_ERROR_IN_ACTIVE");
		} else if(state == ServiceStates.SRV_ERROR) {
			return new String("SRV_ERROR");
		}

		return new String("UNKNOWN_SRV_STATE");
	}
    public ComponentDeploymentStatus getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(ComponentDeploymentStatus deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}
	
    public String getSysappEnable() {
		return sysappEnable;
	}

	public void setSysappEnable(String sysappEnable) {
		this.sysappEnable = sysappEnable;
	}
}

