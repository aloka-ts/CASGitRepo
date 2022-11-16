package com.baypackets.ase.deployer;

import org.apache.log4j.Logger;
import java.util.Iterator;
import java.util.ArrayList;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.sbbdeployment.SbbDeployer;
import com.baypackets.ase.soa.deployer.SoaDeployer;
import com.baypackets.ase.soa.deployer.AxisDeployer;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

public class DeployerFactoryImpl implements DeployerFactory, MComponent {

	private static Logger logger = Logger.getLogger(DeployerFactoryImpl.class);
	private ApplicationDeployer appDeployer = new ApplicationDeployer();
	private SysAppDeployer sysappDeployer = new SysAppDeployer();
	private ResourceDeployer resourceDeployer = new ResourceDeployer();
	private SasDeployer m_sasDeployer = new SasDeployer();
	private SoaDeployer m_soaDeployer = new SoaDeployer();
	private AxisDeployer m_axisDeployer = new AxisDeployer();
	private SbbDeployer m_sbbDeployer = new SbbDeployer();
	private ArrayList deployerList = new ArrayList();

	public DeployerFactoryImpl() {
		super();
		deployerList.add(appDeployer);
		deployerList.add(sysappDeployer);
		deployerList.add(resourceDeployer);
		deployerList.add(m_sasDeployer);
		deployerList.add(m_soaDeployer);
		deployerList.add(m_axisDeployer);
		deployerList.add(m_sbbDeployer);
	}

	public Iterator getAllDeployer() {
		return deployerList.iterator();
	}

	public Deployer getDeployer(int type) {
		Deployer deployer =  null;
		switch(type){
			case DeployableObject.TYPE_SAS_APPLICATION:
				deployer = this.m_sasDeployer;
				break;
			case DeployableObject.TYPE_SYSAPP:
				deployer = this.sysappDeployer;
				break;
			case DeployableObject.TYPE_RESOURCE:
				deployer = this.resourceDeployer;
				break;	
			case DeployableObject.TYPE_SERVLET_APP:
				deployer = this.appDeployer;
				break;
			case DeployableObject.TYPE_PURE_SOA:
			case DeployableObject.TYPE_SOA_SERVLET:
			case DeployableObject.TYPE_SIMPLE_SOA_APP:
				deployer = this.m_soaDeployer;
				break;
			case DeployableObject.TYPE_SOAP_SERVER:
				deployer = this.m_axisDeployer;
				break;
			case DeployableObject.TYPE_SBB:
				deployer = this.m_sbbDeployer;
				break;			
		}
		return deployer;
	}


	/**
     * This method is implemented from the MComponent interface and is called
     * by the EMS management application to update the state of this component.
     */
    public void changeState(MComponentState state) throws UnableToChangeStateException {
        try {
        	
        		
            if (state.getValue() == MComponentState.LOADED) {
							this.m_sasDeployer.initialize();            	
            	this.resourceDeployer.initialize();
							this.m_soaDeployer.initialize();
            	this.sysappDeployer.initialize();
            	this.appDeployer.initialize();
            }
            if (state.getValue() == MComponentState.RUNNING){
            	this.resourceDeployer.start();
            	
                synchronized (this.m_soaDeployer){
                	this.m_soaDeployer.start();	
                }
            	
            	
            	this.sysappDeployer.start();
            	
            	 synchronized (this.appDeployer){
            	  this.appDeployer.start();
            	 }
            	
				this.m_sasDeployer.start();	
				
				//rebinding for the use of EMS adaptor to check that Factory has been started
				started =true;
				
				
				 Registry.bind(Constants.NAME_DEPLOYER_FACTORY, this);
            	}
							
           
            if(state.getValue() == MComponentState.STOPPED){
            }
            
           
        } catch(Exception e){
        	logger.error(e.getMessage(), e);
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
		// No op
    }
    
    
    public boolean isStartUpComplete(){
    	return started;
    }
    
    public boolean started =false;


}
