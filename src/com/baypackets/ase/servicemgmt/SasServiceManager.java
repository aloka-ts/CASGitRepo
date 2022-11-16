/*
 * Created on May 6, 2004
 *
 */
package com.baypackets.ase.servicemgmt;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import RSIEms.ServiceMgmtSession;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.servicemgmt.EmsLiteAdaptor;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.emsagent.EmsAgent;
import com.baypackets.emsliteagent.EmsLiteAgent;

/**
 * @author Ravi
 */
public class SasServiceManager implements MComponent {
	
	public static final String NONEMS_SRVC_MGMT = Constants.PROP_NON_EMS_SERVICE_MNGMT;
	private static Logger logger = Logger.getLogger(SasServiceManager.class);
	
	private DeployerFactory deployerFactory = null;
	private TelnetAdaptor telnetAdaptor = null;
	private EMSAdaptor emsAdaptor = null;
	
	private EmsLiteAdaptor emslAdaptor = null;
	
	private HotDeployer hotDeployer = null;
	private String sysappEnable = null;
	
	private ComponentDeploymentStatus deploymentStatus = null;
	
	/**
	 * 
	 */
	public SasServiceManager() {
		super();
	}
	
	public void initialize() throws Exception{

		ConfigRepository rep = BaseContext.getConfigRepository();	
		String srvcMgmtClients = rep.getValue(NONEMS_SRVC_MGMT);
		sysappEnable = (String)rep.getValue(Constants.PROP_SYSAPP_ENABLE);
		
		
		srvcMgmtClients = (srvcMgmtClients == null) ? "" : srvcMgmtClients.toLowerCase();
		this.deployerFactory = (DeployerFactory) Registry.lookup(DeployerFactory.class.getName());
		
		EmsAgent agent = BaseContext.getAgent();
		
		//Initialize the EMS Adaptor
		if(agent !=  null){
			this.initEmsAdaptor();
		}
		
		//Initialize the emslite adaptor
		EmsLiteAgent emslAgent = BaseContext.getEmslagent();
		if(emslAgent !=  null){
			this.initEmsLiteAdaptor();
		}
		
		//Initialize the telnet adaptor
		if(agent == null){
			this.initTelnetAdaptor();
		}else if( srvcMgmtClients.indexOf(Deployer.CLIENT_TELNET.toLowerCase()) != -1){
			this.initTelnetAdaptor();
		}
		
		//Initialize the Hot Deployer
		if(agent == null){
			this.initHotDeployer();
		}else if( srvcMgmtClients.indexOf(Deployer.CLIENT_HOTDEPLOY.toLowerCase()) != -1){
			this.initHotDeployer();
		}
	}
	
	private void initEmsLiteAdaptor() {
		if(this.emslAdaptor != null)
			return;
		
		//Register with the EmsLiteAgent so that application management can be 
		// performed from the EMSLite console.
		EmsLiteAgent _agent = (EmsLiteAgent)BaseContext.getEmslagent();
		if (_agent != null) {
			this.emslAdaptor = new EmsLiteAdaptor();			
			_agent.registerInstallHandler(this.emslAdaptor);                                            
		    Deployer appDeployer = this.deployerFactory.getDeployer(DeployableObject.TYPE_SAS_APPLICATION);
			this.emslAdaptor.setApplicationDeployer(appDeployer);
		}
		
	}

	public void start() throws Exception {
		
		if(this.emsAdaptor != null) {
			this.emsAdaptor.start();
		}

		if(this.hotDeployer != null && this.hotDeployer.isEnabled()){
			this.hotDeployer.start();
		}
		
		if(this.telnetAdaptor != null){
			this.telnetAdaptor.start();
		}
	}
	
	/** Changes the Component State to the state indicated by the argument 
	passed. The states are changed according to the priority values. **/ 
	public void changeState(MComponentState state)
				 throws UnableToChangeStateException {
		try {
			if (state.getValue() == MComponentState.LOADED) {
				this.initialize();
			} else if (state.getValue() == MComponentState.RUNNING){
				this.start();
			} else if(state.getValue() == MComponentState.STOPPED){
			}
		} catch(Exception e){
			logger.error("Unable to start the service manager.", e);
			throw new UnableToChangeStateException(e.getMessage());
		}
	}

	/** Updates the configuration parameters of the component as 
	specified in the Pair array **/
	public void updateConfiguration(Pair[] configData, OperationType opType)
			 throws UnableToUpdateConfigException {
		for (int i = 0; i < configData.length; i++) {
			// Extract the parameter name and value.
			String name = (String)configData[i].getFirst();
			String value = (String)configData[i].getSecond();
			if (name.equals(Constants.PROP_SD_HOTDEPLOY_REQD)) {
				this.hotDeployer.setEnabled(value.equals("1") ? true : false);
				// If enabled, start hot deploy thread. Otherwise, do nothing
				// and hot deploy thread will die down itself.
				if (this.hotDeployer.isEnabled()) {
					this.hotDeployer.start();
				}
			} else if (name.equals(Constants.PROP_SD_HOTDEPLOY_INTERVAL)) {
				this.hotDeployer.setInterval( Long.valueOf(value).longValue());	    	            	
			}
            
			if(logger.isEnabledFor(Level.INFO)){
				logger.info(name + " is set to " + value);
			}
		}
	}

	private void initTelnetAdaptor() throws Exception{
		
		if(this.telnetAdaptor != null)
			return;
		
		this.telnetAdaptor = new TelnetAdaptor();
		Deployer appDeployer = this.deployerFactory.getDeployer(DeployableObject.TYPE_SAS_APPLICATION);
		this.telnetAdaptor.setApplicationDeployer(appDeployer);
		
		Deployer resourceDeployer = this.deployerFactory.getDeployer(DeployableObject.TYPE_RESOURCE);
		this.telnetAdaptor.setResourceDeployer(resourceDeployer);
		
		Deployer sbbDeployer = this.deployerFactory.getDeployer(DeployableObject.TYPE_SBB);
		this.telnetAdaptor.setSbbDeployer(sbbDeployer);
	}
	
	private void initEmsAdaptor() throws Exception{
		
		if(this.emsAdaptor != null)
			return;
		
		//Register with the EmsAgent so that application management can be 
		// performed from the EMS console.
		EmsAgent _agent = (EmsAgent)BaseContext.getAgent();
		if (_agent != null) {
			this.emsAdaptor = new EMSAdaptor();
			this.emsAdaptor.setSysappEnable(sysappEnable);
		    this.emsAdaptor.setDeploymentStatus(deploymentStatus);
			_agent.registerInstallHandler(this.emsAdaptor);                                            
			ServiceMgmtSession srvcMgmtSession = _agent.registerServiceManagement(this.emsAdaptor);
		    this.emsAdaptor.setSrvcMgmtSession(srvcMgmtSession);
		
		    Deployer appDeployer = this.deployerFactory.getDeployer(DeployableObject.TYPE_SAS_APPLICATION);
			this.emsAdaptor.setApplicationDeployer(appDeployer);
		}
    }
    
    // To find if service management (EMS) at startup is complete?
    public boolean isStartupComplete() {
    	
    	//Fololowing check is applied beacues in case of CAS managed by emslite, 
    	//during FT , this emsAdaptor will be null, so to prevent NULLPOINTER exception.
    	if(this.emsAdaptor != null){
    		return emsAdaptor.isStartupComplete();
    	}
    	
    	//So in case of  emslite , StandByReplicator will not wait , so returning true in this case
        return true;
    }
    
    public void initHotDeployer() throws Exception {
    	if(this.hotDeployer != null)
    		return;
    	
    	this.hotDeployer = new HotDeployer();
    	ConfigRepository rep = BaseContext.getConfigRepository();	
		String strInterval = rep.getValue(Constants.PROP_SD_HOTDEPLOY_INTERVAL);
		String strEnable = rep.getValue(Constants.PROP_SD_HOTDEPLOY_REQD);
		strEnable = (strEnable == null) ? "" : strEnable; 
		
		int interval = 30000;
		try{
			interval = Integer.parseInt(strInterval);
		}catch(NumberFormatException nfe){}
		this.hotDeployer.setInterval(interval);
		this.hotDeployer.setEnabled(strEnable.equals("1") ? true : false);
    }
    
    public ComponentDeploymentStatus getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(ComponentDeploymentStatus deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}
    public static void main(String[] args) {
	}
}
