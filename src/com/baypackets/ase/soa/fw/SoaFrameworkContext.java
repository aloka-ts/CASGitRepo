//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************


//***********************************************************************************
//
//      File:   SoaFrameworkContext.java
//
//      Desc:   This file defines framework context for SOA support in SAS Platform.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  18/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.fw;

import java.util.Enumeration;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.soa.common.SoaConstants;
import com.baypackets.ase.soa.deployer.AxisDeployer;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseHost;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.soa.iface.SoaContext;
import com.baypackets.ase.soa.ServiceMap;
import com.baypackets.ase.soa.ListenerRegistry;
import com.baypackets.ase.soa.SoaContextImpl;
import com.baypackets.ase.soa.deployer.SoaDeployer;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.soa.provisioner.SoaProvisioner;
import com.baypackets.ase.soa.provisioner.SoaProvisionerImpl;
import com.baypackets.ase.soa.codegenerator.CodeGenerator;
import com.baypackets.ase.soa.common.SoapServer;
import com.baypackets.ase.soa.common.SoapServerFactory;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.deployer.AbstractDeployableObject;

public class SoaFrameworkContext implements MComponent	{

	private static Logger m_logger = Logger.getLogger(SoaFrameworkContext.class);
	private boolean m_soaEnabled = false;
	private Map<String,SoaContext> m_soaContextMap = null;
	private Map<String,SoaContext> upgradeMap = null;
	private ServiceMap m_serviceMap = null;
	private SoaProvisioner m_soaProvisioner = null;
	private ListenerRegistry m_listenerRegistry = null;
	private CodeGenerator m_codeGenerator = null;
	//private SoaDeployer m_soaDeployer = null;
	private AxisDeployer m_axisDeployer = null;
	private AseHost m_host = null;
	
	public SoaFrameworkContext()	{
		ConfigRepository configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String soaEnabled = configRep.getValue(Constants.OID_SOA_SUPPORT);
		if((soaEnabled != null) &&(soaEnabled.trim().length() != 0)) {
			try	{
				if(1 == (int)Integer.parseInt(soaEnabled)) {
					m_soaEnabled = true;
				}
			}catch(NumberFormatException e)	{

			}
		}	

	}

	public void changeState(MComponentState state) throws UnableToChangeStateException {
        try {
            if ((state.getValue() == MComponentState.LOADED) && m_soaEnabled)	{
                this.initialize();
            }
            if ((state.getValue() == MComponentState.RUNNING) && m_soaEnabled)	{
                this.start();
            }
            if ((state.getValue() == MComponentState.STOPPED) && m_soaEnabled)	{
                this.stop();
            }
        } catch(Exception e)	{
			m_logger.error("SOA Module changeState", e);
            throw new UnableToChangeStateException(e.getMessage());
        }
	}

	public void updateConfiguration(Pair[] configData, OperationType opType) 
									throws UnableToUpdateConfigException {
        //No op
    }


	private void initialize() throws Exception	{
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Entering initialize() of SoaFrameworkContext");
		}
		DeployerFactory deployerFactory = (DeployerFactory)Registry.lookup(Constants.NAME_DEPLOYER_FACTORY);
		//PRI 50837
		//m_soaDeployer = (SoaDeployer)deployerFactory.getDeployer(DeployableObject.TYPE_SOA_SERVLET);
		m_axisDeployer = (AxisDeployer)deployerFactory.getDeployer(DeployableObject.TYPE_SOAP_SERVER);
		m_host = (AseHost)Registry.lookup(Constants.NAME_HOST);
		m_soaContextMap = new Hashtable<String,SoaContext>();
		upgradeMap = new Hashtable<String,SoaContext>();
		m_codeGenerator = CodeGenerator.getInstance();
		m_listenerRegistry = new ListenerRegistry();
		m_serviceMap = new ServiceMap();
		m_soaProvisioner = new SoaProvisionerImpl();
		// Now invoke initialize() method on utility components
		m_axisDeployer.initialize();
		m_soaProvisioner.initialize();
		//PRI 50837
		//m_soaDeployer.initialize();
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("SOA Framework Context Initialized successfully");
		}
	}

	private void start() throws StartupFailedException {
		 m_axisDeployer.start();
		 ConfigRepository configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		 String soapServerName = configRep.getValue(SoaConstants.NAME_SOAP_SERVER);
		 SoapServer soapServer = SoapServerFactory.getSoapServer(soapServerName);
		 soapServer.initialize();
		 soapServer.start();
		 m_soaProvisioner.start();
		 //PRI 50837
		 //m_soaDeployer.start();
	}

	private void stop()	{

	}
	
	public SoaContext createSoaContext(String name, Map<String,String> params)	{
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Creating SOA Context: Name = "+name);
		}
		SoaContext soaContext = null;
		soaContext = new SoaContextImpl(name,params);
		if(! m_soaContextMap.containsKey(name)) {
			m_soaContextMap.put(name,soaContext);
		}else {
			if(m_logger.isDebugEnabled())
			m_logger.debug("its upgradation process");
			upgradeMap.put(name,soaContext);
		}
		AseContext context = (AseContext)m_host.findChild(name);
		if(context != null) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("setting Attribute in AseContext: Name ="+SoaConstants.NAME_SOA_CONTEXT);
			}
			context.setAttribute(SoaConstants.NAME_SOA_CONTEXT, soaContext);
		} else {
			m_logger.error("Unable to get AseContext");
		}
		return soaContext;
	}

	public void upgrade() {
		Iterator iterator = upgradeMap.entrySet().iterator();	
		while(iterator.hasNext()) {
			Map.Entry entry = (Map.Entry) iterator.next();
		    m_soaContextMap.put((String)entry.getKey(), (SoaContext)entry.getValue());
        }
	}

	public SoaContext getSoaContext(String name) {
    	SoaContext soaContext = null;
		soaContext = m_soaContextMap.get(name);
		if(m_logger.isDebugEnabled())
		m_logger.debug("SoaContext object being returned is " + soaContext);
    	return soaContext;
     }

	public SoaContext getSoaContextByDepName(String name) {
		if(name == null)
			return null;

		SoaContext soaContext = null;
		Enumeration<SoaContext> list = ((Hashtable<String,SoaContext>) m_soaContextMap).elements();
		while(list.hasMoreElements()) {
			SoaContext ctx = list.nextElement();
			AbstractDeployableObject obj = (AbstractDeployableObject) ctx.getDeployableObject();
			if(obj != null && name.equals(obj.getDeploymentName())) {
				soaContext = ctx;
				break;
			}
		}
		if(m_logger.isDebugEnabled())
		m_logger.debug("SoaContext object being returned is " + soaContext);
		return soaContext;
	}
	
	public void removeSoaContext(String name) {
		m_soaContextMap.remove(name);
	}
	
	public Iterator<Map.Entry<String,SoaContext>> listSoaContexts()	{
		return m_soaContextMap.entrySet().iterator();
	}

	public ServiceMap getServiceMap()	{
		return m_serviceMap;
	}

	public SoaProvisioner getSoaProvisioner()	{
		return m_soaProvisioner;
	}

	public ListenerRegistry getListenerRegistry()	{
		return m_listenerRegistry;
	}

	public CodeGenerator getCodeGenerator()	{
		return m_codeGenerator; 
	}

	public boolean isSoaSupportEnabled()	{
		return m_soaEnabled;
	}
	
}
