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
//      File:   SoaDeployer.java
//
//      Desc:   This file extends the DeployerImpl class and does the SOA specific 
//              deployment.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Suresh Kr. Jangir               24/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.soa.deployer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseWrapper;
import com.baypackets.ase.container.exceptions.ActivationFailedException;
import com.baypackets.ase.container.exceptions.DeactivationFailedException;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.container.exceptions.RedeploymentFailedException;
import com.baypackets.ase.container.exceptions.UndeploymentFailedException;
import com.baypackets.ase.container.exceptions.UpgradeFailedException;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.deployer.DeployerImpl;
import com.baypackets.ase.deployer.DeploymentDescriptor;
import com.baypackets.ase.deployer.FileDeployableObjectDAO;
import com.baypackets.ase.dispatcher.RulesRepository;
import com.baypackets.ase.soa.ListenerRegistry;
import com.baypackets.ase.soa.ServiceMap;
import com.baypackets.ase.soa.SoaContextImpl;
import com.baypackets.ase.soa.codegenerator.CodeGenerator;
import com.baypackets.ase.soa.codegenerator.proxy.BaseProxy;
import com.baypackets.ase.soa.common.AseSoaApplication;
import com.baypackets.ase.soa.common.AseSoaService;
import com.baypackets.ase.soa.common.SoaConstants;
import com.baypackets.ase.soa.common.SoapServer;
import com.baypackets.ase.soa.common.SoapServerFactory;
import com.baypackets.ase.soa.common.WebServiceDataObject;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import com.baypackets.ase.soa.iface.SoaContext;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;




/**
 * SoaDeployer is the abstraction for deploying the SOA services or applications.
 * It generates the code using the Code generator, creates the proxy and stubs and 
 * register them in the service map. 
 *
 * @author Suresh Kr. Jangir
 */

public class SoaDeployer extends DeployerImpl {

	private static Logger m_logger = Logger.getLogger(SoaDeployer.class);
	//addition for CAS.xml
	private static final String[] DD_NAMES = new String[] { DeploymentDescriptor.STR_SIP_DD,
															DeploymentDescriptor.STR_WEB_DD,
															DeploymentDescriptor.STR_CAS_DD, 
															DeploymentDescriptor.STR_SAS_DD,
															DeploymentDescriptor.STR_SOA_DD };
	private static  File DEPLOY_DIR = new File(Constants.ASE_HOME, Constants.FILE_HOST_DIR);
	private SoaFrameworkContext m_fwContext = null;
	private ServiceMap m_serviceMap = null;
	private ListenerRegistry m_listenerRegistry = null;
	private boolean isUpgrade = false;

	
	public SoaDeployer() {
		super();
		String appDeployDir = BaseContext.getConfigRepository().getValue(
				Constants.PROP_APP_DEPLOY_DIR);
		
		if (appDeployDir != null && !appDeployDir.isEmpty()) {
			DEPLOY_DIR = new File(appDeployDir);
		}
		m_logger.error("Creating new instance of SoaDeployer");
	}

	public void initialize() throws Exception {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Initializing SoaDeployer");
		}
		super.initialize();
		this.m_fwContext = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
		this.m_serviceMap = this.m_fwContext.getServiceMap();
		this.m_listenerRegistry = this.m_fwContext.getListenerRegistry();
	}
		
	public void start() throws StartupFailedException {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Starting SoaDeployer");
		}

		super.start();
	}

	public synchronized DeployableObject deploy(String name, String version, int priority, String contextPath,
									InputStream stream, String deployedBy)
									throws DeploymentFailedException {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Entering deploy(): with 6 arg name version priority contextPath stream deployedBy Service Name = "+name);
		}
		AbstractDeployableObject deployableObj = null;
		
 		if(super.nsaUpgrade && version!= null && version.contains(AseStrings.PERIOD)) {
 			String[] verSplit = version.split("\\.");
 			version = verSplit[0]+AseStrings.PERIOD;
 			if(verSplit.length>1){
 				version= version+verSplit[1];
 			}
 		} 	
		
		try {
			byte[] binary = this.getByteArray(stream);
			InputStream inStream = null;
			short type = this.findType(name,binary);
			SoaContextImpl soaContext = null;
			if(type == DeployableObject.TYPE_PURE_SOA) {
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("Deploying PURE SOA Service or Application");
				}
				deployableObj = new SoaDeployableObject();
				deployableObj.setDeploymentName(name);	// Bug 6500
				deployableObj.setVersion(version);
				deployableObj.setPriority(priority);
				deployableObj.setType(type);
				this.preDeploy(deployableObj, name, version, priority, binary);
				soaContext = (SoaContextImpl)this.m_fwContext.getSoaContext(deployableObj.getObjectName());
				soaContext.setDeployableObject(deployableObj);
				AseSoaApplication soaApp = soaContext.getSoaApplication();
				if(soaApp != null) {
					soaApp.initializeListener(deployableObj);
				}
				this.deploySoaPart(soaContext,deployableObj);
				deployableObj.deploy();
				deployableObj.setDeployedBy(deployedBy);
				try {
					if(binary != null){
						this.storeArchive(deployableObj, new ByteArrayInputStream(binary));
					}
				}catch(Exception e) {
					m_logger.error(e.getMessage(), e);
				}
			} else {
	
				try {
					inStream = new ByteArrayInputStream(binary);
					deployableObj = (AbstractDeployableObject)super.
									deploy(name, version, priority, contextPath, inStream, deployedBy);
					soaContext = (SoaContextImpl)this.m_fwContext.getSoaContext(deployableObj.getObjectName());
					soaContext.setDeployableObject(deployableObj);
				} catch(Exception exp) {
					m_logger.error(exp.getMessage(),exp);
					throw new DeploymentFailedException("Unable to deploy "+name);
				}
				soaContext = (SoaContextImpl)this.m_fwContext.getSoaContext(deployableObj.getObjectName());
			}
		}catch(Exception e) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug(e.getMessage(), e);
			}
		}
		return deployableObj;
	}

	public synchronized DeployableObject deploy(InputStream stream, String deployedBy, int type) throws DeploymentFailedException {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Entering deploy() with 3 arg instream, deployedBy, type");
		}
		AbstractDeployableObject deployableObj = null;
		SoaContextImpl soaContext = null;
		if(type == DeployableObject.TYPE_PURE_SOA) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Deploying PURE SOA Service or Application");
			}
			deployableObj = new SoaDeployableObject();
			deployableObj.setType((short)type);
			byte[] binary = this.getByteArray(stream);
			this.preDeploy(deployableObj, null, null, -1, binary);
			soaContext = (SoaContextImpl)this.m_fwContext.getSoaContext(deployableObj.getObjectName());
			soaContext.setDeployableObject(deployableObj);
			AseSoaApplication soaApp = soaContext.getSoaApplication();
			if(soaApp != null) {
				soaApp.initializeListener(deployableObj);
			}
			this.deploySoaPart(soaContext,deployableObj);
			deployableObj.deploy();
			deployableObj.setDeployedBy(deployedBy);
			try {
				if(binary != null){
					this.storeArchive(deployableObj, new ByteArrayInputStream(binary));
				}
			}catch(Exception e) {
				m_logger.error(e.getMessage(), e);
			}
		} else {
			deployableObj = (AbstractDeployableObject)super.deploy(stream, deployedBy);
			soaContext = (SoaContextImpl)this.m_fwContext.getSoaContext(deployableObj.getObjectName());
			soaContext.setDeployableObject(deployableObj);
		}
		
		return deployableObj;
	}

	public DeployableObject deploy(AbstractDeployableObject deployable, 
										byte[] binary, boolean upgrade, String deployedBy)
										throws DeploymentFailedException {
		if(m_logger.isDebugEnabled()) {	
			m_logger.debug("entering deploy() with 4 arg: AbstractDeployableObject, byte[], boolean, String "); 
			m_logger.debug("deployable.getId = "+deployable.getId());
		}
		
		boolean success = false;
		SoaContextImpl soaContext = null;
		
		
		try {
			//If the Stream is NULL, the DDs might not have been parsed.
			//So parse the DDs first.
			//Otherwise store the archive file.
			if(binary == null) {
				binary = this.getByteArray(deployable.getArchive().openStream());
				this.preDeploy(deployable, null, null, -1, binary);
			}
			
			//Check the ID of the Deployable Objects.
			if(deployable.getId() == null){
				throw new DeploymentFailedException("Not able to get the name for this Deployable Object.");
			}
			soaContext = (SoaContextImpl)this.m_fwContext.getSoaContext(deployable.getObjectName());
			AseSoaApplication soaApp = soaContext.getSoaApplication();
            if(soaApp != null) {
                soaApp.initializeListener(deployable);
            }
			if( !isUpgrade ) {
				//if ( (deployable.getType() == DeployableObject.TYPE_PURE_SOA) ||(deployable.getType() == DeployableObject.TYPE_SIMPLE_SOA_APP) ) {
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("setDeployableObject on soaContext called");
				}
				soaContext.setDeployableObject(deployable);
				//}
			}
			try {
				this.deploySoaPart(soaContext,deployable);
			} catch(Exception e) {
				try {
					super.undeploy(deployable);
				} catch(Exception exp) {
					m_logger.error("Unable to remove service Name ="+deployable.getObjectName());
				}
				throw new DeploymentFailedException("Unable to deploy Service: "+deployable.getObjectName(),e);
			}
			
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("host = "+this.host+"id = "+deployable.getId());
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

				Iterator it = this.findByName(deployable.getDeploymentName());
				if(it.hasNext()){
					throw new DeploymentFailedException("Object with the same deployment name: " + deployable.getDeploymentName() + " already exists.");
				}
			}
			
			//If the binary is NOT NULL, store it.
			if(binary != null){
				this.storeArchive(deployable, new ByteArrayInputStream(binary));	
			}
			
			//Now deploy and add it to the host.
			deployable.deploy();
			deployable.setDeployedBy(deployedBy);
			if(deployable.getType() == DeployableObject.TYPE_SOA_SERVLET) {
				if(m_logger.isDebugEnabled()) {
					m_logger.debug("its soaServlet");
				}
				this.host.addChild(deployable);
			}
				
			
			//If the Data Access Object(DAO) is not NULL, persist it. 
			if(this.dataAccessObject != null){
				this.dataAccessObject.persist(deployable);
			}
			success = true;
		}catch(Exception e){
			m_logger.error("deploy()", e);
			throw new DeploymentFailedException(e);
		}finally{
			this.notifyListeners(deployable, DeployableObject.STATE_UNINSTALLED, deployable.getState(), success);
		}
		return deployable;
	}

	public DeployableObject findByNameAndVersion(String name, String version) {
 		if(super.nsaUpgrade && version!= null && version.contains(AseStrings.PERIOD)) {	
 			String[] verSplit = version.split("\\.");
 			version = verSplit[0]+AseStrings.PERIOD;
 			if(verSplit.length>1){
 				version= version+verSplit[1];
 			}		// calculate major version
 		}
 		return super.findByNameAndVersion(name, version);
	}
	
	public DeployableObject redeploy(String name, String version, int priority,
										String contextPath, InputStream stream, String deployedBy) 
										throws RedeploymentFailedException {
 		if(super.nsaUpgrade && version!= null && version.contains(AseStrings.PERIOD)) {
 			String[] verSplit = version.split("\\.");
 			version = verSplit[0]+AseStrings.PERIOD;
 			if(verSplit.length>1){
 				version= version+verSplit[1];
 			}		// calculate major version
 		} 	
		return super.redeploy(name, version, priority, contextPath, stream, deployedBy);
	}

	 public synchronized void activate(AbstractDeployableObject deployable) throws ActivationFailedException {
		if(deployable == null) {
			m_logger.error("DeployableObject is null");
			throw new ActivationFailedException("SOA Service Activation failed");
		}
			
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Entering activate(): Service Id = " + deployable.getId());
		}

		try {
			short type = this.findType(deployable.getId());
			//String name = this.getName(deployable.getId());
			SoaContextImpl context = (SoaContextImpl)m_fwContext.getSoaContext(deployable.getObjectName());
			AseSoaApplication soaApp = context.getSoaApplication();
			
			if(type == DeployableObject.TYPE_SIMPLE_SOA_APP) {
				boolean success = false;
				short prevState = deployable.getState();

				if(!soaApp.getListenerUriApi().isEmpty()) {
					this.deployOnSoapServer(deployable);
				}
				DeployableObject depObj = (DeployableObject) context.getDeployableObject();
				Iterator<String> parameters = context.getParameters();
				ArrayList mainArgs = new ArrayList();
				while(parameters.hasNext()) {
					String param = parameters.next();
					String value = context.getParameterValue(param);
					mainArgs.add(param+"="+value);
					if (m_logger.isDebugEnabled()) {
						m_logger.debug("Parameter added in mainArgs list is = " + param + "----" +value);
					}
				}
				
				if(soaApp.getMainClassName() != null) {
					Class classObject = Class.forName(soaApp.getMainClassName(),
													true,depObj.getClassLoader());
					String mainMethod = soaApp.getMainMethod();
					Class[] argType = new Class[] {String[].class};
					Method method = null;
					if(mainMethod == null) {
						mainMethod = "main";
						method = classObject.getDeclaredMethod(mainMethod,argType);
					}else {
						Method [] methods = classObject.getDeclaredMethods();
						for(int i =0; i < methods.length; i++ ) {
							if (methods[i].getName().equals(mainMethod)) {
								method = methods[i];
								break;
							}
						}
					}
						
					if(method != null) {
						Thread th = new AppTriggerThread(method, classObject, mainArgs);
						th.setDaemon(true);
						th.start();
					} else {
						m_logger.error("Main Method not found");
						throw new ActivationFailedException("Main method defined in SOA-DD [" + mainMethod + "] not found.");
					}
					deployable.activate();
					if(super.dataAccessObject != null){
						super.dataAccessObject.persist(deployable);
					}
					success = true;
				} else {
					throw new ActivationFailedException("Main Class is not defined in soa.xml");
				}
				this.notifyListeners(deployable, prevState, deployable.getState(), success);

			} else {
				if((soaApp == null) || !soaApp.getListenerUriApi().isEmpty()) {
					this.deployOnSoapServer(deployable);
				}
					super.activate(deployable);
			}
		} catch(Exception e) {
			m_logger.error("Activation failed", e);
			throw new ActivationFailedException(e.getMessage());
		}

		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Application was activated successfully");
		}
	}

	public DeployableObject undeploy(String id) throws UndeploymentFailedException {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Entering undeploy(id) of SoaDeployer with Id : "+id);
		}
		DeployableObject deployable = null;
		try {
			deployable = super.undeploy(id);
			this.deactivateService(id);
			if(!isUpgrade) {
				this.m_fwContext.removeSoaContext(this.getName(id));
			}
		}catch (Exception e) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug(e.getMessage(), e);
			}
		}
		return deployable;
	}
	
	public synchronized void undeploy(AbstractDeployableObject deployable) throws UndeploymentFailedException {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Entering undeploy(deployable) of SoaDeployer with object ="+deployable);
		}
		try {
			super.undeploy(deployable);
			this.deactivateService(deployable.getId());
			if(!isUpgrade) {
				this.m_fwContext.removeSoaContext(deployable.getObjectName());
			}
		}catch(Exception e) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug(e.getMessage(), e);
			}
		}
	}

			

	public synchronized void deactivate(AbstractDeployableObject deployable) throws DeactivationFailedException {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Going to deactivate service = " + deployable.getId());
		}

		try {
			//this.deactivateService(deployable.getId());
			super.deactivate(deployable);
			// Remove archive file from axis WEB-INF/services
			ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			String soapServerType = repository.getValue(SoaConstants.NAME_SOAP_SERVER);
			SoapServerFactory.getSoapServer(soapServerType).undeployService(deployable.getId());
			deployable.deactivate();
		} catch(Exception e) {
			m_logger.error(e.getMessage(),e);
		}
	}
			

	public AbstractDeployableObject createDeployableObject() {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("createDeployableObject in SoaDeployer Called");
		}
		AbstractDeployableObject deployableObj = new AseContext();
		deployableObj.setType(this.getType());
		return deployableObj;
	}

	public String getDAOClassName() {
		return FileDeployableObjectDAO.class.getName();
	}

	public String[] getDDNames() {
		return DD_NAMES;
	}

	public ArrayList getDDs(AbstractDeployableObject deployable, String[] names) throws DeploymentFailedException {
		ArrayList list = super.getDDs(deployable, names);
		boolean valid =  false;
		boolean validApp =  false;
		for(int i=0; i<list.size();i++){
			DeploymentDescriptor dd = (DeploymentDescriptor)list.get(i);
			if(dd.getType() == DeploymentDescriptor.TYPE_SOA_DD) {
				valid = true;
			}
			if(dd.getType() == DeploymentDescriptor.TYPE_SIP_DD ||
					dd.getType() == DeploymentDescriptor.TYPE_WEB_DD){
				validApp = true;
			}			
		}

		deployable.setValidDescriptorAvailable(validApp);
		
		if(!valid) {
			throw new DeploymentFailedException("SOA Service Archive must contain SOA deployment discriptor file");
		}
		return list;
	}

	public File getDeployDirectory() {
		return DEPLOY_DIR;
	}

	private void deployOnSoapServer(AbstractDeployableObject deployable) {
		try {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Entering deployOnSoapServer");
			}

			ConfigRepository repository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
			String soapServerType = repository.getValue(SoaConstants.NAME_SOAP_SERVER);
			SoapServer soapServer = SoapServerFactory.getSoapServer(soapServerType);
			soapServer.deployService(	deployable.getId(),
										deployable.getUnpackedDir().getAbsolutePath(),
										deployable.getClassLoader());
		} catch(Exception exp) {
			m_logger.error("Unable to deploy on SOAP Server",exp);
		}
	}

	private void deactivateService(String id) {
		String name = this.getName(id);
		SoaContextImpl soaContext = (SoaContextImpl)this.m_fwContext.getSoaContext(name);
		if(soaContext != null) {
			WebServiceDataObject dataObj = soaContext.getWebServiceDetail();
			URI serviceUri = null;
	
			Iterator<AseSoaService> it = dataObj.getServices();
			while (it.hasNext()) {
				AseSoaService service = it.next();
				try {
					String path = service.getServiceName();
					serviceUri = new URI(this.getServiceBaseUrl().concat(path));
					if(!isUpgrade) {
						this.m_serviceMap.removeService(serviceUri);
					}
				}catch(Exception e) {
					m_logger.error("Deactivating service with URI = " + serviceUri, e);
				}
			}//while
		}//if
	}


	private Object createImplObject(String implName, AbstractDeployableObject absObj, SoaContext soaContext) {
		Object implObj = null;
		if(absObj instanceof AseContext) {
			AseContext aseContext = (AseContext) absObj;
			AseContainer[] children = aseContext.findChildren();
			for(int i=0; children != null && i< children.length; i++){
				AseWrapper wrapper = (AseWrapper) children[i];
				if(wrapper != null && implName.equals(wrapper.getServletName())) {
					implObj = wrapper.getServlet();
					if(implObj == null) {
						m_logger.error("Servlet is not initialized:");
					}
				}
			}
			if(implObj == null) {
				try {
					implObj = (Class.forName(implName,true,aseContext.getClassLoader())).newInstance();
				} catch(Exception e) {
					m_logger.error("Unable to create Impl object in case of servlet soa",e);
				}
			}
		}else {
			try {
				implObj = (Class.forName(implName,true,absObj.getClassLoader())).newInstance();
			}catch(Exception e) {
				m_logger.error("Unable to create Impl object in case of pure soa",e);
			}
		}
			return implObj;
	}


	private File getWSDLFile(File dir) {
		File wsdlFile = null;
		try {
			File wsdlDir = new File(dir,"WEB-INF/wsdl");
			if(wsdlDir.exists()) {
				File[] files = wsdlDir.listFiles();
				for(int i=0 ; i<files.length ; i++) {
					if(files[i].getName().endsWith(".wsdl")) {
						wsdlFile = files[i];
						break;
					}
				}
			}
		} catch(Exception e) {
			m_logger.error(e.getMessage(),e);
		}
		return wsdlFile;
	}

	private DeployableObject deploySoaPart(SoaContextImpl soaContext, 
											AbstractDeployableObject deployableObj) 
											throws DeploymentFailedException {

		String displayName = soaContext.getName();
		String baseUrl = deployableObj.getUnpackedDir().getAbsolutePath();
		String wsdlPath = null;
		WebServiceDataObject dataObj = soaContext.getWebServiceDetail();
	
		File wsdlFile = this.getWSDLFile(deployableObj.getUnpackedDir());
		if(wsdlFile != null) {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("WSDL is present in the archive. deploying using WSDL");
			}
			wsdlPath = wsdlFile.getAbsolutePath();
			dataObj.parseWsdl(wsdlPath);
			for(Iterator<AseSoaService> it = dataObj.getServices() ; it.hasNext();) {
				try {
					AseSoaService service = it.next();
					if(m_logger.isDebugEnabled()) {
						m_logger.debug("Invoking codeGenerator for Service ="+service.getServiceName());
					}
					CodeGenerator m_codeGenerator = this.m_fwContext.getCodeGenerator();
					String path = service.getServiceName();
					URI serviceUri = null;

					if( (path == null) ||(path == "") ) {
						serviceUri = new URI(this.getServiceBaseUrl());
					}else {
					 	serviceUri = new URI(this.getServiceBaseUrl().concat(path));
					}
						
					Map generatedMap = m_codeGenerator.generateCode(baseUrl,wsdlPath,
															SoaConstants.OPERATION_DEPLOY,
															SoaConstants.WS_TYPE_SERVICE,
															dataObj.getName(),
															service.getServiceName(),
															deployableObj.getClassLoader());
					String implName = service.getImplClassName();
					boolean allowSelfReg = false;
					if(implName == null) {
						if(m_logger.isDebugEnabled()) {
							m_logger.debug("Service API Impl is not provided in DD");
						}
						allowSelfReg = true;
					} else {
						Object implObj = this.createImplObject(implName, deployableObj, soaContext);
						BaseProxy baseProxy = (BaseProxy)generatedMap.get(SoaConstants.PROXY_SERVICE);
						baseProxy.setImpl(implObj);
						m_serviceMap.addImplObject(service.getServiceApi(),implObj);
					}
					soaContext.registerServiceProxy(serviceUri, generatedMap.get(SoaConstants.PROXY_SERVICE),
													allowSelfReg,isUpgrade);			

					//putting in map(serviceUri, serviceInterfaceClass) of soaContext
					try {
						String servInterface = service.getServiceApi();
						Class servIfaceClass = Class.forName(servInterface, false, deployableObj.getClassLoader());
						soaContext.setServiceURI(serviceUri, servIfaceClass);
					}catch (Exception e) {
						if(m_logger.isDebugEnabled()) {
							m_logger.debug(e.getMessage(), e);
						}
					}
					
					String lsnrClassName = service.getNotificationApi();
					if (lsnrClassName != null) {
						Object lsnrProxy = generatedMap.get(SoaConstants.PROXY_CLIENT_LISTENER);
						Object remoteListenerProxy = generatedMap.get(SoaConstants.PROXY_REMOTE_LISTENER);
						if (lsnrProxy == null) {
							m_logger.error("Listener proxy object not found for class: " + lsnrClassName);
							throw new DeploymentFailedException("Unable to deploy SOA Service: " + displayName);
						}
						Class listenerApi = Class.forName(lsnrClassName, false, deployableObj.getClassLoader());
						if (listenerApi.isInstance(lsnrProxy)) {
							soaContext.addListenerClientProxy(listenerApi, lsnrProxy);
						} else {
							m_logger.error("Generated proxy object [" + lsnrProxy +
								"] is not an instance of listener class [" + listenerApi + "]");
							throw new DeploymentFailedException("Unable to deploy SOA Service: " + displayName);
						}
						if (listenerApi.isInstance(remoteListenerProxy)) {
							SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
							ListenerRegistry listenerReg = soaFw.getListenerRegistry();
							listenerReg.addListenerRemoteProxy(listenerApi.getName(), remoteListenerProxy);
						} else {
							m_logger.error("Generated proxy object [" + remoteListenerProxy +
								"] is not an instance of listener class [" + listenerApi + "]");
							throw new DeploymentFailedException("Unable to deploy SOA Service: " + displayName);
						}
					}
				}catch(Exception e) {
					m_logger.error("Unable to deploy SOA Service Name = "+displayName, e);
					throw new DeploymentFailedException("Unable to deploy SOA Service Name",e);
				}
			}
		} else {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("WSDL is NOT present in the archive.");
			}
			Iterator<AseSoaService> aseServices = soaContext.getSoaServices();
			boolean isSimpleApp = true;
			while(aseServices.hasNext()) {
				try {
					isSimpleApp = false;
					AseSoaService service = aseServices.next();
					if(m_logger.isDebugEnabled()) {
						m_logger.debug("Invoking codeGenerator for Service ="+service.getServiceName());
					}
					String path = service.getServiceName();
					URI serviceUri = new URI(this.getServiceBaseUrl().concat(path));
					CodeGenerator m_codeGenerator = this.m_fwContext.getCodeGenerator();
					Map generatedMap = m_codeGenerator.generateCode(baseUrl,null,
																SoaConstants.OPERATION_DEPLOY,
																SoaConstants.WS_TYPE_SERVICE,
																displayName,
																service.getServiceName(),
																deployableObj.getClassLoader());
					String implName = service.getImplClassName();
					boolean allowSelfReg = false;
					if(implName == null) {
						if(m_logger.isDebugEnabled()) {
							m_logger.debug("Service API Impl is not provided in DD");
						}
						allowSelfReg = true;
					} else {
						Object implObj = this.createImplObject(implName, deployableObj, soaContext);
						BaseProxy baseProxy = (BaseProxy)generatedMap.get(SoaConstants.PROXY_SERVICE);
						baseProxy.setImpl(implObj);
						m_serviceMap.addImplObject(service.getServiceApi(),implObj);
					}
						
					soaContext.registerServiceProxy(serviceUri, generatedMap.get(SoaConstants.PROXY_SERVICE),
													allowSelfReg,isUpgrade);			
					
					//putting in map(serviceUri, serviceInterfaceClass) of soaContext
					try {
						String servInterface = service.getServiceApi();
						Class servIfaceClass = Class.forName(servInterface, false, deployableObj.getClassLoader());
						soaContext.setServiceURI(serviceUri, servIfaceClass);
					}catch (Exception e) {
						if(m_logger.isDebugEnabled()) {
							m_logger.debug(e.getMessage(), e);
						}
					}
					String lsnrClassName = service.getNotificationApi();
					if (lsnrClassName != null) {
						Object lsnrProxy = generatedMap.get(SoaConstants.PROXY_CLIENT_LISTENER);
						Object remoteListenerProxy = generatedMap.get(SoaConstants.PROXY_REMOTE_LISTENER);
						if (lsnrProxy == null) {
							m_logger.error("Listener proxy object not found for class: " + lsnrClassName);
							throw new DeploymentFailedException("Unable to deploy SOA Service: " + displayName);
						}
						Class listenerApi = Class.forName(lsnrClassName, false, deployableObj.getClassLoader());
						if (listenerApi.isInstance(lsnrProxy)) {
							soaContext.addListenerClientProxy(listenerApi, lsnrProxy);
						} else {
							m_logger.error("Generated proxy object [" + lsnrProxy +
								"] is not an instance of listener class [" + listenerApi + "]");
							throw new DeploymentFailedException("Unable to deploy SOA Service: " + displayName);
						}
						if (listenerApi.isInstance(remoteListenerProxy)) {
							SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
							ListenerRegistry listenerReg = soaFw.getListenerRegistry();
							listenerReg.addListenerRemoteProxy(listenerApi.getName(), remoteListenerProxy);
						} else {
							m_logger.error("Generated proxy object [" + remoteListenerProxy +
								"] is not an instance of listener class [" + listenerApi + "]");
							throw new DeploymentFailedException("Unable to deploy SOA Service: " + displayName);
						}
					}
				} catch(Exception e) {
					m_logger.error("Unable to deploy SOA Service Name ="+displayName,e);
					throw new DeploymentFailedException("Unable to deploy SOA Service Name",e);
				}
			}
		}
		AseSoaApplication soaApp = soaContext.getSoaApplication();
		if (soaApp != null) {
			try {
				HashMap listenerImplApi = soaApp.getListenerImplApi();
				if(!listenerImplApi.isEmpty()) {
					String webSvcName = dataObj.getListenerServiceName();
					if(webSvcName == null ) {
						webSvcName = soaApp.getApplicationName();
					}
					CodeGenerator m_codeGenerator = this.m_fwContext.getCodeGenerator();
					Map generatedMap = m_codeGenerator.generateCode(baseUrl,wsdlPath,
															SoaConstants.OPERATION_DEPLOY,
															SoaConstants.WS_TYPE_APP,
															displayName,
															webSvcName,
															deployableObj.getClassLoader());
					BaseProxy listenerImplProxy = (BaseProxy) generatedMap.get(SoaConstants.PROXY_LOCAL_LISTENER);
					BaseProxy listenerClientProxy = (BaseProxy) generatedMap.get(SoaConstants.PROXY_CLIENT_LISTENER);
					Iterator iterator = (soaApp.getListenerImplApi()).entrySet().iterator();
					while(iterator.hasNext()) {
						Map.Entry entry = (Map.Entry) iterator.next();
						Class listenerApi = (Class) entry.getValue();
						if(m_logger.isDebugEnabled()) {
							m_logger.debug("listenerApi ="+listenerApi);
						}
						Class listenerImpl = (Class) entry.getKey();
						listenerImplProxy.setImpl(listenerImpl.newInstance());
						listenerClientProxy.setURI( (soaContext.getListenerURI(listenerApi)).toString() );
						soaContext.registerListenerProxy(soaContext.getListenerURI(listenerApi),
													listenerImplProxy, false);
					}
				}
			} catch(Exception e) {
				m_logger.error("Unable to deploy SOA App",e);
				throw new DeploymentFailedException("Unable to deploy SOA Service Name ="+displayName,e);
			}
		} else {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("No application to deploy in archive");
			}
		}

		return deployableObj;

	}

	public DeployableObject upgrade(InputStream stream)
			throws UpgradeFailedException {
		return this.upgrade(null, null, -1, stream);
	}


	public synchronized DeployableObject upgrade(String appName, String version,
		int priority, InputStream stream) throws UpgradeFailedException {
		isUpgrade = true;
		super.checkState();
		AbstractDeployableObject deployable =  null;
 		if(super.nsaUpgrade && version!= null && version.contains(AseStrings.PERIOD)) {
 			String[] verSplit = version.split("\\.");
 			version = verSplit[0]+AseStrings.PERIOD;
 			if(verSplit.length>1){
 				version= version+verSplit[1];
 			}
 		} 
		try{
			byte[] binary = this.getByteArray(stream);
			short type = this.findType(appName, binary);
			
			//in case of pure_soa_service or simple_soa_application deployable object will be different
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("type of the application in upgrade is = "+type);
			}
			
			if(type == DeployableObject.TYPE_PURE_SOA) {
				deployable = new SoaDeployableObject();
			}
		
			deployable = this.preDeploy(deployable, appName, version, priority, binary);
	
			//Validate the Upgrade process.
			Iterator it = this.find(type, deployable.getDeploymentName(), null);

			AbstractDeployableObject prevDeployable = it.hasNext() ? (AbstractDeployableObject) it.next() : null;
				
			if (prevDeployable == null) {
                throw new UpgradeFailedException("appNotFound with this name"+ appName);
            }
            if (deployable.getVersion().equals(prevDeployable.getVersion())) {
                throw new UpgradeFailedException("duplicateAppVersions"+ appName+"--"+ version);
            }
			
			this.m_fwContext.upgrade();
				SoaContext soaContext = (SoaContextImpl)this.m_fwContext.getSoaContext(deployable.getObjectName());
				soaContext.setDeployableObject(deployable);
			
			//Now deploy the new version of the application.
			String contextPath = prevDeployable.getContextPath();
            deployable.setContextPath(contextPath);
           	this.deploy(deployable, binary, true, prevDeployable.getDeployedBy());

			if(type != DeployableObject.TYPE_PURE_SOA) {
				//Notify our cluster peers of the new app that was just deployed.
				host.startPing(deployable, true);
			}
			
			prevDeployable.setNewDeployableObject(deployable);
			deployable.setOldDeployableObject(prevDeployable);

			if (prevDeployable.getState() == DeployableObject.STATE_READY) {
				this.start(deployable);
			}
			        
			// Handle app upgrade case...
			if (prevDeployable.getState() == DeployableObject.STATE_ACTIVE) {
				RulesRepository repository = (RulesRepository)Registry.lookup(Constants.RULES_REPOSITORY);        
        
				try {

					if(m_logger.isDebugEnabled()) {
						m_logger.debug("Inside the preDeployable.getStat() condition ......BYP");
					}

					repository.lock();            
		
					repository.removeRulesForApp(prevDeployable.getName());
					this.start(deployable);
					//remove the earlier vesion of service deployed on axis server
					ConfigRepository confRepository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
					String soapServerType = confRepository.getValue(SoaConstants.NAME_SOAP_SERVER);
					SoapServerFactory.getSoapServer(soapServerType).undeployService(prevDeployable.getId());

					this.activate(deployable);

					repository.unlock();
				} catch (Exception e) {
					m_logger.error(e.toString(), e);

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
				if(((AseContext)prevDeployable).getAppSessionCount() == 0) {
					this.checkExpectedState(prevDeployable.getId(), false);
				} else {
					if(m_logger.isDebugEnabled()) {
						m_logger.debug("App Session Count is not 0 : Not removing");
					}
				}
				super.persistUpgrade(prevDeployable);
			}
		//destroying previously deployed application	
			if(type == DeployableObject.TYPE_PURE_SOA) {

				if(prevDeployable.getState() == DeployableObject.STATE_ACTIVE ||
					prevDeployable.getState() == DeployableObject.STATE_UPGRADE){
					this.deactivate(prevDeployable);
				}
				if(prevDeployable.getState() == DeployableObject.STATE_READY){
					this.stop(prevDeployable, false);
				}
				if(prevDeployable.getState() == DeployableObject.STATE_INSTALLED){
					this.undeploy(prevDeployable);
				}
			}	
		}catch(Exception e){
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("upgrade()", e);
			}
			throw new UpgradeFailedException(e.getMessage(), e);
		}
		isUpgrade = false;
		return deployable;
	}

	
	public short getType() {
	    return DeployableObject.TYPE_SOA_SERVLET;
	}

	private String getServiceBaseUrl() {
		ConfigRepository config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String ipAddress = AseUtils.getIPAddressList(config.getValue(Constants.OID_HTTP_FLOATING_IP), true);
		String port = config.getValue(Constants.OID_HTTP_CONNECTOR_PORT); 
		String baseUrl = "http://" + ipAddress.trim() + ":" + port.trim() + "/" + "Axis/services/";	
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("baseUrl formed is = " + baseUrl);
		}
		return baseUrl;
	}

	private String getId(String name, String version) {
 		if(super.nsaUpgrade && version!= null && version.contains(AseStrings.PERIOD)) {
 			String[] verSplit = version.split("\\.");
 			version = verSplit[0]+AseStrings.PERIOD;
 			if(verSplit.length>1){
 				version= version+verSplit[1];
 			}
 			
 		} 
		return name.concat("_"+version+"_"+getType());
	}
				
	// This cannot be made a MonitoredThread as application will hold it
	class AppTriggerThread extends Thread {

		private Method method;
		private Object classObject;
		private ArrayList args = new ArrayList();

		public AppTriggerThread(Method p_method,Class p_classObject, ArrayList p_args) {
			try {
				this.method = p_method;
				this.classObject = p_classObject.newInstance();
				this.args = p_args;
			}catch (Exception e) {
				m_logger.error(e.getMessage(), e);
			}
		}
			
		public void run() {
			try {
				method.invoke(classObject,args.toArray());
			} catch(Throwable th) {
				m_logger.error("Caught at AppTriggerThread Level", th);
			}
		}
	}
}
